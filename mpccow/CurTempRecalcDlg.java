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
* File        : CurTempRecalcDlg.java
* Modified    : 
*------------------ Defcon ---------------------------------------------------
* 080930 Imfelk	Bug 77272: Created.
*-----------------------------------------------------------------------------
*/

package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CurTempRecalcDlg extends ASPPageProvider {

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CurTempRecalcDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPTable headtbl;
	private ASPCommandBar headbar;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;  
	private String dflag;     	

	//===============================================================
	// Construction 
	//===============================================================     
	public CurTempRecalcDlg(ASPManager mgr, String page_path) {
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager(); 

		trans = mgr.newASPTransactionBuffer();      

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (mgr.buttonPressed("BOK"))
			bOk();
		else if (mgr.buttonPressed("BCANCEL"))
			cancel();		
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")))
			setDefaults();
	}

	public void  setDefaults()
	{
		ASPManager mgr = getASPManager();
		trans.clear();

		String sContract = mgr.getQueryStringValue("CONTRACT");
		String sCompSiteDesc = mgr.getQueryStringValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.getQueryStringValue("TEMPLATE_ID");
		String sTempDesc = mgr.getQueryStringValue("TEMPLATE_DESCRIPTION");
		String sCalendar = mgr.getQueryStringValue("CALENDAR_ID");

		cmd = trans.addCustomFunction("PLANPERIODMAXPERIOD","SITE_API.Get_Site_Date","BEGIN_DATE");
		cmd.addParameter("CONTRACT",sContract);
		trans = mgr.perform(trans);
		String sBeginDate = trans.getValue("PLANPERIODMAXPERIOD/DATA/BEGIN_DATE");

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("BEGIN_DATE",sBeginDate);      
		headset.setValue("CALENDAR_ID",sCalendar);

	}

	public void  bOk()
	{
		ASPManager mgr = getASPManager();   

		String sContract = mgr.readValue("CONTRACT");
		String sCompSiteDesc = mgr.readValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.readValue("TEMPLATE_ID");
		String sTempDesc  = mgr.readValue("TEMPLATE_DESCRIPTION");
		String sBeginDate = mgr.readValue("BEGIN_DATE");     
		String sCalendar = mgr.readValue("CALENDAR_ID");  

		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("BEGIN_DATE", sBeginDate);
		headset.setValue("CALENDAR_ID", sCalendar);

		if ( "".equals(sContract) ) {
			dflag = "false";
		} else {
			trans.clear();           

			cmd = trans.addCustomCommand("TEMPLATERECALC", "PERIOD_TEMPLATE_MANAGER_API.Recalculate_Template");
			cmd.addParameter("CONTRACT", sContract);           
			cmd.addParameter("TEMPLATE_ID", sTempId);          
			cmd.addParameter("BEGIN_DATE", sBeginDate);
			cmd.addParameter("CALENDAR_ID", sCalendar);
			trans = mgr.perform(trans);
			dflag = "return";       
		}
	}

	public void  cancel()
	{
		trans.clear();   
		dflag = "cancel";
	}   

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("MAIN");

		headblk.addField("OBJID"). 
			setHidden();

		headblk.addField("OBJVERSION"). 
			setHidden();

		headblk.addField("CONTRACT"). 
			setMandatory().           
			setReadOnly().               
			setLabel("MPCCOWCURTEMPRECALCDLGCONTRACT: Site").       
			setSize(5);

		headblk.addField("COMPANY_SITE_DESCRIPTION").   
			setReadOnly().                      
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :CONTRACT)").   
			setLabel("MPCCOWCURTEMPRECALCDLGSITEDESCRIPTION: Site Description"). 
			setSize(30);

		headblk.addField("TEMPLATE_ID","Number").   
			setReadOnly().                            
			setMandatory().                               
			setLabel("MPCCOWCURTEMPRECALCDLGTEMPLATEID: Template ID"). 
			setSize(30);

		headblk.addField("TEMPLATE_DESCRIPTION").   
			setReadOnly().                             
			setMandatory().                                      
			setLabel("MPCCOWCURTEMPRECALCDLGTEMPLATEDESCRIPTION: Template Description").  
			setSize(0);

		headblk.addField("BEGIN_DATE","Date").     
			setLabel("MPCCOWCURTEMPRECALCDLGPERIOD1BEGINDATE: Period 1 Begin Date").   
			setInsertable().     
			setSize(30);

		headblk.addField("CALENDAR_ID").  
			setLabel("MPCCOWCURTEMPRECALCDLGNOOFPERIOD: Calendar ID").   
			setDynamicLOV("WORK_TIME_CALENDAR_LOV").      
			setLOVProperty("TITLE","List of Calendar ID").      
			setInsertable().             
			setSize(20);

		headblk.setView("PERIOD_TEMPLATE"); 
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWCURTEMPRECALCDLGTBLHEAD: Recalculate period details for Site");
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT); 
      headlay.defineGroup(mgr.translate("MPCCOWCURTEMPRECALCDLGRECALCPARAM: Recalculation Parameters"),"CONTRACT,COMPANY_SITE_DESCRIPTION,TEMPLATE_ID,TEMPLATE_DESCRIPTION,BEGIN_DATE,CALENDAR_ID",true,true,1);
		headlay.setEditable();
	}	

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();		

		if (!"return".equals(dflag)) {    
			printHiddenField("FIRST_REQUEST","OK");            
			appendToHTML(headlay.show());
			printNewLine();
			printSubmitButton("BOK","MPCCOWCURTEMPRECALCDLGOK:    OK   ","");
			printSubmitButton("BCANCEL","MPCCOWCURTEMPRECALCDLGCANCEL:  Cancel ","");
		}

		appendDirtyJavaScript("\n\nfunction returnD()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if (\"");                
		appendDirtyJavaScript(mgr.encodeStringForJavascript(dflag));
		appendDirtyJavaScript("\" == \"return\")\n");
		appendDirtyJavaScript("   {\n");
		appendDirtyJavaScript("      window.close();\n");
		appendDirtyJavaScript("      window.opener.refreshPeriodTemplateDialog();\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("   else if (\"");                
		appendDirtyJavaScript(mgr.encodeStringForJavascript(dflag));
		appendDirtyJavaScript("\" == \"cancel\")\n");
		appendDirtyJavaScript("   {\n");   
		appendDirtyJavaScript("      window.close();\n");
		appendDirtyJavaScript("   }\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("returnD();  \n");        
	}   

	protected String getDescription()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWCURTEMPRECALCDLGDESC: Recalculate period details for Site ");
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWCURTEMPRECALCDLGTITLE: Recalculate period details for Site ");
	}

}
