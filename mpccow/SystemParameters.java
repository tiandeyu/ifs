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
 *  File         : SystemParameters.java 
 *  Description  : SystemParameters
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 *     NaLrlk   2007-03-19 - Removed ALLOW_LINE_BACKORDERS form the addWhereCondition clause.
 * ---------------------- Wings Merge End--------------------------------------
 *     ChJalk   2007-01-30 - Merged Wings Code.
 *     ChJalk   2006-11-01 - Created.
 * ---------------------- Wings Merge Start------------------------------------
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SystemParameters extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.SystemParameters");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
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

    private ASPTransactionBuffer trans;

    //===============================================================
    // Construction 
    //===============================================================
    public SystemParameters(ASPManager mgr, String page_path)
    { 
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

	if ( !mgr.commandBarActivated() )
	{
	    okFind();
	    okFindITEM();
	    okFindITEM0();
	    okFindITEM1();
	}
        else if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());
        else if ( mgr.dataTransfered() )
        {
            okFind();
	    okFindITEM();
	    okFindITEM0();
	    okFindITEM1();
        }
    }

    //=============================================================================
    //  Command Bar Search Group functions
    //=============================================================================
    public void  okFind()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(headblk);

	q.addWhereCondition("PARAMETER_CODE IN ('PALLET_HANDLING','SO_CREATE_STATUS','SHORTAGE_HANDLING','TRANSACTIONS_PROJECT_TRANSFERS')");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if ( headset.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWUSYSPARAMNODATA: No data found.");
            headset.clear();
        }
        mgr.createSearchURL(headblk);
        eval(headset.syncItemSets());    
    }

    public void  okFindITEM()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(itemblk);

	q.addWhereCondition("PARAMETER_CODE = 'AUTO_AVAILABILITY_CHECK'");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk);

        if ( itemset.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWUSYSPARAMNODATA: No data found.");
            itemset.clear();
        }
        mgr.createSearchURL(itemblk);
        eval(itemset.syncItemSets());    
    }

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(itemblk0);

	q.addWhereCondition("PARAMETER_CODE = 'PRINT_CONTROL_CODE'");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk0);

        if ( itemset0.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWUSYSPARAMNODATA: No data found.");
            itemset0.clear();
        }
        mgr.createSearchURL(itemblk0);
        eval(itemset0.syncItemSets());    
    }


    public void  okFindITEM1()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPQuery q;

        q = trans.addQuery(itemblk1);

	q.addWhereCondition("PARAMETER_CODE IN ('DEFAULT_PLAN_DATA_PERIODS','LEADTIME_RECEIPTS','LEADTIME_FACTOR1','LEADTIME_FACTOR2')");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk1);

        if ( itemset1.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWUSYSPARAMNODATA: No data found.");
            itemset1.clear();
        }
        mgr.createSearchURL(itemblk1);
        eval(itemset1.syncItemSets());    
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField( "OBJID" ).
        setHidden();

        headblk.addField( "OBJVERSION" ).
        setHidden();

        headblk.addField("PARAMETER_CODE").
        setHidden();

	headblk.addField("DESCRIPTION").
        setMandatory().
        setSize(50).
        setReadOnly();
        
        headblk.addField("PARAMETER_VALUE1").
        setMandatory().
        enumerateValues("GEN_YES_NO_API").
        setSelectBox().
        setSize(50);

	headblk.setView("MPCCOM_SYSTEM_PARAMETER_GYN");
        headblk.defineCommand("MPCCOM_SYSTEM_PARAMETER_API","Modify__");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.unsetAutoLayoutSelect();

        headset = headblk.getASPRowSet();
        
        headtbl = mgr.newASPTable( headblk );
        headtbl.setTitle(mgr.translate("MPCCOWSYSPARAMETERS: System Parameters"));

        headbar = mgr.newASPCommandBar(headblk);
	headbar.disableCommand(headbar.FIND);

        headlay.setDialogColumns(2);

        headlay.setSimple("DESCRIPTION");
        headlay.setSimple("PARAMETER_VALUE1");
	
	

	itemblk = mgr.newASPBlock("ITEM");

        itemblk.addField( "ITEM_OBJID" ).
	setDbName("OBJID").
        setHidden();

        itemblk.addField( "ITEM_OBJVERSION" ).
	setDbName("OBJVERSION").
        setHidden();

        itemblk.addField("ITEM_PARAMETER_CODE").
        setDbName("PARAMETER_CODE").
        setHidden();

	itemblk.addField("ITEM_DESCRIPTION").
        setDbName("DESCRIPTION").
        setMandatory().
        setSize(50).
        setReadOnly();
        

        itemblk.addField("ITEM_PARAMETER_VALUE1").
	setDbName("PARAMETER_VALUE1").
	setMandatory().
        enumerateValues("GEN_YES_NO_ALLOWED_API").
        setSelectBox().
        setSize(50);

	itemblk.setView("MPCCOM_SYSTEM_PARAMETER_GYNA");
        itemblk.defineCommand("MPCCOM_SYSTEM_PARAMETER_API","Modify__");

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
        itemlay.unsetAutoLayoutSelect();

        itemset = itemblk.getASPRowSet();
        
        itemtbl = mgr.newASPTable( itemblk );
        itemtbl.setTitle(mgr.translate("MPCCOWSYSPARAMETERS: System Parameters"));

        itembar = mgr.newASPCommandBar(itemblk);
	itembar.disableCommand(itembar.FIND);
        
        itemlay.setDialogColumns(2);

        itemlay.setSimple("ITEM_DESCRIPTION");
        itemlay.setSimple("ITEM_PARAMETER_VALUE1");
	


	itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField( "ITEM0_OBJID" ).
	setDbName("OBJID").
        setHidden();

        itemblk0.addField( "ITEM0_OBJVERSION" ).
	setDbName("OBJVERSION").
        setHidden();

        itemblk0.addField("ITEM0_PARAMETER_CODE").
        setDbName("PARAMETER_CODE").
        setHidden();

	itemblk0.addField("ITEM0_DESCRIPTION").
        setDbName("DESCRIPTION").
        setMandatory().
        setSize(50).
        setReadOnly();
        
        itemblk0.addField("ITEM0_PARAMETER_VALUE1").
	setDbName("PARAMETER_VALUE1").
	setMandatory().
        setDynamicLOV("CUST_ORD_PRINT_CONTROL",650,445).
        setSize(50);

	itemblk0.setView("MPCCOM_SYSTEM_PARAMETER_COPC");
        itemblk0.defineCommand("MPCCOM_SYSTEM_PARAMETER_API","Modify__");

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.unsetAutoLayoutSelect();

        itemset0 = itemblk0.getASPRowSet();
        
        itemtbl0 = mgr.newASPTable( itemblk0 );
        itemtbl0.setTitle(mgr.translate("MPCCOWSYSPARAMETERS: System Parameters"));

        itembar0 = mgr.newASPCommandBar(itemblk0);
	itembar0.disableCommand(itembar0.FIND);
        
        itemlay0.setDialogColumns(2);

	itemlay0.setSimple("ITEM0_DESCRIPTION");
        itemlay0.setSimple("ITEM0_PARAMETER_VALUE1");

	

	itemblk1 = mgr.newASPBlock("ITEM1");

        itemblk1.addField( "ITEM1_OBJID" ).
	setDbName("OBJID").
        setHidden();

        itemblk1.addField( "ITEM1_OBJVERSION" ).
	setDbName("OBJVERSION").
        setHidden();

        itemblk1.addField("ITEM1_PARAMETER_CODE").
        setDbName("PARAMETER_CODE").
        setHidden();

	itemblk1.addField("ITEM1_DESCRIPTION").
        setDbName("DESCRIPTION").
        setMandatory().
        setSize(50).
        setReadOnly();
        
        itemblk1.addField("ITEM1_PARAMETER_VALUE1").
	setDbName("PARAMETER_VALUE1").
	setMandatory().
        setSize(50);

	itemblk1.setView("MPCCOM_SYSTEM_PARAMETER");
        itemblk1.defineCommand("MPCCOM_SYSTEM_PARAMETER_API","Modify__");

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.unsetAutoLayoutSelect();

        itemset1 = itemblk1.getASPRowSet();
        
        itemtbl1 = mgr.newASPTable( itemblk1 );
        itemtbl1.setTitle(mgr.translate("MPCCOWSYSPARAMETERS: System Parameters"));

        itembar1 = mgr.newASPCommandBar(itemblk1);
	itembar1.disableCommand(itembar1.FIND);
        
        itemlay1.setDialogColumns(2);

	itemlay1.setSimple("ITEM1_DESCRIPTION");
        itemlay1.setSimple("ITEM1_PARAMETER_VALUE1");
    }

    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
        return "MPCCOWSYSPARAMETERSDESC: System Parameters";
    }

    protected String getTitle()
    {
        return "MPCCOWSYSPARAMETERSTITLE: System Parameters";
    }

    protected void printContents() throws FndException
    {
      if(headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if(itemlay.isVisible() && itemset.countRows() >0)
      {
         appendToHTML(itemlay.show());
      }
      if(itemlay0.isVisible() && itemset0.countRows() >0)
      {
         appendToHTML(itemlay0.show());
      }
      if(itemlay1.isVisible() && itemset1.countRows() >0)
      {
         appendToHTML(itemlay1.show());
      }
    }

}
