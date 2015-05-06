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
*  File        : JobsOperationsDlg.java
* ---------------------------------------------------------------------------- 
*  040916  NIJALK  Created.
*  041011  NIJALK  Modified preDefine(), submit(),submitItem(),validate().
*  041111  NIJALK  Made the fields "Executed by" visible and read only in jobs and operations.
*  050617  THWILK  Fixed an existing bug & added methods securityChk & additionalQualifications.Modified
*                  run(),submit(),submitItem(),adjust(),validate(),predefine(), & printContents().
*  050705  THWILK  Modified methods run(),submit(),submitItem(),okFind(),validate(),predefine(), 
*                  printContents() and additionalQualifications().Added showAllOperations.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060126  Corrected localization errors.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMNILK  060807  Merged Bug Id: 58214.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMNILK  070717  Eliminated XSS Security Vulnerability.
*  ILSOLK  070730  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class JobsOperationsDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobsOperationsDlg");

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

	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPBlockLayout itemlay;
	private ASPCommandBar itembar;
	private ASPTable itemtbl;

        private ASPBuffer data;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String pm_no;
        private String pm_revision;
        private String seq_no;
        private String planned_date;
        private String wo_no;

	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;
        private boolean secCompetence;
        private ASPBuffer SecBuff;
        private String qrystr; 
        private String headNo;
        private boolean bOpenNewWindow;
        private String urlString;
        private String newWinHandle;
        private int newWinHeight = 600;
        private int newWinWidth = 900;
        
	//===============================================================
	// Construction 
	//===============================================================
	public JobsOperationsDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                ASPManager mgr = getASPManager();

            pm_no = "";
            pm_revision = "";
            seq_no = "";
            planned_date = "";
            wo_no = "";

            ctx = mgr.getASPContext();
            trans = mgr.newASPTransactionBuffer();
            pm_no = ctx.readValue("PM_NO","");
            pm_revision = ctx.readValue("PM_REVISION",""); 
            planned_date = ctx.readValue("PLANNED_DATE","");
            wo_no = ctx.readValue("WO_NO","");
            win_cls = ctx.readFlag("WINCLS",win_cls);
            ok_sub = ctx.readFlag("OKSUB",ok_sub);
            secCompetence = ctx.readFlag("SECCOMP",false);
            qrystr = ctx.readValue("QRYSTR",""); 

            if (mgr.commandBarActivated())
                    eval(mgr.commandBarFunction());
            else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
                    validate();
            else if (!mgr.isEmpty(mgr.getQueryStringValue("PMQUALIFICATION")))
            {
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                    pm_no        = mgr.readValue("PM_NO");
                    pm_revision  = mgr.readValue("PM_REVISION");
                    seq_no       = mgr.readValue("SEQ_NO");
                    planned_date = mgr.readValue("PLANNED_DATE");
                    wo_no        = mgr.readValue("WO_NO");
                    okFind();
                    headNo        = ctx.getGlobal("HEADGLOBAL");
                    int headsetNo = Integer.parseInt(headNo);
                    headset.goTo(headsetNo);   
                    okFindItem();
            }
            else 
            {
                    pm_no        = mgr.readValue("PM_NO");
                    pm_revision  = mgr.readValue("PM_REVISION");
                    seq_no       = mgr.readValue("SEQ_NO");
                    planned_date = mgr.readValue("PLANNED_DATE");
                    wo_no        = mgr.readValue("WO_NO");
                    okFind();

            }
            securityChk();
            adjust();

            ctx.writeValue("PM_NO",pm_no);
            ctx.writeValue("PM_REVISION",pm_revision);
            ctx.writeValue("PLANNED_DATE",planned_date);
            ctx.writeValue("WO_NO",wo_no);
            ctx.writeFlag("WINCLS",win_cls);
            ctx.writeFlag("SECCOMP",secCompetence);
            ctx.writeValue("QRYSTR",qrystr); 

	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
            ASPManager mgr = getASPManager();
            trans.clear();
            String row_no = "";
            String item_employee_id = "";
            String item_date_from = "";
            String item_date_to = "";
            String planner= "";
            String emp = "";
            String sModify = new String("TRUE");

            headset.changeRow();

            trans.clear();

            cmd= trans.addCustomFunction("GETFND","Fnd_Session_API.Get_Fnd_User","FND_USER");

            cmd= trans.addCustomFunction("GETPLANNER","Person_Info_API.Get_Id_For_User","PLANNER");
            cmd.addReference("FND_USER","GETFND/DATA");

            cmd = trans.addCustomFunction("OLDEMP","Pm_Action_Calendar_Job_API.Get_Employee_Id","OLD_EMP_ID");
            cmd.addParameter("PM_NO");
            cmd.addParameter("PM_REVISION");
            cmd.addParameter("SEQ_NO");
            cmd.addParameter("JOB_ID");

            cmd = trans.addCustomCommand("UPDATEJOB","Pm_Action_Calendar_Job_API.Update_Emp_Date_From");
            cmd.addParameter("PM_NO");
            cmd.addParameter("PM_REVISION");
            cmd.addParameter("SEQ_NO");
            cmd.addParameter("JOB_ID");
            cmd.addParameter("SIGNATURE");
            cmd.addParameter("EMPLOYEE_ID");
            cmd.addParameter("DATE_FROM");
            cmd.addParameter("DATE_TO");
            cmd.addReference("PLANNER","GETPLANNER/DATA");
            cmd.addParameter("S_MODIFY",sModify);

            trans=mgr.perform(trans);

            emp = trans.getValue("OLDEMP/DATA/OLD_EMP_ID");
            planner = trans.getValue("GETPLANNER/DATA/PLANNER"); 

            headset.refreshAllRows();

            for (int i=0;i<itemset.countRows();i++)
            {
                itemset.goTo(i);

                row_no = itemset.getRow().getValue("ROW_NO");
                item_employee_id = itemset.getRow().getValue("EMPLOYEE_ID");
                double pmen = itemset.getRow().getNumberValue("PLAN_MEN");
                
                if (( !mgr.isEmpty(item_employee_id) && (emp.equals(item_employee_id)) || mgr.isEmpty(item_employee_id)) && ((isNaN(pmen)) || (pmen<=1)))
                {
                    trans.clear();

                    cmd = trans.addCustomCommand("UPDATEOPR","Pm_Action_Calendar_Oper_API.Update_Emp_Date_From");
                    cmd.addParameter("PM_NO");
                    cmd.addParameter("PM_REVISION");
                    cmd.addParameter("SEQ_NO");
                    cmd.addParameter("ROW_NO",row_no);
                    cmd.addParameter("SIGNATURE");
                    cmd.addParameter("EMPLOYEE_ID");
                    cmd.addParameter("ITEM_DATE_FROM");
                    cmd.addParameter("ITEM_DATE_TO");
                    cmd.addParameter("PLAN_MEN");
                    cmd.addParameter("PLAN_HRS");
                    cmd.addParameter("PLANNER",planner);
                    cmd.addParameter("TEAM_CONTRACT");
                    cmd.addParameter("TEAM_ID");
        
                    trans=mgr.perform(trans);
                }
            }

            itemset.refreshAllRows();
        }
        
        public void submitItem()
        {

            ASPManager mgr = getASPManager();
            data = mgr.newASPBuffer();
            String sModify = new String("TRUE");
            String date_to = "";
            String old_date_to = "";
            String planner = "";
            String row_no = itemset.getRow().getValue("ROW_NO");
            String tot_hrs = new String("0");

            itemset.changeRow();
            String curr_row_date_to   = mgr.readValue("ITEM_DATE_TO");
            String curr_row_date_from = mgr.readValue("ITEM_DATE_FROM");
            String sign               = mgr.readValue("ITEM_SIGNATURE");
            String emp_id             = mgr.readValue("ITEM_EMPLOYEE_ID");
            int currrow = itemset.getCurrentRowNo();
            
            trans.clear();
            //Bug 58216 start
            cmd = trans.addQuery("OLDDATEFROM","Select DATE_TO from Pm_Action_Calendar_Oper where PM_NO = ? and PM_REVISION = ? and SEQ_NO = ? and ROW_NO = ?");
            cmd.addParameter("PM_NO",pm_no);
            cmd.addParameter("PM_REVISION",pm_revision);
            cmd.addParameter("SEQ_NO",headset.getRow().getValue("SEQ_NO"));
            cmd.addParameter("ROW_NO",row_no);
            //Bug 58216 end

            cmd= trans.addCustomFunction("GETFND","Fnd_Session_API.Get_Fnd_User","FND_USER");

            cmd= trans.addCustomFunction("GETPLANNER","Person_Info_API.Get_Id_For_User","PLANNER");
            cmd.addReference("FND_USER","GETFND/DATA");

            trans = mgr.perform(trans);

            old_date_to = trans.getBuffer("OLDDATEFROM/DATA").getFieldValue("DATE_TO");
            planner = trans.getValue("GETPLANNER/DATA/PLANNER");

            
            if ((mgr.isEmpty(old_date_to) && !mgr.isEmpty(curr_row_date_to)) || (!mgr.isEmpty(old_date_to) && !old_date_to.equals(curr_row_date_to)))
            
            {
                for (int i=0;i<itemset.countRows();i++)
                {
                    itemset.goTo(i);
                    trans.clear();

                    cmd = trans.addCustomCommand("CALCDATETO","Pm_Action_Calendar_Job_API.Calculate_Date_To");
                    cmd.addParameter("DUMMY_DATE_TO");
                    cmd.addParameter("TOT_HOURS",tot_hrs);
                    cmd.addParameter("DATE_FROM",headset.getRow().getFieldValue("DATE_FROM"));
                    if (i == currrow)
                    {
                        cmd.addParameter("ITEM_DATE_FROM",curr_row_date_from);
                        cmd.addParameter("ITEM_DATE_TO",curr_row_date_to);
                    }
                    else
                    {
                        cmd.addParameter("ITEM_DATE_FROM",itemset.getRow().getFieldValue("ITEM_DATE_FROM"));
                        cmd.addParameter("ITEM_DATE_TO",itemset.getRow().getFieldValue("ITEM_DATE_TO"));
                    }

                    trans = mgr.validate(trans);

                    date_to = trans.getBuffer("CALCDATETO/DATA").getFieldValue("DUMMY_DATE_TO");
                    tot_hrs = trans.getBuffer("CALCDATETO/DATA").getFieldValue("TOT_HOURS");
                }
            }
            else
                date_to = itemset.getRow().getFieldValue("DATE_TO");

            trans.clear();
            cmd = trans.addCustomCommand("UPDATEJOB","Pm_Action_Calendar_Job_API.Update_Emp_Date_From");
            cmd.addParameter("PM_NO");
            cmd.addParameter("PM_REVISION");
            cmd.addParameter("SEQ_NO");
            cmd.addParameter("JOB_ID");
            cmd.addParameter("SIGNATURE");
            cmd.addParameter("EMPLOYEE_ID");
            cmd.addParameter("DATE_FROM");
            cmd.addParameter("DATE_TO",date_to);
            cmd.addParameter("PLANNER",planner);
            cmd.addParameter("S_MODIFY",sModify);

            trans=mgr.perform(trans);

            headset.refreshAllRows();
            itemset.goTo(currrow);

            trans.clear();

            cmd = trans.addCustomCommand("UPDATEOPR","Pm_Action_Calendar_Oper_API.Update_Emp_Date_From");
            cmd.addParameter("ITEM_PM_NO");
            cmd.addParameter("ITEM_PM_REVISION");
            cmd.addParameter("ITEM_SEQ_NO");
            cmd.addParameter("ROW_NO",row_no);
            cmd.addParameter("ITEM_SIGNATURE",sign);
            cmd.addParameter("ITEM_EMPLOYEE_ID",emp_id);
            cmd.addParameter("ITEM_DATE_FROM",curr_row_date_from);
            cmd.addParameter("ITEM_DATE_TO",curr_row_date_to);
            cmd.addParameter("PLAN_MEN");
            cmd.addParameter("PLAN_HRS");
            cmd.addParameter("PLANNER",planner);
            cmd.addParameter("TEAM_CONTRACT");
            cmd.addParameter("TEAM_ID");

            trans=mgr.perform(trans);

            itemset.refreshAllRows();
        }

        public void additionalQualifications()
        {
           ASPManager mgr = getASPManager();
           int head_current = headset.getCurrentRowNo();   
           String s_head_curr = String.valueOf(head_current);
           ctx.setGlobal("HEADGLOBAL", s_head_curr);
           if (itemlay.isMultirowLayout())
              itemset.goTo(itemset.getRowSelected());
        
           bOpenNewWindow = true;
           urlString = "PMQualificationsDlg.page?RES_TYPE=MAINTQUALIFICATIONS"+
                       "&PM_NO="+ mgr.URLEncode(mgr.isEmpty(itemset.getValue("PM_NO"))?"":itemset.getValue("PM_NO")) +
                       "&PM_REVISION="+ mgr.URLEncode(mgr.isEmpty(itemset.getValue("PM_REVISION"))?"":itemset.getValue("PM_REVISION")) +
                       "&SEQ_NO="+ mgr.URLEncode(mgr.isEmpty(itemset.getValue("SEQ_NO"))?"":itemset.getValue("SEQ_NO")) +
                       "&ROW_NO="+ mgr.URLEncode(mgr.isEmpty(itemset.getValue("ROW_NO"))?"":itemset.getValue("ROW_NO")) +
                       "&PLANNED_DATE=" + mgr.URLEncode(planned_date)+
                       "&WO_NO=" + mgr.URLEncode(wo_no)+
                       "&QRYSTR=" + mgr.URLEncode(qrystr) +
                       "&FRMNAME=JobsOperations";
           newWinHandle = "JobsOperations"; 
        }

        public void showAllOperations()
         {
            ASPManager mgr = getASPManager();
            int head_current = headset.getCurrentRowNo();   
            String s_head_curr = String.valueOf(head_current);
            ctx.setGlobal("HEADGLOBAL", s_head_curr);
            if (itemlay.isMultirowLayout())
               itemset.goTo(itemset.getRowSelected());
         
            bOpenNewWindow = true;
            urlString = "OperationsOvw.page?RES_TYPE=MAINTOVWOPERATIONS"+
                        "&PM_NO="+ mgr.URLEncode(mgr.isEmpty(headset.getValue("PM_NO"))?"":headset.getValue("PM_NO")) +
                        "&PM_REVISION="+ mgr.URLEncode(mgr.isEmpty(headset.getValue("PM_REVISION"))?"":headset.getValue("PM_REVISION")) +
                        "&SEQ_NO="+ mgr.URLEncode(mgr.isEmpty(headset.getValue("SEQ_NO"))?"":headset.getValue("SEQ_NO")) +
                        "&PLANNED_DATE=" + mgr.URLEncode(planned_date)+
                        "&WO_NO=" + mgr.URLEncode(wo_no)+
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&FRMNAME=JobsOperations";
            newWinHandle = "JobsOperations"; 
        }
       
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();
                trans.clear();

                q = trans.addQuery(headblk);
                //Bug 58216 start
	        q.addWhereCondition("PM_NO = ?");
		q.addWhereCondition("PM_REVISION = ?");
                q.addWhereCondition("SEQ_NO = ?");
                q.addParameter("PM_NO",pm_no);
                q.addParameter("PM_REVISION",pm_revision);
                q.addParameter("SEQ_NO",seq_no);
                //Bug 58216 end
                q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
                else if (headset.countRows()>0 && headlay.isSingleLayout())
                {
                    okFindItem();
                }
                qrystr = mgr.createSearchURL(headblk);       
	}

        public final void okFindItem()
            {
                ASPManager mgr = getASPManager();
                trans.clear();
		int currrow = headset.getCurrentRowNo();  
                q = trans.addQuery(itemblk);
                //Bug 58216 start
		q.addWhereCondition("PM_NO = ?");
	        q.addWhereCondition("PM_REVISION = ?");
                q.addWhereCondition("SEQ_NO = ?");
                q.addWhereCondition("JOB_ID = ?");
                q.addParameter("PM_NO",headset.getValue("PM_NO"));
                q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
                q.addParameter("SEQ_NO",headset.getValue("SEQ_NO"));
                q.addParameter("JOB_ID",headset.getValue("JOB_ID"));
                //Bug 58216 end
                q.includeMeta("ALL");
        
		mgr.submit(trans);
                headset.goTo(currrow);
        }

        //-----------------------------------------------------------------------------
       //-----------------------------  ADJUST FUNCTION  ---------------------------
       //-----------------------------------------------------------------------------

        public void adjust()
        {
                ASPManager mgr = getASPManager();

                if (!"null".equals(wo_no))
                {
                    headbar.disableCommand(headbar.EDITROW);
                    itembar.disableCommand(itembar.EDITROW);
                }
                else
                {
                    headbar.enableCommand(headbar.EDITROW);
                    itembar.enableCommand(itembar.EDITROW);
                }

                if (!secCompetence)
	           {
                        itembar.removeCustomCommand("additionalQualifications");
                        
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

                    String new_sign = mgr.readValue("SIGNATURE","");

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
                            cmd.addParameter("COMPANY");
                            cmd.addParameter("SIGNATURE");
                            trans = mgr.validate(trans);

                            sign = mgr.readValue("SIGNATURE");
                            emp_id = trans.getValue("EMP/DATA/EMPLOYEE_ID");

                    }

                    txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
                    mgr.responseWrite(txt);
                }
                else if ("ITEM_SIGNATURE".equals(val))
                {
                    String reqstr = null;
                    int startpos = 0;
                    int endpos = 0;
                    int i = 0;
                    String ar[] = new String[2];
                    String emp_id = "";
                    String sign = "";
                    double plan_men = 0;

                    String new_sign = mgr.readValue("ITEM_SIGNATURE","");
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
                            cmd.addParameter("ITEM_SIGNATURE");
                            trans = mgr.validate(trans);

                            sign = mgr.readValue("ITEM_SIGNATURE");
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
               else if ("ITEM_DATE_FROM".equals(val))
                {
                     cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","ITEM_DATE_TO");
                     cmd.addParameter("ITEM_PM_NO");
                     cmd.addParameter("ITEM_PM_REVISION");
                     cmd.addParameter("ITEM_SEQ_NO");
                     cmd.addParameter("ITEM_JOB_ID");
                     cmd.addParameter("ITEM_DATE_FROM");
                     cmd.addParameter("PLAN_MEN");
                     cmd.addParameter("PLAN_HRS");

                     trans = mgr.validate(trans);
                     String date_to = trans.getBuffer("GETITEMDATETO/DATA").getFieldValue("DATE_TO");
                     txt = (mgr.isEmpty(date_to)?"":date_to) + "^";
                     mgr.responseWrite(txt);
                }

                else if ("PLAN_HRS".equals(val))
                {
                  String dDateFrom = mgr.readValue("ITEM_DATE_FROM");

                  if (!mgr.isEmpty(dDateFrom)) {
                       cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","DATE_TO");
                       cmd.addParameter("ITEM_PM_NO");
                       cmd.addParameter("ITEM_PM_REVISION");
                       cmd.addParameter("ITEM_SEQ_NO");
                       cmd.addParameter("ROW_NO");
                       cmd.addParameter("ITEM_DATE_FROM");
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
                 String dDateFrom = mgr.readValue("ITEM_DATE_FROM");

                 if (!mgr.isEmpty(dDateFrom)) {
                      cmd = trans.addCustomFunction("GETITEMDATETO","Pm_Action_Calendar_Oper_API.Calculate_Date_To","DATE_TO");
                      cmd.addParameter("ITEM_PM_NO");
                      cmd.addParameter("ITEM_PM_REVISION");
                      cmd.addParameter("ITEM_SEQ_NO");
                      cmd.addParameter("ROW_NO");
                      cmd.addParameter("ITEM_DATE_FROM");
                      cmd.addParameter("PLAN_MEN");
                      cmd.addParameter("PLAN_HRS");


                      trans = mgr.validate(trans);
                      String date_to = trans.getBuffer("GETITEMDATETO/DATA").getFieldValue("DATE_TO");
                      txt = (mgr.isEmpty(date_to)?"":date_to) + "^";
                      mgr.responseWrite(txt);
                 }
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


         public void securityChk()
	 {
	       ASPManager mgr = getASPManager();

	       
			trans.clear();

			trans.addSecurityQuery("COMPETENCY_GROUP_LOV1");
                        trans = mgr.perform(trans);

			SecBuff = trans.getSecurityInfo();
                        if (SecBuff.itemExists("COMPETENCY_GROUP_LOV1"))
                                secCompetence = true;

                
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
		f.setLabel("PCMWJOBSOPRDLGPMNO: PM No");
                f.setHidden();

                f = headblk.addField("PM_REVISION");
		f.setLabel("PCMWJOBSOPRDLGREVISION: Pm Revision");
                f.setHidden();

                f = headblk.addField("SEQ_NO");
		f.setLabel("PCMWJOBSOPRDLGSEQNO: Seq No");
                f.setHidden();

                f = headblk.addField("JOB_ID");
		f.setLabel("PCMWJOBSOPRDLGJOBID: Job Id");
                f.setReadOnly();

                f = headblk.addField("STD_JOB_ID");
		f.setLabel("PCMWJOBSOPRDLGSTDJOBID: Standard Job Id");
                f.setReadOnly();
                f.setFunction("Pm_Action_Job_API.Get_Std_Job_Id(:PM_NO,:PM_REVISION,:JOB_ID)");

                f = headblk.addField("CONTRACT");
		f.setLabel("PCMWJOBSOPRDLGCONTRACT: Site");
                f.setReadOnly();
                f.setFunction("Pm_Action_Job_API.Get_Std_Job_Contract(:PM_NO,:PM_REVISION,:JOB_ID)");

                f = headblk.addField("STD_JOB_REVISION");
		f.setLabel("PCMWJOBSOPRDLGSTDJOBREVISION: Revision");
                f.setReadOnly();
                f.setFunction("Pm_Action_Job_API.Get_Std_Job_Revision(:PM_NO,:PM_REVISION,:JOB_ID)");

                f = headblk.addField("DESCRIPTION");
		f.setLabel("PCMWJOBSOPRDLGDESCRIPTION: Description");
                f.setReadOnly();
                f.setFunction("Pm_Action_Job_API.Get_Description(:PM_NO,:PM_REVISION,:JOB_ID)");

                f = headblk.addField("QTY");
		f.setLabel("PCMWJOBSOPRDLGQTY: Quantity");
                f.setReadOnly();
                f.setFunction("Pm_Action_Job_API.Get_Qty(:PM_NO,:PM_REVISION,:JOB_ID)");

                f = headblk.addField("SIGNATURE");
		f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
		f.setLabel("PCMWJOBSOPRDLGEXECUTEDBY: Executed By");
                f.setUpperCase();
                f.setCustomValidation("COMPANY,SIGNATURE","EMPLOYEE_ID,SIGNATURE");

                f = headblk.addField("EMPLOYEE_ID");
		f.setLabel("PCMWJOBSOPRDLGEMPID: Employee Id");
                f.setUpperCase();
                f.setReadOnly();
                
                f = headblk.addField("DATE_FROM","Datetime");
		f.setLabel("PCMWJOBSOPRDLGDATEFROM: Date From");
                f.setReadOnly();
                
                f = headblk.addField("DATE_TO","Datetime");
		f.setLabel("PCMWJOBSOPRDLGDATETO: Date To");

                f = headblk.addField("PLANNER");
                f.setFunction("Company_Emp_API.Get_Person_Id(:COMPANY,Pm_Action_API.Get_Sign_Id(:PM_NO,:PM_REVISION))");
                f.setUpperCase();
                f.setHidden();   

                f= headblk.addField("OLD_EMP_ID");
                f.setHidden();
                f.setFunction("''");

                f= headblk.addField("S_MODIFY");
                f.setHidden();
                f.setFunction("''"); 

                f= headblk.addField("DUMMY_DATE_TO","Datetime");
                f.setHidden();
                f.setFunction("''");

                f= headblk.addField("TOT_HOURS");
                f.setHidden();
                f.setFunction("''");

                f= headblk.addField("FND_USER");
                f.setHidden();
                f.setFunction("''");

                headblk.setView("PM_ACTION_CALENDAR_JOB");
                headblk.defineCommand("PM_ACTION_CALENDAR_JOB_API","Modify__");
		headset = headblk.getASPRowSet();
                
		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();

                headtbl.setWrap();
                headtbl.enableRowSelect();

                headblk.disableDocMan();
                headbar.disableCommand(headbar.FIND);
                headbar.addCustomCommand("showAllOperations",mgr.translate("PCMWJOBSOPERATIONSDLGSHOWALLQUAL: Show All Operations..."));

		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

                //====================================================================
                //  Operations
                //====================================================================

                itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("ITEM_OBJID");
		f.setHidden();
                f.setDbName("OBJID");

		f = itemblk.addField("ITEM_OBJVERSION");
		f.setHidden();
                f.setDbName("OBJVERSION");

		f = itemblk.addField("ITEM_PM_NO","Number","#");
		f.setLabel("PCMWPMNO: PM No");
                f.setHidden();
		f.setDbName("PM_NO");

                f = itemblk.addField("ITEM_PM_REVISION");
		f.setLabel("PCMWPMREVISION: PM Revision");
                f.setHidden();
		f.setDbName("PM_REVISION");

                f = itemblk.addField("ITEM_SEQ_NO");
		f.setLabel("PCMWSEQNO: Seq No");
                f.setDbName("SEQ_NO");
                f.setHidden();

                f = itemblk.addField("ITEM_JOB_ID");
		f.setLabel("PCMWJOBID: Job Id");
                f.setHidden();
		f.setDbName("JOB_ID");

                f = itemblk.addField("ROW_NO");
		f.setLabel("PCMWROWNO: Operation No");
                f.setReadOnly();

                f = itemblk.addField("ROW_DESCR");
		f.setLabel("PCMWJOBSOPERATIONSDLGDESC: Description");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Descr(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");

                f = itemblk.addField("ITEM_SIGNATURE");
		f.setLabel("PCMWJOBSOPERATIONSDLGEXECUTEDBY: Executed By");
		f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
                f.setDbName("SIGNATURE");
                f.setUpperCase();
                f.setCustomValidation("COMPANY,ITEM_SIGNATURE","ITEM_EMPLOYEE_ID,ITEM_SIGNATURE");

                f = itemblk.addField("ITEM_EMPLOYEE_ID");
		f.setLabel("PCMWJOBSOPERATIONSDLGEMPID: Employee Id");
                f.setDbName("EMPLOYEE_ID");
                f.setUpperCase(); 
                f.setReadOnly();

                f = itemblk.addField("PLAN_MEN","Number");
		f.setLabel("PCMWJOBSOPERATIONSDLGPLANMEN: Planned Men");
                f.setCustomValidation("ITEM_PM_NO,ITEM_PM_REVISION,ITEM_SEQ_NO,ITEM_JOB_ID,ITEM_DATE_FROM,PLAN_MEN,PLAN_HRS","ITEM_DATE_TO");
                
                f = itemblk.addField("PLAN_HRS");
		f.setLabel("PCMWJOBSOPERATIONSDLGPLANHRS: Planned Hours");
                f.setCustomValidation("ITEM_PM_NO,ITEM_PM_REVISION,ITEM_SEQ_NO,ITEM_JOB_ID,ITEM_DATE_FROM,PLAN_MEN,PLAN_HRS","ITEM_DATE_TO");

                f = itemblk.addField("TEAM_CONTRACT");
                f.setSize(7);
                f.setDynamicLOV("USER_ALLOWED_SITE_LOV","COMPANY",600,450);
                f.setLabel("PCMWJOBSOPERATIONSDLGCONT: Team Site");
                f.setMaxLength(5);
                f.setUpperCase();

                f = itemblk.addField("TEAM_ID");
                f.setSize(13);
                f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC");
                f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450);
                f.setMaxLength(20);
                f.setLabel("PCMWJOBSOPERATIONSDLGTID: Team ID");
                f.setUpperCase();

                f= itemblk.addField("TEAMDESC");
                f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
                f.setSize(40);
                f.setMaxLength(200);
                f.setReadOnly();
                f.setLabel("PCMWJOBSOPERATIONSDLGDESC: Description");

                f = itemblk.addField("ITEM_DATE_FROM","Datetime");
		f.setLabel("PCMWJOBSOPERATIONSDLGDATEFROM: Date From");
                f.setDbName("DATE_FROM");
                f.setCustomValidation("ITEM_PM_NO,ITEM_PM_REVISION,ITEM_SEQ_NO,ITEM_JOB_ID,ITEM_DATE_FROM,PLAN_MEN,PLAN_HRS","ITEM_DATE_TO");

                f = itemblk.addField("ITEM_DATE_TO","Datetime");
		f.setLabel("PCMWJOBSOPERATIONSDLGDATETO: Date To");
                f.setDbName("DATE_TO");

                f = itemblk.addField("PREDECESSOR");
                f.setSize(22);
                f.setLabel("PCMWJOBSOPERATIONSDLGPRED: Predecessors");
                f.setFunction("Pm_Role_Dependencies_API.Get_Predecessors(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");
                f.setReadOnly();

                f = itemblk.addField("ORG_CODE");
		f.setLabel("PCMWJOBSOPERATIONSDLGORGCODE: Maint.Org.");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Org_Code(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");

                f = itemblk.addField("ORG_CODE_SITE");
		f.setLabel("PCMWJOBSOPERATIONSDLGORGCODESITE: Maint.Org. Site");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Org_Contract(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");

                f = itemblk.addField("ROLE_CODE");
		f.setLabel("PCMWJOBSOPERATIONSDLGROLECODE: Craft Id");
                f.setReadOnly();
                f.setFunction("Pm_Action_Role_API.Get_Role_Code(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");

                f = itemblk.addField("CBADDQUALIFICATION");
                f.setLabel("PCMWJOBSOPERATIONSDLGCBADDQUAL: Additional Qualifications");
                f.setFunction("Pm_Action_Role_API.Check_Qualifications_Exist(:ITEM_PM_NO,:ITEM_PM_REVISION,:ROW_NO)");
                f.setCheckBox("0,1");
                f.setReadOnly();

                itemblk.setView("PM_ACTION_CALENDAR_OPER");
                itemblk.defineCommand("PM_ACTION_CALENDAR_OPER_API","Modify__");
		itemset = itemblk.getASPRowSet();

                itemblk.setMasterBlock(headblk);

		itembar = mgr.newASPCommandBar(itemblk);
		itemtbl = mgr.newASPTable(itemblk);
		itemlay = itemblk.getASPBlockLayout();
                
                itemtbl.setWrap();
                itemtbl.enableRowSelect();

                itemblk.disableDocMan();
                itembar.disableCommand(itembar.FIND);

                itembar.defineCommand(itembar.OKFIND,"okFindItem");
                itembar.defineCommand(itembar.SAVERETURN,"submitItem","checkItemFields(-1)");
                itembar.addCustomCommand("additionalQualifications",mgr.translate("PCMWJOBSOPERATIONSDLGADDQUAL: Additional Qualifications..."));
                itembar.removeFromMultirowAction("additionalQualifications");

		itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWJOBSOPERATIONSDLGDESCJOBSOPS: Jobs and Operations for PM - "+pm_no+", Revision - "+pm_revision+", Planned Date - "+planned_date+"";
	}

	protected String getTitle()
	{
		return "PCMWJOBSOPERATIONSDLGTITJOBSOPS: Jobs and Operations for PM - "+pm_no+", Revision - "+pm_revision+", Planned Date - "+planned_date+"";
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
                if (headset.countRows()>0 && headlay.isSingleLayout())
                {
                    appendToHTML(itemlay.show());
                }
                appendDirtyJavaScript("window.name = \"JobsOperations\";\n");  
                appendDirtyJavaScript("function lovItemSignature(i,params)\n");
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
                appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('ITEM_SIGNATURE',i).indexOf('%') !=-1)? getValue_('ITEM_SIGNATURE',i):'';\n");
                appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
                appendDirtyJavaScript("       {\n");
                appendDirtyJavaScript("              openLOVWindow('ITEM_SIGNATURE',i,\n");
                appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_SIGNATURE',i))\n"); 
                appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
                appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
                appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ORG_CODE_SITE',i))\n");       
                appendDirtyJavaScript("       	   ,550,500,'validateItemSignature');\n");
                appendDirtyJavaScript("       }\n");
                appendDirtyJavaScript(" else\n");
                appendDirtyJavaScript("       {\n");        
                appendDirtyJavaScript("              openLOVWindow('ITEM_SIGNATURE',i,\n");
                appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_SIGNATURE',i))\n"); 
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
                appendDirtyJavaScript("	whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.ITEM_DATE_FROM.value)+\"','"+mgr.encodeStringForJavascript(sOracleFormat)+"'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); //XSS_Safe AMNILK 20070717
                appendDirtyJavaScript(" if(document.form.ITEM_EMPLOYEE_ID.value != '')\n");
                appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM_EMPLOYEE_ID.value)+\"' \";\n"); 
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
                appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n"); 
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
                appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
                appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
                appendDirtyJavaScript("'");
		appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
		appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
		appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
                appendDirtyJavaScript("}\n");
                     
                if (bOpenNewWindow)
                {
                    appendDirtyJavaScript("  window.open(\"");
                    appendDirtyJavaScript(urlString);
                    appendDirtyJavaScript("\",\"");      
                    appendDirtyJavaScript(newWinHandle);
                    appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
                   

                   
                }



	}
}
