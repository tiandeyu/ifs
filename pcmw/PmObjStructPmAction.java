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
*  File        : PmObjStructPmAction.java 
*  Created     : 041221  NIJALK (AMEC112 - Job Program)
*  Modified    :
*  050117   NIJALK   Removed unused variables.
*  050329   NIJALK   Added addJobProgramToObjectITEM(),addJobProgramToObjectHEAD(). Modified predefine().
*  051109   NIJALK   Bug 128730: Added fields PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT to headblk & itemblk.
*  060815   AMNILK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060904   AMNILK   Merged Bug Id: 58216.
*  070725   AMNILK   Eliminated XSS Security Vulnerability.
*  071127   SHAFLK   Bug 69515, Added Contractor, Contractor Name and Material.
*  080206   CHANLK	 Bug 71027, Change java script function CCA.
*  091027   SHAFLK   Bug 86600, Modified preDefine(), printContents(), countFindITEM() and countFind().
*  100319   CHANLK	 Bug 89374, Added field MchCodeContract.
*  100713   VIATLK  Bug 91231, Modified where condition in okFindITEM().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PmObjStructPmAction extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmObjStructPmAction");

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
    private ASPBuffer SecBuff;
    private int currrow;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPField f;

    private boolean bOpenNewWindow;
    private String newWinHandle;
    private int newWinHeight = 600;
    private int newWinWidth = 900;
    private String urlString;

    private String sMchCode;
    private String sContract;
    private String calling_url;

    private int tot_head_rows;
    private int tot_item_rows;

    private String show = "";
    
    //===============================================================
    // Construction 
    //===============================================================
    public PmObjStructPmAction(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        sMchCode = "";
        sContract = "";
        calling_url = "";
        tot_head_rows = 0;
        tot_item_rows = 0;
        
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        sMchCode = ctx.readValue("MCHCODE",sMchCode);
        sContract = ctx.readValue("CONTR",sContract);
        show = ctx.readValue("SHOW",show);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.readValue("MCH_CODE")) && !mgr.isEmpty(mgr.readValue("CONTRACT")))
        {
            sMchCode =  mgr.readValue("MCH_CODE");
            sContract = mgr.readValue("CONTRACT");
            show = "1";
            okFind();
            okFindITEM();
        }
        else if (mgr.dataTransfered())
        {
            show = "1";
            okFind();
            okFindITEM();
        }

        adjust();

        ctx.writeValue("MCHCODE",sMchCode);
        ctx.writeValue("CONTR",sContract);
        ctx.writeValue("SHOW",show);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
    
