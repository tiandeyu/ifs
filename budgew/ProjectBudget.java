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
import ifs.genbaw.GenbawConstants;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class ProjectBudget extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.ProjectBudget");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private boolean bEmptyTree;
   private String src_str;
   private String iframe_src;
   private String budget_no;
   private String proj_no;
   private String comnd; 
   private String budget_name;
   private ASPContext ctx;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ProjectBudget (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      bEmptyTree = false;
      if( mgr.commandBarActivated() ){
         comnd = mgr.readValue("__COMMAND");         
         if  ( "HEAD.Find".equals(comnd) )
            bEmptyTree = true;   // Clear tree when in the find layout
         eval(mgr.commandBarFunction());
         }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("BUDGET_NO")) )
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
         mgr.showAlert("PROJECTBUDGETNODATA: No data found.");
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
      ASPContext ctx =  mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;
      
      cmd = trans.addEmptyCommand("HEAD","PROJECT_BUDGET_API.New__",headblk);
      cmd.setParameter("PROJ_NO", ctx.findGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT));
      cmd.setParameter("STATUS", "1");  
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

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
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().        
              setLabel("PROJECTBUDGETPROJECTTYPEID: Project No").
              setSize(30);      
      headblk.addField("PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.Get_Proj_Desc(:PROJ_NO)").
              setReadOnly().   
              setLabel("PROJECTBUDGETPROJDESC: Proj Desc").
              setSize(30);  
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("BUDGET_NO").    
              setInsertable().
              setHidden().  
              setLabel("PROJECTBUDGETBUDGETNO: Budget No").
              setSize(30);    
      headblk.addField("BUDGET_NAME").
              setInsertable().
              setLabel("PROJECTBUDGETBUDGETNAME: Budget Name").
              setSize(30);
      headblk.addField("REV").
              setInsertable().
              setLabel("PROJECTBUDGETREV: Rev").
              setSize(30);
      headblk.addField("STATUS").
              setInsertable().
              setLabel("PROJECTBUDGETSTATUS: Status").
              setCheckBox("0,1");  
      headblk.addField("CREATE_TIME","Date").
              setReadOnly().
              setHidden().
              setLabel("PROJECTBUDGETCREATETIME: Create Time").
              setSize(30);          
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setHidden().
              setLabel("PROJECTBUDGETCREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setHidden().
              setFunction("PERSON_INFO_API.Get_Name(:CREATE_PERSON)").
              setLabel("PROJECTBUDGETCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.setView("PROJECT_BUDGET");
      headblk.defineCommand("PROJECT_BUDGET_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("importBudgetTemp",mgr.translate("PROJECTBUDGETLINEIMPORTBUDGETTEMP: Import Budget Temp..."));
      headbar.addCustomCommand("importBudgetLine",mgr.translate("PROJECTBUDGETLINEIMPORTBUDGETLINE: Import Budget Line..."));
      headtbl = mgr.newASPTable(headblk);  
      headtbl.setTitle("PROJECTBUDGETTBLHEAD: Project Budget");
      headtbl.enableRowSelect(); 
      headtbl.setWrap();      
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(2);    
      headlay.setSimple("PROJ_DESC");    
      
   }

   public void importBudgetTemp(){
      ASPManager mgr = getASPManager();
      ASPCommand cmd = mgr.newASPCommand(); 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      cmd=trans.addCustomCommand("import_budget_temp", "PROJECT_BUDGET_LINE_API.Import_Budget_Temp");
      cmd.addParameter("PROJ_NO",headset.getValue("PROJ_NO"));
      cmd.addParameter("BUDGET_NO",headset.getValue("BUDGET_NO"));
      trans = mgr.perform(trans);    
   }   
  
   public void importBudgetLine() throws FndException{
      this.appendDirtyJavaScript("window.open('../contrw/ImportExcelData.page','_blank','height=300, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");
   }    
   
   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      // fill function body
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
         src_str    = "ProjectBudgetLineTree.page";
         iframe_src = "ProjectBudgetBlank.page";
      }
      else        
      {    
         budget_no    = headset.getValue("BUDGET_NO");   
         proj_no = headset.getValue("PROJ_NO");  
         budget_name =  headset.getValue("BUDGET_NAME");    
         ctx=mgr.getASPContext();
         ctx.setGlobal("BUDGET_NAME", budget_name);    
         src_str    = "ProjectBudgetLineTree.page?BUDGET_NO="+budget_no+"&PROJ_NO=" + proj_no +"&BUDGET_NAME = "+ budget_name ;
         iframe_src = "ProjectBudgetLine.page?BUDGET_NO="+budget_no+"&PROJ_NO=" + proj_no +"&BUDGET_NAME = "+ budget_name ;   
      }        
   }          
     
   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "PROJECTBUDGETDESC: Project Budget";
   }

   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;


   }

   protected String getTitle()
   {
      return "PROJECTBUDGETTITLE: Project Budget";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("PROJECTBUDGETTITLE: Project Budget"));
      out.append("</head>\n");
      out.append("<body ");      
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMB\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTIVITYCOM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PROGRESSCOM\" value=\"\">\n");
      out.append(mgr.startPresentation("PROJECTBUDGETTITLE: Project Budget"));
      out.append(headlay.show());  
      if (headlay.isSingleLayout() && headset.countRows() > 0)    
      { 
//            out.append("<frameset cols=\"30%,*\">");
//            out.append("<frame name=\"contents\" target=\"ChildMain\" src=\" " );
//            out.append(src_str);  
//            out.append("\"> </frame>\n");      
//            out.append("<frame name=\"ChildMain\" src=\"" );
//            out.append(iframe_src);   
//            out.append("\"> </frame>\n");      
//            out.append("</frameset>\n");
          out.append("<iframe name=\"contents\" target=\"ChildMain\" src=");
          out.append(src_str);
          out.append(" width=\"20%\" scrolling=\"auto\" height = \"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");  
          out.append("<iframe name =\"ChildMain\" src=");
          out.append(iframe_src);        
          out.append(" width=\"78%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");                            
      }       
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      return out;

   }
}
