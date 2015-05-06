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
 *  File        : ScheduledTaskTreeList.java 
 *  Modified    :
 *  rahelk  2004-05-18 created.
 *  rahelk  2004-06-04  - Implemented PARAMETER Table functionality
 *  rahelk  2004-07-12  - Implemented sorting of tree by TYPE.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/07/07 amiklk Bug 83631, Changed createTree() to show all tasks & reports
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients. 
 * 2006/08/10 buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 * Revision 2006/07/03 buhilk 
 * Bug 58216, Fixed SQL Injection threats
 *
 * Revision 1.3  2005/12/06 rahelk
 * Added FROM_SCHED_TASK to querystring param when calling wizard
 *
 * Revision 1.2  2005/10/10 09:39:00  rahelk
 * Added checking parameter values using validation_method
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/08/08 09:44:06  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.2  2005/02/08 08:12:55  rahelk
 * Merged Call id 121571. Reimplemented scheduled reports to be similar to general tasks.
 *
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.Vector;

public class ScheduledTaskTreeList extends ASPPageProvider
{
   private TreeList root;
   private ASPPopup node_popup;
   private ASPPopup item_popup;
   private ASPPopup root_popup;
   
   private final static String TYPE_MODULE = "TM";
   private final static String MODULE_TYPE = "MT";
   private final static String MODULE      = "M";
   
   private String sort_type;
	//===============================================================
	// Construction 
	//===============================================================
	public ScheduledTaskTreeList(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      sort_type = mgr.getASPContext().readValue("SORT_TYPE",TYPE_MODULE);

      if ("EXPAND_TREE".equals(mgr.getQueryStringValue("VALIDATE")) )
         expandNode();
      else if ("activateTask".equals(mgr.readValue("__COMMAND")))
         activateTask();
      else if ("runTask".equals(mgr.readValue("__COMMAND")))
         runTask();
      else if ("deactivateTask".equals(mgr.readValue("__COMMAND")))
         deactivateTask();
      else if ("deleteTask".equals(mgr.readValue("__COMMAND")))
         deleteTask();

      createTree();
      
      mgr.getASPContext().writeValue("SORT_TYPE",sort_type);
   }
  
//=============================================================================
//  RMB functions
//=============================================================================
   
   private void deleteTask() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("DELE_SCHED","Batch_SYS.Remove_Batch_Schedule");
      cmd.addParameter("SCHEDULE_ID",mgr.readValue("SCHEDULE_ID"));
      
