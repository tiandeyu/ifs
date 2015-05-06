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

public class ConProjMeasureCheck extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConProjMeasureCheck");

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

   public  ConProjMeasureCheck (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
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
         mgr.showAlert("CONPROJMEASURECHECKNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","CON_PROJ_MEASURE_CHECK_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
//      data.setFieldItem("STATUS", "���");
      headset.addRow(data);
   }

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
      String SECONDSIDE = "";
      String SECONDSIDENAME = "";
//      String PRICE = "";
      
      
      if ("CONTRACT_NO".equals(val)) {
         cmd = trans.addCustomFunction("GETCONTRACTNAME", 
               "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_NAME");
         cmd.addParameter("PROJ_NO,CONTRACT_NO");
         
         cmd = trans.addCustomFunction("GETSECONDSIDE", 
               "PROJECT_CONTRACT_API.Get_Secend_Side", "SECOND_SIDE");
         cmd.addParameter("PROJ_NO,CONTRACT_NO");
   
         trans = mgr.validate(trans);
         contractName = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         SECONDSIDE = trans.getValue("GETSECONDSIDE/DATA/SECOND_SIDE");
         
         cmd1 = trans1.addCustomFunction("GETSECONDSIDENAME", 
               "SUPPLIER_INFO_API.GET_NAME", "SECOND_SIDE_NAME");
         cmd1.addParameter("SECOND_SIDE",SECONDSIDE);
         
         trans1 = mgr.validate(trans1);
         
         SECONDSIDENAME = trans1.getValue("GETSECONDSIDENAME/DATA/SECOND_SIDE_NAME");

         txt = ((mgr.isEmpty(contractName)) ? "" : contractName )+ "^"
            +  ((mgr.isEmpty(SECONDSIDE)) ? "" : SECONDSIDE )+ "^" 
            +  ((mgr.isEmpty(SECONDSIDENAME)) ? "" : SECONDSIDENAME )+ "^";
         
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
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONPROJMEASURECHECKPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("HSECHECKGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();

      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("ID").
              setHidden().
              setInsertable().
              setLabel("CONPROJMEASURECHECKID: Id").
              setSize(30);
      
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
//              setLOVProperty("WHERE", "SCHEDULE = 'TRUE'").
              setLabel("CONPROJMEASURECHECKCONTRACTNO: Contract No").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_NO", "CONTRACT_NAME,SECOND_SIDE,SECOND_SIDE_NAME");
      headblk.addField("CONTRACT_NAME").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_NO)").
              setLabel("CONPROJMEASURECHECKCONTRACTNAME: Contract Name").
              setSize(30);

      headblk.addField("SECOND_SIDE").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO)").
              setLabel("CONPROJMEASURECHECKSECONDSIDE: Constraction Org No").
              setSize(30);

      headblk.addField("SECOND_SIDE_NAME").
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setLabel("CONPROJMEASURECHECKSECONDSIDENAME: Constraction Org Name").
              setSize(30);
      
      headblk.addField("SUB_PROJ_NO").
              setMandatory().
              setDefaultNotVisible().
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLOVProperty("ORDER_BY", "NODE_NO").
              setLabel("CONPROJMEASURECHECKSUBPROJNO: Sub Proj No").
              setSize(30);

      headblk.addField("SUB_PROJ_DESC").
              setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:SUB_PROJ_NO)").
              setLabel("CONPROJMEASURECHECKSUBPROJDESC: SUB PROJ DESC").
              setReadOnly().
              setSize(30);
      mgr.getASPField("SUB_PROJ_NO").setValidation("SUB_PROJ_DESC");
      
      headblk.addField("CHECK_NO").
              setInsertable().
              setWfProperties().
              setLabel("CONPROJMEASURECHECKCHECKNO: Check No").
              setSize(30);
      headblk.addField("CHECK_NAME").
              setInsertable().
              setWfProperties().
              setLabel("CONPROJMEASURECHECKCHECKNAME: Check Name").
              setSize(30);
      headblk.addField("TIME","Date").
              setInsertable().
              setLabel("CONPROJMEASURECHECKTIME: Time").
              setSize(30).
              setHidden();
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("PERSON_INFO").
              setLabel("CONPROJMEASURECHECKCREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setLabel("CONPROJMEASURECHECKCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("CONPROJMEASURECHECKCREATETIME: Create Time").
              setSize(30);
      headblk.addField("STATUS").
              setHidden().
              setLabel("CONPROJMEASURECHECKSTATUS: Status").
              setSize(30);
      headblk.addField("STATUS_DESC").
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("CONPROJMEASURECHECKSTATUSDESC: Status Desc").
              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("CONPROJMEASURECHECKNOTE: Note").
              setSize(130).
              setHeight(5);
      headblk.addField("LOCATION").
              setInsertable().
              setLabel("CONPROJMEASURECHECKLOCATION: Location").
              setSize(2000).
              setHidden();

      headblk.setView("CON_PROJ_MEASURE_CHECK");
      headblk.defineCommand("CON_PROJ_MEASURE_CHECK_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CONPROJMEASURECHECKREPORT: Print Proj Measure Check Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONPROJMEASURECHECKTBLHEAD: Con Proj Measure Checks");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDataSpan("NOTE", 5);
      headlay.setDataSpan("STATUS_DESC", 5);
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("SECOND_SIDE_NAME");
      headlay.setSimple("SUB_PROJ_DESC");
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConProjMeasureCheck.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }


   public void  adjust() throws FndException
   {
      // fill function body
      super.adjust();
//      ASPManager mgr = getASPManager();
//      if("MAIN.OkFind".equals(mgr.readValue("__COMMAND"))){
//         mgr.getASPField("SECOND_SIDE").setHidden();
//         mgr.getASPField("CREATE_PERSON ").setHidden();
//         mgr.getASPField("SUB_PROJ_NO ").setHidden();
//      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONPROJMEASURECHECKDESC: Con Proj Measure Check";
   }


   protected String getTitle()
   {
      return "CONPROJMEASURECHECKTITLE: Con Proj Measure Check";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
   protected ASPBlock getBizWfBlock()
   {
      return headblk;      
   }
}
