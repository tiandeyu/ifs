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
 * File        : ConnectionPool.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2004-Nov-10 - Created.
 *    Jacek P  2004-Nov-24 - Removed standalone and load balance group.
 *                           Also removed LDAPAUTH and ORAAUTH.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/09/10 sadhlk Bug 76949, F1PR461 - Notes feature in Web client, Added static variables PAGE_NOTES_LOAD,
 *                                        PAGE_NOTES_SAVE and Modified showContents().
 * 2008/06/26 mapelk Bug id 74852, Programming Model for Activities.
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/09/14 08:02:07  rahelk
 * password management
 *
 * Revision 1.2  2005/03/21 09:19:05  rahelk
 * JAAS implementation
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.1  2004/11/29 07:56:24  japase
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
 * A class that implements a pool with active connections to IFS Extended Server.
 * Request a new connection by calling the get() method, release the connection by calling the
 * release() method on the retrieved ConnectionPool.Slot instance.
 * To be used from ASPManager.
 * This class works as super class for particular implementations: using RMI or JAP.
 */
abstract class ConnectionPool
{
   //==========================================================================
   // Static variables
   //==========================================================================

   public    static       boolean DEBUG        = Util.isDebugEnabled("ifs.fnd.asp.ConnectionPool");

   public    static final int     PLSQLGTW     = 0;
   public    static final int     PROFILE_LOAD = 1;
   public    static final int     PROFILE_SAVE = 2;
   public    static final int     PASSWORD_ENABLED = 3;
   public    static final int     PASSWORD_CHECK   = 4;
   public    static final int     PASSWORD_CHANGE  = 5;
   public    static final int     APPLICATIONSEARCH = 6;
   public    static final int     PAGE_NOTES_LOAD  = 7;
   public    static final int     PAGE_NOTES_SAVE  = 8;
   public    static final int     COMMON_JAP_POOL = 9;

   protected static final int     POOL_CNT     = 10;            //has to be equal the highest pool constant +1
   protected static final Object  LOCK         = new Object(); //used for synchronization before the pool is created

   protected static ConnectionPool pools;

   //==========================================================================
   // Instance variables
   //==========================================================================

   //protected String systype   = null;
   //protected String sysid     = null;
   //protected String syssecret = null;
   protected String config_user = null;
   protected String config_password = null;

   protected  Config[]  config;
   private    Slot[]    first;
   private    int[]     total;
   private    int[]     inuse;


   //==========================================================================
   //  Construction of the only one static instance of ConnectionPool
   //==========================================================================

