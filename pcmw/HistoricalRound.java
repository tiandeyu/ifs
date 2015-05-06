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
*  File        : HistoricalRound.java 
*  Modified    :
*  ARWILK  010221  Created.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*                  Removed unnecessary methods and modified methods okFindITEM..()
*  BUNILK  020909  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  ORDER and DOCMAN modules inside preDefine method.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021210  Merged with 2002-3 SP3
*  CHCRLK  030430  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  INROLK  030624  Added RMB Returns.
*  CHAMLK  030826  Added Ownership, Owner and Owner Name to Materials Tab.
*  CHAMLK  030827  Added Ownership, Owner and Owner Name to Posting Tab.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031216  Edge Developments - (Replaced blocks with tabs, Removed clone and doReset Methods)
*  VAGULK  040114  Made field order according to the order in the Centura application(Web Alignment).
*  ARWILK  040128  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  ARWILK  040430  Added new field CBPROJCONNECTED.
*  SAPRLK  040608  Added key PM_REVISION.
*  ARWILK  040617  Added a new job tab. Added job_id to materials tab.(Spec - AMEC109A)  
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ThWilk  040728  Added the field ACTIVITY_SEQ as a hidden field. (IID AMUT219)
*  ThWilk  040728  Added Project Information Block.(IID AMUT219).
*  ARWILK  040820  Disable New,Remove,Duplicate,Edit operations for Jobs tab. Also modified okFindITEM9 method.
*  SHAFLK  040726  Bug 43249, Modified method preDefine().
*  NIJALK  040902  Merged bug 43249.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  SHAFLK  040916  Bug 46621, Modified method preDefine().
*  Chanlk  041105  Merged Bug 46621.	
*  NIJALK  041105  Added Std Job Status to Jobs Tab.
*  NAMELK  041110  Duplicated Translation Tags Corrected.
*  NIJALK  041112  Modified preDefine().
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  050217  Removed RMB "Returns".
*  ERALLK  051222  Bug 55194, Added new field 'Inspection Note' to the General group.
*  NIJALK  051230  Merged bug 55194.
*  THWILK  060307  Fixed Call136568,Modified predefine().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  DIAMLK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060905  Merged with the Bug Id 58216
*  AMNILK  061110  MEPR604: Original Source info in voucher rows.Modified adjust().
*  AMDILK  070306  Call Id 141409: Insert a confirmation to view the reopened WO. 
*                  Modofied methods printContents(), preDefine(), reOpen()
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  AMNILK  070716  Eliminated XSS Secutiry vulnerability. 
*  SHAFLK  071112  Bug 69193 Changed Number format of cost amount field.
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  SHAFLK  091204  Bug 87554, Modified okFind().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class HistoricalRound extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.HistoricalRound");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

    private ASPBlock condblk;
    private ASPRowSet condset;

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

    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

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
    private ASPCommand cmd;
    private String url;
    private String fldTitleTestPointId;
    private String fldTitleEmpNo;
    private String fldTitleSignature;
    private String fldTitleCatalogNo;
    private String fldTitleAuthSig;
    private String lovTitleTestPointId;
    private String lovTitleEmpNo;
    private String lovTitleSignature;
    private String lovTitleCatalogNo;
    private String lovTitleAuthSig;
    private boolean isSecurityChecked;
    private ASPBuffer actionsBuffer;
    // 040128  ARWILK  Begin  (Remove uneccessary global variables)
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    // 040128  ARWILK  End  (Remove uneccessary global variables)

    //===============================================================
    // Construction 
    //===============================================================
    public HistoricalRound(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
        actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");

        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
            okFind();

        checkObjAvailable();
        adjust();
        adjustActions();

        ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
        ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031216  ARWILK  End  (Replace blocks with tabs)
    }

//-----------------------------------------------------------------------------
//---------------------------  CMDBAR FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addEmptyQuery(condblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        trans.clear();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        mgr.querySubmit(trans, headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
            headset.clear();
        }
        else if (headset.countRows() == 1)
        {
            headset.first();  
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4(); 
            okFindITEM6(); 

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        mgr.createSearchURL(headblk);
    }

    public void okFindReopen()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addEmptyQuery(condblk);
        q.includeMeta("ALL");

        mgr.submit(trans);
        trans.clear();

        q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
            headset.clear();
        }

        if (headlay.isSingleLayout() && headset.countSelectedRows()>0)
            headset.setFilterOn();

        headblk.generateAssignments();
    }

    public void okFindReopenOvw()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
            headset.clear();
        }
        else
            headblk.generateAssignments();
    }

