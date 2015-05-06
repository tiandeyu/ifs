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
 * File        : MyJobListSM.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * MIBOSE  000526  Created.
 * MIBOSE  000606  Added setSize(20) for addField "Short Description".
 * PJONSE  000613  Removed static user.                
 * PJONSE  000628  Added qry.addWhereCondition("AUTHORIZE_CODE = '"+aut_code+"' OR WORK_LEADER_SIGN = '"+sign+"' OR SIGN ='"+sign+"'"). 
 * PJONSE  000704  Call Id: 45169. Added 'if (dummy != 2)' in protected void run();
 * PJONSE  000712  Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 * PJONSE  000831  Changed translation constant MYJOBLISTSMTITLE to MYJOBLISTSMTITLE1 and MYJOBLISTSMTITLE2. 
 * NUPELK  001101  Added the actions 'Started','Work' and 'Print' 
 * JEWILK  001115  Done Specified Functional Corrections(Doc.no 1006241)
 * ARWILK  001127  Fixed the translate problems in LOV's ( Added the prefix PCMWMYJLS ).
 * JIJOSE  001212  Removed spcific selection in LOV's
 * JIJOSE  001218  Distinct selection in query.
 * JIJOSE  010103  Changed Object LOV
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031023  (Call ID 108187) Fixed the problem of faultreport not getting checked.
 * VAGULK  040311  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables,
 *                 and Changed field name "CUSTOMER_NO " to "CUSTOMER_ID".
 * ARWILK  041025  Merged LCS-47169 and did some other corrections.
 * ------------------------------------------
 * NAMELK  060901  Merged Bug 58216, Eliminated SQL Injection security vulnerability.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * SHAFLK  080715  Bug 75697, Removed DEBUG value set to true.
 * SHAFLK  090525  Bug 83038, Modified run().
 * SHAFLK  090721  Bug 83855, Modified init(), submitCustomization() and run().
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

