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

public class ProjectBudgetTemp extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.ProjectBudgetTemp");

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
   private String temp_no;
   private String project_type_id;
   private String comnd; 
   private ASPContext ctx;
   private String temp_desc;
   

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ProjectBudgetTemp (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("TEMP_NO")) )
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
         mgr.showAlert("PROJECTBUDGETTEMPNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","PROJECT_BUDGET_TEMP_API.New__",headblk);
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
      headblk.addField("PROJECT_TYPE_ID").
              setMandatory().
              setDynamicLOV("PROJECT_TYPE").
              setInsertable().    
              setLabel("PROJECTBUDGETTEMPPROJECTTYPEID: Project Type Id").
              setSize(30);
      headblk.addField("PROJECT_TYPE_NAME").
              setFunction("PROJECT_TYPE_API.Get_Project_type_Name(:PROJECT_TYPE_ID)").
              setReadOnly().   
              setLabel("PROJECTBUDGETTEMPPROJECTTYPENAME: Project Type Name").
              setSize(30);
      mgr.getASPField("PROJECT_TYPE_ID").setValidation("PROJECT_TYPE_NAME");
      headblk.addField("TEMP_NO").  
              setInsertable().
              setHidden().  
              setLabel("PROJECTBUDGETTEMPTEMPNO: Temp No").
              setSize(30);    
      headblk.addField("TEMP_DESC").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPTEMPDESC: Temp Desc").
              setSize(30);
      headblk.addField("REV").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPREV: Rev").
              setSize(30);
      headblk.addField("STATUS").
              setInsertable().
              setLabel("PROJECTBUDGETTEMPSTATUS: Status").
              setCheckBox("0,1");      
      headblk.addField("CREATE_TIME","Date").
              setReadOnly().
              setHidden().
              setLabel("PROJECTBUDGETTEMPCREATETIME: Create Time").
              setSize(30);        
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setHidden().
              setLabel("PROJECTBUDGETTEMPCREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
              setReadOnly().
              setHidden().
              setFunction("PERSON_INFO_API.Get_Name(:CREATE_PERSON)").
              setLabel("PROJECTBUDGETTEMPCREATEPERSONNAME: Create Person Name").
              setSize(30);
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.setView("PROJECT_BUDGET_TEMP");
      headblk.defineCommand("PROJECT_BUDGET_TEMP_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("importBudgetTempLine",mgr.translate("PROJECTBUDGETLINEIMPORTBUDGETTEMP: Import Budget Temp Line..."));
      headtbl = mgr.newASPTable(headblk);  
      headtbl.setTitle("PROJECTBUDGETTEMPTBLHEAD: Project Budget Temps");
      headtbl.enableRowSelect();  
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(2);    
      headlay.setSimple("PROJECT_TYPE_NAME");

   }

   public void importBudgetTempLine() throws FndException{
      this.appendDirtyJavaScript("window.open('../contrw/ImportExcelData.page','_blank','height=300, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");
   }                   
      
   public void  adjust()
   {
      // fill function body
      ASPManager mgr = getASPManager();
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
         src_str    = "ProjectBudgetTempLineTree.page";
         iframe_src = "ProjectBudgetBlank.page";
      }
      else        
      {    
         temp_no    = headset.getValue("TEMP_NO");   
         project_type_id = headset.getValue("PROJECT_TYPE_ID");   
         temp_desc =  headset.getValue("TEMP_DESC");  
         ctx=mgr.getASPContext();
         ctx.setGlobal("TEMP_DESC", temp_desc);   
         src_str    = "ProjectBudgetTempLineTree.page?TEMP_NO="+temp_no+"&PROJECT_TYPE_ID=" + project_type_id ;
         iframe_src = "ProjectBudgetTempLine.page?TEMP_NO="+temp_no+"&PROJECT_TYPE_ID=" + project_type_id;
      }
   }    

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "PROJECTBUDGETTEMPDESC: Project Budget Temp";
   }

   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;


   }

   protected String getTitle()
   {
      return "PROJECTBUDGETTEMPTITLE: Project Budget Temp";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("PROJECTBUDGETTEMPTITLE: Project Budget Temp"));
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
      out.append(mgr.startPresentation("PROJECTBUDGETTEMPTITLE: Project Budget Temp"));
      out.append(headlay.show());  
      if (headlay.isSingleLayout() && headset.countRows() > 0)
      { 
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