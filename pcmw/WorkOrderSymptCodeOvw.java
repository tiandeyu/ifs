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
*  File        : WorkOrderSymptCodeOvw.java 
*  Modified    :
*    SHFELK     2001-Feb-14  - Created.
*    ARWILK     031223         Edge Developments - (Removed clone and doReset Methods)
*    THWILK     060130         Call ID 132211,Modified preDefine() and added duplicateRow() method. 
*    THWILK     060131         Call ID 132213,Modified preDefine(). 

* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderSymptCodeOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderSymptCodeOvw");

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
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderSymptCodeOvw(ASPManager mgr, String page_path)
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
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
			okFind();

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
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_SYMPT_CODE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

        public void duplicateRow()
        {
             ASPManager mgr = getASPManager();

             if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());
             else
                headset.selectRow();

             headlay.setLayoutMode(headlay.NEW_LAYOUT);

             trans.clear();

             cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_SYMPT_CODE_API.New__",headblk);
             cmd.setOption("ACTION","PREPARE");
             trans = mgr.perform(trans);
             data = trans.getBuffer("HEAD/DATA");
             data.setFieldItem("ERR_SYMPTOM",headset.getValue("ERR_SYMPTOM"));
             data.setFieldItem("DESCRIPTION",headset.getValue("DESCRIPTION"));

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
			mgr.showAlert(mgr.translate("PCMWWORKORDERSYMPTCODEOVWNODATA: No data found."));
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

		f = headblk.addField("ERR_SYMPTOM");
		f.setSize(10);
		f.setMandatory();
		f.setReadOnly();
		f.setMaxLength(3);
		f.setLabel("PCMWWORKORDERSYMPTCODEOVWERRSYMP: Symptom");
		f.setUpperCase();

		f = headblk.addField("DESCRIPTION");
                f.setMandatory();
		f.setSize(51);
		f.setMaxLength(60);
		f.setLabel("PCMWWORKORDERSYMPTCODEOVWDESC: Description");

		headblk.setView("WORK_ORDER_SYMPT_CODE");
		headblk.defineCommand("WORK_ORDER_SYMPT_CODE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);

		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
                headbar.defineCommand(headbar.DUPLICATEROW,"duplicateRow");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 
		headlay.setDialogColumns(2);    

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERSYMPTCODEOVWHD: Symptoms"));
		headtbl.setWrap();
	}

	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isNewLayout())

			mgr.getASPField("ERR_SYMPTOM").unsetReadOnly();

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
		return "PCMWWORKORDERSYMPTCODEOVWTITLE: Symptoms";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERSYMPTCODEOVWTITLE: Symptoms";
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
