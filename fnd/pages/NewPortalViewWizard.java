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
*  File        : NewPortalViewWizard.java
*    Rahelk    2003-05-30 - Created
*    Rahelk    2003-07-26 - Log Id 1080, Implemented code for importing from xml  
*    Rahelk    2003-08-12 - added translate keys to some missing text.
*    Rahelk    2003-09-12 - added UPLOAD_FROM=__WEB_CLIENT.
*    Rahelk    2004-02-18 - Bug id 40903, added __CONTEXT_ID to query string when importing.
*    Rahelk    2004-03-05 - Bug id 43245, added SIZE depending on browser type.
*    Chandana D2004-05-12 - Updated for the use of new style sheets. 
*    Suneth M  2004-Aug-04 - Changed duplicate localization tags.
* ----------------------------------------------------------------------------
* New Comments:
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
* 2006/08/10 buhilk
* Bug 59442, Corrected Translatins in Javascript
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.3  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.2  2005/02/02 08:22:19  riralk
* Adapted global profile functionality to new profile changes
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class NewPortalViewWizard extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.NewPortalViewWizard");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPForm frm;
   private ASPContext ctx;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   private transient int step;
   private transient int new_view_type;
   private transient boolean finished;
   private transient String view_desc;
   private transient String existing_views_list;
   private transient String copy_from;
   
   private static final int NEW = 0;
   private static final int COPY = 1;
   private static final int IMPORT = 2;
   
   //===============================================================
   // Construction
   //===============================================================
   public NewPortalViewWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      new_view_type = ctx.readNumber("VIEW_TYPE",NEW);
      view_desc = ctx.readValue("VIEW_DESC","");
      existing_views_list = ctx.readValue("EXISTING_LIST","");
      copy_from = ctx.readValue("COPY_FROM","");
            
      if (mgr.buttonPressed("NEXT"))
         nextStep();
      else if (mgr.buttonPressed("FINISH") || !mgr.isEmpty(mgr.getQueryStringValue("UPLOAD_MODE")))
         finish();
      else if (mgr.buttonPressed("PREVIOUS") || !mgr.isEmpty(mgr.getQueryStringValue("__PAGE_ID"))) 
         previousStep();                         // ^ check previous from import step
      
      ctx.writeNumber("VIEW_TYPE",new_view_type);
      ctx.writeValue("VIEW_DESC", view_desc);
      ctx.writeValue("EXISTING_LIST", existing_views_list); 
      ctx.writeValue("COPY_FROM", copy_from);
   }


