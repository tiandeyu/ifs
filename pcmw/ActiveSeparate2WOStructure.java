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
*  File        : ActiveSeparate2WOStructure.java 
*  Created     : ASP2JAVA Tool  010308  Created Using the ASP file ActiveSeparate2WOStructure.asp
*  Modified    :
*  SHFELK  010308  Corrected some conversion errors.
*  ARWILK  010912  Added materils tab to the form.
*  INROLK  011002  WorkOrderStructure is called after creating a WO. call id 69794.
*  BUNILK  021118  Added Object Site, Connection Type and Object description fields and replaced 
*                  work order site by Object site whenever it refer to Object.    
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040227  Web Alignment - Removed Unnecessary code blocks, removed unnecessary global variables.
*  THWILK  040604  Added PM_REVISION under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040607  Added lov for PM_NO to retrive PM_NO only under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040716  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  040901  Call ID: 117455, Modified adjust to support Navigator Button Click when the page is inside a frame.
*  NAMELK  041104  Duplicated Translation Tags Corrected.
*  NIJALK  041116  Replaced of Inventory_Part_Location_API methods from Inventory_Part_In_Stock_API methods
*  Chanlk  041231  Change the layout settings of material.
*  SHAFLK  050919  Bug 52880, Modified preDefine().
*  NIJALK  051004  Merged bug 52880.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NIJALK  051004  Merged bug 58216.
*  080205  CHANLK	 Bug 66842,Remove maxLength of PM_NO
*  ASSALK  080507  Bug 72214, Winglet merge.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparate2WOStructure extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2WOStructure");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
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

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;    

    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;

    private ASPField f;   

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private boolean repVal;
    private ASPTransactionBuffer secBuff;
    private ASPQuery q;
    private int headrowno;
    private ASPBuffer row;
    private String fldTitleWorkLeaderSign;
    private String fldTitleSign;
    private String fldTitlePartNo;
    private String fldTitleItem2CatalogNo;
    private String lovTitleWorkLeaderSign;
    private String lovTitleSign;
    private String lovTitlePartNo;
    private String lovTitleItem2CatalogNo;
    private String title_;
    private String xx;
    private String yy;
    private String isSecure[]=new String[6];  
    private ASPCommand cmd;  


    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparate2WOStructure(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        isSecure =  new String[2] ;

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        repVal = ctx.readFlag("REPVAL",false);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else
            clear();
        adjust();

        //ctx.writeFlag("SATIS",satis);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method,int ref)
    {
        isSecure = new String[6];  

        ASPManager mgr = getASPManager();

        isSecure[ref] = "false" ;
        String splitted[] = split(method,".");

        String first = splitted[0].toString();
        String Second = splitted[1].toString();

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(first,Second);
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
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void clear()
    {

        headset.clear();
        headtbl.clearQueryRow();
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM3();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURENODATA: No data found."));
            headset.clear();
        }
    }

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(headrowno);

        if (itemset0.countRows() > 0)
            setFieldValsItem0();
    }


    public void setFieldValsItem0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int n = itemset0.countRows();
        int i = 0;
        itemset0.first();
        double nBuyQtyDue = 0;

        for (i = 0; i <= n; ++i)
        {
            String catalogNo = itemset0.getRow().getValue("CATALOG_NO");
            String catContract =itemset0.getRow().getFieldValue("CATALOG_CONTRACT");
            String roleCode = itemset0.getRow().getValue("ROLE_CODE");
            String item0Contract = itemset0.getRow().getFieldValue("ITEM0_CONTRACT");
            String item0OrgCode = itemset0.getRow().getFieldValue("ITEM0_ORG_CODE");
            String masterContract = headset.getRow().getValue("CONTRACT");
            String masterOrgCode = headset.getRow().getValue("ORG_CODE");
            double planMen = itemset0.getRow().getNumberValue("PLAN_MEN");
            double planHours = itemset0.getRow().getNumberValue("ITEM0_PLAN_HRS");
            String customerNo = headset.getRow().getValue("CUSTOMER_NO");
            String agreementID = headset.getRow().getValue("AGREEMENT_ID");
            String priceListNo = itemset0.getRow().getValue("PRICE_LIST_NO");
            nBuyQtyDue = planMen + planHours;

            if (checksec("Sales_Part_API.Get_Cost",1))
            {
                cmd = trans.addCustomFunction("COSTA"+i,"Sales_Part_API.Get_Cost","COST");
                cmd.addParameter("CATALOG_CONTRACT",catContract);
                cmd.addParameter("CATALOG_NO",catalogNo);
            }

            cmd = trans.addCustomFunction("COSTB"+i,"Role_API.Get_Role_Cost","COST2");
            cmd.addParameter("ROLE_CODE",roleCode);

            cmd = trans.addCustomFunction("COSTC"+i,"Organization_API.Get_Org_Cost","COST3");
            cmd.addParameter("ITEM0_CONTRACT",item0Contract);
            cmd.addParameter("ITEM0_ORG_CODE",item0OrgCode);

            cmd = trans.addCustomFunction("COSTD"+i,"Organization_API.Get_Org_Cost","COST4");
            cmd.addParameter("CONTRACT",masterContract);
            cmd.addParameter("ORG_CODE",masterOrgCode);

            if (isNaN(nBuyQtyDue))
                nBuyQtyDue=0;
            cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("CATALOG_CONTRACT",catContract);
            cmd.addParameter("CATALOG_NO",catalogNo);
            cmd.addParameter("CUSTOMER_NO",customerNo);
            cmd.addParameter("AGREEMENT_ID",agreementID);
            cmd.addParameter("PRICE_LIST_NO",priceListNo);
            cmd.addParameter("QTY1",String.valueOf(nBuyQtyDue));

            itemset0.next();
        }

        trans = mgr.validate(trans);
        itemset0.first();

        for (i = 0; i <= n; ++i)
        {
            double numCost = NOT_A_NUMBER;
            double numCostAmt = NOT_A_NUMBER;
            double plannedMen = itemset0.getRow().getNumberValue("PLAN_MEN");
            double plannedHours = itemset0.getRow().getNumberValue("ITEM0_PLAN_HRS");

            row = itemset0.getRow();
            if (!mgr.isEmpty(itemset0.getRow().getValue("CATALOG_NO")))
            {
                if (isSecure[1] =="true")
                {
                    numCost= trans.getNumberValue("COSTA" + i + "/DATA/COST");
                    numCostAmt =(numCost * plannedMen * plannedHours);
                }
                else
                {
                    numCost = NOT_A_NUMBER;
                    numCostAmt = NOT_A_NUMBER;
                }
            }

            if (isNaN(numCost) && !mgr.isEmpty(itemset0.getRow().getValue("ROLE_CODE")))
            {
                numCost= trans.getNumberValue("COSTB" + i + "/DATA/COST2");
                numCostAmt =(numCost * plannedMen * plannedHours);
            }

            if (isNaN(numCost) && !mgr.isEmpty(itemset0.getRow().getFieldValue("ITEM0_ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTC" + i + "/DATA/COST3");
                numCostAmt =(numCost * plannedMen * plannedHours);
            }

            if (isNaN(numCost) && !mgr.isEmpty(headset.getRow().getFieldValue("ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTD" + i + "/DATA/COST4"); 
                numCostAmt =(numCost * plannedMen * plannedHours); 
            }

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO" + i + "/DATA/BASE_PRICE");
            double nSaleUnitPrice = trans.getNumberValue("PRICEINFO" + i + "/DATA/SALE_PRICE");
            double nDiscount = trans.getNumberValue("PRICEINFO" + i + "/DATA/DISCOUNT");
            String sPriceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO"); 
            nBuyQtyDue = plannedMen + plannedHours;
            double nsalePriceAmt = nSaleUnitPrice * nBuyQtyDue;

            if (mgr.isEmpty(itemset0.getRow().getValue("PRICE_LIST_NO")))
                row.setValue("PRICE_LIST_NO", sPriceListNo);


            row.setNumberValue("DISCOUNT", nDiscount);
            row.setNumberValue("SALESPRICEAMOUNT", nsalePriceAmt);     
            row.setNumberValue("LISTPRICE", nBaseUnitPrice);     
            row.setNumberValue("COST", numCost);
            row.setNumberValue("COSTAMOUNT", numCostAmt);
            itemset0.setRow(row);

            itemset0.next();
        }

        itemset0.first();
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk1);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURENODATA: No data found."));
            itemset1.clear();
        }


        headset.goTo(headrowno);
    }   

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        if (itemset3.countRows() > 0)
        {
            trans.clear();
            int headsetRowNo = headset.getCurrentRowNo();
            int item1rowno = itemset1.getCurrentRowNo();
            q = trans.addQuery(itemblk2);
            q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
            q.addParameter("ITEM1_WO_NO",itemset1.getRow().getFieldValue("ITEM1_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset1.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans);
            headset.goTo(headsetRowNo);
            itemset1.goTo(item1rowno);

            if (itemset2.countRows() > 0)
                setFieldValsItem2();
        }
    }       

    public void setFieldValsItem2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int n = itemset2.countRows();
        int i = 0;
        double nBuyQtyDue = 0;
        itemset2.first();


        for (i = 0; i <= n; ++i)
        {

            String catalogNo = itemset2.getRow().getFieldValue("ITEM2_CATALOG_NO");
            String catContract = itemset2.getRow().getFieldValue("ITEM2_CATALOG_CONTRACT");
            String customerNo = headset.getRow().getValue("CUSTOMER_NO");
            String agreementID = headset.getRow().getValue("AGREEMENT_ID");
            String priceListNo = itemset2.getRow().getFieldValue("ITEM2_PRICE_LIST_NO");
            nBuyQtyDue = itemset2.getRow().getNumberValue("PLAN_QTY");

            if (isNaN(nBuyQtyDue))
                nBuyQtyDue = 0;

            cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("CATALOG_CONTRACT",catContract);
            cmd.addParameter("CATALOG_NO",catalogNo);
            cmd.addParameter("CUSTOMER_NO",customerNo);
            cmd.addParameter("AGREEMENT_ID",agreementID);
            cmd.addParameter("PRICE_LIST_NO",priceListNo);
            cmd.addParameter("PLAN_QTY",String.valueOf(nBuyQtyDue));

            itemset2.next();
        }

        trans = mgr.validate(trans);
        itemset2.first();

        for (i = 0; i <= n; ++i)
        {
            row = itemset2.getRow();

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
            double nSaleUnitPrice= trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            double nDiscount= trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            nBuyQtyDue = itemset2.getRow().getNumberValue("PLAN_QTY");
            double nsalePriceAmt = nSaleUnitPrice * nBuyQtyDue;

            row.setNumberValue("ITEM2_DISCOUNT",nDiscount);
            row.setNumberValue("ITEM2_SALESPRICEAMOUNT",nsalePriceAmt);      
            row.setNumberValue("ITEM2_LISTPRICE",nBaseUnitPrice);
            itemset2.setRow(row);

            itemset2.next();
        }

        itemset2.first();              
    }  

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(itemblk3);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(headrowno);

        if (itemset3.countRows() > 0)
            setFieldValsItem3();
    }

    public void setFieldValsItem3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int n = itemset3.countRows();
        int i = 0;
        itemset3.first();
        double nBuyQtyDue = 0;
        double nsalePriceAmt = 0;

        for (i = 0; i <= n; ++i)
        {
            String catalogNo = itemset3.getRow().getFieldValue("ITEM3_CATALOG_NO");
            String catContract =itemset3.getRow().getFieldValue("ITEM3_CATALOG_CONTRACT");
            String headContract = headset.getRow().getValue("CONTRACT");
            String headOrgCode = headset.getRow().getValue("ORG_CODE");
            String customerNo = headset.getRow().getValue("CUSTOMER_NO");
            String agreementID = headset.getRow().getValue("AGREEMENT_ID");
            String priceListNo = itemset3.getRow().getFieldValue("ITEM3_PRICE_LIST_NO");
            nBuyQtyDue = itemset3.getRow().getNumberValue("QUANTITY");
            String workOrderInvoiceType = itemset3.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

            if (checksec("Purchase_Part_Supplier_API.Get_Unit_Meas",1))
            {
                cmd = trans.addCustomFunction("COSTA"+i,"Sales_Part_API.Get_Cost","COST");
                cmd.addParameter("CATALOG_CONTRACT",catContract);
                cmd.addParameter("CATALOG_NO",catalogNo);
            }

            cmd = trans.addCustomFunction("COSTC"+i,"Organization_API.Get_Org_Cost","COST3");
            cmd.addParameter("CONTRACT",headContract);
            cmd.addParameter("ORG_CODE",headOrgCode);

            if (isNaN(nBuyQtyDue))
                nBuyQtyDue=0;

            cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("ITEM3_CATALOG_CONTRACT",catContract);
            cmd.addParameter("ITEM3_CATALOG_NO",catalogNo);
            cmd.addParameter("CUSTOMER_NO",customerNo);
            cmd.addParameter("AGREEMENT_ID",agreementID);
            cmd.addParameter("PRICE_LIST_NO",priceListNo);
            cmd.addParameter("QUANTITY",String.valueOf(nBuyQtyDue));

            cmd = trans.addCustomFunction("WOINVOTYPE"+i,"WORK_ORDER_INVOICE_TYPE_API.Encode","WORK_ORDER_INVOICE_TYPE");
            cmd.addParameter("WORK_ORDER_INVOICE_TYPE",workOrderInvoiceType);

            itemset3.next();
        }

        trans = mgr.validate(trans);
        itemset3.first();

        for (i=0; i<=n; ++i)
        {
            double numCost = NOT_A_NUMBER;
            double numCostAmount = NOT_A_NUMBER;
            double nQty = itemset3.getRow().getNumberValue("QUANTITY");
            double nQtyInv = itemset3.getRow().getNumberValue("QTY_TO_INVOICE");

            row = itemset3.getRow();

            if (!mgr.isEmpty(itemset3.getRow().getFieldValue("ITEM3_CATALOG_NO")))
            {
                if (isSecure[1] =="true")
                {
                    numCost= trans.getNumberValue("COSTA"+i+"/DATA/COST");
                    numCostAmount = numCost*nQty;
                }
                else
                {
                    numCost =NOT_A_NUMBER;
                    numCostAmount = NOT_A_NUMBER;
                }
            }
            if (isNaN(numCost) && !mgr.isEmpty(headset.getRow().getValue("ORG_CODE")))
            {
                numCost = trans.getNumberValue("COSTC"+i+"/DATA/COST3");
                numCostAmount = numCost*nQty;
            }

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
            double nSaleUnitPrice= trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            double nDiscount= trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            nBuyQtyDue = itemset3.getRow().getNumberValue("QUANTITY");
            String sWorkOrerInvoiceType = trans.getValue("WOINVOTYPE"+i+"/DATA/WORK_ORDER_INVOICE_TYPE");

            if ("AR".equals(sWorkOrerInvoiceType))
            {
                if (nDiscount == 0)
                    nsalePriceAmt = nBaseUnitPrice * nQty;
                else
                {
                    nsalePriceAmt = nBaseUnitPrice * nQty;
                    nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                }  
            }

            else if ("MINQ".equals(sWorkOrerInvoiceType))
            {
                if (nQty > nQtyInv)
                {
                    if (nDiscount == 0)
                        nsalePriceAmt = nBaseUnitPrice * nQty;
                    else
                    {
                        nsalePriceAmt = nBaseUnitPrice * nQty;
                        nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                    }  
                }
                else
                {
                    if (nDiscount == 0)
                        nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                    else
                    {
                        nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                        nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                    }   
                }
            }

            else if ("MAXQ".equals(sWorkOrerInvoiceType))
            {
                if (nQty < nQtyInv)
                {
                    if (nDiscount == 0)
                        nsalePriceAmt = nBaseUnitPrice * nQty;
                    else
                    {
                        nsalePriceAmt = nBaseUnitPrice * nQty;
                        nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                    }           
                }
                else
                {
                    if (nDiscount == 0)
                        nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                    else
                    {
                        nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                        nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                    }
                }
            }

            else if ("FL".equals(sWorkOrerInvoiceType))
            {
                if (nDiscount == 0)
                    nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                else
                {
                    nsalePriceAmt = nBaseUnitPrice * nQtyInv;
                    nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                }
            }

            row.setNumberValue("ITEM3_COST",numCost);
            row.setNumberValue("ITEM3_COSTAMOUNT",numCostAmount);
            row.setNumberValue("ITEM3_DISCOUNT",nDiscount);
            row.setNumberValue("ITEM3_SALESPRICEAMOUNT",nsalePriceAmt);      
            row.setNumberValue("ITEM3_LISTPRICE",nBaseUnitPrice);

            itemset3.setRow(row);

            itemset3.next();
        }

        itemset3.first(); 
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(headrowno);
    }     

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);
        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
    }

    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("ITEM1_WO_NO",itemset1.getRow().getFieldValue("ITEM1_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset1.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        mgr.submit(trans);
        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
    }    

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
        headset.goTo(headrowno);  
    }

