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
*  File        : JobProgram.java 
*  Created     : 041125 NIJALK  (AMEC112 - Job program)
*  Modified    :
*  041201 CHANLK  Add the RMB copy job program.
*  041220 NIJALK  Modified predefine().
*  041229 NIJALK  Call 120861: Modified predefine(),validate().
*  050223 NIJALK  Modified run(), createNewRevision().
*  050329 NIJALK  Added method addJobProgramToObject().
*  060315 DiAmlk  Bug ID:136881 - Modified the method newRowITEM.
*  060315 NIJALK  Bug ID:137420 - Modified perform(), Added saveReturn().
*  060329 NIJALK  Bug 137420: Modified okFind().
*  060815 DIAMLK  Bug 58216, Eliminated SQL Injection security vulnerability.
*  060906 AMNILK  Merged Bug Id: 58216.
*  070717 AMNILK  Eliminated XSS Security Vulnerability.
*  070731 AMDILK  Removed the scroll buttons of the parent when the detail blocks are in new or edit mode
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class JobProgram extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobProgram");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPBuffer SecBuff;
    private int currrow;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPField f;

    private boolean bOpenNewWindow;
    private boolean secJobCat;
    private boolean varSec;
    private String urlString;
    private String newWinHandle;
    private String qrystr;
    private String val = "";
    private String txt = "";
    private int newWinHeight = 600;
    private int newWinWidth = 900;

    private boolean CopyObsoleteJob = false;
    private String sWarningMsg = "";

    //===============================================================
    // Construction 
    //===============================================================
    public JobProgram(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        secJobCat = ctx.readFlag("SECJOBCAT",false);
        varSec = ctx.readFlag("VARSEC",false);
        qrystr = ctx.readValue("QRYSTR", "");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("REVCRT")))
        {
            int i = 0;
            String new_job_program_id = mgr.readValue("NEW_JOB_PROGRAM_ID");
            String new_revision = mgr.readValue("NEW_REV");
            String new_contract = mgr.readValue("CONTRACT");

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();

            if (headset.countRows()>0)
            {
                headset.first();
                while (i == 0)
                {
                    if (headset.getRow().getValue("JOB_PROGRAM_ID").equals(new_job_program_id) && headset.getRow().getValue("JOB_PROGRAM_REVISION").equals(new_revision) && headset.getRow().getValue("JOB_PROGRAM_CONTRACT").equals(new_contract))
                        i = 1;
                    else
                        headset.next();
                }
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("ADDSTDJOB")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();

            String curRow = ctx.getGlobal("CURRROW");
            int curRowInt = Integer.parseInt(curRow);

            headset.goTo(curRowInt);

            if (headset.countRows() != 1)
                    okFindITEM();
        }

        securityChk();
        adjust();

        ctx.writeFlag("SECJOBCAT",secJobCat);
        ctx.writeFlag("VARSEC",varSec);
        ctx.writeValue("QRYSTR", qrystr);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        val = mgr.readValue("VALIDATE");

        if ("PROGRAM_TYPE_ID".equals(val))
        {
            String sProgramType = mgr.readValue("PROGRAM_TYPE_ID");
            String sProgramDesc = ""; 

            trans.clear();
            cmd = trans.addCustomFunction("GETDESC","PROGRAM_TYPE_API.Get_Description","PROGRAM_TYPE_DESC");
            cmd.addParameter("PROGRAM_TYPE_ID",sProgramType);
            trans = mgr.validate(trans);

            sProgramDesc = trans.getValue("GETDESC/DATA/PROGRAM_TYPE_DESC");

            txt = (mgr.isEmpty(sProgramType) ? "" : (sProgramType))+ "^" +
                  (mgr.isEmpty(sProgramDesc) ? "" : (sProgramDesc))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("STD_JOB_ID".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRevision = "";
            String sContract = mgr.readValue("STD_JOB_CONTRACT");
            String sWorkDescr = "";
            String sStatus = "";
            String sDefinition = "";
            String sType = "";
            String sTypeId = "";
            String sTypeDescr = "";

            String new_std_job_id = mgr.readValue("STD_JOB_ID","");

            if (new_std_job_id.indexOf("~",0)>0)
            {
                String [] attrarr =  new_std_job_id.split("~");

                sStdJobId = attrarr[0];
                sStdJobRevision = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("STD_JOB_ID");
                sStdJobRevision = mgr.readValue("STD_JOB_REVISION");
            }

            if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobRevision) && !mgr.isEmpty(sContract))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETWORKDESCR","STANDARD_JOB_API.Get_Work_Description","STD_JOB_WORK_DESC");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETDEFINITION","STANDARD_JOB_API.Get_Definition","STD_JOB_DEFINITION");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETSTATUS","STANDARD_JOB_API.Get_Status","STD_JOB_STATUS");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETTYPE","STANDARD_JOB_API.Get_Standard_Job_Type","STD_JOB_TYPE");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);

                cmd = trans.addCustomFunction("GETTYPEID","STANDARD_JOB_API.Get_Type_Id","TYPE_ID");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","TYPE_DESCR");
                cmd.addReference("TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sWorkDescr  = trans.getValue("GETWORKDESCR/DATA/STD_JOB_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/STD_JOB_DEFINITION");
                sStatus     = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");
                sType       = trans.getValue("GETTYPE/DATA/STD_JOB_TYPE");
                sTypeId     = trans.getValue("GETTYPEID/DATA/TYPE_ID");
                sTypeDescr  = trans.getValue("GETTYPEDESCR/DATA/TYPE_DESCR");
            }

            txt = (mgr.isEmpty(sStdJobId) ? "" : (sStdJobId))+ "^" +
                  (mgr.isEmpty(sStdJobRevision) ? "" : (sStdJobRevision))+ "^" +
                  (mgr.isEmpty(sWorkDescr) ? "" : (sWorkDescr))+ "^" +
                  (mgr.isEmpty(sDefinition) ? "" : (sDefinition))+ "^" +
                  (mgr.isEmpty(sStatus) ? "" : (sStatus))+ "^" +  
                  (mgr.isEmpty(sType) ? "" : (sType))+ "^" +
                  (mgr.isEmpty(sTypeId) ? "" : (sTypeId))+ "^" +
                  (mgr.isEmpty(sTypeDescr) ? "" : (sTypeDescr))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("STD_JOB_REVISION".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRevision = "";
            String sContract = mgr.readValue("STD_JOB_CONTRACT");
            String sWorkDescr = "";
            String sStatus = "";
            String sDefinition = "";
            String sType = "";
            String sTypeId = "";
            String sTypeDescr = "";

            String new_std_job_id = mgr.readValue("STD_JOB_REVISION","");

            if (new_std_job_id.indexOf("~",0)>0)
            {
                String [] attrarr =  new_std_job_id.split("~");

                sStdJobId = attrarr[0];
                sStdJobRevision = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("STD_JOB_ID");
                sStdJobRevision = mgr.readValue("STD_JOB_REVISION");
            }

            if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobRevision) && !mgr.isEmpty(sContract))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETWORKDESCR","STANDARD_JOB_API.Get_Work_Description","STD_JOB_WORK_DESC");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETDEFINITION","STANDARD_JOB_API.Get_Definition","STD_JOB_DEFINITION");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETSTATUS","STANDARD_JOB_API.Get_Status","STD_JOB_STATUS");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETTYPE","STANDARD_JOB_API.Get_Standard_Job_Type","STD_JOB_TYPE");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);

                cmd = trans.addCustomFunction("GETTYPEID","STANDARD_JOB_API.Get_Type_Id","TYPE_ID");
                cmd.addParameter("STD_JOB_ID",sStdJobId);
                cmd.addParameter("STD_JOB_CONTRACT",sContract);
                cmd.addParameter("STD_JOB_REVISION",sStdJobRevision);

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","TYPE_DESCR");
                cmd.addReference("TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sWorkDescr  = trans.getValue("GETWORKDESCR/DATA/STD_JOB_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/STD_JOB_DEFINITION");
                sStatus     = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");
                sType       = trans.getValue("GETTYPE/DATA/STD_JOB_TYPE");
                sTypeId     = trans.getValue("GETTYPEID/DATA/TYPE_ID");
                sTypeDescr  = trans.getValue("GETTYPEDESCR/DATA/TYPE_DESCR");
            }

            txt = (mgr.isEmpty(sStdJobId) ? "" : (sStdJobId))+ "^" +
                  (mgr.isEmpty(sStdJobRevision) ? "" : (sStdJobRevision))+ "^" +
                  (mgr.isEmpty(sWorkDescr) ? "" : (sWorkDescr))+ "^" +
                  (mgr.isEmpty(sDefinition) ? "" : (sDefinition))+ "^" +
                  (mgr.isEmpty(sStatus) ? "" : (sStatus))+ "^" +  
                  (mgr.isEmpty(sType) ? "" : (sType))+ "^" +
                  (mgr.isEmpty(sTypeId) ? "" : (sTypeId))+ "^" +
                  (mgr.isEmpty(sTypeDescr) ? "" : (sTypeDescr))+ "^" ;

            mgr.responseWrite(txt);
        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer data = null;

        cmd = trans.addEmptyCommand("HEAD","JOB_PROGRAM_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }  

    public void newRowITEM()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer data = null;
            
        cmd = trans.addEmptyCommand("ITEM","STANDARD_JOB_PROGRAM_API.New__",itemblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM/DATA");
        data.setFieldItem("STD_JOB_CONTRACT",headset.getRow().getFieldValue("JOB_PROGRAM_CONTRACT"));
        //Setting Master key values to the child...
        data.setFieldItem("ITEM_JOB_PROGRAM_ID",headset.getRow().getValue("JOB_PROGRAM_ID"));
        data.setFieldItem("ITEM_JOB_PROGRAM_REVISION",headset.getRow().getValue("JOB_PROGRAM_REVISION"));
        data.setFieldItem("ITEM_JOB_PROGRAM_CONTRACT",headset.getRow().getValue("JOB_PROGRAM_CONTRACT"));
        itemset.addRow(data);
    }

    public void saveReturn()
    {
            ASPManager mgr = getASPManager();
            headset.changeRow();
            int currrow = headset.getCurrentRowNo();
            mgr.submit(trans);
            headset.goTo(currrow);
            headlay.setLayoutMode(headlay.getHistoryMode());  

            // Prepare Query String
            if (mgr.isEmpty(qrystr))
                qrystr = mgr.getURL()+"?SEARCH=Y&JOB_PROGRAM_ID="+headset.getRow().getValue("JOB_PROGRAM_ID");
            else
            {
                int start = qrystr.indexOf("JOB_PROGRAM_ID");
                if (start > 0)
                {
                    String subStr1 = qrystr.substring(start);
                    int end = subStr1.indexOf("&");
                    String subStr2;
                    if (end > 0)
                        subStr2 = subStr1.substring(0,end);
                    else
                        subStr2 = subStr1;
                    String subStr3 = subStr2+";"+headset.getRow().getValue("JOB_PROGRAM_ID");
                    qrystr = qrystr.replaceAll(subStr2,subStr3);
                }
                else
                    qrystr = qrystr+";JOB_PROGRAM_ID="+headset.getRow().getValue("JOB_PROGRAM_ID");
            }
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
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }  


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.addWhereCondition("JOB_PROGRAM_CONTRACT IN (Select User_Allowed_Site_API.Authorized(JOB_PROGRAM_CONTRACT) from DUAL)");
        q.setOrderByClause("JOB_PROGRAM_ID");
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 1)
            headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWJOBPROGRAMNODATA: No data found."));
        }
        else if (headset.countRows()>0 && headlay.isSingleLayout())
        {
            okFindITEM();
        }

        qrystr = mgr.createSearchURL(headblk);
    } 

    public void countFindITEM()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
    }  

    public void okFindITEM()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            int currrow = headset.getCurrentRowNo();  

            trans.clear();
            q = trans.addQuery(itemblk);
            //Bug 58216 start
            q.addWhereCondition("JOB_PROGRAM_ID = ?");
            q.addWhereCondition("JOB_PROGRAM_REVISION = ?");
            q.addWhereCondition("JOB_PROGRAM_CONTRACT = ?");
            q.addParameter("JOB_PROGRAM_ID",headset.getValue("JOB_PROGRAM_ID"));
            q.addParameter("JOB_PROGRAM_REVISION",headset.getValue("JOB_PROGRAM_REVISION"));
            q.addParameter("JOB_PROGRAM_CONTRACT",headset.getValue("JOB_PROGRAM_CONTRACT"));
            //Bug 58216 end
            q.setOrderByClause("STD_JOB_ID");
            q.includeMeta("ALL");

            mgr.submit(trans);
            headset.goTo(currrow);
        }
    } 

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void activate()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        trans.clear();
        cmd = trans.addCustomFunction("GETREC","Job_Program_API.Set_Active_To_Obsolete","NO_OF_RECORDS");
        cmd.addParameter("JOB_PROGRAM_ID",headset.getValue("JOB_PROGRAM_ID"));
        cmd.addParameter("JOB_PROGRAM_REVISION",headset.getValue("JOB_PROGRAM_REVISION"));
        cmd.addParameter("JOB_PROGRAM_CONTRACT",headset.getValue("JOB_PROGRAM_CONTRACT"));
        trans = mgr.perform(trans);

        double no_of_recs = trans.getNumberValue("GETREC/DATA/NO_OF_RECORDS");

        if ((!isNaN(no_of_recs)) && (no_of_recs>0))
        {
            mgr.showAlert(mgr.translate("PCMWJOBPROGRAMSETOBSOLETE: Previous 'Active' Revision for Job Program '"+headset.getValue("JOB_PROGRAM_ID")+"' will be set to 'Obsolete'"));
        }

        perform("ACTIVE__");
    }

    public void setObsolete()
    {
        ASPManager mgr = getASPManager();

        perform("OBSOLETE__");
    }

    public void perform(String command)
    {
        ASPManager mgr = getASPManager();
        int currrow = headset.getCurrentRowNo();

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
            mgr.submit(trans);
        }

        if (headset.countRows()>0)
            headset.refreshAllRows();

        headset.goTo(currrow);
    }

    public void addStdJobsByCategory()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        int currow = headset.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "StdJobsByCategoryDlg.page?JOB_PROGRAM_ID=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_ID")) +
                    "&JOB_PROGRAM_REVISION=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_REVISION"))+
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_CONTRACT"))+
                    "&FRMNAME=" + mgr.URLEncode("jobProgram") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) ;

        newWinHandle = "StdJobsByCategoryDlg"; 
        ctx.setGlobal("CURRROW",String.valueOf(currow));
    }


    public void copyJobPorgram()
    {

        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        int currrow = headset.getCurrentRowNo();

        if (headset.getRow().getValue("OBJSTATE").equals("Obsolete"))
        {
            CopyObsoleteJob = true;
            sWarningMsg = hasActiveRevision(headset.getValue("JOB_PROGRAM_ID"), headset.getValue("JOB_PROGRAM_CONTRACT"));
        }
        else
            CopyObsoleteJob = false;

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "CopyJobProgramDlg.page?JOB_PROGRAM_ID="+mgr.URLEncode(headset.getRow().getValue("JOB_PROGRAM_ID"))+
                    "&JOB_PROGRAM_REVISION="+mgr.URLEncode(headset.getRow().getValue("JOB_PROGRAM_REVISION"))+
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_CONTRACT"))+
                    "&COMPANY="+mgr.URLEncode(headset.getRow().getValue("COMPANY"));
        newWinHandle = "CopyJobProgram";
        newWinHeight = 460;
        newWinWidth = 400;
        //
    }

    private String hasActiveRevision(String job_Prog_Id, String job_Prog_Cont)
    {
        ASPManager mgr = getASPManager();
        //Bug 58216 start
        cmd = trans.addQuery("HASACTIVE", "SELECT DISTINCT(1) ACTIVE_REVISION FROM JOB_PROGRAM " +
                             "WHERE JOB_PROGRAM_ID = ? AND " + 
                             "JOB_PROGRAM_CONTRACT = ? AND " +
                             "OBJSTATE = 'Active'");
        cmd.addParameter("JOB_PROGRAM_ID",job_Prog_Id);
        cmd.addParameter("JOB_PROGRAM_CONTRACT",job_Prog_Cont);
        //Bug 58216 end

        trans = mgr.perform(trans);

        if ("1".equals(trans.getValue("HASACTIVE/DATA/ACTIVE_REVISION")))
            return("Revision "+ headset.getRow().getValue("JOB_PROGRAM_REVISION")+
                   " of job program "+ headset.getRow().getValue("JOB_PROGRAM_ID")+ 
                   " is \"Obsolete\" an \"Active\" Revision Exist. Do you want to continue");
        else
            return("Revision "+ headset.getRow().getValue("JOB_PROGRAM_REVISION")+
                   " of job program "+ headset.getRow().getValue("JOB_PROGRAM_ID")+
                   " is \"Obsolete\". Do you want to continue");

    }

    public void createNewRevision()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;
        urlString = "CreateJobProgramRevisionDlg.page?JOB_PROGRAM_ID=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_ID")) +
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_CONTRACT"))+
                    "&JOB_PROGRAM_REVISION=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_REVISION"))+
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        newWinHandle = "CreateJobProgramRevisionDlg"; 
    }

    public void addJobProgramToObject()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;
        urlString = "JobProgToObjMain.page?JOB_PROGRAM_ID=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_ID")) +
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_CONTRACT"))+
                    "&JOB_PROGRAM_REVISION=" + mgr.URLEncode(headset.getValue("JOB_PROGRAM_REVISION"))+
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        newWinHandle = "JobProgToObjMain"; 
    }


