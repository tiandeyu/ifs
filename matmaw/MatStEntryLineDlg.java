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

public class MatStEntryLineDlg extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.standw.NeceCondLineFind");

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

   public  MatStEntryLineDlg (ASPManager mgr, String page_path)
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
         String entry_id = buf.getValue("DATA/ENTRY_ID");
         String contract_id = buf.getValue("DATA/CONTRACT_ID");
         if (!mgr.isEmpty(proj_no))
            ctx.setGlobal("PROJ_NO", proj_no);
         if (!mgr.isEmpty(entry_id))
            ctx.setGlobal("ENTRY_ID", entry_id);
         if (!mgr.isEmpty(contract_id))
            ctx.setGlobal("CONTRACT_ID", contract_id);
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
      q.addWhereCondition("PROJ_NO = '"+ctx.findGlobal("PROJ_NO")+"' AND CONTRACT_ID='"+ctx.findGlobal("CONTRACT_ID")+"'");
      q.addWhereCondition("nvl(ACCEPT_QTY,0)>nvl(MAT_ST_ENTRY_LINE_API.GET_SUM_ENTRY_QTY(PROJ_NO,ACCEPT_ID,LINE_NO),0)");
      q.addWhereCondition("MAT_ACCEPT_API.Get_Status(PROJ_NO,ACCEPT_ID)='已验收'");
      q.includeMeta("ALL");
//      if(mgr.dataTransfered())
//         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("NECECONDLINENODATA: No data found.");
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



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","MAT_ACCEPT_LINE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
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

      headblk.addField("PROJ_NO").setLabel("MATACCEPTLINEITEM0PROJNO: Proj No").setSize(20);
      headblk.addField("ACCEPT_ID").setLabel("MATACCEPTLINEITEM0ACCEPTID: Accept Id").setSize(50);
      headblk.addField("LINE_NO").setLabel("MATACCEPTLINELINENO: Line No").setSize(20);
      headblk.addField("ARRIVE_ID").
                          setLabel("MATACCEPTLINEITEM0ARRIVEID: Arrive Id").
                          setSize(50);
      headblk.addField("ARRIVE_LINE_NO").
                          setLabel("MATACCEPTLINEARRIVELINEID: Arrive Line Id").
                          setSize(20);
      headblk.addField("CONTRACT_ID").
      setHidden().
      setLabel("MATARRIVELINEITEM0CONTRACTID: Contract Id").
      setSize(30);
    headblk.addField("MAT_NO").
                        setLabel("MATACCEPTLINEMATNO: Mat No").
                        setSize(20);

      headblk.addField("MAT_NAME").
                          setReadOnly().
                          setLabel("MATARRIVELINEMATNAME: Mat Name").
                          setFunction("mat_code_api.Get_Mat_Name(:PROJ_NO,:MAT_NO)").
                          setSize(20);

      headblk.addField("ARRIVE_QTY","Number").
                          setLabel("MATACCEPTLINEARRIVEQTY: Arrive Qty").
                          setSize(20);
      headblk.addField("ACCEPT_PRICE","Number").
                          setLabel("MATACCEPTLINEACCEPTPRICE: Accept Price").
                          setSize(20);
      headblk.addField("ACCEPT_QTY","Number").
                          setLabel("MATACCEPTLINEACCEPTQTY: Accept Qty").
                          setSize(20);
      headblk.addField("UNIT_NO").
                          setLabel("MATACCEPTLINEUNITNO: Unit No").
                          setSize(20);
      headblk.addField("UNIT_DESC").
                          setReadOnly().
                          setLabel("MATACCEPTLINEUNITDESC: Unit Desc").
                          setFunction("Iso_Unit_API.Get_Description(:UNIT_NO)").
                          setSize(20);
      headblk.addField("STOCKOUT").
                          setInsertable().
                          setLabel("MATACCEPTLINESTOCKOUT: Stockout").
                          setSize(20);
      headblk.addField("ACCEPT_STATUS").
                          setInsertable().
                          setLabel("MATACCEPTLINEACCEPTSTATUS: Accept Status").
                          setSize(20);
      headblk.addField("NOTE").
                          setInsertable().
                          setLabel("MATACCEPTLINENOTE: Note").
                          setHeight(4).
                          setSize(100);
      headblk.addField("MAT_CHECK_METHOD").
                          enumerateValues("Mat_Check_Method_API").
                          setSelectBox().
                          setInsertable().
                          setLabel("MATACCEPTLINEMATCHECKMETHOD: Mat Check Method").
                          setSize(100);
      headblk.addField("CREATE_PERSON").
                          setLabel("MATACCEPTLINEITEM0CREATEPERSON: Create Person").
                          setSize(20);
      headblk.addField("PERSON_NAME").
                          setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
                          setLabel("MATACCEPTLINEITEM0PERSONNAME: Person Name").
                          setReadOnly().
                          setSize(30);
      
      headblk.addField("CREATE_TIME","Date").
                          setLabel("MATACCEPTLINEITEM0CREATETIME: Create Time").
                          setSize(20);
      headblk.setView("MAT_ACCEPT_LINE");
      headblk.defineCommand("MAT_ACCEPT_LINE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MATACCEPTLINEITEMHEAD1: MatAcceptLine");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setDataSpan("NOTE", 5);
      headlay.setDataSpan("MAT_CHECK_METHOD", 5);

      headlay.setSimple("PERSON_NAME");
      headlay.setSimple("UNIT_DESC");
 
   }

   public void submitCondition(){
      
      mgr = getASPManager();
      ASPCommand cmdBuf; 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
     
      trans.clear();
      String standard_info = "";
      headset.storeSelections();  
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("PROJ_NO,ACCEPT_ID,LINE_NO,ACCEPT_QTY,UNIT_NO,CONTRACT_ID");
      for(int i=0;i<selected_fields.countItems();i++){         
          ASPBuffer subBuff = selected_fields.getBufferAt(i);
          standard_info=standard_info + subBuff.getValueAt(0) + "^";    
         cmdBuf = trans.addCustomCommand("CREATECONDITION"+i, "MAT_ST_ENTRY_LINE_API.Create_File");    
         cmdBuf.addParameter("PROJ_NO", subBuff.getValueAt(0));
         cmdBuf.addParameter("ARRIVE_LINE_NO", ctx.findGlobal("ENTRY_ID"));
         cmdBuf.addParameter("ACCEPT_ID", subBuff.getValueAt(1));
         cmdBuf.addParameter("LINE_NO", subBuff.getValueAt(2));
         cmdBuf.addParameter("ACCEPT_QTY", subBuff.getValueAt(3));
         cmdBuf.addParameter("UNIT_NO", subBuff.getValueAt(4));
         cmdBuf.addParameter("CONTRACT_ID", subBuff.getValueAt(5));
      }
      mgr.perform(trans);
   }

   public void  adjust()
   {
      // fill function body
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "MATACCEPTLINEITEMHEAD1: Mat Accept Line";
   }


   protected String getTitle()
   {
      return "MATACCEPTLINEITEMHEAD1: Mat Accept Line";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      
      if (!headlay.isFindLayout() && headset.countRows() > 0)
      {
         beginDataPresentation();
         printSubmitButton("SUBMIT", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCREATE: 确定"), "OnClick='Close()'");
         printSpaces(1);
         printSubmitButton("CANCEL", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCANCEL: 取消"), "OnClick='javascript:window.close();'");
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

