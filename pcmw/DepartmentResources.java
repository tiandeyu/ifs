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
*  File        : DepartmentResources.java 
*  Modified    : ASP2JAVA Tool  010318  Created Using the ASP file DepartmentResources.asp
*  NUPELK  011012  Adjusted the number validation for time horizon
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040721  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  ILSOLK  070710  Eliminated XSS.
*  ILSOLK  070713  Eliminated XSS.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DepartmentResources extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.DepartmentResources");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String sOrgContract;
	private String sOrgCode;
	private String performRMB;
	private ASPTransactionBuffer trans;
	private String sContract;
	private String keys;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPQuery q;
	private ASPBuffer buffer;
	private String val;
	private ASPBuffer buf;
	private String txt;
	private String closeWin; 

	//===============================================================
	// Construction 
	//===============================================================
	public DepartmentResources(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		sOrgContract = "";
		sOrgCode =  "";
		performRMB = "";

		ASPManager mgr = getASPManager();   

		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();

		sOrgContract = ctx.readValue("CTXORGCONT","");
		sOrgCode = ctx.readValue("CTXORG","");
		sContract = ctx.readValue("CTXCON","");
		keys = ctx.readValue("KEYS","");
		performRMB  = ctx.readValue("PERFRMB",""); 
		closeWin = ctx.readValue("CLOSEWIN",closeWin); 

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			sOrgContract = row.getValue("ORG_CONTRACT"); 
			sOrgCode = row.getValue("ORG_CODE"); 
			closeWin = "true";

		}
		else if (mgr.buttonPressed("OK"))
			ok();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();

		startup();

		ctx.writeValue("CLOSEWIN", closeWin);
		ctx.writeValue("CTXORGCONT", sOrgContract);
		ctx.writeValue("CTXORG", sOrgCode);
		ctx.writeValue("CTXCON", sContract);
	}

