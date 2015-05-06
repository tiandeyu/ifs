/* 
*                 IFS Research & Development 
*
*  This program is protected by copyright law and by international
*  conventions. All licensing, renting, lending or copying (incfluding
*  for private use), and all other use of the program, which is not
*  expressively permitted by IFS Research & Development (IFS), is a
*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File        : ActiveSeparateWizB2E.java 
*  Created     : INROLK  010305  Java conversion.
*  Modified    : 
*  INROLK  010406  Bug ID 60382, Changed Startup() function to get REPORTED_BY from "Person_Info_API.Get_Id_For_User"
*                  Added Validation to the field "REPORTED_BY"
*                  set REPORTED_BY as a visible field,And did some necessary changes.
*  INROLK  010504  Changed btnNext and btnPrevious functions for page 3.to save selected agreement.
*  CHCRLK  010613  Modified overwritten validations and changed document.clientutil to document.applets[0].
*  SHFELK  010627  Changed the function CheckMandatory()
*  INROLK  010928  Changed Discription to description..and Translation Code of page 3. call id 69678.
*  INROLK  011003  Removed "headset.changeRow()" form butNext(). call id 69842.
*  VAGULK  020401  Modified to change the query parameters by entering a random value.
*                  This is done to avoid the browser fetch the page from cache.
* ---------------------Generic WO-------------------------------------------
*  INROLK  021203  Added MCH_CODE_CONTRACT. 
* ----------------------------------------------------------------------------
*  GACOLK  021204  Set Max Length of ITEM0_MCH_CODE to 100
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  BUNILK  030121  Codes reviewed.
*  JEWILK  030924  Corrected overridden javascript function toggleStyleDisplay(). 
*  ARWILK  031216  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040224  Web Alignmnt - removed unnecessary global variables, change of conditional code in validate method.
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  SAPRLK  040323  Merged with SP1.
*  THWILK  040604  Removed STD_JOB_ID according to IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040716  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NAMELK  041104  Duplicated Translation Tags Corrected.
*  ARWILK  041115  Replaced getContents with printContents.
*  SHAFLK  050425  Bug Id 50830, Used mgr.translateJavaScript() and getJSLabel() methods to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  060110  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060209  Call ID 132914,Modified btnNext().
*  SHAFLK  060209  Call 133387,Modified btnNext() and setbuff() methods.
*  NIJALK  060216  Call 134412: Modified accept(). Added method addNumToDate().
*  SHAFLK  060622  Bug 58920, Modified cancelQry() and predefine().
*  AMNILK  060726  Merged Bug Id: 58920.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070404  Modified Predefine() to remove the extra line shown at runtime
*  AMNILK  070427  Call Id:  142918. Removed Cust_Agreement_Product.
*  AMDILK  070504  Call Id 143573: Modified the parameter list for method calls from pkg Psc_Contr_Product_API
*  AMDILK  070506  Call Id 143717: Fetched the agreement details from the contract id
*  AMDILK  070510  Call Id 144208: Change the position of the field "work_type_id"
*  AMDILK  070510  Call Id 144214: Modified btnNext()
*  AMDILK  070514  Call Id 144244: If a maint org is mentioned in the ser.con.line, set that as the default value
*  AMDILK  070519  Call Id 144193: Eliminate the error when saving with an org code
*  ILSOLK  070523  Call ID 144738.(setObjectLov)
*  AMDILK  070525  Call Id 144222: Modified printContents()
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  ILSOLK  070628  Eliminated SQL errors in web applications.
*  ILSOLK  070705  Added SQLInjection comments.
*  ILSOLK  070706  Eliminated XSS.
*  AMDILK  070712  Fetched the agreement informations from the given contrat id
*  ILSOLK  070914  Filtered object using 'Equipment object' type Call ID 148851.
*  CHANLK  071130  Bug 69047 Add contract auto fetch functionality.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
*  071220  NIJALK  Bug 69819, Removed method calls to Customer_Agreement_Api.Get_Desription.
* -----------------------------------------------------------------------
*  080225  AMNILK  Bug Id 70012, Modified printContents().
*  080331  AMNILK  Bug Id 70921, Modified Validate().
*  080430  AMNILK  Bug Id 70156, Modified printContents().
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  SHAFLK  090914  Bug 85582, Modified preDefine() and adjust().
*  SHAFLK  100210  Bug 88904, Modified printContents().
*  IMGULK  100216  Bug 87286, Modified printContents() and corrected where condition of Contract ID LOV.
*  NIJALK  100419  Bug 87935, Modified preDefine().
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPForm;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
//Bug 84436, Start
import ifs.fnd.asp.*;
//Bug 84436, End

public class ActiveSeparateWizB2E extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateWizB2E");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;
    private ASPForm frm;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock itemblk0;
    private ASPRowSet itemset0;
    private ASPCommandBar itembar0;
    private ASPTable itemtbl0;
    private ASPBlockLayout itemlay0;

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPBlockLayout itemlay;

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPBlockLayout itemlay1;

    private ASPBlock itemblklst;
    private ASPRowSet itemsetlst;

    private ASPBlock popblk1;
    private ASPBlock popblk2;
    private ASPBlock popblk3;

    
    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private ASPTransactionBuffer trans12;
    private boolean afterAccept;
    private String obj;
    private String agreeVal;
    private String cont;
    private boolean again;
    private boolean hyper;
    private boolean prev;
    private String custNo;
    private String custName;
    private String repBy;
    private String repById;
    private String fltRepFlg;
    private String compName;
    private String addr1;
    private String addr2;
    private String addr3;
    private String addr4;
    private String addr5;
    private String addr6;
    private String orgCode;
    private String agreementId;
    private String agreementDesc;
    private String agreIndex;
    private String contrct;
    private String reg;
    private String object;
    private String objDesc;
    private String workTypeId;
    private String faultRepAgree;
    private String faultRepAgreeClnt;
    private String planHrs;
    private String authCode;
    private String phoneNo;
    private String referenceNo;
    private String contact;
    private String phone_no;
    private String reference_no;
    private String err_descr_lo;
    private String selecMchCode;
    private boolean againPre;
    private String chosenObject;
    private String closeWin;
    private String chosenWkType;
    private String chosenAgree;
    private String chosenDept;
    private boolean moreWrk;
    private boolean noDept;
    private String execute;
    private boolean objectChanged;
    private ASPBuffer wrkTypeBuff;
    private ASPBuffer objectbuff;
    private ASPBuffer deptBuff;
    private ASPBuffer agreeBuffer;
    private String msg;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private String val;
    private String txt;
    private int i;
    private ASPBuffer row;
    private ASPBuffer temp;
    private String desc;
    private int currrow;
    public  String isSecure[]; 
    private int scr;    
    private int cnt;  
    private String agree;  
    private ASPQuery q;  
    private String str;  
    private boolean showHtmlPart;
    private boolean toDefaultPage;
    private String url;
    private String calling_url;
    private String contractId;
    private String lineNo;
    private String sContractName;
    private String sLineDesc;
    private String sContractType;
    private String sInvoiceType;
    private String sOrgCode;
    //Bug Id 70012,Start
    private boolean bPcmsciExist;
    // Bug Id 70012,End
    
     //Bug 84436, Start
    private ASPField f;
    //Bug 84436, End
    
    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparateWizB2E(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }       

    public void run() 
    {
        ASPManager mgr = getASPManager();


        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();
        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();
        trans12 = mgr.newASPTransactionBuffer();
        frm = mgr.getASPForm();

        afterAccept = ctx.readFlag("AFTERACCEPT",false);
        obj = ctx.readValue("OBJ","");
        agreeVal = ctx.readValue("AGREEVAL","");
        scr = ctx.readNumber("SCR",1);
        cnt = ctx.readNumber("CNT",0);
        cont = ctx.readValue("CONT","");
        again = ctx.readFlag("AGAIN",false);
        hyper = ctx.readFlag("HYPER",false);
        prev = ctx.readFlag("PREV",false);
        custNo = ctx.readValue("CUSTNO","");
        custName = ctx.readValue("CUSTNAME","");
        repBy = ctx.readValue("REPBY",""); 
        repById = ctx.readValue("REPBYID",""); 
        fltRepFlg = ctx.readValue("FLTREPFLG",""); 
        compName = ctx.readValue("COMPNAME",""); 
        addr1 = ctx.readValue("ADDR1",""); 
        addr2 = ctx.readValue("ADDR2",""); 
        addr3 = ctx.readValue("ADDR3",""); 
        addr4 = ctx.readValue("ADDR4",""); 
        addr5 = ctx.readValue("ADDR5",""); 
        addr6 = ctx.readValue("ADDR6","");
        sOrgCode = ctx.readValue("ORGCODE",""); 
        agreementId = ctx.readValue("AGREEMENTID",""); 
        agreementDesc = ctx.readValue("AGREEMENTDESC","");
        agreIndex = ctx.readValue("AGREINDEX","");
        contrct = ctx.readValue("CONTRCT",""); 
        reg = ctx.readValue("REG",""); 
        object = ctx.readValue("OBJECT",""); 
        objDesc = ctx.readValue("OBJDESC",""); 
        workTypeId = ctx.readValue("WORKTYPEID",""); 
        faultRepAgree = ctx.readValue("FAULTREPAGREE",""); 
        faultRepAgreeClnt = ctx.readValue("FAULTREPAGREECLNT",""); 
        planHrs = ctx.readValue("PLANHRS",""); 
        authCode = ctx.readValue("AUTHCODE",""); 
        phoneNo = ctx.readValue("PHONENO","");
        referenceNo = ctx.readValue("REFERENCENO","");
        contact = ctx.readValue("CONTACT","");
        phone_no = ctx.readValue("PHONE_NO",""); 
        reference_no = ctx.readValue("REFERENCE_NO",""); 
        err_descr_lo = ctx.readValue("ERR_DESCR_LO","");   
        selecMchCode = ctx.readValue("SELECMCHCODE","");
        hyper = ctx.readFlag("HYPER",false);
        againPre = ctx.readFlag("AGAINPRE",false);
        chosenObject = ctx.readValue("CHOSENOBJECT","");
        closeWin = ctx.readValue("CLOSEWIN","");
        chosenWkType = ctx.readValue("CHOSENWKTYPE","");
        chosenAgree = ctx.readValue("CHOSENAGREE","");
        //chosenDept = ctx.readValue("CHOSENDEPT","");
	sOrgCode = ctx.readValue("CHOSENDEPT",""); 
	moreWrk = ctx.readFlag("MOREWRK",false);
        noDept = ctx.readFlag("NODEPT",false);
        execute = ctx.readValue("EXECUTE", "");
        objectChanged = ctx.readFlag("OBJECTCHANGED", false);
        wrkTypeBuff = ctx.readBuffer("WRKTYPEBUFF");
        objectbuff = ctx.readBuffer("OBJECTBUFF");
        deptBuff = ctx.readBuffer("DEPTBUFF");
        agreeBuffer = ctx.readBuffer("NEWAGRMNT");
        msg = mgr.translate("PCMWACTIVESEPARATEWIZB2EAGREEMSG: Please select a value for Agreement Id.");
        showHtmlPart  = ctx.readFlag("CTSSHHTML",true);
        toDefaultPage = ctx.readFlag("CTXTODEF",false);
	contractId    = ctx.readValue("CONTRACTID", "");
	lineNo        = ctx.readValue("LINENO", "");
	sContractName = ctx.readValue("CONTRACTNAME", "");
	sLineDesc     = ctx.readValue("LINEDESC", "");
	sContractType = ctx.readValue("CONTRACTTYPE", "");
	sInvoiceType  = ctx.readValue("INVOICETYPE", "");
	agreementId   = ctx.readValue("AGREEMENTID", "");
	agreementDesc = ctx.readValue("AGREEMENTDESC", "");
	//Bug Id 70012, Start
	bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
	//Bug Id 70012, End

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
        {
            if ("ActiveSeparateWizB2E".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
            }
            else if ("ActiveSeparateWizB2EFromPortal".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
                toDefaultPage = true;

                calling_url = ctx.getGlobal("CALLING_URL");
                ctx.setGlobal("CALLING_URL",calling_url);                      
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.buttonPressed("PREVIOUS"))
            btnPrevious();
        else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
            btnNext();
        else if (mgr.buttonPressed("CANCEL"))
            cancel();
        else if (mgr.buttonPressed("BACKTOSERVICE"))
            backToServe();
        else if ("AAAA".equals(mgr.readValue("BUTTONACCEPT")))
            accept();

        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")))
        {
            hyper = true;
            obj = mgr.readValue("MCH_CODE");
            cont = mgr.readValue("CONTRACT");
            startup();
        }
        else
            startup();


        adjust();

        ctx.writeFlag("AFTERACCEPT",afterAccept);
        ctx.writeNumber("SCR",scr);
        ctx.writeNumber("CNT",cnt);
        ctx.writeValue("OBJ",obj);
        ctx.writeValue("AGREEVAL",agreeVal);
        ctx.writeValue("CONT",cont);
        ctx.writeFlag("AGAIN",again);
        ctx.writeFlag("PREV",prev);
        ctx.writeValue("CUSTNO",custNo);
        ctx.writeValue("CUSTNAME",custName);
        ctx.writeValue("REPBY",repBy);
        ctx.writeValue("REPBYID",repById);
        ctx.writeValue("FLTREPFLG",fltRepFlg);
        ctx.writeValue("COMPNAME",compName);
        ctx.writeValue("ADDR1",addr1);
        ctx.writeValue("ADDR2",addr2);
        ctx.writeValue("ADDR3",addr3);
        ctx.writeValue("ADDR4",addr4);
        ctx.writeValue("ADDR5",addr5);
        ctx.writeValue("ADDR6",addr6);
        ctx.writeValue("ORGCODE",sOrgCode);
        ctx.writeValue("AGREEMENTID",agreementId);
        ctx.writeValue("AGREEMENTDESC",agreementDesc);
        ctx.writeValue("CONTRCT",contrct);
        ctx.writeValue("REG",reg);
        ctx.writeValue("OBJECT",object);
        ctx.writeValue("OBJDESC",objDesc);
        ctx.writeValue("WORKTYPEID",workTypeId);
        ctx.writeValue("FAULTREPAGREE",faultRepAgree);
        ctx.writeValue("FAULTREPAGREECLNT",faultRepAgreeClnt);
        ctx.writeValue("PLANHRS",planHrs);
        ctx.writeValue("AUTHCODE",authCode);
        ctx.writeValue("CONTACT",contact);      
        ctx.writeValue("PHONE_NO",phone_no);            
        ctx.writeValue("REFERENCE_NO",reference_no);    
        ctx.writeValue("ERR_DESCR_LO",err_descr_lo); 
        ctx.writeValue("SELECMCHCODE",selecMchCode);
        ctx.writeFlag("AGAINPRE",againPre);
        ctx.writeFlag("HYPER",hyper);
        ctx.writeValue("CHOSENOBJECT",chosenObject);   
        ctx.writeValue("CHOSENWKTYPE",chosenWkType);  
        ctx.writeValue("CHOSENAGREE",chosenAgree);
        ctx.writeValue("AGREINDEX",agreIndex);
	ctx.writeValue("CHOSENDEPT",sOrgCode);  
        ctx.writeFlag("MOREWRK",moreWrk);
        ctx.writeFlag("NODEPT",noDept);
        ctx.writeFlag("OBJECTCHANGED",objectChanged);
        ctx.writeFlag("CTSSHHTML",showHtmlPart);
        ctx.writeFlag("CTXTODEF",toDefaultPage);
	ctx.writeValue("CONTRACTID", contractId);
	ctx.writeValue("LINENO", lineNo );
	ctx.writeValue("CONTRACTNAME", sContractName );
	ctx.writeValue("LINEDESC", sLineDesc );
	ctx.writeValue("CONTRACTTYPE", sContractType );
	ctx.writeValue("INVOICETYPE", sInvoiceType );
	ctx.writeValue("AGREEMENTID", agreementId );
	ctx.writeValue("AGREEMENTDESC", agreementDesc );

        if (showHtmlPart)
        {
            ctx.writeBuffer("WRKTYPEBUFF",wrkTypeBuff);  
            ctx.writeBuffer("DEPTBUFF",deptBuff);
            ctx.writeBuffer("NEWAGRMNT",agreeBuffer);
        }
        //Bug Id 70012, Start
        ctx.writeFlag("PCMSCIEXIST", bPcmsciExist);
        //Bug Id 70012, End

    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000


    public boolean  checksec( String method,int ref,String[] isSecure1)
    {
        ASPManager mgr = getASPManager();
        String first=null;     

        isSecure1[ref] = "false" ;
        String splitted[] = split(method,".");

        first = splitted[0].toString();
        String Second = splitted[1].toString();

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(first,Second);
        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure1[ref] = "true";
            return true; 
        }
        else
            return false;
    }
    public void startup()
    {
        ASPManager mgr = getASPManager();

        wrkTypeBuff = mgr.newASPBuffer();
        agreeBuffer = mgr.newASPBuffer();
        deptBuff    = mgr.newASPBuffer();

        cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        if (!again)
        {
            cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");
            cmd = trans.addCustomFunction("REP","Person_Info_API.Get_Id_For_User","REPORTED_BY");
            cmd.addReference("FND_USER","FNDUSER/DATA");

            cmd = trans.addCustomFunction("REPID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addReference("COMPANY","HEAD/DATA");
            cmd.addReference("REPORTED_BY","REP/DATA");

            cmd = trans.addCustomFunction("COMPNAME","Company_api.Get_name","COMPANY_NAME");
            cmd.addReference("COMPANY","HEAD/DATA"); 
        }

        trans = mgr.submit(trans);

        data = trans.getBuffer("HEAD/DATA");

        if (!again)
        {
            data.setFieldItem("REPORTED_BY",trans.getValue("REP/DATA/REPORTED_BY"));
            data.setFieldItem("REPORTED_BY_ID",trans.getValue("REPID/DATA/REPORTED_BY_ID"));
            data.setFieldItem("FAULT_REP_FLAG","1");
            data.setFieldItem("COMPANY_NAME",trans.getValue("COMPNAME/DATA/COMPANY_NAME"));
        }

        headset.addRow(data);

        if (!again)
            storeVals();

    }


    public void storeVals()
    {

        repBy = headset.getRow().getValue("REPORTED_BY");                                                
        repById = headset.getRow().getValue("REPORTED_BY_ID");                                          
        fltRepFlg = headset.getRow().getValue("FAULT_REP_FLAG");                                        
        compName = headset.getRow().getValue("COMPANY_NAME");    

        addr1 = headset.getRow().getValue("ADDRESS1");                                                  
        addr2 = headset.getRow().getValue("ADDRESS2");                                                  
        addr3 = headset.getRow().getValue("ADDRESS3");                                                  
        addr4 = headset.getRow().getValue("ADDRESS4");                                                  
        addr5 = headset.getRow().getValue("ADDRESS5");                                                  
        addr6 = headset.getRow().getValue("ADDRESS6");                                                  
        sOrgCode = headset.getRow().getValue("ORG_CODE");                                                
        contrct = headset.getRow().getValue("CONTRACT");  
        reg = headset.getRow().getValue("REG_DATE");                                                    
        object = headset.getRow().getValue("MCH_CODE");                                                 
        objDesc = headset.getRow().getValue("MCH_CODE_DESCRIPTION");                                      
        workTypeId = headset.getRow().getValue("WORK_TYPE_ID");                                         
        faultRepAgree = headset.getRow().getValue("FAULT_REP_AGREE");                                   
        faultRepAgreeClnt = headset.getRow().getValue("FAULT_REP_AGREE_CLIENT");                      
        planHrs = headset.getRow().getValue("PLAN_HRS");                                                
        authCode = headset.getRow().getValue("AUTHORIZE_CODE");
	contractId = headset.getRow().getValue("CONTRACT_ID");
	lineNo     = headset.getRow().getValue("LINE_NO");
	sContractName  = headset.getRow().getValue("CONTRACT_NAME");
	sLineDesc      = headset.getRow().getValue("LINE_DESC");
	sContractType  = headset.getRow().getValue("CONTRACT_TYPE");
	sInvoiceType   = headset.getRow().getValue("INVOICE_TYPE");
	agreementId    = headset.getRow().getValue("AGREEMENT_ID");
	agreementDesc  = headset.getRow().getValue("CUSTOMERAGREEMENTDESCRIPTION");

        trans.clear();
    }

//-----------------------------------------------------------------------------
//------------------------  VALIDATE FUNCTION  --------------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        int ref;
        ASPManager mgr = getASPManager();
        isSecure = new String[7];
        String first="";


        val = mgr.readValue("VALIDATE");

        if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("REPORTED_BY");

            trans = mgr.validate(trans);

            String reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
            txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^";
            mgr.responseWrite(txt);
        }


        else if ("CUSTOMER_NO".equals(val))
        {
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("Cust_Ord_Customer");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
            {
                cmd = trans.addCustomFunction("ISCRESTOP","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","CREDITSTOP");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("COMPANY");
            }

            cmd = trans.addCustomFunction("CUSTNAME","CUSTOMER_INFO_API.Get_Name","CUSTOMERNAME");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
                txt = trans.getValue("ISCRESTOP/DATA/CREDITSTOP") +"^"+ trans.getValue("CUSTNAME/DATA/CUSTOMERNAME") +"^" ;
            else
                txt = "2" +"^"+ trans.getValue("CUSTNAME/DATA/CUSTOMERNAME") +"^";

            mgr.responseWrite(txt);
        }


        else if ("MCH_CODE".equals(val))
        {
            String whcond = "MCH_CODE in ( ? )";
            String whcond2 = "SELECT COUNT(IDENTITY) FROM EQUIPMENT_OBJECT_PARTY WHERE "+whcond+ 
                             " GROUP BY IDENTITY HAVING IDENTITY= ?";

            // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery("CONTRA","EQUIPMENT_ALL_OBJECT","CONTRACT",whcond,"");
	    q.addParameter("MCH_CODE" ,mgr.readValue("MCH_CODE") );
            q = trans.addQuery("VALID",whcond2);
	    q.addParameter("IDENTITY" ,mgr.readValue("CUSTOMER_NO"));

            trans = mgr.perform(trans);

            String contra = trans.getValue("CONTRA/DATA/CONTRACT");

            String XX = trans.getValue("VALID/DATA/COUNT(IDENTITY)");
            if ((int)trans.getNumberValue("VALID/DATA/COUNT(IDENTITY)") == 0)
            {
                txt = "No_Data_Found" + mgr.translate("PCMWACTIVESEPARATEWIZB2ENODAT: Not a valid object Id for the customer");
                mgr.responseWrite(txt);
            }
            else if ((int)trans.getNumberValue("VALID/DATA/COUNT(IDENTITY)") > 1)
            {
                txt = "No_Data_Found" + mgr.translate("PCMWACTIVESEPARATEWIZB2EMULDAT: More than one site connected to the object Id, use Search Object button for details");
                mgr.responseWrite(txt);
            }
            else
            {
                trans.clear();

		if (checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure)){
		    cmd = trans.addCustomFunction("STETIME","Psc_Contr_Product_API.Get_Setting_Time","SETTING_TIME");      
		    cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
		    cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));                         
		}

                cmd = trans.addCustomFunction("MCHNAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");              
                cmd.addParameter("CONTRACT",contra);                                                                            
                cmd.addParameter("MCH_CODE");                                                                                   

                cmd = trans.addCustomFunction("DEFADDR","Equipment_Object_Address_API.Get_Default_Address_Id","ADDRESS_ID");
                cmd.addParameter("CONTRACT",contra);                                                                            
                cmd.addParameter("MCH_CODE");                                                                                   

                cmd = trans.addCustomFunction("ADDR1","Equipment_Object_Address_API.Get_Address1","ADDRESS1");              
                cmd.addParameter("CONTRACT",contra);                                                                            
                cmd.addParameter("MCH_CODE");                                                                                   
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                                  

                cmd = trans.addCustomFunction("ADDR2","Equipment_Object_Address_API.Get_Address2","ADDRESS2"); 
                cmd.addParameter("CONTRACT",contra);                                                               
                cmd.addParameter("MCH_CODE");                                                                      
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                     

                cmd = trans.addCustomFunction("ADDR3","Equipment_Object_Address_API.Get_Address3","ADDRESS3"); 
                cmd.addParameter("CONTRACT",contra);                                                               
                cmd.addParameter("MCH_CODE");                                                                      
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                     

                cmd = trans.addCustomFunction("ADDR4","Equipment_Object_Address_API.Get_Address4","ADDRESS4"); 
                cmd.addParameter("CONTRACT",contra);                                                               
                cmd.addParameter("MCH_CODE");                                                                      
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                     

                cmd = trans.addCustomFunction("ADDR5","Equipment_Object_Address_API.Get_Address5","ADDRESS5"); 
                cmd.addParameter("CONTRACT",contra);                                                               
                cmd.addParameter("MCH_CODE");                                                                      
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                     

                cmd = trans.addCustomFunction("ADDR6","Equipment_Object_Address_API.Get_Address6","ADDRESS6"); 
                cmd.addParameter("CONTRACT",contra);                                                               
                cmd.addParameter("MCH_CODE");                                                                      
                cmd.addReference("ADDRESS_ID","DEFADDR/DATA");                                                     

                trans = mgr.validate(trans);                                                                   

                String settime = trans.getValue("STETIME/DATA/SETTING_TIME");                                         
                String mchName = trans.getValue("MCHNAME/DATA/MCH_CODE_DESCRIPTION");                                       

                addr1 = trans.getValue("ADDR1/DATA/ADDRESS1");  
                addr2 = trans.getValue("ADDR2/DATA/ADDRESS2");  
                addr3 = trans.getValue("ADDR3/DATA/ADDRESS3");  
                addr4 = trans.getValue("ADDR4/DATA/ADDRESS4");  
                addr5 = trans.getValue("ADDR5/DATA/ADDRESS5");  
                addr6 = trans.getValue("ADDR6/DATA/ADDRESS6");  

                txt = (mgr.isEmpty(settime)? "" : settime) +"^"+
                      (mgr.isEmpty(mchName)? "" : mchName) +"^"+
                      (mgr.isEmpty(addr1)? "" : addr1) +"^"+
                      (mgr.isEmpty(addr2)? "" : addr2) +"^"+
                      (mgr.isEmpty(addr3)? "" : addr3) +"^"+
                      (mgr.isEmpty(addr4)? "" : addr4) +"^"+
                      (mgr.isEmpty(addr5)? "" : addr5) +"^"+
                      (mgr.isEmpty(addr6)? "" : addr6)+"^"+
                      (mgr.isEmpty(contra)? "" : contra)+"^";

                mgr.responseWrite(txt);
            }
        }

        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("GETCONTRACT","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("REPORTED_BY");

            trans = mgr.validate(trans);

            String reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
            txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^";
            mgr.responseWrite(txt);
        }
       
	else if ("CONTRACT_ID".equals(val))
        {
            String sContractId   = mgr.readValue("CONTRACT_ID");
            String sLineNo       = mgr.readValue("LINE_NO");
	    String sContractName = "";
	    String sLineDesc     = "";
	    String sContractType = "";
	    String sInvoiceType  = "";
	    String sAgreementId  = "";
	    String sAgreementDesc= "";
    
            if (sContractId.indexOf("^") > -1)
            {
                String strAttr = sContractId;
                sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
                sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
            }

            if (checksec("Psc_Contr_Product_Api.Get_Maint_Org",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETMAINORG","Psc_Contr_Product_Api.Get_Maint_Org", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID",sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
	    }

            if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo);
	    }

	    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo );
	    }

	    if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1,isSecure))
	    {
               cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
               cmd.addParameter("AGREEMENT_ID");
               cmd.addParameter("CUSTOMERAGREEMENTDESCRIPTION");
               cmd.addParameter("CONTRACT_ID",sContractId);
	    }
	    
	    trans = mgr.validate(trans);

	    sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sOrgCode       = trans.getValue("GETMAINORG/DATA/ORG_CODE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("CUSTOMERAGREEMENTDESCRIPTION");

	    txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +
		   (mgr.isEmpty(sContractName)?"":sContractName) + "^" + (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sOrgCode)?"":sOrgCode) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ;
            
	    mgr.responseWrite(txt);

	    ctx.writeValue("CHOSENDEPT",sOrgCode); 
        }

        else if ("LINE_NO".equals(val))
        {
            String sContractId   = mgr.readValue("CONTRACT_ID");
            String sLineNo       = mgr.readValue("LINE_NO");
	    String sContractName = "";
	    String sLineDesc     = "";
	    String sContractType = "";
	    String sInvoiceType  = "";
	    String sAgreementId  = "";
	    String sAgreementDesc= "";
        
	    if (sLineNo.indexOf("^") > -1)
            {
                String strAttr = sLineNo;
                sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
                sLineNo =  strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());                
            } 

	    if (checksec("Psc_Contr_Product_Api.Get_Maint_Org",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETMAINORG","Psc_Contr_Product_Api.Get_Maint_Org", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID",sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
	    }

            if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo);
	    }

	    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1,isSecure))
	    {
               cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo );
	    }

	    if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1,isSecure))
	    {
               cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
               cmd.addParameter("AGREEMENT_ID");
               cmd.addParameter("CUSTOMERAGREEMENTDESCRIPTION");
               cmd.addParameter("CONTRACT_ID",sContractId);
	    }


            trans = mgr.validate(trans);
        
	    sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sOrgCode       = trans.getValue("GETMAINORG/DATA/ORG_CODE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("CUSTOMERAGREEMENTDESCRIPTION");

	    txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +
		   (mgr.isEmpty(sContractName)?"":sContractName) + "^" + (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sOrgCode)?"":sOrgCode) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ;
		   
            mgr.responseWrite(txt);
        }
//      Bug 69047, Start
        if ("WORK_TYPE_ID".equals(val))
        {
      	  trans.clear();
           trans.addSecurityQuery("Psc_Contr_Product_API","Get_Valid_Service_Line");
           trans = mgr.perform(trans);
           if (trans.getSecurityInfo().itemExists("Psc_Contr_Product_API.Get_Valid_Service_Line"))
           {
					trans.clear();
               
               cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Get_Valid_Service_Line");
					//Bug Id 70921, Start
					cmd.addParameter("IS_VALID");
					//Bug Id 70921,End
					cmd.addParameter("CONTRACT_ID");
					cmd.addParameter("LINE_NO");
					cmd.addParameter("ORG_CODE");
					cmd.addParameter("SETTINGTIME");
					cmd.addParameter("AUTHORIZE_CODE");
					cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
					cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
					cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));
					cmd.addParameter("MCH_CODE_CONTRACT", mgr.readValue("MCH_CODE_CONTRACT"));
					cmd.addParameter("WORK_TYPE_ID",mgr.readValue("WORK_TYPE_ID"));
					cmd.addParameter("CURRENCY_CODE",mgr.readValue("CURRENCY_CODE",""));
					cmd.addParameter("REG_DATE",mgr.readValue("REG_DATE"));
					cmd.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
					trans = mgr.validate(trans);
					contractId = trans.getValue("SEARCHAGRMNT/DATA/CONTRACT_ID");
					lineNo = trans.getValue("SEARCHAGRMNT/DATA/LINE_NO");
           }
           trans.clear();
           txt = (mgr.isEmpty(contractId) ? "" : contractId)+ "^"
           		 +(mgr.isEmpty(lineNo) ? "" : lineNo)+ "^";
      	  mgr.responseWrite(txt);
        }
//      Bug 69047, End

        ctx.writeValue("CHOSENDEPT",sOrgCode); 

        mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void btnNext()
    {
        ASPManager mgr = getASPManager();
        String first="";
        isSecure = new String[7];
        String satis = "";
        String addr = "";

	secBuff = mgr.newASPTransactionBuffer();
	secBuff.addSecurityQuery("Psc_Contr_Product");

	secBuff = mgr.perform(secBuff);

        if (scr == 3)
        {
	    String selDefDept = null;

            chosenWkType  = mgr.readValue("WKTYPE");
	    contractId    = mgr.readValue("CONTRACT_ID");
	    lineNo        = mgr.readValue("LINE_NO");
	    sContractName = mgr.readValue("CONTRACT_NAME");
	    sLineDesc     = mgr.readValue("LINE_DESC");
	    sContractType = mgr.readValue("CONTRACT_TYPE");
	    sInvoiceType  = mgr.readValue("INVOICE_TYPE");
	    agreementId   = mgr.readValue("AGREEMENT_ID");
	    agreementDesc = mgr.readValue("CUSTOMERAGREEMENTDESCRIPTION");
            agreIndex     = mgr.readValue("SELAGREINDX");
	    sOrgCode      = mgr.readValue("ORG_CODE");

            if (!mgr.isEmpty(chosenAgree))
            {
                String splitted[] = split(chosenAgree," ");
                first = splitted[0].toString();
            }

            if (secBuff.getSecurityInfo().itemExists("Psc_Contr_Product")){
		selDefDept   = "SELECT maint_org from psc_contr_product WHERE contract_id='"+first+ "' AND mch_contract = '"+cont+"' AND mch_code='"+obj+ "'";                       
	    }

            String selDept      = "SELECT org_code,RPAD(org_code,10),' - ',RPAD(description,30) from org_code_allowed_site_lov where contract= ?";

            // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery("DEFDEPT",selDefDept);
            q = trans.addQuery("DEPT",selDept);
	    q.addParameter("CONTRACT" ,mgr.readValue("CONTRACT"));

            q.includeMeta("ALL");

            cmd = trans.addCustomFunction("WKDESC","WORK_TYPE_API.Get_Description","WORKTYPEDESC");
            cmd.addParameter("WORK_TYPE_ID",chosenWkType);

           
            trans = mgr.perform(trans);

            deptBuff    = trans.getBuffer("DEPT");                    

            if (!mgr.isEmpty(first) && !mgr.isEmpty(cont) && !mgr.isEmpty(obj))
            {
                chosenDept  = trans.getValue("DEFDEPT/DATA/ORG_CODE");
            }

            row = headset.getRow();
            row.setValue("WORK_TYPE_ID",chosenWkType);
	    row.setValue("CONTRACT_ID",contractId);
	    row.setValue("LINE_NO",lineNo);
	    row.setValue("CONTRACT_NAME",sContractName);
	    row.setValue("LINE_DESC",sLineDesc);
	    row.setValue("CONTRACT_TYPE",sContractType);
	    row.setValue("INVOICE_TYPE",sInvoiceType);
	    row.setValue("AGREEMENT_ID",agreementId);
	    row.setValue("CUSTOMERAGREEMENTDESCRIPTION",agreementDesc);
            row.setValue("WORKTYPEDESC",trans.getValue("WKDESC/DATA/WORKTYPEDESC"));
	    row.setValue("ORG_CODE", sOrgCode);

            if ("TRUE".equals(satis))
                row.setValue("CUSTOMERAGREEMENTDESCRIPTION",trans.getValue("AGREEDESC/DATA/CUSTOMERAGREEMENTDESCRIPTION"));

            headset.setRow(row);
            scr = scr+1;
        }

        if (scr == 2)
        {
	    String whCondWkType = null;
            String moreWkType = null; 

            err_descr_lo = mgr.readValue("ERR_DESCR_LO");
            contrct =    mgr.readValue("CONTRACT");
            
            if (!hyper)
                obj = mgr.readValue("ITEM0MCHCODE");

            if (secBuff.getSecurityInfo().itemExists("Psc_Contr_Product")){
		moreWkType   = "SELECT COUNT(DISTINCT work_type_id) FROM psc_contr_product where mch_code = '"+obj+"' AND mch_contract = ? ";  
	    }

            // SQLInjection_Safe ILSOLK 20070705
            trans.addQuery("WKTPID","SELECT work_type_id, RPAD(work_type_id,10),' - ',RPAD(description,30) FROM work_type");            
            q = trans.addQuery("MOREWKTYPE",moreWkType);
	    q.addParameter("MCH_CODE_CONTRACT" ,mgr.readValue("CONTRACT"));
            trans = mgr.perform(trans);               
            wrkTypeBuff = trans.getBuffer("WKTPID");

            if ((int)trans.getNumberValue("MOREWKTYPE/DATA/COUNT(DISTINCTWORK_TYPE_ID)") > 1)
                moreWrk = true;
            else
                moreWrk = false;

            finish();
            scr = scr+1;
            execute = "TRUE";
        }

        if (scr == 1)
        {

            if (hyper)
            {

                contact = mgr.readValue("CONTACT");     
                phone_no = mgr.readValue("PHONE_NO");        
                reference_no = mgr.readValue("REFERENCE_NO");
                custNo       = mgr.readValue("CUSTOMER_NO");
                custName     = mgr.readValue("CUSTOMERNAME");
                repBy        = mgr.readValue("REPORTED_BY");
                repById      = mgr.readValue("REPORTED_BY_ID");

                again = true;

                scr = scr+1;
                trans.clear();
                startup();
                again = false;
            }
            else
            {

                trans.clear();              

                contact      = mgr.readValue("CONTACT");
                phone_no     = mgr.readValue("PHONE_NO"); 
                reference_no = mgr.readValue("REFERENCE_NO");
                custNo       = mgr.readValue("CUSTOMER_NO");
                custName     = mgr.readValue("CUSTOMERNAME");
                repBy        = mgr.readValue("REPORTED_BY");
                repById      = mgr.readValue("REPORTED_BY_ID");

                again = true;

                addr = mgr.readValue("ADDRESS");
                if (!mgr.isEmpty(addr))
                    addr = addr.toUpperCase();

                if (mgr.isEmpty (custNo))   custNo  ="";

                q = trans.addQuery("ITEMBLK","select MCH_CODE,CONTRACT FROM EQUIPMENT_OBJECT_PARTY WHERE (IDENTITY = ? AND PARTY_TYPE = OBJECT_PARTY_TYPE_API.GET_CLIENT_VALUE(0) AND CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL))");
                q.addParameter("IDENTITY",custNo);
                q.setBufferSize(100000);
                trans = mgr.perform(trans);

                ASPBuffer itembuff = trans.getBuffer ("ITEMBLK");


                str = sepbuff(itembuff);

                if (!mgr.isEmpty(addr))
                {
                    trans.clear();

                    // SQLInjection_Safe ILSOLK 20070705
                    q = trans.addQuery("ITEM1BLK","select MCH_CODE,CONTRACT from EQUIPMENT_OBJECT_ADDRESS A WHERE (A.MCH_CODE,A.CONTRACT) in "+str+" AND UPPER(ADDRESS1) LIKE ? OR UPPER(ADDRESS2) LIKE ? OR UPPER(ADDRESS3) LIKE ? OR UPPER(ADDRESS4) LIKE ? OR UPPER(ADDRESS5) LIKE ? OR UPPER(ADDRESS6) LIKE ?");
                    for (int i = 0; i < itembuff.countItems()-1; i++)
		    {
			ASPBuffer temp = itembuff.getBufferAt(i);
			q.addParameter("MCH_CODE",temp.getValueAt(0));
			q.addParameter("CONTRACT",temp.getValueAt(1));
		    }
                    q.addParameter("ATR1","%"+addr+"%");
		    q.addParameter("ATR1","%"+addr+"%");
		    q.addParameter("ATR1","%"+addr+"%");
		    q.addParameter("ATR1","%"+addr+"%");
		    q.addParameter("ATR1","%"+addr+"%");
		    q.addParameter("ATR1","%"+addr+"%");
		    q.setBufferSize(100000);
                    trans = mgr.perform(trans);
                    ASPBuffer item1buff = trans.getBuffer ("ITEM1BLK");
                    
                    str = sepbuff(item1buff);
                }

                trans.clear();   

                if (!mgr.isEmpty(addr))
		{
                    // SQLInjection_Safe ILSOLK 20070705
                    q = trans.addQuery("ITEM0BLK","select A.OBJID,A.OBJVERSION,A.CONTRACT HYP_CONTRACT,A.MCH_CODE,A.MCH_NAME,A.CONTRACT,A.SERIAL_NO,A.MCH_TYPE,A.GROUP_ID,A.CATEGORY_ID,B.ADDRESS1 ATR1,B.ADDRESS2 ATR2,B.ADDRESS3 ATR3,B.ADDRESS4 ATR4,B.ADDRESS5 ATR5,B.ADDRESS6 ATR6 from EQUIPMENT_ALL_OBJECT A, EQUIPMENT_OBJECT_ADDRESS B WHERE B.MCH_CODE = A.MCH_CODE and B.CONTRACT = A.CONTRACT and A.IS_CATEGORY_OBJECT != 'TRUE' AND (A.MCH_CODE,A.CONTRACT) in "+str+"");
		    for (int i = 0; i < itembuff.countItems()-1; i++)
		    {
			ASPBuffer temp = itembuff.getBufferAt(i);
			q.addParameter("MCH_CODE",temp.getValueAt(0));
			q.addParameter("CONTRACT",temp.getValueAt(1));
		    }
		}
                else
                {

                    // SQLInjection_Safe ILSOLK 20070705
                    q = trans.addQuery("ITEM0BLK","select DISTINCT(A.MCH_CODE) LST_ITEM_MCH_CODE, A.MCH_CODE LST_ITEM_MCH_CODE,' - ',A.CONTRACT LST_ITEM_CONTRACT from EQUIPMENT_ALL_OBJECT A, EQUIPMENT_OBJECT_ADDRESS B WHERE A.MCH_CODE = B.MCH_CODE(+) and A.CONTRACT = B.CONTRACT(+) and A.IS_CATEGORY_OBJECT != 'TRUE'  AND (A.MCH_CODE,A.CONTRACT) in "+str+""); 
		    for (int i = 0; i < itembuff.countItems() -1 ; i++)
		    {
			ASPBuffer temp = itembuff.getBufferAt(i);
			q.addParameter("MCH_CODE",temp.getValueAt(0));
			q.addParameter("CONTRACT",temp.getValueAt(1));
		    }
                } 
                q.setBufferSize(100000);
                trans = mgr.perform(trans);

                objectbuff = trans.getBuffer("ITEM0BLK");

                ctx.writeBuffer("OBJECTBUFF",objectbuff);

                scr = scr+1;

                trans.clear();
                startup();
                again = false;
            }  
        }


    }

    public void setbuff()
    {
        ASPManager mgr = getASPManager();
        String addr = "";
        addr = mgr.readValue("ADDRESS");
        if (!mgr.isEmpty(addr))
            addr = addr.toUpperCase();

        if (mgr.isEmpty (custNo)) custNo  ="";

        q = trans.addQuery("ITEMBLK","select MCH_CODE,CONTRACT FROM EQUIPMENT_OBJECT_PARTY WHERE (IDENTITY = ? AND PARTY_TYPE = OBJECT_PARTY_TYPE_API.GET_CLIENT_VALUE(0) AND CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL))");
        q.addParameter("IDENTITY",custNo);
        q.setBufferSize(100000);
        trans = mgr.perform(trans);

        ASPBuffer itembuff = trans.getBuffer ("ITEMBLK");

        str = sepbuff(itembuff);

        if (!mgr.isEmpty(addr))
        {
            trans.clear();

            // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery("ITEM1BLK","select MCH_CODE,CONTRACT from EQUIPMENT_OBJECT_ADDRESS A WHERE (A.MCH_CODE,A.CONTRACT) in "+str+" AND UPPER(ADDRESS1) LIKE ? OR UPPER(ADDRESS2) LIKE ? OR UPPER(ADDRESS3) LIKE ? OR UPPER(ADDRESS4) LIKE ? OR UPPER(ADDRESS5) LIKE ? OR UPPER(ADDRESS6) LIKE ?");
	    for (int i = 0; i < itembuff.countItems()-1; i++)
	    {
		ASPBuffer temp = itembuff.getBufferAt(i);
		q.addParameter("MCH_CODE",temp.getValueAt(0));
		q.addParameter("CONTRACT",temp.getValueAt(1));
	    }
	    q.addParameter("ATR1","%"+addr+"%");
	    q.addParameter("ATR1","%"+addr+"%");
	    q.addParameter("ATR1","%"+addr+"%");
	    q.addParameter("ATR1","%"+addr+"%");
	    q.addParameter("ATR1","%"+addr+"%");
	    q.addParameter("ATR1","%"+addr+"%");
            q.setBufferSize(100000);
            trans = mgr.perform(trans);
            ASPBuffer item1buff = trans.getBuffer ("ITEM1BLK");

            str = sepbuff(item1buff);
        }

        trans.clear();   

        if (!mgr.isEmpty(addr))
	{
            // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery("ITEM0BLK","select A.OBJID,A.OBJVERSION,A.CONTRACT HYP_CONTRACT,A.MCH_CODE,A.MCH_NAME,A.CONTRACT,A.SERIAL_NO,A.MCH_TYPE,A.GROUP_ID,A.CATEGORY_ID,B.ADDRESS1 ATR1,B.ADDRESS2 ATR2,B.ADDRESS3 ATR3,B.ADDRESS4 ATR4,B.ADDRESS5 ATR5,B.ADDRESS6 ATR6 from EQUIPMENT_ALL_OBJECT A, EQUIPMENT_OBJECT_ADDRESS B WHERE B.MCH_CODE = A.MCH_CODE and B.CONTRACT = A.CONTRACT and A.IS_CATEGORY_OBJECT != 'TRUE' AND (A.MCH_CODE,A.CONTRACT) in "+str+"");
	    for (int i = 0; i < itembuff.countItems()-1; i++)
	    {
		ASPBuffer temp = itembuff.getBufferAt(i);
		q.addParameter("MCH_CODE",temp.getValueAt(0));
		q.addParameter("CONTRACT",temp.getValueAt(1));
	    }
	}
        else
        {

	    // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery("ITEM0BLK","select DISTINCT(A.MCH_CODE) LST_ITEM_MCH_CODE, A.MCH_CODE LST_ITEM_MCH_CODE,' - ',A.CONTRACT LST_ITEM_CONTRACT from EQUIPMENT_ALL_OBJECT A, EQUIPMENT_OBJECT_ADDRESS B WHERE A.MCH_CODE = B.MCH_CODE(+) and A.CONTRACT = B.CONTRACT(+) and A.IS_CATEGORY_OBJECT != 'TRUE' AND (A.MCH_CODE,A.CONTRACT) in "+str+""); 
	    for (int i = 0; i < itembuff.countItems()-1; i++)
	    {
		ASPBuffer temp = itembuff.getBufferAt(i);
		q.addParameter("MCH_CODE",temp.getValueAt(0));
		q.addParameter("CONTRACT",temp.getValueAt(1));
	    }
        } 
        q.setBufferSize(100000);
        trans = mgr.perform(trans);

        objectbuff = trans.getBuffer("ITEM0BLK");

        ctx.writeBuffer("OBJECTBUFF",objectbuff);
    }

    public String sepbuff(ASPBuffer itembuff1)

    {

        ASPBuffer data_sub_buff_;
        String data_sub_buff_name;
        String mchCode_="";
        String contract_="";
        int n = itembuff1.countItems();
        int count_=0;
        String str = "(('','')";

        for (i=0;i<n-1;i++)
        {

            data_sub_buff_ = itembuff1.getBufferAt(i);
            data_sub_buff_name = itembuff1.getNameAt(i);

            if ("DATA".equals(data_sub_buff_name))
            {
                mchCode_ = data_sub_buff_.getValueAt(0);
                contract_ = data_sub_buff_.getValueAt(1);
            }

            if (i==0)
            {
                //str = "(('"+mchCode_+"','"+contract_+"')";
		str = "(( ?, ?)";
            }
            else
                str = str + ", ( ?, ? )";



        }
        str = str + ")";

        return str;
    }



    public void finish()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer data_sub_buff_;
        String data_sub_buff_name;
        String mchCode_="";
        String contract_="";

        if (hyper)
            cancelQry();
        else
        {
            obj = mgr.readValue("ITEM0MCHCODE");

            if (objectbuff.countItems()>0)
            {
                int sSelectN = (int)mgr.readNumberValue("PNT");
                if (sSelectN > 0)
                {
                    int nSelectNo = sSelectN - 1; 

                    data_sub_buff_ = objectbuff.getBufferAt(nSelectNo);
                    data_sub_buff_name = objectbuff.getNameAt(nSelectNo);

                    if ("DATA".equals(data_sub_buff_name))
                    {
                        mchCode_ = data_sub_buff_.getValueAt(0);
                        contract_ = data_sub_buff_.getValueAt(3);

                    }
                    cont = contract_  ;
                }
            }


            cancelQry();  
        } 

    }


    public void cancelQry()
    {
        ASPManager mgr = getASPManager();

        if (!mgr.isEmpty(obj))
        {
            trans1.clear();

            cmd = trans1.addCustomFunction("MCHNAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");            
            cmd.addParameter("CONTRACT",cont);                                                                            
            cmd.addParameter("MCH_CODE",obj);          

            cmd = trans1.addCustomFunction("DEFADDR","Equipment_Object_Address_API.Get_Default_Address_Id","ADDRESS_ID");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);

            cmd = trans1.addCustomFunction("ADDR1","Equipment_Object_Address_API.Get_Address1","ADDRESS1");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA");

            cmd = trans1.addCustomFunction("ADDR2","Equipment_Object_Address_API.Get_Address2","ADDRESS2");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA");

            cmd = trans1.addCustomFunction("ADDR3","Equipment_Object_Address_API.Get_Address3","ADDRESS3");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA");

            cmd = trans1.addCustomFunction("ADDR4","Equipment_Object_Address_API.Get_Address4","ADDRESS4");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA");

            cmd = trans1.addCustomFunction("ADDR5","Equipment_Object_Address_API.Get_Address5","ADDRESS5");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA") ;

            cmd = trans1.addCustomFunction("ADDR6","Equipment_Object_Address_API.Get_Address6","ADDRESS6");
            cmd.addParameter("CONTRACT",cont);
            cmd.addParameter("MCH_CODE",obj);
            cmd.addReference("ADDRESS_ID","DEFADDR/DATA");

            trans1 = mgr.perform(trans1);

            objDesc = trans1.getValue("MCHNAME/DATA/MCH_CODE_DESCRIPTION");

        }

        temp = headset.getRow();

        temp.setValue("CONTACT",contact);                         
        temp.setValue("PHONE_NO",phone_no);
        temp.setValue("REFERENCE_NO",reference_no);
        temp.setValue("CONTRACT",contrct);


        if (!mgr.isEmpty(obj))
        {
            temp.setValue("ADDRESS1",trans1.getValue("ADDR1/DATA/ADDRESS1"));
            temp.setValue("ADDRESS2",trans1.getValue("ADDR2/DATA/ADDRESS2"));
            temp.setValue("ADDRESS3",trans1.getValue("ADDR3/DATA/ADDRESS3"));
            temp.setValue("ADDRESS4",trans1.getValue("ADDR4/DATA/ADDRESS4"));
            temp.setValue("ADDRESS5",trans1.getValue("ADDR5/DATA/ADDRESS5"));
            temp.setValue("ADDRESS6",trans1.getValue("ADDR6/DATA/ADDRESS6"));
            temp.setValue("MCH_CODE",obj);
            temp.setValue("MCH_CODE_CONTRACT",cont);
        }
        else
        {
            temp.setValue("ADDRESS1",addr1);
            temp.setValue("ADDRESS2",addr2);
            temp.setValue("ADDRESS3",addr3);
            temp.setValue("ADDRESS4",addr4);
            temp.setValue("ADDRESS5",addr5);
            temp.setValue("ADDRESS6",addr6);
            temp.setValue("MCH_CODE",object);
            temp.setValue("MCH_CODE_CONTRACT","");

        }

        temp.setValue("CUSTOMER_NO",custNo);  
        temp.setValue("CUSTOMERNAME",custName);
        temp.setValue("REPORTED_BY",repBy);
        temp.setValue("REPORTED_BY_ID",repById);
        temp.setValue("FAULT_REP_FLAG",fltRepFlg);
        temp.setValue("COMPANY_NAME",compName);
        temp.setValue("ORG_CODE",sOrgCode);
        temp.setValue("CUST_ORDER_TYPE","SEO");     
        
	temp.setValue("REG_DATE",reg);
        temp.setValue("ERR_DESCR_LO",err_descr_lo);
        temp.setValue("MCH_CODE_DESCRIPTION",objDesc);

        temp.setValue("WORK_TYPE_ID",workTypeId);
        temp.setValue("FAULT_REP_AGREE",faultRepAgree);
        temp.setValue("FAULT_REP_AGREE_CLIENT",faultRepAgreeClnt);
        temp.setValue("PLAN_HRS",planHrs);
        temp.setValue("AUTHORIZE_CODE",authCode);
	temp.setValue("CONTRACT_ID", contractId);
	temp.setValue("LINE_NO", lineNo);
	temp.setValue("CONTRACT_NAME", sContractName);
	temp.setValue("LINE_DESC", sLineDesc);
	temp.setValue("CONTRACT_TYPE", sContractType);
	temp.setValue("INVOICE_TYPE", sInvoiceType);
	temp.setValue("AGREEMENT_ID",agreementId);
        temp.setValue("CUSTOMERAGREEMENTDESCRIPTION",agreementDesc);

        headset.setRow(temp);
    }


    public void btnPrevious()
    {
        ASPManager mgr = getASPManager();

        scr = scr-1;

        if (scr == 1)
        {
            againPre = true;

            err_descr_lo = mgr.readValue("ERR_DESCR_LO");
            contrct = mgr.readValue("CONTRACT");
	    
            if (!hyper)
                chosenObject = mgr.readValue("ITEM0MCHCODE");

        }

        if (scr == 2)
        {
            chosenWkType = mgr.readValue("WKTYPE");
	    contractId   = mgr.readValue("CONTRACT_ID");
	    lineNo       = mgr.readValue("LINE_NO");
	    sContractName = mgr.readValue("CONTRACT_NAME");
	    sLineDesc     = mgr.readValue("LINE_DESC");
	    sInvoiceType  = mgr.readValue("CONTRACT_TYPE");
	    sInvoiceType  = mgr.readValue("INVOICE_TYPE");
	    agreementId   = mgr.readValue("AGREEMENT_ID");
	    agreementDesc = mgr.readValue("CUSTOMERAGREEMENTDESCRIPTION");
            chosenAgree   = mgr.readValue("NEWAGRMNT");
            agreIndex     = mgr.readValue("SELAGREINDX");

            setbuff();
            chosenObject = obj;
            headlay.setDialogColumns(1);
            itemlay.setDialogColumns(1);
            itemlay0.setDialogColumns(1);
            scr = 2;
        }

        if (scr == 3)
        {
	    sOrgCode = mgr.readValue("DEPT");
	    execute = "TRUE";                       
        }
    }


    public void cancel()
    {

        afterAccept = false;
        closeWin = "TRUE";
    }


    public void backToServe()
    {

        scr = scr-2;

        if (scr == 2)
            chosenObject = obj;
    }

    public String addNumToDate1(String dt,double num)
    {
        ASPManager mgr = getASPManager();
        ASPBuffer buf = mgr.newASPBuffer();
        buf.addFieldItem("PLAN_S_DATE",dt);      

        trans12.clear();
       q = trans12.addQuery("DATEAFTER","select ? + ? DUMMY_PLAN_F_DATE from DUAL");
        q.addParameter("PLAN_S_DATE",buf.getFieldValue("PLAN_S_DATE"));
        q.addParameter("PLAN_HRS",num/24+"");
        q.includeMeta("ALL");
        trans12 = mgr.perform(trans12);
        String output = trans12.getValue("DATEAFTER/DATA/DUMMY_PLAN_F_DATE");

        trans12.clear();

        return output;
    }

    public String addNumToDate(String dt,double num)
    {
        ASPManager mgr = getASPManager();
        ASPBuffer buf = mgr.newASPBuffer();
        buf.addFieldItem("PLAN_S_DATE",dt);      

        trans12.clear();
        q = trans12.addQuery("DATEAFTER","select ? + ? DUMMY_PLAN_F_DATE from DUAL");
        q.addParameter("PLAN_S_DATE",buf.getFieldValue("PLAN_S_DATE"));
        q.addParameter("PLAN_HRS",num/24+"");
        q.includeMeta("ALL");
        trans12 = mgr.perform(trans12);

        String output = trans12.getBuffer("DATEAFTER/DATA").getFieldValue("DUMMY_PLAN_F_DATE");

        trans12.clear();

        return output;
    }

    public void accept()
    {
        ASPManager mgr = getASPManager();
        String first="";
        String sel = "";
        isSecure   = new String[7];
        trans.clear(); 

        chosenDept  = mgr.readValue("DEPT");
	
        if (!mgr.isEmpty(chosenAgree))
        {
            String splitted[] = split(chosenAgree," ");
            first = splitted[0].toString();
        }
	
	if (checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure)){
	    cmd = trans.addCustomFunction("STETTIME","Psc_Contr_Product_API.Get_Setting_Time","SETTING_TIME");
	    cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
	    cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
	}
	if (checksec("Psc_Contr_Product_API.Get_Max_Resolution_Time",4,isSecure)){
	    cmd = trans.addCustomFunction("EXETIME","Psc_Contr_Product_API.Get_Max_Resolution_Time","PLAN_HRS");
	    cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
	    cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
	}

        trans = mgr.perform(trans);

        double settime = trans.getNumberValue("STETTIME/DATA/SETTING_TIME");
        double execTime = trans.getNumberValue("EXETIME/DATA/PLAN_HRS");
        String sExecTime = trans.getValue("EXETIME/DATA/PLAN_HRS");

        String sPlannedStart = mgr.readValue("REG_DATE");
        String sPlannedCompletion = null;
        String sReqStart = null;
        String sReqEnd = null;

	if (( (int)settime > 0 ) && ( (int)execTime > 0 ))
	{
           sPlannedCompletion = addNumToDate(mgr.readValue("REG_DATE"),execTime+settime);
           sReqStart = addNumToDate(mgr.readValue("REG_DATE"),settime);
           sReqEnd = addNumToDate(sReqStart,execTime); 
	}

        ASPBuffer buf1 = mgr.newASPBuffer();
        buf1.setFieldItem("PLAN_S_DATE",sPlannedStart);
        buf1.setFieldItem("PLAN_F_DATE",sPlannedCompletion);
        buf1.setFieldItem("REQUIRED_START_DATE",sReqStart);
        buf1.setFieldItem("REQUIRED_END_DATE",sReqEnd);

        sPlannedStart = buf1.getValue("PLAN_S_DATE");
        sPlannedCompletion = buf1.getValue("PLAN_F_DATE");
        sReqStart = buf1.getValue("REQUIRED_START_DATE");
        sReqEnd = buf1.getValue("REQUIRED_END_DATE");

        trans.clear();

        if ((int)settime > 0)
        {
            String dateMask = mgr.getFormatMask("Datetime",true);

            sel = addNumToDate1(mgr.readValue("REG_DATE"),settime);
        }

        desc = mgr.readValue("ERR_DESCR_LO");
        int leng = desc.length();
        headset.changeRow(); 

        temp = headset.getRow();
        if (leng > 60)
            temp.setValue("ERR_DESCR",desc.substring(0,59));
        else
            temp.setValue("ERR_DESCR",desc); 

	temp.setValue("ORG_CODE",chosenDept);
        temp.setValue("PLAN_S_DATE",mgr.isEmpty(sPlannedStart)?"":sPlannedStart);
        temp.setValue("PLAN_F_DATE",mgr.isEmpty(sPlannedCompletion)?"":sPlannedCompletion);
        temp.setValue("REQUIRED_START_DATE",mgr.isEmpty(sReqStart)?"":sReqStart);
        temp.setValue("REQUIRED_END_DATE",mgr.isEmpty(sReqEnd)?"":sReqEnd);
        temp.setValue("PLAN_HRS",mgr.isEmpty(sExecTime)?"":sExecTime);

        temp.setValue("CONNECTION_TYPE_DB","EQUIPMENT");

        trans.clear();

        headset.setRow(temp);
        currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        afterAccept = true;
        headset.goTo(currrow);
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("OBJSTATE").
        setHidden();

        headblk.addField("OBJEVENTS").
        setHidden();

        headblk.addField("WO_NO").
        setSize(40).
        setLabel("PCMWACTIVESEPARATEWIZB2EWONO: WO No:");

        headblk.addField("WO_NO_LIST").
        setHidden().
        setFunction("''");

        headblk.addField("FND_USER").
        setFunction("Fnd_Session_Api.Get_Fnd_User").
        setHidden();

        headblk.addField("NAME").
        setFunction("''").
        setHidden();

        headblk.addField("CUSTOMER_NO").
        setSize(10).
        setMaxLength(20).
        setLabel("PCMWACTIVESEPARATEWIZB2ECUSTOMERNO: Customer ID:").
        setCustomValidation("CUSTOMER_NO,COMPANY","CREDITSTOP,CUSTOMERNAME").
        setUpperCase();

        headblk.addField("CUSTOMERNAME").
        setSize(30).
        setLabel("PCMWACTIVESEPARATEWIZB2ECUSTOMERNAME: Customer Name:").
        setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)").
        setReadOnly();  

        headblk.addField("CREDITSTOP").
        setFunction("''").
        setHidden();

        headblk.addField("CONTACT").
        setSize(30).
        setMaxLength(30).
        setLabel("PCMWACTIVESEPARATEWIZB2ECONTACT: Contact Person:");

        headblk.addField("REFERENCE_NO").
        setSize(18).
        setMaxLength(25).
        setLabel("PCMWACTIVESEPARATEWIZB2EREFERENCENO: Reference No:");

        headblk.addField("PHONE_NO").
        setSize(18).
        setMaxLength(20).
        setLabel("PCMWACTIVESEPARATEWIZB2EPHONENO: Phone No:");

        headblk.addField("REPORTED_BY").
        setSize(8).
        setMandatory().
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWACTIVESEPARATEWIZB2EREPORTEDBY: Reported by:").
        setMaxLength(20).
        setCustomValidation("REPORTED_BY,COMPANY","REPORTED_BY_ID").
        setUpperCase();

        headblk.addField("REG_DATE","Datetime").
        setSize(22).
        setMandatory().
        setMaxLength(100).
        setLabel("PCMWACTIVESEPARATEWIZB2EREGDATE: Reg. Date:").
        setReadOnly();  

        headblk.addField("ERR_DESCR_LO").
        setLabel("PCMWACTIVESEPARATEWIZB2EERRDESCR: Service Description:").
        setHeight(2).
        setSize(40).
        setMandatory();  

        headblk.addField("CONTRACT").
        setSize(8).
        setMaxLength(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setMandatory().
        setUpperCase().
        setLabel("PCMWACTIVESEPARATEWIZB2EWOCONTRACT: WO Site:");

        headblk.addField("CONNECTION_TYPE_DB").
        setHidden();

        headblk.addField("MCH_CODE").
        setSize(28).
        setMaxLength(100).
        setHidden().
        setCustomValidation("AGREEMENT_ID,MCH_CODE,WORK_TYPE_ID,ADDRESS_ID,CUSTOMER_NO","SETTING_TIME,MCH_CODE_DESCRIPTION,ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6,MCH_CODE_CONTRACT").
        setLabel("PCMWACTIVESEPARATEWIZB2EMCHCODE: Object ID:").
        setUpperCase();

        headblk.addField("MCH_CODE_DESCRIPTION").
        setHidden().
        setSize(44).
        setReadOnly();

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(8).
        setReadOnly().
        setHidden().
        setMaxLength(5).
        setLabel("PCMWACTIVESEPARATEWIZB2EOBJCONTRACT: Object Site:");

        headblk.addField("ADDRESS1").
        setSize(28).
        setHidden().
        setMaxLength(35).
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS1: Address 1/2:");

        headblk.addField("ADDRESS2").
        setSize(29).
        setHidden().
        setMaxLength(35).
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS2: Address 2:");

        headblk.addField("ADDRESS3").
        setSize(28).
        setHidden().
        setMaxLength(35).
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS3: Address 3/4:");

        headblk.addField("ADDRESS4").
        setSize(29).
        setMaxLength(35).
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS4: Address 4:");

        headblk.addField("ADDRESS5").
        setSize(28).
        setMaxLength(35).
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS5: Address 5/6:");

        headblk.addField("ADDRESS6").
        setSize(29).
        setHidden().
        setMaxLength(35).
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS6: Address 6:");

        headblk.addField("ADDRESS_ID").
        setHidden();

        headblk.addField("ERR_DESCR").
        setMandatory().
        setHidden();

	//Bug 87935, Start, Modified code to get correct pres objects
	f = headblk.addField("CONTRACT_ID");
	f.setSize(9);
	f.setLabel("PCMWACTIVESEPARATEWIZB2ECONTRACTID: Contract ID:");
	f.setLOV("PscContrProductLov.page", "CONTRACT,CUSTOMER_NO,MCH_CODE,WORK_TYPE_ID");
	f.setLOVProperty("WHERE", "OBJSTATE='Active' AND FAULT_REPORT_AGREEMENT_DB = '2'");
	f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,ORG_CODE,AGREEMENT_ID,CUSTOMERAGREEMENTDESCRIPTION");
	f.setUpperCase();
	f.setMaxLength(10);
	//Bug 87935, End

   //Bug 84436, Start  
   f = headblk.addField("CONTRACT_NAME");                
   f. setDefaultNotVisible();    
   if (mgr.isModuleInstalled("SRVCON"))
      f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
   else
       f.setFunction("''");   
	 f.setReadOnly();
    f.setLabel("PCMWACTIVESEPARATEWIZB2ECONTRACTNAME: Contract Name:");
    f.setSize(30);
    //Bug 84436, End  

        //Bug 87935, Start, Modified code to get correct pres objects
        f = headblk.addField("LINE_NO");
	f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID,WORK_TYPE_ID");
	f.setLOVProperty("WHERE", "OBJSTATE='Active' AND FAULT_REPORT_AGREEMENT_DB = '2'");
	f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,ORG_CODE,AGREEMENT_ID,CUSTOMERAGREEMENTDESCRIPTION");
        f.setSize(9);
	f.setLabel("PCMWACTIVESEPARATEWIZB2ELINENO: Line No:");
	//Bug 87935, End

        //Bug 84436, Start
        f = headblk.addField("LINE_DESC");                     
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEWIZB2ELINEDESC: Description:");
        f.setSize(30);
	
        f = headblk.addField("CONTRACT_TYPE");                
        f.setDefaultNotVisible();   
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
        else
            f.setFunction("''");    
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEWIZB2ECONTRACTTYPE: Contract Type:");
        f.setSize(15);
   

        f = headblk.addField("INVOICE_TYPE");                     
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''"); 
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEWIZB2EINVOICETYPE: Invoice Type:");
        f.setSize(15);
        //Bug 84436, End 

        headblk.addField("WORK_TYPE_ID").
        setSize(9).
        setMaxLength(20).
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EWORKTYPEID: Work Type:").
//      Bug 69047, Start
        setCustomValidation("CONTRACT_ID,LINE_NO,ORG_CODE,SETTINGTIME,AUTHORIZE_CODE,CUSTOMER_NO,CONTRACT,MCH_CODE,WORK_TYPE_ID,CURRENCY_CODE,REG_DATE,PLAN_F_DATE,MCH_CODE_CONTRACT", "CONTRACT_ID,LINE_NO").
//      Bug 69047, End
        setUpperCase();

        headblk.addField("WORKTYPEDESC").
        setSize(30).
        setHidden().
        setReadOnly().
        setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");

        headblk.addField("AGREEMENT_ID").
        setSize(9).
        setLabel("PCMWACTIVESEPARATEWIZB2EAGREEMNTID: Agreement ID:").
        setUpperCase().
	setHidden().
        setMaxLength(10);

        //Bug 69819, Start, Modified function call
        headblk.addField("CUSTOMERAGREEMENTDESCRIPTION").  
        setSize(28).
        setHidden().
        setFunction("''");  
        //Bug 69819, End

        headblk.addField("ORG_CODE").
        setSize(9).
        setMandatory().
        setHidden().
        setMaxLength(8).
        setLabel("PCMWACTIVESEPARATEWIZB2EORGCODE: Maintenance Organization:").
        setUpperCase();

        headblk.addField("ORGCODEDESC").
        setFunction("ORGANIZATION_API.Get_Description(:CONTRACT,:ORG_CODE)").
        setReadOnly().
        setHidden().
        setSize(30);

        headblk.addField("REPORTED_BY_ID").
        setHidden();

        headblk.addField("FAULT_REP_FLAG","Number").
        setHidden();

        headblk.addField("COMPANY").
        setHidden();

        headblk.addField("COMPANY_NAME").
        setHidden().
        setFunction("''");

        headblk.addField("CR_STOP").
        setHidden().
        setFunction("''");

        headblk.addField("FAULT_REP_AGREE").
        setHidden().
        setFunction("''");

        headblk.addField("FAULT_REP_AGREE_CLIENT").
        setHidden().
        setFunction("''");

        /*headblk.addField("STD_JOB_ID").
        setLabel("PCMWACTIVESEPARATEWIZB2ESTDJOBID: Standard Job ID").
        setMaxLength(12).
        setHidden();*/

        headblk.addField("PLAN_HRS").
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EPLANHRS: Execution time");

        headblk.addField("AUTHORIZE_CODE").
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EAUTHORIZECODE: Coordinator");

        headblk.addField("PLAN_S_DATE","Datetime").
        setReadOnly().
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EPLANSDATE: Start");

        headblk.addField("PLAN_F_DATE","Datetime").
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EPLANFDATE: Finish");

        headblk.addField("REQUIRED_START_DATE","Datetime").
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EEREQSTDATE: Required Start Date");

        headblk.addField("REQUIRED_END_DATE","Datetime").
        setHidden().
        setLabel("PCMWACTIVESEPARATEWIZB2EREQENDDATE: Required End Date");

        headblk.addField("CUST_ORDER_TYPE").
        setHidden(); 

        headblk.addField("SETTING_TIME","Number").
        setHidden().
        setFunction("''");

        headblk.addField("ATTR0").
        setHidden().
        setFunction("''");

        headblk.addField("ATTR1").
        setHidden().
        setFunction("''");

        headblk.addField("ATTR2").
        setHidden().
        setFunction("''");

        headblk.addField("ATTR3").
        setHidden().
        setFunction("''");

        headblk.addField("ATTR4").
        setHidden().
        setFunction("''");

        headblk.addField("TEMP").
        setHidden().
        setFunction("''");   

        headblk.addField("TEMP_MCH_CODE").
        setHidden().
        setFunction("''");   

        headblk.addField("DUMMY_PLAN_F_DATE","Datetime").
        setHidden().
        setFunction("''"); 

