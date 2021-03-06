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

public class MatInstalReqPlan extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatInstalReqPlan");

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

   private ASPBlock mat_install_req_plan_line_blk;
   private ASPRowSet mat_install_req_plan_line_set;
   private ASPCommandBar mat_install_req_plan_line_bar;
   private ASPTable mat_install_req_plan_line_tbl;
   private ASPBlockLayout mat_install_req_plan_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatInstalReqPlan (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PLAN_ID")) )
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
         mgr.showAlert("MATINSTALREQPLANNODATA: No data found.");
         headset.clear();
      }
      eval( mat_install_req_plan_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","MAT_INSTAL_REQ_PLAN_API.New__",headblk);
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

      q = trans.addQuery(mat_install_req_plan_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND PLAN_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("PLAN_ID", headset.getValue("PLAN_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_install_req_plan_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","MAT_INSTALL_REQ_PLAN_LINE_API.New__",mat_install_req_plan_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_PLAN_ID", headset.getValue("PLAN_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      mat_install_req_plan_line_set.addRow(data);
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
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setLabel("MATINSTALREQPLANPROJNO: Proj No").
              setSize(40).
              setDynamicLOV("GENERAL_PROJECT");
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("MATINSTALREQPLANGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("PLAN_ID").
              setMandatory().
              setInsertable().
              setLabel("MATINSTALREQPLANPLANID: Plan Id").
              setSize(40);
      headblk.addField("PLAN_NAME").
              setInsertable().
              setLabel("MATINSTALREQPLANPLANNAME: Plan Name").
              setSize(40);
      headblk.addField("REQ_ORG").
              setInsertable().
              setLabel("MATINSTALREQPLANREQORG: Req Org").
              setSize(40);
      headblk.addField("PERSON_ID").
              setDbName("CREATE_PERSON").
              setInsertable().
              setLabel("MATINSTALREQPLANCREATEPERSON: Create Person").
              setSize(40).
              setDynamicLOV("PERSON_INFO");
      headblk.addField("USER_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :PERSON_ID)").
              setReadOnly().
              setLabel("MATSTORAGEADMINPERSONINFO: Person Info").
              setSize(40);
      mgr.getASPField("PERSON_ID").setValidation("USER_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("MATINSTALREQPLANCREATETIME: Create Time").
              setSize(30);

      headblk.setView("MAT_INSTAL_REQ_PLAN");
      headblk.defineCommand("MAT_INSTAL_REQ_PLAN_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATINSTALREQPLANTBLHEAD: Mat Instal Req Plans");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("USER_NAME");
 


      mat_install_req_plan_line_blk = mgr.newASPBlock("ITEM1");
      mat_install_req_plan_line_blk.addField("ITEM0_OBJID").
                                    setHidden().
                                    setDbName("OBJID");
      mat_install_req_plan_line_blk.addField("ITEM0_OBJVERSION").
                                    setHidden().
                                    setDbName("OBJVERSION");
      mat_install_req_plan_line_blk.addField("ITEM0_PROJ_NO").
                                    setDbName("PROJ_NO").
                                    setMandatory().
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEITEM0PROJNO: Proj No").
                                    setSize(40).
                                    setHidden();
      mat_install_req_plan_line_blk.addField("ITEM0_PLAN_ID").
                                    setDbName("PLAN_ID").
                                    setMandatory().
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEITEM0PLANID: Plan Id").
                                    setSize(40).
                                    setHidden();
      mat_install_req_plan_line_blk.addField("LINE_NO").
                                    setLabel("MATINSTALLREQPLANLINELINENO: Line No").
                                    setReadOnly().
                                    setSize(40);
      mat_install_req_plan_line_blk.addField("INSTALL_POSTION").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEINSTALLPOSTION: Install Postion").
                                    setSize(40);
      mat_install_req_plan_line_blk.addField("MAT_NO").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEMATNO: Mat No").
                                    setSize(40).
                                    setDynamicLOV("MAT_CODE","PROJ_NO");
      mat_install_req_plan_line_blk.addField("MAT_NAME").
                                    setFunction("MAT_CODE_API.GET_MAT_NAME ( :PROJ_NO,:MAT_NO)").
                                    setLabel("MATINSTALLREQPLANLINEMATNAME: Mat Name").
                                    setSize(40).
                                    setReadOnly();
      mgr.getASPField("MAT_NO").setValidation("MAT_NAME");
      mat_install_req_plan_line_blk.addField("UNIT").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEUNIT: Unit").
                                    setSize(40).
                                    setDynamicLOV("ISO_UNIT");
      mat_install_req_plan_line_blk.addField("QTY","Number").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEQTY: Qty").
                                    setSize(40);
      mat_install_req_plan_line_blk.addField("INSTALL_REQ_TIME","Date").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEINSTALLREQTIME: Install Req Time").
                                    setSize(30);
      /*mat_install_req_plan_line_blk.addField("CONTRACT_ID").
                                    setInsertable().
                                    setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
                                    setLabel("MATINSTALLREQPLANLINECONTRACTID: Contract Id").
                                    setSize(40);
      mat_install_req_plan_line_blk.addField("ITEM_NO").
                                    setInsertable().
                                    setDynamicLOV("PROJECT_CONTRACT_ITEM","PROJ_NO,CONTRACT_ID").
                                    setLabel("MATINSTALLREQPLANLINEITEMNO: No").
                                    setSize(40);*/
      mat_install_req_plan_line_blk.addField("ITEM0_CREATE_PERSON").
                                    setDbName("CREATE_PERSON").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEITEM0CREATEPERSON: Create Person").
                                    setSize(40).
                                    setDynamicLOV("PERSON_INFO");
      mat_install_req_plan_line_blk.addField("ITEM0_USER_NAME").
                                    setFunction("PERSON_INFO_API.GET_NAME ( :PERSON_ID)").
                                    setReadOnly().
                                    setLabel("MATINSTALLREQPLANLINEITEM0USERNAME: User Name").
                                    setSize(40);
      mat_install_req_plan_line_blk.addField("PROGRESS_AND_PROBLEMS").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINEPROGRESSANDPROBLEMS: Progress And Problems").
                                    setSize(120).
                                    setHeight(5);
      mat_install_req_plan_line_blk.addField("NOTE").
                                    setInsertable().
                                    setLabel("MATINSTALLREQPLANLINENOTE: Note").
                                    setSize(120).
                                    setHeight(5);
      mgr.getASPField("ITEM0_CREATE_PERSON").setValidation("ITEM0_USER_NAME");
      mat_install_req_plan_line_blk.setView("MAT_INSTALL_REQ_PLAN_LINE");
      mat_install_req_plan_line_blk.defineCommand("MAT_INSTALL_REQ_PLAN_LINE_API","New__,Modify__,Remove__");
      mat_install_req_plan_line_blk.setMasterBlock(headblk);
      mat_install_req_plan_line_set = mat_install_req_plan_line_blk.getASPRowSet();
      mat_install_req_plan_line_bar = mgr.newASPCommandBar(mat_install_req_plan_line_blk);
      mat_install_req_plan_line_bar.defineCommand(mat_install_req_plan_line_bar.OKFIND, "okFindITEM1");
      mat_install_req_plan_line_bar.defineCommand(mat_install_req_plan_line_bar.NEWROW, "newRowITEM1");
      mat_install_req_plan_line_tbl = mgr.newASPTable(mat_install_req_plan_line_blk);
      mat_install_req_plan_line_tbl.setTitle("MATINSTALLREQPLANLINEITEMHEAD1: MatInstallReqPlanLine");
      mat_install_req_plan_line_tbl.enableRowSelect();
      mat_install_req_plan_line_tbl.setWrap();
      mat_install_req_plan_line_lay = mat_install_req_plan_line_blk.getASPBlockLayout();
      mat_install_req_plan_line_lay.setDefaultLayoutMode(mat_install_req_plan_line_lay.MULTIROW_LAYOUT);
      mat_install_req_plan_line_lay.setDataSpan("PROGRESS_AND_PROBLEMS", 5);
      mat_install_req_plan_line_lay.setDataSpan("NOTE", 5);
      mat_install_req_plan_line_lay.setDataSpan("ITEM0_CREATE_PERSON", 5);
      mat_install_req_plan_line_lay.setSimple("ITEM0_USER_NAME");
      mat_install_req_plan_line_lay.setSimple("MAT_NAME");


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
      return "MATINSTALREQPLANDESC: Mat Instal Req Plan";
   }


   protected String getTitle()
   {
      return "MATINSTALREQPLANTITLE: Mat Instal Req Plan";
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
      if (mat_install_req_plan_line_lay.isVisible())
          appendToHTML(mat_install_req_plan_line_lay.show());

   }
}
