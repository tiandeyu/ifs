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
*  File        : MaintenanceInvPartRMB.java 
*  Created     : ASP2JAVA Tool  010520  Created Using the ASP file MaintenanceInvPartRMB.asp
*  Modified    :
*  CHCRLK  010529  Modified newRowITEM() and added anyLocation().
*  CHCRLK  010613  Modified overwritten validations. 
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                           (Caused when cancelNew is performed after saveNew)
*  SAPRLK  031230  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040210  Edge Developments - (Remove uneccessary global variables, Enhance Performance)
* ------------------------------ EDGE - SP1 Merge ---------------------------
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies.    
*  VAGULK  040324  Merged with SP1.
*  ARWILK  040709  Added ORG_CONTRACT to the headblk. (IID AMEC500C: Resource Planning)
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding  
*  AMNILK  060807  Merged Bug Id: 58214.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceInvPartRMB extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceInvPartRMB");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock itemblk;
	private ASPRowSet itemset;
	private ASPCommandBar itembar;
	private ASPTable itemtbl;
	private ASPBlockLayout itemlay;

	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String sPartNo;
	private String sContract;
	private boolean location;
	private ASPCommand cmd;
	private ASPQuery q;
	private ASPBuffer data;

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenanceInvPartRMB(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		frm = mgr.getASPForm();   
		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		sPartNo = ctx.readValue("PARTNOVAR", sPartNo);
		sContract = ctx.readValue("CONTRACTVAR", sContract);      
		location = ctx.readFlag("LOCATION", false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if ((!mgr.isEmpty(mgr.getQueryStringValue("PART_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT"))))
		{
			sPartNo = mgr.readValue("PART_NO");
			sContract = mgr.readValue("CONTRACT");
			okFind();      
		}
		else if (mgr.dataTransfered())
			okFind();

		adjust();

		ctx.writeValue("PARTNOVAR", sPartNo);
		ctx.writeValue("CONTRACTVAR", sContract);
		ctx.writeFlag("LOCATION", location);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();
		String txt;
		String val = mgr.readValue("VALIDATE");

		if ("PART_NO".equals(val))
		{
			cmd = trans.addCustomFunction("CATDESC", "PART_CATALOG_API.Get_Description", "DESCRIPTION");
			cmd.addParameter("PART_NO");

			trans = mgr.validate(trans);

			String part_desc = trans.getValue("CATDESC/DATA/DESCRIPTION");

			txt= (mgr.isEmpty(part_desc)?"":part_desc) + "^" ;

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

		q = trans.addQuery(headblk);

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENANCEINVPARTRMBNODATA: No data found."));
			headset.clear();
		}
		else
			okFindITEM();
	}

	public void okFindITEM()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int headrowno = headset.getCurrentRowNo();

		q = trans.addQuery(itemblk);
                //Bug 58216 start
                q.addWhereCondition("PART_NO = ?");
                q.addWhereCondition("CONTRACT = ?");
                q.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
                q.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
                //Bug 58216 end
		q.includeMeta("ALL");
		mgr.submit(trans);

		headset.goTo(headrowno);

		if (mgr.commandBarActivated())
		{
			if (itemset.countRows() == 0 && "ITEM.OkFind".equals(mgr.readValue("__COMMAND")))
			{
				mgr.showAlert(mgr.translate("PCMWMAINTENANCEINVPARTRMBNODATA: No data found."));
				itemset.clear();
			}
		}
	}

	public void countFindITEM()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
		itemset.clear();
	}

	public void newRowITEM()
	{
                ASPManager mgr = getASPManager();

                trans.clear();

		cmd = trans.addEmptyCommand("ITEM","MAINTENANCE_INV_PART_API.New__",itemblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		data = trans.getBuffer("ITEM/DATA");
		data.setFieldItem("PART_NO", sPartNo);
		data.setFieldItem("CONTRACT", sContract);   
		itemset.addRow(data);
		
        }

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PART_NO");
		f.setSize(15);
		f.setMandatory();
		f.setLabel("PCMWMAINTENANCEINVPARTRMBPARTNO: Part No");
		f.setUpperCase();
		f.setReadOnly();
		f.setInsertable();
		f.setCustomValidation("PART_NO","DESCRIPTION");

		f = headblk.addField("DESCRIPTION");
		f.setSize(35);
		f.setMaxLength(35);
		f.setMandatory();
		f.setLabel("PCMWMAINTENANCEINVPARTRMBDESCRIPTION: Part Description");

		f = headblk.addField("CONTRACT");
		f.setSize(15);
		f.setMandatory();
		f.setLabel("PCMWMAINTENANCEINVPARTRMBCONTRACT: Site");
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
		f.setLOVProperty("TITLE",mgr.translate("PCMWMAINTENANCEINVPARTRMBUSERSITE: Site"));
		f.setUpperCase();
		f.setReadOnly();
		f.setInsertable();

		f = headblk.addField("DEFLOC");
		f.setHidden();
		f.setFunction("''");

		headblk.setView("INVENTORY_PART");
		headset = headblk.getASPRowSet();
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMAINTENANCEINVPARTRMBMNTINFO: Maint Info"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.BACK);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
		headlay.setDialogColumns(3);

		//--------------------------------------------------------------------------------------------------------------
		//-------------------------------------------    ITEM BLock    -------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("ITEMOBJID");
		f.setDbName("OBJID");
		f.setHidden();

		f = itemblk.addField("ITEMOBJVERSION");
		f.setDbName("OBJVERSION");
		f.setHidden();

		f = itemblk.addField("ORG_CONTRACT");
		f.setSize(10);
		f.setMaxLength(5);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
		f.setLabel("PCMWMAINTENANCEINVPARTRMBORGCONTR: Organization Site");
		f.setUpperCase();

		f = itemblk.addField("ORG_CODE");
		f.setSize(15);
		f.setMaxLength(8);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV", "ORG_CONTRACT CONTRACT");
		f.setLabel("PCMWMAINTENANCEINVPARTRMBORGCODE: Maintenance Organization");
		f.setUpperCase();

		f = itemblk.addField("DIRECTIVE");
		f.setSize(20);
		f.setMaxLength(60);
		f.setLabel("PCMWMAINTENANCEINVPARTRMBDIRECTIVE: Default Directive");

		f = itemblk.addField("REPAIRABLE");
		f.setUpperCase();
		f.setLabel("PCMWMAINTENANCEINVPARTRMBREPAIRABLE: Repairable");
		f.setCheckBox("FALSE,TRUE");
		f.setCustomValidation("REPAIRABLE,AUTO_REPAIR","AUTO_REPAIR");

		f = itemblk.addField("AUTO_REPAIR");
		f.setUpperCase();
		f.setLabel("PCMWMAINTENANCEINVPARTRMBAUTOREPAIR: Auto Repair WO");
		f.setCheckBox("FALSE,TRUE");    
		f.setCustomValidation("REPAIRABLE,AUTO_REPAIR","AUTO_REPAIR");  

		f = itemblk.addField("ITEM_PART_NO");
		f.setDbName("PART_NO");   
		f.setHidden();       

		f = itemblk.addField("ITEM_CONTRACT");
		f.setDbName("CONTRACT");   
		f.setHidden(); 

		itemblk.setView("MAINTENANCE_INV_PART");
		itemblk.defineCommand("MAINTENANCE_INV_PART_API","New__,Modify__,Remove__");
		itemblk.setMasterBlock(headblk);   

		itemset = itemblk.getASPRowSet();
		itemtbl = mgr.newASPTable(itemblk);
		itemtbl.setTitle(mgr.translate("PCMWMAINTENANCEINVPARTRMBREPINFO: Repair Information"));
		itemtbl.setWrap();  

		itembar = mgr.newASPCommandBar(itemblk);
		itembar.disableCommand(itembar.BACK);
		itembar.disableCommand(itembar.DUPLICATEROW);       
		itembar.defineCommand(itembar.OKFIND,"okFindITEM");
		itembar.defineCommand(itembar.COUNTFIND,"countFindITEM");
		itembar.defineCommand(itembar.NEWROW,"newRowITEM");
                itembar.enableCommand(itembar.FIND);

		itembar.defineCommand(itembar.SAVERETURN,null,"checkItemFields()");
		itembar.disableCommand(itembar.SAVENEW);

		itemlay = itemblk.getASPBlockLayout();
		itemlay.setDefaultLayoutMode(itemlay.SINGLE_LAYOUT);             
	}

	public void adjust()
	{
		if (itemset.countRows() > 0)
			itembar.disableCommand(itembar.NEWROW);
		else
		{
			itembar.disableCommand(itembar.EDITROW);     
			itembar.disableCommand(itembar.DELETE);        
		}
		
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMAINTENANCEINVPARTRMBTITLE: Maint Info";
	}

	protected String getTitle()
	{
		return "PCMWMAINTENANCEINVPARTRMBTITLE: Maint Info";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
		{
			appendToHTML("<table cellpadding=0 width=");
			appendToHTML("frm.getFormWidth()");
			appendToHTML(" border=\"0\">\n");
			appendToHTML("<tr>\n");
			appendToHTML("<td>\n");
			appendToHTML(headlay.generateDataPresentation());
			appendToHTML("</td>			\n");
			appendToHTML("</tr>	\n");
			appendToHTML("</table>	\n");
		}

		if (itemlay.isFindLayout() || itemlay.isNewLayout() || itemlay.isEditLayout())
		{
			appendToHTML(headlay.generateDataPresentation());
			appendToHTML(itemlay.show());
		}
		else
			appendToHTML(itemlay.show());

		appendDirtyJavaScript("function validateAutoRepair(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if (getField_('REPAIRABLE',i).checked == false)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      getField_('AUTO_REPAIR',i).checked = false;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkAutoRepair(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('AUTO_REPAIR',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('AUTO_REPAIR',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=AUTO_REPAIR'\n");
		appendDirtyJavaScript("		+ '&REPAIRABLE=' + URLClientEncode(getValue_('REPAIRABLE',i))\n");
		appendDirtyJavaScript("		+ '&AUTO_REPAIR=' + URLClientEncode(getValue_('AUTO_REPAIR',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function validateRepairable(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("	if (getField_('REPAIRABLE',i).checked == false)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      getField_('AUTO_REPAIR',i).checked = false;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("	setDirty();\n");
		appendDirtyJavaScript("	if( !checkRepairable(i) ) return;\n");
		appendDirtyJavaScript("	if( getValue_('REPAIRABLE',i)=='' )\n");
		appendDirtyJavaScript("	{\n");
		appendDirtyJavaScript("		getField_('AUTO_REPAIR',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	}\n");
		appendDirtyJavaScript("	window.status='Please wait for validation';\n");
		appendDirtyJavaScript("	r = __connect(\n");
		appendDirtyJavaScript("		'");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=REPAIRABLE'\n");
		appendDirtyJavaScript("		+ '&REPAIRABLE=' + URLClientEncode(getValue_('REPAIRABLE',i))\n");
		appendDirtyJavaScript("		+ '&AUTO_REPAIR=' + URLClientEncode(getValue_('AUTO_REPAIR',i))\n");
		appendDirtyJavaScript("		);\n");
		appendDirtyJavaScript("	window.status='';\n");
		appendDirtyJavaScript("}\n");
	}
}
