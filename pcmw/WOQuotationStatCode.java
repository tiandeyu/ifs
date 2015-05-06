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
*  File        : WOQuotationStatCode.java 
*  Created     : ARWILK  010215
*  Modified    :
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041112  Replaced getContents with printContents.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WOQuotationStatCode extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WOQuotationStatCode");

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
	private String n;

	//===============================================================
	// Construction 
	//===============================================================
	public WOQuotationStatCode(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.dataTransfered())
			okFind();
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
//---------------------------  CMDBAR FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","QUOTATION_STATISTICAL_CODE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		n = headset.getRow().getValue("N");
		headlay.setCountValue(toInt(n));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWOQUOTATIONSTATCODENODATA: No data found."));
			headset.clear();
		}

		mgr.createSearchURL(headblk);
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("STATISTICAL_CODE");
		f.setSize(15);
		f.setMandatory();
		f.setLabel("PCMWWOQUOTATIONSTATCODESTATCODE: Statistical Code");
		f.setUpperCase();
		f.setReadOnly();
		f.setInsertable();
		f.setMaxLength(10);

		f = headblk.addField("DESCRIPTION");
		f.setSize(60);
		f.setMandatory();
		f.setLabel("PCMWWOQUOTATIONSTATCODEDESCRIPTION: Description");
		f.setMaxLength(30);

		headblk.setView("QUOTATION_STATISTICAL_CODE");
		headblk.defineCommand("QUOTATION_STATISTICAL_CODE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWOQUOTATIONSTATCODEHD: Quotation Statistical Code"));
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWOQUOTATIONSTATCODEGRP: Quotation Statistical Code";
	}

	protected String getTitle()
	{
		return "PCMWWOQUOTATIONSTATCODEGRP: Quotation Statistical Code";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
	}
}
