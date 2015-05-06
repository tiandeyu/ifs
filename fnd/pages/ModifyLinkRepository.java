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
 * File        : ModifyLinkRepository.java
 * Description : Edit my-links saved in the Database (Repository)
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Sampath  2003-05-23 - Created
 *    ChandanaD2004-05-12 - Updated for the use of new style sheets.
 * ---------------------------------------------------------------------------- 
 * New Comments:
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 *
 * Revision 1.5  2007/01/23 14:19:15  buhilk
 * Bug 63032, Modifed printContents() method to show errors for illegal characters in link descriptions.
 *
 * Revision 1.4  2005/04/08 06:05:37  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.3  2005/02/11 09:12:11  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.2  2005/02/02 08:22:19  riralk
 * Adapted global profile functionality to new profile changes
 *
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/07/01 buhilk Bug Id 91498, Changed printContents(), fetchDBLinks(), submitCustomization()
 * 2006/09/24 mapelk Bug Id 59842, Improved CSV code 
 * 2006/09/11 buhilk
 * Bug 59842, Modified submitCustomization() to encode the links with CSV values
 *
 * 2006/08/10 buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.*;

import java.util.*;


public class ModifyLinkRepository extends ASPPageProvider
{
   
   private ASPBlock blk;
     
   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================
   private transient ASPBuffer                dblinks;
   private transient ASPTransactionBuffer    trans;
   private transient boolean                 dblinks_fetched;
   private boolean is_submit;
   
   
   
   /** Creates a new instance of ModifyLinkRepository */
   public ModifyLinkRepository(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   protected String getDescription()
   {
      return "FNDEDITLINKREPOSITORYDESC: Modify Link Repository ";
   }
   
   protected String getTitle()
   {
      return "FNDEDITLINKREPOSITORYTITLE: Modify Link Repository";
   }
      
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      blk = newASPBlock("DUMMY");
      blk.addField("PROFILE");
      blk.addField("URL");
      ASPForm frm = getASPManager().getASPForm();
                  
      disableNavigate();
      disableValidation();
      disableHomeIcon();
      disableOptions();
            
   }
   
