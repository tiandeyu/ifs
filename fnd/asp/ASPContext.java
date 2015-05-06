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
 * File        : ASPContext.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1998-May-05 - Created
 *    Jacek P  1998-May-14 - getDb/CurrentState() moved from ASPManager
 *    Jacek P  1998-May-26 - Added methods read/writeFlag()
 *    Marek D  1998-Jun-01 - Added read/write methods for ASPBufferable objects
 *    Jacek P  1998-Jun-04 - Changes in redirection to other URL. Access of
 *                           getGlobalURL() changed to public.
 *    Marek D  1998-Jun-08 - Introduced private DEBUG flag
 *    Jacek P  1998-Jul-29 - Introduced FndException concept
 *    Jacek P  1998-Aug-07 - Added try..catch block to each public function
 *                           which can throw exception
 *    Marek D  1998-Sep-25 - Added compression of stored Current/DbState: #2624
 *    Jacek P  1998-Nov-24 - Added new functions set/get/removeCookie() for
 *                           new error handling (Todo: #2714,#2943).
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-11 - Added call to Trace Even statistics.
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *                           Removed calls to Scr class
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-05 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement. Added doActivate().
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Jacek P  1999-Mar-31 - Removed ThreadEvent.
 *    Jacek P  1999-Apr-01 - Added methods for handling of user authorization.
 *    Marek D  1999-Apr-26 - Verified usage of clone()
 *                           Added verify() and scan()
 *    Jacek P  1999-May-03 - Added new config parameter for controlling if
 *                           context should be send to browser or not.
 *    Jacek P  1999-May-18 - Added method rollbackContext() called on error
 *                           from ASPLog. Added cloning of object in rewrite()
 *                           (except DB_STATE).
 *    Jacek P  1999-Jun-07 - Added method forceDirty().
 *                           Added new function getAuthUserId(). Changed access
 *                           of getAuthUserCookie() to private. Added call to
 *                           getAuthUserCookie() from doActivate().
 *                           Added new function isUserLoggedOn().
 *    Jacek P  1999-Aug-10 - Added control of removing old context from cache
 *                           in function doActivate().
 *    Jacek P  1999-Dec-14 - Corrected bug in setAuthUserCookie(). Changed return
 *                           type from void to String.
 *    Jacek P  1999-Dec-20 - Bug corrections in getAuthUserCookie() and
 *                           removeAuthUserCookie(). Additional debug print outs
 *                           in isUserLoggedOn(),  doActivate() and getAuthUserId().
 *    Jacek P  2000-Jan-17 - Implementation of portal:
 *                           State constants and buffer variables, constructor for cloning,
 *                           functions newBuffer(), readItemReference(), rewrite()
 *                           and getCompoundItemReference() changed to protected.
 *                           New function isFrozen() used instead of frozen variable.
 *                           Better debugging. New function showContents().
 *    Johan S  2000-Aug-18 - Added MD5-hashed security number in the Authorisation cookie.
 *    Jacek P  2000-Dec-27 - Added methods for handling numbers for support of Java pages.
 *    Johan S  2001-Jan-26 - Revisited the Context Cache.
 *    Piotr Z  2001-Mar-06 - Some changes in cookies handling. Path for cookies is mostly
 *                           set in ASPManager.
 *    Piotr Z  2001-Mar-13 - New separator COOKIE_ITEM_SEP for cookie items due to cookie encoding.
 *                           Authorization cookie stored in security_table is now cloned.
 *    Piotr Z  2001-Mar-17 - Removed __IFS_Application__ cookie, authorization cookie only for security id.
 *                           Globals and authorization data are stored in session object.
 *    Jacek P  2001-Apr-11 - Added debugging of session in handling of global variables.
 *    Jacek P  2001-Apr-12 - Debugging of session moved to ASPManager.
 *                           Changes in removeAuthUser() (Log id #689).
 *    Mangala  2001-May-10 - Security mechanism is altered due to log #676 and #701.
 *                           Changes are only done in setAuthUser().
 *    Jacek P  2001-May-11 - Log id #706: isUserLoggedOn() changed to public.
 *    Mangala  2001-May-23 - Security mecanism changed again to fix the session expiration problem.
 *                           Only a session variable is used instead of having both a session variable and
 *                           a cookie. The "auth_user_cookie_name" cookie is removed and the session variable name
 *                           is changed. Also two cookies ("JRUN_SESSION_COOKIE" and "TOMCAT_SESSION_COOKIE")
 *                           are introduced and refreshs them in setAuthUser() in every visit.
 *    Ramila H 2001-06-13  - Log id # 760 corrected.
 *                           Made changes to readNumber(String s, double d) method.
 *    Jacek P  2001-Sep-13 - Implemented new algorithm for user authorization.
 *    Jacek P  2001-Oct-12 - Authorization cookie will not expire. Removed warning.
 *    Rifki R  2002-Apr-08 - Changed error text for translation key "FNDCTXNOCTX" with a more descriptive message.
 *    Daniel S 2002-Aug-08 - Added support for the new context cache.
 *    Daniel S 2002-Sep-12 - New authorization handling. Moved to session object to get clustering support.
 *    ChandanaD2002-Oct-21 - setCookie() method made public.
 *             2002-Oct-21 - Add user_name infront of the cookie name in getCookie() & setCookie() methods.
 *    ChandanaD2002-Nov-05 - Prevents appending user id to the name of __Authorized_User_ cookie.
 *    ChandanaD2002-Dec-12 - Prefixed user_name to the cookie name in removeCookie() method.
 *    Jacek P  2003-Jan-27 - Session invalidation moved to ASPManager.
 *    Johan S  2003-Mar-04 - rewrote the cookie handling, see ASPManager for more documentation.
 *    Rifki R  2003-Mar-06 - modified isUserAuthorized(), avoid invalidating the session when security key is null.
 *    Mangala  2003-Mar-15 - Redirection to Login page or exception when context is mising applied
 *                           even for the Logon page.
 *    Ramila H 2003-jul-18 - Log id 1119, Authentication by fndext.
 *    Ramila H 2003-Sep-08 - loggedoff user before redirecting to logon page when expired.
 *    Rifki R  2004-Feb-16 - Bug 40903, improvements to context cache.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Ramila H 2004-10-18  - Implemented JSR168 support.
 *    Ramila H 2004-11-09  - Implemented code for setting global variables for standard portlets.
 *    Prabha R 2006/02/20  - Rmoved calls to deprecated functions
 *    Prabha R 2006/04/27  - Bug 55637 Removed send-to-browser option, added an exception if
 *                           __CONTEXT is set with a value for normal pages.
 *    SasankaD 2007/10/17  - Bug 67865, Added constant CURRENT_PAGE_URL and Modified doActivate().
 *    SasankaD 2007/11/01  - Merged Bug 68311, setCookie() and getCookie().
 *    mapelk   2008/06/26  - Bug 74852, Programming Model for Activities. 
 *		buhilk	2008/07/01	- Bug 74852, Added getDBContextDataSet() and modified mergeFndAbstractRecordToArray()
 * ----------------------------------------------------------------------------
 * 09/04/2010 rahelk bug 89989, added auth cookie to prevent weblogic from view back pages after logging out
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.base.SystemException;
import ifs.fnd.internal.FndAttributeInternals;
import ifs.fnd.record.FndAbstractRecord;
import ifs.fnd.record.FndCompoundReference;
import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.pages.DefaultStdPortlet;
import ifs.fnd.portal.DefineGlobalFactory;
import ifs.fnd.record.FndAbstractArray;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.servlet.http.*;


/**
 * A common class for handling of global variables and context buffers.
 *<p>
 * A global variable is implemented as an item within a common for the whole
 * IFS Application cookie and will not disappear as long as the browser is running.
 *<p>
 * The context buffer on the other side is preserved only within one request. The
 * buffer is implemented as a hidden HTML field on the ASP page.
 *<p>
 * An instance of this class is automatically created by ASPManager.
 */
public class ASPContext extends ASPPageElement
{
   //==========================================================================
   // Constants and static variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPContext");
   //Bug 40903, start
   public final static String CONTEXT_ID_FIELD_NAME = "__CONTEXT_ID"; //key for 2nd dimension of context cache
   //Bug 40903, end
   private   final static String CONTEXT_FIELD_NAME     = "__CONTEXT";
   private   final static String __DB                   = "__DB";
   private   final static String __CURRENT              = "__CURRENT";
   protected final static String DB_STATE_BUF_NAME      = "__DB_STATE";
   protected final static String CURRENT_STATE_BUF_NAME = "__CURRENT_STATE";
   private final static String CURRENT_PAGE_URL         = "__CURRENT_PAGE_URL";

   private static TraceEventType uncompress_event_type = new TraceEventType("ASPContext.uncompress");
   private static TraceEventType parse_event_type      = new TraceEventType("ASPContext.parse");

   final static Date cookie_expired = new Date(1000L*3600L*24L*7305L); //set expire date to 1-jan-1990

   private final static String AUTH_USER_ID          = "USER_ID";
   private final static String AUTH_USER_LASTREQ     = "LAST_REQUEST";
   private final static String AUTH_SECURITY_FLAG    = "SECURITY_FLAG";
   private final static String AUTH_SECURITY_KEY    = "SECURITY_KEY";
   private final static String JRUN_SESSION_COOKIE   = "jsessionid";
   private final static String TOMCAT_SESSION_COOKIE = "JSESSIONID";

   private final static String COOKIE_ITEM_SEP   = (char)31+"";


   //==========================================================================
   // Instance variables
   //==========================================================================

   // immutable attributes
   private BufferFormatter  buf_frmt;
   private int              auto_logoff_time;
   private String           auth_user_cookie_name; //JAPA

   // temporary request variables
   protected Buffer           req_buf;
   protected Buffer           resp_buf;
   private   HashMap          req_map;
   private   HashMap          resp_map;   

   private   boolean          frozen;
   private   String           old_page_id;
   private   boolean          user_logged_on;
   private   boolean          keep_context_in_cache;
   private   boolean          remove_context_from_cache;
   //Bug 40903, start
   private   String           context_id;
   //Bug 40903, end

   //==========================================================================
   // Constructors
   //==========================================================================

   /**
    * Package constructor. Calls constructor within the super class.
    */
   ASPContext( ASPPage page )
   {
      super(page);
      if(DEBUG) debug(this+": ASPContext.<init>: "+page);
   }

   ASPContext construct() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.construct()");

      ASPConfig cfg = getASPPage().getASPConfig();

      buf_frmt              = cfg.getFactory().getBufferFormatter();
      auth_user_cookie_name = cfg.getAuthUserCookieName(); //JAPA
      auto_logoff_time      = cfg.getUserAutoLogoffTime();
      auto_logoff_time      = (auto_logoff_time == 0) ? -1 : auto_logoff_time*60;//in seconds,-1 session never expires

      return this;
   }

   /**
    * Package constructor called from clone()
    */
   ASPContext( ASPPage page, ASPContext context )
   {
      super( page );

      if(DEBUG) debug(this+": ASPContext.<init>: "+page+","+context);

      this.buf_frmt              = context.buf_frmt;
      this.auto_logoff_time      = context.auto_logoff_time;
      this.auth_user_cookie_name = context.auth_user_cookie_name; //JAPA

      this.req_buf        = null;
      this.resp_buf       = null;
      this.frozen         = false;
      this.old_page_id    = null;
      this.user_logged_on = false;
      this.req_map        = null;
      this.resp_map       = null;
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


   void forceDirty() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.forceDirty()");
      modifyingMutableAttribute("FORCE_DIRTY");
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.doReset()");

      req_buf        = null;
      resp_buf       = null;
      req_map        = null;
      resp_map       = null;
      
      old_page_id    = null;
      user_logged_on = false;
   }


   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.doActivate()");

      ASPManager mgr  = getASPManager();
      ASPPage    page = getASPPage();
      ASPConfig  cfg  = page.getASPConfig();

      frozen      = false;
      old_page_id = mgr.readValue("__PAGE_ID");
      //Bug 40903, start
      context_id = mgr.readValue(CONTEXT_ID_FIELD_NAME);
      if (Str.isEmpty(context_id))
        context_id="*";

      //CONTEXT not cached (send to the browser) if portlets run in Standard portal or force to send to browser
      if (!(getASPPage() instanceof DefaultStdPortlet))
      {
      	ASPContextCache.ContextObject ctx_obj = ASPContextCache.get(context_id,old_page_id,mgr);
         if (ctx_obj!=null)
         {
            req_buf     = ctx_obj.getContextBuffer();
            req_map    = ctx_obj.getDataMap();
         }
      }
      if(DEBUG)
      {
         debug("ASPContext.doActivate():  Request buffer "+(req_buf!=null ? "("+req_buf.getClass().getName()+"):\n"+Buffers.listToString(req_buf) : "= null") );
      }

      //Bug 40903, end
      keep_context_in_cache = false;
      remove_context_from_cache = false;

      if (req_map == null)
         req_map = new HashMap();

      if(req_buf==null)
      {
         if(DEBUG) debug("ASPContext: create new context.");

         String value = mgr.readValue(CONTEXT_FIELD_NAME);
         if( "*".equals(value))
         {
                String current_url = mgr.readValue(CURRENT_PAGE_URL);
                mgr.getAspSession().setAttribute(mgr.IS_PAGE_EXPIRED,"Y");
                mgr.redirectTo(current_url);
                return;
             /*if (!cfg.isFormBasedAuth())
                throw new FndException("FNDCTXNOCTX: The data has expired, and the web page needs to be refreshed. Close this dialog and press the refresh button in the browser. (In some cases you may need to repeat this operation.)");
             else
             {
                mgr.logOffCurrentUser();
                mgr.redirectTo(cfg.getLogonURL());
             }*/ 
         }

         req_buf = cfg.getFactory().getBuffer();

         if(!"*".equals(value))
         try
         {
            if ((value!=null) && (!(Str.isEmpty(value)))&& (!(getASPPage() instanceof DefaultStdPortlet)))
            {
               throw new FndException("FNDCTXNOTALLOWED: The Operation is not allowed");
            }
            TraceEvent uncompress_event = uncompress_event_type.begin();
            value = Util.uncompress(value);
            uncompress_event.end();

            TraceEvent parse_event = parse_event_type.begin();
            buf_frmt.parse( value, req_buf );
            parse_event.end();
         }
         catch( IOException e )
         {
            throw new FndException(e);
         }
      }
      else if(DEBUG) debug("ASPContext: context found in cache.");

      resp_buf = newBuffer();
      if(DEBUG)
         debug("ASPContext.doActivate():  Request buffer "+(resp_buf!=null ? "("+resp_buf.getClass().getName()+"):\n"+Buffers.listToString(resp_buf) : "= null") );

      rewrite( DB_STATE_BUF_NAME,      null,              false );
      rewrite( CURRENT_STATE_BUF_NAME, DB_STATE_BUF_NAME, page.getASPLov()!=null ? false : true );
      rewriteMaps(page.getASPLov()!=null ? false : true );

      if(DEBUG)
         debug(showContents());

      user_logged_on= true;
   }

   /**
    * Create a copy of request data map and store in the response data map.
    */

   private void rewriteMaps(boolean clone)
   {
      if (clone)
      {
         resp_map = new HashMap();//req_map.clone();
         Set set = req_map.keySet();
         if (set == null ) return;
         Iterator keys = set.iterator();
         while (keys.hasNext())
         {  
            Object key =  keys.next();
            resp_map.put(key,((ASPContextCache.ContextDataSet)req_map.get(key)).clone());
         }
      }
      else
         resp_map = req_map;
          
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.clone("+page+")");

      ASPContext c = new ASPContext((ASPPage)page, this);
      c.setCloned();
      return c;
   }


   protected void verify( ASPPage page ) throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.verify("+page+")");
      this.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.scan("+page+","+level+")");
      scanAction(page,level);
   }

   protected boolean isFrozen()
   {
      if(DEBUG) debug(this+": ASPContext.isFrozen()");
      return false;//frozen; // should be fixed !!!
   }

   //==========================================================================
   // Private routines
   //==========================================================================


   /**
    * Returns new, empty instance of Buffer.
    */
   protected Buffer newBuffer()
   {
      if(DEBUG) debug(this+": ASPContext.newBuffer()");
      return req_buf.newInstance();
   }


   /*
    * Returns new instance of Item with a given name and an empty buffer.
    */
   private Item newCompoundItem( String name )
   {
      if(DEBUG) debug(this+": ASPContext.newCompoundItem("+name+")");
      return new Item( name, newBuffer() );
   }

   //==========================================================================
   // Handling of cookies
   //==========================================================================


    /* VIC METHODS */

   /**
    * Set a VIC cookie. The cookie will be removed on browser close.
    * @see ifs.fnd.asp.ASPContext#setTransientCookie(String name, String value)
    */

   public void setCookie( String name, String value )
   {
      setCookie( name, value, null, null, ASPManager.CookieContainer.VIC );
   }

   /**
    * Set a persistent VIC cookie.
    * @see ifs.fnd.asp.ASPContext#setTransientCookie(String name, String value)
    */

   public void setPersistentCookie( String name, String value )
   {
      setCookie( name, value, null,  new Date(2145916800000L), ASPManager.CookieContainer.VIC );
   }

   /**
    * Set a cookie for given path. The cookie will be removed on browser close.
    * @deprecated  As of version 3.6.0, path is always set to the application path.
    */
   public void setCookie( String name, String value, String path )
   {
      setCookie( name, value, path, null, ASPManager.CookieContainer.VIC );
   }

    /* Transient Methods */


   /**
    * Set a transient cookie. The cookie will be removed on browser close.
    * If the application don't completely fail without this cookie, you should use
    * transient cookie.
    */

   public void setTransientCookie( String name, String value )
   {
      setCookie( name, value, null, null, ASPManager.CookieContainer.TRANSIENT );
   }

   /**
    * Set a persistent transient cookie.
    * If the application don't completely fail without this cookie, you should use
    * transient cookie.
    */

   public void setPersistentTransientCookie( String name, String value )
   {
      setCookie( name, value, null,  new Date(2145916800000L), ASPManager.CookieContainer.TRANSIENT );
   }


    /* Temporary Cookies, should only be used with for page id cookies. */

   void setTemporaryCookie(String name, String value)
   {
       setCookie(name, value, null, null, ASPManager.CookieContainer.TEMPORARY);
   }

   private void setCookie(String name, String value, String path, Date expires, int type)
   {
      if(DEBUG) debug(this+": ASPContext.setCookie("+name+","+value+","+path+","+expires+")" );

      if(!getASPManager().isEmpty(getASPManager().getUserId()) &&
         name.indexOf(getASPManager().getCookiePrefix())==-1 &&
         !name.equals(auth_user_cookie_name) && type!=ASPManager.CookieContainer.TEMPORARY)
            name = getASPManager().getCookiePrefix()+"_"+name;

      try
      {
         ASPManager mgr = getASPManager();
         Cookie cookie = mgr.getAspCookie(name);
         if(cookie==null)
         {
            if(type==ASPManager.CookieContainer.TEMPORARY)
                cookie = mgr.newPageIDCookie(name);
            else if(type==ASPManager.CookieContainer.TRANSIENT)
                cookie = mgr.newTransientAspCookie(name);
            else
                cookie = mgr.newAspCookie(name);
         }
         if(expires!=null)
         {
            if(DEBUG) debug("  expires="+expires);
            cookie.setMaxAge( (int)((expires.getTime() - (new Date()).getTime())/1000) );
         }
         cookie.setValue( Str.nvl(value,"") );
      }
      catch( Throwable e )
      {
         error(e);
      }
   }

   /**
    * Return value of a cookie.
    */
   public String getCookie( String name )
   {
      if(DEBUG) debug(this+": ASPContext.getCookie("+name+")");

      String prefixed_name=null;

      if(!getASPManager().isEmpty(getASPManager().getUserId()) &&
          name.indexOf(getASPManager().getCookiePrefix())==-1 &&
          !name.equals(auth_user_cookie_name))
             prefixed_name = getASPManager().getCookiePrefix()+"_"+name;

      try
      {
         Cookie cookie = getASPManager().getAspCookie( name );
         if(cookie==null && prefixed_name!=null)
             cookie = getASPManager().getAspCookie( prefixed_name );

         return cookie==null ? null : cookie.getValue();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Remove a cookie for a given path
     * @deprecated As of version 3.6.0, this method has been replaced with removeCookie(String name)
    */

    public void removeCookie(String name, String path)
    {
        removeCookie(name);
    }

    /* This method could be changed to put removeable cookies in the asp_cookie_throw_list and marking them
     * with a temporary prefix
     */

   /**
    * Remove a cookie for the current application
    */

   public void removeCookie( String name)
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.removeCookie("+name+"), expires="+cookie_expired );

         String prefixed_name=null;

         if(!getASPManager().isEmpty(getASPManager().getUserId()) &&
            name.indexOf(getASPManager().getUserId())==-1 &&
            !name.equals(auth_user_cookie_name))
               prefixed_name = getASPManager().getUserId()+"_"+name;

         Cookie cookie = getASPManager().getAspCookie( name );
         if(cookie==null && prefixed_name!=null)
             cookie = getASPManager().getAspCookie( prefixed_name );
         if(cookie==null) return;
         cookie.setValue( "*" );

         cookie.setMaxAge(0);//remove cookie
      }
      catch( Throwable e )
      {
         error(e);
      }
   }

   //==========================================================================
   // User authorization
   //==========================================================================

   /**
    * @deprecated
    */
   public boolean isUserLoggedOn() // used by ASPManager.checkUserAuthorized(), ASPPage.readProfileItem(), ASPProfileCache.get()
   {
      if(DEBUG) debug(this+": ASPContext.isUserLoggedOn(): user_logged_on="+user_logged_on);
      return user_logged_on;
   }

   boolean isUserLoggedOnFND() 
   {
      if(DEBUG) debug(this+": ASPContext.isUserLoggedOnFND(): user_logged_on="+user_logged_on);
      return user_logged_on;
   }