//=============================================================================
//  Button functions
//=============================================================================
   
   private void nextStep()
   {
      ASPManager mgr = getASPManager();

      new_view_type = (int)mgr.readNumberValue("VIEW_TYPE");
      if (mgr.isEmpty(view_desc))
         view_desc = mgr.readValue("NEW_VIEW_NAME");

      if (mgr.isEmpty(copy_from))
         copy_from = mgr.readValue("CURRENT_VIEW");

      existing_views_list = mgr.readValue("EXISTING_VIEWS");
      
      step++;
   }
   
   private void previousStep()
   {
      ASPManager mgr = getASPManager();
      view_desc = mgr.readValue("VIEW_DESC");
      copy_from = mgr.readValue("COPY_FROM");
      //step--;
   }
   
   private void finish()
   {
      ASPManager mgr = getASPManager();
      step = 1;
      view_desc = mgr.readValue("VIEW_DESC");
      if (mgr.isEmpty(view_desc))
      {
         mgr.showAlert(mgr.translate("FNDPAGENEWWIZARDEMPTY: View name can not be empty."));
         return;
      }
      
      String str = view_desc.trim().toUpperCase().replace(' ','_');
      str = str.replace('/','_');

      String[] existing_array = split(existing_views_list, ""+(char)30);

      for(int i=0; i<existing_array.length; i++)
      {
         String[] name_array = split(existing_array[i], ""+(char)31);
         if (str.equals(name_array[0]))
         {
            mgr.showAlert(mgr.translate("FNDPAGENEWWIZARDEXISTS: View with name '&1' already exists",view_desc));
            return;
         }
      }

      finished = true;
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();
      disableNavigate();
      disableValidation();
      disableOptions();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGENEWWIZARDDESC: Create new view wizard";
   }

   protected String getTitle()
   {
      return "FNDPAGENEWWIZARDDESC: Create new view wizard";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();

      out.clear();
      
      if (finished)
      {
         String home = mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")+"?__VIEW_NAME=";
         String config_now = "Y";
         String file_name = "";
         
         if (!"Y".equals(mgr.readValue("CONFIGURE_NOW")))
            config_now = "N";
         
         String type = "_NEW";
         
         if (new_view_type == COPY)
            type = "_COPY";
         else if (new_view_type == IMPORT)
         {
            type = "_IMPORT";
            file_name = mgr.getTempProfileFileName();
         }

         appendDirtyJavaScript("window.opener.location.href=\""+mgr.correctURL(home+type
                               +"&CUSTOMIZE="+config_now
                               +"&UPLOAD_FILENAME="+file_name
                               +"&_NEW_VIEW_NAME="+mgr.URLEncode(view_desc)
                               +"&_COPY_FROM="+mgr.readValue("COPY_FROM"))+"\";");
         
         appendDirtyJavaScript("window.close();");
         return out;
      }
      
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      
      if ((new_view_type == IMPORT) && (step == 1) )
         out.append("<form enctype=\"multipart/form-data\" name=\"form\" method=\"POST\" OnSubmit=\"javascript:return importSubmit()\">");
      else
      {
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(" >\n");
      }

      
      out.append(mgr.startPresentation(getDescription()));
      
      out.append("<table width=100% border=0 cellpadding=0 cellspacing=0>\n");
      out.append("<tr><td>&nbsp;&nbsp;</td><td>");

      out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
      out.append("<tr>\n");
      out.append("<td colspan=3 height=19>\n");      
      out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
      out.append("<td nowrap height=19>&nbsp;\n");
      if (step == 0)
         out.append( mgr.translateJavaText("FNDPAGENEWWIZARDSTEP1: Step 1") );
      else
         out.append( mgr.translateJavaText("FNDPAGENEWWIZARDSTEP2: Step 2") );
      out.append("&nbsp;</td>\n");
      out.append("</tr>\n");
      out.append("</table>\n");
      out.append("</td>\n");
      out.append("</tr>\n");
      
      out.append("<tr>\n");
      out.append("<td>\n");
      String imgloc = mgr.getASPConfig().getImagesLocation();
      printImage(imgloc+"create_portal_wizard.gif");
      out.append("</td>\n");
      out.append("<td width=100%>\n");
      if (step == 0)
         step1GUI(out);
      else
         step2GUI(out);
      out.append("</td>\n");
      out.append("<td>&nbsp;</td>");
      out.append("</tr>\n");
      out.append("</table>\n");
      
      //right page margin
      out.append("</td><td>&nbsp;&nbsp;</td></tr>");         
      out.append("</table>\n");
      
      
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      
      return out;
   }

   private void step1GUI(AutoString out) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      out.append("<table border=0 width=100% >\n");
      out.append("<tr><td>&nbsp;</td><td height=275>\n");
      out.append("<p><font class=boldTextValue>",mgr.translate("FNDPAGENEWWIZARDSTEPDET: Choose one of the following"));
      out.append("</font></p><br>\n");
      printRadioButton("FNDPAGENEWWIZARDNEW: New view", "VIEW_TYPE", "0", (new_view_type == NEW), "", 0);
      out.append("<br>\n");
      printRadioButton("FNDPAGENEWWIZARDCOPY: Copy of an existing view", "VIEW_TYPE", "1", (new_view_type == COPY), "", 1);
      out.append("<br>\n");
      printRadioButton("FNDPAGENEWWIZARDIMPORT: Import view", "VIEW_TYPE", "2", (new_view_type == IMPORT), "", 2);
      out.append("<br>\n");
      
      out.append("</td></tr>\n");
      out.append("<tr><td>&nbsp;</td><td align=right>\n");
      printSubmitButton("NEXT", "FNDPAGENEWWIZARDNEXT: Next", "");
      out.append("&nbsp;");
      printButton("CANCEL", "FNDPAGENEWWIZARDCANCEL: Cancel", "onclick=\"javascript:window.close();\"");
      out.append("</td>");
      out.append("</table>\n");
      out.append("<input type=hidden name=NEW_VIEW_NAME >\n");
      out.append("<input type=hidden name=EXISTING_VIEWS >\n");
      out.append("<input type=hidden name=CURRENT_VIEW >\n");
      
      appendDirtyJavaScript("init();");
      appendDirtyJavaScript("function init()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tf.NEW_VIEW_NAME.value=window.opener.NEXT_VIEW_NAME;\n");
      appendDirtyJavaScript("\tf.EXISTING_VIEWS.value=window.opener.EXISTING_VIEWS;\n");
      appendDirtyJavaScript("\tf.CURRENT_VIEW.value=window.opener.CURRENT_VIEW;\n");
      appendDirtyJavaScript("\t\n");
      appendDirtyJavaScript("}\n");
      
   }
   
   private void step2GUI(AutoString out) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      out.append("<table border=0 width=100% >\n");
      out.append("<tr><td>&nbsp;</td><td height=250>\n");

      out.append("<p><font class=boldTextValue>",mgr.translate("FNDPAGENEWWIZARDSTEPNEWDET: Enter the name of the new portal view"));
      out.append("</font></p>\n");
      
      out.append("<font class=normalTextLabel>",mgr.translate("FNDPAGENEWWIZARDSTEPVIEWNAME: New view name:"),"&nbsp;");
      out.append("</font>\n");
      printField("VIEW_DESC",view_desc,"",20);
      
      if (new_view_type == COPY)
         copyViewDialog(out);
      else if (new_view_type == IMPORT)
         importViewDialog(out);
      
      out.append("</td></tr>\n");
      
      out.append("<tr><td>&nbsp;</td><td>\n");
      out.append("<p>");
      out.append("<font class=boldTextValue>");
      printCheckBox("FNDPAGENEWWIZARDCONFIGNOW: Yes I want to configure this view now.","CONFIGURE_NOW", "Y", true, "");
      out.append("</font></p>\n");
      out.append("</td></tr>\n");
      
      out.append("<tr><td>&nbsp;</td><td align=right>\n");
      if (new_view_type != IMPORT)
      {
         printSubmitButton("PREVIOUS", "FNDPAGENEWWIZARDPREVIOUS: Previous", "");
         out.append("&nbsp;");
         printSubmitButton("FINISH", "FNDPAGENEWWIZARDFINISH: Finish", "");
      }
      else
      {
         printSubmitButton("PREVIOUS", "FNDPAGENEWWIZARDPREVIOUS: Previous", "onClick=doSubmit('previous')");
         out.append("&nbsp;");
         printSubmitButton("FINISH", "FNDPAGENEWWIZARDFINISH: Finish", "onClick=doSubmit('finish')");
      }
      out.append("&nbsp;");
      printButton("CANCEL", "FNDPAGENEWWIZARDCANCEL: Cancel", "onclick=\"javascript:window.close();\"");
      out.append("</td>");
      out.append("</table>\n");
   }
   
   private void copyViewDialog(AutoString out)
   {
      ASPManager mgr = getASPManager();
      
      out.append("<br><p><font class=boldTextValue>",mgr.translate("FNDPAGENEWWIZARDSTEPCOPYDET: Choose the existing view you wish to copy for this new view"));
      out.append("</font></p>\n");
      out.append("<font class=normalTextLabel>",mgr.translate("FNDPAGENEWWIZARDSTEPEXISTVIEW: Exsiting view name:"),"&nbsp;");
      out.append("</font>\n");
      
      printSelectStart("COPY_FROM", "");
      String[] existing_array = split(existing_views_list, ""+(char)30);
      for(int i=0; i<existing_array.length; i++)
      {
         String[] name_array = split(existing_array[i], ""+(char)31);
         printSelectOption(mgr.URLDecode(name_array[1]), name_array[0],(name_array[0].equals(copy_from)));
      }
      printSelectEnd();
      
   }

   private void importViewDialog(AutoString out) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      out.append("<p><br><br><font class=normalTextLabel>",mgr.translate("FNDPAGENEWWIZARDSTEPUPLOAD: Select the file to import from:"),"&nbsp;</font>");
      out.append("<br><br>");       
      out.append("<input type=hidden name=COMMAND_ID value=''>"); 
      //Bug id 43245, start
      out.append("<input TYPE =\"file\"  id=\"__upload_file\" name=\"__upload_file\" size=" + (mgr.isNetscape4x()?"40 ":"50 "));
      //Bug id 43245, end
      out.append("class=\"importportalViewField\"></p>");
      
      appendDirtyJavaScript("function doSubmit(comm_id)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("var action_str = ''; \n");
      appendDirtyJavaScript("\tf.COMMAND_ID.value = '';\n");
      appendDirtyJavaScript("action_str='"+getFormTagAction()+"?';\n");
      appendDirtyJavaScript("if (comm_id == 'finish')\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tf.COMMAND_ID.value = 'finish';\n");
      appendDirtyJavaScript("\taction_str +='UPLOAD_MODE=4&UPLOAD_FROM=__WEB_CLIENT';\n");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("if (document.form.CONFIGURE_NOW.checked)\n");
      appendDirtyJavaScript("\taction_str +='&CONFIGURE_NOW=Y';\n");
      
      appendDirtyJavaScript("action_str +='&VIEW_DESC='+URLClientEncode(f.VIEW_DESC.value)+");
      //bug id 40903 start
      appendDirtyJavaScript("'&__CONTEXT_ID='+f.__CONTEXT_ID.value+\n");
      //bug id 40903 end
      appendDirtyJavaScript("'&__PAGE_ID='+f.__PAGE_ID.value;\n");
      appendDirtyJavaScript("document.form.action=action_str; \n");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("function importSubmit()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if (f.COMMAND_ID.value == 'finish')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("\tif (f.__upload_file.value == '')\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\t alert('",mgr.translateJavaScript("FNDPAGENEWWIZARDSELFILETOIMP: Select the file to import from"),"');\n");
      appendDirtyJavaScript("\treturn false;\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("return true;\n");
      appendDirtyJavaScript("}\n");
   }
   
}
