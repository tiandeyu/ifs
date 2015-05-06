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
*  File        : ActiveSeparateReportCOInfo.java 
*  Modified    :
*    ASP2JAVA Tool  2001-04-04  - Created Using the ASP file ActiveSeparateReportCOInfo.asp
*              : CHCR    2001-06-12  - Modified overwritten validations.
*              : BUNI    2001-08-27 Overwrote cancelEdit() method.
*              : INRO    2001-09-06 Changed save return function.. call id 78127
*                INRO    2001-10-19   - Added Decode to Warranty Description. call id 70691.
*              : GACOLK  2002-12-04   - Set Max Length of MCH_CODE to 100
*              : SAPRLK  2003-12-22   - Web Alignment - removed methods clone() and doReset().
*              : SAPRLK  2004-02-09   - Web Alignment - simplified code for RMBs, remove unnecessary method calls for hidden fields,
*                                       removed unnecessary global variables.
*              : VAGULK  2004-02-12   - Web Alignment - arranged the field order as given in Centura
*              : ARWILK  040308       - Bug#112748 - Modified validations for AGREEMENT_ID.
*              : SHAFLK  2004-01-19   - Bug Id 41815,Removed Java dependencies.
*              : SAPRLK  2004-03-23   - Merge with SP1.
*              : SHAFLK  050425       - Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*              : NIJALK  050617       - Merged bug 50830.
*              : NIJALK  050802       - LCS 49624, Modified adjust().
*              : NIJALK  050810       - Bug 126224, Modified preDefine().
*              : SHAFLK  051125       - Bug 54622, Added Reference no.
*              : NIJALK  051208       - Merged bug 54622.
*              : THWILK  060306       - Call ID 135826,Modified predefine().
*              : BUNILK  070504       - Implemented "MTIS907 New Service Contract - Services" changes.
*              ; AMDILK  070530       - Call Id 144903: Commented the db call to Sc_Service_Contract_API.Get_Customer_Id
*              ; AMDILK  070607       - Call Id 145865: Inserted new service contract information; Contrat name, line description
*              ; AMDILK  070712       - Fetched the agreement informations from the given contrat id
*              ; AMNILK  080430       - Bug Id 70156, Modified printContents().
* ----------------------------APP7.5 SP1--------------------------------------
* 071220   NIJALK   Bug 69819, Removed ASPField CUSTOMERAGREEMENTDESCRIPTION. 
* ----------------------------------------------------------------------------
* 080225   AMNILK   Bug Id 70012, Added new method updateContract() and modifed adjust(), preDefine and printContents().
* 090708   HARPLK  Bug 84436, Modified preDefine().
* SHAFLK   100210  Bug 88904, Modified printContents().
* ----------------------------------------------------------------------------
*/


