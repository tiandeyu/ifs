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
*  File        : cromfwNewWindowOpener.java
* ---------------------Wings Merge Start-------------------------------------------
*  Created     : RuRalk 2007-01-17
*                Haunlk 2007-01-31 Merged Wings Code.
* ---------------------Wings Merge End-------------------------------------------
*		 Shwilk 2007-07-20 Done web security corrections.
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class EnterwNewWindowOpener extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.enterwNewWindowOpener");


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private int width;
   private int height;
   private int left;
   private int top;
   private String sFileName;

   //===============================================================
   // Construction
   //===============================================================
   public EnterwNewWindowOpener(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();



      width   = 765;
      height =        450;
      left = 100;
      top =   100;

      sFileName = mgr.readValue("NEWURL")+".page";


      if ( "CreateCompanyWizard".equals(mgr.readValue("NEWURL")) )
      {
         sFileName = sFileName+"?WINDNAV=1";

         if ( !mgr.isExplorer() )
         {
            width   = 1050;
            height = 450;
            left = 40;
            top = 150;
         }

      }
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "Dummy Window Opener";
   }

   protected String getTitle()
   {
      return "Dummy Window Opener";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<html>\n");
      appendToHTML("<head>\n");
      appendToHTML("<title>Dummy Window Opener</title>\n");
      appendToHTML("</head>\n");
      appendToHTML("<body>\n");
      appendToHTML("<form>\n");
      appendToHTML("</form>\n");
      appendToHTML("</body>\n");
      appendToHTML("</html>\n");
      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      appendDirtyJavaScript("   if (history.length == 0)\n");
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("      history.back();\n");

      appendDirtyJavaScript("   window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sFileName));
      appendDirtyJavaScript("\",\"WIZWIN\",\"status,resizable,scrollbars,width=");
      appendDirtyJavaScript(width);
      appendDirtyJavaScript(",height=");
      appendDirtyJavaScript(height);
      appendDirtyJavaScript(",left=");
      appendDirtyJavaScript(left);
      appendDirtyJavaScript(",top=");
      appendDirtyJavaScript(top);
      appendDirtyJavaScript("\");\n");
   }

}
