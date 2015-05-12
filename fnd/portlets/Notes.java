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
 * File        : Notes.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Ramila H  2000-Mar-20 - Created.
 *    Jacek P   2000-Apr-01 - Changed package to fnd.portlets
 *    Jacek P   2000-Apr-17 - Corrected bug in doReset().
 *    Jacek P   2000-May-05 - JavaScript moved to preDefine()
 *    Jacek P   2000-Aug-07 - Added getMinWidth().
 *    Artur K   2001-Mar-20 - Necessary changes for handling multiple portal pages.
 *    Artur K   2001-May-24 - Changed init() function.
 *    Mangala P 2002-Feb-15 - Used ifs standard encode/decode for cookie values.
 *    ChandanaD 2002-Oct-21 - Implemented the run() method. Changed saveNotes() function.
 *    Johan S   2003-Mar-04 - updated the cookie method calls for the rest of the 4:th of mars change.
 *                            (see ASPContext and ASPManager).
 *    ChandanaD 2003-Mar-20 - Changed cookie expiration time.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.fnd.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.util.*;


public class Notes extends ASPPortletProvider
{
   private String msg;
   private String view_name;
   

   public Notes( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }


   protected void doReset() throws FndException
   {
      super.doReset();
      msg = null;
   }
   

   public void run()
   {
     //if( !Str.isEmpty(msg) )
     //  getASPContext().setPersistantCookie(getASPManager().getUserId()+"_"+view_name+"_"+getId()+"NOTES", msg);
   }
   
   public static int getMinWidth()
   {
      return 144;
   }


   public static String getDescription()
   {
      return "FNDNOTESDESC: Notes";
   }


   protected void preDefine() throws FndException
   {
      appendJavaScript( "function saveViewName(name)\n");
      appendJavaScript( "{\n");
      appendJavaScript( "   view_name = name+'_';");
      appendJavaScript( "}\n");

      appendJavaScript( "function saveNotes(obj,id)\n ");
      appendJavaScript( "{\n");
      appendJavaScript( "   var mynotes = getPortletField(id,'NOTES');\n");
      appendJavaScript( "   if(mynotes.value.length > 2000 )\n");
      appendJavaScript( "     alert(\"Note Pad Full - Can not save changes\");\n");
      appendJavaScript( "   else{\n");
      appendJavaScript( "      var expire = new Date();\n");
      appendJavaScript( "      expire.setTime(9145916800000);\n");
      appendJavaScript( "       writeCookie(view_name+id+'NOTES',URLClientEncode(mynotes.value),expire);}\n");
      appendJavaScript( "}\n");

      init();
   }
   
   
   protected void init()
   {
      ASPManager mgr = getASPManager();
      view_name  = mgr.URLEncode(mgr.getPortalPage().getASPPortal().getName());
      msg = mgr.URLDecode(getASPContext().getCookie(view_name+"_"+getId()+"NOTES"));
   }


   public String getTitle( int mode )
   {
      return translate(getDescription());
   }


   public void printContents() throws FndException
   {
      printNewLine();
      printTextArea( "NOTES", msg, 10, 28, "saveViewName('"+view_name+"');saveNotes" );
      printNewLine();
   }

   protected void clearGlobalProfile()
   {
      String name = view_name+"_"+getId()+"NOTES";
      getASPContext().removeCookie(name);
   }
}
