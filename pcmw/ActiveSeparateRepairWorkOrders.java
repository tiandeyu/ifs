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
*  File        : ActiveSeparateRepairWorkOrders.java 
*  Created     : ASP2JAVA Tool  010308  Created Using the ASP file ActiveSeparateRepairWorkOrders.asp 
*  Modified    :
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  GACOLK  021204  Set Max Length of NON_SERIAL_REPAIR_FLAG, NON_SERIAL_LOCATION to 50
*  JEJALK  021206  Takeoff(beta) Modification.Added MchCodeContarct
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040716  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  ARWILK  041110  Replaced getContents with printContents.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Id: Bug 58214.  
*  ILSOLK  070706  Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateRepairWorkOrders extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateRepairWorkOrders");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String url;
	private String rmbPrepFlag;
	private String rmbRepIFlag;
	private String sWoNo;
	private String val;
	private ASPQuery q;
	private int currrow;
	private String head_command;
	private String fldTitleObjectId;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateRepairWorkOrders(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		frm = mgr.getASPForm();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"); 
		rmbPrepFlag = "";
		rmbRepIFlag = "";
		sWoNo = "";    

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();

		adjust();
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

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");

		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void okFind()
	{
		ASPManager mgr = getASPManager();

		currrow = headset.getCurrentRowNo();

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPAIRWORKORDERSNODATA: No data found."));
			headset.clear();
		}

		headset.goTo(currrow);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void prepare()
	{

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		sWoNo = headset.getRow().getValue("WO_NO");

		rmbPrepFlag = "true";
	}

	public void reportIn()
	{

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		sWoNo = headset.getRow().getValue("WO_NO");

		rmbRepIFlag = "true";
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("OBJSTATE");
		f.setHidden();

		f = headblk.addField("OBJEVENTS");
		f.setHidden();

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(12);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSWO_NO: WO No");

		f = headblk.addField("NON_SERIAL_REPAIR_FLAG");
		f.setSize(14);
		f.setMaxLength(50);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSNON_SERIAL_REPAIR_FLAG: Non Serial Repair WO");
		f.setCheckBox("FALSE,TRUE");

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSCONTRACT: WO Site");
		f.setUpperCase();


		f = headblk.addField("MCH_CODE");
		f.setSize(25);
		f.setMaxLength(100);
		f.setDynamicLOV("MAINTENANCE_OBJECT",600,445);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSMCH_CODE: Object ID");
		f.setUpperCase();

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSMCHCODECONTRACT: Site");
		f.setUpperCase();

		f = headblk.addField("MCHNAME");
		f.setSize(40);
		f.setMaxLength(2000);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSMCHNAME: Object Description");
		f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
		mgr.getASPField("MCH_CODE").setValidation("MCHNAME");

		f = headblk.addField("REPAIR_PART_NO");
		f.setSize(25);
		f.setMaxLength(25);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSREPAIR_PART_NO: Part No");
		f.setUpperCase();

		f = headblk.addField("REPAIR_PART_DESCR");
		f.setSize(25);
		f.setMaxLength(25);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSREPAIR_PART_DESCR: Part Descr");
		f.setUpperCase();

		f = headblk.addField("REPAIR_PART_CONTRACT");
		f.setSize(15);
		f.setMaxLength(15);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSREPAIR_PART_CONTRACT: Site for Repair Part");
		f.setUpperCase();

		f = headblk.addField("ORG_CODE");
		f.setSize(18);
		f.setMaxLength(18);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setMandatory();
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSORG_CODE: Maintenance Organization");
		f.setUpperCase();

		f = headblk.addField("ERR_DESCR");
		f.setSize(60);
		f.setMaxLength(60);
		f.setMandatory();
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSERR_DESCR: Directive");

		f = headblk.addField("NON_SERIAL_LOCATION");
		f.setSize(40);
		f.setMaxLength(50);
		f.setSelectBox();
		f.enumerateValues("NON_SERIAL_LOCATION_API");
		f.setReadOnly();
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSNON_SERIAL_LOCATION: Non Serial Location");

		f = headblk.addField("PLAN_S_DATE","Datetime");
		f.setSize(20);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSPLAN_S_DATE: Planned Start");

		f = headblk.addField("PLAN_F_DATE","Datetime");
		f.setSize(20);
		f.setLabel("PCMWACTIVESEPARATEREPAIRWORKORDERSPLAN_F_DATE: Planned Completion");

		headblk.setView("ACTIVE_SEPARATE_REPAIR");
		headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("prepare",mgr.translate("PCMWACTIVESEPARATEREPAIRWORKORDERSPREPARE: Prepare..."));
		headbar.addCustomCommand("reportIn",mgr.translate("PCMWACTIVESEPARATEREPAIRWORKORDERSREPIN: Report In..."));

		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DUPLICATEROW);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		fldTitleObjectId = mgr.translate("PCMWACTIVESEPARATEREPAIRWORKORDERSOBJECTID: Object+ID");
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVESEPARATEREPAIRWORKORDERSTITLE: Repair Work Orders";
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATEREPAIRWORKORDERSTITLE: Repair Work Orders";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("window.name = \"frmRepWorkOrder\";\n");

		// XSS_Safe ILSOLK 20070706
		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(rmbPrepFlag);
		appendDirtyJavaScript("'=='true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open('");
		appendDirtyJavaScript(url);
		appendDirtyJavaScript("'+\"pcmw/ActiveSeparate2.page?WO_NO=\"+'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo));
		appendDirtyJavaScript("',\"frmPrepare\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("}\n");

                // XSS_Safe ILSOLK 20070706
		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(rmbRepIFlag);
		appendDirtyJavaScript("'=='true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("    window.open('");
		appendDirtyJavaScript(url);
		appendDirtyJavaScript("'+\"pcmw/ActiveSeperateReportInWorkOrder.page?WO_NO=\"+'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo));
		appendDirtyJavaScript("',\"frmReportIn\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovMchCode(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   jCont = getValue_('MCH_CODE_CONTRACT',i);\n");
		appendDirtyJavaScript("   if(jCont)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("        openLOVWindow('MCH_CODE',i,\n");
		appendDirtyJavaScript("              '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=");
		appendDirtyJavaScript(fldTitleObjectId);
		appendDirtyJavaScript("&__INIT=1'\n");
                appendDirtyJavaScript("              + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
		appendDirtyJavaScript("              ,600,450,'validateMchCode');\n");
		appendDirtyJavaScript("   }		\n");
		appendDirtyJavaScript("   else   \n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("        openLOVWindow('MCH_CODE',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=");
		appendDirtyJavaScript(fldTitleObjectId);
		appendDirtyJavaScript("&__INIT=1&__WHERE=CONTRACT+IN+%28Select+User%5FAllowed%5FSite%5FAPI%2EAuthorized%28CONTRACT%29+from+DUAL%29'\n");
		appendDirtyJavaScript("		,600,450,'validateMchCode');\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");
	}
}
