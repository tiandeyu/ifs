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
*  File        : MyToDoTask.java
*  Modified    :
* ----------------------------------------------------------------------------
*  $Log: MyToDoTask.java,v $
*
*  BUHILK      2008-Jul-09       Created.
*
* ----------------------------------------------------------------------------
* New Comments:
* 2010/10/16 amiklk Bug 92442, Changed appendCustomCall() to use proper encoding for QS.
* 2010/05/31 buhilk Bug 90640, Modified getUsers(), saveSendTo() and getPopupContents() to improve send to functionality.
* 2009/02/27 buhilk Bug 80960, Added Missing functionality for due date expiration reminders, reassigning & sharing tasks.
* 2009/02/18 buhilk Bug 80667, Modified predefine() to open page links on same window.
* 2008/10/08 buhilk Bug 77648, Modified predefine() to request context update for setFlag and setProirity ajax calls.
* 2008/08/15 buhilk Bug 76288, Modified preDefine(), run(). Removed printContents() and added getContents().
* 2008/08/11 dusdlk Bug 76202, Updated the initParams() function to decode the url values.
*/

package ifs.fnd.web.features.managemytodo;

import ifs.application.enumeration.TodoFolderTypeEnumeration;
import ifs.application.enumeration.TodoItemTypeEnumeration;
import ifs.application.managemytodo.PostNewItem;
import ifs.application.personinfo.PersonInfoArray;
import ifs.fnd.asp.*;
import ifs.fnd.base.IfsException;
import ifs.fnd.base.ParseException;
import ifs.fnd.record.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.AutoString;
import java.util.StringTokenizer;
import ifs.fnd.webfeature.FndWebFeature;
import ifs.fnd.base.ApplicationException;
import ifs.application.mytodoitem.MyTodoItem;
import ifs.application.enumeration.TodoFlagEnumeration;
import ifs.application.enumeration.TodoPriorityEnumeration;

/**
 * Web Feature to manage MyTodo tasks on web clients
 */
public class MyToDoTask extends ASPPageProvider implements FndWebFeature
{
   
   //===============================================================
   // Static constants
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.web.features.managemytodo.MyToDoTask");
   
   //===============================================================
   // Member variables 
   //===============================================================

   ASPBlock blk;
   ASPCommandBar cmdbar;
   ASPTable tbl;
   ASPBlockLayout lay;
   
   //===============================================================
   // temp variables
   //===============================================================
   private String item_id = "";
   private String identity = "";
   private String flag = "";
   private String priority = "";
   private String title = "";
   private String keys = "";
   private String url = "";
   private String busobj = "";
   private String item_message = "";
   
   private ASPContext ctx;
   private String folderType = "";
   private boolean isContextPane = false;
   private boolean showunread = false;
   
   private final static int REASSIGN_TASK = 0;
   private final static int SHARE_TASK    = 1;

   /**
    * Creates a new instance of MyToDoTask
    * @param mgr ASPManager
    * @param page_path Page path
    */
   public MyToDoTask(ASPManager mgr, String page_path) {
      super(mgr,page_path);
   }
   
   // ==========================================================================================================================
   // Protected overloaded Methods
   // ==========================================================================================================================

