package ifs.doctrw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.hzwflw.HzASPPageProviderWf;

public class DocReceiveTrans extends HzASPPageProviderWf {

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.doctrw.DocReceiveTrans");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   protected ASPTabContainer tabs;
   
   protected ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   protected ASPBuffer bc_Buffer;
   protected ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   protected ASPQuery q;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------
   
   protected ASPBlock       itemblk3;
   protected ASPRowSet      itemset3;
   protected ASPCommandBar  itembar3;
   protected ASPTable       itemtbl3;
   protected ASPBlockLayout itemlay3;
   
   protected ASPBlock       itemblk4;
   protected ASPRowSet      itemset4;
   protected ASPCommandBar  itembar4;
   protected ASPTable       itemtbl4;
   protected ASPBlockLayout itemlay4;
   
   private ASPBlock doc_receive_trans_send_blk;
   private ASPRowSet doc_receive_trans_send_set;
   private ASPCommandBar doc_receive_trans_send_bar;
   private ASPTable doc_receive_trans_send_tbl;
   private ASPBlockLayout doc_receive_trans_send_lay;
   
   
   


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------
   public DocReceiveTrans(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();
      trans   = mgr.newASPTransactionBuffer();
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
         String tempCommand = mgr.readValue("__COMMAND");
         if("ITEM1.SaveReturn".equals(tempCommand) 
               || "ITEM1.Delete".equals(tempCommand) 
               || "ITEM1.SaveNew".equals(tempCommand)
               || "ITEM1.OverviewSave".equals(tempCommand) ){
            headset.refreshRow();
         }
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DOC_REV")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("RECEIVE_TRANS_ID")) )
         okFind();
      
      tabs.saveActiveTab();
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
      
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      String tempPersonProjects = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_PROJECTS);
      StringBuffer sb = new StringBuffer("(");
      if(!"()".equals(tempPersonZones)){
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      }else{
         sb.append("(1=1)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());
      
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("DOCRECEIVETRANSNODATA: No data found.");
         headset.clear();
      }
      eval( doc_receive_trans_send_set.syncItemSets() );
      
      if (  headset.countRows() > 0 && headlay.isSingleLayout())
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

      cmd = trans.addEmptyCommand("HEAD","DOC_RECEIVE_TRANS_API.New__",headblk);
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
      if(headset.countRows() == 0){
         return;
      }
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(doc_receive_trans_send_blk);
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV", headset.getValue("DOC_REV"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,doc_receive_trans_send_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","DOC_RECEIVE_TRANS_SEND_API.New__",doc_receive_trans_send_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_DOC_CLASS", headset.getValue("DOC_CLASS"));
      cmd.setParameter("ITEM0_DOC_NO", headset.getValue("DOC_NO"));
      cmd.setParameter("ITEM0_DOC_SHEET", headset.getValue("DOC_SHEET"));
      cmd.setParameter("ITEM0_DOC_REV", headset.getValue("DOC_REV"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      doc_receive_trans_send_set.addRow(data);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Perform Header and Item functions  ---------------------------
   //-----------------------------------------------------------------------------


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
   public void  close()
   {

      performHEAD( "Close__" );
   }
   public void  re()
   {

      performHEAD( "Re_Open__" );
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      int size_long = 113;
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
      
      headblk.addField("DOC_CLASS").
      setMandatory().
      setInsertable().
      setLabel("DOCRECEIVETRANSDOCCLASS: Doc Class").
      setSize(12).
      setHidden();
      
      headblk.addField("DOC_NO").
      setMandatory().
      setInsertable().
      setLabel("DOCRECEIVETRANSDOCNO: Doc No").
      setSize(120).
      setHidden();
      
      headblk.addField("DOC_SHEET").
      setMandatory().
      setInsertable().
      setLabel("DOCRECEIVETRANSDOCSHEET: Doc Sheet").
      setSize(10).
      setHidden();
      
      headblk.addField("DOC_REV").
      setMandatory().
      setInsertable().
      setLabel("DOCRECEIVETRANSDOCREV: Doc Rev").
      setSize(6).
      setHidden();

      
      headblk.addField("REV_TITLE").
      setInsertable().
      setLabel("DOCRECEIVETRANSREVTITLE: Rev Title").
      setReadOnly().setWfProperties().
      setSize(size_long);

      headblk.addField("DOC_CODE").
      setInsertable().
      setLabel("DOCRECEIVETRANSDOCCODE: Doc Code").
      setReadOnly().
      setSize(20);
      
      headblk.addField("SEND_UNIT_NO").
      unsetInsertable().
      setDynamicLOV("GENERAL_ZONE_LOV").
      setLabel("DOCRECEIVETRANSSENDUNITNO: Send Unit No").
      setReadOnly().
      setSize(20);
      
      headblk.addField("SEND_UNIT_NAME").
      setLabel("DOCRECEIVETRANSSENDUNITNAME: Send Unit Name").
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)").
      setReadOnly().
      setSize(20);
      
      headblk.addField("MAIN_CONTENT").
      setReadOnly().
      setLabel("DOCRECEIVETRANSMAINCONTENT: Main Content").
      setSize(113).
      setHidden();
      
      headblk.addField("STATE").
      setLabel("DOCRECEIVETRANSSTATE: State").
      setReadOnly().
      setSize(30).
      setHidden();
      
      headblk.addField("HEAD_VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCMAWDOCISSUEHEADVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV", "NEWWIN").
      setAsImageField();
      
      headblk.addField("PROJ_NO").
//      setInsertable().
      setLabel("DOCRECEIVETRANSPROJNO: Proj No").
      setReadOnly().
      setSize(20);  
      
      headblk.addField("PROJ_NAME").
      setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
      setLabel("DOCRECEIVETRANSPROJNAME: Proj Name").
      setReadOnly().
      setSize(20); 
      
      headblk.addField("MAIN_TRANS_DEPT_NO").
      setReadOnly().
      setLabel("DOCRECEIVETRANSMAINTRANSDEPTNO: Main Trans Dept No").
      setSize(20);
      
      headblk.addField("CO_TRANS_DEPT_NO").
      setLabel("DOCRECEIVETRANSCOTRANSDEPTNO: Co Trans Dept No").
      setReadOnly().
      setSize(20);
      
      headblk.addField("READ_TRANS_DEPT_NO").
      setLabel("DOCRECEIVETRANSREADTRANSDEPTNO: Read Trans Dept No").
      setReadOnly().
      setSize(20);
      
      headblk.addField("DISTR_PERSON_NO").
      setInsertable().
      setLabel("DOCRECEIVETRANSDISTRPERSONNO: Distr Person No").
      setDynamicLOV("PERSON_INFO_LOV","TRANS_DEPT_NO").
      setLOVProperty("WHERE", "USER_ID IS NOT NULL").
      setSize(20).
      setHidden();
      
      headblk.addField("DISTR_LEADER_NO").
      setInsertable().
      setLabel("DOCRECEIVETRANSDISTRLEADERNO: Distr Leader No").
      setDynamicLOV("PERSON_INFO_LOV").
      setLOVProperty("WHERE", "USER_ID IS NOT NULL").
      setSize(20);
      
      headblk.addField("DISTR_LEADER_NAME").
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(:DISTR_LEADER_NO)").
      setLabel("DOCRECEIVETRANSDISTRLEADERNAME: Distr Leader Name").
      setSize(20);
      
      headblk.addField("DISTR_OPINION").
      setInsertable().
      setLabel("DOCRECEIVETRANSDISTROPINION: Distr Opinion").
      setHeight(3).
      setSize(size_long).
      setHidden();
      
      headblk.addField("LEADER_OPINION").
      setInsertable().
      setLabel("DOCRECEIVETRANSLEADEROPINION: Leader Opinion").
      setHeight(3).
      setSize(size_long);
      
      headblk.addField("TRANS_OPINION").
      setInsertable().
      setLabel("DOCRECEIVETRANSTRANSOPINION: Trans Opinion").
      setHeight(3).
      setSize(size_long).
      setHidden();
      
      headblk.addField("TRANS_RESULT").
      setInsertable().
      setLabel("DOCRECEIVETRANSTRANSRESULT: Trans Result").
      setHeight(3).
      setSize(size_long).
      setHidden();
      
      headblk.addField("EMERGENCY").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCRECEIVETRANSEMERGENCY: Emergency").
      setSize(20).
      setHidden();
      
      headblk.addField("NEED_RECEIPT").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCRECEIVETRANSNEEDRECEIPT: Need Receipt").
      setSize(20).
      setHidden();
      
      headblk.addField("REPLIED").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCRECEIVETRANSREPLIED: Replied").
      setSize(20).
      setHidden();
      
      headblk.addField("REPLY_NO").
      setInsertable().
      setLabel("DOCRECEIVETRANSREPLYNO: Reply No").
      setSize(20).
      setHidden();
      
      //Hidden Field begin...
      headblk.addField("ZONE_NO").
      setInsertable().
      setLabel("DOCRECEIVETRANSZONENO: Zone No").
      setSize(20).
      setHidden();
      
      
      headblk.addField("TRANS_DEPT_NO").
      setInsertable().
      setLabel("DOCRECEIVETRANSTRANSDEPTNO: Trans Dept No").
      setSize(20).
      setHidden();
      
      headblk.addField("IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
      setReadOnly().
      setHidden().
      setLabel("DOCTITLEISELEDOC: Is Ele Doc").
      setSize(5);
      //Hidden Field end.  
      
      
      headblk.addField("RECEIVE_TRANS_ID").
      setReadOnly().
      setLabel("DOCRECEIVETRANSRECEIVETRANSID: Receive Trans Id").
      setSize(20);
      
      headblk.addField("START_DATE","Date").
      setInsertable().
      setLabel("DOCRECEIVETRANSSTARTDATE: Start Date").
      setSize(20);
      headblk.addField("FINISH_DATE","Date").
      setInsertable().
      setLabel("DOCRECEIVETRANSFINISHDATE: Finish Date").
      setSize(20);
      
      headblk.addField("DIST_DATE","Date").
      setInsertable().
      setLabel("DOCRECEIVETRANSDISTDATE: Dist Date").
      setSize(20);
      
      headblk.addField("PURPOSE_NO").
      setReadOnly().
      setDynamicLOV("DOC_COMMUNICATION_SEQ").
      setLabel("DOCISSUEPURPOSENO: Purpose No").
      setSize(20);
      
      headblk.addField("PURPOSE_NAME").
      setLabel("DOCRECEIVETRANSPURPOSENAME: Purpose Name").
      setFunction("DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("PURPOSE_NO").setValidation("PURPOSE_NAME");
      
      headblk.addField("TRANSFER_NO").
      setLabel("DOCRECEIVETRANSTRANSFERNO: Transfer No").
      setReadOnly().
      setSize(20).
      setHidden();
      
      headblk.addField("SUB_CLASS").
      setInsertable().
      setLabel("DOCRECEIVETRANSSUBCLASS: Sub Class").
      setDynamicLOV("DOC_SUB_CLASS").
      setSize(20).
      setHidden();
      
      headblk.addField("SUB_CLASS_NAME").setFunction("Doc_Sub_Class_Api.Get_Sub_Class_Name(:DOC_CLASS,:SUB_CLASS)").setLabel("DOCRECEIVETRANSSUBCLASSNAME: Sub Class Name").setSize(20).setHidden(); 
      mgr.getASPField ("SUB_CLASS").setValidation("SUB_CLASS_NAME");
      
      headblk.addField("PAGES","Number").
      setInsertable().
      setLabel("DOCRECEIVETRANSPAGES: Pages").
      setReadOnly().
      setSize(20);
      
      headblk.addField("ATTACH_PAGES","Number").
      setInsertable().
      setLabel("DOCRECEIVETRANSATTACHPAGES: Attach Pages").
      setReadOnly().
      setSize(20);
      
      headblk.addField("RECEIPT").
      setCheckBox("FALSE,TRUE").
      setLabel("DOCRECEIVETRANSRECEIVERECEIPT: Receipt").
      setReadOnly().
      setSize(20);
      
      headblk.addField("RECEIPT_REQUEST","Date").
      setInsertable().
      setLabel("DOCRECEIVETRANSRECEIPTREQUEST: Receipt Request").
      setReadOnly().
      setSize(20);
      

      
      headblk.addField("USER_CREATED").
      setLabel("DOCMAWDOCISSUEUSERCREATED: Created By").
      setDefaultNotVisible().
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCPACKAGEIDRESPONSE: List of Person Id")).
      setReadOnly().
      setHidden().
      setSize(20);
      headblk.addField("USER_CREATED_NAME").setFunction("Fnd_User_Api.Get_Description(:USER_CREATED)").setReadOnly().setSize(20).setHidden();
      
      headblk.addField("DT_CRE","Date").
      setSize(20).
      setReadOnly().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCISSUEDTCRE: Date Created").
      setHidden();
      
      headblk.addField("RECEIVE_DATE","Date").
      setLabel("DOCISSUERECEIVERECEIVEDATE: Receive Date").
      setReadOnly().
      setSize(20);
      
      headblk.setView("DOC_RECEIVE_TRANS_REFERENCE");
      headblk.defineCommand("DOC_RECEIVE_TRANS_API","New__,Modify__,Remove__,Close__,Re_Open__");
      headblk.enableFuncFieldsNonSelect();
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("Close","DOCRECEIVETRANSCLOSE: Close Doc Receive Trans");
      headbar.addCustomCommand("Re","DOCRECEIVETRANSRE: Re Doc Receive Trans");
      
      // Tab commands
      headbar.addCustomCommand("activateConsistsOf", "Consists Of");
      headbar.addCustomCommand("activateWhereUsed", "Where Used");
      headbar.addCustomCommand("activateSendTo", "Send To");


      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      headlay.setDataSpan("REV_TITLE", 5);
      headlay.setDataSpan("STATE", 5);
      headlay.setDataSpan("DIST_DATE", 5);
      headlay.setDataSpan("LEADER_OPINION", 5);
      
      
      headlay.setSimple("PURPOSE_NAME");
      headlay.setSimple("PROJ_NAME");
      headlay.setSimple("SEND_UNIT_NAME");
      headlay.setSimple("DISTR_LEADER_NAME");
//      MAIN_TRANS_DEPT_NO,CO_TRANS_DEPT_NO,READ_TRANS_DEPT_NO
      
      String fieldGroup1 = "REV_TITLE,RECEIVE_TRANS_ID,DOC_CODE,PURPOSE_NO,PURPOSE_NAME,HEAD_VIEW_FILE,PROJ_NO,PROJ_NAME,RECEIVE_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,";
      String fieldGroup2 = "SEND_UNIT_NO,SEND_UNIT_NAME,START_DATE,FINISH_DATE,STATE,DISTR_LEADER_NO,DISTR_LEADER_NAME,DIST_DATE,LEADER_OPINION,MAIN_TRANS_DEPT_NO,CO_TRANS_DEPT_NO,READ_TRANS_DEPT_NO";
      headlay.setFieldOrder(fieldGroup1 + fieldGroup2);
      
//      headlay.defineGroup("Main", "OBJID,OBJVERSION,OBJSTATE,OBJEVENTS,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV," +
//            "REV_TITLE,DOC_CODE,TRANSFER_NO,PURPOSE_NO,PURPOSE_NAME,HEAD_VIEW_FILE," +
//            "SEND_UNIT_NO,SEND_UNIT_NAME,MAIN_CONTENT,STATE", false, true);
//      headlay.defineGroup(mgr.translate("DOCRECEIVETRANSDOCINFOGROUP: Document Info"),
//            "PROJ_NO,PROJ_NAME,RECEIVE_DATE,PAGES,ATTACH_PAGES,RECEIPT,RECEIPT_REQUEST,USER_CREATED,USER_CREATED_NAME,DT_CRE,MAIN_TRANS_DEPT_NO,CO_TRANS_DEPT_NO,READ_TRANS_DEPT_NO," +
//            "DISTR_PERSON_NO,DISTR_LEADER_NO,DISTR_OPINION,LEADER_OPINION," +
//            "TRANS_OPINION,TRANS_RESULT,EMERGENCY,NEED_RECEIPT,REPLIED," +
//            "REPLY_NO,TRANS_DEPT_NO,IS_ELE_DOC", true, false);

      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCRECEIVETRANSTBLHEAD: Doc Receive Transs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      //
      // Document Structure - Consists of
      //
      
      itemblk3 = mgr.newASPBlock("ITEM3");
      
      itemblk3.disableDocMan();
      
      itemblk3.addField("ITEM3_OBJID").
      setDbName("OBJID").
      setHidden();
      
      itemblk3.addField("ITEM3_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();
      
      
      itemblk3.addField("LEVEL_STR").
      setReadOnly().
      setLabel("DOCISSUEFILESLEVELSTR: Level").
      setBold().
      setSize(20);

      itemblk3.addField("ITEM3_VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCMAWDOCISSUEHEADVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV", "NEWWIN").
      setAsImageField();
      
      itemblk3.addField("REPLACE_REVISION_TITLE").
      setHidden().
      setReadOnly().
      setBold().
      setFunction("''").
      setLabel("DOCMAWDOCISSUEREPLACEREVISIONTITLE: Enter Replacement Document:");
      
      itemblk3.addField("SUB_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUESUBDOCCLASS: Doc Class");
      
      itemblk3.addField("SUB_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","SUB_DOC_CLASS DOC_CLASS").
      setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO","SSUBDOCTITLE").
      setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV"). //Bug Id 85223
      setLabel("DOCMAWDOCISSUESUBDOCNO: Doc No").setHidden();
      
      itemblk3.addField("SUB_DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE_LOV1", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO").
      setLOVProperty("TITLE", mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets")).
      setLabel("DOCMAWDOCISSUESUBDOCSHEET: Doc Sheet").setHidden();
      
      itemblk3.addField("SUB_DOC_REV").
      setSize(20).
      setMaxLength(6).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE", "SUB_DOC_CLASS DOC_CLASS, SUB_DOC_NO DOC_NO, SUB_DOC_SHEET DOC_SHEET").
      setCustomValidation("SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV","NNOOFCHILDREN").
      setLabel("DOCMAWDOCISSUESUBDOCREV: Revision");
      
      itemblk3.addField("SSUBDOCTITLE").
      setSize(40).
      setMaxLength(80).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_REV_Title(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)").
      setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV").
      setLabel("DOCMAWDOCISSUESSUBDOCTITLE: Title");
      
      
      itemblk3.addField("ITEM3_ASSOCIATE_ID").
      setDbName("ASSOCIATE_ID").
      setSize(10).
      setInsertable().
      setDynamicLOV("DOC_ASSOCIATION_TYPE").
      setLabel("DOCMAWDOCISSUESASSOCIATEID: Associate Id");
      
      itemblk3.addField("ITEM3_ASSOCIATE_DESC").
      setSize(10).
      setReadOnly().
      setFunction("DOC_ASSOCIATION_TYPE_API.Get_Description(:ITEM3_ASSOCIATE_ID)").
      setLabel("DOCMAWDOCISSUESASSOCIATEDESC: Associate Desc");
      mgr.getASPField("ITEM3_ASSOCIATE_ID").setValidation("ITEM3_ASSOCIATE_DESC");
      
      itemblk3.addField("ITEMBLK3_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_State (:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)").
      setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");
      
      itemblk3.addField("NNOOFCHILDREN","Number").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setFunction("DOC_STRUCTURE_API.Number_Of_Children_(:SUB_DOC_CLASS, :SUB_DOC_NO, :SUB_DOC_SHEET, :SUB_DOC_REV)").
      setLabel("DOCMAWDOCISSUENNOOFCHILDREN: No of Child documents");
      
      itemblk3.addField("CONSISTS_OF_RELATIVE_PATH").
      setDbName("RELATIVE_PATH").
      setSize(40).
      setMaxLength(256).
      setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");
      
      itemblk3.addField("ITEM3_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();
      
      itemblk3.addField("ITEM3_DOC_NO").
      setDbName("DOC_NO").
      setHidden();
      
      itemblk3.addField("ITEM3_DOC_SHEET").
      setDbName("DOC_SHEET").
      setHidden();
      
      itemblk3.addField("ITEM3_DOC_REV").
      setDbName("DOC_REV").
      setHidden();
      
      
      itemblk3.addField("ITEM3_IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:SUB_DOC_CLASS,:SUB_DOC_NO,:SUB_DOC_SHEET,:SUB_DOC_REV)").
      setReadOnly().
      setLabel("DOCTITLEISELEDOC: Is Ele Doc").
      setHidden().
      setSize(5);
      
      itemblk3.addField("ITEM3_PAGE_URL").
      setFunction("DOC_CLASS_API.Get_Page_Url(:ITEM3_DOC_CLASS)").
      setHidden();
      
      itemblk3.setView("(SELECT ds.*, LPAD(' ', 2 * (LEVEL - 1), ' ') || 'й╕йд' LEVEL_STR FROM DOC_STRUCTURE ds" +
            " CONNECT BY ds.doc_class = PRIOR ds.sub_doc_class " +
            " AND        ds.doc_no = PRIOR ds.sub_doc_no " +
            " AND        ds.doc_sheet = PRIOR ds.sub_doc_sheet " +
            " AND        ds.doc_rev = PRIOR ds.sub_doc_rev " +
            " START WITH ds.doc_class = ? " +
            " AND        ds.doc_no = ? " +
            " AND        ds.doc_sheet = ? " +
            " AND        ds.doc_rev = ? )");
      itemblk3.defineCommand("DOC_STRUCTURE_API","New__,Modify__,Remove__");
      itemblk3.setMasterBlock(headblk);
      
      itemset3 = itemblk3.getASPRowSet();
      
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnITEM3");
      itembar3.addCustomCommand("transferToDocInfoFromConsistOf",mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));
      itembar3.addSecureCustomCommand("replaceRevision", mgr.translate("DOCMAWDOCISSUEREPLACEREVISION: Replace Revision..."),"Doc_Structure_API.Replace_Issue_"); //Bug Id 70286
      itembar3.addCustomCommand("previousLevel",mgr.translate("DOCMAWDOCISSUEPREVLEVEL: Previous Level"));
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCISSUEDOCCONSIT: Consists Of"));
      
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
      itemlay3.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      
      
      //
      // Doucment Structure - Where used
      //
      
      itemblk4 = mgr.newASPBlock("ITEM4");
      
      itemblk4.disableDocMan();
      
      itemblk4.addField("ITEM4_OBJID").
      setDbName("OBJID").
      setHidden();
      
      itemblk4.addField("ITEM4_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();
      
      itemblk4.addField("ITEM4_LEVEL_STR").
      setReadOnly().
      setDbName("LEVEL_STR").
      setLabel("DOCMAWDOCISSUEITEM4LEVELSTR: Level").
      setBold().
      setSize(20);

      itemblk4.addField("ITEM4_VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCMAWDOCISSUEITEM4VIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", 
            "ITEM4_DOC_CLASS,ITEM4_DOC_NO,ITEM4_DOC_SHEET,ITEM4_DOC_REV", "NEWWIN").
      setAsImageField();
      
      itemblk4.addField("ITEM4_DOC_CLASS").
      setDbName("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCISSUEITEM4DOCCLASS: Doc Class");
      
      itemblk4.addField("ITEM4_DOC_NO").
      setDbName("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","ITEM4_DOC_CLASS DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO","ITEM4_SSUBDOCTITLE").
      setFieldHyperlink("DocIssue.page", "ITEM4_PAGE_URL","ITEM4_DOC_CLASS DOC_CLASS,ITEM4_DOC_NO DOC_NO,ITEM4_DOC_SHEET DOC_SHEET,ITEM4_DOC_REV DOC_REV"). //Bug Id 85223
      setLabel("DOCMAWDOCISSUEITEM4DOCNO: Doc No").setHidden();
      
      itemblk4.addField("ITEM4_DOC_SHEET").
      setDbName("DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE_LOV1", "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO").
      setLOVProperty("TITLE", mgr.translate("DOCMAWDOCISSUESUBDOCSHEET1: List of Based on Sheets")).
      setLabel("DOCMAWDOCISSUEITEM4DOCSHEET: Doc Sheet").setHidden();
      
      itemblk4.addField("ITEM4_DOC_REV").
      setDbName("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE", "ITEM4_DOC_CLASS DOC_CLASS, ITEM4_DOC_NO DOC_NO, ITEM4_DOC_SHEET DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","ITEM4_NNOOFCHILDREN").
      setLabel("DOCMAWDOCISSUEITEM4DOCREV: Revision");
      
      itemblk4.addField("ITEM4_SSUBDOCTITLE").
      setSize(40).
      setMaxLength(80).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_REV_Title(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)").
      setFieldHyperlink("DocIssue.page", "ITEM4_PAGE_URL","ITEM4_DOC_CLASS DOC_CLASS,ITEM4_DOC_NO DOC_NO,ITEM4_DOC_SHEET DOC_SHEET,ITEM4_DOC_REV DOC_REV").
      setLabel("DOCMAWDOCISSUEITEM4SSUBDOCTITLE: Title");
      mgr.getASPField("ITEM4_DOC_NO").setValidation("ITEM4_SSUBDOCTITLE");
      
      itemblk4.addField("ITEM4_ASSOCIATE_ID").
      setDbName("ASSOCIATE_ID").
      setSize(10).
      setInsertable().
      setDynamicLOV("DOC_ASSOCIATION_TYPE").
      setLabel("DOCMAWDOCISSUESASSOCIATEID: Associate Id");
      
      itemblk4.addField("ITEM4_ASSOCIATE_DESC").
      setSize(10).
      setReadOnly().
      setFunction("DOC_ASSOCIATION_TYPE_API.Get_Description(:ITEM4_ASSOCIATE_ID)").
      setLabel("DOCMAWDOCISSUESASSOCIATEDESC: Associate Desc");
      mgr.getASPField("ITEM4_ASSOCIATE_ID").setValidation("ITEM4_ASSOCIATE_DESC");

      itemblk4.addField("ITEMBLK4_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_State (:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)").
      setLabel("DOCMAWDOCISSUECONSISTSOFSTATE: Document Status");
      
      itemblk4.addField("ITEM4_NNOOFCHILDREN","Number").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setFunction("DOC_STRUCTURE_API.Number_Of_Children_(:ITEM4_DOC_CLASS, :ITEM4_DOC_NO, :ITEM4_DOC_SHEET, :ITEM4_DOC_REV)").
      setLabel("DOCMAWDOCISSUEITEM4NNOOFCHILDREN: No of Child documents");
      mgr.getASPField("ITEM4_DOC_REV").setValidation("ITEM4_NNOOFCHILDREN");
      
      itemblk4.addField("WHERE_USED_RELATIVE_PATH").
      setDbName("RELATIVE_PATH").
      setSize(40).
      setMaxLength(256).
      setLabel("DOCMAWDOCISSUERELATIVEPATH: Relative Path");
      
      itemblk4.addField("ITEM4_SUB_DOC_CLASS").
      setDbName("SUB_DOC_CLASS").
      setHidden();
      
      itemblk4.addField("ITEM4_SUB_DOC_NO").
      setDbName("SUB_DOC_NO").
      setHidden();
      
      itemblk4.addField("ITEM4_SUB_DOC_SHEET").
      setDbName("SUB_DOC_SHEET").
      setHidden();
      
      itemblk4.addField("ITEM4_SUB_DOC_REV").
      setDbName("SUB_DOC_REV").
      setHidden();
      
      itemblk4.addField("ITEM4_IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:ITEM4_DOC_CLASS,:ITEM4_DOC_NO,:ITEM4_DOC_SHEET,:ITEM4_DOC_REV)").
      setReadOnly().
      setLabel("DOCMAWDOCISSUEITEM4ISELEDOC: Is Ele Doc").
      setHidden().
      setSize(5);
      
      itemblk4.addField("ITEM4_PAGE_URL").
      setFunction("DOC_CLASS_API.Get_Page_Url(:ITEM4_DOC_CLASS)").
      setHidden();
      
      itemblk4.setView("(SELECT ds.*, LPAD(' ', 2 * (LEVEL - 1), ' ') || 'й╕йд' LEVEL_STR FROM DOC_STRUCTURE ds" +
            " CONNECT BY PRIOR ds.doc_class = ds.sub_doc_class " +
            " AND        PRIOR ds.doc_no = ds.sub_doc_no " +
            " AND        PRIOR ds.doc_sheet = ds.sub_doc_sheet " +
            " AND        PRIOR ds.doc_rev = ds.sub_doc_rev " +
            " START WITH ds.sub_doc_class = ? " +
            " AND        ds.sub_doc_no = ? " +
            " AND        ds.sub_doc_sheet = ? " +
            " AND        ds.sub_doc_rev = ? )");
      itemblk4.defineCommand("DOC_STRUCTURE_API","New__,Modify__,Remove__");
      itemblk4.setMasterBlock(headblk);
      
      itemset4 = itemblk4.getASPRowSet();
      
      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itembar4.defineCommand(itembar4.DUPLICATEROW,"duplicateRowITEM4");
      itembar4.addCustomCommand("transferToDocInfoFromWhereUsed",mgr.translate("DOCMAWDOCISSUEDOCINFO: Document Info..."));
     
      
      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCISSUEDOCWHEUSED: Where Used"));
      
      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      
      
      doc_receive_trans_send_blk = mgr.newASPBlock("ITEM1");
      doc_receive_trans_send_blk.addField("ITEM0_OBJID").
      setHidden().
      setDbName("OBJID");
      doc_receive_trans_send_blk.addField("ITEM0_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");
      
      doc_receive_trans_send_blk.addField("ITEM0_DOC_CLASS").
      setDbName("DOC_CLASS").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCRECEIVETRANSSENDITEM0DOCCLASS: Doc Class").
      setSize(12);
      
      doc_receive_trans_send_blk.addField("ITEM0_DOC_NO").
      setDbName("DOC_NO").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCRECEIVETRANSSENDITEM0DOCNO: Doc No").
      setSize(120);
      
      doc_receive_trans_send_blk.addField("ITEM0_DOC_SHEET").
      setDbName("DOC_SHEET").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCRECEIVETRANSSENDITEM0DOCSHEET: Doc Sheet").
      setSize(10);
      
      doc_receive_trans_send_blk.addField("ITEM0_DOC_REV").
      setDbName("DOC_REV").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCRECEIVETRANSSENDITEM0DOCREV: Doc Rev").
      setSize(6);
      
      doc_receive_trans_send_blk.addField("SEND_TO_DEPT").
      setMandatory().
      setInsertable().
      setLabel("DOCRECEIVETRANSSENDSENDTODEPT: Send To Dept").
      setDynamicLOV("GENERAL_ORGANIZATION").
      setLOVProperty("WHERE", " ORG_TYPE = 'Dep' ").
      setSize(20);
      
      doc_receive_trans_send_blk.addField("SEND_TO_DEPT_DESC").
      setReadOnly().
      setLabel("DOCRECEIVETRANSSENDSENDTODEPT: Send To Dept Desc").
      setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:SEND_TO_DEPT)").
      setSize(20);
      mgr.getASPField("SEND_TO_DEPT").setValidation("SEND_TO_DEPT_DESC");
       
      doc_receive_trans_send_blk.addField("IS_MAIN_DEPT").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCRECEIVETRANSSENDISMAINDEPT: Is Main Dept").
      setSize(20);
      
      doc_receive_trans_send_blk.addField("IS_CO_DEPT").
      setInsertable().
      setLabel("DOCRECEIVETRANSSENDISCODEPT: Is Co Dept").
      setCheckBox("FALSE,TRUE").
      setSize(20);
      
      doc_receive_trans_send_blk.addField("IS_READ_DEPT").
      setInsertable().
      setLabel("DOCRECEIVETRANSSENDISREADDEPT: Is Read Dept").
      setCheckBox("FALSE,TRUE").
      setSize(20);
      
      doc_receive_trans_send_blk.setView("DOC_RECEIVE_TRANS_SEND");
      doc_receive_trans_send_blk.defineCommand("DOC_RECEIVE_TRANS_SEND_API","New__,Modify__,Remove__");
      doc_receive_trans_send_blk.setMasterBlock(headblk);
      doc_receive_trans_send_set = doc_receive_trans_send_blk.getASPRowSet();
      doc_receive_trans_send_bar = mgr.newASPCommandBar(doc_receive_trans_send_blk);
      doc_receive_trans_send_bar.defineCommand(doc_receive_trans_send_bar.OKFIND, "okFindITEM1");
      doc_receive_trans_send_bar.defineCommand(doc_receive_trans_send_bar.NEWROW, "newRowITEM1");
      doc_receive_trans_send_tbl = mgr.newASPTable(doc_receive_trans_send_blk);
      doc_receive_trans_send_tbl.setTitle("DOCRECEIVETRANSSENDITEMHEAD1: DocReceiveTransSend");
      doc_receive_trans_send_tbl.enableRowSelect();
      doc_receive_trans_send_tbl.setWrap();
      doc_receive_trans_send_lay = doc_receive_trans_send_blk.getASPBlockLayout();
      doc_receive_trans_send_lay.setDefaultLayoutMode(doc_receive_trans_send_lay.MULTIROW_LAYOUT);


      
      
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWDOCISSUESENDTO: Send To"), "javascript:commandSet('MAIN.activateSendTo','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUECONSISTS: Consists Of"), "javascript:commandSet('MAIN.activateConsistsOf','')");
      tabs.addTab(mgr.translate("DOCMAWDOCISSUEWHEREUSED: Where Used"), "javascript:commandSet('MAIN.activateWhereUsed','')");

   }
   
   public void activateConsistsOf()
   {
      tabs.setActiveTab(2);
      okFindITEM3();
   }
   
   public void activateWhereUsed()
   {
      tabs.setActiveTab(3);
      okFindITEM4();
   }
   
   public void activateSendTo()
   {
      tabs.setActiveTab(1);
      okFindITEM1();
   }
   
   
   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
      if (tabs.getActiveTab()== 2)
      {
         if (headset.countRows() == 0)
            return;
         
         trans.clear();
         q = trans.addEmptyQuery(itemblk3);
//         q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
         q.addParameter("DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk3);
         headset.goTo(headrowno);
      }
   }
   
   
   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();
      
      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      //Bug ID 45944, inoslk, start
//      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }
   
   
   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
      if (tabs.getActiveTab()== 3)
      {
         if (headset.countRows() == 0)
            return;
         
         trans.clear();
         q = trans.addQuery(itemblk4);
//         q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
         q.addParameter("SUB_DOC_CLASS", headset.getValue("DOC_CLASS"));
         q.addParameter("SUB_DOC_NO", headset.getValue("DOC_NO"));
         q.addParameter("SUB_DOC_SHEET", headset.getValue("DOC_SHEET"));
         q.addParameter("SUB_DOC_REV", headset.getValue("DOC_REV") );
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans, itemblk4);
         headset.goTo(headrowno);
      }
   }
   
   
   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();
      
      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
//      q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ?");
      q.addParameter("SUB_DOC_CLASS",headset.getValue("DOC_CLASS"));
      q.addParameter("SUB_DOC_NO",headset.getValue("DOC_NO"));
      q.addParameter("SUB_DOC_SHEET",headset.getValue("DOC_SHEET"));
      q.addParameter("SUB_DOC_REV",headset.getValue("DOC_REV"));
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }
   
   


   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.adjust();
      // fill function body
      
      if(headblk.getFuncFieldsNonSelect()){
         this.setFuncFieldValue(headblk);
      }
      
      headbar.removeCustomCommand("activateConsistsOf");
      headbar.removeCustomCommand("activateWhereUsed");
      headbar.removeCustomCommand("activateSendTo");
      headbar.disableCommand(ASPCommandBar.NEWROW);
      
      itembar3.disableCommand(ASPCommandBar.NEWROW);
      itembar3.disableCommand(ASPCommandBar.EDITROW);
      itembar3.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      itembar3.disableCommand(ASPCommandBar.DELETE);
      itembar3.disableCommand(ASPCommandBar.DUPLICATEROW);
      
      itembar4.disableCommand(ASPCommandBar.NEWROW);
      itembar4.disableCommand(ASPCommandBar.EDITROW);
      itembar4.disableCommand(ASPCommandBar.OVERVIEWEDIT);
      itembar4.disableCommand(ASPCommandBar.DELETE);
      itembar4.disableCommand(ASPCommandBar.DUPLICATEROW);
      
      /*if(headlay.isFindLayout()){
         mgr.getASPField("SEND_UNIT_NAME").setHidden();
         mgr.getASPField("PROJ_NAME").setHidden();
      }*/
      
   }
   
   protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
   {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0)
      {
         if(("VIEW_FILE").equals(imageField.getName())||("HEAD_VIEW_FILE").equals(imageField.getName()))
         {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }
            else
            {
               return "";
            }
         }
         else if ("ITEM3_VIEW_FILE".equals(imageField.getName()))
         {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM3_IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }
            else
            {
               return "";
            }
         }
         else if ("ITEM4_VIEW_FILE".equals(imageField.getName()))
         {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM4_IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }
            else
            {
               return "";
            }
         }
      }
      return "";
  }
   
   
   
   
   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "DOCRECEIVETRANSDESC: Doc Receive Trans";
   }


   protected String getTitle()
   {
      return "DOCRECEIVETRANSTITLE: Doc Receive Trans";
   }


   protected void printContents() throws FndException
   {
      super.printContents();

      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
      
      if (headset.countRows()>0)
      {
         if (headlay.isSingleLayout()||headlay.isCustomLayout())
         {
            
            appendToHTML(tabs.showTabsInit());
            if (tabs.getActiveTab()== 1)
            {
               appendToHTML(doc_receive_trans_send_lay.show());
            }
            else if (tabs.getActiveTab()== 2)
            {
               appendToHTML(itemlay3.show());
            }
            else if (tabs.getActiveTab()== 3)
            {
               appendToHTML(itemlay4.show());
            }
            
            appendToHTML(tabs.showTabsFinish());
            
//            //Added by lqw 20131218 begin
//            appendDirtyJavaScript(" if(parent){\n");
//            appendDirtyJavaScript("    if(parent.document.getElementsByTagName){\n");
//            appendDirtyJavaScript("       var allLinks = parent.document.getElementsByTagName('a');\n");
//            appendDirtyJavaScript("       var hasReject = false;\n");
//            appendDirtyJavaScript("       var hasHbReject = false;\n");
//            appendDirtyJavaScript("       var rejectLinkCtl;\n");
//            appendDirtyJavaScript("       var hbRejectLinkCtl;\n");
//            appendDirtyJavaScript("       for(i = 0;i<allLinks.length;i++){\n");
//            appendDirtyJavaScript("          if(hasReject && hasHbReject){\n");
//            appendDirtyJavaScript("             break;\n");
//            appendDirtyJavaScript("          }\n");
//            appendDirtyJavaScript("          var tempLink = allLinks[i];\n");
//            appendDirtyJavaScript("          if('btnReject' == tempLink.name){\n");
//            appendDirtyJavaScript("             hasReject = true;\n");
//            appendDirtyJavaScript("             rejectLinkCtl = tempLink;\n");
//            appendDirtyJavaScript("             continue;\n");
//            appendDirtyJavaScript("          }\n");
//            appendDirtyJavaScript("          var onclickAttr = tempLink.getAttribute('onclick');\n");
//            appendDirtyJavaScript("          if(onclickAttr){\n");
//            appendDirtyJavaScript("             var tempStr = onclickAttr.toString();//alert(onclickAttr.toString());\n");
//            appendDirtyJavaScript("             if(tempStr && tempStr.indexOf('hbreject') > -1){\n"); 
//            appendDirtyJavaScript("                hasHbReject = true;//alert(tempStr); \n"); 
//            appendDirtyJavaScript("                hbRejectLinkCtl = tempLink;\n"); 
//            appendDirtyJavaScript("                continue;\n"); 
//            appendDirtyJavaScript("                \n"); 
//            appendDirtyJavaScript("             }\n"); 
//            appendDirtyJavaScript("          }\n");
//            appendDirtyJavaScript("       }\n");
//            appendDirtyJavaScript("       if(hasReject && hasHbReject){\n");
//            appendDirtyJavaScript("          var rejectImgSpan = rejectLinkCtl.firstChild;\n");
//            appendDirtyJavaScript("          rejectImgSpan.style.display = 'none';\n");
//            appendDirtyJavaScript("          rejectLinkCtl.style.display = 'none';\n");
//            appendDirtyJavaScript("       }\n");
//            appendDirtyJavaScript("    }\n");
//            appendDirtyJavaScript(" }\n");
//          //Added by lqw 20131218 end;
         }
      }
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
   
   protected String sepecifyNextNode() throws FndException{
      //Add some Code Here to specify next nodeId(s).
      ASPManager mgr = getASPManager();
      String curNodeId = mgr.readValue("_WF_HZ_CURNODEID_");
      String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
       
      if("Node1".equals(curNodeId) || "Node18".equals(curNodeId)){
         String mainSend   = headset.getValue("MAIN_TRANS_DEPT_NO");
         String coSend     = headset.getValue("CO_TRANS_DEPT_NO");
         String readSend   = headset.getValue("READ_TRANS_DEPT_NO");
         
         StringBuilder sb = new StringBuilder();
         
         if("Node1".equals(curNodeId)){
            if(!Str.isEmpty(mainSend)){
               sb.append("Line2~Node3|Line25~Node4").append("&");
            }
            if(!Str.isEmpty(coSend)){
               sb.append("Line2~Node3|Line26~Node6").append("&");
            }
            if(!Str.isEmpty(readSend)){
               sb.append("Line2~Node3|Line27~Node7").append("&");
            }
         }else if("Node18".equals(curNodeId)){
            if(!Str.isEmpty(mainSend)){
               sb.append("Line29~Node3|Line25~Node4").append("&");
            }
            if(!Str.isEmpty(coSend)){
               sb.append("Line29~Node3|Line26~Node6").append("&");
            }
            if(!Str.isEmpty(readSend)){
               sb.append("Line29~Node3|Line27~Node7").append("&");
            }
         }else{
            
         }
         
         if(sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
         }
         return sb.toString();//sb.toString();
      }else{
         return null;
      } 
   }
   
}
