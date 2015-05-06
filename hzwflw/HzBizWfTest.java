package ifs.hzwflw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.docmaw.DocmawUtil;
import ifs.docmaw.edm.DocmawFtp;
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPLog;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.base.IfsException;
import ifs.fnd.base.SystemException;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.record.FndSqlType;
import ifs.fnd.record.FndSqlValue;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.sf.j2ee.FndJ2eeConnectionManager;
import ifs.fnd.sf.storage.FndConnection;
import ifs.fnd.sf.storage.FndStatement;
import ifs.fnd.util.Str;
import ifs.wordmw.util.GetWordFromOracle;
import ifs.wordmw.util.Word2Pdf;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import com.horizon.workflow.flowengine.impl.XMLEventInterface;
import com.horizon.workflow.flowengine.pub.XMLFlow;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class HzBizWfTest extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzBizWfTest");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   
   private ASPTabContainer tabs;
   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock hz_biz_wf_test_sona_blk;
   private ASPRowSet hz_biz_wf_test_sona_set;
   private ASPCommandBar hz_biz_wf_test_sona_bar;
   private ASPTable hz_biz_wf_test_sona_tbl;
   private ASPBlockLayout hz_biz_wf_test_sona_lay;

   private ASPBlock hz_hz_biz_wf_test_sonb_blk;
   private ASPRowSet hz_hz_biz_wf_test_sonb_set;
   private ASPCommandBar hz_hz_biz_wf_test_sonb_bar;
   private ASPTable hz_hz_biz_wf_test_sonb_tbl;
   private ASPBlockLayout hz_hz_biz_wf_test_sonb_lay;

   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  HzBizWfTest (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      
      
      
      
      
      
      ASPManager mgr = getASPManager();
      super.run();
      
      
      
      mgr.getAspRequest().getHeader("user-agent").indexOf("MSIE 8.0");
      System.out.println(mgr.getAspRequest().getHeader("iv-user"));

      if( mgr.commandBarActivated() ){
         String tempCommand = mgr.readValue("__COMMAND");
         System.out.println("ssss" + tempCommand);
         eval(mgr.commandBarFunction());
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("HEAD_ID")) )
         okFind();
      else   
         okFind();
      
      
      
      
      tabs.saveActiveTab();
//      if (headlay.isCustomLayout()||headlay.isEditLayout()||headlay.isSingleLayout()) {
//         if (tabs.getActiveTab() == 1) {
//            activateSonA();
//         } else if (tabs.getActiveTab() == 2) {
//            activateSonB();
//         }
//      }
     
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
         mgr.showAlert("HZBIZWFTESTNODATA: No data found.");
         headset.clear();
      }
