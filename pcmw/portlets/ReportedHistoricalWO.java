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
 * File        : ReportedHistoricalWO.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * 040420  BAKALK  Created.
 * 040422  BAKALK  Modified getWhereCondition().
 * 040427  BAKALK  Call Id:114400 changed the view column of "completion date". Modified where condition.
 *                 Call Id:114401 Added radio button for ascending and descending order. Modified Order by clasue too.
 * 040428  BAKALK  Call Id:114399 Added a validation for Time Period to check format of the dates. Added a new lov page for Object id.
 * 040428  BAKALK  Call Id:114398 1. Added a check box "show criteria" in the Configuration page.
 *                 2. Added the search criteria in printContents().
 *                 3. Added portlet description at just above search criteria.
 *                 4. Changed the portlet description from HTML field to Text Area.
 *                 5. Portlet Title changed to "Maintenance - Historical Work Orders". Modified getTilte() too.
 * 040519  BAKALK  Call Id:114598. Modified getWhereCondition() and added new methods: getCondition4EachValue(),getCondition4Column(),
 *                 getCondition4StringColumn(),getCondition4NumberColumn() and getCondition4StringColumn().
 * 040520  BAKALK  Call Id:114596. Modified printContents() and printCustomBody(),added a new method wrapInSmallFont(String).
 * 040521  BAKALK  Call Id:114598. Modified getCondition4EachValue().
 * 040628  DHWELK  Call Id:115497. Added mask to WO_NO.
 * 040826  ARWILK  Call ID:117364. Modified method init.
 * 050715  PRWELK  Call ID:115498. Action menu item "Open Design Object" is set to open in a new window.  
 * 050720  prwelk  Call ID 124982  Auto serach button problem.
 * 060302  chodlk  call ID 136024  Modified method getCondition4EachValue.
 * ----------------------------------------------------------------------------------------
 * 061012  SHTHLK  Bug Id 57093,   Replaced PlantNavigator.page with PlantNavigatorFrameset.page
 * 070301  ILSOLK  Merged Bug Id 57093.
 * 060914  SHTHLK  Bug Id 57189,   Modified OpenDesignObjecsInNewWindow() to validate the plant before opening Plant Navigator 
 * 070301  ILSOLK  Merged Bug Id 57189. 
 * 070320  AMDILK  Modified submitCustomization(), inserted readAbsoluteValue() when writing to the profile
 * 070321  SHAFLK  Bug Id 64307,   Modified run(), printCustomBody().
 * 070409  AMDILK  Merged bug id 64307
 * 070528  CHANLK  Call 144554 Protal Historical Wo does not populate Automatically.
 * 070531  AMDILK  Modified submitCustomization()
 * 070503  CHODLK  Bug Id 65076,   Modified method run().
 * 070711  CHANLK  Merged bug 65076
 * 070725  AMNILK  Eliminated SQL Injections Security Vulnerability. Added new methods prepareWhereCondForDate(),prepareWhereCondForEach() 
 *                 and prepareWhereCondition() in order to simplify the filtering portal and bind the variables.
 * 070727  AMNILK  Eliminated XSS Security Vulnerability.
 * 070731  ILSOLK  Eliminated LocalizationErrors.
 * 080410  NIJALK  Bug 73042, Modified preDefine().
 * --------------------------------------------------------------------------------------------
 */

package ifs.pcmw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

public class ReportedHistoricalWO extends ASPPortletProvider
{
	//==========================================================================
	//  Static constants
	//==========================================================================

	public static boolean DEBUG = Util.isDebugEnabled("ifs.portal.ReportedHistoricalWO");
	private final static String transl_prefix = "PCMWREPORTEDHISTORICALWO";
	private static int size ;

	//==========================================================================
	//  instances created on page creation (immutable attributes)
	//==========================================================================
	private ASPContext ctx;
	private ASPBlock   blk;
	private ASPRowSet  rowset;
	private ASPTable   tbl;

	//==========================================================================
	//  Mutable attributes
	//==========================================================================

	//==========================================================================
	//  Transient temporary variables (never cloned)
	//==========================================================================
	//variable for diciding what to display.
	private transient int      skip_rows;
	private transient int      db_rows;

	private String  show_wo_no;
	private String  show_directive;
	private String  show_work_done;
	private String  show_object_id;
	private String  show_class;
	private String  show_performed_action;
	private String  show_type;
	private String  show_cause;
	private String  show_total_cost;
	private String  show_completion_date;

	//variable for where condition
	private String  err_class_value;			// class
	private String  performed_action_id_value;	// performed action
	private String  err_type_value;				// type
	private String  err_cause_value;			// cause
	private String  mch_code_value;				// object id 
	private String  start_date_value;			 // start date  
	private String  finish_date_value;			// finish date
	//where condition
	private String where_condition;
	//private String where_con_orderby_compleDT;
	private String  auto_display;
	private String  portlet_desc;
	private String  order_by_value;
	private String  order_style_value;
	private String  check_show_criteria_value;
	private boolean bPopulate ;
   private boolean bCanPopulate;
   
  // private String sWinUrl="";
  // private String isKeyaEmpty="TRUE";
	//==========================================================================
	//  Construction
	//==========================================================================

	public ReportedHistoricalWO( ASPPortal portal, String clspath )
	{
		super(portal, clspath);
		if (DEBUG) debug(this+": ReportedHistoricalWO.<init> :"+portal+","+clspath);
	}

	public ASPPage construct() throws FndException
	{
		if (DEBUG) debug(this+": ReportedHistoricalWO.construct()");
		return super.construct();
	}

	//==========================================================================
	//  Methods for implementation of pool
	//==========================================================================

	protected void doFreeze() throws FndException
	{
		if (DEBUG) debug(this+": ReportedHistoricalWO.doFreeze()");
		super.doFreeze();
	}


	protected void doActivate() throws FndException
	{
		if (DEBUG) debug(this+": ReportedHistoricalWO.doActivate()");
		super.doActivate();
	}

	//==========================================================================
	//  
	//==========================================================================

