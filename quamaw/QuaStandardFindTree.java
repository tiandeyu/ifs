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

package ifs.quamaw;
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

public class QuaStandardFindTree extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuaStandardFindTree");
    
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

   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay; 
   
   private String proj_no;
   private String proj_type_no;
   private String plan_no;
   private String standard_no;
   private String sub_proj_no;
   private ASPQuery q;
   private ASPContext ctx;  


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  QuaStandardFindTree (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      ctx   = mgr.getASPContext();    
      if("submitTree".equals(mgr.readValue("USERCOMMAND"))) 
         {ctx.setGlobal("USERCOMMAND", "submitTree");}
      else if("submitTree2".equals(mgr.readValue("USERCOMMAND"))) 
         {ctx.setGlobal("USERCOMMAND", "submitTree2");}
      if(!mgr.isEmpty(mgr.readValue("PROJ_NO"))&&!mgr.isEmpty(mgr.readValue("PLAN_NO"))&&!mgr.isEmpty(mgr.readValue("PROJECT_TYPE_NO"))&&!mgr.isEmpty(mgr.readValue("STANDARD_NO"))&&!mgr.isEmpty(mgr.readValue("SUB_PROJ_NO"))) {
         ctx.setGlobal("PROJ_NO", mgr.readValue("PROJ_NO"));
         ctx.setGlobal("PLAN_NO", mgr.readValue("PLAN_NO"));
         ctx.setGlobal("PROJECT_TYPE_NO", mgr.readValue("PROJECT_TYPE_NO"));
         ctx.setGlobal("SUB_PROJ_NO", mgr.readValue("SUB_PROJ_NO"));
         ctx.setGlobal("STANDARD_NO", mgr.readValue("STANDARD_NO"));
      }
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if (mgr.buttonPressed("SUBMIT")) {
         submitTree();
      }    
      else if( !mgr.isEmpty(mgr.getQueryStringValue("STANDARD_NO")) )
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
      String standardNo = "";
      String sql = "";
      proj_no = mgr.getQueryStringValue("PROJ_NO");
      plan_no = mgr.getQueryStringValue("PLAN_NO");
      proj_type_no = mgr.getQueryStringValue("PROJECT_TYPE_NO");
      
      ctx   = mgr.getASPContext();      
      ctx.setGlobal("QUA_STANDARD_PROJ_NO", proj_no);
      ctx.setGlobal("QUA_STANDARD_PLAN_NO",plan_no);
      ctx.setGlobal("QUA_STANDARD_PROJ_TYPE_NO",proj_type_no);
      
      sql = "select standard_no from quanlity_standard_line a,quanlity_plan_line b,general_project c";
      sql = sql + " where a.project_type_no = c.project_type_id";
      sql = sql + " and   b.proj_no = c.proj_no";
      sql = sql + " and   b.sub_proj_no = a.sub_proj_no";
      sql = sql + " and   b.proj_no = '" + proj_no + "'";
      sql = sql + " and   b.plan_no = '" + plan_no + "'";
      sql = sql + " and   rownum = 1  ";          
//      if(mgr.isEmpty(mgr.readValue("STANDARD_NO")))
//         standard_no = mgr.readValue("STANDARD_NO");
//         ctx.setGlobal("b.STANDARD_NO",standard_no);
//         sql = sql + " and   b.standard_no = '" + standard_no + "'";
      trans.addQuery("ITEMS",sql);
      trans = mgr.perform(trans);
      standardNo =trans.getValue("ITEMS/DATA/STANDARD_NO");
      
      trans.clear();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      if(!"".equals(standardNo)&&standardNo != null){
         q.addWhereCondition("STANDARD_NO = ?");
         q.addParameter("STANDARD_NO", standardNo);
      }      
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("QUANLITYSTANDARDNODATA: No data found.");
         headset.clear();
      }
      eval( itemset.syncItemSets() );
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
            String standardNo = "";
      String sql = "";
      proj_no = mgr.getQueryStringValue("PROJ_NO");
      plan_no = mgr.getQueryStringValue("PLAN_NO");
      proj_type_no = mgr.getQueryStringValue("PROJECT_TYPE_NO");
      
      ctx   = mgr.getASPContext();      
      ctx.setGlobal("QUA_STANDARD_PROJ_NO", proj_no);
      ctx.setGlobal("QUA_STANDARD_PLAN_NO",plan_no);
      ctx.setGlobal("QUA_STANDARD_PROJ_TYPE_NO",proj_type_no);
      
      sql = "select standard_no from quanlity_standard_line a,quanlity_plan_line b,general_project c";
      sql = sql + " where a.project_type_no = c.project_type_id";
      sql = sql + " and   b.proj_no = c.proj_no";
      sql = sql + " and   b.sub_proj_no = a.sub_proj_no";
      sql = sql + " and   b.proj_no = '" + proj_no + "'";
      sql = sql + " and   b.plan_no = '" + plan_no + "'";
