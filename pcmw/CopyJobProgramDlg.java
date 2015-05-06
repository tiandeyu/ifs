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
*  File        : CopyJobProgramDlg.java 
*  Modified    :
*  Chanlk  041201  Created.
*  NIJALK  041220  Modified predefine(),printContents().
*  THWILK  060126  Corrected localization errors.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CopyJobProgramDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CopyJobProgramDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;
	private ASPCommand cmd;
	private String jobprogramid;
	private String jobprogramrev;
	private String jobprogramcont;
	private String jobprogramCompany;
	private boolean selfClose;

	//===============================================================
	// Construction 
	//===============================================================
	public CopyJobProgramDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		jobprogramid = ctx.readValue("JOBPROGRAMID","");
		jobprogramcont = ctx.readValue("JOBPROGRAMCONTRACT","");
		jobprogramrev = ctx.readValue("JOBPROGRAMREVISION","");
		jobprogramCompany = ctx.readValue("JOBPROGRAMCOMPANY","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_ID")))
		{
			jobprogramid   = mgr.getQueryStringValue("JOB_PROGRAM_ID");
			jobprogramrev   = mgr.getQueryStringValue("JOB_PROGRAM_REVISION");
			jobprogramcont   = mgr.getQueryStringValue("JOB_PROGRAM_CONTRACT");
			jobprogramCompany = mgr.getQueryStringValue("COMPANY");
			okFind();
		}


		ctx.writeValue("JOBPROGRAMID",jobprogramid);
		ctx.writeValue("JOBPROGRAMCONT",jobprogramcont);
		ctx.writeValue("JOBPROGRAMREV",jobprogramrev);
		ctx.writeValue("JOBPROGRAMCOMPANY",jobprogramCompany);
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		cmd = trans.addCustomFunction("GETCOMP","SITE_API.Get_Company","SOURCEJOBPROGRAMCOMP");
		cmd.addParameter("SOURCEJOBPROGRAMCONT", jobprogramcont);

		trans = mgr.perform(trans);

		String sCompany = trans.getValue("GETCOMP/DATA/SOURCEJOBPROGRAMCOMP");

		ASPBuffer row = headset.getRow();
		row.setValue("SOURCEJOBPROGRAMID",jobprogramid);
		row.setValue("SOURCEJOBPROGRAMCONT",jobprogramcont);
		row.setValue("SOURCEJOBPROGRAMCOMP",sCompany);
		row.setValue("SOURCEJOBPROGRAMREV",jobprogramrev);
		row.setValue("NEWSOURCEJOBPROGRAMREV","1");
		row.setValue("NEWSOURCEJOBPROGRAMCONT",jobprogramcont);
		row.setValue("COMP",jobprogramCompany);
		headset.setRow(row);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWCOPYJOBPROGRAMDLGNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void ok()
	{
		String oldjobprogramid = null;
		String oldjobprogramcont = null;
		String oldjobprogramrev = null;
		String newjobprogramid = null;
		String newjobprogramcont = null;
		String newjobprogramrev = null;

		ASPManager mgr = getASPManager();

		oldjobprogramid = mgr.readValue("SOURCEJOBPROGRAMID","");
		oldjobprogramcont = mgr.readValue("SOURCEJOBPROGRAMCONT","");
		oldjobprogramrev = mgr.readValue("SOURCEJOBPROGRAMREV","");
		newjobprogramid = mgr.readValue("NEWSOURCEJOBPROGRAMID","");
		newjobprogramcont = mgr.readValue("NEWSOURCEJOBPROGRAMCONT","");
		newjobprogramrev = mgr.readValue("NEWSOURCEJOBPROGRAMREV","");

		if (!(( "".equals(oldjobprogramid) ) ||  ( "".equals(newjobprogramid) ||  ( "".equals(oldjobprogramcont) ) ||  ( "".equals(newjobprogramcont) ) ||  ( "".equals(oldjobprogramrev) ) ||  ( "".equals(newjobprogramrev) ) )))
		{
			cmd = trans.addCustomCommand("COPYACT","Job_Program_Utility_API.Copy_Job_Program");
			cmd.addParameter("SOURCEJOBPROGRAMID");
			cmd.addParameter("SOURCEJOBPROGRAMREV");
			cmd.addParameter("SOURCEJOBPROGRAMCONT");
			cmd.addParameter("NEWSOURCEJOBPROGRAMID");
			cmd.addParameter("NEWSOURCEJOBPROGRAMREV");
			cmd.addParameter("NEWSOURCEJOBPROGRAMCONT");
			trans=mgr.perform(trans);
		}

		headtbl.clearQueryRow();
		headset.setFilterOff();
		headset.unselectRows();       
		selfClose = true;
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();
		selfClose = true;
		//mgr.redirectTo(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+"Default.page");       
		headtbl.clearQueryRow();
		headset.setFilterOff();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		mgr.endResponse();
	}


	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden().
		setFunction("''");

		headblk.addField("OBJVERSION").
		setFunction("''").
		setHidden();

		headblk.addField("SOURCEJOBPROGRAMID").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYJOBPROGRAMDLGOLD: Job Program ID ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		setMaxLength(10);


		headblk.addField("SOURCEJOBPROGRAMREV").
		setSize(12).
		setMandatory(   ).
		setLabel("PCMWCOPYJOBPROGRAMREVGOLD: Revision ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		setMaxLength(10);


		headblk.addField("SOURCEJOBPROGRAMCONT").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYJOBPROGRAMCONTGOLD: Site ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		setMaxLength(10);

		headblk.addField("SOURCEJOBPROGRAMCOMP").
		setHidden().
		setFunction("''");

		headblk.addField("NEWSOURCEJOBPROGRAMID").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYJOBPROGRAMDLGNEWJOBID: New Job Program ID").
		setUpperCase().
		setFunction("''").
		setMaxLength(10);


		headblk.addField("NEWSOURCEJOBPROGRAMREV").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYJOBPROGRAMDLGNEWJOBREV: Revision").
		setUpperCase().
		setFunction("''").
		setMaxLength(10);


		headblk.addField("NEWSOURCEJOBPROGRAMCONT").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYJOBPROGRAMDLGNEWJOBCONT: Site").
		setUpperCase().
		setReadOnly().
		setFunction("''").
		setMaxLength(10);

		headblk.addField("COMP").
		setSize(6).
		setHidden().
		setFunction("''").
		setUpperCase().
		setMaxLength(20);

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELNEW);
		headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWCOPYJOBPROGRAMSDLGGRPLABEL1: Copy From"),"SOURCEJOBPROGRAMID,SOURCEJOBPROGRAMREV,SOURCEJOBPROGRAMCONT",true,true);
		headlay.defineGroup(mgr.translate("PCMWCOPYJOBPROGRAMSDLGGRPLABEL2: Copy To"),"NEWSOURCEJOBPROGRAMID,NEWSOURCEJOBPROGRAMREV,NEWSOURCEJOBPROGRAMCONT",true,true);


		headtbl = mgr.newASPTable(headblk);

 
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCOPYJOBPROGRAMDLGTITLE: Copy Job Program";
	}

	protected String getTitle()
	{
		return "PCMWCOPYJOBPROGRAMDLGTITLE: Copy Job Program";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("function lovNewsourcestdjobcont(i,params)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if(params) param = params;\n");
		appendDirtyJavaScript("	else param = '';\n");
		appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
		appendDirtyJavaScript("	var key_value = (getValue_('NEWSOURCEJOBPROGRAMCONT',i).indexOf('%') !=-1)? getValue_('NEWSOURCEJOBPROGRAMCONT',i):'';\n");
		appendDirtyJavaScript("	openLOVWindow('NEWSOURCEJOBPROGRAMCONT',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Site&__INIT=1&__WHERE='");
                appendDirtyJavaScript("         + URLClientEncode(\"SITE_API.Get_Company(CONTRACT) = '\"+ URLClientEncode(getValue_('SOURCEJOBPROGRAMCOMP',i)) + \"'\")");
		appendDirtyJavaScript("         + '&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('NEWSOURCEJOBPROGRAMCONT',i))\n");
		appendDirtyJavaScript("		+ '&NEWSOURCEJOBPROGRAMCONT=' + URLClientEncode(key_value)\n");
		appendDirtyJavaScript("		,600,445,'validateNewsourcejobprogramcont');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(selfClose);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
	}

}