	protected void preDefine()
	{
		ctx = getASPContext();

		String class_            = translate("PCMWREPORTEDHISTORICALWOCLASS: Class");
		String performed_action_ = translate("PCMWREPORTEDHISTORICALWOPERFORMEDACTION: Performed Action");
		String type_             = translate("PCMWREPORTEDHISTORICALWOTYPE: Type");
		String cause_            = translate("PCMWREPORTEDHISTORICALWOCAUSE: Cause");

		blk = newASPBlock("HistWork");
		//WO_NO
		ASPField work_order_field       = addField(blk,"WO_NO","Number","#").setHidden().setHyperlink("pcmw/HistoricalSeparateRMB.page","WO_NO").setLabel("PCMWREPORTEDHISTORICALWOWONO: Wo No");//wo no
		addField(blk,"ERR_DESCR").setHidden().setLabel("PCMWREPORTEDHISTORICALWODIRECTIVE: Directive");//directive
		addField(blk,"WORK_DONE").setHidden().setLabel("PCMWREPORTEDHISTORICALWOWORKDONE: Work Done");//WORK DONE
		ASPField object_id_field        = addField(blk,"MCH_CODE").setHidden().setHyperlink("equipw/EquipmentAllObjectLight.page","MCH_CODE").setLabel("PCMWREPORTEDHISTORICALWOOBJECTID: Object Id");//object id ,MCH_CODE_CONTRACT
		ASPField object_desc_field      = addField(blk,"MCH_CODE_DESCRIPTION").setHidden();//object id desc
		ASPField error_class_field      = addField(blk,"ERR_CLASS").setHidden().setLabel("PCMWREPORTEDHISTORICALWOCLASS: Class");//class
		addField(blk,"ERR_CLASS_DESC").setHidden().setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(ERR_CLASS)");//class desc
		ASPField performed_action_field = addField(blk,"PERFORMED_ACTION_ID").setHidden().setLabel("PCMWREPORTEDHISTORICALWOPERFORMEDACTION: Performed Action");//Performed Action
		addField(blk,"PERFORMED_ACTION_ID_DESC").setHidden().setFunction("MAINTENANCE_PERF_ACTION_API.Get_Description(PERFORMED_ACTION_ID)");///Performed Action DESC
		ASPField err_type_field         = addField(blk,"ERR_TYPE").setHidden().setLabel("PCMWREPORTEDHISTORICALWOTYPE: Type");//type
		addField(blk,"ERR_TYPE_DESC").setHidden().setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(ERR_TYPE)");///type desc
		ASPField err_cause_field        = addField(blk,"ERR_CAUSE").setHidden().setLabel("PCMWREPORTEDHISTORICALWOCAUSE: Cause");//cause
		addField(blk,"ERR_CAUSE_DESC").setHidden().setFunction("MAINTENANCE_CAUSE_CODE_API.Get_Description(ERR_CAUSE)");///type desc
		//Bug 73042, Start, Modified the data type
                addField(blk,"TOTAL_COST","Money").setHidden().setLabel("PCMWREPORTEDHISTORICALWOTOTALCOST: Total Cost").setFunction("HIST_WORK_ORDER_BUDGET_API.Get_Total_Cost(WO_NO)");///TOTAL COST THIS SERVER METHOD MUST BE WRITTEN
                //Bug 73042, End
		addField(blk,"REAL_F_DATE","Date").setLabel("PCMWREPORTEDHISTORICALWOCOMPLETIONDATE: Completion Date").setHidden();//Completion Date
		ASPField work_order_desc_field  = addField(blk,"WO_NO_DESC").setHidden().setFunction("'"+class_+"='||ERR_CLASS||';'||"+"'"+performed_action_+"='||PERFORMED_ACTION_ID||';'||"+"'"+type_+"='||ERR_TYPE||';'||"+"'"+cause_+"='||ERR_CAUSE||';'");
		
		
		addField(blk,"DUMMY_DATE").setFunction("''").setHidden();// Dummy field for bind variables for Real_F_Date

		object_id_field.setTooltip(object_desc_field);
		work_order_field.setTooltip(work_order_desc_field);

		ASPCommandBar bar = blk.newASPCommandBar();
		
		bar.addCustomCommand("openDesignObject",  "PCMWPORTLETSPCMWREPORTEDHISTORICALWOOPDESIGNOBJ: Open Design Object");
		bar.defineCommand("openDesignObject",null,"OpenDesignObjecsInNewWindow");
		bar.disableCommand(bar.DUPLICATE);
		bar.enableMultirowAction();

		blk.setView("HISTORICAL_SEPARATE");

		tbl = newASPTable( blk );

		rowset = blk.getASPRowSet();

		init();
	}

