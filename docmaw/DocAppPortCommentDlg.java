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
*  File        : DocAppPortCommentDlg.java 
*  Modified    :
*
*  2006-03-15    DIKALK    Cleaned this page.
*  2006-03-18    DIKALK    All logic for approving/rejecting steps in the Approve Documents.
*                          portlet now below to this page. This page also takes care of 
*                          handling security checkpoints.
*  2007-08-09    NaLrlk    XSS Correction.
*  2008-01-11    DULOLK    Bug Id 68454, Modified approveStep() and rejectStep() to prevent the value "null"
*                          being passed when COMMENT field was left empty.
*  2008-08-04    AMNALK    Bug Id 75783, Modified initApprovals(), approveStep() & rejectStep() to handle the comment.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocAppPortCommentDlg extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocAppPortCommentDlg");


   private ASPHTMLFormatter fmt;
   private ASPContext ctx;

   private ASPBlock                 approvalblk;
   private ASPCommandBar            approvalbar;
   private ASPRowSet                approvalset;
   private ASPTable                 approvaltbl;
   private ASPBlockLayout           approvallay;


   public DocAppPortCommentDlg(ASPManager mgr, String page_path)
   { 
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();

      initApprovals();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
   }


   private void initApprovals() throws FndException
   {
      ASPManager mgr = getASPManager();

      ctx.writeValue("COMMAND", mgr.readValue("COMMAND", ctx.readValue("COMMAND", "")));
      ctx.writeValue("PREFIX",  mgr.readValue("PREFIX", ctx.readValue("PREFIX", "")));
      ctx.writeValue("LU_NAME", mgr.readValue("LU_NAME", ctx.readValue("LU_NAME", "")));
      ctx.writeValue("KEY_REF", mgr.readValue("KEY_REF", ctx.readValue("KEY_REF", "")));
      ctx.writeValue("LINE_NO", mgr.readValue("LINE_NO", ctx.readValue("LINE_NO", "")));
      ctx.writeValue("STEP_NO", mgr.readValue("STEP_NO", ctx.readValue("STEP_NO", "")));
      ctx.writeValue("NOTE", mgr.readValue("NOTE", ctx.readValue("NOTE", ""))); //Bug Id 75783

      // add a single row..
      if (approvalset.countRows() == 0)
      {
         approvalset.addRow(null);

         // Why is this line required? Because this page was opened using
         // a query string. When the Reauthentication exception occurs, and
         // this window is pushed one step back in its history, the window
         // would automatically refresh itself using the GET query string
         // as before, this clearing the context. This line would help
         // prevent that.
         appendDirtyJavaScript("submit();\n");
      }

      approvallay.setEditable();
      approvalset.store();

      approvalset.setValue("COMMENT", ctx.readValue("NOTE")); //Bug Id 75783

      // Set block title..
      String command = ctx.readValue("COMMAND");

      if ("APPROVESTEP".equals(command)) 
         approvalblk.setTitle(mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGBLOCKTITLEAPPROVE: Enter a comment before approving this step..."));
      else if ("REJECTSTEP".equals(command)) 
         approvalblk.setTitle(mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGBLOCKTITLEREJECT: Enter a comment before rejecting this step..."));
   }


   public void approveStep() throws FndException
   {
      debug("debug: comment = " + approvalset.getValue("COMMENT"));

      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("APPROVE_STEP", "Approval_Routing_API.Set_Next_App_Step");
      cmd.addParameter("INSTR", ctx.readValue("LU_NAME"));
      cmd.addParameter("INSTR", ctx.readValue("KEY_REF"));
      cmd.addParameter("INSTR", ctx.readValue("LINE_NO"));
      cmd.addParameter("INSTR", ctx.readValue("STEP_NO"));

      ASPQuery query = trans.addQuery("GET_ID_VER", "SELECT OBJID, OBJVERSION FROM APPROVAL_ROUTING WHERE LU_NAME = ? AND KEY_REF = ? AND LINE_NO = ? AND STEP_NO = ?");
      query.addParameter("INSTR", ctx.readValue("LU_NAME"));
      query.addParameter("INSTR", ctx.readValue("KEY_REF"));
      query.addParameter("INSTR", ctx.readValue("LINE_NO"));
      query.addParameter("INSTR", ctx.readValue("STEP_NO"));

      //Bug Id 68454, Start

      String attr = "NOTE" + DocmawUtil.FIELD_SEPARATOR + (mgr.isEmpty(mgr.readValue("COMMENT")) ? "" : mgr.readValue("COMMENT")) + DocmawUtil.RECORD_SEPARATOR; //Bug Id 75783
      attr += "APPROVAL_STATUS_DB" + DocmawUtil.FIELD_SEPARATOR + "APP" + DocmawUtil.RECORD_SEPARATOR;

      cmd = trans.addCustomCommand("SAVE_COMMENT", "Approval_Routing_API.Modify__");
      cmd.addParameter("INSTR");
      cmd.addReference("OBJID", "GET_ID_VER/DATA");
      cmd.addReference("OBJVERSION", "GET_ID_VER/DATA");
      cmd.addParameter("INSTR", attr);
      cmd.addParameter("INSTR", "DO");
      trans = mgr.perform(trans);
      //Bug Id 68454, End

      appendDirtyJavaScript("opener." + mgr.encodeStringForJavascript(ctx.readValue("PREFIX")) + "refreshPortlet();\n");
      appendDirtyJavaScript("window.close();\n");
   }


   public void rejectStep() throws FndException
   {
      debug("debug: comment = " + approvalset.getValue("COMMENT"));

      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("APPROVE_STEP", "Approval_Routing_API.Set_Next_App_Step");
      cmd.addParameter("INSTR", ctx.readValue("LU_NAME"));
      cmd.addParameter("INSTR", ctx.readValue("KEY_REF"));
      cmd.addParameter("INSTR", ctx.readValue("LINE_NO"));
      cmd.addParameter("INSTR", ctx.readValue("STEP_NO"));
      cmd.addParameter("INSTR", "REJ");

      ASPQuery query = trans.addQuery("GET_ID_VER", "SELECT OBJID, OBJVERSION FROM APPROVAL_ROUTING WHERE LU_NAME = ? AND KEY_REF = ? AND LINE_NO = ? AND STEP_NO = ?");
      query.addParameter("INSTR", ctx.readValue("LU_NAME"));
      query.addParameter("INSTR", ctx.readValue("KEY_REF"));
      query.addParameter("INSTR", ctx.readValue("LINE_NO"));
      query.addParameter("INSTR", ctx.readValue("STEP_NO"));

      //Bug Id 68454, Start

      String attr = "NOTE" + DocmawUtil.FIELD_SEPARATOR + (mgr.isEmpty(mgr.readValue("COMMENT")) ? "" : mgr.readValue("COMMENT")) + DocmawUtil.RECORD_SEPARATOR; //Bug Id 75783
      attr += "APPROVAL_STATUS_DB" + DocmawUtil.FIELD_SEPARATOR + "REJ" + DocmawUtil.RECORD_SEPARATOR;

      cmd = trans.addCustomCommand("SAVE_COMMENT", "Approval_Routing_API.Modify__");
      cmd.addParameter("INSTR");
      cmd.addReference("OBJID", "GET_ID_VER/DATA");
      cmd.addReference("OBJVERSION", "GET_ID_VER/DATA");
      cmd.addParameter("INSTR", attr);
      cmd.addParameter("INSTR", "DO");
      trans = mgr.perform(trans);
      //Bug Id 68454, End

      appendDirtyJavaScript("opener." + mgr.encodeStringForJavascript(ctx.readValue("PREFIX")) + "refreshPortlet();\n");
      appendDirtyJavaScript("window.close();\n");
   }



   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      disableHelp();
      disableOptions();
      disableHomeIcon();
      disableNavigate();

      approvalblk = mgr.newASPBlock("APPROVALS");

      approvalblk.addField("COMMENT").
                  setSize(75).
                  setHeight(6).
                  setLabel("DOCMAWDOCAPPPORTCOMMENTDLGCOMMENT: Comment");

      approvalblk.addField("OBJID").
                  setHidden();

      approvalblk.addField("OBJVERSION").
                  setHidden();

      approvalblk.addField("INSTR").
                  setHidden();

      approvalset = approvalblk.getASPRowSet();
      approvaltbl = mgr.newASPTable(approvalblk);

      approvallay = approvalblk.getASPBlockLayout();
      approvallay.setDefaultLayoutMode(ASPBlockLayout.CUSTOM_LAYOUT);

      approvalbar = mgr.newASPCommandBar(approvalblk);
      approvalbar.addCustomCommand("approveStep", "Approve Step");
      approvalbar.addCustomCommand("rejectStep", "Reject Step");


      appendJavaScript("function executeServerCommand(command)\n");
      appendJavaScript("{\n");
      appendJavaScript("   f.__APPROVALS_Perform.value = command;\n");
      appendJavaScript("   commandSet('APPROVALS.Perform','');\n");
      appendJavaScript("}\n");
   }


   protected String getDescription()
   {
      return getTitle();
   }


   protected String getTitle()
   {
      ASPManager mgr = getASPManager();

      String command = ctx.readValue("COMMAND");

      if ("APPROVESTEP".equals(command)) 
         return mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGAPPDOC: Approve Step");
      else if ("REJECTSTEP".equals(command)) 
         return mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGREJDOC: Reject Step");
      else
         return "";
   }


   private void drawControlPanel()
   {
      ASPManager mgr = getASPManager();

      String command = ctx.readValue("COMMAND");
      String execute_lable = null;
      String execute_action = null;

      if ("APPROVESTEP".equals(command)) 
      {
         execute_lable = mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGAPPROVE: Approve");
         execute_action = "approveStep";
      }
      else if ("REJECTSTEP".equals(command)) 
      {
         execute_lable = mgr.translate("DOCMAWDOCAPPPORTCOMMENTDLGREJECT: Reject");
         execute_action = "rejectStep";
      }

      appendToHTML("<table align=right>\n");
      appendToHTML("   <tr><td>\n");
      appendToHTML(fmt.drawButton("EXECUTE", execute_lable, "onClick=\"executeServerCommand('" + execute_action + "')\""));
      appendToHTML("   </td><td>\n");
      appendToHTML(fmt.drawButton("CANCEL", "DOCMAWDOCAPPPORTCOMMENTDLGDLGCANCEL: Cancel", "onClick=\"javascript:window.close();\""));
      appendToHTML("   </td></tr>\n");
      appendToHTML("</table>\n");
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();

      appendToHTML("<html>\n");
      appendToHTML("<head>\n");	
      appendToHTML(mgr.generateHeadTag(getTitle()));
      appendToHTML("</head>\n");	  
      appendToHTML("<body " + mgr.generateBodyTag() + " >\n");	  
      appendToHTML("<form " + mgr.generateFormTag() + " >\n");	   

      appendToHTML("<br>");
      appendToHTML("&nbsp;&nbsp;");
      appendToHTML("<font class=\"pageTitle\">");
      appendToHTML(getTitle());
      appendToHTML("</font>");
      appendToHTML("<br><br>");

      // draw layout..
      appendToHTML(approvallay.show());

      // draw control pannel..
      drawControlPanel();

      appendToHTML(mgr.generateClientScript());
      appendToHTML("</form>\n");	  
      appendToHTML("</body>\n");
      appendToHTML("</html>\n");

      appendToHTML("</body>\n");
      appendToHTML("</html>\n");


      appendDirtyJavaScript("init();\n");
      appendDirtyJavaScript("function init()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.COMMENT.focus();\n");
      appendDirtyJavaScript("}\n");

      return out;
   }
}