//      Bug 69047, Start
        headblk.addField("SETTINGTIME").
        setHidden().
        setFunction("''"); 

        headblk.addField("CURRENCY_CODE").
        setHidden().
        setFunction("''"); 
//      Bug 69047, End

	//Bug Id 70921, Start
        headblk.addField("IS_VALID").
        setFunction("''").
        setHidden();
	//Bug Id 70921, End


        headblk.setView("ACTIVE_SEPARATE");                                        
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__"); 
        headblk.setTitle(mgr.translate("PCMWACTIVESEPARATEWIZB2EBLKTITLE: Create Service Request"));                   

        headset = headblk.getASPRowSet();                                                               

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.SAVERETURN);
        headbar.disableCommand(headbar.CANCELEDIT);
        headbar.disableCommand(headbar.FORWARD);
        headbar.disableModeLabel();

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setSimple("CUSTOMERAGREEMENTDESCRIPTION");
        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("ADDRESS2");
        headlay.setSimple("ADDRESS4");
        headlay.setSimple("ADDRESS6");
        headlay.setSimple("CUSTOMERNAME");
        headlay.setSimple("WORKTYPEDESC");
        headlay.setSimple("ORGCODEDESC");
        headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        headlay.setDialogColumns(1);                                  

        popblk1 = mgr.newASPBlock("WORK");                                        
        popblk2 = mgr.newASPBlock("DEPART");                                                            
        popblk3 = mgr.newASPBlock("AGREE");  

        itemblklst = mgr.newASPBlock("ITEMLST");                                                       

        itemblklst.addField("LST_ITEM_CONTRACT").
        setHidden().
        setDbName("CONTRACT");

        itemblklst.addField("LST_ITEM_MCH_CODE").
        setHidden().
        setDbName("MCH_CODE");

        itemblklst.setView("EQUIPMENT_ALL_OBJECT"); 
        itemsetlst = itemblklst.getASPRowSet();  

        itemblk = mgr.newASPBlock("ITEM");                                        

        itemblk.addField("ITEM_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk.addField("ITEM_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk.addField("ITEM_CONTRACT").
        setHidden().
        setDbName("CONTRACT");

        itemblk.addField("ITEM_MCH_CODE").
        setHidden().
        setDbName("MCH_CODE");

        itemblk.addField("PARTY_TYPE").
        setLabel("PCMWACTIVESEPARATEWIZB2EPARTYTYPE: Party Type").
        setHidden();

        itemblk.addField("IDENTITY").
        setLabel("PCMWACTIVESEPARATEWIZB2EIDENTITY: Identity").
        setHidden();

        itemblk.addField("ADDRESS").
        setFunction("''").
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRESS: Adress");

        itemblk.setView("EQUIPMENT_OBJECT_PARTY"); 
        itemset = itemblk.getASPRowSet();
        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.EDIT_LAYOUT);
        itemlay.setDialogColumns(1);

        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField("ITEM0_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk0.addField("ITEM0_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk0.addField("HYP_CONTRACT").
        setFunction("CONTRACT").
        setHidden();

        itemblk0.addField("ITEM0_MCH_CODE").
        setSize(20).
        setLabel("PCMWACTIVESEPARATEWIZB2EITEM0MCHCODE: Object Id").
        setUpperCase().
        setHidden().
        setReadOnly().
        setMaxLength(100).
        setDbName("MCH_CODE");

        itemblk0.addField("MCH_NAME").
        setSize(40).
        setHidden().
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEWIZB2EITEM0MCHNAME: Description").
        setMaxLength(45);

        itemblk0.addField("SERIAL_NO").
        setLabel("PCMWACTIVESEPARATEWIZB2ESERIALNO: Serial No").
        setSize(20).
        setMaxLength(50).
        setReadOnly().
        setUpperCase();  

        itemblk0.addField("ITEM0_CONTRACT").
        setLabel("PCMWACTIVESEPARATEWIZB2EITEM0CONTRACT: Site").
        setSize(10).
        setUpperCase().
        setReadOnly().
        setDbName("CONTRACT");

        itemblk0.addField("GROUP_ID").
        setSize(10).
        setLabel("PCMWACTIVESEPARATEWIZB2EGROUPID: Group").
        setUpperCase().
        setMaxLength(10).
        setReadOnly();  

        itemblk0.addField("CATEGORY_ID").                              
        setSize(15).                                                    
        setLabel("PCMWACTIVESEPARATEWIZB2ECATEGORYID: Category"). 
        setUpperCase().                         
        setMaxLength(10).                                         
        setReadOnly();                                      

        itemblk0.addField("MCH_TYPE").
        setSize(   10).
        setLabel("PCMWACTIVESEPARATEWIZB2EMCHTYPE: Type").
        setUpperCase().
        setMaxLength(5).
        setReadOnly();  

        itemblk0.addField("ATR1").
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS1: Address 1").
        setFunction("B.ADDRESS1").                             
        setReadOnly().                                
        setSize(15);                                              

        itemblk0.addField("ATR2").                                     
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS2: Address 2").                                  
        setFunction("ADDRESS2").                                  
        setReadOnly().                                            
        setSize(15);                                              

        itemblk0.addField("ATR3").
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS3: Address 3"). 
        setFunction("ADDRESS3").                            
        setReadOnly().                                   
        setSize(15);                                              

        itemblk0.addField("ATR4").                                     
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS4: Address 4").                                  
        setFunction("ADDRESS4").                                  
        setReadOnly().                                            
        setSize(15);                                              

        itemblk0.addField("ATR5").
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS5: Address 5").                                  
        setFunction("ADDRESS5").                                  
        setReadOnly().                                            
        setSize(15);                                              

        itemblk0.addField("ATR6").                                     
        setLabel("PCMWACTIVESEPARATEWIZB2EADDRS6: Address 6").                                  
        setFunction("ADDRESS6").                                  
        setReadOnly().                                            
        setSize(15);   

        itemblk0.setView("EQUIPMENT_ALL_OBJECT");                      
        itemset0 = itemblk0.getASPRowSet();                   
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.EDIT_LAYOUT);

        itembar0 = mgr.newASPCommandBar(itemblk0);                  

        itemtbl0 = mgr.newASPTable(itemblk0);                       
        itemtbl0.setEditable();                                     
        itemtbl0.disableQueryRow();                           


        itemblk1 = mgr.newASPBlock("ITEM1");

        itemblk1.addField("ITEM1_MCH_CODE").
        setHidden().
        setDbName("MCH_CODE");

        itemblk1.addField("ITEM1_CONTRACT").
        setHidden().
        setDbName("CONTRACT");

        itemblk1.addField("ITEM1_ADDRESS1").
        setDbName("ADDRESS1");

        itemblk1.addField("ITEM1_ADDRESS2").
        setDbName("ADDRESS2");

        itemblk1.addField("ITEM1_ADDRESS3").
        setDbName("ADDRESS3");

        itemblk1.addField("ITEM1_ADDRESS4").
        setDbName("ADDRESS4");

        itemblk1.addField("ITEM1_ADDRESS5").
        setDbName("ADDRESS5");

        itemblk1.addField("ITEM1_ADDRESS6").
        setDbName("ADDRESS6");

        itemblk1.setView("EQUIPMENT_OBJECT_ADDRESS");
        itemset1 = itemblk1.getASPRowSet();
     
	headlay.showBottomLine(false);

        setWizardOptions();
    }

    private void setWizardOptions()
    {
        disableHomeIcon();
        disableNavigate();
        disableOptions();
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        // Bug Id 70012
        isSecure = new String[2];

        frm.setFormWidth(730);
        headbar.setWidth(frm.getFormWidth()); 

        if (scr == 1)
        {
            setObjectLov();

            mgr.getASPField("CONTRACT").setHidden(); 
            mgr.getASPField("REG_DATE").setHidden(); 
            mgr.getASPField("ERR_DESCR_LO").setHidden();
            mgr.getASPField("MCH_CODE").setHidden();
            mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
            mgr.getASPField("WORK_TYPE_ID").setHidden();
            mgr.getASPField("WORKTYPEDESC").setHidden();
            mgr.getASPField("ORG_CODE").setHidden();
            mgr.getASPField("ORGCODEDESC").setHidden();
            mgr.getASPField("AGREEMENT_ID").setHidden();
            mgr.getASPField("CUSTOMERAGREEMENTDESCRIPTION").setHidden();
	    mgr.getASPField("CONTRACT_ID").setHidden();
	    mgr.getASPField("LINE_NO").setHidden();
	    mgr.getASPField("CONTRACT_NAME").setHidden();
	    mgr.getASPField("LINE_DESC").setHidden();
	    mgr.getASPField("CONTRACT_TYPE").setHidden();
	    mgr.getASPField("INVOICE_TYPE").setHidden();

            if (againPre)
            {
                trans.clear();
                row = headset.getRow();
                row.setValue("CONTACT",contact);
                row.setValue("PHONE_NO",phone_no);
                row.setValue("REFERENCE_NO",reference_no);
                row.setValue("CUSTOMER_NO",custNo);
                row.setValue("REPORTED_BY",repBy);
                row.setValue("REPORTED_BY_ID",repById);
                row.setValue("CUSTOMERNAME",custName);
                headset.setRow(row); 
            }
        }

        if (scr == 2)
        {
            mgr.getASPField("REG_DATE").setHidden(); 
            mgr.getASPField("CUSTOMER_NO").setHidden();
            mgr.getASPField("REPORTED_BY").setHidden(); 
            mgr.getASPField("CONTACT").setHidden(); 
            mgr.getASPField("PHONE_NO").setHidden(); 
            mgr.getASPField("REFERENCE_NO").setHidden(); 
            mgr.getASPField("WORK_TYPE_ID").setHidden();
            mgr.getASPField("WORKTYPEDESC").setHidden(); 
            mgr.getASPField("ORG_CODE").setHidden();
            mgr.getASPField("ORGCODEDESC").setHidden(); 
            mgr.getASPField("CUSTOMERNAME").setHidden();
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
	    mgr.getASPField("CONTRACT_ID").setHidden();
	    mgr.getASPField("LINE_NO").setHidden();
	    mgr.getASPField("CONTRACT_NAME").setHidden();
	    mgr.getASPField("LINE_DESC").setHidden();
	    mgr.getASPField("CONTRACT_TYPE").setHidden();
	    mgr.getASPField("INVOICE_TYPE").setHidden();

            if (!hyper)
                mgr.getASPField("MCH_CODE").setHidden();
            else
            {
                row = headset.getRow();
                row.setValue("MCH_CODE",obj);
                headset.setRow(row);

                mgr.getASPField("MCH_CODE").unsetHidden().setReadOnly(); 
            }  
            mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden(); 
            mgr.getASPField("AGREEMENT_ID").setHidden(); 
            mgr.getASPField("CUSTOMERAGREEMENTDESCRIPTION").setHidden();

            row = headset.getRow();
            row.setValue("ERR_DESCR_LO",err_descr_lo);
            row.setValue("CONTRACT",contrct);
            headset.setRow(row);

        }

        if (scr == 3)
        {
            mgr.getASPField("REG_DATE").setHidden();
            mgr.getASPField("CUSTOMER_NO").setHidden();
            mgr.getASPField("REPORTED_BY").setHidden();
            mgr.getASPField("CONTACT").setHidden();
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
            mgr.getASPField("PHONE_NO").setHidden();
            mgr.getASPField("REFERENCE_NO").setHidden();
            mgr.getASPField("CUSTOMERNAME").setHidden();
            mgr.getASPField("CONTRACT").setHidden();
            mgr.getASPField("ERR_DESCR_LO").setHidden();
            mgr.getASPField("MCH_CODE").setHidden();
            mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();
            mgr.getASPField("WORK_TYPE_ID").setHidden();
            mgr.getASPField("WORKTYPEDESC").setHidden();
            mgr.getASPField("ORG_CODE").setHidden();
            mgr.getASPField("ORGCODEDESC").setHidden();
        }

        if (!afterAccept)
            mgr.getASPField("WO_NO").setHidden();

        if (scr != 4)
        {
            mgr.getASPField("ADDRESS1").setHidden();
            mgr.getASPField("ADDRESS2").setHidden();
            mgr.getASPField("ADDRESS3").setHidden();
            mgr.getASPField("ADDRESS4").setHidden();
            mgr.getASPField("ADDRESS5").setHidden();
            mgr.getASPField("ADDRESS6").setHidden();
        }

        if (scr == 4)
        {

            mgr.getASPField("CUSTOMER_NO").setReadOnly();
            mgr.getASPField("REFERENCE_NO").setReadOnly();
            mgr.getASPField("PHONE_NO").setHidden().setReadOnly();
            mgr.getASPField("CONTACT").setReadOnly();
            mgr.getASPField("REPORTED_BY").setReadOnly();
            mgr.getASPField("CONTRACT").setReadOnly();
            mgr.getASPField("ERR_DESCR_LO").setReadOnly();
            mgr.getASPField("MCH_CODE").unsetHidden().setReadOnly();
            mgr.getASPField("MCH_CODE_DESCRIPTION").unsetHidden();
            mgr.getASPField("MCH_CODE_CONTRACT").unsetHidden();
            mgr.getASPField("ADDRESS1").unsetHidden().setReadOnly();
            mgr.getASPField("ADDRESS2").unsetHidden().setReadOnly();
            mgr.getASPField("ADDRESS3").unsetHidden().setReadOnly();
            mgr.getASPField("ADDRESS4").unsetHidden().setReadOnly();
            mgr.getASPField("ADDRESS5").unsetHidden().setReadOnly();
            mgr.getASPField("ADDRESS6").unsetHidden().setReadOnly();
            mgr.getASPField("WORK_TYPE_ID").unsetHidden().setReadOnly();
            mgr.getASPField("WORKTYPEDESC").unsetHidden();
            mgr.getASPField("AGREEMENT_ID").unsetHidden().setReadOnly();
	    mgr.getASPField("CONTRACT_ID").setReadOnly();
	    mgr.getASPField("LINE_NO").setReadOnly();

            if (afterAccept)
            {
                mgr.getASPField("WO_NO").unsetHidden().setReadOnly();
                mgr.getASPField("ORG_CODE").unsetHidden().setReadOnly();
            }
            if (!afterAccept)
            {
                mgr.getASPField("ORG_CODE").setHidden();
            }

        }

        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");  
        // Bug Id 70012, Start
	if (mgr.isModuleInstalled("PCMSCI")){
	    bPcmsciExist = true;
	}
	// Bug Id 70012, End
    }

    public void setObjectLov()
    {
        ASPManager mgr = getASPManager();

        if (hyper)
        {
            /*mgr.getASPField("CUSTOMER_NO").setLOV("ActiveSeparateWizB2ELov.page", 600, 445);
            mgr.getASPField("CUSTOMER_NO").setLOVProperty("WHERE", "PARTY_TYPE_DB='CUSTOMER'");*/
	    mgr.getASPField("CUSTOMER_NO").setDynamicLOV("CUSTOMER_INFO", 600, 445);

        }
        else
            mgr.getASPField("CUSTOMER_NO").setDynamicLOV("CUSTOMER_INFO", 600, 445);
    }

    protected String getDescription()
    {
        return "PCMWACTIVESEPARATEWIZB2ETITLE: Create Service Request";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATEWIZB2ETITLE: Create Service Request";
    }

