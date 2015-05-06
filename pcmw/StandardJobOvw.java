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
*  File        : StandardJobOvw.java 
*  Modified    :
*  SHFELK  010219  Created.
*  JEWILK  010403  Changed file extensions from .asp to .page
*  INROLK  010927  Added Security Check to RMBs.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040211  Web Alignment - Arranged field order as in Centura and removed unnecessary code.
*  ARWILK  040806  Added Custom validations to field CONTRACT.
*  NIJALK  041110  Added new methods showObsoleteWORev(), showObsoletePM(), updatePM(). Modified preDefine(),
*                  DissableRmbs(),printContents(),run().
*  Chanlk  041119  Added new fields to the overview.
*  NIJALK  050210  Removed function updatePMActions()
*  AMNILK  051006  Call Id:127600. Modified method preDefine and validate.Add custom validations to ORG_CODE and ORG_CONTRACT.
*  THWILK  051101  Call ID 128381, Removed Mandatory property for WORK_DESCRIPTION.Also removed saveReturn method as it was unnecessary.
*                  Also fixed an additional error "literal does not match format string" 
*  THWILK  051103  Call Id:128445, Modified propereties of Site in Predefine().
*  THWILK  060126  Corrected localization errors.
*  AMNILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  SHAFLK  091215  Bug 87293, Modified okFind().   
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class StandardJobOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.StandardJobOvw");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private boolean genchgpmact;
	private boolean updchgpmact;
	private boolean satis;
	private int currrow;
	private ASPCommand cmd;
	private ASPQuery q;
	private String fldTitleCreatedBy;
	private String fldTitleChangedBy;
	private String lovTitleCreatedBy;
	private String lovTitleChangedBy;
	private int noofrec;  
	private boolean again;
	private boolean stdJobInfo_S;
	private boolean stdJobInfo_R;
        private boolean obsPM;
        private boolean obsWO;

	private boolean replaced = false;
	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
	private int newWinHeight = 600;
	private int newWinWidth = 900;

	//===============================================================
	// Construction 
	//===============================================================
	public StandardJobOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		genchgpmact = ctx.readFlag("GENCHGPMACT",false);
		updchgpmact = ctx.readFlag("UPDCHGPMACT",false);
		satis = ctx.readFlag("SATIS",true);
		noofrec = ctx.readNumber("NOOFREC",0);
		fmt = mgr.newASPHTMLFormatter();
		stdJobInfo_S  = ctx.readFlag("STDJOBINFOS",false);
		stdJobInfo_R  = ctx.readFlag("STDJOBINFOR",false);
		again = ctx.readFlag("AGAIN",again);
                obsPM  = ctx.readFlag("OBSPM",false);
                obsWO  = ctx.readFlag("OBSWO",false);
		replaced = ctx.readFlag("REPLACED",false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (mgr.buttonPressed("SUBMITMSG"))
			updSubmit();
		else if (mgr.buttonPressed("CANCELMSG"))
			genchgpmact = false;
		else if (mgr.buttonPressed("OKMSG"))
			updchgpmact = false;

		if (!again)
		{
			checkObjAvailable();
			again = true;
		}

		adjust();

		ctx.writeFlag("GENCHGPMACT",genchgpmact);
		ctx.writeFlag("UPDCHGPMACT",updchgpmact);
		ctx.writeNumber("NOOFREC",noofrec);

		ctx.writeFlag(      "AGAIN",again);
		ctx.writeFlag("STDJOBINFOS",stdJobInfo_S);
		ctx.writeFlag("STDJOBINFOR",stdJobInfo_R);
                ctx.writeFlag("OBSPM",obsPM);
                ctx.writeFlag("OBSWO",obsWO);
		ctx.writeFlag("REPLACED",replaced);
	}


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();
		String val = null;
		String txt = null;
		String creid = null;
		String chgid = null;

		val = mgr.readValue("VALIDATE");

		
                if ("CONTRACT".equals(val)) {

                    String sCalendar;  
                    String sCalDesc;
                    String sComp;
        
                    cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("ORG_CODE");

                    cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESCRIPTION" );
                    cmd.addReference("CALENDAR_ID","CALENDER/DATA");

                    cmd = trans.addCustomFunction("GETCOMP", "Site_API.Get_Company", "COMPANY" );
                    cmd.addParameter("CONTRACT");

                    trans=mgr.validate(trans);
                    sComp = trans.getValue("GETCOMP/DATA/COMPANY");
                    sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");
                    sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESCRIPTION");

                    txt = (mgr.isEmpty (sComp) ? "" : sComp) +"^"+ 
                          (mgr.isEmpty (sCalendar) ? "" : sCalendar) + "^"+
                          (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^";  
                    mgr.responseWrite(txt);
                }

		else if ("CREATED_BY".equals(val))
		{
			cmd = trans.addCustomFunction("CREID", "Company_Emp_API.Get_Max_Employee_Id", "CREATED_BY_ID");
			cmd.addParameter("COMPANY");
			cmd.addParameter("CREATED_BY");

			trans = mgr.validate(trans);
			creid = trans.getValue("CREID/DATA/CREATED_BY_ID");

			txt = (mgr.isEmpty(creid) ? "" : (creid));
			mgr.responseWrite(txt);
		}
		else if ("CHANGED_BY".equals(val))
		{
			cmd = trans.addCustomFunction("CHGID", "Company_Emp_API.Get_Max_Employee_Id", "CHANGED_BY_ID");
			cmd.addParameter("COMPANY");
			cmd.addParameter("CHANGED_BY");

			trans = mgr.validate(trans);
			chgid = trans.getValue("CHGID/DATA/CHANGED_BY_ID");

			txt = (mgr.isEmpty(chgid) ? "" : (chgid)) ;
			mgr.responseWrite(txt);
		}

                if ("ORG_CODE".equals(val)) {

                    String sCalendar;  
                    String sCalDesc;
                    
                    cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("ORG_CODE");

                    cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESCRIPTION" );
                    cmd.addReference("CALENDAR_ID","CALENDER/DATA");

                    trans=mgr.validate(trans);
                    sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");
                    sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESCRIPTION");
                        
                    txt = (mgr.isEmpty (sCalendar) ? "" : (sCalendar)) + "^" + 
                          (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^";
                            
                    mgr.responseWrite(txt);
                }


		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();
		ASPBuffer data = null;
                cmd = trans.addEmptyCommand("HEAD","STANDARD_JOB_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void okFind()
	{
		ASPManager mgr = getASPManager();
		trans.clear();
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

                mgr.querySubmit(trans, headblk);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWSTANDARDJOBOVWNODATA: No data found."));
		}
	}


//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void stdJobInfo()
	{
		ASPManager mgr = getASPManager();
		String stdjobtypedb=null; 
		String keys=null;
		headset.setFilterOff();

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
		}

		stdjobtypedb = headset.getRow().getValue("STANDARD_JOB_TYPE_DB");
		String stdjobtype = headset.getRow().getValue("STANDARD_JOB_TYPE");

		keys = headset.getRow().getValue("STD_JOB_ID"); 

		if ("1".equals(stdjobtypedb))
			mgr.redirectTo("SeparateStandardJob2.page?STD_JOB_ID="+mgr.URLEncode(keys)+"&STD_JOB_REVISION"+mgr.URLEncode(headset.getRow().getValue("STD_JOB_REVISION"))+"&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT")));
		else
			mgr.redirectTo("RoundStandardJob.page?STD_JOB_ID="+mgr.URLEncode(keys)+"&STD_JOB_REVISION"+mgr.URLEncode(headset.getRow().getValue("STD_JOB_REVISION"))+"&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT")));
		
		headset.setFilterOff();
	}


        public void updSubmit()
	{
		ASPManager mgr = getASPManager();

		genchgpmact = false;
		headset.setFilterOn();  
		cmd = trans.addCustomCommand("MODPMACT", "Standard_Job_Utility_API.Modify_Pm_Actions_");
		cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));          
		cmd.addParameter("STD_JOB_CONTRACT",headset.getRow().getValue("STD_JOB_REVISION"));          
		cmd.addParameter("STD_JOB_REVISION",headset.getRow().getValue("CONTRACT"));          
		trans=mgr.perform(trans);
		trans.clear();
		headset.setFilterOff();
		updchgpmact = true;  
	}


	public void none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("PCMWSTANDARDJOBOVWNORMB: No RMB methods has been selected."));
	}

	public void showObsoletePM()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		bOpenNewWindow = true;
		urlString = "PmWithObsoleteStdJobs.page?STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID")) +
					"&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"));

		newWinHandle = "PmWithObsoleteStdJobs"; 
	}

	public void updatePM()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		try
		{
			if (!replaced)
			{
				replaced = true;
				cmd = trans.addCustomCommand("UPDPM","Standard_Job_API.Replace_Revsions_");
				cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
				cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
				cmd.addParameter("STD_JOB_REVISION","");
				trans = mgr.perform(trans);

				mgr.showAlert(mgr.translate("PCMWSTANDARDJOBOVWREPLACEMENT: The replacement of the standard job revisions will be performed on the same revision of the PM Actions."));
			}
			else
				mgr.showAlert(mgr.translate("PCMWSTANDARDJOBOVWREPLACEMENTERROR2: The replacement of the standard job revisions is already executing as a background job."));                           
		}
		catch (Throwable any)
		{
			replaced = false;
			mgr.showError(mgr.translate("PCMWSTDJOBREPLACEMENTERROR: The update of PM actions was not performed."));
		}

		headset.refreshRow();

	}

        public void showObsoleteWORev()
        {
            ASPManager mgr = getASPManager();

            if (headlay.isMultirowLayout())
                    headset.goTo(headset.getRowSelected());
            else
                    headset.selectRow();

            bOpenNewWindow = true;
            urlString = "WOWithObsoleteStdJobs.page?STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID")) +
                                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"));

            newWinHandle = "WOWithObsoleteStdJobs"; 
        }

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

	public void select()
	{

		headset.switchSelections();

	}


	public void preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("OBJSTATE").
		setHidden();

		headblk.addField("OBJEVENTS").
		setHidden();

		headblk.addField("STD_JOB_ID").
		setSize(20).
		setMandatory().
		setLabel("PCMWSTANDARDJOBOVWSJID: Standard Job ID").
		setUpperCase().
		setReadOnly().
		setInsertable().
		setMaxLength(12);

		headblk.addField("CONTRACT").
		setSize(15).
                setInsertable().
                setReadOnly().
                setMaxLength(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
		setLOVProperty("TITLE", mgr.translate("PCMWSTANDARDJOBOVWCONT: List of Site")).  
		setCustomValidation("CONTRACT,ORG_CODE","COMPANY,CALENDAR_ID,CALENDAR_DESCRIPTION").
		setLabel("PCMWSTANDARDJOBOVWCON: Site").
		setUpperCase();

		headblk.addField("STD_JOB_REVISION").
		setSize(15).
		setMandatory().
		setLabel("PCMWSTANDARDJOBOVWSJREV: Standard Job Revision").
		setUpperCase().
		setReadOnly().
		setInsertable().
		setMaxLength(6);

		headblk.addField("DEFINITION").
		setSize(25).
		setMandatory().
		setLabel("PCMWSTANDARDJOBOVWDEF: Definition").
		setMaxLength(40);

		headblk.addField("STANDARD_JOB_TYPE").
		setSize(22).
		setMandatory().
		setLabel("PCMWSTANDARDJOBOVWSJTYPE: Type").
		setSelectBox().
		enumerateValues("STANDARD_JOB_TYPE_API").
		setReadOnly().
		setInsertable().
		setMaxLength(20);

		
		headblk.addField("ACTION_CODE_ID").
		setSize(10).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("MAINTENANCE_ACTION",600,450).
		setLabel("PCMWSTANDARDJOBOVWACTCODID: Action").
		setInsertable();

		headblk.addField("ACTION_CODE_DESCRIPTION").
		setSize(20).
		setMaxLength(200).
		setFunction("MAINTENANCE_ACTION_API.Get_Description(:ACTION_CODE_ID)").
		setReadOnly().
		setLabel("PCMWSTANDARDJOBOVWACTCODDES: Action Description");
		mgr.getASPField("ACTION_CODE_ID").setValidation("ACTION_CODE_DESCRIPTION");

                headblk.addField("WORK_DESCRIPTION").
		setSize(25).
                setHeight(3).
		setLabel("PCMWSTANDARDJOBOVWWKDESC: Work Description").
		setMaxLength(1500);

		headblk.addField("TYPE_ID").
		setSize(20).
		setMaxLength(20).
		setDynamicLOV("STD_JOB_TYPE",600,450).
		setLabel("PCMWSTANDARDJOBOVWSTDJOBTYPE: Std Job Type").
		setInsertable().
		setDefaultNotVisible();
		
		headblk.addField("TYPE_DESCRIPTION").
		setSize(20).
		setMaxLength(200).
		setFunction("STD_JOB_TYPE_API.Get_Description(:TYPE_ID)").
		setReadOnly().
		setLabel("PCMWSTANDARDJOBOVWSTDJOBTYPEDESC: Std Job Type Description");
		mgr.getASPField("TYPE_ID").setValidation("TYPE_DESCRIPTION");
		
                headblk.addField("ORG_CODE").
		setSize(20).
		setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
		setLabel("PCMWSEPARATESTANDARDJOB2ORGCODE: Maintenance Organization").
                setCustomValidation("CONTRACT,ORG_CODE","CALENDAR_ID,CALENDAR_DESCRIPTION").
		setUpperCase().
		setMaxLength(8);

		headblk.addField("WORK_TYPE_ID").
		setSize(20).
		setMaxLength(20).
		setUpperCase().
		setDynamicLOV("WORK_TYPE",600,450).
		setLabel("PCMWSTANDARDJOBOVWWORKTYPEID: Work Type").
		setInsertable().
		setDefaultNotVisible();

		headblk.addField("OP_STATUS_ID").
		setSize(3).
		setMaxLength(3).
		setUpperCase().
		setDynamicLOV("OPERATIONAL_STATUS",600,450).
		setLabel("PCMWSTANDARDJOBOVWOPSTATUSID: Operational Status").
		setInsertable().
		setDefaultNotVisible();
		
		headblk.addField("PRIORITY_ID").
		setSize(1).
		setMaxLength(1).
		setUpperCase().
		setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
		setLabel("PCMWSEPSTDJOBPRIORITY: Priority").
		setInsertable().
		setDefaultNotVisible();
		
		headblk.addField("EXECUTION_TIME","Number").
		setLabel("PCMWSEPSTDJOBEXECUTIONTIME: Execution Time").
		setInsertable().
		setDefaultNotVisible();

		headblk.addField("SIGNATURE").
		setSize(25).
		setLabel("PCMWSTANDARDJOBOVWPLANEDBY: Planed By").
		setMaxLength(20).
	        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
		setDefaultNotVisible().
		setReadOnly();

		headblk.addField("CREATED_BY").
		setSize(20).
		setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
		setMandatory().
		setLabel("PCMWSTANDARDJOBOVWCREBY: Created by").
		setUpperCase().
		setReadOnly().
		setInsertable().
		setDefaultNotVisible().
		setCustomValidation("COMPANY,CREATED_BY","CREATED_BY_ID").
		setMaxLength(20);

		headblk.addField("CREATED_BY_ID").
		setSize(25).
		setHidden().
		setLabel("PCMWSTANDARDJOBOVWCREBYID: Signature").
		setUpperCase().
		setMaxLength(11);

		headblk.addField("CHANGED_BY_ID").
		setSize(13).
		setHidden().
		setLabel("PCMWSTANDARDJOBOVWCNGBYID: Signature").
		setDefaultNotVisible().
		setUpperCase().
		setMaxLength(11);

		headblk.addField("CREATION_DATE","Date").
		setSize(25).
		setLabel("PCMWSTANDARDJOBOVWCREDATE: Creation Date").
		setDefaultNotVisible().
		setReadOnly();

		headblk.addField("CHANGED_BY").
		setSize(20).
		setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
		setLabel("PCMWSTANDARDJOBOVWCNGBY: Changed by").
		setUpperCase().
		setDefaultNotVisible().
		setCustomValidation("COMPANY,CHANGED_BY","CHANGED_BY_ID").
		setMaxLength(20);

		headblk.addField("LAST_CHANGED","Date").
		setSize(25).
		setLabel("PCMWSTANDARDJOBOVWLCNG: Last Changed").
		setDefaultNotVisible().
		setReadOnly();

		headblk.addField("STD_TEXT_ONLY").
		setSize(22).
		setLabel("PCMWSTANDARDJOBOVWSTONLY: Standard Text Only").
		setMandatory().
		setCheckBox("FALSE,TRUE").
		setReadOnly().
		setInsertable().
		setMaxLength(5);

		headblk.addField("STATE").
		setSize(25).
		setLabel("PCMWSTANDARDJOBOVWSTATE: Status").
		setReadOnly().
		setMaxLength(253); 

                
		headblk.addField("COPIED_SITE").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWCPYSITE: Copied From Site").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("COPIED_STD_JOB_ID").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWCPYSTDJOB: Copied From Std Job").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("COPIED_REVISION").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWCPYREV: Copied From Revision").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("REPLACES_REVISION").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWREPREV: Replaces Revision").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("REPLACED_REVISION").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWREPDREV: Replaced Revision").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("START_VALUE").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWSTSRTVALUE: Start Value").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("PM_START_UNIT").
		setSize(20).
		setLabel("PCMWSTANDARDJOBOVWSTARTUNIT: Start Unit").
		setReadOnly().
		setMaxLength(200).
		setDefaultNotVisible(); 

		headblk.addField("INTERVAL").
		setSize(5).
		setLabel("PCMWSTANDARDJOBOVWINTERVAL: Interval").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("PM_INTERVAL_UNIT").
		setSize(20).
		setLabel("PCMWSTANDARDJOBOVINTUNIT: Interval Unit").
		setReadOnly().
		setMaxLength(200).
		setDefaultNotVisible(); 

		headblk.addField("CALL_CODE").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWEVENT: Event").
		setDynamicLOV("MAINTENANCE_EVENT").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("START_CALL").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWSTARTEVENT: Start Event").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("CALL_INTERVAL").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWEVENTINT: Event Interval").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("VALID_FROM").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWVALIEDFROM: Valid From").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("VALID_TO").
		setSize(15).
		setLabel("PCMWSTANDARDJOBOVWVALIEDTO: Valid To").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("CALENDAR_ID").
		setSize(15).
		setDynamicLOV("WORK_TIME_CALENDAR").
		setLabel("PCMWSTANDARDJOBOVWCALENDAR: Calendar").
		setReadOnly().
		setMaxLength(5).
		setDefaultNotVisible(); 

		headblk.addField("CALENDAR_DESCRIPTION").
		setSize(20).
		setLabel("PCMWSTANDARDJOBOVWCALENDARDES: Description").
		setReadOnly().
		setFunction("WORK_TIME_CALENDAR_API.Get_Description(:CALENDAR_ID)").
		setMaxLength(200).
		setDefaultNotVisible(); 
		mgr.getASPField("CALENDAR_ID").setValidation("CALENDAR_DESCRIPTION");

		headblk.addField("COMPANY").
		setSize(10).
		setMandatory().
		setHidden().
		setLabel("PCMWSTANDARDJOBOVWCOMPANY: Company").
		setUpperCase();

		headblk.addField("STANDARD_JOB_TYPE_DB").
		setSize(20).
		setHidden().
		setLabel("PCMWSTANDARDJOBOVWSJTDB: Standard Job Type DB").
		setReadOnly();

		headblk.addField("IS_REPLACEABLE").
		setHidden().
		setFunction("Standard_Job_API.Is_Replaceable(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

		headblk.addField("HAS_OBS_WO").
		setHidden().
		setFunction("Standard_Job_API.Obs_Std_Has_Wo(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

		headblk.setView("STANDARD_JOB");
		headblk.defineCommand("STANDARD_JOB_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);

		headbar.addCustomCommand("none","");
		headbar.addCustomCommand("stdJobInfo",mgr.translate("PCMWSTANDARDJOBOVWJOBINFO: Standard Job Information..."));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("showObsoleteWORev",mgr.translate("PCMWSTANDARDJOBOVWOBSOLETEWOREV: Obsolete Revisions on Active Work Orders..."));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("showObsoletePM",mgr.translate("PCMWSTANDARDJOBOVWOBSPMREV: Obsolete Revisions on PM Actions..."));
		headbar.addCustomCommand("updatePM",mgr.translate("PCMWSTANDARDJOBOVWUPDATEPMREV: Replace Obsolete Revisions on PM Actions"));

		headbar.addCommandValidConditions("showObsoletePM","IS_REPLACEABLE","Enable","TRUE");
		headbar.addCommandValidConditions("updatePM","IS_REPLACEABLE","Enable","TRUE");
		headbar.addCommandValidConditions("showObsoleteWORev","HAS_OBS_WO","Enable","TRUE");

		headbar.disableCommand(headbar.EDIT);
		headbar.enableCommand(headbar.SUBMIT);

		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWSTANDARDJOBOVWHD: Standard Jobs"));
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2); 

		headlay.setSimple("ACTION_CODE_DESCRIPTION");
		headlay.setSimple("TYPE_DESCRIPTION");
		headlay.setSimple ("CALENDAR_DESCRIPTION");

		headlay.setFieldOrder("STD_JOB_ID,CONTRACT,STD_JOB_REVISION,DEFINITION,STANDARD_JOB_TYPE,ACTION_CODE_ID,ACTION_CODE_DESCRIPTION,WORK_DESCRIPTION,TYPE_ID,TYPE_DESCRIPTION,ORG_CODE,WORK_TYPE_ID,OP_STATUS_ID,PRIORITY_ID,EXECUTION_TIME,SIGNATURE,COPIED_SITE,CREATED_BY,COPIED_STD_JOB_ID,CREATION_DATE,COPIED_REVISION,CHANGED_BY,REPLACES_REVISION,LAST_CHANGED,REPLACED_REVISION,START_VALUE,CALL_CODE,PM_START_UNIT,START_CALL,INTERVAL,CALL_INTERVAL,PM_INTERVAL_UNIT,VALID_FROM,VALID_TO,CALENDAR_ID,CALENDAR_DESCRIPTION,STD_TEXT_ONLY,STATE");
	}

	public void checkObjAvailable()
	{
		ASPManager mgr = getASPManager();

		ASPBuffer availObj;
		trans.clear();
		trans.addSecurityQuery("SEPARATE_STANDARD_JOB,ROUND_STANDARD_JOB,WO_WITH_OBS_STD_JOBS,PM_ACTION_WITH_OBS_STD_JOBS");
		trans.addSecurityQuery("Standard_Job_Utility_API","Modify_Pm_Actions_");
		trans.addPresentationObjectQuery("PCMW/SeparateStandardJob2.page,PCMW/RoundStandardJob.page,PCMW/WOWithObsoleteStdJobs.page,PCMW/PmWithObsoleteStdJobs.page");

		trans = mgr.perform(trans);

		availObj = trans.getSecurityInfo();

		if (availObj.itemExists("SEPARATE_STANDARD_JOB") && availObj.namedItemExists("PCMW/SeparateStandardJob2.page"))
			stdJobInfo_S = true;

		if (availObj.itemExists("ROUND_STANDARD_JOB")  && availObj.namedItemExists("PCMW/RoundStandardJob.page"))
			stdJobInfo_R = true;

                if (availObj.itemExists("PM_ACTION_WITH_OBS_STD_JOBS") && availObj.namedItemExists("PCMW/PmWithObsoleteStdJobs.page"))
	                obsPM = true;

                if (availObj.itemExists("WO_WITH_OBS_STD_JOBS") && availObj.namedItemExists("PCMW/WOWithObsoleteStdJobs.page"))
	                obsWO = true;
        }

	public void DissableRmbs()
	{
		ASPManager mgr = getASPManager();

		if ("1".equals(headset.getRow().getValue("STANDARD_JOB_TYPE_DB")))
		{
			if (!stdJobInfo_S)
				headbar.removeCustomCommand("stdJobInfo");
		}
		else
		{
			if (!stdJobInfo_R)
				headbar.removeCustomCommand("roundpmAction");
		}

                if (!obsPM)
                {
                        debug("****obsPM "+obsPM);
                        headbar.removeCustomCommand("showObsoletePM");
                        headbar.removeCustomCommand("updatePM");
                }

		if (!obsWO)
		        headbar.removeCustomCommand("showObsoleteWORev");
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		mgr.createSearchURL(headblk); 
		if (headset.countRows() > 0)
		{
			DissableRmbs();
		}

		fldTitleCreatedBy = mgr.translate("PCMWSTANDARDJOBOVWCREATEDBYFLD: Created+by");
		fldTitleChangedBy = mgr.translate("PCMWSTANDARDJOBOVWCHANGEDBYFLD: Changed+by");

		lovTitleCreatedBy = mgr.translate("PCMWSTANDARDJOBOVWCREATEDBYLOV: List+of+Created+by");
		lovTitleChangedBy = mgr.translate("PCMWSTANDARDJOBOVWCHANGEDBYLOV: List+of+Changed+by");

		if (headlay.isNewLayout() || headlay.isEditLayout())
		{
			mgr.getASPField("DEFINITION").setSize(40);  
			mgr.getASPField("WORK_DESCRIPTION").setSize(40);  
		}

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWSTANDARDJOBOVWTITLE: Overview - Standard Jobs";
	}

	protected String getTitle()
	{
		return "PCMWSTANDARDJOBOVWTITLE: Overview - Standard Jobs";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		if (headlay.isVisible())
		{
			if (!genchgpmact && !updchgpmact)
			{
				appendToHTML(headlay.show());
			}

			if (genchgpmact || updchgpmact)
			{
				appendToHTML(fmt.drawReadLabel("PCMWSTANDARDJOBOVWINFMESG: Information Message"));
				appendToHTML("      </br></br>\n");
			}

			if (genchgpmact && !updchgpmact)
			{
				appendToHTML(fmt.drawReadLabel(noofrec+" "+ "PCMWSTANDARDJOBOVWMESSAGE1: Standard Job is changed and will generate changes on PM Actions."));
				appendToHTML("      </br></br>\n");
				appendToHTML(fmt.drawSubmit("SUBMITMSG",mgr.translate("PCMWSTANDARDJOBOVWOKBUTT: OK"),""));
				appendToHTML("&nbsp;&nbsp;\n");
				appendToHTML(fmt.drawSubmit("CANCELMSG",mgr.translate("PCMWSTANDARDJOBOVWCANCELBUTT: Cancel"),""));
			}

			if (!genchgpmact && updchgpmact)
			{
				appendToHTML(fmt.drawReadLabel(noofrec+" "+ "PCMWSTANDARDJOBOVWMESSAGE2: Standard Job generated changes on PM actions in a background process."));
				appendToHTML("      </br></br>\n");
				appendToHTML(fmt.drawSubmit("OKMSG",mgr.translate("PCMWSTANDARDJOBOVWOKBUTT: OK"),""));
			}
		}
		appendDirtyJavaScript("function lovCreatedBy(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__')\n");
		appendDirtyJavaScript("	  openLOVWindow('CREATED_BY',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
		appendDirtyJavaScript(fldTitleCreatedBy);
		appendDirtyJavaScript("&__TITLE=");
		appendDirtyJavaScript(lovTitleCreatedBy);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,445,'validateCreatedBy');\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("	  openLOVWindow('CREATED_BY',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
		appendDirtyJavaScript(fldTitleCreatedBy);
		appendDirtyJavaScript("&__TITLE=");
		appendDirtyJavaScript(lovTitleCreatedBy);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
		appendDirtyJavaScript("		,600,445,'validateCreatedBy');\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function lovChangedBy(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__')\n");
		appendDirtyJavaScript("	  openLOVWindow('CHANGED_BY',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
		appendDirtyJavaScript(fldTitleChangedBy);
		appendDirtyJavaScript("&__TITLE=");
		appendDirtyJavaScript(lovTitleChangedBy);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		,600,445,'validateChangedBy');\n");
		appendDirtyJavaScript("	else\n");
		appendDirtyJavaScript("	  openLOVWindow('CHANGED_BY',i,\n");
		appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
		appendDirtyJavaScript(fldTitleChangedBy);
		appendDirtyJavaScript("&__TITLE=");
		appendDirtyJavaScript(lovTitleChangedBy);
		appendDirtyJavaScript("'\n");
		appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
		appendDirtyJavaScript("		,600,445,'validateChangedBy');	\n");
		appendDirtyJavaScript("}\n");

		if (bOpenNewWindow)
		{
			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(urlString);
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
			appendDirtyJavaScript(newWinWidth);
			appendDirtyJavaScript(",height=");
			appendDirtyJavaScript(newWinHeight);
			appendDirtyJavaScript("\"); \n");       
		}
	}
}
