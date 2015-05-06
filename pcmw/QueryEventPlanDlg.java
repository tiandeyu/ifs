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
*  File        : QueryEventPlanDlg.java 
*  Modified    :
*    SHFELK   2001-03-29  - Created 
*    JEWI           2001-04-03  - Changed file extensions from .asp to .page
*    JEWILK         2003-09-26    Corrected overridden javascript function toggleStyleDisplay(). 
*    ARWILK         031222        Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class QueryEventPlanDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.QueryEventPlanDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String OkFlag;
	private String CancelFlag;
	private String strCallCode;
	private String CallSequence;
	private String val;
	private ASPCommand cmd;
	private String mch;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public QueryEventPlanDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();  

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		OkFlag = "";
		CancelFlag = "";
		strCallCode = "";
		CallSequence = "";  

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.buttonPressed("OK"))
			ok();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();

		okFind();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("CALL_CODE".equals(val))
		{
			cmd=trans.addCustomFunction("ESEQ","Maintenance_Event_API.Get_Sequence","CALL_SEQUENCE");
			cmd.addParameter("CALL_CODE");
			trans=mgr.validate(trans);
			mch=trans.getValue("ESEQ/DATA/CALL_SEQUENCE");
			mgr.responseWrite(mch);
		}

		mgr.endResponse();
		trans.clear();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWQUERYEVENTPLANDLGNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void  cancel()
	{
		CancelFlag = "true";
	}

	public void  ok()
	{
		ASPManager mgr = getASPManager();

		strCallCode = mgr.URLEncode(mgr.readValue("CALL_CODE",""));
		CallSequence = mgr.URLEncode(mgr.readValue("CALL_SEQUENCE",""));


		if (( "".equals(mgr.readValue("CALL_CODE","")) ) ||  ( "".equals(mgr.readValue("CALL_SEQUENCE","")) ))
			mgr.showAlert(mgr.translate("PCMWQUERYEVENTPLANDLGNOPARA: Parameter(s) must have values" ));
		else
			OkFlag = "true";
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden().
		setFunction("''");

		headblk.addField("OBJVERSION").
		setHidden().
		setFunction("''");

		headblk.addField("CALL_CODE").
		setSize(11).
		setDynamicLOV("MAINTENANCE_EVENT",600,445).
		setCustomValidation("CALL_CODE","CALL_SEQUENCE").
		setLabel("PCMWQUERYEVENTPLANDLGCALLCODE: Event").
		setUpperCase().
		setFunction("''");

		headblk.addField("CALL_SEQUENCE").
		setSize(11).
		setLabel("PCMWQUERYEVENTPLANDLGCALLSEQ: Sequence"). 
		setFunction("''");

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();
		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWQUERYEVENTPLANDLGGRPLABEL1: Choose Event and Sequence"),"CALL_CODE,CALL_SEQUENCE",true,true);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWQUERYEVENTPLANDLGTITLE: Query Event Plan";
	}

	protected String getTitle()
	{
		return "PCMWQUERYEVENTPLANDLGTITLE: Query Event Plan";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("  <table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" >\n");
		appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWQUERYEVENTPLANDLGOKBUTTON:   OK  "),"OK"));
		appendToHTML("&nbsp;&nbsp;");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWQUERYEVENTPLANDLGCANCELBUTTON: Cancel"),"Cancel"));
		appendToHTML("  </table>\n");

		appendDirtyJavaScript("window.name = \"frmQryEventPlan\";\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(OkFlag);
		appendDirtyJavaScript("'=='true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   qryStr = \"PmActionOvw2.page?CALL_CODE=\"+'");
		appendDirtyJavaScript(strCallCode);
		appendDirtyJavaScript("'+\"&CALL_SEQUENCE=\"+'");
		appendDirtyJavaScript(CallSequence);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("   window.open(qryStr,\"frmEventPlan\",\"\");\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(CancelFlag);
		appendDirtyJavaScript("'=='true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   el = document.getElementById(el);\n");
		appendDirtyJavaScript("   if(el.style.display!='none')\n");
		appendDirtyJavaScript("   {  \n");
		appendDirtyJavaScript("      el.style.display='none';\n");
		appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
		appendDirtyJavaScript("   }  \n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("   {  \n");
		appendDirtyJavaScript("      el.style.display='block';\n");
		appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");
	}
}
