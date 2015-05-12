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
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class MatStDeliveryReq extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatStDeliveryReq");

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

   private ASPBlock mat_st_delivery_req_line_blk;
   private ASPRowSet mat_st_delivery_req_line_set;
   private ASPCommandBar mat_st_delivery_req_line_bar;
   private ASPTable mat_st_delivery_req_line_tbl;
   private ASPBlockLayout mat_st_delivery_req_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatStDeliveryReq (ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }

   public void run() throws FndException
   {
      super.run();

      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("DELIVERY_ID")) )
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate(); 
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
      {
         headset.refreshRow();
         okFindITEM1();
      }
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
      q.addWhereCondition("MAT_DELEVERAY_TYPE = 'Req'");
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("MATSTDELIVERYREQNODATA: No data found.");
         headset.clear();
      }else okFindITEM1();
//      eval( mat_st_delivery_req_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","MAT_ST_DELIVERY_REQ_API.New__",headblk);
      cmd.setParameter("MAT_DELEVERAY_TYPE", "Req");
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

      q = trans.addQuery(mat_st_delivery_req_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND DELIVERY_ID = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("DELIVERY_ID", headset.getValue("DELIVERY_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,mat_st_delivery_req_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","MAT_ST_DELIVERY_REQ_LINE_API.New__",mat_st_delivery_req_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_DELIVERY_ID", headset.getValue("DELIVERY_ID"));
      cmd.setParameter("ITEM0_CONTRACT_ID", headset.getValue("CONTRACT_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      mat_st_delivery_req_line_set.addRow(data);
   }

   public void validate()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String val = mgr.readValue("VALIDATE"); 
       String txt = "";
       String matUnit = "";
       String matName = "";
       String prodModel = "";
       
       if ("MAT_NO".equals(val)) {   
          cmd = trans.addCustomFunction("MATNAME", 
                "MAT_CODE_API.Get_Mat_Name", "MAT_NAME");
          cmd.addParameter("ITEM0_PROJ_NO, MAT_NO");
          
          cmd = trans.addCustomFunction("UNITNO", 
                "MAT_CODE_API.Get_Mat_Unit", "UNIT_NO");
          cmd.addParameter("ITEM0_PROJ_NO, MAT_NO");
          
          cmd = trans.addCustomFunction("PRODMODEL", 
                "MAT_CODE_API.Get_Prod_Model", "PROD_MODEL");
          cmd.addParameter("ITEM0_PROJ_NO, MAT_NO");
   
          trans = mgr.validate(trans);
          matName = trans.getValue("MATNAME/DATA/MAT_NAME");
          matUnit = trans.getValue("UNITNO/DATA/UNIT_NO");
          prodModel = trans.getValue("PRODMODEL/DATA/PROD_MODEL");
          txt = ((mgr.isEmpty(matName)) ? "" : matName )+ "^" + ((mgr.isEmpty(matUnit)) ? "" : matUnit )+ "^";
          txt = txt + ((mgr.isEmpty(prodModel)) ? "" : prodModel) + "^";
          mgr.responseWrite(txt);
       }
       mgr.endResponse();
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
      headblk.addField("DELIVERY_ID").
//              setReadOnly().
              setInsertable().
//              setWfProperties().
              setLabel("MATSTDELIVERYREQDELIVERYID: Delivery Id").
              setSize(30);
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
//              setWfProperties().
              setDynamicLOV("GENERAL_PROJECT",600,445).
              setLabel("MATSTDELIVERYREQPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("MATSTDELIVERYREQGENERALPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
//      headblk.addField("STOWAGE_ID").
//              setDbName("STORAGE_ID").
//              setInsertable().
//              setMandatory().
//              setDynamicLOV("MAT_STOWAGE","PROJ_NO").
//              setLabel("MATSTDELIVERYREQSTOWAGEID: Stowage Id").
//              setSize(20);
//      headblk.addField("STORAGE_DESC").
//              setFunction("MAT_STOWAGE_API.GET_STORAGE_DESC ( :PROJ_NO,:STOWAGE_ID)").
//              setLabel("MATSTDELIVERYREQSTORAGEDESC: Storage Desc").
//              setReadOnly().
//              setSize(30);
//      mgr.getASPField("STOWAGE_ID").setValidation("STORAGE_DESC");
      headblk.addField("MAT_DELEVERAY_TYPE").
              setHidden().
              enumerateValues("Mat_Deleveray_Type_API").
              setSelectBox().
              setReadOnly().
              setLabel("MATSTDELIVERYREQMATDELEVERAYTYPE: Mat Deleveray Type").
              setSize(30);
      headblk.addField("MAT_TYPE_ID").
              setInsertable().
              setHidden().
              setDynamicLOV("MAT_TYPE").
              setLabel("MATSTDELIVERYREQMATTYPEID: Mat Type Id").
              setSize(30);
      headblk.addField("MAT_TYPE_NAME").
              setReadOnly().
              setHidden().
              setFunction("MAT_TYPE_API.Get_Mat_Type_Name ( :MAT_TYPE_ID)").
              setLabel("MATSTDELIVERYREQMATTYPENAME: Mat Type Name").
              setSize(30);
      mgr.getASPField("MAT_TYPE_ID").setValidation("MAT_TYPE_NAME");
      
      //Dec4th add Contract_Id and Contract_desc by @natic
      headblk.addField("CONTRACT_ID").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLOVProperty("WHERE", "CLASS_NO IN ('SB','WZ')").
              setLabel("MATSTDELIVERYREQCONTRACTID: Contract Id").
              setSize(20);
      headblk.addField("CONTRACT_DESC").  
              setReadOnly().
              setLabel("MATSTDELIVERYREQCONTRACTDESC: Contract Desc").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
              setSize(20);
      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");
      //end 
      
      headblk.addField("USE_FOR").
              setInsertable().
              setMandatory().
              setLabel("MATSTDELIVERYREQUSERFOR: Use For").
              setSize(30);

      headblk.addField("STATUS").
              setReadOnly().
              setHidden().
              setLabel("MATSTDELIVERYREQSTATUS: Status").
              setSize(30);

      headblk.addField("STATUS_DESC").
              setReadOnly().
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("MATSTDELIVERYREQSTATUS: Status").
              setSize(30);
      
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("MATSTDELIVERYREQCREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("MATSTDELIVERYREQPERSONINFONAME: Create Person Name").
              setReadOnly().
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");

      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("MATSTDELIVERYREQCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("FLOW_TITLE").
      setWfProperties().
      setReadOnly().
      setHidden().
      setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
      setLabel("FLOWTITLE: Flow Title").
      setSize(30);
     
      headblk.setView("MAT_ST_DELIVERY_REQ");
      headblk.defineCommand("MAT_ST_DELIVERY_REQ_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATSTDELIVERYREQTBLHEAD: Mat St Delivery Reqs");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headbar.addSecureCustomCommand("CreateFile", "MATSTDELIVERYREQCREATEFILE: Create File...", "MAT_ST_DELIVERY_REQ_LINE_API.CREATE_FILE");
      headbar.addCustomCommand("printReport", "MATSTDELIVERYREQPRINTREPORT: Print Report...");
      headbar.addCustomCommand("printReport2", "MATSTDELIVERYREQPRINTREPORT2: Print Report2...");
      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("MAT_TYPE_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setDataSpan("STATUS_DESC", 5);
      headlay.setSimple("CONTRACT_DESC");
//      headlay.setSimple("STORAGE_DESC");
 


      mat_st_delivery_req_line_blk = mgr.newASPBlock("ITEM1");
      mat_st_delivery_req_line_blk.addField("ITEM0_OBJID").
                                   setHidden().
                                   setDbName("OBJID");
      mat_st_delivery_req_line_blk.addField("ITEM0_OBJVERSION").
                                   setHidden().
                                   setDbName("OBJVERSION");
      mat_st_delivery_req_line_blk.addField("ITEM0_PROJ_NO").
                                   setDbName("PROJ_NO").
                                   setHidden().
                                   setLabel("MATSTDELIVERYREQLINEITEM0PROJNO: Proj No").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("ITEM0_DELIVERY_ID").
                                   setDbName("DELIVERY_ID").
                                   setHidden().
                                   setLabel("MATSTDELIVERYREQLINEITEM0DELIVERYID: Delivery Id").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("DELIVERY_LINE_NO").
                                   setHidden().
                                   setLabel("MATSTDELIVERYREQLINEDELIVERYLINENO: Delivery Line No").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("ITEM0_CONTRACT_ID").
                                   setDbName("CONTRACT_ID").
                                   setInsertable().
                                   setHidden().
                                   setLabel("MATSTDELIVERYREQITEM0CONTRACTID: Contract Id").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("STORAGE_ID").
                                   setInsertable().
                                   setDynamicLOV("MAT_STOWAGE","PROJ_NO").
//                                   setLOVProperty("FORCE_KEY", "STORAGE_ID").
                                   setLabel("MATSTDELIVERYREQLINESTORAGEID: Storage Id").
                                   setSize(50);

      mat_st_delivery_req_line_blk.addField("STORAGE_DESC").
                          setReadOnly().
                          setFunction("MAT_STOWAGE_API.Get_Storage_Desc(:ITEM0_PROJ_NO,:STORAGE_ID)").
                          setLabel("MATSTDELIVERYREQLINESTORAGEDESC: Storage Desc").
                          setSize(20);
      mgr.getASPField("STORAGE_ID").setValidation("STORAGE_DESC");
      mat_st_delivery_req_line_blk.addField("MAT_NO").
                                   setInsertable().
                                   setDynamicLOV("MAT_STORAGE_LOV","PROJ_NO,STORAGE_ID,CONTRACT_ID").
//                                   setLOVProperty("FORCE_KEY", "MAT_NO").
                                   setLabel("MATSTDELIVERYREQLINEMATNO: Mat No").
                                   setCustomValidation("ITEM0_PROJ_NO, MAT_NO", "MAT_NAME,UNIT_NO,PROD_MODEL").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("MAT_NAME").
                                   setReadOnly().
                                   setFunction("MAT_CODE_API.Get_Mat_Name (:ITEM0_PROJ_NO, :MAT_NO)").
                                   setLabel("MATSTDELIVERYREQLINEMATNAME: Mat Name").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("UNIT_NO").
                                   setReadOnly().
                                   setLabel("MATSTDELIVERYREQLINEUNITNO: Unit No").
                                   setFunction("MAT_CODE_API.Get_Mat_Unit (:ITEM0_PROJ_NO, :MAT_NO)").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("PROD_MODEL").
                                   setReadOnly().
                                   setLabel("MATSTDELIVERYREQLINEPRODMODEL: PROD MODEL").
                                   setFunction("MAT_CODE_API.Get_Prod_Model (:ITEM0_PROJ_NO, :MAT_NO)").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("STORAGE_QTY").
                                   setReadOnly().
                                   setFunction("Mat_Storage_Api.Get_Storage_Qty (:ITEM0_PROJ_NO, :MAT_NO, :STORAGE_ID,:ITEM0_CONTRACT_ID)").
                                   setLabel("MATSTDELIVERYREQLINESTORAGEQTY: Storage Qty").
                                   setSize(50);
      mgr.getASPField("MAT_NO").setValidation("STORAGE_QTY");
      mat_st_delivery_req_line_blk.addField("REQ_QTY","Number").
                                   setInsertable().
                                   setLabel("MATSTDELIVERYREQLINEREQQTY: Req Qty").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("ITEM0_CREATE_TIME","Date").
                                   setDbName("CREATE_TIME").
                                   setInsertable().
                                   setLabel("MATSTDELIVERYREQLINEITEM0CREATETIME: Create Time").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("ITEM0_CREATE_PERSON").
                                   setDbName("CREATE_PERSON").
                                   setInsertable().
                                   setDynamicLOV("PERSON_INFO").
                                   setLabel("MATSTDELIVERYREQLINEITEM0CREATEPERSON: Create Person").
                                   setSize(50);
      mat_st_delivery_req_line_blk.addField("ITEM0_CREATE_PERSON_NAME").
                                   setFunction("PERSON_INFO_API.GET_NAME ( :ITEM0_CREATE_PERSON)").
                                   setLabel("MATSTDELIVERYREQITEM0PERSONINFONAME: Create Person Name").
                                   setReadOnly().
                                   setSize(30);
      mgr.getASPField("ITEM0_CREATE_PERSON").setValidation("ITEM0_CREATE_PERSON_NAME");

      mat_st_delivery_req_line_blk.setView("MAT_ST_DELIVERY_REQ_LINE");
      mat_st_delivery_req_line_blk.defineCommand("MAT_ST_DELIVERY_REQ_LINE_API","New__,Modify__,Remove__");
      mat_st_delivery_req_line_blk.setMasterBlock(headblk);
      mat_st_delivery_req_line_set = mat_st_delivery_req_line_blk.getASPRowSet();
      mat_st_delivery_req_line_bar = mgr.newASPCommandBar(mat_st_delivery_req_line_blk);
      mat_st_delivery_req_line_bar.defineCommand(mat_st_delivery_req_line_bar.OKFIND, "okFindITEM1");
      mat_st_delivery_req_line_bar.defineCommand(mat_st_delivery_req_line_bar.NEWROW, "newRowITEM1");
      mat_st_delivery_req_line_tbl = mgr.newASPTable(mat_st_delivery_req_line_blk);
      mat_st_delivery_req_line_tbl.setTitle("MATSTDELIVERYREQLINEITEMHEAD1: MatStDeliveryReqLine");
      mat_st_delivery_req_line_tbl.enableRowSelect();
      mat_st_delivery_req_line_tbl.setWrap();
      mat_st_delivery_req_line_lay = mat_st_delivery_req_line_blk.getASPBlockLayout();
      mat_st_delivery_req_line_lay.setDefaultLayoutMode(mat_st_delivery_req_line_lay.MULTIROW_LAYOUT);
      mat_st_delivery_req_line_lay.setSimple("ITEM0_CREATE_PERSON_NAME");
      mat_st_delivery_req_line_lay.setSimple("MAT_NAME");
      mat_st_delivery_req_line_lay.setSimple("STORAGE_DESC");


   }


   public void adjust()
   {
      try {
         super.adjust();
      } catch (FndException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // fill function body
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
      
      ASPBuffer selected_fields = headset.getSelectedRows("PROJ_NO,DELIVERY_ID");

      callNewWindow("MatStDeliveryReqDlg.page", selected_fields);
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
            String delivery_id = headset.getValue("DELIVERY_ID");
             appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatStDeliveryReq.raq&projNo="+proj_no+"&deliveryId="+delivery_id
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
              String delivery_id = headset.getValue("DELIVERY_ID");
               appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=MatStDeliveryReq2.raq&projNo="+proj_no+"&deliveryId="+delivery_id
                 + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
          }
    } 

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "MATSTDELIVERYREQDESC: Mat St Delivery Req";
   }


   protected String getTitle()
   {
      return "MATSTDELIVERYREQTITLE: Mat St Delivery Req";
   }


   protected void printContents() throws FndException
   {
      super.printContents();
      printHiddenField("REFRESH_PARENT", "FALSE");

      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      if (mat_st_delivery_req_line_lay.isVisible())
          appendToHTML(mat_st_delivery_req_line_lay.show());
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}
