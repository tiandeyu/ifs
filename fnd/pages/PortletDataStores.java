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
*  File        : PortletDataStores.java
*  Modified    :
*   Chandana D - 2002-08-28  - Created
*   Suneth M   - 2004-Aug-04 - Changed duplicate localization tags.
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class PortletDataStores extends ASPPageProvider {

   public PortletDataStores(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      disableHomeIcon();
      disableNavigate();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESPORTDATASTOREDES: Portlet Data-Stores";
   }

   protected String getTitle()
   {
      return "FNDPAGESPORTDATASTORETITLE: Portlet Data-Stores - "+getASPManager().getQueryStringValue("VIEW_DESC");
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      int col_count= 0;
      String available_names = mgr.getConfigParameter("ADMIN/FNDAS/SYSTEM_NAME")+","+mgr.getConfigParameter("ADMIN/FNDAS/AVAILABLE_DATA_STORES");
      String portlet_qrystr = mgr.getQueryStringValue("PORTLET_NAMES");
      String datastore_qrystr = mgr.getQueryStringValue("DATA_STORES");
      String imgloc = mgr.getASPConfig().getImagesLocation();
      String portlet_col="";
      String datastore_col="";
      StringTokenizer sys_names = null;
      StringTokenizer portlet_cols = new StringTokenizer(portlet_qrystr,(char)31+"");
      StringTokenizer portlet_rows = null;
      StringTokenizer datastore_cols = new StringTokenizer(datastore_qrystr,(char)31+"");
      StringTokenizer datastore_rows = null;

      appendToHTML("<table width=500 border=0 cellpadding=0 cellspacing=0>\n");
      appendToHTML("<tbody>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td width=1 height=5 colspan=3><img width=1 height=5 src=\""+imgloc+"empty.gif\"></td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td width=727 height=1 colspan=3 bgcolor=#666666><img width=1 height=1 src=\""+imgloc+"empty.gif\"></td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td width=1   height=19 bgcolor=#666666><img width=1 height=1 src=\""+imgloc+"empty.gif\"></td>\n");
      appendToHTML("<td width=725 height=19 bgcolor=#CCCCCC>\n");
      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=CmdBar>\n");
      appendToHTML("<tbody>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td nowrap height=19>&nbsp;<span class=cmdBarTitle>\n");
      appendToHTML(mgr.translate("FNDPROTETDATASTORESTITLE: Protlet Data-Stores Configuration")+"</span>&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
      appendToHTML("</td>\n");
      appendToHTML("<td width=1 height=19 bgcolor=#666666><img width=1 height=1 src=\""+imgloc+"empty.gif\"></td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td width=727 height=1 colspan=3 bgcolor=#66666><img width=1 height=1 src=\""+imgloc+"empty.gif\"></td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
     
      appendToHTML("<table border=0 cellpadding=0 cellspacing=0 bgcolor=green width=500 class=BlockLayoutTable >\n");
      while (portlet_cols.hasMoreTokens())
      {
         col_count++;
         appendToHTML("<tr>\n   <td height=15>&nbsp;</td>\n   <td height=15>&nbsp;</td>\n</tr>\n");
         portlet_col = portlet_cols.nextToken();
         portlet_rows = new StringTokenizer(portlet_col,(char)30+"");
         String portlet_col_no = portlet_rows.nextToken();
         datastore_col = datastore_cols.nextToken();
         datastore_rows = new StringTokenizer(datastore_col,(char)30+"");
         String datastore_col_no = datastore_rows.nextToken();
         Vector list = new Vector();
         appendToHTML("<tr>\n   <td style=\"FONT: 8pt Verdana\"><b>"+mgr.translate("FNDPORTLETDATASTORECOLUMN: Column: ")+portlet_col_no+"</b>   </td>\n");
         appendToHTML("   <td height=15>&nbsp;</td>\n</tr>\n");
         while (portlet_rows.hasMoreTokens()) {
            sys_names = new StringTokenizer(available_names,",");
            appendToHTML("<tr>\n   <td style=\"FONT: 8pt Verdana\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+portlet_rows.nextToken()+"\n   </td>\n");
            appendToHTML("   <td style=\"FONT: 8pt Verdana\">\n      <select name=\"col_"+col_count+"\" style=\"FONT-FAMILY: Verdana, Geneva, sans-serif; FONT-SIZE: 10px\">\n");
            String token=datastore_rows.nextToken();
            appendToHTML("        <option value=\""+token+"\">"+token+"</option>\n");
            list.clear();
            while (sys_names.hasMoreTokens()) {
               String sys_name=sys_names.nextToken();
               if (!token.equals(sys_name) && list.indexOf(sys_name)==-1){
                  appendToHTML("        <option value=\""+sys_name+"\">"+sys_name+"</option>\n");
                  list.addElement(sys_name);
               }
            }
            appendToHTML("      </select>\n   </td>\n</tr>\n");
         }
      }
      appendToHTML("<script language=javascript>\n");
      appendToHTML("function acceptValues(){\n");
      appendToHTML("col_count=1;\n");
      appendToHTML("data_stores='';\n");
      appendToHTML("elem = 'col_'+col_count;\n");
      appendToHTML("elm=eval('document.form.'+elem);\n");
      appendToHTML("while(elm){\n");
      appendToHTML("data_stores+=col_count+String.fromCharCode(30);\n");
      appendToHTML("for(p=0;p<elm.length;p++){\n");
      appendToHTML("if(elm.name){\n");
      appendToHTML("data_stores+=elm[elm.selectedIndex].value+String.fromCharCode(30);\n");
      appendToHTML("break;\n");
      appendToHTML("}\n");
      appendToHTML("else\n");
      appendToHTML("e=elm[p];\n");
      appendToHTML("data_stores+=e[e.selectedIndex].value+String.fromCharCode(30);\n");
      appendToHTML("}\n");
      appendToHTML("col_count++;\n");
      appendToHTML("elem = 'col_'+col_count;\n");
      appendToHTML("elm=eval('document.form.'+elem);\n");
      appendToHTML("data_stores+=String.fromCharCode(31);\n");
      appendToHTML("}\n");
      appendToHTML("window.opener.document.form.DATA_STORES.value=data_stores;\n");
      appendToHTML("window.close();\n");
      appendToHTML("}\n");
      appendToHTML("</script>\n");

      appendToHTML("<tr>\n   <td height=15>&nbsp;</td>\n   <td height=15>&nbsp;</td>\n</tr>\n");
      appendToHTML("<tr>\n   <td nowrap>\n <table><tr><td> <input name=OK type=button value=\""+mgr.translate("FNDDATASTORESOK: OK")+"\" onclick=\"javascript:acceptValues()\" style=\"FONT-FAMILY: Verdana; FONT-SIZE: 10px; FONT-WEIGHT: bold; WIDTH: 80px; TOP: 196px\"></td>");
      appendToHTML("      <td><input name=CANCEL type=button onclick=\"javascript:window.close()\" value=\""+mgr.translate("FNDDATASTORESCANCEL: Cancel")+"\" style=\"FONT-FAMILY: Verdana; FONT-SIZE: 10px; FONT-WEIGHT: bold; WIDTH: 80px; LEFT: 57px; TOP: 196px\"></td></tr></table></td> \n   <td height=15>&nbsp;</td>\n</tr>\n");
      appendToHTML("<tr>\n   <td height=15>&nbsp;</td>\n   <td height=15>&nbsp;</td>\n</tr>\n");
      appendToHTML("</table>\n");
   }
}
