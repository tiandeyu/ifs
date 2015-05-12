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
 * File        : ASPPagePool.java
 * Description : Static pool with the instances of ASPPage.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Feb-17 - Created.
 *    Johan S  2000-Jan-10 - Added static method removeURL() and Cluster.count()
 *    Jacek P  1999-Jan-17 - Added support for dynamic LOV:s in showContents().
 *    Jacek P  2000-Mar-20 - New concept for page pool key in addAndLock().
 *    Jacek P  2000-Apr-14 - Added getPage() function.
 *    Jacek P  2000-May-19 - Convert key to lower case.
 *    Jacek P  2000-Dec-19 - Added function getPageCounter() for monitoring purpose.
 *    Daniel S 2002-Aug-28 - Added support for smartClone.
 *    Ramila H 2002-Ocr-04 - added support for Debug tool.
 *    Jacek P  2004-Jan-04 - Bug#40931. Better debugging possibilities of Page Pool.
 *                           Also new debugging parameters and possibility for disabling the pool.
 *    Jacek P  2004-Apr-10 - Added stub for the new pool cleaning concept.
 *    Rifki R  2004-Jun-23 - Implemented pool cleaner.
 *    Rifki R  2004-Jul-26 - Improvements to page pool cleaner
 *    Rifki R  2004-Sep-07 - pool cleaner code review changes
 *    Ramila H 2004-09-07  - Added new method getPoolKeyLangSuffix.
 *    Rifki R  2004-Oct-06 - Fixed bug in removeURL(), checked not null before synchronizing.
 *    Jacek P  2006-Feb-21 - Added methods for explicit cloning of pages for debugging purpose.
 * ----------------------------------------------------------------------------
 *
 * New Comments:
 * 2008/07/09 sadhlk Bug 73745, Added code to check DEBUG condition before calling debug() method.
 *             2007/06/11           buhilk
 * Bug 64496, changed getASPPageHandle() to skip unlocked dirty handlers.
 *             2006/11/21           sumelk
 * Bug 61330, Changed showContentsWithMessage().
 *             2006/08/18           gegulk 
 * Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 */

package ifs.fnd.asp;


import java.util.*;

import ifs.fnd.service.*;
import ifs.fnd.ap.*;
import ifs.fnd.util.*;


/**
 * Implements pool for instances of ASPPage. Only one, static instance of
 * the class is created. Each path is represented by an instance of class
 * ASPPagePool$Cluster which points out a linked list of page handlers and
 * a reference page that will be used for cloning. Handlers (instances of
 * ASPPageHandle) are responsible for keeping track to the caller which can
 * lock the handle.
 *
 * @see ifs.fnd.asp.ASPPageHandle
 */
public class ASPPagePool
{
   public  static boolean DEBUG     = Util.isDebugEnabled("ifs.fnd.asp.ASPPagePool");
   public  static boolean VERIFY    = false;//Util.isDebugEnabled("VerifyPooledObjects");
   //Bug 40931, start
   public  static boolean AUTOLOCK  = Util.isDebugEnabled("ifs.fnd.asp.ASPPagePool-Autolock");
   public  static boolean NO_POOL   = Util.isDebugEnabled("ifs.fnd.asp.ASPPagePool-NoPool");
   //Bug 40931, end

   private static ASPPagePool pool = new ASPPagePool();

   // member variables
   private Hashtable clusters;
   private Cluster firstcluster;
   private Cluster lastcluster;
   private int       clustercounter;
   private int       pagecounter;
   private int       hits;
   private int       faults;
   private int       cloned;

   private PoolCleaner cleaner;


   //==========================================================================
   //  Construction of the only one static instance of ASPPagePool
   //==========================================================================

   /**
    * Private constructor
    */
   private ASPPagePool()
   {
      clusters = new Hashtable();
      //doubly linked list of clusters in addtion to hashtable (for pool cleaner)
      firstcluster = null;
      lastcluster = null;
   }


   //==========================================================================
   //  Instance methodes
   //==========================================================================

   /**
    * Create a new instance of the inner class Cluster
    */
   private Cluster newCluster( ASPPage page, ASPPageHandle handle, String key )
   {
      return new Cluster(page,handle,key);
   }


