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
*  File        : WorkOrderPlanning1.java 
*  Created     : ASP2JAVA Tool  010416
*  Modified    :  
*  BUNILK  010418  Corrected some conversion errors. Removed some
*                  unwanted codes and changed the line selecting method.   
*  ARWILK  010712  Corrected some errors associated with data tranfer to WorkOrderCoding1.java.
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040617  Added job_id to headblk.(Spec - AMEC109A)  
*  ARWILK  041112  Replaced getContents with printContents.
*  NIJALK  050826  Bug 126558: Modified okCreate(), printContents().
*  AMDILK  060817  Bug 58216, Eliminated SQL errors in web applications. Modified the methods okFind1(), okFind2(), okFind()
*  AMDILK  600906  Merged with the Bug Id 58216
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderPlanning1 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderPlanning1");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String calling_url;
	private String selVal;
	private String frmname;
	private String frmPath;
	private String backFrmName;
	private String rmbName;
	private String rowToEdit;
	private String cancelPath;
	private String pressedOkBut;
	private String passedWoNo;
	private String filterCost;
	private String url;
	private ASPTransactionBuffer trans;
	private String actionCanceled;
	private String sWoNo;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderPlanning1(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		calling_url =  "";
		selVal =  "";
		frmname =  "";
		frmPath =  "";
		backFrmName =  "";
		rmbName =  "";
		rowToEdit =  "";
		cancelPath =  "";
		pressedOkBut =  "";
		passedWoNo =  "";
		calling_url =  "";
		filterCost =  "";
		ASPManager mgr = getASPManager();


		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"); 
		fmt = mgr.newASPHTMLFormatter(); 
		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		frmname = ctx.readValue("THIFRMNAEM",frmname);
		backFrmName = ctx.readValue("SBACKFRMNAME",backFrmName);
		frmPath = ctx.readValue("THIFRMPAT",frmPath);
		actionCanceled = ctx.readValue("ACTICANC","false");
		rmbName = ctx.readValue("CALLRMNAM",rmbName);
		rowToEdit = ctx.readValue("ROWTEDTI",rowToEdit);
		cancelPath = ctx.readValue("CANCPATH",cancelPath);
		passedWoNo = ctx.readValue("PASSWONO",passedWoNo);
		calling_url = ctx.readValue("CALLIUR",calling_url);
		filterCost = ctx.readValue("FCOSPE",filterCost);
		pressedOkBut = ctx.readValue("PREOKBUT","false");

		if (mgr.commandBarActivated())
		{
			eval(mgr.commandBarFunction());
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			sWoNo = mgr.readValue("WO_NO","");
			passedWoNo = sWoNo;
			frmname = mgr.readValue("FRMNAME","");
			rmbName = mgr.readValue("RMBNAME","");
			backFrmName = mgr.readValue("BACKFRMNAME","");

			calling_url = ctx.getGlobal("CALLING_URL");


			if ("ConnectPlan".equals(rmbName))
			{
				rowToEdit = mgr.readValue("EDITROWNUM","");
				filterCost = mgr.readValue("FILCOSTYP","");

				okFind1();
			}
			else
				okFind2();
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
			okFind();
		else if (mgr.buttonPressed("CRECANCEL"))
			cancelCreate();

		ctx.writeValue("THIFRMNAEM",frmname);
		ctx.writeValue("SBACKFRMNAME",backFrmName);   
		ctx.writeValue("ACTICANC",actionCanceled);
		ctx.writeValue("THIFRMPAT",frmPath);
		ctx.writeValue("CALLRMNAM",rmbName);
		ctx.writeValue("ROWTEDTI",rowToEdit);
		ctx.writeValue("CANCPATH",cancelPath);
		ctx.writeValue("PASSWONO",passedWoNo);
		ctx.writeValue("CALLIUR",calling_url);
		ctx.writeValue("FCOSPE",filterCost);
		ctx.writeValue("PREOKBUT",pressedOkBut);
	}


	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");

		mgr.endResponse();
	}


	public void okFind1()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		q.addWhereCondition("WO_NO= ?");
		q.addParameter("WO_NO", sWoNo);
		q.addWhereCondition("WORK_ORDER_COST_TYPE= ?");
		q.addParameter("WORK_ORDER_COST_TYPE", filterCost);
		mgr.submit(trans);

	}

	public void okFind2()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		q.addWhereCondition("WO_NO= ?");
		q.addParameter("WO_NO", sWoNo);
		mgr.submit(trans);

	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		q.addWhereCondition("WORK_ORDER_COST_TYPE= ?");
		q.addParameter("WORK_ORDER_COST_TYPE", filterCost);
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERPLANNING1NODATA: No data found."));
			headset.clear();
		}

	}

	public void okCreate()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		String Newcreval_Workordtype = headset.getRow().getValue("WORK_ORDER_COST_TYPE");
		String Newcreval_Catno = headset.getRow().getValue("CATALOG_NO");                                           
		String Newcreval_Catcont = headset.getRow().getValue("CATALOG_CONTRACT");                                 

		if ("CreatePlan".equals(rmbName))
		{

			frmPath = calling_url+"?NEWCREVAL_WO="+headset.getRow().getValue("WO_NO")+
					  "&NEWCREVAL_WORKORDTYPE="+(mgr.isEmpty(Newcreval_Workordtype)?"":Newcreval_Workordtype)+
					  "&NEWCREVAL_CATNO="+(mgr.isEmpty(Newcreval_Catno)?"":Newcreval_Catno)+
					  "&NEWCREVAL_CATCONT="+(mgr.isEmpty(Newcreval_Catcont)?"":Newcreval_Catcont)+
					  "&NEWCREVAL_QTY="+headset.getRow().getValue("QUANTITY")+
					  "&NEWCREVAL_QTYTOINVOI="+headset.getRow().getValue("QTY_TO_INVOICE")+
					  "&NEWCREVAL_PLANLINNO="+headset.getRow().getValue("PLAN_LINE_NO")+
					  "&NEWCREVAL_COSTAMT="+headset.getRow().getValue("COST")+
					  "&NEWCREVAL_BKFRMNAME="+(mgr.isEmpty(backFrmName)?"":backFrmName);

			cancelPath =  calling_url+"?NEWCANCELVAL_WO="+headset.getRow().getValue("WO_NO")+
						  "&NEWCREVAL_WO="+headset.getRow().getValue("WO_NO")+
						  "&NEWCREVAL_BKFRMNAME="+(mgr.isEmpty(backFrmName)?"":backFrmName);             
		}

		else if ("ConnectPlan".equals(rmbName))
		{
			frmPath = calling_url+"?NEWCONNVAL_WO="+headset.getRow().getValue("WO_NO")+      
					  "&NEWCONNVAL_CATNO="+(mgr.isEmpty(Newcreval_Catno)?"":Newcreval_Catno)+
					  "&NEWCONNVAL_CATCONT="+(mgr.isEmpty(Newcreval_Catcont)?"":Newcreval_Catcont)+
					  "&NEWCONNVAL_QTY="+headset.getRow().getValue("QUANTITY")+
					  "&NEWCONNVAL_QTYTOINVOI="+headset.getRow().getValue("QTY_TO_INVOICE")+
					  "&NEWCONNVAL_PLANLINNO="+headset.getRow().getValue("PLAN_LINE_NO")+
					  "&NEWCONNVAL_ROWNO="+rowToEdit+
					  "&NEWCREVAL_BKFRMNAME="+(mgr.isEmpty(backFrmName)?"":backFrmName);


			cancelPath =  calling_url+"?NEWCANCELVAL_WO="+headset.getRow().getValue("WO_NO")+            
						  "&NEWCONNVAL_WO="+headset.getRow().getValue("WO_NO")+
						  "&NEWCONNVAL_ROWNO="+rowToEdit+
						  "&NEWCREVAL_BKFRMNAME="+(mgr.isEmpty(backFrmName)?"":backFrmName);

		}
		pressedOkBut = "true"; 
	}

	public void cancelCreate()
	{
		actionCanceled = "true";
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("WO_NO","Number").
		setSize(8).
		setLabel("PCMWWORKORDERPLANNING1WONO: WO Number");

		headblk.addField("PLAN_LINE_NO","Number").
		setLabel("PCMWWORKORDERPLANNING1PLANLINENO: Plan Line No");

		headblk.addField("WORK_ORDER_COST_TYPE").
		setSize(200).
		setLabel("PCMWWORKORDERPLANNING1WORKORDERCOSTTYPE: Work Order Cost Type");

		headblk.addField("DATE_CREATED","Date").
		setLabel("PCMWWORKORDERPLANNING1DATECREATED: Date Created").
		setUpperCase();

		headblk.addField("QUANTITY","Number").
		setLabel("PCMWWORKORDERPLANNING1QUANTITY: Quantity").
		setUpperCase();

		headblk.addField("COST","Number").
		setLabel("PCMWWORKORDERPLANNING1COSTVALU: Cost").
		setUpperCase();

		headblk.addField("PLAN_DATE","Date").
		setLabel("PCMWWORKORDERPLANNING1PLANDATE: Plan Date");

		headblk.addField("QTY_TO_INVOICE","Number").
		setLabel("PCMWWORKORDERPLANNING1QTYTOINVOICE: Quantity To Invoice");

		headblk.addField("CATALOG_CONTRACT").
		setUpperCase().
		setSize(5).
		setLabel("PCMWWORKORDERPLANNING1CATALOGCONTRACT: Catalog Contract");

		headblk.addField("CATALOG_NO").
		setSize(25).
		setUpperCase().
		setLabel("PCMWWORKORDERPLANNING1CATALOGNO: Catalog No");

		headblk.addField("SALES_PRICE","Number").
		setLabel("PCMWWORKORDERPLANNING1SALESPRICE: Sales Price").
		setUpperCase();

		headblk.addField("DISCOUNT","Number").
		setLabel("PCMWWORKORDERPLANNING1DISCOUNT:  Discount").
		setUpperCase();

		headblk.addField("PRICE_LIST_NO").
		setSize(10).
		setUpperCase().
		setLabel("PCMWWORKORDERPLANNING1PRICELISTNO:  Price List No");

		headblk.addField("JOB_ID", "Number").
		setSize(8).
		setInsertable().
		setDefaultNotVisible().
		setDynamicLOV("WORK_ORDER_JOB", "WO_NO").
		//setDynamicLOV("WORK_ORDER_JOB");
		setLabel("PCMWWORKORDERPLANNINGJOBID: Job Id");

		headblk.addField("WORK_ORDER_INVOICE_TYPE").
		setSize(200).
		setLabel("PCMWWORKORDERPLANNING1WORKORDERINVOICETYPE: Work Order Invoice Type");

		headblk.addField("CURRENCY_RATE","Number").
		setLabel("PCMWWORKORDERPLANNING1CURRENCYRATE: Currency Rate");   

		headblk.setView("WORK_ORDER_PLANNING");    

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERPLANNING1HD: Plan Line No"));
		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("okCreate",mgr.translate("PCMWWORKORDERPLANNING1OKCREATE: Ok"));
		headbar.disableCommand(headbar.NEWROW);

		headtbl.setWrap();
		headtbl.unsetEditable();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERPLANNING1TITLE: Plan Line No";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERPLANNING1TITLE: Plan Line No";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("<table>\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td align=\"right\">\n");
		appendToHTML("&nbsp;\n");
		appendToHTML(fmt.drawSubmit("CRECANCEL",mgr.translate("PCMWWORKORDERPLANNING1LACNCE: Cancel"),"onClick=self.close()"));
		appendToHTML("&nbsp;\n");
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>					\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(actionCanceled));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == 'true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelPath));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript(",\"alwaysRaised,toolbar,titlebar,menubar,resizable,status,location,scrollbars=yes,width=770,height=460\");\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("} \n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(pressedOkBut));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == 'true')\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmPath));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("','");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"alwaysRaised,toolbar,titlebar,menubar,resizable,status,location,scrollbars=yes,width=770,height=460\");\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}  \n");
	}
}
