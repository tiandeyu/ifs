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
*  File        : SupplierWarrantyType.java 
*  Created     : ASP2JAVA Tool  010308
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHCRLK  010426  Modified submit() method. 
*  BUNILK  010713  Added three fields (Valid from and valid until and Note).
*  INROLK  011019  Added Encode to Warranty Description. call id 70691.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041112  Replaced getContents with printContents.
*  AMDILK  060814  Bug Id 58216, Eliminated SQL errors in web applications. Modified method okFind()
*  AMDILK  060904  Merged with the Bug ID 58216
*  AMNILK  070727  Eliminated SQLInjections Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SupplierWarrantyType extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SupplierWarrantyType");

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
	private String cancelFlag;
	private String returnFlag;
	private String frmname;
	private String qrystr;
	private String rwarrType;
	private String rwarrDesc;
	private String rrowN;
	private String rwarrId;
	private String prepareWorkOrd;
	private String prepareWorkOrdSM;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public SupplierWarrantyType(ASPManager mgr, String page_path)
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

		prepareWorkOrd = ctx.readValue("PREPAREWORKORD","FALSE");
		prepareWorkOrdSM = ctx.readValue("PREPAREWORKORDSM","FALSE");

		if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			okFind();
		}
		else if (mgr.buttonPressed("CANCEL"))
		{
			cancel(); 
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			mchCode = mgr.readValue("MCH_CODE");
			cont = mgr.readValue("CONTRACT");
			row = mgr.readValue("ROW_NO");
			wid = mgr.readValue("WARRANTY_ID");
			wono = mgr.readValue("WO_NO");

			frmname = mgr.readValue("FRMNAME");

			if ("prepareWorkOrd".equals(frmname))
				prepareWorkOrd="TRUE";

			else if ("prepareWorkOrdSM".equals(frmname))
				prepareWorkOrdSM="TRUE";

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

		ctx.writeValue("PREPAREWORKORD",prepareWorkOrd); 
		ctx.writeValue("PREPAREWORKORDSM",prepareWorkOrdSM); 
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();
		String val = null;

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("MCH_CODE= ? AND CONTRACT= ? AND ROW_NO is not null AND WARRANTY_ID is not null");  
                q.addParameter("MCH_CODE", mchCode);
		q.addParameter("CONTRACT", cont);
		
		q.includeMeta("ALL");
		mgr.submit(trans); 
		qrystr = mgr.createSearchURL(headblk); 
	}

	public void submit()
	{
		ASPManager mgr = getASPManager();
		if (headset.countRows() > 0)
		{
			if (headlay.isMultirowLayout())
				headset.goTo(headset.getRowSelected());

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
		f.setLabel("PCMWSUPPLIERWARRANTYTYPECONTRACT: Site");
		f.setReadOnly();

		f = headblk.addField("MCH_CODE");
		f.setMaxLength(100);
		f.setSize(40);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEMCHCODE: Object Id");

		f = headblk.addField("ROW_NO","Number");
		f.setSize(40);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEROWNO: Warranty Row No");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_ID");
		f.setSize(30);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEWARRANTYID: Warranty Id");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_TYPE_ID");
		f.setSize(20);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEWARRANTYTYPEID: Warranty Type Id");
		f.setReadOnly();

		f = headblk.addField("WARRANTY_DESCRIPTION");
		f.setSize(35);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEWARRANTYDESCRIPTION: Warranty Descripition");
		f.setReadOnly();

		f = headblk.addField("VALID_FROM","Date");
		f.setSize(16);
		f.setDefaultNotVisible();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEVALIDFROM: Valid From");
		f.setReadOnly();

		f = headblk.addField("VALID_UNTIL","Date");
		f.setSize(16);
		f.setDefaultNotVisible(); 
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEVALIDUNTIL: Valid Until");
		f.setReadOnly();

		f = headblk.addField("NOTE");
		f.setSize(35);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPENOTE: Note");
		f.setDefaultNotVisible(); 
		f.setReadOnly();

		f = headblk.addField("FIXED_PRICE_COST_TYPE");
		f.setSize(55);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEFIXEDPRICECOSTTYPE: Fixed Price Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible(); 

		f = headblk.addField("MATERIAL_COST_TYPE");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEMATERIALCOSTTYPE: Material Cost Type");
		f.setDefaultNotVisible();

		f = headblk.addField("PERSONNEL_COST_TYPE");
		f.setSize(30);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEPERSONNELCOSTTYPE: Personnel Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("EXTERNAL_COST_TYPE");
		f.setSize(30);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEEXTERNALCOSTTYPE: External Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("EXPENSES_COST_TYPE");
		f.setSize(30);
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEEXPENSESCOSTTYPE: Expenses Cost Type");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = headblk.addField("CONDITION_ID","Number");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPECONDITIONID: Condition Id");

		f = headblk.addField("CONDITION_DESCRIPTION");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPECONDITIONDESCRIPTION: Condition Description");

		f = headblk.addField("MIN_VALUE","Number");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEMINVALUE: Min Value");
		f.setDefaultNotVisible();

		f = headblk.addField("MAX_VALUE","Number");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEMAXVALUE: Max Value");
		f.setDefaultNotVisible();

		f = headblk.addField("TIME_UNIT");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPETIMEUNIT: Time Unit");
		f.setDefaultNotVisible();

		f = headblk.addField("UNIT_CODE");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWSUPPLIERWARRANTYTYPEUNITCODE: Unit Code");
		f.setDefaultNotVisible();

		headblk.setView("OBJ_SUPP_WARRANTY_TYPE");
		headblk.defineCommand("","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.NEWROW); 
		headbar.disableCommand(headbar.DELETE); 
		headbar.disableCommand(headbar.FIND); 
		headbar.disableCommand(headbar.EDITROW); 


		headbar.addCustomCommand("submit",mgr.translate("PCMWSUPPLIERWARRANTYTYPEOK: Ok"));
		headbar.defineCommand(headbar.SAVERETURN,null,"checkItem1Fields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");

		headtbl = mgr.newASPTable(headblk);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		//headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
	}

	public void adjust()
	{

	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWSUPPLIERWARRANTYTYPETITLE: List of Supplier Warranty types";
	}

	protected String getTitle()
	{
		return "PCMWSUPPLIERWARRANTYTYPETITLE: List of Supplier Warranty types";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendToHTML("<table id=\"CANCEL\" border=\"0\">\n");
		appendToHTML("<tr>\n");
		appendToHTML("<td><br>\n");
		appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWSUPPLIERWARRANTYTYPECANCEL: Cancel"),""));
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("</table>\n");

		appendDirtyJavaScript("window.name = \"WarrentDlg\";\n");

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
		appendDirtyJavaScript(mgr.encodeStringForJavascript(prepareWorkOrd));	//XSS_Safe AMNILK 20070726
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
		appendDirtyJavaScript("     window.open(\"ActiveSeparate2.page?WO_NO=\"+URLClientEncode(jwono)+\"&RWARRTYPEENT=\"+URLClientEncode(jWarrType)+\"&RWARRDESC=\"+URLClientEncode(jWarrDesc)+\"&RROWNENT=\"+URLClientEncode(jRowN)+\"&RWARRIDENT=\"+URLClientEncode(jWarrId),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"\"); \n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("   }\n");

		appendDirtyJavaScript("   if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(prepareWorkOrdSM));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("' == \"TRUE\" )\n");
		appendDirtyJavaScript("   {\n");
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
		appendDirtyJavaScript("     window.open(\"ActiveSeparate2ServiceManagement.page?WO_NO=\"+URLClientEncode(jwono)+\"&RWARRTYPEENT=\"+URLClientEncode(jWarrType)+\"&RWARRDESC=\"+URLClientEncode(jWarrDesc)+\"&RROWNENT=\"+URLClientEncode(jRowN)+\"&RWARRIDENT=\"+URLClientEncode(jWarrId),'");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));		//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("',\"\"); \n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");
	}
}
