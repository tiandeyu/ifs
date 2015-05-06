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
 * File        : ASPPortletProvider.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Dec-06 - Created.
 *    Jacek P  2000-Jan-17 - Extends ASPPage
 *    Jacek P  2000-Feb-21 - Changed name from ASPPortalProvider
 *    Jacek P  2000-Mar-21 - Set version 3
 *    Jacek P  2000-Apr-04 - Changes in printTable().
 *    Jacek P  2000-Apr-17 - removed-flag moved to ASPPortletHandle class.
 *                           Added runCustom(). Several functions changed to final.
 *    Jacek P  2000-Apr-18 - Functions run() and runCustom() can now throw FndException.
 *    Jacek P  2000-May-08 - Beta 4 release of Web Kit 3.0.0.
 *    Jacek P  2000-May-16 - Added methods begin/endTableCell().
 *    Jacek P  2000-May-22 - New methods for support of command bars and popups.
 *                           Added parseProviderPrefix(). Id converted to upper case.
 *                           New print*() methods.
 *    Jacek P  2000-May-24 - New functions for hover image links and width control
 *                           of tables.
 *    Jacek P  2000-Jul-05 - Reorganized, added comments.
 *    Jacek P  2000-Jul-25 - Fixed bug 281: Netscape doesn't support empty JavaScript files.
 *    Jacek P  2000-Jul-26 - Log id 226: Improved error handling.
 *    Jacek P  2000-Jul-28 - Bug id 248: Added call to HTMLEncode() for print*() methods.
 *    Jacek P  2000-Aug-14 - Adding cloning of buffer in readProfileBuffer().
 *    Jacek P  2000-Jul-28 - Temporary removed call to HTMLEncode() from printTableCell().
 *    Jacek P  2000-Aug-24 - Changed back printTableCell().
 *                           Added empty-check in printText() and printTableCell().
 *    Jacek P  2000-Oct-04 - Added method getColumnWidth() (log id #426).
 *    Jacek P  2000-Oct-06 - Added methods printLOV() and printDynamicLOV() (log id #298).
 *    Jacek P  2000-Oct-17 - Added call to getApplicationPath() in printLink() (log id #469)
 *    Jacek P  2000-Nov-22 - Added printAbsoluteLink() that replaces the changed prinkLink().
 *                           Implementation of printLink() backed to the previous version (log id #518).
 *    Piotr Z  2000-Mar-12 - New method clearGlobalProfile().
 *    Piotr Z  2001-Apr-25 - Scaling of input and textarea fields size for Netscape.
 *    Piotr Z  2001-May-22 - Added handling of ASPManager.ResponseEndException in the method define().
 *    Piotr Z  2001-May-30 - Moved handling of ASPManager.ResponseEndException from the method define()
 *                           to FndException class.
 *    Artur K  2001-Jun-12 - Changed readProfileValue() and readProfileFlag()
 *    Ramila H 2001-Jun-25 - Added split() method.
 *    Jacek P  2001-Sep-24 - Added reseting of intitlerow variable.
 *    Suneth M 2001-Oct-12 - Changed readProfileValue().
 *    Daniel S 2001-Oct-16 - AltBackground color on every other row not hardcoded anylonger.
 *    Rifki R  2001-Dec-11 - Log id 771: used getASPManager().getASPLog() in enableWriteResponse(),
 *                           disableWriteResponse() and formatPortletErrorMsg() methods.
 *    Jacek P  2002-Feb-04 - Removed some controles while building up the HTML table.
 *                           Affected variables are: tablelevel, tabbodylevel and tabcelllevel
 *    Daniel S 2002-Aug-28 - Added method frameworkReset to break the doReset chain. Necessary
 *                           due to the new smart Resetting/Cloning.
 *    Chandana 2002-Aug-30 - Added an Item ("DATA_STORE") to the request buffer in the submit method.
 *    Chandana 2002-Sep-03 - Modified printLink() method to support hyperlinking only a part of the text using '&&'.
 *    Rifki R  2002-Oct-15 - Moved implementation of split() method to util class Str.
 *    Shiran F 2002-Oct-24 - Added method isMaximized.
 *    Chanaka  2002-Dec-16 - Add overload methods beginTableCell() to set the nowrap property in a cell
 *    Sampath  2002-Dec-26 - Add new method getZoomInURL() to eneble zoomin in portlets
 *    Rifki R  2003-Jan-09 - Log id 1010, changed printTable() for auto security check.
 *    Sampath  2004-May-23 - add a new method beginCustomTable()
 *    Sampath  2004-Aug-13 - called getLOVTag1Iid() in printLOV
 *    Jacek P  2004-Jan-04 - Bug#40931. Better debugging possibilities of Page Pool.
 *    ChandanaD2004-Mar-23 - Merged with SP1.
 *    ChandanaD2004-May-12 - Updated for new style sheets.
 *    Mangala  2004-Jun-17 - Merged with Bug #44656
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Suneth M 2004-Aug-17 - Added overloaded methods printLOV() & printDynamicLOV() to enable
 *                           multi-choice option for LOV's.
 *    Suneth M 2004-Aug-19 - Added methods printCalender(), printDateTimeMask() & changed printField()
 *                           to improve support for date fields.
 *    Ramila H 2004-Nov-18 - Implemented JSR168 support
 *    Mangala  2004-Nov-09 - Changes done for SSO for JSR168.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/06/26 mapelk Bug 74852, Programming Model for Activities. 
 * 2008/05/12 sumelk Bug 73849, Changed printRadioButton() to print the label using the standard font.
 * 2008/03/27 sadhlk Bug 72361, Modified define().
 * 2008/01/21 sumelk Bug 69852, Changed printField() to send the block name as a parameter for formatDate_(). 
 * 2007/07/03 sadhlk Merged Bug 64669, Modified writeProfileBuffer(), writeProfileValue(), writeProfileFlag(). 
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/12/21 buhilk Bug id 61697, Added printBoldText(), printItalicText(), printBoldItalicText() methods.
 * 2006/09/24 mapelk Bug Id 59842, Improved CSV code 
 * 2006/09/19 buhilk               removed overloading readProfileValue() with ASPField parameter
 *
 * 2006/09/19 buhilk Bug id 59842, Modifed private readProfileValue() method to format CSV values according to a field
 *                                 Added more overloading readProfileValue() methods to handle new functionality
 *
 * 2006/09/18 buhilk Bug id 59842, Modifed readProfileValue() to handle context substitution variables 
 *                                 Added overloading readProfileValue() and readAbsoluteProfileValue() methods 
 *                                 and modified the printLink() method to decode URL's with CSV values.
 *
 * 2006/08/01 rahelk Bug id 59663, Fixed bug in printLink and printImage 
 *
 * Revision 1.4  2005/11/08 07:51:30  rahelk
 * core changes for using USAGES in help
 *
 * Revision 1.3  2005/11/07 08:16:33  mapelk
 * Introduced "persistant" att to Dynamic Objects and remove non persistent objects from the DynamicObjectCache in the first get.
 *
 * Revision 1.2  2005/10/14 09:08:14  mapelk
 * Added common profile elements. And removed language specific formats and read from locale.
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.11  2005/08/30 11:14:26  mapelk
 * Introduced new APIs to draw images with a name and a title.
 *
 * Revision 1.10  2005/08/08 09:44:05  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.9  2005/06/27 05:01:09  mapelk
 * Bug fixes for std portlets
 *
 * Revision 1.8  2005/06/08 09:34:51  japase
 * Setting right type when storing profile flags
 *
 * Revision 1.7  2005/04/07 13:49:25  riralk
 * Called updatePageScript() in ASPPage when generating client scripts for portlets from generatePageClientScript().
 *
 * Revision 1.6  2005/04/01 13:59:56  riralk
 * Moved Dynamic object cache to session - support for clustering
 *
 * Revision 1.5  2005/02/24 13:48:59  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.4  2005/02/24 08:53:52  mapelk
 * Improved automatic security checks
 *
 * Revision 1.3  2005/02/16 08:39:57  japase
 * Only ProfileItem instances allowed in profiles
 *
 * Revision 1.2  2005/02/01 10:32:59  mapelk
 * Proxy related bug fix: Removed path dependencies from webclientconfig.xml and changed the framework accordingly.
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.4  2005/01/06 08:56:36  rahelk
 * added setFormReference to printField and printCalender
 *
 * Revision 1.3  2005/01/06 04:33:04  rahelk
 * bug correction to support standard portlet mode
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
import ifs.fnd.ap.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


/**
 */
public abstract class ASPPortletProvider extends ASPPage
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortletProvider");

   /**
    * Constant for definition of portlet mode.
    * Used when portlet is minimized in the browser.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#getTitle
    */
   public static final int MAXIMIZED = ASPPortal.MAXIMIZED;
   /**
    * Constant for definition of portlet mode.
    * Used when portlet is maximized in the browser.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#getTitle
    */
   public static final int MINIMIZED = ASPPortal.MINIMIZED;
   /**
    * Constant for definition of portlet mode.
    * Used when portlet is shown in customize mode.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#getTitle
    */
   public static final int CUSTOM    = ASPPortal.CUSTOM;


   //==========================================================================
   // instances created on page creation (immutable attributes)
   //==========================================================================

   private AutoString script;
   private String     scriptfile;
   private AutoString dirty_script = new AutoString();


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient ASPPortal        portal;
   private transient ASPPageHandle    handle;
   private transient String           prvid;
   private transient ASPPortletHandle porthand;
   private transient int              linkseq = 0;
   private transient FndException     portlet_err;
   private transient Vector           dummy_globals;

   private AutoString out = new AutoString();
   private AutoString temp_globals = new AutoString();
   private AutoString temp_globals_value = new AutoString();



   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Public constructor that will be called by ASPPortal.
    * Must be overridden by the portlet and call the super() function.
    */
   public ASPPortletProvider( ASPPortal portal, String clspath )
   {
      super(portal.getASPManager(), clspath);
      if(DEBUG) debug(this+": ASPPortletProvider.<init>: "+portal+","+clspath);

      this.portal = portal;
   }

   /**
    * Construction of the page.
    */
   //public
   protected ASPPage construct() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.construct()");

      script = new AutoString();
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
      if (DEBUG) debug(this+": ASPPortletProvider.doFreeze()");
//      Vector tables = getASPTables();
//      for( int i=0; i<tables.size(); i++ )
//         ((ASPTable)tables.elementAt(i)).disableNoWrap();
      super.doFreeze();
   }

   /**
    * Just a container of frameworkReset.
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
      if (DEBUG) debug(this+": ASPPortletProvider.frameworkReset()");

      super.doReset();

      out.clear();
      portal      = null;
      handle      = null;
      prvid       = null;
      porthand    = null;
      linkseq     = 0;
      portlet_err = null;
      intitlerow  = false;
      dirty_script.clear();
      if(dummy_globals != null)
         dummy_globals.clear();
      if(temp_globals != null)
         temp_globals.clear();
      if(temp_globals_value != null)
         temp_globals_value.clear();
   }

   /**
    * Activate the page after feching from pool. Call activate() for
    * all enclosed elements.
    * Can only be called if the current object is in state DEFINED.
    */
   protected void doActivate() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.doActivate()");

//      super.doActivate();
      portal = getASPManager().getPortalPage().getASPPortal();
      //prvid     = null; already set from ASPPortal !!!
      linkseq = 0;
      out.clear();
      dirty_script.clear();
      super.doActivate();
   }

   /**
    * Clone the page and all enclosed elements.
    * The new page is always in state DEFINED.
    * Set default values for all mutable attributes.
    */
   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if (DEBUG) debug(this+": ASPPortletProvider.clone("+mgr+")");

      ASPPoolElement page = super.clone(mgr);
      return page;
   }

   /**
    * Create a new instance of the current class by using the Relection package.
    * Called from ASPPage.
    */
   protected final ASPPage newInstance( ASPManager mgr ) throws FndException
   {
      if (DEBUG) debug(this+": ASPPortletProvider.newInstance("+mgr+")");

      ASPPortal portal  = mgr.getPortalPage().getASPPortal();
      String    clspath = null;

      ASPPortletProvider prv;

      try
      {
         Object[] arg = new Object[2];
         arg[0] = portal;
         arg[1] = clspath;
         if(DEBUG) debug("  arg=[0]"+arg[0]+"\n\t  arg=[1]"+arg[1]);

         Class[] argcls = new Class[2];
         argcls[0] = portal.getClass();
         argcls[1] = Class.forName("java.lang.String");
         if(DEBUG) debug("  argcls[0]="+argcls[0]+"\n\t  argcls[1]="+argcls[1]);

         Class cls = getClass(); //Class.forName(getClass().getName());
         Constructor ctr = cls.getConstructor(argcls);
         prv = (ASPPortletProvider)(ctr.newInstance(arg));
      }
      catch( InvocationTargetException x )
      {
         Throwable t = x.getTargetException();
         if(DEBUG) debug("  InvocationTargetException:\n"+Str.getStackTrace(t));
         throw new FndException(t);
      }
      catch( Throwable x )
      {
         if(DEBUG) debug(Str.getStackTrace(x));
         throw new FndException(x);
      }

      prv.script     = script;
      prv.scriptfile = scriptfile;
      return prv;
   }

   /**
    * Returns true if compared instances (portlets) are of the same class
    * and have the same profile information
    */
   protected boolean equals( ASPPortletProvider prv )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.equals("+prv+")" );

      if( getClass().getName() != prv.getClass().getName() ) return false;
      if( porthand==null && prv.porthand==null )             return true;
      if( porthand==null || prv.porthand==null )             return false;

      Buffer prf1 = porthand.getProfile();
      Buffer prf2 = prv.porthand.getProfile();

      if( prf1==null && prf2==null ) return true;
      if( prf1==null || prf2==null ) return false;

      return prf1.equals(prf2);
   }

   //==========================================================================
   //  Functions that handle references to the stored instances. Not to be used by portlets.
   //==========================================================================

   /**
    * Get an instance of ASPLog connected to the main page.
    * Called from constructor in ASPPage.
    * Override the corresponding method in ASPPage.
    *
    * @see ifs.fnd.asp.ASPPage#newASPLog
    */
   protected ASPLog newASPLog()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.newASPLog()");
      return getASPManager().getASPLog();
   }

   /**
    * Create a new instance of ASPContext connected to this page.
    * Called from constructor in ASPPage.
    * Override the corresponding method in ASPPage.
    *
    * @see ifs.fnd.asp.ASPPage#newASPContext
    */
   protected final ASPContext newASPContext()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.newASPContext()");
      return new ASPPortalContext(this);
   }

   /**
    * Return the ASPContext instance connected to the main portal page.
    * Called from construct() and doActivate() in ASPPortalContext.
    */
   ASPContext getParentContext()
   {
      return portal.getASPPage().getASPContext();
   }

   /**
    * Called from ASPPage.setDefined() to create and lock a pool handle.
    * Override the corresponding method in ASPPage.
    */
   protected void setAndLockPageHandle() throws FndException//JAPA
   {
      //Bug 40931, start
      //setPageHandle( ASPPagePool.addAndLock(this) );
      setPageHandle( ASPPagePool.addAndLock(this, portal==null ? null : portal.getASPPage()) );
      //Bug 40931, end
   }

   /**
    * Set a page pool handle for this page.
    * Called from setAndLockPageHandle() and ASPPortletHandle.activate().
    */
   void setPageHandle( ASPPageHandle handle )
   {
      this.handle = handle;
   }

   /**
    * Return the stored page pool handle for this page.
    * Called from ASPPortletHandle.reset().
    */
   ASPPageHandle getPageHandle()
   {
      return handle;
   }

   /**
    * Set a portlet handle for this page.
    * Called from the constructor and activate() in ASPPortletHandle.
    */
   void setHandle( ASPPortletHandle handle )
   {
      this.porthand = handle;
   }

   //==========================================================================
   //  Error handling
   //==========================================================================

   FndException getPortletException()
   {
      return portlet_err;
   }

   void setPortletException( FndException portlet_err )
   {
      this.portlet_err = portlet_err;
   }

   void formatPortletErrorMsg( AutoString out ) throws FndException
   {
      if(portlet_err!=null)
      {
         ASPLog log = getASPManager().getASPLog();
         log.setPortlet(this);
         log.logError(portlet_err,out);
         log.setPortlet(null);
      }
   }

   void enableWriteResponse()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.enableWriteResponse(): "+
                      getASPManager()+","+(getASPManager()==null?"null":getASPManager().getASPLog()+""));
      getASPManager().getASPLog().enableWriteResponse();
   }

   void disableWriteResponse() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.disableWriteResponse(): "+
                      getASPManager()+","+(getASPManager()==null?"null":getASPManager().getASPLog()+""));
      getASPManager().getASPLog().disableWriteResponse(this);
   }

   //==========================================================================
   //  Generation of client script
   //==========================================================================

   /**
    * Create a JS file for the current portlet.
    * Override the corresponding method in ASPPage.
    *
    * @see ifs.fnd.asp.ASPPage#generatePageClientScript
    */
   protected void generatePageClientScript() throws Exception
   {
      if(DEBUG) debug(this+": ASPPortletProvider.generatePageClientScript()");

      if(!Str.isEmpty(scriptfile))
      {
//         String path = getApplicationPath()+scriptfile;
         String path = scriptfile;
         if(DEBUG) debug("  path="+path);

         String phypath = getASPManager().getPhyPath(path); //mapPath()
         if(DEBUG) debug("  phypath="+phypath);

         String jsfile = Util.readAndTrimFile(phypath);

         script.append("\n\n// Included script file:\n\n",jsfile);
      }

      // Workaround due to Netscape bug
      if(script.length()==0)
        script.append("function dummy(){}\n");

      script.append(getASPConfig().getCalendarConsts());

      String docache = getASPConfig().getParameter("ADMIN/DYNAMIC_OBJECT_CACHE/ENABLED","N");
      if ("Y".equals(docache))
      {
        DynamicObjectCache.put(getScriptCacheKey(), script.toString(),"text/javascript", getASPManager(),true );
        updatePageScript(script.toString());
      } 
      else
        Str.writeFile(getScriptFileName(),script.toString());
   }

   /**
    * Add dirty JavaScript block functions and script code.
    * Override the corresponding method in ASPPage.
    *
    * @see ifs.fnd.asp.ASPPage#appendDirtyClientScript
    */
   void appendDirtyClientScript( AutoString buf ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.appendDirtyClientScript(");

      buf.append("\n<!-- append dirty script for portlet: '",prvid,"' -->");
      if( dirty_script.length()>0 )
      {
         buf.append(ASPPage.BEGIN_SCRIPT_TAG);
         Vector blocks = getASPBlocks();
         for( int i=0; i<blocks.size(); i++ )
         {
            ASPBlock blk = (ASPBlock)(blocks.elementAt(i));
            if( isNotDefined() )
               blk.appendPageClientScript(buf);
            else
               blk.appendDirtyClientScript(buf);
         }

         buf.append("setFormReference('");
         buf.append(prvid);
         buf.append("');\n");

         //if (getASPManager().getASPConfig().identifiedByStandardPortal())
         //   buf.append("storeUserKeyCookie('" + getASPManager().getAspSession().getAttribute(ifs.fnd.portal.GenericIFSPortlet.USER_REF_KEY) + "');\n");
         buf.append(dirty_script);
         buf.append(ASPPage.END_SCRIPT_TAG);
      }
   }

   //==========================================================================
   //==========================================================================
   //  Portlet protected interface
   //==========================================================================
   //==========================================================================

   //==========================================================================
   //  Template functions for overriding in portlets
   //==========================================================================

   /**
    * Return the minimum width of the portlet in pixels.
    * Default value corresponds to the default wide column.
    * Used to decide within which portal column the portlet can be placed.
    */
   public static int getMinWidth()
   {
      return 288;
   }

   /**
    * Return the portlet description that will be shown on the 'Customize Portal' page.
    * The description text should contain the translatable constant.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   public static String getDescription()
   {
      return "FNDPRVDESC: Description not set";
   }

   /**
    * Return the title of the portlet depending of the current mode,
    * which can be one of MAXIMIZED, MINIMIZED or CUSTOM.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#MAXIMIZED
    * @see ifs.fnd.asp.ASPPortletProvider#MINIMIZED
    * @see ifs.fnd.asp.ASPPortletProvider#CUSTOM
    */
   protected String getTitle( int mode ) throws FndException
   {
      return getClass().getName();
   }

   /**
    *return the URL of page opened when zoomin button is clicked
    *the zoom button is enebled only when this method returns a non null value
    */
   protected String getZoomInURL()
   {
      return null;
   }

   /**
    * Called by ASPPortletHandle, not for overriding.
    */
   void define() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.define()");

      if( !isDefined() )
      {
         setVersion(3);
         if(portlet_err==null)
            try
            {
               disableWriteResponse();
               preDefine();
               setDefined();
               enableWriteResponse();
            }
            catch( Throwable x )
            {
               enableWriteResponse();
               if( !(x instanceof ASPLog.AbortException) )
               {
                  if( portlet_err==null )
                  {
                     if( x instanceof FndException )
                        portlet_err = (FndException)x;
                     else
                        portlet_err = new FndException(x);
                  }
                  else if( portlet_err.getCaughtException()==null )
                     portlet_err.addCaughtException(x);
                  portlet_err.setAdditionalMessage(translate("FNDPRVERR1: in preDefine()."));
               }
            }
      }
      else if(portlet_err==null)
         try
         {
            disableWriteResponse();
            init();
            enableWriteResponse();
         }
         catch( Throwable x )
         {
            enableWriteResponse();
            if( !(x instanceof ASPLog.AbortException) )
            {
               if( portlet_err==null )
               {
                  if( x instanceof FndException )
                     portlet_err = (FndException)x;
                  else
                     portlet_err = new FndException(x);
               }
               else if( portlet_err.getCaughtException()==null )
                  portlet_err.addCaughtException(x);
               portlet_err.setAdditionalMessage(translate("FNDPRVERR2: in init()."));
            }
         }
      applyProfileFormatter();
      applyLongYearFormatter();
   }

   /**
    * Define the logical structure of the portal.
    * Called only once to create the structure stored in the page pool for later use.
    */
   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.preDefine()");
   }

   /**
    * Called on page definition instead of preDefine()
    * if the portlet is fetched from the pool.
    * Placeholder for initialization of variables
    * and fetching of profile information.
    */
   protected void init() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.init()");
   }

   /**
    * Placeholder for the business logic.
    */
   protected void run() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.run()");
   }

   /**
    * Create the HTML contents of the portlet by calling printContents().
    * Called from ASPPortel.generateBoxBody()
    */
   AutoString getContents() throws FndException
   {
      out.clear();
      if(portlet_err==null)
         try
         {
            disableWriteResponse();
            printContents();
            enableWriteResponse();
         }
         catch( Throwable x )
         {
            enableWriteResponse();
            if( !(x instanceof ASPLog.AbortException) )
            {
               if( portlet_err==null )
               {
                  if( x instanceof FndException )
                     portlet_err = (FndException)x;
                  else
                     portlet_err = new FndException(x);
               }
               else if( portlet_err.getCaughtException()==null )
                  portlet_err.addCaughtException(x);
               portlet_err.setAdditionalMessage(translate("FNDPRVERR3: in getContents()."));
            }
         }
      if(portlet_err!=null)
      {
         out.clear();
         out.append("<p class=normalTextLabel>",translate("FNDPRVERRMSG: Error message:"),"</p><font class=normalTextValue>\n");
         formatPortletErrorMsg(out);
         out.append("</font>\n");
      }

      return out;
   }

   /**
    * Print the HTML contents of the portlet.
    */
   protected void printContents() throws FndException
   {
      printText(null);
   }

     /**
    * Define possibility of portlets customization.
    * If the portlet should be customizable this function must return true.
    */
   protected boolean canCustomize()
   {
      return false;
   }


   /**
    * Called during the next request after the customize mode is finished.
    * Placeholder for saving of the profile information.
    */
   protected void submitCustomization() throws FndException
   {
   }

   /**
    * Run the business logic for the customize mode.
    */
   protected void runCustom() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.runCustom()");
   }

   /**
    * Create the HTML code for the customize mode by calling printCustomBody().
    * Called from ASPPortel.generateCustomizeHTML()
    */
   void getCustomBody( AutoString out ) throws FndException
   {
      this.out.clear();
      printCustomBody();
      out.append(this.out);
   }

   /**
    * Print the HTML code for the customize mode.
    */
   protected void printCustomBody() throws FndException
   {
      printText(null);
   }

   //==========================================================================
   // Functions for handling of portlet ID
   //==========================================================================

   /**
    * Return portlet prefix, based on portlet ID, to be used in conjunction
    * to generation of HTML objects.
    */
   protected final String addProviderPrefix()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.addProviderPrefix()");
      return "_"+getId().replace('.','_')+"_";
   }

   /**
    * Return the portlet ID given the portlet prefix used in conjunction to HTML objects.
    */
   protected final static String parseProviderPrefix( String prefix ) throws FndException
   {
      if(DEBUG) Util.debug("ASPPortletProvider.parseProviderPrefix("+prefix+")");

      if( Str.isEmpty(prefix) )
         throw new FndException("FNDPPRVEMPTYPREFIX: Provider prefix not defined.");
      if( !prefix.startsWith("_") || !prefix.endsWith("_") )
         throw new FndException("FNDPPRVERRBEGEND: Provider prefix syntax error.");

      String id = prefix.substring(1,prefix.length()-1);
      if(DEBUG) Util.debug("  id="+id);
      return id;
   }

   //JAPA ? obsolete? used also by ASPPage
   protected final String prefixWithId()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.prefixWithId()");
      return "";
   }

   /**
    * Sets the portlet ID. Called by ASPPortal.
    */
   void setId( String id )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.setId("+id+")");
      this.prvid = id.toUpperCase();
   }

   /**
    * Returns the current portlet ID. The ID is valid only during the request
    * and is not the same for two requests.
    */
   protected final String getId()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.getId(): "+prvid);
      return prvid;
   }


   /**
    * Returns whether the current portlet is Maximized or not.
    */

   protected final boolean isMaximized()
   {
       return portal.isPortletMaximized(getId());
   }



   /**
    * Returns the HTML name of an object.
    */
   protected final String getHTMLName( String name )
   {
      return addProviderPrefix() + name;
   }

   /**
    * Returns an ID to be used for naming of portlet sub-buffer in the portal context.
    */
   String getContextId()
   {
      if(DEBUG) debug(this+": ASPPortletProvider.getContextId()");
      String ctxid = getClass().getName()+"."+getId();
      if(DEBUG) debug("  ctxid="+ctxid);
      return ctxid;
   }

   //==========================================================================
   //   Other help functions to be used in the portlet
   //==========================================================================

   /**
    * Returns the width of the current portal column.
    */
   protected final int getColumnWidth()
   {
      if(portal!=null && prvid!=null)
         return portal.getColumnWidth(prvid);
      return -1;
   }

   //==========================================================================
   //   Translations
   //==========================================================================

   /**
    * Returns the translation of a translatable constant.
    * <p>
    * A translatable constant is a constant which's value begins with the constant name,
    * a colon, and a space.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected final String translate( String text )
   {
      return getASPManager().translateJavaText(text);
   }

   /**
    * Returns the translation of a translatable constant
    * and inserts parameters in the translation.
    * <p>
    * This function works by replacing placeholders in the constant text with
    * the values of the replacement parameters. A placeholders is an apersand (&)
    * character followed by a number. The number indicates what parameter value should
    * replace the placeholder. For example &1 is replaced by p1, &2 by p2 and &3 by p3.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected final String translate( String text, String p1, String p2, String p3 )
   {
      return getASPManager().translateJavaText(text,p1,p2,p3);
   }

   /**
    * Returns the translation of a translatable constant
    * and inserts parameters in the translation.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected final String translate( String text, String p1, String p2 )
   {
      return getASPManager().translateJavaText(text,p1,p2);
   }

   /**
    * Returns the translation of a translatable constant
    * and inserts parameter in the translation.
    *
    * @see ifs.fnd.asp.ASPManager#translate
    */
   protected final String translate( String text, String p1 )
   {
      return getASPManager().translateJavaText(text,p1);
   }

   //==========================================================================
   //  Profile
   //==========================================================================

   /**
    * Writes a named item (string or buffer) to the profile buffer.
    */
   private void writeItem( Item item )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.writeItem("+item+")" );

      if( porthand==null ) return;

      Buffer profile = porthand.getProfile();
      //if( profile == null ) profile = porthand.setProfile( getASPConfig().getFactory().getBuffer() );
      if( profile == null ) profile = porthand.setProfile( ProfileUtils.newProfileBuffer() );

      synchronized(profile)
      {
         Item existing_item = profile.getItem( item.getName(), null );

         if ( existing_item==null )
            profile.addItem( item );
         else
            existing_item.setValue( item.getValue() );
      }
   }

   /**
    * Reads a named item (string or buffer) from the profile buffer.
    * Returns null if such an item does not exist.
    */
   private Item readItem( String name )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.readItem("+name+")");

      if( porthand==null || porthand.getProfile()==null ) return null;

      Item item = porthand.getProfile().getItem( name, null );

      return item;
   }

   /**
    * Writes a named buffer to the user profile.
    * The cached profile information will be updated directly, but the
    * profile information stored in the database will be updated only
    * if this function is called within the submitCustomization() function.
    */
   protected final void writeProfileBuffer( String name, ASPBuffer buf )
   {
      if(portal.getASPPage().getASPProfile().isUserProfileDisabled())
         return;
      //writeItem( new Item( name, buf.getBuffer() ) );
      writeItem( new ProfileItem(name, ProfileBuffer.bufferToProfileBuffer(buf.getBuffer())) );
   }

   /**
    * Reads a named buffer from the user profile.
    * Returns null if such buffer does not exist.
    */
   protected final ASPBuffer readProfileBuffer( String name )
   {
      Item item = readItem( name );
      return item==null ? null : (new ASPBuffer(getASPManager())).construct( (Buffer)item.getBuffer().clone() );
   }

   /**
    * Writes a named string to the user profile.
    * The cached profile information will be updated directly, but the
    * profile information stored in the database will be updated only
    * if this function is called within the submitCustomization() function.
    */
   protected final void writeProfileValue( String name, String value )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.writeProfileValue("+name+","+value+")");
      if(portal.getASPPage().getASPProfile().isUserProfileDisabled())
         return;
      //writeItem( new Item( name, value ) );
      writeItem( new ProfileItem( name, value ) );
   }

   /**
    * Reads a named value from the query string or, if such value does not exist,
    * from the user profile.
    * If it is a CSV, Value is also evaluated and also if an ASPField is found with the same name as the named value
    * the CSV value is formatted according to the ASPField's data type.
    * Returns 'def_value' otherwise.
    */
   protected final String readProfileValue( String name, String def_value)
   {
      ASPField f = (isASPField(name))? getASPField(name): null;
      return readProfileValue(name,def_value,true,false,f);
   }

   /**
    * Reads a named value from the query string or, if such value does not exist,
    * from the user profile.
    * Returns 'def_value' otherwise.
    */
   protected final String readAbsoluteProfileValue( String name, String def_value)
   {
      return readProfileValue(name,def_value,true,true,null);
   }

   /**
    * Reads a named value from the query string or, if such value does not exist,
    * from the user profile.
    * If it is a CSV, Value is also evaluated and also if an ASPField is found with the same name as the named value
    * the CSV value is formatted according to the ASPField's data type.
    * Returns 'def_value' otherwise.
    */
   protected final String readProfileValue( String name, String def_value, boolean read_global)
   {
      ASPField f = (isASPField(name))? getASPField(name): null;
      return readProfileValue(name,def_value,read_global,false,f);
   }

   /**
    * Reads a named value from the query string or, if such value does not exist,
    * from the user profile.
    * Returns 'def_value' otherwise.
    */
   protected final String readAbsoluteProfileValue( String name, String def_value, boolean read_global)
   {
      return readProfileValue(name,def_value,read_global,true,null);
   }

   private final String readProfileValue( String name, String def_value, boolean read_global, boolean absolute, ASPField format_by_field )
   {
      if(DEBUG) 
          debug(this+": ASPPortletProvider.readProfileValue("+name+","+def_value+")");
      
      String strProfileValue = def_value;
      if( read_global )
      {
         String global = portal.getGlobalVariable(name);
         if( !Str.isEmpty(global) )
         {
             StringTokenizer st = new StringTokenizer(global,",");
             if(st.hasMoreTokens())
                strProfileValue = st.nextToken();
             else
                strProfileValue = global;
             
             /*if (!Str.isEmpty(strProfileValue) && !absolute && getASPManager().isCSVName(strProfileValue))
               return getASPManager().getCSVValue(strProfileValue, def_value, format_by_field);
             else*/
               return strProfileValue;
         }
      }

      Item item = readItem( name );
      strProfileValue = item==null ? def_value : item.getString();
      if (!Str.isEmpty(strProfileValue) && !absolute && getASPManager().isCSVName(strProfileValue))
      {
         if (DEBUG)
             debug("----- Read profile value found as CSV ");
         return getASPManager().getCSVValue(strProfileValue, def_value, format_by_field);
      }
      
      return strProfileValue;
   }

   /**
    * Reads a named value from the query string or, if such value does not exist,
    * from the user profile.
    * Returns null otherwise.
    */
   protected final String readProfileValue( String name )
   {
      ASPField f = (isASPField(name))? getASPField(name): null;
      return readProfileValue(name,null,true,false,f);
   }

   protected final String readAbsoluteProfileValue( String name )
   {
      return readProfileValue(name,null,true,true,null);
   }

   /**
    * Writes a named boolean flag to the user profile.
    * The cached profile information will be updated directly, but the
    * profile information stored in the database will be updated only
    * if this function is called within the submitCustomization() function.
    */
   protected final void writeProfileFlag( String name, boolean value )
   {
      if(portal.getASPPage().getASPProfile().isUserProfileDisabled())
         return;
      /*
      if (value)
         //writeItem( new Item( name, "TRUE" ) );
         writeItem( new ProfileItem( name, "TRUE" ) );
      else
         //writeItem( new Item( name, "FALSE" ) );
         writeItem( new ProfileItem( name, "FALSE" ) );
      */
      ProfileItem item = new ProfileItem(name, value ? "TRUE" : "FALSE");
      item.setType("B");
      writeItem(item);
   }

   /**
    * Reads a named boolean from the query string or, if such variable does not exist,
    * from the user profile.
    * Returns 'def_value' otheriwse.
    */
   protected final boolean readProfileFlag( String name, boolean def_value )
   {
      return readProfileFlag( name, def_value, true );
   }

   protected final boolean readProfileFlag( String name, boolean def_value, boolean read_global )
   {
      if(read_global)
      {
         String global = portal.getGlobalVariable(name);
         if(!Str.isEmpty(global))
           return "TRUE".equals(global.toUpperCase()) ? true : false;
      }
      Item item = readItem( name );
      if (item==null) return def_value;
      if ( "TRUE".equals( item.getString() ) )
         return true;
      else
         return false;
   }

   /**
    * Put some code here if you want to clear infromation
    * writen to global profile within the portlet. Method is called
    * from ASPPortletProfile.rearrangeColumns() for portlets which are
    * close to be removed from portal.
    */
   protected void clearGlobalProfile()
   {

   }

   //==========================================================================
   //  Functions that replace the corresponding functions in ASPManager
   //==========================================================================

   /**
    * Returns a Date object using a client format date string.
    * @param blk Current block.
    */
   protected Date parseDate(String name, String date)
   {
       if(DEBUG) debug(this+": ASPPortletProvider.parseDate()");
       return getASPManager().parseDate( name, date, this);
   }

   /**
    * Call the submit(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    * The ProgID for the default MTS component is defined in the configuration
    * parameter SERVER.PROGID.
    */
   protected final ASPTransactionBuffer submit( ASPTransactionBuffer request )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.submit()");
      return getASPManager().callMTS(request,null,true,true,this);
   }

   /**
    * Call the perform(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    * Not allowed to run before the page is defined.
    */
   protected final ASPTransactionBuffer perform( ASPTransactionBuffer request )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.perform()");

      if( isUndefined() )
         setPerformWhileUndefined();

      return getASPManager().callMTS(request,null,false,true,this);
   }

   /**
    * Pass the request buffer to the default MTS server component
    * for execution. Return the response buffer, if the request has been
    * successfully performed. Otherwise, format an error page, send it
    * to the client and return null. Use the config user.
    * Only allowed to run while the page is still in the undefine state.
    */
   protected final ASPTransactionBuffer performConfig( ASPTransactionBuffer request )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.performConfig()");

      try
      {
         if( !isUndefined() )
            throw new FndException("FNDPPRVEPERC: Not allowed to call performConfig() if the page is already defined.");

         request.addConfigRequestHeader();
      }
      catch( Throwable any )
      {
         error(any);
      }

      return getASPManager().callMTS(request,null,false,true,this);
   }

   /**
    * Call the validate(ASPTransactionBuffer, String) method passing
    * the default MTS server component as the second argument.
    */
   protected final ASPTransactionBuffer validate( ASPTransactionBuffer request )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.validate()");
      return getASPManager().callMTS(request,null,false,false,this);
   }

   //==========================================================================
   //  ASPField functions that replace the corresponding functions in ASPBlock and ASPPage
   //==========================================================================

   /**
    * Define a new field having the specified name, default type ("String") and
    * no format mask.
    * The specified name can contain a comma-separated list of field names.
    * Return a reference to the last created field.
    */
   protected final ASPField addField( ASPBlock blk, String name )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.addField("+blk+","+name+")");
      return blk.addField(name);
   }

   /**
    * Define a new field having the specified name, the specified type
    * and default format mask.
    * The specified name can contain a comma-separated list of field names.
    * Return a reference to the last created field.
    */
   protected final ASPField addField( ASPBlock blk, String name, String type )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.addField("+blk+","+name+","+type+")");
      return blk.addField(name, type);
   }

   /**
    * Define a new field having the specified name, type and format mask.
    * The specified name can contain a comma-separated list of field names.
    * Return a reference to the last created field.
    */
   protected final ASPField addField( ASPBlock blk, String name, String type, String mask )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.addField("+blk+","+name+","+type+","+mask+")");
      return blk.addField(name, type, mask);
   }

   /**
    * Return a reference to the named ASPField. Throw an Exception if the
    * specified field has not been defined on the current ASP page.
    */
   public final ASPField getASPField( String name )
   {
      if(DEBUG) debug(this+": ASPPortletProvider.getASPField("+name+")");
      return super.getASPField(name);
   }

   //==========================================================================
   //  Command Bar & Popup functions
   //==========================================================================

   /**
    * Return true if any command bar function has been activated
    * during the previous request.
    * The portlet framework doesn't support visible command bars.
    * Activation of command bar functions is only possible by
    * popup menus in an ASP table or programmatic by links.
    */
   protected final boolean commandBarActivated()
   {
      return portal.commandBarActivated();
   }

   /**
    * Return true if any standard command bar function
    * has been activated during the previous request.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#commandBarActivated
    */
   protected final boolean cmdBarStandardCommandActivated()
   {
      return portal.cmdBarStandardCommandActivated();
   }

   /**
    * Return true if any custom command bar function
    * has been activated during the previous request.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#commandBarActivated
    */
   protected final boolean cmdBarCustomCommandActivated()
   {
      return portal.cmdBarCustomCommandActivated();
   }

   /**
    * Return the name of a block that the activated command bar is connected to.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#commandBarActivated
    */
   protected final String getCmdBarBlockName()
   {
      return portal.getCmdBarBlockName();
   }

   /**
    * Return the id of the activated standard command.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#commandBarActivated
    */
   protected final String getCmdBarStandardCommandId()
   {
      return portal.getCmdBarStdId();
   }

   /**
    * Return the id of the activated custom command.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#commandBarActivated
    */
   protected final String getCmdBarCustomCommandId()
   {
      return portal.getCmdBarCustId();
   }

   //==========================================================================
   //  Directory locations.
   //==========================================================================

   /**
    * Returns location of the script directory. Default /Common/scripts/.
    */
   protected final String getScriptsLocation()
   {
      return getASPConfig().getScriptsLocation(false);
   }

   /**
    * Returns location of the images directory. Default /Common/images/.
    */
   protected final String getImagesLocation()
   {
      return getASPConfig().getImagesLocation();
   }

   /**
    * Returns location of the images directory with RTL enabled. Default /Common/images/.
    */
   protected final String getImagesLocationWithRTL()
   {
      return getASPConfig().getImagesLocationWithRTL();
   }

   //==========================================================================
   //  Generation of HTML tags. To be called from printContents() and printCustomBody()
   //==========================================================================

   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#setFontStyle
    */
   protected final static int NONE   = 0;
   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#setFontStyle
    */
   protected final static int BOLD   = 1;
   /**
    * Constant for font style definition.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#setFontStyle
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
    * @see ifs.fnd.asp.ASPPortletProvider#endFont
    */
   protected final void setFontStyle( int style )
   {
      //out.append("<p style=\"FONT: ");
      //out.append( style==BOLD ? "BOLD" : (style==ITALIC ? "ITALIC" : "") );
      //out.append(" 8pt Verdana; color:black;\">\n");

      if(style==BOLD)
         out.append("<p class=\"portletFontBold\">");
      else if(style==ITALIC)
         out.append("<p class=\"portletFontItalic\">");

   }

   /**
    * ***********************************************************************
    * Method should be made obsolete in major release
    * ************************************************************************
    * Cancel font setting defined by setFontStyle() function.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#setFontStyle
    */
   protected final void endFont()
   {
      out.append("</p>");
   }


   /**
    * Append a string to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str )
   {
      out.append(str);
   }

   /**
    * Append strings to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str1, String str2 )
   {
      out.append(str1,str2);
   }

   /**
    * Append strings to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str1, String str2, String str3 )
   {
      out.append(str1,str2,str3);
   }

   /**
    * Append strings to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str1, String str2, String str3, String str4 )
   {
      out.append(str1,str2,str3,str4);
   }

   /**
    * Append strings to the HTML-output without any conversion.
    */
   protected final void appendToHTML( String str1, String str2, String str3, String str4, String str5 )
   {
      out.append(str1,str2,str3,str4,str5);
   }


   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined styles.
    */
   protected final void printText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=normalTextValue>");   ///??????????????????????????????
      if(Str.isEmpty(str))
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(translate(str),true));
      out.append("</font>");
   }
   
   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined bold styles.
    */   
   protected final void printBoldText( String str )
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
   protected final void printItalicText( String str )
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
   protected final void printBoldItalicText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=\"italicTextValue boldTextValue\">");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }
   
   // Added by Terry 20131105
   /**
    * Append a string after translation to the HTML-output.
    * Apply predefined hilite styles.
    */ 
   protected final void printHiliteText( String str )
   {
      ASPManager mgr = getASPManager();
      out.append("<font class=hiliteTextValue>");
      if ( Str.isEmpty(str) )
         out.append("&nbsp;");
      else
         out.append(mgr.HTMLEncode(mgr.translateJavaText(str)));
      out.append("</font>");
   }
   // Added end

   /**
    * Append amount of spaces ('&nbsp;' sequences) to the HTML-output.
    */
   protected final void printSpaces( int spaces )
   {
      for( int i=0; i<spaces; i++)
         out.append("&nbsp;");
   }

   /**
    * Append a &lt;br&gt; to the HTML-output.
    */
   protected final void printNewLine()
   {
      out.append("<br>\n");
   }


   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printField( String name, String value )
   {
      printField(name, value, 0, 0, null );
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printField( String name, String value, int size )
   {
      printField(name, value, size, 0, null );
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    */
   protected final void printField( String name, String value, int size, int maxlength )
   {
      printField(name, value, size, maxlength, null );
   }

   /**
    * Append a named input tag to the HTML-output. Apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * Will be called in onChange event handler.
    */
   protected final void printField( String name, String value, int size, String func )
   {
      printField(name, value, size, 0, func );
   }

   /**
    * Append a named input tag of type 'text' to the HTML-output. Apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * Will be called in onChange event handler.
    */
   protected final void printField( String name, String value, int size, int maxlength, String func )
   {
      ASPManager mgr = getASPManager();
      out.append("<input class='editableTextField' type=text");
      out.append(" name=",addProviderPrefix(),name);
      out.append(" value=\"",mgr.HTMLEncode(value),"\"");
      if(size>0)
      {
         out.append(" size=\"");
         out.appendInt(mgr.isExplorer() ? size : (int)Math.round(size*.6));
         out.append("\"");
      }
      if(maxlength>0)
      {
         out.append(" maxlength=\"");
         out.appendInt(maxlength);
         out.append("\"");
      }

      boolean isDateTime=false;

      try
      {
        if (isASPField(name))
            isDateTime  = (getASPField(name).isTime()||getASPField(name).isDateTime());
      }
      catch(Exception e){ isDateTime=false; }

      if (isDateTime)
      {
         String mask = getASPField(name).getMask();
         String block_name = getASPField(name).getBlock().getName();
         String id = getId();
         String field = "_"+id+"_"+name;

         out.append(" onChange=\"javascript:setFormReference('"+id+"');");

         if(!Str.isEmpty(func))
         {
            out.append("formatDate_("+field+",'"+mask+"','"+block_name+"')");
            out.append(";",func,"(this,'",getId(),"')\"");
         }
         else
            out.append("formatDate_("+field+",'"+mask+"','"+block_name+"')\"");

         out.append(">");
         printCalender(name);
         printDateTimeMask(name);
      }
      else if(!Str.isEmpty(func))
      {
         out.append(" onChange=\"javascript:",func,"(this,'",getId(),"')\"");
         out.append(">");
      }
      else
         out.append(">");
   }


   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    */
   protected final void printLOV( String field, String url )
   {
      printLOV(field,url,null,true,null,null,null,0,0,false);
   }
   
   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   multichoice - a flag for enable/disable multi-choice option of the LOV; default 'false'<br>
    */
   // Added by Terry 20140828
   // Add Multichoice parameter
   protected final void printLOV( String field, String url, boolean multichoice )
   {
      printLOV(field,url,null,true,null,null,null,0,0,multichoice);
   }
   // Added end

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    */
   protected final void printLOV( String field, String url, String title )
   {
      printLOV(field,url,title,true,null,null,null,0,0,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    */
   protected final void printLOV( String field, String url, String title, String where_stmt )
   {
      printLOV(field,url,title,true,where_stmt,null,null,0,0,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    */
   protected final void printLOV( String field, String url, String title, int width, int height )
   {
      printLOV(field,url,title,true,null,null,null,width,height,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    */
   protected final void printLOV( String field, String url, String title, String where_stmt, int width, int height )
   {
      printLOV(field,url,title,true,where_stmt,null,null,width,height,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   autosearch  - a flag that tells the Web Kit to run or not query automatically; default 'true'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *   order_by    - a SQL ORDER BY clause that will be added to the query; default null<br>
    *   group_by    - a SQL GROUP BY clause that will be added to the query; default null<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    */
   protected final void printLOV( String field, String url, String title, boolean autosearch,
                                  String where_stmt, String order_by, String group_by, int width, int height )
   {
      printLOV(field,url,title,autosearch,where_stmt,order_by,group_by,width,height,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   url         - location of the LOV-page; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   autosearch  - a flag that tells the Web Kit to run or not query automatically; default 'true'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *   order_by    - a SQL ORDER BY clause that will be added to the query; default null<br>
    *   group_by    - a SQL GROUP BY clause that will be added to the query; default null<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    *   multichoice - a flag for enable/disable multi-choice option of the LOV; default 'false'<br>
    */
   protected final void printLOV( String field, String url, String title, boolean autosearch,
                                  String where_stmt, String order_by, String group_by, int width, int height, boolean multichoice )
   {
      //JavaScript function that will be called:
      //   function lovPortal(id,fld,url,title,auto,where,orderby,groupby,width,height)
      ASPConfig cfg = getASPConfig();
      if(width==0)  width  = cfg.getLOVWindowWidth();
      if(height==0) height = cfg.getLOVWindowHeight();

      String id = getId();
      String app_path = cfg.getApplicationPath();

      if (!url.startsWith(app_path)) url = app_path +"/" +url;

      //out.append("<X","javascript:setFormReference('"+id+"');",cfg.getLOVTag1Iid(false));
      String quote = "\"";
      //out.append("<X",cfg.getLOVTag1Iid(false));
      out.append("<X> <a href=",quote,"javascript:setFormReference('"+id+"');");
      out.append("lovPortal('",id,"','",field,"','");
      out.append(url,"','",translate(Str.isEmpty(title)?"FNDPRVLOVTITLE: List of values":title),"','",autosearch?"":"N");
      out.append("','",multichoice?"true":"");
      out.append("','", Str.isEmpty(where_stmt) ?"":where_stmt);
      out.append("','", Str.isEmpty(order_by)   ?"":order_by);
      out.append("','", Str.isEmpty(group_by)   ?"":group_by, "',");
      out.appendInt(width);
      out.append(",");
      out.appendInt(height);
      out.append(")");
      out.append(cfg.getLOVTag2(false),">");
   }


   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view )
   {
      printDynamicLOV(field,view,null,true,null,null,null,0,0,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   multichoice - a flag for enable/disable multi-choice option of the LOV; default 'false'<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   // Added by Terry 20140828
   // Add Multichoice parameter
   protected final void printDynamicLOV( String field, String view, boolean multichoice )
   {
      printDynamicLOV(field,view,null,true,null,null,null,0,0,multichoice);
   }
   // Added end
   
   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view, String title )
   {
      printDynamicLOV(field,view,title,true,null,null,null,0,0,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view, String title, String where_stmt )
   {
      printDynamicLOV(field,view,title,true,where_stmt,null,null,0,0,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view, String title, int width, int height )
   {
      printDynamicLOV(field,view,title,true,null,null,null,width,height,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   autosearch  - a flag that tells the Web Kit to run or not query automatically; default 'true'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *   order_by    - a SQL ORDER BY clause that will be added to the query; default null<br>
    *   group_by    - a SQL GROUP BY clause that will be added to the query; default null<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view, String title, boolean autosearch,
                                         String where_stmt, String order_by, String group_by, int width, int height )
   {
      printDynamicLOV(field,view,title,autosearch,where_stmt,order_by,group_by,width,height,false);
   }

   /**
    * Append an image-tag representing a LOV-icon to the HTML-output.
    * Input parameters:<br>
    *   field       - name of the HTML-field, created by calling one of printField() methods; required<br>
    *   view        - the name of the database view; required<br>
    *   title       - the title of the LOV-window; default 'List of values'<br>
    *   autosearch  - a flag that tells the Web Kit to run or not query automatically; default 'true'<br>
    *   where_stmt  - a SQL WHERE condition that will be added to the query; default null<br>
    *   order_by    - a SQL ORDER BY clause that will be added to the query; default null<br>
    *   group_by    - a SQL GROUP BY clause that will be added to the query; default null<br>
    *   width       - the width of the LOV window; default specified in ASPConfig.ifm<br>
    *   height      - the height of the LOV window; default specified in ASPConfig.ifm<br>
    *   multichoice - a flag for enable/disable multi-choice option of the LOV; default 'false'<br>
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLOV
    */
   protected final void printDynamicLOV( String field, String view, String title, boolean autosearch,
                                         String where_stmt, String order_by, String group_by, int width, int height, boolean multichoice )
   {
      String url = getASPConfig().getDynamicLOVURL(false);
      printLOV(field,
               url + (url.indexOf("?")>0 ? "&" : "?") + "__DYNAMIC_LOV_VIEW=" + view,
               title,
               autosearch,where_stmt,order_by,group_by,width,height,multichoice);
   }

   /**
    * Append an image-tag representing a calender icon to the HTML-output.
    * Input parameters:<br>
    *   field - name of the HTML-field, created by calling one of printField() methods; required<br>
    */
   protected final void printCalender(String field)
   {
      ASPManager mgr = getASPManager();
      ASPConfig cfg = getASPConfig();
      if (!(mgr.isExplorer() || mgr.isMozilla())) return;

      String _mask = getASPField(field).getMask();
      String _label = getASPField(field).getJSLabel();
      _mask = mgr.replace(_mask,"'","\\\'");

      int type_id = getASPField(field).getTypeId();
      boolean time = (type_id == DataFormatter.DATETIME || type_id == DataFormatter.TIME);

      String id = getId();
      //out.append("<X",cfg.getCalendarTag1());

      out.append("<X> <a href=\"javascript:setFormReference('"+id+"');","showCalendar('");
      out.append("_",id,"_",field);
      out.append(cfg.getCalendarTag2());
      out.append("''");
      out.append(",'",_mask,"',");
      out.appendInt(-1);
      out.append(",'",_label,"'");
      out.append(",'",time+"","'");
      out.append(cfg.getCalendarTag4());
      out.append(cfg.getCalendarTag3(),">");
   }

   /**
    * Append a string representing the mask of a date field to the HTML-output.
    * Input parameters:<br>
    *   field - name of the HTML-field, created by calling one of printField() methods; required<br>
    */
   protected final void printDateTimeMask(String field)
   {
      ASPManager mgr = getASPManager();
      ASPConfig cfg = getASPConfig();
      String translated_mask = getASPField(field).getMask();
      String[] maskarr = cfg.getTranslatedDateTimeMask();

      if(maskarr!=null)
      {
         char[] tempChars = translated_mask.toCharArray();
         StringBuffer tempStr = new StringBuffer();
         for (int i=0;i<tempChars.length;i++)
         {
            switch (tempChars[i])
            {
               case 'G': tempStr.append(maskarr[ASPConfigFile.ERA]);
                  break;
               case 'y': tempStr.append(maskarr[ASPConfigFile.YEAR]);
                  break;
               case 'M': tempStr.append(maskarr[ASPConfigFile.MONTH_IN_YEAR]);
                  break;
               case 'd': tempStr.append(maskarr[ASPConfigFile.DAY_IN_MONTH]);
                  break;
               case 'h': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_AMPM_112]);
                  break;
               case 'H': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_DAY_023]);
                  break;
               case 'm': tempStr.append(maskarr[ASPConfigFile.MINUTE_IN_HOUR]);
                  break;
               case 's': tempStr.append(maskarr[ASPConfigFile.SECOND_IN_MINUTE]);
                  break;
               case 'S': tempStr.append(maskarr[ASPConfigFile.MILLISECOND]);
                  break;
               case 'E': tempStr.append(maskarr[ASPConfigFile.DAY_IN_WEEK]);
                  break;
               case 'D': tempStr.append(maskarr[ASPConfigFile.DAY_IN_YEAR]);
                  break;
               case 'F': tempStr.append(maskarr[ASPConfigFile.DAY_OF_WEEK_IN_MONTH]);
                  break;
               case 'w': tempStr.append(maskarr[ASPConfigFile.WEEK_IN_YEAR]);
                  break;
               case 'W': tempStr.append(maskarr[ASPConfigFile.WEEK_IN_MONTH]);
                  break;
               case 'a': tempStr.append(maskarr[ASPConfigFile.AMPM_MARKER]);
                  break;
               case 'k': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_DAY_124]);
                  break;
               case 'K': tempStr.append(maskarr[ASPConfigFile.HOUR_IN_AMPM_011]);
                  break;
               case 'z': tempStr.append(maskarr[ASPConfigFile.TIME_ZONE]);
                  break;
               default:tempStr.append(tempChars[i]);
                  break;
            }
         }
         translated_mask = tempStr.toString();
      }
      out.append("<font class=normalTextValue>");
      out.append("&nbsp;(");
      out.append(translated_mask,")");
      out.append("</font>");
   }

   /**
    * Append a named hidden input tag to the HTML-output.
    */
   protected final void printHiddenField( String name, String value )
   {
      out.append("<input type=hidden name=",addProviderPrefix(),name);
      out.append(" value=\"",getASPManager().HTMLEncode(value),"\">");
   }

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    */
   protected final void printTextArea( String name, String value, int rows, int cols )
   {
      printTextArea( name, value, rows, cols, null );
   }

   /**
    * Append a named textarea to the HTML-output. Apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    */
   protected final void printTextArea( String name, String value, int rows, int cols, String func )
   {
      ASPManager mgr = getASPManager();
      out.append("<textarea class='editableTextArea'");
      out.append(" name=",addProviderPrefix(),name);
      out.append(" rows=");
      out.appendInt(rows);
      out.append(" cols=");
      out.appendInt(mgr.isExplorer() ? cols : (int)Math.round(cols*.6));
      if(!Str.isEmpty(func))
      {
         String id = getId();
         out.append(" onChange=\"javascript:setFormReference('",id,"');");
         out.append(func,"(this,'",id,"')\"");
      }
      out.append(">",mgr.HTMLEncode(value),"</textarea>");
   }


   /**
    * Append a named check box input tag to the HTML-output and apply predefined styles.
    * Default value of this check box is set to 'TRUE'.
    */
   protected final void printCheckBox( String name, boolean checked )
   {
      printCheckBox( name, "TRUE", checked, null );
   }

   /**
    * Append a named check box input tag to the HTML-output and apply predefined styles.
    * Default value of this check box is set to 'TRUE'.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.
    */
   protected final void printCheckBox( String name, boolean checked, String func )
   {
      printCheckBox( name, "TRUE", checked, func );
   }

   /**
    * Append a named check box input tag to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.
    */
   protected final void printCheckBox( String name, String value, boolean checked, String func )
   {
      out.append("<input class='checkbox' type=checkbox");
      out.append(" name=",addProviderPrefix(),name);
      out.append(" value=\"" + value + "\"");
      if(checked)
         out.append(" CHECKED");
      if(!Str.isEmpty(func))
      {
         String id = getId();
         out.append(" onClick=\"javascript:setFormReference('",id,"');");
         out.append(func,"(this,'",id,"')\"");
      }
      out.append(">");
   }


   /**
    * Append a named radio button input tag to the HTML-output and apply predefined styles.
    * The specified label will be translated.
    */
   protected final void printRadioButton( String name, String value, String label, boolean checked )
   {
      printRadioButton( name, value, label, checked, null );
   }

   /**
    * Append a named radio button input tag to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler. The specified label will be translated.
    */
   protected final void printRadioButton( String name, String value, String label, boolean checked, String func )
   {
      out.append("<input class=radioButton type=radio");
      out.append(" name=",addProviderPrefix(),name);
      out.append(" value=\"" + value + "\"");
      if(checked)
         out.append(" CHECKED");
      if(!Str.isEmpty(func))
      {
         String id = getId();
         out.append(" onClick=\"javascript:setFormReference('",id,"');");
         out.append(func,"(this,'",id,"')\"");
      }
      out.append("><font class=normalTextValue>",translate(label),"</font>");
   }



   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf )
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
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key, String func )
   {
      printSelectBox( name, aspbuf, key, func, false, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    */
   protected final void printSelectBox( String name, ASPBuffer aspbuf, String key, String func, int size )
   {
      printSelectBox( name, aspbuf, key, func, false, size );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning.
    * The data to option-tags is taken from the aspbuf-parameter.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf )
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
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key )
   {
      printSelectBox( name, aspbuf, key, null, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String func )
   {
      printSelectBox( name, aspbuf, key, func, true, 1 );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags without an empty tag at the beginning
    * and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   protected final void printMandatorySelectBox( String name, ASPBuffer aspbuf, String key, String func, int size )
   {
      printSelectBox( name, aspbuf, key, func, true, size );
   }

   /**
    * Append a named select-tag to the HTML-output and apply predefined styles.
    * Generate html &lt;option&gt; tags with an empty tag at the beginning if
    * the mandatory-flag is true and select a value thet corresponds to the key-parameter.
    * The data to option-tags is taken from the aspbuf-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    */
   private void printSelectBox( String name, ASPBuffer aspbuf, String key, String func, boolean mandatory, int size )
   {
      out.append("<select class='selectbox' size=");
      out.appendInt(size);
      out.append(" name=",addProviderPrefix(),name);
      if(!Str.isEmpty(func))
      {
         String id = getId();
         out.append(" onChange=\"javascript:setFormReference('",id,"');");
         out.append(func,"(this,'",id,"')\"");
      }
      out.append(">");
      if(mandatory)
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
   protected final void printIIDSelectBox( ASPField fld ) throws FndException
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
   protected final void printIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, false );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected final void printIIDSelectBox( ASPField fld, String key, String func ) throws FndException
   {
      printIIDSelectBox( fld, key, func, false );
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
   protected final void printMandatoryIIDSelectBox( ASPField fld ) throws FndException
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
   protected final void printMandatoryIIDSelectBox( ASPField fld, String key ) throws FndException
   {
      printIIDSelectBox( fld, key, null, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * without an empty tag at the beginning and select a value that corresponds to the key-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   protected final void printMandatoryIIDSelectBox( ASPField fld, String key, String func ) throws FndException
   {
      printIIDSelectBox( fld, key, func, true );
   }

   /**
    * Append a select-tag to the HTML-output, populated with IID-values corresponded
    * to a given field and apply predefined styles. Generate HTML &lt;option&gt; tags
    * with an empty tag at the beginning if the mandatory-flag is true and select a value
    * thet corresponds to the key-parameter.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onChange event handler.
    * The ASP field must be defined as a select box and be enumerated by IID.
    *
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateListBox
    * @see ifs.fnd.asp.ASPHTMLFormatter#populateMandatoryListBox
    * @see ifs.fnd.asp.ASPField#getClientValues
    * @see ifs.fnd.asp.ASPField#setSelectBox
    */
   private void printIIDSelectBox( ASPField fld, String key, String func, boolean mandatory ) throws FndException
   {
      if(!fld.isSelectBox())
         throw new FndException("FNDPRVNSELBOX: The field &1 is not defined as Select Box.", fld.getName());
      printSelectBox( fld.getName(), fld.getClientValues(), key, func, mandatory, 1 );
   }


   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * If the href represents a relative reference the application root will be added.
    */
   protected final void printAbsoluteLink( String value, String href )
   {
      printLink( value, href, true, false, false, false, null, true, null, null );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    */
   protected final void printLink( String value, String href )
   {
      printLink( value, href, false, false, false );
   }

   /**
    * Append a secure link to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated. link will be
    * inactivate according to the security rights for for the given security
    * object list.
    * @param value value to be display
    * @param href hyperlink
    * @param security_objects comma seperated security objects
    */
   protected final void printLink( String value, String href, String security_objects )
   {
      //printLink( value, href, false, false, false );
      printLink( value, href, false, false, false, false, null, isObjectAccessible(security_objects), null, null);
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * @param imgpath image uri
    * @param href hyperlink uri
    */
   protected final void printImageLink( String imgpath, String href )
   {
      printLink( imgpath, href, true, false, false );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * @param imgpath image uri
    * @param href hyperlink uri
    * @param name name of the image
    * @param title title/alt tag
    */   
   protected final void printImageLink( String imgpath, String href, String name, String title )
   {
      printLink( imgpath, href, false, true, false, false, null, true , name, title);
   }
   

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    */
   protected final void printHoverImageLink( String imgpath, String hovimgpath, String href )
   {
      printLink( imgpath, href, false, true, false, false, hovimgpath, true, null, null );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   protected final void printScriptLink( String value, String func )
   {
      printLink( value, func, false, true, false );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * link will be inactivate according to the security rights for the given security
    * object list.
    * @param value value to be display
    * @param func javascript function
    * @param security_objects comma seperated security objects
    */

   protected final void printScriptLink( String value, String func, String security_objects )
   {
      printLink( value, func, false, false, true, false, null, isObjectAccessible(security_objects), null, null);
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    * Any text, send in the value-parameter, will be translated.
    */
   protected final void printSubmitLink( String value )
   {
      printLink( value, null, false, true, true );
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   protected final void printSubmitLink( String value, String func )
   {
      printLink( value, func, false, true, true );
   }

   /**
    * Append a secure link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    * Any text, send in the value-parameter, will be translated.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * Link will be dissable/enable according to the access rights for the given list of security objects.
    * @param value value to be display
    * @param func javascript function to be called
    * @param security_objects comma seperated security objects
    */
   protected final void printSubmitLink( String value, String func, String security_objects )
   {
      printLink( value, func, false, false, true, true, null, isObjectAccessible(security_objects), null, null);
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * @param imgpath image uri
    * @param func javascript funstion
    */
   protected final void printScriptImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, false );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * @param imgpath image uri
    * @param func javascript funstion
    * @param name name of the image
    * @param title title/alt tag
    */   
   protected final void printScriptImageLink( String imgpath, String func, String name, String title )
   {
      printLink( imgpath, func, false, true, true, false, null, true , name, title);
   }
   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   protected final void printScriptHoverImageLink( String imgpath, String hovimgpath, String func )
   {
      printLink( imgpath, func, false, true, true, false, hovimgpath, true , null, null);
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    */
   protected final void printSubmitImageLink( String imgpath )
   {
      printLink( imgpath, null, true, true, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   protected final void printSubmitImageLink( String imgpath, String func )
   {
      printLink( imgpath, func, true, true, true );
   }

   /**
    * Append an image link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet).
    * The alternative image specification can be send in hovimgpath-parameter
    * to attain the hover effect.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   protected final void printSubmitHoverImageLink( String imgpath, String hovimgpath, String func )
   {
      printLink( imgpath, func, false, true, true, true, hovimgpath, true , null, null);
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    */
   private void printLink( String value, String link, boolean image, boolean script, boolean submit )
   {
      printLink( value, link, false, image, script, submit, null, true , null, null);
   }

   /**
    * Append a link (an A-tag) to the HTML-output and apply predefined styles.
    * The link will submit the request (current portlet) if the submit-flag is true.
    * The value can be any text, which will be translated, or, if the image-parameter
    * is true, an image specification. Then the alternative image specification can be
    * send in hovimgpath-parameter to attain the hover effect.
    * Run a JavaScript function given in the link-parameter rather then an URL
    * if the script-parameter is true.
    * Try to add absolute reference if abshref is true.
    * Specified JavaScript function takes two parameters: object reference and portlet ID.
    * Note: if the value contains '&&' only the text within them will be hyperlinked.
    *       eg: for value "only &&this&& word is hyperlinked" only the word 'this' will be hyperlinked.
    */
   private void printLink( String  value,
                           String  link,
                           boolean abshref,
                           boolean image,
                           boolean script,
                           boolean submit,
                           String  hovimgpath,
                           boolean has_access,
                           String image_name,
                           String image_title)
   {
      String text1="";
      String text2="";
      value = translate(value);
      String id = getId();
      ASPManager mgr = getASPManager();

      //link = !Str.isEmpty(link)? mgr.decodedURLWithCSV(link): link;

      if( mgr.isStdPortlet() && !Str.isEmpty(link) && link.indexOf("javascript:")>=0 && link.indexOf("setFormReference")<0 )
      {
         if(DEBUG) debug("ASPPortletProvider.printLink(): adding call to setFormReference() for JSR168 portlet.");
         link = Str.replace(link, "javascript:", "javascript:setFormReference('"+id+"');");
      }

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
      out.append("<font class=normalTextValue>",getASPManager().HTMLEncode(text1,true),"</font>");

      if (has_access)
      {
         out.append("<a class=hyperLink href=\"");
         if(script)
         {
            out.append("javascript:setFormReference('",id,"');");

            if(!Str.isEmpty(link))
            {
               String suf = link.endsWith(")")?"":("(this,'"+id+"')");
               out.append(link,suf);
            }

            if( submit && !Str.isEmpty(link) )
               out.append(";");

            if(submit)
               out.append("submitPortlet('",id,"')");
         }
         else
         {
            //if (mgr.isStdPortlet() && (link.indexOf("javascript:") == -1)) abshref = true;
            if (mgr.isStdPortlet() && (link.indexOf(":") == -1)) abshref = true; //javascript:, mailto: http:, ftp:, https: etc
            if( abshref && link.indexOf("://")<0 && !link.startsWith("/") )
               out.append(getApplicationPath(),"/");
            out.append( link );
         }
         out.append("\"");

         if( image && !Str.isEmpty(hovimgpath) )
         {
            linkseq++;
            out.append(" onmouseover=\"javascript:setFormReference('",id,"');f.",addProviderPrefix(),"IMG");
            out.appendInt(linkseq);
            out.append(".src='",hovimgpath,"'\"");
            out.append(" onmouseout=\"javascript:setFormReference('",id,"');f.",addProviderPrefix(),"IMG");
            out.appendInt(linkseq);
            out.append(".src='",value,"'\"");
         }
         out.append(">");
      }
      else
         out.append("<font class=dissabledLinkValue>");

      if(image)
      {
         out.append("<img");
         if( !Str.isEmpty(hovimgpath) )
         {
            out.append(" name=\"",addProviderPrefix(),"IMG");
            out.appendInt(linkseq);
            out.append("\"");
         }
         else if (!Str.isEmpty(image_name))
            out.append(" name=\"",image_name,"\"");
         
         
         if( value.indexOf("://")<0 && !value.startsWith("/") && mgr.isStdPortlet()) 
            value = getApplicationPath()+"/"+value;
         
         if (!Str.isEmpty(image_title))
            out.append(" title=\"",translate(image_title),"\"");
         
         out.append(" src=\"",value,"\" border=0>");
      }
      else
         out.append( getASPManager().HTMLEncode(translate(value),true) );

      if (has_access)
         out.append("</a>");
      else
         out.append("</font>");
      out.append("<font class=normalTextValue>",getASPManager().HTMLEncode(text2,true),"</font>");
   }


   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    */
   protected final void printButton( String name, String value, String func )
   {
      printButton( name, value, func, false, true );
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler. Button will be enable according to the
    * security rights for the given list of security objects.
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */
   protected final void printButton( String name, String value, String func, String security_objects )
   {
      printButton( name, value, func, false, isObjectAccessible(security_objects));
   }


   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current portlet).
    */
   protected final void printSubmitButton( String name, String value )
   {
      printButton( name, value, null, true, true );
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current portlet).
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.
    */
   protected final void printSubmitButton( String name, String value, String func )
   {
      printButton( name, value, func, true, true );
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current portlet).
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.Button will be enable according to the
    * security rights for the given list of security objects.
    * @param value value to be display
    * @param tag addtional html tags to apply to this button
    * @param sec_objects comma seperated list of security objects
    */

   protected final void printSubmitButton( String name, String value, String func, String security_objects )
   {
      printButton( name, value, func, true, isObjectAccessible(security_objects));
   }

   /**
    * Append a named button input tag to the HTML-output and apply predefined styles.
    * The button will submit the request (current portlet) if the submit-flag is true.
    * Specified JavaScript function takes two parameters: object reference and portlet ID
    * and will be called in onClick event handler.
    */
   private void printButton( String name, String value, String func, boolean submit, boolean has_security )
   {
      out.append("<input class='button' type=button");

      out.append(" name=",addProviderPrefix(),name);
      out.append(" value=\"", getASPManager().HTMLEncode(translate(value),true), "\"");

      if( submit || !Str.isEmpty(func) )
      {
         String id = getId();
         out.append(" onClick=\"javascript:setFormReference('",id,"');");

         if(!Str.isEmpty(func))
            out.append(func,"(this,'",id,"')");

         if( submit && !Str.isEmpty(func) )
            out.append(";");

         if(submit)
            out.append("submitPortlet('",id,"')");

         out.append("\"");
      }

      //out.append(">");
      out.append(!has_security?" disabled":"", ">");
   }


   /**
    * Populate an ASP table.
    * @param tbl ASPTable object to be printed
    */
   protected final void printTable( ASPTable tbl )
   {
      //tbl.setWrap();
      //tbl.disableNoWrap(); - immutable attribute
      tbl.disableOutputChannels();
      tbl.disableEditProperties();
      tbl.unsetSortable();
      tbl.disableQuickEdit();
      //tbl.getBlock().getASPCommandBar().disableCommand(ASPCommandBar.VIEWDETAILS);
      ASPBlock blk = tbl.getBlock();
      if(blk!=null)
      {
         ASPCommandBar bar = blk.getASPCommandBar();
         if(bar!=null)
         {
            bar.disableCommand(ASPCommandBar.VIEWDETAILS);
            //disable any custom commands which do not have secuirty
            Vector cust_cmds = bar.getCustomCommands();
            String cmd_id;
            String plsql_method;
            ASPCommandBarItem cmd;

            for (int i=0; i<cust_cmds.size(); i++)
            {
              cmd = (ASPCommandBarItem) cust_cmds.elementAt(i);
              cmd_id = cmd.getCommandId();
              plsql_method = bar.getSecurePlSqlMethod(cmd_id);
              if (!Str.isEmpty(plsql_method) && !isObjectAccessible(plsql_method))
                 bar.disableCustomCommand(cmd_id);
            }

           //disable standard commands if any
           Buffer commands = blk.getDefinedCommands();
           String method_name="";
           for(int j=0;j<commands.countItems();j++)
           {
             //get fully qualfied PL/SQL method name from commands buffer
             method_name = commands.findItem(commands.getItem(j).getName()+"/METHOD").getString();

             if ( "New__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
             {
               if (!isObjectAccessible(method_name))
               {
                 bar.disableCommand(ASPCommandBar.NEWROW);
                 bar.disableCommand(ASPCommandBar.DUPLICATEROW);
               }
             }
             else if ( "Modify__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
             {
              if (!isObjectAccessible(method_name))
                bar.disableCommand(ASPCommandBar.EDITROW);

             }
             else if ( "Remove__".equals(commands.getItem(j).getName()) && !Str.isEmpty(method_name) )
             {
              if (!isObjectAccessible(method_name))
               bar.disableCommand(ASPCommandBar.DELETE);
             }
          }

      }

     }

      out.append( tbl.populate() );
   }


   /**
    * Append an image-tag to the HTML-output.
    * @param src image source
    */
   protected final void printImage( String src )
   {
      printImage(src,0,0,0,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    * @param src image source
    * @param width image width
    * @param height image height
    * 
    * 
    */
   protected final void printImage( String src, int width, int height )
   {
      printImage(src,width,height,0,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    * @param src image source
    * @param width image width
    * @param height image height
    * @param border border size
    */
   protected final void printImage( String src, int width, int height, int border )
   {
      printImage(src,width,height,border,null );
   }

   /**
    * Append an image-tag to the HTML-output.
    * @param src image source
    * @param width image width
    * @param height image height
    * @param border border size
    * @param usemap map URI
    */
   protected final void printImage( String src, int width, int height, int border, String usemap )
   {
      printImage( src, width, height, border, usemap, null, null);
   }
   
   
   /**
    * Append an image-tag to the HTML-output.
    * @param src image source
    * @param width image width
    * @param height image height
    * @param border border size 
    * @param usemap map URI
    * @param name image name
    * @param title the titile/alt tag
    */   
   protected final void printImage( String src, int width, int height, int border, String usemap, String name, String title)
   {
      ASPManager mgr = getASPManager();
      if( src.indexOf("://")<0 && !src.startsWith("/") && mgr.isStdPortlet()) 
         src = getApplicationPath()+"/"+src;
      
      out.append("<img src=\"",src,"\"");
      if (!Str.isEmpty(name))
         out.append(" name=\"",name,"\"");

      if(width>0)
      {
         out.append(" width=");
         out.appendInt(width);
      }
      if(height>0)
      {
         out.append(" height=");
         out.appendInt(height);
      }
      out.append(" border=");
      out.appendInt(border);
      if( !Str.isEmpty(usemap) )
         out.append(" usemap=\"",usemap,"\"");
      if (!Str.isEmpty(title))
         out.append(" title=\"",translate(title),"\"");
         
      out.append(">");
   }

   private boolean inmap;

   /**
    * Append a map-tag to the HTML-output. Can be used in conjunction to an image-tag.
    * Should be closed by calling endDefineImageMap().
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endDefineImageMap
    */
   protected final void defineImageMap( String name )
   {
      out.append("<map name=\"",name,"\">\n");
      inmap = true;
   }

   /**
    * Close the previously opened by defineImageMap() function map-tag.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#defineImageMap
    */
   protected final void endDefineImageMap()
   {
      inmap = false;
      out.append("</map>\n");
   }

   /**
    * Append an area-tag to the HTML-output. Can be used in conjunction to a map-tag.
    */
   protected final void printImageArea( String shape, String coords, String href, String func, String param )
   {
      out.append("<area shape=",shape," coords=",coords);
      out.append(" href=\"",href,"\" ");

      if(!Str.isEmpty(func))
      {
         String id = getId();
         out.append(" onClick=\"javascript:setFormReference('",id,"');return ",func);
      }
      out.append(">\n");
   }

   //-------------------------------------------------------------------------
   // HTML Table
   //--------------------------------------------------------------------------

//   private int     tablelevel = 0;
   private boolean transparent = false;

   /**
    * Begin a HTML table (a table-tag).
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    */
   protected final void beginTable()
   {
      beginTable(null,false,true);
   }

   /**
    * Begin a HTML table (a table-tag) with a given id.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    */
   protected final void beginTable( String id )
   {
      beginTable(id,false,true);
   }

   /**
    * Begin a HTML table (a table-tag) with transparent background.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    */
   protected final void beginTransparentTable()
   {
      beginTable(null,true,true);
   }

   /**
    * Begin a HTML table (a table-tag) with transparent background and a given id.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    */
   protected final void beginTransparentTable( String id )
   {
      beginTable(id,true,true);
   }

   /**
    * Begin a HTML table (a table-tag). If transparent-flag is true, the table background
    * will be transparent. Value true of the fullwidth-parameter will
    * generate the table width of 100%.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    */
   protected final void beginTable( String id, boolean transparent, boolean fullwidth )
   {
//      this.tablelevel++;
      this.transparent = transparent;
      out.append("<table");
      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);
      out.append(" border=0 cellpadding=2 cellspacing=0",fullwidth?" width=100%":"",">\n");
      colcnt = 0;
   }

   /**
    * Begin a HTML table (a table-tag) if the tranparent flag is true the table background
    * will be transparent. the attrlist should provide the necessary attributes for the table-tag
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTable
    * @see ifs.fnd.asp.ASPPortletProvider#beginTransparentTable
    * @see ifs.fnd.asp.ASPPortletProvider#beginTable
    */

   protected final void beginCustomTable(String id, boolean transparent,String attrlist)
   {
      this.transparent = transparent;
      out.append("<table");
      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);
      if( !Str.isEmpty(attrlist) )
         out.append(" "+attrlist);
      out.append(">\n");
      colcnt = 0;
   }


   /**
    * End a HTML table opened with beginTable() function.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTable
    */
   protected final void endTable() throws FndException
   {
      out.append("</table>\n");
//      tablelevel--;
//      if(tablelevel<0)
//         throw new FndException("FNDPRVENDTAB: No open tables exist!");
   }


   private boolean intitlerow = false;

   /**
    * Begin a table title row (a tr-tag). Should be closed by calling endTableTitleRow().
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTableTitleRow
    */
   protected final void beginTableTitleRow() throws FndException
   {
      beginTableTitleRow(null);
   }

   /**
    * Begin a table title row (a tr-tag) with a specified id.
    * Should be closed by calling endTableTitleRow().
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTableTitleRow
    */
   protected final void beginTableTitleRow( String id ) throws FndException
   {
//      if(intitlerow)
//         throw new FndException("FNDPRVINTITL: Already in title row!");
      intitlerow = true;
      out.append("<tr");
      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);
      out.append(">\n");
      colcnt = 0;
   }

   /**
    * Close a table title row that was opened by beginTableTitleRow() function.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableTitleRow
    */
   protected final void endTableTitleRow() throws FndException
   {
//      if(!intitlerow)
//         throw new FndException("FNDPRVNINTITL: Not in title row!");
      intitlerow = false;
      out.append("</tr>\n");
   }


//   private int tabbodylevel = 0;

   /**
    * Begin a body of a HTML table (a tbody-tag). Should be closed by endTableBody().
    * Also begin the first row in the table (a tr-tag).
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTableBody
    * @see ifs.fnd.asp.ASPPortletProvider#nextTableRow
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableBody
    * @see ifs.fnd.asp.ASPPortletProvider#nextTableRow
    */
   protected final void beginTableBody( String id ) throws FndException
   {
      ASPConfig cfg = getASPConfig();
      //String bgcolor = cfg.getParameter("SCHEME/FORM/ALT_BGCOLOR", "#E1E2E8");

//      if(tabbodylevel>=tablelevel)
//         throw new FndException("FNDPRVINBODY: Already in table body!");
//      this.tabbodylevel++;
      this.evenrow = false;
      out.append("<tbody>\n<tr");
      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);
      if(!transparent)
         out.append(" class=tableRowColor1");
      out.append(">\n");
      colcnt = 0;
   }

   /**
    * Close the last table row and close table body opened by beginTableBody().
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableBody
    */
   protected final void endTableBody() throws FndException
   {
//      if(tabbodylevel<tablelevel)
//         throw new FndException("FNDPRVNINBDY: Not in table body!");
//      this.tabbodylevel--;
      out.append("</tr>\n</tbody>\n");
   }


   private boolean evenrow = false;

   /**
    * Close current table row and begin next one.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableBody
    */
   protected final void nextTableRow() throws FndException
   {
      nextTableRow(null);
   }

   /**
    * Close current table row and begin next one with specified id.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableBody
    */
   protected final void nextTableRow( String id ) throws FndException
   {
      ASPConfig cfg = getASPConfig();
      //String bgcolor = cfg.getParameter("SCHEME/FORM/ALT_BGCOLOR", "#E1E2E8");
//      if(tabbodylevel<tablelevel)
//         throw new FndException("FNDPRVNINBDY2: Not in table body!");
      evenrow = !evenrow;
      out.append("</tr>\n<tr");
      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);
      if(!transparent && !evenrow)
         out.append(" class=tableRowColor1");
      else if(!transparent)
         out.append(" class=tableRowColor2");
      out.append(">\n");
      colcnt = 0;
   }


   /**
    * Constant for definition of left horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printTableCell
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final static String LEFT   = "left";
   /**
    * Constant for definition of right horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printTableCell
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final static String RIGHT  = "right";
   /**
    * Constant for definition of center horizontal alignment in table cell.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printTableCell
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final static String CENTER = "center";

   private int colcnt = 0;

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final void printTableCell( String text, String align ) throws FndException
   {
      printTableCell(text,0,0,align);
   }

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text and column span.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final void printTableCell( String text, int colspan ) throws FndException
   {
      printTableCell(text,colspan,0,null);
   }

   /**
    * Append a table title or contents cell tag (a th- or td-tag) to the HTML-output
    * with the specified text, horizontal alignment and column and row span.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final void printTableCell( String text, int colspan, int rowspan, String align ) throws FndException
   {
      beginTableCell(colspan, rowspan, align);
      //out.append("<font class=normalTextValue>");   // needed for Netscape ??
      if(Str.isEmpty(text))
         out.append("&nbsp;");
      else
         out.append(getASPManager().HTMLEncode(translate(text),true));
      //out.append("</font>");
      // temporary solution (I hope)
      //out.append(translate(text));
      endTableCell();
   }


//   private int tabcelllevel = 0;

   /**
    * Begin a table title or contents cell (a th- or td-tag).
    * Should be closed by calling endTableCell().
    *
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
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
    * @see ifs.fnd.asp.ASPPortletProvider#endTableCell
    */
   protected final void beginTableCell( int colspan, int rowspan, String align, String id, boolean nowrap ) throws FndException
   {
//      if(!intitlerow && tabbodylevel<tablelevel)
//         throw new FndException("FNDPRVNINTAB: Not in title row or table body!");

//      if(tabcelllevel>=tablelevel)
//         throw new FndException("FNDPRVNINTABCELL: Already in table cell!");

      // Comment by Terry 20131104
      /*if(!transparent && colcnt>0)
      {
         out.append( intitlerow ? "<th>" : (transparent?"<td>":"<td bgcolor=white>"));
         out.append("<img src=\"",getASPConfig().getImagesLocation(),"table_empty_image.gif\" width=3 height=1>");
         out.append( intitlerow ? "</th" : "</td", ">\n");
      }*/

      if(intitlerow)
         //out.append("<th class=\"grdHd\" BGCOLOR=#FFFFFF");
         out.append("<th class=tableColumnHeadingText");
      else
         out.append("<td class=tableContentText");

      if( !Str.isEmpty(id) )
         out.append(" id=",addProviderPrefix(),id);

      if(colspan>0)
      {
         out.append(" colspan=");
         out.appendInt(colspan);
      }
      if(rowspan>0)
      {
         out.append(" rowspan=");
         out.appendInt(rowspan);
      }
      if(!Str.isEmpty(align))
         out.append(" align=",align);

      if (nowrap)
         out.append(" nowrap");

      out.append(">");
      colcnt += colspan>0 ? colspan : 1;
//      tabcelllevel++;
   }

   /**
    * Close a table title or contents cell that was opened by beginTableCell() function.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#beginTableCell
    */
   protected final void endTableCell() throws FndException
   {
//      if(!intitlerow && tabbodylevel<tablelevel)
//         throw new FndException("FNDPRVNINTAB2: Not in title row or table body!");

//      if(tabcelllevel<tablelevel)
//         throw new FndException("FNDPRVNNINTABCELL: Not in table cell!");

      out.append("</",intitlerow?"th":"td",">\n");
//      tabcelllevel--;
   }


   /**
    * Create a string that represents a HTML link. The given label will be translated.
    * One of overloaded printLink() functions should be used for this purpose.
    *
    * @see ifs.fnd.asp.ASPPortletProvider#printLink
    * @see ifs.fnd.asp.ASPPortletProvider#printImageLink
    * @see ifs.fnd.asp.ASPPortletProvider#printHoverImageLink
    * @see ifs.fnd.asp.ASPPortletProvider#printScriptLink
    * @see ifs.fnd.asp.ASPPortletProvider#printScriptImageLink
    * @see ifs.fnd.asp.ASPPortletProvider#printScriptHoverImageLink
    * @see ifs.fnd.asp.ASPPortletProvider#printSubmitLink
    * @see ifs.fnd.asp.ASPPortletProvider#printSubmitImageLink
    * @see ifs.fnd.asp.ASPPortletProvider#printSubmitHoverImageLink
    * @deprecated
    */
   protected final String setHyperlink( String label, String hyperlink )
   {
      return "<a href=\""+hyperlink+"\">"+getASPManager().HTMLEncode(translate(label))+"</a>";
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
      if(s2!=null)  dirty_script.append(s2);
      if(s3!=null)  dirty_script.append(s3);
      if(s4!=null)  dirty_script.append(s4);
      if(s5!=null)  dirty_script.append(s5);
   }


   /**
    * Creates a 'script' tag and appends the specified string as JavaScript code,
    * i.e. script functions. Due this function is called only once for all instances
    * of the same portlet class, the code must not contain portlet specific
    * code (portlet ID). Should be called from preDefine().
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
      if(s2!=null)  script.append(s2);
      if(s3!=null)  script.append(s3);
      if(s4!=null)  script.append(s4);
      if(s5!=null)  script.append(s5);
   }


   /**
    * Creates a 'script' tag and appends the contents of the specified file
    * as JavaScript code,i.e. script functions. Due this function is called only
    * once for all instances of the same portlet class, the code must not contain
    * portlet specific code (portlet ID). Should be called from preDefine().
    * For example:
    * <pre>
    *    includeJavaScriptFile("demorw/DemoScript.js");
    *</pre>
    */
   protected final void includeJavaScriptFile( String filename ) throws FndException
   {
      modifyingImmutableAttribute("JAVASCRIPTFILE");
      if(!Str.isEmpty(scriptfile))
         throw new FndException("FNDPRVFSCRDEF: JavaScript file name is already defined.");

      scriptfile = filename;
   }

   //==========================================================================
   //  Debug
   //==========================================================================

   /**
    * Print the current function stack to the DBMON console.
    */
   public static void dumpStack()
   {
      try
      {
         throw new Exception("Current Stack Dump:");
      }
      catch(Exception e)
      {
         Util.debug( Str.getStackTrace(e) );
      }
   }


   //==========================================================================
   //==========================================================================
   //  TO BE REMOVED !!!
   //==========================================================================


   //JAPA
   protected final ASPPageElement clonePageElement_( ASPPage page, ASPPageElement element ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.clonePageElement("+page+","+element+")");
      return (ASPPageElement)(element.clone(page));
   }

   /**
    * Called by ASPManager, after successful execution of the submit() method.
    */
   void refresh_() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletProvider.refresh()");
      super.refresh();
   }

   int indexInColumn_( Vector column )
   {
      String name = getClass().getName();
      for(int i=0; i<column.size(); i++)
         if( column.elementAt(i).getClass().getName().equals(name) )
         {
            debug("ASPPortletProvider: index of '"+name+"' = "+i);
            return i;
         }
      debug("ASPPortletProvider: index of '"+name+"' = -1");
      return -1;
   }


   /**
    * Returns an array of strings that results when the string str is separated
    * into substrings using the specified set of delimiter characters 'delimChars'.
    */
   protected static final String[] split(String str, String delimChars)
   {
      return Str.split(str,delimChars);
   }

   /** Add given global information in to a temporary global list. But this will not effect to the
    * original global variables. The idea is to keep list of temporary global vriables to generate the
    * URL from witch the user can actually change the original globals.
    * @param name name of the global variable
    * @param label label to display
    * @param value value to set
    * @param description description
    */

   protected void defineGlobalVariable(String name, String label, String value, String description)
   {
      if (dummy_globals == null)
         dummy_globals = new Vector();
      for (int i=0; i<dummy_globals.size();  i++)
      {
         GlobalVariable temp_global = (GlobalVariable) dummy_globals.get(i);
         if (temp_global.getName().equals(name))
         {
            temp_global.setValue(value);
            temp_global.setLabel(label);
            temp_global.setDescription(description);
            return;
         }
      }
      dummy_globals.add(new GlobalVariable(name, value, label, description));
   }

   /**
    * Generate the URL with proper query string to set above defined globals while keepingthe
    * already defined global variables.
    */
   protected String generateGlobalVariableURL()
   {
      // There are some heavy string operations here. This code should be improved after March - 2004.
      // Mangala
      String ret;
      ASPManager mgr = getASPManager();
      temp_globals.clear();
      temp_globals_value.clear();

      for (int i=0; i<dummy_globals.size();  i++)
      {
         GlobalVariable dummy_global = (GlobalVariable) dummy_globals.get(i);
         temp_globals.append(dummy_global.getName(),",");
         temp_globals_value.append("&",dummy_global.getName(),"=",dummy_global.getValue(),",");
         temp_globals_value.append(dummy_global.getLabel(),",",dummy_global.getDescription());
      }
      String local_global_list = "," + temp_globals.toString();
      Enumeration variables = portal.getGlobalVariables().keys();
      while( variables.hasMoreElements() )
      {
         String variable = (String)variables.nextElement();
         if (local_global_list.indexOf(","+variable+",")>-1)
            continue;
         temp_globals.append(variable+",");
         temp_globals_value.append("&",variable,"=",mgr.URLEncode((String)portal.getGlobalVariables().get(variable)));
      }

      temp_globals.append(temp_globals_value);
      ret = mgr.getPortalPage().getURL() + "?__VIEW_NAME="+mgr.URLEncode( portal.getName()) +"&GLOBAL_VARIABLE=" + temp_globals.toString();
      temp_globals_value.clear();
      temp_globals.clear();
      return ret;
   }

   /**
    * Returns the URL where you can remove given global variables while keeping already defined global
    * value unchanged.
    * @param text text to be print
    * @param globals comma seperated list of globals to be remove
    */
   protected void printRemoveGlobalLink(String text, String globals)
   {
      String ret;
      String temp_global_str = ","+globals+",";
      ASPManager mgr = getASPManager();
      temp_globals.clear();
      temp_globals_value.clear();
      Enumeration variables = portal.getGlobalVariables().keys();
      while( variables.hasMoreElements() )
      {
         String variable = (String)variables.nextElement();
         if (temp_global_str.indexOf(","+variable+",")>-1)
            continue;
         temp_globals.append(variable+",");
         temp_globals_value.append("&",variable,"=",mgr.URLEncode((String)portal.getGlobalVariables().get(variable)));
      }

      temp_globals.append(temp_globals_value);
      ret = mgr.getPortalPage().getURL() + "?__VIEW_NAME="+mgr.URLEncode( portal.getName()) +"&GLOBAL_VARIABLE=" + temp_globals.toString();
      temp_globals_value.clear();
      temp_globals.clear();
      printAbsoluteLink(text, ret);
   }

   /**
    * Returns the description of given global variable
    * @param name global vriable name
    */
   protected String getGlobalDescription(String name)
   {
      String [] global_details = split(portal.getGlobalVariable(name), ",");
      if (global_details.length ==3)
         return global_details[2];
      else
         return "";
   }

   /**
    * Returns the label of given global variable
    * @param name global vriable name
    */

   protected String getGlobalLabel(String name)
   {
      String [] global_details = split(portal.getGlobalVariable(name), ",");
      if (global_details.length >2)
         return global_details[1];
      else
         return "";
   }

   class GlobalVariable
   {
      private String name;
      private String value;
      private String label;
      private String description;


      GlobalVariable(String name, String value, String label, String description)
      {
         this.name=name;
         this.value = value;
         this.label = label;
         this.description = description;
      }

      String getName()
      {
         return name;
      }

      String getValue()
      {
         return value;
      }

      String getLabel()
      {
         return label;
      }

      String getDescription()
      {
         return description;
      }

      void setName(String name)
      {
         this.name = name;
      }

      void setValue(String value)
      {
         this.value = value;
      }

      void setLabel(String label)
      {
         this.label = label;
      }

      void setDescription(String description)
      {
         this.description = description;
      }

   }
}