   /**
    * Create and start the Page Pool Cleaner thread with the configuration parameters
    */
   private void startPoolCleaner( ASPPage page ) throws FndException
   {
      try
      {
         ASPConfig cfg = page.getASPConfig();
         cleaner = new PoolCleaner();

         int priority =Integer.parseInt(cfg.getParameter("ADMIN/PAGE_POOL/THREAD_PRIORITY","3"));
         long interval= Integer.parseInt(cfg.getParameter("ADMIN/PAGE_POOL/CLEAN_INTERVAL","30")) * 60000L;
         long template_timeout= Integer.parseInt(cfg.getParameter("ADMIN/PAGE_POOL/TEMPLATE_TIMEOUT","180")) * 60000L;
         long clone_timeout= Integer.parseInt(cfg.getParameter("ADMIN/PAGE_POOL/CLONE_TIMEOUT","30")) * 60000L;
         cleaner.setThreadPriority(priority);
         cleaner.setInterval(interval);
         cleaner.setTemplateTimeout(template_timeout);
         cleaner.setCloneTimeout(clone_timeout);
         cleaner.start();
      }
      catch(Throwable any)
      {
         throw new FndException(any);
      }
   }

   /**
    * Stop the Page Pool Cleaner thread. Called on servlet unload.
    */
   private void stopPoolCleaner()
   {
     if (cleaner!=null)
     {
       cleaner.active = false;
       cleaner.interrupt();
     }
   }

   //==========================================================================
   //  Static methods to be called from other classes
   //==========================================================================

   /**
    * Fetch a page from the pool.
    * Return handle to a free page in the pool for a given path and lock it.
    * Clone the page if all pages for the path in pool are locked.
    * Return null if there are no pages for the specified path.
    * The state of page pointed by the handle is always DEFINED.
    */
   //Bug 40931, start
   //static ASPPageHandle getASPPageHandle( ASPManager mgr, String path ) throws FndException
   static ASPPageHandle getASPPageHandle( ASPManager mgr, String path, ASPPage top_page ) throws FndException
   //Bug 40931, end
   {
      if(DEBUG)
      {
         showContents();
         debug("ASPPagePool.getASPPageHandle("+mgr+","+path+")");
      }

      Cluster c = (Cluster)(pool.clusters.get(path.toLowerCase()));

      pool.hits++;
      if ( c==null)
      {
         pool.faults++;
         return null;
      }

      if (!c.valid) //check if cluster was removed by cleaner
      {
        pool.faults++;
        return null;
      }

      try
      {
         ASPPageHandle h = c.startScan();

         while ( true )
         {
            synchronized(h)
            {
              if ( h.isValid() )
              {
                 if ( !h.isLocked() )
                 {
                   //Bug 40931, start
                   //h.lock(mgr);
                   if(h.getASPPage().isDefined())
                   {
                      h.lock(mgr,top_page);
                      //Bug 40931, end
                      if ( DEBUG ) debug("  found unlocked page handle...");
                      return h;
                   }
                   else 
                   {
                       h.invalidate();
                   }
                 }
                 h = h.getNext();
              }
              else
                h = null;  //stop searching when an invalid/removed handle is found
            }


            if ( h==null )
            {
               if ( DEBUG ) debug("  create new page handle...");
               h = c.ref_page.forceClone() ? new ASPPageHandle( (ASPPage)(c.ref_page.clone(mgr)) )
                   : new ASPPageHandle( (ASPPage)(c.ref_page.frameworkClone(mgr,true)) );
               //Bug 40931, start
               //h.lock(mgr);
               h.lock(mgr,top_page);
               //Bug 40931, end
               c.addHandle(h);
               pool.pagecounter++;
               pool.cloned++;
               return h;
            }
         }
       }
       finally
       {
          c.endScan();
       }

   }


   static String getPoolKeyLangSuffix(ASPManager mgr)
   {
      return "-" + mgr.getUserLanguage();
   }

