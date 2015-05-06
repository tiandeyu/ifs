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
*  File        : ActiveSeparate2light.java 
*  Created     : ASP2JAVA Tool  010220
*  Modified    : 
*  ARWILK  010529  Stored the latest query and called it after the finish operation. 
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  BUNILK  021121  Added three new fields (Object Site, Connection Type and Object description)
*  SHAFLK  021128  Added a new method isModuleInst() to check availability of ORDER module inside preDefine method.
*  THWILK  031020  Call Id 108383, Added the format mask to WO_NO and avoided an exception in the adjust()
*                  method by checking for && headset.countRows() > 0 in the if condition.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031216  Edge Developments - (Replaced blocks with tabs, Removed clone and doReset Methods)
*  SAPRLK  040225  Web Alignment - added multirow option to the headset, simplify code for RMBs,remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  THWILK  040421  Call 112951,Modified permits() and used cookie "PageID_CurrentWindow" to prevent the opening of the 
*                  previously visited window when an error is encountered.
*  THWILK  040604  Added PM_REVISION under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040607  Added lov for PM_NO to retrieve PM_NO only under IID AMEC109A, Multiple Standard Jobs on PMs.
*  SHAFLK  040419  Bug 43848, Modified methods run(),finished(),adjust() and HTML part.Added method CheckObjInRepairShop() and finished().
*  THWILK  040625  Merged Bug 43848.
*  SHAFLK  040514  Bug 44632, Added new field Man hours to head block.
*  THWILK  040701  Merged Bug 44632.
*  ARWILK  040714  Added the two tabs "Jobs" and "Tools and Facilities".
*  ARWILK  040716  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  THWILK  040729  Added Project Information Block.(IID AMUT219).
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  THWILK  040810  Merged Bug 41817.
*  ARWILK  040820  Modified method okFind. Also changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  THWILK  040825  Call 117308, Modified released() and started().
*  SHAFLK  040726  Bug 43249, Modified method preDefine().
*  NIJALK  040902  Merged bug 43249.
*  ARWILK  040909  Modified setFieldValsItem2 to correct price fetching.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NIJALK  040929  Modified  setValuesInMaterials().
*  NIJALK  041001  Renamed "Signature" to "Executed By" in Crafts, Jobs tabs.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041105  Added field Std Job Status to Jobs tab. 
*  SHAFLK  040916  Bug 46621, Modified method preDefine().
*  Chanlk  041110  Merged Bug 46621.
*  NIJALK  041118  Added field employee_id to the crafts and jobs tab.
*  NIJALK  050301  Added "Multiple material requisitions Exist" check box.
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
*  SULILK  060315  Call 137373: Modified preDefine() and removed the java script for LOV Work Leader.
*  SULILK  060320  Call 135197: Modified preDefine(),setValuesInMaterials().
*  ILSOLK  060727  Merged Bud ID 59224. 
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
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  ILSOLK  071214  Bug Id 68773, Eliminated XSS.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  090217  Bug 79436  Modified preDefine(), released() and started(). 
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit(). 
*  HARPLK  090630  Bug 84436, Modified preDefine().
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

