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
*  File        : WoTimeTransactionHist.java 
*  Created     : 050816   NIJALK   (AMAD112 - Overhead and Other Rates)
*  Modified    : 
*  051114   NIJALK   Modified preDefine().
*  060126   THWILK   Corrected localization errors.
*  060201   NIJALK   Call 132475: Modified run(),okFind().
*  060215   NIJALK   Call 134216: Changed default OrderBy.
*  060308   NIJALK   Call 136639: Moved fields in ITEMBLK0 to HEADBLK and removed ITEMBLK0.
*  060731   AMNILK   Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  060807   AMDILK   Merged with Bug Id 58214
*  060818   AMNILK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060906   AMDILK   Merged with the Bug ID 58216
*  061110   AMNILK   MEPR604: Original Source info in voucher rows.Modified workOrder(),preDefine() & adjust().
*  080202   NIJALK   Bug 66456, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class WoTimeTransactionHist extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WoTimeTransactionHist");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;

    private ASPField f;
    private ASPTabContainer tabs;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private ASPTransactionBuffer trans;
    private ASPQuery q;
    private ASPCommand cmd;
    private ASPBuffer buf;
    private ASPBuffer row;

    private boolean bOpenNewWindow;

    private int newWinHeight = 600;
    private int newWinWidth = 900;

    private String urlString;
    private String newWinHandle;
    private String sTransId;

    //===============================================================
    // Construction 
    //===============================================================
    public WoTimeTransactionHist(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        sTransId = "";

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
        }
        else if (mgr.dataTransfered())
        {   
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("TRANSACTION_ID")) && ("Y".equals(mgr.getQueryStringValue("SEARCHME"))))
        {
            if ("M".equals(mgr.getQueryStringValue("COST_TYPE")))
            {
                // If Cost Type is "Material"
                buf = mgr.newASPBuffer();
                row = buf.addBuffer("0");
                row.addFieldItem("TRANSACTION_ID",mgr.getQueryStringValue("TRANSACTION_ID"));
                mgr.transferDataTo("../invenw/InventoryTransactionHist2Qry.page",buf); 
            }
            else
            {
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                okFind();   
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("TRANSACTION_ID")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            sTransId = mgr.getQueryStringValue("TRANSACTION_ID");
            okFind();   
        }

        adjust();

        tabs.saveActiveTab();
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

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
        if (!mgr.isEmpty(sTransId)){
	    q.addWhereCondition("TRANSACTION_ID = ?");
	    q.addParameter("DIRECTION",sTransId);
	}
        q.setOrderByClause("TRANSACTION_ID DESC");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);
                                              
        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM1(); 
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWOTIMETRANSACTIONHISTNODATA: No data found."));
            headset.clear();
        }
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();
        
        if (headset.countRows()>0)
        {
            int headrowno = headset.getCurrentRowNo();

            trans.clear();
            q = trans.addQuery(itemblk1);
            q.addWhereCondition("TRANSACTION_ID = ?");
	    q.addParameter("TRANSACTION_ID",headset.getRow().getValue("TRANSACTION_ID"));
	    q.includeMeta("ALL");
            mgr.querySubmit(trans, itemblk1);

            headset.goTo(headrowno);
        }
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        if (mgr.dataTransfered())
                q.addOrCondition(mgr.getTransferedData());
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }     

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("TRANSACTION_ID = ?");
	q.addParameter("TRANSACTION_ID",headset.getRow().getValue("TRANSACTION_ID"));
	mgr.submit(trans);

        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
        headset.goTo(headrowno);
    }     
