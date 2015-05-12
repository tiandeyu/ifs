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
* File        : ExportNavigator.java
* Description : Interface for exporting the navigator folder & items to a bookmarks file.
* Notes       :
* ----------------------------------------------------------------------------
* Modified    :
*    Stefan M  2000-Oct-19 - Created
*    ASP2JAVA Tool  2001-02-28  - Created Using the ASP file ExportNavigator.asp
*    Chaminda O     2001-02-28  - Modifications after converting to java
*    Ramila H       2001-06-11  - corrected bug # 755. 
*    Suneth M       2001-Sep-12 - Changed duplicate localization tags.
*    Rifki R        2002-Nov-05 - Fixed translation problems. Log#905
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ExportNavigator extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ExportNavigator");

   //===============================================================
   // Construction
   //===============================================================
   public ExportNavigator(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      ExportNavigator page = (ExportNavigator)(super.clone(obj));
      return page;
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESEXPORTNAVIGATORTITLE: Export Tree Navigator";
   }

   protected String getTitle()
   {
      return "FNDPAGESEXPORTNAVIGATORWINDOWTITLE: Export Tree Navigator";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      String loc = "";

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDPAGESEXPORTNAVIGATORWINDOWTITLE: Export Tree Navigator"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation("FNDPAGESEXPORTNAVIGATORTITLE: Export Tree Navigator"));
      out.append("<CENTER><TABLE WIDTH=400><TR><TD>\n");
      out.append("<font SIZE=2 FACE=Arial>\n");
      if(mgr.isExplorer())
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTNAVFAV: The contents of the Tree Navigator can be exported to your browser's favorites by following these steps."));
      else
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTNAVBOK: The contents of the Tree Navigator can be exported to your browser's bookmarks by following these steps."));
      out.append("<UL>\n");
      out.append("<LI>\n");
      if(mgr.isExplorer())
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTLNKFAV: Press the link below. A save dialog will appear, asking you where the favorites file should be saved. This file can then be imported into your browser."));
      else
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTLNKBOK: Press the link below. A save dialog will appear, asking you where the bookmarks file should be saved. This file can then be imported into your browser."));
      out.append("<P>\n");
      if(mgr.isExplorer())
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTEXPLORER1: Note: When importing, you must choose which folder the imported favorites should end up in. This folder must exist before importing."));
      out.append("</LI>\n");
      out.append("<P>\n");
      out.append("<LI>\n");
      if(mgr.isExplorer())
      {
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTEXPLORER2: To import the saved favorites file, choose the menu item 'Import/Export' in the 'File' menu, and from there import from a file. You will then be asked for the location of the file. If you saved the file with any other file extension than '.html', you have to choose 'All Files' from the list of file types shown in this dialog.<p>"));
      }
      else
      {
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTNETSCAPE1: To import the saved bookmarks file, choose the menu item 'Communicator/Bookmarks/Edit Bookmarks', or the equivalent in your browser. From here, choose 'Import' from the 'File' menu. You will then be asked for the location of the bookmarks file."));
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTNETSCAPE2: The bookmarks will then be imported into a new folder of its own."));
      }
      out.append("</LI>\n");
      out.append("</UL>\n");
      out.append("<P>\n");
      out.append("<CENTER>\n");
      out.append("<a href=\"");
      loc = mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR");
      loc = loc.substring(0,loc.indexOf("?"));
      out.append(loc + "?MAINMENU=BOOKMARK");
      out.append("\">\n");
      if(mgr.isExplorer())
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTLINKTOFAV: Click here to export to favorites"));
      else
         out.append(mgr.translate("FNDPAGESEXPORTNAVIGATORFNDEXPORTLINKTOBOK: Click here to export to bookmarks"));
      out.append("</a>\n");
      out.append("</CENTER>\n");
      out.append("</font>\n");
      out.append("</tr></td></table>\n");
      out.append("</center>\n");
      out.append(mgr.endPresentation());
      out.append("</FORM>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
