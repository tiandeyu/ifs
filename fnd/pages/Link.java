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
*  File        : Link.java 
*  Modified    :
*    ASP2JAVA Tool  2001-02-27  - Created Using the ASP file Link.asp
*    Chaminda O.     2001-02-27  - made the file compilable after conversion 
*                                               to Java
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Link extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Link");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPLog log;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery qry;
   private String helpbase;

   //===============================================================
   // Construction 
   //===============================================================
   public Link(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      qry   = null;
      helpbase   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      Link page = (Link)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.qry   = null;
      page.helpbase   = null;
      
      // Cloning immutable attributes
      page.log = page.getASPLog();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

   
      trans = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog();
   
      qry=trans.addQuery("MASTER", "SELECT VALUE FROM FND_SETTING WHERE PARAMETER = 'URL_APP_SUPPORT'");
      trans= mgr.perform(trans);
      helpbase = trans.getValue("MASTER/DATA/VALUE");
   
      mgr.redirectTo(helpbase);
  }


//===============================================================
//  HTML
//===============================================================
/*   
protected String getDescription()
   {
      return null;
   }

   protected String getTitle()
   {
      return null;
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<html>\n");
      appendToHTML("<head>\n");
      appendToHTML("<title></title>\n");
      appendToHTML("</head>\n");
      appendToHTML("<body>\n");
      appendToHTML("</body>\n");
      appendToHTML("</html>\n");
   }

*/
}
