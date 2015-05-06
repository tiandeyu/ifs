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
 * File        : PMProcessing.java 
 * Description : Wiew al separate PM:s for a Object.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Created     : OSRYSE  000601  Created.
 * Modified    :
 * OSRYSE  000808  Added the functionallity to add these parameters from the ABB Portal,
 * JIJOSE  000829  Localization changes.
 * OSRYSE  000831  Localization changes in getDescription.
 * ARWILK  001102  Removed the select boxes (In customize page) and added dynamic LOVs.
 * CHCRLK  001113  Added check boxes in the customize page and did some corrections.
 * JIJOSE  001218  Search function and query conditions
 * JIJOSE  010111  Added object group and executing dept.
 * NUPELK  010903  Modified search condition when no date range is entered 
 * NUPELK  010903  Made further changes to the date range condition 
 * THWILK  031022  Call ID 108196 Modified "Obejct" to "Object" in MPROWOCANTPERF constant.
 * VAGULK  040311  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * THWILK  040604  Added PM_REVISION under IID AMEC109A, Multiple Standard Jobs on PMs.
 * ARWILK  040722  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
 * -----------------------------------
 * NAMELK  060901  Merged Bug 58216, Eliminated SQL Injection security vulnerability.
 * ----------------------------------------------------------------------------
 * ChAmlk  061016  Bug 61103, Modified run() to remove web security issues.
 * ILSOLK  070227  Merged Bug ID 61103.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * ILSOLK  070730  Eliminated LocalizationErrors.
 * SHAFLK  080715  Bug 75697, Removed DEBUG value set to true.
 * SHAFLK  090710  Bug 83839, Modified run() and preDefine().
 * SHAFLK  090821  Bug 85423, Modified run().
 * ----------------------------------------------------------------------------
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;


