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
*  File        : ExtendPlanDlg.java 
*  Modified    :
*    SHFELK  2001-Feb-15  - Created.
*    Chamlk  2001-Oct-18  - Modified function Submit to fetch the current year.
*    ARWILK  031222         Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ExtendPlanDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ExtendPlanDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
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
	private ASPCommand cmd;

	//===============================================================
	// Construction 
	//===============================================================
	public ExtendPlanDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		String val = null;
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  submit()
	{
		String sYear = null;
		String uYear = null;
		ASPManager mgr = getASPManager();

		//cmd = trans.addQuery("REPBY","SELECT to_number(to_char(sysdate,'YYYY')) FROM dual");
		//trans = mgr.perform(trans);
		//sYear = trans.getValue("REPBY/DATA/TO_NUMBER(TO_CHAR(SYSDATE,'YYYY'))");

		trans.clear();
		cmd = trans.addCustomFunction("CURRYEAR","PM_ACTION_CALENDAR_PLAN_API.Get_Curr_Year","CURR_YEAR");
		trans = mgr.perform(trans);
		sYear = trans.getValue("CURRYEAR/DATA/CURR_YEAR");

		trans.clear();

		uYear = mgr.readValue("YEAR","");


		if ((toInt(uYear) < toInt(sYear)) || (toInt(uYear) > 2099))
		{
			mgr.showAlert(mgr.translate("ERROR1:This year is not a valid extension"));
		}
		else
		{
			cmd = trans.addCustomCommand("GEN1","Pm_Action_Calendar_Plan_API.Extend_Plan");
			cmd.addParameter("YEAR",uYear);
			trans=mgr.perform(trans);
			mgr.redirectTo("../Default.page");
		}  
	}

	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		mgr.redirectTo("../Default.page");
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("YEAR", "Number");
		f.setSize(6);
		f.setLabel("PCMWEXTENDPLANDLGYEAR: New Year Limit (YYYY)");
		f.setFunction("''");

		f = headblk.addField("CURR_YEAR");
		f.setFunction("''");
		f.setHidden();

		headblk.setView("");
		headblk.defineCommand("MS_CALL_CODE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWEXTENDPLANDLGGRPLABEL: Parameter"),"YEAR",true,true);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWEXTENDPLANDLGTITLE: Extend PM Plan to New Horizen";
	}

	protected String getTitle()
	{
		return "PCMWEXTENDPLANDLGTITLE: Extend PM Plan to New Horizen";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}

}
