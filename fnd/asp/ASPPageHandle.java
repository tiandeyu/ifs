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
 * File        : ASPPageHandle.java
 * Description : Element in the pool.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Feb-18 - Created.
 *    Jacek P  2004-Jan-04 - Bug#40931. Better debugging possibilities of Page Pool
 *    Rifki R  2004-Jun-23 - Changes for pool cleaner. Modified lock() to reset 
 *                           last_access timestamp and added getLastAccess(). 
 *    Rifki R  2004-Jul-26 - Improvements to page pool cleaner. 
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;

/**
 * Implements an element in linked list in the pool. Each handle points out
 * one page, points out the next handle in the list and keeps track of the
 * instance of ASPManager which has been locked the handle.
 *
 * @see ifs.fnd.asp.ASPPagePool
 */
class ASPPageHandle
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPageHandle");

   private ASPPageHandle next;
   private ASPPageHandle prev;
   private ASPPage       page;          // not null
   private ASPManager    owner;
   private long last_access;   //time this handle was last used (locked)   
   private boolean valid; //indictaes if this handle was removed by the cleaner
   
   //Bug 40931, start
   // other fields for debugging purpose
   int     thread_id;
   ASPPage top_page;
   //Bug 40931, end

   
   
   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Constructor
    */
   ASPPageHandle( ASPPage page )
   {
      if(DEBUG) Util.debug("ASPPageHandle.constructing handle for: "+page);
      this.page = page;
      this.valid = true;
      this.prev = null;
      this.next = null;
   }
   

   //==========================================================================
   //  Package synchronized interface
   //==========================================================================

   /**
    * Lock the handle by an instance of ASPManager.
    */
   //Bug 40931, start
   //synchronized void lock( ASPManager mgr )
   synchronized void lock( ASPManager mgr, ASPPage top_page )
   //Bug 40931, end
   {
      if(DEBUG) Util.debug("ASPPageHandle.lock("+mgr+")");
      owner = mgr;
      page.setASPManager(mgr);
   
      //Bug 40931, start
      // debugging
      this.thread_id = Thread.currentThread().hashCode();
      this.top_page  = top_page;
      //Bug 40931, end
      this.last_access=System.currentTimeMillis(); //reset last_access everytime a handle is used/locked
   }
   
   /**
    * Release the handle.
    */
   synchronized void unlock()
   {
      if(DEBUG) Util.debug("ASPPageHandle.unlock()");
      page.setASPManager(null);
      owner = null;
      
      //Bug 40931, start
      // debugging
      this.thread_id = 0;
      this.top_page  = null;
      //Bug 40931, end
   }
   
   /**
    * Return true if the handle is already locked.
    */
   synchronized boolean isLocked()
   {
      return owner!=null;
   }
   
   /**
    * Set the next handle in the list.
    */
   synchronized void setNext( ASPPageHandle next )
   {
      this.next = next;
   }
      
   /**
    * Return the next handle in the list.
    */
   synchronized ASPPageHandle getNext()
   {
      return next;
   }
   
    /**
    * Set the previous handle in the list.
    */
   synchronized void setPrev( ASPPageHandle prev )
   {
      this.prev = prev;
   }
      
   /**
    * Return the previous handle in the list.
    */
   synchronized ASPPageHandle getPrev()
   {
      return prev;
   }
      
   /**
    * Return the page.
    */
   synchronized ASPPage getASPPage()
   {
      return page;
   }

   /**
    * Return the owner which is locking the handle (if any).
    */
   synchronized ASPManager getOwner()
   {
      return owner;
   }
   
   /*
    * Return the time when this handle was last used/locked
    */
   
   synchronized long getLastAccess()
   {
       return last_access;
   }
   
   /*
    * Return the true if this handle has not been removed by the pool cleaner
    */   
   synchronized boolean isValid()
   {
     return valid;
   }
   
   /*
    * invalidates this handle. Used by the pool cleaner to indicate removal from the cluster it belongs
    */   
   synchronized void invalidate()
   {
     this.valid=false;
   }
}