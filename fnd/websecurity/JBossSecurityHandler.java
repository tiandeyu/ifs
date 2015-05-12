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
 * File        : JBossSecurityHandler.java
 * Description : JAAS implementation for handling authentication.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Revision 1.1  2005/09/15 12:38:03  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/17 10:45:40  rahelk
 * JAAS AS specific security plugin
 *
 *
 */

package ifs.fnd.websecurity;

import ifs.fnd.service.*;

import java.io.*;
import java.util.*;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

class JBossSecurityHandler extends SecurityHandlerPlugin
{
   //private static Subject subject; //subject for config user
   
   protected Subject authenticateConfigSubject(Map cfg) throws LoginException
   {
      Subject subject = null;

      String configUser = getConfigParam(cfg,"admin/config_user","");
      String configPassword = getConfigParam(cfg,"admin/config_password","");
      String loginRealm = getConfigParam(cfg,"admin/user_authentication/plugin_params/config_realm","client-login");

      CallbackHandler callback = new PassiveCallbackHandler(configUser, configPassword);
      LoginContext loginContext = new LoginContext(loginRealm, callback);

      loginContext.login();

      subject = loginContext.getSubject();
      return subject;
   }
}


