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
*  File        : ActiveSeparateDlg2.java 
*  Modified    :  2001-02-23 Indra Rodrigo - Java conversion.
*    ASP2JAVA Tool  2001-02-22  - Created Using the ASP file ActiveSeparateDlg2.asp
*    031222      ARWILK   Edge Developments - (Removed clone and doReset Methods)
*    060225      ASSALK   Modified preDefine(). added method call 'enableConvertGettoPost()' at the end.
*  070706        ILSOLK    Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparateDlg2 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateDlg2");

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
	private String isTransfer;
	private String infoMsg;
	private ASPTransactionBuffer trans;
	private String woNo;
	private String errDescr;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPCommand cmd;
	private String newQuotationId;
	private ASPBuffer buffer;
	private String fmtdBuff;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparateDlg2(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		isTransfer = infoMsg;
		infoMsg = "";
		ASPManager mgr = getASPManager();


		frm = mgr.getASPForm();
		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		woNo = ctx.readValue("WONO","");
		errDescr = ctx.readValue("ERRDESCR","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();

			row = buff.getBufferAt(0); 
			woNo = row.getValue("WO_NO");

			row = buff.getBufferAt(1); 
			errDescr = row.getValue("ERR_DESCR");

			startup();
		}
		else
			startup();

		adjust();

		ctx.writeValue("WONO",woNo);
		ctx.writeValue("ERRDESCR",errDescr);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  startup()
	{
		ASPManager mgr = getASPManager();

		buff = mgr.newASPBuffer();
		buff.setFieldItem("WO_NO",woNo);
		buff.setFieldItem("QUOTATION_DESCRIPTION",errDescr);
		headset.addRow(buff);

	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  ok()
	{
		ASPManager mgr = getASPManager();

		headset.changeRow();

		cmd = trans.addCustomCommand("CREFROMWO","WORK_ORDER_QUOTATION_API.Create_From_Work_Order");
		cmd.addParameter("QUOTATION_ID");
		cmd.addParameter("WO_NO");
		cmd.addParameter("QUOTATION_REV");
		cmd.addParameter("QUOTATION_DESCRIPTION");
		cmd.addParameter("VALID_TO");

		trans = mgr.perform(trans);

		newQuotationId = trans.getValue("CREFROMWO/DATA/QUOTATION_ID");

		infoMsg = mgr.translate("PCMWACTIVESEPARATEDLG2NEWQUOCRTD: Quotation number ");
		infoMsg += newQuotationId;

		buffer = mgr.newASPBuffer();
		row = buffer.addBuffer("0");
		row.addItem("WO_NO",woNo);
		fmtdBuff = buffer.format();
		isTransfer ="TRUE";
	}


	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		buffer = mgr.newASPBuffer();
		row = buffer.addBuffer("0");
		row.addItem("WO_NO",woNo);

		mgr.transferDataTo("ActiveSeparate3.page",buffer);
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();

		headblk = mgr.newASPBlock("HEAD");         

		f = headblk.addField("QUOTATION_REV");
		f.setSize(15);
		f.setMaxLength(6);
		f.setUpperCase();
		f.setLabel("PCMWACTIVESEPARATEDLG2QUOTREV: Revision");

		f = headblk.addField("VALID_TO","Date");
		f.setSize(15);
		f.setLabel("PCMWACTIVESEPARATEDLG2VALIDTO: Valid To");

		f = headblk.addField("QUOTATION_DESCRIPTION");
		f.setSize(50);
		f.setMaxLength(50);
		f.setLabel("PCMWACTIVESEPARATEDLG2QUOTDESC: Description");

		f = headblk.addField("WO_NO");
		f.setHidden();

		f = headblk.addField("QUOTATION_ID");
		f.setHidden();

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATEDLG2HD: Create Quotation"));
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.setDialogColumns(1);
		headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEDLG2IDFQ: Input Data for Quotation"),"QUOTATION_REV,VALID_TO,QUOTATION_DESCRIPTION",true,true);   

                enableConvertGettoPost();
	}

	public void  adjust()
	{

	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVESEPARATEDLG2TITLE: Create Quotation";
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATEDLG2TITLE: Create Quotation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

                // XSS_Safe ILSOLK 20070706
		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(isTransfer);
		appendDirtyJavaScript("' == \"TRUE\") \n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   buff = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(fmtdBuff));
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("	alertMsg = '");
		appendDirtyJavaScript(infoMsg);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("	alert(alertMsg);\n");
		appendDirtyJavaScript("	//self.status='Please wait...';\n");
		appendDirtyJavaScript("   window.location = \"ActiveSeparate3.page?__TRANSFER=\"+URLClientEncode(buff);\n");
		appendDirtyJavaScript("}\n");
	}

}
