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
 * File        : MyReportedActiveWO.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Created     : MIBOSE  000425  Created
 * Modified    :
 * MIBOSE  000606  Added setSize(20) for addField "Short Description".
 * PJONSE  000831  Changed translation constant MYREPWOTITLE to MYREPWOTITLE1 and MYREPWOTITLE2. 
 * BUNILK  001109  Removed some unwanted codes and changed some codes to improve performence.
 *                 Added two checkboxes in customization page for enable or disable possibality of  
 *                 auto search and show inforation text.
 * JIJOSE  001218  Sarch function updated
 * NUPELK  010619  Fixed some problems in 'Auto Search' and the 'Displaying of
 *                 Information Text'. Bug #66472. Made changes to some previously 
 *                 defined variables.
 * BUNILK  021216  Added AspField MCH_CODE_CONTRACT.   
 * ARWILK  031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 * THWILK  031018  Call ID 108173- Changed the title from "My Reported Active Work Orders" to 
 *                 "Active Work Orders reported by me".
 * VAGULK  040311  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * ARWILK  040722  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
 * NAMELK  041108  Non Standard Translation Tags Corrected.
 * -------------------------------
 * DIAMLK  060823  Bug 58216, Eliminated SQL Injection security vulnerability.
 * NAMELK  060901  Merged Bug 58216.
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

