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
*  File        : WorkOrderQuotationWiz.java 
*  Created     : ASP2JAVA Tool  010319
*  Modified    :
*  JEWILK  010322  Corrected conversion errors and done necessary adjustments.
*  CHCRLK  010410  Improved GUI and changed method getQueryString to getQueryStringValue.
*  JEWILK  010416  Set "QUOTATION_ID" field Mandatory. (Call 77443)
*  INROLK  010425  Corrected GUI.
*  VAGULK  010516  Changed the text id s of Quotation Id and Customer Id to ID 
*  CHCRLK  010613  Modified overwritten validations.
*  SHFELK  010628  Added the function checkMandato().                
*  VAGULK  020402  Modified to change the query parameters by entering a random value.
*                  This is done to avoid the browser fetch the page from cache.
*  SHAFLK  021108  Bug Id 34064,Changed method finish. 
*  BUNILK  021212  Merged with 2002-3 SP3
*  JEWILK  030926  Corrected overridden javascript function toggleStyleDisplay(). 
*                  Added new method readNumberValue() to avoid some problems with Web Client 3.6.0.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  NIJALK  050401  Call 122927: Modified preDefine(), getContents().
*  RANFLK  060301  Call 135726: Set Read only Quation Id in Step 2. Change the description appear in the first page.
*  RANFLK  060301  Call 135728: Modify the per().
*  NIJALK  060309  Call 136762: Modified validation to QUOTATION_ID. Modified readNumberValue().nxt().
*  NIJALK  060313  Modified finish().
* ----------------------------------------------------------------------------
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060817  Bug 58216, Eliminated SQL errors in web applications. Modified the method nxt()
*  AMDILK  060906  Merged with the Bug Id 58216
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070404  Modified Predefine() to remove the extra line shown at runtime
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  ILSOLK  070731  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderQuotationWiz extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderQuotationWiz");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPHTMLFormatter fmt;
	private ASPForm frm;
	private ASPContext ctx;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock cntblk;
	private ASPRowSet cntset;
	private ASPBlockLayout cntlay;
	private ASPBlock tempblk;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private boolean POrdConf;
	private boolean QAccept;
	private String quotAcc;
	private String printcon;
	private String CancelFlag;
	private ASPCommand cmd;
	private ASPBuffer row;
	private String valPrnt;
	private String QuotAcc;
	private String TempEvent;
	private int Step;
	private boolean showHtmlPart;
	private String url;
	private boolean toDefaultPage;
	private String calling_url;
        private String note;
        private String stat_code;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderQuotationWiz(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();
		frm = mgr.getASPForm();
		ctx = mgr.getASPContext();

		POrdConf = ctx.readFlag("POC",true);
		Step   = ctx.readNumber("STEP",1);
		QAccept  = ctx.readFlag("QACC",true);
		quotAcc = ctx.readValue("QUOT","");
		printcon = ctx.readValue("PRIN","");
		showHtmlPart = ctx.readFlag("CTSSHHTML",true);
		toDefaultPage = ctx.readFlag("CTXTODEF",false);
		CancelFlag = ""; 

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("PREV"))
			prev();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
		{
			if ("WorkOrderQuotationWiz".equals(mgr.readValue("WNDFLAG","")))
				showHtmlPart = false;
			else if ("WorkOrderQuotationWizFromPortal".equals(mgr.readValue("WNDFLAG","")))
			{
				showHtmlPart = false;
				toDefaultPage = true;

				calling_url = ctx.getGlobal("CALLING_URL");
				ctx.setGlobal("CALLING_URL",calling_url);                     
			}
		}
		else if ("AAAA".equals(mgr.readValue("BUTTONNXT")))
			nxt();
		else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
			finish();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else
			startup();

		adjust();

		ctx.writeFlag("QACC",QAccept);
		ctx.writeFlag("POC",POrdConf);
		ctx.writeNumber("STEP",Step);
		ctx.writeValue("QUOT",quotAcc);
		ctx.writeValue("PRIN",printcon);
		ctx.writeFlag("CTSSHHTML",showHtmlPart);
		ctx.writeFlag("CTXTODEF",toDefaultPage);
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void store()
	{
		if (!headlay.isMultirowLayout() && (headset.countRows()>0))
			headset.changeRow();
		else if (headlay.isMultirowLayout())
		{
			headset.changeRows();
			headset.storeSelections();
                        
		}
	}

	public void assign()
	{
		eval(headblk.generateAssignments());
	}

	public void startup()
	{
		okFind(); 
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");

		if ("QUOTATION_ID".equals(val))
		{
			String QoutId = readNumberValue("QUOTATION_ID");

			cmd = trans.addCustomFunction( "QREV", "WORK_ORDER_QUOTATION_API.Get_Quotation_Rev", "QUOTATION_REV");
			cmd.addParameter("QUOTATION_ID",QoutId);

			cmd = trans.addCustomFunction( "ST", "WORK_ORDER_QUOTATION_API.Get_State", "STATE");
			cmd.addParameter("QUOTATION_ID",QoutId);

			cmd = trans.addCustomFunction( "DESC", "WORK_ORDER_QUOTATION_API.Get_Quotation_Description", "QUOTATION_DESCRIPTION");
			cmd.addParameter("QUOTATION_ID",QoutId);

			cmd = trans.addCustomFunction( "CUSID", "WORK_ORDER_QUOTATION_API.Get_Customer_Id", "CUSTOMER_ID");
			cmd.addParameter("QUOTATION_ID",QoutId);

			cmd = trans.addCustomFunction( "CUSNA", "WORK_ORDER_QUOTATION_API.Get_Customer_Name", "CUSTOMER_NAME");
			cmd.addParameter("QUOTATION_ID",QoutId);

			trans = mgr.validate(trans);

			String qrev = trans.getValue("QREV/DATA/QUOTATION_REV");
			String st   = trans.getValue("ST/DATA/STATE");
			String desc = trans.getValue("DESC/DATA/QUOTATION_DESCRIPTION");
			String cusid = trans.getValue("CUSID/DATA/CUSTOMER_ID");
			String cusna = trans.getValue("CUSNA/DATA/CUSTOMER_NAME");

			String txt = (mgr.isEmpty(QoutId) ? "" : (QoutId))+ "^" +
                                                 (mgr.isEmpty(qrev) ? "" : (qrev))+ "^" + 
						 (mgr.isEmpty(st) ? "" : (st))+ "^" + 
						 (mgr.isEmpty(desc) ? "" : (desc))+ "^"  + 
						 (mgr.isEmpty(cusid) ? "" : (cusid))+ "^"  + 
						 (mgr.isEmpty(cusna) ? "" : (cusna))+ "^";

			mgr.responseWrite(txt);
		}
		else if ("STATISTICAL_CODE".equals(val))
		{
			String StatCode = mgr.readValue("STATISTICAL_CODE");

			cmd = trans.addCustomFunction( "STDESC", "QUOTATION_STATISTICAL_CODE_API.Get_Description", "STATDESCRIPTION");
			cmd.addParameter("STATISTICAL_CODE",StatCode);

			trans = mgr.validate(trans);

			String StDesc = trans.getValue("STDESC/DATA/STATDESCRIPTION");

			String txt = (mgr.isEmpty(StDesc) ? "" : (StDesc))+ "^";

			mgr.responseWrite(txt);
		}

		mgr.endResponse();
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

		if (headset.countRows() == 0)
			mgr.setStatusLine("PCMWWORKORDERQUOTATIONWIZNODATA: No data found.");
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void prev()
	{
		ASPManager mgr = getASPManager();


		if (Step == 1)
			mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONWIZCANNOTMOVEPREV: Cannot go to previous"));
		else
		{

			Step = Step - 1;

			printcon=mgr.readValue("PRINTCON");
                        stat_code =mgr.readValue("STATISTICAL_CODE","");
                        note = mgr.readValue("STATISTICAL_NOTE","");


			row = headset.getRow(); 
			row.setValue("PRINT_ORD_CONF",printcon);
                        row.setValue("STATISTICAL_CODE",stat_code);
                        row.setValue("STATISTICAL_NOTE",note);

			headset.setRow(row);

			valPrnt = headset.getValue("PRINT_ORD_CONF");

			if ("Yes".equals(valPrnt))
				POrdConf = true;

			if ("NO".equals(valPrnt))
                                POrdConf = false;
                                                       

	       }
	}

	public void nxt()
	{
		ASPManager mgr = getASPManager();

		store();

		ASPQuery q = trans.addQuery(cntblk);
		q.includeMeta("ALL");
                if (headset.countRows()>0)
		{
		   q.addWhereCondition("QUOTATION_ID = ?");
		   q.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
		}
		else
		{
		    q.addWhereCondition("QUOTATION_ID = ?");
		    q.addParameter("QUOTATION_ID", readNumberValue("QUOTATION_ID"));
		}
		mgr.submit(trans);

		quotAcc=mgr.readValue("QUOTATIONACC");

		row = headset.getRow(); 
		row.setValue("QUOT_ACCEPTED",quotAcc);
		headset.setRow(row);

		if (toDouble(cntset.getRow().getValue("CNT")) == 0)
			mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONWIZQUOTNOTAVAIL: The quotation is not available for followup!"));
		else if (Step == 2)
			mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONWIZCANNOTMOVENEXT: Cannot goto Next"));
		else
		{
			Step = Step + 1;
			QuotAcc = headset.getValue("QUOT_ACCEPTED");

			if ("Yes".equals(QuotAcc))
				QAccept = true;

			if ("NO".equals(QuotAcc))
				QAccept = false;
		}

		assign();
	}


	public void finish()
	{
		ASPManager mgr = getASPManager();

		store();

		if (Step == 1)
			mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONWIZCANNOTFINISH: Cannot finish now!"));
		else
		{
			printcon=mgr.readValue("PRINTCON");

			row = headset.getRow(); 
			row.setValue("PRINT_ORD_CONF",printcon);
			headset.setRow(row);

			valPrnt = headset.getValue("PRINT_ORD_CONF");

			if ("Yes".equals(valPrnt))
				POrdConf = true;

			if ("NO".equals(valPrnt))
				POrdConf = false;

			if (QAccept)
				TempEvent="Confirm";
			else
				TempEvent="Cancel";

			if ("Confirm".equals(TempEvent))
			{
				if (POrdConf)
				{
					processQuotation();

                                        ASPBuffer buf = mgr.newASPBuffer();
                                        buf.setFieldItem("QUOTATION_ID",readNumberValue("QUOTATION_ID"));

					trans.clear();

					String attr1 =  "REPORT_ID" + (char)31 + "WORK_ORDER_QUOTATION_REP" + (char)30;
					String attr2 =  "QUOTATION_ID" + (char)31 + buf.getValue("QUOTATION_ID") + (char)30;
					String attr3 =  "";
					String attr4 =  "";

					cmd = trans.addCustomCommand( "PRNT", "Archive_API.New_Client_Report");

					cmd.addParameter("ATTR0");  
					cmd.addParameter("ATTR1",attr1);  
					cmd.addParameter("ATTR2",attr2);  
					cmd.addParameter("ATTR3",attr3);  
					cmd.addParameter("ATTR4",attr4);  

					trans = mgr.perform(trans);

					String result_key =  trans.getValue("PRNT/DATA/ATTR0");

					trans.clear();

					ASPBuffer print = mgr.newASPBuffer();
					ASPBuffer printBuff=print.addBuffer("DATA");
					printBuff.addItem("RESULT_KEY",result_key);

					callPrintDlg(print,true);
				}
				else
					processQuotation();
			}
			else
				processQuotation();	//To perform the cancel event

			CancelFlag = "true";
		}
	}

	public void processQuotation()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		cmd = trans.addCustomCommand("PRQUO", "Work_Order_Quotation_API.Process_Quotation");
		cmd.addParameter("QUOTATION_ID",readNumberValue("QUOTATION_ID"));
		cmd.addParameter("DEVENT",TempEvent);
		cmd.addParameter("STATISTICAL_CODE",mgr.readValue("STATISTICAL_CODE"));
		cmd.addParameter("STATISTICAL_NOTE",mgr.readValue("STATISTICAL_NOTE"));

		trans = mgr.perform(trans);
	}

	public void cancel()
	{

		CancelFlag = "true";
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("QUOTATION_ID","Number");
		f.setSize(9);
		f.setDynamicLOV("WORK_ORDER_QUOTATION_FLWUP_LOV",600,450);
		f.setLOVProperty("WHERE","CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZQUOTATION_ID: Quotation ID:");
		f.setFunction("''");
		f.setCustomValidation("QUOTATION_ID","QUOTATION_ID,QUOTATION_REV,STATE,QUOTATION_DESCRIPTION,CUSTOMER_ID,CUSTOMER_NAME");
		f.setMandatory();

		f = headblk.addField("QUOTATION_REV");
		f.setSize(9);
		f.setReadOnly();
		f.setFunction("''");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZQUOTATIONREV: Revision:");

		f = headblk.addField("STATE");
		f.setSize(9);
		f.setReadOnly();
		f.setLabel("PCMWWORKORDERQUOTATIONWIZSTATE: Status:");
		f.setFunction("''");

		f = headblk.addField("QUOTATION_DESCRIPTION");
		f.setSize(42);
		f.setReadOnly();   
		f.setFunction("''");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZDESCRIPTION: Description:");

		f = headblk.addField("CUSTOMER_ID");
		f.setSize(11);
		f.setReadOnly();
		f.setFunction("''");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZCUSTOMER_ID: Customer ID:");

		f = headblk.addField("CUSTOMER_NAME");
		f.setSize(42);
		f.setReadOnly();
		f.setFunction("''");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZNAME: Name:");

		f = headblk.addField("REFERENCE");
		f.setSize(20);
		f.setReadOnly();
		f.setFunction("''");
		f.setLabel("PCMWWORKORDERQUOTATIONWIZREF: Reference:");

		f = headblk.addField("QUOT_ACCEPTED");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERQUOTATIONWIZQUOTACCPTED: Quotation Accepted:");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("PRINT_ORD_CONF");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERQUOTATIONWIZPRINTORDCONF: Print Order Confirmation:");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("STATISTICAL_CODE");
		f.setSize(9);
		f.setLabel("PCMWWORKORDERQUOTATIONWIZSTATCODE: Statistical Code:");
		f.setDynamicLOV("QUOTATION_STATISTICAL_CODE",600,450);
		f.setFunction("''");
		f.setCustomValidation("STATISTICAL_CODE","STATDESCRIPTION");

		f = headblk.addField("STATDESCRIPTION");
		f.setSize(30);
		f.setLabel("PCMWWORKORDERQUOTATIONWIZSTATDESC: Statistical Description:");
		f.setFunction("''");
		f.setReadOnly();

		f = headblk.addField("STATISTICAL_NOTE");
		f.setSize(47);
		f.setLabel("PCMWWORKORDERQUOTATIONWIZSTANOTE: Note:");
		f.setFunction("''");
		f.setHeight(4);

		headblk.setView("DUAL");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
		headlay.setEditable();
		headlay.setSimple("STATDESCRIPTION");
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.BACK);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.SAVERETURN);
		headbar.disableCommand(headbar.CANCELEDIT);
		headbar.disableModeLabel();

		headbar.disableModeLabel();

		headblk.setTitle(mgr.translate("PCMWWORKORDERQUOTATIONWIZBLKTITLE: Follow up on Work Order Quotation")); 
		headlay.setDialogColumns(1);

		cntblk = mgr.newASPBlock("CNTBLOCK");

		f = cntblk.addField("CNT");
		f.setFunction("count(*)");

		cntblk.setView("WORK_ORDER_QUOTATION_FLWUP_LOV");
		cntset = cntblk.getASPRowSet();
		cntlay = cntblk.getASPBlockLayout();
		cntlay.setDefaultLayoutMode(cntlay.CUSTOM_LAYOUT);
		cntlay.setEditable();

		tempblk = mgr.newASPBlock("TEMPBLOCK");

		tempblk.addField("DEVENT");
		tempblk.addField("ATTR0");
		tempblk.addField("ATTR1");
		tempblk.addField("ATTR2");
		tempblk.addField("ATTR3");
		tempblk.addField("ATTR4");

		headlay.showBottomLine(false);

                this.disableHomeIcon();
                this.disableNavigate();
                this.disableOptions();
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		frm.setFormWidth(730);
		headbar.setWidth(frm.getFormWidth());  

		if (Step != 1)
		{
			mgr.getASPField("CUSTOMER_ID").setHidden();
			mgr.getASPField("CUSTOMER_NAME").setHidden();
			mgr.getASPField("REFERENCE").setHidden();
		}
		if (Step != 2)
		{
			mgr.getASPField("STATISTICAL_CODE").setHidden();
			mgr.getASPField("STATISTICAL_NOTE").setHidden();
			mgr.getASPField("STATDESCRIPTION").setHidden();
                       		}
		if (Step == 2 &&   QAccept)
		{
			mgr.getASPField("STATISTICAL_CODE").setHidden();
			mgr.getASPField("STATISTICAL_NOTE").setHidden();
			mgr.getASPField("STATDESCRIPTION").setHidden();
                        mgr.getASPField ("QUOTATION_ID").unsetMandatory().setReadOnly();
		}

		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
	}

	public String readNumberValue(String field)
	{
		ASPManager mgr = getASPManager();

		double num = mgr.readNumberValue(field);
                String val = mgr.formatNumber("QUOTATION_ID",num);

		return val;
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERQUOTATIONWIZFOLLOWUP: Follow up on Work Order Quotation";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERQUOTATIONWIZFOLLOWUP: Follow up on Work Order Quotation";
	}

	protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();
		out.append("<html>\n");

		if (showHtmlPart)
		{
			out.append("<head>");
			out.append(mgr.generateHeadTag("PCMWWORKORDERQUOTATIONWIZFOLLOWUP: Follow up on Work Order Quotation"));
			out.append("<title></title>\n");
			out.append("</head>\n");
			out.append("<body ");
			out.append(mgr.generateBodyTag());
			out.append(">\n");
			out.append("<form ");
			out.append(mgr.generateFormTag());
			out.append(">\n");
                        out.append(mgr.startPresentation(getTitle()));

			printHiddenField("BUTTONVAL","");
			printHiddenField("BUTTONNXT","");

			if (headlay.isVisible())
			{
				out.append(headbar.showBar());
				out.append("<table id=\"TBL1\" border=0 class=\"BlockLayoutTable\" cellspacing=10 cellpadding=1 width= 730>\n");
				out.append("<tr>\n");
				out.append("<td>\n");
				out.append("<table id=\"mainTBL\" border=\"0\" class=\"BlockLayoutTable\" cellpadding=\"1\" cellspacing=\"10\" width=\"100%\">\n");
				out.append("<tr>\n");
				out.append("<td width=\"25%\"><img src=\"../pcmw/images/ChangeWorkOrderWiz.gif\" WIDTH=\"130\" HEIGHT=\"260\"></td>\n");
				out.append("<td width=\"80%\">\n");

				if (Step == 1)
				{
					out.append("<table  border=\"0\" width= '100%'>\n");
					out.append("<tr>\n");
					out.append("<td>\n");
					out.append(fmt.drawReadValue(mgr.translate("PCMWWORKORDERQUOTATIONWIZNOTE11: Register if this quotation is to be accepted or rejected. Agreements with status 'Planned' will become 'Active' with the acceptance of the quotation.")));
					out.append("</td>\n");
					out.append("</tr>\n");
					out.append("</table>\n");
					out.append(headlay.generateDialog());
                                        

					if (QAccept)
					{
						out.append("<table id=\"quotTBL\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width=100");
						out.append("<tr>\n");
						out.append("<td nowrap height=\"26\" align=\"left\"  >");
						out.append("<td nowrap height=\"26\" align=\"left\">");
                                                appendToHTML("&nbsp;");
						out.append(fmt.drawWriteLabel("PCMWWORKORDERQUOTATIONWIZQUOTATIONCCPT: Quotation Accepted"));
                                                appendToHTML("&nbsp;&nbsp;");
						out.append("</td>\n");
						out.append("<td nowrap height=\"26\" align=\"left\" width = '73%'>");
						out.append(fmt.drawSelectStart("QUOTATIONACC","quotAcc"));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZYES: Yes"),"Yes",true));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZNO: No"),"NO",false));
						out.append(fmt.drawSelectEnd());
						out.append("</td>\n");
						out.append("</tr>\n");
						out.append("</table>\n");
					}
					else
					{
						out.append("<table id=\"quotTBL\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width=100");
						out.append("<tr>\n");
						out.append("<td nowrap height=\"26\" align=\"left\" >");
                                                appendToHTML("&nbsp;");
						out.append(fmt.drawWriteLabel("PCMWWORKORDERQUOTATIONWIZQUOTACCPT: Quotation Accepted"));
                                                appendToHTML("&nbsp;&nbsp;");
						out.append("</td>\n");
						out.append("<td nowrap height=\"26\" align=\"left\" width = '73%'>");
						out.append(fmt.drawSelectStart("QUOTATIONACC","quotAcc"));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZYES: Yes"),"Yes",false));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZNO: No"),"NO",true));
						out.append(fmt.drawSelectEnd());
						out.append("</td>\n");
						out.append("</tr>\n");
						out.append("</table>\n");
					}
				}
				else if (QAccept)
				{
					out.append("<table  border=\"0\" width= '100%'>\n");
					out.append("<tr>\n");
					out.append("<td>\n");
					out.append(fmt.drawReadValue(mgr.translate("PCMWWORKORDERQUOTATIONWIZNOTE21: The quotation will be accepted and the status set to confirmed, the work order's status set to observed. If the object or agreement is new, their statuses are changed to appropriate ones.")));
					out.append("</td></tr><tr><td><br>\n");
					out.append("</td>\n");
					out.append("</tr>\n");
					out.append("</table>\n");
					out.append(headlay.generateDialog());
					if (POrdConf)
					{
						out.append("				      <table id=\"prntTBL\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width=100");
						out.append("					     <tr>\n");
						out.append("						     <td nowrap height=\"26\" align=\"left\" >");
                                                appendToHTML("&nbsp;");
						out.append(fmt.drawWriteLabel("PCMWWORKORDERQUOTATIONWIZPRINTORD: Print Order Confirmation:"));
                                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
						out.append("</td>\n");
						out.append("						     <td nowrap height=\"26\" align=\"left\" width = '73%'>");
						out.append(fmt.drawSelectStart("PRINTCON","printcon"));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZYES: Yes"),"Yes",true));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZNO: No"),"NO",false));
						out.append(fmt.drawSelectEnd());
						out.append("						     </td>\n");
						out.append("					     </tr>\n");
						out.append("				      </table>\n");
					}
					else
					{
						out.append("				      <table id=\"prntTBL\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width=100");
						out.append("					      <tr>\n");
						out.append("						      <td nowrap height=\"26\" align=\"left\" >");
                                                appendToHTML("&nbsp;");
						out.append(fmt.drawWriteLabel("PCMWWORKORDERQUOTATIONWIZPRINTORD: Print Order Confirmation:"));
                                                appendToHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
						out.append("</td>\n");
						out.append("						      <td nowrap height=\"26\" align=\"left\" width = '73%'>");
						out.append(fmt.drawSelectStart("PRINTCON","printcon"));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZYES: Yes"),"Yes",false));
						out.append(fmt.drawSelectOption (mgr.translate("PCMWWORKORDERQUOTATIONWIZNO: No"),"NO",true));
						out.append(fmt.drawSelectEnd());
						out.append("						      </td>\n");
						out.append("					      </tr>\n");
						out.append("				      </table>\n");
					}
				}
				else
				{
					out.append("				      <table  border=\"0\" width= '75%'>\n");
					out.append("				              <tr>\n");
					out.append("				                      <td>\n");
					out.append(fmt.drawReadValue(mgr.translate("PCMWWORKORDERQUOTATIONWIZNOTE31: The quotation will be rejected. Define with a statistical code why it was cancelled for follow up.")));
					out.append("</td></tr><tr><td><br>\n");
					out.append("				                      </td>\n");
					out.append("				              </tr>\n");
					out.append("				      </table>\n");
					out.append(headlay.generateDialog());
				}
				out.append("		       </td>\n");
				out.append("		     </tr>\n");
				out.append("		   </table>\n");
				out.append("		</td>\n");
				out.append("	</tr>\n");
				out.append("</table>\n");
				out.append("  <table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=730>\n");
				out.append("  <tr>\n");
				out.append(fmt.drawReadValue(".............................................................."));
				out.append(fmt.drawReadValue("............................................................."));
				out.append(fmt.drawReadValue("............................................................"));
				out.append("  </tr>\n");
				out.append("  </table> \n");
				out.append("<table id=\"buttonTBL\" border=0 cellspacing=10 cellpadding=1 width= 748>\n");
				out.append("	<tr>\n");
				out.append("		<td>\n");

				if (Step == 1)
				{
					out.append("		   <table width= '99.5%'>\n");
					out.append("		      <tr>\n");
					out.append("		         <td align=\"right\">\n");
					out.append(fmt.drawButton("NXT",mgr.translate("PCMWWORKORDERQUOTATIONWIZNEXT1: Next>"),"onClick=checkMandato(1)"));
					out.append("&nbsp;\n");
					out.append(fmt.drawSubmit("CANCEL",mgr.translate("PCMWWORKORDERQUOTATIONWIZCANCEL1: Cancel"),"cancel"));
					out.append("			 </td>\n");
					out.append("		      </tr>\n");
					out.append("	           </table>\n");
				}
				else
				{
					out.append("                   <table width= '99.5%'>\n");
					out.append("		      <tr>\n");
					out.append("		         <td align=\"right\">\n");
					out.append(fmt.drawSubmit("PREV",mgr.translate("PCMWWORKORDERQUOTATIONWIZBACK1: <Back"),"prev"));
					out.append("&nbsp;&nbsp;\n");
					out.append(fmt.drawSubmit("CANCEL",mgr.translate("PCMWWORKORDERQUOTATIONWIZCANCEL1: Cancel"),"cancel"));
					out.append("&nbsp;\n");
					out.append(fmt.drawButton("FINISH",mgr.translate("PCMWWORKORDERQUOTATIONWIZFINISH1: Finish"),"onClick=checkMandato(2)"));
					out.append("			 </td>\n");
					out.append("		      </tr>\n");
					out.append("		   </table>\n");
				}
				out.append("		</td>\n");
				out.append("	</tr>\n");
				out.append("</table>	           \n");
			}

			out.append("   <p>\n");

			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
			appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

			appendDirtyJavaScript("function checkMandato(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("  if (i == 1)	\n");
			appendDirtyJavaScript("  {\n");
			appendDirtyJavaScript("   if( f.QUOTATION_ID.value == '' )  \n");
			appendDirtyJavaScript("            checkQuotationId(-1);\n");
			appendDirtyJavaScript("	  else\n");
			appendDirtyJavaScript("	  {\n");
			appendDirtyJavaScript("	   document.form.BUTTONNXT.value = \"AAAA\";\n");   
			appendDirtyJavaScript("	   f.submit();\n");
			appendDirtyJavaScript("	  }\n");
			appendDirtyJavaScript(" }\n");
			appendDirtyJavaScript("  if (i == 2)	\n");
			appendDirtyJavaScript("  {\n");
			appendDirtyJavaScript("   if( f.QUOTATION_ID.value == '' )  \n");
			appendDirtyJavaScript("            checkQuotationId(-1);\n");
			appendDirtyJavaScript("	  else\n");
			appendDirtyJavaScript("	  {\n");
			appendDirtyJavaScript("	   document.form.BUTTONVAL.value = \"AAAA\";\n");             
			appendDirtyJavaScript("	   f.submit();\n");
			appendDirtyJavaScript("	  }\n");
			appendDirtyJavaScript(" }\n");
			appendDirtyJavaScript(" }\n");

			appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("   el = document.getElementById(el);\n");
			appendDirtyJavaScript("   if(el.style.display!='none')\n");
			appendDirtyJavaScript("   {  \n");
			appendDirtyJavaScript("      el.style.display='none';\n");
			appendDirtyJavaScript("      mainTBL.style.display='none';\n");
			appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
			appendDirtyJavaScript("      lineTBL.style.display='none';\n");
			appendDirtyJavaScript("   }  \n");
			appendDirtyJavaScript("   else\n");
			appendDirtyJavaScript("   {  \n");
			appendDirtyJavaScript("      el.style.display='block';\n");
			appendDirtyJavaScript("      mainTBL.style.display='block';\n");
			appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
			appendDirtyJavaScript("      lineTBL.style.display='block';     \n");
			appendDirtyJavaScript("   }\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("function validateQuotationId(i)\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
			appendDirtyJavaScript("	setDirty();\n");
			appendDirtyJavaScript("	if( !checkQuotationId(i) ) return;\n");
			appendDirtyJavaScript(" window.status='Please wait for validation';\n");
			appendDirtyJavaScript("	r = __connect(\n");
			appendDirtyJavaScript("		'");
			appendDirtyJavaScript(mgr.getURL());
			appendDirtyJavaScript("?VALIDATE=QUOTATION_ID'\n");
			appendDirtyJavaScript("		+ '&QUOTATION_ID=' + URLClientEncode(getValue_('QUOTATION_ID',i))\n");
			appendDirtyJavaScript("		);\n");
			appendDirtyJavaScript(" window.status='';\n");
			appendDirtyJavaScript("	if( checkStatus_(r,'QUOTATION_ID',i,'Quotation Id') )\n");
			appendDirtyJavaScript("	{\n");
			appendDirtyJavaScript("		assignValue_('QUOTATION_ID',i,0);\n");
                        appendDirtyJavaScript("		assignValue_('QUOTATION_REV',i,1);\n");
			appendDirtyJavaScript("		assignValue_('STATE',i,2);\n");
			appendDirtyJavaScript("		assignValue_('QUOTATION_DESCRIPTION',i,3);\n");
			appendDirtyJavaScript("		assignValue_('CUSTOMER_ID',i,4);\n");
			appendDirtyJavaScript("		assignValue_('CUSTOMER_NAME',i,5);\n");
			appendDirtyJavaScript("	}\n");
			appendDirtyJavaScript("}\n");

			appendDirtyJavaScript("if ('");
			appendDirtyJavaScript(CancelFlag);
			appendDirtyJavaScript("'=='true')\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript("    self.close();\n");
			appendDirtyJavaScript("}\n");

			out.append(mgr.endPresentation());
			out.append("</form>\n");
			out.append("</body>\n");
		}
		else
		{
			appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
			appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
			appendDirtyJavaScript("window.location = '");

			if (toDefaultPage)
			{
				appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));	//XSS_Safe AMNILK 20070726
				appendDirtyJavaScript("';\n");
			}
			else
			{
				appendDirtyJavaScript(url);
				appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n"); 
			}                       

			appendDirtyJavaScript("    if (");
			appendDirtyJavaScript(    mgr.isExplorer());
			appendDirtyJavaScript("    )\n");
			appendDirtyJavaScript("    { \n");
			appendDirtyJavaScript("      window.open('");
			appendDirtyJavaScript(url);
			appendDirtyJavaScript("'+\"pcmw/WorkOrderQuotationWiz.page?RANDOM_VAL=\"+Math.random(),\"frmWOQuotationWiz\",\"resizable=yes, status=yes,menubar=no,height=490,width=750\");      \n");
			appendDirtyJavaScript("    } \n");
			appendDirtyJavaScript("    else \n");
			appendDirtyJavaScript("    { \n");
			appendDirtyJavaScript("      window.open('");
			appendDirtyJavaScript(url);
			appendDirtyJavaScript("'+\"pcmw/WorkOrderQuotationWiz.page?RANDOM_VAL=\"+Math.random(),\"frmWOQuotationWiz\",\"resizable=yes, status=yes,menubar=no,height=500,width=750\");      \n");
			appendDirtyJavaScript("    } \n");
		}

		out.append("</html>");
		return out;
	}
}
