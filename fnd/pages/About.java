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
*  File        : About.java
*  Modified    :
*    ASP2JAVA Tool  2001-02-27  - Created Using the ASP file About.asp
*    Chaminda O.    2001-02-27  - Modifications after converting to Java
*    Slawek F       2001-06-06  - Added list of installed modules link
*    Daniel S       2001-10-16  - Changed the look.
*    Mangala P      2003-03-16  - Removed use of depricated method getConfigFilePath
*    Chandana D     2003-05-28  - Modified presentation for the new L&F.
*    Chandana D     2003-09-04  - Made to show the modules in the same window.
*    Chandana D     2003-09-22  - Changed the imge name to Corporate_Layer_About_Logo.gif
*    Ramila H       2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
*    Ramila H       2004-03-23  - Bug id 43431, added link to license and disclaimer html page.
*                                 Changed IFS/Applications to IFS Applications.  
*    Chandana D     2004-05-12 -  Updated for the use of new style sheets.
*    Mangala        2007-01-30 -  Bug 63250, Added theming support in IFS clients.
*    Sasanka D      2007-08-10 -  IFS Application logo align to the center.
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.io.File;


public class About extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.About");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPForm frm;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String ver;

   //===============================================================
   // Construction
   //===============================================================
   public About(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      ver   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      About page = (About)(super.clone(obj));

      // Initializing mutable attributes
      page.ver   = null;

      // Cloning immutable attributes
      page.frm = page.getASPForm();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      frm = mgr.getASPForm();
      ver = mgr.getVersion();
   }


   public void  preDefine()
   {

      disableHeader();
      disableBar();
      disableFooter();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESABOUTTITLE: IFS Applications - About";
   }

   protected String getTitle()
   {
      return "FNDPAGESABOUTWINDOWTITLE: IFS Applications - About";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPConfig cfg = getASPConfig();
      //String bgcolor = cfg.getParameter("SCHEME/FORM/BGCOLOR", "#FFFFFF");
      //String bgcolor2 = cfg.getParameter("SCHEME/FORM/ALT_BGCOLOR", bgcolor);
      beginDataPresentation();
      
      drawSimpleCommandBar("ABOUTIFSWEBLIENTTEXT: About IFS Web Client");
      appendToHTML("          <table border=\"0\" class=pageFormWithBorder>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td colspan='2' align=center>");
      appendToHTML("                  <img src=\"../images/Corporate_Layer_About_Logo.gif\" align=center valign=center alt=\""+ mgr.translate("FNDPAGESABOUTWINDOWIFSAPPLICATIONALT: IFS Applications")+"\" alt=\""+ mgr.translate("FNDPAGESABOUTWINDOWIFSAPPLICATIONALT: IFS Applications")+"\" >\n");
      appendToHTML("                  </td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTIFSWEBKIT: IFS Web Client Version") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML( ver );
      appendToHTML(" </font></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap valign=top align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTAPPL: Application") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML( "//"+mgr.getConfigParameter("APPLICATION/DOMAIN")+"/"+mgr.getConfigParameter("APPLICATION/PATH") );
      appendToHTML(" </font></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap valign=top align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTLANGCODE: Language code") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML( mgr.getLanguageCode() );
      appendToHTML(" </font></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap valign=top align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTUSER: User") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML( mgr.getUserId() );
      appendToHTML(" </font></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap valign=top align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTWEBBROWSER: Web Browser") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML( mgr.getBrowserName());
      appendToHTML(" </font></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td nowrap valign=top align=\"left\"><font");
      appendToHTML("                  class=normalTextLabel>" + mgr.translate("FNDPAGESABOUTWEBSERVSW: Webserver software") + ": </font></td>\n");
      appendToHTML("                  <td align=\"left\"><font\n");
      appendToHTML("                  class=normalTextValue>");
      appendToHTML(mgr.getServerInfo());
      appendToHTML(" </font>\n");
      appendToHTML("                  <!-- content end --></td>\n");
      appendToHTML("                </tr>\n");
      
      //Bug id 43431, start
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td><font class=normalTextLabel>"+mgr.translate("FNDPAGESABOUTINSTALLEDMODULES: Installed Modules")+": </font></td>\n");
      appendToHTML("                  <td nowrap align=\"left\">\n");
      appendToHTML("                  <a class=hyperLink href=\""+mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS")+"Module.page\" title=\""+mgr.translate("FNDPAGESABOUTINSTALLEDMODULES: Installed Modules")+"\">"+mgr.translate("FNDPAGESABOUTMODULE: Modules")+"&nbsp;>></a>\n");
      //appendToHTML("                  <a class=hyperLink href=\""+mgr.getApplicationPath()+"/LicenseDisclaimer.html\" target=\"_blank\">" + mgr.translate("FNDPAGESABOUTLICENSE: License Notice and Disclaimers") + "</a></td>\n");
      appendToHTML("                </tr>\n");
      //Bug id 43431, end
      
      appendToHTML("                <tr>\n");
      appendToHTML("                  <td class=normalTextLabel colspan=2 width=100% align=\"center\">\n");
      appendToHTML("                  <a class=hyperLink href=\""+mgr.getApplicationPath()+"/LicenseDisclaimer.html\" target=\"_blank\">" + mgr.translate("FNDPAGESABOUTLICENSE: License Notice and Disclaimers") + "</a></td>\n");
      //appendToHTML("                  <a class=hyperLink href=\""+mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS")+"Module.page\" title=\""+mgr.translate("FNDPAGESMODULELIST: Installed Modules")+"\">"+mgr.translate("FNDPAGESMODULELIST: Installed Modules")+"</a>\n");
      appendToHTML("                  <!-- content end --></td>\n");
      appendToHTML("                </tr>\n");
      appendToHTML("               </table>\n");
      endDataPresentation(false);
   }

}
