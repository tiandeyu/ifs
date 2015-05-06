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
*  File        : WorkOrderToolsFacilities.java 
*  Created     : CHAMLK  030922  Specification 'LOAM602  Work Order Tools and Facilities'
*  Modified    :
*  SAPRLK  040105  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040219  Edge Developments - (Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  ARWILK  040308  Bug#112757 - Added new block(headblk).
*  --------------------------------- Edge - SP1 Merge --------------------------------------
*  SHAFLK  040120  Bug# 41815-Removed Java dependencies.
*  THWILK  040322  Merge with SP1.
*  ARWILK  040617  Added job_id to itemblk5.(Spec - AMEC109A)  
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  NIJALK  040830  Call 117450, Modified preDefine().
*  NIJALK  040901  Call 117415, Modified okFind(), okFindITEM5().
*  NIJALK  040908  Call 117750: Modified preDefine().
*  ARWILK  041118  Added new RMB freeTimeSearchFromToolsAndFac.
*  SHAFLK  051011  Bug 51044, Added method duplicateITEM5().
*  NIJALK  051014  Merged bug 51044. 
*  NIJALK  051031  Bug 128243: Modified validate().
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060906  Merged with the Bug Id 58216
*  AMDILK  070570  Call Id 144164: Modifeid okFind()
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  ASSALK  080811  Bug 75641, Modified validate().
*  CHANLK  090730  Bug 82934  Column renamed. ToolFacility.Quantity to Planed Quantity.
*  -----------------------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderToolsFacilities extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderToolsFacilities");

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

    private ASPBlock itemblk5;
    private ASPRowSet itemset5;
    private ASPCommandBar itembar5;
    private ASPTable itemtbl5;
    private ASPBlockLayout itemlay5;   

    private ASPBlock prntblk;
    private ASPRowSet printset;  
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPBuffer data;
    private ASPBuffer row;
    private ASPQuery q;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderToolsFacilities(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
            okFind();
        adjust();                                 
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");

        String txt;

        if ("TOOL_FACILITY_ID".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM5_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
            String sToolType = mgr.readValue("TOOL_FACILITY_TYPE");
            String sToolDesc = null;
            String sTypeDesc = null;
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sNote     = mgr.readValue("ITEM5_NOTE");
            String sToolCost = null;
            String sToolCostCurr = null;
            String sToolCostAmt  = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("TOOL_FACILITY_ID","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                trans.clear();
                // Bug 75641 start
                cmd = trans.addCustomFunction("GETORG","Connect_Tools_Facilities_API.Get_Active_Maint_Org","ITEM5_ORG_CODE");
		// Bug 75641 end
                cmd.addParameter("TOOL_FACILITY_ID",sToolId);
                cmd.addParameter("ITEM5_CONTRACT",sToolCont);
                trans = mgr.validate(trans);

                sToolOrg = trans.getValue("GETORG/DATA/ORG_CODE");
            }

            trans.clear();

            cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);

            cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM5_COST");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            sToolDesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");
            String sGetToolType = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
            sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);

            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM5_COST").formatNumber(ToolCost);

            sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (mgr.isEmpty(sToolType))
                sToolType = sGetToolType;

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" +
                  (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" +
                  (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("TOOL_FACILITY_TYPE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addParameter("TOOL_FACILITY_TYPE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM5_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            trans = mgr.validate(trans);

            String sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM5_COST").formatNumber(ToolCost);

            String sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sToolCostAmt = costAmtStr;

            txt = (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + 
                  (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^";

            mgr.responseWrite(txt);                         
        }
        else if ("ITEM5_CONTRACT".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM5_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sNote     = mgr.readValue("ITEM5_NOTE");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");
            String sWn = mgr.readValue("ITEM5_WO_NO");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("ITEM5_CONTRACT","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                sToolCont ="";
                sToolOrg ="";

            }

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);

            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + 
                  (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" + (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_ORG_CODE".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM5_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sNote     = mgr.readValue("ITEM5_NOTE");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("ITEM5_ORG_CODE","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                sToolCont ="";
                sToolOrg ="";
            }

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");


            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);

            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + 
                  (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" + (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_QTY".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_PLANNED_HOUR".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);  
        }
        else if ("ITEM5_CATALOG_NO".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sSalesPart = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogDesc = null;
            String sCatalogPrice = null;
            String sCatalogPriceCurr = null;
            String sCatalogDiscount  = null;
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

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
        }
        else if ("ITEM5_SALES_PRICE".equals(val))
        {
            String sQty = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM5_DISCOUNT".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt); 
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void tfbestfit()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        if (itemlay5.isMultirowLayout())
            itemset5.goTo(itemset5.getRowSelected());

        bOpenNewWindow = true;
        urlString = "../appsrw/BestFitSearchWiz.page";
        newWinHandle = "tfbestfit"; 
    }

    public void freeTimeSearchFromToolsAndFac()
    {
        ASPManager mgr = getASPManager();

        if (itemlay5.isMultirowLayout())
            itemset5.goTo(itemset5.getRowSelected());

        bOpenNewWindow = true;
        urlString = "FreeTimeSearchEngine.page?RES_TYPE=TOOLS_AND_FACILITY" +
                    "&CONTRACT=" + (mgr.isEmpty(itemset5.getValue("CONTRACT"))?"":mgr.URLEncode(itemset5.getValue("CONTRACT"))) + 
                    "&ORG_CODE="+ (mgr.isEmpty(itemset5.getValue("ORG_CODE"))?"":mgr.URLEncode(itemset5.getValue("ORG_CODE"))) + 
                    "&TOOL_FACILITY_TYPE="+ (mgr.isEmpty(itemset5.getValue("TOOL_FACILITY_TYPE"))?"":mgr.URLEncode(itemset5.getValue("TOOL_FACILITY_TYPE")) +
                    "&TOOL_FACILITY_ID="+ (mgr.isEmpty(itemset5.getValue("TOOL_FACILITY_ID"))?"":itemset5.getValue("TOOL_FACILITY_ID"))) +
                    "&WO_NO="+ (mgr.isEmpty(itemset5.getValue("WO_NO"))?"":mgr.URLEncode(itemset5.getValue("WO_NO"))) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset5.getValue("ROW_NO"))?"":mgr.URLEncode(itemset5.getValue("ROW_NO"))) +
                    "&OBJ_ID="+ (mgr.isEmpty(itemset5.getValue("OBJID"))?"":mgr.URLEncode(itemset5.getValue("OBJID"))) +
                    "&OBJ_VERSION="+ (mgr.isEmpty(itemset5.getValue("OBJVERSION"))?"":mgr.URLEncode(itemset5.getValue("OBJVERSION")));
	newWinHandle = "freeTimeSearchFromToolsAndFac"; 
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
        trans.clear();
        okFindITEM5();
    }

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer buff = mgr.newASPBuffer();

        q = trans.addQuery(itemblk5);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);
        
        if (itemset5.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE5NODATA: No data found."));
            itemset5.clear();
        }
    }

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk5);
        q.addWhereCondition("WO_NO = ? AND CONTRACT = ?");
	q.addParameter("WO_NO",headset.getValue("WO_NO"));
	q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
	q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
        itemset5.clear();
    }

    //-----------------------------------------------------------------------------
    //-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
    //-----------------------------------------------------------------------------

    public void newRowITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM5","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk5);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");
        data.setFieldItem("ITEM5_WO_NO", headset.getValue("WO_NO"));
        data.setFieldItem("ITEM5_CONTRACT", headset.getValue("CONTRACT"));
        data.setFieldItem("ITEM5_CATALOG_NO_CONTRACT", headset.getValue("CONTRACT"));
        itemset5.addRow(data);
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        itemset5.changeRow();

        int currrow = itemset5.getCurrentRowNo();
        mgr.submit(trans);
        itemset5.goTo(currrow);
        itemlay5.setLayoutMode(itemlay5.getHistoryMode());    
    }


    public void  duplicateITEM5()
    {
        ASPManager mgr = getASPManager();
        String sWoNo = "";

        if ( itemlay5.isMultirowLayout() )
            itemset5.goTo(itemset5.getRowSelected());

        if (headset.countRows()>0)
            sWoNo = headset.getRow().getValue("WO_NO");

        itemlay5.setLayoutMode(itemlay5.NEW_LAYOUT);

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM5","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk5);
        cmd.setParameter("ITEM5_WO_NO",sWoNo);
        cmd.setOption("ACTION","PREPARE");


        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");

        data.setFieldItem("TOOL_FACILITY_ID",itemset5.getRow().getValue("TOOL_FACILITY_ID"));
        data.setFieldItem("TOOL_FACILITY_DESC",itemset5.getRow().getValue("TOOL_FACILITY_DESC"));
        data.setFieldItem("TOOL_FACILITY_TYPE",itemset5.getRow().getValue("TOOL_FACILITY_TYPE"));
        data.setFieldItem("TYPE_DESCRIPTION",itemset5.getRow().getValue("TYPE_DESCRIPTION"));
        data.setFieldItem("ITEM5_CONTRACT",itemset5.getRow().getFieldValue("ITEM5_CONTRACT"));
        data.setFieldItem("ITEM5_ORG_CODE",itemset5.getRow().getFieldValue("ITEM5_ORG_CODE"));
        data.setFieldItem("ITEM5_QTY",itemset5.getRow().getFieldValue("ITEM5_QTY"));
        data.setFieldItem("ITEM5_PLANNED_HOUR",itemset5.getRow().getFieldValue("ITEM5_PLANNED_HOUR"));
        data.setFieldItem("ITEM5_CRAFT_LINE_NO",itemset5.getRow().getFieldValue("ITEM5_CRAFT_LINE_NO"));
        data.setFieldItem("ITEM5_COST",itemset5.getRow().getFieldValue("ITEM5_COST"));
        data.setFieldItem("ITEM5_COST_CURRENCY",itemset5.getRow().getFieldValue("ITEM5_COST_CURRENCY"));
        data.setFieldItem("ITEM5_COST_AMOUNT",itemset5.getRow().getFieldValue("ITEM5_COST_AMOUNT"));
        data.setFieldItem("ITEM5_CATALOG_NO_CONTRACT",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO_CONTRACT"));
        data.setFieldItem("ITEM5_CATALOG_NO",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO"));
        data.setFieldItem("ITEM5_CATALOG_NO_DESC",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO_DESC"));
        data.setFieldItem("ITEM5_SALES_PRICE",itemset5.getRow().getFieldValue("ITEM5_SALES_PRICE"));
        data.setFieldItem("ITEM5_SALES_CURRENCY",itemset5.getRow().getFieldValue("ITEM5_SALES_CURRENCY"));
        data.setFieldItem("ITEM5_DISCOUNT",itemset5.getRow().getFieldValue("ITEM5_DISCOUNT"));
        data.setFieldItem("ITEM5_DISCOUNTED_PRICE",itemset5.getRow().getFieldValue("ITEM5_DISCOUNTED_PRICE"));
        data.setFieldItem("ITEM5_PLANNED_PRICE",itemset5.getRow().getFieldValue("ITEM5_PLANNED_PRICE"));
        data.setFieldItem("FROM_DATE_TIME",itemset5.getRow().getFieldValue("FROM_DATE_TIME"));
        data.setFieldItem("TO_DATE_TIME",itemset5.getRow().getFieldValue("TO_DATE_TIME"));
        data.setFieldItem("ITEM5_NOTE",itemset5.getRow().getFieldValue("ITEM5_NOTE"));

        itemset5.addRow(data);                        
    }

    // ----------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------------


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

        itemblk5 = mgr.newASPBlock("ITEM5");

        f = itemblk5.addField("ITEM5_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk5.addField("ITEM5_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk5.addField("ITEM5_WO_NO","Number", "#");
        f.setDbName("WO_NO");
        f.setHidden();

        f = itemblk5.addField("ITEM5_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("WOTOOLSFACILITIES5ROWNO: Row No");
        f.setSize(8);
        f.setReadOnly();

        f = itemblk5.addField("TOOL_FACILITY_ID");
        f.setLabel("WOTOOLSFACILITIES5TFID: Tool/Facility Id");
        f.setLOV("ConnectToolsFacilitiesLov.page","ITEM5_CONTRACT CONTRACT,ITEM5_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM5_QTY,ITEM5_COST,ITEM5_COST_CURRENCY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,ITEM5_NOTE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk5.addField("TOOL_FACILITY_DESC");
        f.setLabel("WOTOOLSFACILITIES5TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("TOOL_FACILITY_TYPE");
        f.setLabel("WOTOOLSFACILITIES5TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","TYPE_DESCRIPTION,ITEM5_COST,ITEM5_COST_CURRENCY,ITEM5_COST_AMOUNT");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();
        f.setMandatory();

        f = itemblk5.addField("TYPE_DESCRIPTION");
        f.setLabel("WOTOOLSFACILITIES5TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("WOTOOLSFACILITIES5CONTRACT: Site");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM5_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("ITEM5_CONTRACT,ITEM5_ORG_CODE,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_WO_NO,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_QTY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(8);
        f.setMaxLength(5);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk5.addField("ITEM5_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("WOTOOLSFACILITIES5ORGCODE: Maintenance Organization");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM5_CONTRACT CONTRACT,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("ITEM5_ORG_CODE,ITEM5_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_WO_NO,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_QTY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk5.addField("ITEM5_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("WOTOOLSFACILITIES5QTY: Planned Quantity");
        //Bug ID 82934, End
        f.setCustomValidation("ITEM5_QTY,ITEM5_PLANNED_HOUR,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_WO_NO,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_COST_AMOUNT,ITEM5_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk5.addField("ITEM5_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("WOTOOLSFACILITIES5PLNHRS: Planned Hours");
        f.setCustomValidation("ITEM5_PLANNED_HOUR,ITEM5_QTY,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_WO_NO,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_COST_AMOUNT,ITEM5_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk5.addField("ITEM5_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("WOTOOLSFACILITIES5CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM5_WO_NO WO_NO",600,445);
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");
        f.setLabel("WOTOOLSFACILITIES5PLANLINENO: Plan Line No");
        f.setSize(8);
        f.setReadOnly();
        f.setHidden();

        f = itemblk5.addField("ITEM5_COST", "Money");
        f.setDbName("COST");
        f.setLabel("WOTOOLSFACILITIES5COST: Cost");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");
        f.setLabel("WOTOOLSFACILITIES5COSTCURR: Cost Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();

        f = itemblk5.addField("ITEM5_COST_AMOUNT", "Money");
        f.setLabel("WOTOOLSFACILITIES5COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM5_WO_NO, :ITEM5_QTY, :ITEM5_PLANNED_HOUR)");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("WOTOOLSFACILITIES5CATALOGCONTRACT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);
        f.setMaxLength(5);
        f.setReadOnly();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("WOTOOLSFACILITIES5CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM5_CATALOG_NO_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setSize(20);
        f.setMaxLength(25);
        f.setInsertable();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_CATALOG_NO_DESC");
        f.setLabel("WOTOOLSFACILITIES5CATALOGDESC: Sales Part Description");
        f.setFunction("''");
        f.setSize(30);
        f.setMaxLength(35);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();
        else
            f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_CATALOG_NO)");   

        f = itemblk5.addField("ITEM5_SALES_PRICE", "Money");
        f.setDbName("SALES_PRICE");
        f.setLabel("WOTOOLSFACILITIES5SALESPRICE: Sales Price");
        f.setCustomValidation("ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_DISCOUNT","ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setSize(12);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("WOTOOLSFACILITIES5SALESCURR: Sales Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("WOTOOLSFACILITIES5DISCOUNT: Discount");
        f.setCustomValidation("ITEM5_DISCOUNT,ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_DISCOUNTED_PRICE", "Money");
        f.setLabel("WOTOOLSFACILITIES5DISCOUNTEDPRICE: Discounted Price");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculate_Discounted_Price(:ITEM5_CATALOG_NO,:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_WO_NO,:ITEM5_QTY,:ITEM5_PLANNED_HOUR,:ITEM5_SALES_PRICE,:ITEM5_DISCOUNT)");

        f = itemblk5.addField("ITEM5_PLANNED_PRICE", "Money");
        f.setLabel("WOTOOLSFACILITIES5PLANNEDPRICE: Planned Price Amount");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculated_Price_Amount(:ITEM5_CATALOG_NO,:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_WO_NO,:ITEM5_QTY,:ITEM5_PLANNED_HOUR,:ITEM5_SALES_PRICE,:ITEM5_DISCOUNT)");


        f = itemblk5.addField("ITEM5_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM5_WO_NO WO_NO");
        //f.setDynamicLOV("WORK_ORDER_JOB");
        f.setLabel("WOTOOLSFACILITIES5JOBID: Job Id");


        f = itemblk5.addField("FROM_DATE_TIME", "Datetime");
        f.setLabel("WOTOOLSFACILITIES5FROMDATE: From Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("WOTOOLSFACILITIES5TODATE: To Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_NOTE");
        f.setDbName("NOTE");
        f.setLabel("WOTOOLSFACILITIES5NOTE: Note");
        f.setSize(20);
        f.setHeight(4);
        f.setMaxLength(2000);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_WO_CONTRACT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk5.addField("ITEM5_SP_SITE");
        f.setFunction("''");
        f.setHidden();

        itemblk5.setView("WORK_ORDER_TOOL_FACILITY");
        itemblk5.defineCommand("WORK_ORDER_TOOL_FACILITY_API","New__,Modify__,Remove__");
        itemblk5.setMasterBlock(headblk);

        itemset5 = itemblk5.getASPRowSet();

        itemtbl5 = mgr.newASPTable(itemblk5);
        itemtbl5.setTitle(mgr.translate("PCMWACTIVESEPARATE2BLK5TITLE: Tools and Facilities"));    
        itemtbl5.setWrap();

        itembar5 = mgr.newASPCommandBar(itemblk5);
        itembar5.enableCommand(itembar5.FIND);
        itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
        itembar5.defineCommand(itembar5.DUPLICATEROW,"duplicateITEM5");
        itembar5.addCustomCommand("tfbestfit",mgr.translate("PCMWACTIVESEPARATE2ITEM5BESTFIT: Best Fit Search..."));
        itembar5.addCustomCommandSeparator();
        itembar5.addCustomCommand("freeTimeSearchFromToolsAndFac",mgr.translate("PCMWWOTOOLFACFREETIMESEARCH: Search Engine..."));

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
        itemlay5.setSimple("TOOL_FACILITY_DESC");
        itemlay5.setSimple("TYPE_DESCRIPTION");
    }

    public void adjust()
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

        if (itemlay5.isVisible())
            appendToHTML(itemlay5.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"WOToolFacility\";\n");

        appendDirtyJavaScript("function validateItem5Qty(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Qty(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_QTY'\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_QTY',i,'Quantity') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5PlannedHour(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5PlannedHour(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_PLANNED_HOUR'\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_PLANNED_HOUR',i,'Planned Hours') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5Discount(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Discount(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_DISCOUNT'\n");
        appendDirtyJavaScript("		+ '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateToolFacilityId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility Id') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_CURRENCY',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,13);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,14);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,15);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,16);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,17);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,18);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5Contract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Contract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM5_CONTRACT',i)!='' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_CONTRACT'\n");
        appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_CONTRACT',i,'Site') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,13);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5OrgCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5OrgCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM5_ORG_CODE',i)!='' )\n");
        appendDirtyJavaScript("{\n");                
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_ORG_CODE'\n");
        appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,13);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");       

        //--------- Call Best Fit Search 

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070727
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
    }
}
