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

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import oracle.jdbc.OracleResultSet;

import org.apache.lucene.search.FieldComparator.StringOrdValComparator;

import com.horizon.db.Access;

import ifs.fnd.ap.*;
import ifs.genbaw.GenbawConstants;
import ifs.application.anonymousaccess.impl.AnonymousAccessImpl;


public class LogonXDP extends ASPPageProvider
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

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private boolean change_password;
   
   private ASPTransactionBuffer trans;
   private ASPBuffer keys;
   private ASPBuffer data;
   private ASPBuffer bc_Buffer;
   private ASPBuffer trans_Buffer;
   private ASPCommand cmd;
   
   //Translated Messgae begin
   private String lableCopyRight = null;
   private String lableWeatherReport = null;
   private String lableUserLogon = null;
   private String lableBulletin = null;
   private String lableInnerLink = null;
   private String lableMore = null;
   private String lableCOSMIS = null;
   private String lableConsturct = null;
   private String lableNewsList = null;
   private String lableSystemReference = null;
   private String lableProjectPhoto = null;
   private String lableCNPE = null;
   private String lableYourPosition = null;
   private String lableHome = null;
   private String lableDetailmessage = null;
   private String lableUserName = null;
   private String lablePassword = null;
   private String lableLogon = null;
   private String lableTemperature = null;
   private String lablePM25 = null;
   private String lableAttach = null;
   //End.
   
   final String NEWSLIST = "A";
   final String BULLETIN = "B";
   final String CMIS = "C";
   final String SYSTEM_CONSTRUCT= "D";
   final String SYSTEM_REFERENCE = "E";
   final String ROLLING_PICTURE = "F";
   final String LINKS = "G";
   //===============================================================
   // Construction
   //===============================================================
   public LogonXDP(ASPManager mgr, String page_path)
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
      
