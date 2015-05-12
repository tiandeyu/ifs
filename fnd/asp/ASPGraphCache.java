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
 * File        : ASPGraphCache.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  2000-Aug-03 - Created.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;

import ifs.fnd.service.*;


/**
 * Class for maintenance of the temporary image files generated by Graphic Server.
 * The directory for those files is specified in ASPStaticConfig.inc file as
 * parameter STATIC/GS_CACHE/TEMP_LOCATION. This directory must not be mapped
 * by UNC notation (must be placed on a local drive), but not necessary as a part of
 * the application.
 * Temporary images files will be automatically removed after time out specified
 * by parameter STATIC/GS_CACHE/TIME_OUT (minutes).
 * All files in the directory will be removed on system start up.
 */
class ASPGraphCache
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPGraphCache");

   private static ASPGraphCache cache;

   private String dir;
   private long   timeout;
   private Slot   first;
   private Slot   last;
   private int    counter;
   private int    removed;
   

   //==========================================================================
   //  Construction of the only one static instance of ASPContextCache
   //==========================================================================

   /**
    * Private constructor. Delete all files in the specified temporary directory
    * if the timeout parameter is greater then 0.
    */
   private ASPGraphCache()
   {
      dir     = ASPConfig.getGraphTempPath();
      timeout = ASPConfig.getGraphCacheTimeout() * 60000;
      
      File tmpdir = new File(dir);
      if( timeout>0 && tmpdir.exists() && tmpdir.isDirectory() )
      {
         //clear directory on startup/creation (do not remove files if timeout = 0)
         String   path  = tmpdir.getPath();
         String[] files = tmpdir.list();

         for( int i=0; i<files.length; i++ )
         {
            String name = files[i];
            File file = new File(path,name);

            if(!file.isDirectory())
               file.delete();
         }
      }
   }

   //==========================================================================
   //  Instance methodes
   //==========================================================================

   /**
    * Create a new instance of the inner class Slot
    */
   private Slot newSlot( String file )
   {
      return new Slot(file);
   }

   //==========================================================================
   //  Static methods to be called from other classes
   //==========================================================================

   /**
    * Initialization of the cache. Create a static instance of the cache,
    * if not already created.
    */
   static void init() throws FndException
   {
      if(DEBUG) debug("ASPGraphCache.init()");
      if(cache==null)
         cache = new ASPGraphCache();
   }

   /**
    * Put the name of the new generated file at the beginning of the linked list.
    * Remove all entries from the list that are older then timeout.
    *
    * @see ifs.fnd.asp.ASPGraphCache#addFirst
    * @see ifs.fnd.asp.ASPGraphCache#removeLast
    */
   static void put( String filename ) throws FndException
   {
      if(DEBUG) debug("ASPGraphCache.put("+filename+")");
      if(cache==null) throw new FndException("FNDGRCNOINIT: The Graph Cache is not initiated!");
      
      long maxtime = System.currentTimeMillis() - cache.timeout;
      Slot s = cache.newSlot(filename);

      synchronized(cache)
      {
         addFirst(s);
         if(cache.timeout>0)
            while(cache.last.timestamp < maxtime) removeLast();
      }
   }

   /**
    * Clear the whole cache. Must be re-initialized.
    *
    * @see ifs.fnd.asp.ASPGraphCache#init
    */
   static void clear()
   {
      cache = null;
   }
   
   //==========================================================================
   //  Private static methods
   //==========================================================================

   /**
    * Add the specified instance of the inner class Slot at the beginning
    * of the linked list.
    */
   private static void addFirst( Slot s )
   {
      s.next      = cache.first;
      s.previous  = null;
      if(cache.first!=null)
         cache.first.previous = s;
      cache.first = s;
      if(cache.last==null)
        cache.last = s;

      cache.counter++;
   }

   /**
    * Remove the last instance of the inner class Slot from the linked list.
    * Remove also the corresponding file from the temporary directory.
    *
    * @see ifs.fnd.asp.ASPGraphCache#removeFile
    */
   private static Slot removeLast() throws FndException
   {
      Slot s = cache.last;
      cache.last = s.previous;
      s.previous.next = null;

      removeFile(s.filename);

      cache.counter--;
      cache.removed++;
      
      return s;
   }
   
   /**
    * Remove the specified file from the temporary directory.
    *
    * @see ifs.fnd.asp.ASPGraphCache#removeLast
    */
   private static void removeFile( String filename ) throws FndException
   {
      try
      {
         filename = cache.dir + filename;
         (new File(filename)).delete();
      }
      catch(Exception x)
      {
         throw new FndException(x);
      }
   }

   //==========================================================================
   //==========================================================================
   //  Inner class
   //==========================================================================
   //==========================================================================

   /**
    * The inner class Slot, that contains the file name, time stamp, but also
    * references to the previous and next instances in the linked list.
    */
   class Slot
   {
      private String filename;
      private Slot   next;
      private Slot   previous;
      private long   timestamp;
      
      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor
       */
      private Slot( String filename )
      {
         this.filename  = filename;
         this.timestamp = System.currentTimeMillis();
      }
   }

   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private static void debug( String line )
   {
      Util.debug(line);
   }

   /**
    * Show cache contents
    */
   static void showContents()
   {
      synchronized(cache)
      {
         String out = "\n\n\n\tGraph Cache Counters:\n\t=======================\n"+
                      "\n\tRemoved  : " + cache.removed +
                      "\n\tEntries  : " + cache.counter +
                      "\n\n\tGraph Cache Contents:\n\t=======================\n\n";

         Slot s = cache.first;
         while(s!=null)
         {
            out = out + "\n\t"+s.filename+" ["+(new Date(s.timestamp))+"]";
            s = s.next;
         }
         debug(out+"\n\n\n");
      }
   }
}