   protected void run()
   {
      ASPManager mgr = getASPManager();
      if("TRUE".equals(mgr.readValue("__VALUE_SUBMITTED")))
      {
         is_submit = true;
         submitCustomization();
      }
      
      if(DEBUG) debug(this+": ModifyLinkRepository.run()");
      fetchDBLinks();            
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean is_explorer = mgr.isExplorer();
      
      beginDataPresentation();
      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
      appendToHTML("<tbody>\n"); 
      appendToHTML("<tr>\n");
      appendToHTML("<td nowrap height=22>&nbsp;\n");
      appendToHTML(mgr.translate("FNDMODIFYLINKREPMODIFYLINKREP: Modify Link Repository")+ "&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
      
      appendToHTML("<table border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
      appendToHTML("<tr><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td><td>");
      beginTable("",true,true);
      beginTableBody();
         beginTableCell(3,0,LEFT);
            printReadLabel("FNDMODIFYLINKREPLINKREP: Link Repository");
         endTableCell();
         nextTableRow();
            beginTableCell(0,0,LEFT,"",true);
               printMandatorySelectBox("DBLINKS",dblinks,"","onClick=\"javascript:selectClicked();\" style=\"width:180px;\"",10);
            endTableCell();
            beginTableCell();
               beginTable("",true,false);
               beginTableBody();
                  beginTableCell();
                     printScriptImageLink(mgr.getASPConfig().getImagesLocation()+"/portlet_up.gif","moveItemUp");
                  endTableCell();
                  nextTableRow();
                     beginTableCell();
                        printSpaces(2);
                     endTableCell();
                  nextTableRow();
                     beginTableCell();
                        printScriptImageLink(mgr.getASPConfig().getImagesLocation()+"/portlet_down.gif","moveItemDown");
                     endTableCell();
                  endTableBody();
                  endTable();
            
            endTableCell();
            beginTableCell();
               printText("FNDLINKREPINFOMSG: Use the arrows to reorder the links.  Select a link from the list box and edit it's URL and it's Description. Press 'Save & Return' when done.");
            endTableCell();
         nextTableRow();
            beginTableCell(3,0,LEFT);
               printSpaces(3);
               printScriptLink("FNDMODIFYREPOSITORYADDNEW: Add new","addDbLink");
               printSpaces(3);
               printScriptLink("FNDMODIFYREPOSITORYDELETE: Remove","deleteMyLinks");
            
            endTableCell();
         nextTableRow();
            beginTableCell();
               printSpaces(1);
            endTableCell();
         nextTableRow();    
         nextTableRow();
            beginTableCell(LEFT);
               printReadLabel("FNDMODIFYREPOSITORYURL: URL");
            endTableCell();
            beginTableCell(2,0,LEFT);
               printField("REPOSITORY_URL","","",60);
            endTableCell();
         nextTableRow();
            beginTableCell(LEFT);
               printReadLabel("FNDMODIFYREPOSITORYDESCRIPTION: Description");
            endTableCell();
            beginTableCell(2,0,LEFT);
               printField("REPOSITORY_DESCRIPTION","","",60);
            endTableCell();
         nextTableRow();
            beginTableCell(LEFT);
               printSpaces(1);
            endTableCell();
            beginTableCell(2,0,LEFT);
               printCheckBox("FNDMODIFYREPOSITORYCSVDECODE: Decode CSV Parameters", "REPOSITORY_CSVDECODE", "DECODE", false, "");
            endTableCell();
         nextTableRow();
            beginTableCell(3,0,LEFT);
               printSpaces(1);
            endTableCell();
         nextTableRow();
            beginTableCell(3,0,LEFT);
               printText("FNDMODIFYREPOSITORYCSVDECODEWARN: Note: Decoding CSV parameters might cause abnormal behaviours in certain links.");
            endTableCell();
         nextTableRow();
            beginTableCell();
               printSpaces(1);
            endTableCell();
         nextTableRow();
            beginTableCell(0,0,LEFT,"",true);
               printHiddenField("DBLINKSLIST", "");
               printHiddenField("__VALUE_SUBMITTED","");
               
               String tag = " onclick=\"javascript:assignValue()\"" +
                   " class='button'";
                      
      
               printButton("OK","FNDPAGESEDITMYLINKSREPOSITORYSAVE: Save & Return", tag);
               appendToHTML("&nbsp;");
      
               tag = " onclick=\"javascript:window.close();\""+
                 " class='button'";
                       
               printButton("CANCEL","FNDPAGESEDITMYLINKSREPOSITORYCANCEL: Cancel", tag);
      
            endTableCell();
      
      endTableBody(); 
      endTable();  
      appendToHTML("</td><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td></tr>");
      appendToHTML("</table>");
      endDataPresentation(false);
      
       
      if (is_submit)
          appendDirtyJavaScript("window.close();");
         else 
         {
             
             appendDirtyJavaScript("__SELECT_A_LINK_TO_DELETE_MSG = \""+mgr.translateJavaScript("FNDEDITREPSELECTALINKTODELETE: Select a link to Delete")+"\"\n");
             appendDirtyJavaScript("__REPOSITORY_DELETE_CONFERM_MSG  = \""+mgr.translateJavaScript("FNDREPOSITORYDELETECONFERM: The Link will be Deleted")+"\"\n\n");
             appendDirtyJavaScript("__REPOSITORY_VALUES_MISSING_MSG  = \""+mgr.translateJavaScript("FNDREPOSITORYVALUESMISSING: One or more URL(s) missing ")+"\"\n\n");
             appendDirtyJavaScript("__REPOSITORY_DESC_ILLEGAL_CHAR_MSG  = \""+mgr.translateJavaScript("FNDREPOSITORYDESCILLEGALCHAR: One or more Description(s) contain following illegal charachters:")+"\"\n\n");
             
             appendDirtyJavaScript("var selectedlinkno=-1;\n\n");
             appendDirtyJavaScript("function selectClicked()\n");
             appendDirtyJavaScript("{\n");
             appendDirtyJavaScript("var box = getField_(\"DBLINKS\");\n");
   
             appendDirtyJavaScript("if (selectedlinkno >-1) \n");
             appendDirtyJavaScript("{   \n");
             appendDirtyJavaScript("  if(f.REPOSITORY_DESCRIPTION.value==\"\") \n");
             appendDirtyJavaScript("\t   f.REPOSITORY_DESCRIPTION.value = f.REPOSITORY_URL.value; \n");
             appendDirtyJavaScript("   box.options[selectedlinkno].text = f.REPOSITORY_DESCRIPTION.value;\n");
             appendDirtyJavaScript("   box.options[selectedlinkno].value = (f.REPOSITORY_CSVDECODE.checked?f.REPOSITORY_CSVDECODE.value:\"NULL\")+\"~\"+f.REPOSITORY_URL.value;\n");
             appendDirtyJavaScript("   selectedlinkno = -1;\n");
             appendDirtyJavaScript("}\n");
             appendDirtyJavaScript("var editurl;\n");
             appendDirtyJavaScript("var editline;\n");
             appendDirtyJavaScript("var decodecsv;\n");
             appendDirtyJavaScript("var valselected = false;\n");
             appendDirtyJavaScript("for (i=0; i<box.length-1; i++ )\n");
             appendDirtyJavaScript("{\n");
             appendDirtyJavaScript("if(box.options[i].selected == true)\n");
             appendDirtyJavaScript(" {\n");
             appendDirtyJavaScript("    editline = box.options[i].text;\n");
             appendDirtyJavaScript("    var tmp = box.options[i].value;\n");
             appendDirtyJavaScript("    var pos = tmp.indexOf(\"~\");\n");
             appendDirtyJavaScript("    if(pos>=0){\n");
             appendDirtyJavaScript("      decodecsv = tmp.substr(0,pos);\n");
             appendDirtyJavaScript("      editurl = tmp.substr(pos+1);\n");
             appendDirtyJavaScript("    }\n");
             appendDirtyJavaScript("    selectedlinkno = i;\n");
             appendDirtyJavaScript("    valselected = true;\n");
             appendDirtyJavaScript(" }\n"); 
             appendDirtyJavaScript("}\n\n");
             appendDirtyJavaScript("if(valselected) \n");
             appendDirtyJavaScript(" {\n");
             appendDirtyJavaScript("\t f.REPOSITORY_URL.value=editurl;\n");
             appendDirtyJavaScript("\t f.REPOSITORY_DESCRIPTION.value=editline;\n");
             appendDirtyJavaScript("\t f.REPOSITORY_CSVDECODE.checked=(f.REPOSITORY_CSVDECODE.value===decodecsv);\n");
             appendDirtyJavaScript(" }\n");
             appendDirtyJavaScript("}\n\n\n");
             
             appendDirtyJavaScript("function checkDescriptions()\n");
             appendDirtyJavaScript("{\n");
             appendDirtyJavaScript("\t selectClicked();\n");
             appendDirtyJavaScript("\t var box = getField_(\"DBLINKS\");\n");
             appendDirtyJavaScript("\t var valid = true;\n");
             appendDirtyJavaScript("\t var slash = false;\n");
             appendDirtyJavaScript("\t var hat = false;\n");
             appendDirtyJavaScript("\t var str =\"\\n\";\n");
             appendDirtyJavaScript("\t for(k=0; k<box.length-1; k++)\n");
             appendDirtyJavaScript("\t {\n");
             appendDirtyJavaScript("\t\t val = box.options[k].text;\n");
             appendDirtyJavaScript("\t\t if(val.indexOf(\"/\")>-1 && !slash)\n");
             appendDirtyJavaScript("\t\t {\n");
             appendDirtyJavaScript("\t\t\t valid = false;\n");
             appendDirtyJavaScript("\t\t\t slash = true;\n");
             appendDirtyJavaScript("\t\t\t str += \" /\";\n");
             appendDirtyJavaScript("\t\t }\n");
             appendDirtyJavaScript("\t\t if(val.indexOf(\"^\")>-1 && !hat)\n");
             appendDirtyJavaScript("\t\t {\n");
             appendDirtyJavaScript("\t\t\t valid = false;\n");
             appendDirtyJavaScript("\t\t\t hat = true;\n");
             appendDirtyJavaScript("\t\t\t str += \" ^\";\n");
             appendDirtyJavaScript("\t\t }\n");
             appendDirtyJavaScript("\t }\n");
             appendDirtyJavaScript("\t if(!valid)\n");
             appendDirtyJavaScript("\t\t ifsAlert(__REPOSITORY_DESC_ILLEGAL_CHAR_MSG + str);\n");
             appendDirtyJavaScript("\t return valid;\n");
             appendDirtyJavaScript("}\n");

             appendDirtyJavaScript("function assignValue()\n");
             appendDirtyJavaScript("{\n");
             appendDirtyJavaScript("\t if(checkDescriptions())\n");
             appendDirtyJavaScript("\t {\n");
             appendDirtyJavaScript("\t\t selectClicked();\n");
             appendDirtyJavaScript("\t\t if(checkAllEntries())\n");
             appendDirtyJavaScript("\t\t {\n");
             appendDirtyJavaScript("\t\t\t selectClicked();\n");
             appendDirtyJavaScript("\t\t\t saveLinkChanges();\n");
             appendDirtyJavaScript("\t\t\t f.__VALUE_SUBMITTED.value = \"TRUE\";\n");
             appendDirtyJavaScript("\t\t\t submit();\n");
             appendDirtyJavaScript("\t\t }\n");
             appendDirtyJavaScript("\t\t else\n");
             appendDirtyJavaScript("\t\t\t  ifsAlert(__REPOSITORY_VALUES_MISSING_MSG);\n");
             appendDirtyJavaScript("\t }\n");
             appendDirtyJavaScript("}\n\n");
             
             appendDirtyJavaScript("function saveLinkChanges()\n");
             appendDirtyJavaScript("{\n");
             appendDirtyJavaScript("\tvar lks = \"\";\n");
             appendDirtyJavaScript("\tvar box = getField_(\"DBLINKS\");\n");
             appendDirtyJavaScript("\tfor(i=0; i<box.length-1; i++)\n");
             appendDirtyJavaScript("\tlks = lks + box.options[i].text + \"~\" + box.options[i].value + \"^\";\n");
             appendDirtyJavaScript("\tgetField_(\"DBLINKSLIST\").value = lks;\n");
             appendDirtyJavaScript("}\n\n");
             
               appendDirtyJavaScript("function moveItemUp()\n");
               appendDirtyJavaScript("{\n");
               appendDirtyJavaScript("\t var box = getField_(\"DBLINKS\");\n");
               appendDirtyJavaScript("\t var selected = new Array();\n");
               appendDirtyJavaScript("\t var j=0;\n");
               appendDirtyJavaScript("\t for(i=0; i<box.length-1; i++) \n");
               appendDirtyJavaScript("\t {\n");
               appendDirtyJavaScript("\t\t if(box.options[i].selected == true)\n");
               appendDirtyJavaScript("\t\t {\n");
               appendDirtyJavaScript("\t\t\t if(i==0) break;\n");
               appendDirtyJavaScript("\t\t\t if(i > 0)\n");
               appendDirtyJavaScript("\t\t\t\t var index =i;\n");
               appendDirtyJavaScript("\t\t\t selectedlinkno = i; \n");
               appendDirtyJavaScript("\t\t\t break;\n");
               appendDirtyJavaScript("\t\t }\n");
               appendDirtyJavaScript("\t }\n");
               appendDirtyJavaScript("\t if( index>0 && index<box.length-1 )\n");
               appendDirtyJavaScript("\t { \n");
               appendDirtyJavaScript("\t\t var tmp = box.options[index-1].text;\n");
               appendDirtyJavaScript("\t\t var val = box.options[index-1].value;\n");
               appendDirtyJavaScript("\t\t box.options[index-1].text     = box.options[index].text;\n");
               appendDirtyJavaScript("\t\t box.options[index-1].value    = box.options[index].value;\n\n");
               appendDirtyJavaScript("\t\t box.options[index].text       = tmp;\n");
               appendDirtyJavaScript("\t\t box.options[index].value      = val;\n");
               appendDirtyJavaScript("\t\t box.options[index].selected   = false;\n");
               appendDirtyJavaScript("\t\t box.options[index-1].selected = true;\n");
               appendDirtyJavaScript("\t\t selectedlinkno = selectedlinkno-1;\n");
               appendDirtyJavaScript("\t }\n");
               appendDirtyJavaScript(" }\n");
         
             appendDirtyJavaScript("function moveItemDown()\n");
             appendDirtyJavaScript("\t {\n");
             appendDirtyJavaScript("\t\t var box = getField_(\"DBLINKS\");\n");
             appendDirtyJavaScript("\t var max = box.length-1; \n");
             appendDirtyJavaScript("\t\t var selected = new Array(); \n");
             appendDirtyJavaScript("\t\t var j = 0; \n");
             appendDirtyJavaScript("\t\t for(i=0; i<max; i++) \n");
             appendDirtyJavaScript("\t\t { \n");
             appendDirtyJavaScript("\t\t\t if(box.options[i].selected == true) \n");
             appendDirtyJavaScript("\t\t\t { \n");
             appendDirtyJavaScript("\t\t\t var index = i; \n");
             appendDirtyJavaScript("\t\t\t selectedlinkno = i; \n");
             appendDirtyJavaScript("\t\t\t  break; \n");
             appendDirtyJavaScript("\t\t\t } \n");
             appendDirtyJavaScript("\t\t } \n\n");
             appendDirtyJavaScript("\t var max = box.length-1; \n");
             appendDirtyJavaScript("\t if( index < max-1) \n");
             appendDirtyJavaScript("\t { \n");
             appendDirtyJavaScript("\t\t var tmp = box.options[index+1].text; \n");
             appendDirtyJavaScript("\t\t var val = box.options[index+1].value; \n");
             appendDirtyJavaScript("\t\t box.options[index+1].text     = box.options[index].text; \n");
             appendDirtyJavaScript("\t\t box.options[index+1].value    = box.options[index].value; \n\n");
             appendDirtyJavaScript("\t\t box.options[index].text       = tmp; \n");
             appendDirtyJavaScript("\t\t box.options[index].value      = val; \n");
             appendDirtyJavaScript("\t\t box.options[index].selected   = false; \n");
             appendDirtyJavaScript("\t\t box.options[index+1].selected = true; \n");
             appendDirtyJavaScript("\t\t selectedlinkno = selectedlinkno+1;\n");
             appendDirtyJavaScript("\t } \n");
             appendDirtyJavaScript("\t } \n\n");
      
             appendDirtyJavaScript("function deleteMyLinks() \n");
             appendDirtyJavaScript("{ \n");
             appendDirtyJavaScript("\t  var valselected = false; \n");
             appendDirtyJavaScript("\t var box = getField_(\"DBLINKS\"); \n");
             appendDirtyJavaScript("\t selectedlinkno = 0; \n");
             appendDirtyJavaScript("\t for (i=0; i<box.length-1; i++ ) \n");
             appendDirtyJavaScript("\t { \n");
             appendDirtyJavaScript("\t\t if(box.options[i].selected == true) \n");
             appendDirtyJavaScript("\t\t { \n");
             appendDirtyJavaScript("\t\t\t valselected = true; \n");
             appendDirtyJavaScript("\t\t } \n");
             appendDirtyJavaScript("\t } \n");
             appendDirtyJavaScript("\t if (!valselected) \n");
             appendDirtyJavaScript("\t { \n");
             appendDirtyJavaScript("\t\t ifsAlert(__SELECT_A_LINK_TO_DELETE_MSG); \n");
             appendDirtyJavaScript("\t\t selectedlinkno = -1; \n");
             appendDirtyJavaScript("\t } \n");
             appendDirtyJavaScript("\t else \n");
             appendDirtyJavaScript("\t { \n");
             appendDirtyJavaScript("\t\t if(confirm(__REPOSITORY_DELETE_CONFERM_MSG)) \n");
             appendDirtyJavaScript("\t\t\t removeRepLink(box); \n");
             appendDirtyJavaScript("\t\t else \n");
             appendDirtyJavaScript("\t\t\t selectedlinkno = -1; \n");
             appendDirtyJavaScript("\t } \n");
             appendDirtyJavaScript(" } \n\n");
             
             appendDirtyJavaScript(" function removeRepLink(box) \n");
             appendDirtyJavaScript(" { \n");
             appendDirtyJavaScript("\t for(i=0; i<box.length-1; i++) \n");
             appendDirtyJavaScript("\t { \n");
             appendDirtyJavaScript("\t\t if(box.options[i].selected == true) \n");
             appendDirtyJavaScript("\t\t { \n");
             appendDirtyJavaScript("\t\t\t  if(i < box.length) \n");
             appendDirtyJavaScript("\t\t\t { \n");
             appendDirtyJavaScript("\t\t\t\t box.options[i] = null; \n");
             appendDirtyJavaScript("\t\t\t\t f.REPOSITORY_URL.value=\"\"; \n");
             appendDirtyJavaScript("\t\t\t\t f.REPOSITORY_DESCRIPTION.value=\"\"; \n");
             appendDirtyJavaScript("\t\t\t\t f.REPOSITORY_CSVDECODE.cheked=false; \n");
             appendDirtyJavaScript("\t\t\t\t selectedlinkno = -1; \n");
             appendDirtyJavaScript("\t\t\t } \n");
             appendDirtyJavaScript("\t\t } \n");
             appendDirtyJavaScript("\t } \n");
             appendDirtyJavaScript(" } \n\n");
             
             appendDirtyJavaScript(" function addDbLink() \n");
             appendDirtyJavaScript(" { \n");
             appendDirtyJavaScript("selectClicked();\n");
             appendDirtyJavaScript("\t var box = getField_(\"DBLINKS\"); \n");
             appendDirtyJavaScript("\t var tmp = box.options[box.length-1].text; \n");
             appendDirtyJavaScript("\t box.options[box.length-1].text = \"New Link\"; \n");
             appendDirtyJavaScript("\t box.options[box.length] = new Option(tmp); \n");
             appendDirtyJavaScript("\t box.options[box.length-2].selected = true; \n");
             appendDirtyJavaScript("\t f.REPOSITORY_URL.value=\"\";  \n");
             appendDirtyJavaScript("\t f.REPOSITORY_DESCRIPTION.value=\"New Link\"; \n");
             appendDirtyJavaScript("\t f.REPOSITORY_CSVDECODE.cheked=false; \n");
             appendDirtyJavaScript("\t selectedlinkno = box.length-2; \n");
             appendDirtyJavaScript("\t } \n\n");
             
             appendDirtyJavaScript("\t function checkAllEntries()\n");
             appendDirtyJavaScript("\t {\n");
             appendDirtyJavaScript("\t\t var box = getField_(\"DBLINKS\");\n");
             appendDirtyJavaScript("\t\t var valid = true;\n");
             appendDirtyJavaScript("\t\t var str =\"\";\n");
             appendDirtyJavaScript("\t\t for(k=0; k<box.length-1; k++)\n");
             appendDirtyJavaScript("\t\t {\n");
             appendDirtyJavaScript("\t\t\t val = box.options[k].value.indexOf(\"~\");\n");
             appendDirtyJavaScript("\t\t\t str = box.options[k].value.substring(val+1);\n");
             appendDirtyJavaScript("\t\t\t if(str == \"\")\n");
             appendDirtyJavaScript("\t\t\t   valid = false;\n");
             appendDirtyJavaScript("\t\t }\n");
             appendDirtyJavaScript("\t\t return valid;\n");
             appendDirtyJavaScript("\t }\n");

 
        }
   }
   
   private void fetchDBLinks()
   {
      if(DEBUG) debug(this+": ModifyLinkRepository.fetchDBLinks()");

      if(dblinks_fetched) return;

      dblinks_fetched = true;
      ASPManager mgr = getASPManager();
     
      ASPBuffer buf = readGlobalProfileBuffer("Links",false);
      dblinks = mgr.newASPBuffer();
      
      if(buf!=null)
      {         
                    
         for (int i=0; i<buf.countItems();i++)
         {                  
                        
            String name = buf.getNameAt(i);
            name= name.substring(name.indexOf(ProfileUtils.ENTRY_SEP)+1,name.length());
            String value = buf.getValueAt(i);
            if(value.split("~").length==1) value = "NULL~"+value;
            ASPBuffer row = dblinks.addBuffer("DATA");
            
            row.addItem("VALUE",value);
            
            if(mgr.isEmpty(name))
               row.addItem("DESC",value);
            else
               row.addItem("DESC",name);
         }
      }
      
      ASPBuffer row = dblinks.addBuffer("DATA");
      row.addItem("VALUE"," ");
      row.addItem("DESC","______________________");
   }
   
   
   public void submitCustomization()
   {
      ASPManager mgr = getASPManager();
      String links = readAbsoluteValue("DBLINKSLIST");
      if(DEBUG) debug("  [submitCustomization] DBLINKSLIST: links="+links);
      ASPBuffer tmp = mgr.newASPBuffer();           
      ASPBuffer linksbuf = mgr.newASPBuffer(ProfileUtils.newProfileBuffer());
      
      if(!Str.isEmpty(links))
      {
         StringTokenizer pst = new StringTokenizer(links, "^");         
         int order=0;
         while( pst.hasMoreTokens() )
         {
            String[] link = pst.nextToken().split("~");
            String url = "";

            if(link.length>=2)
                url = link[link.length-2]+"~"+link[link.length-1];
            else
                url = "NULL~"+link[0];

            ASPBuffer row = tmp.addBuffer("DATA");
            row.addItem("VALUE",url);
            row.addItem("DESC",link[0]);
               
            //add to profile buffer
            String name = "Link_"+order + ProfileUtils.ENTRY_SEP + link[0];
            String value = url;
            linksbuf.addItem(name,value);
            order++;
         }  
      }
      
      removeGlobalProfileItem("Links");
      if(!Str.isEmpty(links))      
        writeGlobalProfileBuffer("Links",linksbuf, false);
       
   } 
   
}
