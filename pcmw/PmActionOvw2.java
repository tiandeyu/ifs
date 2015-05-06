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
*  File        : PmActionOvw2.java 
*  Created     : SHFELK  010329  Created 
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040603  Added PM_REVISION key filed under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040616  Removed STD_JOB_ID under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040722  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  BUNILK  070524  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PmActionOvw2 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmActionOvw2");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private boolean MyFlag;
	private String CaSe;
	private String url;
	private String findFlag;
	private String rmbEventGenFlag;
	private String nPmNo;
	private String CallCode;
	private ASPCommand cmd;
	private ASPBuffer r;
	private ASPBuffer b0;
	private String val;
	private String descrip;
	private ASPQuery q;
	private String n;
	private String org_code; 
	private String call_code; 
	private String txt;  

	//===============================================================
	// Construction 
	//===============================================================
	public PmActionOvw2(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();

		nPmNo = ctx.readValue("NPMNO","");
		MyFlag = ctx.readFlag("MULTIROW",true);
		findFlag = ctx.readValue("FINDFLAG","");
		rmbEventGenFlag = ctx.readValue("RMBEVENTGENFLAG","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			MyFlag = true;
			okFind();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CALL_CODE")))
		{
			CallCode = mgr.readValue("CALL_CODE");
			if (mgr.isEmpty(CallCode))
				CallCode = "";
			CaSe = mgr.readValue("CALL_SEQUENCE");
			if (mgr.isEmpty(CaSe))
				CaSe = "";
			MyFlag = false;
			okFind();
		}
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void startup()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addCustomCommand("ORG_CODE", "ORGANIZATION_API.Enumerate");
		cmd.addParameter("CLIENT_VALUES1");
		cmd = trans.addCustomCommand("CALL_CODE", "MAINTENANCE_EVENT_API.Enumerate");
		cmd.addParameter("CLIENT_VALUES6");
		trans = mgr.submit(trans);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("CONTRACT".equals(val))
		{
			cmd = trans.addCustomFunction("DESC", "Maintenance_Object_API.Get_Mch_Name","MCHNAME");
			cmd.addParameter("CONTRACT");
			cmd.addParameter("MCH_CODE");

			trans = mgr.validate(trans);

			descrip = trans.getValue("DESC/DATA/MCHNAME");

			txt = (mgr.isEmpty(descrip ) ? "" :descrip ) + "^" ;

			mgr.responseWrite(txt);
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		n = headset.getRow().getValue("N");
		headlay.setCountValue(toInt(n));
		headset.clear();
	}

	public void find()
	{
		findFlag = "TRUE";
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		//Bug 58216 Start
		q.addWhereCondition("( ? IS NULL OR ( ? >= nvl(start_call,1) AND  MOD( ? - nvl(start_call,1), nvl(call_interval,1) ) = 0 )) AND CALL_CODE = ?");
		q.addParameter("CALL_CODE",CaSe);
		q.addParameter("CALL_CODE",CaSe);
		q.addParameter("CALL_CODE",CaSe);
		q.addParameter("CALL_CODE",CallCode);
		//bug 58216 End
		q.setOrderByClause("MCH_CODE,PM_NO");
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
			mgr.showAlert(mgr.translate("PCMWPMACTIONOVW2NODATA: No data found."));
	}

//-----------------------------------------------------------------------------
//----------------------------  RMB FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

	public void none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("PCMWPMACTIONOVW2NORMB: No RMB method has been selected."));
	}

	public void eventGeneration()
	{
                ASPManager mgr = getASPManager();
		nPmNo = mgr.URLEncode(headset.getRow().getValue("PM_NO"));
		rmbEventGenFlag ="TRUE";
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PM_NO","Number", "#");
		f.setSize(7);
		f.setLabel("PCMWPMACTIONOVW2PMNO: PM No");
		f.setReadOnly();

		f = headblk.addField("PM_REVISION");
		f.setSize(7);
		f.setLabel("PCMWPMACTIONOVW2PMREV: PM Revision");
		f.setReadOnly();

		f = headblk.addField("PM_TYPE");
		f.setSize(10);
		f.setLabel("PCMWPMACTIONOVW2PMTYPE: Type");
		f.setSelectBox();
		f.enumerateValues("PM_TYPE_API");
		f.setReadOnly();

		f = headblk.addField("MCH_CODE");
		f.setSize(17);
		f.setMaxLength(100);
		f.setLabel("PCMWPMACTIONOVW2MCHCODE: Object ID");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("MCHNAME");
		f.setSize(16);
		f.setLabel("PCMWPMACTIONOVW2MCHNAME: Description");
		f.setFunction("Maintenance_Object_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
		f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setLOV("../mpccow/UserAllowedSiteLovLov.page",600,445);
		f.setCustomValidation("CONTRACT,MCH_CODE","MCHNAME");
		f.setMandatory();
		f.setLabel("PCMWPMACTIONOVW2CONT: Site");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("COMPANY");
		f.setSize(8);
		f.setMandatory();
		f.setHidden();
		f.setLabel("PCMWPMACTIONOVW2COMPANY: Company");
		f.setUpperCase();

		f = headblk.addField("TEST_POINT_ID");
		f.setSize(8);
		f.setLOV("../equipw/EquipmentObjectTestPntLov.page","CONTRACT,  MCH_CODE",600,445);
		f.setLabel("PCMWPMACTIONOVW2TPID: Testpoint");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("MSEQOBJECTTESTPOINTDESCRIPTIO");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2MSEQOBJTPDESC: Description");
		f.setFunction("Equipment_Object_Test_Pnt_API.Get_Description(:CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
		mgr.getASPField("TEST_POINT_ID").setValidation("MSEQOBJECTTESTPOINTDESCRIPTIO");
		f.setHidden();

		f = headblk.addField("MSEQOBJECTTESTPOINTLOCATION");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2MSEQOBJTPLOC: Location");
		f.setFunction("Equipment_Object_Test_Pnt_API.Get_Location(:CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
		mgr.getASPField("TEST_POINT_ID").setValidation("MSEQOBJECTTESTPOINTLOCATION");
		f.setHidden();

		f = headblk.addField("ACTION_CODE_ID");
		f.setSize(9);
		f.setLabel("PCMWPMACTIONOVW2ACID: Action");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ACTIONDESCR");
		f.setSize(13);
		f.setLabel("PCMWPMACTIONOVW2ACTDESCR: Description");
		f.setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)");
		mgr.getASPField("ACTION_CODE_ID").setValidation("ACTIONDESCR");
		f.setReadOnly();

		f = headblk.addField("SIGNATURE");
		f.setSize(9);
		f.setLOV("EmployeeLovLov.page","COMPANY",600,445);
		f.setLabel("PCMWPMACTIONOVW2SIGN: Signature");
		f.setUpperCase();
		f.setHidden();

		f = headblk.addField("SIGNATURE_ID");
		f.setSize(6);
		f.setHidden();
		f.setLabel("PCMWPMACTIONOVW2SIGID: Signature");
		f.setUpperCase();

		f = headblk.addField("OP_STATUS_ID");
		f.setSize(6);
		f.setLabel("PCMWPMACTIONOVW2OPSID: Operational Status");
		f.setUpperCase();
		f.setHidden();  

		f = headblk.addField("ORG_CODE");
		f.setSize(12);
		f.setLabel("PCMWPMACTIONOVW2ORGCODE: Maintenance Organization");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("PRIORITY_ID");
		f.setSize(5);
		f.setLabel("PCMWPMACTIONOVW2PRIOID: Priority");
		f.setUpperCase();
		f.setHidden();

		f = headblk.addField("WORK_TYPE_ID");
		f.setSize(20);
		f.setLabel("PCMWPMACTIONOVW2WTID: Work Type");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("PM_GENERATEABLE");
		f.setSize(12);
		f.setLabel("PCMWPMACTIONOVW2PMGEN: Generateable");
		f.setSelectBox();
		f.enumerateValues("PM_GENERATEABLE_API");
		f.setReadOnly();

		f = headblk.addField("PM_PERFORMED_DATE_BASED");
		f.setSize(20);
		f.setLabel("PCMWPMACTIONOVW2PMPERDATBASED: Perf. Date Based");
		f.setSelectBox();
		f.enumerateValues("PM_PERFORMED_DATE_BASED_API");
		f.setReadOnly();

		f = headblk.addField("START_VALUE");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2SVALUE: Start Value");
		f.setUpperCase();
		f.setHidden();

		f = headblk.addField("PM_START_UNIT");
		f.setSize(7);
		f.setLabel("PCMWPMACTIONOVW2PMSUNIT: Start Unit");
		f.setHidden();
		f.setSelectBox();
		f.enumerateValues("PM_START_UNIT_API");

		f = headblk.addField("INTERVAL");
		f.setSize(6);
		f.setLabel("PCMWPMACTIONOVW2INT: Interval");
		f.setHidden();

		f = headblk.addField("PM_INTERVAL_UNIT");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2PMINTUNIT: Interval Unit");
		f.setHidden();
		f.setSelectBox();
		f.enumerateValues("PM_INTERVAL_UNIT_API");

		f = headblk.addField("CALL_CODE");
		f.setSize(7);
		f.setLabel("PCMWPMACTIONOVW2CALLCODE: Event");
		f.setUpperCase();
		f.setHidden();

		f = headblk.addField("START_CALL","Number");
		f.setSize(7);
		f.setLabel("PCMWPMACTIONOVW2SCALL: Start Event");
		f.setHidden();

		f = headblk.addField("CALL_INTERVAL","Number");
		f.setSize(9);
		f.setLabel("PCMWPMACTIONOVW2CALLINT: Event Interval");
		f.setHidden();

		f = headblk.addField("DESCRIPTION");
		f.setSize(17);
		f.setLabel("PCMWPMACTIONOVW2DESCR: Work Description");
		f.setHidden();

		f = headblk.addField("VENDOR_NO");
		f.setSize(12);
		f.setLabel("PCMWPMACTIONOVW2VENNO: Contractor");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("VENDORNAME");
		f.setSize(10);
		f.setLabel("PCMWPMACTIONOVW2VENNAME: Contractor Name");
		f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
		mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
		f.setHidden();

		f = headblk.addField("MATERIALS");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2MAT: Material");
		f.setHidden();

		f = headblk.addField("TESTNUMBER");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2TESTNO: Test Number");
		f.setHidden();

		f = headblk.addField("ALTERNATE_DESIGNATION");
		f.setSize(14);
		f.setLabel("PCMWPMACTIONOVW2ALTDESIG: Alternate Designation");
		f.setHidden();

		f = headblk.addField("LAST_CHANGED","Datetime");
		f.setSize(8);
		f.setLabel("PCMWPMACTIONOVW2LCHANGED: Last Modified"); 
		f.setHidden();

		f = headblk.addField("LATEST_PM","Datetime");
		f.setSize(9);
		f.setLabel("PCMWPMACTIONOVW2LPM: Last Performed");
		f.setHidden();

		headblk.setView("PM_ACTION");
		headblk.defineCommand("PM_ACTION_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("None",   "");
		headbar.addCustomCommand("eventGeneration", mgr.translate("PCMWPMACTIONOVW2EVENGEN: Event Generation..."));
		headbar.enableCommand(headbar.FIND);
		headbar.defineCommand(headbar.FIND,"find");

		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.NEWROW);   
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWPMACTIONOVW2HD: Event Plan"));
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		b = mgr.newASPBlock("ORG_CODE");

		b.addField("CLIENT_VALUES1");

		b = mgr.newASPBlock("CALL_CODE");

		b.addField("CLIENT_VALUES6");
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPMACTIONOVW2EVNTPL: Event Plan";
	}

	protected String getTitle()
	{
		return "PCMWPMACTIONOVW2EVNTPL: Event Plan";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("window.name = \"frmEventPlan\";\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(findFlag));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open(\"QueryEventPlanDlg.page\",\"frmQryEventPlan\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=770,height=460\");   \n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rmbEventGenFlag));	//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open(\"EventGenerationDlg.page?PM_NO=\"+'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(nPmNo));		//XSS_Safe AMNILK 20070725
		appendDirtyJavaScript("',\"dlgEventGen\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=770,height=460\");\n");
		appendDirtyJavaScript("}   \n");
	}
}