public class ActiveSeparate2light extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2light");

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

    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;

    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;

    private ASPBlock itemblk;
    private ASPBlock itemblk1;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;

    private ASPRowSet itemset1;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;
    private ASPBlockLayout itemlay1;

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
    // 031216  ARWILK  Begin  (Replace blocks with tabs)
    private ASPTabContainer tabs;
    // 031216  ARWILK  End  (Replace blocks with tabs)

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private boolean repVal;
    private boolean comBarAct;
    private ASPTransactionBuffer secBuff;
    private ASPQuery q;
    private int headrowno;
    private int n;
    private int i;
    private ASPBuffer row;
    private int headsetRowNo;
    private int item3rowno;
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
    private double nBuyQtyDue;
    private ASPCommand cmd;
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
    public ActiveSeparate2light(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new String[6] ;
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        repVal = ctx.readFlag("REPVAL",false);
        comBarAct = ctx.readFlag("COMBARACT",false);
        keepQuery = ctx.readValue("KEEPQUE",keepQuery);
        actEna1 = ctx.readFlag("ACTENA1",false);
        actEna2 = ctx.readFlag("ACTENA2",false);
        actEna3 = ctx.readFlag("ACTENA3",false);
        again = ctx.readFlag("AGAIN",false);
        repair =  ctx.readValue("REPAIR","FALSE");
        unissue = ctx.readValue("UNISSUE","FALSE");

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        else if (mgr.dataTransfered())
			okFind();

        else if (mgr.commandBarActivated())
        {
            comBarAct = true; 
            eval(mgr.commandBarFunction());
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        }
        //Bug 43848, start
        else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
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
        //Bug 43848, end
        else
            clear();
        adjust();

        ctx.writeValue("KEEPQUE",keepQuery);
        ctx.writeFlag("ACTENA1",actEna1);
        ctx.writeFlag("ACTENA2",actEna2);
        ctx.writeFlag("ACTENA3",actEna3);
        ctx.writeFlag("AGAIN",again);

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031216  ARWILK  End  (Replace blocks with tabs)
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

        ASPQuery tempValueForblk;

        trans.clear();

        keepQuery = mgr.createSearchURL(headblk);

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

        mgr.querySubmit(trans,headblk);
        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM();
            okFindITEM0();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTNODATA: No data found."));
            headset.clear();
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
            q.addParameter("ROW_NO",itemset0.getRow().getValue("ROW_NO"));
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
        n = itemset0.countRows();
        itemset0.first();

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
                planMen = 0;
            double planHours = itemset0.getRow().getNumberValue("PLAN_HRS");
            if (isNaN(planHours))
                planHours = 0;
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
                        numCost = 0;
                    numCostAmt =(numCost * plannedMen * plannedHours);
                }
                else
                {
                    numCost = 0;
                    numCostAmt = 0;
                }
            }

            if (numCost == 0 && !mgr.isEmpty(itemset0.getRow().getValue("ROLE_CODE")))
            {
                numCost= trans.getNumberValue("COSTB"+i+"/DATA/COST2");

                if (isNaN(numCost))
                    numCost = 0;

                numCostAmt =(numCost * plannedMen * plannedHours);
            }

            if (numCost == 0  && !mgr.isEmpty(itemset0.getRow().getFieldValue("ITEM0_CONTRACT")) && !mgr.isEmpty(itemset0.getRow().getFieldValue("ITEM0_ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTC"+i+"/DATA/COST3");

                if (isNaN(numCost))
                    numCost = 0;

                numCostAmt =(numCost * plannedMen * plannedHours);
            }

            if (numCost == 0  && !mgr.isEmpty(headset.getRow().getFieldValue("CONTRACT")) && !mgr.isEmpty(headset.getRow().getFieldValue("ORG_CODE")))
            {
                numCost= trans.getNumberValue("COSTD"+i+"/DATA/COST4"); 

                if (isNaN(numCost))
                    numCost = 0;

                numCostAmt =(numCost * plannedMen * plannedHours); 
            }

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATABASE_PRICE");
            if (isNaN( nBaseUnitPrice))
                nBaseUnitPrice = 0;
            double nSaleUnitPrice= trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            if (isNaN( nSaleUnitPrice))
                nSaleUnitPrice = 0;
            double nDiscount= trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            if (isNaN( nDiscount))
                nDiscount = 0;
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


    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk3);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");

        mgr.submit(trans);


        if (comBarAct)
        {
            if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTNODATA: No data found."));
                itemset3.clear();
            }
        }
        headset.goTo(headrowno);
    }


    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
    }


    public void okFindITEM()
    {
        ASPManager mgr = getASPManager();

        if (itemset3.countRows() > 0)
        {
            trans.clear();
            headsetRowNo = headset.getCurrentRowNo();
            item3rowno = itemset3.getCurrentRowNo();
            q = trans.addQuery(itemblk);
            q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
            q.addParameter("ITEM3_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans);
            headset.goTo(headsetRowNo);
            itemset3.goTo(item3rowno);

            if (itemset.countRows() > 0)
                setValuesInMaterials();
                
        }
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

        mgr.submit(trans);
        //mgr.querySubmit(trans, itemblk4);

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
        mgr.submit(trans);
        //mgr.querySubmit(trans, itemblk5);

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

    public void setValuesInMaterials()
    {
        ASPManager mgr = getASPManager();
        String priceListNo = "";
        double nPlanQty;
        trans.clear(); 

        n = itemset.countRows();
        String cataNo = "";

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
                String planLineNo = itemset.getRow().getFieldValue("ITEM_PLAN_LINE_NO");

                /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo); */

                String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset.getRow().getFieldValue("CONDITION_CODE");

                cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo);
                cmd.addParameter("SERIAL_NO",serialNo);
                cmd.addParameter("CONFIGURATION_ID","*");
                cmd.addParameter("CONDITION_CODE",conditionCode);

                if ((!mgr.isEmpty(cataNo)) && (!mgr.isEmpty(String.valueOf(nPlanQty))))
                {
                    cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("ITEM_BASE_PRICE","0");
                    cmd.addParameter("ITEM_SALE_PRICE","0");
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
                    cmd.addParameter("ITEM_PLAN_LINE_NO",planLineNo);
                }
                itemset.next();
            }           

            trans = mgr.validate(trans);

            itemset.first();

            for (i=0; i<n; ++i)
            {
                double nCost = 0;
                double nCostAmount = 0;

                row = itemset.getRow();
                if (!mgr.isEmpty(itemset.getRow().getFieldValue("PART_NO")))
                    nCost= trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                if (isNaN(nCost))
                    nCost=0;
                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty=0;
                if (!mgr.isEmpty(String.valueOf(nPlanQty)))
                {
                    double nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                    if (isNaN(nCostTemp))
                        nCostTemp=0;
                    nCostAmount = nCostTemp * nPlanQty;
                }
                else
                    nCostAmount = 0;

                priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
                double nDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount=0;
                if ((!mgr.isEmpty(cataNo)) && (!mgr.isEmpty(String.valueOf(nPlanQty))))
                {
                    double listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                    double planQty = 0;
                    double nSalesPriceAmount = 0;
                    if (isNaN(listPrice))
                        listPrice=0;
                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getValue("ITEM_PRICE_LIST_NO");

                    if (mgr.isEmpty(String.valueOf(nDiscount)))
                        nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");
                    if (isNaN(nDiscount))
                        nDiscount=0;
                    if (mgr.isEmpty(String.valueOf(listPrice)))
                    {
                        listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_BASE_PRICE");
                        if (isNaN(listPrice))
                            listPrice=0;
                        planQty =trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY"); 
                        if (isNaN(planQty))
                            planQty=0;
                        double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_SALE_PRICE");
                        if (isNaN(nSaleUnitPrice))
                            nSaleUnitPrice=0;
                        nSalesPriceAmount  = nSaleUnitPrice * planQty;
                    }
                    else
                    {
                        double nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                        if (isNaN(nListPriceTemp))
                            nListPriceTemp=0;
                        planQty =trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty=0;
                        double nDiscountTemp = itemset.getRow().getNumberValue("DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp=0;
                        if (mgr.isEmpty(String.valueOf(nDiscountTemp)))
                            nDiscountTemp = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp=0;
                        nSalesPriceAmount = nListPriceTemp * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscountTemp / 100 * nSalesPriceAmount);
                    }

                    row.setValue("PRICE_LIST_NO",priceListNo);
                    row.setNumberValue("LIST_PRICE",listPrice);
                    row.setNumberValue("ITEM_DISCOUNT",nDiscount);
                    row.setNumberValue("ITEMSALESPRICEAMOUNT",nSalesPriceAmount);
                }

                row.setNumberValue("ITEM_COST",nCost);
                row.setNumberValue("AMOUNTCOST",nCostAmount);

                itemset.setRow(row);

                itemset.next();
            }
        }
        itemset.first();
    }


    public void countFindITEM()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("ITEM3_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
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

        double nsalePriceAmt = 0;
        trans.clear();
        n = itemset2.countRows();
        itemset2.first();

        trans.clear();

        for (i=0; i<= n; ++i)
        {
            String catalogNo = itemset2.getRow().getFieldValue("ITEM2_CATALOG_NO");
            String catContract =itemset2.getRow().getFieldValue("ITEM2_CATALOG_CONTRACT");
            String headContract = headset.getRow().getValue("CONTRACT");
            String headOrgCode = headset.getRow().getValue("ORG_CODE");
            String customerNo = headset.getRow().getValue("CUSTOMER_NO");
            String agreementID = headset.getRow().getValue("AGREEMENT_ID");
            String priceListNo = itemset2.getRow().getFieldValue("ITEM2_PRICE_LIST_NO");
            nBuyQtyDue = itemset2.getRow().getNumberValue("QUANTITY");

            if (isNaN(nBuyQtyDue))
                nBuyQtyDue = 0;

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

        cmd = trans.addCustomFunction("GETCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","COSTTYPE2");
        cmd = trans.addCustomFunction("GETCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","COSTTYPE3");

        trans = mgr.validate(trans);
        itemset2.first();

        String sCostTypeExternal = trans.getValue("GETCOSTTYPE2/DATA/COSTTYPE2");
        String sCostTypeExpenses = trans.getValue("GETCOSTTYPE3/DATA/COSTTYPE3");

        for (i = 0; i<= n; ++i)
        {
            double numCost = 0;
            double numCostAmount = 0;
            double nQty = itemset2.getRow().getNumberValue("QUANTITY");

            if (isNaN(nQty))
                nQty = 0;

            double nQtyInv = itemset2.getRow().getNumberValue("QTY_TO_INVOICE");

            if (isNaN(nQtyInv))
                nQtyInv = 0;

            row = itemset2.getRow();

            String woCostTypeVar = row.getValue("WORK_ORDER_COST_TYPE");

            numCost = trans.getNumberValue("COSTWP"+i+"/DATA/COST2");

            if (isNaN(numCost))
                numCost = 0;

            if (numCost == 0 && !mgr.isEmpty(row.getFieldValue("ITEM2_CATALOG_CONTRACT")) && !mgr.isEmpty(row.getFieldValue("ITEM2_CATALOG_NO")))
            {
                if (isSecure[1] == "true")
                {
                    numCost = trans.getNumberValue("COSTA"+i+"/DATA/COST");
                    if (isNaN(numCost))
                        numCost = 0;
                }
            }
            else if (numCost == 0 && !mgr.isEmpty(headset.getRow().getValue("CONTRACT")) && !mgr.isEmpty(headset.getRow().getValue("ORG_CODE")))
            {
                numCost = trans.getNumberValue("COSTC"+i+"/DATA/COST3");

                if (isNaN(numCost))
                    numCost = 0;
            }
            else if (isNaN(numCost))
                numCost = 0;

            numCostAmount = numCost * nQty;

            double nBaseUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
            if (isNaN(nBaseUnitPrice))
                nBaseUnitPrice = 0;
            double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
            if (isNaN(nSaleUnitPrice))
                nSaleUnitPrice = 0;
            double nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;
            nBuyQtyDue = itemset2.getRow().getNumberValue("QUANTITY");
            if (isNaN(nBuyQtyDue))
                nBuyQtyDue = 0;
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
                        nsalePriceAmt =nBaseUnitPrice * nQty;
                        nsalePriceAmt = nsalePriceAmt - (nDiscount / 100 * nsalePriceAmt);
                    }  
                }
                else
                {
                    if (nDiscount == 0)
                        nsalePriceAmt = nBaseUnitPrice *nQtyInv;
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
            row.setNumberValue("ITEM2_SALESPRICE",nBaseUnitPrice);

            itemset2.setRow(row);

            itemset2.next();
        }

        itemset2.first();   
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
        q.addWhereCondition("WO_NO = ?  AND PARENT_ROW_NO IS NULL");
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
        q.addWhereCondition("WO_NO = ? AND SPARE_CONTRACT = User_Allowed_Site_API.Authorized(SPARE_CONTRACT)");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
        headset.goTo(headrowno);  
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

        mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2LIGHTNONE: No RMB method has been selected."));
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

        urlString = createTransferUrl("ActiveSeperateReportInWorkOrder.page", transferBuffer);

        newWinHandle = "reportIn"; 
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

        urlString = createTransferUrl("ActiveSeparate2.page", transferBuffer);

        newWinHandle = "PrepareWO"; 
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
        perform("TO_PREPARE__");
    }

    public void prepared()
    {
        perform("PREPARE__");
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
        perform("WORK__");
    }

    public void reported()
    {
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
    }

    public void reported1()
    {
        perform("REPORT__");
    }

    public void finished()
    {
        ASPManager mgr = getASPManager();
        repair = "FALSE";
        unissue = "FALSE";

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
    public void finished1()
    {
        ASPManager mgr = getASPManager();

        statusRMBPerformed = true;
        perform("FINISH__");

    }      


    public void cancelled()
    {
        perform("CANCEL__");
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

        boolean orderInst = false; 

        headblk.addField("MODULENAME").
        setHidden().
        setFunction("''");

        if (isModuleInst("ORDER"))
            orderInst = true;
        else
            orderInst = false;  

        headblk.addField("WO_NO","Number","#").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2LIGHTWONO: WO No").
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
        setLabel("PCMWACTIVESEPARATE2LIGHTCONTRACT: Site").
        setDefaultNotVisible().
        setUpperCase().
        setHilite().
        setMaxLength(5);

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(5).
        setHilite().    
        setLabel("PCMWACTIVESEPARATE2LIGHTMCHCODECONTRACT: Site");

        headblk.addField("MCH_CODE").
        setSize(20).
        setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTMCHCODE: Object ID").
        setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN").
        setUpperCase().
        setHilite().
        setMaxLength(100); 

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(20).
        setHilite().   
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTMCHCODEDESC: Description").
        setReadOnly();

        headblk.addField("CONNECTION_TYPE").
        setSize(20).
        setSelectBox().
        setReadOnly().
        setHilite().    
        enumerateValues("MAINT_CONNECTION_TYPE_API").
        setLabel("PCMWACTIVESEPARATE2LIGHTCONNECTIONTYPE: Connection Type");

        headblk.addField("STATE").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTSTATE: Status").
        setReadOnly().
        setHilite().
        setMaxLength(30); 

        headblk.addField("ORG_CODE").
        setSize(20).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setMandatory().
        setLabel("PCMWACSEP2LITEXEDEPT: Maintenance Organization").
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
        setLabel("PCMWACTIVESEPARATE2LIGHTWORKLEADSIGN: Work Leader").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);
        
        headblk.addField("WORK_MASTER_SIGN").
        setSize(12).
        setDynamicLOV("MAINT_EMPLOYEE",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTWORKMASTERSIGN: Executed By").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);

        headblk.addField("ERR_DESCR").
        setSize(30).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2LIGHTERRDESCR: Directive").
        setHilite().
        setMaxLength(60); 

        headblk.addField("PRIORITY_ID").
        setSize(8).
        setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
        setLabel("PCMWACTSEP2LTPRTYID: Priority").
        setUpperCase().
        setMaxLength(1); 

        headblk.addField("OP_STATUS_ID").
        setSize(18).
        setDynamicLOV("OPERATIONAL_STATUS",600,450).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTOPSTATUS: Operational Status").
        setUpperCase().
        setMaxLength(3);

        headblk.addField("PLAN_S_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTPLANSDATE: Planned Start");

        headblk.addField("PLAN_F_DATE","Datetime").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTPLANFDATE: Planned Completion"); 

        headblk.addField("PLANNED_MAN_HRS","Number").
        setSize(10).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTPLANMANHOURS: Total Man Hours");

        headblk.addField("PM_NO","Number", "#").
        setSize(8). 
        setLOV("PmActionLov1.page",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTPMNO: PM No").
        setReadOnly(); 

        headblk.addField("PM_REVISION").
        setSize(8). 
        setDynamicLOV("PM_ACTION","PM_NO",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTPMREV: PM Revision").
        setReadOnly().
        setMaxLength(6); 

        headblk.addField("WORK_TYPE_ID").
        setSize(8).
        setDefaultNotVisible().
        setDynamicLOV("WORK_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTWORKTYPEID: Work Type").
        setUpperCase().
        setMaxLength(2); 

        headblk.addField("CONTRACT_ID").
        setDynamicLOV("PSC_CONTR_PRODUCT_LOV").           
        setUpperCase().
        setMaxLength(15).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTCONTRACTID: Contract ID").
        setSize(15);

        //Bug 84436, Start  
        f = headblk.addField("CONTRACT_NAME");                  
        f.setDefaultNotVisible();
         if (mgr.isModuleInstalled("SRVCON"))
          f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
         else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCONTRACTNAME: Contract Name");
        f.setSize(15);        
        //Bug 84436, End
        
        headblk.addField("LINE_NO","Number").
        setDynamicLOV("PSC_CONTR_PRODUCT_LOV").
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTLINENO: Line No").
        setSize(10); 

        //Bug 84436, Start
        f = headblk.addField("LINE_DESC");                     
        f.setDefaultNotVisible();        
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");        
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTLINEDESC: Description");
        f.setSize(15);
         
        
                
         f = headblk.addField("CONTRACT_TYPE");                     
         f.setDefaultNotVisible();
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
         else
            f.setFunction("''");
         f.setReadOnly();
         f.setLabel("PCMWACTIVESEPARATE2LIGHTCONTRACTTYPE: Contract Type");
         f.setSize(15);        
        
         
        f = headblk.addField("INVOICE_TYPE");                
        f.setDefaultNotVisible();
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");  
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCONTRACTINVOICETYPE: Invoice Type");
        f.setSize(15);
        //Bug 84436, End
        
        headblk.addField("AGREEMENT_ID").
        setLabel("PCMWACTIVESEPARATE2LIGHTAGREEMENTID: Agreement ID").
        setSize(10). 
        setDefaultNotVisible().
        setHidden().
        setReadOnly();

        headblk.addField("CUSTOMER_NO").
        setLabel("PCMWACTIVESEPARATE2LIGHTCUSTOMERNO: Customer No").
        setSize(20). 
        //setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)").
        setDefaultNotVisible().  
        setHilite().
        setReadOnly();

        headblk.addField("CUSTODESCR").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2LIGHTCUSTOMERDESC: Customer Name").
        setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)").
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

        headblk.addField("REPIN").
        setFunction("''").
        setHidden(); 

        headblk.addField("OBJEVENTS").
        setHidden();    

        headblk.addField("CONNECTION_TYPE_DB").setHidden().setFunction("''");

        if (mgr.isModuleInstalled("PROJ"))
        {

            f = headblk.addField("PROGRAM_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTPROGRAMID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Program_Id(:PROJECT_NO)");
            f.setSize(20);

            f = headblk.addField("PROGRAMDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTPROGRAMDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:PROJECT_NO),PROJECT_API.Get_Program_Id(:PROJECT_NO))");
            f.setSize(30);

            f = headblk.addField("PROJECT_NO");
            f.setSize(12);
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setLabel("PCMWACTIVESEPARATE2LIGHTPROJECTNO: Project No");
            f.setMaxLength(10);

            f = headblk.addField("PROJECTDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTPROJECTDESC: Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
            f.setSize(30);

            f = headblk.addField("SUBPROJECT_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSUBPROJECTID: Sub Project ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("SUBPROJECTDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTSUBPROJECTDESC: Sub Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_ID");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTACTIVITYID: Activity ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("ACTIVITYDESC");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTACTIVITYDESC: Activity Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWACTIVESEPARATE2LIGHTACTIVITYSEQ: Activity Sequence");
            f.setSize(20);
            f.setDefaultNotVisible();
            f.setReadOnly();
        }


        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","RE_INIT__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2LIGHTHD: Work Order Information"));
        headtbl.setWrap();

        //Web Alignment - Multirow Action
        headtbl.enableRowSelect();
        //

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("reportIn",mgr.translate("PCMWACTIVESEPARATE2LIGHTREPIN: Report In..."));
        headbar.addCustomCommand("prepareWork",mgr.translate("PCMWACTIVESEPARATE2LIGHTPREWORK: Prepare Work Order..."));
        headbar.addCustomCommand("repInWiz",mgr.translate("PCMWACTIVESEPARATE2LIGHTREPORTINWIZ: Report in Wizard..."));
        headbar.addCustomCommandSeparator(); 

        headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPARATE2LIGHTFAULTWORK: FaultReprt\\WorkRequest"));
        headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPARATE2LIGHTOBSERVED: Observed"));
        headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPARATE2LIGHTUNDPREP: Under Preparation"));
        headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPARATE2LIGHTPREPARED: Prepared"));
        headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPARATE2LIGHTRELEASED: Released"));
        headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPARATE2LIGHTSTARTED: Started"));
        headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPARATE2LIGHTWRKDONE: Work Done"));
        headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPARATE2LIGHTREPORTED: Reported"));
        headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPARATE2LIGHTFINISHED: Finished"));
        headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPARATE2LIGHTCANCELLED: Cancelled"));

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        headbar.addCustomCommand("activateCrafts", "");
        headbar.addCustomCommand("activatePalnning", "");   
        headbar.addCustomCommand("activateMaterials", "");
        headbar.addCustomCommand("activateToolsAndFacilities", "");
        headbar.addCustomCommand("activateJobs", "");
        // 031216  ARWILK  End  (Replace blocks with tabs)

        headbar.addCustomCommandGroup("WORKORDSTATUS",mgr.translate("ACTIVESEPARATE2LIGHTWORKORDSTAT: Work Order Status"));
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

        headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 
        headlay.defineGroup("","MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CONNECTION_TYPE,ERR_DESCR,WO_NO,CONTRACT,STATE,ORG_CODE,ORGCODEDESCR,PM_NO,PM_REVISION,AGREEMENT_ID,CUSTOMER_NO,CUSTODESCR,REPORTED_BY,NAME,PRIORITY_ID,WORK_LEADER_SIGN,WORK_MASTER_SIGN,WORK_TYPE_ID,OP_STATUS_ID,PLAN_S_DATE,PLAN_F_DATE,PLANNED_MAN_HRS",false,true);
        if (mgr.isModuleInstalled("PROJ"))
            headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2LIGHTLABEL0: Project Information"),"PROGRAM_ID,PROGRAMDESC,PROJECT_NO,PROJECTDESC,SUBPROJECT_ID,SUBPROJECTDESC,ACTIVITY_ID,ACTIVITYDESC,ACTIVITY_SEQ",true,false);

        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("ORGCODEDESCR");
        headlay.setSimple("CUSTODESCR");
        headlay.setSimple("NAME");
	headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTCRAFTSTAB: Operations"), "javascript:commandSet('HEAD.activateCrafts', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTPLANNINGTAB: Planning"), "javascript:commandSet('HEAD.activatePalnning', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTMATERIALTAB: Materials"), "javascript:commandSet('HEAD.activateMaterials', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTTANDFTAB: Tools & Facilities"), "javascript:commandSet('HEAD.activateToolsAndFacilities', '')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2LIGHTJOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs', '')");
        // 031216  ARWILK  End  (Replace blocks with tabs)

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
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2LIGHTROWNO: Operation No").
        setReadOnly();

        itemblk0.addField("ITEM0_DESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0DESCRIPTION: Description").
        setDbName("DESCRIPTION").
        setReadOnly().
        setMaxLength(60);

        itemblk0.addField("ITEM0_JOB_ID", "Number").
        setDbName("JOB_ID").
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0JOBID: Job Id");

        itemblk0.addField("ITEM0_COMPANY").
        setSize(8).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0COMPANY: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk0.addField("ITEM0_ORG_CODE").
        setSize(11).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2LIGHTITEM0ORCOLOV: List of Maintenance Organization")).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0ORGCODE: Maintenance Organization").
        setUpperCase().
        setDbName("ORG_CODE").
        setMaxLength(8); 

         //Bug ID 59224,start
        itemblk0.addField("ITEM0_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2LIGHTORGCODEDESC: Maint. Org. Description").
        setFunction("Organization_Api.Get_Description(:ITEM0_CONTRACT,:ITEM0_ORG_CODE)").
        setReadOnly();
        //Bug ID 59224,end
          
        itemblk0.addField("ITEM0_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0CONTRACT: Maint. Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setReadOnly().
        setMaxLength(5);

        itemblk0.addField("ROLE_CODE").
        setSize(9).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445).
        setLabel("PCMWACTIVESEPARATE2LIGHTROLECODE: Craft ID").
        setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2LIGHTROLECODELOV: List of Craft ID")).
        setUpperCase().
        setMaxLength(10);

        //Bug ID 59224,start
       itemblk0.addField("ROLE_CODE_DESC").
       setLabel("PCMWACTIVESEPARATE2LIGHTROLECODEDESC: Craft Description").
       setFunction("ROLE_API.Get_Description(:ROLE_CODE)").
       setReadOnly();
       //Bug ID 59224,end

        itemblk0.addField("SIGN").
        setSize(10).
        setDynamicLOV("EMPLOYEE_LOV","ITEM0_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTSIGN: Executed By").
        setUpperCase().
        setMaxLength(20);  

        itemblk0.addField("ITEM0_SIGN_ID").
        setDbName("SIGN_ID").
        setSize(10).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0SIGNID: Employee ID").
        setUpperCase().
        setReadOnly().
        setMaxLength(20);  

        itemblk0.addField("PLAN_MEN","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTPLANMEN: Planned Men");

        itemblk0.addField("PLAN_HRS","Number").
        setSize(14).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM0PLANHRS: Planned Hours");
        
        itemblk0.addField("TOTAL_MAN_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTMANHRS: Total Man Hours").
        setFunction(":PLAN_MEN * :PLAN_HRS").
        setReadOnly();

        itemblk0.addField("ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTALLOCATEDHRS: Allocated Hours").
        setReadOnly();

        itemblk0.addField("TOTAL_ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTTOTALLOCATEDHRS: Total Allocated Hours").
        setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM0_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk0.addField("TOTAL_REMAINING_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2LIGHTTOTALREMAININGHRS: Total Remaining Hours").
        setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM0_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk0.addField("TEAM_CONTRACT").
        setSize(7).
        setLabel("PCMWACTIVESEPARATE2LIGHTCONT: Team Site").
        setUpperCase();

        itemblk0.addField("TEAM_ID").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2LIGHTTID: Team ID").
        setUpperCase();

        itemblk0.addField("TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
        setSize(40).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2LIGHTDESC: Description");

        itemblk0.addField("CATALOG_CONTRACT").
        setSize(15).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2LIGHTCATACONTRACT: Sales Part Site").
        setUpperCase().
        setMaxLength(5);  

        f = itemblk0.addField("CATALOG_NO");
        f.setSize(17);
        if (orderInst)
            f.setDynamicLOV("SALES_PART_SERVICE_LOV","CATALOG_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCATOGNO: Sales Part Number");
        f.setUpperCase();
        f.setMaxLength(25);

        f = itemblk0.addField("SALESPARTDESC");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSALESPARDESC: Sales Part Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");                
        f.setReadOnly();
        f.setMaxLength(2000);

        f = itemblk0.addField("PRICE_LIST_NO");
        f.setSize(13);
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPRICELISTNO: Price List No");
        f.setUpperCase();
        f.setHidden();
        f.setMaxLength(10);  

        itemblk0.addField("COST","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTCOSTITEM0: Cost").
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("COSTAMOUNT","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTCOSTAMT: Cost Amount").
        setHidden().
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("LISTPRICE", "Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTLISTPRICE: Sales Price").
        setFunction("''").
        setReadOnly();  

        itemblk0.addField("DISCOUNT").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTDISCONT: Discount %").
        setFunction("''");

        itemblk0.addField("PREDECESSOR").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2LIGHTPRED: Predecessors").
        setDefaultNotVisible().
        setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:ITEM0_WO_NO, :ROW_NO)");
        
        itemblk0.addField("SALESPRICEAMOUNT", "Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTSALESPRICEAMOUNT: Sales Price Amount").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk0.addField("DATE_FROM","Datetime").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTDATEFROM: Date from");

        itemblk0.addField("DATE_TO","Datetime").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2LIGHTDATETO: Date to");

        itemblk0.addField("TOOLS").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTTOOLS: Tools").
        setHidden().
        setMaxLength(25);

        itemblk0.addField("REMARK").
        setSize(14).
        setLabel("PCMWACTIVESEPARATE2LIGHTREMARK: Remark").
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
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6PARENTRNO: Parent Operation No").
       setHidden();

       itemblk6.addField("ITEM6_ROW_NO","Number").
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6ROWNO: Operation No").
       setDbName("ROW_NO").
       setHidden().
       setInsertable();

       itemblk6.addField("ITEM6_WO_NO","Number","#").
       setHidden().
       setLabel("PCMWAACTIVESEPARATE2LIGHTITEM6WONO: WO No").
       setDbName("WO_NO");

       itemblk6.addField("ITEM6_COMPANY").
       setSize(8).
       setHidden().
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6COMPANY: Company").
       setUpperCase().
       setDbName("COMPANY");
                            
       itemblk6.addField("ALLOCATION_NO","Number").
       setReadOnly().
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6ALLOCNO: Allocation No");

       itemblk6.addField("ITEM6_DESCRIPTION").
       setDbName("DESCRIPTION").
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6ALLOCDESC: Description");

       itemblk6.addField("ITEM6_SIGN").
       setDbName("SIGN").
       setSize(10).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6SIGN: Executed By").
       setUpperCase();
       
       itemblk6.addField("ITEM6_SIGN_ID").
       setDbName("SIGN_ID").
       setSize(10).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6SIGNID: Employee ID").
       setUpperCase().
       setReadOnly();

       itemblk6.addField("ITEM6_ORG_CODE").
       setSize(11).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6ORGCODE: Maintenance Organization").
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
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6CONTRACT: Maint. Org. Site").
       setUpperCase().
       setDbName("CONTRACT").
       setReadOnly();

       itemblk6.addField("ITEM6_ROLE_CODE").
       setSize(9).
       setDbName("ROLE_CODE").
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6ROLECODE: Craft ID").
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
       setLabel("PCMWACTIVESEPARATE2LIGHTALLOCATEDHRS: Allocated Hours").
       setReadOnly();

       itemblk6.addField("ITEM6_TEAM_CONTRACT").
       setDbName("TEAM_CONTRACT").
       setSize(7).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6CONT: Team Site").
       setUpperCase();

       itemblk6.addField("ITEM6_TEAM_ID").
       setSize(13).
       setDbName("TEAM_ID").
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6TID: Team ID").
       setUpperCase();

       itemblk6.addField("ITEM6_TEAMDESC").
       setFunction("Maint_Team_API.Get_Description(:ITEM6_TEAM_ID,:ITEM6_TEAM_CONTRACT)").
       setSize(40).
       setReadOnly().
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6DESC: Description");

       itemblk6.addField("ITEM6_DATE_FROM","Datetime").
       setDbName("DATE_FROM").
       setSize(18).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6DATEFROM: Date From");

       itemblk6.addField("ITEM6_DATE_TO","Datetime").
       setDbName("DATE_TO").
       setSize(18).
       setLabel("PCMWACTIVESEPARATE2LIGHTITEM6DATETO: Date To");
       
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
        //-------------- This part belongs to MATERIALS TAB -----------------------
        //-----------------------------------------------------------------------

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk3.addField("ITEM3_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = itemblk3.addField("ITEM3_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");

        f = itemblk3.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk3.addField("ITEM3_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setCustomValidation("ITEM3_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM3_CONTRACT,ITEM3_COMPANY,MCHCODE,ITEM3DESCRIPTION");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTWO_NO: Workorder No");

        f = itemblk3.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTMCHCODE: Object ID");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
        f.setUpperCase();

        f = itemblk3.addField("ITEM3DESCRIPTION");
        f.setSize(30);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("SIGNATURE");
        f.setSize(8);
        f.setMaxLength(2000);
        f.setCustomValidation("SIGNATURE,ITEM3_COMPANY","SIGNATURE_ID,SIGNATURENAME");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk3.addField("SIGNATURENAME");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM3_CONTRACT),:SIGNATURE_ID)");

        f = itemblk3.addField("ITEM3_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTITEM3CONTRACT: Site");
        f.setUpperCase();

        f = itemblk3.addField("ENTERED","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTENTERED: Entered");

        f = itemblk3.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setCustomValidation("INT_DESTINATION_ID,ITEM3_CONTRACT","INT_DESTINATION_DESC");
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTINT_DESTINATION_ID: Int Destination");

        f = itemblk3.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk3.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTDUE_DATE: Due Date");

        f = itemblk3.addField("ITEM3_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSTATE: Status");

        f = itemblk3.addField("NREQUISITIONVALUE", "Number");
        f.setSize(8);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTNREQUISITIONVALUE: Total Value");
        f.setFunction("''");

        f = itemblk3.addField("SNOTETEXT");
        f.setSize(15);
        f.setHidden();
        f.setMaxLength(2000);
        f.setFunction("''");

        f = itemblk3.addField("SIGNATURE_ID");
        f.setSize(6);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(11);
        f.setHidden();
        f.setUpperCase();

        f = itemblk3.addField("NNOTEID", "Number");
        f.setSize(6);
        f.setInsertable();
        f.setMaxLength(10);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("ORDERCLASS");
        f.setSize(3);
        f.setHidden();
        f.setMaxLength(3);
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk3.addField("SNOTEIDEXIST");
        f.setSize(4);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("ITEM3_COMPANY");
        f.setHidden();
        f.setDbName("COMPANY");
        f.setFunction("Site_API.Get_Company(:ITEM3_CONTRACT)");
        f.setUpperCase();

        f = itemblk3.addField("NPREACCOUNTINGID", "Number");
        f.setHidden();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk3.addField("MUL_REQ_LINE");
        f.setReadOnly();
        f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM3_WO_NO)");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTMULMATREQEXIST: Multiple Material Requisitions Exist"); 
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

        itemblk3.setView("MAINT_MATERIAL_REQUISITION");
        itemblk3.defineCommand("MAINT_MATERIAL_REQUISITION_API","PLAN__,RELEASE__,CLOSE__");
        itemset3 = itemblk3.getASPRowSet();

        itemblk3.setMasterBlock(headblk);

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setWrap();

        itembar3 = mgr.newASPCommandBar(itemblk3);

        itembar3.enableCommand(itembar3.FIND);

        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
        itemlay3.setSimple("INT_DESTINATION_DESC");
        itemlay3.setSimple("SIGNATURENAME");


        itemblk = mgr.newASPBlock("ITEM");

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
        f.setLabel("PCMWACTIVESEPARATE2LIGHTLINE_ITEM_NO: Line No");

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25);
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN");
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT","CATALOG_NO,CATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS");
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSPAREDESCRIPTION: Part Description");
        f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

        //Bug 43249, start
        f = itemblk.addField("CONDITION_CODE");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk.addField("CONDDESC");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCONDDESC: Condition Code Description");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPARTOWNERSHIP: Ownership");
        f.setReadOnly();

        f = itemblk.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPARTOWNER: Owner");
        f.setReadOnly();
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setInsertable();
        f.setMaxLength(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTDIMQTY: Dimension/ Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTTYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(13);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTDATE_REQUIRED: Date Required");

        f = itemblk.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSUPPLYCODE: Supply Code");

        f = itemblk.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,CATALOG_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,LIST_PRICE,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM_COST,AMOUNTCOST,PRICE_LIST_NO,ITEM_DISCOUNT,LIST_PRICE,SALESPRICEAMOUNT");
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPLAN_QTY: Quantity Required");

        f = itemblk.addField("QTY","Number");
        f.setSize(13);
        f.setReadOnly();
        f.setInsertable();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTQTY: Quantity Issued");

        f = itemblk.addField("QTY_SHORT","Number");
        f.setHidden();
        f.setReadOnly();

        f = itemblk.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTQTYONHAND: Quantity on Hand");
        f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTQTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTQTY_RETURNED: Quantity Returned");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTUNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("ITEM_CATALOG_CONTRACT");
        f.setSize(10);
        f.setDbName("CATALOG_CONTRACT");
        f.setMaxLength(5);
        f.setDefaultNotVisible();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();

        f = itemblk.addField("ITEM_CATALOG_NO");
        f.setSize(9);
        f.setDbName("CATALOG_NO");
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setCustomValidation("CATALOG_NO,ITEM_WO_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_QTY,PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT","LIST_PRICE,ITEM_COST,AMOUNTCOST,CATALOGDESC,ITEM_DISCOUNT,SALESPRICEAMOUNT");
        if (orderInst)
            f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("CATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTCATALOGDESC: Sales Part Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");                

        f = itemblk.addField("ITEM_PRICE_LIST_NO");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDbName("PRICE_LIST_NO");
        f.setDefaultNotVisible();
        f.setCustomValidation("PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,ITEM_COST,PLAN_QTY,CATALOG_NO,PLAN_QTY,ITEM_WO_NO,PLAN_LINE_NO,ITEM_DISCOUNT,CATALOG_CONTRACT","ITEM_COST,AMOUNTCOST,LIST_PRICE,ITEM_DISCOUNT,SALESPRICEAMOUNT");
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTPRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("LIST_PRICE","Money");
        f.setSize(9);
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTLIST_PRICE: Sales Price");

        f = itemblk.addField("ITEM_DISCOUNT","Number");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setFunction("''");
        f.setLabel("PCMWACTIVESEPARATE2LIGHTDISCOUNT: Discount %");

        f = itemblk.addField("ITEMSALESPRICEAMOUNT", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIGHTITEMSALESPRICEAMOUNT: Price Amount");
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
        f.setLabel("PCMWACTIVESEPARATE2LIGHTAMOUNTCOST: Cost Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEA: Account");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEB: Cost Center");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEF: Project No");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEE: Object No");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEC: Code C");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODED: Code D");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEG: Code G");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEH: Code H");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEI: Code I");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTSCODEJ: Code J");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("ITEM_WO_NO","Number","#");
        f.setMandatory();
        f.setHidden();
        f.setDbName("WO_NO");

        f = itemblk.addField("ITEM_PLAN_LINE_NO","Number");
        f.setDbName("PLAN_LINE_NO");
        f.setReadOnly();
        f.setHidden();

        f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setHidden();
        f.setReadOnly();
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        f = itemblk.addField("ITEM_BASE_PRICE","Money");
        f.setHidden();
        f.setFunction("''");

        f = itemblk.addField("ITEM_SALE_PRICE","Money");
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
        itemblk.setMasterBlock(itemblk3);

        itembar = mgr.newASPCommandBar(itemblk);

        itembar.disableCommand(itembar.EDITROW);

        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM");

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

        itemblk2.addField("PLAN_LINE_NO").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("WORK_ORDER_COST_TYPE").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2LIGHTWORKORDCOSTTYPE: Work Order Cost Type").
        setMaxLength(200);

        itemblk2.addField("DATE_CREATED","Date").
        setHidden();

        itemblk2.addField("PLAN_DATE","Date").
        setHidden();

        itemblk2.addField("QUANTITY","Number").
        setSize(13).
        setLabel("PCMWACTIVESEPARATE2LIGHTQUANTITY: Quantity"); 

        itemblk2.addField("QTY_TO_INVOICE","Number").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTQTYTOINVOICE: Quantity To Invoice"); 

        itemblk2.addField("ITEM2_CATALOG_CONTRACT").
        setSize(15).
        setLabel("PCMWACTIVESEPARATE2LIGHTCATALOGCONT: Sales Part Site").
        setDbName("CATALOG_CONTRACT");

        itemblk2.addField("ITEM2_CATALOG_NO").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2LIGHTCATALOGNO: Sales Part Number").
        setDbName("CATALOG_NO");

        f = itemblk2.addField("ITEM2_CATALOGDESC");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTITEM2CATALDESC: Sales Part Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");                
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setHidden();

        f = itemblk2.addField("LINE_DESCRIPTION");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTITEM2LINEDESC: Sales Part Description");
        f.setReadOnly();

        f = itemblk2.addField("ITEM2_PRICE_LIST_NO");
        f.setSize(13);
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWACTIVESEPARATE2LIGHTITEM2_PRICELISTNO: Price List No");
        f.setUpperCase();
        f.setHidden();
        f.setMaxLength(10);
        f.setDbName("PRICE_LIST_NO");

        itemblk2.addField("ITEM2_COST","Number").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTCOSTITEM2: Cost").
        setFunction("''").
        setHidden().
        setReadOnly(); 

        itemblk2.addField("ITEM2_COSTAMOUNT","Money").
        setSize(12).
        setLabel("PCMWACTIVESEPARATE2LIGHTCOSTITEM2AMT: Cost Amount").
        setFunction("''").
        setReadOnly(); 

        itemblk2.addField("ITEM2_LISTPRICE","Number").
        setSize(11).
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM2LISTPRICE: Sales Price").
        setFunction("''").
        setHidden().
        setReadOnly();  

        itemblk2.addField("ITEM2_DISCOUNT").
        setLabel("PCMWACTIVESEPARATE2LIGHTITEM2DISCOUNT: Discount %").
        setFunction("''").
        setHidden();

        itemblk2.addField("ITEM2_SALESPRICEAMOUNT", "Money").
        setSize(18).
        setLabel("PCMWACTIVESEPARATE2LIGHTSALESPRICEAMOUNTITEM2: Sales Price Amount").
        setFunction("''").
        setReadOnly();  

        itemblk2.addField("COSTTYPE2").
        setHidden().
        setFunction("''");

        itemblk2.addField("COSTTYPE3").
        setHidden().
        setFunction("''");  

        itemblk2.addField("WORK_ORDER_INVOICE_TYPE").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2LIGHTWORKORDINVOICETYPE: Work OrderInvoice Type").
        setMaxLength(200);

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
        f.setLabel("ACTSEPLIGHTITEM4ROWNO: Row No");
        f.setSize(8);       

        f = itemblk4.addField("ITEM4_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("ACTSEPLIGHTITEM4CONTRACT: Site");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM4_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTITEM4LOVTITLE: Connect Tools and Facilities"));  
        f.setSize(8);
        f.setUpperCase();

        f = itemblk4.addField("TOOL_FACILITY_ID");
        f.setLabel(                        "ACTSEPLIGHTITEM4TFID: Tool/Facility Id");
        f.setLOV("ConnectToolsFacilitiesLov.page","ITEM4_CONTRACT CONTRACT,ITEM4_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTITEM4LOVTITLE: Connect Tools and Facilities"));      
        f.setSize(20);
        f.setUpperCase();

        f = itemblk4.addField("TOOL_FACILITY_DESC");
        f.setLabel("ACTSEPLIGHTITEM4TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setDefaultNotVisible();

        f = itemblk4.addField("TOOL_FACILITY_TYPE");
        f.setLabel("ACTSEPLIGHTITEM4TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);      
        f.setSize(20);
        f.setUpperCase();       

        f = itemblk4.addField("TYPE_DESCRIPTION");
        f.setLabel("ACTSEPLIGHTITEM4TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("ACTSEPLIGHTITEM4ORGCODE: Maintenance Organization");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM4_CONTRACT CONTRACT,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("ACTSEPLIGHTITEM4LOVTITLE: Connect Tools and Facilities"));      
        f.setSize(8);       
        f.setUpperCase();

        f = itemblk4.addField("ITEM4_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("ACTSEPLIGHTITEM4QTY: Planned Quantity");        
        //Bug ID 82934, End
        f.setSize(10);

        f = itemblk4.addField("ITEM4_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("ACTSEPLIGHTITEM4PLNHRS: Planned Hours");        
        f.setSize(10);      

        f = itemblk4.addField("ITEM4_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);       
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM4_WO_NO WO_NO");
        f.setLabel("PCMWACTIVEROUNDITEM4JOBID: Job Id");

        f = itemblk4.addField("ITEM4_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("ACTSEPLIGHTITEM4CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM4_WO_NO WO_NO",600,445);
        f.setSize(8);       
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");        
        f.setSize(8);       
        f.setHidden();

        f = itemblk4.addField("ITEM4_COST", "Money");
        f.setDbName("COST");
        f.setLabel("ACTSEPLIGHTITEM4COST: Cost");
        f.setSize(12);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");       
        f.setSize(10);
        f.setHidden();

        f = itemblk4.addField("ITEM4_COST_AMOUNT", "Money");
        f.setLabel("ACTSEPLIGHTITEM4COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM4_WO_NO, :ITEM4_QTY, :ITEM4_PLANNED_HOUR)");
        f.setSize(12);      
        f.setDefaultNotVisible(); 
            
        f = itemblk4.addField("ITEM4_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("ACTSEPLIGHTITEM4CATALOGCONTRACT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);               
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("ACTSEPLIGHTITEM4CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM4_CATALOG_NO_CONTRACT CONTRACT",600,445);        
        f.setSize(20);
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_CATALOG_NO_DESC");
        f.setLabel("ACTSEPLIGHTITEM4CATALOGDESC: Sales Part Description");      
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
        f.setLabel("ACTSEPLIGHTITEM4SALESPRICE: Sales Price");      
        f.setSize(12);      
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("ACTSEPLIGHTITEM4SALESCURR: Sales Currency");
        f.setSize(10);              
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("ACTSEPLIGHTITEM4DISCOUNT: Discount");       
        f.setSize(8);       
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk4.addField("ITEM4_DISCOUNTED_PRICE", "Money");
        f.setLabel("ACTSEPLIGHTITEM4DISCOUNTEDPRICE: Discounted Price");        
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
        f.setLabel("ACTSEPLIGHTITEM4PLANNEDPRICE: Planned Price Amount");       
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
        f.setLabel("ACTSEPLIGHTITEM4FROMDATE: From Date/Time");
        f.setSize(20);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("ACTSEPLIGHTITEM4TODATE: To Date/Time");
        f.setSize(20);      
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_NOTE");
        f.setDbName("NOTE");
        f.setLabel("ACTSEPLIGHTITEM4NOTE: Note");
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
        setLabel("ACTSEPLIGHTITEM6JOBID: Job ID");

        itemblk5.addField("STD_JOB_ID").
        setSize(15).
        setLabel("ACTSEPLIGHTITEM6STDJOBID: Standard Job ID").
        setLOV("SeparateStandardJobLov.page", 600, 445).
        setUpperCase().     
        setQueryable();     

        itemblk5.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("ACTSEPLIGHTITEM6STDJOBCONTRACT: Site").
        setUpperCase();

        itemblk5.addField("STD_JOB_REVISION").
        setSize(10).
        setLabel("ACTSEPLIGHTITEM6STDJOBREVISION: Revision").
        setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).          
        setUpperCase().     
        setQueryable();

        itemblk5.addField("DESCRIPTION").
        setSize(35).
        setLabel("ACTSEPLIGHTITEM6DESCRIPTION: Description").
        setUpperCase();

        itemblk5.addField("ITEM5_QTY", "Number").
        setLabel("ACTSEPLIGHTITEM6QTY: Quantity").
        setDbName("QTY");

        itemblk5.addField("STD_JOB_STATUS").
        setLabel("ACTSEPLIGHTITEM6STDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");

        itemblk5.addField("ITEM5_COMPANY").
        setSize(20).
        setHidden().
        setDbName("COMPANY").
        setUpperCase();

        itemblk5.addField("ITEM5_SIGN_ID").
        setSize(35).
        setDbName("SIGNATURE").
        setLabel("ACTSEPLIGHTITEM6SIGNID: Executed By").
        setQueryable().
        setUpperCase();

        itemblk5.addField("EMPLOYEE_ID").
        setSize(15).
        setLabel("ACTSEPLIGHTITEM6EMPLOYEEID: Employee ID").
        setReadOnly().
        setUpperCase();    

        itemblk5.addField("ITEM5_DATE_FROM", "Datetime").
        setSize(20).
        setDbName("DATE_FROM").
        setLabel("ACTSEPLIGHTITEM6DATEFROM: Date From");

        itemblk5.addField("ITEM5_DATE_TO", "Datetime").
        setSize(20).
        setDbName("DATE_TO").
        setLabel("ACTSEPLIGHTITEM6DATETO: Date To");

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
        itemtbl5.setTitle(mgr.translate("ACTSEPLIGHTITEM6WOJOBS: Jobs"));
        itemtbl5.setWrap();

        itembar5 = mgr.newASPCommandBar(itemblk5);
        itembar5.enableCommand(itembar5.FIND);
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);        
    }  

    // 031216  ARWILK  Begin  (Replace blocks with tabs)
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
    // 031216  ARWILK  End  (Replace blocks with tabs)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateCrafts");
        headbar.removeCustomCommand("activatePalnning");    
        headbar.removeCustomCommand("activateMaterials");
        headbar.removeCustomCommand("activateToolsAndFacilities");
        headbar.removeCustomCommand("activateJobs");
        // 031216  ARWILK  End  (Replace blocks with tabs)

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

        fldTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTWORKLEADERFLD: Work+Leader");     
        fldTitleSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSIGNFLD: Signature");     
        fldTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTPARTNOFLD: Part+No");     
        fldTitleItem1CatalogNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTITEM1CATNOFLD: Sales+Part+Number"); 

        lovTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTWORKLEADERLOV: List+of+Work+Leader");     
        lovTitleSign = mgr.translate("PCMWACTIVESEPARATE2LIGHTSIGNLOV: List+of+Signature");     
        lovTitlePartNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTPARTNOLOV: List+of+Part+No");     
        lovTitleItem1CatalogNo = mgr.translate("PCMWACTIVESEPARATE2LIGHTITEM1CATNOLOV: List+of+Sales+Part+Number");               

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {
            String connType = headset.getValue("CONNECTION_TYPE");

            trans.clear();

            cmd = trans.addCustomFunction("CONNTYPE","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
            cmd.addParameter("CONNECTION_TYPE",connType);

            trans = mgr.perform(trans);

            String connTypeDB = trans.getValue("CONNTYPE/DATA/CONNECTION_TYPE_DB");

            if ("VIM".equals(connTypeDB))
            {
                mgr.getASPField("MCH_CODE_CONTRACT").setHidden();  
            }
        }
        //Bug 43848, start
        if (headset.countRows() == 0)
        {
            headbar.disableCommand(headbar.FORWARD);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.BACK);
            headbar.removeCustomCommand("prepareWork");
            headbar.removeCustomCommand("observed");
            headbar.removeCustomCommand("underPrep");
            headbar.removeCustomCommand("prepared");
            headbar.removeCustomCommand("released");
            headbar.removeCustomCommand("started");
            headbar.removeCustomCommand("workDone");
            headbar.removeCustomCommand("reported");
            headbar.removeCustomCommand("finished");
            headbar.removeCustomCommand("cancelled");
        }
        //Bug 43848, end

        if ((itemlay5.isVisible()) && (itemset5.countRows() > 0))        {
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
        return "PCMWACTIVESEPARATE2LIGHTTITLE: Work Order Information";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATE2LIGHTTITLE: Work Order Information";
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
                appendToHTML(itemlay3.show());

                if (itemlay3.isSingleLayout() && (itemset3.countRows() > 0))
                    appendToHTML(itemlay.show());
            }
            else if (tabs.getActiveTab() == 4)
                appendToHTML(itemlay4.show());
            else if (tabs.getActiveTab() == 5)
                appendToHTML(itemlay5.show());
        }
        // 031216  ARWILK  End  (Replace blocks with tabs)
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id68773
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITUNISSUE: All required material has not been issued"));
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITUNISSUE: All required material has not been issued"));
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2LIGHTITREPAIR: This object is in repair shop. Do you still want to continue?"));
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
        appendDirtyJavaScript("}\n");*/

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
