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
*  File        : ActiveWorkOrderDlg1.java 
*  Created     : ASP2JAVA Tool  010502
*  Modified    :
*  JEWILK  010502  Corrected conversion errors and done necessary changes.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  NAMELK  041104  Non Standard Translation Tags Corrected. 
*  ARWILK  041115  Replaced getContents with printContents.
*  NEKOLK  060309  Call 136780 Modified submit() and jscripts.
*  ILSOLK  070709  Eliminated XSS.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
*  071219  NIJALK  Bug 69819, Removed method calls to Customer_Agreement_API.Get_Description.
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveWorkOrderDlg1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrderDlg1");

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
	private String strWoNos;
	private String strSeperator;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private String fname;
	private String qrystr;
	private String frmname;
	private String okClose;
	private String authcode;
	private String coodna;
	private String company;
	private String sAuthCode;
	private String strRowNos;
	private String getSelectedRows;
	private String posSeparator;
        private boolean bCompleted;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveWorkOrderDlg1(ASPManager mgr, String page_path)
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
		calling_url=ctx.getGlobal("CALLING_URL");

		company = ctx.readValue("CTX1",company);
		strWoNos = ctx.readValue("CTX2",strWoNos);
		strRowNos = ctx.readValue("CTX3",strRowNos);
		strSeperator = ctx.readValue("CTX4",strSeperator);
		getSelectedRows = ctx.readValue("CTX5",getSelectedRows);
		fname= ctx.readValue("FORM_NAME","");
		qrystr = ctx.readValue("QRYSTR","");
		frmname = ctx.readValue("FRMNAME","");
		okClose = ctx.readValue("OKCLOSE","FALSE");
		sAuthCode = ctx.readValue("AUTHCODE",sAuthCode);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("SUBMIT"))
			submit();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			strWoNos = mgr.readValue("WO_NO");

			qrystr = mgr.readValue("QRYSTR");     
			frmname = mgr.readValue("FRMNAME");
			sAuthCode = mgr.readValue("AUTHORIZE_CODE");

			prepare();

		}

		ctx.writeValue("CTX1",company);
		ctx.writeValue("CTX2",strWoNos);
		ctx.writeValue("CTX3",strRowNos);
		ctx.writeValue("CTX4",strSeperator);
		ctx.writeValue("CTX5",getSelectedRows);

		ctx.writeValue("QRYSTR",qrystr);
		ctx.writeValue("FRMNAME",frmname);
		ctx.writeValue("FORM_NAME",fname);
		ctx.writeValue("AUTHCODE",sAuthCode);
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
		ASPManager mgr = getASPManager();

		fname = "true";  
	}


	public void prepare()
	{
		ASPBuffer data = trans.getBuffer("HEAD/DATA");

		headset.addRow(data);

	}

	public void submit()
	{
		ASPManager mgr = getASPManager(); 

		authcode =  mgr.readValue("AUTHORIZE_CODE");

		if (mgr.isEmpty(authcode))
			mgr.showAlert("PCMWACTIVEWORKORDERDLG1ADDEDNULL: This Signature is not registered.");
		else
		{
			trans.clear();
			ASPCommand cmd = trans.addCustomCommand("CHCO", "Active_Work_Order_API.Modify_Coordinator");
			cmd.addParameter("WO_NO",strWoNos);
			cmd.addParameter("AUTHORIZE_CODE",authcode);

			cmd = trans.addCustomFunction("CONA","Order_Coordinator_API.Get_Name","COORDINATORINFO");
			cmd.addParameter("AUTHORIZE_CODE",authcode);

			trans = mgr.perform(trans);

			coodna = trans.getValue("CONA/DATA/COORDINATORINFO");

			ASPBuffer r = headset.getRow();

			r.setValue("AUTHORIZE_CODE",authcode);
			r.setValue("COORDINATORINFO", coodna);
			headset.setRow(r);

		       // mgr.showAlert("PCMWACTIVEWORKORDERDLG1ADDED: Coordinator has been added to the Work Order");

			//okClose = "TRUE";
                        bCompleted = true;

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

		f = headblk.addField("OBJSTATE");
		f.setHidden();

		f = headblk.addField("OBJEVENTS");
		f.setHidden();

		f = headblk.addField("WO_NO");
		f.setSize(14);
		f.setDynamicLOV("ACTIVE_WORK_ORDER",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1WONO: WO No");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("ERR_DESCR");
		f.setSize(30);
		f.setLabel("PCMWACTIVEWORKORDERDLG1SHODESCR: Short Description");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("STATE");
		f.setSize(14);
		f.setLabel("PCMWACTIVEWORKORDERDLG1STATE: Status");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("CUSTOMER_NO");
		f.setSize(14);
		f.setDynamicLOV("CUSTOMER_INFO",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1CUSTNO: Customer No");
		f.setUpperCase();
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("PARTYTYPECUSTOMERNAME");
		f.setSize(30);
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("REFERENCE_NO");
		f.setSize(14);
		f.setLabel("PCMWACTIVEWORKORDERDLG1REFERENCE_NO: Reference No");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("AUTHORIZE_CODE");
		f.setSize(14);
		f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1COOR: Coordinator");
		f.setUpperCase();

		f = headblk.addField("COORDINATORINFO");
		f.setSize(30);
		f.setFunction("Order_Coordinator_API.Get_Name (:AUTHORIZE_CODE)");
		f.setReadOnly();

		f = headblk.addField("AGREEMENT_ID");
		f.setSize(14);
		f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1AGREEID: Agreement Id");
		f.setUpperCase();
		f.setReadOnly();
		f.setHidden();

                //Bug 69819, Start, Removed function Call
		f = headblk.addField("CUSTOMERAGREEMENTDESCRIPTION");
		f.setSize(20);
		f.setFunction("''");
		f.setReadOnly();
		f.setHidden();
                //Bug 69819, End

		f = headblk.addField("MCH_CODE");
		f.setSize(14);
		f.setMaxLength(100);
		f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1MCH_CODE: Object Id");
		f.setUpperCase();
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("MCHNAME");
		f.setSize(30);
		f.setLabel("PCMWACTIVEWORKORDERDLG1MCHNAME: Object Description");
		f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("WORK_TYPE_ID");
		f.setSize(14);
		f.setDynamicLOV("WORK_TYPE",600,445);
		f.setLabel("PCMWACTIVEWORKORDERDLG1WORK_TYPE_ID: Work Type Id");
		f.setUpperCase();
		f.setReadOnly();
		f.setHidden();

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWACTIVEWORKORDERDLG1CONTRACT: Site");
		f.setUpperCase();
		f.setReadOnly();

		headblk.setView("ACTIVE_WORK_ORDER");
		headblk.defineCommand("ACTIVE_WORK_ORDER_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.BACK,"cancel");
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.defineCommand(headbar.CANCELNEW,null,"self.close()");


		headbar.defineCommand(headbar.SAVERETURN,"submit");
		headbar.disableModeLabel();

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
		headlay.setDialogColumns(2);
		headlay.setEditable();

		headlay.setSimple("COORDINATORINFO");
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVEWORKORDERDLG1ADDWOCOR: Add Coordinator to Work Order "+strWoNos;
	}

	protected String getTitle()
	{
		return "PCMWACTIVEWORKORDERDLG1ADDWOCOR: Add Coordinator to Work Order ";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		if (bCompleted)
                {
                    appendDirtyJavaScript("window.opener.commandSet('HEAD.refreshForm','');\n");
                    appendDirtyJavaScript("self.close();\n");
                }

                
                appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose)); // Bug Id 68773
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  qrystr = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  frmnme = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname)); // XSS_Safe ILSOLK 20070709
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.open(qrystr,frmnme,\"\");\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
