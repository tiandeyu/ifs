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
 *  File         : SystemEvent.java
 *  Description  : Posting Events and Transaction codes.
 *  Notes        :
 * ----------------------------------------------------------------------------
 *  Modified     :
 *
 *   SenSlk   18-Apr-2007   Created.
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SystemEvent extends ASPPageProvider
{

    //===============================================================
    // Static constants
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.SystemEvent");

    //===============================================================
    // Instances created on page creation (immutable attributes)
    //===============================================================
    private ASPTabContainer tabs;

    private ASPBlock       headblk;
    private ASPRowSet      headset;
    private ASPCommandBar  headbar;
    private ASPTable       headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock       itemblk0;
    private ASPRowSet      itemset0;
    private ASPCommandBar  itembar0;
    private ASPTable       itemtbl0;
    private ASPBlockLayout itemlay0;

    private ASPBlock       itemblk1;
    private ASPRowSet      itemset1;
    private ASPCommandBar  itembar1;
    private ASPTable       itemtbl1;
    private ASPBlockLayout itemlay1;

    private ASPBlock       itemblk2;
    private ASPRowSet      itemset2;
    private ASPCommandBar  itembar2;
    private ASPTable       itemtbl2;
    private ASPBlockLayout itemlay2;

    
    //===============================================================
    // Construction
    //===============================================================
    public SystemEvent(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run()
    {
        ASPManager mgr = getASPManager();

        if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
        {
            okFind();
            okFindITEM0();
            okFindITEM1();
        }
        else if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());
        else if ( mgr.dataTransfered() )
        {
            okFind();
            okFindITEM0();
            okFindITEM1();
        }

        adjust();
        tabs.saveActiveTab();
    }


    //=============================================================================
    //  Command Bar Search Group functions
    //=============================================================================

    public void  countFind()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getValue("N")));
        headset.clear();
    }

    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if ( headset.countRows() == 0 )
        {
            mgr.showAlert(mgr.translate("MPCCOWSYSEVENTNODATA: No data found."));
            headset.clear();
        }
        mgr.createSearchURL(headblk);
        eval(headset.syncItemSets());
    }

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;
        int currow = 0;

        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("TRANSACTION_CODE = ? ");
        q.addParameter("TRANSACTION_CODE",headset.getValue("SYSTEM_EVENT_ID"));
        q.includeMeta("ALL");

        currow  = headset.getCurrentRowNo();
        mgr.querySubmit(trans,itemblk0);
        headset.goTo(currow);

	if(itemset0.countRows()==0)
	{
	   tabs.setTabEnabled(1, false);
	   tabs.setActiveTab(2);
	}

    }

    public void  okFindITEM1()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;
        int currow = 0;

        q = trans.addEmptyQuery(itemblk1);
        q.addWhereCondition("EVENT_CODE = ? ");
        q.addParameter("EVENT_CODE",headset.getValue("SYSTEM_EVENT_ID"));
        q.includeMeta("ALL");

        currow  = headset.getCurrentRowNo();
        mgr.querySubmit(trans,itemblk1);
        headset.goTo(currow);

	if (itemset1.countRows()==0) 
	{
	   tabs.setTabEnabled(2, false);
	   tabs.setActiveTab(1);
	}

    }

    public void  okFindITEM2()
    {
        if(itemset1.countRows()>0)
	{
	    ASPManager mgr = getASPManager();
	    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    ASPQuery q;
	    int nCurRow = 0;
    
	    
	    q = trans.addQuery(itemblk2);
    
	    nCurRow = headset.getCurrentRowNo();
	    
	    q.addWhereCondition("EVENT_CODE = ?");
	    q.addParameter("EVENT_CODE", itemset1.getValue("EVENT_CODE"));
	    q.includeMeta("ALL");
	    mgr.querySubmit(trans,itemblk2);
    
	    headset.goTo(nCurRow);
	}
    }

    public void  activateTransactionInfo()
    {
      tabs.setActiveTab(1);
    }

    public void  activatePostingInfo()
    {
      tabs.setActiveTab(2);
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

	headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField( "SYSTEM_EVENT_ID" ).
	setInsertable().
	setReadOnly().
        setLabel("MPCCOWSYSEVENTSYSTEMEVENTID: System Event ID");

        headblk.addField( "DESCRIPTION" ).
	setInsertable().
	setLabel("MPCCOWSYSEVENTDESCRIPTION: Description");

        headblk.setView("MPCCOM_SYSTEM_EVENT");
        headblk.defineCommand("MPCCOM_SYSTEM_EVENT_API","New__,Modify__,Remove__");
	
	headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
        headlay.unsetAutoLayoutSelect();

        headset = headblk.getASPRowSet();
        
        headtbl = mgr.newASPTable( headblk );
        headtbl.setTitle(mgr.translate("MPCCOWSYSEVENTTITLE: Inventory and Distribution System Event"));

        headbar = mgr.newASPCommandBar(headblk);
	headbar.addCustomCommand("activateTransactionInfo","Transaction Info");
	headbar.addCustomCommand("activatePostingInfo","Posting Info");
       

        //------------------- Transaction Info TAB ------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

	itemblk0.addField("ITEM0_OBJID").
	setDbName("OBJID").
        setHidden();

        itemblk0.addField("ITEM0_OBJVERSION").
	setDbName("OBJVERSION").
        setHidden();

        itemblk0.addField("TRANSACTION_CODE").
        setMandatory().
        setSize(10).
        setMaxLength(10).
        setInsertable().
	setHidden().
        setUpperCase();

        itemblk0.addField("DIRECTION").
        setLabel("MPCCOWSYSEVENTDIRECTION: Direction").
        setMaxLength(1).
	setInsertable();
        
	itemblk0.addField( "TRANSIT_QTY_DIRECTION" ).
	setLabel("MPCCOWSYSEVENTTRANSQTYDIRECTION: Transit Qty Direction");

        itemblk0.addField( "INVENTORY_STAT_DIRECTION" ).
        setLabel("MPCCOWSYSEVENTINVENTSTATDIRECTION: Inventory Stat Direction");

        itemblk0.addField("INTRASTAT_DIRECTION").
        setLabel("MPCCOWSYSEVENTINTRASTATDIRECTION: Intrastat Direction");
        
        itemblk0.addField("SOURCE_APPLICATION").
        setLabel("MPCCOWSYSEVENTSOURCEAPPLICATION: Source Application");

        itemblk0.addField("ORDER_TYPE").
        setLabel("MPCCOWSYSEVENTORDERTYPE: Order Type");

        itemblk0.addField("TRANSACTION_SOURCE").
        setLabel("MPCCOWSYSEVENTTRANSACTIONSOURCE: Transaction Source");

        itemblk0.addField("CORRESPONDING_TRANSACTION").
        setLabel("MPCCOWSYSEVENTCORRESPONDINGTRANSACTION: Corresponding Transaction");

        itemblk0.addField("CORR_TRANS_DESC").
        setLabel("MPCCOWSYSEVENTCORRTRANSDESC: Corresponding Transaction Description").
	setFunction("Mpccom_System_Event_API.Get_Description(:CORRESPONDING_TRANSACTION)");

	itemblk0.addField("NOTC").
        setLabel("MPCCOWSYSEVENTNOTC: NOTC");

	itemblk0.addField("NOTC_DESC").
        setLabel("MPCCOWSYSEVENTNOTCDESC: NOTC Desc").
	setFunction("Notc_API.Get_Description(:NOTC)");

	itemblk0.addField("TRANS_BASED_REVAL_GROUP").
        setLabel("MPCCOWSYSEVENTTRANSBASEDREVALGRP: Trans Based Reval Group");

	itemblk0.addField("PART_TRACING").
        setLabel("MPCCOWSYSEVENTPARTTRACING: Part Tracing");
        
	itemblk0.addField("ACTUAL_COST_RECEIPT").
        setLabel("MPCCOWSYSEVENTACTUALCOSTRECEIPT: Actual Cost Receipt");

	itemblk0.addField("COST_SOURCE").
        setLabel("MPCCOWSYSEVENTCOSTSOURCE: Cost Source");

	itemblk0.addField("SUPPLIER_LOANED_STOCK_DB").
        setLabel("MPCCOWSYSEVENTSUPPLOANEDSTKDB: Supplier Loaned Stock Allowed").
	setCheckBox("FALSE,TRUE");

	itemblk0.addField("CUSTOMER_OWNED_STOCK_DB").
        setLabel("MPCCOWSYSEVENTCUSTOWNEDSTKDB: Customer Owned Stock Allowed").
	setCheckBox("FALSE,TRUE");

	itemblk0.addField("CONSIGNMENT_STOCK").
        setLabel("MPCCOWSYSEVENTCONSIGNMENTSTOCK: Consignment Stock");

        itemblk0.setView("MPCCOM_TRANSACTION_CODE");
        itemblk0.defineCommand("MPCCOM_TRANSACTION_CODE_API","Modify__");
        itemblk0.setMasterBlock(headblk);

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT);
        itemlay0.unsetAutoLayoutSelect();

        itemset0 = itemblk0.getASPRowSet();
        itembar0 = mgr.newASPCommandBar(itemblk0);

        itemtbl0 = mgr.newASPTable( itemblk0 );
        itemtbl0.setTitle(mgr.translate("MPCCOWSYSEVENTTRANSACTIONTAB: Transaction Info"));

	itembar0.disableCommand(itembar0.BACK);

        //------------------- Posting Info TAB ------------------------------

	itemblk1 = mgr.newASPBlock("ITEM1");

        itemblk1.addField("ITEM1_OBJID").
	setDbName("OBJID").
        setHidden();

        itemblk1.addField("ITEM1_OBJVERSION").
	setDbName("OBJVERSION").
        setHidden();
	
	itemblk1.addField("EVENT_CODE").
	setLabel("MPCCOWPOSTINGEVENTSEVENTCODE: Posting Event Code").
	setMandatory().
	setUpperCase().
	setReadOnly().
	setHidden().
	setSize(30).
	setMaxLength(10).
	setInsertable();
        
	itemblk1.addField("ITEM1_DESCRIPTION").
	setDbName("DESCRIPTION").
	setLabel("MPCCOWPOSTINGEVENTSEVENTCODEDESC: Posting Event Description").
        setMandatory().
	setHidden().
        setSize(50).
	setMaxLength(100);


	itemblk1.addField("ONLINE_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSONLINEFLAGDB: Posting Online").
        setCheckBox("N,Y");

        itemblk1.addField("MATERIAL_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMATERIALADDITIONFLAGDB: Material Addition").
	setMandatory().
        setCheckBox("N,Y");

	itemblk1.addField("OH1_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH1BURDENFLAGDB: Overhead Cost 1").
        setCheckBox("N,Y");

	itemblk1.addField("OH2_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH2BURDENFLAGDB: Overhead Cost 2").
        setCheckBox("N,Y");

	itemblk1.addField("SALES_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSSALESOVERHEADFLAGDB: Sales Overhead").
        setCheckBox("FALSE,TRUE");

	itemblk1.addField("MS_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMSADDITIONFLAGDB: Administration Overhead").
        setCheckBox("N,Y");

	itemblk1.addField("LABOR_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWSPOSTEVENTSLABOROVERHEADFLAGDB: Default Labor Overhead").
        setCheckBox("N,Y");

	itemblk1.addField("GENERAL_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSGENERALOVERHEADFLAGDB: General Overhead").
        setCheckBox("N,Y");

	itemblk1.addField("DELIVERY_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSDELIVERYOVERHEADFLAGDB: Delivery Overhead").
        setCheckBox("N,Y");


	itemblk1.setView("ACCOUNTING_EVENT");
        itemblk1.defineCommand("ACCOUNTING_EVENT_API","Modify__");
	itemblk1.setMasterBlock(headblk);

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
	
        
        itemset1 = itemblk1.getASPRowSet();
        
        itemtbl1 = mgr.newASPTable( itemblk1 );
        itemtbl1.setTitle(mgr.translate("MPCCOWPOSTEVENTS: Posting Events"));

        itembar1 = mgr.newASPCommandBar(itemblk1);

	itembar1.disableCommand(itembar1.BACK);
        

        // ----------------- Item Block ------------------------------------------

	itemblk2 = mgr.newASPBlock("ITEM2");

        itemblk2.addField( "ITEM2_OBJID" ).
	setDbName("OBJID").
        setHidden();

        itemblk2.addField( "ITEM2_OBJVERSION" ).
	setDbName("OBJVERSION").
        setHidden();

        itemblk2.addField("ITEM_EVENT_CODE").
        setDbName("EVENT_CODE").
        setHidden();

	itemblk2.addField("STR_CODE").
        setLabel("MPCCOWPOSTEVENTSSTRCODE: Posting Type").
        setSize(50).
        setReadOnly();
        
        itemblk2.addField("ITEM_STR_CODE_DESC").
	setLabel("MPCCOWPOSTEVENTSITEMSTRCODEDESC: Posting Type Description").
	setFunction("Posting_Ctrl_API.Get_Posting_Type_Desc(SITE_API.GET_COMPANY(USER_ALLOWED_SITE_API.Get_Default_Site),STR_CODE)").
	setReadOnly().
        setSize(50);

	itemblk2.addField("DEBIT_CREDIT").
	setLabel("MPCCOWPOSTEVENTSDEBITCREDIT: Debit/Credit").
        setSize(50).
        setReadOnly();

	itemblk2.addField("PRE_ACCOUNTING_FLAG").
	setLabel("MPCCOWPOSTEVENTSPREACCOUNTINGFLAG: Pre Posting Flag").
        setSize(50).
        setReadOnly();

	itemblk2.addField("PROJECT_ACCOUNTING_FLAG").
	setLabel("MPCCOWPOSTEVENTSPROJECTACCOUNTINGFLAG: Project Posting Flag").
        setSize(50).
        setReadOnly();

	itemblk2.setView("ACC_EVENT_POSTING_TYPE");
        
        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
        itemblk2.setMasterBlock(itemblk1);

        itemset2 = itemblk2.getASPRowSet();
        
        itemtbl2 = mgr.newASPTable( itemblk2 );
        itemtbl2.setTitle(mgr.translate("MPCCOWSYSEVENTPOSTINGTAB: Posting Info"));

        itembar2 = mgr.newASPCommandBar(itemblk2);


        //--------------------------------------------------------------------------------------------
        // Tabs
        //--------------------------------------------------------------------------------------------

        tabs = mgr.newASPTabContainer();
        tabs.setDirtyFlagEnabled(false);

        tabs.addTab("MPCCOWSYSEVENTTRANSINFOTAB: Transaction Info","javascript:commandSet('HEAD.activateTransactionInfo','')");
        tabs.addTab("MPCCOWSYSEVENTPOSTINGINFOTAB: Posting Info","javascript:commandSet('HEAD.activatePostingInfo','')");

        tabs.setContainerWidth(1000);
        tabs.setContainerHeight(400);
        tabs.setLeftTabSpace(3);
        tabs.setContainerSpace(6);
        tabs.setTabWidth(100);
    }

    public void  adjust()
    {
       
    }


    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
        return "MPCCOWSYSEVENTTITLE: Inventory and Distribution System Event";
    }

    protected String getTitle()
    {
        return "MPCCOWSYSEVENTTITLE: Inventory and Distribution System Event";
    }

    public void printContents() throws FndException
    {
      int activetab = tabs.getActiveTab();
      
      ASPManager mgr = getASPManager();
      
      if (headlay.isVisible()) 
      {
         appendToHTML(headlay.show());
      }

      if (headset.countRows()>0) 
      {
         if (headlay.isSingleLayout()) 
	 {
            if (headlay.isVisible()) 
	    {
               appendToHTML(tabs.showTabsInit());
            }
            if (activetab == 1) 
	    {
               appendToHTML(itemlay0.show());
            }
            if (activetab == 2) 
	    {
               appendToHTML(itemlay1.show());
	       appendToHTML(itemlay2.show());
            }
            if (headlay.isVisible()) 
	    {
               appendToHTML(tabs.showTabsFinish());
            }
         }
      }
   }
}
