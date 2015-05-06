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
 * File        : ASPPortletHandle.java
 * Description : User profile for ASPPortal
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2000-Mar-21 - Created
 *    Jacek P  2000-Apr-17 - Added forceDirty() and showContents().
 *                           removed-flag moved from ASPPortletProvider.
 *    Jacek P  2000-Apr-18 - Added caching of HTML code. Changes in activate()
 *    Jacek P  2000-May-08 - Beta 4 release of Web Kit 3.0.0.
 *    Jacek P  2000-May-22 - Added handling of ClassNotFoundException
 *                           in ASPPortletHandle.activate()
 *    Jacek P  2000-May-31 - Changed default values for column configuration in construct()
 *    Jacek P  2000-Aug-02 - Renamed and reimplemented functions to- and fromByteString().
 *    Jacek P  2000-Aug-04 - Do not check security in findPortletHandle() (Log id: 300)
 *    Jacek P  2000-Aug-08 - Changes in clone() and rearrangeColumns()
 *    Jacek P  2000-Aug-14 - Added handling of total width of dynamic columns.
 *    Jacek P  2000-Oct-17 - Implementation of the automatic refresh (log id #427).
 *    Jacek P  2000-Nov-16 - Instantiate portlets if the HTML-cache is empty. Changed
 *                           interface of the activate() methods (Log id #331).
 *    Artur K  2000-Nov-23 - Changes regarding replacing ASPPoolElementProfile
 *    Piotr Z                with ASPPoolElementProfile
 *                           Removed MAXTITLE attribute and its asscociated functions.
 *    Jacek P  2000-Dec-11 - Added back MAX- & MINTITLE functionality.
 *    Jacek P  2000-Dec-21 - Improved implementation of the clone() methods.
 *                           Improved error handling in activate() and reset().
 *    Jacek P  2000-Feb-05 - Classes ASPPortalColumn and ASPPortletHandle moved
 *                           to their own files.
 *    Sampath  2002-Dec-26 - Add new methods setCanZoomInURL() and getCanZoomInURL() to have zoomIn functionality
 *    Rifki R  2003-Jan-09 - Log id 1010, made call to checkObjectAccess() in activate().
 *    Jacek P  2004-Jan-04 - Bug#40931. All session and request related data moved to a new class,
 *                           ASPPortletSessionHandler, stored within the session object.
 *                           To minimize affection on other classes, all session related methodes
 *                           are still kept here with delegation to the new class. Should be moved there
 *                           in the future. Only pure profile data kept here due to possibilities
 *                           of usage by different threads simultaneously.
 *    Chandana 2004-Aug-05 - Proxy support corrections.
 *    Ramila H 2004-09-07  - Added lang_code suffix to poolkey(clspath) using ASPPagePool.getPoolKeyLangSuffix.
 *    Ramila H 2004-11-02  - activated only the current portlet if standard portal mode.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2005/09/28 11:48:53  japase
 * Added method isReset() to make it possible to check if a portlet has been reset.
 *
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.lang.reflect.*;


/**
 *
 *  Package class ASPPortletHandle
 *
 */
class ASPPortletHandle implements Serializable
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortletHandle");

   // profile information
   private String clsname;
   private String id;

   private Buffer profile;

   private boolean removed;

   //Bug 40931, start

   //********************************************************
   // Variables below must not be a part of the profile information
   // and have been moved to a new class stored in the session object

   /*
   // request information
   private ASPPortletProvider portlet;

   // session information
   private String  mintitle;
   private String  maxtitle;
   private boolean cancustom;
   private String  canzoomin;

   private AutoString html     = new AutoString();
   private AutoString clscript = new AutoString();
   private AutoString drscript = new AutoString();
   private AutoString popupscr = new AutoString();
   private AutoString popupdef = new AutoString();

   private boolean user_available;
   */
   //Bug 40931, end

   ASPPortletHandle()
   {
      if(DEBUG) debug(this+".<init>");
   }

   //Bug 40931, start
   //ASPPortletHandle( String clsname, String id, ASPPortletProvider portlet )
   ASPPortletHandle( String clsname, String id )
   {
      //if(DEBUG) debug(this+".<init>: clsname="+clsname+", id="+id+", portlet="+portlet);
      if(DEBUG) debug(this+".<init>: clsname="+clsname+", id="+id);
   //Bug 40931, end

      this.clsname = clsname;
      this.id      = id;
      //Bug 40931, start
      /*
      html     = new AutoString();
      clscript = new AutoString();
      drscript = new AutoString();
      popupscr = new AutoString();
      popupdef = new AutoString();

      this.portlet = portlet;
      if(portlet!=null)
         portlet.setHandle(this);
      */
      //Bug 40931, end

   }


   public Object clone()
   {
      if(DEBUG) debug(this+".clone(): clsname="+clsname+", id="+id);
      ASPPortletHandle ph = new ASPPortletHandle();

      synchronized(this)
      {
         //Strings and simple types
         ph.clsname        = clsname;
         ph.id             = id;
         ph.removed        = removed;

         ph.profile = profile==null ? null : (Buffer)(profile.clone());
         //Bug 40931, start
         /*
         ph.mintitle       = mintitle;
         ph.maxtitle       = maxtitle;
         ph.cancustom      = cancustom;
         ph.canzoomin      = canzoomin;
         ph.user_available = user_available;

         // AutoStrings
         ph.html     = (AutoString)(html.clone());
         ph.clscript = (AutoString)(clscript.clone());
         ph.drscript = (AutoString)(drscript.clone());
         ph.popupscr = (AutoString)(popupscr.clone());
         ph.popupdef = (AutoString)(popupdef.clone());

         // Other objects
         ph.portlet = null;
         */
         //Bug 40931, end
      }
      return ph;
   }


   Buffer save( ASPPage page )
   {
      if(DEBUG) debug(this+": ASPPortletHandle.save("+page+")");

      Buffer buf = page.getASPConfig().getFactory().getBuffer();

      buf.addItem( "CLSNAME", clsname );
      buf.addItem( "ID",      id      );

      if(profile!=null)
      synchronized(profile)
      {
         buf.addItem( "PROFILE", profile );
      }

      if(DEBUG) debug("buf=\n"+Buffers.listToString(buf)+"\n");

      return buf;
   }


   void load( ASPPage page, Buffer from ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortletHandle.load("+page+"):\n"+Buffers.listToString(from)+"\n");

      clsname = from.getString( "CLSNAME" );
      id      = from.getString( "ID"      );
      profile = from.getBuffer( "PROFILE", null );
   }


   String getClassName()
   {
      return clsname;
   }


   String getId()
   {
      return id;
   }


   //Bug 40931, start
   ASPPortletProvider getPortlet( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getPortlet();
   }
   //Bug 40931, end


   Buffer getProfile()
   {
      return profile;
   }


   Buffer setProfile( Buffer profile )
   {
      this.profile = profile;
      return this.profile;
   }


   void removeProfile()
   {
      profile = null;
   }


   //Bug 40931, start
   void setMinTitle( ASPPortal portal, String title )
   {
      portal.getPortletSessionHandle(this).setMinTitle(title);
   }


   String getMinTitle( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getMinTitle();
   }


   void setMaxTitle( ASPPortal portal, String title )
   {
      portal.getPortletSessionHandle(this).setMaxTitle(title);
   }


   String getMaxTitle( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getMaxTitle();
   }


   void setCanCustomize( ASPPortal portal, boolean cancust )
   {
      portal.getPortletSessionHandle(this).setCanCustomize(cancust);
   }


   boolean canCustomize( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).canCustomize();
   }

   void setCanZoomInURL( ASPPortal portal, String canzoom )
   {
      portal.getPortletSessionHandle(this).setCanZoomInURL(canzoom);
   }

   String getCanZoomInURL( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getCanZoomInURL();
   }

   AutoString getHTML( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getHTML();
   }


   AutoString getClientScript( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getClientScript();
   }


   AutoString getDirtyScript( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getDirtyScript();
   }


   AutoString getPopupScript( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getPopupScript();
   }


   AutoString getPopupDefinitions( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).getPopupDefinitions();
   }
   //Bug 40931, end


   boolean equals( ASPPortletHandle ph )
   {
      if(DEBUG) debug(this+": ASPPortletHandle.equals("+ph+")");

      if( ph==null ) return false;
      if( (clsname!=null && !clsname.equals(ph.clsname)) || (clsname==null && ph.clsname!=null) ) return false;
      if( (id     !=null && !id.equals(ph.id)          ) || (id     ==null && ph.id     !=null) ) return false;

      if(profile!=null && !profile.equals(ph.profile) ) return false;
      if(profile==null && ph.profile!=null) return false;

      return true;
   }


   void activate( ASPPortal portal,
                  boolean   instantiate,
                  String    portletid,
                  //Bug 40931, start
                  //Buffer    user_portlets ) throws FndException
                  Buffer    user_portlets, ASPPage toppage ) throws FndException
                  //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortletHandle.activate("+portal+","+instantiate+","+portletid+","+user_portlets+")");

      //Bug 40931, start
      ASPPortletSessionHandle psh = portal.getPortletSessionHandle(this);

      if(user_portlets!=null)
         psh.setUserAvailable(user_portlets.findItem(clsname)!=null);
      else
         psh.setUserAvailable(true);

      psh.setPortlet(null);
      ASPPortletProvider portlet = null;

      AutoString html = psh.getHTML();
      //Bug 40931, end
      //if( portal==null || (!Str.isEmpty(portletid) && !portletid.equals(id)) ) return;
      if( (html.length()>0 && ( !instantiate || (!Str.isEmpty(portletid) && !portletid.equals(id)) ))
          || ( html.length()==0 && !instantiate )) return;

      //in standard portal mode go through the entire profile and activate only the current portlet
      if( portal.isStdPortlet() && (!Str.isEmpty(portletid) && !portletid.equals(id)) )
      {
         if(DEBUG) debug("  activate(): not the same ID (expected: '"+id+"') for JSR168 portlet; returning...");
         return;
      }

      if(DEBUG) debug("  Creating portlet...");
      ASPConfig cfg = portal.getASPPage().getASPConfig();
      String appurl  = cfg.getDefaultApplicationPath() +"/";
      String clspath = appurl + clsname + ".cls";
      if(DEBUG) debug("  clspath="+clspath);

      Object[] arg    = new Object[2];
      Class[]  argcls = new Class[2];

      arg[0]    = portal;
      argcls[0] = portal.getClass();

      ASPPortletProvider prv;

      ASPManager mgr = portal.getASPManager();
      //Bug 40931, start
      //ASPPageHandle ph = ASPPagePool.getASPPageHandle(mgr, clspath);
      clspath += ASPPagePool.getPoolKeyLangSuffix(mgr);
      ASPPageHandle ph = ASPPagePool.getASPPageHandle(mgr, clspath, toppage);
      //Bug 40931, end
      if(ph==null)
      {
         if(DEBUG) debug("  Provider not found in Page Pool.");

         arg[1]    = clspath;
         argcls[1] = clspath.getClass();
         if(DEBUG) debug("  arg=[1]"+arg[1]+"\n\t  argcls[1]="+argcls[1]);

         try
         {
            Class cls = Class.forName(clsname);
            Constructor ctr = cls.getConstructor(argcls);
            portlet = (ASPPortletProvider)(ctr.newInstance(arg));
         }
         catch( ClassNotFoundException x )
         {
            //Bug 40931, start
            psh.setUserAvailable(false);
            //Bug 40931, end
            return;
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

         portlet.setPoolKey(clspath);
         portlet.construct();
         portlet.setId(id);
         portlet.getASPContext().doActivate();
      }
      else
      {
         if(DEBUG) debug("  Provider found in Page Pool: ph=["+ph+"]");
         try
         {
            portlet = (ASPPortletProvider)(ph.getASPPage());
            portlet.setPageHandle(ph);
            portlet.setId(id);
            portlet.activate();
         }
         catch( Throwable any )
         {
            if(DEBUG) debug(this+": Caught exception ["+any+"].");
            if( ph!=null && !mgr.noreset )
               try
               {
                  if(DEBUG) debug("  Trying to unlock portlet for portlet=["+portlet+"], ph=["+ph+"] and mgr=["+mgr+"]...");
                  //Bug 40931, start
                  //ASPPagePool.unlock(ph,mgr);
                  ASPPagePool.unlock(ph,mgr,toppage);
                  //Bug 40931, end
               }
               catch( Throwable e)
               {
                  if(DEBUG) debug("  Caught exception ["+e+"] while unlocking the portlet ph=["+ph+"].");
                  mgr.getASPLog().logError(e);
               }
            portlet = null;
            if(DEBUG) debug("  Throwing exception ...");
            if(any instanceof ASPLog.AbortException)
               throw (ASPLog.AbortException)any;
            throw new FndException(any);
         }
      }

      portlet.setHandle(this);
      portlet.define();
      portlet.checkObjectAccess();
      //Bug 40931, start
      psh.setPortlet(portlet);
      //Bug 40931, end
   }

   //Bug 40931, start
   void forceDirty( ASPPortal portal ) throws FndException
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortletHandle.forceDirty()");

      //Bug 40931, start
      ASPPortletProvider portlet = portal.getPortletSessionHandle(this).getPortlet();
      //Bug 40931, end

      if(portlet!=null)
         portlet.forceDirty();
   }

   //Bug 40931, start
   //void reset( ASPManager mgr )
   void reset( ASPPortal portal, ASPManager mgr, ASPPage toppage )
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortletHandle.reset("+mgr+")");

      //Bug 40931, start
      ASPPortletProvider portlet = portal.getPortletSessionHandle(this).getPortlet();
      //Bug 40931, end
      if(portlet==null) return;

      ASPPageHandle ph = portlet.getPageHandle();

      if( ph!=null && !mgr.noreset )
      try
      {
         if(DEBUG) debug(this+" reset(): unlocking page. ph="+ph+")");
         if(ph.isLocked())
            //Bug 40931, start
            //ASPPagePool.unlock(ph,mgr);
            ASPPagePool.unlock(ph,mgr,toppage);
            //Bug 40931, end
         else if(portlet.isDirty())
            portlet.reset();
      }
      catch( Throwable e)
      {
         mgr.getASPLog().logError(e);
      }
      //Bug 40931, start
      portal.getPortletSessionHandle(this).setPortlet(null);
      //Bug 40931, end
   }

   boolean isReset( ASPPortal portal )
   {
      if(DEBUG) debug(this+": ASPPortletHandle.isReset("+portal+")");

      ASPPortletProvider portlet = portal.getPortletSessionHandle(this).getPortlet();
      if( portlet==null ) return true;

      ASPPageHandle ph = portlet.getPageHandle();
      if( ph==null ) return true;

      return portlet.isDefined();
   }

   void setRemoved( boolean removed )
   {
      this.removed = removed;
   }


   boolean isRemoved()
   {
      return this.removed;
   }

   //Bug 40931, start
   void setUserAvailable( ASPPortal portal, boolean user_available )
   {
      portal.getPortletSessionHandle(this).setUserAvailable(user_available);
   }


   boolean isUserAvailable( ASPPortal portal )
   {
      return portal.getPortletSessionHandle(this).isUserAvailable();
   }
   //Bug 40931, end


   /**
    * Debug printout to the DBMON console.
    */
   private void debug( String text )
   {
      Util.debug(text);
   }
}
