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
*  File        : CreateStdJobRevisionDlg.java
* ---------------------------------------------------------------------------- 
*  NIJALK   041018   Created.
*  Modified:
*  NAMELK   041110   Duplicated Translation Tags Corrected.
*  NIJALK   050208   Modified run(),printContents().
*  ILSOLK   070710   Eliminated XSS.
*  CHANLK   080303   Bug 71681, Corrected to handle Error msg's on first request.
*  080403   SHAFLK   Bug 72750, Modified printContents().
*  080505   SHAFLK   Bug 82435, Modified submit(), preDefine() and run().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateStdJobRevisionDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateStdJobRevisionDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPBlockLayout headlay;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String std_job_id;
        private String std_job_contract;
        private String source_std_job_rev;
        private String dest_std_job_rev;
        private String qrystr;

	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

// Bug 71681, Start
   private boolean bFirstRequest;
// Bug 71681, End

	//===============================================================
	// Construction 
	//===============================================================
	public CreateStdJobRevisionDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() {
      std_job_id = "";
      std_job_contract = "";
      source_std_job_rev = "";
      dest_std_job_rev = "";
      qrystr = "";

      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      std_job_id = ctx.readValue("STD_JOB_ID", "");
      std_job_contract = ctx.readValue("CONTRACT", "");
      source_std_job_rev = ctx.readValue("STD_JOB_REVISION", "");
      win_cls = ctx.readFlag("WINCLS", win_cls);
      ok_sub = ctx.readFlag("OKSUB", ok_sub);
      dest_std_job_rev = ctx.readValue("DEST_STD_JOB_REVISION", dest_std_job_rev);
      qrystr = ctx.readValue("QRYSTR", qrystr);

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
//    Bug 71681, Start
      else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) ||!mgr.isExplorer())
      {
        std_job_id = mgr.readValue("STD_JOB_ID");
         std_job_contract = mgr.readValue("CONTRACT");
         source_std_job_rev = mgr.readValue("STD_JOB_REVISION");
         qrystr = mgr.readValue("QRYSTR");

         okFind();

         trans.clear();
         cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","DONEBY");        
         cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE DONEDATE FROM DUAL");

         trans = mgr.perform(trans);
         String repby = trans.getValue("GETUSER/DATA/DONEBY");
         String sysDate = trans.getValue("SUYSTDATE/DATA/DONEDATE");

         ASPBuffer temp = headset.getRow();
         temp.setValue("HEAD_STD_JOB_NO", std_job_id);
         temp.setValue("CONTRACT", std_job_contract);
         temp.setValue("OLDREVISION", source_std_job_rev);
         temp.setValue("DONEBY",repby);
         temp.setValue("DONEDATE",sysDate);
         headset.addRow(temp);
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")) && !mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_REVISION"))) {
         bFirstRequest = true;
      }
//    Bug 71681, End

      ctx.writeValue("STD_JOB_ID", std_job_id);
      ctx.writeValue("STD_JOB_REVISION", source_std_job_rev);
      ctx.writeValue("DEST_STD_JOB_REVISION", dest_std_job_rev);
      ctx.writeValue("CONTRACT", std_job_contract);
      ctx.writeValue("QRYSTR", qrystr);
      ctx.writeFlag("WINCLS", win_cls);
   }

