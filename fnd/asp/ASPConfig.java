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
 * File        : ASPConfig.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-Feb-04 - Created
 *    Jacek P  1998-May-08 - Removed public method findAttribute().
 *    Marek D  1998-Jul-08 - Trim spaces from the end of each parameter value
 *    Jacek P  1998-Jul-17 - One instance of ASPConfig per global.asa stored
 *                           in a hash table (Log id: #78). Some functions
 *                           moved from ASPManager.
 *    Jacek P  1998-Jul-22 - Removed 'throw Exception' from getParameter() with
 *                           default value.
 *    Jacek P  1998-Jul-23 - Using of Util.readAndTrimFile()
 *    Marek D  1998-Jul-24 - Use the standard server date mask from IfsNames
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-13 - Added function getDefaultBufferSize()
 *    Jacek P  1998-Aug-18 - Restructured (Log id:#2623). New syntax of IFM-file.
 *                           New classes ASPConfigParameters and ASPConfigAdmin.
 *    Jacek P  1998-Aug-27 - Better logging and traceing of parameters.
 *    Jacek P  1998-Oct-19 - Added handling of include files (Todo: #2738).
 *    Marek D  1998-Oct-29 - Added new parameter DECIMAL_SEPARATOR (ToDo #2836)
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *                           Classes ASPConfigParameters and ASPConfigAdmin
 *                           now extend ASPPageSubElement.
 *                           Removed calls to Scr class.
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-03 - Help classes ASPConfigAdmin and ASPConfigParameters
 *                           removed. Restructured.
 *    Jacek P  1999-Mar-08 - Introducet new class ASPConfigFile.
 *    Marek D  1999-Mar-19 - Added new get() methods
 *    Jacek P  1999-Apr-01 - Added methods for handling of user authorization.
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-May-04 - Added static methods for parameters controling
 *                           behavior of cache and pool.
 *    Marek D  1999-May-12 - Added getLOVTag1(boolean) and getLOVTag2(boolean)
 *                           Added getConfigUser()
 *    Jacek P  1999-Jul-13 - Added function getScriptsLocation().
 *    Jacek P  1999-Jul-21 - Added function getAppletTag().
 *    Jacek P  1999-Aug-02 - Added function debugSendJS().
 *    Jacek P  1999-Aug-10 - Added new function removeOldContext().
 *    Jacek P  1999-Sep-08 - Added functions isHoverEnabled() and getHoverScript()
 *                           called from ASPManager.generateHeadTag().
 *                           Added function addErrorInfoEnabled().
 *    Stefan M 1999-Dec-21 - Added function getDynamicLOVURL().
 *    Stefan M 2000-Feb-16 - Added getHeader() and getFooter().
 *    Stefan M 2000-Mar-29 - Removed getHeader() and getFooter(). No need, since they are now simple pictures.
 *    Jacek P  2000-Aug-03 - Added static parameters for Graphic Server
 *    Stefan M 2000-Sep-25 - getLateConstantClientscript() added.
 *    Jacek P  2001-mar-22 - Log id 660 & 661: Added handling of TM. Added TM_* constants
 *                           and method getTMMethod().
 *                           getProtocol() changed to public.
 *    Chaminda 2001-May-18 - Replaced '\' with ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING
 *    Upul P   2001-Nov-26 - Added ip masked filtering for GZIP functionality
 *    Daniel S 2002-Jan-07 - Added function to get cached banner height.
 *    Ramila H 2002-Jan-14 - Added functions to get localized address class and templates.
 *    Daniel S 2002-Jan-14 - Added function to get system name.
 *    Daniel S 2002-Feb-05 - Disabled the formatter pool due to synchronization problems.
 *    Mangala  2002-Mar-12 - Introduced a new method isBase64Enabled().
 *    Rifki R  2002-Apr-08 - Fixed translation problems caused by trailing spaces.
 *    ChandanaD2002-Jun-18 - Added new method 'getPageMaskTag()'.
 *    ChandanaD2002-Jun-20 - Added new method InactivatePageAtLoading().
 *    Chandana 2002-Jul-18 - Added new method isBrowserCacheDisabled().
 *    Chandana 2002-Jul-22 - Added new methods isBrowserHistoryRestricted().
 *    Ramila   2002-Jul-30 - Added functions to return Calender tags. Log Id 875.
 *    Daniel S 2002-Aug-08 - Removed methods for the old context cache. log 865
 *    Chandana 2002-Aug-30 - Added dataStoresAvailable method.
 *    Ramila H 2002-Ocr-04 - added support for Debug tool.
 *    ChandanaD2002-Oct-21 - Added facility to Enable/Disable action buttons in standard tool bars.
 *    Rifki R  2002-Nov-05 - Added addDicItem(). Related to Log id 905
 *    Suneth M 2002-Dec-10 - Log id 1002, Added new method isMultiChoiceLovEnabled().
 *    ChandanaD2002-Dec-13 - Sets Grouping(Thousand) Separator.
 *    Suneth M 2002-Dec-30 - Log id 1002, Added new methods isFindModeIidToLovEnabled() & getDynamicIidLOVURL().

 *    Mangala  2003-Jan-01 - Log id 595, Common message display.
 *    Sampath  2003-Jan-10 - made changes to remove the clientutil applet if necessory
 *    Sampath  2003-Jan-20 - Inserted the method getTranslatedDateTimeMask() to get the translated date time mask.
 *    Jacek P  2003-Feb-04 - Handling of config dir instead of file.
 *    Mangala  2003-Feb-14 - getParameter key is no longer case sensitive.
 *    Jacek P  2003-Feb-28 - Changed FNDAS to FNDEXT. Removed system_name.
 *                           Renamed transactions methods to JAP (old ORB) and RMI (old NATIVE).
 *                           Added functions convertPath().
 *    Mangala  2003-Mar-26 - Added support to enable ClientUtil Applet in pages regardless of config
 *                           parameter.
 *    Sampath  2003-Apr-17 - add parameters to enable more_info in error pages
 *    ChandanaD2003-Jun-03 - Added isOptionsEnabled() method.
 *    ChandanaD2003-Jun-03 - Added getLOVTag2Pop( boolean in_assignment ) method.
 *    Ramila H 2003-jul-18 - Log id 1119, Authentication by fndext.
 *    Ramila H 2003-Aug-06 - Log id 853, added method to check if Change password is enabled.
 *    Sampath  2003-Aug-13 - add new function getLOVTag1Iid() for Iid and portlet lovs
 *    Ramila H 2003-Sep-08 - changed access modifer of method getLogonURL to public.
 *    Ramila H 2003-Sep-18 - Log id 1044, Added method isQueryHintsOn.
 *    ChandanaD2004-Jan-27 - Bug 42299. Removed removeApplet() and added getIEValidationMethod(),getNetscape7ValidationMethod().
 *    ChandanaD2004-May-12 - Updated for the usage of new style sheets.
 *    Ramila H 2004-05-26  - implemented code for multi-language support
 *    Ramila H 2004-05-27  - fixed bug regarding mask and language and made
 *                           DECIMAL_SEPARATOR, GROUP_SEPARATOR and WEEK_START_DATE language dependent
 *    Ramila H 2004-05-28  - Bug id 42390. Avoided setting group separator if 2003 application
 *    Ramila H 2004-06-29  - Moved translated stuff from ASPConfigFile to support multi-language
 *    Chandana 2004-Jul-27 - Further improvements to work with proxy servers.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Mangala  2004-Nov-09 - Changes done for SSO support.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/10/13 buhilk Bug Id 93561, Implemented IFS EE links to be open from Federated search results.
 * 2010/05/07 buhilk Bug id 90507, Added new method isDockoutEnabled()
 * 2010/04/20 buhilk Bug id 90137, Portal and Portlet Configuration for End User Roles with PO. Modified isPortalConfigEnabled() and isPortletConfigEnabled()
 * 2010/02/18 buhilk Bug id 88106, support for Federated search through windows-7 UI
 * 2010/02/03 sumelk Bug 88035, Added new moethod getCookiePathDomain(). 
 * 2009/03/11 sumelk Bug 80503, Added new methods isLogoffEnabled() & getLogoffScript().  
 * 2009/02/05 amiklk Bug 80053, Modified getConfigClientScript() to add REM_PORTLET_MSG
 * 2008/08/01 rahelk Bug 74809, Added method to return tiff image timeout
 * 2008/07/15 sadhlk Bug id 74364, Modified getConfigClientScript() to add __MAND_FIELDS_MOVE_MSG. 
 * 2008/04/09 sadhlk Bug id 67895, Added isHybridSearchAvailable()
 * 2008/04/04 buhilk Bug id 72852, Modified getConfigClientScript() to print IS_RWC_HOST js variable.
 * 2007/12/12 sadhlk Bug id 67525, Added getCookiePathPrefix() and getCookieRootPath() to support cookie renaming.
 * 2207712/11 buhilk IID F1PR1472, fixed a bug in default buffer size.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/10/17 sadhlk Merged Bug id 67865, Modified getConfigClientScript() to add a Page Expire warning message.
 * 2007/09/12 sadhlk Set the Access Modifier to public in the getConfigUser(), getConfigPassword() methods
 * 2007/08/23 sadhlk Bug id 67401, Modified getConfigClientScript().
 * 2007/07/18 sumelk Merged the corrections for Bug 66481, Changed getConfigClientScript() to add 
 *                   a warning message for case sensitive checkbox. 
 * 2007/05/22 sumelk Merged the corrections for Bug 65469, Added new method getAuthHeaderName(). 
 * 2007/05/16 rahelk Call id 143888, fixed proxy related bugs
 * 2007/05/04 buhilk Added translations for broadcast message popup window.
 * 2007/03/27 buhilk Modified getConfigClientScript() to insert error message for attempting to hide mandatory fields.
 * 2007/02/19 sadhlk Bug 63561, Modified getThemeData().
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/05 buhilk Changed common messages name to bradcast messages.
 * 2006/12/08 buhilk  Bug id  61535, Deprecated the methods getCommonMsgFilePath() & isCommonMsgPathDefined() 
 *                                   as the common messages were moved into the DataBase.
 * 2006/11/22 rahelk Bug 61532, Added method to return number of precisions for accurate fields
 * 2006/11/13           buhilk
 * Bug id 61819, Added a javascript constant for the Help Message for "Whats this" function when the usage id is null
 *
 * 2006/09/29 gegulk 
 * Bug id 58618, Modified the class as tranlations are fetched from the Database
 * Added the methods getTranslation(),getUsageVersionID(), modified the method addDicItem() &
 * Depricated the methods getDicValue(),getUsageID().
 *
 * 2006/09/13 gegulk 
 * Bug id 60473, Added the method isRTL() and modified the methods getImagesLocation() & 
 *               getNewHoverScript() to support RTL mode (Right To Left)
 *
 * 2006/08/07 buhilk
 * Bug 59442, Corrected javascript translations
 *
 * 2006/05/16 mapelk Improved "What's This?" functionality to show help as a tool tip
 *                2006/05/15 prralk
 * Bug 57841 - Used intern memory
 *                2006/04/20 prralk
 * Bug 57417 performance issue with default language extraction
 *                2006/04/20 prralk
 * Bug 57026 'Help On Fields' display issues
 *                2006/01/20 riralk
 * Added method getTermDisplayName() for "Help on Fields" functionality.
 *
 *                2005/12/16 rahelk
 * Call id 129688 - Added INVALIDQUERYNAME_MSG
 *
 *                2005/12/09 rahelk
 * Error handling on JAAS logon
 *
 *                2005/11/23 mapelk
 * Bug fix: Wrong date validation when profile mask taken.
 * Revision 1.9  2005/11/08 07:50:54  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.8  2005/11/06 08:47:06  mapelk
 * Header/Footer Stretch image size no longer taken from Java
 *
 * Revision 1.7  2005/11/06 05:51:04  mapelk
 * "Data Expired" message is given for all authentication methods when context is missing.
 *
 * Revision 1.6  2005/10/25 11:06:13  mapelk
 * Introduced different validations for Number & Money. Also replaces ASCII 160 with 32 which returns as group seperator for some languages.
 *
 * Revision 1.5  2005/10/19 10:16:32  mapelk
 * Added functionality to refresh profile cache in certain time. Also posibility to flush the cache on demand.
 *
 * Revision 1.4  2005/10/14 09:08:13  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.3  2005/10/06 09:52:32  rahelk
 * moved getSecurityConfigRealm to AS specific class
 *
 * Revision 1.2  2005/09/19 08:47:22  rahelk
 * Simplified login to complex LDAP structure
 *
 * Revision 1.14  2005/09/14 08:02:07  rahelk
 * password management
 *
 * Revision 1.13  2005/08/26 12:32:18  riralk
 * Modified getDataFormatter() to retrieve format masks from Java Locale when empty.
 *
 * Revision 1.12  2005/08/08 09:44:03  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.11  2005/06/27 05:01:08  mapelk
 * Bug fixes for std portlets
 *
 * Revision 1.10  2005/06/13 10:28:51  mapelk
 * Use two static files instead of generating unautherised msg and ifs-dialog files for each language.
 *
 * Revision 1.9  2005/04/01 13:59:56  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.8  2005/04/01 10:51:57  rahelk
 * JAAS: implemented calling EJB's directly
 *
 * Revision 1.7  2005/03/21 09:19:04  rahelk
 * JAAS implementation
 *
 * Revision 1.6  2005/02/11 09:12:09  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.5  2005/02/08 09:38:26  mapelk
 * Remove application path form lov tags
 *
 * Revision 1.4  2005/02/03 11:12:58  mapelk
 * Remove application path form lov tags
 *
 * Revision 1.3  2005/02/02 09:36:26  mapelk
 * bug fix: Lov script error and image generation bug appeared after proxy related changes.
 *
 * Revision 1.2  2005/02/01 10:32:58  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.3  2005/01/12 12:06:45  mapelk
 * Moved the config_user entry to <admin>. Now we can have one config_user for all alnguages.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.util.*;
import java.io.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Common ASP class to handle ASP configuration file. The only instance of this class
 * is created in global.asa within the scope of the aplication.
 */
public class ASPConfig extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPConfig");

   static final int TM_JAP    = 1;
   static final int TM_RMI    = 2;
   static final int TM_SOCKET = 3;

   private static final int VAL    = 1;
   private static final int VALDEF = 2;

   private static final Object EMPTY = new Object();

   //==========================================================================
   // Instance variables
   //==========================================================================

   private ASPConfigFile   configfile;
   private Hashtable       parameters;
   private Hashtable       fmttable;
   private ServerFormatter srv_fmt;
   private ASPLog          asplog;

   //==========================================================================
   // Temporary variables
   //==========================================================================

   private AutoString tmpbuf = new AutoString();

   //==========================================================================
   // Construction and initialization
   //==========================================================================

   /**
    */
   ASPConfig( ASPPage page )
   {
      super(page);
      this.fmttable = new Hashtable();
      this.asplog = page.getASPLog();
   }


   /*
    * The only way to create an instance of ASPConfig.
    *
    * For each global.asa there is only one instance of config buffer stored in
    * the static hash table 'configbuffers'.
    * Each instance of ASPPage has its own instance of ASPConfig.
    * Each instance of ASPConfig has its own hash table with read parameters.
    * The hash table is cloned with the standard clone() method while cloning
    * the instance of ASPConfig, which means that the new, cloned instance
    * refers to the same keys (strings) and values (strings and read-only buffers
    * which are all the sub-buffers of the only one buffer stored in 'configbuffers').
    *
    * The variable 'reread' force to reload the configuration file from the disk.
    */



   void construct() throws FndException
   {
      if (DEBUG) debug("ASPConfig.construct()");
      String cfgdir = getASPManager().getASPConfigFileDir();

      synchronized(getClass())
      {
         configfile = ASPConfigFile.getDir(cfgdir);
         if( !configfile.initialized ) configfile.construct(getASPPage());
      }
      parameters = (Hashtable)configfile.params.clone();
      srv_fmt    = (ServerFormatter)configfile.srv_fmt.clone();
   }

   /**
    * Private constructor called from clone()
    */
   private ASPConfig( ASPPage page, ASPConfig config ) throws FndException
   {
      super( page );
      this.configfile = config.configfile;
      this.parameters = (Hashtable)config.parameters.clone();
      //this.fmttable   = (Hashtable)config.fmttable.clone();
      this.fmttable   = new Hashtable();
      this.srv_fmt    = (ServerFormatter)config.srv_fmt.clone();
      this.asplog     = page.getASPLog();
   }


   boolean isInitialized()
   {
      return configfile!=null;
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#freeze
    */
   protected void doFreeze() throws FndException
   {
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
   }


   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      ASPConfig c = new ASPConfig((ASPPage)page, this);
      c.setCloned();
      return c;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
      asplog.verifyPage(page);
   }


   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   public String getName()
   {
      return configfile==null ? "" : configfile.param_file_dir;
   }

   //==========================================================================
   // Parameters and Sub-Buffers private help functions
   //==========================================================================

   private String replaceMacroReferences( String text ) throws FndException
   {
      if ( Str.isEmpty(text) ) return "";
      if (DEBUG) debug("ASPConfig: Replacing macros in '"+text+"'.");

      String old = text; // only for debug

      int i;
      while ( (i = text.indexOf("&(") ) >= 0 )
      {
         int j = text.indexOf(")", i+2);
         if ( j<0 )
            throw new FndException("FNDCFGSYNE: Syntax error in configuration file for value '&1'.",text);

         String repl = (String)this.parameters.get( text.substring(i+2,j) );

         text = text.substring(0,i)+repl+text.substring(j+1);
      }
      if (DEBUG && !old.equals(text) )
            debug("ASPConfig: String with macros '"+old+"' replaced with '"+text+"'.");
      return text;
   }


   private String getParameter( int type, String name, String  default_value, boolean for_js)
   {
      try
      {
         ASPManager mgr = getASPManager();
         name = name.toUpperCase();

         if("APPLICATION/LANGUAGE".equals(name))
             return mgr.getUserLanguage();
         if("APPLICATION/PROTOCOL".equals(name))
             return getProtocol();
         if ("APPLICATION/DOMAIN".equals(name))
             return getApplicationDomain();
         if ("APPLICATION/PATH".equals(name))
            return getApplicationPath(for_js);
         if ("APPLICATION/LOCATION/ROOT".equals(name))
            return getRootLocation(for_js);
         if ("APPLICATION/LOCATION/IMAGES".equals(name))
            return getImagesLocation(for_js);
         if ("APPLICATION/LOCATION/TRANSLATED_IMAGES".equals(name))
            return getTranslatedImagesLocation(for_js);
         if ("APPLICATION/LOCATION/SCRIPTS".equals(name))
            return getScriptsLocation(for_js);
         if ("APPLICATION/LOCATION/STYLESHEETS".equals(name))
            return getStylesheetsLocation(for_js);
         if ("APPLICATION/LOCATION/PORTAL".equals(name))
            return getPortalLocation(for_js);
         if ("APPLICATION/LOCATION/NAVIGATOR".equals(name))
            return getNavigatorLocation(for_js);
         if ("APPLICATION/LOCATION/ADDLINK".equals(name))
            return getAddLinkLocation(for_js);
         if ("APPLICATION/LOCATION/WEBHELP".equals(name))
            return getWebHelpLocation(for_js);
         if ("APPLICATION/LOCATION/PORTALHELP".equals(name))
            return getPortalHelpLocation(for_js);
         if ("LOV/DYNAMIC_URL".equals(name))
            return getDynamicLOVURL(for_js);


         Object obj = (parameters!=null ? parameters : configfile.params).get(name);

         if( obj==null )
         {
            synchronized(configfile.params)
            {
               //
               //  Object EMPTY stored in ASPConfigFile's hashtable
               //  is used to mark entries written to the log
               //
               Object empty = configfile.params.get(name);
               if( empty!=EMPTY )
               {
                  configfile.params.put(name,EMPTY);
                  if( isLogOn() )
                     log("Parameter ["+name+"] not found in file "+configfile.param_file_dir);
               }
            }
            if( parameters!=null ) parameters.put(name,EMPTY);
            obj = EMPTY;
         }

         String str = obj==EMPTY ? null : (String)obj;
         if( Str.isEmpty(str) )
         {
            switch(type)
            {
               case VAL:
                  throw new FndException("FNDCFGPARNF: Parameter &1 not found in file &2",
                                          name, configfile.param_file_dir);
               case VALDEF:
                  str = replaceMacroReferences( default_value );
                  if (DEBUG)
                     debug("ASPConfig: Parameter ["+name+"] set to default value: '"+str+"'.");
                  return str;
            }
         }
         else
            return str;
         return null;
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }

   //==========================================================================
   // Parameters and Sub-Buffers interface
   //==========================================================================

   ASPConfigFile getConfigFile()
   {
      return configfile;
   }

   /**
    * Return the value of the named parameter, or throw an Exception
    * if such parameter does not exist.
    */
   public String getParameter( String name )
   {
      return getParameter(VAL, name, null, false);
   }

   public String getParameter( String name, boolean for_js )
   {
      return getParameter(VAL, name, null, for_js);
   }

   /**
    * Return the value of the named parameter, or the specified default value,
    * if such parameter does not exist.
    */
   public String getParameter( String name, String default_value )
   {
      return getParameter(VALDEF, name, default_value, false);
   }

   public String getParameter( String name, String default_value, boolean for_js )
   {
      return getParameter(VALDEF, name, default_value, for_js);
   }
   
   boolean isRTL()
   {
      String language_code = getASPManager().getUserPreferredLanguage();
      if (Str.isEmpty(language_code))
         language_code = ASPConfigFile.language_code;
      return "RTL".equals(getParameter("LANGUAGE/"+language_code+"/DIRECTION","LTR"));
      
   }
   //==========================================================================
   //  DataFormatter Pool
   //==========================================================================

   DataFormatter getDataFormatter( int type_id, String mask ) throws Exception
   {
      String language_code = getASPManager().getUserPreferredLanguage();
      if (Str.isEmpty(language_code))
         language_code = ASPConfigFile.language_code;
      return getDataFormatter( type_id, mask, language_code );
   }


   DataFormatter getDataFormatter( int type_id,
                                   String mask, String language ) throws Exception
   {
      synchronized(fmttable)
      {
         tmpbuf.clear();
         tmpbuf.append(language);
         tmpbuf.append(',');
         tmpbuf.append((char)type_id);
         tmpbuf.append(',');
         tmpbuf.append(mask);

         DataFormatter formatter = null;  // (DataFormatter)fmttable.get(tmpbuf);

         if( formatter==null )
         {
            switch(type_id)
            {
               case DataFormatter.STRING:
                  formatter = new StringFormatter(type_id,mask);
                  break;

               case DataFormatter.BOOLEAN:
                  formatter = new BooleanFormatter(type_id,mask);
                  break;

               case DataFormatter.NUMBER:
               case DataFormatter.INTEGER:
               case DataFormatter.MONEY:
                  formatter = new NumberFormatter(language,type_id,mask);
                  String sep = getParameter("LANGUAGE/"+language+"/DECIMAL_SEPARATOR",null);
                  if (sep !=null && sep.length()==1)
                     ((NumberFormatter)formatter).setDecimalSeparator(sep.charAt(0));
                  String g_sep = getParameter("LANGUAGE/"+language+"/GROUP_SEPARATOR",null);
                  if("SPACE".equalsIgnoreCase(g_sep) || configfile.is160UsedInGroupings(language))
                      g_sep = " ";
                  if (g_sep !=null && g_sep.length()==1)
                     ((NumberFormatter)formatter).setGroupingSeparator(g_sep.charAt(0));
                  break;
               case DataFormatter.DATETIME:
                  if (Str.isEmpty(mask))
                  {
                     DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM,new Locale(language));
                     if (df instanceof SimpleDateFormat)
                       mask = ((SimpleDateFormat)df).toPattern();
                  }
                  formatter = new DateFormatter(type_id, mask, getTimeZone());
                  break;
               case DataFormatter.DATE:
                  if (Str.isEmpty(mask))
                  {
                     DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,new Locale(language));
                     if (df instanceof SimpleDateFormat)
                       mask = ((SimpleDateFormat)df).toPattern();
                  }
                  formatter = new DateFormatter(type_id, mask, getTimeZone());
                  break;
               case DataFormatter.TIME:
                  if (Str.isEmpty(mask))
                  {
                     DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT,new Locale(language));
                     if (df instanceof SimpleDateFormat)
                       mask = ((SimpleDateFormat)df).toPattern();
                  }
                  formatter = new DateFormatter(type_id, mask, getTimeZone());
                  break;

               default:
                  throw new FndException("FNDCFGINVVAL: Invalid value for Type ID: &1", ""+type_id);
            }
//            fmttable.put(tmpbuf,formatter);
         }
         return formatter;
      }
   }

   //==========================================================================
   // Common parameters that configurate the behavior of the system.
   //==========================================================================

   /**
    * Returns reference to the server formatter
    */
   ServerFormatter getServerFormatter()
   {
      return this.srv_fmt;
   }


   /**
    * Returns the time zone defined for this ASP server
    */
   String getTimeZone()
   {
      return configfile.time_zone;
   }


   /**
    * Returns the reference to the global Factory object instantiated
    * at startup. It is NOT a public method.
    */
   Factory getFactory()
   {
      return configfile.factory;
   }


   /**
    * Returns the maximum length of string which can be passed as query string.
    */
   int getMaxPackLen()
   {
      return configfile.max_pack_len;
   }


   /**
    * Returns buffer size used by ASPQuery class as default
    */
   int getDefaultBufferSize()
   {
      return getASPManager().isMobileVersion()? configfile.def_mobile_buffer_size: configfile.def_buffer_size;
   }


   /**
    * Returns the protocol for the site, for example "http"
    */
   public String getProtocol()
   {
      ASPManager mgr = getASPManager();
      String protocol = mgr.getAspRequest().getScheme();

      if(mgr.isDifferentApplicationPath())
      {
         int host_no = mgr.getCurrentHostIndex()+1;
         String[] data =Str.split((String)configfile.hosts.get(host_no+""),",");
                        
         if(!"NONE".equals(data[0]))
             protocol = data[0];
      }

      return protocol;
   }

   protected String getCookiePathPrefix()
   {
      ASPManager mgr = getASPManager();
      String cookie_prefix = configfile.cookie_root_prefix;
      
      if(mgr.isDifferentApplicationPath())
      {
         int host_no = mgr.getCurrentHostIndex()+1;
         String[] data =Str.split((String)configfile.hosts.get(host_no+""),",");
         if(!"NONE".equals(data[3]))
            cookie_prefix = data[3];
      }
      return cookie_prefix;
   }

   String getCookiePathDomain()
   {
      return configfile.cookie_root_domain;
   }

   /**
    * Returns prog ID for MTS component
    */
   String getMtsProgId()
   {
      return configfile.mts_progid;
   }


   /**
    * Returns true if ASP response should be buffered
    */
   boolean isResponseBuffered()
   {
      return configfile.buffering_response;
   }

   /**
    * Returns the application domain for the site, for example "cmbrnd34:8080"
    */
   public String getApplicationDomain()
   {
      ASPManager mgr = getASPManager();
      String curr_host = mgr.getCurrentHost();
      if(mgr.isDifferentApplicationPath())
      {
         int host_no = mgr.getCurrentHostIndex()+1;
         String[] data =Str.split((String)configfile.hosts.get(host_no+""),",");
                        
         if(!"NONE".equals(data[1]))
             curr_host = data[1];
      }

      return curr_host;
   }

   /**
    * Returns the application context root for the site, for example "\b2e"
    * @see getApplicationPath()
    * @see getApplicationPhyPath()
    * @see getApplicationDomain()
    */
   public String getApplicationContext()
   {
      return configfile.getApplicationPath(getASPManager().getCurrentHostIndex());
   }

   /**
    * Return the path used for cookies and the javascript variable COOKIE_PATH
    */
   String getCookiePath()
   {
      return getApplicationContext();
   }

   String getCookieRootPath()
   {
      ASPManager mgr = getASPManager();
      String cookie_path = configfile.cookie_root_path;
      
      if(mgr.isDifferentApplicationPath())
      {
         int host_no = mgr.getCurrentHostIndex()+1;
         String[] data =Str.split((String)configfile.hosts.get(host_no+""),",");
         if(!"NONE".equals(data[4]))
            cookie_path = data[4];
      }
      return cookie_path;
         //return configfile.cookie_path;
   }


   /**
    * Returns the application path for the site, for example "/b2e/secured"
    * @see getApplicationContext()
    * @see getApplicationPhyPath()
    * @see getApplicationDomain()
    */
   public String getApplicationPath()
   {
      return getApplicationPath(false);
   }

   public String getApplicationPath(boolean for_js)
   {
      if(for_js)
          return ASPConfigFile.APP_PATH;
      else
          return configfile.getApplicationPath(getASPManager().getCurrentHostIndex()) + "/" + (!getASPManager().isLogonPage()?configfile.secured_path:configfile.unsecured_path);
   }

   public String getIFSEEPath(){
       return configfile.getIFSEEPath();
   }

   String getSecuredApplicationPath()
   {
      return configfile.getApplicationPath(getASPManager().getCurrentHostIndex()) + "/" + configfile.secured_path;
   }


   public String getUnsecuredApplicationPath()
   {
      return configfile.getApplicationPath(getASPManager().getCurrentHostIndex()) + "/" + configfile.unsecured_path;
   }

   String getSecuredPath()
   {
      return configfile.secured_path;
   }

   private String getUnsecuredRootLocation()
   {
      return getUnsecuredApplicationPath() +"/";
   }


   String getApplicationId()
   {
      return configfile.application_id;
   }

   String getApplicationPassword()
   {
      return configfile.application_password;
   }

   /**
    * Return the physical path (folder structure) to the application path
    *
    * @see getApplicationContext()
    * @see getApplicationPath()
    * @see getApplicationDomain()
    * @see getApplicationContextPhyPath()
    */
   public String getApplicationPhyPath()
   {
      return configfile.application_phy_path;
   }

   /**
    * Return the physical path (folder structure) to the application path
    *
    * @see getApplicationContext()
    * @see getApplicationPath()
    * @see getApplicationDomain()
    * @see getApplicationContextPhyPath()
    */
   String getApplicationContextPhyPath()
   {
      return configfile.application_context_phy_path;
   }

   String getDicEntryCode()
   {
      String dic_entry_code = getApplicationDomain()+getApplicationPath()+"."+getASPManager().getUserPreferredLanguage();
      return dic_entry_code;
   }

   String getBodyTag()
   {
      return configfile.body_tag;
   }

   String getHeadTag()
   {
      return getASPManager().isExplorer() ?
             configfile.explorer_head_tag :
             configfile.netscape_head_tag;
   }

   String getDebugCookieScript()
   {
      return configfile.debug_cookie_script;
   }

   String getGenericClientScript()
   {
      if (configfile.debug_send_js)
         return configfile.generic_client_script;
      else
         return ASPPage.SRC_SCRIPT_TAG_LEFT + getUnsecuredScriptsLocation() + configfile.generic_clientscript_file_name + ASPPage.SRC_SCRIPT_TAG_RIGHT;
   }

   String getConstantClientScript()
   {
      //if logon page ONLY include non-sensitive essential stuff in genericclientscript
      if (getASPManager().isLogonPage()) return "";

      if (configfile.debug_send_js)
         return configfile.constant_client_script;
      else
         return ASPPage.SRC_SCRIPT_TAG_LEFT + getScriptsLocation(false) + configfile.clientscript_file_name + ASPPage.SRC_SCRIPT_TAG_RIGHT;
   }

   String getLateConstantClientScript()
   {
      if (configfile.debug_send_js)
         return configfile.late_constant_client_script;
      else
         return ASPPage.SRC_SCRIPT_TAG_LEFT + getScriptsLocation(false) + configfile.late_clientscript_file_name + ASPPage.SRC_SCRIPT_TAG_RIGHT;
   }

   public String getConfigUser() throws FndException
   {
      String user = configfile.config_user;
      if( Str.isEmpty(user) )
         throw new FndException("FNDCFGUSR: The parameter ADMIN/CONFIG_USER is undefined in file &1",configfile.param_file_dir);
      return user;
   }

   public String getConfigPassword()
   {
      return configfile.config_password;
   }

   String getTraceWebUser()
   {
      return configfile.web_user;
   }

   boolean getDebugPageId()
   {
      return configfile.debug_page_id;
   }

   boolean getDebugMgrReference()
   {
      return configfile.debug_mgr_reference;
   }

   boolean debugSendJS()
   {
      return configfile.debug_send_js;
   }

   String getLanguageCode()
   {
      return ASPConfigFile.language_code;
   }

   String getApplicationTempPath()
   {
      return configfile.temp_path;
   }

   String getLOVURL(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.lov_url + "'";
      else
          return getRootLocation(false) + configfile.lov_url;
   }

   String getDynamicLOVURL()
   {
      return configfile.dynamic_lov_url;
   }

   String getDynamicLOVURL(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.dynamic_lov_url + "'";
      else
          return getRootLocation(false) + configfile.dynamic_lov_url;
   }

   String getDynamicIidLOVURL(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.iid_lov_url + "'";
      else
          return getRootLocation(false) + configfile.iid_lov_url;
   }

   String getValidationURL(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.validation_url + "'";
      else
          return getRootLocation(false) + configfile.validation_url;
   }

   String[] getLOVHTML()
   {
       return configfile.lov_html;
   }

   String getConfigClientScript()
   {
      AutoString tmpbuf  = new AutoString();
      ASPManager mgr = getASPManager();

      String language_code = mgr.getUserPreferredLanguage();
      if (Str.isEmpty(language_code))
         language_code = ASPConfigFile.language_code;

      //Decimal & Thousand separator
      tmpbuf.append(ASPPage.BEGIN_SCRIPT_TAG);
      tmpbuf.append("DECIMAL_SEPARATOR_NUMBER ='", mgr.getDecimalSeparator(DataFormatter.NUMBER)+ "';\n");
      tmpbuf.append("DECIMAL_SEPARATOR_MONEY ='", mgr.getDecimalSeparator(DataFormatter.MONEY)+ "';\n");
      tmpbuf.append("GROUP_SEPARATOR_NUMBER ='", mgr.getGroupSeparator(DataFormatter.NUMBER)+ "';\n");
      tmpbuf.append("GROUP_SEPARATOR_MONEY ='", mgr.getGroupSeparator(DataFormatter.MONEY)+ "';\n");
      tmpbuf.append("MASK_MONEY ='", mgr.getFormatMask("Money", true), "';\n");
      tmpbuf.append("MASK_NUMBER ='", mgr.getFormatMask("Number", true), "';\n");
      tmpbuf.append("MASK_INTEGER ='", mgr.getFormatMask("Integer", true), "';\n");
      tmpbuf.append("MASK_DATETIME ='", mgr.getFormatMask("Datetime", true), "';\n");
      tmpbuf.append("MASK_TIME ='", mgr.getFormatMask("Time", true), "';\n");
      tmpbuf.append("MASK_DATE ='", mgr.getFormatMask("Date", true), "';\n");

      tmpbuf.append("IS_RWC_HOST =", mgr.isRWCHost()?"true":"false" , ";\n");
      tmpbuf.append("MORE_INFO_MSG = \""+ mgr.translateJavaScript("FNDCFFMERRI: More info ?") +"\";\n");
      tmpbuf.append("EXPIRE_MSG = \""+ mgr.translateJavaScript("FNDCFFREL: This document has expired from the cache and will be reloaded.") +"\";\n");
      tmpbuf.append("RELOAD_MSG = \""+ mgr.translateJavaScript("FNDCFFAUTOREL: Automatic reload on NEW PAGE !") +"\";\n");
      tmpbuf.append("INVVAL_MSG = \""+ mgr.translateJavaScript("FNDCFFINVVAL: Invalid value for field [") +"\";\n");
      tmpbuf.append("INVNUMVAL_MSG = \""+ mgr.translateJavaScript("FNDCFFINVNUMVAL: Invalid Number value for field [") +"\";\n");
      // Added by Terry 20140410
      // Invalid expression of number field msg
      tmpbuf.append("INVNUMEXPRESSION_MSG = \""+ mgr.translateJavaScript("FNDCFFINVNUMEXPRESSION: Invalid expression of number value for field [") +"\";\n");
      // Added end
      tmpbuf.append("REQVAL_MSG = \""+ mgr.translateJavaScript("FNDCFFVALMAND: Required value for field [") +"\";\n");
      tmpbuf.append("ROFLD_MSG = \""+ mgr.translateJavaScript("FNDCFFROMSG: is a Read-Only field. Its value cannot be changed.") +"\";\n");
      tmpbuf.append("DIRTY_MSG = \""+ mgr.JScriptEncode(mgr.translateJavaScript("FNDCFFDIRTYMSG: Save changes?\n\nThe changes have not been saved!\nChoose 'OK' to return and save the changes.\nChoose 'Cancel' to discard the changes.")) +"\";\n");
      tmpbuf.append("REM_PORTAL_PROFILE_MSG = \"" + mgr.translateJavaScript("FNDCFFREMPORTALPROFILE: Reset your Portal settings?")+"\";\n");
      tmpbuf.append("REM_PORTLET_PROFILE_MSG = \"" + mgr.translateJavaScript("FNDCFFREMPORTLETPROFILE: Reset your Portlet settings?")+"\";\n");
      tmpbuf.append("EXISTINGQUERYNAME_MSG = \""+ mgr.translateJavaScript("FNDCFFEXISTINGQRYNAME: This query already exists. Do you want to replace it?") +"\";\n");
      tmpbuf.append("PREVIOUSQUERYNAME_MSG = \""+ mgr.translateJavaScript("FNDCFFPREVIOUSQRYNAME: Cannot use default name '&1'.",mgr.translate("FNDBLOCKPROPREQRY: Previous Query")) +"\";\n");
      tmpbuf.append("INVALIDQUERYNAME_MSG = \""+ mgr.translateJavaScript("FNDCFFINVALIDQRYNAME: Query name cannot contain the character '&1'.","/")+"\";\n");
      tmpbuf.append("VALIDATE_TIME_OUT_MSG = \""+ mgr.translateJavaScript("FNDVALIDATETIMEOUT: Data Could not be retrieved: System time out!") +"\";\n");
      tmpbuf.append("__STYLES         =\""+  getParameter("APPLICATION/LOCATION/STYLESHEETS","../common/stylesheets/+")+ mgr.getCSSFileName() +"\";\n");
      tmpbuf.append("__MSG_TITLE      =\""+ mgr.translateJavaScript("FNDASPCONFIGMESSAGEPAGETITLE: Message ")+"\";\n");
      tmpbuf.append("__MSG_BUTTONS_OK =\""+ mgr.translateJavaScript("FNDASPCONFIGMESSAGEBOXBTNTXTOK: OK") +"\";\n");
      tmpbuf.append("__HELP_MODE_MSG =\""+ mgr.translateJavaScript("FNDASPCONFIGHELPMODEMSG: Help Mode") +"\";\n");
      tmpbuf.append("__EMPTY_USAGEID_HELP_MSG =\""+ mgr.translateJavaScript("FNDASPCONFIGEMPTYUIDHELPMSG: No help associated with this field.") +"\";\n");
      tmpbuf.append("__MAND_FIELDS_MOVE_ERR =\""+ mgr.translateJavaScript("FNDASPCONFIGMANDFIELDMOVEERR: Mandatory fields cannot be made non-visible.") +"\";\n");
      tmpbuf.append("__MAND_FIELDS_MOVE_MSG =\""+ mgr.translateJavaScript("FNDASPCONFIGMANDFIELDMOVEMSG: One or more mandatory fields have moved to non-visible. Make sure these fields get their values set.") +"\";\n");
      tmpbuf.append("__BROADCASTMSG_TITLE =\""+ mgr.translateJavaScript("FNDASPCONFIGBROADCASTMSGTITLE: Broadcast Messages") +"\";\n");      
      tmpbuf.append("__BROADCASTMSG_CLOSE =\""+ mgr.translateJavaScript("FNDASPCONFIGBROADCASTMSGCLOSE: Close") +"\";\n");      
      tmpbuf.append("CASESENSITIVE_WARNING_MSG = \""+ mgr.translateJavaScript("FNDASPCFGCASESENWARNMSG: Unchecking \'Case sensitive search\' may take longer time to populate.") +"\";\n");
      tmpbuf.append("SESSION_EXPIRE_MSG = \""+ mgr.translateJavaScript("FNDASPCFGSESSIONEXPIREMSG: Session has expired. Please logon.") +"\";\n");
      tmpbuf.append("PAGE_EXPIRE_MSG = \""+ mgr.translateJavaScript("FNDASPCFGPAGEEXPIREMSG: The data used in this page has been expired.\nPage is Reloaded.") +"\";\n");
      tmpbuf.append("REM_PORTLET_MSG = \"" + mgr.translateJavaScript("FNDCFFREMPORTLETMSG: Remove%0Portlet?")+"\";\n");

      tmpbuf.append("TIFF_VIEWER_COUNT = "+(mgr.getTiffViewerCount(false)+1)+";\n");
      
      tmpbuf.append(ASPPage.END_SCRIPT_TAG);

       if(mgr.isExplorer())
          return configfile.config_client_script_explorer+tmpbuf.toString();
       else
          return configfile.config_client_script_netscape+tmpbuf.toString();
   }

   String getIFRAMETag()
   {
      return configfile.iframe_tag;
   }

   String getLocalizedAddressClassName()
   {
      return configfile.localized_address_class;
   }

   String getAddressDisplayLayout()
   {
      return configfile.display_layout;
   }

   String getAddressEditLayout()
   {
      return configfile.edit_layout;
   }


   boolean countDbRows()
   {
      return configfile.count_db_rows;
   }

   public String getImagesLocation()
   {
      return (getASPManager().isMobileVersion())? getMobileImageLocation(): getImagesLocation(false);
   }

   public String getImagesLocation(boolean for_js)
   {
      return getImagesLocation(for_js, false);
   }

   public String getImagesLocationWithRTL()
   {
      return getImagesLocationWithRTL(false);
   }

   public String getImagesLocationWithRTL(boolean for_js)
   {
      return getImagesLocation(for_js, true);
   }

   private String getImagesLocation(boolean for_js, boolean check_rtl)
   {
      String image_location;
      if(check_rtl && isRTL())
         image_location = configfile.rtl_images_location;
      else
         image_location = configfile.images_location;
      if (for_js)
         return getRootLocation(true) + "+ '" + image_location + "'";
      else
          return getRootLocation(false) + image_location;
   }

   public String getUnsecuredImageLocation()
   {
      return getUnsecuredRootLocation() + configfile.images_location;
   }

   public String getTranslatedImagesLocation()
   {
      return getTranslatedImagesLocation(false);
   }

   public String getTranslatedImagesLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.translated_images_location + "'";
      else
         return getRootLocation(false) + configfile.translated_images_location;
   }

   public String getScriptsLocation()
   {
      return getScriptsLocation(false);
   }

   String getScriptsLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.scripts_location + "'";
      else
          return getRootLocation(false) + configfile.scripts_location;
   }

   private String getUnsecuredScriptsLocation()
   {
      return getUnsecuredRootLocation() + configfile.scripts_location;
   }


   String getStylesheetsLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '../"+configfile.unsecured_path+"/" + configfile.stylesheets_location + "'";
      else
         return getUnsecuredRootLocation() + configfile.stylesheets_location;
   }

   private String getUnsecuredStylesheetsLocation()
   {
      return getUnsecuredRootLocation() + configfile.stylesheets_location;
   }



   String getPortalLocation(boolean for_js)
   {
      if (configfile.portal_location.startsWith("/"))
         return configfile.portal_location;
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.portal_location + "'";
      else
          return getSecuredApplicationPath() + "/" + configfile.portal_location;
   }

   String getNavigatorLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.navigator_location + "'";
      else
         return getRootLocation(false) + configfile.navigator_location;
   }

   String getAddLinkLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.addlink_location + "'";
      else
          return getRootLocation(false) + configfile.addlink_location;
   }

   String getWebHelpLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.webhelp_location + "'";
      else
          return getRootLocation(false) + configfile.webhelp_location;
   }

   String getPortalHelpLocation(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.portalhelp_location + "'";
      else
          return getRootLocation(false) + configfile.portalhelp_location;
   }

   String getPageMaskTag(){
       return  configfile.page_mask_tag;
   }



   String getRootLocation(boolean for_js)
   {
      if(for_js)
          return configfile.APP_ROOT;
      else
          return getApplicationPath(false) + "/";
   }

   String getRowStatusImageNew()
   {
      return configfile.row_status_image_new;
   }

   String getRowStatusImageModify()
   {
      return configfile.row_status_image_modify;
   }

   String getRowStatusImageRemove()
   {
      return configfile.row_status_image_remove;
   }

   String getRowStatusImageNormal()
   {
      return configfile.row_status_image_normal;
   }

   int getLOVWindowWidth()
   {
      return configfile.lov_window_width;
   }

   int getLOVWindowHeight()
   {
      return configfile.lov_window_height;
   }

   String getLOVTag1( boolean in_assignment )
   {
      return "> <a href=" + (in_assignment?"\\\"":"\"") + "javascript:preLov";
   }

   String getLOVTag1Iid( boolean in_assignment )
   {
      return "> <a href="+ (in_assignment?"\\\"":"\"") +"javascript:lov";
   }

   String getLOVTag2( boolean in_assignment )
   {
      String q = (in_assignment?"\\\"":"\"");
      return q + "><img onclick=\"javascript:menuClicked(this);\" src="
              + q + getRootLocation(false) + configfile.lov_image + q + configfile.lov_image_properties
              + appendLOVTag2(in_assignment);
   }

   private String appendLOVTag2(boolean in_assignment )
   {
      String quote = (in_assignment?"\\\"":"\"");//"\"";
      AutoString tmpbuf  = new AutoString();
      ASPManager mgr = getASPManager();

      tmpbuf.append(" alt=",quote);
      tmpbuf.append( mgr.translate("FNDCFFLOV: List of values"), quote);
      tmpbuf.append(" title=",quote);
      tmpbuf.append( mgr.translate("FNDCFFLOV: List of values"), quote);
      tmpbuf.append("></a"); // skip the closing ">"

      return tmpbuf.toString();
   }

   String getLOVTag2Pop( boolean in_assignment )
   {

      String q = (in_assignment?"\\\"":"\"");
      return q + "><img onclick=\"javascript:menuClicked(this);\" src="
              + q + getRootLocation(false)+ configfile.lov_pop_image + q + configfile.lov_image_properties + appendLOVTag2(in_assignment);
   }

   String getNavigatorRootImage()
   {
      return configfile.navigator_root_image;
   }

   String getNavigatorNodeImage()
   {
      return configfile.navigator_node_image;
   }

   String getNavigatorItemImage()
   {
      return configfile.navigator_item_image;
   }

   String getHeadTagErrorExplorer()
   {
      return configfile.head_tag_error_ie;
   }

   String getHeadTagErrorNetscape()
   {
      return configfile.head_tag_error_nn;
   }

   String getPageErrorImage()
   {
      return configfile.page_error_image;
   }

   String getPageWarningImage()
   {
      return configfile.page_warning_image;
   }

   String getPageInfoImage()
   {
      return configfile.page_info_image;
   }

   String getPageBodyImage()
   {
      return configfile.page_body_image;
   }

   String getColumnReturnImage()
   {
      return configfile.column_return_image;
   }

   boolean getAlertBoxOnErrorNetscape()
   {
      return configfile.alert_box_on_error_nn;
   }

   boolean getAlertBoxOnErrorExplorer()
   {
      return configfile.alert_box_on_error_ie;
   }

   boolean isIfsDialogOnErrorExplorer()
   {
      return configfile.alert_box_ifs_dialog_ie;
   }

   boolean isIfsDialogOnErrorNetscape()
   {
      return configfile.alert_box_ifs_dialog_nn;
   }

  /**
   * @deprecated - use isUserExternallyIdentified
   */
   public boolean isIisUserAuthorizer()
   {
      return configfile.iis_user_authorization;
   }

  /**
   *
   */
   public boolean isUserExternallyIdentified()
   {
      //return configfile.externally_identified;
      return ((configfile.EXTERNAL).equals(configfile.authorization_method));
   }

   /**
    * Returns true if log off functionality is enabled when authentication method is externally identified.
    */
   public boolean isLogoffEnabled() 
   {
      return configfile.is_logoff_enabled;
   }

   /**
    * Returns the customized javascript method which is running when log off. 
    * (when authentication method is externally identified.)
    */
   public String getLogoffScript()
   {
      return configfile.logoff_script;
   }

   boolean isFormBasedAuth()
   {
      return ((configfile.FORM).equals(configfile.authorization_method));
   }

  /**
   *
   */
   public boolean useAuthorization()
   {
      return configfile.use_authentication;
   }

   /**
   * @deprecated - security handled by JAAS
   */
   public String getAccessPoint()
   {
      return null;
   }

  /**
   * Returns if change password functionality is enabled.
   * Password can be changed ONLY when authentication is done by DB.
   * To change an expired password the OCI driver needs to be used.
   */
   public boolean isChangePasswordEnabled()
   {
      return configfile.change_password_enabled;
   }

   String getSecurityRealm()
   {
      return configfile.security_realm;
   }

   /*
   String getSecurityConfigRealm()
   {
      return configfile.config_realm;
   }
   */

   /**
    * @deprecated - use isUserExternallyIdentified
    */
   int getUserAuthorizationMethod()
   {
      return configfile.user_authorization_method;
   }

   String getAuthHeaderName()
   {
      return configfile.auth_header_name;
   }

   int getUserAutoLogoffTime()
   {
      return configfile.user_auto_logoff_time;
   }

   String getAuthUserCookieName()
   {
      return configfile.auth_user_cookie_name;
   }

   public String getLogonURL()
   {
      //return getRootLocation(false) + configfile.logon_url;
      return getUnsecuredRootLocation() + configfile.logon_url;
   }

   String getErrorURL()
   {
      return getUnsecuredRootLocation() + configfile.logon_error_url;
   }

   String getLogonPage()
   {
      return configfile.logon_url;      
   }
   
   String getErrorPage()
   {
      return configfile.logon_error_url;
   }
   
   /**
    *@deprecated use getLogonURL()
    */
   public String getLogonURL(boolean for_js)
   {
      /*
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.logon_url + "'";
      else
          return getRootLocation(false) + configfile.logon_url;
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.logon_url + "'";
      else
         return getUnsecuredRootLocation() + configfile.logon_url;
         */
      return null;
   }

   String getLogonServerName()
   {
      return configfile.logon_server_name;
   }

   boolean encodeTranslations()
   {
      return configfile.encode_translations;
   }

   boolean sendContext()
   {
      return configfile.send_context_cache;
   }

   boolean removeOldContext()
   {
      return configfile.remove_old_context;
   }

   boolean isHoverEnabled()
   {
      return configfile.hover_enabled;
   }

   String getHoverScript()
   {
      return configfile.hover_script;
   }

   String getNewHoverScript()
   {
      return configfile.new_hover_script;
   }

   boolean addErrorInfoEnabled()
   {
      return configfile.add_error_info;
   }

   /**
   * Returns the transleted text for the given translation key, in the given language
   * @param lang_code Language Code
   * @param Translation key
   */
   public String getTranslation(String language_code, String key)
   {
      // Modified by Terry 20131127
      // Original:
      // return (DictionaryCache.getTranslationText(language_code,key));
      return (DictionaryCache.getTranslationText(getASPManager(),language_code,key));
      // Modified end
   }
   
   /**
    * Returns the translated text when lang_code+"_"+translation key is given
    *@deprecated use ASPConfig.getTranslation
   */
   public String getDicValue(String key)
   {
      String lang_code;
      if(key.indexOf("_")==2)
      {
         lang_code = key.substring(0,2);
         key = key.substring(3,key.length());
      }
      else
      {
         return null;
      }
      return getTranslation(lang_code,key);
   }   

   /**
   * Returns the Usage Verison ID for the given translation key for the given Language
   * @param language_code Language Code
   * @param key Translation key
   */
   public String getUsageVersionID(String language_code,String key)
   { 
      return (DictionaryCache.getUsageVersionID(language_code,key));
   }

  /**
    * Returns the usage version id when lang_code+"_"+translation key is given
   *@deprecated use ASPConfig.getUsageVersionID
   */
   public String getUsageID(String key)
   {
      String lang_code;
      if(key.indexOf("_")==2)
      {
         lang_code = key.substring(0,2);
         key = key.substring(3,key.length());
      }
      else
      {
         return null;
      }
      return getUsageVersionID(lang_code,key);
   }
  /**
   * Adds a translation to the memory
   * @param language Language Code
   * @param key Translation key
   * @param value Translated text
   */
   void addDicItem(String language, String key, String value)
   {
      DictionaryCache.addTranslation(language, key, value);
   }

   /**
    *@deprecated
    */
   String getURLEncodeFunction()
   {
      return configfile.url_encode_function;
   }

   int getTMMethod()
   {
      return configfile.tm_method;
   }

   boolean isGZIPEnabled()
   {
      return configfile.gzip_enabled;
   }


  /**
   * @deprecated
   */
   boolean useFNDDBORB()
   {
      return configfile.fnd_db_orb;
   }

   boolean isInactivatePageAtLoading()
   {
       return configfile.inactivate_page_at_loading;
   }

   boolean isBrowserCacheDisabled()
   {
       return configfile.browser_cache_disabled;
   }

   boolean isBrowserHistoryRestricted()
   {
      return configfile.restrict_history;
   }

   boolean isMultiChoiceLovEnabled()
   {
       return configfile.multichoice_lov_enabled;
   }

   boolean isFindModeIidToLovEnabled()
   {
       return configfile.iid_to_lov_enabled;
   }

   boolean dataStoresAvailable()
   {
      return configfile.available_data_stores;
   }

 //Disabling standard toolbar commands

   boolean isHomeIconEnabled()
   {
      return configfile.home_enabled;
   }


   boolean isOptionsEnabled()
   {
      return configfile.options_enabled;
   }

   boolean isNavigateEnabled()
   {
      return configfile.navigate_enabled;
   }

   boolean isDockoutEnabled()
   {
      return configfile.iee_dockout_enabled;
   }
   
   boolean isHelpEnabled()
   {
      return configfile.help_enabled;
   }

   boolean isPortalConfigEnabled()
   {
      boolean config_enabled = isPortletConfigEnabled() && configfile.portal_config_enabled;
      if(config_enabled)
         config_enabled = getASPPage().isObjectAccessible("FND/Default#PORTALCONFIG");
      return config_enabled;
   }

   boolean isPortletConfigEnabled()
   {
      ASPPage page = getASPPage();
      boolean config_enabled = configfile.portlet_config_enabled;
      if(config_enabled)
         config_enabled = page.isObjectAccessible("FND/Default#PORTLETCONFIG");
      
      if(!config_enabled) page.disablePortletConfiguration();
      
      return config_enabled;
   }

