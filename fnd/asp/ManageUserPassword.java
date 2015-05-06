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
 * File        : ManageUserPassword.java
 * Description : Handling of user password.
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2010/03/01 buhilk Bug id 87603, Changed AnonymousAccess views. Updated setChangePasswordEnabled(), changePassword() & checkPassword().
 * 2009/04/24 buhilk Bug id 82180, Replaced ClientServices with AnonymousAccess. Updated setChangePasswordEnabled(), changePassword() & checkPassword().
 * 2008/12/09 dusdlk Bug id 77830, Updated checkPassword() according to the new view names and method to check password and user name. 
 * 2008/05/09 sadhlk Bug id 73689, changed view names according to new view names in ClientServicesViews
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/09/14 08:02:08  rahelk
 * password management
 *
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.asp;

import ifs.fnd.ap.*;
import ifs.fnd.base.*;
import ifs.fnd.record.*;
import ifs.fnd.service.*;
import ifs.application.anonymousaccess.*;

public class ManageUserPassword
{
   
   final static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.websecurity.SecurityHandler");   
   
   private static ManageUserPassword password_manager;
   private static boolean RMI = true;
   private static boolean change_password_enabled = false;
   
   private ManageUserPassword() {
   }
   
   public static synchronized ManageUserPassword getInstance(ASPConfig cfg) throws FndException
   {
      if(DEBUG) debug("ManageUserPassword.getInstance("+cfg+")");
      
      if (password_manager == null)
      {
         password_manager = new ManageUserPassword();
         
         RMI = "RMI".equals(cfg.getParameter("ADMIN/TRANSACTION_MANAGER","RMI"));
         
         setChangePasswordEnabled(cfg);
      }
      
      return password_manager;
   }
   
   private static void setChangePasswordEnabled(ASPConfig cfg) throws FndException
   {
      if(DEBUG) debug("ManageUserPassword.setChangePasswordEnabled");
      
      change_password_enabled = false;
      
      if (RMI)
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PASSWORD_ENABLED, cfg );
         AnonymousAccessViews.V4 param = 
            new AnonymousAccessViews.V4();
         
         FndBoolean result = (FndBoolean)con.invoke(param);
         con.release();
         
         if(DEBUG) debug("ManageUserPassword.setChangePasswordEnabled(): RMI Result:"+result.booleanValue(false));      
         
         change_password_enabled = result.booleanValue(false);
      }
      else
      {
         Record request_rec;
         Record response_rec;
         
         request_rec = new Record("ANONYMOUSACCESS_ISCHANGEPASSWORDENABLED");
         
         ConnectionPool.Slot con = JAPConnectionPool.get( ConnectionPool.PASSWORD_ENABLED, cfg );
         response_rec = (Record)con.invoke(request_rec);
         con.release();
         
         if(DEBUG) debug("ManageUserPassword.setChangePasswordEnabled(): JAP Result:"+ response_rec );      
         
         RecordAttribute ra = response_rec.find("__RESULT");
         change_password_enabled = ra.getBoolean();
      }
   }
   
   public boolean isChangePasswordEnabled()
   {
      if(DEBUG) debug("ManageUserPassword.isChangePasswordEnabled");
      
      return change_password_enabled;
   }

   public void changePassword(String user_id, String old_password, String new_password, ASPConfig cfg) throws IfsException,
                                                                                                              FndException
   {
      if(DEBUG) debug("ManageUserPassword.changePassword: user_id:"+user_id);
      
      if (RMI)
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PASSWORD_CHANGE, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         
         AnonymousAccessViews.V2 param = 
            new AnonymousAccessViews.V2();

         param.username.setValue(user_id);
         param.oldPassword.setValue(old_password);
         param.newPassword.setValue(new_password);

         con.invoke(param);
         con.release();
         
         if(DEBUG) debug("ManageUserPassword.changePassword(): RMI change password successful" );      
      }
      else
      {
         Record request_rec;
         Record response_rec;
         
         request_rec = new Record("ANONYMOUSACCESS_CHANGEPASSWORD");
         request_rec.add("USERNAME",user_id);
         request_rec.add("OLD_PASSWORD",old_password);
         request_rec.add("NEW_PASSWORD",new_password);
         
         ConnectionPool.Slot con = JAPConnectionPool.get( ConnectionPool.PASSWORD_CHANGE, cfg );
         Server srv = (Server)con.getConnectionProvider();
         
         response_rec = (Record)con.invoke(request_rec);
         con.release();
         
         if(DEBUG) debug("ManageUserPassword.changePassword(): JAP change password successful" );      
      }
      
   }
   
   public Record checkPassword(String user_id, String password, ASPConfig cfg) throws FndException,
                                                                                      ApplicationException 
   {
      if(DEBUG) debug("ManageUserPassword.checkPassword: user_id:"+user_id);
      
      if (RMI)
      {
         ConnectionPool.Slot con = EJBConnectionPool.get( ConnectionPool.PASSWORD_CHECK, cfg );
         FndContext ctx = FndContext.getCurrentContext();
         
         AnonymousAccessViews.V3 param = 
            new AnonymousAccessViews.V3();
         
         param.password.setValue(password);
         param.userName.setValue(user_id.toUpperCase());

         param = (AnonymousAccessViews.V3)con.invoke(param);
         con.release();
         
         Record response_rec = new Record("ANONYMOUSACCESS_GETPASSWORDEXPIRATION");
         response_rec.add("STATUS", (param.status.getValue()).intValue() );
         response_rec.add("ERROR_INFO",param.errorInfo.getValue());
         try
         {
            response_rec.add("GRACE_PERIOD", (param.gracePeriod.getValue()).intValue() );
         }
         catch (Exception e)
         {
         }

         if(DEBUG) debug("ManageUserPassword.checkPassword(): RMI result:"+ response_rec );      

         return response_rec;
      }
      else
      {
         Record request_rec;
         Record response_rec;
         
         request_rec = new Record("ANONYMOUSACCESS_GETPASSWORDEXPIRATION");
         request_rec.add("PASSWORD",password);
         request_rec.add("USER_NAME",user_id);
         
         ConnectionPool.Slot con = JAPConnectionPool.get( ConnectionPool.PASSWORD_CHECK, cfg );
         
         response_rec = (Record)con.invoke(request_rec);
         con.release();
         
         if(DEBUG) debug("ManageUserPassword.checkPassword(): JAP result:"+ response_rec );      
         
         RecordAttribute ra = response_rec.find("STATUS");
         long status = ra.getLong();

         return response_rec;
      }
   }
   
   private static void debug( String line )
   {
      Util.debug(line);
   }
   
}
