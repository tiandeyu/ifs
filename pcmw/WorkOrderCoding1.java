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
*  File        : WorkOrderCoding1.java 
*  Created     : ASP2JAVA Tool  010408
*  Modified    :  
*  BUNILK  010416  Corrected some conversion errors. Redone all validation parts.
*                  Modified some rmb methods adjust(),run() and populating methods.                           
*  BUNILK  010423  Modified ofFindNew() method so as to retrieved Sale Price Amount 
*                  from "Create from Planning"  
*  JEWILK  010427  Modified method authorizeAllRows() to display an error message when all the 
*                  rows are already authorized.   
*  VAGULK  010725  Added the fiels Qty 2 Invoice to the Postings Ovw  
*  ARWILK  010808  Corrected the functionality of Connect/Reconnect to Planning.      
*  JEWILK  010816  Modified function connectToPlanPosting() to disable the action also when 
*                  the 'Booking Status' is 'Transferred' or 'Authorised'.
*  ARWILK  010817  Changed saveReturn function. The attribute string made, is independent
*                  of the fields that are in the respective block.
*  JEWILK  010821  Added new action 'Manage Fixed Price Postings'.  
*  JEWILK  010822  Set field 'LINEDESCRIPTION' default visible. Modified to return to the 
*                  original layout mode when returning from action 'Sales Part Complimentary'.  
*  JEWILK  010823  Modified method 'authorizeSelectedRows' to transfer the seperator symbol also. (call # 77999)
*  JEWILK  010831  Modified to authorize all rows when performed in the single mode also.
*  SHAFLK  021114  Bug ID 34159 Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method.
*  BUNILK  021211  Merged with 2002-3 SP3
*  CHAMLK  021218  Added Craft Row Number and Craft Reference Number. 
*  JEJALK  030331  Renamed Craft Row Number and Craft Reference Number to Operation No and Reference No.
*  CHAMLK  030826  Added Ownership, Owner and Owner Name.
*  CHAMLK  031016  Modified functions authorizeSelectedRows, authorizeAllRows and authorizeCorrect to prevent authorization 
*                  when state of the work order is below Released.
*  SAPRLK  031230  Web Alignment - removed methods clone() and doReset(), replacing Links with RMBs, enabling multirow for RMBs, 
*                  code formatting for RMBs opening in a new window.
*  ARWILK  040210  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* ------------------------------ Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  SHAFLK  040707  Bug Id 44442,added method setSalesPartDesc() and replaced LINEDESCRIPTION with LINE_DESCRIPTION.
*  THWILK  040806  Merged Bug 44442. 
*  NEKOLK  040708  Bug ID 43810,Modified preDefine(),okFindNeW().
*  THWILK  040817  Merged Bug 43810. 
*  ARWILK  040824  Call ID 117186, Modifed preDefine, okFind, okFindNew, okFindSave. 
*  ARWILK  040901  Call ID 117307, Modified validation for CATALOG_NO.
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  Nijalk  050110  Renamed check box label 'Use Cust. Agreement' to 'Use Price Logic'.
*  DiAmlk  050301  Modified the method validate.
*  SHAFLK  050401  Bug 50156, Modified validate(),salesPartComp(),saveReturn() and  preDefine() methods.
*  NIJALK  050531  Merged bug 50156. 
*  SHAFLK  050518  Bug ID 51088,Added field Status
*  NIJALK  050607  Merged 51088.
*  NIJALK  050826  Bug 126558: Modified preDefine().
*  NIJALK  050902  Added fields TRANSACTION_ID,SEARCH to headblock, and added hyperlink to TRANSACTION_ID.
*                  Added Method checkObjAvaileble().
*  NIJALK  051209  Added column CSS_TYPE to headblk.
*  NIJALK  060202  Call 132126: Renamed RMB "Manage Fixed Price Postings..." to "Manage Sales Revenues...". Disabled it 
*                  if there is no customer for Work Order.
*  THWILK  060307  Fixed Call136568,Modified predefine().
* ----------------------------------------------------------------------------
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFindNeW(), okFind(), okFindSave()
*  AMDILK  060904  Merged with the Bug ID 58216
*  AMNILK  060920  MTPR904 Hink tasks Request ID: 45082. Changed methods authorizeSelectedRows(),authorizeAllRows & preDefine().
*		   Added new two methods authorizeByUserByDefault() and getDefaultUser().        
*  AMDILK  061101  MTPR904: Request id 45082.  Modified the authorizeAllRows(), authorizeByUserByDefault() to notify 
                   when there is no valid employee attched to the logged in user. Add a new field "Creation Date"
*  CHANLK  070516  Call 143346: Add duplicateRow(). 
*  CHANLK  070628  Call 144732: Change authorizeByUserByDefault() to get_name_for_user. 
*  BUNILK  070709  Bug ID 65937,Modified all search functions. 
*  CHANLK  070731  Merged bug 65937. 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderCoding1 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCoding1");

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

    private ASPBlock dummyblk;
    private ASPRowSet dummyset;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String strLableJ;
    private String wo_No;
    private String row_Nos;
    private String rowNo;
    private String separator_;
    private int getSelectedRows_;
    private String crePlanPath;
    private int sentrowNo;
    private String mform;
    private String mform1;
    private String backToForm;
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private String companyvar;
    private String connReconToPlan;
    private boolean sentNoEmployee;
    private boolean sentEmployee;
    private boolean recordCancel;
    private boolean enableF;
    private boolean backFlag;
    private String sent_wo_no;
    private String sent_work_ord_type;
    private String sent_catalog_no;
    private String sent_catalog_contract;
    private double sent_quantity;
    private double sent_cost;
    private double sent_qty_to_inv;
    private String sent_plan_lin_no;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPQuery q;
    private String sCatalogDesc;
    private ASPBuffer data;
    private String woNo;
    private boolean flag;
    private int selectedrows;
    private String strCompany;
    private ASPBuffer buffer;
    private ASPBuffer keys;
    private ASPBuffer r;
    private int noRows;
    private String nCusNo;
    private String nAgreeId;
    private ASPBuffer buf;
    private String strLableA;
    private String strLableB;
    private String strLableC;
    private String strLableD;
    private String strLableE;
    private String strLableF;
    private String strLableG;
    private String strLableH;
    private String strLableI;
    private String sWoNo; 
    private String sAgreeId;
    private String sCustomer;
    private String sRowNo;
    private int rowToEdit;
    private ASPBuffer row;
    private String calling_url;
    private String sigval;
    private String qrystr;
    private String queryString;
    private double sQty;
    private double bprice;
    private double sSalesPriceAmount; 
    private String returnForm; 
    private ASPBuffer temp;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle; 
    private boolean ctxIsAuthAllowed;
    private boolean ctxHasUnauthorized;
    private boolean actEna0;
    private boolean again;
    private String defaultUser;
    private String returnFlag;
    private boolean duplicateFlag;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderCoding1(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        strLableA = "";
        strLableB = "";
        strLableB = "";
        strLableC = "";
        strLableD = "";
        strLableE = ""; 
        strLableF = "";
        strLableG = "";
        strLableH = "";
        strLableI = "";

        wo_No = "";
        row_Nos = "";
        separator_ = "";
        getSelectedRows_ = 0;
        crePlanPath = "";
        sentrowNo =  1;
        mform = "";
        mform1 = "";
        backToForm = "";

	duplicateFlag = false;

        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        sWoNo = ctx.readValue("CTXWONO", sWoNo);
        sAgreeId       =       ctx.readValue(      "CTXAGREE",       "");
        sCustomer = ctx.readValue("CTXCUST", "");
        sRowNo = ctx.readValue("CTXROWNO", sRowNo);

        strLableA = ctx.readValue("LABLEA", strLableA );
        strLableB = ctx.readValue("LABLEB", strLableB );
        strLableC = ctx.readValue("LABLEC", strLableC );
        strLableD = ctx.readValue("LABLED", strLableD );
        strLableE = ctx.readValue("LABLEE", strLableE );
        strLableF = ctx.readValue("LABLEF", strLableF );
        strLableG = ctx.readValue("LABLEG", strLableG );
        strLableH = ctx.readValue("LABLEH", strLableH );
        strLableI = ctx.readValue("LABLEI", strLableI );
        strLableJ = ctx.readValue("LABLEJ", strLableJ );
        companyvar =  ctx.readValue("COMPANYVAR", ""); 

        crePlanPath = ctx.readValue("CREPLPOS", crePlanPath);
        connReconToPlan = ctx.readValue("CONNREFRMPLPOS", "flase");
        rowToEdit = ctx.readNumber("ROTEDTIPOS", 1);
        sentNoEmployee = ctx.readFlag("SENTNOEMPOS", false);
        sentEmployee = ctx.readFlag("SENTEMPPOS", false);
        sentrowNo = ctx.readNumber("SENTRWONPOS", sentrowNo);
        mform1 = ctx.readValue("MFORM1", mform1);
        mform = ctx.readValue("MFORM", mform);
        backToForm = ctx.readValue("BTFORM", backToForm);
        recordCancel = ctx.readFlag("RECCENRPOS", false);
        enableF = ctx.readFlag("ENABLEF", false);
        backFlag = ctx.readFlag("BACKFLAG", false);
        qrystr = ctx.readValue("QRYSTR");
        queryString = ctx.readValue("QUERYSTRING");
        ctxIsAuthAllowed = ctx.readFlag("CTXISAUTHALLOWED", false);
        ctxHasUnauthorized = ctx.readFlag("CTXALLAUTH", false);

        connReconToPlan = "false";

        sent_catalog_no = ctx.readValue("CTXCATNO", sent_catalog_no);
        sQty = ctx.readNumber("CTXSQTY", sQty);
        sent_qty_to_inv = ctx.readNumber("CTXSENTQTYINV", sent_qty_to_inv); 
        sent_plan_lin_no = ctx.readValue("CTXPLNINV", sent_plan_lin_no);
        sCatalogDesc = ctx.readValue("CTXCATDESC", sCatalogDesc);
        bprice =ctx.readNumber("CTXBPRICE", bprice);
        sSalesPriceAmount = ctx.readNumber("CTXSALPRIAMOUNT", sSalesPriceAmount); 
        actEna0 = ctx.readFlag("ACTENA0",false);
        again = ctx.readFlag("AGAIN",false);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("ROW_NO")))
        {
            sWoNo = mgr.readValue("WO_NO","");
            sRowNo = mgr.readValue("ROW_NO","");
            getcompany();

            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("AGREEMENT_ID1")))
        {
            sWoNo = mgr.readValue("WO_NO", "");
            sAgreeId = mgr.readValue("AGREEMENT_ID1", "");
            sCustomer = mgr.readValue("CUSTOMER_NO1", "");

            mform = mgr.readValue("FORM", "");
            ctx.setGlobal("MFORM", mform);
            getcompany();

            okFind();
            mgr.setPageExpiring();

        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FORM")))
        {
            queryString = mgr.getQueryString();

            sWoNo = mgr.readValue("WO_NO","");
            mform = mgr.readValue("FORM","");

            sCustomer = mgr.readValue("CUSTOMERNO","");
            sAgreeId  = mgr.readValue("AGREEMENT_ID","");

            ctx.setGlobal("MFORM",mform);
            getcompany();

            okFind();

            mgr.setPageExpiring();

            if (!mgr.isEmpty(mgr.getQueryStringValue("SINGLE_ROW_NO")))
            {
                if (headset.countRows() > 0)
                {
                    headset.goTo(toInt(mgr.getQueryStringValue("SINGLE_ROW_NO")));
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                }
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FORM1")))
        {
            sWoNo = mgr.readValue("WO_NO","");
            mform1 = mgr.readValue("FORM1","");
            ctx.setGlobal("MFORM1",mform1);
            getcompany();

            okFind();

            mgr.setPageExpiring();

            if (!mgr.isEmpty(mgr.getQueryStringValue("SINGLE_ROW_NO")))
            {
                if (headset.countRows() > 0)
                {
                    headset.goTo(toInt(mgr.getQueryStringValue("SINGLE_ROW_NO")));
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                }
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("REFRESH")))
        {
            sWoNo = mgr.readValue("WO_NO","");
            backFlag = true;
            getcompany();

            okFind();

            mgr.setPageExpiring();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            sWoNo = mgr.readValue("WO_NO","");
            getcompany();

            okFind();

            mgr.setPageExpiring();
        }
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCREVAL_WO")))
        {
            sent_wo_no = mgr.readValue("NEWCREVAL_WO", "");
            backToForm = mgr.readValue("NEWCREVAL_BKFRMNAME", "");
            sentEmployee = false;
            sentNoEmployee = false;

            if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCREVAL_WORKORDTYPE")))
            {
                sent_work_ord_type = mgr.readValue("NEWCREVAL_WORKORDTYPE", "");
                sent_catalog_no = mgr.readValue("NEWCREVAL_CATNO", "");
                sent_catalog_contract = mgr.readValue("NEWCREVAL_CATCONT", "");
                sent_quantity = mgr.readNumberValue("NEWCREVAL_QTY");
                sent_cost = mgr.readNumberValue("NEWCREVAL_COSTAMT");
                if (isNaN(sent_quantity))
                    sent_quantity = 0;

                if (isNaN(sent_cost))
                    sent_cost = 0;

                sent_qty_to_inv = mgr.readNumberValue("NEWCREVAL_QTYTOINVOI");
                if (isNaN(sent_qty_to_inv))
                    sent_qty_to_inv = 0;

                sent_plan_lin_no = mgr.readValue("NEWCREVAL_PLANLINNO","");
                sentEmployee = true; 
                recordCancel = false;   

                cmd = trans.addCustomFunction("CL1", "Work_Order_Cost_Type_Api.Get_Client_Value(1)", "CLIENTVAL1");
                cmd = trans.addCustomFunction("CL2", "Work_Order_Cost_Type_Api.Get_Client_Value(0)", "CLIENTVAL2");

                trans = mgr.perform(trans);

                String strMeterial = trans.getValue("CL1/DATA/CLIENTVAL1");
                String strPersonal = trans.getValue("CL2/DATA/CLIENTVAL2");

                trans.clear();

                if (sent_work_ord_type.equals(strMeterial))
                {
                    mgr.showAlert(mgr.translate("REGMATCOST: Registration of Material costs is not allowed on this tab use Material tab."));
                    sent_work_ord_type = "";
                }

                if (sent_work_ord_type.equals(strPersonal))
                {
                    mgr.showAlert(mgr.translate("REGPERCOST: Registration of Personnel costs is not allowed on this tab. Use Time Report tab"));
                    sent_work_ord_type = "";
                }
            }

            if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCANCELVAL_WO")))
            {
                recordCancel = true;
                sentEmployee = true;
                sentNoEmployee = false;
            }

            sWoNo = sent_wo_no;  
            okFindNeW();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCONNVAL_WO")))
        {
            sent_wo_no = mgr.readValue("NEWCONNVAL_WO", "");
            sentEmployee = false;
            sentNoEmployee = false;
            backToForm = mgr.readValue("NEWCREVAL_BKFRMNAME", "");

            if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCONNVAL_CATNO")))
            {
                sent_catalog_no = mgr.readValue("NEWCONNVAL_CATNO", "");
                sent_catalog_contract = mgr.readValue("NEWCONNVAL_CATCONT", "");
                sent_quantity = mgr.readNumberValue("NEWCONNVAL_QTY");

                if (isNaN(sent_quantity))
                    sent_quantity = 0;

                sent_qty_to_inv = mgr.readNumberValue("NEWCONNVAL_QTYTOINVOI");

                if (isNaN(sent_qty_to_inv))
                    sent_qty_to_inv = 0;

                sent_plan_lin_no = mgr.readValue("NEWCONNVAL_PLANLINNO", "");
                sentrowNo = toInt(mgr.readNumberValue("NEWCONNVAL_ROWNO"));

                if (isNaN(sentrowNo))
                    sentrowNo = 0;

                sentNoEmployee = true;   
                recordCancel = false; 
                connReconToPlan = "false";  
            }

            if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCANCELVAL_WO")))
            {
                recordCancel = true;

                sentEmployee = false;
                sentNoEmployee = true;
                connReconToPlan = "false";
                sentrowNo = toInt(mgr.readNumberValue("NEWCONNVAL_ROWNO"));

                if (isNaN(sentrowNo))
                    sentrowNo = 0;
            }

            sWoNo = sent_wo_no;           
            okFindNeW();      
        }

        checkObjAvaileble();
        adjust();

        ctx.writeValue("CTXWONO", sWoNo);
        ctx.writeValue("CTXAGREE", sAgreeId);
        ctx.writeValue("CTXCUST", sCustomer);
        ctx.writeValue("CTXROWNO", sRowNo);

        ctx.writeValue("LABLEA", strLableA );
        ctx.writeValue("LABLEB", strLableB );
        ctx.writeValue("LABLEC", strLableC );
        ctx.writeValue("LABLED", strLableD );
        ctx.writeValue("LABLEE", strLableE );
        ctx.writeValue("LABLEF", strLableF );
        ctx.writeValue("LABLEG", strLableG );
        ctx.writeValue("LABLEH", strLableH );
        ctx.writeValue("LABLEI", strLableI );
        ctx.writeValue("LABLEJ", strLableJ );
        ctx.writeValue("COMPANYVAR", companyvar);

        ctx.writeValue("CREPLPOS", crePlanPath);
        ctx.writeValue("CONNREFRMPLPOS", connReconToPlan);
        ctx.writeNumber("ROTEDTIPOS", rowToEdit);
        ctx.writeFlag("SENTNOEMPOS", sentNoEmployee);
        ctx.writeFlag("SENTEMPPOS", sentEmployee);
        ctx.writeNumber("SENTRWONPOS", sentrowNo);
        ctx.writeFlag("RECCENRPOS", recordCancel);
        ctx.writeValue("MFORM1", mform1);
        ctx.writeValue("BTFORM", backToForm);
        ctx.writeFlag("ENABLEF", enableF);
        ctx.writeFlag("BACKFLAG", backFlag);
        ctx.writeValue("MFORM", mform);
        ctx.writeValue("QRYSTR", qrystr);
        ctx.writeValue("QUERYSTRING", queryString);
        ctx.writeValue("CTXCATNO", sent_catalog_no);
        ctx.writeNumber("CTXSQTY", sQty); 
        ctx.writeNumber("CTXSENTQTYINV", sent_qty_to_inv);
        ctx.writeValue("CTXPLNINV", sent_plan_lin_no);
        ctx.writeValue("CTXCATDESC", sCatalogDesc);
        ctx.writeNumber("CTXBPRICE", bprice);
        ctx.writeNumber("CTXSALPRIAMOUNT", sSalesPriceAmount);    
        ctx.writeFlag("CTXISAUTHALLOWED", ctxIsAuthAllowed);
        ctx.writeFlag("CTXALLAUTH", ctxHasUnauthorized);
        ctx.writeFlag("ACTENA0",actEna0);
        ctx.writeFlag("AGAIN",again);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean checksec( String method,int ref,boolean[]isSecure) 
    {
        String[] splitted;
        ASPManager mgr = getASPManager();

        isSecure[ref] = false ; 
        splitted = split(method,".");

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer ();
        secBuff.addSecurityQuery(splitted[0], splitted[1]);

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
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        boolean isSecure[] = new boolean[6] ;

        String val = mgr.readValue("VALIDATE");
        String txt = "";

        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP", "Site_API.Get_Company", "COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.validate(trans);

            String company = trans.getValue("COMP/DATA/COMPANY");

            txt =  (mgr.isEmpty(company) ? "" :company) + "^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("CATALOG_NO".equals(val))
        {
            if (checksec("Sales_Part_API.Get_Catalog_Desc", 1, isSecure))
            {
                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","LINE_DESCRIPTION");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("CATALOG_NO");
            }

            cmd = trans.addCustomFunction("CLIENT3", "Work_Order_Cost_Type_Api.Get_Client_Value(3)", "CLIENTVAL3");
            cmd = trans.addCustomFunction("CLIENT4", "Work_Order_Cost_Type_Api.Get_Client_Value(4)", "CLIENTVAL4");
            cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");
            cmd = trans.addCustomFunction("CUSTNO","ACTIVE_WORK_ORDER_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("WO_NO");
            cmd = trans.addCustomFunction("AGREE","ACTIVE_WORK_ORDER_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("WO_NO");

            trans = mgr.validate(trans);

            String strCatalogDeac = "";
            String strAgreement = "0";
            double nSalesPriceAmount = 0;

            if (isSecure[1])
                strCatalogDeac= trans.getValue("CATDESC/DATA/LINE_DESCRIPTION");
            else
                strCatalogDeac= "";

            String strClient3 = trans.getValue("CLIENT3/DATA/CLIENTVAL3");
            String strClient4 = trans.getValue("CLIENT4/DATA/CLIENTVAL4");
            String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE");
            String custNo= trans.getValue("CUSTNO/DATA/CUSTOMER_NO");
            String areeId= trans.getValue("AGREE/DATA/AGREEMENT_ID");

            double nAmount = mgr.readNumberValue("AMOUNT");
            if (isNaN(nAmount))
                nAmount = 0;

            double list_price = 0;
            double n_discount = 0;
            double nBuyQtyDue = 0;
            String price_list_no = "";

            String strWorkOrderCost = mgr.readValue("WORK_ORDER_COST_TYPE", "");

            if (!(strClient4.equals(strWorkOrderCost)))
            {
                if (strClient3.equals(strWorkOrderCost))
                {
                    double nQunatity = mgr.readNumberValue("QTY");
                    if (isNaN(nQunatity))
                        nQunatity = 0;

                    if (nQunatity != 0)
                    {
                        trans.clear();
                        if (checksec("Sales_Part_API.Get_Cost", 1, isSecure))
                        {
                            cmd = trans.addCustomFunction("COSTING", "Sales_Part_API.Get_Cost", "AMOUNT_VAR");
                            cmd.addParameter("CONTRACT");
                            cmd.addParameter("CATALOG_NO");

                            trans = mgr.validate(trans);
                            double nCost = trans.getNumberValue("COSTING/DATA/AMOUNT_VAR");
                            if (isNaN(nCost))
                                nCost = 0;

                            nAmount = nCost * nQunatity;
                        }
                        else
                            nAmount = 0;
                    }
                }

                trans.clear();    

                String s_cust ;
                String s_agree ;     

                String alt_cust = mgr.readValue("ALTERNATIVE_CUSTOMER", "");

                if (!mgr.isEmpty(alt_cust) || !"null".equals(alt_cust))
                {
                    s_cust = mgr.readValue("ALTERNATIVE_CUSTOMER", ""); 
                    s_agree = "";
                }
                else
                {
                    s_cust = custNo;
                    s_agree = areeId;
                }

                nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue = 0;

                if (nBuyQtyDue == 0)
                    nBuyQtyDue=1;

                cmd = trans.addCustomFunction("CURRCODE", "Customer_Agreement_API.Get_Currency_Code", "CURRENCEY_CODE");
                cmd.addParameter("AGREEMENT_ID", s_agree);

                cmd = trans.addCustomFunction("CURRCODECUST", "Cust_Ord_Customer_API.Get_Currency_Code", "CURRENCEY_CODE");
                cmd.addParameter("CUSTOMER_NO", s_cust);

                trans = mgr.validate(trans);

                String currCode = trans.getValue("CURRCODE/DATA/CURRENCEY_CODE");
                if (mgr.isEmpty(currCode))
                    currCode = trans.getValue("CURRCODECUST/DATA/CURRENCEY_CODE");

                trans.clear();

                cmd = trans.addCustomFunction("GETPRICELISTFUNC", "Customer_Order_Pricing_Api.Get_Valid_Price_List", "PRICE_LIST_NO");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO", s_cust);
                cmd.addParameter("CURRENCEY_CODE", currCode);


                cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
                cmd.addParameter("CONTRACT");

                cmd = trans.addCustomFunction("CURRCODE","Company_Finance_API.Get_Currency_Code","CURRENCEY_CODE");
                cmd.addReference("COMPANY", "GETCOMP/DATA");

                trans = mgr.validate(trans);

                price_list_no = trans.getBuffer("GETPRICELISTFUNC/DATA").getFieldValue("PRICE_LIST_NO");
                String curCode =  trans.getValue("CURRCODE/DATA/CURRENCEY_CODE");

                trans.clear();
                if ( mgr.isEmpty(s_cust) )
                {
                    cmd = trans.addCustomCommand("PRICEINFO","CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info");
                    cmd.addParameter("SALE_PRICE");
                    cmd.addParameter("BASE_PRICE");
                    cmd.addParameter("CURRENCY_RATE");
                    cmd.addParameter("DISCOUNT");
                    cmd.addParameter("PRICE_SOURCE");
                    cmd.addParameter("PRICE_SOURCE_ID");
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("CUSTOMER_NO",s_cust);
                    cmd.addParameter("CURRENCEY_CODE",curCode);
                    cmd.addParameter("AGREEMENT_ID",s_agree);
                    cmd.addParameter("CATALOG_NO"); 
                    cmd.addParameter("QTY_TO_INVOICE",mgr.formatNumber("QTY_TO_INVOICE",nBuyQtyDue));               
                    cmd.addParameter("PRICE_LIST_NO",price_list_no);
                    cmd.addParameter("SYS_DATE",sysDate);

                }
                else
                {

                    cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_PRICE");
                    cmd.addParameter("SALE_PRICE");
                    cmd.addParameter("DISCOUNT");
                    cmd.addParameter("CURRENCY_RATE");
                    cmd.addParameter("PRICE_SOURCE");
                    cmd.addParameter("PRICE_SOURCE_ID");
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("CATALOG_NO");
                    cmd.addParameter("CUSTOMER_NO", s_cust);
                    cmd.addParameter("AGREEMENT_ID", s_agree);
                    cmd.addParameter("PRICE_LIST_NO", price_list_no);
                    cmd.addParameter("QTY_TO_INVOICE", mgr.formatNumber("QTY_TO_INVOICE",nBuyQtyDue));
                    cmd.addParameter("WO_NO");
                    cmd.addParameter("SYS_DATE",sysDate);
                }

                trans = mgr.validate(trans);

                list_price = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE");
                if (isNaN(list_price))
                    list_price = 0;

                n_discount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
                if (isNaN(n_discount))
                    n_discount = 0;


                if ( nBuyQtyDue != 0 )
                {
                    nSalesPriceAmount = (list_price - ( n_discount / 100 * list_price ))* nBuyQtyDue; 
                }
            }
            else
            {
                nAmount = 0;
                list_price = 0;
            }

            String strAmount = mgr.formatNumber("AMOUNT",nAmount);
            String strListPrice = mgr.formatNumber("LIST_PRICE",list_price);
            String strSalespriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
            String strDiscount = mgr.formatNumber("DISCOUNT",n_discount);
            String strBuyQtyDue = mgr.formatNumber("QTY_TO_INVOICE",nBuyQtyDue);

            txt =  (mgr.isEmpty(strCatalogDeac) ? "" :strCatalogDeac)  + "^" +  (mgr.isEmpty(strAmount ) ? "" :strAmount) + "^" +  (mgr.isEmpty(strListPrice) ? "" :strListPrice) + "^"+ (mgr.isEmpty(strSalespriceAmt) ? "" : (strSalespriceAmt))+ "^"+ (mgr.isEmpty(strDiscount) ? "" :strDiscount) + "^"+ (mgr.isEmpty(price_list_no) ? "" :price_list_no) + "^" + (mgr.isEmpty(strBuyQtyDue) ? "" :strBuyQtyDue) + "^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            String s_cust = "";
            String s_agree = "";

            double list_price = 0;
            double n_discount = 0;
            String col_price_list_no = "";
            String strAgreement = "0";
            double nSalesPriceAmount = 0;

            String alt_cust = mgr.readValue("ALTERNATIVE_CUSTOMER","");

            if (!mgr.isEmpty(alt_cust) || !"null".equals(alt_cust))
            {
                s_cust = mgr.readValue("ALTERNATIVE_CUSTOMER", ""); 
                s_agree = "";
            }
            else
            {
                s_cust = mgr.readValue("CUSTOMER_NO","");
                s_agree = mgr.readValue("AGREEMENT_ID","");;
            }

            double nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nBuyQtyDue))
                nBuyQtyDue = 0;

            if (nBuyQtyDue == 0)
                nBuyQtyDue=1;

            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("CURRCODE","Company_Finance_API.Get_Currency_Code","CURRENCEY_CODE");
            cmd.addReference("COMPANY", "GETCOMP/DATA");
            cmd = trans.addCustomFunction("CUSTNO","ACTIVE_WORK_ORDER_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("AGREE","ACTIVE_WORK_ORDER_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("WO_NO");

            cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");
            trans = mgr.validate(trans);

            String curCode =  trans.getValue("CURRCODE/DATA/CURRENCEY_CODE");
            String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE");
            String custNo= trans.getValue("CUSTNO/DATA/CUSTOMER_NO");
            String areeId= trans.getValue("AGREE/DATA/AGREEMENT_ID");
            trans.clear();

            if ( mgr.isEmpty(s_cust) )
            {
                cmd = trans.addCustomCommand("PRICEINFO","CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("PRICE_SOURCE");
                cmd.addParameter("PRICE_SOURCE_ID");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("CUSTOMER_NO",custNo);
                cmd.addParameter("CURRENCEY_CODE",curCode);
                cmd.addParameter("AGREEMENT_ID",areeId);
                cmd.addParameter("CATALOG_NO"); 
                cmd.addParameter("QTY_TO_INVOICE",mgr.formatNumber("QTY_TO_INVOICE",nBuyQtyDue));               
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("SYS_DATE",sysDate);

            }
            else
            {

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("PRICE_SOURCE");
                cmd.addParameter("PRICE_SOURCE_ID");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO",custNo);
                cmd.addParameter("AGREEMENT_ID",areeId);
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY_TO_INVOICE", mgr.formatNumber("QTY_TO_INVOICE",nBuyQtyDue));
                cmd.addParameter("WO_NO");
                cmd.addParameter("SYS_DATE",sysDate);
            }

            trans = mgr.validate(trans);

            list_price = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE");
            if (isNaN(list_price))
                list_price = 0;

            n_discount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(n_discount))
                n_discount = 0;


            if ( nBuyQtyDue != 0 )
            {
                nSalesPriceAmount = (list_price - ( n_discount / 100 * list_price ))* nBuyQtyDue;
            }

            strAgreement = "1" ;

            String strListPrice = mgr.formatNumber("LIST_PRICE",list_price);
            String strSalespriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
            String strDiscount = mgr.formatNumber("DISCOUNT",n_discount);

            txt =  (mgr.isEmpty(strListPrice) ? "" :strListPrice) + "^" + (mgr.isEmpty(strDiscount) ? "" :strDiscount )+ "^"+ (mgr.isEmpty(strSalespriceAmt) ? "" :strSalespriceAmt )+ "^"+ (mgr.isEmpty(col_price_list_no) ? "" :col_price_list_no )+ "^" ; 

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("QTY_TO_INVOICE".equals(val))
        {
            double qty_inv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qty_inv))
                qty_inv = 0;

            double list_price = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(list_price))
                list_price = 0;

            double n_discount = mgr.readNumberValue("DISCOUNT");
            if ( isNaN(n_discount) )
                n_discount = 0;

            double sale_price_amt = (list_price - ( n_discount / 100 * list_price ))* qty_inv;

            String strSalesPriceAmt =  mgr.formatNumber("SALESPRICEAMOUNT",sale_price_amt);

            txt = (mgr.isEmpty(strSalesPriceAmt) ? "" :strSalesPriceAmt) + "^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("LIST_PRICE".equals(val))
        {
            double qty_inv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qty_inv))
                qty_inv = 0;

            double list_price = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(list_price))
                list_price = 0;

            double n_discount = mgr.readNumberValue("DISCOUNT");
            if ( isNaN(n_discount) )
                n_discount = 0;

            double sale_price_amt = (list_price - ( n_discount / 100 * list_price ))* qty_inv;
            String strSalesPriceAmt =  mgr.formatNumber("SALESPRICEAMOUNT",sale_price_amt);

            txt = (mgr.isEmpty(strSalesPriceAmt) ? "" :strSalesPriceAmt) + "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }

        if ( "DISCOUNT".equals(val) )
        {
            double qty_inv = mgr.readNumberValue("QTY_TO_INVOICE");
            if ( isNaN(qty_inv) )
                qty_inv = 0;

            double list_price = mgr.readNumberValue("LIST_PRICE");
            if ( isNaN(list_price) )
                list_price = 0;

            double n_discount = mgr.readNumberValue("DISCOUNT");
            if ( isNaN(n_discount) )
                n_discount = 0;

            double sale_price_amt = (list_price - ( n_discount / 100 * list_price ))* qty_inv;

            String strSalesPriceAmt =  mgr.formatNumber("SALESPRICEAMOUNT",sale_price_amt);

            txt = (mgr.isEmpty(strSalesPriceAmt) ? "" :strSalesPriceAmt) + "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("SIGNATURE".equals(val))
        {
            cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("SIGNATURE");

            trans = mgr.validate(trans);

            String sign_id = trans.getValue("SIGNID/DATA/SIGNATURE_ID");

            txt = (mgr.isEmpty(sign_id) ? "" :sign_id) + "^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("QTY".equals(val))
        {
            double nQunatity = mgr.readNumberValue("QTY");
            if ( isNaN(nQunatity) )
                nQunatity = 0;
            double nAmount = 0; 
            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","LINE_DESCRIPTION");

            trans.clear();  

            cmd = trans.addCustomFunction("CLIENT3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3"); 

            trans = mgr.validate(trans);
            String strClient3= trans.getValue("CLIENT3/DATA/CLIENTVAL3");

            String strWorkOrderCost= mgr.readValue("WORK_ORDER_COST_TYPE","");

            if (strClient3.equals(strWorkOrderCost))
            {

                if (nQunatity != 0)
                {
                    trans.clear();
                    if (checksec("Sales_Part_API.Get_Cost",1,isSecure))
                    {

                        cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT_VAR");
                        cmd.addParameter("CONTRACT");
                        cmd.addParameter("CATALOG_NO");

                        trans = mgr.validate(trans);
                        double nCost = trans.getNumberValue("COSTING/DATA/AMOUNT_VAR");
                        if (isNaN(nCost))
                            nCost = 0;

                        nAmount = nCost * nQunatity;
                    }
                    else
                        nAmount = 0;
                }
            }
            else
            {
                nAmount = 0;
            }

            String strAmount = mgr.formatNumber("AMOUNT",nAmount);
            txt = (mgr.isEmpty(strAmount) ? "" :strAmount) + "^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("WORK_ORDER_COST_TYPE".equals(val))
        {

            double colAmount = 0;

            cmd = trans.addCustomFunction("PREPOSTID","ACTIVE_WORK_ORDER_API.Get_Pre_Accounting_Id","PRE_POSTING_ID");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomCommand("CODEPARTS","Maintenance_Pre_Posting_API.Get_Pre_Posting_Code_Parts");
            cmd.addParameter("COST_CENTER");
            cmd.addParameter("CODE_C");
            cmd.addParameter("CODE_D");
            cmd.addParameter("CODE_E");
            cmd.addParameter("CODE_F");
            cmd.addParameter("CODE_G");
            cmd.addParameter("CODE_H");
            cmd.addParameter("CODE_I");
            cmd.addParameter("CODE_J");
            cmd.addParameter("ACTIVITY_SEQ");
            cmd.addReference("PRE_POSTING_ID", "PREPOSTID/DATA");

            cmd = trans.addCustomFunction("CL4", "Work_Order_Cost_Type_Api.Get_Client_Value(4)", "CLIENTVAL4");
            cmd = trans.addCustomFunction("CL1", "Work_Order_Cost_Type_Api.Get_Client_Value(1)", "CLIENTVAL1");
            cmd = trans.addCustomFunction("CL2", "Work_Order_Cost_Type_Api.Get_Client_Value(0)", "CLIENTVAL2");

            trans = mgr.validate(trans);

            String sCostCent = trans.getValue("CODEPARTS/DATA/COST_CENTER");
            String sCodeC = trans.getValue("CODEPARTS/DATA/CODE_C");
            String sCodeD = trans.getValue("CODEPARTS/DATA/CODE_D");
            String sCodeE = trans.getValue("CODEPARTS/DATA/CODE_E");
            String sCodeF = trans.getValue("CODEPARTS/DATA/CODE_F");
            String sCodeG = trans.getValue("CODEPARTS/DATA/CODE_G");
            String sCodeH = trans.getValue("CODEPARTS/DATA/CODE_H");
            String sCodeI = trans.getValue("CODEPARTS/DATA/CODE_I");
            String sActSeq = trans.getValue("CODEPARTS/DATA/ACTIVITY_SEQ");

            String strFixedPrice = trans.getValue("CL4/DATA/CLIENTVAL4");
            String colCostType = mgr.readValue("WORK_ORDER_COST_TYPE","");
            String strMeterial = trans.getValue("CL1/DATA/CLIENTVAL1");
            String strPersonal = trans.getValue("CL2/DATA/CLIENTVAL2");

            if (colCostType.equals(strFixedPrice))
                colAmount = 0;

            if (colCostType.equals(strMeterial))
            {
                txt = "No_Data_Found" + mgr.translate("REGMATCOST: Registration of Material costs is not allowed on this tab use Material tab.");
                mgr.responseWrite(txt);
                mgr.endResponse();
            }

            if (colCostType.equals(strPersonal))
            {
                txt = "No_Data_Found" + mgr.translate("REGPERCOST: Registration of Personnel costs is not allowed on this tab. Use Time Report tab");
                mgr.responseWrite(txt);
                mgr.endResponse();
            }

            String strColAmount = mgr.formatNumber("AMOUNT",colAmount);

            txt = (mgr.isEmpty(sCostCent) ? "" : (sCostCent))+ "^"+(mgr.isEmpty(sCodeC) ? "" : (sCodeC))+ "^"+
                  (mgr.isEmpty(sCodeD) ? "" : (sCodeD))+ "^"+(mgr.isEmpty(sCodeE) ? "" : (sCodeE))+ "^"+
                  (mgr.isEmpty(sCodeF) ? "" : (sCodeF))+ "^"+(mgr.isEmpty(sCodeG) ? "" : (sCodeG))+ "^"+
                  (mgr.isEmpty(sCodeH) ? "" : (sCodeH))+ "^"+(mgr.isEmpty(sCodeI) ? "" : (sCodeI))+ "^"+
                  (mgr.isEmpty(sActSeq) ? "" : (sActSeq))+"^"+(mgr.isEmpty(strColAmount) ? "" : (strColAmount))+"^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        // Validate Plan line no
        else if ("PLAN_LINE_NO".equals(val))
        {
            cmd = trans.addCustomFunction("PARA20", "Work_Order_Role_API.Get_Plan_Row_No", "CRAFT_ROW" );
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");

            cmd = trans.addCustomFunction("PARA21", "Work_Order_Role_API.Get_Plan_Ref_Number", "CRAFT_REFERENCE_NUMBER");
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");

            trans = mgr.validate(trans);

            String strPostCraftRow   = trans.getValue("PARA20/DATA/CRAFT_ROW");
            String strPostCraftRefNo = trans.getValue("PARA21/DATA/CRAFT_REFERENCE_NUMBER");

            txt = (mgr.isEmpty(strPostCraftRow)? "" : strPostCraftRow) +"^"+ (mgr.isEmpty(strPostCraftRefNo)? "" : strPostCraftRefNo) +"^";

            mgr.responseWrite(txt);
            mgr.endResponse();

        }
        //
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

    public void okFindNeW()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("GETAUTHALLOWED","Work_Order_Coding_API.Is_Authorisation_Allowed","AUTH_ALLOWED");
        cmd.addParameter("WO_NO", sent_wo_no);

        cmd = trans.addCustomFunction("GETANYUNAUTH","Work_Order_Coding_API.Check_Any_Unauth_Rows","HAS_UNAUTHORIZED");
        cmd.addParameter("WO_NO", sent_wo_no);

        trans = mgr.perform(trans); 

        ctxIsAuthAllowed = ("TRUE".equals(trans.getValue("GETAUTHALLOWED/DATA/AUTH_ALLOWED")));
        ctxHasUnauthorized = ("TRUE".equals(trans.getValue("GETANYUNAUTH/DATA/HAS_UNAUTHORIZED")));

        trans.clear();

        if (sentEmployee)
        {
            q = trans.addEmptyQuery(headblk);
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
            q.addWhereCondition("WO_NO= ?");
	    q.addParameter("WO_NO", sent_wo_no);
            q.setOrderByClause("WO_NO");
            q.includeMeta("ALL"); 

            mgr.querySubmit(trans,headblk);

            trans.clear();

            headlay.setLayoutMode(headlay.NEW_LAYOUT);
            trans.clear();
            newRow();

            if (!recordCancel)
            {
                trans.clear();

                cmd = trans.addCustomFunction("CONTDEF","User_Default_API.Get_Contract","CONTRACT");

                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("CONTRACT",sent_catalog_contract);
                cmd.addParameter("CATALOG_NO",sent_catalog_no); 

                //bug id 43810,start
                cmd = trans.addCustomFunction("LINDESC","Work_Order_Planning_API.Get_Line_Desc","TEMPLINEDESC");
                cmd.addParameter("WO_NO",sWoNo);
                cmd.addParameter("PLAN_LINE_NO",sent_plan_lin_no); 

                cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT_VAR");
                cmd.addParameter("CONTRACT",sent_catalog_contract);
                cmd.addParameter("CATALOG_NO",sent_catalog_no);  

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE", "");
                cmd.addParameter("SALE_PRICE", "");
                cmd.addParameter("DISCOUNT", "");
                cmd.addParameter("CURRENCY_RATE", "");
                cmd.addParameter("CONTRACT", sent_catalog_contract);
                cmd.addParameter("CATALOG_NO", sent_catalog_no);
                cmd.addParameter("CUSTOMER_NO", "");
                cmd.addParameter("AGREEMENT_ID", "");
                cmd.addParameter("PRICE_LIST_NO", "");
                cmd.addReference ("AMOUNT_VAR", "DATA/COSTING"); 

                trans = mgr.perform(trans);
                //bug id 43810,start             
                sCatalogDesc = trans.getValue("LINDESC/DATA/TEMPLINEDESC");
                if (mgr.isEmpty(sCatalogDesc))

                    sCatalogDesc = trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");

                if (sent_quantity == 0)
                {
                    sQty = trans.getNumberValue("COSTING/DATA/AMOUNT_VAR"); 
                    if (isNaN(sQty))
                        sQty = 0;
                }
                else
                    sQty = sent_quantity;

                bprice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 

                if (isNaN(bprice))
                    bprice = 0;

                sSalesPriceAmount = sent_qty_to_inv * bprice; 

                trans.clear();       

                ASPBuffer row = headset.getRow();
                row.setValue("WO_NO",sent_wo_no);
                row.setValue("WORK_ORDER_COST_TYPE",sent_work_ord_type);
                row.setValue("CATALOG_NO",sent_catalog_no);
                row.setNumberValue("QTY",sQty);
                row.setNumberValue("AMOUNT",sent_cost*sQty);
                row.setNumberValue("QTY_TO_INVOICE",sent_qty_to_inv);
                row.setValue("PLAN_LINE_NO",sent_plan_lin_no);
                row.setValue("LINE_DESCRIPTION",sCatalogDesc);  
                row.setNumberValue("LIST_PRICE",bprice); 
                row.setNumberValue("SALESPRICEAMOUNT",sSalesPriceAmount); 
                headset.setRow(row);
            }
        }
        else if (sentNoEmployee)
        {
            q = trans.addEmptyQuery(headblk);
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
            q.addWhereCondition("WO_NO= ?");
	    q.addParameter("WO_NO", sent_wo_no);
            q.setOrderByClause("WO_NO");
            q.includeMeta("ALL"); 

            mgr.querySubmit(trans,headblk);

            trans.clear();  

            headset.goTo(sentrowNo);
            headlay.setLayoutMode(headlay.EDIT_LAYOUT);

            if (!recordCancel)
            {
                trans.clear();

                cmd = trans.addCustomFunction("CONTDEF","User_Default_API.Get_Contract","CONTRACT");

                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("CONTRACT",sent_catalog_contract);
                cmd.addParameter("CATALOG_NO",sent_catalog_no); 

                //bug id 43810,start
                cmd = trans.addCustomFunction("LINDESC","Work_Order_Planning_API.Get_Line_Desc","TEMPLINEDESC");
                cmd.addParameter("WO_NO",sWoNo);
                cmd.addParameter("PLAN_LINE_NO",sent_plan_lin_no); 

                cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT_VAR");
                cmd.addParameter("CONTRACT",sent_catalog_contract);
                cmd.addParameter("CATALOG_NO",sent_catalog_no);  

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("CONTRACT", sent_catalog_contract);
                cmd.addParameter("CATALOG_NO", sent_catalog_no);
                cmd.addParameter("CUSTOMER_NO", "");
                cmd.addParameter("AGREEMENT_ID", "");
                cmd.addParameter("PRICE_LIST_NO", "");
                cmd.addReference ("AMOUNT_VAR","DATA/COSTING");   

                trans = mgr.perform(trans);

                //bug id 43810,start             
                sCatalogDesc = trans.getValue("LINDESC/DATA/TEMPLINEDESC");
                if (mgr.isEmpty(sCatalogDesc))

                    sCatalogDesc = trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");

                if (sent_quantity == 0)
                {
                    sQty = trans.getNumberValue("COSTING/DATA/AMOUNT_VAR"); 
                    if (isNaN(sQty))
                        sQty = 0;
                }
                else
                    sQty = headset.getRow().getNumberValue("QTY");

                bprice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                trans.clear();

                cmd = trans.addCustomCommand("QYTINVVALS","Work_Order_Coding_API.Calc_Qty_To_Invoice");
                cmd.addParameter("QTY_TO_INVOICE", String.valueOf(sent_qty_to_inv));
                cmd.addParameter("ROW_NO", headset.getValue("ROW_NO"));
                cmd.addParameter("ROW_NO", headset.getValue("ROW_NO"));
                cmd.addParameter("PLAN_LINE_NO", sent_plan_lin_no);
                cmd.addParameter("PLAN_LINE_NO", headset.getValue("PLAN_LINE_NO"));
                cmd.addParameter("WORK_ORDER_COST_TYPE", headset.getValue("WORK_ORDER_COST_TYPE"));
                cmd.addParameter("WO_NO", sent_wo_no);

                trans = mgr.perform(trans);

                sent_qty_to_inv = trans.getNumberValue("QYTINVVALS/DATA/QTY_TO_INVOICE");

                if (isNaN(bprice))
                    bprice = 0;

                if (isNaN(sent_qty_to_inv) || (sent_qty_to_inv==0))
                {
                    sent_qty_to_inv = sQty;
                }

                sSalesPriceAmount = sent_qty_to_inv * bprice;

                trans.clear();   

                ASPBuffer row = headset.getRow();
                row.setValue("CATALOG_NO",sent_catalog_no);
                row.setNumberValue("QTY",sQty); 
                row.setNumberValue("QTY_TO_INVOICE",sent_qty_to_inv);
                row.setValue("PLAN_LINE_NO",sent_plan_lin_no);
                row.setValue("LINE_DESCRIPTION",sCatalogDesc);  
                row.setNumberValue("LIST_PRICE",bprice); 
                row.setNumberValue("SALESPRICEAMOUNT",sSalesPriceAmount);
                headset.setRow(row);
            }
        }
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("GETAUTHALLOWED","Work_Order_Coding_API.Is_Authorisation_Allowed","AUTH_ALLOWED");
        cmd.addParameter("WO_NO", sWoNo);

        cmd = trans.addCustomFunction("GETANYUNAUTH","Work_Order_Coding_API.Check_Any_Unauth_Rows","HAS_UNAUTHORIZED");
        cmd.addParameter("WO_NO", sWoNo);

        trans = mgr.perform(trans); 

        ctxIsAuthAllowed = ("TRUE".equals(trans.getValue("GETAUTHALLOWED/DATA/AUTH_ALLOWED")));
        ctxHasUnauthorized = ("TRUE".equals(trans.getValue("GETANYUNAUTH/DATA/HAS_UNAUTHORIZED")));

        trans.clear();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

        if (!mgr.isEmpty(sWoNo))
	{
	    q.addWhereCondition("WO_NO = ?");
	    q.addParameter("WO_NO", sWoNo);
	}

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERCODING1NODATA: No data found."));
            headset.clear();
        }
        if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

        setSalesPartDesc(); 
        connReconToPlan = "false"; 
    }

    public void okFindSave()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("GETAUTHALLOWED","Work_Order_Coding_API.Is_Authorisation_Allowed","AUTH_ALLOWED");
        cmd.addParameter("WO_NO", woNo);

        cmd = trans.addCustomFunction("GETANYUNAUTH","Work_Order_Coding_API.Check_Any_Unauth_Rows","HAS_UNAUTHORIZED");
        cmd.addParameter("WO_NO", woNo);

        trans = mgr.perform(trans); 

        ctxIsAuthAllowed = ("TRUE".equals(trans.getValue("GETAUTHALLOWED/DATA/AUTH_ALLOWED")));
        ctxHasUnauthorized = ("TRUE".equals(trans.getValue("GETANYUNAUTH/DATA/HAS_UNAUTHORIZED")));

        trans.clear();

        q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO", woNo);
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        connReconToPlan = "false";
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");

        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void newRow()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("SPC21","Work_Order_Cost_Type_Api.Get_Client_Value(2)","CLIENTVAL3");
        cmd = trans.addCustomFunction("SP2C2","Work_Order_Account_Type_API.Get_Client_Value(0)","CLIENTVAL4");

        cmd = trans.addCustomFunction("CON","Active_Work_Order_API.Get_Contract","CONTRACT_VAR1");
        cmd.addParameter("WO_NO",sWoNo);

        cmd = trans.addCustomFunction("GETCOMPANY","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT_VAR1","CON/DATA");

        cmd = trans.addCustomFunction("FIXED","Active_Work_Order_API.Get_Fixed_Price","ACTIVEWORKORDERFIXEDPRICE");
        cmd.addParameter("WO_NO",sWoNo);

        cmd = trans.addCustomFunction("NFIXED","Fixed_Price_API.Decode('1')","NOTFIXEDPRICE");

        cmd = trans.addCustomFunction("COSTCENT","Active_Work_Order_API.Get_Cost_Center","CODE_C");
        cmd.addParameter("WO_NO",sWoNo);

        cmd = trans.addCustomFunction("OBJ","Active_Work_Order_API.Get_Object_No ","CODE_D");
        cmd.addParameter("WO_NO",sWoNo);

        cmd = trans.addCustomFunction("PROJ","Active_Work_Order_API.Get_Project_No ","CODE_G");
        cmd.addParameter("WO_NO",sWoNo);

        cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_CODING_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        String strWorkOrdCost = trans.getValue("SPC21/DATA/CLIENTVAL3");
        String strWorkAccount = trans.getValue("SP2C2/DATA/CLIENTVAL4");
        String strContract = trans.getValue("CON/DATA/CONTRACT_VAR1");
        String strComp = trans.getValue("GETCOMPANY/DATA/COMPANY");

        String strActive = trans.getValue("FIXED/DATA/ACTIVEWORKORDERFIXEDPRICE");
        String stNotFixed = trans.getValue("NFIXED/DATA/NOTFIXEDPRICE");
        String strCost = trans.getValue("COSTCENT/DATA/CODE_C");
        String strObj = trans.getValue("OBJ/DATA/CODE_D");
        String strProj = trans.getValue("PROJ/DATA/CODE_G");

        data = trans.getBuffer("HEAD/DATA");                
        data.setFieldItem("WO_NO",sWoNo);                           
        data.setFieldItem("WORK_ORDER_COST_TYPE",strWorkOrdCost);             
        data.setFieldItem("WORK_ORDER_ACCOUNT_TYPE",strWorkAccount);                   
        data.setFieldItem("CONTRACT",strContract); 
        data.setFieldItem("COMPANY",strComp);

        data.setFieldItem("ACTIVEWORKORDERFIXEDPRICE",strActive);            
        data.setFieldItem("NOTFIXEDPRICE",stNotFixed);          
        data.setFieldItem("OBJECT_NO",strObj);                           
        data.setFieldItem("PROJECT_NO",strProj);                            

        headset.addRow(data);      

    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();
        int oldLay = headlay.getHistoryMode();

        int currrow = headset.getCurrentRowNo();
        woNo = headset.getRow().getValue("WO_NO");

        if (sentNoEmployee)
        {
            row = headset.getRow(); 

/* ----------Added this part because rowset.setEdited function doesn't seem to work here
-------------Artha (2001-08-17) ------------- */

            String sAttr = null;

            for (int i=2;i<row.countItems();i++)
            {
                if ((mgr.getASPField(row.getNameAt(i)).isReadOnly())
                    ||(mgr.getASPField(row.getNameAt(i)).isComputable())
                    ||(mgr.getASPField(row.getNameAt(i)).isHidden())
                    ||"ROW_NO".equals(row.getNameAt(i)))
                    continue;

                if (mgr.isEmpty(sAttr))
                {
                    sAttr = row.getNameAt(i)+(char)31+(mgr.isEmpty(mgr.readValue(row.getNameAt(i)))?"":mgr.readValue(row.getNameAt(i)))+(char)30;
                }
                else
                    sAttr += row.getNameAt(i)+(char)31+(mgr.isEmpty(mgr.readValue(row.getNameAt(i)))?"":mgr.readValue(row.getNameAt(i)))+(char)30;                     
            }

            trans.clear();   

            cmd = trans.addCustomCommand("ACTSAVERET","WORK_ORDER_CODING_API.Modify__");
            cmd.addParameter("INFO", "");
            cmd.addParameter("OBJID", headset.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", headset.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR", sAttr);
            cmd.addParameter("ACTION", "DO");

            trans = mgr.perform(trans); 
/*-------------- End -------------------------------------------------*/
        }
        else
        {
            headset.changeRow();

            double list_price = headset.getRow().getNumberValue("LIST_PRICE");
            double discount = headset.getRow().getNumberValue("DISCOUNT");
            if ( isNaN(list_price) )
                list_price = 0;
            if ( isNaN(discount) )
                discount = 0;
            temp = headset.getRow();
            temp.setNumberValue("LIST_PRICE",list_price);
            temp.setNumberValue("DISCOUNT",discount);
            headset.setRow(temp);
            headset.setEdited("LIST_PRICE,DISCOUNT");
            mgr.submit(trans);
        }

        trans.clear();

        okFindSave();
        setSalesPartDesc();
        headset.goTo(currrow);
        headlay.setLayoutMode(oldLay);
    }
    public void duplicateRow()
    {
        ASPManager mgr = getASPManager();

        headset.goTo(headset.getRowSelected());

        ASPBuffer itemRowVals = headset.getRow();
        itemRowVals.setValue("NOTE_ID","");
        itemRowVals.setValue("STATE","");
        headset.addRow(itemRowVals);

        headlay.setLayoutMode(headlay.NEW_LAYOUT);
    }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void authorizeSelectedRows()
    {
        ASPManager mgr = getASPManager();

        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }
	authorizeByUserByDefault();
        // 040210  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void authorizeAllRows()
    {
        ASPManager mgr = getASPManager();

	ASPBuffer transferBuffer;
        boolean hasDataToTransfer = false;
	int currentRow = headset.getCurrentRowNo();

        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        headset.unselectRows();
        headset.setFilterOff();

        transferBuffer = mgr.newASPBuffer();
        headset.first();
		for (int i = 0; i < headset.countRows(); i++)
        {
            if (mgr.isEmpty(headset.getValue("SIGNATURE")) && "TRUE".equals(headset.getValue("AUTH_ALLOWED")))
            {
                headset.selectRow();
                authorizeByUserByDefault();

                if (duplicateFlag)
                    break;

                headset.unselectRow();
            }
            headset.next();
        }
        headset.goTo(currentRow);
		// 040210 ARWILK End (Enable Multirow RMB actions)

    }

    public void authorizeCorrect()
    {
        int count;
        int currrow;
        int rows__;

        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        currrow = headset.getCurrentRowNo();          

        separator_= "@";
        wo_No = "" ;
        row_Nos = "";
        getSelectedRows_ = 0;
        flag = false;

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (headlay.isMultirowLayout())
            selectedrows= headset.countRows();
        else
            selectedrows= 1;
        rows__= 0;

        sigval = headset.getRow().getValue("SIGNATURE");
        String rownum = headset.getRow().getValue("ROW_NO");


        wo_No = headset.getRow().getValue("WO_NO");
        strCompany=headset.getRow().getValue("COMPANY");
        getcompany();
        buffer=mgr.newASPBuffer();

        for (count=1; count <= selectedrows; ++count)
        {
            if (mgr.isEmpty(headset.getRow().getValue("SIGNATURE")))
            {
                row_Nos =  row_Nos + headset.getRow().getValue("ROW_NO") + separator_;
                getSelectedRows_=getSelectedRows_ + 1;
                rows__ = rows__ + 1;

            }
            headset.next();
        }
        if (rows__ != 0)
        {
            if ("FALSE".equals(headset.getValue("AUTH_ALLOWED")))
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERCODING1AUTHNOTALLOWED: Cannot Authoriz when State of the Work Order is below Released."));
            }
            else
            {
                //Web Alignment - simplify code for RMBs
                bOpenNewWindow = true;
                urlString = "AuthorizeCodingDlgSM.page?COMPANY=" + mgr.URLEncode(companyvar) + "&WO_NO=" + mgr.URLEncode(wo_No) + "&ROW_NO=" + mgr.URLEncode(rownum) + "&SIGNATURE=" + mgr.URLEncode(sigval) ;
                newWinHandle = "AuthCorr";
                //
            }
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERCODING1AUTH: Cannot be Authorized.."));
        }

        headset.goTo(currrow); 
    }

    public void authorizeAndCorrectPost()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        String calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);
        String strWoNo = headset.getRow().getValue("WO_NO");

        mgr.redirectTo("WorkOrderCodingDlg.page?WO_NO="+ mgr.URLEncode(strWoNo));
    }

    public void documentText()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url); 

        headset.storeSelections();

        buffer=mgr.newASPBuffer();
        row=buffer.addBuffer("0");
        row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
        row=buffer.addBuffer("1");
        row.addItem("CATALOG_NO",headset.getRow().getValue("REQUISITION_LINE_NO"));
        row=buffer.addBuffer("2");
        row.addItem("NOTE_ID",headset.getRow().getValue("NOTE_ID"));
        row=buffer.addBuffer("3");
        row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        row=buffer.addBuffer("4");
        row.addItem("MCH_NAME",mgr.readValue("MCH_NAME",""));

        mgr.transferDataTo("../mpccow/DocumentTextDlg.page", buffer);
    }
    
    public void getDefaultUser(){
	ASPManager mgr = getASPManager();
	trans.clear();
	ASPCommand cmd = trans.addCustomFunction("USER_SIGN","Fnd_Session_API.Get_Fnd_User","SIGN");
	trans = mgr.perform(trans);

	defaultUser = trans.getValue("USER_SIGN/DATA/SIGN");
    }
    
    public void authorizeByUserByDefault()
    {
        String sComp = headset.getRow().getValue("COMPANY");
        String sEmpName;
        String sAuthId;
        String sWarning = "FALSE";
        ASPBuffer dataBuffer;

        ASPManager mgr = getASPManager();

        getDefaultUser();

        trans.clear();

        cmd = trans.addCustomFunction("PERSON", "Person_Info_API.Get_Id_For_User", "SIGNATURE");
        cmd.addParameter("SIGN", defaultUser);

        cmd = trans.addCustomFunction("EMP1", "Company_Emp_API.Get_Max_Employee_Id", "MAX_EMP_ID");
        cmd.addParameter("COMPANY", sComp);
        cmd.addReference("SIGNATURE", "PERSON/DATA");

        cmd = trans.addCustomFunction("EMP2", "Person_Info_API.Get_Name_For_User", "NAME");
        cmd.addParameter("SIGN", defaultUser);
        trans = mgr.perform(trans);

        sEmpName = trans.getValue("EMP2/DATA/NAME");
        sAuthId = trans.getValue("EMP1/DATA/MAX_EMP_ID");

        if (!mgr.isEmpty(sEmpName))
        {
            trans.clear();
            ASPBuffer selectedRowsBuf = headset.getSelectedRows("COMPANY,WO_NO,ROW_NO");

            if (mgr.isEmpty(sAuthId))
            {
                duplicateFlag = true;
                mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGNOVALIDEMP: A valid employee id does not exists for the logged in user " + defaultUser));
            }
            else
            {
                for (int i = 0; i < selectedRowsBuf.countItems(); i++)
                {
                    dataBuffer = selectedRowsBuf.getBufferAt(i);

                    cmd = trans.addCustomCommand("ADELIVER_" + i, "Work_Order_Coding_API.Authorize");
                    cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
                    cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
                    cmd.addParameter("SIGN", sAuthId);

                    cmd = trans.addCustomFunction("GETWARNING_" + i, "Work_Order_Coding_API.Has_Error_Transactions", "WARNING");
                    cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
                    cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
                }

                trans = mgr.perform(trans);

                for (int i = 0; i < selectedRowsBuf.countItems(); i++)
                {
                    if ("TRUE".equals(trans.getBuffer("GETWARNING_" + i).getValue("WARNING")))
                        sWarning = "TRUE";
                }

                if ("TRUE".equals(sWarning))
                    mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGERRTRANS: Exists Error on Transactions. See Transaction History for more information."));

                returnFlag = "TRUE";
            }

        }
        else
        {
            duplicateFlag = true;
            mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGAUTHNOTREG: The authorizer is not registered."));
        }
        headset.refreshAllRows();
    }


    public void salesPartComp()
    {
        ASPManager mgr = getASPManager();

        String singleRowNo = "";

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
        {
            headset.selectRow();
            singleRowNo = String.valueOf(headset.getCurrentRowNo());
        }

        enableSalesPartComp();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url); 

        if (enableF)
        {
            buffer=mgr.newASPBuffer();
            row=buffer.addBuffer("0");
            row.addItem("CATALOG_NO",headset.getRow().getValue("CATALOG_NO"));
            row=buffer.addBuffer("1");
            row.addItem("LIST_PRICE",headset.getRow().getValue("LIST_PRICE"));
            row=buffer.addBuffer("2");
            row.addItem("CONTRACT",headset.getRow().getFieldValue("CONTRACT"));
            row=buffer.addBuffer("3");
            row.addItem("LINE_DESCRIPTION",headset.getRow().getValue("LINE_DESCRIPTION"));
            row=buffer.addBuffer("4");
            row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
            row=buffer.addBuffer("5");
            row.addItem("ROW_NO",headset.getRow().getValue("ROW_NO"));
            row=buffer.addBuffer("6");
            row.addItem("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
            row=buffer.addBuffer("7");
            row.addItem("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
            row=buffer.addBuffer("8");
            row.addItem("FORM2",mform);
            row=buffer.addBuffer("9");
            row.addItem("SINGLE_ROW_NO",singleRowNo);
            row=buffer.addBuffer("10");
            row.addItem("PRICE_LIST_NO",headset.getRow().getValue("PRICE_LIST_NO"));
            row=buffer.addBuffer("11");
            row.addItem("DISCOUNT",headset.getRow().getValue("DISCOUNT"));

            String fmtdBuff = buffer.format();
            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;  
            urlString = "SalesPartComplimentaryDlg.page?__TRANSFER=" + mgr.URLEncode(fmtdBuff);
            newWinHandle = "sales";
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERCODING1ONERECONLY11: Cannot perform the action."));
        }
    }

    public void enableSalesPartComp()
    {
        ASPManager mgr = getASPManager();

        String catNo = headset.getRow().getValue("CATALOG_NO");
        String bookSta = headset.getRow().getValue("WORK_ORDER_BOOK_STATUS");
        String costTy = headset.getRow().getValue("WORK_ORDER_COST_TYPE");   
        String cusOrNo = headset.getRow().getValue("ORDER_NO"); 

        cmd = trans.addCustomFunction("FUNC1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");
        cmd = trans.addCustomFunction("FUNC2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","CLIENTVAL2");
        cmd = trans.addCustomFunction("FUNC3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");
        cmd = trans.addCustomFunction("FUNC4","Work_Order_Cost_Type_Api.Get_Client_Value(4)","CLIENTVAL4");

        trans = mgr.validate(trans);

        String sMaterial = trans.getValue("FUNC1/DATA/CLIENTVAL1");
        String sExpences = trans.getValue("FUNC3/DATA/CLIENTVAL3");
        String sExternal = trans.getValue("FUNC2/DATA/CLIENTVAL2");
        String sFixed    = trans.getValue("FUNC4/DATA/CLIENTVAL4");

        enableF = true;
    }

    public void manageSalesRev()
    {
        ASPManager mgr = getASPManager();

        String str = "";
        String urlString_ = "";
        String singleRowNo = "";

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            singleRowNo = String.valueOf(headset.getCurrentRowNo());

        ctx.setGlobal("CALLING_URL",mgr.getURL());

        rowNo = headset.getValue("ROW_NO");

        returnForm = ctx.findGlobal("MFORM");

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString_ = "WorkOrderCodingDlg.page?ROW_NO="+mgr.URLEncode(rowNo)+"&WONO="+mgr.URLEncode(sWoNo);
        if (headlay.isSingleLayout())
        {
            str = "&SINGLE_ROW_NO="+mgr.URLEncode(singleRowNo);
        }
        str += "&BACKFRMNAME="+mgr.URLEncode(returnForm);
        urlString = urlString_+str;
        newWinHandle = "manageSalesRev";
        //
    }

    public void createFromPlanPosting()
    {
        ASPManager mgr = getASPManager();

        separator_="@";
        wo_No ="" ;
        row_Nos="";
        getSelectedRows_=0;
        flag=false;

        headset.goTo(headset.getRowSelected());

        if (mgr.isEmpty(sWoNo))
        {
            crePlanPath = headset.getRow().getValue("WO_NO");
        }
        else
            crePlanPath = sWoNo;

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "WorkOrderPlanning1.page?WO_NO=" + mgr.URLEncode(crePlanPath)+"&FRMNAME=WorkOrCoding1&RMBNAME=CreatePlan"+"&BACKFRMNAME="+mgr.URLEncode(backToForm);
        newWinHandle = "CreatePlan";
        //  

        String current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", current_url);    
    }

    public void connectToPlanPosting()
    {
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        ASPManager mgr = getASPManager();  

        String costType = headset.getRow().getValue("WORK_ORDER_COST_TYPE_DB");
        String bookStatus = headset.getRow().getValue("WORK_ORDER_BOOK_STATUS_DB");

        if ("M".equals(costType) || "A".equals(bookStatus) || "T".equals(bookStatus))
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERCODING1STATUSNOTVALID: Cannot perform on selected line."));
        }
        else
        {
            separator_="@";
            wo_No ="" ;
            row_Nos="";
            getSelectedRows_=0;

            flag=false;
            connReconToPlan = "true"; 

            if (mgr.isEmpty(sWoNo))
            {
                crePlanPath = headset.getRow().getValue("WO_NO");
            }
            else
                crePlanPath = sWoNo;   

            String current_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL",current_url);         

            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());

            rowToEdit = headset.getCurrentRowNo();
            String filterCostType = headset.getRow().getValue("WORK_ORDER_COST_TYPE");

            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            urlString = "WorkOrderPlanning1.page?WO_NO="+mgr.URLEncode(crePlanPath)+"&FRMNAME=WorkOrCoding1&RMBNAME=ConnectPlan"+"&EDITROWNUM="+rowToEdit+"&FILCOSTYP="+mgr.URLEncode(filterCostType)+"&BACKFRMNAME="+mgr.URLEncode(backToForm);
            newWinHandle = "ConnectPlann";
            //
        }
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void back()
    {
        ASPManager mgr = getASPManager();

        mform = ctx.readValue("MFORM",mform);
        mform1 = ctx.readValue("MFORM1",mform1); 

        int beg_pos = 0;
        int end_pos = 0;
        String form_name = "";
        String calling_url = "";

        if (!mgr.isEmpty(mform))
        {
            beg_pos = mform.lastIndexOf("/")+1;
            end_pos = mform.lastIndexOf(".");
            form_name = mform.substring(beg_pos,end_pos);

            if ("ActiveSeperateReportInWorkOrder".equals(form_name))
            {
                mgr.redirectTo("../pcmw/ActiveSeperateReportInWorkOrder.page?WO_NO="+mgr.URLEncode(sWoNo)); 
            }

            else if ("ActiveSeperateReportInWorkOrderSM".equals(form_name))
            {
                mgr.redirectTo("../pcmw/ActiveSeperateReportInWorkOrderSM.page?WO_NO="+mgr.URLEncode(sWoNo)); 
            }
        }
        else if (!mgr.isEmpty(mform1))
        {
            beg_pos = mform1.lastIndexOf("/")+1;
            end_pos = mform1.lastIndexOf(".");
            form_name = mform1.substring(beg_pos,end_pos);

            if ("ActiveSeperateReportInWorkOrder".equals(form_name))
            {
                mgr.redirectTo("../pcmw/ActiveSeperateReportInWorkOrder.page?WO_NO="+mgr.URLEncode(sWoNo));  
            }

            else if ("ActiveSeperateReportInWorkOrderSM".equals(form_name))
            {
                mgr.redirectTo("../pcmw/ActiveSeperateReportInWorkOrderSM.page?WO_NO="+mgr.URLEncode(sWoNo)); 
            }
        }
        else if (!mgr.isEmpty(backToForm))
        {
            mgr.redirectTo(backToForm+"?WO_NO="+mgr.URLEncode(sWoNo));
        }
        else if (backFlag)
        {
            calling_url = ctx.getGlobal("CALLING_URL");

            beg_pos = calling_url.lastIndexOf("/")+1;
            end_pos = calling_url.lastIndexOf(".");
            form_name = calling_url.substring(beg_pos,end_pos);

            mgr.redirectTo(calling_url+"?WO_NO="+mgr.URLEncode(sWoNo));

        }
        else
        {
            calling_url = ctx.getGlobal("MFORM");
            mgr.redirectTo(calling_url+"?WO_NO="+mgr.URLEncode(sWoNo));
        }
    }

    //Bug 44442, start
    public void setSalesPartDesc()
    {
        int i;
        String catalogNo;
        String Contract;
        ASPManager mgr = getASPManager();
        boolean isSecure[] = new boolean[10] ;
        String catDesc;
        trans.clear();
        int n = headset.countRows();
        headset.first();
        checksec("Sales_Part_API.Get_Catalog_Desc",1,isSecure);

        if (n > 0)
        {
            for (i=0; i<=n; ++i)
            {
                catalogNo = headset.getRow().getValue("CATALOG_NO");
                Contract =headset.getRow().getValue("CONTRACT");

                if (isSecure[1])
                {
                    cmd = trans.addCustomFunction("DESC"+i,"Sales_Part_API.Get_Catalog_Desc","LINE_DESCRIPTION");
                    cmd.addParameter("CONTRACT",Contract);
                    cmd.addParameter("CATALOG_NO",catalogNo);
                }
                headset.next();
            }

            trans = mgr.validate(trans);
            headset.first();

            for (i=0; i<=n; ++i)
            {
                row = headset.getRow();

                if (isSecure[1])
                {

                    catDesc= trans.getValue("DESC"+i+"/DATA/LINE_DESCRIPTION");

                    if (mgr.isEmpty(headset.getRow().getValue("LINE_DESCRIPTION")))
                        row.setValue("LINE_DESCRIPTION",catDesc);
                }

                headset.setRow(row);

                headset.next();
            }
        }
        headset.first();
    }