//      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//      trans.addCustomFunction("Get_Person_Zones", "Person_Zone_Api.Get_Person_Zones", "IN_1");
//      trans.addCustomFunction("Get_Person_Def_Zone", " Person_Zone_Api.Get_Person_Def_Zone", "IN_1");
//      trans.addCustomFunction("Get_Person_Projs", "Person_Project_Api.Get_Person_Projs", "IN_1");
//      trans.addCustomFunction("Get_Person_Def_Proj", "Person_Project_Api.Get_Person_Def_Proj", "IN_1");
//      mgr.submit(trans);
//      
//      String personZones = trans.getValue("Get_Person_Zones/DATA/IN_1");
//      String defaultZone = trans.getValue("Get_Person_Def_Zone/DATA/IN_1");
//      String personProjs = trans.getValue("Get_Person_Projs/DATA/IN_1");
//      String defaultProjs = trans.getValue("Get_Person_Def_Proj/DATA/IN_1");
   }

   private void validateCredentials()
   {
      ASPManager mgr = getASPManager();
      
      String userId = mgr.readValue("USERID");
      String password = mgr.readValue("PASSWD");
      String domain = mgr.readValue("DOMAIN");
      
      String message = "";
      
      if (!mgr.isEmpty(domain))
         userId = Str.replace(domain,"${user}", userId);
      
      String var = mgr.readValue("CREDENTIALS");
      int status = AnonymousAccessImpl.SUCCESS;
      
      if ("CHANGE_PASSWORD".equals(var))
      {
         try
         {
            ManageUserPassword password_manager = ManageUserPassword.getInstance(mgr.getASPConfig());
            password_manager.changePassword(userId,password, mgr.readValue("NEWPASSWORD"), mgr.getASPConfig());
            
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
            Record response_rec = password_manager.checkPassword(userId,password, mgr.getASPConfig());

            status = (int)response_rec.find("STATUS").getLong();
            if (DEBUG) debug("password_manager.checkPassword status:"+status);

            // Added by Terry 20140519
            // Check ip address
            trans.clear();
            cmd = trans.addCustomFunction("CHECKUSERIP", "FND_USER_IP_ADDRESS_API.Check_Ip_Address", "OUT_1");
            cmd.addParameter("IN_1", userId.toUpperCase());
            cmd.addParameter("IN_1", getIpAddress());
            trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
            trans = mgr.validate(trans);
            if ("FALSE".equals(trans.getValue("CHECKUSERIP/DATA/OUT_1")))
            {
               status = AnonymousAccessImpl.FAILURE;
               message = mgr.translateJavaScript("FNDSCRIPTSLOGONUSERIPADDRESSILLEGAL: The IP address of current user is illegal, contact your system administrator.");
            }
            // Added end
            
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
                  }
                  finally {
                     ASPContext ctx =  mgr.getASPContext();
                     trans.clear();
                     cmd = trans.addCustomFunction( "GETUSERPROJS", "PERSON_PROJECT_API.Get_User_Projs", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERDEFPROJ", "PERSON_PROJECT_API.Get_User_Def_Proj", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERZONES", "PERSON_ZONE_API.Get_User_Zones", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERDEFZONE", "PERSON_ZONE_API.Get_User_Def_Zone", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERNAME", "PERSON_INFO_API.Get_Name", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     
                     cmd = trans.addCustomFunction( "GETACCURATEDEFDEPT", "GENERAL_ORG_POS_PERSON_API.Get_User_Def_Dept", "IN_1");
                     cmd.addParameter("IN_1", userId.toUpperCase());
                     cmd.addParameter("IN_1", "TRUE");
                     
                     cmd = trans.addCustomFunction( "GETDEFDEPT", "GENERAL_ORG_POS_PERSON_API.Get_User_Def_Dept", "IN_1" );
                     cmd.addParameter("IN_1", userId.toUpperCase());
                     cmd.addParameter("IN_1", "FALSE");
                     
                     cmd = trans.addCustomFunction( "GETPERSONID", "Person_Info_API.Get_Id_For_User", "IN_1" );
                     cmd.addParameter("IN_1", userId.toUpperCase());
                     
                     
                     cmd = trans.addCustomFunction( "GETUSERDEFDEPT", "GENERAL_ORG_POS_PERSON_API.GET_USER_DEF_DEPT", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     cmd = trans.addCustomFunction( "GETUSERDEFDEPTNAME", "GENERAL_ORG_POS_PERSON_API.GET_PERSON_DEF_DEPT_DESC", "IN_1" );
                     cmd.addParameter("IN_1",userId.toUpperCase());
                     
                     trans.addRequestHeader(userId.toUpperCase());
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
                     String accurate_def_dept = tempFilter.filterNull(trans.getValue("GETACCURATEDEFDEPT/DATA/IN_1"));
                     String def_dept = tempFilter.filterNull(trans.getValue("GETDEFDEPT/DATA/IN_1"));
                     String person_id = tempFilter.filterNull(trans.getValue("GETPERSONID/DATA/IN_1"));
                     
                     String userDefaultDept = tempFilter.filterNull(trans.getValue("GETUSERDEFDEPT/DATA/IN_1"));
                     String userDefaultDeptDesc = tempFilter.filterNull(trans.getValue("GETUSERDEFDEPTNAME/DATA/IN_1"));
                      
                     
                     ctx.setGlobal("HZ_SESSION_USER_ID", userId.toUpperCase());
                     ctx.setGlobal("HZ_SESSION_USER_NAME", userName);
                     ctx.setGlobal("HZ_SESSION_LOGIN_NAME", userId.toUpperCase());
                     ctx.setGlobal("HZ_SESSION_DEPT_ID", "root_node_id");
                     ctx.setGlobal("HZ_SESSION_DEPT_NAME", "HUIZHENG");
                     ctx.setGlobal("appcode", "system");
                     ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_PROJECT, userDefaultProj);
                     ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_ZONE, userDefaultZone);
                     ctx.setGlobal(GenbawConstants.PERSON_PROJECTS, userProjs);
                     ctx.setGlobal(GenbawConstants.PERSON_ZONES, userZones);
                     ctx.setGlobal(GenbawConstants.PERSON_DEFAULT_DEPT, def_dept);
                     ctx.setGlobal(GenbawConstants.PERSON_ACCURATE_DEFAULT_DEPT, accurate_def_dept);
                     ctx.setGlobal(GenbawConstants.PERSON_ID, person_id);
                     ctx.setGlobal(GenbawConstants.PERSON_DEFALUT_DEPT, userDefaultDept);
                     ctx.setGlobal(GenbawConstants.PERSON_DEFALUT_DEPT_DESC, userDefaultDeptDesc);
                     
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_PROJECT + ":" + userDefaultProj);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFAULT_ZONE + ":" + userDefaultZone);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_PROJECTS + ":" + userProjs);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_ZONES + ":" + userZones);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFALUT_DEPT + ":" + userDefaultDept);
                     System.out.println("DataIsolation*****" + GenbawConstants.PERSON_DEFALUT_DEPT_DESC + ":" + userDefaultDeptDesc);
                     
                     System.out.println("set hz user info sucess............" + userId.toUpperCase());
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
                  if (Str.isEmpty(message))
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
   
   private String getIpAddress()
   {
      ASPManager mgr = getASPManager();
      HttpServletRequest request = mgr.getAspRequest();
      
      String ip = request.getHeader("X-Forwarded-For");
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
      {
         ip = request.getHeader("Proxy-Client-IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
      {
         ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
      {
         ip = request.getHeader("HTTP_CLIENT_IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
      {
         ip = request.getHeader("HTTP_X_FORWARDED_FOR");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
      {
         ip = request.getRemoteAddr();
      }
      return ip;
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      blk = mgr.newASPBlock("MAIN");
      blk.addField("IN_1").
      setFunction("''").
      setReadOnly().setHidden();

      blk.addField("OUT_1").
      setFunction("''").
      setHidden();
      
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
   
   public void generateHeadTag(AutoString temp )
   {

      temp.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");

   }
   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      
      lableCopyRight = mgr.translate("LOGONLABLECOPYRIGHT:  Copyright");
      lableWeatherReport = mgr.translate("LOGONLABLEWEATHERREPORT: Weather");
      lableUserLogon = mgr.translate("LOGONLABLEUSERLOGON: Logon");
      lableBulletin = mgr.translate("LOGONLABLEBULLETIN: Bulletin");
      lableInnerLink = mgr.translate("LOGONLABLEINNERLINK: Links");
      lableMore = mgr.translate("LOGONLABLEMORE: More");
      lableCOSMIS = mgr.translate("LOGONLABLECOSMIS: COSMIS");
      lableConsturct = mgr.translate("LOGONLABLECONSTURCT: Consturct");
      lableNewsList = mgr.translate("LOGONLABLENEWSLIST: News");
      lableSystemReference = mgr.translate("LOGONLABLESYSTEMREFERENCE: Reference");
      lableProjectPhoto = mgr.translate("LOGONLABLEPROJECTPHOTO: Photo");
      lableCNPE = mgr.translate("LOGONLABLECNPE: China Nuclear Power Engineering Co., Ltd.");//中国核电工程有限公司
      lableYourPosition = mgr.translate("LOGONLABLEYOURPOSITION: Position");
      lableHome = mgr.translate("LOGONLABLEFRONTPAGE: Home");
      lableDetailmessage = mgr.translate("LOGONLABLEDETAILMESSAGE: Details");
      lableUserName = mgr.translate("LOGONLABLEUSERNAME: User");
      lablePassword = mgr.translate("LOGONLABLEPASSWORD: Password");
      lableLogon = mgr.translate("LOGONLABLELOGON: Logon");
      lableTemperature = mgr.translate("LOGONLABLETEMPERATURE: Temperature");
      lablePM25= mgr.translate("LOGONLABLEPM25: PM2.5");
      lableAttach= mgr.translate("LOGONLABLEATTACH: Attach");
      
      String commandFlag = mgr.readValue("commandFlag");
      if("logonNewsDetail".equals(commandFlag)){
          getLogonNewsDetail(out);
          return out;
      }else if("getLogonMoreNews".equals(commandFlag)){
         getLogonMoreNews(out);
         return out;
     }
      
      
      

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
      
      out.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
      out.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
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
         out.append("<body style=\"background-image:url(/b2e/unsecured/common/images/logon_bg.gif);background-repeat:no-repeat; background-position:center\" ");
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
         //====
         out.append("    <style type=\"text/css\">\n");
         out.append("         .pageBody {\n");
         out.append("            background-color: #E6E6E6;");
         out.append("         }\n");
         out.append("         span.logon-lable {\n");
         out.append("             font-size:22px;  font-family:\"微软雅黑\",Georgia,Serif; color: #eee;");
         out.append("         }\n");
         out.append("         .editableTextField {\n");
         out.append("             font-size:16px;");
         out.append("         }\n");
         out.append("         .logonButtonZhNew {\n");
         out.append("             background: url(/b2e/unsecured/common/images/fndli/go-zh.gif) no-repeat;");
         out.append("             border: 0px;");
         out.append("             cursor: pointer;");
         out.append("             width:56px;");
         out.append("             height:24px;");
         out.append("         }\n");
         out.append("         .logonButtonEnNew {\n");
         out.append("             background: url(/b2e/unsecured/common/images/fndli/go-en.gif) no-repeat;");
         out.append("             border: 0px;");
         out.append("             cursor: pointer;");
         out.append("             width:56px;");
         out.append("             height:24px;");
         out.append("         }\n");  
         out.append("    </style>\n"); 
         out.append("<!-- -->\n");
         
         
         out.append("<link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/fndli.css' type='text/css'>\n");
         
         
         out.append("<script src=\"/b2e/unsecured/common/scripts/fndli.js\" type=text/javascript></script>\n");
         //====
         
         
         out.append("</head>\n");
         out.append("<body style=\"background-image:url(/b2e/unsecured/common/images/logon_bg.gif);background-repeat:no-repeat;\" topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" class=\"pageBody\" OnLoad=\"javascript:onLoad()\" OnFocus=\"javascript:OnFocus()\" OnUnLoad=\"javascript:OnUnLoad()\" onKeyPress=\"keyPressed()\">\n");
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
            out.append("<table border='0' width='100%' cellspacing=0 cellpadding=0 ><tr><td align='center'>");
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
            getLogonPage(out);
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
            
         }

         printHiddenField("j_username", "");
         printHiddenField("j_password", "");

         out.append(mgr.endPresentation());

         out.append("\n</form>\n");

//         out.append("  <iframe width=525 height=280 name=aa frameborder=0 scrolling=no  src=../../hzwflw/HzBizWfTest.page></iframe>");
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
      if (!change_password)
      {
         appendDirtyJavaScript("var userid = readCookie('_FNDWEB_LOGINUSER');\n");
         appendDirtyJavaScript("if (document.form.USERID.value == '')\n document.form.USERID.value = userid;\n");
         appendDirtyJavaScript("if (document.form.USERID.value != '')\n document.form.__FOCUS.value = 'PASSWD';\n");
         appendDirtyJavaScript("var domain = readCookie('_FNDWEB_LOGINDOMAIN');\n");
         appendDirtyJavaScript("if (document.form.DOMAIN)\n  document.form.DOMAIN.value = domain;\n ");
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
   
   

   
   

   public void getLogonPage(AutoString out) throws FndException{
      ASPManager mgr = getASPManager();
      out.append("                <div class=\"cenl_tet\" style=\"margin-left: 50px;margin-top: 550px;\">\n");
      out.append("                  <table  border=\"0\" style=\"float:left;\">\n");
      out.append("                      <tr>\n");
      out.append("                        <td width=\"30%\"><div align=\"right\"><span class=\"logon-lable\">" + lableUserName + "</span></div></td>\n");
      out.append("                        <td width=\"35%\"><input type=\"text\" name=\"USERID\"  size=\"15\" class=\"editableTextField\" /></td>\n");
      out.append("                        <td width=\"35%\">&nbsp;</td>\n");
      out.append("                      </tr>\n");
      out.append("                      <tr>\n");
      out.append("                        <td><div align=\"right\"><span class=\"logon-lable\">" + lablePassword + "</span></div></td>\n");
      out.append("                        <td><input type=\"password\" name=\"PASSWD\" size=\"15\" class=\"editableTextField\"/></td>\n");
      
      String languageCode =  mgr.getLanguageCode();
      String logonButtonClass = "logonButton";
      
      if("zh".equals(languageCode)){
         logonButtonClass = "logonButtonZhNew";
      }else{
         logonButtonClass = "logonButtonEnNew";
      }
      
      out.append("                        <td><span align=\"left\"><input type=\"submit\" class=\""+logonButtonClass+"\" name=\"LOGONBTN\" value=\"" + "" + "\"/></span></td>\n");
      System.out.println(languageCode);
      out.append("                      </tr>\n");
      out.append("                    </table>\n");
      out.append("                </div>\n");
      
//      out.append("    <div class=\"foot\">"+lableCopyRight+" &copy; 2009 - 2020 "+lableCNPE+" </div>\n");
   }
   
   public void getLogonInfo(AutoString out) throws FndException{
      StringBuilder sqlLogonInfo = new StringBuilder();
      sqlLogonInfo.append(" Select TT.INFO_ID INFO_ID,");
      sqlLogonInfo.append(" TT.INFO_TITLE INFO_TITLE,");
      sqlLogonInfo.append(" TT.INFO_CONTENT INFO_CONTENT,");
      sqlLogonInfo.append(" TT.INFO_DOC_ADDRESS INFO_DOC_ADDRESS,");
      sqlLogonInfo.append(" TT.INFO_DATE INFO_DATE,");
      sqlLogonInfo.append(" TT.INFO_OWNER INFO_OWNER,");
      sqlLogonInfo.append(" TT.INFO_COLOR INFO_COLOR,");
      sqlLogonInfo.append(" TT.INFO_VISIBLE INFO_VISIBLE,");
      sqlLogonInfo.append(" TT.INFO_TYPE INFO_TYPE,");
      sqlLogonInfo.append(" TT.INFO_TYPE_DB INFO_TYPE_DB ");
      sqlLogonInfo.append(" FROM (SELECT *  From LOGON_INFO T WHERE T.info_type_db = ? AND T. INFO_VISIBLE='TRUE' ORDER BY T.info_date DESC ) TT");
      sqlLogonInfo.append(" WHERE ROWNUM < 11");
      
      StringBuilder sqlLogonInfo2 = new StringBuilder();
      sqlLogonInfo2.append(" Select TT.INFO_ID INFO_ID,");
      sqlLogonInfo2.append(" TT.INFO_TITLE INFO_TITLE,");
      sqlLogonInfo2.append(" TT.INFO_CONTENT INFO_CONTENT,");
      sqlLogonInfo2.append(" TT.INFO_DOC_ADDRESS INFO_DOC_ADDRESS,");
      sqlLogonInfo2.append(" TT.INFO_DATE INFO_DATE,");
      sqlLogonInfo2.append(" TT.INFO_OWNER INFO_OWNER,");
      sqlLogonInfo2.append(" TT.INFO_COLOR INFO_COLOR,");
      sqlLogonInfo2.append(" TT.INFO_VISIBLE INFO_VISIBLE,");
      sqlLogonInfo2.append(" TT.INFO_TYPE INFO_TYPE,");
      sqlLogonInfo2.append(" TT.INFO_TYPE_DB INFO_TYPE_DB ");
      sqlLogonInfo2.append(" FROM (SELECT *  From LOGON_INFO T WHERE T.info_type_db = ? AND T.INFO_VISIBLE='TRUE' ORDER BY T.info_date DESC ) TT");
      sqlLogonInfo2.append(" WHERE ROWNUM < 6");
      
      
      ASPManager mgr = getASPManager();
      String destination = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext()+"/fndlw/upload/";
      out.append("<div class=\"main\">\n");
      out.append("<div class=\"bg\">\n");
      out.append("   <div class=\"top\"><img src=\"/b2e/unsecured/common/images/fndli/banner.jpg\" width=\"931\" height=\"86\" /></div>\n");
      out.append("    <div class=\"cen\">\n");
      out.append("      <div class=\"cen_left\">\n");
      out.append("         <div class=\"cenl\">\n");
      out.append("               <div class=\"cenl_tit\">"+lableWeatherReport+"</div>\n");
      out.append("                <div class=\"cenl_tet\" style=\"height:55px;\">\n");
      out.append("                      \n");
      
      StringBuilder sqlWeather = new StringBuilder();
      sqlWeather.append("    select  to_char(t.WEATHER_DATE,'yyyy-mm-dd') WEATHER_DATE,");
      sqlWeather.append("            to_char(t.WEATHER_DATE,'day')  WEATHER_WEEK,");
      sqlWeather.append("            t.TEMPERATURE TEMPERATURE,");
      sqlWeather.append("            t.WEATHER_SITUATION WEATHER_SITUATION,");
      sqlWeather.append("            t.WEATHER_SITUATION_DB WEATHER_SITUATION_DB,");
      sqlWeather.append("            t.PM25 PM25 ");
      sqlWeather.append("     from(  Select tt.*");
      sqlWeather.append("            From LOGON_WEATHER tt where tt.weather_date <= sysdate");
      sqlWeather.append("            order by tt.weather_date desc) t");
      sqlWeather.append("     where rownum < 2");
    String weatherDate="";
    String weatherWeek="";
    String temperatue="";
    String weatherSituation="";
    String weatherSituationDb = "";
    String pm25="";
    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
    ASPQuery q = trans.addQuery("SQLWEATHER", sqlWeather.toString());
    trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
    trans = mgr.perform(trans);
    ASPBuffer buffer = trans.getBuffer("SQLWEATHER");
    int count = buffer.countItems();
    for (int i = 0; i < count; i++)
    {
       if("DATA".equals(buffer.getNameAt(i)))
       {
          weatherDate = buffer.getBufferAt(i).getValue("WEATHER_DATE");
          weatherWeek = buffer.getBufferAt(i).getValue("WEATHER_WEEK");
          temperatue = buffer.getBufferAt(i).getValue("TEMPERATURE");
          weatherSituation = buffer.getBufferAt(i).getValue("WEATHER_SITUATION");
          weatherSituationDb = buffer.getBufferAt(i).getValue("WEATHER_SITUATION_DB");
          pm25 = buffer.getBufferAt(i).getValue("PM25");
          out.append("<div class=\"cenl_date\">"+weatherDate+"&nbsp;&nbsp;&nbsp;"+weatherWeek+"</div>");
          out.append("                    <div class=\"cenl_tq\">\n");
          
          out.append("<div class=\"tq_left\"><img src=\"/b2e/unsecured/common/images/fndli/weather/"+ weatherSituationDb+".gif\" title=\"" + weatherSituation + "\"width=\"25\" height=\"25\" /></div>");
          out.append("                        <div class=\"tq_right\" style='padding:5px;'>\n");
          out.append("                           <p>" + lableTemperature + "："+temperatue+"&#176;C</p>\n");
//          out.append("                            <p>" + lablePM25 + "："+pm25+"</p>\n");
          out.append("                        </div>\n");
          out.append("                        </div>\n");
       }
    }
      
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cenl\">\n");
      out.append("               <div class=\"cenl_tit\">"+lableUserLogon+"</div>\n");
      out.append("                <div class=\"cenl_tet\" style=\"padding:10px 0px;\">\n");
      out.append("                  <table width=\"100%\" border=\"0\" style=\"float:left;\">\n");
      out.append("                      <tr>\n");
      out.append("                        <td width=\"30%\"><div align=\"right\">" + lableUserName + "</div></td>\n");
      out.append("                        <td width=\"35%\"><input type=\"text\" name=\"USERID\"  size=\"15\" class=\"editableTextField\" /></td>\n");
      out.append("                        <td width=\"35%\">&nbsp;</td>\n");
      out.append("                      </tr>\n");
      out.append("                      <tr>\n");
      out.append("                        <td><div align=\"right\">" + lablePassword + "</div></td>\n");
      out.append("                        <td><input type=\"password\" name=\"PASSWD\" size=\"15\" class=\"editableTextField\"/></td>\n");
      
      String languageCode =  mgr.getLanguageCode();
      String logonButtonClass = "logonButton";
      
      if("zh".equals(languageCode)){
         logonButtonClass = "logonButtonZh";
      }else{
         logonButtonClass = "logonButton";
      }
      
      out.append("                        <td><input type=\"submit\" class=\""+logonButtonClass+"\" name=\"LOGONBTN\" value=\"" + "" + "\"/></td>\n");
     System.out.println(languageCode);
      out.append("                      </tr>\n");
      out.append("                    </table>\n");
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("     \n");
      out.append("      \n");
      out.append("            <div class=\"cenl\">\n");
      out.append("               <div class=\"cenl_tit\">\n");
      out.append("                  <div class=\"c_left\">"+lableBulletin+"</div>\n");
      out.append("                    <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + BULLETIN +"\" target=\"_blank\" >"+lableMore+" >></a></div>\n");
      out.append("                </div>\n");
      out.append("\n");
      out.append("                    <div class=\"cenl_tet\" style=\"width:80%; padding:10px 10%;\">\n");
      out.append("                    <div id=\"demo\" class=\"scrollLeft\">\n");
      out.append("                     <div id=\"demo1\" class=\"demo1\">\n");
      out.append("     \n");
      
      String infoId = null;
      String infoTitle = null;
      String infoContent = null;
      String infoDocAddress = null;
      String infoDate = null;
      String infoVisible = null;
      String infoColor = null;
      String infoLink = null;
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1",BULLETIN);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            infoColor = buffer.getBufferAt(i).getValue("INFO_COLOR");
            out.append("<div class='yqlj_li'><a href='"+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"' target=\"_blank\" ><font color='"+("TRUE".equals(infoColor) ? "red": "#000")+"'>"+infoTitle+"</font></a></div>");
            
         }
      }
      out.append("         \n");
      out.append("          \n");
      out.append("                        \n");
      out.append("                                             \n");
      out.append("                     </div>\n");
      out.append("                     <div id=\"demo2\" class=\"demo2\"></div>\n");
      out.append("                    </div>\n");
      out.append("                  <!---->\n");
      
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cenl\">\n");
      out.append("               <div class=\"cenl_tit\">\n");
      out.append("                  <div class=\"c_left\">" + lableInnerLink + "</div>\n");
      out.append("                    <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + LINKS +"\" target=\"_blank\" >"+lableMore+" >></a></div>\n");
      out.append("                </div>\n");
      out.append("\n");
      out.append("                <div class=\"cenl_tet\" style=\"width:80%; padding:10px 10%; height:120px;\">\n");
      
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo2.toString());
      q.addParameter("IN_1",LINKS);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            infoColor = buffer.getBufferAt(i).getValue("INFO_COLOR");
            out.append("<div class='yqlj_li'><a href='"+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"' target=\"_blank\" ><font color='"+("TRUE".equals(infoColor) ? "red": "#000")+"'>"+infoTitle+"</font></a></div>");
         }
      }
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("        </div>\n");
      out.append("        <div class=\"cen_right\">\n");
      out.append("         \n");
      out.append("            <div class=\"cenr\">\n");
      out.append("               <div class=\"cenr_tit\">\n");
      out.append("                  <div class=\"c_left\">"+lableNewsList+"</div>\n");
      out.append("                    <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + NEWSLIST +"\" target=\"_blank\">"+lableMore+" >></a></div>\n");
      out.append("                </div>\n");
      out.append("\n");
      out.append("                <div class=\"cenr_tet\" style=\"height:126px; overflow:hidden;\">\n");
      out.append("<DIV class=newsMain>\n");
      out.append("<DIV class=topNewsBox>\n");
      out.append("<DIV class=topNews>\n");
      out.append("<DIV class=topNewsPic>\n");
      out.append("<TABLE>\n");
      out.append("  <TBODY>\n");
      out.append("   <TR>\n");
      out.append("    <TD id=HotSearchList style=\"FILTER: progid:DXImageTransform.Microsoft.GradientWipe(GradientSize=0.25,wipestyle=0,motion=forward)\">\n");
      out.append("      \n");
      
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1", NEWSLIST);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            out.append("<DIV id=switch_"+i+"><A onclick=setClick(); href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\" blockid=\"931\" target=\"_blank\"><IMG alt=\""+infoTitle+"\" src=\"" + destination + infoDocAddress + "\"></A></DIV>");
         }
      }
      
      out.append("                \n");
      out.append("      \n");
      out.append("      </TD>\n");
      out.append("    </TR>\n");
      out.append("  </TBODY>\n");
      out.append("</TABLE>\n");
      out.append("</DIV>\n");
      out.append("<DIV class=topNewsList>\n");
      out.append("<UL>\n");
      out.append("      \n");
      
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            out.append("<LI><A  id=focus_"+i+" onmouseover=show_focus_image("+i+"); onclick=setClick(); href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\" target=\"_blank\" blockid=\"931\" title=\""+infoTitle+"\">"+infoTitle+"</A></LI>");
         }
      }
      
      
      out.append("           \n");
      out.append("  \n");
      out.append("</UL>\n");
      out.append("</DIV></DIV></DIV>\n");
      out.append("<SCRIPT>\n");
      out.append("<!--\n");
      out.append("   function cleanallstyle() {\n");
      out.append("      for (i=0;i<4;i++) {\n");
      out.append("         document.getElementById(\"focus_\"+i).className = \"\" ;\n");
      out.append("      }\n");
      out.append("   }\n");
      out.append("   function show_focus_image(index) {\n");
      out.append("      clearTimeout(refreshHotQueryTimer);\n");
      out.append("      setHotQueryList(index);\n");
      out.append("      refreshHotQueryTimer = setTimeout('refreshHotQuery();', 5000);\n");
      out.append("   }\n");
      out.append("   function setClick() {\n");
      out.append("      clearTimeout(refreshHotQueryTimer);\n");
      out.append("   }\n");
      out.append("   var refreshHotQueryTimer = null ;\n");
      out.append("   var hot_query_td =  document.getElementById('HotSearchList');\n");
      out.append("   setHotQueryList(CurrentHotScreen);\n");
      out.append("   refreshHotQueryTimer = setTimeout('refreshHotQuery();', 5000);\n");
      out.append("-->\n");
      out.append("</SCRIPT>\n");
      out.append("</div></div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cenr\" style=\"border:0px;width:686px; overflow:hidden;\">\n");
      out.append("               <div class=\"cenrl\" style=\"margin:0px;\">\n");
      out.append("                  <div class=\"cenrl_tit\">\n");
      out.append("                    <div class=\"c_left\">"+lableCOSMIS+"</div>\n");
      out.append("                    <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + CMIS +"\" target=\"_blank\">"+lableMore+" >></a></div>\n");
      out.append("                    </div>\n");
      out.append("                  <div class=\"cenrl_tet\" style=\"height:275px;\">\n");
      out.append("   \n");
      
      
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1", CMIS);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            out.append("<div class=\"cer_li\"><a href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\" target=\"_blank\" title=\""+infoTitle+"\">"+infoTitle+"</a></div>");
         }
      }
                
      out.append("         \n");
      out.append("          \n");
      out.append("                     \n");
      out.append("                    </div>\n");
      out.append("                </div>\n");
      out.append("                <div class=\"cenrl\">\n");
      out.append("                  <div class=\"cenrl_tit\">\n");
      out.append("                     <div class=\"c_left\">"+lableConsturct+"</div>\n");
      out.append("                     <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + SYSTEM_CONSTRUCT +"\" target=\"_blank\">"+lableMore+" >></a></div>\n");
      out.append("                    </div>\n");
      out.append("\n");
      out.append("                  <div class=\"cenrl_tet\" style=\"height:275px;\">\n");
      out.append("     \n");
      
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1", SYSTEM_CONSTRUCT);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            out.append("<div class=\"cer_li\"><a href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\"  target=\"_blank\" title=\""+infoTitle+"\">"+infoTitle+"</a></div>");
         }
      }
      
      out.append("         \n");
      out.append("          \n");
      out.append("                     \n");
      out.append("                    </div>\n");
      out.append("                </div>\n");
      out.append("                <div class=\"cenrl\" style=\"float:right;\">\n");
