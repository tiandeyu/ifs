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
* File        : DeletePeriodsDlg.java
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

public class DeletePeriodsDlg extends ASPPageProvider {

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.DeletePeriodsDlg");

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
	public DeletePeriodsDlg(ASPManager mgr, String page_path) {
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

		cmd = trans.addCustomFunction("PLANPERIODMAXPERIOD","Period_Template_Detail_API.Get_Max_Period_No","DELETING_PERIOD");
		cmd.addParameter("CONTRACT",sContract);
		cmd.addParameter("TEMPLATE_ID",sTempId);

		trans = mgr.perform(trans);
		String sMaxPeriod = trans.getValue("PLANPERIODMAXPERIOD/DATA/DELETING_PERIOD");

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("DELETING_PERIOD",sMaxPeriod);
		headset.setValue("DELETE_TO_PERIOD","1"); 

	}

	public void  bOk()
	{
		ASPManager mgr = getASPManager();

		String sContract = mgr.readValue("CONTRACT");
		String sCompSiteDesc = mgr.readValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.readValue("TEMPLATE_ID");
		String sTempDesc  = mgr.readValue("TEMPLATE_DESCRIPTION");
		String sDeletingPeriod = mgr.readValue("DELETING_PERIOD");     
		String sDeleteToPeriod = mgr.readValue("DELETE_TO_PERIOD");  

		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("DELETING_PERIOD", sDeletingPeriod);
		headset.setValue("DELETE_TO_PERIOD", sDeleteToPeriod);

		if ( "".equals(sContract) ) {
			dflag = "false";
		} else {
			trans.clear();        

			cmd = trans.addCustomCommand("DELETEPERIODS", "Period_Template_Manager_API.Delete_Periods");
			cmd.addParameter("CONTRACT", sContract);           
			cmd.addParameter("TEMPLATE_ID", sTempId);          
			cmd.addParameter("DELETING_PERIOD", sDeletingPeriod);
			cmd.addParameter("DELETE_TO_PERIOD", sDeleteToPeriod);
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
			setLabel("MPCCOWDELETEPERIODDLGCONTRACT: Site").  
			setSize(5);

		headblk.addField("COMPANY_SITE_DESCRIPTION"). 
			setReadOnly().                                
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :CONTRACT)").   
			setLabel("MPCCOWDELETEPERIODDLGSITEDESCRIPTION: Site Description").     
			setSize(30);

		headblk.addField("TEMPLATE_ID","Number"). 
			setReadOnly().                       
			setMandatory().                         
			setLabel("MPCCOWDELETEPERIODDLGTEMPLATEID: Template ID").  
			setSize(30);

		headblk.addField("TEMPLATE_DESCRIPTION"). 
			setReadOnly().    
			setMandatory().       
			setLabel("MPCCOWDELETEPERIODDLGTEMPLATEDESCRIPTION: Template Description").  
			setSize(0);

		headblk.addField("DELETING_PERIOD","Number"). 
			setReadOnly().                       
			setFunction("TO_NUMBER(NULL)").          
			setLabel("MPCCOWDELETEPERIODDLGSTARTPERIOD: Delete from Period").  
			setSize(30);

		headblk.addField("DELETE_TO_PERIOD","Number"). 
			setFunction("TO_NUMBER(NULL)").              
			setLabel("MPCCOWDELETEPERIODDLGNOOFPERIOD: Delete to Period").  
			setInsertable().    
			setSize(30);

		headblk.setView("PERIOD_TEMPLATE");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWDELETEPERIODDLGTBLHEAD: Delete Periods for Site");
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);      
		headlay.setEditable();
	}
   
	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();		

		if (!"return".equals(dflag)) {    
			printHiddenField("FIRST_REQUEST","OK");            
			appendToHTML(headlay.show());
			printNewLine();
			printSubmitButton("BOK","MPCCOWDELETEPERIODDLGOK:    OK   ","");
			printSubmitButton("BCANCEL","MPCCOWDELETEPERIODDLGCANCEL:  Cancel ","");
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

		return mgr.translate("MPCCOWDELETEPERIODDLGDESC: Delete Periods for Site ");
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWDELETEPERIODDLGTITLE: Delete Periods for Site ");
	}

}
