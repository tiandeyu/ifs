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
 * File        : MyDelayedStartedWorkOrdersSM.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * PJONSE  000605 Created.
 * PJONSE  000704 Call Id: 45169. Added 'if (dummy != 2)','if dummy == 1' and 'if dummy == 2' in protected void run().
 * PJONSE  000712 Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831 Changed translation constant DELSTARTWORQWO to DELSTARTWORQWO1 and DELSTARTWORQWO2. 
 * ARWILK  001101 Removed the select boxes (In customize page) and added dynamic LOVs.
 * JEWILK  001115 Done Specified Functional Corrections(Doc.no 1006241)
 * JEWILK  001212 Changed the hyperlink of field 'WO_NO' to 'ActiveSeparate2lightSM.asp'
 * JIJOSE  001215 Search functionality
 * JIJOSE  001218 Distinct selection in query.
 * NUPELK  001227 Added an LOV for Work Leader in the customization page 
 * ARWILK  031015 (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031023 (Bug#108160) Modified "planned start date" to "Number of past days to include" and 
 *                modified the query to cater the required functionality.
 * VAGULK  040310 Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables,
 *                and Changed field name "CUSTOMER_NO " to "CUSTOMER_ID".
 * ARWILK  041025 Merged LCS-47169 and did some other corrections.
 * NIJALK  050308 Modified run().
 *  ---------------------------------
 * NAMELK  060901  Merged Bug 58216, Eliminated SQL Injection security vulnerability.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * ILSOLK  070730  Eliminated LocalizationErrors. 
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


/**
 */
public class MyDelayedStartedWorkOrdersSM extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.MyDelayedStartedWorkOrdersSM");


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

	private transient String  company;
	private transient String  name;
	private transient String  work_leader;
	private transient String  customer_id;
	private transient String  plan_s_date;
	private transient String  string_date;
	private transient String  sign;
	private transient String  aut_code;
	private transient String  role;

	private transient boolean preparedflag;
	private transient boolean workreqflag;
	private transient boolean releasedflag;
	private transient boolean underprepflag;
	private transient boolean observedflag;

	private transient String  prepared;
	private transient String  underpreparation;
	private transient String  released;
	private transient String  workrequest;
	private transient String  observed;

	private transient String  dummy;

	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;

	private transient boolean disp_search;
	private transient boolean hide_info_text;
	private transient boolean search_auto;

	private transient ASPTransactionBuffer  trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	//==========================================================================
	//  Construction
	//==========================================================================

	public MyDelayedStartedWorkOrdersSM( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.<init> :"+portal+","+clspath);
	}


	public ASPPage construct() throws FndException
	{
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.construct()");
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.doFreeze()");
		super.doFreeze();
	}

	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	//  
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.preDefine()");

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();

		// Note, this column is used in DISTINCT query and must be placed first.
		addField(blk, "OBJID" ).setHidden();

		addField(blk, "CONTRACT").setHidden();

		addField(blk, "COMPANY").setHidden();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO","NEWWIN").setLabel("DSWOSMRWO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO","NEWWIN").setLabel("DSWOSMRWO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)

		addField(blk, "ERR_DESCR").setSize(20).setLabel("DSWOSMRD: Directive");

		addField(blk, "MCH_CODE").setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,CONTRACT","NEWWIN").setLabel("DSWOSMROJ: Object ID");

		addField(blk,"CUSTOMER_ID").setDbName("CUSTOMER_NO").setHidden();

		addField(blk, "CUSTOMER_NAME").setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setSize(20).setLabel("DSWOSRMC: Customer");

		addField(blk, "PLAN_S_DATE", "Date").setLabel("DSWOSMRPS: Plan Start");

		addField(blk, "PLAN_F_DATE", "Date").setLabel("DSWOSMRPC: Plan Completion");

		addField(blk, "STATE").setLabel("DSWOSMRS: Status");

		addField(blk, "OBJSTATE").setHidden();

		addField(blk, "GROUP_ID").setHidden();

		addField(blk, "SIGN").setHidden();

                addField(blk, "PLAN_DAYS", "Number").setFunction("''").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFSITEDATE", "Date");       
		addField(defblk, "DEFCOMPANY");
		addField(defblk, "USERSIGN");
		addField(defblk, "DEFORGCODE");
		addField(defblk, "DEFPLANSDATE");

		blk.setView("WORK_ORDER_ROLE_ACTIVEWO");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();

		dummy = "1";

		appendJavaScript("function customizeMyDelayedStartedWorkOrdersSM(obj,id)",
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
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());    

		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.init()");

		disp_search = readProfileFlag("MYDELWOSMDISPSEARCH", false);
		hide_info_text = readProfileFlag("MYDELWOSMHIDEINFOTEXT", false);
		search_auto = readProfileFlag("MYDELWOSMSEARCHAUTO", false);

		name        = readProfileValue("DELSTARTWORNAME", name);
		company     = readProfileValue("COMPANY");   
		work_leader = readProfileValue("WORK_LEADER_SIGN_ID");
		customer_id = readProfileValue("CUSTOMER_ID");
		aut_code    = readProfileValue("AUTHORIZE_CODE");
		role        = readProfileValue("ROLE_CODE");
		plan_s_date = readProfileValue("PLAN_S_DATE2", "");

		preparedflag  = readProfileFlag ( "PREPAREDFLAG", true );
		workreqflag   = readProfileFlag ( "WORKREQFLAG", true );
		releasedflag  = readProfileFlag ( "RELEASEDFLAG", true );
		underprepflag = readProfileFlag ( "UNDERPREPFLAG", true );
		observedflag  = readProfileFlag ( "OBSERVEDFLAG", true );

		no_of_hits    = readProfileValue("NO_HITS", default_size+"");
		size          = Integer.parseInt(no_of_hits);

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (Str.isEmpty(company))
		{
			company   = ctx.readValue("COMPANY");
		}

		if (Str.isEmpty(sign))
		{
			sign   = ctx.readValue("SIGN");
		}


		if (!Str.isEmpty(work_leader))
			writeProfileValue("WORK_LEADER_SIGN_ID", work_leader);

		if (!Str.isEmpty(customer_id))
			writeProfileValue("CUSTOMER_ID", customer_id);

		if (!Str.isEmpty(aut_code))
			writeProfileValue("AUTHORIZE_CODE", aut_code);

		if (!Str.isEmpty(role))
			writeProfileValue("ROLE_CODE", role);

		if (!Str.isEmpty(plan_s_date))
			writeProfileValue("PLAN_S_DATE2", plan_s_date);

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

		if (Str.isEmpty(dummy))
		{
			dummy = "1";
		}
		else if (Str.isEmpty(string_date))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Maintenance_Site_Utility_API.Get_Site_Date(null)", "DEFSITEDATE");
			deftrans.addCommand("SITEDATE", cmd);

			deftrans = perform(deftrans);        

			string_date = deftrans.getValue("SITEDATE/DATA/DEFSITEDATE");
		}
		else if (Str.isEmpty(company))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			//cmd.defineCustomFunction(this, "User_Access_API.Get_User_Current_Company_Id", "DEFCOMPANY");

			cmd.defineCustomFunction(this,"User_Default_API.Get_Contract","CONTRACT");
			deftrans.addCommand("CONTR", cmd);

			cmd = mgr.newASPCommand();
			cmd.defineCustomFunction(this,"Site_API.Get_Company","DEFCOMPANY");
			cmd.addReference(this,"CONTRACT","CONTR/DATA","CONTRACT");
			deftrans.addCommand("COMP", cmd);

			deftrans = perform(deftrans);        

			company = deftrans.getValue("COMP/DATA/DEFCOMPANY");

			writeProfileValue("COMPANY",company);
		}
		
                if (Str.isEmpty(sign))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "USERSIGN");
			deftrans.addCommand("SIGN", cmd);

			deftrans = perform(deftrans);        

			sign = deftrans.getValue("SIGN/DATA/USERSIGN");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery qry = trans.addQuery(blk);

		if (search_auto || find)
		{
			qry.setSelectExpression( "OBJID", "DISTINCT OBJID" );
			qry.setOrderByClause("WO_NO");
			qry.includeMeta("ALL");

			if ("1".equals(dummy))
			{
				qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
				qry.addWhereCondition("PLAN_S_DATE < Maintenance_Site_Utility_API.Get_Site_Date(null)");
				qry.addWhereCondition("OBJSTATE in ('WORKREQUEST','OBSERVED','PREPARED','RELEASED','UNDERPREPARATION')");
				qry.addWhereCondition("SIGN = ?");
                                qry.addParameter(this, "SIGN", sign);
				if (!Str.isEmpty(customer_id))
                                {
                                    qry.addWhereCondition("CUSTOMER_NO like ?");
                                    qry.addParameter(this,"CUSTOMER_ID", customer_id);
                                }
			}
			else
			{
				if ("2".equals(dummy))
				{
					qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
					qry.addWhereCondition("PLAN_S_DATE < Maintenance_Site_Utility_API.Get_Site_Date(null)");
					qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ?");
                                        qry.addParameter(this,"OBJSTATE", workrequest);
                                        qry.addParameter(this,"OBJSTATE", observed);
                                        qry.addParameter(this,"OBJSTATE", prepared);
                                        qry.addParameter(this,"OBJSTATE", released);
                                        qry.addParameter(this,"OBJSTATE", underpreparation);
                                        qry.addWhereCondition("SIGN = ?");
                                        qry.addParameter(this,"SIGN", sign);
				}

				if (!Str.isEmpty(customer_id))
                                {
                                    qry.addWhereCondition("CUSTOMER_NO like ?");
                                    qry.addParameter(this,"CUSTOMER_ID", customer_id);
                                }
                                if (!Str.isEmpty(work_leader))
                                {
                                    qry.addWhereCondition("WORK_LEADER_SIGN_ID = ?");
                                    qry.addParameter(this,"WORK_LEADER_SIGN_ID", work_leader);
                                }
				if (!Str.isEmpty(role))
                                {
                                    qry.addWhereCondition("ROLE_CODE = ?");
                                    qry.addParameter(this,"ROLE_CODE", role);
                                }
				if (!Str.isEmpty(aut_code))
                                {
                                    qry.addWhereCondition("AUTHORIZE_CODE = ?");
                                    qry.addParameter(this,"AUTHORIZE_CODE", aut_code);
                                }
				if (!Str.isEmpty(plan_s_date))
				{
					/* if (Str.toInt(plan_s_date) > 0)
				 {
					 qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + '"+plan_s_date+"'");
				 }
				 else
				 {
					 qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + '"+plan_s_date+"' AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
				 }*/

					qry.addWhereCondition("trunc(PLAN_S_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) - ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                                        qry.addParameter(this,"PLAN_DAYS", plan_s_date);
				}
			}
			qry.setBufferSize(size);
			qry.skipRows(skip_rows);

			submit(trans);

			db_rows = blk.getASPRowSet().countDbRows();
			getASPContext().writeValue("DB_ROWS", db_rows+"" );

			ctx.writeValue("COMPANY", company );
			ctx.writeValue("SIGN", sign );
			ctx.writeFlag("SEARCH_ORDERS",find);
		}
	}

	//==========================================================================
	//  
	//==========================================================================

	public static String getDescription()
	{
		return "DELSTARTWORTIT: My Delayed Started Work Orders ";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": MyDelayedStartedWorkOrdersSM.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("DELSTARTWORQWO1: - You have " ) + db_rows +
			translate("DELSTARTWORQWO2:  Work Orders.");
		}

		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("DELSTARTWORCUST: Customize ")	+ name;           
	}


	public void printContents()
	{
		printNewLine();

		if (disp_search)
		{
			printText("MYDELWOSMSEARCH: Search Criteria:");  
			printNewLine();
			printText("MYDELWOSMCUST: Customer:");
			printSpaces(2);
			if (!Str.isEmpty(customer_id))
				printText(customer_id);
			else
				printText("MYDELWOSMALL: All");   
			printNewLine();

			printText("MYDELWOSMWRKLDR: Work Leader:");
			printSpaces(2);
			if (!Str.isEmpty(work_leader))
				printText(work_leader);
			else
				printText("MYJOBSMALL: All"); 
			printNewLine();

			printText("MYDELWOSMCOORD: Coordinator:");
			printSpaces(2);
			if (!Str.isEmpty(aut_code))
				printText(aut_code);
			else
				printText("MYJOBSMALL: All"); 
			printNewLine();

			printText("MYDELWOSMROLE: Role:");
			printSpaces(2);
			if (!Str.isEmpty(role))
				printText(role);
			else
				printText("MYJOBSMALL: All"); 
			printNewLine();

			printText("MYDELWOSMSTRT: Number of past days to include :");
			if (!Str.isEmpty(plan_s_date))
				printText(plan_s_date);

			printNewLine();
			printNewLine();

		}

		if (!hide_info_text)
		{
			setFontStyle(ITALIC);
			printText("DELSTARTWORINFO1: Work Orders with delayed start date compared to "); 
			printText("DELSTARTWORINFO2: current date where logged on user is planned craft."); 
			printText("DELSTARTWORINFO3:  To see or alter actual conditions you can go to the ");
			printScriptLink("DELSTARTWORCUSTLINK: customize", "customizeMyDelayedStartedWorkOrdersSM");  
			printText("DELSTARTWORINFO4:  page.");
			printNewLine();
			endFont();
		}

		printHiddenField("CMD","");

		int    nRows;
		nRows = rowset.countRows();

		if (search_auto || find)
		{
			if (nRows > 0)
			{
				printTable(tbl);

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("DELSTARTWORFIRST: First","firstCust");
					else
						printText("DELSTARTWORFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("DELSTARTWORPRV: Previous","prevCust");
					else
						printText("DELSTARTWORPRV: Previous");

					printSpaces(5);
					String rows = translate("DELSTARTWORROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("DELSTARTWORLAST: Last","lastCust");
					else
						printText("DELSTARTWORLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("DELSTARTWORNEXT: Next","nextCust");
					else
						printText("DELSTARTWORNEXT: Next");

					printNewLine();
					printNewLine();

				}
			}
			else
			{
				if (!search_auto)
				{
					printSubmitLink("FINDMYDELWOSM: Search","findCust");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("DELSTARTWORNODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("FINDMYDELWOSM: Search","findCust");
			printNewLine();
			printNewLine();
		}
	}

	//==========================================================================
	//  
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
			debug(this+": MyDelayedStartedWorkOrdersSM.runCustom()");
		}

		ASPQuery   qry = trans.addQuery("LLIST","EMPLOYEE_LOV", "EMPLOYEE_ID, EMPLOYEE_ID||' - '||NAME","COMPANY = ? ", "EMPLOYEE_ID");
                qry.addParameter(this,"COMPANY", company);

		trans = perform(trans);
	}


	public void printCustomBody() throws FndException
	{
		if (DEBUG)
		{
			debug(this+": MyDelayedStartedWorkOrdersSM.printCustomBody()");
			debug("\tprintCustomBody(): MyDelayedStartedWorkOrdersSM");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELSWORQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);     
		printText("DELSWORCCU: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERIDC",customer_id);
		printDynamicLOV("CUSTOMERIDC","CUSTOMER_INFO","PCMWMYDELCUST: List of Customer");
		printNewLine();
		printNewLine();

		printSpaces(5);     
		printText("DELSWORCWL: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC", work_leader);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DELSWORCCOO: Choose Coordinator:");
		printNewLine();
		printSpaces(5);
		printField("COORDINATORC",aut_code);
		printDynamicLOV("COORDINATORC","ORDER_COORDINATOR_LOV","PCMWMYDELCOORD: List of Coordinator");
		printNewLine();
		printNewLine();

		printSpaces(5);     
		printText("DELSWORCR: Choose Role:");
		printNewLine();
		printSpaces(5);
		printField("ROLEC",role);
		printDynamicLOV("ROLEC","ROLE","PCMWMYDELROLE: List of Role");
		printNewLine();           
		printNewLine();

		printSpaces(5);
		printText("DELSTWOSMRPLANNEDSTARTDATE: Number of past days to include :");
		printField("PLAN_S_DATE2", plan_s_date, 5, 5);
		printNewLine();
		printNewLine();

		printNewLine();
		printSpaces(5);
		printText("DELSTWOSMRSTATUSTEXT: Choose status:");
		printNewLine();       
		printSpaces(5);
		printCheckBox("DELSTWOSMOBSERVED", observedflag);
		printSpaces(3);
		printText("DELSTWOSMROBSERVEDTEXT: Observed");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMPREPARED", preparedflag);
		printSpaces(3);
		printText("DELSTWOSMRPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("DELSTWOSMRELEASED", releasedflag);
		printSpaces(3);
		printText("DELSTWOSMRELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("DELSTWOSMUNDERPREP", underprepflag);
		printSpaces(3);
		printText("DELSTWOSMRUNDERPREPTEXT: Under Preparation");
		printNewLine();

		printSpaces(5);
		printCheckBox("DELSTWOSMWORKREQ", workreqflag);
		printSpaces(3);
		printText("DELSTWOSMRWORKREQTEXT: Work Request");
		printNewLine();
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("DELSTWOSMRPORTLETCONFIG: Portlet Configuration");
		endFont();

		//printNewLine();
		printSpaces(5);
		printText("DELSTARTWORPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("DELSTARTWORNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("MYDELWOSMDISPSEARCH", disp_search);
		printSpaces(3);
		printText("DISSEACRI: Display search criteria");
		printNewLine();
		printNewLine();   

		printSpaces(5);
		printCheckBox("MYDELWOSMHIDEINFOTEXT", hide_info_text);
		printSpaces(3);
		printText("PCMWPORTMYDELAYSTARTWOSMHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("MYDELWOSMSEARCHAUTO", search_auto);
		printSpaces(3);
		printText("PCMWPORTMYDELAYSTARTWOSMSEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("DELSTARTWORLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();



	}


	public void submitCustomization()
	{

		dummy = "2";

		if (DEBUG)
		{
			debug(this+": MyDelayedStartedWorkOrdersSM.submitCustomization()");
			debug("\tsubmitCustomization(): MyDelayedStartedWorkOrdersSM");
		}

		disp_search = "TRUE".equalsIgnoreCase(readValue("MYDELWOSMDISPSEARCH"));
		hide_info_text = "TRUE".equalsIgnoreCase(readValue("MYDELWOSMHIDEINFOTEXT"));
		search_auto = "TRUE".equalsIgnoreCase(readValue("MYDELWOSMSEARCHAUTO"));

		writeProfileFlag("MYDELWOSMDISPSEARCH", disp_search);
		writeProfileFlag("MYDELWOSMHIDEINFOTEXT", hide_info_text);
		writeProfileFlag("MYDELWOSMSEARCHAUTO", search_auto);

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

		customer_id = readValue("CUSTOMERIDC");
		work_leader = readValue("WOLEADERC");
		aut_code    = readValue("CUSTOMERIDC");        
		role        = readValue("ROLEC");
		name        = readValue("DELSTARTWORNAME");
		plan_s_date = readValue("PLAN_S_DATE2");

		writeProfileValue("COMPANY",  readAbsoluteValue("COMPANY") );
		writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERIDC") );
		writeProfileValue("AUTHORIZE_CODE",  readAbsoluteValue("CUSTOMERIDC") );
		writeProfileValue("WORK_LEADER_SIGN_ID",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("ROLE_CODE", readAbsoluteValue("ROLEC") );
		writeProfileValue("DELSTARTWORNAME",  readAbsoluteValue("DELSTARTWORNAME") );
		writeProfileValue("PLAN_S_DATE2", readAbsoluteValue("PLAN_S_DATE2") );

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}

	}

}