//===============================================================
//  HTML
//===============================================================
    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (showHtmlPart)
        {
            printHiddenField("PNT","");
            printHiddenField("SELAGREINDX","");
            printHiddenField("BUTTONVAL","");
            printHiddenField("BUTTONACCEPT","");

            appendToHTML(headbar.showBar());
            appendToHTML("<table id=\"TBL\" border=0 class=\"BlockLayoutTable\" cellspacing=10 cellpadding=1 width= 730>\n");
            appendToHTML("       <tr>\n");
            appendToHTML("           <td \n");
            appendToHTML("	     <table id=\"TBL1\" border=0 cellspacing=10 cellpadding=1 width= '20%'>\n");
            appendToHTML("	     <tr>\n");
            appendToHTML("               <td nowrap height=\"26\" align=\"left\"><img src=\"../pcmw/images/ServiceRequestwizard.gif\" ></td>\n");
            appendToHTML("	         <td nowrap height=\"26\" align=\"left\">\n");

            if (scr == 1 || scr == 2)
            {
                appendToHTML("   <table border=0 class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 >\n");
                appendToHTML("    <tr>\n");
                appendToHTML("     <td>\n");
                if (scr == 1)
                {
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT1: Welcome to the Service Request Wizard!   ")));
                    appendToHTML("       </br></br>\n");
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT2: Enter your Reference No, Phone and Contact details.")));
                    appendToHTML("       </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("       </br></br></br></br>\n");
                }
                else if (scr == 2)
                {
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT3: Enter details of the Service Requirements or Faults being reported.")));
                    appendToHTML("       </br></br>\n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    if (!hyper)
                    {
                        appendToHTML("        <table  border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>                                                                                                                            \n");
                        appendToHTML("          <tr>                                                                                                                                                                                                                    \n");
                        appendToHTML("     	   <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWACTIVESEPARATEWIZB2EINFOTEXT4: Choose Object:"));
                        appendToHTML("</td>                                                                                                                                   \n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '65%'>");
                        appendToHTML(fmt.drawSelect("ITEM0MCHCODE",objectbuff,chosenObject,"onChange='setContractValue()'"));
                        appendToHTML("</td>                                                                                       \n");
                        appendToHTML("          </tr>                                                                                                                                                                                                                   \n");
                        appendToHTML("        </table>                                                                                                                                                                                                                      \n");
                    }
                    appendToHTML("        </br></br></br></br></br></br>                                                                                                                                                                                                                        \n");
                }

                appendToHTML("      </td>				\n");
                appendToHTML("    </tr>                                                                                                                                                                                                                                           \n");
                appendToHTML("  </table>\n");
                appendToHTML("					</td>			   \n");
                appendToHTML("				</tr>				\n");
                appendToHTML("			</table>\n");

            }
            else
            {
                appendToHTML("   <table border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 >\n");
                appendToHTML("    <tr>\n");
                appendToHTML("     <td>\n");

                if (scr == 3)
                {
//                 Bug 69047, Start
                    appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT13: In order to select a service contract you must enter a work type that best fits the description of the service you are requesting.")));
                    appendToHTML("       </br>\n");
                    appendToHTML("       </br></br>\n");
                    appendToHTML("       </br>                                                                                                                                                                                                             \n");
                    appendToHTML("       <table  border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>                                                                                                                                            \n");
                    appendToHTML("           <tr>                                                                                                                                                                                                                                       \n");
                    appendToHTML("              <td nowrap height=\"26\" align=\"left\" width = '26%'>&nbsp;&nbsp;");
                    appendToHTML(fmt.drawWriteLabel("PCMWACTIVESEPARATEWIZB2EINFOTEXT5: Work type:"));
                    appendToHTML("</br></br></td>                                                                                                                                                      \n");
                    appendToHTML("              <td nowrap height=\"26\" align=\"left\" width = '74%'>");
                    appendToHTML(fmt.drawSelect("WKTYPE",wrkTypeBuff,chosenWkType,"onChange='changeWorkType(0)'"));
                    appendToHTML("</br></br></td>\n");
                    appendToHTML("           </tr> \n");
                    appendToHTML("       </table> \n");
                    appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                    appendToHTML("       <table  border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>                                                                                                                                            \n");
                    appendToHTML("           <tr>\n");
                    appendToHTML("              <td nowrap height=\"26\" align=\"left\" width = '24%'>");
                    appendToHTML(fmt.drawWriteLabel("PCMWACTIVESEPARATEWIZB2EMOREWRKTYP: service contracts exist for more than one Work Type:"));
                    appendToHTML("</td>\n");
                    if (moreWrk)
                    {
                        appendToHTML("                  <td nowrap height=\"26\" align=\"left\" width = '76%'><font class=\"ReadOnlyTextValue\"><img src=\"");
                        appendToHTML(mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES"));
                        appendToHTML("check_box_marked.gif\"><input type=hidden name=\"MOREWKTYPE\" value=\"TRUE\" CHECKED></font></td><td width=10>&nbsp</td>\n");
                    }
                    else
                    {
                        appendToHTML("                  <td nowrap height=\"26\" align=\"left\" width = '76%'><font class=\"ReadOnlyTextValue\"><img src=\"");
                        appendToHTML(mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES"));
                        appendToHTML("check_box_unmarked.gif\"><input type=hidden name=\"MOREWKTYPE\" value=\"FALSE\"></font></td><td width=10>&nbsp</td>\n");
                    }
                    appendToHTML("       </tr> \n");
                    appendToHTML("       </table> \n");
//                  Bug 69047, End

                }
                else if (scr == 4)
                {
                    if (afterAccept)
                    {
                        appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT6: Use the keyboard print command ('ctrl'+'p') if a paper copy is required.")));
                        appendToHTML("         <p>                                                                                                                                                                                                                                          \n");
                    }
                    else
                    {
			appendToHTML(fmt.drawReadValue(mgr.translate("PCMWACTIVESEPARATEWIZB2EINFOTEXT7: To save your service request, click 'Accept service order'. If you want to change any information, click 'Previous'.")));
                        appendToHTML("          </br></br>                                                                                                                                                                                                                                       \n");
                        appendToHTML(headlay.generateDialog()); // XSS_Safe ILSOLK 20070713
                        appendToHTML("          <table  border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>                                                                                                                                         \n");
                        appendToHTML("              <tr>       \n");
                        appendToHTML("                <td nowrap height=\"26\" align=\"left\" width = '24%'>");
                        appendToHTML(fmt.drawWriteLabel("PCMWACTIVESEPARATEWIZB2EINFOTEXT10: Maintenance Organization:"));
                        appendToHTML("</td>           <td nowrap height=\"26\" align=\"left\" width = '76%'>");
                        appendToHTML(fmt.drawSelect("DEPT",deptBuff, headset.getValue("ORG_CODE"), ""));
                        appendToHTML("<font class=\"TextLabel\">*</font></td> \n");
                        appendToHTML("              </tr>                                                                                                                                                                                                                                   \n");
                        appendToHTML("          </table>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     \n");
                    }

		    
		    
                }

                appendToHTML("      </td>\n");
                appendToHTML("     </tr> \n");
                appendToHTML("    </table> \n");
            }

            appendToHTML("  </td>\n");
            appendToHTML(" </tr>\n");
            appendToHTML("</table>\n");
            appendToHTML("  <table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=708>\n");
            appendToHTML("  <tr>\n");

            appendToHTML(fmt.drawReadValue("............................................................."));
            appendToHTML(fmt.drawReadValue("............................................................."));

            if (scr == 1 || scr == 2)
                appendToHTML(fmt.drawReadValue("............................................................"));
            else if (scr == 3)
                appendToHTML(fmt.drawReadValue("............................................................"));
            else if (scr == 4)
                appendToHTML(fmt.drawReadValue("............................................................"));

            appendToHTML("  </tr>\n");
            appendToHTML("  </table> \n");
            appendToHTML("<table id=\"TBL2\" border=0 cellspacing=10 cellpadding=1 width= 745>\n");
            appendToHTML("  <tr>		\n");
            appendToHTML("    <td>  \n");

            if (scr == 1)
            {
                    appendToHTML("			          <table width= '99.5%'>\n");
                    appendToHTML("				    <tr>		\n");
                    appendToHTML("				      <td align=\"right\">  \n");
                    appendToHTML(fmt.drawButton("NEXT",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT1: Next>"),"onClick=checkMandato(1)"));
                    appendToHTML("&nbsp;\n");
                    appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT2: Cancel"),""));
                    appendToHTML("			              </td>   \n");
                    appendToHTML("				    </tr>							\n");
                    appendToHTML("				  </table>\n");
            }
            else if (scr == 2)
            {
                    appendToHTML("			          <table width= '99.5%'>\n");
                    appendToHTML("				    <tr>		\n");
                    appendToHTML("				      <td align=\"right\">      \n");
                    appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT3: <Previous"),""));
                    appendToHTML("&nbsp;                                                                                                                                                                     \n");
                    appendToHTML(fmt.drawButton("NEXT",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT4: Next>"),"onClick=checkMandato(2)"));
                    appendToHTML("&nbsp; \n");
                    appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT5: Cancel"),""));
                    appendToHTML("			              </td>   \n");
                    appendToHTML("				    </tr>							\n");
                    appendToHTML("				  </table>                                                                                                                                                                 \n");
            }
            else if (scr == 3)
            {
                    appendToHTML("			          <table width= '99.5%'>\n");
                    appendToHTML("				    <tr>		\n");
                    appendToHTML("				      <td align=\"right\">\n");
                    appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT6: <Previous"), ""));
                    appendToHTML("&nbsp;                                                                                                                                                                                     \n");
                    appendToHTML(fmt.drawButton("NEXT",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT7: Next>"), "onClick='setAgreement()'"));
                    appendToHTML("&nbsp\n");
                    appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT8: Cancel"),""));
                    appendToHTML("			              </td>   \n");
                    appendToHTML("				    </tr>							\n");
                    appendToHTML("				  </tablen");
             }
             else if (scr == 4)
             {
                    if (afterAccept)
                    {
                        appendToHTML("	                <table width= '99.5%'>\n");
                        appendToHTML("	                 <tr>		\n");
                        appendToHTML("	                  <td align=\"right\"> \n");
                        appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZB2ECLOWIZ: Close wizard"),""));
                        appendToHTML("	                  </td>\n");
                        appendToHTML("	                 </tr>\n");
                        appendToHTML("	                </table>   \n");
                    }
                    else
                    {
                        appendToHTML("                            <table width= '99.5%'>\n");
                        appendToHTML("				            <tr>		\n");
                        appendToHTML("				              <td align=\"right\">\n");
                        appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT15: <Previous"),""));
                        appendToHTML("&nbsp;                                                                                                                                                                             \n");
                        appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT16: Cancel"),""));
                        appendToHTML("&nbsp;\n");
                        appendToHTML(fmt.drawButton("ACCEPT",mgr.translate("PCMWACTIVESEPARATEWIZB2EBUT17: Accept service order"),"onClick=checkMandato(3)"));
                        appendToHTML("				              </td>   \n");
                        appendToHTML("					    </tr>							\n");
                        appendToHTML("					  </table>\n");
                    }
            }

            appendToHTML("    </td>\n");
            appendToHTML("  </tr>\n");
            appendToHTML("</table>\n");
            appendToHTML("<p>\n");

            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin)); // Bug Id 68773
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript(" self.close();\n");

            if (scr == 3)
            	appendDirtyJavaScript("document.form.__FOCUS.value = 'WKTYPE';\n") ;
            appendDirtyJavaScript("function validateCustomerNo(i)\n");
            appendDirtyJavaScript("{   \n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("	    getField_('CREDITSTOP',i).value = '';\n");
            appendDirtyJavaScript("        getField_('CUSTOMERNAME',i).value = '';\n");
            appendDirtyJavaScript("        return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("   window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
            appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("   window.status='';\n");
            appendDirtyJavaScript("    if( checkStatus_(r,'CUSTOMER_NO',i,'Customer No') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("        assignValue_('CREDITSTOP',i,0);\n");
            appendDirtyJavaScript("        assignValue_('CUSTOMERNAME',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    retStr = r.split(\"^\");\n");
            appendDirtyJavaScript("    if(retStr[0] == ' 1')\n");
            appendDirtyJavaScript("    {   \n");
            appendDirtyJavaScript("        message = '");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEWIZB2EMSSG: Customer is Credit blocked"));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("        window.alert(message);\n");
            appendDirtyJavaScript("    }\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function openHelp()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	window.open(\"ObjSearchHelp.html\");\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function checkMandato(i)\n");
            appendDirtyJavaScript("{    \n");
            appendDirtyJavaScript("	if (i == 1)	\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("  				if(f.REPORTED_BY.value == '')\n");
            appendDirtyJavaScript("				{\n");
            appendDirtyJavaScript("					z = eval('f.REPORTED_BY');\n");
            appendDirtyJavaScript("					label = '");
            appendDirtyJavaScript(mgr.getASPField("REPORTED_BY").getJSLabel()); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("					return missingValueError_(z,label,'');\n");
            appendDirtyJavaScript("				}\n");
            appendDirtyJavaScript("					else\n");
            appendDirtyJavaScript("					{\n");
            appendDirtyJavaScript("  	                                    if(checkHeadFields(0))\n");
            appendDirtyJavaScript("	                                    {\n");
            appendDirtyJavaScript("		                                document.form.BUTTONVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("		                                f.submit();\n");
            appendDirtyJavaScript("	                                    }\n");
            appendDirtyJavaScript("					}\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if (i == 2)\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("  				if(f.ERR_DESCR_LO.value == '')\n");
            appendDirtyJavaScript("				{\n");
            appendDirtyJavaScript("					z = eval('f.ERR_DESCR_LO');\n");
            appendDirtyJavaScript("					label = '");
            appendDirtyJavaScript(mgr.getASPField("ERR_DESCR_LO").getJSLabel()); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("					return missingValueError_(z,label,'');\n");
            appendDirtyJavaScript("				}\n");
            appendDirtyJavaScript("					else\n");
            appendDirtyJavaScript("					{\n");
            appendDirtyJavaScript("						document.form.BUTTONVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("						f.submit();\n");
            appendDirtyJavaScript("					}\n");
            appendDirtyJavaScript("  				if(f.CONTRACT.value == '')\n");
            appendDirtyJavaScript("				{\n");
            appendDirtyJavaScript("					z = eval('f.CONTRACT');\n");
            appendDirtyJavaScript("					label = '");
            appendDirtyJavaScript(mgr.getASPField("CONTRACT").getJSLabel()); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("					return missingValueError_(z,label,'');\n");
            appendDirtyJavaScript("				}\n");
            appendDirtyJavaScript("					else\n");
            appendDirtyJavaScript("					{\n");
            appendDirtyJavaScript("						document.form.BUTTONVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("						f.submit();\n");
            appendDirtyJavaScript("					}\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if (i == 3)\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("	        x = eval('f.DEPT');\n");
            appendDirtyJavaScript("	        label = '");
            appendDirtyJavaScript(mgr.getASPField("ORG_CODE").getJSLabel()); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("			if( f.DEPT.options[f.DEPT.selectedIndex].value=='' )\n");
            appendDirtyJavaScript("				return missingValueError_(x,label,'');  \n");
            appendDirtyJavaScript("					else\n");
            appendDirtyJavaScript("					{\n");
            appendDirtyJavaScript("						document.form.BUTTONACCEPT.value = \"AAAA\";\n");
            appendDirtyJavaScript("						f.submit();\n");
            appendDirtyJavaScript("					}\n");      
            appendDirtyJavaScript("	}	\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateErrDescrLo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	return;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setContractValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  n = document.form.ITEM0MCHCODE.selectedIndex;\n");
            appendDirtyJavaScript("  document.form.PNT.value = n;\n");
            appendDirtyJavaScript("}\n");

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
            appendDirtyJavaScript("      lineTBL.style.display='block';    \n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(execute)); // Bug Id 68773
            appendDirtyJavaScript("' == 'TRUE')    \n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("      changeWorkType(0);\n");
            appendDirtyJavaScript("      n = '");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(agreIndex));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setAgreement()\n");
            appendDirtyJavaScript("{    \n");
            appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("}\n"); 

            appendDirtyJavaScript("function changeWorkType(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("      document.form.WORK_TYPE_ID.value = document.form.WKTYPE.options[document.form.WKTYPE.selectedIndex].value; \n");
            appendDirtyJavaScript("      if( document.form.WKTYPE.options[document.form.WKTYPE.selectedIndex].value=='')\n");
            appendDirtyJavaScript("          return;\n");
            appendDirtyJavaScript("      r = __connect(\n");
            appendDirtyJavaScript("          '");
            appendDirtyJavaScript(mgr.getURL());
//      Bug 69047, Start
//            appendDirtyJavaScript("?VALIDATE=WORK_TYPE_ID'\n");
//            appendDirtyJavaScript("          + '&WORK_TYPE_ID=' + document.form.WKTYPE.options[document.form.WKTYPE.selectedIndex].value\n");
//            appendDirtyJavaScript("          + '&CUSTOMER_NO=' + f.CUSTOMER_NO.value\n");
//            appendDirtyJavaScript("          + '&MCH_CODE=' + document.form.MCH_CODE.value\n");
//            appendDirtyJavaScript("          + '&CONTRACT=' + f.CONTRACT.value);\n");

				appendDirtyJavaScript("?VALIDATE=WORK_TYPE_ID'\n");
				appendDirtyJavaScript("            		+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
				appendDirtyJavaScript("+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
				appendDirtyJavaScript("+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
				appendDirtyJavaScript("+ '&SETTINGTIME=' + URLClientEncode(getValue_('SETTINGTIME',i))\n");
				appendDirtyJavaScript("+ '&AUTHORIZE_CODE=' + URLClientEncode(getValue_('AUTHORIZE_CODE',i))\n");
				appendDirtyJavaScript("+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
				appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
				appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
				appendDirtyJavaScript("+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
				appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
				appendDirtyJavaScript("+ '&CURRENCY_CODE=' + URLClientEncode(getValue_('CURRENCY_CODE',i))\n");
				appendDirtyJavaScript("+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
				appendDirtyJavaScript("+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
				appendDirtyJavaScript(");\n");
				appendDirtyJavaScript("window.status='';\n");
				
				appendDirtyJavaScript("if( checkStatus_(r,'WORK_TYPE_ID',i,'Work Type:') )\n");
				appendDirtyJavaScript("    	{\n");
				appendDirtyJavaScript("assignValue_('CONTRACT_ID',i,0);\n");
				appendDirtyJavaScript("assignValue_('LINE_NO',i,1);\n");
				appendDirtyJavaScript("validateContractId(0);\n");
				appendDirtyJavaScript("	}\n");
//      Bug 69047, End
				//Bug Id 70012, Removed code here to avoid the unneccessary script error    		
	    appendDirtyJavaScript("}\n");

            //Bug Id 70012, Start

	    appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
	    appendDirtyJavaScript("	if(params) param = params;\n");
	    appendDirtyJavaScript("	else param = '';\n");
	    appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
	    appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
	    appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
	    appendDirtyJavaScript(" 	{\n");
	    appendDirtyJavaScript("		whereCond1 = '';\n"); 
	    appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
       //Bug Id 87286, Start
       //appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CONTRACT.value)+ \"')) = 'TRUE' ) \" ;\n");
       appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR EXISTS (SELECT 1 FROM SC_CONTRACT_CUSTOMER cc WHERE cc.CONTRACT_ID = PSC_CONTR_PRODUCT_LOV.CONTRACT_ID AND cc.CUSTOMER_ID = ('\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"')) ) \" ;\n");
       //Bug Id 87286, End
       appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT_ID.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT_ID = '\" + URLClientEncode(document.form.CONTRACT_ID.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
	    //Bug Id 70156, Start
	    appendDirtyJavaScript(" 	{\n");
		if (bPcmsciExist) {
		    appendDirtyJavaScript("	      		whereCond1 += \" AND ((MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" OR (CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"')) \";\n"); 
		}else{
		    appendDirtyJavaScript("	      		whereCond1 += \" AND (MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') \";\n"); 
		}
	    appendDirtyJavaScript(" 	}\n");
	    //Bug Id 70156, End
	    if (bPcmsciExist) {	// Filtering by From Date
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
	    }
	    appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
	    appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
	    appendDirtyJavaScript("							,550,500,'validateContractId');\n");
	    appendDirtyJavaScript(" 	}\n");
	    appendDirtyJavaScript("	else\n");
	    appendDirtyJavaScript("  window.open('");
	    appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
	    appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
	    appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
	    appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
	    appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
	    appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
	    appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
	    appendDirtyJavaScript("}\n");

	    appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
	    appendDirtyJavaScript("	if(params) param = params;\n");
	    appendDirtyJavaScript("	else param = '';\n");
	    appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
	    appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
	    appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
	    appendDirtyJavaScript(" 	{\n");
	    appendDirtyJavaScript("		whereCond1 = '';\n"); 
	    appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
       //Bug Id 87286, Start
       //appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CONTRACT.value)+ \"')) = 'TRUE' ) \" ;\n");
       appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR EXISTS (SELECT 1 FROM SC_CONTRACT_CUSTOMER cc WHERE cc.CONTRACT_ID = PSC_CONTR_PRODUCT_LOV.CONTRACT_ID AND cc.CUSTOMER_ID = ('\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"')) ) \" ;\n");
       //Bug Id 87286, End
       appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
	    //Bug Id 70156, Start
	    appendDirtyJavaScript(" 	{\n");
		if (bPcmsciExist) {
		    appendDirtyJavaScript("	      		whereCond1 += \" AND ((MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" OR (CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"')) \";\n"); 
		}else{
		    appendDirtyJavaScript("	      		whereCond1 += \" AND (MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') \";\n"); 
		}
	    appendDirtyJavaScript(" 	}\n");
	    //Bug Id 70156, End
	    if (bPcmsciExist) {	// Filtering by From Date
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
	    }
	    appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
	    appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
	    appendDirtyJavaScript("							,550,500,'validateContractId');\n");
	    appendDirtyJavaScript(" 	}\n");
	    appendDirtyJavaScript("	else\n");
	    appendDirtyJavaScript("  window.open('");
	    appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
	    appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
	    appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
	    appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
	    appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
	    appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
	    appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
	    appendDirtyJavaScript("}\n");
	    //Bug Id 70012, End


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
            appendDirtyJavaScript("'+\"pcmw/ActiveSeparateWizB2E.page?RANDOM_VAL=\"+Math.random(),\"frmActiveSeparateWizB2E\",\"resizable=yes, status=yes,menubar=no,height=635,width =755\" );      \n");
            appendDirtyJavaScript("    } \n");
            appendDirtyJavaScript("    else \n");
            appendDirtyJavaScript("    { \n");
            appendDirtyJavaScript("      window.open('");
            appendDirtyJavaScript(url);
            appendDirtyJavaScript("'+\"pcmw/ActiveSeparateWizB2E.page?RANDOM_VAL=\"+Math.random(),\"frmActiveSeparateWizB2E\",\"resizable=yes, status=yes,menubar=no,height=700,width =766\" );      \n");
            appendDirtyJavaScript("    } \n");
        }
    }
}
