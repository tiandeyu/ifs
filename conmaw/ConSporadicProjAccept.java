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

package ifs.conmaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import java.io.UnsupportedEncodingException;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ConSporadicProjAccept extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConSporadicProjAccept");

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

   private ASPBlock con_sporadic_proj_content_blk;
   private ASPRowSet con_sporadic_proj_content_set;
   private ASPCommandBar con_sporadic_proj_content_bar;
   private ASPTable con_sporadic_proj_content_tbl;
   private ASPBlockLayout con_sporadic_proj_content_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConSporadicProjAccept (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.run();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
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
         mgr.showAlert("CONSPORADICPROJACCEPTNODATA: No data found.");
         headset.clear();
      }
      else
      {
         okFindITEM1();
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

      cmd = trans.addEmptyCommand("HEAD","CON_SPORADIC_PROJ_ACCEPT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
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
      String  contractName = "";
      String  constructionOrgNo = "";
      String  constructionOrgName = "";
      
      if ("CONTRACT_NO".equals(val)) {
         cmd = trans.addCustomFunction("GETCONTRACTNAME", 
               "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_NAME");
         cmd.addParameter("PROJ_NO");
         cmd.addParameter("CONTRACT_NO");
         cmd = trans.addCustomFunction("GETCONSTRUCTIONORGNO", 
                 "PROJECT_CONTRACT_API.Get_Secend_Side", "CONSTRUCTION_ORG_NO");
         cmd.addParameter("PROJ_NO");
         cmd.addParameter("CONTRACT_NO");
         trans = mgr.validate(trans);
         contractName = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         constructionOrgNo = trans.getValue("GETCONSTRUCTIONORGNO/DATA/CONSTRUCTION_ORG_NO");
         
         cmd1 = trans1.addCustomFunction("GETCONSTRUCTIONORGNAME", 
               "SUPPLIER_INFO_API.GET_NAME", "CONSTRUCTION_ORG_NAME");
         cmd1.addParameter("CONSTRUCTION_ORG_NO",constructionOrgNo);
         trans1 = mgr.validate(trans1);
         constructionOrgName = trans1.getValue("GETCONSTRUCTIONORGNAME/DATA/CONSTRUCTION_ORG_NAME");

         txt = ((mgr.isEmpty(contractName)) ? "" : contractName ) + "^" 
               + ((mgr.isEmpty(constructionOrgNo)) ? "" : constructionOrgNo ) + "^" 
               + ((mgr.isEmpty(constructionOrgName)) ? "" : constructionOrgName ) + "^";
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
      
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

      q = trans.addQuery(con_sporadic_proj_content_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,con_sporadic_proj_content_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","CON_SPORADIC_PROJ_CONTENT_API.New__",con_sporadic_proj_content_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      con_sporadic_proj_content_set.addRow(data);
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
              setMandatory().
              setInsertable().
              setLabel("CONSPORADICPROJACCEPTID: Id").
              setSize(200).
              setHidden();
      
      //PROJ_NO
      headblk.addField("PROJ_NO").
              setMandatory().
              setDefaultNotVisible().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONSPORADICPROJACCEPTPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("CONSPORADICPROJACCEPTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
//              setLOVProperty("WHERE", "SCHEDULE = 'TRUE'").
              setLabel("CONADJUSTNOTICECONTRACTNO: Contract No").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_NO", "CONTRACT_NAME,CONSTRUCTION_ORG_NO,CONSTRUCTION_ORG_NAME");
      headblk.addField("CONTRACT_NAME").
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_NO)").
              setLabel("CONADJUSTNOTICECONTRACTNAME: Contract Name").
              setSize(30).
              setReadOnly();
      headblk.addField("CONSTRUCTION_ORG_NO").
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO , :CONTRACT_NO)").
              setDefaultNotVisible().
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNO: Construction Org No").
              setSize(30).
              setReadOnly();
      headblk.addField("CONSTRUCTION_ORG_NAME").
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNAME: Construction Org Name").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();
      
      //MONITOR_ORG_NO
      headblk.addField("ZONE_NO").
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV","PROJ_NO").
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTMONITORORGNO: Monitor Org No").
              setSize(30);
      //MONITOR_ORG_NAME
      headblk.addField("ZONE_DESC").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc ( :ZONE_NO)").
              setLabel("CONSPORADICPROJACCEPTMONITORORGNAME: Monitor Org Name").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();
      mgr.getASPField("ZONE_NO").setValidation("ZONE_DESC");
      

      //ACCEPT_LIST_NO
      headblk.addField("ACCEPT_LIST_NO").
              setInsertable().
              setLabel("CONSPORADICPROJACCEPTACCEPTLISTNO: Accept List No").
              setSize(30);
      headblk.addField("ACCEPT_LIST_NAME").
              setInsertable().
              setMandatory().
              setLabel("CONSPORADICPROJACCEPTACCEPTLISTNAME: Accept List Name").
              setSize(30);
      
      headblk.addField("SUB_PROJ_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLOVProperty("ORDER_BY", "NODE_NO").
              setLabel("CONSPORADICPROJACCEPTSUBPROJNO: Sub Proj No").
              setSize(30);
      headblk.addField("SUB_PROJ_DESC").
              setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:SUB_PROJ_NO)").
              setLabel("CONSPORADICPROJACCEPTSUBPROJDESC: SUB PROJ DESC").
              setSize(30).
              setReadOnly();
      mgr.getASPField("SUB_PROJ_NO").setValidation("SUB_PROJ_DESC");
      
      //PROJ_TIME
      headblk.addField("PROJ_TIME","Number").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTPROJTIME: Proj Time").
              setSize(30);
      
      headblk.addField("START_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTSTARTTIME: Start Time").
              setSize(30);
      headblk.addField("COMPLETE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTCOMPLETETIME: Complete Time").
              setSize(30);
      headblk.addField("ACCEPT_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTACCEPTTIME: Accept Time").
              setSize(30);
      
      
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTPERSONINFOUSERID: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("CONSPORADICPROJACCEPTPERSONINFONAME: Create Person").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");      
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("STATUS").
              setHidden().
              setLabel("CONSPORADICPROJACCEPTSTATUS: Status").
              setSize(30);
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("CONSPORADICPROJACCEPTSTATUSDESC: Status Desc").
              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSPORADICPROJACCEPTNOTE: Note").
              setSize(120).
              setHeight(5);
      headblk.addField("FLOW_TITLE").
              setWfProperties().
              setReadOnly().
              setHidden().
              setFunction("ACCEPT_LIST_NAME").
              setLabel("FLOWTITLE: Flow Title");
      
      
      headblk.setView("CON_SPORADIC_PROJ_ACCEPT");
      headblk.defineCommand("CON_SPORADIC_PROJ_ACCEPT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CONSPORADICPROJACCEPTREPORT: Print Sporadic Proj Accept Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONSPORADICPROJACCEPTTBLHEAD: Con Sporadic Proj Accepts");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CONSTRUCTION_ORG_NAME");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("ZONE_DESC");
 
      headlay.setSimple("CREATE_PERSON_NAME"); 
      headlay.setSimple("SUB_PROJ_DESC");
      headlay.setDataSpan("ACCEPT_TIME", 5);
      headlay.setDataSpan("STATUS", 5);
      headlay.setDataSpan("NOTE", 5);


      con_sporadic_proj_content_blk = mgr.newASPBlock("ITEM1");
      con_sporadic_proj_content_blk.addField("ITEM0_OBJID").
                                    setHidden().
                                    setDbName("OBJID");
      con_sporadic_proj_content_blk.addField("ITEM0_OBJVERSION").
                                    setHidden().
                                    setDbName("OBJVERSION");
      
      con_sporadic_proj_content_blk.addField("ITEM0_PROJ_NO").
                                    setDbName("PROJ_NO").
                                    setMandatory().
                                    setHidden().
                                    setLabel("CONSPORADICPROJCONTENTITEM0PROJNO: Proj No").
                                    setSize(50);
      con_sporadic_proj_content_blk.addField("ITEM0_ID").
                                    setDbName("ID").
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTITEM0ID: Id").
                                    setSize(200).
                                    setHidden();

      con_sporadic_proj_content_blk.addField("LINE_NO").
                                    setMandatory().
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTLINENO: Line No").
                                    setSize(200).
                                    setHidden();
      
      con_sporadic_proj_content_blk.addField("PROJ").
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTPROJ: Proj").
                                    setSize(30);
      con_sporadic_proj_content_blk.addField("ORG").
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTORG: Org").
                                    setSize(30);
      con_sporadic_proj_content_blk.addField("AMOUNT","Number").
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTAMOUNT: Amount").
                                    setSize(30);
      con_sporadic_proj_content_blk.addField("ITEM0_NOTE").
                                    setDbName("NOTE").
                                    setInsertable().
                                    setLabel("CONSPORADICPROJCONTENTITEM0NOTE: Note").
                                    setSize(120).
                                    setHeight(5);
      
      
      con_sporadic_proj_content_blk.setView("CON_SPORADIC_PROJ_CONTENT");
      con_sporadic_proj_content_blk.defineCommand("CON_SPORADIC_PROJ_CONTENT_API","New__,Modify__,Remove__");
      con_sporadic_proj_content_blk.setMasterBlock(headblk);
      con_sporadic_proj_content_set = con_sporadic_proj_content_blk.getASPRowSet();
      con_sporadic_proj_content_bar = mgr.newASPCommandBar(con_sporadic_proj_content_blk);
      con_sporadic_proj_content_bar.defineCommand(con_sporadic_proj_content_bar.OKFIND, "okFindITEM1");
      con_sporadic_proj_content_bar.defineCommand(con_sporadic_proj_content_bar.NEWROW, "newRowITEM1");
      con_sporadic_proj_content_tbl = mgr.newASPTable(con_sporadic_proj_content_blk);
      con_sporadic_proj_content_tbl.setTitle("CONSPORADICPROJCONTENTITEMHEAD1: ConSporadicProjContent");
      con_sporadic_proj_content_tbl.enableRowSelect();
      con_sporadic_proj_content_tbl.setWrap();
      con_sporadic_proj_content_lay = con_sporadic_proj_content_blk.getASPBlockLayout();
      con_sporadic_proj_content_lay.setDefaultLayoutMode(con_sporadic_proj_content_lay.MULTIROW_LAYOUT);

      con_sporadic_proj_content_lay.setDataSpan("AMOUNT", 5);
      con_sporadic_proj_content_lay.setDataSpan("ITEM0_NOTE", 5);


   }


 //Report  Function
   public void  printReport() throws FndException, UnsupportedEncodingException//TODO
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String id = headset.getValue("ID");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConSporadicProjAccept.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }
   

   public void  adjust()
   {
      // fill function body
      try {
         super.adjust();
      } catch (FndException e) {
         // TODO Auto-generated catch block   
         e.printStackTrace();
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONSPORADICPROJACCEPTDESC: Con Sporadic Proj Accept";
   }


   protected String getTitle()
   {
      return "CONSPORADICPROJACCEPTTITLE: Con Sporadic Proj Accept";
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
      if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0)
      {
         appendToHTML(con_sporadic_proj_content_lay.show());
      }

   }
   
 //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;      
   }
}
