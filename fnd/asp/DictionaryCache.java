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
 * File        : DictionaryCache.java
 * Description : cache for all language translations.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Mangala P  2006--8-16 - Created.
 *    Geethika G 2006--8-22 - Added the methods isAllTranslationsFetched(),
 *                            clearTranslations() and getModule()
 *    Buddika H  2007--3--1 - Improved getUsageVersionID()
 *    Sasanka D  2007-12-31 - Bug 69826, Modified initModule() to correct translation issue.
 *    Sasanka D  2008-01-16 - Bug 70598, Reverted the change done in bug 69826.
 *    Sasanka D  2008-05-13 - Bug 73655, Modified initModule() and initLanguage().
 **/

package ifs.fnd.asp;

import java.util.*;

import ifs.fnd.service.*;

public class DictionaryCache
{
   private static Hashtable dictionary = new Hashtable();
   
   private static String module_list;
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.DictionaryCache");
   public static boolean EXTENDED_DEBUG = Util.isDebugEnabled("ifs.fnd.asp.DictionaryCache.extended");
   
   public static synchronized void initLanguage(ASPManager mgr, String lang_code)
   {  
      if(DEBUG) Util.debug("DictionaryCache: Enter initLanguage() for the language:"+lang_code+" & Session ID:"+mgr.getSessionId());            
      
      if (isLanguageInitialized(lang_code))
      {
         if(DEBUG) Util.debug("DictionaryCache: Break initLanguage() for the language:"+lang_code+" & Session ID:"+mgr.getSessionId());        
         return;
      }
      if(DEBUG) Util.debug("Started fetching for the language:"+lang_code+" & Session ID:"+mgr.getSessionId()); 
      long timestamp1 = System.currentTimeMillis();;               

      DictionaryMap dicmap = new DictionaryMap();
      String where_txt = getModulesDeployed(mgr.getModuleList());
      
      int rec_count =mgr.getASPConfig().getTranslationLoadingBufferSize();

      String SELECT_STMT =   "SELECT PATH,TRANSLATION,TERM_USAGE_VERSION_ID FROM runtime_translations WHERE LANG_CODE = ? AND MAIN_TYPE=? AND (INSTR( ?,MODULE)>0) AND (BULK=1 OR MODULE ='FNDWEB')";            
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();                
      ASPQuery q = trans.addQuery("GET_TRANSLATIONS",SELECT_STMT);
      q.addParameter("LANG_CODE", "S", "IN", lang_code.toLowerCase()); 
      q.addParameter("MAIN_TYPE", "S", "IN", "JAVA");
      q.addParameter("MODULE", "S", "IN", where_txt.toUpperCase());
      q.setBufferSize(rec_count);
      
      if(DEBUG) trans.traceBuffer("****QUERY******");
      
      if(mgr.isPortalPage())
      {
         if(mgr.getPortalPage().isUndefined())
            trans = mgr.performConfig(trans);
         else
            trans = mgr.perform(trans);
      }
      else
      {
         if (mgr.getASPPage().isUndefined() )
            trans = mgr.performConfig(trans);
         else
            trans = mgr.perform(trans);
      }
      
      ASPBuffer buf = trans.getBuffer("GET_TRANSLATIONS");
      int count=0;
      if (buf!=null)
      {
         try
         {
            count = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
            if(DEBUG) Util.debug(" Number records collected " + count);
         }
         catch(Exception e)
         {
            if(DEBUG)             
               Util.debug("DictionaryCache: No Records were found for the language code: "+lang_code);
            count = 0;
         }
      }
      dicmap.setModuleAsTranslationLoaded("FNDWEB");

      for (int i=0; i<count; i++)
      {
         ASPBuffer b = buf.getBufferAt(i);
         String path =  b.getValue("PATH").toUpperCase();
         path = (path!=null)?path.intern():"";
         String translation =  b.getValue("TRANSLATION");
         translation = (translation!=null)?translation.intern():"";
         String version_id =  b.getValue("TERM_USAGE_VERSION_ID"); 
         version_id = (version_id!=null)?version_id.intern():"";
         dicmap.putTranslations(path,translation,version_id);         
      }   
      dictionary.put(lang_code.toUpperCase(),dicmap); 
      if (DEBUG)
      {
          Util.debug("====================================================");
          Util.debug("Translations for " + lang_code + " are loaded in " + Math.round((System.currentTimeMillis()-timestamp1)/1000)+" seconds.");
          Util.debug("====================================================");
      }
   }
   
