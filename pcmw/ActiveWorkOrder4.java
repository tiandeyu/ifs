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
*  File        : ActiveWorkOrder4.java 
*  Created     : ASP2JAVA Tool  010430  Created Using the ASP file ActiveWorkOrder4.asp
*  Modified    :
*  JEWILK  010430  Corrected conversion errors and done necessary changes.
*  CHCRLK  010613  Modified overwritten validations.
*  INROLK  010720  Changed function usePriceLogic(). call id 77811..
*  INROLK  010726  Changed validation of List Price and Qyantity to request.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)                           
*  JEWILK  011018  Changed lebel of field 'PRICEAMT'.
*  CHCRLK  011019  Performed Security Checking for actions.
* ---------------------------------------------------------------------------
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
* --------------------------------------------------------------------------
*  CHAMLK  031021  Modified function custOrder() to correct the redirected part to the CustomerOrder.page.
*  SAPRLK  031218  Web Alignment - removed methods clone() and doReset().
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  SAPRLK  040325  Merged with SP1.
*  ARWILK  041111  Replaced getContents with printContents.
*  NAMELK  041215  Discounted Sales Price column and logic added.
*  THWILK  060125  Corrected Localization errors.
*  NEKOLK  060309  Call 136780.Added cmd and function refreshForm()
*  NEKOLK  060309  Call 136784.modified run(), printContents().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged bug 58216.
*  BUNILK  070504  Implemented MTIS907 New Service Contract - Services changes.
*  AMDILK  070517  Call Id 144883: Fetch the value for the coordinator and customer id from the view
*  CHANLK  070523  Call 144904 Add custom function documents.
*  AMDILK  070530  Call Id 144903: Inserted a new field "Mch Code Contract" to the head block
*  ILSOLK  070709  Eliminated XSS.
*  CHANLK  071130  Bug 69047 Add create customer order validation.
*  NIJALK  071220  Bug 69819, Removed method calls to Customer_Agreement_API.Get_Description.
*  SHAFLK  080227  Bug 71766, Modified predefine(), createCustOrderLine(0 and creCustOrderLines().
*  HARPLK  090722  Bug 84436, Modified createCustOrderLine(),creCustOrderLines().
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ActiveWorkOrder4 extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrder4");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPForm frm;
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;

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

    private ASPField f;
    private ASPBlock b;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private boolean coflag;
    private boolean dlgFlag;
    private String str;
    private String qrystr;
    private String layout;
    private String hdrow;
    private int currrow;
    private String enable;
    private ASPCommand cmd;
    private ASPBuffer data;
    private String calling_url;
    private String current_url;
    private String orderno;
    private int currrowitem0;
    private String obj;
    private String strWoNos;
    private String srowno;
    private String sconf;
    private String ssite;
    private String ssalespart;
    private String slistprice;
    private boolean satis;
    private int n;
    private String wono;
    private String agrpriceflg;
    private ASPBuffer r;
    private boolean multirow;
    private boolean overview;
    private String head_command;
    private String item_command;
    private String title_;
    private String xx;
    private String yy; 
    private String sWOCostType;
    private String sWhereStatement;
    private String fmtdBuff;
    private String nDifference;
    private String sDifference;
    private String headSetRow;
    private String sCurrentWONo;
    private String sAuthCode;
    private String eventToPerform;
    //Variables for security checking of actions
    private boolean secCheck;                 
    private boolean ctxAddCoordinator;       
    private boolean ctxChangeCustomer;       
    private boolean ctxAddCusOrderType;   
    private boolean ctxCreateCustOrderLine;   
    private boolean ctxCreCustOrderLines;     
    private boolean ctxCustOrder;         
    private boolean ctxUsePriceLogic;        
    private boolean ctxDocumentTexts;        
    private boolean ctxItem0UndoTranToCustOrd;

    //Web Alignment - simplify code for RMBs
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private boolean bFirstRequest;
    private ASPBuffer buff;
    private ASPBuffer row;
    
    private boolean bHasContract;

    //

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveWorkOrder4(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();


        frm = mgr.getASPForm();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        coflag   = ctx.readFlag("COFLAG",false);
        dlgFlag = ctx.readFlag("DLGFLAG",false);
        sWOCostType = ctx.readValue("WOCOTYPE",sWOCostType);
        nDifference = ctx.readValue("NDIFF",nDifference);
        sWhereStatement = ctx.readValue("SWHERESTAT",sWhereStatement);
        sDifference = ctx.readValue("SDIFFERENCE",sDifference);
        headSetRow = ctx.readValue("HEADSETROW",headSetRow);
        sCurrentWONo = ctx.readValue("CURRWONO",sCurrentWONo);
        sAuthCode = ctx.readValue("AUTHCODE",sAuthCode);
        str = ctx.readValue("STR","");
        qrystr = ctx.readValue("QRYSTR",""); 
        //Variables for security checking of actions
        secCheck = ctx.readFlag("SECCHECK",secCheck);                                  
        ctxAddCoordinator = ctx.readFlag("CTXADDCOORDINATOR",ctxAddCoordinator);                
        ctxChangeCustomer = ctx.readFlag("CTXCHANGECUSTOMER",ctxChangeCustomer);                
        ctxAddCusOrderType = ctx.readFlag("CTXADDCUSORDERTYPE",ctxAddCusOrderType);              
        ctxCreateCustOrderLine = ctx.readFlag("CTXCREATECUSTORDERLINE",ctxCreateCustOrderLine);      
        ctxCreCustOrderLines = ctx.readFlag("CTXCRECUSTORDERLINES",ctxCreCustOrderLines);          
        ctxCustOrder = ctx.readFlag("CTXCUSTORDER",ctxCustOrder);                          
        ctxUsePriceLogic = ctx.readFlag("CTXUSEPRICELOGIC",ctxUsePriceLogic);                  
        ctxDocumentTexts = ctx.readFlag("CTXDOCUMENTTEXTS",ctxDocumentTexts);                  
        ctxItem0UndoTranToCustOrd = ctx.readFlag("CTXITEM0UNDOTRANTOCUSTORD",ctxItem0UndoTranToCustOrd);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());

        else if ("TRUE".equals(mgr.readValue("IGNORECONTRACT")))
           createOrderLine();
        else if ("TRUE".equals(mgr.readValue("IGNORECONTRACTALL")))
           creOrderLines();
        
        else if (!mgr.isEmpty(mgr.getQueryStringValue("POP_ITEM")))
        {
            mgr.setPageExpiring();
            trans.clear();
            layout = ctx.getGlobal("LAYOUT");
            hdrow = ctx.getGlobal("HDROW");
            headlay.setLayoutMode(toInt(layout));


            ctx.setGlobal("LAYOUT","");
            ctx.setGlobal("HDROW","");
            qrystr = "";

            okFind(); 
            headset.goTo(toInt(hdrow));
            trans.clear();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            mgr.setPageExpiring();
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.dataTransfered())
        {
            
                buff = mgr.getTransferedData();
	        row = buff.getBufferAt(0); 
	       	sCurrentWONo = row.getValue("CURRWONO");
                           
                okFind();
                bFirstRequest = true;

        }
        else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) ||!mgr.isExplorer())
        {
            
            okFind();
        }
        else 
         bFirstRequest = true;

        adjust(); 

        ctx.writeFlag("COFLAG",coflag);
        ctx.writeFlag("DLGFLAG",dlgFlag);
        //ctx.writeFlag("ORDERFLAG",orderFlag);

        ctx.writeValue("WOCOTYPE",sWOCostType);
        ctx.writeValue("NDIFF",nDifference);
        ctx.writeValue("SWHERESTAT",sWhereStatement);
        ctx.writeValue("SDIFFERENCE",sDifference);
        ctx.writeValue("HEADSETROW",headSetRow);
        ctx.writeValue("CURRWONO",sCurrentWONo);
        ctx.writeValue("AUTHCODE",sAuthCode);
        ctx.writeValue("COORDINATE",sCurrentWONo);
        ctx.writeValue("STR",str);
        ctx.writeValue("QRYSTR",qrystr); 
        //Variables for security checking of actions
        ctx.writeFlag("SECCHECK",secCheck);                                  
        ctx.writeFlag("CTXADDCOORDINATOR",ctxAddCoordinator);                
        ctx.writeFlag("CTXCHANGECUSTOMER",ctxChangeCustomer);                
        ctx.writeFlag("CTXADDCUSORDERTYPE",ctxAddCusOrderType);              
        ctx.writeFlag("CTXCREATECUSTORDERLINE",ctxCreateCustOrderLine);      
        ctx.writeFlag("CTXCRECUSTORDERLINES",ctxCreCustOrderLines);          
        ctx.writeFlag("CTXCUSTORDER",ctxCustOrder);                          
        ctx.writeFlag("CTXUSEPRICELOGIC",ctxUsePriceLogic);                  
        ctx.writeFlag("CTXDOCUMENTTEXTS",ctxDocumentTexts);                  
        ctx.writeFlag("CTXITEM0UNDOTRANTOCUSTORD",ctxItem0UndoTranToCustOrd);
    }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

    public void perform(String command)
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.markSelectedRows(command);
        }
        else
            headset.markRow(command);

        currrow = headset.getCurrentRowNo();
        mgr.submit(trans);

        if (!headlay.isMultirowLayout() && (headset.countSelectedRows()>0))
            headset.setFilterOn();

        headset.goTo(currrow);

    }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM-ITEM FUNCTION  -----------------------
