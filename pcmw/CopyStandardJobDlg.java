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
*  File        : CopyStandardJobDlg.java 
*  Modified    :
*  VAGULK  020326  Created.
*  CHAMLK  031020  Added Created By to this dialog, and passed it as a parameter to 
*                  method call Standard_Job_Utility_API.Copy_Standard_Job.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040805  Introduced new field SOURCESTDJOBCOMP and lovNewsourcestdjobcont was overwritten.
*  NAMELK  041105  Non Standard translation tags corrected.
* ----------------------------------------------------------------------------
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  SHAFLK  080303  Bug 71678, Modified okFind() and preDefine() methods. 
*  NIJALK  091207  Bug 87292, Modified preDefine().
*  NIJALK  100330  Bug 87935, Added dummy column DUMMY1 to get correct pres objects.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CopyStandardJobDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CopyStandardJobDlg");

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
	private String stdjobid;
	private String stdjobrev;
	private String stdjobcont;
	private String stdjobCompany;
	private String stdjobCreatedBy;
	private boolean selfClose;

	//===============================================================
	// Construction 
	//===============================================================
	public CopyStandardJobDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		stdjobid = ctx.readValue("STDJOBID","");
		stdjobcont = ctx.readValue("STDJOBCONTRACT","");
		stdjobrev = ctx.readValue("STDJOBREVISION","");
		stdjobCompany = ctx.readValue("STDCOMPANY","");
		stdjobCreatedBy = ctx.readValue("STDCREATEDBY","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_ID")))
		{
			stdjobid   = mgr.getQueryStringValue("STD_JOB_ID");
			stdjobrev   = mgr.getQueryStringValue("STD_JOB_REVISION");
			stdjobcont   = mgr.getQueryStringValue("CONTRACT");
			stdjobCompany = mgr.getQueryStringValue("COMPANY");
			stdjobCreatedBy = mgr.getQueryStringValue("CREATED_BY");
			okFind();
		}


		ctx.writeValue("STDJOBID",stdjobid);
		ctx.writeValue("STDJOBCONT",stdjobcont);
		ctx.writeValue("STDJOBREV",stdjobrev);
		ctx.writeValue("STDCOMPANY",stdjobCompany);
		ctx.writeValue("STDCREATEDBY",stdjobCreatedBy);
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		cmd = trans.addCustomFunction("GETCOMP","SITE_API.Get_Company","SOURCESTDJOBCOMP");
		cmd.addParameter("SOURCESTDJOBCONT", stdjobcont);

		trans = mgr.perform(trans);

		String sCompany = trans.getValue("GETCOMP/DATA/SOURCESTDJOBCOMP");

		ASPBuffer row = headset.getRow();
		row.setValue("SOURCESTDJOBID",stdjobid);
		row.setValue("SOURCESTDJOBCONT",stdjobcont);
		row.setValue("SOURCESTDJOBCOMP",sCompany);
		row.setValue("SOURCESTDJOBREV",stdjobrev);
		row.setValue("NEWSOURCESTDJOBREV","1");
                //Bug 71678, start
		row.setValue("NEWSOURCESTDJOBCONT",stdjobcont);
                //Bug 71678, end
		row.setValue("COMP",stdjobCompany);
		row.setValue("CREATEDBY",stdjobCreatedBy);
		headset.setRow(row);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWCOPYSTANDARDJOBDLGNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void ok()
	{
		String oldstdjobid = null;
		String oldstdjobcont = null;
		String oldstdjobrev = null;
		String newstdjobid = null;
		String newstdjobcont = null;
		String newstdjobrev = null;

		ASPManager mgr = getASPManager();

		oldstdjobid = mgr.readValue("SOURCESTDJOBID","");
		oldstdjobcont = mgr.readValue("SOURCESTDJOBCONT","");
		oldstdjobrev = mgr.readValue("SOURCESTDJOBREV","");
		newstdjobid = mgr.readValue("NEWSOURCESTDJOBID","");
		newstdjobcont = mgr.readValue("NEWSOURCESTDJOBCONT","");
		newstdjobrev = mgr.readValue("NEWSOURCESTDJOBREV","");

		if (!(( "".equals(oldstdjobid) ) ||  ( "".equals(newstdjobid) ||  ( "".equals(oldstdjobcont) ) ||  ( "".equals(newstdjobcont) ) ||  ( "".equals(oldstdjobrev) ) ||  ( "".equals(newstdjobrev) ) )))
		{
			cmd = trans.addCustomCommand("COPYACT","Standard_Job_Utility_API.Copy_Standard_Job");
			cmd.addParameter("SOURCESTDJOBID");
			cmd.addParameter("SOURCESTDJOBCONT");
			cmd.addParameter("SOURCESTDJOBREV");
			cmd.addParameter("NEWSOURCESTDJOBID");
			cmd.addParameter("NEWSOURCESTDJOBCONT");
			cmd.addParameter("NEWSOURCESTDJOBREV");
			cmd.addParameter("CREATEDBY");
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

		/*String val = mgr.readValue("VALIDATE");
		String txt;

		if ("SOURCESTDJOBCONT".equals(val))
		{
			cmd = trans.addCustomFunction("GETCOMP","SITE_API.Get_Company","SOURCESTDJOBCOMP");
			cmd.addParameter("SOURCESTDJOBCONT");

			trans = mgr.validate(trans);

			String sCompany = trans.getValue("GETCOMP/DATA/SOURCESTDJOBCOMP");

			txt = (mgr.isEmpty(sCompany) ? "" : (sCompany))+ "^";

			mgr.responseWrite(txt);
		}*/

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

		//Bug 87292, Start, Increased the length
		headblk.addField("SOURCESTDJOBID").
		setMandatory().
		setLabel("PCMWCOPYSTANDARDJOBDLGOLD: Standard Job ID ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		setMaxLength(12);
		//Bug 87292, End

		headblk.addField("SOURCESTDJOBREV").
		setSize(12).
		setMandatory(   ).
		setLabel("PCMWCOPYSTANDARDJOBREVGOLD: Revision ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		setMaxLength(10);


		headblk.addField("SOURCESTDJOBCONT").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYSTANDARDJOBCONTGOLD: Site ").
		setUpperCase().
		setFunction("''").
		setReadOnly().
		//setCustomValidation("SOURCESTDJOBCONT","SOURCESTDJOBCOMP").
		setMaxLength(10);

		headblk.addField("SOURCESTDJOBCOMP").
		setHidden().
		setFunction("''");

		//Bug 87292, Start, Increased the length
		headblk.addField("NEWSOURCESTDJOBID").
                setSize(15).
		setMandatory().
		setLabel("PCMWCOPYSTANDARDJOBDLGNEWJOBID: New Standard Job ID").
		setUpperCase().
		setFunction("''").
		setMaxLength(12);

		headblk.addField("NEWSOURCESTDJOBREV").
		setSize(15).
		setMandatory().
		setLabel("PCMWCOPYSTANDARDJOBDLGNEWJOBREV: Revision").
		setUpperCase().
		setFunction("''").
		setMaxLength(10);
		//Bug 87292, End

		headblk.addField("NEWSOURCESTDJOBCONT").
		setSize(12).
		setMandatory().
		setLabel("PCMWCOPYSTANDARDJOBDLGNEWJOBCONT: Site").
                setReadOnly().
		setUpperCase().
		setFunction("''").
		setMaxLength(10);

		headblk.addField("COMP").
		setSize(6).
		setHidden().
		setFunction("''").
		setUpperCase().
		setMaxLength(20);

		//Bug 87292, Start, Increased the length
		headblk.addField("CREATEDBY").
		setLabel("PCMWCOPYSTANDARDJOBDLGCREBY: Created By ").
		setUpperCase().
		setFunction("''").
		setSize(15).
		setDynamicLOV("EMPLOYEE_LOV","COMP COMPANY",600,450).
		setLOVProperty("TITLE","PCMWCOPYSTANDARDJOBDLGLSTCREBY: List of Created by").
		setMaxLength(20);
		//Bug 87292, End

		//Bug 87935, Start, Added dummy column to get correct pres objects
		headblk.addField("DUMMY1").
		setFunction("''").
		setDynamicLOV("USER_ALLOWED_SITE_LOV").
		setHidden();
		//Bug 87935, End

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWCOPYSTANDARDJOBDLGCPYFRM: Copy From"),"SOURCESTDJOBID,SOURCESTDJOBREV,SOURCESTDJOBCONT",true,true);
		headlay.defineGroup(mgr.translate("PCMWCOPYSTANDARDJOBDLGCPYTO: Copy To"),"NEWSOURCESTDJOBID,NEWSOURCESTDJOBREV,NEWSOURCESTDJOBCONT",true,true);
		headlay.defineGroup("","CREATEDBY",false,true);   


		headtbl = mgr.newASPTable(headblk);

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCOPYSTANDARDJOBDLGCPYSTDJOB: Copy Standard Job ";
	}

	protected String getTitle()
	{
		return "PCMWCOPYSTANDARDJOBDLGCPYSTDJOB: Copy Standard Job ";
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
		appendDirtyJavaScript("	var key_value = (getValue_('NEWSOURCESTDJOBCONT',i).indexOf('%') !=-1)? getValue_('NEWSOURCESTDJOBCONT',i):'';\n");
		appendDirtyJavaScript("	openLOVWindow('NEWSOURCESTDJOBCONT',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Site&__INIT=1&__WHERE='");
                appendDirtyJavaScript("         + URLClientEncode(\"SITE_API.Get_Company(CONTRACT) = '\"+ URLClientEncode(getValue_('SOURCESTDJOBCOMP',i)) + \"'\")");
		appendDirtyJavaScript("         + '&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('NEWSOURCESTDJOBCONT',i))\n");
		appendDirtyJavaScript("		+ '&NEWSOURCESTDJOBCONT=' + URLClientEncode(key_value)\n");
		appendDirtyJavaScript("		,600,445,'validateNewsourcestdjobcont');\n");
		appendDirtyJavaScript("}\n");

		if (selfClose)
		{
			appendDirtyJavaScript("self.close();");
		}
	}

}