//--------------------Lableing fields--------------------------------------

    public void setStringLables()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();

            cmd = trans.addCustomFunction("DEFCONTRACT", "Site_API.Get_Company", "COMPANY" );
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));    

            cmd = trans.addCustomFunction("STRA", "Accounting_Code_Parts_API.Get_Name", "ACCNT" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","A");

            cmd = trans.addCustomFunction("STRB", "Accounting_Code_Parts_API.Get_Name", "COSTCENT" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","B");

            cmd = trans.addCustomFunction("STRC", "Accounting_Code_Parts_API.Get_Name", "PROJECT_NO" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","C");

            cmd = trans.addCustomFunction("STRD", "Accounting_Code_Parts_API.Get_Name", "CODE_D" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","D");

            cmd = trans.addCustomFunction("STRE", "Accounting_Code_Parts_API.Get_Name", "OBJECT_NO" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","E");

            cmd = trans.addCustomFunction("STRF", "Accounting_Code_Parts_API.Get_Name", "CODE_F" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","F");

            cmd = trans.addCustomFunction("STRG", "Accounting_Code_Parts_API.Get_Name", "CODE_G" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","G");

            cmd = trans.addCustomFunction("STRH", "Accounting_Code_Parts_API.Get_Name", "CODE_H" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","H");

            cmd = trans.addCustomFunction("STRI", "Accounting_Code_Parts_API.Get_Name", "CODE_I" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","I");

            cmd = trans.addCustomFunction("STRJ", "Accounting_Code_Parts_API.Get_Name", "CODE_J" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","J");

            trans = mgr.perform(trans);

            ctx.setGlobal("NAMEB","Cost cent");
            ctx.setGlobal("NAMEC","Region");
            ctx.setGlobal("NAMED","Sales");
            ctx.setGlobal("NAMEE","Object");
            ctx.setGlobal("NAMEF","Project");
            ctx.setGlobal("NAMEG","Code G");
            ctx.setGlobal("NAMEH","Code H");
            ctx.setGlobal("NAMEI","Code I");
            ctx.setGlobal("NAMEJ","Code J");

            strLableA = trans.getValue("STRA/DATA/ACCNT");
            strLableB = trans.getValue("STRB/DATA/COSTCENT");
            strLableC = trans.getValue("STRC/DATA/PROJECT_NO");
            strLableD = trans.getValue("STRD/DATA/CODE_D");
            strLableE = trans.getValue("STRE/DATA/OBJECT_NO");
            strLableF = trans.getValue("STRF/DATA/CODE_F");
            strLableG = trans.getValue("STRG/DATA/CODE_G");
            strLableH = trans.getValue("STRH/DATA/CODE_H");
            strLableI = trans.getValue("STRI/DATA/CODE_I");
            strLableJ = trans.getValue("STRJ/DATA/CODE_J");
            trans.clear();
        }
    }

    public void lableingFields()
    {
        ASPManager mgr = getASPManager();

        mgr.getASPField("ACCNT").setLabel(strLableA);
        mgr.getASPField("COST_CENTER").setLabel(strLableB);
        mgr.getASPField("CODE_C").setLabel(strLableC);
        mgr.getASPField("CODE_D").setLabel(strLableD);
        mgr.getASPField("OBJECT_NO").setLabel(strLableE);
        mgr.getASPField("PROJECT_NO").setLabel(strLableF);
        mgr.getASPField("CODE_G").setLabel(strLableG );
        mgr.getASPField("CODE_H").setLabel(strLableH );
        mgr.getASPField("CODE_I").setLabel(strLableI);
        mgr.getASPField("CODE_J").setLabel(strLableJ);
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setHidden();
        f.setCustomValidation("CONTRACT","COMPANY");
        f.setLabel("PCMWWORKORDERCODING1ITEM2CONTRACT: Site");
        f.setUpperCase();

        f = headblk.addField("COMPANY");
        f.setHidden();

        f = headblk.addField("AGREEMENT_ID");
        f.setHidden();
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Agreement_Id(:WO_NO)");

        f = headblk.addField("WORK_ORDER_COST_TYPE");
        f.setSize(11);
        f.setMandatory();
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");
        f.setLabel("PCMWWORKORDERCODING1ITEM2WORKORDETTYPE: Cost Type");
        f.setReadOnly();
        f.setInsertable();
        f.setCustomValidation("WORK_ORDER_COST_TYPE,WO_NO","COST_CENTER,CODE_C,CODE_D,CODE_E,CODE_F,CODE_G,CODE_H,CODE_I,ACTIVITY_SEQ,AMOUNT");

        f = headblk.addField("WORK_ORDER_COST_TYPE_DB");
        f.setFunction("WORK_ORDER_COST_TYPE_API.Encode(WORK_ORDER_COST_TYPE)");
        f.setHidden();

        f = headblk.addField("PLAN_LINE_NO","Number");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ITEM2PLANLINENO: Plan Line No");
        f.setLOV("WorkOrderRolePlanningLovLov.page","WO_NO",600,450);
        f.setUpperCase();
        f.setCustomValidation("WO_NO,PLAN_LINE_NO","CRAFT_ROW,CRAFT_REFERENCE_NUMBER");

        f = headblk.addField("CRAFT_ROW","Number");
        f.setSize(8);
        f.setLabel("PCMWWORKORDERCODING1ITEM2WORKORDETTYPECRAFTROW: Operation No");
        f.setFunction("Work_Order_Role_API.Get_Plan_Row_No(:WO_NO, :PLAN_LINE_NO)" ) ;
        f.setReadOnly(); 

        f = headblk.addField("CRAFT_REFERENCE_NUMBER");
        f.setSize(25);
        f.setLabel("PCMWWORKORDERCODING1ITEM2WORKORDETTYPECRAFTREFNO: Reference No");
        f.setFunction("Work_Order_Role_API.Get_Plan_Ref_Number(:WO_NO, :PLAN_LINE_NO)" ) ;
        f.setMaxLength(25); 
        f.setReadOnly();

        f = headblk.addField("CUSTOMER_NO");
        f.setHidden();
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Customer_No(:WO_NO)");

        f = headblk.addField("CATALOG_NO");
        f.setSize(17);
        f.setLabel("PCMWWORKORDERCODING1ITEM2CATALOG_NO: Sales Part Number");
        f.setUpperCase();
        f.setCustomValidation("CUSTOMER_NO,AGREEMENT_ID,WO_NO,CATALOG_NO,PRICE_LIST_NO,QTY,WORK_ORDER_COST_TYPE,CONTRACT,ALTERNATIVE_CUSTOMER,AMOUNT,QTY_TO_INVOICE,LIST_PRICE,DISCOUNT","LINE_DESCRIPTION,AMOUNT,LIST_PRICE,SALESPRICEAMOUNT,DISCOUNT,PRICE_LIST_NO,QTY_TO_INVOICE");
        f.setMaxLength(18);
        if (mgr.isModuleInstalled("ORDER"))
            f.setDynamicLOV("SALES_PART","CONTRACT",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODING1CATNO: Sales Part Number")); 


        f = headblk.addField("LINE_DESCRIPTION");
        f.setSize(18);
        f.setLabel("PCMWWORKORDERCODING1LINE_DESCRIPTION: Description");

        f = headblk.addField("ORDER_NO");
        f.setSize(16);
        f.setLabel("PCMWWORKORDERCODING1ORDERNUMBER: Customer Order No");
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("PRICE_LIST_NO");
        f.setSize(13);
        if (mgr.isModuleInstalled("ORDER"))
            f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setCustomValidation("ALTERNATIVE_CUSTOMER,PRICE_LIST_NO,QTY_TO_INVOICE,CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,LIST_PRICE,DISCOUNT,WO_NO",",LIST_PRICE,DISCOUNT,SALESPRICEAMOUNT,PRICE_LIST_NO");
        f.setLabel("PCMWWORKORDERCODING1ITEM2PRICELISTNO: Price List No");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODING1PRLINO2: Price List No"));
        f.setDefaultNotVisible();

	f = headblk.addField("CRE_DATE", "Date");
	f.setLabel("PCMWWORKORDERCODING1CREDATE: Creation Date");

        f = headblk.addField("QTY","Number");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ITEM2QTY: Hours/Qty");
        f.setCustomValidation("QTY,CONTRACT,CATALOG_NO,WORK_ORDER_COST_TYPE","AMOUNT");

        f = headblk.addField("QTY_TO_INVOICE","Number");
        f.setSize(13);
        f.setCustomValidation("QTY_TO_INVOICE,LIST_PRICE,DISCOUNT","SALESPRICEAMOUNT");
        f.setLabel("PCMWWORKORDERCODING1ITEM2QTYTOINV: Qty To Invoice");

        f = headblk.addField("AMOUNT","Money");
        f.setSize(11);
        f.setLabel("PCMWWORKORDERCODING1AMOUNT: Cost Amount");
        f.setReadOnly();
        f.setInsertable();
        f.setMandatory();   
        f.setDefaultNotVisible();

        f = headblk.addField("LIST_PRICE","Money");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ITEM2SALEPRICE: Sales Price");
        f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE,DISCOUNT","SALESPRICEAMOUNT");

        f = headblk.addField("PLAN_SALES_PRICE","Money");
        f.setSize(13);
        f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE","SALESPRICEAMOUNT");
        f.setLabel("PCMWWORKORDERCODING1ITEM2LISTPRICE: Plan Sales Price");
        f.setFunction("WORK_ORDER_PLANNING_API.Get_Sales_Price(:WO_NO,:PLAN_LINE_NO)");
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_ORDER_ACCOUNT_TYPE");
        f.setSize(12);
        f.setHidden();

        f = headblk.addField("DISCOUNT","Number");
        f.setSize(17);
        f.setLabel("PCMWWORKORDERCODING1DISCOUNT: Discount");
        f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE,DISCOUNT","SALESPRICEAMOUNT");
        f.setDefaultNotVisible();

        f = headblk.addField("PLAN_DISCOUNT","Number");
        f.setSize(17);
        f.setLabel("PCMWWORKORDERCODING1PLANDISCOUNT: Plan Discount%");
        f.setFunction("WORK_ORDER_PLANNING_API.Get_Discount(:WO_NO, :PLAN_LINE_NO)");
        f.setDefaultNotVisible();

        f = headblk.addField("SALESPRICEAMOUNT","Money");
        f.setSize(17);
        f.setLabel("PCMWWORKORDERCODING1SALESPRICEAMOUNT: Sales Price Amount");
        f.setFunction("((LIST_PRICE - (NVL(DISCOUNT, 0) / 100 * LIST_PRICE))*QTY_TO_INVOICE)");
        f.setReadOnly();

        f = headblk.addField("AMOUNT_VAR","Money");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CONTRACT_VAR1");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERCODING1PARTOWNERSHIP: Ownership");

        f = headblk.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = headblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setLabel("PCMWWORKORDERCODING1PARTOWNER: Owner");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERCODING1PARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = headblk.addField("AGREEMENT_PRICE_FLAG");
        f.setSize(22);
        f.setLabel("PCMWWORKORDERCODING1ITEM2PRICELOGICFLAG: Use Price Logic");
        f.setCheckBox("0,1");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERCODING1KEEPREVENUE: Keep Revenue");
        f.setSelectBox();
        f.enumerateValues("KEEP_REVENUE_API");

        f = headblk.addField("CSS_TYPE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERCODING1CSSTYPE: CSS Type");
        f.setReadOnly();

        f = headblk.addField("STATE");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERCODING1STATUS: Status");
        f.setReadOnly();

        f = headblk.addField("WORK_ORDER_BOOK_STATUS");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ITEM2WORKORDERBOOKSTATUS: Booking Status");
        f.setReadOnly();

        f = headblk.addField("WORK_ORDER_BOOK_STATUS_DB");
        f.setFunction("WORK_ORDER_BOOK_STATUS_API.Encode(WORK_ORDER_BOOK_STATUS)");
        f.setHidden();

        f = headblk.addField("SIGNATURE");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1SIGNATURE: Auth Signature");
        f.setUpperCase();
        f.setCustomValidation("SIGNATURE,COMPANY","SIGNATURE_ID");
        f.setMaxLength(20);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODING1SIG: Auth Signature"));
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("SIGNATURE_ID");
        f.setSize(13);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1SIGNATUREID: Auth Signature");
        f.setUpperCase();

        f = headblk.addField("CMNT");
        f.setSize(20);
        f.setLabel("PCMWWORKORDERCODING1ITEM2CMNT: Comment");
        f.setMaxLength(80);
        f.setDefaultNotVisible();

        f = headblk.addField("ACCNT");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ACCNT: Account");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("COST_CENTER");  
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1ITEM2COSTCENTER: Cost Center");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_F");
        f.setFunction("''");
        f.setLabel("PCMWWORKORDERCODING1CODEF: Code F");
        f.setMaxLength(10);
        f.setSize(13);
        f.setHidden();

        f = headblk.addField("PROJECT_NO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1PROJECT: Project");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("OBJECT_NO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1OBJECT: Object");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_C");
        f.setSize(13);
        f.setDynamicLOV("CODE_C","COMPANY COMPANY",600,445);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_D");
        f.setSize(13);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_E");
        f.setSize(13);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CODE_G");
        f.setSize(13);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_H");
        f.setSize(13);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_I");
        f.setSize(13);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CODE_J");
        f.setSize(13);
        f.setDynamicLOV("CODE_J","COMPANY COMPANY",600,445);
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f= headblk.addField("ACTIVITY_SEQ");
        f.setHidden();

        f= headblk.addField("PRE_POSTING_ID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CDOCUMENTTEXT");
        f.setSize(14);
        f.setLabel("PCMWWORKORDERCODING1CDOCUMENTTEXT: Document Text");
        f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
        f.setCheckBox("0,1");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REQUISITION_NO");
        f.setSize(13);
        f.setDynamicLOV("WORK_ORDER_REQUIS_HEADER",600,445);
        f.setLabel("PCMWWORKORDERCODING1REQUISITIONNO: Requisition No");
        f.setMaxLength(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REQUISITION_LINE_NO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1REQUISITIONLINENO: Requisition Line No");
        f.setMaxLength(4);
        f.setDefaultNotVisible();

        f = headblk.addField("REQUISITION_RELEASE_NO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1REQUISITIONRELEASENO: Requisition Release No");
        f.setMaxLength(4);
        f.setDefaultNotVisible();

        f = headblk.addField("REQUISITIONVENDORNO");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1REQUISITIONVENDORNO: Requisition Supplier No");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
        mgr.getASPField("REQUISITION_NO").setValidation("REQUISITIONVENDORNO");
        f.setDefaultNotVisible();

        f = headblk.addField("EMP_NO");
        f.setSize(9);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1ITEM2EMPNO: Employee");
        f.setUpperCase();

        f = headblk.addField("WO_NO","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();

        f = headblk.addField("CATALOGDESC");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1CATALOGDESC: Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        
        f = headblk.addField("NOTE_ID","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1NOTEID: Note ID");

        f = headblk.addField("CLIENTVAL1");
        f.setHidden();
        f.setFunction("''");    

        f = headblk.addField("CLIENTVAL2");
        f.setHidden();
        f.setFunction("''");    

        f = headblk.addField("CLIENTVAL3");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CLIENTVAL4");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("STRING");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COSTCENT");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("ACTIVEWORKORDERFIXEDPRICE");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1ACTIVEWORKORDERFIXEDPRICE: Fixed Price");
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:WO_NO)");

        f = headblk.addField("NOTFIXEDPRICE");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERCODING1NOTFIXEDPRICE: Not Fixed Price");
        f.setFunction("Fixed_Price_API.Decode('1')");

        f = headblk.addField("BASE_PRICE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SALE_PRICE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("FORM2");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CURRENCY_RATE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("QTY1","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("DEFCONTRACT");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SALESPARTCATALOGDESC");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setHidden();

        //bug id 43810,start
        f = headblk.addField("TEMPLINEDESC");
        f.setFunction("WORK_ORDER_PLANNING_API.Get_Line_Desc(:WO_NO,:PLAN_LINE_NO)");
        f.setHidden();

        f = headblk.addField("BASE_UNIT_PRICE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SALE_UNIT_PRICE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("BUY_QTY_DUE","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ALTERNATIVE_CUSTOMER");
        f.setSize(13);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setLabel("PCMWWORKORDERCODING1ALTCUSTOMER: Alt Customer");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERCODING1ALTCUS: Alternative Customer"));   
        f.setDefaultNotVisible();

        f = headblk.addField("COST_SOURCE","Number");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCODING1COSTSOURCE: Cost Source");
        f.setMaxLength(10);   
        f.setDefaultNotVisible();

        f = headblk.addField("ROW_NO","Number");
        f.setLabel("PCMWWORKORDERCODING1ROWNO: Row No");
        f.setMaxLength(2);   
        f.setDefaultNotVisible();

        f = headblk.addField("INFO");
        f.setFunction("''");   
        f.setHidden();

        f = headblk.addField("ATTR");
        f.setFunction("''");   
        f.setHidden();

        f = headblk.addField("ACTION");
        f.setFunction("''");   
        f.setHidden();

        headblk.addField("CURRENCEY_CODE").
        setHidden().
        setFunction("''");

        f = headblk.addField("AUTH_ALLOWED");
        f.setFunction("Work_Order_Coding_API.Is_Authorisation_Allowed(:WO_NO)");   
        f.setHidden();

        f = headblk.addField("HAS_UNAUTHORIZED");
        f.setFunction("''");   
        f.setHidden();
        f = headblk.addField("PRICE_SOURCE");
        f.setFunction("''");   
        f.setHidden();

        f = headblk.addField("PRICE_SOURCE_ID");
        f.setFunction("''");   
        f.setHidden();

        f = headblk.addField("SYS_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SEARCH");
        f.setFunction("'Y'");
        f.setHidden();

        f = headblk.addField("TRANSACTION_ID");
        f.setLabel("PCMWWORKORDERCODING1TRANSACTIONID: Transaction ID");
        f.setDefaultNotVisible();

	f = headblk.addField("MAX_EMP_ID");
	f.setSize(8);
	f.setHidden();
	f.setUpperCase();
	f.setFunction("''");

	f = headblk.addField("NAME");
	f.setHidden();
	f.setFunction("''");

	f = headblk.addField("SIGN");
	f.setSize(8);
	f.setHidden();
	f.setUpperCase();
	f.setFunction("''");
	 
	f = headblk.addField("WARNING"); 
	f.setHidden();
	f.setFunction("''");

        headblk.setView("WORK_ORDER_CODING");
        headblk.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.defineCommand(headbar.NEWROW, "newRow");
        headbar.defineCommand(headbar.SAVERETURN, "saveReturn", "checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW, null, "checkHeadFields(-1)");
        headbar.defineCommand(headbar.DUPLICATEROW, "duplicateRow");

        headbar.addCustomCommand("authorizeSelectedRows", mgr.translate("PCMWWORKORDERCODING1AUTHSELRO: Authorize Selected Rows..."));
        headbar.addCustomCommand("authorizeAllRows", mgr.translate("PCMWWORKORDERCODING1AUTHALLNOAUHT: Authorize All Non Authorized..."));
        headbar.addCustomCommand("authorizeCorrect", mgr.translate("PCMWWORKORDERCODING1AUTHCORRE: Authorize and Correct Postings..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("documentText", mgr.translate("PCMWWORKORDERCODING1DOCTEXT: Document Text..."));
        //headbar.addCustomCommand("salesPartComp", mgr.translate("PCMWWORKORDERCODING1SAPACOMP: Sales Part Complimentary..."));
        headbar.addCustomCommand("manageSalesRev", mgr.translate("PCMWWORKORDERCODING1MANAGESALESREV: Manage Sales Revenues..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("connectToPlanPosting", mgr.translate("PCMWWORKORDERCODING1CONNRECOFROPLSM: Connect/Reconnect To Planning..."));
        //Links converted to RMBs
        headbar.addCustomCommand("createFromPlanPosting", mgr.translate("PCMWWORKORDERCODING1CREFROPLANSM1: Create from Planning..."));
        //

        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        headbar.addCommandValidConditions("authorizeSelectedRows",    "SIGNATURE",    "Enable", "null");
        headbar.appendCommandValidConditions("authorizeSelectedRows", "AUTH_ALLOWED", "TRUE");
        // 040210  ARWILK  End  (Enable Multirow RMB actions)

        headbar.addCommandValidConditions("manageSalesRev","CUSTOMER_NO","Disable","");

        //Links with multirow RMBs
        headbar.enableMultirowAction();

        headbar.removeFromMultirowAction("authorizeCorrect");
        headbar.removeFromMultirowAction("documentText");
        //headbar.removeFromMultirowAction("salesPartComp");
        headbar.removeFromMultirowAction("connectToPlanPosting");
        //

        headbar.forceEnableMultiActionCommand("authorizeAllRows");
        headbar.forceEnableMultiActionCommand("createFromPlanPosting");

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDERCODING1HD: Postings"));
        headtbl.setWrap();
        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        headtbl.enableRowSelect();
        // 040210  ARWILK  End  (Enable Multirow RMB actions)

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        headlay.setFieldOrder("WORK_ORDER_COST_TYPE,PLAN_LINE_NO,CRAFT_ROW,CRAFT_REFERENCE_NUMBER,CATALOG_NO,LINE_DESCRIPTION,ORDER_NO,PRICE_LIST_NO,CRE_DATE,QTY,QTY_TO_INVOICE,AMOUNT,LIST_PRICE,PLAN_SALES_PRICE,DISCOUNT,PLAN_DISCOUNT,SALESPRICEAMOUNT,PART_OWNERSHIP,OWNER,OWNER_NAME,AGREEMENT_PRICE_FLAG,KEEP_REVENUE,STATE,WORK_ORDER_BOOK_STATUS,SIGNATURE,CMNT,ACCNT,COST_CENTER,OBJECT_NO,PROJECT_NO,CODE_C,CODE_D,CODE_E,CODE_G,CODE_H,CODE_I,CODE_J,CDOCUMENTTEXT,REQUISITION_NO,REQUISITION_LINE_NO,REQUISITION_RELEASE_NO,REQUISITIONVENDORNO");
        headlay.setDialogColumns(2);

        dummyblk = mgr.newASPBlock("DUMMY");  

        dummyblk.addField("RMBVALUE").
        setFunction("''").
        setHidden();  

        dummyblk.setView("DUAL");
        dummyset = dummyblk.getASPRowSet();    
    }

    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            trans.addPresentationObjectQuery("INVENW/InventoryTransactionHist2Qry.page");
            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.namedItemExists("INVENW/InventoryTransactionHist2Qry.page"))
                actEna0 = true;

            again = true;
        }
    }

    public void getcompany()
    {
        ASPManager mgr = getASPManager();

        trans1.clear();

        cmd = trans1.addCustomFunction("DEFCO", "User_Default_API.Get_Contract", "DEFCONTRACT");

        cmd = trans1.addCustomFunction("COMPA", "Site_API.Get_Company", "COMPANY");
        cmd.addReference("DEFCONTRACT", "DEFCO/DATA");

        trans1 = mgr.perform(trans1);

        companyvar = trans1.getValue("COMPA/DATA/COMPANY");   
    }

    public void getSalesPartInfo()
    {
        int count;
        int headCurrRow=0;
        ASPManager mgr = getASPManager();

        boolean isSecure[] = new boolean[6] ;

        noRows =  headset.countRows();

        if (headlay.isSingleLayout())
            headCurrRow = headset.getCurrentRowNo();

        if ((checksec("Company_Finance_API.Get_Currency_Code", 2, isSecure)) && (checksec("Customer_Order_Pricing_API.Get_Valid_Price_List", 3, isSecure)))
        {
            headset.first();
            trans.clear();
            for (count = 1; count <= noRows; ++count)
            {
                if (!mgr.isEmpty(headset.getRow().getFieldValue("ALTERNATIVE_CUSTOMER")))
                {
                    nCusNo =  headset.getRow().getFieldValue("ALTERNATIVE_CUSTOMER");
                    nAgreeId = "";
                }
                else
                {
                    nCusNo =  sCustomer;  
                    nAgreeId = sAgreeId;
                }

                double buyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");

                if (isNaN(buyQtyDue))
                    buyQtyDue = 0;

                if (buyQtyDue == 0)
                    buyQtyDue = 1;

                cmd = trans.addCustomCommand("INFO" + count, "Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_UNIT_PRICE", "0");
                cmd.addParameter("SALE_UNIT_PRICE", "0");
                cmd.addParameter("DISCOUNT", "0");
                cmd.addParameter("CURRENCY_RATE", "0");
                cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
                cmd.addParameter("CATALOG_NO", headset.getRow().getFieldValue("CATALOG_NO"));
                cmd.addParameter("CUSTOMER_NO", nCusNo);
                cmd.addParameter("AGREEMENT_ID", nAgreeId);
                cmd.addParameter("PRICE_LIST_NO", headset.getRow().getValue("PRICE_LIST_NO"));
                cmd.addParameter("BUY_QTY_DUE", mgr.formatNumber("BUY_QTY_DUE", buyQtyDue));

                headset.next();
            }    

            trans = mgr.perform(trans);

            headset.first();

            for (count = 1; count <= noRows; ++count)
            {
                double baseUnitPrice = trans.getNumberValue("INFO" + count + "/DATA/BASE_UNIT_PRICE");
                if (isNaN(baseUnitPrice))
                    baseUnitPrice = 0;

                double discount = trans.getNumberValue("INFO" + count + "/DATA/DISCOUNT");
                if (isNaN(discount))
                    discount = 0;

                String priceListNo = trans.getValue("INFO" + count + "/DATA/PRICE_LIST_NO");

                double colListPrice = headset.getRow().getNumberValue("LIST_PRICE");
                if (isNaN(colListPrice))
                    colListPrice = 0;

                double colDiscount = headset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                buf = headset.getRow();

                if (colListPrice == 0)
                {
                    buf.setNumberValue("LIST_PRICE", baseUnitPrice); 
                    colListPrice = baseUnitPrice;
                }

                if (colDiscount == 0)
                {
                    buf.setNumberValue("DISCOUNT", discount); 
                    colDiscount = discount;
                }

                if (mgr.isEmpty(headset.getRow().getValue("PRICE_LIST_NO")))
                    buf.setValue("PRICE_LIST_NO", priceListNo);


                double qtyToInv = headset.getRow().getNumberValue("QTY_TO_INVOICE");
                if (isNaN(qtyToInv))
                    qtyToInv = 0;


                if (qtyToInv != 0)
                {
                    double nSalPriAmt = colListPrice * qtyToInv;
                    nSalPriAmt = nSalPriAmt - (colDiscount/100 * nSalPriAmt);

                    buf.setNumberValue("SALESPRICEAMOUNT", nSalPriAmt);
                }
                else
                    buf.setValue("SALESPRICEAMOUNT","");

                if (mgr.isEmpty(headset.getRow().getValue("PRICE_LIST_NO")) && !mgr.isEmpty(priceListNo))
                    buf.setValue("PRICE_LIST_NO", priceListNo);

                headset.setRow(buf);
                headset.next();
            }
            trans.clear();
        }
        if (headlay.isSingleLayout())
            headset.goTo(headCurrRow);
    }

    // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
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
    // 040210  ARWILK  End  (Enable Multirow RMB actions)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isFindLayout())
        {
            mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART",600,445);
            mgr.getASPField("CATALOG_NO").setLOVProperty("WHERE","CONTRACT IS NOT NULL");
        }

        setStringLables();

        mgr.getASPField("SIGNATURE").setDynamicLOV("EMPLOYEE_LOV",600,445);
        mgr.getASPField("SIGNATURE").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("ACCNT").setDynamicLOV("ACCOUNTING_CODEPART_A",600,445);
        mgr.getASPField("ACCNT").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("COST_CENTER").setDynamicLOV("CODE_B",600,445);
        mgr.getASPField("COST_CENTER").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_F").setDynamicLOV("CODE_F",600,445);
        mgr.getASPField("CODE_F").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("OBJECT_NO").setDynamicLOV("CODE_E",600,445);
        mgr.getASPField("OBJECT_NO").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("PROJECT_NO").setDynamicLOV("CODE_C",600,445);
        mgr.getASPField("PROJECT_NO").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_D").setDynamicLOV("CODE_D",600,445);
        mgr.getASPField("CODE_D").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_G").setDynamicLOV("CODE_G",600,445);
        mgr.getASPField("CODE_G").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_H").setDynamicLOV("CODE_H",600,445);
        mgr.getASPField("CODE_H").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_I").setDynamicLOV("CODE_I",600,445);
        mgr.getASPField("CODE_I").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        mgr.getASPField("CODE_J").setDynamicLOV("CODE_J",600,445);
        mgr.getASPField("CODE_J").setLOVProperty("WHERE","COMPANY= '" + companyvar + "'");

        lableingFields();

        if (!mgr.isEmpty(mform))
            backToForm = mform;
        else if (!mgr.isEmpty(mform1))
            backToForm = mform1;

        if (headset.countRows()>0)
        {
            String woCostTypeDb = headset.getRow().getValue("WORK_ORDER_COST_TYPE_DB");

            if ("Personal".equals(headset.getRow().getValue("WORK_ORDER_COST_TYPE")))
            {
                if (headlay.isSingleLayout())
                    headbar.disableCommand(headbar.EDITROW);
            }

            if (!mgr.isEmpty(headset.getRow().getValue("TRANSACTION_ID")))
            {
                if ("M".equals(woCostTypeDb))
                {
                    if (actEna0)
                        mgr.getASPField("TRANSACTION_ID").setHyperlink("../invenw/InventoryTransactionHist2Qry.page","SEARCH,TRANSACTION_ID","NEWWIN");
                }
                else if (("P".equals(woCostTypeDb)) || ("T".equals(woCostTypeDb)))
                    mgr.getASPField("TRANSACTION_ID").setHyperlink("../pcmw/WoTimeTransactionHist.page","TRANSACTION_ID","NEWWIN");
            }
        }

        if (headlay.isMultirowLayout())
            headbar.disableCommand(headbar.EDITROW);

        if (ctxIsAuthAllowed)
        {
            if (ctxHasUnauthorized)
            {
                headbar.enableCustomCommand("authorizeAllRows");
                headbar.enableCustomCommand("authorizeCorrect");
            }
            else
            {
                headbar.disableCustomCommand("authorizeAllRows");       
                headbar.disableCustomCommand("authorizeCorrect");
            }
        }
        else
        {
            headbar.disableCustomCommand("authorizeAllRows");
            headbar.disableCustomCommand("authorizeCorrect");   
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERCODING1TITLE: Postings";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERCODING1TITLE: Postings";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("RMBVALUE","");

        appendToHTML(headlay.show());

        appendDirtyJavaScript("window.name = \"WorkOrCoding1\";\n");

        appendDirtyJavaScript("function changeval()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" alert(\"");
        appendDirtyJavaScript(mgr.translate("PCMWWORKORDERCODING1USEPRICELOGICALT: Use Price Logic cannot be changed."));
        appendDirtyJavaScript("\");\n");
        appendDirtyJavaScript(" if (f.AGREEMENT_PRICE_FLAG.checked == false)\n");
        appendDirtyJavaScript("  f.AGREEMENT_PRICE_FLAG.click();\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("  f.AGREEMENT_PRICE_FLAG.checked = false;\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateCatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('CATALOG_NO',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('LINE_DESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("		getField_('AMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('LIST_PRICE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
        appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
        appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_COST_TYPE=' + SelectURLClientEncode('WORK_ORDER_COST_TYPE',i)		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ALTERNATIVE_CUSTOMER=' + URLClientEncode(getValue_('ALTERNATIVE_CUSTOMER',i))\n");
        appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('LINE_DESCRIPTION',i,0);\n");
        appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('DISCOUNT',i,4);\n");
        appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,5);\n");
        appendDirtyJavaScript("		assignValue_('QTY_TO_INVOICE',i,6);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePriceListNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPriceListNo(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('PRICE_LIST_NO',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('LIST_PRICE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
        appendDirtyJavaScript("		+ '&ALTERNATIVE_CUSTOMER=' + URLClientEncode(getValue_('ALTERNATIVE_CUSTOMER',i))\n");
        appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
        appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('DISCOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,2);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateListPrice(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkListPrice(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('LIST_PRICE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=LIST_PRICE'\n");
        appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'LIST_PRICE',i,'Sales Price') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateQtyToInvoice(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkQtyToInvoice(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('QTY_TO_INVOICE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=QTY_TO_INVOICE'\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'QTY_TO_INVOICE',i,'Qty To Invoice') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	validateCatalogNo(i)\n");
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
    }
}