	protected void init()// throws FndException
	{
		ASPManager mgr = getASPManager();
		bCanPopulate=false;
		bPopulate =false;

		//db_rows   = (new Integer(readProfileValue("DB_ROWS","0"))).intValue();
		db_rows   = (new Integer(readProfileValue("DB_ROWS",ctx.readValue("CTXDBROWS", "0")))).intValue();
		skip_rows = (new Integer(readProfileValue("SKIP_ROWS",ctx.readValue("CTXSKIPROWS", "0")))).intValue();
      
      if ("T".equalsIgnoreCase(readProfileValue("CHECK_DISPLAY_AUTO")))//r
      {
         bCanPopulate=true;
         bPopulate=true;
      }
      
		show_wo_no            = readProfileValue("SHOW_WO_NO", "T");
		show_directive        = readProfileValue("SHOW_DIRECTIVE", "T");
		show_work_done        = readProfileValue("SHOW_WORK_DONE", "T");
		show_object_id        = readProfileValue("SHOW_OBJECT_ID", "T");
		show_class            = readProfileValue("SHOW_CLASS", "F");
		show_performed_action = readProfileValue("SHOW_PERFORMED_ACTION", "F");
		show_type             = readProfileValue("SHOW_TYPE", "F");
		show_cause            = readProfileValue("SHOW_CAUSE", "F");
		show_total_cost       = readProfileValue("SHOW_TOTAL_COST", "T");
		show_completion_date  = readProfileValue("SHOW_COMPLETION_DATE", "T");

		err_class_value           = readProfileValue("ERR_CLASS", "");
		performed_action_id_value = readProfileValue("PERFORMED_ACTION_ID", "");
		err_type_value            = readProfileValue("ERR_TYPE", "");
		err_cause_value           = readProfileValue("ERR_CAUSE", "");
		mch_code_value            = readProfileValue("MCH_CODE", "");
		start_date_value          = readProfileValue("START_DATE", "");
		finish_date_value         = readProfileValue("FINISH_DATE", "");
		
		where_condition         = readProfileValue("WHERE_CONDITION","CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");

		auto_display         = readProfileValue("CHECK_DISPLAY_AUTO", "T");//default is set to checked
		portlet_desc         = readProfileValue("PORTLET_DESC", "");
		size                 = (new Integer(readProfileValue("MAX_NO", "15"))).intValue();
		order_by_value       = readProfileValue("ORDER_BY_VALUE", "REAL_F_DATE");
		order_style_value    = readProfileValue("ORDER_STYLE_VALUE", "DESC");

		check_show_criteria_value = readProfileValue("CHECK_SHOW_CRITERIA_VALUE", "F");


		// we have to calculate  the no of the row to be shown as the first
		String cmd = readValue("CMD");

		if (Str.isEmpty(cmd))
		{
			if (cmdBarCustomCommandActivated())
				cmd = getCmdBarCustomCommandId();
		}
		if (!mgr.isEmpty(cmd))
		{
			if ("PREV".equals(cmd) && skip_rows>=size)
			{
				skip_rows -= size;
				writeProfileValue("SKIP_ROWS", (skip_rows+"") );
            ctx.writeValue("CTXSKIPROWS",skip_rows+"");
				bPopulate = true;
			}
			else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
			{
				skip_rows += size;
				writeProfileValue("SKIP_ROWS", (skip_rows+"") );
            ctx.writeValue("CTXSKIPROWS",skip_rows+"");
				bPopulate = true;
			}
			//else if ("OPEN_DESIGN_OBJECT".equals(cmd))
			//	open_design_object();
			else if ("FIND".equals(cmd))
				bPopulate = true;
         
        // else if ("T".equals(auto_display))
        //   bPopulate = true;
         
         
		}
	}
  // dummy function never calls but needs for run time .replaced client funcation with the same name 
	public void openDesignObject()
	{
      /*
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		rowset.selectRows();
		rowset.storeSelections();
		rowset.setFilterOn();

		String object_id = rowset.getRow().getValue("MCH_CODE") ;
		rowset.setFilterOff();

		ASPQuery qry = trans.addQuery("PLANTNAVIGATORPROPERTY","SELECT KEYA,STD_SQ, PLT_SQ,PLANT_PLANT_API.GET_PLT_NAME(PLT_SQ) PLT_NAME,ARTSTD_SQ,OBJECT_SQ,OBJECT_REVISION FROM PLANT_OBJECT WHERE KEYA = '" + object_id+"'");

		trans = mgr.perform(trans);
		String keya            = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/KEYA");
		String std_sq          = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/STD_SQ");
		String plt_sq          = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/PLT_SQ");
		String plt_name        = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/PLT_NAME");
		String artstd_sq       = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/ARTSTD_SQ");
		String object_sq       = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/OBJECT_SQ");
		String object_revision = trans.getValue("PLANTNAVIGATORPROPERTY/DATA/OBJECT_REVISION");

		trans.clear();

		String sUrl = "pladew/PlantNavigator.page"+
					  "?sSTD_SQ="+std_sq+
					  "&sPLT_SQ="+plt_sq+
					  "&sPLT_NAME="+plt_name+
					  "&sARTSTD_SQ="+artstd_sq+
					  "&sObjectKey=o%3A" +(object_sq)+ "%1F" +(object_revision)+ "%1F" +(plt_sq)+ "%1F";

		if (!Str.isEmpty(keya))
      {//mgr.redirectTo(sUrl);
         //try
         //{
         //   appendDirtyJavaScript("window.open("+"\""+sUrl+"\""+",'anotherWindow2','status,resizable,scrollbars,width=825,height=550,left=70,top=50');\n");
         //   bPopulate = true;//populate the row set again
        // }
        // catch (Exception e){;;}
         //isKeyaEmpty="FALSE";
         //sWinUrl=sUrl;
      } 
		else
		{
			mgr.showAlert(translate("PCMWREPORTEDHISTORICALWODESIGNOBJECTNOTEXIST: Corresponding design object does not exist!"));
			bPopulate = true;
		}*/
	}

	
	protected void run()
	{
		if (DEBUG) debug(this+": PcmwReportedHistoricalWO.run()");

		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

		if ("F".equalsIgnoreCase(show_wo_no))
			getASPField("WO_NO").setHidden();
		else
			getASPField("WO_NO").unsetHidden();

		if ("F".equalsIgnoreCase(show_directive))
			getASPField("ERR_DESCR").setHidden();
		else
			getASPField("ERR_DESCR").unsetHidden();   

		if ("F".equalsIgnoreCase(show_work_done))
			getASPField("WORK_DONE").setHidden();
		else
			getASPField("WORK_DONE").unsetHidden();     

		if ("F".equalsIgnoreCase(show_object_id))
			getASPField("MCH_CODE").setHidden();
		else
			getASPField("MCH_CODE").unsetHidden();       

		if ("F".equalsIgnoreCase(show_class))
			getASPField("ERR_CLASS").setHidden();
		else
			getASPField("ERR_CLASS").unsetHidden(); 

		if ("F".equalsIgnoreCase(show_performed_action))
			getASPField("PERFORMED_ACTION_ID").setHidden();
		else
			getASPField("PERFORMED_ACTION_ID").unsetHidden(); 

		if ("F".equalsIgnoreCase(show_type))
			getASPField("ERR_TYPE").setHidden();
		else
			getASPField("ERR_TYPE").unsetHidden();  

		if ("F".equalsIgnoreCase(show_cause))
			getASPField("ERR_CAUSE").setHidden();
		else
			getASPField("ERR_CAUSE").unsetHidden(); 

		if ("F".equalsIgnoreCase(show_total_cost))
			getASPField("TOTAL_COST").setHidden();
		else
			getASPField("TOTAL_COST").unsetHidden(); 

		if ("F".equalsIgnoreCase(show_completion_date))
			getASPField("REAL_F_DATE").setHidden();
		else
			getASPField("REAL_F_DATE").unsetHidden();    

		if ("T".equals(auto_display)|| bPopulate)
      //if ( bPopulate)
      {
			ASPQuery             qry   = trans.addEmptyQuery(blk);
			if (!Str.isEmpty(order_by_value) && !("NONE".equals(order_style_value)))
			{
            qry.setOrderByClause(order_by_value + " " + order_style_value);
         }


			qry.setBufferSize(size);
			qry.skipRows(skip_rows);
			qry.includeMeta("ALL");

		        prepareWhereCondition(qry);

			submit(trans);
			db_rows = blk.getASPRowSet().countDbRows();
			trans.clear();
			writeProfileValue("DB_ROWS", (db_rows+"") );
         
         if ("T".equals(auto_display))
            ctx.writeValue("CTXDBROWS",   db_rows+"");
       }
	}

	private String removeWhiteSpaces(String strWithSpaces)
	{
		char current_char;
		char temp_char = ' ';
		String ret_str = "";

		ASPManager mgr = this.getASPManager();

		if (mgr.isEmpty(strWithSpaces))
			return "";
		for (int k=0;k<strWithSpaces.length();k++)
		{
			current_char = strWithSpaces.charAt(k);

			if (current_char!= temp_char)
			{
				ret_str +=  current_char +"";
			}
		}

		return ret_str;
	}

	public String getWhereCondition()
	{
		ASPManager mgr = getASPManager();
		String return_str   = "";
		String start_string = ""; 
		String condition4CurrentCol = "";

		return_str   = getCondition4StringColumn("ERR_CLASS",err_class_value);

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4StringColumn("PERFORMED_ACTION_ID",performed_action_id_value);

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4StringColumn("ERR_TYPE",err_type_value);

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4StringColumn("ERR_CAUSE",err_cause_value);

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4StringColumn("MCH_CODE",mch_code_value);

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4DateColumn("REAL_F_DATE",start_date_value,">=");

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		start_string = mgr.isEmpty(return_str)?"":" AND ";
		condition4CurrentCol = getCondition4DateColumn("REAL_F_DATE",finish_date_value,"<=");

		if (!mgr.isEmpty(condition4CurrentCol))
		{
			return_str  += start_string + condition4CurrentCol;
		}

		// to filter by contract.
		start_string = mgr.isEmpty(return_str)?"":" AND ";
		return_str += start_string + "CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)";
		return return_str;
	}

	private String getCondition4EachValue(String view_column,String current_value,int stringNumberOrDate, String dateOperation)
	{
		ASPManager mgr = getASPManager();

		if (mgr.isEmpty(current_value))
			return "";
		if ("%".equals(current_value))
			return view_column+" IS not NULL";

		if (stringNumberOrDate==1)
		{ //String 

			if (current_value.indexOf("%")>-1)
			{
				return view_column+" LIKE '"+current_value+"'";
			}
			else
			{
				return view_column+"='"+current_value+"'";
			}
		}
		else if (stringNumberOrDate==2)
		{ //Number
			return view_column+"="+current_value;
		}
		else if (stringNumberOrDate==3)
		{ //Date
			return view_column+" "+ dateOperation +" TO_DATE('"+current_value+"','yyyy-MM-dd')";
      }
		return "";
	}
	/**
	 * for String type column
	*/
	private String getCondition4StringColumn(String view_column,String field_value)
	{
		return getCondition4Column(view_column,field_value,1,"");
	}

