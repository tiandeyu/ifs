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

package ifs.contrw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Time;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ConsCloseOrder extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.contrw.ConsCloseOrder");

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

   private ASPBlock cons_close_order_line_blk;
   private ASPRowSet cons_close_order_line_set;
   private ASPCommandBar cons_close_order_line_bar;
   private ASPTable cons_close_order_line_tbl;
   private ASPBlockLayout cons_close_order_line_lay;
   
   private ASPBlock cons_remain_order_line_blk;
   private ASPRowSet cons_remain_order_line_set;
   private ASPCommandBar cons_remain_order_line_bar;
   private ASPTable cons_remain_order_line_tbl;
   private ASPBlockLayout cons_remain_order_line_lay;

	private ASPTabContainer tabs;
	
	private ASPTransactionBuffer trans;
	private ASPQuery q;
	
	//private String userUnitNo;
	
	private String projNo;
	
	//private String userDeptNo;
	
	private String userNo;
	
	//private String userName;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConsCloseOrder (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      userNo = mgr.getASPContext().findGlobal("CURRUSERID");
	 // userName = mgr.getASPContext().findGlobal("USER_DESC");
	 // userUnitNo = mgr.getASPContext().findGlobal("COMPANY_NO");
	 // userDeptNo = mgr.getASPContext().findGlobal("DEPT_NO");
	  projNo = mgr.getASPContext().findGlobal("PROJECT_NO");
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
          validate();
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("CLOSE_NO")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("TRANS_TYPE")) )
         okFind();
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
         performRefresh();
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
         mgr.showAlert("CONSCLOSEORDERNODATA: No data found.");
         headset.clear();
      }
      eval( cons_close_order_line_set.syncItemSets() );
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
      mgr.getASPField("CREATE_USER").setHidden();
      mgr.getASPField("CLOSE_NO").setHidden();
      mgr.getASPField("CREATE_DATE").setHidden();
      mgr.getASPField("MIS_USER_USER_DESC").setHidden();
      Time time = new Time();
      cmd = trans.addEmptyCommand("HEAD","CONS_CLOSE_ORDER_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANS_TYPE", "Room");
      cmd.setParameter("PROJ_NO", projNo);
      cmd.setParameter("CREATE_DATE", time.toString().substring(0, 10));
      cmd.setParameter("CREATE_USER", userNo);
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
   
      q = trans.addQuery(cons_close_order_line_blk);
      q.addWhereCondition("CLOSE_NO = ? AND PROJ_NO = ? AND TRANS_TYPE = ?");
      q.addParameter("CLOSE_NO", headset.getValue("CLOSE_NO"));
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("TRANS_TYPE", headset.getValue("TRANS_TYPE"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,cons_close_order_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      String remain_ord_no = headset.getValue("REM_ORD_NO");
      cmd = trans.addEmptyCommand("ITEM1","CONS_CLOSE_ORDER_LINE_API.New__",cons_close_order_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_CLOSE_NO", headset.getValue("CLOSE_NO"));
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_TRANS_TYPE", headset.getValue("TRANS_TYPE"));//
      cmd.setParameter("ITEM0_REM_ORD_NO", remain_ord_no);
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      cons_close_order_line_set.addRow(data);
   }

   public void performRefresh()
   {
      okFindITEM1();
   }
   
/*   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(cons_remain_order_line_blk);
      q.addWhereCondition("REM_ORD_NO = ? AND TRANS_TYPE = ? AND PROJ_NO = ?");
      q.addParameter("REM_ORD_NO", headset.getValue("REM_ORD_NO"));
      q.addParameter("TRANS_TYPE", headset.getValue("TRANS_TYPE"));
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,cons_remain_order_line_blk);
      headset.goTo(headrowno);
   }*/
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM2","CONS_REMAIN_ORDER_LINE_API.New__",cons_remain_order_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_REM_ORD_NO", headset.getValue("REM_ORD_NO"));
      cmd.setParameter("ITEM1_TRANS_TYPE", headset.getValue("TRANS_TYPE"));
      cmd.setParameter("ITEM1_PROJ_NO", headset.getValue("PROJ_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      cons_remain_order_line_set.addRow(data);
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
   public void  finish()
   {

      performHEAD( "Finish__" );
   }
  /* public void  return()
   {

      performHEAD( "Return__" );
   }*/
   public void revert(){
	   performHEAD( "Return__" );
   }
   
   public void validate(){
	      ASPManager mgr = getASPManager();

	      trans = mgr.newASPTransactionBuffer();
	      String val = mgr.readValue("VALIDATE");

	      if ("REM_ORD_NO".equals(val))
	      {
	         trans.clear();
	         q =  trans.addQuery("GETREMAININFO", "SELECT T.UNIT_NO UNIT_NO , T.FAC_BUILD_NO FAC_BUILD_NO ,T.ROOM_NO ROOM_NO ,T.DEPT_NO DEPT_NO ,T.ELEVATION ELEVATION FROM CONS_REMAIN_ORDER T WHERE  T.TRANS_TYPE_DB = 'ROOM' AND T.PROJ_NO = ? AND T.REM_ORD_NO= ?");
	         q.addInParameter("PROJ_NO", mgr.getQueryStringValue("PROJ_NO"));
	         q.addInParameter("REM_ORD_NO", mgr.getQueryStringValue("REM_ORD_NO"));
	         trans = mgr.validate(trans);
	         String tempUnitNO = trans.getValue("GETREMAININFO/DATA/UNIT_NO");
	         String tempFacBuildNo = trans.getValue("GETREMAININFO/DATA/FAC_BUILD_NO");
	         String tempRoomNo = trans.getValue("GETREMAININFO/DATA/ROOM_NO");
	         String tempDeptNo = trans.getValue("GETREMAININFO/DATA/DEPT_NO");
	         String tempElevation = trans.getValue("GETREMAININFO/DATA/ELEVATION");
	         
	         tempUnitNO = tempUnitNO == null  ? "" : tempUnitNO;
	         tempRoomNo = tempRoomNo == null  ? "" : tempRoomNo;
	         tempDeptNo = tempDeptNo == null ? "" : tempDeptNo;
	         tempFacBuildNo = tempFacBuildNo == null ? "" : tempFacBuildNo;
	         tempElevation = tempElevation == null ? "" : tempElevation;
	         mgr.responseWrite( tempUnitNO + "^" + tempRoomNo + "^"+tempDeptNo+"^"+tempElevation+"^"+tempFacBuildNo);
	      }
	      
	      mgr.endResponse();//cannot be deleted
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
      headblk.addField("CLOSE_NO").
             // setMandatory().
              setInsertable().
              setLabel("遗留项关闭单号").
              setSize(30).setReadOnly();
      headblk.addField("PROJ_NO").
      		  //setDynamicLOV("CV_PROJ").
              setMandatory().
              //setInsertable().
              setLabel("项目编号").
              setSize(30).setReadOnly();
      headblk.addField("CV_PROJ_PROJ_NAME").
		      setFunction("CV_PROJ_API.GET_PROJ_NAME ( PROJ_NO)").
		      setLabel("项目名称").
		      setSize(30).
		      setReadOnly();
      headblk.addField("TRANS_TYPE").
              enumerateValues("Cons_Trans_Type_API").
              setSelectBox().
              //setMandatory().
              setInsertable().
              setLabel("移交类型").
              setSize(30).
              setHidden();
      headblk.addField("TRANS_TYPE_DB").
              setHidden();
      headblk.addField("APPLY_DATE","Date").
              setInsertable().
              setLabel("提报日期").
              setSize(30);
      headblk.addField("PLAN_CHK_DATE","Date").
              setInsertable().
              setLabel("计划检查日期").
              setSize(30);
      headblk.addField("ACTUAL_CHK_DATE","Date").
              setInsertable().
              setLabel("实际检查日期").
              setSize(30);
      headblk.addField("CREATE_DATE","Date").
              setInsertable().
              setLabel("创建日期").
              setSize(30);
      headblk.addField("REM_ORD_NO").
			  setDynamicLOV("CONS_REMAIN_ORDER").
			  setCustomValidation("REM_ORD_NO,TRANS_TYPE,PROJ_NO", "UNIT_NO,ROOM_NO,DEPT_NO,ELEVATION,FAC_BUILD_NO").
		      setInsertable().
		      setLabel("遗留项单号").
		      setSize(30);
      headblk.addField("ELEVATION").
              setInsertable().
              setLabel("标高").
              setSize(30);
      headblk.addField("UNIT_NO").
      		  setDynamicLOV("CNNC_UNIT").
              setInsertable().
              setLabel("机组").
              setSize(30);
      headblk.addField("CNNC_UNIT_UNIT_NAME").
		      setFunction("CNNC_UNIT_API.GET_UNIT_NAME ( PROJ_NO,UNIT_NO)").
		      setLabel("机组名称").
		      setSize(30).
		      setReadOnly();
      headblk.addField("DEPT_NO").
              setDynamicLOV("SD_STRUCTURE_DEPT","PROJ_NO").setLOVProperty("WHERE", "PARENT_NO IS NULL").
              setInsertable().
              setLabel("承包商").
              setSize(30);
      headblk.addField("SD_STRUCTURE_DEPT_DEPT_NAME").
		      setFunction("SD_STRUCTURE_DEPT_API.GET_DEPT_NAME ( PROJ_NO,DEPT_NO)").
		      setLabel("承包商名称").
		      setSize(30).setReadOnly();
      headblk.addField("FAC_BUILD_NO").
              setDynamicLOV("CNNC_FAC_BUILDING").
              setInsertable().
              setLabel("厂房").
              setSize(30);
      headblk.addField("CNNC_FAC_BUILD_NAME").
		      setFunction("CNNC_FAC_BUILDING_API.GET_FAC_BUILD_NAME ( PROJ_NO,FAC_BUILD_NO)").
		      setLabel("厂房名称").
		      setSize(30).setReadOnly();
      headblk.addField("ROOM_NO").
      		  setDynamicLOV("CNNC_ROOM").
              setInsertable().
              setLabel("房间").
              setSize(30);
      headblk.addField("CNNC_ROOM_ROOM_NAME").
		      setFunction("CNNC_ROOM_API.GET_ROOM_NAME ( PROJ_NO,ROOM_NO)").
		      setLabel("房间名称").
		      setSize(30).
		      setReadOnly();
      headblk.addField("CREATE_USER").
      		  setDynamicLOV("MIS_USER").
              setInsertable().
              setLabel("创建人").
              setSize(30);
      headblk.addField("MIS_USER_USER_DESC").//USER_ID
		      setFunction("MIS_USER_API.GET_USER_DESC ( CREATE_USER)").
		      setLabel("创建人描述").
		      setSize(30).
		      setReadOnly();
      
      headblk.addField("STATE").
              setLabel("状态").
              setSize(30).setHidden();
      headblk.setView("CONS_CLOSE_ORDER");
      headblk.defineCommand("CONS_CLOSE_ORDER_API","New__,Modify__,Remove__,Finish__,Return__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("Finish","CONSCLOSEORDERFINISH: Finish Cons Close Order");
      headbar.addCustomCommand("Revert","CONSCLOSEORDERRETURN: Return Cons Close Order");
      headbar.addCustomCommand("printReport", "打印申请单");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("CONSCLOSEORDERTBLHEAD: Cons Close Orders");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDataSpan("ROOM_NO", 5);
      headlay.setSimple("CV_PROJ_PROJ_NAME");
      headlay.setSimple("CNNC_UNIT_UNIT_NAME");
      headlay.setSimple("SD_STRUCTURE_DEPT_DEPT_NAME");
      headlay.setSimple("MIS_USER_USER_DESC");
      headlay.setSimple("CNNC_FAC_BUILD_NAME");
      headlay.setSimple("CNNC_ROOM_ROOM_NAME");
      headlay.defineGroup("基本信息", "CLOSE_NO,PROJ_NO,CV_PROJ_PROJ_NAME,TRANS_TYPE,APPLY_DATE," +
      		"PLAN_CHK_DATE,ACTUAL_CHK_DATE,REM_ORD_NO,ELEVATION,UNIT_NO,CNNC_UNIT_UNIT_NAME,DEPT_NO," +
      		"SD_STRUCTURE_DEPT_DEPT_NAME,FAC_BUILD_NO,CNNC_FAC_BUILD_NAME,ROOM_NO,CNNC_ROOM_ROOM_NAME," +
      		"CREATE_DATE,CREATE_USER,MIS_USER_USER_DESC,STATE,", true, true);


      cons_close_order_line_blk = mgr.newASPBlock("ITEM1");
      cons_close_order_line_blk.addField("ITEM0_OBJID").
                                setHidden().
                                setDbName("OBJID");
      cons_close_order_line_blk.addField("ITEM0_OBJVERSION").
                                setHidden().
                                setDbName("OBJVERSION");
      cons_close_order_line_blk.addField("ITEM0_CLOSE_NO").
                                setDbName("CLOSE_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("CONSCLOSEORDERLINEITEM0CLOSENO: Close No").
                                setSize(30).setHidden();
      cons_close_order_line_blk.addField("ITEM0_PROJ_NO").
                                setDbName("PROJ_NO").
                                setMandatory().
                                setInsertable().
                                setLabel("CONSCLOSEORDERLINEITEM0PROJNO: Proj No").
                                setSize(30).setHidden();
      cons_close_order_line_blk.addField("ITEM0_TRANS_TYPE").
                                setDbName("TRANS_TYPE").
                                enumerateValues("Cons_Trans_Type_API").
                                setSelectBox().
                                setMandatory().
                                setInsertable().
                                setLabel("移交类型").
                                setSize(30).setHidden();
      cons_close_order_line_blk.addField("LINE_NO","Number").
                                setInsertable().
                                setLabel("CONSCLOSEORDERLINELINENO: 序号").
                                setSize(30).setHidden();
      cons_close_order_line_blk.addField("ITEM0_REM_ORD_NO").
							      setDbName("REM_ORD_NO").
							      setInsertable().
							      setLabel("CONSCLOSEORDERLINEITEM0REMORDNO: 遗留项单号").
							      setSize(30).setHidden();
      cons_close_order_line_blk.addField("REMAIN_NO").
                                setInsertable().
                                setLabel("遗留项编码").
                                setSize(30);
      cons_close_order_line_blk.addField("DESCRIPTION").
                                setInsertable().
                                setLabel("遗留项描述").
                                setSize(30);
      cons_close_order_line_blk.addField("REMAIN_TYPE").
							      enumerateValues("Cons_Remain_Type_API").
							      setSelectBox().
							      setMandatory().
							      setInsertable().
							      setLabel("CONSCLOSEORDERLINEREMAINTYPE: 遗留项类型").
							      setSize(30);
      
      cons_close_order_line_blk.addField("BSEM_CLS_FLAG").
                                setInsertable().
                                setLabel("BSEM关闭确认").
                                setSize(5).setCheckBox("FALSE,TRUE");
      cons_close_order_line_blk.addField("CNPE_CLS_FLAG").
                                setInsertable().
                                setLabel("CNPE关闭确认").
                                setSize(5).setCheckBox("FALSE,TRUE");
      cons_close_order_line_blk.addField("ACTUAL_CLS_DATE","Date").
						      setInsertable().
						      setLabel("关闭时间").
						      setSize(30);
      cons_close_order_line_blk.addField("NOTE").
                                setInsertable().
                                setLabel("备注").
                                setSize(30);
      
      cons_close_order_line_blk.addField("REMAIN_LINE_NO").
                                setInsertable().
                                setLabel("序号").
                                setSize(30).setHidden();
      
      cons_close_order_line_blk.setView("CONS_CLOSE_ORDER_LINE");
      cons_close_order_line_blk.defineCommand("CONS_CLOSE_ORDER_LINE_API","New__,Modify__,Remove__");
      cons_close_order_line_blk.setMasterBlock(headblk);
      cons_close_order_line_set = cons_close_order_line_blk.getASPRowSet();
      cons_close_order_line_bar = mgr.newASPCommandBar(cons_close_order_line_blk);
      cons_close_order_line_bar.defineCommand(cons_close_order_line_bar.OKFIND, "okFindITEM1");
      cons_close_order_line_bar.defineCommand(cons_close_order_line_bar.NEWROW, "newRowITEM1");
      cons_close_order_line_tbl = mgr.newASPTable(cons_close_order_line_blk);
      cons_close_order_line_tbl.setTitle("CONSCLOSEORDERLINEITEMHEAD1: ConsCloseOrderLine");
      cons_close_order_line_tbl.enableRowSelect();
      cons_close_order_line_tbl.setWrap();
      cons_close_order_line_lay = cons_close_order_line_blk.getASPBlockLayout();
      cons_close_order_line_lay.setDefaultLayoutMode(cons_close_order_line_lay.MULTIROW_LAYOUT);
      
      /*cons_remain_order_line_blk = mgr.newASPBlock("ITEM2");
      cons_remain_order_line_blk.addField("ITEM1_OBJID").
                                 setHidden().
                                 setDbName("OBJID");
      cons_remain_order_line_blk.addField("ITEM1_OBJVERSION").
                                 setHidden().
                                 setDbName("OBJVERSION");
      cons_remain_order_line_blk.addField("ITEM1_OBJSTATE").
                                 setHidden().
                                 setDbName("OBJSTATE");
      cons_remain_order_line_blk.addField("ITEM1_OBJEVENTS").
                                 setHidden().
                                 setDbName("OBJEVENTS");
      cons_remain_order_line_blk.addField("ITEM1_REM_ORD_NO").
                                 setDbName("REM_ORD_NO").
                                 setMandatory().
                                 setInsertable().
                                 setLabel("遗留项编码").
                                 setSize(50);
      cons_remain_order_line_blk.addField("ITEM1_TRANS_TYPE").
                                 setDbName("TRANS_TYPE").
                                 enumerateValues("Cons_Trans_Type_API").
                                 setSelectBox().
                                 setMandatory().
                                 setInsertable().
                                 setLabel("移交类型").
                                 setSize(200);
      cons_remain_order_line_blk.addField("ITEM1_PROJ_NO").
                                 setDbName("PROJ_NO").
                                 setMandatory().
                                 setInsertable().
                                 setLabel("项目编号").
                                 setSize(50);
      cons_remain_order_line_blk.addField("ITEM1_LINE_NO","Number").
                                 setDbName("LINE_NO").
                                 setMandatory().
                                 setInsertable().
                                 setLabel("序号").
                                 setSize(30);
      cons_remain_order_line_blk.addField("ITEM1_REMAIN_NO").
                                 setDbName("REMAIN_NO").
                                 setInsertable().
                                 setLabel("CONSREMAINORDERLINEITEM1REMAINNO: Remain No").
                                 setSize(50);
      cons_remain_order_line_blk.addField("ITEM1_DESCRIPTION").
                                 setDbName("DESCRIPTION").
                                 setInsertable().
                                 setLabel("遗留项描述").
                                 setSize(200);
      cons_remain_order_line_blk.addField("ITEM1_CREATE_DATE","Date").
                                 setDbName("CREATE_DATE").
                                 setInsertable().
                                 setLabel("创建日期").
                                 setSize(30);
      cons_remain_order_line_blk.addField("PLAN_DATE","Date").
                                 setInsertable().
                                 setLabel("计划清除时间").
                                 setSize(30);
      cons_remain_order_line_blk.addField("ACTUAL_DATE","Date").
                                 setInsertable().
                                 setLabel("实际清除时间").
                                 setSize(30);
      cons_remain_order_line_blk.addField("ITEM1_REMAIN_TYPE").
                                 setDbName("REMAIN_TYPE").
                                 enumerateValues("Cons_Remain_Type_API").
                                 setSelectBox().
                                 setMandatory().
                                 setInsertable().
                                 setLabel("类型").
                                 setSize(200);
      cons_remain_order_line_blk.addField("ITEM1_STATE").
                                 setDbName("STATE").
                                 setLabel("状态").
                                 setSize(30);
      cons_remain_order_line_blk.setView("CONS_REMAIN_ORDER_LINE");
      cons_remain_order_line_blk.defineCommand("CONS_REMAIN_ORDER_LINE_API","New__,Modify__,Remove__,Close__,Open__");
      cons_remain_order_line_blk.setMasterBlock(headblk);
      cons_remain_order_line_set = cons_remain_order_line_blk.getASPRowSet();
      cons_remain_order_line_bar = mgr.newASPCommandBar(cons_remain_order_line_blk);
      cons_remain_order_line_bar.addCustomCommand("CloseITEM2","CONSREMAINORDERLINECLOSE: Close Cons Remain Order Line");
      cons_remain_order_line_bar.addCustomCommand("OpenITEM2","CONSREMAINORDERLINEOPEN: Open Cons Remain Order Line");

      cons_remain_order_line_bar.defineCommand(cons_remain_order_line_bar.OKFIND, "okFindITEM2");
      cons_remain_order_line_bar.defineCommand(cons_remain_order_line_bar.NEWROW, "newRowITEM2");
      cons_remain_order_line_tbl = mgr.newASPTable(cons_remain_order_line_blk);
      cons_remain_order_line_tbl.setTitle("CONSREMAINORDERLINEITEMHEAD2: ConsRemainOrderLine");
      cons_remain_order_line_tbl.enableRowSelect();
      cons_remain_order_line_tbl.setWrap();
      cons_remain_order_line_lay = cons_remain_order_line_blk.getASPBlockLayout();
      cons_remain_order_line_lay.setDefaultLayoutMode(cons_remain_order_line_lay.MULTIROW_LAYOUT);*/
      

      headbar.addCustomCommand("LoadRemain", "追加遗留项明细");
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab("遗留项清单", "javascript:commandSet('MAIN.activateITEM1','')");
      //tabs.addTab("未关闭遗留项", "javascript:commandSet('MAIN.activateITEM2','')");
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);


   }



   public void  adjust()
   {
      // fill function body
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "CONSCLOSEORDERDESC: Cons Close Order";
   }


   protected String getTitle()
   {
      return "CONSCLOSEORDERTITLE: 房间关闭申请单";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      printHiddenField("REFRESH_PARENT", "FALSE");
      
      if (headlay.isVisible()){
         appendToHTML(headlay.show());
      }
      else {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (headset.countRows() > 0
            && (headlay.isSingleLayout() || headlay.isCustomLayout())) {
         appendToHTML(tabs.showTabsInit());
         appendToHTML(cons_close_order_line_lay.show());
      }
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
      
   }
   
   public void loadRemain() throws FndException
   {  
      //ASPManager mgr = getASPManager();
      if (headset.countRows() > 0)
      {
         String close_no = headset.getValue("CLOSE_NO");
         String proj_no = headset.getValue("PROJ_NO");
         String trans_type_db = headset.getValue("TRANS_TYPE_DB");
         String rem_ord_no = headset.getValue("REM_ORD_NO");
       
         String url = "../contrw/ConsRemainOrderLine.page?CLOSE_NO=" + close_no +
                                                         "PROJ_NO=" + proj_no +
                                                         "TRANS_TYPE_DB=" + trans_type_db +
                                                         "REM_ORD_NO=" + rem_ord_no;
         appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
      }
   }
  
  public void printReport() throws FndException
  {
	   String trans_type = headset.getValue("TRANS_TYPE").toUpperCase();
	   String proj_no = headset.getValue("PROJ_NO");
	   String rem_ord_no = headset.getValue("REM_ORD_NO");
	   String close_no = headset.getValue("CLOSE_NO");
	   String remain_type = "I";//headset.getValue("REMAIN_TYPE");
	   String remain_no = "";
	   String js = "window.open('../report/showReport.jsp?raq=RoomTransClose.raq&proj_no="+proj_no+"&trans_type=" +
	   		""+trans_type+"&close_no="+close_no+"&rem_ord_no="+rem_ord_no+"&remain_type="+remain_type+"&remain_no="+remain_no+"','_blank','height=600, width=780, top=20, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');";
	   
	   appendDirtyJavaScript(js);
  }

}
