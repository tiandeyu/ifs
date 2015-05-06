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
* File        : IndiscreteCharacteristicOvw.java
* Modified    : 
*------------------ Defcon ---------------------------------------------------
* 080930 Imfelk	Bug 77272: Created.
* ----------------------------------------------------------------------------
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

public class IndiscreteCharacteristicOvw extends ASPPageProvider {

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.IndiscreteCharacteristicOvw");

	//-----------------------------------------------------------------------------
	//---------- Header Instances created on page creation --------
	//-----------------------------------------------------------------------------

	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//-----------------------------------------------------------------------------
	//------------------------  Construction  ---------------------------
	//-----------------------------------------------------------------------------

	public  IndiscreteCharacteristicOvw (ASPManager mgr, String page_path)
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
			mgr.showAlert("MPCCOWINDISCRETECHARACTERISTICOVWNODATA: No data found.");
			headset.clear();
		}
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

		cmd = trans.addEmptyCommand("HEAD","INDISCRETE_CHARACTERISTIC_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
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
			setLabel("MPCCOWINDISCRETECHARACTERISTICOVWCHARACTERISTICCODE: Characteristic Code").  
			setSize(5);

		headblk.addField("DESCRIPTION").  
			setMandatory().              
			setInsertable().                 
			setLabel("MPCCOWINDISCRETECHARACTERISTICOVWDESCRIPTION: Characteristic Code Description"). 
			setSize(35);

		headblk.addField("SEARCH_TYPE"). 
			enumerateValues("Alpha_Numeric_API").     
			setSelectBox().                         
			setMandatory().                 
			setInsertable().                     
			setLabel("MPCCOWINDISCRETECHARACTERISTICOVWSEARCHTYPE: Alpha/Num"). 
			setSize(30);

		headblk.addField("CHARACTERISTIC_DESCRIPTION").
			setFunction("CHARACTERISTIC_API.GET_DESCRIPTION ( :CHARACTERISTIC_CODE)").
			setHidden().       
			setSize(30);

		headblk.addField("CHARACTERISTIC_SEARCH_TYPE"). 
			setFunction("CHARACTERISTIC_API.GET_SEARCH_TYPE ( :CHARACTERISTIC_CODE)").  
			setHidden().                          
			setSize(30);

		headblk.setView("INDISCRETE_CHARACTERISTIC");
		headblk.defineCommand("INDISCRETE_CHARACTERISTIC_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWINDISCRETECHARACTERISTICOVWTBLHEAD: Variable Characteristics");		
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}  

	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "MPCCOWINDISCRETECHARACTERISTICOVWDESC: Variable Characteristic";
	}


	protected String getTitle()
	{
		return "MPCCOWINDISCRETECHARACTERISTICOVWTITLE: Variable Characteristic";
	}


	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		if (headlay.isVisible())
			appendToHTML(headlay.show());
	}
}
