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
*  File        : BackgroundJobConfirm.java 
*  Created     : 16-09-2008 DULOLK Bug Id 70808
* ----------------------------------------------------------------------------
*  31-10-2008  : DULOLK - Bug Id 77727, Called setHeadCommand() and commandSet() methods of parent to 
*                                       make security checkpoint work correctly.
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class BackgroundJobConfirm extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.BackgroundJobConfirm");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;

   //===============================================================
   // Construction 
   //===============================================================
   public BackgroundJobConfirm(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      fmt   = mgr.newASPHTMLFormatter();
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableBar();
      disableHeader();
      disableOptions();
      disableHomeIcon();
      disableNavigate();
      disableHelp();
   }

   protected String getDescription()
   {
      return "DOCMAWBGJCONFIRMTITLE: Confirm Approval";
   }

   protected String getTitle()
   {
      return "DOCMAWBGJCONFIRMTITLE: Confirm Approval";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
      appendToHTML("	<tr align=\"left\" vAlign=\"middle\" height=\"20\">\n");
      appendToHTML("       <td width=\"5\"></td>");  
      appendToHTML("       <td align = center>"+fmt.drawReadLabel(mgr.translate("DOCMAWBGJAPPROVECONFIRM: Do you wish to approve the Document Revisions?"))+"</td>\n");               
      appendToHTML("       <td width=\"5\"></td>");
      appendToHTML("    </tr>\n");
      appendToHTML("	<tr align=\"left\" vAlign=\"middle\" height=\"20\">\n");
      appendToHTML("       <td width=\"5\"></td>"); 
      appendToHTML("       <td align = center>"+fmt.drawCheckbox("BGJ","", false,"align=left")+ " " + fmt.drawReadLabel(mgr.translate("DOCMAWBGJAPPROVEBGJ: Perform as Background Job")) + " </td>\n");
      appendToHTML("       <td width=\"5\"></td>"); 
      appendToHTML("    </tr>\n");
      appendToHTML("	<tr align=\"left\" vAlign=\"bottom\" height=\"20\">\n");
      appendToHTML("       <td width=\"5\"></td>");
      appendToHTML("       <td align = center>"+fmt.drawButton("OK","    " + mgr.translate("DOCMAWBGJAPPROVEOK: OK") + "    ","OnClick=ok() width=50 align=right")+ " " + fmt.drawButton("CANCEL"," " + mgr.translate("DOCMAWBGJAPPROVECANCEL: Cancel" + " "),"OnClick=cancel() width=50 align=left")+"</td>\n");
      appendToHTML("       <td width=\"5\"></td>");
      appendToHTML("    </tr>\n");
      appendToHTML(" </table>\n");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      
      appendDirtyJavaScript("function ok()\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("    window.opener.document.form.CONFIRM.value='OK';\n");
      //Bug Id 77727, Start
      appendDirtyJavaScript("    if (document.form.BGJ.checked == 1) \n");
      appendDirtyJavaScript("    { \n");
      appendDirtyJavaScript("       window.opener.document.form.BGJ_CONFIRMED.value='OK';\n");
      appendDirtyJavaScript("       eval(\"opener.submitParent()\");\n");
      appendDirtyJavaScript("    } \n");
      appendDirtyJavaScript("    else \n");
      appendDirtyJavaScript("    { \n");      
      appendDirtyJavaScript("       eval(\"opener.setHEADCommand('setApproved')\");\n");
      appendDirtyJavaScript("       eval(\"opener.commandSet('HEAD.Perform', '')\");\n");
      appendDirtyJavaScript("    } \n");
      //Bug Id 77727, End
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("function cancel()\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("    window.opener.document.form.CONFIRM.value='CANCEL';\n");
      appendDirtyJavaScript("    eval(\"opener.refreshParentRowsSelected()\");\n");
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("} \n");
   }
}
