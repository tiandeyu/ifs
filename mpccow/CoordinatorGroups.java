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
 *  File         : CoordinatorGroups.java 
 *  Description  : CoordinatorGroups
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 *     NiDalk 22-Mar-2007 - Added fields ALLOCATION_ORDER_PREFIX and ALLOCATION_ORDER_NO.
 * ---------------------- Wings Merge End -------------------------------------
 *     ChJalk 30-Jan-2007 - Merged Wings Code.
 *     ChJalk 09-Nov-2006 - Created.
 * ---------------------- Wings Merge Start -----------------------------------
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CoordinatorGroups extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CoordinatorGroups");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPTransactionBuffer trans;

    //===============================================================
    // Construction 
    //===============================================================
    public CoordinatorGroups(ASPManager mgr, String page_path)
    { 
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

	if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
	{
	    okFind();
        }
        else if ( mgr.commandBarActivated() )
            eval(mgr.commandBarFunction());
        else if ( mgr.dataTransfered() )
        {
            okFind();
        }
        adjust();
    }

    public void  adjust()
    {
        if ( headset.countRows() == 0 )
        {
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.BACK);
            headbar.disableCommand(headbar.BACKWARD);
        }
    }

    //=============================================================================
    //  Command Bar Edit Group functions
    //=============================================================================

    public void  newRow()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPCommand cmd;
        ASPBuffer data;

        cmd = trans.addEmptyCommand("HEAD","ORDER_COORDINATOR_GROUP_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);

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

        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if ( headset.countRows() == 0 )
        {
            mgr.showAlert("MPCCOWCOORDINATOTGRPNODATA: No data found.");
            headset.clear();
        }
        
        eval(headset.syncItemSets());    
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField( "OBJID" ).
        setHidden();

        headblk.addField( "OBJVERSION" ).
        setHidden();

        headblk.addField("AUTHORIZE_GROUP").
	setLabel("MPCCOWCOORDINATORGRPAUTHORIZEGROUP: Coordinator Group Id").
	setMandatory().
	setUpperCase().
	setReadOnly().
	setSize(30).
	setMaxLength(1).
        setInsertable();
        
	headblk.addField("DESCRIPTION").
	setLabel("MPCCOWCOORDINATORGRPDESCRIPTION: Coordinator Group Description").
        setMandatory().
	setMaxLength(35).
        setSize(30);
        
        headblk.addField("ORDER_NO","Number").
        setLabel("MPCCOWCOORDINATORGRPORDERNO: Customer Order No").
        setSize(30).
	setMaxLength(12).
	setMandatory();

	headblk.addField("QUOTATION_NO","Number").
        setLabel("MPCCOWCOORDINATORGRPQUOTATIONNO: Sales Quotation No").
        setMandatory().
        setSize(30).
	setMaxLength(12);
        
	headblk.addField("PURCH_ORDER_PREFIX").
	setLabel("MPCCOWCOORDINATORGRPPURCHORDERPREFIX: Purchase Order Prefix").
        setUpperCase().
        setSize(30).
	setMaxLength(1);
        
	headblk.addField("PURCH_ORDER_NO","Number").
	setLabel("MPCCOWCOORDINATORGRPPURCHORDERNO: Purchase Order No").
        setMaxLength(11).
        setSize(30);
        
        headblk.addField("DIST_ORDER_PREFIX").
        setLabel("MPCCOWCOORDINATORGRPDISTORDERPREFIX: Distribution Order Prefix").
        setSize(30).
	setMaxLength(1);

	headblk.addField("DIST_ORDER_NO","Number").
        setLabel("MPCCOWCOORDINATORGRPDISTORDERNO: Distribution Order No").
        setSize(30).
        setMaxLength(10);

   headblk.addField("ALLOCATION_ORDER_PREFIX").
      setLabel("MPCCOWCOORDINATORGRPALLOCORDERPREFIX: Allocation Order Prefix").
      setSize(30).
      setMaxLength(1);

   headblk.addField("ALLOCATION_ORDER_NO","Number").
      setLabel("MPCCOWCOORDINATORGRPALLOCORDERNO: Allocation Order No").
      setSize(30).
      setMaxLength(10);

        
        headblk.setView("ORDER_COORDINATOR_GROUP");
        headblk.defineCommand("ORDER_COORDINATOR_GROUP_API","New__,Modify__,Remove__");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        
        headset = headblk.getASPRowSet();
        
        headtbl = mgr.newASPTable( headblk );
        headtbl.setTitle(mgr.translate("MPCCOWCOORDINATORGRP: Coordinator Groups"));

        headbar = mgr.newASPCommandBar(headblk);
        
    }

    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
        return "MPCCOWCOORDINATORGRPTITLE: Coordinator Groups";
    }

    protected String getTitle()
    {
        return "MPCCOWCOORDINATORGRPDESC: Coordinator Groups";
    }

    protected void printContents() throws FndException
    {
      if(headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
    }

}
