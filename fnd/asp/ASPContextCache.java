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
 * File        : ASPContextCache.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Jacek P  1999-Feb-17 - Created.
 *    Jacek P  1999-Apr-30 - Added possibility for moving contexts to files.
 *                           Timeout of cacheted contexts.
 *    Jacek P  1999-May-18 - Added optional removing of old context in get().
 *    Johan S  2000-Sep-07 - Changed get() to fit the new context handling.
 *    Jacek P  2000-Dec-19 - Added function getCounter() for monitoring purpose.
 *    Johan S  2001-Jan-26 - revisited the Context Disk functionality
 *    Johan S  2001-Mar-05 - Context disk functionality revisited for 3.5
 *    Daniel s 2002-Aug-08 - Rewritten, now with flow control and stored in session object.
 *    Rifki R  2003-Oct-12 - Changed put() to enable pages to use the Lov slot of the context cache.   
 *    Rifki R  2004-Feb-16 - Bug 40903, improvements to context cache. Algorithm rewritten with 
 *                           linear context caches per browser window. Each cache is stored as 
 *                           a seperate session object indetified by "CONTEXT_ID".
 *    Prabha R 2006-Apr-24 - Bug 45230 fix for errors caused by page refresh. 
 *                           If page is set to expire, remove context from cache.
 *    Rifki R  2006-Aug-28 - Bug 59776, Better solution for storing context cache. Moved old 
 *                           contexts out of session memory.                 
 * ----------------------------------------------------------------------------
 * New Comments:
 * 26-06-2008 mapelk Bug id 74852- Programming Model for Activities. 
 * 19-12-2006 rahelk Bug id 62533- Added debug information
 * 17-10-2007 sadhlk Bug id 67865- Modified fetchUserCacheMethod()
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;


import java.util.*;
import java.io.*;

import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.record.FndAbstractArray;

import javax.servlet.http.*;
/**
 *
 */
class ASPContextCache implements java.io.Serializable
{
   public static boolean DEBUG    = Util.isDebugEnabled("ifs.fnd.asp.ASPContextCache");
   //Bug 40903, start
   LinkedList context_handles;   
   int  max_size;      
   
   /* 
    * 'top_cache' is a common cache for all users which will be kept *out of* session memory
    * it contains "session_id" as key and instances of FndSoftHashMap as value, the HashMap instances 
    * will in turn contain "context_id" (id per browser window) as key and instances of ASPContextCache as value.    
    */
   private static Hashtable top_cache = new Hashtable();   
   
   //some contstants for session attribute names
   private static final String SESS_ATTR_CONTEXT_HANDLE = "ContextHandle_";
   private static final String SESS_ATTR_CONTEXT_CACHE  = "ContextCache_";  //only used for storing * cache in session
   
   /**
    * Constructor
    **/
   ASPContextCache(int max_size) 
   {
      this.context_handles  = new LinkedList();            
      this.max_size      = max_size;
   }
   
   
   //==========================================================================
   //  Static methods to be called from other classes
   //==========================================================================
   