//-----------------------------------------------------------------------------
//--------------------------  SEARCH-IT FUNCTIONS  ----------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (headset.countRows() != 0)
        {

            trans.clear();

            headrowno = headset.getCurrentRowNo();
            ASPQuery q = trans.addQuery(itemblk0);
            q.addWhereCondition("WO_NO = ?");
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
            q.includeMeta("ALL");

            mgr.submit(trans);

            if (mgr.commandBarActivated())
            {
                if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset0.clear();
                }
            }

            headset.goTo(headrowno);
        }
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (headset.countRows() != 0)
        {
            trans.clear();
            headrowno = headset.getCurrentRowNo();

            ASPQuery q = trans.addQuery(itemblk1);
            q.addWhereCondition("WO_NO = ?");   
            q.addWhereCondition("WORK_ORDER_COST_TYPE = ?");
            q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addParameter("S_COST_TYPE_0",condset.getRow().getValue("S_COST_TYPE_0"));
            q.addParameter("S_ACCOUNT_TYPE_0",condset.getRow().getValue("S_ACCOUNT_TYPE_0"));
	    q.includeMeta("ALL");

            mgr.submit(trans);  

            if (mgr.commandBarActivated())
            {
                if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset1.clear();
                }
            }

            headset.goTo(headrowno);
        }
    }

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (headset.countRows() != 0)
        {
            trans.clear();
            headrowno = headset.getCurrentRowNo();

            ASPQuery q = trans.addQuery(itemblk2);
            q.addWhereCondition("WO_NO = ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans); 

            if (mgr.commandBarActivated())
            {
                if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset2.clear();
                }
            }

            headset.goTo(headrowno);        
        }

    }

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        int headsetRowNo;
        int item2rowno;

        if (itemset2.countRows() != 0)
        {
            trans.clear();
            headsetRowNo = headset.getCurrentRowNo();
            item2rowno = itemset2.getCurrentRowNo();

            ASPQuery q = trans.addQuery(itemblk);
            q.addWhereCondition("WO_NO = ?");
            q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
            q.addParameter("ITEM2_WO_NO",itemset2.getRow().getFieldValue("ITEM2_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans); 

            if (mgr.commandBarActivated())
            {
                if (itemset.countRows() == 0 && "ITEM5.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset.clear();
                }
            }

            headset.goTo(headsetRowNo);
            itemset2.goTo(item2rowno);

        }
    }

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (headset.countRows() != 0)
        {
            trans.clear();
            headrowno = headset.getCurrentRowNo();

            ASPQuery q = trans.addQuery(itemblk3);
            q.addWhereCondition("WO_NO = ?");   
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.includeMeta("ALL");

            mgr.submit(trans);  

            if (mgr.commandBarActivated())
            {
                if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset3.clear();
                }
            }

            headset.goTo(headrowno);
        }
    }     

    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (headset.countRows() != 0)
        {
            trans.clear();
            headrowno = headset.getCurrentRowNo();

            ASPQuery q = trans.addQuery(itemblk4);
            q.addWhereCondition("WO_NO = ?");   
            q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addParameter("S_ACCOUNT_TYPE_0",condset.getRow().getValue("S_ACCOUNT_TYPE_0"));
            q.includeMeta("ALL");
            

            mgr.submit(trans);

            if (mgr.commandBarActivated())
            {
                if (itemset4.countRows() == 0 && "ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDNODATA: No data found."));
                    itemset4.clear();
                }
            }

            headset.goTo(headrowno);
        }
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno;
        trans.clear();

        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");   
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(headrowno);
    }

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        int headrowno;
        trans.clear();

        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");   
        q.addWhereCondition("WORK_ORDER_COST_TYPE = ?");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("S_COST_TYPE_0",condset.getRow().getValue("S_COST_TYPE_0"));
        q.addParameter("S_ACCOUNT_TYPE_0",condset.getRow().getValue("S_ACCOUNT_TYPE_0"));
        mgr.submit(trans);

        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
        headset.goTo(headrowno);
    }


    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);

        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
    }   

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("ITEM2_WO_NO",itemset2.getRow().getFieldValue("ITEM2_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset2.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        mgr.submit(trans);

        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
    }

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        int headrowno;
        trans.clear();

        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");   
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);

        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));   
        itemset3.clear();
        headset.goTo(headrowno);
    }

    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        int headrowno;
        trans.clear();

        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk4);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");   
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("S_ACCOUNT_TYPE_0",condset.getRow().getValue("S_ACCOUNT_TYPE_0"));
	    mgr.submit(trans);

        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));   
        itemset4.clear();
        headset.goTo(headrowno);
    }

    public void okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk6);

        headset.goTo(headrowno);
    }

    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk6);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
	mgr.submit(trans);

        headset.goTo(headrowno);

        itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
        itemset6.clear();
    }

    //-----------------------------------------------------------------------------
    //------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
    //-----------------------------------------------------------------------------

    public void requisitions()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        // 040128  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderRequisHeaderRMB.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "requisitions";      
        // 040128  ARWILK  End  (Remove uneccessary global variables)   
    }

    public void reOpen()
    {
        ASPManager mgr = getASPManager();

        // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
        int count = 0;

        String current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", current_url);

        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
        }

        for (int i = 0; i < count; i++)
        {
            cmd = trans.addCustomCommand("REOPEN_WO_" + i, "Historical_Work_Order_API.Recreate_Work_Order");
            cmd.addParameter("WO_NO", headset.getValue("WO_NO"));

            if (headlay.isMultirowLayout())
                headset.next();
        }

        if ("TRUE".equals(mgr.readValue("VIEWWORKORDER"))) {
	    urlString = createTransferUrl("ActiveRound.page", headset.getSelectedRows("WO_NO"));
	    newWinHandle = "ActiveRound";
	    bOpenNewWindow = true;
	}

        trans = mgr.perform(trans);

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        /*if (count == 1)
            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDREOPMSGONE: Work Order has been reopened."));
        else
            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDREOPMSGMORE: Work Orders were successfully reopened."));
	*/

        // 040128  ARWILK  End  (Enable Multirow RMB actions)

        if (headlay.isMultirowLayout())
            okFindReopenOvw();
        else
            okFindReopen(); 
    }

    public void reOpenSubmit()
    {
        int n;
        ASPManager mgr = getASPManager();

        n = headset.countSelectedRows();
        headset.first();

        trans.clear();

        if (n == 0)
        {
            for (int count=1; count<=n; ++count)
            {
                ASPCommand cmd = trans.addCustomCommand("RECREATE"+count, "Historical_Work_Order_API.Recreate_Work_Order");
                cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

                headset.next();
            } 

            trans = mgr.perform(trans);
            trans.clear();

            mgr.showAlert(mgr.translate("PCMWHISTORICALROUNDWORE: Work Order(s) have been reopened."));
            headset.setFilterOff();
        }

    }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR3 CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void viewPermit()
    {
        ASPManager mgr = getASPManager();

        // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
        if (itemlay3.isMultirowLayout())
            itemset3.store();
        else
        {
            itemset3.unselectRows();
            itemset3.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("Permit.page", itemset3.getSelectedRows("PERMIT_SEQ"));
        newWinHandle = "viewPermit"; 
        // 040128  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        // ------------------------------------------------------------------------------------------------
        // ------------------------------------    CONDBLK   ----------------------------------------------
        // ------------------------------------------------------------------------------------------------

        condblk = mgr.newASPBlock("COND");

        f = condblk.addField("S_COST_TYPE_0");
        f.setFunction("WORK_ORDER_COST_TYPE_API.Get_Client_Value(0)");
        f.setHidden();

        f = condblk.addField("S_ACCOUNT_TYPE_0");
        f.setFunction("WORK_ORDER_ACCOUNT_TYPE_API.Get_Client_Value(0)");
        f.setHidden();

        condblk.setView("Dual");
        condset = condblk.getASPRowSet();

        // ------------------------------------------------------------------------------------------------
        // ------------------------------------     HEADBLK      ------------------------------------------
        // ------------------------------------------------------------------------------------------------

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("ROUNDDEF_ID");
        f.setSize(15);
        f.setDynamicLOV("PM_ROUND_DEFINITION",600,450);
        f.setLabel("PCMWHISTORICALROUNDROUTEID: Route ID");
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();

        f = headblk.addField("CONTRACT");
        f.setSize(6);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDCONTRACT: Site");
        f.setReadOnly();
        f.setMaxLength(5);
        f.setHilite();

        f = headblk.addField("HEAD_COMPANY");
        f.setSize(6);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDCOMPANY: Company");
        f.setUpperCase();
        f.setReadOnly();
        f.setDbName("COMPANY");

        f = headblk.addField("ROUNDDEFDESCRIPTION");
        f.setSize(27);
        f.setLabel("PCMWHISTORICALROUNDRNDDEFDESC: Route Description");
        f.setFunction("Pm_Round_Definition_API.Get_Description(:ROUNDDEF_ID)");
        mgr.getASPField("ROUNDDEF_ID").setValidation("ROUNDDEFDESCRIPTION");
        f.setReadOnly();
        f.setMaxLength(60);
        f.setHilite();
        f.setDefaultNotVisible();

        ///////////////SET HIDDEN IN EDIT MODE///////////
        f = headblk.addField("SROUNDDEFDESCR");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDSROUNDDEFDESCR: Route Description");
        f.setFunction("Pm_Round_Definition_API.Get_Description(:ROUNDDEF_ID)");
        mgr.getASPField("ROUNDDEF_ID").setValidation("SROUNDDEFDESCR");
        f.setReadOnly();  
        f.setMaxLength(60);
        f.setHilite();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(10);
        f.setLabel("PCMWHISTORICALROUNDWONO: WO No");
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();

        //////////SET HIDDEN IN OVERVIEW MODE///////////
        f = headblk.addField("ORG_CODE");
        f.setSize(20);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setLabel("PCMWHISTORICALROUNDORGCODE: Maintenance Organization");
        f.setReadOnly();
        f.setMaxLength(8);
        f.setMandatory();

        f = headblk.addField("ROLE_CODE");
        f.setSize(10);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,450);
        f.setLabel("PCMWHISTORICALROUNDROLECODE: Craft");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();   

        f = headblk.addField("REAL_S_DATE","Datetime");
        f.setSize(20);
        f.setLabel("PCMWHISTORICALROUNDRSDATE: Actual Start");
        f.setReadOnly();
        f.setDefaultNotVisible();   

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setSize(20);
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDPFDATE: Planned Completion");
        f.setReadOnly();
        f.setDefaultNotVisible();

        /////////SETHIDDEN IN EDIT MODE////////////

        f = headblk.addField("REAL_F_DATE","Datetime");
        f.setSize(20);
        f.setLabel("PCMWHISTORICALROUNDRFDATE: Actual Completion");
        f.setReadOnly();

        f = headblk.addField("PLAN_HRS","Number");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDPLANHRS: Planned Hours");
        f.setReadOnly();
        f.setDefaultNotVisible();   

        /////////SETHIDDEN IN EDIT MODE//////////
        f = headblk.addField("REPORTED_BY");
        f.setSize(20);
        f.setDynamicLOV("EMPLOYEE_LOV","HEAD_COMPANY",600,450);
        f.setLabel("PCMWHISTORICALROUNDREPORTEDBY: Reported By");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();   

        f = headblk.addField("REPORTED_BY_ID");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDREPORTEDBYID: Reported By");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(11);

        f = headblk.addField("REPORT_IN_BY");
        f.setSize(15);
        f.setDynamicLOV("EMPLOYEE_LOV","HEAD_COMPANY",600,450);
        f.setLabel("PCMWHISTORICALROUNDREPINBY: Reported In By");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();   

        f = headblk.addField("REPORT_IN_BY_ID");
        f.setSize(20);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDREPINBYID: Reported In By");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(11);
        f.setDefaultNotVisible();   

        headblk.addField("REPORTEDINBYNAME").
        setSize(20).
        setLabel("PCMWHISTORICALROUNDREPINBYNAME: Name").
        setFunction("PERSON_INFO_API.Get_Name(:REPORT_IN_BY)").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        f = headblk.addField("CALL_CODE");
        f.setSize(10);
        f.setDynamicLOV("MAINTENANCE_EVENT",600,450);
        f.setLabel("PCMWHISTORICALROUNDCALLCODE: Event");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();   

        f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setSize(20);
        f.setLabel("PCMWHISTORICALROUNDPSDATE: Planned Start");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("VENDOR_NO");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDVENNO: Contractor");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(10);
        f.setDynamicLOV("ORDER_COORDINATOR",600,450);
        f.setLabel("PCMWHISTORICALROUNDAUTHCODE: Coordinator");
        //Bug 84436, Start
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
        else
            f.setFunction("''");
         //Bug 84436, End  
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("SVENDORNAME");
        f.setSize(20);
        f.setLabel("PCMWHISTORICALROUNDSVENDORNAME: Contractor Name");
        f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
        f.setReadOnly();
        mgr.getASPField("VENDOR_NO").setValidation("SVENDORNAME");
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(12);
        f.setDynamicLOV("CUSTOMER_INFO",600,450);
        f.setLabel("PCMWHISTORICALROUNDCUSTOMERNO: Customer No.");
        //f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMERNAME");
        f.setSize(18);
        f.setLabel("PCMWHISTORICALROUNDCUSTNAME: Customer Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("CONTACT");
        f.setSize(8);
        f.setLabel("PCMWHISTORICALROUNDCONTACT: Contact");
        f.setReadOnly();
        f.setMaxLength(30);
        f.setDefaultNotVisible();

        f = headblk.addField("NOTE");
        f.setSize(8);
        f.setLabel("PCMWHISTORICALROUNDNOTE: Inspection Note");
        f.setReadOnly();
        f.setMaxLength(30);
        f.setDefaultNotVisible();

        f = headblk.addField("REFERENCE_NO");
        f.setSize(14);
        f.setLabel("PCMWHISTORICALROUNDREFNO: Reference No");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = headblk.addField("PHONE_NO");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDPHONE: Phone No");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("FIXED_PRICE");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDFIXEDPRICE: Fixed Price");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS1");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS1: Address1");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS2");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS2: Address2");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS3");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS3: Address3");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS4");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS4: Address4");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS5");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS5: Address5");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS6");
        f.setSize(35);
        f.setLabel("PCMWHISTORICALROUNDADDRESS6: Address6");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("CONTRACT_ID");        
        f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");           
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setMaxLength(15);
        f.setLabel("PCMWHISTORICALROUNDCONTRACTID: Contract ID");
        f.setSize(15);

        f = headblk.addField("CONTRACT_NAME");                     
        f.setDefaultNotVisible();
         //Bug 84436, Start
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
         else
            f.setFunction("''");
         //Bug 84436, End  
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDCONTRACTNAME: Contract Name");
        f.setSize(15);

        f = headblk.addField("LINE_NO","Number");
        f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDLINENO: Line No");
        f.setSize(10); 

        f = headblk.addField("LINE_DESC");                     
        f.setDefaultNotVisible();
        //Bug 84436, Start
        if(mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        //Bug 84436, End
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDLINEDESC: Description");
        f.setSize(15);

         f = headblk.addField("CONTRACT_TYPE");                     
        f.setDefaultNotVisible();
        //Bug 84436, Start
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
        else
            f.setFunction("''");
        //Bug 84436, End 
         f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDCONTRACTTYPE: Contract Type");
        f.setSize(15);

        f = headblk.addField("INVOICE_TYPE");                     
        f.setDefaultNotVisible();
        //Bug 84436, Start
        if(mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
         else
            f.setFunction("''");
        //Bug 84436, End
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDINVTYPE: Invoice Type");
        f.setSize(15);

        f = headblk.addField("ADDRESS_ID");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDADDID: Address Id");
        f.setUpperCase();

        f = headblk.addField("LU_NAME");
        f.setHidden();
        f.setFunction("'HistoricalRound'");

        f = headblk.addField("KEY_REF");
        f.setHidden();
        f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");

        f = headblk.addField("DOCUMENT");
        if (mgr.isModuleInstalled("DOCMAN"))
            f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('HistoricalRound',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
        else
            f.setFunction("''");
        f.setUpperCase();
        f.setLabel("PCMWHISTORICALROUNDDOCUMENT: Has Documents");
        f.setReadOnly();
        f.setCheckBox("FALSE,TRUE");
        f.setSize(18);  
        f.setDefaultNotVisible();

        f = headblk.addField("CHECKWORDER");
        f.setHidden();
        // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
        f.setFunction("Historical_Work_Order_API.Check_Work_Order(:WO_NO)");
        // 040128  ARWILK  End  (Enable Multirow RMB actions)

        f = headblk.addField("CBPROJCONNECTED");
        f.setFunction("Historical_Round_API.Is_Project_Connected(:WO_NO)");
        f.setLabel("PCMWHISTROUNDPROJCONN: Project Connected");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setSize(5);

        if (mgr.isModuleInstalled("PROJ"))
        {

            f = headblk.addField("PROGRAM_ID");
            f.setLabel("PCMWHISTORICALROUNDPROGRAMID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Program_Id(:HPROJECT_NO)");
            f.setSize(20);

            f = headblk.addField("PROGRAMDESC");
            f.setLabel("PCMWHISTORICALROUNDPROGRAMDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:HPROJECT_NO),PROJECT_API.Get_Program_Id(:HPROJECT_NO))");
            f.setSize(30);

            f = headblk.addField("HPROJECT_NO");
            f.setSize(12);
            f.setReadOnly();
            f.setDbName("PROJECT_NO");
            f.setDefaultNotVisible();
            f.setLabel("PCMWHISTORICALROUNDPROJECTNO: Project No");
            f.setMaxLength(10);

            f = headblk.addField("PROJECTDESC");
            f.setLabel("PCMWHISTORICALROUNDPROJECTDESC: Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
            f.setSize(30);

            f = headblk.addField("SUBPROJECT_ID");
            f.setLabel("PCMWHISTORICALROUNDSUBPROJECTID: Sub Project ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("SUBPROJECTDESC");
            f.setLabel("PCMWHISTORICALROUNDSUBPROJECTDESC: Sub Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_ID");
            f.setLabel("PCMWHISTORICALROUNDACTIVITYID: Activity ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("ACTIVITYDESC");
            f.setLabel("PCMWHISTORICALROUNDACTIVITYDESC: Activity Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWHISTORICALROUNDACTIVITYSEQ: Activity Sequence");
            f.setSize(12);
            f.setDefaultNotVisible();
            f.setReadOnly();
        }

        headblk.setView("HISTORICAL_ROUND");
        headblk.defineCommand("HISTORICAL_ROUND_API", "");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWHISTORICALROUNDHD: Historical Route Work Order"));
        headtbl.setWrap();
        headtbl.enableRowSelect();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.addCustomCommand("reOpen", mgr.translate("PCMWHISTORICALROUNDREOPEN: Reopen"));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("requisitions",mgr.translate("PCMWHISTORICALROUNDREQUISITIONS: Requisitions..."));

        headbar.addCustomCommand("activateGeneral", "");
        headbar.addCustomCommand("activateTimeReport", "");
        headbar.addCustomCommand("activateMaterial", "");
        headbar.addCustomCommand("activatePermits", "");
        headbar.addCustomCommand("activatePostings", "");
        headbar.addCustomCommand("activateJobs", "");

        headbar.defineCommand("reOpen", "reOpen" , "viewWorkOrder");

        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("requisitions");

        headbar.disableCommandGroup(headbar.CMD_GROUP_EDIT);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.defineGroup("","WO_NO,ROUNDDEF_ID,ROUNDDEFDESCRIPTION,CONTRACT,ORG_CODE,ROLE_CODE",false,true); 
        headlay.defineGroup(mgr.translate("PCMWHISTORICALROUNDGRPLABEL1: General"),"PLAN_S_DATE,PLAN_F_DATE,CALL_CODE,REAL_S_DATE,PLAN_HRS,VENDOR_NO,REAL_F_DATE,REPORTED_BY,REPORT_IN_BY,REPORTEDINBYNAME,SVENDORNAME,CUSTOMER_NO,CUSTOMERNAME,CONTACT,REFERENCE_NO,PHONE_NO,FIXED_PRICE,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,DOCUMENT,CBPROJCONNECTED,NOTE",true,true);
        headlay.defineGroup(mgr.translate("PCMWHISTORICALROUNDADDRESSES: Address"),"ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6",true,true);
        if (mgr.isModuleInstalled("PROJ"))
            headlay.defineGroup(mgr.translate("PCMWHISTORICALROUNDGRPLABEL: Project Information"),"PROGRAM_ID,PROGRAMDESC,HPROJECT_NO,PROJECTDESC,SUBPROJECT_ID,SUBPROJECTDESC,ACTIVITY_ID,ACTIVITYDESC,ACTIVITY_SEQ",true,false);

	headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDGENERALTAB: General"), "javascript:commandSet('HEAD.activateGeneral','')");
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDTIMEREPORTTAB: Time Report"), "javascript:commandSet('HEAD.activateTimeReport','')");
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDMATERIALSTAB: Materials"), "javascript:commandSet('HEAD.activateMaterial','')");
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDPERMITSTAB: Permits"), "javascript:commandSet('HEAD.activatePermits','')");
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDPOSTINGSTAB: Postings"), "javascript:commandSet('HEAD.activatePostings','')");
        tabs.addTab(mgr.translate("PCMWHISTORICALROUNDJOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");

        // ------------------------------------------------------------------------------------------------
        // ------------------------------------     General Tab  ------------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("PM_ORDER_NO","Number");
        f.setSize(9);
        f.setLabel("PCMWHISTORICALROUNDPMORDNO: Order");
        f.setReadOnly();

        f = itemblk0.addField("ROUND_REPORT_IN_STATUS");
        f.setSize(30);
        f.setLabel("PCMWHISTORICALROUNDREPSTATUS: Route Report In Status");
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk0.addField("MCH_CODE");
        f.setSize(16);
        f.setLabel("PCMWHISTORICALROUNDMCHCODE: Object ID");
        f.setReadOnly();
        f.setMaxLength(100);

        f = itemblk0.addField("OBJECTDESCRIPTION");
        f.setSize(26);
        f.setLabel("PCMWHISTORICALROUNDOBJDESC: Object Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:ITEM0_CONTRACT,:MCH_CODE)");
        mgr.getASPField("MCH_CODE").setValidation("OBJECTDESCRIPTION");
        f.setReadOnly();
        f.setMaxLength(45);

        f= itemblk0.addField("INSPECTION_NOTE");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDINSPECTIONNOTE: Inspection Notes");
        f.setMaxLength(45);

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE",600,450);
        f.setLabel("PCMWHISTORICALROUNDIT0CONT: Site");
        f.setDbName("CONTRACT");
        f.setReadOnly();
        f.setMaxLength(5);

        f = itemblk0.addField("TEST_POINT_ID");
        f.setSize(13);
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT",600,450); 
        f.setLabel("PCMWHISTORICALROUNDTESTPNTID: Testpoint");
        f.setReadOnly();
        f.setMaxLength(6);

        f = itemblk0.addField("MSEQOBJECTTESTPOINTDESCRIPTIO");
        f.setSize(26);
        f.setLabel("PCMWHISTORICALROUNDTPDESC: Testpoint Description");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        mgr.getASPField("TEST_POINT_ID").setValidation("MSEQOBJECTTESTPOINTDESCRIPTIO");
        f.setReadOnly();
        f.setMaxLength(60);
        f.setDefaultNotVisible();

        f = itemblk0.addField("MSEQOBJECTTESTPOINTLOCATION");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDTPLOC: Location");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        mgr.getASPField("TEST_POINT_ID").setValidation("MSEQOBJECTTESTPOINTLOCATION");
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk0.addField("ACTION_CODE_ID");
        f.setSize(13);
        f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
        f.setLabel("PCMWHISTORICALROUNDACID: Action");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setMandatory();

        f = itemblk0.addField("ACTCODEIDDESCRIPTION");
        f.setSize(26);
        f.setLabel("PCMWHISTORICALROUNDACTDESC: Action Description");
        f.setFunction("MAINTENANCE_ACTION_API.Get_Description(:ACTION_CODE_ID)");
        mgr.getASPField("ACTION_CODE_ID").setValidation("ACTCODEIDDESCRIPTION");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f= itemblk0.addField("PLAN_MEN","Number");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDPLANMEN: Planned Men");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_PLAN_HRS","Number");
        f.setSize(18);
        f.setLabel("PCMWHISTORICALROUNDIT0PLANHRS: Planned Hours");
        f.setDbName("PLAN_HRS");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_WO_NO","Number","#");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDIT0WONO: WO No");
        f.setDbName("WO_NO");
        f.setReadOnly();

        f = itemblk0.addField("PM_NO","Number");
        f.setSize(11);
        f.setLOV("PmActionLov1.page",600,450);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDPMNO: PM No");
        f.setReadOnly();  

        f = itemblk0.addField("PM_REVISION");
        f.setSize(11);
        f.setDynamicLOV("PM_ACTION","PM_NO",600,450);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDPMREV: PM Revision");
        f.setReadOnly();

        itemblk0.setView("HISTORICAL_ROUND_ACTION");
        itemblk0.defineCommand("HISTORICAL_ROUND_ACTION_API", "");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setWrap();
        itembar0 = mgr.newASPCommandBar(itemblk0);

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setSimple("OBJECTDESCRIPTION");
        itemlay0.setSimple("MSEQOBJECTTESTPOINTDESCRIPTIO");
        itemlay0.setSimple("ACTCODEIDDESCRIPTION");

        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");

        itembar0.removeCommandGroup(itembar0.CMD_GROUP_CUSTOM);
        itembar0.disableCommandGroup(itembar0.CMD_GROUP_EDIT);

        // ------------------------------------------------------------------------------------------------
        // ----------------------------------     Time Report Tab  ----------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("CRE_DATE","Date");
        f.setSize(13);
        f.setLabel("PCMWHISTORICALROUNDCREDATE: Creation Date");
        f.setReadOnly();

        f = itemblk1.addField("EMP_NO");
        f.setSize(14);
        f.setLabel("PCMWHISTORICALROUNDEMPNO: Employee ID");
        f.setDynamicLOV("EMPLOYEE_NO",600,450);
        f.setReadOnly();
        f.setMaxLength(11);

        f = itemblk1.addField("SIGNATURE");
        f.setSize(25);
        f.setLabel("PCMWHISTORICALROUNDSIGN: Signature");
        f.setFunction("substr(COMPANY_EMP_API.Get_Person_ID(:COMPANY,:EMP_NO),1,20)");
        f.setDynamicLOV("EMPLOYEE_LOV",600,450);
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk1.addField("NAME");
        f.setSize(50);
        f.setLabel("PCMWHISTORICALROUNDNAME: Name");
        f.setFunction("PERSON_INFO_API.Get_Name(substr(COMPANY_EMP_API.Get_Person_ID(:COMPANY,:EMP_NO),1,20))");
        f.setReadOnly();
        f.setMaxLength(100);

        f = itemblk1.addField("ITEM1_SITE");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWHISTORICALROUNDIT1SITE: Site");
        f.setDbName("CONTRACT");
        f.setReadOnly();
        f.setMaxLength(5);

        f = itemblk1.addField("ORGN_CODE");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDORGNCODE: Maintenance Organization");
        f.setDbName("ORG_CODE");
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM1_SITE CONTRACT", 600,450);
        f.setReadOnly();
        f.setMaxLength(8);

        f = itemblk1.addField("ROL_CODE");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDROLCODE: Craft");
        f.setDbName("ROLE_CODE");
        f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM1_SITE CONTRACT", 600,450);
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk1.addField("QTY", "Number");
        f.setSize(10);
        f.setLabel("PCMWHISTORICALROUNDHOURS: Hours");
        f.setReadOnly();

        f = itemblk1.addField("AMOUNT", "Money","#.##");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDAMOUNT: Amount");
        f.setReadOnly();

        f = itemblk1.addField("CMNT");
        f.setSize(45);
        f.setLabel("PCMWHISTORICALROUNDCMNT: Comment");
        f.setReadOnly();
        f.setMaxLength(80);

        f = itemblk1.addField("ITEM1_WO_NO","Number","#");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDIT1WONO: WO No");
        f.setDbName("WO_NO");
        f.setReadOnly();

        f = itemblk1.addField("COMPANY");
        f.setHidden();
        f.setMandatory();

        f = itemblk1.addField("ITEM1_ROW_NO","Number");
        f.setMandatory();
        f.setHidden();
        f.setDbName("ROW_NO");

        f = itemblk1.addField("ITEM1_WORK_ORDER_COST_TYPE");
        f.setHidden();
        f.setDbName("WORK_ORDER_COST_TYPE");

        f = itemblk1.addField("ITEM1_WORK_ORDER_ACCOUNT_TYPE");
        f.setHidden();
        f.setDbName("WORK_ORDER_ACCOUNT_TYPE");

        itemblk1.setView("WORK_ORDER_CODING");
        itemblk1.defineCommand("WORK_ORDER_CODING_API", "");
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();  

        itembar1 = mgr.newASPCommandBar(itemblk1);

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);  

        itembar1.enableCommand(itembar1.FIND);
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();

        itembar1.removeCommandGroup(itembar1.CMD_GROUP_CUSTOM);
        itembar1.disableCommandGroup(itembar1.CMD_GROUP_EDIT);

        // ------------------------------------------------------------------------------------------------
        // ----------------------------------     Materials Tab  ------------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk2.addField("OBJSTATE");
        f.setHidden();

        f = itemblk2.addField("OBJEVENTS");
        f.setHidden();

        f = itemblk2.addField("MAINT_MATERIAL_ORDER_NO");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk2.addField("ITEM2_WO_NO");
        f.setSize(11);
        f.setMaxLength(8);
        f.setReadOnly();
        f.setDbName("WO_NO");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWHISTORICALROUNDWO_NO: Workorder No");

        f = itemblk2.addField("MCHCODE");
        f.setSize(13);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDMCHCODE: Object ID");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:ITEM2_WO_NO)");
        f.setUpperCase();

        f = itemblk2.addField("ITEM2DESCRIPTION");
        f.setSize(30);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_SIGNATURE");
        f.setSize(8);
        f.setDbName("SIGNATURE");
        f.setMaxLength(20);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDITEM2_SIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk2.addField("SIGNATURENAME");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setMaxLength(5);
        f.setDbName("CONTRACT");
        f.setLabel("PCMWHISTORICALROUNDITEM2_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk2.addField("ENTERED","Date");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDENTERED: Entered");

        f = itemblk2.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDINT_DESTINATION_ID: Int Destination");

        f = itemblk2.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk2.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDDUE_DATE: Due Date");

        f = itemblk2.addField("STATE");
        f.setSize(10);
        f.setMaxLength(20);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDSTATE: Status");

        f = itemblk2.addField("SNOTETEXT");
        f.setSize(15);
        f.setMaxLength(2000);
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDSNOTETEXT: Total Value");
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_SIGNATURE_ID");
        f.setSize(6);
        f.setDbName("SIGNATURE_ID");
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(11);
        f.setHidden();
        f.setUpperCase();

        f = itemblk2.addField("NNOTEID", "Number");
        f.setSize(6);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(10);
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ORDERCLASS");
        f.setSize(3);
        f.setReadOnly();
        f.setMaxLength(3);
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("SNOTEIDEXIST");
        f.setSize(4);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_COMPANY");
        f.setSize(6);
        f.setHidden();
        f.setFunction("''");
        f.setMaxLength(20);
        f.setUpperCase();

        f = itemblk2.addField("NPREACCOUNTINGID", "Number");
        f.setSize(11);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setFunction("''");

        itemblk2.setView("MAINT_MATERIAL_REQUISITION");
        itemblk2.defineCommand("MAINT_MATERIAL_REQUISITION_API", "");
        itemset2 = itemblk2.getASPRowSet();
        itemblk2.setMasterBlock(headblk);

        itemtbl2 = mgr.newASPTable(itemblk2);

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");

        itemtbl2.setWrap();

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
        itemlay2.setSimple("INT_DESTINATION_DESC");
        itemlay2.setSimple("SIGNATURENAME");

        // ------------------------------------------------------------------------------------------------
        // -------------------------------------     ITEMBLK  ---------------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM5");

        f = itemblk.addField("ITEM5_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk.addField("ITEM5_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk.addField("LINE_ITEM_NO","Number");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDLINE_ITEM_NO: Line No");

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN");
        f.setInsertable();
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT",600,445);
        f.setMandatory();
        f.setMaxLength(25);
        f.setLabel("PCMWHISTORICALROUNDPART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setInsertable();
        f.setLabel("PCMWHISTORICALROUNDSPAREDESCRIPTION: Part Description");
        f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("CONDITION_CODE");
        f.setSize(10);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDCONDITIONCODE: Condition Code");

        f = itemblk.addField("CONDITION_CODE_DESC");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDCONDITIONCODEDESC: Condition Code Description");
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setReadOnly();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWHISTORICALROUNDPARTOWNERSHIP: Ownership");
        f.setCustomValidation("PART_OWNERSHIP","PART_OWNERSHIP_DB");

        f = itemblk.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDPARTOWNER: Owner");
        f.setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME,WO_CUST");
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk.addField("WO_CUST");
        f.setSize(20);
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM2_WO_NO)");

        f = itemblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setReadOnly();
        f.setMaxLength(5);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("ITEM_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
        f.setLabel("PCMWHISTORICALROUNDITEMJOBID: Job Id");

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWHISTORICALROUNDDIMQTY: Dimension/Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWHISTORICALROUNDTYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(13);
        f.setLabel("PCMWHISTORICALROUNDDATE_REQUIRED: Date Required");

        f = itemblk.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDPLAN_QTY: Quantity Required");

        f = itemblk.addField("ITEM_QTY","Number");
        f.setSize(13);
        f.setDbName("QTY");
        f.setInsertable();
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDQTYISS: Quantity Issued");

        f = itemblk.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDQTYONHAND: Quantity on Hand");
        //f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Quantity(:SPARE_CONTRACT,:PART_NO,NULL,'ONHAND',NULL,NULL,:PART_OWNERSHIP_DB,NULL,NULL,NULL,:OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,:CONDITION_CODE)");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDQTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setMandatory();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDQTY_RETURNED: Quantity Returned");

        f = itemblk.addField("QTY_SHORT","Number");
        f.setSize(11);
        f.setMandatory();
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDQTY_SHORT: Quantity Short");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWHISTORICALROUNDUNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("CATALOG_CONTRACT");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMaxLength(5);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();

        f = itemblk.addField("ITEM_CATALOG_NO");
        f.setSize(9);
        f.setDbName("CATALOG_NO");
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT",600,445);
        f.setLabel("PCMWHISTORICALROUNDITEM_CATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("CATALOGDESC");
        f.setSize(17);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDCATALOGDESC: Sales Part Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:ITEM_CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("PRICE_LIST_NO");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWHISTORICALROUNDPRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("ITEM_LIST_PRICE","Number");
        f.setSize(9);
        f.setDefaultNotVisible();
        f.setDbName("LIST_PRICE");
        f.setLabel("PCMWHISTORICALROUNDLIST_PRICE: Sales Price");

        f = itemblk.addField("DISCOUNT","Number");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDDISCOUNT: Discount %");

        f = itemblk.addField("SALESPRICEAMOUNT", "Number");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDSALESPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk.addField("COST", "Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDCOST: Cost");
        f.setFunction("''");

        f = itemblk.addField("AMOUNTCOST", "Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(15);
        f.setLabel("PCMWHISTORICALROUNDAMOUNTCOST: Cost Amount");
        f.setFunction("''");

        f = itemblk.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDSCODEA: Account");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDSCODEB: Cost Center");
        f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDSCODEF: Project No");
        f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDSCODEE: Object No");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDCODEC: Code C");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDCODED: Code D");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDCODEG: Code G");
        f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDCODEH: Code H");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDCODEI: Code I");
        f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWHISTORICALROUNDCODEJ: Code J");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("ITEM_WO_NO","Number","#");
        f.setSize(17);
        f.setMandatory();
        f.setMaxLength(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDITEM_WO_NO: Work Order No");
        f.setDbName("WO_NO");

        f = itemblk.addField("PLAN_LINE_NO","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWHISTORICALROUNDPLAN_LINE_NO: Plan Line No");

        f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number");
        f.setSize(17);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        itemblk.setView("MAINT_MATERIAL_REQ_LINE");
        itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API", "");
        itemset = itemblk.getASPRowSet();

        itemblk.setMasterBlock(itemblk2);

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM5");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM5");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

        // ------------------------------------------------------------------------------------------------
        // -------------------------------------     Permits Tab  -----------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk3.addField("PERMIT_SEQ","Number");
        f.setSize(18);
        f.setMandatory();
        f.setLabel("PCMWHISTORICALROUNDPERMITSEQ: Permit");
        f.setDynamicLOV("PERMIT", 600,450);
        f.setReadOnly();
        f.setMaxLength(6);

        f = itemblk3.addField("TYPE");
        f.setSize(18);
        f.setLabel("PCMWHISTORICALROUNDTYPE: Type");
        f.setFunction("PERMIT_API.Get_Permit_Type_Id(:PERMIT_SEQ)");
        f.setReadOnly();
        f.setMaxLength(4);

        f = itemblk3.addField("DESCRIPTION");
        f.setSize(55);
        f.setLabel("PCMWHISTORICALROUNDDESCRIPT: Description");
        f.setFunction("PERMIT_API.Get_Description(PERMIT_SEQ)");
        f.setReadOnly();
        f.setMaxLength(40);

        f = itemblk3.addField("ITEM3_WO_NO","Number","#");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDIT3WONO: WO No");
        f.setDbName("WO_NO");
        f.setReadOnly();

        itemblk3.setView("WORK_ORDER_PERMIT");
        itemblk3.defineCommand("WORK_ORDER_PERMIT_API", "");
        itemblk3.setMasterBlock(headblk);

        itemset3 = itemblk3.getASPRowSet(); 

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.enableCommand(itembar3.FIND);
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setWrap();
        // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
        itemtbl3.enableRowSelect();
        // 040128  ARWILK  End  (Enable Multirow RMB actions)

        itembar3.addCustomCommand("viewPermit",mgr.translate("PCMWHISTORICALROUNDVIEWPER: View Permit..."));

        // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar3.enableMultirowAction();
        // 040128  ARWILK  End  (Enable Multirow RMB actions)

        itembar3.disableCommandGroup(itembar3.CMD_GROUP_EDIT);

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

        // ------------------------------------------------------------------------------------------------
        // ------------------------------------     Postings Tab  -----------------------------------------
        // ------------------------------------------------------------------------------------------------

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk4.addField("COMPNY");
        f.setHidden();
        f.setDbName("COMPANY");

        f = itemblk4.addField("NOTE_ID");
        f.setHidden();

        f = itemblk4.addField("WORK_ORDER_COST_TYPE");
        f.setSize(25);
        f.setLabel("PCMWHISTORICALROUNDCOSTTYPE: Cost Type");
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk4.addField("CATALOG_NO");
        f.setSize(25);
        f.setLabel("PCMWHISTORICALROUNDCATANO: Sales Part Number");
        f.setDynamicLOV("SALES_PART",600,450); 
        f.setReadOnly();
        f.setMaxLength(25);

        f = itemblk4.addField("LINE_DESCRIPTION");
        f.setSize(40);
        f.setLabel("PCMWHISTORICALROUNDCATADESC: Description");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = itemblk4.addField("LIST_PRICE", "Money");
        f.setSize(10);
        f.setLabel("PCMWHISTORICALROUNDLISTPRICE: List Price");
        f.setReadOnly();

        f = itemblk4.addField("HRS_QTY", "Number");
        f.setSize(10);
        f.setLabel("PCMWHISTORICALROUNDHRSQTY: Hours/Qty");
        f.setDbName("QTY");
        f.setReadOnly();

        f = itemblk4.addField("COST_AMOUNT", "Money");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDCOSTAMT: Cost Amount");
        f.setDbName("AMOUNT");
        f.setReadOnly();

        f = itemblk4.addField("POST_PART_OWNERSHIP");
        f.setDbName("PART_OWNERSHIP");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDPARTOWNERSHIP: Ownership");

        f = itemblk4.addField("POST_PART_OWNERSHIP_DB");
        f.setDbName("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk4.addField("POST_OWNER");
        f.setDbName("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setLabel("PCMWHISTORICALROUNDPARTOWNER: Owner");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk4.addField("POST_OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWHISTORICALROUNDPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:POST_OWNER)");

        f = itemblk4.addField("CSS_TYPE");
        f.setSize(14);
        f.setLabel("PCMWHISTORICALROUNDCSSTYPE: CSS Type");
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_STATE");
        f.setDbName("STATE");
        f.setSize(14);
        f.setLabel("PCMWHISTORICALROUNDITEM4STATUS: Status");
        f.setReadOnly();

        f = itemblk4.addField("WORK_ORDER_BOOK_STATUS");
        f.setSize(22);
        f.setLabel("PCMWHISTORICALROUNDBKSTATUS: Booking Status");
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk4.addField("AUTH_SIG");
        f.setSize(22);
        f.setLabel("PCMWHISTORICALROUNDAUTHSIG: Auth Signature");
        f.setDbName("SIGNATURE");
        f.setDynamicLOV("EMPLOYEE_LOV",600,450); 
        f.setReadOnly();
        f.setMaxLength(20);

        f = itemblk4.addField("COMMENT");
        f.setSize(50);
        f.setLabel("PCMWHISTORICALROUNDCOMMENT: Comment");
        f.setDbName("CMNT");
        f.setReadOnly();
        f.setMaxLength(80);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ACCNT");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDACCONT: Account");
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk4.addField("COST_CENTER");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCOSTCENTER: Cost Center");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("PROJECT_NO");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDPROJNO: Project No");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("OBJECT_NO");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDOBJENO: Object No");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_C");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODEC: Code C");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_D");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODED: Code D");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_G");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODEG: Code G");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_H");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODEH: Code H");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_I");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODEI: Code I");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_J");
        f.setSize(12);
        f.setLabel("PCMWHISTORICALROUNDCODEJ: Code J");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("DOC_TEXT");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDDOCTEXT: Document Text");
        f.setCheckBox("FALSE,TRUE");
        f.setFunction("DOCUMENT_TEXT_API.Note_Id_Exist(:NOTE_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITION_NO");
        f.setSize(15);
        f.setLabel("PCMWHISTORICALROUNDREQNO: Requisition No");
        f.setReadOnly();
        f.setMaxLength(12);

        f = itemblk4.addField("REQUISITION_LINE_NO");
        f.setSize(20);
        f.setLabel("PCMWHISTORICALROUNDREQLINENO: Requisition Line No");
        f.setReadOnly();
        f.setMaxLength(4);
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITION_RELEASE_NO");
        f.setSize(24);
        f.setLabel("PCMWHISTORICALROUNDREQRELNO: Requisition Release No");
        f.setReadOnly();
        f.setMaxLength(4);
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQ_SUP_NO");
        f.setSize(24);
        f.setLabel("PCMWHISTORICALROUNDREQSUPNO: Requisition Supplier No");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Line_Vendor_No(:REQUISITION_NO, :REQUISITION_LINE_NO, :REQUISITION_RELEASE_NO)");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_WO_NO","Number","#");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWHISTORICALROUNDIT4WONO: WO No");
        f.setDbName("WO_NO");
        f.setReadOnly();

        f = itemblk4.addField("WORK_ORDER_ACCNT_TYPE");
        f.setHidden();
        f.setDbName("WORK_ORDER_ACCOUNT_TYPE");
        f.setReadOnly();

        f = itemblk4.addField("SIGNATURE_ID");
        f.setHidden();

        f = itemblk4.addField("ITEM4_ROW_NO");
        f.setHidden();
        f.setDbName("ROW_NO");
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_CONTRACT");
        f.setHidden();
        f.setDbName("CONTRACT");
        f.setReadOnly();

        f = itemblk4.addField("CONTRACT1");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("COMPANY1");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("LABELNAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CODENAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CODEPARTCOSTCENTER");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("VIEWNAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("PKGNAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("INTERNAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("LOGCODEPARTCOSTCENTER");
        f.setHidden();
        f.setFunction("''");

        itemblk4.setView("WORK_ORDER_CODING");
        itemblk4.defineCommand("WORK_ORDER_CODING_API", "");
        itemblk4.setMasterBlock(headblk);

        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setSimple("LINE_DESCRIPTION");

        itembar4.enableCommand(itembar4.FIND);
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();

        itembar4.removeCommandGroup(itembar4.CMD_GROUP_CUSTOM);
        itembar4.disableCommandGroup(itembar4.CMD_GROUP_EDIT);

        //---------------------------------------------------------------------------------
        //-----------------------------------  ITEMBLK6 -----------------------------------
        //---------------------------------------------------------------------------------

        itemblk6 = mgr.newASPBlock("ITEM6");

        itemblk6.addField("ITEM6_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk6.addField("ITEM6_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk6.addField("ITEM6_WO_NO", "Number", "#").
        setDbName("WO_NO").
        setHidden().
        setReadOnly().
        setInsertable().
        setMandatory();

        itemblk6.addField("JOB_ID", "Number").
        setLabel("HISTROUNDITEM6JOBID: Job ID").
        setReadOnly().
        setInsertable().
        setMandatory();

        itemblk6.addField("STD_JOB_ID").
        setSize(15).
        setLabel("HISTROUNDITEM6STDJOBID: Standard Job ID").
        setLOV("RoundStandardJobLov.page", "STD_JOB_CONTRACT CONTRACT", 600, 445, true).
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(12);

        itemblk6.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("HISTROUNDITEM6STDJOBCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        itemblk6.addField("STD_JOB_REVISION").
        setSize(10).
        setLabel("HISTROUNDITEM6STDJOBREVISION: Revision").
        setDynamicLOV("ROUND_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445, true).    
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(6);

        itemblk6.addField("ITEM6_DESCRIPTION").
        setDbName("DESCRIPTION").
        setSize(35).
        setLabel("HISTROUNDITEM6DESCRIPTION: Description").
        setUpperCase().
        setMandatory().
        setInsertable().
        setMaxLength(4000);

        itemblk6.addField("ITEM6_QTY", "Number").
        setDbName("QTY").
        setLabel("HISTROUNDITEM6QTY: Quantity").
        setMandatory().
        setInsertable();

        itemblk6.addField("STD_JOB_STATUS").
        setLabel("HISTROUNDSTDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");

        itemblk6.addField("ITEM6_COMPANY").
        setDbName("COMPANY").
        setSize(20).
        setHidden().
        setUpperCase().
        setInsertable();

        itemblk6.addField("SIGN_ID").
        setSize(35).
        setLabel("HISTROUNDITEM6SIGNID: Executed By").
        setQueryable().
        setDbName("SIGNATURE").
        //setFunction("Company_Emp_API.Get_Person_Id(COMPANY, EMPLOYEE_ID)").
        setReadOnly();

        itemblk6.addField("EMPLOYEE_ID").
        setSize(15).
        setLabel("HISTROUNDITEM6EMPLOYEEID: Employee ID").
        setDynamicLOV("EMPLOYEE_NO", "ITEM6_COMPANY COMPANY", 600, 445).    
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(11);

        itemblk6.addField("DATE_FROM", "Datetime").
        setSize(20).
        setLabel("HISTROUNDITEM6DATEFROM: Date From").
        setInsertable();

        itemblk6.addField("DATE_TO", "Datetime").
        setSize(20).
        setLabel("HISTROUNDITEM6DATETO: Date To").
        setInsertable();

        itemblk6.addField("STD_JOB_FLAG", "Number").
        setHidden().
        setInsertable();

        itemblk6.addField("KEEP_CONNECTIONS").
        setHidden().
        setSize(3).
        setInsertable();

        itemblk6.addField("RECONNECT").
        setHidden().
        setSize(3).
        setInsertable();

        // -----------------------------------------------------------------------
        // -----------------------  Hidden Fields --------------------------------
        // -----------------------------------------------------------------------

        itemblk6.addField("N_JOB_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_STD_JOB_EXIST").
        setFunction("''").
        setHidden();

        itemblk6.addField("N_ROLE_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_MAT_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_TOOL_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_PLANNING_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_DOC_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_STD_JOB_ID").
        setFunction("''").
        setHidden();

        itemblk6.addField("S_STD_JOB_CONTRACT").
        setFunction("''").
        setHidden();

        itemblk6.addField("S_STD_JOB_REVISION").
        setFunction("''").
        setHidden();

        itemblk6.addField("N_QTY", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_IS_SEPARATE").
        setFunction("''").
        setHidden();

        itemblk6.addField("S_AGREEMENT_ID").
        setFunction("''").
        setHidden();

        itemblk6.setView("WORK_ORDER_JOB");
        itemblk6.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
        itemblk6.setMasterBlock(headblk);

        itemset6 = itemblk6.getASPRowSet();

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setTitle(mgr.translate("HISTROUNDITEM6WOJOBS: Jobs"));
        itemtbl6.setWrap();

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.enableCommand(itembar6.FIND);

        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");

        itembar6.disableCommand(itembar6.NEWROW);
        itembar6.disableCommand(itembar6.EDITROW);
        itembar6.disableCommand(itembar6.DELETE);
        itembar6.disableCommand(itembar6.DUPLICATEROW);

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);
    }

    public void setDynamicLovs()
    {
        ASPManager mgr = getASPManager();

        int indexB;
        int indexE;
        int indexF;     
        String sCompany;
        String lblNameA;
        String lblNameB;
        String lblNameC;
        String lblNameD;
        String lblNameE;
        String lblNameF;
        String lblNameG;
        String lblNameH;
        String lblNameI;
        String lblNameJ;
        String viewNameB;   
        String viewNameE;
        String viewNameF;    
        String viewNameBF;
        String viewNameBL;
        String viewNameEF;
        String viewNameEL;
        String viewNameFF;
        String viewNameFL;   

        trans.clear();
        ASPCommand cmd = trans.addCustomFunction( "CONT", "User_Default_API.Get_Contract", "CONTRACT1" );

        cmd = trans.addCustomFunction( "GETCOM", "Site_API.Get_Company", "COMPANY1" );
        cmd.addReference("CONTRACT1", "CONT/DATA");

        cmd = trans.addCustomFunction( "GETNAMEA", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "A");

        cmd = trans.addCustomCommand( "GETVIEWB", "Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODENAME");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER", "CostCenter");

        cmd = trans.addCustomFunction( "GETNAMEB", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "B");

        cmd = trans.addCustomFunction( "GETNAMEC", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "C");

        cmd = trans.addCustomFunction( "GETNAMED", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "D");

        cmd = trans.addCustomCommand( "GETVIEWE", "Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER", "Object");

        cmd = trans.addCustomFunction( "GETNAMEE", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "E");

        cmd = trans.addCustomCommand( "GETVIEWF", "Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER", "Project");

        cmd = trans.addCustomFunction( "GETNAMEF", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "F");

        cmd = trans.addCustomFunction( "GETNAMEG", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "G");

        cmd = trans.addCustomFunction( "GETNAMEH", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "H");

        cmd = trans.addCustomFunction( "GETNAMEI", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "I");

        cmd = trans.addCustomFunction( "GETNAMEJ", "Accounting_Code_Parts_API.Get_Name", "LABELNAME");
        cmd.addReference("COMPANY1", "GETCOM/DATA");
        cmd.addParameter("CODENAME", "J");

        trans = mgr.perform(trans);

        sCompany = trans.getValue("GETCOM/DATA/COMPANY1");

        lblNameA = trans.getValue("GETNAMEA/DATA/LABELNAME");
        lblNameB = trans.getValue("GETNAMEB/DATA/LABELNAME");
        lblNameC = trans.getValue("GETNAMEC/DATA/LABELNAME");
        lblNameD = trans.getValue("GETNAMED/DATA/LABELNAME");
        lblNameE = trans.getValue("GETNAMEE/DATA/LABELNAME");
        lblNameF = trans.getValue("GETNAMEF/DATA/LABELNAME");
        lblNameG = trans.getValue("GETNAMEG/DATA/LABELNAME");
        lblNameH = trans.getValue("GETNAMEH/DATA/LABELNAME");
        lblNameI = trans.getValue("GETNAMEI/DATA/LABELNAME");
        lblNameJ = trans.getValue("GETNAMEJ/DATA/LABELNAME");

        viewNameB = trans.getValue("GETVIEWB/DATA/VIEWNAME");
        indexB = viewNameB.indexOf("_", 1);

        if (indexB > -1)
        {
            viewNameBF = viewNameB.substring(0, indexB); 
            viewNameBL = viewNameB.substring(indexB+1,viewNameB.length());
        }
        else
        {
            viewNameBF = "";
            viewNameBL = "";
        }

        viewNameE = trans.getValue("GETVIEWE/DATA/VIEWNAME");
        indexE = viewNameE.indexOf("_", 1);

        if (indexE > -1)
        {
            viewNameEF = viewNameE.substring(0, indexE); 
            viewNameEL = viewNameE.substring(indexE+1,viewNameE.length());
        }
        else
        {
            viewNameEF = "";
            viewNameEL = "";
        }     

        viewNameF = trans.getValue("GETVIEWF/DATA/VIEWNAME");
        indexF = viewNameF.indexOf("_", 1);

        if (indexF > -1)
        {
            viewNameFF = viewNameF.substring(0, indexF); 
            viewNameFL = viewNameF.substring(indexF+1,viewNameF.length());
        }
        else
        {
            viewNameFF = "";
            viewNameFL = "";
        }

        ctx.setGlobal("NAMEA",lblNameA);
        ctx.setGlobal("NAME"+viewNameBL ,lblNameB);
        ctx.setGlobal("NAMEC",lblNameC);
        ctx.setGlobal("NAMED",lblNameD);
        ctx.setGlobal("NAME"+viewNameEL ,lblNameE);
        ctx.setGlobal("NAME"+viewNameFL ,lblNameF);
        ctx.setGlobal("NAMEG",lblNameG);
        ctx.setGlobal("NAMEH",lblNameH);
        ctx.setGlobal("NAMEI",lblNameI);
        ctx.setGlobal("NAMEJ",lblNameJ);


        mgr.getASPField("ACCNT").setDynamicLOV("ACCOUNTING_CODEPART_A",600,450);
        mgr.getASPField("ACCNT").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("ACCNT").setLabel(lblNameA);

        mgr.getASPField("COST_CENTER").setDynamicLOV(viewNameB,600,450);
        mgr.getASPField("COST_CENTER").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("COST_CENTER").setLabel(lblNameB);

        mgr.getASPField("PROJECT_NO").setDynamicLOV(viewNameF,600,450);
        mgr.getASPField("PROJECT_NO").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("PROJECT_NO").setLabel(lblNameF);

        mgr.getASPField("OBJECT_NO").setDynamicLOV(viewNameE,600,450);   
        mgr.getASPField("OBJECT_NO").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("OBJECT_NO").setLabel(lblNameE);

        mgr.getASPField("CODE_C").setDynamicLOV("ACCOUNTING_CODEPART_C",600,450);
        mgr.getASPField("CODE_C").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_C").setLabel(lblNameC);

        mgr.getASPField("CODE_D").setDynamicLOV("ACCOUNTING_CODEPART_D",600,450);
        mgr.getASPField("CODE_D").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_D").setLabel(lblNameD);

        mgr.getASPField("CODE_G").setDynamicLOV("ACCOUNTING_CODEPART_G",600,450);
        mgr.getASPField("CODE_G").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_G").setLabel(lblNameG);

        mgr.getASPField("CODE_H").setDynamicLOV("ACCOUNTING_CODEPART_H",600,450);
        mgr.getASPField("CODE_H").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_H").setLabel(lblNameH);

        mgr.getASPField("CODE_I").setDynamicLOV("ACCOUNTING_CODEPART_I",600,450);
        mgr.getASPField("CODE_I").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_I").setLabel(lblNameI);

        mgr.getASPField("CODE_J").setDynamicLOV("ACCOUNTING_CODEPART_J",600,450);
        mgr.getASPField("CODE_J").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("CODE_J").setLabel(lblNameJ);
    }

    public void checkObjAvailable()
    {
        ASPManager mgr = getASPManager();

        if (!isSecurityChecked)
        {
            trans.clear();

            trans.addSecurityQuery("Historical_Work_Order_API","Check_Work_Order");

            trans.addPresentationObjectQuery("PCMW/WorkOrderRequisHeaderRMB.page,"+
                                             "PCMW/Permit.page");

            trans = mgr.perform(trans);

            ASPBuffer secViewBuff = trans.getSecurityInfo();

            actionsBuffer = mgr.newASPBuffer();

            if (secViewBuff.itemExists("Historical_Work_Order_API.Check_Work_Order"))
                actionsBuffer.addItem("okHeadReOpen","");

            if (secViewBuff.namedItemExists("PCMW/WorkOrderRequisHeaderRMB.page"))
                actionsBuffer.addItem("okHeadRequisitions","");

            if (secViewBuff.namedItemExists("PCMW/Permit.page"))
                actionsBuffer.addItem("okItem3ViewPermit","");

            isSecurityChecked = true;
        }
    }

    public void adjustActions()
    {
        ASPManager mgr = getASPManager();

        if (!actionsBuffer.itemExists("okHeadReOpen"))
            headbar.removeCustomCommand("reOpen");

        if (!actionsBuffer.itemExists("okHeadRequisitions"))
            headbar.removeCustomCommand("requisitions");

        if (!actionsBuffer.itemExists("okItem3ViewPermit"))
            itembar3.removeCustomCommand("viewPermit");
    }

    // 031216  ARWILK  Begin  (Replace blocks with tabs)
    public void activateGeneral()
    {
        tabs.setActiveTab(1);
    }

    public void activateTimeReport()
    {
        tabs.setActiveTab(2);
    }

    public void activateMaterial()
    {
        tabs.setActiveTab(3);
    }

    public void activatePermits()
    {
        tabs.setActiveTab(4);
    }

    public void activatePostings()
    {
        tabs.setActiveTab(5);
    }

    public void activateJobs()
    {
        tabs.setActiveTab(6);
    }
    // 031216  ARWILK  End  (Replace blocks with tabs)

    // 040128  ARWILK  Begin  (Enable Multirow RMB actions)
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
    // 040128  ARWILK  End  (Enable Multirow RMB actions)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateGeneral");
        headbar.removeCustomCommand("activateTimeReport");
        headbar.removeCustomCommand("activateMaterial");
        headbar.removeCustomCommand("activatePermits");
        headbar.removeCustomCommand("activatePostings");
        headbar.removeCustomCommand("activateJobs");
        // 031216  ARWILK  End  (Replace blocks with tabs)

        if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
            mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_NO","NEWWIN");

        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");

        if (mgr.isPresentationObjectInstalled("orderw/SalesPartOvw.page"))
            mgr.getASPField("CATALOG_NO").setHyperlink("../orderw/SalesPartOvw.page","CONTRACT, CATALOG_NO","NEWWIN");

        if (itemlay.isMultirowLayout())
        {
            mgr.getASPField("VENDOR_NO").setDynamicLOV("SUPPLIER_INFO",600,450);

            if (mgr.isPresentationObjectInstalled("enterw/SupplierInfoGeneral.page"))
                mgr.getASPField("VENDOR_NO").setHyperlink("../enterw/SupplierInfoGeneral.page","VENDOR_NO","NEWWIN");
        }
        else
        {
            mgr.getASPField("SROUNDDEFDESCR").setHidden();       
            mgr.getASPField("REPORTED_BY_ID").setHidden();    
            mgr.getASPField("REPORT_IN_BY_ID").setHidden();   
            mgr.getASPField("ADDRESS_ID").setHidden();           
            mgr.getASPField("LU_NAME").setHidden();              

            itembar.disableCommand(itembar.EDIT);
            itembar.enableRowStatus();
        }

        setDynamicLovs();

        fldTitleTestPointId = mgr.translate("PCMWHISTORICALROUNDTESTPNTFLD: Testpoint");
        fldTitleEmpNo = mgr.translate("PCMWHISTORICALROUNDEMPFLD: Employee+ID");
        fldTitleSignature = mgr.translate("PCMWHISTORICALROUNDSIGFLD: Signature");
        fldTitleCatalogNo = mgr.translate("PCMWHISTORICALROUNDSALESPARTFLD: Sales+Part+Number");
        fldTitleAuthSig = mgr.translate("PCMWHISTORICALROUNDAUTHSIGFLD: Auth+Signature");

        lovTitleTestPointId = mgr.translate("PCMWHISTORICALROUNDTESTPNTLOV: List+of+Testpoint");
        lovTitleEmpNo = mgr.translate("PCMWHISTORICALROUNDEMPLOV: List+of+Employee+ID");
        lovTitleSignature = mgr.translate("PCMWHISTORICALROUNDSIGLOV: List+of+Signature");   
        lovTitleCatalogNo = mgr.translate("PCMWHISTORICALROUNDSALESPARTLOV: List+of+Sales+Part+Number");
        lovTitleAuthSig = mgr.translate("PCMWHISTORICALROUNDAUTHSIGLOV: List+of+Auth+Signature");  

	if (!mgr.isEmpty(mgr.getQueryStringValue("IS_POSTING_ACTIVATE"))) {
	    this.activatePostings();
	}
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWHISTORICALROUNDHISTROUTEWO: Historical Route Work Order";
    }

    protected String getTitle()
    {
        return "PCMWHISTORICALROUNDHISTROUTEWO: Historical Route Work Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        if (headlay.isSingleLayout() && (headset.countRows() > 0))
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
                appendToHTML(itemlay0.show());
            else if (tabs.getActiveTab() == 2)
                appendToHTML(itemlay1.show());
            else if (tabs.getActiveTab() == 3)
            {
                appendToHTML(itemlay2.show());

                if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
                    appendToHTML(itemlay.show());
            }
            else if (tabs.getActiveTab() == 4)
                appendToHTML(itemlay3.show());
            else if (tabs.getActiveTab() == 5)
                appendToHTML(itemlay4.show());
            else if (tabs.getActiveTab() == 6)
                appendToHTML(itemlay6.show());
        }
        // 031216  ARWILK  End  (Replace blocks with tabs)

        printHiddenField("VIEWWORKORDER", "FALSE");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"frmHistoricalRound\";\n");

        appendDirtyJavaScript("function lovTestPointId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__')\n");
        appendDirtyJavaScript("	openLOVWindow('TEST_POINT_ID',i, \n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=");
        appendDirtyJavaScript(fldTitleTestPointId);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleTestPointId);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,450,'validateTestPointId');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	openLOVWindow('TEST_POINT_ID',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=");
        appendDirtyJavaScript(fldTitleTestPointId);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleTestPointId);
        appendDirtyJavaScript("'\n");
	appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		,600,450,'validateTestPointId');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovEmpNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__')\n");
        appendDirtyJavaScript("	openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_NO&__FIELD=");
        appendDirtyJavaScript(fldTitleEmpNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleEmpNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,450,'validateEmpNo');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_NO&__FIELD=");
        appendDirtyJavaScript(fldTitleEmpNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleEmpNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,600,450,'validateEmpNo');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovSignature(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__')\n");
        appendDirtyJavaScript("	openLOVWindow('SIGNATURE',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleSignature);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleSignature);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,450,'validateSignature');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	openLOVWindow('SIGNATURE',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleSignature);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleSignature);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,600,450,'validateSignature');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("	openLOVWindow('CATALOG_NO',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART&__FIELD=");
        appendDirtyJavaScript(fldTitleCatalogNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleCatalogNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,450,'validateCatalogNo');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	openLOVWindow('CATALOG_NO',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART&__FIELD=");
        appendDirtyJavaScript(fldTitleCatalogNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleCatalogNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + getValue_('CONTRACT',i)\n");
        appendDirtyJavaScript("		,600,450,'validateCatalogNo');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovAuthSig(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM4',i)=='QueryMode__')\n");
        appendDirtyJavaScript("	openLOVWindow('AUTH_SIG',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleAuthSig);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleAuthSig);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,450,'validateAuthSig');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	openLOVWindow('AUTH_SIG',i,\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(url);
        appendDirtyJavaScript("'+'/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleAuthSig);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleAuthSig);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
	appendDirtyJavaScript("		,600,450,'validateAuthSig');\n");
        appendDirtyJavaScript("}\n");

        // 040128  ARWILK  Begin  (Remove uneccessary global variables)
        if (bOpenNewWindow)
        {

            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));  //XSS_Safe AMNILK 20070716
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
        // 040128  ARWILK  End  (Remove uneccessary global variables)

        appendDirtyJavaScript("function viewWorkOrder()\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("if (confirm(\" ");
	appendDirtyJavaScript(mgr.translateJavaScript("PCMWHISTORICALSEPARATERMBVIEWWO: Work Order(s) opened successfully! Do you want to view the reopened work order(s)?"));
	appendDirtyJavaScript("\" )) \n");
	appendDirtyJavaScript("{ \n");
	appendDirtyJavaScript("document.form.VIEWWORKORDER.value = \"TRUE\"; \n");
	appendDirtyJavaScript("return true;\n");
	appendDirtyJavaScript(" }\n");
	appendDirtyJavaScript("else \n");
	appendDirtyJavaScript("{ \n");
	appendDirtyJavaScript("document.form.VIEWWORKORDER.value = \"FALSE\"; \n");
	appendDirtyJavaScript("return true;\n");
	appendDirtyJavaScript("} \n");
	appendDirtyJavaScript("}\n");
    }
}
