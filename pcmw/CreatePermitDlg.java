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
*  File        : CreatePermitDlg.java 
*  Created     : ASP2JAVA Tool  010322
*  Modified    :  
*  BUNILK  010323  Corrected some conversion errors.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  THWILK  040824  Call 116923, modified run(),submit() and added javascripts.
*  ARWILK  041111  Replaced getContents with printContents.
*  NEKOLK  050920  B 127035:Removed CONNECTION_TYPE
*  AMDILK  070516  Call Id 144578: Modified preDefine()
*  CHANLK  070522  Call 144650 Permit tab not refresh properly
*  ILSOLK  070710  Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreatePermitDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreatePermitDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPHTMLFormatter fmt;
	private ASPCommandBar headbar;
	private ASPTable headtbl;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String wo_no;
	private String start;
	private boolean win_cls; 
	private boolean ok_sub;
	private boolean state;
	private ASPBuffer buff;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;
	private ASPBlockLayout headlay;
	private ASPBuffer row;

	//===============================================================
	// Construction 
	//===============================================================
	public CreatePermitDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		wo_no = ctx.readValue("WO_NO", "");
		start = ctx.readValue("START", "");
		win_cls = ctx.readFlag("WINCLS", false);
		ok_sub = ctx.readFlag("OKSUB", false);
		fmt = mgr.newASPHTMLFormatter();   

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("BACK"))
			cancel();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("START")))
		{
			wo_no = mgr.readValue("WO_NO");
			start = mgr.getQueryStringValue("START");
			okFind();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			wo_no = mgr.readValue("WO_NO");
			okFind();
		}

		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();

		ctx.writeValue("WO_NO", wo_no);
		ctx.writeValue("START", start);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		cmd = trans.addCustomCommand("VAL1","Work_Order_Permit_API.Create_Permit");
		cmd.addParameter("PERMIT_SEQ");
		cmd.addParameter("PERMIT_TYPE", headset.getRow().getValue("PERMIT_TYPE_ID"));
		cmd.addParameter("WO_NO", wo_no);

		trans = mgr.perform(trans);  

		if (!mgr.isEmpty(start))
			state = true;
		else
			ok_sub = true;
	}

	public void cancel()
	{
		win_cls = true;
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

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
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

		f = headblk.addField("PERMIT_TYPE_ID");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWCREATEPERMITDLGPERMIT_TYPE_ID: Permit Type ID");
		f.setUpperCase();

		f = headblk.addField("DESCRIPTION");
		f.setSize(35);
		f.setReadOnly();
		f.setLabel("PCMWCREATEPERMITDLGDESCRIPTION: Description");

                f = headblk.addField("PERMIT_SEQ", "Number");
		f.setSize(11);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO", "Number");
		f.setSize(11);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("PERMIT_TYPE");
		f.setSize(11);
		f.setHidden();
		f.setFunction("''");

		headblk.setView("PERMIT_TYPE");
		headblk.defineCommand("PERMIT_TYPE_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.VIEWDETAILS);
		headbar.disableCommand(headbar.EDITROW);
		headbar.addCustomCommand("submit",mgr.translate("PCMWCREATEPERMIT: Ok"));
		headblk.disableHistory();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);  
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCREATEPERMITTITLE: Create Permit";
	}

	protected String getTitle()
	{
		return "PCMWCREATEPERMITTITLE: Create Permit";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("<br>\n");
		appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWCREATEPERMITDLGBACK: Cancel"),""));

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(win_cls); // XSS_Safe ILSOLK 20070713
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(state);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		//appendDirtyJavaScript("  window.opener.location = \"ActiveRound.page?WO_NO=\" + URLClientEncode(");
		appendDirtyJavaScript("   window.opener.refreshPermitTab();");
		//appendDirtyJavaScript(wo_no);
		//appendDirtyJavaScript(") + \"\";\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("else if (");
		appendDirtyJavaScript(ok_sub); // XSS_Safe ILSOLK 20070713
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.opener.location = \"WorkOrderPermit.page?WO_NO=");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(wo_no)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript(" \";\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n"); 
	}
}
