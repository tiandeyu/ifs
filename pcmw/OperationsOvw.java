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
*  File        : OperationsOvw.java
* ---------------------------------------------------------------------------- 
*  050630  THWILK  Created.
*  Modified:
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060126  Corrected localization errors.
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class OperationsOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.OperationsOvw");

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

        private ASPBuffer data;
        private ASPHTMLFormatter fmt;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String pmNo;
        private String pmRevision;
        private String seqNo;
        private String plannedDate;
        private String woNo;
        private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;
        private String qryStr; 
        private String frameName;
        
	//===============================================================
	// Construction 
	//===============================================================
	public OperationsOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                ASPManager mgr = getASPManager();

            pmNo        = "";
            pmRevision  = "";
            seqNo       = "";
            plannedDate = "";
            woNo        = "";

            ctx   = mgr.getASPContext();
            trans = mgr.newASPTransactionBuffer();
            fmt   = mgr.newASPHTMLFormatter();
            pmNo        = ctx.readValue("PM_NO","");
            pmRevision  = ctx.readValue("PM_REVISION",""); 
            plannedDate = ctx.readValue("PLANNED_DATE","");
            woNo        = ctx.readValue("WO_NO","");
            qryStr      = ctx.readValue("QRYSTR",""); 
            frameName   = ctx.readValue("FRMNAME",""); 

            if (mgr.commandBarActivated())
                eval(mgr.commandBarFunction());
            else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
                validate();
            else 
            {
                pmNo         = mgr.readValue("PM_NO");
                pmRevision   = mgr.readValue("PM_REVISION");
                seqNo        = mgr.readValue("SEQ_NO");
                plannedDate  = mgr.readValue("PLANNED_DATE");
                woNo         = mgr.readValue("WO_NO");
                frameName    = mgr.readValue("FRMNAME");
                qryStr       = mgr.readValue("QRYSTR");
                qryStr       = qryStr + "&PLANNED_DATE=" + mgr.URLEncode(plannedDate)+"&WO_NO=" + mgr.URLEncode(woNo);
                okFind();

            }
            
            ctx.writeValue("PM_NO",pmNo);
            ctx.writeValue("PM_REVISION",pmRevision);
            ctx.writeValue("PLANNED_DATE",plannedDate);
            ctx.writeValue("WO_NO",woNo);
            ctx.writeValue("QRYSTR",qryStr); 
            ctx.writeValue("FRMNAME",frameName); 

	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

               
        public void submit()
        {

            ASPManager mgr = getASPManager();
            data = mgr.newASPBuffer();
            String sModify = new String("TRUE");
            String old_date_to = "";
            String planner = "";
            String jobId;
            String row_no         = mgr.readValue("ROW_NO");
            String curr_date_to   = mgr.readValue("DATE_TO");
            String curr_date_from = mgr.readValue("DATE_FROM");
            String sign           = mgr.readValue("SIGNATURE");
            String emp_id         = mgr.readValue("EMPLOYEE_ID");
                
                
            headset.changeRow();
            trans.clear();
	    //Bug 58216 Start
            cmd = trans.addQuery("OLDDATEFROM","Select DATE_TO from Pm_Action_Calendar_Oper where PM_NO = ? and PM_REVISION = ? and SEQ_NO = ? and ROW_NO = ?");
	    q.addParameter("PM_NO",pmNo);
	    q.addParameter("PM_REVISION",pmRevision);
	    q.addParameter("SEQ_NO",headset.getRow().getValue("SEQ_NO"));
	    q.addParameter("ROW_NO",row_no);
	    //Bug 58216 End

            cmd= trans.addCustomFunction("GETFND","Fnd_Session_API.Get_Fnd_User","FND_USER");
            
            cmd= trans.addCustomFunction("GETPLANNER","Person_Info_API.Get_Id_For_User","PLANNER");
            cmd.addReference("FND_USER","GETFND/DATA");

            trans = mgr.perform(trans);

            old_date_to = trans.getBuffer("OLDDATEFROM/DATA").getFieldValue("DATE_TO");
            planner = trans.getValue("GETPLANNER/DATA/PLANNER");
                
            if (!mgr.isEmpty(headset.getRow().getValue("JOB_ID"))) {
                
               jobId  = headset.getRow().getValue("JOB_ID");
                
               if ((mgr.isEmpty(old_date_to) && !mgr.isEmpty(curr_date_to)) || (!mgr.isEmpty(old_date_to) && !old_date_to.equals(curr_date_to)))
               {
                  trans.clear();
                  cmd = trans.addCustomCommand("CALCDATETO","Pm_Action_Calendar_Job_API.Update_Date_To");
                  cmd.addParameter("PM_NO");
                  cmd.addParameter("PM_REVISION");
                  cmd.addParameter("SEQ_NO");
                  cmd.addParameter("JOB_ID");
                  cmd.addParameter("ROW_NO");
                  cmd.addParameter("DATE_FROM");
                  cmd.addParameter("DATE_TO");
                  trans=mgr.perform(trans);
               }
            }
                
            trans.clear();
            cmd = trans.addCustomCommand("UPDATEOPR","Pm_Action_Calendar_Oper_API.Update_Emp_Date_From");
            cmd.addParameter("PM_NO");
            cmd.addParameter("PM_REVISION");
            cmd.addParameter("SEQ_NO");
            cmd.addParameter("ROW_NO",row_no);
            cmd.addParameter("SIGNATURE",sign);
            cmd.addParameter("EMPLOYEE_ID",emp_id);
            cmd.addParameter("DATE_FROM",curr_date_from);
            cmd.addParameter("DATE_TO",curr_date_to);
            cmd.addParameter("PLAN_MEN",mgr.readValue("PLAN_MEN"));
            cmd.addParameter("PLAN_HRS",mgr.readValue("PLAN_HRS"));
            cmd.addParameter("PLANNER",planner);
            cmd.addParameter("TEAM_CONTRACT");
            cmd.addParameter("TEAM_ID");
            trans=mgr.perform(trans);
            headset.refreshAllRows();
         }
     

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();
                trans.clear();

                q = trans.addQuery(headblk);
		//Bug 58216 Start
		q.addWhereCondition("PM_NO = ?");
		q.addParameter("PM_NO",pmNo);
		q.addWhereCondition("PM_REVISION = ?");
		q.addParameter("PM_REVISION",pmRevision);
		q.addWhereCondition("SEQ_NO = ?");
		q.addParameter("SEQ_NO",seqNo);
		//Bug 58216 End

                q.setOrderByClause("ROW_NO");
                q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWOPERATIONSOVWNODATA: No data found."));
			headset.clear();
		}
        }

        //-----------------------------------------------------------------------------
        //-----------------------------  VALIDATE FUNCTION  ---------------------------
        //-----------------------------------------------------------------------------

	public void validate()
	{
		String val;
                String txt;

		ASPManager mgr = getASPManager();
                val = mgr.readValue("VALIDATE");

                if ("SIGNATURE".equals(val))
                {
                    String reqstr = null;
                    int startpos = 0;
                    int endpos = 0;
                    int i = 0;
                    String ar[] = new String[2];
                    String emp_id = "";
                    String sign = "";
                    double plan_men = 0;

                    String new_sign = mgr.readValue("SIGNATURE","");
                    plan_men = mgr.readNumberValue("PLAN_MEN");

                    if (new_sign.indexOf("^",0)>0)
                    {
                            for (i=0 ; i<2; i++)
                            {
                                    endpos = new_sign.indexOf("^",startpos);
                                    reqstr = new_sign.substring(startpos,endpos);
                                    ar[i] = reqstr;
                                    startpos= endpos+1;
                            }
                            sign = ar[0];
                            emp_id = ar[1];
                    }
                    else
                    {
                            cmd = trans.addCustomFunction("EMP","Employee_API.Get_Max_Maint_Emp","EMPLOYEE_ID");                     
                            cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
                            cmd.addParameter("SIGNATURE");
                            trans = mgr.validate(trans);

                            sign = mgr.readValue("SIGNATURE");
                            emp_id = trans.getValue("EMP/DATA/EMPLOYEE_ID");
                    }

                    if ((!isNaN(plan_men)) && (plan_men>1))
                    {
                        mgr.showAlert("SIGNNOTRELAVANT: Signature is not relevant when Plan Men is more than 1.");
                        sign = "";
                        emp_id = "";
                    }

                    txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
                    mgr.responseWrite(txt);
                }

                else if ("PLAN_HRS".equals(val))
                {
                   String dDateFrom = mgr.readValue("DATE_FROM");
                   
                   if (!mgr.isEmpty(dDateFrom)) {
                        cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","DATE_TO");
                        cmd.addParameter("PM_NO");
                        cmd.addParameter("PM_REVISION");
                        cmd.addParameter("SEQ_NO");
                        cmd.addParameter("ROW_NO");
                        cmd.addParameter("DATE_FROM");
                        cmd.addParameter("PLAN_MEN");
                        cmd.addParameter("PLAN_HRS");


                        trans = mgr.validate(trans);
                        String date_to = trans.getBuffer("GETITEMDATETO/DATA").getFieldValue("DATE_TO");
                        txt = (mgr.isEmpty(date_to)?"":date_to) + "^";
                        mgr.responseWrite(txt);
                   }

                }
                else if ("PLAN_MEN".equals(val))
                {
                  String dDateFrom = mgr.readValue("DATE_FROM");

                  if (!mgr.isEmpty(dDateFrom)) {
                       cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","DATE_TO");
                       cmd.addParameter("PM_NO");
                       cmd.addParameter("PM_REVISION");
                       cmd.addParameter("SEQ_NO");
                       cmd.addParameter("ROW_NO");
                       cmd.addParameter("DATE_FROM");
                       cmd.addParameter("PLAN_MEN");
                       cmd.addParameter("PLAN_HRS");


                       trans = mgr.validate(trans);
                       String date_to = trans.getBuffer("GETITEMDATETO/DATA").getFieldValue("DATE_TO");
                       txt = (mgr.isEmpty(date_to)?"":date_to) + "^";
                       mgr.responseWrite(txt);
                  }
                }
                else if ("DATE_FROM".equals(val))
                {
                     cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","DATE_TO");
                     cmd.addParameter("PM_NO");
                     cmd.addParameter("PM_REVISION");
                     cmd.addParameter("SEQ_NO");
                     cmd.addParameter("JOB_ID");
                     cmd.addParameter("DATE_FROM");
                     cmd.addParameter("PLAN_MEN");
                     cmd.addParameter("PLAN_HRS");

                     trans = mgr.validate(trans);
                     String date_to = trans.getBuffer("GETITEMDATETO/DATA").getFieldValue("DATE_TO");
                     txt = (mgr.isEmpty(date_to)?"":date_to) + "^";
                     mgr.responseWrite(txt);
                }
                else if ("TEAM_ID".equals(val))
                {
                    cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "TEAMDESC" );    
                    cmd.addParameter("TEAM_ID");
                    cmd.addParameter("TEAM_CONTRACT");
                    trans = mgr.validate(trans);   
                    String teamDesc  = trans.getValue("TDESC/DATA/TEAMDESC");
                    
                    txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
                    mgr.responseWrite(txt);

                }

                mgr.endResponse();
        }


        //--------------------------------------------------------------------------------------
        //--------------------- PreDefine-------------------------------------------------------
        //--------------------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

                headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();
                
		f = headblk.addField("OBJVERSION");
		f.setHidden();

                f = headblk.addField("COMPANY");
		f.setLabel("PCMWJOBSOPRDLGCOMPANY: Company");
                f.setHidden();

                
		f = headblk.addField("PM_NO","Number","#");
		f.setLabel("PCMWOPERATIONSOVWPMNO: PM No");
                f.setHidden();
                
                f = headblk.addField("PM_REVISION");
		f.setLabel("PCMWOPERATIONSOVWPMREV: PM Revision");
                f.setHidden();
                
                f = headblk.addField("SEQ_NO");
		f.setLabel("PCMWOPERATIONSOVWSEQNO: Seq No");
                f.setHidden();

                f = headblk.addField("ROW_NO");
		f.setLabel("PCMWOPERATIONSOVWROWNO: Operation No");
                f.setReadOnly();

                f = headblk.addField("JOB_ID");
		f.setLabel("PCMWOPERATIONSOVWJOBID: Job Id");
                f.setReadOnly();
                
                f = headblk.addField("ROW_DESCR");
		f.setLabel("PCMWOPERATIONSOVWDESC: Description");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Descr(:PM_NO,:PM_REVISION,:ROW_NO)");

                f = headblk.addField("SIGNATURE");
		f.setLabel("PCMWOPERATIONSOVWEXEBY: Executed By");
		f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
                f.setUpperCase();
                f.setCustomValidation("COMPANY,SIGNATURE","EMPLOYEE_ID,SIGNATURE");

                f = headblk.addField("EMPLOYEE_ID");
		f.setLabel("PCMWOPERATIONSOVWEMPID: Employee Id");
                f.setUpperCase(); 
                f.setReadOnly();

                f = headblk.addField("PLAN_MEN","Number");
		f.setLabel("PCMWOPERATIONSOVWPLANMEN: Planned Men");
                f.setCustomValidation("PM_NO,PM_REVISION,SEQ_NO,ROW_NO,DATE_FROM,PLAN_MEN,PLAN_HRS","DATE_TO");
                
                f = headblk.addField("PLAN_HRS");
		f.setLabel("PCMWOPERATIONSOVWPLANHRS: Planned Hours");
                f.setCustomValidation("PM_NO,PM_REVISION,SEQ_NO,ROW_NO,DATE_FROM,PLAN_MEN,PLAN_HRS","DATE_TO");
                
                f = headblk.addField("TEAM_CONTRACT");
                f.setSize(7);
                f.setDynamicLOV("USER_ALLOWED_SITE_LOV","COMPANY",600,450);
                f.setLabel("PCMWOPERATIONSOVWCONT: Team Site");
                f.setMaxLength(5);
                f.setUpperCase();

                f = headblk.addField("TEAM_ID");
                f.setSize(13);
                f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC");
                f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450);
                f.setMaxLength(20);
                f.setLabel("PCMWOPERATIONSOVWTID: Team ID");
                f.setUpperCase();

                f= headblk.addField("TEAMDESC");
                f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
                f.setSize(40);
                f.setMaxLength(200);
                f.setReadOnly();
                f.setLabel("PCMWOPERATIONSOVWDESC: Description");

                f = headblk.addField("DATE_FROM","Datetime");
		f.setLabel("PCMWOPERATIONSOVWDATEFROM: Date From");
                f.setCustomValidation("PM_NO,PM_REVISION,SEQ_NO,JOB_ID,DATE_FROM,PLAN_MEN,PLAN_HRS","DATE_TO");

                f = headblk.addField("DATE_TO","Datetime");
		f.setLabel("PCMWOPERATIONSOVWDATETO: Date To");

                f = headblk.addField("PREDECESSOR");
                f.setSize(22);
                f.setLabel("PCMWJOBSOPERATIONSDLGPRED: Predecessors");
                f.setFunction("Pm_Role_Dependencies_API.Get_Predecessors(:PM_NO,:PM_REVISION,:ROW_NO)");
                f.setReadOnly();
                
                f = headblk.addField("ORG_CODE");
		f.setLabel("PCMWOPERATIONSOVWORGCODE: Maint.Org.");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Org_Code(:PM_NO,:PM_REVISION,:ROW_NO)");

                f = headblk.addField("ORG_CODE_SITE");
		f.setLabel("PCMWOPERATIONSOVWORGCODESITE: Maint.Org. Site");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Org_Contract(:PM_NO,:PM_REVISION,:ROW_NO)");

                f = headblk.addField("ROLE_CODE");
		f.setLabel("PCMWOPERATIONSOVWROLECODE: Craft Id");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Role_Code(:PM_NO,:PM_REVISION,:ROW_NO)");

                f = headblk.addField("CBADDQUALIFICATION");
                f.setLabel("PCMWOPERATIONSOVWCBADDQUAL: Additional Qualifications");
                f.setFunction("Pm_Action_Role_API.Check_Qualifications_Exist(:PM_NO,:PM_REVISION,:ROW_NO)");
                f.setCheckBox("0,1");
                f.setReadOnly();

                f= headblk.addField("FND_USER");
                f.setHidden();
                f.setFunction("''");

                f = headblk.addField("PLANNER");
                f.setFunction("Company_Emp_API.Get_Person_Id(:COMPANY,Pm_Action_API.Get_Sign_Id(:PM_NO,:PM_REVISION))");
                f.setUpperCase();
                f.setHidden(); 

                headblk.setView("PM_ACTION_CALENDAR_OPER");
                headblk.defineCommand("PM_ACTION_CALENDAR_OPER_API","Modify__");
                headblk.disableDocMan();
		headset = headblk.getASPRowSet();

                headtbl = mgr.newASPTable(headblk);
                headtbl.setTitle(mgr.translate("PCMWOPERATIONSOVW: Overview Operations"));
                headtbl.setWrap();
                
                headbar = mgr.newASPCommandBar(headblk);
                headbar.disableCommand(headbar.FIND);
                headbar.defineCommand(headbar.OKFIND,"okFind");
               	headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
                                            
                headlay = headblk.getASPBlockLayout();
                headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
                headlay.setDialogColumns(2);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMOPERATIONSOVWTITLE: Overview of Operations";
	}

	protected String getTitle()
	{
		return "PCMOPERATIONSOVWTITLE: Overview of Operations";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
                String sOracleFormat = mgr.replace(mgr.getFormatMask("Datetime",true),"mm","MI");
                if (sOracleFormat.indexOf("HH")>-1)
                {
                    if ("HH".equals(sOracleFormat.substring(sOracleFormat.indexOf("HH"),sOracleFormat.indexOf("HH")+2)))
                        sOracleFormat = mgr.replace(sOracleFormat,"HH","HH24");  
                }

                appendToHTML(headlay.show());
                
                if (headset.countRows() > 0)
                {
                   appendToHTML("<table id=\"SND\" border=\"0\">\n");
                   appendToHTML("<tr>\n");
                   appendToHTML("<td><br>\n");
                   appendToHTML(fmt.drawButton("BACK",mgr.translate("PCMWOPERATIONSOVWBACK: Back"),"onClick='executeOk();'"));
                   appendToHTML("</td>\n");
                   appendToHTML("</tr>\n");
                   appendToHTML("</table>\n");
                }
                appendDirtyJavaScript("window.name = \"JobsOperations\";\n"); 

                appendDirtyJavaScript("function lovSignature(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("	whereCond2 = '';\n");
                appendDirtyJavaScript(" if (document.form.ROLE_CODE.value == '')\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("       if(document.form.ORG_CODE.value != '' && document.form.ORG_CODE_SITE.value != '' && document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ORG_CODE.value) +\"' \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+ \"'AND MAINT_ORG= '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n");
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else if ((document.form.ORG_CODE.value != '') && (document.form.ORG_CODE_SITE.value == '') && (document.form.COMPANY.value != ''))\n");
                appendDirtyJavaScript("          {\n");
	        appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else if(document.form.ORG_CODE.value = '' && document.form.ORG_CODE_SITE.value != '' && document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' \";\n");
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else if(document.form.ORG_CODE.value == '' && document.form.ORG_CODE_SITE.value == '' && document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n");
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else\n");
                appendDirtyJavaScript("       {\n");
                appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n");
                appendDirtyJavaScript("       }\n");
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("    if((document.form.ORG_CODE.value != '') && (document.form.ORG_CODE_SITE.value != '') && (document.form.COMPANY.value != ''))\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ROLE_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n");
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else if(document.form.ORG_CODE.value == '' && document.form.ORG_CODE_SITE.value == '' && document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n"); 
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("       else\n");
                appendDirtyJavaScript("          {\n");
                appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n");  
                appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n"); 
                appendDirtyJavaScript("          }\n");
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript(" if(document.form.TEAM_ID.value != '')\n");                           
                appendDirtyJavaScript("    {\n");
                appendDirtyJavaScript("    if(document.form.ROLE_CODE.value == '')\n");
                appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
                appendDirtyJavaScript("       else\n");
                appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('SIGNATURE',i).indexOf('%') !=-1)? getValue_('SIGNATURE',i):'';\n");
                appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
                appendDirtyJavaScript("       {\n");
                appendDirtyJavaScript("              openLOVWindow('SIGNATURE',i,\n");
                appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('SIGNATURE',i))\n"); 
                appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
                appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
                appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ORG_CODE_SITE',i))\n");       
                appendDirtyJavaScript("       	   ,550,500,'validateSignature');\n");
                appendDirtyJavaScript("       }\n");
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("       {\n");        
                appendDirtyJavaScript("              openLOVWindow('SIGNATURE',i,\n");
                appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('SIGNATURE',i))\n"); 
                appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
                appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
                appendDirtyJavaScript("                  + '&ORG_CODE_SITE=' + URLClientEncode(getValue_('ORG_CODE_SITE',i))\n");       
                appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
                appendDirtyJavaScript("       	   ,550,500,'validateItemSignature');\n");
                appendDirtyJavaScript("       }\n");
                appendDirtyJavaScript("}\n");


                appendDirtyJavaScript("function lovTeamId(i,params)"); 
                appendDirtyJavaScript("{"); 
                appendDirtyJavaScript("	if(params) param = params;\n"); 
                appendDirtyJavaScript("	else param = '';\n"); 
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript("	whereCond2 = '';\n");
                appendDirtyJavaScript(" if(document.form.TEAM_CONTRACT.value != '')\n");
                appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.TEAM_CONTRACT.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
                appendDirtyJavaScript(" if(document.form.COMPANY.value != '')\n");
                appendDirtyJavaScript(" if( whereCond1=='')\n");
	        appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.COMPANY.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.COMPANY.value)+\"' \";\n"); 
                appendDirtyJavaScript(" if( whereCond1 !='')\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
		//XSS_Safe AMNILK 20070718
                appendDirtyJavaScript("	whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.DATE_FROM.value)+\"','"+mgr.encodeStringForJavascript(sOracleFormat)+"'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
                appendDirtyJavaScript(" if(document.form.EMPLOYEE_ID.value != '')\n");
                appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.EMPLOYEE_ID.value)+\"' \";\n"); 
                appendDirtyJavaScript(" if(document.form.ORG_CODE.value != '')\n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript(" if( whereCond2=='')\n");
                appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else \n");
                appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if(document.form.ORG_CODE_SITE.value != '')\n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript(" if(whereCond2=='' )\n");
                appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' \";\n"); 
                appendDirtyJavaScript(" else \n");
                appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CODE_SITE.value)+\"' \";\n"); 
                appendDirtyJavaScript(" }\n");
                appendDirtyJavaScript(" if(whereCond2 !='' )\n");
                appendDirtyJavaScript("     {\n");
                appendDirtyJavaScript("        if(whereCond1 !='' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
                appendDirtyJavaScript("        if(document.form.ROLE_CODE.value == '' )\n");
                appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
                appendDirtyJavaScript("        else \n");
                appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\"		;\n"); 
                appendDirtyJavaScript("     }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n"); 
                appendDirtyJavaScript("	var key_value = (getValue_('TEAM_ID',i).indexOf('%') !=-1)?getValue_('TEAM_ID',i):'';\n"); 
                appendDirtyJavaScript("	openLOVWindow('TEAM_ID',i,\n"); 
                appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
                appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_ID',i))"); 
                appendDirtyJavaScript("		+ '&TEAM_ID=' + URLClientEncode(key_value)"); 
                appendDirtyJavaScript("		,550,500,'validateTeamId');\n"); 
                appendDirtyJavaScript("}\n"); 

                appendDirtyJavaScript("function lovTeamContract(i,params)\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("	if(params) param = params;\n");
                appendDirtyJavaScript("	else param = '';\n");
                appendDirtyJavaScript("	whereCond1 = '';\n"); 
                appendDirtyJavaScript(" if (document.form.COMPANY.value != '') \n");
                appendDirtyJavaScript(" {\n");
                appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.COMPANY.value) + \"' \";\n"); 
                appendDirtyJavaScript("    }\n");
                appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
                appendDirtyJavaScript("'");
		appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
		appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
		appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
                appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function executeOk() {\n");
                appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr));		//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("' + \"&PMQUALIFICATION=1\",'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName));	//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("}\n");
                     
        
	}
}
