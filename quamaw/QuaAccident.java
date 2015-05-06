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

public class QuaAccident extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuaAccident");

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

   public  QuaAccident (ASPManager mgr, String page_path)
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
      q.addWhereCondition(" quote_id is null and belong_to_lu = 'QuaAccident'");
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("QUAACCIDENTNODATA: No data found.");
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
      q.addWhereCondition(" quote_id is null and belong_to_lu = 'QuaAccident'");
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
       String contractDesc = "";
       String contructOrg = "";
       String contructOrgDesc = "";
       String supervisionOrg = "";
       String supervisionOrgDesc = "";
       String projDesc = "";
       String projType = "";

       if ("CONTRACT_NO".equals(val)) {   
          cmd = trans.addCustomFunction("CONTRACTDESC", 
                "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_DESC");
          cmd.addParameter("PROJ_NO,CONTRACT_NO");
          
          cmd = trans.addCustomFunction("CONSTRUCTORG", 
                "PROJECT_CONTRACT_API.Get_Secend_Side", "CONSTRUCT_ORG");
          cmd.addParameter("PROJ_NO,CONTRACT_NO");
          
          cmd = trans.addCustomFunction("SUPERVISIONORG", 
                "PROJECT_CONTRACT_API.Get_Third_Side", "SUPERVISION_ORG");
          cmd.addParameter("PROJ_NO,CONTRACT_NO");
          
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
   
          trans = mgr.validate(trans);
          contructOrgDesc = trans.getValue("CONSTRUCTORGDESC/DATA/CONSTRUCT_ORG_DESC");
          supervisionOrgDesc = trans.getValue("SUPERVISIONORGDESC/DATA/SUPERVISION_ORG_DESC");
          
          txt = ((mgr.isEmpty(contractDesc)) ? "" : contractDesc) + "^" + ((mgr.isEmpty(contructOrg)) ? "" : contructOrg) + "^";
          txt = txt + ((mgr.isEmpty(contructOrgDesc)) ? "" : contructOrgDesc) + "^" + ((mgr.isEmpty(supervisionOrg)) ? "" : supervisionOrg) + "^";
          txt = txt + ((mgr.isEmpty(supervisionOrgDesc)) ? "" : supervisionOrgDesc) + "^";
          mgr.responseWrite(txt);
       }
       if("PROJ_NO".equals(val)) {
          cmd = trans.addCustomFunction("PROJDESC", 
                "GENERAL_PROJECT_API.GET_PROJ_DESC", "GENERAL_PROJECT_PROJ_DESC");
          cmd.addParameter("PROJ_NO");
          
          cmd = trans.addCustomFunction("PROJTYPE", 
                "GENERAL_PROJECT_API.Get_Project_Type_Id", "PROJECT_TYPE_NO");
          cmd.addParameter("PROJ_NO");
          
          trans = mgr.validate(trans);
          projDesc = trans.getValue("PROJDESC/DATA/GENERAL_PROJECT_PROJ_DESC");
          projType = trans.getValue("PROJTYPE/DATA/PROJECT_TYPE_NO");
          
          txt = ((mgr.isEmpty(projDesc)) ? "" : projDesc) + "^" + ((mgr.isEmpty(projType)) ? "" : projType) + "^";
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
              setLabel("QUAACCIDENTPROJNO: Proj No").
              setCustomValidation("PROJ_NO", "GENERAL_PROJECT_PROJ_DESC,PROJECT_TYPE_NO").
              setSize(30); 
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("QUAACCIDENTPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
//      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("ID").
              setHidden().
              setInsertable().
              setLabel("QUAACCIDENTID: Id").
              setSize(50);
      headblk.addField("QUA_ACCIDENT_NO").
              setMandatory().
              setInsertable().
              setLabel("QUAACCIDENTNO: Qua Accident No").
              setSize(30);
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO"). 
              //setLOVProperty("TREE_PARE_FIELD", "PRE_CONTRACT_NO").
              //setLOVProperty("TREE_DISP_FIELD", "CONTRACT_ID,CONTRACT_DESC").
              setLabel("QUAACCIDENTCONTRACTNO: Contract No").
              setCustomValidation("PROJ_NO,CONTRACT_NO", "CONTRACT_DESC,CONSTRUCT_ORG,CONSTRUCT_ORG_DESC,SUPERVISION_ORG,SUPERVISION_ORG_DESC").
              setSize(20);
      headblk.addField("CONTRACT_DESC").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_NO)").
              setLabel("QUAACCIDENTCONTRACTDESC: Contract Desc").
              setSize(30);
      headblk.addField("CONSTRUCT_ORG").
              setReadOnly().
//              setInsertable(). 
              //setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO)").
              setLabel("QUAACCIDENTCONSTRUCTORG: Construct Org").
              setDefaultNotVisible().
              setSize(20);
      headblk.addField("CONSTRUCT_ORG_DESC").
              setReadOnly().
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (PROJECT_CONTRACT_API.Get_Secend_Side (:PROJ_NO,:CONTRACT_NO))").
              setLabel("QUAACCIDENTCONSTRUCTORGEDESC: Construct Org Desc").
              setSize(30);
      headblk.addField("SUPERVISION_ORG").
              setInsertable(). 
              setDynamicLOV("GENERAL_ZONE").
              //setLOVProperty("WHERE", "person_id = Fnd_Session_API.Get_Fnd_User").
              //setFunction("PROJECT_CONTRACT_API.Get_Third_Side (:PROJ_NO,:CONTRACT_NO)").
              setLabel("QUAACCIDENTSUPERVISIONORG: Supervision Org").
              setDefaultNotVisible().
              setSize(20);
      headblk.addField("SUPERVISION_ORG_DESC").
              setReadOnly().
//              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (PROJECT_CONTRACT_API.Get_Third_Side (:PROJ_NO,:CONTRACT_NO))").
              setFunction("GENERAL_ZONE_API.GET_ZONE_DESC ( :SUPERVISION_ORG)").
              setLabel("QUAACCIDENTSUPERVISIONORGDESC: Supervision Org Desc").
              setSize(30);
      mgr.getASPField("SUPERVISION_ORG").setValidation("SUPERVISION_ORG_DESC");
      headblk.addField("SUB_PROJ_NO").
              setInsertable().    
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLabel("QUAACCIDENTSUBPROJNO: Sub Proj No").
              setDefaultNotVisible().
              setSize(30);
      headblk.addField("SUB_PROJ_NAME").
              setReadOnly().
              setFunction("QUANLITY_PLAN_LINE_API.Get_Sub_Proj_Name (:PROJ_NO,:SUB_PROJ_NO)").
              setLabel("QUAACCIDENTSUBPROJNAME: Sub Proj Name").
              setSize(30);
      mgr.getASPField("SUB_PROJ_NO").setValidation("SUB_PROJ_NAME");
      headblk.addField("PROJ_POSITION").
              setInsertable().
              setLabel("QUAACCIDENTPROJPOSITION: Proj Position").
              setSize(50).
              setMaxLength(100);
      headblk.addField("ACCIDENT_NAME").
              setInsertable().
              setLabel("QUAACCIDENTACCIDENTNAME: Accident Name").
              setSize(50).
              setMaxLength(100);
      headblk.addField("QUA_GRADE_NO").
              setInsertable().
              setLabel("QUAACCIDENTQUAGRADENO: Qua Grade No").
              setDynamicLOV("QUALITY_GRADE", "PROJECT_TYPE_NO").
              setDefaultNotVisible().
              setSize(30);
      headblk.addField("QUA_GRADE_DESC").
              setFunction("QUALITY_GRADE_API.Get_Description_ByProjNo( :PROJ_NO, :QUA_GRADE_NO)").
              setLabel("QUAACCIDENTGRADEDESC: Qua Grade Desc").
              setReadOnly().
              setSize(50);      
      mgr.getASPField("QUA_GRADE_NO").setValidation("QUA_GRADE_DESC");
     
      headblk.addField("ACCIDENT_CAUSE").
              setInsertable().
              setLabel("QUAACCIDENTACCIDENTCAUSE: Accident Cause").
              setSize(50).
              setMaxLength(500).
              setHeight(3);
      headblk.addField("PROJECT_TYPE_NO").
              setLabel("QUAACCIDENTPROJECTTYPENO: Project Type No").
              setFunction("GENERAL_PROJECT_API.Get_Project_Type_Id ( :PROJ_NO)").
              setHidden().
              setSize(50);  
      //mgr.getASPField("PROJ_NO").setValidation("PROJECT_TYPE_NO");
      
      headblk.addField("ACCIDENT_CONDITION").
              setInsertable().
              setLabel("QUAACCIDENTACCIDENTCONDITION: Accident Condition").
              setSize(50).
              setMaxLength(2000).
              setHeight(3);
      headblk.addField("LOSS_AMOUT","Number").
              setInsertable().
              setLabel("QUAACCIDENTLOSSAMOUT: Loss Amout").
              setSize(50);
      headblk.addField("HAPPEN_DATE","Date").
              setInsertable().
              setLabel("QUAACCIDENTHAPPENDATE: Happen Date").
              setSize(30);
      headblk.addField("DUTY_ORG").
              setInsertable().
              setDynamicLOV("GENERAL_ZONE").
              setDefaultNotVisible().
              //setLOVProperty("WHERE", "person_id = Fnd_Session_API.Get_Fnd_User").
              setLabel("QUAACCIDENTDUTYORG: Duty Org").
              setSize(30);

      headblk.addField("DUTY_ORG_DESC").
              setReadOnly().
              setFunction("GENERAL_ZONE_API.GET_ZONE_DESC ( :DUTY_ORG)").
              setLabel("QUAACCIDENTDUTYORG: Duty Org").
              setSize(30);
      mgr.getASPField("DUTY_ORG").setValidation("DUTY_ORG_DESC");
      headblk.addField("HANDLE_PERSON").             
              setLabel("QUAACCIDENTHANDLEPERSON: Handle Person").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setSize(30);
      headblk.addField("HANDLE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :HANDLE_PERSON)").
              setLabel("MATBORROWHANDLEPERSONINFONAME: Handle Person Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("HANDLE_PERSON").setValidation("HANDLE_PERSON_NAME");
      headblk.addField("HANDLE_TIME","Date").
              setLabel("QUAACCIDENTHANDLETIME: Handle Time").
              setInsertable().
              setSize(30);
      headblk.addField("INITIAL_DEAL_ADVICE").
              setInsertable().
              setLabel("QUAACCIDENTINITIALDEALADVICE: Initial Deal Advice").
              setSize(150).
              setMaxLength(500).
              setHeight(3);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("QUAACCIDENTNOTE: Note").
              setSize(150).
              setMaxLength(500).
              setHeight(3);
      headblk.addField("BELONG_TO_LU").
              setHidden().
              setLabel("QUAACCIDENTBELONGTOLU: Belong To Lu").
              setSize(50);
      

      headblk.setView("QUA_ACCIDENT");
      headblk.defineCommand("QUA_ACCIDENT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("QUAACCIDENTTBLHEAD: Qua Accidents");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      //headbar.addCustomCommand("printReport", "QUAACCIDENTREPORT: Print Report...");
      headbar.addCustomCommand("printReportCheck", "QUAACCIDENT: Print Report...");
      
      headlay.setDataSpan("HANDLE_TIME", 5);
      headlay.setDataSpan("INITIAL_DEAL_ADVICE", 5);
      headlay.setDataSpan("NOTE", 5);
      headlay.setSimple("CONSTRUCT_ORG_DESC");
      headlay.setSimple("SUPERVISION_ORG_DESC");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setSimple("HANDLE_PERSON_NAME");
      headlay.setSimple("SUB_PROJ_NAME");
      headlay.setSimple("DUTY_ORG_DESC");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("QUA_GRADE_DESC");
     
      
      
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuaAccident.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   
   
  
   
   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "QUAACCIDENTDESC: Quality Accident";
   }


   protected String getTitle()
   {
      return "QUAACCIDENTTITLE: Quality Accident";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());

   }
}
