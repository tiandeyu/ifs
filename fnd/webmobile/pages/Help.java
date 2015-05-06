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
*  File        : Help.java
*  Created     : buhilk
* ----------------------------------------------------------------------------
*  
*  New Comments:
* 2007-Dec-20 buhilk Bug ID: 70052 - Error loading window descriptions for miniFW.
* 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
*/


package ifs.fnd.webmobile.pages;

import ifs.fnd.webmobile.web.MobilePageProvider;
import java.util.*;
import java.text.Collator;

import ifs.fnd.asp.*;
import ifs.fnd.pages.Rtf2Html;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;



public class Help extends MobilePageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   private static final String SELECT_STMT = "SELECT version_handling_id, full_name, NVL(translated_text, basic_text) basic_text  FROM term_field_description WHERE version_handling_id IN ";    
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.webmobile.pages.Help");   

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPLog log;
   private String helpbase_fromdb;     
   private Collator collator;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================   
   private String helpurl;   
   //private TreeSet term_fields;
   private ArrayList term_fields;
   
   //===============================================================
   // Construction
   //===============================================================
   public Help(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      Help page = (Help)(super.clone(obj));

      // Cloning immutable attributes
      page.log = page.getASPLog();
      page.helpbase_fromdb = this.helpbase_fromdb;
      page.collator=this.collator;

      return page;
   }

   protected void doReset() throws FndException
   {
      log = null;      
      helpurl=null;
      term_fields=null;
      super.doReset();
   }

   protected boolean forceReset()
   {
      return true;
   }

   protected boolean forceClone()
   {
      return true;
   }

   public void run()
   {      

      ASPManager mgr = getASPManager();
      ASPConfig  cfg = mgr.getASPConfig();

      String helpbase = mgr.getConfigParameter("ADMIN/HELP_BASE_URL",helpbase_fromdb);
      //String helpurl = null;
      String url = null;
      String lang = null;
      String root = null;
      String module = null;
      String clsname = null;

      log = mgr.getASPLog();
      mgr.removePageId();

      url = mgr.getQueryStringValue("url");
      lang = mgr.getLanguageCode();
      root = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT", "/&(APPLICATION/PATH)/");
      String usagekey   = mgr.getQueryStringValue("USAGEID");
       
      
      if (DEBUG)
      {
        Util.debug(" - url="+url);
        Util.debug(" - usagekey="+usagekey);
      }

      if (mgr.isEmpty(url))
      {  // General Web Help
         helpurl = helpbase+"/"+lang+"/general/SystemNavigationWeb.htm";
         mgr.redirectTo(helpurl);
      }
      else if ( url.indexOf("ifs.") == 0 &&   url.indexOf("/")<0  )
      { // portlet help                  
         
         module  = url.substring(4,url.indexOf(".",5));
         if ("fnd".equals(module))
             module = "fndweb";
         clsname = url.substring(url.lastIndexOf(".")+1);
         helpurl = helpbase+"/"+lang+"/"+module+"/"+clsname+ "Java.htm";
         mgr.redirectTo(helpurl); 
      }
      else if (!Str.isEmpty(usagekey))
      { //help on fields, generated dynamically using terms
                         
            if (DEBUG) mgr.showUsageKeys(usagekey);
            
            Set merged_set = new HashSet();
            
            Set set = mgr.getPageUsages(usagekey);
            if (DEBUG) Util.debug("Usages inside preDefine() = "+(set!=null?set.toString():"[empty]"));
            
            if (set!=null)
             merged_set.addAll(set);            
                        
            set = mgr.getUsageIdSet(usagekey);
            if (DEBUG) Util.debug("Usages outside preDefine() = "+(set!=null?set.toString():"[empty]"));
            
            if (set!=null)                
              merged_set.addAll(set);            
            
            set=mgr.getFrameUsageIdSet(usagekey+"-"+mgr.FRAME_REF);
            if (DEBUG) Util.debug("Usages defined for multiFrame page = "+(set!=null?set.toString():"[empty]"));
            
            if (set!=null)                
              merged_set.addAll(set);    
            
            if (!merged_set.isEmpty())
            {
                
                HashSet temp_set = new HashSet();
                HashSet temp_set2 = new HashSet();
                Hashtable temp_table = new Hashtable();
               
                Hashtable code_part_labels = mgr.getCodePartLabels(); //special handling for code parts
               
                ArrayList duplicates = new ArrayList(); //contains display names which are duplicated
                
                //build version_ids list for WHERE IN clause, also find duplicated display names
                StringBuffer version_ids_buf =new StringBuffer();
                Iterator it = merged_set.iterator();
                while( it.hasNext() )            
                {
                    String tr_key = (String)it.next();
                    //String ver_id = cfg.getUsageID(tr_key);
                    String ver_id =  cfg.getUsageVersionID(mgr.getLanguageCode(),tr_key);
                    version_ids_buf.append("'"+ver_id+"',");                        
                    //String disp_name= cfg.getDicValue(tr_key);
                    String disp_name=cfg.getTranslation(mgr.getLanguageCode(),tr_key);
                    if (code_part_labels!=null && code_part_labels.containsKey(tr_key))
                    {
                      if (DEBUG) debug("Replacing display name '"+disp_name+"' with '"+(String)code_part_labels.get(tr_key) +"'");  
                      disp_name = (String)code_part_labels.get(tr_key);
                    }
                    temp_table.put(ver_id,disp_name);
                    if (!temp_set.add(disp_name) && !duplicates.contains(disp_name)){ 
                        if (temp_set2.add(ver_id))
                     duplicates.add(disp_name); 
                }                
                    temp_set2.add(ver_id);    
                }                
                
                temp_set = null;
                temp_set2 = null;
                
                String version_ids = "(" + version_ids_buf.substring(0,version_ids_buf.length()-1) + ")";

                //send version_id's to database and get full_name and basic_text
                ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
                
                String temp_sql_stmt = SELECT_STMT + version_ids + " AND lang_code = '"+ mgr.getLanguageCode() +"' ";
                        
                ASPQuery q = trans.addQuery("GET_USAGE",temp_sql_stmt);
                q.setBufferSize(merged_set.size());
                trans = mgr.perform(trans);

                ASPBuffer buf = trans.getBuffer("GET_USAGE");
                int term_count=0;
                try{
                     if (buf!=null) 
                    term_count= Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
                }
                catch(Exception e)
                {
                    if (DEBUG) debug("Help.run() - Database does not contain any term definitions for terms referenced by this page");
                }                
                
                if (DEBUG)
                {
                  debug("Help.run() - No of terms referenced from the  page :"+ merged_set.size()); 
                  debug("Help.run() - No of term definitions fetched from DB:"+ term_count); 
                }
                
                Rtf2Html r2h = new Rtf2Html();  //converts RTF basic text to HTML
                
                //term_fields = new TreeSet(); 
                term_fields = new ArrayList();                 
                
                for (int i=0; i<term_count; i++)
                {
                  ASPBuffer b = buf.getBufferAt(i);
                  String version_handling_id= b.getValue("VERSION_HANDLING_ID");
                  String full_name= b.getValue("FULL_NAME");                  
                  String basic_text= b.getValue("BASIC_TEXT");
                  //String display_name=cfg.getTermDisplayName(lang.toUpperCase()+"_"+version_handling_id); //get display name from translation xml file
                  String display_name = (String)temp_table.get(version_handling_id);
                  if (duplicates.contains(display_name))
                      display_name=display_name+" ("+full_name+")";                  
                  
                  if (basic_text != null)
                      basic_text = r2h.convertRTFStringToHTML(basic_text);
                  else
                  {
                      if (DEBUG)
                      {
                          Util.debug("Error Occured in record:"+i+", BASIC TEXT is null");
                          Util.debug("Error in: Usage Id:"+version_handling_id+" display name:"+display_name);
                      }
                  }
                      
                  term_fields.add(new TermField(version_handling_id,display_name,basic_text));                  
                }
                 
            }          
            
            helpurl = mgr.replace(url,root,"") + ".htm";
            helpurl = mgr.replace(helpurl,"webmobile/","");
            if(helpurl.indexOf("/")<0)
               helpurl = helpbase+"/"+lang+"/general/"+helpurl;
            else
               helpurl = helpbase+"/"+lang+"/"+helpurl;

            //replace common/scripts with fndweb
            if(helpurl.indexOf("common/mobilescripts")>-1)
                helpurl = mgr.replace(helpurl,"common/mobilescripts","fndweb");
            
            helpurl = mgr.replace(helpurl,".page.htm",".htm");        
      }        
      else //Help on this page
      {
         if (url.toLowerCase().equals(mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL",mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Default.page").toLowerCase()))
            // General Portal Help
            helpurl = helpbase+"/"+lang+"/general/SystemNavigationPortal.htm";
         else
         {  // ASP page
            helpurl = mgr.replace(url,root,"") + ".htm";
            helpurl = mgr.replace(helpurl,"webmobile/","");
            if(helpurl.indexOf("/")<0)
               helpurl = helpbase+"/"+lang+"/general/"+helpurl;
            else
               helpurl = helpbase+"/"+lang+"/"+helpurl;
            
            //replace common/scripts with fndweb
            if(helpurl.indexOf("common/mobilescripts")>-1)
                helpurl = mgr.replace(helpurl,"common/mobilescripts","fndweb");            

            helpurl = mgr.replace(helpurl,".page.htm",".htm");
         }
            
         mgr.redirectTo(helpurl);
      }
    
   }

   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry = null;
      qry=trans.addQuery("MASTER", "SELECT VALUE FROM FND_SETTING WHERE PARAMETER = 'URL_HELP_BASE'");
      trans= mgr.performConfig(trans);
      helpbase_fromdb = trans.getValue("MASTER/DATA/VALUE");      
      disableHelp();    
      
      String lang_code = mgr.getLanguageCode();
      //create collator object once for each langugae (i.e. preDefine() runs once for each language)
      if (!Str.isEmpty(lang_code))         
         collator = Collator.getInstance(new Locale(lang_code, "", ""));      
      else               
         collator = Collator.getInstance();      
   }
   
   protected String getDescription()
   {
      return "FNDPAGESHELPDONFIELDSESCRIPTION: Help on Fields";
   }

   protected String getTitle()
   {
      ASPManager mgr = getASPManager(); 
      String title   = mgr.getQueryStringValue("HEADTITLE");      
      return mgr.translate("FNDPAGESHELPONFIELDSTITLE: Fields on &1",title);
   }

   protected void printContents() throws FndException
   {       
      ASPManager mgr = getASPManager(); 
      beginDataPresentation();
      appendToMobileHTML("<br>\n");      
      appendToMobileHTML("<p class=\"help\">"+mgr.translate("FNDPAGESSHOWONLINEDOCFORTHISPAGE: Show the IFS Online Documentation for &1this page&2","<a href="+helpurl+">","</a>"));
      appendToMobileHTML("</p>\n");
      
      //check term_fields and generate html 
      if (term_fields !=null && !term_fields.isEmpty())
      {         
         StringBuffer navcombo=new StringBuffer(); //contains html for fields combo box only
         StringBuffer bodytext=new StringBuffer(); //contains all html for the help page
          
         navcombo.append("<p class=\"help\">"+mgr.translate("FNDPAGESHELPGOTOFIELDDESC: Go to the field description for:")+"&nbsp<select class=\"selectbox \" name=\"navcombo\" onChange=\"goToLoc()\">\n");              
         int i = 0;
         Collections.sort(term_fields);
         Iterator it = term_fields.iterator();          
         while (it.hasNext())
         {
            TermField tf = (TermField)it.next();
            navcombo.append("<option value=\"#field_"+i+"\">"+tf.getLabel()+"</option>\n");                 
            bodytext.append("<p class=\"help\"><a id=field_"+i+"><b>"+tf.getLabel()+":</b></a>\n");
            String desc = tf.getBasicText();
            bodytext.append(((mgr.isEmpty(desc))?"":desc)+"</p>\n");
            i++;
         }          
         navcombo.append("</select></p>\n");
         bodytext.insert(0,navcombo.toString());
          
         appendToMobileHTML(bodytext.toString());
         appendDirtyJavaScript("function goToLoc()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("\t nvcombo = document.forms[0].navcombo;\n");
         appendDirtyJavaScript("\tdestination = nvcombo.options[nvcombo.selectedIndex].value;\n");
         appendDirtyJavaScript("\t\tif (destination) location.href = destination;\n");
         appendDirtyJavaScript("}\n");
      }
      else
      {
        mgr.redirectTo(helpurl);
//        appendToMobileHTML("<br>\n");      
//        appendToMobileHTML("<p class=\"help\">"+mgr.translate("FNDPAGESHELPNOTERMDESCS: No term descriptions were found for this page."));
//        appendToMobileHTML("</p>\n");
      }
      endDataPresentation(false);
   }
   
  
   /*
    * Inner class that represents a Term Field. 
    *
    * version_id = a unique? id for each term definition
    * label = display name (+full_name from DB if display name has duplicates)
    * basic text = rtf text fetched from DB and converted to HTML
    */
   private class TermField implements Comparable{
       private String version_id;
       private String label;       
       private String basic_text;           
            
       TermField(String version_id, String label, String basic_text) 
       {
           this.version_id = version_id;
           this.label = label;           
           this.basic_text = basic_text;           
       }
       
       String getVersionId()
       {
           return version_id;
       }
       
       String getLabel()
       {
           return label;
       }       
              
       String getBasicText()
       {
           return basic_text;
       }
       
       public int compareTo(Object o) {
          TermField tf = (TermField)o;
          if ( (DEBUG) && collator.compare(this.label,tf.getLabel()) == 0 )
          {               
            debug("----Warning: Duplictate term definition found---");
            debug(tf.toString());
            debug(this.toString());
          }
          return collator.compare(this.label,tf.getLabel());
       }   
       
       public String toString()
       {
          String obj  = "version_id ="+version_id +"\n";
                 obj += "label      ="+label +"\n";
                 obj += "basic_text ="+basic_text +"\n";
          return obj;       
       }
       
   }
}