   /**
    * Add a new page to the pool.
    * Create a new cluster (if not already exists) for given page (path).
    * Create a new handle, lock it and put it in a handle list pointed by
    * the cluster. The page must be in state DEFINED.
    */
   //Bug 40931, start
   //static ASPPageHandle addAndLock( ASPPage page ) throws FndException
   static ASPPageHandle addAndLock( ASPPage page, ASPPage top_page ) throws FndException
   //Bug 40931, end
   {
      if(DEBUG)
      {
         debug("ASPPagePool.addAndLock("+page+")");
         showContents();
      }

      if ( !page.isDefined() )
         throw new FndException("FNDPPONDEF: The page is not defined!");

      if(pool.cleaner==null)
      {
         synchronized(pool)
         {
            if(pool.cleaner==null)
               pool.startPoolCleaner(page);
         }
      }

      ASPPageHandle h = new ASPPageHandle( page );
      //Bug 40931, start
      //h.lock( page.getASPManager() );
      h.lock( page.getASPManager(), top_page );
      //Bug 40931, start
      //String path = page.getPagePath();
      String key = page.getPoolKey().toLowerCase();

      synchronized(pool)
      {
         //Cluster c = (Cluster)(pool.clusters.get( path ));
         Cluster c = (Cluster)(pool.clusters.get( key ));
         if ( c==null )
         {
            c = pool.newCluster(page, h, key);
            //pool.clusters.put(path, c);
            //Bug 40931, start
            if ( !NO_POOL )
            {
               //Bug 40931, end
               pool.clusters.put(key, c);
              //add new Cluster to the begining of linked list
               c.setPrevious(null);
               c.setNext(pool.firstcluster);
               if (pool.firstcluster!=null)
                 pool.firstcluster.setPrevious(c);
               pool.firstcluster=c;
               if (pool.lastcluster == null)
                 pool.lastcluster=pool.firstcluster;
            }
         }
         else
            c.addHandle(h);
      }

      pool.pagecounter++;
      pool.clustercounter++;

      if(DEBUG)
      {
         debug("  addAndLock(): after adding the page:");
         showContents();
      }
      return h;
   }


   /**
    * Release given page in the pool.
    * Unlock page handle in the pool. The handle can only be unlocked by the same
    * instance of ASPManager which locked it (owner).
    * The page must be in state DEFINED.
    */
   //Bug 40931, start
   //static void unlock( ASPPageHandle handle, ASPManager mgr ) throws FndException
   static void unlock( ASPPageHandle handle, ASPManager mgr, ASPPage toppage ) throws FndException
   //Bug 40931, end
   {
      if ( handle.getOwner() != mgr )
      {
         //Bug 40931, start
         // debugging start
         String errmsg = "\n"+
                         "\nASPManager(thread) ["+ Integer.toHexString(mgr.hashCode()) +"("+ Integer.toHexString(Thread.currentThread().hashCode()) +")] is trying to unlock {"+ Integer.toHexString(handle.getASPPage().hashCode()) +"}"+
                         "\npreviously locked by ["+ Integer.toHexString(handle.getOwner().hashCode()) +"("+ Integer.toHexString(handle.thread_id) +")]"+
                         "\nTop-most page {"+ (toppage==null ? "null" : Integer.toHexString(toppage.hashCode()) ) +"}"+
                         "\nInitially locked by page {"+ ( handle.top_page==null ? "null" : Integer.toHexString(handle.top_page.hashCode()) ) +"}";
         if(DEBUG) debug(errmsg);
         // debugiing end
         //Bug 40931, end
         throw new FndException("FNDPPONOWN: Not an owner!");
      }

      //Bug 40931, start
      if ( DEBUG ) debug("ASPPagePool.unlock(): resetting page {"+Integer.toHexString(handle.getASPPage().hashCode())+"}");
      //Bug 40931, end
      handle.getASPPage().reset();
      //Bug 40931, start
      if ( !AUTOLOCK )
         //Bug 40931, end
         handle.unlock();
   }

   /**
    * Clear the whole pool
    */
   static void clear()
   {
      synchronized(pool)
      {
        pool.clusters.clear();
        pool.pagecounter = pool.clustercounter = pool.hits = pool.faults = pool.cloned = 0;
        pool.firstcluster=null;
        pool.lastcluster=null;
      }
   }

   /**
    * Remove a specific URL from the pool. This function assumes that when a ASPPagePool$Cluster
    * object is removed, all the objects that it has references too will die.
    */

   static void removeURL(String id) throws FndException //used by ASPManager
   {
      id = id.toLowerCase();
      Cluster oldclust = (Cluster)pool.clusters.get(id);
      if (oldclust!=null)
      {
          synchronized(oldclust)
          {
             if (!oldclust.isFree()) //cluster is being scanned by a thread, release lock and wait
             try{
                 oldclust.wait();
             }
             catch (InterruptedException x)
             {
                 throw new FndException(x);
             }
             removeURL(oldclust);
          }
      }
   }

