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
 *  File        : ChangePassword.java
 *  Modified    :
 *    rahelk   2003-07-30 - created. Log id 853 change password.
 *    rahelk   2003-09-02 - corrected translation key, and error messages.      
 *    rahelk   2003-09-04 - Removed button width to avoid translation problems.
 *    ChandanaD2004-05-12 - Updated for the use of new style sheets.
 *    Suneth M 2005-12-27 - Merged the corrections for Bug 54639, Changed getContents().
 *    Mangala  2007-01-30  - Bug 63250, Added theming support in IFS clients.
 * ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ChangePassword extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ChangePassword");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   
   private ASPHTMLFormatter fmt;
   private ASPCommandBar cmdbar;
   private ASPBlock blk;
   private ASPBlockLayout lay;
   private ASPRowSet rowset;


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private String user_id;
   private boolean changed_password;
   
   //===============================================================
   // Construction
   //===============================================================
   public ChangePassword(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      fmt    = getASPHTMLFormatter();

      user_id = mgr.getUserId();
      
      //if (!mgr.getASPConfig().isChangePasswordEnabled()) 
      //   throw new FndException("CHNGPSWNOTAUTH: Change password not allowed.");
      
      if( mgr.buttonPressed("OK"))
         changePassword();
      else
         init();
   }
//===============================================================
//  Button functions
//===============================================================
   
   public void changePassword()
   {
      ASPManager mgr = getASPManager();
      
      try
      {
         ManageUserPassword password_manager = ManageUserPassword.getInstance(mgr.getASPConfig());
         String old_password = mgr.readValue("OLD_PASSWORD");
         String new_password = mgr.readValue("NEW_PASSWORD");
         String confirm_password = mgr.readValue("CONFIRM_PASSWORD");
         
         if (mgr.isEmpty(old_password) ||mgr.isEmpty(new_password) || mgr.isEmpty(confirm_password))
         {
            mgr.showAlert("FNDPAGESCHANGEPASSWORDEMPTY: One or more required fields are empty. Please enter values for all fields and try again.");
         }
         else if (!mgr.isEmpty(new_password) && new_password.equals(confirm_password))
         {
            password_manager.changePassword(user_id,old_password, new_password, mgr.getASPConfig());
            changed_password = true;
         }
         else
            mgr.showAlert("FNDPAGESCHANGEPASSWORDNOTMATCH: The new passwords you typed do not match. Please varify passwords again.");
      }
      catch (Exception e)
      {
         //mgr.showAlert("FNDPAGESCHANGEPASSWORDUNSUCCESS: Password change was unsuccessful.");
         mgr.showAlert(e.getLocalizedMessage());
      }
      
      
   }
   
   public void init()
   {
      ASPManager mgr = getASPManager();
      
      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("USER_ID", mgr.getUserId());
      rowset.addRow(buff);
   }
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDCHANGEPASSWORDTITLE: Change Password";
   }

   protected String getTitle()
   {
      return getASPManager().translate("FNDCHANGEPASSWORDHEADTITLE: Change Password for - &1",user_id);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");
      blk.setTitle(mgr.translate("FNDPAGESCHANGEPASSWORDBLKTIT: Change Password"));
      
      blk.addField("USER_ID").
      setSize(30).
      setReadOnly().
      setLabel("FNDPAGESCHANGEPASSWORDID: User ID");

      blk.addField("OLD_PASSWORD").
      setSize(30).
      setPasswordField().
      setMandatory().
      setLabel("FNDPAGESCHANGEPASSWORDOLD: Old Password");

      blk.addField("NEW_PASSWORD").
      setSize(30).
      setPasswordField().
      setMandatory().
      setLabel("FNDPAGESCHANGEPASSWORDNEW: New Password");

      blk.addField("CONFIRM_PASSWORD").
      setSize(30).
      setPasswordField().
      setMandatory().
      setLabel("FNDPAGESCHANGEPASSWORDCONFIRM: Confirm Password");
      
      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.EDIT_LAYOUT);
      lay.setDialogColumns(1);
   }  

   protected AutoString getContents() throws FndException 
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDPAGESCHANGEPASSWORDHEADTITLE: Change Password"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(" >\n");

      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(" >\n");
      out.append(mgr.startPresentation(mgr.translate("FNDPAGESCHANGEPASSWORDTITLE: Change Password")));

      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate("FNDPAGESCHANGEPASSWORDTITLE: Change Password"));
      endDataPresentation(false);
      
      if (mgr.getASPConfig().isChangePasswordEnabled()) 
      {

         if (!changed_password)
            out.append(lay.generateDataPresentation());
         else
         {
            beginDataPresentation();
            out.append("<table width='100%' class='pageFormWithBorder' cellspacing=0 cellpadding=2>");
            out.append("<tr><td>&nbsp;</td></tr>");
            out.append("<tr><td>");
            printReadLabel("FNDPAGESCHANGEPASSWORDSUCCESS: Password changed successfully.");
            out.append("</td></tr>");             
            out.append("<tr><td>&nbsp;</td></tr></table>");
            endDataPresentation(false);
         }

         beginDataPresentation();

         String tag = " class='button'";

         if (!changed_password)
         {
            printSubmitButton("OK", "FNDPAGESCHANGEPASSWORDOK: Ok", tag);
            appendToHTML("&nbsp;&nbsp;");
            printButton("CANCEL", "FNDPAGESCHANGEPASSWORDCANCEL: Cancel","onClick=\"javascript:cancel()\" "+tag);
         }
         else
            printButton("OK", "FNDPAGESCHANGEPASSWORDOK: Ok","onClick=\"javascript:cancel()\" "+tag);

         appendDirtyJavaScript("function cancel()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("\twindow.location.href='"+mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")+"';");
         appendDirtyJavaScript("}\n");


         endDataPresentation(false);
      }
      else
      {
         beginDataPresentation();
         out.append("<table width='100%' class='pageFormWithBorder' cellspacing=0 cellpadding=2>");
         out.append("<tr><td>&nbsp;</td></tr>");
         out.append("<tr><td>");
         printReadLabel("FNDPAGESCHANGEPASSWORDNOTAUTH: Change password not allowed.");
         out.append("</td></tr>");             
         out.append("<tr><td>&nbsp;</td></tr></table>");
         endDataPresentation(false);
      }
      
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      
      return out;
   }


}
