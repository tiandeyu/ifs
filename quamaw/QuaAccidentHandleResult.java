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

package ifs.quamaw;
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

public class QuaAccidentHandleResult extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuaAccidentHandleResult");

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

   public  QuaAccidentHandleResult (ASPManager mgr, String page_path)
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
      q.addWhereCondition(" QUOTE_ID IS NOT NULL AND IS_PLAN IS NULL and BELONG_TO_LU = 'QuaAccident'");
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("QUAACCIDENTHANDLERESULTNODATA: No data found.");
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
      q.addWhereCondition(" QUOTE_ID IS NOT NULL AND IS_PLAN IS NULL and BELONG_TO_LU = 'QuaAccident'");
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

      cmd = trans.addEmptyCommand("HEAD","QUA_ACCIDENT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("BELONG_TO_LU", "QuaAccident");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void validate()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String val = mgr.readValue("VALIDATE"); 
       String txt = "";
       String contractDesc, contractNo;
       String contructOrg, contructOrgDesc;
       String supervisionOrg, supervisionOrgDesc;
       String subProjNo, subProjName;
       String projPosition; 
       
       
       if ("QUOTE_ID".equals(val)) {  
          
          
          cmd = trans.addCustomFunction("QUOTE_ID", 
                "QUA_ACCIDENT_API.Get_QUOTE_ID", "QUOTE_ID");
          cmd.addParameter("PROJ_NO,QUOTE_ID");
          
          trans = mgr.validate(trans);
          String QUOTE_ID = trans.getValue("QUOTE_ID/DATA/QUOTE_ID");
          trans.clear();
          
          
          cmd = trans.addCustomFunction("CONTRACTNO", 
                "QUA_ACCIDENT_API.Get_Contract_No_By_AccNo", "CONTRACT_NO");
          cmd.addParameter("PROJ_NO,QUOTE_ID");
          
          cmd = trans.addCustomFunction("PROJPOSITION", 
                "QUA_ACCIDENT_API.Get_Proj_Position_By_AccNo", "PROJ_POSITION");
          cmd.addParameter("PROJ_NO,QUOTE_ID");
          
          cmd = trans.addCustomFunction("SUBPROJNO", 
                "QUA_ACCIDENT_API.Get_Sub_Proj_No_By_AccNo", "SUB_PROJ_NO");
          cmd.addParameter("PROJ_NO,QUOTE_ID");
          trans = mgr.validate(trans);
          contractNo = trans.getValue("CONTRACTNO/DATA/CONTRACT_NO");
          projPosition = trans.getValue("PROJPOSITION/DATA/PROJ_POSITION");
          subProjNo = trans.getValue("SUBPROJNO/DATA/SUB_PROJ_NO");
          
          
          trans.clear();
          cmd.clear();
          cmd = trans.addCustomFunction("CONTRACTDESC", 
                "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_DESC");
          cmd.addParameter("PROJ_NO");
          cmd.addParameter("CONTRACT_NO",contractNo);
          
          cmd = trans.addCustomFunction("CONSTRUCTORG", 
                "PROJECT_CONTRACT_API.Get_Secend_Side", "CONSTRUCT_ORG");
          cmd.addParameter("PROJ_NO");
          cmd.addParameter("CONTRACT_NO",contractNo);
          
          cmd = trans.addCustomFunction("SUPERVISIONORG", 
                "QUA_ACCIDENT_API.Get_Supervision_Org_By_AccNo", "SUPERVISION_ORG");
          cmd.addParameter("PROJ_NO");
          cmd.addParameter("QUOTE_ID");
          
          trans = mgr.validate(trans);
          contractDesc = trans.getValue("CONTRACTDESC/DATA/CONTRACT_DESC");
          contructOrg = trans.getValue("CONSTRUCTORG/DATA/CONSTRUCT_ORG");
          supervisionOrg = trans.getValue("SUPERVISIONORG/DATA/SUPERVISION_ORG");
          
          trans.clear();
          cmd.clear();
          cmd = trans.addCustomFunction("CONSTRUCTORGDESC", 
                "GENERAL_ZONE_API.Get_Zone_Desc", "CONSTRUCT_ORG_DESC");
          cmd.addParameter("CONSTRUCT_ORG",contructOrg);
          
          cmd = trans.addCustomFunction("SUPERVISIONORGDESC", 
                "GENERAL_ZONE_API.Get_Zone_Desc", "SUPERVISION_ORG_DESC");
          cmd.addParameter("SUPERVISION_ORG",supervisionOrg);
          
          cmd = trans.addCustomFunction("SUBPROJNAME", 
                "QUANLITY_PLAN_LINE_API.Get_Sub_Proj_Name", "SUB_PROJ_NAME");
          cmd.addParameter("PROJ_NO");
          cmd.addParameter("SUB_PROJ_NO",subProjNo);
          
          trans = mgr.validate(trans);
          contructOrgDesc = trans.getValue("CONSTRUCTORGDESC/DATA/CONSTRUCT_ORG_DESC");
          supervisionOrgDesc = trans.getValue("SUPERVISIONORGDESC/DATA/SUPERVISION_ORG_DESC");
          subProjName = trans.getValue("SUBPROJNAME/DATA/SUB_PROJ_NAME");
          
          txt = ((mgr.isEmpty(contractNo)) ? "" : contractNo) + "^" + ((mgr.isEmpty(contractDesc)) ? "" : contractDesc) + "^";
          txt = txt + ((mgr.isEmpty(contructOrg)) ? "" : contructOrg) + "^" + ((mgr.isEmpty(contructOrgDesc)) ? "" : contructOrgDesc) + "^";
          txt = txt + ((mgr.isEmpty(supervisionOrg)) ? "" : supervisionOrg) + "^" + ((mgr.isEmpty(supervisionOrgDesc)) ? "" : supervisionOrgDesc) + "^";
          txt = txt + ((mgr.isEmpty(subProjNo)) ? "" : subProjNo) + "^" + ((mgr.isEmpty(subProjName)) ? "" : subProjName) + "^";
          txt = txt + ((mgr.isEmpty(projPosition)) ? "" : projPosition) + "^";
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
              setHidden().
              setReadOnly().
              setLabel("QUAACCIDENTHANDLERESULTID: Id").
              setSize(50);
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("QUAACCIDENTHANDLERESULTPROJNO: Proj No").
              setSize(30); 
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("QUAACCIDENTHANDLERESULTPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("QUA_ACCIDENT_NO").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setLabel("QUAACCIDENTHANDLEPLANNO: Qua Accident No").
              setSize(30);
      
      headblk.addField("QUOTE_ID").
              setMandatory().
              setDefaultNotVisible().
              setInsertable().
              setDynamicLOV("QUA_ACCIDENT","PROJ_NO").
              setLOVProperty("WHERE", "IS_PLAN = 'TRUE' AND BELONG_TO_LU = 'QuaAccident'").
              setLabel("QUAACCIDENTHANDLERESULTQUOTEID: Quote Id").
              setCustomValidation("PROJ_NO,QUOTE_ID", "CONTRACT_NO,CONTRACT_DESC,CONSTRUCT_ORG," +
              		"CONSTRUCT_ORG_DESC,SUPERVISION_ORG,SUPERVISION_ORG_DESC,SUB_PROJ_NO,SUB_PROJ_NAME,PROJ_POSITION").
              setSize(30);
      headblk.addField("CONTRACT_NO").
              setReadOnly().
              setDefaultNotVisible().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              //setFunction("QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID)").
              setLabel("QUAACCIDENTHANDLERESULTCONTRACTNO: Contract No").
              setSize(30);
      headblk.addField("CONTRACT_DESC").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID))").
              setLabel("QUAACCIDENTHANDLERESULTCONTRACTDESC: Contract Desc").
              setSize(40);
      headblk.addField("CONSTRUCT_ORG").
              setReadOnly().
              setDefaultNotVisible().
              //setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID))").
              setLabel("QUAACCIDENTHANDLERESULTCONSTRUCTORG: Construct Org").
              setSize(10);
      headblk.addField("CONSTRUCT_ORG_DESC").
              setReadOnly().
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID)))").
              setLabel("QUAACCIDENTHANDLERESULTCONSTRUCTORGEDESC: Construct Org Desc").
              setSize(30);
      headblk.addField("SUPERVISION_ORG").
              setReadOnly().
              setDefaultNotVisible().
