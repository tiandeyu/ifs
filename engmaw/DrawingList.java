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
import ifs.genbaw.GenbawConstants;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class DrawingList extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.engmaw.DrawingList");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock drawing_send_line_blk;
   private ASPRowSet drawing_send_line_set;
   private ASPCommandBar drawing_send_line_bar;
   private ASPTable drawing_send_line_tbl;
   private ASPBlockLayout drawing_send_line_lay;  
   
   private ASPBlock drawing_borrow_line_blk;
   private ASPRowSet drawing_borrow_line_set;
   private ASPCommandBar drawing_borrow_line_bar;          
   private ASPTable drawing_borrow_line_tbl;
   private ASPBlockLayout drawing_borrow_line_lay;
   
   private ASPBlock drawing_back_line_blk;
   private ASPRowSet drawing_back_line_set;
   private ASPCommandBar drawing_back_line_bar;
   private ASPTable drawing_back_line_tbl;
   private ASPBlockLayout drawing_back_line_lay;
   
   private ASPTabContainer tabs;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  DrawingList (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   protected String getDefProj() {
      ASPManager mgr = getASPManager();
      ASPContext ctx =mgr.getASPContext();
      return ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT);
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VOLUME_NO")) )
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
         mgr.showAlert("DRAWINGLISTNODATA: No data found.");
         headset.clear();
      }
      eval( drawing_send_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","DRAWING_LIST_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("PROJ_NO", this.getDefProj());  
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(drawing_send_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND VOLUME_NO = ? AND OBJSTATE = 'Sended'");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("VOLUME_NO", headset.getValue("VOLUME_NO"));  
      q.includeMeta("ALL");       
      headrowno = headset.getCurrentRowNo();  
      mgr.querySubmit(trans,drawing_send_line_blk);
      headset.goTo(headrowno);
   }

   
   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(drawing_borrow_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND VOLUME_NO = ? ");    
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));  
      q.addParameter("VOLUME_NO", headset.getValue("VOLUME_NO"));        
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,drawing_borrow_line_blk);
      headset.goTo(headrowno);
   }  
   
   public void okFindITEM3()
   {  
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;      

      q = trans.addQuery(drawing_back_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND VOLUME_NO = ? AND OBJSTATE = 'Closed' ");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("VOLUME_NO", headset.getValue("VOLUME_NO"));
      q.includeMeta("ALL");      
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,drawing_back_line_blk);
      headset.goTo(headrowno);
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
              setInsertable().
              setDefaultNotVisible().
              setMandatory().  
              setLabel("DRAWINGLISTPROJECTNO: Project No").
              setDynamicLOV("GENERAL_PROJECT").
              setSize(20);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("DRAWINGLISTPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("VOLUME_NO").
              setMandatory().
              setInsertable().
              setLabel("DRAWINGLISTVOLUMENO: Volume No").
              setSize(20);
      headblk.addField("VOLUME_DESC").
              setInsertable().
              setLabel("DRAWINGLISTVOLUMEDESC: Volume Desc").              
              setSize(120);  
      
      headblk.addField("VOLUME_SOURCE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTVOLUMESOURCE: Volume Source").
              setDynamicLOV("VOLUME_SOURCE").
              setSize(20);
      headblk.addField("VOLUME_SOURCE_N").
              setReadOnly().
              setLabel("DRAWINGLISTVOLUMESOURCE: Volume Source").
              setFunction("SUPPLIER_INFO_API.Get_Name (:VOLUME_SOURCE)").
              setSize(20);
      mgr.getASPField("VOLUME_SOURCE").setValidation("VOLUME_SOURCE_N");
      
      headblk.addField("STATUS_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("VOLUME_STATUS").
              setLabel("DRAWINGLISTSTATUSNO: Status No").
              setSize(20);
      headblk.addField("STATUS_DESC").
              setReadOnly().
              setLabel("DRAWINGLISTSTATUSNO: Status No").
              setFunction("VOLUME_STATUS_api.Get_Status_Desc (:STATUS_NO)").
              setSize(20);
      mgr.getASPField("STATUS_NO").setValidation("STATUS_DESC");
      
      headblk.addField("TYPE_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("VOLUME_TYPE").
              setLabel("DRAWINGLISTTYPENO: Type No").
              setSize(20);
      headblk.addField("TYPE_DESC").
              setReadOnly().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTTYPENO: Type No").
              setFunction("VOLUME_TYPE_api.Get_Type_Desc (:TYPE_NO)").
              setSize(20);
      mgr.getASPField("TYPE_NO").setValidation("TYPE_DESC");
      
      headblk.addField("MAC_GRP_NO").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTMACGRPNO: Mac Grp No").
              setDynamicLOV("GENERAL_MACH_GROUP").
              setSize(20);
      headblk.addField("MAC_GRP_DESC").
              setFunction("General_Mach_Group_Api.Get_Mach_Grp_Desc(:MAC_GRP_NO)").
              setDefaultNotVisible().
              setReadOnly().
              setSize(20); 
      mgr.getASPField("MAC_GRP_NO").setValidation("MAC_GRP_DESC");
      
      headblk.addField("MAJOR_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("VOLUME_MAJOR").
              setLabel("DRAWINGLISTMAJORNO: Major No").
              setSize(20);
      headblk.addField("MAJOR_DESC").
              setReadOnly().
              setLabel("DRAWINGLISTMAJORNO: Major No").
              setFunction("VOLUME_MAJOR_API.GET_MAJOR_DESC (:MAJOR_NO)").
              setSize(20);
      mgr.getASPField("MAJOR_NO").setValidation("MAJOR_DESC");
      
      headblk.addField("COPIES","Number").
              setInsertable().
              setLabel("DRAWINGLISTCOPIES: Copies").
              setSize(20);
      headblk.addField("CONTRACT_COPIES","Number").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTCONTRACTCOPIES: Contract Copies").
              setSize(20);
      headblk.addField("SEND_QTY_TOTAL","Number").
              setReadOnly().
              setFunction("DRAWING_SEND_LINE_API.Cal_All_Send_Qty(:PROJ_NO,:VOLUME_NO)").
              setLabel("DRAWINGLISTSENDQTYTOTAL: Send Qty Total").
              setSize(30);
      headblk.addField("BORROW_QTY_TOTAL","Number").
              setReadOnly().
              setDefaultNotVisible().
              setFunction("DRAWING_BACK_LINE_API.Cal_All_Borrow_Back_Qty(:PROJ_NO,:VOLUME_NO)").
              setLabel("DRAWINGLISTBORROWQTYTOTAL: Borrow Qty Total").
              setSize(30);   
      headblk.addField("PLAN_ARRIVE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTPLANARRIVETIME: Plan Arrive Time").
              setSize(20);
      headblk.addField("ACTUAL_ARRIVAL_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTACTUALARRIVALTIME: Actual Arrival Time").
              setSize(20);
      headblk.addField("REQUIRE_TIME","Date").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTREQUIRETIME: Require Time").
              setSize(20);
      headblk.addField("WBS_NO").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTWBSNO: Wbs No").
              setSize(20);
      headblk.addField("REV").
              setInsertable().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTREV: Rev").
              setSize(20); 
      headblk.addField("CREATE_PERSON").
              setReadOnly().
              setDefaultNotVisible().
              setLabel("DRAWINGLISTCREATEPERSON: Create Person").
              setSize(20);    
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setSize(20).
              setDefaultNotVisible().
              setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setReadOnly().    
              setDefaultNotVisible().
              setLabel("DRAWINGLISTCREATETIME: Create Time").
              setSize(20);
      headblk.addField("NOTE").    
              setInsertable().
              setDefaultNotVisible().
              setHeight(6).
              setLabel("DRAWINGLISTNOTE: Note").
              setSize(120);    
     
      headblk.setView("DRAWING_LIST");
      headblk.defineCommand("DRAWING_LIST_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateDrawingSend", "Drawing Send...");  
      headbar.addCustomCommand("activateDrawingBorrow", "Drawing Borrow...");  
      headbar.addCustomCommand("activateDrawingback", "Drawing Back...");  
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DRAWINGLISTTBLHEAD: Drawing Lists");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("VOLUME_SOURCE_N");
      headlay.setSimple("STATUS_DESC");
      headlay.setSimple("TYPE_DESC");
      headlay.setSimple("MAC_GRP_DESC");
      headlay.setSimple("MAJOR_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setDataSpan("NOTE", 5);
      headlay.setDataSpan("VOLUME_DESC", 5);  
 
      
      drawing_send_line_blk = mgr.newASPBlock("ITEM1");
      drawing_send_line_blk.addField("ITEM0_OBJID").
                            setHidden().
                            setDbName("OBJID");
      drawing_send_line_blk.addField("ITEM0_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
      drawing_send_line_blk.addField("ITEM0_PROJ_NO").
                            setDbName("PROJ_NO").
                            setHidden().
                            setMandatory().
                            setInsertable().
                            setLabel("DRAWINGSENDLINEITEM0PROJNO: Proj No").
                            setSize(50);
      drawing_send_line_blk.addField("ITEM0_ID").
                            setDbName("ID").
                            setHidden().
                            setMandatory().
                            setInsertable().
                            setLabel("DRAWINGSENDLINEITEM0ID: Id").
                            setSize(200);
      drawing_send_line_blk.addField("SEND_LINE_NO").
                            setMandatory().
                            setHidden().  
                            setInsertable().  
                            setLabel("DRAWINGSENDLINESENDLINENO: Send Line No").
                            setSize(200);
      drawing_send_line_blk.addField("ITEM0_VOLUME_NO").
                            setInsertable().
                            setHidden().
                            setDbName("VOLUME_NO").  
                            setDynamicLOV("DRAWING_LIST_LOV").
                            setLabel("DRAWINGSENDLINEVOLUMENO: Volume No").
                            setSize(50);
      drawing_send_line_blk.addField("SPECIALIST_ENGINEER").
                            setInsertable().
                            setLabel("DRAWINGSENDLINESPECIALISTENGINEER: Specialist Engineer").
                            setSize(30); 
      drawing_send_line_blk.addField("SPECIALIST_ENGINEER_DESC").
                            setFunction("PERSON_INFO_API.Get_Name(:SPECIALIST_ENGINEER)").
                            setLabel("DRAWINGSENDLINESPECIALISTENGINEERDESC: Specialist Engineer Desc").
                            setSize(30);      
      drawing_send_line_blk.addField("SEND_QTY","Number").
                            setInsertable().
                            setLabel("DRAWINGSENDLINESENDQTY: Send Qty").
                            setSize(30);  
      drawing_send_line_blk.addField("SEND_DATE","Date").
                            setInsertable().
                            setLabel("DRAWINGSENDLINESENDDATE: Send Date").
                              setSize(30);      
      drawing_send_line_blk.addField("SEND_PERSON").
                            setInsertable().
                            setLabel("DRAWINGSENDLINESENDPERSON: Send Person").
                            setSize(30);   
      drawing_send_line_blk.addField("SEND_PERSON_DESC").
                            setFunction("PERSON_INFO_API.Get_Name(:SEND_PERSON)").
                            setLabel("DRAWINGSENDLINESENDPERSONDESC: Send Person Desc").
                            setSize(30);     
      drawing_send_line_blk.addField("OBJSTATE").
                            setInsertable().
                            setHidden().  
                            setLabel("DRAWINGSENDOBJSTATE: Send Objstate").
                            setSize(30);    
      drawing_send_line_blk.addField("SEND_OPTION").
                            setInsertable().        
                            setLabel("DRAWINGSENDSENDOPTION: Send Option").
                            setSize(140).
                            setHeight(6);   
      drawing_send_line_blk.addField("ITEM0_NOTE").
                            setDbName("NOTE").
                            setInsertable().
                            setLabel("DRAWINGSENDLINEITEM0NOTE: Note").
                            setSize(140).    
                            setHeight(6);  
      drawing_send_line_blk.setView("DRAWING_SEND_LINE_SUM");     
      drawing_send_line_blk.defineCommand("DRAWING_SEND_LINE_API","");
      drawing_send_line_blk.setMasterBlock(headblk);    
      drawing_send_line_set = drawing_send_line_blk.getASPRowSet();
      drawing_send_line_bar = mgr.newASPCommandBar(drawing_send_line_blk);
      drawing_send_line_bar.defineCommand(drawing_send_line_bar.OKFIND, "okFindITEM1");
      drawing_send_line_bar.defineCommand(drawing_send_line_bar.NEWROW, "newRowITEM1");
      drawing_send_line_tbl = mgr.newASPTable(drawing_send_line_blk);
      drawing_send_line_tbl.setTitle("DRAWINGSENDLINEITEMHEAD1: DrawingSendLine");
      drawing_send_line_tbl.enableRowSelect();
      drawing_send_line_tbl.setWrap();    
      drawing_send_line_lay = drawing_send_line_blk.getASPBlockLayout();
      drawing_send_line_lay.setDefaultLayoutMode(drawing_send_line_lay.MULTIROW_LAYOUT);
      drawing_send_line_lay.setDataSpan("SEND_QTY_TOTAL", 5);
      drawing_send_line_lay.setDataSpan("SEND_OPTION", 5);
      drawing_send_line_lay.setDataSpan("ITEM0_NOTE", 5); 

      
      drawing_borrow_line_blk = mgr.newASPBlock("ITEM2");
      drawing_borrow_line_blk.addField("ITEM1_OBJID").
                              setHidden().
                              setDbName("OBJID");
      drawing_borrow_line_blk.addField("ITEM1_OBJVERSION").
                              setHidden().
                              setDbName("OBJVERSION");
//      drawing_borrow_line_blk.addField("ITEM1_OBJSTATE").
//                              setDbName("OBJSTATE").
//                              setHidden();       
      drawing_borrow_line_blk.addField("ITEM1_PROJ_NO").
                              setDbName("PROJ_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0PROJNO: Proj No").
                              setSize(50);
      drawing_borrow_line_blk.addField("ITEM1_BORROW_NO").
                              setDbName("BORROW_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0BORROWNO: Borrow No").
                              setSize(200);
      drawing_borrow_line_blk.addField("BORROW_LINE_NO").
                              setMandatory().
                              setHidden().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEBORROWLINENO: Borrow Line No").
                              setSize(20);    
      
      drawing_borrow_line_blk.addField("ITEM1_SUPPLIER_ID").
                              setDbName("SUPPLIER_ID").
                              setMandatory().
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0SUPPLIERID: Supplier Id").
                              setSize(200);
      drawing_borrow_line_blk.addField("SUPPLIER_NAME").  
                              setReadOnly().
                              setLabel("DRAWINGBORROWLINEITEM0SUPPLIERNAME: Supplier Name").
                              setFunction("SUPPLIER_INFO_API.Get_Name (:ITEM1_SUPPLIER_ID)").
                              setSize(20);  
     drawing_borrow_line_blk.addField("BORROW_PERSON").
                             setMandatory().
                             setInsertable().
                             setLabel("DRAWINGBORROWLINEBORROWPERSON: Borrow Person").
                             setSize(20); 
     drawing_borrow_line_blk.addField("BORROW_PERSON_DESC").
                             setFunction("PERSON_INFO_API.Get_Name(:BORROW_PERSON)").
                             setLabel("DRAWINGSENDLINEBORROWPERSONDESC: Borrow Person Desc").
                             setSize(30);          
     drawing_borrow_line_blk.addField("BORROW_DATE","Date").
                             setMandatory().  
                             setInsertable().
                             setLabel("DRAWINGBORROWLINEBORROWDATE: Borrow Date").
                             setSize(20);  
      drawing_borrow_line_blk.addField("BORROW__QTY","Number").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEBORROWQTY: Borrow  Qty").
                              setSize(30);
      drawing_borrow_line_blk.addField("PLAN_BACK_DATA","Date").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEPLANBACKDATA: Plan Back Data").
                              setSize(30);
      drawing_borrow_line_blk.addField("ITEM1_NOTE").
                              setDbName("NOTE").
                              setInsertable().
                              setLabel("DRAWINGBORROWLINEITEM0NOTE: Note").
                              setHeight(6).
                              setSize(140);      
      drawing_borrow_line_blk.setView("DRAWING_BORROW_SUP");  
      drawing_borrow_line_blk.defineCommand("DRAWING_BORROW_LINE_API","");
      drawing_borrow_line_blk.setMasterBlock(headblk);        
      drawing_borrow_line_set = drawing_borrow_line_blk.getASPRowSet();
      drawing_borrow_line_bar = mgr.newASPCommandBar(drawing_borrow_line_blk);
      drawing_borrow_line_bar.defineCommand(drawing_borrow_line_bar.OKFIND, "okFindITEM2");
      drawing_borrow_line_tbl = mgr.newASPTable(drawing_borrow_line_blk);
      drawing_borrow_line_tbl.setTitle("DRAWINGBORROWLINEITEMHEAD1: DrawingBorrowLine");
      drawing_borrow_line_tbl.enableRowSelect();
      drawing_borrow_line_tbl.setWrap();
      drawing_borrow_line_lay = drawing_borrow_line_blk.getASPBlockLayout();
      drawing_borrow_line_lay.setDefaultLayoutMode(drawing_borrow_line_lay.MULTIROW_LAYOUT);
    
      drawing_back_line_blk = mgr.newASPBlock("ITEM3");
      drawing_back_line_blk.addField("ITEM2_OBJID").
                            setHidden().
                            setDbName("OBJID");
      drawing_back_line_blk.addField("ITEM2_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
//      drawing_back_line_blk.addField("ITEM2_OBJSTATE").
//                            setDbName("OBJSTATE").
//                            setHidden();      
      drawing_back_line_blk.addField("ITEM2_PROJ_NO").
                            setDbName("PROJ_NO").
                            setMandatory().
                            setHidden().
                            setInsertable().
                            setLabel("DRAWINGBACKLINEITEM0PROJNO: Proj No").
                            setSize(50);
      drawing_back_line_blk.addField("ITEM2_BACK_NO").
                            setDbName("BACK_NO").
                            setHidden().
                            setLabel("DRAWINGBACKLINEITEM0BACKNO: Back No").
                            setSize(20);
      drawing_back_line_blk.addField("BACK_LINE_NO").
                            setMandatory().
                            setInsertable().
                            setHidden().
                            setLabel("DRAWINGBACKLINEBACKLINENO: Back Line No").
                            setSize(20);
      drawing_back_line_blk.addField("ITEM2_BORROW_NO").
                            setDbName("BORROW_NO").
                            setReadOnly().
                            setHidden().
                            setLabel("DRAWINGBACKLINEBORROWNO: Borrow No").
                            setSize(30);
      drawing_back_line_blk.addField("ITEM2_BORROW_LINE_NO").
                            setReadOnly().
                            setHidden().
                            setDbName("BORROW_LINE_NO").
                            setLabel("DRAWINGBACKLINEBORROWLINENO: Borrow Line No").
                            setSize(30);
      drawing_back_line_blk.addField("ITEM2_VOLUME_NO").
                            setDbName("VOLUME_NO").
                            setReadOnly().      
                            setHidden().
                            setLabel("DRAWINGBORROWLINEVOLUMENO: Volume No").
                            setSize(30);      
      drawing_back_line_blk.addField("ITEM2_SUPPLIER_ID").
                            setDbName("SUPPLIER_ID").
                            setReadOnly().           
                            setLabel("DRAWINGBORROWLINESUPPLIERID: Supplier Id").
                            setSize(30);        
      drawing_back_line_blk.addField("ITEM2_SUPPLIER_NAME").  
                            setReadOnly().
                            setLabel("DRAWINGBORROWLINEITEM0SUPPLIERNAME: Supplier Name").
                            setFunction("SUPPLIER_INFO_API.Get_Name (:ITEM2_SUPPLIER_ID)").
                            setSize(20);      
      drawing_back_line_blk.addField("BACK_PERSON").
                            setReadOnly().               
                            setLabel("DRAWINGBORROWLINEBACKPERSON: Back Person").
                            setSize(30);        
      drawing_back_line_blk.addField("BACK_PERSON_DESC").
                            setFunction("PERSON_INFO_API.Get_Name(:BACK_PERSON)").
                            setLabel("DRAWINGSENDLINEBACKPERSONDESC: Back Person Desc").
                            setSize(30);         
      drawing_back_line_blk.addField("ITEM2_BORROW__QTY","Number").
                            setDbName("BORROW__QTY").
                            setReadOnly().  
                            setLabel("DRAWINGBORROWLINEBORROWQTY: Borrow  Qty").
                            setSize(30);  
      drawing_back_line_blk.addField("BACK_QTY","Number").
                            setInsertable().
                            setLabel("DRAWINGBACKLINEBACKQTY: Back Qty").
                            setSize(30);
      drawing_back_line_blk.addField("ITEM2_PLAN_BACK_DATA","Date").
                            setDbName("PLAN_BACK_DATA").
                            setInsertable().  
                            setLabel("DRAWINGBACKLINEPLANBACKDATA: Plan Back Date").
                            setSize(30);  
      drawing_back_line_blk.addField("BACK_DATA","Date").
                            setInsertable().
                            setLabel("DRAWINGBACKLINEBACKDATA: Back Date").
                            setSize(30);     
      drawing_back_line_blk.addField("ITEM2_NOTE").
                            setDbName("NOTE").     
                            setInsertable().
                            setLabel("DRAWINGBACKLINEITEM0NOTE: Note").
                            setHeight(6).  
                            setSize(120);    
      drawing_back_line_blk.setView("DRAWING_BACK_LINE");     
      drawing_back_line_blk.defineCommand("DRAWING_BACK_LINE_API","");
      drawing_back_line_blk.setMasterBlock(headblk);      
      drawing_back_line_set = drawing_back_line_blk.getASPRowSet();  
      drawing_back_line_bar = mgr.newASPCommandBar(drawing_back_line_blk);
      drawing_back_line_bar.defineCommand(drawing_back_line_bar.OKFIND, "okFindITEM1");
      drawing_back_line_bar.defineCommand(drawing_back_line_bar.NEWROW, "newRowITEM1");
      drawing_back_line_tbl = mgr.newASPTable(drawing_back_line_blk);
      drawing_back_line_tbl.setTitle("DRAWINGBACKLINEITEMHEAD1: DrawingBackLine");
      drawing_back_line_tbl.enableRowSelect();
      drawing_back_line_tbl.setWrap();
      drawing_back_line_lay = drawing_back_line_blk.getASPBlockLayout();
      drawing_back_line_lay.setDefaultLayoutMode(drawing_back_line_lay.MULTIROW_LAYOUT);
      drawing_back_line_lay.setDataSpan("BACK_DATA", 5);
      drawing_back_line_lay.setDataSpan("ITEM0_NOTE", 5); 
      
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("DRAWINGLISTSDRAWINGSENDLIST: Drawing Send"), "javascript:commandSet('MAIN.activateDrawingSend','')");
      tabs.addTab(mgr.translate("DRAWINGLISTSDRAWINGBORROWLIST: Drawing Borrow"), "javascript:commandSet('MAIN.activateDrawingBorrow','')");
      tabs.addTab(mgr.translate("DRAWINGLISTSDRAWINGBACKLIST: Drawing Back"), "javascript:commandSet('MAIN.activateDrawingback','')");
      tabs.setContainerWidth(700);  
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

   }


   public void activateDrawingSend()
   {   
      tabs.setActiveTab(1);
      okFindITEM1();  
   }
   
   public void activateDrawingBorrow()
   {   
      tabs.setActiveTab(2);
      okFindITEM2();  
   }  
   
   public void activateDrawingback()
   {   
      tabs.setActiveTab(3);  
      okFindITEM3();  
   }  
   
   
   public void  adjust()
   {
      // fill function body
      headbar.removeCustomCommand("activateDrawingSend");
      headbar.removeCustomCommand("activateDrawingBorrow");
      headbar.removeCustomCommand("activateDrawingback");
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "DRAWINGLISTDESC: Drawing List";
   }


   protected String getTitle()
   {
      return "DRAWINGLISTTITLE: Drawing List";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible()){
           appendToHTML(headlay.show());
      if(headlay.isSingleLayout()&&headset.countRows()>=0){
         appendToHTML(tabs.showTabsInit());
         if (tabs.getActiveTab() == 1 )
            appendToHTML(drawing_send_line_lay.show());
         if (tabs.getActiveTab() == 2 )
            appendToHTML(drawing_borrow_line_lay.show());
         if (tabs.getActiveTab() == 3 )
            appendToHTML(drawing_back_line_lay.show());
         appendToHTML(tabs.showTabsFinish());                  
      }
      
      }      

   }
}
