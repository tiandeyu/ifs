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
 * File        : ASPHTMLFormatter.java 1998-Jan-23 11:04
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Bobby I  1998-Jan-23 - Created
 *    Bobby I  1998-Mar-21 - Corrected populate2ColxxTable, bad behaviour when
 *                           a column contained null value
 *    Bobby I  1998-Mar-23 - Using Config.ifm to control HTML tags and file
 *                           locations
 *    Bobby I  1998-Mar-24 - Adding methods for message and error formatting
 *    Marek D  1998-May-22 - Declared the constructor and configuration
 *                           constants as protected.
 *                           Use ASPQuery.countRows() to count rows
 *    Micke A  1998-Jun-26 - Added handling of the images for ASPTable row status
 *    Marek D  1998-Jul-06 - Use Field-Separator instead of '^' in List Boxes
 *    Marek D  1998-Jul-08 - Added showCommandLink() method
 *    Micke A  1998-JuL-08 - Added the NORMAL image to the handling of the
 *                           images for ASPTable row status
 *    Marek D  1998-Jul-13 - Use URLEncode() while generating hyperlinks
 *    Jacek P  1998-Jul-15 - Added new images for record status.
 *    Jacek P  1998-Jul-21 - Call to native function OutputDebugString()
 *                           replaced with static function Util.debug()
 *    Jacek P  1998-Jul-23 - Error handling moved to FndException and ASPLog
 *                           classes. Better HTML formatting.
 *    Jacek P  1998-Aug-07 - Removed 'throw Exception' from public functions
 *    Jacek P  1998-Aug-10 - ASPQuery.countRows() replaced with ASPRowSet.countRows()
 *                           Added try..catch block to each public function
 *                           which can throw exception
 *    Jacek P  1998-Aug-19 - Redesigned structure of ASPConfig.ifm file.
 *                           All static variables removed (Log id:#2623).
 *    Marek D  1998-Aug-26 - Use mgr.preapareURL() for hyperlinks (Bug #2653)
 *    Jacek P  1998-Aug-28 - Generate NO-CACHE tag while generating an error page.
 *                           Bug #2657.
 *    Marek D  1998-Aug-31 - Generate VALUE attribute in populateEnumListBox()
 *                           Bug #2654
 *    Jacek P  1998-Sep-01 - Images for messages fetched from config file.
 *    Marek D  1998-Sep-21 - Added method showFavoriteLink()
 *    Marek D  1998-Sep-29 - #2712: Hide IIS error message in an error page
 *                           #2721:Added FNDERRMSG tag to error page
 *    Jacek P  1998-Oct-14 - Better formating of info page. Added 'back' button
 *                           (Todo: #2765).
 *    Jacek P  1998-Nov-19 - Added populateMandatoryListBox(), rewritten
 *                           populateListBox() (Todo: #2830).
 *    Jacek P  1998-Nov-20 - Function formatMsg() rewritten due to new error
 *                           handling (Todo: #2714,#2943).
 *    Marek D  1998-Nov-30 - Added method removeFromListBox() (ToDo #2835)
 *    Jacek P  1998-Dec-08 - Definition of META-tag fetched from ASPConfig.ifm.
 *    Jacek P  1998-Dec-09 - Generation of Alert Box depends on value in hidden
 *                           field __HEAD_TAG_GENERATED.
 *    Jacek P  1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P  1999-Feb-17 - Extends ASPPageElement instead of ASPObject
 *                           Debugging controlled by Registry.
 *    Jacek P  1999-Feb-19 - Implemented ASPPoolable.
 *    Jacek P  1999-Mar-01 - Interface ASPPoolable replaced with an abstarct
 *                           class ASPPoolElement.
 *    Jacek P  1999-Mar-15 - Added method construct().
 *    Marek D  1999-Mar-19 - Removed calls to getConfigParameter()
 *    Marek D  1999-Apr-27 - Added verify() and scan()
 *    Jacek P  1999-May-04 - Justification of code generation by formatMsg()
 *                           if page id is not set yet.
 *    Marek D  1999-May-11 - Added populateListBox(...,String[],...)
 *    Jacek P  1999-Jun-11 - Changed API of formatMsg() function. Added new
 *                           overloaded formatErrorMsg() with additional error
 *                           information.
 *    Jacek P  1999-Jun-18 - Added replacement of '"' in additional message in
 *                           function formatMsg().
 *    Jacek P  1999-Sep-08 - Added optional generation of additional error info
 *                           in formatMsg().
 *    Stefan M 2000-Mar-20 - Added functions for drawing standard HTML labels
 *                           and input fields.
 *    Jacek P  2000-Jul-26 - Log id 226 & 46: Error page for the portal shows
 *                           the link to config page.
 *    Jacek P  2001-Mar-29 - Changed .asp to .page
 *    Piotr Z  2001-Apr-25 - Scaling of input and textarea fields size for Netscape.
 *    Piotr Z  2001-May-23 - Changed the method formatMsg().
 *    Jacek P  2001-May-31 - Log id #742: Added call to HTMLDecode() while creating combo boxes.
 *    Jacek p  2002-Feb-19 - Added support for UTF-8 character set in formatMsg().
 *    ChandanaD2002-Mar-14 - Called getURLEncodeFunction()& getAppletTag() methods.
 *                           Removed escape() method calls as escaping is done in encodeUnicode() method.
 *    Sampath  2002-Oct-09 - Introduced BASE64_ENABLED variable to error pages
 *    ChandanaD2002-Oct-21 - Added CURRENT_USER to the HTML <head> section.
 *             2002-Oct-21 - Replaced all document.cookie statements with writeCookie() function.
 *    ChandanaD2002-Oct-21 - Disables the 'go to portal configuration' link in error page when configuration button is disabled.
 *    ChandanaD2002-Nov-07 - Added AUTH_COOKIE to the HTML <head> section.
 *    Johan S  2003-Mar-04 - Changed javascript writeCookie to writePageIDCookie
 *    Sampath  2003-Apr-17 - made changes in formatMsg() to enable 'more_info' in error pages 
 *    Ramila H 2003-10-20  - Added title HTML attribute for tooltips (NN6 doesnt show alt).
 *    ChandanaD2003-12-15  - Fixed Bug 40908.
 *    ChandanaD2004-May-13 - Updated for the use of new style sheets.
 *    ChandanaD2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Ramila H 2004-08-02  - fixed bug in drawReadOnlyTextField.
 *    Ramila H 2004-08-24  - used ASPManager method to get stylesheet tag (multi language support).
 * ---------------------------------------------------------------------------- 
 * New Comments:
 * 2010/02/03 sumelk Bug 88035, Changed formatMsg(). 
 * 2008/05/06 sumelk Bug 72938, Changed drawTextArea() to show the LOV icon correctly. 
 * 2008/04/21 buhilk Bug 72855, Modified populateHyperlinkTable().
 * 2007/12/12 sadhlk Bug id 67525, Modified formatMsg() to support cookie renaming.
 * 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
 * 2007/11/16 sadhlk Bug id 69140, Modified  formatMsg() to show error messges in multiple frame pages.
 * 2007/11/08 rahelk Bug id 68906, Increased ReAuthentication dlg to accommodate comments field.
 * 2007/11/01 sadhlk Merged Bug id 68311, Modified formatMsg().
 * 2007/07/10 buhilk Bug id 66585, Modified formatMsg() to handle error messages for GET requests
 * 2007/05/28 rahelk Merged Bug id 63254. 
 * 2007/05/09 mapelk Bug id 63254, Security checkpoint is improved.
 * 2007/03/01 buhilk Bug 63870, Modified drawLabel() and drawRadio() to improve "whats this" effects.
 * 2007/01/30 buhilk Bug 63250, Improved theming 
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/11/13           buhilk
 * Bug id 61819, Modified and merged "Whats this" functionality
 *
 * 2006/11/02 gegulk 
 * Modified the methods drawLabel() & drawRadio() to provide "Whats this" functionality 
 *
 * 2006/09/13 gegulk 
 * Bug id 60473, Modified the method formatMsg() to append the variable IS_RTL to the javascript
 *
 * 2006/09/11 gegulk 
 * Bug 60225 HTMLEncoded the variables message inside the method formatMsg()
 
 * 04/08/2006 rahelk
 * Bug Id 59719 - Added error handling check for ConvertGetToPost request
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.4  2005/08/17 10:54:58  rahelk
 * fixed script error in HTML error page - removed appending of clientscript.js
 *
 * Revision 1.3  2005/08/08 09:44:04  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.2  2005/02/11 09:12:09  mapelk
 * Remove ClientUtil applet and it's usage from the framework
 *
 * Revision 1.1  2005/01/28 18:07:25  marese
 * Initial checkin
 *
 * Revision 1.6  2004/12/13 04:33:56  mapelk
 * Minor modifications for electronic signature support
 *
 * Revision 1.5  2004/12/11 10:33:50  mapelk
 * Minor modifications for electronic signature support
 *
 * Revision 1.4  2004/12/10 08:54:28  mapelk
 * Added electronic signature support
 *
 * Revision 1.3  2004/11/23 09:52:58  riralk
 * Merged Bug id 47934, escaped backslash for javascript variable 'CURRENT_USER' in formatMsg().
 *
 * ----------------------------------------------------------------------------
 *
 */
 

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


import java.util.*;
import java.io.*;
import java.text.*;

/**
 * This is a utility class capable of formatting and outputing HTML tags into ASP scripts
 * For example:
 * <pre>
 *    ASPHTMLFormatter html = new ASPHTMLFormatter();
 *    html.optionTag();
 * </pre>
 *
 * @author  Bobby Issazadhe
 * @version %I%, %G%
 */

public class ASPHTMLFormatter extends ASPPageElement
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPHTMLFormatter");

   public static final String field_separator = ""+IfsNames.fieldSeparator;

   private AutoString html = new AutoString();


   //==========================================================================
   //  Construction
   //==========================================================================

   /**
    * Protected constructor
    */
   protected ASPHTMLFormatter(ASPPage page)
   {
      super(page);
   }

   ASPHTMLFormatter construct()
   {
      return this;
   }

   //==========================================================================
   //  Methods that help to implement the ASPPoolable interface
   //==========================================================================

   /**
    * Called by freeze() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPageElement#freeze
    */
   protected void doFreeze()
   {
   }


   /**
    * Called by reset() in the super class after check of state
    * but before changing it.
    *
    * @see ifs.fnd.asp.ASPPageElement#reset
    */
   protected void doReset()
   {
   }


   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPageElement#clone
    *
    */
   public ASPPoolElement clone( Object obj ) throws FndException
   {
      ASPHTMLFormatter f = new ASPHTMLFormatter((ASPPage)obj);
      f.setCloned();
      return f;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   //==========================================================================
   //  Formating of errors, warnings and informations
   //==========================================================================

   private static final int ERROR_MSG   = 0;
   private static final int WARNING_MSG = 1;
   private static final int INFO_MSG    = 2;

   private static final String FNDERRMSG_BEGIN = "<FNDERRMSG>";
   private static final String FNDERRMSG_END   = "</FNDERRMSG>";

   private static final String IE       = "IE";
   private static final String NETSCAPE = "NETSCAPE";

   /**
    *  Formats an error message originated in asp script or as an Alert Box
    */
   String formatErrorMsg(String errmsg, String addmsg)
   {
      return(formatMsg(errmsg, addmsg, ERROR_MSG));
   }

   /**
    *  Formats an error message originated in asp script or as an Alert Box
    */
   public String formatErrorMsg(String errorString)
   {
      return(formatMsg(errorString, null, ERROR_MSG));
   }

   /**
    *  Formats a warning message originated in asp script
    */
   public String formatWarningMsg(String warningString)
   {
      return(formatMsg(warningString, null, WARNING_MSG));
   }

   /**
    *  Formats an information message originated in asp script
    */
   public String formatInfoMsg(String infoString)
   {
      return(formatMsg(infoString, null, INFO_MSG));
   }

   /**
    *  Does the actual formatting of messages
    */
   private String formatMsg(String message, String addmsg, int msg_type)
   {
      try
      {
         ASPManager mgr  = getASPManager();
         
         if(mgr.isMobileVersion())
            return formatMobileMsg(message,addmsg,msg_type);
         
         ASPPage    page = getASPPage();
         ASPConfig  cfg  = page.getASPConfig();
         String     common_images_location = cfg.getImagesLocation();
         boolean    is_explorer = mgr.isExplorer();

         //AutoString html = new AutoString();
         html.clear();
         boolean alert = false;
         boolean ifs_dialog = false;
         boolean page_id_set = mgr.isPageIdSet();

         String page_caption = "";
         String image_name   = "";
//         mgr.removePageId();

         //Create a html page displaying the error message
         mgr.setAspResponsContentType("text/html;charset=utf-8");
         html.append("<HTML>\n");
         html.append("<HEAD>\n");
         html.append("<script language=javascript>\n\r");
         html.append("  IS_RTL="+getASPManager().isRTL()+";\n");
         html.append("  if (IS_RTL)\n");
         html.append("     document.dir = 'rtl';\n"); 
         html.append("</script>\n\r");        
         html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
         html.append(mgr.getStyleSheetTag());
         
         Buffer decision_buf = (Buffer)mgr.getAspSession().getAttribute(ASPManager.ERROR_DECISION_BUF); 
         if (decision_buf != null)
         {
            // ifs.fnd.base.ManualDecisionException: Reauthentication requested.
            // open authentication dialog. store last COMMAND and the perform action (if any)
            mgr.getAspSession().removeAttribute(ASPManager.ERROR_DECISION_BUF); 
            mgr.getAspSession().setAttribute(ASPManager.DECISION_BUF,decision_buf); 
            String key = mgr.readValue("__COMMAND");
            String perform = "";
            if (! mgr.isEmpty(key) )
            {
               int pos = key.indexOf('.');
               String blkname = pos<=0 ? null : key.substring(0,pos);
               perform = "__"+Str.nvl(blkname,"")+"_"+ASPCommandBar.PERFORM;               
            }
            String str_query = "LAST_COMMAND=" + key + "&PERFORM=" + (mgr.isEmpty(perform)?"":mgr.readValue(perform)) + "&PERFORMED_FIELD=" + (mgr.isEmpty(perform)?"":perform);
            
            html.append("</HEAD>");
            //html.append(mgr.getScriptFileTag());
            //html.append("\n",cfg.getGenericClientScript());
            //html.append("\n",cfg.getConstantClientScript());
            html.append("<script language=javascript>\n\r");
            html.append("window.open('",mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS"),"ReAuthenticate.page?",str_query,"',window.name+'_1','toolbar=NO,status=NO,menubar=NO,scrollbars=NO,resizable=YES,dependent=YES,width=400,height=200,screenX=200,screenY=300,top=200,left=300');");            
            html.append("window.history.back();");
            html.append("</script>\n\r");
            html.append("</HTML>");
            return html.toString();
         }


         switch(msg_type)
         {
            case ERROR_MSG:
               page_caption = mgr.translateJavaText("FNDFMTERR: Error Message");
               image_name   = cfg.getPageErrorImage();
               common_images_location = cfg.getUnsecuredImageLocation();
               
               alert = is_explorer ? cfg.getAlertBoxOnErrorExplorer() :
                                     cfg.getAlertBoxOnErrorNetscape();
               ifs_dialog = is_explorer ? cfg.isIfsDialogOnErrorExplorer() :
                                     cfg.isIfsDialogOnErrorNetscape();

               if (mgr.isMobileVersion() || page.isMultiFrameError() || mgr.isRequestbyGET() || !"Y".equals(mgr.readValue("__HEAD_TAG_GENERATED")) || !page_id_set || ("Y".equals(mgr.readValue("CONVERTGTOP"))))
                  alert = false;
               
               break;
            case WARNING_MSG:
               page_caption = mgr.translateJavaText("FNDFMTWAR: Warning Message");
               image_name   = cfg.getPageWarningImage();
               break;
            case INFO_MSG:
               page_caption = mgr.translateJavaText("FNDFMTINF: Information Message");
               image_name   = cfg.getPageInfoImage();
               break;
         }

         //if(!alert) 
            message = mgr.HTMLEncode(message);
         html.append("<TITLE>",page_caption,"</TITLE>\n");

         if(is_explorer)
            html.append( cfg.getHeadTagErrorExplorer() );
         else
            html.append( cfg.getHeadTagErrorNetscape() );

         //html.append("<script language=\"JavaScript\">\n   CURRENT_USER='"+mgr.getUserId()+"';\n");
         //Bug 47934, start
         String current_user= mgr.getCookiePrefix();
         if (!Str.isEmpty(current_user))
         {
             String _domain="";
             String _user="";
             int bslash_ix=current_user.indexOf("\\");      
             //replace single backslash with double backslash
             if (bslash_ix>0)         
             {    
               _domain=current_user.substring(0,bslash_ix);
               _user=current_user.substring(bslash_ix,current_user.length());
               current_user=_domain+"\\"+_user;
             }     
         }
         html.append("<script language=\"JavaScript\">\n   CURRENT_USER='"+current_user+"';\n");
         //Bug 47934, end
         
         html.append("   AUTH_COOKIE='"+cfg.getAuthUserCookieName()+"';\n</script>\n");

         html.append("\n</HEAD>\n");
         //Bug 40908, start
         html.append("<BODY bgcolor=\"#FFFFFF\">\n");
         //Bug 40908, end

         if(alert)
         {
            html.append("<BR>\n");
         }
         else
         {
            html.append("<FORM NAME=FORM METHOD=POST>\n");
            html.append("<TABLE BORDER=0 CELLSPACING=0 WIDTH=590><TR><TD>\n");

            html.append("<TABLE BORDER=0 class=pageCommandBar CELLPADDING=0 CELLSPACING=0 WIDTH=100% height=22>\n");
            html.append("<tr><td height=22 WIDTH=100%>\n&nbsp;&nbsp;");            
            html.append(page_caption);
            html.append("</td></tr>\n");            
            html.append("</TABLE>\n");

            html.append("<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0 class=pageFormWithBorder width=100% height=305>\n");
            html.append("<TR>\n");
            html.append("<TD VALIGN=TOP><BR>\n");
            html.append("<FONT class=boldTextValue>");
         }

         if( msg_type==ERROR_MSG ) html.append(FNDERRMSG_BEGIN);
         if(alert)
         {
            html.append("<h1>",page_caption,"</h1>\n");
            html.append( message );
         }
          else
            html.append( Str.replace(Str.replace(message,"\r",""),"\n","<BR>") );
         if( msg_type==ERROR_MSG ) html.append(FNDERRMSG_END);

         if (!alert)
         {
            html.append("<BR></FONT>\n");
            html.append("<P>&nbsp</P>");
            html.append("<P>&nbsp</P>\n");
            html.append("<DIV ALIGN=CENTER><CENTER><P><IMG SRC=",common_images_location,image_name);
            html.append(" WIDTH=64 HEIGHT=64>\n");
            html.append("</TD>\n");
            html.append("</TR>\n");
            if (msg_type==ERROR_MSG)
            {
               html.append("<TR>\n");
               html.append("<TD VALIGN=bottom>\n");
               html.append("<DIV ALIGN=RIGHT><P>\n");

               if(page.getASPPortal()==null || !mgr.getASPConfig().isPortalConfigEnabled() || mgr.isStdPortlet())
               {
                  html.append("<INPUT type=button value=\"");
                  html.append( mgr.translateJavaText("FNDFMTBACK: Back") );
                  html.append("\" name=\"BACK\"");
                  html.append(" class='button'");
                  html.append(" onClick=\"javascript:onClick()\">\n");
               }
               else
               {
                  html.append("<a class=hyperLink href=\"",cfg.getParameter("APPLICATION/LOCATION/PORTAL","/&(APPLICATION/PATH)/Default.page"),"?CUSTOMIZE=Y\">");
                  html.append( mgr.translateJavaText("FNDFMTPORTCFG: Go to portal configuration page") );
                  html.append("</a></font>\n");
               }

               html.append("</TD>\n");
               html.append("</TR>\n");
            }
            //html.append("<TR><TD background=\"",common_images_location,"dot_bg.gif\" height=1></TD></TR>\n");
            html.append("</TABLE>\n");

            html.append("</TD>\n");
            html.append("</TR>\n");
            html.append("</TABLE>\n");

            html.append("</TD></TR></TABLE>\n");
            html.append("</FORM>\n");

            if( msg_type==ERROR_MSG )
            {
               for( int i=0; i<30; i++ )
                  html.append("<BR>");
               html.append("<BR>\n");
            }

            html.append("</BODY>\n");
            html.append("</HTML>\n\n");
         }

         //html.append(mgr.getScriptFileTag());
         html.append("\n",cfg.getGenericClientScript());
         //html.append("\n",cfg.getConstantClientScript());
         html.append("\n<script language=javascript>\n");
         html.append("  app_path = \"",cfg.getCookiePath(),"\";\n");
         html.append("  cookie_name = \"",mgr.getPageId(),"\";\n");
         html.append("  COOKIE_PREFIX = \"",cfg.getCookiePathPrefix(),"\";\n");
         html.append("  COOKIE_ROOT_PATH = \"",cfg.getCookieRootPath(),"\";\n");
         html.append("  COOKIE_DOMAIN = \"",cfg.getCookiePathDomain(),"\";\n");
         if(page_id_set)
            html.append("  message = readCookie(cookie_name);\n");
         else
            html.append("  message =\"*\";\n");

         html.append("  writePageIDCookie(\"cookie_name\",'*',new Date(\"January 1, 1990\"),app_path);\n");

         html.append("  if(message==\"*\")\n");
         html.append("  {\n");

         String cookie_name   = mgr.readValue("__PAGE_ID");
         String formatted_msg = Str.replace(alert ? page_caption + ":\\n\\n" + mgr.JScriptEncode(message) : "*", "\"", "\\\"");

         if ( !Str.isEmpty(cookie_name) )
         {
            /*
            html.append("    document.cookie = \"",cookie_name,"=\"+encodeUnicode(\"");
            html.append( formatted_msg );
            html.append("\")+\";path=\"+app_path;\n");
            */
            html.append("  writePageIDCookie(\"",cookie_name,"\",encodeUnicode(\""+formatted_msg+"\"),new Date(\"January 1, 9999\"),app_path);\n");

            if((alert && cfg.addErrorInfoEnabled()) || ifs_dialog)
            {
               addmsg = addmsg.substring(0,2000);
               String formatted_addmsg = Str.replace( mgr.JScriptEncode(addmsg), "\"", "\\\"");

               /*
               html.append("    document.cookie = \"",cookie_name,"_ADD_MSG=\"+encodeUnicode(");
               html.append("\"",formatted_addmsg);
               */
               html.append("  writePageIDCookie(\"",cookie_name,"_ADD_MSG\",encodeUnicode(\""+formatted_addmsg+"\"),new Date(\"January 1, 9999\"),app_path);\n");

               if(is_explorer)
               {
                  //html.append("\\n\"+\n");
                  //html.append("document.forms[\"ERRORMSG\"].innerText )+\";path=\"+app_path;\n");
                  html.append("    window.history.back();\n");
               }
               else
                  //html.append("\"))+\";path=\"+app_path;\n    window.back();\n");
                  // html.append("window.back();\n");
                  html.append("    window.history.back();\n");
            }
            else if(alert)
            {
               // Modified by Terry 20130912
               // Original:
               // html.append("    window",is_explorer?".history":"",".back();\n");
               html.append("    window.history.back();\n");
               // Modified end
            }
               
         }
         else if(alert)
         {
            if ( is_explorer && cfg.addErrorInfoEnabled() )
            {
               html.append("    if(confirm(\"");
               html.append( formatted_msg );
               html.append("\\n\\n");
               html.append( mgr.translateJavaText("FNDFMTMERRI: More info ?") );
               html.append("\"))\n");
               html.append("      alert(document.forms[\"ERRORMSG\"].innerText)\n");
            }
            else
            {
               html.append("    alert(\"");
               html.append( formatted_msg );
               html.append("\");\n");
            }
         }

         html.append("  }\n");
         //html.append("  else\n");
         //html.append("    alert(\"",mgr.translateJavaText("FNDFMTINV: This page has been invalidated."),"\");\n");
         //Bug 40908, start
         //html.append("}\n\n");
         //Bug 40908, end

         /*
         html.append("function readCookie(name)\n");
         html.append("{\n");
         html.append("  var cookie_string = document.cookie;\n");
         html.append("  var cookie_set = cookie_string.split(';');\n");
         html.append("  var set_size = cookie_set.length;\n");
         html.append("  var cookie_pieces;\n");
         html.append("  for(x=0;x<set_size;x++)\n");
         html.append("  {\n");
         html.append("    cookie_pieces = cookie_set[x].split('=');\n");
         html.append("    if(cookie_pieces[0].substring(0,1)==' ')\n");
         html.append("      cookie_pieces[0]=cookie_pieces[0].substring(1,cookie_pieces[0].length);\n");
         html.append("    if(cookie_pieces[0]==name)\n");
         html.append("      return unescape(cookie_pieces[1]);\n");
         html.append("  }\n");
         html.append("  return '';\n");
         html.append("}\n\n");
         */

         if (!alert)
            html.append("function onClick() { window.",is_explorer ? "history." : "","back(); }\n");

         html.append("</script>\n\n");

         //Includes encodeUnicode() and decodeUnicode() functions.
         //html.append(cfg.getURLEncodeFunction());

         if (alert)
            html.append("<FORM NAME=ERRORMSG>");
         else
            html.append( Str.replace(Str.replace(addmsg,"\r",""),"\n","<BR>"), "<BR>");

         /*
         if(alert)
            html.append(addmsg,"\r\n\r\n");
         else
            html.append( Str.replace(Str.replace(addmsg,"\r",""),"\n","<BR>"), "<BR>");
         */

         if( mgr.inOnStartPage() )
            html.append( mgr.translateJavaText("FNDFMTCREMGR: Error while creating ASPManager.") );

         if(DEBUG) debug("ASPHTMLFormatter: HTML sent to browser:\r\n\r\n"+html+"\r\n\r\n");

         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   //==========================================================================
   //  Methods for populating
   //==========================================================================

   /**
    *  This method is used by asp pages that serve
    *  JavaScript that use ClientUtil applet to validate user input
    */
   public String populateString(ASPBuffer aspbuf)
   {
      try
      {
         Buffer buf = aspbuf.getBuffer();
         int i,j, rows, cols;
         //AutoString s = new AutoString();
         html.clear();

         rows = ASPRowSet.countRows(aspbuf.getBuffer());
         for (i=0;i < rows; i++)
         {
            Buffer row =buf.getBuffer(i);
            cols = row.countItems();
            for (j=0;j < cols ; j++ )
            {
               html.append(row.getString(j));
               html.append("^");
            }
         }

         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   //==========================================================================
   //  Methods for populating of Combo Boxes
   //==========================================================================

   /**
    *  Generates html <option> tags with an empty tag at the beginning.
    */
   public String populateListBox(ASPBuffer aspbuf)
   {
      return populateListBox(aspbuf, null, false);
   }


   /**
    *  Generates html <option> tags with an empty tag at the beginning and selects a value.
    */
   public String populateListBox(ASPBuffer aspbuf, String key)
   {
      return populateListBox(aspbuf, key, false);
   }


   /**
    *  Generates html <option> tags without an empty tag.
    */
   public String populateMandatoryListBox(ASPBuffer aspbuf)
   {
      return populateListBox(aspbuf, null, true);
   }


   /**
    *  Generates html <option> tags without an empty tag and selects a value.
    */
   public String populateMandatoryListBox(ASPBuffer aspbuf, String key)
   {
      return populateListBox(aspbuf, key, true);
   }


   /**
    *  Private function for generation of html <option> tags
    */
   private String populateListBox(ASPBuffer aspbuf, String key, boolean mandatory)
   {
      try
      {
         if (aspbuf==null) return "";

         //AutoString html = new AutoString();
         ASPManager mgr = getASPManager();
         Buffer     buf  = aspbuf.getBuffer();
         int        rows = ASPRowSet.countRows(buf);
         int        i,j, cols;

         html.clear();
         // optionally add an empty row first
         if ( !mandatory )
            html.append("<option value=\"\"></option>");

         // check if buffer contains result of an enumeration e.i. contains ^ character
         if( rows == 1 && buf.getBuffer(0).getString(0) != null &&
             buf.getBuffer(0).getString(0).indexOf(IfsNames.fieldSeparator) != -1 )
            return ( populateEnumListBox(html, buf, key) );

         for (i=0;i < rows; i++)
         {
            Buffer row = buf.getBuffer(i);
            cols = row.countItems();
            html.append("<option ");
            for (j=0;j < cols ; j++ )
            {
               String s = row.getString(j);
               if (j == 0)
               {
                  // eventually select the requested row
                  if( !Str.isEmpty(s) && !Str.isEmpty(key) && s.equals(key) )
                     html.append("selected ");
                  html.append("value=\"");
                  html.append(mgr.HTMLEncode(s));
                  html.append("\">");
               }
               else
                  html.append(mgr.HTMLEncode(s));
            }
            html.append("</option>");
         }
         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    *  Generates html <option> tags for buffers containing result of an
    *  F1 enumeration command and selects a value
    */
   private String populateEnumListBox(AutoString out, Buffer buf, String key)
   {
      ASPManager mgr = getASPManager();
      StringTokenizer st = new StringTokenizer(buf.getBuffer(0).getString(0),
                                               field_separator);
      while (st.hasMoreTokens())
      {
         String t = st.nextToken();
         out.append("<option ");
         // eventually select the requested row
         if( !Str.isEmpty(key) && t.equals(key) )
            out.append("selected ");
         out.append("value=\"",mgr.HTMLEncode(t),"\">",mgr.HTMLEncode(t),"</option>");
      }
      return(out.toString());
   }


   void populateListBox( AutoString out,
                         String[] arr,
                         String key,
                         boolean mandatory,
                         boolean in_assignment )
   {
      if( arr==null ) return;

      ASPManager mgr = getASPManager();
      String quot = in_assignment ? "\\\"" : "\"";

      if ( !mandatory ) out.append("<option value=",quot,quot,"></option>");

      for( int i=0; i<arr.length; i++ )
      {
         out.append("<option ");
         String value = arr[i];
         String no_encode_value = value;
         value = mgr.HTMLEncode(value);

         if( !Str.isEmpty(value) && !Str.isEmpty(key) && value.compareTo(key) == 0)
            out.append("selected ");
         out.append("value=",quot,no_encode_value,quot);
         out.append(">",value,"</option>");
      }
   }


   /**
    * Remove some options from a buffer containing data for
    * populateListBox(). The second argument is a comma-separated list
    * of options.
    */
   public void removeFromListBox( ASPBuffer aspbuf, String options )
   {
      final String optsep = field_separator;

      try
      {
         if( aspbuf==null || Str.isEmpty(options) ) return;

         Buffer buf = aspbuf.getBuffer();
         int rows = ASPRowSet.countRows(buf);
         if( rows==0 ) return;

         Vector optarr = new Vector();
         StringTokenizer st = new StringTokenizer(options,",");
         while( st.hasMoreTokens() )
            optarr.addElement(st.nextToken());

         Buffer firstrow = buf.getBuffer(0);
         String firstval = firstrow.getString(0);

         if( rows == 1 && firstval != null && firstval.indexOf(optsep) != -1 )
         {
            String list = optsep + firstval;
            for( int i=0; i<optarr.size(); i++ )
               list = Str.replace(list,optsep+(String)optarr.elementAt(i)+optsep,optsep);
            firstrow.getItem(0).setValue(list.substring(1));
         }
         else
         {
            for( int i=0; i<ASPRowSet.countRows(buf); )
               if( optarr.contains(buf.getBuffer(i).getString(0)) )
                  buf.removeItem(i);
               else
                  i++;
         }
      }
      catch( Throwable any )
      {
         error(any);
      }
   }

   //==========================================================================
   //  Methods for populating of HTML tables
   //==========================================================================

   /**
    *  Generates html <table> tags for a 2 column table
    *  where the first column contains a clickable image
    */
   public String populate2ColSelectTable(ASPBuffer aspbuf, String header)
   {
      try
      {
         ASPConfig  cfg = getASPPage().getASPConfig();
         Buffer buf = aspbuf.getBuffer();
         int i,j, rows, cols;
         //AutoString htmlTag = new AutoString();
         html.clear();
         String s = null;
         ASPManager mgr = getASPManager();

         // Create a dummy Form
         html.append("<FORM >");
         // Create the table heading
         rows = ASPRowSet.countRows(aspbuf.getBuffer()); //rows = buf.countItems();
         cols = buf.getBuffer(0).countItems();
         html.append("<TABLE");
         html.append(" BORDER=0");
         html.append(" WIDTH=100% >");
         html.append("<TR>");
         html.append("<TD");
         html.append(" WIDTH=100%");
         html.append(" CELLPADDING=3");
         html.append(" COLSPAN=");
         html.appendInt(cols);
         html.append(" BGCOLOR=#FFFFFF >");
         html.append(" <FONT");
         html.append(" CLASS=normalTextValue >");
         html.append("<STRONG>");
         html.append(header);
         html.append("<STRONG>");
         html.append("</FONT>");
         html.append("</TD>");
         html.append("</TR>");

         String bgcolor      = "pageForm";
         String img_location = cfg.getImagesLocationWithRTL();
         String ret_image    = cfg.getColumnReturnImage();
         String click_msg    = mgr.translateJavaText("FNDFMTRET1: Click to return value");

         // Create the table contents
         for (i=0;i < rows; i++)
         {
            Buffer row = buf.getBuffer(i);
            cols = row.countItems();
            html.append("<TR>");
            for (j=0;j < cols ; j++ )
            {
               s = row.getString(j);
               if(s == null)
                  s = "&nbsp";
               switch(j)
               {
                  case 0:
                     html.append("<TD");
                     html.append(" class=",bgcolor," >");
                     html.append("<p ALIGN=CENTER >");
                     html.append("<a HREF=\"javascript:setValue('",s,"')\">");
                     html.append("<IMG SRC=\"",img_location,ret_image,"\"");
                     html.append(" BORDER=0");
                     html.append(" ALT=\"",click_msg,"\" title=\""+click_msg+"\"></a>");
                     html.append("</TD>");
                     break;
                  case 1:
                     html.append("<TD");
                     html.append(" class=",bgcolor," >");
                     html.append("<FONT");
                     html.append(" SIZE=1 >");
                     html.append(s);
                     html.append(" ");
                     break;
                  default:
                     if(s == null)
                        html.append("&nbsp");
                     else
                        html.append(s);
                     html.append(" ");
               }
            }
            html.append("</FONT>");
            html.append("</TD>");
         }

         html.append("</TR>");
         html.append("</TABLE>");
         html.append("</FORM>");
         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    *  Generates html <table> tags for a 2 column table
    *  where the first column contains a clickable image
    */
   public String populate2ColDialogSelectTable(ASPBuffer aspbuf, String header)
   {
      try
      {
         ASPConfig  cfg = getASPPage().getASPConfig();
         Buffer buf = aspbuf.getBuffer();
         int i,j, rows, cols;
         //AutoString htmlTag = new AutoString();
         html.clear();
         String s = null;
         ASPManager mgr = getASPManager();

         // Create the table heading
         rows = ASPRowSet.countRows(aspbuf.getBuffer()); //rows = buf.countItems();
         cols = buf.getBuffer(0).countItems();
         html.append("<TABLE");
         html.append(" BORDER=0");
         html.append(" WIDTH=100% >");
         html.append("<TR>");
         html.append("<TD");
         html.append(" WIDTH=100%");
         html.append(" CELLPADDING=3");
         html.append(" COLSPAN=");
         html.appendInt(cols);
         html.append(" BGCOLOR=#FFFFFF >");
         html.append(" <FONT");
         html.append(" class=normalTextValue >");
         html.append("<STRONG>");
         html.append(header);
         html.append("<STRONG>");
         html.append("</FONT>");
         html.append("</TD>");
         html.append("</TR>");

         String bgcolor      = "pageForm";
         String img_location = cfg.getImagesLocation();
         String ret_image    = cfg.getColumnReturnImage();
         String click_msg    = mgr.translateJavaText("FNDFMTRET2: Click to return value");

         // Create the table contents
         for (i=0;i < rows; i++)
         {
            Buffer row = buf.getBuffer(i);
            cols = row.countItems();
            html.append("<TR>");
            for (j=0;j < cols ; j++ )
            {
               s = row.getString(j);
               if(s == null)
                  s = "&nbsp";
               switch(j)
               {
                  case 0:
                     html.append("<TD");
                     html.append(" class=",bgcolor,">");
                     html.append("<p ALIGN=CENTER >");
                     html.append("<INPUT SRC=\"",img_location,ret_image);
                     html.append("\" NAME=\"",s,"\"");
                     html.append(" ALT=\"",click_msg,"\" title=\""+click_msg+"\" TYPE=image");
                     html.append(" ONCLICK=\"setValue(name)\">");
                     html.append("</TD>");
                     break;
                  case 1:
                     html.append("<TD");
                     html.append(" class=",bgcolor," >");
                     html.append("<FONT");
                     html.append(" SIZE=1 >");
                     html.append(s);
                     html.append(" ");
                     break;
                  default:
                     if(s == null)
                        html.append("&nbsp");
                     else
                        html.append(s);
                     html.append(" ");
               }
            }
            html.append("</FONT>");
            html.append("</TD>");
         }

         html.append("</TR>");
         html.append("</TABLE>");
         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   /**
    *  Generates html <table> tags
    */
   public String populateTable(ASPBuffer aspbuf, String header)
   {
      try
      {
         Buffer buf = aspbuf.getBuffer();
         int i,j, rows, cols;
         //AutoString htmlTag = new AutoString();
         html.clear();
         String s = null;
         StringTokenizer st = new StringTokenizer(header,",");

         // Create the table heading
         rows = ASPRowSet.countRows(aspbuf.getBuffer()); //rows = buf.countItems();
         html.append("<TABLE BORDER=0 WIDTH=100% CELLPADDING=2 CELLSPACING=1 COLS=");
         html.appendInt(rows);
         html.append("><TR>");

         //unpack and set user defined header items
         while (st.hasMoreTokens())
         {
            html.append("<TH WIDTH=25% BGCOLOR=#000000>","<FONT COLOR=#FFFFFF>");
            html.append(st.nextToken());
            html.append("</TH>");
         }

         // Create the table contents
         for (i=0;i < rows; i++)
         {
            Buffer row =buf.getBuffer(i);
            cols = row.countItems();
            html.append("<TR>");
            for (j=0;j < cols ; j++ )
            {
               s = row.getString(j);
               html.append("<TD WIDTH=25% BGCOLOR=#FFFFFF>");
               html.append("<FONT SIZE=1>");
               if(s==null)
               {
                  html.append("&nbsp");
               }
               else
               {
                  html.append(s);
               }

               html.append("</TD>");
            }
         }

         html.append("</TR>");
         html.append("</TABLE>");
         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   private class HyperlinkColumn
   {
      String          colurl;       // URL for this column
      // Added by Terry 20121218
      // Can use ASPField to set Hyperlink URL.
      String          fieldurl;
      // Added end
      int             colposition;  // position in the DATA buffer for this column
      DataFormatter   colformatter; // DataFormatter for this column

      int[]           parposition;  // position in the DATA buffer for each parameter
      DataFormatter[] parformatter; // DataFormatter for each parameter
      String[]        parname;      // name for each parameter
   }


   /**
    *  Generates html <table> tags and adds
    *  user defined hyperlinks to columns
    */
   public String populateHyperlinkTable(ASPBuffer aspbuf, String fields)
   {
      try
      {
         ASPConfig  cfg = getASPPage().getASPConfig();
         //AutoString htmlTag = new AutoString();
         html.clear();
         Buffer buf = aspbuf.getBuffer();
         ASPManager mgr = getASPManager();
         Vector v = new Vector();
         String urlTag = null;
         String urlParam = null;

         //use tokenizer to retrive list of asp fields to be used in table
         StringTokenizer st = new StringTokenizer(fields," ,\n\r\t");
         while( st.hasMoreTokens() )
            v.addElement(mgr.getASPField(st.nextToken()));

         ASPField[] columns = new ASPField[v.size()];
         v.copyInto(columns);

         int column_count = columns.length;
         int row_count = ASPRowSet.countRows(aspbuf.getBuffer());

         //Create the table and set number of columns according to the number of asp fields
         html.append("<TABLE");
         html.append(" BORDER=0");
         html.append(" WIDTH=100%");
         html.append(" CELLPADDING=3");
         html.append(" COLS=");
         html.appendInt(column_count);
         html.append(">");
         html.append("<TR>");

         HyperlinkColumn[] hypercolumns = new HyperlinkColumn[column_count];
         Buffer firstrow = row_count==0 ? null : buf.getBuffer(0);

         String hbgcolor = "#FFFFFF";
         String hftcolor = "#000000";
         String bgcolor  = "pageForm";

         //Retrive fields and their corresponding labels and format table header
         //Initialize the HyperlinkColumn array
         for( int c=0; c<column_count; c++ )
         {
            ASPField field = columns[c];
            String label = field.getLabel();

            html.append("<TH");
            html.append(" WIDTH=25%");
            html.append(" BGCOLOR=",hbgcolor," >");
            html.append("<FONT");
            html.append(" SIZE=2");
            html.append(" COLOR=",hftcolor," >");
            html.append(label);
            html.append("</TH>");

            if( row_count>0 )
            {
               HyperlinkColumn hcol = new HyperlinkColumn();
               hypercolumns[c] = hcol;

               hcol.colurl = field.getHyperlinkURL();
               // Added by Terry 20121218
               // Can use ASPField to set Hyperlink URL.
               hcol.fieldurl = field.getHyperlinkFieldURL();
               // Added end
               hcol.colformatter = field.getDataFormatter();
               hcol.colposition = firstrow.getItemPosition(field.getName());

               // Modified by Terry 20121218
               // Can use ASPField to set Hyperlink URL.
               // Origianl:
               // if( hcol.colurl!=null )
               if( !Str.isEmpty(hcol.colurl) || !Str.isEmpty(hcol.fieldurl)) // Modified end
               {
                  // Modified by Terry 20131028
                  // Can accept fields aliases
                  // Original:
                  // ASPField[] params = field.getHyperlinkParameters();
                  Vector fields_vector = new Vector();
                  Vector aliases_vector = new Vector();
                  field.getHyperlinkParameters(fields_vector, aliases_vector);
                  ASPField[] params = new ASPField[fields_vector.size()];
                  fields_vector.copyInto(params);
                  String[] params_aliases = new String[aliases_vector.size()];
                  aliases_vector.copyInto(params_aliases);
                  // Modified end
                  
                  int param_count = (params!=null)? params.length: 0;
                  hcol.parposition = new int[param_count];
                  hcol.parformatter = new DataFormatter[param_count];
                  hcol.parname = new String[param_count];

                  for( int p=0; p<param_count; p++ )
                  {
                     ASPField parameter = params[p];
                     hcol.parposition[p] = firstrow.getItemPosition(parameter.getName());
                     hcol.parformatter[p] = parameter.getDataFormatter();
                     // Modified by Terry 20131028
                     // Can accept fields aliases
                     // Original:
                     // hcol.parname[p] = parameter.getName();
                     hcol.parname[p] = params_aliases[p];
                     // Modified end
                  }
               }
            }
         }

         //For each row get from the buffer the corresponding data
         for(int r=0; r<row_count; r++)
         {
            html.append("<TR>");

            Buffer row = buf.getBuffer(r);
            Item item;

            //For each column in the row get the asp field name, use it to retrive the corresponding URL and value, if any.
            //Then carry on with formatting each column that is specified to be displayed in the table
            for(int c=0; c<column_count; c++)
            {
               HyperlinkColumn hcol = hypercolumns[c];
               item = row.getItem(hcol.colposition);
               String column_value = aspbuf.convertToClientString(item, hcol.colformatter);

               // Modified by Terry 20121218
               // Can use ASPField to set Hyperlink URL.
               // Origianl:
               // String url = hcol.colurl;
               String url = "";
               if (!Str.isEmpty(hcol.fieldurl))
                  url = (String) row.getItem(hcol.fieldurl).getValue();
               
               if (Str.isEmpty(url))
                  url = hcol.colurl;
               // Modified end
               
               //check if field have URL attached
               if(url != null)
               {
                  String param_delim = "&";
                  urlParam = "";
                  urlTag = "";

                  //it did have an url so do formatting

                  int param_count = hcol.parposition.length;
                  for( int p=0; p<param_count; p++ )
                  {
                     item  = row.getItem(hcol.parposition[p]);
                     String param_value = item.getString();
                     //String param_value = aspbuf.convertToClientString(item, hcol.parformatter[p]);

                     //Don't add ampersand to last parameter
                     if(p == param_count -1)
                        param_delim =  "";

                     urlParam += hcol.parname[p] + "=" + param_value + param_delim;
                  }
                  urlTag = "<a HREF=" + mgr.prepareURL(url +"?" + urlParam) +">";
               }

               //Create the column
               html.append("<TD");
               html.append(" WIDTH=25%");
               html.append(" class=",bgcolor," >");
               html.append(urlTag);
               html.append("<FONT");
               html.append(" SIZE=1 >");
               if(column_value==null)
               {
                  html.append("&nbsp");
               }
               else
               {
                  html.append(column_value);
                  if(url != null)
                     html.append("</a>");
               }
               html.append("</TD>");
               urlTag = "";
            }

            //formattera tabell-raden
         }

         html.append("</TR>");
         html.append("</TABLE>");

         return html.toString();
      }
      catch( Throwable any )
      {
         getASPManager().getASPLog().error(any);
         return null;
      }
   }


   /**
    * Return HTML code for a Command Link having the specified label
    * and attached to the given FScript function.
    */
   public String showCommandLink( String server_function, String label )
   {
      try
      {
         //AutoString html = new AutoString();
         html.clear();
         html.append("<A class=hyperLink HREF=\"javascript:commandClick('");
         html.append(server_function);
         html.append("')\">");
         html.append(getASPManager().translate(label));
         html.append("</A>\n");

         return html.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }

   /**
    * Return HTML code with a Favorite hyperlink having the specified url and label.
    * When a user clicks on this link then the JavaScript function loadFavoriteUrl()
    * will be invoked which will move the complete url (with all QueryString parameters)
    * to the bowser's address (location) field. Then the user can add this url to
    * his or her Favorites/Bookmarks.
    */
   public String showFavoriteLink( String url, String label )
   {
      try
      {
         if( Str.isEmpty(url) ) return "";
         //AutoString html = new AutoString();
         html.clear();
         html.append("<A class=hyperLink HREF=\"javascript:loadFavoriteUrl('",url,"')\">");
         html.append(getASPManager().translate(label));
         html.append("</A>\n");

         return html.toString();
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }


   //==========================================================================
   //  Misc
   //==========================================================================

   /**
    * Subclasses (in other packages) can use this method to retrieve the Buffer
    * from an ASPBuffer
    */
   protected Buffer getBuffer(ASPBuffer buf)
   {
      return buf.getBuffer();
   }


   //==========================================================================
   //  Custom design functions
   //  Generates translated labels and input tags
   //==========================================================================

   /**
    * Generates html tag to draw a read only text label.
    */
   public String drawReadLabel( String label )
   {
      return drawLabel(label,true);
   }

   /**
    * Generates html tag to draw a text label.
    */
   public String drawWriteLabel( String label )
   {
      return drawLabel(label,false);
   }

   private String drawLabel( String label, boolean readOnlyMode )
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      if (!Str.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      if(readOnlyMode)
         return "<span OnClick=\"showHelpTag('"+usage_id+"')\"><font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usage_id+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel>&nbsp;" + getASPManager().translate(label) + "</font></span>";
      else
         return "<span OnClick=\"showHelpTag('"+usage_id+"')\"><font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usage_id+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel>&nbsp;" + getASPManager().translate(label) + "</font></span>";
   }


   /**
    * Generates html tag to draw a text value.
    */
   public String drawReadValue( String value )
   {
      return "<font class=normalTextValue>&nbsp;" + getASPManager().translate(value) + "</font>";
   }


   /**
    * Generates html tag to draw a text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    */
   public String drawTextField( String name, String value, String tag )
   {
      return drawTextField(name,value,tag,0,0,false);
   }

   /**
    * Generates html tag to draw a text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    */
   public String drawTextField( String name, String value, String tag, int size )
   {
      return drawTextField(name,value,tag,size,0,false);
   }

   /**
    * Generates html tag to draw a text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    * @param maxlength Maximum length of the field
    */
   public String drawTextField( String name, String value, String tag, int size, int maxlength )
   {
      return drawTextField(name,value,tag,size,maxlength,false);
   }

   /**
    * Generates html tag to draw a text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    * @param maxlength Maximum length of the field
    * @param mandatory Set the mandatory property of the field 
    */
   public String drawTextField( String name, String value, String tag, int size, int maxlength, boolean mandatory )
   {
      return "<input class='editableTextField' type=text " + (size==0? "":" size=\"" + (getASPManager().isExplorer() ? size : Math.round(size*.6)) + "\" ") +
             (maxlength==0? "":" maxlength=\"" + maxlength + "\" ") + "name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + ">" + (mandatory? "*":"");
   }


   /**
    * Generates html tag to draw a read only text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    */
   // Read-only text field; no borders (in IE).
   public String drawReadOnlyTextField( String name, String value, String tag )
   {
      return drawReadOnlyTextField(name,value,tag,0,0,false);
   }

   /**
    * Generates html tag to draw a read only text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    */
   public String drawReadOnlyTextField( String name, String value, String tag, int size )
   {
      return drawReadOnlyTextField(name,value,tag,size,0,false);
   }

   /**
    * Generates html tag to draw a read only text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    * @param maxlength Maximum length of the field
    */
   public String drawReadOnlyTextField( String name, String value, String tag, int size, int maxlength )
   {
      return drawReadOnlyTextField(name,value,tag,size,maxlength,false);
   }

   /**
    * Generates html tag to draw a read only text field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param size Size of the field
    * @param maxlength Maximum length of the field
    * @param mandatory Set the mandatory property of the field 
    */
   public String drawReadOnlyTextField( String name, String value, String tag, int size, int maxlength, boolean mandatory )
   {
      return "<input class='readOnlyTextField' type=text readonly tabindex=-1 OnChange='this.value=this.defaultValue' " + (size==0? "":" size=\"" + (getASPManager().isExplorer() ? size : Math.round(size*.6)) + "\" ") +
             (maxlength==0? "":" maxlength=\"" + maxlength + "\" ") + "name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + ">" + (mandatory? "*":"");
   }



   /**
    * Generates html tag to draw a password field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    */
   public String drawPasswordField( String name, String value, String tag )
   {
      return drawPasswordField( name,value,tag,false);
   }

   /**
    * Generates html tag to draw a password field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param mandatory Set the mandatory property of the field 
    */
   public String drawPasswordField( String name, String value, String tag, boolean mandatory )
   {
      return "<input class='passwordTextField' type=password name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + ">" + (mandatory? "*":"");
   }


   /**
    * Generates html tag to draw a password field.
    *
    * @param name Name of the field
    * @param value Value of the field
    * @param tag Additional tag for the text field
    * @param mandatory Set the mandatory property of the field 
    * @param size Size of the field
    */
   public String drawPasswordField( String name, String value, String tag, boolean mandatory, int size )
   {
      return "<input class='passwordTextField' type=password size=" + (getASPManager().isExplorer() ? size : Math.round(size*.6)) +
             " name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + ">" + (mandatory? "*":"");
   }


   /**
    * Generates html tag to draw a hidden field.
    *
    * @param name Name of the field
    * @param value Value of the field
    */
   public String drawHidden( String name, String value )
   {
      return "<input type=hidden name=\"" + name + "\" value=\"" + value + "\">";
   }

   /**
    * Generates html tag to draw a submit button.
    *
    * @param name Name of the button
    * @param value Value of the button
    * @param tag Additional tag for the button
    */
   public String drawSubmit( String name, String value, String tag )
   {
      return "<input class='button' type=submit name=\"" + name + "\" value=\"" + getASPManager().translate(value) + "\" " + (tag==null?"":tag) + ">";
   }


   /**
    * Generates html tag to draw a reset button.
    *
    * @param name Name of the button
    * @param value Value of the button
    * @param tag Additional tag for the button
    */
   public String drawReset( String name, String value, String tag )
   {
      return "<input class='button' type=reset name=\"" + name + "\" value=\"" + getASPManager().translate(value) + "\" " + (tag==null?"":tag) + ">";
   }


   /**
    * Generates html tag to draw a button.
    *
    * @param name Name of the button
    * @param value Value of the button
    * @param tag Additional tag for the button
    */
   public String drawButton( String name, String value, String tag )
   {
      return "<input class='button' type=button name=\"" + name + "\" value=\"" + getASPManager().translate(value) + "\" " + (tag==null?"":tag) + ">";
   }


   /**
    * Generates html tag to draw a text area.
    *
    * @param name Name of the text area
    * @param value Value of the text area
    * @param tag Additional tag for the text area
    * @param rows Number of rows for the text area
    * @param cols Number of columns for the text area
    */
   public String drawTextArea( String name, String value, String tag, int rows, int cols )
   {
      return drawTextArea(name,value,tag,rows,cols,false);
   }

   /**
    * Generates html tag to draw a text area.
    *
    * @param name Name of the text area
    * @param value Value of the text area
    * @param tag Additional tag for the text area
    * @param rows Number of rows for the text area
    * @param cols Number of columns for the text area
    * @param mandatory Set the mandatory property of the text area 
    */
   public String drawTextArea( String name, String value, String tag, int rows, int cols, boolean mandatory )
   {
      if (!("".equals(tag)) && (tag.indexOf("<a") > 0))
      {
         String tag1 = tag.substring(0,tag.indexOf("<a")-1);
         String tag2 = tag.substring(tag.indexOf("<a")-1);
            
         return "<textarea class='editableTextArea' name=\"" + name + "\" rows="+rows+
                " cols="+(getASPManager().isExplorer() ? cols : Math.round(cols*.6))+" " + (tag1==null?"":tag1) + value +
                "</textarea>" + tag2 + ">" + (mandatory? "*":"");
      }    
      else
         return "<textarea class='editableTextArea' name=\"" + name + "\" rows="+rows+
             " cols="+(getASPManager().isExplorer() ? cols : Math.round(cols*.6))+" " + (tag==null?"":tag) + ">" + value +
             "</textarea>" + (mandatory? "*":"");
   }


   /**
    * Generates html tag to draw a radio button.
    *
    * @param label Label for the radio button
    * @param name Name of the radio button
    * @param value Value of the radio button
    * @param checked Initial state of the radio button
    * @param tag Additional tag for the radio button
    */
   public String drawRadio( String label, String name, String value, boolean checked, String tag )
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      if (!Str.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      
      String check = "";
      if(checked)
         check = " CHECKED ";
      return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + check + " " +(tag==null?"":tag) + ">&nbsp;<span OnClick=\"showHelpTag('"+usage_id+"')\"><font onmouseover=\"if(helpMode) changeToHelpModeColor(this, 'normalTextLabel','"+usage_id+"')\" onmouseout=\"changeFromHelpModeColor(this, 'normalTextLabel')\" class=normalTextLabel>" + getASPManager().translate(label) + "</font></span>";
   }


    /**
     * Generates html tag to draw a select box.
      * This version of drawSelect can be usefull when convering old webpages.
      */

   public String drawPreparedSelect(String name, String optiontag, String tag, boolean mandatory)
    {
      return  "<select class='selectbox' name=\"" + name + "\" " + (tag==null?"":tag) + ">" + optiontag + "</select>" + (mandatory? "*":"");
    }


   /**
     * Generates html tag to draw a select box.
     */
   public String drawSelect( String name, ASPBuffer values, String key, String tag )
   {
      return drawSelect(name,values,key,tag,false);
   }

   /**
     * Generates html tag to draw a select box.
     */
   public String drawSelect( String name, ASPBuffer values, String key, String tag, boolean mandatory )
   {
      return  "<select class='selectbox' name=\"" + name + "\" " + (tag==null?"":tag) + ">" + populateListBox(values,key,false) + "</select>" + (mandatory? "*":"");
   }


   /**
     * Generates html code for open a tag to draw a select box.
     *
     * @see #drawSelectOption
     * @see #drawSelectEnd
     */
   public String drawSelectStart( String name, String tag )
   {
      return "<select class='selectbox' name=\"" + name + "\" " + (tag==null?"":tag) + ">";
   }


   /**
     * Generates html code to draw the option values of the select box.
     *
     * @see #drawSelectStart
     * @see #drawSelectEnd
     */
   public String drawSelectOption( String label, String value, boolean selected )
   {
      ASPManager mgr = getASPManager();
      if(selected)
         return "<option selected value=\"" + mgr.HTMLEncode(value) + "\">" + mgr.HTMLEncode(label) + "</option>";
      else
         return "<option value=\"" + mgr.HTMLEncode(value) + "\">" + mgr.HTMLEncode(label) + "</option>";

   }

   /**
     * Generates html code for close the tag to draw a select box.
     *
     * @see #drawSelectStart
     * @see #drawSelectOption
     */
   public String drawSelectEnd( )
   {
      return drawSelectEnd(false);
   }

   /**
     * Generates html code for close the tag to draw a select box.
     *
     * @see #drawSelectStart
     * @see #drawSelectOption
     */
   public String drawSelectEnd( boolean mandatory )
   {
      return "</select>" + (mandatory? "*":"") + "</font>";
   }

   /**
     * Generates html tag to draw a check box.
     */
   public String drawCheckbox( String name, String value, boolean checked, String tag )
   {
      String check = "";
      if(checked)
         check = " CHECKED ";
      return "<input class=checkbox type=checkbox name=\"" + name + "\" value=\"" + value + "\" " + (tag==null?"":tag) + " " + check + ">";
   }

   // ===================================================================================================
   // Mobile Framework
   // ===================================================================================================
   
   /**
    *  Does the actual formatting of messages in mobile web browsers
    */
   private String formatMobileMsg(String message, String addmsg, int msg_type)
   {
      try
      {
         ASPManager mgr  = getASPManager();
         ASPConfig  cfg  = mgr.getASPPage().getASPConfig();
         String     common_images_location = cfg.getMobileImageLocation();

         html.clear();

         //Create a html page displaying the error message
         mgr.setAspResponsContentType("text/html;charset=utf-8");
         html.append("<html>\n");
         html.append("<head>\n");
         html.append("<script language=javascript>\n");
         html.append("  IS_RTL="+getASPManager().isRTL()+";\n");
         html.append("  if (IS_RTL)\n");
         html.append("     document.dir = 'rtl';\n"); 
         html.append("</script>\n");        
         html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
         html.append(mgr.getStyleSheetTag());
         
         String page_caption = "";
         String image_name   = "";

         switch(msg_type)
         {
            case ERROR_MSG:
               page_caption = mgr.translateJavaText("FNDFMTERR: Error Message");
               image_name   = cfg.getPageErrorImage();
               break;
            case WARNING_MSG:
               page_caption = mgr.translateJavaText("FNDFMTWAR: Warning Message");
               image_name   = cfg.getPageWarningImage();
               break;
            case INFO_MSG:
               page_caption = mgr.translateJavaText("FNDFMTINF: Information Message");
               image_name   = cfg.getPageInfoImage();
               break;
         }

         message = mgr.HTMLEncode(message);
         
         html.append("<title>",page_caption,"</title>\n");
         html.append("</head>\n");
         
         html.append("<body topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" class=\"pageBody\" >\n");
         html.append("<form>\n");
         html.append("<table cellpadding=\"1\" width=\"90%\" class=\"pageCommandBar\" align=\"center\">\n");
         html.append("   <tr>\n");
         html.append("      <td>"+page_caption+"</td>\n");
         html.append("   </tr>\n");
         html.append("</table>\n");
         html.append("<table cellpadding=\"1\" width=\"90%\" class=\"pageFormWithBorder\" align=\"center\">\n");
         html.append("   <tr>\n");
         html.append("      <td valign=\"top\" align=\"center\"><img src=\"",common_images_location,image_name,"\"></td>\n");
         html.append("      <td>&nbsp;</td>\n");
         html.append("      <td width=\"100%\" class=\"normalTextValue\">"+message+"</td>\n");
         html.append("   </tr>\n");
         html.append("   <tr>\n");
         html.append("      <td>&nbsp;</td>\n");
         html.append("      <td>&nbsp;</td>\n");
         html.append("      <td align=\"right\"><input class=\"button\" type=\"button\" value=\"",mgr.translateJavaText("FNDFMTBACK: Back"),"\" onclick=\"javascript:window.history.back();\">&nbsp;&nbsp;</td>\n");
         html.append("   </tr>\n");
         html.append("</table>\n");
         html.append("</form>\n");

         if( mgr.inOnStartPage() )
            html.append( mgr.translateJavaText("FNDFMTCREMGR: Error while creating ASPManager.") );

         if(DEBUG) debug("ASPHTMLFormatter: HTML sent to browser:\r\n\r\n"+html+"\r\n\r\n");
         
         html.append("</body>\n");
         html.append("</html>\n");
               
         return(html.toString());
      }
      catch( Throwable any )
      {
         error(any);
         return null;
      }
   }
   
   public void populateMobileListBox( AutoString out, String[] arr, String key, boolean mandatory, boolean in_assignment )
   {
      if(getASPManager().isMobileVersion())
         populateListBox(out, arr, key, mandatory, in_assignment );
      else
         error(new FndException("FNDFMTONLYFORMOBILE: populateMobileListBox() can only be called by MobilePageProviders."));
   }
}
