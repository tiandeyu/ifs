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
 * File        : ASPPortalColumn.java
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
 *    Jacek P  2003-Dec-10 - Better debugging possibilities of Page Pool.
 *    Jacek P  2004-Jan-04 - Bug#40931. Added handling of session related data from ASPPortletHandle.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2005/09/28 11:50:10  japase
 * Added method isReset() to make it possible to check if portlets have been reset.
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
 *  Package class ASPPortalColumn
 *
 */
class ASPPortalColumn implements Serializable
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortalColumn");

   private int     width;
   private boolean dynamic;
   private Vector  portlets = new Vector();


   ASPPortalColumn( int width, boolean dynamic )
   {
      if(DEBUG) debug(this+": ASPPortalColumn.<init>");

      this.width   = width;
      this.dynamic = dynamic;
   }


   boolean isDynamic()
   {
      if(DEBUG) debug(this+": ASPPortalColumn.isDynamic()");
      return dynamic;
   }


   int getWidth()
   {
      if(DEBUG) debug(this+": ASPPortalColumn.getWidth()");
      return width;
   }


   void addPortletHandle( ASPPortletHandle porthand )
   {
      if(DEBUG) debug(this+": ASPPortalColumn.addPortletHandle("+porthand+")");
      portlets.addElement(porthand);
   }


   ASPPortletHandle getPortletHandle( int index )
   {
      return (ASPPortletHandle)portlets.elementAt(index);
   }


   int countPortlets()
   {
      return portlets.size();
   }


   boolean equals( ASPPortalColumn col )
   {
      if(DEBUG) debug(this+": ASPPortalColumn.equals("+col+")");

      if( col==null ) return false;
      if( width != col.width || dynamic != col.dynamic ) return false;

      int psize = portlets.size();
      if( psize != col.portlets.size() ) return false;

      for(int p=0; p<psize; p++)
      {
         ASPPortletHandle ph = (ASPPortletHandle)(portlets.elementAt(p));
         if(!ph.equals( (ASPPortletHandle)(col.portlets.elementAt(p)) ) ) return false;
      }

      return true;
   }


   void activate( ASPPortal portal,
                  boolean   instantiate,
                  String    portletid,
   //Bug 40931, start
   //             Buffer    user_portlets ) throws FndException
                  Buffer    user_portlets, ASPPage toppage ) throws FndException
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalColumn.activate("+portal+","+instantiate+","+portletid+","+user_portlets+")");
      for(int p=0; p<portlets.size(); p++)
        //Bug 40931, start
         ((ASPPortletHandle)(portlets.elementAt(p))).activate(portal,instantiate,portletid,user_portlets,toppage);
        //Bug 40931, end
   }

  //Bug 40931, start
   void forceDirty( ASPPortal portal ) throws FndException
  //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalColumn.forceDirty()");
      for(int p=0; p<portlets.size(); p++)
        //Bug 40931, start
         ((ASPPortletHandle)(portlets.elementAt(p))).forceDirty(portal);
        //Bug 40931, end
   }

   //Bug 40931, start
   void reset( ASPPortal portal, ASPManager mgr, ASPPage toppage )
   //Bug 40931, end
   {
      if(DEBUG) debug(this+": ASPPortalColumn.reset("+mgr+")");
      for(int p=0; p<portlets.size(); p++)
         //Bug 40931, start
         ((ASPPortletHandle)(portlets.elementAt(p))).reset(portal, mgr, toppage);
          //Bug 40931, end
   }

   boolean isReset( ASPPortal portal )
   {
      if(DEBUG) debug(this+": ASPPortalColumn.isReset("+portal+")");
      for(int p=0; p<portlets.size(); p++)
         if( !((ASPPortletHandle)(portlets.elementAt(p))).isReset(portal) ) return false;
      return true;
   }

   /**
    * Debug printout to the DBMON console.
    */
   private void debug( String text )
   {
      Util.debug(text);
   }
}

