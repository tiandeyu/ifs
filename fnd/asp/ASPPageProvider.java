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
 * File        : ASPPageProvider.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P       2000-Sep-04 - Created.
 *    Chaminda O    2000-Dec-06 - Added methods toDouble() and split()
 *    Mangala P     2000-Dec-08 - Changed getContents() to return an empty AutoString
 *                                when its an LOV page
 *    Jacek P       2001-Jan-18 - Changed implementation of eval().
 *    Jacek P       2001-Jan-29 - Increased handling of master connection in eval().
 *    Jacek P       2001-Jan-30 - Added new method createInstance() to fix problem
 *                                while cloning. Improved eval().
 *    Chaminda O    2001-Feb-21 - changed return type of  the split() method to String[]
 *                                and changed internal implementation
 *                              - added method splitToVector()
 *    Artur K       2001-Mar-05 - Necessary changes in newPage() function for handling nested
 *                                directory structure for ASP/Java pages.
 *    Jacek P       2001-Mar-19 - Changes in main(). Writing of HTML handled by ASPManager now.
 *    Jacek P       2001-Mar-28 - Do not create the script tag if there is no dirty script.
 *    Kingsly P     2001-Apr-02 - Added new methods to generate HTML codes and JavaScript codes.
 *                                eg.printNewLine()
 *    Kingsly P     2001-Apr-10 - Added more methods to generate HTML codes.
 *    Piotr Z  2001-Apr-25 - Scaling of input and textarea fields size for Netscape.
 *    Kingsly P     2001-Apr-10 - Changes in includeJavaScriptFile().
 *    Ramila H      2001-Jun-14 - Log id #754. Added public method callPrintDlg().
 *                                Depending on the second parameter opens the dialog
 *                                in a new browser.
 *    Jacek P       2001-Sep-17 - Added algorithm for ".." in URL's in newPage()
 *    Ramila H      2001-Oct-05 - changed window size for print dialog.   
 *    Daniel S      2001-Oct-16 - AltBackground color on every other row not hardcoded anylonger.
 *    Suneth M      2001-Nov-22 - Log Id #808. Changed printField(), printPasswordField() &
 *                                printReadOnlyField().
 *    Chandana D    2002-Jul-30 - Appended a DIV tag in the getContents() method to Create a Tooltip
 *    Daniel S      2002-Aug-28 - Added method frameworkReset to break the doReset chain. Necessary
 *                                due to the new smart Resetting/Cloning
 *    Chandana D    2002-Sep-03 - Modified printLink() method to support hyperlinking only a part of the text using "&&".
 *    Mangala       2002-Oct-09 - Disabled Home Icon from pages opened by showNewBrowser().
 *    Rifki R       2002-Oct-15 - Moved implementation of split() method to util class Str. 
 *    Chanaka A     2002-Dec-16 - Add overload methods beginTableCell() to set the nowrap property in a cell. 
 *    Chandana D    2002-Dec-18 - Log Id 567, Added getActiveCommand() method.
 *    Chandana D    2002-Dec-30 - Corrected a bug in log id 567.
 *    Rifki R       2003-Jan-09 - Log id 1010, made call to checkObjectAccess() in main().
 *    Chandana D    2003-Feb-11 - Modified the getContents() method.
 *    Suneth M      2003-Feb-18 - Modified the printIIDSelectBox() method.
 *    Ramila H      2003-Mar-06 - Bug 34190. Resized open window and added scrollbars.
 *    Chandana D    2003-Mar-17 - Added isAuthorizedToUpload() and getUploadingFileName() methods.
 *    Chandana D    2003-May-26 - Added beginDataPresentation() and endDataPresentation() methods.
 *    Chandana D    2003-May-29 - Overloaded endDataPresentation() method.
 *    Chandana D    2003-Jun-05 - Added drawSimpleCommandBar(String title) method.
 *    Ramila H      2003-Jun-05 - overloaded printCheckbox and printRadioButton methods
 *                                to allow active text clicking.
 *    Chandana D    2003-Sep-04 - Overloaded beginTable() method so as to allow inserting params to the <table> tag.
 *    Chandana D    2004-May-12 - Updated for the use of new style sheets.
 *    Ramila H      2004-may-18 - added method printBoldText 
 *    Chandana D    2004-Jun-10 - Removed all absolute URLs.
 *    Mangala       2004-Jun-17 - Merged with Bug #44656
 *    Ramila H      2004-08-02  - fixed bug in printReadOnlyField
 *    Chandana      2004-Aug-05 - Proxy support corrections.
 *    Ramila H 2004-10-18  - Implemented JSR168 support
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/06/18 sumelk Bug 90122, Changed setDefaultValues() to make template functionality work for editable tables.
 * 2010/05/07 buhilk Bug 90507, Modified main() method to modify IFS EE interface with new attributes.
 * 2010/04/27 buhilk Bug 90137, Modified main() changes to RWC Interface value isPortalPage.
 * 2010/02/03 sumelk Bug 88035, Changed getStdPortalContents(). 
 * 2009/07/08 sumelk Bug 84494, Changed define() to disable Send To menu item without effecting to menu index.
 * 2009/05/15 buhilk Bug 82903, Modified main() to add callWebNavigator() js method to the AEE interface.
 * 2009/03/13 buhilk Bug 81340, Modified main() to correct dockout url casesensitive value.
 * 2009/02/13 buhilk Bug 80265, F1PR454 - Templates IID. Added getTemplate() & modified main(), setDefaultValues().
 * 2008/09/24 dusdlk Bug 77095, Added method prepareTabs() and updated the main method to call prepareTabs().
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Added getClientNoteBook() and saveClientNoteBook().
 * 2008/09/02 buhilk Bug 76759, Modified main(), Added loadEnterpriseExplorerSettings() 
 *                              to load Theme/Regional settings from database when changed from Enterprise Explorer
 * 2008/08/15 buhilk Bug 76288, Moved getMasterBlock() to ASPManager and modified setSendToMenu()
 * 2008/08/12 buhilk Bug 76287, Modified define() to enable send to menu by Global switch to enable/disable Aurora features
 * 2008/08/11 dusdlk Bug 76202, Updated the SetSendToMenu() function to encode complete url assigned to settopage string.
 * 2008/08/05 buhilk Bug 76154, AEE & web integration changes. Added js code & values to AEE interface for portal configuration and dockout url.
 * 2008/07/24 dusdlk Bug 74480, Modified main() updated IF condition to call the appropriate method when hiding the application search
 * 2008/07/10 buhilk Bug 75668, IID F1PR432 - Fixed bug caused by a page having more than one master block.
 * 2008/07/09 buhilk Bug 75668, IID F1PR432 - Workflow/My Todo functionality .
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/05/07 sadhlk Bug 73682, Modified main() to skip checking PO security for mobile pages.
 * 2008/05/02 sadhlk Bug 72853, Modified main() and added isUserLoggedIn() to provide single sign-on
 *                              functionality in Aurora Explorer.
 * 2008/04/09 sumelk Bug 72742, Changed callPrintDlg() to show report print dialog with scrollbars in 
 *                              low screen resolutions.
 * 2008/04/07 sadhlk Bug 72387, Modified main() to enforce security.
 * 2008/04/04 buhilk Bug 72854, Added and overloaded printLink(), printImageLink() & printHoverImageLink() with RWC support.
 *                              Also added printRWCLink(), printRWCImageLink(), printRWCHoverImageLink().
 * 2008/03/27 sadhlk Bug 72361, Modified main().
 * 2007/12/20 buhilk Bug 70052, Modified newPage() to map mobile page url's to ifs.fnd.webmobile.pages
 * 2007/12/12 sadhlk Bug 67525, Modified getStdPortalContents() to support to cookie renaming
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/11/30 sumelk Bug 67400, Added new method printReadOnlyTextArea(). 
 * 2007/10/31 sumelk Bug 67500, Changed printSelectOption() to encode the values. 
 * 2007/05/24 sumelk Merged the corrections for Bug 65593, Fixed a style error in printRadioButton().
 * 2007/04/11 sadhlk Merged Bug 64359, Modified main() to disable Navigator button if the page is opened
 *				   in a new window
 * 2007/04/11 buhilk Bug id 64422, Fixed style errors in printRadioButton() and printCheckBox().
 * 2007/03/06 buhilk Bug id 63950, Modified getActiveCommand() to improve Application search UI.
 * 2007/03/01 buhilk Bug id 63870, Modified printLabel(), printTextLabel(), printCheckBox() and printRadioButton()
 *                                 to improve "whats this" effects.
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * 2007/01/30 buhilk Bug 63250, Improved theming support in IFS clients.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/12/21           buhilk
 * Bug id 61697, Added printItalicText(), printBoldItalicText() methods.
 * 2006/12/08           buhilk 
 * Bug id 62316, Modified getActiveCommand() to replace deprecated captureEvent() js method.
 * 2006/11/13           buhilk
 * Bug id 61819, Modified and merged "Whats this" functionality
 * 2006/11/02           gegulk 
 * Modified the methods printCheckBox() & printRadioButton to include "Whats this" functionality 
 * Replaced <div> tags with <span> tags in the methods  printLabel() & printTextLabel() 
 * to resolve formatting issues
 *
 * 2006/09/29           gegulk 
 * removed the usages of depricated methods getUsageID()
 * 2006/09/29          gegulk
 * Bug id 60886, modified the methods printCheckBox() & printRadioButton() to avoid an exception when
 * the variable "label" is null
 *               2006/09/20          gegulk
 * Bug id 60579, Added "mainWhats this" functionality to the methods printCheckBox() & printRadioButton()
 *
 *               2006/09/21           buhilk
 * Bug id 59842, Modified main() method and moved the CSV init method
 *               2006/09/18           buhilk
 * Bug id 59842, Modified main() method to init CSV cache
 *               2006/09/13          gegulk 
 * Bug id 60473, Modified the method getStdPortalContents()  to append the variable IS_RTL to the javascript
 *               2006/05/17           riralk 
 * Bug 57749, Terms related improvements in web client. Modified printLabel(), printCheckBox(), printRadioButton()
 * to send the page object to ASPManager.translate().  
 *
 *               2006/02/22           prralk
 * B135280 added disabled check for text hyperlink in checkboxes and radio buttons
 *
 *               2006/02/22           prralk
 * B133922 fixed default value display for select boxes
 *               2006/02/13           prralk
 * B131810 "Return" not working when only one field exist in Form.
 *
 * Revision 1.4  2006/01/02   prralk
 * Added a flag, which when set to true will convert a GET request to a POST request
 * Revision 1.3  2005/11/07 08:16:28  mapelk
 * Introduced "persistant" att to Dynamic Objects and remove non persistent objects from the DynamicObjectCache in the first get.
 *
 * Revision 1.2  2005/10/14 09:08:14  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.7  2005/08/08 09:44:04  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.6  2005/06/15 11:15:04  rahelk
 * CSL 2: bug fix: default values
 *
 * Revision 1.5  2005/06/06 07:29:02  rahelk
 * Restructured BlockProfile to handle both queries and default values
 *
 * Revision 1.4  2005/05/06 09:56:42  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 *
 * Revision 1.3  2005/04/01 13:59:56  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.2  2005/02/24 08:53:52  mapelk
 * Improved automatic security checks
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.7  2005/01/20 09:09:09  sumelk
 * Changed getContents(). Moved codes for generate tooltips to ASPPage as a seperate method.
 *
 * Revision 1.6  2005/01/06 04:33:04  rahelk
 * bug correction to support standard portlet mode
 *
 * Revision 1.5  2004/12/15 11:04:04  riralk
 * Support for clustered environments by caching business graphics and generated javascript files in memory
 *
 * Revision 1.4  2004/12/07 06:05:21  mapelk
 * Added translations to drawSimpleCommandBar
 *
 * Revision 1.3  2004/11/29 12:38:40  mapelk
 * Added another overloaded method beginTableCell() where width can be given as a parameter.
 *
 * Revision 1.2  2004/11/22 04:55:59  chdelk
 * Modified/Added APIs to support Activities in Master-Detail pages.
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.application.managenotes.*;
import ifs.application.fndnotebook.*;
import ifs.fnd.base.FndContext;
import ifs.fnd.buffer.*;
import ifs.fnd.record.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.ap.*;
import ifs.fnd.webmobile.web.MobilePageProvider;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import javax.servlet.http.HttpSession;

