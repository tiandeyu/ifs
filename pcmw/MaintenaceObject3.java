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
*  File        : MaintenaceObject3.java 
*  Modified    : 2001-03-19   - Indra Rodrigo - Java conversion.
*  ASP2JAVA Tool  2001-03-18  - Created Using the ASP file MaintenaceObject3.asp
*  JEWI           2001-03-20  - Modified method okFindTransfer() to populate the item block, when countRows is 1.
*  CHCR           2001-05-02  - Modified nextLvl() and preLvl() methods and removed Next Level action from header.
* ----------------------------------------------------------------------------
*  SHAFLK         2002-05-20  - Bug 30188,Modified sparePartObject().
*  GACOLK         2002-12-04  - Set Max Length of MCH_CODE, ITEM0_MCH_CODE to 100
*  ARWILK         031218        Edge Developments - (Removed clone and doReset Methods)
* ------------------------------ EDGE - SP1 Merge ---------------------------
*  SHAFLK         2004-02-13  - Bug 40256,Modified methods run(), sparePartObject() and preDefine(). 
*  VAGULK         040325      - Merged with SP1.
*  NIJALK         050201      - Modified run().
* ----------------------------------------------------------------------------
*  AMDILK         060731      - Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMNILK    	  060807      - Merged Bug Id: 58214.
*  DIAMLK  	  060818      - Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  	  060906      - Merged Bug Id: 58216.
*  AMNILK         070717      - Eliminated XSS Security Vulnerability.
*----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenaceObject3 extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenaceObject3");

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
	private ASPBlockLayout itemlay0;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================

	private ASPTransactionBuffer trans;
	private String woNo;
	private String cont;
	private boolean newWin;
	private String callingUrl;
	private int beg_pos;
	private int end_pos;
	private String oldurl;
	private ASPBuffer buff;
	private ASPBuffer row;
	private String mchCode;
	private String orderNo;
	private ASPQuery q;
	private int currrow;
	private ASPCommand cmd;
	private int cntHeadRows;
	private String strWhereCondition;
	private int i;
	private int strCountNewRows;
	private String strCurMchCode;
	private ASPBuffer buffer;
	private String form_name;  
	private String prefix;  
	private String mch_code;
	private String contract;
	private boolean sparePartOfObj;
	private String qrystr;
	private String frameName;
	private boolean closeMe;

	//===============================================================
	// Construction 
	//===============================================================
	public MaintenaceObject3(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();


		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();
		fmt = mgr.newASPHTMLFormatter();

		woNo= ctx.readValue("WONO","");
		cont = ctx.readValue("CONT","");
		newWin = ctx.readFlag("NEWWIN",false);
		mch_code = ctx.readValue("MCHCODE","");
		orderNo = ctx.readValue("ORDERNO","");
		contract = ctx.readValue("CONTARACT","");
		qrystr = ctx.readValue("QRYSTR",""); 
		frameName = ctx.readValue("FRAMENAME","");
		closeMe = ctx.readFlag("CLOSEME",false);

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("FROMOBJPART")))
			closeMe = true;
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.buttonPressed("BACK"))
			back();
		else if (mgr.dataTransfered())
		{
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			mchCode = row.getValue("MCH_CODE");
			row = buff.getBufferAt(1); 
			cont = row.getValue("CONTRACT");
			row = buff.getBufferAt(2); 
			woNo = row.getValue("WO_NO");
                        orderNo = mgr.readValue("ORDER_NO");
			okFindTransfer();
			okFindITEM0();
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("UP_WO_NO")))
		{
			mchCode = mgr.readValue("UP_MCH_CODE");
			cont = mgr.readValue("CONTRACT");
			woNo = mgr.readValue("UP_WO_NO");
                        orderNo = mgr.readValue("ORDER_NO");
			okFindTransfer();
			okFindITEM0();
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
		{
			mchCode = mgr.readValue("MCH_CODE");
			cont = mgr.readValue("CONTRACT");
			woNo = mgr.readValue("WO_NO");
			orderNo = mgr.readValue("ORDER_NO");
			qrystr = mgr.readValue("QRYSTR","");
			frameName = mgr.readValue("FRMNAME","");
			okFindTransfer();
			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
			newWin = true;
		}

		adjust();

		ctx.writeValue("WONO",woNo);
		ctx.writeValue("CONT",cont);
		ctx.writeFlag("NEWWIN",newWin);
		ctx.writeValue("CONTARACT",contract);
		ctx.writeValue("MCH_CODE",mch_code);
		ctx.writeValue("ORDERNO",orderNo);
		ctx.writeValue("QRYSTR",qrystr);
		ctx.writeValue("FRAMENAME",frameName);
		ctx.writeFlag("CLOSEME",closeMe);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

//-----------------------------------------------------------------------------
//------------------------  BUTTON FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

	public void  back()
	{
		ASPManager mgr = getASPManager();
		if ("WorkOrderRequisHeaderRMB".equals(form_name))
		{
			beg_pos = callingUrl.indexOf("h");
			end_pos = callingUrl.lastIndexOf("/")+1;
			prefix = callingUrl.substring(beg_pos,end_pos);
			String calling_url = oldurl ;
			ctx.setGlobal("CALLING_URL",calling_url);
			String strWoNo=ctx.getGlobal("GLOBAL_WO_NO");
			String strMchCode=ctx.getGlobal("GLOBAL_MCH_CODE");
			String strMchName=ctx.getGlobal("GLOBAL_MCH_NAME");

			buff = mgr.newASPBuffer();
			row = buff.addBuffer("0");
			row.addItem("WO_NO",strWoNo);
			row.addItem("MCH_CODE",strMchCode);
			row.addItem("MCH_NAME",strMchName);

			mgr.transferDataTo("WorkOrderRequisHeaderRMB.page",buff); 
		}
		else
		{
			mgr.redirectTo(callingUrl+"?WO_NO="+mgr.URLEncode(woNo));
		}
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  countFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setSelectList("to_char(count(*)) N");

		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();
	}

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
	}

	public void  okFindTransfer()
	{
		ASPManager mgr = getASPManager();


		q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
                //Bug 58216 start
                q.addWhereCondition("MCH_CODE = ? and CONTRACT = ?");
                q.addParameter("MCH_CODE",mchCode);
                q.addParameter("CONTRACT",cont);
                //Bug 58216 end
		q.includeMeta("ALL");

		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			headset.clear();
		}
		else if (headset.countRows() == 1)
			okFindITEM0();
	}

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

	public void  okFindITEM0()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		q = trans.addEmptyQuery(itemblk0);
                //Bug 58216 start
		q.addWhereCondition("SUP_MCH_CODE = ? and SUP_CONTRACT = ?");
                q.addParameter("ITEM0_SUP_MCH_CODE",headset.getRow().getValue("MCH_CODE"));
                q.addParameter("ITEM0_SUP_CONTRACT",headset.getRow().getValue("CONTRACT"));
                //Bug 58216 end
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setOrderByClause("MCH_CODE");
		q.includeMeta("ALL");

		currrow = headset.getCurrentRowNo();
		mgr.submit(trans);

		headset.goTo(currrow);

		if (itemset0.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3NODATA: No data found."));
			itemset0.clear();
		}
	}


	public void  countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		currrow = headset.getCurrentRowNo();

		q = trans.addQuery(itemblk0);
                //Bug 58216 start
		q.addWhereCondition("SUP_MCH_CODE = ? and SUP_CONTRACT = ?");
                q.addParameter("ITEM0_SUP_MCH_CODE",headset.getRow().getValue("MCH_CODE"));
                q.addParameter("ITEM0_SUP_CONTRACT",headset.getRow().getValue("CONTRACT"));
                //Bug 58216 end
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setSelectList("to_char(count(*)) N");

		mgr.submit(trans);

		itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
		itemset0.clear();
		headset.goTo(currrow);
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


	public void  nextLvl()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout())
			itemset0.goTo(itemset0.getRowSelected());

		mchCode = itemset0.getRow().getFieldValue("ITEM0_MCH_CODE");

		cmd = trans.addCustomFunction("HASSTRUC","Equipment_Object_API.Has_Structure__","STRUCTFLG");
		cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
		cmd.addParameter("MCH_CODE",itemset0.getRow().getFieldValue("ITEM0_MCH_CODE"));

		trans = mgr.perform(trans);

		String hasStruct = trans.getValue("HASSTRUC/DATA/STRUCTFLG");

		if ("FALSE".equals(hasStruct))
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3CANNOT: Cannot perform on selected line."));
		else
		{
			cntHeadRows = headset.countRows();
			headset.first();

			strWhereCondition ="MCH_CODE IN (";

			//Bug 58216 start
			ASPBuffer paramBuff = mgr.newASPBuffer();
			paramBuff = headset.getRows("MCH_CODE");

                        for (i=0; i<cntHeadRows; i++)
			{
			    if (i>0) strWhereCondition += ",";
			    strWhereCondition += "?";
				headset.next();
			}

			strWhereCondition = strWhereCondition + ",?)";
			headset.clear();
			trans.clear();

			q = trans.addEmptyQuery(headblk);  
			q.addWhereCondition(strWhereCondition);

			for (int i = 0; i < paramBuff.countItems(); i++)
			{
			     ASPBuffer temp = paramBuff.getBufferAt(i);
			     q.addParameter("MCH_CODE",temp.getValueAt(0));
			}
			q.addParameter("MCH_CODE", mchCode);
			//Bug 58216 end
			q.includeMeta("ALL");

			mgr.submit(trans);   

			strCountNewRows = headset.countRows();
			headset.first();

			for (i=0; i<=strCountNewRows; i++)
			{
				strCurMchCode = headset.getRow().getValue("MCH_CODE");

				if (mchCode.equals(strCurMchCode))
				{
					headset.resetRow();
					break;
				}
				headset.next();
			}  

			headset.goTo(i);
			trans.clear();
			okFindITEM0();
		}
	}

	public void  preLvl()
	{
		ASPManager mgr = getASPManager();

		String supMchCode = headset.getRow().getFieldValue("SUP_MCH_CODE");

		if (mgr.isEmpty(supMchCode))
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3CANNOT: Cannot perform on selected line."));
		else
		{
			String parent = headset.getRow().getValue("SUP_MCH_CODE");
			cntHeadRows = headset.countRows();
			headset.first();

			strWhereCondition ="MCH_CODE IN (";

			//Bug 58216 start
			ASPBuffer paramBuff = mgr.newASPBuffer();
			paramBuff = headset.getRows("MCH_CODE");

			for (i=0; i<cntHeadRows; i++)
			{
			    if (i>0) strWhereCondition += ",";
			    strWhereCondition += "?";
				headset.next();
			}
			strWhereCondition = strWhereCondition + ",?)";
			headset.clear();
			trans.clear();

			q = trans.addEmptyQuery(headblk);  
			q.addWhereCondition(strWhereCondition);
			for (int i = 0; i < paramBuff.countItems(); i++)
			{
			     ASPBuffer temp = paramBuff.getBufferAt(i);
			     q.addParameter("MCH_CODE",temp.getValueAt(0));
			}
			q.addParameter("MCH_CODE", parent);
			//Bug 58216 end
			q.includeMeta("ALL");

			mgr.submit(trans);   

			strCountNewRows = headset.countRows();
			headset.first();

			for (i=0; i<=strCountNewRows; i++)
			{
				strCurMchCode = headset.getRow().getValue("MCH_CODE");

				if (parent.equals(strCurMchCode))
				{
					headset.resetRow();
					break;
				}
				headset.next();
			}   

			headset.goTo(i);
			trans.clear();
			okFindITEM0();
		}
	}


	public void  sparePartObject()
	{
		ASPManager mgr = getASPManager();
		//Bug 30188, start
		String calling_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL",calling_url);
		//Bug 30188, start 

		itemset0.storeSelections();
		itemset0.setFilterOn();

		itemset0.first();

		cmd = trans.addCustomFunction("HASPART","Equipment_Object_Spare_API.Has_Spare_Part","PARTFLG");
		cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
		cmd.addParameter("MCH_CODE",itemset0.getRow().getFieldValue("ITEM0_MCH_CODE"));

		trans = mgr.perform(trans);

		String partflg = trans.getValue("HASPART/DATA/PARTFLG");

		if ("FALSE".equals(partflg))
			mgr.showAlert(mgr.translate("PCMWMAINTENACEOBJECT3CANNOT: Cannot perform on selected line."));
		else
		{
			mch_code = itemset0.getRow().getFieldValue("ITEM0_MCH_CODE");
			contract = itemset0.getRow().getFieldValue("ITEM0_CONTRACT");

			//Bug 30188, start
			buffer = mgr.newASPBuffer();
			row = buffer.addBuffer("0");
			row.addItem("MCH_CODE",mch_code);
			row.addItem("CONTRACT",contract);
			row.addItem("WO_NO",woNo);
			row.addItem("ORDER_NO",orderNo);
			row.addItem("FRMNAME",frameName);
			row.addItem("QRYSTR",qrystr);
			mgr.transferDataTo("MaintenanceObject2.page",buffer);
			//Bug 30188, end
		}
		itemset0.setFilterOff(); 
	}


	public void  preDefine()
	{
		ASPManager mgr = getASPManager();


		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("MCH_CODE");
		f.setSize(34);
		f.setMaxLength(100);
		f.setMandatory();
		f.setLabel("PCMWMAINTENACEOBJECT3MCHCODE: Object ID");
		f.setReadOnly();

		f = headblk.addField("MCH_NAME");  
		f.setSize(25);                     
		f.setMandatory();                  
		f.setLabel("PCMWMAINTENACEOBJECT3MCHNAME: Description");
		f.setReadOnly();                   

		f = headblk.addField("CONTRACT");
		f.setSize(7);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWMAINTENACEOBJECT3CONTR: Site");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("SUP_MCH_CODE");
		f.setHidden();

		f = headblk.addField("SUP_CONTRACT");
		f.setHidden();


		headblk.setView("MAINTENANCE_OBJECT");
		headblk.defineCommand("MAINTENANCE_OBJECT_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWMAINTENACEOBJECT3HD: Object Structure  for Object"));

		headbar = mgr.newASPCommandBar(headblk);

		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.EDITROW);
		headbar.addCustomCommand("preLvl",mgr.translate("PCMWMAINTENACEOBJECT3PRELEV: Previous Level"));

		headlay = headblk.getASPBlockLayout();                
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(2);

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk0.addField("ITEM0_SUP_MCH_CODE");
		f.setHidden();
		f.setLabel("PCMWMAINTENACEOBJECT3IT0SMCODE: Object ID");
		f.setDbName("SUP_MCH_CODE");

		f = itemblk0.addField("ITEM0_SUP_CONTRACT");
		f.setHidden();
		f.setLabel("PCMWMAINTENACEOBJECT3IT0SCONT: Site");
		f.setDbName("SUP_CONTRACT");

		f = itemblk0.addField("ITEM0_MCH_CODE");
		f.setSize(22);
		f.setMaxLength(100);
		f.setMandatory();
		f.setLabel("PCMWMAINTENACEOBJECT3IT0MCODE: Object ID"); 
		f.setDbName("MCH_CODE");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("ITEM0_CONTRACT");
		f.setSize(10);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setMandatory();
		f.setLabel("PCMWMAINTENACEOBJECT3IT0CONT: Site");
		f.setDbName("CONTRACT");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk0.addField("HASSTRUCTURE");
		f.setSize(12);
		f.setLabel("PCMWMAINTENACEOBJECT3HASSTRU: Structure");
		f.setFunction("Translate_Boolean_API.Decode(EQUIPMENT_OBJECT_API.HAS_STRUCTURE__(:ITEM0_CONTRACT,:ITEM0_MCH_CODE))");
		f.setReadOnly();

		f = itemblk0.addField("HASSPAREPARTS");
		f.setSize(12);
		f.setLabel("PCMWMAINTENACEOBJECT3HASSPAREPARTS: Spare Parts");
		f.setFunction("Translate_Boolean_API.Decode(EQUIPMENT_OBJECT_SPARE_API.HAS_SPARE_PART(:ITEM0_CONTRACT,:ITEM0_MCH_CODE))");
		f.setReadOnly();

		f = itemblk0.addField("ITEM0_MCH_NAME");
		f.setSize(30);
		f.setLabel("PCMWMAINTENACEOBJECT3IT0MNAME: Object Description");
		f.setDbName("MCH_NAME");
		f.setReadOnly();

		f = itemblk0.addField("STRUCTFLG");
		f.setHidden();
		f.setFunction("''");

		f = itemblk0.addField("PARTFLG");
		f.setHidden();
		f.setFunction("''");

		f = itemblk0.addField("WO_NO");
		f.setHidden();
		f.setFunction("''");

		f = itemblk0.addField("ORDER_NO");
		f.setHidden();
		f.setFunction("''");

		itemblk0.setView("MAINTENANCE_OBJECT");
		itemblk0.defineCommand("MAINTENANCE_OBJECT_API","New__,Modify__,Remove__");
		itemset0 = itemblk0.getASPRowSet();
		itemblk0.setMasterBlock(headblk);             

		itembar0 = mgr.newASPCommandBar(itemblk0);

		itembar0.disableCommand(itembar0.EDITROW);
		itembar0.disableCommand(itembar0.NEWROW);
		itembar0.disableCommand(itembar0.DELETE);
		itembar0.disableCommand(itembar0.DUPLICATEROW);

		itembar0.addCustomCommand("nextLvl",mgr.translate("PCMWMAINTENACEOBJECT3NEXLEV2: Next Level"));
		itembar0.addCustomCommand("preLvl",mgr.translate("PCMWMAINTENACEOBJECT3PRELEV2: Previous Level"));
		itembar0.addCustomCommandSeparator();
		itembar0.addCustomCommand("sparePartObject",mgr.translate("PCMWMAINTENACEOBJECT3SPAREPAROBJ: Spare Parts in Object..."));

		itembar0.enableCommand(itembar0.FIND);
		itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWMAINTENACEOBJECT3ITM0: Maintenance Object"));
		itemlay0 = itemblk0.getASPBlockLayout();
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
		itemlay0.setDialogColumns(2);  
	}


	public void  adjust()
	{
		if (itemset0.countRows() == 0)
		{
			headbar.removeCustomCommand("preLvl");
		}

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWMAINTENACEOBJECT3TITLE: Object Structure  for Object ";
	}

	protected String getTitle()
	{
		return "PCMWMAINTENACEOBJECT3TITLE: Object Structure  for Object ";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && (headset.countRows() > 0))
			appendToHTML(itemlay0.show());

		appendToHTML("<br>\n");

		if (!newWin)
			appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWMAINTENACEOBJECT3BAC: Back"),""));

		appendDirtyJavaScript("window.name = \"objStruct\"\n");

		if (closeMe)
			appendDirtyJavaScript("window.close();\n");

		if (sparePartOfObj)
		{
			appendDirtyJavaScript("  jmch_code = '");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(mch_code));		//XSS_Safe AMNILK 20070717
			appendDirtyJavaScript("';\n");
			appendDirtyJavaScript("  jContract = '");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(contract));		//XSS_Safe AMNILK 20070717
			appendDirtyJavaScript("';\n");
			appendDirtyJavaScript("  jWoNo = '");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(woNo));		//XSS_Safe AMNILK 20070717
			appendDirtyJavaScript("';\n");
			appendDirtyJavaScript("  jqrystr = '");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));		//XSS_Safe AMNILK 20070717
			appendDirtyJavaScript("';  \n");
			appendDirtyJavaScript(" window.open(\"MaintenanceObject2.page?MCH_CODE=\"+URLClientEncode(jmch_code)+\"&CONTRACT=\"+URLClientEncode(jContract)+\"&WO_NO=\"+URLClientEncode(jWoNo)+\"&QRYSTR=\"+URLClientEncode(jqrystr)+\"&FRMNAME="+mgr.encodeStringForJavascript(frameName)+"\"+\"&FROMOBJSTRUCT=TRUE\",\"\",\"alwaysRaised=yes,scrollbars=yes,status=yes,width=750,height=450\");   \n");   //XSS_Safe AMNILK 20070717
		}
	}

}
