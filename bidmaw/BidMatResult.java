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

package ifs.bidmaw;
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

public class BidMatResult extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.bidmaw.BidMatResult");

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

   private ASPBlock bid_mat_result_line_blk;
   private ASPRowSet bid_mat_result_line_set;
   private ASPCommandBar bid_mat_result_line_bar;
   private ASPTable bid_mat_result_line_tbl;
   private ASPBlockLayout bid_mat_result_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  BidMatResult (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("RESULT_ID")) )
         okFind();
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
         mgr.showAlert("BIDMATRESULTNODATA: No data found.");
         headset.clear();
      }
      eval( bid_mat_result_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","BID_MAT_RESULT_API.New__",headblk);
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
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(bid_mat_result_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND RESULT_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("RESULT_ID", headset.getValue("RESULT_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,bid_mat_result_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","BID_MAT_RESULT_LINE_API.New__",bid_mat_result_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_RESULT_ID", headset.getValue("RESULT_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      bid_mat_result_line_set.addRow(data);
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
              setLabel("BIDMATRESULTPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("BIDMATRESULTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("RESULT_ID").
              setHidden().
              setInsertable().
              setLabel("BIDMATRESULTRESULTID: Result Id").
              setSize(30);
      headblk.addField("SUPPLIER").
              setInsertable().
              setDynamicLOV("SUPPLIER_INFO").
              setLabel("BIDMATRESULTSUPPLIER: Supplier").
              setSize(30);
      headblk.addField("SUPPLIER_NAME").
              setSize(20).
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.GET_NAME(:SUPPLIER)").    
              setLabel("BIDMATRESULTSUPPLIERNAME: Supplier Name");
      mgr.getASPField("SUPPLIER").setValidation("SUPPLIER_NAME");
      headblk.addField("IS_VIRTUAL_CONTRACT").
              setInsertable().
              setCheckBox("FALSE,TRUE").
              setLabel("BIDMATRESULTISVIRTUALCONTRACT: Is Virtual Contract").
              setSize(20);
      headblk.addField("PURCH_CONTRACT_ID").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT").
              setLOVProperty("WHERE", " CLASS_NO= 'SB' ").
              setLOVProperty("OR", " CLASS_NO= 'WX' ").
              setLOVProperty("OR", " CLASS_NO= 'WZ' ").
              setLabel("BIDMATRESULTPURCHCONTRACTID: Purch Contract Id").
              setSize(30);
      headblk.addField("PURCH_CONTRACT_NAME").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC (:PROJ_NO, :PURCH_CONTRACT_ID)").
              setLabel("BIDMATRESULTPURCHCONTRACTNAME: Purch Contract Name").
              setSize(30);
      mgr.getASPField("PURCH_CONTRACT_ID").setValidation("PURCH_CONTRACT_NAME");
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("BIDMATRESULTCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setLabel("BIDMATRESULTCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("BIDMATRESULTCREATETIME: Create Time").
              setSize(30);
      headblk.addField("PURCH_ID").
              setInsertable().
              setDynamicLOV("BID_MAT_PURCH","PROJ_NO").
              setLOVProperty("WHERE", " CREATE_RESULT = 'FALSE' AND STATUS = '2' ").
              setLabel("BIDMATRESULTPURCHID: Purch Id").
              setSize(30);
      headblk.addField("BID_MAT_PURCH_PURCH_NAME").
              setReadOnly().
              setFunction("BID_MAT_PURCH_API.GET_PURCH_NAME ( :PROJ_NO,:PURCH_ID)").
              setLabel("BIDMATRESULTBIDMATPURCHPURCHNAME: Bid Mat Purch Purch Name").
              setSize(30);
      mgr.getASPField("PURCH_ID").setValidation("BID_MAT_PURCH_PURCH_NAME");
      headblk.addField("GENERATED_CONTRACT").setHidden().setSize(30);
      headblk.setView("BID_MAT_RESULT");
      headblk.defineCommand("BID_MAT_RESULT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("generateContract", "BIDMATRESULTGENERATEDCONTRACT: Generate Contract");
      headbar.addCommandValidConditions("generateContract", "GENERATED_CONTRACT", "Disable", "TRUE");
      headbar.addCommandValidConditions("generateContract", "IS_VIRTUAL_CONTRACT", "Disable", "FALSE");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("BIDMATRESULTTBLHEAD: Bid Mat Results");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("BID_MAT_PURCH_PURCH_NAME");
      headlay.setSimple("SUPPLIER_NAME");
      headlay.setSimple("PURCH_CONTRACT_NAME");
 


      bid_mat_result_line_blk = mgr.newASPBlock("ITEM1");
      bid_mat_result_line_blk.addField("ITEM0_OBJID").
                              setHidden().
                              setDbName("OBJID");
      bid_mat_result_line_blk.addField("ITEM0_OBJVERSION").
                              setHidden().
                              setDbName("OBJVERSION");
      bid_mat_result_line_blk.addField("ITEM0_PROJ_NO").
                              setDbName("PROJ_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("BIDMATRESULTLINEITEM0PROJNO: Proj No").
                              setSize(30);
      bid_mat_result_line_blk.addField("ITEM0_RESULT_ID").
                              setDbName("RESULT_ID").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("BIDMATRESULTLINEITEM0RESULTID: Result Id").
                              setSize(30);
      bid_mat_result_line_blk.addField("RESULT_LINE_NO").
                              setHidden().
                              setInsertable().
                              setLabel("BIDMATRESULTLINERESULTLINENO: Result Line No").
                              setSize(30);
      //The following fields are auto inserting into line-table from Bid_Mat_Purch_Line which had the same Purch_Id in this table.
      bid_mat_result_line_blk.addField("REQ_ID").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEREQID: Req Id").
                              setSize(30);
      bid_mat_result_line_blk.addField("REQ_NAME").
                              setReadOnly().
                              setFunction("BID_MAT_REQ_API.GET_REQ_NAME ( :PROJ_NO, :REQ_ID)").
                              setLabel("BIDMATRESULTLINEREQNAME: Req Name").
                              setSize(30);
      mgr.getASPField("REQ_ID").setValidation("REQ_NAME");
      bid_mat_result_line_blk.addField("REQ_LINE_NO").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEREQLINENO: Req Line No").
                              setSize(30);
      bid_mat_result_line_blk.addField("PURCH_LINE_NO").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEPURCHLINENO: Purch Line No").
                              setSize(30);
      bid_mat_result_line_blk.addField("MAT_NAME").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEMATNAME: Mat Name").
                              setSize(30);
      bid_mat_result_line_blk.addField("SPECIFICATIONS").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINESPECIFICATIONS: Specifications").
                              setSize(30);
      bid_mat_result_line_blk.addField("REQ_QTY","Number").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEREQQTY: Req Qty").
                              setSize(30);
      bid_mat_result_line_blk.addField("PURCH_QTY","Number").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEPURCHQTY: Purch Qty").
                              setSize(30);
      bid_mat_result_line_blk.addField("UNIT").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEUNIT: Unit").
                              setSize(30);
      bid_mat_result_line_blk.addField("BUDGET_PRICE","Number").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEBUDGETPRICE: Budget Price").
                              setSize(30);
      bid_mat_result_line_blk.addField("BUDGET_TOTAL_PRICE","Number").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEBUDGETTOTALPRICE: Budget Total Price").
                              setSize(30);
      bid_mat_result_line_blk.addField("USED_PART").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEUSEDPART: Used Part").
                              setSize(30);
      bid_mat_result_line_blk.addField("ACCEPT_TIME","Date").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEACCEPTTIME: Accept Time").
                              setSize(30);
      bid_mat_result_line_blk.addField("BUDGET_LINE_NO").
                              setReadOnly().
                              setLabel("BIDMATRESULTLINEBUDGETLINENO: Budget Line No").
                              setSize(30);
      bid_mat_result_line_blk.addField("BUDGET_NAME").
                              setReadOnly().
                              setFunction("PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME ( :PROJ_NO, :BUDGET_LINE_NO)").
                              setLabel("BIDMATRESULTLINEBUDGETNAME: Budget Name").
                              setSize(30);
      mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_NAME");
      bid_mat_result_line_blk.addField("NOTE").
                              setReadOnly().
                              setHeight(3).
                              setLabel("BIDMATRESULTLINENOTE: Note").
                              setSize(120);
      //Own fields
      bid_mat_result_line_blk.addField("PURCH_PRICE","Number").
                              setInsertable().
                              setLabel("BIDMATRESULTLINEPURCHPRICE: Purch Price").
                              setSize(30);
      bid_mat_result_line_blk.addField("PURCH_TOTAL_PRICE","Number").
                              setInsertable().
                              setLabel("BIDMATRESULTLINEPURCHTOTALPRICE: Purch Total Price").
                              setSize(30);
      bid_mat_result_line_blk.addField("MANUFACTURER").
                              setInsertable().
                              setLabel("BIDMATRESULTLINEMANUFACTURER: Manufacturer").
                              setSize(30);
      bid_mat_result_line_blk.addField("QUOTATION_NO").
                              setInsertable().
                              setLabel("BIDMATRESULTLINEQUOTATIONNO: Quotation No").
                              setSize(30);
      bid_mat_result_line_blk.setView("BID_MAT_RESULT_LINE");
      bid_mat_result_line_blk.defineCommand("BID_MAT_RESULT_LINE_API","Modify__");
      bid_mat_result_line_blk.setMasterBlock(headblk);
      bid_mat_result_line_set = bid_mat_result_line_blk.getASPRowSet();
      bid_mat_result_line_bar = mgr.newASPCommandBar(bid_mat_result_line_blk);
      bid_mat_result_line_bar.defineCommand(bid_mat_result_line_bar.OKFIND, "okFindITEM1");
//      bid_mat_result_line_bar.defineCommand(bid_mat_result_line_bar.NEWROW, "newRowITEM1");
      bid_mat_result_line_tbl = mgr.newASPTable(bid_mat_result_line_blk);
      bid_mat_result_line_tbl.setTitle("BIDMATRESULTLINEITEMHEAD1: BidMatResultLine");
      bid_mat_result_line_tbl.enableRowSelect();
      bid_mat_result_line_tbl.setWrap();
      bid_mat_result_line_lay = bid_mat_result_line_blk.getASPBlockLayout();
      bid_mat_result_line_lay.setDefaultLayoutMode(bid_mat_result_line_lay.MULTIROW_LAYOUT);
      bid_mat_result_line_lay.setSimple("BUDGET_NAME");
      bid_mat_result_line_lay.setDataSpan("BUDGET_LINE_NO", 5);
      bid_mat_result_line_lay.setDataSpan("NOTE", 5);


   }
   
   public void  adjust()
   {
      // fill function body
      if(headset.countRows() > 0 && headlay.isSingleLayout() && "TRUE".equals(headset.getValue("GENERATED_CONTRACT"))){
         headbar.disableCommand(headbar.DELETE);
         bid_mat_result_line_bar.disableCommand(headbar.EDITROW);
      }
      if(headset.countRows() > 0 && headlay.isMultirowLayout()){
         headbar.disableCommand("generateContract");
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   public void generateContract(){
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String is_virtual_contract = headset.getValue("IS_VIRTUAL_CONTRACT");
      if("TRUE".equals(is_virtual_contract)){
         cmd = trans.addCustomCommand("PROJ_NO,RESULT_ID","BID_MAT_RESULT_API.Generate_Contract");
         cmd.setParameter("PROJ_NO", headset.getValue("PROJ_NO"));
         cmd.setParameter("RESULT_ID", headset.getValue("RESULT_ID"));
         trans = mgr.perform(trans);
         cmd.clear();
         okFind();
      }
   }
   
   protected String getDescription()
   {
      return "BIDMATRESULTDESC: Bid Mat Result";
   }


   protected String getTitle()
   {
      return "BIDMATRESULTTITLE: Bid Mat Result";
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
      if (bid_mat_result_line_lay.isVisible())
          appendToHTML(bid_mat_result_line_lay.show());

   }
}
