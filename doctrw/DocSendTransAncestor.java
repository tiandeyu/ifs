package ifs.doctrw;

import ifs.docmaw.DocmawConstants;
import ifs.docmaw.DocmawUtil;
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.wordmw.GoldGridProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class DocSendTransAncestor  extends GoldGridProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.doctrw.DocSendTrans");
   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   protected ASPBlock headblk;
   protected ASPRowSet headset;
   protected ASPCommandBar headbar;
   protected ASPTable headtbl;
   protected ASPBlockLayout headlay;
   
   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock gold_grid_blk;
   private ASPRowSet gold_grid_set;
   private ASPCommandBar gold_grid_bar;
   private ASPTable gold_grid_tbl;
   private ASPBlockLayout gold_grid_lay;

   private ASPBlock doc_send_trans_rcv_blk;
   private ASPRowSet doc_send_trans_rcv_set;
   private ASPCommandBar doc_send_trans_rcv_bar;
   private ASPTable doc_send_trans_rcv_tbl;
   private ASPBlockLayout doc_send_trans_rcv_lay;
   
   private ASPBlock doc_trans_reference_object_blk;
   private ASPRowSet doc_trans_reference_object_set;
   private ASPCommandBar doc_trans_reference_object_bar;
   private ASPTable doc_trans_reference_object_tbl;
   private ASPBlockLayout doc_trans_reference_object_lay;
   
   protected ASPTabContainer tabs;
   
   ASPTransactionBuffer trans;
   ASPCommand           cmd;
   ASPContext           ctx;
   
   boolean synchronizeAndUpload = false;
   
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public DocSendTransAncestor(ASPManager mgr, String page_path) {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();
      ASPManager mgr = getASPManager();
      
      ctx     = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      if( mgr.commandBarActivated() )
      {
        
       String tempCommand = mgr.readValue("__COMMAND");
       if("SYNCHRONIZEANDUPLOAD".equals(tempCommand)){
          synchronizeAndUpload = false;
          System.out.println("SYNCHRONIZEANDUPLOAD");
          synchronizeDocCodeAndUpload();
       }else{
             eval(mgr.commandBarFunction());
             if("ITEM1.SaveReturn".equals(tempCommand) 
                   || "ITEM1.Delete".equals(tempCommand) 
                   || "ITEM1.SaveNew".equals(tempCommand)
                   || "ITEM1.OverviewSave".equals(tempCommand) ){
                headset.refreshRow();
             }
             else if ("MAIN.Perform".equals(tempCommand))
             {
                String perform_command = mgr.readValue("__MAIN_Perform");
                if("openMoreWord".equals(perform_command)){
                   activateGoldGrid();
//                   openMoreWord();
                }else if("synchronizeDocCodeAndUpload".equals(perform_command)){
                  activateGoldGrid();
                  headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
                  synchronizeAndUpload = true;
                }
             }else if("MAIN.SaveReturn".equals(tempCommand)){
                String synchronizeAndUpload = mgr.readValue("SYNCHRONIZEANDUPLOAD", null);
                if("TRUE".equals(synchronizeAndUpload)){
                     if (headset.countRows() > 0 && "Released".equals(headset.getValue("OBJSTATE")))
                     {
                        if (!Str.isEmpty(headset.getValue("DOC_CLASS")) && !Str.isEmpty(headset.getValue("DOC_NO")) && 
                            !Str.isEmpty(headset.getValue("DOC_SHEET")) && !Str.isEmpty(headset.getValue("DOC_REV")))
                        {
                           try
                           {
                              saveAsPdf1(headset.getValue("DOC_CLASS"),
                                     headset.getValue("DOC_NO"),
                                     headset.getValue("DOC_SHEET"),
                                     headset.getValue("DOC_REV"),
                                     headset.getValue("DOC_CODE") + ".PDF");
                           }
                           catch (FndException e)
                           {
                              e.printStackTrace();
                           }
                        }else{
                         mgr.showAlert("DOCSENDTRANSALERTMUSTSENDBEFOREUPLOAD: you must send the doc before you can upload the file.");
                        }
                     }else{
                       mgr.showAlert("DOCSENDTRANSALERTMUSTSENDBEFOREUPLOAD: you must send the doc before you can upload the file.");
                     }
                }
             }
       }

      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEND_TRANS_ID")) )
         okFind();
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         performRefreshParent();
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
      
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      
      if (FROM_NAVIGATOR.equals(mgr.readValue(FROM_FLAG,FROM_NAVIGATOR)))
      {
         // Data isolation
         /*String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
         StringBuffer sb = new StringBuffer("(");
         if(!"()".equals(tempPersonZones)){
            sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
         }else{
            sb.append("(1=2)");
         }
         sb.append(")");
         q.addWhereCondition(sb.toString());*/
         q.addWhereCondition("CREATE_PERSON = Person_Info_API.Get_Id_For_User(Fnd_Session_API.Get_Fnd_User)");
      }
      
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("DOCSENDTRANSNODATA: No data found.");
         headset.clear();
      }else{
         if(headlay.isSingleLayout()){
            activateGoldGrid();
         }
      }
   }

   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      
      if (FROM_NAVIGATOR.equals(mgr.readValue(FROM_FLAG,FROM_NAVIGATOR)))
      {
         // Data isolation
         q.addWhereCondition("CREATE_PERSON = Person_Info_API.Get_Id_For_User(Fnd_Session_API.Get_Fnd_User)");
      }
      
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

      cmd = trans.addEmptyCommand("HEAD","DOC_SEND_TRANS_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("DOC_CLASS", getCurrentPageDocClass());
      
      
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         cmd.setParameter("SUB_CLASS", subClass);
         if(DocmawConstants.PROJ_SEND_MEETING.equals(subClass)){
            cmd.setParameter("PURPOSE_NO", "99");
         }
      }
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }
   
   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFindITEM1()
   {
      if (  headset.countRows() == 0 ){
         return;
      }
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(doc_send_trans_rcv_blk);
      q.addWhereCondition("SEND_TRANS_ID = ?");
      q.addParameter("SEND_TRANS_ID", headset.getValue("SEND_TRANS_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,doc_send_trans_rcv_blk);
      headset.goTo(headrowno);
      
   }
   
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","DOC_SEND_TRANS_RCV_API.New__",doc_send_trans_rcv_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_SEND_TRANS_ID", headset.getValue("SEND_TRANS_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      doc_send_trans_rcv_set.addRow(data);
   }
   
   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
      String luName = headblk.getLUName();//
      String view = headblk.getView();//
      String objid  = headset.getValue("OBJID");
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      ASPCommand  cmd = trans.addCustomCommand("KEYREF", " client_sys.get_key_reference");
      cmd.addParameter("KEYREF", "S", "OUT", null);
      cmd.addParameter("BIZ_LU", "S", "IN", luName);
      cmd.addParameter("BIZ_OBJID", "S", "IN", objid);
      trans = mgr.validate(trans);
      String keyReference = trans.getValue("KEYREF/DATA/KEYREF");
      trans.clear();
      q = trans.addQuery(doc_trans_reference_object_blk);
      q.addWhereCondition("LU_NAME = ?");
      q.addWhereCondition("KEY_REF = ?");
      q.addParameter("ITEM3_LU_NAME", luName);
      q.addParameter("ITEM3_KEY_REF", keyReference);
      q.includeMeta("ALL");
      int headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,doc_trans_reference_object_blk);
      headset.goTo(headrowno);
   }
   
   
   public void transToDocIssueFiles()
   {
      ASPManager mgr = getASPManager();
      
      headset.store();     
      
      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
         
      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
         
      if (keys.countItems() > 0) 
      {
         String url = getPageUrl(headset.getValue("DOC_CLASS"));
         mgr.transferDataTo(url, keys);
      } 
      else 
      {
         mgr.showAlert(mgr.translate("DOCSENDTRANSNORECSEL: No records selected."));
      }
   }
   
   private String getPageUrl(String doc_class)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETPAGEURL", "Doc_Class_API.Get_Page_Url", "OUT_1");
      cmd.addParameter("DOC_CLASS", doc_class);
      trans = mgr.perform(trans);
      return Str.isEmpty(trans.getValue("GETPAGEURL/DATA/OUT_1")) ? "" : trans.getValue("GETPAGEURL/DATA/OUT_1");
   }
   
   public void performRefreshParent() 
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      //
      // Perform any necessary actions before refreshing
      //
      
      if ("DOC_SEND_TRANS_RCV_API".equals(mgr.readValue("REFRESH_CHILD"))) 
      {
         refreshHeadset();
         activateReceiveBlk();
      }
   }
   
   private void refreshHeadset() 
   {
      /*ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int row_no = headset.getCurrentRowNo();
      
      ASPQuery q = trans.addEmptyQuery(headblk);
      
      String subClass = getCurrentPageSubDocClass();
      if(!Str.isEmpty(subClass)){
         q.addWhereCondition("SUB_CLASS='" + subClass + "'");
      }
      
      // Data isolation
      String tempPersonZones = mgr.getASPContext().findGlobal(ifs.genbaw.GenbawConstants.PERSON_ZONES);
      StringBuffer sb = new StringBuffer("(");
      if(!"()".equals(tempPersonZones)){
         sb.append("(ZONE_NO IN " + tempPersonZones).append(")");
      }else{
         sb.append("(1=2)");
      }
      sb.append(")");
      q.addWhereCondition(sb.toString());
      
      q.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);
      headset.goTo(row_no);*/
      if (headset.countRows() > 0)
         headset.refreshRow();
   }
   

   //-----------------------------------------------------------------------------
   //------------------------  Perform Header and Item functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void performHEAD( String command)
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
   
   public void  release()
   {
      performHEAD( "Release__" );
      synchronizeAndUpload = true;
//      headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
      activateGoldGrid();
   }
   
   public void synchronizeDocCodeAndUpload() {
      if (headset.countRows() > 0 && "Released".equals(headset.getValue("OBJSTATE"))) {

         ASPManager mgr = getASPManager();

         StringBuilder sb = new StringBuilder(
               "select t.unit_no UNIT_NO, t.doc_class DOC_CLASS, t.doc_no DOC_NO, t.doc_sheet DOC_SHEET, t.doc_rev DOC_REV, t.doc_code DOC_CODE ");
         sb.append("from doc_send_trans_rcv t where T.is_main='TRUE' AND T.send_trans_id= ?");
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPQuery query = trans.addQuery("DOCISSUEKEYS", sb.toString());
         query.addInParameter("SEND_TRANS_ID", headset.getValue("SEND_TRANS_ID"));
         trans = mgr.perform(trans);
         ASPBuffer buffer = trans.getBuffer("DOCISSUEKEYS");
         
         // Modified by Terry 20131206
         String sub_class = headset.getValue("SUB_CLASS");
         int count = 0;
         if (buffer != null)
         {
            try
            {
               count = Integer.parseInt(buffer.getBuffer("INFO").getValue("ROWS"));
            }
            catch(Exception e)
            {
               count = 0;
            }
         }
         if (DocmawConstants.PROJ_SEND_MEMO.equals(sub_class) && count > 0)
            count = 1;
         // Modified end
         
         String[] tempStrArray = null;
         List<String[]> tempList = new ArrayList<String[]>();
         
         for (int i = 0; i < count; i++) {
            ASPBuffer dataBuffer = buffer.getBuffer("DATA");
            if ("DATA".equals(buffer.getNameAt(i))) {
               tempStrArray = new String[5];
               tempStrArray[0] = buffer.getBufferAt(i).getValue("DOC_CLASS");
               tempStrArray[1] = buffer.getBufferAt(i).getValue("DOC_NO");
               tempStrArray[2] = buffer.getBufferAt(i).getValue("DOC_SHEET");
               tempStrArray[3] = buffer.getBufferAt(i).getValue("DOC_REV");
               tempStrArray[4] = buffer.getBufferAt(i).getValue("DOC_CODE");
               tempList.add(tempStrArray);
            }
         }

         if (tempList.size() > 0) {
            String[] tempFiles = null;
            try {
               tempFiles = convertDocToPdf();// transfer only once.
               String pdfFilePath = tempFiles[1];
               // upload many times
               for (Iterator iterator = tempList.iterator(); iterator.hasNext();) {
                  String[] tempDocKeys = (String[]) iterator.next();
                  String serverFileName = DocmawUtil.getFileName(pdfFilePath);
                  String originalFileName = tempDocKeys[4] + ".PDF";
                  checkInDocument(tempDocKeys[0], tempDocKeys[1],tempDocKeys[2], tempDocKeys[3], serverFileName,originalFileName);
               }
               mgr.showAlert("UPLOADFILESUCCEED: Successfully upload File.");
            } catch (FndException e) {
               mgr.showAlert("UPLOADFILEFAILED: Upload File Failed." + "\n"
                     + e.getMessage());
            } finally {
               if (null != tempFiles) {
                  for (int i = 0; i < tempFiles.length; i++) {
                     String tempFilePath = tempFiles[i];
                     if (!Str.isEmpty(tempFilePath)) {
                        try {
                           deleteFile(tempFilePath);
                        } catch (FndException e) {
                           System.out.println("warning:delete file failed.");
                           e.printStackTrace();
                        }
                     }
                  }
               }
            }

         } else {
            // no-op
         }
      }

   }


   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      int size_long = 113;
      super.preDefine();
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID");
      headblk.addField("OBJVERSION");
      headblk.addField("OBJSTATE");
      headblk.addField("OBJEVENTS");
      headblk.addField("LU_NAME");
      headblk.addField("KEY_REF");
      
      headblk.addField("SEND_TRANS_ID").
      setInsertable().
      setLabel("DOCSENDTRANSSENDTRANSID: Send Trans Id").
      setSize(20);
      
      headblk.addField("REV_TITLE").
      setInsertable().
      setLabel("DOCSENDTRANSREVTITLE: Rev Title").
      setWfProperties(1).
      setMandatory().
      setSize(size_long);
      
      headblk.addField("DOC_CODE").
      setReadOnly().
      setLabel("DOCSENDTRANSDOCCODE: Doc Code").
      setSize(20);
      
      headblk.addField("PROJ_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCSENDTRANSPROJNO: Proj No").
      setCustomValidation("PROJ_NO" , "PROJ_NAME").
      setDynamicLOV("GENERAL_PROJECT").
      setSize(20);
      
      headblk.addField("PROJ_NAME").
      setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
      setLabel("DOCSENDTRANSPROJNAME: Proj Name").
      setReadOnly().
      setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_NAME");
      
      headblk.addField("PURPOSE_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCSENDTRANSPURPOSENO: Purpose No").
      setDynamicLOV("DOC_COMMUNICATION_SEQ").
      setCustomValidation("PURPOSE_NO", "PURPOSE_NAME").
      setSize(20);
      
      headblk.addField("PURPOSE_NAME").
      setReadOnly().
      setLabel("DOCSENDTRANSPURPOSENAME: Purpose Name").
      setFunction("DOC_COMMUNICATION_SEQ_API.Get_Purpose_Name(:PURPOSE_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("PURPOSE_NO").setValidation("PURPOSE_NAME");
      
      headblk.addField("SEND_UNIT_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCSENDTRANSSENDUNITNO: Send Unit No").
      setDynamicLOV("GENERAL_ZONE_LOV").
      setCustomValidation("SEND_UNIT_NO", "SEND_UNIT_NAME").
      setSize(20);
      
      headblk.addField("SEND_UNIT_NAME").
      setReadOnly().
      setLabel("DOCSENDTRANSSENDUNITNAME: Send Unit Name").
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:SEND_UNIT_NO)").
      setReadOnly().
      setSize(20);
      mgr.getASPField("SEND_UNIT_NO").setValidation("SEND_UNIT_NAME");
      
      headblk.addField("MAIN_SEND_UNIT_NO").
      setReadOnly().
      setLabel("DOCSENDTRANSMAINSENDUNITNO: Main Send Unit No").
      setSize(20);
      
      headblk.addField("MAIN_SEND_UNIT_NAME").
      setReadOnly().
      setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:MAIN_SEND_UNIT_NO)").
      setSize(20);
      
      headblk.addField("DRAFT_DEPT_NO").
      setInsertable().
      setMandatory().
      setLabel("DOCSENDTRANSDRAFTDEPTNO: Draft Dept No").
      setDynamicLOV("GENERAL_DEPARTMENT_LOV", "SEND_UNIT_NO PARENT_ORG_NO", 500, 550, true, true).
      setCustomValidation("DRAFT_DEPT_NO", "DRAFT_DEPT_NAME").
      setSize(20);
      
      headblk.addField("DRAFT_DEPT_NAME").
      setReadOnly().
      setLabel("DOCSENDTRANSDRAFTDEPTNAME: Draft Dept Name").
      setFunction("GENERAL_ORGANIZATION_API.GET_ORG_DESC(:DRAFT_DEPT_NO)").
      setSize(20);
      mgr.getASPField("DRAFT_DEPT_NO").setValidation("DRAFT_DEPT_NAME");
      
      headblk.addField("CO_SEND_UNIT_NO").
      setReadOnly().
      setLabel("DOCSENDTRANSCOSENDUNITNO: Co Send Unit No").
      setSize(20);
      
      headblk.addField("CO_SEND_UNIT_NAME").
      setReadOnly().
      setLabel("DOCSENDTRANSCOSENDUNITNAME: Co Send Unit Name").
      setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:CO_SEND_UNIT_NO)").
      setSize(20);

      headblk.addField("PAGES","Number").
      setInsertable().
      setLabel("DOCSENDTRANSPAGES: Pages").
      setSize(20);
      
      headblk.addField("ATTACH_PAGES","Number").
      setInsertable().
      setLabel("DOCSENDTRANSATTACHPAGES: Attach Pages").
      setSize(20);
      
      
      //
      //Hidden field begin...
      //
      headblk.addField("ZONE_NO").
      setInsertable().
      setLabel("DOCSENDTRANSZONENO: Zone No").
      setSize(20);
      
      headblk.addField("GOLD_GRID_RECORD_ID").
      setInsertable().
      setLabel("DOCSENDTRANSGOLDGRIDRECORDID: Gold Grid Record Id").
      setSize(20);
      
      headblk.addField("GOLD_GRID_TEMP_ID").
      setInsertable().
      setLabel("DOCSENDTRANSGOLDGRIDTEMPID: Gold Grid Temp Id").
      setSize(20);
      
      headblk.addField("GOLD_GRID_SIGN").
      setInsertable().
      setLabel("DOCSENDTRANSGOLDGRIDSIGN: Gold Grid Sign").
      setSize(20);
      
      headblk.addField("STATE").
      setReadOnly().
      setLabel("DOCSENDTRANSSTATE: State").
      setSize(20);
      
      headblk.addField("DOC_CLASS");
      headblk.addField("DOC_NO");
      headblk.addField("DOC_SHEET");
      headblk.addField("DOC_REV");
      
      
      headblk.addField("TRANSFER_NO").
      setInsertable().
      setLabel("DOCSENDTRANSTRANSFERNO: Transfer No").
      setSize(20);
      
      headblk.addField("CREATE_DATE","Date").
      setReadOnly().
      setLabel("DOCSENDTRANSCREATEDATE: Create Date").
      setSize(20);
      
      /*headblk.addField("DRAFT_DATE", "Date").
      setDbName("CREATE_DATE").
      setHidden().
      setLabel("DOCSENDTRANSDRAFTDATE: Draft Date").
      setSize(20);*/
      
      headblk.addField("AUTH_DATE","Date", "yyyy-MM-dd").
      setReadOnly().
      setLabel("DOCSENDTRANSAUTHDATE: Auth Date").
      setSize(20);
      
      headblk.addField("SEND_DATE", "Date", "yyyy-MM-dd").
      setReadOnly().
      setLabel("DOCSENDTRANSSENDDATE: Send Date").
      setSize(20);
      
      headblk.addField("APPROVE_DATE","Date").
      setReadOnly().
      setLabel("DOCSENDTRANSAPPROVEDATE: Approve Date").
      setSize(20);
      
      headblk.addField("RECEIPT").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("DOCSENDTRANSRECEIPT: Receipt").
      setSize(5);
      
      headblk.addField("TRANSLATED_RECEIPT").
      setReadOnly().setHidden().
      setSize(5);
      
      headblk.addField("RECEIPT_REQUEST","Date").
      setInsertable().
      setLabel("DOCSENDTRANSRECEIPTREQUEST: Receipt Request").
      setSize(20);
      
      headblk.addField("PRESENTER").
      setInsertable().
      setLabel("DOCSENDTRANSPRESENTER: Presenter").
      setSize(20);
      
      headblk.addField("MEETING_DATE","Date").
      setInsertable().
      setLabel("DOCSENDTRANSMEETINGDATE: Meeting Date").
      setSize(20);
      
      headblk.addField("MEETING_LOC").
      setInsertable().
      setLabel("DOCSENDTRANSMEETINGLOC: Meeting Loc").
      setSize(20);
      
      headblk.addField("HOST_UNIT").
      setInsertable().
      setLabel("DOCSENDTRANSHOSTUNIT: Host Unit").
      setSize(100);
      
      headblk.addField("ATTEND_UNIT").
      setInsertable().
      setLabel("DOCSENDTRANSATTENDUNIT: Attend Unit").
      setSize(100);
      
      headblk.addField("SUB_CLASS").
      setInsertable().
      setLabel("DOCSENDTRANSSUBCLASS: Sub Class").
      setSize(20);
      
      headblk.addField("ATTACH_TYPE").
      setInsertable().
      setLabel("DOCSENDTRANSATTACHTYPE: Attach Type").
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("WHERE", "COMP_DOC = 'TRUE'").
      setSize(20);
      
      headblk.addField("ATTACH_TYPE_NAME").setFunction("DOC_CLASS_API.Get_Name(:ATTACH_TYPE)").setLabel("DOCISSUEATTACHTYPENAME: Attach Type Name").setReadOnly().setSize(20);    
      mgr.getASPField("ATTACH_TYPE").setValidation("ATTACH_TYPE_NAME");
      
      headblk.addField("MEETING_TYPE").
      setInsertable().
      setLabel("DOCSENDTRANSMEETINGTYPE: Meeting Type").
      setDynamicLOV("DOC_MEETING_TYPE").
      setSize(20);      
      
      headblk.addField("MEETING_TYPE_NAME").setFunction("Doc_Meeting_Type_Api.Get_Type_Name(:MEETING_TYPE)").setLabel("DOCISSUEMEETINGTYPENAME: Meeting Type Name").setReadOnly().setSize(20);    
      mgr.getASPField("MEETING_TYPE").setValidation("MEETING_TYPE_NAME");
      
      
      headblk.addField("INNER_SEND").
      setInsertable().
      setLabel("DOCSENDTRANSINNERSEND: Inner Send").
      setDynamicLOV("GENERAL_DEPARTMENT_CU_LOV", "", 500, 550, true, true).
      setSize(20);
      
      headblk.addField("INNER_SEND_NAME").setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:INNER_SEND)").setLabel("DOCISSUEINNERSENDNAME: Inner Send Name").setReadOnly().setSize(20);      
      
      headblk.addField("CREATE_PERSON").
      setReadOnly().
      setLabel("DOCSENDTRANSCREATEPERSON: Create Person").
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCSENDTRANSLISTOFPERSONID: List of Person Id")).
      setSize(20);
      
      headblk.addField("CREATE_PERSON_NAME").setFunction("Person_Info_Api.Get_Name(:CREATE_PERSON)").setReadOnly().setSize(20);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("AUTH_PERSON").
      setReadOnly().
      setLabel("DOCSENDTRANSAUTHPERSON: Auth Person").
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCSENDTRANSLISTOFPERSONID: List of Person Id")).
      setSize(20);
      
      headblk.addField("AUTH_PERSON_NAME").setFunction("Person_Info_Api.Get_Name(:AUTH_PERSON)").setReadOnly().setSize(20);
      mgr.getASPField("AUTH_PERSON").setValidation("AUTH_PERSON_NAME");
      
      headblk.addField("APPROVE_PERSON").
      setReadOnly().
      setLabel("DOCSENDTRANSAPPROVEPERSON: Approve Person").
      setDynamicLOV("PERSON_INFO_USER").
      setLOVProperty("TITLE",mgr.translate("DOCSENDTRANSLISTOFPERSONID: List of Person Id")).
      setSize(20);
      
      headblk.addField("APPROVE_PERSON_NAME").setFunction("Person_Info_Api.Get_Name(:APPROVE_PERSON)").setReadOnly().setSize(20);
      mgr.getASPField("APPROVE_PERSON").setValidation("APPROVE_PERSON_NAME");
      
      headblk.addField("ATTACH").setFunction("''").setLabel("DOCSENDTRANSATTACH: Attach").setReadOnly().setSize(20);
      
      headblk.addField("CREATE_OPINION").
      setReadOnly().
      setLabel("DOCSENDTRANSANCESTORCREATEOPINION: Create Opinion").
      setHeight(3).
