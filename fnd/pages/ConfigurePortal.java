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
*  File        : ConfigurePortal.java
*  Modified    :
*     Mangala - 2006-11-23 Created
*     Buddika - 2006-11-27 Minor improvements to code. Nothing significant.
*     Mangala - 2007-01-30 - Bug 63250, Added theming support in IFS clients.
*     Suneth  - 2007-08-01 - Merged the corrections for Bug 66964, Corrected localization errors.
*     Suneth  - 2009-10-16 - Bug 86164, Changed setViewOrder() to encode the view names.
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.io.File;
import java.util.StringTokenizer;


public class ConfigurePortal extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ConfigurePortal");
   private transient ASPBuffer available_views;
   
   private static final String VIEW_DESCRIPTION = "Description";
   private static final String VIEW_POSITION = "Position";
  


  //===============================================================
   // Construction
   //===============================================================
   public ConfigurePortal(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      disableNavigate();
      disableOptions();
      appendJavaScript("function setViewOrder(){\n");
      appendJavaScript("    keys=f.VIEWS.options[0].value;\n");
      appendJavaScript("    for (i=1;i<f.VIEWS.length;i++)\n");
      appendJavaScript("        keys = keys +  '^' + f.VIEWS.options[i].value;\n");
      appendJavaScript("    __connect(getCurrentUrl() + '?VALIDATE=SAVE&KEYS=' + URLClientEncode(keys));\n");
      appendJavaScript("    val = __getValidateValue(0);\n");
      appendJavaScript("    if (val == 'OK')\n");
      appendJavaScript("      {opener.document.location='",mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL"),"';window.close();}\n");
      appendJavaScript("    else\n");
      appendJavaScript("      alert('", mgr.translateJavaScript("FNDCONFIGPORTALCANNOTODER: Could not arrange portal views!"),"\\n'+val);}\n");
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         reorderViews();
      
   }
   
   private void drawViewSelectBox()
   {
      ASPBuffer available_views = getASPManager().newASPBuffer(getPortalViews());
      if (DEBUG)
      {
         debug("available_views...");
         available_views.traceBuffer("VIEWS");
      }
      if (available_views!=null)
      {
         int size = available_views.countItems();
         printSelectStart("VIEWS"," size=10");
         for (int i=0; i<size; i++)
         {
            printSelectOption(available_views.getValueAt(i),available_views.getNameAt(i),false);
            if (DEBUG)
            {
               debug(available_views.getValueAt(i) + " - " + available_views.getNameAt(i));
            }
         }
         printSelectEnd();
      }
   }

  
   private void reorderViews()
   {
      try
      {
         ASPManager mgr=getASPManager();
         String views = mgr.readValue("KEYS");
         String names = mgr.readValue("NAMES");
         StringTokenizer view_list = new StringTokenizer(views,"^");
         if (DEBUG)
         {
            debug("Reordering Views...");
            debug(view_list.countTokens()+ " views found");
         }
         
         ASPBuffer buf = readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS,false);
         int size = buf.countItems();
         int pos = 0;
         while (view_list.hasMoreTokens())
         {
            String key = view_list.nextToken();
            if (DEBUG)
               debug( " Setting position for " + key);
            for (int i=0; i<size; i++)
            {
               String temp = buf.getNameAt(i);
               if (temp!=null && temp.startsWith(key+"^"))
               {
                  if (DEBUG)
                     debug("Item found .. " + temp);
                  buf.getBufferAt(i).setValue("Position",pos+"");
               }
            }
            pos++;
         }
         
         writeGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS, buf,false);
         mgr.responseWrite("OK^");
         mgr.endResponse();
      }
      catch(Exception e)
      {
         getASPManager().responseWrite("ERROR: " + e.getMessage());
         getASPManager().endResponse();
      }
   }




//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESCONFIGUREPORTALDESC: Rearrange portal views";
   }

   protected String getTitle()
   {
      return "FNDPAGESCONFIGUREPORTALDESC: Rearrange portal views";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      beginDataPresentation();
      drawSimpleCommandBar("FNDPAGESCONFIGUREPORTALDESC: Rearrange portal views");
      appendToHTML( "<table width=100% cellspacing=0 cellpadding=0 border=0 class=pageFormWithBorder>");
      appendToHTML( "<tr><td>&nbsp;");
      printReadLabel("FNDPORTALCONFIGVIEWLIST: Portal View Names");
      appendToHTML("</td><td></td><td rowspan='2'><ul><li>");
      printText("FNDPORTALCONFIGVIEWINSTRUCTIONS1: Use the up/down arrows to set the order of the portal views.");
      appendToHTML("</li><li>");
      printText("FNDPORTALCONFIGVIEWINSTRUCTIONS2: Press Save & Close button to apply changes.");
      appendToHTML("</li></ul>");
      appendToHTML("</td></tr><tr><td rowspan='2'>&nbsp;");
      drawViewSelectBox();
      appendToHTML( "<br></td><td>");
      printImageLink(getASPConfig().getImagesLocation()+"portlet_up.gif","javascript:_moveSelectBoxItemUp(f.VIEWS)");
      appendToHTML( "</td></tr><tr><td>");
      printImageLink(getASPConfig().getImagesLocation()+"portlet_down.gif","javascript:_moveSelectBoxItemDown(f.VIEWS)");
      appendToHTML( "</td></tr><tr><td colspan='3'>&nbsp;</td></tr></table>");
      printNewLine();
      printButton("SAVE", "FNDPORTALCONFIGSAVE: Save & Close","OnClick='javascript:setViewOrder()'");
      printButton("CANCEL", "FNDPORTALCONFIGCANCEL: Cancel","OnClick='javascript:window.close()'");
      printHiddenField("KEYS","");
      printHiddenField("NAMES","");
      endDataPresentation(false);
      /**
      *  Do not remove this comment: need for localize to find terms
      *  f.setLabel("FNDPORTALCONFIGVIEWLIST: Portal View Names");
      */      
   }

}
