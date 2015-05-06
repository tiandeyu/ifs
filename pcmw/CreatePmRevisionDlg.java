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
*  File        : CreatePmRevisionDlg.java
* ---------------------------------------------------------------------------- 
*  040913  NIJALK  Created.
*  Modified:
*  041110   NAMELK   Non Standard Translation Tags Corrected.
*  050107   NIJALK   Modified display text.
*  050222   NIJALK   Modified run(),printContents().
*  070312   SHAFLK   Bug 64068, Removed extra mgr.translate().   
*  070330   AMDILK   Merged bug id 64068
*  ILSOLK  070710  Eliminated XSS.
*  071026   SHAFLK   Bug 67948, Modified submit(), preDefine() and printContents().
*  080403   SHAFLK   Bug 72750, Modified printContents().
*  080505   SHAFLK   Bug 82435, Modified submit(), preDefine() and run().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreatePmRevisionDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreatePmRevisionDlg");

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
	private ASPHTMLFormatter fmt;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
        private String pm_no;
        private String source_pm_rev;
        private String dest_pm_rev;
        private String qrystr;

	private boolean win_cls;
	private boolean ok_sub;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q; 
        private String sHasActiveRepl;

	//===============================================================
	// Construction 
	//===============================================================
	public CreatePmRevisionDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
                pm_no = "";
                source_pm_rev = "";
                dest_pm_rev = "";
                qrystr = "";

		ASPManager mgr = getASPManager();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

                pm_no = ctx.readValue("PM_NO","");
                source_pm_rev = ctx.readValue("PM_REVISION",""); 
		win_cls = ctx.readFlag("WINCLS",win_cls);
		ok_sub = ctx.readFlag("OKSUB",ok_sub);
                dest_pm_rev = ctx.readValue("DEST_PM_REVISION",dest_pm_rev);
                qrystr = ctx.readValue("QRYSTR",qrystr);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")))
		{
			pm_no = mgr.readValue("PM_NO");
			source_pm_rev = mgr.readValue("PM_REVISION");
                        qrystr = mgr.readValue("QRYSTR");

			okFind();
                        trans.clear();
                        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","DONEBY");        
                        cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE DONEDATE FROM DUAL");

                        trans = mgr.perform(trans);
                        String repby = trans.getValue("GETUSER/DATA/DONEBY");
                        String sysDate = trans.getValue("SUYSTDATE/DATA/DONEDATE");

			ASPBuffer temp = headset.getRow();
			temp.setValue("HEAD_PM_NO",pm_no);
                        temp.setValue("OLDREVISION",source_pm_rev);
                        temp.setValue("DONEBY",repby);
                        temp.setValue("DONEDATE",sysDate);

			headset.addRow(temp);
		}

		ctx.writeValue("PM_NO",pm_no);
		ctx.writeValue("PM_REVISION",source_pm_rev);
                ctx.writeValue("DEST_PM_REVISION",dest_pm_rev);
		ctx.writeFlag("WINCLS",win_cls);
                ctx.writeValue("QRYSTR",qrystr);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		if (!mgr.isEmpty(mgr.readValue("NEWREVISION")))
		{
                        cmd = trans.addCustomCommand("NEWREV","Pm_Action_API.Create_New_Revision");
			cmd.addParameter("HEAD_PM_NO");
			cmd.addParameter("OLDREVISION");
			cmd.addParameter("NEWREVISION");
			cmd.addParameter("REASON");
			cmd.addParameter("DONEBY");
			cmd.addParameter("DONEDATE");

                        //Bug 67948. start
                        cmd = trans.addCustomFunction("ACTREV","Pm_Action_Replaced_API.Has_Active_Replacements","ACTREV");
			cmd.addParameter("HEAD_PM_NO");
			cmd.addParameter("OLDREVISION");

                         
                        trans=mgr.perform(trans);
                        sHasActiveRepl = trans.getValue("ACTREV/DATA/ACTREV");
                        trans.clear();
                        if ("TRUE".equals(sHasActiveRepl))
                        {                      
                            cmd = trans.addCustomCommand("COPYREP","PM_ACTION_REPLACED_API.Copy_Replacements");
                            cmd.addParameter("HEAD_PM_NO");
                            cmd.addParameter("OLDREVISION");
                            cmd.addParameter("NEWREVISION");
                            trans=mgr.perform(trans);
                        }
                        //Bug 67948. end

			
                        dest_pm_rev = mgr.readValue("NEWREVISION");
                        ok_sub = true;
	       	}
		else
		{
			mgr.showAlert(mgr.translate("PCMWCREATEPMREVISIONDLGNEWREVISIONERROR: New Revision should have a value"));

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

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("HEAD_PM_NO");
		f.setSize(9);
		f.setLabel("PCMWPMNO: PM No");
                f.setReadOnly();
                f.setHidden();
		f.setFunction("''");

                f = headblk.addField("NEWREVISION");
		f.setSize(9);
		f.setLabel("PCMWCREATEPMREVISIONDLGNEWREV: New Revision");
		f.setInsertable();
                f.setMandatory();
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
                
                f = headblk.addField("ACTREV");
		f.setHidden();
		f.setFunction("''");


		headblk.setView("MODULE");
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
		return "PCMWCREATEPMREVISION: Create New Revision for PM - "+pm_no+"";
	}

	protected String getTitle()
	{
		return "PCMWCREATEPMREVISION: Create New Revision for PM - "+pm_no+"";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headbar.showBar());
		appendToHTML("<table>\n");
		appendToHTML("   <tr>\n");
		appendToHTML("      <td>&nbsp;&nbsp;");
		appendToHTML(fmt.drawReadLabel("PCMWCREATEPMREVISIONDLGTXTLABEL: 'Obsolete' Standard Jobs will be replaced by their latest revisions."));
		appendToHTML("      </td>\n");
		appendToHTML("   </tr>\n");
		appendToHTML("</table>\n");
		appendToHTML(headlay.generateDataPresentation());

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
                //Bug 72750, start
                appendDirtyJavaScript("  window.opener.location = '" +mgr.encodeStringForJavascript(qrystr)+"&REVCRT=TRUE&NEW_PM_NO="+mgr.encodeStringForJavascript(pm_no)+"&NEW_REV="+mgr.encodeStringForJavascript(dest_pm_rev)+"&ACT_REP="+mgr.encodeStringForJavascript(sHasActiveRepl)+ "';\n"); 
                //Bug 72750, end
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