   /**
    * Method called by frmaework to define webfeature on the page pool.
    * @throws ifs.fnd.service.FndException During definition.
    */
   protected void preDefine() throws FndException {
      ASPManager mgr = getASPManager();
      MyTodoItem myToDo_item = new MyTodoItem();
      
      try {
         blk = mgr.newASPBlock("MYTODO_ITEM");
         blk.addField("OBJ_VERSION", myToDo_item.objVersion).
             setHidden();
         blk.addField("ITEM_ID", myToDo_item.itemId).
             setHidden();
         blk.addField("IDENTITY", myToDo_item.identity).
             setHidden();
         blk.addField("FOLDER_ID", myToDo_item.folderId).
             setHidden();
         blk.addField("READ", myToDo_item.read).
             setHidden();
         blk.addField("PRIORITY", myToDo_item.item().priority).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOPRIORITY: Priority").
             setAsImageField().
             enumerateValues(myToDo_item.item().priority.toEnumerationView()).
             setSelectBox().
             setAsPopupMenuField(priorityMenu(),"FWWEBFEATUREMYTODOPRIORITY: Priority");
         blk.addField("SHARED", myToDo_item.item().shared).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOSHARED: Shared").
             setAsImageField().
             setHyperlink("while(false){}",null,ASPField.JAVASCRIPT);
         blk.addField("SENT_BY", myToDo_item.sentBy).
             setLabel("FWWEBFEATUREMYTODOSENTBY: Sent By").
             setHidden();
         blk.addField("NAME", myToDo_item.sender().name).
             setAggregateReference("SENDER").
             setLabel("FWWEBFEATUREMYTODOFROM: From").
             setReadOnly();
         blk.addField("URL", myToDo_item.item().url).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOURL: Url").
             setHidden();
         blk.addField("BUS_OBJ", myToDo_item.item().businessObject).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOBUSOBJ: Business Object").
             setHidden();             
         blk.addField("TITLE", myToDo_item.item().title).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOTITLE: Title").
             setHyperlink("MyToDoTask.page?","ITEM_ID,IDENTITY,URL").
             setSize(50);
         blk.addField("DATE_RECEIVED", myToDo_item.dateReceived,"dd-MMM-yyyy hh:mm:ss a").
             setLabel("FWWEBFEATUREMYTODORECEIVED: Received").
             setReadOnly();
         blk.addField("DUE_DATE", myToDo_item.dueDate,"dd-MMM-yyyy hh:mm:ss a").
             setLabel("FWWEBFEATUREMYTODODUEDATE: Due Date");
         blk.addField("FLAG", myToDo_item.flag).
             setLabel("FWWEBFEATUREMYTODOFLAG: Flag").
             enumerateValues(myToDo_item.flag.toEnumerationView()).
             setSelectBox().
             setAsImageField().
             setAsPopupMenuField(followUpFlagMenu(),"FWWEBFEATUREMYTODOFOLLOWUP: Follow Up");
         blk.addField("ITEM_MESSAGE", myToDo_item.item().itemMessage).
             setAggregateReference("ITEM").
             setLabel("FWWEBFEATUREMYTODOMSG: Message").
             setHeight(5).
             setSize(50).
             setAsExpandable(2);
      } catch (IfsException ex) {
         ex.printStackTrace();
      }

      blk.setDataAdapter(new MyToDoTaskAdapter());

      blk.enableStandardCommands("Modify,Remove"); // "New" not implemented
      
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableCommand(ASPCommandBar.FIND);
      cmdbar.addCustomCommand("markAsRead", "FWWEBFEATUREMYTODOSETASREAD: Mark as Read");
      cmdbar.addCustomCommand("markAsUnRead", "FWWEBFEATUREMYTODOSETASUNREAD: Mark as Unread");
      cmdbar.addCustomCommand("reAssign", "FWWEBFEATUREMYTODOREASSIGN: Reassign...");
      cmdbar.addCustomCommand("share", "FWWEBFEATUREMYTODOSHARE: Share...");
      cmdbar.addCustomCommand("populate","POP");
      cmdbar.disableCustomCommand("populate");
      
      tbl = mgr.newASPTable(blk);
      tbl.disableDetailLink();
      tbl.addRowCondition("READ","false");
      tbl.addRowCondition("DUE_DATE",ASPTable.GREATER_THAN_OR_EQUAL,"#TODAY#","reminderText");
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
      
      disableContextSensitiveTaskPane();
      
      StringTokenizer pkeys = new StringTokenizer(getMyTodoPrimarKeys(),",");
      
      appendJavaScript("\nfunction getParamList(){");
      appendJavaScript("\n   var paramlist = '';");
      while(pkeys.hasMoreTokens()) {
         String p = pkeys.nextToken().trim();
         appendJavaScript("\n   paramlist += ((paramlist!='')?'&':'') + '"+p+"='+getCustomPopupParamValue('MYTODO_ITEM','"+p+"');");
      }
      appendJavaScript("\n   return paramlist;");
      appendJavaScript("\n}");
      
      appendJavaScript("\nvar __contextParams = '__UPDATE_CONTEXT=Y&__PAGE_ID='+f.__PAGE_ID.value+'&';");
      
      appendJavaScript("\nfunction setFlag(flag, img){");
      appendJavaScript("\n   __connect(APP_ROOT+'fnd/web/features/managemytodo/MyToDoTask.page?'+__contextParams+'ACTION=SETFLAG&flag='+flag+'&'+getParamList());");
      appendJavaScript("\n   var flagfld = document.getElementById('FLAG'+getPopupRow());");
      appendJavaScript("\n   if(last_response_script[0]=='DONE')");
      appendJavaScript("\n   {");
      appendJavaScript("\n      flagfld.src=IMAGES_PATH+img");
      appendJavaScript("\n      f.__PAGE_ID.value = last_response_script[1];");
      appendJavaScript("\n   }");
      appendJavaScript("\n   else");
      appendJavaScript("\n      alert(last_response_script[1]);");
      appendJavaScript("\n}");
      
      appendJavaScript("\nfunction setPriority(priority, img){");
      appendJavaScript("\n   __connect(APP_ROOT+'fnd/web/features/managemytodo/MyToDoTask.page?'+__contextParams+'ACTION=SETPRIORITY&priority='+priority+'&'+getParamList());");
      appendJavaScript("\n   var priorityfld = document.getElementById('PRIORITY'+getPopupRow());");
      appendJavaScript("\n   if(last_response_script[0]=='DONE')");
      appendJavaScript("\n   {");
      appendJavaScript("\n      priorityfld.src=IMAGES_PATH+img");
      appendJavaScript("\n      f.__PAGE_ID.value = last_response_script[1];");
      appendJavaScript("\n   }");
      appendJavaScript("\n   else");
      appendJavaScript("\n      alert(last_response_script[1]);");
      appendJavaScript("\n}");
      
      appendJavaScript("\nfunction openSharedUsers(itemid){");
      appendJavaScript("\n   __connect(APP_ROOT+'fnd/web/features/managemytodo/MyToDoTask.page?ACTION=SHAREDUSERS&ITEM_ID='+itemid);");
      appendJavaScript("\n   if(last_response_script[0]=='DONE')");
      appendJavaScript("\n   {");
      appendJavaScript("\n      var div_obj = document.getElementById('message_window');");
      appendJavaScript("\n      div_obj.style.left = (getBrowserSize()[0])/2-90;");
      appendJavaScript("\n      div_obj.style.top = 200;");
      appendJavaScript("\n      var div_html = \"<table class=normalTextValue border=\\\"0\\\" width=\\\"100%\\\" cellspacing=10>\";");
      appendJavaScript("\n      div_html += \"<tr>\";");
      appendJavaScript("\n      div_html += \"<td>\";");
      appendJavaScript("\n      div_html += \"<img border=\\\"0\\\" src=\\\"\"+IMAGES_PATH+\"users_32x32.png\\\" align=\\\"middle\\\">&nbsp;<b>"+mgr.translateJavaScript("FWWEBFEATUREMYTODOSHAREDUSERS: Shared Users")+"</b><br>\";");
      appendJavaScript("\n      div_html += \"<hr noshade size=1px>\";");
      appendJavaScript("\n      for(var x=1; x<last_response_script.length; x++){");
      appendJavaScript("\n         div_html += last_response_script[x]+\"<br>\";");
      appendJavaScript("\n      }");
      appendJavaScript("\n      div_html += \"</td>\";");
      appendJavaScript("\n      div_html += \"</tr>\";");
      appendJavaScript("\n      div_html += \"<tr>\";");
      appendJavaScript("\n      div_html += \"<td align=\"+((IS_RTL)?\"left\":\"right\")+\">\";");
      appendJavaScript("\n      div_html += \"<input onclick=\\\"javascript:closeSharedUsers();\\\" class=\\\"button\\\" type=\\\"button\\\" value=\\\"\"+__BROADCASTMSG_CLOSE+\"\\\" name=\\\"B3\\\">\";");
      appendJavaScript("\n      div_html += \"</td>\";");
      appendJavaScript("\n      div_html += \"</tr>\";");
      appendJavaScript("\n      div_html += \"</table>\";");
      appendJavaScript("\n      div_obj.innerHTML=div_html;");
      appendJavaScript("\n      div_obj.style.width = 180;");
      appendJavaScript("\n      div_obj.style.visibility= 'visible';");
      appendJavaScript("\n      openShim('message_window');");
      appendJavaScript("\n      applyPageMask();");
      appendJavaScript("\n   }");
      appendJavaScript("\n   else");
      appendJavaScript("\n      alert(last_response_script[1]);");
      appendJavaScript("\n}");
      
      appendJavaScript("\nfunction closeSharedUsers(){");
      appendJavaScript("\n   var div_obj = document.getElementById('message_window');");
      appendJavaScript("\n   div_obj.style.visibility= 'hidden';");
      appendJavaScript("\n   div_obj.innerHTML='';");
      appendJavaScript("\n   closeShim('message_window');");
      appendJavaScript("\n   removePageMask();");
      appendJavaScript("\n}");
      
   }
   
