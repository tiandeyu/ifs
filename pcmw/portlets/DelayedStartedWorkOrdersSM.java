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
 * File        : DelayedStartedWorkOrdersSM.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * PJONSE  000605  Created.
 * PJONSE  000704  Call Id: 45169. Added 'if (dummy != 2)','if dummy == 1' and 'if dummy == 2' in protected void run().
 * PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831  Changed translation constant DELSTARTWOSMCQWO to DELSTARTWOSMCQWO1 and DELSTARTWOSMCQWO2. 
 * ARWILK  001101  Removed the select boxes (In customize page) and added dynamic LOVs.
 * OSRYSE  001105  Changed portlet according to new development guidelines.
 * NUPELK  001128  Removed 'User_Access_API.Get_User_Current_Company_Id' function and
 *                 added new functions according to new devlopment guidelines.
 * JEWILK  001129  Modified function printContents() to print search criteria values in separate printText() methods. 
 * JEWILK  001212  Changed the hyperlink of field 'WO_NO' to 'ActiveSeparate2lightSM.asp'
 * JEWILK  001212  Modified to query WOs in state 'FAULTREPORT' also.
 * JIJOSE  001215  Localization
 * NUPELK  001227  Added an LOV for Work Leader in the customization page. 
 * BUNILK  021217  Added AspField MCH_CODE_CONTRACT.   
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031016  Call Id:107925 Changed the getDescription method to pass the ASPManager type parameter there
 *                 by made it possible to use the translate method to translate a given string.
 * THWILK  031030  Call Id:109746 Added getDescription method sice it was overloaded by the call ID 107925.
 * VAGULK  040310  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * VAGULK  040310  Web Alignment ,Changed field name "CUSTOMER_NO" to "CUSTOMER_ID".
 * --------------------------------- Edge - SP1 Merge -------------------------
 * SHAFLK  040317  Bug 43519, Modified run() method.
 * THWILK  040325  Merge with SP1.
 * ARWILK  040721  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
 * ARWILK  041025  Merged LCS-47169 and did some other corrections.
 * ----------------------------------
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELK  060901  merged bug 58216.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * AMNILK  070716  Eliminated SQLInjection.
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


