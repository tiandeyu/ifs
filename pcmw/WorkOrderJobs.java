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
*  File        : WorkOrderJobs.java 
*  Created     : ARWILK  2004-06-03  Created for IID AMEC109A - "Multiple STD Jobs on PM and WO".
*  Modified    :
*  060814    AMDILK   Bug 58216, Eliminated SQL errors in web applications. Modified methods okFind(), 
*                     okFindHEAD(), countFindHEAD()
*  060904    AMDILK   Merged with the Bug Id 58216
*  070731    ILSOLK   Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------  
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class WorkOrderJobs extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderJobs");   

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;

	private ASPBlock upperblk;
	private ASPRowSet upperset;
	private ASPCommandBar upperbar;
	private ASPTable uppertbl;
	private ASPBlockLayout upperlay;  

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;  

	//private ASPBlock prntblk;
	//private ASPRowSet printset;  

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String qrystr;
	private String rRowN;
	private String rRawId;
	private String callingUrl;
	private ASPBuffer temp;
	private ASPTransactionBuffer secBuff;
	private ASPCommand cmd;
	private ASPBuffer data;
	private int headrowno;
	private ASPBuffer row;
	//private String sWoNo;
	private String sContract;  

	private ASPQuery q;
	private boolean actEna1;
	private boolean again;

	private String performRMB;
	private String URLString;
	private String WindowName;


	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderJobs(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{

		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		sContract = ctx.readValue("SCONNNT",sContract);
		//sWoNo = ctx.readValue("SWONO",sWoNo);
		qrystr = ctx.readValue("QRYSTR","");
		again = ctx.readFlag("AGAIN",false);
		actEna1 = ctx.readFlag("ACTENA1",false);

		callingUrl = ctx.getGlobal("CALLING_URL");  

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();
		//          checkObjAvaileble();
		adjust();                                 


		ctx.writeValue("SCONNNT",sContract);

		//ctx.writeValue("SWONO",sWoNo);
		ctx.writeValue("QRYSTR",qrystr);
		ctx.writeFlag("AGAIN",again);
		ctx.writeFlag("ACTENA1",actEna1);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		String txt;

		if ("STD_JOB_ID".equals(val))
		{
			String valueStr = mgr.readValue("STD_JOB_ID");
			String stdJobId  = "";
			String stdJobContract = mgr.readValue("STD_JOB_CONTRACT");
			String stdJobRev = "";

			if (valueStr.indexOf("~") > -1)
			{
				String[] fieldValues = valueStr.split("~");

				stdJobId = fieldValues[0];
				stdJobRev = fieldValues[2];
			}
			else
			{
				stdJobId = valueStr;
				stdJobRev = mgr.readValue("STD_JOB_REVISION");
			}

			trans.clear();

			cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
			cmd.addParameter("STD_JOB_ID", stdJobId);
			cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
			cmd.addParameter("STD_JOB_REVISION", stdJobRev);

			trans = mgr.validate(trans);

			String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");

			txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)?"":desc) + "^";

			mgr.responseWrite(txt);
		}
		else if ("STD_JOB_REVISION".equals(val))
		{
			trans.clear();

			cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
			cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
			cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
			cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

			trans = mgr.validate(trans);

			String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");

			txt = (mgr.isEmpty(desc)?"":desc) + "^";

			mgr.responseWrite(txt);
		}
		else if ("STD_JOB_FLAG".equals(val))
		{
			int nJobExist = 0;

			String sStdJobExist = "";
			int nRoleExist = 0;
			int nMatExist = 0;
			int nToolExist = 0;
			int nPlanningExist = 0;
			int nDocExist = 0;
			String sStdJobId = "";
			String sStdJobContract = "";
			String sStdJobRevision = "";

			double nQty = 0;
			String sIsSeparate = "";

			String sAgreementId = "";

			trans.clear();

			cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
			cmd.addParameter("HEAD_WO_NO");
			cmd.addParameter("JOB_ID"); 

			cmd = trans.addCustomFunction("GETAGR", "Active_Separate_API.Get_Agreement_Id", "S_AGREEMENT_ID");
			cmd.addParameter("HEAD_WO_NO");

			trans = mgr.validate(trans);

			nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
			sAgreementId = trans.getValue("GETAGR/DATA/S_AGREEMENT_ID");

			if (nJobExist == 1)
			{
				trans.clear();

				cmd = trans.addCustomFunction("GETSTDJOBEXIST", "Work_Order_Job_API.Std_Job_Exist", "S_STD_JOB_EXIST");
				cmd.addParameter("HEAD_WO_NO");
				cmd.addParameter("JOB_ID");         

				cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
				cmd.addParameter("N_ROLE_EXIST","0");
				cmd.addParameter("N_MAT_EXIST","0");
				cmd.addParameter("N_TOOL_EXIST","0");
				cmd.addParameter("N_PLANNING_EXIST","0");
				cmd.addParameter("N_DOC_EXIST","0");
				cmd.addParameter("S_STD_JOB_ID");
				cmd.addParameter("S_STD_JOB_CONTRACT");
				cmd.addParameter("S_STD_JOB_REVISION");
				cmd.addParameter("HEAD_WO_NO");
				cmd.addParameter("JOB_ID"); 

				cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
				cmd.addParameter("HEAD_WO_NO");
				cmd.addParameter("JOB_ID"); 

				cmd = trans.addCustomFunction("GETISSEP", "Active_Separate_API.Is_Separate", "S_IS_SEPARATE");
				cmd.addParameter("HEAD_WO_NO");

				trans = mgr.validate(trans);

				sStdJobExist = trans.getValue("GETSTDJOBEXIST/DATA/S_STD_JOB_EXIST");

				nRoleExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_ROLE_EXIST")).intValue();
				nMatExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_MAT_EXIST")).intValue();
				nToolExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_TOOL_EXIST")).intValue();
				nPlanningExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_PLANNING_EXIST")).intValue();
				nDocExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_DOC_EXIST")).intValue();
				sStdJobId = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_ID");
				sStdJobContract = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_CONTRACT");
				sStdJobRevision = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_REVISION");

				nQty = trans.getNumberValue("GETQTY/DATA/N_QTY");
				sIsSeparate = trans.getValue("GETISSEP/DATA/S_IS_SEPARATE");
			}

			txt = nJobExist + "^" +
				  (mgr.isEmpty(sStdJobExist)?"":sStdJobExist) + "^" +
				  nRoleExist + "^" +
				  nMatExist + "^" +
				  nToolExist + "^" +
				  nPlanningExist + "^" +
				  nDocExist + "^" +
				  (mgr.isEmpty(sStdJobId)?"":sStdJobId) + "^" +
				  (mgr.isEmpty(sStdJobContract)?"":sStdJobContract) + "^" +
				  (mgr.isEmpty(sStdJobRevision)?"":sStdJobRevision) + "^" +
				  nQty + "^" +
				  (mgr.isEmpty(sIsSeparate)?"":sIsSeparate) + "^" +
				  (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^";

			debug(txt);
			mgr.responseWrite(txt);
		}
		mgr.endResponse();
	}

	//-----------------------------------------------------------------------------
	//-------------------------  CUSTOM FUNCTIONS  --------------------------------
	//-----------------------------------------------------------------------------

	//-----------------------------------------------------------------------------
	//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
	//-----------------------------------------------------------------------------

	public void newRowHEAD()
	{
		ASPManager mgr = getASPManager();

		String sFormName = "";

		trans.clear();

		cmd = trans.addEmptyCommand("HEAD", "WORK_ORDER_JOB_API.New__", headblk);
		cmd.setParameter("HEAD_WO_NO", upperset.getValue("WO_NO"));
		cmd.setOption("ACTION", "PREPARE");

		cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "DATE_FROM");
		cmd.addParameter("HEAD_WO_NO", upperset.getValue("WO_NO"));

		cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "DATE_TO");
		cmd.addParameter("HEAD_WO_NO", upperset.getValue("WO_NO"));

		// The same is done in prepare insert.

		/*cmd = trans.addCustomFunction("GETSITE", "Active_Separate_API.Get_Contract", "STD_JOB_CONTRACT");
		cmd.addParameter("WO_NO", upperset.getValue("WO_NO"));

		cmd = trans.addCustomFunction("GETCOMPANY", "Site_API.Get_Company", "COMPANY");
		cmd.addReference("STD_JOB_CONTRACT", "GETSITE/DATA");*/

		trans = mgr.perform(trans);

		data = trans.getBuffer("HEAD/DATA");

		//String sCompany = trans.getValue("GETCOMPANY/DATA/COMPANY");
		Date dPlanSDate = null;
		Date dPlanFDate = null;

		// Actually this check is not needed. The spec has it because only prepare work order has tha plan start date and plan finish date.
		//  Report in Work Order has not. Thus a database call is done to fetch them. Thus in web a database call should be done for both form without 
		// chcking the form names.

		if ("PrepareWO".equals(sFormName))
		{
			dPlanSDate = null;// set Start Date to Date From
			dPlanFDate = null;// set Completion Date to Date To
		}
		//else if ("ReportInWO".equals(sFormName))
		{
			dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("DATE_FROM");
			dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("DATE_TO");
		}

		//data.setFieldItem("WO_NO", upperset.getValue("WO_NO"));
		//data.setFieldItem("COMPANY", sCompany);
		data.setFieldDateItem("DATE_FROM", dPlanSDate);
		data.setFieldDateItem("DATE_TO", dPlanFDate);
		headset.addRow(data);
	}

	/*public void saveReturnHEAD()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		headset.changeRow();

		int currrow = headset.getCurrentRowNo();
		mgr.submit(trans);

		headset.goTo(currrow);
		headlay.setLayoutMode(headlay.getHistoryMode());    
	}*/

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(upperblk);

		if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
		    q.addWhereCondition("WO_NO = ?");
                    q.addParameter("WO_NO", mgr.readValue("WO_NO"));
		}

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		q.includeMeta("ALL");
		mgr.querySubmit(trans, upperblk);

		mgr.showAlert(headset.syncItemSets());
		//if (upperset.countRows() > 0)
		//okFindHEAD();
	}

	public void okFindHEAD()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int upperrowno = upperset.getCurrentRowNo();

		q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
                q.addParameter("WO_NO", upperset.getValue("WO_NO") );
		q.addParameter("STD_JOB_CONTRACT", upperset.getValue("CONTRACT"));

		q.includeMeta("ALL");
		mgr.querySubmit(trans, headblk);

		upperset.goTo(upperrowno);
	}

	public void countFindHEAD()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int upperrowno = upperset.getCurrentRowNo();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
                q.addParameter("WO_NO", upperset.getValue("WO_NO") );
		q.addParameter("STD_JOB_CONTRACT", upperset.getValue("CONTRACT"));
		mgr.submit(trans);

		upperset.goTo(upperrowno);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

