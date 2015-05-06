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
*  File        : WOWithObsoleteStdJobs.java
* ---------------------------------------------------------------------------- 
*  041109  NIJALK  Created.
*  060818  AMNILK  Bug 58216, Eliminated SQL Injection security vulnerability.
*  060906  AMDILK  Merged with the Bug Id 58216
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class WOWithObsoleteStdJobs extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WOWithObsoleteStdJobs");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String std_job_id;
        private String std_job_contract;

	private boolean win_cls;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

        private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
	private int newWinHeight = 600;
	private int newWinWidth = 900;

	//===============================================================
	// Construction 
	//===============================================================
	public WOWithObsoleteStdJobs(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                std_job_id = "";
                std_job_contract = "";

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
                std_job_id = ctx.readValue("STD_JOB_ID","");
                std_job_contract = ctx.readValue("CONTRACT","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")))
		{
			std_job_id = mgr.readValue("STD_JOB_ID");
                        std_job_contract = mgr.readValue("CONTRACT");

			okFind();

		}

                adjust();

		ctx.writeValue("STD_JOB_ID",std_job_id);
		ctx.writeValue("CONTRACT",std_job_contract);
		ctx.writeFlag("WINCLS",win_cls);
	}

        public void  adjust()
	{
	       ASPManager mgr = getASPManager();

               if (headset.countRows()>0)
               {
                   String pm_type = headset.getRow().getValue("PM_TYPE_DB");

                   if ((!mgr.isEmpty(pm_type)) && ("ActiveSeparate".equals(pm_type)))
                   {
                       mgr.getASPField("ROUNDDEF_ID").setHidden();
                       mgr.getASPField("ROUTE_DESC").setHidden();
                       mgr.getASPField("ROLE_CODE").setHidden();
                   }
                   else if ((!mgr.isEmpty(pm_type)) && ("ActiveRound".equals(pm_type)))
                   {
                       mgr.getASPField("PREPARED_BY").setHidden();
                       mgr.getASPField("WORK_MASTER_SIGN").setHidden();
                       mgr.getASPField("WORK_LEADER_SIGN").setHidden();
                       mgr.getASPField("AUTHORIZE_CODE").setHidden();
                       mgr.getASPField("CONNECTION_TYPE").setHidden();
                       mgr.getASPField("MCH_CODE").setHidden();
                       mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
                       mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();
                   }
               }
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
	        q.addWhereCondition("STD_JOB_ID = ?");
		q.addWhereCondition("STD_JOB_CONTRACT = ?");
		q.addParameter("STD_JOB_ID",std_job_id);
		q.addInParameter("STD_JOB_CONTRACT",std_job_contract);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWOWITHOBSOLETESTDJOBSNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CUSTOM FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------
        public void prepare()
        {
            ASPManager mgr = getASPManager();

            if (headlay.isMultirowLayout())
                    headset.store();
            else
            {
                    headset.unselectRows();
                    headset.selectRow();
            }

            String pm_type = headset.getRow().getValue("PM_TYPE_DB");
            bOpenNewWindow = true;

            if (pm_type.equals("ActiveSeparate"))
            {
                urlString = createTransferUrl("ActiveSeparate2.page", headset.getSelectedRows("WO_NO"));
                newWinHandle = "ActiveSeparate2"; 
            }
            else if (pm_type.equals("ActiveRound"))
            {
                urlString = createTransferUrl("ActiveRound.page", headset.getSelectedRows("WO_NO"));
                newWinHandle = "ActiveRound"; 
            }
        }

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

//-----------------------------------------------------------------------------
//------------------------  PREDEFINE  ----------------------------------------
//-----------------------------------------------------------------------------
	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

                f = headblk.addField("OBJID");
                f.setHidden();

                f = headblk.addField("OBJVERSION");
                f.setHidden();

                f = headblk.addField("STD_JOB_ID");
                f.setUpperCase();
                f.setMandatory();
                f.setHidden();
                f.setMaxLength(12);
                f.setQueryable();

                f = headblk.addField("STD_JOB_CONTRACT");
                f.setUpperCase();
                f.setMandatory();
                f.setHidden();
                f.setMaxLength(5);
                f.setQueryable();

                f = headblk.addField("WO_NO","Number");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSWONO: WO No");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(8);
                f.setSize(10);
                f.setReadOnly();

                f = headblk.addField("ROUNDDEF_ID");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSROUNDDEFID: Route ID");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(24);
                f.setSize(15);
                f.setReadOnly();

                f = headblk.addField("ROUTE_DESC");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSROUTEDESC: Route Description");
                f.setFunction("Pm_Round_Definition_API.Get_Description(:ROUNDDEF_ID)");
                f.setReadOnly();

                f = headblk.addField("CONTRACT");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSCONTRACT: WO Site");
                f.setQueryable();
                f.setMaxLength(15);
                f.setSize(10);
                f.setReadOnly();

                f = headblk.addField("CONNECTION_TYPE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSCONNECTIONTYPE: Connection Type");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();

                f = headblk.addField("MCH_CODE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSMCHCODE: Object ID");
                f.setQueryable();
                f.setMaxLength(300);
                f.setReadOnly();

                f = headblk.addField("MCH_CODE_CONTRACT");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSMCHCODECONTRACT: Site");
                f.setQueryable();
                f.setMaxLength(15);
                f.setSize(15);
                f.setReadOnly();

                f = headblk.addField("MCH_CODE_DESCRIPTION");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSMCHCODEDESCRIPTION: Description");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();

                f = headblk.addField("GROUP_ID");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSGROUPID: Group ID");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("CRITICALITY");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSCRITICALITY: Criticality");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("ORG_CODE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSORGCODE: Maint. Org.");
                f.setQueryable();
                f.setMaxLength(24);
                f.setSize(15);
                f.setReadOnly();

                f = headblk.addField("ERR_DESCR");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSERRDESCR: Directive");
                f.setQueryable();
                f.setMaxLength(80);
                f.setReadOnly();

                f = headblk.addField("STATE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSSTATE: Status");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();

                f = headblk.addField("PLAN_S_DATE","Datetime");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPLANSDATE: Planned Start");
                f.setQueryable();
                f.setReadOnly();

                f = headblk.addField("PLAN_F_DATE","Datetime");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPLANFDATE: Planned Completion");
                f.setQueryable();
                f.setReadOnly();

                f = headblk.addField("PLAN_HRS","Number");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPLANHRS: Execution Time");
                f.setQueryable();
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("PLANNED_MAN_HRS","Number");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPLANNEDMANHRS: Man Hours");
                f.setQueryable();
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("REQUIRED_START_DATE","Datetime");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSREQUIREDSTARTDATE: Required Start");
                f.setQueryable();
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("REQUIRED_END_DATE","Datetime");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSREQUIREDENDDATE: Required End");
                f.setQueryable();
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("CALL_CODE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSCALLCODE: Event");
                f.setQueryable();
                f.setMaxLength(30);
                f.setSize(15);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("ROLE_CODE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSROLECODE: Craft");
                f.setQueryable();
                f.setMaxLength(30);
                f.setSize(15);
                f.setReadOnly();

                f = headblk.addField("OP_STATUS_ID");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSOPSTATUSID: Operational Status");
                f.setQueryable();
                f.setMaxLength(9);
                f.setSize(10);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("WORK_TYPE_ID");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSWORKTYPEID: Work Type");
                f.setQueryable();
                f.setMaxLength(60);
                f.setSize(20);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("REPORTED_BY");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSREPORTEDBY: Reported By");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("PREPARED_BY");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPREPAREDBY: Prepared By");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("WORK_MASTER_SIGN");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSWORKMASTERSIGN: Executed By");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("WORK_LEADER_SIGN");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSWORKLEADERSIGN: Work Leader");
                f.setQueryable();
                f.setMaxLength(4000);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("AUTHORIZE_CODE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSAUTHORIZECODE: Coordinator");
                f.setQueryable();
                f.setMaxLength(60);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("TEST_POINT_ID");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSTESTPOINTID: Testpoint");
                f.setQueryable();
                f.setMaxLength(18);
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("PM_NO","Number");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPMNO: PM No");
                f.setQueryable();
                f.setReadOnly();

                f = headblk.addField("PM_REVISION");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPMREVISION: PM Revision");
                f.setQueryable();
                f.setMaxLength(18);
                f.setReadOnly();

                f = headblk.addField("PM_TYPE");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPMTYPE: Pm Type");
                f.setMaxLength(4000);
                f.setHidden();
                f.setReadOnly();

                f = headblk.addField("PM_TYPE_DB");
                f.setLabel("PCMWWOWITHOBSOLETESTDJOBSPMTYPEDB: PM Type DB");
                f.setMaxLength(90);
                f.setHidden();
                f.setReadOnly();

                headblk.setView("WO_WITH_OBS_STD_JOBS");
                headblk.disableDocMan();
                headset = headblk.getASPRowSet();
               
                headbar = mgr.newASPCommandBar(headblk);
                headtbl = mgr.newASPTable(headblk);
                headlay = headblk.getASPBlockLayout();

                headbar.enableMultirowAction();
                headbar.disableCommand(headbar.NEWROW);
                headbar.disableCommand(headbar.EDITROW);
                headbar.addCustomCommand("prepare",mgr.translate("PCMWWOWITHOBSOLETESTDJOBSPREPARE: Prepare..."));

                headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
                headlay.setDialogColumns(2);
                headtbl.enableRowSelect();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWOWITHOBSOLETESTDJOBS: Overview - Active Work Orders with Obsolete Standard Job "+std_job_id+" in Site "+std_job_contract+"";
	}

	protected String getTitle()
	{
                return "PCMWWOWITHOBSOLETESTDJOBS: Overview - Active Work Orders with Obsolete Standard Job "+std_job_id+" in Site "+std_job_contract+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070727
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
			appendDirtyJavaScript(newWinWidth);
			appendDirtyJavaScript(",height=");
			appendDirtyJavaScript(newWinHeight);
			appendDirtyJavaScript("\"); \n");       
		}

	}
}
