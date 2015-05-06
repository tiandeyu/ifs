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
*  File        : ActiveSeparateWiz.java 
*  Created     : INROLK  010224  Java conversion.
*  Modified    : 
*  CHDELK  010405  mgr.readValue() corrections
*  INROLK  010425  Corrected GUI.
*  ARWILK  010517  Did some GUI modifications.
*  INROLK  010524  Changed the function CheckMandatory(), for Call ID 65367.
*  CHCRLK  010829  Modification of Wizard GUI.
*  INROLK  011003  Site is marked as mandatory. call id 69843.
*  INROLK  011012  Values are saved when back button is pressed call id 70512 and aligned the combobox on page 2 call id 70513.     
*  JEWILK  020401  Modified to change the query parameters by entering a random value.
*                  This is done to avoid the browser fetch the page from cache. 
*  YAWILK  021128  Added the new field MCH_CODE_CONTRACT and updated the page with required functionality
*                  modified the length of MCH_CODE to 100; Added a DynamicLov to WO Site    
*  YAWILK  021129  Corrected mch_code validate parameters
*  BUNILK  030121  Codes reviewed.
*  JEWILK  030924  Corrected overridden javascript function toggleStyleDisplay(). 
*  PAPELK  031020  Call Id 106353.Modified run() when assigning transfered data. 
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040227  Web Alignment - removed unnecessary global variables,remove unnecessary method calls for hidden fields.
*  ARWILK  041110  Replaced getContents with printContents.
*  SHAFLK  050425  50830, Used mgr.translateJavaScript() and getJSLabel() methods to get the correctly translated strings in JavaScript functions. 
*  NIJALK  050617  Merged bug 50830. 
*  CHODLK  051021  Modified method run() to accpet a query string param 'SITE','MCH_CODE_SITE'. [IFS Maintenance 800xA.].
*  NEKOLK  051213  54415, Changed server method Get_Contract to Get_Def_Contract .
*  NIJALK  051220  Merged bug 54415.
*  NIJALK  060207  Modified the Info message given after finishing the wizard.
*  CHODLK  060508  Bug 44492, Modified the Info message given after finishing the wizard using a bind argument.
*  AMNILK  060629  Merged with SP1 APP7.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  SHAFLK  070112  Bug 62888, Modified finish().
*  AMNILK  070209  Merged LCS Bug 62888.
*  ILSOLK  070215  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070404  Modified Predefine() to remove the extra line shown at runtime
*  AMDILK  070419  Call Id 142243: Filtered values in the drop down for Orgcode, Modified populateListBoxes(), prepareWiz()
*  AMDILK  070425  Call Id 142921: Change the display format of the maint org combo box, modified preDefine()
*  AMDILK  070425  Call Id 142923: Modify the finish() to print the newly created wo no along with the information message
*  ILSOLK  070706  Eliminated XSS.
*  SHAFLK  071127  Bug 69431, Modified preDefine(). 
*  SHAFLK  090219  Bug 80648, Modified run() and printContents().
*  SHAFLK  090908  Bug 85582, Redisigned the use of Priority, discover code and Symptom.
*  NIJALK  100419  Bug 87935, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateWiz extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateWiz");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;
    private ASPForm frm;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock BlkOrg;
    private ASPRowSet RowsetOrg;

    private ASPBlock BlkPrio;
    private ASPRowSet RowsetPrio;

    private ASPBlock BlkDisc;
    private ASPRowSet RowsetDisc;

    private ASPBlock BlkSympt;
    private ASPRowSet RowsetSympt;

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String wo_no;
    private ASPTransactionBuffer trans;
    private boolean step1;
    private boolean step2;
    private boolean step3;
    private boolean step4;
    private String reportedby;
    private String contr;
    private String objectid;
    private String objdesc;
    private String objcon;
    private String errdesc;
    private String errdesclo;
    private String execdep;
    private String priority;
    private String disc;
    private String sympt;
    private String priodesc;
    private String discode;
    private String errsym;
    private String CancelFlag;
    private String FinishFlag;
    private ASPBuffer buff1;
    private ASPBuffer row1;
    private ASPBuffer row;
    private ASPBuffer tempbuf;
    private ASPCommand cmd;
    private boolean showHtmlPart;
    private boolean toDefaultPage;
    private String url; 
    private String calling_url;
    private String depdesc;

    //===============================================================
    // Construction 
    //===============================================================

    public ActiveSeparateWiz(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        wo_no =  "";
        ASPManager mgr = getASPManager();               

        fmt = mgr.newASPHTMLFormatter();
        ctx  = mgr.getASPContext();
        frm = mgr.getASPForm();
        trans = mgr.newASPTransactionBuffer();

        step1 = ctx.readFlag("STEP1",true);
        step2 = ctx.readFlag("STEP2",false);
        step3 = ctx.readFlag("STEP3",false);
        step4 = ctx.readFlag("STEP4",false);

        reportedby = ctx.readValue("REPORTEDBY","");
        contr = ctx.readValue("CONTR","");
        objectid = ctx.readValue("OBJECTID","");
        objdesc = ctx.readValue("OBJDESC","");
        objcon  = ctx.readValue("OBJCON");      
        errdesc = ctx.readValue("ERRDESC","");
        errdesclo = ctx.readValue("ERRDESCLO","");
        execdep = ctx.readValue("EXECDEP","");
        priority = ctx.readValue("PRIORITY","");
        disc = ctx.readValue("DISC","");
        sympt = ctx.readValue("SYMPT","");
        priodesc = ctx.readValue("PRIODESC","");
        discode = ctx.readValue("DISCODE","");
        errsym = ctx.readValue("ERRSYM","");
        showHtmlPart = ctx.readFlag("CTSSHHTML",true); 
        toDefaultPage = ctx.readFlag("CTXTODEF",false);
        depdesc = ctx.readValue("DEPDESC","");

        CancelFlag = "";
        FinishFlag = "";              

        if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
        {
            if ("ActiveSeparateWiz".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
            }
            else if ("ActiveSeparateWizFromPortal".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
                toDefaultPage = true;

                calling_url = ctx.getGlobal("CALLING_URL");
                ctx.setGlobal("CALLING_URL",calling_url);
            }
        }

        /* IFS Maintenance 800xA Integration -- */
        else if ( (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")) || !mgr.isEmpty(mgr.getQueryStringValue("SITE"))) && 
                  !mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) )
        {

            prepareWiz();

            contr = mgr.getQueryStringValue("CONTRACT");
            objectid = mgr.getQueryStringValue("MCH_CODE");
            objdesc = mgr.getQueryStringValue("MCH_CODE_DESCRIPTION"); 
            objcon = mgr.getQueryStringValue("MCH_CODE_CONTRACT"); 
            errdesc = mgr.getQueryStringValue("ERRDESC"); 
            errdesclo = mgr.getQueryStringValue("ERRDESCLO");
            execdep = mgr.getQueryStringValue("EXECDEP"); 
            priority = mgr.getQueryStringValue("PRIORITY"); 
            disc = mgr.getQueryStringValue("DISC"); 
            sympt = mgr.getQueryStringValue("SYMPT");

            if (!mgr.isEmpty(mgr.getQueryStringValue("SITE")))
            {
                contr = mgr.getQueryStringValue("SITE");
            }

            if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE_SITE")))
            {
                objcon = mgr.getQueryStringValue("MCH_CODE_SITE");
            }

            trans.clear();

            row = headset.getRow();
            row.setValue("CONTRACT",contr);
            row.setValue("MCH_CODE",objectid);
            row.setValue("MCH_CODE_DESCRIPTION",objdesc);
            row.setValue("MCH_CODE_CONTRACT",objcon);
            row.setValue("ERR_DESCR",errdesc);
            row.setValue("ERR_DESCR_LO",errdesclo);
            row.setValue("ORG_CODE",execdep);
            row.setValue("PRIORITY_ID",priority);
            row.setValue("ERR_DISCOVER_CODE",disc);
            row.setValue("ERR_SYMPTOM",sympt);


            headset.setRow(row); 
        }
        /* End - IFS Maintenance 800xA Integration -- */
        else if (mgr.dataTransfered())
        {
            prepareWiz();

            buff1 = mgr.getTransferedData();
            row1 = buff1.getBufferAt(0); 
            objectid = row1.getValue("MCH_CODE");
            row1 = buff1.getBufferAt(1); 
            objdesc = row1.getValue("MCH_CODE_DESCRIPTION");
            row1 = buff1.getBufferAt(2); 
            objcon = row1.getValue("MCH_CODE_CONTRACT"); 

            trans.clear();

            row = headset.getRow();
            if (mgr.isEmpty(objectid))
                objectid = "";
            row.setValue("MCH_CODE",objectid);
            row.setValue("MCH_CODE_DESCRIPTION",objdesc);
            row.setValue("MCH_CODE_CONTRACT",objcon);
            headset.setRow(row); 

        }
        //Bug 80648, start
        else if ("BBBB".equals(mgr.readValue("STATEVAL")))
	    next1();
        //Bug 80648, end
        else if (mgr.buttonPressed("S1CANCEL"))
            cancel();
        else if ("AAAA".equals(mgr.readValue("STATEVAL")))
            next2();
        else if (mgr.buttonPressed("S2CANCEL"))
            cancel();
        else if (mgr.buttonPressed("S2BACK"))
            back2();

        else if ("CCCC".equals(mgr.readValue("STATEVAL")))
            next3();
        else if (mgr.buttonPressed("S3CANCEL"))
            cancel();
        else if (mgr.buttonPressed("S3BACK"))
            back3();
        else if (mgr.buttonPressed("S4FINISH"))
            finish();
        else if (mgr.buttonPressed("S4CANCEL"))
            cancel();
        else if (mgr.buttonPressed("S4BACK"))
            back4();
        else if (step1)
            prepareWiz();

        adjust();

        ctx.writeFlag("STEP1",step1);
        ctx.writeFlag("STEP2",step2);
        ctx.writeFlag("STEP3",step3);
        ctx.writeFlag("STEP4",step4);
        ctx.writeFlag("CTSSHHTML",showHtmlPart);
        ctx.writeFlag("CTXTODEF",toDefaultPage);

        ctx.writeValue("REPORTEDBY",reportedby);
        ctx.writeValue("CONTR",contr);
        ctx.writeValue("OBJECTID",objectid);
        ctx.writeValue("OBJDESC",objdesc);
        ctx.writeValue("OBJCON",objcon);    
        ctx.writeValue("ERRDESC",errdesc);
        ctx.writeValue("ERRDESCLO",errdesclo);
        ctx.writeValue("EXECDEP",execdep);
        ctx.writeValue("PRIORITY",priority);
        ctx.writeValue("DISC",disc);
        ctx.writeValue("SYMPT",sympt);
        ctx.writeValue("PRIODESC",priodesc);
        ctx.writeValue("DISCODE",discode);
        ctx.writeValue("ERRSYM",errsym);
        ctx.writeValue("DEPDESC",depdesc);
    }

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        String txt = null;

        if ("MCH_CODE".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomFunction("MCHCODECON","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));

            cmd = trans.addCustomFunction("MCHDESC","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            cmd.addReference("MCH_CODE_CONTRACT","MCHCODECON/DATA");
            cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));

            trans = mgr.validate(trans);

            String strMchDesc = trans.getValue("MCHDESC/DATA/MCH_CODE_DESCRIPTION");
            String strMchCodeCon=trans.getValue("MCHCODECON/DATA/MCH_CODE_CONTRACT");

            txt = (mgr.isEmpty(strMchDesc) ? "" : (strMchDesc))+ "^"+ 
                  (mgr.isEmpty(strMchCodeCon) ? "" : (strMchCodeCon))+ "^"; 

            mgr.responseWrite(txt);
            mgr.endResponse();

        }
        
        else if ("ORG_CODE".equals(val))
        {
            trans.clear();
            cmd = trans.addCustomFunction("ORGCODEDES","ORGANIZATION_API.Get_Description","ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE"));

            trans = mgr.validate(trans); 

            String orgCodeDesc = trans.getValue("ORGCODEDES/DATA/ORGCODEDESCRIPTION");

            txt = (mgr.isEmpty(orgCodeDesc) ? "" : (orgCodeDesc))+ "^";
            mgr.responseWrite(txt);
            mgr.endResponse();
        }

        else if ("ERR_DISCOVER_CODE".equals(val))
        {
            trans.clear();
            cmd = trans.addCustomFunction("DISCDESC","WORK_ORDER_DISC_CODE_API.Get_Description","ERR_DISCOVER_CODE_DESC");
            cmd.addParameter("ERR_DISCOVER_CODE",mgr.readValue("ERR_DISCOVER_CODE"));

            trans = mgr.validate(trans); 

            String discDesc = trans.getValue("DISCDESC/DATA/ERR_DISCOVER_CODE_DESC");

            txt = (mgr.isEmpty(discDesc) ? "" : (discDesc))+ "^";
            mgr.responseWrite(txt); 
            mgr.endResponse();

        }
        else if ("ERR_SYMPTOM".equals(val))
        {
            trans.clear();
            cmd = trans.addCustomFunction("SYMDESC","WORK_ORDER_SYMPT_CODE_API.Get_Description","ERR_SYMPTOM_DESC");
            cmd.addParameter("ERR_SYMPTOM",mgr.readValue("ERR_SYMPTOM"));

            trans = mgr.validate(trans); 

            String symDesc = trans.getValue("SYMDESC/DATA/ERR_SYMPTOM_DESC");

            txt = (mgr.isEmpty(symDesc) ? "" : (symDesc))+ "^";
            mgr.responseWrite(txt);
            mgr.endResponse();

        }
        else if ("PRIORITY_ID".equals(val))
        {   trans.clear();
            cmd = trans.addCustomFunction("PRYDESC","MAINTENANCE_PRIORITY_API.Get_Description","PRIORITY_DESC");
            cmd.addParameter("PRIORITY_ID",mgr.readValue("PRIORITY_ID"));

            trans = mgr.validate(trans); 

            String pryDesc = trans.getValue("PRYDESC/DATA/PRIORITY_DESC");

            txt = (mgr.isEmpty(pryDesc) ? "" : (pryDesc))+ "^";
            mgr.responseWrite(txt);
            mgr.endResponse();

        }

    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void prepareWiz()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer data;

        trans.clear();
        cmd = trans.addCustomFunction("DEFCO","User_Default_API.Get_Contract","DEFCONTRACT");

        cmd = trans.addCustomFunction("USER","Fnd_Session_API.Get_Fnd_User","DEFREPORTED_BY");

        cmd = trans.addCustomFunction("USER1","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("DEFREPORTED_BY","USER/DATA");

        trans = mgr.perform(trans);

        if (! step2) {
           contr = trans.getValue("DEFCO/DATA/DEFCONTRACT");
	}

        reportedby = trans.getValue("USER1/DATA/REPORTED_BY");

        trans.clear();

        cmd = trans.addEmptyCommand("HEAD1","ACTIVE_SEPARATE_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD1/DATA");
        data.setNumberValue("FAULT_REP_FLAG",1);
        data.setValue("REPORTED_BY",reportedby);
        headset.addRow(data); 
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void cancel()
    {
        CancelFlag = "true";
    }

    public void next1()
    {
        ASPManager mgr = getASPManager();

        step1=false;
        step2=true;
        step3=false;
        step4=false;

        reportedby = mgr.readValue("REPORTED_BY","");  
        contr = mgr.readValue("CONTRACT","");
        objectid = mgr.readValue("MCH_CODE","");
        objdesc = mgr.readValue("MCH_CODE_DESCRIPTION","");
        objcon =mgr.readValue("MCH_CODE_CONTRACT","");

        trans.clear();

        cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addParameter("COMPANY",headset.getRow().getValue("COMPANY"));
        cmd.addParameter("REPORTED_BY",mgr.readValue("REPORTED_BY",""));

        trans = mgr.perform(trans);

        String repById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");

        row = headset.getRow();
        row.setValue("ERR_DESCR",errdesc);
        row.setValue("ERR_DESCR_LO",errdesclo);
        row.setValue("REPORTED_BY_ID",repById);
        row.setValue("ORG_CODE",execdep);
        row.setValue("ORGCODEDESCRIPTION",depdesc);

        headset.setRow(row);
    }

    public void next2()
    {
        ASPManager mgr = getASPManager();

        step1=false;
        step2=false;
        step3=true;
        step4=false;

        errdesc = mgr.readValue("ERR_DESCR","");
        errdesclo = mgr.readValue("ERR_DESCR_LO","");
        execdep = mgr.readValue("ORG_CODE","");
        depdesc = mgr.readValue("ORGCODEDESCRIPTION","");


        row = headset.getRow(); 
        if (mgr.isEmpty(priority))
            priority = "";
        row.setValue("PRIORITY_ID",priority);
        if (mgr.isEmpty(disc))
            disc = "";
        row.setValue("ERR_DISCOVER_CODE",disc);
        if (mgr.isEmpty(sympt))
            sympt = "";
        row.setValue("ERR_SYMPTOM",sympt);
        headset.setRow(row);
    }

    public void next3()
    {
        ASPManager mgr = getASPManager();

        step1=false;
        step2=false;
        step3=false;
        step4=true;

        priority = mgr.readValue("PRIORITY_ID","");
        disc = mgr.readValue("ERR_DISCOVER_CODE","");
        sympt = mgr.readValue("ERR_SYMPTOM","");

        priodesc = mgr.readValue("PRIORITY_DESC","");
        discode = mgr.readValue("ERR_DISCOVER_CODE_DESC","");
        errsym = mgr.readValue("ERR_SYMPTOM_DESC","");
    }

    public void finish()
    {
        ASPManager mgr = getASPManager();

        String attr=null;
        trans.addQuery("SYSDT","DUAL","SYSDATE","","");
        trans = mgr.perform(trans);
        String sysDate = trans.getValue("SYSDT/DATA/SYSDATE");

        trans.clear();  
        headset.changeRow();     

        String mch_code=mgr.isEmpty(headset.getRow().getFieldValue("MCH_CODE"))?"":headset.getRow().getFieldValue("MCH_CODE");
        //Bug 62888, start
        String mch_code_contract=mgr.isEmpty(headset.getRow().getFieldValue("MCH_CODE_CONTRACT"))?"":headset.getRow().getFieldValue("MCH_CODE_CONTRACT");
        //Bug 62888, end
        String err_descr=mgr.isEmpty(headset.getRow().getFieldValue("ERR_DESCR"))?"":headset.getRow().getFieldValue("ERR_DESCR");
        String priority_id=mgr.isEmpty(headset.getRow().getFieldValue("PRIORITY_ID"))?"":headset.getRow().getFieldValue("PRIORITY_ID");
        String err_discover_code=mgr.isEmpty(headset.getRow().getFieldValue("ERR_DISCOVER_CODE"))?"":headset.getRow().getFieldValue("ERR_DISCOVER_CODE");
        String err_symptom=mgr.isEmpty(headset.getRow().getFieldValue("ERR_SYMPTOM"))?"":headset.getRow().getFieldValue("ERR_SYMPTOM");

        String lsAttr =  "REG_DATE" + (char)31 + headset.getRow().getFieldValue("REG_DATE") + (char)30
                         +"REPORTED_BY" + (char)31 + headset.getRow().getFieldValue("REPORTED_BY") + (char)30
                         +"REPORTED_BY_ID" + (char)31 + headset.getRow().getFieldValue("REPORTED_BY_ID") + (char)30
                         +"CONTRACT" + (char)31 + headset.getRow().getFieldValue("CONTRACT") + (char)30
                         +"MCH_CODE" + (char)31 + mch_code + (char)30
                         +"MCH_CODE_CONTRACT" + (char)31 + mch_code_contract + (char)30 
                         +"ERR_DESCR" + (char)31 + err_descr + (char)30
                         +"ORG_CODE" + (char)31 + headset.getRow().getFieldValue("ORG_CODE") + (char)30
                         +"PM_TYPE" + (char)31 + headset.getRow().getFieldValue("PM_TYPE") + (char)30
                         +"FAULT_REP_FLAG" + (char)31 + headset.getRow().getFieldValue("FAULT_REP_FLAG") + (char)30
                         +"COMPANY" + (char)31 + headset.getRow().getFieldValue("COMPANY") + (char)30
                         +"ERR_DESCR_LO" + (char)31 + headset.getRow().getFieldValue("ERR_DESCR_LO") + (char)30
                         +"PRIORITY_ID" + (char)31 + priority_id + (char)30
                         +"ERR_DISCOVER_CODE" + (char)31 + err_discover_code + (char)30
                         +"ERR_SYMPTOM" + (char)31 + err_symptom + (char)30
                         +"CONNECTION_TYPE_DB" + (char)31 + "EQUIPMENT" + (char)30;

        cmd = trans.addCustomCommand("SUBMITFORM", "ACTIVE_SEPARATE_API.New__"); 
        cmd.addParameter("INFO");    
        cmd.addParameter("OBJID", headset.getRow().getValue("OBJID"));
        cmd.addParameter("OBJVERSION", headset.getRow().getValue("OBJVERSION"));
        cmd.addParameter("ATTR",lsAttr);
        cmd.addParameter("ACTION","DO");

        trans = mgr.perform(trans);
        attr = trans.getValue("SUBMITFORM/DATA/ATTR");

        int beg_pos = attr.indexOf("WO_NO")+6;
        int end_pos = beg_pos + 6;
        wo_no = attr.substring(beg_pos,end_pos);


	FinishFlag = "true";
    }

    public void back2()
    {
        ASPManager mgr = getASPManager();

        step1=true;
        step2=false;
        step3=false;
        step4=false;

        errdesc = mgr.readValue("ERR_DESCR","");
        errdesclo = mgr.readValue("ERR_DESCR_LO","");
        execdep = mgr.readValue("ORG_CODE","");
        depdesc = mgr.readValue("ORGCODEDESCRIPTION","");

        row = headset.getRow();
        row.setValue("REPORTED_BY",reportedby);
        row.setValue("CONTRACT",contr);
        if (mgr.isEmpty(objectid))
            objectid = "";
        row.setValue("MCH_CODE",objectid);
        row.setValue("MCH_CODE_DESCRIPTION",objdesc);
        row.setValue("MCH_CODE_CONTRACT",objcon); 
        headset.setRow(row);
    }

    public void back3()
    {
        ASPManager mgr = getASPManager();

        step1=false;
        step2=true;
        step3=false;
        step4=false;
        
        priority = mgr.readValue("PRIORITY_ID","");
        disc = mgr.readValue("ERR_DISCOVER_CODE","");
        sympt = mgr.readValue("ERR_SYMPTOM","");

        priodesc = mgr.readValue("PRIORITY_DESC","");
        discode = mgr.readValue("ERR_DISCOVER_CODE_DESC","");
        errsym = mgr.readValue("ERR_SYMPTOM_DESC","");

        row = headset.getRow();
        row.setValue("ERR_DESCR",errdesc);
        row.setValue("ERR_DESCR_LO",errdesclo);
        row.setValue("ORG_CODE",execdep);
        row.setValue("ORGCODEDESCRIPTION",depdesc);

        headset.setRow(row);
    }

    public void back4()
    {
        ASPManager mgr = getASPManager();

        step1=false;
        step2=false;
        step3=true;
        step4=false;

        row = headset.getRow(); 
        if (mgr.isEmpty(priority))
            priority = "";
        row.setValue("PRIORITY_ID",priority);
        if (mgr.isEmpty(disc))
            disc = "";
        row.setValue("ERR_DISCOVER_CODE",disc);
        if (mgr.isEmpty(sympt))
            sympt = "";
        row.setValue("ERR_SYMPTOM",sympt);
        headset.setRow(row);
                
        row.setValue("PRIORITY_DESC",priodesc);
        row.setValue("ERR_DISCOVER_CODE_DESC",discode);
        row.setValue("ERR_SYMPTOM_DESC",errsym);

    }

//-------------------------------------------------------
//  Populate List Boxes
//-------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("REG_DATE").
        setHidden(); 

        headblk.addField("REPORTED_BY").
        setSize(8).
        setMandatory().
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWACTIVESEPARATEWIZREPORTEDBY: Reported by:").
        setMaxLength(20).
        setUpperCase(); 

        headblk.addField("REPORTED_BY_ID").
        setSize(8).
        setUpperCase().
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZREPBY: Reported By:");  

        headblk.addField("CONTRACT").
        setSize(8).
        setUpperCase().
        setMandatory().
        setMaxLength(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATEWIZCONTRCT: WO Site:");

	//Bug 87935, Start, Modified code to get correct pres objects
        f = headblk.addField("MCH_CODE");
        f.setLabel("PCMWACTIVESEPARATEWIZMCH_CODE: Object ID:");
        f.setSize(37);
        f.setLOV("MaintenanceObjectLov.page","MCH_CODE_CONTRACT CONTRACT",600,445);
        f.setUpperCase();
        f.setMaxLength(100);
        f.setCustomValidation("MCH_CODE","MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT");
	f.setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");
	//Bug 87935, End

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(37).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEWIZMCHCODEDESCRIPTION: Description:");

        headblk.addField("MCH_CODE_CONTRACT").
        setLabel("PCMWACTIVESEPARATEWIZMCH_CODE_CONTRACT: Site:").
        setSize(10).
        setMaxLength(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setUpperCase();

        headblk.addField("ERR_DESCR").
        setLabel("PCMWACTIVESEPARATEWIZERRDESCR: Short Description:").
        setMaxLength(60).
        setMandatory().
        setSize(37);

        headblk.addField("ERR_DESCR_LO").
        setLabel("PCMWACTIVESEPARATEWIZERRDESCLO: Fault Description:").
        setHeight(3).
        setSize(37);

        headblk.addField("ORG_CODE").
        setSize(18).
        setLabel("PCMWACTIVESEPARATEWIZORGCODE: Maintenance Organization:").
        setMandatory().   
        setUpperCase().
        setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCRIPTION").
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);

        headblk.addField("ORGCODEDESCRIPTION").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEWIZORGCODEDESC: Organization Description").   
        setFunction("ORGANIZATION_API.Get_Description(:CONTRACT,:ORG_CODE)");   

        headblk.addField("PRIORITY_ID").
        setSize(10).
        setLabel("PCMWACTIVESEPARATEWIZPRIORITY: Priority:").
        setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
        setCustomValidation("PRIORITY_ID","PRIORITY_DESC").
        setUpperCase();

        headblk.addField("PRIORITY_DESC").
        setLabel("PCMWACTIVESEPARATEWIZPRIODESC: Priority Description:").   
        setSize(30).
        setReadOnly().
        setFunction("MAINTENANCE_PRIORITY_API.Get_Description(:PRIORITY_ID)");

        headblk.addField("ERR_DISCOVER_CODE").
        setSize(10).
        setDynamicLOV("WORK_ORDER_DISC_CODE",600,450).
        setCustomValidation("ERR_DISCOVER_CODE","ERR_DISCOVER_CODE_DESC").
        setLabel("PCMWACTIVESEPARATEWIZDISCVR: Discover Code:").
        setUpperCase();

        headblk.addField("ERR_DISCOVER_CODE_DESC").
        setLabel("PCMWACTIVESEPARATEWIZDISCDESC: Discovery Description:").
        setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)").
        setReadOnly().
        setSize(30);  

        headblk.addField("ERR_SYMPTOM").
        setSize(10).
        setLabel("PCMWACTIVESEPARATEWIZERRSSYMPT: Symptom:").
        setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450).
        setCustomValidation("ERR_SYMPTOM","ERR_SYMPTOM_DESC").
        setUpperCase();

        headblk.addField("ERR_SYMPTOM_DESC").
        setLabel("PCMWACTIVESEPARATEWIZSYMPTDESC: Symptom Description:").
        setReadOnly().
        setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)").
        setSize(30);  
        headblk.addField("PM_TYPE").   
        setSize(19).
        setHidden();

        headblk.addField("FAULT_REP_FLAG","Number").
        setSize(8).
        setHidden();

        headblk.addField("COMPANY").
        setHidden();

        headblk.addField("DEFCONTRACT").
        setSize(8).
        setHidden().
        setFunction("''");

        headblk.addField("DEFREPORTED_BY").
        setSize(8).
        setHidden().
        setFunction("''");

        headblk.addField("INFO").
        setSize(8).
        setHidden().
        setFunction("''");

        headblk.addField("ATTR").
        setSize(8).
        setHidden().
        setFunction("''");

        headblk.addField("ACTION").
        setSize(8).
        setHidden().
        setFunction("''");

        f = headblk.addField("CONNECTION_TYPE_DB");
        f.setHidden();

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.SAVERETURN);
        headbar.disableCommand(headbar.CANCELEDIT);
        headbar.disableCommand(headbar.FORWARD);
        headbar.disableModeLabel();
        headblk.setTitle(mgr.translate("PCMWACTIVESEPARATEWIZBLKTITLE: Fault Report Wizard")); 

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setEditable();
        headlay.setDialogColumns(1);
        headlay.setSimple("PRIORITY_DESC");
        headlay.setSimple("ERR_DISCOVER_CODE_DESC");
        headlay.setSimple("ERR_SYMPTOM_DESC");
        headlay.setSimple("ORGCODEDESCRIPTION");



        disableHomeIcon();
        disableNavigate();
        disableOptions();

        headlay.showBottomLine(false);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        frm.setFormWidth(730);
        headbar.setWidth(frm.getFormWidth());  

        if (step1)
        {
            mgr.getASPField("ERR_DESCR").setHidden(); 
            mgr.getASPField("ERR_DESCR_LO").setHidden(); 
            mgr.getASPField("ORG_CODE").setHidden();            
            mgr.getASPField("ORGCODEDESCRIPTION").setHidden();
            mgr.getASPField("PRIORITY_ID").setHidden(); 
            mgr.getASPField("PRIORITY_DESC").setHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE").setHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE_DESC").setHidden(); 
            mgr.getASPField("ERR_SYMPTOM").setHidden(); 
            mgr.getASPField("ERR_SYMPTOM_DESC").setHidden(); 

        }
        else if (step2)
        {
            mgr.getASPField("REPORTED_BY").setHidden();
            mgr.getASPField("CONTRACT").setHidden(); 
            mgr.getASPField("MCH_CODE").setHidden(); 
            mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden(); 
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
            mgr.getASPField("PRIORITY_ID").setHidden(); 
            mgr.getASPField("PRIORITY_DESC").setHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE").setHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE_DESC").setHidden(); 
            mgr.getASPField("ERR_SYMPTOM").setHidden(); 
            mgr.getASPField("ERR_SYMPTOM_DESC").setHidden(); 

        }
        else if (step3)
        {
            mgr.getASPField("PRIORITY_ID").unsetHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE").unsetHidden(); 
            mgr.getASPField("ERR_SYMPTOM").unsetHidden();
            mgr.getASPField("PRIORITY_DESC").unsetHidden(); 
            mgr.getASPField("ERR_DISCOVER_CODE_DESC").unsetHidden(); 
            mgr.getASPField("ERR_SYMPTOM_DESC").unsetHidden();                        


            mgr.getASPField("REPORTED_BY").setHidden();
            mgr.getASPField("CONTRACT").setHidden(); 
            mgr.getASPField("MCH_CODE").setHidden(); 
            mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();

            mgr.getASPField("ERR_DESCR").setHidden(); 
            mgr.getASPField("ERR_DESCR_LO").setHidden(); 
            mgr.getASPField("ORG_CODE").setHidden();
            mgr.getASPField("ORGCODEDESCRIPTION").setHidden();

        }
        else if (step4)
        {

            row = headset.getRow();
            row.setValue("REPORTED_BY",reportedby);
            row.setValue("CONTRACT",contr);
            if (mgr.isEmpty(objectid))
                objectid = "";
            row.setValue("MCH_CODE",objectid);
            row.setValue("MCH_CODE_DESCRIPTION",objdesc);
            row.setValue("MCH_CODE_CONTRACT",objcon);
            row.setValue("ERR_DESCR",errdesc);
            if (mgr.isEmpty(errdesclo))
                errdesclo = "";
            row.setValue("ERR_DESCR_LO",errdesclo);
            row.setValue("ORG_CODE",execdep);
            if (mgr.isEmpty(priority))
                priority = "";
            row.setValue("PRIORITY_ID",priority);
            if (mgr.isEmpty(disc))
                disc = "";
            row.setValue("ERR_DISCOVER_CODE",disc);
            if (mgr.isEmpty(sympt))
                sympt = "";
            row.setValue("ERR_SYMPTOM",sympt);
            row.setValue("PRIORITY_DESC",priodesc);
            row.setValue("ERR_DISCOVER_CODE_DESC",discode);
            row.setValue("ERR_SYMPTOM_DESC",errsym);
            headset.setRow(row);

            mgr.getASPField("REPORTED_BY").unsetHidden().setReadOnly(); 
            mgr.getASPField("CONTRACT").unsetHidden().setReadOnly(); 
            mgr.getASPField("MCH_CODE").unsetHidden().setReadOnly(); 
            mgr.getASPField("MCH_CODE_DESCRIPTION").unsetHidden().setReadOnly();
            mgr.getASPField("MCH_CODE_CONTRACT").unsetHidden().setReadOnly();
            mgr.getASPField("ERR_DESCR").unsetHidden().setReadOnly(); 
            mgr.getASPField("ERR_DESCR_LO").unsetHidden().setReadOnly(); 
            mgr.getASPField("ORG_CODE").unsetHidden().setReadOnly();
            mgr.getASPField("ORGCODEDESCRIPTION").setHidden();
            mgr.getASPField("PRIORITY_ID").unsetHidden().setReadOnly(); 
            mgr.getASPField("ERR_DISCOVER_CODE").unsetHidden().setReadOnly(); 
            mgr.getASPField("ERR_SYMPTOM").unsetHidden().setReadOnly();
            mgr.getASPField("PRIORITY_DESC").unsetHidden().setReadOnly(); 
            mgr.getASPField("ERR_DISCOVER_CODE_DESC").unsetHidden().setReadOnly(); 
            mgr.getASPField("ERR_SYMPTOM_DESC").unsetHidden().setReadOnly();                        
        }

        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");              
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVESEPARATEWIZTITLE: Fault Report Wizard";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATEWIZTITLE: Fault Report Wizard";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (showHtmlPart)
        {
            printHiddenField("STATEVAL","");

            if (headlay.isVisible())
            {
                appendToHTML(headbar.showBar());
                appendToHTML("<table id=\"TBL\" border=0 class=\"BlockLayoutTable\" cellspacing=10 cellpadding=1 width= 730>\n");
                appendToHTML("	<tr>\n");
                appendToHTML("		<td>\n");
                appendToHTML("		<table id=\"TBL1\" border=0 cellspacing=10 cellpadding=1 width= '20%'>\n");
                appendToHTML("		   <tr>\n");
                appendToHTML("		     <td nowrap height=\"26\" align=\"left\"><img src = \"../pcmw/images/Faultreportwizard.gif\"></td>\n");
                appendToHTML("		     <td nowrap height=\"26\" align=\"left\">\n");

                if (step1)
                {
                    appendToHTML("			<table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");
                    appendToHTML("				<tr>	\n");
                    appendToHTML("					<td>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZWLCM: Welcome to the Fault report wizard!")));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZREGSTR: If you want to connect an object to your fault report, please register object id.")));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("					   </br></br>\n");
                    appendToHTML("					</td>			   \n");
                    appendToHTML("				</tr>				\n");
                    appendToHTML("			 </table>\n");
                    appendToHTML("			 </br></br>		\n");
                }
                else if (step2)
                {
                    appendToHTML("<table border=0 class=\"BlockLayoutTable1\" cellspacing=0 cellpadding=1 width= >\n");
                    appendToHTML("    <tr>	\n");
                    appendToHTML("        <td>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZFLTRPT: Fault Report")));
                    appendToHTML("					   </br>\n");
                    appendToHTML(fmt.drawReadValue(contr));
                    appendToHTML("					   </br>\n");
                    appendToHTML(fmt.drawReadValue(objcon+"  "+objectid+"  "+objdesc));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZCRTFLTRPT: Here you can create a fault report.")));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("        </td>\n");
                    appendToHTML("    </tr>\n");
                    appendToHTML("</table>\n");
                    appendToHTML("</br></br></br></br></br>			   \n");
                }
                else if (step3)
                {
                    appendToHTML("			<table border=0 class=\"BlockLayoutTable1\" cellspacing=0 cellpadding=1>\n");
                    appendToHTML("				<tr>	\n");
                    appendToHTML("					<td>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZPRIO: Here you can decide priority, how the fault was discovered and the symptoms.")));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("					</td>			   \n");
                    appendToHTML("				</tr>				\n");
                    appendToHTML("			</table>\n");
                    appendToHTML("			</br></br></br></br></br></br>			   \n");
                }
                else if (step4)
                {
                    appendToHTML("			<table border=0 class=\"BlockLayoutTable1\" cellspacing=0 cellpadding=1>\n");
                    appendToHTML("				<tr>	\n");
                    appendToHTML("					<td>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZREG: This is what you have registered for the event.")));
                    appendToHTML("					   </br>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZSAV: If you want to save your fault report, click Finish.")));
                    appendToHTML("					   </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("					</td>			   \n");
                    appendToHTML("				</tr>				\n");
                    appendToHTML("			</table>			   \n");
                }

                appendToHTML("		  </td>\n");
                appendToHTML("		 </tr>\n");
                appendToHTML("		</table> \n");
                appendToHTML("		</td>\n");
                appendToHTML("	</tr>\n");
                appendToHTML("</table>\n");
                appendToHTML("<table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=730>\n");
                appendToHTML(" <tr>\n");
                appendToHTML(fmt.drawReadValue(".............................................................."));
                appendToHTML(fmt.drawReadValue("............................................................."));
                appendToHTML(fmt.drawReadValue("............................................................"));
                appendToHTML(" </tr>\n");
                appendToHTML("</table> \n");
                appendToHTML("<table id=\"TBL2\" border=0 cellspacing=10 cellpadding=1 width= 745>\n");
                appendToHTML("  <tr>		\n");
                appendToHTML("    <td align=\"right\">\n");


                if (step1)
                {
                    appendToHTML("		<table width= '99.75%'>\n");
                    appendToHTML("			<tr>		\n");
                    appendToHTML("				<td align=\"right\">			\n");
                    //Bug 80648, start
                    appendToHTML(fmt.drawButton("S1NEXT",mgr.translate("PCMWACTIVESEPARATEWIZNXT1: Next>"),"onClick=checkMandatory0()"));
                    //Bug 80648, end
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("S1CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZCANCL1: Cancel"),""));
                    appendToHTML("				</td>   \n");
                    appendToHTML("			</tr>\n");
                    appendToHTML("		</table>\n");
                }
                else if (step2)
                {
                    appendToHTML("		<table width= '99.75%'>\n");
                    appendToHTML("			<tr>		\n");
                    appendToHTML("				<td align=\"right\">			   \n");
                    appendToHTML(fmt.drawSubmit("S2BACK",mgr.translate("PCMWACTIVESEPARATEWIZBCK2: <Back"),""));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawButton("S2NEXT",mgr.translate("PCMWACTIVESEPARATEWIZNXT2: Next>"),"onClick=checkMandatory()"));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("S2CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZCANCL2: Cancel"),""));
                    appendToHTML("				</td>   \n");
                    appendToHTML("			</tr>\n");
                    appendToHTML("		</table>\n");
                }
                else if (step3)
                {
                    appendToHTML("		<table width= '99.75%'>\n");
                    appendToHTML("			<tr>		\n");
                    appendToHTML("				<td align=\"right\">			   \n");
                    appendToHTML(fmt.drawSubmit("S3BACK",mgr.translate("PCMWACTIVESEPARATEWIZBCK3: <Back"),""));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawButton("S3NEXT",mgr.translate("PCMWACTIVESEPARATEWIZNXT3: Next>"),"onClick=checkMandatory1()"));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("S3CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZCANCL3: Cancel"),""));
                    appendToHTML("				</td>   \n");
                    appendToHTML("			</tr>		\n");
                    appendToHTML("		</table>\n");
                }
                else if (step4)
                {
                    appendToHTML("		<table width= '99.75%'>\n");
                    appendToHTML("			<tr>		\n");
                    appendToHTML("				<td align=\"right\">			    \n");
                    appendToHTML(fmt.drawSubmit("S4BACK",mgr.translate("PCMWACTIVESEPARATEWIZBCK4: <Back"),""));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("S4CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZCANCL4: Cancel"),""));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("S4FINISH",mgr.translate("PCMWACTIVESEPARATEWIZFNSH: Finish"),""));
                    appendToHTML("				</td>   \n");
                    appendToHTML("			</tr>							\n");
                    appendToHTML("		</table>\n");
                }
            }

            appendToHTML("    </td>\n");
            appendToHTML("  </tr>\n");
            appendToHTML("</table>						\n");
            appendToHTML("<p>\n");

            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");


            appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   el = document.getElementById(el);\n");
            appendDirtyJavaScript("   if(el.style.display!='none')\n");
            appendDirtyJavaScript("   {  \n");
            appendDirtyJavaScript("      el.style.display='none';\n");
            appendDirtyJavaScript("      TBL1.style.display='none';\n");
            appendDirtyJavaScript("      TBL2.style.display='none';\n");
            appendDirtyJavaScript("      lineTBL.style.display='none';\n");
            appendDirtyJavaScript("   }  \n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("   {  \n");
            appendDirtyJavaScript("      el.style.display='block';\n");
            appendDirtyJavaScript("      TBL1.style.display='block';\n");
            appendDirtyJavaScript("      TBL2.style.display='block';\n");
            appendDirtyJavaScript("      lineTBL.style.display='block';     \n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n");


            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(CancelFlag);
            appendDirtyJavaScript("'=='true')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("    self.close();\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(FinishFlag);
            appendDirtyJavaScript("'=='true')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   alert('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEWIZFINSH: Fault Report &1 has been submitted.", wo_no));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("   self.close();\n");
            appendDirtyJavaScript("}\n");

            //Bug 80648, start
            appendDirtyJavaScript("function checkMandatory0()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  	if(checkHeadFields(0))\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		document.form.STATEVAL.value = \"BBBB\";\n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function checkMandatory()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  	if(checkHeadFields(0))\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		document.form.STATEVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
            //Bug 80648, end             
            appendDirtyJavaScript("function checkMandatory1()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  	if(checkHeadFields(0))\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		document.form.STATEVAL.value = \"CCCC\";\n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

        }
        else
        {
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");

            appendDirtyJavaScript("window.location = '");

            // XSS_Safe ILSOLK 20070706
            if (toDefaultPage)
            {
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));
                appendDirtyJavaScript("';\n");
            }
            else
            {
                appendDirtyJavaScript(url);
                appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n"); 
            }

            appendDirtyJavaScript("    if (");
            appendDirtyJavaScript(    mgr.isExplorer());
            appendDirtyJavaScript("    )\n");
            appendDirtyJavaScript("    { \n");
            appendDirtyJavaScript("      window.open('");
            appendDirtyJavaScript(url);
            appendDirtyJavaScript("'+\"pcmw/ActiveSeparateWiz.page?RANDOM_VAL=\"+Math.random(),\"frmActiveSeparateWiz\",\"resizable=yes, status=yes,menubar=no,height = 550,width = 752\");\n");
            appendDirtyJavaScript("    } \n");
            appendDirtyJavaScript("    else \n");
            appendDirtyJavaScript("    { \n");
            appendDirtyJavaScript("      window.open('");
            appendDirtyJavaScript(url);
            appendDirtyJavaScript("'+\"pcmw/ActiveSeparateWiz.page?RANDOM_VAL=\"+Math.random(),\"frmActiveSeparateWiz\",\"resizable=yes, status=yes,menubar=no,height=625,width=752\");\n");
            appendDirtyJavaScript("    } \n");
        }
    }
}
