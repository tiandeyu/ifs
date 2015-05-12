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
 * File        : URLBox.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Chaminda O  2000-Apr-03 - Created.
 *    Chaminda O  2000-Apr-05 - Added mgr.translate().
 *    Chaminda O  2000-Apr-06 - Validation for portal height
 *    Chaminda O  2000-Apr-07 - Changes due to upgrade from wkitb1 to b2
 *                              added Saving profiles
 *    Ramila H    2000-Apr-26 - Added url browse field to printContents().
 *                              Removed URL from title.
 *    Ramila H    2000-Apr-27 - Changed javascript function urlbrowse to reload
 *                              only IFRAME.
 *    Ramila H    2000-Apr-28 - Added functionality added current URL as home page.
 *    Ramila H    2000-May-02 - Added functions doFreez(), doReset() and doActivate().
 *                              Changed HOME_PAGE_URL from text field to hidden field
 *                              in customization page.
 *    Jacek P     2000-May-16 - Re-written to support the latest release.
 *    Jacek P     2000-Aug-23 - URL field moved to configuration page. Removed cookie support.
 *    Jacek P     2001-Mar-28 - Logid #599: Default IFS URL changed to ifsworld.com
 *    Shiran F    2002-Oct-24 - Logid #949. Not update when minimized.
 *    Mangala     2002-Oct-24 - Logid #963 and minor changes.
 *    Sampath     2002-Nov-24 - Disabled configure button from NS4 & minor changes at configure page labels   
 *    Sampath     2002-Dec-26 - add methods getZoomInURL() &  to allow the portlet to be zoomed-in  
 *    Chandana D  2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 * ----------------------------------------------------------------------------
 * New Comments:
 *
 * Revision 1.3  2007/02/19 14:59:06  buhilk
 * Bug 63433, Added csv support
 *
 * Revision 1.2  2006/07/13           sumelk
 * Bug 59420, Changed printContents() & printCustomBody().
 *
 * Revision 1.1  2005/09/15 12:38:02  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/01/28 18:07:27  marese
 * Initial checkin
 *
 * Revision 1.3  2005/01/18 09:51:56  sumelk
 * Removed the hard coded reference to www.ifsworld.com. Changed printContents() & printCustomBody().
 * 
 * ----------------------------------------------------------------------------
 *
 */


package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


/**
 * Default URL http://www.ifsab.se
 */
