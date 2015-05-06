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

package ifs.budgew;
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

public class ProjectBudgetTempLine extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.ProjectBudgetTempLine");
      
   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock sub_budget_project_blk;    
   private ASPRowSet sub_budget_project_set;
   private ASPCommandBar sub_budget_project_bar;
   private ASPTable sub_budget_project_tbl;
   private ASPBlockLayout sub_budget_project_lay;
   private boolean bRefreshTree;
   private String comnd;
   private ASPTabContainer tabs;
   private ASPContext ctx;  

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ProjectBudgetTempLine (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      String project_type_id =  mgr.readValue("PROJECT_TYPE_ID");
      String temp_no = mgr.readValue("TEMP_NO");  
      String budget_line_no =  mgr.readValue("BUDGET_LINE_NO");
      System.out.println("project_type_id1111111111111===================="+project_type_id);
      System.out.println("temp_no1111111111===================="+temp_no);
      System.out.println("budget_line_no111111111===================="+budget_line_no);
      ctx = mgr.getASPContext();      
      bRefreshTree      = false;       
      if( mgr.commandBarActivated() ){
         comnd = mgr.readValue("__COMMAND");
         if (( "MAIN.SaveReturn".equals(comnd) )|| ( "ITEM1.SaveReturn".equals(comnd) )|| ( "ITEM1.Delete".equals(comnd) )|| ( "ITEM2.SaveReturn".equals(comnd) )|| ( "ITEM2.SaveNew".equals(comnd) )|| ( "ITEM2.Delete".equals(comnd) ))
         {
            bRefreshTree = true;
         }
         else if ( "MAIN.SaveNew".equals(comnd) ||"ITEM1.SaveNew".equals(comnd))
         {
            bRefreshTree = true;  
         }
         eval(mgr.commandBarFunction());
         
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(project_type_id) || !mgr.isEmpty(temp_no)){
         System.out.println("project_type_id2222222222222222===================="+project_type_id);
         System.out.println("temp_no22222222222222222===================="+temp_no);
         System.out.println("budget_line_no222222222222222===================="+budget_line_no);
         ctx.setGlobal("PROJECT_TYPE_ID", project_type_id);
         ctx.setGlobal("TEMP_NO", temp_no);  
         if(!mgr.isEmpty(budget_line_no) && !"".equals(budget_line_no)) 
         ctx.setGlobal("BUDGET_LINE_NO", budget_line_no);    
         else         
         ctx.setGlobal("BUDGET_LINE_NO", "1");       
         okFind();    
      }       
      else
         okFind();
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
      String project_type_id = ctx.findGlobal("PROJECT_TYPE_ID")==null?"":ctx.findGlobal("PROJECT_TYPE_ID");
      String temp_no = ctx.findGlobal("TEMP_NO")==null?"":ctx.findGlobal("TEMP_NO");
      String budget_line_no = ctx.findGlobal("BUDGET_LINE_NO")==null?"":ctx.findGlobal("BUDGET_LINE_NO");
      ASPQuery q;    
      mgr.createSearchURL(headblk);    
      q = trans.addQuery(headblk);
      if (!mgr.isEmpty(project_type_id) ){
          q.addWhereCondition("PROJECT_TYPE_ID = ?");
          q.addParameter("PROJECT_TYPE_ID",project_type_id); 
          System.out.println("project_type_id3333333333333333333333 ======= "+project_type_id);
      } 
      if (!mgr.isEmpty(temp_no) ){
          q.addWhereCondition("TEMP_NO = ? " );
          q.addParameter("TEMP_NO",temp_no);
          System.out.println("temp_no33333333333333333333333333 ======"+temp_no);
      }
      if (!mgr.isEmpty(budget_line_no)){
          q.addWhereCondition("BUDGET_LINE_NO = ? ");
          q.addParameter("BUDGET_LINE_NO" , budget_line_no);  
          System.out.println("budget_line_no333333333333333333333333 ======"+budget_line_no);    
      }else{
         q.addWhereCondition("PARENT_NO IS NULL");
      }  
      q.setOrderByClause("BUDGET_LINE_NO");
      q.includeMeta("ALL");  
      if(mgr.dataTransfered())
          q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if(headset.countRows()>0)   
      ctx.setGlobal("BUDGET_LINE_NO", headset.getValue("BUDGET_LINE_NO")); 
      if (  headset.countRows() == 0 )
      {      
         mgr.showAlert("PROJECTBUDGETTEMPLINENODATA: No data found.");
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
      cmd = trans.addEmptyCommand("HEAD","PROJECT_BUDGET_TEMP_LINE_API.New__",headblk);
      cmd.setParameter("PROJECT_TYPE_ID", ctx.findGlobal("PROJECT_TYPE_ID"));
      cmd.setParameter("TEMP_NO", ctx.findGlobal("TEMP_NO"));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void newRowItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      cmd = trans.addEmptyCommand("ITEM1","PROJECT_BUDGET_TEMP_LINE_API.New__",sub_budget_project_blk);
      cmd.setParameter("ITEM1_PROJECT_TYPE_ID", ctx.findGlobal("PROJECT_TYPE_ID"));
      cmd.setParameter("ITEM1_TEMP_NO", ctx.findGlobal("TEMP_NO"));  
      cmd.setParameter("ITEM1_PARENT_NO", ctx.findGlobal("BUDGET_LINE_NO"));
      cmd.setOption("ACTION","PREPARE");    
      trans = mgr.perform(trans);    
      data = trans.getBuffer("ITEM1/DATA");  
      sub_budget_project_set.addRow(data);  
   }
   
   public void okFindItem1()  
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String project_type_id = ctx.findGlobal("PROJECT_TYPE_ID");
      String temp_no = ctx.findGlobal("TEMP_NO");
      String budget_line_no = ctx.findGlobal("BUDGET_LINE_NO");
      ASPQuery q;
      mgr.createSearchURL(sub_budget_project_blk);
      q = trans.addQuery(sub_budget_project_blk);
      if (!mgr.isEmpty(project_type_id) ){
          q.addWhereCondition("PROJECT_TYPE_ID = ?");
          q.addParameter("ITEM1_PROJECT_TYPE_ID",project_type_id); 
      }     
      if (!mgr.isEmpty(temp_no) ){
          q.addWhereCondition("TEMP_NO = ? " );
          q.addParameter("ITEM1_TEMP_NO",temp_no);
      }  
      if (!mgr.isEmpty(budget_line_no)){
          q.addWhereCondition("budget_line_no IN (SELECT budget_line_no from IFSAPP.PROJECT_BUDGET_TEMP_LINE WHERE TEMP_NO = ? AND  PROJECT_TYPE_ID = ? START WITH parent_no  = ? connect by prior budget_line_no = parent_no) ");
          q.addParameter("ITEM1_TEMP_NO",temp_no);
          q.addParameter("ITEM1_PROJECT_TYPE_ID",project_type_id);    
          q.addParameter("ITEM1_PARENT_NO" , budget_line_no);  
      }                            
      q.addOrderByClause("SEQ_NO,BUDGET_LINE_NO"); 
      q.includeMeta("ALL");  
      if(mgr.dataTransfered())
          q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,sub_budget_project_blk);
      if (  sub_budget_project_set.countRows() == 0 )
      {
         mgr.showAlert("PROJECTBUDGETTEMPLINENODATA: No data found.");
         sub_budget_project_set.clear();
      }
   }

   public void countFindItem1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(sub_budget_project_blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      sub_budget_project_lay.setCountValue(toInt(sub_budget_project_set.getValue("N")));
      sub_budget_project_set.clear();
   }

   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();      
      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PROJECT_TYPE_ID").
              setMandatory().
              setInsertable().
              setHidden().
              setLabel("PROJECTBUDGETTEMPLINEPROJECTTYPEID: Project Type Id").
              setSize(20);
      headblk.addField("TEMP_NO").
              setMandatory().
              setInsertable().
              setHidden().
              setLabel("PROJECTBUDGETTEMPLINETEMPNO: Temp No").
              setSize(50);
      headblk.addField("BUDGET_LINE_NO").
              setMandatory().
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINEBUDGETLINENO: Budget Line No").
              setSize(30);
      headblk.addField("PRE_CODE").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINEPRECODE: Pre Code").
              setSize(30);
      headblk.addField("BUDGET_NAME").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINEBUDGETNAME: Budget Name").
              setSize(120);    
      headblk.addField("PARENT_NO").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINEPARENTNO: Parent No").
              setSize(30);
      headblk.addField("SEQ_NO").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINESEQNO: Seq No").
              setSize(30);
      headblk.addField("TREE_LEVEL").
              setReadOnly().  
              setLabel("PROJECTBUDGETTEMPLINETREELEVEL: Tree Level").
              setSize(30);
      headblk.addField("BUDGET_TYPE").
              enumerateValues("Budget_Type_API").
              setSelectBox().
              setMandatory().
              setInsertable().
              setLabel("PROJECTBUDGETTEMPLINEBUDGETTYPE: Budget Type").
              setSize(20);  
      headblk.addField("UNIT_CODE").
              setInsertable().
              setDynamicLOV("ISO_UNIT").
              setLabel("PROJECTBUDGETTEMPLINEUNITCODE: Unit Code").
              setSize(30);
      headblk.addField("UNIT_CODE_DESC").
              setReadOnly().
              setFunction("ISO_UNIT_API.Get_Description(:UNIT_CODE)"). 
              setLabel("PROJECTBUDGETTEMPLINEUNITCODE: Unit Code").
              setSize(30);
      mgr.getASPField("UNIT_CODE").setValidation("UNIT_CODE_DESC");    
      headblk.addField("REPORT_TYPE_ID").
              setDynamicLOV("BUDGET_REPORT_TYPE","PROJECT_TYPE_ID").  
              setInsertable().         
              setLabel("PROJECTBUDGETTEMPLINEREPORTTYPEID: Report Type Id").
              setSize(30);
      headblk.addField("REPORT_TYPE_NAME").
              setFunction("BUDGET_REPORT_TYPE_API.Get_Report_Type_Desc(:PROJECT_TYPE_ID,:REPORT_TYPE_ID)").
              setReadOnly().    
              setLabel("PROJECTBUDGETTEMPLINEREPORTTYPENAME: Report Type NAME").
              setSize(30);
      mgr.getASPField("REPORT_TYPE_ID").setValidation("REPORT_TYPE_NAME");
      headblk.addField("SPECIAL_NO").
              setInsertable().
              setDynamicLOV("BUDGET_SPECIAL_TYPE").
              setLabel("PROJECTBUDGETTEMPLINESPECIALNO: Special No").
              setSize(30);
      headblk.addField("SPECIAL_NAME").
              setReadOnly().
              setFunction("BUDGET_SPECIAL_TYPE_API.Get_Special_Name(:SPECIAL_NO)").
              setLabel("PROJECTBUDGETTEMPLINESPECIALNAME: Special NAME").
              setSize(30);  
      mgr.getASPField("SPECIAL_NO").setValidation("SPECIAL_NAME");  
      headblk.setView("PROJECT_BUDGET_TEMP_LINE");
      headblk.defineCommand("PROJECT_BUDGET_TEMP_LINE_API","Modify__,Remove__");  
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("activateSubBudgetProject", "Sub Budget Project...");  
      headbar.addCustomCommand("activateBudgetProject", "Budget Project...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PROJECTBUDGETTEMPLINETBLHEAD: Project Budget Temp Lines");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.setDialogColumns(2);
      headlay.setDataSpan("BUDGET_NAME", 8); 
      headlay.setSimple("SPECIAL_NAME");
      headlay.setSimple("REPORT_TYPE_NAME");
      headlay.setSimple("UNIT_CODE_DESC");    
      //-----------------------------------------------------------------------------
      //------------------------  sub project budget  ---------------------------
      //-----------------------------------------------------------------------------
      
      sub_budget_project_blk = mgr.newASPBlock("ITEM1");
      sub_budget_project_blk.addField("ITEM1_OBJID").
                             setDbName("OBJID").
                             setHidden();
      sub_budget_project_blk.addField("ITEM1_OBJVERSION").
                             setDbName("OBJVERSION").
                             setHidden();
      sub_budget_project_blk.addField("ITEM1_PROJECT_TYPE_ID").
                             setDbName("PROJECT_TYPE_ID").
                             setMandatory().
                             setInsertable().
                             setHidden().
                             setLabel("PROJECTBUDGETTEMPLINEPROJECTTYPEID: Project Type Id").
                             setSize(50);
      sub_budget_project_blk.addField("ITEM1_TEMP_NO").
                             setDbName("TEMP_NO").
                             setMandatory().
                             setHidden().
                             setInsertable().
                             setLabel("PROJECTBUDGETTEMPLINETEMPNO: Temp No").
                             setSize(30); 
      sub_budget_project_blk.addField("ITEM1_PRE_CODE").
                             setDbName("PRE_CODE").
                             setInsertable().
                             setLabel("PROJECTBUDGETTEMPLINEPRECODE: Pre Code").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_BUDGET_LINE_NO").
                             setDbName("BUDGET_LINE_NO").
                             setMandatory().
                             setInsertable().
                             setLabel("PROJECTBUDGETTEMPLINEBUDGETLINENO: Budget Line No").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_BUDGET_NAME").
                             setDbName("BUDGET_NAME").
                             setInsertable().
                             setLabel("PROJECTBUDGETTEMPLINEBUDGETNAME: Budget Name").
                             setSize(120);
      sub_budget_project_blk.addField("ITEM1_PARENT_NO").
                             setDbName("PARENT_NO").
                             setReadOnly().
                             setLabel("PROJECTBUDGETTEMPLINEPARENTNO: Parent No").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_BUDGET_TYPE").
                             enumerateValues("Budget_Type_API").
                             setDbName("BUDGET_TYPE").
                             setSelectBox().  
                             setMandatory().
                             setInsertable().
                             setLabel("PROJECTBUDGETTEMPLINEBUDGETTYPE: Budget Type").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_UNIT_CODE").
                             setDbName("UNIT_CODE").
                             setInsertable().
                             setDynamicLOV("ISO_UNIT").  
                             setLabel("PROJECTBUDGETTEMPLINEUNITCODE: Unit Code").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_UNIT_CODE_DESC").
                             setReadOnly().
                             setFunction("ISO_UNIT_API.Get_Description(:UNIT_CODE)"). 
                             setLabel("PROJECTBUDGETTEMPLINEUNITCODE: Unit Code").
                             setSize(30);
      mgr.getASPField("ITEM1_UNIT_CODE").setValidation("ITEM1_UNIT_CODE_DESC");   
      sub_budget_project_blk.addField("ITEM1_REPORT_TYPE_ID").
                             setDbName("REPORT_TYPE_ID").
                             setDynamicLOV("BUDGET_REPORT_TYPE").    
                             setLOVProperty("PROJECT_TYPE_ID", ctx.findGlobal("PROJECT_TYPE_ID")).
                             setInsertable().    
                             setLabel("PROJECTBUDGETTEMPLINEREPORTTYPEID: Report Type Id").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_REPORT_TYPE_NAME").
                             setFunction("BUDGET_REPORT_TYPE_API.Get_Report_Type_Desc(:ITEM1_PROJECT_TYPE_ID,:ITEM1_REPORT_TYPE_ID)").
                             setReadOnly().    
                             setLabel("PROJECTBUDGETTEMPLINEREPORTTYPENAME: Report Type NAME").
                             setSize(30);
      mgr.getASPField("ITEM1_REPORT_TYPE_ID").setValidation("ITEM1_REPORT_TYPE_NAME");
      sub_budget_project_blk.addField("ITEM1_SPECIAL_NO").
                             setDbName("SPECIAL_NO").
                             setInsertable().
                             setDynamicLOV("BUDGET_SPECIAL_TYPE").
                             setLabel("PROJECTBUDGETTEMPLINESPECIALNO: Special No").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_SPECIAL_NAME").
                             setReadOnly().
                             setFunction("BUDGET_SPECIAL_TYPE_API.Get_Special_Name(:ITEM1_SPECIAL_NO)").
                             setLabel("PROJECTBUDGETTEMPLINESPECIALNAME: Special Name").
                             setSize(30);  
      mgr.getASPField("ITEM1_SPECIAL_NO").setValidation("ITEM1_SPECIAL_NAME");  
      sub_budget_project_blk.addField("ITEM1_SEQ_NO").
                             setDbName("SEQ_NO").
                             setInsertable().  
                             setLabel("PROJECTBUDGETTEMPLINESEQNO: Seq No").
                             setSize(30);
      sub_budget_project_blk.addField("ITEM1_TREE_LEVEL").
                             setDbName("TREE_LEVEL").
                             setReadOnly().
                             setLabel("PROJECTBUDGETTEMPLINETREELEVEL: Tree Level").
                             setSize(20);
      sub_budget_project_blk.setView("PROJECT_BUDGET_TEMP_LINE");
      sub_budget_project_blk.defineCommand("PROJECT_BUDGET_TEMP_LINE_API","New__,Modify__,Remove__");
      sub_budget_project_set = sub_budget_project_blk.getASPRowSet();
      sub_budget_project_bar = mgr.newASPCommandBar(sub_budget_project_blk);
      sub_budget_project_bar.defineCommand(sub_budget_project_bar.OKFIND,"okFindItem1");
      sub_budget_project_bar.defineCommand(sub_budget_project_bar.NEWROW,"newRowItem1");
      sub_budget_project_bar.defineCommand(sub_budget_project_bar.COUNTFIND,"countFindItem1");
      sub_budget_project_tbl = mgr.newASPTable(sub_budget_project_blk);
      sub_budget_project_tbl.setTitle("PROJECTBUDGETTEMPLINETBLHEAD: Project Budget Temp Lines");
      sub_budget_project_tbl.enableRowSelect();  
      sub_budget_project_tbl.setWrap();
      sub_budget_project_lay = sub_budget_project_blk.getASPBlockLayout();
      sub_budget_project_lay.setDefaultLayoutMode(sub_budget_project_lay.MULTIROW_LAYOUT);
      sub_budget_project_lay.setDialogColumns(2);
      sub_budget_project_lay.setDataSpan("ITEM1_BUDGET_NAME", 8); 
      sub_budget_project_lay.setSimple("ITEM1_SPECIAL_NAME");
      sub_budget_project_lay.setSimple("ITEM1_REPORT_TYPE_NAME");
      sub_budget_project_lay.setSimple("ITEM1_UNIT_CODE_DESC");  
     
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("PROJECTBUDGET: Budget Project"), "javascript:commandSet('MAIN.activateBudgetProject','')");
      tabs.addTab(mgr.translate("SUBPROJECTBUDGET: Sub Budget Project"), "javascript:commandSet('MAIN.activateSubBudgetProject','')");
      tabs.setContainerWidth(700); 
      tabs.setLeftTabSpace(1);  
      tabs.setContainerSpace(5);  
      tabs.setTabWidth(100);  
      
   }



   public void  adjust()
   {
      // fill function body
      ASPManager mgr = getASPManager();
      headbar.removeCustomCommand("activateSubBudgetProject");
      headbar.removeCustomCommand("activateBudgetProject");
     
   }

   
   public void activateBudgetProject()
   {   
      tabs.setActiveTab(1);
      okFind(); 
   }

   public void activateSubBudgetProject()
   {   
      tabs.setActiveTab(2);
      okFindItem1();

   }
   
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return null;   
   }  


   protected String getTitle()      
   {
      return null;   
   }  
       
 
   protected AutoString getContents() throws FndException{
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(""));
      out.append("</head>\n");  
      out.append("<body ");      
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("\n");          
      if(tabs!=null&&out!=null){
         out.append(tabs.showTabsInit());
         if (tabs.getActiveTab() == 1 ){
             out.append(headlay.show());
         } else if (tabs.getActiveTab() == 2 ){
             out.append(sub_budget_project_lay.show());   
         } 
         out.append(tabs.showTabsFinish());
         }             
      appendDirtyJavaScript("function refreshTree()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   parent_url = this.parent.location.href;\n");
      appendDirtyJavaScript("  url_str = this.parent.frames[\"contents\"].location.href;\n");  
      appendDirtyJavaScript("        this.parent.frames[\"contents\"].location.href = url_str;\n");
      appendDirtyJavaScript("}\n");    
      if (bRefreshTree)
      {          
         appendDirtyJavaScript("   refreshTree(");
         // XSS_Safe DINHLK 20070808
         appendDirtyJavaScript(");\n");    
         bRefreshTree = false;       
      }  
      out.append(mgr.endPresentation());
      out.append("</form>\n");  
      out.append("</body>\n");
      out.append("</html>");
      return out;  
 }
}
  