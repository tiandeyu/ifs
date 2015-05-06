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
*  File        : CalendarPlanOvw.java 
*  Modified    :
*  SHFELK  010214  Created.
*  SHFELK  010228  add RMB Generate Work Order.
*  INROLK  011008  Added Security Check to RMBs.
*  YAWILK  021203  Modified MCH_CODE max length to 100
*  BUNILK  030121  Codes reviewed.
*  SAPRLK  031218  Web Alignment - removed methods clone() and doReset().
*  SHAFLK  040407  Bug 43959, Modified method generateWorkOrder() and preDefine().
*  THWILK  040628  Merged Bug 43959.
*  ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  JAPALK  050711  Cal ID 125493. Added RMB valid conditions.
*  NIJALK  050805  Bug 126199: Disabled "Genarate Work Order" function for Preliminary and Obsolete PMs.
*                  Modified okFind().
*  NIJALK  060110  Changed DATE format to compatible with data formats in PG19.
*  ChAmlk  061130  Added Total man hrs, Planned Personnal, Material, Tools and Facilties and External cost in preDefine.
*  ILSOLK  070709  Eliminated XSS.
*  BUNILK  070524  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  AMNILK  070713  Eliminated SQL Injection.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  CHANLK  071109  Bug 68687, Added new field Route ID
*  080205  CHANLK	 Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080502  Bug 73429, Added new field Work Description 
*  SAFALK  090911  Bug 85288, Modified okFind() and preDefine() - Added new fields.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CalendarPlanOvw extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CalendarPlanOvw");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPBlock b;
	private ASPBlock headblk1;
	private ASPField f;
	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPQuery q;
	private ASPBuffer keys;
	private boolean separatepmAction_;
	private boolean roundpmAction_;
	private boolean again;

	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;

	//===============================================================
	// Construction 
	//===============================================================
	public CalendarPlanOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		ctx = mgr.getASPContext();

		trans = mgr.newASPTransactionBuffer();
		again = ctx.readFlag("AGAIN",again);
		separatepmAction_= ctx.readFlag("SEPARATEPMACTION",false);
		roundpmAction_= ctx.readFlag("ROUNTPMACTION",false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		if (!again)
		{
			checkObjAvaileble();
			again = true;
		}

		adjust();
		ctx.writeFlag("AGAIN",again);
		ctx.writeFlag("SEPARATEPMACTION",separatepmAction_);
		ctx.writeFlag("ROUNTPMACTION",roundpmAction_);

	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		String val = null;
		String mch = null;
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("CONTRACT".equals(val))
		{
			cmd=trans.addCustomFunction("MNAME","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
			cmd.addParameter("MCH_CODE");
			cmd.addParameter("CONTRACT");

			trans=mgr.validate(trans);
			mch=trans.getValue("MNAME/DATA/MCHNAME");
			mgr.responseWrite(mch);
		}
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
      //Bug 85822, Start 
      //q.addWhereCondition("WO_NO is null");
      //Bug 85822, End
		q.includeMeta("ALL");
                mgr.querySubmit(trans,headblk);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWCALENDARPLANOVWNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


	private String createTransferUrl(String url, ASPBuffer object)
	{
		ASPManager mgr = getASPManager();

		try
		{
			String pkg = mgr.pack(object,1900 - url.length());
			char sep = url.indexOf('?')>0 ? '&' : '?';
			urlString = url + sep + "__TRANSFER=" + pkg ;
			return urlString;
		}
		catch (Throwable any)
		{
			return null;
		}
	}

	public void  separatepmAction()
	{
		ASPManager mgr = getASPManager();

		/*if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
		} */

		if (headlay.isMultirowLayout())
			headset.store();
		else
		{
			headset.unselectRows();
			headset.selectRow();
		}

		//keys=headset.getSelectedRows("PM_NO");
		//mgr.transferDataTo("PmAction.page",keys);


		bOpenNewWindow = true;
		urlString = createTransferUrl("PmAction.page", headset.getSelectedRows("PM_NO,PM_REVISION"));
		newWinHandle = "PmAction"; 
	}


	public void  roundpmAction()
	{
		ASPManager mgr = getASPManager();

		/*if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
		}
		keys=headset.getSelectedRows("PM_NO");
		mgr.transferDataTo("PmActionRound.page",keys);*/


		if (headlay.isMultirowLayout())
			headset.store();
		else
		{
			headset.unselectRows();
			headset.selectRow();
		}


		bOpenNewWindow = true;
		urlString = createTransferUrl("PmActionRound.page", headset.getSelectedRows("PM_NO,PM_REVISION"));
		newWinHandle = "PmActionRound"; 
	}


	public void  none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("PCMWCALENDARPLANOVWNOSELECT: No RMB method has been selected."));
	}


	public boolean  isGeneratable()
	{
		ASPManager mgr = getASPManager();
		String strGenerate = null;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		trans.clear();  
		cmd = trans.addCustomFunction("GENE", "Pm_Action_API.Is_Generateable__", "GENERATE" );
		cmd.addParameter("PM_NO",headset.getValue("PM_NO"));
		cmd.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
		trans = mgr.perform(trans);

		strGenerate= trans.getValue("GENE/DATA/GENERATE");

		if ("TRUE".equals(strGenerate))
			return(true);
		else
			return(false);
	}

	public boolean  isConnected()
	{
		ASPManager mgr = getASPManager();
		String strConected = null;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		trans.clear();
		cmd = trans.addCustomFunction("CONT", "Pm_Action_Connection_API.Is_Connected", "CONECTED" );
		cmd.addParameter("PM_NO",headset.getValue("PM_NO"));
		cmd.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
		trans = mgr.perform(trans);

		strConected= trans.getValue("CONT/DATA/CONECTED");


		if ("TRUE".equals(strConected))
			return(true);
		else
			return(false);
	}


	public void  generateWorkOrder()
	{
		ASPManager mgr = getASPManager();
		ASPQuery hgenqry = null;
		ASPQuery hsyspsd = null;
		String hpmno = null;
		String hpmrev = null;
		double plan_hour = 0.0; 
		ASPQuery syspfd = null;
		String gentdWoNo = null;
		ASPTransactionBuffer secBuff = null;
		String secAvailable = "false";
		String tupeOk = "false";

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		cmd = trans.addCustomFunction("PMT","PM_TYPE_API.Decode","PMTYPE");
		cmd.addParameter("DB_VALUE","ActiveSeparate");
		trans = mgr.perform(trans);
		String sSep=trans.getValue("PMT/DATA/PMTYPE");
		String sPmType = headset.getRow().getValue("PM_TYPE");
		String swono = headset.getRow().getValue("WO_NO");
		String seq = headset.getRow().getValue("SEQ_NO");
		if (sPmType.equals(sSep))
			tupeOk = "true";

		trans.clear();

		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Pm_Action_API","Generate__");

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists("Pm_Action_API.Generate__"))
			secAvailable = "true";

		if ("true".equals(tupeOk) &&  "true".equals(secAvailable) && (isGeneratable()) && (!isConnected()) && mgr.isEmpty(swono))
		{
			trans.clear();

			hpmno = headset.getRow().getValue("PM_NO"); 
			hpmrev = headset.getRow().getValue("PM_REVISION");

			String plan_date = headset.getRow().getValue("PLANNED_DATE");

			// SQLInjection_Safe AMNILK 20070713

			q = trans.addQuery("PLANHOUR","PM_ACTION","PLAN_HRS","PM_NO = ? ","");
			q.addParameter("PM_NO",hpmno);
			
			trans = mgr.perform(trans);
			plan_hour = trans.getNumberValue("PLANHOUR/DATA/PLAN_HRS");

			if (isNaN(plan_hour))
				plan_hour = 0;

			trans.clear();

			// SQLInjection_Safe AMNILK 20070712

			q = trans.addQuery("HPSD","select to_date(?,'YYYY-MM-DD-hh24.MI.ss') PSDATE from DUAL");
			q.addParameter("SNULL",plan_date);

			if (plan_hour!=0.0){
			    q = trans.addQuery("HPFD","select to_date(?,'YYYY-MM-DD-hh24.MI.ss') + ?/24 PFDATE from DUAL");
			    q.addParameter("SNULL",plan_date);
			    q.addParameter("HOUR",new Double(plan_hour).toString());
			}
			else{
			    q = trans.addQuery("HPFD","select to_date(?,'YYYY-MM-DD-hh24.MI.ss') + "+168/24+" PFDATE from DUAL");
			    q.addParameter("SNULL",plan_date);
			}

			trans = mgr.perform(trans);

			String planFDate = trans.getBuffer("HPFD/DATA").getFieldValue("PFDATE");
                        plan_date =  trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");

			trans.clear();

			cmd = trans.addCustomFunction("HGNT","Generation_Type_API.Decode","GNTYPE");
			cmd.addParameter("DB_VALUE","4");

			cmd = trans.addCustomCommand("HGNID", "Pm_Generation_API.Create_Gen");
			cmd.addParameter("GEN_ID1","");
			cmd.addReference("GNTYPE","HGNT/DATA");
			cmd.addParameter("SNULL","");

			cmd = trans.addCustomCommand("GENE3", "Pm_Action_API.Generate__");
			cmd.addParameter("WO_NO1","");
			cmd.addParameter("SNULL","");
			cmd.addParameter("PM_NO",hpmno);
			cmd.addParameter("PM_REVISION",hpmrev);
			cmd.addParameter("PSDATE",plan_date); 
			cmd.addParameter("PFDATE",planFDate); 
			cmd.addParameter("HSEQ_NO",seq); 
			cmd.addReference("GEN_ID1","HGNID/DATA");

			trans=mgr.perform(trans);

			gentdWoNo = trans.getValue("GENE3/DATA/WO_NO1");

			mgr.showAlert(mgr.translate("PCMWPMACTIONWRKORDR: Work Order No. ")+gentdWoNo+mgr.translate("PCMWPMACTIONSUCCESSGENTD:  successfully generated."));        
		}
		else
			mgr.showAlert(mgr.translate("PCMWPMACTIONNOTGENERATABLE: This is not a Generatable Record."));
		trans.clear();
		okFind();
	}


