/* 
*                 IFS Research & Development 
*
*  This program is protected by copyright law and by international
*  conventions. All licensing, renting, lending or copying (including
*  for private use), and all other use of the program, which is not
*  expressively permitted by IFS Research & Development (IFS), is a
*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File        : WorkOrderBudget.java 
*  ASP2JAVA Tool  2001-03-20  - Created Using the ASP file WorkOrderBudget.page
*  Modified    :  2001-03-21  BUNI   Corrected some conversion errors.
*              :  2001-10-17  BUNI   Removed some fields and related codes. 
*              :  2002-12-04  GACO   Set Max Length of MCH_CODE to 100
*              :  2003-11-07  JEWILK Modified display layout of WO_NO. Made all fields in headblk ReadOnly. Call 110343.
*              :  2003-12-30  SAPRLK Web Alignment - removed methods clone() and doReset().
*                 040210      ARWILK Edge Developments - (Remove uneccessary global variables, Enhance Performance)
*                 040830      NIJALK Call 117451, Modified editItem(), run(), viewDetailsITEM().
*                 060202      NIJALK Call 132126: Changed the data type of BUDGET_COST,NPLANNEDCOST,NACTUALCOST to "Money".
*                 060814      AMDILK Bug Id  58216, Eliminated SQL errors in web applications. Modified methods okFind(), okFindITEM()
*                 060904      AMDILK Merged with the Bug Id 58216
*                 061222      SHAFLK Bug Id 61959, Added new columns in Budget tab.
*  		  070202      AMNILK Merged LCS Bug Id: 61959.
*                 070801      AMDILK Removed the scroll buttons of the parent when the detail block is in edit mode
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderBudget extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderBudget");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPCommandBar itembar;
	private ASPTable itemtbl;
	private ASPBlockLayout itemlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	int curentrow;
	private String sWoNo;
	private ASPQuery q;
	private ASPBuffer data;
	private int itemCurrRow; 

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderBudget(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();

		curentrow = ctx.readNumber("CURRENTROW", 0);
		sWoNo = ctx.readValue("CTXWONO", "");
		itemCurrRow = ctx.readNumber("ITEMCURRROW", 0);

		if (mgr.commandBarActivated())
		{
                        String myCmdFunction = mgr.commandBarFunction();
                        if (!"editItem();".equals(myCmdFunction) && (!"viewDetailsITEM();".equals(myCmdFunction)))
			           clearItem();
			eval(myCmdFunction); 
		}
		else if (mgr.dataTransfered())
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
			okFind();  
			okFindITEM(); 
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
			sWoNo = mgr.readValue("WO_NO");
			okFind();
			okFindITEM();
		}

		adjust();   

		ctx.writeNumber("CURRENTROW", curentrow);
		ctx.writeValue("CTXWONO", sWoNo);
		ctx.writeNumber("ITEMCURRROW", itemCurrRow);

	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);

		if (!mgr.isEmpty(sWoNo))
		{
		        q.addWhereCondition("WO_NO = ?");
			q.addParameter("WO_NO", sWoNo);
		}

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERBUDGETNODATA: No data found."));
			headset.clear();
		}
	}

	public void okFindITEM()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addEmptyQuery(itemblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
		int headrowno = headset.getCurrentRowNo();
		q.includeMeta("ALL");
		mgr.submit(trans);

		headset.goTo(headrowno);
		trans.clear();

		fillRows();
		sumColumns();
	}

	public void clearItem()
	{
		if (itemset.countRows() > 0)
		{
			itemset.last();
			itemset.clearRow();
			itemset.first();
		}
	}

	public void fillRows()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		if (itemset.countRows()>0)
		{
			int n = itemset.countRows();
			itemset.first();

			for (int i = 0; i <= n; ++i)
			{
				ASPCommand cmd = trans.addCustomFunction( "ACTCOST" + i, "Work_Order_Budget_API.Get_Actual_Cost","NACTUALCOST");
				cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
				cmd.addParameter("WORK_ORDER_COST_TYPE", itemset.getRow().getValue("WORK_ORDER_COST_TYPE"));

			        cmd = trans.addCustomCommand( "ACTREV" + i, "Work_Order_Budget_API.Actual_Revenue");
				cmd.addParameter("NACTUALREV");
				cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
				cmd.addParameter("WORK_ORDER_COST_TYPE", itemset.getRow().getValue("WORK_ORDER_COST_TYPE"));
				
				cmd = trans.addCustomFunction( "PLANCOST" + i, "Work_Order_Budget_API.Get_Planned_Cost","NPLANNEDCOST");
				cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
				cmd.addParameter("WORK_ORDER_COST_TYPE", itemset.getRow().getValue("WORK_ORDER_COST_TYPE"));

                                cmd = trans.addCustomFunction( "PLANREV" + i, "Work_Order_Budget_API.Get_Planned_Revenue","NPLANNEDREV");
				cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
				cmd.addParameter("WORK_ORDER_COST_TYPE", itemset.getRow().getValue("WORK_ORDER_COST_TYPE"));

				itemset.next();
			}

			trans = mgr.perform(trans);

			itemset.first();

			for (int i = 0; i <= n; ++i)
			{       			
                                double budCost = itemset.getRow().getNumberValue("BUDGET_COST");
                                double BudgRev = itemset.getRow().getNumberValue("BUDGET_REVENUE");
				double actCost = trans.getNumberValue("ACTCOST" + i + "/DATA/NACTUALCOST");
				double actRev = trans.getNumberValue("ACTREV" + i + "/DATA/NACTUALREV");
				double planCost = trans.getNumberValue("PLANCOST" + i + "/DATA/NPLANNEDCOST");
				double planrev = trans.getNumberValue("PLANREV" + i + "/DATA/NPLANNEDREV");

                                if (isNaN(budCost))
                                        budCost = 0;
                                if (isNaN(BudgRev))
                                        BudgRev = 0;

				if (isNaN(actCost))
					actCost = 0;
				if (isNaN(actRev))
					actRev = 0;
				if (isNaN(planCost))
					planCost = 0;
				if (isNaN(planrev))
					planrev = 0;

                                double budMar = BudgRev - budCost;
                                double planmar = planrev - planCost; 
                                double actMar =  actRev -  actCost;

				ASPBuffer buf = itemset.getRow();
				buf.setNumberValue("NACTUALCOST", actCost);
				buf.setNumberValue("NACTUALREV", actRev);
				buf.setNumberValue("NPLANNEDCOST", planCost);
				buf.setNumberValue("NPLANNEDREV", planrev);
				buf.setNumberValue("BUDGET_MARGINE", budMar);
				buf.setNumberValue("PLAN_MARGINE", planmar);
				buf.setNumberValue("ACT_MARGINE", actMar);

				itemset.setRow(buf);
				itemset.next();
			}  

			itemset.first();
		}
	}

	public void sumColumns()
	{

		int n = itemset.countRows();
		itemset.first();
		double totBudgCost = 0;
		double totBudgRev = 0;
		double totBudgMar = 0;
		double totplannCost = 0;
		double totplannRev = 0;
		double totplannMar = 0;
		double totActCost = 0;
		double totActRev = 0;
		double totActMar = 0;

		for (int i = 1; i <= n; i++)
		{
			double budCost = itemset.getRow().getNumberValue("BUDGET_COST");
			if (isNaN(budCost))
				budCost = 0;
			double budgRev = itemset.getRow().getNumberValue("BUDGET_REVENUE");
			if (isNaN(budgRev))
				budgRev = 0;

			double plannCost = itemset.getRow().getNumberValue("NPLANNEDCOST");
			if (isNaN(plannCost))
				plannCost = 0;
			double plannRev = itemset.getRow().getNumberValue("NPLANNEDREV");
			if (isNaN(plannRev))
				plannRev = 0;

			double actCost = itemset.getRow().getNumberValue("NACTUALCOST");
			if (isNaN(actCost))
				actCost = 0;
			double actRev = itemset.getRow().getNumberValue("NACTUALREV");
			if (isNaN(actRev))
				actRev = 0;

			totBudgCost = totBudgCost + budCost;
			totBudgRev = totBudgRev + budgRev;
			totplannCost =  totplannCost + plannCost;
			totplannRev =  totplannRev + plannRev;
			totActCost =  totActCost + actCost;
			totActRev =  totActRev + actRev;

			itemset.next();
		}
                totBudgMar  = totBudgRev - totBudgCost;
                totplannMar = totplannRev - totplannCost;
                totActMar   =  totActRev - totActCost;

		data = trans.getBuffer("ITEM/DATA");

		ASPManager mgr = getASPManager();

		String summary = mgr.translate("PCMWACTIVESEPARATE2SUMMARY: Summary");

		itemset.addRow(data);

		ASPBuffer buf = itemset.getRow();

		buf.setValue("WORK_ORDER_COST_TYPE", summary);  
		buf.setNumberValue("BUDGET_COST", totBudgCost);
		buf.setNumberValue("BUDGET_REVENUE", totBudgRev);
		buf.setNumberValue("BUDGET_MARGINE", totBudgMar);
		buf.setNumberValue("NPLANNEDCOST", totplannCost);
		buf.setNumberValue("NPLANNEDREV", totplannRev);
		buf.setNumberValue("PLAN_MARGINE", totplannMar);
		buf.setNumberValue("NACTUALCOST", totActCost);
		buf.setNumberValue("NACTUALREV", totActRev);
		buf.setNumberValue("ACT_MARGINE", totActMar);

		itemset.setRow(buf);
		itemset.first();
	}

	public void editItem()
	{
		ASPManager mgr = getASPManager();

		if (itemlay.isMultirowLayout())
			curentrow = itemset.getRowSelected();
		else
			curentrow = itemCurrRow;

		if (curentrow == 6)
                {
                    mgr.showAlert("PCMWWORKORDERBUDGETCANNOT: Can not perform on selected line.");
                    clearItem();
                }
			
		else
		{
			itemCurrRow = curentrow;

			if (itemset.countRows() == 5)
				sumColumns();

			itemset.storeSelections();
                        clearItem();
			itemset.goTo(curentrow);
			itemlay.setLayoutMode(itemlay.EDIT_LAYOUT);
		}
	}

	public void saveReturnITEM()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int parentCurrrow = headset.getCurrentRowNo();
		itemset.goTo(curentrow);

		String woNo = mgr.readValue("ITEM_WO_NO");
		String workOrderCostType = mgr.readValue("WORK_ORDER_COST_TYPE");

		double budgCost = mgr.readNumberValue("BUDGET_COST");
		if (isNaN(budgCost))
			budgCost = 0;

		double budgRev = mgr.readNumberValue("BUDGET_REVENUE");
		if (isNaN(budgRev))
			budgRev = 0;

		double planCost = mgr.readNumberValue("NPLANNEDCOST");
		if (isNaN(planCost))
			planCost = 0;

		double actCost = mgr.readNumberValue("NACTUALCOST");
		if (isNaN(actCost))
			actCost = 0;

		ASPBuffer buff = itemset.getRow();

		buff.setValue("WO_NO", woNo);
		buff.setValue("WORK_ORDER_COST_TYPE", workOrderCostType);
		buff.setNumberValue("BUDGET_COST", budgCost);
		buff.setNumberValue("BUDGET_REVENUE", budgRev);
		buff.setNumberValue("NPLANNEDCOST", planCost);
		buff.setNumberValue("NACTUALCOST", actCost);

		itemset.setRow(buff);
		mgr.submit(trans);

		trans.clear();
		okFindITEM();

		itemlay.setLayoutMode(itemlay.getHistoryMode());

		headset.goTo(parentCurrrow);
		       
	}

	public void viewDetailsITEM()
	{
		curentrow = itemset.getRowSelected();
		itemCurrRow = curentrow;
		sumColumns();
		itemset.storeSelections();
                clearItem();
		itemset.goTo(curentrow);
		itemlay.setLayoutMode(itemlay.SINGLE_LAYOUT);
	}

	public void forwardITEM()
	{
		if (itemCurrRow<5)
			itemCurrRow++;
	}

	public void backwardITEM()
	{

		if (itemCurrRow>0)
			itemCurrRow--;
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("OBJSTATE").
		setHidden();

		headblk.addField("WO_NO","Number","#").
		setSize(13).
		setLabel("PCMWWORKORDERBUDGETWONO: WO No").
		setReadOnly().
		setMaxLength(8);

		headblk.addField("CONTRACT").
		setSize(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
		setLabel("PCMWWORKORDERBUDGETCONTRACT: Site").
		setUpperCase().
		setReadOnly().
		setMaxLength(5);

		headblk.addField("MCH_CODE").
		setSize(13).
		setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
		setCustomValidation("CONTRACT,MCH_CODE","DESCRIPTION").
		setLabel("PCMWWORKORDERBUDGETMCHCODE: Object ID").
		setUpperCase().
		setReadOnly().
		setMaxLength(100);

		headblk.addField("DESCRIPTION").
		setSize(28).
		setLabel("PCMWWORKORDERBUDGETOBJDESC: Object Description").
		setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("STATE").
		setSize(11).
		setLabel("PCMWWORKORDERBUDGETSTATE: Status").
		setReadOnly().
		setMaxLength(30); 

		headblk.setView("ACTIVE_SEPARATE");

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.COUNTFIND);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		//--------------------------------------------------------------------------------------------------------------
		//--------------------------------------------     ITEM Block   ------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------

		itemblk = mgr.newASPBlock("ITEM");

		itemblk.addField("ITEM_OBJID").
		setDbName("OBJID").
		setHidden();

		itemblk.addField("ITEM_OBJVERSION").
		setDbName("OBJVERSION").
		setHidden();

		itemblk.addField("ITEM_WO_NO","Number","#").
		setDbName("WO_NO").
		setHidden().
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETWO_NO: WO Number");

		itemblk.addField("WORK_ORDER_COST_TYPE").
		setSize(25).
		setMandatory().
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETWORK_ORDER_COST_TYPE: Work Order Cost Type");

		itemblk.addField("BUDGET_COST","Money").
		setSize(25).
		setLabel("PCMWWORKORDERBUDGETBUDGET_COST: Budget Cost");

		itemblk.addField("BUDGET_REVENUE","Money").
		setSize(25).
		setLabel("PCMWWORKORDERBUDGETBUDGET_REVENUE: Budget Revenue");

		itemblk.addField("BUDGET_MARGINE","Money").
		setSize(25).
                setReadOnly().
		setLabel("PCMWWORKORDERBUDGETBUDGET_MARGINE: Budget Margin").
		setFunction("0");

		itemblk.addField("NPLANNEDCOST","Money").
		setSize(25).
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETNPLANNEDCOST: Planned Cost").
		setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

		itemblk.addField("NPLANNEDREV","Money").
		setSize(25).
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETNPLANNEDREV: Planned Revenue").
		setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE)");

		itemblk.addField("PLAN_MARGINE","Money").
		setSize(25).
                setReadOnly().
		setLabel("PCMWWORKORDERBUDGETPLAN_MARGINE: Planned Margin").
		setFunction("0");

		itemblk.addField("NACTUALCOST","Money").
		setSize(25).
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETNACTUALCOST: Actual Cost").
		setFunction("WORK_ORDER_BUDGET_API.Get_Actual_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

		itemblk.addField("NACTUALREV","Money").
		setSize(25).
		setReadOnly().
		setLabel("PCMWWORKORDERBUDGETNACTUALREV: Actual Revenu").
		setFunction("0");

		itemblk.addField("ACT_MARGINE","Money").
		setSize(25).
                setReadOnly().
		setLabel("PCMWWORKORDERBUDGETACT_MARGINE: Actual Margin").
		setFunction("0");

		itemblk.setView("WORK_ORDER_BUDGET");
		itemblk.defineCommand("WORK_ORDER_BUDGET_API","Modify__");

		itemset = itemblk.getASPRowSet();

		itemblk.setTitle(mgr.translate("PCMWWORKORDERBUDGETBLKTITLE: Budget"));    
		itemblk.setMasterBlock(headblk);

		itembar = mgr.newASPCommandBar(itemblk);
		itembar.defineCommand(itembar.EDITROW, "editItem");
		itembar.defineCommand(itembar.VIEWDETAILS, "viewDetailsITEM");
		itembar.defineCommand(itembar.SAVERETURN, "saveReturnITEM");
		itembar.defineCommand(itembar.FORWARD, "forwardITEM");
		itembar.defineCommand(itembar.BACKWARD, "backwardITEM");

		itemtbl = mgr.newASPTable(itemblk);
		itemtbl.setWrap();

		itemlay = itemblk.getASPBlockLayout();
		itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
		if (itemset.countRows() == 6)
		{
			sumColumns();
			itemset.goTo(itemCurrRow);
		}

		if (itemCurrRow == 6 && itemlay.isSingleLayout())
			itembar.disableCommand(itembar.EDITROW);

                if ( itemlay.isEditLayout() )
		{
		   headbar.disableCommand(headbar.DELETE);
		   headbar.disableCommand(headbar.NEWROW);
		   headbar.disableCommand(headbar.EDITROW);
		   headbar.disableCommand(headbar.DELETE);
		   headbar.disableCommand(headbar.DUPLICATEROW);
		   headbar.disableCommand(headbar.FIND);
		   headbar.disableCommand(headbar.BACKWARD);
		   headbar.disableCommand(headbar.FORWARD);
		}
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERBUDGETTITLE: Work Order Budget";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERBUDGETTITLE: Work Order Budget";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
		{
			if (headset.countRows() == 0)
			{
				headbar.disableCommand(headbar.FORWARD);
				headbar.disableCommand(headbar.BACKWARD);
				headbar.disableCommand(headbar.BACK);
				headbar.disableCommand(headbar.DUPLICATEROW);
			}
			appendToHTML(headlay.show());
		}

		if (headset.countRows() != 0)
		{
			if (itemlay.isEditLayout())
			{
				appendToHTML(headlay.show());
				appendToHTML(itemlay.show());
			}
			if (itemlay.isVisible() && !itemlay.isEditLayout())
			{
				appendToHTML(itemlay.show());
			}
		}
	}
}
