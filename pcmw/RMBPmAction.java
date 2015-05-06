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
*  File        : RMBPmAction.java 
*  Created     : SHFELK  010220  Created.
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  SHCHLK  011016  Did the security check.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
*  THWILK  040601  Incorporated the changes relating to the key change under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040607  Added lov's for CONNECTED_PM_REV and the javascript method validateConnectedPmNo under IID AMEC109A, Multiple Standard Jobs on PMs.
*  NIJALK  041115  Modified LOV for CONNECTED_PM_NO. Added field PM_STATE. Modified adjust(). 
*  NIJALK  050809  Bug 126195, Modified LOV Properties for CONNECTED_PM_NO.
*  AMNILK  060727  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  AMDILK  070801  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080303  Bug 71670,Modified preDefine().
*  SHAFLK  090922  Bug 86045, Modified okFind() and okFindITEM0().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class RMBPmAction extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.RMBPmAction");

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
	private ASPBlock itemblk0;
	private ASPRowSet itemset0;
	private ASPCommandBar itembar0;
	private ASPTable itemtbl0;
	private ASPBlock tempblk1;
	private ASPBlockLayout itemlay0;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private String attr;
	private String attr_rev;
	private ASPTransactionBuffer trans;
	private String calling_url;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;
	private int currrow;
	private int n;
	private ASPBuffer SecBuff;
	private boolean varSec;  
	private boolean secGen;
	private boolean secPmAct;
	private boolean secNextPre; 

	//===============================================================
	// Construction 
	//===============================================================
	public RMBPmAction(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		attr = "";
		attr_rev="";
		ASPManager mgr = getASPManager();


		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		attr= ctx.readValue("ATTRIBUTE",attr);
		attr_rev = ctx.readValue("ATTRIBUTEREV",attr_rev);
		calling_url=ctx.getGlobal("CALLING_URL");
		fmt = mgr.newASPHTMLFormatter();

		secNextPre = ctx.readFlag("SECNEXTPRE",false);
		secPmAct = ctx.readFlag("SECPMACT",false);
		secGen = ctx.readFlag("SECGEN",false);
		varSec = ctx.readFlag("VARSEC",false); 

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());

		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();

		else if (mgr.buttonPressed("BACKTOPR"))
			back();

		else if (mgr.dataTransfered())
			okFind();

		checkSecurity();
		adjust();

		ctx.writeValue("ATTRIBUTE",attr);
		ctx.writeValue("ATTRIBUTEREV",attr_rev);

		ctx.writeFlag("SECNEXTPRE",secNextPre);
		ctx.writeFlag("SECPMACT",secPmAct);
		ctx.writeFlag("SECGEN",secGen);
		ctx.writeFlag("VARSEC",varSec);   
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();
		String val = null;
		String strMachCode = null;
		String strMechName = null;
		String strContract = null;
		String strActionCode = null;
		String strActionDesc = null;
		String txt = null;
		String strPmNo="";
		String strPmRev="";

		val      = mgr.readValue("VALIDATE");

		if ("CONNECTED_PM_NO".equals(val))
		{
			String validationAttrAtr1 = "";
			trans.clear();
			validationAttrAtr1 = mgr.readValue("CONNECTED_PM_NO");
			if (mgr.readValue("CONNECTED_PM_NO").indexOf("~") > -1)
			{
				String [] attrarr =  validationAttrAtr1.split("~");
				strPmNo  = attrarr[0];
				strPmRev = attrarr[1]; 
			}
			else
			{
				strPmNo = mgr.readValue("CONNECTED_PM_NO");
				strPmRev = mgr.readValue("CONNECTED_PM_REV");
			}

			cmd = trans.addCustomFunction("PPGD1", "Pm_Action_API.Get_Mch_Code", "MCHCODE" );
			cmd.addParameter("CONNECTED_PM_NO",strPmNo);
			cmd.addParameter("CONNECTED_PM_REV",strPmRev);

			cmd = trans.addCustomFunction("PPGD2", "Pm_Action_API.Get_Mch_Name", "MCHNAME" );
			cmd.addParameter("CONNECTED_PM_NO",strPmNo);
			cmd.addParameter("CONNECTED_PM_REV",strPmRev);

			cmd = trans.addCustomFunction("PPGD3", "PM_ACTION_API.Get_Contract", "ITEM0_CONTRACT" );
			cmd.addParameter("CONNECTED_PM_NO",strPmNo);
			cmd.addParameter("CONNECTED_PM_REV",strPmRev);

			cmd = trans.addCustomFunction("PPGD4", "Pm_Action_API.Get_Action_Code_Id", "ACTIONCODE" );
			cmd.addParameter("CONNECTED_PM_NO",strPmNo);
			cmd.addParameter("CONNECTED_PM_REV",strPmRev);

			cmd = trans.addCustomFunction("PPGD5", "Pm_Action_API.Get_Action_Descr", "ACTIONDESCRIPTION" );
			cmd.addParameter("CONNECTED_PM_NO",strPmNo);
			cmd.addParameter("CONNECTED_PM_REV",strPmRev);

			trans = mgr.validate(trans);

			strMachCode = trans.getValue("PPGD1/DATA/MCHCODE");
			strMechName = trans.getValue("PPGD2/DATA/MCHNAME");
			strContract = trans.getValue("PPGD3/DATA/ITEM0_CONTRACT");
			strActionCode = trans.getValue("PPGD4/DATA/ACTIONCODE");
			strActionDesc = trans.getValue("PPGD5/DATA/ACTIONDESCRIPTION");

			txt=(mgr.isEmpty(strPmNo)? "":strPmNo)+ "^" + (mgr.isEmpty(strPmRev)? "":strPmRev)+ "^" + (mgr.isEmpty(strMachCode )? "":strMachCode )+ "^" + (mgr.isEmpty(strMechName )? "":strMechName )+ "^" + (mgr.isEmpty(strContract )? "":strContract )+ "^" + (mgr.isEmpty(strActionCode )? "":strActionCode ) + "^" + (mgr.isEmpty(strActionDesc )? "":strActionDesc ) + "^"  ;
			mgr.responseWrite( txt );

			mgr.endResponse();

		}


		if ("CONNECTED_PM_REV".equals(val))
		{
			strPmNo  = "";
			strPmRev = "";
			String validationAttrAtr1 = mgr.readValue("CONNECTED_PM_REV");
			trans.clear();

			if (mgr.readValue("CONNECTED_PM_REV").indexOf("~") > -1)
			{
				String [] attrarr =  validationAttrAtr1.split("~");
				strPmNo  = attrarr[0];
				strPmRev = attrarr[1]; 
			}
			else
			{
				strPmNo = mgr.readValue("CONNECTED_PM_NO");
				strPmRev = mgr.readValue("CONNECTED_PM_REV");
			}

			if (!mgr.isEmpty(strPmNo) && !mgr.isEmpty(strPmRev))
			{
				cmd = trans.addCustomFunction("PPGD1", "Pm_Action_API.Get_Mch_Code", "MCHCODE" );
				cmd.addParameter("CONNECTED_PM_NO",strPmNo);
				cmd.addParameter("CONNECTED_PM_REV",strPmRev);

				cmd = trans.addCustomFunction("PPGD2", "Pm_Action_API.Get_Mch_Name", "MCHNAME" );
				cmd.addParameter("CONNECTED_PM_NO",strPmNo);
				cmd.addParameter("CONNECTED_PM_REV",strPmRev);

				cmd = trans.addCustomFunction("PPGD3", "PM_ACTION_API.Get_Contract", "ITEM0_CONTRACT" );
				cmd.addParameter("CONNECTED_PM_NO",strPmNo);
				cmd.addParameter("CONNECTED_PM_REV",strPmRev);

				cmd = trans.addCustomFunction("PPGD4", "Pm_Action_API.Get_Action_Code_Id", "ACTIONCODE" );
				cmd.addParameter("CONNECTED_PM_NO",strPmNo);
				cmd.addParameter("CONNECTED_PM_REV",strPmRev);

				cmd = trans.addCustomFunction("PPGD5", "Pm_Action_API.Get_Action_Descr", "ACTIONDESCRIPTION" );
				cmd.addParameter("CONNECTED_PM_NO",strPmNo);
				cmd.addParameter("CONNECTED_PM_REV",strPmRev);

				trans = mgr.validate(trans);

				strMachCode = trans.getValue("PPGD1/DATA/MCHCODE");
				strMechName = trans.getValue("PPGD2/DATA/MCHNAME");
				strContract = trans.getValue("PPGD3/DATA/ITEM0_CONTRACT");
				strActionCode = trans.getValue("PPGD4/DATA/ACTIONCODE");
				strActionDesc = trans.getValue("PPGD5/DATA/ACTIONDESCRIPTION");

				txt=(mgr.isEmpty(strPmNo)? "":strPmNo)+ "^" + (mgr.isEmpty(strPmRev)? "":strPmRev)+ "^" + (mgr.isEmpty(strMachCode )? "":strMachCode )+ "^" + (mgr.isEmpty(strMechName )? "":strMechName )+ "^" + (mgr.isEmpty(strContract )? "":strContract )+ "^" + (mgr.isEmpty(strActionCode )? "":strActionCode ) + "^" + (mgr.isEmpty(strActionDesc )? "":strActionDesc ) + "^"  ;
				mgr.responseWrite( txt );

				mgr.endResponse();
			}
		}
	}

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  newRow()
	{
		ASPManager mgr = getASPManager();

		itemset0.clear();
		itemtbl0.clearQueryRow();
		cmd = trans.addEmptyCommand("HEAD","PM_ACTION_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);

	}

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void  newRowITEM0()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addEmptyCommand("ITEM0","PM_ACTION_CONNECTION_API.New__",itemblk0);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM0/DATA");
		data.setFieldItem("ITEM0_PM_NO",headset.getValue("PM_NO"));
		data.setFieldItem("ITEM0_PM_REVISION",headset.getValue("PM_REVISION"));
		itemset0.addRow(data);

	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}


	public void  countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk0);
		//Bug 58216 Start
		q.addWhereCondition("PM_NO = ?");
		q.addWhereCondition("PM_REVISION = ?");
		q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
		q.addParameter("PM_REVISION",headset.getRow().getValue("PM_NO"));
		//Bug 58216 End

		q.setSelectList("to_char(count(*)) N");
		currrow  = headset.getCurrentRowNo();

		mgr.submit(trans);

		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
		itemset0.clear();

		headset.goTo(currrow);

	}


	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");

		q.includeMeta("ALL");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());


		mgr.querySubmit(trans, headblk);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWRMBPMACTIONNODATAFOUND: No data found."));
			headset.clear();
		}

                if (headset.countRows() > 0)
                    okFindITEM0();

		headlay.setLayoutMode(headlay.SINGLE_LAYOUT);      
		trans.clear();


	}

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

	public void  okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
                q = trans.addQuery(itemblk0);
		//Bug 58216 Start
		q.addWhereCondition("PM_NO = ?");
		q.addWhereCondition("PM_REVISION = ?");
		q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
		q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
		//Bug 58216 End
		q.includeMeta("ALL");
		currrow = headset.getCurrentRowNo();
		mgr.querySubmit(trans, itemblk0);
		headset.goTo(currrow);     
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  none()
	{
		ASPManager mgr = getASPManager();

		mgr.showAlert(mgr.translate("PCMWRMBPMACTIONNONE: No RMB method has been selected."));

	}

	public void  prepareing()
	{
		ASPManager mgr = getASPManager();

		calling_url=mgr.getURL();
		ctx.setGlobal("CALLING_URL",calling_url);

		if (itemlay0.isMultirowLayout())
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
		}

		String pm_no = mgr.URLEncode(itemset0.getRow().getValue("CONNECTED_PM_NO"));
		String pm_rev = mgr.URLEncode(itemset0.getRow().getValue("CONNECTED_PM_REV"));

		mgr.redirectTo("PmAction.page?PM_NO="+pm_no+"&PM_REVISION="+pm_rev+"");            
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


	public void  getSearch()
	{
		ASPManager mgr = getASPManager();
		String attr1 = null;
		String attr_con_rev = null;

		itemset0.setFilterOn();
		itemset0.first();

		attr1        =itemset0.getRow().getValue("CONNECTED_PM_NO");
		attr_con_rev =itemset0.getRow().getValue("CONNECTED_PM_REV");
		attr         =headset.getRow().getValue("PM_NO");
		attr_rev     =headset.getRow().getValue("PM_REVISION");

		headset.clear();
		itemset0.clear();  
		trans.clear();

		q = trans.addEmptyQuery(headblk);

		q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");
		//Bug 58216 Start
		q.addWhereCondition(("PM_NO = ? AND PM_REVISION = ?")+ " OR " + ("PM_NO = ? AND PM_REVISION = ?"));
		q.addParameter("PM_NO",attr);
		q.addParameter("PM_REVISION",attr_rev);
		q.addParameter("PM_NO",attr1);
		q.addParameter("PM_REVISION",attr_con_rev);
		//Bug 58216 End

		q.includeMeta("ALL");

		q = trans.addQuery(itemblk0);
		q.addMasterConnection("HEAD","PM_NO","ITEM0_PM_NO");
		q.addMasterConnection("HEAD","PM_REVISION","ITEM0_PM_REVISION");
		q.setOrderByClause("CONNECTED_PM_NO,CONNECTED_PM_REV");

		q.includeMeta("ALL");      
		mgr.submit(trans);

	}


	public void  getPreviousSearch()
	{
		ASPManager mgr = getASPManager();

		headset.clear();
		itemset0.clear();  
		trans.clear();
		q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");
		//Bug 58216 Start
		q.addWhereCondition("PM_NO = ?");
		q.addWhereCondition("PM_REVISION = ?");
		q.addParameter("PM_NO",attr);
		q.addParameter("PM_REVISION",attr_rev);
		//Bug 58216 End

		q.includeMeta("ALL");

		q = trans.addQuery(itemblk0);
		q.addMasterConnection("HEAD","PM_NO","ITEM0_PM_NO");
		q.addMasterConnection("HEAD","PM_REVISION","ITEM0_PM_REVISION");
		q.setOrderByClause("CONNECTED_PM_NO,CONNECTED_PM_REV");


		q.includeMeta("ALL");   
		mgr.submit(trans);
	}


	public void  generateWorkOrder()
	{
		ASPManager mgr = getASPManager();
		ASPQuery hgenqry = null;
		ASPQuery hsyspsd = null;
		String hpmno = null;
		String hpmrev = null;
		String hplan_hours = null;
		ASPQuery syspfd = null;
		String gentdWoNo = null;
		ASPTransactionBuffer secBuff = null;
		String secAvailable = "false";


		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());


		secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Pm_Action_API","Generate__");

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists("Pm_Action_API.Generate__"))
			secAvailable = "true";

		if ("true".equals(secAvailable) && (isGeneratable()) && (!isConnected()))
		{
			trans.clear();

			hpmno = headset.getRow().getValue("PM_NO"); 
			hpmrev = headset.getRow().getValue("PM_REVISION"); 

			//Bug 58216 Start
			q = trans.addQuery("PLANHRS","SELECT PLAN_HRS HOUR FROM PM_ACTION WHERE PM_NO = ? AND PM_REVISION = ?");
			q.addParameter("PM_NO",hpmno);
			q.addParameter("PM_REVISION",hpmrev);
			//Bug 58216 End
			trans = mgr.perform(trans);

			hplan_hours = trans.getValue("PLANHRS/DATA/HOUR");

			trans.clear();

			if (mgr.isEmpty(hplan_hours))
				syspfd=trans.addQuery("HPFD","select sysdate + 168/24 PFDATE from dual");
			else{
				//Bug 58216 Start
				syspfd=trans.addQuery("HPFD","select distinct sysdate + PLAN_HRS/24 PFDATE from PM_ACTION where PM_NO = ? AND PM_REVISION = ?");
				syspfd.addParameter("PM_NO",hpmno);
				syspfd.addParameter("PM_REVISION",hpmrev);
				//Bug 58216 End
			}


			cmd = trans.addCustomFunction("HGNT","Generation_Type_API.Decode","GNTYPE");
			cmd.addParameter("DB_VALUE","4");

			hsyspsd=trans.addQuery("HPSD","select sysdate PSDATE from dual");

			cmd = trans.addCustomCommand("HGNID", "Pm_Generation_API.Create_Gen");
			cmd.addParameter("GEN_ID1","");
			cmd.addReference("GNTYPE","HGNT/DATA");
			cmd.addParameter("SNULL","");

			cmd = trans.addCustomCommand("GENE3", "Pm_Action_API.Generate__");
			cmd.addParameter("WO_NO1","");
			cmd.addParameter("SNULL","");
			cmd.addParameter("PM_NO",hpmno);
			cmd.addParameter("PM_REVISION",hpmrev);
			cmd.addReference("PSDATE","HPSD/DATA"); 
			cmd.addReference("PFDATE","HPFD/DATA");
			cmd.addParameter("HSEQ_NO","0");
			cmd.addReference("GEN_ID1","HGNID/DATA");

			trans=mgr.perform(trans);

			gentdWoNo = trans.getValue("GENE3/DATA/WO_NO1");

			mgr.showAlert(mgr.translate("PCMWPMACTIONWRKORDR: Work Order No. ")+gentdWoNo+mgr.translate("PCMWPMACTIONSUCCESSGENTD:  successfully generated."));        
		}
		else
			mgr.showAlert(mgr.translate("PCMWPMACTIONNOTGENERATABLE: This is not a Generatable Record."));
	}


	public void  nextLevel()
	{

		itemset0.storeSelections();  
		getSearch();  

	}


	public void  headNextLevel()
	{
		ASPManager mgr = getASPManager();

		if (itemset0.countRows() == 0)
			mgr.showAlert(mgr.translate("PCMWRMBPMACTIONROW: No Next Level for this Record."));
		else
		{
			itemset0.storeSelections();
			n = itemset0.countSelectedRows();

			getSearch();
		}
	}


	public void  previousLevel()
	{
		ASPManager mgr = getASPManager();


		if (itemset0.countRows() == 0)
		{
			getPreviousSearch();
		}
		else
			mgr.showAlert(mgr.translate("PCMWRMBPMACTIONROW1: No Previous Level."));

	}


	public void  headPreviousLevel()
	{
		ASPManager mgr = getASPManager();
		if (itemset0.countRows() == 0)
		{
			getPreviousSearch();
		}
		else
			mgr.showAlert(mgr.translate("PCMWRMBPMACTIONROW1: No Previous Level."));

	}

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

	public void  back()
	{
		ASPManager mgr = getASPManager();
		String strPm_No  = null;
		String strPm_Rev = null;

		strPm_No = headset.getRow().getValue("PM_NO");
		strPm_Rev = headset.getRow().getValue("PM_REVISION");
		mgr.redirectTo(calling_url+"?PM_NO="+mgr.URLEncode(strPm_No)+"PM_REVISION="+mgr.URLEncode(strPm_Rev));
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		mgr.beginASPEvent();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("PM_NO","Number", "#");
		f.setSize(8);
		f.setMandatory();
		f.setLabel("PCMWRMBPMACTIONPMNO: PM No");
		f.setReadOnly();

		f = headblk.addField("PM_REVISION");
		f.setSize(10);
		f.setMandatory();
		f.setLabel("PCMWRMBPMACTIONPMREV: PM Revision");
		f.setReadOnly();

		f = headblk.addField("MCH_CODE");
		f.setSize(13);
		f.setMaxLength(100);
		f.setLabel("PCMWRMBPMACTIONMCHCODE: Object ID");
		f.setUpperCase();
		f.setReadOnly();


		f = headblk.addField("MCH_NAME");
		f.setSize(23);
		f.setLabel("PCMWRMBPMACTIONMCHNAME: Object Description");
		f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setSize(9);
		f.setLabel("PCMWRMBPMACTIONCONTRACT: Site");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ACTION_CODE_ID");
		f.setSize(8);
		f.setLabel("PCMWRMBPMACTIONACTIONCODEID: Action");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ACTION_DESCR");
		f.setSize(37);
		f.setLabel("PCMWRMBPMACTIONACTIONDESCR: Action Description");
		f.setReadOnly();

		f = headblk.addField("CONNECTEDPMNO","Number");
		f.setSize(9);
		f.setLabel("PCMWRMBPMACTIONCONNECTEDTOPMNO: Connected to PM No");
		f.setFunction("Pm_Action_Connection_API.Get_Parent(:PM_NO,:PM_REVISION)");
		mgr.getASPField("PM_NO").setValidation("CONNECTEDPMNO");
		f.setReadOnly();

		f = headblk.addField("CONNECTEDPMREVISION");
		f.setSize(10);
		f.setLabel("PCMWRMBPMACTIONCONNECTEDTOPMREVISION: Connected to PM Revision");
		f.setFunction("Pm_Action_Connection_API.Get_Parent_Rev(:PM_NO,:PM_REVISION)");
		mgr.getASPField("PM_REVISION").setValidation("CONNECTEDPMREVISION");
		f.setReadOnly();

		f = headblk.addField("PM_STATE");
		f.setSize(20);
		f.setHidden();
		f.setFunction("Pm_Action_API.Get_Pm_State(:PM_NO,:PM_REVISION)");

		headblk.setView("PM_ACTION");
		headblk.defineCommand("PM_ACTION_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWRMBPMACTIONHD: Connection for Seperate PM Action"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);
		headbar.disableCommandGroup(headbar.CMD_GROUP_EDIT);
		headbar.addCustomCommand("none" ,"");
		headbar.addCustomCommand("headNextLevel",mgr.translate("PCMWRMBPMACTIONCDN1: Next Level"));
		headbar.addCustomCommand("headPreviousLevel",mgr.translate("PCMWRMBPMACTIONCDN2: Previous Level  "));
		headbar.addCustomCommand("generateWorkOrder",mgr.translate("PCMWRMBPMACTIONCDN: Generate Work Order"));

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.disableCommand(headbar.DELETE);       
		headbar.disableCommand(headbar.DUPLICATEROW);

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("CONNECTED_PM_NO","Number", "#");
		f.setSize(15);
		f.setLOV("PmActionLov.page",600,450);
                //Bug 71670, start
		f.setLOVProperty("WHERE","PM_TYPE_DB='ActiveSeparate' AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE IN ('Active', 'Preliminary') AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist(PM_NO,PM_REVISION)= 'FALSE')");
                //Bug 71670, end
                f.setMandatory();
		f.setLabel("PCMWRMBPMACTIONCONNECTEDPMNO: Connected PM No");
		f.setCustomValidation("CONNECTED_PM_NO","CONNECTED_PM_NO,CONNECTED_PM_REV,MCHCODE,MCHNAME,ITEM0_CONTRACT,ACTIONCODE,ACTIONDESCRIPTION");
		f.setReadOnly();
		f.setInsertable();

		f = itemblk0.addField("CONNECTED_PM_REV");
		f.setSize(15);
		//f.setDynamicLOV("PM_ACTION_LOV","CONNECTED_PM_NO PM_NO",600,445);
		f.setLOV("PmActionLov.page","CONNECTED_PM_NO PM_NO",600,450);
		f.setLOVProperty("WHERE","PM_TYPE_DB='ActiveSeparate' AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active' AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist(PM_NO,PM_REVISION)= 'FALSE')");
		f.setMandatory();
		f.setLabel("PCMWRMBPMACTIONCONNECTEDPMREVISION: Connected PM Revision");
		f.setCustomValidation("CONNECTED_PM_NO,CONNECTED_PM_REV","CONNECTED_PM_NO,CONNECTED_PM_REV,MCHCODE,MCHNAME,ITEM0_CONTRACT,ACTIONCODE,ACTIONDESCRIPTION");
		f.setReadOnly();
		f.setInsertable();
		f.setMaxLength(6);

		f = itemblk0.addField("MCHCODE");
		f.setSize(15);
		f.setLabel("PCMWRMBPMACTIONMCHCODE: Object ID");
		f.setFunction("Pm_Action_API.Get_Mch_Code(:CONNECTED_PM_NO,:CONNECTED_PM_REV)");
		f.setReadOnly();

		f = itemblk0.addField("MCHNAME");
		f.setSize(18);
		f.setLabel("PCMWRMBPMACTIONMCHNAME: Object Description");
		f.setFunction("Pm_Action_API.Get_Mch_Name(:CONNECTED_PM_NO,:CONNECTED_PM_REV)");
		f.setReadOnly();

		f = itemblk0.addField("ITEM0_CONTRACT");
		f.setSize(11);
		f.setLabel("PCMWRMBPMACTIONITEM0CONTRACT: Site");
		f.setFunction("PM_ACTION_API.Get_Contract(:CONNECTED_PM_NO,:CONNECTED_PM_REV)");
		f.setReadOnly();

		f = itemblk0.addField("ACTIONCODE");
		f.setSize(12);
		f.setLabel("PCMWRMBPMACTIONACTIONCODE: Action");
		f.setFunction("Pm_Action_API.Get_Action_Code_Id(:CONNECTED_PM_NO,:CONNECTED_PM_REV)");
		f.setReadOnly();

		f = itemblk0.addField("ACTIONDESCRIPTION");
		f.setSize(28);
		f.setLabel("PCMWRMBPMACTIONACTIONDESCRIPTION: Action Description");
		f.setFunction("Pm_Action_API.Get_Action_Descr(:CONNECTED_PM_NO,:CONNECTED_PM_REV)");
		f.setReadOnly();

		f = itemblk0.addField("ITEM0_PM_NO","Number", "#");
		f.setSize(11);
		f.setHidden();
		f.setDbName("PM_NO");

		f = itemblk0.addField("ITEM0_PM_REVISION");
		f.setSize(20);
		f.setHidden();
		f.setDbName("PM_REVISION");


		itemblk0.setView("PM_ACTION_CONNECTION");
		itemblk0.defineCommand("PM_ACTION_CONNECTION_API","New__,Modify__,Remove__");
		itemset0 = itemblk0.getASPRowSet();
		itemblk0.setMasterBlock(headblk);

		itembar0 = mgr.newASPCommandBar(itemblk0);
		itembar0.setBorderLines(true,true);
		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

		itembar0.enableRowStatus();

		itembar0.addCustomCommand("none " ,"");
		itembar0.addCustomCommand("nextLevel",mgr.translate("PCMWRMBPMACTIONCDN1: Next Level"));
		itembar0.addCustomCommand("previousLevel",mgr.translate("PCMWRMBPMACTIONCDN2: Previous Level  "));
		itembar0.addCustomCommand("Prepareing",mgr.translate("PCMWRMBPMACTIONCDN3: Prepare..."));
		itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");   
		itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");   


		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWRMBPMACTIONITM0: PM Action Connection"));
		itemtbl0.setWrap();

		tempblk1 = mgr.newASPBlock("TEMP1");
		f = tempblk1.addField("GENERATE");
		f = tempblk1.addField("CONECTED");
		f = tempblk1.addField("WO_NO1");
		f = tempblk1.addField("DNULL");
		f = tempblk1.addField("SNULL");
		f = tempblk1.addField("GEN_ID1");
		f = tempblk1.addField("GNTYPE");
		f = tempblk1.addField("PSDATE");
		f = tempblk1.addField("PFDATE");
		f = tempblk1.addField("HSEQ_NO");
		f = tempblk1.addField("HOUR");
		f = tempblk1.addField("DB_VALUE");
		f = tempblk1.addField("PLAN_HRS");

		mgr.endASPEvent();

		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
		itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
		itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

	}

	public void checkSecurity()
	{
		ASPManager mgr = getASPManager();

		if (!varSec)
		{
			trans.clear();

			trans.addSecurityQuery("PM_ACTION");
			trans.addSecurityQuery("Pm_Action_API","Generate__");
			trans.addPresentationObjectQuery("PCMW/PmAction.page");

			trans = mgr.perform(trans);

			SecBuff = trans.getSecurityInfo();

			if (SecBuff.itemExists("PM_ACTION"))
				secNextPre = true;

			if (SecBuff.itemExists("Pm_Action_API.Generate__"))
				secGen = true;

			if (SecBuff.namedItemExists("PCMW/PmAction.page"))
				secPmAct = true;

			varSec = true;
		}
	}     

	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if (headset.countRows() == 0)
		{
			headbar.disableCommand(headbar.FORWARD);
			headbar.disableCommand(headbar.BACKWARD);

		}

		if (itemset0.countRows() == 0)
		{
			itembar0.disableCommand(itembar0.FORWARD);
			itembar0.disableCommand(itembar0.BACKWARD);
			itembar0.disableCommand(itembar0.DELETE);
			mgr.getASPField("MCH_CODE").setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,445);
			mgr.getASPField("CONTRACT").setDynamicLOV("SITE",600,445);
			mgr.getASPField("ACTION_CODE_ID").setDynamicLOV("MAINTENANCE_ACTION",600,445);
			eval(headblk.generateAssignments("MCH_CODE,CONTRACT,ACTION_CODE_ID",headset.getRow()));
			itembar0.disableCommand(itembar0.SAVERETURN);
			itembar0.disableCommand(itembar0.DUPLICATE);

		}

		if (!secGen)
			headbar.removeCustomCommand("generateWorkOrder");
		if (!secPmAct)
			itembar0.removeCustomCommand("Prepareing");
		if (!secNextPre)
		{
			headbar.removeCustomCommand("headNextLevel");
			headbar.removeCustomCommand("headPreviousLevel");
			itembar0.removeCustomCommand("nextLevel");
			itembar0.removeCustomCommand("previousLevel");
		}

                if (headset.countRows()>0 && headlay.isSingleLayout() && "Obsolete".equals(headset.getRow().getValue("PM_STATE")))
                {
                    itembar0.disableCommand(itembar0.NEWROW);
                    itembar0.disableCommand(itembar0.DELETE);
                    itembar0.disableCommand(itembar0.EDITROW);
                    itembar0.disableCommand(itembar0.DUPLICATEROW);
                }

                if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() )
		{
		   headbar.disableCommand(headbar.DELETE);
		   headbar.disableCommand(headbar.NEWROW);
		   headbar.disableCommand(headbar.EDITROW);
		   headbar.disableCommand(headbar.DELETE);
		   headbar.disableCommand(headbar.DUPLICATEROW);
		   headbar.disableCommand(headbar.FIND);
		   headbar.disableCommand(headbar.BACKWARD);
		   headbar.disableCommand(headbar.FORWARD);
		}
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWRMBPMACTIONTITLE: Connection for Seperate PM Action";
	}

	protected String getTitle()
	{
		return "PCMWRMBPMACTIONTITLE: Connection for Seperate PM Action";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && (headset.countRows() > 0))
			appendToHTML(itemlay0.show());


		appendDirtyJavaScript("function validateConnectedPmNo(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
		appendDirtyJavaScript("   setDirty();\n");
		appendDirtyJavaScript("   if( getValue_('CONNECTED_PM_NO',i).indexOf('~') == -1)\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      if( !checkConnectedPmNo(i) ) return;\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("      if( getValue_('CONNECTED_PM_NO',i).indexOf('%') != -1) return;\n");
		appendDirtyJavaScript("      if( getValue_('CONNECTED_PM_NO',i)=='' )\n");
		appendDirtyJavaScript("      {\n");
		appendDirtyJavaScript("         getField_('CONNECTED_PM_NO',i).value = '';\n");
		appendDirtyJavaScript("		getField_('CONNECTED_PM_REV',i).value = '';\n");
		appendDirtyJavaScript("		getField_('MCHCODE',i).value = '';\n");
		appendDirtyJavaScript("		getField_('MCHNAME',i).value = '';\n");
		appendDirtyJavaScript("		getField_('ITEM0_CONTRACT',i).value = '';\n");
		appendDirtyJavaScript("		getField_('ACTIONCODE',i).value = '';\n");
		appendDirtyJavaScript("		getField_('ACTIONDESCRIPTION',i).value = '';\n");
		appendDirtyJavaScript("		return;\n");
		appendDirtyJavaScript("	     }\n");
		appendDirtyJavaScript("window.status='Please wait for validation';\n");
		appendDirtyJavaScript(" r = __connect(\n");
		appendDirtyJavaScript(" '");
		appendDirtyJavaScript( mgr.getURL());
		appendDirtyJavaScript("?VALIDATE=CONNECTED_PM_NO'+ '&CONNECTED_PM_NO=' + URLClientEncode(getValue_('CONNECTED_PM_NO',i)));\n");
		appendDirtyJavaScript("window.status='';\n");
		appendDirtyJavaScript("   if( checkStatus_(r,'CONNECTED_PM_NO',i,'Connected PM No') )\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      assignValue_('CONNECTED_PM_NO',i,0);\n");
		appendDirtyJavaScript("      assignValue_('CONNECTED_PM_REV',i,1);\n");
		appendDirtyJavaScript("      assignValue_('MCHCODE',i,2);\n");
		appendDirtyJavaScript("      assignValue_('MCHNAME',i,3);\n");
		appendDirtyJavaScript("      assignValue_('ITEM0_CONTRACT',i,4);\n");
		appendDirtyJavaScript("      assignValue_('ACTIONCODE',i,5);\n");
		appendDirtyJavaScript("      assignValue_('ACTIONDESCRIPTION',i,6);\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");


	}
}
