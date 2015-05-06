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
*  File        : CreateWorkOrderWiz.java 
*  Modified    :
*  ARWILK  030915  Created.
*                  (IID GEAM232: Work Order/ Customer Order).
*  ARWILK  031103  (Bug#109891). Check method comments.
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  DIMALK  040120  Replaced the calls to package Active_Separate1_API with Active_Separate_API
*  ARWILK  040226  Edge Developments - (Remove uneccessary global variables, Enhance Performance)
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  BUNILK  040830  Renamed wizard title and information text.
*  BUNILK  040923  Modified finishWizForm() method and added validation to Reported By column. 
*  ARWILK  041001  LCS Merge:46394
*  NIJALK  050906  Bug 126465: Modified getContents().
*  SHAFLK  060207  LCS Merge: 54784.
*  SHAFLK  060208  LCS Merge: 55139.
*  SHAFLK  060209  Call 133284, Modified Validation(), preDefine() and adjust() methods.
*  NIJALK  060224  Call 135452: Renamed Tags.
*  NIJALK  060224  Call 135453: Modified preDefine().
*  ASSAKL  070130  Wings merge. (ASSALK  061116  Call 128333: Modified preDefine(). Added contract also to the Aggreement ID filteration. / ASSALK  070101  Issue 129258. Modified getContents. Modified java script function __setDate(r)).
*  HARPLK  070301  Call 141302, Modified preDefine(). 
*  HARPLK  070302  Call 141262, Modified getContents().
*  ARWILK  070307  Call 141263, Modified getContents().
*  HARPLK  070318  Call 141544, Modified preDefine(),validate(),getContents().
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070404  Modified Predefine() to remove the extra line shown at runtime
*  CHCRLK  070516  Call ID 142228, Modified method initializeWizForm() to fetch the order type 
*                  from the customer's default
*  ILSOLK  070713  Eliminated XSS.
*  ASSALK  070924  Quick correction of script error, modified validate().
*  SHAFLK  080626  Bug 75375, Modified getContents().   
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

// 031103  ARWILK  Begin  (Bug#109891)
import java.util.*;
// 031103  ARWILK  End  (Bug#109891)

public class CreateWorkOrderWiz extends ASPPageProvider {
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateWorkOrderWiz");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock blkOne;
    private ASPRowSet rowSetOne;
    private ASPTable tblOne;
    private ASPCommandBar barOne;
    private ASPBlockLayout layOne;

    private ASPBlock blkTwo;
    private ASPRowSet rowSetTwo;
    private ASPTable tblTwo;
    private ASPCommandBar barTwo;
    private ASPBlockLayout layTwo;

    private ASPBlock blkDummy;
    private ASPRowSet rowSetDummy;
    private ASPTable tblDummy;
    private ASPCommandBar barDummy;
    private ASPBlockLayout layDummy;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer secBuff;
    private ASPBuffer data;
    private ASPCommand cmd;

    private int wizPageNo;
    boolean bFinishPerformed;
    boolean bCancelPerformed;

    String sTagForFirstRadioButton;
    String sTagForSecondRadioButton;
    String sTagForThirdRadioButton;
    String sTagForFinishButton;

    String sRequiredStartDate;
    String sRequiredEndDate;

    // 031103  ARWILK  Begin  (Bug#109891)  
    Vector vAlertMsgQueue;
    // 031103  ARWILK  End  (Bug#109891)
    private ASPBuffer row;
    String  sContract;
    private ASPBuffer buf;
    //===============================================================
    // Construction 
    //===============================================================
    public CreateWorkOrderWiz(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();  

        trans = mgr.newASPTransactionBuffer(); 
        secBuff = mgr.newASPTransactionBuffer(); 
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        wizPageNo = ctx.readNumber("CTXWIZPAGENO", 1);
        sRequiredStartDate = ctx.readValue("CTXWIZREQSTARTDATE", "");
        sRequiredEndDate = ctx.readValue("CTXWIZREQENDDATE", "");
        sContract =  ctx.readValue("CTXCONTRACT", "");   

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.buttonPressed("CANCEL"))
            cancelWizForm();
        else if (mgr.buttonPressed("PREVIOUS"))
            previousWizPage();
        else if ((mgr.buttonPressed("NEXT")) || (!mgr.isEmpty(mgr.readValue("FINISH_APPROOVED",""))))
            nextWizPage();
        else if (mgr.buttonPressed("FINISH"))
            finishWizForm();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("INI_CUSTOMER_ID"))) {
            wizPageNo = 1;
            initializeWizForm();
        }

        adjust();

        ctx.writeNumber("CTXWIZPAGENO", wizPageNo);
        ctx.writeValue("CTXWIZREQSTARTDATE", sRequiredStartDate);
        ctx.writeValue("CTXWIZREQENDDATE", sRequiredEndDate);
        ctx.writeValue("CTXCONTRACT", sContract);
    }

    //-----------------------------------------------------------------------------
    //-----------------------------  VALIDATE FUNCTION  ---------------------------
    //-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        String txt = null;

        if ("PART_NO".equals(val)) {
            String sPartNo = "";
            String sSerialNo = "";
            String sPartDesc = "";

            trans.clear();

            if (mgr.readValue("PART_NO").indexOf("~") > -1) {
                sPartNo = mgr.readValue("PART_NO").substring(0,mgr.readValue("PART_NO").indexOf("~"));       
                sSerialNo = mgr.readValue("PART_NO").substring(mgr.readValue("PART_NO").indexOf("~") + 1, mgr.readValue("PART_NO").length());
            } else {
                sPartNo = mgr.readValue("PART_NO");
                sSerialNo = mgr.readValue("SERIAL_NO");
            }

            if (!mgr.isEmpty(sPartNo)) {
                cmd = trans.addCustomFunction("GETDESC","Part_Catalog_API.Get_Description","PART_NAME");
                cmd.addParameter("PART_NO", sPartNo);

                trans = mgr.validate(trans);

                sPartDesc = trans.getValue("GETDESC/DATA/PART_NAME");
            }

            // Reset Vehicle ID since a different part number has been selected

            txt = (mgr.isEmpty(sPartNo) ? "" :sPartNo) + "^" + 
                  (mgr.isEmpty(sPartDesc) ? "" :sPartDesc) + "^" + 
                  (mgr.isEmpty(sSerialNo) ? "" :sSerialNo) + "^";

            mgr.responseWrite(txt);
        }

        if ("SERIAL_NO".equals(val)) {
            String sPartNo = "";
            String sSerialNo = "";
            String sPartDesc = "";

            trans.clear();

            if (mgr.readValue("SERIAL_NO").indexOf("~") > -1) {
                sPartNo = mgr.readValue("SERIAL_NO").substring(0,mgr.readValue("SERIAL_NO").indexOf("~"));       
                sSerialNo = mgr.readValue("SERIAL_NO").substring(mgr.readValue("SERIAL_NO").indexOf("~") + 1, mgr.readValue("SERIAL_NO").length());
            } else {
                sPartNo = mgr.readValue("PART_NO");
                sSerialNo = mgr.readValue("SERIAL_NO");
            }

            if (!mgr.isEmpty(sPartNo)) {
                cmd = trans.addCustomFunction("GETDESC","Part_Catalog_API.Get_Description","PART_NAME");
                cmd.addParameter("PART_NO", sPartNo);

                trans = mgr.validate(trans);

                sPartDesc = trans.getValue("GETDESC/DATA/PART_NAME");
            }

            // Reset Vehicle ID since a different part number has been selected

            txt = (mgr.isEmpty(sPartNo) ? "" :sPartNo) + "^" + 
                  (mgr.isEmpty(sPartDesc) ? "" :sPartDesc) + "^" + 
                  (mgr.isEmpty(sSerialNo) ? "" :sSerialNo) + "^";

            mgr.responseWrite(txt);
        }
        // 031103  ARWILK  Begin  (Bug#109891)

        else if ("VALIDATE_SERIAL".equals(val)) {
            String sTransMsg = "";
            String sExists = "";
            trans.clear();

            trans.addSecurityQuery("VIM_SERIAL_API","Check_Exist");

            if (trans.getSecurityInfo().itemExists("VIM_SERIAL_API.Check_Exist")) {
                trans.clear();                        

                cmd = trans.addCustomFunction("VALIDATESERIALNO","VIM_SERIAL_API.Check_Exist","DUMMY");
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));

                trans = mgr.perform(trans);
                sExists = trans.getValue("VALIDATESERIALNO/DATA/DUMMY");

                if (!mgr.isEmpty(sExists) && sExists.equals("FALSE")) {
                    sTransMsg = mgr.translate("PCMWCREATEWOWIZSERIALNOMSG: Serial &1 does not exist for part &2 .", mgr.readValue("SERIAL_NO"), mgr.readValue("PART_NO"));
                }
                
            }

            if (!mgr.isEmpty(sTransMsg)) 
               txt = (mgr.isEmpty(sTransMsg)?"":("No_Data_Found" + sTransMsg));
            else
               txt = "^^";

            mgr.responseWrite(txt);  

        }



        else if ("NO_OF_OBJ".equals(val)) {
            String sObjectName = "";
            int nNoOfObjects = 0;
            String sCustomerConfirmMsg = "";


            trans.clear();


            trans.addSecurityQuery("EQUIPMENT_SERIAL_API","Concatenate_Object");     
            trans.addSecurityQuery("ACTIVE_WORK_ORDER_API","Get_Wo_Per_Object");   

            trans = mgr.perform(trans);


            if (trans.getSecurityInfo().itemExists("EQUIPMENT_SERIAL_API.Concatenate_Object")
                && trans.getSecurityInfo().itemExists("ACTIVE_WORK_ORDER_API.Get_Wo_Per_Object")) {

                trans.clear();

                cmd = trans.addCustomCommand("GETOBJNAME", "Equipment_Serial_API.Concatenate_Object");
                cmd.addParameter("OBJ_NAME", "");
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("GETNOFOBJ", "Active_Work_Order_API.Get_Wo_Per_Object", "NO_OF_OBJ");
                cmd.addReference("OBJ_NAME", "GETOBJNAME/DATA");

                trans = mgr.perform(trans);

                sObjectName = trans.getValue("GETOBJNAME/DATA/OBJ_NAME");
                nNoOfObjects = Integer.parseInt(mgr.isEmpty(trans.getValue("GETNOFOBJ/DATA/NO_OF_OBJ"))?"0":trans.getValue("GETNOFOBJ/DATA/NO_OF_OBJ"));

                sCustomerConfirmMsg = mgr.translate("CUSTVISITWIZWORKORDEXIST: Part number &1 and Serial &2 already exist on &3 active work order(s).", mgr.readValue("PART_NO"), mgr.readValue("SERIAL_NO"), Integer.toString(nNoOfObjects));
            }

            txt = nNoOfObjects + "^" +
                  (mgr.isEmpty(sCustomerConfirmMsg)?"":sCustomerConfirmMsg) + "^";

            mgr.responseWrite(txt);
        } else if ("ORG_CODE".equals(val)) {
            trans.clear();

            cmd = trans.addCustomFunction("GETDESC", "Organization_API.Get_Description", "ORG_DESC");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.perform(trans);

            String sOrgDesc = trans.getValue("GETDESC/DATA/ORG_DESC");

            txt = (mgr.isEmpty(sOrgDesc)?"":sOrgDesc) + "^";

            mgr.responseWrite(txt);
        } else if ("REPORTED_BY".equals(val)) {
            cmd = trans.addCustomFunction("MAXEMP", "Company_Emp_API.Get_Max_Employee_Id", "REPORTED_BY");
            cmd.addParameter("COMPANY");
            cmd.addParameter("REPORTED_BY");

            trans = mgr.validate(trans);

            String sReportedById = trans.getValue("MAXEMP/DATA/REPORTED_BY");
            txt = (mgr.isEmpty(sReportedById)?"":sReportedById) + "^";
            mgr.responseWrite(txt);
        }
        //Bug 54784, start
        if ("STRU_EXIST".equals(val)) {
            trans.clear();

            cmd = trans.addCustomFunction("GETEXIST","Active_Separate_API.Exist_In_Vim_Structure","STRU_EXIST");
            cmd.addParameter("COMPLEX_AGREEMENT_ID",mgr.readValue("COMPLEX_AGREEMENT_ID"));
            cmd.addParameter("ITEM2_CONTRACT",mgr.readValue("ITEM2_CONTRACT"));
            cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
            cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));

            trans = mgr.perform(trans);

            String sExist = trans.getValue("GETEXIST/DATA/STRU_EXIST");

            String sVinStrucMsg = mgr.translate("PCMWCREATEWOWIZNOSTRUCEXIST: This part revision is not present in the agreement structure. Do you want to continue?");

            txt = sExist + "^" +
                  (mgr.isEmpty(sVinStrucMsg)?"":sVinStrucMsg) + "^";

            mgr.responseWrite(txt);
        }

        //Bug 54784, end

        //Bug 55139, start
        if ("STRU_TRANS".equals(val)) {
            trans.clear();

            cmd = trans.addCustomFunction("GETTRANS","Active_Separate_API.Transfered_Vim_Structure","STRU_TRANS");
            cmd.addParameter("ITEM2_CONTRACT",mgr.readValue("ITEM2_CONTRACT"));
            cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
            cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));

            trans = mgr.perform(trans);

            String sTrans = trans.getValue("GETTRANS/DATA/STRU_TRANS");

            String sTransMsg = mgr.translate("PCMWCREATEWOWIZNOSTRUCTRANS: The template structure has not been transferred to the site. Do you want to continue?");

            txt = sTrans + "^" +
                  (mgr.isEmpty(sTransMsg)?"":sTransMsg) + "^";

            mgr.responseWrite(txt);
        }
        //Bug 55139, end

        mgr.endResponse();
    }

    //=============================================================================
    //  User Defined Functions
    //=============================================================================

    public void cancelWizForm()
    {
        ASPManager mgr = getASPManager();

        bCancelPerformed = true;
    }

    public void previousWizPage()
    {
        ASPManager mgr = getASPManager();

        if (wizPageNo == 1)
            return;
        else if (wizPageNo == 2)
            rowSetTwo.changeRow();

        wizPageNo -= 1;
    }

    public void nextWizPage()
    {
        ASPManager mgr = getASPManager();

        if (wizPageNo == 3)
            return;
        else if (wizPageNo == 1) {
            sRequiredStartDate = mgr.readValue("REQUIRED_START_DATE");
            sRequiredEndDate = mgr.readValue("REQUIRED_END_DATE");

            //Bug 54784, start
            sContract =  mgr.readValue("CONTRACT");
            rowSetOne.changeRow();

            row = rowSetTwo.getRow();
            row.setValue("CONTRACT",sContract);
            rowSetTwo.setRow(row);
            //Bug 54784, end
        } else if (wizPageNo == 2) {
            rowSetTwo.changeRow();
        }

        wizPageNo += 1;

        setUpRadioButtonTags();
    }

    public void finishWizForm()
    {
        ASPManager mgr = getASPManager();

        String sWoNo = null;
        String sDoNo = null;
        String sRoNo = null;

        String sCreateWo = null;
        String sCreateWoDo = null;
        String sCreateWoDoRo = null;

        boolean bAtLeastOneOptionSelected = false;
        boolean bInfoShownPreviously = false;

        bFinishPerformed = false;

        if ("CREATE_WO".equals(mgr.readValue("SELACTPERFOPTION"))) {
            sCreateWo = "TRUE";
            sCreateWoDo = "FALSE";
            sCreateWoDoRo = "FALSE";

            bAtLeastOneOptionSelected = true;
        } else if ("CREATE_WO_DO".equals(mgr.readValue("SELACTPERFOPTION"))) {
            sCreateWo = "TRUE";
            sCreateWoDo = "TRUE";
            sCreateWoDoRo = "FALSE";

            bAtLeastOneOptionSelected = true;
        } else if ("CREATE_WO_DO_RO".equals(mgr.readValue("SELACTPERFOPTION"))) {
            sCreateWo = "TRUE";
            sCreateWoDo = "TRUE";
            sCreateWoDoRo = "TRUE";

            bAtLeastOneOptionSelected = true;
        }

        if (bAtLeastOneOptionSelected) {
            trans.clear();

            ASPBuffer firstRowSetContents = rowSetOne.getRow();
            ASPBuffer secondRowSetContents = rowSetTwo.getRow();

            cmd = trans.addCustomCommand("CREAEMROORD","Active_Separate_API.Create_Mro_Orders");
            cmd.addParameter("WO_NO", sWoNo);
            cmd.addParameter("DO_NO", sDoNo);
            cmd.addParameter("RO_NO", sRoNo);
            cmd.addParameter("CREATE_WO_NO", sCreateWo);
            cmd.addParameter("CREATE_DO_NO", sCreateWoDo);
            cmd.addParameter("CREATE_RO_NO", sCreateWoDoRo);

            cmd.addParameter("REPORTED_BY_ID", firstRowSetContents.getValue("REPORTED_BY_ID"));
            cmd.addParameter("CONTRACT", firstRowSetContents.getValue("CONTRACT"));

            cmd.addParameter("CONNECTION_TYPE", firstRowSetContents.getValue("CONNECTION_TYPE"));
            cmd.addParameter("ERR_DESCR", firstRowSetContents.getValue("ERR_DESCR"));

            cmd.addParameter("ORG_CODE", firstRowSetContents.getValue("ORG_CODE"));
            cmd.addParameter("PART_NO", secondRowSetContents.getValue("PART_NO"));

            cmd.addParameter("SERIAL_NO", secondRowSetContents.getValue("SERIAL_NO"));
            cmd.addParameter("CUSTOMER_NO", secondRowSetContents.getValue("CUSTOMER_NO"));

            cmd.addParameter("COMPLEX_AGREEMENT_ID", secondRowSetContents.getValue("COMPLEX_AGREEMENT_ID"));
            cmd.addParameter("CUST_ORDER_TYPE", secondRowSetContents.getValue("CUST_ORDER_TYPE"));

            cmd.addParameter("AUTHORIZE_CODE", secondRowSetContents.getValue("AUTHORIZE_CODE"));

            cmd.addParameter("REQUIRED_START_DATE", sRequiredStartDate);
            cmd.addParameter("REQUIRED_END_DATE", sRequiredEndDate);

            trans = mgr.perform(trans);

            sWoNo = trans.getValue("CREAEMROORD/DATA/WO_NO");
            sDoNo = trans.getValue("CREAEMROORD/DATA/DO_NO");
            sRoNo = trans.getValue("CREAEMROORD/DATA/RO_NO");

            // 031103  ARWILK  Begin  (Bug#109891)
            // mgr.showAlert was removed because there was a request to pop up the messages separately.

            vAlertMsgQueue = new Vector(1,1);

            if ("TRUE".equals(sCreateWo) && (!mgr.isEmpty(sWoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTWOCREATED: Work Order &1 has been created.", sWoNo));
            } else if ("TRUE".equals(sCreateWo) && (mgr.isEmpty(sWoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTNOWOCREATED: Work Order was not created."));
            }

            if ("TRUE".equals(sCreateWoDo) && (!mgr.isEmpty(sDoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTDOCREATED: Dispatch Order &1 has been created.", sDoNo));
            } else if ("TRUE".equals(sCreateWoDo) && (mgr.isEmpty(sDoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTNODOCREATED: Dispatch Order was not created."));
            }

            if ("TRUE".equals(sCreateWoDoRo) && (!mgr.isEmpty(sRoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTROCREATED: Receive Order &1 has been created.", sRoNo));
            } else if ("TRUE".equals(sCreateWoDoRo) && (mgr.isEmpty(sRoNo))) {
                bInfoShownPreviously = true;
                vAlertMsgQueue.add(mgr.translate("PCMWCREATEWOWIZVECTNOROCREATED: Receive Order was not Created."));
            }

            if (!bInfoShownPreviously) {
                if ("TRUE".equals(sCreateWo) && "TRUE".equals(sCreateWoDo) && "FALSE".equals(sCreateWoDoRo))
                    mgr.showAlert(mgr.translate("PCMWCREATEWOWIZVECTNOWODOCREATED: Work Order and Dispatch Order was not created."));
                else if ("TRUE".equals(sCreateWo) && "TRUE".equals(sCreateWoDo) && "TRUE".equals(sCreateWoDoRo))
                    mgr.showAlert(mgr.translate("PCMWCREATEWOWIZVECTNOWODOROCREATED: Work Order, Dispatch Order and Receive Order was not created."));
            }

            // 031103  ARWILK  End  (Bug#109891)

            bFinishPerformed = true;
        } else {
            setUpRadioButtonTags();
            mgr.showAlert(mgr.translate("PCMWCREATEWOWIZVECTCANNOTPERF: Cannot perform finish. At least one option should be selected."));
            bFinishPerformed = false;
        }
    }

    public void initializeWizForm()
    {
        ASPManager mgr = getASPManager();

        String sCustCS = "2";
        boolean bCreditCheckCanGoAhead = false;

        secBuff.clear();

        secBuff.addSecurityQuery("Cust_Ord_Customer_API","Customer_Is_Credit_Stopped");
        secBuff = mgr.perform(secBuff); 

        if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer_API.Customer_Is_Credit_Stopped"))
            bCreditCheckCanGoAhead = true;

        // Setting values to rowset one.

        trans.clear();

        cmd = trans.addEmptyCommand("BLKONE","ACTIVE_SEPARATE_API.New__", blkOne);
        cmd.setOption("ACTION","PREPARE");
        cmd.addParameter("CUSTOMER_NO", mgr.readValue("INI_CUSTOMER_ID"));

        cmd = trans.addCustomFunction("GETCONNTYPE","MAINT_CONNECTION_TYPE_API.Decode","CONNECTION_TYPE");
        cmd.addParameter("CO_TYPE", "VIM");

        cmd = trans.addCustomFunction("GETCUSTORDERTYPE","Cust_Ord_Customer_API.Get_Order_Id","CUST_ORDER_TYPE");
        cmd.addParameter("CUSTOMER_NO", mgr.readValue("INI_CUSTOMER_ID"));

        if (bCreditCheckCanGoAhead) {
            cmd = trans.addCustomFunction("GETISCUSTCREASTOP", "Cust_Ord_Customer_API.Customer_Is_Credit_Stopped", "IS_CREDIT_STOPPED");
            cmd.addReference("CUSTOMER_NO", "BLKONE/DATA");
            cmd.addReference("COMPANY", "BLKONE/DATA");
        }

        trans = mgr.perform(trans);

        String custOrderType =  trans.getValue("GETCUSTORDERTYPE/DATA/CUST_ORDER_TYPE");

        if (mgr.isEmpty(custOrderType))
            custOrderType = "NO";

        data = trans.getBuffer("BLKONE/DATA");
        data.setValue("CONNECTION_TYPE", trans.getValue("GETCONNTYPE/DATA/CONNECTION_TYPE"));

        if (bCreditCheckCanGoAhead)
            sCustCS = trans.getValue("GETISCUSTCREASTOP/DATA/IS_CREDIT_STOPPED");

        if ("1".equals(sCustCS))
            mgr.showAlert(mgr.translate("PCMWCREATEWOWIZ: Customer is Credit blocked."));

        rowSetOne.addRow(data);

        // Setting values to rowset two.

        trans.clear();

        cmd = trans.addEmptyCommand("BLKTWO","ACTIVE_SEPARATE_API.New__", blkTwo);
        cmd.setOption("ACTION","PREPARE");
        cmd.addParameter("CUSTOMER_NO", mgr.readValue("INI_CUSTOMER_ID"));
        trans = mgr.perform(trans);

        data = trans.getBuffer("BLKTWO/DATA");

        data.setValue("CUST_ORDER_TYPE", custOrderType);
        data.setValue("CUSTOMER_NO", mgr.readValue("INI_CUSTOMER_ID"));

        rowSetTwo.addRow(data);
    }

    public void setFieldAccessibility()
    {
        ASPManager mgr = getASPManager();

        if (wizPageNo == 1) {

            if ("VIM".equals(rowSetOne.getRow().getValue("CONNECTION_TYPE")))
                mgr.getASPField("CONNECTION_TYPE").setReadOnly();
            else
                mgr.getASPField("CONNECTION_TYPE").unsetReadOnly(); 
        }
    }

    public void setUpRadioButtonTags()
    {
        ASPManager mgr = getASPManager();

        sTagForFinishButton = "DISABLED";

        if (wizPageNo == 3) {
            String sIdentityType = "";
            String sPartyType = "";
            String sClientValCust = "1";
            boolean securityIsOk;

            ASPBuffer firstRowSetContents = rowSetOne.getRow();
            ASPBuffer secondRowSetContents = rowSetTwo.getRow();

            if (mgr.isEmpty(firstRowSetContents.getValue("REPORTED_BY")) || mgr.isEmpty(firstRowSetContents.getValue("CONTRACT")) || mgr.isEmpty(secondRowSetContents.getValue("CUSTOMER_NO")) || 
                mgr.isEmpty(firstRowSetContents.getValue("ERR_DESCR")) || mgr.isEmpty(firstRowSetContents.getValue("ORG_CODE")) || mgr.isEmpty(firstRowSetContents.getValue("CONNECTION_TYPE")) ||
                mgr.isEmpty(secondRowSetContents.getValue("PART_NO")) || mgr.isEmpty(secondRowSetContents.getValue("SERIAL_NO")) || mgr.isEmpty(secondRowSetContents.getValue("COMPLEX_AGREEMENT_ID"))) {
                sTagForFirstRadioButton = "DISABLED";
            } else {
                sTagForFirstRadioButton = "";
                sTagForFinishButton = "";
            }

            securityIsOk = false;

            secBuff.clear();

            secBuff.addSecurityQuery("Party_Type_API","Get_Client_Value");
            secBuff.addSecurityQuery("Identity_Invoice_Info_API","Get_Identity_Type");
            secBuff.addSecurityQuery("Site_API","Get_Company");
            secBuff = mgr.perform(secBuff); 

            if ((secBuff.getSecurityInfo().itemExists("Party_Type_API.Get_Client_Value")) 
                && (secBuff.getSecurityInfo().itemExists("Identity_Invoice_Info_API.Get_Identity_Type"))
                && (secBuff.getSecurityInfo().itemExists("Site_API.Get_Company")))
                securityIsOk = true;

            trans.clear();

            if (securityIsOk) {
                cmd = trans.addCustomFunction("GETPARTTYPE","Party_Type_API.Get_Client_Value","PARTY_TYPE");
                cmd.addParameter("CLIENT_VAL_CUST", sClientValCust);

                cmd = trans.addCustomFunction("GETSITEVAL","Site_API.Get_Company","COMPANY");
                cmd.addParameter("CONTRACT", firstRowSetContents.getValue("CONTRACT"));

                cmd = trans.addCustomFunction("GETIDVAL","Identity_Invoice_Info_API.Get_Identity_Type","IDENTITY_TYPE");
                cmd.addReference(  "COMPANY","GETSITEVAL/DATA");
                cmd.addParameter("CUSTOMER_NO", secondRowSetContents.getValue("CUSTOMER_NO"));
                cmd.addReference("PARTY_TYPE","GETPARTTYPE/DATA");

                trans = mgr.perform(trans);

                sIdentityType = trans.getValue("GETIDVAL/DATA/IDENTITY_TYPE");
            }

            if (mgr.isEmpty(firstRowSetContents.getValue("REPORTED_BY")) || mgr.isEmpty(firstRowSetContents.getValue("CONTRACT")) || mgr.isEmpty(secondRowSetContents.getValue("CUSTOMER_NO")) || 
                mgr.isEmpty(firstRowSetContents.getValue("ERR_DESCR")) || mgr.isEmpty(firstRowSetContents.getValue("ORG_CODE")) || mgr.isEmpty(firstRowSetContents.getValue("CONNECTION_TYPE")) ||
                mgr.isEmpty(secondRowSetContents.getValue("PART_NO")) || mgr.isEmpty(secondRowSetContents.getValue("SERIAL_NO")) || mgr.isEmpty(secondRowSetContents.getValue("CUST_ORDER_TYPE"))||
                mgr.isEmpty(secondRowSetContents.getValue("AUTHORIZE_CODE")) || mgr.isEmpty(secondRowSetContents.getValue("COMPLEX_AGREEMENT_ID")) || !("EXTERN".equals(sIdentityType))) {
                sTagForSecondRadioButton = "DISABLED";
            } else {
                sTagForSecondRadioButton    = "";
                sTagForFinishButton = "";
            }

            if (mgr.isEmpty(firstRowSetContents.getValue("REPORTED_BY")) || mgr.isEmpty(firstRowSetContents.getValue("CONTRACT")) || mgr.isEmpty(secondRowSetContents.getValue("CUSTOMER_NO")) || 
                mgr.isEmpty(firstRowSetContents.getValue("ERR_DESCR")) || mgr.isEmpty(firstRowSetContents.getValue("ORG_CODE")) || mgr.isEmpty(firstRowSetContents.getValue("CONNECTION_TYPE")) ||
                mgr.isEmpty(secondRowSetContents.getValue("PART_NO")) || mgr.isEmpty(secondRowSetContents.getValue("SERIAL_NO")) || mgr.isEmpty(secondRowSetContents.getValue("CUST_ORDER_TYPE"))||
                mgr.isEmpty(secondRowSetContents.getValue("AUTHORIZE_CODE")) || mgr.isEmpty(firstRowSetContents.getValue("REQUIRED_START_DATE")) || mgr.isEmpty(secondRowSetContents.getValue("COMPLEX_AGREEMENT_ID"))||
                !("EXTERN".equals(sIdentityType))) {
                sTagForThirdRadioButton = "DISABLED";
            } else {
                sTagForThirdRadioButton = "";
                sTagForFinishButton = "";
            }
        } else {
            sTagForFirstRadioButton = "";
            sTagForSecondRadioButton = "";
            sTagForThirdRadioButton = "";

            sTagForFinishButton = "DISABLED";
        }
    }

    //Bug 46394, start
    public boolean isModuleInst(String module_)
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        ASPCommand cmd = mgr.newASPCommand();
        String modVersion;

        cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
        cmd.addParameter("MODULENAME",module_);

        trans1 = mgr.performConfig(trans1);
        modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

        if ( !mgr.isEmpty(modVersion) )
            return true;
        else
            return false;
    }

    //=============================================================================
    //=============================================================================
    //=============================================================================

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        //  -----------------------------------------------------------------------------------------------------------
        //  ----------------------------------------  First Page of The Wizard  ---------------------------------------
        //  -----------------------------------------------------------------------------------------------------------

        blkOne = mgr.newASPBlock("BLKONE");

        blkOne.addField("REPORTED_BY")
        .setDynamicLOV("EMPLOYEE_LOV", "COMPANY", 600,445)
        .setUpperCase()
        .setSize(43)
        .setMaxLength(20)
        .setCustomValidation("REPORTED_BY,COMPANY","REPORTED_BY_ID")
        .setLabel("PCMWCREATEWOWIZREPORTEDBY: Reported By");

        blkOne.addField("CONTRACT")
        .setDynamicLOV("USER_ALLOWED_SITE_LOV", "", 600,445)
        .setUpperCase()
        .setSize(43)
        .setMaxLength(5)
        .setLabel("PCMWCREATEWOWIZCONTRACT: WO Site");

        blkOne.addField("CONNECTION_TYPE")
        .enumerateValues("MAINT_CONNECTION_TYPE_API")
        .setSelectBox()
        .setSize(43)
        .setMaxLength(5)
        .setReadOnly()
        .setLabel("PCMWCREATEWOWIZCONNECTIONTYPE: Asset Type");

        blkOne.addField("ERR_DESCR")
        .setSize(43)
        .setMaxLength(60)
        .setLabel("PCMWCREATEWOWIZERRDESCRIPTION: Directive");

        blkOne.addField("ORG_CODE")
        .setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV", "CONTRACT", 600,445)
        .setCustomValidation("CONTRACT,ORG_CODE","ORG_DESC")
        .setUpperCase()
        .setSize(8)
        .setMaxLength(8)
        .setLabel("PCMWCREATEWOWIZORGCODE: Maintenance Organization");

        blkOne.addField("ORG_DESC")
        .setFunction("Organization_Api.Get_Description(CONTRACT,ORG_CODE)")
        .setReadOnly()
        .setMaxLength(2000)
        .setSize(25);

        blkOne.addField("REQUIRED_START_DATE", "Datetime")
        .setSize(43)
        .setLabel("PCMWCREATEWOWIZREQUIREDSTARTDATE: Required Start");

        blkOne.addField("REQUIRED_END_DATE", "Datetime") 
        .setSize(43)
        .setLabel("PCMWCREATEWOWIZREQUIREDENDDATE: Latest Completion");

        // -------------------------------------------------------------------------------------------------
        // ------------------------  Hidden Fields ---------------------------------------------------------
        // -------------------------------------------------------------------------------------------------

        blkOne.addField("COMPANY")
        .setHidden();

        blkOne.addField("REPORTED_BY_ID")
        .setHidden();

        blkOne.addField("EMPLOYEE")
        .setFunction("''")
        .setHidden();

        blkOne.addField("IDENTITY_TYPE")
        .setFunction("''")
        .setHidden();

        blkOne.addField("PARTY_TYPE")
        .setFunction("''")
        .setHidden();

        blkOne.addField("CLIENT_VAL_CUST")
        .setFunction("''")
        .setHidden();

        blkOne.addField("WO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("DO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("RO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("CREATE_WO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("CREATE_DO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("CREATE_RO_NO")
        .setFunction("''")
        .setHidden();

        blkOne.addField("CO_TYPE")
        .setFunction("''")
        .setHidden();

        blkOne.addField("IS_CREDIT_STOPPED")
        .setFunction("''")
        .setHidden();

        blkOne.setView("ACTIVE_SEPARATE");
        blkOne.defineCommand("ACTIVE_SEPARATE_API", "New__");
        rowSetOne = blkOne.getASPRowSet();

        tblOne = mgr.newASPTable(blkOne);

        barOne = mgr.newASPCommandBar(blkOne);
        barOne.disableCommand(barOne.SAVERETURN);
        barOne.disableCommand(barOne.SAVE);
        barOne.disableCommand(barOne.CANCELEDIT);
        barOne.disableCommand(barOne.FORWARD);
        barOne.disableModeLabel();
        barOne.disableMinimize();

        layOne = blkOne.getASPBlockLayout();
        layOne.setDefaultLayoutMode(layOne.EDIT_LAYOUT);

        layOne.setSimple("ORG_DESC");
        layOne.setEditable();
        layOne.setDialogColumns(1);

        layOne.setFieldOrder("REPORTED_BY,CONNECTION_TYPE,CONTRACT,ORG_CODE,ORG_DESC,ERR_DESCR,REQUIRED_START_DATE,REQUIRED_END_DATE");

        //  -----------------------------------------------------------------------------------------------------------
        //  ----------------------------------------  Second Page of The Wizard  --------------------------------------
        //  -----------------------------------------------------------------------------------------------------------

        blkTwo = mgr.newASPBlock("BLKTWO");

        blkTwo.addField("CUSTOMER_NO")
        .setDynamicLOV("CUSTOMER_INFO", "" , 600, 445)
        .setValidation("CUSTOMER_INFO_API.Get_Name","CUSTOMER_NO","CUSTOMER_DESC")
        .setUpperCase()
        .setSize(20)
        .setMaxLength(20)
        .setLabel("PCMWCREATEWOWIZCUSTOMERNO: Customer ID");

        blkTwo.addField("CUSTOMER_DESC")
        .setFunction("CUSTOMER_INFO_API.Get_Name(CUSTOMER_NO)")
        .setReadOnly()
        .setSize(35)
        .setMaxLength(2000);

        // (+) WINGS (Start)
        blkTwo.addField("COMPLEX_AGREEMENT_ID")
        .setDynamicLOV("COMPLEX_AGREEMENT", "ITEM2_CONTRACT CONTRACT,CUSTOMER_NO" , 600, 445)
        .setValidation("COMPLEX_AGREEMENT_API.Get_Complex_Agreement_Desc","COMPLEX_AGREEMENT_ID","AGREEMENT_DESC")
        .setUpperCase()
        .setSize(20)
        .setMaxLength(10)
        .setLabel("PCMWCREATEWOWIZCOMPLEXAGREEMENTID: Agreement ID");
        // (+) WINGS (Finish)

        blkTwo.addField("AGREEMENT_DESC")
        .setFunction("COMPLEX_AGREEMENT_API.Get_Complex_Agreement_Desc(COMPLEX_AGREEMENT_ID)")
        .setReadOnly()
        .setSize(35)
        .setMaxLength(2000);

        blkTwo.addField("CUST_ORDER_TYPE")
        .setDynamicLOV("CUST_ORDER_TYPE", "" , 600, 445)
        .setValidation("CUST_ORDER_TYPE_API.Get_Description","CUST_ORDER_TYPE","ORDER_DESCRIPTION")
        .setUpperCase()
        .setSize(20)
        .setMaxLength(3)
        .setLabel("PCMWCREATEWOWIZCUSTORDERTYPE: Order Type");

        blkTwo.addField("ORDER_DESCRIPTION")
        .setFunction("CUST_ORDER_TYPE_API.Get_Description(CUST_ORDER_TYPE)")
        .setReadOnly()
        .setSize(35)
        .setMaxLength(2000);

        blkTwo.addField("AUTHORIZE_CODE")
        .setDynamicLOV("ORDER_COORDINATOR_LOV", "" , 600, 445)
        .setValidation("Order_Coordinator_API.Get_Name","AUTHORIZE_CODE","COORDINATOR_NAME")
        .setUpperCase()
        .setSize(20)
        .setMaxLength(20)
        .setLabel("PCMWCREATEWOWIZAUTHORIZECODE: Coordinator");

        blkTwo.addField("COORDINATOR_NAME")
        .setFunction("Order_Coordinator_API.Get_Name(AUTHORIZE_CODE)")
        .setReadOnly()
        .setSize(35)
        .setMaxLength(2000);

        blkTwo.addField("PART_NO")
        .setCustomValidation("PART_NO,SERIAL_NO","PART_NO,PART_NAME,SERIAL_NO")
        .setUpperCase()
        .setSize(25)
        .setMaxLength(25)
        .setLabel("PCMWCREATEWOWIZPARTNO: Part No");

        blkTwo.addField("PART_NAME")
        .setFunction("''")
        .setReadOnly()
        .setSize(38)
        .setMaxLength(2000);

        //Bug 46394, start
        blkTwo.addField("SERIAL_NO")
        .setCustomValidation("SERIAL_NO,PART_NO","PART_NO,PART_NAME,SERIAL_NO")
        .setUpperCase()
        .setSize(73)
        .setMaxLength(50)
        .setLabel("PCMWCREATEWOWIZSERIALNO: Serial No");

        // 031103  ARWILK  Begin  (Bug#109891)
        blkTwo.addField("OBJ_NAME")
        .setFunction("''")
        .setHidden();

        blkTwo.addField("NO_OF_OBJ", "Number")
        .setFunction("0")
        .setHidden();

        blkTwo.addField("CUSTOM_CONFIRM_MSG")
        .setFunction("''")
        .setHidden();

        // 031103  ARWILK  End  (Bug#109891)
        //Bug 54784, start
        blkTwo.addField("STRU_EXIST")
        .setFunction("TRUE")
        .setHidden();

        blkTwo.addField("VIM_TRUC_MSG")
        .setFunction("''")
        .setHidden();

        blkTwo.addField("VALIDATE_SERIAL")
        .setFunction("TRUE")
        .setHidden();

        blkTwo.addField("VALIDATE_SERIAL_MSG")
        .setFunction("''")
        .setHidden();


        blkTwo.addField("ITEM2_CONTRACT")
        .setHidden()
        .setDbName("CONTRACT");
        //Bug 54784, end

        //Bug 55139, start
        blkTwo.addField("STRU_TRANS")
        .setFunction("TRUE")
        .setHidden();

        blkTwo.addField("STRU_TRANS_MSG")
        .setFunction("''")
        .setHidden();

        blkTwo.addField("DUMMY").setFunction("''").setHidden();
        //Bug 55139, end

        blkTwo.setView("ACTIVE_SEPARATE");
        blkTwo.defineCommand("ACTIVE_SEPARATE_API","New__");
        rowSetTwo = blkTwo.getASPRowSet();

        tblTwo = mgr.newASPTable(blkTwo);

        barTwo = mgr.newASPCommandBar(blkTwo);
        barTwo.disableCommand(barTwo.SAVERETURN);
        barTwo.disableCommand(barTwo.SAVE);
        barTwo.disableCommand(barTwo.CANCELEDIT);
        barTwo.disableCommand(barTwo.FORWARD);
        barTwo.disableModeLabel();
        barTwo.disableMinimize();

        layTwo = blkTwo.getASPBlockLayout();
        layTwo.setDefaultLayoutMode(layTwo.EDIT_LAYOUT);

        layTwo.setSimple("CUSTOMER_DESC");
        layTwo.setSimple("AGREEMENT_DESC");
        layTwo.setSimple("ORDER_DESCRIPTION");
        layTwo.setSimple("COORDINATOR_NAME");
        layTwo.setSimple("PART_NAME");

        layTwo.setDialogColumns(1);
        layTwo.setEditable();

        layTwo.defineGroup(mgr.translate("PCMWCREATEWOWIZCUSTINFO: Customer Info"),"CUSTOMER_NO,CUSTOMER_DESC,COMPLEX_AGREEMENT_ID,AGREEMENT_DESC,CUST_ORDER_TYPE,ORDER_DESCRIPTION,AUTHORIZE_CODE,COORDINATOR_NAME",true,true);
        layTwo.defineGroup(mgr.translate("PCMWCREATEWOWIZ2INSETORSWPINTO: Part Info"),"PART_NO,PART_NAME,SERIAL_NO",true,true);

        //  -----------------------------------------------------------------------------------------------------------
        //  ----------------------------------------  Last Page of The Wizard  ----------------------------------------
        //  -----------------------------------------------------------------------------------------------------------

        blkDummy = mgr.newASPBlock("BLKDUMMY");

        blkDummy.addField("DUMMY_FIELD")
        .setFunction("''")
        .setHidden();

        blkDummy.setView("DUAL");
        rowSetDummy = blkDummy.getASPRowSet();

        tblDummy = mgr.newASPTable(blkDummy);

        barDummy = mgr.newASPCommandBar(blkDummy);
        barDummy.disableCommand(barDummy.SAVERETURN);
        barDummy.disableCommand(barDummy.SAVE);
        barDummy.disableCommand(barDummy.CANCELEDIT);
        barDummy.disableCommand(barDummy.FORWARD);
        barDummy.disableModeLabel();
        barDummy.disableMinimize();

        layDummy = blkDummy.getASPBlockLayout();
        layDummy.setDefaultLayoutMode(layDummy.EDIT_LAYOUT);

        layDummy.defineGroup(mgr.translate("PCMWCREATEWOWIZCREATEOPTIONS: Create Options"),"",true,true);


        setWizardOptions();

        layOne.showBottomLine(false);
        layTwo.showBottomLine(false);
        layDummy.showBottomLine(false);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        setFieldAccessibility();

        //Bug 46394, start
        if ( mgr.isPresentationObjectInstalled("VIMW/VimSerialStrWoLov2.page") )
            mgr.getASPField("PART_NO").setLOV("../vimw/VimSerialStrWoLov2.page", "",600,445);

        if ( mgr.isPresentationObjectInstalled("VIMW/VimSerialStrWoLov2.page") )
            mgr.getASPField("SERIAL_NO").setLOV("../vimw/VimSerialStrWoLov3.page", "PART_NO",600,445);
    }

    private void setWizardOptions()
    {
        disableHomeIcon();
        disableNavigate();
        disableOptions();
    }

    //===============================================================
    //  HTML
    //===============================================================

    protected String getDescription()
    {
        return "PCMWCREATEWOWIZARDTITLE: Create Work, Dispatch and Receive Order";
    }

    protected String getTitle()
    {
        return "PCMWCREATEWOWIZARDTITLE: Create Work, Dispatch and Receive Order";
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();
        out.append("<html>\n");
        out.append("<head>");
        out.append(mgr.generateHeadTag(this.getDescription()));
        out.append("<title>");
        out.append(this.getTitle());
        out.append("</title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(mgr.startPresentation(this.getDescription()));

        if (wizPageNo == 3)
            out.append("<table id=\"MAINTBL\" width=\"60%\" border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=0>\n");
        else
            out.append("<table id=\"MAINTBL\" width=\"70%\" border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=0>\n");

        out.append("	<tr>\n");
        out.append("		<td nowrap align=\"left\">\n");

        if (wizPageNo == 1) {
            out.append(barOne.showBar());
        } else if (wizPageNo == 2) {
            out.append(barTwo.showBar());
        } else if (wizPageNo == 3) {
            out.append(barDummy.showBar());
        }

        out.append("			<table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '60%'>\n");
        out.append("				<tr>\n");
        out.append("					<td nowrap align=\"center\" valign=\"top\">\n");
        out.append("			 			<br>\n");
        out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        out.append("						<img src = \"../pcmw/images/ChangeWorkOrderWiz.gif\">");
        out.append("					</td>\n");
        out.append("					<td>\n");
        out.append("						<table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1>\n");

        if (wizPageNo == 1) {
            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                	<br>\n");
            out.append("                                    &nbsp;&nbsp;" + fmt.drawReadValue(mgr.translate("PCMWCREATEWOWIZFIRSTWELCOMEWIZ: Welcome to the Create Work, Dispatch and Receive Order Wizard.")) + "\n");
            out.append("								</td>\n");
            out.append("							</tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                    &nbsp;&nbsp;" + fmt.drawReadValue(mgr.translate("PCMWCREATEWOWIZFIRSTUSEOFWIZ: Use this wizard to create Work Orders along with Dispatch Orders and Receive Orders.")) + "\n");
            out.append("								</td>\n");
            out.append("							</tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                	<br>\n");
            out.append(layOne.generateDialog());
            out.append("								</td>\n");
            out.append("							</tr>\n");
        } else if (wizPageNo == 2) {
            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                	<br>\n");
            out.append("                                    &nbsp;&nbsp;" + fmt.drawReadValue(mgr.translate("PCMWCREATEWOWIZSECONDUSEOFWIZ: Use this wizard page to specify Customer related data and Part related data.")) + "\n");
            out.append("								</td>\n");
            out.append("							</tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                	<br>\n");
            out.append(layTwo.generateDialog());
            out.append("								</td>\n");
            out.append("							</tr>\n");
        } else if (wizPageNo == 3) {
            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                	<br>\n");
            out.append("                                    &nbsp;&nbsp;" + fmt.drawReadValue(mgr.translate("PCMWCREATEWOWIZTHIRDUSEOPTOFWIZ: Select an option. If one or more options are disabled,")) + "\n");
            out.append("								</td>\n");
            out.append("							</tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append("                                    &nbsp;&nbsp;" + fmt.drawReadValue(mgr.translate("PCMWCREATEWOWIZTHIRDUSEBUTTOFWIZ: use the Previous button to go back and enter the necessary data.")) + "\n");
            out.append("								</td>\n");
            out.append("							</tr>\n");
            //Bug 75375, start
            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append(""+fmt.drawRadio("PCMWCREATEWOWIZCREATEWO: Create Work Order",
                                                 "SELACTPERFOPTION",  
                                                 "CREATE_WO",
                                                 false,
                                                 sTagForFirstRadioButton)+"\n");

            out.append("\n      </td>\n   </tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append(""+fmt.drawRadio("PCMWCREATEWOWIZCREATEWODO: Create Work Order and Dispatch Order",
                                                 "SELACTPERFOPTION",
                                                 "CREATE_WO_DO",
                                                 false,
                                                 sTagForSecondRadioButton)+"\n");
            out.append("\n      </td>\n   </tr>\n");

            out.append("							<tr>\n");
            out.append("								<td nowrap align=\"left\" valign=\"top\">\n");
            out.append(""+fmt.drawRadio("PCMWCREATEWOWIZCREATEWODORO: Create Work Order, Dispatch Order and Receive Order",
                                                 "SELACTPERFOPTION",
                                                 "CREATE_WO_DO_RO",
                                                 false,
                                                 sTagForThirdRadioButton)+"\n");
            out.append("\n      </td>\n   </tr>\n");
            //Bug 75375, end
            out.append("                                	<br>\n");
            out.append(layDummy.generateDialog());
            out.append("								</td>\n");
            out.append("							</tr>\n");

        }

        /* This is done on purpose. Else the function handleStepMoveButtons will give errors. */

        out.append("						</table>\n");
        out.append("					</td>\n");
        out.append("				</tr>\n");
        out.append("			</table>\n");
        out.append("			<br>\n");
        out.append("		</td>\n");
        out.append("	</tr>\n");
        out.append("	<tr>\n");
        out.append("</table>\n");

        if (wizPageNo == 3)
            out.append("<table id=\"BUTTONTBL\" width=\"60%\" border=0 cellspacing=0 cellpadding=1>\n");
        else
            out.append("<table id=\"BUTTONTBL\" width=\"70%\" border=0 cellspacing=0 cellpadding=1>\n");

        out.append("	<tr>\n");
        out.append("		<td width=\"100%\" align=\"right\">\n");
        out.append("			<br>\n");
        out.append(fmt.drawSubmit("CANCEL",mgr.translate("PCMWCREATEWOWIZCANCEL:  Cancel "), ""));
        out.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); 
        out.append(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWCREATEWOWIZPREVIOUS: < Previous"),""));
        out.append("&nbsp;&nbsp;");

        // 031103  ARWILK  Begin  (Bug#109891)
        if (wizPageNo == 2)
            /* This is done on purpose. If drawSubmit is used the form submit cannot be controlled conditionally*/
            out.append(fmt.drawButton("NEXT",mgr.translate("PCMWCREATEWOWIZNEXT:  Next > "),"onClick=\"handleWarningMessages(i)\""));
        else
            out.append(fmt.drawSubmit("NEXT",mgr.translate("PCMWCREATEWOWIZNEXT:  Next > "),""));
        // 031103  ARWILK  End  (Bug#109891)

        out.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.append(fmt.drawSubmit("FINISH",mgr.translate("PCMWCREATEWOWIZFINISH:  Finish "),sTagForFinishButton));
        out.append("&nbsp;&nbsp;");
        out.append("		</td>\n");
        out.append("	</tr>\n");
        out.append("</table>\n");
        // 031103  ARWILK  Begin  (Bug#109891)
        out.append(fmt.drawHidden("FINISH_APPROOVED",""));
        // 031103  ARWILK  End  (Bug#109891)
        out.append(mgr.endPresentation());
        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>");

        // -----------------------------------------------------------------------------------------------------------
        // ----The following piece of code adds the javascript function to the end of the OnChange String ------------
        // -----------------------------------------------------------------------------------------------------------

        String scriptPartToAppend = "";
        StringBuffer secondReplacement = new StringBuffer(out.toString());

        scriptPartToAppend = ";handleStepMoveButtons('" + wizPageNo + "')\"";

        int onChangeIndex = 0;
        int lastQuoteIndex = 0;

        while (onChangeIndex != -1) {
            onChangeIndex = secondReplacement.indexOf("OnChange=\"", lastQuoteIndex);

            if (onChangeIndex == -1)
                break;

            lastQuoteIndex = secondReplacement.indexOf("\"", onChangeIndex+11);
            secondReplacement.replace(lastQuoteIndex, lastQuoteIndex+1, scriptPartToAppend);
        }

        out.clear();
        out.append(secondReplacement.toString());

        // ------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------------

        appendDirtyJavaScript("\n\n\n");

        if (wizPageNo == 1)
            appendDirtyJavaScript("document.form.PREVIOUS.disabled = true;");

        if (wizPageNo == 3)
            appendDirtyJavaScript("document.form.NEXT.disabled = true;");

        if ((wizPageNo == 1) || (wizPageNo == 2))
            appendDirtyJavaScript("document.form.FINISH.disabled = true;");

        // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("\n\nhandleStepMoveButtons('" + wizPageNo + "');\n\n");

        appendDirtyJavaScript("\n\n\n");
        appendDirtyJavaScript("function handleStepMoveButtons(jsvWizPageNo)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("		if (jsvWizPageNo == '1')\n");
        appendDirtyJavaScript("		{\n\n");
        appendDirtyJavaScript("			document.form.PREVIOUS.disabled = true; \n\n");
        appendDirtyJavaScript("			if (document.form.REPORTED_BY.value == '' || document.form.CONTRACT.value == '' ||\n");
        appendDirtyJavaScript("		    	document.form.ERR_DESCR.value == '' || document.form.ORG_CODE.value == '' ||\n");
        appendDirtyJavaScript("		    	document.form.CONNECTION_TYPE.value == '' || document.form.REQUIRED_START_DATE.value == '' ||\n");
        appendDirtyJavaScript("	 	    	document.form.REQUIRED_END_DATE.value == '')\n");
        appendDirtyJavaScript("			{\n");
        appendDirtyJavaScript("				document.form.NEXT.disabled = true;\n");
        appendDirtyJavaScript("				return;\n");
        appendDirtyJavaScript("			}\n");
        appendDirtyJavaScript("			else\n");
        appendDirtyJavaScript("			{\n");
        appendDirtyJavaScript("				document.form.NEXT.disabled = false;\n");
        appendDirtyJavaScript("				return;\n");
        appendDirtyJavaScript("			}\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else if (jsvWizPageNo == '2')\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("			document.form.PREVIOUS.disabled = false;\n\n");
        appendDirtyJavaScript("			if (document.form.CUSTOMER_NO.value == '' || document.form.COMPLEX_AGREEMENT_ID.value == '' ||\n");
        appendDirtyJavaScript("				document.form.PART_NO.value == '' || document.form.SERIAL_NO.value == '')\n");
        appendDirtyJavaScript("			{\n");
        appendDirtyJavaScript("				document.form.NEXT.disabled = true;\n");
        appendDirtyJavaScript("				return;\n");
        appendDirtyJavaScript("			}\n");
        appendDirtyJavaScript("			else\n");
        appendDirtyJavaScript("			{\n");
        appendDirtyJavaScript("				document.form.NEXT.disabled = false;\n");
        appendDirtyJavaScript("				return;\n");
        appendDirtyJavaScript("			}\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		else if (jsvWizPageNo == '3')\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("			document.form.NEXT.disabled = true;\n");
        appendDirtyJavaScript("			document.form.PREVIOUS.disabled = false;\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("}\n\n");

        // 031103  ARWILK  Begin  (Bug#109891)
        if (wizPageNo == 2) {
            appendDirtyJavaScript("function handleWarningMessages(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("		document.form.FINISH_APPROOVED.value = \"\";\n");
            appendDirtyJavaScript("         preValidateSerialNo(i);\n");
            appendDirtyJavaScript("		validateSerialNo(i);\n");
            appendDirtyJavaScript("		if (document.form.VALIDATE_SERIAL.value == \"FALSE\")\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			if (document.form.VALIDATE_SERIAL_MSG.value != \"\")\n");
            appendDirtyJavaScript("			{\n");         
            appendDirtyJavaScript("				if (!confirm(document.form.VALIDATE_SERIAL_MSG.value))\n");
            appendDirtyJavaScript("					return false;\n");
            appendDirtyJavaScript("			}\n");

            appendDirtyJavaScript("		}\n");

            appendDirtyJavaScript("		validateNoOfObj(i);\n");
            appendDirtyJavaScript("		if (document.form.NO_OF_OBJ.value > 0)\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			if (document.form.CUSTOM_CONFIRM_MSG.value != \"\")\n");
            appendDirtyJavaScript("			{\n");
            appendDirtyJavaScript("				if (!confirm(document.form.CUSTOM_CONFIRM_MSG.value))\n");
            appendDirtyJavaScript("					return false;\n");
            appendDirtyJavaScript("			}\n");
            appendDirtyJavaScript("		}\n");





            //Bug 55139, start
            appendDirtyJavaScript("		validateStruTrans(i);\n");
            appendDirtyJavaScript("		if (document.form.STRU_TRANS.value == \"FALSE\")\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			if (document.form.STRU_TRANS_MSG.value != \"\")\n");
            appendDirtyJavaScript("			{\n");
            appendDirtyJavaScript("				if (!confirm(document.form.STRU_TRANS_MSG.value))\n");
            appendDirtyJavaScript("					return false;\n");
            appendDirtyJavaScript("			}\n");
            appendDirtyJavaScript("		}\n");
            //Bug 55139, end


            //Bug 54784, start
            appendDirtyJavaScript("		validateStruExist(i);\n");
            appendDirtyJavaScript("		if (document.form.STRU_EXIST.value == \"FALSE\")\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			if (document.form.VIM_TRUC_MSG.value != \"\")\n");
            appendDirtyJavaScript("			{\n");
            appendDirtyJavaScript("				if (!confirm(document.form.VIM_TRUC_MSG.value))\n");
            appendDirtyJavaScript("					return false;\n");
            appendDirtyJavaScript("			}\n");
            appendDirtyJavaScript("		}\n"); 
            //Bug 54784, end
            appendDirtyJavaScript("		document.form.FINISH_APPROOVED.value = \"TRUE\";\n");
            appendDirtyJavaScript("		document.form.submit();\n");
            appendDirtyJavaScript("}\n");


            appendDirtyJavaScript("function validateSerialNo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("         preValidateSerialNo(i);\n");
            //appendDirtyJavaScript("    document.form.NEXT.disabled = true;");
            appendDirtyJavaScript("		if((getValue_(\"PART_NO\",i)==\"\") || (getValue_(\"SERIAL_NO\",i)==\"\"))\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			getField_(\"VALIDATE_SERIAL\",i).value = \"TRUE\";\n");
            appendDirtyJavaScript("			return;\n");
            appendDirtyJavaScript("		}\n\n");
            appendDirtyJavaScript("		window.status=\"Please wait for validation\";\n\n");
            appendDirtyJavaScript(" 	r = __connect(\n"); 
            appendDirtyJavaScript("			\"" + mgr.getURL() + "?VALIDATE=VALIDATE_SERIAL\"\n");
            appendDirtyJavaScript("			+ \"&PART_NO=\" + URLClientEncode(getValue_(\"PART_NO\",i))\n");
            appendDirtyJavaScript("			+ \"&SERIAL_NO=\" + URLClientEncode(getValue_(\"SERIAL_NO\",i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("		window.status=\"\";\n\n");
            appendDirtyJavaScript("		if( checkStatus_(r,\"VALIDATE_SERIAL\",i,\"\") )\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			assignValue_(\"VALIDATE_SERIAL\",i,0);\n");
            appendDirtyJavaScript("			assignValue_(\"VALIDATE_SERIAL_MSG\",i,1);\n");
            appendDirtyJavaScript("		}\n");         
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function preValidateSerialNo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	    if( getRowStatus_('BLKTWO',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	    setDirty();\n");
            appendDirtyJavaScript("	    if( !checkSerialNo(i) ) return;\n");
            appendDirtyJavaScript("	    if( getValue_('SERIAL_NO',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	    if( getValue_('PART_NO',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	    if( getValue_('SERIAL_NO',i)=='' )\n");
            appendDirtyJavaScript("	    {\n");
            appendDirtyJavaScript("		getField_('PART_NO',i).value = '';\n");
            appendDirtyJavaScript("		getField_('PART_NAME',i).value = '';\n");
            appendDirtyJavaScript("		getField_('SERIAL_NO',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	    }\n");
            appendDirtyJavaScript("	    window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	    r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/CreateWorkOrderWiz.page'+'?VALIDATE=SERIAL_NO'\n");
            appendDirtyJavaScript("		+ '&SERIAL_NO=' + URLClientEncode(getValue_('SERIAL_NO',i))\n");
            appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	    window.status='';\n");

            appendDirtyJavaScript("	    if( checkStatus_(r,'SERIAL_NO',i,'Serial No') )\n");
            appendDirtyJavaScript("	    {\n");
            appendDirtyJavaScript("		assignValue_('PART_NO',i,0);\n");
            appendDirtyJavaScript("		assignValue_('PART_NAME',i,1);\n");
            appendDirtyJavaScript("		assignValue_('SERIAL_NO',i,2);\n");
            appendDirtyJavaScript("	    }\n");
            appendDirtyJavaScript("}\n");





            appendDirtyJavaScript("function validateNoOfObj(i)\n");
            appendDirtyJavaScript("{\n");   
            appendDirtyJavaScript("		if((getValue_(\"PART_NO\",i)==\"\") || (getValue_(\"SERIAL_NO\",i)==\"\"))\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			getField_(\"NO_OF_OBJ\",i).value = 0;\n");
            appendDirtyJavaScript("			return;\n");
            appendDirtyJavaScript("		}\n\n");
            appendDirtyJavaScript("		window.status=\"Please wait for validation\";\n\n");
            appendDirtyJavaScript(" 	r = __connect(\n");
            appendDirtyJavaScript("			\"" + mgr.getURL() + "?VALIDATE=NO_OF_OBJ\"\n");
            appendDirtyJavaScript("			+ \"&PART_NO=\" + URLClientEncode(getValue_(\"PART_NO\",i))\n");
            appendDirtyJavaScript("			+ \"&SERIAL_NO=\" + URLClientEncode(getValue_(\"SERIAL_NO\",i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("		window.status=\"\";\n\n");
            appendDirtyJavaScript("		if( checkStatus_(r,\"NO_OF_OBJ\",i,\"\") )\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			assignValue_(\"NO_OF_OBJ\",i,0);\n");
            appendDirtyJavaScript("			assignValue_(\"CUSTOM_CONFIRM_MSG\",i,1);\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("}\n\n");



            //Bug 54784, start
            appendDirtyJavaScript("function validateStruExist(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("		if((getValue_(\"PART_NO\",i)==\"\") || (getValue_(\"SERIAL_NO\",i)==\"\"))\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			getField_(\"STRU_EXIST\",i).value = \"TRUE\";\n");
            appendDirtyJavaScript("			return;\n");
            appendDirtyJavaScript("		}\n\n");
            appendDirtyJavaScript("		window.status=\"Please wait for validation\";\n\n");
            appendDirtyJavaScript(" 	r = __connect(\n"); 
            appendDirtyJavaScript("			\"" + mgr.getURL() + "?VALIDATE=STRU_EXIST\"\n");
            appendDirtyJavaScript("			+ \"&COMPLEX_AGREEMENT_ID=\" + URLClientEncode(getValue_(\"COMPLEX_AGREEMENT_ID\",i))\n");
            appendDirtyJavaScript("			+ \"&ITEM2_CONTRACT=\" + URLClientEncode(getValue_(\"ITEM2_CONTRACT\",i))\n");
            appendDirtyJavaScript("			+ \"&PART_NO=\" + URLClientEncode(getValue_(\"PART_NO\",i))\n");
            appendDirtyJavaScript("			+ \"&SERIAL_NO=\" + URLClientEncode(getValue_(\"SERIAL_NO\",i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("		window.status=\"\";\n\n");
            appendDirtyJavaScript("		if( checkStatus_(r,\"STRU_EXIST\",i,\"\") )\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			assignValue_(\"STRU_EXIST\",i,0);\n");
            appendDirtyJavaScript("			assignValue_(\"VIM_TRUC_MSG\",i,1);\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("}\n\n"); 

            //Bug 54784, end
            //Bug 55139, start
            appendDirtyJavaScript("function validateStruTrans(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("		if((getValue_(\"PART_NO\",i)==\"\") || (getValue_(\"SERIAL_NO\",i)==\"\"))\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			getField_(\"STRU_TRANS\",i).value = \"TRUE\";\n");
            appendDirtyJavaScript("			return;\n");
            appendDirtyJavaScript("		}\n\n");
            appendDirtyJavaScript("		window.status=\"Please wait for validation\";\n\n");
            appendDirtyJavaScript(" 	r = __connect(\n"); 
            appendDirtyJavaScript("			\"" + mgr.getURL() + "?VALIDATE=STRU_TRANS\"\n");
            appendDirtyJavaScript("			+ \"&ITEM2_CONTRACT=\" + URLClientEncode(getValue_(\"ITEM2_CONTRACT\",i))\n");
            appendDirtyJavaScript("			+ \"&PART_NO=\" + URLClientEncode(getValue_(\"PART_NO\",i))\n");
            appendDirtyJavaScript("			+ \"&SERIAL_NO=\" + URLClientEncode(getValue_(\"SERIAL_NO\",i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("		window.status=\"\";\n\n");
            appendDirtyJavaScript("		if( checkStatus_(r,\"STRU_TRANS\",i,\"\") )\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			assignValue_(\"STRU_TRANS\",i,0);\n");
            appendDirtyJavaScript("			assignValue_(\"STRU_TRANS_MSG\",i,1);\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("}\n\n"); 
            //Bug 55139, end

        } else
            appendDirtyJavaScript("		document.form.FINISH_APPROOVED.value = \"\";\n");

        // 031103  ARWILK  End  (Bug#109891)

        /* Since validate methods of date functions are called through the showCalender method the following modification is needed 
            inorder to call custom functions ( This is a function in calendar.js ) (The ASPField.setvalidateFunstion won't work because I need to pass some 
            variables to the function. Else that could have been used.*/

        appendDirtyJavaScript("\n\n"); 
        appendDirtyJavaScript("function __setDate(r)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( checkStatus_(r,target_field,i,label) )\n");
        appendDirtyJavaScript("   {\n");
        // (+) WINGS (Start)
        //appendDirtyJavaScript("      n = (document.all) ? r.length-1:r.length()-1;\n");
        appendDirtyJavaScript("      n = r.length-1;\n");
        // (+) WINGS (Finish)
        appendDirtyJavaScript("      getField_(target_field,pos).value = r.substring(1,n);\n");
        appendDirtyJavaScript("   }\n\n");
        appendDirtyJavaScript("   closeCalendar();");
        appendDirtyJavaScript("   if (post_method != '')\n");
        appendDirtyJavaScript("      eval(post_method + \"(pos);handleStepMoveButtons('" + wizPageNo + "')\");\n"); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("}\n\n");


        /* LOV Functionality has a problem. When a value is changed using an LOV the OnChange functions that are associated with the 
            respective field are not called. So inorder to call client functions the java script lov functions should be changed or 
            the following function should be overwritten. ( This is a function in calendar.js ) (The ASPField.setvalidateFunstion won't work because I need to pass some 
            variables to the function. Else that could have been used.*/ 

        appendDirtyJavaScript("\n\n"); 
        appendDirtyJavaScript("function closeLOVWindow(value)\n"); 
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("		fld = getField_(lov_field_name,lov_row_nr);\n");
        appendDirtyJavaScript("		fld.value = value;\n");
        appendDirtyJavaScript("		fld.focus();\n");
        appendDirtyJavaScript("		lov_window.close();\n");
        appendDirtyJavaScript("		lov_window = null;\n");
        appendDirtyJavaScript("		eval(post_lov_function + \"(lov_row_nr);handleStepMoveButtons('" + wizPageNo + "')\");\n"); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("}\n\n");

        // 031103  ARWILK  Begin  (Bug#109891)
        if (bFinishPerformed || bCancelPerformed) {
            if (bFinishPerformed) {
                if (!vAlertMsgQueue.isEmpty()) {
                    appendDirtyJavaScript("\n\n");

                    for (int i = 0; i < vAlertMsgQueue.size(); i++) {
                        appendDirtyJavaScript("alert(\"" + vAlertMsgQueue.get(i) + "\");\n"); // XSS_Safe ILSOLK 20070713
                    }
                }
            }
            appendDirtyJavaScript("\n\n\nwindow.close();\n\n\n");
        }
        // 031103  ARWILK  End  (Bug#109891)

        return out;
    }
}