//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


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

        f = headblk.addField("COMPANY");
        f.setLabel("PCMWJOBPROGRAMCOMPANY: Company");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setHidden();

        f = headblk.addField("JOB_PROGRAM_ID");
        f.setLabel("PCMWJOBPROGRAMJOBPROGRAMID: Job Program ID");
        f.setInsertable();
        f.setReadOnly();
        f.setUpperCase();
        f.setMandatory();
        f.setQueryable();
        f.setSize(20);
        f.setHilite();
        f.setMaxLength(100);

        f = headblk.addField("DESCRIPTION");
        f.setLabel("PCMWJOBPROGRAMDESCRIPTION: Description");
        f.setMandatory();
        f.setQueryable();
        f.setSize(35);
        f.setMaxLength(2000);

        f = headblk.addField("JOB_PROGRAM_REVISION");
        f.setLabel("PCMWJOBPROGRAMJOBPROGRAMREVISION: Revision");
        f.setInsertable();
        f.setReadOnly();
        f.setUpperCase();
        f.setMandatory();
        f.setQueryable();
        f.setSize(10);
        f.setMaxLength(6);
        f.setHilite();

        f = headblk.addField("JOB_PROGRAM_CONTRACT");
        f.setLabel("PCMWJOBPROGRAMJOBPROGRAMCONTRACT: Site");
        f.setInsertable();
        f.setReadOnly();
        f.setUpperCase();
        f.setMandatory();
        f.setQueryable();
        f.setSize(7);
        f.setMaxLength(5);
        f.setDynamicLOV("SITE","COMPANY",600,450);
        f.setLOVProperty("WHERE","CONTRACT IN (SELECT CONTRACT FROM USER_ALLOWED_SITE WHERE USERID = (SELECT Fnd_Session_API.Get_Fnd_User FROM DUAL))");
        f.setHilite();

        f = headblk.addField("STATE");
        f.setLabel("PCMWJOBPROGRAMSTATE: State");
        f.setReadOnly();
        f.setQueryable();
        f.setSize(30);
        f.setMaxLength(4000);

        f = headblk.addField("PROGRAM_TYPE_ID");
        f.setLabel("PCMWJOBPROGRAMPROGRAMTYPEID: Program Type");
        f.setUpperCase();
        f.setQueryable();
        f.setSize(20);
        f.setMaxLength(100);
        f.setDynamicLOV("PROGRAM_TYPE",600,450);
        f.setCustomValidation("PROGRAM_TYPE_ID","PROGRAM_TYPE_ID,PROGRAM_TYPE_DESC");

        f = headblk.addField("PROGRAM_TYPE_DESC");
        f.setLabel("PCMWJOBPROGRAMPROGRAMTYPEDESC: Description");
        f.setReadOnly();
        f.setQueryable();
        f.setSize(40);
        f.setMaxLength(2000);
        f.setFunction("PROGRAM_TYPE_API.Get_Description(:PROGRAM_TYPE_ID)");

        f = headblk.addField("DATE_CREATED","Datetime");
        f.setLabel("PCMWJOBPROGRAMDATECREATED: Date Created");
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("DATE_MODIFIED","Datetime");
        f.setLabel("PCMWJOBPROGRAMDATEMODIFIED: Date Modified");
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("CHANGED_BY_ID");
        f.setLabel("PCMWJOBPROGRAMCHANGEDBYID: Changed By Id");
        f.setUpperCase();
        f.setMaxLength(11);
        f.setHidden();
        f.setDynamicLOV("COMPANY_EMP","COMPANY",600,450);

        f = headblk.addField("CHANGED_BY");
        f.setLabel("PCMWJOBPROGRAMCHANGEDBY: Changed By");
        f.setReadOnly();
        f.setUpperCase();
        f.setSize(15);
        f.setDynamicLOV("PERSON_INFO",600,450);
        f.setMaxLength(20);

        f = headblk.addField("NO_OF_RECORDS","Number");
        f.setHidden();
        f.setFunction("0");

        f = headblk.addField("ACTIVE_REVISION");
        f.setHidden();
        f.setFunction("0");

        headblk.setView("JOB_PROGRAM");
        headblk.defineCommand("JOB_PROGRAM_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn");

        headbar.addCustomCommand("createNewRevision",mgr.translate("PCMWJOBPROGRAMNEWREV: Create New Revision..."));
        headbar.addCustomCommand("copyJobPorgram",mgr.translate("PCMWJOBPROGRAMCOPY: Copy Job Porgram..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("addJobProgramToObject",mgr.translate("PCMWJOBPROGRAMADDJOBTOOBJ: Add Job Program to Object..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("activate",mgr.translate("PCMWJOBPROGRAMACTIVATE: Activate"));
        headbar.addCustomCommand("setObsolete",mgr.translate("PCMWJOBPROGRAMOBSOLETE: Obsolete"));

        headbar.addCommandValidConditions("addJobProgramToObject","OBJSTATE","Enable","Active");
        headbar.addCommandValidConditions("activate","OBJSTATE","Enable","Preliminary");
        headbar.addCommandValidConditions("setObsolete","OBJSTATE","Enable","Preliminary;Active");

        headbar.addCustomCommandGroup("JOBPROGRAMSTATUS", mgr.translate("PCMWJOBPROGRAMSTATUS: Job Program Status"));
        headbar.setCustomCommandGroup("activate", "JOBPROGRAMSTATUS");
        headbar.setCustomCommandGroup("setObsolete", "JOBPROGRAMSTATUS");

        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("createNewRevision");
        headbar.removeFromMultirowAction("copyJobPorgram");   

        headtbl = mgr.newASPTable(headblk);
        headtbl.enableRowSelect();
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2); 

        //-------------itemblk----------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM");

        f = itemblk.addField("ITEM_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk.addField("ITEM_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk.addField("ITEM_JOB_PROGRAM_ID");
        f.setDbName("JOB_PROGRAM_ID");
        f.setHidden();

        f = itemblk.addField("ITEM_JOB_PROGRAM_REVISION");
        f.setDbName("JOB_PROGRAM_REVISION");
        f.setHidden();

        f = itemblk.addField("ITEM_JOB_PROGRAM_CONTRACT");
        f.setDbName("JOB_PROGRAM_CONTRACT");
        f.setHidden();

        f = itemblk.addField("STD_JOB_CONTRACT");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCONTRACT: Site");
        f.setQueryable();
        f.setReadOnly();
        f.setUpperCase();
        f.setSize(10);
        f.setMaxLength(5);

        f = itemblk.addField("STD_JOB_ID");
        f.setLabel("PCMWJOBPROGRAMSTDJOBID: Standard Job ID");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setUpperCase();
        f.setSize(15);
        f.setMaxLength(12);
        f.setLOV("StandardJobLov.page","STD_JOB_CONTRACT CONTRACT",600,450);
        f.setLOVProperty("WHERE","STATE NOT IN('Preliminary','Obsolete')");
        f.setCustomValidation("STD_JOB_ID,STD_JOB_REVISION,STD_JOB_CONTRACT","STD_JOB_ID,STD_JOB_REVISION,STD_JOB_WORK_DESC,STD_JOB_DEFINITION,STD_JOB_STATUS,STD_JOB_TYPE,TYPE_ID,TYPE_DESCR");

        f = itemblk.addField("STD_JOB_REVISION");
        f.setLabel("PCMWJOBPROGRAMSTDJOBREVISION: Revision");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setSize(10);
        f.setMaxLength(6);
        f.setLOV("StandardJobLov.page","STD_JOB_ID,STD_JOB_CONTRACT CONTRACT",600,450);
        f.setCustomValidation("STD_JOB_ID,STD_JOB_REVISION,STD_JOB_CONTRACT","STD_JOB_ID,STD_JOB_REVISION,STD_JOB_WORK_DESC,STD_JOB_DEFINITION,STD_JOB_STATUS,STD_JOB_TYPE,TYPE_ID,TYPE_DESCR");

        f = itemblk.addField("STD_JOB_WORK_DESC");
        f.setLabel("PCMWJOBPROGRAMSTDJOBWORKDESC: Work Description");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Work_Description(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(40);
        f.setMaxLength(2000);

        f = itemblk.addField("STD_JOB_DEFINITION");
        f.setLabel("PCMWJOBPROGRAMSTDJOBDEFINITION: Description");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Definition(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(40);
        f.setMaxLength(2000);

        f = itemblk.addField("STD_JOB_STATUS");
        f.setLabel("PCMWJOBPROGRAMSTDJOBSTATUS: Status");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(30);
        f.setMaxLength(2000);

        f = itemblk.addField("STD_JOB_TYPE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBTYPE: Type");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Standard_Job_Type(:STD_JOB_ID,:STD_JOB_REVISION,:STD_JOB_CONTRACT)");
        f.setMaxLength(2000);
        f.setSize(30);

        f = itemblk.addField("TYPE_ID");
        f.setLabel("PCMWJOBPROGRAMTYPEID: Std Job Type");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Type_Id(STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION)");
        f.setSize(30);

        f = itemblk.addField("TYPE_DESCR");
        f.setLabel("PCMWJOBPROGRAMTYPEDESCR: Description");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("Std_Job_Type_API.Get_Description(STANDARD_JOB_API.Get_Type_Id(STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION))");
        f.setSize(30);

        f = itemblk.addField("STD_JOB_ACTION_CODE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBACTIONCODE: Action");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(40);
        f.setMaxLength(2000);

        f = itemblk.addField("STD_JOB_ACTION_DESC");
        f.setLabel("PCMWJOBPROGRAMSTDJOBACTIONDESC: Action Description");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("MAINTENANCE_ACTION_API.Get_Description(STANDARD_JOB_API.Get_Action_Code_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION))");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_ORG_CODE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBORGCODE: Maint. Org.");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Org_Code(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(30);
        f.setMaxLength(2000);

        f = itemblk.addField("STD_JOB_WORK_TYPE_ID");
        f.setLabel("PCMWJOBPROGRAMSTDJOBWORKTYPEID: Work Type");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Work_Type_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_OP_STATUS_ID");
        f.setLabel("PCMWJOBPROGRAMSTDJOBOPSTATUSID: Operational Status");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Op_Status_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_PRIORITY_ID");
        f.setLabel("PCMWJOBPROGRAMSTDJOBPRIORITYID: Priority");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Priority_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_EXECUTION_TIME","Number");
        f.setLabel("PCMWJOBPROGRAMSTDJOBEXECUTIONTIME: Execution Time");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Execution_Time(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_SIGNATURE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBSIGNATURE: Planned By");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Execution_Time(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");

        f = itemblk.addField("STD_JOB_CREATED_BY");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCREATEDBY: Created By");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Created_By(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");

        f = itemblk.addField("STD_JOB_CREARED_BY","Datetime");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCREAREDBY: Creation Date");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Creation_Date(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CHANGED_BY");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCHANGEDBY: Changed By");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Changed_By(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_LAST_CHANGED","Datetime");
        f.setLabel("PCMWJOBPROGRAMSTDJOBLASTCHANGED: Last Changed");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Last_Changed(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_START_VALUE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBSTARTVALUE: Start Value");
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Start_Value(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_PM_START_UNIT");
        f.setLabel("PCMWJOBPROGRAMSTDJOBPMSTARTUNIT: Start Unit");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("Pm_Start_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Start_Unit(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION))");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_INTERVAL");
        f.setLabel("PCMWJOBPROGRAMSTDJOBINTERVAL: Interval");
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Interval(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");  
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGRAMSTDJOBINTERVALUNIT: Interval Unit");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("Pm_Interval_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Interval_Unit(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION))");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CALL_CODE");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCALLCODE: Event");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Call_Code(:STD_JOB_ID,:STD_JOB_REVISION,:STD_JOB_CONTRACT)");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_START_CALL","Number");
        f.setLabel("PCMWJOBPROGRAMSTDJOBSTARTCALL: Start");
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Start_Call(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CALL_INTERVAL","Number");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCALLINTERVAL: Interval");
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Call_Interval(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION) ");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGRAMSTDJOBVALIDTO: Valid To");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Valid_To(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGRAMSTDJOBVALIDFROM: Valid From");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Valid_From(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CALENDAR_ID");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCALENDARID: Calendar ID");
        f.setQueryable();
        f.setReadOnly();
        f.setFunction("STANDARD_JOB_API.Get_Calendar_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CAL_DESC");
        f.setLabel("PCMWJOBPROGRAMSTDJOBCALDESC: Description");
        f.setQueryable();
        f.setReadOnly();
        f.setReadOnly();
        f.setFunction("Work_Time_Calendar_API.Get_Description(STANDARD_JOB_API.Get_Calendar_Id(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION))");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_ORG_CONTARCT");
        f.setLabel("PCMWJOBPROGRAMSTDJOBORGCONTARCT: Maint. Org. Site");
        f.setQueryable();
        f.setFunction("STANDARD_JOB_API.Get_Org_Code_Contract(:STD_JOB_ID,:STD_JOB_REVISION,:STD_JOB_CONTRACT)");
        f.setMaxLength(2000);
        f.setHidden();

        f = itemblk.addField("ROWSTATE");
        f.setHidden();
        f.setFunction("Job_Program_API.Get_Rowstate(:ITEM_JOB_PROGRAM_ID,:ITEM_JOB_PROGRAM_REVISION,:ITEM_JOB_PROGRAM_CONTRACT)");

        itemblk.setView("STANDARD_JOB_PROGRAM");
        itemblk.defineCommand("STANDARD_JOB_PROGRAM_API","New__,Remove__");
        itemblk.setMasterBlock(headblk);
        itemblk.disableDocMan();

        itemset = itemblk.getASPRowSet();

        itembar = mgr.newASPCommandBar(itemblk);

        itembar.disableCommand(itembar.EDITROW);
        itembar.disableCommand(itembar.DUPLICATEROW);

        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM");
        itembar.defineCommand(itembar.NEWROW,"newRowITEM");

        itembar.enableMultirowAction();
        itembar.addCustomCommand("addStdJobsByCategory",mgr.translate("PCMWJOBPROGRAMADDSTDJOBS: Add Std Jobs by Category..."));
        itembar.forceEnableMultiActionCommand("addStdJobsByCategory");
        itembar.removeFromRowActions("addStdJobsByCategory");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.enableRowSelect();
        itemtbl.setWrap();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
        itemlay.setDialogColumns(2); 
    }

    public void securityChk()
    {
        ASPManager mgr = getASPManager();

        if (!varSec)
        {
            trans.clear();

            trans.addSecurityQuery("STD_JOB_CATEGORY_DISTINCT,EQUIPMENT_OBJ_CATEGORY,TYPE_DESIGNATION,EQUIPMENT_OBJ_TYPE,PART_CATALOG");
            trans.addPresentationObjectQuery("PCMW/StdJobsByCategoryDlg.page");

            trans = mgr.perform(trans);

            SecBuff = trans.getSecurityInfo();

            if (SecBuff.namedItemExists("PCMW/StdJobsByCategoryDlg.page") && SecBuff.itemExists("EQUIPMENT_OBJ_CATEGORY") && SecBuff.itemExists("TYPE_DESIGNATION") && SecBuff.itemExists("EQUIPMENT_OBJ_TYPE") && SecBuff.itemExists("PART_CATALOG"))
                secJobCat = true;

            varSec = true;
        }
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()==1)
            headbar.disableCommand(headbar.BACK);

        if ((!secJobCat) || (headset.countRows()>0 && headlay.isSingleLayout() && "Obsolete".equals(headset.getRow().getValue("OBJSTATE"))))
            itembar.removeCustomCommand("addStdJobsByCategory");

        if ( itemlay.isNewLayout() || itemlay.isEditLayout() )
        {
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.NEWROW);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.FIND);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.FORWARD);
        }


    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        if (headset.countRows()>0 && headlay.isSingleLayout())
            return "PCMWJOBPROGRMTITLE: Job Program - "+headset.getValue("JOB_PROGRAM_ID")+" : "+headset.getValue("DESCRIPTION")+"";
        else
            return "PCMWJOBPROGRMTITLEMULTI: Job Program";
    }

    protected String getTitle()
    {
        if (headset.countRows()>0 && headlay.isSingleLayout())
            return "PCMWJOBPROGRMTITLE: Job Program - "+headset.getValue("JOB_PROGRAM_ID")+" : "+headset.getValue("DESCRIPTION")+"";
        else
            return "PCMWJOBPROGRMTITLEMULTI: Job Program";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());
        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            appendToHTML(itemlay.show());
        }

        appendDirtyJavaScript("window.name = \"jobProgram\"\n");

        if (bOpenNewWindow)
        {
            if (CopyObsoleteJob)
                appendDirtyJavaScript("if (confirm('" + mgr.encodeStringForJavascript(sWarningMsg) + "'))\n");   //XSS_Safe AMNILK 20070717
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,dependent,scrollbars=yes,width=900,height=600\"); \n");      
        }
    }
}

