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
 * File        : PlsqlProxy.java
 * Description : Access to PLSQL via Oracle implicit connection cache
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D   2006-Jul-14 - Created (Bug id 60208)
 *    Marek D   2007-Jan-05 - Bug id 62608 : Use system user connections in proxy-user mode
 *    Marek D   2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
 *    Marek D   2008-Sep-05 - Bug id 76933 : Security issue when invoking PL/SQL from middle-tier:
 *                                          Activate BaseServerMethodSecurity in the beginning of invoke(),
 *                                          before recursive methods may be called
 *    Marek D   2008-Sep-17 - Bug id 77229 : Memory leak in abortable process list
 *    Mattias B 2009-Feb-17 - Bug id 80191 : Removel of PLSQL GATEWAY
 *    Marek D   2009-Apr-07 - Bug ID 81763 : Concurrent User License for Enterprise Explorer
 *    Marek D   2010-Oct-11 - Bug ID 93475 : Dedicated session user changed to IFSWEBCONFIG
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.services.plsqlserver.service;


import ifs.fnd.base.FndConstants;
import ifs.fnd.base.FndContext;
import ifs.fnd.base.FndDebug;
import ifs.fnd.base.IfsException;
import ifs.fnd.base.NotLoggedOnException;
import ifs.fnd.base.SystemException;
import ifs.fnd.log.LogMgr;
import ifs.fnd.log.Logger;
import ifs.fnd.record.FndAbstractRecord;
import ifs.fnd.record.serialization.FndBufferUtil;
import ifs.fnd.record.serialization.FndRecordFormat;
import ifs.fnd.record.serialization.FndSerializationUtil;
import ifs.fnd.record.serialization.FndSerializeConstants;
import ifs.fnd.remote.FndSkeletonParameter;
import ifs.fnd.services.plsqlserver.PlsqlInvocation;
import ifs.fnd.sf.storage.FndAbstractHandler;
import ifs.fnd.sf.admin.FndProcessList;
import ifs.fnd.sf.j2ee.FndContainer;
import ifs.fnd.sf.FndServerContext;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import javax.servlet.http.HttpServletRequest;


