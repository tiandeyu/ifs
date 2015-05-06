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
*  File        : RoundDefinitions.java 
*  Created     : ASP2JAVA Tool  010212
*  Modified    : 
*  031222  ARWILK  Edge Developments - (Removed clone and doReset Methods)
*  041112  ARWILK  Replaced getContents with printContents.
*  060130  THWILK  Call ID 132212, Modified Predefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class RoundDefinitions extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.RoundDefinitions");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
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
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public RoundDefinitions(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		adjust();
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

		cmd = trans.addEmptyCommand("HEAD","PM_ROUND_DEFINITION_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");

		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWROUNDDEFINITIONSNODATA: No data found."));
			headset.clear();
		}
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("ROUNDDEF_ID");
		f.setSize(16);
		f.setMandatory();
		f.setLabel("PCMWROUNDDEFINITIONSROUNDDEFID: Route ID");
		f.setUpperCase();
		f.setMaxLength(8);
		f.setReadOnly();
		f.setInsertable();      

		f = headblk.addField("DESCRIPTION");
		f.setSize(60);
		f.setMandatory();
		f.setLabel("PCMWROUNDDEFINITIONSDESCRIPTION: Description");
		f.setMaxLength(50);

		f = headblk.addField("CONTRACT");
		f.setSize(30);
                f.setMandatory();
		f.setLabel("PCMWROUNDDEFINITIONSCONTRACT: Site");
		f.setUpperCase();
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
		f.setMaxLength(5);

		headblk.setView("PM_ROUND_DEFINITION");
		headblk.defineCommand("PM_ROUND_DEFINITION_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWROUNDDEFINITIONSHD: Route Definitions"));
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWROUNDDEFINITIONSTITLE: Route Definitions";
	}

	protected String getTitle()
	{
		return "PCMWROUNDDEFINITIONSTITLE: Route Definitions";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("<!--\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("-->\n");
	}
}
