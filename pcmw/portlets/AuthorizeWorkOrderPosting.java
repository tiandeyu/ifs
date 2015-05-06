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
 * File        : AuthorizeWorkOrderPosting.java 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Created     : BUNILK 001019
 * Modified    : 
 * ARWILK  001128  Changed the where condition for the table search(Call ID 53462)and customize page was modified;changed the layout.
 * JEWILK  001206  Added fields 'WORK_ORDER_COST_TYPE' and 'AMOUNT' in preDefine(). (Call 56142)
 * JIJOSE  001212  Corrections in search function
 * BUNILK  010615  Modified run() method.
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031016  Call Id:107913 Changed the getDescription method to pass the ASPManager parameter there
 *                 by made it possible to use the translate method to translate a given string.
 * THWILK  031030  Call Id:109746 Added getDescription method sice it was overloaded by the call ID 107913.
 * VAGULK  040309  Web Alignment,Removed Clone() and doReset() methods and unnecessary global variables.
 * NAMELK  041108  Non Standard Translation Tags Changed.
 * -----------------------------------------
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELLK 060901  Merged bug 58216.
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

public class AuthorizeWorkOrderPosting extends ASPPortletProvider
{

	//==========================================================================
	//  Static constants
	//==========================================================================	 

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.AuthorizeWorkOrderPosting");
	private final static int default_size = 10;

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

	private transient ASPTransactionBuffer trans; 
	private transient String  wo_no;
	private transient int db_rows;
	private transient int skip_rows;
	private transient boolean find;
	private transient boolean autho;
	private transient String  name;
	private transient boolean  hideInfo;
	private transient boolean autoSearch;
	private transient boolean showCriteria;
	private transient boolean search;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient int size;  
	private transient String  no_of_hits;

	//==========================================================================
	//  Construction
	//==========================================================================	  

