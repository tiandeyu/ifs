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
*  File        : CriteriaGeneration.java 
*  Modified    :
*  SHFELK  010215  Created.
*  BUNILK  010720  Made some changes in redirect way of submit() and cancel() methods, to avoid 
*                  problems in Netscape browser 
*  CHCRLK  011011  Changed the LOV of ORG_CODE. 
*  JEJALK  021212  Takeoff(Beta) modifications. Added WO Site.  
*  CHAMLK  031020  Removed setMandetory from field WO Site.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  ARWILK  040903  Modified changed the title and the method printContents.(IID AMEC111A: Std Jobs as PM Templates)
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
* ----------------------------------------------------------------------------
*/


package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CriteriaGeneration extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CriteriaGeneration");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;

	//===============================================================
	// Construction 
	//===============================================================
	public CriteriaGeneration(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD",".New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		cmd = trans.addCustomCommand("CRITGEN","Pm_Criteria_Plan_API.Generate__");
		cmd.addParameter("ORG_CODE");
		cmd.addParameter("CONTRACT");

		trans=mgr.perform(trans);
		trans.clear();
		String curr_url = mgr.getURL();
		int end_pnt = curr_url.indexOf("/CriteriaGeneration");
		String fst_part = curr_url.substring(0,end_pnt); 
		int end_pnt1 = fst_part.lastIndexOf("/");
		String snd_part = fst_part.substring(0,end_pnt1+1);
		String new_url = snd_part + "Default.page";
		mgr.redirectTo(new_url);
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();

		String curr_url = mgr.getURL();
		int end_pnt = curr_url.indexOf("/CriteriaGeneration");
		String fst_part = curr_url.substring(0,end_pnt); 
		int end_pnt1 = fst_part.lastIndexOf("/");
		String snd_part = fst_part.substring(0,end_pnt1+1);
		String new_url = snd_part + "Default.page";
		mgr.redirectTo(new_url);
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();    

		f = headblk.addField("CONTRACT");
		f.setSize(12);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setMaxLength(5);
		f.setLabel("PCMWACTIVESEPARATECONTRACT: WO Site");
		f.setUpperCase();

		f = headblk.addField("ORG_CODE");
		f.setSize(11);
		f.setMaxLength(8);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setLabel("PCMWCRITERIAGENERATIONORGCODE: Maintenance Organization");
		f.setUpperCase();

		headblk.setView("MODULE");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);


		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELNEW); 

		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWCONDITIONGENERATIONHEADTITLE: Condition Generation"));    

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();

		headlay.defineGroup(mgr.translate("PCMWCRITERIAGENERATIONGRPLABEL: Parameters"),"CONTRACT,ORG_CODE",true,true);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCONDITIONGENERATIONHEADTITLE: Condition Generation";
	}

	protected String getTitle()
	{
		return "PCMWCONDITIONGENERATIONHEADTITLE: Condition Generation";
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
		appendToHTML(fmt.drawReadLabel("PCMWCRITERIAGENERATIONNOTEEXPLA: This job will be started as a background job. You will find the status of the job in the background job window, in the General folder in the Navigator."));
		appendToHTML("  </td>\n");
		appendToHTML(" </tr> \n");
		appendToHTML(" <tr>\n");
		appendToHTML("  <td>&nbsp;&nbsp;</td>\n");
		appendToHTML("  <td>\n");
		appendToHTML(fmt.drawReadLabel("PCMWCRITERIAGENERATIONWOEMPT: In order to generate for all maintenance organizations and/or sites, leave the fields empty."));
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
