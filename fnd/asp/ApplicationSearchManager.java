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
 * File        : ApplicationSearchManager.java
 * Description : Handling Application search.
 * Notes       :
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/02/18 buhilk Bug id 88106, support for Federated search through windows-7 UI
 * 2007/12/05 rahelk Bug id 67990, support for domain groups
 * 2007/04/30 rahelk F1PR458 added support for b2b sites
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * 2007/02/20 rahelk Bug id 58590, Added Application Search support
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.service.*;
import ifs.fnd.base.*;
import ifs.application.applicationsearch.*;

import java.util.Vector;

public abstract class ApplicationSearchManager 
{
   final static boolean DEBUG  = Util.isDebugEnabled("ifs.fnd.asp.ApplicationSearchManager");   
   
   private static ApplicationSearchManager search_manager;
   private static String appType = "WEB";
   private static String appTypeWin = "WIN";   
   
   public static String GROUP_TYPE = "Group";
   
   public static synchronized ApplicationSearchManager getInstance(ASPConfig cfg) throws FndException
   {
      if(DEBUG) debug("ApplicationSearchManager.getInstance("+cfg+")");
      
      if (search_manager == null)
      {
         boolean RMI = "RMI".equals(cfg.getParameter("ADMIN/TRANSACTION_MANAGER","RMI"));
         
         if ("b2b".equals(cfg.getParameter("APPLICATION/APP_TEMPLATE")))
            appType = "B2B";
         
         if (RMI)
            search_manager = new EJBApplicationSearchManager();
         else
            search_manager = new JAPApplicationSearchManager();
      }
      
      return search_manager;
   }
   
   abstract SearchDomainArray listAvailableSearchDomains__(String user_id, ASPConfig cfg) throws FndException,ApplicationException,IfsException;
   abstract SearchResultArray search__(String user_id, String[] searchDomain, String criteria, boolean returnSnippet, int maxRows, int skipRows, ASPConfig cfg, String sessionId) throws FndException,ApplicationException,IfsException;
   abstract String getSnippet__(String user_id, String searchDomain, String primaryKey, String criteria, ASPConfig cfg) throws FndException,ApplicationException,IfsException;
   abstract String convertSearchCriteria__(String searchString, ASPConfig cfg) throws FndException,ApplicationException,IfsException;
   abstract String getIndexedAttributes__(String user_id, String searchDomain, ASPConfig cfg) throws FndException,ApplicationException,IfsException;
   
   
   Vector listAvailableSearchDomains(String user_id, ASPConfig cfg) throws FndException,
                                                                           ApplicationException,IfsException 
   {

      SearchDomainArray sdarr = search_manager.listAvailableSearchDomains__(user_id, cfg);  
      
      if(DEBUG) FndDebug.debugArrayRecord(sdarr);

      int noOfRecs = sdarr.size();
      Vector domainVec = new Vector();
      String url = "";      
      String urlWin = "";

      for (int i=0; i<noOfRecs; i++)
      {
         ifs.application.applicationsearch.SearchDomain domain = sdarr.get(i);
         String available_in = domain.availableIn.getValue();
         boolean isUsedInSearchMethod = (domain.usedInSearchMethod.getValue()).booleanValue();

         if(((available_in.indexOf(appType)>-1) || (available_in.indexOf(appTypeWin) > -1) ) && isUsedInSearchMethod)
         {
            url = (String)("WEB".equals(appType)?domain.webPage.getValue():domain.b2bWebPage.getValue());
            urlWin = (String)domain.winUrl.getValue();
            
            domainVec.addElement(new SearchDomain((String)domain.searchDomainId.getValue(),
                                                  (String)domain.description.getValue(),
                                                  (String)domain.view.getValue(),
                                                  (String)domain.column.getValue(),
                                                   url,
                                                  (String)domain.searchDomainType.getValue(),
                                                   urlWin));
         }
      }

      return domainVec;
   }
   
   String convertSearchCriteria(String searchString, ASPConfig cfg) throws FndException,
                                                                           ApplicationException,IfsException 
   {
      String result = search_manager.convertSearchCriteria__(searchString, cfg);

      if(DEBUG) debug(result);      

      return result;
   }

   public String getSnippet(String user_id, String searchDomain, String primaryKey, String criteria, ASPConfig cfg) throws FndException,
                                                                                                                    ApplicationException,IfsException 
   {

      String snippet = search_manager.getSnippet__(user_id, searchDomain, primaryKey, criteria, cfg);
      if(DEBUG) debug(snippet);      
         
      return formatSnippet(snippet);
   }
   
   public String getIndexedInfo(String user_id, String searchDomain, ASPConfig cfg) throws FndException,
                                                                                                                    ApplicationException,IfsException 
   {

      String attributes = search_manager.getIndexedAttributes__(user_id, searchDomain, cfg);
      
      if(DEBUG) debug(attributes);      
         
      return attributes;
   }