	/**
	 * for Number type column
	*/
	private String getCondition4NumberColumn(String view_column,String field_value)
	{
		return getCondition4Column(view_column,field_value,2,"");
	}

	/**
	 * for Date type column,
	 * here dateOperation can take any of "=",">=","<=",">","<"
	*/
	private String getCondition4DateColumn(String view_column, String field_value, String dateOperation )
	{
		return getCondition4Column(view_column,field_value,3,dateOperation);
	}

	private String getCondition4Column(String view_column,String field_value,int stringNumberOrDate,String dateOperation)
	{
		String return_str = "";
		String start_str  = "";
		int temp_counter  = 0;	   //just to check the first vlue in tokenizer
		ASPManager mgr = this.getASPManager();

		if (mgr.isEmpty(field_value))
		{
			return "";
		}
		else
		{
			StringTokenizer comma_tokenizer = new StringTokenizer(field_value,";");
			return_str = "("  ;
			while (comma_tokenizer.hasMoreElements())
			{
				start_str = temp_counter==0? "":" OR ";
				return_str += start_str + getCondition4EachValue(view_column,comma_tokenizer.nextToken(),stringNumberOrDate,dateOperation);
				temp_counter++;
			}

			return_str += ")";
		}

		return return_str;
	}

	private void prepareWhereCondition(ASPQuery q){

	    ASPManager mgr = getASPManager();
    
	    if (!mgr.isEmpty(err_class_value)) {
		prepareWhereCondForEach( q,"ERR_CLASS",err_class_value);
	    }
	    if (!mgr.isEmpty(performed_action_id_value)) {
		prepareWhereCondForEach( q,"PERFORMED_ACTION_ID",performed_action_id_value);
	    }
	    if (!mgr.isEmpty(err_type_value)) {
		prepareWhereCondForEach( q,"ERR_TYPE",err_type_value);
	    }
	    if (!mgr.isEmpty(err_cause_value)) {
		prepareWhereCondForEach( q,"ERR_CAUSE",err_cause_value);
	    }
	    if (!mgr.isEmpty(mch_code_value)) {
		prepareWhereCondForEach( q,"MCH_CODE",mch_code_value);
	    }
	    if (!mgr.isEmpty(start_date_value)) {
		prepareWhereCondForDate( q,"REAL_F_DATE",start_date_value," >= ");
	    }
	    if (!mgr.isEmpty(finish_date_value)) {
		prepareWhereCondForDate( q,"REAL_F_DATE",finish_date_value," <= ");
	    }
    
	    q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
	}

	private void prepareWhereCondForEach(ASPQuery q,String field_name, String field_value){

	    ASPManager mgr = getASPManager();

	    ASPBuffer paramBuff = mgr.newASPBuffer();

	    String start_str = ""; 
	    StringTokenizer comma_tokenizer;
	    String current_value;

	    String strWhereCond = "";

	    int temp_counter  = 0;	   //just to check the first value in tokenizer

	    comma_tokenizer = new StringTokenizer(field_value,";");

	    while (comma_tokenizer.hasMoreElements())
	    {
		
		start_str = temp_counter==0? "":" OR ";
    
		current_value = comma_tokenizer.nextToken();
		
		if (!mgr.isEmpty(current_value)){
    
		     if ("%".equals(current_value))
			 strWhereCond = strWhereCond + start_str +  field_name + " IS NOT NULL";
    
		     else if (err_class_value.indexOf("%")>-1){
			  strWhereCond = strWhereCond + start_str + field_name +" LIKE ?";
			  paramBuff.addItem(field_name,current_value);
    
		     }else{
			  strWhereCond = strWhereCond + start_str +  field_name + " = ?";
			  paramBuff.addItem(field_name,current_value);
		     }
		temp_counter++;
		}
	    }

	    q.addWhereCondition(strWhereCond);

	    for (int i = 0; i < paramBuff.countItems(); i++)
	    {
                q.addParameter(this,paramBuff.getNameAt(i),paramBuff.getValueAt(i));	//SQLInjection_Safe 20070725 AMNILK
	    }

	}

	private void prepareWhereCondForDate(ASPQuery q,String field_name, String field_value, String oper){

	    ASPManager mgr = getASPManager();

	    ASPBuffer paramBuff = mgr.newASPBuffer();

	    String start_str = ""; 
	    StringTokenizer comma_tokenizer;
	    String current_value;

	    String strWhereCond = "";

	    int temp_counter  = 0;	   //just to check the first value in tokenizer

	    comma_tokenizer = new StringTokenizer(field_value,";");

	    while (comma_tokenizer.hasMoreElements())
	    {
		start_str = temp_counter==0? "":" OR ";

		current_value = comma_tokenizer.nextToken();
		
		if (!mgr.isEmpty(current_value)){

		     if ("%".equals(current_value))
			 strWhereCond = strWhereCond + start_str +  field_name + " IS NOT NULL";
		     else{
			  strWhereCond = strWhereCond + start_str +  field_name + oper + "TO_DATE( ? ,'yyyy-MM-dd')";
			  paramBuff.addItem(field_name,current_value);
		     }
		temp_counter++;
		}
	    }

	    q.addWhereCondition(strWhereCond);

	    for (int i = 0; i < paramBuff.countItems(); i++)
	    {
                q.addParameter(this,"DUMMY_DATE",paramBuff.getValueAt(i));	//SQLInjection_Safe 20070725 AMNILK
	    }

	}

	//==========================================================================
	//  
	//==========================================================================

	public String getTitle( int mode )
	{
		ASPManager mgr = getASPManager();
		String temp_desc = translate(getDescription());

		if (mode==ASPPortal.MINIMIZED)
			return temp_desc + mgr.translate("PCMWREPORTEDHISTORICALWOHISWORKORDNO:  - You have &1 Work Orders.", Integer.toString(db_rows));
		else
			return temp_desc;
	}

	private String wrapInSmallFont(String text)
	{
		if (Str.isEmpty(text))
			return "";

		return("<font class=\"WriteTextValue\" face=\"Verdana\" size=\"1\" >" + text + "</font>");
	}