   public static ContextObject get( String context_id, String key, ASPManager mgr) throws FndException       
   {    
      if (DEBUG) debug("ASPContextCache.get():  context_id="+context_id+" page_id="+key+"\n"); 
       
      if (Str.isEmpty(key)) return null;                   
      
      HttpSession session = mgr.getAspSession();
      //1. Try the "ContextHandle_<context_id>" attribute in session
      ContextHandle handle = (ContextHandle)session.getAttribute(SESS_ATTR_CONTEXT_HANDLE + context_id);      
      if (handle!=null && key.equals(handle.key))
      {         
         if (DEBUG) debug("ASPContextCache.get(): Found context in user's session");                  
         boolean[] user_method=fetchUserCacheMethod(handle.context.getContextBuffer());        
         if( user_method[1] && !user_method[0])
            session.removeAttribute(SESS_ATTR_CONTEXT_HANDLE + context_id); //Bug 45230 fix merged with latest changes
         return handle.context;
      }      
      else
      {          
         ASPContextCache anonymous_cache = (ASPContextCache)session.getAttribute(SESS_ATTR_CONTEXT_CACHE+"*"); 
         //2. Try the anonymous "ContextCache_*" attribute in session
         if (anonymous_cache!=null)
         {             
             ContextHandle ctxH = anonymous_cache.remove(key);
             if (ctxH != null)
             {               
               if (DEBUG) debug("ASPContextCache.get(): Found handle in session * context cache.");  
               //transfer the context handle from session memory to ASPContextCache object *outside* session memory
               ASPContextCache cache = null;
               boolean update_top_cache=false;
               boolean update_user_context_caches=false; 
               
               Map user_context_caches = (Map)top_cache.get(mgr.getSessionId()); //find the Map containing context caches for this user.
               if (user_context_caches == null)
               {
                  user_context_caches = new FndSoftHashMap(1);
                  update_top_cache=true;
               }
               else
                  cache = (ASPContextCache)user_context_caches.get(context_id); //get the ASPContextCache object for the particular window (context_id)
               
               if (cache==null)               
               {
                 cache = new ASPContextCache(Integer.parseInt(mgr.getASPConfig().getParameter("ADMIN/CONTEXT/CACHE/MAX_FLOWS","5")));
                 update_user_context_caches=true;
               }                                 
               
               if (DEBUG) debug("ASPContextCache.get(): Moving previous context out of session memory, session_id="+mgr.getSessionId()+" context_id="+context_id+" page_id="+key);  
               cache.put(ctxH.key,ctxH.context);               
               if (update_user_context_caches)
                 user_context_caches.put(context_id,cache);                                                           
               if (update_top_cache)
                 top_cache.put(mgr.getSessionId(),user_context_caches);                              
               return ctxH.context;
             }
         }
         //3. Finally try the ASPContextCaches kept *outside* of session memory
         ASPContextCache cache = null;
         Map user_context_caches = (Map)top_cache.get(mgr.getSessionId());
         if (user_context_caches != null)
            cache = (ASPContextCache)user_context_caches.get(context_id);         
         if (cache!=null)
         {
           ContextHandle ctxH = cache.get(key);         
           if (ctxH != null)           
              return ctxH.context;           
         }
                  
         if (DEBUG) debug("ASPContextCache.get(): context not found!!");
         return null;
      }                
     
   }      
      
     
   /* 1. Check if the session already contains a context for the given id
    * 2. If True, move this (old) context into the ASPContextCache object (outside session) for this user.
    * 3. Store the current context in the user's session memory
    */
    public static void put( String context_id, String key, 
                           ContextObject context, 
                           ASPManager mgr ) throws FndException    
   {
      
      if (key==null)
         throw new FndException("FNDCTXCHIPID: Illegal page id, can't cache context.");
      HttpSession session = mgr.getAspSession();
      
      if (DEBUG) debug("ASPContextCache.put() with context_id="+context_id+"page_id="+key+"\n");      
            
      if ("*".equals(context_id))
      {
        ASPContextCache cache = (ASPContextCache)session.getAttribute(SESS_ATTR_CONTEXT_CACHE+context_id);
      
        if (cache==null)                            
          cache = new ASPContextCache(Integer.parseInt(mgr.getASPConfig().getParameter("ADMIN/CONTEXT/CACHE/TEMP_FLOWS","20")));        

        cache.put( key, context );  
        if (DEBUG) debug("ASPContextCache.put(): Storing handle in * cache in session for context_id="+context_id+" page_id="+key);
        session.setAttribute(SESS_ATTR_CONTEXT_CACHE+context_id,cache);
      }
      else
      {              
        ContextHandle oldhandle = (ContextHandle)session.getAttribute(SESS_ATTR_CONTEXT_HANDLE+context_id);      
        if (oldhandle!=null)                             
        {
          //transfer the previous context handle from session memory to ASPContextCache object *outside* session memory
          ASPContextCache cache = null;  
          boolean update_top_cache=false;
          boolean update_user_context_caches=false;          
          
          Map user_context_caches =  (Map)top_cache.get(mgr.getSessionId()); //get the Map object containing context caches for this user
          if (user_context_caches!=null)
             cache = (ASPContextCache)user_context_caches.get(context_id); //get the ASPContextCache object for the window id.
          else
          {             
             user_context_caches = new FndSoftHashMap(1);
             update_top_cache=true; 
          }
          
          if (cache==null)          
          {
            cache = new ASPContextCache(Integer.parseInt(mgr.getASPConfig().getParameter("ADMIN/CONTEXT/CACHE/MAX_FLOWS","5")));
            update_user_context_caches=true; 
          }                      
          
          if (DEBUG) debug("ASPContextCache.put(): Moving old handle in session to context cache out of session context_id="+context_id+" page_id="+oldhandle.key);
          cache.put(oldhandle.key,oldhandle.context);
          
          if (update_user_context_caches)
            user_context_caches.put(context_id,cache); //call put() only when necessary
          if (update_top_cache)
            top_cache.put(mgr.getSessionId(),user_context_caches);  //call put() only when necessary        
        }
        
        if (DEBUG)  debug("ASPContextCache.put(): Storing current context in session for context_id="+context_id+" page_id="+key);        
        session.setAttribute(SESS_ATTR_CONTEXT_HANDLE+context_id, new ContextHandle(key,context));
      }           
      
      if (DEBUG)
      {
         debug("**************************************************\n");

         ASPContextCache cache = (ASPContextCache)session.getAttribute(SESS_ATTR_CONTEXT_CACHE+context_id);
         int star_cache_size = 0;

         if (cache != null)
            star_cache_size = cache.getCacheContentSize()/1024;
         int buffer_size = getObjectSize(context)/1024;
         int total = buffer_size+star_cache_size;

         debug("Star cache size:"+star_cache_size+"KB\n");
         debug("Current context Buffer size:"+buffer_size+"KB context id:"+context_id+" key:"+key+"\n");
         debug("Approx. Total Context size:"+total+"KB\n");
         debug("**************************************************\n");
      }
   }
    