   /**
    * Method called by the framework to execute web feature.
    */
   public void run() throws FndException {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      
      folderType = ctx.readValue("MyTodoFolderType",mgr.readValue("MyTodoFolderType",TodoFolderTypeEnumeration.TODO.toString()));
      ctx.writeValue("MyTodoFolderType",folderType);
      
      isContextPane = ctx.readFlag("CONTEXTPANE", "Y".equals(mgr.readValue("CONTEXTPANE")));
      ctx.writeFlag("CONTEXTPANE",isContextPane);
      
      busobj = mgr.readValue("BUS_OBJ");
      showunread = !mgr.isEmpty(mgr.readValue("SHOW_UNREAD"));
         
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         autoPopulate(blk);
      
      String validate = mgr.getQueryStringValue("VALIDATE");
      if(validate!=null && validate.equals("Y"))
      {
         String action = mgr.getQueryStringValue("ACTION");
         String resp = "";
         if(action.equals("SETPRIORITY"))
            resp = setPriority();
         else if(action.equals("SETFLAG"))
            resp = setFlag();
         else if(action.equals("SHAREDUSERS"))
            resp = getSharedUsers();
         else if(action.equals("GETUSERS"))
            resp = getUsers();
         else if(action.equals("SAVETASK_1"))
            resp = showSendToPopup();
         else if(action.equals("SAVETASK_2"))
            resp = saveSendTo();
         else if(action.equals("MAILTO"))
            resp = openMailCLient();
         else if(action.equals("REASSIGN_1"))
            resp = showReassignPopup();
         else if(action.equals("REASSIGN_2"))
            resp = modifyTask(REASSIGN_TASK);
         else if(action.equals("SHARE_1"))
            resp = showSharePopup();
         else if(action.equals("SHARE_2"))
            resp = modifyTask(SHARE_TASK);
         mgr.responseWrite(resp);
         mgr.endResponse();
      }
      else
      {
         String url = mgr.getQueryStringValue("URL");
         if(!mgr.isEmpty(url))
            openWindow(url);
      }
   }
   /**
    * Webfeature description.
    * @return feature description
    */
   protected String getDescription(){
      String title = "";
      if(folderType.equals(TodoFolderTypeEnumeration.TODO.toString()))
         title = "FWWEBFEATURETASKS: Tasks";
      else if(folderType.equals(TodoFolderTypeEnumeration.SENT_ITEMS.toString()))
         title = "FWWEBFEATURESENTTASKS: Sent Tasks";
      return title;
   }

   /**
    * webfeature title. Used by the framework to show borwser title as well.
    * @return Title of the webfeature.
    */
   protected String getTitle(){
      return getDescription();
   }
   
   /**
    * Method called by the framework to print the contents of the webpage.
    * @throws ifs.fnd.service.FndException If an error during creation of web contents.
    */
   protected AutoString getContents() throws FndException {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      if (lay.isVisible())
      {
         if(isContextPane)
         {
            out.append("&nbsp;&nbsp;");
            printLink("FWWEBFEATUREMYTODOSHOWALL: All MyTasks","javascript:showMyTasksPage();");
         }
         else
         {
            out.append(mgr.startPresentation(getTitle()));
            if(lay.isMultirowLayout()){
               out.append("&nbsp;&nbsp;");
               printWriteLabel("FWWEBFEATUREMYTODOSHOWUNREAD: Show Unread Items Only");
               printCheckBox("SHOW_UNREAD","1",showunread,"onClick=\"javascript:__openUnread(this.checked)\"");
            }
         }
         out.append(lay.show());
      }
      
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      appendDirtyJavaScript("function __openUnread(show)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var fld = getField_('READ');");
      appendDirtyJavaScript("    fld.value = show?'FALSE':'';");
      appendDirtyJavaScript("    commandSet('MYTODO_ITEM.populate','');");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("function showMyTasksPage()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    window.opener.location = APP_PATH+'/fnd/web/features/managemytodo/MyToDoTask.page?MyTodoFolderType=Todo&SEARCH=Y';\n");
      appendDirtyJavaScript("    window.close();\n");
      appendDirtyJavaScript("}\n");
      
      return out;      
   }