//      out.append("                  <div class=\"cenrl_tit\">"+lableSystemReference+"</div>\n");
      out.append("                  <div class=\"cenrl_tit\">\n");
      out.append("                     <div class=\"c_left\">"+lableSystemReference+"</div>\n");
      out.append("                     <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + SYSTEM_REFERENCE +"\" target=\"_blank\">"+lableMore+" >></a></div>\n");
      out.append("                    </div>\n");
      out.append("\n");
      out.append("                  <div class=\"cenrl_tet\" style=\"height:275px;\">\n");
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1", SYSTEM_REFERENCE);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            out.append("<div class=\"cer_li\"><a href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\"  target=\"_blank\" title=\""+infoTitle+"\">"+infoTitle+"</a></div>");
//            out.append("<div class=\"cer_li\"><a href=\""+destination + infoDocAddress +"\"  title=\""+infoTitle+"\">"+infoTitle+"</a></div>");
         }
      }
      
      
      out.append("                    </div>\n");
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cenr\">\n");
      out.append("               <div class=\"cenr_tit\">\n");
      out.append("                  <div class=\"c_left\">"+lableProjectPhoto+"</div>\n");
      out.append("                    <div class=\"c_right\"><a href=\""+mgr.getURL() + "?commandFlag=getLogonMoreNews&infoType=" + ROLLING_PICTURE +"\" target=\"_blank\">"+lableMore+" >></a></div>\n");
      out.append("                </div>\n");
      out.append("\n");
      out.append("                <div class=\"cenr_tet\" style=\"width:100%; padding:5px 0px;\">\n");
      out.append("<DIV class=rollphotos>\n");
      out.append("<DIV class=blk_29>\n");
      out.append("<DIV class=Cont id=ISL_Cont_1>\n");
      out.append("<!-- 图片列表 begin -->\n");
      out.append("   \n");
      out.append("            \n");
      
      
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1", ROLLING_PICTURE);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
//            out.append("<DIV class=box><A class=imgBorder href=\""+"#"+"\" target=_blank><IMG alt=\""+infoTitle+"\"height=124 src=\""+destination+infoDocAddress +"\" width=162 border=0></A></DIV>");
            out.append("<div class=\"box\"><a class=imgBorder  href=\""+mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\"  target=\"_blank\" title=\""+infoTitle+"\"><IMG alt=\""+infoTitle+"\"height=124 src=\""+destination+infoDocAddress +"\" width=162 border=0></a></div>");
         }
      }

      out.append("          \n");
      out.append("\n");
      out.append("<!-- 图片列表 end -->\n");
      out.append("</DIV>\n");
      out.append("</DIV>\n");
      out.append("\n");
      out.append("\n");
      out.append("<SCRIPT language=javascript type=text/javascript>\n");
      out.append("      <!--//--><![CDATA[//><!--\n");
      out.append("      var scrollPic_02 = new ScrollPic();\n");
      out.append("      scrollPic_02.scrollContId   = \"ISL_Cont_1\"; //内容容器ID\n");
      out.append("      scrollPic_02.arrLeftId      = \"LeftArr\";//左箭头ID\n");
      out.append("      scrollPic_02.arrRightId     = \"RightArr\"; //右箭头ID\n");
      out.append("\n");
      out.append("      scrollPic_02.frameWidth     = 673;//显示框宽度\n");
      out.append("\n");
      out.append("      scrollPic_02.pageWidth      = 1; //翻页宽度\n");
      out.append("\n");
      out.append("      scrollPic_02.speed          = 0.1; //移动速度(单位毫秒，越小越快)\n");
      out.append("      scrollPic_02.space          = 0.1; //每次移动像素(单位px，越大越快)\n");
      out.append("      scrollPic_02.autoPlay       = true; //自动播放\n");
      out.append("      scrollPic_02.autoPlayTime   = 0.01; //自动播放间隔时间(秒)\n");
      out.append("\n");
      out.append("      scrollPic_02.initialize(); //初始化\n");
      out.append("\n");
      out.append("                     \n");
      out.append("      //--><!]]>\n");
      out.append("</SCRIPT>\n");
      out.append("                </div>\n");
      out.append("            </div>\n");
      out.append("            \n");
      out.append("        </div>\n");
      out.append("    </div>\n");
      out.append("    <div class=\"foot\">"+lableCopyRight+" &copy; 2009 - 2020 "+lableCNPE+" </div>\n");
      out.append("</div>\n");
      out.append("</div>\n");
   }
   
   public void getLogonMoreNews(AutoString out) throws FndException{
      ASPManager mgr = getASPManager();
      int currentPageNum = 1;
      int pageSize = 20;
      int totalPages = 1 ; 
      int resultCount = -1 ; 
      
      String infoId = null;
      String infoTitle = null;
      String infoContent = null;
      String infoDocAddress = null;
      String infoDate = null;
      String infoOwner = null;
      String infoVisible = null;
      String infoColor = null;
      String infoType = mgr.readValue("infoType");
      
      String strCurrentPageNum = mgr.readValue("currentPageNum","1");
      currentPageNum = Integer.valueOf(strCurrentPageNum);
      int pageBeginNum = pageSize*(currentPageNum-1);
      int pageEndNum = currentPageNum*pageSize;
      
      
      
      final String NEWSLIST = "A";
      final String BULLETIN = "B";
      final String CMIS = "C";
      final String SYSTEM_CONSTRUCT= "D";
      final String SYSTEM_REFERENCE = "E";
      final String ROLLING_PICTURE = "F";
      
      StringBuilder sqlLogonInfo = new StringBuilder();
      sqlLogonInfo.append(" Select TT.INFO_ID INFO_ID,");
      sqlLogonInfo.append(" TT.INFO_TITLE INFO_TITLE,");
      sqlLogonInfo.append(" TT.INFO_CONTENT INFO_CONTENT,");
      sqlLogonInfo.append(" TT.INFO_DOC_ADDRESS INFO_DOC_ADDRESS,");
      sqlLogonInfo.append(" to_char(TT.INFO_DATE,'yyyy-mm-dd')  INFO_DATE,");
      sqlLogonInfo.append(" TT.INFO_OWNER INFO_OWNER,");
      sqlLogonInfo.append(" TT.INFO_COLOR INFO_COLOR,");
      sqlLogonInfo.append(" TT.INFO_VISIBLE INFO_VISIBLE,");
      sqlLogonInfo.append(" TT.INFO_TYPE INFO_TYPE,");
      sqlLogonInfo.append(" TT.INFO_TYPE_DB INFO_TYPE_DB ");
      sqlLogonInfo.append("  FROM (SELECT ROWNUM SROWNUM,S.*");
      sqlLogonInfo.append("  FROM (SELECT *  From LOGON_INFO T WHERE T.info_type_db = ? AND T. INFO_VISIBLE='TRUE' ORDER BY T.info_date DESC ) S");
      sqlLogonInfo.append("  WHERE ROWNUM <= ?) TT");
      sqlLogonInfo.append("  WHERE TT.SROWNUM>?");
      
      String destination = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext()+"/fndlw/upload/";
      

      
      out.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
      out.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
      out.append("   <head>\n");
      out.append("      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/styles-theme1.css' type='text/css'>\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/Styles-en.css' type='text/css'>\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/fndli.css' type='text/css'>\n");
      out.append("         <title>IFS Applications - Log On</title>\n");
      out.append("               <!-- ASPPage.getHeadTag() -->\n");
      out.append("         <script language=\"JavaScript\">\n");
      out.append("          APP_PATH = \"/b2e/unsecured\";\n");
      out.append("          APP_ROOT=\"/b2e/unsecured/\";\n");
      out.append("          COOKIE_PATH=\"/b2e/\";\n");
      out.append("          COOKIE_ROOT_PATH=\"\";\n");
      out.append("          COOKIE_PREFIX=\"\";\n");
      out.append("          COOKIE_DOMAIN=\"\";\n");
      out.append("          THEME=\"theme1/\";\n");
      out.append("          STD_PORTLET=false;\n");
      out.append("          IS_RTL=false;\n");
      out.append("          </script>\n");
      out.append("          <script language=\"javascript\"  src=\"/b2e/unsecured/common/scripts/genericclientscript.js\"></script>\n");
      out.append("          <script language=\"javascript\"  src=\"/b2e/unsecured/common/scripts/fndli.js\"></script>\n");
      out.append("          <style type=\"text/css\">\n");
      out.append("               .pageBody {\n");
      out.append("                  background-color: #E6E6E6; }\n");
      out.append("               .newsContainer {\n");
      out.append("                 padding-left: 30px;\n");
      out.append("               }\n");
      out.append("               .author {\n");
      out.append("                 display: none;\n");
      out.append("               }\n");
      out.append("               .date {\n");
      out.append("                 color: #008000;\n");
      out.append("               }\n");
      out.append("               .newslists li{\n");
      out.append("                 background:url(/b2e/unsecured/common/images/fndli/header_icon.png) no-repeat left -270px;\n");
      out.append("                 height: 26px;\n");
      out.append("                 line-height: 26px;\n");
      out.append("                 padding-left: 12px;\n");
      out.append("                 background-repeat: no-repeat;\n");
      out.append("                 overflow: hidden;\n");
      out.append("                 \n");
      out.append("               }\n");
      out.append("               .infoTitle {\n");
      out.append("               }\n");
      out.append("               .infocontent {\n");
      out.append("                  font-size: 14px; }\n");
      out.append("                  #page { font-size:12px;}\n");
      out.append("                  #page a{ border:#8db5d7 1px solid; padding:2px; color:#000; text-decoration:none;padding-left:5px; padding-right:5px;}\n");
      out.append("                  #page a:hover{ border:red 1px solid; padding:2px; padding-left:5px; padding-right:5px;}\n");
      out.append("                  #page a:active{ border:red 1px solid; padding:2px;padding-left:5px; padding-right:5px;}\n");
      out.append("                  #page span.select{border:#8db5d7 0px solid; padding:2px;}\n");
      out.append("                  span.none{border:#ccc 1px solid; padding:2px; color:#CCCCCC;}\n");
      out.append("           </style>\n");
      out.append("   </head>\n");
      out.append("   <body style=\"background-image:url(/b2e/unsecured/common/images/logon_bg.gif);background-repeat:no-repeat; background-position:center\" topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\"\n");
      out.append("      class=\"pageBody\">\n");
      out.append("      <div class=\"main\">\n");
      out.append("         <div class=\"bg\">\n");
      out.append("            <div class=\"top\">\n");
      out.append("               <img src=\"/b2e/unsecured/common/images/fndli/banner.jpg\" width=\"931\"\n");
      out.append("                  height=\"86\" />\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cen\">\n");
      out.append("               <div class=\"cenr\">\n");
      out.append("                  <div class=\"cenr_tit\">\n");
      out.append("                     <div class=\"c_left\">\n");
      out.append("                        &nbsp;" + lableYourPosition + ":&nbsp;\n");
      out.append("                        <a href=\""+mgr.getURL()+"\">" + lableHome + "&gt;</a>\n");
      
      String currentLocation = null;
      if(NEWSLIST.equals(infoType)){
         currentLocation = lableNewsList;
      }else if(BULLETIN.equals(infoType)){
         currentLocation = lableBulletin;
      }else if(CMIS.equals(infoType)){
         currentLocation = lableCOSMIS;
      }else if(SYSTEM_CONSTRUCT.equals(infoType)){
         currentLocation = lableConsturct;
      }else if(SYSTEM_REFERENCE.equals(infoType)){
         currentLocation = lableSystemReference;
      }else if(ROLLING_PICTURE.equals(infoType)){
         currentLocation = lableProjectPhoto;
      }
      
      out.append("                        " + currentLocation + "\n");
      out.append("                     </div>\n");
      out.append("                  </div>\n");
      out.append("\n");
      out.append("                  <table align=\"center\" width=\"100%\">\n");
      
      

      
      
      out.append("                              <tr>\n");
      out.append("                                 <td>\n");
      
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = null;
      ASPBuffer buffer = null;
      int count = 0;
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1",infoType);
      q.addParameter("IN_1","" + pageEndNum);
      q.addParameter("IN_1", "" + pageBeginNum);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      out.append("<div class='newsContainer'>");
      out.append("<ul class='newslists'>");
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoContent = buffer.getBufferAt(i).getValue("INFO_CONTENT");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            infoDate = buffer.getBufferAt(i).getValue("INFO_DATE");
            infoOwner = buffer.getBufferAt(i).getValue("INFO_OWNER");
            infoVisible = buffer.getBufferAt(i).getValue("INFO_VISIBLE");
            infoColor = buffer.getBufferAt(i).getValue("INFO_COLOR");
//            out.append("<div class=\"newslists\"><a href=\"" + mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\" target=\"_blank\"  title=\""+infoTitle+"\">"+infoTitle+"</a>" +
//            		"&nbsp;&nbsp;&nbsp;&nbsp;(<span class='date'>"+ infoDate+"</span>/<span class='author'>"+infoOwner+"</span>)</div>");
            out.append("<li><a href=\"" + mgr.getURL() +"?commandFlag=logonNewsDetail&infoId=" + infoId +"\" target=\"_blank\"  title=\""+infoTitle+"\">"+infoTitle+"</a>" +
                  "&nbsp;&nbsp;&nbsp;&nbsp;(<span class='date'>"+ infoDate+"</span><span class='author'>"+infoOwner+"</span>)</li>");
         }
      }
      out.append("</ul>");
      out.append("</div>");
      
      
      out.append("                                 </td>\n");
      out.append("                              </tr>\n");
      
      
      out.append("                              <tr align='center'>\n");
      out.append("                                 <td>\n");
      out.append("<form name='pageForm' action='" +mgr.getURL()+ "?" + mgr.getQueryString()+ "' method='POST' ");
      out.append("    <div id=\"page\">\n");
      
      
      StringBuilder sqlTotalCount = new StringBuilder("SELECT  count(1) TOTALCOUNT From LOGON_INFO T WHERE T.info_type_db = ? AND T. INFO_VISIBLE='TRUE' ORDER BY T.info_date DESC ");
      trans.clear();
      q = trans.addQuery("SQLLOGONINFO", sqlTotalCount.toString());
      q.addParameter("IN_1",infoType);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            resultCount = (int)buffer.getBufferAt(i).getNumberValue("TOTALCOUNT");
            totalPages = (resultCount + pageSize-1) / pageSize;
         }
      }
      
      
      if(currentPageNum ==1){
         out.append("        <span class=\"none\"> < Prev </span> \n");
      }else{
         out.append("        <a href=\"javascript:queryPage(" + (currentPageNum -1) +")\"> &lt;Prev </a>\n");
      }
      
      for (int i = 1; i <= totalPages; i++) {
         if(currentPageNum == i){
            out.append("        <span class=\"select\">" + i + " </span> \n");
         }else{
            out.append("        <a href=\"javascript:queryPage(" + i +")\">" + i + "</a>\n");
         }
         
         if(i > 20){
            
         }
         
      }
      
      if(currentPageNum >= totalPages){
         out.append("       <span class=\"none\"> Next  &gt; </span> \n");
      }else{
         out.append("        <a href=\"javascript:queryPage(" + (currentPageNum +1) +")\">Next  > </a>\n");
      }
      
      
      out.append("     </div>\n");
      printHiddenField("currentPageNum", "");
      out.append("</form>");
      out.append("\n");
      out.append("                                 </td>\n");
      out.append("                              </tr>\n");
      
      
      out.append("                           </table>\n");
      out.append("                        </td>\n");
      out.append("                     </tr>\n");
      out.append("                  </table>\n");
      out.append("               </div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"foot\">" + lableCopyRight + " &copy; 2009 - 2020 " + lableCNPE + " \n");
      out.append("            </div>\n");
      out.append("         </div>\n");
      out.append("\n");
      out.append("      </form>\n");
      out.append("   </body>\n");
      out.append("</html>\n");
      appendDirtyJavaScript("function queryPage(num){\n");
      appendDirtyJavaScript("   document.pageForm.currentPageNum.value = num;\n ");
      appendDirtyJavaScript("   document.pageForm.submit();\n");
      appendDirtyJavaScript("}");
      
   }
   
   
   public void getLogonNewsDetail(AutoString out) throws FndException{
      
      StringBuilder sqlLogonInfo = new StringBuilder();
      sqlLogonInfo.append(" Select TT.INFO_ID INFO_ID,");
      sqlLogonInfo.append(" TT.INFO_TITLE INFO_TITLE,");
      sqlLogonInfo.append(" TT.INFO_CONTENT INFO_CONTENT,");
      sqlLogonInfo.append(" TT.INFO_DOC_ADDRESS INFO_DOC_ADDRESS,");
      sqlLogonInfo.append(" TT.INFO_DOC_NAME INFO_DOC_NAME,");
      sqlLogonInfo.append(" TT.INFO_DATE INFO_DATE,");
      sqlLogonInfo.append(" TT.INFO_OWNER INFO_OWNER,");
      sqlLogonInfo.append(" TT.INFO_COLOR INFO_COLOR,");
      sqlLogonInfo.append(" TT.INFO_VISIBLE INFO_VISIBLE,");
      sqlLogonInfo.append(" TT.INFO_TYPE INFO_TYPE,");
      sqlLogonInfo.append(" TT.INFO_TYPE_DB INFO_TYPE_DB ");
      sqlLogonInfo.append(" FROM LOGON_INFO TT");
      sqlLogonInfo.append(" WHERE TT.INFO_ID = ? ");
      
      ASPManager mgr = getASPManager();
      String destination = mgr.getASPConfig().getProtocol()+"://"+ mgr.getASPConfig().getApplicationDomain()+""+mgr.getASPConfig().getApplicationContext()+"/fndlw/upload/";
      
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = null;
      ASPBuffer buffer = null;
      int count = 0;

      String infoId = mgr.readValue("infoId");
      String infoTitle = null;
      String infoContent = null;
      String infoDocAddress = null;
      String infoDocName = null;
      String infoDate = null;
      String infoOwner = null;
      String infoVisible = null;
      String infoColor = null;
      boolean isGraph = false;
      
      
      q = trans.addQuery("SQLLOGONINFO", sqlLogonInfo.toString());
      q.addParameter("IN_1",infoId);
      trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
      trans = mgr.perform(trans);
      buffer = trans.getBuffer("SQLLOGONINFO");
      count = buffer.countItems();
      for (int i = 0; i < count; i++)
      {
         if("DATA".equals(buffer.getNameAt(i)))
         {
            infoId = buffer.getBufferAt(i).getValue("INFO_ID");
            infoTitle = buffer.getBufferAt(i).getValue("INFO_TITLE");
            infoContent = buffer.getBufferAt(i).getValue("INFO_CONTENT");
            infoDocAddress = buffer.getBufferAt(i).getValue("INFO_DOC_ADDRESS");
            infoDocName = buffer.getBufferAt(i).getValue("INFO_DOC_NAME");
            infoDate = buffer.getBufferAt(i).getValue("INFO_DATE");
            infoOwner = buffer.getBufferAt(i).getValue("INFO_OWNER");
            infoVisible = buffer.getBufferAt(i).getValue("INFO_VISIBLE");
            infoColor = buffer.getBufferAt(i).getValue("INFO_COLOR");
            if(!Str.isEmpty(infoDocAddress) && (infoDocAddress.toLowerCase().endsWith(".jpg") || 
                  infoDocAddress.toLowerCase().endsWith(".jpeg") ||
                  infoDocAddress.toLowerCase().endsWith(".bmp") ||
                  infoDocAddress.toLowerCase().endsWith(".gif") ||
                  infoDocAddress.toLowerCase().endsWith(".png") )){
               isGraph =  true;
            }
         }
      }
      
      String sql = "select t.info_content from logon_info_tab t where t.info_id= ? ";
      List conditionList = new ArrayList(2);
      conditionList.add(infoId);
      ArrayList countList = (ArrayList)Access.getSingleList(sql, conditionList);
	  
	  infoContent = (String) countList.get(0);
      
      out.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
      out.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
      out.append("   <head>\n");
      out.append("      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/styles-theme1.css' type='text/css'>\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/fndli.css' type='text/css'>\n");
      out.append("         <link rel='STYLESHEET' href='/b2e/unsecured/common/stylesheets/Styles-en.css' type='text/css'>\n");
      out.append("               <title>IFS Applications - Log On</title>\n");
      out.append("               <!-- ASPPage.getHeadTag() -->\n");
      out.append("         <script language=\"JavaScript\">\n");
      out.append("            APP_PATH = \"/b2e/unsecured\";\n");
      out.append("            APP_ROOT=\"/b2e/unsecured/\";\n");
      out.append("            COOKIE_PATH=\"/b2e/\";\n");
      out.append("            COOKIE_ROOT_PATH=\"\";\n");
      out.append("            COOKIE_PREFIX=\"\";\n");
      out.append("            COOKIE_DOMAIN=\"\";\n");
      out.append("            THEME=\"theme1/\";\n");
      out.append("            STD_PORTLET=false;\n");
      out.append("            IS_RTL=false;\n");
      out.append("          </script>\n");
      out.append("          <script language=\"javascript\"  src=\"/b2e/unsecured/common/scripts/genericclientscript.js\"></script>\n");
      out.append("          <script language=\"javascript\"  src=\"/b2e/unsecured/common/scripts/fndli.js\"></script>\n");
      out.append("          <style type=\"text/css\">\n");
      out.append("                  .pageBody {\n");
      out.append("                  background-color: #E6E6E6; }\n");
      out.append("                  .infotitle {\n");
      out.append("                   width:750;\n");
      out.append("                   text-align:center;\n");
      out.append("                   }\n");
      out.append("                  .infocontent {\n");
      out.append("                  font-size: 14px; }\n");
      out.append("          </style>\n");
      out.append("   </head>\n");
      out.append("   <body style=\"background-image:url(/b2e/unsecured/common/images/logon_bg.gif);background-repeat:no-repeat; background-position:center\" topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\"\n");
      out.append("      class=\"pageBody\">\n");
      out.append("      <div class=\"main\">\n");
      out.append("         <div class=\"bg\">\n");
      out.append("            <div class=\"top\">\n");
      out.append("               <img src=\"/b2e/unsecured/common/images/fndli/banner.jpg\" width=\"931\"\n");
      out.append("                  height=\"86\" />\n");
      out.append("            </div>\n");
      out.append("            <div class=\"cen\">\n");
      out.append("               <div class=\"cenr\">\n");
      out.append("                  <div class=\"cenr_tit\">\n");
      out.append("                     <div class=\"c_left\">\n");
      out.append("                        &nbsp;" + lableYourPosition + ":&nbsp;\n");
      out.append("                        <a href=\""+mgr.getURL()+"\">" + lableHome + "&gt;</a>\n");
      out.append("                        " + lableDetailmessage + "\n");
      out.append("                     </div>\n");
      out.append("                  </div>\n");
      out.append("\n");
      out.append("                  <table align=\"center\" width=\"100%\">\n");
      out.append("                     <tr align=\"center\">\n");
      out.append("                        <td>\n");
      out.append("                           <table>\n");
      out.append("                              <tr>\n");
      out.append("                                 <td>\n");
      out.append("                                    <div class=\"infotitle\">\n");
      out.append("                                       <h1> " + infoTitle +"</h1>\n");
      out.append("                                    </div>\n");
      out.append("                                 </td>\n");
      out.append("                              </tr>\n");
      
      if(isGraph ){
         out.append("                              <tr>\n");
         out.append("                                 <td>\n");
         out.append("                                    <div class=box >\n");
         out.append("                                          <img style=\"width:750px\" alt=\"\"\n");
         out.append("                                             src=\""+ destination + infoDocAddress +"\"\n");
         out.append("                                             border=0>\n");
         out.append("                                    </div>\n");
         out.append("                                 </td>\n");
         out.append("                              </tr>\n");
      }
      
      out.append("                              <tr align='center'>\n");
      out.append("                                 <td>\n");
      out.append("                                    <div class=\"infocontent\" style=\"width:750px;text-align:left;\">\n");
      out.append("                                       <br />\n");
      if(!Str.isEmpty(infoContent)){
         infoContent = infoContent.trim();
         infoContent =  "        " + infoContent;
         out.append(mgr.HTMLEncode(infoContent,true).replaceAll("\n", "<br>"));
      }
      out.append("                                    </div>\n");
      out.append("                                 </td>\n");
      out.append("                              </tr>\n");
      
      if(!Str.isEmpty(infoDocAddress) && !isGraph){
         out.append("                              <tr align='center'>\n");
         out.append("                                 <td>\n");
         out.append("                                    <div class=\"infocontent\" style=\"width:80%;text-align:right;\">\n");
         out.append("                                       <a class='' href=\""+ destination + infoDocAddress +"\" target=_blank>" + lableAttach + (infoDocName == null ? "" : ": " + infoDocName  )+"</a>\n");
         out.append("                                       <img src=\"/b2e/unsecured/common/images/fndli/attach.png\"  width='16px' border=0>\n");
         out.append("                                    </div>\n");
         out.append("                                 </td>\n");
         out.append("                              </tr>\n");
      }
      
      
      out.append("                           </table>\n");
      out.append("                        </td>\n");
      out.append("                     </tr>\n");
      out.append("                  </table>\n");
      out.append("               </div>\n");
      out.append("            </div>\n");
      out.append("            <div class=\"foot\">" + lableCopyRight + " &copy; 2009 - 2020 " + lableCNPE + " \n");
      out.append("            </div>\n");
      out.append("         </div>\n");
      out.append("\n");
      out.append("      </form>\n");
      out.append("   </body>\n");
      out.append("</html>\n");
   }
} 
