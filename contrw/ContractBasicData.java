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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.contrw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ContractBasicData extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.BudgetBasicData");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock budget_report_type_blk;
   private ASPRowSet budget_report_type_set;
   private ASPCommandBar budget_report_type_bar;
   private ASPTable budget_report_type_tbl;
   private ASPBlockLayout budget_report_type_lay;  
   
   private ASPBlock budget_special_type_blk;
   private ASPRowSet budget_special_type_set;
   private ASPCommandBar budget_special_type_bar;
   private ASPTable budget_special_type_tbl;
   private ASPBlockLayout budget_special_type_lay; 
   
   private ASPTabContainer tabs;
   private int activetab; 

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ContractBasicData (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("TYPE_ID")) )
         okFind();
      else 
         okFind();
      tabs.saveActiveTab();  
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("BUDGETCHARGESTYPENODATA: No data found.");
         headset.clear();
      }
   }



   public void countFind()
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



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","BUDGET_CHARGES_TYPE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void okFindItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(budget_report_type_blk);
      q = trans.addQuery(budget_report_type_blk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,budget_report_type_blk);
      if (  budget_report_type_set.countRows() == 0 )
      {
         mgr.showAlert("BUDGETREPORTTYPENODATA: No data found.");
         budget_report_type_set.clear();
      }
   }



   public void countFindItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(budget_report_type_blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      budget_report_type_lay.setCountValue(toInt(budget_report_type_set.getValue("N")));
      budget_report_type_set.clear();
   }



   public void newRowItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","BUDGET_REPORT_TYPE_API.New__",budget_report_type_blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      budget_report_type_set.addRow(data);
   }
   
   public void okFindItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(budget_special_type_blk);
      q = trans.addQuery(budget_special_type_blk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,budget_special_type_blk);
      if (  budget_special_type_set.countRows() == 0 )
      {
         mgr.showAlert("BUDGETSPECIALTYPENODATA: No data found.");
         budget_special_type_set.clear();
      }
   }



   public void countFindItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(budget_special_type_blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      budget_special_type_lay.setCountValue(toInt(budget_special_type_set.getValue("N")));
      budget_special_type_set.clear();
   }
   public void newRowItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","BUDGET_SPECIAL_TYPE_API.New__",budget_special_type_blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);    
      data = trans.getBuffer("HEAD/DATA");
      budget_special_type_set.addRow(data);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("TYPE_ID").
              setMandatory().
              setInsertable().
              setLabel("BUDGETCHARGESTYPETYPEID: Type Id").
              setSize(20);              
      headblk.addField("TYPE_NAME").
              setMandatory().
              setInsertable().
              setLabel("BUDGETCHARGESTYPETYPENAME: Type Name").
              setSize(20);
      headblk.setView("BUDGET_CHARGES_TYPE");
      headblk.defineCommand("BUDGET_CHARGES_TYPE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateChargeType", "Charge Type...");  
      headbar.addCustomCommand("activateReportType", "Report Type...");
      headbar.addCustomCommand("activateSpecialType", "Special Type...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("BUDGETCHARGESTYPETBLHEAD: Budget Charges Types");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      
      budget_report_type_blk = mgr.newASPBlock("ITEM1");
      budget_report_type_blk.addField("ITEM1_OBJID"). 
                             setDbName("OBJID").
                             setHidden();
      budget_report_type_blk.addField("ITEM1_OBJVERSION").
                             setDbName("OBJVERSION").
                             setHidden();
      budget_report_type_blk.addField("PROJECT_TYPE_ID").
                             setMandatory().
                             setInsertable().
                             setDynamicLOV("PROJECT_TYPE").
                             setLabel("BUDGETREPORTTYPEPROJECTTYPEID: Project Type Id").
                             setSize(20);     
      budget_report_type_blk.addField("PROJECT_TYPE_NAME").
                             setFunction("PROJECT_TYPE_API.GET_PROJECT_TYPE_NAME(:PROJECT_TYPE_ID)").
                             setLabel("BUDGETREPORTTYPEPROJECTTYPENAME: Project Type Name").
                             setReadOnly().
                             setSize(20);    
      mgr.getASPField("PROJECT_TYPE_ID").setValidation("PROJECT_TYPE_NAME");  
      budget_report_type_blk.addField("REPORT_TYPE_ID").
                             setMandatory().
                             setInsertable().
                             setLabel("BUDGETREPORTTYPEREPORTTYPEID: Report Type Id").
                             setSize(20);
      budget_report_type_blk.addField("REPORT_TYPE_DESC").
                             setMandatory().
                             setInsertable().
                             setLabel("BUDGETREPORTTYPEREPORTTYPEDESC: Report Type Desc").
                             setSize(20);      
      budget_report_type_blk.setView("BUDGET_REPORT_TYPE");
      budget_report_type_blk.defineCommand("BUDGET_REPORT_TYPE_API","New__,Modify__,Remove__");
      budget_report_type_set = budget_report_type_blk.getASPRowSet();
      budget_report_type_bar = mgr.newASPCommandBar(budget_report_type_blk);
      budget_report_type_tbl = mgr.newASPTable(budget_report_type_blk);
      budget_report_type_bar.defineCommand(budget_report_type_bar.OKFIND,"okFindItem1");
      budget_report_type_bar.defineCommand(budget_report_type_bar.NEWROW,"newRowItem1");
      budget_report_type_bar.defineCommand(budget_report_type_bar.COUNTFIND,"countFindItem1");
      budget_report_type_tbl.setTitle("BUDGETREPORTTYPETBLHEAD: Budget Report Types");
      budget_report_type_tbl.enableRowSelect();
      budget_report_type_tbl.setWrap();
      budget_report_type_lay = budget_report_type_blk.getASPBlockLayout();
      budget_report_type_lay.setDefaultLayoutMode(budget_report_type_lay.MULTIROW_LAYOUT);
      
      budget_special_type_blk = mgr.newASPBlock("ITEM2");
      budget_special_type_blk.addField("ITEM2_OBJID").
                              setDbName("OBJID").
                              setHidden();
      budget_special_type_blk.addField("ITEM2_OBJVERSION").
                              setDbName("OBJVERSION").
                              setHidden();
      budget_special_type_blk.addField("SPECIAL_NO").
                              setMandatory().
                              setInsertable().
                              setLabel("BUDGETSPECIALTYPESPECIALNO: Special No").
                              setSize(20);
      budget_special_type_blk.addField("SPECIAL_NAME").
                              setMandatory().
                              setInsertable().
                              setLabel("BUDGETSPECIALTYPESPECIALNAME: Special Name").
                              setSize(20);
      budget_special_type_blk.addField("CREATE_TIME","Date").
                              setInsertable().
                              setHidden().
                              setLabel("BUDGETSPECIALTYPECREATETIME: Create Time").
                              setSize(30);
      budget_special_type_blk.addField("CREATE_PERSON").
                              setInsertable().
                              setHidden().  
                              setLabel("BUDGETSPECIALTYPECREATEPERSON: Create Person").
                              setSize(20);
      budget_special_type_blk.setView("BUDGET_SPECIAL_TYPE");
      budget_special_type_blk.defineCommand("BUDGET_SPECIAL_TYPE_API","New__,Modify__,Remove__");
      budget_special_type_set = budget_special_type_blk.getASPRowSet();
      budget_special_type_bar = mgr.newASPCommandBar(budget_special_type_blk);
      budget_special_type_bar.defineCommand(budget_special_type_bar.OKFIND,"okFindItem2");
      budget_special_type_bar.defineCommand(budget_special_type_bar.NEWROW,"newRowItem2");
      budget_special_type_bar.defineCommand(budget_special_type_bar.COUNTFIND,"countFindItem2");
      budget_special_type_tbl = mgr.newASPTable(budget_special_type_blk);  
      budget_special_type_tbl.setTitle("BUDGETSPECIALTYPETBLHEAD: Budget Special Types");
      budget_special_type_tbl.enableRowSelect();
      budget_special_type_tbl.setWrap();
      budget_special_type_lay = budget_special_type_blk.getASPBlockLayout();
      budget_special_type_lay.setDefaultLayoutMode(budget_special_type_lay.MULTIROW_LAYOUT);
   
   
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("BUDGETCHARGESTYPE: Charge Type"), "javascript:commandSet('MAIN.activateChargeType','')");
      tabs.addTab(mgr.translate("BUDGETREPORTTYPE: Report Type"), "javascript:commandSet('MAIN.activateReportType','')");
      tabs.addTab(mgr.translate("BUDGETSPECIALTYPE: Special Type"), "javascript:commandSet('MAIN.activateSpecialType','')");
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);
   }

   public void activateChargeType()
   {   
      tabs.setActiveTab(1);
   }

   public void activateReportType()
   {   
      tabs.setActiveTab(2);
      okFindItem1();

   }

   public void activateSpecialType()
   {   
      tabs.setActiveTab(3);  
      okFindItem2();    
   }  

   public void  adjust()
   {
       // fill function body
       headbar.removeCustomCommand("activateChargeType");
       headbar.removeCustomCommand("activateReportType");
       headbar.removeCustomCommand("activateSpecialType");
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "BUDGETBUDGETBASICDATAEDESC: Budget Basic Data";
   }


   protected String getTitle()
   {
      return "BUDGETBUDGETBASICDATATITLE: Budget Basic Data";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(tabs.showTabsInit());

      if (tabs.getActiveTab() == 1 )
         appendToHTML(headlay.show());
      else if (tabs.getActiveTab() == 2 )
         appendToHTML(budget_report_type_lay.show());
      else if (tabs.getActiveTab() == 3 )      
         appendToHTML(budget_special_type_lay.show());
      appendToHTML(tabs.showTabsFinish());    
   }   
}
  