public class URLBox extends ASPPortletProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.portlets.URLBox");

   // Default Values - constants. Should be placed in ACPConfig.ifm ?
   private static final String DEFAULT_URL    = "";
   private static final String DEFAULT_HEIGHT = "200";


   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient String mintitle;
   private transient String maxtitle;
   private transient String page_url;
   private transient String box_height;
   private transient boolean update_when_minimized;
   private transient boolean can_zoom_in;


   public URLBox(ASPPortal portal, String clspath )
   {
      super(portal,clspath);
   }


   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if(DEBUG) debug(this+": URLBox.clone()");

      URLBox page = (URLBox)(super.clone(obj));

      page.mintitle   = null;
      page.maxtitle   = null;
      page.page_url   = null;
      page.box_height = null;
      page.update_when_minimized = false;
      page.can_zoom_in = false;

      return page;
   }


   protected void doReset() throws FndException
   {
      mintitle   = null;
      maxtitle   = null;
      page_url   = null;
      box_height = null;
      update_when_minimized = false;
      can_zoom_in = false;

      super.doReset();
   }


   public static String getDescription()
   {
      return "FNDURLBOXDESC: Web Page Portlet";
   }


   public String getTitle(int mode)
   {
      if(DEBUG) debug(this+": URLBox.getTitle()");
      
      if( mode==MINIMIZED )
         return mintitle;
      else if( mode==MAXIMIZED )
         return maxtitle;
      else
         return translate("FNDURLBOXBBCUSTTIT: Customize Web Page Portlet");
   }


   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": URLBox.preDefine()");

      appendJavaScript( "function URLBrowser(obj,id)");
      appendJavaScript( "{\n");
      appendJavaScript( "   var pageurl = getPortletField(id,'PAGE_URL').value;\n");
      appendJavaScript( "   window.open(pageurl,getPortletFieldName(id,'URLBOX_FRAME'));");
      appendJavaScript( "}\n");

      init();
   }


   protected void init()
   {
      if(DEBUG) debug(this+": URLBox.init()");

      mintitle = readProfileValue("MINTITLE", translate(getDescription()) );
      maxtitle = readProfileValue("MAXTITLE", translate(getDescription()) );

      page_url   = readProfileValue("PAGE_URL",   DEFAULT_URL);
      page_url   = getASPManager().decodedURLWithCSV(page_url);
      box_height = readProfileValue("BOX_HEIGHT", DEFAULT_HEIGHT);
      update_when_minimized = readProfileFlag("UPDATE_WHEN_MINIMIZED", false);
      can_zoom_in = readProfileFlag("CAN_ZOOM_IN",true);
   }


   public void printContents() throws FndException
   {
      if(getASPManager().isExplorer() || getASPManager().isMozilla())
      {
         if(getASPManager().isEmpty(page_url))
         {
	    printNewLine();
            printLink(translate("FNDPORTLETURLNOURL: Enter your URL on the &&Customization&& page."),"javascript:customizeBox('" + getId() + "')");
	 }
	 else
	 {
             if(!isMaximized() && !update_when_minimized)
             {
                printNewLine();
                printLink(translate("FNDPORTLETURLNOTLOAD1: Press &&update portlet&& to connect to &1.",page_url),"javascript:submitPortlet('" + getId() + "')");
                printNewLine();
                printLink(translate("FNDPORTLETURLNOTLOAD2: You can also &&customize&& the portlet to update even when minimized."),"javascript:customizeBox('" + getId() + "')");                     
             }
             else
             {
                appendToHTML("<IFRAME name=\"",addProviderPrefix(),"URLBOX_FRAME\" src=\"",page_url,"\"");
                appendToHTML(" width=100% height=",box_height,"></IFRAME>");
                
                if (getASPManager().isExplorer())
                      appendDirtyJavaScript("setTimeout('removePageMask()',30000);");
             }
         }    
      }
      else
      {
         printNewLine();
         setFontStyle(BOLD);
         printText("FNDURLBRERR: This portlet only works in Microsoft Internet Explorer and Netscape 6 or later.");
         setFontStyle(NONE);
         printNewLine();
      }
   }
 
   public boolean canCustomize()
   {
      ASPManager mgr = getASPManager();
      if(mgr.isExplorer()||mgr.isMozilla())
         return true;
      else
         return false; 
   }
   
   public String getZoomInURL()
   {
      ASPManager mgr = getASPManager();
      if((mgr.isExplorer()||mgr.isMozilla())&& (can_zoom_in))
         return page_url;
      else
         return null; 
   }



   public void printCustomBody() throws FndException
   {
      if(DEBUG) debug(this+": URLBox.printCustomBody()");

      printNewLine();

      beginTransparentTable();
        beginTableTitleRow();
          printTableCell("FNDURLBOXTITLEMSG: You can choose your own title for this portlet",3,0,LEFT);
        endTableTitleRow();

        beginTableBody();
          beginTableCell();
            printSpaces(5);
            printText("FNDURLBOXMAXTITLE: when maximized:");
          endTableCell();
          beginTableCell(2);
            printField("MAXTITLE",maxtitle,50);
          endTableCell();
        nextTableRow();
          beginTableCell();
            printSpaces(5);
            printText("FNDURLBOXMINTITLE: when minimized:");
          endTableCell();
          beginTableCell(2);
            printField("MINTITLE",mintitle,50);
          endTableCell();
        nextTableRow();
          printTableCell(null,3);
        nextTableRow();
          printTableCell("FNDURLBOXHEIGHT: URL Box Height:");
          beginTableCell(2);
            printField("BOX_HEIGHT",box_height,5);
          endTableCell();
        nextTableRow();
          printTableCell(null,3);
        nextTableRow();
          printTableCell("FNDURLBOXURL: URL:");
          beginTableCell();
            printField("PAGE_URL",readAbsoluteProfileValue("PAGE_URL",   DEFAULT_URL),100);
          endTableCell();
          beginTableCell();//RIGHT);
            printScriptHoverImageLink( getImagesLocation()+"url_goto.gif", getImagesLocation()+"url_goto_hov.gif", "URLBrowser");
          endTableCell();
        nextTableRow();
          printTableCell("");
          printTableCell("FNDURLBOXWARNINGMSG: Warning: Some URL's may hang the browser or result in unexpected behaviors.");
        nextTableRow();
          printTableCell("FNDURLBOXMINCHKBOX: Update when minimized:");
          beginTableCell();
            printCheckBox("UPDATE_WHEN_MINIMIZED", update_when_minimized);
          endTableCell();
        nextTableRow();  
          printTableCell("FNDURLBOXCANZOOM: Zoom In this web page :");
          beginTableCell();
            printCheckBox("CAN_ZOOM_IN", can_zoom_in);
          endTableCell();

        endTableBody();
      endTable();

      printNewLine();

      appendToHTML("<IFRAME name=\"",addProviderPrefix(),"URLBOX_FRAME\" src=\"",page_url,"\"");
      appendToHTML(" width=100% height=",box_height,"></IFRAME>");
      
      if (getASPManager().isExplorer())
         appendDirtyJavaScript("removePageMask()");
   }


   public void submitCustomization()
   {
      if(DEBUG) debug(this+": URLBox.submitCustomization()");

      mintitle = readValue("MINTITLE");
      maxtitle = readValue("MAXTITLE");
      writeProfileValue("MINTITLE", mintitle );
      writeProfileValue("MAXTITLE", maxtitle );

      page_url = getASPManager().decodedURLWithCSV(readValue("PAGE_URL"));
      writeProfileValue("PAGE_URL", readAbsoluteValue("PAGE_URL"));

      box_height = readValue("BOX_HEIGHT");
      try
      {
         Integer.parseInt(box_height);
      }
      catch(NumberFormatException e)
      {
         box_height = DEFAULT_HEIGHT;
      }
      writeProfileValue("BOX_HEIGHT",box_height);

      update_when_minimized = "TRUE".equals(readValue("UPDATE_WHEN_MINIMIZED"));
      if(DEBUG) debug(""+update_when_minimized);
      writeProfileFlag("UPDATE_WHEN_MINIMIZED", update_when_minimized);
      if(DEBUG) debug(" "+can_zoom_in);
      can_zoom_in = "TRUE".equals(readValue("CAN_ZOOM_IN"));
      writeProfileFlag("CAN_ZOOM_IN",can_zoom_in);

   }
}
