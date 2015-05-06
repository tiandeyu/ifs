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
*  File        : ActiveSeparate2lightSM.java 
*  Created     : ASP2JAVA Tool  010222
*  Modified    : 
*  ARWILK  010529  Stored the latest query and called it after the finish operation. 
*  BUNILK  021121  Added three new fields (Object Site, Connection Type and Object description)
*  THWILK  031022  Call ID 108219, Prevented getting "invalid number" exception by puttnig the format mask in ITEM1_WO_NO.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031217  Edge Developments - (Replaced blocks with tabs, Removed clone and doReset Methods)
*  SAPRLK  040225  Web Alignment - added multirow option to the headset, simplify code for RMBs,remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  THWILK  040421  Call 112951,Modified permits() and used cookie "PageID_CurrentWindow" to prevent the opening of the 
*                  previously visited window when an error is encountered.
*  THWILK  040604  Added PM_REVISION under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040607  Added lov for PM_NO to retrieve PM_NO only under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040625  Merged Bug 43848.
*  SHAFLK  040514  Bug 44632, Added new field Man hours to head block.
*  THWILK  040701  Merged Bug 44632.
*  ARWILK  040714  Added the two tabs "Jobs" and "Tools and Facilities".
*  ARWILK  040716  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  THWILK  040729  Added Project Information Block.(IID AMUT219).
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  THWILk  040810  Merged Bug 41817.
*  ARWILK  040820  Modified methods okFindCase and okFind. Also changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  ThWilk  040825  Call 117308, Modified released() and started().
*  SHAFLK  040726  Bug 43249, Modified method preDefine().
*  NIJALK  040902  Merged bug 43249.
*  ARWILK  040909  Modified setFieldValsItem2 to correct price fetching.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NIJALK  041001  Renamed 'Signature' field to 'Executed By' in Crafts, Jobs tabs.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041105  Added field Std Job Status to Jobs tab.
*  SHAFLK  040916  Bug 46621, Modified method preDefine().
*  Chanlk  041110  Merged Bug 46621.
*  NIJALK  041118  Added field Employee ID to the crafts and jobs tabs.
*  NIJALK  050301  Added "Multiple Material Requisitions Exist" check box.
*  NIJALK  050401  Call 122927: Renamed the tab name "Palnning" to "Planning".
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  SHAFLK  050919  Bug 52880, Modified preDefine().
*  NIJALK  051004  Merged bug 52880.
*  THWILK  051031  Bug 128084,Added itemblk6 and added some fields to itemblk0 in Predefine() & Added methods okFindITEM6,countFindITEM6.
*                  Modified methods okFindITEM0,countFindITEM0,setFieldValsItem0,printContents.
*  AMNILK  051101  Renamed the tab 'Crafts' to 'Operations' in preDefine().Modified the adjust().
*  THWILK  060126  Corrected localization errors.
*  NIJALK  060213  Added supply code to material line.
*  THWILK  060308  Call 136743, Modified Predefine().
*  SULILK  060315  Call 137378: Modified preDefine() and removed the java script for LOV Work Leader.
*  SULILK  060320  Call 135197: Modified preDefine(),setValuesInMaterials().
*  ILSOLK  060727  Merged Bug ID 59224.
*  ILSOLK  060904  Merged Bug 59699.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  ASSALK  070424  Corrected APP_PATH tag to COOKIE_PATH.
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  ILSOLK  070706  Eliminated XSS.
*  BUNILK  070524  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  BUNILK  070802  Bug 66838 Added new field WORK_MASTER_SIGN.
*  AMDILK  070809  Merged bug 66838
*  ILSOLK  070917  Removed Global Company from Company field.(Call Id 148479)
*  ------------------------------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  ILSOLK  071214  Bug Id 68773, Eliminated XSS.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  090217  Bug 79436  Modified preDefine(), released() and started(). 
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit(). 
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  CHANLK  090730  Bug 82934  Column renamed. ToolFacility.Quantity to Planed Quantity.
*  SHAFLK  090921  Bug 85901, Modified preDefine().
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class ActiveSeparate2lightSM extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2lightSM");

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

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;

    private ASPBlock itemblk5;
    private ASPRowSet itemset5;
    private ASPCommandBar itembar5;
    private ASPTable itemtbl5;
    private ASPBlockLayout itemlay5;

    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;
    
    private ASPField f;
    // 031217  ARWILK  Begin  (Replace blocks with tabs)
    private ASPTabContainer tabs;
    // 031217  ARWILK  End  (Replace blocks with tabs)

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private boolean repVal;
    private ASPTransactionBuffer secBuff;
    private ASPQuery q;
    private ASPCommand cmd;
    private int headrowno;
    private int i;
    private ASPBuffer row;
    private ASPBuffer buff;
    private int currrow;
    private String eventVal;
    private String fldTitleWorkLeaderSign;
    private String fldTitleSign;
    private String fldTitlePartNo;
    private String fldTitleItem1CatalogNo;
    private String lovTitleWorkLeaderSign;
    private String lovTitleSign;
    private String lovTitlePartNo;
    private String lovTitleItem1CatalogNo;
    private String[] isSecure =  new String[7];
    private boolean statusRMBPerformed;
    private String keepQuery;
    private boolean actEna1;
    private boolean actEna2;
    private boolean actEna3;
    private boolean again;
    //Web Alignment - simplify code for RMBs
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle; 
    //
    private String repair;
    private String unissue;
    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparate2lightSM(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new String[6];
        ASPManager mgr = getASPManager();    

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        repVal = ctx.readFlag("REPVAL",false);
        keepQuery = ctx.readValue("KEEPQUESM",keepQuery);
        actEna1 = ctx.readFlag("ACTENA1",false);
        actEna2 = ctx.readFlag("ACTENA2",false);
        actEna3 = ctx.readFlag("ACTENA3",false);
        again = ctx.readFlag("AGAIN",false);
        repair =  ctx.readValue("REPAIR","FALSE");
        unissue = ctx.readValue("UNISSUE","FALSE");

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("CASE_ID")))
            okFindCase();
        //Bug 43848, start
        else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue = "FALSE";
            finished1();
        }
        else if ("BBBB".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            finished1();
        }
        else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
        {
            unissue="FALSE";
            reported1();
        }
        //Bug 41817, end
        else
            clear();

        adjust();

        ctx.writeValue("KEEPQUESM",keepQuery);
        ctx.writeFlag("ACTENA1",actEna1);
        ctx.writeFlag("ACTENA2",actEna2);
        ctx.writeFlag("ACTENA3",actEna3);
        ctx.writeFlag("AGAIN",again);

        // 031217  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031217  ARWILK  End  (Replace blocks with tabs)
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method,int ref) 
    {
        ASPManager mgr = getASPManager();


        isSecure[ref] = "false" ; 
        String splitted[] = split(method,"."); 

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(splitted[0],splitted[1]);

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

        keepQuery = mgr.createSearchURL(headblk);

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");     

        mgr.querySubmit(trans,headblk);

        eval(headset.syncItemSets());


        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMNODATA: No data found."));
            headset.clear();
        }
    }


    public void okFindCase()
    {
        ASPManager mgr = getASPManager();

        String woList = "";
        String caseId = mgr.readValue("CASE_ID");
        ASPBuffer woBuff = mgr.newASPBuffer();

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery("CC_WORK_ORDER_API","Get_Work_Order_List");

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists("CC_WORK_ORDER_API.Get_Work_Order_List"))
        {

            if (!mgr.isEmpty(caseId) && caseId!=null)
            {
                trans.clear();

                cmd = trans.addCustomFunction("CCWOLIST","CC_WORK_ORDER_API.Get_Work_Order_List","WOLIST");
                cmd.addParameter("CASE_ID",caseId);

                trans = mgr.perform(trans);

                woList = trans.getValue("CCWOLIST/DATA/WOLIST");

                while (woList.indexOf(new Character((char)31).toString())> -1)
                {
                    woList = woList.replace((char)31,',');
                }
                String wo[] = split(woList,",");
                for (int i = 0; i < wo.length; i++)
                    woBuff.addItem("WO_NO",wo[i]);
            }

            if (!mgr.isEmpty(woList))
            {
                trans.clear();
                q = trans.addEmptyQuery(headblk);
                q.addOrCondition(woBuff);
                q.setOrderByClause("WO_NO");
                q.includeMeta("ALL");

                mgr.submit(trans);

                if (headset.countRows() == 1)
                {
                    okFindITEM0();
                    okFindITEM1();
                    okFindITEM2();
                    okFindITEM3();
                    okFindITEM4();
                    okFindITEM5();
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                }
            }
            else
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMNODATA: No data found."));
                headset.clear();
            }
        }
    }

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(headrowno);


        if (itemset0.countRows() > 0)
            setFieldValsItem0();
    }

    public void okFindITEM6()
    {
        if (itemset0.countRows() > 0)
        {
            ASPManager mgr = getASPManager();

            trans.clear();

            int headrowno = headset.getCurrentRowNo();
            int currrow = itemset0.getCurrentRowNo();

            q = trans.addQuery(itemblk6);
            q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO = ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addParameter("ROW_NO",headset.getRow().getValue("ROW_NO"));
            q.includeMeta("ALL");
            mgr.submit(trans);
            headset.goTo(headrowno);
            itemset0.goTo(currrow);

        }
    }



    public void setFieldValsItem0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int n = itemset0.countRows();
        itemset0.first();
        double nBuyQtyDue = 0;

        for (i=0; i<=n; ++i)
        {
            String catalogNo = itemset0.getRow().getValue("CATALOG_NO");
            String catContract =itemset0.getRow().getFieldValue("CATALOG_CONTRACT");
            String roleCode = itemset0.getRow().getValue("ROLE_CODE");
            String item0Contract = itemset0.getRow().getFieldValue("ITEM0_CONTRACT");
            String item0OrgCode = itemset0.getRow().getFieldValue("ITEM0_ORG_CODE");
            String masterContract = headset.getRow().getValue("CONTRACT");
            String masterOrgCode = headset.getRow().getValue("ORG_CODE");

            double planMen = itemset0.getRow().getNumberValue("PLAN_MEN");
            if (isNaN(planMen))
                planMen=0;
            double planHours = itemset0.getRow().getNumberValue("PLAN_HRS");
            if (isNaN(planHours))
                planHours=0;
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
            cmd.addParameter("ITEM0_ORG_CODE",item0Contract);
            cmd.addParameter("ITEM0_ORG_CODE",item0OrgCode);

            cmd = trans.addCustomFunction("COSTD"+i,"Organization_API.Get_Org_Cost","COST4");
            cmd.addParameter("ORG_CODE",masterContract);
            cmd.addParameter("ORG_CODE",masterOrgCode);

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
            cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));

            itemset0.next();
        }

        trans = mgr.validate(trans);
        itemset0.first();

        for (i=0; i<=n; ++i)
        {
            double numCost = 0;
            double numCostAmt = 0;
            double plannedMen = itemset0.getRow().getNumberValue("PLAN_MEN");
            if (isNaN(plannedMen))
                plannedMen=0;
            double plannedHours = itemset0.getRow().getNumberValue("PLAN_HRS");
            if (isNaN(plannedHours))
                plannedHours=0;
            row = itemset0.getRow();
            if (!mgr.isEmpty(itemset0.getRow().getValue("CATALOG_NO")))
            {
                if (isSecure[1] =="true")
                {
                    numCost= trans.getNumberValue("COSTA"+i+"/DATA/COST");
                    if (isNaN(numCost))
                        numCost=0;
                    numCostAmt =numCost * plannedMen * plannedHours;
                }
                else
                {
                    numCost = 0;
                    numCostAmt = 0;
                }
            }

            if (numCost==0 && !mgr.isEmpty(itemset0.getRow().getValue("ROLE_CODE")))
            {
                numCost= trans.getNumberValue("COSTB"+i+"/DATA/COST2");
                if (isNaN(numCost))
                    numCost=0;
                numCostAmt =numCost * plannedMen * plannedHours;
            }

            if (numCost==0 && !mgr.isEmpty(itemset0.getRow().getFieldValue("ITEM0_ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTC"+i+"/DATA/COST3");
                if (isNaN(numCost))
                    numCost=0;
                numCostAmt =numCost * plannedMen * plannedHours;
            }

            if (numCost==0 && !mgr.isEmpty(headset.getRow().getFieldValue("ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTD"+i+"/DATA/COST4"); 
                if (isNaN(numCost))
                    numCost=0;
                numCostAmt = numCost * plannedMen * plannedHours; 
            }

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
            if (isNaN(nBaseUnitPrice))
                nBaseUnitPrice=0;
            double nSaleUnitPrice= trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            if (isNaN(nSaleUnitPrice))
                nSaleUnitPrice=0;
            double nDiscount= trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount=0;
            String sPriceListNo= trans.getBuffer("PRICEINFO"+i+"/DATA").getValue("PRICE_LIST_NO"); 
            nBuyQtyDue = plannedMen + plannedHours;
            double nsalePriceAmt = nSaleUnitPrice * nBuyQtyDue;

            if (mgr.isEmpty(itemset0.getRow().getValue("PRICE_LIST_NO")))
                row.setValue("PRICE_LIST_NO",sPriceListNo);

            row.setNumberValue("DISCOUNT",nDiscount);
            row.setNumberValue("SALESPRICEAMOUNT",nsalePriceAmt);     
            row.setNumberValue("LISTPRICE",nBaseUnitPrice);     
            row.setNumberValue("COST",numCost);
            row.setNumberValue("COSTAMOUNT",numCostAmt);
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

        headset.goTo(headrowno);
    }

    public void setValuesInMaterials()
    {
        ASPManager mgr = getASPManager();
        String priceListNo = "";
        String cataNo = "";
        double nPlanQty = 0;
        double nCostAmount = 0;
        trans.clear();      

        int n = itemset.countRows();

        if (n > 0)
        {
            itemset.first();

            for (i=0; i<=n; ++i)
            {
                String spareCont = itemset.getRow().getValue("SPARE_CONTRACT");
                String partNo =itemset.getRow().getFieldValue("PART_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty=0;
                String cataCont = itemset.getRow().getFieldValue("ITEM_CATALOG_CONTRACT");
                String cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
                String agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
                priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
                String planLineNo = itemset.getRow().getFieldValue("PLAN_LINE_NO");

                /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo);  */

                String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset.getRow().getFieldValue("CONDITION_CODE");

                cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo);
                cmd.addParameter("SERIAL_NO",serialNo);
                cmd.addParameter("CONFIGURATION_ID","*");
                cmd.addParameter("CONDITION_CODE",conditionCode);

                if ((!mgr.isEmpty(cataNo)) && nPlanQty!=0)
                {
                    cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("ITM_BASE_PRICE","0");
                    cmd.addParameter("ITM_SALE_PRICE","0");
                    cmd.addParameter("ITEM_DISCOUNT","0");
                    cmd.addParameter("CURRENCY_RATE","0");
                    cmd.addParameter("ITEM_CATALOG_CONTRACT",cataCont);
                    cmd.addParameter("ITEM_CATALOG_NO",cataNo);
                    cmd.addParameter("CUSTOMER_NO",cusNo);
                    cmd.addParameter("AGREEMENT_ID",agreeId);
                    cmd.addParameter("ITEM_PRICE_LIST_NO",priceListNo);
                    cmd.addParameter("PLAN_QTY",mgr.getASPField("PLAN_QTY").formatNumber(nPlanQty));

                    cmd = trans.addCustomFunction("LISTPRICE"+i,"WORK_ORDER_PLANNING_API.Get_Sales_Price","LIST_PRICE");
                    cmd.addParameter("ITEM_WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));
                    cmd.addParameter("PLAN_LINE_NO",planLineNo);
                }
                itemset.next();
            }           

            trans = mgr.validate(trans);

            itemset.first();

            for (i = 0; i < n; ++i)
            {
                double nCost = 0;
                row = itemset.getRow();
                if (!mgr.isEmpty(itemset.getRow().getFieldValue("PART_NO")))
                    nCost = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                if (isNaN(nCost))
                    nCost = 0;
                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty = 0;
                if (nPlanQty != 0)
                {
                    double nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                    if (isNaN(nCostTemp))
                        nCostTemp = 0;
                    nCostAmount = nCostTemp * nPlanQty;
                }
                else
                    nCostAmount = 0;

                priceListNo = itemset.getRow().getFieldValue("PRICE_LIST_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
                double nDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;
                if ((!mgr.isEmpty(cataNo)) && (nPlanQty!=0))
                {
                    double listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                    double planQty, nSalesPriceAmount = 0;

                    if (isNaN(listPrice))
                        listPrice = 0;

                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getFieldValue("ITEM_PRICE_LIST_NO");

                    if (nDiscount == 0)
                        nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");
                    if (isNaN(nDiscount))
                        nDiscount = 0;
                    if (listPrice == 0)
                    {
                        listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITM_BASE_PRICE");
                        if (isNaN(listPrice))
                            listPrice = 0;
                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty = 0;
                        double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITM_SALE_PRICE");
                        if (isNaN(nSaleUnitPrice))
                            nSaleUnitPrice = 0;
                        nSalesPriceAmount  = nSaleUnitPrice * planQty;
                    }
                    else
                    {
                        double nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                        if (isNaN(nListPriceTemp))
                            nListPriceTemp = 0;
                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty = 0;
                        double nDiscountTemp = itemset.getRow().getNumberValue("DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp =0;

                        if (nDiscountTemp == 0)
                            nDiscountTemp = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp = 0;

                        nSalesPriceAmount = nListPriceTemp * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscountTemp / 100 * nSalesPriceAmount);
                    }

                    row.setValue("PRICE_LIST_NO",priceListNo);
                    row.setNumberValue("LIST_PRICE",listPrice);
                    row.setNumberValue("ITEM_DISCOUNT",nDiscount);
                    row.setNumberValue("ITEM_SALESPRICEAMOUNT",nSalesPriceAmount);
                }

                row.setNumberValue("ITEM_COST",nCost);
                row.setNumberValue("AMOUNTCOST",nCostAmount);

                itemset.setRow(row);

                itemset.next();
            }
        }
        itemset.first();
    }

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(itemblk2);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(headrowno);

        if (itemset2.countRows() > 0)
            setFieldValsItem2();
    }

    public void setFieldValsItem2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int n = itemset2.countRows();
        itemset2.first();

        cmd = trans.addCustomFunction("GETCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","COSTTYPE2");
        cmd = trans.addCustomFunction("GETCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","COSTTYPE3");

        trans = mgr.perform(trans);

        String sCostTypeExternal = trans.getValue("GETCOSTTYPE2/DATA/COSTTYPE2");
        String sCostTypeExpenses = trans.getValue("GETCOSTTYPE3/DATA/COSTTYPE3");

        trans.clear();

        double nBuyQtyDue = 0;
        double nsalePriceAmt = 0;

        for (i=0; i<=n; ++i)
        {
            String catalogNo = itemset2.getRow().getFieldValue("ITEM2_CATALOG_NO");
            String catContract =itemset2.getRow().getFieldValue("ITEM2_CATALOG_CONTRACT");
            String headOrgCode = headset.getRow().getValue("ORG_CODE");
            String headContract = headset.getRow().getValue("CONTRACT");
            String customerNo = headset.getRow().getValue("CUSTOMER_NO");
            String agreementID = headset.getRow().getValue("AGREEMENT_ID");
            String priceListNo = itemset2.getRow().getFieldValue("ITEM2_PRICE_LIST_NO");

            nBuyQtyDue = itemset2.getRow().getNumberValue("QUANTITY");

            if (isNaN(nBuyQtyDue))
                nBuyQtyDue=0;

            String workOrderInvoiceType = itemset2.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

            cmd = trans.addCustomFunction("COSTWP"+i,"Work_Order_Planning_API.Get_Cost","COST2");
            cmd.addParameter("WO_NO",headset.getRow().getFieldValue("WO_NO"));
            cmd.addParameter("PLAN_LINE_NO",itemset2.getRow().getFieldValue("PLAN_LINE_NO"));

            if (checksec("Sales_Part_API.Get_Cost",1))
            {
                cmd = trans.addCustomFunction("COSTA"+i,"Sales_Part_API.Get_Cost","COST");
                cmd.addParameter("ITEM2_CATALOG_CONTRACT",catContract);
                cmd.addParameter("ITEM2_CATALOG_NO",catalogNo);
            }

            cmd = trans.addCustomFunction("COSTC"+i,"Organization_API.Get_Org_Cost","COST3");
            cmd.addParameter("CONTRACT",headContract);
            cmd.addParameter("ORG_CODE",headOrgCode);

            cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM2_CATALOG_CONTRACT",catContract);
            cmd.addParameter("ITEM2_CATALOG_NO",catalogNo);
            cmd.addParameter("CUSTOMER_NO",customerNo);
            cmd.addParameter("AGREEMENT_ID",agreementID);
            cmd.addParameter("PRICE_LIST_NO",priceListNo);
            cmd.addParameter("QUANTITY",mgr.getASPField("QUANTITY").formatNumber(nBuyQtyDue));

            cmd = trans.addCustomFunction("WOINVOTYPE"+i,"WORK_ORDER_INVOICE_TYPE_API.Encode","WORK_ORDER_INVOICE_TYPE");
            cmd.addParameter("WORK_ORDER_INVOICE_TYPE",workOrderInvoiceType);

            itemset2.next();
        }
        trans = mgr.validate(trans);
        itemset2.first();

        for (i = 0; i <= n; ++i)
        {
            double numCost = 0;
            double numCostAmount = 0;
            double nQty = itemset2.getRow().getNumberValue("QUANTITY");

            if (isNaN(nQty))
                nQty=0;

            double nQtyInv = itemset2.getRow().getNumberValue("QTY_TO_INVOICE");

            if (isNaN(nQtyInv))
                nQtyInv=0;

            row = itemset2.getRow();

            String woCostTypeVar = row.getValue("WORK_ORDER_COST_TYPE");

            numCost = trans.getNumberValue("COSTWP"+i+"/DATA/COST2");

            if (isNaN(numCost))
                numCost = 0;

            if (numCost == 0 && !mgr.isEmpty(row.getFieldValue("ITEM2_CATALOG_CONTRACT")) && !mgr.isEmpty(row.getFieldValue("ITEM2_CATALOG_NO")))
            {
                if (isSecure[1] =="true")
                {
                    numCost= trans.getNumberValue("COSTA"+i+"/DATA/COST");

                    if (isNaN(numCost))
                        numCost=0;
                }
            }
            else if (numCost == 0 && !mgr.isEmpty(headset.getRow().getValue("CONTRACT")) && !mgr.isEmpty(headset.getRow().getValue("ORG_CODE")))
            {
                numCost = trans.getNumberValue("COSTC"+i+"/DATA/COST3");

                if (isNaN(numCost))
                    numCost=0;
            }
            else if (isNaN(numCost))
                numCost = 0;

            numCostAmount = numCost * nQty;

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
            if (isNaN(nBaseUnitPrice))
                nBaseUnitPrice=0;
            double nSaleUnitPrice= trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            if (isNaN(nSaleUnitPrice))
                nSaleUnitPrice=0;
            double nDiscount= trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount=0;
            nBuyQtyDue = itemset2.getRow().getNumberValue("QUANTITY");
            if (isNaN(nBuyQtyDue))
                nBuyQtyDue=0;
            String sWorkOrerInvoiceType = trans.getBuffer("WOINVOTYPE"+i+"/DATA").getValue("WORK_ORDER_INVOICE_TYPE");

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
            row.setNumberValue("ITEM2_COST",numCost);
            row.setNumberValue("ITEM2_COSTAMOUNT",numCostAmount);
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

        if (itemset1.countRows() > 0)
        {
            trans.clear();
            int item1rowno = itemset1.getCurrentRowNo();
            int currrowh = headset.getCurrentRowNo();
            q = trans.addQuery(itemblk);
            q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
            q.addParameter("ITEM1_WO_NO",itemset1.getRow().getFieldValue("ITEM1_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset1.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans);
            headset.goTo(currrowh);
            itemset1.goTo(item1rowno);

            if (itemset.countRows() > 0)
                setValuesInMaterials();
        }
    }

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("ITEM_WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
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
        q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(headrowno);
    }

    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk6);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("ROW_NO",itemset0.getRow().getValue("ROW_NO"));
        int headrowno = headset.getCurrentRowNo();
        int itemrowno = itemset0.getCurrentRowNo();
        mgr.submit(trans);
        itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
        itemset6.clear();
        headset.goTo(headrowno);
        itemset0.goTo(itemrowno);
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
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
        headset.goTo(headrowno);  
    }

    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk4);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk4);

        headset.goTo(headrowno);
    }

    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk4);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        int headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);

        headset.goTo(headrowno);

        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
        itemset4.clear();       
    }

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk5);
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk5);

        headset.goTo(headrowno);

    }

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk5);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        mgr.submit(trans);

        headset.goTo(headrowno);

        itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
        itemset5.clear();
    }   

