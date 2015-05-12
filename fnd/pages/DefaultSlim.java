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
*  File        : DefaultSlim.java 
*  Modified    :
*    ASP2JAVA Tool  2001-06-07  - Created Using the ASP file DefaultSlim.asp
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DefaultSlim extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DefaultSlim");
   
   //===============================================================
   // Construction 
   //===============================================================
   
   public DefaultSlim(ASPManager mgr, String page_path)
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
      setSlimMode();
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

   protected void printContents() throws FndException
   {
      appendToHTML(getASPPortal().generateHTML());
   }

}