public class DelayedStartedWorkOrdersSM extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.DelayedStartedWorkOrdersSM");


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

	private transient String  string_date;
	private transient String  sign;

	private transient String  prepared;
	private transient String  underpreparation;
	private transient String  released;
	private transient String  workrequest;
	private transient String  observed;
	private transient String  faultreport;

	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;

	private transient ASPTransactionBuffer  trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	private transient String  contract;
	private transient String  company;
	private transient String  org_code;
	private transient String  work_leader;
	private transient String  work_type;
	private transient String  group_id;
	private transient String  customer_id;
	private transient String  aut_code;
	private transient String  plan_s_date;

	// State flags to use as search criteria
	private transient boolean preparedflag;
	private transient boolean workreqflag;
	private transient boolean releasedflag;
	private transient boolean underprepflag;
	private transient boolean observedflag;
	private transient boolean faultrepflag;

	//private transient String   dummy;
	private transient boolean autosearchflag;	 // If the portlet should be automaticly populated
	private transient boolean infotextflag;	 // Hide the information text
	private transient boolean dispsearchflag;	 // Display search criteria
	private transient String  name;				 // The portlet title

	//==========================================================================
	//  Construction
	//==========================================================================

	public DelayedStartedWorkOrdersSM( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": DelayedStartedWorkOrdersSM.<init> :"+portal+","+clspath);
	}

	//==========================================================================
	//  Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": DelayedStartedWorkOrdersSM.preDefine()");

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();


		addField(blk, "CONTRACT").setHidden();

		addField(blk, "MCH_CODE_CONTRACT").setHidden();

		addField(blk, "COMPANY").setHidden();

		addField(blk, "ORG_CODE").setHidden();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO").setLabel("DSWOSMCWO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO").setLabel("DSWOSMCWO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)

		addField(blk, "ERR_DESCR").setSize(20).setLabel("DSWOSMCD: Directive");

		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("DSWOSMCOJ: Object ID");

		addField(blk,"CUSTOMER_ID").setDbName("CUSTOMER_NO").setHidden();

		addField(blk, "CUSTOMER_NAME").setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setSize(20).setLabel("DSWOSMCC: Customer");

		addField(blk, "PLAN_S_DATE", "Date").setLabel("DSWOSMCPS: Plan Start");

		addField(blk, "PLAN_F_DATE", "Date").setLabel("DSWOSMCPC: Plan Completion");

		addField(blk, "STATE").setLabel("DSWOSMCS: Status");

		addField(blk, "OBJSTATE").setHidden();

		addField(blk, "WORK_TYPE_ID").setHidden();

		addField(blk, "GROUP_ID").setHidden();

		addField(blk, "AUTHORIZE_CODE").setHidden();

                addField(blk, "DUMMY","String").setFunction("''").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFSITEDATE", "Date");       
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "DEFCOMPANY");
		addField(defblk, "USERSIGN");

		blk.setView("ACTIVE_SEPARATE");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();


		appendJavaScript("function woleaderDelayedWorkOrdersCore(obj,id)",
						 "{",
						 "   customizeBox(id);",
						 "}\n");

		appendJavaScript("function coordinatorDelayedWorkOrdersCore(obj,id)",
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
		if (DEBUG) debug(this+": DelayedStartedWorkOrdersSM.init()");

		contract    = readProfileValue("CONTRACT");   
		org_code    = readProfileValue("ORG_CODE");
		work_leader = readProfileValue("WORK_LEADER_SIGN_ID");
		work_type   = readProfileValue("WORK_TYPE_ID");
		group_id    = readProfileValue("GROUP_ID");
		customer_id = readProfileValue("CUSTOMER_ID");
		aut_code    = readProfileValue("AUTHORIZE_CODE");
		name        = readProfileValue("DELSTARTWONAME", name);
		plan_s_date = readProfileValue("PLAN_S_DATE2", "");
		company     = readProfileValue("COMPANY");   

		preparedflag  = readProfileFlag ( "PREPAREDFLAG", true );
		workreqflag   = readProfileFlag ( "WORKREQFLAG", true );
		releasedflag  = readProfileFlag ( "RELEASEDFLAG", true );
		underprepflag = readProfileFlag ( "UNDERPREPFLAG", true );
		observedflag  = readProfileFlag ( "OBSERVEDFLAG", true );
		faultrepflag  = readProfileFlag ( "FAULTREPFLAG", true );

		autosearchflag   = readProfileFlag ( "CUSTOMIZED", false ); 
		infotextflag     = readProfileFlag ( "INFOTEXT", false );   
		dispsearchflag   = readProfileFlag ( "DISPSEARCHFLAG", false );

		no_of_hits    = readProfileValue("NO_HITS", default_size+"");
		find   = ctx.readFlag("SEARCH_ORDERS",false);
		size          = Integer.parseInt(no_of_hits);

		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";

		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";

		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";

		if (underprepflag)
			underpreparation = "UNDERPREPARATION";
		else
			underpreparation = " ";

		if (workreqflag)
			workrequest = "WORKREQUEST";
		else
			workrequest	= " ";

		if (faultrepflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";


		if (Str.isEmpty(company))
		{
			company   = ctx.readValue("COMPANY");
		}

		if (Str.isEmpty(sign))
		{
			sign   = ctx.readValue("SIGN");
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
		ctx.writeValue("SKIP_ROWS",skip_rows+"");

	}

	protected void run()
	{

		if (Str.isEmpty(string_date))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Maintenance_Site_Utility_API.Get_Site_Date(null)", "DEFSITEDATE");
			deftrans.addCommand("SITEDATE", cmd);

			deftrans = mgr.perform(deftrans);        

			string_date = deftrans.getValue("SITEDATE/DATA/DEFSITEDATE");
		}

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

		if (Str.isEmpty(sign))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "USERSIGN");
			deftrans.addCommand("SIGN", cmd);

			deftrans = mgr.perform(deftrans);        

			sign = deftrans.getValue("SIGN/DATA/USERSIGN");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery qry   = trans.addQuery(blk);

		qry.setOrderByClause("WO_NO");
		qry.includeMeta("ALL");

		qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
		qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ?");        
                qry.addParameter(this,"OBJSTATE",workrequest);
                qry.addParameter(this,"OBJSTATE",faultreport);
                qry.addParameter(this,"OBJSTATE",observed);
                qry.addParameter(this,"OBJSTATE",prepared);
                qry.addParameter(this,"OBJSTATE",released);
                qry.addParameter(this,"OBJSTATE",underpreparation);

		if (!Str.isEmpty(contract))
                {
                    qry.addWhereCondition("CONTRACT = ?");
                    qry.addParameter(this,"CONTRACT",contract);
                }

		if (!Str.isEmpty(org_code))
                {
                    qry.addWhereCondition("ORG_CODE = ?");
                    qry.addParameter(this,"ORG_CODE",org_code);
                }

		if (!Str.isEmpty(work_type))
                {
                    qry.addWhereCondition("WORK_TYPE_ID = ?");
                    qry.addParameter(this,"WORK_TYPE_ID",work_type);
                }

		if (!Str.isEmpty(group_id))
                {
                    qry.addWhereCondition("GROUP_ID = ?");
                    qry.addParameter(this,"GROUP_ID",group_id);
                }

		if (!Str.isEmpty(customer_id))
                {
                    qry.addWhereCondition("CUSTOMER_NO like ?");
                    qry.addParameter(this,"CUSTOMER_ID",customer_id);
                }

		if (!Str.isEmpty(work_leader))
                {
                    qry.addWhereCondition("WORK_LEADER_SIGN = ?");
                    qry.addParameter(this,"DUMMY",work_leader);
                }

		if (!Str.isEmpty(aut_code))
                {
                    qry.addWhereCondition("AUTHORIZE_CODE = ?");
                    qry.addParameter(this,"AUTHORIZE_CODE",aut_code);
                }

		if (!Str.isEmpty(plan_s_date))
		{
			String temp_plan_s_date = Str.replace(plan_s_date,"+","");

			if (Str.toInt(temp_plan_s_date) > 0)
                        {
                            qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                            qry.addParameter(this,"DUMMY",temp_plan_s_date);
                        }
			else
                        {
                            qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                            qry.addParameter(this,"DUMMY",temp_plan_s_date);
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
		ctx.writeValue("SIGN", sign );
		ctx.writeFlag("SEARCH_ORDERS",find);
	}


	//==========================================================================
	//  Portlet description
	//==========================================================================
	public static String getDescription()
	{
		return "DELSTARTWOCTIT: Delayed Started Work Orders ";
	}

	public static String getDescription(ASPManager mgr)
	{
		return mgr.translate("DELSTARTWOCTIT: Delayed Started Work Orders ");
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": DelayedStartedWorkOrdersSM.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			ASPManager mgr = getASPManager();
			name = getDescription(mgr);
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("DELSTARTWOSMCQWO1: - You have " ) + db_rows +
			translate("DELSTARTWOSMCQWO2:  Work Orders.");
		}

		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("DELSTARTWOSMCCUST: Customize ") +	name;             
	}


	public void printContents()
	{
		// Information text, shown by default.
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("DELSTARTWOSMQINFO1: Work Orders with delayed start date for chosen "); 
			printScriptLink("DELSTARTWOSMCCUSTLINK1: Work Leader", "woleaderDelayedWorkOrdersCore");  
			printText("DELSTARTWOSMQINFO2:  and ");
			printScriptLink("DELSTARTWOSMCCUSTLINK2: Coordinator", "coordinatorDelayedWorkOrdersCore");  
			printText("DELSTARTWOSMQINFO3: . ");
			printNewLine();
			endFont();
		}

		// Search criteria, not shown by default.
		if (dispsearchflag)
		{
			setFontStyle(BOLD);
			printText("ACTIVEWOORDERSEARCHCRITERIATEXT: Search criteria: ");
			endFont();

			printText("DELSWOSMCCSCONTENTS: Site: ");
			if (!Str.isEmpty(contract))
				printText(contract);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCWTCONTENTS: Work Type: ");
			if (!Str.isEmpty(work_type))
				printText(work_type);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCEDCONTENTS: Maintenance Organization: ");
			if (!Str.isEmpty(org_code))
				printText(org_code);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCGICONTENTS: Object Group Id: ");
			if (!Str.isEmpty(group_id))
				printText(group_id);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCCUCONTENTS: Customer: ");
			if (!Str.isEmpty(customer_id))
				printText(customer_id);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCWLCONTENTS: Work Leader: ");
			if (!Str.isEmpty(work_leader))
				printText(work_leader);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCCCOOCONTENTS: Coordinator: ");
			if (!Str.isEmpty(aut_code))
				printText(aut_code);
			else
				printText("DELSWOSMALL: All");    
			printNewLine();

			printText("DELSWOSMCPLNSTRTCONTENTS: Planned Start Date +/-: ");
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
						printSubmitLink("DELSTARTWOSMCFIRST: First","firstCust");
					else
						printText("DELSTARTWOSMCFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("DELSTARTWOSMCPRV: Previous","prevCust");
					else
						printText("DELSTARTWOSMCPRV: Previous");

					printSpaces(5);
					String rows = translate("DELSTARTWOSMCROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("DELSTARTWOSMCLAST: Last","lastCust");
					else
						printText("DELSTARTWOSMCLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("DELSTARTWOSMCNEXT: Next","nextCust");
					else
						printText("DELSTARTWOSMCNEXT: Next");

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
	//  Customization section
	//==========================================================================

	public void runCustom()
	{
		if (DEBUG) debug(this+": DelayedStartedWorkOrdersSM.runCustom()");

		ASPManager mgr = getASPManager();
		trans = mgr.newASPTransactionBuffer();

		//SQLInjection_Safe AMNILK 20070716

                trans.addQuery("LLIST","EMPLOYEE_LOV", "EMPLOYEE_ID, EMPLOYEE_ID||' - '||NAME","COMPANY = ? ", "EMPLOYEE_ID").addParameter(this,"COMPANY",company);

		trans = mgr.perform(trans);
	}


	public void printCustomBody() throws FndException
	{
		if (DEBUG)
		{
			debug(this+": DelayedStartedWorkOrdersSM.printCustomBody()");
			debug("\tprintCustomBody(): DelayedStartedWorkOrdersSM");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELSWOQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("DELSWOSMCCS: Choose Site: ");
		printNewLine();
		printSpaces(5);
		printField("CONTRACTC",contract);
		printDynamicLOV("CONTRACTC","USER_ALLOWED_SITE_LOV","CUSTOMSITEFLD: List of Site");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELSWOSMCCWT: Choose Work Type: ");
		printNewLine();
		printSpaces(5);
		printField("WOTYPEC",work_type);
		printDynamicLOV("WOTYPEC","WORK_TYPE","CUSTOMWORKTYPEFLD: List of Work Type");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("DELSWOSMCCED: Choose Maintenance Organization:");
		printNewLine();
		printSpaces(5);
		printField("ORGCODEC",org_code);
		printDynamicLOV("ORGCODEC","ORG_CODE_ALLOWED_SITE_LOV","CUSTOMEXECDEPTFLD: List of Maintenance Organization","CONTRACT = '"+contract+"'");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("DELSWOSMCCGI: Choose Object Group Id:");
		printNewLine();
		printSpaces(5);
		printField("GROUPIDC",group_id);
		printDynamicLOV("GROUPIDC","EQUIPMENT_OBJ_GROUP","CUSTOMGRPIDFLD: List of Group ID");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("DELSWOSMCCCU: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERIDC", customer_id, 20);
		printDynamicLOV("CUSTOMERIDC","CUSTOMER_INFO","CUSTOMCUSTOMFLD: List of Customer");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("DELSWOSMCCWL: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC", work_leader);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELSWOSMCCCOO: Choose Coordinator:");
		printNewLine();
		printSpaces(5);
		printField("COORDINATORC",aut_code);
		printDynamicLOV("COORDINATORC","ORDER_COORDINATOR_LOV","CUSTOMCOORDFLD: List of Coordinator");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELSWOSMCPLANNEDSTARTDATE: Planned Start Date +/- ");
		printField("PLAN_S_DATE2", plan_s_date, 5, 5);
		printNewLine();
		printNewLine();

		printNewLine();
		printSpaces(5);
		printText("DELSTWOSMCSTATUSTEXT: Choose status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMOBSERVED", observedflag);
		printSpaces(3);
		printText("DELSTWOSMCOBSERVEDTEXT: Observed");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMPREPARED", preparedflag);
		printSpaces(3);
		printText("DELSTWOSMCPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("DELSTWOSMRELEASED", releasedflag);
		printSpaces(3);
		printText("DELSTWOSMCRELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("DELSTWOSMUNDERPREP", underprepflag);
		printSpaces(3);
		printText("DELSTWOSMCUNDERPREPTEXT: Under Preparation");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMWORKREQ", workreqflag);
		printSpaces(3);
		printText("DELSTWOSMCWORKREQTEXT: Work Request");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMFLTREP", faultrepflag);
		printSpaces(3);
		printText("DELSTWOSMCFLTREPTEXT: Fault Report");
		printNewLine();
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELSTWOSMCRPORTLETCONFIG: Portlet Configuration");
		endFont();

		printNewLine();
		printSpaces(5);
		printText("DELSTARTWOSMCPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("DELSTARTWONAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DESTWOSMWORKDISPSMSEARCHFLAG", dispsearchflag);
		printSpaces(3);
		printText("DESTWOSMDISPSMSEARCHFLAGTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("DESTWOSMINFOTEXTFLAG", infotextflag);
		printSpaces(3);
		printText("DESTWOSMTEXTINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("DESTWOSMCUSTOMIZEDFLAG", autosearchflag);
		printSpaces(3);
		printText("DESTWOSMTEXTCUSTOMIZED: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("DELSTARTWOSMCLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}


	public void submitCustomization()
	{

		if (DEBUG)
		{
			debug(this+": DelayedStartedWorkOrdersSM.submitCustomization()");
			debug("\tsubmitCustomization(): current values:\n\t\tcontract="+contract);
		}
		observedflag = "TRUE".equals(readValue("DELSTWOSMOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";
		writeProfileFlag("OBSERVEDFLAG", observedflag);

		preparedflag = "TRUE".equals(readValue("DELSTWOSMPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";
		writeProfileFlag("PREPAREDFLAG", preparedflag);

		releasedflag = "TRUE".equals(readValue("DELSTWOSMRELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";
		writeProfileFlag("RELEASEDFLAG", releasedflag);

		underprepflag = "TRUE".equals(readValue("DELSTWOSMUNDERPREP"));
		if (underprepflag)
			underpreparation = "UNDERPREPARATION";
		else
			underpreparation = " ";
		writeProfileFlag("UNDERPREPFLAG", underprepflag);

		workreqflag = "TRUE".equals(readValue("DELSTWOSMWORKREQ"));
		if (workreqflag)
			workrequest = "WORKREQUEST";
		else
			workrequest	= " ";
		writeProfileFlag("WORKREQFLAG", workreqflag);


		faultrepflag = "TRUE".equals(readValue("DELSTWOSMFLTREP"));
		if (faultrepflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";
		writeProfileFlag("FAULTREPFLAG", faultrepflag);

		contract    = readValue("CONTRACTC");
		org_code    = readValue("ORGCODEC");
		work_leader = readValue("WOLEADERC");
		aut_code    = readValue("COORDINATORC");        
		work_type   = readValue("WOTYPEC");
		group_id    = readValue("GROUPIDC");
		customer_id = readValue("CUSTOMERIDC");
		name        = readValue("DELSTARTWONAME");
		plan_s_date = readValue("PLAN_S_DATE2");


		writeProfileValue("CONTRACT", readAbsoluteValue("CONTRACTC") );
		writeProfileValue("COMPANY",  readAbsoluteValue("COMPANY") );
		writeProfileValue("ORG_CODE",  readAbsoluteValue("ORGCODEC") );
		writeProfileValue("WORK_LEADER_SIGN_ID",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("WORK_TYPE_ID",  readAbsoluteValue("WOTYPEC") );
		writeProfileValue("GROUP_ID",  readAbsoluteValue("GROUPIDC") );
		writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERIDC") );
		writeProfileValue("AUTHORIZE_CODE",  readAbsoluteValue("COORDINATORC") );
		writeProfileValue("DELSTARTWONAME",  readAbsoluteValue("DELSTARTWONAME") );
		writeProfileValue("PLAN_S_DATE2", readAbsoluteValue("PLAN_S_DATE2") );

		dispsearchflag = "TRUE".equals(readValue("DESTWOSMWORKDISPSMSEARCHFLAG")); 
		writeProfileFlag("DISPSEARCHFLAG", dispsearchflag);

		infotextflag = "TRUE".equals(readValue("DESTWOSMINFOTEXTFLAG")); 
		writeProfileFlag("INFOTEXT", infotextflag);

		autosearchflag = "TRUE".equals(readValue("DESTWOSMCUSTOMIZEDFLAG")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);

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