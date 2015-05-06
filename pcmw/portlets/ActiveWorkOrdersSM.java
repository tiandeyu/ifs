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
* File        : ActiveWorkOrdersSM.java	 
* Description :
* Notes       :
* ----------------------------------------------------------------------------
* Modified    :
*  MIBOSE  000605  Created.
*  PJONSE  000629  Added attribute prio and priority as a wherecondition and a profilevalue. 
*  PJONSE  000704  Call Id: 45169. Added 'if (dummy != 2)','if dummy == 1' and 'if dummy == 2' in protected void run().
*  PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
*  PJONSE  000830  Call Id: 47941. Removed duplicate translation variable - CHOOSEWTYPEACTIVEWOSM.
*  PJONSE  000831  Changed translation constant ACTIVEWORKORDERSSMTITLE to ACTIVEWORKORDERSSMTITLE1 and ACTIVEWORKORDERSSMTITLE2. 
*  OSRYSE  001026  Changed portlet according to new development guidelines.
*  NUPELK  001102  Added 'Start','Work' and 'Print' actions.
*  ARWILK  001107  Removed the select boxes (In customize page) and added dynamic LOVs.
*  ARWILK  001122  Added a new RMB called Work Order to Client.
*  JEWILK  001126  Modified function run() to replace the '+' sign from plan_s_date & plan_f_date. (# Call 54452)
*  JEWILK  001129  Modified function printContents() to print search criteria values in separate printText() methods. 
*  JIJOSE  001211  Removed RMB called Work Order to Client. Function placed in separate portlet.
*  JEWILK  001212  Changed the hyperlink of field 'WO_NO' to 'ActiveSeparate2ServiceManagement.asp'
*  JIJOSE  001212  Changed SEARCH to work also when portlet refreshed.
*  NUPELK  001227  Added an LOV for Work Leader in the customization page 
*  NUPELK  010104  Changed the view used to retrieve work leaders to EMPLOYEE_LOV.
*  BUNILK  021216  Added AspField MCH_CODE_CONTRACT.   
*  ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
*  VAGULK  040309  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
*  VAGULK  040310  Web Alignment ,Changed field name "CUSTOMER_NO" to "CUSTOMER_ID".
*  ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  ARWILK  041025  Merged LCS-47169 and did some other corrections.
* ---------------------------
*  DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELLK 060901  Merged bug 58216.
*  AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
*  AMDILK  070531  Modified submitCustomization()  
*  SHAFLK  080715  Bug 75697, Removed DEBUG value set to true.
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

public class ActiveWorkOrdersSM extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.ActiveWorkOrdersSM");

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

	// Variables that holds the name of the portlet.
	private transient String  name;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================
	private transient String   company;
	private transient String   state;
	private transient int      skip_rows;
	private transient int      db_rows;
	private transient boolean  find;
	private transient String   cmd;

	private transient String  faultreport;
	private transient String  workreq;
	private transient String  observed;
	private transient String  underprep;
	private transient String  prepared;
	private transient String  released;
	private transient String  started;
	private transient String  workdone;
	private transient String  reported;

	private transient ASPTransactionBuffer trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	private transient String  contract;
	private transient String  work_leader_sign;
	private transient String  work_type;
	private transient String  object_id;
	private transient String  customer_id;
	private transient String  plan_s_date;
	private transient String  plan_f_date;
	private transient String  org_code;
	private transient String  prio;
	private transient String  group_id;
	private transient String  symptom;

	// State flags to use as search criteria
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
	private transient boolean autosearchflag;	 // If the portlet should be automaticly populated
	private transient boolean infotextflag;	 // Hide the information text
	private transient boolean dispsearchflag;	 // Display search criteria

	//==========================================================================
	//  Construction
	//==========================================================================

	public ActiveWorkOrdersSM( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": ActiveWorkOrdersSM.<init> :"+portal+","+clspath);
	}

	//==========================================================================
	// Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": ActiveWorkOrdersSM.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		ASPCommandBar cmdbar = blk.newASPCommandBar();

		addField(blk, "OBJID" ).
		setHidden();
		addField(blk, "OBJVERSION" ).
		setHidden();
		addField(blk, "OBJSTATE" ).
		setHidden();
		addField(blk, "OBJEVENTS" ).
		setHidden();
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "MCH_CODE_CONTRACT").setHidden();
		addField(blk, "COMPANY").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2ServiceManagement.page","WO_NO").setLabel("WONOACTIVEWOSM: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2ServiceManagement.page","WO_NO").setLabel("WONOACTIVEWOSM: Wo No");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "ERR_DESCR").setSize(20).setLabel("SHORTDESCACTIVEWOSM: Short Description");
		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("OBJECTIDACTIVEWOSM: Object ID");
		addField(blk, "STATE").setLabel("STATUSACTIVEWOSM: Status");
		addField(blk, "PRIORITY_ID").setLabel("PRIORITYACTIVEWOSM: Prio");
		addField(blk, "CUSTOMER_ID").setDbName("CUSTOMER_NO").setHidden();
		addField(blk, "CUSTOMER_NAME").setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setSize(20).setLabel("CUSTMYJOBLISTSM: Customer");
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
		addField(defblk,"WOEMPID");
		addField(defblk,"WOTOEMP");

		blk.setView("ACTIVE_SEPARATE");
		blk.defineCommand("Active_Separate_API","Start_Order__,Work__");
		cmdbar.addCustomCommand("START",  "ACTWOSTA: Started");
		cmdbar.addCustomCommand("WORK",  "ACTWOWRK: Work Done");
		cmdbar.addCustomCommand("PRINT",  "ACTWOPRN: Print");

		tbl = newASPTable( blk );

		rowset = blk.getASPRowSet();

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

		appendJavaScript( "function customizeACTIVEWOSM(obj,id)",
						  "{",
						  "   customizeBox(id);",
						  "}\n");

		appendJavaScript( "function findRecord(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'SEARCH';",
						  "}\n");


		// mutable attribute
		name = translate(getDescription());

		// call the init()
		init();
	}


	protected void init()// throws FndException
	{

		if (DEBUG) debug(this+": ActiveWorkOrdersSM.init()");

		// Read values from the profile   
		org_code         = readProfileValue("ORG_CODE");
		prio             = readProfileValue("PRIORITY_ID");
		work_type        = readProfileValue("WORK_TYPE_ID");
		work_leader_sign = readProfileValue("WORK_LEADER_SIGN");
		group_id         = readProfileValue("GROUP_ID");
		state            = readProfileValue("STATE");
		contract         = readProfileValue("CONTRACT");   
		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);
		name             = readProfileValue( "ACTIVEWOSMNAME", name);
		plan_s_date      = readProfileValue("PLAN_S_DATE", "");
		plan_f_date      = readProfileValue("PLAN_F_DATE", "");
		customer_id      = readProfileValue("CUSTOMER_ID");
		object_id        = readProfileValue("MCH_CODE");
		symptom          = readProfileValue("ERR_SYMPTOM");

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

		if (DEBUG) debug(this+": ActiveWorkOrdersSM.run()");

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

		if (!Str.isEmpty(prio))
		{
			qry.addWhereCondition("PRIORITY_ID = ?");
                        qry.addParameter(this,"PRIORITY_ID",prio);
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

		if (!Str.isEmpty(customer_id))
                {
                    qry.addWhereCondition("CUSTOMER_NO like ?");
                    qry.addParameter(this,"CUSTOMER_ID",customer_id);
                }
		if (!Str.isEmpty(object_id))
                {
                    qry.addWhereCondition("MCH_CODE like ?");
                    qry.addParameter(this,"MCH_CODE",object_id);
                }
		if (!Str.isEmpty(symptom))
                {
                    qry.addWhereCondition("ERR_SYMPTOM = ?");
                    qry.addParameter(this,"DUMMY",symptom);
                }

		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		// just perform the dabase transaktion if necessary
		if (autosearchflag || find)
			trans = submit(trans);

		db_rows = rowset.countDbRows();
		ctx.writeValue("DB_ROWS", db_rows+"" );
		ctx.writeFlag("SEARCH_ORDERS",find);
	}

	//==========================================================================
	//  Portlet description
	//==========================================================================

	public static String getDescription()
	{
		return "ACTIVEWOSMDESC: Active Work Orders SM";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": ActiveWorkOrdersSM.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}

		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("ACTIVEWORKORDERSSMTITLE1:  - You have " ) + db_rows +
			translate("ACTIVEWORKORDERSSMTITLE2:  Work Orders.");
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
			printText("ACTIVEWOSMINFO1: This view present active work orders depending on the search conditions given. "); 
			printText("ACTIVEWOSMINFO2: To see or alter actual conditions you can go to the ");
			printScriptLink("ACTIVEWOSMCUSTLINK: customize", "customizeACTIVEWOSM");  
			printText("ACTIVEWOSMINFO3:   page. ");
			printNewLine();
			endFont();
		}

		// Search criteria, not shown by default.
		if (dispsearchflag)
		{
			setFontStyle(BOLD);
			printText("ACTIVEWOORDERSEARCHCRITERIATEXT: Search criteria: ");
			endFont();

			printText("ACWOORSMWSITEACTIVEWOSM: Site: ");
			if (!Str.isEmpty(contract))
				printText(contract);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();

			printText("ACWOORSMWTYPEACTIVEWOSM: Work Type: ");
			if (!Str.isEmpty(work_type))
				printText(work_type);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();

			printText("ACWOORSMWLEAACTIVEWOSM: Work Leader: ");
			if (!Str.isEmpty(work_leader_sign))
				printText(work_leader_sign);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();

			printText("ACWOORSMPLANNEDSTARTDATEACTIVEWOSM: Planned Start Date +/-: ");
			if (!Str.isEmpty(plan_s_date))
				printText(plan_s_date);
			printNewLine();

			printText("ACWOORSMPLANNEDCOMPLETIONDATEACTIVEWOSM: Planned Completion Date +/-: ");
			if (!Str.isEmpty(plan_f_date))
				printText(plan_f_date);
			printNewLine();

			printText("ACWOORSMCUACTIVEWOSM: Customer: ");
			if (!Str.isEmpty(customer_id))
				printText(customer_id);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();

			printText("ACWOORSMOIACTIVEWOSM: Object Id: ");
			if (!Str.isEmpty(object_id))
				printText(object_id);
			else
				printText("ACTIVWOSMALL: All");
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

			printText("ACWOORSMSYMPACTIVEWOSM: Symptom: ");
			if (!Str.isEmpty(symptom))
				printText(symptom);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();

			printText("ACWOORSMPRIOACTIVEWOSM: Priority: ");
			if (!Str.isEmpty(prio))
				printText(prio);
			else
				printText("ACTIVWOSMALL: All");
			printNewLine();
			printNewLine();
		}
		// Show the table only if the button search is pressed or if the autosearch is checked.
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
						printSubmitLink("ACTIVEWORKORDERSSMFIRST: First","firstCust");
					else
						printText("ACTIVEWORKORDERSSMFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("ACTIVEWORKORDERSSMPRV: Previous","prevCust");
					else
						printText("ACTIVEWORKORDERSSMPRV: Previous");

					printSpaces(5);
					String rows = translate("ACTIVEWORKORDERSSMROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWORKORDERSSMLAST: Last","lastCust");
					else
						printText("ACTIVEWORKORDERSSMLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWORKORDERSSMNEXT: Next","nextCust");
					else
						printText("ACTIVEWORKORDERSSMNEXT: Next");

					printNewLine();
					printNewLine();

				}

			}
			else
			{
				if (!autosearchflag)
				{
					printSubmitLink("ACTIVEWORKORDERSSMSEARCH: Search","findRecord");
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
			printSubmitLink("ACTIVEWORKORDERSSMSEARCH: Search","findRecord");
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
			debug(this+": ActiveWorkOrdersSM.runCustom()");
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
			debug(this+": ActiveWorkOrdersSM.printCustomBody()");
			debug("\tprintCustomBody(): ActiveWorkOrdersSM");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOSMQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("CHOOSEWSITEACTIVEWOSM: Choose Site: ");
		printNewLine();
		printSpaces(5);
		printField("CONTRACTC",contract);
		printDynamicLOV("CONTRACTC","USER_ALLOWED_SITE_LOV","LOVSITEACTIVEWOSM: List of Site");     
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEWTYPEACTIVEWOSM: Choose Work Type: ");
		printNewLine();
		printSpaces(5);
		printField("WOTYPEC",work_type);
		printDynamicLOV("WOTYPEC","WORK_TYPE","LOVWTYPACTIVEWOSM: List of Work Type");      
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEWLEAACTIVEWOSM: Choose Work Leader:");
		printNewLine();
		printSpaces(5);
		printField("WOLEADERC", work_leader_sign);
		printDynamicLOV("WOLEADERC","EMPLOYEE_LOV","WRKLEADER: List of Work Leader","COMPANY=\\\'"+company+"\\\'");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("PLANNEDSTARTDATEACTIVEWOSM: Planned Start Date +/- ");
		printField("PLAN_S_DATE", plan_s_date, 5, 5);
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("PLANNEDCOMPLETIONDATEACTIVEWOSM: Planned Completion Date +/- ");
		printField("PLAN_F_DATE", plan_f_date, 5, 5);
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSECUACTIVEWOSM: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERIDC", customer_id, 20);
		printDynamicLOV("CUSTOMERIDC","CUSTOMER_INFO","LOVCUSTACTIVEWOSM: List of Customer");   
		printNewLine();
		printNewLine();
		printSpaces(5);  
		printText("CHOOSEOIACTIVEWOSM: Choose Object Id:");
		printNewLine();
		printSpaces(5);
		printField("OBJECTIDC", object_id, 20);
		printDynamicLOV("OBJECTIDC","MAINTENANCE_OBJECT","LOVOIDACTIVEWOSM: List of Object ID");
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEOGRIDACTIVEWOSM: Choose Object Group Id:");
		printNewLine();
		printSpaces(5);
		printField("GROUPIDC",group_id);
		printDynamicLOV("GROUPIDC","EQUIPMENT_OBJ_GROUP","LOVGRPIDACTIVEWOSM: List of Group ID");   
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEEDEPACTIVEWOSM: Choose Maintenance Organization:");
		printNewLine();
		printSpaces(5);
		printField("ORGCODEC",org_code);
		printDynamicLOV("ORGCODEC","ORG_CODE_ALLOWED_SITE_LOV","LOVORGCDACTIVEWOSM: List of Maintenance Organization");     
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSESYMPACTIVEWOSM: Choose Symptom:");
		printNewLine();
		printSpaces(5);
		printField("SYMPTOMC",symptom);
		printDynamicLOV("SYMPTOMC","WORK_ORDER_SYMPT_CODE","LOVSYMPTACTIVEWOSM: List of Symptom");          
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEPRIOACTIVEWOSM: Choose Priority:");
		printNewLine();
		printSpaces(5);
		printField("PRIOC",prio);
		printDynamicLOV("PRIOC","MAINTENANCE_PRIORITY","LOVPRIOACTIVEWOSM: List of Priority");
		printNewLine();
		printNewLine();

		printNewLine();
		printSpaces(5);   
		printText("ACTIVEWOSMCHSTATUS: Choose status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOSMFAULTREPORT", faultreportflag);
		printSpaces(3);
		printText("ACTIVEWOSMFAREPORTTEXT: Faultreport");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOSMWORKREQ", workreqflag);
		printSpaces(3);
		printText("ACTIVEWOSMWORKREQTEXT: Work Request");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOSMOBSERVED", observedflag);
		printSpaces(3);
		printText("ACTIVEWOSMOBSERVEDTEXT: Observed");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOSMUNDERPREP", underprepflag);
		printSpaces(3);
		printText("ACTIVEWOSMUNDERPREPTEXT: Under Preparation");
		printNewLine();
		printSpaces(5);

		printCheckBox("ACTIVEWOSMPREPARED", preparedflag);
		printSpaces(3);
		printText("ACTIVEWOSMPREPAREDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("ACTIVEWOSMRELEASED", releasedflag);
		printSpaces(3);
		printText("ACTIVEWOSMRELEASEDTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("ACTIVEWOSMSTARTED", startedflag);
		printSpaces(3);
		printText("ACTIVEWOSMSTARTEDTEXT: Started");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOSMWORKDONE", workdoneflag);
		printSpaces(3);
		printText("ACTIVEWOSMWORKDONETEXT: Work Done");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOSMREPORTED", reportedflag);
		printSpaces(3);
		printText("ACTIVEWOSMREPORTEDTEXT: Reported");
		printNewLine();  
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOSMPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("ACTIVEWOSMPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("ACTIVEWOSMNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKDISPSMSEARCHFLAG", dispsearchflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONEDISPSMSEARCHFLAGTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKSMINFOTEXT", infotextflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONESMTEXTINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKSMCUSTOMIZED", autosearchflag);
		printSpaces(3);
		printText("ACTIVEWOWORKDONESMTEXTCUSTOMIZED: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("LINEMAXHITACTIVEWOSM: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}

	public void submitCustomization()
	{

		// Read values from the fields in the customization page.
		contract         = readValue("CONTRACTC");
		org_code         = readValue("ORGCODEC");
		prio             = readValue("PRIOC");
		work_type        = readValue("WOTYPEC");
		work_leader_sign = readValue("WOLEADERC");
		group_id         = readValue("GROUPIDC");
		state            = readValue("STATEC");
		plan_s_date      = readValue("PLAN_S_DATE");
		plan_f_date      = readValue("PLAN_F_DATE");
		customer_id      = readValue("CUSTOMERIDC");
		object_id        = readValue("OBJECTIDC");
		symptom          = readValue("SYMPTOMC");
		name             = readValue("ACTIVEWOSMNAME");

		// Write values to the profile.
		writeProfileValue("CONTRACT",  readAbsoluteValue("CONTRACTC") );
		writeProfileValue("ORG_CODE",  readAbsoluteValue("ORGCODEC") );
		writeProfileValue("PRIORITY_ID",  readAbsoluteValue("PRIOC") );
		writeProfileValue("WORK_TYPE_ID",  readAbsoluteValue("WOTYPEC") );
		writeProfileValue("WORK_LEADER_SIGN",  readAbsoluteValue("WOLEADERC") );
		writeProfileValue("GROUP_ID",  readAbsoluteValue("GROUPIDC") );
		writeProfileValue("STATE", readAbsoluteValue("STATEC") );
		writeProfileValue("PLAN_S_DATE", readAbsoluteValue("PLAN_S_DATE") );
		writeProfileValue("PLAN_F_DATE", readAbsoluteValue("PLAN_F_DATE") );
		writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERIDC") );
		writeProfileValue("MCH_CODE",  readAbsoluteValue("OBJECTIDC") );
		writeProfileValue("ERR_SYMPTOM",  readAbsoluteValue("SYMPTOMC") );
		writeProfileValue("ACTIVEWOSMNAME",  readAbsoluteValue("ACTIVEWOSMNAME") );

		dispsearchflag = "TRUE".equals(readValue("ACTIVEWOWORKDISPSMSEARCHFLAG")); 
		writeProfileFlag("DISPSEARCHFLAG", dispsearchflag);

		autosearchflag = "TRUE".equals(readValue("ACTIVEWOWORKSMCUSTOMIZED")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);

		infotextflag = "TRUE".equals(readValue("ACTIVEWOWORKSMINFOTEXT")); 
		writeProfileFlag("INFOTEXT", infotextflag);


		faultreportflag = "TRUE".equals(readValue("ACTIVEWOSMFAULTREPORT"));
		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";
		writeProfileFlag("FAULTREPORTFLAG", faultreportflag);

		workreqflag = "TRUE".equals(readValue("ACTIVEWOSMWORKREQ"));
		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= " ";
		writeProfileFlag("WORKREQFLAG", workreqflag);

		observedflag = "TRUE".equals(readValue("ACTIVEWOSMOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";
		writeProfileFlag("OBSERVEDFLAG", observedflag);

		underprepflag = "TRUE".equals(readValue("ACTIVEWOSMUNDERPREP"));
		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = " ";
		writeProfileFlag("UNDERPREPFLAG", underprepflag);

		preparedflag = "TRUE".equals(readValue("ACTIVEWOSMPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";
		writeProfileFlag("PREPAREDFLAG", preparedflag);

		releasedflag = "TRUE".equals(readValue("ACTIVEWOSMRELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";
		writeProfileFlag("RELEASEDFLAG", releasedflag);


		startedflag = "TRUE".equals(readValue("ACTIVEWOSMSTARTED"));
		if (startedflag)
			started = "STARTED";
		else
			started	= " ";
		writeProfileFlag("STARTEDFLAG", startedflag);

		workdoneflag = "TRUE".equals(readValue("ACTIVEWOSMWORKDONE"));
		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = " ";
		writeProfileFlag("WORKDONEFLAG", workdoneflag);

		reportedflag = "TRUE".equals(readValue("ACTIVEWOSMREPORTED"));
		if (reportedflag)
			reported = "REPORTED";
		else
			reported = " ";
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
