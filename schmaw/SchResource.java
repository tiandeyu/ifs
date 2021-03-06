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

package ifs.schmaw;
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

public class SchResource extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchResource");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private String src_str;  
   private boolean bEmptyTree;
   private String comnd;
   private String resource_no;
   private String project_no;
   private String standard_info;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  SchResource (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("RESOURCE_NO")) )
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
         mgr.showAlert("SCHRESOURCENODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","SCH_RESOURCE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
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
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("SCHRESOURCEPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
              setLabel("SCHRESOURCEPROJDESC: Proj Desc").
              setReadOnly().
              setSize(30);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      headblk.addField("RESOURCE_NO").
              setMandatory().
              setInsertable().
              setLabel("SCHRESOURCERESOURCENO: Resource No").
              setSize(20);
      headblk.addField("RESOURCE_NAME").
              setInsertable().
              setLabel("SCHRESOURCERESOURCENAME: Resource Name").
              setSize(20);
      headblk.addField("CREATE_PERSON").
              setDynamicLOV("PERSON_INFO").
              setDefaultNotVisible().
              setLabel("SCHRESOURCECREATEPERSON: Create Person").
              setSize(20);
      headblk.addField("CREATE_PERSON_NAME").
              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
              setLabel("SCHRESOURCECREATEPERSONNAME: Create Person Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
//              setInsertable().
              setReadOnly().
              setReadOnly().
              setLabel("SCHRESOURCECREATETIME: Create Time").
              setSize(20);
      headblk.addField("STATUS").
              setReadOnly().
              setLabel("SCHRESOURCESTATUS: Status").
              setSize(20);
      headblk.setView("SCH_RESOURCE");
      headblk.defineCommand("SCH_RESOURCE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("SCHRESOURCETBLHEAD: Sch Resources");
//      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("PROJ_DESC");
      headlay.setSimple("CREATE_PERSON_NAME");
      headbar.addCustomCommand("findTree",mgr.translate("QUANLITYPLANSTART: Find Tree Node..."));
 



   }


   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "SCHRESOURCEDESC: Sch Resource";
   }


   protected String getTitle()
   {
      return "SCHRESOURCETITLE: Sch Resource";
   }


//   protected void printContents() throws FndException
//   {
//      super.printContents();
//
//      ASPManager mgr = getASPManager();
//      if (headlay.isVisible())
//          appendToHTML(headlay.show());
//   }
   //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   public void findTree() throws FndException{
      String proj_type;
      ASPManager mgr = getASPManager();
      ASPCommand cmd = mgr.newASPCommand(); 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      headset.storeSelections();  
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("PROJ_NO,RESOURCE_NO");
//      proj_type = headset.getValue("PROJECT_TYPE_NO");
      resource_no    = headset.getValue("RESOURCE_NO");
      project_no    = headset.getValue("PROJ_NO");        
      callNewWindow("SchResourceTree.page", selected_fields);
   }
         
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
//       String id;
       ASPManager mgr = getASPManager();

//        id=buff.getBufferAt(0).getValueAt(0);
        ASPContext ctx = mgr.getASPContext();
        ctx.setGlobal("PROJ_NO",project_no);
        ctx.setGlobal("PLAN_NO",resource_no);
        project_no = buff.getBufferAt(0).getValueAt(0);
        resource_no = buff.getBufferAt(0).getValueAt(1);

      String url = transfer_page+"?PROJ_NO="+project_no+"&RESOURCE_NO="+resource_no;//+"&PROJECT_TYPE_NO="+id
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }
   
   public void  adjust()
   {
      // fill function body
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
         src_str    = "SchResourceTree.page";
      }
      else
      {
         resource_no    = headset.getValue("RESOURCE_NO");
         project_no    = headset.getValue("PROJ_NO");    
         src_str    = "SchResourceTree.page?RESOURCE_NO="+resource_no+"&PROJ_NO="+project_no;
      }     
   }
   
   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;

   }   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------




   protected AutoString getContents() throws FndException
   {
   
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("SCHEDULERESOURCE: Schedule Resource"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
      
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMB\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTIVITYCOM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PROGRESSCOM\" value=\"\">\n");
      out.append(mgr.startPresentation("SCHEDULERESOURCE: Schedule Resource"));
      out.append(headlay.show());
      if (headlay.isSingleLayout() && headset.countRows() > 0)
      { 
          out.append("<iframe name=\"contents\" target=\"ChildMain\" src=");
          out.append(src_str);
          out.append(" width=\"24%\" scrolling=\"yes\" height = \"100%\" scrolling=\"yes\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");
          out.append("<iframe name =\"ChildMain\" src=\"SchResourceLine.page\" width=\"74%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");
          
      }
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      return out;
      
   }   
}
