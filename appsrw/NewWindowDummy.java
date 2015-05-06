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
*  File        : NewWindowDummy.java 
*  ASP2JAVA Tool  2001-03-20  - Created Using the ASP file NewWindowDummy.asp
*  Modified    :  2001-03-20  BUNI Corrected some conversion errors.
*                 2001-08-21  CHCR Removed depreciated methods.
* ----------------------------------------------------------------------------
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class NewWindowDummy extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.NewWindowDummy");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private String url;
   private String wndFlag;

   //===============================================================
   // Construction 
   //===============================================================
   public NewWindowDummy(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      url   = null;
      wndFlag   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      NewWindowDummy page = (NewWindowDummy)(super.clone(obj));

      // Initializing mutable attributes
      page.url   = null;
      page.wndFlag   = null;
      
      // Cloning immutable attributes

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
  
      url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
   
      wndFlag = "";

      if  (  "BestFitSearchWiz.page".equals(mgr.getQueryStringValue("WNDFLAG")) ) 
       wndFlag = "BestFitSearchWiz.page";
   }


//===============================================================
//  HTML
//===============================================================
   

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<html>\n");
      appendToHTML("<head>\n");
      appendToHTML("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
      appendToHTML("<title>New Page </title>\n");
      appendToHTML("</head>\n");
      appendToHTML("<body>\n");
      appendToHTML("</body>\n");
      appendToHTML("</html>\n");
      appendDirtyJavaScript("window.location = '");
      appendDirtyJavaScript(url);
      appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n");
      appendDirtyJavaScript("switch ('");
      appendDirtyJavaScript(wndFlag);
      appendDirtyJavaScript("')\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("    case \"BestFitSearchWiz.page\" :  window.open('");
      appendDirtyJavaScript(url);
      appendDirtyJavaScript("'+\"appsrw/BestFitSearchWiz.page\",\"frmTechnicalSearchWiz\",\"resizable=yes,scrollbars=yes,status=yes,menubar=no,height=517,width=790\");\n");
      appendDirtyJavaScript("                                        break;   \n");
      appendDirtyJavaScript("}\n");
   }

}