//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
	    //Bug 58216 Start
	    q.addWhereCondition("MCH_CODE= ?");
            q.addWhereCondition("CONTRACT= ?");
	    q.addParameter("MCH_CODE",sMchCode);
	    q.addParameter("ITEM_MCH_CODE_CONTRACT",sContract);
	    //Bug 58216 End

            if (mgr.dataTransfered())
                    q.addOrCondition(mgr.getTransferedData());

            q.includeMeta("ALL");

            mgr.querySubmit(trans, headblk);

            tot_head_rows = headset.countRows();

            if (headset.countRows() == 0)
            {
                    headset.clear();
            }  
    }

    public void countFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
            q.setSelectList("to_char(count(*)) N");
            q.addWhereCondition("MCH_CODE= ?");
            q.addWhereCondition("CONTRACT= ?");
            q.addParameter("MCH_CODE",sMchCode);
            q.addParameter("ITEM_MCH_CODE_CONTRACT",sContract);
            mgr.submit(trans);
            headlay.setCountValue(toInt(headset.getRow().getValue("N")));
            headset.clear();
    }

    public void okFindITEM()
    {
       ASPManager mgr = getASPManager();
       //Bug ID 91231, Start
       //Bug 58216 Start
       String sWhere = "(MCH_CODE,MCH_CODE_CONTRACT) IN (SELECT MCH_CODE,CONTRACT FROM Maintenance_Object START WITH SUP_MCH_CODE = ? AND SUP_CONTRACT = ? CONNECT BY PRIOR MCH_CODE = SUP_MCH_CODE AND PRIOR CONTRACT = SUP_CONTRACT)";
       //Bug 58216 End
       // Bug ID Bug 91231, End
       trans.clear();
       
       q = trans.addQuery(itemblk);
       //Bug 58216 Start
       q.addWhereCondition(sWhere);
       q.addParameter("MCH_CODE",sMchCode);
       q.addParameter("ITEM_MCH_CODE_CONTRACT",sContract);
       //Bug 58216 End
       
       if (mgr.dataTransfered())
          q.addOrCondition(mgr.getTransferedData());
       
       q.includeMeta("ALL");
       
       mgr.querySubmit(trans, itemblk);
       
       tot_item_rows = itemset.countRows();
       
       if (itemset.countRows() == 0) {
          itemset.clear();
       }
    }

    public void countFindITEM()
    {
        ASPManager mgr = getASPManager();

        String sWhere = "(MCH_CODE,MCH_CODE_CONTRACT) IN (SELECT MCH_CODE,CONTRACT FROM Maintenance_Object START WITH SUP_MCH_CODE = ? AND SUP_CONTRACT = ? CONNECT BY PRIOR MCH_CODE = SUP_MCH_CODE AND CONTRACT = SUP_CONTRACT)";
        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition(sWhere);
	q.addParameter("MCH_CODE",sMchCode);
	q.addParameter("ITEM_MCH_CODE_CONTRACT",sContract);
        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
    }
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
           
    private String createTransferUrl(String url, ASPBuffer object)
    {
            ASPManager mgr = getASPManager();

            try
            {
                    String pkg = mgr.pack(object,1900 - url.length());
                    char sep = url.indexOf('?') > 0 ? '&' : '?';
                    urlString = url + sep + "__TRANSFER=" + pkg ;
                    return urlString;
            }
            catch (Throwable any)
            {
                    return null;
            }
    }

    public void openNewPmWindow(ASPBlockLayout lay1, ASPRowSet set1, String winHandle)
    {
        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (lay1.isMultirowLayout())
                set1.store();
        else
        {
                set1.unselectRows();
                set1.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl(winHandle+".page", set1.getSelectedRows("PM_NO,PM_REVISION"));
        newWinHandle = winHandle;                    
    }

    public void separatePM()
    {
        openNewPmWindow(headlay, headset, "PmAction");
    }

    public void roundPM()
    {
        openNewPmWindow(headlay, headset, "PmActionRound");
    }

    public void separatePMItem()
    {
        openNewPmWindow(itemlay, itemset, "PmAction");
    }

    public void roundPMItem()
    {
        openNewPmWindow(itemlay, itemset, "PmActionRound");
    }

    public void addJobProgramToObjectHEAD()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;
        urlString = "JobProgToObjMain.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT"));

        newWinHandle = "JobProgToObjMain"; 
    }

    public void addJobProgramToObjectITEM()
    {
        ASPManager mgr = getASPManager();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        bOpenNewWindow = true;
        urlString = "JobProgToObjMain.page?MCH_CODE=" + mgr.URLEncode(itemset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(itemset.getValue("MCH_CODE_CONTRACT"));

        newWinHandle = "JobProgToObjMain"; 
    }

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");    
        f.setHidden();

        f = headblk.addField("OBJSTATE");    
        f.setHidden();

        f = headblk.addField("PM_NO");
        f.setLabel("PCMWPMOBJSTRUCTPMNO: PM No");
        f.setSize(10);
        f.setReadOnly();

        f = headblk.addField("PM_REVISION");
        f.setLabel("PCMWPMOBJSTRUCTPMREVISION: Revision");
        f.setSize(10);
        f.setReadOnly();

        f = headblk.addField("PM_TYPE");
        f.setLabel("PCMWPMOBJSTRUCTPMTYPE: PM Type");
        f.setSize(30);
        f.setReadOnly();

        f = headblk.addField("ACTION_CODE_ID");
        f.setLabel("PCMWPMOBJSTRUCTACTIONCODEID: Action");
        f.setSize(7);
        f.setReadOnly();

        f = headblk.addField("ACTION_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTACTIONDESCR: Action Description");
        f.setSize(20);
        f.setReadOnly();

        f = headblk.addField("START_VALUE");
        f.setLabel("PCMWPMOBJSTRUCTSTARTVALUE: Start Value");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_START_UNIT");
        f.setLabel("PCMWPMOBJSTRUCTPSSTARTUNIT: Start Unit");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("INTERVAL");
        f.setLabel("PCMWPMOBJSTRUCTPSINTERVAL: Interval");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_INTERVAL_UNIT");
        f.setLabel("PCMWPMOBJSTRUCTPSPMINTERVALUNIT: Interval Unit");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("MCH_CODE");
        f.setLabel("PCMWPMOBJSTRUCTMCHCODE: Object ID");
        f.setSize(20);
        f.setReadOnly();

        // Bug 89374, Start
        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setLabel("PCMWPMOBJSTRUCTMCHCODECONTRACT: Mch Code Contract");
        f.setSize(5);
        f.setReadOnly();
        // Bug 89374, End

        f = headblk.addField("ORG_CONTRACT");
        f.setLabel("PCMWPMOBJSTRUCTORGCONTRACT: Site");
        f.setSize(5);
        f.setReadOnly();

        f = headblk.addField("ORG_CODE");
        f.setLabel("PCMWPMOBJSTRUCTORGCODE: Maint.Org.");
        f.setSize(8);
        f.setReadOnly();

        f = headblk.addField("DESCRIPTION");
        f.setLabel("PCMWPMOBJSTRUCTDESCRIPTION: Work Description");
        f.setSize(35);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("ROUNDDEF_ID");
        f.setLabel("PCMWPMOBJSTRUCTROUNDDEFID: Route ID");
        f.setSize(8);
        f.setReadOnly();

        f = headblk.addField("STATE");
        f.setLabel("PCMWPMOBJSTRUCTSTATE: State");
        f.setSize(7);
        f.setReadOnly();

        f = headblk.addField("PM_TYPE_DB");
        f.setLabel("PCMWPMOBJSTRUCTPMTYPEDB: PM Type Db");
        f.setHidden();


        f = headblk.addField("WORK_TYPE_ID");
        f.setLabel("PCMWPMOBJSTRUCTWORKTYPEID: Work Type");
        f.setSize(10);
        f.setReadOnly();

        f = headblk.addField("WORK_TYPE_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTWORKTYPEDESCR: Description");
        f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
        f.setSize(10);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("TEST_POINT_ID");
        f.setLabel("PCMWPMOBJSTRUCTTESTPOINTID: Testpoint");
        f.setSize(6);
        f.setReadOnly();

        f = headblk.addField("TEST_POINT_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTTESTPOINTDESCR: Description");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("LOCATION");
        f.setLabel("PCMWPMOBJSTRUCTLOCATION: Location");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setReadOnly();

        f = headblk.addField("OP_STATUS_ID");
        f.setLabel("PCMWPMOBJSTRUCTOPSTATUSID: Operational Status");
        f.setSize(3);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("OP_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTOPDESCR: Description");
        f.setFunction("OPERATIONAL_STATUS_API.Get_Description(:OP_STATUS_ID)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("MAINT_EMP_SIGN");
        f.setLabel("PCMWPMOBJSTRUCTMAINTEMPSIGN: Executed By");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("SIGNATURE");
        f.setLabel("PCMWPMOBJSTRUCTSIGNATURE: Planned By");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("VENDOR_NO");
        f.setSize(12);
        f.setLabel("PCMWPMOBJSTRUCTVENDNO: Contractor");
        f.setReadOnly();

        f = headblk.addField("VENDORNAME");
        f.setSize(50);
        f.setLabel("PCMWPMOBJSTRUCTVENDNAME: Contractor Name");
        f.setFunction("Maintenance_Supplier_API.Get_Description(:VENDOR_NO)");
        f.setReadOnly();

        f = headblk.addField("MATERIALS");
        f.setSize(70);
        f.setLabel("PCMWPMOBJSTRUCTMATERIALS: Material");
        f.setMaxLength(2000);
        f.setReadOnly();

        headblk.setView("PM_ACTION");
        headblk.setTitle(mgr.translate("PCMWPMOBJSTRUCTPMACTIONHEADTITLE: Selected Object:"));
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableModeLabel();
        headbar.enableMultirowAction();


        headbar.defineCommand(headbar.COUNTFIND,"countFind");
        headbar.enableCommand(headbar.FIND);
        headbar.defineCommand(headbar.OKFIND,"okFind");

        headbar.addCustomCommand("separatePM",mgr.translate("PCMWPMOBJSTRUCTPMACTIONSEPERATEPM: Separate PM Action..."));
        headbar.addCustomCommand("roundPM",mgr.translate("PCMWPMOBJSTRUCTPMACTIONROUNDPM: Round PM Action..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("addJobProgramToObjectHEAD",mgr.translate("PCMWPMOBJSTRUCTPMACTADDJOB: Add Job Program to Object..."));

        headbar.addCommandValidConditions("addJobProgramToObjectHEAD","OBJSTATE","Enable","Preliminary;Active");
        headbar.addCommandValidConditions("separatePM","PM_TYPE_DB","Enable","ActiveSeparate");
        headbar.addCommandValidConditions("roundPM","PM_TYPE_DB","Enable","ActiveRound");

        headbar.removeFromMultirowAction("addJobProgramToObjectHEAD");

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headtbl.enableRowSelect();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);

        this.disableFooter();
        this.disableHeader();
        this.disableHelp();
        this.disableHomeIcon();
        this.disableNavigate();
        this.disableOptions();
        //-------------itemblk----------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM");

        f = itemblk.addField("ITEM_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk.addField("ITEM_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk.addField("ITEM_OBJSTATE");
        f.setDbName("OBJSTATE");    
        f.setHidden();

        f = itemblk.addField("ITEM_PM_NO");
        f.setDbName("PM_NO");
        f.setLabel("PCMWPMOBJSTRUCTITEMPMNO: PM No");
        f.setSize(10);
        f.setReadOnly();

        f = itemblk.addField("ITEM_PM_REVISION");
        f.setDbName("PM_REVISION");
        f.setLabel("PCMWPMOBJSTRUCTITEMPMREVISION: Revision");
        f.setSize(10);
        f.setReadOnly();

        f = itemblk.addField("ITEM_PM_TYPE");
        f.setDbName("PM_TYPE");
        f.setLabel("PCMWPMOBJSTRUCTITEMPMTYPE: PM Type");
        f.setSize(30);
        f.setReadOnly();

        f = itemblk.addField("ITEM_ACTION_CODE_ID");
        f.setDbName("ACTION_CODE_ID");
        f.setLabel("PCMWPMOBJSTRUCTITEMACTIONCODEID: Action");
        f.setSize(7);
        f.setReadOnly();

        f = itemblk.addField("ITEM_ACTION_DESCR");
        f.setDbName("ACTION_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTITEMACTIONDESCR: Action Description");
        f.setSize(20);
        f.setReadOnly();

        f = itemblk.addField("ITEM_START_VALUE");
        f.setDbName("START_VALUE");
        f.setLabel("PCMWPMOBJSTRUCTITEMSTARTVALUE: Start Value");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("ITEM_PM_START_UNIT");
        f.setDbName("PM_START_UNIT");
        f.setLabel("PCMWPMOBJSTRUCTITEMSTARTUNIT: Start Unit");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("ITEM_INTERVAL");
        f.setDbName("INTERVAL");
        f.setLabel("PCMWPMOBJSTRUCTITEMINTERVAL: Interval");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("ITEM_PM_INTERVAL_UNIT");
        f.setDbName("PM_INTERVAL_UNIT");
        f.setLabel("PCMWPMOBJSTRUCTITEMINTERVALUNIT: Interval Unit");
        f.setSize(10);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("ITEM_MCH_CODE");
        f.setDbName("MCH_CODE");
        f.setLabel("PCMWPMOBJSTRUCTITEMMCHCODE: Object ID");
        f.setSize(20);
        f.setReadOnly();

        // Bug 89374, Start
        f = itemblk.addField("ITEM_MCH_CODE_CONTRACT");
        f.setDbName("MCH_CODE_CONTRACT");
        f.setLabel("PCMWPMOBJSTRUCTITEMMCHCODECONTRACT: Mch Code Contract");
        f.setSize(5);
        f.setReadOnly();
        // Bug 89374, End

        f = itemblk.addField("ITEM_ORG_CONTRACT");
        f.setDbName("ORG_CONTRACT");
        f.setLabel("PCMWPMOBJSTRUCTITEMORGCONTRACT: Site");
        f.setSize(5);
        f.setReadOnly();

        f = itemblk.addField("ITEM_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("PCMWPMOBJSTRUCTITEMORGCODE: Maint.Org.");
        f.setSize(8);
        f.setReadOnly();

        f = itemblk.addField("ITEM_DESCRIPTION");
        f.setDbName("DESCRIPTION");
        f.setLabel("PCMWPMOBJSTRUCTITEMDESCRIPTION: Work Description");
        f.setSize(35);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_ROUNDDEF_ID");
        f.setDbName("ROUNDDEF_ID");
        f.setLabel("PCMWPMOBJSTRUCTITEMROUNDDEFID: Route ID");
        f.setSize(8);
        f.setReadOnly();

        f = itemblk.addField("ITEM_STATE");
        f.setDbName("STATE");
        f.setLabel("PCMWPMOBJSTRUCTITEMSTATE: State");
        f.setSize(7);
        f.setReadOnly();

        f = itemblk.addField("ITEM_PM_TYPE_DB");
        f.setDbName("PM_TYPE_DB");
        f.setLabel("PCMWPMOBJSTRUCTITEMPMTYPEDB: PM Type Db");
        f.setHidden();

        f = itemblk.addField("ITEM_WORK_TYPE_ID");
        f.setDbName("WORK_TYPE_ID");
        f.setLabel("PCMWPMOBJSTRUCTITEMWORKTYPEID: Work Type");
        f.setSize(10);
        f.setReadOnly();

        f = itemblk.addField("ITEM_WORK_TYPE_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTITEMWORKTYPEDESCR: Description");
        f.setFunction("WORK_TYPE_API.Get_Description(:ITEM_WORK_TYPE_ID)");
        f.setSize(10);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_TEST_POINT_ID");
        f.setDbName("TEST_POINT_ID");
        f.setLabel("PCMWPMOBJSTRUCTITEMTESTPOINTID: Testpoint");
        f.setSize(6);
        f.setReadOnly();

        f = itemblk.addField("ITEM_TEST_POINT_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTITEMTESTPOINTDESCR: Description");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:ITEM_MCH_CODE_CONTRACT,:ITEM_MCH_CODE,:ITEM_TEST_POINT_ID)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_LOCATION");
        f.setLabel("PCMWPMOBJSTRUCTITEMLOCATION: Location");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:ITEM_MCH_CODE_CONTRACT,:ITEM_MCH_CODE,:ITEM_TEST_POINT_ID)");
        f.setReadOnly();

        f = itemblk.addField("ITEM_OP_STATUS_ID");
        f.setDbName("OP_STATUS_ID");
        f.setLabel("PCMWPMOBJSTRUCTITEMOPSTATUSID: Operational Status");
        f.setSize(3);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("ITEM_OP_DESCR");
        f.setLabel("PCMWPMOBJSTRUCTITEMOPDESCR: Description");
        f.setFunction("OPERATIONAL_STATUS_API.Get_Description(:ITEM_OP_STATUS_ID)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_MAINT_EMP_SIGN");
        f.setDbName("MAINT_EMP_SIGN");
        f.setLabel("PCMWPMOBJSTRUCTITEMMAINTEMPSIGN: Executed By");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_SIGNATURE");
        f.setDbName("SIGNATURE");
        f.setLabel("PCMWPMOBJSTRUCTITEMSIGNATURE: Planned By");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk.addField("ITEM_VENDOR_NO");
        f.setDbName("VENDOR_NO");
        f.setSize(12);
        f.setLabel("PCMWPMOBJSTRUCTITEMVENDNO: Contractor");
        f.setReadOnly();

        f = itemblk.addField("ITEM_VENDORNAME");
        f.setSize(50);
        f.setLabel("PCMWPMOBJSTRUCTITEMVENDNAME: Contractor Name");
        f.setFunction("Maintenance_Supplier_API.Get_Description(:ITEM_VENDOR_NO)");
        f.setReadOnly();

        f = itemblk.addField("ITEM_MATERIALS");
        f.setDbName("MATERIALS");
        f.setSize(70);
        f.setLabel("PCMWPMOBJSTRUCTITEMMATERIALS: Material");
        f.setMaxLength(2000);
        f.setReadOnly();

        itemblk.setView("PM_ACTION");
        itemblk.setTitle(mgr.translate("PCMWPMOBJSTRUCTPMACTIONITEMTITLE: Object Structure:"));
        itemset = itemblk.getASPRowSet();

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.disableModeLabel();
        itembar.enableMultirowAction();

        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM");
        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.OKFIND,"okFindITEM");

        itembar.addCustomCommand("separatePMItem",mgr.translate("PCMWPMOBJSTRUCTPMACTIONSEPERATEPM: Separate PM Action..."));
        itembar.addCustomCommand("roundPMItem",mgr.translate("PCMWPMOBJSTRUCTPMACTIONROUNDPM: Round PM Action..."));
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand("addJobProgramToObjectITEM",mgr.translate("PCMWPMOBJSTRUCTPMACTADDJOB: Add Job Program to Object..."));

        itembar.addCommandValidConditions("addJobProgramToObjectITEM","OBJSTATE","Enable","Preliminary;Active");
        itembar.addCommandValidConditions("separatePMItem","PM_TYPE_DB","Enable","ActiveSeparate");
        itembar.addCommandValidConditions("roundPMItem","PM_TYPE_DB","Enable","ActiveRound");

        itembar.removeFromMultirowAction("addJobProgramToObjectITEM");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();
        itemtbl.enableRowSelect();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
        itemlay.setDialogColumns(2);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
            return "PMOBJSTRUCTPMACTIONTITLE: PM Actions ";
    }

    protected String getTitle()
    {
            return "PMOBJSTRUCTPMACTIONTITLE: PM Actions ";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if ("1".equals(show))
        {
                appendToHTML(headlay.show());
                appendToHTML("<br>");
                appendToHTML(itemlay.show());
                appendToHTML("<br>");
        }

        if (bOpenNewWindow)
        {
                appendDirtyJavaScript("  window.open(\"");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));   //XSS_Safe AMNILK 20070725
                appendDirtyJavaScript("\", \"");
                appendDirtyJavaScript(newWinHandle);
                appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }

        appendDirtyJavaScript("function CCA(CB,row_no)\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("     cca = true;\n");
        appendDirtyJavaScript("     if (CB.checked)\n");
        appendDirtyJavaScript("              highLight(CB,row_no);\n");
        appendDirtyJavaScript("     else\n");
        appendDirtyJavaScript("              downLight(CB,row_no);\n");
        appendDirtyJavaScript("     if (CB.name == '__SELECTED1' && " + tot_item_rows + ">0)\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("         for (var i = 0; i <" + tot_item_rows + "; i++)\n");
//      Bug 71027, Start
        appendDirtyJavaScript("					if (" + tot_item_rows + " == 1){\n");
        appendDirtyJavaScript("						if (f.__SELECTED2.checked)\n");
        appendDirtyJavaScript("							f.__SELECTED2.checked = false;\n"); 
        appendDirtyJavaScript("					}\n");
        appendDirtyJavaScript("					else{\n");
        appendDirtyJavaScript("						if (f.__SELECTED2[i].checked)\n");
        appendDirtyJavaScript("							f.__SELECTED2[i].checked = false;\n"); 
        appendDirtyJavaScript("					}\n");
//      Bug 71027, End
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("     else if (CB.name == '__SELECTED2' && " + tot_head_rows + ">0)\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("         for (var i = 0; i <" + tot_head_rows + "; i++)\n");
//      Bug 71027, Start
        appendDirtyJavaScript("					if (" + tot_head_rows + "== 1){\n");
        appendDirtyJavaScript("						if (f.__SELECTED1.checked)\n");
        appendDirtyJavaScript("							f.__SELECTED1.checked = false;\n"); 
        appendDirtyJavaScript("					}\n");
        appendDirtyJavaScript("					else{\n");
        appendDirtyJavaScript("						if (f.__SELECTED1[i].checked)\n");
        appendDirtyJavaScript("							f.__SELECTED1[i].checked = false;\n"); 
        appendDirtyJavaScript("					}\n");
//      Bug 71027, End
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("} \n");
    }
}
