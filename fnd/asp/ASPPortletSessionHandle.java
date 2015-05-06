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
 * File        : ASPPortletSessionHandle.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2003-Dec-11 - Created
 *    Jacek P  2004-Dec-10 - Added creation of transient instances to readObject().
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/10 07:34:42  japase
 * Corrected serialization of ASPPortletSessionHandle class
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.io.*;

/**
 *
 *  Package class ASPPortletSessionHandle
 *
 */
class ASPPortletSessionHandle implements Serializable
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortletSessionHandle");

   // request information
   private transient ASPPortletProvider portlet;

   // session information
   private transient String  mintitle;
   private transient String  maxtitle;
   private transient boolean cancustom;
   private transient String  canzoomin;

   private transient AutoString html     = new AutoString();
   private transient AutoString clscript = new AutoString();
   private transient AutoString drscript = new AutoString();
   private transient AutoString popupscr = new AutoString();
   private transient AutoString popupdef = new AutoString();

   private transient boolean user_available;


   /**
    * The no-args constructor used only on deserialization
    */
   public ASPPortletSessionHandle()
   {
   }

   ASPPortletSessionHandle( ASPPortletHandle phandle, ASPPortletProvider portlet )
   {
      if(DEBUG) debug(this+".<init>: phandle="+phandle+", portlet="+portlet);

      html     = new AutoString();
      clscript = new AutoString();
      drscript = new AutoString();
      popupscr = new AutoString();
      popupdef = new AutoString();

      this.portlet = portlet;
      if(portlet!=null)
         portlet.setHandle(phandle);
   }


   ASPPortletProvider getPortlet()
   {
      return portlet;
   }


   ASPPortletProvider setPortlet( ASPPortletProvider portlet )
   {
      this.portlet = portlet;
      return portlet;
   }


   void setMinTitle( String title )
   {
      mintitle = title;
   }


   String getMinTitle()
   {
      return mintitle;
   }


   void setMaxTitle( String title )
   {
      maxtitle = title;
   }


   String getMaxTitle()
   {
      return maxtitle;
   }


   void setCanCustomize( boolean cancust )
   {
      this.cancustom = cancust;
   }


   boolean canCustomize()
   {
      return cancustom;
   }

   void setCanZoomInURL(String canzoom)
   {
      this.canzoomin = canzoom;
   }

   String getCanZoomInURL()
   {
      return canzoomin;
   }

   AutoString getHTML()
   {
      return html;
   }


   AutoString getClientScript()
   {
      return clscript;
   }


   AutoString getDirtyScript()
   {
      return drscript;
   }


   AutoString getPopupScript()
   {
      return popupscr;
   }


   AutoString getPopupDefinitions()
   {
      return popupdef;
   }


   void setUserAvailable( boolean user_available )
   {
      this.user_available = user_available;
   }


   boolean isUserAvailable()
   {
      if(DEBUG) debug(this+": ASPPortletSessionHandle.isUserAvailable(): "+this.user_available);
      return this.user_available;
   }


   //==========================================================================
   //  Implementation of the Serializable interface
   //==========================================================================

   /**
    * Save the state of the class to a stream (i.e., serialize it).
    */
   private synchronized void writeObject( ObjectOutputStream out ) throws IOException
   {
      out.writeObject(mintitle);
      out.writeObject(maxtitle);
      out.writeBoolean(cancustom);
      out.writeObject(canzoomin);

      out.writeObject( html.toString() );
      out.writeObject( clscript.toString() );
      out.writeObject( drscript.toString() );
      out.writeObject( popupscr.toString() );
      out.writeObject( popupdef.toString() );

      out.writeBoolean(user_available);
   }


   /**
    * Reconstitute the class from a stream (i.e., deserialize it).
    */
   private synchronized void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException
   {
      // constructor is not called. Creating transient instances
      html     = new AutoString();
      clscript = new AutoString();
      drscript = new AutoString();
      popupscr = new AutoString();
      popupdef = new AutoString();

      mintitle  = (String)(in.readObject());
      maxtitle  = (String)(in.readObject());
      cancustom = in.readBoolean();
      canzoomin = (String)(in.readObject());

      html.append(     (String)(in.readObject()) );
      clscript.append( (String)(in.readObject()) );
      drscript.append( (String)(in.readObject()) );
      popupscr.append( (String)(in.readObject()) );
      popupdef.append( (String)(in.readObject()) );

      user_available = in.readBoolean();

      portlet = null;
   }

   //==========================================================================
   //  Debugging
   //==========================================================================

   /**
    * Debug printout to the DBMON console.
    */
   private void debug( String text )
   {
      Util.debug(text);
   }
}
