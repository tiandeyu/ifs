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
 * File        : ASPPageSubElement.java
 * Description : 
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1999-Feb-16 - Created.
 *    Jacek P  1999-Mar-01 - Introduced 'container' conception.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;

/**
 *
 */
public abstract class ASPPageSubElement extends ASPPageElement
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPPageSubElement");

   private ASPPageElement container;


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Protected constructor.
    */
   protected ASPPageSubElement( ASPPageElement container )
   {
      super(container.getASPPage());
      this.container = container;
   }
   
   //==========================================================================
   //  Other routines
   //==========================================================================

   /**
    * Override method in ASPPoolElement
    */
   protected final ASPPoolElement getContainer()
   {
      return container;
   }
}