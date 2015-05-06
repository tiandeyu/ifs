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
*  File        : WorkOrderRequisHeaderRMBRO.java 
*  Created     : 010302  Created.
*  Modified    :
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*		   (Caused when cancelNew is performed after saveNew)
*  JEWILK  030725  Scream Merge.
*  CHAMLK  030828  Added Ownership, Owner and Owner Name.
*  JEWILK  030926  Corrected overridden javascript function toggleStyleDisplay(). 
*  SAPRLK  040105  Web Alignment, Converting blocks to tabs, removing 'clone' & 'doReset' methods, removing the title of the blocks.
*  VAGULK  040126  Web Alignment,made the field order according to the order in Centura.
*  ARWILK  040129  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  NIJALK  040906  Added Job Id to itemblk0.
*  ARWILK  041112  Replaced getContents with printContents.
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060817  Bug 58216, Eliminated SQL errors in web applications. Modified the methods countFind(), 
*                  countFindITEM0(), countFindITEM1(), okFind(), okFindITEM0(), okFindITEM1()
*  AMDILK  060906  Merged with the Bug Id 58216
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  SHAFLK  081027  Bug 77458, Redisigned the page.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderRequisHeaderRMBRO extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderRequisHeaderRMBRO");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

	private ASPBlock upperblk;
	private ASPRowSet upperset;
	private ASPTable uppertbl;
	private ASPBlockLayout upperlay;
	private ASPCommandBar upperbar;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock itemblk0;
	private ASPRowSet itemset0;
	private ASPCommandBar itembar0;
	private ASPTable itemtbl0;
	private ASPBlockLayout itemlay0;

	private ASPBlock itemblk1;
	private ASPRowSet itemset1;
	private ASPCommandBar itembar1;
	private ASPTable itemtbl1;
	private ASPBlockLayout itemlay1;

	private ASPBlock itemblk2;
	private ASPRowSet itemset2;

	private ASPBlock b;

	private ASPField f;
	private ASPCommand cmd;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================

	private ASPTransactionBuffer trans;
	private String calling_url;
	private String up_mch_code;
	private String mch_name;
	private boolean canBeTransfered;
	private String fldPartNo;
	private String fldSupplier;
	private int buffWoNo;
	private int up_wo_no;

	//Web Alignment - Replacing Blocks with Tabs
	private ASPTabContainer tabs;    
	//

	// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
	// 040129  ARWILK  End  (Enable Multirow RMB actions)

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderRequisHeaderRMBRO(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		calling_url = ctx.getGlobal("CALLING_URL");
		buffWoNo = ctx.readNumber("CTX8",buffWoNo);
		up_wo_no = ctx.readNumber("CTX9",up_wo_no);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
		{
			ASPBuffer buff= mgr.getTransferedData();
			eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME",upperset.getRow())); 

			ASPBuffer row = buff.getBufferAt(0);

			if (row.itemExists("WO_NO"))
				up_wo_no = toInt(row.getValue("WO_NO"));
			if (row.itemExists("MCH_CODE"))
				up_mch_code = row.getValue("MCH_CODE");
			if (row.itemExists("MCH_NAME"))
				mch_name = row.getValue("MCH_NAME");

			searchWorkOrder();
		}

		adjust();

		tabs.saveActiveTab();

		ctx.writeNumber("CTX8",buffWoNo);
		ctx.writeNumber("CTX9",up_wo_no);
	}

	//-----------------------------------------------------------------------------
	//-----------------------------  VALIDATE FUNCTION  ---------------------------
	//-----------------------------------------------------------------------------


	//-----------------------------------------------------------------------------
	//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
	//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		int currrow;

		up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
		currrow = headset.getCurrentRowNo();
		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", Integer.toString(up_wo_no));
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.goTo(currrow);      
	}

	public void countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		int currow;

		currow = headset.getCurrentRowNo();
		ASPQuery q = trans.addQuery(itemblk0);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("REQUISITION_NO = ?");
		q.addParameter("REQUISITION_NO", headset.getRow().getValue("REQUISITION_NO"));
		mgr.submit(trans);

		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
		headset.goTo(currow);

		eval(headblk.generateAssignments());
		eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME",upperset.getRow()));
		up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
		up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
		mch_name = upperset.getRow().getValue("MCH_NAME");
	}


	public void countFindITEM1()
	{
		ASPManager mgr = getASPManager();

		int currow;

		currow = headset.getCurrentRowNo();
		ASPQuery q = trans.addQuery(itemblk1);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("REQUISITION_NO = ?");
		q.addParameter("REQUISITION_NO", headset.getRow().getValue("REQUISITION_NO"));
		mgr.submit(trans);

		itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
		headset.goTo(currow);

		eval(headblk.generateAssignments());
		eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME",upperset.getRow()));
		up_wo_no = toInt(upperset.getRow().getValue("UP_WO_NO"));
		up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
		mch_name = upperset.getRow().getValue("MCH_NAME");
	}

	public void searchWorkOrder()
	{
		ASPManager mgr = getASPManager();

		String buffMchCode;
		String buffMchName;

		ASPQuery q = trans.addQuery(upperblk);
		q.includeMeta("ALL");

		buffWoNo = up_wo_no;

		q = trans.addEmptyQuery(headblk);
		q.setOrderByClause("OBJID");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		q.includeMeta("ALL");

		mgr.submit(trans);

		if (upperset.countRows() == 0)
			mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBRONODATAFOUND: No data found."));

		if (headset.countRows() == 1)
		{
			okFindITEM0();
			okFindITEM1();
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}

		trans.clear();

		cmd = trans.addCustomFunction("GETOBJCODE", "WORK_ORDER_API.Get_Mch_Code", "UP_MCH_CODE");
		cmd.addParameter("WO_NO", Integer.toString(up_wo_no));

		cmd = trans.addCustomFunction("GETOBJNAME", "WORK_ORDER_API.Get_Mch_Code_Description", "MCH_NAME");
		cmd.addParameter("WO_NO", Integer.toString(up_wo_no));

		cmd = trans.addCustomFunction("GETWOSTATE", "WORK_ORDER_API.Get_Wo_Status_Id", "WO_STATUS");
		cmd.addParameter("WO_NO", Integer.toString(up_wo_no));

		trans = mgr.perform(trans);

		buffMchCode = up_mch_code = trans.getValue("GETOBJCODE/DATA/UP_MCH_CODE");
		buffMchName = mch_name = trans.getValue("GETOBJNAME/DATA/MCH_NAME");

		eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
		eval(headblk.generateAssignments());

		ASPBuffer upper = upperset.getRow();
		upper.setNumberValue("UP_WO_NO", buffWoNo);
		upper.setValue("UP_MCH_CODE", buffMchCode);
		upper.setValue("MCH_NAME", buffMchName);
		upperset.setRow(upper);   
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", Integer.toString(buffWoNo));
		q.setOrderByClause("OBJID");
		q.includeMeta("ALL");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		mgr.submit(trans);

		if (headset.countRows() == 1)
		{
			okFindITEM0();
			okFindITEM1();

			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}
		else if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBRONODATA: No data found."));
			headset.clear();
			headlay.setLayoutMode(headlay.MULTIROW_LAYOUT); 
		}

		eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME",upperset.getRow()));  
		eval(headblk.generateAssignments());
		up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
		up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
		mch_name = upperset.getRow().getValue("MCH_NAME");
	}

	//-----------------------------------------------------------------------------
	//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
	//-----------------------------------------------------------------------------

	public void okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();

			headrowno = headset.getCurrentRowNo();

			itemset0.clear();
			ASPQuery q = trans.addEmptyQuery(itemblk0);
			q.addWhereCondition("WO_NO = ? and REQUISITION_NO = ?");
			q.addParameter("WO_NO", Integer.toString(buffWoNo));
		        q.addParameter("REQUISITION_NO", headset.getRow().getValue("REQUISITION_NO"));
			q.setOrderByClause("LINE_NO");
			q.setOrderByClause("RELEASE_NO");   
			q.includeMeta("ALL");

			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBRONODATA: No data found."));
					itemset0.clear();
				}
			}

			headset.goTo(headrowno);

			if (itemset0.countRows() == 1)
				itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
		}
	}

	public void okFindITEM1()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();
			headrowno = headset.getCurrentRowNo();

			itemset1.clear();
			ASPQuery q = trans.addEmptyQuery(itemblk1);
			q.addWhereCondition("WO_NO= ? and REQUISITION_NO = ?");
			q.addParameter("WO_NO", Integer.toString(buffWoNo));
		        q.addParameter("REQUISITION_NO", headset.getRow().getValue("REQUISITION_NO"));
			q.setOrderByClause("LINE_NO");
			q.setOrderByClause("RELEASE_NO");   
			q.includeMeta("ALL");  

			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBRONODATA: No data found."));
					itemset1.clear();
				}
			}

			headset.goTo(headrowno);

			if (itemset1.countRows() == 1)
				itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
		}
	}

	//-----------------------------------------------------------------------------
	//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
	//-----------------------------------------------------------------------------

	public void evalUpper()
	{

		eval(headblk.generateAssignments());
		eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME",upperset.getRow()));
		up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
		up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
		mch_name = upperset.getRow().getValue("MCH_NAME");
	}


	public void setGlobalvalues()
	{
		ASPManager mgr = getASPManager();

		ctx.setGlobal("GLOBAL_WO_NO",mgr.readValue("UP_WO_NO"));
		ctx.setGlobal("GLOBAL_MCH_CODE",mgr.readValue("UP_MCH_CODE"));
		ctx.setGlobal("GLOBAL_MCH_NAME",mgr.readValue("MCH_NAME"));
	}

	public void purchaseOrder0()
	{
		ASPManager mgr = getASPManager();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (itemlay0.isMultirowLayout())
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			count = itemset0.countSelectedRows();
		}
		else
		{
			itemset0.unselectRows();
			count = 1;
		}

		transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
				rowBuff.addItem("ORDER_NO", itemset0.getValue("ORDER_NO"));
			else
				rowBuff.addItem(null, itemset0.getValue("ORDER_NO"));

			transferBuffer.addBuffer("DATA", rowBuff);

			if (itemlay0.isMultirowLayout())
				itemset0.next();
		}

		if (itemlay0.isMultirowLayout())
			itemset0.setFilterOff();

		urlString = createTransferUrl("../purchw/PurchaseOrder.page", transferBuffer);
		newWinHandle = "purchaseOrder0"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	/**    Redirects the user selected line to Inquiry page */
	public void request0()
	{
		ASPManager mgr = getASPManager();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (itemlay0.isMultirowLayout())
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			count = itemset0.countSelectedRows();
		}
		else
		{
			itemset0.unselectRows();
			count = 1;
		}

		transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
			{
				rowBuff.addItem("INQUIRY_NO", itemset0.getValue("INQUIRY_NO"));
				rowBuff.addItem("CONTRACT", itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
			}
			else
			{
				rowBuff.addItem(null, itemset0.getValue("INQUIRY_NO"));
				rowBuff.addItem(null, itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
			}

			transferBuffer.addBuffer("DATA", rowBuff);

			if (itemlay0.isMultirowLayout())
				itemset0.next();
		}

		if (itemlay0.isMultirowLayout())
			itemset0.setFilterOff();

		urlString = createTransferUrl("../purchw/Inquiry.page", transferBuffer);
		newWinHandle = "request0"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	public void purchaseOrder1()
	{
		ASPManager mgr = getASPManager();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (itemlay1.isMultirowLayout())
		{
			itemset1.storeSelections();
			itemset1.setFilterOn();
			count = itemset1.countSelectedRows();
		}
		else
		{
			itemset1.unselectRows();
			count = 1;
		}

		transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
				rowBuff.addItem("ORDER_NO", itemset1.getRow().getFieldValue("ITEM1_ORDERNO"));
			else
				rowBuff.addItem(null, itemset1.getRow().getFieldValue("ITEM1_ORDERNO"));

			transferBuffer.addBuffer("DATA", rowBuff);

			if (itemlay1.isMultirowLayout())
				itemset1.next();
		}

		if (itemlay1.isMultirowLayout())
			itemset1.setFilterOff();

		urlString = createTransferUrl("../purchw/PurchaseOrder.page", transferBuffer);
		newWinHandle = "purchaseOrder1"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	public void request1()
	{
		ASPManager mgr = getASPManager();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (itemlay1.isMultirowLayout())
		{
			itemset1.storeSelections();
			itemset1.setFilterOn();
			count = itemset1.countSelectedRows();
		}
		else
		{
			itemset1.unselectRows();
			count = 1;
		}

		transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
			{
				rowBuff.addItem("INQUIRY_NO", itemset1.getValue("ITEM1_INQUIRY_NO"));
				rowBuff.addItem("CONTRACT", itemset1.getRow().getFieldValue("ITEM1_CONTRACT"));
			}
			else
			{
				rowBuff.addItem(null, itemset1.getValue("ITEM1_INQUIRY_NO"));
				rowBuff.addItem(null, itemset1.getRow().getFieldValue("ITEM1_CONTRACT"));
			}

			transferBuffer.addBuffer("DATA", rowBuff);

			if (itemlay1.isMultirowLayout())
				itemset1.next();
		}

		if (itemlay1.isMultirowLayout())
			itemset1.setFilterOff();

		urlString = createTransferUrl("../purchw/Inquiry.page", transferBuffer);
		newWinHandle = "request1"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	/**    Document Text action itemset0 invokes this */ 
	public void documentText0()
	{
		ASPManager mgr = getASPManager();

		String callingUrl = mgr.getURL();
		ctx.setGlobal("CALLING_URL", callingUrl);

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemset0.getRowSelected());
		else
			itemset0.selectRow();

		bOpenNewWindow = true;
		urlString = "../mpccow/DocumentTextDlg.page?NOTE_ID=" + mgr.URLEncode(itemset0.getValue("NOTEID")) + 
					"&WO_NO=" + mgr.URLEncode(itemset0.getValue("WO_NO")) + 
					"&FRM_NAME=" + mgr.URLEncode("WorkOrderRequisHeaderRMBRO");

		newWinHandle = "documentText0"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	/**    Document Text action itemset1 invokes this */ 
	public void documentText1()
	{
		ASPManager mgr = getASPManager();

		String callingUrl = mgr.getURL();
		ctx.setGlobal("CALLING_URL", callingUrl);

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		if (itemlay1.isMultirowLayout())
			itemset1.goTo(itemset1.getRowSelected());
		else
			itemset1.selectRow();

		bOpenNewWindow = true;
		urlString = "../mpccow/DocumentTextDlg.page?NOTE_ID=" + mgr.URLEncode(itemset1.getValue("ITEM1_NOTEID")) + 
					"&WO_NO=" + mgr.URLEncode(itemset1.getValue("WO_NO")) + 
					"&FRM_NAME=" + mgr.URLEncode("WorkOrderRequisHeaderRMBRO");

		newWinHandle = "documentText1"; 
		// 040129  ARWILK  End  (Enable Multirow RMB actions)
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------     UPPER Block    ----------------------------------------
		//----------------------------------------------------------------------------------------------------

		upperblk = mgr.newASPBlock("UPPER");

		f = upperblk.addField("UP_WO_NO","Number");
		f.setReadOnly();
		f.setFunction("0");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROUWN: WO No");

		f = upperblk.addField("UP_CONTRACT");
		f.setHidden();
		f.setFunction("''");

		f = upperblk.addField("UP_MCH_CODE");
		f.setFunction("''");
		f.setSize(29);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROUMCHCODE: Object ID");

		f = upperblk.addField("MCH_NAME");
		f.setSize(50);
		f.setFunction("''");
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROMNAME: Description");

		f = upperblk.addField("WO_STATUS");
		f.setFunction("''");
		f.setHidden();

		upperblk.setView("DUAL");

		uppertbl = mgr.newASPTable(upperblk);

		upperset = upperblk.getASPRowSet();

		upperbar = mgr.newASPCommandBar(upperblk);

		upperbar.disableCommand(upperbar.FIND);
		upperbar.disableCommand(upperbar.NEWROW);
		upperbar.disableCommand(upperbar.BACK);
		upperbar.disableCommand(upperbar.FORWARD);      
		upperbar.disableCommand(upperbar.BACKWARD);   
		upperbar.disableCommand(upperbar.DUPLICATEROW);

		upperlay = upperblk.getASPBlockLayout();
		upperlay.setDefaultLayoutMode(upperlay.SINGLE_LAYOUT);

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------     HEAD Block    -----------------------------------------
		//---------------------------------------------------------------------------------------------------- 

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("ATTR");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("INFO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ACTION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO","Number");
		f.setHidden();

		f = headblk.addField("MCH_CODE");
		f.setHidden();
		f.setFunction("Active_Separate_API.Get_Mch_Code(:WO_NO)");

		f = headblk.addField("REQUISITION_NO");
		f.setSize(13);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROREQUISITION_NO: Requisition No");

		f = headblk.addField("REQUISITIONERCODE");
		f.setSize(11);
		f.setDynamicLOV("PURCHASE_REQUISITIONER",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROREQUISITIONERCODE: Requisitioner");
                f.setDbName("REQUISITIONER_CODE");
		f.setUpperCase();

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setReadOnly();
		f.setInsertable();
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROCONTRACT: Site");

		f = headblk.addField("REQUISITIONDATE","Date");
		f.setSize(11);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROREQUISITIONDATE: Requisition Date");
                f.setDbName("REQUISITION_DATE");

		f = headblk.addField("STATUSCODE");
		f.setSize(8);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROSTATUSCODE: Status");
		f.setFunction("Purchase_Req_Util_API.Get_Requisition_State(:REQUISITION_NO)");

		f = headblk.addField("ORDER_CODE");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROORDERCODE: Order Code");
		f.setDynamicLOV("PURCHASE_ORDER_TYPE_LOV");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBORDCD: Order Codes"));
		f.setSize(12);
		f.setMaxLength(3);
		f.setMandatory();

		f = headblk.addField("ORDERCODEDESC");
		f.setReadOnly();
		f.setSize(30);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROORDERCODEDESC: Order Code Description");
		f.setFunction("Purchase_Order_Type_API.Get_Description(Purchase_Req_Util_API.Get_Order_Code(REQUISITION_NO))");

		f = headblk.addField("AUTHORIZATION_REQUIRED");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROAUTHOREQUI: Authorization Required");
		f.setCheckBox("NOT_REQUIRED,REQUIRED");
		f.setFunction("Purchase_Requisition_Api.Authorization_Required(:REQUISITION_NO)");
		f.setReadOnly();

		f = headblk.addField("DESTINATION_ID");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROINTDESTINATION: Int Destination");
		f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBROINTDESTINATIONS: Int Destinations"));
		f.setSize(12);
		f.setMaxLength(30);
		f.setUpperCase();           

		f = headblk.addField("INTERNAL_DESTINATION");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROINTDEST: Int Destination Description");

		f = headblk.addField("HEAD_MCH_NAME");
		f.setHidden();
		f.setFunction("Maintenance_Object_API.Get_Mch_Name(Maintenance_Object_API.Get_Sup_Contract(Purchase_Req_Util_API.Get_Contract(:REQUISITION_NO),Active_Separate_API.Get_Mch_Code(:WO_NO)),Active_Separate_API.Get_Mch_Code(:WO_NO))");

		f = headblk.addField("PLANN");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("RELEASE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("POCREATE");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("PLAN_DUM");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("REL_DUM");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CO_ORD");
		f.setHidden();
		f.setFunction("Active_Work_Order_API.Get_Order_Coordinator(:WO_NO)");

		f = headblk.addField("PRE_ACC_EXIST");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROACCEXIST: Pre Accounting Exists");
		f.setCheckBox("0,1");
		f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(PURCHASE_REQUISITION_API.Get_Pre_Accounting_Id(:REQUISITION_NO))");
		f.setReadOnly();

		headblk.setView("WORK_ORDER_REQUIS_PURCH");
		headblk.defineCommand("WORK_ORDER_REQUIS_HEADER_API", "");
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERREQUISHEADERRMBROHD: Requisition"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.defineCommand(headbar.DUPLICATE, "duplicate");
		headbar.defineCommand(headbar.FAVORITE, "favorite");

		//Web Alignment - Replacing Blocks with Tabs
		tabs = mgr.newASPTabContainer();
		tabs.setTabSpace(5);  
		tabs.setTabWidth(100);

		tabs.addTab("Part Requisition Lines","javascript:commandSet('HEAD.activatePartReqLinesTab','')");
		tabs.addTab("No Part Requisition Lines","javascript:commandSet('HEAD.activateNoPartReqLinesTab','')");

		headbar.addCustomCommand("activatePartReqLinesTab","Part Requisition Lines");
		headbar.addCustomCommand("activateNoPartReqLinesTab","No Part Requisition Lines");
		//

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------     ITEM0 Block    ----------------------------------------
		//----------------------------------------------------------------------------------------------------

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("ITEM0_WO_NO","Number", "#");
		f.setSize(11);
		f.setMandatory();
		f.setHidden();
		f.setDbName("WO_NO");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROWO_NO: WO no");

		f = itemblk0.addField("ITEM0_REQUISITION_NO");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_REQUISITION_NO: Requisition no");
		f.setDbName("REQUISITION_NO");

		f = itemblk0.addField("LINE_NO");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROLINE_NO: Line No");

		f = itemblk0.addField("RELEASE_NO");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRORELEASE_NO: Release No");

		f = itemblk0.addField("ITEM0_CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_CONTRACT: Site");
		f.setDbName("CONTRACT");

		f = itemblk0.addField("PART_NO");
		f.setSize(13);
		f.setDynamicLOV("PURCHASE_PART_INVENT_ATTR","CONTRACT",600,445);
		f.setMandatory();
		f.setReadOnly();
		f.setInsertable(); 
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROPARTNO: Part No");
		f.setUpperCase();

		f = itemblk0.addField("DESCRIPTION");
		f.setSize(21);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM_NOTE_TEXT: Description");

		f = itemblk0.addField("VENDOR_NO");
		f.setSize(11);
		f.setDynamicLOV("PURCHASE_PART_SUPPLIER_LOV","PART_NO, CONTRACT",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROVENDORNO: Supplier");

		f = itemblk0.addField("VENDORNAME");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRONAME: Supplier Name");
		f.setDefaultNotVisible();
		f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_Name(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)"); 

		f = itemblk0.addField("CONTACT");
		f.setSize(10);
		f.setDynamicLOV("SUPP_DEF_CONTACT_LOV","VENDOR_NO");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBROVENDORCONT: Contacts"));
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROVENDORCONTACT: Supplier Contact");

		f = itemblk0.addField("PART_OWNERSHIP");
		f.setSize(25);
		f.setInsertable();
		f.setSelectBox();
		f.enumerateValues("PART_OWNERSHIP_API");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROPARTOWNERSHIP: Ownership");

		f = itemblk0.addField("PART_OWNERSHIP_DB");
		f.setSize(20);
                f.setFunction("Part_Ownership_API.Encode(PURCHASE_REQ_LINE_PART_API.Get_Part_Ownership(:REQUISITION_NO, :LINE_NO, :RELEASE_NO))");
		f.setHidden();

		f = itemblk0.addField("OWNING_CUSTOMER_NO");
		f.setSize(15);
		f.setMaxLength(20);
		f.setInsertable();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROPARTOWNER: Owner");
		f.setDynamicLOV("CUSTOMER_INFO");
		f.setUpperCase();

		f = itemblk0.addField("OWNER_NAME");
		f.setSize(20);
		f.setMaxLength(100);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROOWNERNAME: Owner Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNING_CUSTOMER_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("UNIT_MEAS");
		f.setSize(21);
		f.setDynamicLOV("ISO_UNIT",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROUNITMEAS: Unit of Measurement");
		f.setDefaultNotVisible();

		f = itemblk0.addField("ORIGINAL_QTY","Number");
		f.setSize(19);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROORIGINALQTY: Original Quantity");

		f = itemblk0.addField("JOB_ID", "Number");
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_JOB", "ITEM0_WO_NO WO_NO");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBJOBID: Job Id");
		f.setInsertable();
		f.setSize(20);

		f = itemblk0.addField("WANTED_RECEIPT_DATE","Date");
		f.setSize(21);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROWANTEDRECEIPTDATE: Wanted Receipt Date");

		f = itemblk0.addField("LATEST_ORDER_DATE","Date");
		f.setSize(17);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROLATESTORDERDATE: Last Order Date");
		f.setReadOnly();
		f.setInsertable();

		f = itemblk0.addField("LINE_STATE");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_STATUSCODE: Status");

		f = itemblk0.addField("PLANNERBUYER");
		f.setReadOnly();
		f.setSize(11);
		f.setDynamicLOV("INVENTORY_PART_PLANNER",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROPLANNERBUYER: Planner");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Planner_Buyer(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("COST_RETURN");
		f.setSize(17);
		f.setSelectBox();
		f.setMandatory();
		f.enumerateValues("COST_RETURN_API");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROCOST_RETURN: Cost Return at");
		f.setDefaultNotVisible();

		f = itemblk0.addField("BUYER_CODE");
		f.setSize(11);
		f.setDynamicLOV("PURCHASE_BUYER",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROBUYERCODE: Buyer");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk0.addField("ORDER_NO");
		f.setSize(11);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROORDERNO: Order No");
		f.setReadOnly();
		f.setDefaultNotVisible();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		f = itemblk0.addField("INQUIRY_NO");
		f.setHidden();
		f.setFunction("Inquiry_Line_Part_Order_API.Get_Inquiry_No_From_Req(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		f = itemblk0.addField("BUY_UNIT_MEAS");
		f.setSize(16);
		f.setDynamicLOV("ISO_UNIT",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROBUYUNITMEAS: Buy Unit");
		f.setDefaultNotVisible();

		f = itemblk0.addField("ASSG_LINE_NO");
		f.setSize(14);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROASSGLINENO: Order Line No");
		f.setDefaultNotVisible();

		f = itemblk0.addField("ASSG_RELEASE_NO");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROASSGRELEASENO: Order Release No");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk0.addField("LINEORDERSTATUS");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROLINEORDERSTATUS: Order Line Status");
		f.setReadOnly();
		f.setFunction("Purchase_Req_Util_API.Get_Line_Order_Status(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("CATALOG_CONTRACT");
		f.setSize(19);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROCATALOG_CONTRACT: Sales Part Site");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk0.addField("CATALOG_NO");
		f.setSize(19);
		f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROCATALOG_NO: Sales Part Number");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk0.addField("CATALOGDESC");
		f.setSize(25);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROCATALOGDESC: Sales Part Description");
		f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("PRICE_LIST_NO");
		f.setSize(15);
		f.setDynamicLOV("SALES_PRICE_LIST",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROPRICE_LIST_NO: Price List No");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk0.addField("ITEM0_LISTPRICE", "Number");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_LISTPRICE: Sales Price");
		f.setFunction("SALES_PART_API.Get_List_Price(:CATALOG_CONTRACT,:CATALOG_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("ITEM0_SALESPRICEAMOUNT", "Number");
		f.setSize(19);
		f.setReadOnly(); 
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_SALESPRICEAMOUNT: Sales Price Amount");
		f.setFunction("SALES_PART_API.Get_List_Price(:CATALOG_CONTRACT,:CATALOG_NO)*Purchase_Req_Util_API.Get_Line_Original_Qty(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk0.addField("NOTE_TEXT");
		f.setSize(14);
		f.setHeight(3);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0_NOTE_TEXT: Note");
		f.setDefaultNotVisible();

		f = itemblk0.addField("LINE_STATUS");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRODBSTATUSCODE: Status");

		f = itemblk0.addField("WANTEDDELIVERYDATE","Date");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROWANTEDDELIVERYDATE: Wanted delivery");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Wanted_Delivery_Date(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

		f = itemblk0.addField("NOTETEXT");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRONOTETEXT: Note");
		f.setHeight(3);
		f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Text(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

		f = itemblk0.addField("PARTNO");
		f.setSize(11);
		f.setHidden();
		f.setFunction("''");
		f.setUpperCase();

		f = itemblk0.addField("NOTEID","Number");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRONOTEID: Note Id");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

		f = itemblk0.addField("REQ_ORDER_NO");
		f.setHidden();
		f.setFunction("''");

		f = itemblk0.addField("REQ_INFO");
		f.setHidden(); 
		f.setFunction("''");

		f = itemblk0.addField("ITEM0_PREPOST_EXIST");
		f.setReadOnly(); 
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM0PREPOSTEXIST: Pre Accounting Exists");
		f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id(REQUISITION_NO,LINE_NO,RELEASE_NO))");
		f.setCheckBox("0,1");
		f.setDefaultNotVisible();

		f = itemblk0.addField("TECHNICAL_COORDINATOR_ID");
		f.setSize(11);
		f.setDynamicLOV("TECHNICAL_COORDINATOR_LOV");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBTECHCOOD: Tech Coordinators"));
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBTECHCOORDID: Tech Coordinator");
		f.setDefaultNotVisible();
		f.setUpperCase();

		f = itemblk0.addField("PROCESS_TYPE");
		f.setSize(20);
		f.setDynamicLOV("ORDER_PROC_TYPE");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBORDPROCT: Order Proc Types"));
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDPROCTYPE: Order Proc Type");
		f.setMaxLength(3);
		f.setUpperCase();
		f.setDefaultNotVisible();

		itemblk0.setView("PART_WO_REQUIS_PURCH_LINE");
		itemblk0.defineCommand("PART_WO_REQUIS_LINE_API", "");
		itemblk0.setMasterBlock(headblk);

		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);

		if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
			itembar0.addCustomCommand("purchaseOrder0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBROPURCHORD: Purchase Order..."));
		if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
			itembar0.addCustomCommand("request0", mgr.translate("PCMWWORKORDERREQUISHEADERREQUESTRMB: Request..."));
		if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
			itembar0.addCustomCommand("documentText0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBDOCTEXTP: Document Text..."));

		itembar0.defineCommand(itembar0.OKFIND, "okFindITEM0"); 
		itembar0.defineCommand(itembar0.COUNTFIND, "countFindITEM0");
		itembar0.enableCommand(itembar0.FIND);

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)

		if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
                    itembar0.addCommandValidConditions("purchaseOrder0", "ORDER_NO",    "Disable", "null");

		if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
                    itembar0.addCommandValidConditions("request0",       "INQUIRY_NO", "Disable", "null");

		itembar0.enableMultirowAction();
		if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
			itembar0.removeFromMultirowAction("documentText0");
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWWORKORDERREQUISHEADERRMBROITM0: Part Requisition Lines"));
		itemtbl0.setWrap();
		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		itemtbl0.enableRowSelect();
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------     ITEM1 Block    ----------------------------------------
		//----------------------------------------------------------------------------------------------------

		itemblk1 = mgr.newASPBlock("ITEM1");

		f = itemblk1.addField("ITEM1_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk1.addField("ITEM1_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk1.addField("ITEM1_WO_NO", "Number");
		f.setSize(11);
		f.setMandatory();
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_WO_NO: WO No");
		f.setDbName("WO_NO");

		f = itemblk1.addField("ITEM1_REQUISITION_NO");
		f.setSize(11);
		f.setDynamicLOV("WORK_ORDER_REQUIS_HEADER","WO_NO",600,445);
		f.setMandatory();
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_REQUISITION_NO: Requisition no");
		f.setDbName("REQUISITION_NO");

		f = itemblk1.addField("ITEM1_LINE_NO");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_LINE_NO: Line No");
		f.setDbName("LINE_NO");

		f = itemblk1.addField("ITEM1_RELEASE_NO");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_RELEASE_NO: Release No");
		f.setDbName("RELEASE_NO");

		f = itemblk1.addField("ITEM1_CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_CONTRACT: Site");
		f.setDbName("CONTRACT");

		f = itemblk1.addField("ITEM_NOTE_TEXT2");
		f.setSize(14);
		f.setMandatory();
		f.setDbName("DESCRIPTION");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM_NOTE_TEXT2: Description");

		f = itemblk1.addField("STATGRP");
		f.setSize(15);
		f.setDynamicLOV("PURCHASE_PART_GROUP",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROSTATGRP: Purchase Group");
		f.setDbName("STAT_GRP");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_VENDORNO");
		f.setSize(11);
		f.setDynamicLOV("SUPPLIER_INFO",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_VENDORNO: Supplier");
		f.setDbName("VENDOR_NO");

		f = itemblk1.addField("ITEM1_VENDORNAME");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1NAME1: Supplier Name");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_Name(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)"); 

		f = itemblk1.addField("ITEM1_VENDORCONTACT");
		f.setSize(10);
		f.setDynamicLOV("SUPP_DEF_CONTACT_LOV","ITEM1_VENDORNO");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBVENDORCONT1: Contacts"));
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBVCONTRACT2: Supplier Contact");
		f.setDbName("CONTACT");

		f = itemblk1.addField("ITEM1_WANTEDRECEIPTDATE","Date");
		f.setSize(21);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_WANTEDRECEIPTDATE: Wanted Receipt Date");
		f.setDbName("WANTED_RECEIPT_DATE");

		f = itemblk1.addField("ITEM1_ORIGINALQTY","Number");
		f.setSize(19);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_ORIGINALQTY: Original Quantity");
		f.setDbName("ORIGINAL_QTY");

		f = itemblk1.addField("FBUYUNITPRICE","Number");
		f.setSize(19);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROFBUYUNITPRICE: Unit Price");
		f.setDbName("FBUY_UNIT_PRICE");

		f = itemblk1.addField("DISCOUNT","Number");
		f.setSize(19);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBRODISCOUNT: Discount");
		f.setDbName("LINE_DISCOUNT");

		f = itemblk1.addField("ITEM1_LATESTORDERDATE","Date");
		f.setSize(15);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_LATESTORDERDATE: Last Order Date");
		f.setDbName("LATEST_ORDER_DATE");

		f = itemblk1.addField("ITEM1_STATUSCODE");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_STATUSCODE: Status");
		f.setDbName("LINE_STATE");

		f = itemblk1.addField("ITEM1_PLANNERBUYER");
		f.setSize(11);
		f.setReadOnly();
		f.setDynamicLOV("INVENTORY_PART_PLANNER",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_PLANNERBUYER: Planner");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Planner_Buyer(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_COST_RETURN");
		f.setSize(17);
		f.setSelectBox();
		f.enumerateValues("COST_RETURN_API");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_COST_RETURN: Cost Return at");
		f.setDbName("COST_RETURN");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_ORDERNO");
		f.setSize(11);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_ORDERNO: Order No");
		f.setDbName("ORDER_NO");
		f.setDefaultNotVisible();

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		f = itemblk1.addField("ITEM1_INQUIRY_NO");
		f.setHidden();
		f.setFunction("Inquiry_Line_Part_Order_API.Get_Inquiry_No_From_Req(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		f = itemblk1.addField("ITEM1_BUYUNITMEAS");
		f.setSize(16);
		f.setDynamicLOV("ISO_UNIT",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_BUYUNITMEAS: Unit");
		f.setDbName("BUY_UNIT_MEAS");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_ASSGLINENO");
		f.setSize(14);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_ASSGLINENO: Order Line No");
		f.setDbName("ASSG_LINE_NO");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_ASSGRELEASENO");
		f.setSize(18);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_ASSGRELEASENO: Order Release No");
		f.setDbName("ASSG_RELEASE_NO");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_LINEORDERSTATUS");
		f.setSize(18);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_LINEORDERSTATUS: Order Line Status");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Order_Status(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_CATALOG_CONTRACT");
		f.setSize(17);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_CATALOG_CONTRACT: Sales Part Site");
		f.setDbName("CATALOG_CONTRACT");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_CATALOG_NO");
		f.setSize(19);
		f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM1_CATALOG_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_CATALOG_NO: Sales Part Number");
		f.setDbName("CATALOG_NO");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_CATALOGDESC");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_CATALOGDESC: Catalog No Description");
		f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM1_CATALOG_CONTRACT,:ITEM1_CATALOG_NO)");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_PRICE_LIST_NO");
		f.setSize(15);
		f.setDynamicLOV("SALES_PRICE_LIST",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_PRICE_LIST_NO: Price List No");
		f.setDbName("PRICE_LIST_NO");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_LISTPRICE", "Number");
		f.setSize(12);
		f.setReadOnly();
		f.setFunction("SALES_PART_API.Get_List_Price(:ITEM1_CATALOG_CONTRACT,:ITEM1_CATALOG_NO)");
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_LISTPRICE: Sales Price");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_SALESPRICEAMOUNT", "Number");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_SALESPRICEAMOUNT: Sales Price Amount");
		f.setReadOnly();
		f.setFunction("SALES_PART_API.Get_List_Price(:ITEM1_CATALOG_CONTRACT,:ITEM1_CATALOG_NO) * Purchase_Req_Util_API.Get_Line_Original_Qty(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_NOTE_TEXT");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_NOTE_TEXT: Note");
		f.setHeight(3);
		f.setDbName("NOTE_TEXT");
		f.setDefaultNotVisible();

		f = itemblk1.addField("ITEM1_DBSTATUSCODE");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_DBSTATUSCODE: Status");
		f.setDbName("LINE_STATUS");

		f = itemblk1.addField("ITEM1_DESCRIPTION");
		f.setSize(21);
		f.setHidden();
		f.setHeight(3);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_DESCRIPTION: Description");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Description(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

		f = itemblk1.addField("ITEM1_WANTEDDELIVERYDATE","Date");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_WANTEDDELIVERYDATE: wanted delivery");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Wanted_Delivery_Date(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

		f = itemblk1.addField("ITEM1_NOTETEXT");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_NOTETEXT: Note");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Text(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

		f = itemblk1.addField("ITEM1_CONTRACT_DUM");
		f.setSize(11);
		f.setHidden();
		f.setFunction("''");
		f.setUpperCase();

		f = itemblk1.addField("ITEM1_NOTEID","Number");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1_NOTEID: Note");
		f.setHeight(3);
		f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

		f = itemblk1.addField("ITEM1_PREPOST_EXIST");
		f.setReadOnly(); 
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBROITEM1PREPOSTEXIST: Pre Accounting Exists");
		f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id(REQUISITION_NO,LINE_NO,RELEASE_NO))");
		f.setCheckBox("0,1");

		f = itemblk1.addField("ITEM1_TECHCOORDID");
		f.setSize(11);
		f.setDynamicLOV("TECHNICAL_COORDINATOR_LOV");
		f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBTECHCOOD1: Tech Coordinators"));
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBTECHCOORDID2: Tech Coordinator");
		f.setDefaultNotVisible();
		f.setDbName("TECHNICAL_COORDINATOR_ID");
		f.setUpperCase();

		f = itemblk1.addField("ITEM1_BUYERCODE");
		f.setSize(11);
		f.setInsertable();
		f.setDynamicLOV("PURCHASE_BUYER_LOV",600,445);
		f.setLabel("PCMWWORKORDERREQUISHEADERRMBBUYERCODE1: Buyer");
		f.setDbName("BUYER_CODE");
		f.setUpperCase();

		itemblk1.setView("NOPART_WO_REQUIS_PURCH_LINE");
		itemblk1.defineCommand("NOPART_WO_REQUIS_LINE_API", "");
		itemblk1.setMasterBlock(headblk);

		itemset1 = itemblk1.getASPRowSet();

		itembar1 = mgr.newASPCommandBar(itemblk1);

		if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
			itembar1.addCustomCommand("purchaseOrder1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBROPURCHORD1: Purchase Order..."));
		if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
			itembar1.addCustomCommand("request1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBROREQUESTRMB: Request..."));
		if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
			itembar1.addCustomCommand("documentText1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBRODOCTEXTNP: Document Text..."));

		itembar1.defineCommand(itembar1.OKFIND, "okFindITEM1");
		itembar1.defineCommand(itembar1.COUNTFIND, "countFindITEM1");
		itembar1.enableCommand(itembar1.FIND);

		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
                    itembar1.addCommandValidConditions("purchaseOrder1", "ORDER_NO",    "Disable", "null");
		if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
                    itembar1.addCommandValidConditions("request1",       "ITEM1_INQUIRY_NO", "Disable", "null");

		itembar1.enableMultirowAction();
		if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
			itembar1.removeFromMultirowAction("documentText1");
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		itemtbl1 = mgr.newASPTable(itemblk1);
		itemtbl1.setTitle(mgr.translate("PCMWWORKORDERREQUISHEADERRMBROITM1: No Part Requisition Lines"));
		itemtbl1.setWrap();
		// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
		itemtbl1.enableRowSelect();
		// 040129  ARWILK  End  (Enable Multirow RMB actions)

		itemlay1 = itemblk1.getASPBlockLayout();
		itemlay1.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------

		itemblk2 = mgr.newASPBlock("ITEM2");
		f = itemblk2.addField("APPOWNER");
		f.setHidden();
		f.setFunction("''");

		itemblk2.setView("DUAL");
		itemset2 = itemblk2.getASPRowSet();

		//----------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------

		b = mgr.newASPBlock("COST_RETURN");

		b.addField("CLIENT_VALUES0");

		b = mgr.newASPBlock("ITEM1_COST_RETURN");

		b.addField("CLIENT_VALUES1");
	}

	//Web Alignment - replacing blocks with tabs
	public void activatePartReqLinesTab()
	{
		tabs.setActiveTab(1);
	}

	public void activateNoPartReqLinesTab()
	{
		tabs.setActiveTab(2);
	}
	//

	// 040129  ARWILK  Begin  (Enable Multirow RMB actions)
	private String createTransferUrl(String url, ASPBuffer object)
	{
		ASPManager mgr = getASPManager();

		try
		{
			String pkg = mgr.pack(object,1900 - url.length());
			char sep = url.indexOf('?')>0 ? '&' : '?';
			urlString = url + sep + "__TRANSFER=" + pkg ;
			return urlString;
		}
		catch (Throwable any)
		{
			return null;
		}
	}
	// 040129  ARWILK  End  (Enable Multirow RMB actions)

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		headbar.removeCustomCommand("activatePartReqLinesTab");
		headbar.removeCustomCommand("activateNoPartReqLinesTab");

		fldPartNo = mgr.translate("PCMWWORKORDERREQUISHEADERRMBROPARTNOFLD: Part+No");
		fldSupplier = mgr.translate("PCMWWORKORDERREQUISHEADERRMBROSUPPLIERFLD: Supplier");
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERREQUISHEADERRMBROTITLE: Requisition";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERREQUISHEADERRMBROTITLE: Requisition";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(upperlay.show());

		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && headset.countRows() > 0)
		{
			appendToHTML(tabs.showTabsInit());

			if (tabs.getActiveTab() == 1)
				appendToHTML(itemlay0.show());
			else if (tabs.getActiveTab() == 2)
				appendToHTML(itemlay1.show());
		}

		appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   el = document.getElementById(el);\n");
		appendDirtyJavaScript("   if(el.style.display!='none')\n");
		appendDirtyJavaScript("   {  \n");
		appendDirtyJavaScript("      el.style.display='none';\n");
		appendDirtyJavaScript("      tempTBL.style.display='none';\n");
		appendDirtyJavaScript("   }  \n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("   {  \n");
		appendDirtyJavaScript("      el.style.display='block';\n");
		appendDirtyJavaScript("      tempTBL.style.display='block';\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovPartNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__')\n");
		appendDirtyJavaScript("	   openLOVWindow('PART_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_INVENT_ATTR&__FIELD=");
		appendDirtyJavaScript(fldPartNo);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,445,'');\n");
		appendDirtyJavaScript("	 else\n");
		appendDirtyJavaScript("	   openLOVWindow('PART_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_INVENT_ATTR&__FIELD=");
		appendDirtyJavaScript(fldPartNo);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		,600,445,'');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovVendorno(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__')\n");
		appendDirtyJavaScript("	   openLOVWindow('VENDOR_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=");
		appendDirtyJavaScript(fldSupplier);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
		appendDirtyJavaScript("		,600,445,'');\n");
		appendDirtyJavaScript("	 else\n");
		appendDirtyJavaScript("	   openLOVWindow('VENDOR_NO',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=");
		appendDirtyJavaScript(fldSupplier);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
		appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
		appendDirtyJavaScript("		,600,445,'');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("window.name = \"WorkOrderRequisHeaderRMB\";\n");

		// 040129  ARWILK  Begin  (Remove uneccessary global variables)
		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070726	
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
		}
		// 040129  ARWILK  End  (Remove uneccessary global variables)           
	}
}
