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
*  File        : QuickReportInWorkDlg.java 
*  Created     : 010223  CHDELK  Converted to Java
*  Modified    : 
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHDELK  010417  Set Actual start / Actual Completion field lengths to 22
*  INROLK  011018  Changed Newrow(). to get Reported By. call id 70751.
*  BUNILK  021129  Added new field Object Site and replaced 
*                  work order site by Object site whenever it refer to Object.    
*                  Added new field 'Latest Transaction' for earliar 'Current Position'.   
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)   
*  VAGULK  040108  Made the field order according to the order in the Centura application(Web Alignment).
*  THWILK  040603  Added PM_REVISION key filed under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  AMNILK  061102  MTPR904: Hink tasks Req Id: 35867. Added new field WORK_TYPE_ID and changed the Type to Fault Type
*  AMDILK  070608  Called method " showBottomLine() " from preDefine()
*  CHARLK  090806  Bug 84119, change method newRow().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class QuickReportInWorkDlg extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.QuickReportInWorkDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock trns;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private int flag;
	private ASPBuffer buff;
	private ASPBuffer row;
	private String tr_pm_no;
	private String tr_pm_rev;
	private String tr_contract;
	private String tr_mch_code;
	private String tr_test_point_id;
	private String tr_test_point_descr;
	private String tr_wo_no;
	private String val;
	private ASPCommand cmd;
	private String mch;
	private String pos1;
	private String testpdesc;
	private String testploc;
	private String repById;
	private String repByName;
	private ASPQuery q;
	private ASPBuffer data;
	private String orgcd;
	private String txt;
        //Bug 84119, Start
        private String tr_wo_Contract;
        //Bug 84119, End
	//===============================================================
	// Construction 
	//===============================================================
	public QuickReportInWorkDlg(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		flag=0;

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (mgr.dataTransfered())
		{
			flag=1;
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0);
			tr_pm_no = row.getValue("PM_NO");
			row = buff.getBufferAt(1);
			tr_pm_rev= row.getValue("PM_REVISION");
			row = buff.getBufferAt(2);
                        //Bug 84119, Start
			tr_contract = row.getValue("MCH_CODE_CONTRACT");
                        //Bug 84119, End
			row = buff.getBufferAt(3);
			tr_mch_code = row.getValue("MCH_CODE"); 
			row = buff.getBufferAt(4);
			tr_test_point_id = row.getValue("TEST_POINT_ID");
			row = buff.getBufferAt(5);
			tr_test_point_descr = row.getValue("TEST_POINT_DESCR");  
			row = buff.getBufferAt(6);
			tr_wo_no = row.getValue("WO_NO");
                        //Bug 84119, Start
			row = buff.getBufferAt(7);
			tr_wo_Contract = row.getValue("CONTRACT");
                        //Bug 84119, End
			newRow();   
			headlay.setLayoutMode(headlay.NEW_LAYOUT);
		}

		adjust();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("MCH_CODE".equals(val))
		{

			String description = "";
			String sContract = mgr.readValue("MCH_CODE_CONTRACT");
			String mchCode = mgr.readValue("MCH_CODE");

			if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
			{
				String strAttr = mgr.readValue("MCH_CODE");

				mchCode = strAttr.substring(0,strAttr.indexOf("~"));       
				String validationAttrAtr = strAttr.substring(strAttr.indexOf("~")+1,strAttr.length());
				sContract =  validationAttrAtr.substring(validationAttrAtr.indexOf("~")+1,validationAttrAtr.length());                

				cmd = trans.addCustomFunction("OBJDESC","EQUIPMENT_OBJECT_API.Get_Mch_Name","MCHCODEDESCRIPTION");
				cmd.addParameter("MCH_CODE_CONTRACT",sContract);
				cmd.addParameter("MCH_CODE",mchCode);
			}
			else
			{

				if (mgr.isEmpty(sContract))
				{
					cmd = trans.addCustomFunction("OBJCONT", "EQUIPMENT_OBJECT_API.Get_Contract", "MCH_CODE_CONTRACT" );
					cmd.addParameter("MCH_CODE");
				}

				cmd = trans.addCustomFunction("OBJDESC", "EQUIPMENT_OBJECT_API.Get_Mch_Name", "MCHCODEDESCRIPTION" );

				if (mgr.isEmpty(sContract))
					cmd.addReference("MCH_CODE_CONTRACT","OBJCONT/DATA");
				else
					cmd.addParameter("MCH_CODE_CONTRACT",sContract);
				cmd.addParameter("MCH_CODE");

			}

			cmd = trans.addCustomFunction( "CURRPOS", "Part_Serial_Catalog_API.Get_Alt_Latest_Transaction", "LATEST_TRANSACTION" );
			if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
				cmd.addParameter("MCH_CODE_CONTRACT",sContract);
			else
				cmd.addReference("MCH_CODE_CONTRACT","OBJCONT/DATA");
			cmd.addParameter("MCH_CODE",mchCode);

			trans = mgr.validate(trans);

			mch = trans.getValue("OBJDESC/DATA/MCHCODEDESCRIPTION");
			pos1 = trans.getValue("CURRPOS/DATA/LATEST_TRANSACTION");

			if (mgr.readValue("MCH_CODE").indexOf("~") == -1 && mgr.isEmpty(sContract))
				sContract = trans.getValue("OBJCONT/DATA/MCH_CODE_CONTRACT");

			txt = ((mgr.isEmpty(mch))?"":mch) +"^"+
				  ((mgr.isEmpty(pos1))?"":pos1) +"^"+
				  ((mgr.isEmpty(sContract))?"":sContract) +"^"+
				  ((mgr.isEmpty(mchCode))?"":mchCode) +"^";

			mgr.responseWrite(txt);
		}
		else if ("TEST_POINT_ID".equals(val))
		{
			cmd = trans.addCustomFunction("TPDESC", "EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description", "TESTPOINTDESCRIPTION" );
			cmd.addParameter("MCH_CODE_CONTRACT");
			cmd.addParameter("MCH_CODE");
			cmd.addParameter("TEST_POINT_ID");

			cmd = trans.addCustomFunction( "TPLOC", "EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location", "TESTPOINTLOCATION" );
			cmd.addParameter("MCH_CODE_CONTRACT");
			cmd.addParameter("MCH_CODE");
			cmd.addParameter("TEST_POINT_ID");

			trans = mgr.validate(trans);
			testpdesc = trans.getValue("TPDESC/DATA/TESTPOINTDESCRIPTION");
			testploc = trans.getValue("TPLOC/DATA/TESTPOINTLOCATION");

			txt = ((mgr.isEmpty(testpdesc))?"":testpdesc)+"^"+
				  ((mgr.isEmpty(testploc))?"":testploc) +"^";

			mgr.responseWrite(txt);
		}
		else if ("REPORTED_BY".equals(val))
		{
			cmd = trans.addCustomFunction("GETID", "COMPANY_EMP_API.Get_Max_Employee_Id", "REPORTED_BY_ID" );
			cmd.addParameter("COMPANY");
			cmd.addParameter("REPORTED_BY");

			cmd = trans.addCustomFunction("GETNAME", "PERSON_INFO_API.Get_Name", "NAME" );
			cmd.addParameter("REPORTED_BY");

			trans = mgr.validate(trans);
			repById = trans.getValue("GETID/DATA/REPORTED_BY_ID");
			repByName = trans.getValue("GETNAME/DATA/NAME");

			txt = ((mgr.isEmpty(repById))?"":repById) +"^"+
				  ((mgr.isEmpty(repByName))?"":repByName) +"^";

			mgr.responseWrite(txt);
		}
		else if ("ORG_CODE".equals(val))
		{
			cmd = trans.addCustomFunction("GETDESC", "Organization_API.Get_Description", "ORG_CODE_DESC" );
			cmd.addParameter("CONTRACT");
			cmd.addParameter("ORG_CODE");

			trans = mgr.validate(trans);
			String sOrgDesc = trans.getValue("GETDESC/DATA/ORG_CODE_DESC");

			txt = ((mgr.isEmpty(repById))?"":repById) +"^";

			mgr.responseWrite(txt);
		}
		mgr.endResponse();
	}

