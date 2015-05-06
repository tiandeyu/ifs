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
*  File        : MaintenancePriorityOvw.java 
*  Modified    :
*    ASP2JAVA Tool  2001-02-14  - Created Using the ASP file MaintenancePriorityOvw.asp
*    JEWI           2001-02-19  - Modified conversion errors.
*    ARWILK         031222        Edge Developments - (Removed clone and doReset Methods)
*    DIAMLK         2006-08-15  - Bug 58216, Eliminated SQL Injection security vulnerability.
*    AMNILK         2006-09-06  - Merged Bug Id: 58216.
*    ILSOLK         2007-04/25  - Added 'Weight' filed(Call ID 142974) 
*    ILSOLK         20070525    - Modified for Call ID 144538. 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenancePriorityOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenancePriorityOvw");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String priorityid;
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenancePriorityOvw(ASPManager mgr, String page_path)
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
		else if (!mgr.isEmpty(mgr.getQueryStringValue("PRIORITY_ID")))
		{
			priorityid = mgr.readValue("PRIORITY_ID");
			searchZoom();
		}

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

		cmd = trans.addEmptyCommand("HEAD","MAINTENANCE_PRIORITY_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		mgr.getASPField("PRIORITY_ID").setInsertable();
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

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
			mgr.showAlert(mgr.translate("PCMWMAINTENANCEPRIORITYOVWNODATA: No data found."));

		mgr.createSearchURL(headblk);
	}


	public void  searchZoom()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
                //Bug 58216 start
		q.addWhereCondition("PRIORITY_ID = ?");
                q.addParameter("PRIORITY_ID",priorityid);
                //Bug 58216 end
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
			mgr.showAlert(mgr.translate("PCMWMAINTENANCEPRIORITYOVWNODATA: No data found."));

		mgr.createSearchURL(headblk);
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PRIORITY_ID");
		f.setSize(10);
		f.setMandatory();
		f.setReadOnly();
		f.setMaxLength(1);
		f.setLabel("PCMWMAINTENANCEPRIORITYOVWPRIOID: Priority");
		f.setUpperCase();

		f = headblk.addField("DESCRIPTION");
		f.setSize(60);
		f.setMandatory();
		//f.setHeight(4);
                f.setMaxLength(40);
		f.setLabel("PCMWMAINTENANCEPRIORITYOVWDESC: Description");

                f = headblk.addField("WEIGHT");
		f.enumerateValues("WEIGHT_API");
		f.setSelectBox();
		f.setSize(10);
		f.setLabel("PCMWMAINTENANCEPRIORITYOVWWEIGHT: Weight");
		//f.setMandatory();
		f.setMaxLength(1);

		f = headblk.addField("WEIGHT_DB");
	        f.setHidden();

		headblk.setView("MAINTENANCE_PRIORITY");
		headblk.defineCommand("MAINTENANCE_PRIORITY_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  
		headlay.setDialogColumns(2);  

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMAINTENANCEPRIORITYOVWHD: Priorities"));
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");   
		headtbl.setWrap();
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
		return "PCMWMAINTENANCEPRIORITYOVWTITLE: Priorities";
	}

	protected String getTitle()
	{
		return "PCMWMAINTENANCEPRIORITYOVWTITLE: Priorities";
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
