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
 * File        : EJBConnectionPool.java
 * Description : Pool with EJB connections to PlsqlGateway
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  2002-Nov-13 - Created.
 *    Jacek P  2003-Feb-28 - Changed FNDAS to FNDEXT.
 *    Jacek P  2004-Jun-08 - Second synchronization with Montgomery.
 *    Jacek P  2004-Nov-02 - New profile handling
 *    Jacek P  2004-Nov-11 - Extends new abstract class ConnectionPool
 *    Jacek P  2005-Jan-28 - Changes in FNDEXT view names:
 *                           - Class name ManageUserProfileParameterViews.AdministrateUserProfiles_LoadUserProfile_Parameters
 *                             changed to ManageUserProfileViews.AdministrateUserProfiles_LoadUserProfile
 *                           - Class name ManageUserProfileParameterViews.AdministrateUserProfiles_SaveUserProfile_Parameters
 *                             changed to ManageUserProfileViews.AdministrateUserProfiles_SaveUserProfile
 *                           Affected methods: invokeTransaction().
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/03/02 buhilk Bug 87603, Changed view names for AnonymousAccess calls. Updated invokeTransaction().
 * 2009/04/24 buhilk Bug 82180, Replaces ClientServices with AnonymousAccess. Updated initConfig(), createEJBConnection() & invokeTransaction().
 * 2008/12/09 dusdlk Bug 77830, Updated the PASSWORD_CHECK in invokeTransaction() to refer to new view and method to check user name and password.
 * 2008/10/21 buhilk Bug 77910, Touched file because constant values were changed in the super class.
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Modified initConfig(), invokeTransaction() and createEJBConnection().
 * 2008/07/09 sadhlk Bug 73745, Added code to check DEBUG condition before calling debug() method.
 * 2008/05/09 sadhlk Bug id 73689, changed view names according to new view names in ClientServicesViews
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * 2006/10/02 riralk Bug id 60920, made the previous fix in Bug id 60861 more optimal using s static var to hold the property.
 *
 * 2006/09/28 riralk Bug id 60861, Checked property in getFndContext() to check whether profile conversion tool is running.
 *
 * 2006/08/03 madrse Bug id 60208, method getFndContext() calls FndContext.getCurrentContext() instead of new FndRemoteContext()
 *
 * Revision 1.3  2005/10/06 09:52:32  rahelk
 * moved getSecurityConfigRealm to AS specific class
 *
 * Revision 1.2  2005/09/28 12:13:06  japase
 * Property 'configuser' renamed to 'config_user'
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/14 08:02:08  rahelk
 * password management
 *
 * Revision 1.6  2005/08/17 10:52:03  rahelk
 * JAAS AS specific security plugin impl
 *
 * Revision 1.5  2005/06/09 09:55:25  japase
 * Removed the 'user' mode from profile handling
 *
 * Revision 1.4  2005/06/06 08:42:48  rakolk
 * Fixing the build (temporally commented some lines)
 *
 * Revision 1.3  2005/04/01 10:51:57  rahelk
 * JAAS: implemented calling EJB's directly
 *
 * Revision 1.2  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.5  2005/01/28 09:40:49  japase
 * Changed class names in FNDEXT API
 *
 * Revision 1.4  2005/01/27 17:25:33  jehuse
 * Commented call to Load and Save temporarly. New generated remote interface.
 *
 * Revision 1.3  2004/12/17 07:19:41  japase
 * *** empty log message ***
 *
 * Revision 1.2  2004/11/29 07:56:24  japase
 * Introduced common super class for all connection pools.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import ifs.application.fndnotebook.FndNoteBook;
import java.util.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import ifs.fnd.base.*;
import ifs.fnd.remote.*;
import ifs.fnd.remote.j2ee.*;
import ifs.fnd.record.*;
import ifs.application.plsqlservices.*;
import ifs.application.manageuserprofile.*;
import ifs.application.clientprofile.*;
import ifs.application.anonymousaccess.*;
import ifs.application.applicationsearch.*;
import ifs.application.managenotes.*;

import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.ejb.*;

import java.security.*;
import javax.security.auth.Subject;

import ifs.fnd.websecurity.SecurityHandler;

/**
 * A class that implements a pool with active connections to the PlsqlGateway EJB.
 * Request a new connection by calling the get() method, release the connection by
 * calling the release() method on the retrieved Slot instance.
 * To be used from ASPManager.
 */
class EJBConnectionPool extends ConnectionPool
{
   private String config_realm;
   private static final boolean PRFCONV = "true".equals(System.getProperty("fndweb.profile.conversion.tool.running","false"));

