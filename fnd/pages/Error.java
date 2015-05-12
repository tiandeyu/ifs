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
 *  File        : Error.java
 *  Modified    :
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.0  2005/12/07 rahelk
 * created
 *
 * ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Error extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Error");

   //===============================================================
   // Construction
   //===============================================================
   public Error(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      disableHelp();
      disableNavigate();
      disableOptions();
      disableHomeIcon();
      disableValidation();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESERROR: Logon Error";
   }

   protected String getTitle()
   {
      return "FNDPAGESERROR: Logon Error";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      beginDataPresentation();
      appendToHTML("<br>");
      printBoldText("FNDPAGESERRORMSG: Could not logon user.");
      appendToHTML("<br>");
      appendToHTML("<br>");
      printLink("FNDPAGESERRORBACK: Back",mgr.getASPConfig().getLogonURL());
      endDataPresentation(false);      
   }

}
