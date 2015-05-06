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
 * File        : TiffViewerCache.java
 * Description : Cache tiff image objects
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments   :
 * 2008/08/01 rahelk Bug id 74809, created.
 * ----------------------------------------------------------------------------
 */



package ifs.fnd.image;

import java.util.*;
import java.io.*;
import javax.media.jai.PlanarImage;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;

public class TiffViewerCache
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.image.TiffViewerCache");
   
   private static TiffViewerCache cache;
   private HashMap sessionMap;
   private TiffSessionCache sessionCache;
   private long lifetime;
   
   /** Creates a new instance of TiffViewerCache */
   private TiffViewerCache()
   {
      sessionMap = new HashMap();
      lifetime =  ASPConfig.getTiffCacheTimeout() * 60000;      
   }
 
   /**
    * Initialization of the cache. Create a static instance of the cache,
    * if not already created.
    */
   public static TiffSessionCache getInstance(String sessionId) throws FndException
   {
      if(DEBUG) debug("TiffViewerCache.getInstance() session id:"+sessionId);
      
      if(cache==null)
         cache = new TiffViewerCache();
      
      return cache.getSessionCache(sessionId);
   }
   
   private TiffSessionCache getSessionCache(String sessionId)
   {
      TiffSessionCache sc = (TiffSessionCache)cache.sessionMap.get(sessionId);
      
      if (sc == null)
      {
         sc = new TiffSessionCache();
         cache.sessionMap.put(sessionId, sc);
      }
      
      return sc;
   }
   
   /**
    * Clear All tiff images stored in session cache on logout
    */
   public static void clearSessionCache(String sessionId) 
   {
      if (DEBUG) debug("TiffViewerCache.clearSessionCache: Clear Tiff Images cached for session "+sessionId);
      
      if (cache != null ) cache.sessionMap.remove(sessionId);
   }

   /**
    * Clear the whole cache. Must be re-initialized.
    */
   static void clear()
   {
      if (DEBUG) debug("TiffViewerCache.clear: Clear entire Tiff Cache.");

      cache = null;
   }
   
   //==========================================================================
   //  Debugging, error
   //==========================================================================

   private static void debug( String line )
   {
      Util.debug(line);
   }

   //==========================================================================
   //  Inner classes
   //==========================================================================
   public class TiffSessionCache
   {
      private HashMap imageMap; 
      private LinkedList timeoutlist; 

      private TiffSessionCache()
      {
         imageMap = new HashMap();
         timeoutlist = new LinkedList();
      }
      
      /**
       * Return the Planar Image Object at the specified page index.
       */
      public PlanarImage getPlanarImageAt(String filename, int index) throws FndException, IOException
      {
         if (DEBUG) debug("TiffSessionCache.getPlanarImageAt: filename="+filename+" index="+index);
         
         ArrayList imageList = get(filename);

         return (PlanarImage)imageList.get(index);
      }
   
      
      
      ArrayList get(String fileName) throws FndException, IOException
      {
         if (DEBUG) debug("TiffSessionCache.get: fileName="+fileName);
         
         Slot s = (Slot)imageMap.get(fileName);

         if (s == null)
         {
            ArrayList imagesPng = TiffViewer.readAndFormat(new FileInputStream(fileName));
            put(fileName, imagesPng);

            return imagesPng;
         }
         else
         {
            timeoutlist.remove(s);
            timeoutlist.addFirst(s);

            return s.imageList;
         }
      }
      
      /**
       * Put the generated image array list for a Tiff file at the beginning of the linked list.
       * Remove all entries from the list that are older then timeout.
       */
      void put( String filekey, ArrayList images ) throws FndException
      {
         if(DEBUG) debug("TiffSessionCache.put: Array size="+images.size()+" key="+filekey);

         Slot s = new Slot(images, filekey);

         synchronized(imageMap)
         {
            addObject(filekey, s);

            //remove timedout objects from cache 
            removeExpired();
         }
      }

      private void addObject(String key, Object o)
      {
         if(DEBUG) debug("TiffViewerCache.addObject");

         imageMap.put(key, o);
         timeoutlist.addFirst(o);
      }

      private void removeExpired()
      {
         if (DEBUG) debug("TiffSessionCache.removeExpired: lifetime="+cache.lifetime);
         
         if(cache.lifetime>0)
         {
            if (DEBUG) debug("TiffSessionCache.removeExpired: timeoutlist.size="+timeoutlist.size());
            
            boolean done = (timeoutlist.size() == 0);
            Slot s;          
            
            while(!done)
            {
               s = (Slot)timeoutlist.getLast();

               if (DEBUG) debug("TiffSessionCache.removeExpired: timeout="+(System.currentTimeMillis() - s.timestamp));
               
               if (timedOut(s))
               {
                  removeObject(s.key);
                  done = (timeoutlist.size() == 0);
               }
               else
                  done = true;
            }
         }
      }
      
      private boolean timedOut(Slot ref) 
      {
         return (System.currentTimeMillis() - ref.timestamp > cache.lifetime);
      }

      private void removeObject(String key)
      {
         if (DEBUG) debug("TiffSessionCache.removeObject: key="+key);
         
         imageMap.remove(key);
         timeoutlist.removeLast();
      }
      
      /**
       * check if Tiff file already exists in cache
       */
      boolean contains(String filename)
      {
         if(DEBUG) debug("TiffViewerCache.contains");

         return imageMap.containsKey(filename);
      }
   
   }
   
   
   /**
    * The inner class Slot, that contains the image array, time stamp, but also
    * references to the previous and next instances in the linked list.
    */
   class Slot
   {
      private ArrayList imageList;
      //private Slot   next;
      //private Slot   previous;
      private long   timestamp;
      private String key;
      
      //=======================================================================
      //  Construction
      //=======================================================================

      /**
       * Private constructor
       */
      private Slot( ArrayList images, String key )
      {
         this.imageList  = images;
         this.key = key;
         
         this.timestamp = System.currentTimeMillis();
      }
   }


}
