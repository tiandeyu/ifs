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
*  File        : PmWithObsoleteStdJobs.java
* ---------------------------------------------------------------------------- 
*  041101  NIJALK  Created.
*  Modified:
*  041110  NAMELK  Duplicated Translation Tags Corrected.
*  041206  NIJALK  Moved the adjust() to end of file.
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class PmWithObsoleteStdJobs extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmWithObsoleteStdJobs");

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
	public PmWithObsoleteStdJobs(ASPManager mgr, String page_path)
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

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		//Bug 58216 Start
	        q.addWhereCondition("STD_JOB_ID = ?");
		q.addParameter("STD_JOB_ID",std_job_id);
		q.addWhereCondition("CONTRACT = ?");
		q.addParameter("CONTRACT",std_job_contract);
		//Bug 58216 End
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
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
                urlString = createTransferUrl("PmAction.page", headset.getSelectedRows("PM_NO,CONTRACT"));
                newWinHandle = "PmAction"; 
            }
            else if (pm_type.equals("ActiveRound"))
            {
                urlString = createTransferUrl("PmActionRound.page", headset.getSelectedRows("PM_NO,CONTRACT"));
                newWinHandle = "PmActionRound"; 
            }
        }

        public void updatePM()
        {
            ASPManager mgr = getASPManager();
            try
            {
                int count = 0;

                mgr.showAlert(mgr.translate("PCMWSTDJOBREPLACEMENT: The replacement of the standard job revisions will be performed on the same revision of the PM Actions."));

                if (headlay.isMultirowLayout())
                {
                        headset.storeSelections();
                        headset.setFilterOn();
                        count = headset.countSelectedRows();
                }
                else
                {
                        headset.unselectRows();
                        count = 1;
                }

                trans.clear();
                headset.first();
                for (int i = 0;i < count;i++)
                {
                    cmd = trans.addCustomCommand("UPDPM"+i,"Standard_Job_API.Replace_Revsions_");
                    cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
                    cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
                    cmd.addParameter("STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION"));
                    cmd.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
                    cmd.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));

                    if (headlay.isMultirowLayout())
                        headset.next();
                }
                trans = mgr.perform(trans);
                if (headlay.isMultirowLayout())
                        headset.setFilterOff();
            }
            catch (Throwable any)
            {
                mgr.showError(mgr.translate("PCMWSTDJOBREPLACEMENTERROR: The update of PM actions was not performed."));
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

                f = headblk.addField("COMPANY");
                f.setLabel("PCMWPMWITHOBSSTDJOBSCOMPANY: Company");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setUpperCase();
                f.setMaxLength(20);
                f.setHidden();

                f = headblk.addField("STD_JOB_ID");
                f.setUpperCase();
                f.setMandatory();
                f.setHidden();
                f.setMaxLength(12);
                f.setQueryable();

                f = headblk.addField("CONTRACT");
                f.setUpperCase();
                f.setMandatory();
                f.setHidden();
                f.setMaxLength(5);
                f.setQueryable();

                f = headblk.addField("PM_NO","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPMNO: PM No");
                f.setInsertable();
                f.setReadOnly();
                f.setMandatory();
                f.setQueryable();

                f = headblk.addField("PM_REVISION");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPMREVISION: PM Revision");
                f.setQueryable();
                f.setMandatory();
                f.setInsertable();
                f.setReadOnly();
                f.setMaxLength(6);
                f.setQueryable();

                f = headblk.addField("MCH_CONTRACT");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMCHCONTRACT: Site");
                f.setQueryable();
                f.setMandatory();
                f.setInsertable();
                f.setReadOnly();
                f.setDynamicLOV("SITE",600,445);
                f.setUpperCase();
                f.setMaxLength(5);

                f = headblk.addField("MCH_CODE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMCHCODE: Object ID");
                f.setMandatory();
                f.setQueryable();
                f.setMaxLength(100);

                f = headblk.addField("MCH_NAME");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMCHNAME: Object Description");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setMaxLength(45);

                f = headblk.addField("ACTION_CODE_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSACTIONCODEID: Action");
                f.setQueryable();
                f.setMandatory();
                f.setDynamicLOV("MAINTENANCE_ACTION",600,445);
                f.setUpperCase();
                f.setMaxLength(10);

                f = headblk.addField("ACTION_DESCR");
                f.setLabel("PCMWPMWITHOBSSTDJOBSACTIONDESCR: Action Description");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setMaxLength(48);

                f = headblk.addField("STATE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSSTATE: State");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setFunction("Pm_Action_API.Get_Status(:PM_NO,:PM_REVISION)");

                f = headblk.addField("OLD_REVISION");
                f.setLabel("PCMWPMWITHOBSSTDJOBSOLDREVISION: Old Revision");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setFunction("Pm_Action_API.Get_Old_Revision(:PM_NO,:PM_REVISION)");
                f.setDefaultNotVisible();

                f = headblk.addField("ORG_CONTRACT");
                f.setLabel("PCMWPMWITHOBSSTDJOBSORGCONTRACT: WO Site");
                f.setQueryable();
                f.setMandatory();
                f.setDynamicLOV("SITE",600,445);
                f.setUpperCase();
                f.setMaxLength(5);

                f = headblk.addField("ORG_CODE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSORGCODE: Maint. Org.");
                f.setQueryable();
                f.setMandatory();
                f.setDynamicLOV("ORGANIZATION","CONTRACT ORG_CONTRACT",600,445);
                f.setUpperCase();
                f.enumerateValues("ORGANIZATION_API.Enumerate","");
                f.setMaxLength(8);

                f = headblk.addField("MAINT_EMPLOYEE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMAINTEMPLOYEE: Maint Employee");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(11);
                f.setHidden();

                f = headblk.addField("MAINT_EMP_SIGN");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMAINTEMPSIGN: Executed By");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(20);

                f = headblk.addField("SIGNATURE_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSSIGNATUREID: Signature id");
                f.setQueryable();
                f.setDynamicLOV("COMPANY_EMP","COMPANY",600,445);
                f.setUpperCase();
                f.setMaxLength(11);
                f.setHidden();

                f = headblk.addField("SIGNATURE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSSIGNATURE: Planned By");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(20);

                f = headblk.addField("WORK_TYPE_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSWORKTYPEID: Work Type");
                f.setQueryable();
                f.setDynamicLOV("WORK_TYPE",600,445);
                f.setUpperCase();
                f.setMaxLength(20);
                f.setDefaultNotVisible();

                f = headblk.addField("OP_STATUS_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSOPSTATUSID: Operational Status");
                f.setQueryable();
                f.setDynamicLOV("OPERATIONAL_STATUS",600,445);
                f.setUpperCase();
                f.setMaxLength(3);
                f.setDefaultNotVisible();

                f = headblk.addField("MIN_STOP_TIME","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMINSTOPTIME: Min Stop Time");
                f.setDbName("PLAN_HRS");
                f.setQueryable();
                f.setDefaultNotVisible();

                f = headblk.addField("GROUP_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSGROUPID: Group ID");
                f.setInsertable();
                f.setReadOnly();
                f.setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,445);
                f.setMaxLength(2000);
                f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CONTRACT,:MCH_CODE)");
                f.setDefaultNotVisible();

                f = headblk.addField("PRIORITY_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPRIORITYID: Priority");
                f.setQueryable();
                f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
                f.setUpperCase();
                f.setSize(3);
                f.setMaxLength(1);
                f.setDefaultNotVisible();

                f = headblk.addField("PLAN_MEN","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPLANMEN: Planned Men");
                f.setQueryable();

                f = headblk.addField("PLAN_HRS","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPLANHRS: Planned Hours");
                f.setQueryable();

                f = headblk.addField("ROUNDDEF_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSROUNDDEFID: Route ID");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(8);

                f = headblk.addField("PM_ORDER_NO","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPMORDERNO: Order");
                f.setQueryable();

                f = headblk.addField("ROLE_CODE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSROLECODE: Craft ID");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(10);

                f = headblk.addField("LAST_CHANGED","Datetime");
                f.setLabel("PCMWPMWITHOBSSTDJOBSLASTCHANGED: Last Modified");
                f.setQueryable();
                f.setDefaultNotVisible();

                f = headblk.addField("LATEST_PM","Datetime");
                f.setLabel("PCMWPMWITHOBSSTDJOBSLATESTPM: Last Performed");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setDefaultNotVisible();

                f = headblk.addField("OVERDUE_PERCENT","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSOVERDUEPERCENT: OverDue Percent (%)");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setFunction("PM_Action_API.Overdue_Percent(:PM_NO,:PM_REVISION)");
                f.setDefaultNotVisible();

                f = headblk.addField("PM_PERFORMED_DATE_BASED");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPERFDATEBASED: Performed Date Based");
                f.setQueryable();
                f.enumerateValues("PM_PERFORMED_DATE_BASED_API");
                f.setMaxLength(20);
                f.setDefaultNotVisible();

                f = headblk.addField("START_VALUE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSSTARTVALUE: Start Value");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(11);
                f.setDefaultNotVisible();

                f = headblk.addField("PM_START_UNIT");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPMSTARTUNIT: Start Unit");
                f.setQueryable();
                f.enumerateValues("PM_START_UNIT_API");
                f.setMaxLength(10);
                f.setDefaultNotVisible();

                f = headblk.addField("INTERVAL");
                f.setLabel("PCMWPMWITHOBSSTDJOBSINTERVAL: Interval");
                f.setQueryable();
                f.setMaxLength(3);
                f.setDefaultNotVisible();

                f = headblk.addField("PM_INTERVAL_UNIT");
                f.setLabel("PCMWPMWITHOBSSTDJOBSINTRVLUNIT: Interval Unit");
                f.setQueryable();
                f.enumerateValues("PM_INTERVAL_UNIT_API");
                f.setMaxLength(5);
                f.setDefaultNotVisible();

                f = headblk.addField("PM_GENERATEABLE");
                f.setLabel("PCMWPMWITHOBSSTDJOBSGENERATABLE: Generateable");
                f.setQueryable();
                f.enumerateValues("PM_GENERATEABLE_API");
                f.setMaxLength(20);
                f.setDefaultNotVisible();

                f = headblk.addField("VALID_FROM","Datetime");
                f.setLabel("PCMWPMWITHOBSSTDJOBSVALIDFROM: Valid From");
                f.setQueryable();

                f = headblk.addField("VALID_TO","Datetime");
                f.setLabel("PCMWPMWITHOBSSTDJOBSVALIDTO: Valid To");
                f.setQueryable();

                f = headblk.addField("CALENDAR_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSCALENDARID: Calendar");
                f.setQueryable();
                f.setMaxLength(10);
                f.setDefaultNotVisible();

                f = headblk.addField("CAL_DESC");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMCALDESC: Description");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
                f.setDefaultNotVisible();

                f = headblk.addField("CALL_CODE");
                f.setLabel("PCMWPMWITHOBSSTDJOBEVENT: Event");
                f.setQueryable();
                f.setDynamicLOV("MAINTENANCE_EVENT",600,445);
                f.enumerateValues("MAINTENANCE_EVENT_API.Enumerate","");
                f.setUpperCase();
                f.setMaxLength(10);
                f.setDefaultNotVisible();

                f = headblk.addField("START_CALL","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSSTARTCALL: Start Event");
                f.setQueryable();
                f.setMaxLength(2);
                f.setDefaultNotVisible();

                f = headblk.addField("CALL_INTERVAL","Number");
                f.setLabel("PCMWPMWITHOBSSTDJOBSCALLINTERVAL: Event Interval");
                f.setQueryable();
                f.setMaxLength(2);
                f.setDefaultNotVisible();

                f = headblk.addField("DESCRIPTION");
                f.setLabel("PCMWPMWITHOBSSTDJOBSDESCRIPTION: Work Description");
                f.setQueryable();
                f.setMaxLength(2000);
                
                f = headblk.addField("VENDOR_NO");
                f.setLabel("PCMWPMWITHOBSSTDJOBSVENDORNO: Contractor");
                f.setQueryable();
                f.setDynamicLOV("SUPPLIER_INFO",600,445);
                f.setUpperCase();
                f.setMaxLength(20);
                f.setDefaultNotVisible();

                f = headblk.addField("VENDOR_NAME");
                f.setLabel("PCMWPMWITHOBSSTDJOBSVENDORNAME: Contractor Name");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setFunction("Maintenance_Supplier_API.Get_Description(:VENDOR_NO)");
                f.setDefaultNotVisible();

                f = headblk.addField("MATERIALS");
                f.setLabel("PCMWPMWITHOBSSTDJOBSMATERIALS: Material");
                f.setQueryable();
                f.setMaxLength(60);
                f.setDefaultNotVisible();

                f = headblk.addField("TESTNUMBER");
                f.setLabel("PCMWPMWITHOBSSTDJOBSTESTNUMBER: Test Number");
                f.setQueryable();
                f.setMaxLength(15);
                f.setDefaultNotVisible();

                f = headblk.addField("ALTERNATE_DESIGNATION");
                f.setLabel("PCMWPMWITHOBSSTDJOBSALTNATIVEDSG: Alternate Designation");
                f.setQueryable();
                f.setMaxLength(60);
                f.setDefaultNotVisible();

                f = headblk.addField("CONTRACT_ID");              
                f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");           
                f.setUpperCase();
                f.setDefaultNotVisible();
                f.setMaxLength(15);
                f.setLabel("PCMWPMWITHOBSSTDJOBSCONTRACTID: Contract ID");
                f.setSize(15);
            
                f = headblk.addField("LINE_NO","Number");
                f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");
                f.setDefaultNotVisible();
                f.setLabel("PCMWPMWITHOBSSTDJOBSLINENO: Line No");
                f.setSize(10); 

                f = headblk.addField("TEST_POINT_ID");
                f.setLabel("PCMWPMWITHOBSSTDJOBSTESTPOINTID: Testpoint");
                f.setQueryable();
                f.setUpperCase();
                f.setMaxLength(6);
                f.setDefaultNotVisible();

                f = headblk.addField("LOCATION");
                f.setLabel("PCMWPMWITHOBSSTDJOBSLOCATION: Location");
                f.setQueryable();
                f.setInsertable();
                f.setReadOnly();
                f.setMaxLength(2000);
                f.setFunction("Equipment_Object_Test_Pnt_API.Get_Location(:MCH_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
                f.setDefaultNotVisible();

                f = headblk.addField("PM_TYPE_DB");
                f.setLabel("PCMWPMWITHOBSSTDJOBSPMTYPE: PM Type");
                f.setQueryable();
                f.setReadOnly();
                f.setMaxLength(30);
                f.setHidden();

                f = headblk.addField("STD_JOB_REVISION");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("IS_REPLACEABLE");
                f.setHidden();
                f.setFunction("Standard_Job_API.Is_Replaceable(:STD_JOB_ID,:CONTRACT,NULL,:PM_NO,:PM_REVISION)");

		headblk.setView("PM_ACTION_WITH_OBS_STD_JOBS");
                headblk.disableDocMan();
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();

                headbar.addCustomCommand("prepare",mgr.translate("PCMWPMPREPARE: Prepare..."));
                headbar.addCustomCommand("updatePM",mgr.translate("PCMWSEPARATESTANDARDJOB2UPDATEPMREV: Replace Obsolete Revisions on PM Actions"));

                headbar.addCommandValidConditions("updatePM","IS_REPLACEABLE","Enable","TRUE");
                headbar.enableMultirowAction();

		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);
                headtbl.enableRowSelect();

                headlay.setFieldSpan("DESCRIPTION",1,3);
		headlay.setFieldSpan("PM_GENERATEABLE",1,3);
		headlay.setFieldSpan("VENDOR_NO",1,3);
		headlay.setFieldSpan("TEST_POINT_ID",1,3);
		headlay.setFieldSpan("MATERIALS",1,3);
		headlay.setFieldSpan("ALTERNATE_DESIGNATION",1,3);
	}

        public void  adjust()
	{
	       ASPManager mgr = getASPManager();

               if (headset.countRows()>0)
               {
                   if ("ActiveSeparate".equals(headset.getRow().getValue("PM_TYPE_DB")))
                   {
                       mgr.getASPField("PLAN_MEN").setHidden();
                       mgr.getASPField("PLAN_HRS").setHidden();
                       mgr.getASPField("ROUNDDEF_ID").setHidden();
                       mgr.getASPField("PM_ORDER_NO").setHidden();
                       mgr.getASPField("ROLE_CODE").setHidden();
                   }
                   else if ("ActiveRound".equals(headset.getRow().getValue("PM_TYPE_DB")))
                   {
                       mgr.getASPField("MIN_STOP_TIME").setHidden();
                       mgr.getASPField("SIGNATURE").setHidden();
                   }
               }
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPMWITHOBSSTDJOBS: Overview - PM Actions with Obsolete Standard Job "+std_job_id+" in Site "+std_job_contract+"";
	}

	protected String getTitle()
	{
		return "PCMWPMWITHOBSSTDJOBS: Overview - PM Actions with Obsolete Standard Job "+std_job_id+" in Site "+std_job_contract+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070725
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