   /**
    * Created the image tags for the Image fields.
    * @param imageField ASPField object
    * @param row AbstractDataRow containing the data
    * @param rowNum current row number
    * @throws ifs.fnd.service.FndException during tag creation
    * @return image tag html
    */
   protected String getImageFieldTag(ASPField imageField, ASPRowSet row, int rowNum) throws FndException {
      ASPManager mgr = getASPManager();
      MyTodoItem myToDo_item = new MyTodoItem();
      FndDataRow thisRow = (FndDataRow) ((FndDataSet)row).getDataRow(rowNum);
      String img_loc = mgr.getASPConfig().getImagesLocation();
                 
      if(imageField.getName().equals("FLAG"))
      {
         String flagValue = thisRow.getValue(imageField);
         String img =   img_loc + (mgr.isEmpty(flagValue)? "table_empty_image.gif" : "MyToDoItemFollowUp"+flagValue+"16x16.gif");
         return "<img id=\""+imageField.getName()+rowNum+"\" width=\"16\" height=\"16\" onclick=\"javascript:menuClicked(this);\" border=\"0\" src=\""+img+"\" alt=\""+(!mgr.isEmpty(flagValue)?flagValue:"")+"\">";
      }
      else if(imageField.getName().equals("PRIORITY"))
      {
         String priorityValue = thisRow.getValue(imageField);
         String img =   img_loc + "table_empty_image.gif";
         if(!mgr.isEmpty(priorityValue))
         {
            if(priorityValue.equalsIgnoreCase(myToDo_item.item().priority.HIGH.getValue()))
               img =   img_loc + "MyToDoItemPriorityHigh16x16.gif";
            else if(priorityValue.equalsIgnoreCase(myToDo_item.item().priority.LOW.getValue()))
               img =   img_loc + "MyToDoItemPriorityLow16x16.gif";
         }
         return "<img id=\""+imageField.getName()+rowNum+"\" width=\"16\" height=\"16\" onclick=\"javascript:menuClicked(this);\" border=\"0\" src=\""+img+"\" alt=\""+priorityValue+"\">";
      }
      else if(imageField.getName().endsWith("SHARED"))
      {
         boolean shared = Boolean.parseBoolean(thisRow.getValue(imageField));
         if(shared)
            return "<img width=\"16\" onclick=\"javascript:openSharedUsers('"+thisRow.getValue("ITEM_ID")+"')\" height=\"16\" border=\"0\" src=\""+ img_loc + "MyToDoItemShared16x16.gif\">";
         
      }
      return "&nbsp;";
   }
   
   // ==========================================================================================================================
   // Private methods used for validation / ajax
   // ==========================================================================================================================

