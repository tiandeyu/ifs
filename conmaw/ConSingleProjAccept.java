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

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ConSingleProjAccept extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConSingleProjAccept");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConSingleProjAccept (ASPManager mgr, String page_path)
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
         mgr.showAlert("CONSINGLEPROJACCEPTNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","CON_SINGLE_PROJ_ACCEPT_API.New__",headblk);
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
              setLabel("CONSINGLEPROJACCEPTID: Id").
              setSize(200).
              setHidden();
      
      
      //PROJ_NO
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONSINGLEPROJACCEPTPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("CONSINGLEPROJACCEPTGENERALPROJECTPROJDESC: General Project Proj Desc").
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
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNO: Construction Org No").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();
      headblk.addField("CONSTRUCTION_ORG_NAME").
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setDefaultNotVisible().
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNAME: Construction Org Name").
              setSize(30).
              setReadOnly();
      
      //MONITOR_ORG_NO
      headblk.addField("ZONE_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV","PROJ_NO").
              setLabel("CONSINGLEPROJACCEPTMONITORORGNO: Monitor Org No").
              setSize(30);
      //MONITOR_ORG_NAME
      headblk.addField("ZONE_DESC").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc ( :ZONE_NO)").
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTMONITORORGNAME: Monitor Org Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("ZONE_NO").setValidation("ZONE_DESC");
      

      //ACCEPT_LIST_NO
      headblk.addField("ACCEPT_LIST_NO").
              setInsertable().
              setWfProperties().
              setLabel("CONSINGLEPROJACCEPTACCEPTLISTNO: Accept List No").
              setSize(30);
      headblk.addField("ACCEPT_LIST_NAME").
              setInsertable().
              setWfProperties().
              setLabel("CONSINGLEPROJACCEPTACCEPTLISTNAME: Accept List Name").
              setSize(30);
      
      //SINGLE_PROJ_NAME
      headblk.addField("SINGLE_PROJ_NAME").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLOVProperty("ORDER_BY", "NODE_NO").
              setLabel("CONSINGLEPROJACCEPTSINGLEPROJNAME: Single Proj Name").
              setSize(30);
      headblk.addField("SINGLE_PROJ_DESC").
              setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:SINGLE_PROJ_NAME)").
              setLabel("CONSINGLEPROJACCEPTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("SINGLE_PROJ_NAME").setValidation("SINGLE_PROJ_DESC");
      
      
      
      headblk.addField("PLAN_START_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTPLANSTARTTIME: Plan Start Time").
              setSize(30);
      headblk.addField("PLAN_COMPLETE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTPLANCOMPLETETIME: Plan Complete Time").
              setSize(30);
      headblk.addField("ACTUAL_START_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTACTUALSTARTTIME: Actual Start Time").
              setSize(30);
      headblk.addField("ACTUAL_COMPLETE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTACTUALCOMPLETETIME: Actual Complete Time").
              setSize(30);
      
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTPERSONINFOUSERID: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
            setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
            setLabel("CONSINGLEPROJACCEPTPERSONINFONAME: Create Person").
            setSize(30).
            setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("PROJ_CONTENT").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONSINGLEPROJACCEPTPROJCONTENT: Proj Content").
              setSize(120).
              setHeight(5);
      
      headblk.setView("CON_SINGLE_PROJ_ACCEPT");
      headblk.defineCommand("CON_SINGLE_PROJ_ACCEPT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CONSINGLEPROJACCEPTREPORT: Print Single Proj Accept Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONSINGLEPROJACCEPTTBLHEAD: Con Single Proj Accepts");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CONSTRUCTION_ORG_NAME");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("ZONE_DESC");

      headlay.setSimple("CREATE_PERSON_NAME"); 
      headlay.setDataSpan("SINGLE_PROJ_NAME", 5);
      headlay.setSimple("SINGLE_PROJ_DESC"); 
      headlay.setDataSpan("PROJ_CONTENT", 5);



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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConSingleProjAccept.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
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
      return "CONSINGLEPROJACCEPTDESC: Con Single Proj Accept";
   }


   protected String getTitle()
   {
      return "CONSINGLEPROJACCEPTTITLE: Con Single Proj Accept";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
}
