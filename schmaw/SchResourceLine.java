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

public class SchResourceLine extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchResourceLine");

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

   public  SchResourceLine (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      ctx.setGlobal("PROJ_NO", mgr.readValue("PROJ_NO"));
      ctx.setGlobal("RESOURCE_NO", mgr.readValue("RESOURCE_NO"));
      ctx.setGlobal("RESOURCE_LINE_NO", mgr.readValue("RESOURCE_LINE_NO"));
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction()); 
      else if(mgr.dataTransfered())
         okFind();
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
            validate();    
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("RESOURCE_LINE_NO")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("RESOURCE_NO")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")) )
         okFind();
      else
         okFind();
         
      adjust();
   }
   
   
   public void validate()
   {
      ASPManager mgr=getASPManager();
      ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String str=mgr.readValue("VALIDATE");
      
      if ("PRICE".equals(str)) {
         cmd = trans.addCustomFunction("TOTAL_PRICE","SCH_RESOURCE_LINE_API.Get_Totle","TOTAL_PRICE");
         cmd.addParameter("PRICE");
         cmd.addParameter("QTY");

         trans = mgr.validate(trans);
         String TOTAL_PRICE= trans.getValue("TOTAL_PRICE/DATA/TOTAL_PRICE");
   
         TOTAL_PRICE     = mgr.isEmpty(TOTAL_PRICE    )? ""  : TOTAL_PRICE    ;
         
         mgr.responseWrite(TOTAL_PRICE + "^" );
      }
      mgr.endResponse();
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
         mgr.showAlert("SCHRESOURCELINENODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","SCH_RESOURCE_LINE_API.New__",headblk);
      cmd.setParameter("PROJ_NO", ctx.findGlobal("PROJ_NO"));
      cmd.setParameter("PROJ_DESC", PROJ_DESC);
      cmd.setParameter("RESOURCE_NO", ctx.findGlobal("RESOURCE_NO"));
      cmd.setParameter("PARENT_ID", ctx.findGlobal("RESOURCE_LINE_NO"));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
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
              setLabel("SCHRESOURCELINEPROJNO: Proj No").
              setSize(20);
     headblk.addField("PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("SCHRESOURCELINEPROJDESC: Proj Desc").
              setReadOnly().
              setSize(30);
     mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("RESOURCE_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("SCH_RESOURCE","PROJ_NO").
              setLabel("SCHRESOURCELINERESOURCENO: Resource No").
              setSize(20);
      headblk.addField("RESOURCE_NAME").
               setFunction("SCH_RESOURCE_API.GET_RESOURCE_NAME (:PROJ_NO,:RESOURCE_NO)").
               setLabel("SCHRESOURCELINERESOURCENAME: Resource Name").
               setReadOnly().
               setSize(20);
      mgr.getASPField("RESOURCE_NO").setValidation("RESOURCE_NAME");
      headblk.addField("RESOURCE_LINE_NO").
              setMandatory().
              setInsertable().
              setLabel("SCHRESOURCELINERESOURCELINENO: Resource Line No").
              setSize(20);
      headblk.addField("RESOURCE_LINE_NAME").
              setInsertable().
              setLabel("SCHRESOURCELINERESOURCELINENAME: Resource Line Name").
              setSize(20);
      headblk.addField("PARENT_ID").
              setInsertable().
              setDynamicLOV("SCH_RESOURCE_LINE","PROJ_NO,RESOURCE_NO").
              setLabel("SCHRESOURCELINEPARENTID: Parent Id").
              setSize(20);
      headblk.addField("PARENT_NAME").
               setFunction("SCH_RESOURCE_LINE_API.GET_RESOURCE_LINE_NAME (:PROJ_NO,:RESOURCE_NO,:PARENT_ID)").
               setLabel("SCHRESOURCELINEPARENTNAME: Parent Name").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PARENT_ID").setValidation("PARENT_NAME");
      headblk.addField("CLASS_ID").
              setInsertable().
              setDynamicLOV("SCH_RESOURCE_CLASS").
              setLabel("SCHRESOURCELINECLASSID: Class Id").
              setSize(20);
      headblk.addField("CLASS_NAME").
              setFunction("SCH_RESOURCE_CLASS_API.GET_CLASS_NAME (:CLASS_ID)").
              setLabel("SCHRESOURCELINECLASSNAME: Class Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("CLASS_ID").setValidation("CLASS_NAME");
      headblk.addField("QTY","Number").
              setInsertable().
              setLabel("SCHRESOURCELINEQTY: Qty").
              setSize(20);
      headblk.addField("UNIT_CODE").
              setInsertable().
              setDynamicLOV("ISO_UNIT").
              setLabel("SCHRESOURCELINEUNITCODE: Unit Code").
              setSize(20);
      headblk.addField("UNIT_CODE_NAME").
              setFunction("ISO_UNIT_API.GET_DESCRIPTION (:UNIT_CODE)").
              setLabel("SCHRESOURCELINEUNITCODE: Unit Code").
              setReadOnly().
              setSize(30);
      mgr.getASPField("UNIT_CODE").setValidation("UNIT_CODE_NAME");
      headblk.addField("PRICE","Number").
              setInsertable().
              setLabel("SCHRESOURCELINEPRICE: Price").
              setCustomValidation("PRICE,QTY", "TOTAL_PRICE").
              setSize(20);
      headblk.addField("TOTAL_PRICE","Number").
              setReadOnly().
              setLabel("SCHRESOURCELINETOTALPRICE: Total Price").
              setSize(20);
//      headblk.addField("BUDGET_NO").
//              setInsertable().
//              setDynamicLOV("PROJECT_BUDGET","PROJ_NO").
//              setLabel("SCHRESOURCELINEBUDGETNO: Budget No").
//              setSize(20);
//      headblk.addField("BUDGET_NAME").
//              setFunction("PROJECT_BUDGET_API.GET_BUDGET_NAME (:PROJ_NO,:BUDGET_NO)").
//              setLabel("SCHRESOURCELINEBUDGETNAME: Budget Name").
//              setReadOnly().
//              setSize(30);
//      mgr.getASPField("BUDGET_NO").setValidation("BUDGET_NAME");
      headblk.addField("BUDGET_LINE_NO").
              setInsertable().
              setDynamicLOV("PROJECT_BUDGET_LINE","PROJ_NO").
              setLOVProperty("WHERE", "STATUS = 1").
              setLabel("SCHRESOURCELINEBUDGETLINENO: Budget Line No").
              setSize(20);
      headblk.addField("BUDGET_LINE_NAME").
              setFunction("PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME ( :PROJ_NO,:BUDGET_LINE_NO)").
              setLabel("SCHRESOURCELINEBUDGETLINENAME: Budget Line Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_LINE_NAME");
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("SCHRESOURCELINECREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setLabel("SCHRESOURCELINECREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(20);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setReadOnly().
              setLabel("SCHRESOURCELINECREATETIME: Create Time").
              setSize(20);
      headblk.addField("LEVE").
              setInsertable().
              setHidden().
              setLabel("SCHRESOURCELINELEVEL: Level").
              setSize(20);
      headblk.addField("IS_LEAF").
              setInsertable().
              setHidden().
              setLabel("SCHRESOURCELINEISLEAF: Is Leaf").
              setSize(20);
//      headblk.addField("ID").
//              setInsertable().
//              setLabel("SCHRESOURCELINEID: Id").
//              setSize(20);
//      headblk.addField("REV").
//              setInsertable().
//              setLabel("SCHRESOURCELINEREV: Rev").
//              setSize(20);
//      headblk.addField("WBS_NO").
//              setInsertable().
//              setLabel("SCHRESOURCELINEWBSNO: Wbs No").
//              setSize(20);
//      headblk.addField("WORK_NO").
//              setInsertable().
//              setLabel("SCHRESOURCELINEWORKNO: Work No").
//              setSize(20);

      headblk.addField("NOTE").
              setInsertable().
              setLabel("SCHRESOURCELINENOTE: Note").
              setHeight(3).
              setSize(100);
      headblk.setView("SCH_RESOURCE_LINE");
      headblk.defineCommand("SCH_RESOURCE_LINE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("SCHRESOURCELINETBLHEAD: Sch Resource Lines");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDataSpan("NOTE", 5);
      headlay.setDataSpan("CREATE_TIME", 5);
      headlay.setSimple("CLASS_NAME");
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("RESOURCE_NAME");
      headlay.setSimple("UNIT_CODE_NAME");
      headlay.setSimple("PARENT_NAME");
//      headlay.setSimple("BUDGET_NAME");
      headlay.setSimple("BUDGET_LINE_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
 



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
      return "SCHRESOURCELINEDESC: Sch Resource Line";
   }


   protected String getTitle()
   {
      return "SCHRESOURCELINETITLE: Sch Resource Line";
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
