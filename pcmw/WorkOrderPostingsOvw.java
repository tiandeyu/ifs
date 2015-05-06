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
*  File        : WorkOrderPostingsOvw.java 
*  Created     : ARWILK  010305      
*  Modified    :
*  ARWILK  011001  Checked security for RMB's (checkObjAvailable).
*  SHAFLK  021114  Bug ID 34159 Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method. 
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021212  Merged with 2002-3 SP3
*  JEJALK  021213  Takeoff(beta) modifications.Added MCH_CODE_CONTRACT and CONNECTION_TYPE.
*  CHAMLK  031016  Modified functions authorizeSelectedRows and authorizeAllRows to prevent authorization 
*                  when state of the work order is below Released.
*  PAPELK  031017  Call Id 108398.Modified preDefine() and okFind().
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041112  Replaced getContents with printContents.
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  SHAFLK  050518  Bug 51088, Added two fields.
*  NIJALK  050607  Merged bug 51088.
*  NEKOLK  050528  Bug 54198, Modified userwhere.
*  THWILK  051103  Merged Bug 54198.
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderPostingsOvw extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderPostingsOvw");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
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
    private String sCostTypeMat;
    private String sCostType_2;
    private String sAccntType_0;
    private String sBookStatus_0;
    private int agreePriceFlag;
    private String authSigID;
    private ASPQuery q;
    private String n;
    private String calling_url;
    private String sSeparator;
    private int woNo;
    private String rowNos;
    private int sGetSelectedRows;
    private boolean flag;
    private String strCompany;
    //private String authFlag;
    private boolean[] isSecure;
    private boolean again;
    private ASPBuffer booleanBuffer;
    private String comp;

    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderPostingsOvw(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new boolean[6] ;
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();

        ctx = mgr.getASPContext();
        sCostTypeMat = ctx.readValue("SCOSTTYPEMAT","");
        sCostType_2 = ctx.readValue("SCOSTTYPE_2","");
        sAccntType_0 = ctx.readValue("SACCNTTYPE_0","");
        sBookStatus_0 = ctx.readValue("SBOOKSTATUS_0","");  
        again = ctx.readFlag("CTXAGAIN",false);
        booleanBuffer = ctx.readBuffer("CTXBOOLBUFF");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            okFind();
            mgr.setPageExpiring();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        checkObjAvailable();
        adjust();

        ctx.writeValue("SCOSTTYPEMAT",sCostTypeMat);   
        ctx.writeValue("SCOSTTYPE_2",sCostType_2);   
        ctx.writeValue("SACCNTTYPE_0",sAccntType_0);   
        ctx.writeValue("SBOOKSTATUS_0",sBookStatus_0);            

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPCommand cmd;

        double nAmount;
        String strCatalogDesc;
        String strClient3;
        int strAgreement;
        String strWorkOrderCost;
        int nQuantity;
        String txt;
        double nCost;

        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");

        if ("CATALOG_NO".equals(val))
        {
            if (checksec("Sales_Part_API.Get_Catalog_Desc",1))
            {
                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","LINE_DESCRIPTION");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("CATALOG_NO");
            }

            cmd = trans.addCustomFunction("CLIENT3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");

            trans = mgr.validate(trans);

            if (isSecure[1] == true)
                strCatalogDesc = trans.getValue("CATDESC/DATA/LINE_DESCRIPTION");
            else
                strCatalogDesc= "";

            strClient3 = trans.getValue("CLIENT3/DATA/CLIENTVAL3");

            nAmount = mgr.readNumberValue("AMOUNT");
            strAgreement = 0 ;

            strWorkOrderCost= mgr.readValue("WORK_ORDER_COST_TYPE");

            if (strClient3.equals(strWorkOrderCost))
            {
                nQuantity = toInt(mgr.readNumberValue("QTY"));
                if ((!isNaN(nQuantity))&&(nQuantity != 0))
                {
                    trans.clear();
                    if (checksec("Sales_Part_API.Get_Cost",1))
                    {
                        cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","COST_VAR");
                        cmd.addParameter("CONTRACT");
                        cmd.addParameter("CATALOG_NO");

                        trans = mgr.validate(trans);

                        nCost = isNaN(trans.getNumberValue("COSTING/DATA/COST_VAR"))?0 : trans.getNumberValue("COSTING/DATA/COST_VAR");
                    }
                    else
                        nCost = 0;
                    nAmount = nQuantity * nCost;   
                }
            }
            trans.clear();
            txt =  (mgr.isEmpty(strCatalogDesc) ? "" :strCatalogDesc) + "^" 
                   + (this.isNaN(nAmount) ? mgr.getASPField("AMOUNT").formatNumber(0.0): mgr.getASPField("AMOUNT").formatNumber(nAmount)) + "^";

            mgr.responseWrite(txt);
        }

        if ("LIST_PRICE".equals(val))
        {
            agreePriceFlag = 0;
            txt = (this.isNaN(agreePriceFlag) ? mgr.getASPField("AGREEMENT_PRICE_FLAG").formatNumber(0): mgr.getASPField("AGREEMENT_PRICE_FLAG").formatNumber(agreePriceFlag)) + "^";
            mgr.responseWrite(txt); 
        }

        if ("SIGNATURE".equals(val))
        {
            cmd = trans.addCustomFunction("MAXEMPID","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("SIGNATURE");

            trans = mgr.validate(trans); 
            authSigID = trans.getValue("MAXEMPID/DATA/SIGNATURE_ID");      

            txt = (mgr.isEmpty(authSigID) ? "" : (authSigID))+ "^" ;
            mgr.responseWrite(txt);        
        }
        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COM","Site_API.Get_Company", "COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.validate(trans);

            comp = trans.getValue("COM/DATA/COMPANY");

            txt = (mgr.isEmpty(comp) ? "" : (comp)) +"^";
            mgr.responseWrite(txt);
        }



        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------------  CMDBAR FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void checkObjAvailable()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            trans.clear();
            trans.addSecurityQuery("DOCUMENT_TEXT");
            trans.addPresentationObjectQuery("MPCCOW/DocumentTextDlg.page");

            trans = mgr.perform(trans);

            ASPBuffer availObj = trans.getSecurityInfo();

            booleanBuffer = mgr.newASPBuffer();

            if (availObj.namedItemExists("DOCUMENT_TEXT"))
                booleanBuffer.addItem("BOOLVIEW1","true");

            if (availObj.namedItemExists("MPCCOW/DocumentTextDlg.page"))
                booleanBuffer.addItem("BOOLPAGE1","true");

            again = true;
        }
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");   
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.addWhereCondition("MCH_CODE_CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL)");   

        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        n = headset.getRow().getValue("N");
        headlay.setCountValue(toInt(n));
        headset.clear();
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        q.addWhereCondition("CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");   
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.addWhereCondition("MCH_CODE_CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL)");   
        mgr.querySubmit(trans, headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWNODATA: No data found."));
        }
        mgr.createSearchURL(headblk);

        GetSalesPartInfo();
    }

