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
 * File        : ASPPageElement.java
 * Description : Super class for all ASP classes that can be stored in the pool.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-Feb-16 - Created.
 *    Jacek P  1999-Feb-17 - Changed interface.
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;


/**
 * Super class for all ASP classes that can be stored in the pool indirectly
 * as elements of an instance of ASPPage.
 */
public abstract class ASPPageElement extends ASPPoolElement
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPageElement");

   private transient ASPPage page;


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Protected constructor.
    */
   protected ASPPageElement( ASPPage page )
   {
      super(null);
      this.page = page;
      //if(ASPPagePool.VERIFY) page.allobjects.addElement(this);
   }


   //==========================================================================
   //  Common public interface
   //==========================================================================

   /**
    * Return reference to the container page.
    */
   public ASPPage getASPPage()
   {
      return page;
   }


   /**
    * Return reference to the current instance of ASPManager.
    */
   public ASPManager getASPManager()
   {
      return page==null ? null : page.getASPManager();
   }


   //==========================================================================
   //  Other routines
   //==========================================================================

   /**
    * Set an instance of ASPPage to be a container for the actual object.
    *
   void setASPPage( ASPPage page )
   {
      this.page  = page;
   }*/


   /**
    * Override method in ASPObject.
    *
   protected final ASPLog getLog()
   {
      if ( log==null && page!=null )
         log = page.getASPLog();
      return log;
   }*/

   /**
    * Override method in ASPPoolElement
    */
   protected ASPPoolElement getContainer()
   {
      return page;
   }
}