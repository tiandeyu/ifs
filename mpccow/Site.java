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
 *  File         : Site.java
 *  Description  : Site
 *  Notes        :
 * ----------------------------------------------------------------------------
 *  Modified     :
 * ---------------------- Wings Merge Start -----------------------------------
 *     ChJalk 30-Oct-2006 - Created.
 *     ChJalk 07-Nov-2006 - Added Tabs Inventory and Distribution.
 *     ChJalk 08-Jan-2007 - Added validations for Company name and Calendar status.
 *     ChJalk 30-Jan-2007 - Merged Wings Code.
 * ---------------------- Wings Merge End -------------------------------------
 *     Haunlk 08-Feb-2007 - DIBR209,DIBR210, Added the setMaxLength to the field INTERNAL_CUSTOMER,INTERNAL_SUPPLIER
 *     NaLrlk 16-Feb-2007 - Added fields USE_PRE_SHIP_DEL_NOTE_DB, CREATE_ORD_IN_REL_STATE_DB, USE_PARTCA_DESC_ORDER_DB and USE_PARTCA_DESC_PURCH_DB.
 *     SenSlk 16-Apr-2007 - Made Description, Company & Delivery Address Readonly, and added call for the Company Site Dialog and made Delivery_Adddress
 *     SenSlk		        - editable only during EDIT mode.
 *     MaMalk 11-Jun-2007 - Call 146127, Replaced label Use Pre-Ship delivery note with Use Two-stage Picking.	
 *     RaKalk 31-Jul-2007 - Call 147032, made DESCRIPTION and COMPANY fields read only in the adjust method instead of predefine
 *            31-Jul-2007 - Reformated the code to match IFS coding standerds
 *     NaLrlk 21-Aug-2007 - Removed call for Company Site Dialog and modified saveReturn function. Added Javascript functions validateContract,
 *            21-Aug-2007 - lovCompany and lovCompanyConditional. Added printContents function and removed getContents().
 *     MalLlk 02-Mar-2009 - Bug 80863, Handled dynamic calls to modules INVENT, DISCOM, MSCOM and MFGSTD.
 *     MalLlk 11-Mar-2009 - Bug 80863, Added method activateTab to handled dynamic tabs in modules INVENT, DISCOM, MSCOM and MFGSTD.                                  
 *     MalLlk 18-Mar-2009 - Bug 81401, Modified methods okFindItems, printContents and preDefine to handle dynamic modules.                                  
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Site extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.Site");

   // Bug 80863, start
   private static final int MANUFACTURING_TAB = 1;
   private static final int MAINTENANCE_TAB   = 2;
   private static final int INVENTORY_TAB     = 3;
   private static final int DISCOM_TAB        = 4;
   // Bug 80863, end
   // Bug 81401, start
   private static boolean mfgstd_installed;
   private static boolean mscom_installed;
   private static boolean invent_installed;
   private static boolean discom_installed;
   // Bug 81401, end

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPTabContainer tabs;

   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;

   private ASPBlock        itemblk0;
   private ASPRowSet       itemset0;
   private ASPCommandBar   itembar0;
   private ASPTable        itemtbl0;
   private ASPBlockLayout  itemlay0;

   private ASPBlock        itemblk1;
   private ASPRowSet       itemset1;
   private ASPCommandBar   itembar1;
   private ASPTable        itemtbl1;
   private ASPBlockLayout  itemlay1;

   private ASPBlock        itemblk2;
   private ASPRowSet       itemset2;
   private ASPCommandBar   itembar2;
   private ASPTable        itemtbl2;
   private ASPBlockLayout  itemlay2;

   private ASPBlock        itemblk3;
   private ASPRowSet       itemset3;
   private ASPCommandBar   itembar3;
   private ASPTable        itemtbl3;
   private ASPBlockLayout  itemlay3;

   //===============================================================
   // Construction
   //===============================================================

   public Site(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String txt = null;
      String val = mgr.readValue("VALIDATE");

      if ("COMPANY".equals(val))
      {
         cmd = trans.addCustomFunction("GETNAME", "Company_API.Get_Name", "NAME");
         cmd.addParameter("COMPANY");
         trans = mgr.validate(trans);

         String sName = trans.getValue("GETNAME/DATA/NAME");

         txt = (mgr.isEmpty(sName)   ? "" : sName)   + "^";
      }
      if ("DIST_CALENDAR_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GETDISTSTATUS", "Work_Time_Calendar_API.Get_State", "DIST_CALENDAR_STATE");
         cmd.addParameter("DIST_CALENDAR_ID");
         trans = mgr.validate(trans);

         String sDistCalc = trans.getValue("GETDISTSTATUS/DATA/DIST_CALENDAR_STATE");

         txt = (mgr.isEmpty(sDistCalc)   ? "" : sDistCalc)   + "^";
      }
      if ("MANUF_CALENDAR_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GETMANUFSTATUS", "Work_Time_Calendar_API.Get_State", "MANUF_CALENDAR_STATE");
         cmd.addParameter("MANUF_CALENDAR_ID");
         trans = mgr.validate(trans);

         String sManufCalc = trans.getValue("GETMANUFSTATUS/DATA/MANUF_CALENDAR_STATE");

         txt = (mgr.isEmpty(sManufCalc)   ? "" : sManufCalc)   + "^";
      }
      if ("CONTRACT".equals(val))
      {
         String contract = mgr.readValue("CONTRACT");

         cmd = trans.addCustomFunction("SITEEXISTS","Company_Site_API.Check_Exist","SITE_EXIST");
         cmd.addParameter("CONTRACT",contract);

         cmd = trans.addCustomCommand("SITEINFO","Company_Site_API.Get_Info");
         cmd.addParameter("SITE_DESCRIPTION");
         cmd.addParameter("COMPANY");
         cmd.addParameter("NAME");
         cmd.addParameter("CONTRACT",contract);
         trans = mgr.validate(trans);

         String siteExist     = trans.getValue("SITEEXISTS/DATA/SITE_EXIST");
         String siteDesc      = trans.getValue("SITEINFO/DATA/SITE_DESCRIPTION");
         String company       = trans.getValue("SITEINFO/DATA/COMPANY");
         String companyName   = trans.getValue("SITEINFO/DATA/NAME");

         txt = (mgr.isEmpty(siteDesc)     ? "" :siteDesc)      + "^" +
               (mgr.isEmpty(company)      ? "" :company)       + "^" +
               (mgr.isEmpty(companyName)  ? "" :companyName)   + "^" +
               (mgr.isEmpty(siteExist)    ? "" :siteExist)     + "^";
      }
      mgr.responseWrite( txt );
      mgr.endResponse();
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFindAll();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( mgr.dataTransfered() )
         okFindAll();

      adjust();
      tabs.saveActiveTab();
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

      cmd = trans.addEmptyCommand("HEAD","SITE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);

   }

   /**    Invokes when saving the header details */
   public void  saveReturn()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      int        currentRow;

      headset.changeRow();
      currentRow = headset.getCurrentRowNo();

      if ("New__".equals(headset.getRowStatus()))
      {
         if ("0".equals(headset.getValue("SITE_EXIST")))
         {
            cmd = trans.addCustomCommand("CREATE_COMPANY_SITE","Company_Site_API.Create_Company_Site");
            cmd.addParameter("SITE_ID",          headset.getValue("CONTRACT"));
            cmd.addParameter("SITE_DESCRIPTION", headset.getValue("SITE_DESCRIPTION"));
            cmd.addParameter("COMPANY",          headset.getValue("COMPANY"));

         }
      }
      mgr.submit(trans);
      headset.goTo(currentRow);
      headset.refreshRow();
      okFindItems();
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
         mgr.showAlert(mgr.translate("MPCCOWSITENODATA: No data found."));
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
      q.addWhereCondition("CONTRACT = ? ");
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
      q.includeMeta("ALL");

      currow  = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk0);
      headset.goTo(currow);
   }

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int currow = 0;

      q = trans.addEmptyQuery(itemblk1);
      q.addWhereCondition("CONTRACT = ? ");
      q.addParameter("ITEM1_CONTRACT",headset.getValue("CONTRACT"));
      q.includeMeta("ALL");

      currow  = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk1);
      headset.goTo(currow);

   }

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int currow = 0;

      q = trans.addEmptyQuery(itemblk2);
      q.addWhereCondition("CONTRACT = ? ");
      q.addParameter("ITEM2_CONTRACT",headset.getValue("CONTRACT"));
      q.includeMeta("ALL");

      currow  = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk2);
      headset.goTo(currow);

   }

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int currow = 0;

      q = trans.addEmptyQuery(itemblk3);
      q.addWhereCondition("CONTRACT = ? ");
      q.addParameter("ITEM3_CONTRACT",headset.getValue("CONTRACT"));
      q.includeMeta("ALL");

      currow  = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk3);
      headset.goTo(currow);

   }

   public void okFindAll()
   {
      okFind();
      okFindItems();
   }

   public void okFindItems()
   {
      // Bug 81401, Check whether the mfgstd module is installed
      if (mfgstd_installed)
         okFindITEM0();
      // Bug 81401, Check whether the mscom module is installed
      if (mscom_installed)
         okFindITEM1();
      // Bug 81401, Check whether the invent module is installed
      if (invent_installed)
         okFindITEM2();
      // Bug 81401, Check whether the discom module is installed
      if (discom_installed)
         okFindITEM3();
   }

   public void  preDefine()throws FndException
   {
      ASPManager mgr = getASPManager();

      // Bug 81401, start
      mfgstd_installed = mgr.isModuleInstalled("MFGSTD");
      mscom_installed  = mgr.isModuleInstalled("MSCOM");
      invent_installed = mgr.isModuleInstalled("INVENT");
      discom_installed = false; //mgr.isModuleInstalled("DISCOM");
      // Bug 81401, end

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField( "OBJID" ).
      setHidden();

      headblk.addField( "OBJVERSION" ).
      setHidden();

      headblk.addField("CONTRACT").
      setLabel("MPCCOWSITESITEID: Site").
      setMandatory().
      setSize(10).
      setMaxLength(5).
      setDynamicLOV("COMPANY_SITE").
      setLOVProperty("TITLE",mgr.translate("MPCCOWSITESITEID: Site")).
      setReadOnly().
      setInsertable().
      setCustomValidation("CONTRACT","SITE_DESCRIPTION,COMPANY,NAME,SITE_EXIST").
      setUpperCase();

      headblk.addField("SITE_DESCRIPTION").
      setLabel("MPCCOWSITEDESCPTION: Site Description").
      setMandatory().
      setInsertable().
      setMaxLength(35).
      setFunction("Site_API.Get_Description(:CONTRACT)").
      setSize(30);

      headblk.addField("COMPANY").
      setLabel("MPCCOWSITECOMPANY: Company").
      setMandatory().
      setInsertable().
      setDynamicLOV("COMPANY_FINANCE").
      setCustomValidation("COMPANY","NAME").
      setLOVProperty("TITLE",mgr.translate("MPCCOWSITECOMPANY: Company")).
      setMaxLength(20).
      setSize(15);

      headblk.addField("NAME").
      setLabel("MPCCOWSITENAME: Name").
      setMaxLength(100).
      setFunction("Company_API.Get_Name(:COMPANY)").
      setReadOnly().
      setSize(30);

      headblk.addField("DIST_CALENDAR_ID").
      setLabel("MPCCOWSITEDISTCALENDARID: Distribution Calendar").
      setMandatory().
      setDynamicLOV("WORK_TIME_CALENDAR").
      setLOVProperty("TITLE",mgr.translate("MPCCOWSITEDISTCALENDARID: Distribution Calendar")).
      setMaxLength(10).
      setCustomValidation("DIST_CALENDAR_ID","DIST_CALENDAR_STATE").
      setUpperCase().
      setSize(15);

      headblk.addField("DIST_CALENDAR_STATE").
      setLabel("MPCCOWSITEDISTCALENDARSTATE: Calendar Status").
      setReadOnly().
      setFunction("Work_Time_Calendar_API.Get_State(:DIST_CALENDAR_ID)").
      setMaxLength(20).
      setSize(30);

      headblk.addField("MANUF_CALENDAR_ID").
      setLabel("MPCCOWSITEMANUFCALENDARID: Manufacturing Calendar").
      setMandatory().
      setMaxLength(10).
      setUpperCase().
      setCustomValidation("MANUF_CALENDAR_ID","MANUF_CALENDAR_STATE").
      setDynamicLOV("WORK_TIME_CALENDAR").
      setLOVProperty("TITLE",mgr.translate("MPCCOWSITEMANUFCALENDARID: Manufacturing Calendar")).
      setSize(15);

      headblk.addField("MANUF_CALENDAR_STATE").
      setLabel("MPCCOWSITEMANUFCALENDARSTATE: Calendar Status").
      setMaxLength(20).
      setReadOnly().
      setFunction("Work_Time_Calendar_API.Get_State(:MANUF_CALENDAR_ID)").
      setSize(30);

      headblk.addField("OFFSET","Number").
      setMandatory().
      setLabel("MPCCOWSITEOFFSET: Time Zone Offset(hours)").
      setMaxLength(35).
      setSize(15);

      headblk.addField("DELIVERY_ADDRESS").
      setLabel("MPCCOWSITEDELIVERYADDRESS: Delivery Address").
      setMaxLength(50).
      setUpperCase().
      setInsertable().
      setDynamicLOV("COMPANY_ADDRESS_LOV_PUB","COMPANY").
      setLOVProperty("TITLE",mgr.translate("MPCCOWSITEDELIVERYADDRESS: Delivery Address")).
      setSize(30);

      headblk.addField("SITE_EXIST").
      setFunction("Company_Site_API.Check_Exist(:CONTRACT)").
      setHidden();

      headblk.addField("SITE_ID").
      setFunction("''").
      setHidden();

      headblk.setView("SITE");
      headblk.defineCommand("SITE_API","New__,Modify__,Remove__");

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.unsetAutoLayoutSelect();

      headlay.defineGroup("Main", "CONTRACT,SITE_DESCRIPTION", false, true);
      headlay.defineGroup(mgr.translate("MPCCOWSITEGENERAL: General"),"COMPANY,NAME,DIST_CALENDAR_ID,DIST_CALENDAR_STATE,MANUF_CALENDAR_ID,MANUF_CALENDAR_STATE,OFFSET,DELIVERY_ADDRESS", true, true);

      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("MPCCOWSITETBLTITLE: Site"));

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(ASPCommandBar.SAVERETURN, "saveReturn","checkHeadFields()");

      headbar.addCustomCommand("activateManufacturing",mgr.translate("MPCCOWSITEMANUFACTURINGSITE: Manufacturing"));
      headbar.addCustomCommand("activateMaintenance",mgr.translate("MPCCOWSITEMAINTENANCESITE: Maintenance"));
      headbar.addCustomCommand("activateInventory",mgr.translate("MPCCOWSITEINVENTORYSITE: Inventory"));
      headbar.addCustomCommand("activateDistribution",mgr.translate("MPCCOWSITEDISTRIBUTIONITE: Distribution"));

      //--------------------------------------------------------------------
      //------------------- Manufacturing TAB ------------------------------
      //--------------------------------------------------------------------

      // Bug 81401, Check whether the mfgstd module is installed
      if (mfgstd_installed) 
      {
         itemblk0 = mgr.newASPBlock("ITEM0");
   
         itemblk0.addField( "ITEM0_OBJID" ).
         setDbName("OBJID").
         setHidden();
   
         itemblk0.addField( "ITEM0_OBJVERSION" ).
         setDbName("OBJVERSION").
         setHidden();
   
         itemblk0.addField("STRUCTURE_UPDATE").
         setMandatory().
         setLabel("MPCCOWSITESTRUCTUREUPDATE: Structure Update").
         setMaxLength(200).
         enumerateValues("STRUCTURE_UPDATE_CONTROL_API.Enumerate"," ").
         setSelectBox().
         setSize(30);
   
         itemblk0.addField("STRUCTURE_STATE_DEFAULT").
         setMandatory().
         setLabel("MPCCOWSITESTRUCTURESTATEDEFAULT: Structure Status Default").
         setMaxLength(200).
         enumerateValues("STRUCTURE_STATE_DEFAULT_API.Enumerate"," ").
         setSelectBox().
         setSize(30);
   
         itemblk0.addField("DISPOSITION_OF_QUOTATION").
         setMandatory().
         setLabel("MPCCOWSITEDISPOSITIONOFQUOTATION: Disposition of Quotation").
         setMaxLength(200).
         enumerateValues("DISPOSITION_OF_QUOTATION_API.Enumerate"," ").
         setSelectBox().
         setSize(30);
   
         itemblk0.addField("SHPORD_RECEIPT_BACKGROUND_DB").
         setLabel("MPCCOWSITESHPRECPTBKGROUND: Default SO Receipt in Background").
         setCheckBox("FALSE,TRUE");
   
         itemblk0.addField("DOP_AUTO_CLOSE_DB").
         setLabel("MPCCOWSITEDOPAUTOCLOSEDB: DOP Auto Close").
         setCheckBox("FALSE,TRUE");
   
         itemblk0.addField("VIM_MRO_ENABLED_DB").
         setLabel("MPCCOWSITEVIMMROENABLEDDB: VIM MRO Enabled").
         setCheckBox("FALSE,TRUE");
   
         itemblk0.setView("SITE_MFGSTD_INFO");
         itemblk0.defineCommand("SITE_MFGSTD_INFO_API","Modify__");
         itemblk0.setMasterBlock(headblk);
   
         itemlay0 = itemblk0.getASPBlockLayout();
         itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT);
         itemlay0.unsetAutoLayoutSelect();
   
         itemset0 = itemblk0.getASPRowSet();
         itembar0 = mgr.newASPCommandBar(itemblk0);
   
         itemtbl0 = mgr.newASPTable( itemblk0 );
         itemtbl0.setTitle(mgr.translate("MPCCOWSITEMANUFACTURINGSITETAB: Manufacturing"));
      }

      //------------------------------------------------------------------
      //------------------- Maintenance TAB ------------------------------
      //------------------------------------------------------------------

      // Bug 81401, Check whether the mscom module is installed
      if (mscom_installed) 
      {
         itemblk1 = mgr.newASPBlock("ITEM1");
   
         itemblk1.addField( "ITEM1_OBJID" ).
         setDbName("OBJID").
         setHidden();
   
         itemblk1.addField( "ITEM1_OBJVERSION" ).
         setDbName("OBJVERSION").
         setHidden();
   
         itemblk1.addField( "ITEM1_CONTRACT" ).
         setDbName("CONTRACT").
         setHidden();
   
         itemblk1.addField("MESSAGE_RECEIVER").
         setLabel("MPCCOWSITESMESSAGERECEIVER: Message Receiver").
         setMaxLength(30).
         setDynamicLOV("MESSAGE_RECEIVER").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITESMESSAGERECEIVER: Message Receiver")).
         setSize(30);
   
         itemblk1.addField("DISP_COND_WORK_ORDER_DB").
         setLabel("MPCCOWSITEDISPCONDWORKORDERDB: Print Condition Codes on Reports").
         setMaxLength(5).
         setCheckBox("FALSE,TRUE");
   
         itemblk1.setView("SITE_MSCOM_INFO");
         itemblk1.defineCommand("SITE_MSCOM_INFO_API","Modify__");
         itemblk1.setMasterBlock(headblk);
   
         itemlay1 = itemblk1.getASPBlockLayout();
         itemlay1.setDefaultLayoutMode(itemlay1.SINGLE_LAYOUT);
         itemlay1.unsetAutoLayoutSelect();
   
         itemset1 = itemblk1.getASPRowSet();
         itembar1 = mgr.newASPCommandBar(itemblk1);
   
         itemtbl1 = mgr.newASPTable( itemblk1 );
         itemtbl1.setTitle(mgr.translate("MPCCOWSITEMAINTAINENACESITETAB: Maintenance"));
      }

      //----------------------------------------------------------------
      //------------------- Inventory TAB ------------------------------
      //----------------------------------------------------------------

      // Bug 81401, Check whether the invent module is installed
      if (invent_installed) 
      {
         itemblk2 = mgr.newASPBlock("ITEM2");
   
         itemblk2.addField( "ITEM2_OBJID" ).
         setDbName("OBJID").
         setHidden();
   
         itemblk2.addField( "ITEM2_OBJVERSION" ).
         setDbName("OBJVERSION").
         setHidden();
   
         itemblk2.addField( "ITEM2_CONTRACT" ).
         setDbName("CONTRACT").
         setHidden();
   
         itemblk2.addField("PICKING_LEADTIME","Number").
         setLabel("MPCCOWSITEINVENTPICKINGLEADTIME: Picking Lead Time").
         setMandatory().
         setSize(25);
   
         itemblk2.addField("NEGATIVE_ON_HAND").
         setLabel("MPCCOWSITEINVENTNEGATIVEONHAND: Negative On Hand").
         setMaxLength(200).
         setSize(25).
         enumerateValues("NEGATIVE_ON_HAND_API.Enumerate"," ").
         setSelectBox().
         setMandatory();
   
         itemblk2.addField("INVENTORY_VALUE_METHOD").
         setMandatory().
         setLabel("MPCCOWSITEINVENTINVENTORYVALUEMETHOD: Inventory Valuation Method").
         setMaxLength(200).
         enumerateValues("INVENTORY_VALUE_METHOD_API.Enumerate"," ").
         setSelectBox().
         setSize(25);
   
         itemblk2.addField("EXT_SERVICE_COST_METHOD").
         setMandatory().
         setLabel("MPCCOWSITEINVENTEXTSERVICECOSTMETHOD: External Service Cost Method").
         setMaxLength(200).
         enumerateValues("EXT_SERVICE_COST_METHOD_API.Enumerate"," ").
         setSelectBox().
         setSize(25);
   
         itemblk2.addField("INVOICE_CONSIDERATION").
         setMandatory().
         setLabel("MPCCOWSITEINVENTINVOICECONSIDERATION: Supplier Invoice Consideration").
         setMaxLength(200).
         enumerateValues("INVOICE_CONSIDERATION_API.Enumerate"," ").
         setSelectBox().
         setSize(25);
   
         itemblk2.addField("LAST_ACTUAL_COST_CALC","Date").
         setLabel("MPCCOWSITEINVENTLASTACTUALCOSTCALC: Last Period WA Date").
         setSize(25);
   
         itemblk2.addField("COST_DEFAULTS_MANUALLY_DB").
         setMandatory().
         setLabel("MPCCOWSITEINVENTCOSTDEFAULTSMANUALLYDB: Get Cost Detail Defaults Manually").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk2.addField("ROUNDDIFF_INACTIVITY_DAYS","Number").
         setMandatory().
         setLabel("MPCCOWSITEINVENTROUNDDIFFINACTIVITYDAYS: Inactive Days For Rounding Difference").
         setSize(25);
   
         itemblk2.addField("AVG_WORK_DAYS_PER_WEEK","Number").
         setMandatory().
         setLabel("MPCCOWSITEINVENTAVGWORKDAYSPERWEEK:  Average Working Days Per Week").
         setSize(25);
   
         itemblk2.addField("COUNT_DIFF_AMOUNT","Number").
         setLabel("MPCCOWSITEINVENTCOUNTDIFFAMOUNT: Max Counting Difference Amount").
         setSize(25);
   
         itemblk2.addField("COUNT_DIFF_PERCENTAGE","Number").
         setLabel("MPCCOWSITEINVENTCOUNTDIFFPERCENTAGE: Max Counting Difference Percentage:").
         setSize(36);
   
         itemblk2.addField("COUNTRY_CODE").
         setLabel("MPCCOWSITEINVENTCOUNTRYCODE: Country Code").
         setUpperCase().
         setMaxLength(2).
         setDynamicLOV("COUNTRY_OF_REGION_LOV").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITEINVENTCOUNTRYCODE: Country Code")).
         setSize(25);
   
         itemblk2.addField("REGION_CODE").
         setLabel("MPCCOWSITEINVENTREGIONCODE: Region Code").
         setUpperCase().
         setMaxLength(10).
         setDynamicLOV("COUNTRY_REGION","COUNTRY_CODE").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITEINVENTREGIONCODE: Region Code")).
         setSize(38);
   
         itemblk2.addField("MRB_AVAIL_CONTROL_ID").
         setLabel("MPCCOWSITEINVENTMRB_AVAILCONTROLID: MRB Availability Control ID").
         setUpperCase().
         setMaxLength(25).
         setDynamicLOV("PART_AVAILABILITY_CONTROL").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITEINVENTMRB_AVAILCONTROLID: MRB Availability Control ID")).
         setSize(33);
   
         itemblk2.addField("USE_PARTCA_DESC_INVENT_DB").
         setLabel("MPCCOWSITEINVENTUSEPARTCADESCINVENTDB: Use Part Catalog Description as Description for Inventory Part").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk2.setView("SITE_INVENT_INFO");
         itemblk2.defineCommand("SITE_INVENT_INFO_API","Modify__");
         itemblk2.setMasterBlock(headblk);
   
         itemlay2 = itemblk2.getASPBlockLayout();
         itemlay2.setDefaultLayoutMode(itemlay2.SINGLE_LAYOUT);
         itemlay2.unsetAutoLayoutSelect();
   
         itemlay2.defineGroup("Main", "PICKING_LEADTIME,NEGATIVE_ON_HAND", false, true);
         itemlay2.defineGroup(mgr.translate("MPCCOWSITEINVENTVALUATION: Valuation"),"INVENTORY_VALUE_METHOD,EXT_SERVICE_COST_METHOD,INVOICE_CONSIDERATION,LAST_ACTUAL_COST_CALC,COST_DEFAULTS_MANUALLY_DB,ROUNDDIFF_INACTIVITY_DAYS", true, true);
         itemlay2.defineGroup(mgr.translate("MPCCOWSITEINVENTCOUNTING: Counting"), "COUNT_DIFF_AMOUNT,COUNT_DIFF_PERCENTAGE", true, true);
         itemlay2.defineGroup(mgr.translate("MPCCOWSITEINVENTINTRASTAT: Special Intrastat Data"),"COUNTRY_CODE,REGION_CODE", true, true);
         itemlay2.defineGroup("Main", "AVG_WORK_DAYS_PER_WEEK,MRB_AVAIL_CONTROL_ID,USE_PARTCA_DESC_INVENT_DB", false, true);
   
         itemset2 = itemblk2.getASPRowSet();
         itembar2 = mgr.newASPCommandBar(itemblk2);
   
         itemtbl2 = mgr.newASPTable( itemblk2 );
         itemtbl2.setTitle(mgr.translate("MPCCOWSITEINVENTORYSITETAB: Inventory"));
      }

      //-------------------------------------------------------------------
      //------------------- Distribution TAB ------------------------------
      //-------------------------------------------------------------------

      // Bug 81401, Check whether the discom module is installed
      if (discom_installed) 
      {
         itemblk3 = mgr.newASPBlock("ITEM3");
   
         itemblk3.addField( "ITEM3_OBJID" ).
         setDbName("OBJID").
         setHidden();
   
         itemblk3.addField( "ITEM3_OBJVERSION" ).
         setDbName("OBJVERSION").
         setHidden();
   
         itemblk3.addField( "ITEM3_CONTRACT" ).
         setDbName("CONTRACT").
         setHidden();
   
         itemblk3.addField( "ITEM3_COMPANY" ).
         setFunction("SITE_API.Get_Company(:CONTRACT)").
         setHidden();
   
         itemblk3.addField("DOCUMENT_ADDRESS_ID").
         setLabel("MPCCOWSITEDISTRDOCUMENTADDRESSID: Document Address").
         setMaxLength(50).
         setUpperCase().
         setDynamicLOV("COMPANY_DOC_ADDRESS_LOV_PUB","COMPANY").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITEDISTRDOCUMENTADDRESSIDLOV: Document Address")).
         setSize(25);
   
         itemblk3.addField("CUST_ORDER_PRICING_METHOD").
         setLabel("MPCCOWSITEDISTRCUSTORDERPRICINGMETHOD: Pricing Method").
         setMaxLength(200).
         setSize(25).
         enumerateValues("CUST_ORDER_PRICING_METHOD_API.Enumerate"," ").
         setSelectBox().
         setMandatory();
   
         itemblk3.addField("CUST_ORDER_DISCOUNT_METHOD").
         setLabel("MPCCOWSITEDISTRCUSTORDERDISCOUNTMETHOD: Discount Method").
         setMandatory().
         setMaxLength(200).
         enumerateValues("CUST_ORDER_DISCOUNT_METHOD_API.Enumerate"," ").
         setSelectBox().
         setSize(25);
   
         itemblk3.addField("BRANCH").
         setLabel("MPCCOWSITEDISTRBRANCH: Branch").
         setUpperCase().
         setMaxLength(20).
         setDynamicLOV("BRANCH","COMPANY").
         setLOVProperty("TITLE",mgr.translate("MPCCOWSITEDISTRBRANCHLOV: Branch")).
         setSize(25);
   
         itemblk3.addField("PURCH_COMP_METHOD").
         setLabel("MPCCOWSITEDISTRPURCHCOMPMETHOD: Purchase Component Method").
         setMandatory().
         setMaxLength(200).
         enumerateValues("PURCHASE_COMPONENT_METHOD_API.Enumerate"," ").
         setSelectBox().
         setSize(25);
   
         itemblk3.addField("INTERNAL_CUSTOMER").
         setLabel("MPCCOWSITEDISTRINTERNALCUSTOMER: Internal Customer").
         setFunction("Cust_Ord_Customer_API.Get_Customer_No_From_Contract(:CONTRACT)").
         setReadOnly().
         setInsertable().
         setSize(25).
         setMaxLength(20);
   
         itemblk3.addField("INTERNAL_SUPPLIER").
         setLabel("MPCCOWSITEDISTRINTERNALSUPPLIER: Internal Supplier").
         setFunction("Supplier_API.Get_Vendor_No_From_Contract(:CONTRACT)").
         setReadOnly().
         setInsertable().
         setSize(25).
         setMaxLength(20);
   
         itemblk3.addField("DISP_COND_PURCHASE_ORDER_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDPURCHASEORDERDB: Print Condition Codes on Report").
         setCheckBox("FALSE,TRUE").
         setMaxLength(5).
         setSize(25);
   
         itemblk3.addField("DISP_COND_CUSTOMER_ORDER_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDCUSTOMERORDERDB:  Print Condition Codes on Report").
         setCheckBox("FALSE,TRUE").
         setMaxLength(5).
         setSize(25);
   
         itemblk3.addField("USE_PRE_SHIP_DEL_NOTE_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDPRESHIPDELIVNOTEDB: Use Two-stage Picking").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk3.addField("CREATE_ORD_IN_REL_STATE_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDORDINRESLSTATEDB: Create CO from Quotation in Released Status").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk3.addField("USE_PARTCA_DESC_ORDER_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDPARTCADESCORDERDB: Use Part Catalog Description as Description for Sales Part").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk3.addField("USE_PARTCA_DESC_PURCH_DB").
         setLabel("MPCCOWSITEDISTRDISPCONDPARTCADESCPURCHDB: Use Part Catalog Description as Description for Purchase Part").
         setCheckBox("FALSE,TRUE").
         setSize(25);
   
         itemblk3.setView("SITE_DISCOM_INFO");
         itemblk3.defineCommand("SITE_DISCOM_INFO_API","Modify__");
         itemblk3.setMasterBlock(headblk);
   
         itemlay3 = itemblk3.getASPBlockLayout();
         itemlay3.setDefaultLayoutMode(itemlay3.SINGLE_LAYOUT);
         itemlay3.unsetAutoLayoutSelect();
   
         itemlay3.defineGroup("Main", "DOCUMENT_ADDRESS_ID", false, true);
         itemlay3.defineGroup(mgr.translate("MPCCOWSITEDISTRCO: Customer Orders"),"CUST_ORDER_DISCOUNT_METHOD,BRANCH,CUST_ORDER_PRICING_METHOD,DISP_COND_CUSTOMER_ORDER_DB,USE_PRE_SHIP_DEL_NOTE_DB,CREATE_ORD_IN_REL_STATE_DB,USE_PARTCA_DESC_ORDER_DB", true, true);
         itemlay3.defineGroup(mgr.translate("MPCCOWSITEDISTRPO: Purchasing"), "PURCH_COMP_METHOD,DISP_COND_PURCHASE_ORDER_DB,USE_PARTCA_DESC_PURCH_DB", true, true);
         itemlay3.defineGroup(mgr.translate("MPCCOWSITEINTERSITEDATA: Inter-Site Data"),"INTERNAL_CUSTOMER,INTERNAL_SUPPLIER", true, true);
   
         itemset3 = itemblk3.getASPRowSet();
         itembar3 = mgr.newASPCommandBar(itemblk3);
   
         itemtbl3 = mgr.newASPTable( itemblk3 );
         itemtbl3.setTitle(mgr.translate("MPCCOWSITEDISTRIBUTIONSITETAB: Distribution"));
      }

      //--------------------------------------------------------------------------------------------
      // Tabs
      //--------------------------------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.addTab("MPCCOWSITEMANUFACTURINGSITETAB: Manufacturing","javascript:commandSet('HEAD.activateManufacturing','')");
      tabs.addTab("MPCCOWSITEMAINTENANCESITETAB: Maintenance","javascript:commandSet('HEAD.activateMaintenance','')");
      tabs.addTab("MPCCOWSITEINVENTORYSITETAB: Inventory","javascript:commandSet('HEAD.activateInventory','')");
      tabs.addTab("MPCCOWSITEDISTRIBUTIONSITETAB: Distribution","javascript:commandSet('HEAD.activateDistribution','')");
   
      headbar.removeCustomCommand("activateManufacturing");
      headbar.removeCustomCommand("activateMaintenance");
      headbar.removeCustomCommand("activateInventory");
      headbar.removeCustomCommand("activateDistribution");

      // Bug 81401, Replaced the condition checks with variable values
      // Bug 80863, start
      // Remove tabs if the module not installed
      if (!mfgstd_installed)
         tabs.setTabRemoved(MANUFACTURING_TAB, true);
      if (!mscom_installed)
         tabs.setTabRemoved(MAINTENANCE_TAB, true);
      if (!invent_installed)
         tabs.setTabRemoved(INVENTORY_TAB, true);
      if (!discom_installed)
         tabs.setTabRemoved(DISCOM_TAB, true);
      // Bug 80863, end

      ASPField colsCompany = mgr.getASPField("COMPANY");
      String   sLovWidth  = String.valueOf( mgr.getConfigParameter("LOV/WINDOW/WIDTH") );
      String   sLovHeight = String.valueOf( mgr.getConfigParameter("LOV/WINDOW/HEIGHT") );
      String   sLovURL    = colsCompany.getLOVURL();

      appendJavaScript("function lovCompanyConditional(i,params)\n");
      appendJavaScript("{\n");
      appendJavaScript("\tif(params) param = params;\n");
      appendJavaScript("\telse param = '';\n");
      appendJavaScript("\tif ((getValue_('SITE_EXIST',i) == '1') && (getValue_('CONTRACT',i) != ''))\n");
      appendJavaScript("\t{\n");
      appendJavaScript("\t\talert('", mgr.translateJavaScript("MPCCOWSITECOMPANYLOVNOTAVAIL: List of values for [Company] is not available when [Company] is fetched."), "');\n");
      appendJavaScript("\t\treturn;\n");
      appendJavaScript("\t}\n");
      appendJavaScript("\tvar enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendJavaScript("\tvar key_value = (getValue_('COMPANY',i).indexOf('%') !=-1)? getValue_('COMPANY',i):'';\n");
      appendJavaScript("\topenLOVWindow('COMPANY',i,\n");
      appendJavaScript("\t\t'", sLovURL, "'+param+'&__TITLE=", mgr.translateJavaScript("MPCCOWSITECOMPANY: Company"), "&MULTICHOICE='+enable_multichoice+''\n");
      appendJavaScript("\t\t+ '&__KEY_VALUE=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendJavaScript("\t\t+ '&COMPANY=' + URLClientEncode(key_value)\n");
      appendJavaScript("\t\t,", sLovWidth, ",", sLovHeight, ",'validateCompany');\n");
      appendJavaScript("}\n");
   }

   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.BACKWARD);
      }
      if (headlay.isEditLayout())
      {
         mgr.getASPField("SITE_DESCRIPTION").setReadOnly();
         mgr.getASPField("COMPANY").setReadOnly();
      }
      else
      {
         mgr.getASPField("SITE_DESCRIPTION").unsetReadOnly();
         mgr.getASPField("COMPANY").unsetReadOnly();
      }

      // Bug 80863, start
      /* First array element is a dummy - since tab index starts from 1. So first array
         element has no meaning. Actual usage starts from second array element.      */
      boolean[] enabled_tabs = {false, true, true, true, true};

      if (tabs.getActiveTab() != MANUFACTURING_TAB)
         enabled_tabs[MANUFACTURING_TAB] = false;
      if (tabs.getActiveTab() != MAINTENANCE_TAB)
         enabled_tabs[MAINTENANCE_TAB] = false;
      if (tabs.getActiveTab() != INVENTORY_TAB)
         enabled_tabs[INVENTORY_TAB] = false;
      if (tabs.getActiveTab() != DISCOM_TAB)
         enabled_tabs[DISCOM_TAB] = false;

      activateTab (enabled_tabs);
      // Bug 80863, end
   }

   //===============================================================
   // Tabs operations
   //===============================================================

   public String tabsInit()
   {
      return(tabs.showTabsInit());
   }

   public String tabsFinish()
   {
      return(tabs.showTabsFinish());
   }

   // Bug 80863, Used the value of MANUFACTURING_TAB
   public void  activateManufacturing()
   {
      tabs.setActiveTab(MANUFACTURING_TAB);
   }

   // Bug 80863, Used the value of MAINTENANCE_TAB
   public void  activateMaintenance()
   {
      tabs.setActiveTab(MAINTENANCE_TAB);
   }

   // Bug 80863, Used the value of INVENTORY_TAB
   public void  activateInventory()
   {
      tabs.setActiveTab(INVENTORY_TAB);
   }

   // Bug 80863, Used the value of DISCOM_TAB
   public void  activateDistribution()
   {
      tabs.setActiveTab(DISCOM_TAB);
   }

   // Bug 80863, start
   // Set the active tab
   public void activateTab (boolean[] enabled_tabs)
   {
      for (int i = 1; i < enabled_tabs.length; i++)
      {
         if (enabled_tabs[i])
         {
            tabs.setActiveTab(i);
            break;
         }
      }
   }
   // Bug 80863, end

   //===============================================================
   //  HTML
   //===============================================================

   protected String getDescription()
   {
      return "MPCCOWSITETITLE: Site";
   }

   protected String getTitle()
   {
      if (headlay.isMultirowLayout())
         return "MPCCOWSITEOVWTITLE: Overview - Sites";
      else
         return "MPCCOWSITETITLE: Site";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Bug 81401, start
      boolean any_dynamic_tab_visible = (mfgstd_installed && itemlay0.isVisible()) || 
                                        (mscom_installed  && itemlay1.isVisible()) || 
                                        (invent_installed && itemlay2.isVisible()) || 
                                        (discom_installed && itemlay3.isVisible());
      // Bug 81401, end

      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      // Bug 81401, Modified to perform additional check to verify any dynamic tab is installed
      if (headlay.isVisible() && any_dynamic_tab_visible)
      {
         if (headset.countRows() != 0)
            appendToHTML(tabsInit());
      }
      // Bug 80863, Used the value of MANUFACTURING_TAB
      if ((tabs.getActiveTab() == MANUFACTURING_TAB) && (headset.countRows() != 0))
      {
         if (itemlay0.isVisible()) appendToHTML(itemlay0.show());
      }
      // Bug 80863, Used the value of MAINTENANCE_TAB
      if (tabs.getActiveTab()  == MAINTENANCE_TAB && headset.countRows() != 0)
      {
         if (itemlay1.isVisible()) appendToHTML(itemlay1.show());
      }
      // Bug 80863, Used the value of INVENTORY_TAB
      if ((tabs.getActiveTab() == INVENTORY_TAB) && (headset.countRows() != 0))
      {
         if (itemlay2.isVisible()) appendToHTML(itemlay2.show());
      }
      // Bug 80863, Used the value of DISCOM_TAB
      if (tabs.getActiveTab()  == DISCOM_TAB && headset.countRows() != 0)
      {
         if (itemlay3.isVisible()) appendToHTML(itemlay3.show());
      }
      // Bug 81401, Modified to perform additional check to verify any dynamic tab is installed
      if (headlay.isVisible() && headset.countRows() != 0 && any_dynamic_tab_visible)
      {
         appendToHTML(tabsFinish());
      }

      appendDirtyJavaScript("function validateContract(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkContract(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   window.status='Please wait for validation';\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("  	   APP_ROOT+ 'mpccow/Site.page'+'?VALIDATE=CONTRACT'\n");
      appendDirtyJavaScript("  	   + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("  	);\n");
      appendDirtyJavaScript("   window.status='';\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'CONTRACT',i,'Site') )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      assignValue_('SITE_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("      assignValue_('COMPANY',i,1);\n");
      appendDirtyJavaScript("      assignValue_('NAME',i,2);\n");
      appendDirtyJavaScript("      assignValue_('SITE_EXIST',i,3);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (getValue_('SITE_EXIST',i)=='0')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      getField_('SITE_DESCRIPTION',i).readOnly = false;\n");
      appendDirtyJavaScript("      getField_('COMPANY',i).readOnly = false;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      getField_('SITE_DESCRIPTION',i).readOnly = true;\n");
      appendDirtyJavaScript("      getField_('COMPANY',i).readOnly = true;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovCompany(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   lovCompanyConditional(i,params)\n");
      appendDirtyJavaScript("}\n");
   }
}
