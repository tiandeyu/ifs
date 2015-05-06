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
 * File : WorkOrderAddressDlg.java
 * Modified : 
 * 2009-07-16 CHANLK Created.
 * ----------------------------------------------------------------------------
 */

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;

public class WorkOrderAddressDlg extends ASPPageProvider
{

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderAddressDlg");

	//-----------------------------------------------------------------------------
	//---------- Header Instances created on page creation --------
	//-----------------------------------------------------------------------------

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	private ASPBlock        addressblk;
	private ASPRowSet       addressset;
	private ASPCommandBar   addressbar;
	private ASPTable        addresstbl;
	private ASPBlockLayout  addresslay;

	private ASPField ADDRESS1;
	private ASPField ADDRESS2;
	private ASPField ZIP_CODE;
	private ASPField CITY;
	private ASPField COUNTRY_CODE;
	private ASPField STATE;
	private ASPField COUNTY;
	private ASPField ADD_COUNTRY;

	private ASPContext      ctx;
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private ASPBuffer buff;
	private ASPBuffer row;
   private String sViewName;

	private int curentrow;
	//-----------------------------------------------------------------------------
	//------------------------  Construction  ---------------------------
	//-----------------------------------------------------------------------------

	public  WorkOrderAddressDlg (ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		trans = mgr.newASPTransactionBuffer();
		ctx   = mgr.getASPContext();

		if ( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if ( mgr.dataTransfered() )
		{
			buff = mgr.getTransferedData();
			row = buff.getBufferAt(0); 
			ctx.writeValue("WO_NO",row.getValue("WO_NO"));
			okFind();
		}
		adjust();
	}
	//-----------------------------------------------------------------------------
	//------------------------  Command Bar functions  ---------------------------
	//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		ASPQuery q;
		q = trans.addQuery(headblk);
		q.addWhereCondition("WO_NO = ? ");
		q.addParameter("WO_NO",ctx.readValue("WO_NO",""));
		q.includeMeta("ALL");

		mgr.querySubmit(trans,headblk);

		trans.clear();

		if ( headset.countRows() == 1 )
		{
			okFindAddress();
		}

		else if ( headset.countRows() == 0 )
		{
			headset.clear();
			mgr.showAlert(mgr.translate("PCMWWORKORDERADDRESSDATAFOUND: No data found."));
		}

	}
	public void okFindAddress() 
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(addressblk);
		q.addWhereCondition("WO_NO = ? ");
		q.addParameter("WO_NO",ctx.readValue("WO_NO",""));

		q.includeMeta("ALL");