//      eval( hz_biz_wf_test_sona_set.syncItemSets() );
//      eval( hz_hz_biz_wf_test_sonb_set.syncItemSets() );
      
      if(headset.countRows()>0){
         okFindITEM1();
         okFindITEM2();
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

      cmd = trans.addEmptyCommand("HEAD","HZ_BIZ_WF_TEST_API.New__",headblk);
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

      q = trans.addQuery(hz_biz_wf_test_sona_blk);
      q.addWhereCondition("HEAD_ID = ?");
      q.addParameter("HEAD_ID", headset.getValue("HEAD_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hz_biz_wf_test_sona_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","HZ_BIZ_WF_TEST_SONA_API.New__",hz_biz_wf_test_sona_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_HEAD_ID", headset.getValue("HEAD_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hz_biz_wf_test_sona_set.addRow(data);
   }
   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(hz_hz_biz_wf_test_sonb_blk);
      q.addWhereCondition("HEAD_ID = ?");
      q.addParameter("HEAD_ID", headset.getValue("HEAD_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,hz_hz_biz_wf_test_sonb_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM2","HZ_HZ_BIZ_WF_TEST_SONB_API.New__",hz_hz_biz_wf_test_sonb_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_HEAD_ID", headset.getValue("HEAD_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      hz_hz_biz_wf_test_sonb_set.addRow(data);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("HEAD_ID").
              setMandatory().
              setInsertable().
              setLabel("HZBIZWFTESTHEADID: Head Id").
              setWfProperties(1).
              setSize(50);
      headblk.addField("HEAD_NAME").
              setInsertable().
              setLabel("HZBIZWFTESTHEADNAME: Head Name").setWfProperties(2).
              setSize(50);
      headblk.addField("REMARK").
              setReadOnly().
              setLabel("HZBIZWFTESTREMARK: Remark").setDynamicLOV("DOC_CONTACT_UNIT").
              setSize(50);
      headblk.setView("HZ_BIZ_WF_TEST");
      headblk.defineCommand("HZ_BIZ_WF_TEST_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateSonA", "activateSonA");
      headbar.addCustomCommand("activateSonB", "activateSonB");
      headbar.addCustomCommand("newItemA", "newItemA");
      headbar.addCustomCommand("getReport", "getReport");
      
      headbar.addCustomCommand("convertWordToPdf", "convertWordToPdf");
      
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("HZBIZWFTESTTBLHEAD: Hz Biz Wf Tests");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 


      hz_biz_wf_test_sona_blk = mgr.newASPBlock("ITEM1");
      hz_biz_wf_test_sona_blk.addField("ITEM0_OBJID").
                              setHidden().
                              setDbName("OBJID");
      hz_biz_wf_test_sona_blk.addField("ITEM0_OBJVERSION").
                              setHidden().
                              setDbName("OBJVERSION");
      hz_biz_wf_test_sona_blk.addField("ITEM0_HEAD_ID").
                              setDbName("HEAD_ID").
                              setMandatory().
                              setInsertable().
                              setLabel("HZBIZWFTESTSONAITEM0HEADID: Head Id").
                              setSize(50);
      hz_biz_wf_test_sona_blk.addField("SON_ID").
                              setMandatory().
                              setInsertable().
                              setLabel("HZBIZWFTESTSONASONID: Son Id").
                              setSize(50);
      hz_biz_wf_test_sona_blk.addField("SON_NAME").
                              setInsertable().
                              setLabel("HZBIZWFTESTSONASONNAME: Son Name").
                              setSize(50);
      hz_biz_wf_test_sona_blk.setView("HZ_BIZ_WF_TEST_SONA");
      hz_biz_wf_test_sona_blk.defineCommand("HZ_BIZ_WF_TEST_SONA_API","New__,Modify__,Remove__");
      hz_biz_wf_test_sona_blk.setMasterBlock(headblk);
      hz_biz_wf_test_sona_set = hz_biz_wf_test_sona_blk.getASPRowSet();
      hz_biz_wf_test_sona_bar = mgr.newASPCommandBar(hz_biz_wf_test_sona_blk);
      hz_biz_wf_test_sona_bar.defineCommand(hz_biz_wf_test_sona_bar.OKFIND, "okFindITEM1");
      hz_biz_wf_test_sona_bar.defineCommand(hz_biz_wf_test_sona_bar.NEWROW, "newRowITEM1");
      hz_biz_wf_test_sona_tbl = mgr.newASPTable(hz_biz_wf_test_sona_blk);
      hz_biz_wf_test_sona_tbl.setTitle("HZBIZWFTESTSONAITEMHEAD1: HzBizWfTestSona");
      hz_biz_wf_test_sona_tbl.enableRowSelect();
      hz_biz_wf_test_sona_tbl.setWrap();
      hz_biz_wf_test_sona_lay = hz_biz_wf_test_sona_blk.getASPBlockLayout();
      hz_biz_wf_test_sona_lay.setDefaultLayoutMode(hz_biz_wf_test_sona_lay.MULTIROW_LAYOUT);

      hz_hz_biz_wf_test_sonb_blk = mgr.newASPBlock("ITEM2");
      hz_hz_biz_wf_test_sonb_blk.addField("ITEM1_OBJID").
                                 setHidden().
                                 setDbName("OBJID");
      hz_hz_biz_wf_test_sonb_blk.addField("ITEM1_OBJVERSION").
                                 setHidden().
                                 setDbName("OBJVERSION");
      hz_hz_biz_wf_test_sonb_blk.addField("ITEM1_HEAD_ID").
                                 setDbName("HEAD_ID").
                                 setMandatory().
                                 setInsertable().
                                 setLabel("HZHZBIZWFTESTSONBITEM1HEADID: Head Id").
                                 setSize(50);
      hz_hz_biz_wf_test_sonb_blk.addField("ITEM1_SON_ID").
                                 setDbName("SON_ID").
                                 setMandatory().
                                 setInsertable().
                                 setLabel("HZHZBIZWFTESTSONBITEM1SONID: Son Id").
                                 setSize(50);
      hz_hz_biz_wf_test_sonb_blk.addField("ITEM1_SON_NAME").
                                 setDbName("SON_NAME").
                                 setInsertable().
                                 setLabel("HZHZBIZWFTESTSONBITEM1SONNAME: Son Name").
                                 setSize(50);
      hz_hz_biz_wf_test_sonb_blk.setView("HZ_HZ_BIZ_WF_TEST_SONB");
      hz_hz_biz_wf_test_sonb_blk.defineCommand("HZ_HZ_BIZ_WF_TEST_SONB_API","New__,Modify__,Remove__");
      hz_hz_biz_wf_test_sonb_blk.setMasterBlock(headblk);
      hz_hz_biz_wf_test_sonb_set = hz_hz_biz_wf_test_sonb_blk.getASPRowSet();
      hz_hz_biz_wf_test_sonb_bar = mgr.newASPCommandBar(hz_hz_biz_wf_test_sonb_blk);
      hz_hz_biz_wf_test_sonb_bar.defineCommand(hz_hz_biz_wf_test_sonb_bar.OKFIND, "okFindITEM2");
      hz_hz_biz_wf_test_sonb_bar.defineCommand(hz_hz_biz_wf_test_sonb_bar.NEWROW, "newRowITEM2");
      hz_hz_biz_wf_test_sonb_tbl = mgr.newASPTable(hz_hz_biz_wf_test_sonb_blk);
      hz_hz_biz_wf_test_sonb_tbl.setTitle("HZHZBIZWFTESTSONBITEMHEAD2: HzBizWfTestSonb");
      hz_hz_biz_wf_test_sonb_tbl.enableRowSelect();
      hz_hz_biz_wf_test_sonb_tbl.setWrap();
      hz_hz_biz_wf_test_sonb_lay = hz_hz_biz_wf_test_sonb_blk.getASPBlockLayout();
      hz_hz_biz_wf_test_sonb_lay.setDefaultLayoutMode(hz_hz_biz_wf_test_sonb_lay.MULTIROW_LAYOUT);
      
      
      tabs = mgr.newASPTabContainer();
      tabs.addTab("SON_A", "javascript:commandSet('MAIN.activateSonA','')");
      tabs.addTab("SON_B", "javascript:commandSet('MAIN.activateSonB','')");

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);



   }
   
   public void newItemA(){
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      hz_biz_wf_test_sona_lay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
      cmd = trans.addEmptyCommand("ITEM1","HZ_BIZ_WF_TEST_SONA_API.New__",hz_biz_wf_test_sona_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_HEAD_ID", headset.getValue("HEAD_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      hz_biz_wf_test_sona_set.addRow(data);
   }
   
   public void getReport() throws FndException{
      ASPManager mgr = getASPManager();
      String todoUrl = "http://lqw-pc:8080/runqian/reportJsp/showReport.jsp?raq=innerDsReport";
      appendDirtyJavaScript("showNewBrowser_('" + todoUrl + "', (window.screen.availWidth-10), (window.screen.availHeight-30), 'NO'); \n");
   }
   
   public void convertWordToPdf() throws FndException{
      ASPManager mgr = getASPManager();
      String recordId = mgr.readValue("DOCUMENTID");
      if (!mgr.isEmpty(recordId))
      {
         String tempLocation = getTmpPath();
         GetWordFromOracle wordGetter = new GetWordFromOracle(recordId, tempLocation);
         String wordFilePath = wordGetter.getFile();
         String pdfFilePath = Word2Pdf.transfer(wordFilePath);
         
         mgr.showAlert(pdfFilePath);
//         return new String[]{wordFilePath,pdfFilePath};
      }
      else
      {
         mgr.showAlert("WORDMWGGPNOWORDFILE: You need to associate a word document.");
      }
      
   }


   public void adjust() throws FndException
   {
      super.adjust();
      headbar.removeCustomCommand("activateSonA");
      headbar.removeCustomCommand("activateSonB");
      // fill function body
      
      
      ifs.fnd.sf.j2ee.security.jboss.FndOracleLoginModule l;
      
      
      ASPManager mgr = getASPManager();
      String fromLogon = mgr.readValue("");
      
      
      java.lang.NoSuchMethodError d;
      
      
      
      
//      1.引入com.horizon.workflow.flowengine.pub.XMLFlow 类,该类存在于horizon-workflow.jar
//      com.horizon.workflow.flowengine.pub.XMLFlow xl;
      String nodeIds = XMLFlow.getNodeList("HZ4f82914288ee88071428e7c659c0626");
      System.out.println("^^^^^^^^" + nodeIds);
//      2.String nodes = XMLFlow.getNodeList(flowid);  --多个节点用~隔开
      
//      try {
////		test();010101 1000005 1 NA
////    	  [010102, 1013181, 1, NA, XCNH-000015-XCNB]
////		ifs.docmaw.edm.DocumentCheckIn.checkInDocument("010102", "1013181", "1", "NA", "1395286785604.doc.pdf", "XCNH-000015-XCNB.PDF");
//	
//      } catch (IfsException e) {
//		e.printStackTrace();
//	}
      
//      try {
//         ifs.docmaw.reportupload.DocumentCheckOut.downloadConnectedDocs("DesignCr", "CR_CODE=CR-LDI-1-CNPE-000001^PROJ_NO=1116^REV=A^"," AND DOC_CLASS='9901'");
//      } catch (SQLException e) {
//         e.printStackTrace();
//      } catch (IfsException e) {
//         e.printStackTrace();
//      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "HZBIZWFTESTDESC: Hz Biz Wf Test";
   }


   protected String getTitle()
   {
      return "HZBIZWFTESTTITLE: Hz Biz Wf Test";
   }
   
   
   
   public void activateSonA()
   {
      tabs.setActiveTab(1);
      okFindITEM1();
   }
   public void activateSonB()
   {
      tabs.setActiveTab(2);
      okFindITEM2();
   }


   protected void printContents() throws FndException
   {
      super.printContents();
      if(headlay.isSingleLayout()){
         printTextLabel("document id(test word to pdf): ");
         
         printField("DOCUMENTID", "", "");
         
         printText(System.getProperty("java.library.path"));
      }
      ASPManager mgr = getASPManager();
//      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      
      if (headlay.isCustomLayout()||headlay.isEditLayout()||headlay.isSingleLayout()) {
         appendToHTML(tabs.showTabsInit());
         if (tabs.getActiveTab() == 1) {
            appendToHTML(hz_biz_wf_test_sona_lay.show());
         } else if (tabs.getActiveTab() == 2) {
            appendToHTML(hz_hz_biz_wf_test_sonb_lay.show());
         }
      }
      
      
      

      
      
//      String currentNode = "";
//      appendDirtyJavaScript(" var nextNodeFieldCtl_ = parent.document.getElementById('nextNodeID');\n");
//      appendDirtyJavaScript(" alert(nextNodeFieldCtl_.value);\n");
//      appendDirtyJavaScript(" nextNodeFieldCtl_.value = 'Line2~Node2';\n");//Line2~Node2
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
//      appendDirtyJavaScript(" \n");
      
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------
   protected ASPBlock getBizWfBlock()
   {
      XMLEventInterface eventIterface;
      
      
      return headblk;
   }
   
   protected String sepecifyNextNode() throws FndException{//Add some Js Code Here to specify next nodeId(s).
      return "";//Line2~Node2
   }
   
   
   public void test() throws IfsException{
		FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
		FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
		FndStatement stmt=null;
		String sql="BEGIN IFSAPP.MATERIAL_REQUIS_LINE_API.Release_New(?,?,?,?,?,?,?,?,?); END;";
		
		String nextFileNo = "BEGIN ? := IFSAPP.EDM_FILE_API.GET_NEXT_FILE_NO(?,?,?,?,?); END;";;
		
		if(true)
		{
			try {
				stmt=conn.createStatement();
				stmt.prepareCall(nextFileNo);
				
				stmt.defineOutParameter("NEXT_FILE_NO", FndSqlType.NUMBER);
				FndSqlValue  docClass=new FndSqlValue("OBJID","010101",false);
				stmt.defineInParameter(docClass);
				FndSqlValue  docNo=new FndSqlValue("OBJID","1000005",false);
				stmt.defineInParameter(docNo);
				FndSqlValue  docSheet=new FndSqlValue("OBJID","1",false);
				stmt.defineInParameter(docSheet);
				FndSqlValue  docRev=new FndSqlValue("OBJID","NA",false);
				stmt.defineInParameter(docRev);
				FndSqlValue  docType=new FndSqlValue("OBJID","ORIGINAL",false);
				stmt.defineInParameter(docType);
				stmt.execute();
				stmt.getBigDecimal(1);
				
//				stmt.defineOutParameter("LINE_NO", FndSqlType.STRING);
//				stmt.defineOutParameter("RELEASE_NO", FndSqlType.STRING);
//				stmt.defineOutParameter("LINE_ITEM_NO", FndSqlType.NUMBER);
//				
//				String _objid="";
//				FndSqlValue  objid=new FndSqlValue("OBJID",_objid,false);
//				stmt.defineInOutParameter(objid);
//				
//				double _qty_due = 1.3d;
//				FndSqlValue  qty_due=new FndSqlValue("QTY_DUE",_qty_due);
//				stmt.defineInParameter(qty_due);
//				
//				
//				stmt.execute();
//				
//				String line_no=stmt.getString(1);
//				String release_no=stmt.getString(2);
			
				
			} catch (SystemException e) {
				e.printStackTrace();
			} catch (IfsException e) {
				e.printStackTrace();
			}finally{
				try {
					stmt.close();
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
		}
		
   }
   
   public void checkInDocument(
	         String doc_class,
	         String doc_no,
	         String doc_sheet,
	         String doc_rev,
	         String server_file_name,
	         String original_file_name) throws FndException
	   {
	      if (DEBUG) Util.debug(this + ": checkInDocument() {");

	      ASPManager mgr = getASPManager();

	      String doc_type = "ORIGINAL";
	      
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

	      trans.clear();
	      cmd = trans.addCustomFunction("GETNEXTFILENO",  "Edm_File_API.Get_Next_File_No", "OUT_1");
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", doc_type);
	      
	      trans = perform(trans);

	      String file_no = trans.getValue("GETNEXTFILENO/DATA/OUT_1");
	      
	      String file_ext = DocmawUtil.getFileExtention(server_file_name);
	      String file_type = getFileType(file_ext.toUpperCase());

	      trans.clear();
	      cmd = trans.addCustomFunction("GETREMOTEFILENAME", "Edm_File_Api.Generate_Remote_File_Name", "OUT_1");
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", file_ext);
	      cmd.addParameter("IN_NUM",  file_no);

	      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info2", "OUT_1");
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", doc_type);

	      trans = perform(trans);

	      String repository_file_name = trans.getValue("GETREMOTEFILENAME/DATA/OUT_1");;
	      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

	      if (DEBUG)
	         Util.debug(this + ": Creating file references...");

	      trans.clear();
	      
	      cmd = trans.addCustomCommand("CHECKINNEW", "Edm_File_API.Check_In_File_");
	      cmd.addParameter("OUT_1");
	      cmd.addParameter("OUT_2");
	      cmd.addParameter("OUT_3"); // Bug 59605, indicates if a new reference was created
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", original_file_name);
	      cmd.addParameter("IN_STR1", file_type);
	      cmd.addParameter("IN_STR1", "FALSE");
	      cmd.addParameter("IN_NUM",  file_no);
	      cmd = trans.addCustomCommand("SETSTATECHECKOUT", "Edm_File_API.Set_File_State");
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", doc_type);
	      cmd.addParameter("IN_NUM",  file_no);
	      cmd.addParameter("IN_STR1", "StartCheckOut");
	      cmd.addParameter("IN_STR1", null);
	      
	      trans = perform(trans);

	      // Put all files to the repository...
	      // bug 58326, starts....
	      String[] local_file_names = new String[1];
	      String[] repository_file_names = new String[1];
	      local_file_names[0] = server_file_name;
	      repository_file_names[0] = repository_file_name;
	      
	      //
	      // Bug 59605, Start
	      // Check in the files to the repository. If an exception occurs,
	      // role-back the state changes from Operation In Progress
	      //
	      try
	      {
	         putFilesToRepository(addKeys(edm_rep_info, doc_class, doc_no, doc_sheet, doc_rev), local_file_names, repository_file_names);
	      }
	      catch (FndException fnd)
	      {
	         ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();

	         boolean new_file = "TRUE".equals(trans.getValue("CHECKINNEW/DATA/OUT_3"));
	         
	         if (new_file)
	         {
	            cmd = trans2.addCustomCommand("DELETEFILEREF", "Edm_File_API.Remove_Invalid_Reference_");
	            cmd.addParameter("IN_STR1", doc_class);
	            cmd.addParameter("IN_STR1", doc_no);
	            cmd.addParameter("IN_STR1", doc_sheet);
	            cmd.addParameter("IN_STR1", doc_rev);
	            cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW/DATA/OUT_1"));
	            cmd.addParameter("IN_NUM",  file_no);
	         }
	         else
	         {
	            cmd = trans2.addCustomCommand("SETSTATECHECKOUT", "Edm_File_API.Set_File_State");
	            cmd.addParameter("IN_STR1", doc_class);
	            cmd.addParameter("IN_STR1", doc_no);
	            cmd.addParameter("IN_STR1", doc_sheet);
	            cmd.addParameter("IN_STR1", doc_rev);
	            cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW/DATA/OUT_1"));
	            cmd.addParameter("IN_NUM",  file_no);
	            cmd.addParameter("IN_STR1", "FinishCheckOut");
	            cmd.addParameter("IN_STR1", null);
	         }
	         perform(trans2);

	         // re-raise exception..
	         throw fnd;
	      }

	      if (DEBUG)
	         Util.debug(this + ": Files checked in to repository sucessfully..");
	      if (DEBUG)
	         Util.debug(this + ": Changing edm state..");

	      trans.clear();
	      
	      cmd = trans.addCustomCommand("SETSTATECHECKIN", "Edm_File_API.Set_File_State");
	      cmd.addParameter("IN_STR1", doc_class);
	      cmd.addParameter("IN_STR1", doc_no);
	      cmd.addParameter("IN_STR1", doc_sheet);
	      cmd.addParameter("IN_STR1", doc_rev);
	      cmd.addParameter("IN_STR1", doc_type);
	      cmd.addParameter("IN_NUM",  file_no);
	      cmd.addParameter("IN_STR1", "FinishCheckIn");
	      cmd.addParameter("IN_STR1", null);
	      
	      trans = perform(trans);
	      
	   }
   
   public ASPTransactionBuffer perform(ASPTransactionBuffer trans) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer response = null;
      
      try
      {
         response = mgr.performEx(trans);
      }
      catch(ASPLog.ExtendedAbortException e)
      {
         Buffer info = e.getExtendedInfo();
         String error_message = info.getItem("ERROR_MESSAGE").toString();
         error_message = error_message.substring(error_message.indexOf("=") + 1);
         throw new FndException(error_message);
      }
      
      return response;
   }
   
   
   // Get file type with file extention.
   public String getFileType(String file_ext) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_1");
      cmd.addParameter("IN_STR1", file_ext);
      trans = perform(trans);
      return trans.getValue("GETFILETYPE/DATA/OUT_1");
   }
   
   private String addKeys(String info,
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev)
   {
      StringBuffer strBuff = new StringBuffer(info);
      strBuff.append("DOC_CLASS="+doc_class+"^");
      strBuff.append("DOC_NO="+doc_no+"^");
      strBuff.append("DOC_SHEET="+doc_sheet+"^");
      strBuff.append("DOC_REV="+doc_rev+"^");
      return strBuff.toString();
   }
   
   /**
    *Use this method to put the files into respective repository for
    *each file. Use only if files are in different repositories.
    */
   protected void putFilesToRepository(String rep_info, String local_file_names[], String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer edm_rep_info_trans = mgr.newASPTransactionBuffer();
      
      String rep_type;
      String rep_address;
      String rep_user;
      String rep_password;
      String rep_port;
      String local_file;
      String tmpPath = "";
      
      try
      {
         tmpPath = getTmpPath();
      }
      catch(ifs.fnd.service.FndException e)
      {
      }
      
      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");      
      
      int no_files = local_file_names.length;
      String edm_rep_info;
      
      edm_rep_info_trans.clear();
      for (int i = 0;i<no_files;i++)
      {
         cmd = edm_rep_info_trans.addCustomFunction("EDMREPINFO_"+i, "Edm_File_Api.Get_Edm_Repository_Info3", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_NUM",  "1");
         cmd.addParameter("IN_STR1", repository_file_names[i]);         
      }
      
      edm_rep_info_trans = perform(edm_rep_info_trans);
      
      for (int i = 0;i<no_files;i++)
      {         
         edm_rep_info = edm_rep_info_trans.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
         rep_type = getStringAttribute(edm_rep_info, "LOCATION_TYPE");
         if ("2".equals(rep_type))
         {
            local_file = tmpPath + local_file_names[i];
            
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");
            
            if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____FTP_REP___XXXXXXXXXXXXXXXXXX");
            putSingleFileToFtpRepository(rep_address, rep_port, rep_user, rep_password, local_file, repository_file_names[i]);
         }
      }
   }
   
   protected void putSingleFileToFtpRepository(String ftp_address,
         String ftp_port,
         String ftp_user,
         String ftp_password,
         String local_file,
         String ftp_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": putSingleFileToFtpRepository() }");
      
      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();
      
      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
      
      String tp = getTmpPath();
      String local_file_ = local_file.startsWith(tp) ? local_file : tp + local_file;
      
      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         ftp_server.putBinaryFile(local_file_, ftp_file_name);
//         deleteFile(local_file_);
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED3: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
   }
   
   /**
    * This returns the temporary path ending with a system dependent path separator.
    */
   protected String getTmpPath() throws FndException
   {
      String      path = null;
      File        tmpDir;
      boolean     is_absolute;
      
      ASPManager mgr = getASPManager();
      
      //bug 58326
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("DEFAULTSYSCFGPATHFNDWEB", "Docman_Default_API.Get_Default_Value_", "OBJID");
      cmd.addParameter("OBJID","DocIssue");
      cmd.addParameter("OBJID","SYSCFG_SHARED_PATH_FNDWEB");
      trans = mgr.perform(trans);
      
      String defaultPath = trans.getValue("DEFAULTSYSCFGPATHFNDWEB/DATA/OBJID");
      
      
      //Bug Id 57006, Start 
      if (defaultPath == null)
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTDEFINED: The path for default value SYSCFG_SHARED_PATH_FNDWEB is not defined. You can define this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if (!(new File(defaultPath)).exists())
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTEXISTS: The path set for default value SYSCFG_SHARED_PATH_FNDWEB does not exist. You can change this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if ( ! defaultPath.endsWith( File.separator )  ) {
         defaultPath += File.separator;
      }
      //Bug Id 57006, End
      
      is_absolute = new File(defaultPath).isAbsolute();
      
      if(is_absolute)
      {
         /* For work with the existing hard coded path in docmawconfig.xml */
         path = defaultPath;
         if (DEBUG) Util.debug("DocSrv.getTmpPath - path 1 = " + path);
      }
      else
      {
         try
         {
            path = new File(mgr.getPhyPath(defaultPath)).getCanonicalPath();
            if (DEBUG) Util.debug("DocSrv.getTmpPath - path 2 = " + path);
         }
         catch(IOException e)
         {
            if (DEBUG) Util.debug("DocSrv.getTmpPath error: " + Str.getStackTrace(e));
            error(e);
         }
      }
      
      // Make sure it ends with a path separator
      
      path = path.charAt(path.length() - 1) == File.separatorChar ? path : path + File.separator;
      
      // Check that path exist
      File tmp_dir = new File(path);
      
      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 3 = " + path);
      
      if (!tmp_dir.exists())
      {
         createServerPath(path);
      }
      
      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 4 = " + path);
      
      return path;
   }
   
   // Create folder on extended server
   private void createServerPath(String path) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      File server_path = new File(path);
      
      if (!server_path.exists())
      {
         try
         {
            // Attempt to create the temp path..
            server_path.mkdirs();
         }
         catch(Exception e)
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVTMPPATHNOTEXIST: The directory specified by the parameter DOCMAW/DOCUMENT_TEMP_PATH could not be created on the server. The problem may be due to insufficient privileges. Contact your system adminstrator."));
         }
      }
   }
   
   private String handleBackSlash(String input)
   {
      String output = "";
      for (int k=0;k<input.length();k++)
         if("\\".equals(input.charAt(k)+""))
            output += "\\\\";
         else
            output += input.charAt(k);
      return output;
   }
   
   protected String getStringAttribute(String attr_string, String attr_name)
   {
      StringTokenizer st = new StringTokenizer(attr_string, "^");
      
      attr_name += "=";
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(attr_name))
         {
            return str.substring(attr_name.length());
         }
      }
      return "";
   }
   
   protected String decryptFtpPassword(String crypt_pwd)
   {
      if (DEBUG) Util.debug(this+": decryptFtpPassword() {");
      
      StringBuffer uncrypt_pwd = new StringBuffer();
      
      for (int i = crypt_pwd.length()-1; i>=0; i--)
      {
         char ch = crypt_pwd.charAt(i);
         switch (ch)
         {
         case 1: ch = 94;
         break;
         case 2: ch = 61;
         break;
         }
         
         if ((crypt_pwd.length() - i + 1) % 2 == 0)
         {
            ch = (char) (ch + (crypt_pwd.length() - i));
         }
         else
         {
            ch = (char) (ch - (crypt_pwd.length() - i));
         }
         uncrypt_pwd.append(ch);
      }
      
      if (DEBUG) Util.debug(this+":   decrypted FTP password: " + uncrypt_pwd.toString());
      if (DEBUG) Util.debug(this+":   decryptFtpPassword() {");
      
      return uncrypt_pwd.toString();
   }
   
}
