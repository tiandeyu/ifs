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
 * File        : ReAuthenticate.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified
 * 2007/11/08 rahelk Bug id 68906, Added comments field in dlg
 * 2007/05/28 rahelk Merged Bug id 63254. 
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * 2007/05/09 mapelk Bug id 63254, Security checkpoint is improved.
 *
 * Revision 1.2  2005/07/26 10:37:27  mapelk
 * Fixed the problem with dissaprearing ReAuthenticate page when giving wrong Passwords
 *
 * Revision 1.1  2005/01/28 18:07:27  marese
 * Initial checkin
 *
 * Revision 1.4  2004/12/14 09:58:22  mapelk
 * Logon button replaced with Ok and Cancel buttons
 *
 * Revision 1.3  2004/12/13 04:33:57  mapelk
 * Minor modifications for electronic signature support
 *
 * Revision 1.2  2004/12/11 10:33:51  mapelk
 * Minor modifications for electronic signature support
 *
 * Revision 1.1  2004/12/10 08:54:29  mapelk
 * Added electronic signature support
 *
 * ----------------------------------------------------------------------------
 */
package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

/**
 *
 */
public class ReAuthenticate extends ASPPageProvider {
   
   
   /** ==============================================================
    *  Static constants
    * =============================================================== 
    */
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ReAuthenticate");
   
   private ASPBlock blk;
   private ASPBlockLayout lay;
   private ASPRowSet rowset;
   
   private boolean resubmit;

   /**
    * Creates a new instance of this class.
    */
   public ReAuthenticate(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      
      ASPManager mgr = getASPManager();
      //if ("T".equals(mgr.readValue("SUBMITPWD")))
      Buffer reauth_buf = mgr.getDecisionBuffer().getBuffer("^REAUTHENTICATION^",null);
      if (mgr.buttonPressed("AUTHENTICATE"))
      {
         try
         {
            reauth_buf.setItem("USERNAME",mgr.readValue("USER_ID"));
            reauth_buf.setItem("PASSWORD",mgr.readValue("PASSWORD"));
            reauth_buf.setItem("USER_COMMENT",mgr.readValue("USER_COMMENT"));
            
            resubmit = true;
         }
         catch (Exception e)
         {
            error(new FndException("FNDPAGESREAUTHENTICATIONFAIL: Reauthentication fail."));
         }
      } else
      {
          ASPBuffer row = mgr.newASPBuffer();
          try
          {
            row.setValue("USER_ID",(reauth_buf!=null)?reauth_buf.getItem("USERNAME").getValue().toString():"");
          }
          catch (ItemNotFoundException e)
          {
              }
          rowset.clear();
          rowset.addRow(row);
      }
   }
   
   protected String getDescription()
   {
      return "FNDPAGESREAUTHENTICATETITLE: Authentication";
   }

   protected String getTitle()
   {
      return "FNDPAGESREAUTHENTICATETITLE: Authentication";
   }
   
   protected void preDefine()
   {
      ASPManager mgr = getASPManager();

      disableHeader();
      disableBar();
      disableFooter();
      
      blk = mgr.newASPBlock("MAIN");
         
      blk.addField("USER_ID").
         setLabel("FNDPAGESREAUTHENTICATEUSERID: User Id");
         //setReadOnly();
      
      blk.addField("PASSWORD").
         setPasswordField().
         setLabel("FNDPAGESREAUTHENTICATEPASSWORD: Password");

      blk.addField("USER_COMMENT").
          setSize(40).
          setHeight(3).
          setLabel("FNDPAGESREAUTHENTICATECOMMENTS: Comments");
      
      ASPCommandBar bar = blk.newASPCommandBar();
      rowset = blk.getASPRowSet();
      
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.EDIT_LAYOUT);
      lay.setDialogColumns(1);
   }
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      printNewLine();
      beginDataPresentation();
      drawSimpleCommandBar("FNDPAGESREAUTHENTICATETITLE: Authentication");
      endDataPresentation(false);
      appendToHTML(lay.generateDataPresentation());
      beginDataPresentation();
      printSubmitButton("AUTHENTICATE","FNDPAGESREAUTHENTICATELOGON: OK","");
      printSpaces(2);
      printButton("CANCEL","FNDPAGESREAUTHENTICATECANCEL: Cancel","OnClick='javascript:window.close()'"); 
      endDataPresentation(false);
      printHiddenField("SUBMITPWD", "F");

      String command = mgr.readValue("LAST_COMMAND");
      String perform = mgr.readValue("PERFORM");
      String perform_field = mgr.readValue("PERFORMED_FIELD");

      printHiddenField("LAST_COMMAND", command);
      printHiddenField("PERFORM", perform);
      printHiddenField("PERFORMED_FIELD", perform_field);
      
      if (DEBUG)
      {
         debug("LAST_COMMAND = " + command);
         debug("PERFORM = " + perform);
         debug("PERFORMED_FIELD=" + perform_field);
      }

      appendDirtyJavaScript("\r\nwindow.focus();\r\n" +
                           "\r\nfunction performLastCommand(){" +
                           "\r\n\twindow.opener.document.form.__REAUTHENTICATION.value='Y';" + 
                           "\r\n\twindow.opener.document.form.__COMMAND.value='"+command+"';" + 
                           "\r\n\twindow.opener.document.form." + perform_field + ".value='"+perform+"';" + 
                           "\r\n\twindow.opener.document.form.submit();" +
                          "\r\n}");
      if (resubmit)
         appendDirtyJavaScript("\r\nperformLastCommand();\r\nwindow.close();");
   }
}
