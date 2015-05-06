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
*  File        : MaintenanceEventOvw.java 
*  Modified    :
*    ASP2JAVA Tool  2001-02-12  - Created Using the ASP file MaintenanceEventOvw.asp
*    031222         ARWILK        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceEventOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceEventOvw");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

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
	public MaintenanceEventOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
			clear();

		adjust();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","MAINTENANCE_EVENT_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  clear()
	{

		headset.clear();
		headtbl.clearQueryRow();
	}


	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENANCEEVENTOVWNODATA: No data found."));
			headset.clear();
		}
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("CALL_CODE").
		setSize(12).
		setMandatory().
		setReadOnly().
		setInsertable().
		setMaxLength(10). 
		setLabel("PCMWMAINTENANCEEVENTOVWCALLCODE: Event").
		setUpperCase();

		headblk.addField("DESCRIPTION").
		setSize(60).
		setMandatory().
		setMaxLength(40). 
		setLabel("PCMWMAINTENANCEEVENTOVWDESCR: Description");

		headblk.addField("SEQUENCE","Number").
		setSize(18).
		setReadOnly().
		setLabel("PCMWMAINTENANCEEVENTOVWSEQ: Event Count");

		headblk.setView("MAINTENANCE_EVENT");
		headblk.defineCommand("MAINTENANCE_EVENT_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMAINTENANCEEVENTOVWHD: Events"));
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void  adjust()
	{

	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMAINTENANCEEVENTOVWTITLE: Events";
	}

	protected String getTitle()
	{
		return "PCMWMAINTENANCEEVENTOVWTITLE: Events";
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
