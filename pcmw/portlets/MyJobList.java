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
 * File        : MyJobList.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    MIBO      00-05-26 Created.
 *    MIBO      00-06-02 Added addWhereCondition in run().
 *    MIBO      00-06-06 Added setSize(20) for addField "Short Description".
 *    PJONSE    00-06-28 Added qry.addWhereCondition("WORK_LEADER_SIGN = '"+sign+"' OR SIGN ='"+sign+"'"). 
 *    PJONSE    00-07-04 Call Id: 45169. Added 'if (dummy != 2)' in protected void run().
 *    PJONSE	00-07-12 Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 *    PJONSE    00-08-31 Changed translation constant MYJOBLISTTITLE to MYJOBLISTTITLE1 and MYJOBLISTTITLE2. 
 *    MAGN      00-11-02 Changed portlet according to new development guidelines.
 *    NUPELK    00-11-01 Added the actions 'Started','Work' and 'Print' 
 *    JIJOSE    00-12-18 Distinct selection in query.
 *    CHCRLK    01-06-19 Added code to make Hide information text and Display search criteria work.
 *    ARWILK    03-10-15 (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 *    VAGULK    040311   Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables,
 * -------------------------------------------
 *    NAMELK    060901   Merged Bug 58216, Eliminated SQL Injection security vulnerability.
 *    AMDILK    070320   Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 *    AMDILK    070531   Modified submitCustomization()
 *    SHAFLK    080715  Bug 75697, Removed DEBUG value set to true.
 *    SHAFLK    090525  Bug 83038, Modified run().
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

public class MyJobList extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.MyJobList");

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

	private transient int      skip_rows;
	private transient int      db_rows;
	private transient boolean  find;
	private transient int      size;

	private transient String  faultreport;
	private transient String  workreq;
	private transient String  observed;
	private transient String  underprep;
	private transient String  prepared;
	private transient String  released;
	private transient String  started;
	private transient String  workdone;
	private transient String  reported;
	private transient String  showrows;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient boolean faultreportflag;
	private transient boolean workreqflag;
	private transient boolean observedflag;
	private transient boolean underprepflag;
	private transient boolean preparedflag;
	private transient boolean releasedflag;
	private transient boolean startedflag;
	private transient boolean workdoneflag;
	private transient boolean reportedflag;

	private transient String  name;
	private transient String  no_of_hits;

	// Control if any values should be fetched to the portlet for each user.
	private transient boolean autosearchflag;
	private transient boolean dispcriteriaflag;
	private transient boolean hideinfoflag;
	private transient boolean cantperform;
	//==========================================================================
	//  Construction
	//==========================================================================

	public MyJobList( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": MyJobList.<init> :"+portal+","+clspath);
	}


	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================
	/**
	  * The first method to be called in this class after construction.
	  * Not called on cloning.
	  */
	public ASPPage construct() throws FndException
	{
		if (DEBUG) debug(this+": MyJobList.construct()");
		return super.construct();
	}

	/**
	 * Called after define() while changing state from UNDEFINED to DEFINED.
	 */
	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": MyJobList.doFreeze()");
		super.doFreeze();
	}

	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": MyJobList.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	// 
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": MyJobList.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		ASPCommandBar cmdbar = blk.newASPCommandBar();

		// Note, this column is used in DISTINCT query and must be placed first.
		addField(blk, "OBJID" ).
		setHidden();
		addField(blk, "OBJVERSION" ).
		setHidden();
		addField(blk, "OBJSTATE" ).
		setHidden();
		addField(blk, "OBJEVENTS" ).
		setHidden();
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "STATUS_SEQUENCE").setHidden();
		addField(blk, "SIGN").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("WONOMYJOBLIST: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("WONOMYJOBLIST: Wo No");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "ERR_DESCR").setSize(20).setLabel("SHORTDESCMYJOBLIST: Short Description");
		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,CONTRACT").setLabel("OBJECTIDMYJOBLIST: Object ID");
		addField(blk, "STATE").setLabel("STATUSMYJOBLIST: Status");
		addField(blk, "PRIORITY_ID").setLabel("PRIORITYMYJOBLIST: Prio");  
		// Added NUPE/LK
		addField(blk, "CHAR_CODE30").setFunction("CHR(30)").setHidden();
		addField(blk, "CHAR_CODE31").setFunction("CHR(31)").setHidden();
		addField(blk, "RESULT_KEY").
		setFunction("' '").
		setHidden();
		addField(blk,"ATTR0").setFunction("''").setHidden();
		addField(blk,"ATTR1").setFunction("''").setHidden();
		addField(blk,"ATTR2").setFunction("''").setHidden();
		addField(blk,"ATTR3").setFunction("''").setHidden();
		addField(blk,"ATTR4").setFunction("''").setHidden();

		blk.setView("WORK_ORDER_ROLE_ACTIVEWO");
		blk.defineCommand("Active_Separate_API","Start_Order__,Work__");
		cmdbar.addCustomCommand("START",  "ACTWOSTA: Started");
		cmdbar.addCustomCommand("WORK",  "ACTWOWRK: Work Done");
		cmdbar.addCustomCommand("PRINT",  "ACTWOPRN: Print");

		tbl = newASPTable( blk );
        
		rowset = blk.getASPRowSet();

		autosearchflag = false;
		hideinfoflag = false;
		dispcriteriaflag = false;

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

		appendJavaScript( "function customizeMyJobList(obj,id)",
						  "{",
						  "   customizeBox(id);",
						  "}\n");

		init();
	}


	protected void init()// throws FndException
	{


		if (DEBUG) debug(this+": MyJobList.init()");

		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);
		name             = readProfileValue( "PCMMYJOBLISTNAME", name);


		faultreportflag  = readProfileFlag ( "FAULTREPORTFLAG", true );
		workreqflag      = readProfileFlag ( "WORKREQFLAG", true );
		observedflag     = readProfileFlag ( "OBSERVEDFLAG", true );
		underprepflag    = readProfileFlag ( "UNDERPREPFLAG", true );
		preparedflag     = readProfileFlag ( "PREPAREDFLAG", true );
		releasedflag     = readProfileFlag ( "RELEASEDFLAG", true );
		startedflag      = readProfileFlag ( "STARTEDFLAG", true );
		workdoneflag     = readProfileFlag ( "WORKDONEFLAG", true );
		reportedflag     = readProfileFlag ( "REPORTEDFLAG", true );            

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= "";

		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= "";

		if (observedflag)
			observed = "OBSERVED";
		else
			observed = "";

		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = "";

		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = "";

		if (releasedflag)
			released = "RELEASED";
		else
			released = "";


		if (startedflag)
			started = "STARTED";
		else
			started	= "";

		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = "";

		if (reportedflag)
			reported = "REPORTED";
		else
			reported = "";

		autosearchflag = readProfileFlag ( "AUTOSEARCHFLAG", false );
		hideinfoflag = readProfileFlag ( "HIDEINFOFLAG", false );
		dispcriteriaflag = readProfileFlag ( "DISPSEARCHFLAG", false );

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );


		showrows = readValue("CMD");
		//  cmd = readValue("CMD");

		if (Str.isEmpty(showrows))
		{

			if (cmdBarCustomCommandActivated())
				showrows = getCmdBarCustomCommandId();

		}
		if ("FIND".equals(showrows))
		{
			skip_rows = 0;
			find = true;
		}
		else if ("FIRST".equals(showrows))
		{
			skip_rows = 0;
		}
		else if ("PREV".equals(showrows))
			if (skip_rows>=size)
			{
				skip_rows -= size;
			}
			else
			{
				skip_rows = 0;
			}


		else if ("NEXT".equals(showrows) && skip_rows<=db_rows-size)
		{
			skip_rows += size;
		}
		else if ("LAST".equals(showrows))// && skip_rows>=size )
		{
			skip_rows = db_rows - size;
		}

		ctx.writeValue("SKIP_ROWS",skip_rows+"");

	}

	protected void run()
	{

		if (DEBUG) debug(this+": MyJobList.run()");
		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

		if ("START".equals(showrows))
		{

			rowset.selectRows();
			String cmdname;
			int cnt;

			rowset.setFilterOn();
			String option = rowset.getValue("OBJEVENTS");
			rowset.setFilterOff();

			if (option.indexOf("StartOrder^") == -1)
			{
				cantperform  = true;  
				find = true;
			}
			else
			{

				cmdname = "Start_Order__";
				cnt = rowset.markSelectedRows(cmdname);

				if (cnt > 0)
				{

					submit(trans);
					trans.clear();


					find = true;
				}
			}  

		}

		if ("WORK".equals(showrows))
		{
			rowset.selectRows();
			String cmdname;
			int cnt;


			rowset.setFilterOn();
			String option = rowset.getValue("OBJEVENTS");
			rowset.setFilterOff();

			if (option.indexOf("Work^") == -1)
			{
				cantperform  = true;  
				find = true;
			}
			else
			{

				cmdname = "Work__";
				cnt = rowset.markSelectedRows(cmdname);

				if (cnt > 0)
				{
					submit(trans);
					trans.clear();

					find = true;
				}
			}   
		}
		if ("PRINT".equals(showrows))
		{

			rowset.selectRows();

			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();        
			ASPCommand readcmd = mgr.newASPCommand();

			int i=0;
			while (i<rowset.countRows())
			{
				if (rowset.isRowSelected())
				{

					String attr1 = "REPORT_ID" + rowset.getValue("CHAR_CODE31") + "ACTIVE_SEP_WO_PRINT_REP" + rowset.getValue("CHAR_CODE30");
					String attr2 = "WO_NO_LIST" + rowset.getValue("CHAR_CODE31") + rowset.getValue("WO_NO") + rowset.getValue("CHAR_CODE30");

					readcmd = deftrans.addCustomCommand( "PRNT","Archive_API.New_Client_Report");
					readcmd.addParameters(this,"ATTR0");                       
					readcmd.addParameter(this,"ATTR1", attr1);
					readcmd.addParameter(this,"ATTR2", attr2);
					readcmd.addParameter(this,"ATTR3","");      
					readcmd.addParameter(this,"ATTR4","");       

					deftrans = perform(deftrans);

					rowset.setValue("RESULT_KEY",deftrans.getValue("PRNT/DATA/ATTR0"));

					mgr.transferDataTo(mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS")+"ReportPrintDlg.page",rowset.getSelectedRows("RESULT_KEY"));    
					break;
				}
				rowset.next();
				i++;
			}
		}

		if (autosearchflag || find)
		{
			trans = getASPManager().newASPTransactionBuffer();
			ASPQuery qry   = trans.addQuery(blk);
			qry.setSelectExpression( "OBJID", "DISTINCT OBJID" );
			qry.setOrderByClause("STATUS_SEQUENCE, PRIORITY_ID"); 
			qry.includeMeta("ALL");


			qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ?");
                        qry.addParameter(this,"OBJSTATE", faultreport);
                        qry.addParameter(this,"OBJSTATE", workreq);
                        qry.addParameter(this,"OBJSTATE", observed);
                        qry.addParameter(this,"OBJSTATE", underprep);
                        qry.addParameter(this,"OBJSTATE", prepared);
                        qry.addParameter(this,"OBJSTATE", released);
                        qry.addParameter(this,"OBJSTATE", started);
                        qry.addParameter(this,"OBJSTATE", workdone);
                        qry.addParameter(this,"OBJSTATE", reported);
			qry.addWhereCondition("WORK_LEADER_SIGN = Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User) OR SIGN = Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User) OR JOB_SIGN = Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User) OR WORK_MASTER_SIGN = Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)");

			qry.setBufferSize(size);
			qry.skipRows(skip_rows);
			submit(trans);

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
		return "MYJOBLISTDESC: My Job List";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": MyJobList.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("MYJOBLISTTITLE1:  - You have " ) + db_rows +
			translate("MYJOBLISTTITLE2:  Work Orders.");
		}

		else
			return name;   

	}

	public void printContents()
	{
		if (dispcriteriaflag)
		{
			boolean none = true;
			printNewLine();
			printText("PCMWMYJLSEARCH: Search Criteria:");  
			printNewLine();
			printText("PCMWMYJL: Status:");
			printSpaces(2);
			if (faultreportflag)
			{
				printText("MYJOBLISTFAREPORTTEXT: Faultreport");
				none = false;
			}

			if (workreqflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTWORKREQTEXT: Work Request");
				none = false;         
			}

			if (observedflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTOBSERVEDTEXT: Observed");
				none = false;
			}

			if (underprepflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTUNDERPREPTEXT: Under Preparation");
				none = false;
			}

			if (preparedflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTPREPAREDTEXT: Prepared");
				none = false;
			}

			if (releasedflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTRELEASEDTEXT: Released");
				none = false;
			}

			if (startedflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTSTARTEDTEXT: Started");
				none = false;
			}

			if (workdoneflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTWORKDONETEXT: Work Done");
				none = false;
			}

			if (reportedflag)
			{
				if (!none)
				{
					printText(", ");
				}
				printText("MYJOBLISTREPORTEDTEXT: Reported");
				none = false;
			}

			if (none)
			{
				printText("MYJOBLISTNOSTAT: No Status Selected.");
			}
			printNewLine();
		}

		if (!hideinfoflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("PCMWMYJOBLISTINFO1: Active work orders where you are work leader or planned craft. "); 
			printText("PCMWMYJOBLISTINFO2: In  ");
			printScriptLink("MYJOBLISTCUSTLINK: customize", "customizeMyJobList");  
			printText("MYJOBLISTINFO3:    page you can select what status to view. ");
			printNewLine();
			endFont();
		}

		printHiddenField("CMD","");

		int    nRows;
		nRows = rowset.countRows();


		if (autosearchflag || find)
		{
			if (nRows > 0)
			{

				printTable(tbl);

				if (cantperform)
				{
					setFontStyle(BOLD);   
					printText("CANTPERFORMOP: Requested operation is not allowed for the selection");   
					endFont();
					cantperform = false;
				}

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("MYJOBLISTFIRST: First","firstCust");
					else
						printText("MYJOBLISTFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("MYJOBLISTPRV: Previous","prevCust");
					else
						printText("MYJOBLISTPRV: Previous");

					printSpaces(5);
					String rows = translate("MYJOBLISTROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("MYJOBLISTLAST: Last","lastCust");
					else
						printText("MYJOBLISTLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("MYJOBLISTNEXT: Next","nextCust");
					else
						printText("MYJOBLISTNEXT: Next");

					printNewLine();
					printNewLine();
				}
			}
			else
			{
				if (!autosearchflag)
				{
					printSubmitLink("MYJOBLISTSEARCH: Search","findCust");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("MYJOBLISTNODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("MYJOBLISTSEARCH: Search","findCust");
			printNewLine();
			printNewLine();
		}
	}

	public boolean canCustomize()
	{
		return true;
	}

	public void runCustom()
	{
		if (DEBUG)
		{
			debug(this+": MyJobList.runCustom()");
		}

	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": MyJobList.printCustomBody()");
			debug("\tprintCustomBody(): MyJobList");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOSMQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);   
		printText("MYJOBLISTCHSTATUS: Choose status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTFAULTREPORT", faultreportflag);
		printSpaces(3);
		printText("MYJOBLISTFAREPORTTEXT: Faultreport");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTWORKREQ", workreqflag);
		printSpaces(3);
		printText("MYJOBLISTWORKREQTEXT: Work Request");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTOBSERVED", observedflag);
		printSpaces(3);
		printText("MYJOBLISTOBSERVEDTEXT: Observed");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTUNDERPREP", underprepflag);
		printSpaces(3);
		printText("MYJOBLISTUNDERPREPTEXT: Under Preparation");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTPREPARED", preparedflag);
		printSpaces(3);
		printText("MYJOBLISTPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("MYJOBLISTRELEASED", releasedflag);
		printSpaces(3);
		printText("MYJOBLISTRELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("MYJOBLISTSTARTED", startedflag);
		printSpaces(3);
		printText("MYJOBLISTSTARTEDTEXT: Started");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTWORKDONE", workdoneflag);
		printSpaces(3);
		printText("MYJOBLISTWORKDONETEXT: Work Done");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTREPORTED", reportedflag);
		printSpaces(3);
		printText("MYJOBLISTREPORTEDTEXT: Reported");
		printNewLine();  
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("PCMMYJOBLISTPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("PCMMYJOBLISTPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("PCMMYJOBLISTNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMMYJOBLISTDISPSEARCH", dispcriteriaflag);
		printSpaces(3);
		printText("PCMMYJOBLISTDISPSEARCHTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMMYJOBLISTHIDEINFO", hideinfoflag);
		printSpaces(3);
		printText("PCMMYJOBLISTHIDEINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMMYJOBLISTAUTOSEARCH", autosearchflag);
		printSpaces(3);
		printText("PCMMYJOBLISTAUTOSEARCHTEXT: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("LINEMAXHITMYJOBLIST: Max no of hits to be displayed ");
//      size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}

	public void submitCustomization()
	{

		name        = readValue("PCMMYJOBLISTNAME");
		writeProfileValue("PCMMYJOBLISTNAME",  readAbsoluteValue("PCMMYJOBLISTNAME") );

		faultreportflag = "TRUE".equals(readValue("MYJOBLISTFAULTREPORT"));
		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";
		writeProfileFlag("FAULTREPORTFLAG", faultreportflag);

		workreqflag = "TRUE".equals(readValue("MYJOBLISTWORKREQ"));
		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= " ";
		writeProfileFlag("WORKREQFLAG", workreqflag);

		observedflag = "TRUE".equals(readValue("MYJOBLISTOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";
		writeProfileFlag("OBSERVEDFLAG", observedflag);

		underprepflag = "TRUE".equals(readValue("MYJOBLISTUNDERPREP"));
		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = " ";
		writeProfileFlag("UNDERPREPFLAG", underprepflag);

		preparedflag = "TRUE".equals(readValue("MYJOBLISTPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";
		writeProfileFlag("PREPAREDFLAG", preparedflag);

		releasedflag = "TRUE".equals(readValue("MYJOBLISTRELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";
		writeProfileFlag("RELEASEDFLAG", releasedflag);


		startedflag = "TRUE".equals(readValue("MYJOBLISTSTARTED"));
		if (startedflag)
			started = "STARTED";
		else
			started	= " ";
		writeProfileFlag("STARTEDFLAG", startedflag);

		workdoneflag = "TRUE".equals(readValue("MYJOBLISTWORKDONE"));
		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = " ";
		writeProfileFlag("WORKDONEFLAG", workdoneflag);

		reportedflag = "TRUE".equals(readValue("MYJOBLISTREPORTED"));
		if (reportedflag)
			reported = "REPORTED";
		else
			reported = " ";
		writeProfileFlag("REPORTEDFLAG", reportedflag);

		autosearchflag = "TRUE".equals(readValue("PCMMYJOBLISTAUTOSEARCH"));
		writeProfileFlag("AUTOSEARCHFLAG",autosearchflag);

		dispcriteriaflag = "TRUE".equals(readValue("PCMMYJOBLISTDISPSEARCH"));
		writeProfileFlag("DISPSEARCHFLAG",dispcriteriaflag);

		hideinfoflag = "TRUE".equals(readValue("PCMMYJOBLISTHIDEINFO"));
		writeProfileFlag("HIDEINFOFLAG",hideinfoflag);


		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}
	}

	private String nvl( String str, String instead_of_empty )
	{
		return Str.isEmpty(str) ? instead_of_empty : str;
	}
}
