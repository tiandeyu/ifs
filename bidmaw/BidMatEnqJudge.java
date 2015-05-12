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

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class BidMatEnqJudge extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.bidmaw.BidMatEnqJudge");

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

   private ASPBlock bid_judge_mat_list_blk;
   private ASPRowSet bid_judge_mat_list_set;
   private ASPCommandBar bid_judge_mat_list_bar;
   private ASPTable bid_judge_mat_list_tbl;
   private ASPBlockLayout bid_judge_mat_list_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  BidMatEnqJudge (ASPManager mgr, String page_path)
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
         mgr.showAlert("BIDMATENQJUDGENODATA: No data found.");
         headset.clear();
      }
      eval( bid_judge_mat_list_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","BID_MAT_ENQ_JUDGE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(bid_judge_mat_list_blk);
      q.addWhereCondition("PROJ_NO = ? AND CRE_ID = ? AND BID_MAT_ID = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("CRE_ID", headset.getValue("CRE_ID"));
      q.addParameter("BID_MAT_ID", headset.getValue("BID_MAT_ID"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,bid_judge_mat_list_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM2","BID_JUDGE_MAT_LIST_API.New__",bid_judge_mat_list_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM1_CRE_ID", headset.getValue("CRE_ID"));
      cmd.setParameter("ITEM1_BID_MAT_ID", headset.getValue("BID_MAT_ID"));
      cmd.setParameter("ITEM1_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      bid_judge_mat_list_set.addRow(data);
   }
   
   public void validate()
   {
       ASPManager mgr = getASPManager();
       String val = mgr.readValue("VALIDATE");  
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String txt = "";
       String matName = "";
       String matPrice = "";
       String matCount = "";
       String matTotalPrice = "";
       String matProdModle = "";
       String createBidName="";
       String budgetPrice="";
       float tempValue=0;
       if("MAT_LINE_NO".equals(val)){          
          cmd = trans.addCustomFunction("GET_MAT_NAME", "BID_ENQ_MAT_LIST_API.GET_MAT_NAME", "ITEM1_MAT_NAME");
          cmd.addParameter("ITEM1_PROJ_NO, ITEM1_CRE_ID, DOC_ID, MAT_LINE_NO");
          
          cmd = trans.addCustomFunction("GET_PRICE", "BID_ENQ_MAT_LIST_API.GET_PRICE", "MAT_PRICE");
          cmd.addParameter("ITEM1_PROJ_NO, ITEM1_CRE_ID, DOC_ID, MAT_LINE_NO");
          
          cmd = trans.addCustomFunction("GET_TOTAL_PRICE", "BID_ENQ_MAT_LIST_API.GET_TOTAL_PRICE", "MAT_TOTAL_PRICE");
          cmd.addParameter("ITEM1_PROJ_NO, ITEM1_CRE_ID, DOC_ID, MAT_LINE_NO");
          
          cmd = trans.addCustomFunction("GET_PROD_MODLE", "BID_ENQ_MAT_LIST_API.GET_PROD_MODLE", "MAT_PROD_MODLE");
          cmd.addParameter("ITEM1_PROJ_NO, ITEM1_CRE_ID, DOC_ID, MAT_LINE_NO");
          trans = mgr.validate(trans);
                   
          matName = trans.getValue("GET_MAT_NAME/DATA/ITEM1_MAT_NAME");
          matPrice = trans.getValue("GET_PRICE/DATA/MAT_PRICE");
          matTotalPrice = trans.getValue("GET_TOTAL_PRICE/DATA/MAT_TOTAL_PRICE");
          matProdModle = trans.getValue("GET_PROD_MODLE/DATA/MAT_PROD_MODLE");
          
          txt = ((mgr.isEmpty(matName)) ? "" : matName )+ "^";
          txt = txt + ((mgr.isEmpty(matPrice)) ? "" : matPrice )+ "^";
          txt = txt + ((mgr.isEmpty(matTotalPrice)) ? "" : matTotalPrice )+ "^" + ((mgr.isEmpty(matProdModle)) ? "" : matProdModle )+ "^";
          mgr.responseWrite(txt);
       }
       if("CRE_ID".equals(val)){
          cmd = trans.addCustomFunction("GETCREATEBIDNAME", 
                "BID_CRE_PROJ_APPLY_API.GET_CREATE_BID_NAME", "CREATE_BID_NAME");
          cmd.addParameter("PROJ_NO,CRE_ID");
          
          cmd = trans.addCustomFunction("GETFEEBUDGET", 
                "BID_CRE_PROJ_APPLY_API.GET_FEE", "FEE_BUDGET");
          cmd.addParameter("PROJ_NO,CRE_ID");
          trans = mgr.validate(trans);
          
          createBidName = trans.getValue("GETCREATEBIDNAME/DATA/CREATE_BID_NAME");
          budgetPrice = trans.getValue("GETFEEBUDGET/DATA/FEE_BUDGET");
          
          txt = ((mgr.isEmpty(createBidName)) ? "" : createBidName )+"^" + 
          ((mgr.isEmpty(budgetPrice)) ? "" : budgetPrice )+ "^";
    
          mgr.responseWrite(txt);
       }

        else if("PRICE_ONE".equals(val)){
           tempValue = ((mgr.readValue("COUNT")==null)?0:Float.parseFloat(mgr.readValue("COUNT")))*((mgr.readValue("PRICE_ONE")==null)?0:Float.parseFloat(mgr.readValue("PRICE_ONE")));
           mgr.responseWrite(String.valueOf(tempValue)+"^");
        }
        else if("PRICE_TWO".equals(val)){
           tempValue = ((mgr.readValue("COUNT")==null)?0:Float.parseFloat(mgr.readValue("COUNT")))*((mgr.readValue("PRICE_TWO")==null)?0:Float.parseFloat(mgr.readValue("PRICE_TWO")));
           mgr.responseWrite(String.valueOf(tempValue)+"^");
        }else if("PRICE_THREE".equals(val)){
           tempValue = ((mgr.readValue("COUNT")==null)?0:Float.parseFloat(mgr.readValue("COUNT")))*((mgr.readValue("PRICE_THREE")==null)?0:Float.parseFloat(mgr.readValue("PRICE_THREE")));
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
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_PROJECT",600,445).
              setLabel("BIDMATENQJUDGEPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("BIDMATENQJUDGEGENERALPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC"); 
      headblk.addField("CRE_ID").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("BID_CRE_PROJ_APPLY_LOV","PROJ_NO").
              setLOVProperty("WHERE", "STATUS='2' AND BID_PROJ_TYPE_DB = 'MAT'"). 
              setLabel("BIDMATENQJUDGECREID: Cre Id").
              setCustomValidation("PROJ_NO,CRE_ID", "CREATE_BID_NAME,FEE_BUDGET").
              setSize(30);
      headblk.addField("CREATE_BID_NAME").
              setFunction("BID_CRE_PROJ_APPLY_API.Get_Create_Bid_Name ( :PROJ_NO, :CRE_ID)").
              setLabel("BIDMATENQJUDGEGETCREATEBIDNAME: Create Bid Name").
              setReadOnly().
              setSize(30);
      headblk.addField("BID_MAT_ID").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("BID_MATPRE_ENQ_VERIFY", "PROJ_NO,CRE_ID").
              setLOVProperty("WHERE", "STATUS='2'").
              setLabel("BIDMATENQJUDGEBIDMATID: Bid Mat Id").
              setSize(30);
      headblk.addField("ID").
              setHidden().
              setLabel("BIDMATENQJUDGEID: Id").
              setSize(30);
      headblk.addField("FEE_BUDGET","Number").
              setReadOnly().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGEFEEBUDGET: Fee Budget").
              setSize(30);
      headblk.addField("CREATE_DATE","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGECREATEDATE: Create Date").
              setSize(30);
      headblk.addField("CREATE_PERSON").
              setReadOnly().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGECREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGECREATEPERSONNAME: Create Person Name").
              setSize(30).
              setReadOnly();
      headblk.addField("CREATE_ORG").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGECREATEORG: Create Org").
              setSize(30).
              setDynamicLOV("GENERAL_ORGANIZATION");
      headblk.addField("START_ORG_NAME").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:CREATE_ORG)").
              setLabel("CONPROJCONNECTIONLISTFREENOREPLYSTARTORGNAME: Start Org Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CREATE_ORG").setValidation("START_ORG_NAME");
      headblk.addField("BID_DESC").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGEBIDDESC: Bid Desc").
              setHeight(3).
              setSize(129);      
      headblk.addField("STATUS").
              setHidden().
              setLabel("BIDMATENQJUDGESTATUS: Status").
              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDMATENQJUDGENOTE: Note").
              setHeight(5).
              setSize(129);
      
      headblk.setView("BID_MAT_ENQ_JUDGE");
      headblk.defineCommand("BID_MAT_ENQ_JUDGE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("BIDMATENQJUDGETBLHEAD: Bid Mat Enq Judges");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.addCustomCommand("printReport", "BIDMATENQJUDGEPRINTREPORT: Print Report...");
      headlay.setDataSpan("CREATE_ORG", 5);
      headlay.setDataSpan("BID_DESC", 5);
      headlay.setDataSpan("NOTE", 5);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("CREATE_BID_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("START_ORG_NAME");
      /*headbar.addCustomCommand("activateITEM1",mgr.translate("BIDMATENQRESULT: Bid Mat Enq Result"));
      headbar.addCustomCommand("activateITEM2",mgr.translate("BIDJUDGEMATLIST: Bid Judge Mat List"));*/
 
 
      bid_judge_mat_list_blk = mgr.newASPBlock("ITEM2");
      bid_judge_mat_list_blk.addField("ITEM1_OBJID").
                             setHidden().
                             setDbName("OBJID");
      bid_judge_mat_list_blk.addField("ITEM1_OBJVERSION").
                             setHidden().
                             setDbName("OBJVERSION");
      bid_judge_mat_list_blk.addField("ITEM1_PROJ_NO").
                             setDbName("PROJ_NO").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTITEM1PROJNO: Proj No").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_CRE_ID").
                             setDbName("CRE_ID").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTITEM1CREID: Cre Id").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_BID_MAT_ID").
                             setDbName("BID_MAT_ID").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTITEM1BIDMATID: Bid Mat Id").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_ID").
                             setDbName("ID").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTITEM1ID: Id").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_LINE_NO").
                             setDbName("LINE_NO").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTLINENO: Line No").
                             setSize(30);
      bid_judge_mat_list_blk.addField("DOC_ID").
                             setHidden().
                             setLabel("BIDJUDGEMATLISTDOCID: Doc Id").
                             setSize(30);
      bid_judge_mat_list_blk.addField("MAT_LINE_NO").
                             setHidden().
//                             setDynamicLOV("BID_ENQ_MAT_LIST", "ITEM1_PROJ_NO,ITEM1_CRE_ID,DOC_ID").
                             setLabel("BIDJUDGEMATLISTMATLINENO: Mat Line No").
                             setCustomValidation("ITEM1_PROJ_NO, ITEM1_CRE_ID, DOC_ID, MAT_LINE_NO", "ITEM1_MAT_NAME,MAT_PRICE,MAT_TOTAL_PRICE,MAT_PROD_MODLE").
                             setSize(30);
      bid_judge_mat_list_blk.addField("MAT_NO").
//                             setFunction("BID_ENQ_MAT_LIST_API.Get_Mat_No ( :ITEM1_PROJ_NO, :ITEM1_CRE_ID,:DOC_ID,:MAT_LINE_NO)").
                             setLabel("BIDJUDGEMATLISTMATNO: Mat No").
                             setReadOnly().
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_MAT_NAME").
                             setFunction("BID_ENQ_MAT_LIST_API.Get_Mat_Name ( :ITEM1_PROJ_NO, :ITEM1_CRE_ID,:DOC_ID,:MAT_LINE_NO)").
                             setLabel("BIDJUDGEMATLISTMATNAME: Mat Name").
                             setReadOnly().
                             setSize(30);
    
      bid_judge_mat_list_blk.addField("MAT_PRICE").
                              setFunction("BID_ENQ_MAT_LIST_API.Get_Price ( :ITEM1_PROJ_NO, :ITEM1_CRE_ID,:DOC_ID,:MAT_LINE_NO)").
                              setLabel("BIDJUDGEMATLISTMATPRICE: Mat Price").
                              setHidden().
                              setReadOnly().
                              setSize(30);
   
      bid_judge_mat_list_blk.addField("COUNT").
                             setLabel("BIDJUDGEMATLISTMATCOUNT: Mat Count").
                             setInsertable().
                             setSize(30);

      bid_judge_mat_list_blk.addField("MAT_TOTAL_PRICE").
                             setFunction("BID_ENQ_MAT_LIST_API.Get_Total_Price ( :ITEM1_PROJ_NO, :ITEM1_CRE_ID,:DOC_ID,:MAT_LINE_NO)").
                             setLabel("BIDJUDGEMATLISTMATTOTALPRICE: Mat Total Price").
                             setReadOnly().
                             setHidden().
                             setSize(30);

      bid_judge_mat_list_blk.addField("MAT_PROD_MODLE").
                             setFunction("BID_ENQ_MAT_LIST_API.Get_Prod_Modle ( :ITEM1_PROJ_NO, :ITEM1_CRE_ID,:DOC_ID,:MAT_LINE_NO)").
                             setLabel("BIDJUDGEMATLISTMATPRODMODLE: Mat Prod Modle").
                             setReadOnly().
                             setSize(30);    
      
      bid_judge_mat_list_blk.addField("ORG_ONE").
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTORGORG: Org One").
                             setDynamicLOV("BID_MATPRE_ENQ_ORG_INFO", "ITEM1_PROJ_NO,ITEM1_CRE_ID,ITEM1_BID_MAT_ID,MAT_NO").
                             setSize(30);
      bid_judge_mat_list_blk.addField("PRICE_ONE","Number").      
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTPRICEONE: Price One").
                             setCustomValidation("COUNT,PRICE_ONE", "TOTAL_PRICE_ONE").
                             setSize(30);
      bid_judge_mat_list_blk.addField("TOTAL_PRICE_ONE","Number").      
                             setReadOnly().
                             setLabel("BIDJUDGEMATLISTTOTALPRICEONE: Total Price One").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ORG_TWO").
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTORGTWO: Org Two").
                             setDynamicLOV("BID_MATPRE_ENQ_ORG_INFO", "ITEM1_PROJ_NO,ITEM1_CRE_ID,ITEM1_BID_MAT_ID,MAT_NO").   
                             setSize(30);
      bid_judge_mat_list_blk.addField("PRICE_TWO","Number").
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTPRICETWO: Price Two").
                             setCustomValidation("COUNT,PRICE_TWO", "TOTAL_PRICE_TWO").
                             setSize(30);
      bid_judge_mat_list_blk.addField("TOTAL_PRICE_TWO","Number").
                             setReadOnly().
                             setLabel("BIDJUDGEMATLISTTOTALPRICETWO: Total Price Two").
                             setSize(30);
      bid_judge_mat_list_blk.addField("ITEM1_BIDED_ORG").
                             setDbName("BIDED_ORG").
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTBIDEDORG: Bided Org").
                             setDynamicLOV("BID_MATPRE_ENQ_ORG_INFO", "ITEM1_PROJ_NO,ITEM1_CRE_ID,ITEM1_BID_MAT_ID,MAT_NO").
                             setSize(30);
      bid_judge_mat_list_blk.addField("PRICE_THREE","Number").
                             setInsertable().
                             setLabel("BIDJUDGEMATLISTPRICETHREE: Price Three").
                             setCustomValidation("COUNT,PRICE_THREE", "TOTAL_PRICE_THREE").
                             setSize(30);
      bid_judge_mat_list_blk.addField("TOTAL_PRICE_THREE","Number").
                             setReadOnly().
                             setLabel("BIDJUDGEMATLISTTOTALPRICETHREE: Total Price Three").
                             setSize(30);
      
      bid_judge_mat_list_blk.setView("BID_JUDGE_MAT_LIST");
      bid_judge_mat_list_blk.defineCommand("BID_JUDGE_MAT_LIST_API","Modify__");
      bid_judge_mat_list_blk.setMasterBlock(headblk);
      bid_judge_mat_list_set = bid_judge_mat_list_blk.getASPRowSet();
      bid_judge_mat_list_bar = mgr.newASPCommandBar(bid_judge_mat_list_blk);
      bid_judge_mat_list_bar.defineCommand(bid_judge_mat_list_bar.OKFIND, "okFindITEM2");
      bid_judge_mat_list_bar.defineCommand(bid_judge_mat_list_bar.NEWROW, "newRowITEM2");
      bid_judge_mat_list_tbl = mgr.newASPTable(bid_judge_mat_list_blk);
      bid_judge_mat_list_tbl.setTitle("BIDJUDGEMATLISTITEMHEAD2: BidJudgeMatList");
      bid_judge_mat_list_tbl.enableRowSelect();
      bid_judge_mat_list_tbl.setWrap();
      bid_judge_mat_list_lay = bid_judge_mat_list_blk.getASPBlockLayout();
      bid_judge_mat_list_lay.setDefaultLayoutMode(bid_judge_mat_list_lay.MULTIROW_LAYOUT);
      bid_judge_mat_list_lay.setDataSpan("ORG_ONE", 5);
      bid_judge_mat_list_lay.setDataSpan("ORG_TWO", 5);
      bid_judge_mat_list_lay.setDataSpan("ITEM1_BIDED_ORG", 5);

   }

   public void  adjust()
   {
      // fill function body
   }
   
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
             String cre_id = headset.getValue("CRE_ID");
             String bid_mat_id = headset.getValue("BID_MAT_ID");
             String id = headset.getValue("ID");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=BidMatEnqJudge.raq&proj_no="+proj_no+"&cre_id="+cre_id+"&bid_mat_id="+bid_mat_id+"&id="+id
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "BIDMATENQJUDGEDESC: Bid Mat Enq Judge";
   }


   protected String getTitle()
   {
      return "BIDMATENQJUDGETITLE: Bid Mat Enq Judge";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
//      if (bid_judge_mat_list_lay.isVisible())
//          appendToHTML(bid_judge_mat_list_lay.show());
      else 
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }  
      if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0)
      {
         appendToHTML(bid_judge_mat_list_lay.show());
      }

   }
}
