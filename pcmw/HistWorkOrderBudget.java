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
*  File        : HistWorkOrderBudget.java 
*  Modified    :
*    ARWILK  2001-02-19  - Created.
*    GACOLK  2002-12-04  - Set Max Length of MCH_CODE to 100
*    SAPRLK  2003-12-18  - Web Alignment - removed methods clone() and doReset().
*    SAPRLK  2004-02-11  - Web Alignment - remove unnecessary method calls for hidden fields, 
*                                          removed back button functionality,removed unnecessary global variables.  
*    DIAMLK  2006-08-15  - Bug 58216, Eliminated SQL Injection security vulnerability.
*    AMNILK  2006-09-06  - Merged Bug Id: 58216.
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class HistWorkOrderBudget extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.HistWorkOrderBudget");


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
    private int curentrow;
    private String sWoNo;
    private String callingUrl; 
    private int itemCurrRow;


    //===============================================================
    // Construction 
    //===============================================================
    public HistWorkOrderBudget(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();


        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        curentrow = toInt(ctx.readValue("CURRENTROW","0"));
        sWoNo = ctx.readValue("CTXWONO","");
        callingUrl = ctx.getGlobal("CALLING_URL");
        itemCurrRow = toInt(ctx.readValue("ITEMCURRROW","0"));


        if (mgr.commandBarActivated())
        {
            clearItem();
            eval(mgr.commandBarFunction());
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
            okFindTransfer();
            okFindITEM();
        }

        adjust();   

        ctx.writeValue("CURRENTROW",Integer.toString(curentrow));
        ctx.writeValue("CTXWONO",sWoNo);
        ctx.writeValue("ITEMCURRROW",Integer.toString(itemCurrRow));

    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

    public void  okFindTransfer()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");     
        //Bug 58216 start
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",sWoNo);
        //Bug 58216 end
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");
        mgr.submit(trans);


        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWHISTWORKORDERBUDGETNODATA: No data found."));
            headset.clear();
        }
    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");  
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWHISTWORKORDERBUDGETNODATA: No data found."));
            headset.clear();
        }
    }


    public void  okFindITEM()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        trans.clear();
        ASPQuery q = trans.addQuery(itemblk);
        //Bug 58216 start
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        //Bug 58216 end
        headrowno = headset.getCurrentRowNo();
        q.includeMeta("ALL");
        mgr.submit(trans);
        headset.goTo(headrowno);
        trans.clear();
        fillRows();
        sumColumns();
    }


    public void  clearItem()
    {
        if (itemset.countRows() > 0)
        {
            itemset.last();
            itemset.clearRow();
            itemset.first();
        }
    }


    public void  fillRows()
    {
        ASPManager mgr = getASPManager();

        int n;
        double budgRev;
        double budgCost;
        double planRev;
        double planCost;
        double actCost;
        double actRev;  
        double actMargin;
        double budgMargin;
        double planMargin;  

        trans.clear();

        if (itemset.countRows()>0)
        {
            n = itemset.countRows();
            itemset.first();

            for (int i = 0; i <= n; ++i)
            {

                ASPCommand cmd = trans.addCustomFunction( "ACTCOST"+i, "Work_Order_Budget_API.Get_Actual_Cost","NACTUALCOST");
                cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
                cmd.addParameter("WORK_ORDER_COST_TYPE",itemset.getValue("WORK_ORDER_COST_TYPE"));

                cmd = trans.addCustomCommand( "ACTREV"+i, "Work_Order_Budget_API.Actual_Revenue");
                cmd.addParameter("NACTUALREVENUE",itemset.getValue("NACTUALREVENUE"));
                cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
                cmd.addParameter("WORK_ORDER_COST_TYPE",itemset.getValue("WORK_ORDER_COST_TYPE"));

                itemset.next();
            }

            trans = mgr.perform(trans);
            itemset.first();

            for (int i = 0; i <= n; ++i)
            {
                budgRev = itemset.getRow().getNumberValue("BUDGET_REVENUE");
                budgCost = itemset.getRow().getNumberValue("BUDGET_COST");
                planRev = itemset.getRow().getNumberValue("NPLANNEDREVENUE");
                planCost = itemset.getRow().getNumberValue("NPLANNEDCOST");

                actCost = trans.getNumberValue("ACTCOST"+i+"/DATA/NACTUALCOST");
                actRev = trans.getNumberValue("ACTREV"+i+"/DATA/NACTUALREVENUE");

                actMargin =  actRev - actCost;
                budgMargin = budgRev - budgCost;
                planMargin = planRev - planCost;

                ASPBuffer buff = itemset.getRow();        
                buff.setNumberValue("NACTUALCOST",actCost);
                buff.setNumberValue("NACTUALREVENUE",actRev);
                buff.setNumberValue("NACTUALMARGIN",actMargin);
                buff.setNumberValue("NBUDGETMARGIN",budgMargin);
                buff.setNumberValue("NPLANNEDMARGIN",planMargin);
                itemset.setRow(buff);   

                itemset.next();
            }  

            itemset.first();
        }
    }


    public void  sumColumns()
    {
        int n;
        double totBudgCost;
        double totBudgRev;
        double totBudgMargin;
        double totplannCost;
        double totPlannRev;
        double totplannMargin;
        double totActCost;
        double totActRev;
        double totActMargin;
        double budCost;
        double budRev;
        double budMargine;
        double plannCost;
        double plannRev;
        double actCost;
        double actRev;  
        double actMargin;
        double budgMargin;
        double planMargin;  

        totBudgCost = 0;
        totBudgRev = 0;
        totBudgMargin = 0;
        totplannCost = 0;
        totPlannRev = 0;
        totplannMargin = 0;
        totActCost = 0;
        totActRev = 0;
        totActMargin = 0;

        n = itemset.countRows();
        itemset.first();

        for (int i = 1; i <= n; i++)
        {
            budCost = itemset.getRow().getNumberValue("BUDGET_COST");
            budRev = itemset.getRow().getNumberValue("BUDGET_REVENUE");
            budMargine = itemset.getRow().getNumberValue("NBUDGETMARGIN");
            plannCost = itemset.getRow().getNumberValue("NPLANNEDCOST");
            plannRev =  itemset.getRow().getNumberValue("NPLANNEDREVENUE");
            planMargin = itemset.getRow().getNumberValue("NPLANNEDMARGIN");
            actCost = itemset.getRow().getNumberValue("NACTUALCOST");
            actRev = itemset.getRow().getNumberValue("NACTUALREVENUE");
            actMargin = itemset.getRow().getNumberValue("NACTUALMARGIN");

            totBudgCost = totBudgCost + budCost;
            totBudgRev =  totBudgRev + budRev;
            totBudgMargin = totBudgMargin + budMargine;
            totplannCost =  totplannCost + plannCost;
            totPlannRev = totPlannRev + plannRev;
            totplannMargin = totplannMargin + planMargin;
            totActCost =  totActCost + actCost;
            totActRev =  totActRev + actRev;
            totActMargin =  totActMargin + actMargin; 

            itemset.next();
        }

        ASPBuffer data = trans.getBuffer("ITEM/DATA");

        itemset.addRow(data);

        ASPBuffer buff = itemset.getRow();     
        buff.setValue("WORK_ORDER_COST_TYPE","Summary");  
        buff.setNumberValue("BUDGET_COST",totBudgCost);
        buff.setNumberValue("BUDGET_REVENUE",totBudgRev);
        buff.setNumberValue("NBUDGETMARGIN",totBudgMargin);
        buff.setNumberValue("NPLANNEDCOST",totplannCost);
        buff.setNumberValue("NPLANNEDREVENUE",totPlannRev);
        buff.setNumberValue("NPLANNEDMARGIN",totplannMargin);
        buff.setNumberValue("NACTUALCOST",totActCost);
        buff.setNumberValue("NACTUALREVENUE",totActRev);
        buff.setNumberValue("NACTUALMARGIN",totActMargin);   
        itemset.setRow(buff);

        itemset.first();
    }  

    public void  viewDetailsITEM()
    {
        curentrow = itemset.getRowSelected();
        itemCurrRow = curentrow;
        sumColumns();
        itemset.storeSelections();
        itemset.goTo(curentrow);
        itemlay.setLayoutMode(itemlay.SINGLE_LAYOUT);
    }   

    public void  forwardITEM()
    {
        if (itemCurrRow<5)
            itemCurrRow++;
    }


    public void  backwardITEM()
    {
        if (itemCurrRow>0)
            itemCurrRow--;
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("WO_NO","Number").
        setSize(13).
        setLabel("PCMWHISTWORKORDERBUDGETWONO: WO No").
        setReadOnly().
        setMaxLength(8);

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWHISTWORKORDERBUDGETCONTRACT: Site").
        setUpperCase().
        setMaxLength(5);

        headblk.addField("MCH_CODE").
        setSize(13).
        setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
        setCustomValidation("CONTRACT,MCH_CODE","DESCRIPTION").
        setLabel("PCMWHISTWORKORDERBUDGETMCHCODE: Object ID").
        setUpperCase().
        setMaxLength(100);

        headblk.addField("DESCRIPTION").
        setSize(28).
        setLabel("PCMWHISTWORKORDERBUDGETOBJDESC: Object Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("WO_STATUS_ID").
        setHidden();

        headblk.addField("STATUS").
        setSize(5).
        setLabel("PCMWHISTWORKORDERBUDGETSTATE: Status").
        setFunction("Active_Separate_API.Finite_State_Decode__(WO_STATUS_ID)").
        setReadOnly();    


        headblk.setView("HISTORICAL_SEPARATE");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWHISTWORKORDERBUDGETHD: Historical Work Order Budget"));
        headtbl.setWrap();
        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.COUNTFIND);
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        itemblk = mgr.newASPBlock("ITEM");

        itemblk.addField("ITEM_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk.addField("ITEM_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk.addField("ITEM_WO_NO","Number").
        setDbName("WO_NO").
        setHidden();

        itemblk.addField("WORK_ORDER_COST_TYPE").
        setSize(25).
        setMandatory().
        setLabel("PCMWHISTWORKORDERBUDGETWORK_ORDER_COST_TYPE: Work Order Cost Type").
        setReadOnly();

        itemblk.addField("BUDGET_COST","Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETBUDGET_COST: Budget Cost");

        itemblk.addField("BUDGET_REVENUE","Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETBUDGET_REVENUE: Budget Revenue");

        itemblk.addField("NBUDGETMARGIN", "Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNBUDGETMARGIN: Budget Margin").
        setFunction("0").
        setReadOnly();

        itemblk.addField("NPLANNEDCOST","Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNPLANNEDCOST: Planned Cost").
        setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)").
        setReadOnly();

        itemblk.addField("NPLANNEDREVENUE","Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNPLANNEDREVENUE: Planned Revenue").
        setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE)").
        setReadOnly();

        itemblk.addField("NPLANNEDMARGIN", "Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNPLANNEDMARGIN: Planned Margin").
        setFunction("0").
        setReadOnly();

        itemblk.addField("NACTUALCOST","Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNACTUALCOST: Actual Cost").
        setFunction("WORK_ORDER_BUDGET_API.Get_Actual_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)").
        setReadOnly();

        itemblk.addField("NACTUALREVENUE", "Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNACTUALREVENUE: Actual Revenue").
        setFunction("0").
        setReadOnly();

        itemblk.addField("NACTUALMARGIN", "Money").
        setSize(25).
        setLabel("PCMWHISTWORKORDERBUDGETNACTUALMARGIN: Actual Margin").
        setFunction("0").
        setReadOnly();

        itemblk.setView("HIST_WORK_ORDER_BUDGET");
        itemblk.defineCommand("HIST_WORK_ORDER_BUDGET_API","New__,Modify__,Remove__");
        itemset = itemblk.getASPRowSet();
        itemblk.setTitle(mgr.translate("PCMWHISTWORKORDERBUDGETBLKTITLE: Budget"));    
        itemblk.setMasterBlock(headblk);

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.disableCommand(itembar.DELETE);
        itembar.disableCommand(itembar.NEWROW);
        itembar.disableCommand(itembar.DUPLICATEROW);  
        itembar.disableCommand(itembar.EDITROW);

        itembar.defineCommand(itembar.VIEWDETAILS,"viewDetailsITEM");   
        itembar.defineCommand(itembar.FORWARD,"forwardITEM");
        itembar.defineCommand(itembar.BACKWARD,"backwardITEM");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setTitle(mgr.translate("PCMWHISTWORKORDERBUDGETITM: Budget"));
        itemtbl.setWrap();
        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

    }


    public void  adjust()
    {
        if (itemset.countRows() == 5)
        {
            sumColumns();
            itemset.goTo(itemCurrRow);
        }

        if (itemCurrRow == 5 &&   itemlay.isSingleLayout())
        {
            itembar.disableCommand(itembar.EDITROW);
        }
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWHISTWORKORDERBUDGETTITLE: Historical Work Order Budget";
    }

    protected String getTitle()
    {
        return "PCMWHISTWORKORDERBUDGETTITLE: Historical Work Order Budget";
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
                appendToHTML(itemlay.show());

        }

        appendToHTML("<br>\n");
    }

}