   //removes a cluster from hashtable and doubly linked list (maintained for PoolCleaner)
   private static void removeURL(Cluster cluster) throws FndException
   {
       //remove from hashtable
       pool.clusters.remove(cluster.pool_key);

       //remove from linked list
       if (cluster.prev==null) //first cluster in list
       {
         synchronized(pool)
         {
            pool.firstcluster=cluster.next;
            if(pool.firstcluster!=null)
              synchronized(pool.firstcluster)
              {
                pool.firstcluster.setPrevious(null);
              }
            else
               pool.lastcluster = null;
            cluster.valid=false;  //indicate that cluster has been removed
         }
       }
       else if(cluster.next==null) //last cluster in list
       {
         Cluster prev_cluster = cluster.prev;
         synchronized(pool)
         {
           synchronized(prev_cluster)
           {
              prev_cluster.setNext(null);
              pool.lastcluster=prev_cluster;
              cluster.valid=false;
           }
         }
      }
      else //cluster is in the middle of the list
      {
         Cluster prev_cluster = cluster.prev;
         Cluster next_cluster = cluster.next;
         synchronized(prev_cluster)
         {
            synchronized(next_cluster)
            {
               prev_cluster.setNext(next_cluster);
               next_cluster.setPrevious(prev_cluster);
               cluster.valid=false;
            }
         }
      }

      //set counters
      pool.clustercounter -= 1;
      try
      {
         pool.pagecounter -= cluster.count();
      }
      catch( InterruptedException e )
      {
         throw new FndException(e);
      }

      if(DEBUG) debug("Removed \""+cluster.pool_key+"\" from the page pool.");

   }

   /*
    * Called by the PoolCleaner thread to remove pool objects that have timed out.
    */
   private static void removeExpired() throws FndException
   {
     if (DEBUG) showContentsWithTimestamps("Page pool contents before PoolCleaner execution...");
     int removed_clones = 0;
     Cluster cluster = pool.firstcluster;
     while ( cluster != null )
     {
        if ((System.currentTimeMillis()-cluster.last_access) > pool.cleaner.template_timeout) //first check if template object has expired
        {
           if ( DEBUG )
           {
             java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
             String timestamp = df.format(new Date(cluster.last_access));
             debug("PoolCleaner: Removing timed out cluster - "+cluster.pool_key+"["+timestamp+"]");
           }
           synchronized(cluster)
           {
             if(cluster.isFree())
                removeURL(cluster);
           }
        }
        else //remove timed out objects from the page handle linked list
        {
            ASPPageHandle current_handle;
            boolean clones_exist=true;

            synchronized (cluster)
            {
              current_handle = cluster.last_handle;
              if (current_handle == cluster.first_handle) //check if there are cloned handles (i.e. more than one in list)
                 clones_exist=false;
            }
            while ( clones_exist && current_handle!=null )
            {
              if ((System.currentTimeMillis()-current_handle.getLastAccess()) > pool.cleaner.clone_timeout)
              {
                if (current_handle.getPrev()!=null) //avoid removing the first handle containing the ref_page
                {
                     synchronized(cluster)
                     {
                      synchronized(current_handle)
                      {
                       ASPPageHandle prev_handle = current_handle.getPrev();
                       synchronized(prev_handle)
                       {
                          prev_handle.setNext(null);
                          cluster.last_handle=prev_handle;
                          current_handle.invalidate();
                          removed_clones++;
                          if ( DEBUG )
                          {
                            java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
                            String timestamp = df.format(new Date(current_handle.getLastAccess()));
                            String page = Integer.toHexString(current_handle.getASPPage().hashCode());
                            debug("PoolCleaner: Removed timed out page handle - "+page+"["+timestamp+"] from cluster "+cluster.pool_key);
                          }
                       }
                      }
                     }
                }
              }
              else
                 break; //stop traversing handle list when the first non timed out handle is found

              current_handle = current_handle.getPrev();
            }
        }
        cluster=cluster.next;
     }
     if (removed_clones>0)
     {
         synchronized(pool){
           pool.pagecounter-=removed_clones;
         }
     }
     if (DEBUG) showContentsWithTimestamps("Page pool contents after PoolCleaner execution...");
   }