	public AuthorizeWorkOrderPosting( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": AuthorizeWorkOrderPosting.<init> :"+portal+","+clspath);
	}     

	//==========================================================================
	//  PreDefine
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": AuthorizeWorkOrderPosting.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setSize(13).setLabel("PCMWAUTHORIZEWORKORDERPOSTINGWONO: WO No").setHyperlink("pcmw/ActiveSeperateReportInWorkOrder.page","WO_NO");
		addField(blk, "WO_NO", "Number", "#").setSize(13).setLabel("PCMWAUTHORIZEWORKORDERPOSTINGPORTWONO: WO No").setHyperlink("pcmw/ActiveSeperateReportInWorkOrder.page","WO_NO");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "CMNT").setSize(25).setLabel("CMNT: Comment");
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "COMPANY").setHidden().setFunction("''");
		addField(blk, "CATALOG_NO").setHidden();
		addField(blk, "DESCRIPTION").setSize(20).setLabel("CATALOGDESC: Description").setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
		addField(blk, "QTY").setSize(8).setLabel("QTIY: Hours/Qty");
		addField(blk, "WORK_ORDER_COST_TYPE").setLabel("WOORDCOSTTYPE: Cost Type");
		addField(blk, "AMOUNT").setLabel("AMOUNT: Cost Amount");
		addField(blk, "ROW_NO","Number").setHidden();
		addField(blk, "SIGN_ID").setHidden().setFunction("''");
		addField(blk, "PERSON_ID_").setHidden().setFunction("''");
		addField(blk, "FND_USER").setHidden().setFunction("''");
		addField(blk, "SIGNATURE").setHidden();
		addField(blk, "WORK_LEADER_SIGN").setHidden();

		blk.setView("WORK_ORDER_CODING4");
		tbl = newASPTable( blk );
		tbl.setWrap();
		tbl.enableRowSelect();

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

		appendJavaScript( "function customizeAuthoWO(obj,id)",
						  "{",
						  "   customizeBox(id);",
						  "}\n");

		appendJavaScript( "function authoSelec(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'AUTHOSEL';",
						  "}\n");

		init();
	}      

	protected void init()
	{
		if (DEBUG) debug(this+": AuthorizeWorkOrderPosting.init()");

		wo_no  = readProfileValue("WO_NO");
		no_of_hits = readProfileValue("NO_HITS", default_size+"");
		size = Integer.parseInt(no_of_hits);
		name  = readProfileValue( "AUTHOWONAME", name);
		hideInfo = readProfileFlag("DISINFOTEX", false);
		autoSearch = readProfileFlag("SEARCHAUTO", false);
		showCriteria = readProfileFlag("DISSEARCH", false);

		find   = ctx.readFlag("SEARCH_ORDERS",false);

		if (Str.isEmpty(wo_no))
			wo_no   = ctx.readValue("WO_NO");

		if (autoSearch)
			find = true;


		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );

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
		else if ("LAST".equals(cmd))
		{
			skip_rows = db_rows - size;
			find = true;
		}

		else if ("AUTHOSEL".equals(cmd))
		{
			autho = true;
		}

		ctx.writeValue("SKIP_ROWS",skip_rows+"");
	}

	protected void run()
	{
		if (DEBUG) debug(this+": AuthorizeWorkOrderPosting.run()");

		if (autho)
		{
			rowset.selectRows();

			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();
			ASPTransactionBuffer signtrans = mgr.newASPTransactionBuffer();         
			ASPCommand cmd;

			int i=0;

			while (i<rowset.countRows())
			{
				if (rowset.isRowSelected())
				{
					String sigN = rowset.getValue("SIGNATURE");
					if (sigN == null)
					{
						signtrans.clear();  
						deftrans.clear();

						cmd = mgr.newASPCommand(); 
						cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "SIGN_ID");
						signtrans.addCommand("PERINF", cmd);

						cmd = mgr.newASPCommand(); 
						cmd.defineCustomFunction(this,"Site_API.Get_Company","COMPANY");
						cmd.addParameter(this,"CONTRACT",rowset.getValue("CONTRACT"));

						signtrans.addCommand("COMP", cmd);

						cmd = mgr.newASPCommand(); 
						cmd.defineCustomFunction(this,"Company_Emp_API.Get_Max_Employee_Id","PERSON_ID_");
						cmd.addReference(this,"COMPANY","COMP/DATA","COMPANY");
						cmd.addReference(this,"SIGN_ID","PERINF/DATA","SIGN_ID");
						signtrans.addCommand("SIGNID", cmd);

						signtrans = perform(signtrans);    

						String empId = signtrans.getValue("SIGNID/DATA/PERSON_ID_");
						signtrans.clear();

						ASPCommand readcmd = mgr.newASPCommand();

						readcmd = deftrans.addCustomCommand("WOAUTHO"+i,"Work_Order_Coding_API.Authorize");

						readcmd.addParameter(this,"WO_NO",rowset.getValue("WO_NO"));                       
						readcmd.addParameter(this,"ROW_NO",rowset.getValue("ROW_NO"));
						readcmd.addParameter(this,"PERSON_ID_",empId);

						deftrans = perform(deftrans);
					}
				}
				rowset.next();
				i++;
			}    
		}

		if (find)
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer woleadtrans = mgr.newASPTransactionBuffer();
			ASPCommand cmd;

			cmd = mgr.newASPCommand();
			cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "FND_USER");
			woleadtrans.addCommand("SESS", cmd);  

			woleadtrans = perform(woleadtrans);   
			String workLead = woleadtrans.getValue("SESS/DATA/FND_USER");

			ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
			ASPQuery qry = trans.addQuery(blk);
			if (!Str.isEmpty(wo_no))
                        {
                            qry.addWhereCondition("WO_NO = ?");
                            qry.addParameter(this,"WO_NO",wo_no);
                        }
			qry.addWhereCondition("WORK_LEADER_SIGN = ?");
                        qry.addParameter(this,"WORK_LEADER_SIGN",workLead);
			qry.setOrderByClause("WO_NO");
			qry.includeMeta("ALL");

			qry.setBufferSize(size);    
			qry.skipRows(skip_rows);

			submit(trans);

			db_rows = blk.getASPRowSet().countDbRows();
			ctx.writeValue("DB_ROWS", db_rows+"" );
			ctx.writeFlag("SEARCH_ORDERS",find);
		}
	}  

	//==========================================================================
	//  Print Functions
	//==========================================================================
	public static String getDescription()
	{
		return "AUTHOWOPOST: Authorize Work Order Posting";
	}
	public static String getDescription(ASPManager mgr)
	{
		return mgr.translate("AUTHOWOPOST: Authorize Work Order Posting");
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": AuthorizeWorkOrderPosting.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			ASPManager mgr = getASPManager();
			name = getDescription(mgr);
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("ACTIVEWORKORDERSTITLE1:  - You have " ) + db_rows +
			translate("PCMWAUTHORIZEWORKORDERPOSTINGPORTROWS:  rows.");
		}
		else
			return name; 
	}

	public void printContents()
	{
		printNewLine();

		int    nRows;
		nRows = rowset.countRows();

		if (showCriteria)
		{
			printText("SEARCCRY: Search Criteria:");  
			printNewLine();
			printText("WON: WO No:");
			printSpaces(2);
			if (!Str.isEmpty(wo_no))
				printText(wo_no);
			else
				printText("ALLWO: All");  
			printNewLine();
		}

		if (!hideInfo)
		{
			setFontStyle(ITALIC);
			printText("AUTOWOPOSTINFO1: Not authorized postings Active work orders where you are work leader. In ");
			printScriptLink("AUTHOWOCUSTLINK: customize", "customizeAuthoWO");
			printText("AUTOWOPOSTINFO2:  page you can change search conditions."); 
			printNewLine();
			endFont();
		}

		if (!autoSearch && !find)
		{
			printNewLine();  
			printSubmitLink("FINDAUTHOWO: Search","findCust");
			printNewLine();
		}
		else if ((find)&&(nRows>0))
		{
			printNewLine();
			printSpaces(5);
			printSubmitLink("AUTHOSEL: Authorize","authoSelec");
			printNewLine();
			printNewLine();
		}

		if (!(nRows > 0))
		{
			if (find)
			{
				if (!autoSearch)
				{
					printNewLine();  
					printSubmitLink("FINDAUTHOWO: Search","findCust");
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("AUTHOWONODATA: No data found!");
				endFont();
			}
		}


		printHiddenField("CMD","");

		if (nRows > 0)
		{
			if (find)
			{
				printTable(tbl);

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("AUTHOWORKORDERSFIRST: First","firstCust");
					else
						printText("ACTIVEWORKORDERSFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("AUTHOWORKORDERSPRV: Previous","prevCust");
					else
						printText("AUTHOWORKORDERSPRV: Previous");

					printSpaces(5);
					String rows = translate("AUTHOWORKORDERSROWS: (Rows &1 to &2 of &3)",
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
		}
	}

	//==========================================================================
	// Custom body functions  
	//==========================================================================	 

	public boolean canCustomize()
	{
		return true ;
	}

	public void runCustom()
	{
	}
	public void printCustomBody()
	{
		if (DEBUG)
			debug(this+": AuthorizeWorkOrderPosting.printCustomBody()");
		runCustom();

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("AUTHOWOQUERYSECTION: Query Selections");
		endFont();

		printSpaces(5);
		printText("AUTHOWCHOOSEWONO: Choose WO No: ");
		printSpaces(5);
		printField("WONOSELEC", wo_no, 8);
		printDynamicLOV("WONOSELEC","ACTIVE_WORK_ORDER",translate("LISTOFWONO: List of Work Orders"),600,445);
		printNewLine();

		setFontStyle(BOLD);
		printSpaces(5);
		printText("AUTHOWOPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("AUTHOWOPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("AUTHOWONAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DISSEARCH", showCriteria);
		printSpaces(3);
		printText("DISSEACRI: Display search criteria");
		printNewLine();
		printNewLine();   

		printSpaces(5);
		printCheckBox("DISINFOTEX", hideInfo);
		printSpaces(3);
		printText("PCMWPORTAUTHORIZEWOPOSTINGHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("SEARCHAUTO", autoSearch);
		printSpaces(3);
		printText("PCMWPORTAUTHORIZEWOPOSTINGSEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("LINEMAXHITAUTHOWO: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
	}    

	public void submitCustomization()
	{
		wo_no = readValue("WONOSELEC");  
		writeProfileValue("WO_NO", readAbsoluteValue("WONOSELEC"));

		name  = readValue("AUTHOWONAME");
		writeProfileValue("AUTHOWONAME",readAbsoluteValue("AUTHOWONAME"));

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));

		hideInfo = "TRUE".equalsIgnoreCase(readValue("DISINFOTEX")); 
		writeProfileFlag("DISINFOTEX",hideInfo);

		autoSearch = "TRUE".equalsIgnoreCase(readValue("SEARCHAUTO"));
		writeProfileFlag("SEARCHAUTO",autoSearch);

		showCriteria = "TRUE".equalsIgnoreCase(readValue("DISSEARCH"));
		writeProfileFlag("DISSEARCH",showCriteria);
	}       
}   
