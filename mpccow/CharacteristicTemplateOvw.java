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
* File        : CharacteristicTemplateOvw.java
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

public class CharacteristicTemplateOvw extends ASPPageProvider {

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CharacteristicTemplateOvw");

	//-----------------------------------------------------------------------------
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

	private ASPBlock char_templ_indiscrete_char_blk;
	private ASPRowSet char_templ_indiscrete_char_set;
	private ASPCommandBar char_templ_indiscrete_char_bar;
	private ASPTable char_templ_indiscrete_char_tbl;
	private ASPBlockLayout char_templ_indiscrete_char_lay;

	private ASPBlock char_templ_discrete_char_blk;
	private ASPRowSet char_templ_discrete_char_set;
	private ASPCommandBar char_templ_discrete_char_bar;
	private ASPTable char_templ_discrete_char_tbl;
	private ASPBlockLayout char_templ_discrete_char_lay;

	private ASPTabContainer tabs;

	//-----------------------------------------------------------------------------
	//------------------------  Construction  ---------------------------
	//-----------------------------------------------------------------------------

	public  CharacteristicTemplateOvw (ASPManager mgr, String page_path)
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
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("ENG_ATTRIBUTE")) )
			okFind();		

		tabs.saveActiveTab();

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
			mgr.showAlert("MPCCOWCHARACTERISTICTEMPLATEOVWNODATA: No data found.");
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

		cmd = trans.addEmptyCommand("HEAD","CHARACTERISTIC_TEMPLATE_API.New__",headblk);
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

		q = trans.addQuery(char_templ_indiscrete_char_blk);      
		q.addWhereCondition("ENG_ATTRIBUTE = ?");
		q.addParameter("ENG_ATTRIBUTE", headset.getValue("ENG_ATTRIBUTE"));      
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans,char_templ_indiscrete_char_blk);
		headset.goTo(headrowno);
	}

	public void newRowITEM1()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;      
		cmd = trans.addEmptyCommand("ITEM1","CHAR_TEMPL_INDISCRETE_CHAR_API.New__",char_templ_indiscrete_char_blk);
		cmd.setOption("ACTION","PREPARE");
		cmd.setParameter("ITEM0_ENG_ATTRIBUTE", headset.getValue("ENG_ATTRIBUTE"));      
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
		char_templ_indiscrete_char_set.addRow(data);
	}

	public void okFindITEM2()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPQuery q;
		int headrowno;

		q = trans.addQuery(char_templ_discrete_char_blk);
		q.addWhereCondition("ENG_ATTRIBUTE = ?");
		q.addParameter("ENG_ATTRIBUTE", headset.getValue("ENG_ATTRIBUTE"));      
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans,char_templ_discrete_char_blk);
		headset.goTo(headrowno);
	}

	public void newRowITEM2()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;         
		cmd = trans.addEmptyCommand("ITEM2","CHAR_TEMPL_DISCRETE_CHAR_API.New__",char_templ_discrete_char_blk);
		cmd.setOption("ACTION","PREPARE");
		cmd.setParameter("ITEM1_ENG_ATTRIBUTE", headset.getValue("ENG_ATTRIBUTE"));      
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM2/DATA");
		char_templ_discrete_char_set.addRow(data);
	}

	public void activateVariableTemplate()
	{      
		tabs.setActiveTab(1);
	}

	public void activateDiscreteTemplate()
	{       
		tabs.setActiveTab(2);
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

		headblk.addField("ENG_ATTRIBUTE").
			setMandatory().        
			setInsertable().      
			setReadOnly().         
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWENGATTRIBUTE: Characteristic Template").
			setSize(5);

		headblk.addField("DESCRIPTION").  
			setMandatory().       
			setInsertable().       
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWDESCRIPTION: Description"). 
			setSize(35);

		headblk.setView("CHARACTERISTIC_TEMPLATE");
		headblk.defineCommand("CHARACTERISTIC_TEMPLATE_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.addCustomCommand("activateVariableTemplate", "");
		headbar.addCustomCommand("activateDiscreteTemplate", "");
		headbar.removeCustomCommand("activateVariableTemplate");
		headbar.removeCustomCommand("activateDiscreteTemplate");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWCHARACTERISTICTEMPLATEOVWTBLHEAD: Characteristic Templates");     
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		char_templ_indiscrete_char_blk = mgr.newASPBlock("ITEM1");

		char_templ_indiscrete_char_blk.addField("ITEM0_OBJID").
			setHidden().    
			setDbName("OBJID");

		char_templ_indiscrete_char_blk.addField("ITEM0_OBJVERSION"). 
			setHidden().        
			setDbName("OBJVERSION");

		char_templ_indiscrete_char_blk.addField("ITEM0_ENG_ATTRIBUTE").
			setDbName("ENG_ATTRIBUTE").
			setMandatory().   
			setHidden().   
			setSize(5);

		char_templ_indiscrete_char_blk.addField("CHARACTERISTIC_CODE"). 
			setMandatory().    
			setDynamicLOV("INDISCRETE_CHARACTERISTIC").  
			setLOVProperty("TITLE","MPCCOWCHARACTERISTICTEMPLATEOVWINDISCHARCODELOVTITLE: List of Characteristic Code").
			setInsertable().    
			setReadOnly().          
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWCHARCODE: Characteristic Code").   
			setSize(5);

		char_templ_indiscrete_char_blk.addField("CHAR_DESC").   
			setFunction("CHARACTERISTIC_API.GET_DESCRIPTION ( :CHARACTERISTIC_CODE)").   
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWCHARDESC: Characteristic Code Description"). 
			setReadOnly(). 
			setSize(30);      

		char_templ_indiscrete_char_blk.addField("UNIT_MEAS").
			setInsertable().             
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWUNITMEAS: UoM"). 
			setDynamicLOV("ISO_UNIT").                      
			setLOVProperty("TITLE","MPCCOWCHARACTERISTICTEMPLATEOVWINDISUNTMESLOVTITLE").
			setSize(30);

		char_templ_indiscrete_char_blk.addField("CHAR_TEMP_DESC").  
			setFunction("CHARACTERISTIC_TEMPLATE_API.GET_DESCRIPTION ( :ITEM0_ENG_ATTRIBUTE)").
			setHidden().          
			setSize(30);      

		char_templ_indiscrete_char_blk.addField("CHAR_SEARCH_TYP").    
			setFunction("CHARACTERISTIC_API.GET_SEARCH_TYPE ( :CHARACTERISTIC_CODE)").  
			setHidden().         
			setSize(30);

		char_templ_indiscrete_char_blk.addField("ISO_UNIT_UNIT_TYPE").  
			setFunction("ISO_UNIT_API.GET_UNIT_TYPE ( :UNIT_MEAS)").  
			setHidden().               
			setSize(30);

		char_templ_indiscrete_char_blk.addField("ISO_UNIT_BASE_UNIT").
			setFunction("ISO_UNIT_API.GET_BASE_UNIT ( :UNIT_MEAS)").  
			setHidden().       
			setSize(30);

		getASPField("CHARACTERISTIC_CODE").setValidation("CHAR_DESC");
		char_templ_indiscrete_char_blk.setView("CHAR_TEMPL_INDISCRETE_CHAR");
		char_templ_indiscrete_char_blk.defineCommand("CHAR_TEMPL_INDISCRETE_CHAR_API","New__,Modify__,Remove__");
		char_templ_indiscrete_char_blk.setMasterBlock(headblk);

		char_templ_indiscrete_char_set = char_templ_indiscrete_char_blk.getASPRowSet();

		char_templ_indiscrete_char_bar = mgr.newASPCommandBar(char_templ_indiscrete_char_blk);
		char_templ_indiscrete_char_bar.defineCommand(char_templ_indiscrete_char_bar.OKFIND, "okFindITEM1");
		char_templ_indiscrete_char_bar.defineCommand(char_templ_indiscrete_char_bar.NEWROW, "newRowITEM1");

		char_templ_indiscrete_char_tbl = mgr.newASPTable(char_templ_indiscrete_char_blk);
		char_templ_indiscrete_char_tbl.setTitle("MPCCOWCHARACTERISTICTEMPLATEOVWINDISITEMHEAD1: Variable Template Characteristics");      
		char_templ_indiscrete_char_tbl.setWrap();

		char_templ_indiscrete_char_lay = char_templ_indiscrete_char_blk.getASPBlockLayout();
		char_templ_indiscrete_char_lay.setDefaultLayoutMode(char_templ_indiscrete_char_lay.MULTIROW_LAYOUT);

		char_templ_discrete_char_blk = mgr.newASPBlock("ITEM2");

		char_templ_discrete_char_blk.addField("ITEM1_OBJID").   
			setHidden(). 
			setDbName("OBJID");

		char_templ_discrete_char_blk.addField("ITEM1_OBJVERSION"). 
			setHidden().      
			setDbName("OBJVERSION");

		char_templ_discrete_char_blk.addField("ITEM1_ENG_ATTRIBUTE").  
			setDbName("ENG_ATTRIBUTE").  
			setMandatory().        
			setInsertable().     
			setHidden().      
			setSize(5);

		char_templ_discrete_char_blk.addField("ITEM1_CHAR_CODE"). 
			setDbName("CHARACTERISTIC_CODE").   
			setMandatory().   
			setInsertable(). 
			setReadOnly().    
			setDynamicLOV("DISCRETE_CHARACTERISTIC").   
			setLOVProperty("TITLE","MPCCOWCHARACTERISTICTEMPLATEOVWDISCHARCODELOVTITLE: List of Characteristic Code").
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWITEMCHARCODE: Characteristic Code").                                       
			setSize(5);

		char_templ_discrete_char_blk.addField("ITEM1_CHARC_DESC").   
			setFunction("CHARACTERISTIC_API.GET_DESCRIPTION ( :ITEM1_CHAR_CODE)").   
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWITEM1CHARDESC: Characteristic Code Description").  
			setReadOnly(). 
			setSize(30);      

		char_templ_discrete_char_blk.addField("ITEM1_UNIT_MEAS"). 
			setDbName("UNIT_MEAS").   
			setInsertable().    
			setLabel("MPCCOWCHARACTERISTICTEMPLATEOVWITEM1UNTMES: UoM"). 
			setDynamicLOV("ISO_UNIT").      
			setLOVProperty("TITLE","MPCCOWCHARACTERISTICTEMPLATEOVWDISUNTMESLOVTITLE"). 
			setSize(30);

		char_templ_discrete_char_blk.addField("ITEM1_CHAR_TEMP_DESC"). 
			setFunction("CHARACTERISTIC_TEMPLATE_API.GET_DESCRIPTION ( :ITEM1_ENG_ATTRIBUTE)").
			setHidden(). 
			setSize(30);      

		char_templ_discrete_char_blk.addField("ITEM1_CHAR_SEARCH_TYP").  
			setFunction("CHARACTERISTIC_API.GET_SEARCH_TYPE ( :ITEM1_CHAR_CODE)").  
			setHidden().     
			setSize(30);

		char_templ_discrete_char_blk.addField("ITEM1_ISO_UNIT_UNIT_TYPE").   
			setFunction("ISO_UNIT_API.GET_UNIT_TYPE ( :ITEM1_UNIT_MEAS)").   
			setHidden().      
			setSize(30);

		char_templ_discrete_char_blk.addField("ITEM1_ISO_UNIT_BASE_UNIT").
			setFunction("ISO_UNIT_API.GET_BASE_UNIT ( :ITEM1_UNIT_MEAS)").  
			setHidden().    
			setSize(30);

		getASPField("ITEM1_CHAR_CODE").setValidation("ITEM1_CHARC_DESC");
		char_templ_discrete_char_blk.setView("CHAR_TEMPL_DISCRETE_CHAR");

		char_templ_discrete_char_blk.defineCommand("CHAR_TEMPL_DISCRETE_CHAR_API","New__,Modify__,Remove__");
		char_templ_discrete_char_blk.setMasterBlock(headblk);

		char_templ_discrete_char_set = char_templ_discrete_char_blk.getASPRowSet();

		char_templ_discrete_char_bar = mgr.newASPCommandBar(char_templ_discrete_char_blk);
		char_templ_discrete_char_bar.defineCommand(char_templ_discrete_char_bar.OKFIND, "okFindITEM2");
		char_templ_discrete_char_bar.defineCommand(char_templ_discrete_char_bar.NEWROW, "newRowITEM2");

		char_templ_discrete_char_tbl = mgr.newASPTable(char_templ_discrete_char_blk);
		char_templ_discrete_char_tbl.setTitle("MPCCOWCHARACTERISTICTEMPLATEOVWITM1HEAD2: Discrete Template Characteristics");      
		char_templ_discrete_char_tbl.setWrap();

		char_templ_discrete_char_lay = char_templ_discrete_char_blk.getASPBlockLayout();
		char_templ_discrete_char_lay.setDefaultLayoutMode(char_templ_discrete_char_lay.MULTIROW_LAYOUT);

		tabs = mgr.newASPTabContainer();
		tabs.addTab("MPCCOWCHARACTERISTICTEMPLATEOVWVARTAB: Variable Template Characteristics", "javascript:commandSet('" + headblk.getName() +
						".activateVariableTemplate', '')");
		tabs.addTab("MPCCOWCHARACTERISTICTEMPLATEOVWDISCTAB: Discrete Template Characteristics", "javascript:commandSet('" + headblk.getName() +
						".activateDiscreteTemplate', '')");
	}  

	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "MPCCOWCHARACTERISTICTEMPLATEOVWDESC: Characteristic Template";
	}


	protected String getTitle()
	{
		return "MPCCOWCHARACTERISTICTEMPLATEOVWTITLE: Characteristic Template";
	} 

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		if (headlay.isVisible()) {
			appendToHTML(headlay.show());   
		}
		if (headlay.isVisible() && char_templ_indiscrete_char_lay.isVisible()) {
			appendToHTML(tabs.showTabsInit());   
		}

		switch (tabs.getActiveTab()) {
		case 1: if (char_templ_indiscrete_char_lay.isVisible())
				appendToHTML(char_templ_indiscrete_char_lay.show());
			break;
		case 2: if (char_templ_discrete_char_lay.isVisible())
				appendToHTML(char_templ_discrete_char_lay.show());
			break;
		}
	}  

}