public class PMProcessing extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	private final static String default_date_horizon = "30";

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcm.portlets.PMProcessing");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient String contract;
	private transient String object_id;
	private transient String group_id;
	private transient String org_code;

	private transient String  name;
	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;

	private transient String abbFrom_date;
	private transient String abbTo_date;
	private transient String abbContract;
	private transient String abbMch_code;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;
	private transient String  from_date;    
	private transient String  to_date;
	private transient boolean disp_search;
	private transient boolean hide_info_text;
	private transient boolean search_auto;   
	private transient boolean search;
	private transient boolean cantperform;

	//==========================================================================
	//  Construction
	//==========================================================================

	public PMProcessing( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": PMProcessing.<init> :"+portal+","+clspath);
	}

	public ASPPage construct() throws FndException
	{
		if (DEBUG) debug(this+": PMProcessing.construct()");
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": PMProcessing.doFreeze()");
		super.doFreeze();
	}

	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": PMProcessing.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	//  
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": PMProcessing.preDefine()");
		// Get the Parameters from the ABB Portal
		if (getQueryString("PORTLET") != null)

			if (getQueryString("PORTLET").equalsIgnoreCase("PMProcessing"))
			{
				if (getQueryString("OBJECT_ID") != null)
					abbMch_code =  getQueryString("OBJECT_ID");
				else
					abbMch_code	=  null;

				if (getQueryString("SITE") != null)
					abbContract = getQueryString("SITE");
				else
					abbContract	= null;

				/*if(getQueryString("PM_NO") != null)
				   abbPm_no = getQueryString("PM_NO");
				else
				   abbPm_no = null;*/

				if (getQueryString("FROM_DATE") != null)
					abbFrom_date = getQueryString("FROM_DATE");
				else
					abbFrom_date = null;

				if (getQueryString("TO_DATE") != null)
					abbTo_date = getQueryString("TO_DATE");
				else
					abbTo_date = null;
			}

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();


		addField(blk, "PM_REVISION").setLabel("PMPPMREVISION: PM Revision").setHidden();
		addField(blk, "PM_NO").setLabel("PMPPMNO: PM No").
		setHyperlink("pcmw/PmAction.page","PM_NO,PM_REVISION");



		addField(blk, "PLANNED_DATE","Date").setLabel("PMPPDATE: Planned Date");

		addField(blk, "ACTION_DESCRIPTION").setLabel("PMPADESC: Action Description"). 
		setFunction("pm_action_api.get_action_descr(:PM_NO,:PM_REVISION)");

		addField(blk, "PM_TYPE").setLabel("PMPPMTYPE: PM Type").setHidden();

                addField(blk, "VALID_FROM").setFunction("''").setHidden();
                addField(blk, "VALID_TO2").setFunction("''").setHidden();

		addField(blk, "CONTRACT").setLabel("PMSITE: Site");

		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,CONTRACT").setLabel("PMMCHCODE: Object ID");

		addField(blk, "ORG_CODE").setLabel("PMORGCODE: Maintenance Organization");
		addField(blk, "GROUP_ID").setHidden();

		blk.setView("PM_CALENDAR_PLAN");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();

		appendJavaScript("function customizePMProcessing(obj,id)",
						 "{",
						 "   customizeBox(id);",
						 "}\n");

		appendJavaScript( "function firstCust(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIRST';",
						  "}\n");

		appendJavaScript( "function prevCust(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'PREV';",
						  "}\n");

		appendJavaScript( "function lastCust(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'LAST';",
						  "}\n");

		appendJavaScript( "function nextCust(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'NEXT';",
						  "}\n");

		appendJavaScript( "function findCust(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIND';",
						  "}\n");

		init();

	}

	protected void init()
	{
		if (DEBUG) debug(this+": PMProcessing.init()");

		// Uses the value from the ABB Portal
		if (abbMch_code != null)
		{
			object_id = abbMch_code;
			abbMch_code = null;
			writeProfileValue("MCH_CODE", object_id);
		}
		else
			object_id = readProfileValue("MCH_CODE");

		org_code      = readProfileValue("ORG_CODE");
		group_id      = readProfileValue("GROUP_ID");

		if (abbContract != null)
		{
			contract = abbContract;
			abbContract = null;
			writeProfileValue("CONTRACT", contract);
		}
		else
			contract = readProfileValue("CONTRACT");

		if (abbFrom_date != null)
		{
			from_date = abbFrom_date;
			abbFrom_date = null;
			writeProfileValue("VALID_FROM", from_date);
		}
		else
			from_date = readProfileValue("VALID_FROM");

		if (abbTo_date != null)
		{
			to_date = abbTo_date;
			abbTo_date = null;
			writeProfileValue("VALID_TO2", to_date);
		}
		else
			to_date	= readProfileValue("VALID_TO2");

		no_of_hits     = readProfileValue("NO_HITS", default_size+"");
		size           = Integer.parseInt(no_of_hits);
		name           = readProfileValue("PMPNAME");
		disp_search    = readProfileFlag("DISPSEARCH", false);
		hide_info_text = readProfileFlag("HIDEINFOTEXT", false);
		search_auto    = readProfileFlag("SEARCHAUTO", false);

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		String cmd = readValue("CMD");

		if ("FIND".equals(cmd))
		{
			skip_rows = 0;
			find = true;
		}
		else if ("FIRST".equals(cmd))
		{
			skip_rows = 0;
			find = true;
		}
		else if ("PREV".equals(cmd))
			if (skip_rows>=size)
			{
				skip_rows -= size;
				find = true;
			}
			else
			{
				skip_rows = 0;
				find = true;
			}


		else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
		{
			skip_rows += size;
			find = true;
		}
		else if ("LAST".equals(cmd))// && skip_rows>=size )
		{
			skip_rows = db_rows - size;
			find = true;
		}

		ctx.writeValue("SKIP_ROWS",skip_rows+"");  
	}


	protected void run()
	{       
		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery             qry   = trans.addEmptyQuery(blk);
		String planned_date;

		qry.addWhereCondition("wo_no is NULL");

		if (Str.isEmpty(from_date))	from_date ="0";
		if (Str.isEmpty(to_date)) to_date ="0";
                String temp_from_date = Str.replace(from_date,"+","");
                String temp_to_date = Str.replace(to_date,"+","");

                if (Str.toInt(temp_from_date) != 0 && Str.toInt(temp_to_date) != 0)
                {
                    qry.addWhereCondition("trunc(planned_date) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? and trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                    qry.addParameter(this,"VALID_FROM", temp_from_date);
                    qry.addParameter(this,"VALID_TO2", temp_to_date);
                    
                }
                else if (Str.toInt(temp_from_date) != 0 && Str.toInt(temp_to_date) == 0)
                {
                    qry.addWhereCondition("trunc(planned_date) >= trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                    qry.addParameter(this,"VALID_FROM", temp_from_date);
                }
                else if (Str.toInt(temp_from_date) == 0 && Str.toInt(temp_to_date) != 0)
                {
                    qry.addWhereCondition("trunc(planned_date) <= trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                    qry.addParameter(this,"VALID_TO2", temp_to_date);
                    
                }


		if (!Str.isEmpty(contract))
                {
                    qry.addWhereCondition("CONTRACT = ?");
                    qry.addParameter(this,"CONTRACT", contract);
                }
		else
			qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
		qry.addWhereCondition("Pm_Type_API.Encode(pm_type) = 'ActiveSeparate'");

		if (!Str.isEmpty(object_id))
                {
                    qry.addWhereCondition("MCH_CODE = ? ");
                    qry.addParameter(this,"MCH_CODE", object_id);
                }                                                
		if (!Str.isEmpty(group_id))
		{
                    qry.addWhereCondition("GROUP_ID = ? ");
                    qry.addParameter(this,"GROUP_ID", group_id);
		}
		if (!Str.isEmpty(org_code))
		{
                    qry.addWhereCondition("ORG_CODE = ? ");
                    qry.addParameter(this,"ORG_CODE", org_code);
		}

		if (from_date =="0") from_date = "";
		if (to_date =="0") to_date = "";

		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		if ((search_auto || find))
		{
			if (!Str.isEmpty(contract) || !Str.isEmpty(object_id))
				submit(trans);
			else
				cantperform	= true;  
		}

		db_rows = rowset.countDbRows();
		ctx.writeValue("DB_ROWS", db_rows+"" );
		ctx.writeFlag("SEARCH_ORDERS",find);
	}


	//==========================================================================
	//  
	//==========================================================================

	public static String getDescription()
	{
		return "PMPROCESS: PM Processing ";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": PMProcessing.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("PMPROWOQWO: - You have ") + db_rows +
			translate("PMPROWOQWO2:  PM No:s.");
		}
		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("PMPROWOCUST: Customize ")	+ name;
	}


	public void printContents()
	{
		if (!hide_info_text)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("PMPINFO1: Here you can find Separate PM Actions for an object. "); 
			printText("PMPINFO2: To see more specific information you can ");
			printScriptLink("PMPLINK: customize", "customizePMProcessing");  
			printText("PMPROWOINFO3:  and give more search data. Site or Object Id are mandatory! ");
			endFont();
		}

		printHiddenField("CMD","");

		if (disp_search)
		{
			printText("ISSMATSEARCH: Search Criteria:");  
			printNewLine();
			printText("PMPSITETXT: Site: ");
			printSpaces(2);
			if (!Str.isEmpty(contract))
				printText(contract);
			else
				printText("PMPROALL: All");  

			printNewLine();
			printText("PMPOBJECTIDTXT: Object Id: ");
			printSpaces(2);
			if (!Str.isEmpty(object_id))
				printText(object_id);
			else
				printText("PMPROALL: All");

			printNewLine();         
			printText("ACWOORSMOGRIDACTIVEWOSM: Object Group Id: ");
			if (!Str.isEmpty(group_id))
				printText(group_id);
			else
				printText("ACTIVWOSMALL: All");

			printNewLine();
			printText("ACWOORSMEDEPACTIVEWOSM: Maintenance Organization: ");
			if (!Str.isEmpty(org_code))
				printText(org_code);
			else
				printText("ACTIVWOSMALL: All");

			printNewLine();
			printText("PMPROVALIDFROM: Valid from +/-: ");
			printSpaces(2);
			if (!Str.isEmpty(from_date))
				printText(from_date);
			else
				printText("PMPRONONE:  ");   

			printNewLine();
			printText("PMPROVALIDTO: Valid to +/-: ");
			printSpaces(2);
			if (!Str.isEmpty(to_date))
				printText(to_date);
			else
				printText("PMPRONONE:  ");            
			printNewLine();
			printNewLine();
		}

		int    nRows;
		nRows = rowset.countRows();

		if (search_auto || find)
		{
			if (cantperform)
			{
				setFontStyle(BOLD);   
				printText("PMPROWOCANTPERF: Site or Object Id mandatory!");  
				endFont();
				cantperform = false;
			}
			else
			{
				if (nRows > 0)
				{
					printTable(tbl);

					if (size < db_rows)
					{
						printNewLine();

						if (skip_rows>0)
							printSubmitLink("PMPROWOFIRST: First","firstCust");
						else
							printText("PMPROWOFIRST: First");

						printSpaces(5);
						if (skip_rows>0)
							printSubmitLink("PMPROWOPRV: Previous","prevCust");
						else
							printText("PMPROWOPRV: Previous");

						printSpaces(5);
						String rows = translate("PMPROWOROWS: (Rows &1 to &2 of &3)",
												(skip_rows+1)+"",
												(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
												db_rows+"");
						printText( rows );
						printSpaces(5);

						if (skip_rows<db_rows-size)
							printSubmitLink("PMPROWOLAST: Last","lastCust");
						else
							printText("PMPROWOLAST: Last");

						printSpaces(5);
						if (skip_rows<db_rows-size)
							printSubmitLink("PMPROWONEXT: Next","nextCust");
						else
							printText("PMPROWONEXT: Next");
					}
					printNewLine();
				}
				else
				{
					if (!search_auto)
					{
						printSubmitLink("PCMWPORTPMPROCESSFINDCUST: Search","findCust");
						printNewLine();
						printNewLine();
					}

					setFontStyle(BOLD);    
					printText("PMPROWONODATA: No data found!");
					endFont();
				}
			} 
		}
		else
		{
			printSubmitLink("PCMWPORTPMPROCESSFINDCUSTOMER: Search","findCust");
			printNewLine();
			printNewLine();
		}
	}

	//==========================================================================
	//  
	//==========================================================================

	public void runCustom()
	{
	}

	public void printCustomBody() throws FndException
	{
		if (DEBUG)
		{
			debug(this+": PMProcessing.printCustomBody()");
			debug("\tprintCustomBody(): PMProcessing");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("PMPROWOQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("PMPROCS: Choose Site: ");
		printNewLine();
		printSpaces(5);
		printField("CONTRACTC",contract);
		printDynamicLOV("CONTRACTC","USER_ALLOWED_SITE_LOV","PMPROCLOVCONT: List of Site");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printText("PMPROCO: Choose Object Id: ");
		printNewLine();
		printSpaces(5);
		printField("OBJECTIDC", object_id, 20);
		printDynamicLOV("OBJECTIDC","MAINTENANCE_OBJECT","PMPROCLOVOI: List of Object ID");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEOGRIDACTIVEWOPM: Choose Object Group Id:");
		printNewLine();
		printSpaces(5);
		printField("GROUPIDC",group_id);
		printDynamicLOV("GROUPIDC","EQUIPMENT_OBJ_GROUP","LOVGRPIDACTIVEWOPM: List of Group ID");   
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEEDEPACTIVEWOPM: Choose Maintenance Organization:");
		printNewLine();
		printSpaces(5);
		printField("ORGCODEC",org_code);
		printDynamicLOV("ORGCODEC","ORG_CODE_ALLOWED_SITE_LOV","LOVORGCDACTIVEWOPM: List of Maintenance Organization","CONTRACT = '"+contract+"'");     
		printNewLine();
		printNewLine();
		printSpaces(5);

		printNewLine();
		printSpaces(5);
		printText("WOQLISTVALIDFROM: Valid from +/- ");
		printField("VALID_FROM", from_date, 5, 5);
		printNewLine();

		printNewLine();
		printSpaces(5);
		printText("WOQLISTVALIDTO: Valid to +/- ");
		printField("VALID_TO2", to_date, 5, 5);
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("PMPROWOPORTLETCONFIG: Portlet Configuration");
		endFont();

		printNewLine();
		printSpaces(5);
		printText("PMPPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("PMPNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DISPSEARCH", disp_search);
		printSpaces(3);
		printText("DISSEACRI: Display search criteria");
		printNewLine();
		printNewLine();       

		printSpaces(5);
		printCheckBox("HIDEINFOTEXT", hide_info_text);
		printSpaces(3);
		printText("PCMWPORTPMPROCESSHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("SEARCHAUTO", search_auto);
		printSpaces(3);
		printText("PCMWPORTPMPROCESSSEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("PMPLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
	}

	public void submitCustomization()
	{
		if (DEBUG)
		{
			debug(this+": NotInvoicedPMProcessingPMProcessing.submitCustomization()");
			debug("\tsubmitCustomization(): current values:\n\t\tcontract="+contract);
		}
		contract    = readValue("CONTRACTC");
		object_id   = readValue("OBJECTIDC");
		org_code    = readValue("ORGCODEC");
		group_id    = readValue("GROUPIDC");
		from_date   = readValue("VALID_FROM");
		to_date     = readValue("VALID_TO2");
		name        = readValue("PMPNAME");
		disp_search = "TRUE".equalsIgnoreCase(readValue("DISPSEARCH"));
		hide_info_text = "TRUE".equalsIgnoreCase(readValue("HIDEINFOTEXT"));
		search_auto = "TRUE".equalsIgnoreCase(readValue("SEARCHAUTO"));

		writeProfileValue("CONTRACT", readAbsoluteValue("CONTRACTC") );
		writeProfileValue("MCH_CODE",  readAbsoluteValue("OBJECTIDC") );
		writeProfileValue("ORG_CODE",  readAbsoluteValue("ORGCODEC") );
		writeProfileValue("GROUP_ID",  readAbsoluteValue("GROUPIDC") );
		writeProfileValue("VALID_FROM", readAbsoluteValue("VALID_FROM") );
		writeProfileValue("VALID_TO2", readAbsoluteValue("VALID_TO2") );
		writeProfileValue("PMPNAME", readAbsoluteValue("PMPNAME") );
		writeProfileFlag("DISPSEARCH", disp_search);
		writeProfileFlag("HIDEINFOTEXT", hide_info_text);
		writeProfileFlag("SEARCHAUTO", search_auto); 

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);

		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}
	}

	public boolean canCustomize()
	{
		return true;
	}
}