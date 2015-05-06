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

package ifs.matmaw;
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

public class MatSupProject extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatSupProject");

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

   private ASPBlock mat_sup_project_list_blk;
   private ASPRowSet mat_sup_project_list_set;
   private ASPCommandBar mat_sup_project_list_bar;
   private ASPTable mat_sup_project_list_tbl;
   private ASPBlockLayout mat_sup_project_list_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatSupProject (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SUP_PROJ_NO")) )
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
         mgr.showAlert("MATSUPPROJECTNODATA: No data found.");
         headset.clear();
      }
      eval( mat_sup_project_list_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","MAT_SUP_PROJECT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("SUP_UNIT", "国电物资集团");
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

      q = trans.addQuery(mat_sup_project_list_blk);
      q.addWhereCondition("SUP_PROJ_NO = ?");
      q.addParameter("SUP_PROJ_NO", headset.getValue("SUP_PROJ_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_sup_project_list_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","MAT_SUP_PROJECT_LIST_API.New__",mat_sup_project_list_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_SUP_PROJ_NO", headset.getValue("SUP_PROJ_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      mat_sup_project_list_set.addRow(data);
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
      headblk.addField("SUP_PROJ_NO").
              setMandatory().
              setInsertable().
              setLabel("MATSUPPROJECTSUPPROJNO: Sup Proj No").
              setSize(20);
      headblk.addField("SUP_PROJ_NAME").
              setInsertable().
              setLabel("MATSUPPROJECTSUPPROJNAME: Sup Proj Name").
              setSize(30);
      headblk.addField("PROJ_NO").
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("MATSUPPROJECTPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("MATSUPPROJECTPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("CONTRACT_ID").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("MATSUPPROJECTCONTRACTID: Contract Id").
              setSize(20);
      headblk.addField("CONTRACT_DESC").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC ( :PROJ_NO,:CONTRACT_ID)").
              setLabel("MATSUPPROJECTCONTRACTDESC: Contract Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");
      
      headblk.addField("UNIT_MODEL").
              setInsertable().
              setLabel("MATSUPPROJECTUNITMODEL: Unit Model").
              setSize(20);
      headblk.addField("UNIT_SIZE").
              setInsertable().
              setLabel("MATSUPPROJECTUNITSIZE: Unit Size").
              setSize(20);
      headblk.addField("UNIT_NUMBER").
              setInsertable().
              setLabel("MATSUPPROJECTUNITNUMBER: Unit Number").
              setSize(20);
      headblk.addField("UNIT_PERSON_NAME").
              setInsertable().
              setLabel("MATSUPPROJECTUNITPERSONNAME: Unit Person Name").
              setSize(20);
      headblk.addField("UNIT_PERSON_MAIL").
              setInsertable().
              setLabel("MATSUPPROJECTUNITPERSONMAIL: Unit Person Mail").
              setSize(20);
      headblk.addField("UNIT_PERSON_TEL").
              setInsertable().
              setLabel("MATSUPPROJECTUNITPERSONTEL: Unit Person Tel").
              setSize(20);
      headblk.addField("SUP_UNIT").
              setInsertable().
              setLabel("MATSUPPROJECTSUPUNIT: Sup Unit").
              setSize(20);
      headblk.addField("SUP_MANAGER_NAME").
              setInsertable().
              setLabel("MATSUPPROJECTSUPMANAGERNAME: Sup Manager Name").
              setSize(20);
      headblk.addField("SUP_MANAGER_TEL").
              setInsertable().
              setLabel("MATSUPPROJECTSUPMANAGERTEL: Sup Manager Tel").
              setSize(20);
      headblk.addField("SUP_PROJ_MAIL").
              setInsertable().
              setLabel("MATSUPPROJECTSUPPROJMAIL: Sup Proj Mail").
              setSize(20);
      headblk.addField("SUP_PROJ_FAX").
              setInsertable().
              setLabel("MATSUPPROJECTSUPPROJFAX: Sup Proj Fax").
              setSize(20);
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("MATSUPPROJECTCREATETIME: Create Time").
              setSize(30);
      headblk.addField("PERSON_ID").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("MATSUPPROJECTPERSONID: Person Id").
              setSize(20);
      headblk.addField("PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME (:PERSON_ID)").
              setLabel("MATSUPPROJECTPERSONNAME: Person Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PERSON_ID").setValidation("PERSON_NAME");
//      headblk.addField("PROJECT_CONTRACT_FIRST_SIDE").
//              setFunction("PROJECT_CONTRACT_API.GET_FIRST_SIDE ( PROJ_NO,CONTRACT_ID)").
//              setLabel("MATSUPPROJECTPROJECTCONTRACTFIRSTSIDE: Project Contract First Side").
//              setSize(30);
      headblk.setView("MAT_SUP_PROJECT");
      headblk.defineCommand("MAT_SUP_PROJECT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATSUPPROJECTTBLHEAD: Mat Sup Projects");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setSimple("PERSON_NAME");
 


      mat_sup_project_list_blk = mgr.newASPBlock("ITEM1");
      mat_sup_project_list_blk.addField("ITEM0_OBJID").
                               setHidden().
                               setDbName("OBJID");
      mat_sup_project_list_blk.addField("ITEM0_OBJVERSION").
                               setHidden().
                               setDbName("OBJVERSION");
      mat_sup_project_list_blk.addField("ITEM0_SUP_PROJ_NO").
                               setDbName("SUP_PROJ_NO").
                               setHidden().
                               setLabel("MATSUPPROJECTLISTITEM0SUPPROJNO: Sup Proj No").
                               setSize(20);
      mat_sup_project_list_blk.addField("LIST_NO").
                               setMandatory().
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTLISTNO: List No").
                               setSize(20);
      mat_sup_project_list_blk.addField("SUP_EQU_NO").
                               setInsertable().
                               setDynamicLOV("MAT_SUP_EQUIPMENT").
                               setLabel("MATSUPPROJECTLISTSUPEQUNO: Sup Equ No").
                               setSize(20);

      mat_sup_project_list_blk.addField("MAT_NO").
                               setFunction("MAT_SUP_EQUIPMENT_API.GET_MAT_NO (:SUP_EQU_NO)").
                               setReadOnly().
                               setLabel("MATSUPPROJECTLISTMATNO: Mat No").
                               setSize(30);
      mgr.getASPField("SUP_EQU_NO").setValidation("MAT_NO");
//      mat_sup_project_list_blk.addField("MAT_NAME").
//                               setReadOnly().
//                               setLabel("MATARRIVELINEMATNAME: Mat Name").
//                               setFunction("mat_code_api.Get_Mat_Name(:PROJ_NO,:MAT_NO)").
//                               setSize(20);
      
      mat_sup_project_list_blk.addField("SUP_ENG_NAME").
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTSUPENGNAME: Sup Eng Name").
                               setSize(20);
      mat_sup_project_list_blk.addField("SUP_ENG_TEL").
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTSUPENGTEL: Sup Eng Tel").
                               setSize(20);
      mat_sup_project_list_blk.addField("SUP_SPE_ENG_NAME").
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTSUPSPEENGNAME: Sup Spe Eng Name").
                               setSize(20);
      mat_sup_project_list_blk.addField("SUP_SPE_ENG_TEL").
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTSUPSPEENGTEL: Sup Spe Eng Tel").
                               setSize(20);
      mat_sup_project_list_blk.addField("NOTE").
                               setInsertable().
                               setLabel("MATSUPPROJECTLISTNOTE: Note").
                               setHeight(3).
                               setSize(100);
      mat_sup_project_list_blk.setView("MAT_SUP_PROJECT_LIST");
      mat_sup_project_list_blk.defineCommand("MAT_SUP_PROJECT_LIST_API","New__,Modify__,Remove__");
      mat_sup_project_list_blk.setMasterBlock(headblk);
      mat_sup_project_list_set = mat_sup_project_list_blk.getASPRowSet();
      mat_sup_project_list_bar = mgr.newASPCommandBar(mat_sup_project_list_blk);
      mat_sup_project_list_bar.defineCommand(mat_sup_project_list_bar.OKFIND, "okFindITEM1");
      mat_sup_project_list_bar.defineCommand(mat_sup_project_list_bar.NEWROW, "newRowITEM1");
      mat_sup_project_list_tbl = mgr.newASPTable(mat_sup_project_list_blk);
      mat_sup_project_list_tbl.setTitle("MATSUPPROJECTLISTITEMHEAD1: MatSupProjectList");
      mat_sup_project_list_tbl.enableRowSelect();
      mat_sup_project_list_tbl.setWrap();
      mat_sup_project_list_lay = mat_sup_project_list_blk.getASPBlockLayout();
      mat_sup_project_list_lay.setDefaultLayoutMode(mat_sup_project_list_lay.MULTIROW_LAYOUT);

      mat_sup_project_list_lay.setDataSpan("NOTE", 5);



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
      return "MATSUPPROJECTDESC: Mat Sup Project";
   }


   protected String getTitle()
   {
      return "MATSUPPROJECTTITLE: Mat Sup Project";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (mat_sup_project_list_lay.isVisible())
          appendToHTML(mat_sup_project_list_lay.show());

   }
}
