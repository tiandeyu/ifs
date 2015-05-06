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
 * File        : MobileLanguageSelector.java
 * Description :
 * Notes       : Created by buhilk
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/02/03 sumelk Bug 88795, Replaced lang_table Hashtable with a Hashmap in printContents(). 
 * 2007-Dec-03 buhilk IID F1PR1472 - Improved miniFW functionality.
 *
 */


package ifs.fnd.webmobile.pages;

import ifs.fnd.asp.*;
import ifs.fnd.webmobile.web.MobilePageProvider;
import ifs.fnd.service.*;
import java.util.*;


public class MobileLanguageSelector extends MobilePageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.webmobile.pages.MobileLanguageSelector");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   
   private ASPBlock blk;
   private ASPContext ctx;   
   private String redirect_to;
   
   public MobileLanguageSelector(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      ASPManager mgr = getASPManager();
      ctx    = mgr.getASPContext();
      
      redirect_to = ctx.readValue("REDIRECT_TO_URL","");
      
      if (mgr.isEmpty(mgr.getUserPreferredLanguage()))
      {
         disableConfiguration();
         disableHomeIcon();
         disableNavigate();
         disableOptions();
      }

      if (!mgr.isEmpty(mgr.getQueryStringValue("CURRENT_URL")))
         redirect_to = mgr.getQueryStringValue("CURRENT_URL");
      else if ("SET_LANGUAGE".equals(mgr.readValue("__COMMAND")))
         setUserLanguage();
      
      ctx.writeValue("REDIRECT_TO_URL", redirect_to);
   }

   void setUserLanguage()
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("LANG", "fnd_user_api.set_property");
      cmd.addParameter("IDENTITY",mgr.getFndUser());
      cmd.addParameter("PROPERTY","PREFERRED_LANGUAGE");
      cmd.addParameter("LANG_CODE",mgr.readValue("LANG_CODE"));

      mgr.perform(trans);
      
      mgr.setUserPreferredLanguage(mgr.readValue("LANG_CODE"));
      
      if (!mgr.isEmpty(redirect_to))
         mgr.redirectTo(redirect_to);
      else
         mgr.redirectFrom();
   }
   

   protected void  preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
   
      blk = mgr.newASPBlock("MAIN");
      blk.addField("IDENTITY");
      blk.addField("PROPERTY");
      blk.addField("LANG_CODE");
      
      
      appendJavaScript("function setUserLanguage(lang_code){\n"+
                       " f.LANG_CODE.value=lang_code;\n"+                          
                       " f.__COMMAND.value='SET_LANGUAGE';submit();\n"+
                       "}\n");
   }
   
   protected String getDescription()
   {
      return "FNDLNGSELECTDESC: Change Language";
   }

   protected String getTitle()
   {
      return "FNDLNGSELECTDESC: Change Language";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      String lang_code = mgr.getLanguageCode().toUpperCase();
      HashMap lang_table = ASPConfigFile.language_table;
      Iterator it = lang_table.keySet().iterator();
      
      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate(getTitle()));
      appendToMobileHTML("<table class=\"pageFormWithBorder\" width=\"100%\">");
      while (it.hasNext())     
      {
         if(mgr.isRTL())
         {
            appendToMobileHTML("<tr><td align=right>");
            String key = (String)it.next();
            printSpaces(1);
            if (key.equals(lang_code))
               printText("["+mgr.translate("FNDLNGSELECTCURR: current")+"]");         
            
            printLink(""+lang_table.get(key), "javascript:setUserLanguage('"+key.toLowerCase()+"')");         
         }
         else
         {
         appendToMobileHTML("<tr><td align=left>");
         String key = (String)it.next();
         printLink(""+lang_table.get(key), "javascript:setUserLanguage('"+key.toLowerCase()+"')");
         
         if (key.equals(lang_code))
            printText(" ["+mgr.translate("FNDLNGSELECTCURR: current")+"]");
         }
         appendToMobileHTML("<br></td></tr>");
      }
      appendToMobileHTML("</table>");
      endDataPresentation(false);
      printHiddenField("LANG_CODE", "");
   }
}
