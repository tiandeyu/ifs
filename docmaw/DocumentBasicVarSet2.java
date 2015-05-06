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
*  File        : DocumentBasicVarSet2.java 
*  Bakalk  2002-11-06  - Created.
*  Bakalk  2002-11-27  - Remove eval method and changed the return type of "tabsInit"
                         and "tabsFinish" methods from void to String.  
*       
*  Prsalk  2003-02-11  - Added Booking List tab.
*  InoSlk  2003-2-13   - Added Tab 'Number Counter'.
*  Inoslk  2003-2-19   - Set ID1 and ID2 in Tab 'Number Counter' to UPPERCASE.
*  InoSlk  2003-2-20   - Modified field properties in tabs Booking List and Number Counter.
*  Prsalk  2003-2-24 -  Added new lov "DOC_NUMBER_COUNTER_LOV" for ID1, set MaxLength to "Remark" and added null value checks.
*  mdahse  2003-02-26 - Changed getDescription() and getTitle().
*  Prsalk  2003-03-04 - Changed "Generate" layout.
*  BaKalk  2003-04-01 - Added clone() and doReset() in order to support 3.5.1 web client.
*  DhPelk  2003-05-20 - Removed column Line_No.
*  DhPelk  2003-05-22 - Fixed call 95336
*  Shtolk  2003-07-30 - Fixed Call ID 99802, Changed the translation constants according to the call description. 
*  InoSlk  2003-09-09 - Call ID 102752: Fixed the LOV problem in Web Client 360 (b4) Find Layout.
*  Inoslk  2003-09-11 - Call ID 102752: Added translateable constants in ITEMBLK4.
*  NiSilk  2003-10-07 - Set the prefix and suffix to uppercase.
*  BaKalk  2003-12-17 - Web Alignment (tabs) done.
*  BaKalk  2003-12-17 - Web Alignment (Multirow Actions) done.
*  BaKalk  2004-01-02 - Web Alignment (Field Order) done.
*  BaKalk  2004-03-03 - Call Id 112657,refreshed the record set after generating new numbers (in booking list) and deleting them.
*  ThWilk  2006-02-09 - Call ID 133187,Modified Predefine().
*  BAKALK 2006-07-25  - Bug ID 58216, Fixed Sql Injection.
*  SHTHLK  2008-10-15 - Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand(). Added canGenerateNumbers() to check security rights
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentBasicVarSet2 extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentBasicVarSet2");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
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

   private ASPTabContainer tabs;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;

   private String showGen;
   private String root_path;

   //===============================================================
   // Construction 
   //===============================================================
   public DocumentBasicVarSet2(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      trans     = null;
      val       = null;
      q         = null;
      cmd       = null;
      data      = null;
      showGen   = null;
      root_path = null;

      super.doReset();
   }
   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentBasicVarSet2 page = (DocumentBasicVarSet2)(super.clone(obj));

      // Initializing mutable attributes
      page.trans     = null;
      page.val       = null;
      page.q         = null;
      page.cmd       = null;
      page.data      = null;
      page.showGen   = null;
      page.root_path = null;

      // Cloning immutable attributes
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();

      page.headblk  = page.getASPBlock(headblk.getName());
      page.headset  = page.headblk.getASPRowSet();
      //page.item1tbl = page.getASPTable(item1tbl.getName());
      page.headbar  = page.headblk.getASPCommandBar();
      page.headlay  = page.headblk.getASPBlockLayout();

      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemlay0 = page.itemblk0.getASPBlockLayout();

      page.itemblk1 = page.getASPBlock(itemblk1.getName());
      page.itemset1 = page.itemblk1.getASPRowSet();
      page.itemtbl1 = page.getASPTable(itemtbl1.getName());
      page.itembar1 = page.itemblk1.getASPCommandBar();
      page.itemlay1 = page.itemblk1.getASPBlockLayout();

      page.itemblk2 = page.getASPBlock(itemblk2.getName());
      page.itemset2 = page.itemblk0.getASPRowSet();
      page.itemtbl2 = page.getASPTable(itemtbl2.getName());
      page.itembar2 = page.itemblk2.getASPCommandBar();
      page.itemlay2 = page.itemblk2.getASPBlockLayout();

      page.itemblk3 = page.getASPBlock(itemblk3.getName());
      page.itemset3 = page.itemblk3.getASPRowSet();
      page.itemtbl3 = page.getASPTable(itemtbl3.getName());
      page.itembar3 = page.itemblk3.getASPCommandBar();
      page.itemlay3 = page.itemblk3.getASPBlockLayout();

      page.itemblk4 = page.getASPBlock(itemblk4.getName());
      page.itemset4 = page.itemblk4.getASPRowSet();
      page.itemtbl4 = page.getASPTable(itemtbl4.getName());
      page.itembar4 = page.itemblk4.getASPCommandBar();
      page.itemlay4 = page.itemblk4.getASPBlockLayout();

      page.tabs      = page.getASPTabContainer();
      page.f         = page.getASPField(f.getName());

      return page;
   }



   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt   = mgr.newASPHTMLFormatter();

      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      showGen = ctx.readValue("SHOWGEN", "0");

      if (mgr.commandBarActivated())
      {
         eval(mgr.commandBarFunction());      
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.buttonPressed("GENERATE"))
         generate();

      tabs.saveActiveTab();//w.a.
      adjust();

      ctx.writeValue("SHOWGEN", showGen);

   }

   //=============================================================================
   //  Validation
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

   }

   //=============================================================================
   //  Command functions
   //=============================================================================

   public void  deleteAllNumbers()
   {
      String sBookingList;
      ASPManager mgr = getASPManager();

      if (itemset2.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NOROWSTODEL: No rows to delete."));         
      }
      else
      {

         if (headlay.isMultirowLayout())
         {
            itemset2.storeSelections();
            itemset2.setFilterOn();
         }
         else
            itemset2.selectRow();


         sBookingList = itemset1.getRow().getValue("BOOKING_LIST");

         trans.clear();
         cmd = trans.addCustomCommand("DELETEALLNUMS","DOC_NUM_BOOKING_DETAIL_API.Delete_All_Numbers");
         cmd.addParameter("BOOKING_LIST",sBookingList);

         trans = mgr.perform(trans);

         okFindITEM2();
      }
   }


   public void deleteAllNumbersOfBookingList()
   {
      ASPManager mgr = getASPManager();
      int noOfSelectedRows = 0;
      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         noOfSelectedRows = itemset1.countRows();
         if (noOfSelectedRows==0) {
            mgr.showError(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NORECSELECGENERATNUM: No rows selected."));
            return;
         }
      }
      else
      {
         itemset1.selectRow();
         noOfSelectedRows = 1;
      }
      trans.clear();
      for (int k=0;k<noOfSelectedRows;k++)
      {
         cmd = trans.addCustomCommand("DELETEALLNUMS"+k,"DOC_NUM_BOOKING_DETAIL_API.Delete_All_Numbers");
         cmd.addParameter("BOOKING_LIST",itemset1.getRow().getValue("BOOKING_LIST"));
         if (itemlay1.isMultirowLayout())
         {
            itemset1.next();
         }
      }
      trans = mgr.perform(trans);
      
      if (itemlay1.isMultirowLayout())
      {
         itemset1.first();
         for (int i=0;i<itemset1.countRows();i++) {
            itemset1.refreshRow();
            itemset1.next();
         }
         itemset1.setFilterOff();
      }
      else
      {
         itemset1.refreshRow();
         okFindITEM2();
      }
      
   }



   //=============================================================================
   // ITEM10
   //=============================================================================

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk0);

      if (itemset0.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NODATAFOUND: No data found"));
         itemset0.clear();
      }
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk0);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOC_SCALE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   }

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);

      if (itemset1.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NODATAFOUND: No data found"));
         itemset1.clear();
      }
      else
         okFindITEM2();

   }

   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk1);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }

   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_NUM_BOOKING_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int item1rowno = itemset1.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      //bug 58216 starts
      q.addWhereCondition("BOOKING_LIST = ?");
      q.addParameter("BOOKING_LIST",itemset1.getValue("BOOKING_LIST"));
      //bug 58216 end
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);

      itemset1.goTo(item1rowno);

   }

   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_NUM_BOOKING_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }

   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk2);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk4);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk4);

      if (itemset4.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NODATAFOUND: No data found"));
         itemset4.clear();
      }
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk4);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }


   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","DOC_NUMBER_COUNTER_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
   }



   public void  generate()
   {
      String sBookingList, sRemark,numRows;

      double  tem;
      ASPManager mgr = getASPManager();

      sBookingList = itemset1.getRow().getValue("BOOKING_LIST");
      numRows = mgr.readValue("NUMROWS");
      sRemark = mgr.readValue("SREMARK");

      tem = mgr.readNumberValue("NUMROWS");


      if (numRows==null)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2GENERATENULL: Number to generate should not be empty."));
      }

      else if (this.isNaN(tem))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2GENERATENOTNUM: Number to generate should be a number value."));      
      }

      else
      {
         trans.clear();
         cmd = trans.addCustomCommand("GENERATENUMS","DOC_NUM_BOOKING_DETAIL_API.Generate_Numbers");
         cmd.addParameter("BOOKING_LIST",sBookingList);
         cmd.addParameter("REMARK",sRemark);
         cmd.addParameter("NUM_ROWS", numRows);

         trans = mgr.perform(trans);
         itemset1.refreshRow();

         okFindITEM2();
      }

   }

   public void  generateNumbers()
   {
      showGen = "1";
      if (itemlay1.isMultirowLayout()) {
         itemset1.storeSelections();
         itemset1.goTo(itemset1.getRowSelected());
         okFindITEM2();// otherwise wrong record set can be displayed.:bakalk
         itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
      }
      

   }

   //Bug Id 70286, Start
   public boolean  canGenerateNumbers()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();

      trans.addSecurityQuery("DOC_NUM_BOOKING_DETAIL_API","Generate_Numbers");
      trans = mgr.perform(trans);
      ASPBuffer sec = trans.getSecurityInfo();

      if ((!sec.itemExists("DOC_NUM_BOOKING_DETAIL_API.Generate_Numbers")))
         return false;
      else
         return true;
   }
   //Bug Id 70286, End

   //=============================================================================
   // Tab activate functions
   //=============================================================================

   public void  activateScale()
   {

      tabs.setActiveTab(1);
   }

   // inoslk
   public void  activateNumberCounter()
   {
      tabs.setActiveTab(2);
   }

   //prsalk
   public void  activateBookingList()
   {
      // Interchanged the tab order 2 and 1.
      tabs.setActiveTab(3);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");
      headblk.disableDocMan();

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.NEWROW);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


      // tab commands
      headbar.addCustomCommand("activateScale", mgr.translate("DOCMAWDOCUMENTBASICVARSET2SCALE: Scale"));
      headbar.addCustomCommand("activateNumberCounter", mgr.translate("DOCMAWDOCUMENTBASICVARSET2NUMBERCOUNTER: Number Counter"));
      headbar.addCustomCommand("activateBookingList", mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKLIST: Booking List"));   


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 0 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk0 = mgr.newASPBlock("ITEM0");
      itemblk0.disableDocMan();

      f = itemblk0.addField("OBJID");
      f.setHidden();

      f = itemblk0.addField("OBJVERSION");
      f.setHidden();

      f = itemblk0.addField("SCALE");
      f.setSize(20);
      f.setMaxLength(30);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2SCALE: Scale");



      itemblk0.setView("DOC_SCALE");
      itemblk0.defineCommand("DOC_SCALE_API","New__,Modify__,Remove__");

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);

      itembar0.enableCommand(itembar0.FIND);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.disableCommand(itembar0.DUPLICATEROW);

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSET2SCALE: Scale"));

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(1);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);


      //
      //    Booking List  - Master
      //

      itemblk1 = mgr.newASPBlock("ITEM1");
      itemblk1.disableDocMan();

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("BOOKING_LIST");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(20);
      f.setUpperCase();
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2BOOKINGLIST: Booking List");

      f = itemblk1.addField("DESCRIPTION");
      f.setSize(35);
      f.setMaxLength(35);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2BOOKINGDESCRIPTION: Description");

      f = itemblk1.addField("ID1");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(10);
      f.setDynamicLOV("DOC_NUMBER_COUNTER_LOV");
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2NUMCOUNTID1: Number Counter ID 1");

      f = itemblk1.addField("ID2");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(30);
      f.setDynamicLOV("DOC_NUMBER_COUNTER");
      f.setLOVProperty("TITLE",mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKINGLOVID2: List of Number Counter ID 2"));
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2NUMCOUNTID2: Number Counter ID 2");

      f = itemblk1.addField("CURRENT_VALUE");
      f.setFunction("DOC_NUMBER_COUNTER_API.Get_Current_Value(ID1,ID2)");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(10);
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2CURRENTVALUE: Current Value");

      f = itemblk1.addField("GEN_NUM_AVAILABLE");
      f.setFunction("DOC_NUM_BOOKING_DETAIL_API.Check_Exist_Booking_List(BOOKING_LIST)");
      f.setHidden();


      itemblk1.setView("DOC_NUM_BOOKING");
      itemblk1.defineCommand("DOC_NUM_BOOKING_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.addCustomCommand("deleteAllNumbersOfBookingList",mgr.translate("DOCMAWDOCUMENTBASICVARSET2DELALLNUM: Delete All Booked Numbers..."));
      itembar1.addSecureCustomCommand("generateNumbers",mgr.translate("DOCMAWDOCUMENTBASICVARSET2GENNUM: Generate Numbers..."),"DOC_NUM_BOOKING_DETAIL_API.Generate_Numbers"); //Bug Id 70286

      itembar1.enableMultirowAction();
      itembar1.removeFromMultirowAction("generateNumbers");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKLIST: Booking List"));
      itemtbl1.enableRowSelect();//w.a.

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(2);
      itemlay1.setDefaultLayoutMode(itemlay1.SINGLE_LAYOUT);   

      //
      //    Booking List  - Details
      //

      itemblk2 = mgr.newASPBlock("ITEM2");
      itemblk2.disableDocMan();

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("ITEM2_BOOKING_LIST");
      f.setDbName("BOOKING_LIST");
      f.setHidden();

      f = itemblk2.addField("GENERATED_NUMBER");
      f.setSize(40);
      f.setMaxLength(40);
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2ITEM2NUMBER: Number");

      f = itemblk2.addField("REMARK");
      f.setSize(35);
      f.setMaxLength(35);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2REMARK: Remark");

      itemblk2.setView("DOC_NUM_BOOKING_DETAIL");
      itemblk2.defineCommand("DOC_NUM_BOOKING_DETAIL_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(itemblk1);
      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.disableCommand(itembar2.DUPLICATEROW);

      itembar2.disableCommand(itembar2.NEWROW);
      itembar2.disableCommand(itembar2.DELETE);
      itembar2.disableCustomCommand(itembar2.DELETE);

      itembar2.addCustomCommand("deleteAllNumbers",mgr.translate("DOCMAWDOCUMENTBASICVARSET2DELALLNUM: Delete All Booked Numbers..."));

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKLIST: Booking List"));

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);   


      //
      //    Generate Dialog
      //

      itemblk3 = mgr.newASPBlock("ITEM3");
      itemblk3.disableDocMan();

      itemblk3.addField("NUM_ROWS").
      setHidden().
      setFunction("''");   

      itemblk3.setView("DUAL");
      itemblk3.setTitle("Numbers to generate");
      eval(itemblk3.generateAssignments());
      itembar3 = mgr.newASPCommandBar(itemblk3);

      itemset3 = itemblk3.getASPRowSet();

      itemtbl3 = mgr.newASPTable(itemblk3);

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.CUSTOM_LAYOUT);
      itemlay3.setEditable();
      itemlay3.setDialogColumns(1);


      //
      // Number Counter
      //


      itemblk4 = mgr.newASPBlock("ITEM4");
      itemblk4.disableDocMan();

      f = itemblk4.addField("ITEM4_OBJID");
      f.setDbName("OBJID");
      f.setHidden();

      f= itemblk4.addField("ITEM4_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();

      f=itemblk4.addField("ITEM4_ID1");
      f.setDbName("ID1");
      f.setSize(10);
      f.setMaxLength(10);
      f.setUpperCase();
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2ID1: ID1");       

      f=itemblk4.addField("ITEM4_ID2");
      f.setDbName("ID2");
      f.setSize(10);
      f.setMaxLength(10);
      f.setUpperCase();
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();       
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2ID2: ID2");

      f=itemblk4.addField("ITEM4_START_VALUE");
      f.setDbName("START_VALUE");
      f.setReadOnly();
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2STARTVAL: Start Value");

      f= itemblk4.addField("ITEM4_LENGTH_SHOWN");
      f.setDbName("LENGTH_SHOWN");
      f.setMaxLength(2);
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2LENGTHSHOWN: Length Shown");

      f= itemblk4.addField("ITEM4_PREFIX");
      f.setDbName("PREFIX");
      f.setSize(10);
      f.setMaxLength(10);
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2PREFIX: Prefix");

      f= itemblk4.addField("ITEM4_SUFFIX");
      f.setDbName("SUFFIX");
      f.setSize(10);
      f.setMaxLength(10);
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2SUFFIX: Suffix");

      f= itemblk4.addField("ITEM4_CURRENT_VALUE");
      f.setDbName("CURRENT_VALUE");
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2CURRVAL: Current Value");

      f=itemblk4.addField("ITEM4_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setMandatory();
      f.setSize(35);
      f.setMaxLength(35);
      f.setLabel("DOCMAWDOCUMENTBASICVARSET2DESC: Description");

      itemblk4.setView("DOC_NUMBER_COUNTER");
      itemblk4.defineCommand("DOC_NUMBER_COUNTER_API","New__,Modify__,Remove__");
      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND, "okFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW, "newRowITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND, "countFindITEM4");

      itembar4.enableCommand(itembar4.EDITROW);
      itembar4.enableCommand(itembar4.DUPLICATEROW);
      itembar4.enableCommand(itembar4.DELETE);
      
      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NUMCOUNT: Number Counter"));

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      itemlay4.setFieldOrder("ITEM4_OBJID,ITEM4_OBJVERSION,ITEM4_ID1,ITEM4_ID2,ITEM4_DESCRIPTION,ITEM4_START_VALUE,ITEM4_LENGTH_SHOWN,ITEM4_PREFIX,ITEM4_SUFFIX,ITEM4_CURRENT_VALUE");


      //---------------------------------------------------------------------
      //-------------- DEFINITIONS OF TABS ----------------------------------
      //---------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSET2SCALE: Scale"),"javascript:commandSet('HEAD.activateScale','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NUMBERCOUNTER: Number Counter"),"javascript:commandSet('HEAD.activateNumberCounter','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKLIST: Booking List"),"javascript:commandSet('HEAD.activateBookingList','')");   

      tabs.setContainerWidth(700);   
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

   }


   public void  adjust()
   {

      if (itemset1.countRows() == 0)
      {
         itembar1.disableCommand(itembar1.BACK);
         itembar1.disableCommand(itembar1.DUPLICATEROW);
         itembar1.disableCommand(itembar1.DELETE);
         itembar1.disableCommand(itembar1.FORWARD);
         itembar1.disableCommand(itembar1.BACKWARD);       
      }

      if (itemset2.countRows()==0 && itemlay1.isSingleLayout()) {// no rows in generated numbers and booking list in detail mode
         itembar1.disableCommand("generateNumbers");
      }
      else{
         itembar1.enableCommand("generateNumbers");
      }

      itembar1.addCommandValidConditions("deleteAllNumbersOfBookingList","GEN_NUM_AVAILABLE","Enable","TRUE");
      //Bug Id 70286, Start      
      if ((itemset2.countRows() == 0) && canGenerateNumbers())
         showGen = "1";
      //Bug Id 70286, End
   }

   /*public String  tabsInit()
   {
      ASPManager mgr = getASPManager();

      tabs.setActiveTab(Integer.valueOf(activetab,10).intValue()+1);

      return tabs.showTabsInit();
   }*/


   public String  tabsFinish()
   {
      ASPManager mgr = getASPManager();

      return tabs.showTabsFinish();
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTBASICVARSET2TITLE: Basic Data - Extended Various Settings";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTBASICVARSET2TITLE: Basic Data - Extended Various Settings";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCUMENTBASICVARSET2TITLE: Basic Data - Extended Various Settings"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("DOCMAWDOCUMENTBASICVARSET2TITLE: Basic Data - Extended Various Settings"));
      out.append(tabs.showTabsInit());

      if (tabs.getActiveTab()==1)
      {
         out.append(itemlay0.show());
      }
      else if (tabs.getActiveTab()==2)
      {
         out.append(itemlay4.show());
      }

      else if (tabs.getActiveTab()==3)
      {
         out.append(itemlay1.show()); 
         if ((itemlay1.isSingleLayout()||itemlay1.isCustomLayout())&&(itemset1.countRows() != 0))
         {

            if (("1".equals(showGen))) //Bug Id 70286
            {
               out.append(itembar2.showBar());

               out.append("       <td colspan=3 rowspan=1 align=\"left\" valign=\"top\" height=\"30\">");
               out.append(fmt.drawWriteLabel(mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKLISTNUMSTOGEN: Numbers to generate:")));
               out.append("  <table>\n");
               out.append("  </table>\n");
               out.append("  <table>\n");
               out.append("  </table>\n");
               out.append("          <td>");
               out.append(fmt.drawWriteLabel("DOCMAWDOCUMENTBASICVARSET2BOOKLISTHOWMANY: How many numbers?"));
               out.append("</td>\n");
               out.append("          <td>");
               out.append(fmt.drawTextField("NUMROWS","",""));
               //out.append(fmt.drawTextField("NUMROWS",n_rowst,""));
               out.append("&nbsp;&nbsp;\n");
               out.append("</td>\n");
               out.append("        </tr>\n");
               out.append("        <tr>\n");
               out.append("          <td>");
               out.append(fmt.drawWriteLabel("DOCMAWDOCUMENTBASICVARSET2BOOKLISTREMARK: Remark"));
               out.append("</td>\n");
               out.append("          <td>");
               out.append(fmt.drawTextField("SREMARK","","",35,35));
               out.append("&nbsp;&nbsp;\n");
               out.append("</td>\n");
               out.append("          <td align=\"right\">");
               out.append("</td>\n");


               out.append(fmt.drawSubmit("GENERATE",mgr.translate("DOCMAWDOCUMENTBASICVARSET2BOOKGENERATE:  Generate"),""));
               out.append("&nbsp;\n");
               out.append("       </td>\n");
               out.append(itemlay3.generateDialog());
               out.append("       </tr>\n");

               out.append(itemtbl2.populate());
               showGen = "0";
               ctx.writeValue("SHOWGEN", showGen);

            }

            else if (itemset2.countRows() != 0)
            {
               out.append(itemlay2.show()); 
            }
         }
      }

      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      appendDirtyJavaScript("function lovId2(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var key_value = (getValue_('ID2',i).indexOf('%') !=-1)? getValue_('ID2',i):'';\n");
      appendDirtyJavaScript("   openLOVWindow('ID2',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_NUMBER_COUNTER&__FIELD=Number+Counter+ID+2&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('ID2',i))\n");
      appendDirtyJavaScript("   + '&ID2=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("     + '&ID1=' + URLClientEncode(getValue_('ID1',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateId2');\n");
      appendDirtyJavaScript("}\n");

      out.append(tabs.showTabsFinish());
      out.append(mgr.endPresentation());
      appendDirtyJavaScript(" //=======client methods==============\n");
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