//      setClientFunc(" style=\"ime-mode:disabled\" onkeypress=\"event.returnValue = false;\" onpaste=\"return false;\" ").
      setSize(size_long);
      
      headblk.addField("AUTH_OPINION").
      setReadOnly().
      setLabel("DOCSENDTRANSANCESTORAUTHOPINION: Auth Opinion").
      setHeight(3).
//      setClientFunc(" style=\"ime-mode:disabled\" onkeypress=\"event.returnValue = false;\" onpaste=\"return false;\" ").
      setSize(size_long);
      
      headblk.addField("APPROVE_OPINION").
      setReadOnly().
      setLabel("DOCSENDTRANSANCESTORAPPROVEOPINION: Approve Opinion").
      setHeight(3).
//      setClientFunc(" style=\"ime-mode:disabled\" onkeypress=\"event.returnValue = false;\" onpaste=\"return false;\" ").
      setSize(size_long);
      
      //
      //Hidden field end.
      //
      headblk.setView("DOC_SEND_TRANS");
      headblk.defineCommand("DOC_SEND_TRANS_API","New__,Modify__,Remove__,Release__");
      headblk.enableFuncFieldsNonSelect();
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      //tab command
      headbar.addCustomCommand("activateGoldGrid", "Gold Grid...");
      headbar.addCustomCommand("activateReceiveBlk", "Receive Unit...");
      headbar.addCustomCommand("activateReferenceObject", "Activate Reference Object...");
      headbar.addSecureCustomCommand("Release", "DOCSENDTRANSRELEASE: Release...", "DOC_SEND_TRANS_API.Release__");
      headbar.defineCommand("Release", null, "validateIsignatureUsbKey");
      headbar.addSecureCustomCommand("transToDocIssueFiles", mgr.translate("DOCFILESENDDOCISSUEFILES: Document Info..."), "DOC_ISSUE");
      headbar.addCommandValidConditions("transToDocIssueFiles", "OBJSTATE", "Enable", "Released");
      headbar.addCommandValidConditions("transToDocIssueFiles", "DOC_NO", "Disable", "");
      headbar.enableMultirowAction();
      headbar.addCommandValidConditions("Release", "OBJSTATE", "Enable", "Initialized");
      
      headbar.removeFromMultirowAction("Release");
      
      headbar.addCustomCommand("CreateDocConnection", "DOCTRWDSTCREDOCCONN: Create Doc Connection...", "../common/images/toolbar/" + mgr.getLanguageCode() + "/connectDocument.gif", true);
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_VIEW, "DOC_ISSUE_LOV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_VIEW_PARAMS, "ATTACH_TYPE DOC_CLASS");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_PARPA, "LU_NAME,KEY_REF");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_FIEPA, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_TARG_FIE, "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_TARG_PKG, "DOC_REFERENCE_OBJECT_API");
      headbar.setCmdProperty("CreateDocConnection", headbar.CMD_PRO_ADD_TAR_VIEW, "DOC_REFERENCE_OBJECT");
      headbar.removeFromMultirowAction("CreateDocConnection");
      headbar.addCommandValidConditions("CreateDocConnection", "OBJSTATE", "Enable", "Initialized");
      
      headbar.addSecureCustomCommand("CreateUnit", "DOCTRWDSTCREATEUNIT: Create Unit...", "DOC_SEND_TRANS_RCV_API.New__", "../common/images/toolbar/" + mgr.getLanguageCode() + "/addZone.gif", true);
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_VIEW, "GENERAL_ZONE_LOV");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_PARPA, "SEND_TRANS_ID");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_FIEPA, "ZONE_NO");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_TARG_FIE, "UNIT_NO");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_TARG_PKG, "DOC_SEND_TRANS_RCV_API");
      headbar.setCmdProperty("CreateUnit", headbar.CMD_PRO_ADD_TAR_VIEW, "DOC_SEND_TRANS_RCV");
      headbar.removeFromMultirowAction("CreateUnit");
      headbar.addCommandValidConditions("CreateUnit", "OBJSTATE", "Enable", "Initialized");


      headlay = headblk.getASPBlockLayout();
      
      customizeHeadlay();
      
      headlay.setSimple("PURPOSE_NAME");
      headlay.setSimple("PROJ_NAME");
      headlay.setSimple("SEND_UNIT_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("AUTH_PERSON_NAME");
      headlay.setSimple("APPROVE_PERSON_NAME");
      headlay.setSimple("MAIN_SEND_UNIT_NAME");
      headlay.setSimple("CO_SEND_UNIT_NAME");
      headlay.setSimple("INNER_SEND_NAME");
      headlay.setSimple("ATTACH_TYPE_NAME");
      headlay.setSimple("MEETING_TYPE_NAME");
      
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCSENDTRANSTBLHEAD: Doc Send Transs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      
      addGoldGridCmd(headbar);
      
      gold_grid_blk  = mgr.newASPBlock("ITEM2");
      gold_grid_blk.addField("ITEM2_IN_1").setHidden();
      gold_grid_blk.setView("dual");
      gold_grid_blk.setMasterBlock(headblk);
      gold_grid_bar = mgr.newASPCommandBar(gold_grid_blk);
      gold_grid_tbl = mgr.newASPTable(gold_grid_blk);
      gold_grid_tbl.setTitle("DOCSENDTRANSRCVITEMHEAD1: DocSendTransRcv");
      gold_grid_tbl.enableRowSelect();
      gold_grid_tbl.setWrap();
      gold_grid_lay = gold_grid_blk.getASPBlockLayout();
      gold_grid_lay.setDefaultLayoutMode(gold_grid_lay.CUSTOM_LAYOUT);
      
      
      doc_send_trans_rcv_blk = mgr.newASPBlock("ITEM1");
      doc_send_trans_rcv_blk.addField("ITEM0_OBJID").
      setHidden().
      setDbName("OBJID");
      
      doc_send_trans_rcv_blk.addField("ITEM0_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      doc_send_trans_rcv_blk.addField("ITEM0_SEND_TRANS_ID").
      setDbName("SEND_TRANS_ID").
      setMandatory().
      setInsertable().
      setLabel("DOCSENDTRANSRCVITEM0SENDTRANSID: Send Trans Id").
      setHidden().
      setSize(20);
      
      doc_send_trans_rcv_blk.addField("VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      unsetInsertable().
      setLabel("DOCMAWDOCTITLEOVWVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV", "NEWWIN").
      setAsImageField();
      
      doc_send_trans_rcv_blk.addField("IS_ELE_DOC").
      setCheckBox("FALSE,TRUE").
      setFunction("EDM_FILE_API.Have_Edm_File(:SUB_DOC_CLASS,:SUB_DOC_NO,:SUB_DOC_SHEET,:SUB_DOC_REV)").
      setReadOnly().
      setHidden().
      setLabel("DOCTITLEISELEDOC: Is Ele Doc").
      setSize(5);
      

      doc_send_trans_rcv_blk.addField("UNIT_NO").
      setMandatory().
      setInsertable().
      setDynamicLOV("GENERAL_ZONE_LOV").
      setLabel("DOCSENDTRANSRCVUNITNO: Unit No").
      setSize(20);
      
      doc_send_trans_rcv_blk.addField("UNIT_NAME").
      setLabel("DOCSENDTRANSRCVUNITNAME: Unit Name").
      setReadOnly().
      setFunction("GENERAL_ZONE_API.GET_ZONE_DESC(:UNIT_NO)").
      setSize(20);
      mgr.getASPField("UNIT_NO").setValidation("UNIT_NAME");

      
      doc_send_trans_rcv_blk.addField("IS_MAIN").
      setCheckBox("FALSE,TRUE").
      setInsertable().
      setLabel("DOCSENDTRANSRCVISMAIN: Is Main").
      setSize(5);
      
      doc_send_trans_rcv_blk.addField("SUB_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCSENDTRANSREFERENCEBLKDOCCODE: Doc Code").setReadOnly().setHyperlink("../docmaw/DocmawIntermediatePage.page?FROMFLAG=DOCSENDTRANS&SUB_CLASS="+getCurrentPageSubDocClass()+"&", "SUB_DOC_CLASS,SUB_DOC_NO,SUB_DOC_SHEET,SUB_DOC_REV");
      doc_send_trans_rcv_blk.addField("SUB_DOC_CLASS").setDbName("DOC_CLASS").setHidden();
      doc_send_trans_rcv_blk.addField("SUB_DOC_NO").setDbName("DOC_NO").setHidden();
      doc_send_trans_rcv_blk.addField("SUB_DOC_SHEET").setDbName("DOC_SHEET").setHidden();
      doc_send_trans_rcv_blk.addField("SUB_DOC_REV").setDbName("DOC_REV").setHidden();

      doc_send_trans_rcv_blk.setView("DOC_SEND_TRANS_RCV");
      doc_send_trans_rcv_blk.defineCommand("DOC_SEND_TRANS_RCV_API","New__,Modify__,Remove__");
      doc_send_trans_rcv_blk.setMasterBlock(headblk);
      doc_send_trans_rcv_set = doc_send_trans_rcv_blk.getASPRowSet();
      doc_send_trans_rcv_bar = mgr.newASPCommandBar(doc_send_trans_rcv_blk);
      doc_send_trans_rcv_bar.defineCommand(doc_send_trans_rcv_bar.OKFIND, "okFindITEM1");
      doc_send_trans_rcv_bar.defineCommand(doc_send_trans_rcv_bar.NEWROW, "newRowITEM1");
      doc_send_trans_rcv_tbl = mgr.newASPTable(doc_send_trans_rcv_blk);
      doc_send_trans_rcv_tbl.setTitle("DOCSENDTRANSRCVITEMHEAD1: DocSendTransRcv");
      doc_send_trans_rcv_tbl.enableRowSelect();
      doc_send_trans_rcv_tbl.setWrap();
      doc_send_trans_rcv_lay = doc_send_trans_rcv_blk.getASPBlockLayout();
      doc_send_trans_rcv_lay.setDefaultLayoutMode(doc_send_trans_rcv_lay.MULTIROW_LAYOUT);
      
      //DOC_TRANS_REFERENCE_OBJECT
      doc_trans_reference_object_blk = mgr.newASPBlock("ITEM3");
      doc_trans_reference_object_blk.addField("ITEM3_VIEW_FILE").
      setFunction("''").
      setReadOnly().
      unsetQueryable().
      setLabel("DOCSENDTRANSREFERENCEBLKVIEWFILE: View File").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "ITEM3_DOC_CLASS DOC_CLASS,ITEM3_DOC_NO DOC_NO,ITEM3_DOC_SHEET DOC_SHEET,ITEM3_DOC_REV DOC_REV", "NEWWIN").
      setAsImageField();
      doc_trans_reference_object_blk.addField("ITEM3_DOC_CLASS").setDbName("DOC_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKDOCCLASS: Doc Class");
      doc_trans_reference_object_blk.addField("ITEM3_DOC_NO").setDbName("DOC_NO").setLabel("DOCSENDTRANSREFERENCEBLKDOCNO: Doc No").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_DOC_SHEET").setDbName("DOC_SHEET").setLabel("DOCSENDTRANSREFERENCEBLKDOCSHEET: Doc sheet").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_DOC_REV").setDbName("DOC_REV").setLabel("DOCSENDTRANSREFERENCEBLKDOCREV: Doc Rev").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_LU_NAME").setDbName("LU_NAME").setLabel("DOCSENDTRANSREFERENCEBLKLUNAME: Lu Name").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_KEY_REF").setDbName("KEY_REF").setLabel("DOCSENDTRANSREFERENCEBLKKEYREF: Key Ref").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_KEY_VALUE").setDbName("KEY_VALUE").setLabel("DOCSENDTRANSREFERENCEBLKKEYVALUE: Key Value").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_REV_TITLE").setDbName("DOC_TITLE").setLabel("DOCSENDTRANSREFERENCEBLKREVTITLE: Rev Title").setFieldHyperlink("DocIssue.page", "ITEM3_PAGE_URL","ITEM3_DOC_CLASS DOC_CLASS,ITEM3_DOC_NO DOC_NO,ITEM3_DOC_SHEET DOC_SHEET,ITEM3_DOC_REV DOC_REV");
      doc_trans_reference_object_blk.addField("ITEM3_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCSENDTRANSREFERENCEBLKDOCCODE: Doc Code");
      doc_trans_reference_object_blk.addField("ITEM3_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKSUBCLASS: Sub Class");
      doc_trans_reference_object_blk.addField("ITEM3_PAGE_URL").setFunction("nvl(DOC_CLASS_API.Get_Page_Url(:ITEM3_DOC_CLASS), DOC_SUB_CLASS_API.Get_Page_Url(:ITEM3_DOC_CLASS,:ITEM3_SUB_CLASS))").setHidden();
      doc_trans_reference_object_blk.addField("ITEM3_IS_ELE_DOC").
      setFunction("EDM_FILE_API.Have_Edm_File(:ITEM3_DOC_CLASS,:ITEM3_DOC_NO,:ITEM3_DOC_SHEET,:ITEM3_DOC_REV)").
      setHidden().
      setLabel("DOCSENDTRANSREFERENCEBLKISELEDOC: Is Ele Doc").
      setSize(5);
      
      doc_trans_reference_object_blk.setView("DOC_REFERENCE_OBJECT");
        
      doc_trans_reference_object_blk.setMasterBlock(headblk);
      doc_trans_reference_object_set = doc_trans_reference_object_blk.getASPRowSet();
      doc_trans_reference_object_bar = mgr.newASPCommandBar(doc_trans_reference_object_blk);
      doc_trans_reference_object_bar.defineCommand(doc_trans_reference_object_bar.OKFIND, "okFindITEM3");
      doc_trans_reference_object_tbl = mgr.newASPTable(doc_trans_reference_object_blk);
      doc_trans_reference_object_tbl.enableRowSelect();
      doc_trans_reference_object_tbl.setWrap();
      doc_trans_reference_object_lay = doc_trans_reference_object_blk.getASPBlockLayout();
      doc_trans_reference_object_lay.setDefaultLayoutMode(doc_trans_reference_object_lay.MULTIROW_LAYOUT);
      
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCSENDTRANSGOLDDOCUMENT: Gold Grid Document"), "javascript:commandSet('MAIN.activateGoldGrid','')");
      tabs.addTab(mgr.translate("DOCSENDTRANSREFERENCEOBJECT: Reference Object"), "javascript:commandSet('MAIN.activateReferenceObject','')");
      tabs.addTab(mgr.translate("DOCSENDTRANSSENDTO: Send To"), "javascript:commandSet('MAIN.activateReceiveBlk','')");
   }
   

   public void  adjust() throws FndException
   {
      // fill function body
      ASPManager mgr = getASPManager();
      super.adjust();
      
      //Added by lqw 20131029
      try{
         ASPField[] fields = headblk.getFields();
         ASPField tempField = null;
         for (int i = 0; i < fields.length; i++) {
            tempField = fields[i];
            tempField.setHidden();
         }
      }catch(Throwable th){

      }
      if(headblk.getFuncFieldsNonSelect()){
         this.setFuncFieldValue(headblk);
      }
      //Added end.
      
      
      
      headbar.removeCustomCommand("activateReceiveBlk");
      headbar.removeCustomCommand("activateReferenceObject");
      headbar.removeCustomCommand("activateGoldGrid");
      
      if(headlay.isSingleLayout() && headset.countRows() > 0)
      {
         if(!mgr.isEmpty(headset.getValue("OBJSTATE")) && "Released".equals(headset.getValue("OBJSTATE")))
         {
            headbar.disableCommand(ASPCommandBar.EDIT);
            headbar.disableCommand(ASPCommandBar.EDITROW);
            headbar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
            headbar.disableCommand(ASPCommandBar.DELETE);
            
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.EDIT);
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.EDITROW);
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.OVERVIEWEDIT);
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.DELETE);
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.NEWROW);
            doc_send_trans_rcv_bar.disableCommand(ASPCommandBar.DUPLICATE);
         }
      }
      
      /*String fromPage = mgr.readValue(FROM_FLAG,FROM_NAVIGATOR);
      if (FROM_TODO.equals(fromPage))
      {
         headbar.disableCustomCommand("CreateDocConnection");
         headbar.disableCustomCommand("CreateUnit");
      }
      
      if (FROM_NAVIGATOR.equals(fromPage) && headset.countRows() > 0 && (headlay.isSingleLayout() || headlay.isCustomLayout()))
      {
         String currentBizProcessId = getCurrentBizProcessId();
         if (!mgr.isEmpty(currentBizProcessId))
         {
            headbar.disableCustomCommand("CreateDocConnection");
            headbar.disableCustomCommand("CreateUnit");
         }
      }*/
      
