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
*  File        : WebClientAdmin.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-04  - Created Using the ASP file WebkitAdmin.asp
*    Chaminda O     2001-03-24  - Modifications after converting to java
*    Jacek P        2001-03-22  - Added possibility to control debug flag in DLL.
*    Mangala P      2003-03-16  - Removed use of depricated method getConfigFilePath
*    Sampath W      2003-06-04  - changed the title from 'webkit' to 'ifs webclient'.
*    Rifki R        2003-08-28  - Removed 'MTS' in displayed messages.
*    Jacek P        2003-12-17  - Renamed to WebClientAdmin. Added more debugging
*                                 possibilities controlled by query string.
*    Rifki R        2004-06-29  - Check for PO security in run()
*    Suneth M       2004-Aug-04 - Changed duplicate localization tags.
* ----------------------------------------------------------------------------
* New Comments:
* 2010/02/03 sumelk Bug 88795, Replaced lang_table Hashtable with a Hashmap in printContents().
* 2008/04/07 sadhlk Bug id 72387, Removed security check in run()as it is handled for all
*                                 pages in ASPPageProvider class.
* 2007/04/06 mapelk Removed remaining image generation code
*
* Revision 1.10 2007/02/05 buhilk
* Bug Id: 63250, Removed Re-Generate Images button from admin page.
*
* Revision 1.9  2006/12/15 sadhlk
* Changed name of common messages to Broadcase Messages.
*
* Revision 1.8  2006/12/15 sadhlk
* Bug id 62442, Added button to extract navigator for Presentation Object security.
*
* Revision 1.7  2006/12/14 buhilk
* Bug id 61535, Added button and relevent functionality to refresh common message cache.
*
* Revision 1.6  2006/09/29 gegulk
* Changed the page to a tab page and added a new tab for loading translations
*
* Revision 1.5  2005/11/17 14:36:43  japase
* Added runGC()
*
* Revision 1.4  2005/11/15 13:59:57  mapelk
* Moved "Regenerate Images" in to the debug section with some note.
*
* Revision 1.3  2005/11/06 05:46:57  mapelk
* Introduced posibility to trace Dynamic Object cache from WebClientAdmin
*
* Revision 1.2  2005/10/19 10:16:34  mapelk
* Added functionality to refresh profile cache in certain time. Also posibility to flush the cache on demand.
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.2  2005/02/03 03:49:03  sumelk
* Added possibility to reload the profile cache.
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.servlets.*;

import java.util.*;

