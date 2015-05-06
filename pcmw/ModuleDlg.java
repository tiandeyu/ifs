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
*  File        : ModuleDlg.java 
*  Created     : ASP2JAVA Tool  010516
*  Modified    :
*  SHCHLK  011017  Redirected to the main form (Work Clearence) after the submit() function.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041001  Merged LCS Bug: 47017.
*  NEKOLK  050815  Call 126363.Modified the Title..
*  THWILK  060313  Call 137151, Modified predefine().
*  NEKOLK  060321  Modified predefine() and modify().
*  AMNILK  070718  Eliminated XSS Security Vulnerability.
*  ILSOLK  070913  Modified printContent().Navigator Disappear(Call ID 148816).
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ModuleDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ModuleDlg");

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
	private String closeOk;
	private String cancelWin;
	private String CurrDelim;
	private ASPCommand cmd;
	private String strDeliDesc;
	private String mNewDlO;
	private double nNewDlo;

	//===============================================================
	// Construction 
	//===============================================================
	public ModuleDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();       

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		closeOk = ctx.readValue("CLOSEOK","");
		cancelWin = ctx.readValue("CANCELWIN","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  cancel()
	{
		cancelWin = "TRUE";
	}

	public void  submit()
	{
		ASPManager mgr = getASPManager();

		CurrDelim = mgr.readValue("DELIMITATION_ID");

		if (!mgr.isEmpty(CurrDelim))
		{
			cmd = trans.addCustomFunction("DELIDESC","Delimitation_API.Get_Description","DESCRIPTION");
			cmd.addParameter("DELIMITATION_ID",mgr.readValue("DELIMITATION_ID"));

			cmd = trans.addCustomCommand("NEWDLO","Delimitation_Order_Utility_API.Create_Dlo_From_Delimitation");
			cmd.addParameter("DELIMITATION_ORDER_NO");
			cmd.addParameter("DELIMITATION_ID",mgr.readValue("DELIMITATION_ID"));

			trans=mgr.perform(trans);

			strDeliDesc = trans.getValue("DELIDESC/DATA/DESCRIPTION");
			nNewDlo = trans.getNumberValue("NEWDLO/DATA/DELIMITATION_ORDER_NO");
                        mNewDlO = String.valueOf(nNewDlo);
                        
                        if (mNewDlO.indexOf(".") != -1)
                            mNewDlO = mNewDlO.substring(0,mNewDlO.indexOf("."));
                       
			if (mgr.isEmpty(strDeliDesc))
				mgr.showError(mgr.translate("PCMWMODULEDLGNOTREG: This Isolation is not registered!"));
			else if ((!mgr.isEmpty(strDeliDesc)) && (!this.isNaN(nNewDlo)))
				closeOk = "TRUE";
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

		f = headblk.addField("DELIMITATION_ORDER_NO","Number","#");
                f.setHidden();
		f.setFunction("''");

		f = headblk.addField("DESCRIPTION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("DELIMITATION_ID","Number","#");
		f.setSize(22);
		f.setDynamicLOV("DELIMITATION",600,445);
		//Bug 47017 start
		f.setLOVProperty("WHERE","APPROVED_BY IS NOT NULL");
		//Bug 47017 end
		f.setFunction("''");
		f.setLabel("PCMWMODULEDLGDELIMITATION_ID: Isolation ID");

		headblk.setView("MODULE");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT); 
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWMODULEDLGDELI: Isolation"),"DELIMITATION_ID",true,true);
		
		enableConvertGettoPost();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMODULEDLGTITLE: Create Isolation Order from Isolation";
	}

	protected String getTitle()
	{
		return "PCMWMODULEDLGTITLE: Create Isolation Order from Isolation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();


		appendToHTML(headlay.show());

		appendDirtyJavaScript("if('");     
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelWin));	//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("' == 'TRUE')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("} \n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(closeOk));		//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("' == 'TRUE')\n");
		appendDirtyJavaScript("{ \n");
		appendDirtyJavaScript("  jnNewDlo='");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(mNewDlO));		//XSS_Safe AMNILK 20070717
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.opener.location = '");
		appendDirtyJavaScript("  DelimitationOrderTab.page?DELIMITATION_ORDER_NO=");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(mNewDlO));
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("} \n");

		
	}
}
