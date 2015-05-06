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
* -----------------------------------------------------------------------------------------
*  File        : WorkOrderReportInToolsFacilities.java 
*  Created     : CHAMLK  030922  Specification 'LOAM602 Work Order Tools and Facilities'
*  Modified    :
*  SAPRLK  040105  Web Alignment - removed methods clone() and doReset(), simplify code for RMBs.
*  SAPRLK  040212  Web Alignment - change of conditional code in validate method, 
*                  remove unnecessary method calls for hidden fields
*  ARWILK  040311  Bug#112934 - Added new block(headblk).
* -------------------------------- Edge - SP1 Merge --------------------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  ARWILK  040617  Added job_id to itemblk6.(Spec - AMEC109A)  
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  NIJALK  040830  Call 117450, Modified preDefine.
*  SHAFLK  051011  Bug 51044, Added method duplicateITEM6().
*  NIJALK  051014  Merged bug 51044.
*  AMDILK  060817  Bug 58216, Eliminated SQL errors in web applications. Modified the methods okFindITEM6(), countFindITEM6()
*  AMDILK  060906  Merged with the Bug Id 58216
*  CHANLK  090730  Bug 82934  Column renamed. ToolFacility.Quantity to Planed Quantity.
* -----------------------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderReportInToolsFacilities extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderReportInToolsFacilities");

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

    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;   

    private ASPBlock prntblk;
    private ASPRowSet printset;  

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String qrystr;
    private String rRowN;
    private String rRawId;
    private String callingUrl;
    private ASPBuffer temp;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private int headrowno;
    private ASPBuffer row;
    private String sWoNo;
    private String sContract;  
    private String sBestFit;
    private ASPQuery q;
    private boolean actEna1;
    private boolean again;
    private String performRMB;
    private String URLString;
    private String WindowName;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderReportInToolsFacilities(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();

        sContract = ctx.readValue("SCONNNT", sContract);
        sWoNo = ctx.readValue("SWONO", sWoNo);
        qrystr = ctx.readValue("QRYSTR", "");
        again = ctx.readFlag("AGAIN", false);
        actEna1 = ctx.readFlag("ACTENA1", false);

        callingUrl = ctx.getGlobal("CALLING_URL");  

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            okFind();
        }
        adjust();

        ctx.writeValue("SCONNNT", sContract);

        ctx.writeValue("SWONO", sWoNo);
        ctx.writeValue("QRYSTR", qrystr);
        ctx.writeFlag("AGAIN", again);
        ctx.writeFlag("ACTENA1", actEna1);
    }

    public void  saveReturn()
    {
        ASPManager mgr = getASPManager();

        itemset6.changeRow();

        int currrow = itemset6.getCurrentRowNo();
        mgr.submit(trans);
        itemset6.goTo(currrow);
        itemlay6.setLayoutMode(itemlay6.getHistoryMode());    
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");

        String txt;

        if ("TOOL_FACILITY_ID".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sToolType = mgr.readValue("TOOL_FACILITY_TYPE");
            String sToolDesc = null;
            String sTypeDesc = null;
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sToolCost = null;
            String sToolCostCurr = null;
            String sToolCostAmt  = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");

            cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
            cmd.addParameter("TOOL_FACILITY_ID");

            cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
            cmd.addParameter("TOOL_FACILITY_ID");

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM6_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM6_COST_CURRENCY");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            //cmd.addReference("ITEM6_QTY","TFQTY/DATA");

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            sToolDesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");
            String sGetToolType = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
            sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM6_COST").formatNumber(ToolCost);

            sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (mgr.isEmpty(sToolType))
                sToolType = sGetToolType;
            /*if (mgr.isEmpty(sQty))
               sQty = qtyStr;*/
            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sToolDesc) ? "" : (sToolDesc))+ "^" + (mgr.isEmpty(sToolType) ? "" : (sToolType))+ "^" + 
                  (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("TOOL_FACILITY_TYPE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addParameter("TOOL_FACILITY_TYPE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM6_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM6_COST_CURRENCY");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            trans = mgr.validate(trans);

            String sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM6_COST").formatNumber(ToolCost);

            String sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sToolCostAmt = costAmtStr;

            txt = (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + 
                  (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^";

            mgr.responseWrite(txt); 
            mgr.endResponse();                        
        }
        else if ("ITEM6_CONTRACT".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");
            String sWn = mgr.readValue("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            /*if (mgr.isEmpty(sQty))
               sQty = qtyStr;*/
            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("ITEM6_ORG_CODE".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            /*if (mgr.isEmpty(sQty))
               sQty = qtyStr;*/
            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("ITEM6_QTY".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("ITEM6_PLANNED_HOUR".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);  
            mgr.endResponse();
        }
        else if ("ITEM6_CATALOG_NO".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sSalesPart = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogDesc = null;
            String sCatalogPrice = null;
            String sCatalogPriceCurr = null;
            String sCatalogDiscount  = null;
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);


            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
            }
            if ("TRUE".equals(sSpSite))
            {
                sCatalogDesc = sGetCatalogDesc;
                sCatalogPrice = catPriceStr;
                sCatalogPriceCurr = sGetCatalogPriceCurr;
                sCatalogDiscount = catDiscount;
                sCatalogDisPrice = catDiscountedPrice;
            }

            txt = (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + 
                  (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + 
                  (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("ITEM6_SALES_PRICE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        else if ("ITEM6_DISCOUNT".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
            mgr.endResponse();
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    //--------- T/F Best Fit search
    public void  tfbestfit()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        if (itemlay6.isMultirowLayout())
            itemset6.goTo(itemset6.getRowSelected());

        trans = mgr.newASPTransactionBuffer();              
        //trans.addSecurityQuery("PURCHASE_PART");                   
        trans.addPresentationObjectQuery("APPSRW/BestFitSearchWiz.page");

        trans = mgr.perform(trans);                   

        ASPBuffer secBuff = trans.getSecurityInfo();

        if (secBuff.namedItemExists("APPSRW/BestFitSearchWiz.page"))
        {
            //sBestFit = "TRUE"; 
            //Web Alignment - simplify code for RMBs
            performRMB = "TRUE";
            URLString = "../appsrw/BestFitSearchWiz.page";
            WindowName = "BestFitWiz";
            //
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addQuery(headblk);
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() > 0)
            okFindITEM6();
    }

    public void  okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", headset.getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);
    }

    public void  countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", headset.getValue("WO_NO"));
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
        itemset6.clear();
    }

    //-----------------------------------------------------------------------------
    //-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
    //-----------------------------------------------------------------------------

    public void  newRowITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM6","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk6);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM6/DATA");
        data.setFieldItem("ITEM6_WO_NO", headset.getValue("WO_NO"));
        itemset6.addRow(data);
    }

    public void  duplicateITEM6()
    {
        ASPManager mgr = getASPManager();

        if ( itemlay6.isMultirowLayout() )
            itemset6.goTo(itemset6.getRowSelected());

        itemlay6.setLayoutMode(itemlay6.NEW_LAYOUT);

        cmd = trans.addEmptyCommand("ITEM6","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk6);
        cmd.setParameter("ITEM6_WO_NO",sWoNo);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM6/DATA");

        data.setFieldItem("ITEM6_WO_NO", headset.getValue("WO_NO"));
        data.setFieldItem("TOOL_FACILITY_ID",itemset6.getRow().getValue("TOOL_FACILITY_ID"));
        data.setFieldItem("TOOL_FACILITY_DESC",itemset6.getRow().getValue("TOOL_FACILITY_DESC"));
        data.setFieldItem("TOOL_FACILITY_TYPE",itemset6.getRow().getValue("TOOL_FACILITY_TYPE"));
        data.setFieldItem("TYPE_DESCRIPTION",itemset6.getRow().getValue("TYPE_DESCRIPTION"));
        data.setFieldItem("ITEM6_CONTRACT",itemset6.getRow().getFieldValue("ITEM6_CONTRACT"));
        data.setFieldItem("ITEM6_ORG_CODE",itemset6.getRow().getFieldValue("ITEM6_ORG_CODE"));
        data.setFieldItem("ITEM6_QTY",itemset6.getRow().getFieldValue("ITEM6_QTY"));
        data.setFieldItem("ITEM6_PLANNED_HOUR",itemset6.getRow().getFieldValue("ITEM6_PLANNED_HOUR"));
        data.setFieldItem("ITEM6_REPORTED_HOUR",itemset6.getRow().getFieldValue("ITEM6_REPORTED_HOUR"));
        data.setFieldItem("ITEM6_CRAFT_LINE_NO",itemset6.getRow().getFieldValue("ITEM6_CRAFT_LINE_NO"));
        data.setFieldItem("ITEM6_COST",itemset6.getRow().getFieldValue("ITEM6_COST"));
        data.setFieldItem("ITEM6_COST_CURRENCY",itemset6.getRow().getFieldValue("ITEM6_COST_CURRENCY"));
        data.setFieldItem("ITEM6_COST_AMOUNT",itemset6.getRow().getFieldValue("ITEM6_COST_AMOUNT"));
        data.setFieldItem("ITEM6_CATALOG_NO_CONTRACT",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO_CONTRACT"));
        data.setFieldItem("ITEM6_CATALOG_NO",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO"));
        data.setFieldItem("ITEM6_CATALOG_NO_DESC",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO_DESC"));
        data.setFieldItem("ITEM6_SALES_PRICE",itemset6.getRow().getFieldValue("ITEM6_SALES_PRICE"));
        data.setFieldItem("ITEM6_SALES_CURRENCY",itemset6.getRow().getFieldValue("ITEM6_SALES_CURRENCY"));
        data.setFieldItem("ITEM6_DISCOUNT",itemset6.getRow().getFieldValue("ITEM6_DISCOUNT"));
        data.setFieldItem("ITEM6_DISCOUNTED_PRICE",itemset6.getRow().getFieldValue("ITEM6_DISCOUNTED_PRICE"));
        data.setFieldItem("ITEM6_PLANNED_PRICE",itemset6.getRow().getFieldValue("ITEM6_PLANNED_PRICE"));
        data.setFieldItem("FROM_DATE_TIME",itemset6.getRow().getFieldValue("FROM_DATE_TIME"));
        data.setFieldItem("TO_DATE_TIME",itemset6.getRow().getFieldValue("TO_DATE_TIME"));
        data.setFieldItem("ITEM6_NOTE",itemset6.getRow().getFieldValue("ITEM6_NOTE"));

        itemset6.addRow(data);                        
    }

    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------

    public void  preDefine()
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
        setLabel("PCMWWORKORDERBUDGETWONO: WO No").
        setReadOnly().
        setMaxLength(8);

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWWORKORDERBUDGETCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        headblk.addField("MCH_CODE").
        setSize(13).
        setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
        setCustomValidation("CONTRACT,MCH_CODE","DESCRIPTION").
        setLabel("PCMWWORKORDERBUDGETMCHCODE: Object ID").
        setUpperCase().
        setReadOnly().
        setMaxLength(100);

        headblk.addField("DESCRIPTION").
        setSize(28).
        setLabel("PCMWWORKORDERBUDGETOBJDESC: Object Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("STATE").
        setSize(11).
        setLabel("PCMWWORKORDERBUDGETSTATE: Status").
        setReadOnly().
        setMaxLength(30); 

        headblk.setView("ACTIVE_SEPARATE");

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.disableCommand(headbar.FIND);
        headbar.disableCommand(headbar.COUNTFIND);
        headbar.disableCommand(headbar.BACK);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

        // ----------------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------------

        itemblk6 = mgr.newASPBlock("ITEM6");

        f = itemblk6.addField("ITEM6_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk6.addField("ITEM6_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        boolean orderInst = false; 

        f = itemblk6.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        if (mgr.isModuleInstalled("ORDER"))
            orderInst = true;

        f = itemblk6.addField("ITEM6_WO_NO","Number", "#");
        f.setDbName("WO_NO");
        //f.setLabel("WOTOOLSFACILITIESREPORTIN6WONO: Wo No");
        //f.setSize(8);
        f.setMandatory();
        //f.setInsertable();
        //f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6ROWNO: Row No");
        f.setSize(8);
        f.setReadOnly();

        f = itemblk6.addField("TOOL_FACILITY_ID");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6TFID: Tool/Facility Id");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_LOV3","ITEM6_CONTRACT CONTRACT,ITEM6_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_ID,ITEM6_CONTRACT,ITEM6_ORG_CODE,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM6_QTY,ITEM6_COST,ITEM6_COST_CURRENCY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE,ITEM6_NOTE");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("TOOL_FACILITY_DESC");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("TOOL_FACILITY_TYPE");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","TYPE_DESCRIPTION,ITEM6_COST,ITEM6_COST_CURRENCY,ITEM6_COST_AMOUNT");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();
        f.setMandatory();

        f = itemblk6.addField("TYPE_DESCRIPTION");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6CONTRACT: Site");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_SITE","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_ORG_CODE ORG_CODE",600,445);
        f.setCustomValidation("ITEM6_CONTRACT,ITEM6_ORG_CODE,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_WO_NO,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","ITEM6_NOTE,ITEM6_QTY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setMaxLength(5);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6ORGCODE: Maintenance Organization");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_ORG","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM6_ORG_CODE,ITEM6_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_WO_NO,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","ITEM6_NOTE,ITEM6_QTY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("WOTOOLSFACILITIESREPORTIN6QTY: Planned Quantity");
        //Bug ID 82934, End
        f.setCustomValidation("ITEM6_QTY,ITEM6_PLANNED_HOUR,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_WO_NO,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_SALES_PRICE,ITEM6_DISCOUNT","ITEM6_COST_AMOUNT,ITEM6_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk6.addField("ITEM6_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6PLNHRS: Planned Hours");
        f.setCustomValidation("ITEM6_PLANNED_HOUR,ITEM6_QTY,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_WO_NO,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_SALES_PRICE,ITEM6_DISCOUNT","ITEM6_COST_AMOUNT,ITEM6_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk6.addField("ITEM6_REPORT_HOUR", "Number");
        f.setDbName("REPORT_HOUR");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6REPHR: Report Hours");
        f.setSize(10);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_REPORTED_HOUR", "Number");
        f.setDbName("REPORTED_HOUR");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6REPTEDHRS: Reported Hours");
        f.setSize(10);
        f.setReadOnly();

        f = itemblk6.addField("ITEM6_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM6_WO_NO WO_NO",600,445);
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");
        //f.setLabel("WOTOOLSFACILITIESREPORTIN6PLANLINENO: Plan Line No");
        //f.setSize(8);
        //f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_COST", "Money");
        f.setDbName("COST");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6COST: Cost");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");
        //f.setLabel("WOTOOLSFACILITIESREPORTIN6COSTCURR: Cost Currency");
        //f.setSize(10);
        //f.setMaxLength(3);
        //f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_COST_AMOUNT", "Money");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM6_WO_NO, :ITEM6_QTY, :ITEM6_PLANNED_HOUR)");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6CATALOGCONT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);
        f.setMaxLength(5);
        f.setInsertable();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM6_CATALOG_NO_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(20);
        f.setMaxLength(25);
        f.setInsertable();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_CATALOG_NO_DESC");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6CATALOGDESC: Sales Part Description");
        f.setFunction("''");
        f.setSize(30);
        f.setMaxLength(35);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_CATALOG_NO)");   

        f = itemblk6.addField("ITEM6_SALES_PRICE", "Money");
        f.setDbName("SALES_PRICE");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6SALESPRICE: Sales Price");
        f.setCustomValidation("ITEM6_SALES_PRICE,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_DISCOUNT","ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(12);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6SALESCURR: Sales Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6DISCOUNT: Discount");
        f.setCustomValidation("ITEM6_DISCOUNT,ITEM6_SALES_PRICE,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_DISCOUNTED_PRICE", "Money");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6DISCPRICE: Discounted Price");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculate_Discounted_Price(:ITEM6_CATALOG_NO,:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_WO_NO,:ITEM6_QTY,:ITEM6_PLANNED_HOUR,:ITEM6_SALES_PRICE,:ITEM6_DISCOUNT)");

        f = itemblk6.addField("ITEM6_PLANNED_PRICE", "Money");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6PLANPRICE: Planned Price Amount");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculated_Price_Amount(:ITEM6_CATALOG_NO,:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_WO_NO,:ITEM6_QTY,:ITEM6_PLANNED_HOUR,:ITEM6_SALES_PRICE,:ITEM6_DISCOUNT)");


        f = itemblk6.addField("ITEM6_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM6_WO_NO WO_NO");
        //f.setDynamicLOV("WORK_ORDER_JOB");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6JOBID: Job Id");

        f = itemblk6.addField("FROM_DATE_TIME", "Datetime");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6FROMDATE: From Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6TODATE: To Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_NOTE");
        f.setDbName("NOTE");
        f.setLabel("WOTOOLSFACILITIESREPORTIN6NOTE: Note");
        f.setSize(20);
        f.setHeight(4);
        f.setMaxLength(2000);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_WO_CONTRACT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk6.addField("ITEM6_SP_SITE");
        f.setFunction("''");
        f.setHidden();

        itemblk6.setView("WORK_ORDER_TOOL_FACILITY");
        itemblk6.defineCommand("WORK_ORDER_TOOL_FACILITY_API","New__,Modify__,Remove__");
        itemblk6.setMasterBlock(headblk);

        itemset6 = itemblk6.getASPRowSet();

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setTitle(mgr.translate("WOTOOLSFACILITIESREPORTIN6TITLE: Tools and Facilities"));    
        itemtbl6.setWrap();

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.enableCommand(itembar6.FIND);
        itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.DUPLICATEROW,"duplicateITEM6");
        itembar6.addCustomCommand("tfbestfit",mgr.translate("WOTOOLSFACILITIESREPORTIN6BESTFIT: Best Fit Search..."));

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);
        itemlay6.setSimple("TOOL_FACILITY_DESC");
        itemlay6.setSimple("TYPE_DESCRIPTION");
    }

    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWORKORDERTF: Tools and Facilities";
    }

    protected String getTitle()
    {
        return "PCMWORKORDERTF: Tools and Facilities";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && headset.countRows() > 0)
            appendToHTML(itemlay6.show());

        appendToHTML("<br>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"WOToolFacility\";\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(performRMB);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  url_to_go = '");
        appendDirtyJavaScript(URLString);
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  window_name = '");
        appendDirtyJavaScript(WindowName);
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem6Qty(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem6Qty(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM6_QTY'\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("          + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_QTY',i,'Quantity') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function validateItem6PlannedHour(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem6PlannedHour(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM6_PLANNED_HOUR'\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_PLANNED_HOUR',i,'Planned Hours') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem6Discount(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem6Discount(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM6_DISCOUNT'\n");
        appendDirtyJavaScript("		+ '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateToolFacilityId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkToolFacilityId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('TOOL_FACILITY_DESC',i).value = '';\n");
        appendDirtyJavaScript("		validateToolFacilityType(i);\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=TOOL_FACILITY_ID'\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility Id') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_CURRENCY',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,13);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,14);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,15);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function validateItem6Contract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem6Contract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM6_CONTRACT',i)!='' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM6_CONTRACT'\n");
        appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_CONTRACT',i,'Site') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function validateItem6OrgCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem6OrgCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM6_ORG_CODE',i)!='' )\n");
        appendDirtyJavaScript("{\n");                
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM6_ORG_CODE'\n");
        appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n"); 
    }
}
