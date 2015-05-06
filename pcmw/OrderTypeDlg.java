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
*  File        : OrderTypeDlg.java 
*  Created     : ASP2JAVA Tool  010502
*  Modified    :
*  JEWILK  010503  Corrected conversion errors and done necessary changes.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041115  Replaced getContents with printContents.
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class OrderTypeDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.OrderTypeDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
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
	private String sWoNo;
	private String desc;
	private ASPTransactionBuffer trans;
	private String cancelFlag;
	private String okFlag;
	private String sTitle;
	private String xx;

	//===============================================================
	// Construction 
	//===============================================================
	public OrderTypeDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		sWoNo =  desc ;
		desc =  "";
		ASPManager mgr = getASPManager();

		fmt = mgr.newASPHTMLFormatter();
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		sWoNo = ctx.readValue("SWONO",sWoNo);  
		cancelFlag =  ctx.readValue("CANCELFLAG","FALSE");                         
		okFlag =  ctx.readValue("OKFLAG","FALSE");                        

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO1")))
			sWoNo = mgr.readValue("WO_NO1");
		else if (mgr.dataTransfered())
			okFind();

		adjust();

		ctx.writeValue("SWONO",sWoNo);
		ctx.writeValue("CANCELFLAG",cancelFlag);
		ctx.writeValue("OKFLAG",okFlag);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void submit()
	{
		ASPManager mgr = getASPManager();

		ASPCommand cmd = trans.addCustomFunction("CUSTDESC","Cust_Order_Type_API.Get_Description","DESCRIPTION");
		cmd.addParameter("CUST_ORDER_TYPE",mgr.readValue("CUST_ORDER_TYPE"));

		trans = mgr.perform(trans);

		desc = trans.getValue("CUSTDESC/DATA/DESCRIPTION");

		if (mgr.isEmpty(desc))
			mgr.showAlert("PCMWORDERTYPEDLGINVALIDTYPE: This Order type is not registered");
		else
		{
			trans.clear();

			cmd = trans.addCustomCommand("ACTWORK","Active_Work_Order_API.Modify_Cust_Order_Type");
			cmd.addParameter("WO_NO",sWoNo);
			cmd.addParameter("CUST_ORDER_TYPE",mgr.readValue("CUST_ORDER_TYPE"));

			trans = mgr.perform(trans);

			okFlag = "TRUE";
		}   
	}


	public void cancel()
	{

		cancelFlag = "TRUE";
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);
	}


	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("OBJVERSION");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("CUST_ORDER_TYPE");
		f.setSize(20);
		f.setDynamicLOV("CUST_ORDER_TYPE",600,445);
		f.setLabel("PCMWORDERTYPEDLGCUST_ORDER_TYPE: Customer Order Type");
		f.setUpperCase();
		f.setFunction("''");

		f = headblk.addField("DESCRIPTION");
		f.setSize(8);
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("WO_NO");
		f.setSize(8);
		f.setHidden();
		f.setFunction("''");

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);
		headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.defineGroup(mgr.translate("PCMWORDERTYPEDLGCUSTORD: Add Order Type"),"CUST_ORDER_TYPE",true,true);
		headlay.setDialogColumns(1);

		headtbl = mgr.newASPTable(headblk);
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		sTitle = mgr.translate("PCMWORDERTYPEDLGADDCUSORDERTYPE: Add Customer Order Type to Work Order")+" - " ;

		xx = sWoNo;
		desc = mgr.URLEncode(desc);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return sTitle+xx;
	}

	protected String getTitle()
	{
		return "PCMWORDERTYPEDLGADDCUSORDERTYPE: Add Customer Order Type to Work Order";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("window.name = \"OrderTypeDlg\";\n");
		
		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(okFlag));   //XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.open(\"ActiveWorkOrder4.page?WO_NO=\"+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo));	//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("')+\"&REFRESH=\"+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(desc));	//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("'),\"AciveWorkOrder4\",\"alwaysRaised,resizable,scrollbars=yes,width=770,height=460\");  \n");
		appendDirtyJavaScript("   window.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelFlag));  //XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
