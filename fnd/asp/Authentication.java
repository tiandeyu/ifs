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
 * File        : AUTHConnectionPool.java
 * Description : class used to authenticate using fndext
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H 2003-07-02 - Created.
 *    Ramila H 2003-08-06 - Log id 853, added methods to check and change password.
 *    Jacek P  2004-Apr-20 - First synchronization with the Montgomery project
 *    Suneth M 2004-May-26 - Merged Bug 44759, Changed authenticateUser().
 *    Jacek P  2004-Jun-08 - Second synchronization with Montgomery.
 * ----------------------------------------------------------------------------
 * New Comments:
 * Revision 1.2  2009/04/25 15:42:00  buhilk
 * Bug ID: 82180, Removed ClientServices and replaced with AnonymousAccess
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 *
 * ----------------------------------------------------------------------------

 */

package ifs.fnd.asp;


import java.util.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.ap.*;
import ifs.fnd.base.*;
import ifs.fnd.remote.*;
import ifs.fnd.remote.j2ee.*;
import ifs.fnd.record.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.ejb.CreateException;

//JAAS stuff
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

public class Authentication
{
   //==========================================================================
   // Static variables
   //==========================================================================

   public static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.asp.Authentication");

   private static Authentication pool;
   private static String realm = null;

   private static boolean externally_identified = false;
   private static boolean use_authentication;
   private static boolean change_password_enabled = false;

   private static String systype   = null;
   private static String sysid     = null;
   private static String syssecret = null;

   //==========================================================================
   //  Construction of the only one static instance of EJBConnectionPool
   //==========================================================================

   /**
    * Private constructor
    **/
   private Authentication()
   {
   }

   //==========================================================================
   //  Static methods to be called from ASPManager
   //==========================================================================

  /**
   * @deprecated 
   */
   public static synchronized Authentication getInstance(ASPConfig cfg)
   {
      if(DEBUG) debug("Authenticattion.get("+cfg+")");

      if (pool == null)
      {
         pool = new Authentication();

         realm = cfg.getParameter("ADMIN/USER_AUTHENTICATION/SECURITY_REALM","IFSApplications");

         //setExternallyIdentified();
         //setUseAuthentication();
         //setChangePasswordEnable();
      }

      return pool;
   }

   //==========================================================================
   //  public methods
   //==========================================================================
  /**
   * @deprecated - use methods in ASPConfig
   */
   public boolean isUserExternallyIdentified()
   {
      return externally_identified;
   }

  /**
   * @deprecated - use methods in ASPManager
   */
   public int authenticateUser(String user_id, String password) 
   {
      int err = 0;

      return err;
   }

  /**
   * @deprecated - use methods in ASPConfig
   */
   public static boolean useAuthentication()
   {
      return use_authentication;
   }

  /**
   * @deprecated - use methods in ASPConfig
   */
   public static boolean isChangePasswordEnabled()
   {
      return change_password_enabled;
   }

  /**
   * @deprecated 
   */
   public static boolean changePassword(String user_id, String old_password, String new_password) throws FndException,
                                                                                                      ApplicationException,
                                                                                                      IfsException,
                                                                                                      RemoteException
   {
      return false;
      /*
      if ("RMI".equals(tm_method))
      {
         ChangePasswordRequest cpr = new ChangePasswordRequest();
         cpr.accessPointName.setValue(access_point_name);
         cpr.identity.setValue(user_id);
         cpr.credential.setValue(old_password);
         cpr.newCredential.setValue(new_password);

         ClientServicesParameterViews.AuthenticateUser_ChangePassword_Parameters cpp =
            new ClientServicesParameterViews.AuthenticateUser_ChangePassword_Parameters();
         cpp.changePasswordRequest.setRecord(cpr);

         FndRemoteContext context = new FndRemoteContext();
         context.setSecurityData(insertSecurityData());
         FndRecordResultWrapper result = ejb.authenticateUser_ChangePassword(cpp, context);
         cpp = (ClientServicesParameterViews.AuthenticateUser_ChangePassword_Parameters)result.getResult();
         return cpp.result.getValue().booleanValue();
      }
      else
      {
         //srv.setConnectionString(url);
         Record requestRec = new Record("AUTHENTICATE");
         requestRec.add("ACCESSPOINTNAME",access_point_name);
         requestRec.add("IDENTITY",user_id);
         requestRec.add("CREDENTIAL",old_password);
         requestRec.add("NEWCREDENTIAL",new_password);
         try
         {
            Record responseRec = (Record)srv.invoke(interfname, "ChangePassword", requestRec, null, null);
            return responseRec.find("__RESULT").getBoolean();
         }
         catch ( APException e )
         {
            Buffer headbuf = e.getHeaderBuffer();
            if ( headbuf == null )
            { // header buffer in APException will not be null if TM has been bound
               FndException efnd = new FndException("FNDMGRTMNF2: Failed to contact ORB or LDAP/Oracle-Server. Contact your system administrator.");
               efnd.addCaughtException(e);
               throw efnd;
            }
            else
            {
               FndException efnd = new FndException();
               efnd.loadAndParse(headbuf);
               throw efnd;
            }
         }
      }
     */
   }

   //==========================================================================
   //  private methods
   //==========================================================================


   //==========================================================================
   //  Debugging, error
   //==========================================================================
   private static void debug( String line )
   {
      Util.debug(line);
   }
   //=======================================================================
   //  FND security
   //=======================================================================
   /**
    * Creates a FND_SECURITY item in the specified request header
    * depending on ADMIN/FNDAS-section in the ASPConfig file.
    */
   private static byte[] insertSecurityData()
   {
      AutoString data = new AutoString();

      appendSecurityString(data, sysid);

      appendSecurityString(data, systype);

      if("".equals(syssecret))
      {
         data.append("a");
      }
      else
      {
         data.append("ba");
         appendSecurityString(data, syssecret);
      }

      return data.toString().getBytes();
   }

   private static void appendSecurityString(AutoString data, String str)
   {
      data.append(String.valueOf(str.length()));
      data.append(":");
      data.append(str);
   }


}
