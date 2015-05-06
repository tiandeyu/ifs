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
 * File        : CustomerVisit.java	 
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 * 2003-07-09  YAWILK   Created.
 * 2003-09-19  DIAMLK   Call ID 102973 Removed the Menu Item "Agreement".
 * 2003-09-22  YAWILK   Call ID 101580 Added the Check box Hide Information Text.
 *                      Call ID 101548 Modified the printCustomBody() method.
 *                      Call ID 101574 Changed the current value of the global variable "CUSTOMER_NO"
 *                      When a new Customer is selected.
 * 2003-09-22  YAWILK   Call ID 106188 Modified the init() and submitCustomization() methods.
 * 2003-10-24  ARWILK   (Bug#109221) Changed some links. (Check method comments)
 * 2003-10-30  YAWILK   Call ID 109664 URLEncoded the customerNo and added additional filteratio to OBJSTATE
 * 			in the links Current Co and Closed Co.
 * 040309      VAGULK   Web Alignment ,Removed Clone() and doReset() methods and unnecessary global variables.
 * 041108      NAMELK   Non Standard Translation Tags Changed. 
 * 060802      AMNILK   Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
 * 060807      AMDILK   Merged with Bug Id 58214
 * 060823      DIAMLK   Bug 58216, Eliminated SQL Injection security vulnerability.
 * 060901      NAMELK   merged bug 58216.
 * 070130      DINHLK   Merged from Wings, Call 128873, Renamed the Customer Visit Portlet to "MRO Shop Visit".
 * 070320      AMDILK   Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * 070531      AMDILK   Modified submitCustomization()
 * 080715      SHAFLK   Bug 75697, Removed DEBUG value set to true.
 * ---------------------------------------------------------------------------
 *
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;

public class CustomerVisit extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.portlets.CustomerVisit");

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================

	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

    //==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================

	private transient ASPTransactionBuffer trans;

	//==========================================================================
	//  Profile variables (temporary)
	//==========================================================================

	private transient String  customerNo;

	// Choosen name by a user or default name on creation.
	private transient String  name;

	// Customization flags
	private transient boolean vimInstalled;
	private transient boolean again;
	private transient boolean infotextflag;	 // Hide the information text

	//==========================================================================
	//  Construction
	//==========================================================================

	public CustomerVisit( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": CustomerVisit.<init> :"+portal+","+clspath);
	}


	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================


	protected void doFreeze() throws FndException
	{
		String pre_name = translate(getDescription());
		super.doFreeze();
	}

	//==========================================================================
	// Configuration and initialisation
	//==========================================================================

	protected void preDefine() throws FndException
	{
		if (DEBUG) debug(this+": CustomerVisit.preDefine()");

		blk = newASPBlock("MAIN");
		blk.newASPCommandBar();

		addField(blk, "CUSTOMER_ID").setReadOnly().setLabel("VIMMRWCUSTOMERNO: Customer No:");
		addField(blk, "NAME").setReadOnly().setLabel("VIMMRWCUSTOMERNAME: Customer Name:");
		addField(blk, "LU_EXIST").setHidden().setFunction("''");
		addField(blk, "LU_NAME").setHidden().setFunction("''");

		blk.setView("CUSTOMER_INFO");

		tbl = newASPTable( blk );
		tbl.disableQueryRow();
		tbl.disableQuickEdit();
		tbl.disableRowCounter();
		tbl.disableRowSelect();
		tbl.disableRowStatus();
		tbl.unsetSortable();

		rowset = blk.getASPRowSet();

		try
		{
			appendJavaScript( "function portalCustomize(obj,id)",
							  "{",
							  "   customizeBox(id);",
							  "}\n");

		}
		catch (FndException e)
		{
		}
		// mutable attribute
		name = translate(getDescription());
		// call the init()
		init();
	}

	protected void init()
	{
		ASPManager mgr = getASPManager();

		if (DEBUG) debug(this+": CustomerVisit.init()");

		again   = readProfileFlag ( "AGAIN", false );

		name = readProfileValue( "VIMMRWCUSTOMERVISITNAME", name);
		vimInstalled   = readProfileFlag ( "VIMINSTALLED", false ); 
		infotextflag   = readProfileFlag ( "INFOTEXT", false );     

		if (mgr.getPortalPage().getASPPortal().isGlobalVariableDefined("CUSTOMER_NO"))
		{
			StringTokenizer st = new StringTokenizer(mgr.getPortalPage().getASPPortal().getGlobalVariable("CUSTOMER_NO"), ",");
			customerNo = st.nextToken();
		}
		else
		{
			customerNo = mgr.isEmpty(readProfileValue("CUSTOMERNO"))?"": readProfileValue("CUSTOMERNO"); 
			if (!mgr.isEmpty(customerNo))
				mgr.getPortalPage().getASPPortal().setGlobalVariable("CUSTOMER_NO",customerNo);
		}
	}

	protected void run()
	{
		if (DEBUG) debug(this+": CustomerVisit.run()");

		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		//Check if Vim Installed
		if (!again)
		{
			ASPCommand cmd             = mgr.newASPCommand();
			ASPBuffer data             = mgr.newASPBuffer();
			trans.addSecurityQuery("ENG_PART_REVISION_SERIAL");
			trans = mgr.perform(trans);         
			data = trans.getSecurityInfo();
			if (data.itemExists("ENG_PART_REVISION_SERIAL"))
				vimInstalled = true;
			else
				vimInstalled = false;
			again = true;
			writeProfileFlag("VIMINSTALLED", vimInstalled);
			writeProfileFlag("AGAIN", again);
		}

		trans = getASPManager().newASPTransactionBuffer();

		ASPQuery qry = trans.addEmptyQuery(blk);
		qry.addWhereCondition("CUSTOMER_ID = ?");
                qry.addParameter(this,"CUSTOMER_ID",customerNo);
		qry.includeMeta("ALL");

		submit(trans);
	}

	//==========================================================================
	//  Portlet description
	//==========================================================================

	public static String getDescription()
	{       
                //(+/-) WINGS (START)
		return "MROSHOPVISITLBL: MRO Shop Visit";
                //(+/-) WINGS (END)
	}

	public String getTitle( int mode )
	{
		if (DEBUG) debug(this+": CustomerVisit.getTitle()");

		if (mode==MINIMIZED || mode==MAXIMIZED)
		{
			if (Str.isEmpty(name))
				name = translate(getDescription());

			return name;
		}
		else
                {
                    //(+/-) WINGS (START)
                    return translate("MROSHOPVISITCUSTMIZE: Customize MRO Shop Visit");
                    //(+/-) WINGS (END)
                }
			
	}

	public void printContents()
	{
		if (DEBUG) debug(this+": CustomerVisit.printContents()");
		// Information text, shown by default.
		ASPManager mgr = getASPManager();
		if (!infotextflag)
		{
			setFontStyle(ITALIC);
			printNewLine();
                        //(+/-) WINGS (START)
			printText("PCMWMROSHOPVISITPORTCUSVISBASDEP: This view present MRO Shop Visit Basic depending "); 
                        //(+/-) WINGS (END)
			printText("PCMWCUSTOMERVISITPORTSRCHCONDGVN: on the search conditions given. To see or alter ");
			printText("PCMWCUSTOMERVISITPORTACTCOND: actual conditions you can go to the "); 
			printScriptLink("POTELCUSTOMIZE: customize","portalCustomize");
			printText(" page.");
			printNewLine();
			endFont();
		}

		printText("PCMWCUSTOMERVISITPORTSCHCTR: Search Criteria:");
		printTable(tbl);
		printNewLine();
		if (vimInstalled)
		{
			//(+/-) WINGS (START)
                        printLink("MROSHOPVISITDEFAULTS: MRO Shop Visit Defaults","vimmrw/CustomerVisitDefaults.page?SEARCH=Y&CUSTOMER_NO="+customerNo+"");
                        //(+/-) WINGS (END)
			printNewLine();
		}
		printLink("CURRENTWO: Current WOs","pcmw/ActiveSeparate2lightSM.page?SEARCH=Y&CUSTOMER_NO="+mgr.URLEncode(customerNo)+"");
		printNewLine();
		// 031024  ARWILK  Begin  (Bug#109221)
		printLink("PCMWCUSTOMERVISITPORTHISTORICALWO: Historical WOs","pcmw/HistoricalSeparateRMB.page?SEARCH=Y&CUSTOMER_NO="+mgr.URLEncode(customerNo)+"");
		// 031024  ARWILK  End  (Bug#109221)
		printNewLine();
		printLink("CURRENTCO: Current COs","orderw/CustomerOrder.page?SEARCH=Y&CUSTOMER_NO="+mgr.URLEncode(customerNo)+"&OBJSTATE=Released;Planned;Reserved;Picked;PartiallyDelivered&LAYOUT=OVERVIEW");
		printNewLine();
		printLink("CLOSEDCO: Closed COs","orderw/CustomerOrder.page?SEARCH=Y&CUSTOMER_NO="+mgr.URLEncode(customerNo)+"&OBJSTATE=Invoiced;Delivered&LAYOUT=OVERVIEW");
	}

	public boolean canCustomize()
	{
		return true;
	}

	public void printCustomBody()
	{
		if (DEBUG)
		{
			debug(this+": CustomerVisit.printCustomBody()");
			debug("\tprintCustomBody(): CustomerVisit");
		}

		printNewLine();
		setFontStyle(BOLD);
		printSpaces(5);
		printText("VIMMRWCUSTOMERVISITPORTLETCONFIG: Portlet Configuration");
		endFont();

		printSpaces(5);
		printText("VIMMRWCUSTOMERVISITPORTLETNAME: Portlet name: ");
		printNewLine();
		printSpaces(5);
		printField("VIMMRWCUSTOMERVISITNAME", name, 30);
		printNewLine();
		printNewLine();

		printSpaces(5);
		printText("VIMMRWCUSTOMERVISITCUSTOMERNUMBER: Customer No: ");
		printNewLine();
		printSpaces(5);
		printField("CUSTOMERNO", customerNo, 30);
		printDynamicLOV("CUSTOMERNO","CUSTOMER_INFO","LOVTITLE: List Of Customers");
		printNewLine();
		printNewLine();

		printSpaces(5);
		printCheckBox("VIMMRWCUSTOMERVISITINFOTEXT", infotextflag);
		printSpaces(3);
		printText("VIMMRWCUSTOMERVISITTEXTINFOTEXT: Hide Information Text");
		printNewLine();

	}

	public void submitCustomization()
	{
		ASPManager mgr = getASPManager();
		name = readValue("VIMMRWCUSTOMERVISITNAME");
		customerNo = readValue("CUSTOMERNO");
		// Write values to the profile.
		writeProfileValue("VIMMRWCUSTOMERVISITNAME", readAbsoluteValue("VIMMRWCUSTOMERVISITNAME"));
		writeProfileValue("CUSTOMERNO",  readAbsoluteValue("CUSTOMERNO") );
		//New Global Customer Number
		if (!mgr.isEmpty(customerNo))
			mgr.getPortalPage().getASPPortal().setGlobalVariable("CUSTOMER_NO",customerNo);

		infotextflag = "TRUE".equals(readValue("VIMMRWCUSTOMERVISITINFOTEXT")); 
		writeProfileFlag("INFOTEXT", infotextflag);

	}
}
