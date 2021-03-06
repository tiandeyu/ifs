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

public class BidProjEntrustReport extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.bidmaw.BidProjEntrustReport");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   protected ASPTabContainer tabs;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock bid_entrust_detail_blk;
   private ASPRowSet bid_entrust_detail_set;
   private ASPCommandBar bid_entrust_detail_bar;
   private ASPTable bid_entrust_detail_tbl;
   private ASPBlockLayout bid_entrust_detail_lay;

   private ASPBlock bid_entrust_quan_list_blk;
   private ASPRowSet bid_entrust_quan_list_set;
   private ASPCommandBar bid_entrust_quan_list_bar;
   private ASPTable bid_entrust_quan_list_tbl;
   private ASPBlockLayout bid_entrust_quan_list_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  BidProjEntrustReport (ASPManager mgr, String page_path)
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
      tabs.saveActiveTab();
      adjust();
   }
   
   public void validate(){
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String val = mgr.readValue("VALIDATE");
      float tempValue = 0;
      String txt="";
      String price="";
      String budgetName="";
      
      if("BUDGET_LINE_NO".equals(val)){
         cmd = trans.addCustomFunction("GETPRICE", 
               "PROJECT_BUDGET_LINE_API.GET_PRICE", "PRICE");
         cmd.addParameter("PROJ_NO,BUDGET_LINE_NO");
         cmd = trans.addCustomFunction("GETBUDGETNAME", 
               "PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME", "BUDGET_NAME");
         cmd.addParameter("PROJ_NO,BUDGET_LINE_NO");
         trans = mgr.validate(trans);
         price = trans.getValue("GETPRICE/DATA/PRICE");
         budgetName = trans.getValue("GETBUDGETNAME/DATA/BUDGET_NAME");
         txt=((mgr.isEmpty(price)) ? "" : price )+ "^"+((mgr.isEmpty(budgetName)) ? "" : budgetName )+ "^";
         mgr.responseWrite(txt);
      }
      if("PRICE".equals(val)){
         tempValue = ((mgr.readValue("COUNT")==null)?0:Float.parseFloat(mgr.readValue("COUNT")))*
         ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")));
         mgr.responseWrite(String.valueOf(tempValue)+"^");
       }
      if("COUNT".equals(val)) {         
          tempValue = ((mgr.readValue("COUNT")==null)?0:Float.parseFloat(mgr.readValue("COUNT")))*
          ((mgr.readValue("PRICE")==null)?0:Float.parseFloat(mgr.readValue("PRICE")));
          mgr.responseWrite(String.valueOf(tempValue)+"^");
      }
        
       
      mgr.endResponse();
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
         mgr.showAlert("BIDPROJENTRUSTREPORTNODATA: No data found.");
         headset.clear();
      }
      eval( bid_entrust_detail_set.syncItemSets() );
      eval( bid_entrust_quan_list_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","BID_PROJ_ENTRUST_REPORT_API.New__",headblk);
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

      q = trans.addQuery(bid_entrust_detail_blk);
      q.addWhereCondition("PROJ_NO = ? AND SERIAL_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("SERIAL_NO", headset.getValue("SERIAL_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,bid_entrust_detail_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","BID_ENTRUST_DETAIL_API.New__",bid_entrust_detail_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_SERIAL_NO", headset.getValue("SERIAL_NO"));
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      bid_entrust_detail_set.addRow(data);
   }
   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(bid_entrust_quan_list_blk);
      q.addWhereCondition("PROJ_NO = ? AND SERIAL_NO = ? AND ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("SERIAL_NO", headset.getValue("SERIAL_NO"));
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,bid_entrust_quan_list_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM2","BID_ENTRUST_QUAN_LIST_API.New__",bid_entrust_quan_list_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM1_SERIAL_NO", headset.getValue("SERIAL_NO"));
      cmd.setParameter("ITEM1_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      bid_entrust_quan_list_set.addRow(data);
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
              setLabel("BIDPROJENTRUSTREPORTPROJNO: Proj No").
              setSize(30).
              setDynamicLOV("GENERAL_PROJECT");
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("GENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("SERIAL_NO").
              setInsertable().
              setMandatory().
              setDefaultNotVisible().
              setLabel("BIDPROJENTRUSTREPORTENTRUSTBILLNO: Entrust Bill No").
              setSize(30).
              setDynamicLOV("BID_PROJ_ENTRUST","PROJ_NO");
      headblk.addField("ENTRUST_NAME").
              setReadOnly().
              setFunction("BID_PROJ_ENTRUST_API.GET_ENTRUST_NAME ( :PROJ_NO,:SERIAL_NO)").
              setLabel("BIDPROJENTRUSTENTRUSTNAME: Entrust Name").
              setSize(30);
      mgr.getASPField("SERIAL_NO").setValidation("ENTRUST_NAME");
      headblk.addField("ID").
              setInsertable().
              setLabel("BIDPROJENTRUSTREPORTID: Id").
              setSize(30).
              setHidden();
      headblk.addField("HANDLER").
              setReadOnly().
              setDefaultNotVisible().
              setLabel("BIDPROJENTRUSTREPORTHANDLER: Handler").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :HANDLER)").
              setLabel("BIDMATENQJUDGECREATEPERSONNAME: Create Person Name").
              setSize(30).
              setDefaultNotVisible().
              setReadOnly();
      headblk.addField("HANDLE_DATE","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDPROJENTRUSTREPORTHANDLEDATE: Handle Date").
              setSize(30);
      headblk.addField("ENTRUST_CONTENT").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDPROJENTRUSTREPORTENTRUSTCONTENT: Entrust Content").
              setSize(129).
              setHeight(5);
      headblk.addField("STATUS").
              setInsertable().
              setLabel("BIDPROJENTRUSTREPORTSTATUS: Status").
              setSize(30).
              setHidden();
      headblk.addField("NOTE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("BIDPROJENTRUSTREPORTNOTE: Note").
              setSize(129).
              setHeight(3).
              setHidden();
      headblk.setView("BID_PROJ_ENTRUST_REPORT");
      headblk.defineCommand("BID_PROJ_ENTRUST_REPORT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("BIDPROJENTRUSTREPORTTBLHEAD: Bid Proj Entrust Reports");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC"); 
      headlay.setSimple("ENTRUST_NAME"); 
      headlay.setDataSpan("HANDLE_DATE", 5);
      headlay.setDataSpan("ENTRUST_CONTENT", 5);
      headlay.setSimple("CREATE_PERSON_NAME");
      
      //multiple tabs
      headbar.addCustomCommand("activateBidEntrustDetail", "Bid Entrust Detail");
      headbar.addCustomCommand("activateBidEntrustQuanList", "Bid Entrust Quan List");
      
 


      bid_entrust_detail_blk = mgr.newASPBlock("ITEM1");
      bid_entrust_detail_blk.addField("ITEM0_OBJID").
                             setHidden().
                             setDbName("OBJID");
      bid_entrust_detail_blk.addField("ITEM0_OBJVERSION").
                             setHidden().
                             setDbName("OBJVERSION");
      bid_entrust_detail_blk.addField("ITEM0_PROJ_NO").
                             setDbName("PROJ_NO").
                             setMandatory().
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILITEM0PROJNO: Proj No").
                             setSize(30).setHidden();
      bid_entrust_detail_blk.addField("ITEM0_SERIAL_NO").
                             setDbName("SERIAL_NO").
                             setMandatory().
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILITEM0SERIALNO: Serial No").
                             setSize(30).setHidden();
      bid_entrust_detail_blk.addField("ITEM0_ID").
                             setDbName("ID").
                             setMandatory().
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILITEM0ID: Id").
                             setSize(30).setHidden();
      bid_entrust_detail_blk.addField("LINE_NO").
                             setLabel("BIDENTRUSTDETAILLINENO: Line No").
                             setSize(30).
                             setReadOnly();
      bid_entrust_detail_blk.addField("PROJ_NAME").
                             setInsertable().
                          /*   setDynamicLOV("CON_QUA_TREE","PROJ_NO").
                             setLOVProperty("TREE_PARE_FIELD", "PARENT_ID").
                             setLOVProperty("TREE_DISP_FIELD", "NODE_NO,NODE_NAME").*/
                             setLabel("BIDENTRUSTDETAILPROJNAME: Proj Name").
                             setSize(30);
     /* bid_entrust_detail_blk.addField("SUB_PROJ_DESC").
                             setFunction("CON_PROJ_CONSTRUCTION_MAN_API.Get_Node_Name ( :PROJ_NO,:PROJ_NAME)").
                             setLabel("CONPROJCONNECTIONLISTCOSTORGSUBPROJDESC: SUB PROJ DESC").
                             setReadOnly().
                             setSize(30);
      mgr.getASPField("PROJ_NAME").setValidation("SUB_PROJ_DESC");*/
      bid_entrust_detail_blk.addField("ENTRUST_ORG").
                             setFunction("BID_PROJ_ENTRUST_API.GET_ENTRUST_ORG ( :PROJ_NO,:SERIAL_NO)").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILENTRUSTORG: Entrust Org").
                             setSize(30).
                             setDynamicLOV("ISO_UNIT");
      bid_entrust_detail_blk.addField("CONST_ORG_APP_SUM").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILCONSTORGAPPSUM: Const Org App Sum").
                             setSize(30);
      bid_entrust_detail_blk.addField("THIRD_JUDGE_SUM").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILTHIRDJUDGESUM: Third Judge Sum").
                             setSize(30);
      bid_entrust_detail_blk.addField("THIRD_JUDGE_PERSON").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILTHIRDJUDGEPERSON: Third Judge Person").
                             setSize(30);
      bid_entrust_detail_blk.addField("PROJ_DEPT_JUDGE_SUM").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILPROJDEPTJUDGESUM: Proj Dept Judge Sum").
                             setSize(30);
      bid_entrust_detail_blk.addField("PROJ_JUDGE_PERSON").
                             setInsertable().
                             setLabel("BIDENTRUSTDETAILPROJJUDGEPERSON: Proj Judge Person").
                             setSize(30);
      bid_entrust_detail_blk.setView("BID_ENTRUST_DETAIL");
      bid_entrust_detail_blk.defineCommand("BID_ENTRUST_DETAIL_API","New__,Modify__,Remove__");
      bid_entrust_detail_blk.setMasterBlock(headblk);
      bid_entrust_detail_set = bid_entrust_detail_blk.getASPRowSet();
      bid_entrust_detail_bar = mgr.newASPCommandBar(bid_entrust_detail_blk);
      bid_entrust_detail_bar.defineCommand(bid_entrust_detail_bar.OKFIND, "okFindITEM1");
      bid_entrust_detail_bar.defineCommand(bid_entrust_detail_bar.NEWROW, "newRowITEM1");
      bid_entrust_detail_tbl = mgr.newASPTable(bid_entrust_detail_blk);
      bid_entrust_detail_tbl.setTitle("BIDENTRUSTDETAILITEMHEAD1: BidEntrustDetail");
      bid_entrust_detail_tbl.enableRowSelect();
      bid_entrust_detail_tbl.setWrap();
      bid_entrust_detail_lay = bid_entrust_detail_blk.getASPBlockLayout();
      bid_entrust_detail_lay.setDefaultLayoutMode(bid_entrust_detail_lay.MULTIROW_LAYOUT);
//      bid_entrust_detail_lay.setSimple("SUB_PROJ_DESC");

      bid_entrust_quan_list_blk = mgr.newASPBlock("ITEM2");
      bid_entrust_quan_list_blk.addField("ITEM1_OBJID").
                                setHidden().
                                setDbName("OBJID");
      bid_entrust_quan_list_blk.addField("ITEM1_OBJVERSION").
                                setHidden().
                                setDbName("OBJVERSION");
      bid_entrust_quan_list_blk.addField("ITEM1_PROJ_NO").
                                setDbName("PROJ_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTITEM1PROJNO: Proj No").
                                setSize(30).setHidden();
      bid_entrust_quan_list_blk.addField("ITEM1_SERIAL_NO").
                                setDbName("SERIAL_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTITEM1SERIALNO: Serial No").
                                setSize(30).setHidden();
      bid_entrust_quan_list_blk.addField("ITEM1_ID").
                                setDbName("ID").
                                setMandatory().
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTITEM1ID: Id").
                                setSize(30).setHidden();
      bid_entrust_quan_list_blk.addField("ITEM1_LINE_NO").
                                setDbName("LINE_NO").
                                setHidden().
                                setLabel("BIDENTRUSTQUANLISTITEM1LINENO: Line No").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("ORG_NAME").
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTORGNAME: Org Name").
                                setSize(30).setHidden();
      bid_entrust_quan_list_blk.addField("BUDGET_LINE_NO").
                                setDbName("BUDGE_LINE_NO").
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTBUDGETLINENO: Budge Line No").
                                setSize(30).
                                setCustomValidation("PROJ_NO,BUDGET_LINE_NO", "PRICE,BUDGET_NAME").
                                setDynamicLOV("PROJECT_BUDGET_LINE");
      bid_entrust_quan_list_blk.addField("BUDGET_NAME").
                                setReadOnly().
                                setFunction("PROJECT_BUDGET_LINE_API.GET_BUDGET_NAME ( :PROJ_NO,:BUDGET_LINE_NO)").
                                setLabel("BIDENTRUSTQUANLISTBUDGETNAME: Budget Name").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("CONTRACT_SIGN_COUNT","Number").
                                setInsertable().
                                setHidden().
                                setLabel("BIDENTRUSTQUANLISTCONTRACTSIGNCOUNT: Contract Sign Count").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("CONTRACT_SIGN_AMOUNT","Number").
                                setInsertable().
                                setHidden().
                                setLabel("BIDENTRUSTQUANLISTCONTRACTSIGNAMOUNT: Contract Sign Amount").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("FEE_NAME").
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTFEENAME: Fee Name").
                                setSize(30).setHidden();
      bid_entrust_quan_list_blk.addField("UNIT").
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTUNIT: Unit").
                                setSize(30).
                                setDynamicLOV("ISO_UNIT");
      bid_entrust_quan_list_blk.addField("COUNT","Number").
                                setInsertable().
                                setLabel("BIDENTRUSTQUANLISTCOUNT: Count").
                                setCustomValidation("COUNT,PRICE", "TOTAL_PRICE").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("PRICE","Number").
                                setReadOnly().
                                setLabel("BIDENTRUSTQUANLISTPRICE: Price").
                                setCustomValidation("COUNT,PRICE", "TOTAL_PRICE").
                                setSize(30);
      bid_entrust_quan_list_blk.addField("TOTAL_PRICE","Number").
                                setReadOnly().
                                setLabel("BIDENTRUSTQUANLISTTOTALPRICE: Total Price").
                                setSize(30);
      bid_entrust_quan_list_blk.setView("BID_ENTRUST_QUAN_LIST");
      bid_entrust_quan_list_blk.defineCommand("BID_ENTRUST_QUAN_LIST_API","New__,Modify__,Remove__");
      bid_entrust_quan_list_blk.setMasterBlock(headblk);
      bid_entrust_quan_list_set = bid_entrust_quan_list_blk.getASPRowSet();
      bid_entrust_quan_list_bar = mgr.newASPCommandBar(bid_entrust_quan_list_blk);
      bid_entrust_quan_list_bar.defineCommand(bid_entrust_quan_list_bar.OKFIND, "okFindITEM2");
      bid_entrust_quan_list_bar.defineCommand(bid_entrust_quan_list_bar.NEWROW, "newRowITEM2");
      bid_entrust_quan_list_tbl = mgr.newASPTable(bid_entrust_quan_list_blk);
      bid_entrust_quan_list_tbl.setTitle("BIDENTRUSTQUANLISTITEMHEAD2: BidEntrustQuanList");
      bid_entrust_quan_list_tbl.enableRowSelect();
      bid_entrust_quan_list_tbl.setWrap();
      bid_entrust_quan_list_lay = bid_entrust_quan_list_blk.getASPBlockLayout();
      bid_entrust_quan_list_lay.setDefaultLayoutMode(bid_entrust_quan_list_lay.MULTIROW_LAYOUT);
      bid_entrust_quan_list_lay.setSimple("BUDGET_NAME");

      //tabs
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("BIDENTRUSTDETAIL: Bid Entrust Detail"), "javascript:commandSet('MAIN.activateBidEntrustDetail','')");
      tabs.addTab(mgr.translate("BIDENTRUSTQUANLIST: Bid Entrust Quan List"), "javascript:commandSet('MAIN.activateBidEntrustQuanList','')");
   }



   public void  adjust()
   {
      // fill function body
      headbar.removeCustomCommand("activateBidEntrustDetail");    
      headbar.removeCustomCommand("activateBidEntrustQuanList");
   
      ASPManager mgr = getASPManager();
      if("ITEM1.Back".equals(mgr.readValue("__COMMAND"))){
         mgr.getASPField("PROJ_NAME").setHidden();
      }
      else if("ITEM1.SaveReturn".equals(mgr.readValue("__COMMAND"))){
         mgr.getASPField("PROJ_NAME").setHidden();
      }
      else if("ITEM1.OkFind".equals(mgr.readValue("__COMMAND"))){
         mgr.getASPField("PROJ_NAME").setHidden();
      }
      
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   public void activateBidEntrustDetail(){
      tabs.setActiveTab(1);
      okFindITEM1();
   }
   
   public void activateBidEntrustQuanList(){
      tabs.setActiveTab(2);
      okFindITEM2();
   }
   
   protected String getDescription()
   {
      return "BIDPROJENTRUSTREPORTDESC: Bid Proj Entrust Report";
   }


   protected String getTitle()
   {
      return "BIDPROJENTRUSTREPORTTITLE: Bid Proj Entrust Report";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
//      if (bid_entrust_detail_lay.isVisible())
//          appendToHTML(bid_entrust_detail_lay.show());
//      if (bid_entrust_quan_list_lay.isVisible())
//          appendToHTML(bid_entrust_quan_list_lay.show());
      else 
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }  
      if (headset.countRows()>0)
      {
         if (headlay.isSingleLayout()||headlay.isCustomLayout() ||headlay.isEditLayout())
         {
            appendToHTML(tabs.showTabsInit());
            if (tabs.getActiveTab()== 1)
            {
               appendToHTML(bid_entrust_detail_lay.show());
            }else if(tabs.getActiveTab()== 2){
               appendToHTML(bid_entrust_quan_list_lay.show());
            }   
            appendToHTML(tabs.showTabsFinish());
            
          } 
      }
      
   }
}