//      if(mgr.isEmpty(mgr.readValue("STANDARD_NO")))
//         sql = sql + " and   b.standard_no = '" + standard_no + "'";
      sql = sql + " and   rownum = 1  ";          
      trans.addQuery("ITEMS",sql);
      trans = mgr.perform(trans);
      standardNo =trans.getValue("ITEMS/DATA/STANDARD_NO");
      
      trans.clear();
      ASPQuery q;

      q = trans.addQuery(headblk);
      if(!"".equals(standardNo)&&standardNo != null){
         q.addWhereCondition("STANDARD_NO = ?");
         q.addParameter("STANDARD_NO", standardNo);
      }    
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

      cmd = trans.addEmptyCommand("HEAD","QUANLITY_STANDARD_API.New__",headblk);
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
      ctx   = mgr.getASPContext();
      ctx.setGlobal("proj_no",ctx.getGlobal("QUA_STANDARD_PROJ_NO"));
      ctx.setGlobal("plan_no",ctx.getGlobal("QUA_STANDARD_PLAN_NO"));
      ctx.setGlobal("proj_type_no",ctx.getGlobal("QUA_STANDARD_PROJ_TYPE_NO"));
      ASPQuery q;  
      int headrowno;  
        
      q = trans.addQuery(itemblk);
      q.addWhereCondition("PROJECT_TYPE_NO = ? AND STANDARD_NO = ?");
      q.addParameter("PROJECT_TYPE_NO", headset.getValue("PROJECT_TYPE_NO"));
      q.addParameter("STANDARD_NO", headset.getValue("STANDARD_NO"));
      q.addWhereCondition("PROJECT_TYPE_NO = ?");
      q.addParameter("PROJECT_TYPE_NO", headset.getValue("PROJECT_TYPE_NO"));
      q.addWhereCondition("NOT EXISTS (SELECT 1 FROM QUANLITY_PLAN_LINE"
               + " WHERE QUANLITY_PLAN_LINE.SUB_PROJ_NO = QUANLITY_STANDARD_LINE.SUB_PROJ_NO"
               + " AND QUANLITY_PLAN_LINE.PLAN_NO = '" + ctx.readValue("plan_no") + "')");
      q.addOrderByClause("SUB_PROJ_NO");         
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","QUANLITY_STANDARD_LINE_API.New__",itemblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJECT_TYPE_NO", headset.getValue("PROJECT_TYPE_NO"));
      cmd.setParameter("ITEM0_STANDARD_NO", headset.getValue("STANDARD_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset.addRow(data);
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
      headblk.addField("PROJECT_TYPE_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("PROJECT_TYPE",600,450).
              setLabel("QUANLITYSTANDARDPROJECTTYPENO: Project Type No").
              setSize(20);
      headblk.addField("STANDARD_NO").
              setMandatory().
              setInsertable().
//              setDynamicLOV("QUA_CRITERION").
              setLabel("QUANLITYSTANDARDSTANDARDNO: Standard No").
              setSize(50);
      headblk.addField("STANDARD_DESC").
              setFunction("Qua_Criterion_Api.Get_Description(:PROJECT_TYPE_NO,:STANDARD_NO)").
              setLabel("QUANLITYSTANDARDSTANDARDDESC: Standard Desc").
              setReadOnly();      
      mgr.getASPField("STANDARD_NO").setValidation("STANDARD_DESC");
      
      headblk.setView("QUANLITY_STANDARD");
      headblk.defineCommand("QUANLITY_STANDARD_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("submitTree",mgr.translate("QUANLITYPLANSTART: Submit Tree Node..."));
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("QUANLITYSTANDARDLINETBLHEAD: Quanlity Standard Lines");
//      headtbl.enableRowSelect();       
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
 


      itemblk = mgr.newASPBlock("ITEM1");
      itemblk.addField("ITEM_PROJECT_TYPE_NO").
              setDbName("PROJECT_TYPE_NO").
              setMandatory().
              setInsertable().
              setHidden().
              setLabel("QUANLITYSTANDARDLINEPROJECTTYPENO: Project Type No").
              setSize(20);
      itemblk.addField("ITEM_STANDARD_NO").
              setDbName("STANDARD_NO").
              setMandatory().
              setHidden().
              setInsertable().
              setLabel("QUANLITYSTANDARDLINESTANDARDNO: Standard No").
              setSize(50);
      itemblk.addField("SUB_PROJ_NAME").
              setInsertable().
              setLabel("QUANLITYSTANDARDLINESUBPROJNAME: Sub Proj Name").
              setSize(50).
              setMaxLength(100);  
      itemblk.addField("SUB_PROJ_NO").
              setReadOnly().
              setLabel("QUANLITYSTANDARDLINESUBPROJNO: Sub Proj No").
              setSize(50);         
      itemblk.addField("PARENT_PROJ_NO").
              setReadOnly().
              setLabel("QUANLITYSTANDARDLINEPARENTPROJNO: Parent Proj No").
              setSize(50);
      itemblk.addField("QUA_CLASS_NO").
              setInsertable().
              setDynamicLOV("QUA_PROJECT_CLASS","ITEM_PROJECT_TYPE_NO").
              setLabel("QUANLITYSTANDARDLINEQUACLASSNO: Qua Class No").
              setSize(20).
              setMaxLength(50);
      itemblk.addField("QUA_CLASS_DESC").
              setFunction("QUA_PROJECT_CLASS_API.Get_Description(:ITEM_PROJECT_TYPE_NO,:QUA_CLASS_NO)").
              setLabel("QUANLITYSTANDARDLINEQUACLASSDESC: Qua Class Desc").
              setReadOnly();      
      mgr.getASPField("QUA_CLASS_NO").setValidation("QUA_CLASS_DESC");
      itemblk.addField("QUA_MODE_NO").
              setInsertable().
              setDynamicLOV("QUA_CONTROL_MODE").
              setLabel("QUANLITYSTANDARDLINEQUAMODENO: Qua Mode No").
              setSize(20).
              setMaxLength(50);
      itemblk.addField("QUA_MODE_DESC").
              setFunction("QUA_CONTROL_MODE_API.Get_Description(:QUA_MODE_NO)").
              setLabel("QUANLITYSTANDARDLINEQUAMODEDESC: Qua Mode No").
              setReadOnly();      
      mgr.getASPField("QUA_MODE_NO").setValidation("QUA_MODE_DESC");
      itemblk.setView("QUANLITY_STANDARD_LINE");
      itemblk.defineCommand("QUANLITY_STANDARD_LINE_API","");
      itemblk.setMasterBlock(headblk);        
      itemset = itemblk.getASPRowSet();        
      itembar = mgr.newASPCommandBar(itemblk);
      itembar.defineCommand(itembar.OKFIND, "okFindITEM1");
      itembar.defineCommand(itembar.NEWROW, "newRowITEM1");
      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setTitle("QUANLITYSTANDARDLINEITEMHEAD1: itemblk");
      itemtbl.enableRowSelect();
      itemtbl.setWrap();
      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
   }

 	public void submitTree(){
 	  ASPManager mgr = getASPManager();
      ASPCommand cmdBuf;       
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
     
      trans.clear();

      itemset.storeSelections();  
      if (itemlay.isSingleLayout())
         itemset.selectRow();
      ASPBuffer selected_fields=itemset.getSelectedRows("STANDARD_NO,SUB_PROJ_NO");
      if("submitTree".equals((ctx.getGlobal("USERCOMMAND")))){
      for(int i=0;i<selected_fields.countItems();i++){         
          ASPBuffer subBuff = selected_fields.getBufferAt(i);
          String standard_info = "";
          standard_info=standard_info + subBuff.getValueAt(0) + "|";
          standard_info=standard_info + subBuff.getValueAt(1) ;
          cmdBuf = trans.addCustomCommand("CREATETREE"+i, "QUANLITY_PLAN_LINE_API.Create_Qualine_Tree");
          cmdBuf.addParameter("OBJID", ctx.getGlobal("proj_no"));
          cmdBuf.addParameter("OBJVERSION", ctx.getGlobal("plan_no"));     
          cmdBuf.addParameter("PROJECT_TYPE_NO", ctx.getGlobal("proj_type_no"));
          cmdBuf.addParameter("STANDARD_NO", standard_info);
          trans=mgr.perform(trans);
          
          trans.clear();
      }
      }else if("submitTree2".equals((ctx.getGlobal("USERCOMMAND")))){
         for(int i=0;i<selected_fields.countItems();i++){         
            ASPBuffer subBuff = selected_fields.getBufferAt(i);
         cmdBuf = trans.addCustomCommand("CREATETREE"+i, "QUANLITY_PLAN_LINE_API.Create_Qualine_Tree2");
         cmdBuf.addParameter("PARENT_PROJ_NO", ctx.getGlobal("PROJ_NO"));
         cmdBuf.addParameter("QUA_MODE_NO", ctx.getGlobal("PLAN_NO"));     
         cmdBuf.addParameter("SUB_PROJ_NO", ctx.getGlobal("SUB_PROJ_NO"));
         cmdBuf.addParameter("PROJECT_TYPE_NO", ctx.getGlobal("PROJECT_TYPE_NO"));
         cmdBuf.addParameter("STANDARD_NO",subBuff.getValueAt(0));
         cmdBuf.addParameter("SUB_PROJ_NAME",subBuff.getValueAt(1));
         trans=mgr.perform(trans);
         trans.clear();
         }
      }
//      cmdBuf = trans.addCustomCommand("CREATETREE", "QUANLITY_PLAN_LINE_API.Create_Qualine_Tree");
//      cmdBuf.addParameter("OBJID", ctx.getGlobal("proj_no"));
//      cmdBuf.addParameter("OBJVERSION", ctx.getGlobal("plan_no"));     
//      cmdBuf.addParameter("PROJECT_TYPE_NO", ctx.getGlobal("proj_type_no"));
//      cmdBuf.addParameter("STANDARD_NO", standard_info); 
//      mgr.perform(trans);
      
   }

   public void  adjust()
   {
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
//      if(headset.countRows()==1)  
//         headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
   }  

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "QUANLITYSTANDARDLINEDESC: Quanlity Standard Line";
   }


   protected String getTitle()    
   {
      return "QUANLITYSTANDARDLINETITLE: Quanlity Standard Line";
   }    


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      if (itemlay.isVisible())
          appendToHTML(itemlay.show());    
      if (!headlay.isFindLayout() && headset.countRows() > 0)
      {
         beginDataPresentation();
         printSubmitButton("SUBMIT", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCREATE: ȷ��"), "OnClick='Close()'");
         printSpaces(1);
         printSubmitButton("CANCEL", mgr.translate("DOCCTWDOCISSUEDISTFILESDLGCANCEL: ȡ��"), "OnClick='javascript:window.close();'");
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
