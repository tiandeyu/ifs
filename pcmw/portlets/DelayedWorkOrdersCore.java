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
 * File        : DelayedWorkOrdersCore.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * PJONSE  000529  Created.
 * PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831  Changed translation constant DELWOCOREQWO to DELWOCOREQWO1 and DELWOCOREQWO2. 
 * ARWILK  001101  Removed the select boxes (In customize page) and added dynamic LOVs.
 * OSRYSE  001104  Changed portlet according to new development guidelines.
 * ARWILK  001123  Added the field planned completion.
 * NUPELK  001128  Removed 'User_Access_API.Get_User_Current_Company_Id' function and
 *                 added new functions according to new devlopment guidelines.
 * NUPELK  001130  Added an LOV for Work Leader in the customization page
 * ARWILK  001130  Modified the show criteria option.
 * JIJOSE  001215  Localization
 * JIJOSE  010111  LOV on Executing department changed
 * SHFELK  010615  Modified to replace '+' sign in plan_s_date if the user enter it.
 * BUNILK  021217  Added AspField MCH_CODE_CONTRACT.   
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031016  Call Id:107925 Changed the getDescription method to pass the ASPManager type parameter there
 *                 by made it possible to use the translate method to translate a given string.
 * THWILK  031030  Call Id:109746 Added getDescription method sice it was overloaded by the call ID 107925.
 * VAGULK  040310  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
 *-------------------------------------------
 * NAMELK  060901  Merged Bug 58216, Eliminated SQL Injection security vulnerability.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * SHAFLK  080715  Bug 75697, Removed DEBUG value set to true.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;