package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ActiveSeparateReportCOInfo extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateReportCOInfo");

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

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String cbWarrantyVar;
    private String rWarrDesc;
    private ASPTransactionBuffer trans;
    //private String warrOk;
    private String qrystr;
    private String rRowN;
    private String rRawId;
    private boolean newWarr;
    private String callingUrl;
    private ASPBuffer temp;
    private int currrow;
    private String val;
    private ASPCommand cmd;
    private ASPBuffer data;
    private int headrowno;
    private String warrantyVar;
    private String warrantyRowNo;
    private ASPBuffer row;
    private ASPTransactionBuffer secBuff;

    //Web Alignment - simplify code for RMBs
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    //

    //================================================================================
    // ASP2JAVA WARNING: Types of the following variables are not identified by
    // the ASP2JAVA converter. Define them and remove these comments.
    //================================================================================
    private String sRegDatePassed;
    private String sMchCode;
    private String sMchNameDes;
    private String sWorkTypeID;
    private String openCustObj;
    private String openObjectCust;
    private String grapStructure;
    private String rWarrType;
    private String isSecure[];  
    //Bug Id 70012,Start
    private boolean bPcmsciExist;
    private String sPlanSDate;
    private String sWorkTypeId;
    private String sConTypeDb;
    private ASPBuffer r;
    private boolean bValContrId;
    // Bug Id 70012,End
    //Bug 84436, Start
    private ASPField f;
    //Bug 84436, End
    
    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparateReportCOInfo(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }


    public void run() 
    {
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        qrystr = ctx.readValue("QRYSTR","");
        rWarrType =ctx.readValue("RWARRTYPE",rWarrType);
        rWarrDesc =ctx.readValue("RWARRDESC",rWarrDesc);
        rRowN = ctx.readValue("RROWN","");
        rRawId = ctx.readValue("RRAWID","");
        newWarr = ctx.readFlag("NEWWARR",false);  
        //Bug Id 70012, Start
	bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
	sWorkTypeId = ctx.readValue("WORKTYPEID","");
	sPlanSDate = ctx.readValue("PLANSDATE","");
	sConTypeDb = ctx.readValue("CONNECTIONTYPEDB","");
        //Bug Id 70012, End

        callingUrl = ctx.getGlobal("CALLING_URL");

	isSecure = new String[14] ;

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
        {
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("RWARRTYPEENT")))
        {

            okFind();

            rWarrType = mgr.readValue("RWARRTYPEENT");
            rWarrDesc = mgr.URLDecode(mgr.readValue("RWARRDESC"));
            rRowN = mgr.readValue("RROWNENT");
            rRawId = mgr.readValue("RWARRIDENT");  

            temp = headset.getRow();
            temp.setValue("CUST_WARR_TYPE_DUMMY",rWarrType);
            temp.setValue("CUST_WARR_DESC_DUMMY",rWarrDesc);


            headset.setRow(temp);
            headlay.setLayoutMode(headlay.EDIT_LAYOUT);

            newWarr=true;   
            mgr.showAlert(mgr.translate("CHANGEWARRANPOST: Adding or modifying Warranty Type will not update any existing postings on this Work Order."));
        }
	//Bug Id 70012, Start
	else if (!mgr.isEmpty(mgr.readValue("TEMPCONTRACTID"))) {
	       updateContract();
	}

        if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_PLN_S_DATE"))){
	    sPlanSDate = mgr.readValue("PASS_PLN_S_DATE");
	}
        if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_WORK_TYPE_ID"))){
            headset.setValue("WORK_TYPE_ID_DUMMY",mgr.readValue("PASS_WORK_TYPE_ID"));
	    sWorkTypeId = mgr.readValue("PASS_WORK_TYPE_ID");
	}

	if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_CON_TYPE_DB"))) {
	    sConTypeDb = mgr.readValue("PASS_CON_TYPE_DB");
	}
	//Bug Id 70012, End

        adjust();

        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("RWARRTYPE",rWarrType);
        ctx.writeValue("RWARRDESC",rWarrDesc);
        ctx.writeValue("RROWN",rRowN);
        ctx.writeValue("RRAWID",rRawId);
        ctx.writeFlag("NEWWARR",newWarr);
        //Bug Id 70012, Start
        ctx.writeFlag("PCMSCIEXIST", bPcmsciExist);
	ctx.writeValue("WORKTYPEID",sWorkTypeId);
	ctx.writeValue("PLANSDATE",sPlanSDate);
	ctx.writeValue("CONNECTIONTYPEDB",sConTypeDb);
        //Bug Id 70012, End
    }