public class WebClientAdmin extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public  static boolean DEBUG        = Util.isDebugEnabled("ifs.fnd.pages.WebClientAdmin");
   private static boolean debug_native = false;

   private static final int GENERAL_TAB       = 1;
   private static final int TRANSLATIONS_TAB     = 2;  
   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter hmf;

   private ASPBlockLayout blockLayout; 
   private ASPBlock blk;
   private ASPCommandBar bar;
   private ASPTabContainer tabs;    
   
   private ASPBlock trnblk;
   private ASPRowSet trnset;
   private ASPCommandBar trnbar;
   private ASPTable trntbl;
   private ASPBlockLayout trnlay;
   
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String  status_text;
   private String  name;
   private boolean debug_mode;

   //===============================================================
   // Construction
   //===============================================================
   public WebClientAdmin(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void activateGENERAL_TAB()
   {
      tabs.setActiveTab(GENERAL_TAB);
   }

   public void activateTRANSLATIONS_TAB()
   {
      tabs.setActiveTab(TRANSLATIONS_TAB);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      status_text   = null;
      name   = null;
      debug_mode = false;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      WebClientAdmin page = (WebClientAdmin)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.status_text   = null;

      // Cloning immutable attributes
      page.hmf = page.getASPHTMLFormatter();
      page.name   = null;
      page.debug_mode = false;

      return page;
   }

   protected void preDefine() throws FndException
   {
      init();
      
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");    
      bar = mgr.newASPCommandBar(blk);
      bar.disableMinimize();
      
      bar.addCustomCommand("activateGENERAL_TAB","GENERAL_TAB");
      bar.addCustomCommand("activateTRANSLATIONS_TAB","TRANSLATIONS_TAB");
          
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
     
      tabs.addTab("FNDPAGESWEBCLIENTCONFIGGENERAL: General","javascript:commandSet('MAIN.activateGENERAL_TAB','')");
      tabs.addTab("FNDPAGESWEBCLIENTCONFIGTRANSLATIONS: Translations","javascript:commandSet('MAIN.activateTRANSLATIONS_TAB','')");
      
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

      bar.removeCustomCommand("activateGENERAL_TAB");
      bar.removeCustomCommand("activateTRANSLATIONS_TAB");
      
      trnblk = mgr.newASPBlock("TRANSLATIONS_BLK");

      trnblk.addField("TRANSLATIONS").
            setHidden();

      trnblk.addField("LANGUAGE").
          setLabel("FNDPAGESWEBCLIENTCONFIGLANGUAGE: Language").
          setSize(5).
          setReadOnly();

      trnblk.addField("DESCRIPTION").
          setLabel("FNDPAGESWEBCLIENTCONFIGDESCRIPTION: Description").
          setSize(50).
          setReadOnly();
      
      trnblk.addField("FETCHED").
          setLabel("FNDPAGESWEBCLIENTCONFIGFETCHED: Translations Fetched").
          setCheckBox("FALSE,TRUE").
          setReadOnly();
      
    
      trnlay = trnblk.getASPBlockLayout();
      trnlay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      trnset = trnblk.getASPRowSet();     
                    
      trnbar = mgr.newASPCommandBar(trnblk);
      trnbar.disableMinimize();
      trnbar.disableCommand(ASPCommandBar.FIND);
      trnbar.disableCommand(ASPCommandBar.PROPERTIES);

      trntbl = mgr.newASPTable( trnblk );
                 
      trntbl.disableQueryRow();
      trntbl.disableRowStatus();
      trntbl.enableRowSelect();
      trntbl.setEditable();
      
      trntbl.disableRowCounter();
      
      setProgressBarEnabled();
      
      
   }

   protected void init() throws FndException
   {
      try
      {
         debug_native = ifs.fnd.os.NativeUtilities.isNativeDebugEnabled();
      }
      catch (Throwable any)
      {
         debug_native = false;
      }
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = getASPContext();
      trans = mgr.newASPTransactionBuffer();
      hmf  = mgr.newASPHTMLFormatter();

      if (!mgr.isEmpty(mgr.readValue("REFRESHLANGUAGE")))
         refreshLanguage();
      
     // if (!isObjectAccessible("FND/WebClientAdmin.page"))
     //   error(new FndException("FNDPAGESWEBADMINNOSEC: You do not have sufficient privileges to use this page"));

      status_text = "";

      debug_mode = ctx.readFlag("DEBUG", false);
      if(!debug_mode)
      {
         String debug = mgr.readValue("DEBUG");
         if( !mgr.isEmpty(debug) && debug.toUpperCase().startsWith("Y") )
            debug_mode = true;
         else
            debug_mode = false;
      }
      if( mgr.commandBarActivated() )       
         eval(mgr.commandBarFunction());       
      if (!mgr.isEmpty(mgr.readValue("PERFCONF")))
         clearConf();
      else if (!mgr.isEmpty(mgr.readValue("PERFMTS")))
         clearMTS();
      else if (!mgr.isEmpty(mgr.readValue("PERFPROF")))
         clearProfileCache();
      else if (!mgr.isEmpty(mgr.readValue("DEBUGNATIVE")))
      {
         debug_native = !debug_native;
         ifs.fnd.os.NativeUtilities.enableNativeDebug(debug_native);
      }
      else if (!mgr.isEmpty(mgr.readValue("ENABLETRC")))
         enableDisableTrace();
      else if (!mgr.isEmpty(mgr.readValue("ENABLESTAT")))
         enableDisableStatistics();
      else if (!mgr.isEmpty(mgr.readValue("SPOOLSTAT")))
         spoolStatistics();
      else if (!mgr.isEmpty(mgr.readValue("LOCKPAGES")))
         autolockPages();
      else if (!mgr.isEmpty(mgr.readValue("DISABLEPOOL")))
         disablePagePool();
      else if (!mgr.isEmpty(mgr.readValue("SHOWPOOL")))
         showPagePool();
      else if (!mgr.isEmpty(mgr.readValue("CLEARPOOL")))
         clearPagePool();
      else if (!mgr.isEmpty(mgr.readValue("ALERTPRIO")))
         setAlertPrio();
      else if (!mgr.isEmpty(mgr.readValue("ALERTSLEEP")))
         setAlertSleepTime();
      else if (!mgr.isEmpty(mgr.readValue("PERFPROFDISPLAY")))
         mgr.displayProfileCache();
      //else if (mgr.buttonPressed("LOCFILES"))
      //   getLocalizationSizes();   
      else if (mgr.buttonPressed("SPOOLSESSTAT"))
         status_text = RequestHandler.spoolSessionStatistics();
      else if (mgr.buttonPressed("DYNACACHEDISPLAY"))
         mgr.dispalyDynamicCache();
      else if (mgr.buttonPressed("RUNGC"))
         status_text = mgr.runGC();
      else if (mgr.buttonPressed("PERFCOMMSG"))
         clearCommonMsgCache();

      ctx.writeFlag("DEBUG", debug_mode);
   }

   //=============================================================================
   //  Functions
   //=============================================================================

   public void  clearConf()
   {
      ASPManager mgr = getASPManager();

      mgr.reloadConfig();
      try
      {
        name = (new java.io.File(mgr.getASPConfig().convertPath(mgr.getASPConfigFileDir(),null))).getCanonicalPath();
      }
      catch(Exception any)
      { name = "";}
      status_text= mgr.translate ("FNDPAGESWCADMINCFGFNAME: The Configuration files at &1 have been reloaded, the context cache cleared and page pool emptied", name);
   }


   public void  clearMTS()
   {
      ASPManager mgr = getASPManager();

      mgr.clearMTSSecurityCache();
      status_text=mgr.translate("FNDPAGESWCADMINMTSCASHFL: Security Cache has been flushed.");
   }

   public void  clearProfileCache()
   {
      ASPManager mgr = getASPManager();

      mgr.reloadProfileCache();
      status_text=mgr.translate("FNDPAGESWEBKITADMINPROFILECASHFL: Profile Cache has been flushed.");
   }

   public void enableDisableTrace()
   {
      ASPLog log = getASPLog();

      log.enableTrace(!log.isTraceOn());
   }


   public void enableDisableStatistics()
   {
      if(TraceEvent.statisticsEnabled)
         TraceEvent.statisticsEnabled = false;
      else
         TraceEvent.statisticsEnabled = true;
   }


   public void spoolStatistics()
   {
      ASPManager mgr = getASPManager();
      mgr.spoolTraceStatistics();
      mgr.clearTraceStatistics();
   }


   public void autolockPages()
   {
      ASPManager mgr = getASPManager();
      if( mgr.isPagePoolAutolockOn() )
      {
         mgr.setPagePoolAutolock(false);
         mgr.clearPagePool();
      }
      else
         mgr.setPagePoolAutolock(true);
   }

   public void disablePagePool()
   {
      ASPManager mgr = getASPManager();
      if( mgr.isPagePoolDisabled() )
         mgr.disablePagePool(false);
      else
         mgr.disablePagePool(true);
      mgr.clearPagePool();
   }

   public void showPagePool()
   {
      ASPManager mgr = getASPManager();
      mgr.showPagePoolContents();
   }

   public void clearPagePool()
   {
      ASPManager mgr = getASPManager();
      mgr.clearPagePoolContents();
   }

   public void setAlertPrio()
   {
      ASPManager mgr = getASPManager();
      try
      {
         int newprio = Integer.parseInt( mgr.readValue("ALERTPRIOVAL") );
         Alert.setThreadPriority(newprio);
      }
      catch(Throwable any)
      {
      }
   }

   public void setAlertSleepTime()
   {
      ASPManager mgr = getASPManager();
      try
      {
         int sec = Integer.parseInt( mgr.readValue("ALERTSLEEPTIME") );
         Alert.setSleepTime(sec);
      }
      catch(Throwable any)
      {
      }
   }

   private void getLocalizationSizes()
   {
      ASPManager mgr = getASPManager();
      int[] sizes = mgr.getLocalizationSizes();

      status_text =
      "Size of dictionary table: "+((int)(sizes[0]/1024)) + " kB\n"+
      "Size of term usage table: "+((int)(sizes[1]/1024)) + " kB\n"+
      "Size of term display table: "+((int)(sizes[2]/1024)) + " kB";
   }
   
   /*
    * Clears all the common message cache by resetting the message timeout
    * thus forcing all active messages from the database to be read.
    */
   private void clearCommonMsgCache()
   {
      IFSCommonMessages.resetTimeout();
      status_text=getASPManager().translate("FNDPAGESWCADMINMSGCACHEFLSD: Broadcast Message Cache has been flushed.");
   }

   //===============================================================
   //  HTML
   //===============================================================

   protected String getDescription()
   {
      return "FNDPAGESWCADMINWKA: IFS WebClient Administration";
   }

   protected String getTitle()
   {
      return "FNDPAGESWCADMINWKA: IFS WebClient Administration";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      String loc = "";
      
      appendToHTML(tabs.showTabsInit());      
      drawSimpleCommandBar("");
      
      if (tabs.getActiveTab() == GENERAL_TAB)
      {
      appendToHTML("<table border=0>\n");
      appendToHTML("<tr><th colspan=2>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINWEAD: IFS WebClient Administration"));
      appendToHTML("</th></tr>\n");

      appendToHTML("<tr><td>");
      appendToHTML(hmf.drawSubmit("PERFCONF","FNDPAGESWCADMINWEADP: Execute",""));
      appendToHTML("</td><td>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINWEADCONF: Reload Configuration file"));
      appendToHTML("</td></tr>\n");

      appendToHTML("<tr><td>");
      appendToHTML(hmf.drawSubmit("PERFMTS","FNDPAGESWCADMINWEADP: Execute",""));
      appendToHTML("</td><td>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINWEADMTS: Clear Security Cache"));
      appendToHTML("</td></tr>\n");

      appendToHTML("<tr><td>");
      appendToHTML(hmf.drawSubmit("PERFPROF","FNDPAGESWEBKITADMINWEADP: Execute",""));
      appendToHTML("</td><td>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINWEADPROFILE: Clear Profile Cache"));
      appendToHTML("</td></tr>\n");

      appendToHTML("<tr><td>");
      appendToHTML(hmf.drawSubmit("PERFCOMMSG","FNDPAGESWEBKITADMINWEADP: Execute",""));
      appendToHTML("</td><td>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINCLEARBRDMSGCACHE: Clear Broadcast Message Cache"));
      appendToHTML("</td></tr>\n");
      
      loc = mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR");
      loc = loc.substring(0,loc.indexOf("?"));
      loc = loc+"?MAINMENU=TOXML";
    
      appendToHTML("<tr><td>");
      appendToHTML(hmf.drawButton("PERFNAV","FNDPAGESWEBKITADMINNAVEXT: Execute","Onclick=\"parent.location.href ='"+loc+ "'\""));
      appendToHTML("</td><td>");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINNAVEXTRACT: Extract Navigator for presentation Object security"));
      appendToHTML("</td></tr>\n");

      if(debug_mode)
      {
         appendToHTML("<tr><td>");
         printSpaces(1);
         appendToHTML("</td><td>");
         printSpaces(1);
         appendToHTML("</td><td>");

         appendToHTML("<tr><td>");
         printSpaces(1);
         appendToHTML("</td><td>");
         printReadLabel("Page Pool debug status:");
         printNewLine();
         printText("Pool is "+( mgr.isPagePoolDisabled() ? "DISABLED" : "ENABLED" )+
                   ", Autolock is "+( mgr.isPagePoolAutolockOn() ? "ON" : "OFF" )+" (Defaults: ENABLED, OFF)");
         appendToHTML("</td><td>");

         ASPLog log = getASPLog();

         appendToHTML("<tr><td>");
         printSpaces(1);
         appendToHTML("</td><td>");
         printReadLabel("Other debug/trace status:");
         printNewLine();
         printText("Trace is "+( log.isTraceOn() ? "ON" : "OFF" )+
                   ", Trace statistics is "+( TraceEvent.statisticsEnabled ? "ON" : "OFF" )+" (Defaults: OFF, OFF)");
         appendToHTML("</td><td>");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("DEBUGNATIVE","FNDPAGESWCADMINDEBUGNATIVEEX: Execute",""));
         appendToHTML("</td><td>");
         if (debug_native)
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINDEBUGNATIVEDIS: Disable debugging of Native code"));
         else
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINDEBUGNATIVEENA: Enable debugging of Native code"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("LOCKPAGES","FNDPAGESWCADMINLOCKPOOLB: Execute",""));
         appendToHTML("</td><td>");
         if( mgr.isPagePoolAutolockOn() )
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINRELAUTOCLEPOOL: Release Autolock and clear Page Pool"));
         else
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINAUTOLOCKPPOOL: Autolock pages in the pool"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("DISABLEPOOL","FNDPAGESWCADMINDISPOOL: Execute",""));
         appendToHTML("</td><td>");
         if( mgr.isPagePoolDisabled() )
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINEPOOL: Enable Page Pool"));
         else
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINDPOOL: Disable Page Pool"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("SHOWPOOL","FNDPAGESWCADMINSHOWPOOLB: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINSHOWPOOL: Show contents of Page Pool"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("CLEARPOOL","FNDPAGESWCADMINCLEARPOOLB: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINCLEARPOOL: Clear contents of Page Pool"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("ENABLETRC","FNDPAGESWCADMINTRC: Execute",""));
         appendToHTML("</td><td>");
         if(log.isTraceOn())
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINTRACEOFF: Disable trace"));
         else
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINTRACEON: Enable trace"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("ENABLESTAT","FNDPAGESWCADMINWEADP: Execute",""));
         appendToHTML("</td><td>");
         if(TraceEvent.statisticsEnabled)
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINDSTATISTICS: Disable trace statistics"));
         else
            appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINESTATISTICS: Enable trace statistics"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("SPOOLSTAT","FNDPAGESWCADMINWEADP: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINSPOOLSTAT: Spool trace statistics to DBMON"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("ALERTPRIO","FNDPAGESWCADMINALERTP: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINALERTPR: Set prio of Alert thread to: "));
         appendToHTML(hmf.drawTextField("ALERTPRIOVAL", "", null, 5) );
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("ALERTSLEEP","FNDPAGESWCADMINALERTS: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINALERTSL: Set number of seconds Alert thread should wait between iteractions: "));
         appendToHTML(hmf.drawTextField("ALERTSLEEPTIME", "", null, 5) );
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("PERFPROFDISPLAY","FNDPAGESWEBKITADMINWEADP: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINWEADPROFILE11: Display Profile Cache"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("DYNACACHEDISPLAY","FNDPAGESWEBKITADMINWEADP: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINDYNACACHE: Display Dynamic Cache"));
         appendToHTML("</td></tr>\n");

         //appendToHTML("<tr><td>");
         //appendToHTML(hmf.drawSubmit("LOCFILES","FNDPAGESWEBKITADMINLOCFILES: Execute",""));
         //appendToHTML("</td><td>");
         //appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINLOCFILESTXT: Show size of Localize tables"));
         //appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("SPOOLSESSTAT","FNDPAGESWEBKITADMINSPOOLSESSTAT: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINSPOOLSESSTATTXT: Show session and profile sizes"));
         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td>");
         appendToHTML(hmf.drawSubmit("RUNGC","FNDPAGESWEBKITADMINRUNGCEX: Execute",""));
         appendToHTML("</td><td>");
         appendToHTML(hmf.drawWriteLabel("FNDPAGESWEBKITADMINRUNGC: Run Garbage Collector"));
         appendToHTML("</td></tr>\n");
      }

      appendToHTML("</table><br>\n");
      appendToHTML(hmf.drawWriteLabel("FNDPAGESWCADMINWEADSTATUS: Status Monitor:"));
      appendToHTML("&nbsp;");
      appendToHTML(hmf.drawTextArea("STATUS",status_text,"",5,100));
      }
      else if(tabs.getActiveTab() == TRANSLATIONS_TAB)
      {
         appendDirtyJavaScript("var modules = new Array();\n");
         HashSet module_list = mgr.getModuleList();
         Iterator it = module_list.iterator(); 
         String where_txt = "";
         int i=2;
         while (it.hasNext())
         {  
            appendDirtyJavaScript("modules["+i+"]='"+((String)it.next())+"';\n");
            i++;
         }
         
         appendDirtyJavaScript("modules[0]='CLEAR';\n");
         appendDirtyJavaScript("modules[1]='BULK';\n");

         appendDirtyJavaScript("function refreshLanguages(__SELECTED1,LANGUAGE)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   var qry_string_arr = new Array();\n");
         appendDirtyJavaScript("   var prog_text_arr = new Array();\n");
         appendDirtyJavaScript("   if(__SELECTED1.type)\n");
         appendDirtyJavaScript("   {\n");         
         appendDirtyJavaScript("      if(__SELECTED1.checked)\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         qry_string_arr = getQryStringArray(LANGUAGE[1].value,qry_string_arr)\n");
         appendDirtyJavaScript("         prog_text_arr = getProgTextArray(LANGUAGE[1].value,prog_text_arr)\n");
         appendDirtyJavaScript("      }\n");          
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");             
         appendDirtyJavaScript("      for(i=0;i<__SELECTED1.length;i++)\n");
         appendDirtyJavaScript("      {\n");         
         appendDirtyJavaScript("         if(__SELECTED1[i].checked)\n");
         appendDirtyJavaScript("         {\n");         
         appendDirtyJavaScript("            qry_string_arr = getQryStringArray(LANGUAGE[i+1].value,qry_string_arr);\n");                  
         appendDirtyJavaScript("            prog_text_arr = getProgTextArray(LANGUAGE[i+1].value,prog_text_arr);\n");                  
         appendDirtyJavaScript("         }\n");                            
         appendDirtyJavaScript("      }\n");                           
         appendDirtyJavaScript("   }\n");    
         appendDirtyJavaScript("   var url = APP_ROOT+ 'common/scripts/WebClientAdmin.page'\n");   
         appendDirtyJavaScript("   showProgressBar(url,qry_string_arr,prog_text_arr,\"clearPagePool();commandSet('MAIN.activateTRANSLATIONS_TAB','')\");\n");
         appendDirtyJavaScript("   deselectAllRows('__SELECTED1');\n");          
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("function clearPagePool()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   __connect(APP_ROOT+ 'common/scripts/WebClientAdmin.page'+'?CLEARPOOL=Y');\n");
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("function getQryStringArray(language,qry_string_arr)\n");
         appendDirtyJavaScript("{\n");                           
         appendDirtyJavaScript("   for(j=qry_string_arr.length,k=0;k<modules.length;k++,j++)\n");
         appendDirtyJavaScript("   {\n");      
         appendDirtyJavaScript("      qry_string_arr[j]='?REFRESHLANGUAGE='+language+'&MODULE='+modules[k];\n");         
         appendDirtyJavaScript("   }\n"); 
         appendDirtyJavaScript("   return qry_string_arr;\n");          
         appendDirtyJavaScript("}\n");
         
         String desc = mgr.translate("FNDPAGESWEBCLIENTCONFIGFETCHINGTRANSLATIONS: Fetching Translations...");
         String lang = mgr.translate("FNDPAGESWEBCLIENTCONFIGPROGRESSLANGUAGE: Language:");
         String mdl = mgr.translate("FNDPAGESWEBCLIENTCONFIGPROGRESSMODULE: Module:");
         
         appendDirtyJavaScript("function getProgTextArray(language,prog_text_arr)\n");
         appendDirtyJavaScript("{\n");                           
         appendDirtyJavaScript("   for(m=prog_text_arr.length,n=0;n<modules.length;n++,m++)\n");
         appendDirtyJavaScript("   {\n");      
         appendDirtyJavaScript("      var module_name = ('BULK'==modules[n])?'General':modules[n];\n"); 
         appendDirtyJavaScript("      prog_text_arr[m]='"+desc+";"+lang+" '+language+';"+mdl+" '+module_name;\n");         
         appendDirtyJavaScript("   }\n"); 
         appendDirtyJavaScript("   return prog_text_arr;\n");          
         appendDirtyJavaScript("}\n");         

         appendDirtyJavaScript("function clearLanguages(__SELECTED1,LANGUAGE)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if(__SELECTED1.type)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      if(__SELECTED1.checked)\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         __connect(APP_ROOT+ 'common/scripts/WebClientAdmin.page'+'?REFRESHLANGUAGE='+LANGUAGE[1].value+'&MODULE=CLEAR');\n");         
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");         
         appendDirtyJavaScript("      for(i=0;i<__SELECTED1.length;i++)\n");
         appendDirtyJavaScript("      {\n");               
         appendDirtyJavaScript("         if(__SELECTED1[i].checked)\n");
         appendDirtyJavaScript("         {\n");
         appendDirtyJavaScript("               __connect(APP_ROOT+ 'common/scripts/WebClientAdmin.page'+'?REFRESHLANGUAGE='+LANGUAGE[i+1].value+'&MODULE=CLEAR');\n");                  
         appendDirtyJavaScript("         }\n");          
         appendDirtyJavaScript("      }\n");        
         appendDirtyJavaScript("   }\n");          
         appendDirtyJavaScript("   deselectAllRows('__SELECTED1');\n"); 
         appendDirtyJavaScript("   clearPagePool();\n"); 
         appendDirtyJavaScript("   commandSet('MAIN.activateTRANSLATIONS_TAB','');");
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("function debugTranslations(__SELECTED1,LANGUAGE)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if(__SELECTED1.type)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      if(__SELECTED1.checked)\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         __connect(APP_ROOT+ 'common/scripts/WebClientAdmin.page'+'?REFRESHLANGUAGE='+LANGUAGE[1].value+'&DEBUGTRN=Y');\n");         
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");         
         appendDirtyJavaScript("      for(i=0;i<__SELECTED1.length;i++)\n");
         appendDirtyJavaScript("      {\n");               
         appendDirtyJavaScript("         if(__SELECTED1[i].checked)\n");
         appendDirtyJavaScript("         {\n");
         appendDirtyJavaScript("               __connect(APP_ROOT+ 'common/scripts/WebClientAdmin.page'+'?REFRESHLANGUAGE='+LANGUAGE[i+1].value+'&DEBUGTRN=Y');\n");                  
         appendDirtyJavaScript("         }\n");          
         appendDirtyJavaScript("      }\n");        
         appendDirtyJavaScript("   }\n");          
         appendDirtyJavaScript("   deselectAllRows('__SELECTED1');\n"); 
         appendDirtyJavaScript("   commandSet('MAIN.activateTRANSLATIONS_TAB','');");
         appendDirtyJavaScript("}\n");  
         trnset.clear();
         ASPBuffer data = mgr.newASPBuffer();
         HashMap lang_table = ASPConfigFile.language_table;
         Iterator en = lang_table.keySet().iterator();
         int lang_count = 0;
         while (en.hasNext())     
         {  
            data.clear();
            String key = (String)en.next();
            data.addFieldItem("LANGUAGE",key);   
            data.addFieldItem("DESCRIPTION",(String)lang_table.get(key));
            data.addFieldItem("FETCHED",DictionaryCache.isAllTranslationsFetched(mgr,key)?"TRUE":"FALSE");
            trnset.addRow(data);
            lang_count++;            
         }
         appendDirtyJavaScript("var lang_count = "+lang_count+";");
    
         appendToHTML(trntbl.populate());
                     
         appendToHTML(hmf.drawButton("REFRESH",mgr.translate("FNDPAGESWEBCLIENTCONFIGREFRESHLANGUAGE: Refresh"),"onclick=\"javascript:refreshLanguages(__SELECTED1,LANGUAGE);\""));
         appendToHTML(hmf.drawButton("CLEAR",mgr.translate("FNDPAGESWEBCLIENTCONFIGCLEARLANGUAGE: Clear"),"onclick=\"javascript:clearLanguages(__SELECTED1,LANGUAGE);\""));
         if(debug_mode)
            appendToHTML(hmf.drawButton("DEBUG",mgr.translate("FNDPAGESWEBCLIENTCONFIGPRINTTODEBUG: Debug"),"onclick=\"javascript:debugTranslations(__SELECTED1,LANGUAGE);\""));

      }            
      appendToHTML(tabs.showTabsFinish());
   }
   private void refreshLanguage()
   {      
      ASPManager mgr = getASPManager();
      String module = mgr.readValue("MODULE");
      String lang_code = mgr.readValue("REFRESHLANGUAGE");
      String debugTranslations = mgr.readValue("DEBUGTRN");
      if (!mgr.isEmpty(debugTranslations))
         DictionaryCache.printTranslationsToDebug(lang_code);
      else
      {
         if("CLEAR".equalsIgnoreCase(module))
         {
            DictionaryCache.clearTranslations(lang_code.toUpperCase());         
         }
         else if("BULK".equalsIgnoreCase(module))
         {
            DictionaryCache.initLanguage(getASPManager(),lang_code.toLowerCase());         
         }
         else
         {
            DictionaryCache.initModule(getASPManager(),lang_code.toLowerCase(),module);
         }
      }
      mgr.responseWrite(lang_code+"^"+module+"^");
      mgr.endResponse();
      
   }
}
