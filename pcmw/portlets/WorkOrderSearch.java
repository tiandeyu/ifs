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
 * File        : WorkOrderSearchSM.java 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    SHERMAL	00-10-31 Created.
 *    JIJOSE    00-12-15 Localization
 *    SHFELK    00-12-29 Changed the hyperlink of field 'WO_NO' to 'ActiveSeparate2light1.asp'
 *    SHFELK    01-06-18 Put Field Names into seperate lines.(# 66097)
 * ----------------------------------------------------------------------------
 *    SHAFLK    02-11-15 Bug 34166,Changed setHyperlink of Wo in predefine.
 *    BUNILK    02-12-16 Merged with 2002-3 SP3
 *    BUNILK    02-12-16 Added AspField MCH_CODE_CONTRACT.   
 *    ARWILK    03-10-15 (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 *    VAGULK    040302   Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 *    NAMELK    041108   Non Standard Translation Tags Changed.
 *  -----------------------------------------------------------------------------------------
 *    ChAmlk    061016   Bug 61103, Modified run() to remove web security issues.
 *    ILSOLK    070227   Merged Bug Id 61103.
 *    AMDILK    070320   Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile 
 *    AMDILK    070531   Modified submitCustomization()
 *  ----------------------------------------------------------------------------------------
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

public class WorkOrderSearch extends ASPPortletProvider
{
	private String title;
	private String showText;
	private String noOfHits;

	private static final int defaultSize = 10;

	private transient int dbRows;
	private transient int size;
	private transient int skipRows;
	private transient boolean find;

	private ASPContext ctx;
	private ASPBlock blk;
	private ASPTable tbl;
	private ASPRowSet rowset;
   // private ASPTransactionBuffer trans;

	public WorkOrderSearch(ASPPortal portal, String clsPath)
	{
		super(portal,clsPath); 
	}


	public void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		printHiddenField("CMD","");

		if (!("TRUE".equalsIgnoreCase(showText)))
		{
			printNewLine();
			setFontStyle(ITALIC);
			printText("WOSCHHEADTEXT: Here you can search for work orders. Enter search key words and click ");
			printSubmitLink("PCMWWOSCHFNDLINK: Find","findWO");
			endFont();
		}
		else
			printNewLine();

		printText("PCMWWOSCHWONO: WO No:");
		printSpaces(3);
		if (mgr.isEmpty(readValue("WO_NO")))
			printField("WO_NO","",5);
		else
			printField("WO_NO",readValue("WO_NO"),5);
		printNewLine(); 
		printText("PCMWWOSCHWOSHDESCR: Short Description:");
		printSpaces(3);
		if (mgr.isEmpty(readValue("ERR_DESCR")))
			printField("ERR_DESCR","",25);
		else
			printField("ERR_DESCR",readValue("ERR_DESCR"),25);
		printNewLine(); 
		printText("PCMWWOSCHOBJID: Object Id:");
		printSpaces(3);
		if (mgr.isEmpty(readValue("MCH_CODE")))
			printField("MCH_CODE","",15);
		else
			printField("MCH_CODE",readValue("MCH_CODE"),15);
		printNewLine(); 
		printText("PCMWWOSCHSTAT: Status:");
		printSpaces(3);
		if (mgr.isEmpty(readValue("STATE")))
			printField("STATE","",12);
		else
			printField("STATE",readValue("STATE"),12);
		printNewLine(); 
		printText("PCMWWOSCHPRIO: Prio:");
		printSpaces(3);
		if (mgr.isEmpty(readValue("PRIORITY_ID")))
			printField("PRIORITY_ID","",4);
		else
			printField("PRIORITY_ID",readValue("PRIORITY_ID"),4);
		printNewLine();

		int nRows = rowset.countRows();

		if (nRows>0 || find)
		{
			printTable(tbl);
			if (size < dbRows)
			{
				printNewLine();
				if (skipRows>0)
				{
					printSubmitLink("WOSCHFIRSTWO: First","firstWO");
					printSpaces(5);
					printSubmitLink("WOSCHPREVWO: Previous","previousWO");
				}
				else
				{
					printText("WOSCHFIRSTWO: First");
					printSpaces(5);
					printText("WOSCHPREVWO: Previous");
				}

				printSpaces(5);
				String rows = translate("WORKORDERSROWS: (Rows &1 to &2 of &3)",
										(skipRows+1)+"",
										(skipRows+size<dbRows?skipRows+size:dbRows)+"",
										dbRows+"");
				printText( rows );
				printSpaces(5);

				if (skipRows<dbRows-size)
				{
					printSubmitLink("WOSCHNEXTWO: Next","nextWO");
					printSpaces(5);
					printSubmitLink("WOSCHLASTWO: Last","lastWO");
				}
				else
				{
					printText("WOSCHNEXTWO: Next");
					printSpaces(5);
					printText("WOSCHLASTWO: Last");
				}

				printNewLine();
				printNewLine();
			}
		}
		else if (find)
		{
			setFontStyle(BOLD);
			printText("PCMWWOSCHNODATA: No data found!");
			endFont();
		}
	}


	public static String getDescription()
	{
		return "PCMWWOSCHKEYMAX: Work Order Search";
	}


	public String getTitle(int mode)
	{
		if (mode==MAXIMIZED)
			if (Str.isEmpty(readProfileValue("TITLE")))
			{
				title = translate(getDescription());
				return title;
			}
			else
			{
				title = translate("PCMWWOSCHPROFVAL: "+readProfileValue("TITLE"));
				return title;
			}
		else if (mode==MINIMIZED)
			if (Str.isEmpty(readProfileValue("TITLE")))
			{
				title = translate(getDescription());
				return title +" - "+dbRows+" match(es)";
			}
			else
			{
				title = translate("PCMWWOSCHKEYMIN: "+readProfileValue("TITLE"));
				return title +" - "+dbRows+" match(es)";
			}
		else
			return translate("PCMWWOSCHKEYS: Customize Work Orders");
	}


	public boolean canCustomize()
	{
		return true;
	}


	public void runCustom()
	{
	}


	public void printCustomBody()
	{
		runCustom();

		setFontStyle(BOLD);
		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("PCMWWOSCHPOCONFG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("PCMWWOSCHPOTNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("WOSCHPORTLTNAME", title, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("WOSCHDISINFOTEX", false);
		printSpaces(3);
		printText("PCMWWOSCHHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NOOFHITS", noOfHits, 5);
		printSpaces(3);
		printText("PCMWWOSCHMAXLNO: Max no of hits to be displayed ");
		size = Integer.parseInt(noOfHits);
		printNewLine();
	}   


	public void submitCustomization()
	{
		noOfHits = readValue("NOOFHITS");
		size = Integer.parseInt(noOfHits);

		//		if (Integer.parseInt(readProfileValue("NOOFHITS",defaultSize+"")) != size)
		//		find = true;
		find = false;

		writeProfileValue("NOOFHITS",readAbsoluteValue("NOOFHITS"));

		title = readValue("WOSCHPORTLTNAME");
		writeProfileValue("TITLE",readAbsoluteValue("WOSCHPORTLTNAME"));

		showText = readValue("WOSCHDISINFOTEX");
		writeProfileValue("WOSCHDISINFOTEX",readAbsoluteValue("WOSCHDISINFOTEX"));
	}


	public void init()
	{
		showText = readProfileValue("WOSCHDISINFOTEX",showText);
		title = readProfileValue("TITLE",title);
		dbRows = Integer.parseInt(ctx.readValue("DBROWS","0"));
		skipRows = Integer.parseInt(ctx.readValue("SKIPROWS","0"));
		noOfHits = readProfileValue("NOOFHITS",defaultSize+"");
		size = Integer.parseInt(noOfHits);
		String cmd = readValue("CMD");

		find = false;

		if ("FIND".equals(cmd))
		{
			skipRows = 0;
			find = true;
		}
		else if ("FIRST".equals(cmd))
		{
			skipRows = 0;
			find = true;
		}
		else if ("PREV".equals(cmd))
		{
			if (skipRows>size)
				skipRows -= size;
			else
				skipRows = 0;
			find = true;
		}
		else if ("NEXT".equals(cmd) && skipRows<=dbRows-size)
		{
			skipRows += size;
			find = true;
		}
		else if ("LAST".equals(cmd))
		{
			skipRows = dbRows - size;
			find = true;
		}

		ctx.writeValue("SKIPROWS",skipRows+"");
	}


	public void preDefine() throws FndException
	{
		ctx = getASPContext();

		blk = newASPBlock("HEAD");
		blk.newASPCommandBar();

		addField(blk,"CONTRACT").setHidden();
		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk,"WO_NO","Number").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO,CONTRACT").setSize(10).setLabel("PCMWWORKORDERSEARCHWONO: WO No");
		addField(blk, "WO_NO", "Number", "#").setHyperlink("pcmw/ActiveSeparate2light.page","WO_NO,CONTRACT").setSize(10).setLabel("PCMWWORKORDERSEARCHPORTWONO: WO No");
		// 031015  ARWILK  End  (Bug#104789)
                addField(blk, "WO_NO2").setHidden().setFunction("''");
		addField(blk,"ERR_DESCR").setSize(25).setLabel("ERRDESC: Short Description");
		addField(blk,"MCH_CODE_CONTRACT").setHidden();
		addField(blk,"MCH_CODE").setSize(12).setLabel("PCMWPORTWOSEARCHMCHCODE: Object ID").setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE,MCH_CODE_CONTRACT");
		addField(blk,"STATE").setSize(10).setLabel("PCMWWORKORDERSEARCHPORTSTATE: Status");
		addField(blk,"PRIORITY_ID").setSize(4).setLabel("PRIORITYID: Prio");

		blk.setView("ACTIVE_SEPARATE");

		tbl = newASPTable(blk);
		tbl.disableRowStatus();
		rowset = blk.getASPRowSet();

		appendJavaScript("function firstWO(obj,id)",
						 "{",
						 "   getPortletField(id,'CMD').value = 'FIRST';",
						 "}\n");

		appendJavaScript("function previousWO(obj,id)",
						 "{",
						 "   getPortletField(id,'CMD').value = 'PREV';",
						 "}\n");

		appendJavaScript("function lastWO(obj,id)",
						 "{",
						 "   getPortletField(id,'CMD').value = 'LAST';",
						 "}\n");

		appendJavaScript("function nextWO(obj,id)",
						 "{",
						 "   getPortletField(id,'CMD').value = 'NEXT';",
						 "}\n");

		appendJavaScript("function findWO(obj,id)",
						 "{",
						 "   getPortletField(id,'CMD').value = 'FIND';",
						 "}\n");
		title = translate(getDescription());

		init();
	}
	

	protected void doFreeze() throws FndException
	{
		String preTitle;
		preTitle = title;
		super.doFreeze();
	}


	protected void run()
	{
		ASPTransactionBuffer trans = this.getASPManager().newASPTransactionBuffer();
		String woQryStr;
		String errDescQryStr;
		String mchCodeQryStr;
		String statusQryStr;
		String prioQryStr;

		if (find)
		{
			ASPQuery qry = trans.addEmptyQuery(blk);
			if (!Str.isEmpty(readValue("WO_NO")))
			{
				qry.addWhereCondition("Report_sys.Parse_Parameter(\"WO_NO\",?) = 'TRUE'");
                                qry.addParameter(this,"WO_NO2", readValue("WO_NO"));
                                        
			}
			if (!Str.isEmpty(readValue("ERR_DESCR")))
			{
				qry.addWhereCondition("Report_sys.Parse_Parameter(Upper(\"ERR_DESCR\"), ?) = 'TRUE'");
                                qry.addParameter(this,"ERR_DESCR", readValue("ERR_DESCR").toUpperCase());
                                        
			}
			if (!Str.isEmpty(readValue("MCH_CODE")))
			{
				qry.addWhereCondition("Report_sys.Parse_Parameter(Upper(\"MCH_CODE\"), ?) = 'TRUE'");
                                qry.addParameter(this,"MCH_CODE", readValue("MCH_CODE").toUpperCase());
                                       
			}
			if (!Str.isEmpty(readValue("STATE")))
			{
				qry.addWhereCondition("Report_sys.Parse_Parameter(Upper(\"STATE\"), ?) = 'TRUE'");
                                qry.addParameter(this,"STATE", readValue("STATE").toUpperCase());
                                       
			}
			if (!Str.isEmpty(readValue("PRIORITY_ID")))
			{
				qry.addWhereCondition("Report_sys.Parse_Parameter(\"PRIORITY_ID\", ?) = 'TRUE'");
                                qry.addParameter(this,"PRIORITY_ID", readValue("PRIORITY_ID").toUpperCase());
                                       
			}

			qry.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
			qry.setOrderByClause("WO_NO");
			qry.includeMeta("ALL");
			qry.setBufferSize(size);    
			qry.skipRows(skipRows);

			submit(trans);
		}

		dbRows = blk.getASPRowSet().countDbRows();
		getASPContext().writeValue("DBROWS", dbRows+"" );
	}       


   
}