//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURENONE: No RMB method has been selected."));
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

        headblk.addField("WO_NO","Number","#").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREWONO: WO No").
        setReadOnly().
        setHilite().
        setMaxLength(8);

        headblk.addField("COMPANY").
        setHidden();

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECONTRACT: Wo Site").
        setDefaultNotVisible().
        setUpperCase().
        setHilite().
        setMaxLength(5);

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREMCH_CODECONTRACT: Site").
        setDefaultNotVisible().
        setHilite().
        setMaxLength(5);

        headblk.addField("CONNECTION_TYPE").
        setSize(20).
        setSelectBox().
        setReadOnly().
        setHilite().    
        enumerateValues("MAINT_CONNECTION_TYPE_API").
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECONNECTIONTYPE: Connection Type");

        headblk.addField("MCH_CODE").
        setSize(20).
        setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREMCHCODE: Object ID").
        setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN").
        setUpperCase().
        setHilite().
        setMaxLength(100); 

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREOBJDESC: Object Description").
        setDefaultNotVisible().
        setReadOnly().
        setHilite().
        setMaxLength(2000);

        headblk.addField("STATE").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESTATE: Status").
        setReadOnly().
        setHilite().
        setMaxLength(30); 

        headblk.addField("ORG_CODE").
        setSize(20).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREEXEDEPT: Exec Dept").
        setUpperCase().
        setHilite().
        setMaxLength(8);

        headblk.addField("ORGCODEDESCR").
        setSize(30).
        setFunction("Organization_Api.Get_Description(:CONTRACT,:ORG_CODE)").
        setReadOnly().
        setHilite().
        setDefaultNotVisible();

        headblk.addField("WORK_LEADER_SIGN").
        setSize(12).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREWORKLEADSIGN: Work Leader").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20); 

        headblk.addField("ERR_DESCR").
        setSize(30).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREERRDESCR: Directive").
        setHilite().
        setMaxLength(60); 

        headblk.addField("PRIORITY_ID").
        setSize(8).
        setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPRIORITYID: Prio").
        setUpperCase().
        setMaxLength(1); 

        headblk.addField("OP_STATUS_ID").
        setSize(18).
        setDynamicLOV("OPERATIONAL_STATUS",600,450).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREOPSTATUS: Operational Status").
        setUpperCase().
        setMaxLength(3);

        headblk.addField("PLAN_S_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPLANSDATE: Planned Start");

        headblk.addField("PLAN_F_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPLANFDATE: Planned Completion");

        headblk.addField("WORK_TYPE_ID").
        setSize(8).
        setDefaultNotVisible().
        setDynamicLOV("WORK_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREWORKTYPEID: Work Type").
        setUpperCase().
        setMaxLength(2); 

        headblk.addField("PM_NO","Number").
        setSize(8). 
        setLOV("PmActionLov1.page",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPMNO: PM No").
        setReadOnly(); 

        headblk.addField("PM_REVISION").
        setSize(8). 
        setDynamicLOV("PM_ACTION","PM_NO",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPMREV: PM Revision").
        setReadOnly().
        setMaxLength(6); 

        headblk.addField("AGREEMENT_ID").
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREAGREEMENTID: Agreement ID").
        setSize(10). 
        setDefaultNotVisible().
        setReadOnly();

        headblk.addField("CUSTOMER_NO").
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECUSTOMERNO: Customer No").
        setSize(20). 
        setDefaultNotVisible().  
        setHilite().
        setReadOnly();

        headblk.addField("CUSTODESCR").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECUSTOMERDESC: Customer Name").
        setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)").
        setReadOnly().
        setHilite().
        setDefaultNotVisible();    

        headblk.addField("REPIN").
        setFunction("''").
        setHidden(); 

        headblk.addField("OBJEVENTS").
        setHidden();

        // Bug 72214, start
        f = headblk.addField("SCHEDULING_CONSTRAINTS");
        f.setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESCHEDCONST: Scheduling Constraints");
        f.setSize(35);
        f.setReadOnly();
        // Bug 72214, end

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.FIND); 
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);

        headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
	// Bug 72214, start
        headlay.defineGroup("","MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CONNECTION_TYPE,ERR_DESCR,WO_NO,CONTRACT,STATE,ORG_CODE,ORGCODEDESCR,CUSTOMER_NO,CUSTODESCR,AGREEMENT_ID,PRIORITY_ID,WORK_LEADER_SIGN,WORK_TYPE_ID,PM_NO,PM_REVISION,OP_STATUS_ID,PLAN_S_DATE,PLAN_F_DATE,SCHEDULING_CONSTRAINTS",false,true);
        // Bug 72214, end

        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("ORGCODEDESCR");
        headlay.setSimple("CUSTODESCR");

        //-----------------------------------------------------------------------
        //-------------- This part belongs to CRAFTS ------------------------
        //-----------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField("ITEM0_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk0.addField("ITEM0_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk0.addField("ROW_NO","Number").
        setSize(8).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREROWNO: Row No").
        setHidden().
        setReadOnly();

        itemblk0.addField("ITEM0_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM0CONTRACT: Site").
        setUpperCase().
        setDbName("CONTRACT").
        setReadOnly().
        setMaxLength(5);

        itemblk0.addField("ITEM0_COMPANY").
        setSize(8).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM0COMPANY: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk0.addField("ITEM0_DESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3DESCRIPTION: Description").
        setDbName("DESCRIPTION").
        setMaxLength(60);

        itemblk0.addField("ITEM0_ORG_CODE").
        setSize(11).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITEM0ORCOLOV: List of Maintenance Organization")).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM0ORGCODE: Maintenance Organization").
        setUpperCase().
        setHidden().
        setDbName("ORG_CODE").
        setMaxLength(8);  

        itemblk0.addField("ROLE_CODE").
        setSize(9).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREROLECODE: Craft ID").
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREROLECODELOV: List of Craft ID")).
        setUpperCase().
        setMaxLength(10);

        itemblk0.addField("SIGN").
        setSize(10).
        setDynamicLOV("EMPLOYEE_LOV","ITEM0_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESIGN: Signature").
        setUpperCase().
        setMaxLength(20);  

        itemblk0.addField("PLAN_MEN","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPLANMEN: Planned Men");

        itemblk0.addField("ITEM0_PLAN_HRS","Number").
        setSize(14).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM0PLANHRS: Planned Hours").
        setDbName("PLAN_HRS");  

        itemblk0.addField("CATALOG_CONTRACT").
        setSize(15).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECATACONTRACT: Sales Part Site").
        setUpperCase().
        setMaxLength(5);  

        itemblk0.addField("CATALOG_NO").
        setSize(17).
        setDynamicLOV("SALES_PART_SERVICE_LOV","CATALOG_CONTRACT CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECATOGNO: Sales Part Number").
        setUpperCase().
        setMaxLength(25);

        itemblk0.addField("SALESPARTDESC").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESALESPARDESC: Sales Part Description").
        setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)").
        setReadOnly().
        setMaxLength(2000);

        itemblk0.addField("PRICE_LIST_NO").
        setSize(13).
        setDynamicLOV("SALES_PRICE_LIST",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPRICELISTNO: Price List No").
        setUpperCase().
        setHidden().
        setMaxLength(10);  

        itemblk0.addField("COST","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECOSTITEM0: Cost").
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("COSTAMOUNT","Number").
        setHidden().
        setFunction("'").
        setReadOnly();  

        itemblk0.addField("LISTPRICE", "Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURELISTPRICE: Sales Price").
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("DISCOUNT").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREDISCONT: Discount %").
        setFunction("''");


        itemblk0.addField("SALESPRICEAMOUNT", "Number").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk0.addField("DATE_FROM","Datetime").
        setHidden();

        itemblk0.addField("DATE_TO","Datetime").
        setHidden();

        itemblk0.addField("TOOLS").
        setHidden();

        itemblk0.addField("REMARK").
        setHidden();

        itemblk0.addField("COST2").
        setHidden().
        setFunction("''");

        itemblk0.addField("COST3").
        setHidden().  
        setFunction("''");

        itemblk0.addField("COST4").
        setHidden().  
        setFunction("''");

        itemblk0.addField("BASE_PRICE","Number").
        setFunction("''").
        setHidden();

        itemblk0.addField("SALE_PRICE","Number").
        setFunction("''").
        setHidden(); 

        itemblk0.addField("CURRENCY_RATE","Number").
        setFunction("''").
        setHidden();

        itemblk0.addField("QTY1","Number").
        setFunction("''").
        setHidden(); 

        itemblk0.setView("WORK_ORDER_ROLE");
        itemblk0.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);  

        itembar0.disableCommand(itembar0.DELETE);
        itembar0.disableCommand(itembar0.EDITROW);
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITM0: Crafts"));
        itemtbl0.setWrap();

        itemblk0.setTitle(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURECRAFT: Crafts"));
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);    

        //-----------------------------------------------------------------------
        //-------------- This part belongs to MATERIALS TAB -----------------------
        //-----------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("ITEM1_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = itemblk1.addField("ITEM1_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");

        f = itemblk1.addField("MAINT_MATERIAL_ORDER_NO");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk1.addField("ITEM1_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setCustomValidation("ITEM1_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM1_CONTRACT,ITEM1_COMPANY,MCHCODE,ITEM1DESCRIPTION");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTWO_NO: Workorder No");

        f = itemblk1.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTMCHCODE: Object ID");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1DESCRIPTION");
        f.setSize(30);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("SIGNATURE");
        f.setSize(8);
        f.setMaxLength(2000);
        f.setCustomValidation("SIGNATURE,ITEM1_COMPANY","SIGNATURE_ID,SIGNATURENAME");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk1.addField("SIGNATURENAME");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM1_CONTRACT),:SIGNATURE_ID)");

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM1CONTRACT: Site");
        f.setUpperCase();

        f = itemblk1.addField("ENTERED","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTENTERED: Entered");

        f = itemblk1.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setCustomValidation("INT_DESTINATION_ID,ITEM1_CONTRACT","INT_DESTINATION_DESC");
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTINT_DESTINATION_ID: Int Destination");

        f = itemblk1.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk1.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTDUE_DATE: Due Date");

        f = itemblk1.addField("ITEM1_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSTATE: Status");

        f = itemblk1.addField("NREQUISITIONVALUE", "Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("SNOTETEXT");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("SIGNATURE_ID");
        f.setReadOnly();
        f.setHidden();
        f.setUpperCase();

        f = itemblk1.addField("NNOTEID", "Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ORDERCLASS");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("SNOTEIDEXIST");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ITEM1_COMPANY");
        f.setSize(6);
        f.setHidden();
        f.setDbName("COMPANY");
        f.setFunction("Site_API.Get_Company(:ITEM1_CONTRACT)");
        f.setMaxLength(20);
        f.setUpperCase();

        f = itemblk1.addField("NPREACCOUNTINGID", "Number");
        f.setHidden();
        f.setFunction("''");

        itemblk1.setView("MAINT_MATERIAL_REQUISITION");    
        itemblk1.defineCommand("MAINT_MATERIAL_REQUISITION_API","New__,Modify__,Remove__,PLAN__,RELEASE__,CLOSE__");
        itemblk1.setTitle(mgr.translate("PCMWACTIVESEPARATE2LIGHTITM3: Materials"));

        itemset1 = itemblk1.getASPRowSet();    

        itembar1 = mgr.newASPCommandBar(itemblk1);    

        itembar1.enableCommand(itembar1.FIND);
        itembar1.disableCommand(itembar1.DELETE);
        itembar1.disableCommand(itembar1.EDITROW);
        itembar1.disableCommand(itembar1.NEWROW); 
        itembar1.disableCommand(itembar1.DUPLICATEROW);    
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setSimple("INT_DESTINATION_DESC");
        itemlay1.setSimple("SIGNATURENAME");       itemblk1.setMasterBlock(headblk);

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setTitle(mgr.translate("PCMWACTIVESEPARATE2LIGHTITM3: Materials"));       
        itemtbl1.setWrap();               

        //-----------------------------------------------------------------------
        //-------------- This part belongs to MATERIALS  -----------------------
        //-----------------------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");

        itemblk2.addField("ITEM2_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk2.addField("ITEM2_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk2.addField("LINE_ITEM_NO","Number").
        setSize(6).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURELINEITMNO: Row No").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("PART_NO").
        setSize(11).
        setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,450).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPARTNO: Part No").
        setUpperCase().
        setReadOnly().
        setInsertable().
        setMaxLength(25); 

        itemblk2.addField("PARTDESCRIPTION").
        setSize(17).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPARTSDESCRIPTION: Part Description").
        setFunction("PURCHASE_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)").
        setReadOnly().
        setMaxLength(2000); 

        itemblk2.addField("SPARE_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURESPARECONTLOV: List of Site")).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESPARECONTCT: Site").
        setUpperCase().
        setReadOnly().
        setHidden().
        setInsertable().
        setMaxLength(5); 

        itemblk2.addField("CONDITION_CODE").
        setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Codition_Code(:MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)").
        setHidden();

        itemblk2.addField("HASSPARESTRUCTURE").
        setSize(10).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREHASARESTRUCT: Structure").
        setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)").
        setReadOnly().
        setHidden().   
        setMaxLength(2000); 

        itemblk2.addField("DIMQTY").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREDIMQTY: Dimension/Quality").
        setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)").
        setReadOnly().
        setHidden(). 
        setMaxLength(2000); 

        itemblk2.addField("TYPEDESIGN").
        setSize(17).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURETYPEDESIGN: Type Designation").
        setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)").
        setReadOnly().
        setHidden().
        setMaxLength(2000);

        itemblk2.addField("DATE_REQUIRED","Date").
        setSize(14).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREDATEREQ: Date Required");

        itemblk2.addField("PLAN_QTY","Number").
        setSize(18).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYREQUID: Quantity Required"); 

        itemblk2.addField("QTY","Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYISSUED: Quantity Issued").
        setReadOnly(); 

        itemblk2.addField("QTYONHAND","Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYONHAND: Quantity on Hand").
        setFunction("INVENTORY_PART_IN_STOCK_API.Get_Inventory_Qty_Onhand(:SPARE_CONTRACT,:PART_NO,NULL)").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("QTY_ASSIGNED","Number").
        setSize(18).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYASSID: Quantity Assigned").
        setReadOnly(); 

        itemblk2.addField("QTY_RETURNED","Number").
        setSize(13).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYRETURN: Quantity Returned").
        setHidden().
        setReadOnly();  

        itemblk2.addField("QTY_SHORT","Number").
        setSize(13).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYSHORT: Quantity Short").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("UNITMEAS").
        setSize(7).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREUNITMEAS: Unit").
        setFunction("PURCHASE_PART_SUPPLIER_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)").
        setReadOnly().
        setHidden().
        setMaxLength(2000); 

        itemblk2.addField("ITEM2_CATALOG_CONTRACT").
        setSize(16). 
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3CATCONTR: Sales Part Site").
        setUpperCase().
        setDbName("CATALOG_CONTRACT").
        setHidden().
        setMaxLength(5);

        itemblk2.addField("ITEM2_CATALOG_NO").
        setSize(17).
        setDynamicLOV("SALES_PART_ACTIVE_LOV","ITEM2_CATALOG_CONTRACT CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3CATANO: Sales Part Number").
        setUpperCase().
        setDbName("CATALOG_NO").
        setMaxLength(25); 

        itemblk2.addField("ITEM2_CATALOGDESC").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM2CATALDESC: Sales Part Description").
        setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)").
        setReadOnly().
        setMaxLength(2000); 

        itemblk2.addField("ITEM2_PRICE_LIST_NO").
        setSize(14).   
        setDynamicLOV("SALES_PRICE_LIST",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3PRICELISTLOV: List of Price List No")).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3PRICELST: Price List No").
        setUpperCase().
        setHidden().
        setDbName("PRICE_LIST_NO").
        setMaxLength(10);

        itemblk2.addField("ITEM2_COST","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECOSTITEM2: Cost").
        //setFunction("INVENTORY_PART_API.Get_Inventory_Value_By_Method(:SPARE_CONTRACT,:PART_NO)"). 
        setFunction("Active_Separate_API.Get_Invent_Value(:SPARE_CONTRACT,:PART_NO,NULL,'*',:CONDITION_CODE)").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("ITEM2_COSTAMOUNT","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECOSTITEMAMT: Cost Amount").
        //setFunction("PLAN_QTY*INVENTORY_PART_API.Get_Inventory_Value_By_Method(:SPARE_CONTRACT,:PART_NO)").
        setFunction("PLAN_QTY*Active_Separate_API.Get_Invent_Value(:SPARE_CONTRACT,:PART_NO,NULL,'*',:CONDITION_CODE)").
        setReadOnly(); 

        itemblk2.addField("ITEM2_LISTPRICE", "Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM2LISTPRICE: Sales Price").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk2.addField("ITEM2_DISCOUNT").
        setFunction("''").
        setSize(11).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM2DISCOUNT: Discount %");

        itemblk2.addField("ITEM2_SALESPRICEAMOUNT", "Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESALESPRICEAMOUNTITEM2: Sales Price Amount").
        setFunction("''").
        setReadOnly();

        itemblk2.addField("SERIAL_NO").
        setFunction("''").
        setHidden();

        itemblk2.addField("CONFIGURATION_ID").
        setFunction("''").
        setHidden();
        
        itemblk2.setView("MAINT_MATERIAL_REQ_LINE");
        itemblk2.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
        itemblk2.setMasterBlock(itemblk1);

        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);  

        itembar2.disableCommand(itembar2.DELETE);
        itembar2.disableCommand(itembar2.EDITROW);
        itembar2.disableCommand(itembar2.NEWROW);
        itembar2.disableCommand(itembar2.DUPLICATEROW);
        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.SAVERETURN,null,"checkItem2Fields(-1)");
        itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();              

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);     

        //-----------------------------------------------------------------------
        //-------------- This part belongs to PLANNED(EXTERNAL COST)-------------
        //-----------------------------------------------------------------------

        itemblk3 = mgr.newASPBlock("ITEM3");

        itemblk3.addField("ITEM3_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk3.addField("ITEM3_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk3.addField("PLAN_LINE_NO").
        setSize(8).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPLANLINENO: Plan Line No").
        setHidden().
        setReadOnly(); 

        itemblk3.addField("WORK_ORDER_COST_TYPE").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREWORKORDCOSTTYPE: Work Order Cost Type").
        setMaxLength(200);

        itemblk3.addField("DATE_CREATED","Date").
        setSize(14).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREDATECRE: Date Created");

        itemblk3.addField("PLAN_DATE","Date").
        setSize(14).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREPLANDATE: Plan Date");

        itemblk3.addField("QUANTITY","Number").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTY: Quantity"); 

        itemblk3.addField("QTY_TO_INVOICE","Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREQTYTOINVOICE: Quantity To Invoice"); 

        itemblk3.addField("ITEM3_CATALOG_CONTRACT").
        setSize(15).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECATALOGCONT: Sales Part Site").
        setDbName("CATALOG_CONTRACT");

        itemblk3.addField("ITEM3_CATALOG_NO").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECATALOGNO: Sales Part Number").
        setDbName("CATALOG_NO");

        itemblk3.addField("ITEM3_CATALOGDESC").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3CATALDESC: Sales Part Description").
        setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)").
        setReadOnly().
        setMaxLength(2000);  

        itemblk3.addField("ITEM3_PRICE_LIST_NO").
        setSize(13).
        setDynamicLOV("SALES_PRICE_LIST",600,450).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3_PRICELISTNO: Price List No").
        setUpperCase().
        setHidden().
        setMaxLength(10).
        setDbName("PRICE_LIST_NO");

        itemblk3.addField("ITEM3_COST","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECOSTITEM3: Cost").
        setFunction("''").
        setHidden().
        setReadOnly(); 

        itemblk3.addField("ITEM3_COSTAMOUNT","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURECOSTITEM3AMT: Cost Amount").
        setFunction("''").
        setReadOnly(); 

        itemblk3.addField("ITEM3_LISTPRICE", "Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3LISTPRICE: Sales Price").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk3.addField("ITEM3_DISCOUNT").
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREITEM3DISCOUNT: Discount %").
        setHidden().
        setFunction("''");

        itemblk3.addField("ITEM3_SALESPRICEAMOUNT", "Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTURESALESPRICEAMOUNTITEM3: Sales Price Amount").
        setFunction("''").
        setReadOnly();  

        itemblk3.addField("WORK_ORDER_INVOICE_TYPE").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2WOSTRUCTUREWORKORDINVOICETYPE: Work OrderInvoice Type").
        setMaxLength(200);

        itemblk3.setView("WORK_ORDER_PLANNING");
        itemblk3.defineCommand("WORK_ORDER_PLANNING_API","New__,Modify__,Remove__");
        itemblk3.setMasterBlock(headblk);

        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);  

        itembar3.disableCommand(itembar3.DELETE);
        itembar3.disableCommand(itembar3.EDITROW);
        itembar3.disableCommand(itembar3.NEWROW); 
        itembar3.disableCommand(itembar3.DUPLICATEROW);
        itembar3.enableCommand(itembar3.FIND);
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields(-1)");
        itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setTitle(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITM2: Planning"));
        itemtbl3.setWrap();

        itemblk3.setTitle(mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITM2: Planning"));
        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);   
    }

    private String modifiedStartPres(ASPManager mgr,String myStartPresentation)
    {
        int defInd = myStartPresentation.indexOf("Default.");

        if (defInd != -1)
        {
            int endInd = myStartPresentation.indexOf(">",defInd);
            String ext = myStartPresentation.substring(defInd+7,endInd);
            String oldStr = "Default"+ext;
            String newStr = "Default"+ext+" target=\"_parent\""; 
            myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
        }

        int navInd = myStartPresentation.indexOf("Navigator.");

        if (navInd != -1)
        {
            int endInd = myStartPresentation.indexOf(">",navInd);
            String ext = myStartPresentation.substring(navInd+9,endInd);
            String oldStr = "Navigator"+ext;
            String newStr = "Navigator"+ext+" target=\"_parent\""; 
            myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
        }

        return myStartPresentation;
    }

    private String modifiedEndPres(ASPManager mgr,String myEndPresentation)
    {
        myEndPresentation = mgr.replace(myEndPresentation,"document.location","parent.location");
        return myEndPresentation;
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);

        fldTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREWORKLEADERFLD: Work+Leader");     
        fldTitleSign = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURESIGNFLD: Signature");     
        fldTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREPARTNOFLD: Part+No");     
        fldTitleItem2CatalogNo = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITEM2CATNOFLD: Sales+Part+Number"); 

        lovTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREWORKLEADERLOV: List+of+Work+Leader");     
        lovTitleSign = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTURESIGNLOV: List+of+Signature");     
        lovTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREPARTNOLOV: List+of+Part+No");     
        lovTitleItem2CatalogNo = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREITEM2CATNOLOV: List+of+Sales+Part+Number"); 

        if (headset.countRows() > 0)
        {
            xx = headset.getRow().getValue("WO_NO");
            yy = headset.getRow().getValue("ERR_DESCR");
            title_ = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information")+" - " + xx+"  "+yy ;

        }
        else
        {
            title_ = mgr.translate("PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information");
            xx = "";
            yy = "";
        }  
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return  "PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information";
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();

        out.append("<html>\n");
        out.append("<head>\n");
        out.append(mgr.generateHeadTag("PCMWACTIVESEPARATE2WOSTRUCTUREWOINF: Work Order Information"));
        out.append("</head>									\n");
        out.append("<body ");
        out.append( mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(modifiedStartPres(mgr,mgr.startPresentation(title_)));

        if (headlay.isVisible())
            out.append(headlay.show());

        if (headset.countRows() != 0)
        {
            if (!itemlay2.isVisible())
            {
                if (itemlay0.isFindLayout())
                {
                    out.append(headlay.show());
                    out.append(itemlay0.show());
                }

                if (itemlay0.isVisible() && !(itemlay0.isFindLayout()))
                    out.append(itemlay0.show());
            }

            if (itemlay1.isFindLayout())
            {
                out.append(headlay.show());
                out.append(itemlay1.show());
            }

            if (itemlay1.isVisible() && !(itemlay1.isFindLayout()))
                out.append(itemlay1.show());

            if (itemset1.countRows() != 0)
            {
                if (itemlay2.isFindLayout())
                {
                    out.append(headlay.show());
                    out.append(itemlay1.show());
                    out.append(itemlay2.show());
                }

                if (itemlay2.isVisible() && !(itemlay2.isFindLayout()))
                    out.append(itemlay2.show());
            }

            if (!itemlay2.isVisible())
            {
                if (itemlay3.isFindLayout())
                {
                    out.append(headlay.show());
                    out.append(itemlay3.show());
                }

                if (itemlay3.isVisible() && !(itemlay3.isFindLayout()))
                    out.append(itemlay3.show());
            }
        }

        // appendDirtyJavaScript("window.name = \"ActiveSeparate2WOStructure\";\n");

        appendDirtyJavaScript("function lovWorkLeaderSign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	openLOVWindow('WORK_LEADER_SIGN',i,\n");
        appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleWorkLeaderSign);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleWorkLeaderSign);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("	+ '&COMPANY= null'\n");
        appendDirtyJavaScript("	,600,450,'validateWorkLeaderSign');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovSign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   openLOVWindow('SIGN',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleSign);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleSign);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY= null'\n");
        appendDirtyJavaScript("	,600,450,'validateSign');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovPartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   openLOVWindow('PART_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=INVENTORY_PART_WO_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePartNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePartNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&CONTRACT= null'\n");
        appendDirtyJavaScript("     ,600,450,'validatePartNo');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem2CatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	openLOVWindow('ITEM2_CATALOG_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART_ACTIVE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleItem2CatalogNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleItem2CatalogNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&CONTRACT= null'\n");
        appendDirtyJavaScript("		,600,450,'validateItem2CatalogNo');\n");
        appendDirtyJavaScript("}\n");


        out.append(modifiedEndPres(mgr,mgr.endPresentation()));
        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>");
        return out;
    }

}