/*
   String getHeader()
   {
      return configfile.header;
   }

   String getFooter()
   {
      return configfile.footer;
   }
*/


   String getLoadBalancingGroup()
   {
      return configfile.lb_group;
   }

   //==========================================================================
   // Static parameters.
   //==========================================================================

   static Buffer getASPConfigBuffer()
    {
        return ASPConfigFile.cfg;
    }

   static Buffer getThreadBuffer()
    {
        return ASPConfigFile.thread_buffer;
    }

   static int getProfileCacheMaxSize()
   {
      return ASPConfigFile.profile_cache_max_size;
   }

   static int getTranslationLoadingBufferSize()
   {
      return ASPConfigFile.translation_loading_buffer_size;
   }
   
   
   static int getGarbageLimit()
   {
      String limit = ASPConfigFile.garbage_limit;
      if(Str.isEmpty(limit)) return -1;
      return Integer.parseInt(limit);
   }

   static int getProfileCacheTimeout()
   {
      return ASPConfigFile.profile_cache_time_out;
   }

   /*static String getProfileCachePath()
   {
      String path = ASPConfigFile.profile_cache_path;
      if( Str.isEmpty(path) ) return null;
      return path+ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;
   }*/

   static int getPagePoolMaxSize()
   {
      return ASPConfigFile.page_pool_max_size;
   }

   static int getPagePoolTimeout()
   {
      return ASPConfigFile.page_pool_time_out;
   }

   static String getPagePoolPath()
   {
      String path = ASPConfigFile.page_pool_path;
      if( Str.isEmpty(path) ) return null;
      return path+ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;
   }

   static String getGraphTempLocation()
   {
      return ASPConfigFile.graph_temp_location;
   }

   static String getGraphTempPath()
   {
      String path = ASPConfigFile.graph_temp_path;
      if( Str.isEmpty(path) ) return null;
      return path+ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;
   }

   static int getGraphCacheTimeout()
   {
      return ASPConfigFile.graph_cache_time_out;
   }

   public static int getTiffCacheTimeout()
   {
      return ASPConfigFile.tiff_cache_time_out;
   }

   static String getSystemType()
   {
      return ASPConfigFile.system_type;
   }

   static String getSystemId()
   {
      return ASPConfigFile.system_id;
   }

   static String getSharedSecret()
   {
      return ASPConfigFile.shared_secret;
   }

   int [] getEnabledMask()
   {
       return configfile.ip_enable_masks;
   }

   int [] getDisabledMask()
   {
       return configfile.ip_disable_masks;
   }

   int convertIpToInt(String ip)
   {
      return configfile.ipToInt(ip);
   }

   int getMinZipSize()
   {
      return configfile.min_zipped_size;
   }

   String getCalendarScript()
   {
      AutoString tmpbuf  = new AutoString();

      //Calendar script non needed for logon page
      if (getASPManager().isLogonPage()) return "";

      String fname = getParameter("CALENDAR/JAVASCRIPT_FILE_NAME","calendar.js");
      tmpbuf.append(ASPPage.SRC_SCRIPT_TAG_LEFT);
      tmpbuf.append(getScriptsLocation(false), fname );
      tmpbuf.append(ASPPage.SRC_SCRIPT_TAG_RIGHT);

      if (DEBUG)
         debug("getCalendarScript() ---- " + tmpbuf.toString());
      return tmpbuf.toString();

   }

   String getCalendarConsts() throws FndException
   {
       return configfile.getCalendarConts(getASPManager());
   }

   String getCalendarTag1()
   {
      return configfile.calendar_tag1;
   }

   String getCalendarTag2()
   {
      return configfile.calendar_tag2;
   }

   String getCalendarTag3()
   {
      AutoString tmpbuf  = new AutoString();
      ASPManager mgr = getASPManager();

      tmpbuf.append("<img src=\"",getRootLocation(false),"common/images/calendar.gif\"",configfile.calendar_tag3);
      tmpbuf.append(" alt=\"");
      tmpbuf.append( mgr.translate("FNDCFFCALALT: Calendar"),"\"");
      tmpbuf.append(" title=\"");
      tmpbuf.append( mgr.translate("FNDCFFCALALT: Calendar"),"\"");
      tmpbuf.append("></a");

      //if(mgr.isDifferentApplicationPath())
      //    return configfile.calendar_tag3_arr[mgr.getCurrentHostIndex()]+tmpbuf.toString();
      //else

      return tmpbuf.toString();
   }

   String getCalendarTag4()
   {
      return configfile.calendar_tag4;
   }
   /**    
    *@deprecated as the Common Messeges were moved into the DataBase
    */
   public String getCommonMsgFilePath()
   {
      //return configfile.common_msg_file_path;
      return null;
   }
  /**    
    *@deprecated as the Common Messeges were moved into the DataBase
    */
   public boolean isCommonMsgPathDefined()
   {
      //return configfile.common_msg_file_path!="" && configfile.common_msg_file_path!=null;
      return true;
   }

   public String getCommonMessageBulletTag()
   {
      //getParameter("IFS_COMMON_MESSAGES/MESSAGE_BULLET_IMAGE","blue-ball-small.gif");
      return "&nbsp;&nbsp;<img src='" + getImagesLocation(false) + getParameter("IFS_BROADCAST_MESSAGES/MESSAGE_BULLET_IMAGE","blue-ball-small.gif") + "'>&nbsp;";
   }

   /**
    * @deprecated instead use getTranslatedDateTimeMask()
    */
   public String[] getTranlatedDateTimeMask()
   {
      return getTranslatedDateTimeMask();
   }

   private static int ERA= 0;
   private static int YEAR = 1;
   private static int MONTH_IN_YEAR = 2;
   private static int DAY_IN_MONTH = 3;
   private static int HOUR_IN_AMPM_112 = 4;
   private static int HOUR_IN_DAY_023 = 5;
   private static int MINUTE_IN_HOUR = 6;
   private static int SECOND_IN_MINUTE = 7;
   private static int MILLISECOND = 8;
   private static int DAY_IN_WEEK = 9;
   private static int DAY_IN_YEAR = 10;
   private static int DAY_OF_WEEK_IN_MONTH = 11;
   private static int WEEK_IN_YEAR = 12;
   private static int WEEK_IN_MONTH = 13;
   private static int AMPM_MARKER = 14;
   private static int HOUR_IN_DAY_124 = 15;
   private static int HOUR_IN_AMPM_011 = 16;
   private static int TIME_ZONE = 17;

   public String[] getTranslatedDateTimeMask()
   {
      String[] timemask = new String[19];
      ASPManager mgr = getASPManager();

      timemask[ERA] = mgr.translate("FNDCFF_ERA: G");
      timemask[YEAR] = mgr.translate("FNDCFF_YEAR: y");
      timemask[MONTH_IN_YEAR] = mgr.translate("FNDCFF_MONTH_IN_YEAR: M");
      timemask[DAY_IN_MONTH] = mgr.translate("FNDCFF_DAY_IN_MONTH: d");
      timemask[HOUR_IN_AMPM_112] = mgr.translate("FNDCFF_HOUR_IN_AMPM_112: h");
      timemask[HOUR_IN_DAY_023] = mgr.translate("FNDCFF_HOUR_IN_DAY_023: H");
      timemask[MINUTE_IN_HOUR] = mgr.translate("FNDCFF_MINUTE_IN_HOUR: m");
      timemask[SECOND_IN_MINUTE] = mgr.translate("FNDCFF_SECOND_IN_MINUTE: s");
      timemask[MILLISECOND] = mgr.translate("FNDCFF_MILLISECOND: S");
      timemask[DAY_IN_WEEK] = mgr.translate("FNDCFF_DAY_IN_WEEK: E");
      timemask[DAY_IN_YEAR] = mgr.translate("FNDCFF_DAY_IN_YEAR: D");
      timemask[DAY_OF_WEEK_IN_MONTH] = mgr.translate("FNDCFF_DAY_OF_WEEK_IN_MONTH: F");
      timemask[WEEK_IN_YEAR] = mgr.translate("FNDCFF_WEEK_IN_YEAR: w");
      timemask[WEEK_IN_MONTH] = mgr.translate("FNDCFF_WEEK_IN_MONTH: W");
      timemask[AMPM_MARKER] = mgr.translate("FNDCFF_AMPM_MARKER: a");
      timemask[HOUR_IN_DAY_124] = mgr.translate("FNDCFF_HOUR_IN_DAY_124: k");
      timemask[HOUR_IN_AMPM_011] = mgr.translate("FNDCFF_HOUR_IN_AMPM_011: K");
      timemask[TIME_ZONE] = mgr.translate("FNDCFF_TIME_ZONE: z");

      return timemask;
   }

   String convertPath( String path )
   {
      return configfile.convertPath(path);
   }

   public static String convertPath( String path, String basedir )
   {
      return ASPConfigFile.convertPath(path, basedir);
   }


   public boolean isQueryHintsOn()
   {
      return configfile.query_hints_on;
   }
   
   public boolean isHybridSearchAvailable()
   {
      return configfile.is_hybrid_search_available;
   }

   //Bug 42299, start
   String getIEValidationMethod()
   {
       return configfile.validation_method_ie;
   }

   String getNetscape7ValidationMethod()
   {
       return configfile.validation_method_netscape7;
   }
   //Bug 42299, end

   public boolean isMultiLanguageEnabled()
   {
      return configfile.multi_language_enabled;
   }

   public String getDefaultLanguage()
   {
      return ASPConfigFile.language_code;
   }

   public String getLanguageURL()
   {
      return getLanguageURL(false);
   }

   public String getLanguageURL(boolean for_js)
   {
      if (for_js)
         return getRootLocation(true) + "+ '" + configfile.language_url + "'";
      else
          return getRootLocation(false) + configfile.language_url;
   }

   /**
    * Return an ASPBuffer with the login domain list.
    * First buffer item is the system representation, second item is the display name.
    */
   public ASPBuffer getLogonDomainList()
   {
      return configfile.domain_list;
   }

   //Bug id 42390, start
   String getApplicationCompatibility()
   {
      return configfile.application_compatibility;
   }
   //Bug id 42390, end

   HashMap getRequestingHosts()
   {
       return configfile.requesting_hosts;
   }

   HashMap getHosts()
   {
       return configfile.hosts;
   }

   String getDefaultApplicationPath()
   {
        return configfile.application_path + "/" + configfile.secured_path;
   }
   
   String getDefaultUnsecuredApplicationPath()
   {
        return configfile.application_path + "/" + configfile.unsecured_path;
   }
   
   String[] getApplicationPathArr()
   {
       return configfile.getApplicationPathArr();
   }

   String getDefaultImagesLocation()
   {
       return configfile.images_location;
   }

   String getDefaultTranslatedImagesLocation()
   {
       return configfile.translated_images_location;
   }

   boolean identifiedByStandardPortal()
   {
      return ((configfile.STANDARD_PORTAL).equals(configfile.authorization_method));
   }

   boolean identifiedByPlugin()
   {
      return ((configfile.PLUGIN).equals(configfile.authorization_method));
   }

   String getAuthenticatePluginClassName()
   {
      return configfile.auth_plugin_class_name;
   }

   int[] getLocalizationSizes()
   {
      return new int[1];//figfile.getLocalizationSizes();
   }
   
   int getNoOfDecimals()
   {
      return configfile.precision;
   }
   
   /**
    * Returns the theme data array 
    *  [0] - Theme Name
    *  [1] - css file name
    */
   
   String[] getThemeData(int i) throws FndException
   {
      if (i<configfile.themes.length)
         return configfile.themes[i-1];
      else 
         throw new FndException("FNDASPCONFIGTHEMENOTDEFINED: This theme is not defined in the configuration file.");
   }
   
   /* 
    * Returns an 2D array of theme information. Not to be used by application pages. 
    * This is called from general Configuration page.
    */
   
   public String[][] getThemes()
   {
      return configfile.themes;
   }
   /**
    * Returns an array of image names
    * 0 - Home
    * 1 - Home With down arrow
    * 2 - Refresh
    * 3 - Options
    * 4 - Navigator
    * 5 - Navigator With down arrow
    * 6 - Configure
    * 7 - Help
    * 8 - What's this
    */
   
   String[] getToolBarImages()
   {
      return new String[]{
         configfile.header_home, 
         configfile.header_home_pop,
         configfile.header_refresh,
         configfile.header_options,
         configfile.header_navigator,
         configfile.header_navigator_pop,
         configfile.header_configure,
         configfile.header_help,
         configfile.header_whatsthis,
         configfile.header_ifs_logo
      };
   }
   
   String getPNGCorrectionScript()
   {
       return configfile.png_correction_script;
   }
   
   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================

   String getMobileCSSFile() throws FndException
   {
      return configfile.mobilecssfile;
   }
   
   public String getMobileImageLocation()
   {
      return getRootLocation(false) + configfile.mobile_images_location;
   }
   
   String getMobileBodyTag()
   {
      return configfile.mobile_body_tag;
   }

   String[] getMobileToolBarImages()
   {
      return new String[]{configfile.header_navigator,configfile.header_help};
   }
}
