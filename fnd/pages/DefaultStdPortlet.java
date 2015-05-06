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
 *  File        : DefaultStdPortlet.java
 *  Modified    :
 *    Jacek P  2004-Feb-02 - Created
 *    Ramila H 2004-10-18  - Implemented JSR168 support
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.buffer.*;


public class DefaultStdPortlet extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DefaultStdPortlet");


   //===============================================================
   // Construction
   //===============================================================

   public DefaultStdPortlet(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void define() throws FndException
   {
      if( !isDefined() )
      {
         disableBar();
         disableHeader();
         disableFooter();
      }
//      setSlimMode();
      getASPManager().createPortalPage();
   }

   //===============================================================
   //  HTML
   //===============================================================

   protected String getDescription()
   {
      return getASPPortal().getDescription();
   }

   protected String getTitle()
   {
      return getASPPortal().getDescription();
   }

   protected AutoString getContents() throws FndException
   {
      return getStdPortalContents();
   }
}
