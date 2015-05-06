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

public class ConEntrustQuantitiesVisa extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConEntrustQuantitiesVisa");

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

   private ASPBlock con_quantities_visa_line_blk;
   private ASPRowSet con_quantities_visa_line_set;
   private ASPCommandBar con_quantities_visa_line_bar;
   private ASPTable con_quantities_visa_line_tbl;
   private ASPBlockLayout con_quantities_visa_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConEntrustQuantitiesVisa (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.run();
      String comnd;
      if( mgr.commandBarActivated() ){
         eval(mgr.commandBarFunction());
         comnd = mgr.readValue("__COMMAND");
         if (  "ITEM1.SaveReturn".equals(comnd) ||  "ITEM1.Delete".equals(comnd) )
            headset.refreshAllRows();
         else if("ITEM1.SaveNew".equals(comnd))
            headset.refreshAllRows();
       }
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
      q.addWhereCondition("CONTRACT_ENTRUST_NO = ?");
      q.addParameter("CONTRACT_ENTRUST_NO", "ENTRUST");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("CONQUANTITIESVISANODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","CON_QUANTITIES_VISA_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("CONTRACT_ENTRUST_NO", "ENTRUST");
      headset.addRow(data);
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

      q = trans.addQuery(con_quantities_visa_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,con_quantities_visa_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","CON_QUANTITIES_VISA_LINE_API.New__",con_quantities_visa_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      con_quantities_visa_line_set.addRow(data);
   }
   
 //-----------------------------------------------------------------------------
   //------------------------  Validate functions  ---------------------------
   //-----------------------------------------------------------------------------
   public void validate() {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd1;
      ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();
      ASPCommand cmd2;
      
      String val = mgr.readValue("VALIDATE");
      
      String txt = "";
      String  contract_id = "";
      String  contractEntrustName = "";
      String  org_no = "";
      String  org_name = "";
      float tempValue;
      
      if ("SERIAL_NO".equals(val)) {
         cmd = trans.addCustomFunction("GETCONTRACTENTRUSTNAME", 
               "BID_PROJ_ENTRUST_API.Get_Entrust_Name", "NAME");
         cmd.addParameter("PROJ_NO");
         cmd.addParameter("SERIAL_NO");
         
         
         cmd = trans.addCustomFunction("GETCONTRACTID", 
               "BID_PROJ_ENTRUST_API.Get_Contract_Id", "CONTRACT_ID");
         cmd.addParameter("PROJ_NO");
         cmd.addParameter("SERIAL_NO");
         
         trans = mgr.validate(trans);
         
         contractEntrustName = trans.getValue("GETCONTRACTENTRUSTNAME/DATA/NAME");
         contract_id = trans.getValue("GETCONTRACTID/DATA/CONTRACT_ID");
         
         cmd1 = trans1.addCustomFunction("GETORG", 
               "GENERAL_ORGANIZATION_API.GET_ORG_NO", "ORG");
         cmd1.addParameter("PROJ_NO");
         cmd1.addParameter("CONTRACT_ID",contract_id);
         
         trans1 = mgr.validate(trans1);
         
         org_no = trans1.getValue("GETORG/DATA/ORG");
         
         cmd2 = trans2.addCustomFunction("GETORGNAME", 
               "GENERAL_ORGANIZATION_API.GET_ORG_DESC", "ORG_NAME");
         cmd2.addParameter("ORG",org_no);
         
         trans2 = mgr.validate(trans2);
         
         org_name = trans2.getValue("GETORGNAME/DATA/ORG_NAME");
         

         txt = ((mgr.isEmpty(contractEntrustName)) ? "" : contractEntrustName )+ "^" 
               + ((mgr.isEmpty(org_no)) ? "" : org_no )+ "^" 
               + ((mgr.isEmpty(org_name)) ? "" : org_name )+ "^";
         
         mgr.responseWrite(txt);
      }
      else if ("ENG_DEPT_QTY".equals(val)) {         
         tempValue = ((mgr.readValue("ENG_DEPT_QTY")==null)?0:Float.parseFloat(mgr.readValue("ENG_DEPT_QTY")))*((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")));
         mgr.responseWrite(String.valueOf(tempValue)+"^");
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
              setLabel("CONENTRUSTQUANTITIESVISAPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("CONENTRUSTQUANTITIESVISAGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setLabel("CONQUANTITIESVISAID: Id").
              setSize(200).
              setHidden();
      headblk.addField("SERIAL_NO").
              setInsertable().
              setDynamicLOV("BID_PROJ_ENTRUST","PROJ_NO").
              setLOVProperty("WHERE", " IS_VALID = 'TRUE' AND ENTRUST_TYPE = 'TRUE' AND STATUS = '2' AND IS_FEEDBACK = 'FALSE' ").
              setLabel("CONQUANTITIESVISAENTRUSTNO: Entrust No").
              setSize(30).
              setCustomValidation("PROJ_NO,SERIAL_NO", "NAME,ORG,ORG_NAME");
      headblk.addField("NAME").
              setFunction("BID_PROJ_ENTRUST_API.Get_Entrust_Name (:PROJ_NO,:SERIAL_NO)").
              setLabel("CONENTRUSTQUANTITIESVISACONTRACTENTRUSTNAME: Entrust Name").
              setSize(30).
              setReadOnly();
      headblk.addField("VISA_NO").
              setInsertable().
              setWfProperties().
              setLabel("CONQUANTITIESVISAVISANO: Visa No").
              setSize(30);
      headblk.addField("VISA_NAME").
              setInsertable().
              setWfProperties().
              setLabel("CONQUANTITIESVISAVISANAME: Visa Name").
              setSize(30);
      headblk.addField("CONTRACT_ENTRUST_NO").
              setInsertable().
              setLabel("CONQUANTITIESVISACONTRACTENTRUSTNO: Contract Entrust No").
              setSize(30).
              setHidden();
      headblk.addField("ORG").
            setFunction("BID_PROJ_ENTRUST_API.Get_Recommend_Org ( :PROJ_NO,:SERIAL_NO)").
            setLabel("CONENTRUSTQUANTITIESVISACONSTRUCTIONORG: Construction Org").
            setSize(30).
            setHidden();
      
      headblk.addField("ORG_NAME").
              setFunction("GENERAL_ORGANIZATION_API.GET_ORG_DESC ( BID_PROJ_ENTRUST_API.Get_Entrust_Org ( :PROJ_NO,:SERIAL_NO))").
              setLabel("CONENTRUSTQUANTITIESVISACONSTRUCTIONORGNAME: Construction Org Name").
              setSize(30).
              setReadOnly();
      
      headblk.addField("SUB_PROJ_NO").
              setInsertable().
              setDynamicLOV("CON_QUA_TREE","PROJ_NO").
              setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
              setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").
              setLOVProperty("ORDER_BY", "NODE_NO").
              setLabel("CONENTRUSTQUANTITIESVISASUBPROJNO: Sub Proj No").
              setSize(30);
      headblk.addField("SUB_PROJ_DESC").
              setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:SUB_PROJ_NO)").
              setLabel("CONENTRUSTQUANTITIESVISASUBPROJDESC: SUB PROJ DESC").
              setSize(30).
              setReadOnly();
      mgr.getASPField("SUB_PROJ_NO").setValidation("SUB_PROJ_DESC");
      headblk.addField("REAL_ESTIMATE_COST","Money","#0.00").
              setReadOnly().
              setLabel("CONQUANTITIESVISAREALESTIMATECOST: Real Estimate Cost").
              setSize(30);
      headblk.addField("ESTIMATE_COST","Money","#0.00").
              setReadOnly().
              setLabel("CONQUANTITIESVISAESTIMATECOST: Estimate Cost").
              setSize(30);
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setLabel("CONENTRUSTQUANTITIESVISACREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
            setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
            setLabel("CONENTRUSTQUANTITIESVISACREATEPERSONNAME: Create Person").
            setSize(30).
            setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("CONQUANTITIESVISACREATETIME: Create Time").
              setSize(30);
      headblk.addField("STATUS").
              setHidden().
              setLabel("CONENTRUSTQUANTITIESVISASTATUS: Status").
              setSize(20);
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("CONENTRUSTQUANTITIESVISASTATUSDESC: Status Desc").
              setSize(30);
      headblk.addField("VISA_REASON").
              setInsertable().
              setLabel("CONQUANTITIESVISAVISAREASON: Visa Reason").
              setSize(120).
              setHeight(5);
      //Virtual Field
      headblk.addField("CONTRACT_ID").setFunction("''").setHidden();
      //Entrust_No is used for 'IS_VALID'!
      headblk.addField("ENTRUST_NO").setHidden();
      
      headblk.setView("CON_QUANTITIES_VISA");
      headblk.defineCommand("CON_QUANTITIES_VISA_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("printReport", "CONENTRUSTQUANTITIESVISAREPORT: Print Entrust Quantitie Visa Report...");
      headbar.addCustomCommand("setFeedback", "CONENTRUSTQUANTITIESVISASETFEEDBACK: Set Feedback");
      headbar.addCommandValidConditions("setFeedback", "ENTRUST_NO", "Disable", "FALSE");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONQUANTITIESVISATBLHEAD: Con Quantities Visas");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("NAME");
      headlay.setSimple("SUB_PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setDataSpan("STATUS_DESC", 5);
      headlay.setDataSpan("VISA_REASON", 5);


      con_quantities_visa_line_blk = mgr.newASPBlock("ITEM1");
      con_quantities_visa_line_blk.addField("ITEM0_OBJID").
                                   setHidden().
                                   setDbName("OBJID");
      con_quantities_visa_line_blk.addField("ITEM0_OBJVERSION").
                                   setHidden().
                                   setDbName("OBJVERSION");
      con_quantities_visa_line_blk.addField("ITEM0_PROJ_NO").
                                   setDbName("PROJ_NO").
                                   setMandatory().
                                   setHidden().
                                   setLabel("CONQUANTITIESVISALINEITEM0PROJNO: Proj No").
                                   setSize(50);
      con_quantities_visa_line_blk.addField("ITEM0_ID").
                                   setDbName("ID").
                                   setMandatory().
                                   setInsertable().
                                   setLabel("CONQUANTITIESVISALINEITEM0ID: Id").
                                   setSize(50).
                                   setHidden();
      con_quantities_visa_line_blk.addField("LINE_NO").
                                   setInsertable().
                                   setLabel("CONQUANTITIESVISALINELINENO: Line No").
                                   setSize(50).
                                   setHidden();
      con_quantities_visa_line_blk.addField("JOB_LIST_CONTENT").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINEJOBLISTCONTENT: Job List Content").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("UNIT").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINEUNIT: Unit").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("PRICE","Money","#0.00").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINEPRICE: Price").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("QUANTITIES","Number","#0.00").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINEQUANTITIES: Quantities").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("CON_ORG_QTY","Number","#0.00").
                                   setInsertable().
                                   setLabel("CONQUANTITIESVISALINECONORGQTY: Con Org Qty").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("SUPERVISOR_QTY","Number","#0.00").
                                   setInsertable().
                                   setLabel("CONQUANTITIESVISALINESUPERVISORQTY: Supervisor Qty").
                                     setSize(30);
      con_quantities_visa_line_blk.addField("ENG_DEPT_QTY","Number","#0.00").
                                   setInsertable().
                                   setLabel("CONQUANTITIESVISALINEENGDEPTQTY: Eng Dept Qty").
                                   setSize(30).
                                   setCustomValidation("ENG_DEPT_QTY,PRICE", "COST_ESTIMATE");
      con_quantities_visa_line_blk.addField("COST_ESTIMATE","Money","#0.00").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINECOSTESTIMATE: Cost Estimate").
                                   setSize(30);
     
      con_quantities_visa_line_blk.addField("BUDGET_NO").
                                   setReadOnly().
                                   setLabel("CONQUANTITIESVISALINEBUDGETNO: Budget No").
                                   setSize(30);
      con_quantities_visa_line_blk.addField("BUDGET_NAME").
                                   setReadOnly().
                                   setFunction("PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME( :PROJ_NO, :BUDGET_NO)").
                                   setLabel("CONQUANTITIESVISALINEBUDGETNAME: Budget Name").
                                   setSize(30);
      mgr.getASPField("BUDGET_NO").setValidation("BUDGET_NAME");
      con_quantities_visa_line_blk.setView("CON_QUANTITIES_VISA_LINE");
      con_quantities_visa_line_blk.defineCommand("CON_QUANTITIES_VISA_LINE_API","New__,Modify__,Remove__");
      con_quantities_visa_line_blk.setMasterBlock(headblk);
      con_quantities_visa_line_set = con_quantities_visa_line_blk.getASPRowSet();
      con_quantities_visa_line_bar = mgr.newASPCommandBar(con_quantities_visa_line_blk);
      con_quantities_visa_line_bar.defineCommand(con_quantities_visa_line_bar.OKFIND, "okFindITEM1");
      con_quantities_visa_line_bar.defineCommand(con_quantities_visa_line_bar.NEWROW, "newRowITEM1");
      con_quantities_visa_line_tbl = mgr.newASPTable(con_quantities_visa_line_blk);
      con_quantities_visa_line_tbl.setTitle("CONQUANTITIESVISALINEITEMHEAD1: ConQuantitiesVisaLine");
      con_quantities_visa_line_tbl.enableRowSelect();
      con_quantities_visa_line_tbl.setWrap();
      con_quantities_visa_line_lay = con_quantities_visa_line_blk.getASPBlockLayout();
      con_quantities_visa_line_lay.setDefaultLayoutMode(con_quantities_visa_line_lay.MULTIROW_LAYOUT);
      con_quantities_visa_line_lay.setSimple("BUDGET_NAME");
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
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RptConEntrustQuantitiesVisa.raq&proj_no="+proj_no+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }

   public void setFeedback(){
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      cmd = trans.addCustomCommand("PROJ_NO,ID,SERIAL_NO","CON_QUANTITIES_VISA_API.SET_FEEDBACK");
      cmd.setParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ID", headset.getValue("ID"));
      cmd.setParameter("SERIAL_NO", headset.getValue("SERIAL_NO"));
      trans = mgr.perform(trans);
      cmd.clear();
      okFind();
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
      if(headset.countRows() > 0 && headlay.isSingleLayout()){
         con_quantities_visa_line_bar.disableCommand(headbar.DELETE);
         con_quantities_visa_line_bar.disableCommand(headbar.NEWROW);
      }
      if(headset.countRows() > 0 && headlay.isMultirowLayout()){
         headbar.disableCommand("setFeedback");
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONENTRUSTQUANTITIESVISADESC: Entrust Quantities Visa";
   }


   protected String getTitle()
   {
      return "CONENTRUSTQUANTITIESVISATITLE: Entrust Quantities Visa";
   }


   protected void printContents() throws FndException
   {
      super.printContents();
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
         appendToHTML(con_quantities_visa_line_lay.show());
      }
      
//      if (con_quantities_visa_line_lay.isVisible())
//          appendToHTML(con_quantities_visa_line_lay.show());

   }
   
 //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;      
   }
}