   public static synchronized void initModule(ASPManager mgr, String lang_code, String module)
   {      
      if(DEBUG) Util.debug("DictionaryCache: Enter initModule() for the language:"+lang_code+" module:"+module+" & Session ID:"+mgr.getSessionId()); 
      
      if (mgr.isEmpty(module) || isModuleInitialized(lang_code,module)) 
      {   
         if(DEBUG) Util.debug("DictionaryCache: Break initModule() for the language:"+lang_code+" module:"+module+" & Session ID:"+mgr.getSessionId());       
         return;
      }
      
      if(DEBUG) Util.debug("Started fetching for the language:"+lang_code+" module:"+module+" & Session ID:"+mgr.getSessionId()); 
      long timestamp1 = System.currentTimeMillis();;
           
      module = module.toUpperCase();      
      DictionaryMap dicmap = (DictionaryMap)dictionary.get(lang_code.toUpperCase());

      int rec_count = mgr.getASPConfig().getTranslationLoadingBufferSize();
              
      String SELECT_STMT = "SELECT PATH,TRANSLATION,TERM_USAGE_VERSION_ID FROM runtime_translations WHERE LANG_CODE = ? AND MAIN_TYPE= ? AND MODULE = ? AND  BULK = 0";
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();                
      ASPQuery q = trans.addQuery("GET_TRANSLATIONS",SELECT_STMT);
      q.addParameter("LANG_CODE", "S", "IN", lang_code.toLowerCase());         
      q.addParameter("MAIN_TYPE", "S", "IN", "JAVA");
      q.addParameter("MODULE", "S", "IN", module.toUpperCase());
         
      q.setBufferSize(rec_count);
      if(mgr.isPortalPage())
      {
         if(mgr.getPortalPage().isUndefined())
            trans = mgr.performConfig(trans);
         else
            trans = mgr.perform(trans);
      }
      else
      {
         if (mgr.getASPPage().isUndefined() )
            trans = mgr.performConfig(trans);
         else
            trans = mgr.perform(trans);
      }
      
      ASPBuffer buf = trans.getBuffer("GET_TRANSLATIONS");
      int count=0;
      if (buf!=null) 
      {
         try
         {
            count = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
         }
         catch(Exception e)
         {
            if(DEBUG)             
               Util.debug("DictionaryCache: No Records were found for the language code: "+lang_code+"module: "+module);
            count = 0;
         }
      }
      for (int i=0; i<count; i++)
      {
         ASPBuffer b = buf.getBufferAt(i);
         String path = b.getValue("PATH").toUpperCase().intern();
         String translation =  (b.getValue("TRANSLATION")!=null) ? b.getValue("TRANSLATION").intern() : "";
         String version_id =  (b.getValue("TERM_USAGE_VERSION_ID")!=null) ? b.getValue("TERM_USAGE_VERSION_ID").intern() : "";
         dicmap.putTranslations(path,translation,version_id);
      } 
      dicmap.setModuleAsTranslationLoaded(module);
      if (DEBUG)
      {
          Util.debug("====================================================");
          Util.debug("Translations for " +  module + " - " + lang_code + " are loaded in " + Math.round((System.currentTimeMillis()-timestamp1)/1000)+" seconds.");
          Util.debug("====================================================");
      }
      
   }
   
   static boolean isLanguageInitialized(String language)
   {
      return (dictionary.get(language.toUpperCase())!=null);         
   }
   
   static boolean isModuleInitialized(String language, String module)
   {
      //check upp
      DictionaryMap lng_dic  = (DictionaryMap)dictionary.get(language.toUpperCase());
      if (lng_dic==null) return false;
      return lng_dic.isModuleTranslationsLoaded(module);
   }
   
   public static boolean isAllTranslationsFetched(ASPManager mgr,String lang_code)
   {  
      
      if (dictionary.get(lang_code.toUpperCase())!=null)
      {
         DictionaryMap dicMap = (DictionaryMap)dictionary.get(lang_code.toUpperCase());
         HashSet module_list = mgr.getModuleList();
         module_list.add("FNDWEB");
         Iterator it = module_list.iterator(); 
         

         while (it.hasNext())
         {  
            String module = (String)it.next();            
            if (!dicMap.isModuleTranslationsLoaded(module.toUpperCase()))
               return false;   
         }        
         return true;
      }
      else
         return false;
   }
   
