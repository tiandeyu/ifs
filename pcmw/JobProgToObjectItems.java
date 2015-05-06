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
*  File        : JobProgToObjectItems.java 
*  Created     : 050314 NIJALK  (AMEC112 - Job program)
*  Modified    :
*  THWILK  060126  Corrected localization errors.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  DIAMLK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMNILK  070717  Eliminated XSS Security Vulnerability.
*  ASSALK  080303  Bug 77177. Modified showStdJobITEM2().
*  SHAFLK  080304  Bug 71775, Modified separateSimulate() and roundSimulate().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class JobProgToObjectItems extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobProgToObjectItems");

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

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;

    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;

    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPBuffer SecBuff;
    private int currrow;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPField f;

    private ASPTabContainer tabs;

    private String sExist;
    private String mchCode;
    private String mchContract;
    private String jobProgId;
    private String jobProgRev;
    private String jobProgContract;
    private String maintOrg;
    private String action;
    private String routeId;
    private String merge;
    private String validFrom;
    private String validTo;
    private String urlString;
    private String newWinHandle;
    private String qrystr;
    private String qryString;
    private String val = "";
    private String txt = "";
    private String sWarningMsg;
    private int newWinHeight = 600;
    private int newWinWidth = 900;
    private boolean bWarning ;
    private boolean bOpenNewWindow;
    private boolean bJobs;
    private boolean bOpt;
    private boolean enSepSimulate;
    private boolean enRndSimulate;
    private boolean enSepGenarate;
    private boolean enRndGenarate;

    //===============================================================
    // Construction 
    //===============================================================
    public JobProgToObjectItems(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        sExist = "";
        mchCode = "";
        mchContract = "";
        jobProgId = "";
        jobProgRev = "";
        jobProgContract = "";
        maintOrg = "";
        action = "";
        routeId = "";
        merge = "";
        validFrom = "";
        validTo = "";
        sWarningMsg = "";
        enSepSimulate = false;
        enRndSimulate= false;
        enSepGenarate = false;
        enRndGenarate = false;
        bWarning = false;
        bJobs = false;
        bOpt = false;

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        qrystr = mgr.getQueryString();

        qryString = ctx.readValue("QRYSTRING","");
        sExist = ctx.readValue("CHECK_EXISTS","");
        mchCode = ctx.readValue("MCH_CODE","");        
        mchContract = ctx.readValue("MCH_CODE_CONTRACT","");
        jobProgId = ctx.readValue("JOB_PROGRAM_ID","");
        jobProgRev = ctx.readValue("JOB_PROGRAM_REVISION","");
        jobProgContract = ctx.readValue("JOB_PROGRAM_CONTRACT","");
        maintOrg = ctx.readValue("MAINT_ORG","");
        action = ctx.readValue("ACTION","");
        routeId = ctx.readValue("ROUTE_ID","");
        merge = ctx.readValue("MERGE","");
        validFrom = ctx.readValue("VALID_FROM","");
        validTo = ctx.readValue("VALID_TO","");
        sWarningMsg = ctx.readValue("SWARNINGMSG","");
        bJobs = ctx.readFlag("BJOBS",false);
        bOpt = ctx.readFlag("BOPT",false);
        bWarning = ctx.readFlag("BWARNING",false);
        enSepSimulate = ctx.readFlag("SEPSIM",enSepSimulate);
        enRndSimulate = ctx.readFlag("RNDSIM",enRndSimulate);
        enSepGenarate = ctx.readFlag("SEPGEN",enSepGenarate);
        enRndGenarate = ctx.readFlag("RNDGEN",enRndGenarate);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.readValue("FROMJOBPROGOBJ")))
        {
            sExist = mgr.readValue("CHECK_EXISTS");
            mchCode = mgr.readValue("MCH_CODE");        
            mchContract =mgr.readValue("MCH_CODE_CONTRACT");
            jobProgId = mgr.readValue("JOB_PROGRAM_ID");
            jobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
            jobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
            maintOrg = mgr.readValue("MAINT_ORG");
            action = mgr.readValue("ACTION");
            routeId = mgr.readValue("ROUTE_ID");
            merge = mgr.readValue("MERGE");
            validFrom = mgr.readValue("VALID_FROM");
            validTo = mgr.readValue("VALID_TO");

            if (!mgr.isEmpty(jobProgId) && !mgr.isEmpty(jobProgRev) && !mgr.isEmpty(jobProgContract) && !mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract))
                bJobs = true;
            if (!mgr.isEmpty(maintOrg) && !mgr.isEmpty(action))
                bOpt = true;

            populateItems(qrystr);
        }
        else if (!mgr.isEmpty(mgr.readValue("ADDSTDJOB")) && !mgr.isEmpty(mgr.readValue("SEARCH")))
        {
            mchCode = ctx.findGlobal("MCH_CODE");        
            mchContract = ctx.findGlobal("MCH_CODE_CONTRACT");
            jobProgId = ctx.findGlobal("JOB_PROGRAM_ID");
            jobProgRev = ctx.findGlobal("JOB_PROGRAM_REVISION");
            jobProgContract = ctx.findGlobal("JOB_PROGRAM_CONTRACT");
            maintOrg = ctx.findGlobal("MAINT_ORG");
            action = ctx.findGlobal("ACTION");
            routeId = ctx.findGlobal("ROUTE_ID");
            merge = ctx.findGlobal("MERGE");
            validFrom = ctx.findGlobal("VALID_FROM");
            validTo = ctx.findGlobal("VALID_TO");

            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
        }
        else if (!mgr.isEmpty(mgr.readValue("PARENTCLOSED")))
        {
            removeStdJobs();
            removePmActions();
        }
        else if (mgr.buttonPressed("SEPSIMULATE"))
            separateSimulate();
        else if (mgr.buttonPressed("SEPGENERATE"))
            separateGenarate();
        else if (mgr.buttonPressed("RTSIMULATE"))
            roundSimulate();
        else if (mgr.buttonPressed("RTGENERATE"))
            roundGenarate();

        adjust();

        if ("2".equals(ctx.findGlobal("ACTTAB")))
            tabs.setActiveTab(2);
        else
            tabs.setActiveTab(1);

        tabs.saveActiveTab();

        ctx.writeValue("QRYSTR", qrystr);
        ctx.writeValue("QRYSTRING",qryString);
        ctx.writeValue("SWARNINGMSG", sWarningMsg);
        ctx.writeValue("MCH_CODE",mchCode);
        ctx.writeValue("MCH_CODE_CONTRACT",mchContract);
        ctx.writeValue("JOB_PROGRAM_ID",jobProgId);
        ctx.writeValue("JOB_PROGRAM_REVISION",jobProgRev);
        ctx.writeValue("JOB_PROGRAM_CONTRACT",jobProgContract);
        ctx.writeValue("CHECK_EXISTS",sExist);
        ctx.writeValue("MAINT_ORG",maintOrg);
        ctx.writeValue("ACTION",action);
        ctx.writeValue("ROUTE_ID",routeId);
        ctx.writeValue("MERGE",merge);
        ctx.writeValue("VALID_FROM",validFrom);
        ctx.writeValue("VALID_TO",validTo);
        ctx.writeFlag("BJOBS",bJobs);
        ctx.writeFlag("BOPT",bOpt);
        ctx.writeFlag("SEPSIM",enSepSimulate);
        ctx.writeFlag("RNDSIM",enRndSimulate);
        ctx.writeFlag("SEPGEN",enSepGenarate);
        ctx.writeFlag("RNDGEN",enRndGenarate);
        ctx.writeFlag("BWARNING",bWarning);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        val = mgr.readValue("VALIDATE");

        if ("ITEM1_STD_JOB_ID".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRev = "";
            String sStdJobContract = mgr.readValue("ITEM1_STD_JOB_CONTRACT");
            String sStdJobType = "";
            String sWorkDescr = "";
            String sDefinition = "";
            String sStatus = "";
            String sTypeId = "";
            String sTypeDescr = "";
            String new_job = mgr.readValue("ITEM1_STD_JOB_ID","");

            if (new_job.indexOf("~",0)>0)
            {
                String [] attrarr =  new_job.split("~");

                sStdJobId = attrarr[0];
                sStdJobRev = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("ITEM1_STD_JOB_ID");
                sStdJobRev = mgr.readValue("ITEM1_STD_JOB_REVISION");
            }

            if (mgr.isEmpty(sStdJobId))
                sStdJobRev = "";
            else if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobContract) && !mgr.isEmpty(sStdJobRev))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETTYPE","Standard_Job_API.Get_Standard_Job_Type","ITEM1_STD_JOB_TYPE");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEDB","Standard_Job_Type_API.Encode","ITEM1_STD_JOB_TYPE");
                cmd.addReference("ITEM1_STD_JOB_TYPE","GETTYPE/DATA");

                cmd = trans.addCustomFunction("GETWORKDESR","Standard_Job_API.Get_Work_Description","ITEM1_WORK_DESC");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETDEFINITION","Standard_Job_API.Get_Definition","ITEM1_DEFINITION");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","ITEM1_STATUS");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEID","Standard_Job_API.Get_Type_Id","ITEM1_TYPE_ID");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);  

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","ITEM1_TYPE_DESC");
                cmd.addReference("ITEM1_TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sStdJobType = trans.getValue("GETTYPEDB/DATA/STD_JOB_TYPE");
                sWorkDescr = trans.getValue("GETWORKDESR/DATA/ITEM1_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/ITEM1_DEFINITION");
                sStatus = trans.getValue("GETSTATUS/DATA/ITEM1_STATUS");
                sTypeId = trans.getValue("GETTYPEID/DATA/ITEM1_TYPE_ID");
                sTypeDescr = trans.getValue("GETTYPEDESCR/DATA/ITEM1_TYPE_DESC");
            }

            txt = (mgr.isEmpty(sStdJobId)? "" : sStdJobId) + "^" +
                  (mgr.isEmpty(sStdJobRev)? "" : sStdJobRev) + "^" +
                  (mgr.isEmpty(sStdJobType)? "" : sStdJobType) + "^" +
                  (mgr.isEmpty(sWorkDescr)? "" : sWorkDescr) + "^" +
                  (mgr.isEmpty(sDefinition)? "" : sDefinition) + "^" +
                  (mgr.isEmpty(sStatus)? "" : sStatus) + "^" +
                  (mgr.isEmpty(sTypeId)? "" : sTypeId) + "^" +
                  (mgr.isEmpty(sTypeDescr)? "" : sTypeDescr) + "^" ;
                        
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_STD_JOB_ID".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRev = "";
            String sStdJobContract = mgr.readValue("ITEM2_STD_JOB_CONTRACT");
            String sStdJobType = "";
            String sWorkDescr = "";
            String sDefinition = "";
            String sStatus = "";
            String sTypeId = "";
            String sTypeDescr = "";
            String new_job = mgr.readValue("ITEM2_STD_JOB_ID","");

            if (new_job.indexOf("~",0)>0)
            {
                String [] attrarr =  new_job.split("~");

                sStdJobId = attrarr[0];
                sStdJobRev = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("ITEM2_STD_JOB_ID");
                sStdJobRev = mgr.readValue("ITEM2_STD_JOB_REVISION");
            }

            if (mgr.isEmpty(sStdJobId))
                sStdJobRev = "";
            else if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobContract) && !mgr.isEmpty(sStdJobRev))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETTYPE","Standard_Job_API.Get_Standard_Job_Type","ITEM2_STD_JOB_TYPE");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEDB","Standard_Job_Type_API.Encode","ITEM2_STD_JOB_TYPE");
                cmd.addReference("ITEM2_STD_JOB_TYPE","GETTYPE/DATA");

                cmd = trans.addCustomFunction("GETWORKDESR","Standard_Job_API.Get_Work_Description","ITEM2_WORK_DESC");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETDEFINITION","Standard_Job_API.Get_Definition","ITEM2_DEFINITION");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","ITEM2_STATUS");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEID","Standard_Job_API.Get_Type_Id","ITEM2_TYPE_ID");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);  

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","ITEM2_TYPE_DESC");
                cmd.addReference("ITEM2_TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sStdJobType = trans.getValue("GETTYPEDB/DATA/STD_JOB_TYPE");
                sWorkDescr = trans.getValue("GETWORKDESR/DATA/ITEM2_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/ITEM2_DEFINITION");
                sStatus = trans.getValue("GETSTATUS/DATA/ITEM2_STATUS");
                sTypeId = trans.getValue("GETTYPEID/DATA/ITEM2_TYPE_ID");
                sTypeDescr = trans.getValue("GETTYPEDESCR/DATA/ITEM2_TYPE_DESC");
            }

            txt = (mgr.isEmpty(sStdJobId)? "" : sStdJobId) + "^" +
                  (mgr.isEmpty(sStdJobRev)? "" : sStdJobRev) + "^" +
                  (mgr.isEmpty(sStdJobType)? "" : sStdJobType) + "^" +
                  (mgr.isEmpty(sWorkDescr)? "" : sWorkDescr) + "^" +
                  (mgr.isEmpty(sDefinition)? "" : sDefinition) + "^" +
                  (mgr.isEmpty(sStatus)? "" : sStatus) + "^" +
                  (mgr.isEmpty(sTypeId)? "" : sTypeId) + "^" +
                  (mgr.isEmpty(sTypeDescr)? "" : sTypeDescr) + "^" ;
                        
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_STD_JOB_REVISION".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRev = "";
            String sStdJobContract = mgr.readValue("ITEM1_STD_JOB_CONTRACT");
            String sStdJobType = "";
            String sWorkDescr = "";
            String sDefinition = "";
            String sStatus = "";
            String sTypeId = "";
            String sTypeDescr = "";
            String new_job = mgr.readValue("ITEM1_STD_JOB_REVISION","");

            if (new_job.indexOf("~",0)>0)
            {
                String [] attrarr =  new_job.split("~");

                sStdJobId = attrarr[0];
                sStdJobRev = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("ITEM1_STD_JOB_ID");
                sStdJobRev = mgr.readValue("ITEM1_STD_JOB_REVISION");
            }

            if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobContract) && !mgr.isEmpty(sStdJobRev))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETTYPE","Standard_Job_API.Get_Standard_Job_Type","ITEM1_STD_JOB_TYPE");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEDB","Standard_Job_Type_API.Encode","ITEM1_STD_JOB_TYPE");
                cmd.addReference("ITEM1_STD_JOB_TYPE","GETTYPE/DATA");

                cmd = trans.addCustomFunction("GETWORKDESR","Standard_Job_API.Get_Work_Description","ITEM1_WORK_DESC");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETDEFINITION","Standard_Job_API.Get_Definition","ITEM1_DEFINITION");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","ITEM1_STATUS");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEID","Standard_Job_API.Get_Type_Id","ITEM1_TYPE_ID");
                cmd.addParameter("ITEM1_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM1_STD_JOB_REVISION",sStdJobRev);  

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","ITEM1_TYPE_DESC");
                cmd.addReference("ITEM1_TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sStdJobType = trans.getValue("GETTYPEDB/DATA/STD_JOB_TYPE");
                sWorkDescr = trans.getValue("GETWORKDESR/DATA/ITEM1_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/ITEM1_DEFINITION");
                sStatus = trans.getValue("GETSTATUS/DATA/ITEM1_STATUS");
                sTypeId = trans.getValue("GETTYPEID/DATA/ITEM1_TYPE_ID");
                sTypeDescr = trans.getValue("GETTYPEDESCR/DATA/ITEM1_TYPE_DESC");
            }

            txt = (mgr.isEmpty(sStdJobId)? "" : sStdJobId) + "^" +
                  (mgr.isEmpty(sStdJobRev)? "" : sStdJobRev) + "^" +
                  (mgr.isEmpty(sStdJobType)? "" : sStdJobType) + "^" +
                  (mgr.isEmpty(sWorkDescr)? "" : sWorkDescr) + "^" +
                  (mgr.isEmpty(sDefinition)? "" : sDefinition) + "^" +
                  (mgr.isEmpty(sStatus)? "" : sStatus) + "^" +
                  (mgr.isEmpty(sTypeId)? "" : sTypeId) + "^" +
                  (mgr.isEmpty(sTypeDescr)? "" : sTypeDescr) + "^" ;
                        
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_STD_JOB_REVISION".equals(val))
        {
            String sStdJobId = "";
            String sStdJobRev = "";
            String sStdJobContract = mgr.readValue("ITEM2_STD_JOB_CONTRACT");
            String sStdJobType = "";
            String sWorkDescr = "";
            String sDefinition = "";
            String sStatus = "";
            String sTypeId = "";
            String sTypeDescr = "";
            String new_job = mgr.readValue("ITEM2_STD_JOB_REVISION","");

            if (new_job.indexOf("~",0)>0)
            {
                String [] attrarr =  new_job.split("~");

                sStdJobId = attrarr[0];
                sStdJobRev = attrarr[2];
            }
            else
            {
                sStdJobId = mgr.readValue("ITEM2_STD_JOB_ID");
                sStdJobRev = mgr.readValue("ITEM2_STD_JOB_REVISION");
            }

            if (!mgr.isEmpty(sStdJobId) && !mgr.isEmpty(sStdJobContract) && !mgr.isEmpty(sStdJobRev))
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETTYPE","Standard_Job_API.Get_Standard_Job_Type","ITEM2_STD_JOB_TYPE");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEDB","Standard_Job_Type_API.Encode","ITEM2_STD_JOB_TYPE");
                cmd.addReference("ITEM2_STD_JOB_TYPE","GETTYPE/DATA");

                cmd = trans.addCustomFunction("GETWORKDESR","Standard_Job_API.Get_Work_Description","ITEM2_WORK_DESC");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETDEFINITION","Standard_Job_API.Get_Definition","ITEM2_DEFINITION");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","ITEM2_STATUS");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);

                cmd = trans.addCustomFunction("GETTYPEID","Standard_Job_API.Get_Type_Id","ITEM2_TYPE_ID");
                cmd.addParameter("ITEM2_STD_JOB_ID",sStdJobId);
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("ITEM2_STD_JOB_REVISION",sStdJobRev);  

                cmd = trans.addCustomFunction("GETTYPEDESCR","Std_Job_Type_API.Get_Description","ITEM2_TYPE_DESC");
                cmd.addReference("ITEM2_TYPE_ID","GETTYPEID/DATA");

                trans = mgr.validate(trans);

                sStdJobType = trans.getValue("GETTYPEDB/DATA/STD_JOB_TYPE");
                sWorkDescr = trans.getValue("GETWORKDESR/DATA/ITEM2_WORK_DESC");
                sDefinition = trans.getValue("GETDEFINITION/DATA/ITEM2_DEFINITION");
                sStatus = trans.getValue("GETSTATUS/DATA/ITEM2_STATUS");
                sTypeId = trans.getValue("GETTYPEID/DATA/ITEM2_TYPE_ID");
                sTypeDescr = trans.getValue("GETTYPEDESCR/DATA/ITEM2_TYPE_DESC");
            }

            txt = (mgr.isEmpty(sStdJobId)? "" : sStdJobId) + "^" +
                  (mgr.isEmpty(sStdJobRev)? "" : sStdJobRev) + "^" +
                  (mgr.isEmpty(sStdJobType)? "" : sStdJobType) + "^" +
                  (mgr.isEmpty(sWorkDescr)? "" : sWorkDescr) + "^" +
                  (mgr.isEmpty(sDefinition)? "" : sDefinition) + "^" +
                  (mgr.isEmpty(sStatus)? "" : sStatus) + "^" +
                  (mgr.isEmpty(sTypeId)? "" : sTypeId) + "^" +
                  (mgr.isEmpty(sTypeDescr)? "" : sTypeDescr) + "^" ;
                        
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CONNECTED_PM_NO".equals(val))
        {
            String new_pm = mgr.readValue("ITEM1_CONNECTED_PM_NO","");
            String pm_no = "";
            String pm_rev = "";
            String pm_sim_id = "";

            if (new_pm.indexOf("~",0)>0)
            {
                String [] attrarr =  new_pm.split("~");

                pm_no = attrarr[0];
                pm_rev = attrarr[1];
            }
            else
            {
                pm_no = mgr.readValue("ITEM1_CONNECTED_PM_NO");
                pm_rev = mgr.readValue("ITEM1_CONNECTED_PM_REVISION");
            }

            if (!mgr.isEmpty(pm_no) && !mgr.isEmpty(pm_rev))
                pm_sim_id = ctx.findGlobal("PMSIMULATIONID");

            txt = (mgr.isEmpty(pm_no)? "" : pm_no) + "^" +
                  (mgr.isEmpty(pm_rev)? "" : pm_rev) + "^"+
                  (mgr.isEmpty(pm_sim_id)? "" : pm_sim_id) + "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CONNECTED_PM_REVISION".equals(val))
        {
            String new_pm = mgr.readValue("ITEM1_CONNECTED_PM_REVISION","");
            String pm_no = "";
            String pm_rev = "";
            String pm_sim_id = "";

            if (new_pm.indexOf("~",0)>0)
            {
                String [] attrarr =  new_pm.split("~");

                pm_no = attrarr[0];
                pm_rev = attrarr[1];
            }
            else
            {
                pm_no = mgr.readValue("ITEM1_CONNECTED_PM_NO");
                pm_rev = mgr.readValue("ITEM1_CONNECTED_PM_REVISION");
            }

            if (!mgr.isEmpty(pm_no) && !mgr.isEmpty(pm_rev))
                pm_sim_id = ctx.findGlobal("PMSIMULATIONID");

            txt = (mgr.isEmpty(pm_no)? "" : pm_no) + "^" +
                  (mgr.isEmpty(pm_rev)? "" : pm_rev) + "^"+
                  (mgr.isEmpty(pm_sim_id)? "" : pm_sim_id) + "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_CONNECTED_PM_NO".equals(val))
        {
            String new_pm = mgr.readValue("ITEM2_CONNECTED_PM_NO","");
            String pm_no = "";
            String pm_rev = "";
            String pm_sim_id = "";

            if (new_pm.indexOf("~",0)>0)
            {
                String [] attrarr =  new_pm.split("~");

                pm_no = attrarr[0];
                pm_rev = attrarr[1];
            }
            else
            {
                pm_no = mgr.readValue("ITEM2_CONNECTED_PM_NO");
                pm_rev = mgr.readValue("ITEM2_CONNECTED_PM_REVISION");
            }

            if (!mgr.isEmpty(pm_no) && !mgr.isEmpty(pm_rev))
                pm_sim_id = ctx.findGlobal("PMSIMULATIONID");

            txt = (mgr.isEmpty(pm_no)? "" : pm_no) + "^" +
                  (mgr.isEmpty(pm_rev)? "" : pm_rev) + "^"+
                  (mgr.isEmpty(pm_sim_id)? "" : pm_sim_id) + "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_CONNECTED_PM_REVISION".equals(val))
        {
            String new_pm = mgr.readValue("ITEM2_CONNECTED_PM_REVISION","");
            String pm_no = "";
            String pm_rev = "";
            String pm_sim_id = "";

            if (new_pm.indexOf("~",0)>0)
            {
                String [] attrarr =  new_pm.split("~");

                pm_no = attrarr[0];
                pm_rev = attrarr[1];
            }
            else
            {
                pm_no = mgr.readValue("ITEM2_CONNECTED_PM_NO");
                pm_rev = mgr.readValue("ITEM2_CONNECTED_PM_REVISION");
            }

            if (!mgr.isEmpty(pm_no) && !mgr.isEmpty(pm_rev))
                pm_sim_id = ctx.findGlobal("PMSIMULATIONID");

            txt = (mgr.isEmpty(pm_no)? "" : pm_no) + "^" +
                  (mgr.isEmpty(pm_rev)? "" : pm_rev) + "^"+
                  (mgr.isEmpty(pm_sim_id)? "" : pm_sim_id) + "^";
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }  

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void populateItems(String qrystr)
    {
        ASPManager mgr = getASPManager();

        if (qrystr.indexOf("VALJOBCONTRACT")>-1)
        {
            ctx.removeGlobal("SEPPMSIMULATED");
            ctx.removeGlobal("RTPMSIMULATED");

            removeStdJobs();
            removeRelatedPMs();
            if (!mgr.isEmpty(ctx.findGlobal("PMSIMULATIONID")))
            {
                okFindITEM3();
                okFindITEM4();
            }
            ClearStdJobTables();
            ctx.removeGlobal("STDJOBSIMULATIONID");
            if (!(itemset3.countRows()>0) && !(itemset4.countRows()>0))
                ctx.removeGlobal("PMSIMULATIONID");

            if (!mgr.isEmpty(jobProgId) && !mgr.isEmpty(jobProgRev) && !mgr.isEmpty(jobProgContract))
            {
                if ("TRUE".equals(sExist))
                    findInsertStdJobs();
            }
        }
        else if (qrystr.indexOf("VALJOBID")>-1 || qrystr.indexOf("VALJOBREV")>-1)
        {
            ctx.removeGlobal("SEPPMSIMULATED");
            ctx.removeGlobal("RTPMSIMULATED");

            removeStdJobs();
            removeRelatedPMs();
            if (!mgr.isEmpty(ctx.findGlobal("PMSIMULATIONID")))
            {
                okFindITEM3();
                okFindITEM4();
            }
            ClearStdJobTables();
            ctx.removeGlobal("STDJOBSIMULATIONID");
            if (!(itemset3.countRows()>0) && !(itemset4.countRows()>0))
                ctx.removeGlobal("PMSIMULATIONID");
            if (!mgr.isEmpty(jobProgId) && !mgr.isEmpty(jobProgRev) && !mgr.isEmpty(jobProgContract))
            {
                if ("TRUE".equals(sExist))
                    findInsertStdJobs();
            }
        }
        else if (qrystr.indexOf("VALMCHCONTRACT")>-1 || qrystr.indexOf("VALMCHCODE")>-1)
        {
            ctx.removeGlobal("SEPPMSIMULATED");
            ctx.removeGlobal("RTPMSIMULATED");

            removePmActions();
            ClearPMTables();
            ctx.removeGlobal("PMSIMULATIONID");

            if (!mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract))
            {
                if ("TRUE".equals(sExist))
                    findInsertPmActions();
            }
            okFindITEM1();
            okFindITEM2();
        }
        else if (qrystr.indexOf("WINOPENED")>-1)
        {
            if (!mgr.isEmpty(jobProgId) && !mgr.isEmpty(jobProgRev) && !mgr.isEmpty(jobProgContract))
                findInsertStdJobs();
            else if (!mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract))
                findInsertPmActions();
        }
        else
        {
            if (qrystr.indexOf("VALROUTEID")>-1)
                ctx.removeGlobal("RTPMSIMULATED");
            else
            {
                ctx.removeGlobal("SEPPMSIMULATED");
                ctx.removeGlobal("RTPMSIMULATED");
            }
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
        }
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk1);
        //Bug 58216 start
        q.addWhereCondition("JOB_PROGRAM_ID = ?");
        q.addWhereCondition("JOB_PROGRAM_REVISION = ?");
        q.addWhereCondition("JOB_PROGRAM_CONTRACT = ?");
        q.addWhereCondition("STD_JOB_SIMULATION_ID = ?");
        q.addWhereCondition("STD_JOB_TYPE = '1'");
        q.addParameter("JOB_PROGRAM_ID",jobProgId);
        q.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
        q.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);
        q.addParameter("ITEM1_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        //Bug 58216 end
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset1.countRows()>0)
        {                             
            itemset1.first();
            for (int i=0;i<itemset1.countRows();i++)
            { 
              enSepSimulate = true;
              if ((mgr.isEmpty(itemset1.getRow().getValue("ITEM1_STD_JOB_ACTION")) && mgr.isEmpty(action)) || (mgr.isEmpty(itemset1.getRow().getValue("ITEM1_STD_JOB_MAINT_ORG")) && mgr.isEmpty(maintOrg)))
              {   
                  enSepSimulate = false;
                  break;  
              }
              itemset1.next();
            }
        }
        qryString = mgr.createSearchURL(itemblk1);
    }

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk2);
        //Bug 58216 start
        q.addWhereCondition("JOB_PROGRAM_ID = ?");
        q.addWhereCondition("JOB_PROGRAM_REVISION = ?");
        q.addWhereCondition("JOB_PROGRAM_CONTRACT = ?");
        q.addWhereCondition("STD_JOB_SIMULATION_ID = ?");
        q.addWhereCondition("STD_JOB_TYPE = '2'");
        q.addParameter("JOB_PROGRAM_ID",jobProgId);
        q.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
        q.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);
        q.addParameter("ITEM2_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        //Bug 58216 end
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset2.countRows()>0)
        {
            for (int i=0;i<itemset2.countRows();i++)
            {
              itemset2.first();
              enRndSimulate = true;
              if ((mgr.isEmpty(itemset2.getRow().getValue("ITEM2_STD_JOB_ACTION")) && mgr.isEmpty(action)) || (mgr.isEmpty(itemset2.getRow().getValue("ITEM2_STD_JOB_MAINT_ORG")) && mgr.isEmpty(maintOrg)))
              {
                  enRndSimulate = false;
                  break;    
              }
              itemset2.next();
            }
        }

        qryString = mgr.createSearchURL(itemblk2);
    }

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk3);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE= ?");
        q.addWhereCondition("MCH_CODE_CONTRACT= ?");
        q.addWhereCondition("PM_SIMULATION_ID= ?");
        q.addWhereCondition("SIMULATED_PM_TYPE = 'ActiveSeparate'");
        q.addParameter("MCH_CODE",mchCode);
        q.addParameter("MCH_CODE_CONTRACT",mchContract);
        q.addParameter("ITEM1_PM_SIMULATION_ID",ctx.findGlobal("PMSIMULATIONID"));
        //Bug 58216 end
        q.includeMeta("ALL");

        mgr.submit(trans);
    }
    
    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk4);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE= ?");
        q.addWhereCondition("MCH_CODE_CONTRACT= ?");
        q.addWhereCondition("PM_SIMULATION_ID= ?");
        q.addWhereCondition("SIMULATED_PM_TYPE = 'ActiveRound'");
        q.addParameter("MCH_CODE",mchCode);
        q.addParameter("MCH_CODE_CONTRACT",mchContract);
        q.addParameter("ITEM1_PM_SIMULATION_ID",ctx.findGlobal("PMSIMULATIONID"));
        //Bug 58216 end
        q.includeMeta("ALL");

        mgr.submit(trans);
    }

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        //Bug 58216 start
        q.addWhereCondition("JOB_PROGRAM_ID = ?");
        q.addWhereCondition("JOB_PROGRAM_REVISION = ?");
        q.addWhereCondition("JOB_PROGRAM_CONTRACT = ?");
        q.addWhereCondition("STD_JOB_SIMULATION_ID = ?");
        q.addWhereCondition("STD_JOB_TYPE = '1'");
        q.addParameter("JOB_PROGRAM_ID",jobProgId);
        q.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
        q.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);
        q.addParameter("ITEM1_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        //Bug 58216 end

        mgr.submit(trans);
        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
    }  

    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        //Bug 58216 start
        q.addWhereCondition("JOB_PROGRAM_ID = ?");
        q.addWhereCondition("JOB_PROGRAM_REVISION = ?");
        q.addWhereCondition("JOB_PROGRAM_CONTRACT = ?");
        q.addWhereCondition("STD_JOB_SIMULATION_ID = ?");
        q.addWhereCondition("STD_JOB_TYPE = '2'");
        q.addParameter("JOB_PROGRAM_ID",jobProgId);
        q.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
        q.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);
        q.addParameter("ITEM2_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        //Bug 58216 end

        mgr.submit(trans);
        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
    }  

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        //Bug 8216 start
        q.addWhereCondition("MCH_CODE= ?");
        q.addWhereCondition("MCH_CODE_CONTRACT= ?");
        q.addWhereCondition("PM_SIMULATION_ID= ?");
        q.addWhereCondition("SIMULATED_PM_TYPE = 'ActiveSeparate'");
        q.addParameter("MCH_CODE",mchCode);
        q.addParameter("MCH_CODE_CONTRACT",mchContract);
        q.addParameter("ITEM1_PM_SIMULATION_ID",ctx.findGlobal("PMSIMULATIONID"));
        //Bug 58216 end

        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
    }  

    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk4);
        q.setSelectList("to_char(count(*)) N");
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE= ?");
        q.addWhereCondition("MCH_CODE_CONTRACT= ?");
        q.addWhereCondition("PM_SIMULATION_ID= ?");
        q.addWhereCondition("SIMULATED_PM_TYPE = 'ActiveRound'");
        q.addParameter("MCH_CODE",mchCode);
        q.addParameter("MCH_CODE_CONTRACT",mchContract);
        q.addParameter("ITEM1_PM_SIMULATION_ID",ctx.findGlobal("PMSIMULATIONID"));
        //Bug 58216 end

        mgr.submit(trans);
        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
        itemset4.clear();
    } 

    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM1","SIMULATED_STD_JOB_API.New__",itemblk1);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_STD_JOB_JOB_PROGRAM_ID",jobProgId);
        data.setFieldItem("ITEM1_STD_JOB_JOB_PROGRAM_REV",jobProgRev);
        data.setFieldItem("ITEM1_STD_JOB_JOB_PROGRAM_CONTRACT",jobProgContract);
        data.setFieldItem("ITEM1_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        data.setFieldItem("ITEM1_STD_JOB_CONTRACT",jobProgContract);
        itemset1.addRow(data);
    }

    public void newRowITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM2","SIMULATED_STD_JOB_API.New__",itemblk2);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM2/DATA");
        data.setFieldItem("ITEM2_STD_JOB_JOB_PROGRAM_ID",jobProgId);
        data.setFieldItem("ITEM2_STD_JOB_JOB_PROGRAM_REV",jobProgRev);
        data.setFieldItem("ITEM2_STD_JOB_JOB_PROGRAM_CONTRACT",jobProgContract);
        data.setFieldItem("ITEM2_STD_JOB_SIMULATION_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        data.setFieldItem("ITEM2_STD_JOB_CONTRACT",jobProgContract);
        itemset2.addRow(data);
    }

    public boolean saveReturnITEM1()
    {
        ASPManager mgr = getASPManager();

        itemset1.changeRow();
        mgr.submit(trans);
        
        ctx.removeGlobal("SEPPMSIMULATED");
        okFindITEM1();

        return true;
    }

    public boolean saveReturnITEM2()
    {
        ASPManager mgr = getASPManager();

        itemset2.changeRow();
        mgr.submit(trans);

        ctx.removeGlobal("RTPMSIMULATED");
        okFindITEM2();

        return true;
    }

    public void saveNewITEM1()
    {
        trans.clear();

        if (saveReturnITEM1())
                newRowITEM1();
    }

    public void saveNewITEM2()
    {
        trans.clear();

        if (saveReturnITEM2())
                newRowITEM2();
    }

    public void deleteITEM1()
    {
        ASPManager mgr = getASPManager();

        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());

        itemset1.setRemoved();
        mgr.submit(trans);

        okFindITEM1();
    }

    public void deleteITEM2()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());

        itemset2.setRemoved();
        mgr.submit(trans);

        okFindITEM2();
    }

    public void deleteITEM3()
    {
        ASPManager mgr = getASPManager();

        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());

        itemset3.setRemoved();
        mgr.submit(trans);

        okFindITEM3();
    }

    public void deleteITEM4()
    {
        ASPManager mgr = getASPManager();

        if (itemlay4.isMultirowLayout())
            itemset4.goTo(itemset4.getRowSelected());

        itemset4.setRemoved();
        mgr.submit(trans);

        okFindITEM4();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void findInsertStdJobs()
    {
        ASPManager mgr = getASPManager();

    trans.clear();
    cmd = trans.addCustomFunction("GETSTATE","Job_Program_API.Get_Rowstate","STATE");
    cmd.addParameter("JOB_PROGRAM_ID", jobProgId);
    cmd.addParameter("JOB_PROGRAM_REVISION", jobProgRev);
    cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);

    trans = mgr.perform(trans); 

    if ("Active".equals(trans.getValue("GETSTATE/DATA/STATE")))
    {
        String sStdJobSimulationId = ctx.findGlobal("STDJOBSIMULATIONID");

        if (mgr.isEmpty(sStdJobSimulationId))
        {
                    trans.clear();

                    cmd = trans.addCustomCommand("FINDINS","Job_Program_Utility_API.Find_Insert_Std_Jobs");
                    cmd.addParameter("STD_JOB_SIM_ID",sStdJobSimulationId);
                    cmd.addParameter("JOB_PROGRAM_ID",jobProgId);
                    cmd.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
                    cmd.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);

                    trans = mgr.perform(trans);

                    sStdJobSimulationId = trans.getValue("FINDINS/DATA/STD_JOB_SIM_ID");
                    ctx.setGlobal("STDJOBSIMULATIONID",sStdJobSimulationId);

                    if (!mgr.isEmpty(sStdJobSimulationId))
                    {
                        okFindITEM1();
                        okFindITEM2();
                    }
                }
            }
            else
            {
                bWarning = true;
                sWarningMsg = mgr.translate("PCMWJOBPROGITEMSACT: Only Job Programs in Status \"Active\" can be added to PM Actions.");
            }
    }

    public void findInsertPmActions()  
    {
        ASPManager mgr = getASPManager();
        String sPmSimulationId = ctx.findGlobal("PMSIMULATIONID");

        trans.clear();
        cmd = trans.addCustomCommand("FINDINSPM","Pm_Action_API.Find_Insert_Pm_Actions");
        cmd.addParameter("PM_SIM_ID",sPmSimulationId);
        cmd.addParameter("MCH_CODE",mchCode);
        cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

        trans = mgr.perform(trans);

        sPmSimulationId = trans.getValue("FINDINSPM/DATA/PM_SIM_ID");
        ctx.setGlobal("PMSIMULATIONID",sPmSimulationId);

        if (!mgr.isEmpty(sPmSimulationId))
        {
            okFindITEM3();
            okFindITEM4();
        }
    }

    public void separateSimulate()
    {
        ASPManager mgr = getASPManager();

        String sPmType = "ActiveSeparate";
        String sStdJobType = "1";
        String sSimulatedPmNo = "";
        ctx.removeGlobal("SEPPMSIMULATED");

        if ("TRUE".equals(merge))
        {
            trans.clear();
            cmd = trans.addCustomCommand("SEPSIMMERGE","Job_Program_Utility_API.Simulate_Merged_Pm_Actions");
            cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
            cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
            cmd.addParameter("JOB_PROGRAM_ID", jobProgId);
            cmd.addParameter("JOB_PROGRAM_REVISION", jobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("ITEM1_STD_JOB_MAINT_ORG",maintOrg);
            cmd.addParameter("ITEM1_STD_JOB_ACTION",action);
            cmd.addParameter("ROUTE_ID",routeId);
            cmd.addParameter("ITEM1_STD_JOB_VALID_FROM",validFrom);
            cmd.addParameter("ITEM1_STD_JOB_VALID_TO",validTo);
            cmd.addParameter("SJOBTYPE",sStdJobType);

            trans = mgr.perform(trans);
            ctx.removeGlobal("PMSIMULATIONID");
            ctx.setGlobal("PMSIMULATIONID",trans.getValue("SEPSIMMERGE/DATA/PM_SIM_ID"));
        }
        else
        {
            int n = itemset1.countRows();

            itemset1.first();
            for (int i=0;i<n;i++)
            {
                trans.clear();
                cmd = trans.addCustomCommand("SEPSIM","Job_Program_Utility_API.Simulate_Pm_Action");
                cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
                cmd.addParameter("SSIMULATEDPMNO",sSimulatedPmNo);
                cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
                cmd.addParameter("JOB_PROGRAM_ID", jobProgId);
                cmd.addParameter("JOB_PROGRAM_REVISION", jobProgRev);
                cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
                cmd.addParameter("ITEM1_STD_JOB_ID",itemset1.getRow().getValue("STD_JOB_ID"));
                cmd.addParameter("ITEM1_STD_JOB_REVISION",itemset1.getRow().getValue("STD_JOB_REVISION"));
                cmd.addParameter("ITEM1_STD_JOB_CONTRACT",itemset1.getRow().getValue("STD_JOB_CONTRACT"));
                cmd.addParameter("MCH_CODE",mchCode);
                cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
                cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
                cmd.addParameter("ITEM1_STD_JOB_MAINT_ORG",maintOrg);
                cmd.addParameter("ITEM1_STD_JOB_ACTION",action);
                cmd.addParameter("ROUTE_ID",routeId);
                cmd.addParameter("ITEM1_STD_JOB_VALID_FROM",validFrom);
                cmd.addParameter("ITEM1_STD_JOB_VALID_TO",validTo);
                cmd.addParameter("SJOBTYPE",sPmType);

                trans = mgr.perform(trans);

                sSimulatedPmNo = trans.getValue("SEPSIM/DATA/SSIMULATEDPMNO");
                ctx.removeGlobal("PMSIMULATIONID");
                ctx.setGlobal("PMSIMULATIONID",trans.getValue("SEPSIM/DATA/PM_SIM_ID"));

                itemset1.next();
            }
        }
        ctx.setGlobal("SEPPMSIMULATED","TRUE");
        //Bug 71775, start
        okFindITEM1();
        okFindITEM3();

        if (itemset3.countRows()>0)
        {
            itemset3.first();
            for (int i=0;i<itemset3.countRows();i++)
            { 

              enSepGenarate = true;
              if (mgr.isEmpty(itemset3.getRow().getValue("SIMULATED_PM_REVISION")) || mgr.isEmpty(itemset3.getRow().getValue("SIMULATED_WO_SITE")) || mgr.isEmpty(itemset3.getRow().getValue("SIMULATED_ACTION_ID")) || mgr.isEmpty(itemset3.getRow().getValue("SIMULATED_ORG_CODE")))
              {   

                  enSepGenarate = false;
                  break;    
              }
              itemset3.next();
            }

        }
        //Bug 71775, end

        if ("TRUE".equals(merge))
            updatePmRevision(itemset1,itemset3);

        okFindITEM1();
        okFindITEM2();
        okFindITEM3();
        okFindITEM4();

    }

    public void roundSimulate()
    {
        ASPManager mgr = getASPManager();

        String sPmType = "ActiveRound";
        String sStdJobType = "2";
        String sSimulatedPmNo = "";
        ctx.removeGlobal("RTPMSIMULATED");

        if ("TRUE".equals(merge))
        {
            trans.clear();
            cmd = trans.addCustomCommand("RNDSIMMERGE","Job_Program_Utility_API.Simulate_Merged_Pm_Actions");
            cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
            cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
            cmd.addParameter("JOB_PROGRAM_ID", jobProgId);
            cmd.addParameter("JOB_PROGRAM_REVISION", jobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("ITEM1_STD_JOB_MAINT_ORG",maintOrg);
            cmd.addParameter("ITEM1_STD_JOB_ACTION",action);
            cmd.addParameter("ROUTE_ID",routeId);
            cmd.addParameter("ITEM1_STD_JOB_VALID_FROM",validFrom);
            cmd.addParameter("ITEM1_STD_JOB_VALID_TO",validTo);
            cmd.addParameter("SJOBTYPE",sStdJobType);

            trans = mgr.perform(trans);
            ctx.removeGlobal("PMSIMULATIONID");
            ctx.setGlobal("PMSIMULATIONID",trans.getValue("RNDSIMMERGE/DATA/PM_SIM_ID"));

        }
        else
        {
            int n = itemset2.countRows();

            itemset2.first();
            for (int i=0;i<n;i++)
            {
                trans.clear();
                cmd = trans.addCustomCommand("RNDSIM","Job_Program_Utility_API.Simulate_Pm_Action");
                cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
                cmd.addParameter("SSIMULATEDPMNO",sSimulatedPmNo);
                cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
                cmd.addParameter("JOB_PROGRAM_ID", jobProgId);
                cmd.addParameter("JOB_PROGRAM_REVISION", jobProgRev);
                cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
                cmd.addParameter("ITEM2_STD_JOB_ID",itemset2.getRow().getValue("STD_JOB_ID"));
                cmd.addParameter("ITEM2_STD_JOB_REVISION",itemset2.getRow().getValue("STD_JOB_REVISION"));
                cmd.addParameter("ITEM2_STD_JOB_CONTRACT",itemset2.getRow().getValue("STD_JOB_CONTRACT"));
                cmd.addParameter("MCH_CODE",mchCode);
                cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
                cmd.addParameter("JOB_PROGRAM_CONTRACT", jobProgContract);
                cmd.addParameter("ITEM2_STD_JOB_MAINT_ORG",maintOrg);
                cmd.addParameter("ITEM2_STD_JOB_ACTION",action);
                cmd.addParameter("ROUTE_ID",routeId);
                cmd.addParameter("ITEM2_STD_JOB_VALID_FROM",validFrom);
                cmd.addParameter("ITEM2_STD_JOB_VALID_TO",validTo);
                cmd.addParameter("SJOBTYPE",sPmType);

                trans = mgr.perform(trans);

                sSimulatedPmNo = trans.getValue("RNDSIM/DATA/SSIMULATEDPMNO");
                ctx.removeGlobal("PMSIMULATIONID");
                ctx.setGlobal("PMSIMULATIONID",trans.getValue("RNDSIM/DATA/PM_SIM_ID"));

                itemset2.next();
            }
        }
        ctx.setGlobal("RTPMSIMULATED","TRUE");
        //Bug 71775, start
        okFindITEM2();
        okFindITEM4();

        if (itemset4.countRows()>0)
        {   
            itemset4.first();
            for (int i=0;i<itemset4.countRows();i++)
            {
              
              enRndGenarate = true;
              if (mgr.isEmpty(itemset4.getRow().getValue("SIMULATED_PM_REVISION")) || mgr.isEmpty(itemset4.getRow().getValue("SIMULATED_WO_SITE")) || mgr.isEmpty(itemset4.getRow().getValue("SIMULATED_ACTION_ID")) || mgr.isEmpty(itemset4.getRow().getValue("SIMULATED_ORG_CODE")) || mgr.isEmpty(itemset4.getRow().getValue("SIMULATED_ROUTE_ID")))
              {
                  enRndGenarate = false;
                  break;    
              }
              itemset4.next();
            }
        }
        //Bug 71775, end
        
        if ("TRUE".equals(merge))
            updatePmRevision(itemset2,itemset4);

        okFindITEM1();
        okFindITEM2();
        okFindITEM3();
        okFindITEM4();
    }

    public boolean updatePmRevision(ASPRowSet set1,ASPRowSet set2)
    {
        ASPManager mgr = getASPManager();
        int n = set2.countRows();
        int m = set1.countRows();
        String conn_pm_no = "";
        String conn_pm_rev = "";
        String sim_pm_no = "";
        String sim_pm_rev = "";

        if (n>0 && m>0)
        {
            set2.first();
            for (int i=0;i<n;i++)
            {
                sim_pm_no = set2.getRow().getValue("SIMULATED_PM_NO");
                sim_pm_rev = set2.getRow().getValue("SIMULATED_PM_REVISION");

                if (sim_pm_no.length() > 3)
                {
                    trans.clear();

                    cmd = trans.addCustomFunction("GETNEWROW","Pm_Action_Api.Check_New_Row","IS_NEW_ROW");
                    cmd.addParameter("ITEM3_SIMULATED_PM_NO",sim_pm_no);
                    cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",sim_pm_rev);

                    cmd = trans.addCustomFunction("VALIDPM","Pm_Action_Api.Is_Valid_Pm","IS_VALID");
                    cmd.addParameter("ITEM3_SIMULATED_PM_NO",sim_pm_no);
                    cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",sim_pm_rev);

                    trans = mgr.validate(trans);

                    if ("FALSE".equals(trans.getValue("GETNEWROW/DATA/IS_NEW_ROW")) && "TRUE".equals(trans.getValue("VALIDPM/DATA/IS_VALID")))
                    {
                        set1.first();
                        for (int j=0;j<m;j++)
                        {
                            conn_pm_no = set1.getRow().getValue("CONNECTED_PM_NO");
                            conn_pm_rev = set1.getRow().getValue("CONNECTED_PM_REVISION");

                            if (conn_pm_no.length() > 3 && (sim_pm_no.equals(conn_pm_no)) && (sim_pm_rev.equals(conn_pm_rev)))
                            {
                                trans.clear();
                                cmd = trans.addCustomFunction("GETSTATE","Pm_Action_Api.Get_Pm_State","STATE");
                                cmd.addParameter("ITEM1_CONNECTED_PM_NO",conn_pm_no);
                                cmd.addParameter("ITEM1_CONNECTED_PM_REVISION",conn_pm_rev);
                                trans = mgr.validate(trans);

                                if ("Active".equals(trans.getValue("GETSTATE/DATA/STATE")))
                                {
                                    trans.clear();
                                    cmd = trans.addCustomCommand("MODIFYSTDJOB","SIMULATED_STD_JOB_API.Modify_Std_Jobs");
                                    cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
                                    cmd.addParameter("ITEM3_SIMULATED_PM_NO",sim_pm_no);
                                    cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",sim_pm_rev);
                                    mgr.perform(trans);
                                    
                                    mgr.showAlert(mgr.translate("PCMWJOBPROGTOOBJITEMSREVREQ1: A new revision is required for PM No &1",conn_pm_no));
                                    enRndGenarate = false;
                                    return true;
                                }
                            }
                            set1.next();
                        }
                    }
                }
                set2.next();
            }
        }
        return false;
    } 

    public void separateGenarate()
    {
        ASPManager mgr = getASPManager();
        String sNewRevReq = "FALSE";

        if (itemset3.countRows()>0 && itemset1.countRows()>0)
        {
            int n = itemset3.countRows();
            itemset3.first();

            for (int i=0;i<n;i++)
            {
                String pm_no = itemset3.getRow().getValue("SIMULATED_PM_NO");
                String pm_rev = itemset3.getRow().getValue("SIMULATED_PM_REVISION");

                if (pm_no.length() > 3)
                {
                    // Check whether the PM No and Revision is used by any of the std jobs
                    String sPmRevUsedOnStdJob = "FALSE";
                    int t = itemset1.countRows();
                    itemset1.first();
                    for (int j=0;j<t;j++)
                    {
                        String conn_pm_no = itemset1.getRow().getValue("CONNECTED_PM_NO");
                        String conn_pm_rev = itemset1.getRow().getValue("CONNECTED_PM_REVISION");

                        if (conn_pm_no.length()>3 && pm_no.equals(conn_pm_no) && pm_rev.equals(conn_pm_rev))
                            sPmRevUsedOnStdJob = "TRUE";

                        itemset1.next();
                    }

                    if ("TRUE".equals(sPmRevUsedOnStdJob))
                    {
                        trans.clear();

                        cmd = trans.addCustomFunction("PMEXIST","Pm_Action_API.Check_Exist","PM_EXIST");
                        cmd.addParameter("ITEM3_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",pm_rev);

                        cmd = trans.addCustomFunction("PMSTATE","Pm_Action_API.Get_Pm_State","STATE");
                        cmd.addParameter("ITEM3_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",pm_rev);

                        cmd = trans.addCustomFunction("WOEXIST","Pm_Action_Calendar_Plan_API.Generated_Wo_Exist","WO_EXIST");
                        cmd.addParameter("ITEM3_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM3_SIMULATED_PM_REVISION",pm_rev);

                        trans = mgr.perform(trans);

                        String pmExist = trans.getValue("PMEXIST/DATA/PM_EXIST");
                        String pmState = trans.getValue("PMSTATE/DATA/STATE");
                        String woExist = trans.getValue("WOEXIST/DATA/WO_EXIST");

                        if ("TRUE".equals(pmExist) && "Active".equals(pmState) && "TRUE".equals(woExist))
                        {
                            sNewRevReq = "TRUE";
                            mgr.showAlert(mgr.translate("PCMWJOBPROGTOOBJITEMSREVREQ1: A new revision is required for PM No &1",pm_no));
                            break;
                        }
                    }
                }
                itemset3.next();
            }
            if ("FALSE".equals(sNewRevReq))
            {
                trans.clear();
                cmd = trans.addCustomCommand("GENPM","Job_Program_Utility_API.Generate_Pm_Actions");
                cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
                cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
                cmd.addParameter("SJOBTYPE","ActiveSeparate");
                mgr.perform(trans);

                enSepGenarate = false;
            }
            if (!mgr.isEmpty(ctx.findGlobal("PMSIMULATIONID")))
            {
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }
        }
    }

    public void roundGenarate()
    {
        ASPManager mgr = getASPManager();
        String sNewRevReq = "FALSE";

        if (itemset4.countRows()>0 && itemset2.countRows()>0)
        {
            int n = itemset4.countRows();
            itemset4.first();
            for (int i=0;i<n;i++)
            {
                String pm_no = itemset4.getRow().getValue("SIMULATED_PM_NO");
                String pm_rev = itemset4.getRow().getValue("SIMULATED_PM_REVISION");

                if (pm_no.length() > 3)
                {
                    // Check whether the PM No and Revision is used by any of the std jobs
                    String sPmRevUsedOnStdJob = "FALSE";
                    int t = itemset2.countRows();
                    itemset2.first();
                    for (int j=0;j<t;j++)
                    {
                        String conn_pm_no = itemset2.getRow().getValue("CONNECTED_PM_NO");
                        String conn_pm_rev = itemset2.getRow().getValue("CONNECTED_PM_REVISION");

                        if (conn_pm_no.length()>3 && pm_no.equals(conn_pm_no) && pm_rev.equals(conn_pm_rev))
                            sPmRevUsedOnStdJob = "TRUE";

                        itemset2.next();
                    }

                    if ("TRUE".equals(sPmRevUsedOnStdJob))
                    {
                        trans.clear();

                        cmd = trans.addCustomFunction("PMEXIST","Pm_Action_API.Check_Exist","PM_EXIST");
                        cmd.addParameter("ITEM4_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM4_SIMULATED_PM_REVISION",pm_rev);

                        cmd = trans.addCustomFunction("PMSTATE","Pm_Action_API.Get_Pm_State","STATE");
                        cmd.addParameter("ITEM4_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM4_SIMULATED_PM_REVISION",pm_rev);

                        cmd = trans.addCustomFunction("WOEXIST","Pm_Action_Calendar_Plan_API.Generated_Wo_Exist","WO_EXIST");
                        cmd.addParameter("ITEM4_SIMULATED_PM_NO",pm_no);
                        cmd.addParameter("ITEM4_SIMULATED_PM_REVISION",pm_rev);

                        trans = mgr.perform(trans);

                        String pmExist = trans.getValue("PMEXIST/DATA/PM_EXIST");
                        String pmState = trans.getValue("PMSTATE/DATA/STATE");
                        String woExist = trans.getValue("WOEXIST/DATA/WO_EXIST");

                        if ("TRUE".equals(pmExist) && "Active".equals(pmState) && "TRUE".equals(woExist))
                        {
                            sNewRevReq = "TRUE";
                            mgr.showAlert(mgr.translate("PCMWJOBPROGTOOBJITEMSREVREQ0: A new revision is required for PM No &0",pm_no));
                            break;
                        }
                    }
                }
                itemset4.next();
            }
            if ("FALSE".equals(sNewRevReq))
            {
                trans.clear();
                cmd = trans.addCustomCommand("GENPM","Job_Program_Utility_API.Generate_Pm_Actions");
                cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
                cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
                cmd.addParameter("SJOBTYPE","ActiveRound");
                mgr.perform(trans);

                enRndGenarate = false;
            }
            if (!mgr.isEmpty(ctx.findGlobal("PMSIMULATIONID")))
            {
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }
        }
    }

    public void removeStdJobs()
    {
        ASPManager mgr = getASPManager();
        
        trans.clear();
        cmd = trans.addCustomCommand("REMSTDJOBS","Simulated_Std_Job_API.Remove_Std_Jobs");
        cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        mgr.perform(trans);
    }

    public void removeRelatedPMs()
    {
        ASPManager mgr = getASPManager();
        
        trans.clear();
        cmd = trans.addCustomCommand("REMPMS","Simulated_Pm_Action_API.Remove_Related_Pm");
        cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
        mgr.perform(trans);
    }

    public void removePmActions()
    {
        ASPManager mgr = getASPManager();
        
        trans.clear();
        cmd = trans.addCustomCommand("REMPMS","Simulated_Pm_Action_API.Remove_Pm_Actions");
        cmd.addParameter("PM_SIM_ID",ctx.findGlobal("PMSIMULATIONID"));
        cmd.addParameter("STD_JOB_SIM_ID",ctx.findGlobal("STDJOBSIMULATIONID"));
        mgr.perform(trans);

        ctx.removeGlobal("PMSIMULATIONID");
    }

    public void ClearStdJobTables()
    {
        itemset1.clear();
        itemset2.clear();
    }

    public void ClearPMTables()
    {
        itemset3.clear();
        itemset4.clear();
    }

    public void addStdJobsByCategoryITEM1()
    {
        ASPManager mgr = getASPManager();

        ctx.setGlobal("MCH_CODE",mchCode);
        ctx.setGlobal("MCH_CODE_CONTRACT",mchContract);
        ctx.setGlobal("JOB_PROGRAM_ID",jobProgId);
        ctx.setGlobal("JOB_PROGRAM_REVISION",jobProgRev);
        ctx.setGlobal("JOB_PROGRAM_CONTRACT",jobProgContract);
        ctx.setGlobal("MAINT_ORG",maintOrg);
        ctx.setGlobal("ACTION",action);
        ctx.setGlobal("ROUTE_ID",routeId);
        ctx.setGlobal("MERGE",merge);
        ctx.setGlobal("VALID_FROM",validFrom);
        ctx.setGlobal("VALID_TO",validTo);
        
        if (itemlay2.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        int currow = itemset1.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "StdJobsByCategoryDlg.page?JOB_PROGRAM_ID=" + mgr.URLEncode(jobProgId) +
                    "&JOB_PROGRAM_REVISION=" + mgr.URLEncode(jobProgRev)+
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(jobProgContract)+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qryString)+
                    "&STDJOBSIMULATIONID="+ mgr.URLEncode(ctx.findGlobal("STDJOBSIMULATIONID"));

        newWinHandle = "StdJobsByCategoryDlg"; 
        ctx.setGlobal("CURRROW",String.valueOf(currow));
    }

    public void showStdJobITEM1()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        int currow = itemset1.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "SeparateStandardJob2.page?STD_JOB_ID=" + mgr.URLEncode(itemset1.getRow().getValue("STD_JOB_ID")) +
                    "&STD_JOB_REVISION=" + mgr.URLEncode(itemset1.getRow().getValue("STD_JOB_REVISION"))+
                    "&STD_JOB_CONTRACT=" + mgr.URLEncode(itemset1.getRow().getValue("STD_JOB_CONTRACT"))+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) ;

        newWinHandle = "SeparateStandardJob2"; 
    }

    public void addStdJobsByCategoryITEM2()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());
        else
            itemset2.selectRow();

        int currow = itemset2.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "StdJobsByCategoryDlg.page?JOB_PROGRAM_ID=" + mgr.URLEncode(jobProgId) +
                    "&JOB_PROGRAM_REVISION=" + mgr.URLEncode(jobProgRev)+
                    "&JOB_PROGRAM_CONTRACT=" + mgr.URLEncode(jobProgContract)+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qryString) +
                    "&STDJOBSIMULATIONID="+ mgr.URLEncode(ctx.findGlobal("STDJOBSIMULATIONID"));

        newWinHandle = "StdJobsByCategoryDlg"; 
        ctx.setGlobal("CURRROW",String.valueOf(currow));
    }

    public void showStdJobITEM2()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());
        else
            itemset2.selectRow();

        int currow = itemset2.getCurrentRowNo();

        bOpenNewWindow = true;
        // Bug 71777 start
        urlString = "RoundStandardJob.page?STD_JOB_ID=" + mgr.URLEncode(itemset2.getRow().getValue("STD_JOB_ID")) +
                    "&STD_JOB_REVISION=" + mgr.URLEncode(itemset2.getRow().getValue("STD_JOB_REVISION"))+
                    "&STD_JOB_CONTRACT=" + mgr.URLEncode(itemset2.getRow().getValue("STD_JOB_CONTRACT"))+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) ;
        // Bug 71777 end

        newWinHandle = "RoundStandardJob"; 
    }

    public void showPmITEM3()
    {
        ASPManager mgr = getASPManager();

        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());
        else
            itemset3.selectRow();

        int currow = itemset3.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "PmAction.page?PM_NO=" + mgr.URLEncode(itemset3.getRow().getValue("SIMULATED_PM_NO")) +
                    "&PM_REVISION=" + mgr.URLEncode(itemset3.getRow().getValue("SIMULATED_PM_REVISION"))+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) ;

        newWinHandle = "PmAction"; 
    }

    public void showPmITEM4()
    {
        ASPManager mgr = getASPManager();

        if (itemlay4.isMultirowLayout())
            itemset4.goTo(itemset4.getRowSelected());
        else
            itemset4.selectRow();

        int currow = itemset4.getCurrentRowNo();

        bOpenNewWindow = true;
        urlString = "PmActionRound.page?PM_NO=" + mgr.URLEncode(itemset4.getRow().getValue("SIMULATED_PM_NO")) +
                    "&PM_REVISION=" + mgr.URLEncode(itemset4.getRow().getValue("SIMULATED_PM_REVISION"))+
                    "&FRMNAME=" + mgr.URLEncode("jobProgToObjectItems") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) ;

        newWinHandle = "PmActionRound"; 
    }

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        this.disableHeader();
        this.disableHelp();
        this.disableHomeIcon();
        this.disableNavigate();
        this.disableOptions();

        //************ Separate Standard Jobs ****************************

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden(); 

        f = itemblk1.addField("ITEM1_CONNECTED_PM_NO");
        f.setDbName("CONNECTED_PM_NO");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1CONNECTEDPMNO: PM No");
        f.setLOV("SimulatedPmActionLov.page");
        f.setQueryable();
        f.setMaxLength(10);
        f.setSize(10);
        f.setCustomValidation("ITEM1_CONNECTED_PM_NO,ITEM1_CONNECTED_PM_REVISION","ITEM1_CONNECTED_PM_NO,ITEM1_CONNECTED_PM_REVISION,ITEM1_PM_SIMULATION_ID");

        f = itemblk1.addField("ITEM1_CONNECTED_PM_REVISION");
        f.setDbName("CONNECTED_PM_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1CONNECTEDPMREV: PM Revision");
        f.setLOV("SimulatedPmActionLov.page","ITEM1_CONNECTED_PM_NO SIMULATED_PM_NO");
        f.setQueryable();
        f.setMaxLength(10);
        f.setUpperCase();
        f.setSize(10);
        f.setCustomValidation("ITEM1_CONNECTED_PM_NO,ITEM1_CONNECTED_PM_REVISION","ITEM1_CONNECTED_PM_NO,ITEM1_CONNECTED_PM_REVISION,ITEM1_PM_SIMULATION_ID");

        f = itemblk1.addField("ITEM1_STD_JOB_CONTRACT");
        f.setDbName("STD_JOB_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCONTRACT: Site");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setMaxLength(5);
        f.setReadOnly();
        f.setSize(5);

        f = itemblk1.addField("ITEM1_STD_JOB_ID");
        f.setDbName("STD_JOB_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBID: Standard Job ID");
        f.setLOV("StandardJobLov.page","ITEM1_STD_JOB_CONTRACT CONTRACT",600,450);
        f.setLOVProperty("WHERE","STANDARD_JOB_TYPE_DB = '1'");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(12);
        f.setSize(12);
        f.setCustomValidation("ITEM1_STD_JOB_CONTRACT,ITEM1_STD_JOB_ID,ITEM1_STD_JOB_REVISION","ITEM1_STD_JOB_ID,ITEM1_STD_JOB_REVISION,ITEM1_STD_JOB_TYPE,ITEM1_WORK_DESC,ITEM1_DEFINITION,ITEM1_STATUS,ITEM1_TYPE_ID,ITEM1_TYPE_DESC");

        f = itemblk1.addField("ITEM1_STD_JOB_REVISION");
        f.setDbName("STD_JOB_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBREVISION: Revision");
        f.setLOV("StandardJobLov.page","ITEM1_STD_JOB_ID STD_JOB_ID,ITEM1_STD_JOB_CONTRACT CONTRACT");
        f.setLOVProperty("WHERE","STANDARD_JOB_TYPE_DB = '1'");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(6);
        f.setSize(6);
        f.setCustomValidation("ITEM1_STD_JOB_CONTRACT,ITEM1_STD_JOB_ID,ITEM1_STD_JOB_REVISION","ITEM1_STD_JOB_ID,ITEM1_STD_JOB_REVISION,ITEM1_STD_JOB_TYPE,ITEM1_WORK_DESC,ITEM1_DEFINITION,ITEM1_STATUS,ITEM1_TYPE_ID,ITEM1_TYPE_DESC");

        f = itemblk1.addField("ITEM1_WORK_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1CONNECTED: Work Description");
        f.setFunction("STANDARD_JOB_API.Get_Work_Description(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setSize(40);

        f = itemblk1.addField("ITEM1_DEFINITION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1DEFINITION: Description");
        f.setFunction("STANDARD_JOB_API.Get_Definition(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(15);

        f = itemblk1.addField("ITEM1_STATUS");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STATUS: Status");
        f.setFunction("STANDARD_JOB_API.Get_Status(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(11);

        f = itemblk1.addField("ITEM1_STD_JOB_TYPE");
        f.setDbName("STD_JOB_TYPE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBTYPE: Type");
        f.setHidden();
        
        f = itemblk1.addField("ITEM1_TYPE_ID");  
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1TYPEID: Standard Job Type");
        f.setFunction("STANDARD_JOB_API.Get_Type_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_TYPE_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1TYPEDESC: Description");
        f.setFunction("Std_Job_Type_API.Get_Description(STANDARD_JOB_API.Get_Type_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_ACTION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBACTION: Action");
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_ACTION_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBACTIONDESC: Action Description");
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(15);

        f = itemblk1.addField("ITEM1_STD_JOB_MAINT_ORG");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBMAINTORG: Maint. Org.");
        f.setFunction("STANDARD_JOB_API.Get_Org_Code(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_WORK_TYPE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBWORKTYPEID: Work Type");
        f.setFunction("STANDARD_JOB_API.Get_Work_Type_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_OP_STATUS_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBOPSTATUSID: Operational Status");
        f.setFunction("STANDARD_JOB_API.Get_Op_Status_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_PRIORITY_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBPRIORITYID: Priority");
        f.setFunction("STANDARD_JOB_API.Get_Priority_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_EXEC_TIME","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBEXECTIME: Execution Time");
        f.setFunction("STANDARD_JOB_API.Get_Execution_Time(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_SIGNATURE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBSIGNATURE: Planned By");
        f.setFunction("STANDARD_JOB_API.Get_Signature(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_CREATED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCREATEDBY: Created By");
        f.setFunction("STANDARD_JOB_API.Get_Created_By(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();
        
        f = itemblk1.addField("ITEM1_STD_JOB_CRE_DATE","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCREDATE: Creation Date");
        f.setFunction("STANDARD_JOB_API.Get_Creation_Date(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_CHANGED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCHANGEDBY: Changed By");
        f.setFunction("STANDARD_JOB_API.Get_Changed_By(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_LAST_CHANGED","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBLASTCHANGED: Last Changed");
        f.setFunction("STANDARD_JOB_API.Get_Last_Changed(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_START_VAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBSTARTVAL: Start Value");
        f.setFunction("STANDARD_JOB_API.Get_Start_Value(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBSTARTUNIT: Start Unit");
        f.setFunction("Pm_Start_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Start_Unit(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_INTERVAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Interval(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBINTERVALUNIT: Interval Unit");
        f.setFunction("Pm_Interval_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Interval_Unit(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_CALL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCALL: Event");
        f.setFunction("STANDARD_JOB_API.Get_Call_Code(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_REVISION,:ITEM1_STD_JOB_CONTRACT)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_CALL_START","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCALLSTART: Start");
        f.setFunction("STANDARD_JOB_API.Get_Start_Call(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_CALL_INTERVAL","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCALLINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Call_Interval(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk1.addField("ITEM1_STD_JOB_VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBVALIDFROM: Valid From");
        f.setFunction("STANDARD_JOB_API.Get_Valid_From(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBVALIDTO: Valid To");
        f.setFunction("STANDARD_JOB_API.Get_Valid_To(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_STD_JOB_CALENDAR_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCALENDARID: Calendar ID");
        f.setFunction("STANDARD_JOB_API.Get_Calendar_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();
        
        f = itemblk1.addField("ITEM1_STD_JOB_CAL_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBCALDESC: Description");
        f.setFunction("Work_Time_Calendar_API.Get_Description(STANDARD_JOB_API.Get_Calendar_Id(:ITEM1_STD_JOB_ID,:ITEM1_STD_JOB_CONTRACT,:ITEM1_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        //Hidden fields

        f = itemblk1.addField("ITEM1_STD_JOB_JOB_PROGRAM_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBJOBPROGRAMID: Job Program ID");
        f.setDbName("JOB_PROGRAM_ID");
        f.setQueryable();
        f.setHidden();

        f = itemblk1.addField("ITEM1_STD_JOB_JOB_PROGRAM_REV");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBJOBPROGRAMREV: Job Program Revision");
        f.setDbName("JOB_PROGRAM_REVISION");
        f.setHidden();
        f.setQueryable();

        f = itemblk1.addField("ITEM1_STD_JOB_JOB_PROGRAM_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBJOBPROGRAMCONTRACT: Job Program Contract");
        f.setDbName("JOB_PROGRAM_CONTRACT");
        f.setHidden();
        f.setQueryable();

        f = itemblk1.addField("ITEM1_STD_JOB_SIMULATION_ID","Number"); 
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1STDJOBSIMULATIONID: Std Job Simulation Id");
        f.setDbName("STD_JOB_SIMULATION_ID");
        f.setHidden();
        f.setQueryable();

        f = itemblk1.addField("ITEM1_PM_SIMULATION_ID","Number"); 
        f.setLabel("PCMWJOBPROGTOOBJECTITEM1PMSIMULATIONID: PM Simulation Id");
        f.setDbName("PM_SIMULATION_ID");
        f.setHidden();

        itemblk1.setView("SIMULATED_STD_JOB");
        itemblk1.defineCommand("SIMULATED_STD_JOB_API","New__,Modify__,Remove__");
        itemblk1.disableDocMan();

        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.setBorderLines(false,true);
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1");
        itembar1.defineCommand(itembar1.SAVENEW,"saveNewITEM1");
        itembar1.defineCommand(itembar1.DELETE,"deleteITEM1");

        itemtbl1 = mgr.newASPTable(itemblk1);

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setDialogColumns(2); 
        
        itembar1.addCustomCommand("activateSeparate","");
        itembar1.addCustomCommand("activateRoute","");
        itembar1.addCustomCommand("addStdJobsByCategoryITEM1",mgr.translate("PCMWJOBPROGTOOBJECTITEMSADDJOBSBYCAT: Add Std Jobs by Category..."));
        itembar1.addCustomCommand("showStdJobITEM1",mgr.translate("PCMWJOBPROGTOOBJECTITEMSSTDJOBS: Standard Job..."));

        itembar1.enableMultirowAction();
        itembar1.removeFromMultirowAction("showStdJobITEM1");
        itembar1.forceEnableMultiActionCommand("addStdJobsByCategoryITEM1");

        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWJOBPROGTOOBJECTSEPARATE: Separate"), "javascript:commandSet('ITEM1.activateSeparate', '')");
        tabs.addTab(mgr.translate("PCMWJOBPROGTOOBJECTROUTE: Route"), "javascript:commandSet('ITEM1.activateRoute', '')");

        //********** Route Standard Jobs *********************************

        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden(); 

        f = itemblk2.addField("ITEM2_CONNECTED_PM_NO");
        f.setDbName("CONNECTED_PM_NO");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2CONNECTEDPMNO: PM No");
        f.setLOV("SimulatedPmActionLov.page");
        f.setQueryable();
        f.setMaxLength(10);
        f.setSize(10);
        f.setCustomValidation("ITEM2_CONNECTED_PM_NO,ITEM2_CONNECTED_PM_REVISION","ITEM2_CONNECTED_PM_NO,ITEM2_CONNECTED_PM_REVISION,ITEM2_PM_SIMULATION_ID");

        f = itemblk2.addField("ITEM2_CONNECTED_PM_REVISION");
        f.setDbName("CONNECTED_PM_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2CONNECTEDPMREV: PM Revision");
        f.setLOV("SimulatedPmActionLov.page","ITEM2_CONNECTED_PM_NO SIMULATED_PM_NO");
        f.setQueryable();
        f.setMaxLength(10);
        f.setUpperCase();
        f.setSize(10);
        f.setCustomValidation("ITEM2_CONNECTED_PM_NO,ITEM2_CONNECTED_PM_REVISION","ITEM2_CONNECTED_PM_NO,ITEM2_CONNECTED_PM_REVISION,ITEM2_PM_SIMULATION_ID");

        f = itemblk2.addField("ITEM2_STD_JOB_CONTRACT");
        f.setDbName("STD_JOB_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCONTRACT: Site");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setMaxLength(5);
        f.setReadOnly();
        f.setSize(5);

        f = itemblk2.addField("ITEM2_STD_JOB_ID");
        f.setDbName("STD_JOB_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBID: Standard Job ID");
        f.setLOV("StandardJobLov.page","ITEM2_STD_JOB_CONTRACT CONTRACT",600,450);
        f.setLOVProperty("WHERE","STANDARD_JOB_TYPE_DB = '2'");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(12);
        f.setSize(12);
        f.setCustomValidation("ITEM2_STD_JOB_CONTRACT,ITEM2_STD_JOB_ID,ITEM2_STD_JOB_REVISION","ITEM2_STD_JOB_ID,ITEM2_STD_JOB_REVISION,ITEM2_STD_JOB_TYPE,ITEM2_WORK_DESC,ITEM2_DEFINITION,ITEM2_STATUS,ITEM2_TYPE_ID,ITEM2_TYPE_DESC");

        f = itemblk2.addField("ITEM2_STD_JOB_REVISION");
        f.setDbName("STD_JOB_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBREVISION: Revision");
        f.setLOV("StandardJobLov.page","ITEM2_STD_JOB_ID STD_JOB_ID,ITEM2_STD_JOB_CONTRACT CONTRACT");
        f.setLOVProperty("WHERE","STANDARD_JOB_TYPE_DB = '2'");
        f.setQueryable();
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(6);
        f.setSize(6);
        f.setCustomValidation("ITEM2_STD_JOB_CONTRACT,ITEM2_STD_JOB_ID,ITEM2_STD_JOB_REVISION","ITEM2_STD_JOB_ID,ITEM2_STD_JOB_REVISION,ITEM2_STD_JOB_TYPE,ITEM2_WORK_DESC,ITEM2_DEFINITION,ITEM2_STATUS,ITEM2_TYPE_ID,ITEM2_TYPE_DESC");

        f = itemblk2.addField("ITEM2_WORK_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECT1TEM2CONNECTED: Work Description");
        f.setFunction("STANDARD_JOB_API.Get_Work_Description(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setSize(40);

        f = itemblk2.addField("ITEM2_DEFINITION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2DEFINITION: Description");
        f.setFunction("STANDARD_JOB_API.Get_Definition(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(15);

        f = itemblk2.addField("ITEM2_STATUS");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STATUS: Status");
        f.setFunction("STANDARD_JOB_API.Get_Status(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(11);

        f = itemblk2.addField("ITEM2_STD_JOB_TYPE");
        f.setDbName("STD_JOB_TYPE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBTYPE: Type");
        f.setHidden();
        
        f = itemblk2.addField("ITEM2_TYPE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2TYPEID: Standard Job Type");
        f.setFunction("STANDARD_JOB_API.Get_Type_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_TYPE_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2TYPEDESC: Description");
        f.setFunction("Std_Job_Type_API.Get_Description(STANDARD_JOB_API.Get_Type_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_ACTION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBACTION: Action");
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_ACTION_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBACTIONDESC: Action Description");
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(15);

        f = itemblk2.addField("ITEM2_STD_JOB_MAINT_ORG");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBMAINTORG: Maint. Org.");
        f.setFunction("STANDARD_JOB_API.Get_Org_Code(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_WORK_TYPE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBWORKTYPEID: Work Type");
        f.setFunction("STANDARD_JOB_API.Get_Work_Type_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_OP_STATUS_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBOPSTATUSID: Operational Status");
        f.setFunction("STANDARD_JOB_API.Get_Op_Status_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_PRIORITY_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBPRIORITYID: Priority");
        f.setFunction("STANDARD_JOB_API.Get_Priority_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_EXEC_TIME","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBEXECTIME: Execution Time");
        f.setFunction("STANDARD_JOB_API.Get_Execution_Time(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_SIGNATURE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBSIGNATURE: Planned By");
        f.setFunction("STANDARD_JOB_API.Get_Signature(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_CREATED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCREATEDBY: Created By");
        f.setFunction("STANDARD_JOB_API.Get_Created_By(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();
        
        f = itemblk2.addField("ITEM2_STD_JOB_CRE_DATE","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCREDATE: Creation Date");
        f.setFunction("STANDARD_JOB_API.Get_Creation_Date(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_CHANGED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCHANGEDBY: Changed By");
        f.setFunction("STANDARD_JOB_API.Get_Changed_By(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_LAST_CHANGED","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBLASTCHANGED: Last Changed");
        f.setFunction("STANDARD_JOB_API.Get_Last_Changed(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_START_VAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBSTARTVAL: Start Value");
        f.setFunction("STANDARD_JOB_API.Get_Start_Value(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBSTARTUNIT: Start Unit");
        f.setFunction("Pm_Start_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Start_Unit(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_INTERVAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Interval(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBINTERVALUNIT: Interval Unit");
        f.setFunction("Pm_Interval_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Interval_Unit(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_CALL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCALL: Event");
        f.setFunction("STANDARD_JOB_API.Get_Call_Code(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_REVISION,:ITEM2_STD_JOB_CONTRACT)");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_CALL_START","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCALLSTART: Start");
        f.setFunction("STANDARD_JOB_API.Get_Start_Call(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_CALL_INTERVAL","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCALLINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Call_Interval(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION) ");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(10);

        f = itemblk2.addField("ITEM2_STD_JOB_VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBVALIDFROM: Valid From");
        f.setFunction("STANDARD_JOB_API.Get_Valid_From(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBVALIDTO: Valid To");
        f.setFunction("STANDARD_JOB_API.Get_Valid_To(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_STD_JOB_CALENDAR_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCALENDARID: Calendar ID");
        f.setFunction("STANDARD_JOB_API.Get_Calendar_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION)");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();
        
        f = itemblk2.addField("ITEM2_STD_JOB_CAL_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBCALDESC: Description");
        f.setFunction("Work_Time_Calendar_API.Get_Description(STANDARD_JOB_API.Get_Calendar_Id(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION))");
        f.setQueryable();
        f.setReadOnly();
        f.setDefaultNotVisible();

        //Hidden fields

        f = itemblk2.addField("ITEM2_STD_JOB_JOB_PROGRAM_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBJOBPROGRAMID: Job Program ID");
        f.setDbName("JOB_PROGRAM_ID");
        f.setQueryable();
        f.setHidden();

        f = itemblk2.addField("ITEM2_STD_JOB_JOB_PROGRAM_REV");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBJOBPROGRAMREV: Job Program Revision");
        f.setDbName("JOB_PROGRAM_REVISION");
        f.setHidden();
        f.setQueryable();

        f = itemblk2.addField("ITEM2_STD_JOB_JOB_PROGRAM_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBJOBPROGRAMCONTRACT: Job Program Contract");
        f.setDbName("JOB_PROGRAM_CONTRACT");
        f.setHidden();
        f.setQueryable();

        f = itemblk2.addField("ITEM2_STD_JOB_SIMULATION_ID","Number"); 
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2STDJOBSIMULATIONID: Std Job Simulation Id");
        f.setDbName("STD_JOB_SIMULATION_ID");
        f.setHidden();
        f.setQueryable();

        f = itemblk2.addField("ITEM2_PM_SIMULATION_ID","Number"); 
        f.setLabel("PCMWJOBPROGTOOBJECTITEM2PMSIMULATIONID: PM Simulation Id");
        f.setDbName("PM_SIMULATION_ID");
        f.setHidden();

        itemblk2.setView("SIMULATED_STD_JOB");
        itemblk2.defineCommand("SIMULATED_STD_JOB_API","New__,Modify__,Remove__");
        itemblk2.disableDocMan();

        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.setBorderLines(false,true);
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2");
        itembar2.defineCommand(itembar2.SAVENEW,"saveNewITEM2");
        itembar2.defineCommand(itembar2.DELETE,"deleteITEM2");

        itembar2.addCustomCommand("addStdJobsByCategoryITEM2",mgr.translate("PCMWJOBPROGTOOBJECTITEMSADDJOBSBYCAT: Add Std Jobs by Category..."));
        itembar2.addCustomCommand("showStdJobITEM2",mgr.translate("PCMWJOBPROGTOOBJECTITEMSSTDJOBS: Standard Job..."));

        itembar2.enableMultirowAction();
        itembar2.removeFromMultirowAction("showStdJobITEM2");
        itembar2.forceEnableMultiActionCommand("addStdJobsByCategoryITEM2");

        itemtbl2 = mgr.newASPTable(itemblk2);

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
        itemlay2.setDialogColumns(2); 

        //********** Separate PM Actions *********************************

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk3.addField("ITEM3_SIMULATED_PM_NO");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3SIMULATEDPMNO: PM No");
        f.setDbName("SIMULATED_PM_NO");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(10);
        f.setSize(10);

        f = itemblk3.addField("ITEM3_SIMULATED_PM_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3SIMULATEDPMREVISION: Revision");
        f.setDbName("SIMULATED_PM_REVISION");
        f.setMandatory();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(6);
        f.setUpperCase();
        f.setSize(6);

        f = itemblk3.addField("ITEM3_SIMULATED_PM_STATE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3SIMULATEDPMSTATE: State");
        f.setDbName("SIMULATED_PM_STATE");
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(30);
        f.setSize(11);

        f = itemblk3.addField("ITEM3_MCH_CODE_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3MCHCODECONTRACT: Site");
        f.setDbName("MCH_CODE_CONTRACT");
        f.setDynamicLOV("MAINTENANCE_OBJECT");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(5);
        f.setSize(5);

        f = itemblk3.addField("ITEM3_MCH_CODE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3MCHCODE: Object ID");
        f.setDbName("MCH_CODE");
        f.setDynamicLOV("MAINTENANCE_OBJECT","ITEM3_MCH_CODE_CONTRACT CONTRACT");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(100);
        f.setSize(10);

        f = itemblk3.addField("ITEM3_MCH_NAME");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3MCHNAME: Object Description");
        f.setReadOnly();
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:ITEM3_MCH_CODE,:ITEM3_MCH_CODE_CONTRACT)");
        f.setQueryable();
        f.setSize(15);

        f = itemblk3.addField("ITEM3_ACTION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3ACTION: Action");
        f.setDbName("SIMULATED_ACTION_ID");
        f.setDynamicLOV("MAINTENANCE_ACTION");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setSize(10);

        f = itemblk3.addField("ITEM3_ACTION_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3ACTIONDESC: Action Description");
        f.setReadOnly();
        f.setFunction("Maintenance_Action_API.Get_Description(:ITEM3_ACTION)");
        f.setSize(15);

        f = itemblk3.addField("ITEM3_WO_SITE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3WOSITE: WO Site");
        f.setDbName("SIMULATED_WO_SITE");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);

        f = itemblk3.addField("ITEM3_MAINT_ORG");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3MAINTORG: Maint. Org.");
        f.setDbName("SIMULATED_ORG_CODE");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(10);

        f = itemblk3.addField("ITEM3_EXECUTED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EXECUTEDBY: Executed By");
        f.setDbName("SIMULATED_PM_MAINT_EMP");
        f.setDynamicLOV("MAINT_EMPLOYEE");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_EMPLOYEE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EMPLOYEEID: Executed By ID");
        f.setDbName("SIMULATED_MAINT_EMP");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(11);
        f.setHidden();
        f.setReadOnly();
        
        f = itemblk3.addField("ITEM3_PLANNED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3PLANNEDBY: Planned By");
        f.setDbName("SIMULATED_PM_PLANNED_BY");
        f.setDefaultNotVisible();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_PLANNED_BY_SIGN");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3PLANNEDBYSIGN: Planned By Sign");
        f.setDbName("SIMULATED_PLANNED_BY");
        f.setQueryable();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_OP_STATUS");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3OPSTATUS: Operational Status");
        f.setDbName("SIMULATED_OPER_STATUS");
        f.setDynamicLOV("OPERATIONAL_STATUS");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_PRIORITY_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3PRIORITYID: Priority");
        f.setDbName("SIMULATED_PRIORITY");
        f.setDynamicLOV("MAINTENANCE_PRIORITY");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_EXEC_TIME","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EXECTIME: Execution Time");
        f.setDbName("SIMULATED_EXECU_TIME");
        f.setQueryable();
        f.setSize(5);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_WORK_TYPE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3WORKTYPE: Work Type");
        f.setDbName("SIMULATED_WORK_TYPE");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(7);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_START_VALUE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3STARTVALUE: Start Value");
        f.setDbName("SIMULATED_START_VALUE");
        f.setQueryable();
        f.setMaxLength(11);
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3STARTUNIT: Start Unit");
        f.setDbName("SIMULATED_START_UNIT");
        f.enumerateValues("PM_START_UNIT_API");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(50);
        f.setSize(10);
        f.setHidden();

        f = itemblk3.addField("ITEM3_PM_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3PMSTARTUNIT: Start Unit");
        f.setDbName("SIMULATED_PM_START_UNIT");
        f.enumerateValues("PM_START_UNIT_API");
        f.setQueryable();
        f.setMaxLength(50);
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_INTERVAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3INTERVAL: Interval");
        f.setDbName("SIMULATED_INTERVAL");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3INTERVALUNIT: Interval Unit");
        f.setDbName("SIMULATED_INTER_UNIT");
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(50);
        f.setSize(10);
        f.setHidden();

        f = itemblk3.addField("ITEM3_PM_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3PMINTERVALUNIT: Interval Unit");
        f.setDbName("SIMULATED_PM_INTER_UNIT");
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setQueryable();
        f.setMaxLength(50);
        f.setUpperCase();
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_EVENT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EVENT: Event");
        f.setDbName("SIMULATED_EVENT_CODE");
        f.setDynamicLOV("MAINTENANCE_EVENT");
        f.setQueryable();
        f.setMaxLength(8);
        f.setUpperCase();
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_EVENT_START","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EVENTSTART: Start");
        f.setDbName("SIMULATED_EVENT_START");
        f.setQueryable();
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_EVENT_INTERVAL","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3EVENTINTERVAL: Interval");
        f.setDbName("SIMULATED_EVENT_INTER");
        f.setQueryable();
        f.setSize(10);
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3VALIDFROM: Valid From");
        f.setDbName("SIMULATED_VALID_FROM");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3VALIDTO: Valid To");
        f.setDbName("SIMULATED_VALID_TO");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_CAL_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3CALID: Calendar ID");
        f.setDbName("SIMULATED_CALENDAR_ID");
        f.setDynamicLOV("WORK_TIME_CALENDAR");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(10);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_CAL_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3CALDESC: Calendar Description");
        f.setReadOnly();
        f.setFunction("Work_Time_Calendar_API.Get_Description(:ITEM3_CAL_ID)");
        f.setDefaultNotVisible();
        
        //Hidden fields
        f = itemblk3.addField("ITEM3_SIMULATION_ID","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM3SIMULATIONID: Pm Simulation Id");
        f.setReadOnly();
        f.setDbName("PM_SIMULATION_ID");
        f.setHidden();

        itemblk3.setView("SIMULATED_PM_ACTION");
        itemblk3.defineCommand("SIMULATED_PM_ACTION_API","New__,Modify__,Remove__");

        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.setBorderLines(false,true);
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.DELETE,"deleteITEM3");
        itembar3.disableCommand(itembar3.NEWROW);
        itembar3.disableCommand(itembar3.DUPLICATEROW);
        itembar3.disableCommand(itembar3.EDITROW);
        itembar3.disableCommand(itembar3.CANCELEDIT);

        itembar3.addCustomCommand("showPmITEM3",mgr.translate("PCMWJOBPROGTOOBJECTITEMPMS: PM Action..."));

        itemtbl3 = mgr.newASPTable(itemblk3);

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
        itemlay3.setDialogColumns(2); 

        //********** Route PM Actions ************************************

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk4.addField("ITEM4_SIMULATED_PM_NO");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4SIMULATEDPMNO: PM No");
        f.setDbName("SIMULATED_PM_NO");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(10);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_SIMULATED_PM_REVISION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4SIMULATEDPMREVISION: Revision");
        f.setDbName("SIMULATED_PM_REVISION");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(6);
        f.setUpperCase();
        f.setSize(6);

        f = itemblk4.addField("ITEM4_SIMULATED_PM_STATE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4SIMULATEDPMSTATE: State");
        f.setDbName("SIMULATED_PM_STATE");
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(30);
        f.setSize(11);

        f = itemblk4.addField("ITEM4_MCH_CODE_CONTRACT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4MCHCODECONTRACT: Site");
        f.setDbName("MCH_CODE_CONTRACT");
        f.setDynamicLOV("MAINTENANCE_OBJECT");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setMaxLength(5);
        f.setSize(5);

        f = itemblk4.addField("ITEM4_MCH_CODE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4MCHCODE: Object ID");
        f.setDbName("MCH_CODE");
        f.setDynamicLOV("MAINTENANCE_OBJECT","ITEM4_MCH_CODE_CONTRACT CONTRACT");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(100);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_MCH_NAME");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4MCHNAME: Object Description");
        f.setReadOnly();
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:ITEM4_MCH_CODE,:ITEM4_MCH_CODE_CONTRACT)");
        f.setQueryable();
        f.setSize(15);

        f = itemblk4.addField("ITEM4_ACTION");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4ACTION: Action");
        f.setDbName("SIMULATED_ACTION_ID");
        f.setDynamicLOV("MAINTENANCE_ACTION");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setSize(10);

        f = itemblk4.addField("ITEM4_ACTION_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4ACTIONDESC: Action Description");
        f.setReadOnly();
        f.setFunction("Maintenance_Action_API.Get_Description(:ITEM4_ACTION)");
        f.setSize(15);

        f = itemblk4.addField("ITEM4_WO_SITE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4WOSITE: WO Site");
        f.setDbName("SIMULATED_WO_SITE");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);

        f = itemblk4.addField("ITEM4_MAINT_ORG");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4MAINTORG: Maint. Org.");
        f.setDbName("SIMULATED_ORG_CODE");
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_EXECUTED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EXECUTEDBY: Executed By");
        f.setDbName("SIMULATED_PM_MAINT_EMP");
        f.setDynamicLOV("MAINT_EMPLOYEE");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_EMPLOYEE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EMPLOYEEID: Executed By ID");
        f.setDbName("SIMULATED_MAINT_EMP");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(11);
        f.setHidden();
        
        f = itemblk4.addField("ITEM4_PLANNED_BY");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4PLANNEDBY: Planned By");
        f.setDbName("SIMULATED_PM_PLANNED_BY");
        f.setDefaultNotVisible();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_PLANNED_BY_SIGN");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4PLANNEDBYSIGN: Planned By Sign");
        f.setDbName("SIMULATED_PLANNED_BY");
        f.setQueryable();
        f.setHidden();
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_OP_STATUS");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4OPSTATUS: Operational Status");
        f.setDbName("SIMULATED_OPER_STATUS");
        f.setDynamicLOV("OPERATIONAL_STATUS");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_PRIORITY_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4PRIORITYID: Priority");
        f.setDbName("SIMULATED_PRIORITY");
        f.setDynamicLOV("MAINTENANCE_PRIORITY");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(5);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_EXEC_TIME","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EXECTIME: Execution Time");
        f.setDbName("SIMULATED_EXECU_TIME");
        f.setQueryable();
        f.setSize(5);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_WORK_TYPE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4WORKTYPE: Work Type");
        f.setDbName("SIMULATED_WORK_TYPE");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(20);
        f.setSize(7);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_ROUTE_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4ROUTEID: Route ID");
        f.setDbName("SIMULATED_ROUTE_ID");
        f.setDynamicLOV("PM_ROUND_DEFINITION");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(8);

        f = itemblk4.addField("ITEM4_START_VALUE");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4STARTVALUE: Start Value");
        f.setDbName("SIMULATED_START_VALUE");
        f.setQueryable();
        f.setMaxLength(11);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4STARTUNIT: Start Unit");
        f.setDbName("SIMULATED_START_UNIT");
        f.enumerateValues("PM_START_UNIT_API");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(50);
        f.setSize(10);
        f.setHidden();

        f = itemblk4.addField("ITEM4_PM_START_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4PMSTARTUNIT: Start Unit");
        f.setDbName("SIMULATED_PM_START_UNIT");
        f.enumerateValues("PM_START_UNIT_API");
        f.setQueryable();
        f.setMaxLength(50);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_INTERVAL");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4INTERVAL: Interval");
        f.setDbName("SIMULATED_INTERVAL");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(10);

        f = itemblk4.addField("ITEM4_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4INTERVALUNIT: Interval Unit");
        f.setDbName("SIMULATED_INTER_UNIT");
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(50);
        f.setSize(10);
        f.setHidden();

        f = itemblk4.addField("ITEM4_PM_INTERVAL_UNIT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4PMINTERVALUNIT: Interval Unit");
        f.setDbName("SIMULATED_PM_INTER_UNIT");
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setQueryable();
        f.setMaxLength(50);
        f.setUpperCase();
        f.setSize(10);

        f = itemblk4.addField("ITEM4_EVENT");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EVENT: Event");
        f.setDbName("SIMULATED_EVENT_CODE");
        f.setDynamicLOV("MAINTENANCE_EVENT");
        f.setQueryable();
        f.setMaxLength(8);
        f.setUpperCase();
        f.setSize(10);

        f = itemblk4.addField("ITEM4_EVENT_START","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EVENTSTART: Start");
        f.setDbName("SIMULATED_EVENT_START");
        f.setQueryable();
        f.setSize(10);

        f = itemblk4.addField("ITEM4_EVENT_INTERVAL","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4EVENTINTERVAL: Interval");
        f.setDbName("SIMULATED_EVENT_INTER");
        f.setQueryable();
        f.setSize(10);

        f = itemblk4.addField("ITEM4_VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4VALIDFROM: Valid From");
        f.setDbName("SIMULATED_VALID_FROM");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4VALIDTO: Valid To");
        f.setDbName("SIMULATED_VALID_TO");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_CAL_ID");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4CALID: Calendar ID");
        f.setDbName("SIMULATED_CALENDAR_ID");
        f.setDynamicLOV("WORK_TIME_CALENDAR");
        f.setQueryable();
        f.setUpperCase();
        f.setSize(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_CAL_DESC");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4CALDESC: Calendar Description");
        f.setReadOnly();
        f.setFunction("Work_Time_Calendar_API.Get_Description(:ITEM4_CAL_ID)");
        f.setDefaultNotVisible();

        //Hidden fields
        f = itemblk4.addField("ITEM4_SIMULATION_ID","Number");
        f.setLabel("PCMWJOBPROGTOOBJECTITEM4SIMULATIONID: Pm Simulation Id");
        f.setReadOnly();
        f.setDbName("PM_SIMULATION_ID");
        f.setHidden();

        f = itemblk4.addField("STD_JOB_SIM_ID");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("PM_SIM_ID");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("JOB_PROGRAM_ID");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("JOB_PROGRAM_REVISION");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("JOB_PROGRAM_CONTRACT");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("STATE");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("MCH_CODE");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("MCH_CODE_CONTRACT");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("ROUTE_ID");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("SJOBTYPE");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("SSIMULATEDPMNO");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("PM_EXIST");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("WO_EXIST");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("IS_NEW_ROW");     
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("IS_VALID");     
        f.setHidden();
        f.setFunction("''");

        itemblk4.setView("SIMULATED_PM_ACTION");
        itemblk4.defineCommand("SIMULATED_PM_ACTION_API","New__,Modify__,Remove__");

        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.setBorderLines(false,true);
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.defineCommand(itembar4.DELETE,"deleteITEM4");
        itembar4.disableCommand(itembar4.NEWROW);
        itembar4.disableCommand(itembar4.DUPLICATEROW);
        itembar4.disableCommand(itembar4.EDITROW);
        itembar4.disableCommand(itembar4.CANCELEDIT);

        itembar4.addCustomCommand("showPmITEM4",mgr.translate("PCMWJOBPROGTOOBJECTITEMPMS: PM Action..."));

        itemtbl4 = mgr.newASPTable(itemblk4);

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setDialogColumns(2);     
    }

    public void activateSeparate()
    {
        ASPManager mgr = getASPManager();

        tabs.setActiveTab(1);
        qryString = mgr.createSearchURL(itemblk1);
        ctx.removeGlobal("ACTTAB");
        ctx.setGlobal("ACTTAB","1");
    }

    public void activateRoute()
    {
        ASPManager mgr = getASPManager();
        tabs.setActiveTab(2);
        qryString = mgr.createSearchURL(itemblk2);
        ctx.removeGlobal("ACTTAB");
        ctx.setGlobal("ACTTAB","2");
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        itembar1.removeCustomCommand("activateSeparate");
        itembar1.removeCustomCommand("activateRoute");

        if (!mgr.isEmpty(jobProgId) && !mgr.isEmpty(jobProgRev) && !mgr.isEmpty(jobProgContract))
        {
            trans.clear();
            cmd = trans.addCustomFunction("GETSTATE","Job_Program_API.Get_Rowstate","STATE");
            cmd.addParameter("JOB_PROGRAM_ID",jobProgId);
            cmd.addParameter("JOB_PROGRAM_REVISION",jobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",jobProgContract);
            trans = mgr.validate(trans);

            if (!"Active".equals(trans.getValue("GETSTATE/DATA/STATE")))
            {
                if (tabs.getActiveTab() == 1)
                    itembar1.disableCustomCommand("addStdJobsByCategoryITEM1");
                else if (tabs.getActiveTab() == 2)
                    itembar2.disableCustomCommand("addStdJobsByCategoryITEM2");
            }
        }

        if (tabs.getActiveTab() == 1 && (itemlay1.isNewLayout() || itemlay1.isEditLayout()))
        {
            String stmt = "PM_SIMULATION_ID = "+ctx.findGlobal("PMSIMULATIONID")+" AND SIMULATED_PM_TYPE = 'ActiveSeparate'";
            mgr.getASPField("ITEM1_CONNECTED_PM_NO").setLOVProperty("WHERE",stmt);
            mgr.getASPField("ITEM1_CONNECTED_PM_REVISION").setLOVProperty("WHERE",stmt);
        }
        else if (tabs.getActiveTab() == 2 && (itemlay2.isNewLayout() || itemlay2.isEditLayout()))
        {
            String stmt = "PM_SIMULATION_ID = "+ctx.findGlobal("PMSIMULATIONID")+" AND SIMULATED_PM_TYPE = 'ActiveRound'";
            mgr.getASPField("ITEM2_CONNECTED_PM_NO").setLOVProperty("WHERE",stmt);
            mgr.getASPField("ITEM2_CONNECTED_PM_REVISION").setLOVProperty("WHERE",stmt);
        }
    } 

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return null;
    }

    protected String getTitle()
    {
        return null;
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        
        appendToHTML(tabs.showTabsInit());

        if (tabs.getActiveTab() == 1)
        {
            appendToHTML(itemlay1.show());
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("SEPSIMULATE",mgr.translate("PCMWSEPSIM:  Simulate "),"SEPSIMULATE"));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML("</td>\n</tr>\n</table>\n");
            appendToHTML(itemlay3.show());
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("SEPGENERATE",mgr.translate("PCMWSEPGEN:  Generate "),"SEPGENERATE"));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML("</td>\n</tr>\n</table>\n");
        }
        else if (tabs.getActiveTab() == 2)
        {
            appendToHTML(itemlay2.show());
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("RTSIMULATE",mgr.translate("PCMWRTSIM:  Simulate "),"RTSIMULATE"));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML("</td>\n</tr>\n</table>\n");
            appendToHTML(itemlay4.show());
            appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
            appendToHTML("<tr align=\"right\">\n");
            appendToHTML("<td width=\"100%\" align=\"right\">\n");
            appendToHTML(fmt.drawSubmit("RTGENERATE",mgr.translate("PCMWRTGEN:  Generate "),"RTGENERATE"));
            appendToHTML("&nbsp;&nbsp;"); 
            appendToHTML("</td>\n</tr>\n</table>\n");
        }

        appendDirtyJavaScript("window.name = \"jobProgToObjectItems\"\n");
        appendDirtyJavaScript("SEARCHBASE = SEARCHBASE.replace(\"JobProgToObjectItems.page\",\"JobProgToObjMain.page\");\n");

        if (bWarning)
        {
            appendDirtyJavaScript("window.alert('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(sWarningMsg));   //XSS_Safe AMNILK 20070717
            appendDirtyJavaScript("');");
        }

        if (tabs.getActiveTab() == 1)
        {
            // Enable/disable the Separate Simulate button
            appendDirtyJavaScript("f.SEPSIMULATE.disabled = true;\n");
            if (itemlay1.isNewLayout() || itemlay1.isEditLayout() || itemlay1.isFindLayout() || itemlay3.isFindLayout())
                appendDirtyJavaScript("f.SEPSIMULATE.disabled = true;\n");
            else if ("TRUE".equals(ctx.findGlobal("SEPPMSIMULATED")))
                appendDirtyJavaScript("f.SEPSIMULATE.disabled = true;\n");
            else
            {
                if (bJobs)
                {
                    if (bOpt || enSepSimulate)
                        appendDirtyJavaScript("f.SEPSIMULATE.disabled = false;\n"); 
                    else
                        appendDirtyJavaScript("f.SEPSIMULATE.disabled = true;\n"); 
                }
                else
                    appendDirtyJavaScript("f.SEPSIMULATE.disabled = true;\n");
            }

            // Enable/disable the Separate Genarate button
            appendDirtyJavaScript("f.SEPGENERATE.disabled = true;\n");

            if (itemlay1.isNewLayout() || itemlay1.isEditLayout() || itemlay1.isFindLayout() || itemlay3.isFindLayout() || itemlay3.isEditLayout())
                appendDirtyJavaScript("f.SEPGENERATE.disabled = true;\n");
            else if (enSepGenarate && "TRUE".equals(ctx.findGlobal("SEPPMSIMULATED")))
                appendDirtyJavaScript("f.SEPGENERATE.disabled = false;\n");
        }
        else if (tabs.getActiveTab() == 2)
        {
            // Enable/disable the Round Simulate button
            appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n");
            if (itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay2.isFindLayout() || itemlay4.isFindLayout())
                appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n");
            else if ("TRUE".equals(ctx.findGlobal("RTPMSIMULATED")))
                appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n");
            else if (mgr.isEmpty(routeId))
                appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n");
            else
            {
                if (bJobs)
                {
                    if (bOpt || enRndSimulate)
                        appendDirtyJavaScript("f.RTSIMULATE.disabled = false;\n");
                    else
                        appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n"); 
                }
                else
                    appendDirtyJavaScript("f.RTSIMULATE.disabled = true;\n");
            }

            // Enable/disable the Round Genarate button
            appendDirtyJavaScript("f.RTGENERATE.disabled = true;\n");
            if (itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay2.isFindLayout() || itemlay4.isFindLayout() ||itemlay4.isEditLayout())
                appendDirtyJavaScript("f.RTGENERATE.disabled = true;\n");
            else if (enRndGenarate && "TRUE".equals(ctx.findGlobal("RTPMSIMULATED")))
                appendDirtyJavaScript("f.RTGENERATE.disabled = false;\n");
        }

        appendDirtyJavaScript("function validateItem1StdJobId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem1StdJobId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM1_STD_JOB_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM1_STD_JOB_REVISION',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_STD_JOB_ID'\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM1_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_ID=' + URLClientEncode(getValue_('ITEM1_STD_JOB_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_REVISION=' + URLClientEncode(getValue_('ITEM1_STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_STD_JOB_ID',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJITEM1STDJOBID: Standard Job ID"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_REVISION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_TYPE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_WORK_DESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_DEFINITION',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STATUS',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_TYPE_ID',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_TYPE_DESC',i,7);\n");
        appendDirtyJavaScript("	}\n"); 
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateItem2StdJobId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem2StdJobId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM2_STD_JOB_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM2_STD_JOB_REVISION',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM2_STD_JOB_ID'\n");
        appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM2_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_ID=' + URLClientEncode(getValue_('ITEM2_STD_JOB_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_REVISION=' + URLClientEncode(getValue_('ITEM2_STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM2_STD_JOB_ID',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJITEM1STDJOBID: Standard Job ID"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_REVISION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_TYPE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_WORK_DESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_DEFINITION',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STATUS',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_TYPE_ID',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_TYPE_DESC',i,7);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function validateItem1StdJobRevision(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem1StdJobRevision(i) ) return;\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_STD_JOB_REVISION'\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM1_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_ID=' + URLClientEncode(getValue_('ITEM1_STD_JOB_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_REVISION=' + URLClientEncode(getValue_('ITEM1_STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_STD_JOB_REVISION',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJITEM1STDJOBREVISION: Standard Job Revision"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_REVISION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STD_JOB_TYPE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_WORK_DESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_DEFINITION',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_STATUS',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_TYPE_ID',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_TYPE_DESC',i,7);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function validateItem2StdJobRevision(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem2StdJobRevision(i) ) return;\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM2_STD_JOB_REVISION'\n");
	appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM2_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_ID=' + URLClientEncode(getValue_('ITEM2_STD_JOB_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_STD_JOB_REVISION=' + URLClientEncode(getValue_('ITEM2_STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM2_STD_JOB_REVISION',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJITEM1STDJOBREVISION: Standard Job Revision"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_REVISION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STD_JOB_TYPE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_WORK_DESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_DEFINITION',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_STATUS',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_TYPE_ID',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_TYPE_DESC',i,7);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n"); 

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

