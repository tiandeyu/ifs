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
*  File        : PermitTypeOvw.java 
*  Created     : ASP2JAVA Tool  010212
*  Modified    : 
*  JEWILK  010403  Changed file extensions from .asp to .page
*  SAPRLK  011016  Performed the Securtiy check for this form, 
*                  added functions checkObjAvaileble() and adjust(); 
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  041111  Replaced getContents with printContents.
*  NEKOLK  050802  AMUT 115: Removed CONNECTION_TYPE..
*  NEKOLK  060213  Call 133707..Added RMB characteristics.
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
*  ILSOLK  070730  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PermitTypeOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PermitTypeOvw");

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

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String pertyid;
	private String flag1;
	private String val;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;
	private String calling_url;
	private ASPBuffer buffer;
	private ASPBuffer row;  
	private boolean actEna1;
	private boolean again;


	
	private String luName;
	private String kRef;
	private String desc;
	private String charac;
	private String flag;
	private boolean chksec;
        private String mchtype;
        private String current_url;

	//===============================================================
	// Construction 
	//===============================================================
	public PermitTypeOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		pertyid = ctx.readValue("PERTY", "");
		flag1 = ctx.readValue("FLAG1", "FALSE");

		actEna1 = ctx.readFlag("ACTENA1", false);
		again = ctx.readFlag("AGAIN", false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_TYPE_ID")))
		{
			okFind(); 
		}

		checkObjAvaileble();
		adjust();

		ctx.writeFlag("ACTENA1", actEna1);
		ctx.writeFlag("AGAIN", again);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","PERMIT_TYPE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");

		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWPERMITTYPEOVWNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

        public void characteristics()
        {
            ASPManager mgr = getASPManager();
            ASPTransactionBuffer secBuff = null;


            current_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL",current_url);

            if (headlay.isMultirowLayout()  ||  ( headset.countRows() == 1 ))
                headset.goTo(headset.getRowSelected());

            luName = "PermitType";
            mchtype = headset.getRow().getValue("PERMIT_TYPE_ID");
            if (mgr.isEmpty(mchtype))
                kRef = "";
            else
                kRef = mchtype;
            kRef = "PERMIT_TYPE_ID=" + kRef+"^";


            desc = mgr.translate("PERMITTYPE: Permit Type: ")+headset.getRow().getValue("PERMIT_TYPE_ID");
            charac = "TRUE" ;

            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("DOC_REFERENCE_OBJECT");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT"))
                flag = "true";
            else
                flag = "false"; 
        }  


        public void attributes()
	{
		ASPManager mgr = getASPManager();

		calling_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL",calling_url);
		ctx.setGlobal("FORM_NAME","PermitTypeOvw.page");
		ctx.setGlobal("PRIMARY_KEY","''");

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		pertyid = headset.getRow().getValue("PERMIT_TYPE_ID");
		flag1 = "TRUE";

		buffer = mgr.newASPBuffer();
		row = buffer.addBuffer("");
		row.addItem("PERMIT_TYPE_ID", headset.getRow().getValue("PERMIT_TYPE_ID"));
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PERMIT_TYPE_ID");
		f.setSize(16);
		f.setMandatory();
		f.setLabel("PCMWPERMITTYPEOVWPTYPEID: Permit Type");
		f.setUpperCase();
		f.setReadOnly();
		f.setMaxLength(4);
		f.setInsertable();

		f = headblk.addField("DESCRIPTION");
		f.setSize(35);
		f.setMandatory();
		f.setLabel("PCMWPERMITTYPEOVWDESC: Description");
		f.setMaxLength(40);

		f = headblk.addField("CONNECTION_TYPE");
		f.setSize(25);
		f.setLabel("PCMWPERMITTYPEOVWCONTYPE: Permit Classification");
		f.setReadOnly();
		f.setMaxLength(20);
                f.setHidden();


		headblk.setView("PERMIT_TYPE");
		headblk.defineCommand("PERMIT_TYPE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields()");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");   

                headbar.addCustomCommand("characteristics", mgr.translate("PCMWPERMITTYPEOVWCRACTER: Characteristics..."));
                headbar.addCustomCommand("attributes",  mgr.translate("PCMWPERMITTYPEOVWSPA: Attributes..."));

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWPERMITTYPEOVWHD: Permit Types"));
		headtbl.setWrap();
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void checkObjAvaileble()
	{
		if (!again)
		{
			ASPManager mgr = getASPManager();

			ASPBuffer availObj;
			trans.clear();
			trans.addSecurityQuery("PERMIT_TYPE_ATTRIBUTE");
			trans.addPresentationObjectQuery("PCMW/PermitTypeRMB.page");

			trans = mgr.perform(trans);

			availObj = trans.getSecurityInfo();

			if ( availObj.itemExists("PERMIT_TYPE_ATTRIBUTE") && availObj.namedItemExists("PCMW/PermitTypeRMB.page") )
				actEna1 = true;

			trans.clear();
			again = true;
		}
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		if (!actEna1)
			headbar.removeCustomCommand("attributes");
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWPERMITTYPEOVWTITLE: Permit Types";
	}

	protected String getTitle()
	{
		return "PCMWPERMITTYPEOVWTITLE: Permit Types";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript(" window.name = \"Attributes\";\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(flag1));    //XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   pertypeid='");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(pertyid));	//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("   window.open(\"PermitTypeRMB.page?PERMIT_TYPE_ID=\"+URLClientEncode(pertypeid),\"PermitTypeId\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("}\n");


		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(charac);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   sluName='");
		appendDirtyJavaScript(luName);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("   skRef='");
		appendDirtyJavaScript(kRef);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("   sdesc='");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(desc));	//XSS_Safe AMNILK 20070718
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("   sFlag = '");
		appendDirtyJavaScript(flag);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.open(\"../equipw/TechnicalObjectReferenceType.page?LU_NAME=\"+URLClientEncode(sluName)+\"&KEY_REF=\"+URLClientEncode(skRef)+\"&MCH_TYPE=\"+URLClientEncode(sdesc)+\"&SFLAG=\"+URLClientEncode(sFlag),\"ObjType\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
		appendDirtyJavaScript("}\n");


	}
}