// -----------------------------------------------------------------------------
// ------------------------- CMDBAR EDIT FUNCTIONS ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		if (!mgr.isEmpty(mgr.readValue("NEWREVISION")))
		{
                        cmd = trans.addCustomCommand("NEWREV","Standard_Job_API.Create_New_Revision");
			cmd.addParameter("HEAD_STD_JOB_NO");
                        cmd.addParameter("CONTRACT");
			cmd.addParameter("OLDREVISION");
			cmd.addParameter("NEWREVISION");
			cmd.addParameter("REASON");
			cmd.addParameter("DONEBY");
			cmd.addParameter("DONEDATE");
                         
                        trans=mgr.perform(trans);
			
                        dest_std_job_rev = mgr.readValue("NEWREVISION");
                        ok_sub = true;
	       	}
		else
		{
			mgr.showAlert(mgr.translate("PCMWCREATESTDJOBREVISIONDLGNEWREVISIONERROR: New Revision should have a value"));

		}
	}

	public void  cancel()
	{
		win_cls = true;
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("HEAD_STD_JOB_NO");
		f.setSize(9);
		f.setLabel("PCMWCREATESTDJOBREVISIONDLGSTDJOBNO: Standard Job No");
                f.setReadOnly();
                f.setHidden();
		f.setFunction("''");

                f = headblk.addField("CONTRACT");
		f.setSize(9);
		f.setLabel("PCMWCREATESTDJOBREVISIONDLGSTDJOBSITE: Standard Job Site");
                f.setReadOnly();
                f.setHidden();
		f.setFunction("''");

                f = headblk.addField("NEWREVISION");
		f.setSize(9);
                f.setMaxLength(18);
		f.setLabel("PCMWCREATESTDJOBREVISIONDLGNEWREV: New Revision");
		f.setInsertable();
                f.setMandatory();
                f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("OLDREVISION");
		f.setSize(9);
		f.setLabel("PCMWOLDREVISION: From Old Revision");
		f.setReadOnly();
		f.setFunction("''");

		f = headblk.addField("REASON");
		f.setSize(100);
		f.setLabel("PCMWCREATEPMREVISIONDLGREASON: Reason");
		f.setMaxLength(100);
		f.setFunction("''");
                
		f = headblk.addField("DONEBY");
		f.setSize(20);
		f.setLabel("PCMWCREATEPMREVISIONDLGDONEBY: Created By");
		f.setReadOnly();
		f.setFunction("''");
                
		f = headblk.addField("DONEDATE","Datetime");
		f.setSize(20);
		f.setLabel("PCMWCREATEPMREVISIONDLGDONEDATE: Date");
		f.setReadOnly();
		f.setFunction("''");

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.disableCommand(headbar.FORWARD);

		headbar.enableCommand(headbar.CANCELNEW);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELNEW,"cancel");
                headbar.disableCommand(headbar.SAVENEW);

		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setDialogColumns(1);
		headtbl.disableRowStatus();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCREATESTDJOBREVISION: Create New Revision for Standard Job - "+std_job_id+"";
	}

	protected String getTitle()
	{
		return "PCMWCREATESTDJOBREVISION: Create New Revision for Standard Job - "+std_job_id+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
      
//    Bug 71681, Start
		if (bFirstRequest && mgr.isExplorer())
      {
             appendToHTML("<html>\n"); 
             appendToHTML("<head></head>\n"); 
             appendToHTML("<body>"); 
             appendToHTML("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.HTMLEncode(mgr.getQueryString())+"'>"); //XSS_Safe AMNILK 20070725
             appendToHTML(mgr.newASPHTMLFormatter().drawHidden("FIRST_REQUEST", "OK"));  
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("STD_JOB_ID"))+"\" name=\"STD_JOB_ID\" >"); 
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("CONTRACT"))+"\" name=\"CONTRACT\" >"); 
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("STD_JOB_REVISION"))+"\" name=\"STD_JOB_REVISION\" >"); 
             appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("QRYSTR"))+"\" name=\"QRYSTR\" >"); 
             appendToHTML("</form></body></html>"); 
             appendDirtyJavaScript("document.form.submit();"); 
      } 
//    Bug 71681, End
      
		appendToHTML(headlay.show());

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(win_cls); // XSS_Safe ILSOLK 20070713
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");

                // XSS_Safe ILSOLK 20070710
                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(ok_sub); // XSS_Safe ILSOLK 20070713
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
                //appendDirtyJavaScript("	alert('");
		//appendDirtyJavaScript(mgr.translate("PCMWNEWREVCREATED: Revision "+ dest_std_job_rev +" Created for Standard Job "+ std_job_id +"."));
		//appendDirtyJavaScript("');\n");

                //Bug 72750, start
                appendDirtyJavaScript("  window.opener.location = '" +mgr.encodeStringForJavascript(qrystr)+"&REVCRT=TRUE&NEW_STD_JOB_ID="+mgr.encodeStringForJavascript(std_job_id)+"&NEW_REV="+mgr.encodeStringForJavascript(dest_std_job_rev)+"&CONTRACT="+mgr.encodeStringForJavascript(std_job_contract)+ "';\n"); 
                //Bug 72750, end
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