//-------------------------------------------------------------------------------
//-------------------------PREDEFINE FUNCTION------------------------------------
//-------------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();
		
                headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("PM_NO","Number", "#").
		setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPMNO: PM No");

		headblk.addField("PM_REVISION").
		setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPMREV: PM Revision").
		setMaxLength(6);

		headblk.addField("PM_TYPE").
		setSize(18).
		setLabel("PCMWCALENDARPLANOVWPMTY: Type").
		setReadOnly().
		setMaxLength(30);

		//Start, Bug 68687
		headblk.addField("ROUNDDEF_ID").
		setSize(10).
		setDynamicLOV("PM_ROUND_DEFINITION",600,450).
		setUpperCase().
		setLabel("PCMWCALENDARPLANOVWROUTEID: Route ID").
		setReadOnly().
		setMaxLength(10);
		//End, Bug 68687
		
		headblk.addField("PLANNED_DATE","Date").
		setSize(18).
		setLabel("PCMWCALENDARPLANOVWGENDA: Planned Date").
		setReadOnly();

		headblk.addField("PLANNED_WEEK").
		setSize(8).
		setLabel("PCMWCALENDARPLANOVWWE: Week").
		setReadOnly().
		setMaxLength(4);

      //Bug 85822, Start
      headblk.addField("WO_NO").
      setSize(12).
      setLabel("PCMWCALENDARPLANOVWWONO: WO No").
      setFunction("PM_Action_Calendar_Plan_API.Get_Wo_No(:PM_NO,:PM_REVISION,:SEQ_NO)").
      setMaxLength(10).
      setReadOnly();

      headblk.addField("ORG_CONTRACT").
      setSize(12).
      setLabel("PCMWCALENDARPLANOVWWOSITE: WO Site").
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setFunction("PM_Action_API.Get_Org_Contract(:PM_NO,:PM_REVISION)").
      setMaxLength(5).
      setReadOnly();

      headblk.addField("PM_STATUS").
      setSize(12).
      setLabel("PCMWCALENDARPLANOVWPMSTATUS: Status").
      setFunction("Pm_Action_API.Get_Status(:PM_NO,:PM_REVISION)").
      setMaxLength(15).
      setReadOnly();
      //Bug 85822, End
      
		headblk.addField("OVERDUE_PERCENT","Number").
		setSize(20).
		setLabel("PCMWCALENDARPLANOVWOVRDUEPCNT: Overdue Percent (%)").
		setReadOnly().
		setMaxLength(4).
		setFunction("PM_Action_API.Overdue_Percent(:PM_NO,:PM_REVISION)");

		headblk.addField("ORG_CODE").
		setSize(18).
		setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
		setLabel("PCMWCALENDARPLANOVWORGCO: Maintenance Organization").    
		setUpperCase().
		setReadOnly().
		setMaxLength(8);

		headblk.addField("ACTION_CODE_ID").
		setSize(12).
		setDynamicLOV("MAINTENANCE_ACTION",600,450).
		setLabel("PCMWCALENDARPLANOVWACID: Action").
		setUpperCase().
		setReadOnly().
		setMaxLength(10);

		headblk.addField("ACTIONDESCR").
		setSize(22).
		setLabel("PCMWCALENDARPLANOVWACDESCR: Action Description"). 
		setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)").
		setReadOnly().
		setMaxLength(2000);
		mgr.getASPField("ACTION_CODE_ID").setValidation("ACTIONDESCR");

      //Bug 85822, Start - Changed label: "Site" to "Object Site"
		headblk.addField("CONTRACT").
		setSize(12).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
		setLabel("PCMWCALENDARPLANOVWCON: Object Site").
		setUpperCase(). 
		setReadOnly().
		setMaxLength(5);
      //Bug 85822, End

		headblk.addField("MCH_CODE").
		setSize(18).
		setDynamicLOV("MAINTENANCE_OBJECT",600,450).
		setLabel("PCMWCALENDARPLANOVWMCHCO: Object ID").
		setUpperCase().
		setReadOnly().
		setMaxLength(100). 
		setHyperlink("../pcmw/CalendarPlanOvwRedirect.page","MCH_CODE,CONTRACT","NEWWIN");

		headblk.addField("MCHNAME").
		setSize(30).
		setLabel("PCMWCALENDARPLANOVWMCHNAME: Object Description").
		setFunction("Maintenance_Object_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
		setReadOnly().
		setMaxLength(2000);


                headblk.addField("WORKDESC").
                setLabel("PCMWCALENDARPLANOVWWORKDESC: Work Description").
                setSize(30).
                setFunction("Pm_Action_API.Get_Description(:PM_NO,:PM_REVISION)").
                setReadOnly().
                setMaxLength(2000);

		headblk.addField("MCH_TYPE").
		setSize(12).
		setDynamicLOV("EQUIPMENT_OBJ_TYPE",600,450).
		setLabel("PCMWCALENDARPLANOVWMCHTYPE: Object Type").
		setUpperCase(). 
		setReadOnly().
		setMaxLength(5);

		headblk.addField("GROUP_ID").
		setSize(18).
		setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,450).
		setLabel("PCMWCALENDARPLANOVWGRID: Object Group").
		setUpperCase().
		setReadOnly().
		setMaxLength(10);

		headblk.addField("CATEGORY_ID").
		setSize(18).
		setDynamicLOV("EQUIPMENT_OBJ_CATEGORY",600,450).
		setLabel("PCMWCALENDARPLANOVWCAID: Object Category").
		setUpperCase().
		setReadOnly().
		setMaxLength(10);

		headblk.addField("TYPE").
		setSize(20).
		setDynamicLOV("TYPE_DESIGNATION",600,450).
		setLabel("PCMWCALENDARPLANOVWTYPE: Type Designation").
		setUpperCase().
		setReadOnly().
		setMaxLength(30);

		headblk.addField("ROLE_CODE").
		setSize(12).
		setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,450).
		setLabel("PCMWCALENDARPLANOVWRODE: Craft").
		setUpperCase().
		setReadOnly().
		setMaxLength(10);

		headblk.addField("TOTAL_MAN_HOURS","Number").
                setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWTOTMANHRS: Total Man Hours").
                setMaxLength(10);

                headblk.addField("PLANNED_PERSONNEL_COST","Money").
                setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPLANOVWPERSCOST: Planned Personnel Cost").
                setMaxLength(10);

                headblk.addField("PLANNED_MATERIAL_COST","Money").
                setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPLANOVWPLANMATECOST: Planned Material Cost").
                setMaxLength(10);

                headblk.addField("PLANNED_TOOL_COST","Money").
                setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPLANOVWPLANTOOLS: Planned Tools/Facilities Cost").
                setMaxLength(10);

                headblk.addField("PLANNED_EXTERNAL_COST","Money").
                setSize(12).
		setReadOnly().
		setLabel("PCMWCALENDARPLANOVWPLANOVWPLANEXCOST: Planned External Cost").
                setMaxLength(10);

                headblk.addField("VENDOR_NO").
		setSize(12).
		setDynamicLOV("SUPPLIER_INFO",600,450).
		setLabel("PCMWCALENDARPLANOVWVENO: Contractor").
		setUpperCase().
		setReadOnly().
		setMaxLength(20);

		headblk.addField("VENDORNAME").
		setSize(20).
		setLabel("PCMWCALENDARPLANOVWVENAME: Contractor Name").
		setFunction("Maintenance_Supplier_API.Get_Description(:VENDOR_NO)").
		setReadOnly().
		setMaxLength(2000);
		mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
      
      //Bug 85822, Start
      headblk.addField("COMPLETION_DATE", "Date").
      setSize(18).
      setLabel("PCMWCALENDARPLANOVWCOMPDATE: Actual Completion").
      setReadOnly();

      headblk.addField("GENERATION_DATE", "Date").
      setSize(18).
      setLabel("PCMWCALENDARPLANOVWGENDATE: Generation Date").
      setReadOnly();

      headblk.addField("REPLACED_BY_PM_NO").
      setSize(12).
      setLabel("PCMWCALENDARPLANOVWREPPMNO: Replaced By PM No").
      setReadOnly();

      headblk.addField("REPLACED_BY_PM_REVISION").
      setSize(12).
      setLabel("PCMWCALENDARPLANOVWREPPMREV: Revision").
      setReadOnly();
      //Bug 85822, End

		headblk.addField("COMPANY").
		setSize(12).
		setHidden().
		setLabel("PCMWCALENDARPLANOVWCOM: Company").
		setUpperCase().
		setReadOnly().
		setMaxLength(20);

		headblk.addField("SEQ_NO","Number").
		setSize(12).
		setHidden();

                headblk.addField("PM_TYPE_DB").
                setFunction("Pm_Type_API.Encode(:PM_TYPE)").
                setHidden();
		
                headblk.addField("PM_STATE").
                setFunction("Pm_Action_API.Get_Pm_State(:PM_NO,:PM_REVISION)").
                setHidden();

		headblk.setView("PM_CALENDAR_PLAN");
		headblk.defineCommand("","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("none",   "");
		headbar.addCustomCommand("separatepmAction",  mgr.translate("PCMWCALENDARPLANOVWSPA: Separate Pm Actions..."));
		headbar.addCustomCommand("roundpmAction",  mgr.translate("PCMWCALENDARPLANOVWRPA: Round Pm Actions..."));
		headbar.addCustomCommand("generateWorkOrder",mgr.translate("PCMWCALENDARPLANOVWCDN: Generate Work Order"));
                headbar.addCommandValidConditions("generateWorkOrder","PM_TYPE_DB","Disable","ActiveRound");
                headbar.appendCommandValidConditions("generateWorkOrder","PM_STATE","Disable","Preliminary;Obsolete");
                headbar.addCommandValidConditions("separatepmAction","PM_TYPE_DB","Disable","ActiveRound");
                headbar.addCommandValidConditions("roundpmAction","PM_TYPE_DB","Disable","ActiveSeparate");

		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.EDITROW);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWCALENDARPLANOVWHEADTITLE: Calendar Plan"));    
		headtbl.setWrap();

		headtbl.enableRowSelect();

		headbar.enableMultirowAction();
		headbar.removeFromMultirowAction("generateWorkOrder");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  

		b = mgr.newASPBlock("PM_TYPE");

		b.addField("CLIENT_VALUES0");

		headblk1 = mgr.newASPBlock("HEAD1");
		headblk1.addField("WO_NO1");
		headblk1.addField("DNULL");
		headblk1.addField("SNULL");
		headblk1.addField("CONECTED");
		headblk1.addField("GENERATE");
		headblk1.addField("GEN_ID1");
		headblk1.addField("GNTYPE");
		headblk1.addField("PMTYPE");
		headblk1.addField("PSDATE","Datetime");
		headblk1.addField("PFDATE","Datetime");
		headblk1.addField("CONECTED2");
		headblk1.addField("GENERATE2");
		headblk1.addField("HSEQ_NO","Number");   
		headblk1.addField("HOUR");
		headblk1.addField("DB_VALUE");
		headblk1.addField("PLAN_HRS","Number"); 

	}

	public void checkObjAvaileble()
	{
		ASPManager mgr = getASPManager();

		ASPBuffer availObj;
		trans.clear();
		trans.addSecurityQuery("PM_ACTION");
		trans.addPresentationObjectQuery("PCMW/PmAction.page,PCMW/PmActionRound.page");

		trans = mgr.perform(trans);

		availObj = trans.getSecurityInfo();

		if (availObj.itemExists("PM_ACTION") && availObj.namedItemExists("PCMW/PmAction.page"))
			separatepmAction_ = true;

		if (availObj.itemExists("PM_ACTION")  && availObj.namedItemExists("PCMW/PmActionRound.page"))

			roundpmAction_ = true;
	}

	public void  DissableRmbs()
	{
		ASPManager mgr = getASPManager();

		if (!separatepmAction_)
			headbar.removeCustomCommand("separatepmAction");
		if (!roundpmAction_)
			headbar.removeCustomCommand("roundpmAction");


	}
	public void  adjust()
	{
		DissableRmbs();
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWCALENDARPLANOVWTITLE: Calendar Plan";
	}

	protected String getTitle()
	{
		return "PCMWCALENDARPLANOVWTITLE: Calendar Plan";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		appendToHTML(headlay.show());
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070709
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
		}
	}

}
