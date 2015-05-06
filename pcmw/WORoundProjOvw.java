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
*  File        : WORoundProjOvw.java 
*  Created     : VAGULK  040707  Created.
*  Modified    :
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  ARWILK  041112  Replaced getContents with printContents.
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  NIJALK  080202  Bug 66456, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WORoundProjOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WORoundProjOvw");

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
	private int currrow;
	private ASPCommand cmd;
	private ASPQuery q;
	private ASPBuffer data;
	//============ Variables for Security check for RMBs ===============
	private boolean again;
	private boolean printServO_;
	private boolean printWO_;
	private boolean copy_;
	private boolean prepareWo_;

	//Web Alignment - replace Links with RMBs
	private String urlString;
	private String performRMB;
	private String URLString;
	private String WindowName;

	//===============================================================
	// Construction 
	//===============================================================
	public WORoundProjOvw(ASPManager mgr, String page_path)
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
			mgr.showAlert(mgr.translate("PCMWWOROUNDPROJOVWNODATA: No data found."));
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
		q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWOROUNDPROJOVWNODATA: No data found."));
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
		q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND  CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
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
		//data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");
		headset.addRow(data);
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

		mgr.showAlert(mgr.translate("PCMWWOROUNDPROJOVWNOSELECT: No RMB methods has been selected."));
	}

	public void reportInRouteWO()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		URLString = "ActiveRound.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+
					"&CONTRACT="+mgr.URLEncode(headset.getValue("CONTRACT"));

		performRMB   = "TRUE";
		WindowName   = "REPORTINROUTEWO";       
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
		f.setLabel("PCMWWOROUNDPROJOVWCONNECTION: Connection");
		f.setInsertable();
		f.setCheckBox("0,1");  

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(12);
		f.setLabel("PCMWWOROUNDPROJOVWWONO: WO No");
		f.setReadOnly();
		f.setMaxLength(8);
		f.setHilite();

		f = headblk.addField("ROUNDDEF_ID");
		f.setSize(21);
		f.setDynamicLOV("PM_ROUND_DEFINITION",600,450);
		f.setLOVProperty("TITLE",mgr.translate("PCMWWOROUNDPROJOVWROUID: List of Route ID"));
		f.setReadOnly();
		f.setMaxLength(8);
		f.setHilite();
		f.setLabel("PCMWWOROUNDPROJOVWROUNDDEF_ID: Route ID");
		f.setUpperCase();

		f = headblk.addField("ROUNDDEFDESCRIPTION");
		f.setSize(21);
		f.setReadOnly();
		f.setHilite();
		f.setLabel("PCMWWOROUNDPROJOVWROUNDDEFDESCRIPTION: Description");
		f.setFunction("Pm_Round_Definition_API.Get_Description(:ROUNDDEF_ID)");
		mgr.getASPField("ROUNDDEF_ID").setValidation("ROUNDDEFDESCRIPTION");

		f = headblk.addField("CONTRACT");
		f.setSize(12);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
		f.setLabel("PCMWPCMWWOROUNDPROJOVWCONTRACT: Site");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(5);

		f = headblk.addField("ORG_CODE");
		f.setSize(10);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setMandatory();
		f.setLabel("PCMWWOROUNDPROJOVWORGCODE: Maintenance Organization");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(8);

		f = headblk.addField("PLAN_S_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWPLANSDATE: Planned Start");
		f.setReadOnly();

		f = headblk.addField("REAL_S_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWREALSDATE: Actual Start");
		f.setReadOnly();

		f = headblk.addField("PLAN_F_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWPLANFDATE: Planned Completion");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("REAL_F_DATE","Datetime");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWREALFDATE: Actual Completion");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("PLAN_HRS","Number");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWPLANHRS: Planned Hours");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("COMPANY");
		f.setSize(10);
		f.setHidden();
		f.setLabel("PCMWPCMWWOROUNDPROJOVWCOMPANY: Company");
		f.setUpperCase();

		f = headblk.addField("REPORTED_BY_ID");
		f.setSize(14);
		f.setHidden();
		f.setLabel("PCMWPCMWWOROUNDPROJOVWREPORTEDBY2: Reported By ID");
		f.setMaxLength(11);
		f.setDefaultNotVisible();

		f = headblk.addField("REPORTED_BY");
		f.setSize(14);
		f.setDynamicLOV("EMPLOYEE_LOV",600,445);
		f.setMandatory();
		f.setLabel("PCMWWOROUNDPROJOVWREPORTEDBY1: Reported By");
		f.setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setHilite();

		f = headblk.addField("STATE");
		f.setSize(12);
		f.setLabel("PCMWWOROUNDPROJOVWSTATE: Status"); 
		f.setReadOnly();
		f.setMaxLength(253);
		f.setHilite();

                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    f = headblk.addField("PROGRAM_ID");
                    f.setSize(10);
                    f.setFunction("PROJECT_API.Get_Program_Id(:PROJECT_NO)");
                    f.setLabel("PCMWWOROUNDPROJOVWPROGRAMID: Program ID");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROGRAM_DESC");
                    f.setSize(10);
                    f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:PROJECT_NO),PROJECT_API.Get_Program_Id(:PROJECT_NO))");
                    f.setLabel("PCMWWOROUNDPROJOVWPROGRAMDESC: Program Description");
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROJECT_NO");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWPROJECTNO: Project ID");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("PROJECT_DESC");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWPROJECTDESC: Project Description");
                    f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_SEQ");
                    f.setSize(10);
                    f.setHidden();
    
                    f = headblk.addField("SUB_PROJECT_ID");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWSUBPROJECTID: Sub Project ID");
                    f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("SUB_PROJECT_DESC");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWSUBPROJECTDESC: Sub Project Description");
                    f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_ID");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWACTIVITYID: Activity ID");
                    f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
    
                    f = headblk.addField("ACTIVITY_DESC");
                    f.setSize(10);
                    f.setLabel("PCMWWOROUNDPROJOVWACTIVITYDESC: Activity Description");
                    f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
                    f.setMaxLength(10);
                    f.setReadOnly();
                    f.setDefaultNotVisible();
                }
                //Bug 66456, End

		f = headblk.addField("CALL_CODE");
		f.setSize(9);
		f.setDynamicLOV("MAINTENANCE_EVENT",600,445);
		f.setLabel("PCMWWOROUNDPROJOVWCALLCODE: Event");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(10);
		f.setDefaultNotVisible();

		f = headblk.addField("ROLE_CODE");
		f.setSize(8);
		f.setReadOnly();
		f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,450);
		f.setUpperCase();
		f.setLOVProperty("TITLE",mgr.translate("PCMWWOROUNDPROJOVWROCO: List of Craft")); 
		f.setLabel("PCMWWOROUNDPROJOVWROLE_CODE: Craft"); 

		f = headblk.addField("VENDOR_NO");
		f.setSize(12);
		f.setDynamicLOV("SUPPLIER_INFO",600,445);
		f.setLabel("PCMWWOROUNDPROJOVWVENDORNO: Contractor");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("VENDORNAME");
		f.setSize(18);
		f.setLabel("PCMWWOROUNDPROJOVWVENDORNAME: Contractor Name");
		f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
		mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("CUSTOMER_NO");
		f.setSize(14);
		f.setDynamicLOV("CUSTOMER_INFO",600,445);
		f.setLabel("PCMWWOROUNDPROJOVWCUSTOMERNO: Customer No");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(20);

		f = headblk.addField("CUSTOMERNAME");
		f.setSize(40);
		f.setLabel("PCMWWOROUNDPROJOVWCUSTOMERNAME: Customer Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("CONTACT");
		f.setSize(12);
		f.setLabel("PCMWPCMWWOROUNDPROJOVWCONTACT: Contact");
		f.setReadOnly();
		f.setMaxLength(30);
		f.setDefaultNotVisible();

		f = headblk.addField("REFERENCE_NO");
		f.setSize(12);
		f.setLabel("PCMWPCMWWOROUNDPROJOVWREFERENCENO: Reference No.");
		f.setReadOnly();
		f.setMaxLength(25);
		f.setDefaultNotVisible();

		f = headblk.addField("PHONE_NO");
		f.setSize(12);
		f.setLabel("PCMWPCMWWOROUNDPROJOVWPHONENO: Phone No.");
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("FIXED_PRICE");
		f.setSize(13);
		f.setLabel("PCMWPCMWWOROUNDPROJOVWFIXEDPRICE: Fixed Price");
		f.setSelectBox();
		f.enumerateValues("FIXED_PRICE_API");
		f.setReadOnly();
		f.setMaxLength(20);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS1");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS1: Address1");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(100);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS2");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS2: Address2");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS3");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS3: Address3");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS4");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS4: Address4");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS5");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS5: Address5");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS6");
		f.setSize(35);
		f.setLabel("PCMWWOROUNDPROJOVWADDRESS6: Address6");
		f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)");
		f.setReadOnly();
		f.setMaxLength(35);
		f.setDefaultNotVisible();

		f = headblk.addField("ADDRESS_ID");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWWOROUNDPROJOVWADDRESSID: Address Id");
		f.setUpperCase();
		f.setMaxLength(50);
		f.setDefaultNotVisible();

		headblk.setView("ACTIVE_ROUND");
		headblk.defineCommand("ACTIVE_ROUND_API","Modify__");
		headblk.disableDocMan();
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWOROUNDPROJOVWHD: Active Routes"));
		headtbl.setWrap();

		headtbl.enableRowSelect();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);

		headbar.addCustomCommand("reportInRouteWO",mgr.translate("PCMWWOROUNDPROJOVW: Report in Route Work Order..."));

		headbar.enableMultirowAction();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);

		headlay.defineGroup("","ACTIVITY_CONNECTED,WO_NO,ROUNDDEF_ID,ROUNDDEFDESCRIPTION,CONTRACT,REPORTED_BY,CALL_CODE,ROLE_CODE,VENDOR_NO,VENDORNAME,FIXED_PRICE,STATE",false,true);   
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))   
                    headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWPROJINFO: Project Infomation"),"PROGRAM_ID,PROGRAM_DESC,PROJECT_NO,PROJECT_DESC,SUB_PROJECT_ID,SUB_PROJECT_DESC,ACTIVITY_ID,ACTIVITY_DESC",true,false);
                //Bug 66456, End
		headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWCONTINFO: Contact Information"),"CONTACT,PHONE_NO,REFERENCE_NO",true,false);
		headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWCUSTOMER: Customer"),"CUSTOMER_NO,CUSTOMERNAME",true,false);
		headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWOBJECT: Object"),"ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6",true,false);
		headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWPLANINFO: Planning Information"),"ORG_CODE",true,false);
		headlay.defineGroup(mgr.translate("PCMWWOROUNDPROJOVWPLANSCHEDULE: Planning Schedule"),"PLAN_S_DATE,PLAN_F_DATE,REAL_S_DATE,REAL_F_DATE,PLAN_HRS",true,false);
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
			copy_ = true;

		if (availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
			prepareWo_ = true;

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
		return "PCMWWOROUNDPROJOVWTITLE: Overview - Active Routes";
	}

	protected String getTitle()
	{
		return "PCMWWOROUNDPROJOVWTITLE: Overview - Active Routes";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
			appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(performRMB);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  url_to_go = '");
		appendDirtyJavaScript(URLString);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window_name = '");
		appendDirtyJavaScript(WindowName);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
		appendDirtyJavaScript("}\n");
	}
}
