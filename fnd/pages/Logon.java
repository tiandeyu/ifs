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
*  File        : Logon.java
*  Modified    :
*    ASP2JAVA Tool  2001-Feb-15 - Created Using the ASP file Logon.asp
*    Suneth M       2001-Sep-12 - Changed localization tags according to standard. 
*    Ramila H       2001-Oct-11 - Added coding to remove navigator_directory_cookie cookie.
*    Ramila H       2001-Nov-20 - corrected PROJ call id 70421.
*    Ramila H       2002-Mar-11 - disabled Help and Naviagte Button (PROJ #78170).
*    Chandana D     2002-Oct-21 - Commented the statement which removes the cookie for the Navigator.
*    Suneth M       2002-Nov-05 - Changed run() & getContents() to give a understandable error
*                                 message when try to logon without a password.    
*    Chandana D     2003-May-27 - Modified coding for the new L&F.
*    Ramila H       2003-Sep-05 - Removed button width to avoid translation problems.
*    Ramila H       2003-Sep-08 - Added check to redirect to portal page.
*    Chandana D     2003-Dec-18 - Bug 40908, fixed. 
*    Ramila H       2004-02-23  - Bug id 42913, changed redirect if condition. 
*    Chandana D     2004-May-12 - Updated for the use of new style sheets.
*    Ramila H       2004-05-26  - implemented code for multi-language support
*    Ramila H       2004-10-21  - made improvements to changing language. 
* ----------------------------------------------------------------------------
* New Comments:
* 2010/03/02 sumelk Bug 87909, Changed getContents() to append two javascript variables.
* 2010/02/03 sumelk Bug 88795, Replaced language_table Hashtable with a Hashmap in getContents().
* 2009/04/24 buhilk Bug 82180, Replaces ClientServices with AnonymousAccess. Updated validateCredentials() & getContents().
* 2009/03/12 sumelk Bug 80503, Changed getContents() to support logoff functionlity for 
*                   externally identified authentication.
* 2008/07/22 sadhlk Bug 73058, Modified getContents() to correct script error due to translation.
* 2008/05/02 sadhlk Bug 72853, Modified run() and added getContents() to provide single sign-on
*                              functionality in Aurora Explorer.
* 2007/12/03 buhilk IID F1PR1472, Added Mini framework functionality for use on PDA.
* 2007/08/23 sadhlk Bug id 67401, Modified getContents().
* 2007/08/17 rahelk Bug 65961, Performed password checking through a AJAX POST call.
* 2007/06/29 buhilk Bug 66378, Modified getContents() to save query string in session.
*
* 2007/04/16 rahelk Bug 64766, Fixed domain selectbox GUI problem.
* 2007/04/11 buhilk Bug 64422, Fixed translation errors in getContents().
*
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
* 2006/08/18 gegulk 
* Bug id 59985, Removed the usages of the word "enum" as variable names
*
* 2006/08/08 buhilk
* Bug 59442, Corrected Translatins in Javascript
*
* 2006/06/23 rahelk
* bug id 58990 fixed. Changed to PersistentCookie
*
* Revision 1.5  2006/02/14 05:32:52  rahelk
* call id 133275 - changed replaced domain string in javascript when changepassword disabled
*
* Revision 1.5  2005/10/05 05:32:52  rahelk
* added language links back
*
* Revision 1.4  2005/09/29 08:20:01  rahelk
* bug fix when DOMAIN form element not there
*
* Revision 1.3  2005/09/20 11:30:26  rahelk
* bug fix for username and domain cookies
*
* Revision 1.2  2005/09/19 08:47:23  rahelk
* Simplified login to complex LDAP structure
*
* Revision 1.5  2005/09/14 10:56:13  rahelk
* password management
*
* Revision 1.4  2005/09/14 08:02:08  rahelk
* password management
*
* Revision 1.3  2005/08/08 09:57:01  rahelk
* Declarative JAAS security restructure
*
* Revision 1.2  2005/02/11 09:12:11  mapelk
* Remove ClientUtil applet and it's usage from the framework
*
* Revision 1.1  2005/01/28 18:07:26  marese
* Initial checkin
*
* Revision 1.2  2005/01/04 04:32:20  mapelk
* Bug fix; logon page reappear with the loged on user name some time
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;

import java.util.*;

import javax.servlet.http.HttpSession;

import ifs.fnd.ap.*;
import ifs.genbaw.GenbawConstants;
import ifs.application.anonymousaccess.impl.AnonymousAccessImpl;


public class Logon extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.Logon");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPCommandBar cmdbar;
   private ASPBlock blk;
   private ASPBlockLayout lay;
   private ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   private ASPBuffer bc_Buffer;
   private ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private boolean change_password;

   //===============================================================
   // Construction
   //===============================================================
   public Logon(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      trans   = mgr.newASPTransactionBuffer();
      if(mgr.isRWCHost())
         mgr.getASPContext().removeCookie("__FNDWEB_LOGGED_ONCE");

      mgr.setPageExpiring();
      
      if( !mgr.isEmpty(mgr.getQueryStringValue("LOGOUT")) )
      {
         mgr.logOffCurrentUser();
         mgr.cleanUpRedirectInfo();
         if( mgr.isEmpty(mgr.getQueryStringValue("EXTERNAL")) )
            mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")); //?? PORTAL ??
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CREDENTIALS")))
         validateCredentials();
      else if ("TRUE".equals(mgr.getQueryStringValue("CHANGE_PASSWORD")))
      {
         change_password = true;
         mgr.setInitialFocus("NEWPASSWORD");
      }
   }

   private void validateCredentials()
   {
      ASPManager mgr = getASPManager();
      
      String username = mgr.readValue("USERID");
      String password = mgr.readValue("PASSWD");
      String domain = mgr.readValue("DOMAIN");
      
      String message = "";
      
      if (!mgr.isEmpty(domain))
         username = Str.replace(domain,"${user}", username);
      
      String var = mgr.readValue("CREDENTIALS");
      int status = AnonymousAccessImpl.SUCCESS;
      
      if ("CHANGE_PASSWORD".equals(var))
      {
         try
         {
            ManageUserPassword password_manager = ManageUserPassword.getInstance(mgr.getASPConfig());
            password_manager.changePassword(username,password, mgr.readValue("NEWPASSWORD"), mgr.getASPConfig());
            
         }
         catch(Exception any)
         {
            if (DEBUG) any.printStackTrace(System.out);
            message = any.getMessage();
            status = AnonymousAccessImpl.FAILURE;
         }
         
         mgr.responseWrite(status+"^"+message+"^"); 
      }
      else 
      {
         try
         {
            ManageUserPassword password_manager = ManageUserPassword.getInstance(mgr.getASPConfig());
            Record response_rec = password_manager.checkPassword(username,password, mgr.getASPConfig());

            status = (int)response_rec.find("STATUS").getLong();
            if (DEBUG) debug("password_manager.checkPassword status:"+status);

            switch ( status )
            {
               case AnonymousAccessImpl.SUCCESS:
                  try
                  {
                     int grace_period = (int)response_rec.find("GRACE_PERIOD").getLong();

                     if (mgr.getASPConfig().isChangePasswordEnabled())
                     {
                        if (grace_period > 0)
                           message = mgr.translateJavaScript("FNDSCRIPTSLOGONEXPIRINGNODAYS: Your password will expire in &1 day(s). Change password now.",""+grace_period);
                        else
                           message = mgr.translateJavaScript("FNDSCRIPTSLOGONEXPIRINGSOON: The account will expire soon. Change password now.");
                     }
                     else
                     {
                        if (grace_period > 0)
                           message = mgr.translateJavaScript("FNDSCRIPTSLOGONNOCHNGEXPIRINGNODAYS: Your password will expire in &1 day(s).",""+grace_period);
                        else
                           message = mgr.translateJavaScript("FNDSCRIPTSLOGONNOCHNGEXPIRINGSOON: The account will expire soon.");
                     }
                  }    
                  catch(Exception any)
                  {
                     message = "";
                  }finally {
                     ASPContext ctx =  mgr.getASPContext();
                     trans.clear();
                     cmd = trans.addCustomFunction( "GETUSERPROJS", "PERSON_PROJECT_API.Get_User_Projs", "IN_1" );
                     cmd.addParameter("IN_1",username.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERDEFPROJ", "PERSON_PROJECT_API.Get_User_Def_Proj", "IN_1" );
                     cmd.addParameter("IN_1",username.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERZONES", "PERSON_ZONE_API.Get_User_Zones", "IN_1" );
                     cmd.addParameter("IN_1",username.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERDEFZONE", "PERSON_ZONE_API.Get_User_Def_Zone", "IN_1" );
                     cmd.addParameter("IN_1",username.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERNAME", "PERSON_INFO_API.Get_Name", "IN_1" );
                     cmd.addParameter("IN_1",username.toUpperCase());
                     
                     trans.addRequestHeader(username.toUpperCase());
                     trans = mgr.validate(trans);
                     
                     final class FilterNullUtil{
                       String filterNull(String input){
                           return input == null ? "" : input;
                        }
                     }  
                     
                     FilterNullUtil tempFilter = new FilterNullUtil();
                     String userProjs = tempFilter.filterNull(trans.getValue("GETUSERPROJS/DATA/IN_1"));
                     String userDefaultProj = tempFilter.filterNull(trans.getValue("GETUSERDEFPROJ/DATA/IN_1"));
                     String userZones = tempFilter.filterNull(trans.getValue("GETUSERZONES/DATA/IN_1"));
                     String userDefaultZone = tempFilter.filterNull(trans.getValue("GETUSERDEFZONE/DATA/IN_1"));
                     String userName = tempFilter.filterNull(trans.getValue("GETUSERNAME/DATA/IN_1"));
                      
                     
                     ctx.setGlobal("HZ_SESSION_USER_ID", username.toUpperCase());
                     ctx.setGlobal("HZ_SESSION_USER_NAME", userName);
                     ctx.setGlobal("HZ_SESSION_LOGIN_NAME", username.toUpperCase());
                     ctx.setGlobal("HZ_SESSION_DEPT_ID", "root_node_id");
                     ctx.setGlobal("HZ_SESSION_DEPT_NAME", "HUIZHENG");
                     ctx.setGlobal("appcode", "system");
                     ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT, userDefaultProj);
                     ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_ZONE, userDefaultZone);
                     ctx.setGlobal(GenbawConstants.PERSON_PROJECTS, userProjs);
                     ctx.setGlobal(GenbawConstants.PERSON_ZONES, userZones); 
                     HttpSession session = mgr.getAspSession();  
                     session.setAttribute("LOGON", "1");  
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_PROJECT + ":" + userDefaultProj);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_ZONE + ":" + userDefaultZone);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_PROJECTS + ":" + userProjs);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_ZONES + ":" + userZones);
                     
                     System.out.println("set hz user info sucess............" + username.toUpperCase());   

                     }
   
                  break;
               case AnonymousAccessImpl.INVALID_CREDENTIALS:
                  message = mgr.translateJavaScript("FNDSCRIPTSLOGOINVCRED: Invalid username/password");
                  break;
               case AnonymousAccessImpl.ACCOUNT_LOCKED:
                  message = mgr.translateJavaScript("FNDSCRIPTSLOGONACCLOCKED: Account has been locked. Contact your system administrator.");
                  break;
               case AnonymousAccessImpl.PASSWORD_EXPIRED:
                  if (mgr.getASPConfig().isChangePasswordEnabled())
                     message = mgr.translateJavaScript("FNDSCRIPTSLOGONEXPIRED: Password has expired. Change password now.");
                  else
                     message = mgr.translateJavaScript("FNDSCRIPTSLOGONEXPIREDNOTAVAILABLE: Password has expired. Contact your system administrator.");
                  break;
               case AnonymousAccessImpl.FAILURE:
                  message = response_rec.find("ERROR_INFO").getString();
                  break;
            }

         }
         catch (Exception e)
         {
            if (DEBUG) e.printStackTrace(System.out);
            message = e.getMessage();
            status = AnonymousAccessImpl.FAILURE;
         }
         
         mgr.responseWrite(status+"^"+message+"^"); 
      }
      
      mgr.endResponse();
   }
   

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      blk = mgr.newASPBlock("MAIN");
      blk.addField("IN_1").
      setFunction("''").
      setReadOnly().setHidden();  
      blk.setTitle(mgr.translate("FNDSCRIPTSLOGONBLKTIT: Log On"));

      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableMinimize();

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);

      disableHeader();
      disableFooter();

      disableHelp();
      disableNavigate();
      disableOptions();
      disableHomeIcon();
      disableValidation();
   }

   
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDSCRIPTSLOGONTITLE1: IFS Applications";
   }

   protected String getTitle()
   {
      return "FNDSCRIPTSLOGONHEADTITLE: IFS Applications - Log On";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      if ("YES".equals(mgr.getQueryStringValue("EXTERNAL")))
      {
         out.append("<html><head></head>\n");
         out.append("<body></body></html>\n");
         appendDirtyJavaScript("logoutExternal();\n");
         appendDirtyJavaScript("function logoutExternal()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript(mgr.getASPConfig().getLogoffScript()+"\n"); 
         appendDirtyJavaScript("}\n"); 
         return out;
      }
      
      String html = "<table border='0' width='100%' height='90%' cellspacing=0 cellpadding=0 ><tr><td align='center'> <font class=normalTextValue>"
                 + mgr.translateJavaScript("FNDSCRIPTSLOGONINTERMEDIATEMSG: This page was used to redirect you to the Web Client.") + 
                 "</font> </td></tr></table>";
         
      out.append("<!-- Logon Page -->\n");

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDSCRIPTSLOGONHEADTITLE: IFS Applications - Log On"));

      if("Y".equals(mgr.getASPContext().findGlobal("USER_LOGGED_IN")))
      {
         out.append("<script language =\"javascript\" >\n");
         out.append("if(RWC_HOST){ \n");     
         out.append("document.execCommand('Stop'); \n document.clear();\n document.title=\""+ mgr.translateJavaScript("FNDSCRIPTSLOGONINTERMEDIATETITLE: Web Client Synchronization Page")+"\"\n" +
                   " document.write(\""+ html +"\");\n");     
         out.append("} else \n");
         out.append("window.location.replace('"+mgr.getProtocol() + "://" +  mgr.getApplicationDomain()+ mgr.getASPConfig().getApplicationContext()+ "/secured/Default.page'); \n");
         out.append("</script>\n");
      }
 
      if (mgr.isRWCHost()){

         out.append("<script language =\"javascript\" >\n");


         out.append("var rwcLoginCookie = readCookie(\"__FNDWEB_LOGGED_ONCE\");\n");
         out.append("if(rwcLoginCookie == 'true'){\n document.execCommand('Stop');\n document.clear();\n document.title=\""+mgr.translateJavaScript("FNDSCRIPTSLOGONINTERMEDIATETITLE: Web Client Synchronization Page")+"\"\n" +
                 " document.write(\""+ html +"\");\n}\n");
         out.append("</script>\n");
         out.append("</head>\n");
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(" >\n");
         out.append("<form name=\"form\" action=\""+mgr.getApplicationAbsolutePath()+"/j_security_check\" method=post ");

         if (!change_password)
            out.append("OnSubmit=\"return __FNDWEB_validateCredentials()\">\n");
         else
            out.append("OnSubmit=\"return __FNDWEB_changePassword()\">\n");
         
         out.append("<script language =\"javascript\" >\n");
         out.append("checkLogonUrl(); \n");
         out.append("</script>\n");
         mgr.startRWCInterfaceItem("info");
         mgr.addRWCInterfaceValue("logon_page", "Y");
         mgr.endRWCInterfaceItem("info");    
         out.append("<table border='0' width='100%' height='90%' cellspacing=0 cellpadding=0 ><tr><td align='center'><table border='0' cellspacing=0 cellpadding=0 width=10><tr><td>");
         printHiddenField("j_username", "");
         printHiddenField("j_password", "");
         printHiddenField("DOMAIN","");
         printHiddenField("USERID",mgr.isEmpty(mgr.readValue("USERID"))?"":mgr.readValue("USERID"));
         out.append("<img valign=middle align=center border=0 src='",mgr.getASPConfig().getImagesLocation(), "/loading.gif'>");
         printSpaces(2);
         printText("FNDSCRIPTSLOGONLOADINGMSG: loading...");
         out.append(mgr.endPresentation());

         out.append("</td></tr></table></td></tr></table>");
         out.append("\n</form>\n");         
         out.append("</body>\n");
         out.append("</html>\n");
      }
      else{

         out.append("</head>\n");
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(" >\n");

         out.append("<form name=\"form\" action=\""+mgr.getApplicationAbsolutePath()+"/j_security_check\" method=post ");
         if (!change_password)
            out.append("OnSubmit=\"return __FNDWEB_validateCredentials()\">\n");
         else
            out.append("OnSubmit=\"return __FNDWEB_changePassword()\">\n");
  
        out.append("<script language =\"javascript\" >\n");
        out.append("checkLogonUrl(); \n");
        out.append("</script>\n");

         // language list 
         if (mgr.getASPConfig().isMultiLanguageEnabled())
         {
            HashMap lang_table = ASPConfigFile.language_table;
            Iterator it = lang_table.keySet().iterator();

            String lang_code = mgr.getLanguageCode().toUpperCase();
            out.append("<br><table border='0' width='100%' cellspacing=0 cellpadding=0 ><tr><td align='center'>");
            while (it.hasNext())
            {
               String key = (String)it.next();
               if (!key.equals(lang_code))
                  printLink(""+lang_table.get(key), "javascript:setUserLanguage('"+key.toLowerCase()+"')");
               else
                  printText(""+lang_table.get(key));
               appendToHTML(" | ");
            }
            out.append("</td></tr></table>");
         }      
         int field_size = 30;

         if(mgr.isMobileVersion()) 
         {
            field_size = 15;
            out.append("<br>");
            out.append("<table cellpadding=\"1\" width=\"221px\" class=\"pageCommandBar\" align=\"center\">\n");
            out.append("   <tr>\n");
            out.append("      <td>&nbsp;&nbsp;"+getASPManager().translate("FNDSCRIPTSLOGONBLKTIT: Log On")+"</td>\n");
            out.append("   </tr>\n");
            out.append("</table>\n");
            out.append("<table cellpadding=\"1\" width=\"221px\" class=\"pageFormWithBorder\" align=\"center\">\n");
            out.append("   <tr>\n");
            out.append("      <td colspan=\"2\"><img src=\"",mgr.getASPConfig().getImagesLocation(),"CorporateLayerConnectTo.gif\" border=\"0\"></td>\n");
            out.append("   </tr>\n");
            out.append("   <tr>\n");
            out.append("      <td>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONUSERID: User Id:");
            out.append("</td>\n");
            out.append("      <td align=\"right\">");
            printField("USERID",mgr.isEmpty(mgr.readValue("USERID"))?"":mgr.readValue("USERID"),(change_password?"disabled":""),field_size);
            out.append("&nbsp;</td>\n");
            out.append("   </tr>\n");
            out.append("   <tr>\n");
            out.append("      <td>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONPASSWD: Password:");
            out.append("</td>\n");
            out.append("      <td align=\"right\">");
            printPasswordField("PASSWD",mgr.isEmpty(mgr.readValue("PASSWD"))?"":mgr.readValue("PASSWD"),(change_password?"disabled":""),field_size,false);
            out.append("&nbsp;</td>\n");
            out.append("   </tr>\n");        
         }
         else
         {
            field_size = 30;
            out.append("<table border='0' width='100%' height='90%' cellspacing=0 cellpadding=0 ><tr><td align='center'><table border='0' cellspacing=0 cellpadding=0 width=10><tr><td>");
            out.append("<table border=0 cellspacing=0 cellpadding=0 width=321>");
            out.append("<tr><td>");
            drawSimpleCommandBar("FNDSCRIPTSLOGONBLKTIT: Log On");
            out.append("</td></tr>");
            out.append("<tr><td>");
            //form border table
            out.append("<table border=0 cellspacing=0 cellpadding=2 width=321");
            out.append(" class=pageFormWithBorder><tr>\n");
            out.append("    <tr><td colspan=2 align=center><img src='",mgr.getASPConfig().getImagesLocation(),"CorporateLayerConnectTo.png' border=0></td></tr>");
            out.append("  <td nowrap>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONUSERID: User Id:");
            out.append("    </td>");
            out.append("    <td nowrap align=right>");
            printField("USERID",mgr.isEmpty(mgr.readValue("USERID"))?"":mgr.readValue("USERID"),(change_password?"disabled":""),field_size);
            out.append("    &nbsp;</td>");
            out.append("    </tr>");
            out.append("    <tr>");
            out.append("    <td nowrap>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONPASSWD: Password:");
            out.append("    </td>");
            out.append("    <td nowrap align=right>");
            printPasswordField("PASSWD",mgr.isEmpty(mgr.readValue("PASSWD"))?"":mgr.readValue("PASSWD"),(change_password?"disabled":""),field_size,false);
            out.append("    &nbsp;</td>");
            out.append("    </tr>");
         }

         if (change_password)
         {
            out.append("    <tr>");
            out.append("    <td nowrap>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONNEWPASSWD: New Password:");
            out.append("    </td>");
            out.append("    <td nowrap align=right>");
            printPasswordField("NEWPASSWORD","","",field_size,false);
            out.append("    &nbsp;</td>");
            out.append("    </tr>");
            out.append("    <tr>");
            out.append("    <td nowrap>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONCONNEWPASSWD: Confirm New Password:");
            out.append("    </td>");
            out.append("    <td nowrap align=right>");
            printPasswordField("CONFIRMPASSWD","","",field_size,false);
            out.append("    &nbsp;</td>");
            out.append("    </tr>");

            appendDirtyJavaScript("PASSWORD_MISMATCH_MSG ='",mgr.translateJavaScript("FNDSCRIPTSLOGONPSWDNOTEQUAL: The new passwords you typed do not match. Try again."),"';\n");
            appendDirtyJavaScript("PASSWORD_EMPTY_MSG ='",mgr.translateJavaScript("FNDSCRIPTSLOGONNEWINVALID: New password is invalid. Try again"),"';\n");
         }

         ASPBuffer domain_list = mgr.getASPConfig().getLogonDomainList();

         if (domain_list.countItems() > 0)
         {
            out.append("    <tr>");
            out.append("    <td nowrap>&nbsp;");
            printReadLabel("FNDSCRIPTSLOGONDOMAIN: Domain:");
            out.append("    </td>");
            out.append("    <td nowrap align=right>");
            printSelectBox("DOMAIN", domain_list );
            out.append("    &nbsp;</td>");
            out.append("    </tr>");
         }

         if(mgr.isMobileVersion())
         {
            out.append("   <tr>\n");
            out.append("      <td>&nbsp;</td>\n");
            out.append("      <td align=\"right\">");
            printSubmitButton("LOGONBTN", "FNDSCRIPTSLOGONLOGON: Log On","");
            out.append("&nbsp;</td>\n");
            out.append("   </tr>\n");
            out.append("</table>");
         }
         else
         {
            out.append("    <tr><td colspan=2>&nbsp</td></tr>");
            out.append("    <tr><td colspan=2 align=right>");

            printSubmitButton("LOGONBTN", "FNDSCRIPTSLOGONLOGON: Log On","");

            out.append("&nbsp;    </td></tr><tr><td colspan=2><br></td></tr>");
            out.append("   </table>");
            //end form border

            out.append("  </td>\n");
            out.append(" </table></td>\n");


            out.append("</td></tr></table></td></tr></table>");
         }

         printHiddenField("j_username", "");
         printHiddenField("j_password", "");

         out.append(mgr.endPresentation());

         out.append("\n</form>\n");
         out.append("</body>\n");
         out.append("</html>\n");
      }
      
      String url = mgr.getURL() + "?"+ASPManager.PREFERRED_LANGUAGE+"=";
      appendDirtyJavaScript("function setUserLanguage(lang_code){\n"+
                            " writePersistentCookie('__USER_LANGUAGE',lang_code);\n"+
                            " window.location='"+url+"'+lang_code;\n"+
                            "}\n");


      appendDirtyJavaScript("\nfunction __FNDWEB_setLogonAction(){\n" +
                            "\tdocument.form.action = \""+mgr.getURL()+"?CHANGE_PASSWORD=TRUE\"; \n"+
                            "}\n");
      
      appendDirtyJavaScript("\nfunction __FNDWEB_setJAASCredentials(){\n" +
                            " var username = document.form.USERID.value;\n"+
                            " var domain = '';\n"+
                            " if (document.form.DOMAIN){\n"+
                            "   domain = document.form.DOMAIN.value;\n"+
                            "  if (domain != '') username = domain.replace(\"${user}\",username);\n"+
                            " }\n"+
                            " document.form.j_username.value = username;\n"+
                            " document.form.j_password.value = document.form.PASSWD.value;\n"+
                            "}\n");
      
      appendDirtyJavaScript("\nfunction __FNDWEB_setCookies(){\n"+
                            "if (document.form.USERID.value != '')\n" +         
                            "    writePersistentCookie('_FNDWEB_LOGINUSER',document.form.USERID.value)\n" +
                            "if (document.form.DOMAIN)\n" +         
                            "    writePersistentCookie('_FNDWEB_LOGINDOMAIN',document.form.DOMAIN.value)\n" +
                            "}\n");
      appendDirtyJavaScript("\nfunction __FNDWEB_submitForm(){\n"+
                              "__FNDWEB_setJAASCredentials();\n"+
                              "document.form.submit();\n"+
                            "}\n");
      
      if (!change_password)
      {
         appendDirtyJavaScript("var userid = readCookie('_FNDWEB_LOGINUSER');\n");
         appendDirtyJavaScript("if (document.form.USERID.value == '')\n document.form.USERID.value = userid;\n");
         appendDirtyJavaScript("if (document.form.USERID.value != '')\n document.form.__FOCUS.value = 'PASSWD';\n");
         appendDirtyJavaScript("var domain = readCookie('_FNDWEB_LOGINDOMAIN');\n");
         appendDirtyJavaScript("if (document.form.DOMAIN)\n  document.form.DOMAIN.value = domain;\n ");
      }

      if (!mgr.isEmpty(mgr.readValue("USERID")))
      {
        appendDirtyJavaScript("document.form.USERID.value='"+mgr.readValue("USERID")+"';\n");
        appendDirtyJavaScript("document.form.PASSWD.value='"+"1"+"';\n");
        appendDirtyJavaScript("__FNDWEB_submitForm();\n");
      }
      
      appendDirtyJavaScript("function showHelpTag(usageid){}\n");
      
      appendDirtyJavaScript("var __EMPTY_USERID_MSG='"+mgr.translateJavaScript("FNDSCRIPTSLOGONEMPTYUSERID: Enter user id.")+"';\n");
      appendDirtyJavaScript("var __CHANGE_PASSWORD_ENABLED="+mgr.getASPConfig().isChangePasswordEnabled()+";\n");
      appendDirtyJavaScript("var __SUCCESS='"+AnonymousAccessImpl.SUCCESS+"';\n");
      appendDirtyJavaScript("var __INVALID_CREDENTIALS='"+AnonymousAccessImpl.INVALID_CREDENTIALS+"';\n");
      appendDirtyJavaScript("var __ACCOUNT_LOCKED='"+AnonymousAccessImpl.ACCOUNT_LOCKED+"';\n");
      appendDirtyJavaScript("var __PASSWORD_EXPIRED='"+AnonymousAccessImpl.PASSWORD_EXPIRED+"';\n");
      appendDirtyJavaScript("var __FAILURE='"+AnonymousAccessImpl.FAILURE+"';\n");
      appendDirtyJavaScript("var logon_url='"+mgr.getASPConfig().getLogonURL()+"';\n");
      appendDirtyJavaScript("var portal_page='"+mgr.getASPConfig().getProtocol()+"://"+mgr.getASPConfig().getApplicationDomain() + mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL","Default.page")+"';\n");
      
      return out;
   }
  
}
