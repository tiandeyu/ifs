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

package ifs.engmaw;
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

public class DrawingBorrow extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.engmaw.DrawingBorrow");

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

   private ASPBlock drawing_borrow_line_blk;
   private ASPRowSet drawing_borrow_line_set;
   private ASPCommandBar drawing_borrow_line_bar;          
   private ASPTable drawing_borrow_line_tbl;
   private ASPBlockLayout drawing_borrow_line_lay;

            
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  DrawingBorrow (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("BORROW_NO")) )
         okFind();
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         okFindITEM1();   
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
         mgr.showAlert("DRAWINGBORROWNODATA: No data found.");
         headset.clear();
      }
      eval( drawing_borrow_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","DRAWING_BORROW_API.New__",headblk);
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

      q = trans.addQuery(drawing_borrow_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND BORROW_NO = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("BORROW_NO", headset.getValue("BORROW_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,drawing_borrow_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","DRAWING_BORROW_LINE_API.New__",drawing_borrow_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_BORROW_NO", headset.getValue("BORROW_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      drawing_borrow_line_set.addRow(data);
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
   public void  report()
   {

      performHEAD( "Report__" );
   }     
   public void  close()
   {

      performHEAD( "Close__" );
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
      headblk.addField("OBJSTATE").
              setHidden();
      headblk.addField("OBJEVENTS").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").      
              setLabel("DRAWINGBORROWPROJNO: Proj No").
              setSize(30);     
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("DRAWINGBORROWGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30);            
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("BORROW_NO").
              setMandatory().
              setInsertable().
              setLabel("DRAWINGBORROWBORROWNO: Borrow No").
              setSize(30);
      headblk.addField("BORROW_PERSON").
              setInsertable().  
              setDynamicLOV("PERSON_INFO").      
              setLabel("DRAWINGBORROWBORROWPERSON: Borrow Person").
              setSize(30);                            
      headblk.addField("BORROW_PERSON_NAME").
              setReadOnly().    
              setLabel("DRAWINGBORROWBORROWPERSONNAME: Borrow Person Name").
              setFunction("PERSON_INFO_API.Get_Name(:BORROW_PERSON)").
              setSize(30);        
      mgr.getASPField("BORROW_PERSON").setValidation("BORROW_PERSON_NAME");
      headblk.addField("BORROW_DATE","Date").
              setInsertable().
              setLabel("DRAWINGBORROWBORROWDATE: Borrow Date").
              setSize(30);
      headblk.addField("REPORT_PERSON").
              setReadOnly().
              setLabel("DRAWINGBORROWREPORTPERSON: Report Person").
              setSize(30);
      headblk.addField("REPORT_PERSON_NAME").
              setReadOnly().       
              setLabel("DRAWINGBORROWREPORTPERSONNAME: Report Person Name").
              setFunction("PERSON_INFO_API.Get_Name(:REPORT_PERSON)").
              setSize(30);      
      mgr.getASPField("REPORT_PERSON").setValidation("REPORT_PERSON_NAME");
      headblk.addField("REPORT_DATE","Date").
//              setInsertable().
              setReadOnly().    
              setLabel("DRAWINGBORROWREPORTDATE: Report Date").
              setSize(30);
      headblk.addField("SUPPLIER_ID").
              setInsertable().
              setDynamicLOV("SUPPLIER_INFO").
              setLabel("DRAWINGBORROWSUPPLIERID: Supplier Id").
              setSize(30);
      headblk.addField("SUPPLIER_NAME").
              setReadOnly().
              setFunction("SUPPLIER_INFO_API.Get_Name (:SUPPLIER_ID)").
              setSize(20);
      mgr.getASPField("SUPPLIER_ID").setValidation("SUPPLIER_NAME");
      headblk.addField("STATE").     
              setLabel("DRAWINGBORROWSTATE: State").
              setSize(30);
      headblk.addField("NOTE").
              setInsertable().
              setLabel("DRAWINGBORROWNOTE: Note").
              setSize(140).
              setHeight(6);    
      headblk.setView("DRAWING_BORROW");
      headblk.defineCommand("DRAWING_BORROW_API","New__,Modify__,Remove__,Report__,Close__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addSecureCustomCommand("create_drawing_borrow", "DRAWINGSENDCREATEDRAWINGBORROW: Create Drawing Borrow...","DRAWING_BORROW_LINE_API.Create_Drawing_Borrow_Line");
      headbar.addSecureCustomCommand("Report", "DRAWINGBORROWREPORT: Report Drawing Borrow","DRAWING_BORROW_API.Report__");
      headbar.addSecureCustomCommand("Close","DRAWINGBORROWCLOSE: Close Drawing Borrow","DRAWING_SEND_API.Close__");
//      headbar.addCustomCommand("Report","DRAWINGBORROWREPORT: Report Drawing Borrow");    
//      headbar.addCustomCommand("Close","DRAWINGBORROWCLOSE: Close Drawing Borrow");  
      headbar.addCommandValidConditions("Report",     "OBJSTATE",    "Enable",      "Planed");     
      headbar.addCommandValidConditions("create_drawing_borrow",     "OBJSTATE",    "Enable",      "Planed");  
      headbar.addCommandValidConditions("Close", "OBJSTATE",    "Enable",      "Reported");    
      headtbl = mgr.newASPTable(headblk);          
      headtbl.setTitle("DRAWINGBORROWTBLHEAD: Drawing Borrows");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("REPORT_PERSON_NAME");
      headlay.setSimple("BORROW_PERSON_NAME");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("SUPPLIER_NAME");
      headlay.setDataSpan("NOTE", 5);      

      drawing_borrow_line_blk = mgr.newASPBlock("ITEM1");
      drawing_borrow_line_blk.addField("ITEM0_OBJID").
                              setHidden().
                              setDbName("OBJID");
      drawing_borrow_line_blk.addField("ITEM0_OBJVERSION").
                              setHidden().
                              setDbName("OBJVERSION");
      drawing_borrow_line_blk.addField("ITEM0_PROJ_NO").
                              setDbName("PROJ_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0PROJNO: Proj No").
                              setSize(50);
      drawing_borrow_line_blk.addField("ITEM0_BORROW_NO").
                              setDbName("BORROW_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0BORROWNO: Borrow No").
                              setSize(200);
      drawing_borrow_line_blk.addField("BORROW_LINE_NO").
                              setMandatory().
                              setInsertable().
                              setHidden().
                              setLabel("DRAWINGBORROWLINEBORROWLINENO: Borrow Line No").
                              setSize(200);
      drawing_borrow_line_blk.addField("VOLUME_NO").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEVOLUMENO: Volume No").
                              setSize(50);
      drawing_borrow_line_blk.addField("VOLUME_DESC").
                              setReadOnly().
                              setFunction("DRAWING_LIST_API.Get_Volume_Desc(:ITEM0_PROJ_NO,:VOLUME_NO)").
                              setLabel("DRAWINGBORROWLINEVOLUMEDESC: Volume Desc").              
                              setSize(120);
      drawing_borrow_line_blk.addField("MAJOR_NO").
                              setReadOnly().
                              setFunction("DRAWING_LIST_API.Get_Major_No(:ITEM0_PROJ_NO,:VOLUME_NO)").
                              setLabel("DRAWINGBORROWLINEMAJORNO: Major No").
                              setSize(20);  
      drawing_borrow_line_blk.addField("MAJOR_DESC").
                              setLabel("DRAWINGBORROWLINEMAJORDESC: Major Desc").
                              setReadOnly().
                              setFunction("VOLUME_MAJOR_API.GET_MAJOR_DESC (DRAWING_LIST_API.Get_Major_No(:ITEM0_PROJ_NO,:VOLUME_NO))").
                              setSize(20);    
      drawing_borrow_line_blk.addField("COPIES","Number").
                              setFunction("DRAWING_LIST_API.Get_Copies(:ITEM0_PROJ_NO,:VOLUME_NO)").
                              setReadOnly().
                              setLabel("DRAWINGBORROWLINECOPIES: Copies").
                              setSize(20);          
      drawing_borrow_line_blk.addField("BORROW__QTY","Number").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEBORROWQTY: Borrow  Qty").
                              setSize(30);
      drawing_borrow_line_blk.addField("PLAN_BACK_DATA","Date").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEPLANBACKDATA: Plan Back Data").
                              setSize(30);
      drawing_borrow_line_blk.addField("ITEM0_NOTE").
                              setDbName("NOTE").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0NOTE: Note").
                              setHeight(6).
                              setSize(140);      
      drawing_borrow_line_blk.setView("DRAWING_BORROW_LINE");
      drawing_borrow_line_blk.defineCommand("DRAWING_BORROW_LINE_API","Modify__,Remove__");
      drawing_borrow_line_blk.setMasterBlock(headblk);        
      drawing_borrow_line_set = drawing_borrow_line_blk.getASPRowSet();
      drawing_borrow_line_bar = mgr.newASPCommandBar(drawing_borrow_line_blk);
      drawing_borrow_line_bar.defineCommand(drawing_borrow_line_bar.OKFIND, "okFindITEM1");
      drawing_borrow_line_bar.defineCommand(drawing_borrow_line_bar.NEWROW, "newRowITEM1");
      drawing_borrow_line_tbl = mgr.newASPTable(drawing_borrow_line_blk);
      drawing_borrow_line_tbl.setTitle("DRAWINGBORROWLINEITEMHEAD1: DrawingBorrowLine");
      drawing_borrow_line_tbl.enableRowSelect();
      drawing_borrow_line_tbl.setWrap();
      drawing_borrow_line_lay = drawing_borrow_line_blk.getASPBlockLayout();
      drawing_borrow_line_lay.setDefaultLayoutMode(drawing_borrow_line_lay.MULTIROW_LAYOUT);
      drawing_borrow_line_lay.setDataSpan("ITEM0_NOTE", 5);
      drawing_borrow_line_lay.setDataSpan("PLAN_BACK_DATA", 5);  
   }



   public void  adjust()
   {
      // fill function body
      if(headlay.isSingleLayout()&&headset.countRows()>0){
         String state = headset.getValue("OBJSTATE");
         if( headlay.isSingleLayout() && ("Closed".equals(state) || "Checked".equals(state))){ 
               headbar.disableCommand(headbar.DELETE);
               headbar.disableCommand(headbar.EDITROW);   
               drawing_borrow_line_bar.disableCommand( drawing_borrow_line_bar.DELETE );
               drawing_borrow_line_bar.disableCommand( drawing_borrow_line_bar.EDITROW );  
         }    
     }
   }
   
   public void create_drawing_borrow() throws FndException{
      ASPManager mgr = getASPManager();       
      ASPCommand cmd = mgr.newASPCommand(); 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      headset.storeSelections();
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("PROJ_NO,BORROW_NO");
      callNewWindow("DrawingListBorrowDlg.page", selected_fields);         
   }      
         
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
       ASPManager mgr = getASPManager();
       ASPContext ctx = mgr.getASPContext();
       String proj_no = buff.getBufferAt(0).getValueAt(0);
       String borrow_no= buff.getBufferAt(0).getValueAt(1);   
       ctx.setGlobal("DRAWINGBOWRROWLISTDLGPROJNO", proj_no);
       ctx.setGlobal("DRAWINGBOWRROWLISTDLGID", borrow_no);  
      String url = transfer_page+"?USERCOMMAND=submitTree";     
//      String url = transfer_page+"?PROJ_NO="+project_no+"&PLAN_NO="+plan_no+"&PROJECT_TYPE_NO="+id;
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }  

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "DRAWINGBORROWDESC: Drawing Borrow";
   }


   protected String getTitle()
   {
      return "DRAWINGBORROWTITLE: Drawing Borrow";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      printHiddenField("REFRESH_PARENT", "FALSE");
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      if (drawing_borrow_line_lay.isVisible())
          appendToHTML(drawing_borrow_line_lay.show());
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");  
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

   }
}