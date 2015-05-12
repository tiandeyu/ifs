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
 * File                          : Templates.java
 * Description                   : 
 * Notes                         :
 * Other Programs Called         :
 * ----------------------------------------------------------------------------
 * Modified    :
 * 2009/02/13  buhilk - Created. F1PR454 - Templates IID.
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.service.FndException;

public class Templates extends ASPPageProvider
{
   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;
   
   private String queryString;

   /* Constructor */
   public Templates (ASPManager mgr, String page_path){
      super(mgr,page_path);
   }

   /* Framework methods */
   protected void preDefine() throws FndException {
      ASPManager mgr = getASPManager();
      
      headblk = mgr.newASPBlock("TEMPLATES");
      headblk.addField("GLOBAL_TEMPLATE_ID").
              setHidden();
      headblk.addField("TEMPLATE_ID").
              setHidden();
      headblk.addField("BLOCK").
              setHidden();
      headblk.addField("URL").
              setHidden();
      headblk.addField("DEFAULT").
              setHidden();
      headblk.addField("TITLE").
              setLabel("FNDPAGESTEMPLATETITLE: Title").
              setSize(20).
              setMandatory();
      headblk.addField("DESCRIPTION").
              setLabel("FNDPAGESTEMPLATEDESC: Description").
              setSize(50);
      headblk.addField("ENTITY").
              setLabel("FNDPAGESTEMPLATEENTITY: Entity").
              setReadOnly().
              setSize(50);
      headblk.addField("PAGE").
              setLabel("FNDPAGESTEMPLATECLASS: Page").
              setReadOnly().
              setSize(100);
      
      headblk.enableStandardCommands("Modify,Remove");
      
      ASPCommandBar headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.SAVERETURN, "saveTemplate");
      headbar.defineCommand(headbar.EDITROW, "editTemplate");
      headbar.defineCommand(headbar.CANCELEDIT, "cancelEdit");
      headbar.defineCommand(headbar.DELETE, "deleteTemplate","addPageToPool");
      headbar.disableCommand(ASPCommandBar.FIND);
      headbar.disableCommand(ASPCommandBar.NEWROW);
      
      headtbl = mgr.newASPTable(headblk);
      
      headset = headblk.getASPRowSet();
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
   }

   protected void run() throws FndException {
      ASPManager mgr = getASPManager();
      ASPContext con = mgr.getASPContext();
      
      queryString = con.readValue("PAGE_URL", mgr.readValue("TEMPLATE_PAGE_URL"));
      
      if(mgr.commandBarActivated()){
         if(!headlay.isMultirowLayout())
            headset.store();
         eval(mgr.commandBarFunction());
      }
      
      con.writeValue("PAGE_URL", queryString);
      
      createTemplateRows(false);
   }

   protected String getTitle() {
      return "FNDPAGESTEMPLATEBLOCKTITLE: Templates";
   }

   protected String getDescription() {
      return getTitle();
   }
   
   protected void printContents() throws FndException {
      appendToHTML(headlay.show());
      appendToHTML("<input type=hidden name=POOLKEY value=\"\">");
      
      if(headlay.isSingleLayout()){
         appendDirtyJavaScript("function addPageToPool(){\n"
                              +"\tvar url = '"+headset.getValue("URL")+"';\n"
                              +"\t__connect(url+\"?ADDTOPOOL=Y\");\n"
                              +"\tif(last_response_script[0]=='TRUE'){\n"
                              +"\t\tf.POOLKEY.value = last_response_script[1];\n"
                              +"\t\treturn true;\n"
                              +"\t}\n"
                              +"}\n");
      }
      else if(headlay.isMultirowLayout())
      {
         appendDirtyJavaScript("var pathArr = new Array(");
         for(int i=0; i<headset.countRows(); i++){
            if(i>0) appendDirtyJavaScript(",");
            appendDirtyJavaScript("'"+headset.getValue("URL")+"'");
         }
         appendDirtyJavaScript(");\n");
         appendDirtyJavaScript("function addPageToPool(){\n"
                              +"\tvar url = pathArr[tblRow1];\n"
                              +"\t__connect(url+\"?ADDTOPOOL=Y\");\n"
                              +"\tif(last_response_script[0]=='TRUE'){\n"
                              +"\t\tf.POOLKEY.value = last_response_script[1];\n"
                              +"\t\treturn true;\n"
                              +"\t}\n"
                              +"}\n");
      }
   }
   
   /* ===================================================================================
    *                            Page specefic functionality
    * =================================================================================== */
   
   private void createTemplateRows(boolean force) throws FndException {
      if(!force && headset!=null && headset.countRows()!=0) return;
      ASPBuffer buf = null;
      ASPManager mgr = getASPManager();
      ASPBuffer templates = readGlobalTemplateProfileBuffer(true);
      headset.clear();
      if(templates==null) return;
      for(int i=0; i<templates.countItems(); i++){
         buf = templates.getBufferAt(i);
         if(!mgr.isEmpty(queryString) && !(buf.getFieldValue("URL").equals(queryString))) continue;
         headset.addRow(buf);
      }
      headset.sort("URL");
      headset.first();
   }
   
   public void deleteTemplate() throws FndException{
      if(headlay.getLayoutMode()==headlay.MULTIROW_LAYOUT)
         headset.goTo(headset.getRowSelected());
      ASPManager mgr = getASPManager();
      ASPPage profilePage = mgr.getProfilePage(mgr.readValue("POOLKEY"));
      ASPBlock block = mgr.getASPBlockFromPage(profilePage,headset.getValue("BLOCK"));
      boolean is_default = "true".equals(headset.getValue("DEFAULT"));
      block.removeTemplate(headset.getValue("TEMPLATE_ID"),is_default);
      profilePage.getASPProfile().save(this);
      createTemplateRows(true);
      headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
   }
   
   public void editTemplate() throws FndException{
      if(headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      headlay.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);      
   }
   
   public void cancelEdit() throws FndException{
      headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      createTemplateRows(true);
   }
   
   public void saveTemplate() throws FndException{
      ASPManager mgr = getASPManager();
      headblk.updateTemplateProfileBuffer(headset.getRow(), null);
      getASPProfile().save(this);
      createTemplateRows(true);
      headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
   }
}