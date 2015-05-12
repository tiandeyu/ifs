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
 * File        : PrintImageDlg.java
 * Description : Print dialog for printong Tiff images.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Rifki R  2004-10-15 - Created
 * ----------------------------------------------------------------------------
 *
 * New Comments:
 * 2008/08/01 rahelk Bug id 74809, changed according to new structure
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/08/10 buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 * buhilk 5-July-2006 11:47:00 AM
 * Bug Id: 58214, Fixed the XSS vulnarabilities
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PrintImageDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.PrintImageDlg");
   
   private ASPBlockLayout lay;
   private ASPRowSet rowset;
   private ASPHTMLFormatter fmt;
   
   //===============================================================
   // Construction
   //===============================================================
   public PrintImageDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected String getDescription()
   {
      return "FNDPAGESPRINTIMAGEDLGDESC: Print Image";
   }

   protected String getTitle()
   {
      return "FNDPAGESPRINTIMAGEDLGTITLE: Print Image";
   }

   public void  preDefine()
   {
       ASPManager mgr = getASPManager();
       ASPBlock blk = mgr.newASPBlock("MAIN");               
             
       blk.addField("PRINT_RANGE__");          
       blk.addField("PRINT_FROM__");           
       blk.addField("PRINT_TO__");      
       blk.addField("CURRENT_PAGE__")
       .setHidden();       
       blk.addField("LAST_PAGE__")
      .setHidden();
      
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
      fmt = mgr.newASPHTMLFormatter();       
   }
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
            
      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate("FNDPRINTIMGDIALOGTITLE: Print Image Dialog"));
      appendToHTML("<table class=pageFormWithBorder width=100%><tr><td width=100%>&nbsp;</td></tr>");      
      appendToHTML("<tr><td width=100%>\n");                       
      appendToHTML("<tr><td>");      
      appendToHTML(fmt.drawRadio("FNDPRINTALL: All","PRINT_RANGE__","ALL",true,null));
      appendToHTML("</td></tr>"); 
      appendToHTML("<tr><td>"); 
      appendToHTML(fmt.drawRadio("FNDPRINTCURR: Current","PRINT_RANGE__","CURRENT",false,null));       
      appendToHTML("</td></tr>"); 
      appendToHTML("<tr><td>"); 
      appendToHTML(fmt.drawRadio("FNDPRINTRANGE: Pages","PRINT_RANGE__","RANGE",false,null));     
      appendToHTML("&nbsp;&nbsp;");
      String tag = " onfocus=\"javascript:f.PRINT_RANGE__[2].checked=true;\""; 
      appendToHTML(fmt.drawReadLabel("FNDPRINGIMGFROM: from:"));
      appendToHTML("&nbsp;");            
      appendToHTML(fmt.drawTextField("PRINT_FROM__","1",tag,5));       
      appendToHTML(fmt.drawReadLabel("FNDPRINGIMGTO: to:")); 
      appendToHTML("&nbsp;");
      printField("PRINT_TO__",mgr.readValue("__LAST_PAGE"),tag,5);   
      appendToHTML("</td>");  
      appendToHTML("</tr>");             
      appendToHTML("</td></tr>\n");
      appendToHTML("<tr><td width=100% align=right>\n");
            
      tag = " onclick=\"javascript:returnValues()\"" +
                   " class=\"button\"";      
      
      printButton("OK","FNDPRINTIMGDLGOK: OK", tag);
      appendToHTML("&nbsp;");
      
      tag = " onclick=\"javascript:window.close();\""+
            " class=\"button\"";
      
      printButton("CANCEL","FNDPRINTIMGDLGCANCEL: Cancel", tag);
      appendToHTML("</td></tr>\n");
      appendToHTML("</table>\n");
      printHiddenField("LAST_PAGE__",mgr.readValue("__LAST_PAGE"));
      printHiddenField("CURRENT_PAGE__",mgr.readValue("__CURRENT_PAGE"));      
      endDataPresentation(false);      
      appendDirtyJavaScript("function isValidRange(from,to)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tif ( isNaN(from) || isNaN(to) )\n");
      appendDirtyJavaScript("\t\treturn false\n");
      appendDirtyJavaScript("\tif ( from>to )\n");
      appendDirtyJavaScript("\t\treturn false\n");      
      appendDirtyJavaScript("\tif ( from<1 )\n");
      appendDirtyJavaScript("\t\treturn false\n");      
      appendDirtyJavaScript("\tif ( to>f.LAST_PAGE__.value )\n");
      appendDirtyJavaScript("\t\treturn false\n"); 
      appendDirtyJavaScript("\treturn true\n"); 
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function returnValues()\n");
      appendDirtyJavaScript("{\n");      
      appendDirtyJavaScript(" var from,to,rad_val;");      
      appendDirtyJavaScript("for (var i=0; i < f.PRINT_RANGE__.length; i++)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (f.PRINT_RANGE__[i].checked)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("rad_val = f.PRINT_RANGE__[i].value;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");      
      appendDirtyJavaScript("\tif (rad_val == 'ALL')\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\tfrom=1;\n");
      appendDirtyJavaScript("\tto=f.LAST_PAGE__.value;\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("\telse if (rad_val == 'CURRENT')\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\tfrom=f.CURRENT_PAGE__.value;\n");
      appendDirtyJavaScript("\tto=f.CURRENT_PAGE__.value;\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("\telse\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\tfrom=f.PRINT_FROM__.value;\n");
      appendDirtyJavaScript("\tto=f.PRINT_TO__.value;\n");
      appendDirtyJavaScript("\t}\n"); 
      appendDirtyJavaScript("\tif (isValidRange(from,to))\n");
      appendDirtyJavaScript("\t{\n");      
      appendDirtyJavaScript("\t\t window.opener.printTiffImage(from,to,"+mgr.getQueryStringValue("STAMP")+");\n");
      appendDirtyJavaScript("\t\t window.close();\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("\telse\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\talert('"+mgr.translateJavaScript("FNDPAGESPRINTIMAGEDLGINVALIDRANGE: Invalid page range.")+"');");
      appendDirtyJavaScript("\treturn false;\n");      
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("}\n");      
   }
}
