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
*  File        : WorkOrderProjOvw.java 
*  VAGULK  040706  Created.
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  ARWILK  041112  Replaced getContents with printContents.
*  NIJALK  051031  Removed field "PACKING_SLIP_ID" (AMAD108 To be removed).
*  NIJALK  060322  Renamed 'Work Master' to 'Executed By'.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100 .
*  NIJALK  080202  Bug 66456, Modified preDefine().
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderProjOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderProjOvw");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock printblk;
	private ASPRowSet printset;
	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String newreq;
	private int currrow;
	private ASPCommand cmd;
	private ASPQuery q;
	private ASPBuffer data;
	private String current_url;  
	private String attr3;
	private String attr4;
	private String attr;
	private ASPBuffer r;
	private ASPBuffer key;
	private String head_command;
	private String sCompany; 
	private String attr1;  
	private String attr2; 
	//============ Variables for Security check for RMBs ===============
	private boolean again;
	private boolean printServO_;
	private boolean printWO_;
	private boolean copy_;
	private boolean prepareWo_;

	//Web Alignment - replace Links with RMBs
	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
	//

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderProjOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager(); 

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		//=============== Variables for Security check for RMBs =================
		again = ctx.readFlag("AGAIN",again);
		printServO_= ctx.readFlag("PRINTSERVO",false);
		printWO_= ctx.readFlag("PRINTWO",false);
		copy_= ctx.readFlag("COPY",false);
		prepareWo_ = ctx.readFlag("PREPAREWO",false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
			findTransfered();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FAULT_REP_FLAG")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();

		if (!again)
		{
			checkObjAvaileble();
			again = true;
		}

		adjust();

		ctx.writeFlag("AGAIN", again);
		ctx.writeFlag("PRINTSERVO", printServO_);
		ctx.writeFlag("PRINTWO", printWO_);
		ctx.writeFlag("COPY", copy_);
		ctx.writeFlag("PREPAREWO", prepareWo_);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------


//: Parameter types are not recognized and set them to String. Declare type[s] for (command)
	public void perform( String command) 
	{
		ASPManager mgr = getASPManager();

		//Web Alignment - Multirow Action
		trans.clear();

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.markSelectedRows(command);
			mgr.submit(trans);
		}
		else
		{
			headset.unselectRows();
			headset.markRow(command);
			int currrow = headset.getCurrentRowNo();
			mgr.submit(trans);

			headset.goTo(currrow);
		}   
	}


	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		String txt="";  

		if ("MCH_CODE".equals(val))
		{
			String mch = "";
			String cur = "";
			String grp = "";
			String crt = "";
			String mchcon = "";

			cmd = trans.addCustomFunction("OBJMCHCON","EQUIPMENT_OBJECT_API.Get_Contract","MCH_CODE_CONTRACT");
			cmd.addParameter("MCH_CODE");

			cmd = trans.addCustomFunction( "OBJDESC","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION" );
			cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
			cmd.addParameter("MCH_CODE");

			cmd = trans.addCustomFunction( "CURRPOS","PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction","LATEST_TRANSACTION" );
			cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
			cmd.addParameter("MCH_CODE");

			cmd = trans.addCustomFunction( "GRPID","EQUIPMENT_OBJECT_API.Get_Group_Id","GROUPID" );
			cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
			cmd.addParameter("MCH_CODE");

			cmd = trans.addCustomFunction( "CRTLY","EQUIPMENT_FUNCTIONAL_API.Get_Criticality","CRITICALITY" );
			cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
			cmd.addParameter("MCH_CODE");

			trans = mgr.validate(trans);

			mchcon = trans.getValue("OBJMCHCON/DATA/MCH_CODE_CONTRACT");
			mch = trans.getValue("OBJDESC/DATA/MCH_CODE_DESCRIPTION");
			cur = trans.getValue("CURRPOS/DATA/LATEST_TRANSACTION");
			grp = trans.getValue("GRPID/DATA/GROUPID");
			crt = trans.getValue("CRTLY/DATA/CRITICALITY");
			txt = (mgr.isEmpty(mch) ? "" :mch) + "^" +
				  (mgr.isEmpty(cur) ? "" :cur)+ "^" +
				  (mgr.isEmpty(grp) ? "" :grp)+ "^" +
				  (mgr.isEmpty(crt) ? "" :crt)+ "^" +
				  (mgr.isEmpty(mchcon) ? "" :mchcon)+ "^";
			mgr.responseWrite(txt);
		}
		else if ("REPORTED_BY".equals(val))
		{
			String repid =""; 
			cmd = trans.addCustomFunction( "REPOID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID" );
			cmd.addParameter("COMPANY");
			cmd.addParameter("REPORTED_BY");

			trans = mgr.validate(trans);
			repid = trans.getValue("REPOID/DATA/REPORTED_BY_ID");
			mgr.responseWrite(repid);
		}
		else if ("PREPARED_BY".equals(val))
		{
			String preid = "";
			cmd = trans.addCustomFunction( "PREPID","Company_Emp_API.Get_Max_Employee_Id","PREPARED_BY_ID" );
			cmd.addParameter("COMPANY");
			cmd.addParameter("PREPARED_BY");

			trans = mgr.validate(trans);

			preid = trans.getValue("PREPID/DATA/PREPARED_BY_ID");
			mgr.responseWrite(preid);
		}
		else if ("WORK_MASTER_SIGN".equals(val))
		{
			String wmsid = "";
			cmd = trans.addCustomFunction( "WOMSID","Company_Emp_API.Get_Max_Employee_Id","WORK_MASTER_SIGN_ID" );
			cmd.addParameter("COMPANY");
			cmd.addParameter("WORK_MASTER_SIGN");

			trans = mgr.validate(trans);
			wmsid = trans.getValue("WOMSID/DATA/WORK_MASTER_SIGN_ID");
			mgr.responseWrite(wmsid);
		}
		else if ("WORK_LEADER_SIGN".equals(val))
		{
			String wlsid = ""; 
			cmd = trans.addCustomFunction( "WOLSID","Company_Emp_API.Get_Max_Employee_Id","WORK_LEADER_SIGN_ID" );
			cmd.addParameter("COMPANY");
			cmd.addParameter("WORK_LEADER_SIGN");

			trans = mgr.validate(trans);
			wlsid = trans.getValue("WOLSID/DATA/WORK_LEADER_SIGN_ID");
			mgr.responseWrite(wlsid);
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

	public void findTransfered()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNODATA: No data found."));
			headset.clear();
		}
		else
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT ); 
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		currrow = headset.getCurrentRowNo();
		q = trans.addQuery(headblk);

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNODATA: No data found."));
			headset.clear();
		}
		else if (headset.countRows() == 1)
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}

		headset.goTo(currrow);
		mgr.createSearchURL(headblk);
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		// q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND  CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		cmd = trans.addEmptyCommand("MAIN","ACTIVE_SEPARATE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("MAIN/DATA");
		headset.addRow(data);

		//newreq = "TRUE";
	}

	public void saveReturn()
	{
		ASPManager mgr = getASPManager();

		headset.changeRow();
		int currrowHead = headset.getCurrentRowNo();

		mgr.submit(trans);

		headset.goTo(currrowHead);
		headset.refreshRow();
	}

	public void saveNew()
	{
		saveReturn();
		newRow();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------



	private String createTransferUrl(String url, ASPBuffer object)
	{
		ASPManager mgr = getASPManager();
		try
		{
			String pkg = mgr.pack(object,1900-url.length());
			char sep = url.indexOf('?')>0 ? '&' : '?';
			urlString = url + sep + "__TRANSFER=" + pkg ;
			return urlString;
		}
		catch (Throwable any)
		{
			return null;
		}
	}

	public void none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNOSELECT: No RMB methods has been selected."));
	}

	/*  public void copy()
  {
	  ASPManager mgr = getASPManager();
	  ASPBuffer row=null;
	  ASPBuffer buff;

	  /*if (headlay.isMultirowLayout())
	  {
		  headset.goTo(headset.getRowSelected());
	  } *//*

	  if (headlay.isMultirowLayout())
		  headset.store();
	  else
	  {
		  headset.unselectRows();
		  headset.selectRow();
	  }

	  current_url = mgr.getURL();
	  ctx.setGlobal("CALLING_URL",current_url);
	  ctx.setGlobal("FORM_NAME","CopyWorkOrderDlg.page");

	  buff = mgr.newASPTransactionBuffer();
	  row = buff.addBuffer("0");
	  row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
	  row = buff.addBuffer("1");
	  row.addItem("FAULT_REP_FLAG",headset.getRow().getValue("FAULT_REP_FLAG"));
			  row = buff.addBuffer("2");
	  row.addItem("COMPANY",headset.getRow().getValue("COMPANY"));

	  //mgr.transferDataTo("CopyWorkOrderDlg.page",buff);

	  //Web Alignment - open tabs implemented as RMBs in a new window
	  bOpenNewWindow = true;
	  urlString = createTransferUrl("CopyWorkOrderDlg.page", buff);
	  newWinHandle = "copy";
	  //
  }*/

	public void confirm()
	{

		if (headlay.isMultirowLayout())
			headset.store();
		else
		{
			headset.unselectRows();
			headset.selectRow();
		}

		perform("CONFIRM__");   
	}   

	public void cancel()
	{

		if (headlay.isMultirowLayout())
			headset.store();
		else
		{
			headset.unselectRows();
			headset.selectRow();
		}
		perform("CANCEL__");
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("MAIN");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("OBJSTATE");
		f.setHidden();

		f = headblk.addField("OBJEVENTS");
		f.setHidden();

		f = headblk.addField("ACTIVITY_CONNECTED","Number");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERPROJOVWCONNECTION: Connection");
		f.setInsertable();
		f.setCheckBox("0,1");  

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERPROJOVWWONO: WO No");
		f.setReadOnly();
		f.setHilite();
		f.setMaxLength(8);

		f = headblk.addField("CONTRACT");
		f.setSize(12);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
		f.setLabel("PCMWWORKORDERPROJOVWCONTRACT: WO Site");
		f.setUpperCase();
		f.setHilite();
		f.setMaxLength(5);
		f.setReadOnly();

		f = headblk.addField("CONNECTION_TYPE");
		f.setSize(15);
		f.setSelectBox();                                  
		f.setLabel("PCMWWORKORDERPROJOVWCONNECTIONTYPE: Connection Type");
		f.enumerateValues("MAINT_CONNECTION_TYPE_API");
		f.setReadOnly();
		f.setInsertable();

		f = headblk.addField("MCH_CODE");
		f.setSize(12);
		f.setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT CONTRACT",600,445);
		// f.setCustomValidation("MCH_CODE","MCH_CODE_DESCRIPTION,LATEST_TRANSACTION,GROUPID,CRITICALITY,MCH_CODE_CONTRACT");
		f.setLabel("PCMWWORKORDERPROJOVWMCHCODE: Object ID");
		f.setUpperCase();
		f.setMaxLength(100);
		f.setReadOnly();

		f = headblk.addField("MCH_CODE_DESCRIPTION");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERPROJOVWMCHNAME: Object Description");
		f.setMaxLength(45);
		f.setDefaultNotVisible();

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setSize(12);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
		f.setLabel("PCMWWORKORDERPROJOVWMCHCODECONTRACT: Site");
		f.setUpperCase();
		f.setMaxLength(5);
		f.setReadOnly();

		f = headblk.addField("LATEST_TRANSACTION");
		f.setSize(25);
		f.setLabel("PCMWWORKORDERPROJOVWLATESTTRANSACTION: Latest Transaction");
		f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);

		f = headblk.addField("GROUPID");
		f.setSize(12);
		f.setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWGROUPID: Group ID");
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)");
		f.setReadOnly();
		f.setMaxLength(2000);
		f.setDefaultNotVisible();

		f = headblk.addField("CRITICALITY");
		f.setSize(25);
		f.setDynamicLOV("EQUIPMENT_CRITICALITY",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWCRITICALITY: Criticality");
		f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:MCH_CODE_CONTRACT,:MCH_CODE)");
		f.setReadOnly();
		f.setMaxLength(10);
		f.setDefaultNotVisible();

		f = headblk.addField("ORG_CODE");
		f.setSize(10);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERPROJOVWORGCODE: Maintenance Organization");
		f.setUpperCase();
		f.setMaxLength(8);
		f.setReadOnly();

		f = headblk.addField("ERR_DESCR");
		f.setSize(50);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERPROJOVWERRDESCR: Directive");
		f.setMaxLength(60);
		f.setReadOnly();

		f = headblk.addField("CLIENT_VALUES");
		f.setSize(14);
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("STATE");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERPROJOVWSTATE: Status"); 
		f.setReadOnly();
		f.setMaxLength(253);
		f.setHilite();

		f = headblk.addField("PLAN_S_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWPLANSDATE: Planned Start");
		f.setReadOnly();

		f = headblk.addField("PLAN_F_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWPLANFDATE: Planned Completion");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("PLAN_HRS","Number");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWPLANHRS: Execution Time");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("REQUIRED_START_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWREALFDATE: Required Start");
		f.setReadOnly();

		f = headblk.addField("REQUIRED_END_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWREQUIREDENDDATE: Latest Completion");
		f.setReadOnly();

		f = headblk.addField("REAL_F_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWPCMWACTIVESEPARATEOVWREALFDATE: Actual Completion");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("CALL_CODE");
		f.setSize(9);
		f.setDynamicLOV("MAINTENANCE_EVENT",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWCALLCODE: Event");
		f.setUpperCase();
		f.setMaxLength(10);
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("OP_STATUS_ID");
		f.setSize(20);
		f.setDynamicLOV("OPERATIONAL_STATUS",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWOPSTATUSID: Operational Status");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("PRIORITY_ID");
		f.setSize(5);
		f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWPRIORITYID: Priority");
		f.setHyperlink("../pcmw/MaintenancePriorityOvw.page","PRIORITY_ID","NEWWIN");
		f.setUpperCase();
		f.setMaxLength(1);
		f.setReadOnly();

		/* f = headblk.addField("PRIORITYDESC");
	 f.setSize(25);
	 f.setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)");
	 f.setReadOnly();
	 //f.setDefaultNotVisible();
	 f.setMaxLength(2000);
	 mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESC");*/

		f = headblk.addField("WORK_TYPE_ID");
		f.setSize(12);
		f.setDynamicLOV("WORK_TYPE",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWWORKTYPEID: Work Type");
		f.setUpperCase();
		f.setHyperlink("../pcmw/WorkType.page","WORK_TYPE_ID","NEWWIN");
		f.setMaxLength(20);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("WORKTYPEDESC");
		f.setSize(25);
		f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
		f.setLabel("PCMWWORKORDERPROJOVWWWORKTYPEID: Work Description");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);
		mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESC");

		f = headblk.addField("VENDOR_NO");
		f.setSize(12);
		f.setDynamicLOV("SUPPLIER_INFO",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWVENDORNO: Contractor");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("VENDORNAME");
		f.setSize(18);
		f.setLabel("PCMWWORKORDERPROJOVWVENDORNAME: Contractor Name");
		f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
		mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("PM_TYPE");
		f.setSize(12);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWPMTYPE: PM Type");
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("REPORTED_BY_ID");
		f.setSize(14);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWREPORTEDBY2: Reported By ID");
		f.setMaxLength(11);
		f.setDefaultNotVisible();

		f = headblk.addField("REPORTED_BY");
		f.setSize(14);
		f.setDynamicLOV("EMPLOYEE_LOV",600,445);
		f.setMandatory();
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERPROJOVWREPORTEDBY1: Reported By");
		f.setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setHilite();

		f = headblk.addField("REPAIR_FLAG");
		f.setSize(22);
		f.setLabel("PCMWWORKORDERPROJOVWREPAIRFLAG: Repair Work Order");
		f.setCheckBox("FALSE,TRUE");
		f.setReadOnly();
		f.setMaxLength(5);
		f.setDefaultNotVisible();

		f = headblk.addField("PREPARED_BY");
		f.setSize(14);
		f.setDynamicLOV("EMPLOYEE_LOV",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWPREPAREDBY: Prepared By");
		f.setCustomValidation("COMPANY,PREPARED_BY","PREPARED_BY_ID");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("PREPARED_BY_ID");
		f.setSize(14);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWREPORTEDBY3: Prepared By ID");
		f.setMaxLength(11);
		f.setDefaultNotVisible();

		f = headblk.addField("WORK_MASTER_SIGN");
		f.setSize(14);
		f.setDynamicLOV("EMPLOYEE_LOV",600,445);
		f.setLOVProperty("TITLE","PCMWWORKORDERPROJOVWLOVTITLE4: List of Executed By");
		f.setLabel("PCMWWORKORDERPROJOVWWORKMASTERSGN: Executed By");
		f.setCustomValidation("COMPANY,WORK_MASTER_SIGN","WORK_MASTER_SIGN_ID");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("WORK_MASTER_SIGN_ID");
		f.setSize(14);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWREPORTEDBY4: Executed By ID");
		f.setMaxLength(11);

		f = headblk.addField("WORK_LEADER_SIGN");
		f.setSize(15);
		f.setDynamicLOV("EMPLOYEE_LOV",600,445);
		f.setLOVProperty("TITLE","PCMWWORKORDERPROJOVWLOVTITLE2: List of Work Leader");
		f.setLabel("PCMWWORKORDERPROJOVWWORKLEADERSIGN: Work Leader");
		f.setCustomValidation("COMPANY,WORK_LEADER_SIGN","WORK_LEADER_SIGN_ID");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("WORK_LEADER_SIGN_ID");
		f.setSize(14);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWREPORTEDBY5: Reported By");
		f.setMaxLength(11);

		f = headblk.addField("AUTHORIZE_CODE");
		f.setSize(14);
		f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWAUTHORIZECODE: Coordinator");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("COMPANY");
		f.setSize(10);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWCOMPANY: Company");
		f.setUpperCase();

		f = headblk.addField("COST_CENTER");
		f.setSize(14);
		f.setLabel("PCMWWORKORDERPROJOVWCOSTCENTER: Cost Center");
		f.setMaxLength(10);
		f.setReadOnly();
		f.setDefaultNotVisible();

                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ")) 
                {
                    f = headblk.addField("PROGRAM_ID");
                    f.setSize(14);
                    f.setFunction("PROJECT_API.Get_Program_Id(:PROJECT_NO)");
                    f.setLabel("PCMWWORKORDERPROJOVWPROGRAMID: Program ID");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROGRAM_DESC");
                    f.setSize(14);
                    f.setReadOnly();
                    f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:PROJECT_NO),PROJECT_API.Get_Program_Id(:PROJECT_NO))");
                    f.setLabel("PCMWWORKORDERPROJOVWPROGRAMDESC: Program Description");
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROJECT_NO");
                    f.setSize(14);
                    f.setReadOnly();
                    f.setLabel("PCMWWORKORDERPROJOVWPROJECTNO: Project ID");
                    f.setMaxLength(10);
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROJECT_DESC");
                    f.setSize(14);
                    f.setLabel("PCMWWORKORDERPROJOVWPROJECTDESC: Project Description");
                    f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_SEQ");
                    f.setSize(14);
                    f.setHidden();
    
                    f = headblk.addField("SUB_PROJECT_ID");
                    f.setSize(14);
                    f.setLabel("PCMWWORKORDERPROJOVWSUBPROJECTID: Sub Project ID");
                    f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("SUB_PROJECT_DESC");
                    f.setSize(20);
                    f.setLabel("PCMWWORKORDERPROJOVWSUBPROJECTDESC: Sub Project Description");
                    f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_ID");
                    f.setSize(14);
                    f.setReadOnly();
                    f.setLabel("PCMWWORKORDERPROJOVWACTIVITYID: Activity ID");
                    f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_DESC");
                    f.setSize(20);
                    f.setLabel("PCMWWORKORDERPROJOVWACTIVITYDESC: Activity Description");
                    f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
                }
                //Bug 66456, End

		f = headblk.addField("OBJECT_NO");
		f.setSize(14);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERPROJOVWOBJECTNO: Object no.");
		f.setMaxLength(10);
		f.setDefaultNotVisible();

		f = headblk.addField("REG_DATE","Datetime");
		f.setSize(12);
		f.setMaxLength(20);
		f.setLabel("PCMWWORKORDERPROJOVWREGDATE: Registration Date");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("LAST_ACTIVITY_DATE","Datetime");
		f.setSize(14);
		f.setLabel("PCMWWORKORDERPROJOVWLASTACTIVITYDATE: Last Updated");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("TEST_POINT_ID");
		f.setSize(12);
		f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","MCH_CODE,MCH_CODE_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWTESTPOINTID: Testpoint");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(6);
		f.setDefaultNotVisible();

		f = headblk.addField("PM_NO","Number");
		f.setSize(10);
		f.setLabel("PCMWWORKORDERPROJOVWPMNO: PM NO");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("PM_REVISION");
		f.setSize(10);
		f.setLabel("PCMWWORKORDERPROJOVWPMREVISION: PM Revision");
		f.setReadOnly();
		f.setMaxLength(6);
		f.setDefaultNotVisible();

		f = headblk.addField("QUOTATION_ID","Number");
		f.setSize(16);
		f.setDynamicLOV("WORK_ORDER_QUOTATION_LOV",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWQUOTATIONID: Quotation ID");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("SALESMANCODE");
		f.setSize(16);
		f.setDynamicLOV("PERSON_INFO",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWSALESMANCODE: Salesman Code");
		f.setFunction("WORK_ORDER_QUOTATION_API.Get_Salesman_Code(:QUOTATION_ID)");
		f.setReadOnly();
		f.setMaxLength(5);
		f.setDefaultNotVisible();

		f = headblk.addField("CUSTOMER_NO");
		f.setSize(14);
		f.setDynamicLOV("CUSTOMER_INFO",600,445);
		f.setLabel("PCMWWORKORDERPROJOVWCUSTOMERNO: Customer No");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);

		f = headblk.addField("CUSTOMERNAME");
		f.setSize(40);
		f.setLabel("PCMWWORKORDERPROJOVWCUSTOMERNAME: Customer Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("CONTACT");
		f.setSize(12);
		f.setLabel("PCMWPCMWWORKORDERPROJOVWCONTACT: Contact");
		f.setReadOnly();
		f.setMaxLength(30);
		f.setDefaultNotVisible();

		f = headblk.addField("REFERENCE_NO");
		f.setSize(12);
		f.setLabel("PCMWPCMWWORKORDERPROJOVWREFERENCENO: Reference No.");
		f.setReadOnly();
		f.setMaxLength(25);
		f.setDefaultNotVisible();

		f = headblk.addField("PHONE_NO");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERPROJOVWPHONENO: Phone No.");
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("FIXED_PRICE");
		f.setSize(13);
		f.setLabel("PCMWWORKORDERPROJOVWFIXEDPRICE: Fixed Price");
		f.setSelectBox();
		f.enumerateValues("FIXED_PRICE_API");
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("ERR_DISCOVER_CODE");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERPROJOVWDISCOVERY: Discovery");
		f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,445);
		f.setMaxLength(3);
		f.setDefaultNotVisible();

		f = headblk.addField("ERR_SYMPTOM");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERPROJOVWERRSYMPTOM: Symptom");
		f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,445);
		f.setMaxLength(3);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS1");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS1: Address1");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS2");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS2: Address2");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS3");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS3: Address3");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS4");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS4: Address4");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS5");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS5: Address5");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS6");
		f.setSize(35);
		f.setLabel("PCMWWORKORDERPROJOVWADDRESS6: Address6");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS_ID");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWADDRESSID: Address Id");
		f.setUpperCase();
		f.setMaxLength(50);
		f.setDefaultNotVisible();

		f = headblk.addField("FAULT_REP_FLAG","Number");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWWORKORDERPROJOVWFAULTREPFLAG: Fault Report Flag");

		headblk.setView("ACTIVE_SEPARATE");
		headblk.defineCommand("ACTIVE_SEPARATE_API","Modify__");//,CONFIRM__,CANCEL__
		headblk.disableDocMan();
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERPROJOVWACTIVEWOS: Active Work Orders"));
		headtbl.setWrap();

		//Web Alignment - Multirow Action
		headtbl.enableRowSelect();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);

		headbar.enableMultirowAction();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);

		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.NEWROW);

		headlay.defineGroup("","ACTIVITY_CONNECTED,WO_NO,ERR_DESCR,CONTRACT,REPORTED_BY,REG_DATE,STATE,CALL_CODE,OP_STATUS_ID,PM_TYPE,PREPARED_BY,VENDOR_NO,VENDORNAME,QUOTATION_ID,SALESMANCODE,FIXED_PRICE",false,true);   
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))   
                    headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWPROJINFO: Project Infomation"),"PROGRAM_ID,PROGRAM_DESC,PROJECT_NO,PROJECT_DESC,SUB_PROJECT_ID,SUB_PROJECT_DESC,ACTIVITY_ID,ACTIVITY_DESC",true,false);
                //Bug 66456, End
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWPREPINFO: Prepare Info  "),"WORK_TYPE_ID,WORKTYPEDESC,WORK_MASTER_SIGN,WORK_LEADER_SIGN,AUTHORIZE_CODE",true,false);
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWCONTINFO: Contact Information"),"CONTACT,PHONE_NO,REFERENCE_NO",true,false);
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWCUSTOMER: Customer"),"CUSTOMER_NO,CUSTOMERNAME",true,false);
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWOBJECT: Object"),"CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,LATEST_TRANSACTION,GROUPID,CRITICALITY,ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6",true,false);
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWPLANINFO: Planning Information"),"ORG_CODE,ORGCODEDESCR,PRIORITY_ID",true,false);//PRIORITYDESC
		headlay.defineGroup(mgr.translate("PCMWWORKORDERPROJOVWPLANSCHEDULE: Planning Schedule"),"REQUIRED_START_DATE,REQUIRED_END_DATE,PLAN_S_DATE,PLAN_F_DATE,REAL_F_DATE,PLAN_HRS,LAST_ACTIVITY_DATE",true,false);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL5: PM Information"),"PM_NO,PM_REVISION,TEST_POINT_ID,OBJECT_NO,COST_CENTER,ERR_DISCOVER_CODE,ERR_SYMPTOM",true,false);

		headlay.setSimple("CUSTOMERNAME");
	}

	public void checkObjAvaileble()
	{
		ASPManager mgr = getASPManager();

		ASPBuffer availObj;
		trans.clear();
		trans.addSecurityQuery("CUSTOMER_AGREEMENT,ACTIVE_SEP_WO_PRINT_REP");
		trans.addSecurityQuery("Active_Work_Order_API","Copy__");
		trans.addPresentationObjectQuery("PCMW/CopyWorkOrderDlg.page,PCMW/ActiveSeparate2.page");

		trans = mgr.perform(trans);

		availObj = trans.getSecurityInfo();

		if (availObj.itemExists("CUSTOMER_AGREEMENT"))
			printServO_ = true;

		if (availObj.itemExists("ACTIVE_SEP_WO_PRINT_REP"))

			printWO_ = true;

		if (availObj.itemExists("Active_Work_Order_API.Copy__") && availObj.namedItemExists("PCMW/CopyWorkOrderDlg.page"))
			copy_= true;

		if (availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
			prepareWo_= true;
	}

	public void DissableRmbs()
	{
		ASPManager mgr = getASPManager();

		if (!printServO_)
			headbar.removeCustomCommand("printServO");
		if (!printWO_)
			headbar.removeCustomCommand("printWO");

		// if (!copy_)
		// headbar.removeCustomCommand("copy");
		if (!prepareWo_)
			headbar.removeCustomCommand("prepareWo");

	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERPROJOVWTITLE: Overview - Active Work Orders - Project";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERPROJOVWTITLE: Overview - Active Work Orders - Project";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
			appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(urlString);
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
		}
	}
}
