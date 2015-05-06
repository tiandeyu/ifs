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
*  File        : WarrantyTypeDlg.java 
*  Created     : INROLK  010223  Java conversion
*  Modified    : 
*  INROLK  010712  Added fiels Valid from and valid until. callid 77803
*  INROLK  010727  Changed okFind() to populate all th warranty lines .  call id  77834.
*  INROLK  010905  Removed Field Note .  call id  78113.
*  INROLK  011019  Added Encode to Warranty Description. call id 70691.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041112  Replaced getContents with printContents.
*  AMDILK  060811  Bug Id 58216, Eliminated SQL errors in web applications. Modified method okFind()
*  AMDILK  060904  Merged with the Bug Id 58216
*  AMNILK  070727  Eliminated SQLInjections Security Vulnerability.
* ---------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WarrantyTypeDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WarrantyTypeDlg");

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
	private ASPTransactionBuffer trans;
	private String mchCode;
	private String cont;
	private String row;
	private String wid;
	private String wono;
	private ASPCommand cmd;
	private String cancelFlag;
	private String returnFlag;
	private String frmname;
	private String qrystr;
	private String rwarrType;
	private String rwarrDesc;
	private String rrowN;
	private String rwarrId;
	private String serReq;
	private String coInfo;
	private String coRepInfo;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public WarrantyTypeDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		fmt = mgr.newASPHTMLFormatter();
		trans = mgr.newASPTransactionBuffer();


		mchCode = ctx.readValue("MCHCODE","");
		cont = ctx.readValue("CONT","");
		row = ctx.readValue("ROW","");
		wid = ctx.readValue("WID","");
		wono = ctx.readValue("WONO","");
		cancelFlag = ctx.readValue("CANCELFLAG","FALSE");
		returnFlag = ctx.readValue("RETURNFLAG","FALSE");
		frmname= ctx.readValue("FRMNAME","");
		qrystr = ctx.readValue("QRYSTR","");
		rwarrType = ctx.readValue("RWARRTYPE","");
		rwarrDesc = ctx.readValue("RWARRDESC","");   
		rrowN = ctx.readValue("RROWN","");
		rwarrId = ctx.readValue("RWARRID","");

		serReq = ctx.readValue("SERREQ","FALSE");
		coInfo = ctx.readValue("COINFO","FALSE");
		coRepInfo = ctx.readValue("COREPINFO","FALSE");

		if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
			okFind();
		else if (mgr.buttonPressed("CANCEL"))
			cancel();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			mchCode = mgr.readValue("MCH_CODE");
			cont = mgr.readValue("CONTRACT");
			row = mgr.readValue("ROW_NO");
			wid = mgr.readValue("WARRANTY_ID");
			wono = mgr.readValue("WO_NO");

			frmname = mgr.readValue("FRMNAME");

			if ("ServiceRequest".equals(frmname))
			{
				serReq="TRUE";
				coInfo="FALSE";
				coRepInfo="FALSE";
			}
			else if ("COInfo".equals(frmname))
			{
				coInfo="TRUE";
				serReq="FALSE";
				coRepInfo="FALSE";
			}

			else if ("CORepInfo".equals(frmname))
			{
				coRepInfo="TRUE";
				serReq="FALSE";
				coInfo="FALSE";
			}

			okFind();
		}

		adjust();

		ctx.writeValue("MCHCODE",mchCode);
		ctx.writeValue("CONT",cont);
		ctx.writeValue("WONO",wono);
		ctx.writeValue("ROW",row);
		ctx.writeValue("WID",wid);

		ctx.writeValue("FRMNAME",frmname);
		ctx.writeValue("QRYSTR",qrystr);

		ctx.writeValue("SERREQ",serReq);
		ctx.writeValue("COINFO",coInfo);
		ctx.writeValue("COREPINFO",coRepInfo);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		if (!(mgr.isEmpty(mchCode)))
		{
			q = trans.addEmptyQuery(headblk);
			//q.addWhereCondition("MCH_CODE='"+mchCode+"' AND CONTRACT='"+cont+"' AND ROW_NO='"+row+"' AND WARRANTY_ID='"+wid+"' ");
			q.addWhereCondition("MCH_CODE= ? AND CONTRACT= ? AND ROW_NO IS NOT NULL  AND WARRANTY_ID IS NOT NULL");
			q.addInParameter("MCH_CODE", mchCode);
			q.addInParameter("CONTRACT", cont);   
		}
		else
		{
			q = trans.addEmptyQuery(headblk);
			q.addWhereCondition("MCH_CODE IS NOT NULL AND CONTRACT= ?");
			q.addParameter("CONTRACT", cont);
		}

		q.includeMeta("ALL");
		mgr.submit(trans); 
		qrystr = mgr.createSearchURL(headblk); 
	}

	//-------------------------
	//--------------------------

	public void submit()
	{

		ASPManager mgr = getASPManager();
		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		if (headset.countRows() > 0)
		{
			rwarrType = headset.getRow().getValue("WARRANTY_TYPE_ID");
			rwarrDesc = mgr.URLEncode(headset.getRow().getValue("WARRANTY_DESCRIPTION")); 
			rrowN = headset.getRow().getValue("ROW_NO");
			rwarrId = headset.getRow().getValue("WARRANTY_ID");
			returnFlag = "TRUE";
		}
		else
		{
			cancelFlag = "TRUE";
		}   

	}

	public void cancel()
	{

		cancelFlag = "TRUE";
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("CONTRACT");
		f.setSize(5);
		f.setLabel("PCMWWARRANTYTYPEDLGCONTRACT: Site");
		f.setHidden();
		f.setReadOnly();

		f = headblk.addField("MCH_CODE");
		f.setSize(40);
		f.setMaxLength(100);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGMCHCODE: Object ID");

		f = headblk.addField("ROW_NO","Number");
		f.setSize(40);
		f.setLabel("PCMWWARRANTYTYPEDLGROWNO: Cust Warranty Row No");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_ID","Number");
		f.setSize(30);
		f.setLabel("PCMWWARRANTYTYPEDLGWARRANTYID: Cust Warranty ID");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_TYPE_ID");
		f.setSize(30);
		f.setLabel("PCMWWARRANTYTYPEDLGWARRANTYTYPEID: Cust Warranty Type ID");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_DESCRIPTION");
		f.setSize(40);
		f.setLabel("PCMWWARRANTYTYPEDLGWARRANTYDESCRIPTION: Warranty Descripition");
		f.setReadOnly();

		f = headblk.addField("VALID_FROM","Datetime");
		f.setSize(22);
		f.setLabel("PCMWWARRANTYTYPEDLGVALIDFROM: Valid From");
		f.setReadOnly();

		f = headblk.addField("VALID_UNTIL","Datetime");
		f.setSize(22);
		f.setLabel("PCMWWARRANTYTYPEDLGVALIDUNTIL: Valid Until");
		f.setReadOnly();
		/*		
		f = headblk.addField("NOTE");
		f.setSize(15);
		f.setLabel("PCMWWARRANTYTYPEDLGNOTE: Note");
		f.setReadOnly();
		*/
		f = headblk.addField("EXPENSES_COST_TYPE");
		f.setSize(55);
		f.setLabel("PCMWWARRANTYTYPEDLGEXPENSESCOSTTYPE: Expenses Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible(); 

		f = headblk.addField("EXTERNAL_COST_TYPE");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGEXTERNALCOSTTYPE: External Cost Type");
		f.setDefaultNotVisible(); 

		f = headblk.addField("FIXED_PRICE_COST_TYPE");
		f.setSize(40);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGFIXEDPRICECOSTTYPE: Fixed Price Cost Type");
		f.setDefaultNotVisible(); 

		f = headblk.addField("MATERIAL_COST_TYPE");
		f.setReadOnly();
		f.setSize(30);
		f.setLabel("PCMWWARRANTYTYPEDLGMATERIALCOSTTYPE: Material Cost Type ");
		f.setDefaultNotVisible(); 

		f = headblk.addField("PERSONNEL_COST_TYPE");
		f.setSize(30);
		f.setLabel("PCMWWARRANTYTYPEDLGPERSONNELCOSTTYPE: Personnel Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible(); 

		f = headblk.addField("CONDITION_ID","Number");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGCONDITIONID: Condition ID");
		f.setDefaultNotVisible(); 

		f = headblk.addField("CONDITION_DESCRIPTION");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGCONDITIONDESCRIPTION: Condition Description");
		f.setDefaultNotVisible(); 

		f = headblk.addField("MIN_VALUE","Number");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGMINVALUE: Min Value");
		f.setDefaultNotVisible(); 

		f = headblk.addField("MAX_VALUE","Number");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGMAXVALUE: Max Value");
		f.setDefaultNotVisible(); 

		f = headblk.addField("TIME_UNIT");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGTIMEUNIT: Time Unit");
		f.setDefaultNotVisible(); 

		f = headblk.addField("UNIT_CODE");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWWARRANTYTYPEDLGUNITCODE: Unit Code");
		f.setDefaultNotVisible(); 

		headblk.setView("OBJ_CUST_WARRANTY_TYPE");
		headblk.defineCommand("","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.NEWROW); 
		headbar.disableCommand(headbar.DELETE); 
		headbar.disableCommand(headbar.FIND); 
		headbar.disableCommand(headbar.EDITROW); 

		headbar.addCustomCommand("submit",mgr.translate("PCMWWARRANTYTYPEDLGOK: Ok"));
		headbar.defineCommand(headbar.SAVERETURN,null,"checkItem1Fields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWWARRANTYTYPEDLGTITLE: List of Warranty types";
	}

	protected String getTitle()
	{
		return "PCMWWARRANTYTYPEDLGTITLE: List of Warranty types";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("<table id=\"CANCEL\" border=\"0\">\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td><br>\n");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWWARRANTYTYPEDLGCANCEL: Cancel"),""));
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

		appendDirtyJavaScript("window.name = \"WarrentyDlg\";\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelFlag));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   self.close();\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(returnFlag));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(serReq));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     jWarrType = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrType));	//XSS_Safe AMNILK 20070726	
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrDesc = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrDesc));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' \n");
		appendDirtyJavaScript("     jRowN = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rrowN));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrId = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrId));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jwono = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(wono));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     window.open(\"ActiveSeparate3.page?WO_NO=\"+URLClientEncode(jwono)+\"&RWARRTYPEENT=\"+URLClientEncode(jWarrType)+\"&RWARRDESC=\"+URLClientEncode(jWarrDesc)+\"&RROWNENT=\"+URLClientEncode(jRowN)+\"&RWARRIDENT=\"+URLClientEncode(jWarrId),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"\"); \n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("  }\n");

		appendDirtyJavaScript("  if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(coInfo));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     jWarrType = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrType));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrDesc = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrDesc));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' \n");
		appendDirtyJavaScript("     jRowN = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rrowN));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrId = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrId));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jwono = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(wono));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     window.open(\"ActiveSeparate2RMB.page?WO_NO=\"+URLClientEncode(jwono)+\"&RWARRTYPEENT=\"+URLClientEncode(jWarrType)+\"&RWARRDESC=\"+URLClientEncode(jWarrDesc)+\"&RROWNENT=\"+URLClientEncode(jRowN)+\"&RWARRIDENT=\"+URLClientEncode(jWarrId),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"\"); \n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("  }\n");

		appendDirtyJavaScript("  if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(coRepInfo));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("  {\n");
		appendDirtyJavaScript("     jWarrType = '");	
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrType));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrDesc = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrDesc));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' \n");
		appendDirtyJavaScript("     jRowN = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rrowN));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jWarrId = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(rwarrId));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     jwono = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(wono));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("     window.open(\"ActiveSeparateReportCOInfo.page?WO_NO=\"+URLClientEncode(jwono)+\"&RWARRTYPEENT=\"+URLClientEncode(jWarrType)+\"&RWARRDESC=\"+URLClientEncode(jWarrDesc)+\"&RROWNENT=\"+URLClientEncode(jRowN)+\"&RWARRIDENT=\"+URLClientEncode(jWarrId),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"\"); \n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("  }\n");
		appendDirtyJavaScript("}\n");
	}
}
