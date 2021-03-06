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

package ifs.hsemaw;
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

public class HseBaseDataLec extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseBaseDataLec");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock hse_job_blk;
   private ASPRowSet hse_job_set;
   private ASPCommandBar hse_job_bar;
   private ASPTable hse_job_tbl;
   private ASPBlockLayout hse_job_lay;
   
   private ASPBlock hse_special_equ_blk;
   private ASPRowSet hse_special_equ_set;
   private ASPCommandBar hse_special_equ_bar;
   private ASPTable hse_special_equ_tbl;
   private ASPBlockLayout hse_special_equ_lay;
   
   private ASPBlock ProjectTypeblk;
   private ASPRowSet ProjectTypeset;
   private ASPCommandBar ProjectTypebar;
   private ASPTable ProjectTypetbl;
   private ASPBlockLayout ProjectTypelay;
   
   private ASPBlock HseAccGradeblk;
   private ASPRowSet HseAccGradeset;
   private ASPCommandBar HseAccGradebar;
   private ASPTable HseAccGradetbl;
   private ASPBlockLayout HseAccGradelay;
   
   private ASPTabContainer tabs;
   private ASPTabContainer item_tabs;
   private int activetab;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseBaseDataLec (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else 
         okFind();
      tabs.saveActiveTab();
      item_tabs.saveActiveTab(); 
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
         mgr.showAlert("HSEBASEDATALECNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","HSE_BASE_DATA_LEC_API.New__",headblk);
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

      mgr.createSearchURL(hse_job_blk);
      q = trans.addQuery(hse_job_blk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,hse_job_blk);
      if (  hse_job_set.countRows() == 0 )
      {
         mgr.showAlert("HSEJOBNODATA: No data found.");
         hse_job_set.clear();
      }
   }
   
   public void countFindItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(hse_job_blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      hse_job_lay.setCountValue(toInt(hse_job_set.getValue("N")));
      hse_job_set.clear();
   }



   public void newRowItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","HSE_JOB_API.New__",hse_job_blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      hse_job_set.addRow(data);
   }

   public void okFindItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(hse_special_equ_blk);
      q = trans.addQuery(hse_special_equ_blk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,hse_special_equ_blk);
      if (  hse_special_equ_set.countRows() == 0 )
      {
         mgr.showAlert("HSESPECIALEQUNODATA: No data found.");
         hse_special_equ_set.clear();
      }
   }
   
   public void countFindItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(hse_special_equ_blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      hse_special_equ_lay.setCountValue(toInt(hse_special_equ_set.getValue("N")));
      hse_special_equ_set.clear();
   }

   public void newRowItem2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","HSE_SPECIAL_EQU_API.New__",hse_special_equ_blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      hse_special_equ_set.addRow(data);
   }

   
   public void okFindItem3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(ProjectTypeblk);
      q = trans.addQuery(ProjectTypeblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,ProjectTypeblk);
      if (  ProjectTypeset.countRows() == 0 )
      {
         mgr.showAlert("HSEPROJECTTYPENODATA: No data found.");
         ProjectTypeset.clear();
      }
   }



   public void countFindItem3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(ProjectTypeblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      ProjectTypelay.setCountValue(toInt(ProjectTypeset.getValue("N")));
      ProjectTypeset.clear();
   }



   public void newRowItem3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("ITEM3","PROJECT_TYPE_API.New__",ProjectTypeblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      ProjectTypeset.addRow(data);
   }
   
   
   public void okFindItem4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      mgr.createSearchURL(HseAccGradeblk);
      q = trans.addQuery(HseAccGradeblk);
      q.addWhereCondition("PROJECT_TYPE_NO = ?");
      q.addParameter("PROJECT_TYPE_NO", ProjectTypeset.getValue("PROJECT_TYPE_NO"));
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,HseAccGradeblk);
      if (  HseAccGradeset.countRows() == 0 )
      {
         mgr.showAlert("HSEACCGRADENODATA: No data found.");
         HseAccGradeset.clear();
      }
   }
   public void newRowItem4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      
      cmd = trans.addEmptyCommand("ITEM4","HSE_ACC_GRADE_API.New__",HseAccGradeblk);
      cmd.setParameter("PROJECT_TYPE_NO", ProjectTypeset.getValue("PROJECT_TYPE_NO"));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      HseAccGradeset.addRow(data);
   }   

   public void countFindItem4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(HseAccGradeblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      HseAccGradelay.setCountValue(toInt(HseAccGradeset.getValue("N")));
      HseAccGradeset.clear();
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
      headblk.addField("ID").
              setHidden().
              setLabel("HSEBASEDATALECID: Id").
              setSize(200);
      headblk.addField("HSE_LEC").
              enumerateValues("Hse_Lec_API").
              setSelectBox().
              setMandatory().
              setInsertable().
              setLabel("HSEBASEDATALECHSELEC: Hse Lec").
              setSize(100);
      headblk.addField("SCORE").
              setInsertable().
              setLabel("HSEBASEDATALECSCORELEVEL: Score/Level").
              setSize(30);      
      headblk.addField("LEC_DESC").
              setInsertable().
              setLabel("HSEBASEDATALECLECDESC: Lec Desc").
              setSize(120);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("HSEBASEDATALECNOTE: Note").
              setHeight(3).
              setSize(120);     
      headblk.setView("HSE_BASE_DATA_LEC");
      headblk.defineCommand("HSE_BASE_DATA_LEC_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateHseBaseDataLec", "Hse Base Data Lec");  
      headbar.addCustomCommand("activateHseJob", "Hse Job");
      headbar.addCustomCommand("activateHseSpecialEqu", "Hse Special Equ");
      headbar.addCustomCommand("activateHseProjectType", "Hse Project Type");
      headbar.addCustomCommand("activateHseAccGrade", "Hse Acc Grade");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSEBASEDATALECTBLHEAD: Hse Base Data Lecs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDataSpan("LEC_DESC", 5);
      headlay.setDataSpan("NOTE",5);
      
      hse_job_blk = mgr.newASPBlock("ITEM1");
      hse_job_blk.addField("OBJID1").
                  setDbName("OBJID").
                  setHidden();
      hse_job_blk.addField("OBJVERSION1").
                  setDbName("OBJVERSION").
                  setHidden();
      hse_job_blk.addField("JOB_NO").
                  setMandatory().
                  setInsertable().
                  setLabel("HSEJOBJOBNO: Job No").
                  setSize(40);
      hse_job_blk.addField("JOB_NAME").
                  setInsertable().
                  setLabel("HSEJOBJOBNAME: Job Name").
                  setSize(40);
      hse_job_blk.addField("NOTE1").
                  setDbName("NOTE").
                  setHeight(5).
                  setInsertable().
                  setLabel("HSEJOBNOTE: Note").
                  setSize(130);
      hse_job_blk.setView("HSE_JOB");
      hse_job_blk.defineCommand("HSE_JOB_API","New__,Modify__,Remove__");
      hse_job_set = hse_job_blk.getASPRowSet();
      hse_job_bar = mgr.newASPCommandBar(hse_job_blk);
      hse_job_tbl = mgr.newASPTable(hse_job_blk);
      hse_job_bar.defineCommand(hse_job_bar.OKFIND,"okFindItem1");
      hse_job_bar.defineCommand(hse_job_bar.NEWROW,"newRowItem1");
      hse_job_bar.defineCommand(hse_job_bar.COUNTFIND,"countFindItem1");
      hse_job_tbl.setTitle("HSEJOBTBLHEAD: Hse Jobs");
      hse_job_tbl.enableRowSelect();
      hse_job_tbl.setWrap();
      hse_job_lay = hse_job_blk.getASPBlockLayout();
      hse_job_lay.setDefaultLayoutMode(hse_job_lay.MULTIROW_LAYOUT);
      hse_job_lay.setDataSpan("NOTE1", 5);
      
      
      hse_special_equ_blk = mgr.newASPBlock("ITEM2");
      hse_special_equ_blk.addField("OBJID2").
                          setDbName("OBJID").
                          setHidden();
      hse_special_equ_blk.addField("OBJVERSION2").
                          setDbName("OBJVERSION").
                          setHidden();
      hse_special_equ_blk.addField("EQU_NO").
                          setMandatory().
                          setInsertable().
                          setLabel("HSESPECIALEQUEQUNO: Equ No").
                          setSize(40);
      hse_special_equ_blk.addField("EQU_NAME").
                          setInsertable().
                          setLabel("HSESPECIALEQUEQUNAME: Equ Name").
                          setSize(40);
      hse_special_equ_blk.addField("NOTE2").
                          setDbName("NOTE").
                          setHeight(5).
                          setInsertable().
                          setLabel("HSESPECIALEQUNOTE: Note").
                          setSize(130);
      hse_special_equ_blk.setView("HSE_SPECIAL_EQU");
      hse_special_equ_blk.defineCommand("HSE_SPECIAL_EQU_API","New__,Modify__,Remove__");
      hse_special_equ_set = hse_special_equ_blk.getASPRowSet();
      hse_special_equ_bar = mgr.newASPCommandBar(hse_special_equ_blk);
      hse_special_equ_tbl = mgr.newASPTable(hse_special_equ_blk);
      hse_special_equ_bar.defineCommand(hse_special_equ_bar.OKFIND,"okFindItem2");
      hse_special_equ_bar.defineCommand(hse_special_equ_bar.NEWROW,"newRowItem2");
      hse_special_equ_bar.defineCommand(hse_special_equ_bar.COUNTFIND,"countFindItem2");
      hse_special_equ_tbl.setTitle("HSESPECIALEQUTBLHEAD: Hse Special Equs");
      hse_special_equ_tbl.enableRowSelect();
      hse_special_equ_tbl.setWrap();
      hse_special_equ_lay = hse_special_equ_blk.getASPBlockLayout();
      hse_special_equ_lay.setDefaultLayoutMode(hse_special_equ_lay.MULTIROW_LAYOUT);
      hse_special_equ_lay.setDataSpan("NOTE2", 5);
      
      
      
      
      ProjectTypeblk = mgr.newASPBlock("ITEM3");
      ProjectTypeblk.addField("PROJECTTYPEOBJID").
              setDbName("OBJID").
              setHidden();
      ProjectTypeblk.addField("PROJECTTYPEOBJVERSION").
              setDbName("OBJVERSION").
              setHidden();
      ProjectTypeblk.addField("PROJECT_TYPE_NO").
              setMandatory().
              setInsertable().
              setLabel("PROJECTTYPEPROJECTTYPENO: Project Type No").
              setSize(15);
      ProjectTypeblk.addField("PROJECT_TYPE_NAME").
              setInsertable().
              setLabel("PROJECTTYPEPROJECTTYPENAME: Project Type Name").
              setSize(30);
      ProjectTypeblk.setView("PROJECT_TYPE");
      ProjectTypeblk.defineCommand("PROJECT_TYPE_API","New__,Modify__,Remove__");
      ProjectTypeset = ProjectTypeblk.getASPRowSet();
      ProjectTypebar = mgr.newASPCommandBar(ProjectTypeblk);
      ProjectTypetbl = mgr.newASPTable(ProjectTypeblk);
      ProjectTypetbl.setTitle("PROJECTTYPETBLHEAD: Project Types");
      ProjectTypetbl.enableRowSelect();
      ProjectTypetbl.setWrap();
      ProjectTypelay = ProjectTypeblk.getASPBlockLayout();
      ProjectTypelay.setDefaultLayoutMode(ProjectTypelay.MULTIROW_LAYOUT);
      
      
    ///�¹ʵȼ�
      HseAccGradeblk = mgr.newASPBlock("ITEM4");
      HseAccGradeblk.addField("ITEM4_OBJID").
              setDbName("OBJID").
              setHidden();
      HseAccGradeblk.addField("ITEM4_OBJVERSION").
              setDbName("OBJVERSION").
              setHidden();
      HseAccGradeblk.addField("ITEM4_PROJECT_TYPE_NO").
              setDbName("PROJECT_TYPE_NO").
              setHidden().
              setMandatory().
              setInsertable().
              setDynamicLOV("PROJECT_TYPE",600,450).
              setLabel("HSEACCGRADEPROJECTTYPENO: Project Type No").
              setSize(30);
      HseAccGradeblk.addField("GRADE_NO").
              setMandatory().
              setInsertable().
              setLabel("HSEACCGRADEGRADENO: Grade No").
              setSize(30);
      HseAccGradeblk.addField("ITEM4_DESCRIPTION").
              setDbName("DESCRIPTION").
              setInsertable().
              setLabel("HSEACCGRADEDESCRIPTION: Description").
              setSize(30).
              setMaxLength(120);
      HseAccGradeblk.addField("ITEM4_NOTE").
              setDbName("NOTE").
              setInsertable().
              setLabel("HSEACCGRADENOTE: Note").
              setSize(50).
              setMaxLength(500).
              setHeight(3);
      HseAccGradeblk.setView("HSE_ACC_GRADE");
      HseAccGradeblk.defineCommand("HSE_ACC_GRADE_API","New__,Modify__,Remove__");
      HseAccGradeblk.setMasterBlock(ProjectTypeblk);
      HseAccGradeset = HseAccGradeblk.getASPRowSet();
      HseAccGradebar = mgr.newASPCommandBar(HseAccGradeblk);
      HseAccGradebar.defineCommand(HseAccGradebar.OKFIND, "okFindItem4");
      HseAccGradebar.defineCommand(HseAccGradebar.NEWROW, "newRowItem4");
      HseAccGradebar.defineCommand(HseAccGradebar.COUNTFIND,"countFindITEM4");        
      HseAccGradetbl = mgr.newASPTable(HseAccGradeblk);
      HseAccGradetbl.setTitle("setTitle: Hse Acc Grades");
      HseAccGradetbl.enableRowSelect();
      HseAccGradetbl.setWrap();
      HseAccGradelay = HseAccGradeblk.getASPBlockLayout();
      HseAccGradelay.setDefaultLayoutMode(HseAccGradelay.MULTIROW_LAYOUT);
      
      
      
      
      
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("HSEBASEDATALEC: Hse Base Data Lec"), "javascript:commandSet('MAIN.activateHseBaseDataLec','')");
      tabs.addTab(mgr.translate("HSEJOB: Hse Job"), "javascript:commandSet('MAIN.activateHseJob','')");
      tabs.addTab(mgr.translate("HSESPECIALEQU: Hse Special Equ"), "javascript:commandSet('MAIN.activateHseSpecialEqu','')");
      tabs.addTab(mgr.translate("HSEPROJECTTYPE: Project Type"),"javascript:commandSet('MAIN.activateHseProjectType','')");
      
      item_tabs = newASPTabContainer("");
      item_tabs.addTab(mgr.translate("HSEACCGRADE: Hse Acc Grade"),"javascript:commandSet('MAIN.activateHseAccGrade','')");
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

   }
   
   public void activateHseBaseDataLec()
   {   
      tabs.setActiveTab(1);
   }
   
   public void activateHseJob()
   {   
      tabs.setActiveTab(2);
      okFindItem1();  

   }
   
   public void activateHseSpecialEqu()
   {   
      tabs.setActiveTab(3);
      okFindItem2();
   }

   public void activateHseProjectType()
   {   
      tabs.setActiveTab(4);
      okFindItem3();
   }

   public void activateHseAccGrade()
   {   
      item_tabs.setActiveTab(1);
      okFindItem3();
      okFindItem4();
   }

   public void  adjust()
   {
      // fill function body
      headbar.removeCustomCommand("activateHseBaseDataLec");
      headbar.removeCustomCommand("activateHseJob");
      headbar.removeCustomCommand("activateHseSpecialEqu");
      headbar.removeCustomCommand("activateHseProjectType");
      
      ProjectTypebar.disableCommand(ProjectTypebar.DELETE);
      ProjectTypebar.disableCommand(ProjectTypebar.EDITROW);
      ProjectTypebar.disableCommand(ProjectTypebar.NEWROW);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HSEBASEDATALECDESC: Hse Base Data Lec";
   }


   protected String getTitle()
   {
      return "HSEBASEDATALECTITLE: Hse Base Data Lec";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(tabs.showTabsInit());

      if (tabs.getActiveTab() == 1 )
         appendToHTML(headlay.show());
      else if (tabs.getActiveTab() == 2 )
         appendToHTML(hse_job_lay.show());
      else if (tabs.getActiveTab() == 3 )
         appendToHTML(hse_special_equ_lay.show());
      else if (tabs.getActiveTab() == 4 ) {
         appendToHTML(ProjectTypelay.show());
         
         if (  (ProjectTypelay.isSingleLayout() || ProjectTypelay.isCustomLayout())) {
            if (item_tabs.getActiveTab() == 1  )
               appendToHTML(HseAccGradelay.show());
         }
      }
      
      appendToHTML(tabs.showTabsFinish());    

   }
}
