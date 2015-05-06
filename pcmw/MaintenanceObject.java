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
*  File        : MaintenanceObject.java 
*  Created     : SHFELK  010307
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  SHCHLK  011016  Did the security check. 
*  SHAFLK  020430  Bug 29240, Changed addCustomCommand of "Detached Spare Part List... and sparePartList()."  
*  SHAFLK  020508  Bug 29948,Changed HTML part and run() to return to correct page after Save&Return.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  ThWilk  040429  Modified sparePartList() to prevent getting invalid identifiers.
*  Thwilk  040601  Added PM_REVISION and performed necesary changes relating to the key change under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  041116  Replaced of Inventory_Part_Location_API methods from Inventory_Part_In_Stock_API methods
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMNILK  070718  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObject extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObject");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

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

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String form_name;
	private String strPmNo;
	private String isDataTransfered;
	private String selfClose;
	private ASPTransactionBuffer trans;
	private String sPmNo;
	private String sPmRev;
	private String sMchCode;
	private String sContract;
	private String oldurl;
	private ASPBuffer buff1;
	private ASPBuffer row1;
	private String val;
	private ASPQuery q;
	private int currrow;
	private String n;
	private ASPCommand cmd;
	private ASPBuffer buffer;
	private String sFormTitle;
	private ASPBuffer row;
	private ASPBuffer secBuff;
	private boolean varSec;
	private boolean secDetSp;
	//Bug 29948, start
	private String frameName;
	private String qrystr;
	//Bug 29948,end
	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceObject(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		strPmNo = isDataTransfered;
		isDataTransfered = selfClose;
		selfClose = "";
		ASPManager mgr = getASPManager();


		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		sPmNo= ctx.readValue("SPMNO","");
		sPmRev= ctx.readValue("SPMREV","");
		sMchCode = ctx.readValue("SMCHCODE","");
		sContract = ctx.readValue("SCONTRACT","");

		secDetSp = ctx.readFlag("SECDETSP",false);
		varSec = ctx.readFlag("VARSEC",false);
		//Bug 29948, start
		frameName = ctx.readValue("FRAMENAME","");
		qrystr = ctx.readValue("QRYSTR","");
		//Bug 29948, start
		if ("WorkOrderRequisHeaderRMB".equals(form_name))
			oldurl = ctx.getGlobal("OLD_URL") ;

		strPmNo= ctx.readValue("STR",strPmNo);


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());

		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
		{
			isDataTransfered = "true";
			sPmNo=mgr.readValue("PM_NO","");
			sPmRev=mgr.readValue("PM_REVISION","");
			sMchCode = mgr.readValue("MCH_CODE","");
			sContract = mgr.readValue("CONTRACT","");
			//Bug 29948, start
			frameName = mgr.readValue("FRMNAME","");
			qrystr = mgr.readValue("QRYSTR","");
			//Bug 29948, end
			okFind();
		}

		else if (mgr.dataTransfered())
		{
			buff1 = mgr.getTransferedData();
			row1 = buff1.getBufferAt(0);
			sMchCode = row1.getValue("MCH_CODE");
			okFind();
		}

		checkSecurity();
		adjust();

		ctx.writeValue("SPMNO",sPmNo);
		ctx.writeValue("SPMREV",sPmRev);
		ctx.writeValue("SMCHCODE",sMchCode);
		ctx.writeValue("SCONTRACT",sContract);

		ctx.writeFlag("SECDETSP",secDetSp);
		ctx.writeFlag("VARSEC",varSec);
		//Bug 29948, start
		ctx.writeValue("FRAMENAME",frameName);
		ctx.writeValue("QRYSTR",qrystr);
		//Bug 29948, end
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addEmptyQuery(headblk);
                //Bug 58216 start
		q.addWhereCondition("MCH_CODE = ? AND CONTRACT = ?");
                q.addParameter("MCH_CODE",sMchCode);
                q.addParameter("CONTRACT",sContract);
                //Bug 58216 end
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (( headset.countRows() == 0 ) &&  ! (isDataTransfered=="true"))
		{
			mgr.showAlert("PCMWMAINTENANCEOBJECTNODATA: No data found.");
			headset.clear();
			headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);    
		}
		headlay.setLayoutMode(headlay.SINGLE_LAYOUT); 
		if (headset.countRows() == 1)
			okFindITEM0();
	}

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

	public void okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addEmptyQuery(itemblk0);
                //Bug 58216 start
		q.addWhereCondition("MCH_CODE = ?");
                q.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));
                //Bug 58216 end
		q.includeMeta("ALL");

		currrow = headset.getCurrentRowNo();
		mgr.submit(trans);

		headset.goTo(currrow);
	}

	public void okFindITEM0Alert()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(itemblk0);
                //Bug 58216 start
		q.addWhereCondition("MCH_CODE = ?");
                q.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));
                //Bug 58216 end
		q.includeMeta("ALL");

		currrow = headset.getCurrentRowNo();
		mgr.submit(trans);

		headset.goTo(currrow);

		if (itemset0.countRows() == 0)
		{
			mgr.showAlert("PCMWMAINTENANCEOBJECTNODATA: No data found.");
			itemset0.clear();
		}
	}

	public void countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(itemblk0);
                //Bug 58216 start
		q.addWhereCondition("MCH_CODE = ?");
                q.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		currrow  = headset.getCurrentRowNo();

		mgr.submit(trans);

		n = itemset0.getRow().getValue("N");
		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
		itemset0.clear();
		headset.goTo(currrow);
	}

	public void saveReturnITEM0()
	{
		ASPManager mgr = getASPManager();

		if (itemset0.countRows()>0)
		{
			itemset0.changeRow();
			sPmNo= ctx.readValue("SPMNO",sPmNo);
			sPmRev= ctx.readValue("SPMREV",sPmRev);

			cmd = trans.addCustomCommand("PMNEW", "PM_Action_Spare_Part_API.New");
			cmd.addParameter("PM_NO1",sPmNo);
			cmd.addParameter("PM_REVISION",sPmRev);
			cmd.addParameter("SPARE_CONTRACT",itemset0.getValue("SPARE_CONTRACT"));
			cmd.addParameter("SPARE_ID",itemset0.getValue("SPARE_ID"));
			cmd.addParameter("QTY",itemset0.getValue("QTY"));

			mgr.perform(trans);
			selfClose = "TRUE";
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void sparePartList()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemset0.getRowSelected());

		if (itemset0.getValue("HAS_SPARE_STRUCTURE").equals(headset.getValue("SYES")))	 //Bug 29240
		{
			buffer = mgr.newASPBuffer();
			row = buffer.addBuffer("0");
			//Bug 29943, start
			row.addItem("SPARE_ID",itemset0.getValue("SPARE_ID"));
			//row.addItem("PM_NO",sPmNo);
			//row.addItem("FRAME",frameName);
			mgr.transferDataTo("../equipw/EquipmentSpareStructure2.page",buffer);
			//Bug 29943, end
		}
		else
			mgr.showAlert(mgr.translate("PCMWMAINTENANCEOBJECTCANNOT: Can not perform on selected line."));
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("MCH_CODE");
		f.setSize(30);
		f.setMaxLength(100);
		f.setLabel("PCMWMAINTENANCEOBJECTMCHCODE: Object ID");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("DESCRIPTION");
		f.setSize(29);
		f.setLabel("PCMWMAINTENANCEOBJECTDESC: Description");
		f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
		mgr.getASPField("MCH_CODE").setValidation("DESCRIPTION");
		f.setReadOnly();


		f = headblk.addField("CONTRACT");
		f.setSize(7);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWMAINTENANCEOBJECTCONT: Site");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("PM_NO1", "Number");
		f.setSize(15);
		f.setHidden();
		f.setLabel("PCMWMAINTENANCEOBJECTPMNO: PM No");
		f.setFunction("''");

		f = headblk.addField("PM_REVISION");
		f.setSize(15);
		f.setHidden();
		f.setLabel("PCMWMAINTENANCEOBJECTPMREVISION: PM Revision");
		f.setFunction("''");


		f = headblk.addField("FRAME");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("SYES");
		f.setHidden();
		f.setFunction("Translate_Boolean_API.Get_Client_Value(1)");

		headblk.setView("MAINTENANCE_OBJECT");
		headblk.defineCommand("MAINTENANCE_OBJECT_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DUPLICATEROW);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTHD: Spare Parts in Object"));
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("QTY","Number");
		f.setSize(16);
		f.setMandatory();
		f.setLabel("PCMWMAINTENANCEOBJECTQTY: Planned Quantity");
		f.setFunction("''");

		f = itemblk0.addField("SPARE_ID");
		f.setSize(15);
		f.setDynamicLOV("INVENTORY_PART_WO_LOV",600,445);
		f.setLabel("PCMWMAINTENANCEOBJECTSPAREID: Part No");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("PART_NO");
		f.setHidden();
		f.setFunction("SPARE_ID");

		f = itemblk0.addField("ITEM0_DESCRIPTION");
		f.setSize(22);
		f.setLabel("PCMWMAINTENANCEOBJECTIT0DESC: Description");
		f.setFunction("Purchase_Part_API.Get_Description(:SPARE_CONTRACT,:SPARE_ID)");
		f.setReadOnly();

		f = itemblk0.addField("SPARE_CONTRACT");
		f.setSize(12);
		f.setLabel("PCMWMAINTENANCEOBJECTSPARECON: Site");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("INVENTORYFLAG");
		f.setSize(14);
		f.setLabel("PCMWMAINTENANCEOBJECTINVFLAG: Inventory Part");
		f.setFunction("Purchase_Part_API.Get_Inventory_Flag(:SPARE_CONTRACT,:SPARE_ID)");
		f.setReadOnly();

		f = itemblk0.addField("QTYONHAND","Number");
		f.setSize(15);
		f.setLabel("PCMWMAINTENANCEOBJECTQTYONHAND: Quantity on Hand");
		f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Qty_Onhand(:SPARE_CONTRACT,:SPARE_ID,NULL)");
		f.setReadOnly();

		f = itemblk0.addField("UNITMEAS");
		f.setSize(11);
		f.setLabel("PCMWMAINTENANCEOBJECTUNITMEAS: Unit");
		f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:SPARE_ID)");
		f.setReadOnly();

		f = itemblk0.addField("HAS_SPARE_STRUCTURE");
		f.setSize(9);
		f.setLabel("PCMWMAINTENANCEOBJECTHASSPSTR: Structure");
		f.setReadOnly();

		f = itemblk0.addField("DIMQUALITY");
		f.setSize(16);
		f.setLabel("PCMWMAINTENANCEOBJECTDIMQLTY: Dimension/Quality");
		f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:SPARE_ID)");
		f.setReadOnly();

		f = itemblk0.addField("TYPEDESIGNATION");
		f.setSize(15);
		f.setLabel("PCMWMAINTENANCEOBJECTTYPEDESIGN: Type Designation");
		f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:SPARE_ID)");
		f.setReadOnly();

		f = itemblk0.addField("MCH_PART");
		f.setSize(11);
		f.setLabel("PCMWMAINTENANCEOBJECTMCHPART: Object Part");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("DRAWING_NO");
		f.setDefaultNotVisible();
		f.setSize(18);
		f.setLabel("PCMWMAINTENANCEOBJECTDRAWNO: Drawing No");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("DRAWING_POS");
		f.setDefaultNotVisible();
		f.setSize(16);
		f.setLabel("PCMWMAINTENANCEOBJECTDRAWPOS: Drawing Position");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("NOTE");
		f.setDefaultNotVisible();
		f.setSize(20);
		f.setLabel("PCMWMAINTENANCEOBJECTNOTE: Note");
		f.setReadOnly();

		f = itemblk0.addField("ITEM0_MCH_CODE");
		f.setHidden();
		f.setDbName("MCH_CODE");

		itemblk0.setView("EQUIPMENT_OBJECT_SPARE");
		itemblk0.defineCommand("EQUIPMENT_OBJECT_SPARE_API","New__,Modify__,Remove__");
		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);

		itembar0.defineCommand(headbar.OKFIND,"okFindITEM0Alert");
		itembar0.defineCommand(headbar.COUNTFIND,"countFindITEM0");  
		itembar0.defineCommand(headbar.SAVERETURN,"saveReturnITEM0","checkItem0Fields(-1)"); 
		itembar0.disableCommand(headbar.NEWROW);
		itembar0.disableCommand(headbar.DELETE);
		itembar0.disableCommand(headbar.DUPLICATEROW);

		if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure2.page")) //Bug 29943
			itembar0.addCustomCommand("sparePartList",mgr.translate("PCMWMAINTENANCEOBJECTCDN: Detached Spare Part List..."));	//Bug 29240

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWMAINTENANCEOBJECTTBL0: Spare Parts"));
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

		mgr.endASPEvent();
	}

	public void checkSecurity()
	{
		ASPManager mgr = getASPManager();

		if (!varSec)
		{
			trans.clear();

			trans.addSecurityQuery("PURCHASE_PART");
			trans.addPresentationObjectQuery("EQUIPW/EquipmentSpareStructure2.page");	//Bug 29943

			trans = mgr.perform(trans);

			secBuff = trans.getSecurityInfo();

			if (secBuff.itemExists("PURCHASE_PART") && secBuff.namedItemExists("EQUIPW/EquipmentSpareStructure2.page"))	//Bug 29943
				secDetSp = true;

			varSec = true;
		}
	}     

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		sFormTitle = mgr.translate("PCMWMAINTENANCEOBJECTFORMTITLE: Spare Parts in Object");


		if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure2.page"))//Bug 29943
		{
			if (!secDetSp)
				itembar0.removeCustomCommand("sparePartList");
		}
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return sFormTitle;
	}

	protected String getTitle()
	{
		return "PCMWMAINTENANCEOBJECTMASTERTITLE: Spare Parts in Object";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (itemlay0.isVisible() && (itemset0.countRows()>0))
			appendToHTML(itemlay0.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		//Bug 29948, start
		appendDirtyJavaScript("window.name = \"frmSpare\";\n");   

		appendDirtyJavaScript("selfClose = '");
		appendDirtyJavaScript(selfClose);
		appendDirtyJavaScript("';\n");

		appendDirtyJavaScript("if (selfClose == 'TRUE')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));		//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("' + \"&OKSPAREPART=1\",'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName)); 	//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("	self.close();\n");    
		appendDirtyJavaScript("}\n");
		//Bug 29948, end
	}
}
