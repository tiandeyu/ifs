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
*  File        : WorkOrderPlanning.java 
*  Created     : ASP2JAVA Tool  010406  Created Using the ASP file WorkOrderPlanning.asp
*  Modified    : 
*  BUNILK  010407  Corrected some conversion errors.
*  JEWILK  010524  Corrected validation error of the field 'CATALOG_NO' to correctly fetch 'SALES_PRICE'
*                  and to calculate 'SALESPRICEAMOUNT'. modified method 'CountSalesPriceAmount()'.
*                  overwritten tha javascript function 'lovCatalogNo()' to pass the 'CATALOG_CONTRACT'
*                  to the LOV.
*  INROLK  010806  Added field Discounted . call id 77839.
*  BUNILK  010818  Modified newRowITEM() method. 
*  BUNILK  010824  Modified CATALOG_NO field validation. 
*  BUNILK  011019  Modified validation method. 
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  CHCRLK  031110  Modified validations of fields WORK_ORDER_COST_TYPE, QTY_TO_INVOICE, QUANTITY & CATALOG_NO. Set Number mask # to WO_NO.
*  SAPRLK  040105  Web Alignment - removed methods clone() and doReset().
*  VAGULK  040128  Web Alignment - arranged the field order as in Centura Application.
*  SAPRLK  040212  Web Alignment - change of conditional code in validate method, 
*                  remove unnecessary method calls for hidden fields
*  THWILK  040315  Call ID 112687 - Added saveReturn() method and made Cost Amount, Price Amount, Discounted Price fileds as Readonly.
* --------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040303  Bug 42994, Added methods saveReturnItem(), saveNewItem() and modified predefine().
*  THWILK  040324  Merge with SP1.
*  ARWILK  040617  Added job_id to itemblk.(Spec - AMEC109A)  
*  SHAFLK  040705  Bug 42838, Added adjust(), modified run() and preDefine(). 
*  ThWilk  040806  Merged Bug 42838.
*  SHAFLK  040629  Bug 45550, Changed validation of QTY_TO_INVOICE. 
*  ThWilk  040816  Merged Bug 45550.
*  NIAJLK  040830  Call 117458, Modified validate().
*  NIJALK  040908  Call 117750: Modified predefine().
*  DIAMLK  050215  Modified the method validate().
*  NIJALK  050826  Bug 126558: Modified preDefine(), adjust() to Set the cost field read only for automatically generated external lines.
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFind1(), okFindITEM() 
*  AMDILK  060904  Merged with the Bug Id 58216.
*  ILSOLK  060905  Merged Bug 59699.
*  AMDILK  070801  Removed the scroll buttons of the parent when the detail block is in new or edit modes
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderPlanning extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderPlanning");


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
    private String frmname;
    private String quo_id;
    private String sCustomerNo;
    private String sAgreementId;
    private String sOrgCode;
    private String sCostTypeMaterial;
    private String sCostTypePersonnel;
    private String sCostTypeFixedPrice;
    private String sCostTypeExternal;
    private String sCostTypeExpenses;
    private String sCostTypeTools;
    private String callingUrl;
    private String wo_no;
    private ASPBuffer buff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private ASPQuery q;
    private ASPBuffer row;
    private String qut_id;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderPlanning(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();
        frmname = ctx.readValue("QRYSTR","");
        quo_id = ctx.readValue("MWONO","");
        sCustomerNo = ctx.readValue("CUSTOMERNO","");
        sAgreementId = ctx.readValue("AGREEMENTID","");
        sOrgCode = ctx.readValue("SORGCODE","");
        qut_id = ctx.readValue("QUTID","");

        sCostTypeMaterial = ctx.readValue("SCOSTTYPEMATERIAL","");
        sCostTypePersonnel = ctx.readValue("SCOSTTYPEPERSONNEL","");
        sCostTypeFixedPrice = ctx.readValue("SCOSTTYPEFIXEDPRICE","");
        sCostTypeExternal = ctx.readValue("SCOSTTYPEEXTERNAL","");
        sCostTypeExpenses = ctx.readValue("SCOSTTYPEEXPENSES","");
        sCostTypeTools = ctx.readValue("SCOSTTYPETOOLS","");

        callingUrl = ctx.getGlobal("CALLING_URL");

        startUp();  

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            wo_no = mgr.readValue("WO_NO","");
            quo_id = mgr.readValue("QUOTATION_ID","");
            frmname = mgr.readValue("FORM_NAME","");
            sCustomerNo = mgr.readValue("CUSTOMER_NO","");
            sAgreementId = mgr.readValue("AGREEMENT_ID","");
            sOrgCode = mgr.readValue("ORG_CODE",""); 

            okFind1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();
            row = buff.getBufferAt(0);
            wo_no = row.getValue("WO_NO");
            sCustomerNo = row.getValue("CUSTOMER_NO");
            sAgreementId = row.getValue("AGREEMENT_ID");
            sOrgCode = row.getValue("ORG_CODE"); 
            qut_id = row.getValue("QUOTATION_ID");

            okFind1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        adjust();

        ctx.writeValue("QRYSTR",frmname);
        ctx.writeValue("MWONO",quo_id);

        ctx.writeValue("SCOSTTYPEMATERIAL",sCostTypeMaterial);
        ctx.writeValue("SCOSTTYPEPERSONNEL",sCostTypePersonnel);
        ctx.writeValue("SCOSTTYPEFIXEDPRICE",sCostTypeFixedPrice);
        ctx.writeValue("SCOSTTYPEEXTERNAL",sCostTypeExternal);
        ctx.writeValue("SCOSTTYPEEXPENSES",sCostTypeExpenses);
        ctx.writeValue("SCOSTTYPETOOLS",sCostTypeTools);     
        ctx.writeValue("QUTID",qut_id);
    }

    public void  startUp()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("CIENTVAL1","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX1");
        cmd.addParameter("INDEX1","1");

        cmd = trans.addCustomFunction("CIENTVAL0","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX0");
        cmd.addParameter("INDEX0","0");

        cmd = trans.addCustomFunction("CIENTVAL4","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX4");
        cmd.addParameter("INDEX4","4");

        cmd = trans.addCustomFunction("CIENTVAL2","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX2");
        cmd.addParameter("INDEX2","2");

        cmd = trans.addCustomFunction("CIENTVAL3","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX3");
        cmd.addParameter("INDEX3","3");

        cmd = trans.addCustomFunction("CIENTVAL5","Work_Order_Cost_Type_Api.Get_Client_Value","INDEX5");
        cmd.addParameter("INDEX5","5");

        trans = mgr.perform(trans);

        sCostTypeMaterial = trans.getValue("CIENTVAL1/DATA/INDEX1");
        sCostTypePersonnel = trans.getValue("CIENTVAL0/DATA/INDEX0");
        sCostTypeFixedPrice = trans.getValue("CIENTVAL4/DATA/INDEX4");
        sCostTypeExternal = trans.getValue("CIENTVAL2/DATA/INDEX2");
        sCostTypeExpenses = trans.getValue("CIENTVAL3/DATA/INDEX3");
        sCostTypeTools = trans.getValue("CIENTVAL5/DATA/INDEX5");

        trans.clear();
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");

        String txt = "";

        if ("WORK_ORDER_COST_TYPE".equals(val))
        {
            String sWorkOrderCostType = mgr.readValue("WORK_ORDER_COST_TYPE","");

            if (sCostTypeMaterial.equals(sWorkOrderCostType))
                txt = "No_Data_Found" + mgr.translate("PCMWWORKORDERPLANNINGREGMATCOST: Registration of Material costs is not allowed on this form.");

            else if (sCostTypePersonnel.equals(sWorkOrderCostType))
                txt = "No_Data_Found" + mgr.translate("PCMWWORKORDERPLANNINGREGPERCOST: Registration of Personnel costs is not allowed on this form.");

            else if (sCostTypeTools.equals(sWorkOrderCostType))
                txt = "No_Data_Found" + mgr.translate("PCMWWORKORDERPLANNINGREGTOOLCOST: Registration of Tools/Facilities cost is not allowed on this form.");

            else if (sCostTypeFixedPrice.equals(sWorkOrderCostType))
            {
                trans.clear();

                cmd = trans.addCustomFunction("WOINVOTYPE","Work_Order_Invoice_Type_Api.Get_Client_Value","INDEX1");
                cmd.addParameter("INDEX1","0");

                trans = mgr.validate(trans);

                String sClientVal = trans.getValue("WOINVOTYPE/DATA/INDEX1");
                double sCost = 0;

                String s_cost = mgr.getASPField("COST").formatNumber(sCost);

                txt = (mgr.isEmpty(sClientVal)?"":sClientVal) + "^" + (mgr.isEmpty(s_cost)?"":s_cost) + "^";
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomFunction("WOINVOTYPE","Work_Order_Invoice_Type_Api.Get_Client_Value","INDEX1");
                cmd.addParameter("INDEX1","1");

                trans = mgr.validate(trans);

                String sClientVal = trans.getValue("WOINVOTYPE/DATA/INDEX1");

                txt = (mgr.isEmpty(sClientVal)?"":sClientVal) + "^" + "" + "^";
            }

            mgr.responseWrite(txt);
        }

        else if ("QTY_TO_INVOICE".equals(val))
        {
            String sCatalogNo = mgr.readValue("CATALOG_NO","");
            double nQtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nQtyToInvoice))
                nQtyToInvoice = 0;

            double nSalesPrice = mgr.readNumberValue("SALES_PRICE");
            if (isNaN(nSalesPrice))
                nSalesPrice = 0;

            double nSalesPriceAmt = 0;
            double nDiscount = 0;
            String sPriceListNo = mgr.readValue("PRICE_LIST_NO","");
            double nSalesUnitPrice = 0;
            double discountedPrice = 0;

            double nQuantity = mgr.readNumberValue("QUANTITY");
            if (isNaN(nQuantity))
                nQuantity = 0;

            String sCatalogContract = new String();
            String sSalesPrice = new String();
            String sSalesPriceAmt = new String();
            String sDiscount = new String();
            String sdiscountedPrice = new String();
            String sCost = new String(); 
            String sCostAmt = new String();      

            //sOrgCode = mgr.readValue("ORG_CODE","");

            if (!mgr.isEmpty(sCatalogNo) && nQtyToInvoice != 0)
            {
                sCatalogContract = mgr.readValue("CATALOG_CONTRACT","");

                double nQty = 0;
                if (nQtyToInvoice != 0)
                    nQty = nQtyToInvoice;
                else
                    nQty = nQuantity;

                trans.clear();

                sCustomerNo = mgr.readValue("CUSTOMER_NO","");
                sAgreementId = mgr.readValue("AGREEMENT_ID","");

                cmd = trans.addCustomCommand("GETPRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0");
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT",sCatalogContract);
                cmd.addParameter("CATALOG_NO",sCatalogNo);
                cmd.addParameter("CUSTOMER_NO",sCustomerNo);
                cmd.addParameter("AGREEMENT_ID",sAgreementId);
                cmd.addParameter("PRICE_LIST_NO",sPriceListNo);
                cmd.addParameter("QUANTITY",mgr.formatNumber("QUANTITY",nQty));
            }

            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","COST");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            trans = mgr.validate(trans);


            double nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
            if (isNaN(nCost))
                nCost = 0;

            //if(nCost == 0 && !mgr.isEmpty(sOrgCode))
            //nCost = trans.getNumberValue("ORGACOST/DATA/COST");

            if (!mgr.isEmpty(sCatalogNo) && nQtyToInvoice != 0)
            {
                if (mgr.isEmpty(sPriceListNo))
                    sPriceListNo = trans.getValue("GETPRICEINFO/DATA/PRICE_LIST_NO");

                nSalesUnitPrice = trans.getNumberValue("GETPRICEINFO/DATA/SALE_PRICE");
                if (isNaN(nSalesUnitPrice))
                    nSalesUnitPrice = 0;

                double nQuantityNew = trans.getNumberValue("GETPRICEINFO/DATA/QUANTITY");
                if (isNaN(nQuantityNew))
                    nQuantityNew = 0;

                nSalesPriceAmt = nSalesUnitPrice * nQuantityNew;
                nDiscount = trans.getNumberValue("GETPRICEINFO/DATA/DISCOUNT");
                discountedPrice = nSalesUnitPrice - (nDiscount/100 * nSalesUnitPrice);
            }

            trans.clear();


            double nCostAmt;

            if (nCost > 0)
                nCostAmt = nCost * nQuantity;
            else
                nCostAmt = 0;

            double CountSPriceAmount[] = CountSalesPriceAmount(true,nSalesUnitPrice,nDiscount);
            nSalesPriceAmt = CountSPriceAmount[0];
            discountedPrice = CountSPriceAmount[1];

            sSalesPrice = mgr.formatNumber("SALES_PRICE",nSalesUnitPrice);
            sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            sDiscount = mgr.formatNumber("DISCOUNT",nDiscount);
            sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);
            sCost = mgr.getASPField("COST").formatNumber(nCost); 
            sCostAmt = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmt);      

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^" + (mgr.isEmpty(sPriceListNo)?"":sPriceListNo) + "^" + (mgr.isEmpty(sSalesPrice)?"":sSalesPrice) + "^" + (mgr.isEmpty(sDiscount)?"":sDiscount) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^";           

            mgr.responseWrite(txt);
        }

        else if ("QUANTITY".equals(val))
        {
            //sOrgCode = mgr.readValue("ORG_CODE","");

            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","COST");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            trans = mgr.validate(trans);

            double nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
            if (isNaN(nCost))
                nCost = 0;

            //if(nCost == 0 && !mgr.isEmpty(sOrgCode))
            //nCost = trans.getNumberValue("ORGACOST/DATA/COST");

            double CountSPriceAmount[] = CountSalesPriceAmount(false,0,0);
            double nSalesPriceAmt = CountSPriceAmount[0];
            double discountedPrice = CountSPriceAmount[1];

            double nQuantity = mgr.readNumberValue("QUANTITY");
            if (isNaN(nQuantity))
                nQuantity = 0;

            double nCostAmt = 0;

            if (nCost > 0)
                nCostAmt = nCost * nQuantity;
            else
                nCostAmt = 0;

            String sCostAmt = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmt);
            String sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);
            String sCost = mgr.getASPField("COST").formatNumber(nCost); 

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^" + (mgr.isEmpty(sCostAmt)?"":sCostAmt) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^"+ (mgr.isEmpty(sCost)?"":sCost) + "^";
            mgr.responseWrite(txt);
        }

        else if ("COST".equals(val))
        {
            double CountSPriceAmount[] = CountSalesPriceAmount(false,0,0);
            double nSalesPriceAmt = CountSPriceAmount[0];
            double discountedPrice = CountSPriceAmount[1];

            double nQuantity = mgr.readNumberValue("QUANTITY");
            if (isNaN(nQuantity))
                nQuantity = 0;

            double nCost = mgr.readNumberValue("COST");
            if (isNaN(nCost))
                nCost = 0;

            double nCostAmt = 0;

            if (nCost > 0)
                nCostAmt = nCost * nQuantity;

            String costTypeVal = mgr.readValue("WORK_ORDER_COST_TYPE");
            String sSalesPartNo = mgr.readValue("CATALOG_NO");

            trans.clear();
            cmd = trans.addCustomFunction("GETWOCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
            cmd = trans.addCustomFunction("GETWOCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
            trans = mgr.validate(trans);

            String woCostTypeExternal = trans.getValue("GETWOCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
            String woCostTypeExpense = trans.getValue("GETWOCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE");

            if (mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExternal) || mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExpense))
            {
                if ((nCost>= 0 || sSalesPartNo != null)&&(nQuantity== 0))
                {
                    nQuantity = 1;
                }
            }

            String sCostAmt = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmt);     
            String sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);
            String sWoQtyPlan = mgr.formatNumber("QUANTITY",nQuantity);

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^" + (mgr.isEmpty(sCostAmt)?"":sCostAmt) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^"+ (mgr.isEmpty(sWoQtyPlan)?"":sWoQtyPlan) + "^";
            mgr.responseWrite(txt); 
        }

        else if ("SALES_PRICE".equals(val))
        {

            double CountSPriceAmount[] = CountSalesPriceAmount(false,0,0);
            double nSalesPriceAmt = CountSPriceAmount[0];
            double discountedPrice = CountSPriceAmount[1];

            String sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^";
            mgr.responseWrite(txt); 
        }

        else if ("DISCOUNT".equals(val))
        {
            double CountSPriceAmount[] = CountSalesPriceAmount(false,0,0);
            double nSalesPriceAmt = CountSPriceAmount[0];
            double discountedPrice = CountSPriceAmount[1]; 

            String sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^";
            mgr.responseWrite(txt); 
        }

        else if ("WORK_ORDER_INVOICE_TYPE".equals(val))
        {
            double CountSPriceAmount[] = CountSalesPriceAmount(false,0,0);
            double nSalesPriceAmt = CountSPriceAmount[0];
            double discountedPrice = CountSPriceAmount[1]; 
            String sSalesPriceAmt = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmt);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);

            txt = (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^";
            mgr.responseWrite(txt); 
        }

        else if ("CATALOG_NO".equals(val))
        {
            trans.clear();

            double nQuantity = mgr.readNumberValue("QUANTITY");
            if (isNaN(nQuantity))
                nQuantity = 0;

            cmd = trans.addCustomFunction("GETWOCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
            cmd = trans.addCustomFunction("GETWOCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
            trans = mgr.validate(trans);

            String woCostTypeExternal = trans.getValue("GETWOCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
            String woCostTypeExpense = trans.getValue("GETWOCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE");

            if (!mgr.isEmpty(mgr.readValue("CATALOG_NO"))&&(mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExternal)) || (mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExpense)))
            {
                if (nQuantity == 0)
                {
                    nQuantity = 1;
                }
            }

            trans.clear();

            sCustomerNo = mgr.readValue("CUSTOMER_NO","");
            //sOrgCode = mgr.readValue("ORG_CODE","");
            sAgreementId = mgr.readValue("AGREEMENT_ID",""); 

            cmd = trans.addCustomFunction("VALPRICELIST","Customer_Order_Pricing_Api.Get_Valid_Price_List","PRICE_LIST_NO");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO",sCustomerNo);    
            cmd.addParameter("CURRENCEY_CODE");

            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","COST");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            cmd = trans.addCustomFunction("CATDESC1","SALES_PART_API.Get_Catalog_Desc","LINE_DESCRIPTION");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            double qtyToInvVar = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qtyToInvVar))
                qtyToInvVar = 0;

            double nQty = 0;
            if (qtyToInvVar != 0)
                nQty = qtyToInvVar;
            else
                nQty = nQuantity;

            double nSalesPrice = 0;
            double nDiscount = 0;
            double discountedPrice = 0;
            double nSalesPriceAmt = 0;

            if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")) && (nQuantity != 0))
            {
                cmd = trans.addCustomCommand("INFO5", "Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0");
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO",sCustomerNo);
                cmd.addParameter("AGREEMENT_ID",sAgreementId);
                cmd.addReference("PRICE_LIST_NO","VALPRICELIST/DATA");
                cmd.addParameter("QUANTITY",mgr.formatNumber("QUANTITY",nQty));
            }

            trans = mgr.validate(trans);

            String sPriceListNo = trans.getValue("VALPRICELIST/DATA/PRICE_LIST_NO");
            double nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
            String sLineDesc = trans.getValue("CATDESC1/DATA/LINE_DESCRIPTION");
            if (isNaN(nCost))
                nCost = 0;

            //if( nCost == 0 && !mgr.isEmpty(sOrgCode))
            //nCost = trans.getNumberValue("ORGACOST/DATA/COST");

            if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")) && (nQuantity != 0))
            {
                if (mgr.isEmpty(sPriceListNo))
                    sPriceListNo = trans.getBuffer("INFO5/DATA").getFieldValue("PRICE_LIST_NO");

                nSalesPrice = trans.getNumberValue("INFO5/DATA/SALE_PRICE");    
                if (isNaN(nSalesPrice))
                    nSalesPrice = 0;

                nDiscount = trans.getNumberValue("INFO5/DATA/DISCOUNT");                   
                if (isNaN(nDiscount))
                    nDiscount = 0;

                discountedPrice = (nSalesPrice - (nDiscount / 100 * nSalesPrice));
                nSalesPriceAmt = discountedPrice * nQty;
            }
            double nCostAmt;

            if (nCost > 0)
                nCostAmt = nCost * nQuantity;
            else
                nCostAmt = 0;    

            double CountSPriceAmount[] = CountSalesPriceAmount(true,nSalesPrice,nDiscount);
            nSalesPriceAmt = CountSPriceAmount[0];
            discountedPrice = CountSPriceAmount[1];

            String sCostAmt = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmt);      
            String sCost = mgr.getASPField("COST").formatNumber(nCost);      
            String sSalesPriceAmt = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmt);  
            String sSalesPrice = mgr.getASPField("SALES_PRICE").formatNumber(nSalesPrice);  
            String sDiscount = mgr.getASPField("DISCOUNT").formatNumber(nDiscount);
            String sdiscountedPrice = mgr.formatNumber("DISCOUNTED",discountedPrice);
            String sWoQtyPlan = mgr.formatNumber("QUANTITY",nQuantity);

            txt = (mgr.isEmpty(sCost)?"":sCost) + "^" + (mgr.isEmpty(sCostAmt)?"":sCostAmt) + "^" + (mgr.isEmpty(sSalesPriceAmt)?"":sSalesPriceAmt) + "^" + (mgr.isEmpty(sPriceListNo)?"":sPriceListNo) + "^" + (mgr.isEmpty(sSalesPrice)?"":sSalesPrice) + "^" + (mgr.isEmpty(sDiscount)?"":sDiscount) + "^"+ (mgr.isEmpty(sdiscountedPrice)?"":sdiscountedPrice) + "^"+(mgr.isEmpty(sWoQtyPlan)?"":sWoQtyPlan) + "^"+ (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^";

            mgr.responseWrite(txt);
        }
        mgr.endResponse();
    }

    public double[] CountSalesPriceAmount(boolean hasVlues, double inSalesPrice, double inDiscount)
    {
        double countSPAmount[] =new double[2];
        ASPManager mgr = getASPManager();

        trans.clear();

        String sWorkOrderInvoiceType = mgr.readValue("WORK_ORDER_INVOICE_TYPE","");

        cmd = trans.addCustomFunction("INVTYPE","Work_Order_Invoice_Type_API.Encode","WORK_ORDER_INVOICE_TYPE");
        cmd.addParameter("WORK_ORDER_INVOICE_TYPE");

        trans = mgr.validate(trans);

        String valWoInvType = trans.getValue("INVTYPE/DATA/WORK_ORDER_INVOICE_TYPE");
        double nQtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
        if (isNaN(nQtyToInvoice))
            nQtyToInvoice = 0;

        double nQuantity = mgr.readNumberValue("QUANTITY");
        if (isNaN(nQuantity))
            nQuantity = 0;

        double nSalesPrice = mgr.readNumberValue("SALES_PRICE");
        if (isNaN(nSalesPrice))
            nSalesPrice = 0;

        double nDiscount = mgr.readNumberValue("DISCOUNT");
        if (isNaN(nDiscount))
            nDiscount = 0;
        double discountedPrice = mgr.readNumberValue("DISCOUNTED");
        if (isNaN(discountedPrice))
            discountedPrice = 0;

        if (hasVlues)
        {
            nSalesPrice = inSalesPrice;
            nDiscount = inDiscount;
        }

        double nSalesPriceAmt = 0;

        if ("AR".equals(valWoInvType))
        {
            if (nDiscount == 0)
            {
                nSalesPriceAmt = nSalesPrice * nQuantity;
                discountedPrice = nSalesPrice;
            }
            else
            {
                nSalesPriceAmt = nSalesPrice * nQuantity;
                nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
            }
        }

        else if ("MINQ".equals(valWoInvType))
        {
            if (nQuantity > nQtyToInvoice)
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmt = nSalesPrice * nQuantity;
                    discountedPrice = nSalesPrice;
                }
                else
                {
                    nSalesPriceAmt = nSalesPrice * nQuantity;
                    nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                    discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
                }
            }
            else
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                    discountedPrice = nSalesPrice;
                }
                else
                {
                    nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                    nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                    discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
                }
            }
        }

        else if ("MAXQ".equals(valWoInvType))
        {
            if (nQuantity < nQtyToInvoice)
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmt = nSalesPrice * nQuantity;
                    discountedPrice = nSalesPrice;
                }
                else
                {
                    nSalesPriceAmt = nSalesPrice * nQuantity;
                    nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                    discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
                }
            }
            else
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                    discountedPrice = nSalesPrice;
                }
                else
                {
                    nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                    nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                    discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
                }
            }
        }

        else if ("FL".equals(valWoInvType))
        {
            if (nDiscount == 0)
            {
                nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                discountedPrice = nSalesPrice;
            }
            else
            {
                nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
            }
        }


        else if (mgr.isEmpty(valWoInvType))
        {

            if (nDiscount == 0)
            {
                nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                discountedPrice = nSalesPrice;
            }
            else
            {
                nSalesPriceAmt = nSalesPrice * nQtyToInvoice;
                nSalesPriceAmt = nSalesPriceAmt - (nDiscount / 100 * nSalesPriceAmt);
                discountedPrice = nSalesPrice - (nDiscount / 100 * nSalesPrice);
            }
        }
        countSPAmount[0]=nSalesPriceAmt;
        countSPAmount[1]=discountedPrice;
        return countSPAmount;
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  newRowITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_PLANNING_API.New__",itemblk);
        cmd.setParameter("ITEM_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("GETCON","ACTIVE_SEPARATE_API.Get_Contract","CONTRACT");
        cmd.addParameter("ITEM_WO_NO",headset.getRow().getValue("WO_NO"));

        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM/DATA");
        data.setFieldItem("CATALOG_CONTRACT",trans.getValue("GETCON/DATA/CONTRACT"));
        itemset.addRow(data);

        if (!mgr.isEmpty(qut_id))
            mgr.showAlert(mgr.translate("PCMWWORKORDERPLANNINGWOCREATEFROMQUOTATION: The Work Order is created from a Work Order Quotation."));
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void  countFindITEM()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
    }

    public void  okFind()
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
            mgr.showAlert(mgr.translate("PCMWWORKORDERPLANNINGNODATA: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
            okFindITEM();
    }

    public void  okFind1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO", wo_no);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERPLANNINGNODATA: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
            okFindITEM();

        if (headset.countRows() > 0)
        {
            ASPBuffer row = headset.getRow();
            row.setValue("CUSTOMER_NO",sCustomerNo);
            row.setValue("AGREEMENT_ID",sAgreementId);
            row.setValue("ORG_CODE",sOrgCode);
            headset.setRow(row);
        }
    }

    public void  okFindITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk);
        q.addWhereCondition("WO_NO = ?");  
	q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        int headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(headrowno);

        if (itemset.countRows() > 0)
            countSalesPriceAmt();
    }

    public void  saveReturnItem()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addCustomFunction("RETURNQUANTITY","WORK_ORDER_PLANNING_API.Get_Qty","QUANTITY");
        cmd.addParameter("WO_NO");
        cmd.addParameter("PLAN_LINE_NO");
        cmd = trans.addCustomFunction("COST_TYPE","WORK_ORDER_COST_TYPE_API.Encode","CLIENT_VAL");
        cmd.addParameter("WORK_ORDER_COST_TYPE");
        trans = mgr.perform(trans);

        double qty= trans.getNumberValue("RETURNQUANTITY/DATA/QUANTITY");
        String cost_type = trans.getValue("COST_TYPE/DATA/CLIENT_VAL");

        if ((cost_type.equals("P"))||(cost_type.equals("M"))||(cost_type.equals("T")))
        {

            if (qty!=mgr.readNumberValue("QUANTITY"))
            {
                mgr.showError(mgr.translate("PCMWWORKORDERPLANNINGCOSTTYPE: Quantity is not updatable when Work Order Cost Type is Personnel,Material or Tools/Facilities."));
            }

        }

        trans.clear();

        int currHead = headset.getCurrentRowNo();
        int currrowItem = itemset.getCurrentRowNo();

        itemset.changeRow();
        String status = itemset.getRowStatus();

        mgr.submit(trans);

        itemlay.setLayoutMode(itemlay.SINGLE_LAYOUT);
        itemset.goTo(currrowItem);
        headset.goTo(currHead);


        if ("Modify__".equals(status) && "T".equals(itemset.getRow().getFieldValue("WORK_ORDER_COST_TYPE_DB")))
        {
            String lsAttr =  "CATALOG_NO_CONTRACT" + (char)31 + itemset.getRow().getFieldValue("CATALOG_CONTRACT") + (char)30
                             +"CATALOG_NO" + (char)31 + itemset.getRow().getFieldValue("CATALOG_NO") + (char)30
                             +"LINE_DESCRIPTION" + (char)31 + itemset.getRow().getFieldValue("LINE_DESCRIPTION") + (char)30
                             +"SALES_PRICE" + (char)31 + itemset.getRow().getFieldValue("SALES_PRICE") + (char)30
                             +"DISCOUNT" + (char)31 + itemset.getRow().getFieldValue("DISCOUNT") + (char)30;

            trans.clear();
            cmd = trans.addCustomFunction("GETROW", "Work_Order_Tool_Facility_Api.Get_Row_No_For_Planning","ROW_NO");
            cmd.addParameter("ITEM_WO_NO"); 
            cmd.addParameter("PLAN_LINE_NO");

            cmd = trans.addCustomCommand("SETVAL","Work_Order_Tool_Facility_Api.Set_Field_Value");
            cmd.addParameter("ITEM_WO_NO"); 
            cmd.addReference("ROW_NO","GETROW/DATA"); 
            cmd.addParameter("ATTR",lsAttr); 
            trans=mgr.perform(trans);
        }

    }

    public void  saveNewItem()
    {
        saveReturnItem();
        newRowITEM();
    }

    public void  countSalesPriceAmt()
    {
        ASPManager mgr = getASPManager();

        int n = itemset.countRows();

        itemset.first();

        for (int i=0; i<=n; ++i)
        {
            trans.clear();

            String sWorkOrderInvoiceType = itemset.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

            cmd = trans.addCustomFunction("INVTYPE"+i,"Work_Order_Invoice_Type_API.Encode","WORK_ORDER_INVOICE_TYPE");
            cmd.addParameter("CLIENT_VAL", sWorkOrderInvoiceType);

            trans = mgr.perform(trans);

            String valWoInvType = trans.getValue("INVTYPE"+i+"/DATA/WORK_ORDER_INVOICE_TYPE");

            double salesPriceAmt = 0;
            double discountedPrice =0;


            if ("AR".equals(valWoInvType))
            {
                double colDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;
                double colSalesPrice = itemset.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(colSalesPrice))
                    colSalesPrice = 0;

                double colQuantity = itemset.getRow().getNumberValue("QUANTITY");
                if (isNaN(colQuantity))
                    colQuantity = 0;

                if (colDiscount == 0)
                {
                    salesPriceAmt = colSalesPrice * colQuantity;
                    discountedPrice = colSalesPrice;
                }
                else
                {
                    salesPriceAmt = colSalesPrice * colQuantity;
                    salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                    discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                }
            }

            else if ("MINQ".equals(valWoInvType))
            {
                double colQuantity = itemset.getRow().getNumberValue("QUANTITY");
                if (isNaN(colQuantity))
                    colQuantity = 0;

                double colQuantityToInvoice = itemset.getRow().getNumberValue("QTY_TO_INVOICE");
                if (isNaN(colQuantityToInvoice))
                    colQuantityToInvoice = 0;

                double colDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double colSalesPrice = itemset.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(colSalesPrice))
                    colSalesPrice = 0;

                if (colQuantity > colQuantityToInvoice)
                {
                    if (colDiscount == 0)
                    {
                        salesPriceAmt = colSalesPrice * colQuantity;
                        discountedPrice = colSalesPrice;
                    }
                    else
                    {
                        salesPriceAmt = colSalesPrice * colQuantity;
                        salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                        discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                    }
                }
                else
                {
                    if (colDiscount == 0)
                    {
                        salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                        discountedPrice = colSalesPrice;
                    }
                    else
                    {
                        salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                        salesPriceAmt = salesPriceAmt - (colDiscount / 100  * salesPriceAmt);
                        discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                    }
                }
            }

            else if ("MAXQ".equals(valWoInvType))
            {
                double colQuantity = itemset.getRow().getNumberValue("QUANTITY");
                if (isNaN(colQuantity))
                    colQuantity = 0;

                double colQuantityToInvoice = itemset.getRow().getNumberValue("QTY_TO_INVOICE");
                if (isNaN(colQuantityToInvoice))
                    colQuantityToInvoice = 0;

                double colDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double colSalesPrice = itemset.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(colSalesPrice))
                    colSalesPrice = 0;

                if (colQuantity < colQuantityToInvoice)
                {
                    if (colDiscount == 0)
                    {
                        salesPriceAmt = colSalesPrice * colQuantity;
                        discountedPrice = colSalesPrice;
                    }
                    else
                    {
                        salesPriceAmt = colSalesPrice * colQuantity;
                        salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                        discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                    }
                }
                else
                {
                    if (colDiscount == 0)
                    {
                        salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                        discountedPrice = colSalesPrice;
                        discountedPrice = colSalesPrice;
                    }
                    else
                    {
                        salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                        salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                        discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                    }
                }
            }

            else if ("FL".equals(valWoInvType))
            {
                double colDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double colQuantityToInvoice = itemset.getRow().getNumberValue("QTY_TO_INVOICE");
                if (isNaN(colQuantityToInvoice))
                    colQuantityToInvoice = 0;

                double colSalesPrice = itemset.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(colSalesPrice))
                    colSalesPrice = 0;

                if (colDiscount == 0)
                {
                    salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                    discountedPrice = colSalesPrice;
                }
                else
                {
                    salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                    salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                    discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                }
            }

            else if ("".equals(valWoInvType))
            {
                double colDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double colQuantityToInvoice = itemset.getRow().getNumberValue("QTY_TO_INVOICE");
                if (isNaN(colQuantityToInvoice))
                    colQuantityToInvoice = 0;

                double colSalesPrice = itemset.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(colSalesPrice))
                    colSalesPrice = 0;

                double colQuantity = itemset.getRow().getNumberValue("QUANTITY");
                if (isNaN(colQuantity))
                    colQuantity = 0;


                if (colDiscount == 0)
                {
                    salesPriceAmt = colSalesPrice * colQuantityToInvoice;
                    discountedPrice = colSalesPrice;
                }
                else
                {
                    salesPriceAmt = colSalesPrice * colQuantity;
                    salesPriceAmt = salesPriceAmt - (colDiscount / 100 * salesPriceAmt);
                    discountedPrice = colSalesPrice - (colDiscount / 100 * colSalesPrice);
                }
            }
            row = itemset.getRow();
            row.setNumberValue("SALESPRICEAMOUNT",salesPriceAmt);
            row.setNumberValue("DISCOUNTED",discountedPrice);
            itemset.setRow(row);

            itemset.next();
        } 
        itemset.first();
    }


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
        setLabel("PCMWWORKORDERPLANNINGHEADWONO: WO No").
        setReadOnly().
        setMaxLength(8);

        headblk.addField("CONTRACT").
        setSize(5).
        setLabel("PCMWWORKORDERPLANNINGCONTRACT: Site").
        setUpperCase().
        setReadOnly();

        headblk.addField("MCH_CODE").
        setSize(13).
        setMaxLength(100).
        setLabel("PCMWWORKORDERPLANNINGMCH_CODE: Object ID").
        setUpperCase().
        setReadOnly();

        headblk.addField("DESCRIPTION").
        setSize(28).
        setLabel("PCMWWORKORDERPLANNINGDESCRIPTION: Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("INDEX1").
        setFunction("''").
        setHidden();  

        headblk.addField("INDEX0").
        setFunction("''").
        setHidden();  

        headblk.addField("INDEX2").
        setFunction("''").
        setHidden();    

        headblk.addField("INDEX3").
        setFunction("''").
        setHidden();  

        headblk.addField("INDEX4").
        setFunction("''").
        setHidden();   

        headblk.addField("INDEX5").
        setFunction("''").
        setHidden();   

        headblk.addField("ORG_CODE").
        setFunction("''").
        setHidden();    

        headblk.addField("STATE").
        setSize(11).
        setLabel("PCMWWORKORDERPLANNINGSTATE: Status").
        setReadOnly().
        setMaxLength(30);

        headblk.addField("CUSTOMER_NO").
        setHidden().
        setFunction("''");  

        headblk.addField("AGREEMENT_ID").
        setHidden().
        setFunction("''");   

        headblk.addField("CURRENCEY_CODE").
        setHidden().
        setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:WO_NO)");


        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDERPLANNINGHD: Planning"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);

        itemblk = mgr.newASPBlock("ITEM");

        itemblk.addField("ITEM_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk.addField("ITEM_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk.addField("ITEM_WO_NO").
        setDbName("WO_NO").
        setMandatory(). 
        setHidden();

        itemblk.addField("PLAN_LINE_NO","Number").
        setSize(8).
        setLabel("PCMWWORKORDERPLANNINGPLAN_LINE_NO: Plan Line No").
        setMaxLength(8);

        itemblk.addField("WORK_ORDER_COST_TYPE").
        setSize(8).
        setMandatory().
        setSelectBox().
        setCustomValidation("WORK_ORDER_COST_TYPE","WORK_ORDER_INVOICE_TYPE,COST").
        enumerateValues("WORK_ORDER_COST_TYPE_API").
        setLabel("PCMWWORKORDERPLANNINGWORK_ORDER_COST_TYPE: Work Order Cost Type").
        setMaxLength(20);

        itemblk.addField("DATE_CREATED","Date").
        setSize(12).
        setLabel("PCMWWORKORDERPLANNINGDATE_CREATED: Date Created");

        itemblk.addField("PLAN_DATE","Date").
        setSize(12).
        setLabel("PCMWWORKORDERPLANNINGPLAN_DATE: Plan Date");

        itemblk.addField("QUANTITY","Number").
        setSize(8). 
        setCustomValidation("CATALOG_NO,CATALOG_CONTRACT,WORK_ORDER_INVOICE_TYPE,QUANTITY,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE,COST,ORG_CODE","SALESPRICEAMOUNT,COSTAMOUNT,DISCOUNTED,COST").
        setLabel("PCMWWORKORDERPLANNINGQUANTITY: Quantity");

        /*itemblk.addField("QTY_TO_INVOICE","Number").
        setSize(8). 
                setCustomValidation("QTY_TO_INVOICE,CATALOG_NO,CATALOG_CONTRACT,PRICE_LIST_NO,QUANTITY,WORK_ORDER_INVOICE_TYPE,DISCOUNT,SALES_PRICE,CUSTOMER_NO,AGREEMENT_ID,ORG_CODE","SALESPRICEAMOUNT,PRICE_LIST_NO,SALES_PRICE,DISCOUNT,DISCOUNTED").
        setLabel("PCMWWORKORDERPLANNINGQTY_TO_INVOICE: Qty to Invoice");*/

        itemblk.addField("QTY_TO_INVOICE","Number").
        setSize(8). 
        setCustomValidation("QTY_TO_INVOICE,CATALOG_NO,CATALOG_CONTRACT,PRICE_LIST_NO,QUANTITY,WORK_ORDER_INVOICE_TYPE,DISCOUNT,SALES_PRICE,CUSTOMER_NO,AGREEMENT_ID,ORG_CODE","SALESPRICEAMOUNT,PRICE_LIST_NO,SALES_PRICE,DISCOUNT,DISCOUNTED").
        setLabel("PCMWWORKORDERPLANNINGQTY_TO_INVOICE: Qty to Invoice");

        itemblk.addField("CATALOG_CONTRACT").
        setSize(12).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setLabel("PCMWWORKORDERPLANNINGCATALOG_CONTRACT: Sales Part Site").
        setCustomValidation("CATALOG_NO,WORK_ORDER_COST_TYPE,COST,CATALOG_CONTRACT,QUANTITY,PRICE_LIST_NO,WORK_ORDER_INVOICE_TYPE,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE","COST,COSTAMOUNT,SALESPRICEAMOUNT,PRICE_LIST_NO,SALES_PRICE,DISCOUNT,DISCOUNTED").                                                              
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        itemblk.addField("CATALOG_NO").
        setSize(12).
        setLabel("PCMWWORKORDERPLANNINGCATALOG_NO: Sales Part Number").
        setUpperCase().
        setCustomValidation("CATALOG_NO,WORK_ORDER_COST_TYPE,COST,CATALOG_CONTRACT,QUANTITY,PRICE_LIST_NO,WORK_ORDER_INVOICE_TYPE,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE,CURRENCEY_CODE,CUSTOMER_NO,ORG_CODE,AGREEMENT_ID","COST,COSTAMOUNT,SALESPRICEAMOUNT,PRICE_LIST_NO,SALES_PRICE,DISCOUNT,DISCOUNTED,QUANTITY,LINE_DESCRIPTION").
        setMaxLength(25);

        itemblk.addField("LINE_DESCRIPTION").
        setSize(23).
        setDefaultNotVisible().
        setLabel("PCMWWORKLINEDESCRIPTION: Sales Part Description").
        setMaxLength(35);

        itemblk.addField("COST","Money").
        setSize(8).
        setCustomValidation("WORK_ORDER_INVOICE_TYPE,QUANTITY,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE,COST,WORK_ORDER_COST_TYPE","SALESPRICEAMOUNT,COSTAMOUNT,DISCOUNTED,QUANTITY").
        setLabel("PCMWWORKORDERPLANNINGCOST: Cost");

        itemblk.addField("COSTAMOUNT","Money","#.##").
        setSize(8).
        setFunction("QUANTITY*COST").
        setLabel("PCMWWORKORDERPLANNINGCOSTAMT: Cost Amount").
        setReadOnly(); 

        itemblk.addField("SALES_PRICE","Money","#.##").
        setSize(8).
        setCustomValidation("WORK_ORDER_INVOICE_TYPE,QUANTITY,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE","SALESPRICEAMOUNT,DISCOUNTED").
        setLabel("PCMWWORKORDERPLANNINGSALES_PRICE: Sales Price");

        itemblk.addField("DISCOUNT","Number").
        setSize(8).
        setCustomValidation("WORK_ORDER_INVOICE_TYPE,QUANTITY,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE","SALESPRICEAMOUNT,DISCOUNTED").
        setLabel("PCMWWORKORDERPLANNINGDISCOUNT: Discount");

        itemblk.addField("DISCOUNTED","Money","#.##").
        setSize(8).
        setFunction("''").
        setLabel("PCMWWORKORDERPLANNINGDISCOUNTED: Discounted Price").
        setReadOnly();

        itemblk.addField("SALESPRICEAMOUNT","Money","#.##").
        setSize(8).
        setFunction("''").
        setLabel("PCMWWORKORDERPLANNINGSALESPRICEAMOUNT: Price Amount").
        setReadOnly(); 

        itemblk.addField("PRICE_LIST_NO").
        setSize(8).
        setDynamicLOV("SALES_PRICE_LIST",600,445).
        setLabel("PCMWWORKORDERPLANNINGPRICE_LIST_NO: Price List No").
        setUpperCase().
        setMaxLength(10);

        itemblk.addField("ITEM_JOB_ID", "Number").
        setDbName("JOB_ID").
        setSize(8).
        setInsertable().
        setDefaultNotVisible().
        setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO").
        //setDynamicLOV("WORK_ORDER_JOB");
        setLabel("PCMWWORKORDERPLANNINGITEMJOBID: Job Id");

        itemblk.addField("WORK_ORDER_INVOICE_TYPE").
        setSize(8).
        setMandatory().
        setSelectBox().
        setCustomValidation("WORK_ORDER_INVOICE_TYPE,QUANTITY,DISCOUNT,SALES_PRICE,QTY_TO_INVOICE","SALESPRICEAMOUNT,DISCOUNTED").
        enumerateValues("WORK_ORDER_INVOICE_TYPE_API").
        setLabel("PCMWWORKORDERPLANNINGWORK_ORDER_INVOICE_TYPE: Work Order Invoice Type").
        setMaxLength(100);

        itemblk.addField("CLIENT_VAL").
        setFunction("''").
        setHidden();

        itemblk.addField("BASE_PRICE","Money").
        setHidden().
        setFunction("''");

        itemblk.addField("SALE_PRICE","Money").
        setHidden().
        setFunction("''"); 

        itemblk.addField("CURRENCY_RATE").
        setHidden().
        setFunction("''"); 

        itemblk.addField("ROW_NO").
        setHidden().
        setFunction("''");

        itemblk.addField("WORK_ORDER_COST_TYPE_DB").
        setHidden();

        itemblk.addField("ATTR").
        setHidden().
        setFunction("''");

        itemblk.addField("CLIENTVAL1").
        setFunction("''").
        setHidden();

        itemblk.addField("IS_AUTO_LINE","Number").
        setFunction("Work_Order_Planning_API.Is_Auto_External_Line(:ITEM_WO_NO,:PLAN_LINE_NO)").
        setHidden();

        itemblk.setView("WORK_ORDER_PLANNING");
        itemblk.defineCommand("Work_Order_Planning_API","New__,Modify__,Remove__");
        itemblk.setMasterBlock(headblk);
        itemset = itemblk.getASPRowSet();

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.defineCommand(itembar.NEWROW,"newRowITEM");

        itembar.defineCommand(itembar.SAVERETURN,"saveReturnItem","checkItemFields(-1)");
        itembar.defineCommand(itembar.SAVENEW,"saveNewItem","checkItemFields(-1)");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setTitle(mgr.translate("PCMWWORKORDERPLANNINGITM: Work Order Planning"));
        itemtbl.setWrap();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);


    }

    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        if ( itemset.countRows()>0)
        {
            String lineType = itemset.getValue("WORK_ORDER_COST_TYPE");
            if (mgr.isEmpty(lineType))
            {
                lineType = "XXXXX";
            }
            String lovwhere = "CONTRACT='"+itemset.getValue("CATALOG_CONTRACT")+"'";
            trans.clear();

            cmd = trans.addCustomFunction("CL1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");

            trans = mgr.perform(trans);

            String strMaterial = trans.getValue("CL1/DATA/CLIENTVAL1");

            if ( lineType.equals(strMaterial) )
                mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART_ACTIVE_LOV",600,445);
            else
                mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART_SERVICE_LOV",600,445); 

            mgr.getASPField("CATALOG_NO").setLOVProperty("WHERE",lovwhere);

            // Set the cost field read only for automatically generated external lines.
            if ("1".equals(itemset.getRow().getFieldValue("IS_AUTO_LINE")))
                mgr.getASPField("COST").setReadOnly();
        }

        if ( itemlay.isNewLayout() || itemlay.isEditLayout() )
        {
           headbar.disableCommand(headbar.DELETE);
           headbar.disableCommand(headbar.NEWROW);
           headbar.disableCommand(headbar.EDITROW);
           headbar.disableCommand(headbar.DELETE);
           headbar.disableCommand(headbar.DUPLICATEROW);
           headbar.disableCommand(headbar.FIND);
           headbar.disableCommand(headbar.BACKWARD);
           headbar.disableCommand(headbar.FORWARD);
        }
    }


    //===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERPLANNINGTITLE: Planning";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERPLANNINGTITLE: Planning";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (itemlay.isVisible())
            appendToHTML(itemlay.show());

        appendToHTML("<br>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
    }
}
