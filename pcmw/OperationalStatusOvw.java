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
*  File        : OperationalStatusOvw.java 
*  Modified    :
*    ASP2JAVA Tool  2001-02-13  - Created Using the ASP file OperationalStatusOvw.asp
*    JEWI           2001-02-19  - Modified conversion errors.
*    ARWILK         031219        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class OperationalStatusOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.OperationalStatusOvw");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private String val;
	private ASPBuffer data;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public OperationalStatusOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();


		if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
			startup();

		adjust();
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  startup()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addCustomCommand("OPERATIONAL_STATUS_TYPE","OPERATIONAL_STATUS_TYPE_API.Enumerate");
		cmd.addParameter("CLIENT_VALUES0");
		trans = mgr.submit(trans);
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

		cmd = trans.addEmptyCommand("HEAD","OPERATIONAL_STATUS_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);

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
			mgr.showAlert(mgr.translate("PCMWOPERATIONALSTATUSOVWNODATA: No data found."));
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

		f = headblk.addField("OP_STATUS_ID");
		f.setSize(18);
		f.setMandatory();
		f.setReadOnly();
		f.setInsertable();
		f.setLabel("PCMWOPERATIONALSTATUSOVWOSID: Operational Status");
		f.setUpperCase();
		f.setMaxLength(3);

		f = headblk.addField("DESCRIPTION");
		f.setSize(43);
		f.setMandatory();
		f.setLabel("PCMWOPERATIONALSTATUSOVWDESC: Description");
		f.setMaxLength(40);

		f = headblk.addField("OPERATIONAL_STATUS_TYPE");
		f.setSize(28);
                f.setMandatory();
                f.setLabel("PCMWOPERATIONALSTATUSOVWOSTYPE: Operational Status Type");
		f.setSelectBox();
		f.enumerateValues("OPERATIONAL_STATUS_TYPE_API");

		headblk.setView("OPERATIONAL_STATUS");
		headblk.defineCommand("OPERATIONAL_STATUS_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
		headbar.disableCommand(headbar.TOGGLE);
		headbar.enableCommand(headbar.SUBMIT);
		headbar.disableCommand(headbar.EDIT);
		headbar.enableRowStatus();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  
		headlay.setDialogColumns(1);    

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWOPERATIONALSTATUSOVWHD: Operational Status"));
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");   
		headtbl.setWrap();

		b = mgr.newASPBlock("OPERATIONAL_STATUS_TYPE");

		b.addField("CLIENT_VALUES0");
	}


	public void  adjust()
	{

		if (headset.countRows() == 0)
		{
			headbar.disableCommand(headbar.DELETE);
			headbar.disableCommand(headbar.EDITROW);
			headbar.disableCommand(headbar.DUPLICATEROW);

		}
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWOPERATIONALSTATUSOVWTITLE: Operational Status";
	}

	protected String getTitle()
	{
		return "PCMWOPERATIONALSTATUSOVWTITLE: Operational Status";
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
