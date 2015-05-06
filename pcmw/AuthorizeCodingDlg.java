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
*  File        : AuthorizeCodingDlg.java 
*  Created     : ARWILK  010305  Created
*  Modified    :
*  INROLK  010608  Changed java scripts when call the previous form..due to Call ID 66103.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ThWilk  040826  Call 117300, Modified run() and javascripts. 
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  060308  Bug 136642: Modified submit().
*  THWILK  060310  Call 136653,Modified submit() and adjust().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  AMNILK  060919  Call Id: 139567. Modified the methods preDefine(),adjust().
*  ILSOLK  070709  Eliminated XSS.
*  ILSOLK  070730  Eliminated LocalizationErrors.    	   
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class AuthorizeCodingDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.AuthorizeCodingDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPHTMLFormatter fmt;
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
	private String calling_url;
	private String returnFlag;
	private String cancelFlag;
	private ASPBuffer transferedDataBuffer;
	private String qrystr;

	//===============================================================
	// Construction 
	//===============================================================
	public AuthorizeCodingDlg(ASPManager mgr, String page_path)
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

		calling_url = ctx.getGlobal("CALLING_URL");
		qrystr = ctx.readValue("QRYSTR","");
                transferedDataBuffer = ctx.readBuffer("CTXTRANSBUFF");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("SUBMIT"))
			submit();
		else if (mgr.buttonPressed("BACK"))
			cancel();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			if (!mgr.isEmpty(mgr.getQueryStringValue("QRYSTR")))
				qrystr=mgr.getQueryStringValue("QRYSTR");
			transferedDataBuffer = mgr.getTransferedData();
			eval(headblk.generateAssignments("SIGN", headset.getRow()));
		}
		else
			clear();

		adjust();

		ctx.writeBuffer("CTXTRANSBUFF", transferedDataBuffer);
		ctx.writeValue("QRYSTR",qrystr);  
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
		String strIsName;
		String strAuthId;
                String sWarning = "FALSE";
		ASPBuffer dataBuffer;

		ASPManager mgr = getASPManager();

		eval(headblk.generateAssignments("SIGN", headset.getRow()));

		ASPCommand cmd = trans.addCustomFunction("EMP1", "Company_Emp_API.Get_Max_Employee_Id", "SIGN_ID");
		cmd.addParameter("COMPANY", transferedDataBuffer.getBufferAt(0).getValue("COMPANY"));
		cmd.addParameter("SIGN", mgr.readValue("SIGN"));

		cmd = trans.addCustomFunction("EMP2", "Person_Info_API.Get_Name", "NAME");
		cmd.addParameter("SIGN", mgr.readValue("SIGN"));
		trans = mgr.perform(trans);

		strIsName = trans.getValue("EMP2/DATA/NAME");
		strAuthId = trans.getValue("EMP1/DATA/SIGN_ID");

		if (!mgr.isEmpty(strIsName))
		{
			trans.clear();

			for (int i = 0; i < transferedDataBuffer.countItems(); i++)
			{
				dataBuffer = transferedDataBuffer.getBufferAt(i);

				cmd = trans.addCustomCommand("ADELIVER_" + i, "Work_Order_Coding_API.Authorize");
				cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
				cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
				cmd.addParameter("SIGN_ID", strAuthId);

                                cmd = trans.addCustomFunction("GETWARNING_"+i,"Work_Order_Coding_API.Has_Error_Transactions","WARNING");
				cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
				cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
			}

			trans = mgr.perform(trans);

			for (int i = 0; i < transferedDataBuffer.countItems(); i++)
                        {
                            if ("TRUE".equals(trans.getBuffer("GETWARNING_"+i).getValue("WARNING")))
                                sWarning = "TRUE";
                        }

                        if ("TRUE".equals(sWarning))
                            mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGERRTRANS: Exists Error on Transactions. See Transaction History for more information."));

			returnFlag = "TRUE";
		}
		else
			mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGAUTHNOTREG: The authorizer is not registered."));
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

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("SIGN");
		f.setSize(9);
		f.setLabel("PCMWAUTHORIZECODINGDLGSIGN: Signature");
                f.setLOVProperty("TITLE",mgr.translate("PCMWAUTHORIZECODINGDLGSIGNLOV: List of Signature"));
		f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("SIGN_ID");
		f.setSize(8);
		f.setHidden();
		f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("COMPANY");
                f.setLabel("PCMWAUTHORIZECODINGDLGCOPM: comp");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("NAME");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ROW_NO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("DESCRIPTION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WARNING");
		f.setHidden();
		f.setFunction("''");

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

                enableConvertGettoPost();
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();
                mgr.getASPField("SIGN").setDynamicLOV("EMPLOYEE_LOV",600,445);
                mgr.getASPField("SIGN").setLOVProperty("WHERE","COMPANY= '" + transferedDataBuffer.getBufferAt(0).getValue("COMPANY") + "'");  
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWAUTHORIZECODINGDLGTITLE: Authorize Posting";
	}

	protected String getTitle()
	{
		return "PCMWAUTHORIZECODINGDLGTITLE: Authorize Posting";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(returnFlag);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  str = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  re  = \"ActiveRound\";\n");
		appendDirtyJavaScript("  if (str.search(re) != -1) \n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     window.opener.location=\""+mgr.encodeStringForJavascript(qrystr)+"&START=Authorize&WONO=\"+'"); // XSS_Safe ILSOLK 20070709
                appendDirtyJavaScript(mgr.URLEncode(transferedDataBuffer.getBufferAt(0).getValue("WO_NO")));
		appendDirtyJavaScript("'+\"\";\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  else if (str.search(\"PostingsOvw\") != -1)   \n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     window.opener.location=\"WorkOrderPostingsOvw.page?WO_NO=\"+'");
                appendDirtyJavaScript(mgr.URLEncode(transferedDataBuffer.getBufferAt(0).getValue("WO_NO")));
		appendDirtyJavaScript("'+\"\";\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  else\n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     window.opener.location=\"WorkOrderCoding1.page?WO_NO=\"+'");
                appendDirtyJavaScript(mgr.URLEncode(transferedDataBuffer.getBufferAt(0).getValue("WO_NO")));
		appendDirtyJavaScript("'+\"\";\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("  self.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(cancelFlag);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