// -----------------------------------------------------------------------------
// ------------------------        PREDEFINE     -------------------------------
// -----------------------------------------------------------------------------

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		upperblk = mgr.newASPBlock("UPPER");

		upperblk.addField("UPPER_OBJID").
		setDbName("OBJID").
		setHidden();

		upperblk.addField("UPPER_OBJVERSION").
		setDbName("OBJVERSION").
		setHidden();

		upperblk.addField("UPPER_OBJSTATE").
		setDbName("OBJSTATE").
		setHidden();

		upperblk.addField("WO_NO","Number","#").
		setSize(13).
		setLabel("PCMWWOJOBUPPERWONO: WO No").
		setReadOnly().
		setMaxLength(8);

		upperblk.addField("CONTRACT").
		setSize(5).
		setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
		setLabel("PCMWWOJOBUPPERCONTRACT: Site").
		setUpperCase().
		setReadOnly().
		setMaxLength(5);

		upperblk.addField("UPPER_STATE").
		setDbName("STATE").
		setSize(11).
		setLabel("PCMWWOJOBUPPERSTATE: Status").
		setReadOnly().
		setMaxLength(30); 

		upperblk.setView("ACTIVE_SEPARATE");

		upperset = upperblk.getASPRowSet();

		uppertbl = mgr.newASPTable(upperblk);
		uppertbl.setWrap();

		upperbar = mgr.newASPCommandBar(upperblk);

		upperbar.disableCommand(upperbar.FIND);
		upperbar.disableCommand(upperbar.COUNTFIND);
		upperbar.disableCommand(upperbar.BACK);

		upperlay = upperblk.getASPBlockLayout();
		upperlay.setDefaultLayoutMode(upperlay.SINGLE_LAYOUT);


		//----------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------


		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("HEAD_OBJID").
		setDbName("OBJID").
		setHidden();

		headblk.addField("HEAD_OBJVERSION").
		setDbName("OBJVERSION").
		setHidden();

		headblk.addField("HEAD_WO_NO", "Number", "#").
		setDbName("WO_NO").
		setHidden().
		setReadOnly().
		setInsertable().
		setMandatory();

		headblk.addField("JOB_ID", "Number").
		setLabel("PCMWWOJOBHEADJOBID: Job ID").
		setReadOnly().
		setInsertable().
		setMandatory();

		headblk.addField("STD_JOB_ID").
		setSize(15).
		setLabel("PCMWWOJOBHEADSTDJOBID: Standard Job ID").
		setLOV("StandardJobLov.page", "STD_JOB_CONTRACT CONTRACT", 600, 445, true).
		setUpperCase().
		setInsertable().
		setQueryable().
		setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION").
		setMaxLength(12);

		headblk.addField("STD_JOB_CONTRACT").
		setSize(10).
		setLabel("PCMWWOJOBHEADSTDJOBCONTRACT: Site").
		setUpperCase().
		setReadOnly().
		setMaxLength(5);

		headblk.addField("STD_JOB_REVISION").
		setSize(10).
		setLabel("PCMWWOJOBHEADSTDJOBREVISION: Revision").
		setDynamicLOV("STANDARD_JOB", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445, true).    
		setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION").
		setUpperCase().
		setInsertable().
		setQueryable().
		setMaxLength(6);

		headblk.addField("DESCRIPTION").
		setSize(35).
		setLabel("PCMWWOJOBHEADDESCRIPTION: Description").
		setUpperCase().
		setMandatory().
		setInsertable().
		setMaxLength(4000);

		headblk.addField("QTY", "Number").
		setLabel("PCMWWOJOBHEADQTY: Quantity").
		setMandatory().
		setInsertable();

		headblk.addField("COMPANY").
		setSize(20).
		setHidden().
		setUpperCase().
		setInsertable();

		headblk.addField("EMPLOYEE_ID").
		setSize(15).
		setLabel("PCMWWOJOBHEADEMPLOYEEID: Employee ID").
		setDynamicLOV("EMPLOYEE_NO", "COMPANY", 600, 445).    
		setUpperCase().
		setInsertable().
		setQueryable().
		setMaxLength(11);

		headblk.addField("SIGN_ID").
		setSize(35).
		setLabel("PCMWWOJOBHEADSIGNID: Signature").
		setQueryable().
		setFunction("Company_Emp_API.Get_Person_Id(COMPANY, EMPLOYEE_ID)").
		setReadOnly();
		mgr.getASPField("EMPLOYEE_ID").setValidation("Company_Emp_API.Get_Person_Id", "COMPANY,EMPLOYEE_ID", "SIGN_ID");

		headblk.addField("DATE_FROM", "Datetime").
		setSize(20).
		setLabel("PCMWWOJOBHEADDATEFROM: Date From").
		setInsertable();

		headblk.addField("DATE_TO", "Datetime").
		setSize(20).
		setLabel("PCMWWOJOBHEADDATETO: Date To").
		setInsertable();

		headblk.addField("STD_JOB_FLAG", "Number").
		setHidden().
		setCustomValidation("HEAD_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_IS_SEPARATE,S_AGREEMENT_ID").
		setInsertable();

		headblk.addField("KEEP_CONNECTIONS").
		setHidden().
		setSize(3).
		setInsertable();

		headblk.addField("RECONNECT").
		setHidden().
		setSize(3).
		setInsertable();

		// -----------------------------------------------------------------------
		// -----------------------  Hidden Fields --------------------------------
		// -----------------------------------------------------------------------

		headblk.addField("N_JOB_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("S_STD_JOB_EXIST").
		setFunction("''").
		setHidden();

		headblk.addField("N_ROLE_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("N_MAT_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("N_TOOL_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("N_PLANNING_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("N_DOC_EXIST", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("S_STD_JOB_ID").
		setFunction("''").
		setHidden();

		headblk.addField("S_STD_JOB_CONTRACT").
		setFunction("''").
		setHidden();

		headblk.addField("S_STD_JOB_REVISION").
		setFunction("''").
		setHidden();

		headblk.addField("N_QTY", "Number").
		setFunction("0").
		setHidden();

		headblk.addField("S_IS_SEPARATE").
		setFunction("''").
		setHidden();

		headblk.addField("S_AGREEMENT_ID").
		setFunction("''").
		setHidden();

		headblk.setView("WORK_ORDER_JOB");
		headblk.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
		headblk.setMasterBlock(upperblk);

		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWWOJOBHEADWOJOBS: Jobs"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.enableCommand(headbar.FIND);

		headbar.defineCommand(headbar.NEWROW, "newRowHEAD");
		headbar.defineCommand(headbar.SAVERETURN, "saveReturnHEAD", "checkSaveParams(i)");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMPCMWWOJOBHEADTITLE: Jobs";
	}

	protected String getTitle()
	{
		return "PCMPCMWWOJOBHEADTITLE: Jobs";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(upperlay.show());

		if (upperset.countRows() > 0)
			appendToHTML(headlay.show());

		appendToHTML("<br>\n");

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("window.name = \"WorkOrderJobs\";\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(performRMB);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  url_to_go = '");
		appendDirtyJavaScript(URLString);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window_name = '");
		appendDirtyJavaScript(WindowName);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
		appendDirtyJavaScript("}\n\n");    

		appendDirtyJavaScript("function checkSaveParams(i)\n");
		appendDirtyJavaScript("{");
		appendDirtyJavaScript("   r = __connect(\n");
		appendDirtyJavaScript("		  '");
		appendDirtyJavaScript(mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=STD_JOB_FLAG'\n");
		appendDirtyJavaScript("       + '&HEAD_WO_NO=' + URLClientEncode(getValue_('HEAD_WO_NO',i))\n");
		appendDirtyJavaScript("       + '&JOB_ID=' + URLClientEncode(getValue_('JOB_ID',i))\n");
		appendDirtyJavaScript("       + '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
		appendDirtyJavaScript("       + '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
		appendDirtyJavaScript("       + '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
		appendDirtyJavaScript("       );\n");
		appendDirtyJavaScript("   window.status='';\n");
		appendDirtyJavaScript("   if( checkStatus_(r,'STD_JOB_FLAG',i,'STD_JOB_FLAG') )\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("           assignValue_('N_JOB_EXIST',i,0);\n");
		appendDirtyJavaScript("           assignValue_('S_STD_JOB_EXIST',i,1);\n");
		appendDirtyJavaScript("           assignValue_('N_ROLE_EXIST',i,2);\n");
		appendDirtyJavaScript("           assignValue_('N_MAT_EXIST',i,3);\n");
		appendDirtyJavaScript("           assignValue_('N_TOOL_EXIST',i,4);\n");
		appendDirtyJavaScript("           assignValue_('N_PLANNING_EXIST',i,5);\n");
		appendDirtyJavaScript("           assignValue_('N_DOC_EXIST',i,6);\n");
		appendDirtyJavaScript("           assignValue_('S_STD_JOB_ID',i,7);\n");
		appendDirtyJavaScript("           assignValue_('S_STD_JOB_CONTRACT',i,8);\n");
		appendDirtyJavaScript("           assignValue_('S_STD_JOB_REVISION',i,9);\n");
		appendDirtyJavaScript("           assignValue_('N_QTY',i,10);\n");
		appendDirtyJavaScript("           assignValue_('S_IS_SEPARATE',i,11);\n");
		appendDirtyJavaScript("           assignValue_('S_AGREEMENT_ID',i,12);\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   if (getValue_('N_JOB_EXIST',i) == 1)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      if (getValue_('S_STD_JOB_EXIST',i) == 'TRUE')\n"); 
		appendDirtyJavaScript("      {\n");
		appendDirtyJavaScript("         if ((getValue_('N_ROLE_EXIST',i) == 1 || getValue_('N_MAT_EXIST',i) == 1 || getValue_('N_TOOL_EXIST',i) == 1 || getValue_('N_PLANNING_EXIST',i) == 1 || getValue_('N_DOC_EXIST',i) == 1)\n");
		appendDirtyJavaScript("             && getValue_('STD_JOB_ID',i) == ''\n");
		appendDirtyJavaScript("             && getValue_('STD_JOB_REVISION',i) == '')\n");
		appendDirtyJavaScript("         {\n");
		appendDirtyJavaScript("            if (getValue_('S_IS_SEPARATE',i) == 'TRUE')\n");
		appendDirtyJavaScript("            {\n");
		appendDirtyJavaScript("               if (confirm('");
		appendDirtyJavaScript(mgr.translate("WOJOB_REMSEP_STDJOB: Do you want to remove connected Operations, Materials, Planning, Tools/Facilities and Documents?"));
		appendDirtyJavaScript("'))\n");
		appendDirtyJavaScript("                  getField_('KEEP_CONNECTIONS',i).value = 'NO';\n");
		appendDirtyJavaScript("               else\n");
		appendDirtyJavaScript("                  getField_('KEEP_CONNECTIONS',i).value = 'YES';\n");
		appendDirtyJavaScript("            }\n");
		appendDirtyJavaScript("            else if (getValue_('S_IS_SEPARATE',i) == 'FALSE')\n");
		appendDirtyJavaScript("            {\n");
		appendDirtyJavaScript("               if (confirm('");
		appendDirtyJavaScript(mgr.translate("PCMWWORKORDERJOBSREMOVEMATEPLANDOC: Do you want to remove connected Materials, Planning and Documents?"));
		appendDirtyJavaScript("'))\n");
		appendDirtyJavaScript("                  getField_('KEEP_CONNECTIONS',i).value = 'NO';\n");
		appendDirtyJavaScript("               else\n");
		appendDirtyJavaScript("                  getField_('KEEP_CONNECTIONS',i).value = 'YES';\n");
		appendDirtyJavaScript("            }\n");
		appendDirtyJavaScript("         }\n");
		appendDirtyJavaScript("         else if ((getValue_('N_ROLE_EXIST',i) == 1 || getValue_('N_MAT_EXIST',i) == 1 || getValue_('N_TOOL_EXIST',i) == 1 || getValue_('N_PLANNING_EXIST',i) == 1 || getValue_('N_DOC_EXIST',i) == 1)\n"); 
		appendDirtyJavaScript("                  && getValue_('STD_JOB_ID',i) != ''\n");
		appendDirtyJavaScript("                  && getValue_('STD_JOB_REVISION',i) != ''\n");
		appendDirtyJavaScript("                  && getValue_('N_QTY',i) != ''\n");
		appendDirtyJavaScript("                  && (getValue_('S_STD_JOB_ID',i) != getValue_('STD_JOB_ID',i) || getValue_('S_STD_JOB_REVISION',i) != getValue_('STD_JOB_REVISION',i) || getValue_('N_QTY',i) != getValue_('QTY',i)))\n");
		appendDirtyJavaScript("         {\n");
		appendDirtyJavaScript("            if (getValue_('S_IS_SEPARATE',i) == 'TRUE')\n");
		appendDirtyJavaScript("            {\n");
		appendDirtyJavaScript("               if (confirm('");
		appendDirtyJavaScript(mgr.translate("WOJOB_RECSEP_STDJOB: Do you want to remove and reconnect Operations, Materials, Tools/Facilities, Planning and Documents?"));
		appendDirtyJavaScript("'))\n");
		appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'YES';\n");
		appendDirtyJavaScript("               else\n");
		appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'NO';\n");
		appendDirtyJavaScript("            }\n");
		appendDirtyJavaScript("            else if (getValue_('S_IS_SEPARATE',i) == 'FALSE')\n");
		appendDirtyJavaScript("            {\n");
		appendDirtyJavaScript("               if (confirm('");
		appendDirtyJavaScript(mgr.translate("WOJOB_RECROUND_STDJOB: Do you want to remove and reconnect Materials and Documents?"));
		appendDirtyJavaScript("'))\n");
		appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'YES';\n");
		appendDirtyJavaScript("               else\n");
		appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'NO';\n");
		appendDirtyJavaScript("            }\n");
		appendDirtyJavaScript("         }\n");
		appendDirtyJavaScript("      }\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   if (getValue_('STD_JOB_ID',i) != '' && getValue_('STD_JOB_CONTRACT',i) != '' && getValue_('STD_JOB_REVISION',i) != '' && getValue_('S_AGREEMENT_ID',i) != '')\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      if (confirm('");
		appendDirtyJavaScript(mgr.translate("WOJOB_INFO_STDJOB: Update with agreement prices?"));
		appendDirtyJavaScript("'))\n"); 
		appendDirtyJavaScript("         getField_('STD_JOB_FLAG',i).value = '1';\n");
		appendDirtyJavaScript("      else\n");
		appendDirtyJavaScript("         getField_('STD_JOB_FLAG',i).value = '0';\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   return true;\n");
		appendDirtyJavaScript("}");
	}
}
