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
 * ---------------------------------------------------------------------------
 * File        : ActiveWorkOrders.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * MIBOSE  000508  Created.
 * MIBOSE  000606  Added setSize(20) for addField "Short Description".
 * PJONSE  000704  Call Id: 45169. Added 'if (dummy != 2)','if dummy == 1' and 'if dummy == 2' in protected void run().
 * PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831  Changed translation constant ACTIVEWORKORDERSTITLE to ACTIVEWORKORDERSTITLE1 and ACTIVEWORKORDERSTITLE2. 
 * OSRYSE  001012  Changed portlet according to new development guidelines.
 * NUPELK  001102  Added 'Start','Work' and 'Print' actions.
 * ARWILK  001107  Removed the select boxes (In customize page) and added dynamic LOVs.
 * JEWILK  001128  Modified to replace '+' sign in plan_s_date and plan_f_date if the user enter it, since the method Str.toInt() can not handle '+' sign [in run()].
 * JEWILK  001129  Modified function printContents() to print search criteria values in different printText() methods. 
 * NUPELK  001227  Added an LOV for Work Leader in the customization page 
 * NUPELK  010104  Changed the view used to retrieve work leaders to EMPLOYEE_LOV. 
 * INROLK  010627  Changed init() to check SEARCH. call id 65915.
 * BUNILK  021216  Added AspField MCH_CODE_CONTRACT.   
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031016  Call Id:107874 Changed the getDescription method to pass the ASPManager parameter there
 *                 by made it possible to use the translate method to translate a given string. 
 * THWILK  031030  Call Id:109746 Added getDescription method sice it was overloaded by the call ID 107874.
 * VAGULK  040302  Web Alignment Removed Clone() and doReset() methods and unnecessary global variables.
 * ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
 * NAMELK  041108  Non Standard Translation Tags Changed.
 * ------------------------------- 
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELK  060901  Merged bug 58216.
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

