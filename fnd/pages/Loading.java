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
 *  File        : Loading.java
 *  Description : To show loading msg 
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2007/04/05 rahelk F1PR458 - Application Search Improvements
 * ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class Loading extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.About");

   public Loading(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   
   protected String getDescription()
   {
      return "FNDPAGESLOADINGTITLE: Loading ...";
   }

   protected String getTitle()
   {
      return "FNDPAGESLOADINGTITLE: Loading ...";
   }
   
   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      
      out.append("<body bgcolor=\"#FFFFCC\" ><p><br>\n");
      printText("FNDPAGESLOADINGPLSWAIT: Loading please wait...");
      out.append("</p></body>\n");
      out.append("</html>\n");
      
      return out;
   }
   
   
   
   
}
