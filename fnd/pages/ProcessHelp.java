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
 * File        : ProcessHelp.java
 * Description : Validates the usage description for given USAGE_ID. This page is called 
 *               from "What's This?" functionality.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *
 *    2006/09/20 gegulk Bug 60579, Modified the SQL statement to enable multi language "Whats this"
 *    2006/07/03 buhilk Bug 58216, Fixed SQL Injection threats
 *
 *    Geethika  2006-May-11 - Created
 */
package ifs.fnd.pages;

import ifs.fnd.asp.*;

public class ProcessHelp extends ASPPageProvider
{
   public ProcessHelp (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      
      String usagekey   = mgr.getQueryStringValue("USAGEID");
      String SELECT_STMT = "SELECT NVL(translated_text, basic_text) basic_text FROM term_field_description WHERE version_handling_id = ";    
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String select_string = SELECT_STMT +"? AND lang_code = ? ";
      ASPQuery q = trans.addQuery("GET_USAGE", select_string);
      q.addParameter("USAGE_KEY", "S", "IN", usagekey);
      q.addParameter("LANG_CODE", "S", "IN", mgr.getLanguageCode());
      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("GET_USAGE");
                
      ASPBuffer b = buf.getBufferAt(0);
      String basic_text= b.getValue("BASIC_TEXT");
      
      Rtf2Html r2h = new Rtf2Html();  //converts RTF basic text to HTML
      basic_text = r2h.convertRTFStringToHTML(basic_text);
      
      mgr.responseWrite(basic_text+"^");
      mgr.endResponse();
   }
} 
