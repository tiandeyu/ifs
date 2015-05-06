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

import java.io.UnsupportedEncodingException;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class BidMatPurch extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.bidmaw.BidMatPurch");

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

   private ASPBlock bid_mat_purch_line_blk;
   private ASPRowSet bid_mat_purch_line_set;
   private ASPCommandBar bid_mat_purch_line_bar;
   private ASPTable bid_mat_purch_line_tbl;
   private ASPBlockLayout bid_mat_purch_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  BidMatPurch (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PURCH_ID")) )
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
         mgr.showAlert("BIDMATPURCHNODATA: No data found.");
         headset.clear();
      }else{
         okFindITEM1();
      }
      eval( bid_mat_purch_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","BID_MAT_PURCH_API.New__",headblk);
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

      q = trans.addQuery(bid_mat_purch_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND PURCH_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("PURCH_ID", headset.getValue("PURCH_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,bid_mat_purch_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","BID_MAT_PURCH_LINE_API.New__",bid_mat_purch_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_PURCH_ID", headset.getValue("PURCH_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      bid_mat_purch_line_set.addRow(data);
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
              setLabel("BIDMATPURCHPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("BIDMATPURCHGENERALPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("PURCH_ID").
              setHidden().
              setInsertable().
              setLabel("BIDMATPURCHPURCHID: Purch Id").
              setSize(30);
      headblk.addField("PURCH_NAME").
              setInsertable().
              setMandatory().
              setWfProperties().
              setLabel("BIDMATPURCHPURCHNAME: Purch Name").
              setSize(30);
      headblk.addField("PURCH_PERSON").
              setInsertable().
              setDynamicLOV("GENERAL_ORG_PER_POS_LOV","PROJ_NO").
              setLOVProperty("WHERE", " POS_NO = 'MATZG' ").
              setLabel("BIDMATPURCHPURCHPERSON: Purch Person").
              setSize(20);
      headblk.addField("PURCH_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.GET_NAME (:PURCH_PERSON)").
              setLabel("BIDMATPURCHPURCHPERSONNAME: Purch Person Name").
              setSize(30);
      mgr.getASPField("PURCH_PERSON").setValidation("PURCH_PERSON_NAME");
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("BIDMATPURCHCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setLabel("BIDMATPURCHCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("BIDMATPURCHCREATETIME: Create Time").
              setSize(30);
      headblk.addField("STATUS").
              setHidden().
              setLabel("BIDMATPURCHSTATUS: Status").
              setSize(20);
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("BIDMATPURCHSTATUSDESC: Status Desc").
              setSize(30);
      mgr.getASPField("STATUS").setValidation("STATUS_DESC");
      headblk.addField("PURCH_REASON").
              setInsertable().
              setHeight(3).
              setLabel("BIDMATPURCHPURCHREASON: Purch Reason").
              setSize(120);
      headblk.setView("BID_MAT_PURCH");
      headblk.defineCommand("BID_MAT_PURCH_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("createFile", "BIDMATPURCHBATCHADD: Batch Add");
      headbar.addCommandValidConditions("createFile", "STATUS", "Disable", "2");
      headbar.addCustomCommand("printReport", "BIDMATPURCHPRINTREPORT: Print Report...");
      headbar.addCustomCommand("printReport2", "BIDMATPURCHPRINTREPORT2: Print Report2...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("BIDMATPURCHTBLHEAD: Bid Mat Purchs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("PURCH_PERSON_NAME");
 


      bid_mat_purch_line_blk = mgr.newASPBlock("ITEM1");
      bid_mat_purch_line_blk.addField("ITEM0_OBJID").
                             setHidden().
                             setDbName("OBJID");
      bid_mat_purch_line_blk.addField("ITEM0_OBJVERSION").
                             setHidden().
                             setDbName("OBJVERSION");
      bid_mat_purch_line_blk.addField("ITEM0_PROJ_NO").
                             setDbName("PROJ_NO").
                             setMandatory().
                             setHidden().
                             setInsertable().
                             setLabel("BIDMATPURCHLINEITEM0PROJNO: Proj No").
                             setSize(30);
      bid_mat_purch_line_blk.addField("ITEM0_PURCH_ID").
                             setDbName("PURCH_ID").
                             setMandatory().
                             setHidden().
                             setInsertable().
                             setLabel("BIDMATPURCHLINEITEM0PURCHID: Purch Id").
                             setSize(30);
      bid_mat_purch_line_blk.addField("PURCH_LINE_NO").
                             setHidden().
                             setInsertable().
                             setLabel("BIDMATPURCHLINEPURCHLINENO: Purch Line No").
                             setSize(30);
      //The following fields's source are BidMatReqLine which IS_PURCHED='TRUE'.
      bid_mat_purch_line_blk.addField("REQ_ID").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEREQID: Req Id").
                             setSize(30);
      bid_mat_purch_line_blk.addField("REQ_NAME").
                             setReadOnly().
                             setFunction("BID_MAT_REQ_API.GET_REQ_NAME ( :PROJ_NO, :REQ_ID)").
                             setLabel("BIDMATPURCHLINEREQNAME: Req Name").
                             setSize(30);
      mgr.getASPField("REQ_ID").setValidation("REQ_NAME");
      bid_mat_purch_line_blk.addField("REQ_LINE_NO").
                             setReadOnly().
//                             setDynamicLOV("BID_MAT_REQ_LINE","PROJ_NO,REQ_ID").
//                             setLOVProperty("WHERE", " IS_PURCHED = 'TRUE' ").
                             setLabel("BIDMATPURCHLINEREQLINENO: Req Line No").
                             setSize(30);
      bid_mat_purch_line_blk.addField("MAT_NAME").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEMATNAME: Mat Name").
                             setSize(30);
      bid_mat_purch_line_blk.addField("SPECIFICATIONS").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINESPECIFICATIONS: Specifications").
                             setSize(30);
      bid_mat_purch_line_blk.addField("REQ_QTY","Number").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEREQQTY: Req Qty").
                             setSize(30);
      bid_mat_purch_line_blk.addField("UNIT").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEUNIT: Unit").
                             setSize(30);
      bid_mat_purch_line_blk.addField("BUDGET_PRICE","Number").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEBUDGETPRICE: Budget Price").
                             setSize(30);
      bid_mat_purch_line_blk.addField("BUDGET_TOTAL_PRICE","Number").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEBUDGETTOTALPRICE: Budget Total Price").
                             setSize(30);
      bid_mat_purch_line_blk.addField("USED_PART").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEUSEDPART: Used Part").
                             setSize(30);
      bid_mat_purch_line_blk.addField("ACCEPT_TIME","Date").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEACCEPTTIME: Accept Time").
                             setSize(30);
      bid_mat_purch_line_blk.addField("BUDGET_LINE_NO").
                             setReadOnly().
                             setLabel("BIDMATPURCHLINEBUDGETLINENO: Budget Line No").
                             setSize(30);
      bid_mat_purch_line_blk.addField("BUDGET_NAME").
                             setReadOnly().
                             setFunction("PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME ( :PROJ_NO, :BUDGET_LINE_NO)").
                             setLabel("BIDMATPURCHLINEBUDGETNAME: Budget Name").
                             setSize(30);
      mgr.getASPField("BUDGET_LINE_NO").setValidation("BUDGET_NAME");
      bid_mat_purch_line_blk.addField("NOTE").
                             setReadOnly().
                             setHeight(3).
                             setLabel("BIDMATPURCHLINENOTE: Note").
                             setSize(120);
      //Own field
      bid_mat_purch_line_blk.addField("PURCH_QTY","Number").
                             setInsertable().
                             setLabel("BIDMATPURCHLINEPURCHQTY: Purch Qty").
                             setSize(30);
      bid_mat_purch_line_blk.setView("BID_MAT_PURCH_LINE");
      bid_mat_purch_line_blk.defineCommand("BID_MAT_PURCH_LINE_API","New__,Modify__,Remove__");
      bid_mat_purch_line_blk.setMasterBlock(headblk);
      bid_mat_purch_line_set = bid_mat_purch_line_blk.getASPRowSet();
      bid_mat_purch_line_bar = mgr.newASPCommandBar(bid_mat_purch_line_blk);
      bid_mat_purch_line_bar.defineCommand(bid_mat_purch_line_bar.OKFIND, "okFindITEM1");
      bid_mat_purch_line_bar.defineCommand(bid_mat_purch_line_bar.NEWROW, "newRowITEM1");
      bid_mat_purch_line_tbl = mgr.newASPTable(bid_mat_purch_line_blk);
      bid_mat_purch_line_tbl.setTitle("BIDMATPURCHLINEITEMHEAD1: BidMatPurchLine");
      bid_mat_purch_line_tbl.enableRowSelect();
      bid_mat_purch_line_tbl.setWrap();
      bid_mat_purch_line_lay = bid_mat_purch_line_blk.getASPBlockLayout();
      bid_mat_purch_line_lay.setDefaultLayoutMode(bid_mat_purch_line_lay.MULTIROW_LAYOUT);
      bid_mat_purch_line_lay.setDataSpan("BUDGET_LINE_NO", 5);
      bid_mat_purch_line_lay.setDataSpan("NOTE", 5);
      bid_mat_purch_line_lay.setSimple("BUDGET_NAME");
      bid_mat_purch_line_lay.setSimple("REQ_NAME");



   }
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
      ASPManager mgr = getASPManager();
      String serialized_data = mgr.pack(buff);
      String url = transfer_page + "?" + "__TRANSFER=" + serialized_data;
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }
   public void createFile() throws FndException
   {
      // store selections
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      headset.store();
      
      ASPBuffer selected_fields = headset.getSelectedRows("PROJ_NO,PURCH_ID");

      callNewWindow("BidMatPurchLineDlg.page", selected_fields);
   }


   public void  adjust() throws FndException
   {
      // fill function body
         super.adjust();
   }

   public void  printReport() throws FndException, UnsupportedEncodingException
  {
   ASPManager mgr = getASPManager();
   ASPConfig cfg = getASPConfig();
   String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
   if (headlay.isMultirowLayout())
      headset.goTo(headset.getRowSelected());
   if (headset.countRows()>0 )
         {   
            String proj_no = headset.getValue("PROJ_NO");
            String accept_id = headset.getValue("PURCH_ID");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=BidMatPurch.raq&proj_no="+proj_no+"&id="+accept_id
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }   

   public void  printReport2() throws FndException, UnsupportedEncodingException
  {
   ASPManager mgr = getASPManager();
   ASPConfig cfg = getASPConfig();
   String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
   if (headlay.isMultirowLayout())
      headset.goTo(headset.getRowSelected());
   if (headset.countRows()>0 )
         {   
            String proj_no = headset.getValue("PROJ_NO");
            String accept_id = headset.getValue("PURCH_ID");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=BidMatPurch2.raq&proj_no="+proj_no+"&id="+accept_id
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   protected String getDescription()
   {
      return "BIDMATPURCHDESC: Bid Mat Purch";
   }


   protected String getTitle()
   {
      return "BIDMATPURCHTITLE: Bid Mat Purch";
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
      if (bid_mat_purch_line_lay.isVisible())
          appendToHTML(bid_mat_purch_line_lay.show());

   }

   protected ASPBlock getBizWfBlock() {
      return headblk;
   }
}