/**
 * <B>Framework internal class:</B>
 * Access to PLSQL via Oracle implicit connection cache.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public final class PlsqlProxy {

   
   private static final Logger logger = LogMgr.getFrameworkLogger();

   private PlsqlProxy() {}

   /**
    * Performs PlsqlInvocation request using Oracle implicit connection cache.
    *
    * @param httpRequest current HTTP request
    */
   
   public static void invoke(
      HttpServletRequest httpRequest,
      byte[] requestHeader,
      byte[] requestBody,
      FndSkeletonParameter responseHeader,
      FndSkeletonParameter responseBody) {

      InvocationResult result = invoke(httpRequest, requestHeader, requestBody);
      responseHeader.value = result.header;
      responseBody.value = result.body;
   }

   private static InvocationResult invoke(HttpServletRequest httpRequest, byte[] inHeader, byte[] inBody) {
      FndServerContext ctx = FndServerContext.getCurrentServerContext();
      ctx.setProxyUserMode(true);
      ctx.setProxySessionId(httpRequest.getHeader("Dedicated-Session-Id"));
      ctx.enableBaseServerMethodSecurity();

      // If Dedicated-Session-Action=LogOn then a new dedicated connection should be created and initialized

      String action = httpRequest.getHeader("Dedicated-Session-Action");
      if (action != null && FndDebug.isDebugCallSequenceOn()) {
         FndDebug.debug("PlsqlProxy.invoke(): Dedicated-Session-Id = '&1'", httpRequest.getHeader("Dedicated-Session-Id"));
         FndDebug.debug("PlsqlProxy.invoke(): Dedicated-Session-Action = '&1'", action);
      }

      if("LogOn".equalsIgnoreCase(action))
         ctx.setLoggedOn(false);
      else
         ctx.setLoggedOn(true);

      String osUserName = httpRequest.getHeader("Dedicated-Session-OSUser");
      if(osUserName != null) {
         ctx.setOsUserName(osUserName);
         if (FndDebug.isDebugCallSequenceOn())
            FndDebug.debug("PlsqlProxy.invoke(): Dedicated-Session-OSUser = '&1'", osUserName);
      }

      // Marshal protocol: IN_VIEW_RETURN_VIEW
      InvocationResult result = new InvocationResult();
      try {
         // initContext
         if (FndContainer.JBOSS.equals(FndContainer.getCurrentContainer())) {
            try {
               /* When using EXTERNALLY_IDENTIFIED_HTTP_HEADER authentication, the httpRequest.getUserPrincipal()
                * returns null. As a fix the application user is fetched from the JACC policy context instead.
                * The solution only worked with JBoss AS during the tests. See Bug 92433 for more details.
                */
               Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
               Object[] p = subject.getPrincipals().toArray();
               ctx.setApplicationUser(((Principal) p[0]).getName());

            } catch (PolicyContextException e) {
               if (logger.warning) {
                  logger.warning(e, "Cannot identify the application user. Cause: " + e.getMessage());
               }
               throw new SystemException(e, "Cannot identify the application user. Cause: " + e.getMessage());
            }
         } else {
            ctx.setApplicationUser(httpRequest.getUserPrincipal().getName());
         }

         FndBufferUtil.parseRecord(inHeader, ctx);

         PlsqlInvocation inRecord = new PlsqlInvocation();
         FndSerializationUtil.parseRecord(inBody, inRecord);
         PlsqlInvocation outRecord = invoke(httpRequest, inRecord, ctx);
         result.body = FndSerializationUtil.formatRecord(outRecord, (String)null);
      }
      catch (IfsException e) {
         // abort
         rollback();

         // If NotLoggedOnException caused the error then make it visible to the client
         if(!(e instanceof NotLoggedOnException) && (e.getCause() instanceof NotLoggedOnException))
            e = (IfsException) e.getCause();

         FndContext.getCurrentContext().setErrorMessage(e);
         if (result != null)
            result.body = inBody;
      }
      formatResponse(result);
      return result;
   }

   private static PlsqlInvocation invoke(HttpServletRequest httpRequest, PlsqlInvocation inRecord, FndContext ctx) throws IfsException {
      // Marshal protocol: IN_VIEW_RETURN_VIEW
      beforeCall(httpRequest, inRecord, "AccessPlsql_Invoke", ctx);
      PlsqlInvocation result = (new PlsqlProxyInvocationProcessor(httpRequest)).invoke(inRecord);
      afterCall(result);
      return result;
   }

   private static void beforeCall(HttpServletRequest httpRequest, FndAbstractRecord record, String operationName, FndContext ctx) throws IfsException {
      initFndContext(httpRequest);

      if (FndDebug.isDebugCallSequenceOn()) {
         FndDebug.debug("PlsqlProxy &1.&2", "AccessPlsql", operationName);
      }
      if (FndDebug.isDebugSkeletonArgumentsOn()) {
         FndDebug.debug("PlsqlProxy Request Header");
         FndContext.debugContext();
         FndDebug.debug("PlsqlProxy Request Body");
         FndDebug.debugRecord(record);
      }
   }

   /**
    * Set value of response header.
    */
   private static void formatResponse(InvocationResult result) {
      try {
         FndServerContext ctx = FndServerContext.getCurrentServerContext();
         if (ctx.getExternalBodyType() == FndSerializeConstants.BODY_TYPE_XML)
            ctx.setBodyType("XML");
         result.header = FndBufferUtil.formatRecord(ctx);
      }
      catch (IfsException e) {
         /**
          * This should never happen. If it does we can not send a response to
          * the calling client (client gateway). The only sensible thing to do
          * is to rollback the current transaction.
          */
         rollback();
      }
      finally {
         FndContext.setCurrentContext(null);
      }
   }

   private static void afterCall(FndAbstractRecord rec) throws IfsException {
      FndServerContext ctx = FndServerContext.getCurrentServerContext();
      ctx.afterCall(FndContext.DONE);
      ctx.setSerializationTarget(FndRecordFormat.CLIENT);
      ctx.notifyStreamManagers(true);

      // debugResponse
      if (FndDebug.isDebugStubArgumentsOn()) {
         FndDebug.debug("PlsqlProxy Response Header");
         FndContext.debugContext();
         FndDebug.debug("PlsqlProxy Response Body");
         FndDebug.debugRecord(rec);
         FndProcessList.getInstance().debug();
      }
   }

   /**
    * Mark FND context with error status.
    */
   private static void rollback() {
      FndServerContext ctx = FndServerContext.getCurrentServerContext();
      ctx.afterCall(FndContext.ERROR);
      try {
         ctx.notifyStreamManagers(false);
      }
      catch(SystemException e) {
         if(FndDebug.isLogOn())
            FndDebug.log("   WARNING! Ignored error during transaction rollback: " + e);
      }
   }

   /**
    * Prepares FND context for a call to PLSQL Gateway in proxy-user mode.
    * The method assumes that the current JAAS user is already authenticated.
    * If run-as user is set in FND context then it will be used to execute the request.
    * The method checks system privilege and activity grants for PLSQL Gateway.
    * @param httpRquest current HTTP request
    */
   private static void initFndContext(HttpServletRequest httpRequest) throws IfsException {

      FndServerContext ctx = FndServerContext.getCurrentServerContext();

      // set current authenticated user as current user in FndContext (already done in initContext)
      //ctx.setApplicationUser(httpRequest.getUserPrincipal().getName());

      // set run-as user as current user in FndContext
      String runAsDirectoryId = ctx.getRunAs();
      if (runAsDirectoryId != null) {
         if (httpRequest.isUserInRole(FndConstants.TRUSTED_MODULE_ROLE)) {
            ctx.setApplicationUser(runAsDirectoryId);
         }
         else {
            System.err.println("User '" + ctx.getApplicationUser() + "' lacks the necessary system privilege to run as user '" + runAsDirectoryId + "'");
            throw new ifs.fnd.base.SecurityException("IMPERSONATEUSER:Access denied due to system privilege configuration");
         }
      }

      // check activity grants for PLSQL Gateway
      FndAbstractHandler.checkActivityGranted(ctx.getApplicationUser(), "AccessPlsql");
   }

   private static class InvocationResult {
      private byte[] header;
      private byte[] body;
   }
}
