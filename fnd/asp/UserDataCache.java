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
 * File        : UserDataCache.java
 * Description : Cache to store and keep all user related data.
 * Notes       :
 * ----------------------------------------------------------------------------
 * 2007/05/15 rahelk Bug id 65257 corrected. Checked null before removing
 * 2007/02/20 rahelk created.
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;


import java.util.HashMap;
import java.util.Vector;
import java.util.Calendar;
import java.util.StringTokenizer;

import ifs.fnd.service.*;
import ifs.fnd.base.*;

public class UserDataCache
{
   final static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.asp.UserDataCache");   
   
   private static UserDataCache user_cache;
   private static HashMap userDataMap;
   
   //vector indices
   private static final int SEARCH_DOMAIN = 0;
   private static final int SEARCH_VIEWS = 1; //available views for application search
   
   /**
    * Data structure to hold all user data related to a session. 
    * Key is the session_id
    * 0th index - search domains
    *
    * The data pointed to by this session id wil be cleared on invalidation of session
    * using FndwebSessionListener.
    */
   private UserDataCache()
   {
      userDataMap = new HashMap();
   }
   
   public static synchronized UserDataCache getInstance() throws FndException
   {
      if(DEBUG) debug("UserDataCache.getInstance");
      
      if (user_cache == null)
         user_cache = new UserDataCache();
      
      return user_cache;
   }
   
   void initialize(String user_id, ASPManager mgr) throws FndException, ApplicationException, IfsException
   {
      //Vector obj = (Vector)userDataMap.get(user_id);
      Vector obj = (Vector)userDataMap.get(mgr.getSessionId());
      
      if (obj == null)
         initializeUserData(user_id, mgr);
         
   }
   
   /* one initialize to call all DB methods. To avoid several DB calls.
    * Add comments to separate each user data segment
    */
   private void initializeUserData(String user_id, ASPManager mgr) throws FndException, ApplicationException, IfsException
   {
      Vector userData = new Vector();
      
      //start - searchDomain 
      ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(mgr.getASPConfig());
      Vector searchDomains = searchManager.listAvailableSearchDomains(user_id,mgr.getASPConfig());
      Vector availableViews = extractAvailableSearchViews(searchDomains);
      //end - searchDomain 
      
      //start - another 
      //end - another
      
      userData.add(SEARCH_DOMAIN, searchDomains);  //add search domains to user data list
      userData.add(SEARCH_VIEWS, availableViews);  //add available views for application search
      
      synchronized (userDataMap)
      {
         userDataMap.put(mgr.getSessionId(), userData);
      }
   }

   private Vector extractAvailableSearchViews(Vector domains)
   {
      Vector vec = new Vector();
      int size = domains.size();
      
      for (int i=0; i<size; i++)
         vec.add(((ApplicationSearchManager.SearchDomain)domains.elementAt(i)).view);

      return vec;
   }
   
   String[][] getSearchDomainList(String session_id) 
   {
      Vector obj = (Vector)userDataMap.get(session_id);
      Vector searchDomains = (Vector)obj.get(SEARCH_DOMAIN);
      int size = searchDomains.size();
      String[][] domains = new String[size][2];  // 0 - searchDomain, 1 - description
      
      for (int i=0; i<size; i++)
      {
         domains[i][0] = ((ApplicationSearchManager.SearchDomain)searchDomains.elementAt(i)).searchDomain;
         domains[i][1] = ((ApplicationSearchManager.SearchDomain)searchDomains.elementAt(i)).description;
      }

      return domains;
   }
   
   public Vector getSearchDomains(String session_id) 
   {
      Vector obj = (Vector)userDataMap.get(session_id);
      return (Vector)obj.get(SEARCH_DOMAIN);
   }
   
   /**
    * Return a vector with the list of all available views used by application search
    */
   public Vector getApplicationSearchViews(String session_id) 
   {
      Vector obj = (Vector)userDataMap.get(session_id);
      return (Vector)obj.get(SEARCH_VIEWS);
   }
   

   /*
    * Called when the user's session is destroyed. 
    * Called from FndwebSessionListner.sessionDestroyed()
    */ 
   static void clearCacheOnSessionId(String session_id ) 
   {      
      if (DEBUG) debug("UserDataCache.clearCacheOnSessionId: Clearing all user data cached for session "+session_id);
      if (userDataMap != null) userDataMap.remove(session_id);
   }
   
   
   private static void debug( String line )
   {
      Util.debug(line);
   }
}
