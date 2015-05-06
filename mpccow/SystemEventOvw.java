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
*  File        : SystemEventOvw.java 
*  Modified    :
*	SenSlk	20-Apr-2007 Created. 
* ----------------------------------------------------------------------------
*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SystemEventOvw extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.SystemEventOvw");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPQuery qry;

   //===============================================================
   // Construction 
   //===============================================================

   public SystemEventOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();   

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("IDENTITY")))
         okFind();
    
      adjust();
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      String strCompany = null;

      qry = trans.addQuery(headblk);
      qry.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert("MPCCOWPOSTINGEVENTSPERSYSEVENTSOVWALERT: No data found.");
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   

   public void adjust()
   {
      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
      }

   }  

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("SYSTEM_EVENT_ID").
         setLabel("MPCCOWSYSTEMEVENTOVWSYSEVENTID: System Event ID").
         setReadOnly().
         setUpperCase();

      headblk.addField("DESCRIPTION").
         setLabel("MPCCOWSYSTEMEVENTOVWDESCRIPTION: System Event Description");

      headblk.addField("DIRECTION").
         setLabel("MPCCOWSYSTEMEVENTOVWDIRECTION: Transaction Direction");

      headblk.addField("INVENTORY_STAT_DIRECTION").
         setLabel("MPCCOWSYSTEMEVENTOVWINVSTATDIRECTION: Inventory Stat Direction");

      headblk.addField("INTRASTAT_DIRECTION").
         setLabel("MPCCOWSYSTEMEVENTOVWINTRADIRECTION: Intrastat Direction");

      headblk.addField("TRANSIT_QTY_DIRECTION").
         setLabel("MPCCOWSYSTEMEVENTOVWTRANSQTYDIRECTION: Transit Qty Direction");

      headblk.addField("TRANSACTION_SOURCE").
         setLabel("MPCCOWSYSTEMEVENTOVWTRANSSOURCE: Transaction Source");

      headblk.addField("ORDER_TYPE").
         setLabel("MPCCOWSYSTEMEVENTOVWORDERTYPE: Order Type");

      headblk.addField("SOURCE_APPLICATION").
         setLabel("MPCCOWSYSTEMEVENTOVWSOURCEAPPLICATION: Source Application");

      headblk.addField("CONSIGNMENT_STOCK").
         setLabel("MPCCOWSYSTEMEVENTOVWCONSIGNMENTSTOCK: Consignment Stock");

      headblk.addField("CUSTOMER_OWNED_STOCK_DB").
	 setCheckBox("CUSTOMER OWNED STOCK NOT ALLOWED,CUSTOMER OWNED STOCK ALLOWED").
         setLabel("MPCCOWSYSTEMEVENTOVWCUSOWNEDSTKDB: Customer Owned Stock Allowed");

      headblk.addField("SUPPLIER_LOANED_STOCK_DB").
	 setCheckBox("SUPPLIER LOANED STOCK NOT ALLOWED,SUPPLIER LOANED STOCK ALLOWED").
         setLabel("MPCCOWSYSTEMEVENTOVWSUPLOANEDSTKDB: Supplier Loaned Stock Allowed");

      headblk.addField("CORRESPONDING_TRANSACTION").
         setLabel("MPCCOWSYSTEMEVENTOVWCORRESTRANS: Corresponding Transaction");

      headblk.addField("CORRESPONDING_TRANSACTION_DESC").
	 setFunction("Mpccom_System_Event_API.Get_Description(:CORRESPONDING_TRANSACTION)").
         setLabel("MPCCOWSYSTEMEVENTOVWCORRESTRANSDESC: Corresponding Transaction Description");

      headblk.addField("PART_TRACING").
         setLabel("MPCCOWSYSTEMEVENTOVWPARTTRACING: Part Tracing");

      headblk.addField("ACTUAL_COST_RECEIPT").
         setLabel("MPCCOWSYSTEMEVENTOVWACTUALCOSTRECEIPT: Actual Cost Receipt");

      headblk.addField("COST_SOURCE").
         setLabel("MPCCOWPOSTINGTYPESPERSYSEVENTSCOSTSOURCE: Cost Source");

      headblk.addField("TRANS_BASED_REVAL_GROUP").
         setLabel("MPCCOWSYSTEMEVENTOVWTRANSBASEDREVALGRP: Trans Based Reval Group");

      headblk.addField("NOTC").
	 setLabel("MPCCOWSYSTEMEVENTOVWNOTC: Notc");

      headblk.addField("NOTC_DESC").
	 setFunction("NOTC_API.Get_Description(:NOTC)").
	 setLabel("MPCCOWSYSTEMEVENTOVWNOTCDESC: Notc");

      headblk.addField("AUTHORIZE_ID").
	 setLabel("MPCCOWSYSTEMEVENTOVWAUTHORIZEID: Authorize ID");

      headblk.addField("ONLINE_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSONLINEFLAGDB: Posting Online").
        setCheckBox("N,Y");

      headblk.addField("MATERIAL_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMATERIALADDITIONFLAGDB: Material Addition").
	setMandatory().
        setCheckBox("N,Y");

      headblk.addField("OH1_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH1BURDENFLAGDB: Overhead Cost 1").
        setCheckBox("N,Y");

      headblk.addField("OH2_BURDEN_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSOH2BURDENFLAGDB: Overhead Cost 2").
        setCheckBox("N,Y");

      headblk.addField("SALES_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSSALESOVERHEADFLAGDB: Sales Overhead").
        setCheckBox("FALSE,TRUE");

      headblk.addField("MS_ADDITION_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSMSADDITIONFLAGDB: Administration Overhead").
        setCheckBox("N,Y");

      headblk.addField("LABOR_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWSPOSTEVENTSLABOROVERHEADFLAGDB: Default Labor Overhead").
        setCheckBox("N,Y");

      headblk.addField("GENERAL_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSGENERALOVERHEADFLAGDB: General Overhead").
        setCheckBox("N,Y");

      headblk.addField("DELIVERY_OVERHEAD_FLAG_DB").
        setLabel("MPCCOWPOSTEVENTSDELIVERYOVERHEADFLAGDB: Delivery Overhead").
        setCheckBox("N,Y");

      headblk.addField("CONSIGNMENT_EVENT").
	 setLabel("MPCCOWSYSTEMEVENTOVWCONSEVENT: Consignment Event");

      headblk.setView("MPCCOM_SYSTEM_EVENT_ALL");
      headblk.defineCommand("MPCCOM_SYSTEM_EVENT_API","");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.setBorderLines(false,true); 
      headbar.enableMultirowAction();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MPCCOWSYSTEMEVENTOVWTITLE: Query - Inventory and Distribution System Events");
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }

//===============================================================
//  HTML
//===============================================================

   protected String getDescription()
   {
      return "MPCCOWSYSTEMEVENTOVWTITLE: Query - Inventory and Distribution System Events";
   }

   protected String getTitle()
   {
      return "MPCCOWSYSTEMEVENTOVWTITLE: Query - Inventory and Distribution System Events";
   }

   protected void printContents() throws FndException
   { 
     if(headlay.isVisible()) 
       appendToHTML(headlay.show());
   }
}
