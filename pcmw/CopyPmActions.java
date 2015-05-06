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
*  File        : CopyPmActions.java 
*  Created     : 010730  VAGULK  Created.
*  Modified    :  
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  JEJALK  030403  Added Tools and Facilities to the Copy Function.
*  VAGULK  031219  Made the field order according to the order in the Centura application(Web Alignment),
*                  and Removed clone and doReset Methods.
*  ARWILK  040908  Added new checkbbox CBPLANNING and the related functionality(IID AMEC111A: Std Jobs as PM Templates)
*  NIJALK  050107  Modified display text.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  ILSOLK  070710  Eliminated XSS.
*  ILSOLK  070730  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
*  SHAFLK  080303  Bug 71664, Modified run() and startup().
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CopyPmActions extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CopyPmActions");

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
	private String isTransfer;
	private String infoMsg;
	private ASPTransactionBuffer trans;
	private String pmNo;
	private String pmRev;
	private String errDescr;
	private ASPBuffer buff;
	private ASPBuffer row;
	private ASPCommand cmd;
	private String newQuotationId;
	private ASPBuffer buffer;
	private String fmtdBuff;
	private String val;
	private String txt; 
	private String comp;
	private String strMchCode;
	private ASPQuery qry;
	private ASPQuery evqry;
	private String route_id;
	private String closewin;
	private String Msg;
	private String CancelWin;
	private boolean newwin;
	private String npmNo;
	private String npmRev;
	private String sStdJobId;
	private String routId;
	//private String getTxtonly;

	//===============================================================
	// Construction 
	//===============================================================
	public CopyPmActions(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		comp = "";
		strMchCode = "";

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		comp =  ctx.readValue("COMP","");
		strMchCode =ctx.readValue ("STRMCHCODE","");
		route_id  = ctx.readValue("ROUTEID","");
		closewin = ctx.readValue("CLOSEWIN","FALSE");
		Msg =ctx.readValue("MSG","");                
		CancelWin = ctx.readValue("CANCELWIN","FALSE");
		newwin = ctx.readFlag("NEWWIN",false);
		pmNo =ctx.readValue ("PMNO","");
		pmRev =ctx.readValue ("PMREV","");
		routId =  ctx.readValue("ROUTID","");
                

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if ("TRUE".equals(mgr.readValue("ROUNDDEFEXSIST")))
		{
			setCheckBoxReadOnly(); 
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
		{
			strMchCode =  mgr.getQueryStringValue("MCH_CODE"); 
			comp =  mgr.getQueryStringValue("CONTRACT"); 
			pmNo = mgr.getQueryStringValue("PM_NO");
			pmRev = mgr.getQueryStringValue("PM_REVISION");
                        //Bug 71664, start
                        routId = mgr.getQueryStringValue("ROUNDDEF_ID");
                        //Bug 71664, end
			startup();
		}
		else if (mgr.dataTransfered())
		{
			okFind();
		}

		ctx.writeValue("STRMCHCODE",strMchCode);
		ctx.writeValue("COMP",comp);
		ctx.writeValue("PMNO",pmNo);
		ctx.writeValue("PMREV",pmRev);
		ctx.writeFlag("NEWWIN",newwin);
		ctx.writeValue("MSG",Msg); 
		ctx.writeValue("CLOSEWIN",closewin);
		ctx.writeValue("CANCELWIN",CancelWin);
		ctx.writeValue("ROUTID",routId);
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void copy()
	{
		ASPManager mgr = getASPManager();   

		String cbcrafts;
		String cbjobs;
		String cbconnections;
		String cbpermits;
		String cbspareparts;
		String cbcriteria;
		String cbdocumts;
		String cbtoolfacility;
		String cbplanning;

		if ("1".equals(mgr.readValue("CBCRAFTS")))
			cbcrafts = "1";
		else
			cbcrafts = "0"; 

		if ("1".equals(mgr.readValue("CBJOBS")))
			cbjobs = "1";
		else
			cbjobs = "0"; 

		if ("1".equals(mgr.readValue("CBCONNECTIONS")))
			cbconnections = "1";
		else
			cbconnections = "0"; 

		if ("1".equals(mgr.readValue("CBPERMITS")))
			cbpermits = "1";
		else
			cbpermits = "0"; 

		if ("1".equals(mgr.readValue("CBSPAREPARTS")))
			cbspareparts = "1";
		else
			cbspareparts = "0"; 

		if ("1".equals(mgr.readValue("CBCRITERIA")))
			cbcriteria = "1";
		else
			cbcriteria = "0"; 

		if ("1".equals(mgr.readValue("CBDOCUMTS")))
			cbdocumts = "1";
		else
			cbdocumts = "0";

		if ("1".equals(mgr.readValue("CBTOOLFACILITY")))
			cbtoolfacility = "1";
		else
			cbtoolfacility = "0";

		if ("1".equals(mgr.readValue("CBPLANNING")))
			cbplanning = "1";
		else
			cbplanning = "0";

		cmd = trans.addCustomCommand( "COPY","Pm_Action_API.Copy_Pm");

		cmd.addParameter("NEW_PM_NO",npmNo);
		cmd.addParameter("NEW_PM_REV",npmRev);
		cmd.addParameter("PM_NO",pmNo);
		cmd.addParameter("PM_REVISION",pmRev);
		cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT")); 
		cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE")); 
		cmd.addParameter("ROUNDDEF_ID",mgr.readValue("ROUNDDEF_ID")); 
		cmd.addParameter("CBCRAFTS",cbcrafts);  
		cmd.addParameter("CBCONNECTIONS",cbconnections);
		cmd.addParameter("CBPERMITS",cbpermits);
		cmd.addParameter("CBSPAREPARTS",cbspareparts);
		cmd.addParameter("CBCRITERIA",cbcriteria);
		cmd.addParameter("CBDOCUMTS",cbdocumts);
		cmd.addParameter("CBTOOLFACILITY",cbtoolfacility);
		cmd.addParameter("CBJOBS",cbjobs);  
		cmd.addParameter("CBPLANNING",cbplanning);

		trans = mgr.submit(trans);  

		int newpmno = toInt(trans.getNumberValue("COPY/DATA/NEW_PM_NO"));
		String newpmrev = trans.getValue("COPY/DATA/NEW_PM_REV");

		String text1 = mgr.translate("PCMWCOPYPMACTIOSENDMSG1: PM action  ");
		String text2 = mgr.translate("PCMWCOPYPMACTIOSENDMSG2:  has been created");
		Msg = text1 + newpmno +" , " + newpmrev + text2;
		closewin = "TRUE" ;
	}

	public void cancel()
	{
		ASPManager mgr = getASPManager();

		CancelWin = "TRUE";
	}

	public void validate()
	{
		ASPManager mgr = getASPManager();
		val = mgr.readValue("VALIDATE");  

		mgr.endResponse();
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		qry = trans.addQuery(headblk);
		qry.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows()==0)
		{
			mgr.showAlert(mgr.translate("PCMWCOPYPMACTIONSNODATA: No data found."));
			headset.clear();
		}
	}

	public void startup()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		qry = trans.addEmptyQuery(headblk);
		qry.includeMeta("ALL");
		mgr.submit(trans);

		ASPBuffer row1 = headset.getRow();
		row1.setValue("CONTRACT",comp);
                //Bug 71664, start
                if (!mgr.isEmpty(routId)) {
                    row1.setValue("ROUNDDEF_ID",routId);
                }
                //Bug 71664, end
                headset.setRow(row1);

		HasJobs();
	}

	public void setCheckBoxReadOnly()
	{
		ASPManager mgr = getASPManager();
		route_id = mgr.readValue("ROUNDFEFID");
		String object_id = mgr.readValue("OBJECTID");

		String cbx_crafts = mgr.readValue("CRAFTS");
		String cbx_jobs = mgr.readValue("JOBS");
		String cbx_connections = mgr.readValue("CONNECTIONS");
		String cbx_permits = mgr.readValue("PERMITS");
		String cbx_spareparts = mgr.readValue("SPAREPARTS");
		String cbx_criteria = mgr.readValue("CRITERIA");
		String cbx_documts = mgr.readValue("DOCUMTS");
		String cbx_toolfacility = mgr.readValue("TOOLFACILITY");  
		String cbx_planning = mgr.readValue("PLANNING");

		ASPBuffer buff = headset.getRow();
		buff.setValue("ROUNDDEF_ID",route_id); 
		buff.setValue("MCH_CODE",object_id);

		buff.setValue("CBCRAFTS",cbx_crafts);
		buff.setValue("CBJOBS",cbx_jobs);
		buff.setValue("CBCONNECTIONS",cbx_connections);
		buff.setValue("CBPERMITS",cbx_permits);
		buff.setValue("CBSPAREPARTS",cbx_spareparts);
		buff.setValue("CBCRITERIA",cbx_criteria);
		buff.setValue("CBDOCUMTS",cbx_documts);
		buff.setValue("CBTOOLFACILITY",cbx_toolfacility);
		buff.setValue("CBPLANNING",cbx_planning);

		headset.setRow(buff);

		if (!mgr.isEmpty(route_id))
		{
			mgr.getASPField("CBCRAFTS").setReadOnly();
			mgr.getASPField("CBCONNECTIONS").setReadOnly();
			mgr.getASPField("CBSPAREPARTS").setReadOnly();
		}
		else
		{
			mgr.getASPField("CBCRAFTS").unsetReadOnly();
			mgr.getASPField("CBCONNECTIONS").unsetReadOnly();
			mgr.getASPField("CBSPAREPARTS").unsetReadOnly();
		}
	} 

	public void HasJobs()
	{
		ASPManager mgr = getASPManager();   

		cmd = trans.addCustomFunction("HASJOB","PM_ACTION_JOB_API.Has_Jobs","JOB_EXIST");
		cmd.addParameter("PM_NO",pmNo);
		cmd.addParameter("PM_REVISION",pmRev);

		trans = mgr.perform(trans);

		String hasJob = trans.getValue("HASJOB/DATA/JOB_EXIST");

		if ("0".equals(hasJob))
		{
			mgr.getASPField("CBJOBS").setReadOnly();
		}
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();        

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden().
		setFunction("''");

		headblk.addField("OBJVERSION").
		setFunction("''").
		setHidden();                   

		headblk.addField("COMPANY").
		setHidden().
		setFunction("''");   

		headblk.addField("CBJOBS").
		setLabel("PCMWCOPYPMACTIONSCBJOBS: Jobs").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBCRAFTS").
		setLabel("PCMWCOPYPMACTIONSCBCRAFTS: Crafts").
		setFunction("''").
		setCheckBox("0,1");    

		headblk.addField("CBPERMITS").
		setLabel("PCMWCOPYPMACTIONSCBPERMITS: Permits").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBCRITERIA").
		setLabel("PCMWCOPYPMACTIONSCBCRITERIA: Criteria").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBCONNECTIONS").
		setLabel("PCMWCOPYPMACTIONSCBCONNNECT: Connections").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBSPAREPARTS").
		setLabel("PCMWCOPYPMACTIONSCBSPARTS: Spare Parts").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBTOOLFACILITY").
		setLabel("PCMWCOPYPMACTIONSCBTOOLFACITY: Tools and Facilities").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBPLANNING").
		setLabel("PCMWCOPYPMACTIONSCBPLANNING: Planning").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("CBDOCUMTS").
		setLabel("PCMWCOPYPMACTIONSCBDOCUMTS: Documents").
		setFunction("''").
		setCheckBox("0,1");

		headblk.addField("ROUNDDEF_ID").   
		setDynamicLOV("PM_ROUND_DEFINITION2","CONTRACT",600,450).                                               
		setLabel("PCMWCOPYPMACTIONSROUTEID: Route ID").
		setFunction("''").
		setSize(15);               

		headblk.addField("MCH_CODE").
		setSize(15).
		setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450).
		setLabel("PCMWCOPYPMACTIONSOBJID: Object ID").
		setMandatory ().
		setFunction("''").
		setUpperCase().
		setHilite().    
		setMaxLength(100);  

		headblk.addField("CONTRACT").
		setSize(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
		setLabel("PCMWCOPYPMACTIONSCONTRACT: Site").
		setMandatory ().
		setUpperCase().
		setFunction("''").
		setHilite().    
		setMaxLength(7); 

		headblk.addField("PM_NO","Number").
		setHidden().
		setFunction("''");    

		headblk.addField("PM_REVISION").
		setHidden().
		setFunction("''"); 

		headblk.addField("NEW_PM_NO","Number").
		setHidden().
		setFunction("''");                            

		headblk.addField("NEW_PM_REV").
		setHidden().
		setFunction("''");                            

		headblk.addField("STD_JOB_ID").
		setHidden().
		setFunction("''");   

		headblk.addField("STD_JOB_REVISION").
		setHidden().
		setFunction("''");   

		headblk.addField("STD_JOB_CONTRACT").
		setHidden().
		setFunction("''");   

		headblk.addField("GET_TXT_ONLY").
		setHidden().
		setFunction("''");

		headblk.addField("JOB_EXIST").
		setHidden().
		setFunction("''");

		headblk.setView("DUAL");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.defineCommand(headbar.SAVERETURN,"copy","checkHeadFields(-1)");
		headbar.defineCommand(headbar.CANCELEDIT,"cancel");
		headbar.enableCommand(headbar.SAVERETURN);
		headbar.enableCommand(headbar.CANCELEDIT);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWCOPYPMACTIONSHD: Copy PM Actions"));
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
		headlay.setDialogColumns(3);   
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCOPYPMACTIONTITLE: Copy PM Actions";
	}

	protected String getTitle()
	{
		return "PCMWCOPYPMACTIONTITLE: Copy PM Actions";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		printHiddenField("ROUNDDEFEXSIST","");
		printHiddenField("ROUNDFEFID","");
		printHiddenField("OBJECTID","");
		printHiddenField("CRAFTS","0");
		printHiddenField("JOBS","0");
		printHiddenField("CONNECTIONS","0");
		printHiddenField("PERMITS","0");
		printHiddenField("SPAREPARTS","0");
		printHiddenField("CRITERIA","0");
		printHiddenField("DOCUMTS","0");
		printHiddenField("TOOLFACILITY","0");
		printHiddenField("PLANNING","0");

		appendToHTML(headbar.showBar());
		appendToHTML("<table>\n");
		appendToHTML("   <tr>\n");
		appendToHTML("      <td>&nbsp;&nbsp;");
		appendToHTML(fmt.drawReadLabel("PCMWCOPYPMACTIONSTXTLABEL: If no options are selected, only the general PM Action data will be copied. 'Obsolete' Standard Jobs will be replaced by their latest revisions, if copying Jobs."));
		appendToHTML("      </td>\n");
		appendToHTML("   </tr>\n");
		appendToHTML("</table>\n");
		appendToHTML(headlay.generateDataPresentation());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("function validateRounddefId(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   document.form.ROUNDDEFEXSIST.value = \"TRUE\";\n");
		appendDirtyJavaScript("   document.form.ROUNDFEFID.value = getValue_('ROUNDDEF_ID',i);\n");
		appendDirtyJavaScript("   document.form.OBJECTID.value = getValue_('MCH_CODE',i);\n");
		appendDirtyJavaScript(" if(document.form.CBCRAFTS.checked == true)\n");
		appendDirtyJavaScript("   document.form.CRAFTS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBJOBS.checked == true)\n");
		appendDirtyJavaScript("   document.form.JOBS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBCONNECTIONS.checked == true)\n");
		appendDirtyJavaScript("   document.form.CONNECTIONS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBPERMITS.checked == true)\n");
		appendDirtyJavaScript("   document.form.PERMITS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBSPAREPARTS.checked == true)\n");
		appendDirtyJavaScript("   document.form.SPAREPARTS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBCRITERIA.checked == true)\n");
		appendDirtyJavaScript("   document.form.CRITERIA.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBDOCUMTS.checked == true)\n");
		appendDirtyJavaScript("   document.form.DOCUMTS.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBTOOLFACILITY.checked == true)\n");
		appendDirtyJavaScript("   document.form.TOOLFACILITY.value = 1;\n");
		appendDirtyJavaScript(" if(document.form.CBPLANNING.checked == true)\n");
		appendDirtyJavaScript("   document.form.PLANNING.value = 1;\n");
		appendDirtyJavaScript("   f.submit();\n");
		appendDirtyJavaScript("}\n");  

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(CancelWin)); // Bug Id 68773
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("     window.close();\n");

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(closewin)); // Bug Id 68773
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	alert('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(Msg)); // XSS_Safe ILSOLK 20070710
		appendDirtyJavaScript("');\n");
		appendDirtyJavaScript("     window.close();\n");
		appendDirtyJavaScript("}\n");  
	}
}