//      if(!fromPage.equals(FROM_TODO)){
//         headbar.removeCustomCommand("synchronizeDocCodeAndUpload");
//      }
   }
   
   protected void ctlIfsCustomBtnFromNavWithFlow() throws Exception
   {
      headbar.disableCustomCommand("CreateDocConnection");
      headbar.disableCustomCommand("CreateUnit");
   }
   
   protected void ctlIfsCustomBtnFromTodo() throws Exception
   {
      headbar.disableCustomCommand("CreateDocConnection");
      headbar.disableCustomCommand("CreateUnit");
   }
   protected void ctlIfsCustomBtnFromDone() throws Exception
   {
      headbar.disableCustomCommand("CreateDocConnection");
      headbar.disableCustomCommand("CreateUnit");
   }
   protected void ctlIfsCustomBtnFromToRead() throws Exception
   {
      headbar.disableCustomCommand("CreateDocConnection");
      headbar.disableCustomCommand("CreateUnit");
   }
   protected void ctlIfsCustomBtnFromRead() throws Exception
   {
      headbar.disableCustomCommand("CreateDocConnection");
      headbar.disableCustomCommand("CreateUnit");
   }
   
   public void activateReceiveBlk()
   {
      tabs.setActiveTab(3);
      okFindITEM1();
   }
   
   public void activateReferenceObject()
   {
      tabs.setActiveTab(2);
      okFindITEM3();
   }
   public void activateGoldGrid()
   {
      tabs.setActiveTab(1);
   }
   
   
   protected String getCurrentPageDocClass() {
      return DocmawConstants.PROJ_SEND;
   }
   
   protected abstract String getCurrentPageSubDocClass();
   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------
   
   protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
   {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0)
      {
         if ("VIEW_FILE".equals(imageField.getName()))
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
      }
      return "";
   }


   protected String getDescription()
   {
      return getTitle();
   }


   protected String getTitle()
   {
      return  "DOCSENDTRANSTITLE: Doc Send Trans";
   }


   protected void printContents() throws FndException
   {
      super.printContents();
      
      printHiddenField("REFRESH_PARENT", "FALSE");
      printHiddenField("REFRESH_CHILD", "");
      
      if(synchronizeAndUpload){
        printHiddenField("SYNCHRONIZEANDUPLOAD", "TRUE");
      }
      
      if (headlay.isVisible())
         appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      
      
      if (headset.countRows()>0)
      {
         if (headlay.isSingleLayout()||headlay.isCustomLayout() ||headlay.isEditLayout())
         {
//        	printButton("grouptest", "GROUPTEST", "onclick='GroupTest();'");
            appendToHTML(tabs.showTabsInit());
            if (tabs.getActiveTab()== 1)
            {
               printGoldGrid();
            }
            else if (tabs.getActiveTab()== 2)
            {
               appendToHTML(doc_trans_reference_object_lay.show());
            }
            else if (tabs.getActiveTab()== 3)
            {
               appendToHTML(doc_send_trans_rcv_lay.show());
            }
            appendToHTML(tabs.showTabsFinish());
         }
         
         if(headlay.isSingleLayout() && synchronizeAndUpload && headset.countRows() > 0){
          // in single layout, the field name is not printed. so if you want to synchronize
          printHiddenField("REV_TITLE", headset.getValue("REV_TITLE"));
          printHiddenField("DOC_CODE", headset.getValue("DOC_CODE"));
          printHiddenField("SYNCHRONIZEANDUPLOAD", "TRUE");
         }
         
         if(headlay.isNewLayout() || headlay.isEditLayout()){
            appendDirtyJavaScript("function checkMandatory_(field,label,msg)  \n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  if( field.type.substr(0,6)=='select' )  \n");
            appendDirtyJavaScript("  {  \n");
            appendDirtyJavaScript("    if( field.options[field.selectedIndex].value=='' )\n");
            appendDirtyJavaScript("      return missingValueError_(field,label,msg);  \n");
            appendDirtyJavaScript("  }  \n");
            appendDirtyJavaScript("  else if( field.type.substr(0,6)=='hidden' )  \n");
            appendDirtyJavaScript("  {  \n");
            appendDirtyJavaScript("    return true; \n");
            appendDirtyJavaScript("  } \n");
            appendDirtyJavaScript("  else  \n");
            appendDirtyJavaScript("  {  \n");
            appendDirtyJavaScript("    if( field.value == '' )  \n");
            appendDirtyJavaScript("      return missingValueError_(field,label,msg);  \n");
            appendDirtyJavaScript("  }  \n");
            appendDirtyJavaScript("  return true;  \n");
            appendDirtyJavaScript("}\n");
            
            
//            appendDirtyJavaScript("getField_('CREATE_OPINION','-1').disabled=true;");
//            appendDirtyJavaScript("getField_('AUTH_OPINION','-1').disabled=true;");
//            appendDirtyJavaScript("getField_('APPROVE_OPINION','-1').disabled=true;");
//            
            
         }
         
      }
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
   }
   
   protected void initParameter() {
      blockName = "MAIN";
      goldgridRecordFieldName = "GOLD_GRID_RECORD_ID";
      goldgridTemplateFieldName = "GOLD_GRID_TEMP_ID";
      signFieldName = "GOLD_GRID_SIGN";
   }

   @Override
   protected ASPBlock getBizWfBlock() {
      return headblk;
   }

   @Override
   protected boolean goldGridActivated() {
      
      return tabs.getActiveTab() == 1;
   }
   
   protected abstract void customizeHeadlay();

}
