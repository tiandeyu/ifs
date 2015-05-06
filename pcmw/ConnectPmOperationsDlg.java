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
*  File        : ConnectPmOperationsDlg.java 
*  Created     : THWILK  050521  Created
*  Modified    :
*  THWILK  050630  Modified methods run(),adjust() and printContents(). 
*  THWILK  050707  Modified methods run() and printContents(). 
*  THWILK  050719  Modified printContents(). 
*  ILSOLK  070709  Eliminated XSS.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConnectPmOperationsDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ConnectPmOperationsDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
        private ASPCommand cmd;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
        private String cancelFlag;
        private String pmNo;
        private String pmRev;
        private String jobId;
        private boolean bCloseWindow;
        private String frameName;
        private String qryStr;
        private String calling_url;

	//===============================================================
	// Construction 
	//===============================================================
	public ConnectPmOperationsDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

                pmNo      = ctx.readValue("PM_NO", "");
                pmRev     = ctx.readValue("PM_REVISION", "");
                jobId     = ctx.readValue("JOB_ID", "");
                frameName = ctx.readValue("FRAMENAME","");
		qryStr    = ctx.readValue("QRYSTR","");
                calling_url = ctx.readValue("CALLING_URL","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("SUBMIT"))
			submit();
		else if (mgr.buttonPressed("BACK"))
			cancel();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
                else if ((!mgr.isEmpty(mgr.getQueryStringValue("PM_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION"))) && (!mgr.isEmpty(mgr.getQueryStringValue("JOB_ID"))))
               {
                       pmNo      = mgr.getQueryStringValue("PM_NO");
                       pmRev     = mgr.getQueryStringValue("PM_REVISION");
                       jobId     = mgr.getQueryStringValue("JOB_ID");
                       frameName = mgr.readValue("FRMNAME","");
                       qryStr    = mgr.readValue("QRYSTR","");
                       calling_url=ctx.getGlobal("CALLING_URL");
                       calling_url= calling_url+"?&PM_NO="+pmNo+"&PM_REVISION="+pmRev;
               }

	       else
		  clear();

		adjust();
                ctx.writeValue("PM_NO", pmNo);
                ctx.writeValue("PM_REVISION", pmRev);
                ctx.writeValue("JOB_ID", jobId);
                ctx.writeValue("FRAMENAME",frameName);
		ctx.writeValue("QRYSTR",qryStr);
                ctx.writeValue("CALLING_URL",calling_url);
	 }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void cancel()
	{
		cancelFlag = "TRUE";
	}

	public void submit()
	{
                ASPManager mgr = getASPManager();
                cmd = trans.addCustomCommand("CONOPERATION","Pm_Action_Role_API.Connect_Job");
		cmd.addParameter("PM_NO",pmNo);
                cmd.addParameter("PM_REVISION",pmRev);
		cmd.addParameter("ROW_NO", mgr.readValue("ROW_NO"));
		cmd.addParameter("JOB_ID", jobId);

		trans = mgr.perform(trans);  
                bCloseWindow = true;

	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void clear()
	{
		headset.clear();
	}   

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("ROW_NO", "Number").
		setLabel("PCMWCONOPERATIONSDLGROWNO: Operation No").
		setInsertable();

		headblk.addField("PM_NO", "Number", "#").
		setSize(20).
		setHidden().
		setFunction("''");

		headblk.addField("PM_REVISION").
		setSize(20).
		setMaxLength(6).
		setHidden().
		setFunction("''");

		headblk.addField("JOB_ID", "Number").
		setSize(11).
		setHidden().
		setFunction("''");


		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELNEW); 

		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELNEW,"cancel");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setDialogColumns(2);
		headlay.setEditable();   
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		mgr.getASPField("ROW_NO").setDynamicLOV("PM_ACTION_ROLE_OP",600,445);
		mgr.getASPField("ROW_NO").setLOVProperty("WHERE","PM_NO= '" + pmNo + "' AND PM_REVISION= '" + pmRev + "' AND JOB_ID IS NULL");  
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCONPMOPERATIONSDLGTITLE: Connect an Existing Operation";
	}

	protected String getTitle()
	{
		return "PCMWPMCONOPERATIONSDLGTITLE: Connect an Existing Operation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
                
		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(cancelFlag);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n"); 
               
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(bCloseWindow);
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("if (");
                appendDirtyJavaScript(!mgr.isEmpty(qryStr));
                appendDirtyJavaScript(")\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qryStr)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("' + \"&PMQUALIFICATION=1\",'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("else\n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open('");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url)); // XSS_Safe ILSOLK 20070709
                appendDirtyJavaScript("','");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(frameName)); // XSS_Safe ILSOLK 20070709
                appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                appendDirtyJavaScript("	self.close();\n");  
                appendDirtyJavaScript("}\n");
                appendDirtyJavaScript("}\n");


	}
}