//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
    public void workOrder()
    {
        ASPManager mgr = getASPManager();
        String newWin = "";
        String woType = "";

        if (headset.countRows()>0)
        {
            woType = headset.getRow().getValue("WO_TYPE");
	    if ("ActiveSeparate".equals(woType))
		newWin = "WorkOrderCoding1";
	    else if ("ActiveRound".equals(woType))
		newWin = "ActiveRound";
	    else if ("HistoricalActiveSeparate".equals(woType))
		newWin = "HistoricalSeparateRMB";
	    else if ("HistoricalActiveRound".equals(woType))
		newWin = "HistoricalRound";
        }  

        if (!mgr.isEmpty(newWin))
        {
            bOpenNewWindow = true;
		urlString = newWin+".page?WO_NO="+ mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                                "&FRMNAME=WoTimeTransactionHistory"+"&IS_POSTING_ACTIVATE=true";
            newWinHandle = newWin;
        }

    }

    public void setCodePartLabels()
    {
        ASPManager mgr = getASPManager();
        String company = "";

        if (headset.countRows()>0)
            company = headset.getRow().getValue("COMPANY");
        
        trans.clear();

        //Fetching names for Code Parts A to J
        cmd = trans.addCustomFunction("GETCODENAMEA","Accounting_Code_Parts_API.Get_Name","ACCOUNT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("ACCOUNT_NO","A");

        cmd = trans.addCustomFunction("GETCODENAMEB","Accounting_Code_Parts_API.Get_Name","COST_CENTER");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("COST_CENTER","B");

        cmd = trans.addCustomFunction("GETCODENAMEC","Accounting_Code_Parts_API.Get_Name","CODE_C");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_C","C");

        cmd = trans.addCustomFunction("GETCODENAMED","Accounting_Code_Parts_API.Get_Name","CODE_D");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_D","D");

        cmd = trans.addCustomFunction("GETCODENAMEE","Accounting_Code_Parts_API.Get_Name","OBJECT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("OBJECT_NO","E");

        cmd = trans.addCustomFunction("GETCODENAMEF","Accounting_Code_Parts_API.Get_Name","PROJECT_NO");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("PROJECT_NO","F");

        cmd = trans.addCustomFunction("GETCODENAMEG","Accounting_Code_Parts_API.Get_Name","CODE_G");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_G","G");

        cmd = trans.addCustomFunction("GETCODENAMEH","Accounting_Code_Parts_API.Get_Name","CODE_H");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_H","H");

        cmd = trans.addCustomFunction("GETCODENAMEI","Accounting_Code_Parts_API.Get_Name","CODE_I");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_I","I");

        cmd = trans.addCustomFunction("GETCODENAMEJ","Accounting_Code_Parts_API.Get_Name","CODE_J");
        cmd.addParameter("COMPANY",company);
        cmd.addParameter("CODE_J","J");

        trans = mgr.perform(trans);

        String sLabelA = trans.getValue("GETCODENAMEA/DATA/ACCOUNT_NO");
        mgr.getASPField("ACCOUNT_NO").setLabel(mgr.isEmpty(sLabelA)?"@A":sLabelA);

        String sLabelB = trans.getValue("GETCODENAMEB/DATA/COST_CENTER");
        mgr.getASPField("COST_CENTER").setLabel(mgr.isEmpty(sLabelB)?"@B":sLabelB);

        String sLabelC = trans.getValue("GETCODENAMEC/DATA/CODE_C");
        mgr.getASPField("CODE_C").setLabel(mgr.isEmpty(sLabelC)?"@C":sLabelC);

        String sLabelD = trans.getValue("GETCODENAMED/DATA/CODE_D");
        mgr.getASPField("CODE_D").setLabel(mgr.isEmpty(sLabelD)?"@D":sLabelD);

        String sLabelE = trans.getValue("GETCODENAMEE/DATA/OBJECT_NO");
        mgr.getASPField("OBJECT_NO").setLabel(mgr.isEmpty(sLabelE)?"@E":sLabelE);

        String sLabelF = trans.getValue("GETCODENAMEF/DATA/PROJECT_NO");
        mgr.getASPField("PROJECT_NO").setLabel(mgr.isEmpty(sLabelF)?"@F":sLabelF);

        String sLabelG = trans.getValue("GETCODENAMEG/DATA/CODE_G");
        mgr.getASPField("CODE_G").setLabel(mgr.isEmpty(sLabelG)?"@G":sLabelG);

        String sLabelH = trans.getValue("GETCODENAMEH/DATA/CODE_H");
        mgr.getASPField("CODE_H").setLabel(mgr.isEmpty(sLabelH)?"@H":sLabelH);

        String sLabelI = trans.getValue("GETCODENAMEI/DATA/CODE_I");
        mgr.getASPField("CODE_I").setLabel(mgr.isEmpty(sLabelI)?"@I":sLabelI);

        String sLabelJ = trans.getValue("GETCODENAMEJ/DATA/CODE_J");
        mgr.getASPField("CODE_J").setLabel(mgr.isEmpty(sLabelJ)?"@J":sLabelJ);
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("TRANSACTION_ID","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTTRANSACTIONID: Transaction ID");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("WO_NO","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTWONO: WO No");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(8);

        f = headblk.addField("ROW_NO","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTROWNO: Row No");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("CONTRACT");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCONTRACT: Site");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(5);

        f = headblk.addField("COMPANY");
        f.setFunction("Site_API.Get_Company(:CONTRACT)");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCOMPANY: Company");
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();

        f = headblk.addField("WORK_ORDER_COST_TYPE");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTWOCOSTTYPE: Cost Type");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(200);
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");

        f = headblk.addField("WO_TYPE");
        f.setFunction("Active_Work_Order_Api.Get_Work_Order_Type(:WO_NO)");
        f.setHidden();

        f = headblk.addField("DIRECTION");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTDIRECTION: Direction");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(1);
        f.setSize(1);

        f = headblk.addField("QUANTITY","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTQUANTITY: Quantity");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("UNIT");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTUNIT: Unit");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setMaxLength(30);
        f.setSize(3);
        f.setDynamicLOV("ISO_UNIT",600,450);
        f.setFunction("Iso_Unit_API.Get_Description(UNIT_CODE, Language_SYS.Get_Language)");

        f = headblk.addField("USER_ID");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTUSERID: User");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(30);
        f.setDynamicLOV("FND_USER",600,450);

        f = headblk.addField("COST","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCOST: Cost/Unit");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("TRANS_DATE","Datetime");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTDATE: Date");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("OVERHEAD","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTOVERHEAD: Overhead");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = headblk.addField("PROJECT_ID");
            f.setLabel("PCMWWOTIMETRANSACTIONHISTPROJECTID: Project ID");
            f.setInsertable();
            f.setReadOnly();
            f.setQueryable();
            f.setUpperCase();
            f.setMaxLength(10);
            f.setDynamicLOV("PROJECT",600,450);
        }
        //Bug 66456, End
        
        f = headblk.addField("TOTAL_COST","Number");    
        f.setLabel("PCMWWOTIMETRANSACTIONHISTTOTALCOST: Total Cost");
        f.setReadOnly();
        f.setFunction("(NVL(QUANTITY*(DECODE(DIRECTION,'+',1,'-',-1)),0)*NVL(COST,0))+NVL(OVERHEAD,0)");
        f.unsetQueryable();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = headblk.addField("ACTIVITY_SEQ","Number");
            f.setLabel("PCMWWOTIMETRANSACTIONHISTACTIVITYSEQUENCE: Activity Sequence");
            f.setInsertable();
            f.setReadOnly();
            f.setQueryable();
            f.setDynamicLOV("ACTIVITY","PROJECT_ID",600,450);
        }
        //Bug 66456, End

        headblk.setView("WO_TIME_TRANSACTION_HIST");
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();
        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("activateAccounting", "");
        headbar.addCustomCommand("workOrder",mgr.translate("PCMWWOTIMETRANSACTIONHISTRMBWO: Work Order..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWWOTIMETRANSACTIONHISTACCOUNTINGTAB: Accounting"), "javascript:commandSet('HEAD.activateAccounting','')");

        //-----------------------------------------------------------------------------------------------
        //------------------- Accounting Tab ---------------------------------------------------------------
        //-----------------------------------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk1.addField("ITEM1_TRANSACTION_ID","Number");
        f.setDbName("TRANSACTION_ID");
        f.setHidden();
        f.setDynamicLOV("WO_TIME_TRANSACTION_HIST",600,450);

        f = itemblk1.addField("SEQ","Number");
        f.setHidden();

        f = itemblk1.addField("DEBIT_VALUE","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTDEBITVALUE: Debit Amount");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = itemblk1.addField("CREDIT_VALUE","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCREDITVALUE: Credit Amount");
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = itemblk1.addField("ACCOUNT_NO");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTACCOUNTNO: @A");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setQueryable();

        f = itemblk1.addField("STR_CODE");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTSTRCODE: Posting Type");
        f.setMandatory();
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(10);
        f.setDynamicLOV("POSTING_CTRL_POSTING_TYPE");

        f = itemblk1.addField("STR_CODE_DESC");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTSTRCODEDESC: Posting Type Description");
        f.setInsertable();
        f.setReadOnly();
        f.setFunction("Posting_Ctrl_Posting_Type_API.Get_Description(:STR_CODE)");

        f = itemblk1.addField("COST_CENTER");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCOSTCENTER: @B");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(10);

        f = itemblk1.addField("PROJECT_NO");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTPROJECTNO: @F");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(10);

        f = itemblk1.addField("OBJECT_NO");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTOBJECTNO: @E");
        f.setQueryable();
        f.setUpperCase();
        f.setMaxLength(10);

        f = itemblk1.addField("STATE");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTSTATE: Status");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(4000);

        f = itemblk1.addField("ERROR_DESC");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTERRORDESC: Error Description");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(100);

        f = itemblk1.addField("CODE_C");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODEC: @C");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        f = itemblk1.addField("CODE_D");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODED: @D");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        f = itemblk1.addField("CODE_G");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODEG: @G");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        f = itemblk1.addField("CODE_H");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODEH: @H");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        f = itemblk1.addField("CODE_I");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODEI: @I");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        f = itemblk1.addField("CODE_J");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTCODEJ: @J");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setUpperCase();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = itemblk1.addField("ITEM1_ACTIVITY_SEQ","Number");
            f.setDbName("ACTIVITY_SEQ");
            f.setLabel("PCMWWOTIMETRANSACTIONHISTACTIVITYSEQ: Activity Seq");
            f.setQueryable();
            f.setInsertable();
            f.setReadOnly();
        }
        //Bug 66456, End

        f = itemblk1.addField("VOUCHER_NO","Number");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTVOUCHERNO: Voucher No");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk1.addField("VOUCHER_TYPE");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTVOUCHERTYPE: Voucher Type");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(3);

        f = itemblk1.addField("ACCOUNTING_YEAR");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTACCOUNTINGYEAR: Accounting Year");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(4);
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ACCOUNTING_PERIOD");
        f.setLabel("PCMWWOTIMETRANSACTIONHISTACCOUNTINGPERIOD: Accounting Period");
        f.setQueryable();
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(20);
        f.setUpperCase();
        f.setDefaultNotVisible();

        itemblk1.setView("PCM_ACCOUNTING");
        itemblk1.disableDocMan();
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();
        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();
        itembar1 = mgr.newASPCommandBar(itemblk1);

        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        itembar1.enableCommand(itembar1.FIND);

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
    }

    public void activateAccounting()
    {
        tabs.setActiveTab(1);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        headbar.removeCustomCommand("activateAccounting"); 
        // Set the Field Labels for code parts.
        setCodePartLabels();

        if (!headlay.isFindLayout())
        {
            mgr.getASPField("DIRECTION").setLabel("PCMWWOTIMETRANSACTIONHISTQUANTITY: Quantity");
            headlay.setSimple("QUANTITY");
            headlay.setSimple("UNIT");
        }
        else
        {
            mgr.getASPField("DIRECTION").setLabel("PCMWWOTIMETRANSACTIONHISTDIRECTION: Direction");
            headlay.unsetSimple("QUANTITY");
            headlay.unsetSimple("UNIT");
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWOTIMETRANSACTIONHISTTITLE: Work Order Transaction History";
    }

    protected String getTitle()
    {
        return "PCMWWOTIMETRANSACTIONHISTTITLE: Work Order Transaction History";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
                appendToHTML(itemlay1.show());
        }

        appendDirtyJavaScript("window.name = \"WoTimeTransactionHistory\"\n");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");      
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");  

        }
    }
}