public class MyReportedActiveWO extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	private final static int default_size = 10;
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.MyReportedActiveWO");

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
	private transient String  reported_by;
	private transient int skip_rows;
	private transient int db_rows;
	private transient boolean  find;
	private transient String  name;
	private transient boolean  hideInfo;
	private transient boolean autoSearch;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  no_of_hits;
	private transient int size;

	//==========================================================================
	//  Construction
	//==========================================================================

	public MyReportedActiveWO( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": MyReportedActiveWO.<init> :"+portal+","+clspath);
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
		if (DEBUG) debug(this+": MyReportedActiveWO.construct()");
		return super.construct();
	}


	/**
	 * Called after define() while changing state from UNDEFINED to DEFINED.
	 */
	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.doFreeze()");
		super.doFreeze();
	}

	/**
	 * Called as the first function just after fetching the page from the pool.
	 * Almost all that should be done here can be done in the init() function.
	 * Not called on instance creation (not cloning).
	 */
	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	// 
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.preDefine()");

		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();
		addField(blk, "CONTRACT").setHidden();
		addField(blk, "MCH_CODE_CONTRACT").setHidden();
		addField(blk, "REG_DATE").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("PCMWMYREPORTEDACTIVEWOWONO: Wo No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO").setLabel("PCMWMYREPORTEDACTIVEWOPORTWONO: Wo No");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "ERR_DESCR").setSize(20).setLabel("SHORTDESC: Short Description");
		addField(blk, "MCH_CODE").setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT").setLabel("OBJECTIDMYREPWO: Object ID");
		addField(blk, "STATE").setLabel("STATUSMYREPWO: Status");
		addField(blk, "ORG_CODE").setLabel("DEPARTMENTMYREPWO: Maintenance Organization");
		addField(blk, "WORK_LEADER_SIGN").setLabel("WORKLEADERMYREPWO: Work Leader");

		defblk = newASPBlock("DEFBLK");
		addField(defblk, "DEFCONTRACT");
		addField(defblk, "USERREPORTEDBY");

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

		init();
	}


	protected void init()// throws FndException
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.init()");

		no_of_hits  = readProfileValue("NO_HITS", default_size+"");
		size        = Integer.parseInt(no_of_hits);
		name        = readProfileValue( "MYREPORTEDAWONAME", name);
		hideInfo    = readProfileFlag("DISINFOTEX", hideInfo);
		autoSearch  = readProfileFlag("SEARCHAUTO", autoSearch);


		find   = ctx.readFlag("SEARCH_ORDERS",false);

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

		ctx.writeValue("SKIP_ROWS",skip_rows+"");
	}

	protected void run()
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.run()");

		if (Str.isEmpty(reported_by))
		{
			ASPManager mgr = getASPManager();
			ASPTransactionBuffer deftrans = mgr.newASPTransactionBuffer();
			ASPCommand cmd = mgr.newASPCommand();

			cmd.defineCustomFunction(this, "Person_Info_API.Get_Id_For_User(FND_SESSION_API.Get_Fnd_User)", "USERREPORTEDBY");

			deftrans.addCommand("REPORTEDBY", cmd);
			deftrans = perform(deftrans);        
			reported_by = deftrans.getValue("REPORTEDBY/DATA/USERREPORTEDBY");
		}

		ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
		ASPQuery             qry   = trans.addQuery(blk);
		if (autoSearch || find)
		{
			qry.setOrderByClause("REG_DATE DESC");
			qry.includeMeta("ALL");

			if (!Str.isEmpty(reported_by))
			{
                            qry.addWhereCondition("REPORTED_BY = ?");
                            qry.addInParameter(this,"WORK_LEADER_SIGN",reported_by);
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
		return "MYREPACTIVEWODESC: Active Work Orders reported by me";
	}

	public String getTitle(int mode)
	{
		if (DEBUG) debug(this+": MyReportedActiveWO.getTitle("+mode+")");

		if (Str.isEmpty(name))
		{
			name = translate(getDescription());
		}
		if (mode==ASPPortal.MINIMIZED)
		{
			return name + translate("MYREPWOTITLE1:  - You have " ) + db_rows +
			translate("MYREPWOTITLE2:  Work Orders.");
		}
		else
			return name;   
	}

	public void printContents()
	{
		printHiddenField("CMD","");

		if (!(hideInfo == true))
		{
			setFontStyle(ITALIC);
			printNewLine();
			printText("MYREPORTEDACTIVEWOINFO1: Active work orders where you are reported by. "); 
			printNewLine();
			endFont();
		}

		int    nRows;
		nRows = rowset.countRows();

		if (autoSearch || find)
		{
			if (nRows > 0)
			{
				printTable(tbl);

				if (size < db_rows)
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("MYREPORTEDACTIVEWOFIRST: First","firstCust");
					else
						printText("MYREPORTEDACTIVEWOFIRST: First");
					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("MYREPORTEDACTIVEWOPRV: Previous","prevCust");
					else
						printText("MYREPORTEDACTIVEWOPRV: Previous");

					printSpaces(5);
					String rows = translate("MYREPORTEDACTIVEWOROWS: (Rows &1 to &2 of &3)",
											(skip_rows+1)+"",
											(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
											db_rows+"");
					printText( rows );
					printSpaces(5);

					if (skip_rows<db_rows-size)
						printSubmitLink("MYREPORTEDACTIVEWOLAST: Last","lastCust");
					else
						printText("MYREPORTEDACTIVEWOLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("MYREPORTEDACTIVEWONEXT: Next","nextCust");
					else
						printText("MYREPORTEDACTIVEWONEXT: Next");

					printNewLine();
					printNewLine();
				}
			}
			else
			{
				if (!autoSearch)
				{
					printSubmitLink("FINDAUTHOWO: Search","findCust");   
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("MYREPORTEDACTIVEWONODATA: No data found!");
				endFont();
			}    
		}
		else
		{
			printSubmitLink("FINDAUTHOWO: Search","findCust");   
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
			debug(this+": MyReportedActiveWO.runCustom()");
		}
	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": MyReportedActiveWO.printCustomBody()");
			debug("\tprintCustomBody(): MyReportedActiveWO");
		}
		runCustom(); // will be called automatically in future release

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("MYJOBLISTPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("MYREPORTEDAWOPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("MYREPORTEDAWONAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DISINFOTEX", hideInfo);
		printSpaces(3);
		printText("PCMWPORTMYREPORTACTIVEWOHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("SEARCHAUTO", autoSearch);
		printSpaces(3);
		printText("PCMWPORTMYREPORTACTIVEWOSEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("MYREPORTEDAWOLINEMAXHIT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
	}

	public void submitCustomization()
	{
		name  = readValue("MYREPORTEDAWONAME");

		writeProfileValue("MYREPORTEDAWONAME",  readAbsoluteValue("MYREPORTEDAWONAME") );

		no_of_hits = readValue("NO_HITS");
		size = Integer.parseInt(no_of_hits);
		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}

		hideInfo = "TRUE".equals(readValue("DISINFOTEX")); 
		writeProfileFlag("DISINFOTEX",hideInfo);

		autoSearch = "TRUE".equals(readValue("SEARCHAUTO"));
		writeProfileFlag("SEARCHAUTO",autoSearch);

	}

	private String nvl( String str, String instead_of_empty )
	{
		return Str.isEmpty(str) ? instead_of_empty : str;
	}
}
