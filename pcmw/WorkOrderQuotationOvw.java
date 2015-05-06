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
*  File        : WorkOrderQuotationOvw.java 
*  Created     : ASP2JAVA Tool  010312
*  Modified    :
*  JEWILK  010315  Corrected conversion errors and done adjustments.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  JEWILK  031015  Modified method adjust() to call methods checkObjAvailable() and adjustActions(). Call 106584.
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  SAPRLK  040422  Web Alignment - Added multirow action, removed unnecessary global variables, simplified code for RMBs.   
*  ARWILK  041112  Replaced getContents with printContents.
*  NIJALK  050401  Call 122926: Removed field MCH_CODE.
*  NEKOLK  060228  Call 135721.Modified prepareQuotation(),prepareWorkOrder(),reportInWorkOrder().
*  THWILK  060308  Call 136757,Modified predefine().
*  NEKOLK  060510  Bug ID 57371,Modified  prepareWorkOrder(),reportInWorkOrder() and added historicalWork()
*                                 And modified reopen().
*  AMNILK  060629  Merged with SP1 APP7.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderQuotationOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderQuotationOvw");

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
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private boolean isSecurityChecked;
	private ASPBuffer actionsBuffer;
	//Web Alignment - simplify code for RMBs
	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
   private String calling_url;
	//
   private ASPCommand cmd;
   private boolean bOpenHisWindow;
   private String quotationid;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderQuotationOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();   

		isSecurityChecked = ctx.readFlag("SECURITYCHECKED", false);
		actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");

		if ( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
			okFind();
		adjust();

		ctx.writeFlag("SECURITYCHECKED", isSecurityChecked);
		ctx.writeBuffer("ACTIONSBUFFER", actionsBuffer);
	}


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		q.setOrderByClause("QUOTATION_ID");
		q.includeMeta("ALL");

		mgr.submit(trans);

		if ( headset.countRows() == 0 )
		{
			mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONOVWNODATA: No data found."));
			headset.clear();
		}
		mgr.createSearchURL(headblk);   
	}


	public void countFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	//Web Alignment - Enable Multirow Action
	private String createTransferUrl(String url, ASPBuffer object)
	{
		ASPManager mgr = getASPManager();

		try
		{
			String pkg = mgr.pack(object,1900 - url.length());
			char sep = url.indexOf('?')>0 ? '&' : '?';
			urlString = url + sep + "__TRANSFER=" + pkg ;
			return urlString;
		}
		catch (Throwable any)
		{
			return null;
		}
	}
	//

	public void prepareQuotation()
	{
		ASPManager mgr = getASPManager();

		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			count = headset.countSelectedRows();
		}
		else
		{
			headset.unselectRows();
			count = 1;
		}   

                transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
			{
				rowBuff.addItem("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
			}
			else
			{
				rowBuff.addItem(null, headset.getValue("QUOTATION_ID"));
			}

			transferBuffer.addBuffer("DATA", rowBuff);

			if (headlay.isMultirowLayout())
				headset.next();
		}


		if (headlay.isMultirowLayout())
			headset.setFilterOff();

   		urlString = createTransferUrl("WorkOrderQuotation.page", transferBuffer);

		newWinHandle = "wndPrepareWO"; 
                ctx.setCookie("NewWinOpen","true");
	}

	public void prepareWorkOrder()
	{
		ASPManager mgr = getASPManager();
		//Web Alignment - enable multirow action
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			count = headset.countSelectedRows();
		}
		else
		{
			headset.unselectRows();
			count = 1;
		}   

                transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
			{
				rowBuff.addItem("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
			}
			else
			{
				rowBuff.addItem(null, headset.getValue("QUOTATION_ID"));
			}

			transferBuffer.addBuffer("DATA", rowBuff);

			if (headlay.isMultirowLayout())
				headset.next();
		}


		if (headlay.isMultirowLayout())
			headset.setFilterOff();

   		urlString = createTransferUrl("ActiveSeparate2ServiceManagement.page", transferBuffer);
                
               //urlString = createTransferUrl("ActiveSeparate2ServiceManagement.page", headset.getSelectedRows("QUOTATION_ID"));


		newWinHandle = "wndPrepareWO"; 
                ctx.setCookie("NewWinOpen","true");
	}

	public void reportInWorkOrder()
	{
		ASPManager mgr = getASPManager();


		//Web Alignment - enable multirow action
		int count = 0;
		ASPBuffer transferBuffer;
		ASPBuffer rowBuff;

		bOpenNewWindow = true;

	        if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			count = headset.countSelectedRows();
		}
		else
		{
			headset.unselectRows();
			count = 1;
		}   

                transferBuffer = mgr.newASPBuffer();

		for (int i = 0; i < count; i++)
		{
			rowBuff = mgr.newASPBuffer();

			if (i == 0)
			{
				rowBuff.addItem("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
			}
			else
			{
				rowBuff.addItem(null, headset.getValue("QUOTATION_ID"));
			}

			transferBuffer.addBuffer("DATA", rowBuff);

			if (headlay.isMultirowLayout())
				headset.next();
		}


		if (headlay.isMultirowLayout())
			headset.setFilterOff();
                

   		urlString = createTransferUrl("ActiveSeperateReportInWorkOrderSM.page", transferBuffer);

	       // urlString = createTransferUrl("ActiveSeperateReportInWorkOrderSM.page", headset.getSelectedRows("QUOTATION_ID"));

		newWinHandle = "wndReportInWO"; 
                ctx.setCookie("NewWinOpen","true");
		
	}

       public void historicalWork()
       {
           ASPManager mgr = getASPManager();
                    if (headlay.isMultirowLayout())
                  {
                          headset.storeSelections();
                          headset.setFilterOn();
                  }
                  else
                  {
                          headset.unselectRows();
                  }   
                  if (headlay.isMultirowLayout())
                          headset.setFilterOff();
                  bOpenHisWindow = true;
                  if (headlay.isMultirowLayout())
                      headset.setFilterOff();
                  quotationid = headset.getRow().getValue("QUOTATION_ID");


                  ctx.setCookie("NewWinOpen","true");
	          newWinHandle = "wndHistWO"; 
        
    }
    public void perform( String command) 
	{

		ASPManager mgr = getASPManager();
		//Web Alignment - Multirow Action 
		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.markSelectedRows(command);
			mgr.submit(trans);
		}
		else
		{
			headset.unselectRows();
			headset.markRow(command);
			int currrow = headset.getCurrentRowNo();
			mgr.submit(trans);

			headset.goTo(currrow);
		}   
	}

	public void reOpen()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		String val = getObjState(headset.getValue("OBJID"));
      if ( mgr.isEmpty(val) || !("Rejected".equals(val)) )
         mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONOVWFLAG1: Cannot perform for selected line."));
      else
      {
         trans.clear();  
         cmd = trans.addCustomCommand("REOPEN","Work_Order_Quotation_API.Reopen");
         cmd.addParameter("QUOTATION_ID",headset.getRow().getValue("QUOTATION_ID"));
         trans=mgr.perform(trans);    

         mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONOVWFLAG7: The quotation was reopened!"));
         trans.clear(); 

      }   
   }

	public String  getObjState(String objId)
	{
		ASPManager mgr = getASPManager();

		String sqlStmt = "SELECT OBJSTATE FROM WORK_ORDER_QUOTATION_OVERVIEW WHERE OBJID = '" + objId + "'";
		trans.clear();
		trans.addQuery("GETSTATE",sqlStmt);
		trans = mgr.perform(trans);

		String objState = trans.getValue("GETSTATE/DATA/OBJSTATE");
		trans.clear();
		return objState;
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("OBJEVENTS");
		f.setHidden();

		f = headblk.addField("OBJSTATE");
		f.setHidden();

		f = headblk.addField("QUOTATION_ID","Number");
		f.setSize(12);
		f.setDynamicLOV("WORK_ORDER_QUOTATION",600,450); 
		f.setLabel("PCMWWORKORDERQUOTATIONOVWQUOTATION_ID: Quotation ID");
		f.setHyperlink("WorkOrderQuotation.page","QUOTATION_ID","NEWWIN");
		f.setReadOnly();

		f = headblk.addField("QUOTATION_REV");
		f.setSize(9);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWQUOTATION_REV: Revision");
		f.setReadOnly();
		f.setMaxLength(6);

		f = headblk.addField("QUOTATION_DESCRIPTION");
		f.setSize(24);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWQUOTATION_DESCRIPTION: Description");
		f.setReadOnly();
		f.setMaxLength(50);

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);  
		f.setLabel("PCMWWORKORDERQUOTATIONOVWCONTRACT: Site");
		f.setReadOnly();
		f.setMaxLength(5);

		f = headblk.addField("STATE");
		f.setSize(10);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWSTATE: State");
		f.setReadOnly();
		f.setMaxLength(253);

		f = headblk.addField("VALID_FROM","Date");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWVALID_FROM: Valid From");
		f.setReadOnly();

		f = headblk.addField("VALID_TO","Date");
		f.setSize(12);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWVALID_TO: Valid To");
		f.setReadOnly();

		f = headblk.addField("QUOTATION_DATE","Date");
		f.setSize(15);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWQUOTATION_DATE: Quotation Date");
		f.setReadOnly();

		f = headblk.addField("SALESMAN_CODE");
		f.setSize(15);
		f.setDynamicLOV("SALES_PART_SALESMAN",600,450); 
		f.setLabel("PCMWWORKORDERQUOTATIONOVWSALESMAN_CODE: Salesman Code");
		f.setReadOnly();
		f.setMaxLength(5);

		f = headblk.addField("SALESPARTSALESMANNAME");
		f.setSize(20);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWSALESPARTSALESMANNAME: Salesman");
		f.setFunction("SALES_PART_SALESMAN_API.Get_Name(:SALESMAN_CODE)");
		f.setReadOnly();
		f.setMaxLength(2000);

		f = headblk.addField("CUSTOMER_ID");
		f.setSize(12);
		f.setDynamicLOV("CUSTOMER_INFO",600,450); 
		f.setLabel("PCMWWORKORDERQUOTATIONOVWCUSTOMER_ID: Customer ID");
		f.setReadOnly();
		f.setMaxLength(20);

		f = headblk.addField("CUSTOMERINFONAME");
		f.setSize(22);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWCUSTOMERINFONAME: Customer Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_ID)");
		f.setReadOnly();
		f.setMaxLength(2000);

		f = headblk.addField("WO_NO","Number");
		f.setSize(15);
		f.setDynamicLOV("WORK_ORDER",600,450);  
		f.setLabel("PCMWWORKORDERQUOTATIONOVWWO_NO: Parent WO No");
		f.setReadOnly();
		f.setMaxLength(8);
                f.setHidden();

                f = headblk.addField("STATISTICAL_CODE");
		f.setSize(21);
		f.setDynamicLOV("QUOTATION_STATISTICAL_CODE",600,450); 
		f.setLabel("PCMWWORKORDERQUOTATIONOVWSTATISTICAL_CODE: Statistical Code");
		f.setReadOnly();
		f.setMaxLength(10);

		f = headblk.addField("STATISTICAL_NOTE");
		f.setSize(21);
		f.setLabel("PCMWWORKORDERQUOTATIONOVWSTATISTICAL_NOTE: Statistical Note");
		f.setReadOnly();
		f.setMaxLength(2000);
      
      f = headblk.addField("CHECK_HIST_WO_EXIST");
      f.setFunction("HISTORICAL_SEPARATE_API.Has_His_Wos(:QUOTATION_ID)");
      f.setHidden();

      f = headblk.addField("CHECK_ACT_WO_EXIST");
      f.setFunction("ACTIVE_SEPARATE_API.Has_Act_Wos(:QUOTATION_ID)");
      f.setHidden();


		headblk.setView("WORK_ORDER_QUOTATION_OVERVIEW");
		headblk.defineCommand("WORK_ORDER_QUOTATION_API","New__,Modify__,Remove__,Reopen__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWORKORDERQUOTATIONOVWHD: Work Order Quotation"));
		headtbl.setWrap();

		//Web Alignment - Multirow Action
		headtbl.enableRowSelect();
		//

		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.DUPLICATEROW);

		headbar.addCustomCommand("reOpen", mgr.translate("PCMWWORKORDERQUOTATIONOVWREOPEN: Reopen"));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("prepareQuotation",  mgr.translate("PCMWWORKORDERQUOTATIONOVWPWOQ: Prepare Quotation..."));
		headbar.addCustomCommand("prepareWorkOrder",  mgr.translate("PCMWWORKORDERQUOTATIONOVWPWO: Prepare Work Order..."));
		headbar.addCustomCommand("reportInWorkOrder",  mgr.translate("PCMWWORKORDERQUOTATIONOVWRIWO: Report In Work Order..."));
      headbar.addCustomCommand("historicalWork",  mgr.translate("PCMWWORKORDERQUOTATIONHISTWO: Historical Work Order..."));
		//Web Alignment - Multirow Action
		headbar.enableMultirowAction();
		headbar.addCommandValidConditions("reOpen", "OBJSTATE", "Enable", "Rejected");

		headbar.addCommandValidConditions("prepareQuotation", "OBJSTATE", "Disable", "Rejected");
      headbar.addCommandValidConditions("historicalWork", "CHECK_HIST_WO_EXIST","Disable","FALSE");
      headbar.addCommandValidConditions("prepareWorkOrder", "CHECK_ACT_WO_EXIST","Disable","FALSE");
      headbar.addCommandValidConditions("reportInWorkOrder", "CHECK_ACT_WO_EXIST","Disable","FALSE");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		b = mgr.newASPBlock("STATE");

		b.addField("CLIENT_VALUES0");
	}

	public void checkObjAvailable()
	{
		ASPManager mgr = getASPManager();

		if ( !isSecurityChecked )
		{
			trans.clear();

			trans.addSecurityQuery("WORK_ORDER_QUOTATION,ACTIVE_SEPARATE");

			trans.addPresentationObjectQuery("PCMW/WorkOrderQuotation.page,"+
											 "PCMW/ActiveSeparate2.page,"+
											 "PCMW/ActiveSeperateReportInWorkOrder.page");

			trans = mgr.perform(trans);

			ASPBuffer secViewBuff = trans.getSecurityInfo();

			actionsBuffer = mgr.newASPBuffer();

			if ( secViewBuff.itemExists("WORK_ORDER_QUOTATION") && secViewBuff.namedItemExists("PCMW/WorkOrderQuotation.page") )
				actionsBuffer.addItem("okHeadPrepareQuotation","");

			if ( secViewBuff.itemExists("ACTIVE_SEPARATE") )
			{
				if ( secViewBuff.namedItemExists("PCMW/ActiveSeparate2.page") )
					actionsBuffer.addItem("okHeadPrepareWorkOrder","");
				if ( secViewBuff.namedItemExists("PCMW/ActiveSeperateReportInWorkOrder.page") )
					actionsBuffer.addItem("okHeadReportInWorkOrder","");
			}

			isSecurityChecked = true;
		}
	}

	public void adjustActions()
	{
		ASPManager mgr = getASPManager();

		// Removing actions which are not allowed to the user.
		// headbar
		if ( !actionsBuffer.itemExists("okHeadPrepareQuotation") )
			headbar.removeCustomCommand("prepareQuotation");

		if ( !actionsBuffer.itemExists("okHeadPrepareWorkOrder") )
			headbar.removeCustomCommand("prepareWorkOrder");

		if ( !actionsBuffer.itemExists("okHeadReportInWorkOrder") )
			headbar.removeCustomCommand("prepareQuotation");

	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();


		if ( mgr.isPresentationObjectInstalled("orderw/SalesPartSalesmanOvw.page") )
			mgr.getASPField("SALESMAN_CODE").setHyperlink("../orderw/SalesPartSalesmanOvw.page","SALESMAN_CODE","NEWWIN");
		if ( mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page") )
			mgr.getASPField("CUSTOMER_ID").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_ID","NEWWIN");
		//if ( mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page") )
		  //      mgr.getASPField("AGREEMENT_ID").setHyperlink("../orderw/CustomerAgreement.page","AGREEMENT_ID","NEWWIN");

		checkObjAvailable();
		adjustActions();

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERQUOTATIONOVWDESCRIPTION: Overview - Work Order Quotation";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERQUOTATIONOVWDESCRIPTION: Overview - Work Order Quotation";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("window.name = \"PrepareWOQuotation\";\n");

	    
                if (bOpenHisWindow)
		{
                    appendDirtyJavaScript("if (readCookie('NewWinOpen')=='true')\n");
                    appendDirtyJavaScript("{\n");
                    appendDirtyJavaScript("   removeCookie('NewWinOpen',COOKIE_PATH);\n");    
                    appendDirtyJavaScript("   jquotationid='");
                    appendDirtyJavaScript(mgr.encodeStringForJavascript(quotationid));		//XSS_Safe AMNILK 20070726
                    appendDirtyJavaScript("';\n");
                    appendDirtyJavaScript("   window.open(\"HistoricalSeparateRMB.page?QUOTATION_ID=\"+URLClientEncode(jquotationid),\"wndHistWO\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
                    appendDirtyJavaScript("}\n");

                }

                if (bOpenNewWindow)
		{
		        appendDirtyJavaScript("if (readCookie('NewWinOpen')=='true')\n");
                        appendDirtyJavaScript("{\n");
                        appendDirtyJavaScript("   removeCookie('NewWinOpen',COOKIE_PATH);\n");    
                        appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070726
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
		        appendDirtyJavaScript("}\n");
                }
		//
	}
}
