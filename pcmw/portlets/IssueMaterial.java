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
 * File        : IssueMaterial.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    JEWI     001101  Created.
 *    JEWI     001123  Changed the VIEW and the method used to Issue. Modified functions preDefine() and run().
 *    JEWI     001129  Modified WHERE condition to filter CATALOG_CONTRACT and SPARE_CONTRACT.
 *    JIJOSE   001215  Search functionality
 *    VAGULK   001008  Changed the name from Issue Material to Issue WO Material (67860)
 *    ARWILK   031015  (Bug#104789) Changed format mask for WO_NO. (Check method comments)
 *    THWILK   031021  (Bug#108136) Changed the link to ActiveSeperateRedirect from ActiveSeperateReportInWorkOrder.
 *    VAGULK   040310  Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 *    BUNILK   040625  Modified run() and preDefine() methods.
 *    NAMELK   041108  Non Standard Translation Tags Corrected.
 *  --------------------------------------
 *    NAMELK   060901  Merged Bug 58216, Eliminated SQL Injection security vulnerability.     
 *    AMDILK   070320  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 *    AMDILK   070531  Modified submitCustomization()
 *    ILSOLK   070730  Eliminated LocalizationErrors.
 *    ASSALK   070910  Call 148510. Modified preDefine(), run(). 
 *    SHAFLK   100809  Bug 92336, Modified init(), run() and preDefine().
 * ----------------------------------------------------------------------------
 *
 */


package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

public class IssueMaterial extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.IssueMaterial");
	private final static int default_size = 10;

	//==========================================================================
	//  Instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;


	//==========================================================================
	//  Mutable attributes
	//==========================================================================

	private transient String name;

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient ASPTransactionBuffer trans;
	private transient int db_rows;
	private transient int skip_rows;
	private transient boolean search;

	private transient String wo_no;
	private transient String part_no;
	private transient String date_required;

	private transient String  usercmd;
	private transient boolean disp_search;
	private transient boolean hide_info_text;
	private transient boolean search_auto;


	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient int size;  
	private transient String no_of_hits;

	//==========================================================================
	//  Construction
	//==========================================================================

	/**
	 * The only mandatory function - constructor.
	 */ 
	public IssueMaterial( ASPPortal portal, String clspath )
	{
		super(portal,clspath);
	}

	protected void preDefine() throws FndException
	{
		ctx = getASPContext();
		blk = newASPBlock("MAIN");
		ASPCommandBar cmdbar = blk.newASPCommandBar();

		// 031015  ARWILK  Begin  (Bug#104789)
		//addField(blk, "WO_NO", "Number").setLabel("PCMWISSUEMATERIALWONO: WO No").setHyperlink("pcmw/ActiveSeperateReportInWorkOrder.page","WO_NO","NEWWIN");
		addField(blk, "WO_NO", "Number", "#").setLabel("PCMWISSUEMATERIALPORTWONO: WO No").setHyperlink("pcmw/ActiveSeperateRedirect.page","WO_NO","NEWWIN");
		// 031015  ARWILK  End  (Bug#104789)
		addField(blk, "SPARE_CONTRACT").setHidden();
		addField(blk, "MAINT_MATERIAL_ORDER_NO", "Number").setLabel("MAINTMATORDNO: Order No");
		addField(blk, "LINE_ITEM_NO", "Number").setHidden();
		addField(blk, "PART_NO").setLabel("PARTNO: Part No");
		addField(blk, "PART_DESCR").setLabel("PARTDESCR: Description").setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)").setSize(20);
		addField(blk, "TRACKING").setFunction("PART_CATALOG_API.Get_Serial_Tracking_Code_Db(:PART_NO)").setHidden();
		addField(blk, "DATE_REQUIRED", "Date").setLabel("DATEREQUIRED: Required");      
		addField(blk, "QTY_TO_ISSUE", "Number").setFunction("PLAN_QTY-QTY-QTY_RETURNED").setLabel("QTYREQUIRED: Qty");
		addField(blk, "LOCATION_NO").setFunction("''").setHidden();
		addField(blk, "LOT_BATCH_NO").setFunction("''").setHidden();
		addField(blk, "SERIAL_NO").setFunction("''").setHidden();
		addField(blk, "ENG_CHG_LEVEL").setFunction("''").setHidden();
		addField(blk, "WAIV_DEV_REJ_NO").setFunction("''").setHidden();
		addField(blk, "DATE_RANGE").setFunction("''").setHidden();
                addField(blk, "PROJECT_ID").setFunction("''").setHidden();
                addField(blk, "ACTIVITY_SEQ").setFunction("''").setHidden();
                addField(blk, "DUMMY_ACT_QTY_ISSUED","Number").setFunction("''").setHidden();
                addField(blk, "DUMMY","String").setFunction("''").setHidden();

		blk.setView("MAINT_MATERIAL_REQ_LINE");
		cmdbar.addCustomCommand("ISSUE",  "ISSUEMAT: Issue");
		tbl = newASPTable(blk); 
		tbl.setWrap();
		rowset = blk.getASPRowSet();
		tbl.disableQueryRow();   
		tbl.disableQuickEdit();  
		tbl.disableRowCounter(); 
		tbl.disableRowSelect();  
		//tbl.disableRowStatus();  
		tbl.unsetSortable();     



		appendJavaScript( "function firstWO(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIRST';",
						  "}\n");

		appendJavaScript( "function prevWO(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'PREV';",
						  "}\n");

		appendJavaScript( "function lastWO(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'LAST';",
						  "}\n");

		appendJavaScript( "function nextWO(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'NEXT';",
						  "}\n");

		appendJavaScript( "function findWO(obj,id)",
						  "{",
						  "   getPortletField(id,'CMD').value = 'FIND';",
						  "}\n");


		appendJavaScript( "function customizeIssueMat(obj,id)",
						  "{",
						  "   customizeBox(id);",
						  "}\n");


		// mutable attribute
		name = translate(getDescription());

		init();
	}

	protected void init()
	{
		if (DEBUG) debug(this+": IssueMaterial.init()");

		wo_no = readProfileValue("WONO");
		part_no = readProfileValue("PART_NO");
		date_required = readProfileValue("DATE_REQUIRED");

		disp_search = readProfileFlag("DISPSEARCH", false);
		hide_info_text = readProfileFlag("HIDEINFOTEXT", false);
		search_auto = readProfileFlag("SEARCHAUTO", false);

		name = readProfileValue( "ISSUEMATNAME", name);
		no_of_hits = readProfileValue("NO_HITS", default_size+"");
		size = Integer.parseInt(no_of_hits);

		db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
		skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );

		search   = ctx.readFlag("SEARCH_ORDERS",false);        

		String cmd = readValue("CMD");
		usercmd = cmd;

		if ( Str.isEmpty(usercmd) )
		{
			if (cmdBarCustomCommandActivated())
				usercmd = getCmdBarCustomCommandId();
			else if ( cmdBarStandardCommandActivated() )
				usercmd = getCmdBarStandardCommandId();
		}

		if ( "FIND".equals(cmd) )
		{
			skip_rows = 0;
			search = true;
		}
		else if ( "FIRST".equals(cmd))
		{
			skip_rows = 0;
			search = true;
		}
		else if ( "PREV".equals(cmd) )
			if ( skip_rows>=size )
			{
				skip_rows -= size;
				search = true;
			}
			else
			{
				skip_rows = 0;
				search = true;
			}
		else if ( "NEXT".equals(cmd) && skip_rows<=db_rows-size )
		{
			skip_rows += size;
			search = true;
		}
		else if ( "LAST".equals(cmd))
		{
			skip_rows = db_rows - size;
			search = true;
		}

		ctx.writeValue("SKIP_ROWS",skip_rows+"");
	}



	protected void run()
	{
		if ( !Str.isEmpty(usercmd) )
		{
			if ("ISSUE".equals(usercmd))
			{
				rowset.selectRows();
				rowset.goTo(rowset.getRowSelected());


				String maint_material_order_no_ = rowset.getValue("MAINT_MATERIAL_ORDER_NO");
				String line_item_no_ = rowset.getValue("LINE_ITEM_NO");
				String qty_to_issue_ = rowset.getValue("QTY_TO_ISSUE");

				ASPTransactionBuffer issueTrans = getASPManager().newASPTransactionBuffer();  
				ASPCommand issueCmd = getASPManager().newASPCommand();

				//issueCmd = issueTrans.addCustomCommand("ISS","WORK_ORDER_PART_API.make_issue_detail");
				issueCmd = issueTrans.addCustomCommand("ISS","MAINT_MATERIAL_REQ_LINE_API.Make_Issue_Detail");
                                issueCmd.addParameter(this,"DUMMY_ACT_QTY_ISSUED","");
				issueCmd.addParameter(this,"MAINT_MATERIAL_ORDER_NO",maint_material_order_no_);
				issueCmd.addParameter(this,"LINE_ITEM_NO",line_item_no_);
				issueCmd.addParameter(this,"LOCATION_NO","");
				issueCmd.addParameter(this,"LOT_BATCH_NO","");
				issueCmd.addParameter(this,"SERIAL_NO","");
				issueCmd.addParameter(this,"ENG_CHG_LEVEL","");
				issueCmd.addParameter(this,"WAIV_DEV_REJ_NO","");
                                issueCmd.addParameter(this,"PROJECT_ID","");
                                issueCmd.addParameter(this,"ACTIVITY_SEQ","");
                                issueCmd.addParameter(this,"QTY_TO_ISSUE",qty_to_issue_);

				issueTrans = perform(issueTrans);
				search =true;
			}
		}
		if (search_auto || search)
		{
			trans = getASPManager().newASPTransactionBuffer();

			ASPQuery qry = trans.addEmptyQuery(blk);
			qry.addWhereCondition("CATALOG_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CATALOG_CONTRACT) FROM dual)");
			qry.addWhereCondition("SPARE_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(SPARE_CONTRACT) FROM dual)");
			qry.addWhereCondition("OBJSTATE IN ('RELEASED','STARTED','WORKDONE')");
			qry.addWhereCondition("(PLAN_QTY-QTY-QTY_RETURNED)>0");
			qry.addWhereCondition("PART_CATALOG_API.Get_Serial_Tracking_Code_Db(part_no) = 'NOT SERIAL TRACKING'");
			qry.setOrderByClause("WO_NO");

			if (!Str.isEmpty(wo_no))
                        {
                            qry.addWhereCondition("WO_NO= ?");
                            qry.addInParameter(this,"WO_NO",wo_no);
                        }
			if (!Str.isEmpty(part_no))
			{
                            qry.addWhereCondition("PART_NO= ?");
                            qry.addInParameter(this,"PART_NO",part_no);
                        }
			if (!Str.isEmpty(date_required))
			{
                            String temp_date_required = Str.replace(date_required,"+","");
                            if (Str.toInt(temp_date_required) > 0)
                            {   
                                qry.addWhereCondition("trunc(DATE_REQUIRED) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ?");
                                qry.addParameter(this,"DUMMY", temp_date_required);
                            }
                            else
                            {
                                qry.addWhereCondition("trunc(DATE_REQUIRED) between trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) + ? AND trunc(Maintenance_Site_Utility_API.Get_Site_Date(null)) ");
                                qry.addParameter(this,"DUMMY", temp_date_required);
                            }
			}

			qry.includeMeta("ALL");
			qry.setBufferSize(size); 
			qry.skipRows(skip_rows);

			submit(trans);

			db_rows = blk.getASPRowSet().countDbRows();
			ctx.writeValue("DB_ROWS", db_rows+"" );
			ctx.writeFlag("SEARCH_ORDERS",search);
		}
	}
	

	protected void doFreeze() throws FndException
	{
		String pre_name = translate(getDescription());
		super.doFreeze();

	}

	//==========================================================================
	//  Portlet description
	//==========================================================================

	/**
	 * Description that will be shown on the 'Customize Portal' page.
	 */ 
	public static String getDescription()
	{
		return "PCMISSUEMAT: Issue WO Material";
	}

	/**
	 * Create the portlet title for different modes.
	 */
	public String getTitle( int mode )
	{
		if ( mode==MINIMIZED || mode==MAXIMIZED )
		{
			if (Str.isEmpty(name))
				name = translate(getDescription());

			return name;
		}
		else
			return translate("PCMISSUEMATCUST: Customize Issue Material");
	}

	public void printContents()
	{
		printNewLine();

		if (!hide_info_text)
		{
			setFontStyle(ITALIC);
			printText("ISSUEMATDESC1: Not issued material on Active work orders.");
			printText("ISSUEMATDESC2:  In ");
			printScriptLink("ISSUEMATDESCLNK: customize", "customizeIssueMat"); 
			printText("ISSUEMATDESC3:  page you can select search conditions.");
			endFont();
		}

		if (disp_search)
		{
			setFontStyle(BOLD);
			printText("ISSMATSEARCH: Search Criteria:");  
			endFont();

			printText("ISSMATWONO: WO No:");
			printSpaces(2);
			if (!Str.isEmpty(wo_no))
				printText(wo_no);
			else
				printText("ISSMATALL: All"); 
			printNewLine();

			printText("ISSMATPARTNO: Part No:");
			printSpaces(2);
			if (!Str.isEmpty(part_no))
				printText(part_no);
			else
				printText("ISSMATALL: All"); 
			printNewLine();

			printText("ISSMATDATEREQ: Date Required +/-:");
			printSpaces(2);
			if (!Str.isEmpty(date_required))
				printText(date_required);
			else
				printText("ISSMATNONE:  ");  
			printNewLine();
			printNewLine();
		}

		printHiddenField("CMD","");

		int tot_rows = rowset.countRows();

		if (search_auto || search)
		{
			if (tot_rows>0)
			{
				printTable(tbl);

				if (size < db_rows )
				{
					printNewLine();

					if (skip_rows>0)
						printSubmitLink("AUTHOWORKORDERSFIRST: First","firstWO");
					else
						printText("ACTIVEWORKORDERSFIRST: First");

					printSpaces(5);
					if (skip_rows>0)
						printSubmitLink("AUTHOWORKORDERSPRV: Previous","prevWO");
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
						printSubmitLink("ACTIVEWORKORDERSLAST: Last","lastWO");
					else
						printText("ACTIVEWORKORDERSLAST: Last");

					printSpaces(5);
					if (skip_rows<db_rows-size)
						printSubmitLink("ACTIVEWORKORDERSNEXT: Next","nextWO");
					else
						printText("ACTIVEWORKORDERSNEXT: Next");

					printNewLine();
					printNewLine();
				}
			}
			else
			{
				if (!search_auto)
				{
					printSubmitLink("PCMWPORTISSUEMATERIALFINDISSMAT: Search","findWO");
					printNewLine();
					printNewLine();
				}
				setFontStyle(BOLD);    
				printText("ISSMATNODATA: No data found!");
				endFont();
			}
		}
		else
		{
			printSubmitLink("PCMWPORTISSUEMATERIALFINDWO: Search","findWO");
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
		/*
		ASPManager mgr = getASPManager();
		trans = mgr.newASPTransactionBuffer();
		ASPBuffer buf = mgr.newASPBuffer();
		
		if(DEBUG) debug(this+": IssueMaterial.runCustom()");
		
		trans.clear();
		trans.addQuery("WONOBUF","SELECT DISTINCT WO_NO,WO_NO from MAINT_MATERIAL_REQ_LINE WHERE (OBJSTATE IN ('RELEASED','STARTED','WORKDONE'))");
		trans = mgr.perform(trans); 
		*/
	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": IssueMaterial.printCustomBody()");
		}

		runCustom(); // will be called automatically in future release      

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("ISSMATQRYSLC: Query Selections");
		endFont();

		printSpaces(5);
		printText("PCMWISSUEMATERIALPORTCHOOSEWONO: Choose WO No:");
		printNewLine();
		printSpaces(5);
		printField("WO_NO", wo_no, 5, 5);
		//printSelectBox("WO_NO", trans.getBuffer("WONOBUF"), wo_no);
		//printDynamicLOV("WO_NO","ACTIVE_SEPARATE",translate("LISTWONO: List of WO No"),"WO_NO IN (SELECT DISTINCT WO_NO FROM MAINT_MATERIAL_REQ_LINE)");
		printLOV("WO_NO","pcmw/MaintMaterialReqLineLov.page",translate("LISTWONO: List of WO No"),600,445);

		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("CHOOSEPARTNO: Choose Part No:");
		printNewLine();
		printSpaces(5);
		printField("PART_NO", part_no, 5, 5);
		printDynamicLOV("PART_NO","INVENTORY_PART_WO_LOV",translate("LISTPARTNO: List of Part No"),600,445);

		printNewLine();
		printNewLine();
		printSpaces(5);
		printText("DATERANGE: Date Required +/- ");
		//printNewLine();
		//printSpaces(5);
		printField("DATE_RANGE", date_required, 5, 5);

		setFontStyle(BOLD);
		printSpaces(5);
		printText("ISSUEMATPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("ISSUEMATPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("ISSUEMATNAME", name, 45);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("DISPSEARCH", disp_search);
		printSpaces(3);
		printText("DISSEACRI: Display search criteria");
		printNewLine();
		printNewLine();   

		printSpaces(5);
		printCheckBox("HIDEINFOTEXT", hide_info_text);
		printSpaces(3);
		printText("PCMWPORTISSUEMATERIALHIINFOTEX: Hide information text");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("PCMWPORTISSUEMATERIALSEARCHAUTO", search_auto);
		printSpaces(3);
		printText("SEARCHAUTO: Search automatically");
		printNewLine();
		printNewLine();

		printSpaces(5);  
		printField("NO_HITS", no_of_hits, 5);
		printSpaces(3);
		printText("LINEMAXHITISSMAT: Max no of hits to be displayed ");
		size = Integer.parseInt(no_of_hits);
		printNewLine();
		printNewLine();


	}

	public void submitCustomization()
	{
		wo_no = readValue("WO_NO");
		part_no = readValue("PART_NO");
		date_required = readValue("DATE_RANGE");

		name = readValue("ISSUEMATNAME");
		no_of_hits = readValue("NO_HITS");
		disp_search = "TRUE".equalsIgnoreCase(readValue("DISPSEARCH"));
		hide_info_text = "TRUE".equalsIgnoreCase(readValue("HIDEINFOTEXT"));
		search_auto = "TRUE".equalsIgnoreCase(readValue("SEARCHAUTO"));

		writeProfileValue("WONO", readAbsoluteValue("WO_NO"));
		writeProfileValue("PART_NO", readAbsoluteValue("PART_NO"));
		writeProfileValue("DATE_REQUIRED", readAbsoluteValue("DATE_RANGE"));
		writeProfileFlag("DISPSEARCH", disp_search);
		writeProfileFlag("HIDEINFOTEXT", hide_info_text);
		writeProfileFlag("SEARCHAUTO", search_auto);
		writeProfileValue("ISSUEMATNAME", readAbsoluteValue("ISSUEMATNAME"));

		size = Integer.parseInt(no_of_hits);

		if (!Str.isEmpty(no_of_hits))
		{
			writeProfileValue("NO_HITS", readAbsoluteValue("NO_HITS"));
		}
	}

}
