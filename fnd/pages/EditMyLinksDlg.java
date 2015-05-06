
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
 * File        : EditMyLinksDlg.java
 * Description : Edit my-links saved in the userProfile
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Sampath  2003-05-21 - Created
 *    ChandanaD2004-05-12 - Updated for the use of new style sheets.
 * ----------------------------------------------------------------------------
 * 2010/07/01 buhilk Bug 91498, Changed run() printContents() to enable csv parameter decode option
 * 2008/01/29 sadhlk Bug 70439, Modified run(), printContents() to avoid decoding the url.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/23  buhilk
 * Bug 63032, Modifed run() method to show errors for illegal characters in link descriptions.
 *
 * 2006/09/11 buhilk
 * Bug 59842, Modifed run() method to read csv values and encode url with csv values
 *
 * 2006/08/08 buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 * 2006/02/01 prralk
 * Merged the corrections for LCS bug 55208, url with % in querry string
 *
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

public class EditMyLinksDlg extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.EditMyLinksDlg");
   
     
   private boolean editmode;
   private boolean submitUrlmode;
   private String  correctedUrl;
   private String  desc;
   private String  csvdecode;
   
   //===============================================================
   // Construction
   //===============================================================
   
   /** Creates a new instance of EditMyLinksDlg */
   public EditMyLinksDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   protected String getDescription()
   {
      return "FNDEDITMYLINKSDESC: Edit My Links";
   }
   
   protected String getTitle()
   {
      return "FNDEDITMYLINKSTITLE: Edit My Links";
   }
   
   
   public void  preDefine()
   {
      disableNavigate();
      disableValidation();
      disableHomeIcon();
      disableOptions();
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      if("EDITMYLINKS".equals(mgr.getQueryStringValue("__MYLINKCALLTYPE"))){
        editmode=true;
      }else{
        editmode = false;
      }
      
      if("Y".equalsIgnoreCase((mgr.readValue("SUBMIT_FORM__")))){
        submitUrlmode=true;
        
        if("EDITMYLINKS".equalsIgnoreCase((mgr.readValue("__MYLINKCALLTYPE")))){
         editmode=true;
      }
        if(!Str.isEmpty(mgr.readAbsoluteValue("LINK_URL__"))){
            correctedUrl = mgr.encodedURLWithCSV(mgr.readAbsoluteValue("LINK_URL__"));// breakAndEncode(mgr.encodedURLWithCSV(mgr.readAbsoluteValue("LINK_URL__")));
        }

        csvdecode = mgr.readValue("LINK_CSV__","NULL");

        if(!Str.isEmpty(mgr.readValue("LINK_DESCRIPTION__"))){
            desc = mgr.readValue("LINK_DESCRIPTION__");
            if ( desc.indexOf("/")>-1 )
            {
               mgr.showAlert(mgr.translate("FNDMODIFYMYLINKSBARINVALIDCHARACTER: The character, &1 is not allowed within the description", "/"));
               submitUrlmode = false;
            }
            else if (desc.indexOf("^")>-1 )
            {
               mgr.showAlert(mgr.translate("FNDMODIFYMYLINKSBARINVALIDCHARACTER: The character, &1 is not allowed within the description", "^"));
               submitUrlmode = false;
            }
        }
      }
   }
   
   protected String breakAndEncode(String text){
       ASPManager mgr = getASPManager();
      
       if ( text ==null || text.length()==0 ) return null;
       
       int firstIndex = text.indexOf("?");
             
       if(firstIndex > -1){
           String finalUrl = text.substring(0,firstIndex+1);
           String restOfUrl = text.substring(firstIndex+1);
            
           java.util.StringTokenizer tok = new java.util.StringTokenizer(restOfUrl, "&", false);
            
           int currentCount = 0;
           int numberOfTok = tok.countTokens();
           while ( tok.hasMoreTokens() ){
               currentCount++;
               String pair = tok.nextToken();
               int pos = pair.indexOf('=');
               if ( pos != -1 ){
                   String value = mgr.URLEncode(pair.substring(pos+1, pair.length()));
                   finalUrl += pair.substring(0, pos+1)+value;
                   if( currentCount < numberOfTok)
                       finalUrl +="&";
               }else
                   finalUrl += pair;
           }
           return finalUrl;
        }else{
           return text;
      }
   }
   
   protected String breakAndDecode(String text){
       
       ASPManager mgr = getASPManager();
      
       if ( text ==null || text.length()==0 ) return null;
       
       int firstIndex = text.indexOf("?");
             
       if(firstIndex > -1){
           String finalUrl = text.substring(0,firstIndex+1);
           String restOfUrl = text.substring(firstIndex+1);
            
           java.util.StringTokenizer tok = new java.util.StringTokenizer(restOfUrl, "&", false);
            
           int currentCount = 0;
           int numberOfTok = tok.countTokens();
           while ( tok.hasMoreTokens() ){
               currentCount++;
               String pair = tok.nextToken();
               int pos = pair.indexOf('=');
               if ( pos != -1 ){
                   String value = mgr.URLDecode(pair.substring(pos+1, pair.length()));
                   finalUrl += pair.substring(0, pos+1)+value;
                   if( currentCount < numberOfTok)
                       finalUrl +="&";
               }else
                   finalUrl += pair;
           }
           return finalUrl;
       }else{
          return text;
       }
   }
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean is_explorer = mgr.isExplorer();
      String selectedlinkno = mgr.URLDecode(mgr.getQueryStringValue("__MYLINKNO"));
      if(ifs.fnd.util.Str.isEmpty(selectedlinkno))
          selectedlinkno = mgr.URLDecode(mgr.readValue("__MYLINKNO"));
      
      String myportletid = mgr.URLDecode(mgr.getQueryStringValue("__MYPORTLETID"));
      if(ifs.fnd.util.Str.isEmpty(myportletid))
          myportletid = mgr.URLDecode(mgr.readValue("__MYPORTLETID"));
      
      beginDataPresentation();
      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageCommandBar>\n");
      appendToHTML("<tbody>\n"); 
      appendToHTML("<tr>\n");
      appendToHTML("<td nowrap height=22>&nbsp;\n");
      if(editmode)
         appendToHTML(mgr.translate("FNDMODIFYMYLINKSBARMSGMODIFY: Modify My Links")+ "&nbsp;</td>\n");
      else
         appendToHTML(mgr.translate("FNDMODIFYMYLINKSBARMSGADDNEW: Add New Link")+ "&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</tbody>\n");
      appendToHTML("</table>\n");
      
      appendToHTML("<table width=100% border=0 cellpadding=0 cellspacing=0 class=pageFormWithBorder >\n");
      appendToHTML("<tr><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td><td>");
      
         beginTable("",true,true);
            beginTableBody();
               beginTableCell();
                  printSpaces(1);
               endTableCell();
            nextTableRow();
               beginTableCell();
                  printWriteLabel("FNDPAGESEDITMYLINKSURL: URL ");
               endTableCell();
               beginTableCell();
                  if(editmode)
                     printField("LINK_URL__", /*breakAndDecode((Str.isEmpty(correctedUrl))*/(Str.isEmpty(correctedUrl)? mgr.getQueryStringValue("__MYURL"): correctedUrl),"",60);
                     //printField("LINK_URL__", breakAndDecode(mgr.URLDecode(mgr.getQueryStringValue("__MYURL"))),""",60);
                  else   
                     printField("LINK_URL__",/*breakAndDecode((Str.isEmpty(correctedUrl))*/(Str.isEmpty(correctedUrl)? "": correctedUrl),"",60);
               endTableCell();
            nextTableRow();
               beginTableCell();
                  printWriteLabel("FNDPAGESEDITMYLINKSLINKDESC: Description ");
               endTableCell();
               beginTableCell();
                  if(editmode)
                     //printField("LINK_DESCRIPTION__",mgr.URLDecode(mgr.getQueryStringValue("__MYDESC")),"",60);
                     printField("LINK_DESCRIPTION__",(Str.isEmpty(desc))? mgr.getQueryStringValue("__MYDESC"): desc,"",60);
                  else   
                     printField("LINK_DESCRIPTION__",(Str.isEmpty(desc))? "": desc,"",60);
               
               endTableCell();
            nextTableRow();
               beginTableCell();
                  printSpaces(1);
               endTableCell();
               beginTableCell();
                  printCheckBox("FNDPAGESEDITMYLINKSLINKCSV: Decode CSV Parameters", "LINK_CSV__", "DECODE", (editmode && "DECODE".equals(mgr.getQueryStringValue("__MYCSVDECODE"))), "");
               endTableCell();
            nextTableRow();
               beginTableCell();
                  printSpaces(1);
               endTableCell();
            nextTableRow();
               beginTableCell(3,0,LEFT);
                  printText("FNDPAGESEDITMYLINKSCSVWARN: Note: Decoding CSV parameters might cause abnormal behaviours in certain links.");
               endTableCell();
            nextTableRow();
               beginTableCell();
                  printSpaces(1);
               endTableCell();
            nextTableRow();
               beginTableCell(0,0,LEFT,"",true);
                  String tag = " onclick=\"javascript:assignValue()\"" +
                     " class='button'";
                       
                  printButton("OK","FNDPAGESEDITMYLINKSOK: OK", tag);
                  appendToHTML("&nbsp;");
      
                  tag = " onclick=\"javascript:window.close();\""+
                        " class='button'";
                     
                   printButton("CANCEL","FNDPAGESEDITMYLINKSCANCEL: Cancel", tag);
               endTableCell();
             nextTableRow();
               beginTableCell();
                  printSpaces(1);
               endTableCell();
             endTableBody();
          endTable();
      
      appendToHTML("</td><td>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("</td></tr>");
      appendToHTML("</table>");
      endDataPresentation(false);      
   
      printHiddenField("SUBMIT_FORM__","");
      printHiddenField("__MYLINKCALLTYPE","");
      printHiddenField("__MYLINKNO","");
      printHiddenField("__MYPORTLETID","");
      
      appendDirtyJavaScript("function assignValue()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tif (f.LINK_URL__.value == \"\")\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\t\t ifsAlert(\"",mgr.translateJavaScript("FNDPAGESEDITMYLINKSENTERURL: Enter the URL"),"\"); \n");
      appendDirtyJavaScript("\t\t f.LINK_URL__.focus(); \n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("\telse\n");
      appendDirtyJavaScript("\t{\n");
      appendDirtyJavaScript("\tif (f.LINK_DESCRIPTION__.value == \"\")\n");
      appendDirtyJavaScript("\t\t{\n");
      appendDirtyJavaScript("\t\t\t f.LINK_DESCRIPTION__.value = f.LINK_URL__.value; \n");
      appendDirtyJavaScript("\t\t}\n");
      appendDirtyJavaScript("\t\t f.SUBMIT_FORM__.value = \"Y\";\n");
      if(editmode)
        appendDirtyJavaScript("\t\t f.__MYLINKCALLTYPE.value = \"EDITMYLINKS\";\n");
      
      appendDirtyJavaScript("\t\t f.__MYLINKNO.value = "+selectedlinkno+";\n");
      appendDirtyJavaScript("\t\t f.__MYPORTLETID.value = "+myportletid+";\n");
      
      appendDirtyJavaScript("submit();\n");
      appendDirtyJavaScript("\t}\n");
      appendDirtyJavaScript("}\n");
               
      appendDirtyJavaScript("function getBackToEditLink()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\t\t\t window.opener.EditMyLink(\""+correctedUrl+"\",\""+desc+"\",\""+csvdecode+"\","+selectedlinkno+","+myportletid+");\n");
      appendDirtyJavaScript("\t}\n");
      
      appendDirtyJavaScript("function getBackToNewLink()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\t\t\t window.opener.addNewMyLink(\""+correctedUrl+"\",\""+desc+"\",\""+csvdecode+"\","+myportletid+");\n");
      appendDirtyJavaScript("\t}\n");

      if (submitUrlmode){
          if(editmode)
              appendDirtyJavaScript("\t\t\t getBackToEditLink();\n");
      else
             appendDirtyJavaScript("\t\t\t getBackToNewLink();\n");
         
      appendDirtyJavaScript("\t\t\t window.close();\n");
   
      }
      
   }
   
} 
