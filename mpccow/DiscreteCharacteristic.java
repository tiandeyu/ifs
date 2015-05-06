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
* File        : DiscreteCharacteristic.java
* Modified    : 
*------------------ Defcon ---------------------------------------------------
* 080930 Imfelk	Bug 77272: Created.
*-----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.mpccow;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class DiscreteCharacteristic extends ASPPageProvider {

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.DiscreteCharacteristic");

	//-------------------------syn----------------------------------------------------
	//---------- Header Instances created on page creation --------
	//-----------------------------------------------------------------------------

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//-----------------------------------------------------------------------------
	//---------- Item Instances created on page creation --------
	//-----------------------------------------------------------------------------

	private ASPBlock discrete_charac_value_blk;
	private ASPRowSet discrete_charac_value_set;
	private ASPCommandBar discrete_charac_value_bar;
	private ASPTable discrete_charac_value_tbl;
	private ASPBlockLayout discrete_charac_value_lay;


	//-----------------------------------------------------------------------------
	//------------------------  Construction  ---------------------------
	//-----------------------------------------------------------------------------

	public  DiscreteCharacteristic (ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run()
	{
		ASPManager mgr = getASPManager();

		if ( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if (mgr.dataTransfered())
			okFind();
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
			okFind();
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("CHARACTERISTIC_CODE")) )
			okFind();
	}
	//-----------------------------------------------------------------------------
	//------------------------  Command Bar functions  ---------------------------
	//-----------------------------------------------------------------------------

	public void okFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		mgr.createSearchURL(headblk);
		q = trans.addQuery(headblk);
		q.includeMeta("ALL");
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		mgr.querySubmit(trans,headblk);
		if (  headset.countRows() == 0 ) {
			mgr.showAlert("MPCCOWDISCRETECHARACTERISTICNODATA: No data found.");
			headset.clear();
		}
		eval( headset.syncItemSets() );
	}      

	public void countFind()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;

		q = trans.addQuery(headblk);
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);
		headlay.setCountValue(toInt(headset.getValue("N")));
		headset.clear();
	}

	public void newRow()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPBuffer data;
		ASPCommand cmd;

		cmd = trans.addEmptyCommand("HEAD","DISCRETE_CHARACTERISTIC_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}


	//-----------------------------------------------------------------------------
	//------------------------  Item block cmd bar functions  ---------------------------
	//-----------------------------------------------------------------------------


	public void okFindITEM1()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		int headrowno;

		q = trans.addQuery(discrete_charac_value_blk);
		q.addWhereCondition("CHARACTERISTIC_CODE = ?");
		q.addParameter("CHARACTERISTIC_CODE", headset.getValue("CHARACTERISTIC_CODE"));
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans,discrete_charac_value_blk);
		headset.goTo(headrowno);
	}

	public void newRowITEM1()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;

		cmd = trans.addEmptyCommand("ITEM1","DISCRETE_CHARAC_VALUE_API.New__",discrete_charac_value_blk);
		cmd.setOption("ACTION","PREPARE");
		cmd.setParameter("ITEM_CHAR_CODE", headset.getValue("CHARACTERISTIC_CODE"));
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
		discrete_charac_value_set.addRow(data);
	}

	//-----------------------------------------------------------------------------
	//------------------------  Predefines Head ---------------------------
	//-----------------------------------------------------------------------------

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("MAIN");

		headblk.addField("OBJID").   
			setHidden();

		headblk.addField("OBJVERSION"). 
			setHidden();

		headblk.addField("CHARACTERISTIC_CODE").
			setMandatory().              
			setInsertable().           
			setReadOnly().                   
			setLabel("MPCCOWDISCRETECHARACTERISTICCHARCCODE: Characteristic Code").   
			setSize(5);

		headblk.addField("DESCRIPTION").   
			setMandatory().                
			setInsertable().                  
			setLabel("MPCCOWDISCRETECHARACTERISTICDESCRIPTION: Characteristic Code Description").    
			setSize(35);

		headblk.addField("SEARCH_TYPE").  
			enumerateValues("Alpha_Numeric_API").  
			setSelectBox().                     
			setMandatory().             
			setInsertable().               
			setLabel("MPCCOWDISCRETECHARACTERISTICSEARCHTYPE: Alpha/Num").   
			setSize(30);

		headblk.addField("CHARACTERISTIC_DESCRIPTION"). 
			setFunction("CHARACTERISTIC_API.GET_DESCRIPTION ( :CHARACTERISTIC_CODE)"). 
			setHidden().                                                           
			setSize(30);

		headblk.addField("CHARACTERISTIC_SEARCH_TYPE").  
			setFunction("CHARACTERISTIC_API.GET_SEARCH_TYPE ( :CHARACTERISTIC_CODE)").  
			setHidden().     
			setSize(30);

		headblk.setView("DISCRETE_CHARACTERISTIC");
		headblk.defineCommand("DISCRETE_CHARACTERISTIC_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWDISCRETECHARACTERISTICTBLHEAD: Discrete Characteristics");		
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 

		discrete_charac_value_blk = mgr.newASPBlock("ITEM1");

		discrete_charac_value_blk.addField("ITEM0_OBJID").  
			setHidden().                           
			setDbName("OBJID");

		discrete_charac_value_blk.addField("ITEM0_OBJVERSION").
			setHidden().                
			setDbName("OBJVERSION");

		discrete_charac_value_blk.addField("ITEM_CHAR_CODE").  
			setDbName("CHARACTERISTIC_CODE"). 
			setMandatory().              
			setInsertable().           
			setHidden().             
			setSize(5);

		discrete_charac_value_blk.addField("CHARACTERISTIC_VALUE").  
			setMandatory().                          
			setInsertable().                       
			setReadOnly().                           
			setLabel("MPCCOWDISCRETECHARACTERISTICCHARVALUE: Characteristic Value").   
			setSize(60);

		discrete_charac_value_blk.addField("CHARACTERISTIC_VALUE_DESC").  
			setMandatory().     
			setInsertable().         
			setLabel("MPCCOWDISCRETECHARACTERISTICVALCHARVALUEDESC: Characteristic Value Description"). 
			setSize(120);    

		discrete_charac_value_blk.setView("DISCRETE_CHARAC_VALUE");
		discrete_charac_value_blk.defineCommand("DISCRETE_CHARAC_VALUE_API","New__,Modify__,Remove__");
		discrete_charac_value_blk.setMasterBlock(headblk);

		discrete_charac_value_set = discrete_charac_value_blk.getASPRowSet();

		discrete_charac_value_bar = mgr.newASPCommandBar(discrete_charac_value_blk);
		discrete_charac_value_bar.defineCommand(discrete_charac_value_bar.OKFIND, "okFindITEM1");
		discrete_charac_value_bar.defineCommand(discrete_charac_value_bar.NEWROW, "newRowITEM1");

		discrete_charac_value_tbl = mgr.newASPTable(discrete_charac_value_blk);
		discrete_charac_value_tbl.setTitle("MPCCOWDISCRETECHARACTERISTICITEMHEAD1: Discrete Characteristics Values");		
		discrete_charac_value_tbl.setWrap();          
		
		discrete_charac_value_lay = discrete_charac_value_blk.getASPBlockLayout();
		discrete_charac_value_lay.setDefaultLayoutMode(discrete_charac_value_lay.MULTIROW_LAYOUT);
	} 

	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "MPCCOWDISCRETECHARACTERISTICDESC: Discrete Characteristic";
	}


	protected String getTitle()
	{
		return "MPCCOWDISCRETECHARACTERISTICTITLE: Discrete Characteristic";
	}


	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		if (headlay.isVisible())
			appendToHTML(headlay.show());
		if (discrete_charac_value_lay.isVisible())
			appendToHTML(discrete_charac_value_lay.show());

	}
}
