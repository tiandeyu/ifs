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
 * File        : AuthenticationPlugin.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified
 *   Mangala 2004-Nov-09 - Created for SSO.
 *   Mangala 2006-Nov-21 - Bug Id 61977, Changed getUserId to send HttpServletRequest //no one still uses it
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;

import javax.servlet.http.HttpServletRequest;

/** The idea is to implement your class from this interface to identify (and probably
 * authenticate too) the current user and return from getUserId() method. To make it work you have
 * to change some configuration parameters as well.
 *
 * Enable identified_by_plugin and set your class name. Then the framework simply retrieve the remote
 * user by calling your getUserId() method.
 * <pre>
 *     &lt;user_authentication&gt;
 *        &lt;auth_method&gt;PLUGIN&lt;/auth_method&gt;
 *        &lt;auth_plugin_class&gt;YOUR-CLASS-NAME&lt;/auth_plugin_class&gt;
 *      &lt;/user_authentication&gt;
 * </pre>
 */
public interface AuthenticationPlugin 
{
   
   /** Method should returns the log on user */ 
   public String getUserId(HttpServletRequest req);
}
