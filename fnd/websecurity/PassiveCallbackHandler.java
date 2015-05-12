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
 * Revision 1.1  2005/09/15 12:38:03  japase
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/17 10:45:40  rahelk
 * JAAS AS specific security plugin
 *
 * Revision 1.1  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 *
 */

package ifs.fnd.websecurity;

import javax.security.auth.callback.*;

public class PassiveCallbackHandler implements CallbackHandler
{
   private String username;
   private char[] password;

   /**
    * Creates a callback handler with the give username and password.
    */
   public PassiveCallbackHandler(String user, String pass) {
      this.username = user;
      this.password = pass.toCharArray();
   }
   
   public void handle(Callback[] callbacks) throws java.io.IOException, UnsupportedCallbackException
   {
      for (int i = 0; i < callbacks.length; i++)
      {
         if (callbacks[i] instanceof NameCallback) {
            ((NameCallback) callbacks[i]).setName(username);
         }
         else if (callbacks[i] instanceof PasswordCallback) {
            ((PasswordCallback) callbacks[i]).setPassword(password);
         }
         else {
            throw new UnsupportedCallbackException(callbacks[i], "Callback class not supported");
         }
      }
   }
   
   public void clearPassword() {
      if (password != null) {
         for (int i = 0; i < password.length; i++) {
            password[i] = ' ';
         }
         password = null;
      }
   }
   
}
