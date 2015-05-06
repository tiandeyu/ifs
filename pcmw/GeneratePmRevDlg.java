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
*  File        : GeneratePmRevDlg.java
* ---------------------------------------------------------------------------- 
*  041104  NIJALK  Created.
*  041209  Chanlk  Changed the title.
*  070218  ILSOLK  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMNILK  070716  Eliminated XSS Secutiry vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class GeneratePmRevDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.GeneratePmRevDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPBlockLayout headlay;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPField f;
        private ASPHTMLFormatter fmt;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String action_code_id;
        private String contract;
        private String standard_job_type;
        private String std_job_id;
        private String std_job_revision;
        private String org_code;

        private String pm_no;
        private String type;

	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public GeneratePmRevDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                action_code_id = "";
                contract = "";
                standard_job_type = "";
                std_job_id = "";
                std_job_revision = "";
                org_code = "";

		ASPManager mgr = getASPManager();
                fmt = mgr.newASPHTMLFormatter();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
                action_code_id = ctx.readValue("ACTION_CODE_ID","");
                contract = ctx.readValue("CONTRACT","");
                standard_job_type = ctx.readValue("STANDARD_JOB_TYPE_DB","");
                std_job_id = ctx.readValue("STD_JOB_ID","");
                std_job_revision = ctx.readValue("STD_JOB_REVISION","");
                org_code = ctx.readValue("ORG_CODE","");

		win_cls = ctx.readFlag("WINCLS",win_cls);
		ok_sub = ctx.readFlag("OKSUB",ok_sub);   

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("ACTION_CODE_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")) && !mgr.isEmpty(mgr.getQueryStringValue("STANDARD_JOB_TYPE_DB")) && !mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_REVISION")) && !mgr.isEmpty(mgr.getQueryStringValue("ORG_CODE")))
		{
			action_code_id = mgr.readValue("ACTION_CODE_ID");
			contract = mgr.readValue("CONTRACT");
                        standard_job_type = mgr.readValue("STANDARD_JOB_TYPE_DB");
                        std_job_id = mgr.readValue("STD_JOB_ID");
                        std_job_revision = mgr.readValue("STD_JOB_REVISION");
                        org_code = mgr.readValue("ORG_CODE");

			okFind();

                        adjust();

			ASPBuffer temp = headset.getRow();
			temp.setValue("ACTION_ID",action_code_id);
                        temp.setValue("WO_SITE",contract);
                        temp.setValue("ORG_CODE",org_code);
			temp.setValue("STD_JOB_ID",std_job_id);
                        temp.setValue("STD_JOB_CONTRACT",contract);
                        temp.setValue("STD_JOB_REVISION",std_job_revision);
			headset.addRow(temp);
		}

		ctx.writeValue("ACTION_CODE_ID",action_code_id);
		ctx.writeValue("CONTRACT",contract);
                ctx.writeValue("STANDARD_JOB_TYPE_DB",standard_job_type);
		ctx.writeValue("STD_JOB_ID",std_job_id);
		ctx.writeValue("STD_JOB_REVISION",std_job_revision);
                ctx.writeValue("ORG_CODE",org_code);
		ctx.writeFlag("WINCLS",win_cls); 
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();
                String route_id = "";
                String route_comp = "";
                String wo_comp = "";
                type = "";
                if ("2".equals(standard_job_type))
                {
                    route_id = headset.getValue("ROUTE_ID");
                    type = "Route";

                    cmd = trans.addCustomFunction("GETROUTECONT","Pm_Round_Definition_API.Get_Contract","ROUTE_CONTRACT");
                    cmd.addParameter("ROUTE_ID");

                    cmd = trans.addCustomFunction("GETROUTECOMP","SITE_API.Get_Company","ROUTE_COMPANY");
                    cmd.addReference("ROUTE_CONTRACT","GETROUTECONT/DATA");

                    cmd = trans.addCustomFunction("GETWOCOMP","SITE_API.Get_Company","WO_COMPANY");
                    cmd.addParameter("WO_SITE");

                    trans = mgr.perform(trans);
                    route_comp = trans.getValue("GETROUTECOMP/DATA/ROUTE_COMPANY");
                    wo_comp = trans.getValue("GETWOCOMP/DATA/WO_COMPANY");
                }
                else if ("1".equals(standard_job_type))
                {
                    type = "Separate";
                }

                trans.clear();
                if ((("2".equals(standard_job_type)) && (route_comp.equals(wo_comp))) || ("1".equals(standard_job_type)))
                {
                    cmd = trans.addCustomCommand("GENERATEPM","Standard_Job_API.Generate_Pm_Revision");
                    cmd.addParameter("PM_NO");
                    cmd.addParameter("STD_JOB_ID");
                    cmd.addParameter("STD_JOB_CONTRACT");
                    cmd.addParameter("STD_JOB_REVISION");
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("MCH_CODE");
                    cmd.addParameter("ACTION_ID");
                    cmd.addParameter("ORG_CODE");
                    cmd.addParameter("ROUTE_ID");
                    trans = mgr.perform(trans);

                    pm_no = trans.getValue("GENERATEPM/DATA/PM_NO");
                    ok_sub = true;
                }
                else if ("2".equals(standard_job_type))
                {
                    if (!(route_comp.equals(wo_comp)))
                    {
                        mgr.showAlert("PCMWGENERATEPMREVROUTENOTVALID: Executing Department Site and Route ID site are not in the same company for PM No .");
                    }
                }

	}

	public void  cancel()
	{
		win_cls = true;
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
	}

        public void adjust()
        {
               ASPManager mgr = getASPManager();

               if (headset.countRows()>0 && "1".equals(standard_job_type))
               {
                   mgr.getASPField("ROUTE_ID").setHidden();
               }
        }

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

                f = headblk.addField("CONTRACT");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVSITE: Site");
                f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
                f.setMandatory();
                f.setSize(10);
                f.setMaxLength(5); 

                f = headblk.addField("MCH_CODE");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVMCHCODE: Object ID");
                f.setDynamicLOV("MAINTENANCE_OBJECT_LOV","CONTRACT",600,450);
                f.setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Pm(OBJ_LEVEL) = 'TRUE'))");
                f.setMandatory();
                f.setValidation("Maintenance_Object_API.Get_Mch_Name","CONTRACT,MCH_CODE","DESCRIPTION");
                f.setSize(16);
                f.setMaxLength(100); 
		
                f = headblk.addField("DESCRIPTION");
                f.setFunction("''");
                f.setReadOnly();
                f.setLabel("PCMWGENERATEPMREVDESCRIPTION: Description");
                f.setSize(20);
                f.setMaxLength(45); 

		f = headblk.addField("ACTION_ID");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVACTIONID: Action");
                f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
                f.setMandatory();
                f.setSize(10);
                f.setMaxLength(10);

		f = headblk.addField("ROUTE_ID");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVROUTEID: Route ID");
                f.setDynamicLOV("PM_ROUND_DEFINITION2","WO_SITE CONTRACT",600,450);
                f.setMandatory();
                f.setSize(10);
                f.setMaxLength(10);

		f = headblk.addField("WO_SITE");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVWOSITE: WO Site");
                f.setReadOnly();

		f = headblk.addField("ORG_CODE");
                f.setFunction("''");
                f.setLabel("PCMWGENERATEPMREVORGCODE: Maint. Org.");
                f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","WO_SITE CONTRACT",600,450);
                f.setMandatory();
                f.setSize(10);
                f.setMaxLength(10);

                f = headblk.addField("PM_NO","Number");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("STD_JOB_ID");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("STD_JOB_CONTRACT");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("STD_JOB_REVISION");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("ROUTE_CONTRACT");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("ROUTE_COMPANY");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("WO_COMPANY");
                f.setHidden();
                f.setFunction("''");

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELNEW);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.FORWARD);
                headbar.disableCommand(headbar.SAVENEW);

		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

                headlay.setSimple("DESCRIPTION");

		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setDialogColumns(2);
		headtbl.disableRowStatus();
                headbar.disableModeLabel();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWGENERATEPMREVTITLE: Create PM Action from Standard Job";
	}

	protected String getTitle()
	{
		return "PCMWGENERATEPMREVTITLE: Create PM Action from Standard Job";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '90%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>\n");
		appendToHTML(headbar.showBar());
		appendToHTML("  </td>\n");
		appendToHTML(" </tr>\n");
		appendToHTML("</table>\n");
		appendToHTML("<table id=\"cntITEM0\" border=\"0\" width= '90%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>&nbsp;&nbsp;</td>\n");
		appendToHTML("  <td>\n");
		appendToHTML(fmt.drawReadLabel("PCMWGENERATEPMREVINTRO: Enter Object data, Action and Maintenance Organization. Route ID should be provided when generating a Route PM Revision."));
		appendToHTML("  </td>\n");
		appendToHTML(" </tr> \n");
		appendToHTML("</table>\n");
		appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '90%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>\n");
		appendToHTML(headlay.generateDataPresentation());
		appendToHTML("  </td>\n");
		appendToHTML(" </tr>\n");
		appendToHTML("	</table>\n");

                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(win_cls); //XSS_Safe AMNILK 20070716
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(ok_sub); //XSS_Safe AMNILK 20070716
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	alert('");
		appendDirtyJavaScript(mgr.translate("PCMWPMGENERATEDMSG: "+type+" PM Action "+pm_no+" was Created."));
		appendDirtyJavaScript("');\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
