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
*  File        : WorkOrderRolePlanningLov.java 
*  Created     : ASP2JAVA Tool  010328  Created Using the ASP file WorkOrderRolePlanningLov.asp
*  Modified    :
*  JEWILK  010618  Removed 'Ok' button and added it as a command bar function.
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040316  Call ID -112876 ,Corrected the error in Time Report tab,modified okCreate().
*  VAGULK  040317  Replaced getContents() with printContents(),mgr.submit() with mgr.querysubmit().
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  AMNILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060906  Merged with the Bug ID 58216
*  SHAFLK  060914  Bug 60342, Added new fields TEAM_ID, TEAM_CONTRACT.
*  AMNILK  060926  Merged Bug Id: 60342.
*  AMDILK  070522  Call ID 144902: Saved the callers last query
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  HARPLK  090901  Bug 83757,Modified okCreate().
*  INROLK  091027  Bug 86298, Modified okCreate().
*  NIJALK  100426  Bug 85045, Modified okCreate() and preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderRolePlanningLov extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderRolePlanningLov");

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
	private String rmbName;
	private String rowToEdit;
	private String cancelPath;
	private boolean singleMode;
	private boolean pressedOkBut;
        private boolean bInvokeCallerFunction;
	private String passedWoNo;
	private String url;
	private ASPTransactionBuffer trans;
	private String actionCanceled;
	private String goToPrevFrm;
	private String sWoNo;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderRolePlanningLov(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"); 
		fmt = mgr.newASPHTMLFormatter(); 
		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		frmname        = ctx.readValue("THIFRMNAEM",frmname);
		frmPath        = ctx.readValue("THIFRMPAT",frmPath);
		actionCanceled = ctx.readValue("ACTICANC","false");
		goToPrevFrm    = ctx.readValue("GOTPREFR","false");
		rmbName        = ctx.readValue("CALLRMNAM",rmbName);
		rowToEdit      = ctx.readValue("ROWTEDTI");
		cancelPath     = ctx.readValue("CANCPATH",cancelPath);
		passedWoNo     = ctx.readValue("PASSWONO");
		calling_url    = ctx.readValue("CALLIUR",calling_url);

		singleMode     = ctx.readFlag("SINGTRUE",false);
		pressedOkBut   = ctx.readFlag("PREOKBUT",false);
                bInvokeCallerFunction = ctx.readFlag("OPENCALLERFUNC", false);

		if (mgr.commandBarActivated())
		{
			eval(mgr.commandBarFunction());
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			sWoNo = mgr.readValue("WO_NO");
			passedWoNo = sWoNo;
			frmname = mgr.readValue("FRMNAME");
			rmbName = mgr.readValue("RMBNAME");

			calling_url    =    ctx.getGlobal(   "CALLING_URL");

			if ("'ConnectPlan'".equals(rmbName))
			{
				rowToEdit = mgr.readValue("EDITROWNUM");
			}

			okFind();

		}
		else if (mgr.dataTransfered())
			okFind();
		else if (mgr.buttonPressed("CREATEPLAN"))
			okCreate();
		else if (mgr.buttonPressed("CRECANCEL"))
			cancelCreate();

		adjust();   

		ctx.writeValue("THIFRMNAEM",frmname);
		ctx.writeValue("ACTICANC",actionCanceled);
		ctx.writeValue("GOTPREFR",goToPrevFrm);
		ctx.writeValue("THIFRMPAT",frmPath);
		ctx.writeValue("CALLRMNAM",rmbName);
		ctx.writeValue("ROWTEDTI",rowToEdit);
		ctx.writeValue("CANCPATH",cancelPath);
		ctx.writeValue("PASSWONO",passedWoNo);
		ctx.writeValue("CALLIUR",calling_url);

		ctx.writeFlag("SINGTRUE",singleMode);
		ctx.writeFlag("PREOKBUT",pressedOkBut);
                ctx.writeFlag("OPENCALLERFUNC", bInvokeCallerFunction);
	}

	private String fixNull(String str)
	{
		ASPManager mgr = getASPManager();
		if (mgr.isEmpty(str))
			return "";
		return mgr.URLEncode(str);
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		q.addWhereCondition("WO_NO= ?");
		q.addParameter("DESCRIPTION",passedWoNo);

		mgr.querySubmit(trans, headblk);
	}


	public void okCreate()
	{

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		if ("CreatePlan".equals(rmbName))
		{
			//Bug Id 86298 Start
         ctx.removeGlobal("NEWCREVAL_EMPID");
         ctx.removeGlobal("NEWCREVAL_DESCRIPTION");
         ctx.removeGlobal("NEWCREVAL_ORG_CODE");
	 //Bug 85045, Start
         ctx.removeGlobal("NEWCREVAL_ORG_CONTRACT");
	 //Bug 85045, End
         ctx.removeGlobal("NEWCREVAL_ROLE_CODE");
         ctx.removeGlobal("NEWCREVAL_TEAM_CONTRACT");
         ctx.removeGlobal("NEWCREVAL_TEAM_ID");
         ctx.removeGlobal("NEWCREVAL_SIGN");
         ctx.removeGlobal("NEWCREVAL_EMP");
         ctx.removeGlobal("NEWCREVAL_PLAN_LINE_NO");
         ctx.removeGlobal("NEWCREVAL_CONTRACT");
         ctx.removeGlobal("NEWCREVAL_SALNO");
         ctx.removeGlobal("NEWCREVAL_PRICE_LIST_NO");
         ctx.removeGlobal("NEWCREVAL_SALES_PRICE");
         ctx.removeGlobal("NEWCREVAL_DISCOUNT");
         //Bug Id 86298 End
         
         frmPath = calling_url+"?NEWCREVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"))+
					  "&NEWCREVAL_EMPID="+fixNull(headset.getRow().getValue("EMPID"))+
					  "&NEWCREVAL_DESCRIPTION="+fixNull(headset.getRow().getValue("DESCRIPTION"))+
					  "&NEWCREVAL_ORG_CODE="+fixNull(headset.getRow().getValue("ORG_CODE"))+
					  //Bug 85045, Start
					  "&NEWCREVAL_ORG_CONTRACT="+fixNull(headset.getRow().getValue("CONTRACT"))+
					  //Bug 85045, End
					  "&NEWCREVAL_ROLE_CODE="+fixNull(headset.getRow().getValue("ROLE_CODE"))+
                                          "&NEWCREVAL_TEAM_CONTRACT="+fixNull(headset.getRow().getValue("TEAM_CONTRACT"))+
                                          "&NEWCREVAL_TEAM_ID="+fixNull(headset.getRow().getValue("TEAM_ID"))+
					  "&NEWCREVAL_SIGN="+fixNull(headset.getRow().getValue("SIGN"))+   
                 //Bug 83757,start
                 "&NEWCREVAL_EMP="+fixNull(headset.getRow().getValue("SIGN_ID"))+ 
                 //Bug 83757,end
					  "&NEWCREVAL_PLAN_LINE_NO="+fixNull(headset.getRow().getValue("PLAN_LINE_NO"))+
					  "&NEWCREVAL_CONTRACT="+fixNull(headset.getRow().getValue("CATALOG_CONTRACT"))+            
					  "&NEWCREVAL_SALNO="+fixNull(headset.getRow().getValue("CATALOG_NO"))+            
					  "&NEWCREVAL_PRICE_LIST_NO="+fixNull(headset.getRow().getValue("PRICE_LIST_NO"))+            
					  "&NEWCREVAL_SALES_PRICE="+fixNull(headset.getRow().getValue("SALES_PRICE"))+            
					  "&NEWCREVAL_DISCOUNT="+fixNull(headset.getRow().getValue("DISCOUNT"));            

			cancelPath =  calling_url+"?NEWCANCELVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"))+
						  "&NEWCREVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"));    

         ctx.setGlobal("NEWCREVAL_WO",headset.getRow().getValue("WO_NO"));
			ctx.setGlobal("NEWCREVAL_EMPID",headset.getRow().getValue("EMPID"));
			ctx.setGlobal("NEWCREVAL_DESCRIPTION",headset.getRow().getValue("DESCRIPTION"));
			ctx.setGlobal("NEWCREVAL_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
			//Bug 85045, Start
			ctx.setGlobal("NEWCREVAL_ORG_CONTRACT",headset.getRow().getValue("CONTRACT"));
			//Bug 85045, End
			ctx.setGlobal("NEWCREVAL_ROLE_CODE",headset.getRow().getValue("ROLE_CODE"));
			ctx.setGlobal("NEWCREVAL_TEAM_CONTRACT",headset.getRow().getValue("TEAM_CONTRACT"));
			ctx.setGlobal("NEWCREVAL_TEAM_ID",headset.getRow().getValue("TEAM_ID"));
			ctx.setGlobal("NEWCREVAL_SIGN",headset.getRow().getValue("SIGN"));
         //Bug 83757,start
         ctx.setGlobal("NEWCREVAL_EMP",headset.getRow().getValue("SIGN_ID"));
         //Bug 83757,end
			ctx.setGlobal("NEWCREVAL_PLAN_LINE_NO",headset.getRow().getValue("PLAN_LINE_NO"));
			ctx.setGlobal("NEWCREVAL_CONTRACT",headset.getRow().getValue("CATALOG_CONTRACT"));
			ctx.setGlobal("NEWCREVAL_SALNO",headset.getRow().getValue("CATALOG_NO"));
			ctx.setGlobal("NEWCREVAL_PRICE_LIST_NO",headset.getRow().getValue("PRICE_LIST_NO"));
			ctx.setGlobal("NEWCREVAL_SALES_PRICE",headset.getRow().getValue("SALES_PRICE"));
			ctx.setGlobal("NEWCREVAL_DISCOUNT",headset.getRow().getValue("DISCOUNT"));

		}

		else if ("ConnectPlan".equals(rmbName))
		{
			frmPath = calling_url+"?NEWCONNVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"))+
					  "&NEWCONNVAL_CONTRACT="+fixNull(headset.getRow().getValue("CATALOG_CONTRACT"))+
					  "&NEWCONNVAL_SALNO="+fixNull(headset.getRow().getValue("CATALOG_NO"))+
					  "&NEWCONNVAL_PLANO="+fixNull(headset.getRow().getValue("PLAN_LINE_NO"))+
					  "&NEWCONNVAL_ROWNO="+fixNull(rowToEdit);


			cancelPath =  calling_url+"?NEWCANCELVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"))+            
						  "&NEWCONNVAL_WO="+fixNull(headset.getRow().getValue("WO_NO"))+
						  "&NEWCONNVAL_ROWNO="+fixNull(rowToEdit);


		}

                if ( "/b2e/secured/pcmw/ActiveSeperateReportInWorkOrderSM.page".equals(calling_url) ) 
                    bInvokeCallerFunction = true;
		else
        	    pressedOkBut = true;          
 
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
		setSize(15).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVWONO: WO Number");

		headblk.addField("ROW_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVROWNO: Row Number");

		headblk.addField("DESCRIPTION").
		setSize(40).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVDESCRIPTIONWO: Description");

		headblk.addField("ORG_CODE").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVORGCODE: Maintenance Organization").
		setUpperCase();

		headblk.addField("ROLE_CODE").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVROLECODE: Craft").
		setUpperCase();

		headblk.addField("SIGN").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVSIGNAT: Signature").
		setUpperCase();
      
      //Bug 83757,start
      headblk.addField("SIGN_ID").
		setSize(11).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVEMPID: Employee ID").
		setUpperCase();
      //Bug 83757,end

		headblk.addField("DATE_FROM","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVDASTFORM: Date From").
		setSize(20);

		headblk.addField("DATE_TO","Date").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVDATTO: Date To").
		setSize(20);

		headblk.addField("PLAN_LINE_NO","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVPLANLINNUMB: Plan Line No").
		setSize(20);

		headblk.addField("CATALOG_CONTRACT").
		setSize(5).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVCATCONTRA: Site");

		headblk.addField("CATALOG_NO").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVCATNUMB: Sales Part No").
		setUpperCase();

		headblk.addField("PRICE_LIST_NO").
		setSize(10).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVPRICELISTNO:  Price List No").
		setUpperCase();

		headblk.addField("QUANTITY","Number").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVQUANTI:  Quantity");

		headblk.addField("SALES_PRICE","Number").
		setLabel("PCMWWORKORDERROLEPLANNINGLOVSALEPRIC: Sales Price").
		setSize(20);

		headblk.addField("WORK_ORDER_INVOICE_TYPE").
		setSize(20).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVWOORDINVOTY: Work Order Invoice Type");   

		headblk.addField("DISCOUNT").
		setSize(8).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVDISCOUT: Discount").
		setUpperCase();

		headblk.addField("REFERENCE_NUMBER").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVREFNO: Reference Number").
		setUpperCase();

		headblk.addField("TEAM_CONTRACT").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVTEAMCONTRACT: Team Site").
		setUpperCase();

		headblk.addField("TEAM_ID").
		setSize(25).
		setLabel("PCMWWORKORDERROLEPLANNINGLOVTEAMID: Team Id").
		setUpperCase();

		headblk.addField("EMPID").
		setFunction("Company_Emp_API.Get_Max_Employee_Id('02',:SIGN)").
		setHidden();

		headblk.addField("EMPNAME").
		setFunction("Person_Info_API.Get_Name(:SIGN)").
		setHidden();  

		//Bug 85045, Start
		headblk.addField("CONTRACT").
		setHidden();
		//Bug 85045, End

		headblk.setView("WORK_ORDER_ROLE_PLANNING_LOV");  

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("okCreate",mgr.translate("PCMWWORKORDERROLEPLANNINGLOVOK: Ok"));
		headbar.removeCustomCommand(headbar.NEWROW);
		headblk.disableHistory();

		headtbl.setWrap();
		headtbl.unsetEditable();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		mgr.createSearchURL(headblk);
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWORKORDERROLEPLANNINGLOVTITLE: Plan Line No";
	}

	protected String getTitle()
	{
		return "PCMWWORKORDERROLEPLANNINGLOVTITLE: Plan Line No";
	}

	protected void printContents() throws FndException
	{ 
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
		appendToHTML("<table>\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td align=\"right\">\n");                
		appendToHTML(fmt.drawSubmit("CRECANCEL",mgr.translate("PCMWWORKORDERROLEPLANNINGLOVLACNCE: Cancel"),"onClick=self.close()"));
		appendToHTML("&nbsp;\n");
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(actionCanceled));	//XSS_Safe AMNILK 20070727
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		//appendDirtyJavaScript("  window.opener.location = '" + frmPath + "';\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n"); 

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(goToPrevFrm));	//XSS_Safe AMNILK 20070727
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.opener.location = '" + mgr.encodeStringForJavascript(frmPath) + "';\n"); //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n"); 

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(pressedOkBut);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.opener.location = '" + mgr.encodeStringForJavascript(frmPath) + "';\n");  //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n"); 

                appendDirtyJavaScript("if (");
		appendDirtyJavaScript(bInvokeCallerFunction);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  window.opener.refreshPermitBlock();\n");
		appendDirtyJavaScript("  window.close();\n");
		appendDirtyJavaScript("}\n"); 
	}
}