   /**
    * Private constructor called from init()
    */
   private EJBConnectionPool( int poolid )
   {
      super(poolid);
   }

   /**
    * Create the only static instance of the Pool.
    * Only one type of pool allowed at the same time.
    * Implements delayed initialization.
    */
   private static void init()
   {
      if(DEBUG) debug("EJBConnectionPool.init()");
      synchronized(LOCK)
      {
         if(pools==null)
            pools = new EJBConnectionPool(POOL_CNT);
         else if( !(pools instanceof EJBConnectionPool) )
            throw new RuntimeException("Connection Pool is already created!");
      }
   }

   //==========================================================================
   //  Static methodes to be called from ASPManager and ProfileUtils
   //==========================================================================

   /**
    * Return an instance of the ConnectionPool.Slot class from the linked list.
    * Create a new one if the list is empty.
    * Initialize if necessary using IFS Web Client configuration data.
    */
   static Slot get(int poolid, ASPConfig cfg) throws FndException
   {
      init();
      return get(poolid, cfg, null);
   }

   /**
    * Return an instance of the ConnectionPool.Slot class from the linked list.
    * Create a new one if the list is empty.
    * Initialize if necessary using properties.
    */
   static Slot get(int poolid, Properties properties ) throws FndException
   {
      init();
      return get(poolid, null, properties);
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================


   //==========================================================================
   //  Inherited interface
   //==========================================================================

   /**
    * Inherited interface. Called from synchronized code.
    * Initiate the Pool with configuration data from the configuration file or properties.
    */
   protected void initConfig( ASPConfig cfg, Properties properties ) throws FndException
   {
      if(cfg!=null)
      {
         config_user     = cfg.getConfigUser();
         config_password = cfg.getConfigPassword();
         //config_realm    = cfg.getSecurityConfigRealm();

         pools.config[PLSQLGTW]    .operation  = cfg.getParameter("ADMIN/FNDEXT/PLSQL/JNDI_NAME",    "ejb/ifs/fndweb/PlsqlBufferServices");
         pools.config[PROFILE_LOAD].operation  = cfg.getParameter("ADMIN/FNDEXT/PROFILES/JNDI_NAME", "ejb/ifs/fndweb/ManageUserProfile");
         pools.config[PROFILE_SAVE].operation  = cfg.getParameter("ADMIN/FNDEXT/PROFILES/JNDI_NAME", "ejb/ifs/fndweb/ManageUserProfile");

         pools.config[PASSWORD_ENABLED].operation = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/JNDI_NAME", "ejb/ifs/fndweb/AnonymousAccess");
         pools.config[PASSWORD_CHECK].operation   = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/JNDI_NAME", "ejb/ifs/fndweb/AnonymousAccess");
         pools.config[PASSWORD_CHANGE].operation  = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/JNDI_NAME", "ejb/ifs/fndweb/AnonymousAccess");

         pools.config[APPLICATIONSEARCH].operation  = cfg.getParameter("ADMIN/FNDEXT/APPLICATION_SEARCH/JNDI_NAME", "ejb/ifs/fndweb/ApplicationSearch");

         pools.config[PAGE_NOTES_LOAD].operation      = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/JNDI_NAME", "ejb/ifs/fndweb/ManageNotes");         
         pools.config[PAGE_NOTES_SAVE].operation      = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/JNDI_NAME", "ejb/ifs/fndweb/ManageNotes");         
      }
      else if(properties!=null)
      {
         config_user = properties.getProperty("config_user");

         if( Str.isEmpty(config_user) )
            throw new FndException("FNDJAPCONNNOCONFGUSR: The property 'config_user' is undefined.");

         config_password = properties.getProperty("configpassword");
         config_realm    = properties.getProperty("configrealm","client-login");

         pools.config[PLSQLGTW]    .operation  = properties.getProperty("plsql_jndi_name",  "ejb/ifs/fndweb/PlsqlBufferServices");
         pools.config[PROFILE_LOAD].operation  = properties.getProperty("profile_jndi_name","ejb/ifs/fndweb/ManageUserProfile");
         pools.config[PROFILE_SAVE].operation  = properties.getProperty("profile_jndi_name","ejb/ifs/fndweb/ManageUserProfile");

         pools.config[PASSWORD_ENABLED].operation = properties.getProperty("password_jndi_name", "ejb/ifs/fndext/AnonymousAccess");
         pools.config[PASSWORD_CHECK]  .operation = properties.getProperty("password_jndi_name", "ejb/ifs/fndext/AnonymousAccess");
         pools.config[PASSWORD_CHANGE] .operation = properties.getProperty("password_jndi_name", "ejb/ifs/fndext/AnonymousAccess");

         pools.config[PAGE_NOTES_LOAD].operation     = properties.getProperty("pagenote_jndi_name", "ejb/ifs/fndweb/ManageNotes");
         pools.config[PAGE_NOTES_SAVE].operation     = properties.getProperty("pagenote_jndi_name", "ejb/ifs/fndweb/ManageNotes");
      }
      else
         throw new RuntimeException("FNDEJBCONNPOOLNOCFG: Required configuration is not available!.");
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Perform the transaction to Extended Server.
    */
   protected Object invokeTransaction( Slot slot, Object obj ) throws FndException
   {
      if(DEBUG) debug(this+"invokeTransaction("+System.identityHashCode(obj)+")");

      try
      {
         //SecurityHandler.getConfigSubject(config_realm, config_user, config_password);
         SecurityHandler.getConfigSubject();
         switch(slot.poolid)
         {
            case PLSQLGTW:
               BufferPlsqlGatewayRemote plejb = (BufferPlsqlGatewayRemote)slot.prv;
               Buffer request = (Buffer)obj;
               return plejb.plsqlGateway_Perform(request);
            case PROFILE_LOAD:
               ManageUserProfileRemote lprfejb = (ManageUserProfileRemote)slot.prv;
               if( obj instanceof ManageUserProfileViews.V11 )   //ManageUserProfileViews.V11
               {
                  ManageUserProfileViews.V11 param =
                     (ManageUserProfileViews.V11)obj;
                  param = (ManageUserProfileViews.V11)
                             lprfejb.manageUserProfile_LoadUserProfile(param, getFndContext() ).getResult();
                  return param.result; //UserProfileArray
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR1: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());

            case PROFILE_SAVE:
               ManageUserProfileRemote sprfejb = (ManageUserProfileRemote)slot.prv;
               if( obj instanceof ManageUserProfileViews.V12 )
               {
                  ManageUserProfileViews.V12 param =
                     (ManageUserProfileViews.V12)obj;
                  sprfejb.manageUserProfile_SaveUserProfile(param, getFndContext() );
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());
               return null;

            case PASSWORD_ENABLED:
               AnonymousAccessRemote pwdejb = (AnonymousAccessRemote)slot.prv;
               if( obj instanceof AnonymousAccessViews.V4 )
               {
                  AnonymousAccessViews.V4 param =
                     (AnonymousAccessViews.V4)obj;
                  param = (AnonymousAccessViews.V4)
                           pwdejb.anonymousAccess_IsChangePasswordEnabled(param, getFndContext()).getResult();

                  return param.result; //FndBoolean
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());

            case PASSWORD_CHECK:
               AnonymousAccessRemote pwdchk_ejb = (AnonymousAccessRemote)slot.prv;
               if( obj instanceof AnonymousAccessViews.V3)
               {
                  AnonymousAccessViews.V3 param =
                     (AnonymousAccessViews.V3)obj;

                  FndContext ctx = FndContext.getCurrentContext();

                  param = (AnonymousAccessViews.V3)
                           pwdchk_ejb.anonymousAccess_GetPasswordExpiration(param, ctx).getResult();

                  return param;
               } 
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());

            case PASSWORD_CHANGE:
               AnonymousAccessRemote pwdchg_ejb = (AnonymousAccessRemote)slot.prv;
               if( obj instanceof AnonymousAccessViews.V2)
               {
                  AnonymousAccessViews.V2 param =
                     (AnonymousAccessViews.V2)obj;

                  FndContext ctx = FndContext.getCurrentContext();

                  pwdchg_ejb.anonymousAccess_ChangePassword(param, ctx);
                  return null;
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());

            case APPLICATIONSEARCH:
               return slot.prv;
            case PAGE_NOTES_LOAD:
               if(obj instanceof FndQueryRecord)
               {
                  ManageNotesRemote note_ejb = (ManageNotesRemote) slot.prv;
                  FndContext ctx = FndContext.getCurrentContext();
                  FndQueryRecord rec = (FndQueryRecord) obj;                  
                  return note_ejb.manageNotes_QueryFndNoteBook(rec, ctx).getResult();
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());
               
            case PAGE_NOTES_SAVE:
               if(obj instanceof FndNoteBook)
               {
                  ManageNotesRemote note_ejb = (ManageNotesRemote) slot.prv;
                  FndContext ctx = FndContext.getCurrentContext();
                  FndNoteBook clntb = (FndNoteBook) obj;
                  return note_ejb.manageNotes_SaveFndNoteBook(clntb, ctx).getResult();
               }
               else
                  throw new FndException("FNDEJBPOOLTYPEERR2: Type '&1' doesn't supported by EJBConnectionPool.",obj.getClass().getName());
               
            default:
               throw new FndException("FNDEJBPOOLIDERR: No such pool");
         }
      }
      catch( IfsException x )
      {
         throw new FndException(x);
      }
      catch( RemoteException x )
      {
         throw new FndException(x);
      }
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Create the connection provider object used for transaction.
    */
   protected Object createConnectionProvider( int poolid, Config config )
   {
      try
      {
         return createEJBConnection( poolid, config.operation );
      }
      catch( Exception any )
      {
         if(DEBUG) debug(this+"Error! Could not create EJB connection:\n"+Str.getStackTrace(any));
         Alert.add(this+"Error! Could not create EJB connection:\n"+Str.getStackTrace(any));
         throw new RuntimeException(any);
      }
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Disconnect the provider object from Extended Server.
    */
   protected void disconnectProvider( Slot slot )
   {
      try
      {
         ((EJBObject)slot.prv).remove();
      }
      catch( Exception e )
      {
         if (DEBUG) debug(this+"Warning! Disconnect of EJB connection failed:\n"+Str.getStackTrace(e));
         Alert.add(this+"Warning! Disconnect of EJB connection failed:\n"+Str.getStackTrace(e));
      }
   }

   //=======================================================================
   //  EJB connection
   //=======================================================================

   /**
    * Performs a JNDI lookup of given EJB.
    */
   private EJBObject createEJBConnection( int poolid, String jndi_name ) throws NamingException, RemoteException, CreateException, FndException
   {
      Context initial = new InitialContext();
      Object  objref  = initial.lookup(jndi_name);
      Object  home    = null;
      if(DEBUG) debug("EJBConnectionPool: JNDI lookup of '"+jndi_name+"' returned '"+(objref==null ? "null" : objref.getClass().getName())+"'");

      //SecurityHandler.getConfigSubject(config_realm, config_user, config_password);
      SecurityHandler.getConfigSubject();

      switch(poolid)
      {
         case PLSQLGTW:
            home = PortableRemoteObject.narrow(objref, BufferPlsqlGatewayHome.class);
            return ((BufferPlsqlGatewayHome)home).create();

         case PROFILE_LOAD:
         case PROFILE_SAVE:
            home = PortableRemoteObject.narrow(objref, ManageUserProfileHome.class);
            return ((ManageUserProfileHome)home).create();

         case PASSWORD_ENABLED:
         case PASSWORD_CHANGE:
         case PASSWORD_CHECK:
            home = PortableRemoteObject.narrow(objref, AnonymousAccessHome.class);
            return ((AnonymousAccessHome)home).create();
            
         case APPLICATIONSEARCH:
            home = PortableRemoteObject.narrow(objref, ApplicationSearchHome.class);
            return ((ApplicationSearchHome)home).create();

         case PAGE_NOTES_LOAD:
         case PAGE_NOTES_SAVE:
            home = PortableRemoteObject.narrow(objref, ManageNotesHome.class);
            return ((ManageNotesHome)home).create();

         default:
            throw new FndException("FNDEJBPOOLIDERR2: No such pool");
      }
   }

   //=======================================================================
   //  FND security
   //=======================================================================

   /**
    * Create and preparate FNDEXT context.
    */
   private FndContext getFndContext() throws IfsException
   {           
      return PRFCONV? new FndRemoteContext(): FndContext.getCurrentContext();
   }

   /**
    * Append security data to given AutoString.
    */
   /*
   private void appendSecurityString( AutoString data, String str )
   {
      data.append(String.valueOf(str.length()));
      data.append(":");
      data.append(str);
   }
   */

   /**
    * Creates a FND_SECURITY item in the specified request header
    * depending on ADMIN/FNDAS-section in the ASPConfig file.
    */
   /*
   private String insertSecurityData()
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
      return data.toString();
   }
   */

   /*
   class ActionHandler implements PrivilegedExceptionAction
   {
      private BufferPlsqlGatewayRemote remote = null;
      private Buffer request;
      private BufferPlsqlGatewayHome home = null;

      ActionHandler(BufferPlsqlGatewayRemote remote, Buffer request)
      {
         this.remote  = remote;
         this.request = request;
      }

      ActionHandler(BufferPlsqlGatewayHome home)
      {
         this.home = home;
      }

      public Object run() throws Exception
      {
         if (home != null)
            return create();
         else
            return perform();
      }

      private Object create() throws Exception
      {
         return home.create();
      }

      private Object perform() throws Exception
      {
         return remote.plsqlGateway_Perform(request);
      }
   }
   */
}