//              setFunction("PROJECT_CONTRACT_API.Get_Third_Side (:PROJ_NO,QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID))").
              //setFunction("QUA_ACCIDENT_API.Get_SUPERVISION_ORG (:PROJ_NO,:QUOTE_ID)").
              setLabel("QUAACCIDENTHANDLERESULTSUPERVISIONORG: Supervision Org").
              setSize(10);
      headblk.addField("SUPERVISION_ORG_DESC").
              setReadOnly().
//              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (PROJECT_CONTRACT_API.Get_Third_Side (:PROJ_NO,QUA_ACCIDENT_API.Get_Contract_No (:PROJ_NO,:QUOTE_ID)))").
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (QUA_ACCIDENT_API.Get_SUPERVISION_ORG (:PROJ_NO,:QUOTE_ID))").
              setLabel("QUAACCIDENTHANDLERESULTSUPERVISIONORGDESC: Supervision Org Desc").
              setSize(30);
      headblk.addField("SUB_PROJ_NO").
              setReadOnly().  
              setDefaultNotVisible().
              setDynamicLOV("QUA_PLAN_LINE_INFO","PROJ_NO").
              //setFunction("QUA_ACCIDENT_API.Get_Sub_Proj_No(:PROJ_NO,:QUOTE_ID)").
              setLabel("QUAACCIDENTHANDLERESULTSUBPROJNO: Sub Proj No").
              setSize(10);
      headblk.addField("SUB_PROJ_NAME").
              setReadOnly().
              setLabel("QUAACCIDENTHANDLEPLANSUBPROJNAME: Sub Proj Name").
              setFunction("QUANLITY_PLAN_LINE_API.Get_Sub_Proj_Name (:PROJ_NO,QUA_ACCIDENT_API.Get_Sub_Proj_No(:PROJ_NO,:QUOTE_ID))").
              setSize(40);
      headblk.addField("PROJ_POSITION").
              setReadOnly().
              //setFunction("QUA_ACCIDENT_API.Get_Proj_Position(:PROJ_NO,:QUOTE_ID)").
              setLabel("QUAACCIDENTHANDLERESULTPROJPOSITION: Proj Position").
              setSize(50).
              setMaxLength(100);
      
      headblk.addField("HANDLE_PERSON").             
              setLabel("QUAACCIDENTHANDLERESULTHANDLEPERSON: Handle Person").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("PERSON_INFO").
              setSize(30);
      headblk.addField("HANDLE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :HANDLE_PERSON)").
              setLabel("MATBORROWHANDLEPERSONINFONAME: Handle Person Name").
              setReadOnly().
              setDefaultNotVisible().
              setSize(30);
      mgr.getASPField("HANDLE_PERSON").setValidation("HANDLE_PERSON_NAME");
      headblk.addField("HANDLE_TIME","Date").
              setLabel("QUAACCIDENTHANDLERESULTHANDLETIME: Handle Time").
              setInsertable().
              setDefaultNotVisible().
              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("QUAACCIDENTHANDLERESULTNOTE: Note").
              setSize(150).
              setDefaultNotVisible().
              setMaxLength(500).
              setHeight(3);
      headblk.addField("BELONG_TO_LU").
              setHidden().
              setLabel("QUAACCIDENTRESULTBELONGTOLU: Belong To Lu").
              setSize(150);

      headblk.setView("QUA_ACCIDENT");
      headblk.defineCommand("QUA_ACCIDENT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("QUAACCIDENTHANDLERESULTTBLHEAD: Qua Accident Handle Plan");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.addCustomCommand("printReportCheck", "QUAACCIDENTCHECK: Print Report Check...");

      headlay.setDataSpan("HANDLE_TIME", 5);
      headlay.setDataSpan("NOTE", 5);
      headlay.setSimple("CONSTRUCT_ORG_DESC");
      headlay.setSimple("SUPERVISION_ORG_DESC");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setSimple("HANDLE_PERSON_NAME");
      headlay.setSimple("SUB_PROJ_NAME");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
   }

   public void  adjust()
   {
      // fill function body
   }
   
   public void  printReportCheck() throws FndException, UnsupportedEncodingException
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuaAccidentCheck.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "QUAACCIDENTHANDLERESULTDESC: Qua Accident Handle Result";
   }


   protected String getTitle()
   {
      return "QUAACCIDENTHANDLERESULTTITLE: Qua Accident Handle Result";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
}
