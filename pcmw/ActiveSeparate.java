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
*  File        : ActiveSeparate.java 
*  Created     : ASP2JAVA Tool  010228  Created Using the ASP file ActiveSeparate.asp  
*  Modified    : 
*  VAGULK          Set some fields DefaultNotVisible() , Call ID - 70298
*  VAGULK          Set Date & Object Description to DefaultNotVisible 70298
*  YAWILK          Changed the MCH_CODE length to 100 Add the new object site MCH_CODE_CONTRACT
*  YAWILK          Set Connectin_type_db to 'EQUIPMENT'
*  YAWILK          Corrected mch_code validate parameters  
*  NIMHLK          Added a new action to call condition code dialog according to specification W110 - Condition Codes.
*  YAWILK  030129  Call Id 93402 added sMchCodeContract to get the Object Site and modified transferWrite().
*  THWILK  031018  Call ID 104789 Added an extra parameter(#)to the deffinition of the field WO_NO.
*  CHCRLK  031105  Added missing where condition to method countFind(). [Call ID 110123]
*  CHCRLK  031105  Modified method preparefault() to open action in same window. [Call ID 110127]
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040108  Made the field order according to the order in the Centura application(Web Alignment).
*  ARWILK  040716  Changed methods calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  041110  Replaced getContents with printContents.
*  SHAFLK  050809  Bug 52123, Modified newRow(), preDefine() and added rmbConnectedDocs(). 
*  NIJALK  050926  Merged bug 52123.
*  SHAFLK  050919  Bug 52880, Modified adjust(),
*  NIJALK  051004  Merged bug 52880.
*  NEKOLK  051125  Bug 54415, Changed server method Get_Contract to Get_Def_Contract .
*  NIJALK  051220  Merged bug 54415.
*  NIJALK  051222  LCS 54415, Modified LOV for 'MCH_CODE' and modified custom validation to fetch values from LOV.
* ----------------------------------------------------------------------------
*  CHODLK  060510  Bug 56390, Modified method run(),transferWrite(). 
*  CHODLK  060524  Bug 56390-2, Modified method transferWrite().
*  AMNILK  060629  Merged with SP1 APP7.
*  JEWILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060905  Merged bug 58216.
*  AMDILK  060918  Removed the RMB "Send SMS ..."
*  AMNILK  060918  Call Id:  139557.Modified the preDefine() & adjust() methods.
*  SHAFLK  060731  Bug 59613, Modified changeConditionCode(). 
*  ILSOLK  070215  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  ILSOLK  070705  Eliminated XSS. 
*  NIJALK  070525  Bug 64744, Modified run(), preDefine() and printContents(). Added saveReturn(), updatePostings().
*  AMDILK  070716  Merged bug 64744
*  ILSOLK  070905  Modifed okFind().(Call ID 148318)
*  SHAFLK  080326  Bug 72539, Modified adjust(). 
*  SHAFLK  080730  Bug 76003, Modified transferWrite() and preDefine().
*  SHAFLK  090220  Bug 80648, Modified preDefine() and printContents().
*  SHAFLK  091117  Bug 86948, Modified transferWrite().
*  ILSOLK  100916  Bug 93061, Modified saveReturn().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class ActiveSeparate extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout lay;

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String url;
    //private String rmbPrepFaultFlag;
    private String sWoNo;
    //private String rmbSendSMSFlag;
    private boolean notTransferd;
    private ASPCommand cmd;
    private String reportById;
    private String name;
    private ASPQuery q;
    private ASPBuffer data;
    private String head_command;
    private String sContract;  
    private String sMchCode; 
    private String sMchNameDes;
    private String sMchCodeContract;
    private String urlName;
    private int count;
    private String callingUrl;

    private int curr_row;
    private String luname;
    private ASPBuffer buff;
    private String keyRef;
    private String wono;
    private ASPBuffer row;
    
    private String sTestPointId;
    private String pmDescription;
    private String insNote;
    private String sPmNo;

    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private boolean bPpChanged;

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparate(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        sContract = "";
        sMchCode = "";
        sMchNameDes = "";
        sMchCodeContract = "";

        ASPManager mgr = getASPManager();
        ASPBuffer buff1=null;
        ASPBuffer row1=null;

        trans = mgr.newASPTransactionBuffer();

        ctx = mgr.getASPContext();
        sContract = ctx.readValue("CTXCONTRACT",sContract);
        sMchCode = ctx.readValue("CTXMCHCODE",sMchCode);
        sMchNameDes = ctx.readValue("CTXMCHNAMEDESCRIPTION",sMchNameDes);
        sMchCodeContract = ctx.readValue("CTXMCHCODECONTRACT",sMchCodeContract);
        sTestPointId = ctx.readValue("CTXTESTPOINTID","");
        pmDescription= ctx.readValue("PMDESC","");
        insNote = ctx.readValue("INSPNOTE","");
        sPmNo = ctx.readValue("PMNO","");
        
        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"); 
        sWoNo = "";
        //rmbSendSMSFlag = "";

        notTransferd = true;
        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            okFind();
            notTransferd = false;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
        {
            sContract = mgr.readValue("CONTRACT");
            sMchCode = mgr.readValue("MCH_CODE");
            sMchNameDes = mgr.readValue("MCH_CODE_DESCRIPTION"); 
            sMchCodeContract = mgr.readValue("MCH_CODE_CONTRACT");
            sTestPointId = mgr.readValue("TEST_POINT_ID");
            sPmNo = mgr.readValue("PM_NO");
            pmDescription = mgr.readValue("PM_DESCR");
            insNote = mgr.readValue("NOTE");
            transferWrite(sContract,sMchCode,sMchNameDes,sMchCodeContract,sTestPointId,sPmNo,pmDescription,insNote);
            lay.setLayoutMode(lay.NEW_LAYOUT);  
            notTransferd = false;    
        }
        else if (mgr.dataTransfered())
        {
            // nimhlk - Begin
            urlName = ctx.findGlobal("FROM_URL");
            count = urlName.lastIndexOf('/');
            callingUrl = urlName.substring(count+1);

            if ("ChangeConditionCodeDlg.page".equals(callingUrl))
                okFind();
            // nimhlk - End
            else
            {
                buff1 = mgr.getTransferedData();
                row1 = buff1.getBufferAt(0); 
                sContract = row1.getValue("CONTRACT");
                row1 = buff1.getBufferAt(1); 
                sMchCode = row1.getValue("MCH_CODE");
                row1 = buff1.getBufferAt(2); 
                sMchNameDes = row1.getValue("MCH_CODE_DESCRIPTION");
					 transferWrite(sContract,sMchCode,sMchNameDes,null,null,null,null,null);
			} 
            notTransferd = false;
        }
        else if ("TRUE".equals(mgr.readValue("PRPOSTCHANGED")))
            updatePostings();

        adjust();   
        ctx.writeValue("CTXCONTRACT",sContract);
        ctx.writeValue("CTXMCHCODE",sMchCode);
        ctx.writeValue("CTXMCHNAMEDESCRIPTION",sMchNameDes);
        ctx.writeValue("CTXTESTPOINTID",sTestPointId);
        ctx.writeValue("PMNO",sPmNo);
        ctx.writeValue("PMDESC",pmDescription);
        ctx.writeValue("INSPNOTE",insNote);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        String val=null;
        String txt ="";
        String orgCodeDesc="";
        String mchCodeDes="";
        String discDesc="";
        String symDesc="";
        String pryDesc="";
        String mchCodeCon="";
        ASPBuffer buf=null;

        val = mgr.readValue("VALIDATE");

        if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
            cmd.addParameter("REPORTED_BY",mgr.readValue("REPORTED_BY"));

            cmd = trans.addCustomFunction("REPNAME","Employee_API.Get_Employee_Info","NAME");
            cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
            cmd.addReference("REPORTED_BY_ID","REPBYID/DATA");

            trans = mgr.validate(trans);

            reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
            name = trans.getValue("REPNAME/DATA/NAME");

            txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^" + (mgr.isEmpty(name) ? "" : (name))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("ORGCODEDES","ORGANIZATION_API.Get_Description","ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE"));

            trans = mgr.validate(trans); 

            orgCodeDesc = trans.getValue("ORGCODEDES/DATA/ORGCODEDESCRIPTION");

            txt = (mgr.isEmpty(orgCodeDesc) ? "" : (orgCodeDesc))+ "^";
            mgr.responseWrite(txt); 
        }
        else if ("MCH_CODE".equals(val))
        {
            String mchCode = mgr.readValue("MCH_CODE");
            String validationAttrAtr1 = "";

            if (mchCode.indexOf("~") > -1)
            {
                String sMchCode = mgr.readValue("MCH_CODE");
                mchCode = sMchCode.substring(0,sMchCode.indexOf("~"));   
                validationAttrAtr1 = sMchCode.substring(sMchCode.indexOf("~")+1,sMchCode.length());
                mchCodeDes =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));
                mchCodeCon =  validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());                
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomFunction("MCHCODECON","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
                cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));

                cmd = trans.addCustomFunction("MCHCODEDES","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
                cmd.addReference("MCH_CODE_CONTRACT","MCHCODECON/DATA");
                cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));

                trans = mgr.validate(trans); 

                mchCodeDes = trans.getValue("MCHCODEDES/DATA/MCH_CODE_DESCRIPTION");
                mchCodeCon = trans.getValue("MCHCODECON/DATA/MCH_CODE_CONTRACT");
            }

            txt = (mgr.isEmpty(mchCode) ? "" : (mchCode))+ "^"+
                  (mgr.isEmpty(mchCodeDes) ? "" : (mchCodeDes))+ "^"+
                  (mgr.isEmpty(mchCodeCon) ? "" : (mchCodeCon))+ "^";
            mgr.responseWrite(txt); 
        }
        else if ("ERR_DISCOVER_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("DISCDESC","WORK_ORDER_DISC_CODE_API.Get_Description","DISCOVERDESCRIPTION");
            cmd.addParameter("ERR_DISCOVER_CODE",mgr.readValue("ERR_DISCOVER_CODE"));

            trans = mgr.validate(trans); 

            discDesc = trans.getValue("DISCDESC/DATA/DISCOVERDESCRIPTION");

            txt = (mgr.isEmpty(discDesc) ? "" : (discDesc))+ "^";
            mgr.responseWrite(txt); 
        }
        else if ("ERR_SYMPTOM".equals(val))
        {
            cmd = trans.addCustomFunction("SYMDESC","WORK_ORDER_SYMPT_CODE_API.Get_Description","SYMPTOMDESCRIPTION");
            cmd.addParameter("ERR_SYMPTOM",mgr.readValue("ERR_SYMPTOM"));

            trans = mgr.validate(trans); 

            symDesc = trans.getValue("SYMDESC/DATA/SYMPTOMDESCRIPTION");

            txt = (mgr.isEmpty(symDesc) ? "" : (symDesc))+ "^";
            mgr.responseWrite(txt); 
        }
        else if ("PRIORITY_ID".equals(val))
        {
            cmd = trans.addCustomFunction("PRYDESC","MAINTENANCE_PRIORITY_API.Get_Description","PRIORITYDESCRIPTION");
            cmd.addParameter("PRIORITY_ID",mgr.readValue("PRIORITY_ID"));

            trans = mgr.validate(trans); 

            pryDesc = trans.getValue("PRYDESC/DATA/PRIORITYDESCRIPTION");

            txt = (mgr.isEmpty(pryDesc) ? "" : (pryDesc))+ "^";
            mgr.responseWrite(txt); 
        }
        ///----------------------------------------------------------------------------------
        ///-------------------------- Check for date formats --------------------------------
        ///----------------------------------------------------------------------------------
        else if ("REG_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REG_DATE",mgr.readValue("REG_DATE"));

            txt = (mgr.isEmpty(mgr.readValue("REG_DATE")) ? "" : (mgr.readValue("REG_DATE")))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REQUIRED_START_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE"));

            txt = (mgr.isEmpty(mgr.readValue("REQUIRED_START_DATE")) ? "" : (mgr.readValue("REQUIRED_START_DATE")))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REQUIRED_END_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE"));

            txt = (mgr.isEmpty(mgr.readValue("REQUIRED_END_DATE")) ? "" : (mgr.readValue("REQUIRED_END_DATE")))+ "^";

            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

