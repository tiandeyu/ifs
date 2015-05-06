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
 * File        : WorkOrderQuotationList.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    PJONSE    00-05-29 Created.
 *    PJONSE    00-05-29 Added VALID_TO2.
 *    PJONSE    00-07-04 Call Id: 45169. Added 'if (dummy != 2)' in protected void run().
 *    PJONSE	00-07-12 Converted dummy to String and added if( Str.isEmpty(dummy)) in protected void run().
 *    PJONSE    00-08-31 Changed translation constant WOQLISTQQID to WOQLISTQQID1 and WOQLISTQQID2. 
 *    OSRYSE 	00-11-01 Changed portlet according to new development guidelines.
 *    ARWILK    00-11-07 Removed the select boxes (In customize page) and added dynamic LOVs.
 *    JIJOSE	00-11-29 Wrong method when reading users contract
 *    JIJOSE    00-12-18 Search function updated
 *    NUPELK    00-12-20 (#54048)Modified 'printContents' function's 'search criteria' section to support translations.
 *    SHFELK    01-06-15 Modified to replace '+' sign in valid_from and valid_to if the user enter it.
 *    THWILK    03-10-22 Call ID 108231, Allowed the possibility of customizing a site.
 *    VAGULK    040311   Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * -----------------------------------------
 *    NAMELK    060901   Merged Bug 58216, Eliminated SQL Injection security vulnerability. 
 *    AMDILK    070320   Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile 
 *    AMDILK    070531   Modified submitCustomization()
 *    SHAFLK    080715  Bug 75697, Removed DEBUG value set to true.
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


public class WorkOrderQuotationList extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.WorkOrderQuotationList");


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

	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;

	private transient String  newstate;
	private transient String  printed;
	private transient String  followup;
	private transient String  rejected;
	private transient String  hold;
	private transient String  confirmed;
	private transient String  revise;

	private transient ASPTransactionBuffer  trans;
	private transient boolean bCon; 

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int     size;

	private transient String  contract;
	private transient String  salesman;
        private transient String  customer_id;   
	private transient String  valid_to;
	private transient String  valid_from; 

	// State flags to use as search criteria
	private transient boolean newflag;
	private transient boolean printedflag;
	private transient boolean followupflag;
	private transient boolean rejectedflag;
	private transient boolean holdflag;
	private transient boolean confirmedflag;
	private transient boolean reviseflag;

	// The name of the portlet
	private transient String  name;

	// Customization flags
	private transient boolean autosearchflag;	 // If the portlet should be automaticly populated
	private transient boolean infotextflag;	 // Hide the information text
	private transient boolean dispsearchflag;	 // Display search criteria

	//==========================================================================
	//  Construction
	//==========================================================================

	public WorkOrderQuotationList( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": WorkOrderQuotationList.<init> :"+portal+","+clspath);
	}

	//==========================================================================
	//  Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": WorkOrderQuotationList.preDefine()");

		ctx = getASPContext ();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();


		addField(blk, "CONTRACT").setLabel("WOQLISTSITE: Site");

		addField(blk, "QUOTATION_ID").setHyperlink("pcmw/WorkOrderQuotation.page","QUOTATION_ID").setLabel("WOQLISTQID: Q Id");

		addField(blk, "QUOTATION_DESCRIPTION").setSize(20).setLabel("WOQLISTDESC: Description");

		addField(blk,"CUSTOMER_ID").setHyperlink("enterw/CustomerInfo.page","CUSTOMER_ID").setLabel("WOQLISTCUSTOMERNO: Customer No");

		addField(blk, "CUSTOMER_NAME").setSize(20).setFunction("Customer_Info_API.Get_Name(:CUSTOMER_ID)").setLabel("WOQLISTCUSTOMER: Customer");

		addField(blk, "VALID_TO", "Date").setLabel("WOQLISTVALIDTODATE: Valid To");

		addField(blk, "STATE").setLabel("WOQLISTSTATUS: Status");

		addField(blk, "OBJSTATE").setHidden();

		addField(blk, "TEMP_VALID", "Number").setFunction("''").setHidden();

		addField(blk, "SALESMAN_CODE").setHidden();


		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "DEFVALIDTO");
		addField(defblk, "DEFVALIDFROM");


		blk.setView("WORK_ORDER_QUOTATION_OVERVIEW");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();


		appendJavaScript( "function customizeWorkOrderQuotationList(obj,id)",
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
		name = translate(getDescription());

		// call the init()
		init();   

	}


	protected void init()
	{
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());    

		if (DEBUG) debug(this+": WorkOrderQuotationList.init()");

		contract       = readProfileValue("CONTRACT");   
                customer_id    = readProfileValue("CUSTOMER_ID");
		salesman       = readProfileValue("SALESMAN_CODE");
		name           = readProfileValue("WOQLISTNAME", name);
		valid_to       = readProfileValue("VALID_TO2", "");
		valid_from     = readProfileValue("VALID_FROM", "");

		newflag        = readProfileFlag ( "NEWFLAG", true );
		printedflag    = readProfileFlag ( "PRINTEDFLAG", true );
		followupflag   = readProfileFlag ( "FOLLOWUPFLAG", true );
		rejectedflag   = readProfileFlag ( "REJECTEDFLAG", true );
		holdflag       = readProfileFlag ( "HOLDFLAG", true );
		confirmedflag  = readProfileFlag ( "CONFIRMEDFLAG", true );
		reviseflag     = readProfileFlag ( "REVISEFLAG", true );

		no_of_hits    = readProfileValue("NO_HITS", default_size+"");
		size          = Integer.parseInt(no_of_hits);

		autosearchflag   = readProfileFlag ( "CUSTOMIZED", false ); 
		infotextflag     = readProfileFlag ( "INFOTEXT", false );   
		dispsearchflag   = readProfileFlag ( "DISPSEARCHFLAG", false );

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (newflag)
			newstate = "New";
		else
			newstate = " ";

		if (printedflag)
			printed = "Printed";
		else
			printed	= " ";

		if (followupflag)
			followup = "FollowUp";
		else
			followup = " ";

		if (rejectedflag)
			rejected = "Rejected";
		else
			rejected = " ";

		if (holdflag)
			hold = "Hold";
		else
			hold = " ";

		if (confirmedflag)
			confirmed = "Confirmed";
		else
			confirmed = " ";

		if (reviseflag)
			revise = "Revise";
		else
			revise = " ";


		if (Str.isEmpty(contract))
		{
			contract   = ctx.readValue("CONTRACT");
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

	//==========================================================================
	//  Normal mode
	//==========================================================================

	/**
	* Run the business logic here. Do not call submit() or perform() located
	* in ASPManager class. Use versions from the super class.
	*/
	protected void run()
	{

		if (DEBUG) debug(this+": WorkOrderQuotationList.run()");

		if (Str.isEmpty(contract))
		{
			if (bCon==false)
			{

				ASPManager mgr = getASPManager();
				ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

				ASPCommand cmd = mgr.newASPCommand();

				cmd.defineCustomFunction(this, "User_Default_API.Get_Contract", "DEFCONTRACT");
				deftrans.addCommand("SITE", cmd);

				deftrans = mgr.perform(deftrans);        

				contract = deftrans.getValue("SITE/DATA/DEFCONTRACT");
			}
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery             qry   = trans.addQuery(blk);

		qry.setOrderByClause("VALID_TO");
		qry.includeMeta("ALL");


		qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");
		qry.addWhereCondition("OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? OR OBJSTATE = ? ");
                qry.addParameter(this,"OBJSTATE", newstate);
                qry.addParameter(this,"OBJSTATE", printed);
                qry.addParameter(this,"OBJSTATE", followup);
                qry.addParameter(this,"OBJSTATE", rejected);
                qry.addParameter(this,"OBJSTATE", hold);
                qry.addParameter(this,"OBJSTATE", confirmed);
                qry.addParameter(this,"OBJSTATE", revise);


		if (!Str.isEmpty(contract))
		{
		    qry.addWhereCondition("CONTRACT like ? ");
                    qry.addParameter(this,"CONTRACT", contract);

		}
                if (!Str.isEmpty(customer_id))
		{
                    qry.addWhereCondition("CUSTOMER_ID like ? ");
                    qry.addParameter(this,"CUSTOMER_ID", customer_id);
		}

		if (!Str.isEmpty(salesman))
		{
                    qry.addWhereCondition("SALESMAN_CODE = ? ");
                    qry.addParameter(this,"SALESMAN_CODE", salesman);
		}

		if (!Str.isEmpty(valid_from))
		{
			String temp_valid_from = Str.replace(valid_from,"+","");
			if (Str.toInt(temp_valid_from) > 0)
			{
                            qry.addWhereCondition("trunc(VALID_FROM) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                            qry.addParameter(this,"TEMP_VALID", temp_valid_from);
			}
			else
			{
                            qry.addWhereCondition("trunc(VALID_FROM) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                            qry.addParameter(this,"TEMP_VALID", temp_valid_from);
			}
		}

		if (!Str.isEmpty(valid_to))
		{
			String temp_valid_to = Str.replace(valid_to,"+","");
			if (Str.toInt(temp_valid_to) > 0)
			{
                            qry.addWhereCondition("trunc(VALID_TO) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? ");
                            qry.addParameter(this,"TEMP_VALID", temp_valid_to);
			}
			else
			{
                            qry.addWhereCondition("trunc(VALID_TO) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                            qry.addParameter(this,"TEMP_VALID", temp_valid_to);
			}
		}


		qry.setBufferSize(size);
		qry.skipRows(skip_rows);

		// just perform the dabase transaction if necessary
		if (autosearchflag || find)
			trans = submit(trans);

		db_rows = blk.getASPRowSet().countDbRows();
		getASPContext().writeValue("DB_ROWS", db_rows+"" );

		ctx.writeValue("CONTRACT", contract );
		ctx.writeFlag("SEARCH_ORDERS",find);

	}


	//==========================================================================
	//  Portlet description
	//==========================================================================

	public static String getDescription()
	{
		return "WOQLISTTITEL: Work Order Quotation List ";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": WorkOrderQuotationList.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("WOQLISTQQID1: - You have " ) + db_rows +
			translate("WOQLISTQQID2:  Work Orders.");
		}

		else if (mode==ASPPortal.MAXIMIZED)
			return name;
		else
			return translate("WOQLISTCUSTOMIZE: Customize ") + name;             

	}


	public void printContents()
	{
		// Information text, shown by default.        
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("WOQLISTINFO1: Here you can find current work order quotations. To see more "); 
			printText("WOQLISTINFO2: specific information you can ");
			printScriptLink("WOQLISTCUSTLINK: customize", "customizeWorkOrderQuotationList");
			printSpaces(1);
			printText("WOQLISTINFO3: and give more search data.");
			printNewLine();
			endFont();
		}

		// Search criteria, not shown by default.
		if (dispsearchflag)
		{
			setFontStyle(BOLD);
			printText("WOORQULISEARCHCRITERIATEXT: Search criteria: ");
			endFont();
			printText("WOORQULISITETEXT: Site: ");
			if (!Str.isEmpty(contract))
				printText(contract);
			else
				printText("ACTIVWOALL: All");

			printNewLine(); 
                        printText("WOQLISTCCCO: Choose Customer: ");
			if (!Str.isEmpty(customer_id))
				printText(customer_id);
			else
				printText("ACTIVWOALL: All");

			printNewLine();
			printText("WOQLISTCSMCO: Choose Salesman: ");
			if (!Str.isEmpty(salesman))
				printText(salesman);
			else
				printText("ACTIVWOALL: All");

			printNewLine();
			printText("WOQLISTVALIDFROMCCO: Valid from +/- ");
			if (!Str.isEmpty(valid_from))
				printText(valid_from);
			printNewLine();
			printText("WOQLISTVALIDTOCCO: Valid to +/- ");
			if (!Str.isEmpty(valid_to))
				printText(valid_to);
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
						printSubmitLink("WOQLISTFIRST: First","firstCust");
					else
						printText("WOQLISTFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("WOQLISTPRV: Previous","prevCust");
					else
						printText("WOQLISTPRV: Previous");

					printSpaces(5);
					String rows = translate("WOQLISTROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("WOQLISTLAST: Last","lastCust");
					else
						printText("WOQLISTLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("WOQLISTNEXT: Next","nextCust");
					else
						printText("WOQLISTNEXT: Next");

					printNewLine();
					printNewLine();

				}
			}
			else
			{
				if (!autosearchflag)
				{
					printSubmitLink("WOORQULISEARCH: Search","findRecord");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("WOORQULINODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("WOORQULISEARCH: Search","findRecord");
			printNewLine();
			printNewLine();
		}

	}


	//==========================================================================
	//  Customization section
	//==========================================================================

	public void runCustom()
	{
		if (DEBUG) debug(this+": WorkOrderQuotationList.runCustom()");

		ASPManager mgr = getASPManager();
		trans          = mgr.newASPTransactionBuffer();

		trans = mgr.perform(trans);

	}


	public void printCustomBody() throws FndException
	{
		if (DEBUG)
		{
			debug(this+": WorkOrderQuotationList.printCustomBody()");
			debug("\tprintCustomBody(): WorkOrderQuotationList");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("WOQLISTQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("WOQLISTCS: Choose Site: ");
		printNewLine();
		printSpaces(5);
		printField("CONTRACTC",contract);
		printDynamicLOV("CONTRACTC","USER_ALLOWED_SITE_LOV","WOQLISTLOVSITE: List of Site");    
		printNewLine();
		printNewLine();
                printSpaces(5);     
		printText("WOQLISTCC: Choose Customer:");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERC", customer_id, 20);
		printDynamicLOV("CUSTOMERC","CUSTOMER_INFO","WOQLISTLOVCUST: List of Customer");    
		printNewLine();
		printNewLine();
		printSpaces(5);     
		printText("WOQLISTCSM: Choose Salesman:");
		printNewLine();
		printSpaces(5);
		printField("SALESMANC",salesman);
		printDynamicLOV("SALESMANC","SALES_PART_SALESMAN_LOV","WOQLISTLOVSALMAN: List of Salesman");        
		printNewLine();

		printNewLine();
		printSpaces(5);
		printText("WOQLISTVALIDFROMC: Valid from +/- ");
		printField("VALID_FROM", valid_from, 5, 5);
		printNewLine();

		printNewLine();
		printSpaces(5);
		printText("WOQLISTVALIDTOC: Valid to +/- ");
		printField("VALID_TO2", valid_to, 5, 5);
		printNewLine();

		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("WOQLISTSTATUSTEXT: Choose Status:");
		printNewLine();

		printSpaces(5);
		printCheckBox("WOQLISTNEW", newflag);
		printSpaces(3);
		printText("WOQLISTNEWTEXT: New");
		printNewLine();

		printSpaces(5);
		printCheckBox("WOQLISTPRINTED", printedflag);
		printSpaces(3);
		printText("WOQLISTPRINTEDTEXT: Printed");
		printNewLine();  

		printSpaces(5);
		printCheckBox("WOQLISTFOLLOWUP", followupflag);
		printSpaces(3);
		printText("WOQLISTFOLLOWUPTEXT: Follow Up");
		printNewLine();  

		printSpaces(5);
		printCheckBox("WOQLISTREJECTED", rejectedflag);
		printSpaces(3);
		printText("WOQLISTREJECTEDTEXT: Rejected");
		printNewLine();

		printSpaces(5);
		printCheckBox("WOQLISTHOLD", holdflag);
		printSpaces(3);
		printText("WOQLISTHOLDTEXT: Hold");
		printNewLine();

		printSpaces(5);
		printCheckBox("WOQLISTCONFIRMED", confirmedflag);
		printSpaces(3);
		printText("WOQLISTCONFIRMEDTEXT: Confirmed");
		printNewLine();

		printSpaces(5);
		printCheckBox("WOQLISTREVISE", reviseflag);
		printSpaces(3);
		printText("WOQLISTREVISETEXT: Revise");
		printNewLine();
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("WOQLISTPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("WOQLISTPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("WOQLISTNAME", name, 45);
		printNewLine();
		printNewLine();  

		printSpaces(5);
		printCheckBox("WOQLISDISPSMSEARCHFLAG", dispsearchflag);
		printSpaces(3);
		printText("WOQLISDISPSMSEARCHFLAGTEXT: Display search criteria");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKSMINFOTEXTFLAG", infotextflag);
		printSpaces(3);
		printText("WOQLISINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOWORKSMCUSTOMIZEDFLAG", autosearchflag);
		printSpaces(3);
		printText("WOQLISTEXTCUSTOMIZED: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("WOQLISTLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();

	}


	public void submitCustomization()
	{

		if (DEBUG)
		{
			debug(this+": WorkOrderQuotationListWorkOrderQuotationListWorkOrderQuotationList.submitCustomization()");
			debug("\tsubmitCustomization(): current values:\n\t\tcontract="+contract);
		}
		
		String  state;

		// Read values from the fields in the customization page.
		contract        = readValue("CONTRACTC");
		state           = readValue("STATEC");
                customer_id     = readValue("CUSTOMERC");
		salesman        = readValue("SALESMANC");
		valid_from      = readValue("VALID_FROM");
		valid_to        = readValue("VALID_TO2");
		name            = readValue("WOQLISTNAME");

		// Write values to the profile.
		writeProfileValue("CONTRACT", readAbsoluteValue("CONTRACTC") );
		writeProfileValue("STATE", readAbsoluteValue("STATEC") );
		writeProfileValue("SALESMAN_CODE",  readAbsoluteValue("SALESMANC") );
                writeProfileValue("CUSTOMER_ID",  readAbsoluteValue("CUSTOMERC") );
		writeProfileValue("VALID_FROM", readAbsoluteValue("VALID_FROM") );
		writeProfileValue("VALID_TO2", readAbsoluteValue("VALID_TO2") );
		writeProfileValue("WOQLISTNAME",  readAbsoluteValue("WOQLISTNAME") );

		if (Str.isEmpty(contract))
		{
			bCon=true;
		}

		dispsearchflag = "TRUE".equals(readValue("WOQLISDISPSMSEARCHFLAG")); 
		writeProfileFlag("DISPSEARCHFLAG", dispsearchflag);

		infotextflag = "TRUE".equals(readValue("ACTIVEWOWORKSMINFOTEXTFLAG")); 
		writeProfileFlag("INFOTEXT", infotextflag);

		autosearchflag = "TRUE".equals(readValue("ACTIVEWOWORKSMCUSTOMIZEDFLAG")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);


		newflag = "TRUE".equals(readValue("WOQLISTNEW"));
		if (newflag)
			newstate = "New";
		else
			newstate = " ";
		writeProfileFlag("NEWFLAG", newflag);

		printedflag = "TRUE".equals(readValue("WOQLISTPRINTED"));
		if (printedflag)
			printed = "Printed";
		else
			printed	= " ";
		writeProfileFlag("PRINTEDFLAG", printedflag);

		followupflag = "TRUE".equals(readValue("WOQLISTFOLLOWUP"));
		if (followupflag)
			followup = "FollowUp";
		else
			followup = " ";
		writeProfileFlag("FOLLOWUPFLAG", followupflag);

		rejectedflag = "TRUE".equals(readValue("WOQLISTREJECTED"));
		if (rejectedflag)
			rejected = "Rejected";
		else
			rejected = " ";
		writeProfileFlag("REJECTEDFLAG", rejectedflag);

		holdflag = "TRUE".equals(readValue("WOQLISTHOLD"));
		if (holdflag)
			hold = "Hold";
		else
			hold = " ";
		writeProfileFlag("HOLDFLAG", holdflag);

		confirmedflag = "TRUE".equals(readValue("WOQLISTCONFIRMED"));
		if (confirmedflag)
			confirmed = "Confirmed";
		else
			confirmed = " ";
		writeProfileFlag("CONFIRMEDFLAG", confirmedflag);

		reviseflag = "TRUE".equals(readValue("WOQLISTREVISE"));
		if (reviseflag)
			revise = "Revise";
		else
			revise = " ";
		writeProfileFlag("REVISEFLAG", reviseflag);


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