public class DelayedWorkOrdersCore extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.DelayedWorkOrdersCore");


	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPBlock   qblk;
	private ASPBlock   defblk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient String contract;

	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;

	private transient ASPTransactionBuffer  trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String no_of_hits;
	private transient int    size;

	private transient String org_contract;
	private transient String org_code;
	private transient String company;
	private transient String wo_leader;
	private transient String plan_s_date;

	private transient String name;

	// Customization flags
	private transient boolean autosearchflag;	 // If the portlet should be automaticly populated
	private transient boolean infotextflag;	 // Hide the information text
	private transient boolean dispsearchflag;	 // Display search criteria

	//==========================================================================
	//  Construction
	//==========================================================================

	public DelayedWorkOrdersCore( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": DelayedWorkOrdersCore.<init> :"+portal+","+clspath);
	}

	//==========================================================================
	//  Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": DelayedWorkOrdersCore.preDefine()");

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();


		addField(blk, "CONTRACT").setHidden();

		addField(blk, "MCH_CODE_CONTRACT").setHidden();

		addField(blk, "COMPANY").setHidden();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("DWOCOREWO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("DWOCOREWO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)

		addField(blk, "ERR_DESCR").setSize(20).setLabel("DWOCOREDIR: Directive");

		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("DWOCOREOID: Object ID");

		addField(blk, "STATE").setLabel("DWOCORESTATUS: Status");

		addField(blk, "PLAN_F_DATE").setLabel("DWOCOREPLANFDATE: Planned Completion");

		addField(blk, "PRIORITY_ID").setLabel("DWOCOREPRIO: Prio");

		addField(blk, "OBJSTATE").setHidden();

		addField(blk, "ORG_CODE").setHidden();

		addField(blk, "WORK_LEADER_SIGN_ID").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCOMPANY");
		addField(defblk, "USERSIGNID");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "DEFORGCONT");
		addField(defblk, "DEFORGCODE");
		addField(defblk, "PLAN_S_DATE");

		blk.setView("ACTIVE_SEPARATE");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();


		appendJavaScript("function departmentDelayedWorkOrdersCore(obj,id)",
						 "{",
						 "   customizeBox(id);",
						 "}\n");

		appendJavaScript("function workleaderDelayedWorkOrdersCore(obj,id)",
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

		appendJavaScript( "function findRecord(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIND';",
						  "}\n");


		// mutable attribute
		ASPManager mgr = getASPManager();
		name = getDescription(mgr);

		// call the init() 
		init();  
	}

	protected void init()
	{
		if (DEBUG) debug(this+": DelayedWorkOrdersCore.init()");

		contract         = readProfileValue("CONTRACT");   
		company          = readProfileValue("COMPANY");   
		wo_leader        = readProfileValue("WORK_LEADER_SIGN_ID");
		org_contract     = readProfileValue("CONTRACT");
		org_code         = readProfileValue("ORG_CODE");
		name             = readProfileValue("DELSTARTWOCNAME", name);
		plan_s_date      = readProfileValue("PLAN_S_DATE", "");

		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);

		autosearchflag   = readProfileFlag ( "CUSTOMIZED", false ); 
		infotextflag     = readProfileFlag ( "INFOTEXT", false );    
		dispsearchflag   = readProfileFlag ( "DISPSEARCHFLAG", false );

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (Str.isEmpty(company))
		{
			company   = ctx.readValue("COMPANY");
		}

		ASPContext ctx = getASPContext();

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
		if (DEBUG) debug(this+": ActiveWorkOrdersSM.run()");

		if (Str.isEmpty(company))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();
			cmd.defineCustomFunction(this, "User_Default_API.Get_Contract", "DEFCONTRACT");
			deftrans.addCommand("DCONT", cmd);

			cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Site_API.Get_Company", "DEFCOMPANY");
			deftrans.addCommand("COMP", cmd);
			cmd.addReference(this, "DEFCONTRACT", "DCONT/DATA", "DEFCONTRACT"); 

			deftrans = perform(deftrans);        

			company = deftrans.getValue("COMP/DATA/DEFCOMPANY");            
		}

		if (Str.isEmpty(org_code) || Str.isEmpty(org_contract))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Employee_API.Get_Org_Contract('"+company+"', (Company_Emp_API.Get_Max_Employee_Id('"+company+"', (Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)))))", "DEFORGCONT");
			deftrans.addCommand("ORGCN", cmd);

			cmd.defineCustomFunction(this, "Employee_API.Get_Organization('"+company+"', (Company_Emp_API.Get_Max_Employee_Id('"+company+"', (Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)))))", "DEFORGCODE");
			deftrans.addCommand("ORGCD", cmd);

			deftrans = mgr.perform(deftrans);        

			org_contract = deftrans.getValue("ORGCN/DATA/DEFORGCONT");
			org_code = deftrans.getValue("ORGCD/DATA/DEFORGCODE");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery             qry   = trans.addQuery(blk);

		qry.setOrderByClause("WO_NO");
		qry.includeMeta("ALL");
		qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
		qry.addWhereCondition("Maintenance_Site_Utility_API.Get_Site_Date(null) > PLAN_F_DATE");
		qry.addWhereCondition("OBJSTATE in ('WORKREQUEST','FAULTREPORT','OBSERVED','PREPARED','RELEASED','UNDERPREPARATION','STARTED')");

		if (!Str.isEmpty(org_contract))
		{
			qry.addWhereCondition("CONTRACT = ?");
                        qry.addParameter(this,"CONTRACT", org_contract);
		}

		if (!Str.isEmpty(org_code))
		{
			qry.addWhereCondition("ORG_CODE = ?");
                        qry.addParameter(this,"ORG_CODE", org_code);
		}

		if (!Str.isEmpty(wo_leader))
		{
			qry.addWhereCondition("WORK_LEADER_SIGN_ID = ?");
                        qry.addParameter(this,"WORK_LEADER_SIGN_ID", wo_leader);
		}

		if (!Str.isEmpty(plan_s_date))
		{
			String temp_plan_s_date = Str.replace(plan_s_date,"+","");
			if (Str.toInt(temp_plan_s_date) > 0)
			{
				qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                                qry.addParameter(this,"PLAN_S_DATE", temp_plan_s_date);
			}
			else
			{
				qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                                qry.addParameter("PLAN_S_DATE", temp_plan_s_date);
			}
		}

		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		// just perform the dabase transaktion if necessary
		if (autosearchflag || find)
			trans = submit(trans);

		db_rows = blk.getASPRowSet().countDbRows();
		getASPContext().writeValue("DB_ROWS", db_rows+"" );

		ctx.writeValue("COMPANY", company );
		ctx.writeFlag("SEARCH_ORDERS",find);

	}


	//==========================================================================
	//  Portlet description
	//==========================================================================
	public static String getDescription()
	{
		return "DELWOCORETITEL: Delayed Work Orders ";
	}
	public static String getDescription(ASPManager mgr)
	{
		return mgr.translate("DELWOCORETITEL: Delayed Work Orders ");
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": DelayedWorkOrdersCore.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			ASPManager mgr = getASPManager();
			name = getDescription(mgr);
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("DELWOCOREQWO1:  - You have " ) + db_rows +
			translate("DELWOCOREQWO2:  Work Orders.");
		}

		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("DELWOCORECUST: Customize ") +	name;             
	}


	public void printContents()
	{

		// Information text, shown by default.
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("DELWOCOREINFO1: Active work orders where planned completion has passed for a specific "); 
			printScriptLink("DELWOCORECUSTLINK1: maintenance organization", "departmentDelayedWorkOrdersCore");
			printSpaces(1);
			printText("DELWOCOREINFO2: and/or ");
			printScriptLink("DELWOCORECUSTLINK2: work leader.", "workleaderDelayedWorkOrdersCore");  
			printNewLine();
			endFont();
		}

		// Search criteria, not shown by default.
		if (dispsearchflag)
		{
			setFontStyle(BOLD);
			printText("ACTIVEWOORDERSEARCHCRITERIATEXT: Search criteria: ");
			endFont();
			printText("DEWOORCOMEDEPACTIVEWOORGSIT: Organization Site: ");
			printSpaces(2);
			if (!Str.isEmpty(org_contract))
				printText(org_contract);
			else
				printText("DEWOORCOALL: All");       
			printNewLine();  
			printText("DEWOORCOMEDEPACTIVEWO: Maintenance Organization: ");
			printSpaces(2);
			if (!Str.isEmpty(org_code))
				printText(org_code);
			else
				printText("DEWOORCOALL: All");       
			printNewLine(); 
			printText("DEWOORCODELWOCCWL: Choose Work Leader: "); 
			printSpaces(2);
			if (!Str.isEmpty(wo_leader))
				printText(wo_leader);
			else
				printText("DEWOORCOALL: All"); 
			printNewLine();
			printText("DEWOORCODELWOCPLANNEDSTARTDATETEXT: Planned Start Date +/-: ");
			if (!Str.isEmpty(plan_s_date))
				printText(plan_s_date);
			printNewLine();
			printNewLine();
		}

		printHiddenField("CMD","");

		// Show the table only if the button search is pressed or if the autosearch is checked.
		if (autosearchflag || find)
		{

			int    nRows;
			nRows = rowset.countRows();

			if (nRows > 0)
			{
				printTable(tbl);

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("DELWOCFIRST: First","firstCust");
					else
						printText("DELWOCFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("DELWOCPRV: Previous","prevCust");
					else
						printText("DELWOCPRV: Previous");

					printSpaces(5);
					String rows = translate("DELWOCROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("DELWOCLAST: Last","lastCust");
					else
						printText("DELWOCLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("DELWOCNEXT: Next","nextCust");
					else
						printText("DELWOCNEXT: Next");

					printNewLine();
					printNewLine();

				}
			}
			else
			{
				if (!autosearchflag)
				{
					printSubmitLink("ACTIVEWOSEARCH: Search","findRecord");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("ACTIVEWONODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("ACTIVEWOSEARCH: Search","findRecord");
			printNewLine();
			printNewLine();
		}
	}

	//==========================================================================
	//  Customiztion section
	//==========================================================================

	public boolean canCustomize()
	{
		return true;
	}

	public void runCustom()
	{

		ASPManager mgr = getASPManager();
		trans          = mgr.newASPTransactionBuffer();

		if (DEBUG)
		{
			debug(this+": DelayedWorkOrdersCore.runCustom()");
		}

		ASPQuery qry = trans.addQuery("LLIST","EMPLOYEE_LOV", "EMPLOYEE_ID, EMPLOYEE_ID||' - '||NAME","COMPANY = ? ", "EMPLOYEE_ID");
                qry.addParameter(this,"COMPANY",company);

		trans = mgr.perform(trans);
	}

	public void printCustomBody() throws FndException
	{
		if (DEBUG)
		{
			debug(this+": DelayedWorkOrdersCore.printCustomBody()");
			debug("\tprintCustomBody(): DelayedWorkOrdersCore");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELWOCQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("DELWOCCORGSI: Choose Organization Site: ");
		printNewLine();
		printSpaces(5);
		printField("ORGCONTRACT", org_contract);
		printDynamicLOV("ORGCONTRACT","USER_ALLOWED_SITE_LOV","PCMWDELCODEORGSITLOV: List of Organization Site");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELWOCCED: Choose Maintenance Organization: ");
		printNewLine();
		printSpaces(5);
		printField("ORGCODEC", org_code);
		printDynamicLOV("ORGCODEC","ORG_CODE_ALLOWED_SITE_LOV","PCMWDELCODEPLOV: List of Maintenance Organization");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("DELWOCCWL: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC", wo_leader);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELWOCPLANNEDSTARTDATE: Planned Start Date +/- ");
		printField("PLAN_S_DATE", plan_s_date, 5, 5);
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELWOCEPORTLETCONFIG: Portlet Configuration");
		endFont();

		printNewLine();
		printSpaces(5);
		printText("DELWOCPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("DELSTARTWOCNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DEWOORCODISPSEARCHFLAG", dispsearchflag);
		printSpaces(3);
		printText("DEWOORCODISPSEARCHFLAGTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("DEWOORCOWORKINFOTEXT", infotextflag);
		printSpaces(3);
		printText("DEWOORCODONETEXTINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("DEWOORCOCOCUSTOMIZED", autosearchflag);
		printSpaces(3);
		printText("DEWOORCOTEXTCUSTOMIZED: Search automatically");
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("DELWOCLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}

	public void submitCustomization()
	{

		if (DEBUG)
		{
			debug(this+": DelayedWorkOrdersCore.submitCustomization()");
			debug("\tsubmitCustomization(): current values:\n\t\tcontract="+contract);
		}

		// Read values from the fields in the customization page.
		org_contract = readValue("ORGCONTRACT");
		org_code     = readValue("ORGCODEC");
		wo_leader    = readValue("WOLEADERC");
		name         = readValue("DELSTARTWOCNAME");
		plan_s_date  = readValue("PLAN_S_DATE");

		// Write values to the profile.	      	      
		writeProfileValue("CONTRACT", readAbsoluteValue("ORGCONTRACT") );
		writeProfileValue("ORG_CODE", readAbsoluteValue("ORGCODEC") );
		writeProfileValue("COMPANY",  readAbsoluteValue("COMPANY") );
		writeProfileValue("WORK_LEADER_SIGN_ID",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("DELSTARTWOCNAME",  readAbsoluteValue("DELSTARTWOCNAME") );
		writeProfileValue("PLAN_S_DATE", readAbsoluteValue("PLAN_S_DATE") );

		dispsearchflag = "TRUE".equals(readValue("DEWOORCODISPSEARCHFLAG")); 
		writeProfileFlag("DISPSEARCHFLAG", dispsearchflag);

		infotextflag = "TRUE".equals(readValue("DEWOORCOWORKINFOTEXT")); 
		writeProfileFlag("INFOTEXT", infotextflag);

		autosearchflag = "TRUE".equals(readValue("DEWOORCOCOCUSTOMIZED")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}
	}
}