/**
 */
public abstract class ASPPageProvider extends ASPPage
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPageProvider");
   protected static final double NOT_A_NUMBER = ASPManager.NOT_A_NUMBER;


   private ASPManager mgr;
   private AutoString out          = new AutoString();
   private AutoString dirty_script = new AutoString();
   private AutoString script = new AutoString();
   private String     scriptfile;      
   private transient int linkseq = 0;   
   private String user_logged_in;
   
   
   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor that will be called by ASPManager.
    */
   protected ASPPageProvider( ASPManager mgr, String page_path )
   {
      super(mgr,page_path);
      if ( DEBUG ) debug(this+": ASPPageProvider.<init>: "+mgr+","+page_path);
   }

   /**
    * Construction of the page.
    */
   protected ASPPage construct() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.construct()");
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Freeze the page. Call freeze() for all enclosed elements.
    * Set the page in state DEFINED. Can only by called if the current
    * state of the page is UNDEFINED.
    */
   protected void doFreeze() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.doFreeze()");
      super.doFreeze();
   }

   /**
    * Just a wrapper for frameworkReset.
    */
   protected void doReset() throws FndException
   {
      frameworkReset();
   }

   /**
    * Reset the page. Call reset() for all enclosed elements.
    * Set the page in state DEFINED. Can only be called if the current
    * page is in state DIRTY or DEFINED. Prepare for releasing in the pool.
    */
   protected final void frameworkReset() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.doReset()");
      out.clear();
      dirty_script.clear();
      mgr = null;
      linkseq = 0;
      super.doReset();
   }
   /**
    * Activate the page after feching from pool. Call activate() for
    * all enclosed elements.
    * Can only be called if the current object is in state DEFINED.
    */
   protected void doActivate() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.doActivate()");
      out.clear();
      dirty_script.clear();
      linkseq = 0;
      super.doActivate();
   }

   /**
    * Clone the page and all enclosed elements.
    * The new page is always in state DEFINED.
    * Set default values for all mutable attributes.
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.clone("+obj+")");
      ASPPoolElement page = super.clone(obj);
      
      return page;
   }

   /**
    * Create a new instance of the current class by using the Relection package.
    * Called from ASPPage.
    */
   protected final ASPPage newInstance( ASPManager mgr ) throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.newInstance("+mgr+")");
      //return newPage(mgr, getClass().getName());
      return createInstance(mgr,getClass().getName(),null);
   }

   //==========================================================================
   //  Template functions for overriding in pages
   //==========================================================================

   /**
    * Return the page description that will be shown ...
    * The description text should contain the translatable constant.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected String getDescription()
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getDescription()");
      return "FNDPAGPRVDESC: Description not set";
   }

   /**
    * Return the title of the page ...
    */
   protected String getTitle() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getTitle()");
      return getClass().getName();
   }

   /**
    * ... not for overriding in the first palce
    */
   protected void define() throws FndException //not to override in the first palce
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.define()");

      // Modified by Terry 20120822
      // Bug
      // Original: if ( !isDefined() )
      if ( !isDefined() && isUndefined() )
      {
         setVersion(3);
         preDefine();
         setSendToMenu();
         ASPBlock blk = getASPManager().getMasterBlock();
         if(blk!= null && !getASPManager().isAuroraFeaturesEnabled())
            blk.disableSendTo();   
         setDefined();
         appendJavaScript();
      }
      else
         init();
   }

   /**
    * Define the logical structure of the page.
    * Called only once to create the structure stored in the page pool for later use.
    */
   protected void preDefine() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.preDefine()");
   }

   private void setSendToMenu() throws FndException
   {
      if(this instanceof ifs.fnd.web.features.managemytodo.MyToDoTask) return;
      ASPBlock blk = getASPManager().getMasterBlock();
      if(blk!=null)
      {
         ASPCommandBar cmdbar = blk.getASPCommandBar();
         String baseurl = mgr.getASPConfig().getProtocol()+"://"+mgr.getASPConfig().getApplicationDomain();
         String url = getURL().replaceFirst(mgr.getCSVValue(baseurl),"");
                url = url.replaceFirst(getASPConfig().getConfigFile().application_path+"/","");
                url = mgr.URLEncode("ifsweb:"+url);
         String keys = blk.getLUKeys();
         String imageLoc = getASPConfig().getImagesLocation();
         if(mgr.isEmpty(keys)) return;
         String sendtopage = mgr.getApplicationAbsolutePath()+"/fnd/web/features/managemytodo/MyToDoTask.page?title="+mgr.URLEncode(mgr.translate(getTitle()))+"&keys="+mgr.URLEncode(keys)+"&url="+url+"&busobj="+mgr.URLEncode(blk.getLUName());

         ASPPopup s = newASPPopup("_fndSendTo");
         s.setParameterFields(keys.replace('^',','));
         s.addItem(imageLoc+"user_16x16.png", "WEBFEATURESENDTOCOLLEAGUE: Colleague","javascript:sendToColleague();");
         s.addItem(imageLoc+"SendToMailRecipient16x16.png", "WEBFEATURESENDTOMAILRECIP: Mail Recipient as Link","javascript:sendViaMail();");
         blk.addPopupMenu("WEBFEATURESENDTOMENU: Send To",s);
         
         StringTokenizer pkeys = new StringTokenizer(keys,"^");
         
         appendJavaScript("\nfunction __getSendToParamList(){");
         appendJavaScript("\n   var paramlist = '';");
         while(pkeys.hasMoreTokens()) {
            String p = pkeys.nextToken().trim();
            appendJavaScript("\n   paramlist += ((paramlist!='')?'&':'') + '"+p+"='+URLClientEncode(getCustomPopupParamValue('"+blk.getName().toUpperCase()+"','"+p+"'));");
         }
         appendJavaScript("\n   return paramlist;");
         appendJavaScript("\n}\n");
         
         appendJavaScript("\nfunction sendToColleague(){");
         appendJavaScript("\n   showNewBrowser_('"+sendtopage+"&dhtmlpopup=true&ACTION=SAVETASK_1"+"&'+__getSendToParamList(),0,0,'NO');");
         appendJavaScript("\n}\n");
         
         appendJavaScript("\nfunction sendViaMail(){");
         appendJavaScript("\n   showNewBrowser_('"+sendtopage+"&javascript=true&ACTION=MAILTO"+"&'+__getSendToParamList(),0,0,'NO');");
         appendJavaScript("\n}");
      }
   }
   
   /**
    * Called on page definition instead of preDefine()
    * if the page is fetched from the pool.
    * Placeholder for initialization of variables.
    */
   protected void init() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.init()");
   }

   /**
    * Placeholder for the business logic.
    */
   protected void run() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPortletProvider.run()");
   }
   
   /**
    * Create the HTML contents of the page by calling printContents().
    * ... not for overriding in the first palce
    */
   protected AutoString getContents() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getContents()");

      out.clear();

      if ( getASPLov() != null )
         return out;

      out.append("<html>\n");
      out.append("<head>\n");
      // Modified by Terry 20120925
      // Original: out.append(mgr.generateHeadTag(getDescription()));
      // Add Biz workflow source description.
      out.append(mgr.generateHeadTag(getDescription() + getBizWfSourceDesc()));
      // Modified end
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      // Modified by Terry 20120925
      // Original: out.append(mgr.startPresentation(getTitle()));
      // Add Biz workflow source description.
      out.append(mgr.startPresentation(getTitle() + getBizWfSourceDesc()));
      // Modified end

      printContents();

      out.append(mgr.endPresentation());

      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      return out;
   }

   protected AutoString getContentsToConvertGet()throws FndException{
      if ( DEBUG ) debug(this+": ASPPageProvider.getContentsToConvertGet()");

      out.clear();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");
      out.append("<form ");
      out.append(generateFormTagToConvertGet());
      out.append(" >\n");
      printHiddenField("CONVERTGTOP","Y");
      out.append(mgr.generateClientScript());
      out.append("</form>\n");
      out.append("<script language=javascript>");
      out.append("submit();");
      out.append("</script>");
     
      out.append("</body>\n");
      out.append("</html>\n");

      return out;
   }
   
   protected String generateFormTagToConvertGet()
   {
      if(DEBUG) debug(this+": ASPPage.generateFormTagToConvertGet()");

      String tag1       = "name=\"form\" method=\"POST\" action=\"";
      String tag2       = "\" OnSubmit=\"javascript:return onSubmit()\"";
      
      String form_tag_action_url = getCurrentPagePath()
                                   + ((getASPManager().getQueryString()==null)?"":"?"+(getASPManager().getQueryString()));
            //getASPManager().readValue("__DYNAMIC_LOV_VIEW") : ""                       
      return tag1 + getASPManager().correctURL(form_tag_action_url) + tag2;
   }

   
   /**
    * Create the HTML contents of the page by calling printContents().
    * ... not for overriding in the first palce
    */
   protected AutoString getStdPortalContents() throws FndException
   {
      String id = getASPPortal().getPortletId();
      if(DEBUG) debug(this+": ASPPageProvider.getStdPortalContents(): id="+id);

      ASPConfig cfg    = getASPConfig();
      ASPPortal portal = getASPPortal();

      out.clear();
      out.append("\n<!-- ASPPageProvider.getStdPortalContents() - begining -->\n");

      out.append("<!-- ASPPageProvider.getStdPortalContents() -->\n");
      out.append("<script language=\"JavaScript\">\n");
      out.append("STD_PORTLET="+getASPManager().isStdPortlet()+";\n");
      out.append(ASPConfigFile.APP_PATH + " = \""+ getASPConfig().getApplicationPath() +"\";\n");
      out.append(ASPConfigFile.APP_ROOT + "=\""+ getASPConfig().getApplicationPath() +"/\";\n");
      out.append("THEME=\"",getASPManager().getUserTheme()+"/\";\n");
      out.append(ASPConfigFile.COOKIE_PATH + " = \""+ getASPConfig().getCookiePath() +"\";\n");
      out.append(ASPConfigFile.COOKIE_ROOT_PATH + " = \""+ getASPConfig().getCookieRootPath() + "\";\n");
      out.append(ASPConfigFile.COOKIE_PREFIX + " = \""+ getASPConfig().getCookiePathPrefix() + "\";\n");
      out.append(ASPConfigFile.COOKIE_DOMAIN + " = \""+ getASPConfig().getCookiePathDomain() + "\";\n");
      out.append("IS_RTL="+getASPManager().isRTL()+";\n");
      out.append("  if (IS_RTL)\n");
      out.append("     document.dir = 'rtl';\n"); 
      out.append("</script>\n");

      out.append(getASPManager().getScriptFileTag());
      //out.append( cfg.getGenericClientScript() );      
      //out.append( cfg.getConstantClientScript() );
      out.append("\n<!-- ASPConfig.getConstantClientScript() - end -->\n");

      out.append( cfg.getCalendarScript());
      out.append("\n<!-- ASPConfig.getCalendarScript() - end -->\n");

      out.append("\n<form method=\"POST\" name=\"",id.toUpperCase(),"_FORM\" action=\"",portal.getActionURL(),"\">\n");

      out.append("\n<!-- ASPPortal.generateHTML() - begining -->\n");
      out.append(getASPPortal().generateHTML());
      out.append("\n<!-- ASPPortal.generateHTML() - end -->\n");

      out.append("\n<!-- ASPManager.endPresentationSimple() - begining -->\n");
      out.append(mgr.endPresentationSimple());
      out.append("\n<!-- ASPManager.endPresentationSimple() - end -->\n");

//      out.append("\n</form>\n");
      out.append("\n<!-- ASPPageProvider.getStdPortalContents() - end -->\n");

      return out;
   }

   
   private void getActiveCommand(AutoString out)
   {
       if(getBlockCount()==1 && !mgr.isEmpty(getDefaultCommand()))
        {
            out.append("<input type=\"text\" disabled=\"true\" id =\"d1\" name=\"__dummy\" value=\"\" style=\"visibility:hidden;\">\n");
            out.append("<script language=\"JavaScript\">\n");
            out.append("var c=0;\n");
            out.append("var size = document.forms[0].length;\n");
            out.append("for (x=0; x<size; x++){\n");
            out.append("if((document.forms[0].elements[x].type=='text') && (!document.forms[0].elements[x].disabled))\n");
            out.append("c++;");
            out.append("}\n");
            out.append("if (c==1){\n");
            out.append("var t = document.getElementById(\"d1\");\n");
            //out.append("alert(\"test:\"+t.type)\n;");
            out.append("t.disabled=\"false\";\n");
            out.append("}\n");
            out.append("default_command = true;\n");
            out.append("ACTIVE_COMMAND_HOVR_IMG = '"+getActiveCommandHoverImage()+"';\n");
            out.append("ACTIVE_COMMAND_NORM_IMG = '"+getActiveCommandNormalImage()+"';\n");
            if(!mgr.isExplorer())
            {
               out.append("if (window.addEventListener)\n{\n");
               out.append("    window.addEventListener('keypress', keyPressed, true);\n");
               out.append("} \nelse if (window.attachEvent)\n{\n");
               out.append("    window.attachEvent('onkeypress', keyPressed);\n}\n");
               //out.append("window.captureEvents(Event.KEYPRESS);\n");
               //out.append("window.onkeypress = keyPressed;\n");
            }
            out.append("function executeCommand(e){\n");
            out.append("ok=false;\n");
            out.append("if(IS_EXPLORER){\n");
            out.append("  if(window.event.keyCode==13)\n    ok=true;\n");
            out.append("}else{\n");
            out.append("  if(e.which==13)\n    ok=true;\n}\n");
            out.append("if(ok && !focusInSearchBox && !__dhtmlPopupOpen)\n");
            out.append("   commandSet('"+getDefaultBlock()+"."+getDefaultCommand()+"','');\n");
            out.append("if(ok && focusInSearchBox)\n");
            out.append("   search();\n");
            out.append("}\n");
            out.append("</script>\n");
        }
   }

   /**
    * Print the HTML contents of the portlet.
    */
   protected void printContents() throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.printContents()");
   }


   protected AutoString getOutputStream()
   {
      //out.clear();
      return out;
   }

   //==========================================================================
   //  Generation of HTML tags. To be called from printContents()
   //==========================================================================

   /**
    * Append a string to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str )
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.appendToHTML("+str+")");
      out.append(str);
   }


   protected final void appendBlockLayout( ASPBlockLayout lay ) throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.appendBlockLayout("+lay+")");
      out.append(lay.show());
   }

   //==========================================================================
   //  Generation of JavaScript code
   //==========================================================================

   /**
    * Creates a 'script' tag and appends the specified string as JavaScript code,
    * i.e. script variables. Due this function is called for each instance of the
    * same portlet class, the code should contain portlet ID.
    * Should be called from printContents() or printCustomBody().
    */
   protected final void appendDirtyJavaScript( String str ) throws FndException
   {
      appendDirtyJavaScript(str,null,null,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code.
    */
   protected final void appendDirtyJavaScript( String s1, String s2 ) throws FndException
   {
      appendDirtyJavaScript(s1,s2,null,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code.
    */
   protected final void appendDirtyJavaScript( String s1, String s2, String s3 ) throws FndException
   {
      appendDirtyJavaScript(s1,s2,s3,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code.
    */
   protected final void appendDirtyJavaScript( String s1, String s2, String s3, String s4 ) throws FndException
   {
      appendDirtyJavaScript(s1,s2,s3,s4,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code.
    */
   protected final void appendDirtyJavaScript( String s1, String s2, String s3, String s4, String s5 ) throws FndException
   {
      modifyingMutableAttribute("DIRTY_JAVASCRIPT");
      dirty_script.append(s1);
      if ( s2!=null )  dirty_script.append(s2);
      if ( s3!=null )  dirty_script.append(s3);
      if ( s4!=null )  dirty_script.append(s4);
      if ( s5!=null )  dirty_script.append(s5);
   }

   protected final void appendDirtyJavaScript( boolean value ) throws FndException
   {
      dirty_script.appendBoolean(value);
   }

   protected final void appendDirtyJavaScript( int value ) throws FndException
   {
      dirty_script.appendInt(value);
   }

   //==========================================================================
   //
   //==========================================================================

   protected void main( ASPManager mgr ) throws FndException
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.main("+mgr+")");

      this.mgr = mgr;
      HttpSession session = mgr.getAspSession();
      String is_user_logged = isUserLoggedIn(session);
      
      if(is_user_logged == null && !mgr.isLogonPage())
               session.setAttribute("USER_LOGGED_IN", "Y");

      define();
      if(!mgr.isLogonPage() && !mgr.isPortalPage() && !(this instanceof MobilePageProvider)){
         if(!isObjectAccessible(getComponent().toUpperCase()+"/"+getPageName()+".page"))
            error(new FndException("FNDPRVNOSEC: You do not have sufficient privileges to use this page"));
      }
      if(!mgr.isEmpty(mgr.readValue("ADDTOPOOL")))
      {
         mgr.responseWrite("TRUE^"+this.getPoolKey());
         mgr.endResponse();
      }
      if (isDefined())
      {
         applyProfileFormatter();
         applyLongYearFormatter();
      }
      checkObjectAccess();
      if ("Y".equals(mgr.readValue("__NEWWIN")) || getASPContext().readFlag("__NEWWIN",false))
      {        
         if (getASPPortal() == null)
         {
            mgr.getASPPage().disableApplicationSearch();
            disableHomeIcon();
            disableSettingsLink();
            disableSignoutLink();
            disableSubHeader();
            disableNavigate();
            
            getASPContext().writeFlag("__NEWWIN",true);
         }
         else
         {
            mgr.getPortalPage().disableApplicationSearch();
         }
      }
      
      if(!mgr.isEmpty(mgr.readValue("__EE_VALIDATE")))
         loadEnterpriseExplorerSettings();
      
      if ((mgr.isExplorer())&&((isConvertGettoPost()))&&
      ("GET".equalsIgnoreCase(mgr.getRequestMethod()))&&
      (Str.isEmpty(mgr.getQueryStringValue("VALIDATE"))))
      {
          getContentsToConvertGet();         
      }else
      {
          if(!mgr.isLogonPage() && !mgr.isPortalPage()) mgr.initCSVCache();
          
          prepareTabs(); 
          run();
          saveLayout();
          
          /* ------------------------- Append AEE Interface section ------------------------- */
          
          boolean isPortalPage = false;
          boolean navigator = true;
          boolean dockout_url = true;
          if(mgr.isPortalPage()){
             ASPPage portal = mgr.getPortalPage();
             isPortalPage = !portal.isConfigurationDisabled() && !mgr.getPortalPage().getASPProfile().isUserProfileDisabled() && getASPConfig().isPortalConfigEnabled();
             navigator = getASPConfig().isNavigateEnabled() && !portal.isNavigateDisabled();
             dockout_url = getASPConfig().isDockoutEnabled() && portal.isDockoutEnabled();
          }else{
             navigator = getASPConfig().isNavigateEnabled() && !mgr.getASPPage().isNavigateDisabled();
             dockout_url = getASPConfig().isDockoutEnabled() && mgr.getASPPage().isDockoutEnabled();
          }
          if(isPortalPage)
          {
             // Manage portal menu functions
             String home = mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")+"?__VIEW_NAME=";

             mgr.addJSToRWCInterface("function removePortalView() {if (confirm('"+mgr.translateJavaScript("FNDCONDELMSG: Delete this view?")+"')) document.location='" +mgr.correctURL(home+"_REMOVE&_CURRENT_VIEW="+mgr.URLEncode(mgr.getPortalPage().readGlobalProfileValue(mgr.getPortalPage().getASPPortal().CURRENT_VIEW,false))+"&CUSTOMIZE=N")+"';}\n");
             mgr.addJSToRWCInterface("function createPortalView() {showNewBrowser('" + mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS") + "NewPortalViewWizard.page" + "');}\n");
             mgr.addJSToRWCInterface("function savePortalView() {");
             if (mgr.isNetscape4x())
                mgr.addJSToRWCInterface("showNewStyledBrowser('" + mgr.correctURL(home+"_EXPORT") + "','status,resizable,menubar,scrollbars,width=790,height=500'+NEWWIN_POS);");
             else
                mgr.addJSToRWCInterface("document.location='" +mgr.correctURL(home+"_EXPORT")+"';");
             mgr.addJSToRWCInterface("}\n");
             mgr.addJSToRWCInterface("function rearrangePortalViews() {showNewStyledBrowser('ConfigurePortal.page','status,resizable,width=500,height=400');}");

             mgr.addJSToRWCInterface("function helpOnWebPage(){ showHelp('"+mgr.getURL()+"'); }\n");
          }
          else if(!mgr.isLogonPage())
             mgr.addJSToRWCInterface("function helpOnWebPage(){ showFieldHelp('"+mgr.getURL()+"','"+mgr.getUsageKey()+"'); }\n");
          
			 if(!mgr.isLogonPage())
             mgr.addJSToRWCInterface("function callWebNavigator(){ self.location = "+getASPConfig().getNavigatorLocation(true)+";}\n");

          mgr.startRWCInterfaceItem("command_bar");
          mgr.addRWCInterfaceValue("portal_page",isPortalPage?"Y":"N");
          mgr.addRWCInterfaceValue("navigator",navigator?"Y":"N");
          String doc_url = mgr.getASPConfig().getProtocol()+"://"+mgr.getASPConfig().getApplicationDomain()+getURL();

          String params = getASPContext().readValue("__SEARCHPARAM","");
          doc_url += mgr.isEmpty(params)?"":params;
          doc_url += (doc_url.indexOf("?")!=-1?"":"?")+"&CASESENCETIVE=";
          if(!mgr.isEmpty(readValue("CASESENCETIVE")))
             doc_url += readValue("CASESENCETIVE");
          else
             doc_url += "Y".equals(readValue("__CASESS_VALUE"))?"TRUE":"FALSE";

          if(dockout_url)
             mgr.addRWCInterfaceValue("dockout_url",doc_url);
          mgr.endRWCInterfaceItem("command_bar");
          
          /* ------------------------------------------------------------------------------ */

          AutoString html = getContents();

          if ( dirty_script.length()>0 )
          {
             html.append("<script language=javascript>\n");
             html.append(dirty_script);
             html.append("</script>\n");
          }
      }
      mgr.setEndResponse();
      /*
      try
      {
         mgr.addCookies();
         mgr.getAspResponse().getWriter().write( html.toString() );
      }
      catch( IOException x )
      {
         throw new FndException(x);
      }
      */
   }
   
   /*
    * Checks for user accessibility for the tabs and populates new tabs array 
    * according to new position of the re-arranged tabs.
    */
   private void prepareTabs()
   {
         //Check if tabs in tab container is accessible
         Vector tabvcontainers = getAllTabContainer();
         Iterator itr = tabvcontainers.iterator();
         
         while(itr.hasNext())
         {
            ASPTabContainer tabcontainer = (ASPTabContainer)itr.next();
            tabcontainer.checkTabSecurity();
            tabcontainer.createTabPos();
         }                  
   }
   
   private static final String IFSTMT = "if(__master_has_rows){";

   protected void eval( String command )
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.eval("+(command==null?"<NULL>":command)+")");

      if ( !Str.isEmpty(command) )
         try
         {
            boolean runstmt = true;
            StringTokenizer st = new StringTokenizer(command, ";");
            while ( st.hasMoreTokens() )
            {
               String cmd = st.nextToken();
               if ( Str.isEmpty(cmd) || cmd.startsWith("while(false){}") || cmd.equalsIgnoreCase("null") )
               {
                  continue;
               }
               else if ( "__master_has_rows=true".equals(cmd) )
               {
                  masterHasRows(true);
               }
               else if ( "}".equals(cmd) )
               {
                  runstmt=true;
               }
               else
               {
                  int start = cmd.startsWith(IFSTMT) ? IFSTMT.length() : 0;
                  if ( start>0 )
                  {
                     runstmt = doesMasterHaveRows();
                  }

                  if ( runstmt )
                  {
                     int end = cmd.indexOf("(",start);
                     if ( end > 0 )
                        cmd = cmd.substring(start,end);
                     else
                        cmd = cmd.substring(start);

                     if ( "}".equals(cmd) )
                     {
                        runstmt=true;
                        continue;
                     }

                     Method method = getClass().getMethod(cmd,new Class[0]);
                     Object[] arg = new Object[0];
                     try
                     {
                        method.invoke(this,arg);
                     }
                     catch ( InvocationTargetException x )
                     {
                        throw x.getTargetException().fillInStackTrace();
                     }
                  }
               }
            }
         }
         catch ( Throwable e )
         {
            error(e);
         }
   }


   protected final int toInt( int value )
   {
      return value;
   }

   protected final int toInt( String value )
   {
      try
      {
         return Integer.parseInt(value);
      }
      catch ( Throwable e )
      {
         error(e);
      }
      return -1;
   }

   protected final int toInt( double value )
   {
      return(int)value;
   }


   protected final boolean isNaN( double value )
   {
      if ( DEBUG ) Util.debug("ASPPageProvider.isNaN("+value+"): "+NOT_A_NUMBER+","+Double.NaN);
      boolean res = Double.isNaN(value); //value==NOT_A_NUMBER || value==Double.NaN;
      if ( DEBUG ) Util.debug("ASPPageProvider.isNaN() = "+res);
      return res;
   }


   static ASPPageProvider newPage( ASPManager mgr, String page_path ) throws FndException
   {
      if ( DEBUG ) Util.debug("ASPPageProvider.newPage("+mgr+","+page_path+")");
      
      if( "DefaultStdPortlet.page".equals(page_path) )
      {
         String clsname = "ifs.fnd.pages.DefaultStdPortlet";
         if(DEBUG) Util.debug("  new Page: clsname="+clsname);
         return createInstance(mgr,clsname,page_path);
      }
      
      String path = page_path.substring(mgr.getAspRequest().getContextPath().length());

      //remove "secured" part from page_path
      int sec_index = path.indexOf("secured");
      if ((sec_index>-1))
         path = path.substring(sec_index+"secured".length());
         

      int pos1 = path.indexOf("..");
      int pos2 = -1;
      
      while(pos1>0)
      {
         int p = pos1 + 2;
         if(path.charAt(pos1-1)=='/')
         {
            pos2 = path.lastIndexOf("/",pos1-2);
            if(pos2>=0)
            {
               path = path.substring(0,pos2) + path.substring(pos1+2);
               p = pos2;
            }
         }
         pos1 = path.indexOf("..",p);
         pos2 = -1;
      }
      
      pos1 = path.lastIndexOf("/");
      pos2 = path.lastIndexOf(".");

      String clsname;
      String pre;

      if ( path.startsWith("/common/scripts") )
      {
         path = path.substring(pos1);
         pos2 = path.lastIndexOf(".");
         pre = "ifs.fnd.pages";
      }
      else if ( path.startsWith("/common/mobilescripts") || path.startsWith("/webmobile"))
      {
         path = path.substring(pos1);
         pos2 = path.lastIndexOf(".");
         pre = "ifs.fnd.webmobile.pages";
      }
      else
         pre = pos1!=0 ? "ifs" : "ifs.fnd.pages";

      if ( pos2<0 )
         clsname = pre+path.replace('/','.');
      else
         clsname = pre+path.substring(0,pos2).replace('/','.');

      if ( DEBUG ) Util.debug("  new Page: clsname="+clsname);
      return createInstance(mgr,clsname,page_path);
   }

   private static ASPPageProvider createInstance( ASPManager mgr, String clsname, String page_path ) throws FndException
   {
      if ( DEBUG ) Util.debug("ASPPageProvider.createInstance("+mgr+","+clsname+")");

      try
      {
         Class[]  argcls = new Class[2];
         Object[] arg    = new Object[2];

         argcls[0] = Class.forName("ifs.fnd.asp.ASPManager");
         argcls[1] = Class.forName("java.lang.String");
         arg[0]    = mgr;
         arg[1]    = page_path;

         Class cls = Class.forName(clsname);
         Constructor ctr = cls.getConstructor(argcls);
         return(ASPPageProvider)(ctr.newInstance(arg));
      }
      catch ( ClassNotFoundException x )
      {
         if ( DEBUG ) Util.debug(Str.getStackTrace(x));
         throw new FndException(x).addCaughtException(x);
      }
      catch ( InvocationTargetException x )
      {
         Throwable t = x.getTargetException();
         if ( DEBUG ) Util.debug("  InvocationTargetException:\n"+Str.getStackTrace(t));
         throw new FndException(t);
      }
      catch ( Throwable x )
      {
         if ( DEBUG ) Util.debug(Str.getStackTrace(x));
         throw new FndException(x);
      }
   }

   protected final double toDouble( double value )
   {
      return value;
   }

   protected final double toDouble( String value )
   {
      try
      {
         return Double.valueOf(value).doubleValue();
      }
      catch ( Throwable e )
      {
         error(e);
      }
      return -1;
   }

   protected final double toDouble( int value )
   {
      return(double) value;
   }


   /**
    * Returns the Vector of strings that results when the string str is separated
    * into substrings using the specified set of delimiter characters 'delimChars'.
    */

   /**
    * Use split() instead.
    *
    * @see ifs.fnd.asp.ASPPageProvider#split
    * @deprecated
    */
   protected final Vector splitToVector(String str, String delimChars)
   {
      if ( str==null||str.length()==0 )
         return null;

      StringTokenizer st = new StringTokenizer(str, delimChars,true);
      Vector v = new Vector();
      String tmp = null;

      while ( st.hasMoreTokens() )
      {
         tmp = st.nextToken();
         if ( delimChars.indexOf(tmp)==-1 )
         {
            v.addElement(tmp);
            if ( st.hasMoreTokens() )
               tmp = st.nextToken();
         }
         else
            v.addElement("");
      }

      if ( delimChars.indexOf(tmp)!= -1 )
         v.addElement("");

      return v;
   }

   /**
    * Returns an array of strings that results when the string str is separated
    * into substrings using the specified set of delimiter characters 'delimChars'.
    */
   protected static final String[] split(String str, String delimChars)
   {
      return Str.split(str,delimChars);
   }

   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPageProvider#setFontStyle
    */   
   protected final static int NONE   = 0;   

   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPageProvider#setFontStyle
    */
   protected final static int BOLD   = 1;      

   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPageProvider#setFontStyle
    */
   protected final static int ITALIC = 2;   

   /**
    * ***********************************************************************
    * Method should be made obsolete in major release
    * ************************************************************************
    * Set the font style according to the input parameter. Allowed values are
    * one of predefined constants NONE, BOLD or ITALIC.
    * The setting is canceled by endFont() function.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endFont
    */
   protected final void setFontStyle( int style )
   {
      if(style==BOLD)
         out.append("<p class=\"pageFontBold\">");
      else if(style==ITALIC) 
         out.append("<p class=\"pageFontItalic\">");
   }

   /**
    * ***********************************************************************
    * Method should be made obsolete in major release
    * ************************************************************************
    * Cancel font setting defined by setFontStyle() function.
    *
    * @see ifs.fnd.asp.ASPPageProvider#setFontStyle
    */   
   protected final void endFont()
   {
      out.append("</p>");
   }

   /**
    * Append a Lable after translation to the HTML-output.
    * Apply predefined styles.
    */
   protected final void printReadLabel( String label )
   {
      printLabel(label,true);
   }

   /**
    * Append a Lable after translation to the HTML-output.
    * Apply predefined styles.
    */
   protected final void printWriteLabel( String label )
   {
      printLabel(label,false);
   }

   private void printLabel( String label, boolean readOnlyMode )
   {
      String usage_id ="";
      int index = label.indexOf(":");
      if (index>0)
      {
         String tr_constant = label.substring(0,index);
         usage_id = getASPManager().getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      out.append("<span OnClick=\"showHelpTag('",usage_id,"')\">");
      if(getASPManager().isLogonPage())
         out.append("<font class=normalTextLabel>");
      else
         out.append("<font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','",usage_id,"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel>");
      out.append(getASPManager().translate(label,this));
      out.append("</font>");
      out.append("</span>");
   }  

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined styles.
    */   
   protected void printText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=normalTextValue>");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined bold styles.
    */   
   protected void printBoldText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=boldTextValue>");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined italic styles.
    */   
   protected void printItalicText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=italicTextValue>");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }

   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined bold and italic styles.
    */   
   protected void printBoldItalicText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=\"italicTextValue boldTextValue\">");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }
   
   /**
    * Append a label after translation to the HTML-output.
    * Apply predefined styles.
    */   
   protected void printTextLabel( String str )
   {
      String usage_id ="";
      int index = 0;
      if(!Str.isEmpty(str))
         index = str.indexOf(":");
      if (index>0)
      {
         String tr_constant = str.substring(0,index);
         usage_id = getASPManager().getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      
      ASPManager mgr = getASPManager();
      out.append("<span OnClick=\"showHelpTag('",usage_id,"')\">");
      out.append("<font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','",usage_id,"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel>");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str),true));
      out.append("</font>");
      out.append("</span>");
   }   

   /**
    * Append amount of spaces ('&nbsp;' sequences) to the HTML-output.
    */   
   protected void printSpaces( int spaces )
   {
      for ( int i=0; i<spaces; i++ )
         out.append("&nbsp;");
   }

   /**
    * Append a &lt;br&gt; to the HTML-output.
    */   
   protected void printNewLine()
   {
      out.append("<br>\n");
   }

   /**
    * Append a named hidden input tag to the HTML-output.
    */   
   protected void printHiddenField( String name, String value )
   {
      out.append("<input type=hidden name=",name);
      out.append(" value=\"",getASPManager().HTMLEncode(value),"\">");
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected void printField( String name, String value, String tag )
   {
      printField(name, value, tag, 0, 0, false);
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected void printField( String name, String value, String tag, int size)
   {
      printField(name, value, tag, size, 0, false);      
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected void printField( String name, String value, String tag, int size, int maxlength)
   {
      printField(name, value, tag, size, maxlength, false);      
   }   

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected void printField( String name, String value, String tag, int size, int maxlength, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      out.append("<input class='editableTextField' type=text");
      out.append(" name=\"",name);
      out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");

      if ( size>0 )
      {
         out.append(" size=\"");
         out.appendInt(mgr.isExplorer() ? size : (int)Math.round(size*.6));
         out.append("\" ");
      }
      if ( maxlength>0 )
      {
         out.append(" maxlength=\"");
         out.appendInt(maxlength);
         out.append("\" ");
      }
      if ( tag != null )
         out.append(tag);

      out.append(">");

      if ( mandatory )
         out.append("*");      
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */   
   protected void printReadOnlyTextField( String name, String value, String tag )
   {
      printReadOnlyField( name, value, tag, 0, 0, false);
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */  
   protected void printReadOnlyTextField( String name, String value, String tag, int size )
   {
      printReadOnlyField( name, value, tag, size, 0, false);      
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected void printReadOnlyTextField( String name, String value, String tag, int size, int maxlength )
   {
      printReadOnlyField( name, value, tag, size, maxlength, false);
   }

   /**
    * Append a named ReadOnly input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected void printReadOnlyField( String name, String value, String tag, int size, int maxlength, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      out.append("<input class='readOnlyTextField' type=text readonly tabindex=-1 OnChange='this.value=this.defaultValue' ");
      out.append(" name=\"",name);
      out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");

      if ( size>0 )
      {
         out.append(" size=\"");
         out.appendInt(mgr.isExplorer() ? size : (int)Math.round(size*.6));
         out.append("\" ");
      }
      if ( maxlength>0 )
      {
         out.append(" maxlength=\"");
         out.appendInt(maxlength);
         out.append("\" ");
      }
      if ( tag != null )
         out.append(tag);

      out.append(">");

      if ( mandatory )
         out.append("*");
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */   
   protected void printPasswordField( String name, String value, String tag )
   {
      printPasswordField( name, value, tag, 0, false);            
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */   
   protected void printPasswordField( String name, String value, String tag, boolean mandatory )
   {
      printPasswordField( name, value, tag, 0, mandatory);            
   }

   /**
    * Append a named input tag of type 'password' to the HTML-output. Apply predefined styles.
    */
   protected void printPasswordField( String name, String value, String tag, int size, boolean mandatory)
   {
      ASPManager mgr = getASPManager();
      out.append("<input class='passwordTextField' type=password");
      out.append(" name=\"",name);
      out.append("\" value=\"",mgr.HTMLEncode(value),"\" ");

      if ( size>0 )
      {
         out.append(" size=\"");
         out.appendInt(mgr.isExplorer() ? size : (int)Math.round(size*.6));
         out.append("\" ");
      }
      if ( tag != null )
         out.append(tag);

      out.append(">");

      if ( mandatory )
         out.append("*");
   }   

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    */   
   protected void printTextArea( String name, String value, String tag, int rows, int cols )
   {
      printTextArea( name, value, tag, rows, cols, false);
   }

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    */   
   protected void printTextArea( String name, String value, String tag, int rows, int cols, boolean mandatory )
   {
      ASPManager mgr = getASPManager();
      out.append("<textarea class='editableTextArea'");
      out.append(" name=\"",name);
      out.append("\" rows=");
      out.appendInt(rows);
      out.append(" cols=");
      out.appendInt(mgr.isExplorer() ? cols : (int)Math.round(cols*.6));      

      if ( tag != null )
         out.append(" ",tag);
      out.append(">",mgr.HTMLEncode(value),"</textarea>");

      if ( mandatory )
         out.append("*");      
   }

   /**
    * Append a named ReadOnly input tag of type 'textarea' to the HTML-output. Apply predefined styles.
    */
   protected void printReadOnlyTextArea( String name, String value, String tag, int rows, int cols, boolean mandatory )
   {
      ASPManager mgr = getASPManager();
      out.append("<textarea class='readOnlyTextArea'");
      out.append(" name=\"",name);
      out.append("\" rows=");
      out.appendInt(rows);
      out.append(" cols=");
      out.appendInt(mgr.isExplorer() ? cols : (int)Math.round(cols*.6));      

      if ( tag != null )
         out.append(" ",tag);
      out.append(">",mgr.HTMLEncode(value),"</textarea>");

      if ( mandatory )
         out.append("*");      
   }   

   /**
    * Append a named check box input tag to the HTML-output and apply predefined styles.
    */   
   protected void printCheckBox( String name, String value, boolean checked, String tag)
   {
      printCheckBox( null, name, value, checked, tag);
   }
   
   protected void printCheckBox( String label, String name, String value, boolean checked, String tag)
   {
      ASPManager mgr = getASPManager();
            
      out.append("<input class='checkbox' type=checkbox");
      out.append(" name=\"",name);
      out.append("\" value=\"" + value + "\" ");

      if ( tag != null )
         out.append(tag);
      if ( checked )
         out.append(" CHECKED");
      out.append(">");
      if (!Str.isEmpty(label))
      {
         String usage_id ="";
         int i = label.indexOf(":");
         if (i>0)
         {
            String tr_constant = label.substring(0,i);            
            usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
         }
         out.append("<span onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+ usage_id+ "'); else this.style.cursor='pointer';\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel'); this.style.cursor='';\" class=normalTextLabel onclick=\"if(helpMode) showHelpTag('"+ usage_id+ "'); else _selectItem();");
         //out.append("&nbsp;<a class=normalTextLabel href=\"javascript:_selectItem();");
         out.append("function _selectItem(){if (f."+name+".disabled) return; f."+name+".checked=(!f."+name+".checked==true);");
         if ( tag != null && (tag.indexOf("=") > -1))
         {
            String event = tag.substring(0,tag.indexOf("="));
            event = event.trim();
            out.append("f."+name+"."+event.toLowerCase()+"();");
         }
         out.append("}\">",getASPManager().translate(label,this),"</span>");
      }
   }

   /**
    * Append a named radio button input tag to the HTML-output and apply predefined styles.
    * The specified label will be translated.
    */
   protected void printRadioButton( String label, String name, String value, boolean checked, String tag )
   {
      printRadioButton( label, name, value, checked, tag, -1 );
   }
   protected void printRadioButton( String label, String name, String value, boolean checked, String tag, int index )
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      if (!Str.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      
      out.append("<input class=radioButton type=radio");
      out.append(" name=\"",name);
      out.append("\" value=\"" + value + "\"");
      if ( checked )
         out.append(" CHECKED ");
      if ( tag != null )
         out.append(tag);
      
      if (index > -1)
      {
         out.append(">","<span onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+ usage_id+ "'); else this.style.cursor='pointer';\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel'); this.style.cursor='';\" class=normalTextLabel onclick=\"if(helpMode) showHelpTag('"+ usage_id+ "'); else _selectItem();");
         //out.append(">","<a class=normalTextLabel href=\"javascript:_selectItem();");
         out.append("function _selectItem(){if (f."+name+".disabled) return; f."+name+"["+index+"].checked=true;");
         if ( tag != null && (tag.indexOf("=") > -1))
         {
            String event = tag.substring(0,tag.indexOf("="));
            event = event.trim();
            out.append("f."+name+"["+index+"]."+event.toLowerCase()+"();");
         }
         out.append("}\">",getASPManager().translate(label,this),"</span>");
      }
      else
         out.append("><span onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+ usage_id+ "')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel OnClick=\"showHelpTag('",usage_id,"')\">",getASPManager().translate(label,this),"</span>"); 
   }   

   protected void printSelectStart( String name, String tag )
   {
      out.append("<select class='selectbox'");
      out.append(" name=\"",name,"\" ");
      if ( tag != null )
         out.append(tag);
      out.append(">");
   }

   protected void printSelectOption( String label, String value, boolean selected )
   {
      if ( selected )
         out.append("<option selected value=\"",value,"\">",label);
      else
         out.append("<option value=\"",mgr.HTMLEncode(value),"\">",label);
      out.append("</option>");

   }

   protected void printSelectEnd( )
   {
      printSelectEnd(false);
   }

   protected void printSelectEnd( boolean mandatory )
   {
      out.append("</select>",(mandatory? "*":""));
   }   

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected void printSelectBox( String name, ASPBuffer aspbuf )
   {
      printSelectBox( name, aspbuf, null, null, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected void printSelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag )
   {
      printSelectBox( name, aspbuf, key, tag, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag, int size )
   {
      printSelectBox( name, aspbuf, key, tag, false, size );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected void printMandatorySelectBox( String name, ASPBuffer aspbuf )
   {
      printSelectBox( name, aspbuf, null, null, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String tag )
   {
      printSelectBox( name, aspbuf, key, tag, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */   
   protected void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String tag, int size )
   {
      printSelectBox( name, aspbuf, key, tag, true, size );
   }   

   private void printSelectBox( String name, ASPBuffer aspbuf, String key, String tag, boolean mandatory, int size )
   {
      out.append("<select class='selectbox' size=");
      out.appendInt(size);
      out.append(" name=\"",name,"\" ");

      if ( tag != null )
         out.append(tag);
      out.append(">");
      if ( mandatory )
         out.append( getASPHTMLFormatter().populateMandatoryListBox(aspbuf, key) );
      else
         out.append( getASPHTMLFormatter().populateListBox(aspbuf, key) );
      out.append("</select>");
   }   

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected void printIIDSelectBox( ASPField fld ) throws FndException
   {
      printIIDSelectBox( fld, null, null, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected void printIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected void printIIDSelectBox( ASPField fld, String key, String tag ) throws FndException
   {
      printIIDSelectBox( fld, key, tag, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected void printMandatoryIIDSelectBox( ASPField fld ) throws FndException
   {
      printIIDSelectBox( fld, null, null, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected void printMandatoryIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */   
   protected void printMandatoryIIDSelectBox( ASPField fld, String key, String tag ) throws FndException
   {
      printIIDSelectBox( fld, key, tag, true );
   }

   private void printIIDSelectBox( ASPField fld, String key, String tag, boolean mandatory ) throws FndException
   {
      if ( !fld.isSelectBox() )
         throw new FndException("FNDPRVNSELBOX: The field &1 is not defined as Select Box.", fld.getName());
      printSelectBox( fld.getName(), fld.getIidClientValuesBuffer(), key, tag, mandatory, 1 );
   }   

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    *
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    */   
   protected void printButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 0, true);
   }
   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */
   protected void printButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 0, isObjectAccessible(sec_objects));
   }

   
   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current page).
    */   
   protected void printSubmitButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 1, true);
   }

   /**
    * Append a named submit button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */   
   protected void printSubmitButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 1,isObjectAccessible(sec_objects));
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will reset the request (current page).
    */   
   protected void printResetButton(String name, String value, String tag)
   {
      printButton(name, value, tag, 2, true);
   }
   /**
    * Append a named reset button input tag to the HTML-output and apply predefined styles.
    * @param name name of the button
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */     
   protected void printResetButton(String name, String value, String tag, String sec_objects)
   {
      printButton(name, value, tag, 2, isObjectAccessible(sec_objects));
   }

   private void printButton( String name, String value, String tag, int type, boolean has_security )
   {
      switch ( type ){
      case 0:
         out.append("<input class='button' type=button");
         break;
      case 1:
         out.append("<input class='button' type=submit");
         break;
      default:
         out.append("<input class='button' type=reset");
      }

      out.append(" name=\"",name);
      out.append("\" value=\"", getASPManager().HTMLEncode(getASPManager().translate(value),true), "\" ");

      if ( tag != null )
         out.append(tag);

      out.append(!has_security?" disabled":"", ">");
   }   

   private void appendJavaScript()
   {
      try{

         String docache = getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N");
         String tempScript = "";
         
         if ("Y".equals(docache))  
         {
           DynamicObject dobj = DynamicObjectCache.get(getScriptCacheKey(), getASPManager().getAspSession());
           tempScript = (String)dobj.getData();           
         }
         else    
           tempScript = Util.readAndTrimFile(getScriptFileName());

         script.append("\n",tempScript,"\n");          

         if ( !Str.isEmpty(scriptfile) )
         {
            String path = scriptfile;
            if ( DEBUG ) debug("  path="+path);

            String phypath = getASPManager().getPhyPath(path);
            if ( DEBUG ) debug("  phypath="+phypath);

            String jsfile = Util.readAndTrimFile(phypath);
            script.append("\n\n\n\n// Included from script file:\n\n",jsfile);
         }

         if ("Y".equals(docache))    
         {
           DynamicObjectCache.put(getScriptCacheKey(), script.toString(), "text/javascript",getASPManager(), true);
           updatePageScript(script.toString()); //also update the variable containing the srcipt in ASPPage
         }
         else 
           Str.writeFile(getScriptFileName(),script.toString());
      }
      catch ( Throwable any ){
         error(any);
      };
   }

   /**
    * Appends the contents of the specified file as JavaScript code to the created .js file.
    * Should be called from preDefine(). For example:
    * <pre>
    *    includeJavaScriptFile("demorw/DemoScript.js");
    *</pre>
    */   
   protected final void includeJavaScriptFile( String filename )
   {
      try{
         modifyingImmutableAttribute("JAVASCRIPTFILE");
         if ( !Str.isEmpty(scriptfile) )
            throw new FndException("FNDPRVFSCRDEF: JavaScript file name is already defined.");
      }
      catch ( Throwable any )
      {
         error(any);
      }

      scriptfile = filename;
   }   

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code,
    */   
   protected final void appendJavaScript( String str ) throws FndException
   {
      appendJavaScript(str,null,null,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code,
    */   
   protected final void appendJavaScript( String s1, String s2 ) throws FndException
   {
      appendJavaScript(s1,s2,null,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code,
    */   
   protected final void appendJavaScript( String s1, String s2, String s3 ) throws FndException
   {
      appendJavaScript(s1,s2,s3,null,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code,
    */
   protected final void appendJavaScript( String s1, String s2, String s3, String s4 ) throws FndException
   {
      appendJavaScript(s1,s2,s3,s4,null);
   }

   /**
    * Creates a 'script' tag and appends the specified strings as JavaScript code,
    */
   protected final void appendJavaScript( String s1, String s2, String s3, String s4, String s5 ) throws FndException
   {
      modifyingImmutableAttribute("JAVASCRIPT");
      script.append(s1);
      if ( s2!=null )  script.append(s2);
      if ( s3!=null )  script.append(s3);
      if ( s4!=null )  script.append(s4);
      if ( s5!=null )  script.append(s5);
   }   

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * If the href represents a relative reference the application root will be added.
    */
   protected void printAbsoluteLink( String value, String href )
   {
      printLink( value, href, true, false, false, false, null, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * @param value label to be display
    * @param href hyperlink 
    */
   protected void printLink( String value, String href )
   {
      printLink( value, href, false, false, false, true );
   }
   
   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * @param value label to be display
    * @param href hyperlink 
    * @param sec_objects comma seperated list of security objects
    */
   protected void printLink( String value, String href, String sec_objects )
   {
      printLink( value, href, false, false, false,isObjectAccessible(sec_objects));
   }

   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * Depending on the container the link will refer to a webpage or a rich webclient form.
    * @param value label to be display
    * @param web_url hyperlink to web page
    * @param rwc_url hyperlink to rich webclient form
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printLink( String value, String web_url, String rwc_url, String sec_objects )
   {
      if(mgr.isRWCHost())
         printLink( value, rwc_url, false, false, false, false, null, ((sec_objects==null)?true:isObjectAccessible(sec_objects)), true);
      else
         printLink( value, web_url, false, false, false,isObjectAccessible(sec_objects));
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    */
   protected void printImageLink( String imgpath, String href )
   {
      printLink( imgpath, href, true, false, false, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * Depending on the container the link will refer to a webpage or a rich webclient form.
    * @param imgpath path to the image location
    * @param web_url hyperlink to web page
    * @param rwc_url hyperlink to rich webclient form
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printImageLink( String imgpath, String web_url, String rwc_url, String sec_objects )
   {
      if(mgr.isRWCHost())
         printLink( imgpath, rwc_url, false, true, false, false, null, ((sec_objects==null)?true:isObjectAccessible(sec_objects)),true);
      else
         printLink( imgpath, web_url, true, false, false, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    */
   protected final void printHoverImageLink( String imgpath, String hovimgpath, String href )
   {
      printLink( imgpath, href, false, true, false, false, hovimgpath, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    * Depending on the container the link will refer to a webpage or a rich webclient form.
    * @param imgpath path to the image location
    * @param hovimgpath path to the hover image location
    * @param web_url hyperlink to web page
    * @param rwc_url hyperlink to rich webclient form
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printHoverImageLink( String imgpath, String hovimgpath, String web_url, String rwc_url, String sec_objects )
   {
      if(mgr.isRWCHost())
         printLink( imgpath, rwc_url, false, true, false, false, hovimgpath, ((sec_objects==null)?true:isObjectAccessible(sec_objects)),true);
      else
         printLink( imgpath, web_url, false, true, false, false, hovimgpath, true );
   }
   
   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters. 
    */
   protected void printScriptLink( String value, String func )
   {
      printLink( value, func, false, true, false, true );
   }
   

   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters.
    * @param value value to be display
    * @param func javascript function with no argument
    * @param sec_objects comma seperated security objects
    */
   
   protected void printScriptLink( String value, String func, String sec_objects )
   {
      printLink( value, func, false, true, false, isObjectAccessible(sec_objects));
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    */
   protected void printSubmitLink( String value )
   {
      printLink( value, null, false, true, true, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters. 
    */
   protected void printSubmitLink( String value, String func )
   {
      printLink( value, func, false, true, true, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes no parameters.
    * @param value value to be display
    * @param func javascript function with param
    * @param sec_objects list of comma seperated security objects
    */   
   protected void printSubmitLink( String value, String func, String sec_objects )
   {
      printLink( value, func, false, true, true, isObjectAccessible(sec_objects));
   }
   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes no parameters. 
    */
   protected void printScriptImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, false,true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    * Specified JavaScript function takes no parameters.
    */
   protected final void printScriptHoverImageLink( String imgpath, String hovimgpath, String func )
   {
      printLink( imgpath, func, false, true, true, false, hovimgpath,true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    */
   protected void printSubmitImageLink( String imgpath )
   {
      printLink( imgpath, null, true, true, true, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * Specified JavaScript function takes no parameters.
    */
   protected void printSubmitImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, true, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page).
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    * Specified JavaScript function takes no parameters.
    */
   protected final void printSubmitHoverImageLink( String imgpath, String hovimgpath, String func )
   {
      printLink( imgpath, func, false, true, true, true, hovimgpath, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Specified JavaScript function takes no parameters.
    */
   private void printLink( String value, String link, boolean image, boolean script, boolean submit, boolean has_access )
   {
      printLink( value, link, false, image, script, submit, null, has_access );
   }   

   protected final void printRWCLink( String value, String url, String sec_objects )
   {
      if (mgr.isRWCHost() || mgr.showRWCLinksInBrowser())
         printLink( value, url, false, false, false, false, null, ((sec_objects==null)?true:isObjectAccessible(sec_objects)), true);
   }   

   protected final void printRWCImageLink( String image_path, String url, String sec_objects )
   {
      if (mgr.isRWCHost() || mgr.showRWCLinksInBrowser())
         printLink( image_path, url, false, true, false, false, null, ((sec_objects==null)?true:isObjectAccessible(sec_objects)),true);
   }

   protected final void printRWCHoverImageLink(String image_path, String hoover_image_path, String url, String sec_objects )
   {
      if (mgr.isRWCHost() || mgr.showRWCLinksInBrowser())
         printLink( image_path, url, false, true, false, false, hoover_image_path, ((sec_objects==null)?true:isObjectAccessible(sec_objects)),true);
   }
   
   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current page) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification. Then the alternative image specification can be
    * send in hovimgpath-parameter to attain the hover effect.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Specified JavaScript function takes no parameters. 
    * Try to add absolute reference if abshref is true.
    * Note: if the value contains '&&' only the text within them will be hyperlinked.
    *       eg: "Click on &&this&& link" for value will only hyperlink the word 'this'.
    */   
   private void printLink( String  value,
                           String  link,
                           boolean abshref,
                           boolean image,
                           boolean script,
                           boolean submit,
                           String  hovimgpath,
                           boolean has_access)
   {
         printLink( value, link, abshref, image, script, submit, hovimgpath, has_access, false);
   }
   
   private void printLink( String  value,
                           String  link,
                           boolean abshref,
                           boolean image,
                           boolean script,
                           boolean submit,
                           String  hovimgpath,
                           boolean has_access,
                           boolean rwc_link)
   {
      String text1="";
      String text2="";
      value = getASPManager().translate(value);
      if(value.indexOf("&&")!=-1)
      {
         if(value.indexOf("&&")==value.lastIndexOf("&&"))
         {
            text1 = value.substring(0,value.indexOf("&&"));
            value = value.substring(value.indexOf("&&")+2,value.length());
         }
         else
         {
            text1 = value.substring(0,value.indexOf("&&"));
            text2 = value.substring(value.lastIndexOf("&&")+2,value.length());
            value = value.substring(value.indexOf("&&")+2,value.lastIndexOf("&&"));
         }
      } 
      //if (has_access)
         out.append("<font class=normalTextValue>");
      //else
      //  out.append("<font class=dissabledLinkValue>");
      out.append(getASPManager().HTMLEncode(text1,true));
      
      if (has_access)
      {
         out.append("<a class=hyperLink href=\"");

         if ( script )
         {
            out.append("javascript:");

            if ( !Str.isEmpty(link) )
               out.append(link,"()");

            if ( submit && !Str.isEmpty(link) )
               out.append(";");

            if ( submit )
               out.append("submit(",")");
         }
         else if (rwc_link)
         {
            out.append("javascript:_navigateToRWC('",link,"');");
         }
         else
         {
            if ( abshref && link.indexOf("://")<0 && !link.startsWith("/") )
               out.append(getApplicationPath(),"/");
            out.append( link );
         }
         out.append("\"");

         if ( image && !Str.isEmpty(hovimgpath) )
         {
            linkseq++;
            out.append(" onmouseover=\"javascript:f.","IMG");
            out.appendInt(linkseq);
            out.append(".src='",hovimgpath,"'\"");
            out.append(" onmouseout=\"javascript:f.","IMG");
            out.appendInt(linkseq);
            out.append(".src='",value,"'\"");
         }
         out.append(">");
      }
      else
         out.append("<font class=dissabledLinkValue>");

      if ( image )
      {
         out.append("<img");
         if ( !Str.isEmpty(hovimgpath) )
         {
            out.append(" name=\"","IMG");
            out.appendInt(linkseq);
            out.append("\"");
         }
         out.append(" src=\"",value,"\" border=0>");
      }
      else
         out.append( getASPManager().HTMLEncode(getASPManager().translate(value),true) );

      if (has_access)
         out.append("</a>");
      else
         out.append("</font>");
     
      out.append(getASPManager().HTMLEncode(text2,true));
      out.append("</font>");
   }   

   /**
    * Return HTML code for a Command Link having the specified label
    * and attached to the given JScript function.
    * @param label label to be display
    * @param server_function server method to be execute
    */
   public final void printCommandLink( String server_function, String label )
   {
      printCommandLink(server_function,label,null);
   }
   
   /**
    * Return HTML code for a Command Link having the specified label
    * and attached to the given JScript function if the use has access to the
    * given comma seperated security object list.
    * @param label label to be display
    * @param server_function server method to be execute
    * @param security_objects comma seperated security object list
    */   
   public final void printCommandLink( String server_function, String label, String security_objects)
   {
      boolean has_access = (security_objects == null)?true:isObjectAccessible(security_objects);
      if (has_access)
      {
         out.append("<A class=hyperLink HREF=\"javascript:commandClick('");
         out.append(server_function);
         out.append("')\">");
      }
      else
         out.append("<font class=dissabledLinkValue>");
      out.append(getASPManager().translate(label));
      if (has_access)
         out.append("</A>\n");
      else
         out.append("</font>\n");

   }
   

   /**
    * Append an image-tag to the HTML-output.
    */
   protected void printImage( String src )
   {
      printImage(src,0,0,0,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    */
   protected void printImage( String src, int width, int height )
   {
      printImage(src,width,height,0,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    */
   protected void printImage( String src, int width, int height, int border )
   {
      printImage(src,width,height,border,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    */
   protected void printImage( String src, int width, int height, int border, String usemap )
   {
      out.append("<img src=\"",src,"\"");
      if ( width>0 )
      {
         out.append(" width=");
         out.appendInt(width);
      }
      if ( height>0 )
      {
         out.append(" height=");
         out.appendInt(height);
      }
      out.append(" border=");
      out.appendInt(border);
      if ( !Str.isEmpty(usemap) )
         out.append(" usemap=\"",usemap,"\"");
      out.append(">");
   }

   /**
    * Append a map-tag to the HTML-output. Can be used in conjunction to an image-tag.
    * Should be closed by calling endDefineImageMap().
    *
    * @see ifs.fnd.asp.ASPPageProvider#endDefineImageMap
    */
   protected void defineImageMap( String name )
   {
      out.append("<map name=\"",name,"\">\n");
   }

   /**
    * Close the previously opened by defineImageMap() function map-tag.
    *
    * @see ifs.fnd.asp.ASPPageProvider#defineImageMap
    */
   protected void endDefineImageMap()
   {
      out.append("</map>\n");
   }

   /**
    * Append an area-tag to the HTML-output. Can be used in conjunction to a map-tag.
    */
   protected void printImageArea( String shape, String coords, String href, String func, String param )
   {
      out.append("<area shape=",shape," coords=",coords);
      out.append(" href=\"",href,"\" ");

      if ( !Str.isEmpty(func) )
      {
         out.append(" onClick=\"javascript:return ",func);
         out.append("('",param,"');\"");
      }
      out.append(">\n");
   }   

   private int     tablelevel = 0;
   private boolean transparent = false;

   /**
    * Begin a HTML table (a table-tag).
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTable()
   {
      beginTable(null,false,true);
   }

   /**
    * Begin a HTML table (a table-tag) with a given id.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTable( String id )
   {
      beginTable(id,false,true);
   }

   /**
    * Begin a HTML table (a table-tag) with transparent background.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTransparentTable()
   {
      beginTable(null,true,true);
   }

   /**
    * Begin a HTML table (a table-tag) with transparent background and a given id.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTransparentTable( String id )
   {
      beginTable(id,true,true);
   }
   
   /**
    * Begin a HTML table (a table-tag). If transparent-flag is true, the table background
    * will be transparent. Value true of the fullwidth-parameter will
    * generate the table width of 100%.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTable( String id, boolean transparent, boolean fullwidth )
   {
       beginTable( id, transparent, fullwidth, "" ); 
   }
   
   /**
    * Begin a HTML table (a table-tag). If transparent-flag is true, the table background
    * will be transparent. Value true of the fullwidth-parameter will
    * generate the table width of 100%.
    * params will be inseterd within the table tag.
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTable
    */
   protected void beginTable( String id, boolean transparent, boolean fullwidth, String params )
   {
      this.tablelevel++;
      this.transparent = transparent;
      out.append("<table");
      if ( !Str.isEmpty(id) )
         out.append(" id=",id);
      out.append(" border=0 cellpadding=0 cellspacing=0 ",fullwidth?"width=100% ":"",params,">\n");
      colcnt = 0;
   }

   /**
    * End a HTML table opened with beginTable() function.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTable
    */
   protected void endTable() throws FndException
   {
      out.append("</table>\n");
      tablelevel--;
      if ( tablelevel<0 )
         throw new FndException("FNDPRVENDTAB: No open tables exist!");
   }


   private boolean intitlerow = false;

   /**
    * Begin a table title row (a tr-tag). Should be closed by calling endTableTitleRow().
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTableTitleRow
    */
   protected final void beginTableTitleRow() throws FndException
   {
      beginTableTitleRow(null);
   }

   /**
    * Begin a table title row (a tr-tag) with a specified id.
    * Should be closed by calling endTableTitleRow().
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTableTitleRow
    */
   protected final void beginTableTitleRow( String id ) throws FndException
   {
      if ( intitlerow )
         throw new FndException("FNDPRVINTITL: Already in title row!");
      intitlerow = true;
      out.append("<tr");
      if ( !Str.isEmpty(id) )
         out.append(" id=",id);
      out.append(">\n");
      colcnt = 0;
   }

   /**
    * Close a table title row that was opened by beginTableTitleRow() function.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableTitleRow
    */
   protected final void endTableTitleRow() throws FndException
   {
      if ( !intitlerow )
         throw new FndException("FNDPRVNINTITL: Not in title row!");
      intitlerow = false;
      out.append("</tr>\n");
   }


   private int tabbodylevel = 0;

   /**
    * Begin a body of a HTML table (a tbody-tag). Should be closed by endTableBody().
    * Also begin the first row in the table (a tr-tag).
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTableBody
    * @see ifs.fnd.asp.ASPPageProvider#nextTableRow
    */
   protected final void beginTableBody() throws FndException
   {
      beginTableBody(null);
   }

   /**
    * Begin a HTML table body (a tbody-tag) with a specified id.
    * Should be closed by endTableBody().
    * Also begin the first row in the table (a tr-tag).
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTableBody
    * @see ifs.fnd.asp.ASPPageProvider#nextTableRow
    */
   protected final void beginTableBody( String id ) throws FndException
   {
      ASPConfig cfg = getASPConfig();
      if ( tabbodylevel>=tablelevel )
         throw new FndException("FNDPRVINBODY: Already in table body!");
      this.tabbodylevel++;
      this.evenrow = false;
      out.append("<tbody>\n<tr");
      if ( !Str.isEmpty(id) )
         out.append(" id=",id);
      if ( !transparent )
         out.append(" class=tableRowColor1");
      out.append(">\n");
      colcnt = 0;
   }

   /**
    * Close the last table row and close table body opened by beginTableBody().
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableBody
    */
   protected final void endTableBody() throws FndException
   {
      if ( tabbodylevel<tablelevel )
         throw new FndException("FNDPRVNINBDY: Not in table body!");
      this.tabbodylevel--;
      out.append("</tr>\n</tbody>\n");
   }


   private boolean evenrow = false;

   /**
    * Close current table row and begin next one.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableBody
    */
   protected final void nextTableRow() throws FndException
   {
      nextTableRow(null);
   }

   /**
    * Close current table row and begin next one with specified id.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableBody
    */
   protected final void nextTableRow( String id ) throws FndException
   {
      ASPConfig cfg = getASPConfig();
      if ( tabbodylevel<tablelevel )
         throw new FndException("FNDPRVNINBDY2: Not in table body!");
      evenrow = !evenrow;
      out.append("</tr>\n<tr");
      if ( !Str.isEmpty(id) )
         out.append(" id=",id);
      if ( !transparent && !evenrow )
         out.append(" class=tableRowColor1");
      else if(!transparent)
         out.append(" class=tableRowColor2");
         
      out.append(">\n");
      colcnt = 0;
   }


   /**
    * Constant for definition of left horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPageProvider#printTableCell
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final static String LEFT   = "left";
   /**
    * Constant for definition of right horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPageProvider#printTableCell
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final static String RIGHT  = "right";
   /**
    * Constant for definition of center horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPageProvider#printTableCell
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final static String CENTER = "center";

   private int colcnt = 0;

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    *
    */
   protected final void printTableCell( String text ) throws FndException
   {
      printTableCell(text,0,0,null);
   }

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text and horizontal alignment.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final void printTableCell( String text, String align ) throws FndException
   {
      printTableCell(text,0,0,align);
   }

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text and column span.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final void printTableCell( String text, int colspan ) throws FndException
   {
      printTableCell(text,colspan,0,null);
   }

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text, horizontal alignment and column and row span.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final void printTableCell( String text, int colspan, int rowspan, String align ) throws FndException
   {
      beginTableCell(colspan, rowspan, align);
      if ( Str.isEmpty(text) )
         out.append("&nbsp;");
      else
         out.append(getASPManager().HTMLEncode(getASPManager().translate(text),true));
      endTableCell();
   }


   private int tabcelllevel = 0;

   /**
    * Begin a table title or contents cell (a th- or td-tag).
    * Should be closed by calling endTableCell().
    *
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell() throws FndException
   {
       beginTableCell(0,0,null,null,false);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag)
    * and define the cells horizontal alignment.
    * Should be closed by calling endTableCell().
    * 
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell( String align ) throws FndException
   {
       beginTableCell(0,0,align,null,false);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag)
    * and define the cells column span.
    * Should be closed by calling endTableCell().
    * 
    * @param colspan Integer value specifying column span.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell( int colspan ) throws FndException
   {
       beginTableCell(colspan,0,null,null,false);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag).
    * Define the cells horizontal alignment, column and row span.
    * Should be closed by calling endTableCell().
    *
    * @param colspan Integer value specifying column span.
    * @param rowspan Integer value specifying row span.
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell( int colspan, int rowspan, String align ) throws FndException
   {
       beginTableCell(colspan,rowspan,align,null,false);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag) with a specified id.
    * Define the cells horizontal alignment, column and row span.
    * Should be closed by calling endTableCell().
    *
    * @param colspan Integer value specifying column span.
    * @param rowspan Integer value specifying row span.
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @param id String value specifying the cell Id.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell( int colspan, int rowspan, String align, String id ) throws FndException
   {
       beginTableCell(colspan,rowspan,align,id,false);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag)
    * Define the cells nowrap property.
    * Should be closed by calling endTableCell().
    * 
    * @param nowrap Set to true to add nowrap else false.
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
    */
   protected final void beginTableCell(boolean nowrap) throws FndException
   {
       beginTableCell(0,0,null,null,nowrap);
   }

   /**
    * Begin a table title or contents cell (a th- or td-tag)
    * Define the cells nowrap property and horizontal alignment.
    * Should be closed by calling endTableCell().
    * 
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @param nowrap Set to true to add nowrap else false.
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
    */
   protected final void beginTableCell(String align, boolean nowrap) throws FndException
   {
      beginTableCell(0,0,align,null,nowrap);

   }
   /**
    * Begin a table title or contents cell (a th- or td-tag) with a specified id.
    * Define the cells horizontal alignment, column, row span and nowrap.
    * Should be closed by calling endTableCell().
    *
    * @param colspan Integer value specifying column span.
    * @param rowspan Integer value specifying row span.
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @param id String value specifying the cell Id.
    * @param nowrap Set to true to add nowrap else false.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   protected final void beginTableCell( int colspan, int rowspan, String align, String id , boolean nowrap) throws FndException
   {
      beginTableCell( colspan, rowspan, align, id , nowrap, 0);
   }

    /**
    * Begin a table title or contents cell (a th- or td-tag) with a specified id.
    * Define the cells horizontal alignment, column, row span and nowrap.
    * Should be closed by calling endTableCell().
    *
    * @param colspan Integer value specifying column span.
    * @param rowspan Integer value specifying row span.
    * @param align Specifies horizontal alignment in a cell. Pass LEFT,CENTER or RIGHT.
    * @param id String value specifying the cell Id.
    * @param nowrap Set to true to add nowrap else false.
    * @param width Set the width in percentage.
    * @see ifs.fnd.asp.ASPPageProvider#endTableCell
    */
   
   protected final void beginTableCell( int colspan, int rowspan, String align, String id , boolean nowrap, int width) throws FndException
   {

      //if(!intitlerow && !intabbody)
      if ( !intitlerow && tabbodylevel<tablelevel )
         throw new FndException("FNDPRVNINTAB: Not in title row or table body!");

      //if(intabcell)
      if ( tabcelllevel>=tablelevel )
         throw new FndException("FNDPRVNINTABCELL: Already in table cell!");

      //if(colcnt>0)
      if ( !transparent && colcnt>0 )
      {
         out.append( intitlerow ? "<th>" : (transparent?"<td>":"<td bgcolor=white>"));
         out.append("<img src=\"",getASPConfig().getImagesLocation(),"table_empty_image.gif\" width=3 height=1>");
         out.append( intitlerow ? "</th" : "</td", ">\n");
      }

      if ( intitlerow )
         out.append("<th class=tableColumnHeadingText");
      else
         out.append("<td class=tableContentText");

      if ( !Str.isEmpty(id) )
         out.append(" id=",id);

      if ( colspan>0 )
      {
         out.append(" colspan=");
         out.appendInt(colspan);
      }
      if ( rowspan>0 )
      {
         out.append(" rowspan=");
         out.appendInt(rowspan);
      }
      if ( !Str.isEmpty(align) )
         out.append(" align=",align);

      if (nowrap) 
         out.append(" nowrap");
      
      if (width >0)
         out.append(" width='" + width + "%'");

      out.append(">");
      colcnt += colspan>0 ? colspan : 1;
      //intabcell = true;
      tabcelllevel++;
   }

   /**
    * Close a table title or contents cell that was opened by beginTableCell() function.
    *
    * @see ifs.fnd.asp.ASPPageProvider#beginTableCell
    */
   protected final void endTableCell() throws FndException
   {
      //if(!intitlerow && !intabbody)
      if ( !intitlerow && tabbodylevel<tablelevel )
         throw new FndException("FNDPRVNINTAB2: Not in title row or table body!");

      //if(!intabcell)
      if ( tabcelllevel<tablelevel )
         throw new FndException("FNDPRVNNINTABCELL: Not in table cell!");

      out.append("</",intitlerow?"th":"td",">\n");
      //intabcell = false;
      tabcelllevel--;
   }   

   /**
    * Populate an ASP table.
    */
   protected final void printTable( ASPTable tbl )
   {
      tbl.disableOutputChannels();
      tbl.disableEditProperties();
      tbl.unsetSortable();
      tbl.disableQuickEdit();
      ASPBlock blk = tbl.getBlock();
      if ( blk!=null )
      {
         ASPCommandBar bar = blk.getASPCommandBar();
         if ( bar!=null )
            bar.disableCommand(ASPCommandBar.VIEWDETAILS);
      }

      out.append( tbl.populate() );
   }   

   /**
    * Transfer data to print dialog page. If second parameter is true,
    * opens dialog in new browser, else in same browser window.
    */
   public void callPrintDlg(ASPBuffer object, boolean newwindow)
   {
      if ( DEBUG ) debug("ASPPageProvider.callPrintDlg() ");
      
      ASPManager mgr = getASPManager();
      
      String protocol = mgr.getProtocol();
      String server = mgr.getServerName();
      String port = mgr.getPortString();
      String path = mgr.getApplicationPath();
      String dialogstr = path+"/common/scripts/ReportPrintDlg.page";

      String url = dialogstr;
      
      if ( DEBUG ) debug("ASPPageProvider.callPrintDlg() url: " + url);

      if ( newwindow )
      {
         String transfer = "";
         transfer = mgr.getTransferedQueryString(object);
         transfer += "&__NEWWIN=true";
         debug("ASPPageProvider.callPrintDlg() new window " + transfer);

         dirty_script.append("\n javascript:openPrintDlg(\""+url+"?"+transfer+"\"); \n");
      } 
      else
      {  
         mgr.transferDataTo(url,object);
      }

   }

  /**
    * This can be overridden by the developer.
    * This will be automatically called before a file is being uploaded,
    * provided that the run() method implements  if( mgr.commandBarActivated() )eval(mgr.commandBarFunction());
    * When overridden, mgr.responseWrite("TRUE"); will allow uploading, 
    * whereas mgr.responseWrite("FALSE"); will restrict uploading.
    */
   public void isAuthorizedToUpload()
   {
      ASPManager mgr = getASPManager();
      mgr.responseWrite("TRUE");
      mgr.endResponse();
   }

   /**
    * Returns the uploaded file name
    * @return Uploaded file name.
    */
   public String getUploadingFileName()
   { 
       return mgr.readValue("__FILE");
   }    

   /**
    * Appends the start of a table to print data presentation
    */   
   public void beginDataPresentation()
   {
       out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\"><tr><td>&nbsp;&nbsp;</td><td width=\"100%\">\n" );   
   }

   /**
    * Appends the end of the table to print data presentation
    */
   public void endDataPresentation()
   {
        endDataPresentation(true);
   }
   
   /**
    * Appends the end of the table to print data presentation
    * @params dotted_line Value 'false' will ignore the dotted line at the end of the data presentation.
    */      
   public void endDataPresentation(boolean dotted_line)
   {
       if(dotted_line)
       {
          out.append( "<table cellspacing=0 cellpadding=0 border=0 width=\"100%\" class=pageForm>"+
                      "<tr><td with=100% height=1></td></tr></table>\n"+
                      "</td><td>&nbsp;&nbsp;</td></table>\n" );
       }   
       else
          out.append( " </td><td>&nbsp;&nbsp;</td></table>\n" );   
   }   
   
   /**
    * Appends the html table for a simple command bar.
    * @params title Title of the command bar.
    */ 
   public void drawSimpleCommandBar(String title)
   {
       out.append( "<table cellpadding=0 cellspacing=0 width=100% class=\"pageCommandBar\" height=22>"+
                   "<tr><td height=22 width=100% >&nbsp;&nbsp;"+getASPManager().translate(title)+"</td></tr></table>\n");
   }  
   
   /**
    * @deprecated - use new programing model instead.
    */
   public int qurySubmit(Record req, ASPBlock blk){return 0;}
   
   private String getTemplate() throws FndException
   {
      ASPManager mgr = getASPManager();
      String value = mgr.readValue("__TEMPLATE_ID","");
      return value;
   }
   
   public void setDefaultValues() throws FndException
   {
      if ( DEBUG ) debug("ASPPageProvider.setDefaultValues() ");
      
      String key = getASPManager().readValue("__COMMAND");
      if ( Str.isEmpty(key) )
         throw new FndException("FNDMGRCMDBARNACT: The command bar has not been activated");
      int pos = key.indexOf('.');
      String blkname = pos<=0 ? null : key.substring(0,pos);
      
      ASPBlock blk = getASPBlock(blkname);
      ASPBuffer buff = blk.setDefaultValues(getTemplate());
      int size = buff.countItems();
      
      String finalize = "";
      String field_tag = ".value=";
      String chkbox_tag = ".checked=";
      
      boolean multirow = blk.getASPBlockLayout().isMultirowLayout();
      int row_no = 0;
      
      if (multirow)
      {
         row_no = blk.getASPRowSet().countRows()-1;
         field_tag  = "["+row_no+"].value=";
         chkbox_tag = "["+row_no+"].checked=";
      }
      
      appendDirtyJavaScript("__setDefaultValues();\n");
      appendDirtyJavaScript("function __setDefaultValues(){\n");
      for (int i=0; i<size; i++)
      {
         ASPBuffer buf = buff.getBufferAt(i);
         String name    = buf.getValue("NAME");
         String def_val = buf.getValue("DEF_VAL");
         String method  = buf.getValue("METHOD");
         
         ASPField fld = blk.getASPField(name);
         
         if (fld.isCheckBox())
         {
            if ("TRUE".equals(def_val))
            {
               appendDirtyJavaScript("\n\tf."+name+chkbox_tag+"true;\n");
               finalize += "\n\tf."+name+chkbox_tag+"true;\n";
            }
            /*else
            {
               appendDirtyJavaScript("\n\tf."+name+chkbox_tag+"false;\n");
               finalize += "\n\tf."+name+chkbox_tag+"false;\n";
            }*/
         }
         else if (fld.isSelectBox())
         {
            String client_value = fld.decode(def_val);
            appendDirtyJavaScript("\n\tf."+name+".value=\""+client_value+"\";\n");
            finalize += "\n\tf."+name+".value=\""+client_value+"\";\n";
         }
         else
         {
            if (!multirow)
            {    
               appendDirtyJavaScript("\n\tf."+name+".value=\""+def_val+"\";\n");
               finalize += "\n\tf."+name+".value=\""+def_val+"\";\n";
            }  
            else  
            {    
               appendDirtyJavaScript("\n\tgetField_('"+name+"',"+(row_no+1)+").value=\""+def_val+"\";\n");
               finalize += "\n\tgetField_('"+name+"',"+(row_no+1)+").value=\""+def_val+"\";\n";
            }
         }
         
         if (!multirow)
            appendDirtyJavaScript("\t"+method+"(-1);\n");
         else
            appendDirtyJavaScript("\t"+method+"("+(row_no+1)+");\n");
         
      }
      //repopulate all feidls with default values; in case they have been overridden by validations
      appendDirtyJavaScript(finalize);
      appendDirtyJavaScript("}\n");
   }
   
   /**
    * Enables conversion of a get request to a post request.
    */
   public void enableConvertGettoPost()
   {
     try
     {
         modifyingImmutableAttribute("CONVERT_GET_TO_POST");
         convert_gettopost = true;
     }
     catch( Throwable any )
     {
         error(any);
     }
     
   }

   /**
    * Disables conversion of a get request to a post request
    */
   public void disableConvertGettoPost()
   {
      try
      {
         modifyingImmutableAttribute("CONVERT_GET_TO_POST");
         convert_gettopost= false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }
   
   /* get the convert_gettopost value*/
   public boolean isConvertGettoPost()
   {
      return convert_gettopost;
   }
 
   /* 
    * Retrieves the notes attached to a record of the page (Works when the page is in Master-Detail format).
    * The parameter query is of Object type because, depending on the access provider (RMI or JAP) the in parameter can
    * be of type 'FndQueryRecord' when RMI is used and of type 'Record' when JAP is used.
    * The return value is of type Object because, depending on the access provider (RMI or JAP) return value can
    * be of type 'FndNoteBookArray' when RMI is used and of type 'Record' when JAP is used.
    * @ param Object
    * @ return Object
    */
   public Object getClientNoteBook(Object query) throws FndException
   {
      boolean RMI = true;
      RMI = "RMI".equals(getASPConfig().getParameter("ADMIN/TRANSACTION_MANAGER","RMI"));
      Object return_obj = null;
      if(RMI)
      {
         if(query instanceof FndQueryRecord)
         {
            ConnectionPool.Slot con = EJBConnectionPool.get(ConnectionPool.PAGE_NOTES_LOAD, getASPConfig());
            return_obj = con.invoke(query);
            con.release();
         }
      }
      else
      {
         if(query instanceof Record)
         {
            ConnectionPool.Slot con = JAPConnectionPool.get(ConnectionPool.PAGE_NOTES_LOAD, getASPConfig());
            return_obj = con.invoke(query);
            con.release();
         }
      }
      return return_obj;
   }

   /* 
    * Saves the notes belonging to a record of the page (Works when the page is in Master-Detail format).
    * The parameter client_notebook is of Object type because, depending on the access provider (RMI or JAP) the in parameter can
    * be of type 'FndNoteBook' when RMI is used and of type 'Record' when JAP is used.
    * The return value is of type Object because, depending on the access provider (RMI or JAP) return value can
    * be of type 'FndNoteBook' when RMI is used and of type 'Record' when JAP is used.
    * @ param Object
    * @ return Object
    */
   public Object saveClientNoteBook(Object client_notebook) throws FndException
   {
      boolean RMI = true;
      RMI = "RMI".equals(getASPConfig().getParameter("ADMIN/TRANSACTION_MANAGER","RMI"));
      Object return_obj = null;
      if(RMI)
      {
         if(client_notebook instanceof FndNoteBook)
         {

            ConnectionPool.Slot con = EJBConnectionPool.get(ConnectionPool.PAGE_NOTES_SAVE, getASPConfig());
            FndContext ctx = FndContext.getCurrentContext();
            ctx.setRunAs(mgr.getUserId().toUpperCase());
            return_obj = con.invoke(client_notebook);
            con.release();
         }
      }
      else
      {
         if(client_notebook instanceof Record)
         {
            ConnectionPool.Slot con = JAPConnectionPool.get(ConnectionPool.PAGE_NOTES_SAVE, getASPConfig());
            Server srv = (Server)con.getConnectionProvider();
            srv.setRunAs(mgr.getUserId());
            return_obj = con.invoke(client_notebook);
            con.release();
         }
      }
      return return_obj;
   }
   
   /*
    * Returns whether user has logged in. if the user_logged_in value
    * is null. get the session attribute value. This method has used to enchance Aurora
    * Single sign on functionality.
    * @ return HttpSession
    */
   private String isUserLoggedIn(HttpSession session)
   {
         if(user_logged_in == null)
            return (String)session.getAttribute("USER_LOGGED_IN");
         else
            return user_logged_in;
   }  
 
   private void loadEnterpriseExplorerSettings()
   {
      if(!mgr.isEmpty(mgr.readValue("_SETTHEME")))
         writeGlobalProfileValue(ProfileUtils.SELECTED_THEME, mgr.readValue("_SETTHEME"), false );
      else if(!mgr.isEmpty(mgr.readValue("_LDPROFBUF")))
         mgr.reloadProfileCache();
      
      mgr.responseWrite("0^Done");
      mgr.endResponse();
   }
   
   // Added by Terry 20120925
   // Must be override in subclass.
   protected String getBizWfSourceDesc()
   {
      if ( DEBUG ) debug(this+": ASPPageProvider.getBizWfSourceDesc()");
      return "";
   }
   
   // Adjust function in ASPPageProvider
   protected void adjust() throws FndException
   {
      // fill function body
   }
   // Added end
   
}
