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

public class ConAdjustReply extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConAdjustReply");

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

   public  ConAdjustReply (ASPManager mgr, String page_path)
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
      q.addWhereCondition("TYPE_ID  IS NULL");
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("CONADJUSTREPLYNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","CON_ADJUST_REPLY_API.New__",headblk);
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
//      String noticeNo = "";
      String noticeName = "";
      String  contractName = "";
      String  constructionOrgNo = "";
      String  constructionOrgName = "";
      
      if ("NOTICE_ID".equals(val)) {
//         cmd = trans.addCustomFunction("GETNOTICENO", 
//               "CON_ADJUST_NOTICE_API.GET_NOTICE_NO", "NOTICE_NO");
//         cmd.addParameter("PROJ_NO,NOTICE_ID");
         
         cmd = trans.addCustomFunction("GETNOTICENAME", 
                 "CON_ADJUST_NOTICE_API.GET_NOTICE_NAME2", "NOTICE_NAME");
         cmd.addParameter("PROJ_NO,NOTICE_ID");
         
         trans = mgr.validate(trans);
         
//         noticeNo = trans.getValue("GETNOTICENO/DATA/NOTICE_NO");
         noticeName = trans.getValue("GETNOTICENAME/DATA/NOTICE_NAME");

//         txt = ((mgr.isEmpty(noticeNo)) ? "" : noticeNo ) + "^"+ ((mgr.isEmpty(noticeName)) ? "" : noticeName )+ "^";
         txt = ((mgr.isEmpty(noticeName)) ? "" : noticeName )+ "^";
         mgr.responseWrite(txt);
      }
      else if ("CONTRACT_NO".equals(val)) {
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

      headblk.addField("ID").
              setInsertable().
              setLabel("CONADJUSTREPLYID: Id").
              setSize(200).
              setHidden();
    //PROJ_NO
      headblk.addField("PROJ_NO").
              setMandatory().
              setDefaultNotVisible().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("CONADJUSTREPLYPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("CONADJUSTREPLYGENERALPROJECTPROJDESC: General Project Proj Desc").
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
              setDefaultNotVisible().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO , :CONTRACT_NO)").
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNO: Construction Org No").
              setSize(30).
              setReadOnly();
      headblk.addField("CONSTRUCTION_ORG_NAME").
              setFunction("SUPPLIER_INFO_API.GET_NAME (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setLabel("CONADJUSTNOTICECONSTRUCTIONORGNAME: Construction Org Name").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();
      
      headblk.addField("ZONE_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_ZONE").
              setLabel("CONADJUSTREPLYMONITORORGNO: Monitor Org No").
              setSize(30);
      headblk.addField("ZONE_DESC").
              setFunction("GENERAL_ZONE_API.GET_ZONE_DESC ( :ZONE_NO)").
              setLabel("CONADJUSTREPLYMONITORORGNAME: Monitor Org Name").
              setDefaultNotVisible().
              setSize(30).
              setReadOnly();
      mgr.getASPField("ZONE_NO").setValidation("ZONE_DESC");
      
      headblk.addField("SUB_PROJ_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLOVProperty("ORDER_BY", "NODE_NO").
              setLabel("CONADJUSTREPLYSUBPROJNO: Sub Proj No").
              setSize(30);

      headblk.addField("SUB_PROJ_DESC").
              setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:SUB_PROJ_NO)").
              setLabel("CONADJUSTREPLYSUBPROJDESC: SUB PROJ DESC").
              setReadOnly().
              setSize(30);
      mgr.getASPField("SUB_PROJ_NO").setValidation("SUB_PROJ_DESC");
      
      headblk.addField("NOTICE_ID").
              setInsertable().
              setDynamicLOV("CON_ADJUST_NOTICE","PROJ_NO").
              setLOVProperty("FORCE_KEY", "NOTICE_NO").
              setLabel("CONADJUSTREPLYNOTICEID: Notice Id").
              setSize(30).
              setCustomValidation("PROJ_NO,NOTICE_ID", "NOTICE_NAME");
//      headblk.addField("NOTICE_NO").
//              setFunction("CON_ADJUST_NOTICE_API.GET_NOTICE_NO ( :PROJ_NO, :NOTICE_ID)").
//              setLabel("CONADJUSTREPLYNOTICENO: NOTICE_NO").
//              setSize(30);
      
      headblk.addField("NOTICE_NAME").
              setFunction("CON_ADJUST_NOTICE_API.GET_NOTICE_NAME2 ( :PROJ_NO, :NOTICE_ID)").
              setLabel("CONADJUSTREPLYNOTICENAME: NOTICE NAME").
              setSize(30).
              setReadOnly();
  
      headblk.addField("ADJUST_REPLY_NO").
              setInsertable().
              setLabel("CONADJUSTREPLYADJUSTREPLYNO: Adjust Reply No").
              setSize(30);
      headblk.addField("ADJUST_REPLY_NAME").
              setInsertable().
              setMandatory().
              setLabel("CONADJUSTREPLYADJUSTREPLYNAME: Adjust Reply Name").
              setSize(30);
      
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setLabel("CONADJUSTREPLYPERSONINFOUSERID: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("CONADJUSTNOTICEPERSONINFONAME: Create Person Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONADJUSTREPLYCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("STATUS").
              setHidden().
              setLabel("CONADJUSTREPLYSTATUS: Status").
              setSize(30);
      
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("CONADJUSTREPLYSTATUSDESC: Status Desc").
              setSize(30);
      
      headblk.addField("NOTE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("CONADJUSTREPLYNOTE: Note").
              setSize(120).
              setHeight(5);
      headblk.addField("FLOW_TITLE").
              setWfProperties().
              setReadOnly().
              setHidden().
              setFunction("ADJUST_REPLY_NAME").
              setLabel("FLOWTITLE: Flow Title");

      headblk.setView("CON_ADJUST_REPLY");
      headblk.defineCommand("CON_ADJUST_REPLY_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CONADJUSTREPLYREPORT: Print Adjust Reply Report...");//TODO
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONADJUSTREPLYTBLHEAD: Con Adjust Replys");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC"); 
      headlay.setSimple("CONTRACT_NAME"); 
      headlay.setSimple("CONSTRUCTION_ORG_NAME"); 
      headlay.setSimple("ZONE_DESC"); 
      headlay.setSimple("SUB_PROJ_DESC");
      headlay.setSimple("NOTICE_NAME");
      headlay.setDataSpan("NOTICE_ID", 5);
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setDataSpan("STATUS_DESC", 5);
      headlay.setDataSpan("NOTE", 5);

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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConAdjustReply.raq&proj_no="+proj_no+"&id="+id
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
      return "CONADJUSTREPLYDESC: Con Adjust Reply";
   }


   protected String getTitle()
   {
      return "CONADJUSTREPLYTITLE: Con Adjust Reply";
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