//-----------------------------------------------------------------------------
//-----------------------------  UTILITY FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public boolean checksec(String method, int ref) 
    {
        String splitted[] = new String[14];
        ASPManager mgr = getASPManager();

        splitted = null;

        isSecure[ref] = "false"; 
        splitted = split(method, "."); 

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(splitted[0], splitted[1]);

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure[ref] = "true";
            return true; 
        }
        else
            return false;
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String txt;
        val = mgr.readValue("VALIDATE");

        if ("CUSTOMER_NO".equals(val))
        {
            ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("Cust_Ord_Customer");

            secBuff = mgr.perform(secBuff);
            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
            {
                cmd = trans.addCustomFunction("ISCRESTOP","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","CREDITSTOP");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("COMPANY");
            }

            cmd = trans.addCustomFunction("CUSTDESC","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
                txt = (trans.getValue("ISCRESTOP/DATA/CREDITSTOP")) +"^"+ (mgr.isEmpty(trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION"))? "":trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION")) +"^";
            else
                txt = "2" +"^"+ (mgr.isEmpty(trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION"))? "":trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION")) +"^";

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

            if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
	    {
               cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo);
	    }

	    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1))
	    {
               cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo );
	    }
	    
	    if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1))
	    {
               cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
               cmd.addParameter("AGREEMENT_ID");
               cmd.addParameter("AGREEMENT_DESC");
               cmd.addParameter("CONTRACT_ID",sContractId);
	    }

            trans = mgr.validate(trans);

            sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");
            
            txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
		   (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ; 
            mgr.responseWrite(txt);

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
        
            if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
	    {
               cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo);
	    }

	    if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1))
	    {
               cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	    }

	    if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1))
	    {
               cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
               cmd.addParameter("CONTRACT_ID", sContractId);
	       cmd.addParameter("LINE_NO", sLineNo );
	    }

	    if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1))
	    {
               cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
               cmd.addParameter("AGREEMENT_ID");
               cmd.addParameter("AGREEMENT_DESC");
               cmd.addParameter("CONTRACT_ID",sContractId);
	    }
	    
            trans = mgr.validate(trans);

            sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");
	    
            txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
		   (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ; 

            mgr.responseWrite(txt);
        
        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

    public void  custWarr()
    {

        ASPManager mgr = getASPManager();
        currrow = headset.getCurrentRowNo();
        headset.goTo(currrow);
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "WarrantyTypeDlg.page?MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+"&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+"&ROW_NO="+mgr.URLEncode(headset.getRow().getValue("OBJ_CUST_WARRANTY"))+"&WARRANTY_ID="+mgr.URLEncode(headset.getRow().getValue("CUST_WARRANTY"))+"&WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&FRMNAME=CORepInfo"+"&QRYSTR="+mgr.URLEncode(qrystr);
        newWinHandle = "custwarr";
        //

    }

    //Bug Id 70012, Start
    public void updateContract()
    {
        ASPManager mgr = getASPManager();
        if (!headlay.isEditLayout())
      	  headlay.setLayoutMode(headlay.EDIT_LAYOUT);
        r = headset.getRow();
        r.setValue("CONTRACT_ID",mgr.readValue("TEMPCONTRACTID"));
        r.setValue("LINE_NO",mgr.readValue("TEMPLINENO"));
        headset.setRow(r);
        bValContrId = true;
    }
    //Bug Id 70012, End

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }

    public void  saveReturn()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();

        if (newWarr)
        {
            temp = headset.getRow();

            temp.setValue("OBJ_CUST_WARRANTY",rRowN);
            temp.setValue("CUST_WARRANTY",rRawId);
            temp.setValue("CUST_WARR_TYPE",rWarrType);
            headset.setRow(temp);
            headset.setEdited("CUST_WARR_TYPE,OBJ_CUST_WARRANTY,CUST_WARRANTY");
            newWarr = false;
        }

        currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
        headlay.setLayoutMode(headlay.getHistoryMode());    
    }

    public void cancelEdit()
    {
        ASPManager mgr = getASPManager();

        headlay.setLayoutMode(headlay.getHistoryMode());
        headset.resetRow();
        headtbl.clearQueryRow();


        if (newWarr)
        {
            ASPBuffer temp = headset.getRow();
            temp.setValue("CUST_WARR_TYPE_DUMMY","");
            temp.setValue("CUST_WARR_DESC_DUMMY","");
            headset.setRow(temp);
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFONODATA: No data found."));
            headset.clear();
        }
    }

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void  setCheckBoxValue()
    {
        ASPManager mgr = getASPManager();

        double warranty_row_no = headset.getRow().getNumberValue("OBJ_CUST_WARRANTY");  
        if (isNaN(warranty_row_no))
            warranty_row_no = 0;

        if (warranty_row_no == 0)
            cbWarrantyVar = "FALSE";
        else
            cbWarrantyVar = "TRUE";

        row = headset.getRow();
        row.setValue("WARRANTYWO",cbWarrantyVar);
        headset.setRow(row);
    }

