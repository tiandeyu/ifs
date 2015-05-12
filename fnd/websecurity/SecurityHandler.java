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
 * File        : SecurityHandler.java
 * Description : JAAS implementation for handling authentication.
 * Notes       :
 * ----------------------------------------------------------------------------
 * 09/04/2010 rahelk bug 89989, added support for weblogic intorducing run-as
 * Revision 1.5  2005/12/28 12:32:11  japase
 * Added support for stand-alone profile conversion tool
 *
 * Revision 1.4  2005/11/08 12:32:11  japase
 * Correction of accidently commit - back to version 1.2
 *
 * Revision 1.2  2005/10/06 09:52:33  rahelk
 * moved getSecurityConfigRealm to AS specific class
 *
 * Revision 1.1  2005/09/15 12:38:03  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/17 10:50:53  rahelk
 * made default construct private
 *
 * Revision 1.1  2005/08/17 10:45:40  rahelk
 * JAAS AS specific security plugin
 *
 * Revision 1.2  2005/04/01 10:51:58  rahelk
 * JAAS: implemented calling EJB's directly
 *
 * Revision 1.1  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 *
 */

package ifs.fnd.websecurity;

import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import java.io.*;
import java.util.*;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

import java.lang.reflect.*;

public abstract class SecurityHandler
{
   final static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.websecurity.SecurityHandler");

   private static Subject subject; //subject for config user
   private static Class cls;
   static Map cfgparams;
   static private SecurityHandlerPlugin security_handler;
   private static boolean RunAsPrincipalEnabled;


   private SecurityHandler()
   {
   }

   public static void init(Map cfg) throws FndException
   {
      cfgparams = cfg;
      String class_name = null;

      RunAsPrincipalEnabled = "Y".equalsIgnoreCase(getConfigParam(cfgparams, "admin/user_authentication/run-as-principal-enabled","N"));

      if(cfg instanceof Properties)
         class_name = ((Properties)cfg).getProperty("jaas_plugin_class_name");
      else
         class_name = getConfigParam(cfgparams, "admin/user_authentication/jaas_plugin_class","ifs.fnd.websecurity.JBossSecurityHandler");

      try
      {
         cls = Class.forName(class_name);
         security_handler = (SecurityHandlerPlugin) cls.newInstance();

         //security_handler.authenticateConfigSubject(cfgparams);

      }
      /*catch (LoginException fle)
      {
         throw new FndException("FNDSECHANDLERCONFUSERAUTHFAILED: Could not authenticate config user.\n"+fle);
      }
       */
      catch (Exception any)
      {
         throw new FndException("SecurtyHanlder.init:\n"+any);
      }
   }

   public static boolean isRunAsPrincipalEnabled()
   {
       return RunAsPrincipalEnabled;
   }

   /**
    * Programmatically authenticating an user
    */
   public static boolean authenticateUser(String loginRealm, String username, String password)
   {
      if(DEBUG) debug("SecurityHandler.authenticateUser: realm="+loginRealm+"username="+username+"");

      try
      {
         CallbackHandler callback = new PassiveCallbackHandler(username, password);
         LoginContext loginContext = new LoginContext(loginRealm, callback);

         loginContext.login();

         if(DEBUG) debug("Real User: "+username+" succeessfully authenticated.");

         loginContext.logout(); //log real user and use config user

         return true;
      }
      catch (LoginException fle)
      {
         return false;
      }
   }

   /**
    * authenticating and propagating the configuser from web container to EJB container.
    */

   //public static Subject getConfigSubject(String loginRealm, String configUser, String configPassword) throws FndException

   public static Subject getConfigSubject() throws FndException
   {
      if (isRunAsPrincipalEnabled())  return null; 

      try
      {
         subject = security_handler.authenticateConfigSubject(cfgparams);

      }
      catch (LoginException fle)
      {
         throw new FndException("FNDSECHANDLERCONFUSERAUTHFAILED: Could not authenticate config user.\n"+fle);
      }
      catch (Exception any)
      {
         throw new FndException("SecurtyHanlder.getConfigSubject:\n"+any);
      }

      return subject;
   }

   /**
    * Return named configuration parameter or default value if not found.
    */
   static String getConfigParam( Map cfg, String name, String def_value )
   {
      if(cfg instanceof Properties)
      {
         if(name.startsWith("admin/"))
            name = name.substring(6);
         String str = ((Properties)cfg).getProperty(name);
         return Str.isEmpty(str) ? def_value : str;
      }

      Object value = cfg.get(name.toUpperCase());
      if( !(value instanceof String) )
         return def_value;
      String str = (String)value;
      return Str.isEmpty(str) ? def_value : str;
   }

   protected static void debug( String line )
   {
      Util.debug(line);
   }

}


