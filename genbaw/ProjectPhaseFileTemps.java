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

package ifs.genbaw;
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

public class ProjectPhaseFileTemps extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.genbaw.ProjectPhaseFileTemps");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock project_file_temp_blk;
   private ASPRowSet project_file_temp_set;
   private ASPCommandBar project_file_temp_bar;
   private ASPTable project_file_temp_tbl;
   private ASPBlockLayout project_file_temp_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ProjectPhaseFileTemps (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROJECT_PHASE_NO")) )
         okFind();
      else
         okFind();
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
         mgr.showAlert("PROJECTPHASENODATA: No data found.");
         headset.clear();
      }
      eval( project_file_temp_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","PROJECT_PHASE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(project_file_temp_blk);
      q.addWhereCondition("PROJECT_TYPE_NO = ? AND PROJECT_PHASE_NO = ?");
      q.addParameter("PROJECT_TYPE_NO", headset.getValue("PROJECT_TYPE_NO"));
      q.addParameter("PROJECT_PHASE_NO", headset.getValue("PROJECT_PHASE_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,project_file_temp_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","PROJECT_FILE_TEMP_API.New__",project_file_temp_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJECT_TYPE_NO", headset.getValue("PROJECT_TYPE_NO"));
      cmd.setParameter("ITEM0_PROJECT_PHASE_NO", headset.getValue("PROJECT_PHASE_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      project_file_temp_set.addRow(data);
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
      headblk.addField("PROJECT_TYPE_NO").
              setMandatory().
              setInsertable().
              setLabel("PROJECTPHASEPROJECTTYPENO: Project Type No").
              setSize(20);
      headblk.addField("PROJECT_PHASE_NO").
              setMandatory().
              setInsertable().
              setLabel("PROJECTPHASEPROJECTPHASENO: Project Phase No").
              setSize(20);
      headblk.addField("PROJECT_PHASE_NAME").
              setInsertable().
              setLabel("PROJECTPHASEPROJECTPHASENAME: Project Phase Name").
              setSize(50);
      headblk.setView("PROJECT_PHASE");
      headblk.defineCommand("PROJECT_PHASE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PROJECTPHASETBLHEAD: Project Phases");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 


      project_file_temp_blk = mgr.newASPBlock("ITEM1");
      project_file_temp_blk.addField("ITEM0_OBJID").
                            setHidden().
                            setDbName("OBJID");
      project_file_temp_blk.addField("ITEM0_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
      project_file_temp_blk.addField("ITEM0_PROJECT_TYPE_NO").
                            setDbName("PROJECT_TYPE_NO").
                            setMandatory().
                            setInsertable().
                            setLabel("PROJECTFILETEMPITEM0PROJECTTYPENO: Project Type No").
                            setSize(20);
      project_file_temp_blk.addField("ITEM0_PROJECT_PHASE_NO").
                            setDbName("PROJECT_PHASE_NO").
                            setMandatory().
                            setInsertable().
                            setLabel("PROJECTFILETEMPITEM0PROJECTPHASENO: Project Phase No").
                            setSize(20);
      project_file_temp_blk.addField("LINE_NO","Number").
                            setMandatory().
                            setInsertable().
                            setLabel("PROJECTFILETEMPLINENO: Line No").
                            setSize(30);
      project_file_temp_blk.addField("FILE_NAME").
                            setInsertable().
                            setLabel("PROJECTFILETEMPFILENAME: File Name").
                            setSize(50);
      project_file_temp_blk.setView("PROJECT_FILE_TEMP");
      project_file_temp_blk.defineCommand("PROJECT_FILE_TEMP_API","New__,Modify__,Remove__");
      project_file_temp_blk.setMasterBlock(headblk);
      project_file_temp_set = project_file_temp_blk.getASPRowSet();
      project_file_temp_bar = mgr.newASPCommandBar(project_file_temp_blk);
      project_file_temp_bar.defineCommand(project_file_temp_bar.OKFIND, "okFindITEM1");
      project_file_temp_bar.defineCommand(project_file_temp_bar.NEWROW, "newRowITEM1");
      project_file_temp_tbl = mgr.newASPTable(project_file_temp_blk);
      project_file_temp_tbl.setTitle("PROJECTFILETEMPITEMHEAD1: Project File Temps");
      project_file_temp_tbl.enableRowSelect();
      project_file_temp_tbl.setWrap();
      project_file_temp_lay = project_file_temp_blk.getASPBlockLayout();
      project_file_temp_lay.setDefaultLayoutMode(project_file_temp_lay.MULTIROW_LAYOUT);



   }



   public void  adjust()
   {
      // fill function body
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "PROJECTPHASEDESC: Project Phase";
   }


   protected String getTitle()
   {
      return "PROJECTPHASETITLE: Project Phase";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      if (project_file_temp_lay.isVisible())
          appendToHTML(project_file_temp_lay.show());

   }
}
