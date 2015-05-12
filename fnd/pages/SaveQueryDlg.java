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
 * File        : SaveQueryDlg.java
 * Description : User profile for saved queries
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2002-12-10 - Created
 *    Chandana D2003-Jun-05 - Modified GUI.
 *    Sampath   2003-Jul-25 - Fixed bug of submitting the page when enter is 
 *                             pressed.
 *    Ramila H  2003-09-04 - Added javascript Trim call.
 *    Ramila H  2003-09-04 - Removed button width to avoid translation problems.
 *    Chandana D2004-05-12 - Updated for the use of new style sheets.
 * ----------------------------------------------------------------------------
 *
 * New Comments   :
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/08/08 buhilk Bug 59442, Corrected Translatins in Javascript
 *
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SaveQueryDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.SavaQueryDlg");
   
   private ASPBlockLayout lay;
   private ASPRowSet rowset;
   //===============================================================
   // Construction
   //===============================================================
   public SaveQueryDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected String getDescription()
   {
      return "FNDSAVEQRYDLGTITLE: Save Query";
   }

   protected String getTitle()
   {
      return "FNDSAVEQRYDLGTITLE: Save Query";
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPBlock blk = mgr.newASPBlock("MAIN");
      
      blk.addField("QUERY_NAME__")
      .setLabel("FNDSAVEQRYDLGQNAME: Query Name:")
      .setSize(30);
      
      rowset = blk.getASPRowSet();
      lay = blk.getASPBlockLayout();

      lay.showBottomLine(false);
      
      lay.setDefaultLayoutMode(lay.EDIT_LAYOUT);
      
      disableNavigate();
      disableValidation();
      disableHeader();
      disableFooter();
      disableHomeIcon();
      disableOptions();
   }

   
   public void run()
   {
      ASPManager mgr = getASPManager();
      if (rowset.countRows() == 0)
         rowset.addRow(mgr.newASPBuffer());
      
      rowset.setValue("QUERY_NAME__",mgr.getQueryStringValue("QUERY_NAME"));
   }
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean is_explorer = mgr.isExplorer();
      
      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate("FNDSAVEQRYDIALOGTITLE: Save Query Dialog"));
      appendToHTML("<table class=pageFormWithBorder width=100%><tr><td width=100%>&nbsp;</td></tr>");
      //endDataPresentation(false);
      
      appendToHTML("<tr><td width=100%>\n");
      
      appendToHTML(lay.generateDialog());
      
      appendToHTML("</td></tr>\n");
      appendToHTML("<tr><td width=100% align=right>\n");
            
      String tag = " onclick=\"javascript:assignValue()\"" +
                   " class=\"button\"";
      
      //beginDataPresentation();                   
      printButton("OK","FNDSAVEQRYDLGOK: OK", tag);
      appendToHTML("&nbsp;");
      
      tag = " onclick=\"javascript:window.close();\""+
            " class=\"button\"";
      
      printButton("CANCEL","FNDSAVEQRYDLGCANCEL: Cancel", tag);
      appendToHTML("</td></tr>\n");
      appendToHTML("</table>\n");
      endDataPresentation(false);
      appendToHTML("<div style=\"position:relative; visibility:hidden\" >\n");
      appendToHTML("<input type=\"text\" size=\"10\" class='editableTextField'>\n");
      appendToHTML("</div>\n");
      appendDirtyJavaScript("function assignValue()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tquery_name = f.QUERY_NAME__.value = Trim(f.QUERY_NAME__.value);\n");
      appendDirtyJavaScript("\tif (query_name == '')\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\t\t alert('",mgr.translateJavaScript("FNDSAVEQRYDLGENTERNAME: Enter a name for the query"),"'); \n");
      appendDirtyJavaScript("\t\t f.QUERY_NAME__.focus(); \n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("\telse\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\t\t window.opener.saveQuery(query_name);\n");
      appendDirtyJavaScript("\t\t window.close();\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("}\n");
      
   }
}