   private int getCacheContentSize() throws FndException       
   {
      int cache_size = 0;
      for (int i=0; i<context_handles.size();i++)
      {
         ContextHandle ct= (ContextHandle)context_handles.get(i);
         cache_size += getObjectSize(ct);
      }
      return cache_size;
   } 
    
   
   /*
    * Called when the user's session is destroyed all the corresponding contexts 
    * which are outside the session memory are no longer needed. Called from 
    * FndwebSessionListner.sessionDestroyed()
    */ 
   static void clearCacheOnSessionId(String session_id ) //throws FndException
   {      
      if (DEBUG) debug("ASPContextCache.clearCacheOnSessionId(): Clearing all cached context data for session "+session_id);
      top_cache.remove(session_id);
   }
    
   private static boolean[] fetchUserCacheMethod(Buffer context) throws FndException
   {
      try
      {  
         boolean[] user_method = {false,false};
         if(context !=null)
         {
            Item item = context.getItem( "__KEEPCONTEXT", context.getItem( "__KEEPCONTEXT", null ) );
            Item item2 = context.getItem( "__REMOVECONTEXT", context.getItem( "__REMOVECONTEXT", null ) );
   
            if (item==null || item2==null) return user_method;
            if ( "TRUE".equals( item.getString() ) )
               user_method[0]=true;
            if ( "TRUE".equals( item2.getString() ) )
               user_method[1]=true;
         }
         return user_method;
      }
      catch( Throwable any )
      {
          throw new FndException(any);
      }

   }
   
