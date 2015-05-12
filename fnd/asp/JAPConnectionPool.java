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
 * File        : JAPConnectionPool.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2001-Jul-20 - Created.
 *    Daniel S 2002-Jan-14 - Added support for credentials.
 *    Jacek P  2002-Jan-22 - Changed name from TMConnectionPool to ORBConnectionPool.
 *                           Aray of pools used by framework insteayd of only one.
 *    Marek D  2002-Nov-21 - setConnectionString on Server object for Plsql Gateway
 *    Jacek P  2003-Feb-28 - Changed name from ORBConnectionPool to JAPConnectionPool.
 *                           Removed support for stand alone mode.
 *    Jacek P  2004-Jun-08 - Second synchronization with Montgomery.
 *    Suneth M 2004-Aug-04 - Changed duplicate localization tags.
 *    Jacek P  2004-Nov-11 - Extends new abstract class ConnectionPool
 *    Jacek P  2004-Dec-03 - Defined configuration parameters for profil handling
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/04/24 buhilk Bug 82180, Replaces ClientServices with AnonymousAccess. Updated initConfig().
 * 2008/12/09 dusdlk Bug 77830, Updated the PASSWORD_CHECK config in initConfig() to refer to method to check user name and password.
 * 2008/10/21 buhilk Bug 77910, Touched file because constant values were changed in the super class.
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Modified initConfig() and invokeTransaction().
 * 2008/07/09 sadhlk Bug 73745, Added code to check DEBUG condition before calling debug() method.
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * Revision 1.2  2005/09/28 12:11:48  japase
 * Property 'config_user' renamed to 'configuser'
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.7  2005/09/14 08:02:08  rahelk
 * password management
 *
 * Revision 1.6  2005/06/09 09:49:33  japase
 * Removed the 'user' mode from profile handling
 *
 * Revision 1.5  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 * Revision 1.4  2005/03/08 07:15:29  madrse
 * Commented out access to java access provider
 *
 * Revision 1.3  2005/02/28 10:21:33  japase
 * *** empty log message ***
 *
 * Revision 1.2  2005/02/25 14:00:12  japase
 * Changed property name
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.4  2004/12/17 07:19:41  japase
 * *** empty log message ***
 *
 * Revision 1.3  2004/12/03 07:04:32  japase
 * *** empty log message ***
 *
 * Revision 1.2  2004/11/29 07:56:24  japase
 * Introduced common super class for all connection pools.
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;

import ifs.fnd.ap.*;

/**
 * A class that implements a pool with active connections to the ORB based Transaction Manager.
 * Request a new connection by calling the get() method, release the connection by calling the
 * release() method on the retrieved Slot instance.
 * To be used from ASPManager.
 */
