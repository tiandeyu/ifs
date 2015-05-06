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
*  File        : OrderCoordinatorDlg.java 
*  Modified    :
*    SHFELK       2001-Feb-20  - Created.
*    ARWILK       031222         Edge Developments - (Removed clone and doReset Methods)
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class OrderCoordinatorDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.OrderCoordinatorDlg");

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
	private String nWoNo;
	private String wo_no;
	private String strAuthoCode;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPCommand cmd;
	private String strName;
	private ASPBuffer buffer;
	private ASPQuery q;


	//===============================================================
	// Construction 
	//===============================================================
	public OrderCoordinatorDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		nWoNo = "";
		wo_no = "";
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		calling_url=ctx.getGlobal("CALLING_URL");
		nWoNo=ctx.readValue("CTX1",nWoNo);
		wo_no=ctx.readValue("CTX2",wo_no);
		strAuthoCode=ctx.readValue("CTX3",strAuthoCode);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();
			row = buff.getBuffer("0"); 
			nWoNo = row.getValue("WO_NO");
			row = buff.getBuffer("1"); 
			strAuthoCode = row.getValue("AUTHORIZE_CODE");
		}

		okFind();
		adjust();

		ctx.writeValue("CTX1",nWoNo);
		ctx.writeValue("CTX2",wo_no);
		ctx.writeValue("CTX3", strAuthoCode);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  ok()
	{
		ASPManager mgr = getASPManager();


		nWoNo= ctx.readValue("CTX1",nWoNo);
		strAuthoCode= ctx.readValue("CTX3",strAuthoCode);
		headset.storeSelections();

		cmd = trans.addCustomFunction( "PARTEXIST1", "Order_Coordinator_API.Get_Name", "NAME" );
		cmd.addParameter("AUTHORIZE_CODE",mgr.readValue("AUTHORIZE_CODE",""));

		trans = mgr.perform(trans);
		strName=trans.getValue("PARTEXIST1/DATA/NAME");

		trans.clear();

		if (!mgr.isEmpty(strName))
		{
			cmd = trans.addCustomCommand( "PARTEXIST2", "Active_Work_Order_API.Modify_Coordinator");
			cmd.addParameter("WO_NO",nWoNo);
			cmd.addParameter("AUTHORIZE_CODE",mgr.readValue("AUTHORIZE_CODE",""));

			trans = mgr.perform(trans);

			buffer=mgr.newASPBuffer();
			row=buffer.addBuffer("0");
			row.addItem("WO_NO",nWoNo);

			mgr.transferDataTo("ActiveWorkOrder3.page",buffer);

		}
		else
		{
			mgr.showAlert(mgr.translate("PCMWORDERCOORDINATORDLGNOTREG: This signature is not registered."));  
			headset.clear();
		}
	}


	public void  cancel()
	{
		ASPManager mgr = getASPManager();

		nWoNo=ctx.readValue("CTX1",nWoNo);
		strAuthoCode= ctx.readValue("CTX3",strAuthoCode);

		buffer=mgr.newASPBuffer();
		row=buffer.addBuffer("0");
		row.addItem("WO_NO",nWoNo);

		mgr.transferDataTo("ActiveWorkOrder3.page",buffer);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWORDERCOORDINATORDLGNODATA: No data found."));
			headset.clear();
		}
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setFunction("''").
		setHidden();

		headblk.addField("OBJVERSION").
		setFunction("''").
		setHidden();

		headblk.addField("AUTHORIZE_CODE").
		setSize(8).
		setDynamicLOV("ORDER_COORDINATOR",600,450).
		setLabel("PCMWORDERCOORDINATORDLGAUTHORIZE_CODE: Coordinator").
		setFunction("''").
		setUpperCase();

		headblk.addField("DESCRIPTION").
		setSize(12).
		setFunction("''").
		setHidden();

		headblk.addField("NAME").
		setHidden().
		setFunction("''").
		setUpperCase();

		headblk.addField("WO_NO","Number").
		setHidden().
		setFunction("''");

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"ok");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWORDERCOORDINATORDLGGRPLABEL1: Add Signature"),"AUTHORIZE_CODE",true,true);
	}

	public void  adjust()
	{
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWORDERCOORDINATORDLGTITLE: Add Coordinator to Work Order ";
	}

	protected String getTitle()
	{
		return "PCMWORDERCOORDINATORDLGTITLE: Add Coordinator to Work Order ";
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
