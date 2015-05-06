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
 * File        : SecuritySupervisor.java
 * Description : Class that controls access to database objects
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D  1998-Jul-17 - Created
 *    Jacek P  1998-Aug-04 - PL/SQL package Mts_SYS replaced with functions
 *                           within General_SYS and Security_SYS
 *    Marek D  1998-Sep-25 - Synchronize updates of static counters
 *    Marek D  1999-Feb-12 - Call Security_SYS.Is_Prefixed_View_Available_
 *                           for prefixed tables/views (item type = "T")
 *    Johan S  2000-Jan-03 - Added method clearCache()
 *    Reine A  2000-Mar-29 - New logic for type "R" in isDbObjectAvailable()
 *                           New method getSecPresSetupFlag().
 *    Reine A  2000-Apr-20 - Moved the check of the SecPresSetupFlag to
 *                           method performSecurityQuery().
 *    Reine A  2000-May-24 - Bug correction in isDbObjectAvailable().
 *    Reine A  2000-May-29 - Added functionality in isDbObjectAvailable(),
 *                           getSecPresSetupFlag() and performSecurityQuery()
 *                           to check security on fnduser.
 *    Mangala  2000-Oct-02 - Added method isDbProcedureAvailable to return a boolean
 *                           value instead of firing Exceptions like checkDbProcedure does
 *    Marek D  2002-Oct-30 - Copied from package ifs.fnd.service
 *    Marek D  2003-Feb-07 - Missing space in call to Is_Pres_Object_Available_
 *    Marek D  2003-May-14 - Abort SecurityQuery with un-trimmed object names (ToDo #4035)
 *    Marek D  2003-Jul-17 - ToDo #4407 - Removed usage of PLSQL SystemName
 *    Marek D  2003-Nov-03 - Refresh SecPresSetupFlag in isDbObjectAvailable
 *                           Added getPresObjectDescription()
 * ----------------------------------------------------------------------------
 *    Marek D  2008-Apr-21 - Bug id 69076 : Added Server functionality needed by new Aurora client
 *
 * Revision 1.1  2005/04/20 19:03:12  marese
 * Merged fndext with fndbas
 *
 * Revision 1.2  2005/04/20 06:57:09  madrse
 * Clear security cache in PlsqlGateway by sending an administrative framework message to all deployed applications.
 *
 * Revision 1.1  2005/01/28 17:36:51  marese
 * Initial checkin
 *
 * Revision 1.3  2004/10/06 21:41:46  marese
 * Coding style corrections
 *
 * Revision 1.2  2004/08/23 11:58:52  marese
 * Style corrections
 *
 * Revision 1.1  2004/08/17 15:11:21  marese
 * Initial check in
 *
 * Revision 1.3  2004/02/13 16:46:29  marese
 * Javadoc corrections
 *
 * Revision 1.2  2004/02/05 11:06:20  marese
 * Added CVS update logging
 *
 */

package ifs.fnd.services.plsqlserver.service;

import ifs.fnd.service.FndException;
import ifs.fnd.buffer.*;

import java.util.*;

/**
 * <B>Framework internal class:</B>
 * This class controls access to database objects per Fnd user. It maintenance a
 * memory cache for both successful and failed checks.
 *
 * <P><B>This is a framework internal class! Backward compatibility is not guaranteed.</B>
 */
public class SecuritySupervisor {
   public static boolean DEBUG = false;

   private static Hashtable security_cache = new Hashtable();
   
   // Added by Terry 20131125
   // Save key clear timestamp
   private static Hashtable<String, Long> security_cache_timestamp = new Hashtable<String, Long>();
   private static final int security_cache_interval  = 1;
   // Added end

   private static int yes_db_count = 0;

   private static int yes_cache_count = 0;

   private static int no_db_count = 0;

   private static int no_cache_count = 0;

   private RequestContext context;

   private boolean trace_on;

   /**
    * Creates an instance of SecuritySupervisor that will be used for checking
    * security during execution of the current request.
    */
   public SecuritySupervisor(RequestContext context) {
      this.context = context;
      if(context != null)
         this.trace_on = context.isDebugOn();
   }

   /**
    * Checks if an application object is available for the current FND user.
    *
    * @param dbObjectName
    *           the name of an application object
    * @param type
    *           one-letter type marker of the application object, one of
    *
    * <pre>
    *
    *              &quot;R&quot; - presentation object
    *              &quot;T&quot; - database table or view (with or without a schema prefix)
    *              &quot;P&quot; - method (database procedure or function)
    *
    * </pre>
    *
    * @return true if the specified object is available for the current FND
    *         user, false otherwise
    */
   
   public boolean isDbObjectAvailable(String dbObjectName, String type) throws Exception {
      if (dbObjectName == null)
         return false;
      if ("R".equals(type)) {
         String rkey = "." + context.getFndUser() + "," + "PRES_SETUP_FLAG";
         Object flag = security_cache.get(rkey);
         if (flag == null) {
            getSecPresSetupFlag();
            flag = security_cache.get(rkey);
         }

         if (!"ON".equals(flag))
            return true;
      }

      String key = "." + context.getFndUser() + "," + (type == null ? "" : type + ":") + dbObjectName;

      // Added by Terry 20131125
      // Save key clear timestamp
      if (security_cache_timestamp.containsKey(key))
      {
         long key_timestamp = (Long)security_cache_timestamp.get(key);
         long cur_time = System.currentTimeMillis();
         long key_mins   = (cur_time - key_timestamp) / 60000;
         if (key_mins > security_cache_interval)
         {
            security_cache.remove(key);
//            System.out.println("SECURITY_SUPERVISOR: Remove overdue security key: " + key);
         }
      }
      // Added end
      
      // System.out.println("SECURITY_SUPERVISOR: Before db check, security cache is: " + security_cache.toString());
      
      String flag = (String) security_cache.get(key);
      if ("Y".equals(flag)) {
         synchronized (security_cache) {
            yes_cache_count++;
         }
         if (trace_on)
            traceCheck(key, true, "CACHE", yes_cache_count);
         return true;
      }
      else if ("N".equals(flag)) {
         synchronized (security_cache) {
            no_cache_count++;
         }
         if (trace_on)
            traceCheck(key, false, "CACHE", no_cache_count);
         return false;
      }

      int pos = 0;
      String stmt = "";
      Buffer arg = context.createNewBuffer();
      arg.addItem(new Item("AVAILABLE", "S", "OUT", null));

      if ("R".equals(type)) {
         stmt = "{call " + context.getDbPrefix() + "Security_SYS.Is_Pres_Object_Available_(?,?)}";
         arg.addItem(new Item("PRES_OBJECT_ID", "S", "IN", dbObjectName));
      }
      else if ("T".equals(type) && (pos = dbObjectName.indexOf('.')) > 0) {
         stmt = "{call " + context.getDbPrefix() + "Security_SYS.Is_Prefixed_View_Available_(?,?,?)}";

         arg.addItem(new Item("SCHEMA_NAME", "S", "IN", dbObjectName.substring(0, pos)));
         arg.addItem(new Item("VIEW_NAME", "S", "IN", dbObjectName.substring(pos + 1)));
      }
      else {
         stmt = "{call " + context.getDbPrefix() + "Security_SYS.Is_Object_Available_(?,?)}";

         arg.addItem(new Item("OBJECT_NAME", "S", "IN", dbObjectName));
      }

      context.call(stmt, arg, false);

      if ("TRUE".equals(arg.getString(0))) {
         security_cache.put(key, "Y");
         // Added by Terry 20131125
         // Save key clear timestamp
         security_cache_timestamp.put(key, System.currentTimeMillis());
         // Added end
         // System.out.println("SECURITY_SUPERVISOR: Later db check TRUE, security cache is: " + security_cache.toString());
         synchronized (security_cache) {
            yes_db_count++;
         }
         if (trace_on)
            traceCheck(key, true, "DB", yes_db_count);
         return true;
      }
      else {
         security_cache.put(key, "N");
         // Added by Terry 20131125
         // Save key clear timestamp
         security_cache_timestamp.put(key, System.currentTimeMillis());
         // Added end
         // System.out.println("SECURITY_SUPERVISOR: Later db check FALSE, security cache is: " + security_cache.toString());
         synchronized (security_cache) {
            no_db_count++;
         }
         if (trace_on)
            traceCheck(key, false, "DB", no_db_count);
         return false;
      }
   }

   private void getSecPresSetupFlag() throws Exception {
      String rkey = "." + context.getFndUser() + "," + "PRES_SETUP_FLAG";

      if (security_cache.containsKey(rkey))
         return;

      String stmt = "{? = call " + context.getDbPrefix() + "Fnd_User_API.Get_Pres_Security_Setup('" + context.getFndUser() + "')}";

      Buffer arg = context.createNewBuffer();
      arg.addItem(new Item("FLAG", "S", "OUT", null));

      context.call(stmt, arg, false);

      security_cache.put(rkey, arg.getString(0));
      return;
   }

   /**
    * Retrieves from database the description for a presentation object.
    *
    * @param id
    *           presentation object id
    * @return description for the specified presentation object
    */
   public String getPresObjectDescription(String id) throws Exception {
      String stmt = "{? = call " + context.getDbPrefix() + "Pres_Object_API.Get_Description('" + id + "')}";
      Buffer arg = context.createNewBuffer();
      arg.addItem(new Item("DESC", "S", "OUT", null));
      context.call(stmt, arg, false);
      return arg.getString(0);
   }

   /**
    * Performs the security query on all application objects found in the DATA
    * item of the specified command buffer. The DATA item of the result buffer
    * will contain only those application objects that have successfully passed
    * the security check.
    *
    * @param command
    *           the input buffer with a list of objects to be checked
    * @param result
    *           the output buffer with a list of objects that have successfully
    *           passed the security check
    */
   public void performSecurityQuery(Buffer command, Buffer result) throws Exception {
      Buffer outdata = command.newInstance();
      result.addItem("DATA", outdata);

      Buffer indata = command.getBuffer("DATA", null);

      if (indata == null)
         return;

      getSecPresSetupFlag();

      BufferIterator iter = indata.iterator();
      while (iter.hasNext()) {
         Item item = iter.next();
         String name = item.getName();
         if (name != null && !name.equals(name.trim()))
            throw new FndException("FNDSECQRYTRIM: Invalid object name '&1' passed to security query", name);
         String type = item.getType();
         if (isDbObjectAvailable(name, type))
            outdata.addItem(new Item(name, null));
      }
   }

   /**
    * Checks accessibility of a database table or view.
    *
    * @param dbObjectName
    *           the name of a database table or view
    * @throws Exception
    *            if the specified object is not accessible to the current FND
    *            user
    */
   public void checkDbTable(String dbObjectName) throws Exception {
      if (!isDbObjectAvailable(dbObjectName, "T"))
         throw new FndException("FNDUSRDBOBJ: User '&1' is not allowed to access the database object '&2'", context.getFndUser(), dbObjectName);
   }

   /**
    * Checks accessibility of a database procedure or function.
    *
    * @param dbObjectName
    *           the name of a database procedure or function prefixed with a
    *           package name
    * @throws Exception
    *            if the specified method is not accessible to the current FND
    *            user
    */
   public void checkDbProcedure(String dbObjectName) throws Exception {
      if (!isDbObjectAvailable(dbObjectName, "P"))
         throw new FndException("FNDUSRDBOBJ: User '&1' is not allowed to access the database object '&2'", context.getFndUser(), dbObjectName);
   }

   /**
    * Checks accessibility of a database procedure or function.
    *
    * @param dbObjectName
    *           the name of a database procedure or function prefixed with a
    *           package name
    * @return true if the specified method is accessible to the current FND
    *         user, false otherwise
    */
   public boolean isDbProcedureAvailable(String dbObjectName) {
      try {
         return isDbObjectAvailable(dbObjectName, "P");
      }
      catch (Exception any) {
         return false;
      }
   }

   private void traceCheck(String key, boolean success, String where, int count) {
      context.debug("SECURITY check (" + key + ") " + (success ? "succeeded" : "failed") + " in " + where + " [" + count + "]");
   }

   /**
    * Clears the security cache with information about accessible application
    * objects per FND user
    */
   public void clearCache() {
      synchronized (security_cache) {
         security_cache.clear();
//         System.out.println("SECURITY_SUPERVISOR: Invoke clear...");
         yes_db_count = 0;
         yes_cache_count = 0;
         no_db_count = 0;
         no_cache_count = 0;
      }
   }

}