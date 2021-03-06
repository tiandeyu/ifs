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

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class MatStDeliveryReqDlg extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.matmaw.MatStDeliveryReqDlg");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPManager mgr;
   private String id;
   private String proj_type_no;
   private String plan_no;
   private ASPQuery q;
   private ASPContext ctx;
   private String type;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  MatStDeliveryReqDlg (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      if (mgr.dataTransfered())
         storeParamters();
      
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if (mgr.buttonPressed("SUBMIT")) 
         submitCondition();
      else 
         okFind();
      adjust();
   }
   
   public void storeParamters() 
   {
      ASPManager mgr = getASPManager();

      if (mgr.dataTransfered())
      {
         ASPContext ctx = mgr.getASPContext();
         ASPBuffer buf = mgr.getTransferedData();
         String proj_no = buf.getValue("DATA/PROJ_NO");
         String delivery_id = buf.getValue("DATA/DELIVERY_ID");
//         String contract_id = buf.getValue("DATA/CONTRACT_ID");
         if (!mgr.isEmpty(proj_no))
            ctx.setGlobal("PROJ_NO", proj_no);
         if (!mgr.isEmpty(delivery_id))
            ctx.setGlobal("DELIVERY_ID", delivery_id);
//         if (!mgr.isEmpty(entry_id))
//            ctx.setGlobal("CONTRACT_ID", contract_id);
      }
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      ASPContext ctx = mgr.getASPContext();
      
      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.addWhereCondition("PROJ_NO = '"+ctx.findGlobal("PROJ_NO")+"'");
//      q.addWhereCondition("CONTRACT_ID = '"+ctx.findGlobal("CONTRACT_ID")+"' AND OUT_ID IS NULL AND��(PROJ_NO,ENTRY_ID) IN (SELECT PROJ_NO,ENTRY_ID FROM MAT_ST_ENTRY WHERE MAT_ENTRY_TYPE_DB = 'CONTRACT')");
      q.includeMeta("ALL");
//      if(mgr.dataTransfered())
//         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("MATSTORAGENODATA: No data found.");
         headset.clear();
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



//   public void newRow()
//   {
//      ASPManager mgr = getASPManager();
//      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//      ASPBuffer data;
//      ASPCommand cmd;
//
//      cmd = trans.addEmptyCommand("HEAD","MAT_ACCEPT_LINE_API.New__",headblk);
//      cmd.setOption("ACTION","PREPARE");
//      trans = mgr.perform(trans);
//      data = trans.getBuffer("HEAD/DATA");
//      headset.addRow(data);
//   }


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
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("MATSTORAGEPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
               setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
               setLabel("MATACCEPTPROJDESC: Proj Desc").
               setReadOnly().
               setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("MAT_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("MAT_CODE","PROJ_NO").
              setLabel("MATSTORAGEMATNO: Mat No").
              setSize(20);
      headblk.addField("MAT_NAME").  
              setReadOnly().
              setLabel("MATSTORAGENAME: Mat Name").
              setFunction("MAT_CODE_API.GET_MAT_NAME(:PROJ_NO,:MAT_NO)").
              setSize(20);
      mgr.getASPField("MAT_NO").setValidation("MAT_NAME");
      
      //Dec4th add Contract_Id and Contract_desc by @natic
      headblk.addField("CONTRACT_ID").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              setLOVProperty("WHERE", "CLASS_NO IN ('SB','WZ')").
              setLabel("MATSTORAGECONTRACTID: Contract Id").
              setSize(20);
      headblk.addField("CONTRACT_DESC").  
              setReadOnly().
              setLabel("MATSTORAGECONTRACTDESC: Contract Desc").
              setFunction("PROJECT_CONTRACT_API.GET_CONTRACT_DESC(:PROJ_NO,:CONTRACT_ID)").
              setSize(20);
      mgr.getASPField("CONTRACT_ID").setValidation("CONTRACT_DESC");
      //end headlay.setSimple("CONTRACT_DESC");
      
      headblk.addField("STORAGE_ID").
              setMandatory().
              setInsertable().
//              setHidden().
              setLabel("MATSTORAGESTORAGEID: Storage Id").
              setSize(20);
      headblk.addField("STORAGE_QTY","Number").
              setInsertable().
              setLabel("MATSTORAGESTORAGEQTY: Storage Qty").
              setSize(20);
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("MATSTORAGECREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setLabel("MATACCEPTCREATEPERSONNAME: Create Person Name").
              setFunction("PERSON_INFO_API.GET_NAME (:CREATE_PERSON)").
              setSize(20);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.setView("MAT_STORAGE");
//      headblk.defineCommand("MAT_STORAGE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATSTORAGETBLHEAD: Mat Storages");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("MAT_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("CONTRACT_DESC");


   }

   public void submitCondition(){
      
      mgr = getASPManager();
      ASPCommand cmdBuf; 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
     
      trans.clear();
//      String standard_info = "";
      headset.storeSelections();  
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("PROJ_NO,MAT_NO,CONTRACT_ID,STORAGE_ID,STORAGE_QTY");
      for(int i=0;i<selected_fields.countItems();i++){         
          ASPBuffer subBuff = selected_fields.getBufferAt(i);
//          standard_info=standard_info + subBuff.getValueAt(0) + "^";    
         cmdBuf = trans.addCustomCommand("CREATECONDITION"+i, "MAT_ST_DELIVERY_REQ_LINE_API.CREATE_FILE");    
         cmdBuf.addParameter("PROJ_NO", subBuff.getValueAt(0));
         cmdBuf.addParameter("PROJ_DESC", ctx.findGlobal("DELIVERY_ID"));
         cmdBuf.addParameter("MAT_NO", subBuff.getValueAt(1));
         cmdBuf.addParameter("CONTRACT_ID", subBuff.getValueAt(2));
         cmdBuf.addParameter("STORAGE_ID", subBuff.getValueAt(3));
         cmdBuf.addParameter("STORAGE_QTY", subBuff.getValueAt(4));
      }
      mgr.perform(trans);
   }

   public void  adjust()
   {
      // fill function body
//      headbar.disableCommand(headbar.DELETE);
//      headbar.disableCommand(headbar.EDITROW);
//      headbar.disableCommand(headbar.NEWROW);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "MATSTORAGEDESC: Mat Storage";
   }


   protected String getTitle()
   {
      return "MATSTORAGEDESC: Mat Storage";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      if (!headlay.isFindLayout() && headset.countRows() > 0)
      {
         beginDataPresentation();
         printSubmitButton("SUBMIT", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCREATE: Submit"), "OnClick='Close()'");
         printSpaces(1);
         printSubmitButton("CANCEL", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCANCEL: Cancle"), "OnClick='javascript:window.close();'");
         endDataPresentation();
      }
    appendDirtyJavaScript("function Close()\n");
    appendDirtyJavaScript("{\n");
    appendDirtyJavaScript("   try\n");
    appendDirtyJavaScript("   {\n");
    appendDirtyJavaScript("      eval(\"opener.refreshParent()\");\n");
    appendDirtyJavaScript("   }\n");
    appendDirtyJavaScript("   catch(err){}\n");
    appendDirtyJavaScript("   try\n");
    appendDirtyJavaScript("   {\n");
    appendDirtyJavaScript("      window.close();\n");
    appendDirtyJavaScript("   }\n");
    appendDirtyJavaScript("   catch(err){}\n");
    appendDirtyJavaScript("}\n");
 }
   }

