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
* File        : StatisticPeriodOvw.java
* Modified    : 
*--------------------------------- Defcon ------------------------------------
* 080930 Imfelk	Bug 77272: Created.
* 090702 SuThlk	Bug 83940: Added method adjust to hide PERIOD_CLOSED when adding new record.
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

public class StatisticPeriodOvw extends ASPPageProvider {

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.StatisticPeriodOvw");

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

	public  StatisticPeriodOvw (ASPManager mgr, String page_path)
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
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("STAT_YEAR_NO")) )
			okFind();
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("STAT_PERIOD_NO")) )
			okFind();
      
		// Bug 83940, start
		adjust();
		// Bug 83940, end
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
			mgr.showAlert("MPCCOWSTATISTICPERIODOVWNODATA: No data found.");
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

		cmd = trans.addEmptyCommand("HEAD","STATISTIC_PERIOD_API.New__",headblk);
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

		headblk.addField("STAT_YEAR_NO","Number"). 
			setMandatory().                  
			setInsertable().                      
			setLabel("MPCCOWSTATISTICPERIODOVWSTATYEARNO: Statistical Year").   
			setSize(30);

		headblk.addField("STAT_PERIOD_NO","Number").   
			setMandatory().        
			setInsertable().         
			setLabel("MPCCOWSTATISTICPERIODOVWSTATPERIODNO: Statistical Period").   
			setSize(30);

		headblk.addField("DESCRIPTION").  
			setInsertable().                
			setLabel("MPCCOWSTATISTICPERIODOVWDESCRIPTION: Description").    
			setSize(35);

		headblk.addField("BEGIN_DATE","Date"). 
			setMandatory().                
			setInsertable().                      
			setLabel("MPCCOWSTATISTICPERIODOVWBEGINDATE: Valid From").    
			setSize(30);

		headblk.addField("END_DATE","Date").     
			setMandatory().                          
			setInsertable().           
			setLabel("MPCCOWSTATISTICPERIODOVWENDDATE: Valid To").   
			setSize(30);

		headblk.addField("PERIOD_CLOSED").   
			enumerateValues("Period_Closed_API"). 
			setSelectBox(). 
			setInsertable().
			setMandatory().         
			setLabel("MPCCOWSTATISTICPERIODOVWPERIODCLOSED: Period Closed").  
			setSize(30);

		headblk.setView("STATISTIC_PERIOD");
		headblk.defineCommand("STATISTIC_PERIOD_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
      // Bug 83940, start
      headbar.defineCommand(headbar.DUPLICATEROW, "duplicateRow");
      // Bug 83940, end

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWSTATISTICPERIODOVWTBLHEAD: Statistic Periods for Inventory and Distribution ");      
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
	}   
   
	// Bug 83940, start
	public void  adjust()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isNewLayout()){
			mgr.getASPField("PERIOD_CLOSED").setHidden();
		}
	}
   
   public void duplicateRow(){
      ASPManager mgr = getASPManager();
   	ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
   	ASPCommand cmd;
   	ASPBuffer data;
      
   	if (headlay.isMultirowLayout())
    	   headset.goTo(headset.getRowSelected());
      else
      	headset.selectRow();
      
      headlay.setLayoutMode(headlay.NEW_LAYOUT);
      
      cmd = trans.addEmptyCommand("HEAD","STATISTIC_PERIOD_API.New__", headblk );
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      
      data.setFieldItem("STAT_YEAR_NO",headset.getValue("STAT_YEAR_NO"));
      data.setFieldItem("STAT_PERIOD_NO",headset.getValue("STAT_PERIOD_NO"));
      data.setFieldItem("DESCRIPTION",headset.getValue("DESCRIPTION"));
      data.setFieldDateItem("BEGIN_DATE",headset.getDateValue("BEGIN_DATE"));
      data.setFieldDateItem("END_DATE",headset.getDateValue("END_DATE"));
      data.setFieldItem("PERIOD_CLOSED","");
      
      headset.addRow(data);
   }
	// Bug 83940, end
   
	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "MPCCOWSTATISTICPERIODOVWDESC: Statistic Periods for Inventory and Distribution ";
	}


	protected String getTitle()
	{
		return "MPCCOWSTATISTICPERIODOVWTITLE: Statistic Periods for Inventory and Distribution ";
	}


	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		if (headlay.isVisible())
			appendToHTML(headlay.show());

	}
}
