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
*  File        : EventGenerationDlg.java 
*  Modified    :
*  SHFELK  010215  Created.
*  BUNILK  010720  Made some changes in redirect way of submit() and cancel() methods, to avoid 
*                  problems in Netscape browser  
*  YAWILK  021203  Modified the field CONTRACT  
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)  
*  ARWILK  040903  Modified methods preDefine and printContents.(IID AMEC111A: Std Jobs as PM Templates)         
*  NAMELK  041105  Non Standard and Duplicated Translation Tags Corrected.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bud id 64068
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class EventGenerationDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.EventGenerationDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public EventGenerationDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
			clear();

		okFind();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		String val = null;
		String mch = null;
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("CALL_CODE".equals(val))
		{
			cmd=trans.addCustomFunction("ESEQ","Maintenance_Event_API.Get_Sequence","CALL_SEQUENCE");
			cmd.addParameter("CALL_CODE");
			trans=mgr.validate(trans);
			mch=trans.getValue("ESEQ/DATA/CALL_SEQUENCE");
			mgr.responseWrite(mch);
		}

		mgr.endResponse();
		trans.clear();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWEVENTGENERATIONDLGNODATA: No data found."));
			headset.clear();
		}
	}

	public void clear()
	{
		headset.clear();
		headtbl.clearQueryRow();
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		if (!mgr.isEmpty(mgr.readValue("CALL_CODE","")))
		{
			cmd = trans.addCustomCommand("GEN","Pm_Event_Plan_API.Generate__");
			cmd.addParameter("CONTRACT");
			cmd.addParameter("CALL_CODE");

			trans=mgr.perform(trans);
			trans.clear();
			String curr_url = mgr.getURL();
			int end_pnt = curr_url.indexOf("/EventGenerationDlg");
			String fst_part = curr_url.substring(0,end_pnt); 
			int end_pnt1 = fst_part.lastIndexOf("/");
			String snd_part = fst_part.substring(0,end_pnt1+1);
			String new_url = snd_part + "Default.page";
			mgr.redirectTo(new_url);
		}
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();

		String curr_url = mgr.getURL();
		int end_pnt = curr_url.indexOf("/EventGenerationDlg");
		String fst_part = curr_url.substring(0,end_pnt); 
		int end_pnt1 = fst_part.lastIndexOf("/");
		String snd_part = fst_part.substring(0,end_pnt1+1);
		String new_url = snd_part + "Default.page";
		mgr.redirectTo(new_url);
		headtbl.clearQueryRow();
		headset.setFilterOff();
	}

//-----------------------------------------------------------------------------
//--------------------------PREDEFINITION FUNCTION-----------------------------
//-----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden().
		setFunction("''");

		headblk.addField("OBJVERSION").
		setHidden().
		setFunction("''");

		headblk.addField("CONTRACT").
		setLabel("PCMWEVENTGENERATIONDLGWOSITE: WO Site").
		setSize(11).
		setMaxLength(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
		setLOVProperty("TITLE",mgr.translate("PCMWEVENTGENERATIONDLGCONTRACTLIST: List of Sites")).
		setFunction("''");

		headblk.addField("CALL_CODE").
		setSize(11).
		setMandatory().
		setDynamicLOV("MAINTENANCE_EVENT",600,450).
		setLOVProperty("TITLE",mgr.translate("PCMWEVENTGENERATIONDLGEVNTLST: List of Event")).
		setCustomValidation("CALL_CODE","CALL_SEQUENCE"). 
		setLabel("PCMWEVENTGENERATIONDLGEVENT: Event").
		setUpperCase().
		setFunction("''");

		headblk.addField("CALL_SEQUENCE").
		setSize(11).
		setLabel("PCMWEVENTGENERATIONDLGCASEQ: Sequence").
		setReadOnly().
		setFunction("''");

		headblk.setView("DUAL");                                                                       
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields()");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWEVENTGENERATIONDLGGRPLABEL1: Parameters"),"CONTRACT,CALL_CODE,CALL_SEQUENCE",true,true);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWEVENTGENERATIONDLGTITLE: Event Generation";
	}

	protected String getTitle()
	{
		return "PCMWEVENTGENERATIONDLGTITLE: Event Generation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>\n");
		appendToHTML(headbar.showBar());
		appendToHTML("  </td>\n");
		appendToHTML(" </tr>\n");
		appendToHTML("</table>\n");
		appendToHTML("<table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>&nbsp;&nbsp;</td>\n");
		appendToHTML("  <td>\n");
		appendToHTML(fmt.drawReadLabel("PCMWEVENTGENERATIONDLGINFOTEXT1: This job will be started as a background job. You will find the status of the job in the Background jobs window, in the General folder in the Navigator."));
		appendToHTML("  </td>\n");
		appendToHTML(" </tr> \n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>&nbsp;&nbsp;</td>\n");
		appendToHTML("  <td>\n");
		appendToHTML(fmt.drawReadLabel("PCMWEVENTGENERATIONWOSITEMPT: In order to generate for all sites, leave the field empty."));
		appendToHTML("  </td>\n");
		appendToHTML(" </tr> \n");
		appendToHTML("</table>\n");
		appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>\n");
		appendToHTML(headlay.generateDataPresentation());
		appendToHTML("  </td>\n");
		appendToHTML(" </tr>\n");
		appendToHTML("	</table>\n");

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}

}