   /**
    * Stop the Page Pool Cleaner thread. Called on servlet unload.
    */
   public static void stopCleaner()
   {
      pool.stopPoolCleaner();
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
   //  Inner classes
   //==========================================================================
   //==========================================================================

   /**
    * Definition of entry point for a given path. Points out a linked list
    * of page handlers (ASPPageHandle) and a page which will be used as
    * a reference page for cloning of pages in the pool. Counts all threads
    * actually scanning the entry in the pool.
    */
   class Cluster
   {
      private ASPPage       ref_page;   // not null
      private ASPPageHandle first_handle;     // not null
      private ASPPageHandle last_handle; //not null
      private int           counter;    // Thread counter
      private Cluster prev;
      private Cluster next;
      private String pool_key;
      private long last_access;
      private boolean valid; //valid becomes false when the pool cleaner removes this cluster


      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor
       */
      private Cluster( ASPPage page, ASPPageHandle handle, String key )
      {
         this.ref_page = page;
         this.valid = true;
         this.first_handle  = handle;
         this.last_handle   = handle;
         this.pool_key=key;
         this.counter  = 0;
         this.last_access= System.currentTimeMillis();
      }


      //=======================================================================
      //  Synchronized interface
      //=======================================================================

      /**
       * Called by the thread before beginning of searching.
       * Return a handle to the first page in the list.
       */
      private synchronized ASPPageHandle startScan()
      {
         last_access= System.currentTimeMillis(); //reset timestamp when searching cluster
         counter++;
         return first_handle;
      }


      /**
       * Called by the thread after searching.
       * Notify other threads if no thread is scanning the current entry.
       */
      private synchronized void endScan()
      {
         if ( counter>0 )  counter--;
         if ( counter==0 ) this.notifyAll();
      }


      /**
       * Return true if no thread is scanning the current entry.
       */
      private synchronized boolean isFree()
      {
         return counter==0;
      }


      /**
       * Add a new (cloned?) page handle at the second position of the list.
       */
      private synchronized void addHandle( ASPPageHandle handle )
      {

        ASPPageHandle second_handle=first_handle.getNext();

        if (second_handle!=null)
        {
           synchronized(first_handle){
             synchronized(second_handle){
               synchronized(handle){
                 handle.setNext(second_handle);
                 handle.setPrev(first_handle);
                 second_handle.setPrev(handle);
                 first_handle.setNext(handle);
           }}}
        }
        else
        {
          synchronized(first_handle){
           synchronized(handle){
               handle.setPrev(first_handle);
               handle.setNext(null);
               first_handle.setNext(handle);
               last_handle=handle;
          }}
        }
        this.last_access= System.currentTimeMillis(); //reset timestamp when adding handles to cluster
      }


      /**
       * To be implemented in connection to weak references.
       * Clean up all collected references.
       */
      private synchronized void clear() throws InterruptedException
      {
         if ( counter>0 )
            this.wait();
         // do clear here
      }

      /**
       * Counts the number of pages in the cluster.
       */
      private synchronized int count() throws InterruptedException
      {
         int count = 0;
         ASPPageHandle currhandle = this.first_handle;
         while ( currhandle!=null )
         {
            currhandle = currhandle.getNext();
            count++;
         }
         return count;
      }

      /*
       * sets the next cluster after this in the linked list
       */
      private synchronized void setNext(Cluster next)
      {
          this.next = next;
      }

      /*
       * sets the previous cluster before this in the linked list
       */
      private synchronized void setPrevious(Cluster prev)
      {
          this.prev = prev;
      }

   }


   /**
    * Page Pool Cleaner - a separate thread running with low priority and
    * performing periodical cleaning of the Page Pool
    */
   class PoolCleaner extends Thread
   {
      private boolean active        = true;
      private long wait_interval;
      private long clone_timeout;
      private long template_timeout;

      /**
       * Constructor.
       */
      private PoolCleaner()
      {
         super("FNDWEB-PagePoolCleaner");
         System.out.println("Starting Page Pool cleaning thread");
      }

      //==========================================================================
      //  Member methods
      //==========================================================================

      public void setInterval(long interval)
      {
         this.wait_interval = interval;
      }

      public void setThreadPriority(int priority)
      {
         try
         {
           if(priority>MAX_PRIORITY) priority=MAX_PRIORITY;
           if(priority<MIN_PRIORITY) priority=MIN_PRIORITY;
           setPriority(priority);
         }
         catch( IllegalArgumentException x )
         {
           error("The priority value is not allowed for the Pool Cleaner thread!", x);
         }
         catch( SecurityException x )
         {
          error("The calling thread is not allowed to change priority of the Pool Cleaner thread!", x);
         }
         catch( Throwable any )
         {
          error("Error while trying to change Pool Cleaner thread priority", any);
         }
      }

      public void setCloneTimeout(long timeout)
      {
        this.clone_timeout=timeout;
      }

      public void setTemplateTimeout(long timeout)
      {
        this.template_timeout=timeout;
      }

      /**
       * Start the execution of the task.
       * The task executes as a infinite loop ...
       * ...
       */
      public void run()
      {
         if(DEBUG) debug("PoolCleaner.run(): starting the loop");
         while(active)
         {
            if(DEBUG) debug("PoolCleaner.run(): performing cleaning" );

            try
            {
               // remove all expired objects from PagePool
                pool.removeExpired();
               // if(DEBUG) debug("PoolCleaner.run(): "+pool.removed+" pool object(s) removed due to expiration");
            }
            catch( Throwable any )
            {
                error("Error when cleaning the PagePool", any);
            }

            if(DEBUG) debug("PoolCleaner.run(): sleeping "+(wait_interval/60000)+" min");
            try
            {
               sleep(wait_interval);
            }
            catch(InterruptedException e)
            {
            }
            catch( Throwable any )
            {
                error("Error while waiting", any);
            }

         }
         if(DEBUG) debug("PoolCleaner.run(): stopping the task");
         System.out.println("Stopping FNDWEB PoolCleaner thread");
      }

       private void error( String msg, Throwable x )
       {
          error(msg, null, x);
       }

       private void error( String msg, String txt, Throwable x )
       {
         debug(msg);
         if(txt!=null)
          debug("The message text\n:"+txt);
         debug("Caught error:\n"+Str.getStackTrace(x));
       }
   }


   //=======================================================================
   //  Debugging
   //=======================================================================

   /**
    * Lock all pages in the pool with the given instance of ASPManager.
    */
   static void lockAll( ASPManager mgr )
   {
      synchronized(pool.clusters)
      {
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            ASPPageHandle h = c.first_handle;
            while ( h!=null )
            {
               h.unlock();
               //Bug 40931, start
               //h.lock(mgr);
               h.lock(mgr,null);
               //Bug 40931, end
               if(DEBUG) debug("Page "+h.getASPPage()+" locked by "+mgr);
               h = h.getNext();
            }
         }
      }
   }

