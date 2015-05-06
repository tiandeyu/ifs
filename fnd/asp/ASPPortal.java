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
 * File        : ASPPortal.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Nov-30 - Created.
 *    Jacek P  2000-Jan-17 - Extends ASPPageElement
 *    Jacek P  2000-Feb-21 - Changed name for ASPPortletProvider class.
 *    Jacek P  2000-Feb-22 - New GUI.
 *    Jacek P  2000-Mar-21 - New concept with pool key.
 *    Stefan M 2000-Apr-06 - Added generatePopupClickScript(), generatePopupDefinitions(),
 *                           called for all portlets.
 *    Jacek P  2000-Apr-11 - Implementation of portal states and cache.
 *                           Added call to runCustom().
 *    Jacek P  2000-Apr-19 - Changes in portlet initialization. Caching of HTML.
 *    Jacek P  2000-May-08 - Beta 4 release of Web Kit 3.0.0.
 *    Jacek P  2000-May-09 - New version of initAvailableList() function that will
 *                           scan the file structure after installed portlets.
 *    Jacek P  2000-May-11 - Netscape support for minimization ('*' as portlet id).
 *    Jacek P  2000-May-16 - Changes in HTML layout for support of Netscape.
 *    Jacek P  2000-May-22 - Added handling of command bar function. New function
 *                           parseCommandBarFunction(). New query string
 *                           parameter 'CUSTOMIZE=Y'.
 *    Jacek P  2000-May-24 - Show only user portlets in 'Available portlets' combo box
 *                           on customize page (generateHTML() method). Description
 *                           fetched from available list.
 *    Jacek P  2000-May-31 - Better support for static column width in prepareHTML().
 *                           caught FndExeption in call to run() method in portlets
 *                           for adding of portlet title.
 *    Jacek P  2000-Jul-26 - Log id 226: Improved error handling.
 *    Jacek P  2000-Jul-28 - Bug id 248: Added call to HTMLEncode() for all titles
 *                           and translated descriptions.
 *    Jacek P  2000-Jul-31 - Log id 269: Added sorting of items in Add portlet combo
 *                           in method generateHTML().
 *    Jacek P  2000-Aug-01 - Changes in minimize-portlet algorithm due to problems
 *                           in Netscape (#123).
 *    Jacek P  2000-Aug-03 - Log id 107: added update-button to every portlet in
 *                           generateBoxHeader().
 *    Jacek P  2000-Aug-04 - Better configuration of portal (log id: 114)
 *    Jacek P  2000-Aug-08 - Generation of cookie name for minimized portlets.
 *    Jacek P  2000-Aug-14 - Better algorithm for resizing of dynamic and static columns.
 *    Jacek P  2000-Oct-04 - Added method getColumnWidth() (log id #426).
 *    Jacek P  2000-Oct-17 - Implementation of the automatic refresh (log id #427).
 *    Stefan M 2000-Nov-07 - Image names are now fetched from ASPConfig.
 *    Jacek P  2000-Nov-16 - Instantiate portlets if the HTML-cache is empty. Changed
 *                           interface of the activate() methods (Log id #331).
 *    Jacek P  2000-Dec-11 - Added back MAX- & MINTITLE functionality.
 *    Jacek P  2000-Dec-21 - Improved handling of profiles. Corrected bug in handling
 *                           of refresh time.
 *    Artur K  2001-Feb-21 - Changed clspath in initAvailablePortlets() function - temporary
 *                           solution.
 *    Artur K  2001-Mar-20 - Necessary changes for handling multiple portal pages.
 *    Artur K  2001-Apr-17 - Added functions for handling global variables; changes in multiple
 *                           portal pages functionality.
 *    Piotr Z  2001-Apr-25 - Added portal view export and import functionality.
 *    Piotr Z  2001-Apr-25 - Scaling of input and textarea fields size for Netscape.
 *    Artur K  2001-May-24 - Changes for handling special characters in view description.
 *    Kingsly P. 2001-Jun-04 - Show Configuration page after pressing OK if column no
 *                             changed.(Log Id #590).
 *    Piotr Z  2001-Jun-07 - Changed initAvailableList() method.
 *    Artur K  2001-Jun-12 - Changes for handling slimed portal version (log id 575);
 *                           added isGlobalVariableDefined() function.
 *    Chandana 2001-Jun-27 - Changed OK and Default button types from submit to normal.
 *    Ramila   2001-Aug-24 - Implemented code for Log # 775.
 *    Ramila   2001-Aug-24 - Corrected grammatical error. PROJ call id 68088.
 *    Suneth M 2001-Sep-12 - Changed duplicate localization tags.
 *    Ramila H 2001-Sep-19 - Corrected call id 69047, removed extra ).
 *    Daniel S 2002-Feb-05 - Fixed thread problems. pre_profile is now cloned and some methods
 *                           are synchronized.
 *    Jacek P  2002-Feb-19 - Changed handling of profile cloning on submitCustomization due to a bug.
 *    Daniel S 2002-Mar-06 - Fixed handling of profile cloning on submitCustomization due to above change.
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    Chandana 2002-Aug-30 - Added support to Multiple Data Stores for portlets.
 *    Mangala  2002-Sep-04 - Minor bug fix in clone method
 *    ChandanaD2002-Oct-21 - Checks cfg.isPortletConfigEnabled() to enable the customize icon of portlets.
 *    Shiran   2002-Oct-24 - Added method isPortletMaximized.
 *    ChandanaD2002-Oct-28 - Made compatible with NE6 and above.
 *    Mangala  2002-Nov-04 - Bug fix for the above task.
 *    Ramila H 2002-Nov-04 - Bug id 33961, corrected.
 *    ChandanaD2002-Nov-06 - Corrected portlet command bar icon alignment problem in NE6 and above.
 *    ChandanaD2002-Nov-25 - Portlet header height adjusted.
 *    ChandanaD2002-Dec-12 - Corrected portlet command bar icon alignment problem in NE4.x.
 *    Sampath  2002-Dec-26 - add functionality to have zoom in feature in portlets
 *    Sampath  2003-Feb-28 - fixed problems in portal copy
 *    Johan S  2003-Mar-04 - Changed min/max boxes to use transient cookies.
 *    Sampath  2003-Apr-24 - Changes to display the customised portlet Title in portal configuration page.
 *    ChandanaD2003-May-22 - Modified prepareHTML() and generateHTML(out) to format the portlets frame.
 *    ChandanaD2003-May-27 - Modified prepareHTML() to insert left/right page margins in the portlets customize mode.
 *    Ramila H 2003-May-30 - Did GUI face lift for portal configuration page.
 *                           Added multi-column portlet support.
 *    Ramila H 2003-Jun-05 - Added support for wizard steps.
 *    Ramila H 2003-Jun-19 - Log id 1078 implemented.
 *    Ramila H 2003-Jul-26 - Log id 1080, xml profile import/export implemented.
 *    Ramila H 2003-Sep-03 - Call Id 102117, checked to ifs.fnd.portlets and hardcoded 'General'.
 *    Ramila H 2003-Sep-03 - Call Id 102228, added translatable text for forced move message.
 *    Ramila H 2003-Sep-04 - Changed mime type when exporting profiles to xml.
 *    Ramila H 2003-Sep-04 - Removed button width to avoid translation problems.
 *    Ramila H 2003-10-03  - Added functionality to set initial focus o left most column.
 *                           Changed some error messages.
 *                           Avoiding submitting the form when enter is pressed. (visibility:hidden field).
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    ChandanaD2003-10-20  - Temporally reveresd changes done for clonnig of profiles.
 *    Ramila H 2003-10-28  - Changed translation key for 'clear profile'.
 *    Jacek P  2004-Jan-04 - Bug#40931. Added handling of session related data from ASPPortletHandle. Introduction
 *                           of a new class, ASPPortletSessionHandle.
 *    ChandanaD2004-Apr-23 - Added tooltips for portlet boxes.
 *    ChandanaD2004-May-12 - Updated for new style sheets.
 *    ChandanaD2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Ramila H 2004-08-18  - fixed bug regarding portlet.js and multilanguage
 *    Ramila H 2004-10-18  - Implemented JSR168 support
 *    Ramila H 2004-10-21  - Added method appendPortletClientScript to add ONLY current portlet's js in CUSTOM mode.
 *    Ramila H 2004-11-03  - Implemented code to show alert msgs at the bottom of portlets in std portal mode.
 *    Ramila H 2004-11-04  - Corrected bug in getColumnWidth.
 *    Jacek P  2004-Nov-11 - Added method newProfile()
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/10/26 sumelk Bug 93718, Changed initAvailableList() to remove the Select Statement portlet for b2b template.
 * 2010/10/21 amiklk Bug 93632, Changed prepareHTML() to schedule portal refresh n minutes after the page loads.
 * 2010/03/03 amiklk Bug 89328, Changed generateBoxHeader()
 * 2009/11/10 sumelk Bug 87034, Changed generateCustBodyHTML() to fix the HTML errors.
 * 2009/02/05 amiklk Bug 80053, Added String remove_portlet, 
              Modified construct() to initialize remove_portlet, clone() to clone remove_portlet, 
              generateBoxHeader(out, ph, mode) to add the Remove_portlet icon, 
              generatePortletsHTML(col, columnNumber) to print the portlet ids as hidden fields.
 * 2008/01/16 sadhlk Bug 70598, Reverted the change done in bug 69826. 
 * 2008/01/04 sumelk Bug 70238, Changed prepareHTML() to append the variable __CURRENT_BLOCK_NAME.
 * 2007/12/31 sadhlk Bug 69826, Modified initAvailableList() to correct translation issue.
 * 2007/12/07 sumelk Bug 69484, Modified generateCustBodyHTML(). 
 * 2007/10/17 sadhlk Bug 67682, Modified setViewName().
 * 2007/07/03 sadhlk Merged Bug 64669, Modified generateBoxHeader(), generateCustomizeHTML()
 * 2007/05/14 sadhlk Bug 65359, Modified setViewName().
 * 2007/05/09 buhilk Bug 65252, Modified generateCustBodyHTML() to fix but in French language.
 * 2007/05/04 sadhlk Bug 64337, Added constants 'DELETED_VIEWS','IS_OWN' and Modified setViewName().
 * 2007/05/04 buhilk Bug 65098, Modified prepareHTML() to improve broadcast message functionality.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/12/08 buhilk Bug id 62316, Modified generateCustBodyHTML() to fix html style warnings in FF2.
 * 2006/10/18 riralk Bug id 57025, Modified setViewName() and submitCustomization() to consider Position of portal views.
 * Revision 1.12 2006/09/13           gegulk 
 * Bug 60473, Modified the method prepareHTML() to set the alignment according to the direction (RTL mode)
 * Revision 1.11 2006/07/20           buhilk
 * Bug 59518, Changed "_IMPORT" mode in setViewName(), xmlContents() and Added importProfileBuffer()
 *
 * Revision 1.10 2006/04/27           sumelk
 * Bug 56316, Changed setViewName(). 
 *
 * Revision 1.9  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.8  2005/11/15 11:37:24  japase
 * ASPProfile.save() takes 'this' as argument.
 *
 * Revision 1.7  2005/11/10 10:21:08  rahelk
 * changes related to usages
 *
 * Revision 1.6  2005/11/10 07:38:40  mapelk
 * Bug Fix: Null Pointer exception when configuring portlets
 *
 * Revision 1.5  2005/11/08 07:50:55  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.4  2005/11/04 12:31:19  japase
 * Method profile.forceDirty() called conditionally.
 *
 * Revision 1.3  2005/10/20 08:18:29  mapelk
 * Performance correction: Send only one DB call instead of one DB call per each component in initAvailableList()
 *
 * Revision 1.2  2005/09/28 11:58:37  japase
 * Added check if profile is reset befor making a new assignment.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.11  2005/08/11 11:43:37  riralk
 * Modified exportToXML() to set correct mime type and content disposition.
 *
 * Revision 1.10  2005/08/08 09:44:05  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.9  2005/08/05 11:36:52  riralk
 * Modified setViewName() and xmlContents() to support  XML-export and  XML-import in new profile framework.
 *
 * Revision 1.8  2005/08/04 13:41:21  riralk
 * Fixed/Removed profile related Q&D's and removed obsolete code.
 *
 * Revision 1.7  2005/07/14 06:48:32  japase
 * Added some debug statements
 *
 * Revision 1.6  2005/05/06 09:56:42  mapelk
 * changes required to make standard portlets (JSR 168) to run on WebSphere
 *
 * Revision 1.5  2005/04/08 06:05:37  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.4  2005/04/01 13:59:56  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.3  2005/02/24 13:48:58  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.2  2005/02/02 08:22:18  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.4  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * Revision 1.3  2004/12/17 09:55:02  riralk
 * Minor fixes for caching support for clustered environments when used with proxy servers
 *
 * Revision 1.2  2004/12/15 11:04:04  riralk
 * Support for clustered environments by caching business graphics and generated javascript files in memory
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.servlets.*;
import ifs.fnd.xml.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

//Bug 40931, start
import javax.servlet.http.HttpSession;
//Bug 40931, end
import javax.portlet.*;


/**
 */
public class ASPPortal extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortal");

   public static final int MAXIMIZED = 1;
   public static final int MINIMIZED = 2;
   public static final int CUSTOM    = 3;

   public static final int NORMAL_MODE = 1;
   private static final int SUBMIT_MODE = 2;
   private static final int REMOVE_MODE = 3;
   public static final int CUSTOM_MODE = 4;

   private static final int PREPARE = 1;
   private static final int PERFORM = 2;

   private static final String SEP = "@";

   private static final String MIN_BOXES_COOKIE = "IFSAPPMINIMIZEDPORTLETS";
   //Bug 40931, start
   private static final String PORTLET_SESSION_HANDLERS = "PORTLET_SESSION_HANDLERS";
   //Bug 40931, end

   //constants for new profile handling
   public static final String PAGE_NODE = ProfileUtils.ENTRY_SEP + "Page Node";
   static final String PORTAL_VIEWS     = "PortalViews";
   static final String CURRENT_VIEW     = PORTAL_VIEWS  + "/Current" + PAGE_NODE;
   public static final String AVAILABLE_VIEWS  = PORTAL_VIEWS  + "/Available";
   static final String PORTAL_DESC      = "Description";
   static final String PORTAL_POSITION  = "Position";
   public static final String DELETED_VIEWS  = PORTAL_VIEWS + "/DeletedFromBase";
   static final String IS_OWN         = "IsOwn";

   //==========================================================================
   // Instance variables
   //==========================================================================

   // immutable
   private ASPContext ctx;
   private ASPConfig  cfg;
   private String     imgloc;
   private String     imgLocRTL;
   private int[]      refresh_times;
   private int        min_refresh_time;
   private String portlet_names="";
   private Vector ds = new Vector();

   // images
   private String upd_portlet;
   private String upd_portlet_hov;
   private String cust_portlet;
   private String cust_portlet_hov;
   private String portlet_help;
   private String portlet_help_hov;
   private String zoom_in_portlet;
   private String zoom_in_portlet_hov;
   private String remove_portlet;
   private String remove_portlet_hov;

   // temporary
   private AutoString out = new AutoString();

   private transient int                runmode;
   private transient String             portletid;
   private transient String             minboxes;
   private transient String             custcols;
   private transient boolean            user_profile_prepared;
   private transient ASPPortletProvider portlet;
   private transient ASPPortalColumn    slimcol;
   private transient boolean            is_explorer;
   private transient boolean            is_netscape6_above;
   private transient Buffer             user_portlets;
   private transient String             view_name;
   private transient String             view_desc;
   private transient boolean            view_import;
   private transient Hashtable          global_variables;
   private transient boolean            slim_mode;
   private transient boolean            is_copy;

   // temporary variables for using with command bar function
   private transient String             cmdblkname;
   private transient String             cmdstdid;
   private transient String             cmdcustid;

   // mutable
   private transient ASPPortalProfile profile;     private transient ASPPortalProfile pre_profile;
   private transient boolean portlet_conf_dis;     private transient boolean pre_portlet_conf_dis;
   //Bug 40931, start
   private transient Buffer portlet_session_handlers;
   //Bug 40931, end

   private transient boolean std_portlet;
   private transient String  single_portlet_name;
   private transient int  std_portlet_state;

   private transient String portlet_usagekey;

   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Package constructor
    */
   ASPPortal( ASPPage page )
   {
      super(page);
      if(DEBUG) debug(this+": ASPPortal.<init>: "+page);

      ctx = page.getASPContext();
      cfg = page.getASPConfig();
   }


   /**
    * Package construct() called by definePortal() function within ASPPage.
    * Do not runs on cloning.
    */
   ASPPortal construct() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.construct()");

      imgloc = cfg.getImagesLocation();
      imgLocRTL = cfg.getImagesLocationWithRTL();
      //$PORTAL=!
      //-$AUTO_REFRESH_TIMES=5,10,15
      String reftimes = cfg.getParameter("PORTAL/AUTO_REFRESH_TIMES","");

      if(DEBUG) debug("  construct(): reftimes="+reftimes);

      if(!Str.isEmpty(reftimes))
      {
         refresh_times = new int[16];
         min_refresh_time = 1000;
         int cnt = 0;
         StringTokenizer pst = new StringTokenizer(reftimes, ",");
         String token = null;
         while( pst.hasMoreTokens() )
         {
            try
            {
               if(cnt==refresh_times.length)
               {
                  int[] tmp = new int[2*cnt];
                  System.arraycopy(refresh_times,0,tmp,0,refresh_times.length);
                  refresh_times = tmp;
               }
               token = pst.nextToken();
               int t = Integer.parseInt(token);
               if(t<1) continue;
               if(t<min_refresh_time) min_refresh_time = t;
               refresh_times[cnt] = t;
               cnt++;
            }
            catch(NumberFormatException x)
            {
               if(DEBUG) debug("  Parsing error: token="+token);
               refresh_times = null;
               break;
            }
         }
         if( refresh_times!=null && cnt<refresh_times.length)
         {
            int[] tmp = new int[cnt];
            System.arraycopy(refresh_times,0,tmp,0,cnt);
            refresh_times = tmp;
         }
      }

      upd_portlet      = cfg.getParameter("PAGE/TOOLBAR/UPDATE_PORTLET/NORMAL","upd_portlet.gif");
      upd_portlet_hov  = cfg.getParameter("PAGE/TOOLBAR/UPDATE_PORTLET/HOVER","upd_portlet_hov.gif");
      zoom_in_portlet      = cfg.getParameter("PAGE/TOOLBAR/ZOOM_IN_PORTLET/NORMAL","zoom_in_portlet.gif");
      zoom_in_portlet_hov  = cfg.getParameter("PAGE/TOOLBAR/ZOOM_IN_PORTLET/HOVER","zoom_in_portlet_hov.gif");
      cust_portlet     = cfg.getParameter("PAGE/TOOLBAR/CUSTOMIZE_PORTLET/NORMAL","cust_portlet.gif");
      cust_portlet_hov = cfg.getParameter("PAGE/TOOLBAR/CUSTOMIZE_PORTLET/HOVER","cust_portlet_hov.gif");
      portlet_help     = cfg.getParameter("PAGE/TOOLBAR/PORTLET_HELP/NORMAL","portlet_help.gif");
      portlet_help_hov = cfg.getParameter("PAGE/TOOLBAR/PORTLET_HELP/HOVER","portlet_help_hov.gif");
      remove_portlet   = cfg.getParameter("PAGE/TOOLBAR/PORTLET_REMOVE/NORMAL","remove_portlet.gif");
      remove_portlet_hov   = cfg.getParameter("PAGE/TOOLBAR/PORTLET_REMOVE/HOVER","remove_portlet_hov.gif");

      doActivate();
      return this;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.doActivate()");

      ASPManager mgr = getASPManager();

      String mode = mgr.getQueryStringValue("CUSTOMIZE");
      if( !Str.isEmpty(mode) && mode.toUpperCase().startsWith("Y") )
      {
         if( mgr.getPortalPage().isConfigurationDisabled() || !cfg.isPortalConfigEnabled())
            throw new FndException("FNDPORCONFDIS: User is not allowed to run the configuration page!");
         else
            runmode = CUSTOM_MODE;
      }
      else
      {
         mode = mgr.readValue("__PORTAL_MODE");
         if( "C".equals(mode) )
         {
            //if( mgr.getPortalPage().isConfigurationDisabled() || !cfg.isPortalConfigEnabled())
            //   throw new FndException("FNDPORCONFDIS: User is not allowed to run the configuration page!");
            runmode = CUSTOM_MODE;
         }
         else if( "S".equals(mode) )
         {
            runmode = SUBMIT_MODE;
            custcols = mgr.readValue("__PORTAL_COLUMNS");
         }
         else if( "R".equals(mode) )
            runmode = REMOVE_MODE;
         else
            runmode = NORMAL_MODE;
      }

      if( !parseCommandBarFunction() )
         portletid = mgr.readValue("__PORTLET_ID");

      minboxes    = ctx.getCookie(MIN_BOXES_COOKIE);
      portlet     = null;
      slimcol     = null;
      slim_mode   = false;
      std_portlet = mgr.isStdPortlet();
      single_portlet_name = null;
      std_portlet_state = 0;
      portlet_usagekey  = null;
      is_copy     = false;
      is_explorer = mgr.isExplorer();
      is_netscape6_above = mgr.isMozilla();

      if(DEBUG) debug("  doActivate():"+
                      "\n\t  runmode   = "+(runmode==NORMAL_MODE ? "NORMAL_MODE" :
                                           (runmode==SUBMIT_MODE ? "SUBMIT_MODE" :
                                           (runmode==REMOVE_MODE ? "REMOVE_MODE" :
                                           (runmode==CUSTOM_MODE ? "CUSTOM_MODE" : "UNKNOWN_MODE"))))+
                      "\n\t  portletid = "+portletid+
                      "\n\t  std_portlet = "+std_portlet+
                      "\n\t  single_portlet_name = "+single_portlet_name+
                      "\n\t  portlet_usagekey = "+portlet_usagekey+
                      "\n\t  minboxes  = "+minboxes+
                      "\n\t  custcols  = "+custcols);

      // eventuellt: ??
      //if( !isUndefined() )
      //   prepareProfileInfo();
   }


   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.doFreeze()");

      if(!slim_mode)
         createBaseProfile();
   }


   void forceDirty() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.forceDirty()");
      if(profile!=null)
         profile.forceDirty(this);
      modifyingMutableAttribute("FORCE_DIRTY");
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset()
   {
      if(DEBUG) debug(this+": ASPPortal.doReset()");

      //Bug 40931, start
      HttpSession session = getASPManager().getAspSession();
      session.setAttribute(PORTLET_SESSION_HANDLERS, portlet_session_handlers);

      //if(profile!=null) profile.reset( getASPManager() );
      if(profile!=null) profile.reset( this, getASPManager(), getASPPage() );
      //Bug 40931, end

      portlet_names = "";
      out.clear();
      runmode       = NORMAL_MODE;
      portletid     = null;
      minboxes      = null;
      custcols      = null;
      portlet       = null;
      slimcol       = null;
      is_explorer   = true;
      is_netscape6_above = true;
      user_portlets = null;
      view_name     = null;
      view_desc     = null;
      cmdblkname    = null;
      cmdstdid      = null;
      cmdcustid     = null;
      slim_mode     = false;
      std_portlet   = false;
      single_portlet_name = null;
      portlet_usagekey    = null;
      global_variables = null;
      is_copy       = false;
      user_profile_prepared = false;

      checkProfile(); //profile is set here; check if reset before new assignment
      profile = pre_profile;

      //Bug 40931, start
      portlet_session_handlers = null;
      //Bug 40931, end
   }

   /**
    * Check if all portlets have been reset before assigning a new profile instance.
    */
   private void checkProfile()
   {
      if( profile!=null && !profile.isReset(this) )
         throw new RuntimeException("FNDPORPRFASS: The profile has not been reset before a new assignment!");
   }

   /**
    * Clone this portal into a new portal included in the specified page.
    * The state of the new portal is DEFINED.
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.clone("+page+")");

      ASPPortal p = new ASPPortal((ASPPage)page);

      p.runmode       = NORMAL_MODE;
      p.portletid     = null;
      p.minboxes      = null;
      p.custcols      = null;
      p.portlet       = null;
      p.slimcol       = null;
      p.slim_mode     = false;
      p.std_portlet   = false;
      p.single_portlet_name = null;
      p.portlet_usagekey    = null;
      p.is_explorer   = true;
      p.imgloc        = imgloc;
      p.imgLocRTL     = imgLocRTL;
      p.cmdblkname    = null;
      p.cmdstdid      = null;
      p.cmdcustid     = null;
      p.is_copy       = false;
      p.refresh_times = refresh_times;
      p.min_refresh_time = min_refresh_time;
      //Bug 40931, start
      p.portlet_session_handlers = null;
      //Bug 40931, start

      p.checkProfile(); //profile is set here; check if reset before new assignment
      p.profile = p.pre_profile = (ASPPortalProfile)pre_profile.clone();

      p.upd_portlet      = upd_portlet;
      p.upd_portlet_hov  = upd_portlet_hov;
      p.cust_portlet     = cust_portlet;
      p.cust_portlet_hov = cust_portlet_hov;
      p.portlet_help     = portlet_help;
      p.portlet_help_hov = portlet_help_hov;
      p.zoom_in_portlet     = zoom_in_portlet;
      p.zoom_in_portlet_hov = zoom_in_portlet_hov;
      p.remove_portlet      = remove_portlet;
      p.remove_portlet_hov  = remove_portlet_hov;

      p.is_netscape6_above = is_netscape6_above;

      p.setCloned();
      return p;
   }

   /**
    * Create and return new instance of corresponding profile class.
    * Return null if the class doesn't support profile handling.
    */
   protected ASPPoolElementProfile newProfile()
   {
      return new ASPPortalProfile();
   }


   protected void verify( ASPPage page ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.verify("+page+")");

      this.verifyPage(page);
      // loop for all child.verifyPage(page);
   }


   protected void scan( ASPPage page, int level ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.scan("+page+","+level+")");

      scanAction(page,level);
      // loop for all child.scan(page,level+1);
      if( !page.isVerifying() )
      {
         if(pre_profile!=null)
            page.appendContents(pre_profile.showContents(),level+1);
         if( profile!=null && profile!=pre_profile )
            page.appendContents(profile.showContents(),level+1);

         if(portlet!=null)
            portlet.scan(page,level+1);
      }
   }


   private void setViewName() throws FndException
   {
      if(DEBUG) debug(this+": setViewName()");
      ASPPage page = getASPPage();
      view_name    = getASPManager().getQueryStringValue("__VIEW_NAME");
      view_desc    = getASPManager().getQueryStringValue("__IMPORT");
      view_import  = false;
      String user_id = getASPManager().getFndUser();

      if(Str.isEmpty(view_name))
      {
         view_name = page.readGlobalProfileValue(CURRENT_VIEW,false);
         view_desc = page.readGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC,false);

         if(view_name==null)
         {
            view_name = std_portlet ? "STDPORTLET" : "PORTAL";
            prepareProfileInfo();
            ASPProfile prf = page.getASPProfile();
            prf.remove(this);
            view_desc = getASPManager().translate("IFSFNDSCRIPTSDEFAULTTITLE2: IFS Personal Portal");
            view_name = view_desc.toUpperCase().replace(' ','_');
            prf.update(this,profile);

            page.writeGlobalProfileValue(CURRENT_VIEW, view_name,false);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc,false);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+IS_OWN, "TRUE",false);

            //TODO: what is the following for?
            ASPBuffer links = page.readGlobalProfileBuffer("Links",false);
            if(links!=null)
            {
               page.removeGlobalProfileItem("Links");
               page.writeGlobalProfileBuffer(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/Links", links, false);
            }
         }
      }
      // RIRALK: old functionality, should be removed
      /*else if (view_desc != null)
      {
         try
         {
            importProfile(PERFORM);
         }
         catch(IOException x)
         {
            throw new FndException(x);
         }
      }*/
      else
      {
         if(view_name.equals("_REMOVE"))
         {
            //view_name = page.readGlobalProfileValue(CURRENT_VIEW,false);
            view_name = getASPManager().getQueryStringValue("_CURRENT_VIEW");
            view_desc = page.readGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC,false);
            page.writeGlobalProfileValueForDelete(DELETED_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC,view_desc,AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE);
            page.removeGlobalProfileItem(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE);
            removeProfile();
            boolean last_portal_removed = false;
            //ASPBuffer aspbuf= page.readGlobalProfileBuffer(AVAILABLE_VIEWS,false);
            Buffer buf= page.getPortalViews();
            if(buf!=null && buf.countItems()>0)
            {               
               view_name = buf.getItem(0).getName();
               view_desc = buf.getItem(0).getString();               
               //reassingn values starting from 0 for each view to ensure correct order
               for (int i=0; i<buf.countItems(); i++)
               {                   
                 String view_ = buf.getItem(i).getName();  
                 page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_+PAGE_NODE+"/"+PORTAL_POSITION, ""+i,false);
               }
            }
            else
            {
               view_desc = getASPManager().translateJavaText("IFSFNDSCRIPTSDEFAULTTITLE2: IFS Personal Portal");
               view_name = view_desc.toUpperCase().replace(' ','_')+"_"+ user_id;
               ASPBuffer delbuf = page.readGlobalProfileBuffer(ASPPortal.DELETED_VIEWS,false);
               if(delbuf != null)
               {
                  int pos = delbuf.getItemPosition(view_name);
                  if(pos >= 0)
                  {
                     view_desc = getASPManager().translateJavaText("IFSFNDSCRIPTSDEFAULTTITLE2: IFS Personal Portal");
                     view_name = view_desc.toUpperCase().replace(' ','_')+"_"+ user_id+"_1";
                  }
               }
               last_portal_removed = true;
            }
            page.writeGlobalProfileValue(CURRENT_VIEW, view_name,false);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc,false);
            if (last_portal_removed){
               page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_POSITION, ""+0,false); 
               page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+IS_OWN, "TRUE",false);
            }

         }
         else if(view_name.equals("_COPY"))
         {
            page.writeGlobalProfileValue(CURRENT_VIEW, getASPManager().getQueryStringValue("_COPY_FROM"),false);

            view_name = page.readGlobalProfileValue(CURRENT_VIEW,false);
            view_desc = getASPManager().getQueryStringValue("_NEW_VIEW_NAME");

            is_copy = true;
            prepareProfileInfo();
            view_name = view_desc.toUpperCase().replace(' ','_');
            view_name = view_name.replace('/','_');
            int i=0;
            while( page.readGlobalProfileBuffer(AVAILABLE_VIEWS,false).itemExists(view_name+PAGE_NODE) )
            {
               i++;
               view_name = view_desc.toUpperCase().replace(' ','_')+"("+i+")";
            }
            if( i>0 ) view_desc += "("+i+")";
            saveProfile();
            page.writeGlobalProfileValue(CURRENT_VIEW, view_name,false);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc, false);
            ASPBuffer aspbuf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS,false);
            if (aspbuf!=null)
            {              
              int pos = aspbuf.countItems()-1; //positions start with 0
              page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_POSITION, ""+pos,false); 
            }                
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+IS_OWN, "TRUE",false);
         }
         else if(view_name.equals("_NEW"))
         {
            view_desc = getASPManager().getQueryStringValue("_NEW_VIEW_NAME");
            view_name = view_desc.toUpperCase().replace(' ','_');
            view_name = view_name.replace('/','_');
            int i = 0;
            while( page.readGlobalProfileBuffer(AVAILABLE_VIEWS,false).itemExists(view_name+PAGE_NODE) )
            {
               i++;
               view_name = view_desc.toUpperCase().replace(' ','_')+i;
            }
            if( i>0 ) view_desc += i;
            page.writeGlobalProfileValue(CURRENT_VIEW, view_name, false);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc, false);
            ASPBuffer aspbuf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS,false);
            if (aspbuf!=null)
            {              
              int pos = aspbuf.countItems()-1; //positions start with 0
              page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_POSITION, ""+pos,false); 
            }
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+IS_OWN, "TRUE",false);
         }
         else if(view_name.equals("_EXPORT")) //to xml
         {
            view_name = page.readGlobalProfileValue(CURRENT_VIEW,false);
            view_desc = page.readGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC,false);
            try
            {
               exportToXML();
            }
            catch(Exception x)
            {
               throw new FndException(x);
            }
         }
         else if(view_name.equals("_IMPORT")) //from xml
         {
            view_desc = getASPManager().getQueryStringValue("_NEW_VIEW_NAME");
            view_name = view_desc.toUpperCase().replace(' ','_');
            view_name = view_name.replace('/','_');

            String filename = getASPManager().getASPConfig().getParameter("ADMIN/FILE_UPLOAD/TARGET","../server/uploaded_files");
            filename += File.separator + getASPManager().getQueryStringValue("UPLOAD_FILENAME");
            ASPBuffer import_buffer = getASPManager().newASPBuffer();

            try
            {
               Buffer tempoldbuf = getASPManager().newASPBuffer().getBuffer();
               XMLUtil.saveToBuffer(filename, tempoldbuf, "portal_profile");
               ProfileBuffer oldbuf = ProfileBuffer.bufferToProfileBuffer(tempoldbuf); //Bug 59518 fix.

               //deleted uploaded file
               getASPManager().deleteTempProfileFile(filename);

               checkProfile(); //profile is set here; check if reset before new assignment
               profile = new ASPPortalProfile();

               //ProfileBuffer oldbuf = ProfileBuffer.bufferToProfileBuffer(buff);
               ProfileBuffer newbuf = ProfileUtils.newProfileBuffer();
               //change imported buffer to correct profile format to save in DB
               String minportlets = oldbuf.getString("MINIMIZEDPORTLETS",null);
               int nextid = oldbuf.getInt("NEXTID");
               int refresh_time = oldbuf.getInt("REFRESHTIME",0);
               int wide_column  = oldbuf.getInt("WIDECOLUMNEXISTS" ,0);

               ProfileUtils.findOrCreateNestedItem(newbuf,ASPPortalProfile.MIN_PORTLETS).setValue(minportlets);
               ProfileUtils.findOrCreateNestedItem(newbuf,ASPPortalProfile.NEXT_ID).setValue(nextid);
               ProfileUtils.findOrCreateNestedItem(newbuf,ASPPortalProfile.REFRESH_TIME).setValue(refresh_time);
               ProfileUtils.findOrCreateNestedItem(newbuf,ASPPortalProfile.WIDE_COL_EXISTS).setValue(wide_column);

               int buf_count = oldbuf.countItems();
               for(int c=0; c<buf_count; c++)
               {
                 Item i = oldbuf.getItem(c);
                 if( i!=null && i.isCompound() && i.getName().startsWith("COLUMN_"))
                 {
                    String col_id =  i.getName().substring(i.getName().indexOf("_")+1);
                    Buffer colbuf = ProfileUtils.findOrCreateNestedBuffer(newbuf,ASPPortalProfile.COLUMN+col_id);

                    Buffer srcbuf = i.getBuffer();
                    for (int j=0; j<srcbuf.countItems(); j++)
                    {
                        Item it = srcbuf.getItem(j);
                        String name = it.getName();
                        if (name.equalsIgnoreCase("PORTLET"))
                        {
                           Buffer port_buf = ProfileUtils.findOrCreateNestedBuffer(colbuf,ASPPortalProfile.PORTLET_DEF+it.getBuffer().getString("ID"));
                           ProfileUtils.findOrCreateNestedItem(port_buf,ASPPortalProfile.PORTLET_POS_CAP,ASPPortalProfile.PORTLET_POS).setValue(it.getBuffer().getString("POSITION"));
                           ProfileUtils.findOrCreateNestedItem(port_buf,ASPPortalProfile.PORTLET_CLS_CAP,ASPPortalProfile.PORTLET_CLS).setValue(it.getBuffer().getString("CLASSNAME"));
                           if(it.isCompound()){  //Bug 59518 fix.
                              try{
                                 Item bf = it.getBuffer().getItem("PROFILEBUFFER");
                                 if(bf!=null){
                                    ProfileUtils.findOrCreateNestedItem(port_buf,ASPPortalProfile.PORTLET_PRF_CAP,ASPPortalProfile.PORTLET_PRF).setValue(importProfileBuffer(bf.getBuffer()));
                                 }   
                              }catch (Exception bufex){
                                 continue;
                              }
                           }
                        }
                        else if (name.equalsIgnoreCase("DEFINITION"))
                        {

                           Buffer def_buf = ProfileUtils.findOrCreateNestedBuffer(colbuf,ASPPortalProfile.COLUMN_DEF);
                           ProfileUtils.findOrCreateNestedItem(def_buf,ASPPortalProfile.COLUMN_WIDTH_CAP,ASPPortalProfile.COLUMN_WIDTH).setValue(it.getBuffer().getInt("WIDTH"));
                           ProfileUtils.findOrCreateNestedItem(def_buf,ASPPortalProfile.COLUMN_APPEAR_CAP,ASPPortalProfile.COLUMN_APPEAR).setValue(it.getBuffer().getString("APPEARANCE"));

                        }
                    }
                 }
               }

               profile.assign(this, newbuf);
               saveProfile();

               page.writeGlobalProfileValue(CURRENT_VIEW, view_name, false);
               page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc, false);
               ASPBuffer aspbuf = page.readGlobalProfileBuffer(ASPPortal.AVAILABLE_VIEWS,false);
               if (aspbuf!=null)
               {              
                 int pos = aspbuf.countItems()-1; //positions start with 0
                 page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_POSITION, ""+pos,false); 
               }
            }
            catch (Exception e)
            {
               throw new FndException(e);
            }
         }
         else
         {
            view_desc = page.readGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC,false);
            if(view_desc==null)
               throw new FndException("FNDPORVIEWNOTEXISTS: Portal view '&1' does not exists",view_name);
            page.writeGlobalProfileValue(CURRENT_VIEW, view_name, false);
         }
      }
   }

   /**
   * Returns a given buffer with all its contents restructured by 
   * removing the extra ProfileBuffer element to match a proper ProfileBuffer
   * Bug 59518 fix.
   */
   private Buffer importProfileBuffer(Buffer buffer) throws FndException
   {
      Buffer profbuf = getASPManager().newASPBuffer().getBuffer();
      for(int i=0; i<buffer.countItems(); i++){
         Item temp_item = buffer.getItem(i);
         if(!temp_item.isCompound())
            profbuf.addItem(temp_item.getName(), temp_item.getValue());
         else if(temp_item.isCompound() && "PROFILEBUFFER".equalsIgnoreCase(temp_item.getName()))
            profbuf = importProfileBuffer(temp_item.getBuffer());
         else
            profbuf.addItem(temp_item.getName(), importProfileBuffer(temp_item.getBuffer()));
      }
      return (Buffer) ProfileBuffer.bufferToProfileBuffer(profbuf);
   }
   
   
   /**
    * Returns the name of the portal view.
    */
   public String getName()
   {
      return view_name;
   }

   /**
    * Returns the description of the portal view.
    */
   public String getDescription()
   {
      return view_desc;
   }

   private void readGlobalVariables()
   {
      if(DEBUG) debug(this+" readGlobalVariables");
      ASPManager mgr = getASPManager();
      String globals = mgr.getQueryStringValue("GLOBAL_VARIABLE");
      global_variables = new Hashtable();
      if(globals!=null)
      {
         StringTokenizer st = new StringTokenizer(globals,",");
         while( st.hasMoreTokens() )
         {
            String variable = st.nextToken();
            String value    = mgr.getQueryStringValue(variable);
            global_variables.put(variable,value);
         }
      }
      else if(slim_mode)
      {
         String params = mgr.getQueryString();
         StringTokenizer st = new StringTokenizer(params,"&");
         while( st.hasMoreTokens() )
         {
            String str = st.nextToken();
            int i = str.indexOf("=");
            String variable = str.substring(0,i);
            String value    = mgr.URLDecode(str.substring(i+1));
            global_variables.put(variable,value);
         }
      }
   }

   
   /**
    * Returns the value of the global variable.
    * @param name Name of the global variable
    *
    * @see #setGlobalVariable
    * @see #removeGlobalVariable
    * @see #isGlobalVariableDefined
    */
   public String getGlobalVariable( String name )
   {
      if(DEBUG) debug(this+" getGlobalVariable");
      return global_variables!=null ? (String)global_variables.get(name) : null;
   }

   Hashtable getGlobalVariables()
   {
      return global_variables;
   }

   /**
    * Set a value to a global variable.
    * @param variable Name of the global variable
    * @param value Value wants to set
    *
    * @see #getGlobalVariable
    * @see #removeGlobalVariable
    * @see #isGlobalVariableDefined
    */
   public void setGlobalVariable( String variable, String value )
   {
      global_variables.put(variable,value);
   }

   /**
    * Removes a global variable.
    * @param variable Name of the global variable
    *
    * @see #setGlobalVariable
    * @see #getGlobalVariable
    * @see #isGlobalVariableDefined
    */
   public void removeGlobalVariable( String variable )
   {
      global_variables.remove(variable);
   }

   /**
    * Returns true if the global variable is defined.
    * @param variable Name of the global variable
    *
    * @see #setGlobalVariable
    * @see #getGlobalVariable
    * @see #removeGlobalVariable
    */
   public boolean isGlobalVariableDefined( String variable )
   {
      return global_variables.containsKey(variable);
   }

   String parseGlobalVariables()
   {
      if(global_variables.isEmpty()) return "";

      ASPManager mgr = getASPManager();
      String global = "&GLOBAL_VARIABLE=";
      String value  = "";
      String portlet = "";
      if( slim_mode )
      {
         portlet = "?PORTLET="+mgr.URLEncode((String)global_variables.get("PORTLET"));
         global_variables.remove("PORTLET");
      }

      Enumeration variables = global_variables.keys();
      while( variables.hasMoreElements() )
      {
         String variable = (String)variables.nextElement();
         global += variable+",";
         value  += "&"+variable+"="+mgr.URLEncode((String)global_variables.get(variable));
      }
      if( !slim_mode )
         return "?__VIEW_NAME="+mgr.URLEncode(view_name)+global+value;
      else
         return portlet+value;
   }

   /**
    * Returns the string representation of the portal. (Can use for the debugging purposes.)
    */
   public String toString()
   {
      return super.toString() + " Profiles: "
             + (profile==null ? "null" : Integer.toHexString(profile.hashCode()) ) +","
             + (pre_profile==null ? "null" : Integer.toHexString(pre_profile.hashCode()) )
             + " Mode: "+(runmode==CUSTOM_MODE?"C":(runmode==SUBMIT_MODE?"S":(runmode==REMOVE_MODE?"R":"N")))
             + "["+portletid+"]";
   }

   //=============================================================================
   //  Profile
   //=============================================================================

   //Bug 40931, start
   private void initPortletSession()
   {
      if(portlet_session_handlers==null)
      {
         if(!std_portlet)
         {
            HttpSession session = getASPManager().getAspSession();
            portlet_session_handlers = (Buffer)session.getAttribute(PORTLET_SESSION_HANDLERS);
         }
         if(portlet_session_handlers==null)
            portlet_session_handlers = getASPPage().getASPConfig().getFactory().getBuffer();
      }
   }

   /**
    * Return an instance of ASPPortletSessionHandle class that encapsulates all
    * request and session related data for a given ASPPortletHandle.
    * The data is stored in the session object.
    *
    * @see ifs.fnd.asp.ASPPortletSessionHandle
    */
   ASPPortletSessionHandle getPortletSessionHandle( ASPPortletHandle ph )
   {
      initPortletSession();

      String id = ph.getId();
      //ASPPortletSessionHandle psh = (ASPPortletSessionHandle)(portlet_session_handlers.get(id));
      ASPPortletSessionHandle psh = (ASPPortletSessionHandle)(portlet_session_handlers.getObject(id,null));
      if(psh==null)
      {
         psh = new ASPPortletSessionHandle(ph,null);
         //portlet_session_handlers.put(id,psh);
         portlet_session_handlers.addItem(id,psh);
      }
      return psh;
   }
   //Bug 40931, end

   private void createBaseProfile() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.createBaseProfile()");

      if(profile!=null) throw new FndException("FNDPORPRFERR: Profile already defined!");
      profile = new ASPPortalProfile();
      profile.construct(this);
      user_profile_prepared = false;
      pre_profile = profile;
      if(DEBUG) debug("  createBaseProfile(): profile = pre_profile["+pre_profile+"]");
   }


   private void prepareProfileInfo()
   {
      if(DEBUG) debug(this+": ASPPortal.prepareProfileInfo()");
      if( user_profile_prepared ) return;

      try
      {
         ASPPage page = getASPPage();
         if( pre_profile==null ) createBaseProfile();

         ASPProfile aspprf = page.getASPProfile();
         if(DEBUG)
         {
            debug("  aspprf = "+aspprf);
            if(profile!=null && profile!=pre_profile)
               debug("    WARNING: profile ["+profile+"] already exists!");
         }
         //Bug 40931, start
         //if(profile!=null) profile.reset(getASPManager());
         if(profile!=null) profile.reset(this, getASPManager(), getASPPage());
         //Bug 40931, end
       //  profile = (ASPPortalProfile)aspprf.get(this,pre_profile);
         if(is_copy)
         {
            checkProfile(); //profile is set here; check if reset before new assignment
            profile = pre_profile.getNewProfileInstance((ASPPortalProfile)aspprf.get(this,pre_profile));
            //is_copy = false;
         }
         else
         {
            checkProfile(); //profile is set here; check if reset before new assignment
            profile = (ASPPortalProfile)aspprf.get(this,pre_profile);
            //Commented temporally for Build6-patch4
            //profile = (ASPPortalProfile)(aspprf.get(this,pre_profile).clone());
         }

        if(DEBUG){
             AutoString out = new AutoString();
             profile.showContents(out);
             debug("prepareProfileInfo():"+
                         "\n\t\t  pre_profile["+pre_profile+"]"+
                         "\n\t\t  profile["+profile+"]="+out.toString());
         }
        //Uncommented code temporally for build6-patch4
         if(runmode==SUBMIT_MODE && profile==pre_profile)
         {
            if(DEBUG) debug(this+": cloning default profile: "+pre_profile);
            checkProfile(); //profile is set here; check if reset before new assignment
            profile = (ASPPortalProfile)pre_profile.clone();
         }

         modifyingMutableAttribute("PROFILE");

         ASPPortletHandle ph = null;
         if( !Str.isEmpty(portletid) && !"*".equals(portletid) )
            ph = profile.findPortletHandle(this,portletid);

         if( runmode==REMOVE_MODE && ph!=null )
         {
            if(DEBUG) debug("  removing portlet profile");
            ph.removeProfile();
            saveProfile();
         }

         user_portlets = profile.getUserPortlets();
         if(DEBUG) debug("  user_portlets [prepareProfileInfo]:\n"+Buffers.listToString(user_portlets));
         if(user_portlets==null)
            checkPortletsSecurity();
         if(profile!=pre_profile)
            profile.setUserPortlets(user_portlets);

         //profile.activate( ph==null && runmode!=NORMAL_MODE ? null : this, portletid, user_portlets );


         if( runmode==REMOVE_MODE && ph==null )
            //Bug 40931, start
            //profile.activate(this,true,null,null); // Q&D solution for REMOVE_MODE
            profile.activate(this,true,null,null,getASPPage()); // Q&D solution for REMOVE_MODE
           //Bug 40931, end
         else if(is_copy)
         {
            //Bug 40931, start
            //profile.activate(this,false,null,null);
            profile.activate(this,false,null,null,getASPPage());
            //Bug 40931, end
            is_copy = false;
         }
         else
            //Bug 40931, start
            //profile.activate( this, !(ph==null && runmode!=NORMAL_MODE), portletid, user_portlets );
            profile.activate( this, !(ph==null && runmode!=NORMAL_MODE), portletid, user_portlets, getASPPage() );
            //Bug 40931, end

         //Bug 40931, start
         if( ph!=null && ph.isUserAvailable(this) )
            portlet = ph.getPortlet(this);
         //Bug 40931, end

         if( Str.isEmpty(minboxes) )
            minboxes = profile.getMinimizedBoxes();
         else if(profile!=pre_profile)
            profile.setMinimizedBoxes(minboxes); //will not be saved to DB until the first customization
         if( !Str.isEmpty(minboxes) )
            //ctx.setCookie(MIN_BOXES_COOKIE, minboxes, page.getApplicationPath()+(is_explorer?"/":""), new Date(2145916800000L) );
            ctx.setPersistentTransientCookie(MIN_BOXES_COOKIE, minboxes);
         user_profile_prepared = true;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Store the profile information in the database.
    *
    * @see ifs.fnd.asp.ASPPortal.getProfile
    */
   private void saveProfile()
   {
      if(DEBUG) debug(this+": ASPPortal.saveProfile()");
      try
      {
         modifyingMutableAttribute("PROFILE");
         user_profile_prepared = false;

         ASPPage page = getASPPage();
         ASPProfile prf = page.getASPProfile();
         if(DEBUG) debug("  profile="+prf);
         prf.update(this,profile);
         //prf.save(page);
         prf.save(this);
      }
      catch( Throwable any )
      {
         error(any);
      }

   }


   /**
    * Remove profile information from database for this ASPPortal.
    *
    * @see ifs.fnd.asp.ASPPortal.saveProfile
    */
   private void removeProfile()
   {
      if(DEBUG) debug(this+": ASPPortal.removeProfile()");
      try
      {
         ASPPage    page = getASPPage();
         ASPProfile prf  = page.getASPProfile();
         //Bug 40931, start
         //if(profile!=null && profile!=pre_profile) profile.reset(getASPManager());
         if(profile!=null && profile!=pre_profile) profile.reset(this, getASPManager(), getASPPage());
         //Bug 40931, end
         if (view_name==null)
            view_name = page.readGlobalProfileValue(CURRENT_VIEW,false);
         prf.remove(this, true);
         //prf.save(page);
         prf.save(this);
         checkProfile(); //profile is set here; check if reset before new assignment
         profile = pre_profile;
         user_profile_prepared = false;
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Export portal view profile information to database.
    */

   //TODO RIRALK: Remove this functions, it is private and NOT used from this file, old left over code
   /*private void exportProfile() throws FndException, IOException
   {
      ASPPage         page    = getASPPage();
      Factory         factory = page.getASPConfig().getFactory();
      Buffer          buf     = factory.getBuffer();
      BufferFormatter frmt    = factory.getBufferFormatter();


      ASPPortalProfile port_prf =  (ASPPortalProfile)page.getASPProfile().get(this,null);
      if (port_prf==null)
      {
         saveProfile();
         port_prf =  (ASPPortalProfile)page.getASPProfile().get(this,null);
      }
      String prf = port_prf.format(this);
      //TODO: page.getASPProfile().addProfile(buf,getClass().getName(),getName(),prf,false);

      ASPManager      mgr    = page.getASPManager();
      ASPCommand      cmd    = (new ASPCommand(mgr)).construct();

      cmd.defineCustom("Web_Profile_API.Save_Current_Profile");
      cmd.addParameter("URL",     "S", null, (page.getPagePath().toLowerCase()+SEP+getDescription()));
      cmd.addParameter("PROFILE", "S", null, Util.compress(frmt.format(buf)));

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      trans.addCommand("CURRENT", cmd);
      mgr.perform(trans);
   }*/

   /**
    * Export portal view profile information to XML.
    */
   private void exportToXML() throws FndException
   {
      ASPPage         page    = getASPPage();
      Factory         factory = page.getASPConfig().getFactory();
      Buffer          buf     = factory.getBuffer();
      BufferFormatter frmt    = factory.getBufferFormatter();


      ASPPortalProfile port_prf =  (ASPPortalProfile)page.getASPProfile().get(this,null);
      if (port_prf==null)
      {
         saveProfile();
         port_prf =  (ASPPortalProfile)page.getASPProfile().get(this,null);
      }

      //String prf = port_prf.format(this);
      //page.getASPProfile().addProfile(buf,getClass().getName(),getName(),prf,false);

      ASPManager mgr = page.getASPManager();

 //     if (mgr.isExplorer())
 //        mgr.setAspResponsContentType("application/xml");
      if (mgr.isNetscape4x())
         mgr.setAspResponsContentType("text/plain");
      else
      {
         mgr.setAspResponsContentType("text/xml");
         mgr.setResponseContentFileName(view_name+".xml",true); //'true' to force download as attch.
      }

      mgr.responseWrite(xmlContents(port_prf).getBytes());
      mgr.endResponse();

   }


   private String xmlContents( ASPPortalProfile port_prf )
   {
      AutoString out = new AutoString();

      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      out.append("<portal_profile>\n");
      out.append("<MinimizedPortlets>",port_prf.getMinimizedBoxes());
      out.append("</MinimizedPortlets>\n");
      out.append("<NextId>"+port_prf.getNextId(),"</NextId>\n");
      out.append("<RefreshTime>"+port_prf.getRefreshTime(),"</RefreshTime>\n");
      out.append("<WideColumnExists>"+port_prf.getWideColumn(),"</WideColumnExists>\n");

      int colsize = port_prf.countColumns();

      for(int c=0; c<colsize; c++)
      {
         ASPPortalColumn col = port_prf.getColumn(c);

         out.append("<Column_"+c,">\n");
         out.append("<Definition>\n");
         out.append("<Width>"+col.getWidth()+"</Width>\n");
         out.append("<Appearance>",(col.isDynamic()?"D":"S"),"</Appearance>\n");
         out.append("</Definition>\n");
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            out.append("<Portlet>\n");
            out.append("<Id>"+ph.getId()+"</Id>\n");
            out.append("<ClassName>"+ph.getClassName()+"</ClassName>\n");
            out.append("<Position>"+p+"</Position>\n");
            try{ //Bug 59518 fix.
               ProfileBuffer pb = (ProfileBuffer) ph.getProfile();
               if(pb!=null)
                  pb.toXML(out, 0);
            }catch(FndException e){
               continue;
            }
            out.append("</Portlet>\n");
         }
         out.append("</Column_"+c,">\n");
      }
      out.append("</portal_profile>\n");

      return out.toString();
   }


   /**
    * Import portal view profile information from database.
    * There are two modes: PREPARE and PERFORM.
    *
    * RIRALK: commneted for now, but should be totally removed!
    */

   /*private void importProfile(int mode) throws FndException, IOException
   {
      ASPPage              page  = getASPPage();
      ASPManager           mgr   = page.getASPManager();
      ASPCommand           cmd   = (new ASPCommand(mgr)).construct();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String               path  = page.getPagePath().toLowerCase();

      switch (mode)
      {
         case PERFORM:
            view_name = view_desc.trim().toUpperCase().replace(' ','_');

            if( page.readGlobalProfileBuffer(AVAILABLE_VIEWS).itemExists(view_name+PAGE_NODE) )
               throw new FndException("FNDPORDESCEXISTS2: View with description '&1' already exists",view_desc);

            cmd.defineCustomFunction("Web_Profile_API.Get_Current_Profile");
            cmd.addParameter("PROFILE", "S", null, null);
            cmd.addParameter("URL",     "S", null, path+SEP+view_desc);
            trans.addCommand("CURRENT", cmd);

            cmd   = (new ASPCommand(mgr)).construct();
            cmd.defineCustomFunction("Web_Profile_API.Get_Default_Profile");
            cmd.addParameter("PROFILE", "S", null, null);
            cmd.addParameter("URL",     "S", null, path+SEP+view_desc);
            trans.addCommand("DEFAULT", cmd);

            trans = mgr.perform(trans);

            String ret_val  = trans.getValue("CURRENT/DATA/PROFILE");
            if (ret_val==null) ret_val = trans.getValue("DEFAULT/DATA/PROFILE");

            Factory         fact     = page.getASPConfig().getFactory();
            Buffer          buf      = fact.getBuffer();
            BufferFormatter buf_frmt = fact.getBufferFormatter();
            buf_frmt.parse( Util.uncompress(ret_val), buf);

            Item item  = buf.findItem(getClass().getName());
            if(item==null)
            {
               if(DEBUG) debug("  Item for class ["+getClass().getName()+"] not found.");
               return;
            }
            item = item.getBuffer().findItem(view_name);
            if(item==null)
            {
               if(DEBUG) debug("  Item for object name '"+view_name+"' not found.");
               return;
            }
            Object obj =  item.getValue();

            profile = new ASPPortalProfile();
            profile.parse(this, (String)obj);
            saveProfile();
            page.writeGlobalProfileValue(CURRENT_VIEW, view_name);
            page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_DESC, view_desc);
            break;

         case PREPARE:
            trans.addQuery("PROFILES",
                           "WEB_PROFILE",
                           "DISTINCT SUBSTR(URL,INSTR(URL,'"+SEP+"')+1) URL",
                           "FND_USER IN(Fnd_Session_API.Get_Fnd_User,'DEFAULT') "+
                           "AND URL LIKE '"+path+SEP+"%'",
                           "URL").includeMeta("ALL");
            trans = mgr.perform(trans);
            Buffer profbuf = trans.getBuffer().getBuffer("PROFILES");

            out.append("<select name=\"import\" onChange=\"javascript:changePortal(this)\"");
            out.append(" style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");
            out.append("<option value=\"EMPTY\"></option>\n");

            String home = mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")+"?__VIEW_NAME=";
            String desc;

            BufferIterator iter = profbuf.iterator();
            while( iter.hasNext() )
            {
               Item data = iter.next();
               if (data.getName().equals("DATA"))
               {
                  desc = data.getBuffer().getItem("URL").getString();
                  out.append("<option value='",
                              home+mgr.URLEncode(desc.trim().toUpperCase().replace(' ','_')),
                              "&__IMPORT="+ mgr.URLEncode(desc)+"&CUSTOMIZE=Y'>",
                              desc,
                             "</option>\n");
               }
            }
            out.append("</select>");
            break;

         default:
      }
   }*/


   private void prepareStdPortlet() throws FndException
   {
      if(DEBUG) debug("ASPPortal.prepareStdPortlet()");
      ASPManager mgr = getASPManager();
      single_portlet_name = getASPPage().getStdPortletName();
      std_portlet_state = getASPPage().getStdPortletState();
      if(DEBUG) debug("  prepareStdPortlet: single_portlet_name="+single_portlet_name);

      setViewName();
      readGlobalVariables();
      prepareProfileInfo();
   }

   private void prepareSlimPortal() throws FndException
   {
      ASPManager mgr = getASPManager();
      String name  = mgr.getQueryStringValue("PORTLET");
      String id = "";
      int width = 0;
      if( Str.isEmpty(name) )
         throw new FndException("FNDPORSLIM: Portlet must be defined in the query string");
      slim_mode = true;
      readGlobalVariables();
      Buffer portlets = getAvailablePortlets(mgr);
      for( int i=0; i<portlets.countItems(); i++ )
      {
         Item item   = portlets.getItem(i);
         String desc = mgr.translateJavaText(item.getString());
         if( desc.equals(name) )
         {
            id = item.getName();
            width = Integer.parseInt(item.getType());
            break;
         }
      }
      slimcol = new ASPPortalColumn(width,false);
      //Bug 40931, start
      //ASPPortletHandle ph = new ASPPortletHandle(id, "0", null);
      ASPPortletHandle ph = new ASPPortletHandle(id, "0");
      //Bug 40931, end
      slimcol.addPortletHandle(ph);
      //Bug 40931, start
      //slimcol.activate(this,true,null,null);
      slimcol.activate(this,true,null,null,getASPPage());
      //Bug 40931, end
      if( (runmode==CUSTOM_MODE || runmode==SUBMIT_MODE) && !Str.isEmpty(portletid) )
         //Bug 40931, start
         portlet = ph.getPortlet(this);
         //Bug 40931, send
   }

   //==========================================================================
   //  Security and installed portlets
   //==========================================================================

   private static Buffer available_portlets = null;
   private static HashMap available_modules = new HashMap();


   private static void initAvailableList(ASPManager mgr)
   {
      if(DEBUG) Util.debug("ASPPortal.initAvailableList()");

      ASPConfig cfg = mgr.getASPConfig();
      ASPPage  page = mgr.getPortalPage();

      available_portlets = cfg.getFactory().getBuffer();
      //String clspath = System.getProperty("java.class.path");
      String clspath = mgr.getASPConfig().getApplicationContextPhyPath()+"WEB-INF"+ifs.fnd.os.OSInfo.OS_SEPARATOR+"classes"+ifs.fnd.os.OSInfo.OS_SEPARATOR+";";
      if(DEBUG) Util.debug("  classpath="+clspath);

      RequestHandler servlet      = (RequestHandler)mgr.getServlet();
      Vector         portlet_cand = servlet.getPortletCandidatesList();
      Hashtable      pres_objects = servlet.getPresentationObjectsList();

      if (portlet_cand == null || pres_objects == null) return;

      //StringTokenizer pst = new StringTokenizer(clspath, ";");
      Iterator itr = portlet_cand.iterator();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      HashSet module_set = new HashSet();
      String mod, mod_name;

      while( itr.hasNext() )
      {
         try
         {
            String clsname = (String)itr.next();
            Class  cls     = Class.forName(clsname);

            if(cls.isInterface())                        continue;
            if(Modifier.isAbstract(cls.getModifiers()))  continue;

            if (("b2b".equals(cfg.getParameter("APPLICATION/APP_TEMPLATE")))
                 && ("ifs.fnd.portlets.SelectStatement".equals(clsname))) continue;

            try
            {
               Class portletcls = Class.forName("ifs.fnd.asp.ASPPortletProvider");
               if (!portletcls.isAssignableFrom(cls)) continue;
            }
            catch(ClassNotFoundException e)
            {
               page.error(e);
            }

            //pres_objects.put(clsname, "Y");

            String desc = (String)(cls.getMethod("getDescription",new Class[0]).invoke(null,new Object[0]));
            int width   = ((Integer)(cls.getMethod("getMinWidth",new Class[0]).invoke(null,new Object[0]))).intValue();

            available_portlets.addItem(new Item(clsname,width+"",null,desc) );

            String class_mod = clsname.substring(0,clsname.lastIndexOf("."));
            mod = clsname.substring(4); //remove ifs
            mod = mod.substring(0,mod.indexOf(".")).toUpperCase(); //retrieve the module
            mod_name = class_mod;
            if (!module_set.contains(mod))
            {
               ASPCommand cmd = mgr.newASPCommand();
               cmd.defineCustomFunction("MODULE_API.Get_Name");
               cmd.addParameter("NAME","S",null,null);
               cmd.addParameter("MODULE","S", "IN", mod);
               trans.addCommand("GET_NAME_"+mod.toUpperCase(),cmd);
               module_set.add(mod);
            }

            if(DEBUG) Util.debug("  Found portlet: '"+clsname+"'["+width+": "+desc+"]");
         }
         catch( ClassNotFoundException e )
         {
            page.logError(e);
            continue;
         }
         catch( NoSuchMethodException e )
         {
            page.logError(e);
            continue;
         }
         catch( IllegalAccessException e )
         {
            page.logError(e);
            continue;
         }
         catch( Throwable any )
         {
            page.error(any);
         }
      }
      trans = mgr.perform(trans);
      Iterator module_iter = module_set.iterator();
      while (module_iter.hasNext())
      {
         mod = (String)module_iter.next();
         mod_name = trans.getBuffer("GET_NAME_"+ mod+"/DATA").getValue("NAME");
         if (mgr.isEmpty(mod_name))
            mod_name = mod;
         available_modules.put("ifs." + mod.toLowerCase()+".portlets",mod_name);
      }
      servlet.resetPortletCandidatesList();
   }

   /**
    * Returns a list of available portlets.
    */
   public static synchronized Buffer getAvailablePortlets(ASPManager mgr)
   {
      if(DEBUG) Util.debug("ASPPortal.getAvailablePortlets()");

      if(available_portlets==null)
         initAvailableList(mgr);
      return available_portlets;
   }


   private void checkPortletsSecurity()
   {
      if(DEBUG) debug(this+": ASPPortal.checkPortletsSecurity()");

      ASPManager mgr = getASPManager();

      BufferIterator itr = getAvailablePortlets(mgr).iterator();
      out.clear();
      while( itr.hasNext() )
         out.append(itr.next().getName(),",");
      ASPTransactionBuffer buf = mgr.newASPTransactionBuffer();
      buf.addPresentationObjectQuery(out.toString());
      buf = mgr.perform(buf);
      user_portlets = buf.getSecurityInfo().getBuffer();

      if(DEBUG) debug("  user_portlets [checkPortletsSecurity]:\n"+Buffers.listToString(user_portlets));
   }

   //==========================================================================
   //  Functions run before HTML generation
   //==========================================================================

   /**
    * Called from ASPManager.createPortalPage() after the main page is created
    * or fetched from the page pool.
    */
   void run() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.run()");

      imgloc = cfg.getImagesLocation();
      imgLocRTL = cfg.getImagesLocationWithRTL();
      if( runmode==REMOVE_MODE && Str.isEmpty(portletid) )
         removeProfile();

      if( getASPManager().getPortalPage().isSlimMode() )
      {
         if(DEBUG) debug("  run(): slim mode");
         prepareSlimPortal();
      }
      else if( std_portlet )
      {
         if(DEBUG) debug("  run(): std portlet");
         prepareStdPortlet();
      }
      else
      {
         if(DEBUG) debug("  run(): default mode");
         setViewName();
         readGlobalVariables();
         prepareProfileInfo();
      }

      if(DEBUG) debug("ASPPortal.run(): profile contents:\n"+ profile.showContents() );

      if( runmode==SUBMIT_MODE )
         submitCustomization();
/* Q&D solution - should be checked. This function is alreadu run in prepareProfileInfo()
       else if( runmode==REMOVE_MODE && portlet==null )
          //profile.activate(this,null,null);
          profile.activate(this,true,null,null);
*/
      if(DEBUG) profile.debugProfile();

      //if( portlet!=null || runmode==CUSTOM_MODE )
      if (!std_portlet)
      {
         if( !Str.isEmpty(portletid) || runmode==CUSTOM_MODE )
         {
            // re-write context for all portlets but the current (if exists)
            for(int c=0; c<profile.countColumns(); c++)
            {
               ASPPortalColumn col = profile.getColumn(c);

               for(int p=0; p<col.countPortlets(); p++)
               {
                  ASPPortletHandle ph = col.getPortletHandle(p);
                  //Bug 40931, start
                  if(!ph.isUserAvailable(this)) continue;
                  if( ph.getPortlet(this)==null )
                  //Bug 40931, end
                     ctx.rewrite(ph.getClassName() +"."+ph.getId());
               }
            }
         }
      }
      prepareHTML();
   }


   private void submitCustomization()
   {
      // called by run() method if OK button pressed.
      if(DEBUG) debug(this+": ASPPortal.submitCustomization(): "+custcols);

      try
      {
/*          if(profile==pre_profile)                                            */
/*          {                                                                   */
/*             if(DEBUG) debug(this+": cloning default profile: "+pre_profile); */
/*             profile.reset(getASPManager());                                  */
/*             profile = (ASPPortalProfile)pre_profile.clone();                 */
/*          }                                                                   */

         if(portlet==null)
         {
            String desc = getASPManager().readValue("viewdesc");
            if(desc!=null && !desc.equals(view_desc))  //portal view has been renamed
            {
               String str = desc.trim().toUpperCase().replace(' ','_');
               str = str.replace('/','_');
               ASPPage page = getASPPage();
               try
               {
                  if( page.readGlobalProfileBuffer(AVAILABLE_VIEWS,false).itemExists(str+PAGE_NODE) )
                     throw new FndException("FNDPORDESCEXISTS: View with description '&1' already exists",desc);
               }
               catch(Throwable any)
               {
                  error(any);
               }
               ASPProfile prf = page.getASPProfile();
               //remove the portal profile which has the old view_name from DB
               prf.remove(this);
               prf.save(this);

               ASPBuffer aspbuf = page.readGlobalProfileBuffer(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE,false);
               //get the position of this view if it has been given
               int curr_pos = -1;
               if (aspbuf.itemExists(PORTAL_POSITION))
               {
                 try{  
                   curr_pos = Integer.parseInt(aspbuf.getValue(PORTAL_POSITION));
                 }
                 catch (NumberFormatException ne){
                   curr_pos = -1;    
                 }
               }
               page.removeGlobalProfileItem(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE);
               //change portal view_name to the new one
               view_name = str;
               view_desc = desc.trim();
               aspbuf.setValue(PORTAL_DESC,view_desc);
               page.writeGlobalProfileBuffer(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE, aspbuf, false);
               page.writeGlobalProfileValue(CURRENT_VIEW, view_name,false);
               if (curr_pos > -1)
                 page.writeGlobalProfileValue(AVAILABLE_VIEWS+"/"+view_name+PAGE_NODE+"/"+PORTAL_POSITION, ""+curr_pos,false); 
            }

            //Bug 40931, start
            //profile.rearrangeColumns(this, custcols);
            profile.rearrangeColumns(this, custcols, getASPPage());
            //Bug 40931, end
            int t = 0;
            try
            {
               t = Integer.parseInt(getASPManager().readValue("autoRefresh"));
            }
            catch(NumberFormatException x)
            {
               t = 0;
            }
            profile.setRefreshTime(t);
            if ("Y".equals(getASPManager().readValue("WIDE_COLUMN")))
               profile.enableWideColumn();
            else
               profile.disableWideColumn();

         }
         else if(portlet.getPortletException()==null)
            try
            {
               portlet.disableWriteResponse();
               portlet.submitCustomization();
               portlet.enableWriteResponse();
            }
            catch( Throwable x )
            {
               if(DEBUG) debug("  submitCustomization(): Caught exception "+Str.getStackTrace(x));
               portlet.enableWriteResponse();
               if( !(x instanceof ASPLog.AbortException) )
               {
                  FndException portlet_err = portlet.getPortletException();
                  if( portlet_err==null )
                  {
                     if( x instanceof FndException )
                        portlet_err = (FndException)x;
                     else
                        portlet_err = new FndException(x);
                     portlet.setPortletException(portlet_err);
                  }
                  else if( portlet_err.getCaughtException()==null )
                     portlet_err.addCaughtException(x);
                  portlet_err.setAdditionalMessage(getASPManager().translateJavaText("FNDPORPRVERR3: in submitCustomization()."));
               }
            }

         // save profile information here - both cache and database
         if( !slim_mode )
            saveProfile();
      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   //==========================================================================
   //  HTML generation
   //==========================================================================

   String getPortletId()
   {
      if(Str.isEmpty(single_portlet_name))
         return portlet!=null ? portlet.getId() : "";
      else
         return portletid;
   }


   /**
    * Generate the HTML code for the whole portal body
    */
   public String generateHTML()
   {
      if(DEBUG) debug(this+": public ASPPortal.generateHTML()");
      return out.toString();
   }

   private void prepareHTML() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.prepareHTML()");

      ASPManager mgr = getASPManager();
      
      String tr_text;

      if("Y".equals(mgr.readValue("__CHANGE_COL_NO")))
          runmode = 4;

      out.clear();

      if (!std_portlet)
      {
         out.append("<script language=\"JavaScript\">\n");
         out.append("\tmin_boxes = '",MIN_BOXES_COOKIE,"';\n");
         out.append("</script>\n");
      }

      out.append("<script language=\"JavaScript\">\n");
      out.append("__CURRENT_BLOCK_NAME = '';\n"); 
      out.append("</script>\n");

      if( runmode==CUSTOM_MODE )
      {
         if (!std_portlet)
         {
            out.append("<!-- Header row for Customize page -->\n");
            //left page margin
            out.append("<table width=100% border=0 cellpadding=0 cellspacing=0>\n");
            out.append("<tr><td>&nbsp;&nbsp;</td><td>");

            if(portlet!=null)
            {
               out.append("<table width=727 border=0 cellpadding=0 cellspacing=0>\n");
               portlet_usagekey = portlet.getPoolKey();
            }
            else
               out.append("<table width=100% border=0 cellpadding=0 cellspacing=0>\n");
            out.append("<tbody>\n");
            out.append("<tr>\n");
            out.append("<td width=100% height=19>\n");
            out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
            out.append("<tbody>\n");
            out.append("<tr>\n");

            out.append("<td width=100% nowrap height=22>&nbsp;\n");
            if(portlet==null)
               out.append( mgr.translateJavaText("FNDPORPORCUST: Portal Configuration") );
            else
               out.append( mgr.HTMLEncode(portlet.getTitle(CUSTOM)), debugRef(portlet) );
            out.append("</span>&nbsp;</td>\n");

            // Help URL
            if(portlet!=null) //&& provider.canShowHelp() )
            {
               out.append("<td width=23 align=right valign=middle>");
               out.append("<a href=\"javascript:showHelp('"+portlet.getClass().getName()+"','",mgr.getUsageKey(),"','"+mgr.getUsageSessionKey()+"')\"");
               //out.append(" onmouseout=\"javascript:f.porthelpimg.src='",imgloc,portlet_help,"'\"");
               //out.append(" onmouseover=\"javascript:f.porthelpimg.src='",imgloc,"/theme"+getUserTheme(),portlet_help_hov,"'\"");
               out.append(">");
               tr_text = mgr.translateJavaText("FNDPORALTHELP2: Help");
               out.append("<img align=middle border=0 name=porthelpimg");
               out.append(" alt=\"",tr_text,"\"");
               out.append(" title=\"",tr_text,"\"");
               out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",portlet_help,"\"></a></td>\n");
            }
            out.append("</tr>\n");
            out.append("</tbody>\n");
            out.append("</table>\n");
            out.append("</td>\n");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td colspan=3>\n");

            out.append("<!-- Customize page contetnts -->\n");
            if(portlet!=null)
               out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 bgcolor=green class=portletCustomizeBody width=727>\n");
            else
               out.append("<table width=100% class=portletCustomizeBody border=0 cellpadding=2 cellspacing=0>\n");

            out.append("<tbody><tr><td class=\"portletCustomizeText\">\n");
         }

         generateCustomizeHTML(out);

         if (!std_portlet)
         {
            out.append("</td></tr><tr><td><br></td></tr></tbody>\n");
           // if (mgr.getConfigParameter("SCHEME/PORTAL/DOT_LINE","Y").startsWith("Y")){

           //     out.append("<!-- Dotted Line -->\n");
           //     out.append("<TBODY><TR><TD background=\"",imgloc,"dot_bg.gif\" height=1 width=727></TD></TR></TBODY>\n");
           // }
            out.append("</table>\n<!-- END of Customize page contetnts -->\n<br>\n");

            out.append("</td>\n");
            out.append("</tr>\n");
            out.append("</tbody>\n");
            out.append("</table>\n");

            //right page margin
            out.append("</td><td>&nbsp;&nbsp;</td></tr>");
            out.append("</table>\n");

            out.append("<!-- END of Header row for Customize page -->\n");
         }
         portlet_usagekey = null;
      }
      else
      {
         int t = slim_mode ? min_refresh_time : profile.getRefreshTime();
         if(DEBUG) debug("refresh time is set to "+t);
         if(t>0 && t<min_refresh_time) t = min_refresh_time;

         if (std_portlet) t=0;

         if( refresh_times!=null && t>0 )
         {
            out.append("<script language=\"JavaScript\">\n");
            out.append("  function scheduleRefresh() {\n");
            out.append("setTimeout('refreshPortal()',");
            out.appendInt(t*1000*60); //conver milisec to min
            out.append(");\n");
            out.append("  }\n");
            out.append("  addEvent(window, 'load', scheduleRefresh);\n");
            out.append("</script>\n");
         }

         if (!std_portlet)
         {
            out.append("<!-- Portlet Frame Table -->\n");
            //Page frame start
            out.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
            //out.append("<tr><td colspan=\"20\" height=\"10\" width=\"100%\"></td></tr>");
            out.append("<tr>");
            out.append("<td>&nbsp;&nbsp;</td>");
            
            if (mgr.isRTL())
               out.append("<td width=\"100%\" align=\"right\">");   
            else
            out.append("<td width=\"100%\" align=\"left\">");

            out.append("<table cellpadding=",is_explorer?"0":"1"," cellspacing=\"0\" border=\"0\"");
            if(!slim_mode)
            {
               if( profile.countDynamicColumns()>0 )
                  out.append(" width=100%");
               else
                  out.append(" width=10");
            }
            out.append("><tbody>\n");

            if (profile.isWideColumnAvailable())
            {
               int colsize = slim_mode ? 1 : profile.countColumns();

               out.append("<tr>");
               out.append("<td class=\"portalWideColumn\" valign=top width=100% colspan=");
               out.appendInt((colsize-1)*2);
               out.append(">\n");
               generateWideColumnHTML(out);
               out.append("</td></tr>");
            }

            out.append("<tr>\n");
         }

         generateHTML(out);

         if (!std_portlet)
         {
            out.append("</tr></tbody></table>\n");

            //Page frame end
            out.append("</td>");
            out.append("</tr></table>");

            ASPPage page = getASPPage();
            String view_desc = mgr.translateJavaText("FNDPORNEWPOR2: New Portal View");
            String view_name = view_desc.toUpperCase().replace(' ','_');
            int i = 0;
            ASPBuffer profile_buf = page.readGlobalProfileBuffer(AVAILABLE_VIEWS,false);
            while( profile_buf.itemExists(view_name+PAGE_NODE) )
            {
               i++;
               view_name = view_desc.toUpperCase().replace(' ','_')+i;
            }
            //hidden fields used by the wizard
            if (i != 0)
               out.append("<script>\nNEXT_VIEW_NAME=\"",view_desc+i,"\"\n");
            else
               out.append("<script>\nNEXT_VIEW_NAME=\"",view_desc,"\"\n");
            out.append("CURRENT_VIEW =\"",mgr.URLEncode(page.readGlobalProfileValue(CURRENT_VIEW,false)),"\"\n");
            out.append("EXISTING_VIEWS =\"");

            for (i=0; i<profile_buf.countItems(); i++)
            {
                if(i == profile_buf.countItems()-1)
                {
                   String name_ = profile_buf.getNameAt(i);
                   name_ = name_.substring(0,name_.indexOf(PAGE_NODE));
                   out.append(mgr.URLEncode(name_)+(char)31+mgr.URLEncode(profile_buf.getBufferAt(i).getValue(PORTAL_DESC)));
                }
               else
               {
                   String name_ = profile_buf.getNameAt(i);
                   name_ = name_.substring(0,name_.indexOf(PAGE_NODE));
                   out.append(mgr.URLEncode(name_)+(char)31+mgr.URLEncode(profile_buf.getBufferAt(i).getValue(PORTAL_DESC))+(char)30);
               }
            }
            out.append("\"\n");
            out.append("</script>\n");
            profile_buf.traceBuffer("");
         }
      }
   }


   /**
    * function to generate wide column HTML
    */
   private void generateWideColumnHTML( AutoString out )
   {
      if(DEBUG) debug(this+": ASPortal.generateWideColumnHTML()");
      int c = profile.getWideColumnIndex();
      ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);

      try
      {
         out.append("\n\n<!-- COLUMN ");
         out.appendInt(c);
         out.append("-->\n\n");

         if( col.countPortlets() == 0 ) return;

         generatePortletsHTML(col,c);

         out.append("\n\n<!-- COLUMN ");
         out.appendInt(c);
         out.append(" END -->\n\n");

      }
      catch( Throwable any )
      {
         error(any);
      }


   }

   /**
    * Common function for HTML generation.
    */
   private void generateHTML( AutoString out )
   {
      if(DEBUG) debug(this+": ASPPortal.generateHTML()");

      try
      {
         ASPManager mgr = getASPManager();

         int colsize = slim_mode ? 1 : profile.countColumns();
         int custcolwidth = colsize>0 ? 100/colsize : 100;
         boolean has_wide_col = profile.isWideColumnAvailable();
         int wide_index = profile.getWideColumnIndex();

         out.append("<input type=hidden name=colnum value="+ colsize +">");
         out.append("<input type=hidden name=WIDE_COLUMN value=\""+ (profile.isWideColumnAvailable()? "Y":"N") +"\">");
         for(int c=0; c<colsize; c++)
         {
            ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
            
            out.append("<input type=hidden name='col"+ c +"width' value="+ col.getWidth() +">");
            out.append("<input type=hidden name='col"+ c +"type' value=FALSE>");
            
            if (c == wide_index) continue;            

            out.append("\n\n<!-- COLUMN ");
            out.appendInt(c);
            out.append("-->\n\n");

            if( col.countPortlets() == 0 ) continue;

            if (!std_portlet)
            {
               out.append("<td class=\"portalNormColumn\" valign=top");

               int colwidth = col.getWidth();
               if( !col.isDynamic() )
                  out.append(">\n");
               else
               {
                  out.append(" width=\"");
                  //int dyncol = profile.countDynamicColumns();
                  int dyncoltot = profile.getDynamicColTotWidth();
                  out.appendInt( dyncoltot>0 ? 100*colwidth/dyncoltot : 100 );
                  out.append("%\">\n");
               }
               out.append("<img src=\"",imgloc,"trans.gif\" height=1 width=");
               out.appendInt(colwidth);
               out.append("><br>\n");
            }

            generatePortletsHTML(col, c);

            if (!std_portlet)
            {
               out.append("</td>\n");

               //Page frame right
               out.append("<td>&nbsp;&nbsp;</td>\n");
            }
         }
      }
      catch( Throwable any )
      {
         error(any);
      }

   }

   /**
    * function to generate customize portal body
    */
   private void generateCustBodyHTML( AutoString out )
   {
      if(DEBUG) debug(this+": ASPPortal.generateHTML()");

      try
      {
         ASPManager mgr = getASPManager();

         int colsize = slim_mode ? 1 : profile.countColumns();
         int custcolwidth = colsize>0 ? 100/colsize : 100;
         boolean has_wide_col = profile.isWideColumnAvailable();
         int no_of_cols;
         int wide_index = profile.getWideColumnIndex();

         if( runmode==CUSTOM_MODE )
         {
            no_of_cols = (has_wide_col)?colsize-1:colsize;
            out.append("<iframe style=\"position: absolute;width:150px;height:16px;border:none;visibility:hidden;z-index:100;\" id=\"dhtmltooltip\"></iframe>\n");
            out.append("<div class=\"tooltip\" style=\"position:absolute;visibility:hidden;z-index:100;border:1px solid;\" id=\"tooltipx\"></div>\n");
            out.append("<table border=0 cellpadding=2 cellspacing=2 width=100%>\n");
            out.append("<p>");
            out.append("<FONT class=normalTextHeading>");
            out.append(mgr.translateJavaText("FNDPORSTEP1: Step 1: Choose Layout for this View"),"<br>\n");
            out.append("</font>");
            out.append("<font class=normalTextValue>");
            out.append(mgr.translateJavaText("FNDPORSTEP1TXT: Choose one to four column styles for your portal."),"\n");
            out.append("</font>");
            out.append("</p>");

            out.append("<tr>");

            out.append("<td nowrap class=normalTextValue>");
            out.append("<img name='rb1' border=0 src=\"",imgloc,((no_of_cols == 1)?"rb1colatv.gif":"rb1col.gif"),"\" >");
            out.append("&nbsp;",drawRadioButton("FNDPORONECOL: One column","LAYOUT_STYLE","1",(no_of_cols == 1),"onclick=\"javascript:changeColumnNo('1')\"",0));
            out.append("&nbsp;","</td>");
            out.append("<td nowrap class=normalTextValue>");
            out.append("<img name='rb2' border=0 src=\"",imgloc,((no_of_cols == 2)?"rb2colatv.gif":"rb2col.gif"),"\" >");
            out.append("&nbsp;",this.drawRadioButton("FNDPORTWOCOL: Two columns","LAYOUT_STYLE","2",(no_of_cols == 2),"onClick=\"javascript:changeColumnNo('2')\"",1));
            out.append("&nbsp;","</td>");
            out.append("<td nowrap class=normalTextValue>");
            out.append("<img name='rb3' border=0 src=\"",imgloc,((no_of_cols == 3)?"rb3colatv.gif":"rb3col.gif"),"\" >");
            out.append("&nbsp;",this.drawRadioButton("FNDPORTHREECOL: Three columns","LAYOUT_STYLE","3",(no_of_cols == 3),"onClick=\"javascript:changeColumnNo(this.value)\"",2));
            out.append("&nbsp;","</td>");
            out.append("<td class=normalTextValue>");
            out.append("<img name='rb4' border=0 src=\"",imgloc,((no_of_cols == 4)?"rb4colatv.gif":"rb4col.gif"),"\" >");
            out.append("&nbsp;",this.drawRadioButton("FNDPORFOURCOL: Four columns","LAYOUT_STYLE","4",(no_of_cols == 4),"onClick=\"javascript:changeColumnNo(this.value)\"",3));
            out.append("&nbsp;","</td>");


            out.append("<td nowrap>\n");
            out.append("<img name='wideimg' border=0 src=\"",imgloc,(has_wide_col?"cbtopcolatv.gif":"cbtopcol.gif"),"\" >");
            out.append(this.drawCheckbox("WIDE_COLUMN","Y","FNDPORMULTICOL: Multi column",has_wide_col,"onClick=\"javascript:changeColNo()\""));
            out.append("&nbsp;&nbsp;","</td>\n");

            if(refresh_times!=null)
            {
               out.append("<td align=right nowrap><font class=normalTextLabel>");
               out.append(mgr.translateJavaText("FNDPORREFRTIM: Auto refresh time (min)"),"</font>","&nbsp;\n");
               out.append("<select name=autoRefresh");
               out.append(" class=\"selectbox\">\n");
               out.append("<option value=0>&nbsp;</option>\n");
               int reftime = profile.getRefreshTime();
               for(int i=0; i<refresh_times.length; i++)
               {
                  int t = refresh_times[i];
                  out.append("<option value=");
                  out.appendInt(t);
                  out.append(reftime==t?" selected>":">");
                  out.appendInt(t);
                  out.append("</option>\n");
               }
               out.append("</select>\n");
               out.append("&nbsp;</td>");
            }
            out.append("<tr>\n");
            out.append("<td colspan=5>\n");
            out.append("<input type=hidden name=colnum value=");
            out.appendInt(colsize);
            out.append(" >\n");
            out.append("<input type=\"hidden\" name=\"__CHANGE_COL_NO\" value=\"N\">");
            out.append("\n");
            out.append("</td>\n");


            out.append("<td "+((mgr.isRTL())?"align=left":"align=right")+">\n");
            out.append("<input id=APPLY name=APPLY type=button value=",mgr.translateJavaText("FNDPORAPPLYBTN: Apply"));
            out.append(" onclick=\"submitCustomization('",portlet==null?"":portlet.getId(),"')\"");
            out.append(" class='button'");
            out.append(" >&nbsp;\n");
            //out.append( is_explorer ? " WIDTH: 80px;" : "", " LEFT: 57px; TOP: 196px\" >&nbsp;\n");
            out.append("</td>\n");
            out.append("</tr>\n");

            out.append("<tr><td nowrap colspan=6 background=\"",imgloc,"dot_bg.gif\" height=1></td></tr>\n");
            out.append("</td></tr></table>\n");
            // step 1 end - step 2 start html
            out.append("<table border=0 cellpadding=0 cellspacing=0 width=100%>\n");
            out.append("<p>");


            out.append("<FONT class=normalTextHeading>");
            out.append(mgr.translateJavaText("FNDPORSTEP2: Step 2: Select Portlets and Name for this View"),"<BR>","\n");
            out.append("</font>");
            out.append("<font class=normalTextValue>");
            out.append("<UL>");
            if (mgr.isNetscape4x())
               out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT1NN: Select a column in the portal layout."),"\n");
            else
               out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT1: Select a column in the portal layout (a selected column will turn white)."),"\n");
            out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT2: Select category in the combo-box."),"\n");
            out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT3: Select your portlet."),"\n");
            out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT4: Use the right-arrow and the trash icon to move portlets back and forth from this column."),"\n");
            out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT5: Repeat this procedure for any column you want to have portlets in."),"\n");
            out.append("<LI>",mgr.translateJavaText("FNDPORSTEP2TXT6: When done, press 'Save &amp; Return' to save the configuration."),"\n");
            out.append("</LI></UL>");
            out.append("</font>");
            out.append("</p><p>");
            out.append("</table>");


            out.append("<script language=\"JavaScript\">\n");
            out.append("\tmaxcolno  = ");
            out.appendInt(colsize);
            out.append(";\n");

            out.append("\thas_wide_col = "+profile.isWideColumnAvailable(),";\n");
            out.append("\twide_index      = "+wide_index+";\n");
            out.append("\tSELECT_COLUMN     = \"",mgr.translateJavaText("FNDPORJSSELCOL: Select a column."),"\";\n");
            out.append("\tCANT_PERFORM_OP   = \"",mgr.translateJavaText("FNDPORJSNOOP: Can not perform this operation on selected column."),"\";\n");
            out.append("\tWIDE_PORTLET      = \"",mgr.translateJavaText("FNDPORJSWP: Selected column width is too small for portlet(s)."),"\";\n");
            out.append("\tNO_OTHER_COLUMN   = \"",mgr.translateJavaText("FNDPORJSNC: All other columns are too small to fit this portlet"),"\";\n");
            out.append("\tWIDE_PORTLET_CHK1 = \"",mgr.translateJavaText("FNDPORJSWPCHK1: Portlet '"),"\";\n");
            out.append("\tWIDE_PORTLET_CHK2 = \"",mgr.translateJavaText("FNDPORJSWPCHK2: ' does not fit in the selected column."),"\";\n");
            out.append("\tNO_EMPTY_COL      = \"",mgr.translateJavaText("FNDPORJSNOEMPCOL: You must empty all columns you want to remove"),"\";\n");
            out.append("\tFORCED_TO_FITTING_COLUMN = \"",mgr.translateJavaText("FNDPORJSFORCEDFIT: Selected portlet could not fit in neighboring column. Moved to next possible column."),"\";\n");

            out.append("</script>\n");
            //portel selctbox layout HTML
            out.append("<table border=0 cellpadding=1 cellspacing=1 width=100%>\n");
            out.append("<tbody>\n");
            out.append("<tr>\n");
            out.append("<td class=normalTextLabel>",mgr.translateJavaText("FNDPORPORTLIB: Portlet Library"),"</td>\n");
            out.append("<td></td>\n");
            out.append("<td></td>\n");
            out.append("<td class=normalTextLabel><b>",mgr.translateJavaText("FNDPORPORTLAY: Portal Layout for View"),"</b>");

            out.append("&nbsp;<input class='editableTextField' type=text name=viewdesc value=");
            out.append("\"",mgr.HTMLEncode(view_desc),"\"");
            out.append(" size=25 >&nbsp;&nbsp;</td>\n");

            out.append("<td></td>\n");
            out.append("</tr>");
            out.append("<tr>\n");

            //Portlet Library
            AutoString modules = new AutoString();
            AutoString portlets = new AutoString();
            AutoString port_list = new AutoString();
            String class_mod;

            if(DEBUG) debug("  user_portlets [generateHTML]:\n"+Buffers.listToString(user_portlets));
            if(user_portlets!=null)
            {
               Buffer all = getAvailablePortlets(mgr);
               Buffer buf = all.newInstance();
               Buffer mod = cfg.getFactory().getBuffer();
               BufferIterator itr = user_portlets.iterator();
               HashMap stored_modules = new HashMap();

               while( itr.hasNext() )
               {
                  Item item = itr.next();
                  String name = item.getName();
                  //String desc = mgr.translateJavaText(all.getString(name));
                  Item item2 = all.getItem(name);
                  String desc = mgr.translateJavaText(item2.getString());
                  String width = item2.getType();
                  Buffer b = all.newInstance();
                  b.addItem("CLSNAME",name);
                  b.addItem("DESC",desc);
                  b.addItem("WIDTH",width);
                  buf.addItem("DATA",b);

                  b = cfg.getFactory().getBuffer();
                  class_mod = name.substring(0,name.lastIndexOf("."));

                  if (!stored_modules.containsKey(class_mod))
                  {
                     b.addItem("CLSNAME",class_mod);
                     b.addItem("MODNAME",available_modules.get(class_mod));
                     mod.addItem("DATA",b);
                     stored_modules.put(class_mod, "");
                  }

               }
               stored_modules = null;
               RowComparator comp = new RowComparator(mgr.getServerFormatter(),"DESC",true);
               Buffers.sort(buf,comp);

               itr = buf.iterator();
               while( itr.hasNext() )
               {
                  Item item = itr.next();
                  String name  = item.getBuffer().getString("CLSNAME");
                  String desc  = item.getBuffer().getString("DESC");
                  String width = item.getBuffer().getString("WIDTH");
                  if(DEBUG) debug("  option tag: ["+name+"]='"+width+"."+desc+"'");
                  portlets.append("<option value=",width,".",name,">");
                  portlets.append(desc,"</option>\n");

                  port_list.append(width,".",name,""+(char)30);
                  port_list.append(desc,""+(char)31);
               }

               comp = new RowComparator(mgr.getServerFormatter(),"MODNAME",true);
               Buffers.sort(mod,comp);

               itr = mod.iterator();
               while( itr.hasNext() )
               {
                  Item item = itr.next();
                  String name  = item.getBuffer().getString("CLSNAME");
                  String mod_name  = (!"ifs.fnd.portlets".equals(name)?item.getBuffer().getString("MODNAME"):mgr.translate("FNDPORTALFNDMODULE: General"));

                  modules.append("<option value=",name,">");
                  modules.append(mod_name,"</option>\n");
               }

            }


            out.append("<td valign=top><table class=borders cellpadding=2 cellspacing=2>\n");
            out.append("<tr><td>\n");
            out.append("<select onclick=\"javascript:showdrivetip(this,7,event);\" name=modules onChange=\"javascript:validateModule()\"");
            out.append(" class=\"selectbox\" style=\"MARGIN: 2px 0px 0px 2px; PADDING-BOTTOM: 0px; PADDING-LEFT: 0px; PADDING-RIGHT: 0px; PADDING-TOP: 0px; WIDTH: 180px\">\n");
            out.append("<option>",mgr.translateJavaText("FNDPORALLPORTS: All Available Portlets"),"</option>\n");
            out.append(modules.toString());
            out.append("</select>\n");
            out.append("</td></tr>\n");

            out.append("<tr><td ");
            out.append(" align=left>\n");
            if (mgr.isNetscape4x())
               out.append("<select class=portletSelections multiple name=addProvider size=15>");
            else
               out.append("<select ",mgr.isExplorer()?"onchange":"onclick","=\"showdrivetip(this,6,event)\" style=\"MARGIN: 2px 0px 0px 2px;WIDTH: 180px;\" class=selectbox multiple name=addProvider size=14>");

            out.append(portlets.toString());
            out.append("</select>\n");
            out.append("<input type=hidden name=PORT_LIST value=\"",port_list.toString(),"\" >");
            out.append("</td></tr>\n");
            out.append("</table></td>");
            //adding/deleting images
            out.append("<td>");
            out.append("<a href=\"javascript:addPortlet()\">");
            out.append("<img border=0 src=\"",imgLocRTL,"portlet_add.gif\"");
            out.append(" alt=\"",mgr.translateJavaText("FNDPORALTADDPOR: Add portlet"),"\"");
            out.append(" title=\"",mgr.translateJavaText("FNDPORALTADDPOR: Add portlet"),"\"");
            out.append("></a><br><br>\n");
            out.append("<a href=\"javascript:removePortlet()\">");
            out.append("<img border=0 src=\"",imgloc,"trash.gif\" ");
            out.append(" alt=\"",mgr.translateJavaText("FNDPORALTDELPOR: Remove portlet"),"\"");
            out.append(" title=\"",mgr.translateJavaText("FNDPORALTDELPOR: Remove portlet"),"\"");
            out.append("></a>");
            out.append("</td>");
            out.append("<td>&nbsp;&nbsp;</td>\n");
            //portel layout
            out.append("<td valign=top><table class=borders cellpadding=5 cellspacing=0><tr>\n");
            out.append("<td valign=top><table cellpadding=0 cellspacing=0>\n");
            // HTML for wide column
            if (profile.isWideColumnAvailable())
            {
               out.append("<tr>");
               out.append("<td colspan=");
               out.appendInt(colsize);
               out.append(">\n");
               ASPPortalColumn col = profile.getColumn(wide_index);
               generateWideColumnBox(col,wide_index,colsize-1);
               out.append("</td></tr>");
            }
            out.append("<tr>");
            int nn4ind = 0;
            int initially_selected_index = -1;

            for(int c=0; c<colsize; c++)
            {
               if (c == wide_index) continue;

               if (initially_selected_index == -1)
                  initially_selected_index = c;

               ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);

               out.append("\n\n<!-- COLUMN ");
               out.appendInt(c);
               out.append("-->\n\n");

               nn4ind++; // used to enable the proper radio button in NN4

               generateCustomBox(col,c,colsize-1,(profile.isWideColumnAvailable()?nn4ind:nn4ind-1));
            }
            if (initially_selected_index == -1)
               initially_selected_index=wide_index;

            out.append("<input type=hidden name=SELECTED_COL value='"+initially_selected_index+"'>");
            out.append("</tr>");
            out.append("</table></td>");
            out.append("</td></tr></table></td>"); //outter table

            //moving and config
            out.append("<td valign=top><table cellpadding=0 cellspacing=0>\n");
            out.append("<tr><td class=normalTextValue>",mgr.translateJavaText("FNDPORMOVEPORT: Move current portlet"),"</td></tr>\n");
            //arrows
            out.append("<tr><td><table>\n");
            out.append("<tr><td></td>\n");
            out.append("<td><a href=\"javascript:moveUp()\"><img border=0 src=\"",imgloc,"portlet_up.gif\"</a></td>\n");
            out.append("<td></td></tr>\n");
            out.append("<tr>\n");
            out.append("<td><a href=\"javascript:moveSide('left')\"><img border=0 src=\"",imgLocRTL,"portlet_left.gif\"</a></td>\n");
            out.append("<td></td>");
            out.append("<td><a href=\"javascript:moveSide('right')\"><img border=0 src=\"",imgLocRTL,"portlet_right.gif\"</a></td>\n");
            out.append("</tr>\n");
            out.append("<tr><td></td>\n");
            out.append("<td><a href=\"javascript:moveDown()\"><img border=0 src=\"",imgloc,"portlet_down.gif\"</a></td>\n");
            out.append("<td></td></tr>\n");
            out.append("</table></td></tr>");

            out.append("<tr><td class=normalTextValue><br>",mgr.translateJavaText("FNDPORCHOPORTWIDTH: Choose the width of the <br>marked column:"),"</td></tr>\n");
            out.append("<tr><td class=normalTextValue><br>");
            out.append(drawRadioButton("FNDPORNARROW: Narrow","PORTLET_SIZE","144",false,"onClick=\"javascript:changeColumnSize(this)\"",0),"</td></tr>");
            out.append("<tr><td class=normalTextValue>");
            out.append(drawRadioButton("FNDPORWIDE: Wide","PORTLET_SIZE","288",false,"onClick=\"javascript:changeColumnSize(this)\"",1),"</td></tr>");
            out.append("<tr><td class=normalTextValue>");
            out.append(drawRadioButton("FNDPORDYN: Dynamic","PORTLET_SIZE","D",false,"onClick=\"javascript:setDynamic('TRUE',this)\"",2),"</td></tr>");
            out.append("</table></td>");
            out.append("</tr>");
            out.append("</tbody>");
            out.append("</table>");

            out.append("\n<script language=javascript>");
            out.append("\nfunction initialFocus(){\n");
            if (!mgr.isNetscape4x())
               out.append("document.forms[0].col_"+initially_selected_index+".onclick();");
            else
            {
               // to handle the radio buttons in NN4
               if (colsize != 1)
                  out.append("document.forms[0].ACT_COL["+(profile.isWideColumnAvailable()?1:0)+"].onclick();");
               else
               {
                  out.append("\ndocument.forms[0].ACT_COL.checked=true;");
                  out.append("document.forms[0].ACT_COL.onclick();");
               }
            }
            out.append("\n}\n</script>\n");

         }

      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //--------------------------------------------------------------------------
   //  Standard (portal) mode
   //--------------------------------------------------------------------------

   /**
    * Generate the HTML code for one column only
    */
   private void generatePortletsHTML( ASPPortalColumn col, int columnNumber ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.generatePortletsHTML("+col+")");

      for(int p=0; p<col.countPortlets(); p++)
      {
         ASPManager mgr = getASPManager();

         ASPPortletHandle ph = col.getPortletHandle(p);
         String           prvname = ph.getClassName();
         String           id      = ph.getId();

         if (!std_portlet)
         {
            if(!ph.isUserAvailable(this)) continue;
         }
         else if( !single_portlet_name.equals(prvname) || (portletid!=null && !portletid.equals(id)) )
         {
            if(DEBUG) debug("  generatePortletsHTML(): skipping this portlet...");
            continue;
         }

         ASPPortletProvider prv = ph.getPortlet(this);

         //Bug 40931, end
         if( prv!=null && prv.getPortletException()==null )
         {
            portlet_usagekey = prv.getPoolKey();
            try
            {
               prv.disableWriteResponse();
               prv.run();
               prv.enableWriteResponse();
            }
            //catch( FndException e )
            catch( Throwable x )
            {
               if(DEBUG) debug("  generatePortletsHTML(): Caught exception "+Str.getStackTrace(x));
               prv.enableWriteResponse();
               if( !(x instanceof ASPLog.AbortException) )
               {
                  FndException portlet_err = prv.getPortletException();
                  if( portlet_err==null )
                  {
                     if( x instanceof FndException )
                        portlet_err = (FndException)x;
                     else
                        portlet_err = new FndException(x);
                     prv.setPortletException(portlet_err);
                  }
                  else if( portlet_err.getCaughtException()==null )
                     portlet_err.addCaughtException(x);
                  portlet_err.setAdditionalMessage(mgr.translate("FNDPORPRVERR: in run()."));
               }
               //e.setAdditionalMessage(mgr.translate("FNDPORPRVERR1: in portlet '&1'",mgr.translate(prv.getTitle(MAXIMIZED))));
               //throw e;
            }
         }

         int mode;

         if(DEBUG) debug("  id="+id+", minboxes="+minboxes);
         if( mgr.isEmpty(minboxes) || minboxes.indexOf(","+id+",")==-1 )
            mode = MAXIMIZED;
         else
            mode = MINIMIZED;

         prvname = prvname +" ["+id+"]";

         out.append("\n\n<!-- Portlet: ",prvname," -->\n");
         
         out.append("\n<input type=hidden id='C"+columnNumber+"_"+p+"' value="+ id +">");         
         if ( !std_portlet )
         {
            out.append("<TABLE border=0 cellPadding=0 cellSpacing=0 width=100%>\n");//class=c350>\n");
            out.append("<TR>\n");
            out.append("<TD>\n\n");

            out.append("<!-- Portlet header for: ",prvname," -->\n");

            if(is_explorer||is_netscape6_above)
            {
                out.append("<div id=max_head_",ph.getId(),"");
                out.append((mode==MAXIMIZED)?"":" style=\"display:none\"",">");
                generateBoxHeader(out, ph, MAXIMIZED);
                out.append("</div>\n");

                out.append("<div id=min_head_",ph.getId(),"");
                out.append((mode==MINIMIZED)?"":" style=\"display:none\"",">");
                generateBoxHeader(out, ph, MINIMIZED);
                out.append("</div>\n");
            }
            else
               generateBoxHeader(out, ph, mode);
         }
         else
         {
            //set standard portlet title
            String std_portlet_title = "";

            if (std_portlet_state == MINIMIZED)
               std_portlet_title = mgr.HTMLEncode(prv.getTitle(MINIMIZED));
            else
               std_portlet_title = mgr.HTMLEncode(prv.getTitle(MAXIMIZED));

            setStdPortletTitle(std_portlet_title);

            if (std_portlet_state == MINIMIZED) mgr.endResponse();
         }

         out.append("\n<!-- Portlet body for: ",prvname," -->\n");
         mgr.responseWrite("response here");
         generateBoxBody(out, ph, mode);

         if ( !std_portlet )
         {
            out.append("\n</TD>\n");
            out.append("</TR>\n");
            out.append("</TABLE>\n");
         }
         out.append("<!-- End of Portlet: ",prvname," -->\n\n");

         if ( !std_portlet )
            out.append("<br>\n");

         portlet_usagekey = null;
      }
   }

   String getPortletUsagekey()
   {
      return portlet_usagekey;
   }


   /**
    * Generate the HTML code for one portlet header only
    */
   private void generateBoxHeader( AutoString out, ASPPortletHandle ph, int mode )
   {
      if(DEBUG) debug(this+": ASPPortal.generateBoxHeader()");

      ASPManager mgr = getASPManager();
      ASPPortletProvider prv = ph.getPortlet(this);
      String tr_text;
      if(prv!=null)
      {
         String mintitle = null;
         String maxtitle = null;

         if(prv.getPortletException()==null)
            try
            {
               prv.disableWriteResponse();
               mintitle = mgr.HTMLEncode(prv.getTitle(MINIMIZED));
               maxtitle = mgr.HTMLEncode(prv.getTitle(MAXIMIZED));
               if(DEBUG) debug("  Encoded titles:\n    mintitle="+mintitle+"\n    maxtitle="+maxtitle);
               prv.enableWriteResponse();
            }
            catch( Throwable x )
            {
               if(DEBUG) debug("  generateBoxHeader(): Caught exception "+Str.getStackTrace(x));
               prv.enableWriteResponse();
               if( !(x instanceof ASPLog.AbortException) )
               {
                  FndException portlet_err = prv.getPortletException();
                  if( portlet_err==null )
                  {
                     if( x instanceof FndException )
                        portlet_err = (FndException)x;
                     else
                        portlet_err = new FndException(x);
                     prv.setPortletException(portlet_err);
                  }
                  else if( portlet_err.getCaughtException()==null )
                     portlet_err.addCaughtException(x);
                  portlet_err.setAdditionalMessage(mgr.translateJavaText("FNDPORPRVERR2: in getTitle()."));
               }
            }

         if(prv.getPortletException()!=null)
            mintitle = maxtitle = mgr.HTMLEncode(mgr.translateJavaText(getAvailablePortlets(mgr).getString(ph.getClassName(),ph.getClassName())));
               //(String)(Class.forName(ph.getClassName()).getMethod("getDescription",new Class[0]).invoke(null,new Object[0]));

         //Bug 40931, start
         ph.setMinTitle( this, mintitle );
         ph.setMaxTitle( this, maxtitle );
         ph.setCanCustomize( this, prv.canCustomize() );
         //Bug 40931, end
         if(!mgr.isEmpty(prv.getZoomInURL()))
            //Bug 40931, start
            ph.setCanZoomInURL(this,prv.getZoomInURL());
            //Bug 40931, end
         else
            //Bug 40931, start
            ph.setCanZoomInURL(this,"");
            //Bug 40931, end

      }

      out.append("<TABLE width=100% border=0 cellpadding=0 cellspacing=0 class=\"portletCommandBar\">\n");
      out.append("<TR>\n");

      String id = ph.getId();

      // portlet title
      if( mode==MAXIMIZED )
      {
         out.append("<td nowrap width=100% height=19");
         out.append(" id=hmax_",id,">");
         //Bug 40931, start
         out.append("&nbsp;", ph.getMaxTitle(this),debugRef(ph), "&nbsp;</td>\n");
         //Bug 40931, end
      }
      if( mode==MINIMIZED )
      {
         out.append("<td nowrap width=100% height=19");
         out.append(" id=hmin_",id,">");
         //Bug 40931, start
         out.append("&nbsp;", ph.getMinTitle(this),debugRef(ph), "&nbsp;</td>\n");
         //Bug 40931, end
      }

      // customize icon
      //Bug 40931, start
      if( !mgr.getPortalPage().isPortletConfigurationDisabled() && ph.canCustomize(this) && !slim_mode && cfg.isPortletConfigEnabled() && !mgr.getPortalPage().getASPProfile().isUserProfileDisabled() )
      //Bug 40931, end
      {
         out.append("<td width=23 nowrap align=right valign=middle>");
         out.append("<a href=\"javascript:customizeBox('",id,"')\"");
         out.append(">");
         tr_text = mgr.translateJavaText("FNDPORALTCUST: Customize settings");
         out.append("<img align=middle border=0 name=portcust",mode+"",id);
         out.append(" alt=\"",tr_text,"\"");
         out.append(" title=\"",tr_text,"\"");
         out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",cust_portlet,"\"></a></td>\n");
      }
      // update portlet icon
      out.append("<td width=23 nowrap align=right valign=middle>");
      out.append("<a href=\"javascript:submitPortlet('",id,"')\"");
      out.append(">");
      tr_text = mgr.translateJavaText("FNDPORALTUPDBOX: Update portlet");
      out.append("<img align=middle border=0 name=portupd",mode+"",id);
      out.append(" alt=\"",tr_text,"\"");
      out.append(" title=\"",tr_text,"\"");
      out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",upd_portlet,"\"></a></td>\n");

      //Bug 40931, start
      if(!mgr.isEmpty(ph.getCanZoomInURL(this)))
      //Bug 40931, end
      {
         String url ="";
         //Bug 40931, start
         url = ph.getCanZoomInURL(this);
         //Bug 40931, end
         out.append("<td width=23 nowrap align=right valign=middle>");
         out.append("<a href=\"javascript:showNewBrowser('",url,"')\"");
         out.append(">");
         tr_text = mgr.translateJavaText("FNDPORALTZOOMIN: Zoom in");
         out.append("<img align=middle border=0 name=zoomIn",mode+"",id);
         out.append(" alt=\"",tr_text,"\"");
         out.append(" title=\"",tr_text,"\"");
         out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",zoom_in_portlet,"\"></a></td>\n");
      }

      // Help URL

      out.append("<td width=23 nowrap align=right valign=middle>");
      out.append("<a href=\"javascript:showHelp('"+ph.getClassName()+"','"+mgr.getUsageKey(),"','"+mgr.getUsageSessionKey()+"')\"");
      //out.append(" onmouseout=\"javascript:f.porthelpimg",mode+"",id,".src='");
      //out.append(imgloc,portlet_help,"'\"");
      //out.append(" onmouseover=\"javascript:f.porthelpimg",mode+"",id,".src='");
      //out.append(imgloc,portlet_help_hov,"'\"");
      out.append(">");
      tr_text = mgr.translateJavaText("FNDPORALTHELP: Help");
      out.append("<img align=middle border=0 name=porthelpimg",mode+"",id);
      out.append(" alt=\"",tr_text,"\"");
      out.append(" title=\"",tr_text,"\"");
      out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",portlet_help,"\"></a></td>\n");
            
      // minimize-maximize icon
      if( mode==MAXIMIZED )
      {
         out.append("<td width=23 nowrap align=right valign=middle>");
         out.append("<a href=\"javascript:minimizeBox('",id,"')\"");
         out.append(">");
         tr_text = mgr.translateJavaText("FNDPORALTMIN: Minimize this component");
         out.append("<img align=middle border=0 name=portmin",id);
         out.append(" alt=\"",tr_text,"\"");
         out.append(" title=\"",tr_text,"\"");
         out.append(" src=\"",imgloc,mgr.getUserTheme()+"/","min_portlet.gif\"></a></td>\n");
      }
      if( mode==MINIMIZED )
      {
         out.append("<td width=23 nowrap align=right valign=middle>");
         out.append("<a href=\"javascript:maximizeBox('",id,"')\"");
         out.append(">");
         tr_text = mgr.translateJavaText("FNDPORALTMAX: Maximize this component");
         out.append("<img align=middle border=0 name=portmax",id);
         out.append(" alt=\"",tr_text,"\"");
         out.append(" title=\"",tr_text,"\"");
         out.append(" src=\"",imgloc,mgr.getUserTheme()+"/","max_portlet.gif\"></a></td>\n");
      }

      //--------------------------------------------------------------------------------------
      if (cfg.isPortalConfigEnabled())
      {
         // The RemovePortlet Icon
         tr_text = mgr.translateJavaText("FNDPORALTREMOVE: Remove portlet");
         out.append("<td width=23 nowrap align=right valign=middle>");
         out.append("<a href='javascript:removePortlet("+ ph.getId() +");'>");
         out.append("<img align=middle border=0");
         out.append(" alt=\"",tr_text,"\"");
         out.append(" title=\"",tr_text,"\"");
         out.append(" src=\"",imgloc,mgr.getUserTheme()+"/",remove_portlet,"\"></a></td>\n");
      }
      //--------------------------------------------------------------------------------------
      
      out.append("</TR>\n");
      out.append("</TABLE>\n");
   }


   /**
    * Generate the HTML code for one portlet body only
    */
   private void generateBoxBody( AutoString out, ASPPortletHandle ph, int mode ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.generateBoxBody()");


      ASPManager mgr = getASPManager();
      //Bug 40931, start
      ASPPortletProvider prv  = ph.getPortlet(this);
      AutoString         html = ph.getHTML(this);
      //Bug 40931, end
      String             id   = ph.getId();

      if(prv!=null)
      {
         if(DEBUG) debug("  Creating and caching new HTML for portlet ["+id+"].");
         html.clear();
         html.append( prv.getContents() );

         String alert_msg = mgr.getAlertMsg();
         if (isStdPortlet() && !Str.isEmpty(alert_msg))
         {
            html.append("<div id='msgtab0_"+id+"'");
            html.append(" style=\"DISPLAY: block\">\n");
            html.append("<table width=100% border=0 cellpadding=0 cellspacing=0>");
            html.append("<tbody>\n");
            html.append("<tr>\n");
            html.append("<td width=100% class=\"portlet-msg-alert\">\n");

            html.append(Str.replace(alert_msg,"\n\n","<br>"));

            html.append("</td>\n");
            //html.append("</tr>\n");

            //html.append("<tr>\n");
            html.append("<td valign=bottom>\n");
            html.append("<font class='portlet-font-dim'><a href=\"javascript:hideMsg('"+id+"')\">\n");
            html.append(mgr.translate("FNDPORALTHIDE: Hide"));
            html.append("</a></font>\n");
            html.append("</td>\n");
            html.append("</tr>\n");

            html.append("</tbody>\n");
            html.append("</table>\n");
            html.append("</div>\n");

            html.append("<div id='msgtab1_"+id+"'");
            html.append(" style=\"DISPLAY: none\">\n");
            html.append("<table width=100% border=0 cellpadding=0 cellspacing=0>");
            html.append("<tr>\n");
            html.append("<td width=100%>&nbsp;</td>\n");
            html.append("<td>\n");
            html.append("<font class='portlet-font-dim'><a href=\"javascript:showMsg('"+id+"')\">\n");
            html.append(mgr.translate("FNDPORALTSHOW: Show"));
            html.append("</a></font>\n");
            html.append("</td>\n");
            html.append("</tr>\n");
            html.append("</table>\n");
            html.append("</div>\n");
         }
      }


      if( is_explorer || is_netscape6_above || mode==MAXIMIZED)
      {  // dynamic HTML for IE/NE6 and new request for Netscape

         if(DEBUG) debug("  Fetching HTML for portlet ["+id+"] from cache. mode="+mode);

         if (std_portlet)
             out.append(html);
         else if(is_explorer || is_netscape6_above)
         {
             out.append("<div id=tab_");
             out.append( id );
             out.append(" style=\"DISPLAY: ", mode==MAXIMIZED ? "block" : "none", "\">\n");

             out.append("<table width=100% border=0 cellpadding=4 cellspacing=0 class=portletBody>");
             out.append("<tbody>\n");
             out.append("<tr>\n");
             out.append("<td class=\"portletBodyText\">\n");

             out.append(html);

             out.append("</td>\n");
             out.append("</tr>\n");
             //if (mgr.getConfigParameter("SCHEME/PORTAL/DOT_LINE","Y").startsWith("Y")){
             //    out.append("<tr><td background=\"",imgloc,"dot_bg.gif\" height=1></td></tr>\n");
             //}
             out.append("</tbody>\n");
             out.append("</table>\n");
             //out.append("</tdiv>\n");
         }
         else
         {
             out.append("<table width=100% border=0 cellpadding=0 cellspacing=0 class=portletBody id=tab_");
             out.append( id );
             out.append(" style=\"DISPLAY: ", mode==MAXIMIZED ? "block" : "none", "\">\n");
             out.append("<tbody>\n");
             out.append("<tr>\n");
             out.append("<td class=\"portletBodyText\">\n");

             out.append(html);

             out.append("</td>\n");
             out.append("</tr>\n");
            // if (mgr.getConfigParameter("SCHEME/PORTAL/DOT_LINE","Y").startsWith("Y")){
            //     out.append("<tr><td background=\"",imgloc,"dot_bg.gif\" height=1></td></tr>\n");
            // }
             out.append("</tbody>\n");
             out.append("</table>\n");
         }
      }

   }

   //--------------------------------------------------------------------------
   //  Customize mode
   //--------------------------------------------------------------------------

   /**
    * Return true if the portal should be shown in customeize - mode.
    * Called also by ASPManager.
    */
   boolean isCustomizeMode()
   {
      if(DEBUG) debug(this+": ASPPortal.isCustomizeMode()");
      return runmode == CUSTOM_MODE;
   }


   /**
    * Generate the HTML code for the customize page.
    */
   private void generateCustomizeHTML( AutoString out ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.generateCustomizeHTML()");

      ASPManager mgr = getASPManager();

      if(portlet==null)
      {
         out.append("<p><p>");
         //out.append("\n</p><table width=\"100%\"><tr>\n");
         generateCustBodyHTML(out);
         //out.append("</tr></table>\n");

         //buttons
         out.append("<p><table width=\"100%\"><tr><td align=left>\n");
         //out.append("&nbsp;&nbsp;&nbsp;\n");
         if (!view_import)
         {
            out.append("<input id=OK name=OK type=button value='",mgr.translateJavaText("FNDPORSAVEEXITBTN: Save & Return"),"'");
            out.append(" onclick=\"javascript:return submitCustomization('",portlet==null?"":portlet.getId(),"')\"");
            out.append(" class='button'");
            out.append(" >\n");
            //out.append( is_explorer ? " WIDTH: 80px;" : "", " TOP: 196px\">\n");
         }

         out.append("<input id=CANCEL name=CANCEL type=button onclick=\"javascript:submit()\" value=",mgr.translateJavaText("FNDPORCANCBTN: Cancel"));
         out.append(" class='button'");
         out.append(" >\n");
         //out.append( is_explorer ? " WIDTH: 80px;" : "", " LEFT: 57px; TOP: 196px\">\n");

         if (!view_import)
         {
            out.append("<input id=CLEARPRF name=CLEARPRF type=button value='",mgr.translateJavaText("FNDPORCLRPROFILEBTN: Clear Profile"));
            out.append("' onclick=\"javascript:clearProfile('",portlet==null?"":portlet.getId(),"')\"");
            out.append(" class='button'");
            out.append(" >\n");
            //out.append( is_explorer ? " WIDTH: 90px;" : "", " LEFT: 57px; TOP: 196px\">\n");
         }
         out.append("&nbsp;&nbsp;&nbsp;\n");

         out.append("<input id=CLEAR name=CLEAR type=button value='",mgr.translateJavaText("FNDPORCLEARBTN: Clear Column"));
         out.append("' onclick=\"javascript:clearColumn();\" class='button'");
         out.append(" >\n");
         //out.append( is_explorer ? " WIDTH: 100px;" : "", " LEFT: 57px; TOP: 196px\">\n");

         out.append("<input id=CLEAR_ALL name=CLEAR_ALL type=button value='",mgr.translateJavaText("FNDPORCLEARALLBTN: Clear All"),"'");
         out.append(" onclick=\"javascript:clearAllColumns();\" class='button'");
         out.append(" >\n&nbsp;&nbsp;\n");
         //out.append( is_explorer ? " WIDTH: 80px;" : "", " LEFT: 57px; TOP: 196px\">\n&nbsp;&nbsp;\n");


         out.append("</td></tr></table>\n");

         out.append("<script language=javascript src=");
         out.append(getASPManager().getASPConfig().getScriptsLocation()+"PortalConfig.js");
         out.append(" ></script>\n");
      }
      else
      {
         boolean can_customize = portlet.canCustomize();

         if (std_portlet)
            setStdPortletTitle(mgr.HTMLEncode(portlet.getTitle(CUSTOM)));
         
         if(!getASPManager().getPortalPage().getASPProfile().isUserProfileDisabled())
         {
            if (!can_customize)
            {
               out.append("<p>");
               out.append("<FONT class=normalTextHeading>");
               out.append(mgr.translateJavaText("FNDPORCANTCUST: This portlet can not be customized."),"<br>\n");
               out.append("</font>");
               out.append("</p>");
            }
            else
            {
               portlet.runCustom();
               portlet.getCustomBody(out);
            }
         }
         else
         {
               out.append("<p>");
               out.append("<FONT class=normalTextHeading>");
               out.append(mgr.translateJavaText("FNDPORNOTALWCUST: Current User is not allowed to customize."),"<br>\n");
               out.append("</font>");
               out.append("</p>");
         }

         out.append("<p>&nbsp;&nbsp;&nbsp;\n");

         String id = portlet==null?"":portlet.getId();
        if(!getASPManager().getPortalPage().getASPProfile().isUserProfileDisabled())
        {
            if (can_customize)
            {
               if (!view_import)
               {
                  out.append("<input id=OK name=OK type=button value=",mgr.translateJavaText("FNDPOROKBTN: OK"));
                  out.append(" onclick=\"javascript:");
                  if(!Str.isEmpty(id))
                     out.append("setFormReference('",id,"');");
                  out.append(" return submitCustomization('",id,"')\"");

                  out.append(" class='button'");
                  out.append(" >\n&nbsp;\n");
                  //out.append( is_explorer ? " WIDTH: 80px;" : "", " TOP: 196px\">\n&nbsp;\n");
               }

               out.append("<input id=CANCEL name=CANCEL type=button onclick=\"javascript:");
               if(!Str.isEmpty(id))
                  out.append("setFormReference('",id,"');");
               out.append("submit()\" value=",mgr.translateJavaText("FNDPORCANCBTN: Cancel"));
               out.append(" class='button'");
               out.append(" >\n&nbsp;\n");
               //out.append( is_explorer ? " WIDTH: 80px;" : "", " LEFT: 57px; TOP: 196px\">\n&nbsp;\n");

               if (!view_import)
               {
                  out.append("<input id=CLEARPRF name=CLEARPRF type=button value=",mgr.translateJavaText("FNDPORCLRBTN: Default"));
                  out.append(" onclick=\"javascript:");
                  if(!Str.isEmpty(id))
                     out.append("setFormReference('",id,"');");
                  out.append(" clearProfile('",id,"')\"");

                  out.append(" class='button'");
                  out.append(" >\n");
                  //out.append( is_explorer ? " WIDTH: 80px;" : "", " LEFT: 57px; TOP: 196px\">\n");
               }
            }
            else
            {
               out.append("<input id=OK name=OK type=button onclick=\"javascript:");
               if(!Str.isEmpty(id))
                  out.append("setFormReference('",id,"');");
               out.append("submit()\" value=",mgr.translateJavaText("FNDPOROKBTN: OK"));
               out.append(" class='button'");
               out.append(" >\n");
            }
        }
        else
        {
               out.append("<input id=OK name=OK type=button onclick=\"javascript:");
               if(!Str.isEmpty(id))
                  out.append("setFormReference('",id,"');");
               out.append("submit()\" value=",mgr.translateJavaText("FNDPOROKBTN: OK"));
               out.append(" class='button'");
               out.append(" >\n");
        }

      }
      //to avoid submitting when u have only one text field and press enter.
      if (!mgr.isNetscape4x())
      {
         // javascript error in NN4 cant find hidden fields after a div.
         out.append("<div style=\"position:relative; visibility:hidden\" >\n");
         out.append("<input type=\"text\" size=\"10\">\n");
         out.append("</div>\n");
      }


   }


   private void generateWideColumnBox(ASPPortalColumn col, int pos, int span)
   {
      ASPManager mgr = getASPManager();

      if (mgr.isNetscape4x())
      {
         out.append("<select onfocus=\"javascript:selectColumnNN4("+pos+",0)\" name=col_");
         out.appendInt(pos);
         out.append(" size=3 style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px;\">");
      }
      else
      {
         int col_width = 0;
         if (mgr.isExplorer())
            col_width = 140*span;
         else
            col_width = 144*span;

         out.append("<select onclick=selectColumn(this,"+pos+",event) name=col_");
         out.appendInt(pos);

         out.append(" size=5 class=portalConfigInactivePortletBox style=\"width:"+col_width+";\">\n");
      }

      if (col != null)
      {
         for(int p=0; p<col.countPortlets(); p++)
         {
            try
            {
               ASPPortletHandle ph = col.getPortletHandle(p);
               //Bug 40931, start
               if(!ph.isUserAvailable(this)) continue;
               //Bug 40931, end

               out.append("<option value=");
               Buffer all = getAvailablePortlets(mgr);
               Item item = all.getItem(ph.getClassName());
               out.append(item.getType(),".",ph.getId());

               //Bug 40931, start
               String configpagetitle = (!mgr.isEmpty(ph.getMaxTitle(this)))?ph.getMaxTitle(this):item.getString();
               //Bug 40931, end
               out.append(">",mgr.HTMLEncode(mgr.translateJavaText(configpagetitle)),debugRef(ph),"</option>\n");
            }
            catch( Exception any )
            {
               error(any);
            }
         }
      }
      String wide_str = "________________";

      for (int j=0; j<span; j++)
         wide_str += "_________________";

      out.append("<option>",wide_str,"</option>\n");
      out.append("</select>\n");
      //if (mgr.isExplorer())
      //   out.append("</div>\n");
      if (mgr.isNetscape4x())
         out.append("<br>",drawRadioButton("FNDPORNNMULTI: Multi column","ACT_COL",""+pos,false,"onClick=\"javascript:selectColumnNN4(this.value,0)\"",0));

      out.append("</td>\n<input type=hidden name=col"+pos,"width value="+profile.MULTI_WIDTH+" >"); //any value greater than 288
      out.append("</tr>");

   }

   /**
    * Generate HTML code for customization of one column
    */
   private void generateCustomBox( ASPPortalColumn col, int pos, int maxpos, int nn4ind )
   {
      if(DEBUG) debug(this+": ASPPortal.generateCustomBox("+col+")");

      ASPManager mgr = getASPManager();

      out.append("<td>");
      //if (mgr.isExplorer())
      //   out.append("<div class=form_col >");
      if (mgr.isNetscape4x())
      {
         out.append("<select class=normalTextValue onfocus=\"javascript:selectColumnNN4("+pos+","+nn4ind+")\" name=col_");
         out.appendInt(pos);
         if (profile.isWideColumnAvailable())
            out.append(" size=8 style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");
         else
            out.append(" size=12 style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");

      }
      else
      {
         out.append("<select class=portalConfigInactivePortletBox onclick=selectColumn(this,"+pos+",event) name=col_");
         out.appendInt(pos);
         if (profile.isWideColumnAvailable())
            out.append(" size=11 >\n");
            //out.append(" size=13 style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");
         else
            out.append(" size=16 >\n");
            //out.append(" size=19 style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");
      }

      for(int p=0; p<col.countPortlets(); p++)
      {
         try
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;
            //Bug 40931, end

            out.append("<option value=");
            Buffer all = getAvailablePortlets(mgr);
            Item item = all.getItem(ph.getClassName());
            out.append(item.getType(),".",ph.getId());

            //Bug 40931, start
            String configpagetitle = (!mgr.isEmpty(ph.getMaxTitle(this)))?ph.getMaxTitle(this):item.getString();
            //Bug 40931, end
            out.append(">",mgr.HTMLEncode(mgr.translateJavaText(configpagetitle)),debugRef(ph),"</option>\n");
         }
         catch( Exception any )
         {
            error(any);
         }
      }

      out.append("<option>__________________</option>\n");
      out.append("</select>\n");
      //if (mgr.isExplorer())
      //   out.append("</div>\n");

      out.append("<input type=hidden name=col");
      out.appendInt(pos);
      out.append("width value=");
      out.appendInt(col.getWidth());
      out.append(" >");
      out.append("<input type=hidden name=col");
      out.appendInt(pos);
      out.append("type value=");
      if(col.isDynamic())
         out.append("TRUE");
      else
         out.append("FALSE");
      out.append(" >\n");

      if (mgr.isNetscape4x())
         out.append("<br>",drawRadioButton("FNDPORNNCOL: column "+(profile.isWideColumnAvailable()?nn4ind:pos+1),"ACT_COL",""+pos,false,"onClick=\"javascript:selectColumnNN4(this.value,"+nn4ind+")\"",nn4ind));
      out.append("</td>\n");

   }

   //==========================================================================
   //  Client Script methods
   //==========================================================================

   void prepareClientScript() throws FndException,IOException
   {
      if(DEBUG) debug(this+": ASPPortal.prepareClientScript()");

      int i = slim_mode ? 1 : profile.countColumns();
      for(int c=0; c<i; c++)
      {
         ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;

            ASPPortletProvider prv = ph.getPortlet(this);
            //Bug 40931, end
            if(prv!=null)
               prv.prepareClientScript();
         }
      }
   }

   void appendPageClientScript( AutoString buf ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.appendPageClientScript()");

      //add ONLY this portlet's .js when in Custom mode.
      if( runmode==CUSTOM_MODE && portlet != null)
      {
         appendPortletClientScript(buf);
         return;
      }

      Buffer classes = cfg.getFactory().getBuffer();

      int i = slim_mode ? 1 : profile.countColumns();
      for(int c=0; c<i; c++)
      {
         ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;

            ASPPortletProvider prv      = ph.getPortlet(this);
            AutoString         clscript = ph.getClientScript(this);
            //Bug 40931, end
            String             clsname  = ph.getClassName();

            if(prv!=null)
            {
               clscript.clear();
               prv.appendPageClientScript(clscript);
            }

            if(classes.findItem(clsname)==null)
            {
               classes.addItem(new Item(clsname));
               buf.append(clscript);
            }
         }
      }
   }

   private void appendPortletClientScript( AutoString buf ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.appendPortletClientScript()");

      if ( portlet != null )
      {
         String clurl = portlet.getURL();
         clurl = clurl.substring(0,clurl.lastIndexOf("."));
         clurl = clurl + getASPPage().getLanguageSuffix() + ".js";

         String docache = cfg.getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N");

         if ("Y".equals(docache))
         {
          String appl_path =  cfg.getApplicationPath();
          if (!Str.isEmpty(appl_path))
            clurl = Str.replaceFirst(clurl, appl_path, appl_path + "/" +DynamicObjectCache.URL_INDICATOR );
          else
            clurl = "/" + DynamicObjectCache.URL_INDICATOR + clurl;
         }

         if( cfg.debugSendJS() )
         {
            ASPPage page = getASPPage();

            buf.append("\n<!-- including JavaScript file: "+clurl+" -->");
            buf.append(ASPPage.BEGIN_SCRIPT_TAG);

            String jsfile ="";
            if ("Y".equals(docache))
            {
                DynamicObject dobj = DynamicObjectCache.get(portlet.getScriptCacheKey(), getASPManager().getAspSession());
                jsfile = (String)dobj.getData();
            }
            else
            {
                int len = clurl.indexOf(page.getApplicationPath()) + page.getApplicationPath().length()+1;
                String path = clurl.substring(len);
                if(DEBUG) debug("  path="+path);
                String phypath = getASPManager().getPhyPath(path);
                if(DEBUG) debug("  phypath="+phypath);
                jsfile = Util.readAndTrimFile(phypath);
            }
            buf.append(jsfile,ASPPage.END_SCRIPT_TAG);
         }
         else
         {
            buf.append(ASPPage.SRC_SCRIPT_TAG_LEFT);
            buf.append(clurl);
            buf.append(ASPPage.SRC_SCRIPT_TAG_RIGHT);
         }
      }
   }

   void appendDirtyClientScript( AutoString buf ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.appendDirtyClientScript()");

      if(runmode==CUSTOM_MODE && portlet==null) return;

      int i = slim_mode ? 1 : profile.countColumns();
      for(int c=0; c<i; c++)
      {
         ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;

            ASPPortletProvider prv = ph.getPortlet(this);
            AutoString drscript = ph.getDirtyScript(this);
            //Bug 40931, end
            if(prv!=null)
            {
               drscript.clear();
               prv.appendDirtyClientScript(drscript);
            }
            if(runmode!=CUSTOM_MODE || prv!=null)
               buf.append(drscript);
         }
      }
   }

   /**
    * Calls generatePopupClickScript() on all portlets.
    */
   void generatePopupClickScript( AutoString buf )
   {
      if(DEBUG) debug(this+": ASPPortal.generatePopupClickScript()");

      int i = slim_mode ? 1 : profile.countColumns();
      for(int c=0; c<i; c++)
      {
         ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;

            ASPPortletProvider prv = ph.getPortlet(this);
            AutoString ppupscr = ph.getPopupScript(this);
            //Bug 40931, end
            if(prv!=null)
            {
               ppupscr.clear();
               prv.generatePopupClickScript(ppupscr);
            }
            buf.append(ppupscr);
         }
      }
   }


   /**
    * Calls generatePopupDefinitions() on all portlets.
    */
   void generatePopupDefinitions( AutoString buf )
   {
      if(DEBUG) debug(this+": ASPPortal.generatePopupDefinitions()");

      int i = slim_mode ? 1 : profile.countColumns();
      for(int c=0; c<i; c++)
      {
         ASPPortalColumn col = slim_mode ? slimcol : profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            //Bug 40931, start
            if(!ph.isUserAvailable(this)) continue;

            ASPPortletProvider prv = ph.getPortlet(this);
            AutoString ppupdef = ph.getPopupDefinitions(this);
            //Bug 40931, end
            if(prv!=null)
            {
               ppupdef.clear();
               prv.generatePopupDefinitions(ppupdef);
            }
            buf.append(ppupdef);
         }
      }
   }

   //==========================================================================
   //  Command Bar & Popup functions
   //==========================================================================

   private boolean parseCommandBarFunction() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortal.parseCommandBarFunction()");

      ASPManager mgr = getASPManager();

      String key = mgr.readValue("__COMMAND");
      if( Str.isEmpty(key) ) return false;

      int pos1 = key.indexOf('.');
      int pos2 = -1;
      if(pos1>=0)
         pos2 = key.indexOf('.',pos1+1);

      if(pos2<0)
         throw new FndException("FNDPORPARSEERR: Can not parse command bar function");

      String prefix = key.substring(0,pos1);
      portletid  = ASPPortletProvider.parseProviderPrefix(prefix);
      cmdblkname = key.substring(pos1+1,pos2);
      cmdstdid   = key.substring(pos2+1);
      cmdcustid  = null;

      if( ASPCommandBar.PERFORM.equals(cmdstdid) )
         cmdcustid = mgr.readValue(prefix+"__"+Str.nvl(cmdblkname,"")+"_"+ASPCommandBar.PERFORM);

      if(DEBUG) debug("  parseCommandBarFunction()"+
                      "\n\t  portletid  = "+portletid+
                      "\n\t  cmdblkname = "+cmdblkname+
                      "\n\t  cmdstdid   = "+cmdstdid+
                      "\n\t  cmdcustid  = "+cmdcustid);
      return true;
   }


   String getCmdBarBlockName()
   {
      return cmdblkname;
   }


   String getCmdBarStdId()
   {
      return cmdstdid;
   }


   String getCmdBarCustId()
   {
      return cmdcustid;
   }


   boolean commandBarActivated()
   {
      return !Str.isEmpty(cmdstdid);
   }


   boolean cmdBarStandardCommandActivated()
   {
      return !Str.isEmpty(cmdstdid) && !ASPCommandBar.PERFORM.equals(cmdstdid) && Str.isEmpty(cmdcustid);
   }


   boolean cmdBarCustomCommandActivated()
   {
      return ASPCommandBar.PERFORM.equals(cmdstdid) && !Str.isEmpty(cmdcustid);
   }

   boolean isPortletMaximized(String id)
   {
       return (getASPManager().isEmpty(minboxes) || minboxes.indexOf(","+id+",")==-1);
   }

   //==========================================================================
   //  Other functions
   //==========================================================================

   /**
    * Returns the width of the portal column for the specified portlet id.
    */
   int getColumnWidth( String id )
   {
      id = id.toUpperCase(); //NOTE: id is uppercase anyway (safety)
      for(int c=0; c<profile.countColumns(); c++)
      {
         ASPPortalColumn col = profile.getColumn(c);
         for(int p=0; p<col.countPortlets(); p++)
         {
            ASPPortletHandle ph = col.getPortletHandle(p);
            String ph_id = ph.getId().toUpperCase(); //ph.getId() is lowercase

            if( id.equals(ph_id) )
               return col.getWidth();
         }
      }
      return -1;
   }

   boolean isSubmitMode()
   {
      return runmode == SUBMIT_MODE;
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   private String debugRef( ASPPortletHandle ph )
   {
      if( DEBUG && ph!=null )
      {
         //Bug 40931, start
         ASPPortletProvider prv = ph.getPortlet(this);
         //Bug 40931, end
         return "("+ph.getId()+")["+(prv!=null?Integer.toHexString(prv.hashCode()):"-")+"]";
      }
      else
         return "";
   }


   private String debugRef( ASPPortletProvider prv )
   {
      if( DEBUG && prv!=null )
      {
         return "("+prv.getId()+")["+Integer.toHexString(prv.hashCode())+"]";
      }
      else
         return "";
   }

   private String drawCheckbox( String name, String value, String label, boolean checked, String tag )
   {
      String check = "";
      if(checked)
         check = " CHECKED ";
      AutoString out = new AutoString();

      out.append("<input class='checkbox' type=checkbox name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + " " + check + ">&nbsp;");

      if (!Str.isEmpty(label))
      {
         out.append("<a class=normalTextLabel href=\"javascript:_selectItem();");
         out.append("function _selectItem(){f."+name+".checked=(!f."+name+".checked==true);");
         if ( tag != null && (tag.indexOf("=") > -1))
         {
            String event = tag.substring(0,tag.indexOf("="));
            event = event.trim();
            out.append("f."+name+"."+event.toLowerCase()+"();");
         }
         out.append("}\">",getASPManager().translate(label),"</a>");
      }
      else
         out.append(getASPManager().translate(label));

      return out.toString();
   }


   private String drawRadioButton( String label, String name, String value, boolean checked, String tag, int index)
   {
      AutoString out = new AutoString();
      out.append("<input class=radioButton type=radio");
      out.append(" name=\"",name);
      out.append("\" value=\"" + value + "\"");
      if ( checked )
         out.append(" CHECKED ");
      if ( tag != null )
         out.append(tag);

      if (index > -1)
      {
         out.append(">&nbsp;","<a class=normalTextLabel href=\"javascript:_selectItem();");
         out.append("function _selectItem(){var elem; if (f."+name+"["+index+"]) elem= f."+name+"["+index+"]; else elem= f."+name+"; elem.checked=true;");
         if ( tag != null && (tag.indexOf("=") > -1))
         {
            String event = tag.substring(0,tag.indexOf("="));
            event = event.trim();
            out.append("elem."+event.toLowerCase()+"();");
         }
         out.append("}\">",getASPManager().translate(label),"</a>");
      }
      else
         out.append(">",getASPManager().translate(label));

      return out.toString();
   }

   boolean isStdPortlet()
   {
      return std_portlet;
   }

   String getActionURL()
   {
      return (String)getASPManager().getAspRequest().getAttribute("renderURL");
   }

   private void setStdPortletTitle(String title)
   {
      if (Str.isEmpty(title)) return;

      //TODO: mapelk - method body comented due to a bug in WebSphere
      //ASPManager mgr = getASPManager();
      //RenderResponse render_response = mgr.getRenderResponse();
      //render_response.setTitle(title);
   }

}