//-----------------------------------------------------------------------------
//------------------------  UTILITY FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

	public void startup()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");                                                                                  

		mgr.submit(trans);

		trans.clear();

		if (!mgr.isEmpty(sOrgCode) && !mgr.isEmpty(sOrgContract))
		{
			buffer = headset.getRow();
			buffer.setValue("ORG_CONTRACT",sOrgContract);
			buffer.setValue("ORG_CODE",sOrgCode);
			headset.setRow(buffer);   
		}
	}

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("SYS_DATE".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("SYS_DATE",mgr.readValue("SYS_DATE"));

			txt = mgr.readValue("SYS_DATE") +"^";
			mgr.responseWrite(txt);      
		}

		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//----------------------------  CUSTOM FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void createURL( String frm) 
	{
		keys = buffer.format();
		buffer.clear();
		performRMB = frm;
	}

	public void ok()
	{
		ASPManager mgr = getASPManager();  

		String selectedOrgCont = mgr.readValue("ORG_CONTRACT");
		String selectedOrg = mgr.readValue("ORG_CODE");

		try
		{
			int val1 = Integer.parseInt(mgr.readValue("PAST_WEEKS"));
			int val2 = Integer.parseInt(mgr.readValue("FOLLOW_WEEKS"));

			buffer = mgr.newASPBuffer();
			row = buffer.addBuffer("0");
			row.addItem("ORG_CONTRACT",mgr.readValue("ORG_CONTRACT"));
			row.addItem("ORG_CODE",mgr.readValue("ORG_CODE"));
			row.addItem("SYS_DATE",mgr.readValue("SYS_DATE"));
			row.addItem("PAST_WEEKS",mgr.readValue("PAST_WEEKS"));
			row.addItem("FOLLOW_WEEKS",mgr.readValue("FOLLOW_WEEKS"));

			createURL("DepartmentResourcesGraph"); 
		}
		catch (NumberFormatException e)
		{
			mgr.showAlert(mgr.translate("PCMWEMPLOYEERESOURCESINVALIDNUMBER: Invalid Number in the Time Horizon"));
		}

		buffer = headset.getRow();
		buffer.setValue("ORG_CONTRACT",selectedOrgCont);
		buffer.setValue("ORG_CODE",selectedOrg);
		headset.setRow(buffer);
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();

		mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("SYS_DATE","Date");                                                  
		f.setSize(25);
		f.setCustomValidation("SYS_DATE","SYS_DATE");
		f.setFunction("SYSDATE");
		f.setLabel("PCMWDEPARTMENTRESOURCESDATE: Date");                                                      
		f.setInsertable();        

		f = headblk.addField("ORG_CONTRACT");                                                  
		f.setSize(10); 
		f.setMandatory();                                                                         
		f.setUpperCase();
		f.setFunction("''");                                                       
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);                                                                                   
		f.setLabel("PCMWDEPARTMENTRESOURCESDEPTSITE: Organization Site");                                                     
		f.setMaxLength(5);                                                                
		f.setInsertable();   

		f = headblk.addField("ORG_CODE");                                                  
		f.setSize(15); 
		f.setMandatory();                                                                         
		f.setUpperCase();
		f.setFunction("''");                                                       
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,450);                                                                                   
		f.setLabel("PCMWDEPARTMENTRESOURCESDEPT: Maintenance Organization");                                                     
		f.setMaxLength(8);                                                                
		f.setInsertable();                                                                 

		f = headblk.addField("PAST_WEEKS");                                                 
		f.setLabel("PCMWDEPARTMENTRESOURCESPASTWKS: Past Weeks");                                                     
		f.setFunction("1");                                                       
		f.setMaxLength(40);                                                                
		f.setInsertable();                                                                 
		f.setSize(25);   

		f = headblk.addField("FOLLOW_WEEKS");                                                 
		f.setLabel("PCMWDEPARTMENTRESOURCESFOLLWKS: Following Weeks");                                                     
		f.setFunction("3");                                                       
		f.setMaxLength(40);                                                                
		f.setInsertable();                                                                 
		f.setSize(25);   

		f = headblk.addField("WEEK_FORMAT");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_DATE");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM1");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM2");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM3");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("TEM_NUM4");
		f.setFunction("''");
		f.setHidden();

		f = headblk.addField("SYSDATE","Date");
		f.setHidden();  

		headblk.setView("DUAL");                  
		headset = headblk.getASPRowSet();                         

		headlay = headblk.getASPBlockLayout();                    
		headlay.setDialogColumns(1);
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();  
		headlay.defineGroup(mgr.translate("PCMWDEPARTMENTRESOURCESTIMEDEP: Time and Maintenance Organization"),"SYS_DATE,ORG_CONTRACT,ORG_CODE",true,true);
		headlay.defineGroup(mgr.translate("PCMWDEPARTMENTRESOURCESNUMWEEK: Number of Weeks in the Time Horizon"),"PAST_WEEKS,FOLLOW_WEEKS",true,true);

		headtbl = mgr.newASPTable(headblk);                       
		headtbl.setTitle(mgr.translate("PCMWDEPARTMENTRESOURCESMAST: Parameters to Resource Graph"));  
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);                  
		headbar.disableCommand(headbar.REMOVE);

		headbar.disableCommand(headbar.PREVIOUS);                
		headbar.disableCommand(headbar.NEXT);                    
		headbar.disableCommand(headbar.COUNT);                   
		headbar.disableCommand(headbar.DUPLICATE);               
		headbar.disableCommand(headbar.FIND);                    
		headbar.disableCommand(headbar.OKFIND);                  
		headbar.disableCommand(headbar.COUNTFIND);               
		headbar.disableCommand(headbar.EDIT);                    
		headbar.disableCommand(headbar.CANCELFIND);              
		headbar.disableCommand(headbar.CANCELEDIT);              
		headbar.disableCommand(headbar.BACK);  
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");                        
		headbar.defineCommand(headbar.SAVERETURN,"ok");               
		headbar.enableCommand(headbar.CANCELNEW);                
		headbar.defineCommand(headbar.CANCELNEW,"cancel");   
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWDEPARTMENTRESOURCESTITLE: Parameters to Resource Graph";
	}

	protected String getTitle()
	{
		return "PCMWDEPARTMENTRESOURCESTITLE: Parameters to Resource Graph";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<table bgcolor=\"white\" width=\"727\" id=\"cntHEAD\" class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("<tr><td>\n");
		appendToHTML(headlay.generateDataPresentation());
		appendToHTML("</td></tr>\n");
		appendToHTML("<tr>		\n");
		appendToHTML("       <td align=\"left\"> \n");
		appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWDEPARTMENTRESOURCESBUTOK:   OK   "),"onFocus='checkHeadFields()'"));
		appendToHTML("&nbsp;\n");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWDEPARTMENTRESOURCESBUTCANCEL: Cancel"),""));
		appendToHTML("&nbsp;\n");
		appendToHTML("       </td>   \n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("' != \"\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  keys = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(keys)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  winName  = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("'.substr('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("'.lastIndexOf('/')+1);\n");
		appendDirtyJavaScript("  window.open('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("'+\".page?__TRANSFER=\"+URLClientEncode(keys),winName,\"scrollbars,resizable,status=yes,width=770,height=620\");\n");
		appendDirtyJavaScript("  formname = \"\";\n");
		appendDirtyJavaScript(" if('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin)); // Bug Id 68773
		appendDirtyJavaScript("' == 'true')\n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("}\n");
	}
}