//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


    //Web Alignment - Enable Multirow Action
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
    //


    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMNONE: No RMB method has been selected."));
    }

    public void reportIn()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - enable multirow action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("WO_NO", headset.getValue("WO_NO"));
            }
            else
            {
                rowBuff.addItem(null, headset.getValue("WO_NO"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (headlay.isMultirowLayout())
                headset.next();
        }

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        urlString = createTransferUrl("ActiveSeperateReportInWorkOrderSM.page", transferBuffer);

        newWinHandle = "reportInWOSM"; 
        //

    }

    public void prepareWork()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - enable multirow action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("WO_NO", headset.getValue("WO_NO"));
            }
            else
            {
                rowBuff.addItem(null, headset.getValue("WO_NO"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (headlay.isMultirowLayout())
                headset.next();
        }

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        urlString = createTransferUrl("ActiveSeparate2ServiceManagement.page", transferBuffer);

        newWinHandle = "PrepareWOSM"; 
        //

    }

    public void repInWiz()
    {
        ASPManager mgr = getASPManager();
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "ReportInWorkOrderWiz.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
        newWinHandle = "reportInWiz";
        //
    }

    public void perform(String command) 
    {
        ASPManager mgr = getASPManager();     

        int count = 0;

        //Web Alignment - enable multirow action
        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            count = headset.countSelectedRows();
            headset.markSelectedRows(command);
            mgr.submit(trans);

            headset.refreshAllRows();
        }
        else
        {
            headset.unselectRows();
            headset.markRow(command);
            int currrow = headset.getCurrentRowNo();
            mgr.submit(trans);

            headset.goTo(currrow);
            headset.refreshRow();
        }   
        //     
    }

    public void reinit()
    {
       ASPManager mgr = getASPManager();
       perform("RE_INIT__");
    }

    public void observed()
    {
        perform("CONFIRM__");

    }

    public void underPrep()
    {
        //Web Alignment - Enable Multirow Action
        perform("TO_PREPARE__");
        //
    }

    public void prepared()
    {
        //Web Alignment - Enable Multirow Action
        perform("PREPARE__");
        //
    }

    public void released()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
        int count = 0;
        String eventVal = "";

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            count = 1;
            currrow = headset.getCurrentRowNo();
        }

        trans.clear();

        for (int i = 0; i < count; i ++)
        {
            eventVal = headset.getRow().getValue("OBJEVENTS");

            if (eventVal.indexOf("Release") != -1)
                headset.markRow("RELEASE__");
            else if (eventVal.indexOf("Replan") != -1)
                headset.markRow("REPLAN__");

            if (headlay.isMultirowLayout())
                headset.next();
        }

        mgr.submit(trans);


        for (int i = 0;i < headset.countRows();i++)
        {
            headset.refreshRow();
            if (headlay.isMultirowLayout())
                headset.next();
        }    

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
        else
            headset.goTo(currrow);
        headset.refreshRow();
        //
    }

    public void started()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
        int count = 0;
        String eventVal = "";

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            count = 1;
            currrow = headset.getCurrentRowNo();
        }

        trans.clear();

        for (int i = 0; i < count; i ++)
        {
            eventVal = headset.getRow().getValue("OBJEVENTS");

            if (eventVal.indexOf("Restart") != -1)
                headset.markRow("RESTART__");
            else if (eventVal.indexOf("StartOrder") != -1)
                headset.markRow("START_ORDER__");

            if (headlay.isMultirowLayout())
                headset.next();
        }

        mgr.submit(trans);


        for (int i = 0;i < headset.countRows();i++)
        {
            headset.refreshRow();
            if (headlay.isMultirowLayout())
                headset.next();
        }    

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
        else
            headset.goTo(currrow);

        headset.refreshRow();
    }

    public void workDone()
    {
        //Web Alignment - Enable Multirow Action
        perform("WORK__");
        //
    }

    public void reported()
    {
        //Web Alignment - Enable Multirow Action
        unissue = "FALSE";
        if (!CheckAllMaterialIssued())
        {
            trans.clear();
            perform("REPORT__");
        }
        else
        {
            ctx.setCookie( "PageID_my_cookie2", "TRUE" );
            unissue = "TRUE";
        }
        //
    }

    public void reported1()
    {
        perform("REPORT__");
    }

    public void finished()
    {
        ASPManager mgr = getASPManager();
        repair = "FALSE";
        unissue= "FALSE";

        //Bug 43848, start

        if ((!CheckObjInRepairShop()) && (!CheckAllMaterialIssued()))
        {
            trans.clear();
            perform("FINISH__");
            statusRMBPerformed = true;
        }
        else if (CheckAllMaterialIssued())
        {
            ctx.setCookie( "PageID_my_cookie1", "TRUE" );
            unissue = "TRUE";
        }
        if (CheckObjInRepairShop())
        {
            ctx.setCookie( "PageID_my_cookie", "TRUE" );
            repair = "TRUE";
        }
        //Bug 43848, end
    }

    //Bug 43848, start
    public void finished1()
    {
        ASPManager mgr = getASPManager();

        statusRMBPerformed = true;
        perform("FINISH__");
    }

    public void cancelled()
    {
        //Web Alignment - Enable Multirow Action
        perform("CANCEL__");
        //
    }

    //Bug 43848, start
    public boolean CheckObjInRepairShop()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addCustomFunction("GETPARTNO","EQUIPMENT_SERIAL_API.GET_PART_NO","PART_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        cmd = trans.addCustomFunction("GETSERIALNO","EQUIPMENT_SERIAL_API.GET_SERIAL_NO","SERIAL_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        cmd = trans.addCustomFunction("GETSTATE","Part_Serial_Catalog_Api.Get_Objstate","OBJSTATE");
        cmd.addReference("PART_NO","GETPARTNO/DATA");
        cmd.addReference("SERIAL_NO","GETSERIALNO/DATA");

        trans = mgr.perform(trans);

        String objState = trans.getValue("GETSTATE/DATA/OBJSTATE");
        if ("InRepairWorkshop".equals(objState))
            return true;
        else
            return false;  
    }
    //Bug 43848, end

    //Bug 41817, start
    public boolean CheckAllMaterialIssued()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addCustomFunction("NOTISSUED","Maint_Material_Req_Line_API.Is_All_Qty_Not_Issued","QTY");
        cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        double issued = trans.getNumberValue("NOTISSUED/DATA/QTY");
        if (issued == 1)
        {
            return true;
        }
        else
            return false;  
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
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWONO: WO No").
        setReadOnly().
        setHilite().
        setMaxLength(8);

        headblk.addField("COMPANY").
        setHidden().
        //setGlobalConnection("COMPANY").
        setUpperCase();

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCONTRACT: Site").
        setDefaultNotVisible().
        setUpperCase().
        setHilite().
        setMaxLength(5);

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(5).
        setHilite().    
        setLabel("PCMWACTIVESEPARATE2LIGHTSMMCHCODECONTRACT: Site");

        headblk.addField("MCH_CODE").
        setSize(20).
        setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMMCHCODE: Object ID").
        setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN").
        setUpperCase().
        setHilite().
        setMaxLength(100); 

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(20).
        setHilite().   
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMMCHCODEDESC: Description").
        setReadOnly();

        headblk.addField("CONNECTION_TYPE").
        setSize(20).
        setSelectBox().
        setReadOnly().
        setHilite().    
        enumerateValues("MAINT_CONNECTION_TYPE_API").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCONNECTIONTYPE: Connection Type");

        headblk.addField("STATE").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMSTATE: Status").
        setReadOnly().
        setHilite().
        setMaxLength(30); 

        headblk.addField("ORG_CODE").
        setSize(20).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMEXEDEPT: Maintenance Organization").
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
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWORKLEADSIGN: Work Leader").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20); 

        headblk.addField("WORK_MASTER_SIGN").
        setSize(12).
        setDynamicLOV("MAINT_EMPLOYEE",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWORKMASTERSIGN: Executed By").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);

        headblk.addField("ERR_DESCR").
        setSize(30).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMERRDESCR: Directive").
        setHilite().
        setMaxLength(60); 

        headblk.addField("PRIORITY_ID").
        setSize(8).
        setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPRIORITYID: Priority").
        setUpperCase().
        setMaxLength(1); 

        headblk.addField("OP_STATUS_ID").
        setSize(18).
        setDynamicLOV("OPERATIONAL_STATUS",600,450).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMOPSTATUS: Operational Status").
        setUpperCase().
        setMaxLength(3);

        headblk.addField("PLAN_S_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANSDATE: Planned Start");

        headblk.addField("PLAN_F_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANFDATE: Planned Completion");

        headblk.addField("PLANNED_MAN_HRS","Number").
        setSize(10).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANMANHOURS: Total Man Hours");

        headblk.addField("WORK_TYPE_ID").
        setSize(8).
        setDefaultNotVisible().
        setDynamicLOV("WORK_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWORKTYPEID: Work Type").
        setUpperCase().
        setMaxLength(2); 

        headblk.addField("PM_NO","Number", "#").
        setSize(8). 
        setLOV("PmActionLov1.page",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPMNO: PM No").
        setReadOnly(); 

        headblk.addField("PM_REVISION").
        setSize(8). 
        setDynamicLOV("PM_ACTION","PM_NO",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPMREV: PM Revision").
        setReadOnly().
        setMaxLength(6); 

        headblk.addField("CONTRACT_ID").
        setDynamicLOV("PSC_CONTR_PRODUCT_LOV").                 
        setUpperCase().
        setMaxLength(15).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCONTRACTID: Contract ID").
        setSize(15);

        //Bug 84436, Start 
        f=headblk.addField("CONTRACT_NAME");                    
        f.setDefaultNotVisible();
        if(mgr.isModuleInstalled("SRVCON"))        
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
        else
           f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCONTRACTNAME: Contract Name");
        f.setSize(15);
        //Bug 84436, End
        
        headblk.addField("LINE_NO","Number").
        setDynamicLOV("PSC_CONTR_PRODUCT_LOV").
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMLINENO: Line No").
        setSize(10); 

         //Bug 84436, Start 
        f = headblk.addField("LINE_DESC");                     
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMLINEDESC: Description");
        f.setSize(15);        
        
        
        f = headblk.addField("CONTRACT_TYPE");                     
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCONTRACTTYPE: Contract Type");
        f.setSize(15);
              
        f = headblk.addField("INVOICE_TYPE");                   
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMINVOICETYPE: Invoice Type");
        f.setSize(15);
        //Bug 84436, End
        
        headblk.addField("AGREEMENT_ID").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMAGREEMENTID: Agreement ID").
        setSize(10). 
        setDefaultNotVisible().
	setHidden().
        setReadOnly();

        headblk.addField("CUSTOMER_NO").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCUSTOMERNO: Customer No").
        setSize(20). 
        //setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)").
        setDefaultNotVisible().  
        setHilite().
        setReadOnly();

        headblk.addField("CUSTODESCR").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCUSTOMERDESC: Customer Name").
        setFunction("CUSTOMER_INFO_API.Get_Name(CUSTOMER_NO)").
        setReadOnly().
        setHilite().
        setDefaultNotVisible(); 

        headblk.addField("REPORTED_BY").
        setSize(12).
        setDynamicLOV("EMPLOYEE_LOV",600,450).
        setMandatory().
        setDefaultNotVisible(). 
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREPORTEDBY: Reported by").
        setUpperCase().
        setReadOnly().
        setHilite().
        setMaxLength(20);

        headblk.addField("NAME").
        setSize(28).
        setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREPORTBYDESC: Name").
        setHilite().
        setDefaultNotVisible(). 
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);  

        headblk.addField("REG_DATE","Datetime").
        setSize(30).
        setMandatory().
        setDefaultNotVisible(). 
        setCustomValidation("REG_DATE","REG_DATE").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREGDATE: Reg. Date").
        setReadOnly();

        headblk.addField("REFERENCE_NO").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREFERENCENO: Reference No").
        setDefaultNotVisible().
        setReadOnly();

        headblk.addField("REPIN").
        setFunction("''").
        setHidden(); 

        headblk.addField("WOLIST").
        setFunction("''").
        setHidden();  

        headblk.addField("CASE_ID").
        setFunction("''").
        setHidden();  

        headblk.addField("OBJEVENTS").
        setHidden();    

        headblk.addField("CONNECTION_TYPE_DB").setHidden().setFunction("''");

        if (mgr.isModuleInstalled("PROJ"))
        {

            f = headblk.addField("PROGRAM_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPROGRAMID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Program_Id(:PROJECT_NO)");
            f.setSize(20);

            f = headblk.addField("PROGRAMDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPROGRAMDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:PROJECT_NO),PROJECT_API.Get_Program_Id(:PROJECT_NO))");
            f.setSize(30);

            f = headblk.addField("PROJECT_NO");
            f.setSize(12);
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPROJECTNO: Project No");
            f.setMaxLength(10);

            f = headblk.addField("PROJECTDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPROJECTDESC: Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
            f.setSize(30);

            f = headblk.addField("SUBPROJECT_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSUBPROJECTID: Sub Project ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("SUBPROJECTDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSUBPROJECTDESC: Sub Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMACTIVITYID: Activity ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("ACTIVITYDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMACTIVITYDESC: Activity Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSMACTIVITYSEQ: Activity Sequence");
            f.setSize(20);  
            f.setDefaultNotVisible();
            f.setReadOnly();
        }

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","RE_INIT__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMTITLE: Work Order Information SM"));
        headtbl.setWrap();

        //Web Alignment - Multirow Action
        headtbl.enableRowSelect();
        //

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("reportIn",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMREPIN:  Report in WO SM..."));
        headbar.addCustomCommand("prepareWork",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMPREWORK: Prepare WO SM..."));
        headbar.addCustomCommand("repInWiz",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMREPORTINWIZ: Report in Wizard..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMFAULTWORK: FaultReprt\\WorkRequest"));
        headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMOBSERVED: Observed"));
        headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMUNDPREP: Under Preparation"));
        headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMPREPARED: Prepared"));
        headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMRELEASED: Released"));
        headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMSTARTED: Started"));
        headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMWRKDONE: Work Done"));
        headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMREPORTED: Reported"));
        headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMFINISHED: Finished"));
        headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMCANCELLED: Cancelled"));

        // 031217  ARWILK  Begin  (Replace blocks with tabs)
        headbar.addCustomCommand("activateCrafts", "");
        headbar.addCustomCommand("activatePalnning", "");   
        headbar.addCustomCommand("activateMaterials", "");
        headbar.addCustomCommand("activateToolsAndFacilities", "");
        headbar.addCustomCommand("activateJobs", "");
        // 031217  ARWILK  End  (Replace blocks with tabs)

        headbar.addCustomCommandGroup("WORKORDSTATUS",mgr.translate("ACTIVESEPARATE2LIGHTSMWORKORDSTAT: Work Order Status"));
        headbar.setCustomCommandGroup("reinit", "WORKORDSTATUS");
        headbar.setCustomCommandGroup("observed","WORKORDSTATUS"); 
        headbar.setCustomCommandGroup("underPrep","WORKORDSTATUS");
        headbar.setCustomCommandGroup("prepared","WORKORDSTATUS"); 
        headbar.setCustomCommandGroup("released","WORKORDSTATUS");
        headbar.setCustomCommandGroup("started","WORKORDSTATUS"); 
        headbar.setCustomCommandGroup("workDone","WORKORDSTATUS");
        headbar.setCustomCommandGroup("reported","WORKORDSTATUS");
        headbar.setCustomCommandGroup("finished","WORKORDSTATUS"); 
        headbar.setCustomCommandGroup("cancelled","WORKORDSTATUS");

        //Web Alignment - Multirow Action
        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("repInWiz");

        headbar.addCommandValidConditions("reinit",   "OBJSTATE",  "Enable",  "OBSERVED");
        headbar.addCommandValidConditions("observed",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;UNDERPREPARATION");
        headbar.addCommandValidConditions("underPrep",  "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;PREPARED");
        headbar.addCommandValidConditions("prepared",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;RELEASED");
        headbar.addCommandValidConditions("released",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;STARTED");
        headbar.addCommandValidConditions("started",    "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;WORKDONE");
        headbar.addCommandValidConditions("workDone",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED");
        headbar.addCommandValidConditions("reported",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE");
        headbar.addCommandValidConditions("finished",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE;REPORTED");
        headbar.addCommandValidConditions("cancelled",  "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE;REPORTED");
        //

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 
        headlay.defineGroup("","MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CONNECTION_TYPE,ERR_DESCR,WO_NO,CONTRACT,STATE,ORG_CODE,ORGCODEDESCR,CUSTOMER_NO,CUSTODESCR,REPORTED_BY,NAME,REG_DATE,PM_NO,PM_REVISION,AGREEMENT_ID,PRIORITY_ID,WORK_LEADER_SIGN,WORK_MASTER_SIGN,WORK_TYPE_ID,OP_STATUS_ID,PLAN_S_DATE,PLAN_F_DATE,PLANNED_MAN_HRS,REFERENCE_NO",false,true); 
        if (mgr.isModuleInstalled("PROJ"))
            headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMLABEL0: Project Information"),"PROGRAM_ID,PROGRAMDESC,PROJECT_NO,PROJECTDESC,SUBPROJECT_ID,SUBPROJECTDESC,ACTIVITY_ID,ACTIVITYDESC,ACTIVITY_SEQ",true,false);

        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("ORGCODEDESCR");
        headlay.setSimple("CUSTODESCR");
        headlay.setSimple("NAME");
	headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        // 031217  ARWILK  Begin  (Replace blocks with tabs)
        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMCRAFTSTAB: Operations"), "javascript:commandSet('HEAD.activateCrafts', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMPLANNINGTAB: Planning"), "javascript:commandSet('HEAD.activatePalnning', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMMATERIALTAB: Materials"), "javascript:commandSet('HEAD.activateMaterials', '')");      
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMTANDFTAB: Tools & Facilities"), "javascript:commandSet('HEAD.activateToolsAndFacilities', '')");      
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTSMJOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs', '')");      
        // 031217  ARWILK  End  (Replace blocks with tabs)

        //-----------------------------------------------------------------------
        //-------------- This part belongs to OPERATIONS ------------------------
        //-----------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField("ITEM0_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk0.addField("ITEM0_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk0.addField("ALLOC_NO","Number").
        setDbName("ALLOCATION_NO").
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTALLOCATIONNO: Allocation No");

        itemblk0.addField("ITEM0_WO_NO","Number","#").
        setHidden().
        setLabel("PCMWAACTIVESEPARATE2LIGHTITEM0WONO: WO No").
        setDbName("WO_NO");

        itemblk0.addField("ROW_NO","Number").
        setSize(8).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMROWNO: Operation No").
        setReadOnly();

        itemblk0.addField("ITEM0_DESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0DESCRIPTION: Description").
        setDbName("DESCRIPTION").
        setReadOnly().
        setMaxLength(60);

        itemblk0.addField("ITEM0_JOB_ID", "Number").
        setDbName("JOB_ID").
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0JOBID: Job Id");

        itemblk0.addField("ITEM0_COMPANY").
        setSize(8).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0COMPANY: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk0.addField("ITEM0_ORG_CODE").
        setSize(11).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMITEM0ORCOLOV: List of Maintenance Organization")).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0ORGCODE: Maintenance Organization").
        setUpperCase().
        setDbName("ORG_CODE").
        setMaxLength(8);  

        //Bug ID 59224,start
        itemblk0.addField("ITEM0_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMORGCODEDESC: Maint. Org. Description").
        setFunction("Organization_Api.Get_Description(:ITEM0_CONTRACT,:ITEM0_ORG_CODE)").
        setReadOnly();
        //Bug ID 59224,end

        itemblk0.addField("ITEM0_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0CONTRACT: Maint. Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setReadOnly().
        setMaxLength(5);


        itemblk0.addField("ROLE_CODE").
        setSize(9).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMROLECODE: Craft ID").
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2LIGHTSMROLECODELOV: List of Craft ID")).
        setUpperCase().
        setMaxLength(10);

        //Bug ID 59224,start
       itemblk0.addField("ROLE_CODE_DESC").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMROLECODEDESC: Craft Description").
       setFunction("ROLE_API.Get_Description(:ROLE_CODE)").
       setReadOnly();
       //Bug ID 59224,end


        itemblk0.addField("SIGN").
        setSize(10).
        setDynamicLOV("MAINT_EMPLOYEE","ITEM0_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMSIGN: Executed By").
        setUpperCase().
        setMaxLength(20);  

        itemblk0.addField("ITEM0_SIGN_ID").
        setDbName("SIGN_ID").
        setSize(10).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0SIGNID: Employee ID").
        setUpperCase().
        setReadOnly().
        setMaxLength(20);  

        itemblk0.addField("PLAN_MEN","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANMEN: Planned Men");

        itemblk0.addField("PLAN_HRS","Number").
        setSize(14).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0PLANHRS: Planned Hours");

        itemblk0.addField("TOTAL_MAN_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTMANHRS: Total Man Hours").
        setFunction(":PLAN_MEN * :PLAN_HRS").
        setReadOnly();

        itemblk0.addField("ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMALLOCATEDHRS: Allocated Hours").
        setReadOnly();

        itemblk0.addField("TOTAL_ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMTOTALLOCATEDHRS: Total Allocated Hours").
        setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM0_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk0.addField("TOTAL_REMAINING_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREMAININGHRS: Total Remaining Hours").
        setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM0_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk0.addField("TEAM_CONTRACT").
        setSize(7).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCONT: Team Site").
        setUpperCase();

        itemblk0.addField("TEAM_ID").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMTID: Team ID").
        setUpperCase();

        itemblk0.addField("TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
        setSize(40).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMDESC: Description");

        itemblk0.addField("CATALOG_CONTRACT").
        setSize(15).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCATACONTRACT: Sales Part Site").
        setUpperCase().
        setMaxLength(5);  

        itemblk0.addField("CATALOG_NO").
        setSize(17).
        setDynamicLOV("SALES_PART_SERVICE_LOV","CATALOG_CONTRACT CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCATOGNO: Sales Part Number").
        setUpperCase().
        setMaxLength(25);

        itemblk0.addField("SALESPARTDESC").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMSALESPARDESC: Sales Part Description").
        setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)").
        setReadOnly().
        setMaxLength(2000);

        itemblk0.addField("PRICE_LIST_NO").
        setSize(13).
        setDynamicLOV("SALES_PRICE_LIST",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPRICELISTNO: Price List No").
        setUpperCase().
        setHidden().
        setMaxLength(10);  

        itemblk0.addField("COST","Money").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCOSTITEM0: Cost").
        setFunction("'").
        setReadOnly();  

        itemblk0.addField("COSTAMOUNT","Money").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCOSTAMT: Cost Amount").
        setHidden().
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("LISTPRICE", "Money").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMLISTPRICE: Sales Price").
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("DISCOUNT","Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMDISCONT: Discount %").
        setFunction("''");

        itemblk0.addField("PREDECESSOR").
        setSize(22).
        setLabel("PCMWCTIVESEPARATE2LIGHTSMPRED: Predecessors").
        setDefaultNotVisible().
        setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:ITEM0_WO_NO, :ROW_NO)");

        itemblk0.addField("SALESPRICEAMOUNT", "Money").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMSALESPRICEAMOUNT: Sales Price Amount").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk0.addField("DATE_FROM","Datetime").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMDATEFROM: Date from");

        itemblk0.addField("DATE_TO","Datetime").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMDATETO: Date to");

        itemblk0.addField("TOOLS").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMTOOLS: Tools").
        setHidden().
        setMaxLength(25);

        itemblk0.addField("REMARK").
        setSize(14).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMREMARK: Remark").
        setHidden().
        setMaxLength(80);

        itemblk0.addField("COST2").
        setHidden().
        setFunction("''");

        itemblk0.addField("COST3").
        setHidden().  
        setFunction("''");

        itemblk0.addField("COST4").
        setHidden().  
        setFunction("''");

        itemblk0.addField("BASE_PRICE","Money").
        setFunction("''").
        setHidden();

        itemblk0.addField("SALE_PRICE","Money").
        setFunction("''").
        setHidden(); 

        itemblk0.addField("CURRENCY_RATE","Number").
        setFunction("''").
        setHidden();

        itemblk0.addField("QTY1","Number").
        setFunction("''").
        setHidden(); 

        itemblk0.setView("WORK_ORDER_ROLE");
        itemblk0.defineCommand("WORK_ORDER_ROLE_API", "Modify__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);  

        itembar0.disableCommand(itembar0.EDITROW);

        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

        //----------------------------------------------------------------------
        //------------Allocation on Operations - child of operations------------

       itemblk6 = mgr.newASPBlock("ITEM6");

       itemblk6.addField("ITEM6_OBJID").
       setHidden().
       setDbName("OBJID");

       itemblk6.addField("ITEM6_OBJVERSION").
       setHidden().
       setDbName("OBJVERSION");

       itemblk6.addField("PARENT_ROW_NO","Number").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6PARENTRNO: Parent Operation No").
       setHidden();

       itemblk6.addField("ITEM6_ROW_NO","Number").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6ROWNO: Operation No").
       setDbName("ROW_NO").
       setHidden().
       setInsertable();

       itemblk6.addField("ITEM6_WO_NO","Number","#").
       setHidden().
       setLabel("PCMWAACTIVESEPARATE2LIGHTSMITEM6WONO: WO No").
       setDbName("WO_NO");

       itemblk6.addField("ITEM6_COMPANY").
       setSize(8).
       setHidden().
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6COMPANY: Company").
       setUpperCase().
       setDbName("COMPANY");
                            
       itemblk6.addField("ALLOCATION_NO","Number").
       setReadOnly().
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6ALLOCNO: Allocation No");

       itemblk6.addField("ITEM6_DESCRIPTION").
       setDbName("DESCRIPTION").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6ALLOCDESC: Description");

       
             
       itemblk6.addField("ITEM6_SIGN").
       setDbName("SIGN").
       setSize(10).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6SIGN: Executed By").
       setUpperCase();
       
       itemblk6.addField("ITEM6_SIGN_ID").
       setDbName("SIGN_ID").
       setSize(10).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6SIGNID: Employee ID").
       setUpperCase().
       setReadOnly();

       
       itemblk6.addField("ITEM6_ORG_CODE").
       setSize(11).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6ORGCODE: Maintenance Organization").
       setUpperCase().
       setDbName("ORG_CODE");

         //Bug ID 59224,start
        itemblk6.addField("ITEM6_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2LIGHTORGCODEDESC: Maint. Org. Description").
        setFunction("Organization_Api.Get_Description(:ITEM6_CONTRACT,:ITEM6_ORG_CODE)").
        setReadOnly();
        //Bug ID 59224,end


       itemblk6.addField("ITEM6_CONTRACT").
       setSize(8).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6CONTRACT: Maint. Org. Site").
       setUpperCase().
       setDbName("CONTRACT").
       setReadOnly();

       
       itemblk6.addField("ITEM6_ROLE_CODE").
       setSize(9).
       setDbName("ROLE_CODE").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6ROLECODE: Craft ID").
       setUpperCase().
       setMaxLength(10);

         //Bug ID 59224,start
       itemblk6.addField("ITEM6_ROLE_CODE_DESC").
       setLabel("PCMWACTIVESEPARATE2LIGHTROLECODEDESC: Craft Description").
       setFunction("ROLE_API.Get_Description(:ITEM6_ROLE_CODE)").
       setReadOnly();
        //Bug ID 59224,end

              
       itemblk6.addField("ITEM6_ALLOCATED_HOURS","Number").
       setDbName("ALLOCATED_HOURS").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMALLOCATEDHRS: Allocated Hours").
       setReadOnly();

       itemblk6.addField("ITEM6_TEAM_CONTRACT").
       setDbName("TEAM_CONTRACT").
       setSize(7).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6CONT: Team Site").
       setUpperCase();

       itemblk6.addField("ITEM6_TEAM_ID").
       setSize(13).
       setDbName("TEAM_ID").
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6TID: Team ID").
       setUpperCase();

       itemblk6.addField("ITEM6_TEAMDESC").
       setFunction("Maint_Team_API.Get_Description(:ITEM6_TEAM_ID,:ITEM6_TEAM_CONTRACT)").
       setSize(40).
       setReadOnly().
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6DESC: Description");

       itemblk6.addField("ITEM6_DATE_FROM","Datetime").
       setDbName("DATE_FROM").
       setSize(18).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6DATEFROM: Date From");

       itemblk6.addField("ITEM6_DATE_TO","Datetime").
       setDbName("DATE_TO").
       setSize(18).
       setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM6DATETO: Date To");
       
       itemblk6.setView("WORK_ORDER_ROLE");
       itemblk6.defineCommand("WORK_ORDER_ROLE_API", "Modify__");
       itemblk6.setMasterBlock(itemblk0);

       itemset6 = itemblk6.getASPRowSet();

       itembar6 = mgr.newASPCommandBar(itemblk6);  

       itembar6.disableCommand(itembar6.EDITROW);

       itembar6.enableCommand(itembar6.FIND);
       itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
       itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");

       itemtbl6 = mgr.newASPTable(itemblk6);
       itemtbl6.setWrap();

       itemlay6 = itemblk6.getASPBlockLayout();
       itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT); 
       
      
        //-----------------------------------------------------------------------
        //-------------- This part belongs to MATERIALS  -----------------------
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

        f = itemblk1.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk1.addField("ITEM1_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMWO_NO: Workorder No");

        f = itemblk1.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMMCHCODE: Object ID");
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
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSIGNATURE: Signature");
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
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM1CONTRACT: Site");
        f.setUpperCase();

        f = itemblk1.addField("ENTERED","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMENTERED: Entered");

        f = itemblk1.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMINT_DESTINATION_ID: Int Destination");

        f = itemblk1.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk1.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMDUE_DATE: Due Date");

        f = itemblk1.addField("ITEM1_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSTATE: Status");

        f = itemblk1.addField("NREQUISITIONVALUE", "Number");
        f.setSize(8);
        f.setHidden();
        f.setReadOnly(   );
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMNREQUISITIONVALUE: Total Value");
        f.setFunction("''");

        f = itemblk1.addField("SNOTETEXT");
        f.setSize(15);
        f.setHidden();
        f.setMaxLength(2000);
        f.setFunction("''");

        f = itemblk1.addField("SIGNATURE_ID");
        f.setSize(6);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(11);
        f.setHidden();
        f.setUpperCase();

        f = itemblk1.addField("NNOTEID", "Number");
        f.setSize(6);
        f.setInsertable();
        f.setMaxLength(10);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ORDERCLASS");
        f.setSize(3);
        f.setHidden();
        f.setMaxLength(3);
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk1.addField("SNOTEIDEXIST");
        f.setSize(4);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ITEM1_COMPANY");
        f.setSize(6);
        f.setHidden();
        f.setFunction("Site_API.Get_Company(:ITEM1_CONTRACT)");
        f.setMaxLength(20);
        f.setUpperCase();

        f = itemblk1.addField("NPREACCOUNTINGID", "Number");
        f.setSize(11);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setFunction("''");

        f = itemblk1.addField("MUL_REQ_LINE");
        f.setReadOnly();
        f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM1_WO_NO)");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMMULMATREQEXIST: Multiple Material Requisitions Exist"); 
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

        itemblk1.setView("MAINT_MATERIAL_REQUISITION");
        itemblk1.defineCommand("MAINT_MATERIAL_REQUISITION_API", "PLAN__,RELEASE__,CLOSE__");
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);  

        itembar1.enableCommand(itembar1.FIND);
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);     
        itemlay1.setSimple("INT_DESTINATION_DESC");
        itemlay1.setSimple("SIGNATURENAME");

        //-----------------------------------------------------------------
        //-----------------------------------------------------------------
        //-----------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM3");

        f = itemblk.addField("ITEM_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk.addField("ITEM_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk.addField("LINE_ITEM_NO","Number");
        f.setSize(8);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMLINE_ITEM_NO: Line No");

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25);
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN");
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSPAREDESCRIPTION: Part Description");
        f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

        //Bug 43249, start
        f = itemblk.addField("CONDITION_CODE");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk.addField("CONDDESC");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCONDDESC: Condition Code Description");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPARTOWNERSHIP: Ownership");
        f.setReadOnly();

        f = itemblk.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPARTOWNER: Owner");
        f.setReadOnly();
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setInsertable();
        f.setMaxLength(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMDIMQTY: Dimension/ Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMTYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(13);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMDATE_REQUIRED: Date Required");

        f = itemblk.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSUPPLYCODE: Supply Code");

        f = itemblk.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPLAN_QTY: Quantity Required");

        f = itemblk.addField("QTY","Number");
        f.setSize(13);
        f.setReadOnly();
        f.setInsertable();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMQTY: Quantity Issued");

        f = itemblk.addField("QTY_SHORT","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMQTY_SHORT: Quantity Short");

        f = itemblk.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMQTYONHAND: Quantity on Hand");
        f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMQTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMQTY_RETURNED: Quantity Returned");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMUNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("ITEM_CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setDbName("CATALOG_CONTRACT");
        f.setDefaultNotVisible();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();

        f = itemblk.addField("ITEM_CATALOG_NO");
        f.setSize(9);
        f.setMaxLength(25);
        f.setDbName("CATALOG_NO");
        f.setDefaultNotVisible();
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("CATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCATALOGDESC: Sales Part Description");
        f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");

        f = itemblk.addField("ITEM_PRICE_LIST_NO");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDbName("PRICE_LIST_NO");
        f.setDefaultNotVisible();
        f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("LIST_PRICE","Number");
        f.setSize(9);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMLIST_PRICE: Sales Price");

        f = itemblk.addField("ITEM_DISCOUNT","Number");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setFunction("''");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMDISCOUNT: Discount %");

        f = itemblk.addField("ITEM_SALESPRICEAMOUNT", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMITEMSALESPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk.addField("ITEM_COST", "Money");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMCOST: Cost");
        f.setFunction("''");

        f = itemblk.addField("AMOUNTCOST", "Money");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMAMOUNTCOST: Cost Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEA: Account");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEB: Cost Center");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEF: Project No");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEE: Object No");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEC: Code C");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODED: Code D");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEG: Code G");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEH: Code H");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEI: Code I");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMSCODEJ: Code J");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("ITEM_WO_NO","Number","#");
        f.setSize(17);
        f.setMandatory();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(8);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0_WO_NO: Work Order No");
        f.setDbName("WO_NO");

        f = itemblk.addField("PLAN_LINE_NO","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMPLAN_LINE_NO: Plan Line No");

        f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(17);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        f = itemblk.addField("ITM_BASE_PRICE");
        f.setHidden();
        f.setFunction("''");

        f = itemblk.addField("ITM_SALE_PRICE");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk.addField("DB_STATE");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk.addField("SERIAL_TRACK");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOT_BATCH_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ENG_CHG_LEVEL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("WAIV_DEV_REJ_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_TO_ISSUE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_TO_ASSIGN");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_LEFT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        itemblk.setView("MAINT_MATERIAL_REQ_LINE");

        itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API", "Modify__");
        itemset = itemblk.getASPRowSet();
        itemblk.setMasterBlock(itemblk1);

        itembar = mgr.newASPCommandBar(itemblk);  

        itembar.disableCommand(itembar.EDITROW);

        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM3");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM3");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

        //-----------------------------------------------------------------------
        //-------------- This part belongs to PLANNED(EXTERNAL COST)-------------
        //-----------------------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");

        itemblk2.addField("ITEM2_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk2.addField("ITEM2_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk2.addField("ITEM2_PLAN_LINE_NO").
        setSize(8).
        setMandatory(). 
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANLINENO: Plan Line No").
        setHidden().
        setDbName("PLAN_LINE_NO").
        setReadOnly(); 

        itemblk2.addField("WORK_ORDER_COST_TYPE").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWORKORDCOSTTYPE: Work Order Cost Type").
        setMaxLength(200);

        itemblk2.addField("DATE_CREATED","Date").
        setSize(14).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMDATECRE: Date Created");

        itemblk2.addField("PLAN_DATE","Date").
        setSize(14).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTSMPLANDATE: Plan Date");

        itemblk2.addField("QUANTITY","Number").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2LIGHTSQUANTITY: Quantity"); 

        itemblk2.addField("QTY_TO_INVOICE","Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMQTYTOINVOICE: Quantity To Invoice"); 

        itemblk2.addField("ITEM2_CATALOG_CONTRACT").
        setSize(15).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCATALOGCONT: Sales Part Site").
        setDbName("CATALOG_CONTRACT");

        itemblk2.addField("ITEM2_CATALOG_NO").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCATALOGNO: Sales Part Number").
        setDbName("CATALOG_NO");

        itemblk2.addField("ITEM2_CATALOGDESC").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM2CATALDESC: Sales Part Description").
        setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)").
        setReadOnly().
        setMaxLength(2000).
        setHidden();

        itemblk2.addField("LINE_DESCRIPTION").
	setSize(25).
	setLabel("PCMWACTIVESEPARATE2LIGHTSMLINEDESCRIPTION: Sales Part Description").
	setReadOnly();

        itemblk2.addField("ITEM2_PRICE_LIST_NO").
        setSize(13).
        setDynamicLOV("SALES_PRICE_LIST",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM2_PRICELISTNO: Price List No").
        setUpperCase().
        setHidden().
        setMaxLength(10).
        setDbName("PRICE_LIST_NO");

        itemblk2.addField("ITEM2_COST","Money").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCOSTITEM2: Cost").
        setFunction("''").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("ITEM2_COSTAMOUNT","Money").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMCOSTITEM2AMT: Cost Amount").
        setFunction("''").
        setReadOnly(); 

        itemblk2.addField("ITEM2_LISTPRICE", "Money").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM2LISTPRICE: Sales Price").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk2.addField("ITEM2_DISCOUNT","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTSMITEM2DISCOUNT: Discount %").
        setHidden().
        setFunction("''");

        itemblk2.addField("ITEM2_SALESPRICEAMOUNT","Money").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMSALESPRICEAMOUNTITEM2: Sales Price Amount").
        setFunction("''").
        setReadOnly();  

        itemblk2.addField("WORK_ORDER_INVOICE_TYPE").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2LIGHTSMWORKORDINVOICETYPE: Work OrderInvoice Type").
        setMaxLength(200);

        itemblk2.addField("COSTTYPE2").
        setHidden().
        setFunction("''");

        itemblk2.addField("COSTTYPE3").
        setHidden().
        setFunction("''");

        itemblk2.setView("WORK_ORDER_PLANNING");
        itemblk2.defineCommand("WORK_ORDER_PLANNING_API", "Modify__");
        itemblk2.setMasterBlock(headblk);

        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);  

        itembar2.disableCommand(itembar2.EDITROW);

        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);     

        // ----------------------------------------------------------------------
        // -----------------------------    ITEMBLK4   --------------------------
        // ----------------------------------------------------------------------

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk4.addField("ITEM4_WO_NO","Number");
        f.setDbName("WO_NO");               
        f.setHidden();

        f = itemblk4.addField("ITEM4_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("ACTSEPLIGHTSMITEM5ROWNO: Row No");
        f.setSize(8);       

        f = itemblk4.addField("ITEM4_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("ACTSEPLIGHTSMITEM5CONTRACT: Site");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM4_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTSMITEM5LOVTITLE: Connect Tools and Facilities"));    
        f.setSize(8);
        f.setUpperCase();

        f = itemblk4.addField("TOOL_FACILITY_ID");
        f.setLabel("ACTSEPLIGHTSMITEM5TFID: Tool/Facility Id");
        f.setLOV("ConnectToolsFacilitiesLov.page","ITEM4_CONTRACT CONTRACT,ITEM4_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTSMITEM5LOVTITLE: Connect Tools and Facilities"));        
        f.setSize(20);
        f.setUpperCase();

        f = itemblk4.addField("TOOL_FACILITY_DESC");
        f.setLabel("ACTSEPLIGHTSMITEM5TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setDefaultNotVisible();

        f = itemblk4.addField("TOOL_FACILITY_TYPE");
        f.setLabel("ACTSEPLIGHTSMITEM5TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);      
        f.setSize(20);
        f.setUpperCase();       

        f = itemblk4.addField("TYPE_DESCRIPTION");
        f.setLabel("ACTSEPLIGHTSMITEM5TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("ACTSEPLIGHTSMITEM5ORGCODE: Maintenance Organization");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM4_CONTRACT CONTRACT,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTSMITEM5LOVTITLE: Connect Tools and Facilities"));        
        f.setSize(8);       
        f.setUpperCase();

        f = itemblk4.addField("ITEM4_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("ACTSEPLIGHTSMITEM5QTY: Planned Quantity");      
        //Bug ID 82934, End
        f.setSize(10);

        f = itemblk4.addField("ITEM4_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("ACTSEPLIGHTSMITEM5PLNHRS: Planned Hours");      
        f.setSize(10);      

        f = itemblk4.addField("ITEM4_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);       
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM4_WO_NO WO_NO");
        f.setLabel("PCMWACTIVEROUNDITEM4JOBID: Job Id");

        f = itemblk4.addField("ITEM4_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("ACTSEPLIGHTSMITEM5CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM4_WO_NO WO_NO",600,445);
        f.setSize(8);       
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");        
        f.setSize(8);       
        f.setHidden();

        f = itemblk4.addField("ITEM4_COST", "Money");
        f.setDbName("COST");
        f.setLabel("ACTSEPLIGHTSMITEM5COST: Cost");
        f.setSize(12);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");       
        f.setSize(10);
        f.setHidden();

        f = itemblk4.addField("ITEM4_COST_AMOUNT", "Money");
        f.setLabel("ACTSEPLIGHTSMITEM5COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM4_WO_NO, :ITEM4_QTY, :ITEM4_PLANNED_HOUR)");
        f.setSize(12);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("ACTSEPLIGHTSMITEM5CATALOGCONTRACT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);               
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("ACTSEPLIGHTSMITEM5CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM4_CATALOG_NO_CONTRACT CONTRACT",600,445);        
        f.setSize(20);
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_CATALOG_NO_DESC");
        f.setLabel("ACTSEPLIGHTSMITEM5CATALOGDESC: Sales Part Description");        
        f.setSize(30);              
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
        {
            f.setHidden();
            f.setFunction("''");
        }
        else
            f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:ITEM4_CATALOG_NO_CONTRACT,:ITEM4_CATALOG_NO)");   

        f = itemblk4.addField("ITEM4_SALES_PRICE", "Money");
        f.setDbName("SALES_PRICE");
        f.setLabel("ACTSEPLIGHTSMITEM5SALESPRICE: Sales Price");        
        f.setSize(12);      
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("ACTSEPLIGHTSMITEM5SALESCURR: Sales Currency");
        f.setSize(10);              
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("ACTSEPLIGHTSMITEM5DISCOUNT: Discount");     
        f.setSize(8);       
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_DISCOUNTED_PRICE", "Money");
        f.setLabel("ACTSEPLIGHTSMITEM5DISCOUNTEDPRICE: Discounted Price");      
        f.setSize(12);      
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
        {
            f.setHidden();
            f.setFunction("''");
        }
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculate_Discounted_Price(:ITEM4_CATALOG_NO,:ITEM4_CATALOG_NO_CONTRACT,:ITEM4_WO_NO,:ITEM4_QTY,:ITEM4_PLANNED_HOUR,:ITEM4_SALES_PRICE,:ITEM4_DISCOUNT)");

        f = itemblk4.addField("ITEM4_PLANNED_PRICE", "Money");
        f.setLabel("ACTSEPLIGHTSMITEM5PLANNEDPRICE: Planned Price Amount");     
        f.setSize(12);      
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
        {
            f.setHidden();
            f.setFunction("''");
        }
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculated_Price_Amount(:ITEM4_CATALOG_NO,:ITEM4_CATALOG_NO_CONTRACT,:ITEM4_WO_NO,:ITEM4_QTY,:ITEM4_PLANNED_HOUR,:ITEM4_SALES_PRICE,:ITEM4_DISCOUNT)");

        f = itemblk4.addField("FROM_DATE_TIME", "Datetime");
        f.setLabel("ACTSEPLIGHTSMITEM5FROMDATE: From Date/Time");
        f.setSize(20);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("ACTSEPLIGHTSMITEM5TODATE: To Date/Time");
        f.setSize(20);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_NOTE");
        f.setDbName("NOTE");
        f.setLabel("ACTSEPLIGHTSMITEM5NOTE: Note");
        f.setSize(20);
        f.setHeight(4);             
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_WO_CONTRACT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk4.addField("ITEM4_SP_SITE");
        f.setFunction("''");
        f.setHidden();

        itemblk4.setView("WORK_ORDER_TOOL_FACILITY");
        itemblk4.defineCommand("WORK_ORDER_TOOL_FACILITY_API", "");
        itemset4 = itemblk4.getASPRowSet();

        itemblk4.setMasterBlock(headblk);

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.enableCommand(itembar4.FIND);      
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setDialogColumns(2);  

        // ----------------------------------------------------------------------
        // -----------------------------    ITEMBLK5   --------------------------
        // ----------------------------------------------------------------------

        itemblk5 = mgr.newASPBlock("ITEM5");

        itemblk5.addField("ITEM5_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk5.addField("ITEM5_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk5.addField("ITEM5_WO_NO", "Number", "#").
        setDbName("WO_NO").
        setHidden();

        itemblk5.addField("JOB_ID", "Number").
        setLabel("ACTSEPLIGHTSMITEM6JOBID: Job ID");

        itemblk5.addField("STD_JOB_ID").
        setSize(15).
        setLabel("ACTSEPLIGHTSMITEM6STDJOBID: Standard Job ID").
        setLOV("SeparateStandardJobLov.page", 600, 445).
        setUpperCase().     
        setQueryable();     

        itemblk5.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("ACTSEPLIGHTSMITEM6STDJOBCONTRACT: Site").
        setUpperCase();

        itemblk5.addField("STD_JOB_REVISION").
        setSize(10).
        setLabel("ACTSEPLIGHTSMITEM6STDJOBREVISION: Revision").
        setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).          
        setUpperCase().     
        setQueryable();

        itemblk5.addField("DESCRIPTION").
        setSize(35).
        setLabel("ACTSEPLIGHTSMITEM6DESCRIPTION: Description").
        setUpperCase();

        itemblk5.addField("ITEM5_QTY", "Number").
        setLabel("ACTSEPLIGHTSMITEM6QTY: Quantity").
        setDbName("QTY");

        itemblk5.addField("STD_JOB_STATUS").
        setLabel("ACTSEPLIGHTSMITEM6STDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");

        itemblk5.addField("ITEM5_COMPANY").
        setSize(20).
        setHidden().
        setDbName("COMPANY").
        setUpperCase();

        itemblk5.addField("ITEM5_SIGN_ID").
        setSize(35).
        setDbName("SIGN_ID").
        setLabel("ACTSEPLIGHTSMITEM6SIGNID: Executed By").
        setQueryable().
        setFunction("Company_Emp_API.Get_Person_Id(COMPANY,EMPLOYEE_ID)").    
        setQueryable();

        itemblk5.addField("EMPLOYEE_ID").
        setSize(15).
        setReadOnly().
        setLabel("ACTSEPLIGHTSMITEM6EMPLOYEEID: Employee ID").
        setUpperCase();

        itemblk5.addField("ITEM5_DATE_FROM", "Datetime").
        setSize(20).
        setDbName("DATE_FROM").
        setLabel("ACTSEPLIGHTSMITEM6DATEFROM: Date From");

        itemblk5.addField("ITEM5_DATE_TO", "Datetime").
        setSize(20).
        setDbName("DATE_TO").
        setLabel("ACTSEPLIGHTSMITEM6DATETO: Date To");

        itemblk5.addField("STD_JOB_FLAG", "Number").
        setHidden();

        itemblk5.addField("KEEP_CONNECTIONS").
        setHidden();

        itemblk5.addField("RECONNECT").
        setHidden();

        itemblk5.setView("WORK_ORDER_JOB");
        itemblk5.defineCommand("WORK_ORDER_JOB_API", "");
        itemblk5.setMasterBlock(headblk);

        itemset5 = itemblk5.getASPRowSet();

        itemtbl5 = mgr.newASPTable(itemblk5);
        itemtbl5.setTitle(mgr.translate("ACTSEPLIGHTSMITEM6WOJOBS: Jobs"));
        itemtbl5.setWrap();

        itembar5 = mgr.newASPCommandBar(itemblk5);
        itembar5.enableCommand(itembar5.FIND);
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
    }

    // 031217  ARWILK  Begin  (Replace blocks with tabs)
    public void activateCrafts()
    {
        tabs.setActiveTab(1);
    }

    public void activatePalnning()
    {
        tabs.setActiveTab(2);
    }

    public void activateMaterials()
    {
        tabs.setActiveTab(3);
    }

    public void activateToolsAndFacilities()
    {
        tabs.setActiveTab(4);
    }

    public void activateJobs()
    {
        tabs.setActiveTab(5);
    }
    // 031217  ARWILK  End  (Replace blocks with tabs)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        // 031217  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateCrafts");
        headbar.removeCustomCommand("activatePalnning");   
        headbar.removeCustomCommand("activateMaterials");
        headbar.removeCustomCommand("activateToolsAndFacilities");
        headbar.removeCustomCommand("activateJobs");
        // 031217  ARWILK  End  (Replace blocks with tabs)

        if (!again)
        {
            ASPBuffer availObj;
            trans.clear();
            trans.addPresentationObjectQuery("PCMW/ActiveSeperateReportInWorkOrder.page,PCMW/ActiveSeparate2.page,PCMW/ReportInWorkOrderWiz.page");
            trans = mgr.perform(trans);
            availObj = trans.getSecurityInfo();

            if (availObj.namedItemExists("PCMW/ActiveSeperateReportInWorkOrder.page"))
                actEna1 = true;
            if (availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
                actEna2 = true;
            if (availObj.namedItemExists("PCMW/ReportInWorkOrderWiz.page"))
                actEna3 = true;

            again = true;
        }

        if (!actEna1)
            headbar.removeCustomCommand("reportIn");
        if (!actEna2)
            headbar.removeCustomCommand("prepareWork");
        if (!actEna3)
            headbar.removeCustomCommand("repInWiz");

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);

        fldTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMWORKLEADERFLD: Work+Leader");     
        fldTitleSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMSIGNFLD: Signature");     
        fldTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMPARTNOFLD: Part+No");     
        fldTitleItem1CatalogNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMITEM1CATNOFLD: Sales+Part+Number"); 

        lovTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMWORKLEADERLOV: List+of+Work+Leader");     
        lovTitleSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMSIGNLOV: List+of+Signature");     
        lovTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTSMPARTNOLOV: List+of+Part+No");     
        lovTitleItem1CatalogNo       =       mgr.translate(      "PCMWACTIVESEPARATE2LIGHTSMITEM1CATNOLOV: List+of+Sales+Part+Number");       

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {
            String connType = headset.getValue("CONNECTION_TYPE");

            trans.clear();

            cmd = trans.addCustomFunction("CONNTYPE","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
            cmd.addParameter("CONNECTION_TYPE",connType);

            trans = mgr.perform(trans);

            String connTypeDB = trans.getValue("CONNTYPE/DATA/CONNECTION_TYPE_DB");

            if (connTypeDB.equals("VIM"))
            {
                mgr.getASPField("MCH_CODE_CONTRACT").setHidden();  
            }
        }

        if (itemlay5.isVisible() && (itemset5.countRows() > 0))
        {
            String sWhereStrForITEM5 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

            if (itemlay5.isFindLayout())
            {
                mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
                sWhereStrForITEM5 = sWhereStrForITEM5 + " AND STANDARD_JOB_TYPE_DB = '1'";
            }

            mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM5);
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVESEPARATE2LIGHTSMTITLE: Work Order Information SM";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATE2LIGHTSMTITLE: Work Order Information SM";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("BUTTONVAL","");

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
            {
               appendToHTML(itemlay0.show());
               if (itemlay0.isSingleLayout() && (itemset0.countRows() > 0))
                    appendToHTML(itemlay6.show());
            }
            else if (tabs.getActiveTab() == 2)
                appendToHTML(itemlay2.show());
            else if (tabs.getActiveTab() == 3)
            {
                appendToHTML(itemlay1.show());
                if (itemlay1.isSingleLayout() && (itemset1.countRows() > 0))
                    appendToHTML(itemlay.show());
            }
            else if (tabs.getActiveTab() == 4)
                appendToHTML(itemlay4.show());
            else if (tabs.getActiveTab() == 5)
                appendToHTML(itemlay5.show());
        }
        // 031217  ARWILK  End  (Replace blocks with tabs)
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITSMUNISSUE: All required material has not been issued"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie1\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id 68773 
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie2\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITSMUNISSUE: All required material has not been issued"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"CCCC\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie2\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(repair)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITSMREPAIR: This object is in repair shop. Do you still want to continue?"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		repair = \"FALSE\";\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"BBBB\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        // XSS_Safe ILSOLK 20070706
        appendDirtyJavaScript("if (");
        appendDirtyJavaScript(statusRMBPerformed);
        appendDirtyJavaScript(")\n");
        appendDirtyJavaScript("window.location=\"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(keepQuery));
        appendDirtyJavaScript("\";");

        /*appendDirtyJavaScript("function lovWorkLeaderSign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	openLOVWindow('WORK_LEADER_SIGN',i,\n");
        appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleWorkLeaderSign);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleWorkLeaderSign);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("	+ '&COMPANY= null'\n");
        appendDirtyJavaScript("	,600,450,'validateWorkLeaderSign');\n");
        appendDirtyJavaScript("}\n"); */

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

        appendDirtyJavaScript("function lovItem1CatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	openLOVWindow('ITEM1_CATALOG_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART_ACTIVE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleItem1CatalogNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleItem1CatalogNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&CONTRACT= null'\n");
        appendDirtyJavaScript("		,600,450,'validateItem1CatalogNo');\n");
        appendDirtyJavaScript("}\n");   

        // XSS_Safe ILSOLK 20070706
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("if (readCookie(\"PageID_CurrentWindow\") == \"*\")");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
            appendDirtyJavaScript("}\n");
        }
    }
}
