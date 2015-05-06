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
*  File        : JobProgToObject.java 
*  Created     : 050309 NIJALK  (AMEC112 - Job program)
*  Modified    :
*  050407   NIJALK   Field "Job Program Revision" made Read Only.
*  050412   NIJALK   Call 123328: Replaced frame name "__pmPart" with frame index frames[1].
*                    Added methods modifiedStartPres(),modifiedEndPres().
*  060738   AMDILK   Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  060807   AMNILK   Merged Bug Id: 58214.
*  DIAMLK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216. Recorrected the script error of merging of Bug 58214.
*  SHAFLK  080304  Bug 71775, Modified preDefine().
*  SHAFLK  080313  Bug 71775, Modified validation().
* ---------------------------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class JobProgToObject extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.JobProgToObject");

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
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPField f;
    
    private String mchCode;
    private String mchContract;
    private String jobProgId;
    private String jobProgRev;
    private String jobProgContract;

    private boolean sTransfer;
    
    private String qrystr;
    private String val = "";
    private String txt = "";

    //===============================================================
    // Construction 
    //===============================================================
    public JobProgToObject(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        mchCode = "";
        mchContract = "";
        jobProgId = "";
        jobProgRev = "";
        jobProgContract = "";

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        mchCode = ctx.readValue("MCH_CODE",mchCode);
        mchContract = ctx.readValue("MCH_CONTRACT",mchContract);
        jobProgId = ctx.readValue("JOB_PROGRAM_ID",jobProgId);
        jobProgRev = ctx.readValue("JOB_PROGRAM_REVISION",jobProgRev);
        jobProgContract = ctx.readValue("JOB_PROGRAM_CONTRACT",jobProgContract);
        sTransfer = ctx.readFlag("STRANSFER",false);
        qrystr = ctx.readValue("QRYSTR", "");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        if ((!mgr.isEmpty(mgr.readValue("MCH_CODE")) && !mgr.isEmpty(mgr.readValue("MCH_CONTRACT"))) || (!mgr.isEmpty(mgr.readValue("JOB_PROGRAM_ID")) && !mgr.isEmpty(mgr.readValue("JOB_PROGRAM_REVISION")) && !mgr.isEmpty(mgr.readValue("JOB_PROGRAM_CONTRACT"))))
        {
            mchCode = mgr.readValue("MCH_CODE");        
            mchContract =mgr.readValue("MCH_CONTRACT");
            jobProgId = mgr.readValue("JOB_PROGRAM_ID");
            jobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
            jobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
            
            sTransfer = true;
            newRow();
        }
        else
            newRow();

        ctx.writeValue("QRYSTR", qrystr);
        ctx.writeValue("MCH_CODE",mchCode);
        ctx.writeValue("MCH_CONTRACT",mchContract);
        ctx.writeValue("JOB_PROGRAM_ID",jobProgId);
        ctx.writeValue("JOB_PROGRAM_REVISION",jobProgRev);
        ctx.writeValue("JOB_PROGRAM_CONTRACT",jobProgContract);
        ctx.writeFlag("STRANSFER",sTransfer);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        val = mgr.readValue("VALIDATE");

        if ("JOB_PROGRAM_ID".equals(val))
        {
            String sProgExist = "";
            String sJobProdId = "";
            String sJobProgRev = "";
            String sJobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
            String mchCode = mgr.readValue("MCH_CODE");
            String mchContract = mgr.readValue("MCH_CODE_CONTRACT");
            String new_job = mgr.readValue("JOB_PROGRAM_ID","");
            String isLast = "FALSE";

            if (new_job.indexOf("~",0)>0)
            {
                String [] attrarr =  new_job.split("~");

                sJobProdId = attrarr[0];
                sJobProgRev = attrarr[1];
            }
            else
            {
                sJobProdId = mgr.readValue("JOB_PROGRAM_ID");

                cmd = trans.addCustomFunction("GETREV","Job_Program_API.Get_Active_Revision","JOB_PROGRAM_REVISION");
                cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
                cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

                trans = mgr.validate(trans);
                sJobProgRev = trans.getValue("GETREV/DATA/JOB_PROGRAM_REVISION");
            }

            trans.clear();

            cmd = trans.addCustomFunction("GETDESC","Job_Program_API.Get_Description","DESCRIPTION");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETSTATE","Job_Program_API.Get_Status","STATE");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETSITEDESCR","Site_API.Get_Description","WO_CONT_DESC");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("JOBEXISTS","Job_Program_API.Check_Exist","CHECK_EXISTS");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("PROGEXITS","Job_Program_Utility_API.Program_Exist_On_Object","PROG_USED");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);
            cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

            trans = mgr.validate(trans);

            String sDescr = trans.getValue("GETDESC/DATA/DESCRIPTION");
            String sState = trans.getValue("GETSTATE/DATA/STATE");
            String sWoSiteDescr = trans.getValue("GETSITEDESCR/DATA/WO_CONT_DESC");
            String sJobExsist = trans.getValue("JOBEXISTS/DATA/CHECK_EXISTS");
            sProgExist = trans.getValue("PROGEXITS/DATA/PROG_USED"); 

            if (!mgr.isEmpty(sJobProdId) && !mgr.isEmpty(sJobProgRev) && !mgr.isEmpty(sJobProgContract) && !mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract) && "TRUE".equals(sProgExist))
                sProgExist = "TRUE";
            else
                sProgExist = "FALSE";

            txt = (mgr.isEmpty(sJobProdId)? "" : sJobProdId) + "^" +
                  (mgr.isEmpty(sJobProgRev)? "" : sJobProgRev) + "^" +
                  (mgr.isEmpty(sJobProgContract)? "" : sJobProgContract) + "^" +
                  (mgr.isEmpty(sDescr)? "" : sDescr) + "^" +
                  (mgr.isEmpty(sState)? "" : sState) + "^" +
                  (mgr.isEmpty(sJobProgContract)? "" : sJobProgContract) + "^" +
                  (mgr.isEmpty(sWoSiteDescr)? "" : sWoSiteDescr) + "^" +
                  (mgr.isEmpty(sJobExsist)? "" : sJobExsist) + "^" +
                  (mgr.isEmpty(sProgExist)? "" : sProgExist) + "^";

            mgr.responseWrite(txt);
        }
        else if ("JOB_PROGRAM_CONTRACT".equals(val))
        {
            String sJobProdId = mgr.readValue("JOB_PROGRAM_ID");
            String sJobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
            String mchCode = mgr.readValue("MCH_CODE");
            String mchContract = mgr.readValue("MCH_CODE_CONTRACT");
            String sProgExist = "";

            cmd = trans.addCustomFunction("GETREV","Job_Program_API.Get_Active_Revision","JOB_PROGRAM_REVISION");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","USER1");

            cmd = trans.addCustomCommand("USERALLOWED","User_Allowed_Site_API.Exist");
            cmd.addReference("USER1","GETUSER/DATA");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETSITEDESCR","Site_API.Get_Description","WO_CONT_DESC");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETDESC","Job_Program_API.Get_Description","DESCRIPTION");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addReference("JOB_PROGRAM_REVISION","GETREV/DATA");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("GETSTATE","Job_Program_API.Get_Status","STATE");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addReference("JOB_PROGRAM_REVISION","GETREV/DATA");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("JOBEXISTS","Job_Program_API.Check_Exist","CHECK_EXISTS");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addReference("JOB_PROGRAM_REVISION","GETREV/DATA");
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);

            cmd = trans.addCustomFunction("PROGEXITS","Job_Program_Utility_API.Program_Exist_On_Object","PROG_USED");
            cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
            cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);
            cmd.addReference("JOB_PROGRAM_REVISION","GETREV/DATA");
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

            trans = mgr.validate(trans);

            String sWoSite = mgr.readValue("JOB_PROGRAM_CONTRACT");
            String sWoSiteDescr = trans.getValue("GETSITEDESCR/DATA/WO_CONT_DESC");
            String sJobDescr = trans.getValue("GETDESC/DATA/DESCRIPTION");
            String sJobState = trans.getValue("GETSTATE/DATA/STATE");
            String sJobExtsts = trans.getValue("JOBEXISTS/DATA/CHECK_EXISTS");
            sProgExist = trans.getValue("PROGEXITS/DATA/PROG_USED"); 
            String sNull = "";
            String sJobProgRev = trans.getValue("GETREV/DATA/JOB_PROGRAM_REVISION");

            if (!mgr.isEmpty(sJobProdId) && !mgr.isEmpty(sJobProgRev) && !mgr.isEmpty(sJobProgContract) && !mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract) && "TRUE".equals(sProgExist))
                sProgExist = "TRUE";
            else
                sProgExist = "FALSE";

            txt = (mgr.isEmpty(sWoSite)? "" : sWoSite) + "^" +
                  (mgr.isEmpty(sWoSiteDescr)? "" : sWoSiteDescr) + "^" +
                  (mgr.isEmpty(sJobDescr)? "" : sJobDescr) + "^" +
                  (mgr.isEmpty(sJobState)? "" : sJobState) + "^" +
                  (mgr.isEmpty(sJobExtsts)? "" : sJobExtsts) + "^" +
                  sNull + "^" + sNull + "^" + sNull + "^" + sNull + "^" +
                  (mgr.isEmpty(sProgExist)? "FALSE" : sProgExist) + "^"+
                  (mgr.isEmpty(sJobProgRev)? "FALSE" : sJobProgRev) + "^";

            mgr.responseWrite(txt);
        }
        else if ("MAINT_ORG".equals(val))
        {
            String sOrg = mgr.readValue("MAINT_ORG");

            cmd = trans.addCustomFunction("ORGEXIST","Organization_API.Check_Exist","CHECK_EXISTS");
            cmd.addParameter("WO_CONTRACT",mgr.readValue("WO_CONTRACT"));
            cmd.addParameter("MAINT_ORG",sOrg);

            cmd = trans.addCustomFunction("ORGDESCR","Organization_API.Get_Descr","MAINT_ORG_DESC");
            cmd.addParameter("WO_CONTRACT",mgr.readValue("WO_CONTRACT"));
            cmd.addParameter("MAINT_ORG",sOrg);

            trans = mgr.validate(trans);

            String sExist = trans.getValue("ORGEXIST/DATA/CHECK_EXISTS");
            String sDescr = trans.getValue("ORGDESCR/DATA/MAINT_ORG_DESC");

            txt = (mgr.isEmpty(sOrg)? "" : sOrg) + "^" +
                  (mgr.isEmpty(sDescr)? "" : sDescr) + "^" +
                  (mgr.isEmpty(sExist)? "" : sExist) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ROUTE_ID".equals(val))
        {
            String sRoute = mgr.readValue("ROUTE_ID");
            String sWoContract = mgr.readValue("WO_CONTRACT");
            String sExist = "TRUE";

            cmd = trans.addCustomFunction("ROUTEDESC","Pm_Round_Definition_API.Get_Description","ROUTE_ID_DESC");
            cmd.addParameter("ROUTE_ID",sRoute);

            cmd = trans.addCustomFunction("ROUTESITE","Pm_Round_Definition_API.Get_Contract","WO_CONTRACT");
            cmd.addParameter("ROUTE_ID",sRoute);

            trans = mgr.validate(trans);

            String sDescr = trans.getValue("ROUTEDESC/DATA/ROUTE_ID_DESC");
            String sRouteContract = trans.getValue("ROUTESITE/DATA/WO_CONTRACT");

            if ((!mgr.isEmpty(sWoContract) && !mgr.isEmpty(sRouteContract) && (!sWoContract.equals(sRouteContract))) || (mgr.isEmpty(sRouteContract)))
                sExist = "FALSE";

            txt = (mgr.isEmpty(sRoute)? "" : sRoute) + "^" +
                  (mgr.isEmpty(sDescr)? "" : sDescr) + "^" +
                  (mgr.isEmpty(sExist)? "" : sExist) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ACTION".equals(val))
        {
            cmd = trans.addCustomFunction("ACTDESC","Maintenance_Action_API.Get_Description","ACTION_DESC");
            cmd.addParameter("ACTION",mgr.readValue("ACTION"));

            trans = mgr.validate(trans);

            mgr.responseWrite(trans.getValue("ACTDESC/DATA/ACTION_DESC"));
       }
       else if ("MCH_CODE_CONTRACT".equals(val))
       {
           String sJobProdId = mgr.readValue("JOB_PROGRAM_ID");
           String sJobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
           String sJobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
           String mchCode = mgr.readValue("MCH_CODE");
           String mchContract = mgr.readValue("MCH_CODE_CONTRACT");
           String sProgExist = "";

           cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","USER1");

           cmd = trans.addCustomCommand("USERALLOWED","User_Allowed_Site_API.Exist");
           cmd.addReference("USER1","GETUSER/DATA");
           cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

           cmd = trans.addCustomFunction("CHECKEXIST","Maintenance_Object_API.Check_Exist","CHECK_EXISTS");
           cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
           cmd.addParameter("MCH_CODE",mchCode);

           cmd = trans.addCustomFunction("PROGEXITS","Job_Program_Utility_API.Program_Exist_On_Object","PROG_USED");
           cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
           cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);
           cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
           cmd.addParameter("MCH_CODE",mchCode);
           cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

           trans = mgr.validate(trans);

           String sExists = trans.getValue("CHECKEXIST/DATA/CHECK_EXISTS");
           sProgExist = trans.getValue("PROGEXITS/DATA/PROG_USED"); 

           if (!mgr.isEmpty(sJobProdId) && !mgr.isEmpty(sJobProgRev) && !mgr.isEmpty(sJobProgContract) && !mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract) && "TRUE".equals(sProgExist))
               sProgExist = "TRUE";
           else
               sProgExist = "FALSE";

           txt = mgr.readValue("MCH_CODE_CONTRACT") + "^" +
                 (mgr.isEmpty(sExists)? "" : sExists) + "^" +
                 (mgr.isEmpty(sProgExist)? "FALSE" : sProgExist) + "^" ;
           mgr.responseWrite(txt);
       }
       else if ("MCH_CODE".equals(val))
       {
           String sJobProdId = mgr.readValue("JOB_PROGRAM_ID");
           String sJobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
           String sJobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");
           String mchCode = mgr.readValue("MCH_CODE");
           String mchContract = mgr.readValue("MCH_CODE_CONTRACT");
           String sProgExist = "";

           if (mgr.isEmpty(mchContract))
           {
               cmd = trans.addCustomFunction("GETSITE","Equipment_Object_API.Get_Contract","MCH_CODE_CONTRACT");
               cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));
               trans = mgr.validate(trans);
               mchContract = trans.getValue("GETSITE/DATA/MCH_CODE_CONTRACT");
               trans.clear();
           }

           cmd = trans.addCustomFunction("CHECKEXIST","Maintenance_Object_API.Check_Exist","CHECK_EXISTS");
           cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
           cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));

           cmd = trans.addCustomFunction("PROGEXITS","Job_Program_Utility_API.Program_Exist_On_Object","PROG_USED");
           cmd.addParameter("JOB_PROGRAM_ID",sJobProdId);
           cmd.addParameter("JOB_PROGRAM_CONTRACT",sJobProgContract);
           cmd.addParameter("JOB_PROGRAM_REVISION",sJobProgRev);
           cmd.addParameter("MCH_CODE",mchCode);
           cmd.addParameter("MCH_CODE_CONTRACT",mchContract);

           trans = mgr.validate(trans);

           String sExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXISTS");
           sProgExist = trans.getValue("PROGEXITS/DATA/PROG_USED"); 

           if (!mgr.isEmpty(sJobProdId) && !mgr.isEmpty(sJobProgRev) && !mgr.isEmpty(sJobProgContract) && !mgr.isEmpty(mchCode) && !mgr.isEmpty(mchContract) && "TRUE".equals(sProgExist))
               sProgExist = "TRUE";
           else
               sProgExist = "FALSE";

           txt = (mgr.isEmpty(mchContract)? "" : mchContract) + "^" +
                  mgr.readValue("MCH_CODE") + "^" +
                  (mgr.isEmpty(sExist)? "" : sExist) + "^"+
                  (mgr.isEmpty(sProgExist)? "FALSE" : sProgExist) + "^" ;
           mgr.responseWrite(txt);
       }
       else if ("VALID_FROM".equals(val))
       {
           String sExist = "TRUE";
           String sValidFrom = mgr.readValue("VALID_FROM");
           String sValidTo = mgr.readValue("VALID_TO");

           ASPBuffer buf = mgr.newASPBuffer();
           buf.addFieldItem("VALID_FROM",mgr.readValue("VALID_FROM",""));
           buf.addFieldItem("VALID_TO",mgr.readValue("VALID_TO","")); 

           if (!mgr.isEmpty(sValidFrom) && !mgr.isEmpty(sValidTo))
           {   
               //Bug 58216 start
               cmd = trans.addQuery("GETVALID","SELECT (? - ?) NUM FROM DUAL");
               cmd.addParameter("VALID_TO",buf.getFieldValue("VALID_TO"));
               cmd.addParameter("VALID_FROM",buf.getFieldValue("VALID_FROM"));
               //Bug 58216 end
               trans = mgr.validate(trans);

               if (trans.getNumberValue("GETVALID/DATA/NUM")<0)
                   sExist = "FALSE";
           }

           mgr.responseWrite(mgr.readValue("VALID_FROM","") + "^" + sExist + "^");
       }
       else if ("VALID_TO".equals(val))
       {
           String sExist = "TRUE";
           String sValidFrom = mgr.readValue("VALID_FROM");
           String sValidTo = mgr.readValue("VALID_TO");

           ASPBuffer buf = mgr.newASPBuffer();
           buf.addFieldItem("VALID_FROM",mgr.readValue("VALID_FROM",""));
           buf.addFieldItem("VALID_TO",mgr.readValue("VALID_TO","")); 

           if (!mgr.isEmpty(sValidFrom) && !mgr.isEmpty(sValidTo))
           {
               //Bug 58216 start
               cmd = trans.addQuery("GETVALID","SELECT (? - ?) NUM FROM DUAL");
               cmd.addParameter("VALID_TO",buf.getFieldValue("VALID_TO"));
               cmd.addParameter("VALID_FROM",buf.getFieldValue("VALID_FROM"));
               //Bug 58216 end
               trans = mgr.validate(trans);

               if (trans.getNumberValue("GETVALID/DATA/NUM")<0)
                   sExist = "FALSE";
           }

           mgr.responseWrite(mgr.readValue("VALID_TO","") + "^" + sExist + "^");
       }
       else if ("MERGE".equals(val))
       {
           mgr.responseWrite(mgr.readValue("MERGE","") + "^");
       }

        mgr.endResponse();
    }  

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();
        String sDescr = "";
        ctx.setGlobal("ACTTAB","SEPARATE");
        trans.clear();

        mchCode = mgr.readValue("MCH_CODE");        
        mchContract =mgr.readValue("MCH_CONTRACT");
        jobProgId = mgr.readValue("JOB_PROGRAM_ID");
        jobProgRev = mgr.readValue("JOB_PROGRAM_REVISION");
        jobProgContract = mgr.readValue("JOB_PROGRAM_CONTRACT");

        if (sTransfer)
        {
            cmd = trans.addCustomFunction("GETSITEDESCR","Site_API.Get_Description","WO_CONT_DESC");
            cmd.addReference("JOB_PROGRAM_CONTRACT",jobProgContract);

            trans = mgr.validate(trans);

            sDescr = trans.getValue("GETSITEDESCR/DATA/WO_CONT_DESC");

            ASPBuffer data = mgr.newASPBuffer();
            data.setFieldItem("JOB_PROGRAM_CONTRACT",jobProgContract);
            data.setFieldItem("JOB_PROGRAM_ID",jobProgId);
            data.setFieldItem("JOB_PROGRAM_REVISION",jobProgRev);
            data.setFieldItem("MCH_CODE",mchCode);
            data.setFieldItem("MCH_CODE_CONTRACT",mchContract);
            data.setFieldItem("WO_CONTRACT",jobProgContract);
            data.setFieldItem("WO_CONT_DESC",sDescr);
            headset.addRow(data);
        }
        else
        {
            cmd = trans.addCustomFunction("DEFSITE","User_Default_API.Get_Contract","JOB_PROGRAM_CONTRACT");

            cmd = trans.addCustomFunction("GETSITEDESCR","Site_API.Get_Description","WO_CONT_DESC");
            cmd.addReference("JOB_PROGRAM_CONTRACT","DEFSITE/DATA");

            trans = mgr.validate(trans);

            String sDefaultSite = trans.getValue("DEFSITE/DATA/JOB_PROGRAM_CONTRACT");
            sDescr = trans.getValue("GETSITEDESCR/DATA/WO_CONT_DESC");

            ASPBuffer data = mgr.newASPBuffer();
            data.setFieldItem("JOB_PROGRAM_CONTRACT",sDefaultSite);
            data.setFieldItem("MCH_CODE_CONTRACT",sDefaultSite);
            data.setFieldItem("WO_CONTRACT",sDefaultSite);
            data.setFieldItem("WO_CONT_DESC",sDescr);
            headset.addRow(data);
        }
    }    

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWJOBPROGRAMNODATA: No data found."));
        }

        qrystr = mgr.createSearchURL(headblk);
    } 

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();
        this.disableFooter();

        //============== Header ==================================

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();   

        f = headblk.addField("JOB_PROGRAM_CONTRACT");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTJOBPROGRAMCONTRACT: Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setUpperCase();
        f.setQueryable();
        f.setMaxLength(5);
        f.setSize(5);
        f.setCustomValidation("JOB_PROGRAM_CONTRACT,JOB_PROGRAM_ID,JOB_PROGRAM_REVISION,MCH_CODE_CONTRACT,MCH_CODE","WO_CONTRACT,WO_CONT_DESC,DESCRIPTION,STATE,CHECK_EXISTS,MAINT_ORG,MAINT_ORG_DESC,ROUTE_ID,ROUTE_ID_DESC,PROG_USED,JOB_PROGRAM_REVISION");

        f = headblk.addField("JOB_PROGRAM_ID");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTJOBPROGRAMID: Job Program ID");
        f.setLOV("JobProgramLov.page","JOB_PROGRAM_CONTRACT");
        f.setLOVProperty("WHERE","OBJSTATE = 'Active'");
        f.setUpperCase();
        f.setQueryable();
        f.setMaxLength(100);
        f.setSize(10);
        f.setCustomValidation("JOB_PROGRAM_ID,JOB_PROGRAM_REVISION,JOB_PROGRAM_CONTRACT,MCH_CODE_CONTRACT,MCH_CODE","JOB_PROGRAM_ID,JOB_PROGRAM_REVISION,JOB_PROGRAM_CONTRACT,DESCRIPTION,STATE,WO_CONTRACT,WO_CONT_DESC,CHECK_EXISTS,PROG_USED");

        f = headblk.addField("DESCRIPTION");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTDESCRIPTION: Description");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(15);
        f.setMaxLength(2000);

        f = headblk.addField("JOB_PROGRAM_REVISION");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTJOBPROGRAMREVISION: Job Program Revision");
        f.setUpperCase();
        f.setMaxLength(6);
        f.setSize(6);
        f.setReadOnly();

        f = headblk.addField("STATE");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTSTATE: State");
        f.setQueryable();
        f.setReadOnly();
        f.setSize(11);
        f.setMaxLength(4000);

        //========= Mandatory Object ============================

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTMCHCODECONTRACT: Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setUpperCase();
        f.setMaxLength(5);
        f.setSize(5);
        f.setCustomValidation("JOB_PROGRAM_ID,JOB_PROGRAM_REVISION,JOB_PROGRAM_CONTRACT,MCH_CODE_CONTRACT,MCH_CODE","MCH_CODE_CONTRACT,CHECK_EXISTS,PROG_USED");

        f = headblk.addField("MCH_CODE");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTMCHCODE: Object ID");
        f.setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT CONTRACT");
        f.setLOVProperty("WHERE","OPERATIONAL_STATUS_DB != 'SCRAPPED'");
        f.setUpperCase();
        f.setMaxLength(100);
        f.setSize(15);
        f.setCustomValidation("JOB_PROGRAM_ID,JOB_PROGRAM_REVISION,JOB_PROGRAM_CONTRACT,MCH_CODE_CONTRACT,MCH_CODE","MCH_CODE_CONTRACT,MCH_CODE,CHECK_EXISTS,PROG_USED");

        // ============== Valid ===================================

        f = headblk.addField("VALID_FROM","Datetime");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTVALIDFROM: Valid From");
        f.setCustomValidation("VALID_FROM,VALID_TO","VALID_FROM,CHECK_EXISTS");

        f = headblk.addField("VALID_TO","Datetime");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTVALIDTO: Valid To");
        f.setFunction("''");
        f.setCustomValidation("VALID_FROM,VALID_TO","VALID_TO,CHECK_EXISTS");

        //=============== defaults =================================

        f = headblk.addField("WO_CONTRACT");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTWOCONTRACT: WO Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(5);
        f.setSize(5);

        f = headblk.addField("WO_CONT_DESC");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTWOCONTDESC: WO Site Description");
        f.setFunction("Site_API.Get_Description(:WO_CONTRACT)");
        f.setReadOnly();
        f.setSize(20);

        f = headblk.addField("MAINT_ORG");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTMAINTORG: Maint. Org.");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","WO_CONTRACT CONTRACT");
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(15);
        f.setCustomValidation("WO_CONTRACT,MAINT_ORG","MAINT_ORG,MAINT_ORG_DESC,CHECK_EXISTS");

        f = headblk.addField("MAINT_ORG_DESC");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTMAINTORGDESC: Maint.Org. Description");
        f.setReadOnly();
        f.setSize(20);

        f = headblk.addField("ROUTE_ID");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTROUTEID: Route ID");
        f.setDynamicLOV("PM_ROUND_DEFINITION2","WO_CONTRACT CONTRACT");
        f.setLOVProperty("ORDER_BY","CONTRACT, ROUNDDEF_ID");
        f.setUpperCase();
        f.setMaxLength(8);
        f.setSize(8);
        f.setCustomValidation("WO_CONTRACT,ROUTE_ID","ROUTE_ID,ROUTE_ID_DESC,CHECK_EXISTS");

        f = headblk.addField("ROUTE_ID_DESC");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTROUTEIDDESC: Route Description");
        f.setReadOnly();
        f.setSize(15);

        f = headblk.addField("ACTION");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTACTION: Action");
        f.setDynamicLOV("MAINTENANCE_ACTION");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setSize(10);
        f.setCustomValidation("ACTION","ACTION_DESC");

        f = headblk.addField("ACTION_DESC");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTACTIONDESC: Action Description");
        f.setReadOnly();
        f.setSize(15);
        
        //=============== Check Boxes ===========================

        f = headblk.addField("PROG_USED");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTPROGUSED: Program Used on Object");
        f.setCheckBox("FALSE,TRUE");
        f.setSize(15);
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("MERGE");
        f.setLabel("PCMWJOBPROGRAMTOOBJECTMERGE: Merge on PM");
        f.setCheckBox("FALSE,TRUE");
        f.setCustomValidation("MERGE","MERGE");
        f.setSize(15);

        //=============== Hidden fields ================

        f = headblk.addField("USER1"); 
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CHECK_EXISTS"); 
        f.setFunction("''");
        f.setHidden();

        headblk.setView("DUAL");

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.disableMinimize();
        headblk.disableDocMan();
        headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.setDialogColumns(2); 

        headlay.defineGroup("","JOB_PROGRAM_CONTRACT,JOB_PROGRAM_ID,DESCRIPTION,JOB_PROGRAM_REVISION,STATE",false,true); 
        headlay.defineGroup(mgr.translate("PCMWJOBPROGTOOBJECTMANDATORY: Mandatory Object"),"MCH_CODE_CONTRACT,MCH_CODE",true,true);   
        headlay.defineGroup(mgr.translate("PCMWJOBPROGTOOBJECTVALID: Valid"),"VALID_FROM,VALID_TO",true,true);   
        headlay.defineGroup(mgr.translate("PCMWJOBPROGTOOBJECTDEFAULTS: Defaults"),"WO_CONTRACT,WO_CONT_DESC,MAINT_ORG,MAINT_ORG_DESC,ROUTE_ID,ROUTE_ID_DESC,ACTION,ACTION_DESC",true,true);   
        headlay.defineGroup("","PROG_USED,MERGE",false,true); 

        headlay.setSimple("WO_CONT_DESC");
        headlay.setSimple("MAINT_ORG_DESC");
        headlay.setSimple("ROUTE_ID_DESC");
        headlay.setSimple("ACTION_DESC");
    }

    private String modifiedStartPres(ASPManager mgr,String myStartPresentation)
    {
            int defInd = myStartPresentation.indexOf("Default.");
            if (defInd != -1)
            {
                    int endInd = myStartPresentation.indexOf(">",defInd);
                    String ext = myStartPresentation.substring(defInd+7,endInd);
                    String oldStr = "Default"+ext;
                    String newStr = "Default"+ext+" target=\"_parent\""; 
                    myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
            }

            int navInd = myStartPresentation.indexOf("Navigator.");
            if (navInd != -1)
            {
                    int endInd = myStartPresentation.indexOf(">",navInd);
                    String ext = myStartPresentation.substring(navInd+9,endInd);
                    String oldStr = "Navigator"+ext;
                    String newStr = "Navigator"+ext+" target=\"_parent\""; 
                    myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
            }

            return myStartPresentation;
    }

    private String modifiedEndPres(ASPManager mgr,String myEndPresentation)
    {
            myEndPresentation = mgr.replace(myEndPresentation,"document.location","parent.location");
            return myEndPresentation;
    }   

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWJOBPROGRAMTOOBJECTTITLE: Add Job Program to Object";
    }

    protected String getTitle()
    {
        return "PCMWJOBPROGRAMTOOBJECTTITLE: Add Job Program to Object";
    }

    protected AutoString getContents() throws FndException
    {
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();

        out.append("<html>\n");
        out.append("<head>\n");
        out.append(mgr.generateHeadTag(mgr.translate("PCMWJOBPROGRAMTOOBJECTTITLE: Add Job Program to Object")));
        out.append("<title></title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(modifiedStartPres(mgr,mgr.startPresentation(mgr.translate("PCMWJOBPROGRAMTOOBJECTTITLE: Add Job Program to Object"))));
        out.append(headlay.show());
        out.append(modifiedEndPres(mgr,mgr.endPresentation()));

        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"JobProgToObject\"\n");
        appendDirtyJavaScript("SEARCHBASE = SEARCHBASE.replace(\"JobProgToObject.page\",\"JobProgToObjMain.page\");\n");

        appendDirtyJavaScript("function validateMaintOrg(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkMaintOrg(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('MAINT_ORG',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('MAINT_ORG_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = 'TRUE';\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMAINTORG=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=MAINT_ORG'\n");
        appendDirtyJavaScript("		+ '&WO_CONTRACT=' + URLClientEncode(getValue_('WO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MAINT_ORG=' + URLClientEncode(getValue_('MAINT_ORG',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'MAINT_ORG',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJMAINTORG: Maint. Org."));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('MAINT_ORG',i,0);\n");
        appendDirtyJavaScript("		assignValue_('MAINT_ORG_DESC',i,1);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,2);\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJMAINTORG1: Maintenance Organization "));
        appendDirtyJavaScript("'+f.MAINT_ORG.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJMAINTORG2: does not exist in Site "));
        appendDirtyJavaScript("'+f.WO_CONTRACT.value+'.");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMAINTORG=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateRouteId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkRouteId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ROUTE_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ROUTE_ID_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = 'TRUE';\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALROUTEID=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ROUTE_ID'\n");
        appendDirtyJavaScript("		+ '&WO_CONTRACT=' + URLClientEncode(getValue_('WO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ROUTE_ID=' + URLClientEncode(getValue_('ROUTE_ID',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ROUTE_ID',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJROUTEID: Route ID"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ROUTE_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ROUTE_ID_DESC',i,1);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,2);\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJROUTEID1: Route ID "));
        appendDirtyJavaScript("'+f.ROUTE_ID.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJROUTEID2: does not exist in Site "));
        appendDirtyJavaScript("'+f.WO_CONTRACT.value+'.");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALROUTEID=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateJobProgramContract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkJobProgramContract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('JOB_PROGRAM_CONTRACT',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('WO_CONTRACT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('WO_CONT_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("		getField_('STATE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('MAINT_ORG',i).value = '';\n");
        appendDirtyJavaScript("		getField_('MAINT_ORG_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ROUTE_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ROUTE_ID_DESC',i).value = '';\n");
        appendDirtyJavaScript("		assignCheckBoxValue_('PROG_USED',i,9,'TRUE');\n");
        appendDirtyJavaScript("		getField_('JOB_PROGRAM_REVISION',i).value = '';\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALJOBCONTRACT=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=JOB_PROGRAM_CONTRACT'\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_CONTRACT=' + URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_ID=' + URLClientEncode(getValue_('JOB_PROGRAM_ID',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_REVISION=' + URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'JOB_PROGRAM_CONTRACT',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJCONTRACT: Site"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('WO_CONTRACT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('WO_CONT_DESC',i,1);\n");
        appendDirtyJavaScript("		assignValue_('DESCRIPTION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('STATE',i,3);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,4);\n");
        appendDirtyJavaScript("		assignValue_('MAINT_ORG',i,5);\n");
        appendDirtyJavaScript("		assignValue_('MAINT_ORG_DESC',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ROUTE_ID',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ROUTE_ID_DESC',i,8);\n");
        appendDirtyJavaScript("		if (i>0)\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('_PROG_USED',i-1,9,'TRUE');\n");
        appendDirtyJavaScript("		   setCheckBox_('PROG_USED',getField_('_PROG_USED',i-1).checked,i,'TRUE');\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else \n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('PROG_USED',i,9,'TRUE');\n");
        appendDirtyJavaScript("		assignValue_('JOB_PROGRAM_REVISION',i,10);\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("         if (f.JOB_PROGRAM_ID.value != '' && f.JOB_PROGRAM_REVISION.value != '' && f.JOB_PROGRAM_CONTRACT.value != '')\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST1: Revision "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_REVISION.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST2: of Job Program "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_ID.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST3: does not exist in Site "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_CONTRACT.value+'.");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALJOBCONTRACT=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateJobProgramId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkJobProgramId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('JOB_PROGRAM_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('JOB_PROGRAM_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('JOB_PROGRAM_REVISION',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("		getField_('STATE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('WO_CONTRACT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('WO_CONT_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = '';\n");
        appendDirtyJavaScript("		assignCheckBoxValue_('PROG_USED',i,8,'TRUE');\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALJOBID=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=JOB_PROGRAM_ID'\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_ID=' + URLClientEncode(getValue_('JOB_PROGRAM_ID',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_REVISION=' + URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_CONTRACT=' + URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'JOB_PROGRAM_ID',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJJOBPROGID: Job Program ID"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('JOB_PROGRAM_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('JOB_PROGRAM_REVISION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('JOB_PROGRAM_CONTRACT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('DESCRIPTION',i,3);\n");
        appendDirtyJavaScript("		assignValue_('STATE',i,4);\n");
        appendDirtyJavaScript("		assignValue_('WO_CONTRACT',i,5);\n");
        appendDirtyJavaScript("		assignValue_('WO_CONT_DESC',i,6);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,7);\n");
        appendDirtyJavaScript("		if (i>0)\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('_PROG_USED',i-1,8,'TRUE');\n");
        appendDirtyJavaScript("		   setCheckBox_('PROG_USED',getField_('_PROG_USED',i-1).checked,i,'TRUE');\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else \n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('PROG_USED',i,8,'TRUE');\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("         if (f.JOB_PROGRAM_ID.value != '' && f.JOB_PROGRAM_REVISION.value != '' && f.JOB_PROGRAM_CONTRACT.value != '')\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST1: Revision "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_REVISION.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST2: of Job Program "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_ID.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST3: does not exist in Site "));
        appendDirtyJavaScript("'+f.JOB_PROGRAM_CONTRACT.value+'.");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALJOBID=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateMchCodeContract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkMchCodeContract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('MCH_CODE_CONTRACT',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = '';\n");
        appendDirtyJavaScript("		assignCheckBoxValue_('PROG_USED',i,2,'TRUE');\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMCHCONTRACT=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=MCH_CODE_CONTRACT'\n");
        appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_CONTRACT=' + URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_ID=' + URLClientEncode(getValue_('JOB_PROGRAM_ID',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_REVISION=' + URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'MCH_CODE_CONTRACT',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJMCHCONT: Object Site"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('MCH_CODE_CONTRACT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,1);\n");
        appendDirtyJavaScript("		if (i>0)\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('_PROG_USED',i-1,2,'TRUE');\n");
        appendDirtyJavaScript("		   setCheckBox_('PROG_USED',getField_('_PROG_USED',i-1).checked,i,'TRUE');\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else \n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('PROG_USED',i,2,'TRUE');\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("         if (f.MCH_CODE_CONTRACT.value != '' && f.MCH_CODE.value != '')\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST4: Object ID "));
        appendDirtyJavaScript("'+f.MCH_CODE.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST5: does not exist in Site "));
        appendDirtyJavaScript("'+f.MCH_CODE_CONTRACT.value+'. ");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMCHCONTRACT=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateMchCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkMchCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('MCH_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CHECK_EXISTS',i).value = '';\n");
        appendDirtyJavaScript("		assignCheckBoxValue_('PROG_USED',i,3,'TRUE');\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMCHCODE=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=MCH_CODE'\n");
        appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_CONTRACT=' + URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_ID=' + URLClientEncode(getValue_('JOB_PROGRAM_ID',i))\n");
        appendDirtyJavaScript("		+ '&JOB_PROGRAM_REVISION=' + URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'MCH_CODE',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJMCHCODE: Object ID"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('MCH_CODE_CONTRACT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('MCH_CODE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,2);\n");
        appendDirtyJavaScript("		if (i>0)\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('_PROG_USED',i-1,3,'TRUE');\n");
        appendDirtyJavaScript("		   setCheckBox_('PROG_USED',getField_('_PROG_USED',i-1).checked,i,'TRUE');\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else \n");
        appendDirtyJavaScript("		   assignCheckBoxValue_('PROG_USED',i,3,'TRUE');\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("         if (f.MCH_CODE_CONTRACT.value != '' && f.MCH_CODE.value != '')\n");
        appendDirtyJavaScript("             window.alert('");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST4: Object ID "));
        appendDirtyJavaScript("'+f.MCH_CODE.value+' ");
        appendDirtyJavaScript(                 mgr.translate("PCMWJOBPROGTOOBJNOTEXIST5: does not exist in Site "));
        appendDirtyJavaScript("'+f.MCH_CODE_CONTRACT.value+'. ");
        appendDirtyJavaScript("             ');\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMCHCODE=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateValidFrom(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkValidFrom(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('VALID_FROM',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALVALIDFROM=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=VALID_FROM'\n");
        appendDirtyJavaScript("		+ '&VALID_FROM=' + URLClientEncode(getValue_('VALID_FROM',i))\n");
        appendDirtyJavaScript("		+ '&VALID_TO=' + URLClientEncode(getValue_('VALID_TO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'VALID_FROM',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJVALIDFROM: Valid From"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('VALID_FROM',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,1);\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("window.alert('");
        appendDirtyJavaScript(mgr.translate("PCMWJOBPROGTOOBJINVVALIDFROM: Valid To cannot be earlier than Valid From."));
        appendDirtyJavaScript("');");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALVALIDFROM=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateValidTo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkValidTo(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('VALID_TO',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALVALIDTO=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=VALID_TO'\n");
        appendDirtyJavaScript("		+ '&VALID_FROM=' + URLClientEncode(getValue_('VALID_FROM',i))\n");
        appendDirtyJavaScript("		+ '&VALID_TO=' + URLClientEncode(getValue_('VALID_TO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'VALID_TO',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJVALIDTO: Valid To"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('VALID_TO',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CHECK_EXISTS',i,1);\n");
        appendDirtyJavaScript("		if (f.CHECK_EXISTS.value == 'FALSE')\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("window.alert('");
        appendDirtyJavaScript(mgr.translate("PCMWJOBPROGTOOBJINVVALIDTO: Valid From cannot be later than Valid To."));
        appendDirtyJavaScript("');");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALVALIDTO=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateAction(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkAction(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ACTION',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ACTION_DESC',i).value = '';\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALACTION=1\";\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ACTION'\n");
        appendDirtyJavaScript("		+ '&ACTION=' + URLClientEncode(getValue_('ACTION',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ACTION',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJACTION: Action"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ACTION_DESC',i,0);\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALACTION=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateMerge(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkMerge(i) ) return;\n");
        appendDirtyJavaScript(" window.status='");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJPLWAITVAL: Please wait for validation"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=MERGE'\n");
        appendDirtyJavaScript("		+ '&MERGE=' + URLClientEncode(getValue_('MERGE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'MERGE',i,'");
        appendDirtyJavaScript(mgr.translate("PCMWJBPRGTOOBJMERGE: Merge on PM"));
        appendDirtyJavaScript("') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("	   if (i>0)\n");
        appendDirtyJavaScript("	   {\n");
        appendDirtyJavaScript("	      assignCheckBoxValue_('_MERGE',i-1,0,'TRUE');\n");
        appendDirtyJavaScript("	      setCheckBox_('MERGE',getField_('_MERGE',i-1).checked,i,'TRUE');\n");
        appendDirtyJavaScript("	   }\n");
        appendDirtyJavaScript("	   else \n");
        appendDirtyJavaScript("	      assignCheckBoxValue_('MERGE',i,0,'TRUE');\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?JOB_PROGRAM_ID=\"+URLClientEncode(getValue_('JOB_PROGRAM_ID',i))+\"&JOB_PROGRAM_REVISION=\"+URLClientEncode(getValue_('JOB_PROGRAM_REVISION',i))+\"&JOB_PROGRAM_CONTRACT=\"+URLClientEncode(getValue_('JOB_PROGRAM_CONTRACT',i))+\n");
        appendDirtyJavaScript("\"&MCH_CODE_CONTRACT=\"+URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))+\"&MCH_CODE=\"+URLClientEncode(getValue_('MCH_CODE',i))+\n");
        appendDirtyJavaScript("\"&ROUTE_ID=\"+URLClientEncode(getValue_('ROUTE_ID',i))+\"&MERGE=\"+URLClientEncode(getValue_('MERGE',i))+\"&VALID_FROM=\"+URLClientEncode(getValue_('VALID_FROM',i))+\"&VALID_TO=\"+URLClientEncode(getValue_('VALID_TO',i))+\n");
        appendDirtyJavaScript("\"&ACTION=\"+URLClientEncode(getValue_('ACTION',i))+\"&MAINT_ORG=\"+URLClientEncode(getValue_('MAINT_ORG',i))+\"&CHECK_EXISTS=\"+URLClientEncode(getValue_('CHECK_EXISTS',i))+\"&FROMJOBPROGOBJ=1&VALMERGE=1\";\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	f.CHECK_EXISTS.value = '';\n");
        appendDirtyJavaScript("}\n");

        // Start: Removing Data from tables when closing window.
        appendDirtyJavaScript("function OnUnLoad()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("parent.frames[1].location = \"JobProgToObjectItems.page?PARENTCLOSED=1\";\n");
        appendDirtyJavaScript("if(LOV_ALWAYS_ON_TOP)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if(opener)\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if(document.getElementById)\n");
        appendDirtyJavaScript("         eval(\"try{opener.lov_closed=true;} catch(e){}\");\n");
        appendDirtyJavaScript("      else \n");
        appendDirtyJavaScript("         opener.lov_closed=true;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else if(lov_window)\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      lov_window.close();\n");
        appendDirtyJavaScript("      lov_window = null;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function OnClose() {}");
        // End: Removing Data from tables when closing window.

        return out;
    }
}
