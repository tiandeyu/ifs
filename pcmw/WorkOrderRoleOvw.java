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
*  File        : WorkOrderRoleOvw.java 
*  Created     : NUPELK 010227
*  Modified    :     
*  THWILK  031024  Call ID 104789 Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040723  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  SHAFLK  040713  Bug 42641, Increased size of Remark field
*  ThWilk  040806  Merged Bug 42641.
*  NIJALK  041111  Renamed field Signature to Executed By, unhide the column Employee ID & made it read only.
*  NIJALK  041129  Changed the LOV for field SIGN.
*  THWILK  050923  Added method populateQuery() and modified printContents() and run().
*  THWILK  051013  Added ALLOCATION_NO field.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile). 
 * JEWILK  060111  Corrected sOracleFormat in populateQuery().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderRoleOvw extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.equipw.WorkOrderRoleOvw");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx; 
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;
	private ASPCommand cmd;
	private String cont;
	private ASPBuffer temp;
	private String val;
	private ASPBuffer buf;
	private String txt;
	private ASPBuffer data;
	private boolean actEna1;
	private boolean actEna2;
	private boolean actEna3;
	private boolean again;
        private String originForm;
        private String sCompany;
        private String sEmpNo;
        private String sDateFrom;
        private String sDateTo;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderRoleOvw(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();

		actEna1 = ctx.readFlag("ACTENA1",false);
		actEna2 = ctx.readFlag("ACTENA2",false);
		actEna3 = ctx.readFlag("ACTENA3",false);
		again   = ctx.readFlag("AGAIN",false);
                sCompany  = ctx.readValue("COMPANY","");
                sEmpNo    = ctx.readValue("EMPNO","");
                sDateFrom = ctx.readValue("DATEFROM","");
                sDateTo   = ctx.readValue("DATETO","");


		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
                else if (!mgr.isEmpty(mgr.getQueryStringValue("RES_TYPE")))
                {
                    sCompany  = mgr.getQueryStringValue("COMPANY");   
                    sEmpNo    = mgr.getQueryStringValue("EMP_NO");   
                    sDateFrom = mgr.getQueryStringValue("DATE_FROM");   
                    sDateTo   = mgr.getQueryStringValue("DATE_TO");   
                    populateQuery();
                }
		 
                else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		checkObjAvaileble();

		adjust();

		ctx.writeFlag("ACTENA1",actEna1);
		ctx.writeFlag("ACTENA2",actEna2);
		ctx.writeFlag("ACTENA3",actEna3);
		ctx.writeFlag("AGAIN",again);
                ctx.writeValue("COMPANY",sCompany);
                ctx.writeValue("EMPNO",sEmpNo);
                ctx.writeValue("DATEFROM",sDateFrom);
                ctx.writeValue("DATETO",sDateTo);
                
	}

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

	public void  startup()
	{
		ASPManager mgr = getASPManager();

		q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");                                                                                  

		mgr.submit(trans);

		trans.clear();   

		cmd = trans.addCustomCommand("CON","User_Default_API.Get_User_Contract");
		cmd.addParameter("CONTRACT");
		trans = mgr.perform(trans);
		cont=trans.getValue("CON/DATA/CONTRACT");

		temp = headset.getRow();
		temp.setValue("CONTRACT",cont);   
		headset.setRow(temp);
	}

       public void  populateQuery()
       {
               ASPManager mgr = getASPManager();

               q = trans.addEmptyQuery(headblk);
               String sOracleFormat = "yyyy-mm-dd-hh24.MI.ss"; 

               String sWhere = "CONTRACT = IFSAPP.User_Allowed_Site_API.Authorized(CONTRACT) AND COMPANY='" + sCompany + 
                     "' AND SIGN_ID='" + sEmpNo + "' AND (DATE_FROM BETWEEN to_date('" + sDateFrom +"','"+sOracleFormat+"') AND to_date('" + sDateTo +"','"+sOracleFormat+"') OR DATE_TO BETWEEN  to_date('" + sDateFrom +"','"+sOracleFormat+"') AND to_date('" + sDateTo +"','"+sOracleFormat+"'))";

               q.addWhereCondition(sWhere);
               q.includeMeta("ALL");                                                                                  

               mgr.submit(trans);

               
       }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		val = mgr.readValue("VALIDATE");

		if ("DATE_FROM".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("DATE_FROM",mgr.readValue("DATE_FROM"));

			txt = mgr.readValue("DATE_FROM") +"^";
			mgr.responseWrite(txt);     
		}

		else if ("DATE_TO".equals(val))
		{
			buf = mgr.newASPBuffer();
			buf.addFieldItem("DATE_TO",mgr.readValue("DATE_TO"));

			txt = mgr.readValue("DATE_TO") +"^";
			mgr.responseWrite(txt);     
		}
		else if ("SIGN".equals(val))
		{
			String reqstr = null;
			int startpos = 0;
			int endpos = 0;
			int i = 0;
			String ar[] = new String[2];
			String emp_id = "";
			String sign = "";

			String new_sign = mgr.readValue("SIGN","");

			if (new_sign.indexOf("^",0)>0)
			{
				for (i=0 ; i<2; i++)
				{
					endpos = new_sign.indexOf("^",startpos);
					reqstr = new_sign.substring(startpos,endpos);
					ar[i] = reqstr;
					startpos= endpos+1;
				}
				sign = ar[0];
				emp_id = ar[1];
			}
			else
			{
                                cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
                                cmd.addParameter("CONTRACT");

				cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","SIGN_ID");                     
                                cmd.addReference("COMPANY","GETCOMP/DATA");
				cmd.addParameter("SIGN");

				trans = mgr.validate(trans);
				emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
				sign = new_sign;

				trans.clear();
			}

			txt = (mgr.isEmpty(emp_id) ? "": (emp_id))+ "^" + 
				  (mgr.isEmpty(sign)? "": (sign))+ "^";
			mgr.responseWrite(txt);
		}
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_ROLE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");

		trans = mgr.perform(trans);
                data = trans.getBuffer("HEAD/DATA");

		headset.addRow(data);
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("EQUIPWWORKORDERROLEOVWNODATA: No data found."));
			headset.clear();
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  clear()
	{

		headset.clear();
		headtbl.clearQueryRow();
	}

	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("EQUIPWWORKORDERROLEOVWNOSELECT: No RMB method has been selected."));
	}

	public void  prepare()
	{
		ASPManager mgr = getASPManager();
		ASPBuffer keys,row;


		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();

			keys=headset.getSelectedRows("WO_NO");
			mgr.transferDataTo("ActiveSeparate2.page",keys);
		}
		else
		{
			keys = mgr.newASPBuffer();
			row = keys.addBuffer("0");
			row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));  
			mgr.transferDataTo("ActiveSeparate2.page",keys);   
		}     


		if (headlay.isMultirowLayout())
		{
			headset.setFilterOff();
		}

		keys = null;
	}

	public void  reportin()
	{
		ASPManager mgr = getASPManager();
		ASPBuffer keys,row;


		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			keys=headset.getSelectedRows("WO_NO");
			mgr.transferDataTo("ActiveSeperateReportInWorkOrder.page",keys);

		}
		else
		{

			keys = mgr.newASPBuffer();
			row = keys.addBuffer("0");
			row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));  
			mgr.transferDataTo("ActiveSeperateReportInWorkOrder.page",keys);  

		}  

		if (headlay.isMultirowLayout())
		{
			headset.setFilterOff();
		}

		keys = null;
	}

	public void  structure()
	{
		ASPManager mgr = getASPManager();
		ASPBuffer keys,row;

		if (headlay.isMultirowLayout())
		{
			headset.storeSelections();
			headset.setFilterOn();
			keys=headset.getSelectedRows("WO_NO");
			mgr.transferDataTo("SeparateWorkOrder.page",keys);

		}
		else
		{
			keys = mgr.newASPBuffer();
			row = keys.addBuffer("0");
			row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));  
			mgr.transferDataTo("SeparateWorkOrder.page",keys);    

		}     

		if (headlay.isMultirowLayout())
		{
			headset.setFilterOff();
		}

		keys = null;
	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("ROLE_CODE");
		f.setSize(10);   
		f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,445);
		f.setLabel("EQUIPWWORKORDERROLEOVWROLE_CODE: Craft ID");
		f.setUpperCase();
		f.setMaxLength(10);

		f = headblk.addField("CONTRACT");
		f.setSize(10);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("EQUIPWWORKORDERROLEOVWCONTRACT: Site");
		f.setUpperCase();
		f.setMaxLength(6);

		f = headblk.addField("COMPANY");
		f.setSize(5);
		f.setHidden();
		f.setUpperCase();
		f.setMaxLength(6);

		f = headblk.addField("SIGN");
		f.setSize(10);
		f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
		f.setCustomValidation("CONTRACT,SIGN","SIGN_ID,SIGN");
		f.setLabel("EQUIPWWORKORDERROLEOVWSIGN: Executed By");
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("SIGN_ID");
		f.setSize(10);
		f.setReadOnly();
		f.setLabel("EQUIPWWORKORDERROLEOVWSIGN_ID: Employee ID");
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(10);
		f.setDynamicLOV("ACTIVE_WORK_ORDER",600,445);
		f.setMandatory();
		f.setLabel("EQUIPWWORKORDERROLEOVWWO_NO: WO No");
		f.setMaxLength(8);
		f.setReadOnly();
		f.setInsertable();

		f = headblk.addField("ROW_NO","Number");
		f.setSize(10);
		f.setMandatory();
		f.setLabel("EQUIPWWORKORDERROLEOVWROW_NO: Row No");
		f.setMaxLength(10);
		f.setReadOnly();
		f.setInsertable();

		f = headblk.addField("DESCRIPTION");
		f.setSize(55);
		f.setLabel("EQUIPWWORKORDERROLEOVWDESCRIPTION: Description");
		f.setMaxLength(40);

                f =headblk.addField("ALLOCATION_NO","Number");
                f.setLabel("PCMWWORKORDERROLEOVWALLOCNO: Allocation No");

		f = headblk.addField("ACTIVEWORKORDEROBJ_STATE");
		f.setSize(27);
		f.setLabel("EQUIPWWORKORDERROLEOVWACTIVEWORKORDEROBJ_STATE: WO Status");
		f.setFunction("ACTIVE_WORK_ORDER_API.Get_State(:WO_NO)");
		f.setMaxLength(12);
		f.setReadOnly();

		f = headblk.addField("ORG_CODE");
		f.setSize(10);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setLabel("EQUIPWWORKORDERROLEOVWORG_CODE: Maintenance Organization");
		f.setUpperCase();
		f.setMaxLength(8);

		f = headblk.addField("DATE_FROM","Datetime");
		f.setSize(27);
		f.setCustomValidation("DATE_FROM","DATE_FROM");
		f.setLabel("EQUIPWWORKORDERROLEOVWDATE_FROM: Date From");

		f = headblk.addField("DATE_TO","Datetime");
		f.setSize(27);
		f.setCustomValidation("DATE_TO","DATE_TO");
		f.setLabel("EQUIPWWORKORDERROLEOVWDATE_TO: Date To");

		f = headblk.addField("PLAN_HRS","Number");
		f.setSize(10);
		f.setLabel("EQUIPWWORKORDERROLEOVWPLAN_HRS: Planned Hours");
		f.setMaxLength(10);

		f = headblk.addField("PLAN_MEN","Number");
		f.setSize(10);
		f.setLabel("EQUIPWWORKORDERROLEOVWPLAN_MEN: Planned Men");
		f.setMaxLength(10);

		f = headblk.addField("TOOLS");
		f.setSize(27);
		f.setLabel("EQUIPWWORKORDERROLEOVWTOOLS: Tools");
		f.setMaxLength(20);

		f = headblk.addField("REMARK");
		f.setSize(50);
                f.setHeight(4);
		f.setLabel("EQUIPWWORKORDERROLEOVWREMARK: Remark");
		f.setMaxLength(2000);

		headblk.setView("WORK_ORDER_ROLE");
		headblk.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.addCustomCommand("none",   "");
		headbar.addCustomCommand("prepare",  mgr.translate("EQUIPWWORKORDERROLEOVWPRP: Prepare..."));
		headbar.addCustomCommand("reportin",  mgr.translate("EQUIPWWORKORDERROLEOVWRPI: Report In..."));
		headbar.addCustomCommand("structure",  mgr.translate("EQUIPWWORKORDERROLEOVWSTR: Structure..."));

		headbar.enableCommand(headbar.SAVERETURN); 
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");  
		headbar.enableCommand(headbar.SAVENEW);
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");
		headbar.enableCommand(headbar.REMOVE);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("EQUIPWWORKORDERROLEOVWHD: Work Orders for Craft/Employee"));
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  

		b = mgr.newASPBlock("ACTIVEWORKORDEROBJ_STATE");

		b.addField("CLIENT_VALUES0");

		b = mgr.newASPBlock("ORG_CODE");

		b.addField("CLIENT_VALUES1");
	}

	public void checkObjAvaileble()
	{

		if (!again)
		{

			ASPManager mgr = getASPManager();

			ASPBuffer availObj;
			trans.clear();
			trans.addSecurityQuery("ACTIVE_SEPARATE,SEPARATE_WORK_ORDER");
			trans.addPresentationObjectQuery("PCMW/ActiveSeparate2.page,PCMW/ActiveSeperateReportInWorkOrder.page,PCMW/SeparateWorkOrder.page");

			trans = mgr.perform(trans);

			availObj = trans.getSecurityInfo();

			if (availObj.itemExists("ACTIVE_SEPARATE") && availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
				actEna1 = true;

			if (availObj.itemExists("ACTIVE_SEPARATE") && availObj.namedItemExists("PCMW/ActiveSeperateReportInWorkOrder.page"))
				actEna2 = true;

			if (availObj.itemExists("SEPARATE_WORK_ORDER") && availObj.namedItemExists("PCMW/SeparateWorkOrder.page"))
				actEna3 = true;


			again = true;
		}
	}

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		if (!actEna1)
			headbar.removeCustomCommand("prepare");
		if (!actEna2)
			headbar.removeCustomCommand("reportin");
		if (!actEna3)
			headbar.removeCustomCommand("structure");
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "EQUIPWWORKORDERROLEOVWTITLE: Work Orders for Craft/Employee";
	}

	protected String getTitle()
	{
		return "EQUIPWWORKORDERROLEOVWTITLE: Work Orders for Craft/Employee";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());
                appendDirtyJavaScript("window.name = \"WorkOrderRoleOvw\";\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}

}