/*    private void updateJSPSession()                                         */
/*    {                                                                       */
/*                                                                            */
/*       //Rewriting session id cookies to avoid expiration of session cookie */
/*       HttpSession session = getASPManager().getAspSession();               */
/*       String sess_id = session.getId();                                    */
/*                                                                            */
/*       setCookie(JRUN_SESSION_COOKIE,   sess_id);                           */
/*       setCookie(TOMCAT_SESSION_COOKIE, sess_id);                           */
/*                                                                            */
/*       session.setMaxInactiveInterval(auto_logoff_time);                    */
/*    }                                                                       */

   private void invalidateJSPSession()
   {
      // called from:
      //   getAuthorizedUserEntry()
      //   removeAuthorizedUserEntry()

      if(DEBUG) debug(this+".invalidateJSPSession()");

      ASPManager  mgr     = getASPManager();
      //HttpSession session = mgr.getAspSession();

      String redir_from = findGlobal(mgr.URL_FROM);
      String redir_to   = findGlobal(mgr.URL_TO);

      //session.invalidate();
      mgr.invalidateSession();

      if(!Str.isEmpty(redir_from)) setGlobal(mgr.URL_FROM, redir_from);
      if(!Str.isEmpty(redir_to))   setGlobal(mgr.URL_TO,   redir_to);
   }

   private static SecureRandom random = new SecureRandom();

   private String generateSecurityKey()
   {
      // called from:
      //   setAuthorizedUserEntry()

      byte[] bytes = new byte[8];
      synchronized(random)
      {
         random.nextBytes(bytes);
      }
      return Util.toHexText(bytes);
   }

   /**
    * @deprecated
    */
   public boolean isUserAuthorized()
   {
      return true;
      
      /* JAAS changes
         String sec_key = getCookie(auth_user_cookie_name);

         if( getASPPage().getASPConfig().isUserExternallyIdentified() || getASPManager().isJSPPage())
             return true;

         if(Str.isEmpty(sec_key))
         {
            //invalidateJSPSession();
            user_logged_on = false;
            return false;
         }

         HttpSession session = getASPManager().getAspSession();
         if (!sec_key.equals(session.getAttribute(AUTH_SECURITY_KEY)))
         {
            removeCookie(auth_user_cookie_name, null);
            invalidateJSPSession();
            user_logged_on = false;
            return false;
         }

         return true;
       */
   }

   /**
    *@deprecated
    */
   public void authorizeUser(String webuser)
   {
       //String sec_key = generateSecurityKey();

       HttpSession session = getASPManager().getAspSession();
       //session.setAttribute(AUTH_SECURITY_KEY,sec_key);
       session.setAttribute(AUTH_USER_ID,webuser);

       //setCookie(auth_user_cookie_name,sec_key);

       user_logged_on = true;
   }

   // prevent being able to go back in browser after loggin out in weblogic
   void setAuthorizedCookie(String webuser)
   {
       setCookie(auth_user_cookie_name,webuser);
   }

   /**
    * Unauthorize the user invalidating the session object.
    */
   public void unAuthorizeUser()
   {
       //removeCookie(auth_user_cookie_name, null);
       removeCookie(auth_user_cookie_name);
       invalidateJSPSession();
       user_logged_on = false;
   }

   /**
    *@deprecated
    */
   public String getAuthUserId()
   {
       HttpSession session = getASPManager().getAspSession();
       return (String)session.getAttribute(AUTH_USER_ID);
   }





   //==========================================================================
   //==========================================================================
   // Public methods for handling of global variables
   //==========================================================================
   //==========================================================================

   /**
    * Sets the value of a named global variable.
    */
   public void setGlobal( String name, String value )
   {
      if(DEBUG) debug(this+": ASPContext.setGlobal("+name+","+value+")" );

      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(value))
      {
         return;
      }
      else
      {
         try
         {
            HttpSession session = mgr.getAspSession();
            session.setAttribute(name, value);
            if(DEBUG) mgr.debugSessionAttributes(session, "after setting "+name);
         }
         catch( Throwable e )
         {
            error(e);
         }
      }
   }


   /**
    * Returns the value of a named global variable.
    */
   public String findGlobal( String name )
   {
      if(DEBUG) debug(this+": ASPContext.findGlobal("+name+")" );

      try
      {
         HttpSession session = getASPManager().getAspSession();
         return (String)session.getAttribute(name);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns the value of the named global variable. If the variable is empty
    * (or does not exist) the default value will be returned.
    */
   public String findGlobal( String name, String default_value )
   {
      if(DEBUG) debug(this+": ASPContext.findGlobal("+name+","+default_value+")");

      try
      {
         String value;
         value = findGlobal( name );

         if ( Str.isEmpty(value) ) return default_value;
         return value;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns the value of a named global variable. If the variable is empty
    * (or does not exist) the request is redirect to a given URL for definition of
    * the variable. The variable name is added to the URL as a query string.
    * If the variable is empty and the URL is missing, a run time exception
    * will be generated.
    */
   public String getGlobal( String name, String url )
   {
      String value;

      try
      {
         if(DEBUG) debug(this+": ASPContext.getGlobal("+name+","+url+")" );
         value = findGlobal( name );

         if ( Str.isEmpty(value) )
         {
            ASPManager mgr = getASPManager();
            if( mgr.isStdPortlet())
               return getStdPortalGlobal(name);
               //throw new FndException("Global variable [&1] cannot be automatically defined.", name);

            if ( Str.isEmpty(url) )
               throw new FndException("FNDCTXUDEFURL: URL for definition of [&1] is undefined.", name);
            else
            {
               if ( url.indexOf('?')<0 )
                  value = url + "?GLOBAL=" + name;
               else
                  value = url + "&GLOBAL=" + name;

               if(DEBUG) debug("  [getGlobal]redirect to URL: "+value);
               mgr.redirectTo( value );
            }
         }
         return value;
      }
      catch(Throwable e)
      {
         error(e);
      }
      return null;
   }

   private String getStdPortalGlobal(String name)
   {
      String value = null;

      try
      {
         String clsname = getGlobalClass(name);
         Class cls = Class.forName(clsname);

         DefineGlobalFactory dg = (DefineGlobalFactory) cls.newInstance();

         dg.defineGlobals(this, getASPManager());

         value = findGlobal( name );
         return value;
      }
      catch (Throwable any)
      {
         error(any);
      }
      return null;
   }

   private String getGlobalClass( String name )
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.getGlobalClass("+name+")" );

         ASPConfig cfg = getASPPage().getASPConfig();

         String class_name;

         class_name = cfg.getParameter("GLOBAL/"+name+"/CLASS",null);
         if(DEBUG) debug("  Class for global variable ["+name+"] fetched: "+class_name);

         if ( Str.isEmpty(class_name) )
            throw new FndException("FNDCTXUDEFCLASS: CLASS for definition of [&1] is undefined.", name);

         return class_name;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Returns the value of a named global variable. If the variable is empty
    * (or does not exist) the request is redirect to an URL defined in the ASPConfig.ifm
    * file at the entry point 'GLOBAL/<variable_name>/URL' for definition of
    * the variable. The variable name is added to the URL as a query string.
    * If the variable is empty and the URL is missing, a run time exception
    * will be generated.
     */
   public String getGlobal( String name )
   {
      try
      {
         return getGlobal( name, getGlobalURL( name ) );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Resets a named global variable.
    */
   public void removeGlobal( String name )
   {
      if(DEBUG) debug(this+": ASPContext.removeGlobal("+name+")" );

      try
      {
         ASPManager mgr = getASPManager();
         HttpSession session = mgr.getAspSession();
         session.removeAttribute(name);
         if(DEBUG) mgr.debugSessionAttributes(session, "after removing "+name);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Returns the URL for definition of named global variable.
    * The URL can be defined in the ASPConfig.ifm at entry point
    * 'GLOBAL/<variable_name>/URL'.
    */
   public String getGlobalURL( String name )
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.getGlobalURL("+name+")" );

         ASPConfig cfg = getASPPage().getASPConfig();

         String url;

         url = cfg.getParameter("GLOBAL/"+name+"/URL", null);
         if(DEBUG) debug("  URL for global variable ["+name+"] fetched: "+url);

         url = Str.replaceFirst(url,cfg.getDefaultApplicationPath(),cfg.getApplicationPath());

         return url;
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   //==========================================================================
   // Private methods for handling of global variables implemented by cookies
   //==========================================================================


   /**
    * Returns the name of the common cookie used by IFS Applications to store
    * global variables.
    *<p>
    * All global variables are stored as items in the same cookie which name
    * can be defined in the ASPConfig.ifm at entry point 'COOKIE/NAME'.
    *<p>
    * If no cookie name is defined in the configurations file '__Ifs_Applications__'
    * is taken.
    *
    * @see ifs.fnd.asp.ASPContext#getCookieExpires
    */
   private String getCookieName() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.getCookieName()");

      String name;

      name = getASPPage().getASPConfig().getParameter("COOKIE/NAME","__Ifs_Applications__");
      if(DEBUG) debug(this+": ASPContext.getCookieName():\n\t  cookie name fetched: "+name);

      return name;
   }


   /**
    * Returns the expire date of the common cookie used by IFS Applications to store
    * global variables.
    *<p>
    * The date can be defined in the ASPConfig.ifm at entry point 'COOKIE/EXPIRES'
    * as a number of days from today.
    *<p>
    * If no date is defined in the configurations file the value of '0' is taken,
    * which means that cookie will not be stored and will disappear on browser exit.
    *
    * @see ifs.fnd.asp.ASPContext#getCookieName
    */
   private Date getCookieExpires() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.getCookieExpires()");

      Date curr_date = new Date();
      int days;

      days = Integer.parseInt( getASPPage().getASPConfig().getParameter("COOKIE/EXPIRES","0") ) * 24*3600*1000;
      if (days==0)
         curr_date = null;
      else
         curr_date.setTime(curr_date.getTime() + days);
      if(DEBUG) debug(this+": ASPContext.getCookieExpires():\n\t  cookie expire date fetched: "+curr_date);

      return curr_date;
   }

   //==========================================================================
   //==========================================================================
   // Public routines for handling of context cache
   //==========================================================================
   //==========================================================================

    /**
     * Keep the old context in the cache, no matter how the context cache has been configurated.
     **/

    public void keepContextInCache()
    {
        keep_context_in_cache = true;
        remove_context_from_cache = false;
    }


    /**
     * Force the old context out of the cache, no matter how the context cache has been configurated.
     **/

    public void removeContextFromCache()
    {
        keep_context_in_cache=false;
        remove_context_from_cache = true;
    }

    /**
     * Undo the changes in the context cache made by removeContextFromCache() and keepInContextCache().
     * This function is only meaningful during a requst when the aforementioned functions were called.
     **/

    public void undoContextCacheChange()
    {
        keep_context_in_cache=false;
        remove_context_from_cache = false;
    }

    boolean isCacheUserSaved()
    {
        return keep_context_in_cache;
    }

    boolean isCacheUserRemoved()
    {
        return remove_context_from_cache;
    }

   //==========================================================================
   //==========================================================================
   // Common public routines for handling of context buffers
   //==========================================================================
   //==========================================================================


   /**
    * Copies named item from the request context buffer to the response one.
    * The item can be of type ASPBuffer, ASPBlock, ASPTable or String.
    * If the item does not exist in the request buffer the empty one will be
    * crated in the response context buffer.
    *<p>
    * The buffer is serialized and then stored on the ASP page in a hidden HTML
    * field with name '__CONTEXT'.
    *<p>
    * The request buffer is fetched from Request object; the response buffer is
    * prepared and then saved in the field.
    *<p>
    * The specified name may be used to restore the item contents
    * from the next client request.
    *<p>
    * All methods for handling of context buffers will work only if a call to
    * method generateClientScript() within ASPManager class is correctly placed
    * in the ASP file.
    */
   public void rewrite( String name )
   {
      try
      {
         if(DEBUG)
         {
            debug(this+": ASPContext.rewrite("+name+")");
            debug(" - Request buffer ("+req_buf.getClass().getName()+"):\n"+Buffers.listToString(req_buf));
            debug(" - Response buffer ("+resp_buf.getClass().getName()+"):\n"+Buffers.listToString(resp_buf));
         }
         rewrite(name,null,true);
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   //==========================================================================
   // Public methods for handling of ASPBuffers
   //==========================================================================


   /**
    * Save an ASPBuffer in the context buffer.
    * The specified name may be used to restore the buffer contents
    * from the next client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public void writeBuffer( String name, ASPBuffer buf )
   {
      if(DEBUG) debug(this+": ASPContext.writeBuffer("+name+","+buf+")");

      try
      {
         //writeItemReference( new Item( name, (Buffer)buf.getBuffer().clone() ), true );
         writeItemReference( new Item( name, buf.getBuffer() ), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
      if(DEBUG)
      {
         debug(this+": ASPContext.writeBuffer()");
         buf.traceBuffer(name);
      }
   }


   /**
    * Restore the contents of the named ASPBuffer previously stored
    * in a context buffer. Return null if buffer does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public ASPBuffer readBuffer( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readBuffer("+name+")");

      try
      {
         //Item item = readClonedItem( name );
         Item item = readItemReference( name );
         return item==null ? null : (new ASPBuffer(getASPManager())).construct( (Buffer)item.getValue() );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Restore the contents of the named ASPBuffer previously stored
    * in a context buffer and save it again for future use.
    * The specified name may be used to restore the buffer contents
    * from the next client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public ASPBuffer readwriteBuffer( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readwriteBuffer("+name+")");

      try
      {
         rewrite( name );
         return readBuffer( name );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   // Public methods for handling of ASPBufferable objects
   //==========================================================================

   /**
    * Save an ASPBufferable object in the context buffer.
    * The specified name may be used to restore the object
    * from the next client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public void writeObject( String name, ASPBufferable obj )
   {
      if(DEBUG) debug(this+": ASPContext.writeObject("+name+","+obj+")");

      try
      {
         ASPBuffer aspbuf = getASPManager().newASPBuffer();
         obj.save(aspbuf);
         writeItemReference( new Item(name,aspbuf.getBuffer()), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Restore the contents of the named ASPBufferable object previously stored
    * in the context buffer. Return null if such object does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public ASPBufferable readObject( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readObject("+name+")");

      try
      {
         Item item = readItemReference(name);
         if (item==null) return null;
         Buffer buf = item.getBuffer();

         String clsname = buf.getString("CLASS");
         ASPManager mgr = getASPManager();
         ASPBufferable obj = (ASPBufferable)mgr.newASPObject(clsname);
         obj.load(mgr.newASPBuffer(buf));
         return obj;
      }
      catch(Throwable e)
      {
         error(e);
         return null;
      }
   }


   /**
    * Restore the contents of the named ASPBufferable object previously stored
    * in the context buffer and save it again for future use.
    * The specified name may be used to restore the object
    * from the next client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public ASPBufferable readwriteObject( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readwriteObject("+name+")");

      try
      {
         rewrite(name);
         return readObject(name);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   // Public methods for handling of Strings
   //==========================================================================


   /**
    * Save a string value in the context buffer.
    * The specified name may be used to restore the string from the next
    * client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public void writeValue( String name, String value )
   {
      if(DEBUG) debug(this+": ASPContext.writeValue("+name+","+value+")");

      try
      {
         writeItemReference( new Item( name, value ), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Restore the value of the named string previously stored
    * in a context buffer. Return null if such a string does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public String readValue( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readValue("+name+")");

      try
      {
         return readValue( name, null );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Restore the value of the named string previously stored
    * in a context buffer. Return default value if such a string does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public String readValue( String name, String default_value )
   {
      if(DEBUG) debug(this+": ASPContext.readValue("+name+","+default_value+")");

      try
      {
         Item item = readItemReference( name );
         return item==null ? default_value : item.getString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Restore the value of the named string previously stored
    * in a context buffer and save it again for future use.
    * The specified name may be used to restore the string from the next
    * client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public String readwriteValue( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readwriteValue("+name+")");

      try
      {
         rewrite( name );
         return readValue( name );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   //==========================================================================
   // Public methods for handling of Numbers
   //==========================================================================


   /**
    * Save a numeric value in the context buffer.
    * The specified name may be used to restore the number from the next
    * client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public void writeNumber( String name, double value )
   {
      if(DEBUG) debug(this+": ASPContext.writeNumber("+name+","+value+")");

      try
      {
         writeItemReference( new Item( name, new Double(value) ), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   public void writeNumber( String name, int value )
   {
      if(DEBUG) debug(this+": ASPContext.writeNumber("+name+","+value+")");

      try
      {
         writeItemReference( new Item(name,value), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Restore the value of the named number previously stored
    * in a context buffer. Return NaN if such a string does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public double readNumber( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readNumber("+name+")");

      try
      {
         return readNumber( name, ASPManager.NOT_A_NUMBER );
      }
      catch( Throwable any )
      {
         error(any);
         return ASPManager.NOT_A_NUMBER;
      }
   }


   /**
    * Restore the value of the named number previously stored
    * in a context buffer. Return default value if such a number does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public double readNumber( String name, double default_value )
   {
      if(DEBUG) debug(this+": ASPContext.readValue("+name+","+default_value+")");

      try
      {
         Item item = readItemReference( name );
         return item==null ? default_value : item.getDouble();
      }
      catch( Throwable any )
      {
         error(any);
         return ASPManager.NOT_A_NUMBER;
      }
   }

   public int readNumber( String name, int default_value )
   {
      if(DEBUG) debug(this+": ASPContext.readValue("+name+","+default_value+")");

      try
      {
         Item item = readItemReference( name );
         return item==null ? default_value : item.getInt();
      }
      catch( Throwable any )
      {
         error(any);
         return default_value;
      }
   }


   /**
    * Restore the value of the named number previously stored
    * in a context buffer and save it again for future use.
    * The specified name may be used to restore the number from the next
    * client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public double readwriteNumber( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readwriteNumber("+name+")");

      try
      {
         rewrite( name );
         return readNumber( name );
      }
      catch( Throwable any )
      {
         error(any);
         return ASPManager.NOT_A_NUMBER;
      }
   }


   //==========================================================================
   // Public methods for handling of boolean flags
   //==========================================================================


   /**
    * Save a boolean flag in the context buffer.
    * The specified name may be used to restore the flag from the next
    * client request.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public void writeFlag( String name, boolean value )
   {
      if(DEBUG) debug(this+": ASPContext.writeFlag("+name+","+value+")");

      try
      {
         if (value)
            writeItemReference( new Item( name, "TRUE" ), true );
         else
            writeItemReference( new Item( name, "FALSE" ), true );
      }
      catch( Throwable any )
      {
         error(any);
      }
   }


   /**
    * Restore the value of the named boolean flag previously stored
    * in a context buffer. Return default value if such flag does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   public boolean readFlag( String name, boolean default_value )
   {
      if(DEBUG) debug(this+": ASPContext.readFlag("+name+","+default_value+")");

      try
      {
         Item item = readItemReference( name );
         if (item==null) return default_value;
         if ( "TRUE".equals( item.getString() ) )
            return true;
         else
            return false;
      }
      catch( Throwable any )
      {
         error(any);
         return false;
      }
   }


   //==========================================================================
   // Private common routines for handling of context buffers
   //==========================================================================


   /**
    * Writes a named item (string or buffer) to the response context buffer, which
    * will be written as a hidden HTML field with name '__CONTEXT' to the ASP page.
    * If the overwrite flag is false an exception will be generated if the item
    * already exists in the response buffer.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   private void writeItemReference( Item item, boolean overwrite )
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.writeItemReference("+item+","+overwrite+")" );

         if(isFrozen()) throw new FndException("FNDCTXFROZ: Context already written to the response object.");

         Item existing_item = resp_buf.getItem( item.getName(), null );

         if ( existing_item==null )
            resp_buf.addItem( item );
         else
         {
            if (overwrite)
               existing_item.setValue( item.getValue() );
            else
            {
               if(DEBUG) debug("  Response buffer:\n"+Buffers.listToString(resp_buf));
               throw new FndException("FNDCTXITEX: Item '&1' already exists.",existing_item.getName());
            }
         }
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Writes a named item (string or buffer) to the response context buffer, which
    * will be written as a hidden HTML field with name '__CONTEXT' to the ASP page.
    * If the overwrite flag is false an exception will be generated if the item
    * already exists in the response buffer.
    *
    * @see ifs.fnd.asp.ASPContext#writeItemReference
    */
   private void writeClonedItem( Item item, boolean overwrite ) throws Exception
   {
      if(DEBUG) debug(this+": ASPContext.writeClonedItem("+item+","+overwrite+")" );
      writeItemReference( (Item)item.clone(), overwrite );
   }


   /**
    * Reads a named item (string or buffer) from the context response or request
    * buffer. The request buffer is automatically created from a hidden HTML field
    * named '__CONTEXT'. Returns null if such an item does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   protected Item readItemReference( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readItemReference("+name+")");

      Item item = resp_buf.getItem( name, req_buf.getItem( name, null ) );
      return item==null ? null : item;
   }


   /**
    * Reads a named item (string or buffer) from the context response or request
    * buffer. The request buffer is automatically created from a hidden HTML field
    * named '__CONTEXT'. Returns null if such an item does not exist.
    *
    * @see ifs.fnd.asp.ASPContext#readItemReference
    */
   private Item readClonedItem_( String name )
   {
      if(DEBUG) debug(this+": ASPContext.readClonedItem("+name+")");

      Item item = readItemReference( name );
      return item==null ? null : (Item)item.clone();
   }


   /**
    * Copies named item from the request context buffer to the response one and
    * marks it as derived from another item.
    * If the item does not exist in the request buffer an empty one will be
    * crated in the response context buffer.
    *
    * @see ifs.fnd.asp.ASPContext#rewrite
    */
   protected void rewrite( String name, String master_name, boolean do_clone )
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.rewrite("+name+","+master_name+","+do_clone+")");

         Item item = req_buf.getItem( name, null );
         if( item==null ) item = newCompoundItem(name);
         if( master_name!=null ) item.setType( master_name );
         if(DEBUG) debug("  item: "+item);
         if(do_clone)
            writeClonedItem( item, false );
         else
            writeItemReference(item,false);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }


   /**
    * Returns a reference to a named compound item (buffer) in the context response
    * buffer. Creates a new, empty item with empty buffer if such one does not already exist.
    *
    * @see ifs.fnd.asp.ASPContext#readItem
    */
   protected Item getCompoundItemReference( String name ) throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.getCompoundItemReference("+name+")");

      if(isFrozen()) throw new FndException("FNDCTXFRZ2: Context already written to the response object.");

      Item item = (Item)resp_buf.getItem( name, null );
      if (item==null)
      {
         item = newCompoundItem( name );
         writeItemReference( item, false );
      }
      if(DEBUG) debug("item="+item);
      return item;
   }


   /**
    * Expand a previously compressed context buffer.
    */
   private void expandBuffer_( Buffer buf ) throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.expandBuffer("+buf+")");

      BufferIterator itr = buf.iterator();
      while ( itr.hasNext() )
      {
         Item item = itr.next();
         String master_name = item.getType();

         if (master_name!=null && "M".equals(item.getStatus()) )
         {
           if (item.getValue()!=null) throw new FndException("FNDCTXHASVAL: Item already has a value !");
           // master defined for the item and item marked with "M"
           item.setValue( ((Item)(buf.getItem( master_name, null ).clone())).getValue() );
           // Null Pointer Exception will be generated if master does not exist
           item.setStatus("C");
         } // else nothing - an ordinary item or not equals master value
      }
      if(DEBUG) debug("  buf: "+buf);
   }


   /**
    * Compress context buffer before its serialization.
    */
   private Buffer compressBuffer_( Buffer buf )
   {
      if(DEBUG) debug(this+": ASPContext.compressBuffer("+buf+")");

      BufferIterator itr = buf.iterator();
      while ( itr.hasNext() )
      {
         Item item = itr.next();
         String master_name = item.getType();

         if (master_name!=null)
         {
            Object value = item.getValue();
            Item master = buf.getItem( master_name, null );

            if (master!=null && value!=null && value.equals(master.getValue()) )
            {
              // item value equals with master value
              item.setStatus("M");
              item.setValue(null);
            }
            else
              // master does not exist or its value not equals item value
              item.setStatus("C");
         } // else nothing - an ordinary item
      }
      if(DEBUG) debug("  buf: "+buf);
      return buf;
   }


   //==========================================================================
   // Routins for handling of Db/Current state
   //==========================================================================


   /**
    * Callback function that will be called from ASPManager just before
    * generating of client script. Adds a private HTML field named '__CONTEXT'.
    */
   void prepareClientScript() throws FndException,IOException
   {
      if(DEBUG) debug(this+": ASPContext.prepareClientScript()");

      ASPManager mgr = getASPManager();
      String value;

      writeFlag("__KEEPCONTEXT", keep_context_in_cache);
      writeFlag("__REMOVECONTEXT", remove_context_from_cache);

      if(getASPPage() instanceof DefaultStdPortlet) //CONTEXT always send to the browser if portlets run in Standard portal
      {
         //String value = buf_frmt.format(compressBuffer(resp_buf));
         value = buf_frmt.format(resp_buf);
         value = Util.compress(value);
      }
      else // do not send context to the browser
      {
         value = "*";
         if (DEBUG) 
         {
            debug(" ============ Put the new context ====================");
            debug(printMaps(resp_map));
            debug(" =====================================================");
         }
            
         ASPContextCache.put(context_id, mgr.getPageId(), new ASPContextCache.ContextObject(resp_buf,resp_map), mgr);
      }

      //Bug 40903, start
      if (!context_id.equals("*"))
       mgr.addFieldToGenerate(CONTEXT_ID_FIELD_NAME,context_id);
      else
       mgr.addFieldToGenerate(CONTEXT_ID_FIELD_NAME,"");
      //Bug 40903, end
      mgr.addFieldToGenerate(CONTEXT_FIELD_NAME,value);
      frozen = true;
   }


   /**
    * Put back the old context in the cache. Called on error from ASPLog
    * to preserve the old, already removed, context.
    */
   void rollbackContext() throws FndException
   {
      if(DEBUG) debug(this+": ASPContext.rollbackContext()");
      //Bug 40903, start
      ASPContextCache.put(context_id,old_page_id, new ASPContextCache.ContextObject(req_buf,req_map), getASPManager());
      //Bug 40903, end
   }


   /**
    * Returns an ASPBuffer with all requested items and their database states.
    */
   public ASPBuffer getDbState()
   {
      try
      {
         if(DEBUG) debug(this+": ASPContext.getDbState()");
         return readBuffer( DB_STATE_BUF_NAME );
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    * Called at submit() from ASPManager. Updates contents of both DB and
    * current state context buffers.
    */
   void putDbState( ASPBuffer buf) throws Exception
   {
      if(DEBUG) debug(this+": ASPContext.putDbState("+buf+")");

      writeBuffer( DB_STATE_BUF_NAME,      buf );
      writeBuffer( CURRENT_STATE_BUF_NAME, buf );
   }


   /**
    * Returns a reference to a Buffer with all items and their current states.
    * The buffer will be created if not already exists.
    */
   Buffer getCurrentStateBuffer() throws Exception
   {
      if(DEBUG) debug(this+": ASPContext.getCurrentStateBuffer()");
      return (Buffer)getCompoundItemReference( CURRENT_STATE_BUF_NAME ).getValue();
   }


   /**
    * Returns a reference to an ASPBuffer with all items and their current states.
    */
   ASPBuffer getCurrentState() throws Exception
   {
      if(DEBUG) debug(this+": ASPContext.getCurrentState()");
      return (new ASPBuffer(getASPManager())).construct(getCurrentStateBuffer());
   }

   void addFndAbstractArray(FndAbstractArray data, String blk_name) 
   {
      ASPContextCache.ContextDataSet ctx_db = new ASPContextCache.ContextDataSet(data,0,data.size(),0,null,false,false,false,false);
      ASPContextCache.ContextDataSet ctx_cur;
      try {
         ctx_cur= new ASPContextCache.ContextDataSet((FndAbstractArray)data.clone(),0,data.size(),0,null,false,false,false,false);
      } catch (CloneNotSupportedException ex) {
         ex.printStackTrace();
         ctx_cur = null;
      }
      resp_map.put(blk_name+__DB,ctx_db);
      resp_map.put(blk_name+__CURRENT,ctx_cur);
   }

   void mergeFndAbstractRecordToArray(FndAbstractRecord data, String blk_name, int rowno)
   {
      ASPContextCache.ContextDataSet ctx_cur = getCurrentContextDataSet(blk_name);
      ASPContextCache.ContextDataSet db_ctx_cur = getDBContextDataSet(blk_name);
      FndAbstractArray array = ctx_cur.getData();
      FndAbstractArray db_array = db_ctx_cur.getData();
      
      List recList = FndAttributeInternals.getInternalRecords(array);
      if(rowno < recList.size())
         recList.remove(rowno);
      if(data!=null)
         recList.add(rowno, data);
      
      db_array = array;
   }

   String showContents()
   {
      AutoString out = new AutoString();
      out.append("\nContents of: "+this+"\nRequest Context Buffer:\n",Buffers.listToString(req_buf));
      out.append("\nResponse Context Buffer\n",Buffers.listToString(resp_buf));
      out.append("\nData Maps (FndAbstractArrays and INFO\n");
      out.append("==========================================\n");
      out.append("Request Data Map\n");
      out.append(printMaps(req_map));
      out.append("==========================================\n");
      out.append("Response Data Map\n");
      out.append(printMaps(resp_map));
      return out.toString();     
   }

   private String printMaps(HashMap map)
   {
      Iterator keys = map.keySet().iterator();
      AutoString out = new AutoString();
      while (keys.hasNext())
      {
         String key = (String)keys.next();
         out.append("\n#### ",key ," #### \n");
         ASPContextCache.ContextDataSet data_set = (ASPContextCache.ContextDataSet)map.get(key);
         out.append(data_set.toString());
      }   
      return out.toString();
      
   }
   
   HashMap getDataSet()
   {
      return resp_map;
   }
   
   /**
    * Returns the currunt data array for the given block.
    */
   
   ASPContextCache.ContextDataSet getCurrentContextDataSet(String blk_name)
   {
      Object array = resp_map.get(blk_name + __CURRENT);
      if (array != null)
         return (ASPContextCache.ContextDataSet)array;
      return null;
   }

   /**
    * Returns the db data array for the given block.
    */
   
   ASPContextCache.ContextDataSet getDBContextDataSet(String blk_name)
   {
      Object array = resp_map.get(blk_name + __DB);
      if (array != null)
         return (ASPContextCache.ContextDataSet)array;
      return null;
   }
   
   // Added by Terry 20120822
   // Set object into session.
   public void setGlobalObject( String name, Object obj )
   {
      if(DEBUG) debug(this+": ASPContext.setGlobal("+name+")" );

      ASPManager mgr = getASPManager();

      try
      {
         HttpSession session = mgr.getAspSession();
         session.setAttribute(name, obj);
         if(DEBUG) mgr.debugSessionAttributes(session, "after setting "+name);
      }
      catch( Throwable e )
      {
         error(e);
      }
   }
   
   // Get object from session.
   public Object findGlobalObject( String name )
   {
      if(DEBUG) debug(this+": ASPContext.findGlobal("+name+")" );

      try
      {
         HttpSession session = getASPManager().getAspSession();
         return session.getAttribute(name);
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }
   // Added end
}
