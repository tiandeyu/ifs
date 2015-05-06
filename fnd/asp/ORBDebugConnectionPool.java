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
 * File        : ORBConnectionPool.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2002-Oct-04 - Created according to ORDConnectionPool.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.ap.*;

/**
 * A class that implements a pool with active connections to the ORB based Transaction Manager.
 * Request a new connection by calling the get() method, release the connection by calling the
 * release() method on the retrieved Slot instance.
 * To be used from ASPManager.
 */
public class ORBDebugConnectionPool
{
   //==========================================================================
   // Static variables
   //==========================================================================

   public static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.asp.ORBDebugConnectionPool");

   public static final int DEBUGGER = 0;

   private static ORBDebugConnectionPool pools = new ORBDebugConnectionPool(1);

   private static String systype   = null;
   private static String sysid     = null;
   private static String syssecret = null;


   //==========================================================================
   // Instance variables
   //==========================================================================

   private Slot[]   first;
   private Config[] config;
   private int[]    total;
   private int[]    inuse;


   //==========================================================================
   //  Construction of the only one static instance of TMConnectionPool
   //==========================================================================

   /**
    * Private constructor
    **/
   private ORBDebugConnectionPool( int poolcnt )
   {
      total  = new int[poolcnt];
      inuse  = new int[poolcnt];
      first  = new Slot[poolcnt];
      config = new Config[poolcnt];
      for(int i=0; i<config.length; i++)
         config[i] = new Config();
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================

   /**
    * Create a new instance of the inner class Slot
    */
   private Slot newSlot( int poolid )
   {
      return new Slot(poolid);
   }

   //==========================================================================
   //  Static methods to be called from ASPManager
   //==========================================================================

   public static Slot get(int poolid)
   {
      synchronized(pools)
      {
         if(sysid==null)
         {
            systype   = ASPConfig.getSystemType();
            sysid     = ASPConfig.getSystemId();
            syssecret = ASPConfig.getSharedSecret();

            pools.config[DEBUGGER].interfname = "Utility_Debug_Server_Handler";
            pools.config[DEBUGGER].operation = "setOutput";
            //pools.config[DEBUGGER].loadbalgrp = "rahelk"; //cfg.getLoadBalancingGroup();
            pools.config[DEBUGGER].standalone=false;

         }

         Slot slot;
         if(pools.first[poolid]==null)
         {
            pools.total[poolid]++;
            pools.inuse[poolid]++;
            slot = pools.newSlot(poolid);
         }
         else
         {
            slot = pools.first[poolid];
            pools.first[poolid] = slot.next;
            slot.next = null;
            pools.inuse[poolid]++;
         }
         if(DEBUG) debug("\tslot="+slot+"\n\tfirst="+pools.first[poolid]+"\n\ttotal="+pools.total[poolid]+"\n\tinuse="+pools.inuse[poolid] );
         return slot;
      }
   }


   public static Slot get(int poolid, String load_group)
   {
      synchronized(pools)
      {
         if(sysid==null)
         {
            systype   = ASPConfig.getSystemType();
            sysid     = ASPConfig.getSystemId();
            syssecret = ASPConfig.getSharedSecret();

            pools.config[DEBUGGER].interfname = "Utility_Debug_Server_Handler";
            pools.config[DEBUGGER].operation = "setOutput";
            //pools.config[DEBUGGER].loadbalgrp = "rahelk"; //cfg.getLoadBalancingGroup();
            pools.config[DEBUGGER].standalone=false;

         }

         pools.config[DEBUGGER].loadbalgrp = load_group;

         Slot slot;
         if(pools.first[poolid]==null)
         {
            pools.total[poolid]++;
            pools.inuse[poolid]++;
            slot = pools.newSlot(poolid);
         }
         else
         {
            slot = pools.first[poolid];
            pools.first[poolid] = slot.next;
            slot.next = null;
            pools.inuse[poolid]++;
         }
         if(DEBUG) debug("\tslot="+slot+"\n\tfirst="+pools.first[poolid]+"\n\ttotal="+pools.total[poolid]+"\n\tinuse="+pools.inuse[poolid] );
         return slot;
      }
   }


   /**
    * Add the released instance to the linked list.
    */
   private static void addFirst( int poolid, Slot slot )
   {
      if(DEBUG) debug("ORBDebugConnectionPool.addFirst("+poolid+","+slot+")");

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

   private static void debug( String line )
   {
      Util.debug(line);
   }

   //==========================================================================
   //==========================================================================
   //  Inner class Config
   //==========================================================================
   //==========================================================================

   class Config
   {
      private String  interfname;
      private String  operation;
      private String  loadbalgrp;
      private boolean standalone;
   }

   //==========================================================================
   //==========================================================================
   //  Inner class Slot
   //==========================================================================
   //==========================================================================

   /**
    * The inner class for encapsulation of the Server object.
    */
   public class Slot
   {
      private Slot    next;
      private int     poolid;
      private Server  srv;
      private long    lastused;

      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor called from newSlot()
       */
      private Slot( int poolid )
      {
         this.lastused = System.currentTimeMillis();
         this.next     = null;
         this.poolid   = poolid;

         this.srv      = new Server();
         /* JAAS
         this.srv.setTrustedMode(true);

         if("".equals(syssecret))
         {
            this.srv.setTrustedCredentials(new NoSecurityCredentials(sysid,systype));
         }
         else
         {
            this.srv.setTrustedCredentials(new SharedSecretCredentials(sysid,systype,syssecret));
         }
         */
      }


      /**
       * Return the encapsulated instance of the Server class.
       */
      public Server getServer()
      {
         return srv;
      }


      /**
       *
       */
      public Object invoke( Object obj) throws FndException, APException
      {
         switch(poolid)
         {
            case DEBUGGER:
               return srv.invoke(pools.config[poolid].interfname, pools.config[poolid].operation, (Record)obj, null, pools.config[DEBUGGER].loadbalgrp);
            default:
               throw new FndException("FNDORBPOOLIDERR: No such pool");
         }
      }


      /**
       * Update the timestamp and put back the object at the beginning of the linked list.
       */
      public void release()
      {
         if(DEBUG) debug(this+"ORBDebugConnectionPool$Slot.release()");

         lastused = System.currentTimeMillis();
         pools.addFirst(poolid,this);
      }


      /**
       * Disconnect from the TM server.
       */
      private void disconnect()
      {
         srv.disconnect();
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
         String out = "\n\n\n\tORB Debug Connection Pool:\n\t=======================\n"+
                      "\n\t           DEBUGGER"+
                      "\n\tTotal    : " + pools.total[DEBUGGER] +
                      "\n\tIn use   : " + pools.inuse[DEBUGGER] +
                      "\n\n\tTimstamps for connections in list:\n\t==================================\n\n";
         out = out + pools.getTimestamps();
         debug(out+"\n\n\n");
      }
   }
}
