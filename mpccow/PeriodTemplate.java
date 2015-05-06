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
* File                          : PeriodTemplate.java
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

public class PeriodTemplate extends ASPPageProvider {

	//-----------------------------------------------------------------------------
	//---------- Static constants ------------------------------------------------
	//-----------------------------------------------------------------------------

	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.PeriodTemplate");

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

	private ASPBlock period_template_detail_blk;
	private ASPRowSet period_template_detail_set;
	private ASPCommandBar period_template_detail_bar;
	private ASPTable period_template_detail_tbl;
	private ASPBlockLayout period_template_detail_lay;

	private String URLString;
	private String dflag;
	private String WindowName;
	private String performRMB;   

	//-----------------------------------------------------------------------------
	//------------------------  Construction  ---------------------------
	//-----------------------------------------------------------------------------

	public  PeriodTemplate (ASPManager mgr, String page_path)
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
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("TEMPLATE_ID")) )
			okFind();
		else if ("TRUE".equals(mgr.readValue("REFRESH_FLAG")))
			refreshData(mgr.readValue("REFRESH_BLOCK"), mgr.readValue("REFRESH_ROW"));
		
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
			mgr.showAlert("PERIODTEMPLATENODATA: No data found.");
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

		cmd = trans.addEmptyCommand("HEAD","PERIOD_TEMPLATE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);
		data = trans.getBuffer("HEAD/DATA");
		String sContract = trans.getValue("HEAD/DATA/CONTRACT");

		trans.clear();
		cmd = trans.addCustomFunction("CDESC","COMPANY_SITE_API.GET_DESCRIPTION","COMPANY_SITE_DESCRIPTION");
		cmd.setParameter("CONTRACT", sContract);

		trans = mgr.validate(trans);
		String sDesc = trans.getValue("CDESC/DATA/COMPANY_SITE_DESCRIPTION");
		headset.addRow(data);
		headset.setValue("COMPANY_SITE_DESCRIPTION",sDesc);
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

		q = trans.addQuery(period_template_detail_blk);
		q.addWhereCondition("CONTRACT = ? AND TEMPLATE_ID = ? AND PERIOD_NO > 0");
		q.addParameter("CONTRACT", headset.getValue("CONTRACT"));
		q.addParameter("TEMPLATE_ID", headset.getValue("TEMPLATE_ID"));
		q.includeMeta("ALL");
		headrowno = headset.getCurrentRowNo();
		mgr.querySubmit(trans,period_template_detail_blk);
		headset.goTo(headrowno);
	}

	public void newRowITEM1()
	{
		ASPManager mgr = getASPManager();
		ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
		ASPCommand cmd;
		ASPBuffer data;

		cmd = trans.addCustomFunction("PLANPERIODMAXPERIOD","PERIOD_TEMPLATE_DETAIL_API.Get_Max_Period_No","PERIOD_NO");
		cmd.setParameter("ITEM0_CONTRACT", headset.getValue("CONTRACT"));
		cmd.setParameter("ITEM0_TEMPLATE_ID", headset.getValue("TEMPLATE_ID"));

		trans = mgr.validate(trans);
		String sPeriodNo = trans.getValue("PLANPERIODMAXPERIOD/DATA/PERIOD_NO");
		sPeriodNo = Integer.toString(Integer.parseInt(sPeriodNo)+1);

		trans.clear();

		cmd = trans.addEmptyCommand("ITEM1","PERIOD_TEMPLATE_DETAIL_API.New__",period_template_detail_blk);
		cmd.setOption("ACTION","PREPARE");
		cmd.setParameter("ITEM0_CONTRACT", headset.getValue("CONTRACT"));
		cmd.setParameter("ITEM0_TEMPLATE_ID", headset.getValue("TEMPLATE_ID"));      
		cmd.setParameter("PERIOD_NO", sPeriodNo);    
		trans = mgr.perform(trans);
		data = trans.getBuffer("ITEM1/DATA");
		period_template_detail_set.addRow(data);      
	}

	public void  createPeriods()
	{

		ASPManager mgr = getASPManager();
		ASPCommand cmd;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		URLString =  "CreatePeriodsDlg.page?CONTRACT="+ mgr.URLEncode(headset.getValue("CONTRACT"))+
						 "&COMPANY_SITE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("COMPANY_SITE_DESCRIPTION"))+
						 "&TEMPLATE_ID="+ mgr.URLEncode(headset.getValue("TEMPLATE_ID"))+        
						 "&TEMPLATE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("TEMPLATE_DESCRIPTION"));

		performRMB   = "TRUE";
		WindowName = "CREATEPERIODS";      
	}

	public void  deletePeriods()
	{

		ASPManager mgr = getASPManager();
		ASPCommand cmd;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		URLString =  "DeletePeriodsDlg.page?CONTRACT="+ mgr.URLEncode(headset.getValue("CONTRACT"))+
						 "&COMPANY_SITE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("COMPANY_SITE_DESCRIPTION"))+
						 "&TEMPLATE_ID="+ mgr.URLEncode(headset.getValue("TEMPLATE_ID"))+        
						 "&TEMPLATE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("TEMPLATE_DESCRIPTION"));

		performRMB   = "TRUE";
		WindowName = "DELETEPERIODS";      
	}

	public void  copyTemplate()
	{

		ASPManager mgr = getASPManager();
		ASPCommand cmd;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		URLString =  "CopyTemplateDlg.page?CONTRACT="+ mgr.URLEncode(headset.getValue("CONTRACT"))+
						 "&COMPANY_SITE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("COMPANY_SITE_DESCRIPTION"))+
						 "&TEMPLATE_ID="+ mgr.URLEncode(headset.getValue("TEMPLATE_ID"))+        
						 "&TEMPLATE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("TEMPLATE_DESCRIPTION"));


		performRMB   = "TRUE";
		WindowName = "COPYTEMPLATE";      
	}

	public void  curTempRecal()
	{
		ASPManager mgr = getASPManager();
		ASPCommand cmd;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		URLString =  "CurTempRecalcDlg.page?CONTRACT="+ mgr.URLEncode(headset.getValue("CONTRACT"))+
						 "&COMPANY_SITE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("COMPANY_SITE_DESCRIPTION"))+
						 "&TEMPLATE_ID="+ mgr.URLEncode(headset.getValue("TEMPLATE_ID"))+        
						 "&TEMPLATE_DESCRIPTION="+ mgr.URLEncode(headset.getValue("TEMPLATE_DESCRIPTION"))+
						 "&CALENDAR_ID="+ mgr.URLEncode(headset.getValue("CALENDAR_ID"));

		performRMB   = "TRUE";
		WindowName = "CURRENTTEMPLATERECALC";      
	}

	public void  allTempRecalc()
	{

		ASPManager mgr = getASPManager();
		ASPCommand cmd;

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());

		URLString =  "AllTempRecalcDlg.page?CONTRACT="+ mgr.URLEncode(headset.getValue("CONTRACT"));              

		performRMB   = "TRUE";
		WindowName = "ATTTEMPLATERECALC";      
	}

	public void refreshData(String sRefreshBlock, String sRefreshRow)
	{
		/*
		 This method is used to refresh a particular block or record
		 when returned from a RMB action.
		*/

		ASPManager mgr = getASPManager();

		int nRefreshRow = 0;

		if ( !mgr.isEmpty(sRefreshRow) && !("null".equals(sRefreshRow)) )
			nRefreshRow = Integer.parseInt(sRefreshRow);

		if ( isNaN(nRefreshRow) )
			nRefreshRow = 0;

		if ( headset.countRows() > 0 ) {
			if ( "MAIN".equals(sRefreshBlock) ) {
				headset.goTo(nRefreshRow);
				headset.refreshAllRows();  
				okFindITEM1();
			}
		}
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

		headblk.addField("CONTRACT").  
			setMandatory().        
			setInsertable().         
			setReadOnly().                
			setLabel("MPCCOWPERIODTEMPLATECONTRACT: Template Site").     
			setDynamicLOV("USER_ALLOWED_SITE_LOV").              
			setLOVProperty("TITLE","PERIODTEMPLATECONTRACTLOVTITLE: List of Site").  
			setSize(5);

		headblk.addField("COMPANY_SITE_DESCRIPTION"). 
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :CONTRACT)"). 
			setReadOnly().                                                    
			setLabel("MPCCOWPERIODTEMPLATECOMPANYSITEDESCRIPTION: Site Description").  
			setSize(30);

		headblk.addField("TEMPLATE_ID","Number").   
			setMandatory().                    
			setInsertable().               
			setReadOnly().                   
			setLabel("MPCCOWPERIODTEMPLATETEMPLATEID: Template ID").      
			setSize(30);

		headblk.addField("TEMPLATE_DESCRIPTION").      
			setMandatory().                                
			setInsertable().                           
			setLabel("MPCCOWPERIODTEMPLATETEMPLATEDESCRIPTION: Template Description").  
			setSize(0);

		headblk.addField("CALENDAR_ID").  
			setMandatory().              
			setInsertable().        
			setReadOnly().            
			setLabel("MPCCOWPERIODTEMPLATECALENDARID: Calendar ID").    
			setDynamicLOV("WORK_TIME_CALENDAR").             
			setLOVProperty("TITLE","List of Calendar ID").  
			setSize(0);

		headblk.addField("CALENDAR_DESC").  
			setLabel("MPCCOWPERIODTEMPLATECALENDARDESC: Description").   
			setReadOnly().                                                 
			setFunction("WORK_TIME_CALENDAR_API.GET_DESCRIPTION ( :CALENDAR_ID)").    
			setSize(30);

		headblk.addField("RECALCULATION_DATE","Date").    
			setMandatory().              
			setInsertable().           
			setReadOnly().                  
			setLabel("MPCCOWPERIODTEMPLATERECALCULATIONDATE: Period 1 Begin Date").     
			setSize(30);
		
		getASPField("CONTRACT").setValidation("COMPANY_SITE_DESCRIPTION");
		getASPField("CALENDAR_ID").setValidation("CALENDAR_DESC");

		headblk.setView("PERIOD_TEMPLATE");
		headblk.defineCommand("PERIOD_TEMPLATE_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);        
		headbar.addCustomCommand("createPeriods","MPCCOWPERIODTEMPLATECREATEPERIOD: Create Periods...");
		headbar.addCustomCommand("deletePeriods","MPCCOWPERIODTEMPLATEDELETEPERIOD: Delete Periods...");
		headbar.addCustomCommand("copyTemplate","MPCCOWPERIODTEMPLATECOPYCURRENTTEMPLATE: Copy Current Template...");
		headbar.addCustomCommand("curTempRecal","MPCCOWPERIODTEMPLATERECALCCURRENTTEMPLATE: Recalculate Current Template Details...");
		headbar.addCustomCommand("allTempRecalc","MPCCOWPERIODTEMPLATERECALCALLTEMPLATE: Recalculate all Template Details for Site...");

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWPERIODTEMPLATETBLHEAD: Period Templates");      
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

		period_template_detail_blk = mgr.newASPBlock("ITEM1");

		period_template_detail_blk.addField("ITEM0_OBJID").  
			setHidden().                
			setDbName("OBJID");

		period_template_detail_blk.addField("ITEM0_OBJVERSION").   
			setHidden().                                       
			setDbName("OBJVERSION");

		period_template_detail_blk.addField("ITEM0_CONTRACT").  
			setDbName("CONTRACT").                     
			setMandatory().                                 
			setHidden().                                       
			setSize(5);

		period_template_detail_blk.addField("ITEM0_TEMPLATE_ID","Number").  
			setDbName("TEMPLATE_ID").                    
			setMandatory().        
			setHidden().         
			setSize(30);

		period_template_detail_blk.addField("PERIOD_NO","Number"). 
			setMandatory().                                       
			setReadOnly().                                             
			setLabel("MPCCOWPERIODTEMPLATEDETAILPERIODNO: Period No").  
			setSize(30);

		period_template_detail_blk.addField("PERIOD_LENGTH","Number").  
			setMandatory().       
			setInsertable().           
			setLabel("MPCCOWPERIODTEMPLATEDETAILPERIODLENGTH: Period Length").      
			setSize(30);

		period_template_detail_blk.addField("PLAN_PERIOD_UNIT"). 
			enumerateValues("Plan_Period_Unit_API").          
			setSelectBox().                                 
			setMandatory().                               
			setInsertable().                           
			setLabel("MPCCOWPERIODTEMPLATEDETAILPLANPERIODUNIT: Plan Period Unit").  
			setSize(200);

		period_template_detail_blk.addField("LENGTH_IN_WORK_DAYS","Number").   
			setLabel("MPCCOWPERIODTEMPLATEDETAILLENGTHINWORKDAYS: Length In Work Days").  
			setReadOnly().   
			setSize(30);      

		period_template_detail_blk.addField("PREVIOUS_LENGTH","Number").    
			setMandatory().               
			setHidden().      
			setSize(30);

		period_template_detail_blk.addField("PERIOD_BEGIN_COUNTER","Number").  
			setHidden().          
			setSize(30);

		period_template_detail_blk.addField("PERIOD_END_COUNTER","Number"). 
			setMandatory().     
			setHidden(). 
			setSize(30);      

		period_template_detail_blk.addField("PERIOD_BEGIN_DATE","Date"). 
			setLabel("MPCCOWPERIODTEMPLATEDETAILLENGTHINWORKDAYS: Period Begin Date"). 
			setFunction("WORK_TIME_CALENDAR_API.GET_WORK_DAY ( PERIOD_TEMPLATE_API.GET_CALENDAR_ID ( :ITEM0_CONTRACT,:ITEM0_TEMPLATE_ID), :PERIOD_BEGIN_COUNTER)").
			setReadOnly().   
			setSize(30);

		period_template_detail_blk.addField("PERIOD_END_DATE","Date").
			setLabel("MPCCOWPERIODTEMPLATEDETAILLENGTHINWORKDAYS: Period End Date").
			setFunction("WORK_TIME_CALENDAR_API.GET_WORK_DAY ( PERIOD_TEMPLATE_API.GET_CALENDAR_ID ( :ITEM0_CONTRACT,:ITEM0_TEMPLATE_ID), :PERIOD_END_COUNTER)").
			setReadOnly().              
			setSize(30);

		period_template_detail_blk.setView("PERIOD_TEMPLATE_DETAIL");            
		period_template_detail_blk.defineCommand("PERIOD_TEMPLATE_DETAIL_API","New__,Modify__,Remove__");
		period_template_detail_blk.setMasterBlock(headblk);

		period_template_detail_set = period_template_detail_blk.getASPRowSet();

		period_template_detail_bar = mgr.newASPCommandBar(period_template_detail_blk);
		//period_template_detail_bar.defineCommand(period_template_detail_bar.OKFIND, "okFindITEM1");
		//period_template_detail_bar.defineCommand(period_template_detail_bar.NEWROW, "newRowITEM1");

		period_template_detail_tbl = mgr.newASPTable(period_template_detail_blk);
		period_template_detail_tbl.setTitle("MPCCOWPERIODTEMPLATEDETAILITEMHEAD1: Period Template Details");      
		period_template_detail_tbl.setWrap();

		period_template_detail_lay = period_template_detail_blk.getASPBlockLayout();
		period_template_detail_lay.setDefaultLayoutMode(period_template_detail_lay.MULTIROW_LAYOUT);
	}  

	//-----------------------------------------------------------------------------
	//------------------------  Presentation functions  ---------------------------
	//-----------------------------------------------------------------------------

	protected String getDescription()
	{
		return "MPCCOWPERIODTEMPLATEDESC: Period Template Maintenance";
	}


	protected String getTitle()
	{
		return "MPCCOWPERIODTEMPLATETITLE: Period Template Maintenance";
	}	

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		if (headlay.isVisible())
			appendToHTML(headlay.show());
		if (period_template_detail_lay.isVisible() && headlay.isSingleLayout())
			appendToHTML(period_template_detail_lay.show());

		printHiddenField("REFRESH_FLAG","");
		printHiddenField("REFRESH_BLOCK",""); 

		appendDirtyJavaScript("window.name = \"PeriodTemplate\"\n");

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB));
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");       

		appendDirtyJavaScript("  url_to_go = '");         
		appendDirtyJavaScript(mgr.encodeStringForJavascript(URLString));
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window_name = '");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(WindowName));
		appendDirtyJavaScript("';\n");         
		appendDirtyJavaScript("  showNewBrowser(url_to_go,window_name,\"resizable,alwaysRaised=yes,status=yes,scrollbars=yes,width=800,height=600\");\n");
		appendDirtyJavaScript("\n");

		appendDirtyJavaScript("function refreshPeriodTemplateDialog()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   f.REFRESH_BLOCK.value = 'MAIN';\n");
		appendDirtyJavaScript("   refreshData();\n");
		appendDirtyJavaScript("}\n");  
		appendDirtyJavaScript("} \n");

		appendDirtyJavaScript("function refreshData()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
		appendDirtyJavaScript("   f.submit();\n");
		appendDirtyJavaScript("}\n");

	}
}

