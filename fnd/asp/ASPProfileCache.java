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
 * File        : ASPProfileCache.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-May-05 - Created.
 *    Jacek P  1999-Jun-07 - Creating a new, empty ASPProfile without putting it
 *                           into the cache in function get() if user is not logged on.
 *    Jacek P  1999-Jul-13 - Added call to ASPProfile.clearSecurity() from clear()
 *    Jacek P  2000-Apr-14 - Added showProfile() function.
 *    Jacek P  2000-Apr-27 - Removed case sensitivity for URL in function get().
 *    Jacek P  2000-Aug-02 - Removed case sensitivity for user name in function get().
 *    Artur K  2000-Nov-23 - Necessary changes for ASPPageProfile(global profile)
 *    Piotr Z                handling.
 *    Jacek P  2000-Dec-14 - Constant NO_URL refered from ASPProfile.
 *                           Character ':' replaced with '.' in key.
 *                           Not longer possible to work with disk.
 *    Jacek P  2000-Dec-19 - Added function getCounter() for monitoring purpose.
 *    Jacek P  2001-Jan-24 - Corrected bug #587 - global profile is shared among sites.
 *    Ramila H 2002-Oct-04 - added support for Debug tool.
 *    Ramila H 2002-Dec-13 - Log id 933. Changed the url key to get the page pool key to
 *                           support block profiles for dynamic LOVs
 *    ChandanaD2003-Jan-08 - Overloaded get() method.
 *    ChandanaD2003-Jan-10 - Changed get(page,url) method.
 *    ChandanaD2003-Jan-14 - Corrected a bug in the get( ASPPage page, String url) method.
 *    Ramila H 2004-10-22  - Used method getProfilePoolKey to get profile key without lang suffix
 *    Jacek P  2004-Nov-11 - Extensive changes due to new profile handling.
 *    Jacek P  2006-Feb-21 - Added method for computation of the cache size.
 * ----------------------------------------------------------------------------
 * New Comments:
 *             2006/08/18           gegulk 
 * Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 *             2006/05/11           riralk
 * Bug 57024, Added method clearUser() which will be called when an exception occurs in ASPProfile.save()
 *
 * Revision 1.4  2006/02/20           prralk
 * Removed calls to deprecated functions
 *
 * Revision 1.3  2005/10/19 10:16:32  mapelk
 * Added functionality to refresh profile cache in certain time. Also posibility to flush the cache on demand.
 *
 * Revision 1.2  2005/09/28 12:08:40  japase
 * Throw exception if user ID is empty
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.11  2005/08/16 05:57:28  riralk
 * Removed "secured" from profile key and eventually from profile structure. Used ASPConfig.getApplicationContext() to get the site name when constructing the profile key.
 *
 * Revision 1.10  2005/08/11 09:55:28  riralk
 * Modified get(), used getApplicationPath() when creating profile key for fetching global profile.
 *
 * Revision 1.9  2005/08/08 10:03:20  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.8  2005/07/27 12:27:49  riralk
 * Added get() method to get profiles of pages other than the current one. Used by ASPField.getLovQueries()
 *
 * Revision 1.7  2005/07/07 08:10:39  rahelk
 * CSL: Corrected profile_page locking problem when getting profile elements from CSL page
 *
 * Revision 1.6  2005/05/17 10:44:13  rahelk
 * CSL: Merged ASPTable and ASPBlockLayout profiles and added CommandBarProfile
 *
 * Revision 1.5  2005/05/04 05:32:00  rahelk
 * Layout profile support for groups
 *
 * Revision 1.4  2005/04/08 06:05:37  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.3  2005/02/24 13:48:59  riralk
 * Adapted Portal profiles to new profile algorithm. Removed some obsolete code.
 *
 * Revision 1.2  2005/02/02 08:22:19  riralk
 * Adapted global profile functionality to new profile changes
 *
 * Revision 1.1  2005/01/28 18:07:26  marese
 * Initial checkin
 *
 * Revision 1.2  2004/12/20 08:45:07  japase
 * Changes due to the new profile handling
 *
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.ap.*;

/**
 *
 */