//-----------------------------------------------------------------------------
//-------------------------   SECURITY FUNCTION  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods. This function is 
// called before the required method is performed. 

    public boolean checksec(String method,int ref) 
    {
        String[] splitted;
        ASPManager mgr = getASPManager();

        isSecure[ref] = false; 
        splitted = split(method,".");

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(splitted[0],splitted[1]);

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure[ref] = true;
            return true; 
        }
        else
            return false;
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWNONE: No RMB method has been selected."));
    }


    public void authorizeSelectedRows()
    {
        boolean authSelecFlag;
        ASPBuffer buffer;
        ASPBuffer row;
        ASPCommand cmd;

        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        if (checksec("Work_Order_Coding_API.Authorize", 1))
        {
            if (mgr.isEmpty(headset.getRow().getValue("SIGNATURE")))
                authSelecFlag = true;
            else
                authSelecFlag = false; 
        }
        else
        {
            authSelecFlag = false;      
        }

        if (authSelecFlag)
        {
            woNo = isNaN(headset.getRow().getNumberValue("WO_NO"))?0 : toInt(headset.getRow().getNumberValue("WO_NO"));

            // cannot Authorize if state is < Released
            cmd = trans.addCustomFunction("ISAUTHALLOWED", "Work_Order_Coding_API.Is_Authorisation_Allowed", "AUTH_ALLOWED");
            cmd.addParameter("WO_NO", Integer.toString(woNo));

            trans = mgr.perform(trans); 

            String sAuth = trans.getValue("ISAUTHALLOWED/DATA/AUTH_ALLOWED");

            if ("FALSE".equals(sAuth))
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWAUTHNOTALLOWED: Cannot Authoriz when State of the Work Order is below Released."));
            }
            else
            {

                bOpenNewWindow = true;
                urlString = createTransferUrl("AuthorizeCodingDlg.page", headset.getSelectedRows("COMPANY,WO_NO,ROW_NO"));
                newWinHandle = "authorizeSelectedRows"; 
            }
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWSELROWNOAUTH: Selected row cannot be Authorized.")); 
        }
    }


    public void authorizeAllRows()
    {
        int count;

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        boolean hasDataToTransfer = false;
        ASPCommand cmd;

        ASPManager mgr = getASPManager();

        if (checksec("Work_Order_Coding_API.Authorize",1))
        {
            calling_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL", calling_url);

            headset.unselectRows();
            headset.setFilterOff();

            trans.clear();

            for (int i = 0; i <= headset.countRows(); i++)
            {
                if (mgr.isEmpty(headset.getValue("SIGNATURE")))
                {
                    cmd = trans.addCustomFunction("ISAUTHALLOWED_" + i, "Work_Order_Coding_API.Is_Authorisation_Allowed","AUTH_ALLOWED");
                    cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
                }

                headset.next();
            }

            trans = mgr.perform(trans); 

            headset.first();

            transferBuffer = mgr.newASPBuffer();

            for (int i = 0; i <= headset.countRows(); i++)
            {
                rowBuff = mgr.newASPBuffer();

                if (mgr.isEmpty(headset.getValue("SIGNATURE")))
                {
                    if ("FALSE".equals(trans.getValue("ISAUTHALLOWED_" + i + "/DATA/AUTH_ALLOWED")))
                    {
                        mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWWOAUTHNOTALLOWED: Cannot Authorize Work Order &1 when State of the Work Order is below Released.", headset.getValue("WO_NO")));
                    }
                    else
                    {
                        if (!hasDataToTransfer)
                        {
                            rowBuff.addItem("COMPANY", headset.getValue("COMPANY"));
                            rowBuff.addItem("WO_NO", headset.getValue("WO_NO"));
                            rowBuff.addItem("ROW_NO", headset.getValue("ROW_NO"));
                            hasDataToTransfer = true;
                        }
                        else
                        {
                            rowBuff.addItem(null, headset.getValue("COMPANY"));
                            rowBuff.addItem(null, headset.getValue("WO_NO"));
                            rowBuff.addItem(null, headset.getValue("ROW_NO"));
                        }

                        transferBuffer.addBuffer("DATA", rowBuff);
                    }

                }

                headset.next();
            }

            bOpenNewWindow = true;
            urlString = createTransferUrl("AuthorizeCodingDlg.page", transferBuffer);
            newWinHandle = "authorizeAllRows"; 
        }
        else
            mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWNOAUTH: Cannot perform action Authorize."));    
    }

    public void documentText()
    {
        ASPBuffer buffer;
        ASPBuffer row;

        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url); 

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        if (!( mgr.isEmpty(headset.getRow().getValue("NOTE_ID")) ))
        {
            buffer = mgr.newASPBuffer();
            row = buffer.addBuffer("0");
            row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
            row = buffer.addBuffer("1");
            row.addItem("CATALOG_NO",headset.getRow().getValue("REQUISITION_LINE_NO"));
            row = buffer.addBuffer("2");
            row.addItem("NOTE_ID",headset.getRow().getValue("NOTE_ID"));
            row = buffer.addBuffer("3");
            row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
            row = buffer.addBuffer("4");
            row.addItem("MCH_NAME",mgr.readValue("MCH_NAME"));

            mgr.transferDataTo("../mpccow/DocumentTextDlg.page", buffer);
        }
        else
            mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWNODOCTEXT: Cannot perform Document Text."));     
    }


    public void salesPartComp()
    {
        ASPBuffer buffer;
        ASPBuffer row;

        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url); 

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        if (!mgr.isEmpty(headset.getRow().getValue("SIGNATURE")))
        {
            buffer = mgr.newASPBuffer();
            row = buffer.addBuffer("0");
            row.addItem("CATALOG_NO",headset.getRow().getValue("CATALOG_NO"));
            row = buffer.addBuffer("1");
            row.addItem("LIST_PRICE",headset.getRow().getValue("LIST_PRICE"));
            row = buffer.addBuffer("2");
            row.addItem("CONTRACT",headset.getRow().getFieldValue("CONTRACT"));
            row = buffer.addBuffer("3");
            row.addItem("LINE_DESCRIPTION",headset.getRow().getValue("LINEDESCRIPTION"));
            row = buffer.addBuffer("4");
            row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
            row = buffer.addBuffer("5");
            row.addItem("ROW_NO",headset.getRow().getValue("ROW_NO"));

            mgr.transferDataTo("SalesPartComplementaryDlg.page", buffer);
        }
        else
            mgr.showAlert(mgr.translate("PCMWWORKORDERPOSTINGSOVWSLSPRTDISABLED: Sales Part Complementary is disabled for this line."));
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

    private String createTransferUrl(String url, ASPBuffer object)
    {
        ASPManager mgr = getASPManager();

        try
        {
            String pkg = mgr.pack(object,1900 - url.length());
            char sep = url.indexOf('?')>0 ? '&' : '?';
            urlString = url + sep + "__TRANSFER=" + pkg ;
            return urlString;
        }
        catch (Throwable any)
        {
            return null;
        }
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

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
        f.setSize(8);
        f.setDynamicLOV("ACTIVE_WORK_ORDER",600,450);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWONO: WO No");

        f = headblk.addField("CONTRACT");
        f.setSize(6);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setCustomValidation("CONTRACT","COMPANY");
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCONTRACT: WO Site");
        f.setUpperCase();

        f = headblk.addField("CONNECTION_TYPE");
        f.setSize(16);
        f.setMandatory();
        f.setReadOnly();
        f.setHilite();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCONNECTIONTYPE: Connection Type");
        f.setUpperCase();

        f = headblk.addField("MCH_CODE");
        f.setSize(12);
        f.setMaxLength(100);
        f.setDynamicLOV("EQUIPMENT_ALL_OBJECT","MCH_CODE_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWMCHCODE: Object ID");
        f.setUpperCase();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(6);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWMCHCODECONTRACT: Site");
        f.setUpperCase();

        f = headblk.addField("MCHNAME");
        f.setSize(18);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWMCHNAME: Object Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
        mgr.getASPField("MCH_CODE").setValidation("MCHNAME");

        f = headblk.addField("ORG_CODE");
        f.setSize(8);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV",600,450);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWORGCODE: Exec Dept");
        f.setUpperCase();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(10);
        f.setDynamicLOV("CUSTOMER_INFO",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCUSTOMERNO: Customer No");

        f = headblk.addField("CUSTOMERNAME");
        f.setSize(18);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCUSTOMERNAME: Customer Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        mgr.getASPField("CUSTOMER_NO").setValidation("CUSTOMERNAME");

        f = headblk.addField("WORKORDERQUOTATIONID","Number");
        f.setSize(8);
        f.setDynamicLOV("WORK_ORDER_QUOTATION",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKORDERQUOTATIONID: Quotation Id");
        f.setFunction("WORK_ORDER_API.Get_Quotation_Id(:WO_NO)");


        f = headblk.addField("WORKORDERAGREEMENTID");
        f.setSize(8);
        if (orderInst)
            f.setDynamicLOV("CUSTOMER_AGREEMENT",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKORDERAGREEMENTID: Agreement Id");
        f.setFunction("WORK_ORDER_API.Get_Agreement_Id(:WO_NO)");

        f = headblk.addField("COMPANY");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCOMPANY: Company Id");
        f.setUpperCase();

        f = headblk.addField("WORK_ORDER_COST_TYPE");
        f.setSize(10);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKORDERCOSTTYPE: Cost Type");

        f = headblk.addField("CATALOG_NO");
        f.setSize(10);
        if (orderInst)
            f.setDynamicLOV("SALES_PART","CONTRACT",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCATALOGNO: Sales Part Number");
        f.setUpperCase();
        f.setCustomValidation("CATALOG_NO,COMPANY","LINE_DESCRIPTION,AMOUNT");

        f = headblk.addField("LINE_DESCRIPTION");
        f.setSize(15);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWLINEDESCRIPTION: Description");

        f = headblk.addField("PRICE_LIST_NO");
        f.setSize(8);
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWPRICELISTNO: Price List No");
        f.setUpperCase();

        f = headblk.addField("WORK_ORDER_ACCOUNT_TYPE");
        f.setSize(9);
        f.setHidden();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKORDERACCOUNTTYPE: Account Type");

        f = headblk.addField("QTY","Number");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWQTY: Hours/Qty");
        f.setCustomValidation("QTY","AGREEMENT_PRICE_FLAG");      

        f = headblk.addField("AMOUNT","Number");
        f.setSize(8);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCOSTAMOUNT: Cost Amount");

        f = headblk.addField("SALESPRICEAMOUNT","Number");
        f.setSize(8);
        f.setFunction("''");   
        f.setLabel("PCMWWORKORDERPOSTINGSOVWSALESPRICEAMOUNT: Sales Price Amount");

        f = headblk.addField("LIST_PRICE","Number");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWLISTPRICE: List Price");
        f.setCustomValidation("LIST_PRICE","AGREEMENT_PRICE_FLAG");   

        f = headblk.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWKEEPREVENUE: Keep Revenue");
        f.setSelectBox();
        f.enumerateValues("KEEP_REVENUE_API");

        f = headblk.addField("STATE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWSTATUS: Status");
        f.setReadOnly();   

        f = headblk.addField("WORK_ORDER_BOOK_STATUS");
        f.setSize(10);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKORDERBOOKSTATUS: Booking Status");

        f = headblk.addField("WORKTYPE");
        f.setSize(10);
        f.setDynamicLOV("WORK_TYPE",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWWORKTYPE: Work Type");
        f.setFunction("WORK_ORDER_API.Get_Work_Type_Id(:WO_NO)");
        mgr.getASPField("WO_NO").setValidation("WORKTYPE");

        f = headblk.addField("SIGNATURE");
        f.setSize(10);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setCustomValidation("SIGNATURE,COMPANY","SIGNATURE_ID");
        f.setLabel("PCMWWORKORDERPOSTINGSOVWSIGNATURE: Auth Signature");
        f.setUpperCase();

        f = headblk.addField("SIGNATURE_ID");
        f.setSize(10);
        f.setHidden();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWSIGNATUREID: Auth Signature");
        f.setUpperCase();

        f = headblk.addField("CRE_DATE","Date");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCREDATE: Creation Date");

        f = headblk.addField("VER_DATE","Date");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWVERDATE: Ver date");

        f = headblk.addField("CMNT");
        f.setSize(15);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCMNT: Comment");

        f = headblk.addField("ACCNT");
        f.setSize(8);
        f.setDynamicLOV("CODE_A","COMPANY",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWACCNT: Account");

        f = headblk.addField("COST_CENTER");
        f.setSize(8);
        f.setDynamicLOV("CODE_B","COMPANY",600,450);

        f = headblk.addField("PROJECT_NO");
        f.setSize(8);
        f.setDynamicLOV("CODE_F","COMPANY",600,450);

        f = headblk.addField("OBJECT_NO");
        f.setSize(8);
        f.setDynamicLOV("CODE_E","COMPANY",600,450);

        f = headblk.addField("CODE_C");
        f.setSize(8);
        f.setDynamicLOV("CODE_C","COMPANY",600,450);

        f = headblk.addField("CODE_D");
        f.setSize(8);
        f.setDynamicLOV("CODE_D","COMPANY",600,450);

        f = headblk.addField("CODE_G");
        f.setSize(8);
        f.setDynamicLOV("CODE_G","COMPANY",600,450);

        f = headblk.addField("CODE_H");
        f.setSize(8);
        f.setDynamicLOV("CODE_H","COMPANY",600,450);

        f = headblk.addField("CODE_I");
        f.setSize(8);
        f.setDynamicLOV("CODE_I","COMPANY",600,450);

        f = headblk.addField("CODE_J");
        f.setSize(8);
        f.setDynamicLOV("CODE_J","COMPANY",600,450);

        f = headblk.addField("ROW_NO","Number");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWWORKORDERPOSTINGSOVWROWNO: Row No");

        f = headblk.addField("CDOCUMENTTEXT");
        f.setSize(11);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWCDOCUMENTTEXT: Document Text");
        f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
        f.setCheckBox("0,1");

        f = headblk.addField("REQUISITION_NO");
        f.setSize(8);
        f.setDynamicLOV("WORK_ORDER_REQUIS_HEADER","WO_NO",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWREQUISITIONNO: Requisition No");

        f = headblk.addField("REQUISITION_LINE_NO");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWREQUISITIONLINENO: Requisition Line No");

        f = headblk.addField("REQUISITION_RELEASE_NO");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWREQUISITIONRELEASENO: Requisition Release No");

        f = headblk.addField("REQUISITIONVENDORNO");
        f.setSize(8);
        f.setDynamicLOV("SUPPLIER_INFO",600,450);
        f.setLabel("PCMWWORKORDERPOSTINGSOVWREQUISITIONVENDORNO: Requisition Supplier No");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
        mgr.getASPField("REQUISITION_NO").setValidation("REQUISITIONVENDORNO");

        f = headblk.addField("EMP_NO");
        f.setHidden();

        f = headblk.addField("CATALOGDESC");
        f.setHidden();
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");

        f = headblk.addField("NOTE_ID","Number");
        f.setHidden();

        f = headblk.addField("ACTIVEWORKORDERFIXEDPRICE");
        f.setHidden();
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:WO_NO)");

        f = headblk.addField("NOTFIXEDPRICE");
        f.setSize(8);
        f.setHidden();
        f.setFunction("Fixed_Price_API.Decode('1')");

        f = headblk.addField("AGREEMENT_PRICE_FLAG","Number");
        f.setHidden();


        f = headblk.addField("DESCRIPTION");
        f.setSize(8);
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setHidden();

        f = headblk.addField("STRING");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("SCOSTTYPEMAT");
        f.setHidden();
        f.setFunction("''");   

        f = headblk.addField("SCOSTTYPE_2");
        f.setHidden();
        f.setFunction("''");   

        f = headblk.addField("SACCNTTYPE_0");
        f.setHidden();
        f.setFunction("''");   

        f = headblk.addField("SBOOKSTATUS_0");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("BASEUNITPRICE","Number");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("SALEUNITPRICE","Number");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("DISCOUNT","Number");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("CURRENCYRATE","Number");
        f.setHidden();
        f.setFunction("''");  

        f = headblk.addField("PRICELISTNO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COST_VAR");
        f.setHidden();
        f.setFunction("''");      

        f = headblk.addField("AUTH_ALLOWED");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("WORK_ORDER_CODING");
        headblk.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.DUPLICATEROW);         

        headbar.addCustomCommand("none","");
        headbar.addCustomCommand("authorizeSelectedRows",mgr.translate("PCMWWORKORDERPOSTINGSOVWAUTHSELRO: Authorize Selected Row..."));
        headbar.addCustomCommand("authorizeAllRows",mgr.translate("PCMWWORKORDERPOSTINGSOVWAUTHALLNOAUHT: Authorize All Non Authorized..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("documentText",mgr.translate("PCMWWORKORDERPOSTINGSOVWDOCTEXT: Document Text..."));

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDERPOSTINGSOVWHD: Work Order Postings"));
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        b = mgr.newASPBlock("ORG_CODE");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("WORK_ORDER_COST_TYPE");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("WORK_ORDER_ACCOUNT_TYPE");

        b.addField("CLIENT_VALUES2");

        b = mgr.newASPBlock("WORK_ORDER_BOOK_STATUS");

        b.addField("CLIENT_VALUES3");
    }


    public void GetSalesPartInfo()
    {
        int count;
        double baseUnitPrice;
        double saleUnitPrice;
        double discount;
        double currencyRate;
        String priceListNo;
        int buyQtyDue;
        int noRows;
        ASPBuffer buf;

        ASPCommand cmd;
        ASPManager mgr = getASPManager();

        noRows =  headset.countRows();
        headset.first();
        if ((checksec("Company_Finance_API.Get_Currency_Code",2)) && (checksec("Customer_Order_Pricing_API.Get_Valid_Price_List",3)))
        {
            for (count=1; count<= noRows; ++count)
            {
                if ((!mgr.isEmpty(headset.getRow().getValue("CATALOG_NO"))) && (!mgr.isEmpty(headset.getRow().getValue("QTY"))))
                {
                    trans.clear();

                    cmd = trans.addCustomCommand("INFO", "Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASEUNITPRICE","");
                    cmd.addParameter("SALEUNITPRICE","");
                    cmd.addParameter("DISCOUNT","");
                    cmd.addParameter("CURRENCYRATE","");
                    cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
                    cmd.addParameter("CATALOG_NO",headset.getRow().getValue("CATALOG_NO"));
                    cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
                    cmd.addParameter("WORKORDERAGREEMENTID",headset.getRow().getValue("WORKORDERAGREEMENTID"));
                    cmd.addParameter("PRICELISTNO",headset.getRow().getValue("PRICE_LIST_NO"));
                    cmd.addParameter("QTY",headset.getRow().getValue("QTY"));

                    trans = mgr.perform(trans);

                    baseUnitPrice = isNaN(trans.getNumberValue("INFO/DATA/BASEUNITPRICE"))?0 : trans.getNumberValue("INFO/DATA/BASEUNITPRICE");
                    saleUnitPrice = isNaN(trans.getNumberValue("INFO/DATA/SALEUNITPRICE"))?0 : trans.getNumberValue("INFO/DATA/SALEUNITPRICE");
                    discount = isNaN(trans.getNumberValue("INFO/DATA/DISCOUNT"))?0 : trans.getNumberValue("INFO/DATA/DISCOUNT");
                    currencyRate = isNaN(trans.getNumberValue("INFO/DATA/CURRENCYRATE"))?0 : trans.getNumberValue("INFO/DATA/CURRENCYRATE");
                    priceListNo = trans.getValue("INFO/DATA/PRICELISTNO");
                    buyQtyDue = isNaN(headset.getRow().getNumberValue("QTY"))?0 : toInt(headset.getRow().getNumberValue("QTY"));

                    buf = headset.getRow();
                    if (mgr.isEmpty(headset.getRow().getValue("PRICE_LIST_NO")))
                        buf.setValue("PRICE_LIST_NO",priceListNo);
                    buf.setValue("LIST_PRICE",Double.toString(baseUnitPrice)); 
                    buf.setValue("SALESPRICEAMOUNT",Double.toString(baseUnitPrice*buyQtyDue)); 
                    headset.setRow(buf);                                                                     
                }
                headset.next();
            } 
            trans.clear();
        }
    }


    public void setLabelString()
    {
        ASPCommand cmd; 

        String strLableA;
        String strLableB;
        String strLableC;
        String strLableD;
        String strLableE;
        String strLableF;
        String strLableG;
        String strLableH;
        String strLableI;
        String strLableJ;    

        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("DEFCONTRACT", "User_Default_API.Get_Contract","CONTRACT" );

        cmd = trans.addCustomFunction("DEFCOMPANY", "Site_API.Get_Company", "COMPANY" );
        cmd.addReference("CONTRACT","DEFCONTRACT/DATA"); 

        cmd = trans.addCustomFunction("STRA", "Accounting_Code_Parts_API.Get_Name", "ACCNT" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","A");

        cmd = trans.addCustomFunction("STRB", "Accounting_Code_Parts_API.Get_Name", "COST_CENTER" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","B");

        cmd = trans.addCustomFunction("STRC", "Accounting_Code_Parts_API.Get_Name", "CODE_C" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","C");

        cmd = trans.addCustomFunction("STRD", "Accounting_Code_Parts_API.Get_Name", "CODE_D" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","D");

        cmd = trans.addCustomFunction("STRE", "Accounting_Code_Parts_API.Get_Name", "OBJECT_NO" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","E");

        cmd = trans.addCustomFunction("STRF", "Accounting_Code_Parts_API.Get_Name", "PROJECT_NO" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","F");

        cmd = trans.addCustomFunction("STRG", "Accounting_Code_Parts_API.Get_Name", "CODE_G" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","G");

        cmd = trans.addCustomFunction("STRH", "Accounting_Code_Parts_API.Get_Name", "CODE_H" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","H");

        cmd = trans.addCustomFunction("STRI", "Accounting_Code_Parts_API.Get_Name", "CODE_I" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","I");

        cmd = trans.addCustomFunction("STRJ", "Accounting_Code_Parts_API.Get_Name", "CODE_J" );
        cmd.addReference("COMPANY", "DEFCOMPANY/DATA");
        cmd.addParameter("STRING","J");

        trans = mgr.perform(trans); 

        strLableA = trans.getValue("STRA/DATA/ACCNT");
        strLableB = trans.getValue("STRB/DATA/COST_CENTER");
        strLableC = trans.getValue("STRC/DATA/CODE_C");
        strLableD = trans.getValue("STRD/DATA/CODE_D");
        strLableE = trans.getValue("STRE/DATA/OBJECT_NO");
        strLableF = trans.getValue("STRF/DATA/PROJECT_NO");
        strLableG = trans.getValue("STRG/DATA/CODE_G");
        strLableH = trans.getValue("STRH/DATA/CODE_H");
        strLableI = trans.getValue("STRI/DATA/CODE_I");
        strLableJ = trans.getValue("STRJ/DATA/CODE_J"); 

        mgr.getASPField("ACCNT").setLabel(strLableA);
        mgr.getASPField("COST_CENTER").setLabel(strLableB);
        mgr.getASPField("CODE_C").setLabel(strLableC );
        mgr.getASPField("CODE_D").setLabel(strLableD);
        mgr.getASPField("OBJECT_NO").setLabel(strLableE);
        mgr.getASPField("PROJECT_NO").setLabel(strLableF);
        mgr.getASPField("CODE_G").setLabel(strLableG );
        mgr.getASPField("CODE_H").setLabel(strLableH );
        mgr.getASPField("CODE_I").setLabel(strLableI);
        mgr.getASPField("CODE_J").setLabel(strLableJ);
        trans.clear();       
    }


    public void getClientVals()
    {
        ASPCommand cmd; 
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addCustomFunction("COSTCLIENT1", "Work_Order_Cost_Type_Api.Get_Client_Value(1)", "SCOSTTYPEMAT" );

        cmd = trans.addCustomFunction("COSTCLIENT2", "Work_Order_Cost_Type_API.Get_Client_Value(2)", "SCOSTTYPE_2" );

        cmd = trans.addCustomFunction("ACCCLIENT0", "Work_Order_Account_Type_API.Get_Client_Value(0)", "SACCNTTYPE_0" );

        cmd = trans.addCustomFunction("BSTATCLIENT0", "Work_Order_Book_Status_API.Get_Client_Value(0)", "SBOOKSTATUS_0" );

        trans = mgr.perform(trans); 

        sCostTypeMat = trans.getValue("COSTCLIENT1/DATA/SCOSTTYPEMAT");
        sCostType_2 = trans.getValue("COSTCLIENT2/DATA/SCOSTTYPE_2");
        sAccntType_0 = trans.getValue("ACCCLIENT0/DATA/SACCNTTYPE_0");
        sBookStatus_0 = trans.getValue("BSTATCLIENT0/DATA/SBOOKSTATUS_0");
        trans.clear();
    }   

    public void adjust()
    {
        for (int i=1;i<=booleanBuffer.countItems();i++)
        {
            if ((!booleanBuffer.itemExists("BOOLVIEW"+i))||(!booleanBuffer.itemExists("BOOLPAGE"+i)))
            {
                switch (i)
                {
                case 1: headbar.removeCustomCommand("documentText");break;        
                }         
            }
        }

        getClientVals();
        setLabelString();     
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERPOSTINGSOVWWOPOS: Overview - Work Order Postings";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERPOSTINGSOVWWOPOS: Overview - Work Order Postings";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        appendDirtyJavaScript("window.name = \"WorkOrdPostings\";\n");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
    }
}
