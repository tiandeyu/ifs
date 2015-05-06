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
*  041018  NIJALK  Created.
*  Modified:
*  041110 NAMELK Duplicated Translation Tags Corrected.
*  050223 NIJALK Modified run(),printContents().
*  060329 NIJALK Call 137420: Modified preDefine().
*  070710 ILSOLK    Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateJobProgramRevisionDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateJobProgramRevisionDlg");

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
        private String job_program_id;
        private String job_program_contract;
        private String source_job_program_rev;
        private String dest_job_program_rev;
        private String qrystr;

	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public CreateJobProgramRevisionDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                job_program_id = "";
                job_program_contract = "";
                source_job_program_rev = "";
                dest_job_program_rev = "";
                qrystr = "";

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
                job_program_id = ctx.readValue("JOB_PROGRAM_ID","");
                job_program_contract = ctx.readValue("JOB_PROGRAM_CONTRACT","");
                source_job_program_rev = ctx.readValue("JOB_PROGRAM_REVISION",""); 
		win_cls = ctx.readFlag("WINCLS",win_cls);
		ok_sub = ctx.readFlag("OKSUB",ok_sub);
                dest_job_program_rev = ctx.readValue("DEST_JOB_PROGRAM_REVISION",dest_job_program_rev);
                qrystr = ctx.readValue("QRYSTR",qrystr);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_CONTRACT")) && !mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_REVISION")))
		{
			job_program_id = mgr.readValue("JOB_PROGRAM_ID");
                        job_program_contract = mgr.readValue("JOB_PROGRAM_CONTRACT");
			source_job_program_rev = mgr.readValue("JOB_PROGRAM_REVISION");
                        qrystr = mgr.readValue("QRYSTR");

			okFind();

			ASPBuffer temp = headset.getRow();
			temp.setValue("HEAD_JOB_PROGRAM_NO",job_program_id);
                        temp.setValue("JOB_PROGRAM_CONTRACT",job_program_contract);
                        temp.setValue("OLD_JOB_PROGRAM_REVISION",source_job_program_rev);
			headset.addRow(temp);
		}

		ctx.writeValue("JOB_PROGRAM_ID",job_program_id);
		ctx.writeValue("JOB_PROGRAM_CONTRACT",job_program_contract);
		ctx.writeValue("JOB_PROGRAM_REVISION",source_job_program_rev);
                ctx.writeValue("DEST_JOB_PROGRAM_REVISION",dest_job_program_rev);
		ctx.writeFlag("WINCLS",win_cls);
                ctx.writeValue("QRYSTR",qrystr);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		if (!mgr.isEmpty(mgr.readValue("NEW_JOB_PROGRAM_REVISION")))
		{
                        cmd = trans.addCustomCommand("NEWREV","Job_Program_API.Create_New_Revision");
			cmd.addParameter("HEAD_JOB_PROGRAM_NO");
                        cmd.addParameter("JOB_PROGRAM_CONTRACT");
			cmd.addParameter("OLD_JOB_PROGRAM_REVISION");
			cmd.addParameter("NEW_JOB_PROGRAM_REVISION");
                         
                        trans=mgr.perform(trans);
			
                        dest_job_program_rev = mgr.readValue("NEW_JOB_PROGRAM_REVISION");
                        ok_sub = true;
	       	}
		else
		{
			mgr.showAlert(mgr.translate("PCMWCREATEJOBPROGRAMREVISIONDLGNEWREVISIONERROR: New Revision should have a value"));

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

		f = headblk.addField("HEAD_JOB_PROGRAM_NO");
		f.setSize(9);
		f.setLabel("PCMWCREATEJOBPROGRAMREVISIONDLGSTDJOBNO: Job Program No");
                f.setReadOnly();
                f.setHidden();
		f.setFunction("''");

                f = headblk.addField("JOB_PROGRAM_CONTRACT");
		f.setSize(9);
		f.setLabel("PCMWCREATEJOBPROGRAMREVISIONDLGSTDJOBSITE: Job Program Site");
                f.setReadOnly();
                f.setHidden();
		f.setFunction("''");

                f = headblk.addField("NEW_JOB_PROGRAM_REVISION");
		f.setSize(9);
                f.setMaxLength(18);
		f.setLabel("PCMWCREATEJOBPROGRAMREVISIONDLGNEWREV: New Revision");
		f.setInsertable();
                f.setMandatory();
                f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("OLD_JOB_PROGRAM_REVISION");
		f.setSize(9);
		f.setLabel("PCMWOLDJOBPROGRAMREVISION: From Old Revision");
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

                enableConvertGettoPost();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCREATEJOBPROGRAMREVISION: Create New Revision for Job Program - "+job_program_id+"";
	}

	protected String getTitle()
	{
		return "PCMWCREATEJOBPROGRAMREVISION: Create New Revision for Job Program - "+job_program_id+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

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
                appendDirtyJavaScript("  window.open('"+mgr.encodeStringForJavascript(qrystr)+"&REVCRT=TRUE&NEW_JOB_PROGRAM_ID="+mgr.encodeStringForJavascript(job_program_id)+"&NEW_REV="+mgr.encodeStringForJavascript(dest_job_program_rev)+"&CONTRACT="+mgr.encodeStringForJavascript(job_program_contract)+"\',\'jobProgram\');");
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
