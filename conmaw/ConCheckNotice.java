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

public class ConCheckNotice extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConCheckNotice");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   protected ASPBlock headblk;
   protected ASPRowSet headset;
   protected ASPCommandBar headbar;
   protected ASPTable headtbl;
   protected ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConCheckNotice (ASPManager mgr, String page_path)
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
      q.addWhereCondition("CON_CHECK_LIST_TYPE_DB = ?");
      q.addParameter("CON_CHECK_LIST_TYPE_DB","PROGRESS");
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("CONCHECKNOTICENODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","CON_CHECK_NOTICE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("CON_CHECK_LIST_TYPE_DB", "PROGRESS");
      headset.addRow(data);
   }
   
   
   public void  performHEAD( String command)
   {
      int currow;
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      

      currow = headset.getCurrentRowNo();
      if(headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
      headset.markSelectedRows( command );
      mgr.submit(trans);
      headset.goTo(currow);
   }
   public void  checking()
   {

      performHEAD( "Checking__" );
   }
   public void  close()
   {

      performHEAD( "Close__" );
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
               + ((mgr.isEmpty(constructionOrgName)) ? "" : constructionOrgName )+ "^";
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
      headblk.addField("OBJSTATE").
              setHidden();
      headblk.addField("OBJEVENTS").
              setHidden();
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setLabel("CONCHECKNOTICEID: Id").
              setSize(200).
              setHidden();
      
      //PROJ_NO
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONCHECKNOTICEPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("CONCHECKNOTICEGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setMandatory().
              setDefaultNotVisible().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLabel("CONADJUSTNOTICECONTRACTNO: Contract No").
              setSize(30).
              setCustomValidation("PROJ_NO,CONTRACT_NO", "CONTRACT_NAME,CONSTRUCTION_ORG_NO,CONSTRUCTION_ORG_NAME");
      headblk.addField("CONTRACT_NAME").
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_NO)").
              setLabel("CONADJUSTNOTICECONTRACTNAME: Contract Name").
              setSize(30).
              setReadOnly();
      headblk.addField("CONSTRUCTION_ORG_NO").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO , :CONTRACT_NO)").
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNO: Construction Org No").
              setSize(30);
      headblk.addField("CONSTRUCTION_ORG_NAME").
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNAME: Construction Org Name").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();

      
      //CON_CHECK_LIST_TYPE
//      headblk.addField("CON_CHECK_LIST_TYPE").
//              enumerateValues("Con_Check_List_Type_API").
//              setSelectBox().
//              setMandatory().
//              setInsertable().
//              setLabel("CONCHECKNOTICECONCHECKLISTTYPE: Con Check List Type").
//              setSize(30);      
      
      headblk.addField("CON_CHECK_LIST_TYPE_DB").
              setHidden().
              setSize(30); 
      
      headblk.addField("CHECK_NOTICE_NO").
              setInsertable().
              setLabel("CONCHECKNOTICECHECKNOTICENO: Check Notice No").
              setSize(30);
      headblk.addField("CHECK_NOTICE_NAME").
              setInsertable().
              setMandatory().
              setLabel("CONCHECKNOTICECHECKNOTICENAME: Check Notice Name").
              setSize(30);

      //IS_CHARGE
      headblk.addField("IS_CHARGE").
              setCheckBox("FALSE,TRUE").
              setReadOnly().
              setLabel("CONCHECKNOTICEISCHARGE: Is Charge").
              setSize(30);
      
      headblk.addField("DETAIN_PRICE").
              setInsertable().
              setLabel("CONCHECKNOTICEDETAINPRICE: Detain Price").
              setSize(30);
      
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setLabel("CONCHECKNOTICEPERSONINFOUSERID: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
            setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
            setLabel("CONCHECKNOTICEPERSONINFONAME: Create Person Name").
            setSize(30).
            setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONCHECKNOTICECREATETIME: Create Time").
              setSize(30);
      
//      headblk.addField("STATUS").
//              setReadOnly().
//              setLabel("CONCHECKNOTICESTATUS: Status").
//              setSize(30);
      
      headblk.addField("STATE").
              setLabel("CONCHECKNOTICESTATE: State").
              setReadOnly().
              setSize(30);
      
      //CHECK_ACCORDING
      headblk.addField("CHECK_ACCORDING").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONCHECKNOTICECHECKACCORDING: Check According").
              setSize(120).
              setHeight(5);
      
      headblk.addField("CAUSE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONCHECKNOTICECAUSE: Cause").
              setSize(120).
              setHeight(5);
      headblk.addField("FLOW_TITLE").
              setWfProperties().
              setReadOnly().
              setHidden().
              setFunction("CHECK_NOTICE_NAME").
              setLabel("FLOWTITLE: Flow Title");
      
      headblk.setView("CON_CHECK_NOTICE");
      headblk.defineCommand("CON_CHECK_NOTICE_API","New__,Modify__,Remove__,Checking__,Close__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("Checking","CONCHECKNOTICECHECKING: Checking Con Check Notice");
      headbar.addCustomCommand("Close","CONCHECKNOTICECLOSE: Close Con Check Notice");
      headbar.addCommandValidConditions("Checking",     "OBJSTATE",    "Enable",      "Initialized");
      headbar.addCommandValidConditions("Close", "OBJSTATE",    "Enable",      "Checked");    
      headbar.addCustomCommand("printReport", "CONCHECKNOTICEREPORT: Print Check Notice Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONCHECKNOTICETBLHEAD: Con Check Notices");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC"); 
      headlay.setSimple("CONTRACT_NAME"); 
      headlay.setSimple("CONSTRUCTION_ORG_NAME");
      headlay.setSimple("CREATE_PERSON_NAME"); 
      headlay.setDataSpan("IS_CHARGE", 5);
      headlay.setDataSpan("CHECK_ACCORDING", 5);
      headlay.setDataSpan("CAUSE", 5);
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConCheckNotice.raq&proj_no="+proj_no+"&id="+id
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
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      
//      String PROJNO = headset.getValue("PROJ_NO");
//      
//      cmd = trans.addCustomFunction("GETPROJSTATE",
//            "GENERAL_PROJECT_API.GET_STATE", "STATE");
//      cmd.addParameter("PROJ_NO",PROJNO);
//      
//      trans = mgr.validate(trans);
//      
//      String STATE = trans.getValue("GETPROJSTATE/DATA/STATE");
//      
//      
//      if(headlay.isMultirowLayout()){
//         headset.storeSelections();
//         ASPBuffer selected_fields=headset.getSelectedRows("OBJSTATE");
//         for(int i=0;i<selected_fields.countItems();i++){
//             ASPBuffer subBuff = selected_fields.getBufferAt(i);
//             String state=subBuff.getValueAt(0);
//             if( "Closed".equals(state) || "Checked".equals(state)|| "Closed".equals(STATE) ){
//                 headbar.disableCommand(headbar.DELETE);
//                 headbar.disableCommand(headbar.EDITROW);   
//            }
//         }  
//      } 
//         else {
//         if(headset.countRows()>0){
//          String state = headset.getValue("OBJSTATE");
//          if( headlay.isSingleLayout() && ("Closed".equals(state) || "Checked".equals(state)||"Closed".equals(STATE))){ 
//                headbar.disableCommand(headbar.DELETE);
//                headbar.disableCommand(headbar.EDITROW);   
//          }
//        }       
//      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONCHECKNOTICEDESC: Con Check Notice";
   }


   protected String getTitle()
   {
      return "CONCHECKNOTICETITLE: Con Check Notice";
   }


   protected void printContents() throws FndException
   {
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
