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
 * File        : NotInvoicedWO.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Created     : PJONSE  000529  Created.
 * Modified    :
 * PJONSE  000704  Call Id: 45169. Added 'if (dummy != 2)' in protected void run().
 * PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831  Changed translation constant NINVWOQWO to NINVWOQWO1 and NINVWOQWO2. 
 * MAGNSE  001020  Changed portlet according to new development guidelines.
 * ARWILK  001102  Removed the select boxes (In customize page) and added dynamic LOVs.
 * JIJOSE  001215  Serach functionality changed
 * NUPELK  001227  Added an LOV for Work Leader in the customization page 
 * NUPELK  010104  Changed the view used to retrieve work leaders to EMPLOYEE_LOV
 * JEWILK  010104  Modified to display labels and values in different printText() methods (in function printContents).
 * INROLK  021217  Added MCH_CODE_CONTRACT as In Generic Work Order.
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * VAGULK  040311  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables,
 *                 and Changed field name "CUSTOMER_NO " to "CUSTOMER_ID".
 * ARWILK  041025  Merged LCS-47169 and did some other corrections.
 * ----------------------------
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELK  060901  merged bug 58216.
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


public class NotInvoicedWO extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.NotInvoicedWO");


	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPBlock   defblk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	private String default_name; //Used as name for portlet when fetched from pool if name not saved in profile.

	// Decoded state values initialized on creation.
	private String dec_workrequest;
	private String dec_faultreport;
	private String dec_observed;
	private String dec_underpreparation;
	private String dec_prepared;
	private String dec_released;
	private String dec_started;
	private String dec_workdone;
	private String dec_reported;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	// State variables used in where statements holds OBJSTATE value.
	private transient String  workrequest;    
	private transient String  faultreport;
	private transient String  observed;
	private transient String  underpreparation;
	private transient String  prepared;
	private transient String  released;
	private transient String  started;
	private transient String  workdone;
	private transient String  reported;

	private String[] stateArray;
	private boolean[] stateFlagArray;

	private transient int     size;	// Numeric representation of variable no_of_hits. Control how many rows to be shown in portlet. 
	private transient int     skip_rows; //Rows to skip in ASPQuery.
	private transient int     db_rows; //Total number of rows in Rowset after the submitted transaction.
	private transient boolean find;	// Control submitlink Search.

	private transient ASPTransactionBuffer  trans; 
	// private transient ASPQuery  qry; 

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  contract;
	private transient String  company;
	private transient String  object_id;
	private transient String  customer_id;
	private transient String  wo_leader;
	private transient String  aut_code;

	// Customization flags to control which objstates to be used in where statements for each user.
	private transient boolean workreqflag;
	private transient boolean faultrepflag;
	private transient boolean observedflag;
	private transient boolean underprepflag;
	private transient boolean preparedflag;
	private transient boolean releasedflag;
	private transient boolean startedflag;
	private transient boolean workdoneflag;
	private transient boolean reportedflag;

	// Control if any values should be fetched to the portlet for each user.
	private transient boolean autosearchflag;
	private transient boolean dispcriteriaflag;
	private transient boolean hideinfoflag;

	// Choosen name by a user or default name on creation.
	private transient String  name;

	// Control how many rows to be shown in portlet.
	private transient String  no_of_hits;


	//==========================================================================
	//  Construction
	//==========================================================================

	public NotInvoicedWO( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": NotInvoicedWO.<init> :"+portal+","+clspath);
	}


	public ASPPage construct() throws FndException
	{
		if (DEBUG) debug(this+": NotInvoicedWO.construct()");
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	/**
	 * Copy the current value of every mutable attribute to
	 * its default value (pre-variable).
	 * Not necessary if you do not have any mutable attributes
	 * (attributes that obtain their default values during portlet definition,
	 * e.g. in preDefine(), and can be changed during execution).
	 */
	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": NotInvoicedWO.doFreeze()");
		default_name = name; // ^^MAGN^^ Save immutable variable default_name after definition.
		super.doFreeze();
	}

	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": NotInvoicedWO.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	//  
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": NotInvoicedWO.preDefine()");

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();


		addField(blk, "CONTRACT").setHidden();
		addField(blk, "MCH_CODE_CONTRACT").setHidden();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO").setLabel("NINVWOWO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO").setLabel("NINVWOWO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)

		addField(blk, "ERR_DESCR").setSize(20).setLabel("NINVWOD: Directive");

		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("NINVWOOBI: Object ID");

		addField(blk,"CUSTOMER_ID").setDbName("CUSTOMER_NO").setHidden();

		addField(blk, "CUSTOMER_NAME").setSize(20).setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setLabel("NINVWOC: Customer");

		addField(blk, "PLAN_F_DATE", "Date").setLabel("NINVWOPS: Plan Completion");

		addField(blk, "STATE").setLabel("NINVWOS: Status");

		addField(blk, "AUTHORIZE_CODE").setHidden();

		addField(blk, "WORK_LEADER_SIGN_ID").setHidden();

                addField(blk, "DUMMY","String").setFunction("''").setHidden();

		blk.setView("ACTIVE_SEPARATE_CODING");

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "DEFCOMPANY");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();


		rowset = blk.getASPRowSet();

		name = translate(getDescription());	 // ^^MAGN^^ Use default name on creation of portlet.

		autosearchflag = false;
		hideinfoflag = false;
		dispcriteriaflag = false;

		// Fetch decoded work order states.
		defineDecodedStates();

		appendJavaScript("function customizeNotInvoicedWO(obj,id)",
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


// Call to init() when necessery.   init();	

	}


	protected void init()
	{

		stateFlagArray = new boolean[9];
		stateArray = new String[9];

		if (DEBUG) debug(this+": NotInvoicedWO.init()");

		contract    = readProfileValue("CONTRACT");   
		company     = readProfileValue("COMPANY");   
		object_id   = readProfileValue("MCH_CODE");
		aut_code    = readProfileValue("AUTHORIZE_CODE");
		wo_leader   = readProfileValue("WORK_LEADER_SIGN_ID");
		customer_id = readProfileValue("CUSTOMER_ID");
		name        = readProfileValue("NINVWO", default_name);

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		workreqflag   = readProfileFlag ( "WORKREQFLAG", true );
		if (workreqflag) workrequest = "WORKREQUEST";
		stateFlagArray[0] = workreqflag;
		stateArray[0] = dec_workrequest;

		faultrepflag  = readProfileFlag ( "FAULTREPFLAG", true );
		if (faultrepflag) faultreport = "FAULTREPORT";
		stateFlagArray[1] = faultrepflag;
		stateArray[1] = dec_faultreport;

		observedflag  = readProfileFlag ( "OBSERVEDFLAG", true );
		if (observedflag) observed = "OBSERVED";
		stateFlagArray[2] = observedflag;
		stateArray[2] = dec_observed;

		underprepflag = readProfileFlag ( "UNDERPREPFLAG", true );
		if (underprepflag) underpreparation = "UNDERPREPARATION";
		stateFlagArray[3] = underprepflag;
		stateArray[3] = dec_underpreparation;

		preparedflag  = readProfileFlag ( "PREPAREDFLAG", true );
		if (preparedflag) prepared = "PREPARED";
		stateFlagArray[4] = preparedflag;
		stateArray[4] = dec_prepared;

		releasedflag  = readProfileFlag ( "RELEASEDFLAG", true );
		if (releasedflag) released = "RELEASED";
		stateFlagArray[5] = releasedflag;
		stateArray[5] = dec_released;

		startedflag   = readProfileFlag ( "STARTEDFLAG", true );
		if (startedflag) started = "STARTED";
		stateFlagArray[6] = startedflag;
		stateArray[6] = dec_started;

		workdoneflag  = readProfileFlag ( "WORKDONEFLAG", true );
		if (workdoneflag) workdone = "WORKDONE";
		stateFlagArray[7] = workdoneflag;
		stateArray[7] = dec_workdone;

		reportedflag  = readProfileFlag ( "REPORTEDFLAG", true );
		if (reportedflag) reported = "REPORTED";
		stateFlagArray[8] = reportedflag;
		stateArray[8] = dec_reported;

		autosearchflag = readProfileFlag ( "AUTOSEARCHFLAG", false );
		hideinfoflag = readProfileFlag ( "HIDEINFOFLAG", false );
		dispcriteriaflag = readProfileFlag ( "DISPSEARCHFLAG", false );

		no_of_hits    = readProfileValue("NO_HITS", default_size+"");
		size          = Integer.parseInt(no_of_hits);


		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		String showrows = readValue("CMD");

		if ("FIND".equals(showrows))
		{
			skip_rows = 0;
			find = true;
		}
		else if ("FIRST".equals(showrows))
			skip_rows = 0;
		else if ("PREV".equals(showrows))
			if (skip_rows>=size)
				skip_rows -= size;
			else
				skip_rows = 0;
		else if ("NEXT".equals(showrows) && skip_rows<=db_rows-size)
			skip_rows += size;
		else if ("LAST".equals(showrows))
			skip_rows = db_rows - size;

		ctx.writeValue("SKIP_ROWS",skip_rows+"");

	}


	protected void run()
	{

		trans = getASPManager().newASPTransactionBuffer();
		ASPQuery qry   = trans.addQuery(blk);


                if (!Str.isEmpty(contract))
                {
                    qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = ?)");
                    qry.addParameter(this,"CONTRACT",contract);
                }
                else
			qry.addWhereCondition("CONTRACT = User_Default_API.Get_Contract");

		if (!Str.isEmpty(object_id))
                {
                    qry.addWhereCondition("MCH_CODE like ?");
                    qry.addParameter(this,"MCH_CODE",object_id);
                }

		if (!Str.isEmpty(aut_code))
                {
                    qry.addWhereCondition("AUTHORIZE_CODE = ?");
                    qry.addParameter(this,"AUTHORIZE_CODE",aut_code);
                }

		if (!Str.isEmpty(customer_id))
                {
                    qry.addWhereCondition("CUSTOMER_NO like ?");
                    qry.addParameter(this,"CUSTOMER_ID",customer_id);
                }

		if (!Str.isEmpty(wo_leader))
                {
                    qry.addWhereCondition("WORK_LEADER_SIGN = ?");
                    qry.addParameter(this,"WORK_LEADER_SIGN_ID",wo_leader);
                }

		qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ?");
                qry.addParameter(this,"DUMMY",workrequest);
                qry.addParameter(this,"DUMMY",observed);
                qry.addParameter(this,"DUMMY",prepared);
                qry.addParameter(this,"DUMMY",released);
                qry.addParameter(this,"DUMMY",underpreparation);
                qry.addParameter(this,"DUMMY",faultreport);
                qry.addParameter(this,"DUMMY",started);
                qry.addParameter(this,"DUMMY",workdone);
                qry.addParameter(this,"DUMMY",reported);

                if (!"".equals(wo_leader))
                {
                    qry = trans.addQuery("WO_LEADER", "DUAL", "Employee_API.Get_Name(Site_API.Get_Company(?), ?) NAME", "", "");
                    qry.addParameter(this,"CONTRACT",contract);
                    qry.addParameter(this,"WORK_LEADER_SIGN_ID",wo_leader);
                }
			
		if (!"".equals(aut_code))
                {
                    qry = trans.addQuery("AUTHORIZER", "DUAL", "Order_Coordinator_API.Get_Name(?) NAME", "", "");
                    qry.addParameter(this,"AUTHORIZE_CODE",aut_code);
                }

		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		// Only access db on demand from user.
		if (autosearchflag || find)	trans = submit(trans);

		db_rows = rowset.countDbRows();
		ctx.writeValue("DB_ROWS", db_rows+"" );     
		ctx.writeFlag("SEARCH_ORDERS",find);
	}


	//==========================================================================
	//  
	//==========================================================================

	public static String getDescription()
	{
		return "NINVWOTIT: Not invoiced/concluded work orders ";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": NotInvoicedWO.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("NINVWOQWO1: - You have " ) + db_rows +
			translate("NINVWOQWO2:  Work Orders.");
		}
		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("NINVWOCUST: Customize ") + name;            
	}


	public void printContents()
	{
		// Show information text in portlet.
		if (!hideinfoflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("NINVWOINFO1: Here you can find work orders which have posting lines that are not invoiced. "); 
			printText("NINVWOINFO2: To see more specific information you can ");
			printScriptLink("NINVWOCUSTLINK: customize", "customizeNotInvoicedWO");  
			printSpaces(1);	// MAGN Correction of text.
			printText("NINVWOINFO3: and give more search data.");
			endFont();
		}
		// Show search criteria in portlet. 
		if (dispcriteriaflag)
		{
			setFontStyle(BOLD);
			printText("PCMINVWOSEARCHCRIT: Search criteria");
			endFont();
			printText("PCMNINVWOSITE: Site: ");
			printText(contract);
			printNewLine();
			printText("PCMNINVWOOBJ: Object Id: ");
			printText(object_id);
			printNewLine();
			printText("PCMNINVWOCUST: Customer: ");
			printText(customer_id);
			printNewLine();
			if ("".equals(aut_code))
				printText("PCMNINVWOCOORD: Coordinator: ");
			else if (trans.getValue("AUTHORIZER/DATA/NAME") == null)
			{
				printText("PCMNINVWOCOORD: Coordinator: ");
				printText(aut_code);
			}
			else
			{
				printText("PCMNINVWOCOORD: Coordinator: ");
				printText(aut_code+"-"+trans.getValue("AUTHORIZER/DATA/NAME"));
			}
			printNewLine();
			if ("".equals(wo_leader))
				printText("PCMNINVWOWOLEADER: Work Leader: ");
			else if (trans.getValue("WO_LEADER/DATA/NAME") == null)
			{
				printText("PCMNINVWOWOLEADER: Work Leader: ");
				printText(wo_leader);
			}
			else
			{
				printText("PCMNINVWOWOLEADER: Work Leader: ");
				printText(wo_leader+"-"+trans.getValue("WO_LEADER/DATA/NAME"));
			}
			printNewLine();
			printText("PCMNINVWOSSTATES: States: ");
			printText(getSearchStates(stateFlagArray, stateArray));
			printNewLine();
			printNewLine();
		}

		printHiddenField("CMD","");


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
						printSubmitLink("NINVWOFIRST: First","firstCust");
					else
						printText("NINVWOFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("NINVWOPRV: Previous","prevCust");
					else
						printText("NINVWOPRV: Previous");

					printSpaces(5);
					String rows = translate("NINVWOROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("NINVWOLAST: Last","lastCust");
					else
						printText("NINVWOLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("NINVWONEXT: Next","nextCust");
					else
						printText("NINVWONEXT: Next");

					printNewLine();
					printNewLine();

				}
			}
			else
			{
				if (!autosearchflag)
				{
					printNewLine();
					printSubmitLink("NINVWOSEARCH: Search","findCust");
				}
				setFontStyle(BOLD);    
				printText("NINVWONODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printNewLine();
			printSubmitLink("NINVWOSEARCH: Search","findCust");
		}
	}

	//==========================================================================
	//  
	//==========================================================================

	public void runCustom()
	{
		if (DEBUG) debug(this+": NotInvoicedWO.runCustom()");

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

	}


	public void printCustomBody() throws FndException
	{
		{
			debug(this+": NotInvoicedWO.printCustomBody()");
			debug("\tprintCustomBody(): NotInvoicedWO");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("NINVWOQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("NINVWOCS: Choose Site: ");
		printNewLine();
		printSpaces(5);
		printField("CONTRACTC",contract);
		printDynamicLOV("CONTRACTC","USER_ALLOWED_SITE_LOV","NINVWOLOVCON: List of Site");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("NINVWOCOI: Choose Object Id:");
		printNewLine();
		printSpaces(5);
		printField("OBJECTIDC", object_id, 20);
		printDynamicLOV("OBJECTIDC","MAINTENANCE_OBJECT","NINVWOLOVOI: List of Object ID");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("NINVWOCCU: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERIDC", customer_id, 20);
		printDynamicLOV("CUSTOMERIDC","CUSTOMER_INFO","NINVWOLOVCUS: List of Customer");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("NINVWOCCOO: Choose Coordinator:");
		printNewLine();
		printSpaces(5);
		printField("COORDINATORC",aut_code);
		printDynamicLOV("COORDINATORC","ORDER_COORDINATOR_LOV","NINVWOLOVCOOR: List of Coordinator");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("NINVWOCWL: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC", wo_leader);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);     
		setFontStyle(BOLD);
		printText("NINVWOSTATUSTEXT: Choose status:");
		endFont();
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOWORKREQ", workreqflag);
		printSpaces(3);
		printText("NINVWOWORKREQTEXT: Work Request");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOFAULTREP", faultrepflag);
		printSpaces(3);
		printText("NINVWOFAULTREPTEXT: Fault Report");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOOBSERVED", observedflag);
		printSpaces(3);
		printText("NINVWOOBSERVEDTEXT: Observed");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOUNDERPREP", underprepflag);
		printSpaces(3);
		printText("NINVWOUNDERPREPTEXT: Under Preparation");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOPREPARED", preparedflag);
		printSpaces(3);
		printText("NINVWOPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("NINVWORELEASED", releasedflag);
		printSpaces(3);
		printText("NINVWORELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("NINVWOSTARTED", startedflag);
		printSpaces(3);
		printText("NINVWOSTARTEDTEXT: Started");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOWORKDONE", workdoneflag);
		printSpaces(3);
		printText("NINVWOWORKDONETEXT: Work Done");
		printNewLine();

		printSpaces(5);
		printCheckBox("NINVWOREPORTED", reportedflag);
		printSpaces(3);
		printText("NINVWOREPORTEDTEXT: Reported");
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("NINVWOPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("NINVWOPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("NINVWO", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMACTIVESEPARATEDISPSEARCH", dispcriteriaflag);
		printSpaces(3);
		printText("PCMACTIVESEPARATEDISPSEARCHTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMACTIVESEPARATEHIDEINFO", hideinfoflag);
		printSpaces(3);
		printText("PCMACTIVESEPARATEHIDEINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMACTIVESEPARATEAUTOSEARCH", autosearchflag);
		printSpaces(3);
		printText("PCMACTIVESEPARATEAUTOSEARCHTEXT: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("NINVWOLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();     
		printNewLine();

	}


	public void submitCustomization()
	{

		stateFlagArray = new boolean[9];
		stateArray = new String[9];

		if (DEBUG)
		{
			debug(this+": NotInvoicedNotInvoicedWONotInvoicedWO.submitCustomization()");
			debug("\tsubmitCustomization(): current values:\n\t\tcontract="+contract);
		}

		workreqflag = "TRUE".equals(readValue("NINVWOWORKREQ"));
		if (workreqflag)
			workrequest = "WORKREQUEST";
		else
			workrequest	= null;
		writeProfileFlag("WORKREQFLAG", workreqflag);
		stateFlagArray[0] = workreqflag;
		stateArray[0] = dec_workrequest;

		faultrepflag = "TRUE".equals(readValue("NINVWOFAULTREP"));
		if (faultrepflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= null;
		writeProfileFlag("FAULTREPFLAG", faultrepflag);
		stateFlagArray[1] = faultrepflag;
		stateArray[1] = dec_faultreport;

		observedflag = "TRUE".equals(readValue("NINVWOOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = null;
		writeProfileFlag("OBSERVEDFLAG", observedflag);
		stateFlagArray[2] = observedflag;
		stateArray[2] = dec_observed;

		underprepflag = "TRUE".equals(readValue("NINVWOUNDERPREP"));
		if (underprepflag)
			underpreparation = "UNDERPREPARATION";
		else
			underpreparation = null;
		writeProfileFlag("UNDERPREPFLAG", underprepflag);
		stateFlagArray[3] = underprepflag;
		stateArray[3] = dec_underpreparation;

		preparedflag = "TRUE".equals(readValue("NINVWOPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = null;
		writeProfileFlag("PREPAREDFLAG", preparedflag);
		stateFlagArray[4] = preparedflag;
		stateArray[4] = dec_prepared;

		releasedflag = "TRUE".equals(readValue("NINVWORELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = null;
		writeProfileFlag("RELEASEDFLAG", releasedflag);
		stateFlagArray[5] = releasedflag;
		stateArray[5] = dec_released;

		startedflag = "TRUE".equals(readValue("NINVWOSTARTED"));
		if (startedflag)
			started = "STARTED";
		else
			started	= null;
		writeProfileFlag("STARTEDFLAG", startedflag);
		stateFlagArray[6] = startedflag;
		stateArray[6] = dec_started;

		workdoneflag = "TRUE".equals(readValue("NINVWOWORKDONE"));
		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = null;
		writeProfileFlag("WORKDONEFLAG", workdoneflag);
		stateFlagArray[7] = workdoneflag;
		stateArray[7] = dec_workdone;

		reportedflag = "TRUE".equals(readValue("NINVWOREPORTED"));
		if (reportedflag)
			reported = "REPORTED";
		else
			reported = null;
		writeProfileFlag("REPORTEDFLAG", reportedflag);
		stateFlagArray[8] = reportedflag;
		stateArray[8] = dec_reported;

		contract    = readValue("CONTRACTC");
		object_id   = readValue("OBJECTIDC");
		aut_code    = readValue("COORDINATORC");
		wo_leader   = readValue("WOLEADERC");
		customer_id = readValue("CUSTOMERIDC");
		name        = readValue("NINVWO");

		writeProfileValue("CONTRACT", readAbsoluteValue("CONTRACTC") );
		writeProfileValue("COMPANY",  readAbsoluteValue("COMPANY") );
		writeProfileValue("MCH_CODE",  readAbsoluteValue("OBJECTIDC") );
		writeProfileValue("WORK_LEADER_SIGN_ID",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("AUTHORIZE_CODE",  readAbsoluteValue("COORDINATORC") );
		writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERIDC") );
		writeProfileValue("NINVWO",  readAbsoluteValue("NINVWO") );

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));

		dispcriteriaflag = "TRUE".equals(readValue("PCMACTIVESEPARATEDISPSEARCH"));
		writeProfileFlag("DISPSEARCHFLAG",dispcriteriaflag);

		hideinfoflag = "TRUE".equals(readValue("PCMACTIVESEPARATEHIDEINFO"));
		writeProfileFlag("HIDEINFOFLAG",hideinfoflag);

		autosearchflag = "TRUE".equals(readValue("PCMACTIVESEPARATEAUTOSEARCH"));
		writeProfileFlag("AUTOSEARCHFLAG",autosearchflag);
	}

	public boolean canCustomize()
	{
		return true;
	}


	private void defineDecodedStates()
	{
		trans = getASPManager().newASPTransactionBuffer();

		trans.addQuery("WORKREQUEST", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('WORKREQUEST') DECODEWORKREQUEST", "", "");

		trans.addQuery("FAULTREPORT", "DUAL","Active_Work_Order_API.Finite_State_Decode__('FAULTREPORT') DECODEFAULTREPORT", "", "");

		trans.addQuery("OBSERVED", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('OBSERVED') DECODEOBSERVED", "", "");

		trans.addQuery("UNDERPREPARATION", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('UNDERPREPARATION') DECODEUNDERPREPARATION", "", "");

		trans.addQuery("PREPARED", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('PREPARED') DECODEPREPARED", "", "");

		trans.addQuery("RELEASED", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('RELEASED') DECODERELEASED", "", "");

		trans.addQuery("STARTED", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('STARTED') DECODESTARTED", "", "");

		trans.addQuery("WORKDONE", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('WORKDONE') DECODEWORKDONE", "", "");

		trans.addQuery("REPORTED", "DUAL", "Active_Work_Order_API.Finite_State_Decode__('REPORTED') DECODEREPORTED", "", "");


		// performConfig is only supposed to be used when ASPPoolElement is undefined.
		if (isUndefined()) trans = performConfig(trans);
		else trans = submit(trans);

		dec_workrequest = trans.getValue("WORKREQUEST/DATA/DECODEWORKREQUEST");
		dec_faultreport = trans.getValue("FAULTREPORT/DATA/DECODEFAULTREPORT");
		dec_observed = trans.getValue("OBSERVED/DATA/DECODEOBSERVED");
		dec_underpreparation = trans.getValue("UNDERPREPARATION/DATA/DECODEUNDERPREPARATION");
		dec_prepared = trans.getValue("PREPARED/DATA/DECODEPREPARED");
		dec_released = trans.getValue("RELEASED/DATA/DECODERELEASED");
		dec_started = trans.getValue("STARTED/DATA/DECODESTARTED");
		dec_workdone = trans.getValue("WORKDONE/DATA/DECODEWORKDONE");
		dec_reported = trans.getValue("REPORTED/DATA/DECODEREPORTED");

	}

	private String getSearchStates( boolean[] stateFlagArray, String[] stateArray )
	{
		String statelist = "";

		for (int i=0; i<stateFlagArray.length; i++)
		{
			if (stateFlagArray[i])
				statelist = statelist+stateArray[i]+" ";
		}
		return statelist;
	}

}