   public Vector search(String user_id, String[] searchDomain, String criteria, boolean returnSnippet, int maxRows, int skipRows, ASPConfig cfg, String sessionId) throws FndException,
                                                                                                                                                                          ApplicationException,
                                                                                                                                                                          IfsException
   {
      return search(user_id, searchDomain, criteria, returnSnippet, maxRows, skipRows, cfg, sessionId, false);
   }
   
   public Vector search(String user_id, String[] searchDomain, String criteria, boolean returnSnippet, int maxRows, int skipRows, ASPConfig cfg, String sessionId, boolean winResults) throws FndException,
                                                                                                                                                                          ApplicationException,
                                                                                                                                                                          IfsException
   {
      Vector results = new Vector();
      SearchResultArray srarr = search_manager.search__(user_id, searchDomain, criteria, returnSnippet, maxRows, skipRows, cfg, sessionId);
      
      if(DEBUG) FndDebug.debugArrayRecord(srarr);

      int size = srarr.size();
      Vector domainList = UserDataCache.getInstance().getSearchDomains(sessionId);

      for (int i=0; i<size; i++)
      {
         SearchResult sr = srarr.get(i);
         String available_in = sr.availableIn.getValue();

         if (available_in.indexOf(appType)>-1 || ((winResults) && (available_in.indexOf(appTypeWin)>-1)))
         {
            String domainId = (String)sr.searchDomainId.getValue();

            SearchDomain sd = findSearchDomain(domainId, domainList);

            results.add(new Result(domainId,
                                   (String)sr.title.getValue(),
                                   (String)sr.primaryKey.getValue(),
                                   (String)sr.snippet.getValue(),
                                   ((Long)sr.rank.getValue()).intValue(),
                                    sd.view, sd.url, sd.description, available_in));
         }
      }

      return results; 
   }
   
   public SearchDomain findSearchDomain(String domainId, Vector domainList)
   {
      int noOfDomains = domainList.size();
      for (int i=0; i<noOfDomains; i++)
      {
         if (domainId.equals(((SearchDomain)domainList.elementAt(i)).searchDomain))
            return (SearchDomain)domainList.elementAt(i);
      }
      return null;
   }
   
   private static void debug( String line )
   {
      Util.debug(line);
   }
   
   public class SearchDomain
   {
      String searchDomain;
      String description;
      String view;
      String column;
      String url;
      String type;
      String urlWin;
      
      SearchDomain(String search_domain, String description, String view, String col, String url, String type, String urlWin)
      {
         this.searchDomain = search_domain;
         this.description = (description==null || "".equals(description))?search_domain:description;
         this.view = view;
         this.column = col;
         this.url = url;
         this.type = type;
         this.urlWin = urlWin;
      }

      public String getURLWin()
      {
          return urlWin;
      }
      
      public String getSearchDomain()
      {
         return searchDomain;
      }
      
      public String getDescription()
      {
         return description;
      }
      
      public String getView()
      {
         return view;
      }
      
      public String getColumn()
      {
         return column;
      }
      
      public String getURL()
      {
         return url;
      }
      
      public String getDomainType()
      {
         return type;
      }
   }
   
   private String formatSnippet(String text)
   {
      if (text == null) return "";

      String formattedText = text.replaceAll("<#","<font class=snippetHilite>");
      formattedText = formattedText.replaceAll("#>", "</font>");
      formattedText = formattedText.replaceAll("\n", "<br>");

      return formattedText;

      //return text.replaceAll("<#","<font class=snippetHilite>").replaceAll("#>", "</font>");
   }
   
   public class Result
   {
      String searchDomainId;
      String snippet;
      String title;
      String primaryKey;
      int rank;
      String url;
      String view;
      String desc;
      String unformattedSnippet;
      String availableClients;
      
      Result(String domain, String title, String keys, String snippet, int rank, String view, String url, String desc, String clients)
      {
         this.searchDomainId = domain;
         this.title = title;
         this.primaryKey = keys;
         this.snippet = formatSnippet(snippet);
         this.unformattedSnippet = snippet.replace("<#", "").replace("#>", "");
         this.rank = rank;
         this.view = view;
         this.url = url; //"common/scripts/FndUser.page"; //url;
         this.desc = desc;
         this.availableClients = clients;
      }

      public String getClients()
      {
          return availableClients;
      }

      public String getSearchDomainId()
      {
         return searchDomainId;
      }
      
      public String getTitle()
      {
         return title;
      }
      
      public String getPrimaryKeys()
      {
         return primaryKey;
      }
      
      public String getSnippet()
      {
         return snippet;
      }
      
      public String getUnformattedSnippet()
      {
         return unformattedSnippet;
      }
      
      public int getRank()
      {
         return rank;
      }
      
      public String getView()
      {
         return view;
      }
      
      public String getURL()
      {
         return url;
      }
      
      public String getDescription()
      {
         return desc;
      }
   }
}


