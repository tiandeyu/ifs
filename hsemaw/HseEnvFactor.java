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

public class HseEnvFactor extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hsemaw.HseEnvFactor");

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

   private ASPBlock hse_env_factor_line_blk;
   private ASPRowSet hse_env_factor_line_set;
   private ASPCommandBar hse_env_factor_line_bar;
   private ASPTable hse_env_factor_line_tbl;
   private ASPBlockLayout hse_env_factor_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HseEnvFactor (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ENV_FACTOR_ID")) )
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
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
         mgr.showAlert("HSEENVFACTORNODATA: No data found.");
         headset.clear();
      }
      eval( hse_env_factor_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","HSE_ENV_FACTOR_API.New__",headblk);
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
      
      q = trans.addQuery(hse_env_factor_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ENV_FACTOR_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ENV_FACTOR_ID", headset.getValue("ENV_FACTOR_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hse_env_factor_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","HSE_ENV_FACTOR_LINE_API.New__",hse_env_factor_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ENV_FACTOR_ID", headset.getValue("ENV_FACTOR_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hse_env_factor_line_set.addRow(data);
   }

   
   //-----------------------------------------------------------------------------
   //------------------------  Validate functions  ---------------------------
   //-----------------------------------------------------------------------------
   public void validate() {
      // TODO Auto-generated method stub
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd1;
      
      String val = mgr.readValue("VALIDATE");
      String txt = "";
      String  CREATE_PERSON_NAME = "";
      String  APPLY_ORG_DESC = "";
      String  APPLY_ORG = "";
      
      if ("CREATE_PERSON".equals(val)) {
         
         cmd = trans.addCustomFunction("GETCREATEPERSONNAME", 
               "PERSON_INFO_API.GET_NAME", "CREATE_PERSON_NAME");
         cmd.addParameter("CREATE_PERSON");
         
         cmd = trans.addCustomFunction("GETPERSONDEFZONE", 
               "PERSON_ZONE_API.Get_User_Def_Zone", "APPLY_ORG");
         cmd.addParameter("CREATE_PERSON");
         
         trans = mgr.validate(trans);
         CREATE_PERSON_NAME = trans.getValue("GETCREATEPERSONNAME/DATA/CREATE_PERSON_NAME");
         APPLY_ORG = trans.getValue("GETPERSONDEFZONE/DATA/APPLY_ORG");
         
         
         cmd1 = trans1.addCustomFunction("GETPERSONAPPLYORG", 
               "GENERAL_ZONE_API.Get_Zone_Desc", "APPLY_ORG_DESC");
         cmd1.addParameter("APPLY_ORG",APPLY_ORG);
         trans1 = mgr.validate(trans1);
         APPLY_ORG_DESC = trans1.getValue("GETPERSONAPPLYORG/DATA/APPLY_ORG_DESC");

         txt = ((mgr.isEmpty(CREATE_PERSON_NAME)) ? "" : CREATE_PERSON_NAME) + "^"
               + ((mgr.isEmpty(APPLY_ORG)) ? "" : APPLY_ORG) + "^"
               + ((mgr.isEmpty(APPLY_ORG_DESC)) ? "" : APPLY_ORG_DESC) + "^";
      }
      mgr.responseWrite(txt);
      mgr.endResponse();
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
              setLabel("HSEENVFACTORPROJNO: Proj No").
              setDynamicLOV("GENERAL_PROJECT",600,445).
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("HSEENVFACTORPROJNO: General Project Proj Desc").
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");

      headblk.addField("ENV_FACTOR_ID").
              setHidden().
              setSize(30);
      headblk.addField("STATUS").
              setHidden().
              setLabel("HSEENVFACTORSTATUS: Status").
              setSize(30);
      headblk.addField("CREATE_PERSON").
              setLabel("HSEENVFACTORCREATEPERSON: Create Person").
              setDynamicLOV("PERSON_INFO").
              setSize(30).
              setCustomValidation("CREATE_PERSON","CREATE_PERSON_NAME,APPLY_ORG,APPLY_ORG_DESC");
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("HSEENVFACTORPERSONINFONAME: Create Person Name").
              setSize(30).
              setReadOnly();
      headblk.addField("APPLY_ORG").
              setDynamicLOV("GENERAL_ZONE").
              setLabel("HSEENVFACTORAPPLYORG: Apply Org").
              setSize(30);
      headblk.addField("APPLY_ORG_DESC").
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc ( :APPLY_ORG)").
              setLabel("HSEENVFACTORAPPLYORGDESC: Apply Org Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("APPLY_ORG").setValidation("APPLY_ORG_DESC");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("HSEENVFACTORCREATETIME: Create Time").
              setSize(30);
      headblk.addField("CHECK_PERSON").
              setInsertable().
              setLabel("HSEENVFACTORCHECKPERSON: Check Person").
              setSize(30).
              setHidden();
      headblk.addField("CHECK_TIME","Date").
              setInsertable().
              setLabel("HSEENVFACTORCHECKTIME: Check Time").
              setSize(30).
              setHidden();
      headblk.addField("APPROVE_PERSON").
              setInsertable().
              setLabel("HSEENVFACTORAPPROVEPERSON: Approve Person").
              setSize(30).
              setHidden();
      headblk.addField("APPROVE_TIME","Date").
              setInsertable().
              setLabel("HSEENVFACTORAPPROVETIME: Approve Time").
              setSize(30).
              setHidden();
      headblk.setView("HSE_ENV_FACTOR");
      headblk.defineCommand("HSE_ENV_FACTOR_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HSEENVFACTORTBLHEAD: Hse Env Factors");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("APPLY_ORG_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");


      hse_env_factor_line_blk = mgr.newASPBlock("ITEM1");
      hse_env_factor_line_blk.addField("ITEM0_OBJID").
                              setHidden().
                              setDbName("OBJID");
      hse_env_factor_line_blk.addField("ITEM0_OBJVERSION").
                              setHidden().
                              setDbName("OBJVERSION");
      hse_env_factor_line_blk.addField("ITEM0_PROJ_NO").
                              setDbName("PROJ_NO").
                              setMandatory().
                              setInsertable().
                              setLabel("HSEENVFACTORLINEITEM0PROJNO: Proj No").
                              setHidden().
                              setSize(30);
      hse_env_factor_line_blk.addField("ITEM0_ENV_FACTOR_ID").
                              setDbName("ENV_FACTOR_ID").
                              setMandatory().
                              setInsertable().
                              setLabel("HSEENVFACTORLINEITEM0ENVFACTORID: Env Factor Id").
                              setHidden().        
                              setSize(30);
      hse_env_factor_line_blk.addField("LINE_NO").
                              setHidden().
                              setSize(30);
      hse_env_factor_line_blk.addField("TYPE").
                              setInsertable().
                              setLabel("HSEENVFACTORLINETYPE: Type").
                              setSize(30);
      hse_env_factor_line_blk.addField("EVN_FACTOR_NAME").
                              setInsertable().
                              setLabel("HSEENVFACTORLINEEVNFACTORNAME: Evn Factor Name").
                              setSize(30);
      hse_env_factor_line_blk.addField("DEPT").
                              setDynamicLOV("GENERAL_ZONE").
//                              setLOVProperty("WHERE", "PERSON_ID = '"+mgr.getFndUser()+"'").
                              setLabel("HSEENVFACTORLINEDEPT: Dept").
                              setSize(30);
      hse_env_factor_line_blk.addField("DEPT_DESC").
                              setReadOnly().
                              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc_ (:DEPT)").
                              setLabel("HSEENVFACTORLINEDEPTDESC: Dept Desc").
                              setSize(30);  
      mgr.getASPField("DEPT").setValidation("DEPT_DESC");

      
      hse_env_factor_line_blk.addField("CONTROL_METHOD").
                              setInsertable().
                              setLabel("HSEENVFACTORLINECONTROLMETHOD: Control Method").
                              setSize(30);
      hse_env_factor_line_blk.addField("ITEM0_APPROVE_TIME","Date").
                              setDbName("APPROVE_TIME").
                              setInsertable().
                              setLabel("HSEENVFACTORLINEITEM0APPROVETIME: Approve Time").
                              setSize(30);
      hse_env_factor_line_blk.addField("NOTE").
                              setInsertable().
                              setLabel("HSEENVFACTORLINENOTE: Note").
                              setSize(120).
                              setHeight(5);
      hse_env_factor_line_blk.setView("HSE_ENV_FACTOR_LINE");
      hse_env_factor_line_blk.defineCommand("HSE_ENV_FACTOR_LINE_API","New__,Modify__,Remove__");
      hse_env_factor_line_blk.setMasterBlock(headblk);
      hse_env_factor_line_set = hse_env_factor_line_blk.getASPRowSet();
      hse_env_factor_line_bar = mgr.newASPCommandBar(hse_env_factor_line_blk);
      hse_env_factor_line_bar.defineCommand(hse_env_factor_line_bar.OKFIND, "okFindITEM1");
      hse_env_factor_line_bar.defineCommand(hse_env_factor_line_bar.NEWROW, "newRowITEM1");
      hse_env_factor_line_tbl = mgr.newASPTable(hse_env_factor_line_blk);
      hse_env_factor_line_tbl.setTitle("HSEENVFACTORLINEITEMHEAD1: HseEnvFactor");
      hse_env_factor_line_tbl.enableRowSelect();
      hse_env_factor_line_tbl.setWrap();
      hse_env_factor_line_lay = hse_env_factor_line_blk.getASPBlockLayout();
      hse_env_factor_line_lay.setDefaultLayoutMode(hse_env_factor_line_lay.MULTIROW_LAYOUT);
      hse_env_factor_line_lay.setSimple("DEPT_DESC");
      hse_env_factor_line_lay.setDataSpan("NOTE", 5);
      hse_env_factor_line_lay.setDataSpan("ITEM0_APPROVE_TIME", 5);


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
      return "HSEENVFACTORDESC: Hse Env Factor";
   }


   protected String getTitle()
   {
      return "HSEENVFACTORTITLE: Hse Env Factor";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible()) {
          appendToHTML(headlay.show());
      }
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (hse_env_factor_line_lay.isVisible())
          appendToHTML(hse_env_factor_line_lay.show());

   }
}