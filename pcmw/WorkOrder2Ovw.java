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
*  File        : WorkOrder2Ovw.java 
*  Created     : ARWILK  010305  Created.
*  Modified    :
*  INROLK  021202  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.  
*  SHAFLK  021114  Bug ID 34159 Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method. 
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021211  Merged with 2002-3 SP3
*  THWILK  031020  Call Id 108383,Added format mask to WO_NO,LINE_NO & VERIFICATION_NO and allowed the browsing 
*                  facility when more than 100 records are been selected. Allowed to display only user allowed sites. 
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  THWILK  040603  Added PM_REVISION key field according to IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  SHAFLK  050518  Bug 51088, Added two fields.
*  NIJALK  050607  Merged 51088.
*  NEKOLK  050528  Bug 54198, Modified userwhere.
*  THWILK  051103  Merged Bug 54198.In addition fixed and error in the flow by changing setLablesAndLovs().
*  SHAFLK  060110  Bug 55490, Modified okFind().
*  SULILK  060207  Merged bug 55490.
*  NEKOLK  060621  Bug 58723, Added fld ALTERNATIVE_CUSTOMER.
*  NAMELK  060725  Merged Bug 58723.
*  AMDILK  060811  Bug 58216, Eliminated SQL errors in web applications. Modified methods countFind(), okFind()
*  AMDILK  060904  Merged with the Bug Id 58216
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  HARPLK  090709  Bug 84436, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrder2Ovw extends ASPPageProvider {
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrder2Ovw");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPBlock dummyblk;
    private ASPRowSet dummyset;
    private ASPBlock tempblk;
    private ASPRowSet tempset;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;
    private ASPField f;
    private ASPBlock b;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;


    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrder2Ovw(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();   

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        adjust();
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        ASPBuffer data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        preSearch();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("NOT (WORK_ORDER_ACCOUNT_TYPE = ?  AND  WORK_ORDER_COST_TYPE = ?) AND CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL) AND MCH_CODE_CONTRACT IN (Select User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) from DUAL)");
        q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset.getRow().getValue("REVENUE"));
	q.addParameter("WORK_ORDER_COST_TYPE", dummyset.getRow().getValue("WORK_ORDER_COST_TYPE"));
	q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        String n = headset.getRow().getValue("N");
        mgr.showAlert(mgr.translate("PCMWWORKORDER2OVWQRYCNT: Query will retrieve &1 line(s).",n));

    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        preSearch();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("NOT (WORK_ORDER_ACCOUNT_TYPE = ?  AND  WORK_ORDER_COST_TYPE = ?) AND CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL) AND MCH_CODE_CONTRACT IN (Select User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) from DUAL)");
        q.addParameter("WORK_ORDER_ACCOUNT_TYPE", dummyset.getRow().getValue("REVENUE"));
	q.addParameter("WORK_ORDER_COST_TYPE", dummyset.getRow().getValue("PERSONNEL"));
	if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
            mgr.showAlert(mgr.translate("PCMWWORKORDER2OVWNODATA: No data found."));

        mgr.createSearchURL(headblk);
    }


    public void  preSearch()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addEmptyQuery(dummyblk);
        q.includeMeta("ALL");
        mgr.submit(trans);
        trans.clear();
    }

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

        if (!mgr.isEmpty(modVersion))
            return true;
        else
            return false;
    }
    //Bug 34159, end


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        //====== DUMMY BLOCK ======
        dummyblk = mgr.newASPBlock("DUMMY");

        f = dummyblk.addField("REVENUE");
        f.setHidden();
        f.setFunction("Work_Order_Account_Type_API.get_client_value(1)");

        f = dummyblk.addField("PERSONNEL");
        f.setHidden();
        f.setFunction("Work_Order_Account_Type_API.get_client_value(0)");

        dummyblk.setView("DUAL");
        dummyset = dummyblk.getASPRowSet();


        //====== TEMP BLOCK ======

        tempblk = mgr.newASPBlock("TEMP");

        f = tempblk.addField("CONTRACT1");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("COMPANY1");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPART");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEA");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEB");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEC");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMED");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEE");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEF");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEG");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEH");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEI");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTNAMEJ");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("CODEPARTCOSTCENTER");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("VIEWNAME");
        f.setHidden();
        f.setFunction("''");


        f = tempblk.addField("PKGNAME");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("INTERNAME");
        f.setHidden();
        f.setFunction("''");

        f = tempblk.addField("LOGCODEPARTCOSTCENTER");
        f.setHidden();
        f.setFunction("''");


        //====== HEAD BLOCK ======

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        boolean orderInst = false; 
        boolean docmanInst = false; 

        f = headblk.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        if (isModuleInst("ORDER"))
            orderInst = true;
        else
            orderInst = false;

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(10);
        f.setDynamicLOV("WORK_ORDER",600,445);
        f.setHyperlink("WorkOrder2OvwRedirect.page","WO_NO","NEWWIN");
        f.setLabel("PCMWWORKORDER2OVWWONO: WO No");
        f.setReadOnly();

        f = headblk.addField("ROW_NO","Number","#");
        f.setSize(10);
        f.setLabel("PCMWWORKORDER2OVWROWNO: Line No");
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDER2OVWCONTRACT: WO Site");
        f.setReadOnly();
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWOBJSITE: List of Object Site"));

        f = headblk.addField("CONNECTION_TYPE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWCONNTYPE: Connection Type");
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("MCH_CODE");
        f.setSize(12);
        f.setMaxLength(100);
        f.setDynamicLOV("MAINTENANCE_OBJECT",600,445);
        f.setLabel("PCMWWORKORDER2OVWMCHCODE: Object ID");
        f.setReadOnly();
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWOBJIDLOV: List of Object ID"));

        f = headblk.addField("MCH_CODE_DESCRIPTION");
        f.setSize(20);
        f.setLabel("PCMWWORKORDER2OVWMCHNAME: Object Description");
        f.setReadOnly();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDER2OVWMCHCONTRACT: Site");
        f.setReadOnly();
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWOBJSITE: List of Object Site"));

        f = headblk.addField("ORG_CODE");
        f.setSize(14);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWWORKORDER2OVWORGCODE: Maintenance Organization");
        f.setReadOnly();
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWDEPORT1: List of Maintenance Organization"));

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(14);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setLabel("PCMWWORKORDER2OVWCUSTOMERNO: Customer No");
        f.setReadOnly();
        f.setUpperCase();
         //Bug 84436, Start
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
         else
            f.setFunction("''");
        //Bug 84436, End 
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWCUSTNO1: List of Customer No"));

        f = headblk.addField("SCUSTOMERNAME");
        f.setSize(18);
        f.setLabel("PCMWWORKORDER2OVWSCUSTOMERNAME: Customer Name");
        f.setReadOnly();
        //Bug 84436, Start
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))");
         else
            f.setFunction("''");
        //Bug 84436, End 
        mgr.getASPField("CUSTOMER_NO").setValidation("SCUSTOMERNAME");
       
        f = headblk.addField("QUOTATION_ID","Number");
        f.setSize(14);
        f.setDynamicLOV("WORK_ORDER_QUOTATION",600,445);
        f.setLabel("PCMWWORKORDER2OVWQUOTATIONID: Quotation ID");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWQUTOO: List of Quotation ID"));

        f = headblk.addField("CONTRACT_ID");
        f.setUpperCase();
        f.setMaxLength(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWCONTRACTID: Contract ID");
        f.setSize(15);

        f = headblk.addField("SRV_LINE_NO","Number"); 
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWWORKORDER2OVWLINENO: Line No");
        f.setSize(10);                                          

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(14);
        if (orderInst)
            f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
        f.setLabel("PCMWWORKORDER2OVWAGREEMENTID: Agreement ID");
        f.setReadOnly();
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWAGREEID: List of Agreement ID"));

        f = headblk.addField("ORDER_NO");
        f.setSize(20);
        f.setLabel("PCMWWORKORDER2OVWORDERNO: Customer Order No");
        f.setReadOnly();
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = headblk.addField("LINE_NO");
        f.setSize(10);
        f.setLabel("PCMWWORKORDER2OVWLINENO: Line No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REL_NO");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWRELNO: Release No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("LINE_ITEM_NO");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWLINEITEMNO: Line Item No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWKEEPREVENUE: Keep Revenue");
        f.setSelectBox();
        f.enumerateValues("KEEP_REVENUE_API");

        f = headblk.addField("STATE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWSTATUS: Status");
        f.setReadOnly();

        f = headblk.addField("WORK_ORDER_BOOK_STATUS");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWWORKORDERBOOKSTATUS: Booking Status");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REG_DATE","Datetime");
        f.setSize(20);
        f.setLabel("PCMWWORKORDER2OVWREGDATE: Registration Date");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COMPANY");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWWORKORDER2OVWCOMPANY: Company");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setUpperCase();

        f = headblk.addField("COST_CENTER");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("ACCNT");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PROJECT_NO");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("OBJECT_NO");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_C");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_D");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_G");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_H");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_I");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_J");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CRE_DATE","Date");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWCREDATE: Creation Date");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("VER_TYPE");
        f.setSize(18);
        f.setLabel("PCMWWORKORDER2OVWVERTYPE: Verification Type");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("VER_NO","Number","#");
        f.setSize(18);
        f.setLabel("PCMWWORKORDER2OVWVERNO: Verification No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("VER_DATE","Date");
        f.setSize(18);
        f.setLabel("PCMWWORKORDER2OVWVERDATE: Verification Date");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("QTY","Number");
        f.setSize(12);
        f.setLabel("PCMWWORKORDER2OVWQTY: Hours/Qty");
        f.setReadOnly();

        f = headblk.addField("AMOUNT","Number");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWAMOUNT: Cost Amount");
        f.setReadOnly();

        f = headblk.addField("CMNT");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWCMNT: Comment");
        f.setReadOnly();

        f = headblk.addField("PM_NO","Number");
        f.setSize(10);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWPMNO: PM No");
        f.setReadOnly();

        f = headblk.addField("PM_REVISION");
        f.setSize(10);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWPMREVISION: PM Revision");
        f.setReadOnly();

        f = headblk.addField("WORK_ORDER_COST_TYPE");
        f.setSize(24);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWWORKORDERCOSTTYPE: Work Order Cost Type");
        f.setReadOnly();

        f = headblk.addField("WORK_ORDER_ACCOUNT_TYPE");
        f.setHidden();
        f.setSize(26);
        f.setLabel("PCMWWORKORDER2OVWWORKORDERACCOUNTTYPE: Work Order AccountType");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("SIGNATURE");
        f.setSize(12);
        f.setLabel("PCMWWORKORDER2OVWSIGNATURE: Signature");
        f.setReadOnly();
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWSIGNA: List of Signature"));

        f = headblk.addField("SIGNATURE_ID");
        f.setSize(6);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWSIGNATUREID: Signature");
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("ROLE_CODE");
        f.setSize(10);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWWORKORDER2OVWROLECODE: Craft ID");
        f.setReadOnly();
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWCRAFTING: List of Craft ID"));

        f = headblk.addField("SPARE_ID");
        f.setSize(10);
        f.setDynamicLOV("SALES_PART","CONTRACT",600,445);
        f.setLabel("PCMWWORKORDER2OVWSPAREID: Part No");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWPART1: List of Part No"));

        f = headblk.addField("EMP_NO");
        f.setSize(16);
        f.setDynamicLOV("COMPANY_EMP","CONTRACT",600,445);
        f.setLabel("PCMWWORKORDER2OVWEMPNO: Employee No");
        f.setReadOnly();
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = headblk.addField("INVOICE_ROW_NO","Number");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWINVOICEROWNO: Invoice Row No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("INVOICE_ITEM_ID","Number");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWINVOICEITEMID: Invoice Item ID");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CATCONTRACT");
        f.setSize(18);
        f.setDynamicLOV("SITE",600,445);
        f.setLabel("PCMWWORKORDER2OVWCATCONTRACT: Sales Part Site");
        f.setReadOnly();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Contract(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDER2OVWSALESSDFA: List of Sales Part Site"));

        f = headblk.addField("CATALOGNO");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWCATALOGNO: Sales Part No");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Catalog_No(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("CATALOGDESC");
        f.setSize(26);
        f.setLabel("PCMWWORKORDER2OVWCATALOGDESC: Sales Part Description");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Catalog_Desc(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("CUST_STATE");
        f.setSize(8);
        f.setLabel("PCMWWORKORDER2OVWSTATE: Status");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_State(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("SALESUNITMEAS");
        f.setSize(26);
        f.setLabel("PCMWWORKORDER2OVWSALESUNITMEAS: Sales Unit Measurement");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Sales_Unit_Meas(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("BASESALEUNITPRICE","Number");
        f.setHidden();
        f.setSize(24);
        f.setLabel("PCMWWORKORDER2OVWBASESALEUNITPRICE: Base Sale Unit Price");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Base_Sale_Unit_Price(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("COST","Number");
        f.setHidden();
        f.setSize(22);
        f.setLabel("PCMWWORKORDER2OVWCOST: Customer Order Cost");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Cost(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("QTYINVOICED","Number");
        f.setSize(15);
        f.setLabel("PCMWWORKORDER2OVWQTYINVOICED: Qty Invoiced");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Qty_Invoiced(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("SUMINVOICED","Number");
        f.setSize(18);
        f.setLabel("PCMWWORKORDER2OVWSUMINVOICED: Sales Part Amount");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Sum_Invoiced_Base_Curr(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("QTYONORDER","Number");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWQTYONORDER: Qty on Order");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Qty_On_Order(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("BUYQTYDUE","Number");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWBUYQTYDUE: Buy Qty Due");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Buy_Qty_Due(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("CATALOGTYPE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWCATALOGTYPE: Catalog Type");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Catalog_Type(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("DATEENTERED");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDER2OVWDATEENTERED: Date Entered");
        f.setReadOnly();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Date_Entered(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("DISCOUNT","Number");
        f.setSize(12);
        f.setLabel("PCMWWORKORDER2OVWDISCOUNT: Discount");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Discount(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("NOTETEXT");
        f.setSize(20);
        f.setLabel("PCMWWORKORDER2OVWNOTETEXT: Note");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Note_Text(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("ORDERDISCOUNT","Number");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWORDERDISCOUNT: Order Discount");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Order_Discount(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("SALEUNITPRICE","Number");
        f.setSize(16);
        f.setLabel("PCMWWORKORDER2OVWSALEUNITPRICE: Sale Unit Price");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (orderInst)
            f.setFunction("Customer_Order_Line_API.Get_Sale_Unit_Price(:ORDER_NO,:LINE_NO,:REL_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");
        //Bug 31459, end

        f = headblk.addField("ALTERNATIVE_CUSTOMER");
        f.setSize(14);
        f.setLabel("PCMWWORKORDER2OVWALTERNATIVECUSTOMER: Alternative Customer");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("Work_Order_Coding_API.Get_Alternative_Customer(:WO_NO,:ROW_NO)");

        headblk.setView("WORK_ORDER2");
        headblk.defineCommand("WORK_ORDER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk); headbar.setBorderLines(false,true); 

        headbar.disableCommand(headbar.NEWROW);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 
        headlay.setDialogColumns(2);  
        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("SCUSTOMERNAME"); 
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDER2OVWHD: Overview  - WO Analysis of Cost and Revenue"));
        headtbl.setWrap();

        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.DUPLICATEROW);


        b = mgr.newASPBlock("WORK_ORDER_BOOK_STATUS");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("WORK_ORDER_COST_TYPE");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("WORK_ORDER_ACCOUNT_TYPE");

        b.addField("CLIENT_VALUES2");
    }


    public void  setLablesAndLovs()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPCommand cmd = trans.addCustomFunction( "CONT","User_Default_API.Get_Contract","CONTRACT1" );

        cmd = trans.addCustomFunction( "COMP","Site_API.Get_Company","COMPANY1" );
        cmd.addReference("CONTRACT1","CONT/DATA");

        cmd = trans.addCustomFunction( "CODEPARTA","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEA" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "A");

        cmd = trans.addCustomFunction( "CODEPARTB","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEB" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "B");

        cmd = trans.addCustomFunction( "CODEPARTC","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEC" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "C");

        cmd = trans.addCustomFunction( "CODEPARTD","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMED" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "D");

        cmd = trans.addCustomFunction( "CODEPARTE","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEE" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "E");

        cmd = trans.addCustomFunction( "CODEPARTF","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEF" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "F");

        cmd = trans.addCustomFunction( "CODEPARTG","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEG" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "G");

        cmd = trans.addCustomFunction( "CODEPARTH","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEH" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "H");

        cmd = trans.addCustomFunction( "CODEPARTI","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEI" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "I");

        cmd = trans.addCustomFunction( "CODEPARTJ","Accounting_Code_Parts_API.Get_Name","CODEPARTNAMEJ" );
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("CODEPART", "J");


        cmd = trans.addCustomCommand( "CODEPART1","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","CostCenter");

        cmd = trans.addCustomCommand( "CODEPART2","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","Project");

        cmd = trans.addCustomCommand( "CODEPART3","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","COMP/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","Object");

        trans = mgr.perform(trans);

        String sCompany = trans.getValue("COMP/DATA/COMPANY1");

        String sCodePartNameA = trans.getValue("CODEPARTA/DATA/CODEPARTNAMEA");
        String sCodePartNameB = trans.getValue("CODEPARTB/DATA/CODEPARTNAMEB");
        String sCodePartNameC = trans.getValue("CODEPARTC/DATA/CODEPARTNAMEC");
        String sCodePartNameD = trans.getValue("CODEPARTD/DATA/CODEPARTNAMED");
        String sCodePartNameE = trans.getValue("CODEPARTE/DATA/CODEPARTNAMEE");
        String sCodePartNameF = trans.getValue("CODEPARTF/DATA/CODEPARTNAMEF");
        String sCodePartNameG = trans.getValue("CODEPARTG/DATA/CODEPARTNAMEG");
        String sCodePartNameH = trans.getValue("CODEPARTH/DATA/CODEPARTNAMEH");
        String sCodePartNameI = trans.getValue("CODEPARTI/DATA/CODEPARTNAMEI");
        String sCodePartNameJ = trans.getValue("CODEPARTJ/DATA/CODEPARTNAMEJ");

        String viewName1 = trans.getValue("CODEPART1/DATA/VIEWNAME");
        String viewName2 = trans.getValue("CODEPART2/DATA/VIEWNAME");
        String viewName3 = trans.getValue("CODEPART3/DATA/VIEWNAME");

        mgr.getASPField("ACCNT").setLabel(sCodePartNameA);
        mgr.getASPField("COST_CENTER").setLabel(sCodePartNameB);
        mgr.getASPField("CODE_C").setLabel(sCodePartNameC);
        mgr.getASPField("CODE_D").setLabel(sCodePartNameD);
        mgr.getASPField("OBJECT_NO").setLabel(sCodePartNameE);
        mgr.getASPField("PROJECT_NO").setLabel(sCodePartNameF);
        mgr.getASPField("CODE_G").setLabel(sCodePartNameG);
        mgr.getASPField("CODE_H").setLabel(sCodePartNameH);
        mgr.getASPField("CODE_I").setLabel(sCodePartNameI);
        mgr.getASPField("CODE_J").setLabel(sCodePartNameJ); 

        mgr.getASPField("COST_CENTER").setDynamicLOV(viewName1,600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("PROJECT_NO").setDynamicLOV(viewName2,600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("OBJECT_NO").setDynamicLOV(viewName3,600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");  

        mgr.getASPField("CODE_C").setDynamicLOV("CODE_C",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'"); 

        mgr.getASPField("CODE_D").setDynamicLOV("CODE_D",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("CODE_G").setDynamicLOV("CODE_G",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("CODE_H").setDynamicLOV("CODE_H",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("CODE_I").setDynamicLOV("CODE_I",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("CODE_J").setDynamicLOV("CODE_J",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("ACCNT").setDynamicLOV("ACCOUNT",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

        mgr.getASPField("SIGNATURE").setDynamicLOV("EMPLOYEE_LOV",600,445).setLOVProperty("WHERE","COMPANY='"+sCompany+"'");   

    }


    public void  adjust()
    {
        setLablesAndLovs();
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDER2OVWTITLE: Overview - WO Analysis of Cost and Revenue";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDER2OVWTITLE: Overview - WO Analysis of Cost and Revenue";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (headlay.isVisible()) {
            appendToHTML(headlay.show());
        }

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
    }
}
