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
*  File        : EmployeeResources.java 
*  Created     : ASP2JAVA Tool  010318  Created Using the ASP file EmployeeResources.asp
*  Modified    : 
*  NUPELK  011011  Added the employee validation
* ----------------------------------------------------------------------------
*  SHAFLK  030314  Modified HTML part. 
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  050401  Call 122927: Modified printContents().
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070507  Call Id 143572: Inserted a new field "SIGNATURE", Modified preDefine(), Ok()
*  AMDILK  070509  Call Id 143157: Modified preDefine() and validate()
*  ILSOLK  070710  Eliminated XSS.
*  AMDILK  070723  Call Id 146893: Inserted a new fild "Site" to the window
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class EmployeeResources extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.EmployeeResources");

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

	private ASPBlock headblk2;
	private ASPRowSet headset2;
	private ASPCommandBar headbar2;
	private ASPTable headtbl2;
	private ASPBlockLayout headlay2;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String performRMB;
	private ASPTransactionBuffer trans;
	private String sMchCode;
	private String sContract;
	private String keys;
	private ASPBuffer buff1;
	private ASPBuffer row1;
	private ASPQuery q;
	private ASPCommand cmd;
	private String con;
	private String sCompany;
	private ASPBuffer buffer;
	private String val;
	private ASPBuffer buf;
	private ASPBuffer buf1;
	private String txt;
	private ASPBuffer row; 

	//===============================================================
	// Construction 
	//===============================================================
	public EmployeeResources(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		performRMB = "";
		ASPManager mgr = getASPManager(); 

		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();

		sMchCode = ctx.readValue("CTXWONO","");
		sContract = ctx.readValue("CTXCON","");
		keys = ctx.readValue("KEYS","");
		performRMB  = ctx.readValue("PERFRMB","");

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			buff1 = mgr.getTransferedData();
			row1 = buff1.getBufferAt(0); 
			sMchCode = row1.getValue("MCH_CODE");
			row1 = buff1.getBufferAt(1); 
			sContract = row1.getValue("CONTRACT");
		}
		else if (mgr.buttonPressed("OK"))
			ok();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();

		startup();
		ctx.writeValue("CTXWONO",sMchCode);
		ctx.writeValue("CTXCON",sContract);
	}

