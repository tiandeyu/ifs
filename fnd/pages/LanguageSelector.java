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
 * File        : LanguageSelector.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2004-05-26 - Created
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/02/03 sumelk Bug 88795, Replaced lang_table Hashtable with a Hashmap in printContents().
 * 2007/05/24 sadhlk Bug 65373, Modified preDefine().
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2006/09/13 gegulk 
 * Bug id 60473, Modified the method printContents() to align properly in RTL mode
 * 2006/08/18 gegulk 
 * Bug id 59985, Removed the usages of the word "enum" as variable names
 *
 * 2006/06/23 rahelk
 * bug id 58990 fixed. Changed to PersistentCookie
 * ----------------------------------------------------------------------------
 */


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import java.util.*;


public class LanguageSelector extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.LanguageSelector");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   
   private ASPBlock blk;
   private ASPContext ctx;   
   private String redirect_to;
   
   public LanguageSelector(ASPManager mgr, String page_path)
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
                       " writePersistentCookie('__USER_LANGUAGE',lang_code);\n"+
                       " writeGeneralPersistentCookie('__USER_LANGUAGE',lang_code);\n" +
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
      appendToHTML("<table class=pageFormWithBorder width=100%>");
      while (it.hasNext())     
      {
         if(mgr.isRTL())
         {
            appendToHTML("<tr><td align=right>");
            String key = (String)it.next();
            
            if (key.equals(lang_code))
               printText(" ["+mgr.translate("FNDLNGSELECTCURR: current")+"]");         
            
            printLink(""+lang_table.get(key), "javascript:setUserLanguage('"+key.toLowerCase()+"')");         
         }
         else
         {
         appendToHTML("<tr><td align=left>");
         String key = (String)it.next();
         printLink(""+lang_table.get(key), "javascript:setUserLanguage('"+key.toLowerCase()+"')");
         
         if (key.equals(lang_code))
            printText(" ["+mgr.translate("FNDLNGSELECTCURR: current")+"]");
         }
         appendToHTML("<br></td></tr>");
      }
      appendToHTML("</table>");
      endDataPresentation(false);
      printHiddenField("LANG_CODE", "");
   }
}
