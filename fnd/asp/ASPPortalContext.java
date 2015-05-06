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
 * File        : ASPPortalContext.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2000-Jan-17 - Created
 *    Jacek P  2000-Feb-21 - Changed name for ASPPortletProvider class.
 *    Jacek P  2000-Apr-17 - Minor changes.
 *    Jacek P  2000-Apr-18 - Function rewriteContext() throws exception if the
 *                           item already exists.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.util.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.io.*;
import java.util.*;


/**
 */
public class ASPPortalContext extends ASPContext
{
   //==========================================================================
   // Instance variables
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPortalContext");

   // immutable attributes
   private ASPPortletProvider provider;

   // mutable temporary attributes
   private transient ASPContext ctx;


   //==========================================================================
   // Constructors
   //==========================================================================

   /**
    * Package constructor. Calls constructor within the super class.
    */
   ASPPortalContext( ASPPortletProvider provider )
   {
      super(provider);
      if(DEBUG) debug(this+": ASPPortalContext.<init>: "+provider);
      this.provider = provider;
   }


   ASPContext construct() throws FndException
   {
      if (DEBUG) debug(this+": ASPPortalContext.construct()");

      this.ctx = provider.getParentContext();
      super.construct();
      return this;
   }


   /**
    * Package constructor called from clone()
    */
   ASPPortalContext( ASPPage page, ASPContext context )
   {
      super(page, context);
      if(DEBUG) debug(this+": ASPPortalContext.<init>: "+page+","+context);
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPoolElement#reset
    */
   protected void doReset() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalContext.doReset()");

      super.doReset();
      ctx = null;
   }


   /**
    * Initiate the instance for the current request.
    * Called by activate() in the super class after check of state.
    *
    * @see ifs.fnd.asp.ASPPoolElement#activate
    */
   protected void doActivate() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalContext.doActivate()");

      ctx        = provider.getParentContext();
      String cid = provider.getContextId();

      Item item = ctx.readItemReference(cid);
      if(item==null)
      {
         if(DEBUG) debug(this+": Creating new request buffer item for context '"+cid+"'.");
         req_buf = provider.getASPConfig().getFactory().getBuffer();
      }
      else
      {
         req_buf = (Buffer)(item.getValue());
         if(DEBUG) debug(this+": Found request buffer item for context '"+cid+"':\n"+Buffers.listToString(req_buf));
      }

      resp_buf = (Buffer)(ctx.getCompoundItemReference(cid).getValue());
      if(DEBUG) debug(this+": Response buffer item for context '"+cid+"':\n"+Buffers.listToString(resp_buf));

      rewrite( DB_STATE_BUF_NAME,      null,              false );
      rewrite( CURRENT_STATE_BUF_NAME, DB_STATE_BUF_NAME, false );
   }


   protected void rewriteContext() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalContext.rewriteContext()");

      for(int i=0; i<req_buf.countItems(); i++)
      {
         Item item = req_buf.getItem(i);
         Item existing_item = resp_buf.getItem( item.getName(), null );

         if ( existing_item==null )
            resp_buf.addItem( item );
         else
         {
            if(DEBUG) debug("  Response buffer:\n"+Buffers.listToString(resp_buf));
            throw new FndException("FNDPCTXITEX: Item '&1' already exists.",existing_item.getName());
         }
      }
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPoolElement#clone
    *
    */
   protected ASPPoolElement clone( Object page ) throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalContext.clone("+page+")");

      ASPPortalContext c = new ASPPortalContext((ASPPortletProvider)page, this);
      c.provider = (ASPPortletProvider)page;
      c.ctx = null;
      c.setCloned();
      return c;
   }


   //==========================================================================
   //
   //==========================================================================

   /**
    * Return reference to the container page.
    */
   public ASPPage getASPPage()
   {
      if(DEBUG) debug(this+": ASPPortalContext.getASPPage()");
      return ctx.getASPPage();
   }


   protected boolean isFrozen()
   {
      if(DEBUG) debug(this+": ASPPortalContext.isFrozen()");
      return ctx.isFrozen();
   }

   /**
    * Callback function that will be called from ASPManager just before
    * generating of client script. Adds a private HTML field named '__CONTEXT'.
    */
   void prepareClientScript() throws FndException,IOException
   {
      if(DEBUG) debug(this+": ASPPortalContext.prepareClientScript()");
   }


   /**
    * Put back the old context in the cache. Called on error from ASPLog
    * to preserve the old, already removed, context.
    */
   void rollbackContext() throws FndException
   {
      if(DEBUG) debug(this+": ASPPortalContext.rollbackContext()");
      ctx.rollbackContext();
   }
}