      mgr.perform(trans);
   }
   
   private void runTask()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("RUN_SCHED","Batch_Schedule_API.Run_Batch_Schedule__");
      cmd.addParameter("SCHEDULE_ID",mgr.readValue("SCHEDULE_ID"));
      cmd.addParameter("ONLINE","FALSE");
      
      mgr.perform(trans);
   }
   
   private void activateTask() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("ACT_SCHED","Batch_Schedule_API.Activate__");
      cmd.addParameter("SCHEDULE_ID",mgr.readValue("SCHEDULE_ID"));
      
      mgr.perform(trans);
   }
   
   private void deactivateTask() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("ACT_SCHED","Batch_Schedule_API.Deactivate__");
      cmd.addParameter("SCHEDULE_ID",mgr.readValue("SCHEDULE_ID"));
      
      mgr.perform(trans);
   }

   private void expandNode()
   {
      ASPManager mgr = getASPManager();
      
      if (!mgr.isEmpty(mgr.readValue("MODULE")))
         searchFolder();
      else
         searchNode();
      
      mgr.endResponse();
   }

   
   private void searchFolder()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = null;

      if ("Y".equals(mgr.readValue("POPULATE_GENERAL"))){
         q = trans.addQuery("MODTASK","BATCH_SCHEDULE_METHOD","SCHEDULE_METHOD_ID, DESCRIPTION, METHOD_NAME, ARGUMENT_TYPE_DB, VALIDATION_METHOD","MODULE=? and upper(METHOD_NAME) <> upper(?)", "DESCRIPTION");
         q.addParameter("MODULE", "S", "IN", mgr.readValue("MODULE"));
         q.addParameter("METHOD_NAME", "S", "IN", ScheduledTask.SCHED_REP_METHOD_NAME);
      }
      
      if ("Y".equals(mgr.readValue("POPULATE_REPORTS"))){
         trans.addQuery("MODREP","BATCH_SCHEDULE_REPORT_METHOD","REPORT_ID, REPORT_TITLE","MODULE=?", "REPORT_TITLE").
                 addParameter("MODULE", "S", "IN", mgr.readValue("MODULE"));
      }
      //q.includeMeta("ALL");

      trans = mgr.perform(trans);

      ASPBuffer buf = null;
      String item_data = "";
      int size = 0;
      TreeListNode scheduel_node = null;
      String image_location = mgr.getASPConfig().getImagesLocation();
      
      TreeList dummynode = new TreeList(mgr,"DUMMY");
      
      if ("Y".equals(mgr.readValue("POPULATE_GENERAL")))
      {
         buf = trans.getBuffer("MODTASK");
         size = buf.countItems()-1;
         
         for (int i=0; i<size; i++)
         {
            ASPBuffer row_buff = buf.getBufferAt(i);

            scheduel_node = dummynode.addNode(row_buff.getValue("DESCRIPTION"),"",image_location+"navigator_task.gif","&SCHEDULE_METHOD_ID="+row_buff.getValue("SCHEDULE_METHOD_ID"));

            String init_call = init_call = "initNewTask('"+row_buff.getValue("DESCRIPTION")+"','"+row_buff.getValue("SCHEDULE_METHOD_ID")+"','"+row_buff.getValue("METHOD_NAME")+"','"+row_buff.getValue("ARGUMENT_TYPE_DB")+"','"+(!mgr.isEmpty(row_buff.getValue("VALIDATION_METHOD"))?row_buff.getValue("VALIDATION_METHOD"):"")+"')";
            scheduel_node.addDefinedPopup(node_popup.generateCall(init_call));
         }
      }
      
      if ("Y".equals(mgr.readValue("POPULATE_REPORTS")))
      {
         buf = trans.getBuffer("MODREP");
         size = buf.countItems()-1;

         for (int i=0; i<size; i++)
         {
            ASPBuffer row_buff = buf.getBufferAt(i);

            scheduel_node = dummynode.addNode(row_buff.getValue("REPORT_TITLE"),"",image_location+"navigator_report.gif","&REPORT_ID="+row_buff.getValue("REPORT_ID"));

            String init_call = "initReportTask('"+mgr.readValue("MODULE")+"','"+row_buff.getValue("REPORT_ID")+"')";
            scheduel_node.addDefinedPopup(node_popup.generateCall(init_call));

         }
      }

      item_data = dummynode.getDynamicNodeString();            
      mgr.responseWrite(item_data+"^");
   }
   
   
   private void searchNode()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = null;
      
      String view_name = "BATCH_SCHEDULE";
      String where_condition = "SCHEDULE_METHOD_ID=?";
      String image_name = "object_schedule_task.gif";
      
      
      if (!mgr.isEmpty(mgr.readValue("REPORT_ID")))
      {
         view_name = "BATCH_SCHEDULE_REPORT";
         where_condition = "REPORT_ID=?";
         image_name = "object_schedule_report.gif";
      }

      q = trans.addQuery("MAIN",view_name,"SCHEDULE_ID, SCHEDULE_NAME, ACTIVE_DB, INSTALLATION_ID", where_condition, "SCHEDULE_ID");
      if (!mgr.isEmpty(mgr.readValue("REPORT_ID")))
      {
         q.addParameter("REPORT_ID", "S", "IN", mgr.readValue("REPORT_ID"));
      }else{
         q.addParameter("SCHEDULE_METHOD_ID", "S", "IN", mgr.readValue("SCHEDULE_METHOD_ID"));
      }      
      q.includeMeta("ALL");

      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("MAIN");

      String item_data = "";
      
      int size = buf.countItems()-1;
      String target = "";
      TreeListItem item;
      String image_location = mgr.getASPConfig().getImagesLocation();
      
      if (size > 0) {
         TreeList dummynode = new TreeList(mgr,"DUMMY");
         for (int j=0; j<size; j++)
         {
            Vector enable_list = new Vector();

            //enable_list.addElement("true"); // new schedule menu item
            enable_list.addElement("true"); // delete menu item
            enable_list.addElement("true"); // run
            
            String schedule_id = buf.getBufferAt(j).getValue("SCHEDULE_ID");
            String schedule_name = buf.getBufferAt(j).getValue("SCHEDULE_NAME");
            String label = schedule_id+" - "+schedule_name + (mgr.isEmpty(buf.getBufferAt(j).getValue("INSTALLATION_ID"))?"":" "+mgr.translateJavaScript("FNDSCHEDTASKSYSDEFINED: (System Defined)"));
            target = "ScheduledTaskHeader.page?SCHEDULE_ID="+buf.getBufferAt(j).getValue("SCHEDULE_ID");
            item = dummynode.addItem(label,target,image_location+image_name); 
            
            if ("TRUE".equals(buf.getBufferAt(j).getValue("ACTIVE_DB")))
            {
               enable_list.addElement("false"); // activate menu item
               enable_list.addElement("true"); // deactivate menu item
            }
            else
            {
               enable_list.addElement("true"); // activate menu item
               enable_list.addElement("false"); // deactivate menu item
            }
            String init_call = "f.SCHEDULE_ID.value="+schedule_id+";__schedule_name ='"+label+"'";
            item.addDefinedPopup(item_popup.generateCall(init_call,enable_list));
         }

         item_data = dummynode.getDynamicNodeString();            
      }
      mgr.responseWrite(item_data+"^");
   }
   
   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      ASPBlock utilblk = mgr.newASPBlock("UTIL");
      utilblk.addField("SCHEDULE_ID");
      utilblk.addField("ONLINE");
      
      node_popup = newASPPopup("scheduledTaskMenu1");
      node_popup.addItem("FNDSCHEDTASKNEWSCHED: New Schedule","javascript:openWizard();");
      
      item_popup = newASPPopup("scheduledTaskMenu2");
      item_popup.addItem("FNDSCHEDTASKDELSCHED: Delete","deleteTask()");
      item_popup.addSeparator();
      item_popup.addItem("FNDSCHEDTASKRUNSCHED: Run ASAP","runTask()");
      item_popup.addItem("FNDSCHEDTASKACTSCHED: Activate","activateTask()");
      item_popup.addItem("FNDSCHEDTASKDEACTSCHED: Deactivate","deactivateTask()");

      root_popup = newASPPopup("scheduleRootMenu");
      root_popup.addItem("FNDSCHEDTASKTYPEMOD: Type - Module","javascript:setSorting('"+TYPE_MODULE+"');");
      root_popup.addItem("FNDSCHEDTASKMODTYPE: Module - Type","javascript:setSorting('"+MODULE_TYPE+"');");
      root_popup.addItem("FNDSCHEDTASKMOD: Module","javascript:setSorting('"+MODULE+"');");
      
      appendJavaScript("\nvar __schedule_name = '';");
      appendJavaScript("\nvar __schedule_method_id = '';\n");
      appendJavaScript("\nvar __method_name = '';\n");
      appendJavaScript("\nvar __argument_type = '';\n");
      appendJavaScript("\nvar __report_id = '';\n");
      appendJavaScript("\nvar __report_module = '';\n");
      appendJavaScript("\nvar __schedule_type = '';\n");
      appendJavaScript("\nvar __validation_method = '';\n");

      
      appendJavaScript("\nfunction initNewTask(task_name, method_id, method_name, arg_type, val_method ){");      
      appendJavaScript("\n\t__schedule_name = task_name;\n");
      appendJavaScript("\n\t__schedule_method_id = method_id;\n");
      appendJavaScript("\n\t__method_name = method_name;\n");
      appendJavaScript("\n\t__argument_type = arg_type;\n");
      appendJavaScript("\n\t__schedule_type = '';\n");
      appendJavaScript("\n\t__validation_method = val_method;\n");
      appendJavaScript("\n}\n");


      appendJavaScript("\nfunction initReportTask(module, report_id){");      
      appendJavaScript("\n\t __report_module = module;\n");
      appendJavaScript("\n\t __report_id = report_id;\n");
      appendJavaScript("\n\t __schedule_type = 'REPORTS';\n");
      appendJavaScript("\n\t __validation_method = '';\n");
      appendJavaScript("\n}\n");

      
      appendJavaScript("\nfunction openWizard(){");
      appendJavaScript("\n if(__schedule_type == 'REPORTS') openReportWizard();\n");
      appendJavaScript("\n\telse");
      appendJavaScript("\n\t showNewBrowser('" + getASPConfig().getScriptsLocation() + "ScheduledTaskWizard.page?SCHEDULE_NAME='+__schedule_name+'&SCHEDULE_METHOD_ID='+__schedule_method_id+'&METHOD_NAME='+__method_name+'&ARGUMENT_TYPE_DB='+__argument_type+'&FROM_SCHED_TASK=Y&TREE_TYPE=ALL'+'&SORT_BY='+f.__SORT_BY.value+'&VALIDATION_METHOD='+__validation_method);");
      appendJavaScript("\n}\n");

      appendJavaScript("\nfunction openReportWizard(){");
      appendJavaScript("\n\tshowNewBrowser('" + getASPConfig().getScriptsLocation() + "ScheduledTaskWizard.page?METHOD_NAME="+ScheduledTask.SCHED_REP_METHOD_NAME+"&REPORT_ID='+__report_id+'&REPORT_MODULE='+__report_module+'&FROM_SCHED_TASK=Y&TREE_TYPE='+__tree_type+'&SORT_BY='+f.__SORT_BY.value);");
      appendJavaScript("\n __schedule_type = '';\n");
      appendJavaScript("\n}\n");
      
      appendJavaScript("\nfunction deleteTask(){");
      appendJavaScript("\n\tf.__COMMAND.value='deleteTask';");
      appendJavaScript("\n\tif(confirm('\\''+__schedule_name+'\\' - "+mgr.translateJavaScript("FNDSCHEDTASKCONFIRMDEL: Task will be deleted. Do you want to continue?")+"'))");
      appendJavaScript("\n\t\tsubmit();");
      appendJavaScript("\n}\n");
      
      appendJavaScript("\nfunction runTask(){");
      appendJavaScript("\n\tf.__COMMAND.value='runTask';");
      appendJavaScript("\n\tif(confirm('\\''+__schedule_name+'\\' - "+mgr.translateJavaScript("FNDSCHEDTASKCONFIRMRUN: Task will be run. Do you want to continue?")+"'))");
      appendJavaScript("\n\t\tsubmit();");
      appendJavaScript("\n}\n");
      
      appendJavaScript("\nfunction activateTask(){");
      appendJavaScript("\n\tf.__COMMAND.value='activateTask';");
      appendJavaScript("\n\tif(confirm('\\''+__schedule_name+'\\' - "+mgr.translateJavaScript("FNDSCHEDTASKCONFIRMACT: Task will be activated. Do you want to continue?")+"'))");
      appendJavaScript("\n\t\tsubmit();");
      appendJavaScript("\n}\n");

      appendJavaScript("\nfunction deactivateTask(){");
      appendJavaScript("\n\tf.__COMMAND.value='deactivateTask';");
      appendJavaScript("\n\tif(confirm('\\''+__schedule_name+'\\' - "+mgr.translateJavaScript("FNDSCHEDTASKCONFIRMDEACT: Task will be deactivated. Do you want to continue?")+"'))");
      appendJavaScript("\n\t\tsubmit();");
      appendJavaScript("\n}\n");
      
      appendJavaScript("\nfunction setSorting(sort_by){");
      appendJavaScript("\n\tf.__SORT_BY.value=sort_by;");
      appendJavaScript("\n\tsubmit();");
      appendJavaScript("\n}\n");
      
      
   }      

   public void  createTree() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = null;
      String image_location = mgr.getASPConfig().getImagesLocationWithRTL();
      root = null;
      
      if (!"REPORTS".equals(mgr.getQueryStringValue("SCHEDULE_TYPE")))
      {
         root = new TreeList(mgr,mgr.translate("FNDSCHEDTASKROOT: Tasks"),null, image_location+"Object_Root.gif");
         root.addDefinedPopup(root_popup.generateCall());
         
         trans.clear();    
         q = trans.addQuery("SCHEDTASK","select distinct MODULE, MODULE_API.Get_Name(MODULE) MODULE_NAME from BATCH_SCHEDULE_METHOD ORDER BY MODULE_NAME");
         q.setBufferSize(Integer.MAX_VALUE);                  
         q = trans.addQuery("SCHEDREP","select distinct MODULE, MODULE_API.Get_Name(MODULE) MODULE_NAME from BATCH_SCHEDULE_REPORT_METHOD ORDER BY MODULE_NAME");
         q.setBufferSize(Integer.MAX_VALUE);
         q.includeMeta("ALL");

         trans = mgr.perform(trans);

         ASPBuffer buf = null;
         String module = "";
         TreeListNode folder_node = null;
         TreeListNode scheduel_node = null;
         
         if (!mgr.isEmpty(mgr.readValue("__SORT_BY")))
            sort_type = mgr.readValue("__SORT_BY");
         
         if (TYPE_MODULE.equals(sort_type))
         {
            folder_node = root.addNode(mgr.translate("FNDSCHEDTASKGENERAL: General"),null,image_location+"Object_Activity.gif");
            buf = trans.getBuffer("SCHEDTASK");
            int size = buf.countItems()-1;
            
            if (size >0)
            {
               for (int i=0; i<size; i++)
               {
                  ASPBuffer row_buff = buf.getBufferAt(i);
                  folder_node.addNode(row_buff.getValue("MODULE_NAME"),"",image_location+"schedule_folder.gif","&POPULATE_GENERAL=Y&MODULE="+row_buff.getValue("MODULE"));
               }
            }
            
            folder_node = root.addNode(mgr.translate("FNDSCHEDTASKREPORTS: Reports"),null,image_location+"Object_Activity.gif");
            folder_node.addDefinedPopup(node_popup.generateCall("initReportTask('','')"));
            buf = trans.getBuffer("SCHEDREP");
            size = buf.countItems()-1;
            
            if (size >0)
            {
               for (int i=0; i<size; i++)
               {
                  ASPBuffer row_buff = buf.getBufferAt(i);
                  TreeListNode report_node = folder_node.addNode(row_buff.getValue("MODULE_NAME"),"",image_location+"schedule_folder.gif","&POPULATE_REPORTS=Y&MODULE="+row_buff.getValue("MODULE"));
                  String init_call = "initReportTask('"+row_buff.getValue("MODULE")+"','');";
                  report_node.addDefinedPopup(node_popup.generateCall(init_call));
               }
            }
         }
         else 
         {
            buf = mgr.newASPBuffer();
            ASPBuffer task_buf = trans.getBuffer("SCHEDTASK");
            ASPBuffer rep_buf = trans.getBuffer("SCHEDREP");
            
            int size = task_buf.countItems();
            
            for (int i=0; i<size-1; i++)
               buf.addBuffer("DATA",task_buf.getBufferAt(i));
            
            size = rep_buf.countItems();
            
            for (int i=0; i<size-1; i++)
               buf.addBuffer("DATA",rep_buf.getBufferAt(i));
            
            buf.sort("MODULE_NAME",true); 
            size = buf.countItems();
            
            String previous = "";
            for (int i=0; i <size; i++)
            {
               ASPBuffer row_buff = buf.getBufferAt(i);
               module = row_buff.getValue("MODULE");
               
               if (module.equals(previous)) continue;
               
               previous = module;
               
               if (MODULE_TYPE.equals(sort_type))
               {
                  folder_node = root.addNode(row_buff.getValue("MODULE_NAME"),"",image_location+"schedule_folder.gif");
                  folder_node.addNode(mgr.translate("FNDSCHEDTASKGENERAL: General"),"",image_location+"Object_Activity.gif","&POPULATE_GENERAL=Y&MODULE="+row_buff.getValue("MODULE"));

                  TreeListNode report_node = folder_node.addNode(mgr.translate("FNDSCHEDTASKREPORTS: Reports"),"",image_location+"Object_Activity.gif","&POPULATE_REPORTS=Y&MODULE="+row_buff.getValue("MODULE"));
                  String init_call = "initReportTask('"+row_buff.getValue("MODULE")+"','')";
                  report_node.addDefinedPopup(node_popup.generateCall(init_call));
                  
               }
               else
                  root.addNode(row_buff.getValue("MODULE_NAME"),"",image_location+"schedule_folder.gif","&POPULATE_GENERAL=Y&POPULATE_REPORTS=Y&MODULE="+row_buff.getValue("MODULE"));
            }
         }
         
         appendDirtyJavaScript("\nvar __tree_type = 'ALL';\n");
      }
      else //scheduled reports only
      {
         root = new TreeList(mgr,mgr.translate("FNDSCHEDREPORTROOT: Reports"),null, image_location+"Object_Root.gif");
         root.addDefinedPopup(node_popup.generateCall("initReportTask('','')"));

         trans.clear();    
         q = trans.addQuery("SCHEDREP","SELECT distinct MODULE, MODULE_API.Get_Name(MODULE) MODULE_NAME FROM BATCH_SCHEDULE_REPORT_METHOD ORDER BY MODULE_NAME");
         q.setBufferSize(Integer.MAX_VALUE); 
         q.includeMeta("ALL");

         trans = mgr.perform(trans);
         
         ASPBuffer buf = trans.getBuffer("SCHEDREP");
         String module = "";
         TreeListNode folder_node = null;
         
         int size = buf.countItems()-1;
         if (size >0)
         {
            for (int i=0; i<size; i++)
            {
               ASPBuffer row_buff = buf.getBufferAt(i);
               module = row_buff.getValue("MODULE");
               String init_call = "";
               
               folder_node = root.addNode(row_buff.getValue("MODULE_NAME"),"",image_location+"schedule_folder.gif","&POPULATE_REPORTS=Y&MODULE="+module);
               init_call = "initReportTask('"+module+"','')";
               folder_node.addDefinedPopup(node_popup.generateCall(init_call));
            }
         }
         appendDirtyJavaScript("\nvar __tree_type = 'REPORTS_ONLY';\n");
      }
      //root.setBaseTarget("_parent"); //for embedded iframe sol
      root.addHiddenField("SCHEDULE_ID", "");
      root.addHiddenField("__SORT_BY", sort_type);
   }
   
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return getTitle();
   }

   protected String getTitle()
   {
      return "FNDSCHEDTASKTREETITLE: Scheduled Tasks";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      AutoString out = this.getOutputStream();
      out.clear();
      
      //root.initiallyExpandedNode(3);

      out.append(root.show());
      
      out.append(node_popup.generateDefinition());
      out.append(item_popup.generateDefinition());
      
      return out;
   }
   
}