//-----------------------------------------------------------------------------

    public void performItem( String command)
    {
        ASPManager mgr = getASPManager();


        itemset0.markRow(command);

        currrow = headset.getCurrentRowNo();
        mgr.submit(trans);

        headset.goTo(currrow);
    }


    public int  findObjEvents(String objid, String comm) 
    {
        ASPManager mgr = getASPManager();

        ASPQuery eventQ = trans.addQuery("GETEVENT","SELECT OBJEVENTS FROM WORK_ORDER_CODING WHERE OBJID = ?");
        eventQ.addParameter("OBJID",objid);
        trans = mgr.perform(trans);

        ASPBuffer getEvent = trans.getBuffer("GETEVENT/DATA");
        String newObjEvents = getEvent.getValue("OBJEVENTS");

        trans.clear();

        enable = "^"; 
        enable = "^"+newObjEvents;

        eventToPerform = "^"+comm+"^";       
        if (enable.indexOf(eventToPerform)<=-1)
        {
            return 0;
        }

        return 1;

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        double lipri;
        double qtyamt;
        double pric;
        double discpric;
        double qty;
        double disc;
        String agre;
        String strPric = "";
        String strDiscpric = "";
        String strqtyamt = "";

        String val = mgr.readValue("VALIDATE");

        if ("LIST_PRICE".equals(val))
        {
            lipri = mgr.readNumberValue("LIST_PRICE");
            qtyamt = mgr.readNumberValue("QTY_TO_INVOICE");
            disc = mgr.readNumberValue("DISCOUNT");

            if (isNaN(disc))
               disc = 0;

            discpric = lipri - (lipri*disc/100);
            pric = discpric * qtyamt; 
            agre = "0";

            strPric = "";
            strDiscpric = "";

            if (!isNaN(pric))
                strPric = mgr.getASPField("PRICEAMT").formatNumber(pric);

            if (!isNaN(discpric))
                strDiscpric = mgr.getASPField("DISCPRICEAMT").formatNumber(discpric);

            String txt = strPric +"^"+ agre +"^"+ strDiscpric +"^";

            mgr.responseWrite(txt);
        }
        else if ("QTY_TO_INVOICE".equals(val))
        {
            //lipri = mgr.readNumberValue("LIST_PRICE");
            discpric = mgr.readNumberValue("DISCPRICEAMT");
            qtyamt = mgr.readNumberValue("QTY_TO_INVOICE");
            qty = mgr.readNumberValue("QTY");

            if (isNaN(qtyamt) || qtyamt == 0)
            {
                qtyamt = qty;
            }

            pric = discpric * qtyamt;
            strPric = "";
            
            if (!isNaN(pric))
                strPric = mgr.getASPField("PRICEAMT").formatNumber(pric);

            if (!isNaN(qtyamt))
                strqtyamt = mgr.getASPField("QTY_TO_INVOICE").formatNumber(qtyamt);

            String txt = strqtyamt + "^" + strPric + "^";

            mgr.responseWrite(txt);
        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();
        ASPQuery q = trans.addQuery(headblk);
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );

        q = trans.addEmptyQuery(itemblk0);
        q.addMasterConnection("HEAD","WO_NO","ITEM0_WO_NO");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0) AND NOT( (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_BOOK_STATUS IS NULL ) OR  (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(2) AND  WORK_ORDER_BOOK_STATUS IS NULL ) OR  (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(3)  AND  WORK_ORDER_BOOK_STATUS IS NULL ) OR (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(4) AND  WORK_ORDER_BOOK_STATUS IS NULL ) )");
        q.includeMeta("ALL");

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NODATA: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        sCurrentWONo = headset.getRow().getValue("WO_NO");
        qrystr = mgr.createSearchURL(headblk);
    }


    public void countFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");


        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();

    }


    public void newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","ACTIVE_WORK_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");

        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH ITEM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();


        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0) AND NOT( (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_BOOK_STATUS IS NULL ) OR  (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(2) AND  WORK_ORDER_BOOK_STATUS IS NULL ) OR  (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(3)  AND  WORK_ORDER_BOOK_STATUS IS NULL ) OR (WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(4) AND  WORK_ORDER_BOOK_STATUS IS NULL ) )");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        q.includeMeta("ALL");
        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {

            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NODATA: No data found."));
                itemset0.clear();
            }
        }
        headset.goTo(headrowno);

    }


    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        str = "";
        mgr.showAlert("PCMWACTIVEWORKORDER4NORMB: No RMB has been selected");
    }


    public void addCoordinator()
    {
        ASPManager mgr = getASPManager();

        if (!ConnectedToCO())
        {
            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());

            headset.storeSelections();   

            calling_url=mgr.getURL();
            ctx.setGlobal("CALLING_URL",calling_url);


            sCurrentWONo = headset.getRow().getValue("WO_NO");
            sAuthCode = headset.getRow().getValue("AUTH_CODE");

            //coordinate =true;

            //Web Alignment - simplify code for  RMBs
            bOpenNewWindow = true; 
            urlString = "ActiveWorkOrderDlg1.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                        "&AUTHORIZE_CODE="+mgr.URLEncode(headset.getRow().getValue("AUTH_CODE"))+
                        "&QRYSTR="+mgr.URLEncode(qrystr); 
            newWinHandle = "WorkOrderDlg"; 
            //

            ctx.setGlobal("LAYOUT",String.valueOf(headlay.getLayoutMode()));
            ctx.setGlobal("HDROW",String.valueOf(headset.getCurrentRowNo()));

            qrystr = replaceString(qrystr, "?SEARCH", "?POP_ITEM");

            str = "";   
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4CANNOTADDCOORD: Can not perform 'Add Coordinator to Work Order' for this line"));         
        }
        ctx.setCookie("NewWinOpen","true");
    }

    public void refreshForm()
    {
        ASPManager mgr = getASPManager();
        headset.refreshRow();
    }


    private boolean ConnectedToCO()
    {
        ASPManager mgr = getASPManager();

        int nCountRows = itemset0.countRows();
        itemset0.first();

        for (int nIndex=0; nIndex<nCountRows; nIndex++)
        {
            String colOrderNo = itemset0.getValue("ORDER_NO");
            if (! mgr.isEmpty(colOrderNo))
                return true;

            itemset0.next();
        }
        return false;
    }


    public void changeCustomer()
    {
        ASPManager mgr = getASPManager();

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        ASPBuffer buffer = mgr.newASPBuffer();
        ASPBuffer row = buffer.addBuffer("0");
        row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
        row = buffer.addBuffer("1");
        row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        row = buffer.addBuffer("2");
        row.addItem("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
        row = buffer.addBuffer("3");
        row.addItem("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
        row = buffer.addBuffer("4");
        row.addItem("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
        row = buffer.addBuffer("5");
        row.addItem("CONTRACT",headset.getRow().getValue("CONTRACT"));
        row = buffer.addBuffer("6");
	row.addItem("MCH_CODE_CONTRACT", headset.getRow().getValue("MCH_CODE_CONTRACT"));

        mgr.transferDataTo("ActiveWorkOrderDlg.page",buffer);
        str = "";   
    }


    public void addCusOrderType()
    {
        ASPManager mgr = getASPManager();


        //Web Alignment - simplify code for  RMBs
        bOpenNewWindow = true; 
        urlString = "OrderTypeDlg.page?WO_NO1=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
        newWinHandle = "OrderTypeDlg"; 
        //
        ctx.setCookie("NewWinOpen","true");
    }


    public void custOrder()
    {
        ASPManager mgr = getASPManager();


        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        currrow = itemset0.getCurrentRowNo();

        if (mgr.isEmpty(itemset0.getRow().getValue("ORDER_NO")))
            mgr.showAlert("PCMWACTIVEWORKORDER4CANNOTCUSTORDER: Cannot perform for selected line(s)");
        else
        {
            orderno = itemset0.getRow().getValue("ORDER_NO");
            mgr.redirectTo("../orderw/CustomerOrder.page?ORDER_NO="+mgr.URLEncode(orderno));

        }

        str = "";
    }


    public void createCustOrderLine()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        currrowitem0 = itemset0.getCurrentRowNo();     
        String sCurrrowitem0 = String.valueOf(currrowitem0);
        ctx.setGlobal("GLOBALCURRENTROW", sCurrrowitem0);

        bHasContract = false;

        if (mgr.isEmpty(headset.getRow().getValue("CONTRACT_ID"))){

           trans.clear();
           //Bug 84436,Start
           if(mgr.isModuleInstalled("PCMSCI"))
           {
           cmd = trans.addCustomCommand("GETHASVALCONTR","Psc_Contr_Product_Api.Has_Valid_Contract");
           cmd.addParameter("HAS_CONTRACT");
           cmd.addParameter("CONNECTION_TYPE_DB",headset.getRow().getValue("CONNECTION_TYPE_DB"));
           cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
           cmd.addParameter("MCH_CODE_CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
           cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
           cmd.addParameter("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
           cmd.addParameter("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE"));
           }
           //Bug 84436,End
           trans = mgr.perform(trans);
           String sHasContract = trans.getValue("GETHASVALCONTR/DATA/HAS_CONTRACT");
           trans.clear();
    
           if ("TRUE".equals(sHasContract)){
              ctx.setCookie( "PageID_my_cookie", "TRUE" );
              bHasContract = true;
           }
           else{
              createOrderLine();
           }
        }
        else{
           createOrderLine();
        }
    }

    
    private void createOrderLine(){
       ASPManager mgr = getASPManager();

       String sCurrrowitem0 = ctx.getGlobal("GLOBALCURRENTROW");
       currrowitem0 = Integer.parseInt(sCurrrowitem0);
       
       itemset0.goTo(currrowitem0);
       
       obj = itemset0.getRow().getFieldValue("ITEM0_OBJEVENTS");
       strWoNos = itemset0.getRow().getFieldValue("ITEM0_WO_NO");
       srowno = itemset0.getRow().getFieldValue("ROW_NO");
       sconf = itemset0.getRow().getFieldValue("CONFIRMED");
       ssite = itemset0.getRow().getFieldValue("ITEM0_CONTRACT");
       ssalespart = itemset0.getRow().getFieldValue("CATALOG_NO");
       slistprice = itemset0.getRow().getFieldValue("LIST_PRICE");

       satis = true;

       obj = "^"+obj;

       if (obj.indexOf("^Transfer^")<0)
       {
           satis = false;
           mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4CRCUSORDLIN: Cannot perform Create Customer Order Line on line."));
       }
       else
       {
           performItem("TRANSFER__");                    
           okFindITEM0();
           itemset0.goTo(currrowitem0);
       } 
  	 
    }

    public void creCustOrderLines()
    {
        ASPManager mgr = getASPManager();

        bHasContract = false;
        if (mgr.isEmpty(headset.getRow().getValue("CONTRACT_ID"))){
 
           trans.clear();
           //Bug 84436,Start
           if(mgr.isModuleInstalled("PCMSCI"))
           {                   
           cmd = trans.addCustomCommand("GETHASVALCONTR","Psc_Contr_Product_Api.Has_Valid_Contract");
           cmd.addParameter("HAS_CONTRACT");
           cmd.addParameter("CONNECTION_TYPE_DB",headset.getRow().getValue("CONNECTION_TYPE_DB"));
           cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
           cmd.addParameter("MCH_CODE_CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
           cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
           cmd.addParameter("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
           cmd.addParameter("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE"));
           }
           //Bug 84436,End
           trans = mgr.perform(trans);
           String sHasContract = trans.getValue("GETHASVALCONTR/DATA/HAS_CONTRACT");
           trans.clear();
    
           if ("TRUE".equals(sHasContract)){
              ctx.setCookie( "PageID_my_cookie2", "TRUE" );
              bHasContract = true;
           }
           else
              creOrderLines();
        }
        else
           creOrderLines();
    }
    
    private void creOrderLines() {
       ASPManager mgr = getASPManager();
//       currrowitem0 = itemset0.getCurrentRowNo();    
//       n = itemset0.countRows();

       cmd = trans.addCustomCommand("GETCOLINES","WORK_ORDER_CODING_UTILITY_API.Create_Customer_Order_Inv");
       cmd.addParameter("TRANSFERED_LINES");
       cmd.addParameter("UNTRANSFERED_ROWS");
       cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

       trans = mgr.perform(trans);

       okFindITEM0();
      
    }


    public void usePriceLogic()
    {
        ASPManager mgr = getASPManager();
        String listp;
        double lipri;
        
        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
        testbuff.addSecurityQuery("Sales_Part_API","Get_Cost");

        mgr.perform(testbuff);

        ASPBuffer sec = testbuff.getSecurityInfo();
        if (sec.itemExists("Sales_Part_API.Get_Cost"))
        {

            wono = itemset0.getRow().getFieldValue("ITEM0_WO_NO");
            String objev = "^"+itemset0.getRow().getFieldValue("ITEM0_OBJEVENTS");
            agrpriceflg = itemset0.getRow().getValue("AGREEMENT_PRICE_FLAG");

            if ((! mgr.isEmpty(wono) ) &&  ( objev.indexOf("^Transfer^",0)>=0 ) &&  ( "0".equals(agrpriceflg) ))
            {
                String custno_ = itemset0.getRow().getValue("ALTERNATIVE_CUSTOMER");
                if (mgr.isEmpty(custno_))
                    custno_ = headset.getRow().getValue("CUSTOMER_NO");

                cmd = trans.addCustomFunction("GETLP","Sales_Part_API.Get_List_Price","LIST_PRICE");
                cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("CONTRACT"));
                cmd.addParameter("CATALOG_NO",itemset0.getRow().getValue("CATALOG_NO"));

                if (!mgr.isEmpty(itemset0.getRow().getValue("CATALOG_NO")))
                {
                    cmd = trans.addCustomCommand("NEWLP","Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_PRICE",itemset0.getRow().getValue("BASE_PRICE"));
                    cmd.addParameter("SALE_PRICE",itemset0.getRow().getValue("SALE_PRICE"));
                    cmd.addParameter("DISCOUNT",itemset0.getRow().getValue("DISCOUNT"));
                    cmd.addParameter("CURRENCY_RATE",itemset0.getRow().getValue("CURRENCY_RATE"));
                    cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("CONTRACT"));
                    cmd.addParameter("CATALOG_NO",itemset0.getRow().getValue("CATALOG_NO"));
                    cmd.addParameter("CUSTOMER_NO",custno_);
                    cmd.addParameter("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
                    cmd.addParameter("PRICE_LIST_NO",itemset0.getRow().getValue("PRICE_LIST_NO"));
                    cmd.addParameter("QTY_TO_INVOICE",itemset0.getRow().getValue("QTY_TO_INVOICE"));
                }
                trans = mgr.perform(trans);


                if (!mgr.isEmpty(itemset0.getRow().getValue("CATALOG_NO")))
                {
                    lipri = trans.getNumberValue("NEWLP/DATA/SALE_PRICE");
                }
                else
                {
                    lipri = trans.getNumberValue("GETLP/DATA/LIST_PRICE");
                }

                double qtyamt = itemset0.getRow().getNumberValue("QTY_TO_INVOICE");
                double discnt = itemset0.getRow().getNumberValue("DISCOUNT");

                if (isNaN(discnt))
                   discnt = 0;

                double discpric = lipri - (lipri*discnt/100);
                double pric = discpric * qtyamt;

                r = itemset0.getRow();
                r.setNumberValue("LIST_PRICE",lipri);
                r.setValue("AGREEMENT_PRICE_FLAG","1");
                r.setNumberValue("PRICEAMT",pric);  
                r.setValue("ORDER_NO","");
                r.setValue("LINE_NO","");
                r.setNumberValue("DISCPRICEAMT",discpric);
                itemset0.setRow(r);
                itemset0.unselectRows();

                itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);

            }
            else
                mgr.showAlert("PCMWACTIVEWORKORDER4NOUSEPRICE: Cannot perform Use Price Logic in Customer Order on this line.");

        }
        else
            mgr.showAlert("PCMWACTIVEWORKORDER4NOUSEPRICE: Cannot perform Use Price Logic in Customer Order on this line.");
    }

    public void documentTexts()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        currrow = itemset0.getCurrentRowNo();

        String noteid = itemset0.getRow().getValue("NOTE_ID");
        String obev = "^"+itemset0.getRow().getFieldValue("ITEM0_OBJEVENTS");

        if ((!mgr.isEmpty(noteid)) && (obev.indexOf("^Transfer^",0)>=0))
        {
            current_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL",current_url);

            ASPBuffer buffer=mgr.newASPBuffer();
            ASPBuffer row=buffer.addBuffer("0");
            row.addItem("WO_NO",itemset0.getRow().getFieldValue("ITEM0_WO_NO"));
            row=buffer.addBuffer("1");
            row.addItem("CATALOG_NO",itemset0.getRow().getValue("CATALOG_NO"));
            row=buffer.addBuffer("2");
            row.addItem("NOTE_ID",itemset0.getRow().getValue("NOTE_ID"));

            mgr.transferDataTo("../mpccow/DocumentTextDlg.page",buffer);
        }
        else
        {
            mgr.showAlert("PCMWACTIVEWORKORDER4CANNOT: Cannot perform on this line.");
        }

        itemset0.goTo(currrow);
        str = "";
    }


    public void item0Release()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        String objId = itemset0.getValue("OBJID");

        if (findObjEvents(objId, "Release") == 0)
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NORELEASE: Cannot perform 'Release' on the selected line."));
        else
            performItem("RELEASE__");

        str = "";   
    }


    public void item0Hold()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        String objId = itemset0.getValue("OBJID");

        if (findObjEvents(objId, "Hold") == 0)
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NOHOLD: Cannot perform 'Hold' on the selected line."));
        else
            performItem("HOLD__");

        str = "";
    }


    public void item0ToBeNotInvoiced()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        String objId = itemset0.getValue("OBJID");

        if (findObjEvents(objId, "ToBeNotInvoiced") == 0)
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NONOINVO: Cannot perform 'To Not Invoiceble' on the selected line."));
        else
            performItem("TO_BE_NOT_INVOICED__");

        str = "";
    }


    public void item0ToBeInvoiced()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        String objId = itemset0.getValue("OBJID");

        if (findObjEvents(objId, "ToBeInvoiced") == 0)
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4NOTOINVOI: Cannot perform 'To Invoiceble' on the selected line."));
        else
            performItem("TO_BE_INVOICED__");

        str = "";
    }


    public void item0UndoTranToCustOrd()
    {
        ASPManager mgr = getASPManager();

        currrow = itemset0.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        String colOrderNo = itemset0.getValue("ORDER_NO");
        String colLineNo = itemset0.getValue("LINE_NO");
        String objId = itemset0.getValue("OBJID");
        int undoTransfer = findObjEvents(objId, "UndoTransfer");

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery("Customer_Order_Line_API","Remove");

        mgr.perform(secBuff);

        ASPBuffer secInfo = secBuff.getSecurityInfo();

        if ((!mgr.isEmpty(colOrderNo)) && (!mgr.isEmpty(colLineNo)) && (undoTransfer==1) && (secInfo.itemExists("Customer_Order_Line_API.Remove")))
        {
            performItem("Undo_Transfer__");

            trans.clear();

            okFindITEM0();

            itemset0.goTo(currrow);
            str = "";
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER4UNDOTRANSFER: Cannot perform 'Undo Transfer to Customer Order' on the selected line."));
    }


    public String  getEvent( String item_command) 
    {
        if ("item0Release".equals(item_command))
            return "Release";

        if ("item0Hold".equals(item_command))
            return "Hold";

        if ("item0ToBeNotInvoiced".equals(item_command))
            return "ToBeNotInvoiced";

        if ("item0ToBeInvoiced".equals(item_command))
            return "ToBeInvoiced";

        return "";
    }
    public void documents()
    {
       ASPManager mgr = getASPManager();
//       mgr. redirectTo("../docmaw/DocReference.page?VIEW="+mgr.URLEncode("ACTIVE_SEPARATE")+"&OBJID="+mgr.URLEncode(headset.getRow().getValue("OBJID")));

       bOpenNewWindow = true;
       urlString = "../docmaw/DocReference.page?VIEW="+mgr.URLEncode("ACTIVE_SEPARATE")+"&OBJID="+mgr.URLEncode(headset.getRow().getValue("OBJID"));
       newWinHandle = "DocReference"; 
       ctx.setCookie("NewWinOpen","true");
    }

//-----------------------------------------------------------------------------
//-------------------------  OVERVIEWMODE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void overviewMode()
    {
        multirow = true;
        overview = true;
        headtbl.clearQueryRow();
        headset.setFilterOff();
        headset.unselectRows();
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");
        headblk.disableDocMan();

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("OBJSTATE");
        f.setHidden();

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("WO_NO");
        f.setSize(14);
        f.setDynamicLOV("ACTIVE_WORK_ORDER",600,445);
        f.setLabel("PCMWACTIVEWORKORDER4WONO: WO No");
        f.setReadOnly();

        f = headblk.addField("ERR_DESCR");
        f.setSize(30);
        f.setLabel("PCMWACTIVEWORKORDER4SHODESCR: Short Description");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("STATE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4STATE: Status");
        f.setReadOnly();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(14);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        //f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
        f.setLabel("PCMWACTIVEWORKORDER4CUSTNO: Customer No");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("PARTYTYPECUSTOMERNAME");
        f.setSize(30);
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REFERENCE_NO");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4REFERENCE_NO: Reference No");
        f.setReadOnly();

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(14);
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
        f.setLabel("PCMWACTIVEWORKORDER4COOR: Coordinator");
        //f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("COORDINATORINFO");
        f.setSize(30);
        f.setFunction("Order_Coordinator_API.Get_Name(:AUTHORIZE_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CONTRACT_ID");
        f.setUpperCase();
        f.setMaxLength(15);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER4CONTRACTID: Contract ID");
        f.setSize(15);
        
        f = headblk.addField("SRV_LINE_NO","Number");
        f.setDbName("LINE_NO");
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER4LINENO: Line No");
        f.setSize(10);                                    

        //Bug 69819, Start, Modified ASPFields
        f = headblk.addField("AGREEMENT_ID");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4AGREEID: Agreement ID");
        f.setUpperCase();

        f = headblk.addField("CUSTOMERAGREEMENTDESCRIPTION");
        f.setSize(20);
        f.setFunction("''");
        f.setHidden();
        //Bug 69819, End

        f = headblk.addField("MCH_CODE");
        f.setSize(14);
        f.setMaxLength(100);
        f.setLabel("PCMWACTIVEWORKORDER4MCH_CODE: Object ID");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(14);
        f.setMaxLength(100);
        f.setHidden();

        f = headblk.addField("MCHNAME");
        f.setSize(30);
        f.setLabel("PCMWACTIVEWORKORDER4MCHNAME: Object Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(14);
        f.setDynamicLOV("WORK_TYPE",600,445);
        f.setLabel("PCMWACTIVEWORKORDER4WORK_TYPE_ID: Work Type ID");
        f.setUpperCase();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CONTRACT");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4CONTRACT: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("CUST_ORDER_TYPE");
        f.setSize(8);
        f.setLabel("PCMWACTIVEWORKORDER4CUSTORTYPE: Customer Order Type");
        f.setUpperCase();
        f.setReadOnly();

        headblk.addField("CONNECTION_TYPE_DB").
        setHidden().
        setFunction("ACTIVE_SEPARATE_API.Get_Connection_Type(:WO_NO)");

        headblk.addField("HAS_CONTRACT").
        setHidden().
        setFunction("''");

        headblk.addField("PLAN_F_DATE","Datetime").
        setReadOnly().
        setHidden();
        
//        //Bug 69819, Start, Added missing parameter to function call
////      Bug 69047, Start
//        headblk.addField("HAS_CONTRACT").
//        setHidden().
//        setFunction("Psc_Contr_Product_Api.Has_Valid_Contract(ACTIVE_SEPARATE_API.Get_Connection_Type(:WO_NO),:MCH_CODE,:MCH_CODE_CONTRACT,:CUSTOMER_NO,:WORK_TYPE_ID,SYSDATE)");
////      Bug 69047, End
//        //Bug 69819, End

        headblk.setView("ACTIVE_WORK_ORDER");
        headblk.defineCommand("ACTIVE_WORK_ORDER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVEWORKORDER4HD: Transfer of Work Order Lines to Customer Order"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);

        headbar.disableRowStatus();
        headbar.addCustomCommand("documents",mgr.translate("PCMWACTIVEWORKORDER4DOCUMENTS: Documents"));
        headbar.addCustomCommand("none","");
        headbar.addCustomCommand("addCoordinator",mgr.translate("PCMWACTIVEWORKORDER4ADDCOORTOWORKORDER: Add Coordinator to Work Order..."));
        headbar.addCustomCommand("changeCustomer",mgr.translate("PCMWACTIVEWORKORDER4CHACUST: Change Customer..."));
        headbar.addCustomCommand("addCusOrderType",mgr.translate("PCMWACTIVEWORKORDER4ADDCUSORTY: Add Customer Order Type to Work Order..."));

        headbar.addCustomCommand("refreshForm","");
        headbar.addCommandValidConditions("addCusOrderType","WO_NO","Disable","null");
        headbar.addCommandValidConditions("changeCustomer","MCH_CODE","Disable","null");
        headbar.addCommandValidConditions("changeCustomer","WORK_TYPE_ID","Disable","null");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        //Bug 69819, Start, Modified field order
        headlay.setFieldOrder("WO_NO,ERR_DESCR,STATE,CUSTOMER_NO,PARTYTYPECUSTOMERNAME,REFERENCE_NO,AUTH_CODE,COORDINATORINFO,MCH_CODE,MCHNAME,WORK_TYPE_ID");
        //Bug 69819, End

        headlay.setDialogColumns(2);

        headlay.setSimple("ERR_DESCR");
        headlay.setSimple("PARTYTYPECUSTOMERNAME");
        headlay.setSimple("COORDINATORINFO");
        headlay.setSimple("CUSTOMERAGREEMENTDESCRIPTION");
        headlay.setSimple("MCHNAME");

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_WO_NO","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4ITEM0_WO_NO: WO Number");
        f.setDbName("WO_NO");


        f = itemblk0.addField("ROW_NO","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4ROW_NO: Row No");

        f = itemblk0.addField("WORK_ORDER_COST_TYPE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4COSTYPE: Cost Type");
        f.setReadOnly();

        f = itemblk0.addField("QTY","Number");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4QTY: Qty");
        f.setReadOnly();

        f = itemblk0.addField("QTY_TO_INVOICE","Number");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4QTYTOINVOICE: Qty to Invoice");
        f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE,QTY,DISCPRICEAMT","QTY_TO_INVOICE,PRICEAMT");

        f = itemblk0.addField("CONFIRMED");
        f.setLabel("PCMWACTIVEWORKORDER4CONFIRMED: Confirmed");
        f.setCheckBox("0,1");
        f.setHidden();
        f.setFunction("'0'");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4ITEM0_CONTRACT: Site");
        f.setDbName("CONTRACT");
        f.setUpperCase();

        f = itemblk0.addField("CATALOG_NO");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4CATALOG_NO: Sales Part");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("SALESPARTCATALOG_DESC");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4SALESPARTCATALOG_DESC: Description");
        f.setReadOnly();
        f.setFunction("NVL(LINE_DESCRIPTION,SALES_PART_API.Get_Catalog_Desc(contract,CATALOG_NO))");
        mgr.getASPField("CATALOG_NO").setValidation("SALESPARTCATALOG_DESC");

        f = itemblk0.addField("PRICE_LIST_NO");
        f.setSize(14);
        f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWACTIVEWORKORDER4PRICE_LIST_NO: Price List No");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("AMOUNT","Number");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWACTIVEWORKORDER4AMOUNT: Cost Amount");
        f.setReadOnly();

        f = itemblk0.addField("LIST_PRICE","Number");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4PRICE: Price");
        f.setCustomValidation("LIST_PRICE,QTY_TO_INVOICE,AGREEMENT_PRICE_FLAG,DISCOUNT","PRICEAMT,AGREEMENT_PRICE_FLAG,DISCPRICEAMT");
        f.setMaxLength(10);

        f = itemblk0.addField("DISCOUNT","Number");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4DISCOUN: Discount");
        f.setReadOnly();   

        f = itemblk0.addField("EDIT_FLG");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("DISCPRICEAMT","Number");
        f.setSize(14);
        f.setFunction("(LIST_PRICE - (NVL(DISCOUNT, 0)/100 * LIST_PRICE)) ");
        f.setLabel("PCMWACTIVEWORKORDER4DISCSALESPRICE: Discounted Sales Price");
        f.setReadOnly();

        f = itemblk0.addField("PRICEAMT","Number");
        f.setSize(14);
        f.setFunction("(LIST_PRICE - (NVL(DISCOUNT, 0)/100 * LIST_PRICE)) * QTY_TO_INVOICE");
        f.setLabel("PCMWACTIVEWORKORDER4PRICEAMT: Sales Price Amount");
        f.setReadOnly();

        f = itemblk0.addField("AGREEMENT_PRICE_FLAG","Number");
        f.setSize(20);
        f.setLabel("PCMWACTIVEWORKORDER4USECUSTAGREE: Use Cust. Agreement");
        f.setCheckBox("0,1");

        f = itemblk0.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4KEEPREVENUE: Keep Revenue");
        f.setSelectBox();
        f.enumerateValues("KEEP_REVENUE_API");

        f = itemblk0.addField("ITEM0_STATE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4STATUS: Status");
        f.setDbName("STATE");
        f.setReadOnly();

        f = itemblk0.addField("CDOCUMENTTEXT");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4DOCUTEXT: Document Text");
        f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
        f.setReadOnly();
        f.setCheckBox("0,1");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ORDER_NO");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4ORDNO: Order No");
        f.setUpperCase();
        f.setMaxLength(12);
        f.setDefaultNotVisible();

        f = itemblk0.addField("LINE_NO");
        f.setSize(14);
        f.setLabel("PCMWACTIVEWORKORDER4LINENO: Line No");
        f.setMaxLength(12);
        f.setDefaultNotVisible();

        f = itemblk0.addField("ALTERNATIVE_CUSTOMER");
        f.setSize(14);
        f.setDynamicLOV("CUSTOMER_INFO",600,450);
        f.setLabel("PCMWACTIVEWORKORDER4ALTERNATIVECUSTOMER: Alt Customer");
        f.setMaxLength(12);
        f.setDefaultNotVisible();

        f = itemblk0.addField("NOTE_ID","Number");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVEWORKORDER4NOTEID: Note ID");

        f = itemblk0.addField("ISFIXEDPRICE");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("TEST_CLIENT_VALUE");
        f.setHidden();
        f.setFunction("''");   

        f = itemblk0.addField("BASE_PRICE","Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("SALE_PRICE","Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("CURRENCY_RATE","Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("CAL_LIST");
        f.setLabel("PCMWACTIVEWORKORDER4CALLITS: Calculated List");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("DIFFERENCE");
        f.setLabel("PCMWACTIVEWORKORDER4DIFFERENCE: Difference");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("BY_QTY_DATE","Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("QSTR");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("UNTRANSFERED_ROWS");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("TRANSFERED_LINES");
        f.setHidden();
        f.setFunction("''");

        itemblk0.setView("WORK_ORDER_CODING");
        itemblk0.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__,RELEASE__,HOLD__,TO_BE_NOT_INVOICED__,TO_BE_INVOICED__,Undo_Transfer__,TRANSFER__");
        itemset0 = itemblk0.getASPRowSet();

        itemblk0.setMasterBlock(headblk);
        itemblk0.setTitle(mgr.translate("PCMWACTIVEWORKORDER4BLK0TITLE: General"));
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);  

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWACTIVEWORKORDER4ITM0: General"));
        itemtbl0.setWrap();
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE); 

        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");


        itembar0.addCustomCommand("none","");
        itembar0.addCustomCommand("createCustOrderLine",mgr.translate("PCMWACTIVEWORKORDER4CREACUSTORDELINE: Create Customer Order Line..."));
        itembar0.addCustomCommand("creCustOrderLines",mgr.translate("PCMWACTIVEWORKORDER4CREACUSTORDELINES: Create CO line for all records..."));

        if (mgr.isPresentationObjectInstalled("ORDERW/CustomerOrder.page"))
            itembar0.addCustomCommand("custOrder",mgr.translate("PCMWACTIVEWORKORDER4CUSTORDER: Customer Order..."));

        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("usePriceLogic",mgr.translate("PCMWACTIVEWORKORDER4USEPRILOGINCUSTORD: Use Price Logic in Customer Order"));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("documentTexts",mgr.translate("PCMWACTIVEWORKORDER4DOCTEXTS: Document Texts"));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("item0Release",mgr.translate("PCMWACTIVEWORKORDER4RELEASE: Release"));
        itembar0.addCustomCommand("item0Hold",mgr.translate("PCMWACTIVEWORKORDER4HOLD: Hold"));
        itembar0.addCustomCommand("item0ToBeNotInvoiced",mgr.translate("PCMWACTIVEWORKORDER4TONOTINVO: To Not Invoiceble"));
        itembar0.addCustomCommand("item0ToBeInvoiced",mgr.translate("PCMWACTIVEWORKORDER4TOINV: To Invoiceble"));
        itembar0.addCustomCommand("item0UndoTranToCustOrd",mgr.translate("PCMWACTIVEWORKORDER4UNTRACUSORD: Undo Transfer to Customer Order"));

        itembar0.addCommandValidConditions("creCustOrderLines","OBJSTATE","Disable","Transferred");
        
        //itembar0.defineCommand("createCustOrderLine","createCustOrderLine","createCustOrderLineClient(-1)");
        
        b = mgr.newASPBlock("WORK_ORDER_COST_TYPE");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("KEEP_REVENUE");

        b.addField("CLIENT_VALUES1");
        head_command = headbar.getSelectedCustomCommand();
        item_command = itembar0.getSelectedCustomCommand();
    }


    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            title_ = mgr.translate("PCMWACTIVEWORKORDER4TRE: Transfer of Work Order Lines to Customer Order - ");

            xx = headset.getRow().getValue("WO_NO");
            yy = headset.getRow().getValue("ERR_DESCR");
        }
        else
        {
            title_ = mgr.translate("PCMWACTIVEWORKORDER4TRBE: Transfer of Work Order Lines to Customer Order");
            xx = "";
            yy = "";
        } 
        if (itemlay0.isMultirowLayout()|| itemlay0.isSingleLayout())
            itembar0.enableCommand(itembar0.FIND);

        if (itemlay0.isFindLayout())
        {
            mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART",600,450);
            mgr.getASPField("CATALOG_NO").setLOVProperty("WHERE","CONTRACT IS NOT NULL ");
        }

        if (headlay.isFindLayout())
        {
            mgr.getASPField("MCH_CODE").setDynamicLOV("MAINTENANCE_OBJECT",600,450);
            mgr.getASPField("MCH_CODE").setLOVProperty("WHERE","CONTRACT IS NOT NULL ");
        }

        mgr.getASPField("AGREEMENT_PRICE_FLAG").setReadOnly();

        if (!secCheck)
        {
            actionSecurityCheck();
            secCheck = true;
        }

        disableCommands();
        headbar.removeCustomCommand("refreshForm");

    }

    public void actionSecurityCheck()                                                                                                                                                                                               
    {
        ASPManager mgr = getASPManager();                                                                                                                                                                                           

        trans = mgr.newASPTransactionBuffer();                                                                                                                                                                                      
        trans.addSecurityQuery("DOCUMENT_TEXT");
        trans.addSecurityQuery("Active_Work_Order_API","Modify_Coordinator");
        trans.addSecurityQuery("Active_Work_Order_API","Modify_Customer_Order_Type");
        trans.addSecurityQuery("WORK_ORDER_CODING_API","Transfer__");
        trans.addSecurityQuery("WORK_ORDER_CODING_UTILITY_API","Create_Customer_Order_Inv");
        trans.addSecurityQuery("Sales_Part_API","Get_Cost");
        trans.addSecurityQuery("Customer_Order_Line_API","Remove");
        trans.addPresentationObjectQuery("PCMW/ActiveWorkOrderDlg1.page,PCMW/ActiveWorkOrderDlg.page,PCMW/OrderTypeDlg.page,ORDERW/CustomerOrder.page,MPCCOW/DocumentTextDlg.page");

        trans = mgr.perform(trans);                                                                                                                                                                                                 
        ASPBuffer secBuff = trans.getSecurityInfo();                                                                                                                                                                                      

        if (secBuff.itemExists("Active_Work_Order_API.Modify_Coordinator") && secBuff.namedItemExists("PCMW/ActiveWorkOrderDlg1.page"))
            ctxAddCoordinator = true;
        if (secBuff.namedItemExists("PCMW/ActiveWorkOrderDlg.page"))
            ctxChangeCustomer = true;
        if (secBuff.itemExists("Active_Work_Order_API.Modify_Customer_Order_Type") && secBuff.namedItemExists("PCMW/OrderTypeDlg.page"))
            ctxAddCusOrderType = true;
        if (secBuff.itemExists("WORK_ORDER_CODING_API.Transfer__"))
            ctxCreateCustOrderLine = true;
        if (secBuff.itemExists("WORK_ORDER_CODING_UTILITY_API.Create_Customer_Order_Inv"))
            ctxCreCustOrderLines = true;
        if (secBuff.namedItemExists("ORDERW/CustomerOrder.page"))
            ctxCustOrder = true;
        if (secBuff.itemExists("Sales_Part_API.Get_Cost"))
            ctxUsePriceLogic = true;
        if (secBuff.itemExists("DOCUMENT_TEXT") && secBuff.namedItemExists("MPCCOW/DocumentTextDlg.page"))
            ctxDocumentTexts = true;
        if (secBuff.itemExists("Customer_Order_Line_API.Remove"))
            ctxItem0UndoTranToCustOrd = true;
    }                                                                                                                                                                                                                              

    public void disableCommands()                                                                                                                                                                                                   
    {
        ASPManager mgr = getASPManager();                                                                                                                                                                                           

        if (!ctxAddCoordinator)
            headbar.disableCustomCommand("addCoordinator");
        if (!ctxChangeCustomer)
            headbar.disableCustomCommand("changeCustomer");
        if (!ctxAddCusOrderType)
            headbar.disableCustomCommand("addCusOrderType");

        if (!ctxCreateCustOrderLine)
            itembar0.disableCustomCommand("createCustOrderLine");
        if (!ctxCreCustOrderLines)
            itembar0.disableCustomCommand("creCustOrderLines");
        if (!ctxUsePriceLogic)
            itembar0.disableCustomCommand("usePriceLogic");
        if (!ctxDocumentTexts)
            itembar0.disableCustomCommand("documentTexts");
        if (!ctxItem0UndoTranToCustOrd)
            itembar0.disableCustomCommand("item0UndoTranToCustOrd");

        if (mgr.isPresentationObjectInstalled("ORDERW/CustomerOrder.page"))
        {
            if (!ctxCustOrder)
                itembar0.disableCustomCommand("custOrder");
        }
    }                                                                                                                                                                                                                               

    private String replaceString(String str, String s1, String s2)
    {
        ASPManager mgr = getASPManager();

        int length = s1.length();
        int start = str.indexOf(s1);
        int end = start+length;

        String firstPart = str.substring(0,start);
        String lastPart = str.substring(end,str.length());    

        String newString = firstPart.concat(s2);
        newString = newString.concat(lastPart); 

        return newString;
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return title_ + xx + " " + yy;
    }

    protected String getTitle()
    {
        return "PCMWACTIVEWORKORDER4TITLE: Transfer of Work Order Lines to Customer Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        printHiddenField("IGNORECONTRACT", "");
        printHiddenField("IGNORECONTRACTALL", "");
        
        if ((bFirstRequest) && mgr.isExplorer())
              {
                 
            appendToHTML("<html>\n"); 
            appendToHTML("<head></head>\n"); 
            appendToHTML("<body>"); 
            appendToHTML("<form name='form' method='POST' action='"+mgr.getURL()+"'>");
            appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(sCurrentWONo)+"\" name=\"WO_NO\" >");  // XSS_Safe ILSOLK 20070709
            appendToHTML(fmt.drawHidden("FIRST_REQUEST", "OK"));            
            appendToHTML("</form></body></html>"); 
            appendDirtyJavaScript("document.form.submit();");           
             }
       else
       {
       
           appendToHTML(headlay.show());

           if (headset.countRows()>0)
           {
               if (itemlay0.isVisible())
                   appendToHTML(itemlay0.show());
           }

           appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
           appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
           appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

            appendDirtyJavaScript("window.name = \"AciveWorkOrder4\";\n");

            appendDirtyJavaScript("function validateListPrice(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkListPrice(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('LIST_PRICE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('PRICEAMT',i).value = '';\n");
            appendDirtyJavaScript("		getField_('AGREEMENT_PRICE_FLAG',i).value = '';\n");
            appendDirtyJavaScript("		getField_('DISCPRICEAMT',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("   window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=LIST_PRICE'\n");
            appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
            appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
            appendDirtyJavaScript("		+ '&AGREEMENT_PRICE_FLAG=' + URLClientEncode(getValue_('AGREEMENT_PRICE_FLAG',i))\n");
            appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("   window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'LIST_PRICE',i,'Price') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('PRICEAMT',i,0);\n");
            appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,1);\n");
            appendDirtyJavaScript("		assignValue_('DISCPRICEAMT',i,2);\n");
            appendDirtyJavaScript("		f.AGREEMENT_PRICE_FLAG.checked = false;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateQtyToInvoice(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkQtyToInvoice(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('LIST_PRICE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('QTY_TO_INVOICE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('QTY',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('PRICEAMT',i).indexOf('%') != -1) return;\n\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		'/b2e/pcmw/ActiveWorkOrder4.page?VALIDATE=QTY_TO_INVOICE'\n");
            appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
            appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
            appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
            appendDirtyJavaScript("		+ '&DISCPRICEAMT=' + URLClientEncode(getValue_('DISCPRICEAMT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'QTY_TO_INVOICE',i,'Qty to Invoice') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('QTY_TO_INVOICE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('PRICEAMT',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");


            if (bOpenNewWindow)
            {
                appendDirtyJavaScript("if (readCookie('NewWinOpen')=='true')\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("   removeCookie('NewWinOpen',COOKIE_PATH);\n");
                appendDirtyJavaScript("  window.open(\"");
                appendDirtyJavaScript(urlString);
                appendDirtyJavaScript("\", \"");
                appendDirtyJavaScript(newWinHandle);
                appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");
                appendDirtyJavaScript("}\n");
            }
            
            appendDirtyJavaScript("function createCustOrderLineClient(i)\n");
            appendDirtyJavaScript("{\n");
            if (headset.countRows() > 0){
            	if (headset.getRow().getValue("CONTRACT_ID")== null && "TRUE".equals(headset.getRow().getValue("HAS_CONTRACT"))) 
            		appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("WARNING_NOSRVCON: No Service Contract has been entered for the work order - &1. Do you still want to transfer Work Order Lines to Customer Order?", headset.getRow().getValue("WO_NO")) + "');\n");
            }
            appendDirtyJavaScript("      return true;\n"); 
            appendDirtyJavaScript("}\n");
        }
        if (bHasContract)
        {
      	   bHasContract = false;
      	   appendDirtyJavaScript("if ((readCookie(\"PageID_my_cookie\")==\"TRUE\") || (readCookie(\"PageID_my_cookie2\")==\"TRUE\"))\n");
            appendDirtyJavaScript("if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("WARNING_NOSRVCON: No Service Contract has been entered for the work order - &1. Do you still want to transfer Work Order Lines to Customer Order?", headset.getRow().getValue("WO_NO")));
            appendDirtyJavaScript("\")) {\n");
            appendDirtyJavaScript("       if (readCookie(\"PageID_my_cookie\")==\"TRUE\")\n");
            appendDirtyJavaScript("	         document.form.IGNORECONTRACT.value = \"TRUE\";\n");
            appendDirtyJavaScript("       else if (readCookie(\"PageID_my_cookie2\")==\"TRUE\")\n");
            appendDirtyJavaScript("          document.form.IGNORECONTRACTALL.value = \"TRUE\";\n");
            appendDirtyJavaScript("       writeCookie(\"PageID_my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
            appendDirtyJavaScript("       writeCookie(\"PageID_my_cookie2\", \"FALSE\", '', COOKIE_PATH); \n");
            appendDirtyJavaScript("       f.submit();\n");
            appendDirtyJavaScript("     } \n");
        }

    }
}
