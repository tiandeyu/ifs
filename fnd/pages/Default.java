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
*  File        : Default.java
*  Modified    :
*    ASP2JAVA Tool  2001-02-16  - Created Using the ASP file default.asp
*    Artur K  2001-Mar-20 - Necessary changes for handling multiple portal pages.
*    Jacek P  2001-Apr-12 - Instance of ASPManager must not be saved.
*                           Some other changes. Removed unnecessary code and methods.
*    Artur K  2001-Jun-12 - Added call of enableConfiguration().
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import javax.servlet.http.HttpSession;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;


public class Default extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Default");


   //===============================================================
   // Construction
   //===============================================================

   public Default(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void define() throws FndException
   {
      if( !isDefined() ) enableConfiguration();
      getASPManager().createPortalPage();
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      HttpSession session = mgr.getAspSession();  
      String logon = (String)session.getAttribute("LOGON");
      if("1".equals(logon)){ 
          session.setAttribute("LOGON", "0");
          mgr.redirectTo("/b2e/secured/corpvw/IFSCorpvView.page");
          
      }       
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