   public static synchronized void clearTranslations(String lang_code)
   {
      if(DEBUG) Util.debug("ASPConfigFile: clearLanguage() for the language:"+lang_code);            
      dictionary.remove(lang_code.toUpperCase());
   }
   
   static synchronized String getTranslationText(ASPManager mgr, String language, String key)
   {
      DictionaryMap lng_dic = (DictionaryMap)dictionary.get(language.toUpperCase());
      if (lng_dic == null)
         initLanguage(mgr, language);
      
      lng_dic = (DictionaryMap)dictionary.get(language.toUpperCase());
      if (lng_dic == null)
      {
         Util.debug("***************************** Critical Error ("+ key +") !!! - Dictionary not loaded.");
         return null;
      }
      
      // Modified by Terry 20131127
      // Original:
      // return lng_dic.getTranslationText(key);
      return lng_dic.getTranslationText(mgr, language, key);
      // Modified end
   }
   
   static String getUsageVersionID(String language, String key)
   {
      DictionaryMap lng_dic  = (DictionaryMap)dictionary.get(language.toUpperCase());
      if (lng_dic==null) return "";      
      return lng_dic.getUsageId(key);
   }  
   
   static void addTranslation(String language, String key, String translation_text)
   {
      DictionaryMap dicmap = (DictionaryMap)dictionary.get(language.toUpperCase());
      if (dicmap!=null)
      {
        dicmap.putTranslations(key,translation_text,"");
        if (EXTENDED_DEBUG)
           Util.debug("Successfully added key "+ key);  
      }
      else
        Util.debug("An error occurred while Adding Dictionary Value: " + key);  
   }
     
   static private String getModulesDeployed(HashSet modules)
   {
      if (module_list!=null)
         return module_list;

      Iterator it = modules.iterator(); 
      String where_txt = "";

      while (it.hasNext())
      {           
         where_txt = where_txt +((String)it.next()).toUpperCase() +"^";                  
      }
      module_list = where_txt + "FNDWEB";
      return module_list;
   }
   
   static public String getModule(String path)
   {     
      String module = null;      
      if( "DefaultStdPortlet.page".equals(path) )      
         return module;                  

      //remove upto "secured" part from path
      int sec_index = path.indexOf("secured");
      if ((sec_index>-1))
         path = path.substring(sec_index+"secured".length());
         

      int pos1 = path.indexOf("..");
      int pos2 = -1;
      
      while(pos1>0)
      {
         int p = pos1 + 2;
         if(path.charAt(pos1-1)=='/')
         {
            pos2 = path.lastIndexOf("/",pos1-2);
            if(pos2>=0)
            {
               path = path.substring(0,pos2) + path.substring(pos1+2);
               p = pos2;
            }
         }
         pos1 = path.indexOf("..",p);
         pos2 = -1;
      }


      if ( path.startsWith("/common/scripts") )      
         return module;
      
      if(path.indexOf("/")>-1)
         path = path.substring(path.indexOf("/")+1);
      if(path.indexOf("/")>-1)
         module = path.substring(0,path.indexOf("/"));     
      
      return module;
   }
   
   public static void printTranslationsToDebug(String lang_code)
   {  
      if(DEBUG) Util.debug("DictionaryCache: printTranslationsToDebug() for the language:"+lang_code);
      
      DictionaryMap dicMap = (DictionaryMap)dictionary.get(lang_code);
      if (dicMap!=null)
      {   
         Util.debug("=============================================");
         Util.debug("Translations Info for the language:"+lang_code);  
         //printing the module list   
         Util.debug("=============================================");
         Util.debug("Total Modules:"+Integer.toString(dicMap.modules.size()));
         //printing the translations list
         Util.debug("=============================================");
         Util.debug("Total Translations:"+Integer.toString(dicMap.translations.size()));
         Util.debug("=============================================");
      }
      else
      {
         Util.debug("=============================================");
         Util.debug("No Translations for the language:"+lang_code);  
         Util.debug("=============================================");
      }
   }
   
   //-------------------------------------------------------------
   // Nested Inner classes
   //-------------------------------------------------------------
   private static class Translations
   { 
      String translated_text;
      String usage_version_id;
   }
   