   /**
    * Return the cached context for given session and page id
    * or null if such entry does not exist.
    */
   private ContextHandle get( String key ) throws FndException 
   {
      if(DEBUG) debug("ASPContextCache.get("+key+")");
      
      if( Str.isEmpty(key) ) return null;
            
      for (int i=0; i<context_handles.size();i++)
      {
         ContextHandle ctH= (ContextHandle)context_handles.get(i);
         if (key.equals(ctH.key)) 
         {
           if (i>0)  //remove contexts that appear before this context since they are now obsolete (i.e. not reachable from browser history) 
           {
             for (int j=0; j<i; j++)              
               context_handles.removeFirst();             
           }
           return ctH;         
         }
      }
      
      return null;      
   }
   
   /**
    * similar to get() but additionally removes the the returned context. 
    * used when moving contexts from temperory cache to a cache dedicated 
    * for a particular window.      
    */   
   private ContextHandle remove( String key ) throws FndException 
   {
      if(DEBUG) debug("ASPContextCache.remove("+key+")");      
           
      if( Str.isEmpty(key) ) return null;
      
      for (int i=0; i<context_handles.size();i++)
      {
         ContextHandle ctH= (ContextHandle)context_handles.get(i);
         if (key.equals(ctH.key))
         {
             context_handles.remove(i);
             return ctH;
         }
      }             
      return null;       
   }
   
   
   /**
    *
    */
   private void put( String key, ContextObject context ) throws FndException 
   {
      if(DEBUG) debug("ASPContextCache.put("+key+")");
      
      ContextHandle ctxH = new ContextHandle(key,context);
      
      if (context_handles.size()>=max_size)        
        context_handles.removeLast();
      
      context_handles.addFirst(ctxH);      
   }       
      
      
   /**
    * Clear the whole cache
    */
   void clear() 
   {
     context_handles.clear();
   }

      
   void printCacheContents(String context_id) throws FndException       
   {
      
      debug("Printing Context Cache [Context Id="+context_id+"]");           

      StringBuffer d=new StringBuffer();
      for (int i=0; i<context_handles.size();i++)
      {
         ContextHandle ct= (ContextHandle)context_handles.get(i);
         d.append("["+i+"] - "+ct.key+"["+getObjectSize(ct)+"]"+"\r\n\t\t");                 
         HashMap data = ct.context.getDataMap();
         Iterator iter = data.keySet().iterator();
         while (iter.hasNext())
            d.append("\n========\n"+((ContextDataSet)iter.next()).toString());
      }
      debug(d.toString());
      debug("Number of contexts in cache: "+context_handles.size());
      
   } 
   
   
   //==========================================================================
   //  Debugging, error
   //==========================================================================
   
   private static void debug( String line ) {
      Util.debug(line);
   }
   
   /**
   * Determines the size of an object in bytes when it is serialized.
   * This should NOT be used for anything other than optimization
   * testing since it can be memory and processor intensive.
   */
    private static int getObjectSize(Object object){
      if(object==null){        
        return -1;
      }
      try{
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(object);
         byte[] bytes = baos.toByteArray();
         oos.close();
         baos.close();
         return bytes.length;
      }
      catch(Exception e){
        e.printStackTrace();
      }
    return -1;
  }  
  
  
   //==========================================================================
   //  Inner classes
   //==========================================================================

   static class ContextHandle implements java.io.Serializable
   {      
      private String        key;    
      private long          timestamp;      
      private ContextObject        context;     
      
      /**
       * Constructor
       */
      ContextHandle( String key, ContextObject context) 
      {
         if(DEBUG) Util.debug("ContextHandle.constructing handle with: \nkey: "+key);
         this.context   = context;        
         this.key       = key;         
         this.timestamp = System.currentTimeMillis();                 
      }      
      
   }

   // Object which keeps collection of ContextDataSets and Buffer
   static class ContextObject implements java.io.Serializable
   {
      private Buffer  context_buffer; //buffered data for ASPRowSet
      private HashMap data_map;       //AbstractArray for FndDataSet

      /**
       * Constructor
       */      
      ContextObject(Buffer context_buffer, HashMap data_map)
      {
         this.context_buffer = context_buffer;
         this.data_map       = data_map;
      }
      
