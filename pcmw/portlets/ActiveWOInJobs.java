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
 * File        : ActiveWOInJobs.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Created     : MIBOSE  000602  Created.
 * Modified    :
 * MIBOSE  000606  Added setSize(20) for addField "Short Description".         
 * PJONSE  000704  Call Id: 44404. Added WhereCondition 'CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)'.                    
 * PJONSE  000831  Changed translation constant ACTIVEWOINJOBSTITLE to ACTIVEWOINJOBSTITLE1 and ACTIVEWOINJOBSTITLE2.
 * OSRYSE  001101  Changed portlet according to new development guidelines.
 * JIJOSE  001212  Localize Search
 * NIVILK  010112  Modify search condition to filter Work Orders only with Objstate is "FAULTREPORT" and WO_NO < 600000.
 * INROLK  021217  Added Mch_code_contract as in Generic Work Order.
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031016  Call Id:107862 Changed the getDescription method to pass the ASPManager parameter there
 *                 by made it possible to use the translate method to translate a given string.
 * THWILK  031030  Call Id:109746 Added getDescription method sice it was overloaded by the call ID 107862. 
 * JEWILK  031110  Changed hyperlink of WO_NO to ActiveSeparateOvw.page. Added OBJSTATE 'WORKREQUEST' to where condition. Call 110483.
 * VAGULK  040301  Edge Developments ,Removed Clone() and doReset() methods and unused globle variables.
 * ARWILK  040727  Changed method calls to support the org_code key change and also Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
 * NAMELK  041108  Non Standard Translation Tags Corrected.
 * -------------------------------------------
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELK  060901  Merged bug 58216.
 * AMDILK  070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * AMDILK  070531  Modified submitCustomization()
 * SHAFLK  080715  Bug 75697, Removed DEBUG value set to true.
 * NIJALK  090820  Bug 85406, Modified run().
 * ---------------------------------------------------------------------------
 *
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