   /**
    * Protected constructor
    */
   protected ConnectionPool( int poolcnt )
   {
      total  = new int[poolcnt];
      inuse  = new int[poolcnt];
      first  = new Slot[poolcnt];
      config = new Config[poolcnt];
      for(int i=0; i<config.length; i++)
         config[i] = new Config();
      if(DEBUG) debug("ConnectionPool.<init>:"+poolcnt);
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================

   /**
    * Create a new instance of the inner class Slot
    */
   protected Slot newSlot( int poolid, ASPConfig conf  )
   {
      return new Slot(poolid, conf);
   }

   //==========================================================================
   //  Static methods to be called from subclasses
   //==========================================================================

   /**
    * Return an instance of the Slot class from the linked list.
    * Create a new one if the list is empty.
    */
   static Slot get( int poolid, ASPConfig cfg, Properties properties ) throws FndException
   {
      if(DEBUG) debug("ConnectionPool.get("+poolid+","+cfg+","+properties+")");

      Slot slot = null;

      synchronized(pools)
      {
         if(pools.config_user==null)
            pools.initConfig(cfg, properties);

         if( pools.first[poolid]!=null )
         {
            slot = pools.first[poolid];
            pools.first[poolid] = slot.next;
            slot.next = null;
            pools.inuse[poolid]++;
         }
      }

      if( slot==null )
      {
         slot = pools.newSlot(poolid, cfg);

         synchronized(pools)
         {
            pools.total[poolid]++;
            pools.inuse[poolid]++;
         }
      }

      if(DEBUG) debug("\tslot="+slot+"\n\tfirst="+pools.first[poolid]+"\n\ttotal="+pools.total[poolid]+"\n\tinuse="+pools.inuse[poolid] );
      return slot;
   }

   /**
    * Add the released instance to the linked list.
    */
   private static void addFirst( int poolid, Slot slot )
   {
      if(DEBUG) debug("ConnectionPool.addFirst("+poolid+","+slot+")");

      synchronized(pools)
      {
         slot.next  = pools.first[poolid];
         pools.first[poolid] = slot;
         pools.inuse[poolid]--;
         if(DEBUG) debug("\tslot="+slot+"\n\tfirst="+pools.first[poolid]+"\n\ttotal="+pools.total[poolid]+"\n\tinuse="+pools.inuse[poolid] );
      }
   }

   /**
    * Clear the whole pool
    */
   static void clear()
   {
      synchronized(pools)
      {
         for(int i=0; i<pools.first.length; i++)
         {
            while(true)
            {
               if(pools.first[i]==null) break;
               pools.first[i].disconnect();
               pools.first[i] = pools.first[i].next;
            }
            pools.total[i] = 0;
            pools.inuse[i] = 0;
         }
      }
   }

   //==========================================================================
   //  Debugging, error
   //==========================================================================

   /**
    * Write a line of text to the debug output.
    */
   protected static void debug( String line )
   {
      Util.debug(line);
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================


   //==========================================================================
   //  To be inherited
   //==========================================================================

   /**
    * To be overriden. Called from synchronized code.
    * Initiate the Pool with configuration data from the configuration file or properties.
    */
   protected abstract void initConfig( ASPConfig cfg, Properties properties ) throws FndException;

   /**
    * To be overriden. Called from synchronized code.
    * Perform the transaction to Extended Server.
    */
   protected abstract Object invokeTransaction( Slot slot, Object obj ) throws FndException;

   /**
    * To be overriden. Called from synchronized code.
    * Create the connection provider object used for transaction.
    */
   protected abstract Object createConnectionProvider( int poolid, Config config );

   /**
    * To be overriden. Called from synchronized code.
    * Disconnect the provider object from Extended Server.
    */
   protected abstract void   disconnectProvider( Slot slot );

   //==========================================================================
   //==========================================================================
   //  Inner class Config
   //==========================================================================
   //==========================================================================

   /**
    * Inner class containing communication data
    */
   class Config
   {
      protected String  interfname;
      protected String  operation;  // JNDI name for EJB
      protected String  url;        // connection string to IFS Client Gateway
   }

   //==========================================================================
   //==========================================================================
   //  Inner class Slot
   //==========================================================================
   //==========================================================================

   /**
    * The inner class for encapsulation of the connection provider object.
    */
   class Slot
   {
      protected Slot    next;
      protected int     poolid;
      protected long    lastused;
      protected Object  prv;

      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor called from newSlot()
       */
      private Slot( int poolid, ASPConfig conf )
      {
         if (DEBUG)
         {
            debug("Creating a slot for :");
            debug("   poolid:" + poolid);
         }
         this.lastused = System.currentTimeMillis();
         this.next     = null;
         this.poolid   = poolid;

         if (poolid == ConnectionPool.COMMON_JAP_POOL)
         {
            try {
               // Common connection pool is used to comunicate with general activities
               if (DEBUG)
               {
                  debug(" Common JAP connection");
               }
               Server srv = new Server();
               srv.setCredentials(conf.getConfigUser(), conf.getConfigPassword());
               srv.setConnectionString(conf.getParameter("ADMIN/FNDEXT/URL"));
               prv = srv;
               return;
            } catch (FndException ex) {
               ex.printStackTrace();
            }
         }

         this.prv      = createConnectionProvider( poolid, pools.config[poolid] );
      }


      /**
       * Return the encapsulated instance of the connection provider class.
       */
      Object getConnectionProvider()
      {
         return prv;
      }


      /**
       * Run the trancastion.
       */
      Object invoke( Object obj ) throws FndException
      {
         return invokeTransaction(this, obj);
      }


      /**
       * Run the trancastion. Same as invoke(), but expects Buffer as argument and returns Buffer.
       */
      Buffer perform( Buffer buf ) throws FndException
      {
         return (Buffer)invokeTransaction(this, buf);
      }


      /**
       * Update the timestamp and put back the object at the beginning of the linked list.
       */
      void release()
      {
         if(DEBUG) debug(this+"JAPConnectionPool$Slot.release()");

         lastused = System.currentTimeMillis();
         pools.addFirst(poolid,this);
      }


      /**
       * Disconnect the provider object from the Extended Server.
       */
      private void disconnect()
      {
         disconnectProvider(this);
      }
   }

   //=======================================================================
   //  Debugging
   //=======================================================================

   /**
    * Return the total number of active entries in the pool.
    */
   public static int getTotal( int poolid )
   {
      return pools.total[poolid];
   }


   /**
    * Return the number of entries that are currently in use.
    */
   public static int getInUse( int poolid )
   {
      return pools.inuse[poolid];
   }


   /**
    * Create a list with timestamps for all connections in the linked list.
    */
   private String getTimestamps()
   {
      AutoString buf = new AutoString();
      for(int i=0; i<first.length; i++)
      {
         Slot s = first[i];
         buf.append("\n\npool: "+i+"\n");
         while(s!=null)
         {
            buf.append("\t\t",(new Date(s.lastused)).toString(), "\n\t\t");
            s = s.next;
         }
      }
      return buf.toString();
   }


   /**
    * Show the contents of the pool.
    */
   static void showContents()
   {
      synchronized(pools)
      {
         String out = "\n\n\n\tORB Connection Pool:\n\t=======================\n"+
                      "\n\t           PLSQLGTW   PROFILE_LOAD PROFILE_SAVE PAGE_NOTES_LOAD PAGE_NOTES_SAVE"+
                      "\n\tTotal    : " + pools.total[PLSQLGTW] + pools.total[PROFILE_LOAD] + pools.total[PROFILE_SAVE] + pools.total[PAGE_NOTES_LOAD] + pools.total[PAGE_NOTES_SAVE] +
                      "\n\tIn use   : " + pools.inuse[PLSQLGTW] + pools.inuse[PROFILE_LOAD] + pools.inuse[PROFILE_SAVE] + pools.inuse[PAGE_NOTES_LOAD] + pools.inuse[PAGE_NOTES_SAVE] +
                      "\n\n\tTimstamps for connections in list:\n\t==================================\n\n";
         out = out + pools.getTimestamps();
         debug(out+"\n\n\n");
      }
   }
}