	public void printContents() throws FndException
	{
		ASPManager mgr = this.getASPManager();

		if (DEBUG) debug(this+": PcmwReportedHistoricalWO.getContents()");

		// hidden field for next and previous links
		printHiddenField("CMD","");
		// portlet description must come here
		if (!Str.isEmpty(portlet_desc))
		{
			printText(portlet_desc);
		}

		//search criteria.
		if ("T".equals(check_show_criteria_value))
		{
			//=========== main table : to leave a space 
			appendToHTML("<!-- table start -->");
			appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"); 
			appendToHTML("<tr>\n"); 
			appendToHTML("<td width=\"10\"></td>\n"); 
			appendToHTML("<td >\n"); 

			// ========= for search criteria
			appendToHTML("<p style=\"line-height: 75%; margin-top: 0\">");

			printText("PCMWREPORTEDHISTORICALWOSEARCHCRITERIA: Search Criteria:");

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOCLASS: Class");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(err_class_value);

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOPERFORMEDACTION: Performed Action");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(performed_action_id_value);

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOTYPE: Type");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(err_type_value);

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOCAUSE: Cause");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(err_cause_value);

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOOBJECTID: Object Id");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(mch_code_value);

			appendToHTML("<br>");
			printText("-  ");
			printText("PCMWREPORTEDHISTORICALWOTIMEPERIOD: Time Period");
			appendToHTML("&nbsp;");
			printText("PCMWREPORTEDHISTORICALWOFROM: From");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(start_date_value);
			appendToHTML("&nbsp;");
			printText("PCMWREPORTEDHISTORICALWOTO: To");
			appendToHTML(wrapInSmallFont(":"));		//XSS_Safe AMNILK 20070727
			appendToHTML("&nbsp;");
			printText(finish_date_value);
			appendToHTML("</p>");
			//end of search criteria

			appendToHTML("</td>\n"); 
			appendToHTML("<td width=\"10\"></td>\n"); 
			appendToHTML("</tr>\n"); 
			appendToHTML("</table>\n"); 
			//============ end of main table
			appendToHTML("<!-- table end -->");
		}

		if ("T".equals(auto_display)|| bPopulate)
      //if (bPopulate)
      {
			//printTable(tbl);
			//printCustomTable();
          printTable(tbl);

			if (size < db_rows)
			{
				printNewLine();

				if (skip_rows>0)
					printSubmitLink("PCMWPORTLETSPCMWREPORTEDHISTORICALWOCUSTPRV: Previous","prevCust");
				else
					printText("PCMWPORTLETSPCMWREPORTEDHISTORICALWOCUSTPRV: Previous");

				printSpaces(5);
				String rows = translate("PCMWPORTLETSPCMWREPORTEDHISTORICALWOCUSTROWS: Rows &1 to &2 of &3",			  //bakatoday
										(skip_rows+1)+"",
										(skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
										db_rows+"");
				printText( rows );
				printSpaces(5);

				if (skip_rows<db_rows-size)
					printSubmitLink("PCMWPORTLETSPCMWREPORTEDHISTWOCUSTNEXT: Next" , "nextCust");
				else
					printText("PCMWPORTLETSPCMWREPORTEDHISTORICALWONEXT: Next");

				printNewLine();
				printNewLine();

				appendDirtyJavaScript(
									 "function prevCust(obj,id)"+
									 "{"+
									 "   getPortletField(id,'CMD').value = 'PREV';"+
									 "}\n"+
									 "function nextCust(obj,id)"+
									 "{"+
									 "   getPortletField(id,'CMD').value = 'NEXT';"+
									 "}\n");


			}
			else
				printNewLine();

			appendDirtyJavaScript(
								 "function "+addProviderPrefix()+"openInNewWindow( file_path )\n"+
								 "{ \n"+
								 "   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n"+
								 "} \n");
		}
		else
		{
			this.appendToHTML("<br>");
			printSubmitLink("WONOTTRANSSEARCH: Search","findRecord");
			appendDirtyJavaScript("function findRecord(obj,id)"+
								  "{"+
								  "   getPortletField(id,'CMD').value = 'FIND';"+
								  "}\n");
		}

     
      appendDirtyJavaScript("function OpenDesignObjecsInNewWindow()\n");
		appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("var msg =\"",translate("PCMWREPORTEDHISTORICALWODESIGNOBJECTNOTEXIST: Corresponding design object does not exist!"),"\";\n");    //XSS_Safe AMNILK 20070727
      
      if (rowset.countRows()>0)
      {
         appendDirtyJavaScript("   aMecCodeArray = new Array(",rowset.countRows()+"",");\r\n");
         for (int i=0; i<rowset.countRows(); i++)
         {
            appendDirtyJavaScript("   aMecCodeArray[",i+"","] = '",mgr.encodeStringForJavascript(rowset.getValueAt(i,"MCH_CODE")),"';\r\n");
         }
           String home_page = "http://"+mgr.getApplicationDomain() + mgr.getConfigParameter("APPLICATION/LOCATION/PORTAL")+"?_ID="+Math.round(Math.random()*1000000); 
           appendDirtyJavaScript("var appRoot= \"http://"+mgr.encodeStringForJavascript(mgr.getApplicationDomain())+mgr.encodeStringForJavascript(mgr.getApplicationPath())+"\";\n");
           appendDirtyJavaScript("  var mechCode = aMecCodeArray[tblRow1]; "+    
                            " r = __connect(appRoot+\"/pladew/PlantDesignPortletClientUtil.page?VALIDATE=MECHCODE&MECH_CODE=\"+ mechCode);\n"+
                            " var keya=__getValidateValue(0);\n"+
                            " var std_sq=__getValidateValue(1);\n" +
                            "  var plt_sq=__getValidateValue(2); \n"+
                            "  var plt_name=__getValidateValue(3); \n"+
                            "  var artstd_sq=__getValidateValue(4); \n"+
                            "  var object_sq=__getValidateValue(5); \n"+
                            "  var object_revision=__getValidateValue(6); \n"+
                            "  var default_plant=__getValidateValue(7); \n");
                           /* "  if (keya != \"\")  \n"+
                            "  { \n"+
                            "  var sWinUrl = \"pladew/PlantNavigatorFrameset.page"+
					                             "?sSTD_SQ=std_sq&sPLT_SQ=plt_sq&sPLT_NAME=plt_name&sARTSTD_SQ=artstd_sq&sObjectKey=\"+\"o%3A\"+object_sq+ \"%1F\"+object_revision+ \"%1F\" +plt_sq+ \"%1F\";\r\n");*/
	    
	    appendDirtyJavaScript("var bOpenNavigator = true;\n");
	    appendDirtyJavaScript("if (keya != \"\")  \n");
            appendDirtyJavaScript("{ \n");
	    appendDirtyJavaScript("   if (default_plant !=plt_sq)\n");
	    appendDirtyJavaScript("   {\n");
	    appendDirtyJavaScript("      if (confirm('");
	    appendDirtyJavaScript(mgr.translate("PCMWREPORTEDHISTORICALWCONFIRM: To view the requested information, your default plant will be changed to:")+" ");
	    appendDirtyJavaScript("'+plt_name))\n");
	    appendDirtyJavaScript("      {\n");
	    appendDirtyJavaScript("         try{\n");
	    appendDirtyJavaScript("            r = __connect(appRoot+\"/pladew/PlantDesignPortletClientUtil.page?CHANGE_PLT_SQ=\"+ plt_sq);\n");
            appendDirtyJavaScript("	       parent.location.reload(\""+mgr.encodeStringForJavascript(home_page)+"\"); \n");
	    appendDirtyJavaScript("	     }catch(e) { bOpenNavigator = false;  }\n");
	    appendDirtyJavaScript("      }\n");
	    appendDirtyJavaScript("      else\n");
	    appendDirtyJavaScript("         bOpenNavigator = false;\n");
	    appendDirtyJavaScript("   } \n");
	    appendDirtyJavaScript("   if(bOpenNavigator) {\n");
            appendDirtyJavaScript("      var sWinUrl = \"pladew/PlantNavigatorFrameset.page"+
		                  "?sSTD_SQ=std_sq&sPLT_SQ=plt_sq&sPLT_NAME=plt_name&sARTSTD_SQ=artstd_sq&sObjectKey=\"+\"o%3A\"+object_sq+ \"%1F\"+object_revision+ \"%1F\" +plt_sq+ \"%1F\";\r\n");
           appendDirtyJavaScript("   window.open(sWinUrl,'anotherWindow2','status,resizable,scrollbars,width=825,height=550,left=70,top=50')\n");
           appendDirtyJavaScript("   } \n");
	   appendDirtyJavaScript("} \n");
           appendDirtyJavaScript("else \n");
           appendDirtyJavaScript("{ \n");
           appendDirtyJavaScript("   alert(msg) ; \n" );
           appendDirtyJavaScript("} \n");
		
      }
      appendDirtyJavaScript("	}\n");
     

	}

	public void printCustomBody()  throws FndException 
	{
		ASPManager mgr = getASPManager();

		//=========== main table : to leave a space 
		appendToHTML("<table border=\"0\">\n"); 
		appendToHTML("<tr>\n"); 
		appendToHTML("<td width=\"10\"></td>\n"); 
		appendToHTML("<td >\n"); 
		// sub tables start
		// ========= for search criteria
		appendToHTML("<br>\n");

		appendToHTML("<table border=\"0\" > \n"); 
		appendToHTML("<tr>\n");
		appendToHTML("  <td  colspan=\"6\">\n");
		printText("PCMWREPORTEDHISTORICALWODEFINEYOURSEARCH: Use these fields to define your search.Mandatory fields are marked with an asterisk.");
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("<tr>\n");
		appendToHTML("  <td  width=\"10\" rowspan=\"7\"></td>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOCLASS: Class");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td >\n");
		printField("ERR_CLASS",err_class_value);
		printDynamicLOV("ERR_CLASS","WORK_ORDER_CLASS_CODE");
		appendToHTML(" </td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("<tr>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOPERFORMEDACTION: Performed Action");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td >\n");
		printField("PERFORMED_ACTION_ID",performed_action_id_value);
		printDynamicLOV("PERFORMED_ACTION_ID","MAINTENANCE_PERF_ACTION");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("<tr>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOTYPE: Type");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td >\n");
		printField("ERR_TYPE",err_type_value);
		printDynamicLOV("ERR_TYPE","WORK_ORDER_TYPE_CODE");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOCAUSE: Cause");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td >\n");
		printField("ERR_CAUSE",err_cause_value);
		printDynamicLOV("ERR_CAUSE","MAINTENANCE_CAUSE_CODE");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("<tr>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOOBJECTID: Object Id");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td >\n");
		printField("MCH_CODE",mch_code_value);
		printLOV("MCH_CODE","pcmw/MaintenanceObjectLov2.page");
		appendToHTML("</td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("  <td ></td>\n");
		appendToHTML("</tr>\n");
		appendToHTML("<tr>\n");
		appendToHTML("  <td >\n"); 
		printText("PCMWREPORTEDHISTORICALWOTIMEPERIOD: Time Period");
		appendToHTML("</td>\n");
		appendToHTML("  <td >\n");
		printText("PCMWREPORTEDHISTORICALWOFROM: From");
		appendToHTML("  </td>\n");
		appendToHTML("  <td >\n");
		//amnilk
		printField("START_DATE",start_date_value,20,10,"check_format");

		appendToHTML("</td>\n");
		appendToHTML("  <td >\n");
		printText("PCMWREPORTEDHISTORICALWOTO: To");
		appendToHTML("  </td>\n");
		appendToHTML("  <td >\n");
		printField("FINISH_DATE",finish_date_value,20,10,"check_format");
		appendToHTML("</td>\n");
		appendToHTML("</tr>\n");

		appendToHTML("</table>\n");

		// =========end search criteria

		appendToHTML("<br><br> \n");

		// select columns to be shown.
		appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
		appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana' colspan=\"3\" width=\"225\">");

		printText("PCMWREPORTEDHISTORICALWOSELECTFIELDSTOSHOW: Select fields to show in the table:");
		appendToHTML("</td><td valign=\"bottom\" >");


		// another sub table for ascending , descending 
		appendToHTML("<table border=\"0\" width=\"313\"> \n");
		appendToHTML("  <tr>\n");
		appendToHTML("    <td width=\"62\" rowspan=\"1\" valign=\"middle\">");
		printText("PCMWREPORTEDHISTORICALWOORDEREDBY: Order by");
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"20\" valign=\"bottom\">");
                printRadioButton("ORDER_STYLE","NONE","","NONE".equals(order_style_value));
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"161\" valign=\"middle\">");
		printText("PCMWREPORTEDHISTORICALWONONE: None");
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"20\" valign=\"bottom\">");
		printRadioButton("ORDER_STYLE","ASC","","ASC".equals(order_style_value));
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"161\" valign=\"middle\">");
		printText("PCMWREPORTEDHISTORICALWOASCEND: Ascending");
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"20\" valign=\"bottom\">");
		printRadioButton("ORDER_STYLE","DESC","","DESC".equals(order_style_value));
		appendToHTML("    </td>\n");
		appendToHTML("    <td width=\"161\" valign=\"middle\">");
		printText("PCMWREPORTEDHISTORICALWODESC: Descending");
		appendToHTML("    </td>\n");
		appendToHTML("  </tr>\n");
		appendToHTML("</table>\n");

		// end of  sub table for ascending , descending 

		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'><br>");


		printText("PCMWREPORTEDHISTORICALWOWONO: Wo No");
		appendToHTML("</td><td style='FONT: 8pt Verdana'><br>");
		printCheckBox("CHECK_SHOW_WO_NO","T".equalsIgnoreCase(show_wo_no)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","WO_NO","","WO_NO".equals(order_by_value));

		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWODIRECTIVE: Directive");
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_DIRECTIVE","T".equalsIgnoreCase(show_directive)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","ERR_DESCR","","ERR_DESCR".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOWORKDONE: Work Done");
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_WORK_DONE","T".equalsIgnoreCase(show_work_done)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","WORK_DONE","","WORK_DONE".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOOBJECTID: Object Id");
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_OBJECT_ID","T".equalsIgnoreCase(show_object_id)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","MCH_CODE","","MCH_CODE".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOCLASS: Class");// // //
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_CLASS","T".equalsIgnoreCase(show_class)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","ERR_CLASS","","ERR_CLASS".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOPERFORMEDACTION: Performed Action");// // //
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_PERFORMED_ACTION","T".equalsIgnoreCase(show_performed_action)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","PERFORMED_ACTION_ID","","PERFORMED_ACTION_ID".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOTYPE: Type");// // //
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_TYPE","T".equalsIgnoreCase(show_type)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","ERR_TYPE","","ERR_TYPE".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOCAUSE: Cause");// // //
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_CAUSE","T".equalsIgnoreCase(show_cause)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","ERR_CAUSE","","ERR_CAUSE".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOTOTALCOST: Total Cost");
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_TOTAL_COST","T".equalsIgnoreCase(show_total_cost)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","TOTAL_COST","","TOTAL_COST".equals(order_by_value));
		appendToHTML("</td></tr><tr><td  width=\"10\" ></td><td style='FONT: 8pt Verdana'>");

		printText("PCMWREPORTEDHISTORICALWOCOMPLETIONDATE: Completion Date");
		appendToHTML("</td><td style='FONT: 8pt Verdana'>");
		printCheckBox("CHECK_SHOW_COMPLETION_DATE","T".equalsIgnoreCase(show_completion_date)?true:false);
		appendToHTML("</td><td valign=\"bottom\">");
		printRadioButton("ORDER_BY","REAL_F_DATE","","REAL_F_DATE".equals(order_by_value));
		appendToHTML("</td></tr></tbody></table>");

		appendToHTML("<br><br> \n");

		//table for title , max no and search automatically
		appendToHTML("<table border=\"0\" >\n");
		appendToHTML("  <tr>\n");
		appendToHTML("    <td   valign=\"top\">\n");
		printText("PCMWREPORTEDHISTORICALWOENTPORTLETDESC: Enter Portlet Description");
		appendToHTML("</td><td>");//&nbsp;
		//printField("PORTLET_DESC",portlet_desc,50);
		printTextArea("PORTLET_DESC",portlet_desc,4,50);
		appendToHTML("</td>\n");
		appendToHTML("  </tr>\n");
		appendToHTML("</table>\n");//end of the table for portlet description

		//table max no and search automatically
		appendToHTML("<table border=\"0\" >\n");
		//=======
		appendToHTML("  <tr>\n");
		appendToHTML("    <td width=\"10\" ></td>\n");
		appendToHTML("    <td  >\n");
		printCheckBox("CHECK_SHOW_CRITERIA","T".equalsIgnoreCase(check_show_criteria_value)?true:false);
		appendToHTML("</td>\n");
		appendToHTML("    <td  >\n");
		printText("PCMWREPORTEDHISTORICALWOSHOWCRI: Show Criteria");
		appendToHTML("</td>\n");
		appendToHTML("  </tr>\n");
		//=======
		appendToHTML("  <tr>\n");
		appendToHTML("    <td width=\"10\" ></td>\n");
		appendToHTML("    <td  >\n");
		printCheckBox("CHECK_DISPLAY_AUTO","T".equalsIgnoreCase(auto_display)?true:false);
		appendToHTML("</td>\n");
		appendToHTML("    <td  >\n");
		printText("PCMWREPORTEDHISTORICALWOSEARCHAUTOMAT: Search automatically");

		appendToHTML("</td>\n");
		appendToHTML("  </tr>\n");
		appendToHTML("  <tr>\n");
		appendToHTML("    <td width=\"10\" ></td>\n");
		appendToHTML("    <td  >\n");
		printField("MAX_NO",size+"",5);
		appendToHTML("</td>\n");
		appendToHTML("    <td  >\n");
		printText("PCMWREPORTEDHISTORICALWOMAXNODISPLAYED: Max no of hits to be displayed");

		appendToHTML("</td>\n");
		appendToHTML("  </tr>\n");
		appendToHTML("</table>\n"); 
		//end of table max no and search automatically


		// sub tables end
		appendToHTML("</td>\n"); 
		appendToHTML("<td width=\"10\"></td>\n"); 
		appendToHTML("</tr>\n"); 
		appendToHTML("</table>\n"); 
		//============ end of main table

		appendDirtyJavaScript("function check_format(obj,id)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   var field_name = obj.name; \n");
		appendDirtyJavaScript("   var field_value; \n");
		appendDirtyJavaScript("   var error_msg; \n");
		appendDirtyJavaScript("   var bWent_wrong = false; \n");
		appendDirtyJavaScript("   if(obj.value==\"\"){\n");
		appendDirtyJavaScript("      return;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   //alert(getPortletField(id,'START_DATE').value); \n");
		//appendDirtyJavaScript("   alert(field_name); \n");
		appendDirtyJavaScript("   if(field_name.indexOf(\"START_DATE\")>0) \n");
		appendDirtyJavaScript("   { \n");
		appendDirtyJavaScript("      field_value=getPortletField(id,'START_DATE').value; \n");
		appendDirtyJavaScript("      error_msg=\""+translate("PCMWREPORTEDHISTORICALWOWRONGFORMATSTARTD: Wrong Format for From(Time Period)")+"\"; \n");  //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("   } \n");
		appendDirtyJavaScript("   else if(field_name.indexOf(\"FINISH_DATE\")>0) \n");
		appendDirtyJavaScript("   { \n");
		appendDirtyJavaScript("      error_msg=\""+translate("PCMWREPORTEDHISTORICALWOWRONGFORMATFINISHD: Wrong Format for To(Time Period)")+"\"; \n");	  //XSS_Safe AMNILK 20070727		
		appendDirtyJavaScript("      field_value=getPortletField(id,'FINISH_DATE').value; \n");
		appendDirtyJavaScript("   } \n");
		appendDirtyJavaScript("   var temp_array = field_value.split(\"-\"); \n");
		appendDirtyJavaScript("   if (temp_array.length != 3) \n");
		appendDirtyJavaScript("   { \n");
		appendDirtyJavaScript("      alert(error_msg+\"\\n   "+translate("PCMWREPORTEDHISTORICALWOMUSTBEYYYYMMDD: it must be in yyyy-mm-dd")+"!!\"); \n"); //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("      bWent_wrong = true; \n");
		appendDirtyJavaScript("   } \n");
		appendDirtyJavaScript("   else \n");
		appendDirtyJavaScript("   { \n");
		appendDirtyJavaScript("      var year  = Number(temp_array[0]); \n");
		appendDirtyJavaScript("      var month = Number(temp_array[1]); \n");
		appendDirtyJavaScript("      var date  = Number(temp_array[2]); \n");
		appendDirtyJavaScript("      if (temp_array[0].length != 4 ||temp_array[1].length>2 ||temp_array[2].length>2) \n");
		appendDirtyJavaScript("      { \n");
		appendDirtyJavaScript("         alert(error_msg+\"\\n   "+translate("PCMWREPORTEDHISTORICALWOMUSTBEYYYYMMDD: it must be in yyyy-mm-dd")+"!!\"); \n");   //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         bWent_wrong = true; \n");
		appendDirtyJavaScript("      } \n");
		appendDirtyJavaScript("      else \n");
		appendDirtyJavaScript("      { \n");
		appendDirtyJavaScript("         var temp_err=\"\"; \n");
		appendDirtyJavaScript("         if (isNaN(year)) { \n");
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWOYEARMUSTBNUMERIC: year must be numeric")+"\"; \n");	        //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if (isNaN(month)) { \n");
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWOMONTHMUSTBNUMERIC: month must be numeric")+"\"; \n");	//XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if (isNaN(date)) { \n");	
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWODATEMUSTBNUMERIC: date must be numeric")+"\"; \n");		//XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if (year<0) { \n");
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWOYEARCANNOTBNEGETIVE: year cannot be negetive")+"\"; \n");	//XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if (month<1 || month>12) { \n");
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWOMONTHBETWEEN1N12: month must be in between 1 and 12")+"\"; \n"); //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if (date<1 || date >31) { \n");
		appendDirtyJavaScript("            temp_err += \"\\n   "+translate("PCMWREPORTEDHISTORICALWODATEBETWEEN1N31: date must be in between 1 and 31")+"\"; \n");   //XSS_Safe AMNILK 20070727
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("         if(temp_err!=\"\") \n");
		appendDirtyJavaScript("         { \n");
		appendDirtyJavaScript("            alert(error_msg+temp_err);  \n");
		appendDirtyJavaScript("            bWent_wrong = true; \n");
		appendDirtyJavaScript("         } \n");
		appendDirtyJavaScript("      } \n");//else end
		appendDirtyJavaScript("   } \n");
		appendDirtyJavaScript("   if(bWent_wrong) \n");
		appendDirtyJavaScript("   { \n");
		appendDirtyJavaScript("      obj.value=\"\"; \n");
		appendDirtyJavaScript("   } \n");
		appendDirtyJavaScript("}\n");
	}

	public void submitCustomization()
	{
		ASPManager mgr = getASPManager();

		show_wo_no            = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_WO_NO"))?"T":"F";
		show_directive        = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_DIRECTIVE"))?"T":"F";
		show_work_done        = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_WORK_DONE"))?"T":"F";
		show_object_id        = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_OBJECT_ID"))?"T":"F";
		show_class            = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_CLASS"))?"T":"F";
		show_performed_action = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_PERFORMED_ACTION"))?"T":"F";
		show_type             = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_TYPE"))?"T":"F";
		show_cause            = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_CAUSE"))?"T":"F";
		show_total_cost       = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_TOTAL_COST"))?"T":"F";
		show_completion_date  = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_COMPLETION_DATE"))?"T":"F";

		auto_display          = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_DISPLAY_AUTO"))?"T":"F";

		err_class_value           = readAbsoluteValue("ERR_CLASS");
		performed_action_id_value = readAbsoluteValue("PERFORMED_ACTION_ID");
		err_type_value            = readAbsoluteValue("ERR_TYPE");
		err_cause_value           = readAbsoluteValue("ERR_CAUSE");
		mch_code_value            = readAbsoluteValue("MCH_CODE");
		start_date_value          = readAbsoluteValue("START_DATE");
		finish_date_value         = readAbsoluteValue("FINISH_DATE");

		portlet_desc              = readAbsoluteValue("PORTLET_DESC");
		size                      = (new Integer(readAbsoluteValue("MAX_NO"))).intValue();
		order_by_value            = readAbsoluteValue("ORDER_BY");
		order_style_value         = readAbsoluteValue("ORDER_STYLE");

		check_show_criteria_value = "TRUE".equalsIgnoreCase(readAbsoluteValue("CHECK_SHOW_CRITERIA"))?"T":"F";

//		writeProfileValue("SHOW_WO_NO",            readAbsoluteValue(show_wo_no) );
//		writeProfileValue("SHOW_DIRECTIVE",        readAbsoluteValue(show_directive) );
//		writeProfileValue("SHOW_WORK_DONE",        readAbsoluteValue(show_work_done) );
//		writeProfileValue("SHOW_OBJECT_ID",        readAbsoluteValue(show_object_id) );
//		writeProfileValue("SHOW_CLASS",            readAbsoluteValue(show_class) );
//		writeProfileValue("SHOW_PERFORMED_ACTION", readAbsoluteValue(show_performed_action) );
//		writeProfileValue("SHOW_TYPE",             readAbsoluteValue(show_type) );
//		writeProfileValue("SHOW_CAUSE",            readAbsoluteValue(show_cause) );
//		writeProfileValue("SHOW_TOTAL_COST",       readAbsoluteValue(show_total_cost) );
//		writeProfileValue("SHOW_COMPLETION_DATE",  readAbsoluteValue(show_completion_date) );
//
//		writeProfileValue("ERR_CLASS",           readAbsoluteValue(err_class_value) );
//		writeProfileValue("PERFORMED_ACTION_ID", readAbsoluteValue(performed_action_id_value) );
//		writeProfileValue("ERR_TYPE",            readAbsoluteValue(err_type_value) );
//		writeProfileValue("ERR_CAUSE",           readAbsoluteValue(err_cause_value) );
//		writeProfileValue("MCH_CODE",            readAbsoluteValue(mch_code_value) );
//		writeProfileValue("START_DATE",          readAbsoluteValue(start_date_value) );
//		writeProfileValue("FINISH_DATE",         readAbsoluteValue(finish_date_value) );
//
//		writeProfileValue("WHERE_CONDITION",     readAbsoluteValue(where_condition) );
//
//		writeProfileValue("CHECK_DISPLAY_AUTO",  readAbsoluteValue(auto_display) );
//		writeProfileValue("PORTLET_DESC",        readAbsoluteValue(portlet_desc) );
//		writeProfileValue("MAX_NO",              size+"" );
//		// must show the record set from the begining
//		skip_rows = 0;
//		writeProfileValue("SKIP_ROWS",           "0" ); 
//		writeProfileValue("ORDER_BY_VALUE",      readAbsoluteValue(order_by_value) );
//		writeProfileValue("ORDER_STYLE_VALUE",   readAbsoluteValue(order_style_value) );
//		writeProfileValue("CHECK_SHOW_CRITERIA_VALUE",   readAbsoluteValue(check_show_criteria_value) );
//      
//      writeProfileValue("SKIP_ROWS",           "0" ); 

      writeProfileValue("SHOW_WO_NO",             show_wo_no);
      writeProfileValue("SHOW_DIRECTIVE",         show_directive);
      writeProfileValue("SHOW_WORK_DONE",         show_work_done);
      writeProfileValue("SHOW_OBJECT_ID",         show_object_id);
      writeProfileValue("SHOW_CLASS",             show_class);
      writeProfileValue("SHOW_PERFORMED_ACTION",  show_performed_action);
      writeProfileValue("SHOW_TYPE",              show_type);
      writeProfileValue("SHOW_CAUSE",             show_cause);
      writeProfileValue("SHOW_TOTAL_COST",        show_total_cost);
      writeProfileValue("SHOW_COMPLETION_DATE",   show_completion_date);
      
      writeProfileValue("ERR_CLASS",            err_class_value);
      writeProfileValue("PERFORMED_ACTION_ID",  performed_action_id_value);
      writeProfileValue("ERR_TYPE",             err_type_value);
      writeProfileValue("ERR_CAUSE",            err_cause_value);
      writeProfileValue("MCH_CODE",             mch_code_value);
      writeProfileValue("START_DATE",           start_date_value);
      writeProfileValue("FINISH_DATE",          finish_date_value);
      
      writeProfileValue("WHERE_CONDITION",      where_condition);
      
      writeProfileValue("CHECK_DISPLAY_AUTO",   auto_display);
      writeProfileValue("PORTLET_DESC",         portlet_desc);
      writeProfileValue("MAX_NO",              size+"" );
      // must show the record set from the begining
      skip_rows = 0;
      writeProfileValue("SKIP_ROWS",           "0" ); 
      writeProfileValue("ORDER_BY_VALUE",       order_by_value);
      writeProfileValue("ORDER_STYLE_VALUE",    order_style_value);
      writeProfileValue("CHECK_SHOW_CRITERIA_VALUE",    check_show_criteria_value);
      
      writeProfileValue("SKIP_ROWS",           "0" ); 
	}

	public static String getDescription()
	{
		return "PCMWREPORTEDHISTORICALWOMAINTHISWORKORD: Maintenance - Historical Work Orders";
	}

	public boolean canCustomize()
	{
		return true;
	} 
}