public class ActiveWOInJobs extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.ActiveWOInJobs");

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

	private transient int     skip_rows;
	private transient int     db_rows;
	private transient boolean find;
	private transient int     size;		 // Numeric representation of variable no_of_hits. Control how many rows to be shown in portlet. 

	private transient ASPTransactionBuffer trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	// Control how many rows to be shown in portlet.
	private transient String  no_of_hits;

	private transient String  contract;
	private transient String  company;
	private transient String  org_contract;
	private transient String  org_code;

	// Choosen name by a user or default name on creation.
	private transient String  name;

	// Customization flags
	private transient boolean autosearchflag;  // If the portlet should be automaticly populated
	private transient boolean infotextflag;	   // Hide the information text

	//==========================================================================
	//  Construction
	//==========================================================================

	public ActiveWOInJobs( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": ActiveWOInJobs.<init> :"+portal+","+clspath);
	}

	//==========================================================================
	// Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": ActiveWOInJobs.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "MCH_CODE_CONTRACT").setHidden();
		addField(blk, "REG_DATE").setHidden();
		addField(blk, "COMPANY").setHidden();
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparateOvw.page","WO_NO").setLabel("PCMWACTIVEWOINJOBSPORTWONO: Wo No"); // JEWILK Call 110483.
		addField(blk, "ERR_DESCR").setSize(20).setLabel("SHORTDESC: Short Description");
		addField(blk, "MCH_CODE").
		setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("PCMWACTIVEWOINJOBSPORTOBJECTID: Object ID");
		addField(blk, "PRIORITY_ID").setLabel("PCMWACTIVEWOINJOBSPORTPRIORITY: Prio");  
		addField(blk, "REPORTED_BY").setLabel("PCMWACTIVEWOINJOBSPORTREPORTEDBY: Reported By");
                addField(blk, "DUMMY","String").setFunction("''").setHidden();

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "USERREPORTEDBY");
		addField(defblk, "DEFCOMPANY");
		addField(defblk, "DEFORGCONTRACT");
		addField(defblk, "DEFORGCODE");

		blk.setView("ACTIVE_SEPARATE");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

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

		appendJavaScript( "function findRecord(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIND';",
						  "}\n");


		// mutable attribute
		ASPManager mgr = getASPManager();
		name = getDescription(mgr);

		init();
	}


	protected void init()// throws FndException
	{
		blk    = getASPBlock("MAIN");
		rowset = blk.getASPRowSet();
		tbl    = getASPTable(blk.getName());

		if (DEBUG) debug(this+": ActiveWOInJobs.init()");

		no_of_hits       = readProfileValue("NO_HITS", default_size+"");
		size             = Integer.parseInt(no_of_hits);
		name             = readProfileValue( "ACTIVEWOINJOBSNAME", name);
		company          = readProfileValue("COMPANY"); 
		org_contract     = readProfileValue("ORG_CONTRACT");      
		org_code         = readProfileValue("ORG_CODE");      
		contract         = readProfileValue("CONTRACT");      

		autosearchflag   = readProfileFlag ( "CUSTOMIZED", false ); 
		infotextflag     = readProfileFlag ( "INFOTEXT", false );   

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (Str.isEmpty(company))
		{
			company   = ctx.readValue("COMPANY");
		}

		ASPContext ctx = getASPContext();

		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

		// Read the command from the hidden field CMD
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
		if (DEBUG) debug(this+": ActiveWOInJobs.run()");

		ASPManager mgr = getASPManager();

		if (mgr.isEmpty(company))
		{
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Site_API.Get_Company(User_Default_API.Get_Contract)", "DEFCOMPANY");
			deftrans.addCommand("COMP", cmd);

			deftrans = mgr.perform(deftrans);        

			company = deftrans.getValue("COMP/DATA/DEFCOMPANY");
		}

		if (mgr.isEmpty(org_code) || mgr.isEmpty(org_contract))
		{
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();

			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Employee_API.Get_Org_Contract('"+company+"', (Company_Emp_API.Get_Max_Employee_Id('"+company+"', (Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)))))", "DEFORGCONTRACT");
			deftrans.addCommand("ORGCONT", cmd);

			//Bug 85406, Start
			cmd = mgr.newASPCommand();
			//Bug 85406, End

			cmd.defineCustomFunction(this, "Employee_API.Get_Organization('"+company+"', (Company_Emp_API.Get_Max_Employee_Id('"+company+"', (Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)))))", "DEFORGCODE");
			deftrans.addCommand("ORGCODE", cmd);

			deftrans = mgr.perform(deftrans);        

			org_contract = deftrans.getValue("ORGCONT/DATA/DEFORGCONTRACT");
			org_code = deftrans.getValue("ORGCODE/DATA/DEFORGCODE");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery qry = trans.addQuery(blk);

		// Sort on descendig REG_DATE.
		qry.setOrderByClause("REG_DATE DESC");
		qry.includeMeta("ALL");

		qry.addWhereCondition("CONTRACT = ?");  
		qry.addWhereCondition("ORG_CODE = ?");  
		qry.addWhereCondition("CONTRACT = (SELECT site FROM user_allowed_site_pub WHERE site = contract)");                 
		qry.addWhereCondition("OBJSTATE in ('FAULTREPORT','WORKREQUEST')");	  //JEWILK Call 110483.
		qry.addWhereCondition("WO_NO<(SELECT Number_Serie_API.Get_Last_Wo_Number FROM DUAL)");
                qry.addParameter(this,"DUMMY",org_contract);
                qry.addParameter(this,"DUMMY",org_code);

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
		return "ACTIVEWOINJOBSDESC: Active Work Order Incoming jobs";
	}
	public static String getDescription(ASPManager mgr)
	{
		return mgr.translate("ACTIVEWOINJOBSDESC: Active Work Order Incoming jobs");
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": ActiveWOInJobs.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			ASPManager mgr = getASPManager();
			name = getDescription(mgr);
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("ACTIVEWOINJOBSTITLE1:  - You have " ) + db_rows +
			translate("ACTIVEWOINJOBSTITLE2:  Work Orders.");
		}
		else
			return name;   
	}

	public void printContents()
	{
		// Information text, shown by default.
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("ACTIVEWOINJOBSINFO1: Incoming jobs on your maintenance organization. "); 
			printNewLine();
			endFont();
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
						printSubmitLink("ACTIVEWOINJOBSFIRST: First","firstCust");
					else
						printText("ACTIVEWOINJOBSFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("ACTIVEWOINJOBSPRV: Previous","prevCust");
					else
						printText("ACTIVEWOINJOBSPRV: Previous");

					printSpaces(5);
					String rows = translate("ACTIVEWOINJOBSROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWOINJOBSLAST: Last","lastCust");
					else
						printText("ACTIVEWOINJOBSLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWOINJOBSNEXT: Next","nextCust");
					else
						printText("ACTIVEWOINJOBSNEXT: Next");

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
		ASPManager mgr = getASPManager();
		trans          = mgr.newASPTransactionBuffer();

		if (DEBUG)
		{
			debug(this+": ActiveWOInJobs.runCustom()");
		}

		//Show only parts for the supplier connected to the current user

		trans = mgr.perform(trans); 

	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": ActiveWOInJobs.printCustomBody()");
			debug("\tprintCustomBody(): ActiveWOInJobs");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("ACTIVEWOINJOBSPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("ACTIVEWOINJOBSPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("ACTIVEWOINJOBSNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOINJOBSINFOTEXT", infotextflag);
		printSpaces(3);
		printText("ACTIVEWOINJOBSTEXTINFOTEXT: Hide information text");
		printNewLine();

		printSpaces(5);
		printCheckBox("ACTIVEWOINJOBSCUSTOMIZED", autosearchflag);
		printSpaces(3);
		printText("ACTIVEWOINJOBSEXTCUSTOMIZED: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("ACTIVEWOINJOBSLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
	}

	public void submitCustomization()
	{
		name = readValue("ACTIVEWOINJOBSNAME");

		// Write values to the profile.
		writeProfileValue("ACTIVEWOINJOBSNAME",  readAbsoluteValue("ACTIVEWOINJOBSNAME") );
		writeProfileValue("COMPANY",  readAbsoluteValue("COMPANY") );
		writeProfileValue("ORG_CONTRACT", readAbsoluteValue("ORG_CONTRACT") );
		writeProfileValue("ORG_CODE", readAbsoluteValue("ORG_CODE") );
		writeProfileValue("CONTRACT", readAbsoluteValue("CONTRACT") );

		autosearchflag = "TRUE".equals(readValue("ACTIVEWOINJOBSCUSTOMIZED")); 
		writeProfileFlag("CUSTOMIZED", autosearchflag);

		infotextflag = "TRUE".equals(readValue("ACTIVEWOINJOBSINFOTEXT")); 
		writeProfileFlag("INFOTEXT", infotextflag);

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