   /**
    * Release all pages in the pool.
    */
   static void unlockAll() throws FndException
   {
      synchronized(pool.clusters)
      {
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            ASPPageHandle h = c.first_handle;
            while ( h!=null )
            {
               h.getASPPage().reset();
               h.unlock();
               if(DEBUG) debug("Page "+h.getASPPage()+" successfully unlocked!");
               h = h.getNext();
            }
         }
      }
   }

   /**
    * Return the number of entries in the pool
    */
   public static int getPageCounter()
   {
      // return pool.pagecounter; // counters are not reset if running with pool disabled
      try
      {
         int page_count = 0;
         Iterator itr = pool.clusters.values().iterator();
         while( itr.hasNext() )
         {
            Cluster c = (Cluster)itr.next();
            page_count += c.count();
         }
         return page_count;
      }
      catch( InterruptedException x )
      {
         return -1;
      }
   }


   /**
    * Clear pool contents
    */
   static void clearContents()
   {
      synchronized(pool.clusters)
      {
         Runtime r  = Runtime.getRuntime();
         long free  = r.freeMemory();
         long total = r.totalMemory();
         String out = "\n\n\tMemory total:"+Util.lpad(""+total,10)+ " bytes\n\t        free:"+Util.lpad(""+free,10)+" bytes\n"
                      + "\t-->  Running Garbage Collector ..............\n";
         r.gc();
         free  = r.freeMemory();
         total = r.totalMemory();
         out = out + "\tMemory total:"+Util.lpad(""+total,10)+ " bytes\n\t        free:"+Util.lpad(""+free,10)+" bytes\n"
               + "\t-->  Clearing Page Pool ..............\n";
         clear();
         out = out + "\t-->  Running Garbage Collector ..............\n";
         r.gc();
         free  = r.freeMemory();
         total = r.totalMemory();
         out = out + "\tMemory total:"+Util.lpad(""+total,10)+ " bytes\n\t        free:"+Util.lpad(""+free,10)+" bytes\n\n";
         if(DEBUG) debug(out);
      }
   }


   /**
    * Show pool contents
    */
   static void showContents()
   {
      //Bug 40931, start
      showContentsWithMessage(null);
      //Bug 40931, end
   }

   /**
    * Show pool contents
    */
   //Bug 40931, start
   static void showContentsWithMessage( String message )
   {
      synchronized(pool.clusters)
      {
         String out = (message==null ? "\n\n" : "\n\n   "+message) +
                      "\n   Page Pool Counters:\n   ===================\n"+
                      "\n   Hits     : " + pool.hits           +
                      "\n   Faults   : " + pool.faults         +
                      "\n   Cloned   : " + pool.cloned         +
                      "\n   Clusters : " + pool.clustercounter +
                      "\n   Pages    : " + pool.pagecounter    +
                      "\n\n   Page Pool Contents:\n   ===================\n\n";
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            int i = path.lastIndexOf("/");
            int j = path.lastIndexOf(".");

            if (i > j)
            {
                path = path.substring(0,j+5);
                i = path.lastIndexOf("/");
            }    

            String qrypar = "";
            if ( "asp".equalsIgnoreCase(path.substring(j+1,j+4)) )
               qrypar = path.substring(j+4);

            if ( i>0 ) path = path.substring(i+1)+qrypar+"\t";

            ASPPageHandle h = c.first_handle;

            while ( h!=null )
            {
               String owner;
               if ( h.isLocked() )
                  owner = Integer.toHexString(h.getOwner().hashCode()) +
                          "("+Integer.toHexString(h.thread_id)+")";
               else
                  owner = "---";


               String page = Integer.toHexString(h.getASPPage().hashCode());
               path = path + page + "["+owner + "] ";

               h = h.getNext();
            }
            out = out + "\n   "+path;
         }
         if(DEBUG) debug(out+"\n\n\n");
      }
   }

   private static void showContentsWithTimestamps( String message )
   {
      synchronized(pool.clusters)
      {
         String out = (message==null ? "\n\n" : "\n\n   "+message) +
                      "\n   Page Pool Counters:\n   ===================\n"+
                      "\n   Hits     : " + pool.hits           +
                      "\n   Faults   : " + pool.faults         +
                      "\n   Cloned   : " + pool.cloned         +
                      "\n   Clusters : " + pool.clustercounter +
                      "\n   Pages    : " + pool.pagecounter    +
                      "\n\n   Page Pool Contents:\n   ===================\n\n";
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            int i = path.lastIndexOf("/");
            int j = path.lastIndexOf(".");

            String qrypar = "";
            if ( "asp".equalsIgnoreCase(path.substring(j+1,j+4)) )
               qrypar = path.substring(j+4);

            if ( i>0 ) path = path.substring(i+1)+qrypar+"\t";

            ASPPageHandle h = c.first_handle;

            java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
            path=path+"["+df.format(new Date(c.last_access))+"] == ";

            while ( h!=null )
            {
               String owner;
               if ( h.isLocked() )
                  owner = Integer.toHexString(h.getOwner().hashCode()) +
                          "("+Integer.toHexString(h.thread_id)+")";
               else
                  owner = "---";

               String timestamp = df.format(new Date(h.getLastAccess()));
               String page = Integer.toHexString(h.getASPPage().hashCode());
               path = path + page + "["+timestamp+"] ";
               h = h.getNext();
            }
            out = out + "\n   "+path;
         }
         if(DEBUG) debug(out+"\n\n\n");
      }

   }

   //Bug 40931, end

   /**
    * Show pool contents
    */
   static void showContents(String load_group)
   {
      synchronized(pool.clusters)
      {
         String out = "\n\n\n   Page Pool Counters:\n   ===================\n"+
                      "\n   Hits     : " + pool.hits           +
                      "\n   Faults   : " + pool.faults         +
                      "\n   Cloned   : " + pool.cloned         +
                      "\n   Clusters : " + pool.clustercounter +
                      "\n   Pages    : " + pool.pagecounter    +
                      "\n\n   Page Pool Contents:\n   ===================\n\n";
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            int i = path.lastIndexOf("/");
            int j = path.lastIndexOf(".");

            String qrypar = "";
            if ( "asp".equalsIgnoreCase(path.substring(j+1,j+4)) )
               qrypar = path.substring(j+4);

            if ( i>0 ) path = path.substring(i+1)+qrypar+"\t";

            ASPPageHandle h = c.first_handle;
            while ( h!=null )
            {
               String owner;
               if ( h.isLocked() )
                  owner = Integer.toHexString(h.getOwner().hashCode());
               else
                  owner = "---";

               String page = Integer.toHexString(h.getASPPage().hashCode());

               path = path + page + "["+owner + "] ";
               h = h.getNext();
            }
            out = out + "\n   "+path;
         }
         //debug(out+"\n\n\n");

         if ( System.getProperty("O_ORBDAEMON") != null )
         {
            ORBDebugConnectionPool.Slot orbcon = ORBDebugConnectionPool.get(ORBDebugConnectionPool.DEBUGGER, load_group);

            Record requestRec = new Record("RESULTSTREAM");
            requestRec.add("DEBUG_TYPE","PAGE_POOL");
            requestRec.add("OUTPUT",out);

            try
            {
               Record responseRec = (Record)orbcon.invoke(requestRec);
               orbcon.release();

            }
            catch ( Throwable e )
            {
               debug(out+"\n\n\n");
            }
         }
         else
            debug(out+"\n\n\n");
      }
   }


   static void showPage( String id ) throws FndException
   {
      synchronized(pool.clusters)
      {
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            ASPPageHandle h = c.first_handle;
            while ( h!=null )
            {
               ASPPage page   = h.getASPPage();
               String  pageid = Integer.toHexString(page.hashCode());
               if ( id.equalsIgnoreCase(pageid) )
               {
                  page.showContents();
                  return;
               }
               h = h.getNext();
            }
         }
         debug("Page ["+id+"] not found in pool!\n");
      }
   }

   static ASPPage getPage( String path )
   {
      if ( DEBUG ) debug("ASPPagePool.getPage("+path+")");

      Cluster c = (Cluster)(pool.clusters.get(path));
      return c==null ? null : c.ref_page;
   }

   public static void showAvailablePages()
   {
      synchronized(pool.clusters)
      {
         String out = "\n\n" +
                      "\n   Page Pool Counters:\n   ===================\n"+
                      "\n   Hits     : " + pool.hits           +
                      "\n   Faults   : " + pool.faults         +
                      "\n   Cloned   : " + pool.cloned         +
                      "\n   Clusters : " + pool.clustercounter +
                      "\n   Pages    : " + pool.pagecounter    +
                      "\n\n   Pages in the Pool :\n   ===================\n\n";
         Enumeration path_list = pool.clusters.keys();
         while ( path_list.hasMoreElements() )
         {
            String path = (String)path_list.nextElement();
            Cluster c = (Cluster)pool.clusters.get(path);
            out += "\n   "+path;
         }
         debug(out+"\n\n\n");
      }
   }

   public static String clonePage( String path, int count, ASPManager mgr )
   {
      try
      {
         Cluster c = (Cluster)pool.clusters.get(path);
         if( c==null )
            return "Can not find cluster for page path '"+path+"'";

         if( !c.valid )
            return "The cluster is invalid - probably already removed by the cleaner.";

         for( int i=0; i<count; i++ )
         {
            try
            {
               ASPPageHandle h = c.startScan();

               while(true)
               {
                  synchronized(h)
                  {
                    if( h.isValid() )
                       h = h.getNext();
                    else
                      h = null;  //stop searching when an invalid/removed handle is found
                  }

                  if( h==null )
                  {
                     if ( DEBUG ) debug("  create new page handle...");
                     h = c.ref_page.forceClone() ? new ASPPageHandle( (ASPPage)(c.ref_page.clone(mgr)) )
                         : new ASPPageHandle( (ASPPage)(c.ref_page.frameworkClone(mgr,true)) );
                     c.addHandle(h);
                     pool.pagecounter++;
                     pool.cloned++;
                     break;
                  }
               }
            }
            finally
            {
               c.endScan();
            }
         }
         return "Page successfully cloned.";
      }
      catch( FndException x )
      {
         return "Error while cloning page: "+x;
      }
   }
}
