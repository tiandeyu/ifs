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
 * File        : PlsqlProxyInvocationProcessor.java
 * Description : PlsqlInvocationProcessor which works in proxy-user mode
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2006-Jul-18 - Created (Bug id 60208)
 *    Marek D  2007-Jan-05 - Bug id 62608 : Use system user connections in proxy-user mode
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.services.plsqlserver.service;

import javax.servlet.http.HttpServletRequest;

/**
 * <B>Framework internal class:</B> PlsqlInvocationProcessor which works in proxy-user mode.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
class PlsqlProxyInvocationProcessor extends PlsqlInvocationProcessor {

   /**
    * Creates an extension of PlsqlInvocationProcessor that accesses database in proxy-user mode.
    * This constructor is supposed to be called directly by a servlet code, not from a subclass of FndSessionBean.
    * The created instance will access database via JDBC, not using a database connection pool in a J2EE container.
    * The specified HTTP request will be used to check system privileges and activity grants for PLSQL Gateway
    * as well as to retrieve session ID that identifies the client database connection, which may have
    * active transaction and/or cursors.
    *
    * @param httpRequest current HTTP request
    */
   public PlsqlProxyInvocationProcessor(HttpServletRequest httpRequest) {
      setHttpServletRequest(httpRequest);
   }
}


