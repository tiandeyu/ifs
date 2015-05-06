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
*  File        : ActiveRoundFinishConfirmDlg.java 
*  Created     : ARWILK  010801
*  Modified    :
*  JAPELK  020301  Call Id : 78415 - Changed the finishOk() method.
*  VAGULK  030821  Bud Id  : 30786 - Made actual date to be displayed automatically.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041110  Replaced getContents with printContents.
*  NIJALK  060131  Call 132030: Modified finishOk().
*  THWILK  060201  Call 132040 : Modified predefine().
*  JEWILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060905  Merged bug 58216.
*  ILSOLK  070705  Eliminated XSS. 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveRoundFinishConfirmDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveRoundFinishConfirmDlg");

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
	private String routeWoNo;
	private String queryStrVal;
	private String parentWinHandle;
	private String reportedInBy;
	private String sRealFDate;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private String sRealFDateStr;
	private String sItemRealFDate;
	private String txt;
	private String val;
	private String title;
	private boolean openFormInNewWin;  

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveRoundFinishConfirmDlg(ASPManager mgr, String page_path)
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

		parentWinHandle = ctx.readValue("CTXPARWINHAND","");     
		queryStrVal = ctx.readValue("CTXQRYSTRVAL","");


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("ROUTE_WO_NO")))
		{
			routeWoNo = mgr.readValue("ROUTE_WO_NO"); 
			queryStrVal = mgr.readValue("QRYSTRVAL"); 
			parentWinHandle = mgr.readValue("WINHANDVAL");
			okFind();
		}

		adjust();

		ctx.writeValue("CTXPARWINHAND",parentWinHandle);
		ctx.writeValue("CTXQRYSTRVAL",queryStrVal);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("REAL_F_DATE".equals(val))
		{
			ASPBuffer buf = mgr.newASPBuffer();
			buf.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE",""));
			mgr.responseWrite(buf.getFieldValue("REAL_F_DATE") +"^" );
		}

		mgr.endResponse();
	}


	public void okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",routeWoNo);
		q.setOrderByClause("WO_NO");
		q.includeMeta("ALL");

		mgr.submit(trans);

		trans.clear();

		cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE REAL_F_DATE FROM DUAL");
		trans = mgr.perform(trans);
		String sysDate = trans.getBuffer("SUYSTDATE/DATA").getValue("REAL_F_DATE");

		trans.clear();

		ASPBuffer buff = headset.getRow();
		buff.setValue("REAL_F_DATE",sysDate);
		headset.setRow(buff);

		int currrow = headset.getCurrentRowNo();

		headlay.setLayoutMode(headlay.EDIT_LAYOUT);
		cmd = trans.addEmptyCommand("HEAD","ACTIVE_ROUND_API.Modify__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		headset.goTo(currrow);    
	}


	 public void finishOk()
	{
		ASPManager mgr = getASPManager();

		reportedInBy = mgr.readValue("REPORT_IN_BY_ID","");
		headset.changeRow();

		if (mgr.isEmpty(reportedInBy))
		{
			mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCONFIRM: This signature is not registered."));        
		}
		else
		{

			cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","DLG_REPORTED_IN_BY_ID");
			cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
			cmd.addParameter("REPORT_IN_BY_ID",mgr.readValue("REPORT_IN_BY_ID",""));

			trans = mgr.perform(trans);

			String reportedInById = trans.getValue("REPBYID/DATA/DLG_REPORTED_IN_BY_ID");

			trans.clear();

			int currrow = headset.getCurrentRowNo();

			ASPBuffer temp1 = headset.getRow();
			temp1.setValue("REPORT_IN_BY_ID",reportedInById);
			headset.setRow(temp1); 

			headset.setEdited("REPORT_IN_BY_ID,REAL_F_DATE");        
			//headset.changeRow();

			mgr.submit(trans);
			trans.clear();
			headset.goTo(currrow); 

			headset.markRow("FINISH__");
			mgr.submit(trans); 
			headset.goTo(currrow);

                        queryStrVal = queryStrVal + "&WOFINISHED=Y";
			openFormInNewWin = true;
		}
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("OBJSTATE");
		f.setHidden();

		f = headblk.addField("ATTR");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ACTION");
		f.setHidden();
		f.setFunction("''");      

		f = headblk.addField("INFO");
		f.setHidden();
		f.setFunction("''");     

		f = headblk.addField("WO_NO");
		f.setHidden();

		f = headblk.addField("REAL_F_DATE","Datetime");
		f.setSize(22);
		f.setMandatory();
		f.setLabel("PCMWACTIVEROUNDDLGREALFDATE: Actual Completion");
		f.setCustomValidation("REAL_F_DATE","REAL_F_DATE");   

		f = headblk.addField("REPORT_IN_BY_ID");
		f.setSize(11);
		f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
		f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDREPBYLOV: List of Signature")); 
		f.setLabel("PCMWACTIVEROUNDDLGREPORTEDBY: Signature");
		f.setUpperCase();
		f.setMandatory();

		f = headblk.addField("COMPANY");
		f.setSize(6);
		f.setHidden();

		f = headblk.addField("STATE");
		f.setSize(10);
		f.setReadOnly();
		f.setHilite();
		f.setLabel("PCMWACTIVEROUNDDLGSTATUS: Status"); 
		f.setHidden();

		f = headblk.addField("DLG_REPORTED_IN_BY_ID");
		f.setHidden();
		f.setFunction("''");

		headblk.setView("ACTIVE_ROUND");
		headblk.defineCommand("ACTIVE_ROUND_API","Modify__,FINISH__");
		headblk.disableDocMan();
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATE);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.BACK);
		headbar.defineCommand(headbar.SAVERETURN,"finishOk","checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,null,"openParentForm()");     

		headtbl = mgr.newASPTable(headblk);

		headtbl.setTitle(mgr.translate("PCMWACTROUNDCONFIRM: Report In Route"));
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
		headlay.setEditable();
		headlay.setDialogColumns(1);
		headlay.defineGroup(mgr.translate("REPINVALHADLE: Report In Values"),"REAL_F_DATE,REPORT_IN_BY_ID",true,true,1);    

                enableConvertGettoPost();
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		title = mgr.translate("PCMWACTROUNDCONFIRM: Report In Route");     
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return title;
	}

	protected String getTitle()
	{
		return "PCMWACTROUNDCONFIRM: Report In Route";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

                // XSS_Safe ILSOLK 20070705  
		appendDirtyJavaScript(" if (");
		appendDirtyJavaScript(openFormInNewWin);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("      window.open(\"");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(queryStrVal));
		appendDirtyJavaScript("\",\"");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(parentWinHandle));
		appendDirtyJavaScript("\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("window.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function openParentForm()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