class ASPProfileCache implements Serializable
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPProfileCache");
   static int     MAX_SIZE = ASPConfig.getProfileCacheMaxSize();
   static long    TIME_OUT = ASPConfig.getProfileCacheTimeout()*60000;

   private static final String  EXT = ".tmp";

   private static ASPProfileCache cache = new ASPProfileCache();

   private Hashtable profiles;
   private Slot      first;
   private Slot      last;
   private int       counter;
   private int       hits;
   private int       fromfile;
   private int       tofile;
   private int       faults;
   private int       removed;



   //==========================================================================
   //  Construction of the only one static instance of ASPProfileCache
   //==========================================================================

   /**
    * Private constructor
    */
   private ASPProfileCache()
   {
      profiles = new Hashtable();
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================

   /**
    * Create a new instance of the inner class Slot
    */
   private Slot newSlot( String key, ASPProfile profile )
   {
      return new Slot(key, profile);
   }

   //==========================================================================
   //  Static methods to be called from other classes
   //==========================================================================

   /**
    * Return the cached profile for given userid and URL.
    */
   static ASPProfile get( ASPPage page, boolean global_url ) throws FndException
   {
      // TODO: Done!
      // 'useurl' should be replaced by 'global_url' and the NO_URL constant by the new GLOBAL_PRF
      // changes required to both this class and ASPProfile.

      if (DEBUG) debug("ASPProfileCache.get("+page+")");

      long timeout = System.currentTimeMillis() - TIME_OUT;

      if(page==null) throw new FndException("FNDPRCPAGN: Required page reference is missing.");

      cache.hits++;
      String userid = page.getASPManager().getUserId();
      if(Str.isEmpty(userid))
         throw new FndException("FNDPRCEMPTYUSER: User ID is empty when trying to retrieve user profile for page '&1'.", page.getPagePath());
      String url;

      //----------------------------------------
      // ???
      // set proper: key,
      //
      // key   = userid and URL in lower case
      // value = an instance of inner class Slot

      if(global_url)
      {
        //String app_path = page.getPoolKey().substring(1);
        //app_path = app_path.substring(0,app_path.indexOf('/'));
        //String app_path = page.getASPManager().getApplicationPath().substring(1);
        //String app_path = page.getASPConfig().getApplicationContext().substring(1);
         
        String app_path = page.getASPManager().getAspRequest().getContextPath().substring(1);
        url = app_path + "/" + ASPProfile.GLOBAL_PRF;
      }
      else
        url = page.getProfilePoolKey(); // page.getPoolKey();

      String key = userid.toLowerCase()+"-"+url.toLowerCase();
      key = Str.replace(Str.replace(Str.replace(key,"/","."),"\\","."),":",".");

      if(DEBUG) debug("\tuserid="+userid+"\n\turl="+url+"\n\tkey="+key);

      Slot s = (Slot)cache.profiles.get(key);
      if( s!=null )
      {
         if(DEBUG) debug("  Profile ["+s.profile+"] for user '"+userid+"' and URL=["+url+"] found in cache.");
         if (s.timestamp<timeout)
         {
            synchronized(cache)
            {
               if(DEBUG) debug("  Found profile item is expired on " + (new Date(s.timestamp)).toString());
               while( cache.last!=null && cache.last.timestamp < timeout)
               {
                  if(DEBUG) debug("  Clearing old profile items " + (new Date(cache.last.timestamp)).toString());
                  removeLast();
               }
            }
         }
         else
            return s.profile;
      }

      /* JAAS changes
      if( !page.getASPContext().isUserLoggedOn() && !(page.getASPManager().onSaveProfileProperties()))
      {

         ASPProfile p = new ASPProfile(page,url,false);

         if(DEBUG) debug("  User '"+userid+"' not logged on - creating empty profile ["+p+"] for URL=["+url+"].");
         return p;
      }
       */

      cache.faults++;
      if(DEBUG) debug("  Profile for user '"+userid+"' and URL=["+url+"] not found - creating new one.");

      return put(key, new ASPProfile(page,url,true) );

   }

   /*
    * Similar to get() above but uses the url passed to the method instead of building it
    * using page.getProfileKey(). Used to get profiles of pages other than the current one.
    * (ex: fetching an LOV page's saved queries from profile before it is launched)
    * Called from ASPField.getLovQueries()
    */
   static ASPProfile get( ASPPage page, String url ) throws FndException
   {

      if(DEBUG) debug("ASPProfileCache.get("+page+")");
      long timeout = System.currentTimeMillis() - TIME_OUT;

      if(page==null) throw new FndException("FNDPRCPAGN: Required page reference is missing.");

      cache.hits++;
      String userid = page.getASPManager().getUserId();

     //url = page.getProfilePoolKey(); // page.getPoolKey();  use the provided url instead

      String key = userid.toLowerCase()+"-"+url.toLowerCase();
      key = Str.replace(Str.replace(Str.replace(key,"/","."),"\\","."),":",".");

      if(DEBUG) debug("\tuserid="+userid+"\n\turl="+url+"\n\tkey="+key);

      Slot s = (Slot)cache.profiles.get(key);
      if( s!=null )
      {
         if(DEBUG) debug("  Profile ["+s.profile+"] for user '"+userid+"' and URL=["+url+"] found in cache.");
         if (s.timestamp<timeout)
         {
            if(DEBUG) debug("  Found profile item is expired on " + (new Date(s.timestamp)).toString());
            synchronized(cache)
            {
               while( cache.last!=null && cache.last.timestamp < timeout)
               {
                  if(DEBUG) debug("  Clearing old profile items " + (new Date(cache.last.timestamp)).toString());
                  removeLast();
               }
            }
         }
         else
            return s.profile;
      }

      if( !page.getASPContext().isUserLoggedOnFND() )
      {

         ASPProfile p = new ASPProfile(page,url,false);

         if(DEBUG) debug("  User '"+userid+"' not logged on - creating empty profile ["+p+"] for URL=["+url+"].");
         return p;
      }

      cache.faults++;
      if(DEBUG) debug("  Profile for user '"+userid+"' and URL=["+url+"] not found - creating new one.");

      return put(key, new ASPProfile(page,url,true) );
   }

   //private static ASPProfile put( String key, ASPProfile profile, ASPPage page ) throws FndException
   private static ASPProfile put( String key, ASPProfile profile )
   {
      if(DEBUG) debug("ASPProfileCache.put("+key+","+profile+")");

      if(MAX_SIZE < 1) return profile;

      long timeout = System.currentTimeMillis() - TIME_OUT;
      Slot s       = cache.newSlot(key,profile);

      if(DEBUG)
         debug("\tslot="+s+
               "\n\tcache="+cache+
               "\n\tcache.profiles"+(cache==null?"<null>":""+cache.profiles) );

      synchronized(cache)
      {
         addFirst(s);
         while( cache.last.timestamp < timeout || cache.counter > MAX_SIZE )
            removeLast();
      }
      return profile;
   }

   private static void addFirst( Slot s )
   {
      cache.profiles.put(s.key,s);

      s.next      = cache.first;
      s.previous  = null;
      if(cache.first!=null)
         cache.first.previous = s;
      cache.first = s;
      if(cache.last==null)
        cache.last = s;

      cache.counter++;
   }

   private static Slot removeLast()
   {
      Slot s = cache.last;
      if(DEBUG) debug("Removing  " + s.key);
      if (cache.first == s)
      {
         cache.last = null;
         cache.first = null;
      }
      else
      {
         cache.last = s.previous;
         s.previous.next = null;
      }

      cache.profiles.remove(s.key);
      cache.counter--;
      cache.removed++;
      if (DEBUG) debug("Removed  " + s.key);

      return s;
   }

   /**
    * Clear the user specific entries in the cache
    */
   static void clearUser(String user_id)
   {
       if(DEBUG) debug("ASPProfileCache.clearUser("+user_id.toLowerCase()+")");
       
       String prefix=user_id.toLowerCase()+"-";
       
       synchronized(cache)
       {
         Enumeration keys = cache.profiles.keys();
         while( keys.hasMoreElements() )
         {
            String key = (String)keys.nextElement();
            if (key.startsWith(prefix))
            {
              Slot s = (Slot)cache.profiles.remove(key);
              if (cache.first==s)
                  cache.first=s.next;
              if (cache.last==s)
                  cache.last=s.previous;
              if (s.previous!=null)
                  s.previous.next=s.next;
              if (s.next!=null)
                  s.next.previous=s.previous;
              cache.counter--;
              cache.removed++;
              if (DEBUG) debug("Removed  " + s.key);              
            }         
         }
       }
   }
   
   /**
    * Clear the whole cache
    */
   static void clear()
   {
      cache.profiles.clear();
      cache.first = null;
      cache.last  = null;
      cache.counter = cache.removed = cache.hits = cache.fromfile = cache.tofile = cache.faults = 0;
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
   //  Inner class
   //==========================================================================
   //==========================================================================

   class Slot implements Serializable
   {
      private String     key;
      private Slot       next;
      private Slot       previous;
      private long       timestamp;
      private ASPProfile profile;

      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor
       */
      private Slot( String key, ASPProfile profile )
      {
         this.key       = key;
         this.profile   = profile;
         this.timestamp = System.currentTimeMillis();
      }
   }

   //=======================================================================
   //  Debugging
   //=======================================================================

   /**
    * Return the number of entries in the cache
    */
   public static int getCounter()
   {
      return cache.counter;
   }

   /**
    * Compute size of the entire cache by serializing it.
    */
   static long getTotalSize()
   {
      try
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         ObjectOutputStream    out  = new ObjectOutputStream(bout);

         out.writeObject(cache.profiles);
         out.close();

         byte[] bytes = bout.toByteArray();

         return bytes.length;
      }
      catch( IOException x )
      {
         String msg = "Exception while debugging profile cache:\n"+Str.getStackTrace(x);
         debug(msg);
         Alert.add(msg);
         return -1;
      }
   }

   /**
    * Show cache contents
    */
   static void showContents()
   {
      synchronized(cache.profiles)
      {
         String out = "\n\n\n   Profile Cache Statistics:\n   =========================\n"+
                      "\n   Hits     : " + cache.hits    +
                      "\n   From file: " + cache.fromfile+
                      "\n   To file  : " + cache.tofile+
                      "\n   Faults   : " + cache.faults  +
                      "\n   Removed  : " + cache.removed +
                      "\n   Entries  : " + cache.counter +
                      "\n   Max size : " + MAX_SIZE      +
                      "\n\n   Profile Cache Contents:\n   =======================\n\n";
         Enumeration key_list = cache.profiles.keys();
         while( key_list.hasMoreElements() )
         {
            String key = (String)key_list.nextElement();
            Slot s = (Slot)cache.profiles.get(key);

            out = out + "\n   "+key + " ["+ Integer.toHexString(s.profile.hashCode()) +"]" + new Date(s.timestamp).toString();
         }
         debug(out+"\n\n\n");
      }
   }


   /**
    * Show cache contents
    */
   static void showContents(String load_group)
   {
      synchronized(cache.profiles)
      {
         String out = "\n\n\n   Profile Cache Statistics:\n   =========================\n"+
                      "\n   Hits     : " + cache.hits    +
                      "\n   From file: " + cache.fromfile+
                      "\n   To file  : " + cache.tofile+
                      "\n   Faults   : " + cache.faults  +
                      "\n   Removed  : " + cache.removed +
                      "\n   Entries  : " + cache.counter +
                      "\n   Max size : " + MAX_SIZE      +
                      "\n\n   Profile Cache Contents:\n   =======================\n\n";
         Enumeration key_list = cache.profiles.keys();
         while( key_list.hasMoreElements() )
         {
            String key = (String)key_list.nextElement();
            Slot s = (Slot)cache.profiles.get(key);

            out = out + "\n   "+key + " ["+ Integer.toHexString(s.profile.hashCode()) +"]";
         }
         //debug(out+"\n\n\n");

         if (System.getProperty("O_ORBDAEMON") != null)
         {
            ORBDebugConnectionPool.Slot orbcon = ORBDebugConnectionPool.get(ORBDebugConnectionPool.DEBUGGER,load_group);

            Record requestRec = new Record("RESULTSTREAM");
            requestRec.add("DEBUG_TYPE","PROFILE_CACHE");
            requestRec.add("OUTPUT",out);

            try {

                Record responseRec = (Record)orbcon.invoke(requestRec);
                orbcon.release();


            } catch (Throwable e) {
               debug(out+"\n\n\n");
            }

         }
         else
            debug(out+"\n\n\n");
      }
   }


   static void showProfile( String id ) throws FndException
   {
      if(id.indexOf('.')<0)
      {
         synchronized(cache.profiles)
         {
            Enumeration key_list = cache.profiles.keys();
            while( key_list.hasMoreElements() )
            {
               String key = (String)key_list.nextElement();
               Slot   s   = (Slot)cache.profiles.get(key);
               String pid = Integer.toHexString(s.profile.hashCode());
               if(pid.equalsIgnoreCase(id))
               {
                  if(DEBUG) debug("  Profile ["+key+"]("+id+") found in cache.");
                  s.profile.showContents();
                  return;
               }
            }
            debug("Profile ["+id+"] not found in cache!\n");
         }
      }
      else
      {
         Slot s = (Slot)cache.profiles.get(id);
         if( s!=null )
         {
            if(DEBUG) debug("  Profile ["+id+"] found in cache.");
            s.profile.showContents();
         }
         debug("Profile ["+id+"] not found in cache!\n");
      }
   }
}