class JAPConnectionPool extends ConnectionPool
{
   /**
    * Private constructor called from init()
    */
   private JAPConnectionPool( int poolid )
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
      if(DEBUG) debug("JAPConnectionPool.init()");
      synchronized(LOCK)
      {
         if(pools==null)
            pools = new JAPConnectionPool(POOL_CNT);
         else if( !(pools instanceof JAPConnectionPool) )
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
         String url       = cfg.getParameter("ADMIN/FNDEXT/URL", "");
         String anyms_url = cfg.getParameter("ADMIN/FNDEXT/ANONYMOUS_URL", "");
         config_user      = cfg.getConfigUser();
         config_password  = cfg.getConfigPassword();


         pools.config[PLSQLGTW].interfname     = cfg.getParameter("ADMIN/FNDEXT/PLSQL/INTERFACE",         "PlsqlGateway");
         pools.config[PLSQLGTW].operation      = cfg.getParameter("ADMIN/FNDEXT/PLSQL/OPERATION",         "Perform");
         pools.config[PLSQLGTW].url            = url;

         pools.config[PROFILE_LOAD].interfname = cfg.getParameter("ADMIN/FNDEXT/PROFILES/INTERFACE",      "ManageUserProfile");
         pools.config[PROFILE_LOAD].operation  = cfg.getParameter("ADMIN/FNDEXT/PROFILES/OPERATION_LOAD", "LoadUserProfile");
         pools.config[PROFILE_LOAD].url        = url;

         pools.config[PROFILE_SAVE].interfname = cfg.getParameter("ADMIN/FNDEXT/PROFILES/INTERFACE",      "ManageUserProfile");
         pools.config[PROFILE_SAVE].operation  = cfg.getParameter("ADMIN/FNDEXT/PROFILES/OPERATION_SAVE", "SaveUserProfile");
         pools.config[PROFILE_SAVE].url        = url;

         pools.config[PASSWORD_ENABLED].interfname = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/INTERFACE",         "AnonymousAccess");
         pools.config[PASSWORD_ENABLED].operation  = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/OPERATION_ENABLED", "IsChangePasswordEnabled");
         pools.config[PASSWORD_ENABLED].url        = anyms_url;

         pools.config[PASSWORD_CHECK].interfname = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/INTERFACE",           "AnonymousAccess");
         pools.config[PASSWORD_CHECK].operation  = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/OPERATION_CHECK",     "GetPasswordExpiration");
         pools.config[PASSWORD_CHECK].url        = anyms_url;

         pools.config[PASSWORD_CHANGE].interfname = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/INTERFACE",          "AnonymousAccess");
         pools.config[PASSWORD_CHANGE].operation  = cfg.getParameter("ADMIN/FNDEXT/PASSWORD/OPERATION_CHANGE",   "ChangePassword");
         pools.config[PASSWORD_CHANGE].url        = anyms_url;

         pools.config[APPLICATIONSEARCH].url        = url;
         
         pools.config[PAGE_NOTES_LOAD].interfname = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/INTERFACE",       "ManageNotes");
         pools.config[PAGE_NOTES_LOAD].operation  = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/OPERATION_LOAD",  "QueryFndNoteBook");
         pools.config[PAGE_NOTES_LOAD].url        = url;

         pools.config[PAGE_NOTES_SAVE].interfname = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/INTERFACE",        "ManageNotes");
         pools.config[PAGE_NOTES_SAVE].operation  = cfg.getParameter("ADMIN/FNDEXT/PAGENOTES/OPERATION_SAVE",   "SaveFndNoteBook");
         pools.config[PAGE_NOTES_SAVE].url        = url;
      }
      else if(properties!=null)
      {
         config_user = properties.getProperty("config_user");

         if( Str.isEmpty(config_user) )
            throw new FndException("FNDJAPCONNNOCONFGUSR: The property 'config_user' is undefined.");

         config_password = properties.getProperty("configpassword");

         pools.config[PLSQLGTW].interfname = properties.getProperty("pl_interface", "PlsqlGateway");
         pools.config[PLSQLGTW].operation  = properties.getProperty("pl_operation", "Perform");
         pools.config[PLSQLGTW].url        = properties.getProperty("url",          "");

         pools.config[PROFILE_LOAD].interfname  = properties.getProperty("prf_interface",      "ManageUserProfile");
         pools.config[PROFILE_LOAD].operation   = properties.getProperty("prf_operation_load", "LoadUserProfile");
         pools.config[PROFILE_LOAD].url         = properties.getProperty("url",                "");

         pools.config[PROFILE_SAVE].interfname  = properties.getProperty("prf_interface",      "ManageUserProfile");
         pools.config[PROFILE_SAVE].operation   = properties.getProperty("prf_operation_save", "SaveUserProfile");
         pools.config[PROFILE_SAVE].url         = properties.getProperty("url",                "");

         pools.config[PASSWORD_ENABLED].interfname = properties.getProperty("pwd_interface",         "AnonymousAccess");
         pools.config[PASSWORD_ENABLED].operation  = properties.getProperty("pwd_operation_enabled", "IsChangePasswordEnabled");
         pools.config[PASSWORD_ENABLED].url        = properties.getProperty("url",                "");

         pools.config[PASSWORD_CHECK].interfname = properties.getProperty("pwd_interface",       "AnonymousAccess");
         pools.config[PASSWORD_CHECK].operation  = properties.getProperty("pwd_operation_check", "GetPasswordExpiration");
         pools.config[PASSWORD_CHECK].url        = properties.getProperty("url",                "");

         pools.config[PASSWORD_CHANGE].interfname = properties.getProperty("pwd_interface",        "AnonymousAccess");
         pools.config[PASSWORD_CHANGE].operation  = properties.getProperty("pwd_operation_change", "ChangePassword");
         pools.config[PASSWORD_CHANGE].url        = properties.getProperty("url",                "");

         pools.config[PAGE_NOTES_LOAD].interfname = properties.getProperty("notes_interface",       "ManageNotes");
         pools.config[PAGE_NOTES_LOAD].operation  = properties.getProperty("notes_operation_load",  "queryFndNoteBook");
         pools.config[PAGE_NOTES_LOAD].url        = properties.getProperty("url",                "");

         pools.config[PAGE_NOTES_SAVE].interfname = properties.getProperty("notes_interface",        "ManageNotes");
         pools.config[PAGE_NOTES_SAVE].operation  = properties.getProperty("notes_operation_save",   "SaveFndNoteBook");
         pools.config[PAGE_NOTES_SAVE].url        = properties.getProperty("url",                "");
      }
      else
         throw new RuntimeException("FNDJAPCONNPOOLNOCFG: Required configuration is not available!.");
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Perform the transaction to Extended Server.
    */
   protected Object invokeTransaction( Slot slot, Object obj ) throws FndException
   {
      try
      {
         Server srv = (Server)slot.prv;
         switch(slot.poolid)
         {
            case PLSQLGTW:
               if( !Str.isEmpty(config[slot.poolid].url) )
                  srv.setConnectionString(config[slot.poolid].url);
               if(DEBUG) debug("JAPConnectionPool.invokeTransaction():\n   "+config[slot.poolid].url+": "+config[slot.poolid].interfname+"."+config[slot.poolid].operation);
               return srv.invoke(config[slot.poolid].interfname, config[slot.poolid].operation, (Buffer)obj, null, null);

            case PROFILE_LOAD:
            case PROFILE_SAVE:
               if( !Str.isEmpty(config[slot.poolid].url) )
                  srv.setConnectionString(config[slot.poolid].url);
               if(DEBUG) debug("JAPConnectionPool.invokeTransaction():\n   "+config[slot.poolid].url+": "+config[slot.poolid].interfname+"."+config[slot.poolid].operation);
               return srv.invoke(config[slot.poolid].interfname, config[slot.poolid].operation, (Record)obj, null, null);

            case PASSWORD_ENABLED:
            case PASSWORD_CHECK:
            case PASSWORD_CHANGE:
               if( !Str.isEmpty(config[slot.poolid].url) )
                  srv.setConnectionString(config[slot.poolid].url);
               if(DEBUG) debug("JAPConnectionPool.invokeTransaction():\n   "+config[slot.poolid].url+": "+config[slot.poolid].interfname+"."+config[slot.poolid].operation);
               return srv.invoke(config[slot.poolid].interfname, config[slot.poolid].operation, (Record)obj, null, null);
            case APPLICATIONSEARCH:
               if( !Str.isEmpty(config[slot.poolid].url) )
                  srv.setConnectionString(config[slot.poolid].url);
               srv.saveToThread();
               return srv;                  

            case PAGE_NOTES_LOAD:
            case PAGE_NOTES_SAVE:
               if( !Str.isEmpty(config[slot.poolid].url) )
                  srv.setConnectionString(config[slot.poolid].url);
               if(DEBUG) debug("JAPConnectionPool.invokeTransaction():\n   "+config[slot.poolid].url+": "+config[slot.poolid].interfname+"."+config[slot.poolid].operation);

               return srv.invoke(config[slot.poolid].interfname, config[slot.poolid].operation, (Record)obj, null, null);

            default:
               throw new FndException("FNDJAPPOOLIDERR: No such pool");
         }
      }
      catch( APException x )
      {
         if(DEBUG) debug(Str.getStackTrace(x));
         Alert.add(Str.getStackTrace(x));
         throw new FndException(x).addCaughtException(x);
      }
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Create the connection provider object used for transaction.
    */
   protected Object createConnectionProvider( int poolid, Config config )
   {
      Server srv = new Server();
      srv.setCredentials(config_user, config_password);

      return srv;
   }

   /**
    * Inherited interface. Called from synchronized code.
    * Disconnect the provider object from Extended Server.
    */
   protected void disconnectProvider( Slot slot )
   {
      ((Server)slot.prv).disconnect();
   }
}

