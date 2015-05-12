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
 * File        : SecurityHandlerPlugin.java
 * Description : Abstract class for J2EE application server specific JAAS impl.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Revision 1.1  2005/09/15 12:38:03  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/08/17 10:50:04  rahelk
 * JAAS AS specific security plugin
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

abstract class SecurityHandlerPlugin
{
   
   public SecurityHandlerPlugin() {
   }
   
   protected abstract Subject authenticateConfigSubject(Map cfgparams) throws LoginException;
   
   /**
    * Return named configuration parameter or default value if not found.
    */
   protected String getConfigParam( Map cfg, String name, String def_value )
   {
      return SecurityHandler.getConfigParam( cfg, name, def_value );
   }
   
   protected static void debug( String line )
   {
      Util.debug(line);
   }
   
   
}