public class ActiveWorkOrders extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.ActiveWorkOrders");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPBlock   defblk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	//==========================================================================
	//  Mutable attributes
	//==========================================================================

	private transient String  name; 

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================
	private transient String  company;
	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;      

	private transient String  faultreport;
	private transient String  workreq;
	private transient String  observed;
	private transient String  underprep;
	private transient String  prepared;
	private transient String  released;
	private transient String  started;
	private transient String  workdone;
	private transient String  reported;
	private transient String  cmd;

	private transient ASPTransactionBuffer trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	private transient String  contract;
	private transient String  state;
	private transient String  work_type;
	private transient String  work_leader_sign;
	private transient String  plan_s_date;
	private transient String  plan_f_date;
	private transient String  org_code;
	private transient String  group_id;

	private transient boolean faultreportflag;
	private transient boolean workreqflag;
	private transient boolean observedflag;
	private transient boolean underprepflag;
	private transient boolean preparedflag;
	private transient boolean releasedflag;
	private transient boolean startedflag;
	private transient boolean workdoneflag;
	private transient boolean reportedflag;
	private transient boolean cantperform;

	// Customization flags
	private transient boolean autosearchflag;    
	private transient boolean infotextflag;  
	private transient boolean dispsearchflag;    


	//==========================================================================
	//  Construction
	//==========================================================================

	public ActiveWorkOrders( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": ActiveWorkOrders.<init> :"+portal+","+clspath);
	}



	//==========================================================================
	// Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": ActiveWorkOrders.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		ASPCommandBar cmdbar = blk.newASPCommandBar();

		addField(blk, "OBJID" ).setHidden();
		addField(blk, "OBJVERSION" ).setHidden();
		addField(blk, "OBJSTATE" ).setHidden();
		addField(blk, "OBJEVENTS" ).setHidden();
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "MCH_CODE_CONTRACT").setHidden();
		addField(blk, "COMPANY").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2.page","WO_NO").setLabel("WONOACTIVEWO: Wo No");
		addField(blk, "WO_NO", "Number" , "#").setHyperlink("pcmw/ActiveSeparate2.page","WO_NO").setLabel("WONOACTIVEWO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "ERR_DESCR").setSize(20).setLabel("SHORTDESCACTIVEWO: Short Description");
		addField(blk, "MCH_CODE").setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("OBJECTIDACTIVEWO: Object ID");
		addField(blk, "STATE").setLabel("STATUSACTIVEWO: Status");
		addField(blk, "PRIORITY_ID").setLabel("PRIORITYACTIVEWO: Prio");
		addField(blk, "CHAR_CODE30").setFunction("CHR(30)").setHidden();
		addField(blk, "CHAR_CODE31").setFunction("CHR(31)").setHidden();
		addField(blk, "RESULT_KEY").setFunction("' '").setHidden();
                addField(blk, "DUMMY","String").setFunction("''").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "DEFCOMPANY");
		addField(defblk, "USERREPORTEDBYID");
		addField(defblk, "DEFORGCODE");
		addField(defblk, "PLAN_S_DATE");
		addField(defblk, "PLAN_F_DATE");
		addField(defblk,"ATTR0");
		addField(defblk,"ATTR1");
		addField(defblk,"ATTR2");
		addField(defblk,"ATTR3");
		addField(defblk,"ATTR4");

		blk.setView("ACTIVE_SEPARATE");
		blk.defineCommand("Active_Separate_API","Start_Order__,Work__");
		cmdbar.addCustomCommand("START",  "ACTWOSTA: Started");
		cmdbar.addCustomCommand("WORK",  "ACTWOWRK: Work Done");
		cmdbar.addCustomCommand("PRINT",  "ACTWOPRN: Print");

		tbl = newASPTable( blk );

		rowset = blk.getASPRowSet();


		// JavaScript functions, for the functions FIRST, PREV, LAST, NEXT and FIND
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

		appendJavaScript( "function customizeActiveWO(obj,id)",
						  "{",
						  "   customizeBox(id);",
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


	protected void init()// throws FndException
	{

		if (DEBUG) debug(this+": ActiveWorkOrders.init()");


		// Read values from the profile
		org_code         = readProfileValue("ORG_CODE", null); 
		work_type        = readProfileValue("WORK_TYPE_ID", null);
		work_leader_sign = readProfileValue("WORK_LEADER_SIGN", null);
		group_id         = readProfileValue("GROUP_ID", null);
		state            = readProfileValue("STATE");
		contract         = readProfileValue("CONTRACT");   
		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);
		name             = readProfileValue( "ACTIVEWONAME", name);
		plan_s_date      = readProfileValue("PLAN_S_DATE", "");
		plan_f_date      = readProfileValue("PLAN_F_DATE", "");

		faultreportflag  = readProfileFlag ( "FAULTREPORTFLAG", true );
		workreqflag      = readProfileFlag ( "WORKREQFLAG", true );
		observedflag     = readProfileFlag ( "OBSERVEDFLAG", true );
		underprepflag    = readProfileFlag ( "UNDERPREPFLAG", true );
		preparedflag     = readProfileFlag ( "PREPAREDFLAG", true );
		releasedflag     = readProfileFlag ( "RELEASEDFLAG", true );
		startedflag      = readProfileFlag ( "STARTEDFLAG", true );
		workdoneflag     = readProfileFlag ( "WORKDONEFLAG", true );
		reportedflag     = readProfileFlag ( "REPORTEDFLAG", true ); 
		autosearchflag   = readProfileFlag ( "CUSTOMIZED", false ); 
		infotextflag     = readProfileFlag ( "INFOTEXT", false );    
		dispsearchflag   = readProfileFlag ( "DISPSEARCHFLAG", false );

		find   = ctx.readFlag("SEARCH_ORDERS",false);


		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";

		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= " ";

		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";

		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = " ";

		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";

		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";

		if (startedflag)
			started = "STARTED";
		else
			started	= " ";

		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = " ";

		if (reportedflag)
			reported = "REPORTED";
		else
			reported = " ";


		if (Str.isEmpty(contract))
		{
			contract      = ctx.readValue("CONTRACT");
		}
		if (Str.isEmpty(org_code))
			org_code   = ctx.readValue("ORG_CODE");

		ASPContext ctx = getASPContext();

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		// Read the command from the hidden field CMD
		cmd = readValue("CMD","");

		if (Str.isEmpty(cmd))
		{

			if (cmdBarCustomCommandActivated())
				cmd = getCmdBarCustomCommandId();

		}
		if ("FIND".equals(cmd))
		{
			skip_rows = 0;
			find = true;
		}
		else if ("SEARCH".equals(cmd))
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

	//==========================================================================
	//  Normal mode
	//==========================================================================

	/**
	* Run the business logic here. Do not call submit() or perform() located
	* in ASPManager class. Use versions from the super class.
	*/
	protected void run()
	{

		if (DEBUG) debug(this+": ActiveWorkOrders.run()");

		if (Str.isEmpty(company))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Site_API.Get_Company(User_Default_API.Get_Contract)", "DEFCOMPANY");
			deftrans.addCommand("COMP", cmd);

			deftrans = mgr.perform(deftrans);        

			company = deftrans.getValue("COMP/DATA/DEFCOMPANY");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

		if ("START".equals(cmd))
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

					getASPManager().getASPLog().debug(cmd) ;  
					submit(trans);
					trans.clear();


					find = true;
				}
			}  

		}

		if ("WORK".equals(cmd))
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

		if ("PRINT".equals(cmd))
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


		ASPQuery             qry   = trans.addQuery(blk);

		qry.setOrderByClause("WO_NO");
		qry.includeMeta("ALL");

		qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ?");                        
		qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
                qry.addParameter(this,"OBJSTATE",faultreport);
                qry.addParameter(this,"OBJSTATE",workreq);
                qry.addParameter(this,"OBJSTATE",observed);
                qry.addParameter(this,"OBJSTATE",underprep);
                qry.addParameter(this,"OBJSTATE",prepared);
                qry.addParameter(this,"OBJSTATE",released);
                qry.addParameter(this,"OBJSTATE",started);
                qry.addParameter(this,"OBJSTATE",workdone);
                qry.addParameter(this,"OBJSTATE",reported);

		if (!Str.isEmpty(contract))
		{
			qry.addWhereCondition("CONTRACT = ?");
                        qry.addParameter(this,"CONTRACT",contract);
		}

		if (!Str.isEmpty(org_code))
		{
			qry.addWhereCondition("ORG_CODE = ?");
                        qry.addParameter(this,"DUMMY",org_code);
		}

		if (!Str.isEmpty(work_type))
		{
			qry.addWhereCondition("WORK_TYPE_ID = ?");
                        qry.addParameter(this,"DUMMY",work_type);
		}

		if (!Str.isEmpty(work_leader_sign))
		{
			qry.addWhereCondition("WORK_LEADER_SIGN = ?");
                        qry.addParameter(this,"DUMMY",work_leader_sign);
		}

		if (!Str.isEmpty(group_id))
		{
			qry.addWhereCondition("GROUP_ID = ?");
                        qry.addParameter(this,"DUMMY",group_id);
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

		if (!Str.isEmpty(plan_f_date))
		{
			String temp_plan_f_date = Str.replace(plan_f_date,"+","");

			if (Str.toInt(temp_plan_f_date) > 0)
			{
				qry.addWhereCondition("trunc(PLAN_F_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                                qry.addParameter(this,"DUMMY",temp_plan_f_date);
			}
			else
			{
				qry.addWhereCondition("trunc(PLAN_F_DATE) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                                qry.addParameter(this,"DUMMY",temp_plan_f_date);
			}
		}

		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		if (autosearchflag || find)	trans = submit(trans);

		db_rows = rowset.countDbRows();
		ctx.writeValue("DB_ROWS", db_rows+"" );
		ctx.writeFlag("SEARCH_ORDERS",find);
	}

	//==========================================================================
	//    Portlet description
	//==========================================================================
	public static String getDescription()
	{
		return "ACTIVEWOEAMDESC: Active Work Orders";
	}
	public static String getDescription(ASPManager mgr)
	{
		return mgr.translate("ACTIVEWOEAMDESC: Active Work Orders");
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": ActiveWorkOrders.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			ASPManager mgr = getASPManager();
			name = getDescription(mgr);
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("PCMWACTIVEWORKORDERSPORTTITLE1:  - You have " ) + db_rows +
			translate("PCMWACTIVEWORKORDERSPORTWOS:  Work Orders.");
		}
		else
			return name;   
	}

	public void printContents()
	{
		printHiddenField("CMD","");

		// Information text, shown by default.
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("ACTIVEWOINFO1: This view present active work orders depending on the search conditions given. "); 
			printText("ACTIVEWOINFO2: To see or alter actual conditions you can go to the ");
			printScriptLink("ACTIVEWOCUSTLINK: customize", "customizeActiveWO");  
			printText("ACTIVEWOINFO3:   page. ");
			printNewLine();
			endFont();
		}

		// Search criteria, not shown by default.
		if (dispsearchflag)
		{
			setFontStyle(BOLD);
			printText("ACTIVEWOORDERSEARCHCRITERIATEXT: Search criteria: ");
			endFont();
			printText("DEPACTWODISP: Maintenance Organization: ");
			if (!Str.isEmpty(org_code))
				printText(org_code);
			else
				printText("ACTIVWOALL: All");
			printNewLine();

			printText("WTYPEACTWODISP: Work Type: ");
			if (!Str.isEmpty(work_type))
				printText(work_type);
			else
				printText("ACTIVWOALL: All");
			printNewLine();

			printText("WLEAACTWODISP: Work Leader: ");
			if (!Str.isEmpty(work_leader_sign))
				printText(work_leader_sign);
			else
				printText("ACTIVWOALL: All");
			printNewLine();

			printText("OGRIDACTWODISP: Object Group Id: ");
			if (!Str.isEmpty(group_id))
				printText(group_id);
			else
				printText("ACTIVWOALL: All");
			printNewLine();

			printText("PLANSTARTACTWODISP: Planned Start Date +/-: ");
			if (!Str.isEmpty(plan_s_date))
				printText(plan_s_date);
			printNewLine();

			printText("PLANCOMPLETACTWODISP: Planned Completion Date +/-: ");
			if (!Str.isEmpty(plan_f_date))
				printText(plan_f_date);
			printNewLine();
			printNewLine();
		}

		if (autosearchflag || find)
		{
			int    nRows;
			nRows = rowset.countRows();

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
						printSubmitLink("ACTIVEWORKORDERSFIRST: First","firstCust");
					else
						printText("ACTIVEWORKORDERSFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("ACTIVEWORKORDERSPRV: Previous","prevCust");
					else
						printText("ACTIVEWORKORDERSPRV: Previous");

					printSpaces(5);
					String rows = translate("ACTIVEWORKORDERSROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWORKORDERSLAST: Last","lastCust");
					else
						printText("ACTIVEWORKORDERSLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWORKORDERSNEXT: Next","nextCust");
					else
						printText("ACTIVEWORKORDERSNEXT: Next");

					printNewLine();
					printNewLine();

				}

			}
			else
			{
				if (!autosearchflag)
				{
					printSubmitLink("WONOTTRANSSEARCH: Search","findRecord");
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
			printSubmitLink("WONOTTRANSSEARCH: Search","findRecord");
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
			debug(this+": ActiveWorkOrders.runCustom()");
		}

		if (Str.isEmpty(company))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Site_API.Get_Company(User_Default_API.Get_Contract)", "DEFCOMPANY");
			deftrans.addCommand("COMP", cmd);

			deftrans = mgr.perform(deftrans);        

			company = deftrans.getValue("COMP/DATA/DEFCOMPANY");
		}

	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": ActiveWorkOrders.printCustomBody()");
			debug("\tprintCustomBody(): ActiveWorkOrders");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("CHOOSEEDEPACTIVEWO: Choose Maintenance Organization:");
		printNewLine();
		printSpaces(5);
		printField("ORGCODEC", org_code, 20);
		printDynamicLOV("ORGCODEC","ORG_CODE_ALLOWED_SITE_LOV","LOVEXECDEPACTIVEWO: List of Maintenance Organization");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEWTYPEACTIVEWO: Choose Work Type: ");
		printNewLine();
		printSpaces(5);
		printField("WOTYPEC",work_type);
		printDynamicLOV("WOTYPEC","WORK_TYPE","LOVWTYPEACTIVEWO: List of Work Type");   
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEWLEAACTIVEWO: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC",work_leader_sign);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEOGRIDACTIVEWO: Choose Object Group Id:");
		printNewLine();
		printSpaces(5);
		printField("GROUPIDC",group_id);
		printDynamicLOV("GROUPIDC","EQUIPMENT_OBJ_GROUP","LOVGIDACTIVEWO: List of Group ID");   
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("PLANNEDSTARTDATEACTIVEWO: Planned Start Date +/- ");
		printField("PLAN_S_DATE", plan_s_date, 5, 5);
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("PLANNEDCOMPLETIONDATEACTIVEWO: Planned Completion Date +/- ");
		printField("PLAN_F_DATE", plan_f_date, 5, 5);
		printNewLine();
		printNewLine();

		printNewLine();
		printSpaces(5);   
		printText("ACTIVEWOCHSTATUS: Choose status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOFAULTREPORT", faultreportflag);
		printSpaces(3);
		printText("ACTIVEWOFAREPORTTEXT: Faultreport");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKREQ", workreqflag);
		printSpaces(3);
		printText("ACTIVEWOWORKREQTEXT: Work Request");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOOBSERVED", observedflag);
		printSpaces(3);
		printText("ACTIVEWOOBSERVEDTEXT: Observed");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOUNDERPREP", underprepflag);
		printSpaces(3);
		printText("ACTIVEWOUNDERPREPTEXT: Under Preparation");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOPREPARED", preparedflag);
		printSpaces(3);
		printText("ACTIVEWOPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("ACTIVEWORELEASED", releasedflag);
		printSpaces(3);
		printText("ACTIVEWORELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("ACTIVEWOSTARTED", startedflag);
		printSpaces(3);
		printText("ACTIVEWOSTARTEDTEXT: Started");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKDONE", workdoneflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONETEXT: Work Done");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOREPORTED", reportedflag);
		printSpaces(3);
		printText("ACTIVEWOREPORTEDTEXT: Reported");
		printNewLine();  
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("ACTIVEWOPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("ACTIVEWONAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKDISPSEARCHFLAG", dispsearchflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONEDISPSEARCHFLAGTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKINFOTEXT", infotextflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONETEXTINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKCUSTOMIZED", autosearchflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONETEXTCUSTOMIZED: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("LINEMAXHITACTIVEWO: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}

	public void submitCustomization()
	{

		org_code         = readValue("ORGCODEC");
		work_type        = readValue("WOTYPEC");
		work_leader_sign = readValue("WOLEADERC");
		group_id         = readValue("GROUPIDC");
		state            = readValue("STATEC");
		plan_s_date      = readValue("PLAN_S_DATE");
		plan_f_date      = readValue("PLAN_F_DATE");
		name             = readValue("ACTIVEWONAME");

		writeProfileValue("ORG_CODE",  readAbsoluteValue("ORGCODEC") );
		writeProfileValue("WORK_TYPE_ID",  readAbsoluteValue("WOTYPEC") );
		writeProfileValue("WORK_LEADER_SIGN",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("GROUP_ID",  readAbsoluteValue("GROUPIDC") );
		writeProfileValue("STATE", readAbsoluteValue("STATEC") );
		writeProfileValue("PLAN_S_DATE", readAbsoluteValue("PLAN_S_DATE") );
		writeProfileValue("PLAN_F_DATE", readAbsoluteValue("PLAN_F_DATE") );
		writeProfileValue("ACTIVEWONAME",  readAbsoluteValue("ACTIVEWONAME") );

		dispsearchflag = "TRUE".equals(readValue("ACTIVEWOWORKDISPSEARCHFLAG")); 
		writeProfileFlag("DISPSEARCHFLAG", dispsearchflag);

		autosearchflag = "TRUE".equals(readValue("ACTIVEWOWORKCUSTOMIZED")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);

		infotextflag = "TRUE".equals(readValue("ACTIVEWOWORKINFOTEXT")); 
		writeProfileFlag("INFOTEXT", infotextflag);

		faultreportflag = "TRUE".equals(readValue("ACTIVEWOFAULTREPORT"));
		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= null;
		writeProfileFlag("FAULTREPORTFLAG", faultreportflag);

		workreqflag = "TRUE".equals(readValue("ACTIVEWOWORKREQ"));
		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= null;
		writeProfileFlag("WORKREQFLAG", workreqflag);

		observedflag = "TRUE".equals(readValue("ACTIVEWOOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = null;
		writeProfileFlag("OBSERVEDFLAG", observedflag);

		underprepflag = "TRUE".equals(readValue("ACTIVEWOUNDERPREP"));
		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = null;
		writeProfileFlag("UNDERPREPFLAG", underprepflag);

		preparedflag = "TRUE".equals(readValue("ACTIVEWOPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = null;
		writeProfileFlag("PREPAREDFLAG", preparedflag);

		releasedflag = "TRUE".equals(readValue("ACTIVEWORELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = null;
		writeProfileFlag("RELEASEDFLAG", releasedflag);

		startedflag = "TRUE".equals(readValue("ACTIVEWOSTARTED"));
		if (startedflag)
			started = "STARTED";
		else
			started	= null;
		writeProfileFlag("STARTEDFLAG", startedflag);

		workdoneflag = "TRUE".equals(readValue("ACTIVEWOWORKDONE"));
		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = null;
		writeProfileFlag("WORKDONEFLAG", workdoneflag);

		reportedflag = "TRUE".equals(readValue("ACTIVEWOREPORTED"));
		if (reportedflag)
			reported = "REPORTED";
		else
			reported = null;
		writeProfileFlag("REPORTEDFLAG", reportedflag);

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