      Buffer getContextBuffer()
      {
         return context_buffer;
      }
      
      HashMap getDataMap()
      {
         return data_map;
      }      
   }
   
   // Stores AbstractArray and releated information
   static class ContextDataSet implements java.io.Serializable
   {
      private FndAbstractArray data;
      private int skip_rows;
      private int rows;
      private int current_row;
      private String sorted_column;
      private boolean sorted_ascending;
      private boolean db_links_created;
      private boolean has_more_rows;
      private boolean filter_enabled;
      
      /**
       * private constructor to be used by clone method
       */
      private ContextDataSet()
      {
         
      }
      
      /**
       * constructor to be used when adding FndAbstractArrays
       */      
      ContextDataSet(FndAbstractArray data,int skip_rows,int rows,int current_row,String sorted_column,boolean sorted_ascending,boolean db_links_created,boolean has_more_rows,boolean filter_enabled)
      {
         this.data = data;
         this.skip_rows = skip_rows;
         this.rows = rows;
         this.current_row = current_row;
         this.sorted_column = sorted_column;
         this.sorted_ascending = sorted_ascending;
         this.db_links_created = db_links_created;
         this.has_more_rows = has_more_rows;
         this.filter_enabled = filter_enabled;
      }
          
      void setCurrentRow(int current_row_no)
      {
         this.current_row = current_row_no;
      }
      
      void setFilterEnabled(boolean filter_enabled)
      {
         this.filter_enabled=filter_enabled;
      }
      
      void setSortedColumn(String sorted_column)
      {
         this.sorted_column = sorted_column;
      }

      void setSortedAssending(boolean sorted_ascending)
      {
         this.sorted_ascending = sorted_ascending;
      }
      
      void setDBLinksCreated(boolean db_links_created)
      {
         this.db_links_created = db_links_created;
      }      
      
      
      boolean isFilterEnabled()
      {
         return filter_enabled;
      }
      
      boolean hasMoreRows()
      {
         return has_more_rows;
      }
      
      boolean isDBLinksCreated()
      {
         return db_links_created;
      }
      
      boolean isSortedAssending()
      {
         return sorted_ascending;
      }
      
      String getSortedColumn()
      {
         return sorted_column;
      }
      
      int getCurrentRow()
      {
         return current_row;
      }
      
      int getRows()
      {
         return rows;
      }
      
      FndAbstractArray getData()
      {
         return data;
      }
      
      int getSkipRows()
      {
         return skip_rows;
      }      
      

      
      // Returns the cloned object
      protected Object clone()
      {
         ContextDataSet obj = new ContextDataSet();
         try {
            obj.data = (FndAbstractArray)data.clone();
         } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
         }
         obj.skip_rows = skip_rows;
         obj.rows = rows;
         obj.current_row = current_row;
         obj.sorted_column = sorted_column;
         obj.sorted_ascending = sorted_ascending;
         obj.db_links_created = db_links_created;
         obj.has_more_rows = has_more_rows;
         obj.filter_enabled = filter_enabled;
         return obj;
      }
      
      public String toString()
      {
         AutoString out = new AutoString();
         out.append("\n ===========================================");
         out.append("\n  Contents of Context Data Set");
         out.append("\n ===========================================");
         out.append("\n SKIP_ROWS        ",skip_rows+"");
         out.append("\n ROWS             ",rows+"");
         out.append("\n HAS_MORE_ROWS    ",has_more_rows+"");
         out.append("\n CURRENT_ROW      ",current_row+"");
         out.append("\n FILTER_ENABLED   ",filter_enabled+"");
         out.append("\n SORTED_ASCENDING ",sorted_ascending+"");
         out.append("\n DB_LINKS_CREATED ",db_links_created+"");
         out.append("\n SORTED_COLUMN    ",sorted_ascending+"");   
         out.append("\n ",ASPManager.printAbstractArray(data,0));
         return out.toString();
         
      }
      
   }
   
   
}
