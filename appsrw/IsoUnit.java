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
*  File        : IsoUnit.java 
*  ASP2JAVA Tool  2001-03-08 VAGU Created Using the ASP file IsoUnit.asp
*  Modified    :  2001-03-14 BUNI Corrected some conversion errors.
*              :  2001-04-19 BUNI Modified saveReturn() method.   
*              :  2001-05-17 VAGU Code Review
*              :  2001-08-15 JEWI Set Maxlenght of field 'UNIT_CODE' to 10. (Call # 77934)  
*              :  2001-08-29 VAGU Changed the name from Units to Units of Measure (78053)
*              :  2003-10-12 Zahalk - Call ID 106477, Did some changes to show all the records.
* ----------------------------------------------------------------------------
 * New Comments:
 * 2007/05/04 buhilk Bug 65155, Modified newRow() to fix the error when duplicating UoM values.
 * 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class IsoUnit extends ASPPageProvider
{

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.IsoUnit");


	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPBuffer data;
	private ASPQuery q;

	//===============================================================
	// Construction 
	//===============================================================
	public IsoUnit(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	protected void doReset() throws FndException
	{
		//Resetting mutable attributes
		trans   = null;
		cmd   = null;
		data   = null;
		q   = null;
		super.doReset();
	}

	public ASPPoolElement clone(Object obj) throws FndException
	{
		IsoUnit page = (IsoUnit)(super.clone(obj));

		// Initializing mutable attributes
		page.trans   = null;
		page.cmd   = null;
		page.data   = null;
		page.q   = null;

		// Cloning immutable attributes
		page.headblk = page.getASPBlock(headblk.getName());
		page.headset = page.headblk.getASPRowSet();
		page.headbar = page.headblk.getASPCommandBar();
		page.headtbl = page.getASPTable(headtbl.getName());
		page.headlay = page.headblk.getASPBlockLayout();
		page.f = page.getASPField(f.getName());

		return page;
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("UNIT_CODE")))
			okFind();
		else if (mgr.dataTransfered())
			okFind();

		adjust();
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		String txt = "";


		if ("PRESENT_FACTOR".equals(val))
		{
			String Present_Fact = mgr.readValue("PRESENT_FACTOR");

			ASPBuffer buff = mgr.newASPBuffer();
			buff.addFieldItem("PRESENT_FACTOR_TEMP",Present_Fact);

			txt = Present_Fact;
			mgr.responseWrite(txt);
		}

		if ("BASE_UNIT".equals(val))
		{
			cmd = trans.addCustomFunction("BUNIT","ISO_UNIT_API.Get_Unit_Type","UNIT_TYPE");
			cmd.addParameter("BASE_UNIT");

			trans = mgr.validate(trans);

			String bUnit = trans.getValue("BUNIT/DATA/UNIT_TYPE");

			txt = (mgr.isEmpty(bUnit) ? "": (bUnit))+ "^";
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

		cmd = trans.addEmptyCommand("HEAD","ISO_UNIT_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);      
      headset.setValue("PRESENT_FACTOR","1");
	}


	public void  saveReturn()
	{
	   ASPManager mgr = getASPManager();

	   if ("New__".equals(headset.getRowStatus()))
	   {
              int currHead = headset.getCurrentRowNo();
              headset.changeRow();
	      mgr.submit(trans);
	      headset.goTo(currHead);
	   }
	   else
	   {
              String temp = mgr.readValue("DESCRIPTION");

	      String lsAttr ="DESCRIPTION" + (char)31 + mgr.readValue("DESCRIPTION") + (char)30;

              cmd = trans.addCustomCommand("MODIF", "ISO_UNIT_API.Modify__"); 
	      cmd.addParameter("INFO");    
	      cmd.addParameter("OBJID");
	      cmd.addParameter("OBJVERSION");
	      cmd.addParameter("ATTR",lsAttr);
	      cmd.addParameter("ACTION","DO");

	      int currrow = headset.getCurrentRowNo();

	      int n = headset.countRows();
	      String separator_="','";
	      String  unit_code = "";
	      headset.last();

	      int j=n;

	      while (j>=1)
	      {
	         unit_code = unit_code + headset.getRow().getValue("UNIT_CODE")+separator_;
	         headset.previous();
	         --j;
	      }
	      mgr.submit(trans);   

	      headset.goTo(currrow);
	      headlay.setLayoutMode(headlay.getHistoryMode());
	      trans.clear();
	      okFindSave(unit_code);
	   }
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------

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
	   q.setOrderByClause("BASE_UNIT");
	   q.includeMeta("ALL");
	   mgr.querySubmit(trans,headblk);

	   if (headset.countRows() == 0)
	   {
	      mgr.showAlert(mgr.translate("APPSRWISOUNITNODATA: No data found."));
	      headset.clear();
	   }
	}


	public void  okFindSave(String unit_code)
	{
	   ASPManager mgr = getASPManager();

	   q = trans.addEmptyQuery(headblk);
	   q.addWhereCondition("UNIT_CODE in(?)");
      q.addParameter("UNIT_CODE", unit_code);
	   q.setOrderByClause("BASE_UNIT");
	   q.includeMeta("ALL");
	   mgr.submit(trans);
	}


	public void  okFind1()
	{
	   ASPManager mgr = getASPManager();

	   q = trans.addEmptyQuery(headblk);
	   q.setOrderByClause("BASE_UNIT");
	   q.includeMeta("ALL");
	   mgr.querySubmit(trans,headblk);

	   if (headset.countRows() == 0)
	   {
	      mgr.showAlert(mgr.translate("APPSRWISOUNITNODATA: No data found."));
	      headset.clear();
	   }
	}

	public void  preDefine()
	{
	        ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("UNIT_CODE");
		f.setSize(30);
		f.setMandatory();
		f.setInsertable();
		f.setReadOnly();
		f.setMaxLength(30);
		f.setLabel("APPSRWISOUNITUNITCODE: Unit Code");
		f.setWfProperties(1);

		f = headblk.addField("DESCRIPTION");
		f.setSize(50);
		f.setMandatory();
		f.setInsertable();
		f.setMaxLength(50);
		f.setLabel("APPSRWISOUNITDESC: Description");
		f.setWfProperties(2);

		f = headblk.addField("PRESENT_FACTOR_TEMP","Number");
		f.setHidden();
		f.setFunction("0");

		f = headblk.addField("PRESENT_FACTOR");
		f.setSize(20);
		f.setMandatory();
		f.setInsertable();
		f.setReadOnly();
		f.setMaxLength(20);
		f.setCustomValidation("PRESENT_FACTOR","PRESENT_FACTOR");
		f.setLabel("APPSRWISOUNITPRESENTFACTOR: Factor");

		f = headblk.addField("BASE_UNIT");
		f.setSize(30);
		f.setInsertable();
		f.setReadOnly();
		f.setMaxLength(30);
		f.setDynamicLOV("ISO_UNIT_BASE",600,450);
		f.setLOVProperty("TITLE",mgr.translate("APPSRWISOUNITBASE: Base Units"));
		f.setCustomValidation("BASE_UNIT","UNIT_TYPE");
		f.setLabel("APPSRWISOUNITBASEUNIT: Base Unit");

		f = headblk.addField("UNIT_TYPE");
		f.setSize(200);
		f.setMandatory();
		f.setInsertable();
		f.setReadOnly();
		f.setMaxLength(200);
		f.setLabel("APPSRWISOUNITUTYPE: Unit Type");
		f.setSelectBox();
		f.enumerateValues("ISO_UNIT_TYPE_API");
		f.setWfProperties(0);

		f = headblk.addField("DUMMY");
		f.setSize(8);
		f.setHidden();
		f.setLabel("APPSRWISOUNITDUMMY: Dummy");
		f.setFunction("''"); 
		f.setUpperCase();

		f = headblk.addField("USER_DEFINED");
		f.setCheckBox("FALSE,TRUE");
		f.setSize(15);
		f.setReadOnly();
		f.setLabel("APPSRWISOUNITUSERDEFINED: User Defined");

		f = headblk.addField("MULTI_FACTOR","Number");
		f.setSize(5);
		f.setHidden();
		f.setReadOnly();
		f.setLabel("APPSRWISOUNITMULTIFACTOR: Multipl Factor");

		f = headblk.addField("DIV_FACTOR","Number");
		f.setSize(5);
		f.setHidden();
		f.setReadOnly();
		f.setLabel("APPSRWISOUNITDIVFACTOR: Div Factor");

		f = headblk.addField("TEN_POWER","Number");
		f.setSize(5);
		f.setReadOnly();
		f.setHidden();
		f.setLabel("APPSRWISOUNITTENPOWER: Pow of ten");

		f = headblk.addField("ATTR");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("INFO");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("ACTION");
		f.setHidden();
		f.setFunction("''");

		headblk.setView("ISO_UNIT");
		headblk.defineCommand("ISO_UNIT_API","New__,Modify__,Remove__");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headbar.defineCommand(headbar.SAVERETURN ,"saveReturn","checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW ,null,"checkHeadFields(-1)");

		headtbl = mgr.newASPTable(headblk);
		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.setDialogColumns(1);
		headlay.setSimple("DESCRIPTION");
	}


	public void  adjust()
	{

	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "APPSRWISOUNITTITLE: Units of Measure";
	}

	protected String getTitle()
	{
		return "APPSRWISOUNITTITLE: Units of Measure";
	}

	protected void printContents() throws FndException
	{

		if (headlay.isVisible())
		{
			appendToHTML(headlay.show());
		}
		
		if (headset.countRows() > 0 && headlay.isSingleLayout())
		{
		   this.printReadLabel(headblk.getWfDescription());
		}
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}

}
