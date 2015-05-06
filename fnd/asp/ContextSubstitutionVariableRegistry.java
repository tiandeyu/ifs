/*
 *
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
 * File        : ContextSubstitutionVariableRegistry.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2010/08/13 sumelk Bug 92426, Changed init() to increase the buffer size of the query.
 * 2006/09/24           Mangala  Improved CSV code 
 * 2006/09/21           Buddika  Removed all performConfig() method calls
 * 2006/09/14           Buddika  Improved by adding support Methods
 * 2006/09/13           Mangala  Created
 * 
 */

package ifs.fnd.asp;

import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import java.util.HashSet;

public class ContextSubstitutionVariableRegistry {
    
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ContextSubstitutionVariableRegistry");
   private static boolean initialized;
    
   private static HashSet client_dynamic;
   private static HashSet client_static;
   private static HashSet server_dynamic;
   private static HashSet server_static;
   
   /**
    * Initialise CSV Registry
    * @param mgr ASPManager
    */
   synchronized static void init(ASPManager mgr)
   {
      if(DEBUG) debug("CSV Registry initialized = " + initialized);
      if (initialized) return;
      if(DEBUG) debug("CSV Registry Initialising.....");
       
      //do a query for the table...
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       trans.addQuery("GET_CSVS","SELECT NAME, IMPLEMENTATION_TYPE_DB, TRANSIENT_DB FROM CONTEXT_SUBSTITUTION_VAR").setBufferSize(10000);
       trans = mgr.perform(trans);
       ASPBuffer buf = trans.getBuffer("GET_CSVS");

       // fill all four hash sets
       client_dynamic = new HashSet();
       client_static = new HashSet();
       server_dynamic = new HashSet();
       server_static = new HashSet();

       for(int i=0; i<buf.countItems(); i++)
       {
          ASPBuffer tempbuf = buf.getBufferAt(i);
          String name = tempbuf.getValue("NAME");
          boolean impl_by_server = ("SERVER".equals(tempbuf.getValue("IMPLEMENTATION_TYPE_DB")))? true: false;
          boolean static_type = ("FALSE".equals(tempbuf.getValue("TRANSIENT_DB")))? true: false;
           
          if(!Str.isEmpty(name))
             if(impl_by_server)
             {
                if(static_type)
                {
                   if(DEBUG) debug("Add Server Static: " + name); 
                   server_static.add(name);
                }
                else
                {
                    if(DEBUG) debug("Add Server dynamic: " + name); 
                    server_dynamic.add(name);
                }
             }
             else
             {
                if(static_type)
                {
                   if(DEBUG) debug("Add Client Static: " + name); 
                   client_static.add(name);
                }
                else
                {
                   if(DEBUG) debug("Add Client Dynamic: " + name); 
                   client_dynamic.add(name);
                }
             }
       }
        
       initialized = true;
   }
    
   /**
    * Reset CSV Registry
    * @param mgr ASPManager
    */
   synchronized static void reset()
   {
       initialized = false;
   }

   /**
    * Return whether the CSV Registry is initialised
    * @return boolean
    */
   static boolean isInitialized()
   {
       return initialized;
   }

   //===========================================================================================================
   //                                       Methods to check CSV availability    
   //===========================================================================================================

   /**
    * Checks client implemented static CSV availability
    */
   static boolean isClientCSV (String name)
   {
      return (client_static.contains(name) || client_dynamic.contains(name));
   }
    
   /**
    * Checks server implemented static CSV availability
    */
   static boolean isServerCSV (String name)
   {
      return (server_static.contains(name) || server_dynamic.contains(name));
   }

   /**
    * Checks cient implemented dynamic CSV availability
    */
   static boolean isDynamicCSV (String name)
   {
      return (server_dynamic.contains(name) || client_dynamic.contains(name));
   }

   /**
    * Checks server implemented dynamic CSV availability
    */
   static boolean isStaticCSV (String name)
   {
      return (server_static.contains(name) || client_static.contains(name));
   }
    
   //===========================================================================================================
   //                                       Methods to get CSV's as HashSets    
   //===========================================================================================================

   /**
    * Returns the client implemented static CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getClientStaticCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getClientStaticCSVSet()");
      return client_static;
   }

   /**
    * Returns the server implemented static CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getServerStaticCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getServerStaticCSVSet()");
      return server_static;
   }

   /**
    * Returns the client implemented dynamic CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getClientDynaicCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getClientDynaicCSVSet()");
      return client_dynamic;
   }

   /**
    * Returns the server implemented dynamic CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getServerDynamicCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getServerDynamicCSVSet()");
      return server_dynamic;
   }
    
   /**
    * Returns the static CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getStaticCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getStaticCSVSet()");
      HashSet set = new HashSet();
      set.addAll(client_static);
      set.addAll(server_static);
      return set;
   }

   /**
    * Returns the dynamic CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getDynamicCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getDynamicCSVSet()");
      HashSet set = new HashSet();
      set.addAll(client_dynamic);
      set.addAll(server_dynamic);
      return set;
   }

   /**
    * Returns the client implemented CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getClientCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getClientCSVSet()");
      HashSet set = new HashSet();
      set.addAll(client_static);
      set.addAll(client_dynamic);
      return set;
   }

   /**
    * Returns the server implemented CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getServerCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getServerCSVSet()");
      HashSet set = new HashSet();
      set.addAll(server_static);
      set.addAll(server_dynamic);
      return set;
   }

   /**
    * Returns all CSV's as a HashSet
    * @return HashSet
    */
   static HashSet getCSVSet()
   {
      if(DEBUG) debug("CSV Registry.getCSVSet()");
      HashSet set = new HashSet();
      set.addAll(getServerCSVSet());
      set.addAll(getClientCSVSet());
      return set;
   }
    
   //===========================================================================================================
   //                                     Private Utility Methods
   //===========================================================================================================

   /**
    * Prints debug string
    * @param text String to be printed
    */
   private static void debug(String text )
   {
      Util.debug(text);
   }

}