// ------------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------------

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("WO_NO","Number","#").
        setSize(13).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOHEADWONO: WO No").
        setReadOnly().
        setDynamicLOV("ACTIVE_WORK_ORDER",600,445).
        setMaxLength(8);

        headblk.addField("MCH_CODE").
        setSize(13).
        setMaxLength(100).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOMCH_CODE: Object ID").
        setUpperCase().
        setReadOnly();

        headblk.addField("DESCRIPTION").
        setSize(28).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFODESCRIPTION: Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("CONTRACT").
        setSize(5).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCONTRACT: Site").
        setUpperCase().
        setReadOnly();

        headblk.addField("PHONE_NO").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2RMBPHONENO: Phone No:").
        setMaxLength(20);

        headblk.addField("CONTACT").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2RMBCONTACT: Contact:").
        setMaxLength(30);  

        headblk.addField("STATE").
        setSize(11).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOSTATE: Status").
        setReadOnly().
        setMaxLength(30);

        headblk.addField("CUSTOMER_NO").
        setSize(11).
        setDynamicLOV("CUSTOMER_INFO",600,445).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTOMER_NO: Customer No").
        //setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)").
        setCustomValidation("CUSTOMER_NO,COMPANY","CREDITSTOP,CUSTOMERDESCRIPTION").
        setUpperCase();

        headblk.addField("CREDITSTOP").
        setFunction("''").
        setHidden();      

        headblk.addField("CUSTOMERDESCRIPTION").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTOMER_NO_DESC: Customer Description").
        setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");

        //Bug 84436, Start
        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(11);
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);          
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
        else
            f.setFunction("''");        
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOAUTHORIZE_CODE: Coordinator");
        f.setUpperCase();
        //Bug 84436, End  

        headblk.addField("CONTRACT_ID").
        setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE").
        setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,AGREEMENT_ID,AGREEMENT_DESC").
        setUpperCase().
        setMaxLength(15).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCONTRACTID: Contract ID").
        setSize(15);

        //Bug 84436, Start  
        f = headblk.addField("CONTRACT_NAME");         
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
        else
            f.setFunction("''");       
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOCONTRTNAME: Contract Name");
        f.setSize(15);
        //Bug 84436, End 

        headblk.addField("LINE_NO").
        setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID").
        setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,AGREEMENT_ID,AGREEMENT_DESC").
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOLINENO: Line No").
        setSize(10);                                                   
              
        //Bug 84436, Start
        f = headblk.addField("LINE_DESC");  
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOLINEDESC: Description");
        f.setSize(15);

        
        f = headblk.addField("CONTRACT_TYPE"); 
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
        else
            f.setFunction("''");        
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOCONTTYPE: Contract Type");
        f.setSize(15);
        
        f = headblk.addField("INVOICE_TYPE");  
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOINVTYPE: Invoice Type");
        f.setSize(15);
        //Bug 84436, End 

        headblk.addField("AGREEMENT_ID").
        setSize(11).
	setHidden().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOAGREEMENT_ID: Agreement ID").
        setUpperCase();

	headblk.addField("AGREEMENT_DESC").
	setFunction("''").
	setHidden();

        //Bug 84436, Start
        f = headblk.addField("AGREEMENTTYPE");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEREPORTCOINFOCONTRACTTYPE: Contract Type");          
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_Api.Get_Contract_Type(:CONTRACT_ID)");
        else
            f.setFunction("''");
        //Bug 84436, End 
        
        headblk.addField("CUST_ORDER_TYPE").
        setSize(11).
        setDynamicLOV("CUST_ORDER_TYPE",600,445).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUST_ORDER_TYPE: Cust Order Type");

        headblk.addField("CUSTORDERTYPEDESCRIPTION").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTOMER_ORD_DESC: Customer Order Type Description").
        setFunction("CUST_ORDER_TYPE_API.Get_Description(:CUST_ORDER_TYPE)");
        mgr.getASPField("CUST_ORDER_TYPE").setValidation("CUSTORDERTYPEDESCRIPTION");


        headblk.addField("REFERENCE_NO").
        setSize(25).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOREFERENCE_NO: Reference No");

        headblk.addField("COMPANY").
        setHidden().
        setUpperCase(); 

        headblk.addField("CURRENCY_CODE").
        setSize(11).
        setDynamicLOV("CURRENCY_CODE","COMPANY",600,445).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCURRENCY_CODE: Currency");

        headblk.addField("DUMMY").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFODUMMY:           ").
        setFunction("''");

        headblk.addField("WARRANTY_ROW_NO").
        setHidden();

        headblk.addField("WARRANTYWO").
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOWARRANTYWO: Warranty Work Order").
        setFunction("Active_Separate_API.Has_Cust_Warr_Type(:WO_NO)").
        setReadOnly().
        setCheckBox("FALSE,TRUE");

        headblk.addField("MUL_CSUT_EXIST").
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOMULCSUTEXIST: Multiple Customer Exist").
        setFunction("WORK_ORDER_CODING_UTILITY_API.Multiple_Customer_Exist(:WO_NO)").
        setReadOnly().
        setCheckBox("FALSE,TRUE");     

        headblk.addField("CUST_ORDER_NO").
        setSize(16).
        setDynamicLOV("ACTIVE_SEPARATE_CUSTOMER_ORDER","CONTRACT ORDER_CONTRACT,CUSTOMER_NO,CUST_ORDER_TYPE ORDER_ID,CURRENCY_CODE,AUTHORIZE_CODE",600,445).
        setQueryable().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUST_ORDER_NO: Customer Order Reference"); 

        headblk.addField("DELIVERYDATE","Datetime").
        setSize(30).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFODELIVERYDATE: Planned Delivery Date").
        setFunction("nvl(CUSTOMER_ORDER_LINE_API.GET_PLANNED_DELIVERY_DATE(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO),Customer_Order_API.Get_Wanted_Delivery_Date(:CUST_ORDER_NO))");

        headblk.addField("CUST_ORDER_LINE_NO").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUST_ORDER_LINE_NO: Line No");

        headblk.addField("CUST_ORDER_REL_NO").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUST_ORDER_REL_NO: Delivery No");

        headblk.addField("CUST_ORDER_LINE_ITEM_NO","Number").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUST_ORDER_LINE_ITEM_NO: Line Item No"); 

        headblk.addField("CUSTLINESALESPARTNO").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTLINESALESPARTNO: Sales Part No").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

        headblk.addField("CUSTLINESALESPARTDESC").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTLINESALESPARTNODESC: Sales Part Description").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_DESC(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)"); 

        headblk.addField("CUSTLINEINVPART").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTLINEINVPART: Inventory Part").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

        headblk.addField("CUSTLINEINVPARTDESC").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTLINEINVPARTDESC: Inventory Part Description").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_DESCRIPTION(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

        headblk.addField("BELONGSTOSITEAFTERDELIVERY").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOBELONGSTOSITEAFTERDELIVERY: Site").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_CONTRACT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");

        headblk.addField("BELONGSTOOBJECTAFTERDELIVERY").
        setSize(11).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOBELONGSTOOBJECTAFTERDELIVERY: Object").
        setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_OBJECT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");  

        headblk.addField("OBJECTAFTERDELIVERYDESC").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOOBJECTAFTERDELIVERYDESCRIPTION: Object Description").
        setDbName("NULL");   

        headblk.addField("LINE_STATUS").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOLINESTA: Line Status").
        setFunction("substr(Customer_Order_Line_API.Get_Objstate(CUST_ORDER_NO, CUST_ORDER_LINE_NO, CUST_ORDER_REL_NO, CUST_ORDER_LINE_ITEM_NO), 1, 35)");   

        headblk.addField("ORDER_STATUS").
        setSize(20).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOORDERSTA: Order Status").
        setFunction("substr(Customer_Order_API.Get_Objstate(CUST_ORDER_NO), 1, 35)");   

        headblk.addField("OBJ_CUST_WARRANTY").
        setHidden(); 

        headblk.addField("CUST_WARRANTY").
        setHidden();  

        headblk.addField("CUST_WARR_TYPE").
        setSize(16).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTWARRTYPE: Warranty Type").
        setUpperCase(); 

        headblk.addField("CUST_WARR_TYPE_DUMMY").
        setSize(16).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTWARRTYPE: Warranty Type").
        setReadOnly(). 
        setFunction("''").
        setUpperCase();  

        headblk.addField("WARRANTY_DESCRIPTION").
        setSize(50).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOWARRANTYDESCRIPTION: Warranty Description").
        setFunction("Cust_Warranty_Type_API.Get_Warranty_Description(:CUST_WARRANTY,:CUST_WARR_TYPE)"); 

        headblk.addField("CUST_WARR_DESC_DUMMY").
        setSize(50).
        setLabel("PCMWACTIVESEPARATEREPORTCOINFOCUSTWARRDESC: Warranty Description").
        setReadOnly(). 
        setFunction("''");

        //Bug Id 70012, Start
        headblk.addField("WORK_TYPE_ID_DUMMY").
        setSize(50).
        setReadOnly(). 
        setFunction("''");
        //Bug Id 70012, End

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOHD: CO Information"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.BACK);
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT,"cancelEdit");

        headbar.addCustomCommand("custWarr",mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOCUSTWARR: Customer Warranty Type..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
        headlay.setDialogColumns(2);
        headlay.defineGroup("","WO_NO,CONTRACT,MCH_CODE,DESCRIPTION,STATE",false,true);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOGRPLABEL0: Contact Information"),"CONTACT,PHONE_NO",true,true);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOGRPLABEL1: Order/Invoice Information"),"CUSTOMER_NO,CUSTOMERDESCRIPTION,AUTHORIZE_CODE,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,CUST_ORDER_TYPE,CUSTORDERTYPEDESCRIPTION,REFERENCE_NO,CURRENCY_CODE,MUL_CSUT_EXIST,CUST_WARR_TYPE,CUST_WARR_TYPE_DUMMY,WARRANTY_DESCRIPTION,CUST_WARR_DESC_DUMMY,DUMMY,WARRANTYWO",true,true);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOGRPLABEL2: After Sales Information"),"CUST_ORDER_NO,DELIVERYDATE,CUST_ORDER_LINE_NO,CUST_ORDER_REL_NO,CUST_ORDER_LINE_ITEM_NO,CUSTLINESALESPARTNO,CUSTLINESALESPARTDESC,ORDER_STATUS,CUSTLINEINVPART,CUSTLINEINVPARTDESC,LINE_STATUS",true,true);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEREPORTCOINFOGRPLABEL3: Belongs to After Delivery"),"BELONGSTOOBJECTAFTERDELIVERY,OBJECTAFTERDELIVERYDESC,BELONGSTOSITEAFTERDELIVERY",true,true);
 
        headlay.setSimple("CUSTOMERDESCRIPTION");
        headlay.setSimple("CUSTORDERTYPEDESCRIPTION");
        headlay.setSimple("CUSTLINESALESPARTDESC");
        headlay.setSimple("CUSTLINEINVPARTDESC");
        headlay.setSimple("OBJECTAFTERDELIVERYDESC");
        headlay.setSimple("WARRANTY_DESCRIPTION");
        headlay.setSimple("CUST_WARR_DESC_DUMMY");
	headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        headlay.setDataSpan("CUSTOMER_NO",2);
        headlay.setDataSpan("AGREEMENT_ID",2);
        headlay.setDataSpan("CUST_ORDER_TYPE",2);  
        headlay.setDataSpan("CURRENCY_CODE",2);  
    }


    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        if (newWarr)
        {
            mgr.getASPField("CUST_WARR_TYPE").setHidden();
            mgr.getASPField("WARRANTY_DESCRIPTION").setHidden();
        }
        else
        {
            mgr.getASPField("CUST_WARR_TYPE_DUMMY").setHidden();
            mgr.getASPField("CUST_WARR_DESC_DUMMY").setHidden();
        } 

        if ((headset.countRows()>0) && (!mgr.isEmpty(headset.getRow().getValue("CUST_ORDER_NO"))))
        {
            mgr.getASPField("CUST_ORDER_NO").setReadOnly();
            mgr.getASPField("CURRENCY_CODE").setReadOnly();
        }
        else
        {
            mgr.getASPField("CUST_ORDER_NO").unsetReadOnly();
            mgr.getASPField("CURRENCY_CODE").unsetReadOnly();
        }
        // Bug Id 70012, Start
	if (mgr.isModuleInstalled("PCMSCI")){
	    bPcmsciExist = true;
	}
	// Bug Id 70012, End
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVESEPARATEREPORTCOINFOTITLE: CO Information";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATEREPORTCOINFOTITLE: CO Information";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        //Bug Id 70012, Start
	printHiddenField("TEMPCONTRACTID","");
        printHiddenField("TEMPLINENO", "");
	//Bug Id 70012, End

        if (headlay.isVisible())
        {
            appendToHTML(headlay.show());
        }
        appendToHTML("<br>\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("window.name = \"CORepInfo\";\n");

        appendDirtyJavaScript("function validateCustomerNo(i)\n");
        appendDirtyJavaScript("{   \n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CREDITSTOP',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CUSTOMERDESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
        appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("    retStr = r.split(\"^\");\n");
        appendDirtyJavaScript("    if(retStr[0] == ' 1')\n");
        appendDirtyJavaScript("    {   \n");
        appendDirtyJavaScript("        message = '");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPORTCOINFOMSSG: Customer is Credit blocked"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("        window.alert(message);\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("    if( checkStatus_(r,'CUSTOMER_NO',i,'Customer No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CREDITSTOP',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CUSTOMERDESCRIPTION',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        //Web Alignment - simplify code for RMBs
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
        //

        
	//Bug Id 70012, Start
        appendDirtyJavaScript("function validateContractId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("		setDirty();\n");
        appendDirtyJavaScript("	if( !checkContractId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('CONTRACT_ID',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('LINE_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('CONTRACT_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CONTRACT_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('LINE_NO',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CONTRACT_NAME',i).value = '';\n");
        appendDirtyJavaScript("		getField_('LINE_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CONTRACT_TYPE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('INVOICE_TYPE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('AUTHORIZE_CODE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('AGREEMENT_DESC',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate3.page'+'?VALIDATE=CONTRACT_ID'\n");
        appendDirtyJavaScript("	+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
        appendDirtyJavaScript("	+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
        appendDirtyJavaScript("	);\n");
        appendDirtyJavaScript("	window.status='';\n");

        appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT_ID',i,'Contract ID') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('LINE_NO',i,1);\n");
        appendDirtyJavaScript("		assignValue_('CONTRACT_NAME',i,2);\n");
        appendDirtyJavaScript("		assignValue_('LINE_DESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('CONTRACT_TYPE',i,4);\n");
        appendDirtyJavaScript("		assignValue_('INVOICE_TYPE',i,5);\n");
        appendDirtyJavaScript("		assignValue_('CUSTOMER_NO',i,6);\n");
        appendDirtyJavaScript("		assignValue_('AUTHORIZE_CODE',i,7);\n");
        appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,8);\n");
        appendDirtyJavaScript("		assignValue_('AGREEMENT_DESC',i,9);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        
	if (bValContrId)
        {
      	  bValContrId = false;
      	  appendDirtyJavaScript("validateCustomerNo(0)\n");
      	  appendDirtyJavaScript("if (getValue_('CONTRACT_ID',0) != '')\n");
      	  appendDirtyJavaScript("	 validateContractId(0)\n");
        }

        appendDirtyJavaScript("function setContractId(contrId,lineNo)\n{\n");
        appendDirtyJavaScript("	document.form.TEMPCONTRACTID.value = contrId;\n");
        appendDirtyJavaScript("	document.form.TEMPLINENO.value = lineNo;\n");
        appendDirtyJavaScript("	f.submit();\n}\n");

        appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
	if (!sConTypeDb.equals("VIM")) {
	    appendDirtyJavaScript("		whereCond1 = '';\n"); 
	    appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CONTRACT.value)+ \"')) = 'TRUE' ) \" ;\n"); 
	    if (!sWorkTypeId.equals("null")) {
		appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode('"+ sWorkTypeId +"') + \"' \";\n"); 
	    }
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT_ID.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT_ID = '\" + URLClientEncode(document.form.CONTRACT_ID.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
	    //Bug Id 70156, Start
	    appendDirtyJavaScript(" 	{\n");
		if (bPcmsciExist) {
		    appendDirtyJavaScript("	      		whereCond1 += \" AND ((MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" OR (CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) \";\n"); 
		}else{
		    appendDirtyJavaScript("	      		whereCond1 += \" AND (MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') \";\n"); 
		}
	    appendDirtyJavaScript(" 	}\n");
	    //Bug Id 70156, End

	    if (bPcmsciExist) {	// Filtering by From Date
		if (!sPlanSDate.equals("null")) 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode('"+ sPlanSDate +"') + \"','YYYY-MM-DD-hh24.MI.ss'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
		else
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
	    }
	    appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
	    appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
	    appendDirtyJavaScript("							,550,500,'validateContractId');\n");
	}
	else{
        appendDirtyJavaScript("  window.open(\"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?__TRANSFER="));
        appendDirtyJavaScript("\"+ URLClientEncode('");
        appendDirtyJavaScript(headset.getSelectedRows("CUSTOMER_NO, CUSTOMERDESCRIPTION, WO_NO, CONTRACT, MCH_CODE, WORK_TYPE_ID_DUMMY").format());
        appendDirtyJavaScript("'),'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
	}
	appendDirtyJavaScript("}\n");

        
        appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");

	if (!sConTypeDb.equals("VIM")) {

	    appendDirtyJavaScript("		whereCond1 = '';\n"); 
	    appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
	    appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
	    appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CONTRACT.value)+ \"')) = 'TRUE' ) \" ;\n"); 
	    if (!sWorkTypeId.equals("null")) {
		appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode('"+ sWorkTypeId +"') + \"' \";\n"); 
	    }
	    appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
	    //Bug Id 70156, Start
	    appendDirtyJavaScript(" 	{\n");
		if (bPcmsciExist) {
		    appendDirtyJavaScript("	      		whereCond1 += \" AND ((MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" OR (CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) \";\n"); 
		}else{
		    appendDirtyJavaScript("	      		whereCond1 += \" AND (MCH_CODE = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' \";\n"); 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND MCH_CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') \";\n"); 
		}
	    appendDirtyJavaScript(" 	}\n");
	    //Bug Id 70156, End

	    if (bPcmsciExist) {	// Filtering by From Date
		if (!sPlanSDate.equals("null")) 
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode('"+ sPlanSDate +"') + \"','YYYY-MM-DD-hh24.MI.ss'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
		else
		    appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
	    }
	    appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
	    appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
	    appendDirtyJavaScript("							,550,500,'validateContractId');\n");
	}
	else{

        appendDirtyJavaScript("  window.open(\"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?__TRANSFER="));
        appendDirtyJavaScript("\"+ URLClientEncode('");
        appendDirtyJavaScript(headset.getSelectedRows("CUSTOMER_NO, CUSTOMERDESCRIPTION, WO_NO, CONTRACT, MCH_CODE, WORK_TYPE_ID_DUMMY").format());
        appendDirtyJavaScript("'),'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
	}
        appendDirtyJavaScript("}\n");
	//Bug Id 70012, End
    }

}
