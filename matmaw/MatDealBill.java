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

public class MatDealBill extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatDealBill");

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

   private ASPBlock mat_deal_bill_line_blk;
   private ASPRowSet mat_deal_bill_line_set;
   private ASPCommandBar mat_deal_bill_line_bar;
   private ASPTable mat_deal_bill_line_tbl;
   private ASPBlockLayout mat_deal_bill_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatDealBill (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DEAL_BILL_NO")) )
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
         mgr.showAlert("MATDEALBILLNODATA: No data found.");
         headset.clear();
      }
      eval( mat_deal_bill_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","MAT_DEAL_BILL_API.New__",headblk);
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

      q = trans.addQuery(mat_deal_bill_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND DEAL_BILL_NO = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("DEAL_BILL_NO", headset.getValue("DEAL_BILL_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_deal_bill_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","MAT_DEAL_BILL_LINE_API.New__",mat_deal_bill_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_DEAL_BILL_NO", headset.getValue("DEAL_BILL_NO"));
      cmd.setParameter("ITEM0_ACCEPT_ID", headset.getValue("ACCEPT_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      mat_deal_bill_line_set.addRow(data);
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

      headblk.addField("DEAL_BILL_NO").
//              setMandatory().
//              setInsertable().
              setReadOnly().
              setLabel("MATDEALBILLDEALBILLNO: Deal Bill No").
              setSize(20);
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("MATDEALBILLPROJNO: Proj No").
              setSize(20);

      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("MATDEALBILLPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
//      headblk.addField("ACCEPT_NO").
//              setInsertable().
//              setLabel("MATDEALBILLACCEPTNO: Accept No").
//              setSize(50);

      headblk.addField("ACCEPT_ID").
              setInsertable().
              setDynamicLOV("MAT_ACCEPT","PROJ_NO").
              setLabel("MATDEALBILLACCEPTID: Accept Id").
              setSize(20);
      headblk.addField("DEAL_STATUS").
              setInsertable().
//              setHidden().
              setLabel("MATDEALBILLDEALSTATUS: Deal Status").
              setSize(20);
//      headblk.addField("CREATE_PERSON").
//              setInsertable().
//              setLabel("MATDEALBILLCREATEPERSON: Create Person").
//              setSize(20);
      headblk.setView("MAT_DEAL_BILL");
//      headblk.defineCommand("MAT_DEAL_BILL_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATDEALBILLTBLHEAD: Mat Deal Bills");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.addCustomCommand("makeAccept", "MATDEALBILLMAKEACCEPT: Make Accept");
      headlay.setSimple("PROJ_DESC");
 


      mat_deal_bill_line_blk = mgr.newASPBlock("ITEM1");
      mat_deal_bill_line_blk.addField("ITEM0_OBJID").
                             setHidden().
                             setDbName("OBJID");
      mat_deal_bill_line_blk.addField("ITEM0_OBJVERSION").
                             setHidden().
                             setDbName("OBJVERSION");
      mat_deal_bill_line_blk.addField("ITEM0_PROJ_NO").
                             setDbName("PROJ_NO").
                             setMandatory().
                             setHidden().
                             setInsertable().
                             setLabel("MATDEALBILLLINEITEM0PROJNO: Proj No").
                             setSize(50);
      mat_deal_bill_line_blk.addField("ITEM0_DEAL_BILL_NO").
                             setDbName("DEAL_BILL_NO").
                             setMandatory().
                             setHidden().
                             setInsertable().
                             setLabel("MATDEALBILLLINEITEM0DEALBILLNO: Deal Bill No").
                             setSize(50);

      mat_deal_bill_line_blk.addField("IS_DEAL").
//                             setInsertable().
                             setReadOnly().
                             setCheckBox("FALSE,TRUE").
                             setLabel("MATDEALBILLLINEISDEAL: Is Deal").
                             setSize(30);
      mat_deal_bill_line_blk.addField("DEAL_LINE_NO").
//                             setMandatory().
//                             setInsertable().
                             setReadOnly().
                             setLabel("MATDEALBILLLINEDEALLINENO: Deal Line No").
                             setSize(20);
      mat_deal_bill_line_blk.addField("ITEM0_ACCEPT_ID").
                             setDbName("ACCEPT_ID").
                             setInsertable().
                             setHidden().
                             setLabel("MATDEALBILLLINEITEM0ACCEPTID: Accept Id").
                             setSize(50);
      mat_deal_bill_line_blk.addField("LINE_NO").
//                             setInsertable().
                             setReadOnly().
                             setDynamicLOV("MAT_ACCEPT_LINE","PROJ_NO,ACCEPT_ID").
                             setLabel("MATDEALBILLLINELINENO: Line No").
                             setSize(20);

      mat_deal_bill_line_blk.addField("CONTRACT_ID").
                          setLabel("MATACCEPTCONTRACTID: Contract Id").
                          setFunction("MAT_ACCEPT_LINE_API.Get_Contract_Id(:PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                          setReadOnly().
                          setSize(20);
      mat_deal_bill_line_blk.addField("CONTRACT_DESC").
                          setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC ( :PROJ_NO,MAT_ACCEPT_LINE_API.Get_Contract_Id(:PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                          setLabel("MATACCEPTCONTRACTDESC: Contract Desc").
                          setReadOnly().
                          setSize(30);

      mat_deal_bill_line_blk.addField("ITEM_NO").
                          setFunction("MAT_ACCEPT_LINE_API.Get_Item_No(:PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                          setLabel("MATACCEPTLINEITEMNO: Item No").
                          setReadOnly().
                          setSize(20);
      mat_deal_bill_line_blk.addField("ITEM_DESC").
                          setFunction("PROJECT_CONTRACT_ITEM_API.GET_ITEM_DESC ( :PROJ_NO,MAT_ACCEPT_LINE_API.Get_Contract_Id(:PROJ_NO,:ACCEPT_ID,:LINE_NO),MAT_ACCEPT_LINE_API.Get_Item_No(:PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                          setLabel("MATARRIVELINEITEMDESC: Item Desc").
                          setReadOnly().
                          setSize(30);

      mat_deal_bill_line_blk.addField("MAT_NO").
                          setReadOnly().
                          setFunction("MAT_ACCEPT_LINE_API.Get_Mat_No (:PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                          setLabel("MATACCEPTLINEMATNO: Mat No").
                          setSize(20);

      mat_deal_bill_line_blk.addField("MAT_NAME").
                            setReadOnly().
                            setLabel("MATARRIVELINEMATNAME: Mat Name").
                            setFunction("mat_code_api.Get_Mat_Name(:PROJ_NO,MAT_ACCEPT_LINE_API.Get_Mat_No (:PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                            setSize(20);

      mat_deal_bill_line_blk.addField("ACCEPT_NO_QTY","Number").
                          setReadOnly().
                          setFunction("MAT_ACCEPT_LINE_API.Get_Accept_No_Qty(:PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                          setLabel("MATACCEPTLINEACCEPTNOQTY: Accept No Qty").
                          setSize(20);
      
      mat_deal_bill_line_blk.addField("UNIT_NO").
                          setReadOnly().
                          setFunction("MAT_ACCEPT_LINE_API.Get_Mat_No(:PROJ_NO,:ACCEPT_ID,:LINE_NO)").
                          setLabel("MATACCEPTLINEUNITNO: Unit No").
                          setSize(20);
      mat_deal_bill_line_blk.addField("UNIT_DESC").
                          setReadOnly().
                          setLabel("MATACCEPTLINEUNITDESC: Unit Desc").
                          setFunction("Iso_Unit_API.Get_Description(MAT_ACCEPT_LINE_API.Get_Mat_No(:PROJ_NO,:ACCEPT_ID,:LINE_NO))").
                          setSize(30);

      
      mat_deal_bill_line_blk.addField("UNQUALIFIED_DESC").
                             setInsertable().
                             setLabel("MATDEALBILLLINEUNQUALIFIEDDESC: Unqualified Desc").
                             setHeight(3).
                             setSize(100);
      mat_deal_bill_line_blk.addField("DEAL_STYLE").
                             setInsertable().
                             setLabel("MATDEALBILLLINEDEALSTYLE: Deal Style").
                             setHeight(3).
                             setSize(100);
      mat_deal_bill_line_blk.setView("MAT_DEAL_BILL_LINE");
      mat_deal_bill_line_blk.defineCommand("MAT_DEAL_BILL_LINE_API","Modify__");
      mat_deal_bill_line_blk.setMasterBlock(headblk);
      mat_deal_bill_line_set = mat_deal_bill_line_blk.getASPRowSet();
      mat_deal_bill_line_bar = mgr.newASPCommandBar(mat_deal_bill_line_blk);
      mat_deal_bill_line_bar.defineCommand(mat_deal_bill_line_bar.OKFIND, "okFindITEM1");
//      mat_deal_bill_line_bar.defineCommand(mat_deal_bill_line_bar.NEWROW, "newRowITEM1");
      mat_deal_bill_line_bar.addCustomCommand("printReport", "MATDEALBILLLINEPRINTREPORT: Print Report...");
      mat_deal_bill_line_tbl = mgr.newASPTable(mat_deal_bill_line_blk);
      mat_deal_bill_line_tbl.setTitle("MATDEALBILLLINEITEMHEAD1: MatDealBillLine");
      mat_deal_bill_line_tbl.enableRowSelect();
      mat_deal_bill_line_tbl.setWrap();
      mat_deal_bill_line_lay = mat_deal_bill_line_blk.getASPBlockLayout();
      mat_deal_bill_line_lay.setDefaultLayoutMode(mat_deal_bill_line_lay.MULTIROW_LAYOUT);
      
      mat_deal_bill_line_lay.setSimple("CONTRACT_DESC");
      mat_deal_bill_line_lay.setSimple("ITEM_DESC");
      mat_deal_bill_line_lay.setSimple("MAT_NAME");
      mat_deal_bill_line_lay.setSimple("UNIT_DESC");
      mat_deal_bill_line_lay.setDataSpan("UNQUALIFIED_DESC",5);
      mat_deal_bill_line_lay.setDataSpan("DEAL_STYLE",5);



   }


   public void  printReport() throws FndException, UnsupportedEncodingException
  {
   ASPManager mgr = getASPManager();
   ASPConfig cfg = getASPConfig();
   String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
   if (mat_deal_bill_line_lay.isMultirowLayout())
      mat_deal_bill_line_set.goTo(headset.getRowSelected());
   if (mat_deal_bill_line_set.countRows()>0 )
         {   
            String proj_no = mat_deal_bill_line_set.getValue("PROJ_NO");
            String deal_bill_no = mat_deal_bill_line_set.getValue("DEAL_BILL_NO");
            String deal_line_no = mat_deal_bill_line_set.getValue("DEAL_LINE_NO");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatDealBill.raq&PROJ_NO="+proj_no+"&DEAL_BILL_NO="+deal_bill_no+"&DEAL_LINE_NO="+deal_line_no
               + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
        }
  }   
   
   public void makeAccept() throws FndException{
      ASPManager mgr = getASPManager();
      ASPCommand cmdBuf; 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String deliveryInfo = "";
      int selectCount = 0;
      mat_deal_bill_line_set.storeSelections();  
      ASPBuffer aspbuf = mat_deal_bill_line_set.getSelectedRows();
      if(aspbuf.countItems() == 0){
         mgr.showAlert("MATDEALBILLWARN1: There is no deal bill selected!");
      }else{
         for(int i=0;i< aspbuf.countItems() ;i++ ){     
            if("FALSE".equals(aspbuf.getBufferAt(i).getValue("IS_DEAL"))){
               deliveryInfo = deliveryInfo + aspbuf.getBufferAt(i).getValue("DEAL_LINE_NO") + "^";
            }
         }
         if(!"".equals(deliveryInfo)){                        
            cmdBuf = trans.addCustomCommand("DEALBILLACCEPT", "MAT_DEAL_BILL_API.Back_To_Accept__");
            cmdBuf.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
            cmdBuf.addParameter("DEAL_BILL_NO", headset.getValue("DEAL_BILL_NO"));     
            cmdBuf.addParameter("OBJID", deliveryInfo);
            mgr.perform(trans);
            okFindITEM1();
         }else{         
            mgr.showAlert("MATDEALBILLWARN2: All the deal bills those were choosen were already done.");
         }
      }
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
      return "MATDEALBILLDESC: Mat Deal Bill";
   }


   protected String getTitle()
   {
      return "MATDEALBILLTITLE: Mat Deal Bill";
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
      if (mat_deal_bill_line_lay.isVisible())
          appendToHTML(mat_deal_bill_line_lay.show());

   }
}