public class MyJobListSM extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.MyJobListSM");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPBlock   defblk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================
	private transient String   sign;
	private transient String   aut_code;
	private transient String   group_id;
	private transient String   object_id;
	private transient String   customer_id;
	private transient int      skip_rows;
	private transient int      db_rows;
	private transient boolean  find;
	private transient String   dummy;
	private transient String   name;
	private transient String   cmd;
	//private transient String  readcmd;

	private transient boolean disp_search;
	private transient boolean hide_info_text;
	private transient boolean search_auto;

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

	private transient String  faultreport;
	private transient String  workreq;
	private transient String  observed;
	private transient String  underprep;
	private transient String  prepared;
	private transient String  released;
	private transient String  started;
	private transient String  workdone;
	private transient String  reported;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	//==========================================================================
	//  Construction
	//==========================================================================

	public MyJobListSM( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": MyJobListSM.<init> :"+portal+","+clspath);
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
		if (DEBUG) debug(this+": MyJobListSM.construct()");
		return super.construct();
	}

	/**
	 * Called after define() while changing state from UNDEFINED to DEFINED.
	 */
	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": MyJobListSM.doFreeze()");
		super.doFreeze();
	}


	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": MyJobListSM.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	// 
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": MyJobListSM.preDefine()");

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
		addField(blk, "AUTHORIZE_CODE").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO","NEWWIN").setLabel("PCMWMYJLSMWONO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2lightSM.page","WO_NO","NEWWIN").setLabel("PCMWMYJLSMWONO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "ERR_DESCR").setSize(20).setLabel("PCMWMYJLSMSHORTDESC: Short Description");
		addField(blk, "MCH_CODE").setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,CONTRACT","NEWWIN").setLabel("PCMWMYJLSMOBJECTID: Object ID");
		addField(blk, "STATE").setLabel("PCMWMYJLSMSTATUS: Status");
		addField(blk, "PRIORITY_ID").setLabel("PCMWMYJLSMPRIORITY: Prio");  
		addField(blk,"CUSTOMER_ID").setDbName("CUSTOMER_NO").setHidden();
		addField(blk, "CUSTOMER_NAME").setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setSize(20).setLabel("PCMWMYJLSMCUST: Customer");
		addField(blk, "CHAR_CODE30").setFunction("CHR(30)").setHidden();
		addField(blk, "CHAR_CODE31").setFunction("CHR(31)").setHidden();
		addField(blk, "RESULT_KEY").setFunction("' '").setHidden();
                addField(blk, "WORK_LEADER_SIGN").setHidden();
                addField(blk, "JOB_SIGN").setHidden();
                addField(blk, "WORK_MASTER_SIGN").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "USERSIGN");
		addField(defblk, "USERAUTCODE");
		// Added NUPE/LK
		addField(defblk,"ATTR0");
		addField(defblk,"ATTR1");
		addField(defblk,"ATTR2");
		addField(defblk,"ATTR3");
		addField(defblk,"ATTR4");

		blk.setView("WORK_ORDER_ROLE_ACTIVEWO");
		blk.defineCommand("Active_Separate_API","Start_Order__,Work__");
		cmdbar.addCustomCommand("START",  "ACTWOSTA: Started");
		cmdbar.addCustomCommand("WORK",  "ACTWOWRK: Work Done");
		cmdbar.addCustomCommand("PRINT",  "ACTWOPRN: Print");

		tbl = newASPTable( blk );

		rowset = blk.getASPRowSet();

		dummy = "1";


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

		appendJavaScript( "function customizeMyJobListSM(obj,id)",
						  "{",
						  "   customizeBox(id);",
						  "}\n");

		init();
	}


	protected void init()// throws FndException
	{
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());


		if (DEBUG) debug(this+": MyJobListSM.init()");

		sign             = readProfileValue("SIGN");
		aut_code         = readProfileValue("AUTCODE");
		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);
		name             = readProfileValue( "MYJOBLISTSMNAME", name);
		group_id         = readProfileValue("GROUP_ID");
		object_id        = readProfileValue("MCH_CODE");
		customer_id      = readProfileValue("CUSTOMER_ID");

		disp_search = readProfileFlag("MYJOBSMDISPSEARCH", false);
		hide_info_text = readProfileFlag("MYJOBSMHIDEINFOTEXT", false);
		search_auto = readProfileFlag("MYJOBSMSEARCHAUTO", false);

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

		if (!Str.isEmpty(sign))
			sign      = ctx.readValue("SIGN");

		if (!Str.isEmpty(aut_code))
			aut_code  = ctx.readValue("AUTCODE");

		if (!Str.isEmpty(group_id))
			writeProfileValue("GROUP_ID", group_id);

		if (!Str.isEmpty(object_id))
			writeProfileValue("MCH_CODE", object_id);

		if (!Str.isEmpty(customer_id))
			writeProfileValue("CUSTOMER_ID", customer_id);

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		cmd = readValue("CMD");

		if (Str.isEmpty(cmd))
		{
			if (cmdBarCustomCommandActivated())
				cmd = getCmdBarCustomCommandId();
			else if (cmdBarStandardCommandActivated())
				cmd = getCmdBarStandardCommandId();
		}

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

		if (DEBUG) debug(this+": MyJobListSM.run()");

		if (Str.isEmpty(dummy))
		{
			dummy = "1";
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

		if (Str.isEmpty(aut_code))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "USERAUTCODE");

			deftrans.addCommand("AUTCODE", cmd);

			deftrans = perform(deftrans);        

			aut_code = deftrans.getValue("AUTCODE/DATA/USERAUTCODE");
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

		if (search_auto || find)
		{
			ASPQuery  qry   = trans.addEmptyQuery(blk); 
			qry.setSelectExpression( "OBJID", "DISTINCT OBJID" );
			qry.setOrderByClause("STATUS_SEQUENCE, PRIORITY_ID"); 
			qry.includeMeta("ALL");

			if ("1".equals(dummy))
			{
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
                            qry.addWhereCondition("AUTHORIZE_CODE = ? OR WORK_LEADER_SIGN = ? OR SIGN = ? OR JOB_SIGN = ? OR WORK_MASTER_SIGN = ? ");
                            qry.addParameter(this,"AUTHORIZE_CODE", aut_code);
                            qry.addParameter(this,"WORK_LEADER_SIGN", sign);
                            qry.addParameter(this,"SIGN", sign);
                            qry.addParameter(this,"JOB_SIGN", sign);
                            qry.addParameter(this,"WORK_MASTER_SIGN", sign);
                            if (!Str.isEmpty(customer_id) )
                            {
                               qry.addWhereCondition("CUSTOMER_NO like ?");
                               qry.addParameter(this,"CUSTOMER_NO", customer_id);
                            }
			}
			else
			{
				if ("2".equals(dummy))
				{
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
                                        qry.addWhereCondition("AUTHORIZE_CODE = ? OR WORK_LEADER_SIGN = ? OR SIGN = ? OR JOB_SIGN = ? OR WORK_MASTER_SIGN = ? ");
                                        qry.addParameter(this,"AUTHORIZE_CODE", aut_code);
                                        qry.addParameter(this,"WORK_LEADER_SIGN", sign);
                                        qry.addParameter(this,"SIGN", sign);
                                        qry.addParameter(this,"JOB_SIGN", sign);
                                        qry.addParameter(this,"WORK_MASTER_SIGN", sign);
				}

				if (!Str.isEmpty(group_id))
                                {
                                    qry.addWhereCondition("GROUP_ID = ? ");
                                    qry.addParameter(this,"GROUP_ID", group_id);
                                }
                                if (!Str.isEmpty(object_id))
                                {
                                    qry.addWhereCondition("MCH_CODE like ? ");
                                    qry.addParameter(this,"MCH_CODE", object_id);
                                }
                                if (!Str.isEmpty(customer_id))
                                {
                                    qry.addWhereCondition("CUSTOMER_NO like ? ");
                                    qry.addParameter(this,"CUSTOMER_NO", customer_id);
                                }
			}

			qry.setBufferSize(size);
			qry.skipRows(skip_rows);

			submit(trans);

			db_rows = blk.getASPRowSet().countDbRows();
			ctx.writeValue("DB_ROWS", db_rows+"" );
			ctx.writeFlag("SEARCH_ORDERS",find);
		}
	}

	//==========================================================================
	//  
	//==========================================================================

	public static String getDescription()
	{
		return "PCMWMYJLSMDESC: My Job List SM";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": MyJobListSM.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("PCMWMYJLSMTITLE1:  - You have " ) + db_rows +
			translate("PCMWMYJLSMTITLE2:  Work Orders.");
		}

		else
			return name;   

	}

	public void printContents()
	{
		printNewLine();

		if (disp_search)
		{
			printText("PCMWMYJLSMSEARCH: Search Criteria:");  
			printNewLine();
			printText("PCMWMYJLSMMCUST1: Customer:");
			printSpaces(2);
			if (!Str.isEmpty(customer_id))
				printText(customer_id);
			else
				printText("PCMWMYJLSMALL: All");  
			printNewLine();

			printText("PCMWMYJLSMOBJ2: Object ID:");
			printSpaces(2);
			if (!Str.isEmpty(object_id))
				printText(object_id);
			else
				printText("MYJOBSMALL: All"); 
			printNewLine();

			printText("PCMWMYJLSMOBJGRP: Object Group ID:");
			printSpaces(2);
			if (!Str.isEmpty(group_id))
				printText(group_id);
			else
				printText("PCMWMYJLSMALL: All");  
			printNewLine();
			printNewLine();
		}

		if (!hide_info_text)
		{
			setFontStyle(ITALIC);
			printText("PCMWMYJLSMINFO1: Active work orders where you are work leader, coordinator or planned craft. "); 
			printText("PCMWMYJLSMINFO2: In  ");
			printScriptLink("PCMWMYJLSMCUSTLINK: customize", "customizeMyJobListSM");  
			printText("PCMWMYJLSMINFO3:    page you can select customer, object or what status to view. ");
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

				if (cantperform)
				{
					setFontStyle(BOLD);   
					printText("PCMWMYJLSMCATPFMOP: Requested operation is not allowed for the selection");  
					endFont();
					cantperform = false;
				}

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("PCMWMYJLSMFIRST: First","firstCust");
					else
						printText("PCMWMYJLSMFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("PCMWMYJLSMPRV: Previous","prevCust");
					else
						printText("PCMWMYJLSMPRV: Previous");

					printSpaces(5);
					String rows = translate("PCMWMYJLSMROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("PCMWMYJLSMLAST: Last","lastCust");
					else
						printText("PCMWMYJLSMLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("PCMWMYJLSMNEXT: Next","nextCust");
					else
						printText("PCMWMYJLSMNEXT: Next");

					printNewLine();
					printNewLine();

				}
			}
			else
			{
				if (!search_auto)
				{
					printSubmitLink("PCMWMYJLSMFIND: Search","findCust");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("PCMWMYJLSMNODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("PCMWMYJLSMFIND: Search","findCust");
			printNewLine();
			printNewLine();
		}
	}

	public boolean canCustomize()
	{
		return true;
	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": MyJobListSM.printCustomBody()");
			debug("\tprintCustomBody(): MyJobListSM");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("PCMWMYJLSMQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("PCMWMYJLSMMCCU: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERIDC", customer_id, 20);
		printDynamicLOV("CUSTOMERIDC","CUSTOMER_INFO","PCMWMYJLSMCUSTLOV: List of Customer");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printText("PCMWMYJLSMCOI: Choose Object ID:");
		printNewLine();
		printSpaces(5);
		printField("OBJECTIDC", object_id, 20);
		printDynamicLOV("OBJECTIDC","MAINTENANCE_OBJECT","PCMWMYJLSMOBJLOV: List of Object ID");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printText("PCMWMYJLSMGROUPID: Choose Object Group ID:");
		printNewLine();
		printSpaces(5);
		printField("GROUPIDC", group_id, 20);
		printDynamicLOV("GROUPIDC","EQUIPMENT_OBJ_GROUP","PCMWMYJLSMGRPLOV: List of Group ID");
		printNewLine();
		printNewLine();

		printNewLine();
		printSpaces(5);   
		printText("PCMWMYJLSMCHSTATUS: Choose status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTSMFAULTREPORT", faultreportflag);
		printSpaces(3);
		printText("PCMWMYJLSMFARETEXT: Faultreport");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTSMWORKREQ", workreqflag);
		printSpaces(3);
		printText("PCMWMYJLSMWKREQTEXT: Work Request");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTSMOBSERVED", observedflag);
		printSpaces(3);
		printText("PCMWMYJLSMOBDTEXT: Observed");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTSMUNDERPREP", underprepflag);
		printSpaces(3);
		printText("PCMWMYJLSMUNDPREPTEXT: Under Preparation");
		printNewLine();
		printSpaces(5);

		printCheckBox("MYJOBLISTSMPREPARED", preparedflag);
		printSpaces(3);
		printText("PCMWMYJLSMPREPDTEXT: Prepared");
		printNewLine();  

		printSpaces(5);
		printCheckBox("MYJOBLISTSMRELEASED", releasedflag);
		printSpaces(3);
		printText("PCMWMYJLSMRELTEXT: Released");
		printNewLine();  

		printSpaces(5);
		printCheckBox("MYJOBLISTSMSTARTED", startedflag);
		printSpaces(3);
		printText("PCMWMYJLSMSTDTEXT: Started");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTSMWORKDONE", workdoneflag);
		printSpaces(3);
		printText("PCMWMYJLSMWRKDTEXT: Work Done");
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBLISTSMREPORTED", reportedflag);
		printSpaces(3);
		printText("PCMWMYJLSMREPDTEXT: Reported");
		printNewLine();  
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("PCMWMYJLSMPOLETCON: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("PCMWMYJLSMPOETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("MYJOBLISTSMNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBSMDISPSEARCH", disp_search);
		printSpaces(3);
		printText("PCMWMYJLSMDISSEACRI: Display search criteria");
		printNewLine();
		printNewLine();   

		printSpaces(5);
		printCheckBox("MYJOBSMHIDEINFOTEXT", hide_info_text);
		printSpaces(3);
		printText("PCMWMYJLSMHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("MYJOBSMSEARCHAUTO", search_auto);
		printSpaces(3);
		printText("PCMWMYJLSMSEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("PCMWMYJLSMLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}

	public void submitCustomization()
	{

		dummy = "2";

		name        = readValue("MYJOBLISTSMNAME");
		group_id    = readValue("GROUPIDC");
		object_id   = readValue("OBJECTIDC");
		customer_id = readValue("CUSTOMERIDC");

		disp_search = "TRUE".equalsIgnoreCase(readValue("MYJOBSMDISPSEARCH"));
		hide_info_text = "TRUE".equalsIgnoreCase(readValue("MYJOBSMHIDEINFOTEXT"));
		search_auto = "TRUE".equalsIgnoreCase(readValue("MYJOBSMSEARCHAUTO"));

		writeProfileFlag("MYJOBSMDISPSEARCH", disp_search);
		writeProfileFlag("MYJOBSMHIDEINFOTEXT", hide_info_text);
		writeProfileFlag("MYJOBSMSEARCHAUTO", search_auto);


		writeProfileValue("MYJOBLISTSMNAME",  readAbsoluteValue("MYJOBLISTSMNAME") );
		writeProfileValue("GROUP_ID",  readAbsoluteValue("GROUPIDC") );
		writeProfileValue("MCH_CODE",  readAbsoluteValue("OBJECTIDC") );
		writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERIDC") );

		faultreportflag = "TRUE".equals(readValue("MYJOBLISTSMFAULTREPORT"));
		if (faultreportflag)
			faultreport = "FAULTREPORT";
		else
			faultreport	= " ";
		writeProfileFlag("FAULTREPORTFLAG", faultreportflag);

		workreqflag = "TRUE".equals(readValue("MYJOBLISTSMWORKREQ"));
		if (workreqflag)
			workreq = "WORKREQUEST";
		else
			workreq	= " ";
		writeProfileFlag("WORKREQFLAG", workreqflag);

		observedflag = "TRUE".equals(readValue("MYJOBLISTSMOBSERVED"));
		if (observedflag)
			observed = "OBSERVED";
		else
			observed = " ";
		writeProfileFlag("OBSERVEDFLAG", observedflag);

		underprepflag = "TRUE".equals(readValue("MYJOBLISTSMUNDERPREP"));
		if (underprepflag)
			underprep = "UNDERPREPARATION";
		else
			underprep = " ";
		writeProfileFlag("UNDERPREPFLAG", underprepflag);

		preparedflag = "TRUE".equals(readValue("MYJOBLISTSMPREPARED"));
		if (preparedflag)
			prepared = "PREPARED";
		else
			prepared = " ";
		writeProfileFlag("PREPAREDFLAG", preparedflag);

		releasedflag = "TRUE".equals(readValue("MYJOBLISTSMRELEASED"));
		if (releasedflag)
			released = "RELEASED";
		else
			released = " ";
		writeProfileFlag("RELEASEDFLAG", releasedflag);

		startedflag = "TRUE".equals(readValue("MYJOBLISTSMSTARTED"));
		if (startedflag)
			started = "STARTED";
		else
			started	= " ";
		writeProfileFlag("STARTEDFLAG", startedflag);

		workdoneflag = "TRUE".equals(readValue("MYJOBLISTSMWORKDONE"));
		if (workdoneflag)
			workdone = "WORKDONE";
		else
			workdone = " ";
		writeProfileFlag("WORKDONEFLAG", workdoneflag);

		reportedflag = "TRUE".equals(readValue("MYJOBLISTSMREPORTED"));
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
