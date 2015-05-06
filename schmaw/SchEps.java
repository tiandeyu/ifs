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

package ifs.schmaw;
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

public class SchEps extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchEps");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPContext ctx;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  SchEps (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      ctx.setGlobal("PROJ_NO", mgr.readValue("PROJ_NO"));
      ctx.setGlobal("ID", mgr.readValue("ID"));
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();     
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("REV")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")) )
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
         mgr.showAlert("SCHEPSNODATA: No data found.");
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
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      
      cmd = trans1.addCustomFunction("GET_PROJ_DESC","GENERAL_PROJECT_API.GET_PROJ_DESC","PROJ_DESC");
      cmd.addParameter("PROJ_NO",ctx.findGlobal("PROJ_NO"));

      trans1 = mgr.validate(trans1);
      String PROJ_DESC= trans1.getValue("GET_PROJ_DESC/DATA/PROJ_DESC");

      cmd = trans.addEmptyCommand("HEAD","SCH_EPS_API.New__",headblk);
      cmd.setParameter("PROJ_NO", ctx.findGlobal("PROJ_NO"));
      cmd.setParameter("PROJ_DESC", PROJ_DESC);
      cmd.setParameter("PARENT_ID", ctx.findGlobal("ID"));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   

   public void validate()
  {
     ASPManager mgr=getASPManager();
     ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
     ASPTransactionBuffer trans1=mgr.newASPTransactionBuffer();
     ASPCommand cmd;
     String str=mgr.readValue("VALIDATE");
   
     if ("CONTRACT_ID".equals(str)) {
        cmd = trans.addCustomFunction("CONTRACT_DESC","PROJECT_CONTRACT_API.GET_CONTRACT_DESC","CONTRACT_DESC");
        cmd.addParameter("PROJ_NO");
        cmd.addParameter("CONTRACT_ID");
        cmd = trans.addCustomFunction("RESPONSE_ORG","PROJECT_CONTRACT_API.GET_SECEND_SIDE","RESPONSE_ORG");
        cmd.addParameter("PROJ_NO");
        cmd.addParameter("CONTRACT_ID");

        trans = mgr.validate(trans);
        String CONTRACT_DESC     = trans.getValue("CONTRACT_DESC/DATA/CONTRACT_DESC"            );
        String RESPONSE_ORG     = trans.getValue("RESPONSE_ORG/DATA/RESPONSE_ORG"            );

        CONTRACT_DESC     = mgr.isEmpty(CONTRACT_DESC    )? ""  : CONTRACT_DESC    ;
        RESPONSE_ORG     = mgr.isEmpty(RESPONSE_ORG    )? ""  : RESPONSE_ORG    ;
        
        
        cmd = trans1.addCustomFunction("GETRESPONSEORGDESC","GENERAL_ZONE_API.GET_ZONE_DESC","RESPONSE_ORG_DESC");
        cmd.addParameter("RESPONSE_ORG",RESPONSE_ORG);
        
        trans1 = mgr.validate(trans1);
        
        String RESPONSE_ORG_DESC     = trans1.getValue("GETRESPONSEORGDESC/DATA/RESPONSE_ORG_DESC");
        
        RESPONSE_ORG_DESC = mgr.isEmpty(RESPONSE_ORG_DESC    )? ""  : RESPONSE_ORG_DESC;
        
        mgr.responseWrite(CONTRACT_DESC + "^"+RESPONSE_ORG + "^" + RESPONSE_ORG_DESC + "^");
     }
     if ("RESPONSE_ORG".equals(str)) {
        cmd = trans.addCustomFunction("RESPONSEORGDESC","GENERAL_ZONE_API.GET_ZONE_DESC","RESPONSE_ORG_DESC");
        cmd.addParameter("RESPONSE_ORG");

        trans = mgr.validate(trans);
        String RESPONSE_ORG_DESC     = trans.getValue("RESPONSEORGDESC/DATA/RESPONSE_ORG_DESC"            );


        RESPONSE_ORG_DESC     = mgr.isEmpty(RESPONSE_ORG_DESC    )? ""  : RESPONSE_ORG_DESC    ;        
        
        mgr.responseWrite(RESPONSE_ORG_DESC );
     }
     
     mgr.endResponse();
  }
   
   
   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().
              setLabel("SCHEPSPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("SCHEPSPROJDESC: Proj Desc").
               setReadOnly().
               setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setLabel("SCHEPSID: Id").
              setSize(20);
      headblk.addField("REV").
              setReadOnly().
              setLabel("SCHEPSREV: Rev").
              setSize(20);
      headblk.addField("EPS_ID").
              setInsertable().
              setLabel("SCHEPSEPSID: Eps Id").
              setSize(20);
      headblk.addField("EPS_NAME").
              setInsertable().
              setLabel("SCHEPSEPSNAME: Eps Name").
              setSize(20);
      headblk.addField("PARENT_ID").
              setInsertable().
              setDynamicLOV("SCH_EPS","PROJ_NO").
              setLOVProperty("FORCE_KEY", "ID").
              setLabel("SCHEPSPARENTID: Parent Id").
              setSize(20);
      headblk.addField("CONTRACT_ID").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("SCHEPSCONTRACTID: Contract Id").
              setCustomValidation("PROJ_NO,CONTRACT_ID", "CONTRACT_DESC,RESPONSE_ORG,RESPONSE_ORG_DESC").
              setSize(20);
      headblk.addField("CONTRACT_DESC").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC ( :PROJ_NO,:CONTRACT_ID)").
              setLabel("SCHEPSCONTRACTDESC: Contract Desc").
              setReadOnly().
              setSize(30);
      headblk.addField("RESPONSE_ORG").
              setInsertable().
              setDynamicLOV("GENERAL_ZONE","PROJ_NO").
              setFunction("PROJECT_CONTRACT_API.GET_SECEND_SIDE(:PROJ_NO,:CONTRACT_ID)").
              setLabel("SCHEPSRESPONSEORG: Response Org").
              setCustomValidation("RESPONSE_ORG", "RESPONSE_ORG_DESC").
              setSize(20);
      headblk.addField("RESPONSE_ORG_DESC").
              setFunction("GENERAL_ZONE_API.GET_ZONE_DESC (PROJECT_CONTRACT_API.GET_SECEND_SIDE(:PROJ_NO,:CONTRACT_ID))").
              setLabel("SCHEPSRESPONSEORGDESC: Response Org Desc").
              setReadOnly().
              setSize(30);
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("SCHEPSCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setLabel("SCHEPSCREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(20);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("SCHEPSCREATETIME: Create Time").
              setSize(20);
      headblk.addField("STATUS").
              setInsertable().
              setHidden().
              setLabel("SCHEPSSTATUS: Status").
              setSize(20);
      headblk.addField("LEVE").
              setInsertable().
              setHidden().
              setLabel("SCHEPSLEVEL: Level").
              setSize(20);
      headblk.addField("IS_LEAF").
              setInsertable().
              setHidden().
              setLabel("SCHEPSISLEAF: Is Leaf").
              setSize(20);
//      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
//              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( PROJ_NO)").
//              setLabel("SCHEPSGENERALPROJECTPROJDESC: General Project Proj Desc").
//              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("SCHEPSNOTE: Note").
              setHeight(3).
              setSize(100);
      headblk.setView("SCH_EPS");
      headblk.defineCommand("SCH_EPS_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("SCHEPSTBLHEAD: Sch Epss");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("RESPONSE_ORG_DESC");
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setDataSpan("NOTE", 5);
      headlay.setDataSpan("PARENT_ID", 5);
 



   }


   public void adjust()
   {
//      super.adjust();
      // fill function body
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "SCHEPSDESC: Sch Eps";
   }


   protected String getTitle()
   {
      return "SCHEPSTITLE: Sch Eps";
   }


   protected void printContents() throws FndException
   {
      super.printContents();

      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}