//-----------------------------------------------------------------------------
//------------------------  UTILITY FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

	public void startup()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");   

		q = trans.addEmptyQuery(headblk2);
		q.includeMeta("ALL");                                                                                  

		mgr.submit(trans);

		trans.clear();

		cmd = trans.addCustomCommand("DEFCON", "User_Default_API.Get_User_Contract");
		cmd.addParameter("TEM_NUM1");                         
		trans = mgr.perform(trans);                                                                    
		con = trans.getValue("DEFCON/DATA/TEM_NUM1");    
		trans.clear();

		cmd = trans.addCustomFunction("DEFCOM", "Site_API.Get_Company","COMPANY");
		cmd.addParameter("TEM_NUM1",con);                         
		trans = mgr.perform(trans);                                                                    
		sCompany = trans.getValue("DEFCOM/DATA/COMPANY");  

		buffer = headset.getRow();
		buffer.setValue("COMPANY",sCompany);
		headset.setRow(buffer); 
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

		if ("PAST_WEEKS".equals(val))
		{

			buf1 = mgr.newASPBuffer();
			buf1.addFieldItem("PAST_WEEKS",mgr.readValue("PAST_WEEKS"));

			txt = mgr.readValue("PAST_WEEKS") +"^";
			mgr.responseWrite(txt); 
		}
		else if ("SIGNATURE".equals(val))
		{
		    String sSignature   = mgr.readValue("SIGNATURE");
		    String sEmpId       = mgr.readValue("EMPLOYEE_ID");
		    
		    if (sSignature.indexOf("^") > -1)
		    {
			String strAttr = sSignature;
			sSignature = strAttr.substring(0, strAttr.indexOf("^"));       
			sEmpId     = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());

		    }
	
		    txt =  (mgr.isEmpty(sSignature) ? "" : (sSignature)) + "^" +(mgr.isEmpty(sEmpId)?"":sEmpId)+ "^" ;

		    mgr.responseWrite(txt);
		}

		else if ("EMPLOYEE_ID".equals(val))
		{
		    String sSignature   = mgr.readValue("SIGNATURE");
		    String sEmpId       = mgr.readValue("EMPLOYEE_ID");
		    
		    if (sEmpId.indexOf("^") > -1)
		    {
			String strAttr = sEmpId;
			sSignature = strAttr.substring(0, strAttr.indexOf("^"));       
			sEmpId     = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
		    }
	
		    txt =  (mgr.isEmpty(sEmpId)?"":sEmpId)+ "^" ;

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

		try
		{

			int val1 = Integer.parseInt(mgr.readValue("PAST_WEEKS"));
			int val2 = Integer.parseInt(mgr.readValue("FOLLOW_WEEKS"));

			
			cmd = trans.addCustomCommand("CHECKEXIST2", "Company_Emp_API.Exist");
			cmd.addParameter("COMPANY");  
			cmd.addParameter("EMPLOYEE_ID");
			
			trans = mgr.perform(trans);                                                                    

			trans.clear();

			buffer = mgr.newASPBuffer();
			row = buffer.addBuffer("0");
			row.addItem("SIGNATURE",mgr.readValue("SIGNATURE"));
			row.addItem("EMPLOYEE_ID",mgr.readValue("EMPLOYEE_ID"));
			row.addItem("SYS_DATE",mgr.readValue("SYS_DATE"));
			row.addItem("PAST_WEEKS",mgr.readValue("PAST_WEEKS"));
			row.addItem("FOLLOW_WEEKS",mgr.readValue("FOLLOW_WEEKS"));
			row.addItem("OPTION",mgr.readValue("OPTION2"));
			row.addItem("CONTRACT",mgr.readValue("CONTRACT"));

			createURL("EmployeeResourcesGraph"); 


		}
		catch (NumberFormatException e)
		{
			mgr.showAlert(mgr.translate("PCMWEMPLOYEERESOURCESINVALIDNUMBER: Invalid Number in the Time Horizon"));
		}
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
		f.setLabel("PCMWEMPLOYEERESOURCESDATE: Date");                                                      
		f.setInsertable();                                                                 

		f = headblk.addField("COMPANY");
		f.setFunction("''");
		f.setHidden();  

		f = headblk.addField("CONTRACT");                                                  
		f.setSize(25); 
		f.setMandatory();                                                                         
		f.setUpperCase();
		f.setFunction("''");
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWEMPLOYEERESOURCESECONTRACT: Site");                                                     
		f.setMaxLength(40);                                                                
		f.setInsertable();  

		f = headblk.addField("SIGNATURE");                                                  
		f.setSize(25); 
		f.setMandatory();                                                                         
		f.setUpperCase();
		f.setFunction("''"); 
		f.setLOV("EmployeeResourcesSignatureLov.page", "CONTRACT CONTRACT", 600, 450);
	        f.setCustomValidation("SIGNATURE","SIGNATURE,EMPLOYEE_ID");
		f.setLabel("PCMWEMPLOYEERESOURCESSIGN: Signature");                                                     
		f.setMaxLength(40);                                                                
		f.setInsertable();  

		f = headblk.addField("EMPLOYEE_ID");                                                  
		f.setSize(25); 
		f.setMandatory();                                                                         
		f.setUpperCase();
		f.setFunction("''");
		f.setLOV("EmployeeResourcesEmployeesLov.page", "CONTRACT CONTRACT,SIGNATURE PERSON_ID", 600, 450);
		f.setCustomValidation("EMPLOYEE_ID","EMPLOYEE_ID");
		f.setLabel("PCMWEMPLOYEERESOURCESEMP: Employee");                                                     
		f.setMaxLength(40);                                                                
		f.setInsertable();  

		f = headblk.addField("COMPEXIST");
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

		headtbl = mgr.newASPTable(headblk);                       
		headtbl.setTitle(mgr.translate("PCMWEMPLOYEERESOURCESMAST: Parameters to Resource Graph"));  
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


		headblk2 = mgr.newASPBlock("HEAD2");

		f = headblk2.addField("PAST_WEEKS");                                                 
		f.setLabel("PCMWEMPLOYEERESOURCESPASTWKS: Past Weeks");                                                     
		f.setFunction("1");                                                       
		f.setMaxLength(40);                                                                
		f.setInsertable();                                                                 
		f.setSize(25);   

		f = headblk2.addField("FOLLOW_WEEKS");                                                 
		f.setLabel("PCMWEMPLOYEERESOURCESFOLLWKS: Following Weeks");                                                     
		f.setFunction("3");                                                       
		f.setMaxLength(40);                                                                
		f.setInsertable();                                                                 
		f.setSize(25);

		f = headblk2.addField("OPTION2");                                                 
		f.setHidden();
		f.setFunction("0");                                                       

		headblk2.setView("DUAL");                  
		headblk2.setMasterBlock(headblk);
		headset2 = headblk2.getASPRowSet();                         

		headlay2 = headblk2.getASPBlockLayout();                    
		headlay2.setDialogColumns(1);
		headlay2.setDefaultLayoutMode(headlay2.CUSTOM_LAYOUT);
		headlay2.setEditable();  
		headlay2.defineGroup(mgr.translate("PCMWEMPLOYEERESOURCESNUMWEEK: Number of Weeks in the Time Horizon"),"PAST_WEEKS,FOLLOW_WEEKS",true,true);

		headlay.showBottomLine(false);
                headlay2.showBottomLine(false);

		headtbl2 = mgr.newASPTable(headblk2);                       
		headtbl2.setTitle(mgr.translate("PCMWEMPLOYEERESOURCESMAST: Parameters to Resource Graph"));  
		headtbl2.setWrap();

		headbar2 = mgr.newASPCommandBar(headblk2);                  
		headbar2.disableCommand(headbar2.REMOVE);

		headbar2.disableCommand(headbar2.PREVIOUS);                
		headbar2.disableCommand(headbar2.NEXT);                    
		headbar2.disableCommand(headbar2.COUNT);                   
		headbar2.disableCommand(headbar2.DUPLICATE);               
		headbar2.disableCommand(headbar2.FIND);                    
		headbar2.disableCommand(headbar2.OKFIND);                  
		headbar2.disableCommand(headbar2.COUNTFIND);               
		headbar2.disableCommand(headbar2.EDIT);                    
		headbar2.disableCommand(headbar2.CANCELFIND);              
		headbar2.disableCommand(headbar2.CANCELEDIT);              
		headbar2.disableCommand(headbar2.BACK);  
		headbar2.enableCommand(headbar2.SAVERETURN);
		headbar2.defineCommand(headbar2.SAVERETURN,null,"checkHeadFields()");                        
		headbar2.defineCommand(headbar2.SAVERETURN,"ok");               
		headbar2.enableCommand(headbar2.CANCELNEW);                
		headbar2.defineCommand(headbar2.CANCELNEW,"cancel");  
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWEMPLOYEERESOURCESTITLE: Parameters to Resource Graph";
	}

	protected String getTitle()
	{
		return "PCMWEMPLOYEERESOURCESTITLE: Parameters to Resource Graph";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML("<table bgcolor=\"white\" width=\"727\" id=\"cntHEAD\" class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("<tr><td>\n");
		appendToHTML(headlay.generateDataPresentation());
		appendToHTML("</td></tr>\n");
		appendToHTML("<!-- table cell for radio buttons    -->\n");
		appendToHTML("<tr><td>\n");
		appendToHTML("<table border=0 width=100%  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("	<tr><td>\n");
		appendToHTML("	<table width=100% cellspacing=0 cellpadding=0 class=group>\n");
		appendToHTML("		<tr>\n");
		appendToHTML("			<td width=10 height=\"17\" noWrap vAlign=\"bottom\"><img src=\"../common/images/block_topleft.gif\"></td><td align=left width=25 height=\"17\" noWrap vAlign=\"center\"><td align=left height=\"17\" noWrap vAlign=\"center\"><font class=groupBoxTitleText>Time Scale</font></td><td width=10>&nbsp;</td>		<td width=100% align=\"right\" height=\"17\" noWrap vAlign=\"bottom\" background=\"../common/images/block_topmiddle.gif\"><img src=\"../common/images/table_empty_image.gif\" width=\"0\" height=\"0\"></td><td align=\"right\" vAlign=\"bottom\" width=1><img valign=bottom src=\"../common/images/block_topright.gif\">\n");
		appendToHTML("			</td>\n");
		appendToHTML("		</tr>\n");
		appendToHTML("</table>\n");
		appendToHTML("<table width=100% border=0  class=\"SubBlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
		appendToHTML("<tr>\n");
		appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
		appendToHTML(fmt.drawRadio("PCMWEMPLOYEERESOURCESRADIO1: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Show day by day for 1 week","OPTION","0",true," OnClick='validateOption(0)'"));
		appendToHTML("</td><td width=10>&nbsp</td>\n");
		appendToHTML("	<tr>\n");
		appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
		appendToHTML(fmt.drawRadio("PCMWEMPLOYEERESOURCESRADIO2: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Show week by week", "OPTION","1",false,"OnClick='validateOption(1)'"));
		appendToHTML("</td><td width=10>&nbsp</td>\n");
		appendToHTML("</table>	\n");
		appendToHTML("<table  cellpadding=0 cellspacing=0 width=100%>");
		appendToHTML("<tr><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td><td width=100% height=9 background=\"../common/images/block_bottommiddle.gif\"><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"1\"></td><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td></tr></table><p>\n");
		appendToHTML("</td></tr>\n");
		appendToHTML("</table>\n");
		appendToHTML("</td></tr>\n");
		appendToHTML("<!-- end of table cell for radio buttons    -->\n");
		appendToHTML("<tr><td>\n");
		appendToHTML(headlay2.generateDataPresentation());
		appendToHTML("</td></tr>\n");
		appendToHTML("<tr>		\n");
		appendToHTML("       <td align=\"left\"> \n");
		appendToHTML(fmt.drawSubmit("OK",mgr.translate("PCMWEMPLOYEERESOURCESBUTOK:   OK   "),"onFocus='checkHeadFields()'"));
		appendToHTML("&nbsp;\n");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWEMPLOYEERESOURCESBUTCANCEL: Cancel"),""));
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
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateOption(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("getField_('OPTION2',i).value = i;\n");
		appendDirtyJavaScript("}\n");

                appendDirtyJavaScript("function lovEmployeeId(i,params)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("if(params) param = params;\n");
		appendDirtyJavaScript("else param = '';\n");
		appendDirtyJavaScript("var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
		appendDirtyJavaScript("var signature = (getValue_('SIGNATURE',i) != '')? getValue_('SIGNATURE',i):'NULL';\n");
		appendDirtyJavaScript("var contract =  (getValue_('CONTRACT',i)!= '')? getValue_('CONTRACT',i):'NULL';\n");
		appendDirtyJavaScript("	  openLOVWindow('EMPLOYEE_ID',i,\n");
		appendDirtyJavaScript("                 'EmployeeResourcesEmployeesLov.page' + '?__VIEW=DUMMY&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("                 + '&CONTRACT=' + URLClientEncode(contract)\n");
		appendDirtyJavaScript("                 + '&PERSON_ID=' + URLClientEncode(signature)\n");       
		appendDirtyJavaScript("       	   		,550,500,'validateEmployeeId');\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("function lovSignature(i,params)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("if(params) param = params;\n");
		appendDirtyJavaScript("else param = '';\n");
		appendDirtyJavaScript("var enable_multichoice = (true && HEAD_IN_FIND_MODE);\n");
		appendDirtyJavaScript("var contract = (getValue_('CONTRACT',i) != '')? getValue_('CONTRACT',i):'NULL';\n");
		appendDirtyJavaScript("	  openLOVWindow('SIGNATURE',i,\n");
		appendDirtyJavaScript("                 'EmployeeResourcesSignatureLov.page' + '?__VIEW=DUMMY&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
		appendDirtyJavaScript("                 + '&CONTRACT=' + URLClientEncode(contract)\n");
		appendDirtyJavaScript("       	   		,550,500,'validateSignature');\n");
		appendDirtyJavaScript("}\n");

		performRMB = "false";
	}
}
