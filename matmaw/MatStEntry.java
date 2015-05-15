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

package ifs.matmaw;
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

public class MatStEntry extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatStEntry");

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

   private ASPBlock mat_st_entry_line_blk;
   private ASPRowSet mat_st_entry_line_set;
   private ASPCommandBar mat_st_entry_line_bar;
   private ASPTable mat_st_entry_line_tbl;
   private ASPBlockLayout mat_st_entry_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatStEntry (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();     
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ENTRY_ID")) )
         okFind();
     else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT"))){
         headset.refreshRow();
         okFindITEM1();
     }
//         performRefreshParent();
     else 
        okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void performRefreshParent() 
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      if ("CREATE_FILE".equals(ctx.readValue("OPERATION"))) 
      {
         refreshHeadset(false);
      }
   }
   private void refreshHeadset(boolean refresh_one) 
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      int row_no = headset.getCurrentRowNo();

      ASPQuery q = trans.addEmptyQuery(headblk);
      
      q.addOrCondition(headset.getRows("SEQ_NO"));
      q.addWhereCondition("MAT_ENTRY_TYPE = 'List'");
      q.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);
      headset.goTo(row_no);
      if (refresh_one)
         refreshITEM1();
   }
   public void refreshITEM1()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
          return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(mat_st_entry_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ENTRY_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ENTRY_ID", headset.getValue("ENTRY_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_st_entry_line_blk);
      headset.goTo(headrowno);
   }
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
      refreshITEM1();
   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.addWhereCondition("MAT_ENTRY_TYPE = 'List'");
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("MATSTENTRYNODATA: No data found.");
         headset.clear();
      }
      eval( mat_st_entry_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","MAT_ST_ENTRY_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("STATUS", "已创建");
      cmd.setParameter("MAT_ENTRY_TYPE", "List");
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

      q = trans.addQuery(mat_st_entry_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND ENTRY_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("ENTRY_ID", headset.getValue("ENTRY_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_st_entry_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","MAT_ST_ENTRY_LINE_API.New__",mat_st_entry_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_ENTRY_ID", headset.getValue("ENTRY_ID"));
      cmd.setParameter("ITEM0_CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      mat_st_entry_line_set.addRow(data);
   }
   
   
   

   public void validate()
  {
     ASPManager mgr=getASPManager();
     ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
     ASPCommand cmd;
     ASPTransactionBuffer trans0=mgr.newASPTransactionBuffer();
     ASPCommand cmd0;
     String str=mgr.readValue("VALIDATE");
   
     if ("LINE_NO".equals(str)) {
        cmd = trans.addCustomFunction("MAT_NO","MAT_ACCEPT_LINE_API.GET_MAT_NO","MAT_NO");
        cmd.addParameter("PROJ_NO");
        cmd.addParameter("ACCEPT_ID");
        cmd.addParameter("LINE_NO");

        trans = mgr.validate(trans);
        String MAT_NO     = trans.getValue("MAT_NO/DATA/MAT_NO"            );
        
        cmd0 = trans0.addCustomFunction("MAT_NAME","MAT_CODE_API.GET_MAT_NAME","MAT_NAME");
        cmd0.addParameter("PROJ_NO");
        cmd0.addParameter("MAT_NO",MAT_NO);
        cmd0 = trans0.addCustomFunction("MANUFACTORY","MAT_CODE_API.GET_MANUFACTORY","MANUFACTORY");
        cmd0.addParameter("PROJ_NO");
        cmd0.addParameter("MAT_NO",MAT_NO);
        cmd0 = trans0.addCustomFunction("ACCEPT_QTY","MAT_ACCEPT_LINE_API.GET_ACCEPT_QTY","ACCEPT_QTY");
        cmd0.addParameter("PROJ_NO");
        cmd0.addParameter("ACCEPT_ID");
        cmd0.addParameter("LINE_NO");
        cmd0 = trans0.addCustomFunction("UNIT_NO","MAT_ACCEPT_LINE_API.GET_UNIT_NO","UNIT_NO");
        cmd0.addParameter("PROJ_NO");
        cmd0.addParameter("ACCEPT_ID");
        cmd0.addParameter("LINE_NO");

        trans0 = mgr.validate(trans0);
        String MAT_NAME     = trans0.getValue("MAT_NAME/DATA/MAT_NAME"            );
        String MANUFACTORY     = trans0.getValue("MANUFACTORY/DATA/MANUFACTORY"            );
        String ARRIVE_QTY     = trans0.getValue("ACCEPT_QTY/DATA/ACCEPT_QTY"            );
        String UNIT_NO     = trans0.getValue("UNIT_NO/DATA/UNIT_NO"            );

        MAT_NAME     = mgr.isEmpty(MAT_NAME    )? ""  : MAT_NAME    ;
        MANUFACTORY     = mgr.isEmpty(MANUFACTORY    )? ""  : MANUFACTORY    ;
        ARRIVE_QTY     = mgr.isEmpty(ARRIVE_QTY    )? ""  : ARRIVE_QTY    ;
        UNIT_NO     = mgr.isEmpty(UNIT_NO    )? ""  : UNIT_NO    ;
        
        mgr.responseWrite(MAT_NO + "^"+ MAT_NAME + "^"+ MANUFACTORY + "^"+ ARRIVE_QTY + "^"+ ARRIVE_QTY + "^"+ UNIT_NO + "^");
     }
     if ("ADMIN_ID".equals(str)) {
        cmd = trans.addCustomFunction("ADMIN_ID","MAT_STORAGE_ADMIN_API.GET_ADMIN_USER","ADMIN_ID");
        cmd.addParameter("PROJ_NO");
        cmd.addParameter("STORAGE_ID");
        cmd.addParameter("ADMIN_ID");

        trans = mgr.validate(trans);
        String MAT_NO     = trans.getValue("ADMIN_ID/DATA/ADMIN_ID"            );
        
        cmd0 = trans0.addCustomFunction("ADMIN_NAME","PERSON_INFO_API.GET_NAME","ADMIN_NAME");
        cmd0.addParameter("ADMIN_ID",MAT_NO);

        trans0 = mgr.validate(trans0);
        String UNIT_NO     = trans0.getValue("ADMIN_NAME/DATA/ADMIN_NAME"            );
        
        UNIT_NO     = mgr.isEmpty(UNIT_NO    )? ""  : UNIT_NO    ;
        
        mgr.responseWrite(UNIT_NO + "^");
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
      headblk.addField("ENTRY_ID").
              setMandatory().
              setInsertable().
              setLabel("MATSTENTRYENTRYID: Entry Id").
              setSize(20);
      headblk.addField("PROJ_NO").
              setMandatory().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().
              setLabel("MATSTENTRYPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("MATSTENTRYPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("STATUS").
//              setInsertable().
              setReadOnly().
              setLabel("MATSTENTRYSTATUS: Status").
              setSize(10);
      headblk.addField("MAT_ENTRY_TYPE").
              enumerateValues("Mat_Entry_Type_API").
              setSelectBox().
              setInsertable().
              setHidden().
              setLabel("MATSTENTRYMATENTRYTYPE: Mat Entry Type").
              setSize(20);
//      headblk.addField("INVOICE_NO").
//              setInsertable().
//              setLabel("MATSTENTRYINVOICENO: Invoice No").
//              setSize(20);
      headblk.addField("CONTRACT_ID").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLOVProperty("WHERE", "CLASS_NO IN ('SB','WZ')").
              setLabel("MATSTENTRYCONTRACTID: Contract Id").
              setSize(20);
      headblk.addField("CONTRACT_DESC").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC ( :PROJ_NO,:CONTRACT_ID)").
              setLabel("MATSTENTRYCONTRACTDESC: Contract Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");
      headblk.addField("STORAGE_ID").
              setInsertable().
              setDefaultNotVisible().
              setMandatory().
              setDynamicLOV("MAT_STOWAGE","PROJ_NO").
              setLabel("MATSTENTRYSTORAGEID: Storage Id").
              setSize(20);
      headblk.addField("STORAGE_DESC").
              setFunction("MAT_STOWAGE_API.GET_STORAGE_DESC ( :PROJ_NO,:STORAGE_ID)").
              setLabel("MATSTENTRYSTORAGEDESC: Storage Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("STORAGE_ID").setValidation("STORAGE_DESC");
      headblk.addField("ADMIN_ID").
              setInsertable().
              setDefaultNotVisible().
//              setDynamicLOV("PERSON_INFO").
              setDynamicLOV("MAT_STORAGE_ADMIN_LOV","PROJ_NO,STORAGE_ID").
              setLabel("MATSTENTRYADMINID: Admin Id").
              setCustomValidation("PROJ_NO,STORAGE_ID,ADMIN_ID", "ADMIN_NAME").
              setSize(20);
      headblk.addField("ADMIN_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( MAT_STORAGE_ADMIN_API.GET_ADMIN_USER( :PROJ_NO, :STORAGE_ID, :ADMIN_ID))").
//              setFunction("PERSON_INFO_API.GET_NAME (:ADMIN_ID)").
              setLabel("MATSTENTRYADMINNAME: Admin Name").
              setDefaultNotVisible().
              setReadOnly().
              setSize(30);
//      mgr.getASPField("ADMIN_ID").setValidation("ADMIN_NAME");
      
//      headblk.addField("MAT_TYPE_ID").
//              setInsertable().
//              setLabel("MATSTENTRYMATTYPEID: Mat Type Id").
//              setSize(50);

      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("MATSTENTRYCREATETIME: Create Time").
              setSize(20);

      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("PERSON_INFO").
              setLabel("MATARRIVECREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setLabel("MATARRIVEPERSONINFONAME: Person Name").
              setDefaultNotVisible().
              setReadOnly().
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("PERSON_NAME");
      headblk.setView("MAT_ST_ENTRY");
      headblk.defineCommand("MAT_ST_ENTRY_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATSTENTRYTBLHEAD: Mat St Entrys");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.addCustomCommand("printReport", "MATSTENTRYPRINTREPORT: Print Report...");
      headbar.addCustomCommand("printReport2", "MATSTENTRYPRINTREPORT2: Print Report2...");
      headbar.addCustomCommand("printReport3", "MATSTENTRYPRINTREPORT3: Print Report3...");

      headbar.addCustomCommand("matEntry", "确认入库","MAT_ST_ENTRY_API.Mat_St_Entry___");
      headbar.addCommandValidConditions("matEntry", "STATUS", "Disable", "已入库");
      headbar.addSecureCustomCommand("CreateFile", "DOCCTWREVOKECREATEFILE: Create File...", "MAT_ST_ENTRY_LINE_API.CREATE_FILE");
      headbar.addCommandValidConditions("CreateFile", "STATUS", "Disable", "已入库");
      
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setSimple("STORAGE_DESC");
      headlay.setSimple("ADMIN_NAME");
      headlay.setSimple("PERSON_NAME");
 


      mat_st_entry_line_blk = mgr.newASPBlock("ITEM1");
      mat_st_entry_line_blk.addField("ITEM0_OBJID").
                            setHidden().
                            setDbName("OBJID");
      mat_st_entry_line_blk.addField("ITEM0_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
      mat_st_entry_line_blk.addField("ITEM0_PROJ_NO").
                            setDbName("PROJ_NO").
                            setMandatory().
                            setInsertable().
                            setHidden().
                            setLabel("MATSTENTRYLINEITEM0PROJNO: Proj No").
                            setSize(50);
      mat_st_entry_line_blk.addField("ITEM0_ENTRY_ID").
                            setDbName("ENTRY_ID").
                            setHidden().
                            setMandatory().
                            setInsertable().
                            setLabel("MATSTENTRYLINEITEM0ENTRYID: Entry Id").
                            setSize(50);
      mat_st_entry_line_blk.addField("ENTRY_LINE_NO").
//                            setMandatory().
                            setReadOnly().
//                            setInsertable().
                            setLabel("MATSTENTRYLINEENTRYLINENO: Entry Line No").
                            setSize(20);
      mat_st_entry_line_blk.addField("ACCEPT_ID").
                            setInsertable().
                            setDynamicLOV("MAT_ACCEPT","PROJ_NO,CONTRACT_ID").
                            setLOVProperty("WHERE", "STATUS='已验收'").
                            setLabel("MATSTENTRYLINEACCEPTID: Accept Id").
                            setSize(20);
      mat_st_entry_line_blk.addField("LINE_NO").
                            setInsertable().
                            setDynamicLOV("MAT_ACCEPT_LINE_LEFT","PROJ_NO,ACCEPT_ID").
                            setLOVProperty("WHERE", "accept_qty > entry_qty").
                            setLabel("MATSTENTRYLINELINENO: Line No").
                            setCustomValidation("PROJ_NO,ACCEPT_ID,LINE_NO", "MAT_NO,MAT_NAME,MANUFACTORY,ACCEPT_QTY,ENTRY_QTY,UNIT_NO").
                            setSize(20);

      mat_st_entry_line_blk.addField("MAT_NO").
                            setReadOnly().
                            setLabel("MATARRIVELINEMATNO: Mat No").
                            setFunction("MAT_ACCEPT_LINE_API.GET_MAT_NO ( :PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                            setSize(20);
      mat_st_entry_line_blk.addField("MAT_NAME").  
                            setReadOnly().
                            setLabel("MATARRIVELINEMATNAME: Mat Name").
                            setFunction("MAT_CODE_API.GET_MAT_NAME(:PROJ_NO,MAT_ACCEPT_LINE_API.GET_MAT_NO ( :PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                            setSize(20);
      mat_st_entry_line_blk.addField("MANUFACTORY").  
                            setReadOnly().
                            setLabel("MATARRIVELINEMANUFACTORY: Manufactory").
                            setFunction("MAT_CODE_API.GET_MANUFACTORY(:PROJ_NO,MAT_ACCEPT_LINE_API.GET_MAT_NO ( :PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                            setSize(30);
      
      mat_st_entry_line_blk.addField("ACCEPT_QTY","Number").
                            setReadOnly().
                            setLabel("MATACCEPTLINEACCEPTQTY: Accept Qty").
                            setFunction("MAT_ACCEPT_LINE_API.GET_ACCEPT_QTY ( :PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                            setSize(20);
      mat_st_entry_line_blk.addField("ENTRY_QTY","Number").
                            setInsertable().
                            setLabel("MATSTENTRYLINEENTRYQTY: Entry Qty").
                            setSize(20);
//      mat_st_entry_line_blk.addField("NO_TAX_PRICE","Number").
//                            setInsertable().
//                            setLabel("MATSTENTRYLINENOTAXPRICE: No Tax Price").
//                            setSize(30);
//      mat_st_entry_line_blk.addField("ENTRY_PRICE","Number").
//                            setInsertable().
//                            setLabel("MATSTENTRYLINEENTRYPRICE: Entry Price").
//                            setSize(30);
//      mat_st_entry_line_blk.addField("TAX_RATE","Number").
//                            setInsertable().
//                            setLabel("MATSTENTRYLINETAXRATE: Tax Rate").
//                            setSize(30);
      mat_st_entry_line_blk.addField("UNIT_NO").
                            setInsertable().
                            setDynamicLOV("ISO_UNIT").
                            setLabel("MATSTENTRYLINEUNITNO: Unit No").
                            setSize(20);
      mat_st_entry_line_blk.addField("ITEM0_CONTRACT_ID").
                            setDbName("CONTRACT_ID").
                            setHidden().
                            setLabel("MATSTENTRYLINEITEM0CONTRACTID: Contract Id").
                            setSize(20);
//      mat_st_entry_line_blk.addField("ITEM_NO").
//                            setInsertable().
//                            setLabel("MATSTENTRYLINEITEMNO: No").
//                            setSize(50);

//      mat_st_entry_line_blk.addField("STOCK").
//                            setInsertable().
//                            setLabel("MATSTENTRYLINESTOCK: Stock").
//                            setSize(100);
      
      mat_st_entry_line_blk.addField("ITEM0_CREATE_TIME","Date").
                            setDbName("CREATE_TIME").
                            setInsertable().
                            setLabel("MATSTENTRYLINEITEM0CREATETIME: Create Time").
                            setSize(20);
      mat_st_entry_line_blk.addField("ITEM0_CREATE_PERSON").
                            setInsertable().
                            setDbName("CREATE_PERSON").
                            setDynamicLOV("PERSON_INFO").
                            setLabel("MATSTENTRYLINECREATEPERSON: Create Person").
                            setSize(20);
      mat_st_entry_line_blk.addField("ITEM0_PERSON_NAME").
                            setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
                            setLabel("MATACCEPTLINEITEM0PERSONNAME: Person Name").
                            setReadOnly().
                            setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("ITEM0_PERSON_NAME");
      mat_st_entry_line_blk.setView("MAT_ST_ENTRY_LINE");
      mat_st_entry_line_blk.defineCommand("MAT_ST_ENTRY_LINE_API","New__,Modify__,Remove__");
      mat_st_entry_line_blk.setMasterBlock(headblk);
      mat_st_entry_line_set = mat_st_entry_line_blk.getASPRowSet();
      mat_st_entry_line_bar = mgr.newASPCommandBar(mat_st_entry_line_blk);
      mat_st_entry_line_bar.defineCommand(mat_st_entry_line_bar.OKFIND, "okFindITEM1");
      mat_st_entry_line_bar.defineCommand(mat_st_entry_line_bar.NEWROW, "newRowITEM1");
      mat_st_entry_line_tbl = mgr.newASPTable(mat_st_entry_line_blk);
      mat_st_entry_line_tbl.setTitle("MATSTENTRYLINEITEMHEAD1: MatStEntryLine");
      mat_st_entry_line_tbl.enableRowSelect();
      mat_st_entry_line_tbl.setWrap();
      mat_st_entry_line_lay = mat_st_entry_line_blk.getASPBlockLayout();
      mat_st_entry_line_lay.setDefaultLayoutMode(mat_st_entry_line_lay.MULTIROW_LAYOUT);

//      mat_st_entry_line_lay.setDataSpan("STOCK", 5);

      mat_st_entry_line_lay.setSimple("ITEM0_PERSON_NAME");
      mat_st_entry_line_lay.setSimple("MAT_NAME");



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
             String entry_id = headset.getValue("ENTRY_ID");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatStEntry.raq&proj_no="+proj_no+"&entry_id="+entry_id
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
               String entry_id = headset.getValue("ENTRY_ID");
                appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatStEntry2.raq&proj_no="+proj_no+"&entry_id="+entry_id
                  + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
           }
     }   
   public void  printReport3() throws FndException, UnsupportedEncodingException
   {
      ASPManager mgr = getASPManager();
      ASPConfig cfg = getASPConfig();
      String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      if (headset.countRows()>0 )
            {   
               String proj_no = headset.getValue("PROJ_NO");
               String entry_id = headset.getValue("ENTRY_ID");
                appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatStEntry3.raq&proj_no="+proj_no+"&entry_id="+entry_id
                  + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
           }
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
      
      ASPBuffer selected_fields = headset.getSelectedRows("PROJ_NO,ENTRY_ID,CONTRACT_ID");

      callNewWindow("MatStEntryLineDlg.page", selected_fields);
   }

   public void matEntry(){
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      cmd = trans.addCustomCommand("PROJ_NO,ENTRY_ID","MAT_ST_ENTRY_API.Mat_St_Entry___");
      cmd.setParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ENTRY_ID", headset.getValue("ENTRY_ID"));
      trans = mgr.perform(trans);
      cmd.clear();
      okFind();
   }

   public void  adjust()
   {
      // fill function body

      if(headset.countRows() > 0&&headlay.isMultirowLayout()){
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DELETE);}
      else if(headset.countRows() > 0&&headlay.isSingleLayout()&&"已入库".equals(headset.getValue("STATUS"))){
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DELETE);
         mat_st_entry_line_bar.disableCommand(mat_st_entry_line_bar.NEWROW);
         mat_st_entry_line_bar.disableCommand(mat_st_entry_line_bar.EDITROW);
         mat_st_entry_line_bar.disableCommand(mat_st_entry_line_bar.DELETE);
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "MATSTENTRYDESC: Mat St Entry";
   }


   protected String getTitle()
   {
      return "MATSTENTRYTITLE: Mat St Entry";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      printHiddenField("REFRESH_PARENT", "FALSE");
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (mat_st_entry_line_lay.isVisible())
          appendToHTML(mat_st_entry_line_lay.show());

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

   }
}
