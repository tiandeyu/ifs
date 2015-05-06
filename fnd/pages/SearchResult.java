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
 *  File        : SearchResult.java
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2008/06/13 buhilk Bug id 69812, Modified addSearchProvider() to write the opensearch xml to dynacache.
 * 2008/03/14 sadhlk Bug id 71478, Modified printContents() to correct XSS issue.
 * 2007/06/29 buhilk IID F1PR497,  Modified run(), search(), printContents() and Added addSearchProvider().
 * 2007/03/06 rahelk Bug id 63987, passed directoryId instead of fnduser to search activity handler
 * 2007/02/20 rahelk Bug id 58590, created for Application Search support
 * ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;

import java.util.Vector;
import java.util.StringTokenizer;

public class SearchResult extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.SearchResult");   

   private Vector results;
   private boolean show_snippet;
   private String criteria;
   
   private String open_search_text;
   private boolean open_search;
   private boolean domains_available;
   
   public SearchResult(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      open_search = false;
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("SAVE")))
      {
         //Called from domain selection dialog to save settings
         saveProfile(); 
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("FETCH")))
      {
         fetchSnippet();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("ADDPROVIDER")))
      {
         addSearchProvider();
      }
      else if(!mgr.isEmpty(mgr.getQueryStringValue("opensearch")))
      {
         open_search = true;
         open_search_text = mgr.getQueryStringValue("opensearch");
         search();
      }
      else
         search();
   }
   
   private void fetchSnippet()
   {
      ASPManager mgr = getASPManager();  
      String snippet = "";

      try
      {
         ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(mgr.getASPConfig());
         String domain = mgr.getQueryStringValue("DOMAIN");
         String keys = mgr.getQueryStringValue("KEYS");
         String criteria = mgr.getQueryStringValue("CRITERIA");

         snippet = searchManager.getSnippet(mgr.getUserId(),domain,keys,criteria,mgr.getASPConfig());
      }
      catch (Exception any)
      {         
      }
      
      mgr.responseWrite(snippet);
      mgr.endResponse();
   }
   
   private void addSearchProvider()
   {
         ASPManager mgr = getASPManager();
         ASPConfig config = mgr.getASPConfig();
         String xml = "";
         String baseurl = config.getProtocol()+"://"+config.getApplicationDomain()+config.getApplicationPath();
         
         xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
         xml += "<OpenSearchDescription xmlns=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:moz=\"http://www.mozilla.org/2006/browser/search/\">\n";
         xml += "   <ShortName>IFS Applications</ShortName>\n";
         xml += "   <Description>Search for information from IFS Applications using keywords</Description>\n";
         xml += "   <Contact>No contact</Contact>\n";
         xml += "   <Url type=\"text/html\"\n";
         xml += "        method=\"GET\"\n";
         xml += "        template=\""+baseurl+"/common/scripts/SearchResult.page?opensearch={searchTerms}\">\n";
         xml += "   </Url>";
         xml += "   <LongName>IFS Applications - Search</LongName>";
         xml += "   <Image height=\"16\" width=\"16\" type=\"image/x-icon\">"+baseurl+"/common/images/ifsicon.ico</Image>\n";
         xml += "   <Query role=\"\" searchTerms=\"\" />\n";
         xml += "   <Developer>IFS Web Client</Developer>\n";
         xml += "   <Attribution>\n";
         xml += "      IFS Applications\n";
         xml += "   </Attribution>\n";
         xml += "   <SyndicationRight>open</SyndicationRight>\n";
         xml += "   <AdultContent>false</AdultContent>\n";
         xml += "   <Language>en-us</Language>\n";
         xml += "   <OutputEncoding>UTF-8</OutputEncoding>\n";
         xml += "   <InputEncoding>UTF-8</InputEncoding>\n";
         xml += "</OpenSearchDescription>\n";
                  
         ifs.fnd.asp.DynamicObjectCache.put("IFSSearchProvider.xml", xml, "text/xml", mgr, false);
                  
         mgr.responseWrite("Done");
         mgr.endResponse();      
   }
   
   private void saveProfile() throws FndException
   {
      ASPManager mgr = getASPManager();  
      
      writeGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"SelectedDomains", mgr.getQueryStringValue("DOMAINS"), false );
      writeGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"MaxRows", mgr.getQueryStringValue("MAXROWS"), false );
      writeGlobalProfileFlag("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"ShowSnippet","TRUE".equals(mgr.getQueryStringValue("SNIPPET")));
      
      mgr.endResponse();
   }
   
   private void search() throws FndException
   {
      ASPManager mgr = getASPManager();  
      int max_rows= 20;
      String domains="";
      
      try
      {
         ApplicationSearchManager searchManager = ApplicationSearchManager.getInstance(mgr.getASPConfig());
         
         if(!open_search)
         {
         
            domains  = mgr.readValue("FND_SEARCH_DOMAINS");
            if(mgr.isEmpty(domains))
            {
               domains_available = false;
               return;
            }
            domains_available = true;
            show_snippet = "TRUE".equals(mgr.readValue("FND_SNIPPET"));

            if ("SAVE".equals(mgr.readValue("FND_SEARCH_PROFILE")))
            {
               writeGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"SelectedDomains", domains, false );
               writeGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"MaxRows", mgr.readValue("FND_SEARCH_MAXROWS"), false );
               writeGlobalProfileFlag("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"ShowSnippet",show_snippet);
            }

            criteria = mgr.readValue("FND_APPLICATION_SEARCH");

            max_rows = Integer.parseInt(mgr.readValue("FND_SEARCH_MAXROWS"));
            
         }
         else
         {
            domains = readGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"SelectedDomains", false);
            if(mgr.isEmpty(domains))
            {
               domains_available = false;
               return;
            }
            domains_available = true;
            show_snippet = readGlobalProfileFlag("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"ShowSnippet",false);
            criteria = open_search_text;
            max_rows = Integer.parseInt(readGlobalProfileValue("Defaults/ApplicationSearch"+ProfileUtils.ENTRY_SEP+"MaxRows", ""+max_rows, false));
         }

         int skip_rows = 0;
         
         StringTokenizer st = new StringTokenizer(domains, ",");
         String[] domainArr = new String[st.countTokens()];
         int i=0;
         while (st.hasMoreElements())
         {
            domainArr[i] = (String)st.nextElement();
            i++;
         }
         
         results = searchManager.search(mgr.getUserId(),domainArr,criteria,show_snippet,max_rows,skip_rows,mgr.getASPConfig(),mgr.getSessionId());
      }
      catch (Exception any)
      {
         throw new FndException(any);
      }
   }
   
   protected String getDescription()
   {
      return "FNDPAGESSEARCHRESULTSWINDOWTITLE: Search Result";
   }

   protected String getTitle()
   {
      return "FNDPAGESSEARCHRESULTSWINDOWTITLE: Search Result";
   }
   

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();  
            
      if(open_search)
         appendDirtyJavaScript("f.FND_APPLICATION_SEARCH.value =\""+ASPManager.encodeStringForJavascript(open_search_text)+"\";");
      else
         appendDirtyJavaScript("f.FND_APPLICATION_SEARCH.value =\""+ASPManager.encodeStringForJavascript(mgr.readValue("FND_APPLICATION_SEARCH"))+"\";");
         
      beginDataPresentation();
      drawSimpleCommandBar(getTitle());

      if(!domains_available)
      {
         appendToHTML("\n<p>");
         printText("FNDSEARCHRESULTNODOMAINSFOUND: Please select at least one search domain.");
         appendToHTML("\n</p>");
      }
      else
      {
         int size = results.size();
         String url = "";

         if (size > 0)
         {
            appendToHTML("\n<p>");
            if (size == 1)
               printText("FNDSEARCHRESULTONEITEM: 1 item found");
            else
               printText(mgr.translate("FNDSEARCHRESULTNOOFITEMS: &1 items found",""+size));
            appendToHTML("\n</p>");
           
            for (int i=0; i<size; i++)
            {
               ApplicationSearchManager.Result row = (ApplicationSearchManager.Result)results.elementAt(i);

               ASPConfig cfg = mgr.getASPConfig();
               url = cfg.getProtocol()+"://"+cfg.getApplicationDomain()+cfg.getApplicationPath()+"/";
               url = url+row.getURL()+"?__APP_SEARCH=Y&VIEW="+row.getView()+"&KEYS="+mgr.URLEncode(row.getPrimaryKeys());

               appendToHTML("\n<p>");
               appendToHTML("<font class=snippetTitle>");
               appendToHTML("<a href=\""+url+"\">");
               appendToHTML(row.getTitle()+"</a>");
               //appendToHTML(row.getDescription()+" | "+row.getTitle()+"</a>");
               //appendToHTML("<a href=\""+url+"\">"+row.getTitle()+"</a>");
               appendToHTML("</font><br>\n");
               printText("("+row.getDescription()+")");
               appendToHTML("<br>\n");

               if (show_snippet)
               {
                  appendToHTML("<font class=normalTextValue>");
                  appendToHTML(row.getSnippet());
                  appendToHTML("</font><br>\n");
               }
               else
               {
                  appendToHTML("<font class=normalTextValue><div id=snippet"+i+">\n");
                  printLink("[fetch summary]","javascript:fetchSnippet("+i+",'"+row.getSearchDomainId()+"','"+row.getPrimaryKeys()+"','"+criteria+"');");
                  appendToHTML("</div></font>\n");
               }

               appendToHTML("<font class=snippetURL>");
               appendToHTML(row.getURL());
               appendToHTML("</font><br>\n");
               appendToHTML("</p>\n");
            }
         }
         else
         {
            printNewLine();
            printText("FNDSEARCHRESULTNOHITS: No results for your search - "+"'"+criteria+"'");
            printNewLine();
         }
      }
      endDataPresentation(false);         
   }
}