/////----------------------------------------------------------------------------
/////------------------------ GCreated Functions --------------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO<(SELECT Number_Serie_API.Get_Last_Wo_Number FROM DUAL) AND CONTRACT IN(SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
        q.addWhereCondition("CONNECTION_TYPE_DB = 'EQUIPMENT'");
        q.includeMeta("ALL");

        // nimhlk - Begin
        if (mgr.dataTransfered())
        {
            ASPBuffer retBuffer =  mgr.getTransferedData();
            if (retBuffer.itemExists("WO_NO"))
            {
                String ret_wo_no = retBuffer.getValue("WO_NO");
                q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",ret_wo_no);
            }
            else
                q.addOrCondition(mgr.getTransferedData());
        }
        // nimhlk - End

        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATENODATA: No data found."));
            headset.clear();
        }
        if (headset.countRows() == 1)
        {
            lay.setLayoutMode(lay.SINGLE_LAYOUT);
        }
        mgr.createSearchURL(headblk);
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO<(SELECT Number_Serie_API.Get_Last_Wo_Number FROM DUAL) AND CONTRACT IN(SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");   
        q.addWhereCondition("CONNECTION_TYPE_DB = 'EQUIPMENT'");
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        String n = headset.getRow().getValue("N");
        lay.setCountValue(toInt(n));
        headset.clear();
    }

    public void newRow()
    {
        ASPManager mgr = getASPManager();
        String repby=null;

        cmd = trans.addEmptyCommand("HEAD","FAULT_REPORT_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");

        trans.clear();
        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","ISUSER");

        cmd = trans.addCustomFunction("GETREPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("ISUSER","GETUSER/DATA");

        cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addParameter("COMPANY",data.getFieldValue("COMPANY"));
        cmd.addReference("REPORTED_BY","GETREPBY/DATA");

        cmd = trans.addCustomFunction("REPNAME","Employee_API.Get_Employee_Info","NAME");
        cmd.addParameter("COMPANY",data.getFieldValue("COMPANY"));
        cmd.addReference("REPORTED_BY_ID","REPBYID/DATA");

        trans = mgr.perform(trans);
        repby = trans.getValue("GETREPBY/DATA/REPORTED_BY");
        reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
        name = trans.getValue("REPNAME/DATA/NAME");

        data.setValue("REPORTED_BY",repby);
        data.setValue("REPORTED_BY_ID",reportById);
        data.setValue("NAME",name);
        data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");
        headset.addRow(data); 
    }

// Parameter types are not recognized and set them to String. Declare type[s] for (sContract,sMchCode,sMchNameDes)
    public void transferWrite( String sContract,String sMchCode,String sMchNameDes,String sMchCodeCont,String sTestPointId,String sPmNo,String pmDescription,String insNote)  
    {
        ASPManager mgr = getASPManager();
        String repby=null;

        cmd = trans.addEmptyCommand("HEAD","FAULT_REPORT_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");

        trans.clear();
        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","ISUSER");

        cmd = trans.addCustomFunction("GETREPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("ISUSER","GETUSER/DATA");

        cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addParameter("COMPANY",data.getFieldValue("COMPANY"));
        cmd.addReference("REPORTED_BY","GETREPBY/DATA");

        trans = mgr.perform(trans);
        repby = trans.getValue("GETREPBY/DATA/REPORTED_BY");
        reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");

        data.setValue("REPORTED_BY",repby);
        data.setValue("REPORTED_BY_ID",reportById);
        data.setValue("CONTRACT",sContract);
        data.setValue("MCH_CODE",sMchCode);
        data.setValue("MCH_CODE_DESCRIPTION",sMchNameDes);
        data.setValue("MCH_CODE_CONTRACT",sMchCodeCont);
        data.setValue("TEST_POINT_ID",sTestPointId);
        data.setValue("PM_NO",sPmNo);
        data.setValue("PM_DESCR",pmDescription);
        data.setValue("NOTE",insNote);
        //Bug 76003, start
        data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");
        //Bug 76003, end

        headset.addRow(data);
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        String sPpExist = "";

        headset.changeRow();

        //Bug 93061,Start added mch_code_contract as a param
        trans1.clear();
        cmd = trans1.addCustomFunction("PPEXISTS","ACTIVE_SEPARATE_API.Exist_Pre_Postings","PP_EXISTS");
        cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
        cmd.addParameter("MCH_CODE_CONTRACT", headset.getValue("MCH_CODE_CONTRACT"));
        cmd.addParameter("MCH_CODE", headset.getValue("MCH_CODE"));
        trans1 = mgr.perform(trans1);
        sPpExist = trans1.getValue("PPEXISTS/DATA/PP_EXISTS");
        //Bug 93061,End

        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
        
        lay.setLayoutMode(lay.getHistoryMode());

        if ("REMOVED".equals(sPpExist))
            mgr.showAlert("PCMWACTIVESEPARATEPPREMOVED: Existing pre-posting values are not updated");
        else if ("CHANGED".equals(sPpExist))
            bPpChanged = true;
    }

    public void updatePostings()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addCustomCommand("UPDATEPP","ACTIVE_SEPARATE_API.Update_Mch_Pre_Postings");
        cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
        trans = mgr.perform(trans);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void preparefault()
    {
        ASPManager mgr = getASPManager();
        if (lay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        sWoNo = headset.getRow().getValue("WO_NO");
        //rmbPrepFaultFlag = "true";
        mgr.redirectTo("ActiveSeparate2.page?WO_NO="+mgr.URLEncode(sWoNo));
    }

    /*public void sendSMS()
    {

        rmbSendSMSFlag = "true";
    }*/

    // nimhlk - Begin
    public void changeConditionCode()
    {
        ASPManager mgr = getASPManager();
        //itemset.goTo(itemset.getRowSelected());

        ASPCommand cmd;
        ASPBuffer buff,row;
        String sPartNo = "";
        String sSerialNo = "";
        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (lay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        trans.clear();
        //Bug 59613, end
        cmd = trans.addCustomFunction("GETPARTNO","EQUIPMENT_SERIAL_API.GET_PART_NO","PART_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        cmd = trans.addCustomFunction("GETSERIALNO","EQUIPMENT_SERIAL_API.GET_SERIAL_NO","SERIAL_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        trans = mgr.perform(trans);

        sPartNo = trans.getValue("GETPARTNO/DATA/PART_NO");
        sSerialNo = trans.getValue("GETSERIALNO/DATA/SERIAL_NO");

        trans.clear();

        cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
        cmd.addParameter("PART_NO", sPartNo);

        trans = mgr.perform(trans);
         
        if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
        {
            buff = mgr.newASPBuffer();
            row = buff.addBuffer("0");
            row.addItem("PART_NO",sPartNo);
            row.addItem("SERIAL_NO",sSerialNo);

            bOpenNewWindow = true;
            urlString = "../partcw/ChangeConditionCodeDlg.page?" + mgr.getTransferedQueryString(buff);
            newWinHandle = "chgCond"; 

        }
        else{
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATECANNOTPERCHACON: Cannot change the condition code."));
        }
        //Bug 59613, end
    }

    public void  rmbConnectedDocs()
    {
        ASPManager mgr = getASPManager();


        if (lay.isMultirowLayout())
            curr_row = headset.getRowSelected();
        else
            curr_row = headset.getCurrentRowNo();

        headset.goTo(curr_row);

        luname   = "ActiveSeparate";
        wono    = headset.getRow().getValue("WO_NO");


        keyRef   = "WO_NO="+wono+"^";  

        buff=mgr.newASPBuffer();
        row=buff.addBuffer("1");
        row.addItem("LU_NAME",mgr.URLEncode(luname));
        row.addItem("KEY_REF",keyRef);

        mgr.transferDataTo("../docmaw/DocReference.page",buff);
    }
    // nimhlk - End

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(12);
        f.setReadOnly();
        f.setMaxLength(8);
        f.setLabel("PCMWACTIVESEPARATEWONO: WO No");

        f = headblk.addField("REG_DATE","Datetime");
        f.setSize(25);
        f.setReadOnly();
        f.setMandatory();
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setCustomValidation("REG_DATE","REG_DATE");
        f.setLabel("PCMWACTIVESEPARATEREGDATE: Date");


        f = headblk.addField("CONTRACT");
        f.setSize(12);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVESEPARATECONTRACT: WO Site");
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setMandatory();    

        f = headblk.addField("COMPANY");
        f.setSize(20);
        f.setUpperCase();
        f.setMaxLength(20);
        f.setHidden();

        f = headblk.addField("REPORTED_BY");
        f.setSize(10);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATEREPORTEDBY: Reported by");
        f.setMaxLength(10);
        f.setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID,NAME");
        f.setUpperCase();

        f = headblk.addField("REPORTED_BY_ID");
        f.setSize(20);
        f.setUpperCase();
        f.setMaxLength(11);
        f.setHidden();

        f = headblk.addField("NAME");
        f.setSize(40);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATENAME: Name - Internal Phone");
        f.setDefaultNotVisible();
        f.setFunction("Employee_API.Get_Employee_Info(:COMPANY,:REPORTED_BY_ID)");

        f = headblk.addField("ERR_DESCR");
        f.setSize(40);
        f.setMandatory();
        f.setMaxLength(60);
        f.setLabel("PCMWACTIVESEPARATEERRDESCR: Short Descr");

        f = headblk.addField("ORG_CODE");
        f.setSize(10);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setMandatory();
        f.setMaxLength(8); 
        f.setLabel("PCMWACTIVESEPARATEORGCODE: Maintenance Organization");
        f.setUpperCase();
        f.setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCRIPTION");

        f = headblk.addField("ORGCODEDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEORGCODEDESC: Organization Description");   
        f.setDefaultNotVisible();
        f.setFunction("ORGANIZATION_API.Get_Description(:CONTRACT,:ORG_CODE)");              

        f = headblk.addField("MCH_CODE");
        f.setSize(20);
        f.setLabel("PCMWACTIVESEPARATEMCHCODE: Object ID");
        f.setUpperCase();
        f.setMaxLength(100);
        f.setCustomValidation("MCH_CODE","MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT");

        f = headblk.addField("MCH_CODE_DESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setFunction("''");
        f.setLabel("PCMWACTIVESEPARATEMCHCODEDESCRIPTION: Object Description");
        f.setDefaultNotVisible();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(12);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVESEPARATEMCHCODECONTRACT: Site");
        f.setUpperCase();


        f = headblk.addField("ERR_DISCOVER_CODE");
        f.setSize(4);
        f.setMaxLength(3);
        f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
        f.setLabel("PCMWACTIVESEPARATEERRDISCOVERCODE: Discovery Code");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setCustomValidation("ERR_DISCOVER_CODE","DISCOVERDESCRIPTION");   

        f = headblk.addField("DISCOVERDESCRIPTION");
        f.setSize(32);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEDISCDESC: Discovery Description"); 
        f.setDefaultNotVisible();
        f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");

        f = headblk.addField("ERR_SYMPTOM");
        f.setSize(6);
        f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
        f.setLabel("PCMWACTIVESEPARATEERRSYMPTOM: Symptom");
        f.setMaxLength(3);
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setCustomValidation("ERR_SYMPTOM","SYMPTOMDESCRIPTION");

        f = headblk.addField("SYMPTOMDESCRIPTION");
        f.setSize(34);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATESYMPTDESC: Symptom Description");  
        f.setDefaultNotVisible();
        f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");

        f = headblk.addField("PRIORITY_ID");
        f.setSize(4);
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);
        f.setLabel("PCMWACTIVESEPARATEPRIORITYID: Priority");
        f.setMaxLength(1);
        f.setUpperCase();
        f.setCustomValidation("PRIORITY_ID","PRIORITYDESCRIPTION");

        f = headblk.addField("PRIORITYDESCRIPTION");
        f.setSize(32);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEPRIODESC: Priority Description");   
        f.setDefaultNotVisible();
        f.setFunction("MAINTENANCE_PRIORITY_API.Get_Description(:PRIORITY_ID)");

        f = headblk.addField("REQUIRED_START_DATE","Datetime");
        f.setSize(25);
        f.setCustomValidation("REQUIRED_START_DATE","REQUIRED_START_DATE");
        f.setLabel("PCMWACTIVESEPARATEREQSDATE: Required Start");

        f = headblk.addField("REQUIRED_END_DATE","Datetime");
        f.setSize(25);
        f.setCustomValidation("REQUIRED_END_DATE","REQUIRED_END_DATE");
        f.setLabel("PCMWACTIVESEPARATEREQEDATE: Latest Completion");

        f = headblk.addField("ERR_DESCR_LO");
        f.setSize(49);
        f.setHeight(3);
        f.setMaxLength(2000);
        f.setDefaultNotVisible ();
        f.setLabel("PCMWACTIVESEPARATEERRDESCRLO: Fault Descr");                

        f = headblk.addField("PM_TYPE");
        f.setSize(20);
        f.setHidden();
        f.setMaxLength(20);

        f = headblk.addField("ISUSER");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CONNECTION_TYPE_DB");
        f.setHidden();
        f.setSize(10);

        f = headblk.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("COND_CODE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("COND_CODE_DESC");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PART_NO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PART_DESC");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("LOT_BATCH_NO");
        f.setFunction("''");
        f.setHidden();
        
         f = headblk.addField("TEST_POINT_ID");
         f.setHidden();
         

         f = headblk.addField("PM_DESCR");
         f.setHidden();

         f = headblk.addField("NOTE");
         f.setHidden();

         f = headblk.addField("PM_NO","Number");
         f.setHidden();
         
         f = headblk.addField("PP_EXISTS"); 
         f.setHidden();  
         f.setFunction("''");

        headblk.setView("FAULT_REPORT");
        headblk.defineCommand("FAULT_REPORT_API","New__,Modify__,Remove__");
        headblk.disableDocMan();
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATEHD: Fault Report"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("rmbConnectedDocs",mgr.translate("PCMWACTIVESEPARATEDOCS: Documents"));

        headbar.addCustomCommand("preparefault",mgr.translate("PCMWACTIVESEPARATEPREPA: Prepare..."));
        //headbar.addCustomCommand("sendSMS",mgr.translate("PCMWACTIVESEPARATESNDSMS: Send SMS..."));
        headbar.addCustomCommand("changeConditionCode",mgr.translate("PCMWACTIVESEPARATECHANGECONDITION: Change of Condition Code..."));

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
        headbar.defineCommand(headbar.SAVENEW,null);

        head_command = headbar.getSelectedCustomCommand();

        lay = headblk.getASPBlockLayout();
        lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
        lay.defineGroup(" ","WO_NO,REG_DATE,CONTRACT",false,true);
        lay.defineGroup(mgr.translate("PCMWACTIVESEPARATEGRPLABEL1: Mandatory"),"REPORTED_BY,NAME,ERR_DESCR,ORG_CODE,ORGCODEDESCRIPTION",true,true);
        lay.defineGroup(mgr.translate("PCMWACTIVESEPARATEGRPLABEL2: Planning Schedule"),"REQUIRED_START_DATE,REQUIRED_END_DATE",true,true);   
        lay.defineGroup(mgr.translate("PCMWACTIVESEPARATEGRPLABEL3: More Information about the Fault"),"MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,ERR_DESCR_LO,ERR_DISCOVER_CODE,DISCOVERDESCRIPTION,ERR_SYMPTOM,SYMPTOMDESCRIPTION,PRIORITY_ID,PRIORITYDESCRIPTION",true,true);

        lay.setSimple("ORGCODEDESCRIPTION");
        lay.setSimple("MCH_CODE_DESCRIPTION");
        lay.setSimple("DISCOVERDESCRIPTION");
        lay.setSimple("SYMPTOMDESCRIPTION");
        lay.setSimple("PRIORITYDESCRIPTION");
    }

    public void getDefaults()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addCustomFunction( "GETCON","User_Default_API.Get_Contract","CONTRACT" );

        cmd = trans.addCustomFunction( "GETCOM","Site_API.Get_Company","COMPANY" );
        cmd.addReference("CONTRACT","GETCON/DATA");
        trans = mgr.perform(trans);
        String sCompany = trans.getValue("GETCOM/DATA/COMPANY");
        mgr.getASPField("REPORTED_BY").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        trans.clear();
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
            mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
        if ((notTransferd=true) && (lay.isFindLayout()))
        {
            getDefaults();
            mgr.getASPField("REPORTED_BY").setDynamicLOV("EMPLOYEE_LOV",600,450);
        }
        else
        {
            getDefaults();
            mgr.getASPField("REPORTED_BY").setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);     
        }
	if (lay.isFindLayout()) {
	    mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLov2.page","MCH_CODE",600,450);
        }
        //Bug 72539, start
	else{
	    mgr.getASPField("MCH_CODE").setLOV("MaintObjectLov.page","MCH_CODE_CONTRACT",600,450)
				       .setLOVProperty("WHERE","OPERATIONAL_STATUS_DB != 'SCRAPPED'AND(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");
	}
        //Bug 72539, end

        if (headset.countRows() == 0)
        {
            headbar.disableCommandGroup(headbar.CMD_GROUP_CUSTOM);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.removeCustomCommand("changeConditionCode");
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVESEPARATETITLE: Fault Report";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATETITLE: Fault Report";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("PRPOSTCHANGED", "");

        appendToHTML(lay.show());

        appendDirtyJavaScript("window.name = \"frmFaultReport\";\n");

        /*appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(rmbSendSMSFlag);
        appendDirtyJavaScript("'=='true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    window.open('");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+\"common/scripts/SendSMSMessage.page\",\"frmSendSMS\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(rmbPrepFaultFlag);
        appendDirtyJavaScript("'=='true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    window.open('");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+\"pcmw/ActiveSeparate2.page?WO_NO=\"+'");
        appendDirtyJavaScript(sWoNo);
        appendDirtyJavaScript("',\"frmPrepareFR\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
        appendDirtyJavaScript("}\n");*/

        // XSS_Safe ILSOLK 20070705
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }

        if (bPpChanged)
        {
            bPpChanged = false;
            appendDirtyJavaScript("if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPEXTPP: Do you want to replace existing pre-postings?"));
            appendDirtyJavaScript("\")) {\n");
            appendDirtyJavaScript("	  document.form.PRPOSTCHANGED.value = \"TRUE\";\n");
            appendDirtyJavaScript("       f.submit();\n");
            appendDirtyJavaScript("     } \n");
        }
    }
}