		mgr.querySubmit(trans,addressblk);
	}

	public void saveReturn(){
		ASPManager mgr = getASPManager();
		addressset.changeRow();
		addressset.changeRow();
		mgr.submit(trans);
		addresslay.setLayoutMode(addresslay.SINGLE_LAYOUT);
	}


	public void cancelNew()
	{
		ASPManager mgr = getASPManager();
		ctx.writeValue("WO_NO",mgr.readValue("WO_NO"));
		addressset.clear();
		addresslay.setLayoutMode(addresslay.SINGLE_LAYOUT);
		adjust();
	}

	public void newRowADDRESS()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_ADDRESS_API.New__",addressblk);
		cmd.setOption("ACTION","PREPARE");


		trans = mgr.perform(trans);

		buff = trans.getBuffer("HEAD/DATA");
		buff.setValue("WO_NO",ctx.readValue("WO_NO",""));

		addressset.addRow(buff);

		addresslay.setLayoutMode(addresslay.NEW_LAYOUT);
	}

	//-----------------------------------------------------------------------------
	//------------------------  Predefines Head ---------------------------
	//-----------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setHidden();

		headblk.addField("OBJVERSION").
		setHidden();

		headblk.addField("WO_NO","Number","#").
		setSize(20).
		setLabel("PCMWWORKORDERADDRESSHEADWONO: WO No").
		setReadOnly();

		headblk.addField("CONTRACT").
		setSize(5).
		setLabel("PCMWWORKORDERADDRESSCONTRACT: WO Site").
		setUpperCase().
		setReadOnly();

		headblk.addField("ERR_DESCR").
		setSize(30).
		setLabel("PCMWWORKORDERADDRESSERRDESCR: Directive").
		setReadOnly();

		sViewName = mgr.getDynamicDefKey().toUpperCase();

      headblk.setView(sViewName);
		headblk.defineCommand(sViewName + "_API","");

		headset = headblk.getASPRowSet();
		headtbl = mgr.newASPTable(headblk);
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.NEWROW);
		headbar.disableCommand(headbar.DELETE);
		headbar.disableCommand(headbar.BACK);

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
//		-------------------------- Address Tab  --------------------------------------

		addressblk = mgr.newASPBlock("ADDRESS");

		addressblk.addField("ADDRESS_OBJID").
		setHidden().
		setDbName("OBJID");

		addressblk.addField("ADDRESS_OBJVERSION").
		setHidden().
		setDbName("OBJVERSION");

		addressblk.addField("ADDRESS_ID").
		setHidden();

		addressblk.addField("ADDRESS_WO_NO").
		setLabel("PCMWWORKORDERADDRESSWONO: WO No").
		setReadOnly().
		setDbName("WO_NO").
		setHidden().
		setMaxLength(8);

		ADDRESS1  =    addressblk.addField("ADDRESS1").
							setSize(29).
							setMandatory().
							setLabel("PCMWWORKORDERADDRESSADDRESS1: Address1");

		ADDRESS2  =    addressblk.addField("ADDRESS2").
							setSize(25).
							setLabel("PCMWWORKORDERADDRESSADDRESS2: Address2");

		ZIP_CODE  =    addressblk.addField("ZIP_CODE").
							setSize(29).
							setDbName("ADDRESS3").
							setLabel("PCMWWORKORDERADDRESSZIPCODE: Zip Code");

		CITY      =    addressblk.addField("CITY").
							setSize(25).
							setLabel("PCMWWORKORDERADDRESSCITY: City").
							setDbName("ADDRESS4").
							setDynamicLOV("CITY_CODE1_LOV","COUNTY,STATE,COUNTY");

		STATE     =    addressblk.addField("STATE").
							setSize(29).
							setLabel("PCMWWORKORDERADDRESSSTATE: State").
							setDbName("ADDRESS5").
							setDynamicLOV("STATE_CODE_LOV","COUNTY");

		COUNTY  =		addressblk.addField("COUNTY").
							setSize(25).
							setLabel("PCMWWORKORDERADDRESSCOUNTY: Country").
							setDbName("ADDRESS6").
							setDynamicLOV("COUNTY_CODE1_LOV","COUNTY,STATE");

		COUNTRY_CODE  =addressblk.addField("COUNTRY_CODE").
							setSize(25).
							setMandatory().
							//setDbName("ADDRESS7").
							setFunction("Work_Order_Address_API.Get_Address7(:ADDRESS_WO_NO)").
							setDynamicLOV("ISO_COUNTRY").
							setLabel("PCMWWORKORDERADDRESSADDRESSCOUNTRYCODE: Country Code");

		ADD_COUNTRY   	=addressblk.addField("ADD_COUNTRY").
							setSize(25).
							setMandatory().
							setLabel("PCMWWORKORDERADDRESSADDRESSCOUNTRY: Country").
							setFunction("Iso_Country_API.Get_Description(Work_Order_Address_API.Get_Address7(:ADDRESS_WO_NO))");


		addressblk.setView("WORK_ORDER_ADDRESS");
		addressblk.defineCommand("WORK_ORDER_ADDRESS_API","New__,Modify__");

		addressblk.disableDocMan();
		addressblk.setMasterBlock(headblk);

		addressset = addressblk.getASPRowSet();

		addressbar = mgr.newASPCommandBar(addressblk);
		addresstbl = mgr.newASPTable(addressblk);

		addresslay = addressblk.getASPBlockLayout();
		addresslay.setAddressFieldList(ADDRESS1,ADDRESS2,CITY,STATE,ZIP_CODE,COUNTY,COUNTRY_CODE,ADD_COUNTRY,"PCMWWORKORDERADDRESSADD: Address",null);

		addresslay.setDefaultLayoutMode(addresslay.SINGLE_LAYOUT);
		addressbar.defineCommand(addressbar.SAVERETURN,"saveReturn","checkAddressFields()");
		addressbar.defineCommand(addressbar.NEWROW,"newRowADDRESS");
		addressbar.defineCommand(addressbar.CANCELNEW,"cancelNew");

		addressbar.disableCommand(addressbar.DUPLICATEROW);
		addressbar.disableCommand(addressbar.FIND);
		addressbar.disableCommand(addressbar.DELETE);
		addressbar.disableCommand(addressbar.SAVENEW);
		
		if ((sViewName.indexOf("HISTORICAL")) != -1){
			addressbar.disableCommand(addressbar.EDITROW);
			addressbar.disableCommand(addressbar.NEWROW);
		}

	}

	public void  adjust()
	{
		if (addressbar.IsEnabled(addressbar.NEWROW) &&  addressset.countRows() != 0 ){
			addressbar.disableCommand(addressbar.NEWROW);
		}
	}

	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "PCMWWORKORDERADDRESSDLGDESC: Work Order Address Details";
	}


	protected String getTitle()
	{
		return "PCMWWORKORDERADDRESSDLGTITLE: Work Order Address Details";
	}


	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		appendToHTML(headlay.show()); 
		if (addresslay.isVisible())
			appendToHTML(addresslay.show());

		appendDirtyJavaScript("function checkAddressFields(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if (document.form.ADDRESS1.value != '')\n");
		appendDirtyJavaScript("   	return true;\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("    {   \n");
		appendDirtyJavaScript("        message = '");
		appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERADDRESSDMSSG: Enter value for Address"));
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("        window.alert(message);\n");
		appendDirtyJavaScript("    }\n");
		appendDirtyJavaScript("}\n");

	}
}