/////----------------------------------------------------------------------------
/////----------------------- Command Bar Functions ------------------------------
//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWQUICKREPORTINWORKDLGNODATA: No data found."));
			headset.clear();
		}
		else
		{
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}   
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","HISTORICAL_SEPARATE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");

		cmd = trans.addCustomFunction("GETCOMP2","Site_API.Get_Company","COMPANY");
		cmd.addReference("CONTRACT","HEAD/DATA");

		cmd = trans.addQuery("REPBY","SELECT Fnd_Session_API.Get_Fnd_User FROM DUAL");

		cmd = trans.addCustomFunction("PERSONINFO", "Person_Info_API.Get_Id_For_User", "REPORTED_BY" );
		cmd.addReference("GET_FND_USER","REPBY/DATA");

		cmd = trans.addCustomFunction("GETID", "COMPANY_EMP_API.Get_Max_Employee_Id", "REPORTED_BY_ID" );
		cmd.addReference("COMPANY","GETCOMP2/DATA");
		cmd.addReference("REPORTED_BY","PERSONINFO/DATA");

		cmd = trans.addCustomFunction("GETNAME", "PERSON_INFO_API.Get_Name", "NAME" );
		cmd.addReference("REPORTED_BY","PERSONINFO/DATA");


		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");

		String repby = trans.getValue("PERSONINFO/DATA/REPORTED_BY");
		String repById = trans.getValue("GETID/DATA/REPORTED_BY_ID");
		String repByName = trans.getValue("GETNAME/DATA/NAME");

		data.setValue("REPORTED_BY",repby);
		data.setValue("REPORTED_BY_ID",repById);
		data.setValue("NAME",repByName);

		if (flag>0)
		{
			trans.clear();
			cmd = trans.addCustomFunction("GTORGCD","Active_Work_Order_API.Get_Org_Code","ORG_CODE");
			cmd.addParameter("TR_WO_NO",tr_wo_no);
			trans = mgr.perform(trans);
			orgcd = trans.getValue("GTORGCD/DATA/ORG_CODE");
			data.setValue("ORG_CODE",orgcd);

			trans.clear();
                        //Bug 84119, Start
                        data.setValue("CONTRACT",tr_wo_Contract);
                        //Bug 84119, End
			data.setValue("PM_NO",tr_pm_no);
			data.setValue("PM_REVISION",tr_pm_rev);
                        //Bug 84119, Start
			data.setValue("MCH_CODE_CONTRACT",tr_contract);
                        //Bug 84119, End
			data.setValue("MCH_CODE",tr_mch_code);     
			data.setValue("TEST_POINT_ID",tr_test_point_id);
			data.setValue("TEST_POINT_DESCR",tr_test_point_descr);

		}
		headset.addRow(data); 
	}

	public void cancelNew()
	{
		ASPManager mgr = getASPManager();

		mgr.redirectTo("../pcmw/QuickReportInWorkDlg.page");
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("MCH_CODE");
		f.setSize(20);
		f.setMandatory();
		f.setLabel("PCMWQUICKREPORTINWORKDLGMCHCODE: Object ID");
		f.setLOV("MaintenanceObjectLov1.page","MCH_CODE_CONTRACT CONTRACT",600,450);
		f.setUpperCase();
		f.setMaxLength(100);
		f.setCustomValidation("MCH_CODE_CONTRACT, MCH_CODE", "MCHCODEDESCRIPTION, LATEST_TRANSACTION, MCH_CODE_CONTRACT,MCH_CODE");

		f = headblk.addField("MCHCODEDESCRIPTION");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGMCHCODEDESC: Object Description");
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
		f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGWOCONTRACT: WO Site");
		f.setUpperCase();
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setMandatory();

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGCONTRACT: Site");
		f.setUpperCase();
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setMandatory();

		f = headblk.addField("TEST_POINT_ID");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGTESTPOINTID: Testpoint");
		f.setUpperCase();
		f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","MCH_CODE_CONTRACT,MCH_CODE",600,450);
		f.setCustomValidation("MCH_CODE_CONTRACT, MCH_CODE, TEST_POINT_ID", "TESTPOINTDESCRIPTION, TESTPOINTLOCATION");

		f = headblk.addField("TESTPOINTDESCRIPTION");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGTESTPNTDESC: Testpoint Description");
		f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
		f.setReadOnly();

		f = headblk.addField("TESTPOINTLOCATION");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGTESTPNTLOC: Testpoint Location");   
		f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
		f.setReadOnly();

		f = headblk.addField("COMPANY");
		f.setHidden();
		f.setUpperCase();

		f = headblk.addField("LATEST_TRANSACTION");
		f.setSize(50);
		f.setLabel("PCMWQUICKREPORTINWORKDLGLATESTTRANSACTION: Latest Transaction");
		f.setFunction("Part_Serial_Catalog_API.Get_Alt_Latest_Transaction( :MCH_CODE_CONTRACT, :MCH_CODE)");
		f.setReadOnly();

		f = headblk.addField("REPORTED_BY");
		f.setLabel("PCMWQUICKREPORTINWORKDLGREPORTEDBY: Reported by");
		f.setMandatory();
		f.setSize(20);
		f.setUpperCase();
		f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
		f.setCustomValidation("COMPANY,REPORTED_BY", "REPORTED_BY_ID,NAME");   

		f = headblk.addField("REPORTED_BY_ID");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGREPORTEDBYID: Reported by ID");
		f.setUpperCase();
		f.setHidden();

		f = headblk.addField("NAME");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGNAME: Name");
		f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");
		f.setReadOnly();

		f = headblk.addField("ORG_CODE");
		f.setSize(20);
		f.setMandatory();
		f.setLabel("PCMWQUICKREPORTINWORKDLGORGCODE: Maintenance Organization");
		f.setUpperCase();
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
		f.setCustomValidation("CONTRACT,ORG_CODE","ORG_CODE_DESC");

		f = headblk.addField("ORG_CODE_DESC");
		f.setSize(40);
		f.setReadOnly();
		f.setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)");

		f = headblk.addField("ERR_SYMPTOM");
		f.setSize(20);

		f.setLabel("PCMWQUICKREPORTINWORKDLGERRSYMPTOM: Symptom");
		f.setUpperCase();
		f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);

		f = headblk.addField("SYMPTOMDESCRIPTION");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGSYMPTDESC: Symptom Description");   
		f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");
		mgr.getASPField("ERR_SYMPTOM").setValidation("SYMPTOMDESCRIPTION");
		f.setReadOnly();

		f = headblk.addField("ERR_DISCOVER_CODE");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRDISCOVERCODE: Discovery         ");
		f.setUpperCase();
		f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);

		f = headblk.addField("DISCOVERDESCRIPTION");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRDISCDESC: Discovery Description");   
		f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");
		mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCOVERDESCRIPTION");
		f.setReadOnly();

		f = headblk.addField("ERR_DESCR");
		f.setSize(50);
		f.setMaxLength(60);
		f.setMandatory();
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRDESCR: Directive");

		f = headblk.addField("ERR_CLASS");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRCLASS: Class");
		f.setUpperCase();
		f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,450);

		f = headblk.addField("ERRCLASSDESCR");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGCLASSDESC: Class Description");   
		f.setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(:ERR_CLASS)");
		mgr.getASPField("ERR_CLASS").setValidation("ERRCLASSDESCR");
		f.setReadOnly();

		f = headblk.addField("ERR_TYPE");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRTYPE: Fault Type");
		f.setUpperCase();
		f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,450);

		f = headblk.addField("ERRTYPEDESC");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRDESC: Type Description");   
		f.setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(:ERR_TYPE)");
		mgr.getASPField("ERR_TYPE").setValidation("ERRTYPEDESC");
		f.setReadOnly();

		f = headblk.addField("PERFORMED_ACTION_ID");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGPERFORMEDACTIONID: Performed Action");
		f.setUpperCase();
		f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,450);

		f = headblk.addField("PERFORMEDACTIONDESC");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGPERFACTDESC: Performed Action Description");   
		f.setFunction("MAINTENANCE_PERF_ACTION_API.Get_Description(:PERFORMED_ACTION_ID)");
		mgr.getASPField("PERFORMED_ACTION_ID").setValidation("PERFORMEDACTIONDESC");
		f.setReadOnly();

		f = headblk.addField("ERR_CAUSE");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGERRCAUSE: Cause");
		f.setUpperCase();
		f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,450);

		f = headblk.addField("ERRCAUSEDESC");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGCAUSEDESC: Cause Description");   
		f.setFunction("MAINTENANCE_CAUSE_CODE_API.Get_Description(:ERR_CAUSE)");
		mgr.getASPField("ERR_CAUSE").setValidation("ERRCAUSEDESC");
		f.setReadOnly();

		f = headblk.addField("WORK_DONE");
		f.setSize(60);
		f.setMaxLength(60);
		f.setLabel("PCMWQUICKREPORTINWORKDLGWORKDONE: Work Done");

		f = headblk.addField("WORK_TYPE_ID");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGWORKTYPE: Work Type");
		f.setUpperCase();
		f.setDynamicLOV("WORK_TYPE",600,450);
		f.setCustomValidation("WORK_TYPE_ID", "WORKTYPEDESC");
                
		f = headblk.addField("WORKTYPEDESC");
		f.setSize(20);
		f.setLabel("PCMWQUICKREPORTINWORKDLGWORKTYPEDESC: Work Type Description");
		f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
                mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESC");
		f.setReadOnly();

		f = headblk.addField("PERFORMED_ACTION_LO");
		f.setSize(60);
		f.setHeight(5);
		f.setMaxLength(2000);
		f.setLabel(" ");

		f = headblk.addField("REAL_S_DATE","Datetime");
		f.setSize(22);
		f.setLabel("PCMWQUICKREPORTINWORKDLGREALSDATE: Actual Start");

		f = headblk.addField("REAL_F_DATE","Datetime");
		f.setSize(22);
		f.setMandatory();
		f.setLabel("PCMWQUICKREPORTINWORKDLGREALFDATE: Actual Completion");

		f = headblk.addField("PM_NO","Number");
		f.setHidden();

		f = headblk.addField("PM_REVISION");
		f.setHidden();
		

		f = headblk.addField("REG_DATE","Datetime");
		f.setHidden();

		f = headblk.addField("WO_NO","Number");
		f.setHidden();

		f = headblk.addField("PM_TYPE");
		f.setHidden();

		f = headblk.addField("GET_FND_USER");
		f.setHidden();
		f.setFunction("''");

		trns = mgr.newASPBlock("TRNS");
		f = trns.addField("TR_WO_NO");
		f.setFunction("''");
		f.setHidden();

		headblk.setView("HISTORICAL_SEPARATE");
		headblk.defineCommand("HISTORICAL_SEPARATE_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();
		headtbl = mgr.newASPTable(headblk);
		headtbl.setWrap();
		headbar = mgr.newASPCommandBar(headblk);
		headlay = headblk.getASPBlockLayout();

		//headbar.defineCommand(headbar.CANCELNEW,"cancelNew");
		headbar.defineCommand(headbar.SAVERETURN,null,"checkAllFields()");   
		headbar.defineCommand(headbar.SAVENEW,null,"checkAllFields()");

		headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.defineGroup(mgr.translate("PCMWQUICKREPORTINWORKDLGGRPLABEL1: Object"),"MCH_CODE,MCHCODEDESCRIPTION,MCH_CODE_CONTRACT,TEST_POINT_ID,TESTPOINTDESCRIPTION,TESTPOINTLOCATION,LATEST_TRANSACTION",true,true);
		headlay.defineGroup(mgr.translate("PCMWQUICKREPORTINWORKDLGGRPLABEL2: By"),"REPORTED_BY,NAME,ORG_CODE,ORG_CODE_DESC,CONTRACT",true,true);
		headlay.defineGroup(mgr.translate("PCMWQUICKREPORTINWORKDLGGRPLABEL3: Fault Information"),"ERR_SYMPTOM,SYMPTOMDESCRIPTION,ERR_DISCOVER_CODE,DISCOVERDESCRIPTION,ERR_DESCR",true,true);
		headlay.defineGroup(mgr.translate("PCMWQUICKREPORTINWORKDLGGRPLABEL4: Classification/Performed Work"),"ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,WORK_DONE,WORK_TYPE_ID,WORKTYPEDESC,PERFORMED_ACTION_LO,REAL_S_DATE,REAL_F_DATE",true,true);

		headlay.setSimple("SYMPTOMDESCRIPTION");
		headlay.setSimple("DISCOVERDESCRIPTION");
		headlay.setSimple("ERRCLASSDESCR");
		headlay.setSimple("ERRTYPEDESC");
		headlay.setSimple("PERFORMEDACTIONDESC");
		headlay.setSimple("ERRCAUSEDESC");
		headlay.setSimple("ORG_CODE_DESC");
		headlay.setSimple("NAME");
		headlay.setSimple("MCHCODEDESCRIPTION");
		headlay.setSimple("WORKTYPEDESC");
		headlay.setFieldSpan("REPORTED_BY",1,3);
		headlay.setFieldSpan("PERFORMED_ACTION_LO",1,3);

		headlay.showBottomLine(false);
	}

	public void adjust()
	{
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.BACK);

		if (headset.countRows() == 0)
		{
			headbar.disableCommand(headbar.DUPLICATEROW);
			headbar.disableCommand(headbar.CANCELNEW);
		}
		else
		{
			headbar.enableCommand(headbar.SAVENEW);
			headbar.enableCommand(headbar.SAVERETURN);      
		}   
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWQUICKREPORTINWORKDLGQCKRPTWRK: Quick Report in Work";
	}

	protected String getTitle()
	{
		return "PCMWQUICKREPORTINWORKDLGQCKRPTWRK: Quick Report in Work";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("function checkAllFields(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   return checkHeadFields(i);\n");
		appendDirtyJavaScript("}\n");
	}
}
