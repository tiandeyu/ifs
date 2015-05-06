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
*  File        : PrinterDefinition.java 
*  Modified    :  
*                 2008-12-01  - DUSDLK - Bug 78560, removed setDbName() property to fields that contained the setFunction property and added setMandatory().
*                 2004-06-15  - ThAblk Merged LCS Patch 44351 and added a new method saveReturnAndRefresh and corrected
*                               moving to the last record when saving in new 3 methods added in below bug.
*                               2004-06-01  - HIPELK = Bug ID 44351, Added 3 methods saveReturnITEM1(), saveReturnITEM2() and
*                                                      saveReturnITEM3(), which refreshes the rowsets everytime new records are saved.
*                 2003-12-16  - Gacolk - Web Alignment: Converted the 4 blocks to tabs and removed
*                                        obsolete methods.
*                 2003-10-12  - Call ID 106476, Did a few changes to the User ID fields.
*                 2001-08-21  - CHCR - Removed depreciated methods.
*                 2001-05-17  - VAGU -Code Review
*  ASP2JAVA Tool  2001-03-12  - Created Using the ASP file PrinterDefinition.asp
*                 2001-03-13  - Indra Rodrigo - Java conversion.
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PrinterDefinition extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.PrinterDefinition");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
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
   private ASPField f;

   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   //===============================================================
   // Construction 
   //===============================================================
   public PrinterDefinition(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
   	 okFind();  
   	 okFindITEM1(); 
   	 okFindITEM2(); 
   	 okFindITEM3(); 
      }

      tabs.saveActiveTab();
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("HEAD","LOGICAL_PRINTER_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM2","REPORT_USER_PRINTER_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM2","REPORT_PRINTER_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }


   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addEmptyCommand("ITEM2","USER_PRINTER_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);
   }

   public void  saveReturnAndRefresh(ASPRowSet rowSet)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int currow = rowSet.getCurrentRowNo();
      
      rowSet.changeRow();
      mgr.submit(trans);
      
      rowSet.first();
      for (int i = 0; i < rowSet.countRows(); i++) 
      {
         rowSet.refreshRow();
         rowSet.next();
      }
      rowSet.goTo(currow);
   }

   public void  saveReturnITEM1()
   {
      saveReturnAndRefresh(itemset1);
   }

   public void  saveReturnITEM2()
   {
      saveReturnAndRefresh(itemset2);
   }

   public void  saveReturnITEM3()
   {
      saveReturnAndRefresh(itemset3);
   }
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.submit(trans);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWPRINTERDEFINITIONNODATA: No data found."));
         headset.clear();
      } 
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
   }


   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
   }


   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.submit(trans);
   
      if ( itemset1.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWPRINTERDEFINITIONNODATA: No data found."));
         itemset1.clear();
      }
      headset.goTo(currrow);
      itemset2.goTo(itemrrow2);
      itemset3.goTo(itemrrow3);
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow3 = itemset3.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(itemblk2);
      q.includeMeta("ALL");
      mgr.submit(trans);
   
      if ( itemset2.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWPRINTERDEFINITIONNODATA: No data found."));
         itemset2.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset3.goTo(itemrrow3);
   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
   }


   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   	  
      int currrow = headset.getCurrentRowNo();
      int itemrrow1 = itemset1.getCurrentRowNo();
      int itemrrow2 = itemset2.getCurrentRowNo();
   
      ASPQuery q = trans.addQuery(itemblk3);
      q.includeMeta("ALL");
      mgr.submit(trans);
   
      if ( itemset3.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("APPSRWPRINTERDEFINITIONNODATA: No data found."));
         itemset3.clear();
      }
      headset.goTo(currrow);
      itemset1.goTo(itemrrow1);
      itemset2.goTo(itemrrow2);
   }

   public void activateLogical()
   {   
      tabs.setActiveTab(1);   
   }

   public void activateReportUser()
   {   
      tabs.setActiveTab(2);   
   }

   public void activateReport()
   {   
      tabs.setActiveTab(3);   
   }

   public void activateUser()
   {   
      tabs.setActiveTab(4);   
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      f = headblk.addField("OBJID");
      f.setHidden();
   
      f = headblk.addField("OBJVERSION");
      f.setHidden();
   
      f = headblk.addField("PRINTER_ID");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(30);
      f.setLabel("APPSRWPRINTERDEFINITIONPRINTERID: Printer Id");
   
      f = headblk.addField("DESCRIPTION");
      f.setSize(50);
      f.setMandatory();
      f.setInsertable();
      f.setMaxLength(50);
      f.setLabel("APPSRWPRINTERDEFINITIONDESCRIPTION: Description");
   
      headblk.setView("LOGICAL_PRINTER");
      headblk.defineCommand("LOGICAL_PRINTER_API","New__,Modify__,Remove__");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

      headbar.addCustomCommand("activateLogical","Logical Printer");
      headbar.addCustomCommand("activateReportUser","Report User Printer");
      headbar.addCustomCommand("activateReport","Report");
      headbar.addCustomCommand("activateUser","User");

      headbar.removeCustomCommand("activateLogical");
      headbar.removeCustomCommand("activateReportUser");
      headbar.removeCustomCommand("activateReport");
      headbar.removeCustomCommand("activateUser");
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWPRINTERDEFINITIONTBL1: Logical Printer"));
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to REPORT USER PRINTER TAB ------------------------
   //-----------------------------------------------------------------------
   
      itemblk1 = mgr.newASPBlock("ITEM1");
   
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk1.addField("REPORT_ID");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(30);
      f.setDynamicLOV("REPORT_DEFINITION",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONREPORTDEFINITION: Report Id"));
      f.setLabel("APPSRWPRINTERDEFINITIONREPORTID: Report Id"); 
   
      f = itemblk1.addField("REPORT_TITLE");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("APPSRWPRINTERDEFINITIONREPORTTITLE: Report Title");
      f.setFunction("REPORT_DEFINITION_API.Get_Report_Title(:REPORT_ID)");
      mgr.getASPField("REPORT_ID").setValidation("REPORT_TITLE");
   
      f = itemblk1.addField("USER_ID");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setUpperCase();
      f.setMaxLength(30);
      f.setDynamicLOV("APPLICATION_USER",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONAPPLICATIONUSER: User Id"));
      f.setLabel("APPSRWPRINTERDEFINITIONUSERID: User Id"); 
   
      f = itemblk1.addField("AVAILABLE_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(30);
      f.setLabel("APPSRWPRINTERDEFINITIONAVAILABLEPRINTER: Available Printer");
      f.setDynamicLOV("LOGICAL_PRINTER",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONLOGICALPRINTER: Logical Printer"));
   
      f = itemblk1.addField("PRINTER_DESCRIPTION");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("APPSRWPRINTERDEFINITIONPRINTERDESCRIPTION: Description");
      f.setFunction("LOGICAL_PRINTER_API.Get_Description(:AVAILABLE_PRINTER)");
      mgr.getASPField("AVAILABLE_PRINTER").setValidation("PRINTER_DESCRIPTION");
   
      f = itemblk1.addField("DEFAULT_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setMaxLength(5);
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("APPSRWPRINTERDEFINITIONDEFAULTPRINTER: Default");
   
      itemblk1.setView("REPORT_USER_PRINTER");
      itemblk1.defineCommand("REPORT_USER_PRINTER_API","New__,Modify__,Remove__");
   
      itemset1 = itemblk1.getASPRowSet();
   
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1"); 
      itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1","checkItem1Fields(-1)");
      itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");
   
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWPRINTERDEFINITIONTBL2: Report User Printer")); 
   
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to REPORT PRINTER TAB ------------------------
   //-----------------------------------------------------------------------	
   
      itemblk2 = mgr.newASPBlock("ITEM2");
   
      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
   
      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
   
      f = itemblk2.addField("ITEM2_REPORT_ID");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(30);
      f.setDbName("REPORT_ID");
      f.setDynamicLOV("REPORT_DEFINITION",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONREPORTDEFINITION: Report Id"));
      f.setLabel("APPSRWPRINTERDEFINITIONITEM2_REPORTID: Report Id"); 
   
      f = itemblk2.addField("ITEM2_REPORT_TITLE");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("APPSRWPRINTERDEFINITIONITEM2_REPORTTITLE: Report Title");
      f.setFunction("REPORT_DEFINITION_API.Get_Report_Title(:ITEM2_REPORT_ID)");
      mgr.getASPField("ITEM2_REPORT_ID").setValidation("ITEM2_REPORT_TITLE");
   
      f = itemblk2.addField("ITEM2_AVAILABLE_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setReadOnly();
      f.setMandatory();
      f.setMaxLength(30);
      f.setDbName("AVAILABLE_PRINTER");
      f.setLabel("APPSRWPRINTERDEFINITIONITEM2_AVAILABLEPRINTER: Available Printer");
      f.setDynamicLOV("LOGICAL_PRINTER",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONLOGICALPRINTER: Logical Printer"));
   
      f = itemblk2.addField("ITEM2_PRINTER_DESCRIPTION");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("APPSRWPRINTERDEFINITIONITEM2_PRINTERDESCRIPTION: Description");
      f.setFunction("LOGICAL_PRINTER_API.Get_Description(:ITEM2_AVAILABLE_PRINTER)");
      mgr.getASPField("ITEM2_AVAILABLE_PRINTER").setValidation("ITEM2_PRINTER_DESCRIPTION");
   
      f = itemblk2.addField("ITEM2_DEFAULT_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setMaxLength(5);
      f.setDbName("DEFAULT_PRINTER");
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("APPSRWPRINTERDEFINITIONITEM2_DEFAULTPRINTER: Default");
   
      itemblk2.setView("REPORT_PRINTER");
      itemblk2.defineCommand("REPORT_PRINTER_API","New__,Modify__,Remove__");
   
      itemset2 = itemblk2.getASPRowSet();
   
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2","checkItem2Fields(-1)");
      itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");
   
   
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("APPSRWPRINTERDEFINITIONTBL3: Report Printer")); 
   
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      itemlay2.setDialogColumns(1);
   
   //-----------------------------------------------------------------------
   //-------------- This part belongs to USER PRINTER ------------------------
   //-----------------------------------------------------------------------
   
   
      itemblk3 = mgr.newASPBlock("ITEM3");
   
      f = itemblk3.addField("ITEM3_OBJID");
      f.setDbName("OBJID");
      f.setHidden();
   
      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();
   
      f = itemblk3.addField("ITEM3_USER_ID");
      f.setSize(30);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setUpperCase();
      f.setMaxLength(30);
      f.setDbName("USER_ID");
      f.setDynamicLOV("APPLICATION_USER",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONAPPLICATIONUSER: User Id"));
      f.setLabel("APPSRWPRINTERDEFINITIONITEM3_USERID: User Id"); 
   
      f = itemblk3.addField("ITEM3_AVAILABLE_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setReadOnly();
      f.setMandatory();
      f.setMaxLength(30);
      f.setDbName("AVAILABLE_PRINTER");
      f.setLabel("APPSRWPRINTERDEFINITIONITEM3_AVAILABLEPRINTER: Available Printer");
      f.setDynamicLOV("LOGICAL_PRINTER",600,450);
      f.setLOVProperty("TITLE",mgr.translate("APPSRWPRINTERDEFINITIONLOGICALPRINTER: Logical Printer"));
   
   
      f = itemblk3.addField("ITEM3_PRINTER_DESCRIPTION");
      f.setSize(50);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("APPSRWPRINTERDEFINITIONITEM3_PRINTERDESCRIPTION: Description");
      f.setFunction("LOGICAL_PRINTER_API.Get_Description(:ITEM3_AVAILABLE_PRINTER)");
      mgr.getASPField("ITEM3_AVAILABLE_PRINTER").setValidation("ITEM3_PRINTER_DESCRIPTION");
   
      f = itemblk3.addField("ITEM3_DEFAULT_PRINTER");
      f.setSize(30);
      f.setInsertable();
      f.setMaxLength(5);
      f.setDbName("DEFAULT_PRINTER");
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("APPSRWPRINTERDEFINITIONITEM3_DEFAULTPRINTER: Default");
   
      itemblk3.setView("USER_PRINTER");
      itemblk3.defineCommand("USER_PRINTER_API","New__,Modify__,Remove__");
   
      itemset3 = itemblk3.getASPRowSet();
   
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnITEM3","checkItem3Fields(-1)");
      itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");
   
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("APPSRWPRINTERDEFINITIONTBL4: User Printer"));    
   
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      itemlay3.setDialogColumns(1);


      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("APPSRWPRINTERDEFINITIONLOGICAL: Logical Printer"),"javascript: commandSet('HEAD.activateLogical','')");
      tabs.addTab(mgr.translate("APPSRWPRINTERDEFINITIONRPTUSER: Report User Printer"),"javascript:commandSet('HEAD.activateReportUser','')");
      tabs.addTab(mgr.translate("APPSRWPRINTERDEFINITIONRPT: Report Printer"),"javascript: commandSet('HEAD.activateReport','')");
      tabs.addTab(mgr.translate("APPSRWPRINTERDEFINITIONUSER: User Printer"),"javascript:commandSet('HEAD.activateUser','')");
      
      tabs.setTabWidth(100); 
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "APPSRWPRINTERDEFINITIONTITLE: Printer Definition";
   }

   protected String getTitle()
   {
      return "APPSRWPRINTERDEFINITIONTITLE: Printer Definition";
   }

   protected void printContents() throws FndException
   {      
      appendToHTML(tabs.showTabsInit()); 
      switch (tabs.getActiveTab()) 
      {
         case 1:
         {
            appendToHTML(headlay.show());
            break;
         }
         case 2:
         {
            appendToHTML(itemlay1.show());
            break;
         }
         case 3:
         {
            appendToHTML(itemlay2.show());
            break;
         }
         case 4:
         {
            appendToHTML(itemlay3.show());
            break;
         }
      }
      appendToHTML(tabs.showTabsFinish()); 
   }

}