   private static class DictionaryMap
   {
      HashSet modules = new HashSet();
      Hashtable translations = new Hashtable(8209); //initial capacity; prime number
      // Added by Terry 20131127
      // Save path clear timestamp
      private static Hashtable<String, Long> translations_timestamp = new Hashtable<String, Long>();
      private static final int translations_interval  = 720;
      // Added end

      private boolean isModuleTranslationsLoaded(String module)
      {
        return modules.contains(module.toUpperCase());
      }
      
      private void setModuleAsTranslationLoaded(String module)
      {
         modules.add(module.toUpperCase());
      }

      private void putTranslations(String path,String translation,String version_id)
      {  
         Translations trn_obj = new Translations();         
         trn_obj.translated_text = translation;         
         trn_obj.usage_version_id = version_id;
         translations.put(path,trn_obj);
         // Added by Terry 20131127
         // Save path clear timestamp
         translations_timestamp.put(path, System.currentTimeMillis());
         // Added end
      }
      
      private String getTranslationText(ASPManager mgr, String language, String key)
      {  
         // Added by Terry 20131127
         // Save path clear timestamp
         if (translations_timestamp.containsKey(key))
         {
            long key_timestamp = (Long)translations_timestamp.get(key);
            long cur_time = System.currentTimeMillis();
            long key_mins   = (cur_time - key_timestamp) / 60000;
            if (key_mins > translations_interval)
            {
               getTranslation(mgr, language, key);
            }
         }
         else
            getTranslation(mgr, language, key);
         
         // Added end
         Translations tr = (Translations)translations.get(key);
         if (tr!=null)
            return tr.translated_text;
         else
         {
            if(EXTENDED_DEBUG)
               Util.debug("DictionaryCache: No Translations were found for the key: "+ key);
            return null;
         }
      }
      
      private String getUsageId(String key)
      {
         Translations tr = (Translations)translations.get(key);
         if (tr!=null)
            return tr.usage_version_id;
         else
         {
            //if(DEBUG)
              // Util.debug("DictionaryCache: No Usage version IDs were found for the key: "+ key);
            return "";
         }         
      }
      
      private void getTranslation(ASPManager mgr, String language, String path)
      {
         String SELECT_STMT = "SELECT TRANSLATION,TERM_USAGE_VERSION_ID FROM runtime_translations WHERE LANG_CODE = ? AND MAIN_TYPE= ? AND PATH = ?";
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();                
         ASPQuery q = trans.addQuery("GET_TRANSLATIONS",SELECT_STMT);
         q.addParameter("LANG_CODE", "S", "IN", language.toLowerCase());         
         q.addParameter("MAIN_TYPE", "S", "IN", "JAVA");
         q.addParameter("PATH", "S", "IN", path);
         
         try
         {
            if(mgr.isPortalPage())
            {
               if(mgr.getPortalPage().isUndefined())
                  trans = mgr.performConfig(trans);
               else
               {
                  trans.addRequestHeader(mgr.getASPConfig().getConfigUser());
                  trans = mgr.perform(trans);
               }
            }
            else
            {
               if (mgr.getASPPage().isUndefined() )
                  trans = mgr.performConfig(trans);
               else
               {
                  trans.addRequestHeader(mgr.getASPConfig().getConfigUser());               
                  trans = mgr.perform(trans);
               }
            }
         } catch (FndException e) {
            e.printStackTrace();
         }
         
         ASPBuffer buf = trans.getBuffer("GET_TRANSLATIONS");
         
         int count=0;
         if (buf != null)
         {
            try
            {
               count = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
               if(DEBUG) Util.debug(" Number records collected " + count);
            }
            catch(Exception e)
            {
               if(DEBUG)             
                  Util.debug("DictionaryCache: No Records were found for the language code: "+language);
               count = 0;
            }
         }
         
         if (count > 0)
         {
            ASPBuffer b = buf.getBufferAt(0);
            String translation =  (b.getValue("TRANSLATION")!=null) ? b.getValue("TRANSLATION").intern() : "";
            String version_id =  (b.getValue("TERM_USAGE_VERSION_ID")!=null) ? b.getValue("TERM_USAGE_VERSION_ID").intern() : "";
            
            putTranslations(path, translation, version_id);
         }
         else
            translations_timestamp.put(path, System.currentTimeMillis());
      }
   }


}