   /**
    * Set the priority flag for an TodoItem.
    * @return ajax String
    */
   private String setPriority() {
      MyTodoItem item = new MyTodoItem();    
      ASPManager mgr = getASPManager();
      String resp = "";
      item_id = mgr.getQueryStringValue("ITEM_ID");
      identity = mgr.getQueryStringValue("IDENTITY");
      int priority = Integer.parseInt(mgr.getQueryStringValue("priority").trim());
      
      try{
         if(item_id!=null && identity!=null)
         {
            item.itemId.setValue(item_id);
            item.identity.setValue(identity);
            MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) getASPBlock("MYTODO_ITEM").getDataAdapter();
            TodoPriorityEnumeration.Enum prioritytype = null;
            
            if(priority==0)
               prioritytype = TodoPriorityEnumeration.HIGH;
            else if(priority==1)
               prioritytype = TodoPriorityEnumeration.NORMAL;
            else if(priority==2)
               prioritytype = TodoPriorityEnumeration.LOW;
            
            boolean flagset = adapter.setPriority(item, prioritytype);
            if(flagset)
               resp = "DONE^"+mgr.getPageId();
            else
               resp = "ERROR^"+mgr.translateJavaScript("FWWEBFEATUREMYTODOPRIORITYERROR: Error in setting priority.");
         }
      }catch(ApplicationException ae)
      {
         resp = "ERROR^"+ae.getMessage()+" "+mgr.translateJavaScript("WWEBFEATUREMYTODOERROCC: error occurred.");
      }
      blk.getASPRowSet().refreshRow();
      return resp;
   }

   /**
    * Set the followup flag for an TodoItem.
    * @return ajax String
    */
   private String setFlag() {
      MyTodoItem item = new MyTodoItem();    
      ASPManager mgr = getASPManager();
      String resp = "";
      item_id = mgr.getQueryStringValue("ITEM_ID");
      identity = mgr.getQueryStringValue("IDENTITY");
      int flag = Integer.parseInt(mgr.getQueryStringValue("flag").trim());
      
      try{
         if(item_id!=null && identity!=null)
         {
            item.itemId.setValue(item_id);
            item.identity.setValue(identity);
            MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) getASPBlock("MYTODO_ITEM").getDataAdapter();
            TodoFlagEnumeration.Enum flagtype = null;
            
            if (flag==-1)
               flagtype = null;
            else if (flag==0)
               flagtype = TodoFlagEnumeration.RED_FLAG;
            else if (flag==1)
               flagtype = TodoFlagEnumeration.BLUE_FLAG;
            else if (flag==2)
               flagtype = TodoFlagEnumeration.YELLOW_FLAG;
            else if (flag==3)
               flagtype = TodoFlagEnumeration.GREEN_FLAG;

            boolean flagset = adapter.setFollowUpFlag(item, flagtype);
            if(flagset)
               resp = "DONE^"+mgr.getPageId();
            else
               resp = "ERROR^"+mgr.translateJavaScript("FWWEBFEATUREMYTODOFLAGERROR: Error in setting flag.");
         }
      }catch(ApplicationException ae)
      {
         resp = "ERROR^"+ae.getMessage()+" "+mgr.translateJavaScript("WWEBFEATUREMYTODOERROCC: error occurred.");
      }
      blk.getASPRowSet().refreshRow();
      return resp;
   }
   
   /**
    * Set the read/unread status for a TodoItem
    * @param itemid Unique itemid value
    * @param identity TodoItem owners identity
    * @param readFlag read/unread status as true/false
    */
   private void setReadStatus(String itemid, String identity, boolean readFlag) {
      MyTodoItem item = new MyTodoItem();    
            
      if(itemid!=null && identity!=null)
      {
         try {
            item.itemId.setValue(itemid);
            item.identity.setValue(identity);
            item.read.setValue(readFlag);
         } catch (ApplicationException ex) {
            ex.printStackTrace();
         }
         MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) getASPBlock("MYTODO_ITEM").getDataAdapter();
         adapter.setRead(item);
      }
   }
   
   /**
    * Opens a link when clicked on the Title
    * @param url link to open.
    */
   private void openWindow(String url) {
      ASPManager mgr = getASPManager();
      item_id = mgr.getQueryStringValue("ITEM_ID");
      identity = mgr.getQueryStringValue("IDENTITY");
      setReadStatus(item_id,identity,true);
      
      if(mgr.isRWCHost())
      {
         try
         {
            String surl = url;
            if(url.indexOf("ifsweb:")<0 && url.indexOf(".page")>0)
            {
               int pos = url.indexOf("secured");
               surl = "ifsweb:"+url.substring(pos);
            }
            else if(url.indexOf(":")<0 && url.indexOf("ifs")!=0)
               error(new FndException("Unknown URL"));
            appendDirtyJavaScript("javascript:window.external.Navigate(\""+surl+"\");\n");
         }catch(FndException fe){
            error(fe);
         }
      }
      else
      {
         if(url.indexOf(".page")<0 && url.indexOf(":")>0)
            mgr.redirectToAbsolute("/client/runtime/Ifs.Fnd.Explorer.application?"+url);
         else
            mgr.redirectTo(url.replaceFirst("ifsweb:secured",mgr.getCSVValue("#BASE_URL#")));
      }
   }
   
   /**
    * Open Mail client object ready to send a mail with values all set except recipient.
    * @return ajax string
    */
   private String openMailCLient() {
      initParams();
      ASPManager mgr = getASPManager();
      String link = url.replaceFirst("ifsweb:secured",mgr.getCSVValue("#BASE_URL#"));
      return "EVAL^"+"window.open('mailto:%20?Subject='+escape('"+title+"')+'&Body='+escape('"+link+"'));";
   }
   
   /**
    * Shows a list of shared userd.
    * @return ajax string
    */
   private String getSharedUsers() {
      MyTodoItem item = new MyTodoItem();    
      ASPManager mgr = getASPManager();
      String resp = "";
      item_id = mgr.getQueryStringValue("ITEM_ID");
      if(!mgr.isEmpty(item_id))
      {
         try{
            MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) getASPBlock("MYTODO_ITEM").getDataAdapter();
            String users[] = adapter.getSharedUsers(item_id);
            if(users.length > 0)
            {
               String _users = "";
               for(int i=0; i<users.length; i++)
                  _users += users[i]+"^";
               resp = "DONE^"+_users;
            }
            else
               resp = "Error^"+mgr.translateJavaScript("FWWEBFEATUREMYTODONOUSERS: No Shared Users");
         }
         catch(Exception e)
         {
            resp = "Error^"+e.getMessage();
         }
      }
      else
         resp = "Error^"+mgr.translateJavaScript("FWWEBFEATUREMYTODONOITEMID: Task ID is required to find users.");
      
      return resp;
   }
   
   /**
    * Gets a list of userid's
    * @return a comma/semicolan separetaed list of userId's
    */
   private String getUsers() {
      ASPManager mgr = getASPManager();
      String names = "";
      String ids   = "";

      String users = mgr.getQueryStringValue("USERS");
      PersonInfoArray persons = ((MyToDoTaskAdapter) blk.getDataAdapter()).getPersons(users);
      if(persons==null || persons.getLength()==0) return "USERS^";
      for(int p=0; p<persons.getLength(); p++){
         names += persons.get(p).name.getValue() + "; ";
         ids   += persons.get(p).userId.getValue() + ";";
      }
      return "USERS^"+names.trim()+"^"+ids.trim();
   }

   /**
    * Shows the send to dialog popup.
    * @return ajax string
    */
   private String showSendToPopup() {
      initParams();
      return "POPUPHTML^390^"+getPopupContents("SAVETASK_2"); // Format POPUPHTML^width^contents
   }
   
   /**
    * Initialize parameter values
    */
   private void initParams() {
      ASPManager mgr = getASPManager();
      String tmp = "";
      
      tmp = mgr.getQueryStringValue("title");
      if(!mgr.isEmpty(tmp))
         title = mgr.URLDecode(tmp);
            
      keys = mgr.getQueryStringValue("keys");
      if(!mgr.isEmpty(keys))
      {
         keys = mgr.URLDecode(keys);
         StringTokenizer st = new StringTokenizer(keys, ",;^");
         tmp = "";
         boolean more = true;
         while(st.hasMoreTokens())
         {
            more = !more;
            String key = st.nextToken();
            String value = mgr.getQueryStringValue(key);
            title += ((more)?" : ":" - ") + value;
            tmp += ((more)?"&":"") + key + "="+value;
            more = false;
         }
         keys=tmp;
      }
      
      tmp = mgr.getQueryStringValue("url");
      if(!mgr.isEmpty(tmp))
      {
         url = mgr.URLDecode(tmp);
         if(url.indexOf("ifsweb")<0)
            url = "ifsweb:"+url;
         url = url+"?SEARCH=Y&"+keys+"&CASESENCETIVE=TRUE";
      }
      
      tmp = mgr.getQueryStringValue("busobj");
      if(!mgr.isEmpty(tmp))
         busobj = mgr.URLDecode(tmp);      
   }
   
   /**
    * Saves a MyTodoItem
    * @return ajax string
    */
   private String saveSendTo() {
      ASPManager mgr = getASPManager();
      PostNewItem postItem = new PostNewItem();

      String output = "ERROR^" + mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDERROR: Could not send due to error.");
      String _receivers = mgr.getQueryStringValue("RECEIVERS");
      
      if(mgr.isEmpty(_receivers)) return "ERROR^" + mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDUSERERROR: Could not Send due to empty receivers list.");
      
      String _title = mgr.getQueryStringValue("TITLE");
      String _url = mgr.getQueryStringValue("URL");
      String _priority = mgr.getQueryStringValue("PRIORITY");
      String _message = mgr.getQueryStringValue("MESSAGE");
      String _busobj = mgr.getQueryStringValue("BUSOBJ");
      
      MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) blk.getDataAdapter();      
      PersonInfoArray persons = adapter.getPersons(_receivers);
      
      try {
         postItem.title.setValue(_title);
         postItem.message.setValue(_message);
         postItem.priority.setValue(TodoPriorityEnumeration.Enum.parseString(_priority));
         postItem.itemType.setValue(TodoItemTypeEnumeration.Enum.SELECTION);
         postItem.businessObject.setValue(_busobj);
         postItem.url.setValue(mgr.URLDecode(_url));
         if(persons!=null)
            postItem.receivers.add(persons);

         if(adapter.postNewItem(postItem))
            output = "DONE^TRUE";

      } catch (ApplicationException ex) {
         ex.printStackTrace();
         output = "ERROR^"+ex.getMessage();
      } catch (ParseException px) {
         px.printStackTrace();
         output = "ERROR^"+px.getMessage();
      } catch (Exception e){
         e.printStackTrace();
         output = "ERROR^"+e.getMessage();
      }
      
      return output;
   }
   
   /**
    * Returns a the Primary Kyes for the MyTodoItem View.
    * @return comma separated list of Primary keys.
    */
   private String getMyTodoPrimarKeys() {
      return  "ITEM_ID,IDENTITY";
   }
   
   /**
    * Created a popup menu for the MyTodoItem Priority list.
    * @return Priority popup object
    */
   private ASPPopup priorityMenu() {
      ASPManager mgr = getASPManager();
      String img_loc = mgr.getASPConfig().getImagesLocation();
      
      ASPPopup priority_popup = newASPPopup("PriorityMenu");
      priority_popup.setParameterFields(getMyTodoPrimarKeys());
      priority_popup.addItem(img_loc+"MyToDoItemPriorityHigh16x16.gif", "FWWEBFEATUREMYTODOPRIORITYHIGH: High Priority","javascript:setPriority(0, 'MyToDoItemPriorityHigh16x16.gif');");
      priority_popup.addItem("FWWEBFEATUREMYTODOPRIORITYNORMAL: Normal Priority","javascript:setPriority(1, 'table_empty_image.gif');");
      priority_popup.addItem(img_loc+"MyToDoItemPriorityLow16x16.gif","FWWEBFEATUREMYTODOPRIORITYLOW: Low Priority","javascript:setPriority(2, 'MyToDoItemPriorityLow16x16.gif');");
      return priority_popup;
   }
   
   /**
    * Created a popup menu for the MyTodoItem followup flag list.
    * @return flag popup object
    */
   private ASPPopup followUpFlagMenu() {
      ASPManager mgr = getASPManager();
      String img_loc = mgr.getASPConfig().getImagesLocation();
      
      ASPPopup flag_popup = newASPPopup("FollowUpFlagMenu");
      flag_popup.setParameterFields(getMyTodoPrimarKeys());
      flag_popup.addItem(img_loc+"MyToDoItemFollowUpRedFlag16x16.gif", "FWWEBFEATUREMYTODOREDFLAG: Red Flag","javascript:setFlag(0, 'MyToDoItemFollowUpRedFlag16x16.gif');");
      flag_popup.addItem(img_loc+"MyToDoItemFollowUpBlueFlag16x16.gif", "FWWEBFEATUREMYTODOBLUEFLAG: Blue Flag","javascript:setFlag(1, 'MyToDoItemFollowUpBlueFlag16x16.gif');");
      flag_popup.addItem(img_loc+"MyToDoItemFollowUpYellowFlag16x16.gif", "FWWEBFEATUREMYTODOYELLOWFLAG: Yellow Flag","javascript:setFlag(2, 'MyToDoItemFollowUpYellowFlag16x16.gif');");
      flag_popup.addItem(img_loc+"MyToDoItemFollowUpGreenFlag16x16.gif", "FWWEBFEATUREMYTODOGREENFLAG: Green Flag","javascript:setFlag(3, 'MyToDoItemFollowUpGreenFlag16x16.gif');");
      flag_popup.addSeparator();
      //flag_popup.addItem("FWWEBFEATUREMYTODOADDREMINDER: Add Reminder"); //Todo
      flag_popup.addItem("FWWEBFEATUREMYTODOCLEAR: Clear Flag","javascript:setFlag(-1, 'table_empty_image.gif');"); // Todo
      return flag_popup;
   }
           
   /**
    * Draws the contents of the poppup window.
    * @param action To which action it draws the contents for.
    * @return the contest as a HTML string.
    */
   private String getPopupContents(String action) {
      ASPManager mgr = getASPManager();
      AutoString html = new AutoString();
      TodoPriorityEnumeration priorityEnum = new TodoPriorityEnumeration();
      TodoFlagEnumeration flagEnum = new TodoFlagEnumeration();
      String dialogTitle = "";
      
      if(action.equals("REASSIGN_2")){
         dialogTitle = mgr.translateJavaScript("FWWEBFEATUREMYTODOREASSIGNTOCOLEEAGE: Reassign");
      }else{
         dialogTitle = mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDTOCOLEEAGE: Send to Colleague");
      }
      
      html.append( "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"pageCommandBar\" height=\"22\">"+
                   "<tr><td>&nbsp;&nbsp;"+dialogTitle+"</td></tr></table>\n");
      html.append("<table  align=\"center\" cellpadding=\"3\" border=\"0\" width=\"90%\" class=\"normalTextValue\">\n");
      html.append("	<tr>\n");
      html.append("		<td class=\"normalTextLabel\">");      
      html.append(mgr.HTMLEncode(mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDTORECEIVER: To"),true));      
      html.append("</td>\n");
      html.append("		<td colspan=\"2\" class=\"normalTextLabel\">");
      html.append("<input type=\"hidden\" id=\"__ITEM_ID\" name=\"__ITEM_ID\" value=\""+item_id+"\">");
      html.append("<input type=\"hidden\" id=\"__IDENTITY\" name=\"__IDENTITY\" value=\""+identity+"\">");
      html.append("<input class=\"editableTextField\" size=\"60\" type=\"text\" id=\"__USERS\" name=\"__USERS\" onblur=\"javascript:this.value = getUsers_(this, document.getElementById('__IDENTITY'), document.getElementById('__SENDTOBUTTON'));\">");
      html.append("</td>\n");
      html.append("	</tr>\n");
      html.append("	<tr>\n");
      html.append("		<td nowrap class=\"normalTextLabel\">");
      html.append(mgr.HTMLEncode(mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDTOTITLE: Title"),true));
      html.append("</td>\n");
      html.append("		<td nowrap>");
      html.append("<input type=\"hidden\" id=\"__BUSOBJ\" name=\"__BUSOBJ\" value=\""+busobj+"\">");
      html.append("<input type=\"hidden\" id=\"__URL\" name=\"__URL\" value=\""+url+"\">");
      html.append("<input class=\"editableTextField\" size=\"47\" type=\"text\" id=\"__TITLE\" name=\"__TITLE\" value=\""+title+"\">");
      html.append("</td>\n");
      html.append("		<td nowrap width=\"100%\">\n");
      html.append("         <select class=\"selectbox\" id=\"__PRIORITY\" name=\"__PRIORITY\">\n");
      html.append("            <option value=\""+priorityEnum.HIGH.getValue()+"\" "+(priorityEnum.HIGH.getValue().equalsIgnoreCase(priority)?"selected": "")+">"+priorityEnum.HIGH.getDisplayText()+"</option>\n");
      html.append("            <option value=\""+priorityEnum.NORMAL.getValue()+"\" "+((priorityEnum.NORMAL.getValue().equalsIgnoreCase(priority)||action.equals("SAVETASK_2"))?"selected": "")+">"+priorityEnum.NORMAL.getDisplayText()+"</option>\n");
      html.append("            <option value=\""+priorityEnum.LOW.getValue()+"\" "+(priorityEnum.LOW.getValue().equalsIgnoreCase(priority)?"selected": "")+">"+priorityEnum.LOW.getDisplayText()+"</option>\n");
      html.append("         </select>\n");
      html.append("</td>\n");
      html.append("	</tr>\n");
      html.append("	<tr>\n");
      html.append("		<td nowrap colspan=\"3\" class=\"normalTextLabel\">");
      html.append(mgr.HTMLEncode(mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDTOMESSAGE: Message"),true));
      html.append("</td>\n");
      html.append("	</tr>\n");
      html.append("	<tr>\n");
      html.append("		<td nowrap colspan=\"3\">");
      html.append("<textarea class=\"editableTextArea\" id=\"__MESSAGE\" name=\"__MESSAGE\" cols=\"64\" rows=\"7\">"+item_message+"</textarea>");
      html.append("</td>\n");
      html.append("	</tr>\n");
      html.append("	<tr>\n");
      html.append("		<td nowrap colspan=\"3\" align=\""+(mgr.isRTL()?"left":"right")+"\">");
      html.append("<input id=\"__SENDTOBUTTON\" disabled class=\"button\" onclick=\"javascript:saveTask('"+action+"', document.getElementById('__ITEM_ID'), document.getElementById('__IDENTITY'),document.getElementById('__TITLE'),document.getElementById('__URL'),document.getElementById('__PRIORITY'),document.getElementById('__MESSAGE'),document.getElementById('__BUSOBJ'));\" type=\"button\" value=\""+mgr.translateJavaScript("FWWEBFEATUREMYTODOSENDBUTTON: Send")+"\" name=\"Send\">");
      html.append("<input class=\"button\" onclick=\"javascript:closePopupBrowser_();\" type=\"button\" value=\""+mgr.translateJavaScript("FWWEBFEATUREMYTODOCANCELBUTTON: Cancel")+"\" name=\"Cancel\">");
      html.append("</td>\n");
      html.append("	</tr>\n");
      html.append("</table>\n");
      
      return html.toString();
   }
   
   /**
    * Append a customised ajax call to the body of the webpage to generate the popup dialog.
    */
   private void appendCustomCall(String action) throws FndException {
      ASPManager mgr = getASPManager();
      ASPRowSet rset = blk.getASPRowSet();
      int pos = rset.getRowSelected();
      item_id = rset.getValueAt(pos, "ITEM_ID");
      identity = rset.getValueAt(pos, "IDENTITY");
      priority = rset.getValueAt(pos, "ITEM.PRIORITY");
      title = rset.getValueAt(pos, "ITEM.TITLE");
      item_message = rset.getValueAt(pos, "ITEM.ITEM_MESSAGE");
      appendDirtyJavaScript("showNewPopUpBrowser_(CURRENT_URL+'ACTION="+action+"&ITEM_ID="+item_id+"&IDENTITY="+identity+"&PRIORITY="+priority+"&TITLE='+URLClientEncode(\""+title+"\")+'&ITEM_MESSAGE=' + URLClientEncode(\""+item_message+"\") );\n");
   }
   
   /**
    * Show popup dialog for reassigning tasks.
    */
   private String showReassignPopup() {
      initPopupParams();
      return "POPUPHTML^390^"+getPopupContents("REASSIGN_2");
   }
   
   /**
    * Show popup dialog for sharing tasks.
    */
   private String showSharePopup() {
      initPopupParams();
      return "POPUPHTML^390^"+getPopupContents("SHARE_2");
   }
   
   /**
    * Initialize the parameters sent by the sent to popup dialog.
    */
   private void initPopupParams() {
      ASPManager mgr = getASPManager();
      item_id = mgr.getQueryStringValue("ITEM_ID");
      identity = mgr.getQueryStringValue("IDENTITY");
      priority = mgr.getQueryStringValue("PRIORITY");
      title = mgr.getQueryStringValue("TITLE");
      item_message = mgr.getQueryStringValue("ITEM_MESSAGE");      
   }
   
   /**
    * Used to either reassign or share existing tasks.
    * @param modyfy_type the modification type to be done. i.e.: REASSIGN_TASK or SHARE_TASK
    */
   private String modifyTask(int modify_type) {
      ASPManager mgr = getASPManager();
      
      String output = "ERROR^";
      if(modify_type==REASSIGN_TASK)
         output+=mgr.translateJavaScript("FWWEBFEATUREMYTODOREASSIGNERR: Could not re-assign task due to error.");
      else if (modify_type==SHARE_TASK)
         output+=mgr.translateJavaScript("FWWEBFEATUREMYTODOSHAREERR: Could not share task due to error.");
      
      String _item_id = mgr.getQueryStringValue("ITEM_ID");
      String _identity = mgr.getQueryStringValue("IDENTITY");
      String _priority = mgr.getQueryStringValue("PRIORITY");
      String _title = mgr.getQueryStringValue("TITLE");
      String _message = mgr.getQueryStringValue("MESSAGE");
      String _receivers = mgr.getQueryStringValue("RECEIVERS");
      
      try {
         MyToDoTaskAdapter adapter = (MyToDoTaskAdapter) blk.getDataAdapter();

         PersonInfoArray persons = adapter.getPersons(_receivers);
         
         MyTodoItem item = adapter.getMyTodoItem(_item_id, _identity);
         item.item().itemMessage.setValue(_message);
         
         TodoPriorityEnumeration priorityEnum = new TodoPriorityEnumeration();         
         if(_priority.equals(priorityEnum.HIGH.toString()))
            item.item().priority.setValue(priorityEnum.HIGH);
         else if(_priority.equals(priorityEnum.NORMAL.toString()))
            item.item().priority.setValue(priorityEnum.NORMAL);
         else if(_priority.equals(priorityEnum.LOW.toString()))
            item.item().priority.setValue(priorityEnum.LOW);
         
         if(modify_type==REASSIGN_TASK)
            adapter.reassignItem(item, persons);
         else if (modify_type==SHARE_TASK)
            adapter.shareItem(item, persons);
         
         output = "DONE^Javascript:commandSet('populate','')";
         
      } catch (FndException e1) {
         e1.printStackTrace();
      } catch (ApplicationException e2) {
         e2.printStackTrace();
      }
      
      return output;
   }
   
      
   // ==========================================================================================================================
   // Public Custom Command Methods
   // ==========================================================================================================================
   
   /**
    * re populate the block
    */
   public void populate(){
       autoPopulate(blk);
   }
   
   /**
    * Mark the MyTOdoItem as read
    */
   public void markAsRead() {
      FndDataSet rowset = ((FndDataSet)blk.getASPRowSet());
      if(lay.isMultirowLayout())
         rowset.goTo(rowset.getRowSelected());
      setReadStatus(rowset.getValue("ITEM_ID"), rowset.getValue("IDENTITY"), true);
      blk.getASPRowSet().refreshRow();
   }
   
   /**
    * Mark the MyTOdoItem as unread
    */
   public void markAsUnRead() {
      FndDataSet rowset = ((FndDataSet)blk.getASPRowSet());
      if(lay.isMultirowLayout())
         rowset.goTo(rowset.getRowSelected());
      setReadStatus(rowset.getValue("ITEM_ID"), rowset.getValue("IDENTITY"), false);
      blk.getASPRowSet().refreshRow();
   }
   
   /**
    * Reassign task.
    */
   public void reAssign() throws FndException{
      appendCustomCall("REASSIGN_1");
   }
   
   /**
    * Share task with another user.
    */
   public void share() throws FndException{
      appendCustomCall("SHARE_1");
   }   
   
}
