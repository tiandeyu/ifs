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
* File        : CopyTemplateDlg.java
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

public class CopyTemplateDlg extends ASPPageProvider {

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CopyTemplateDlg");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPTable headtbl;
	private ASPCommandBar headbar;
	private ASPBlockLayout headlay;

	private ASPBlock targetHeadblk;
	private ASPRowSet targetHeadset;
	private ASPTable targetHeadtbl;
	private ASPCommandBar targetHeadbar;
	private ASPBlockLayout targetHeadlay;  

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPCommand cmd;
	private String dflag;	 

	//===============================================================
	// Construction 
	//===============================================================   
	public CopyTemplateDlg(ASPManager mgr, String page_path) {
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
		ASPManager   mgr   =   getASPManager(  );
		trans.clear();

		String sContract = mgr.getQueryStringValue("CONTRACT");
		String sCompSiteDesc = mgr.getQueryStringValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.getQueryStringValue("TEMPLATE_ID");
		String sTempDesc = mgr.getQueryStringValue("TEMPLATE_DESCRIPTION");      

		cmd = trans.addCustomFunction("TARGETBEGINDATE","Period_Template_API.Get_Recalculation_Date","PERIOD_BEGIN_DATE");
		cmd.addParameter("CONTRACT",sContract);
		cmd.addParameter("TEMPLATE_ID",sTempId);

		trans = mgr.perform(trans);
		String sBeginDate = trans.getValue("TARGETBEGINDATE/DATA/PERIOD_BEGIN_DATE");

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);

		targetHeadset.addRow(trans.getBuffer("TARGET/DATA"));
		targetHeadset.setValue("CONTRACT", sContract);
		targetHeadset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		targetHeadset.setValue("TEMPLATE_ID", sTempId);
		targetHeadset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		targetHeadset.setValue("PERIOD_BEGIN_DATE", sBeginDate);    
	}

	public void  bOk()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		String sContract = mgr.readValue("CONTRACT");
		String sCompSiteDesc = mgr.readValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.readValue("TEMPLATE_ID");
		String sTempDesc  = mgr.readValue("TEMPLATE_DESCRIPTION");

		String sTargetContract = mgr.readValue("TARGET_CONTRACT");
		String sTargetTempId = mgr.readValue("TARGET_TEMPLATE_ID");
		String sTargetTempDesc = mgr.readValue("TARGET_TEMPLATE_DESCRIPTION");      
		String sBeginDate = mgr.readValue("PERIOD_BEGIN_DATE"); 

		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);

		targetHeadset.setValue("CONTRACT", sTargetContract);     
		targetHeadset.setValue("TEMPLATE_ID", sTargetTempId);
		targetHeadset.setValue("TEMPLATE_DESCRIPTION", sTargetTempDesc);
		targetHeadset.setValue("PERIOD_BEGIN_DATE", sBeginDate);

		if ( "".equals(sContract) ) {
			dflag = "false";
		} else {
			cmd = trans.addCustomCommand("COPYTEMPLATE", "PERIOD_TEMPLATE_MANAGER_API.Copy_Template");
			cmd.addParameter("CONTRACT", sContract);           
			cmd.addParameter("TEMPLATE_ID", sTempId);          
			cmd.addParameter("CONTRACT", sTargetContract);           
			cmd.addParameter("TEMPLATE_ID", sTargetTempId);          
			cmd.addParameter("TEMPLATE_DESCRIPTION", sTargetTempDesc);           
			cmd.addParameter("PERIOD_BEGIN_DATE", sBeginDate);                      

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
			setLabel("MPCCOWCOPYTEMPLATEDLGCONTRACT: Template Site").   
			setSize(5);

		headblk.addField("COMPANY_SITE_DESCRIPTION").   
			setReadOnly().                                     
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :CONTRACT)").   
			setLabel("MPCCOWCOPYTEMPLATEDLGSITEDESCRIPTION: Site Description"). 
			setSize(30);

		headblk.addField("TEMPLATE_ID","Number"). 
			setReadOnly().                      
			setMandatory().                     
			setLabel("MPCCOWCOPYTEMPLATEDLGTEMPLATEID: Template ID"). 
			setSize(30);

		headblk.addField("TEMPLATE_DESCRIPTION").   
			setReadOnly().                            
			setMandatory().                            
			setLabel("MPCCOWCOPYTEMPLATEDLGTEMPLATEDESCRIPTION: Template Description").   
			setSize(0);      
            
		headblk.setView("PERIOD_TEMPLATE");
		headblk.defineCommand("PERIOD_TEMPLATE_API","New__,Modify__,Remove__");

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);	

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWCOPYTEMPLATEDLGTBLHEAD: Copy Template");		
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);  
		headlay.defineGroup(mgr.translate("MPCCOWCOPYTEMPLATEDLGSOURCETEMPLATE: Source Template"),"CONTRACT,COMPANY_SITE_DESCRIPTION,TEMPLATE_ID,TEMPLATE_DESCRIPTION",true,true,1);      
		headlay.setEditable();

		targetHeadblk = mgr.newASPBlock("TARGET");

		targetHeadblk.addField("TARGET_OBJID").  
			setDbName("OBJID").    
			setHidden();

		targetHeadblk.addField("TARGET_OBJVERSION"). 
			setDbName("OBJVERSION").       
			setHidden();

		targetHeadblk.addField("TARGET_CONTRACT").  
			setDbName("CONTRACT").      
			setMandatory().               
			setDynamicLOV("USER_ALLOWED_SITE_LOV").   
			setLOVProperty("TITLE","List of Site").     
			setLabel("MPCCOWCOPYTEMPLATEDLGTARGETCONTRACT: Site").
			setSize(5);

		targetHeadblk.addField("TARGET_SITE_DESCRIPTION").   
			setDbName("COMPANY_SITE_DESCRIPTION").      
			setReadOnly().                   
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :TARGET_CONTRACT)").  
			setLabel("MPCCOWCOPYTEMPLATEDLGTARGETSITEDESCRIPTION: Site Description").  
			setSize(30);

		targetHeadblk.addField("PERIOD_BEGIN_DATE","Date"). 
			setFunction("TO_NUMBER(NULL)").               
			setLabel("MPCCOWCOPYTEMPLATEDLGTARGETSTARTPERIOD: Period 1 Begin Date").  
			setSize(30);      

		targetHeadblk.addField("TARGET_TEMPLATE_ID","Number").   
			setDbName("TEMPLATE_ID").                         
			setMandatory().                   
			setLabel("MPCCOWCOPYTEMPLATEDLGTARGETTEMPLATEID: Template ID").      
			setSize(30);

		targetHeadblk.addField("TARGET_TEMPLATE_DESCRIPTION"). 
			setDbName("TEMPLATE_DESCRIPTION").    
			setMandatory().                  
			setLabel("MPCCOWCOPYTEMPLATEDLGTARGETTEMPLATEDESCRIPTION: Template Description").  
			setSize(0); 
		
		getASPField("TARGET_CONTRACT").setValidation("TARGET_SITE_DESCRIPTION");

		targetHeadblk.setView("PERIOD_TEMPLATE");
		targetHeadset = targetHeadblk.getASPRowSet();

		targetHeadbar = mgr.newASPCommandBar(targetHeadblk);

		targetHeadtbl = mgr.newASPTable(targetHeadblk);
		targetHeadtbl.setTitle("MPCCOWCOPYTEMPLATEDLGTARGETTBLHEAD: Copy Template");
		targetHeadtbl.setWrap();     

		targetHeadlay = targetHeadblk.getASPBlockLayout();
		targetHeadlay.setDefaultLayoutMode(targetHeadlay.CUSTOM_LAYOUT);   
		targetHeadlay.defineGroup(mgr.translate("MPCCOWCOPYTEMPLATEDLGTARGETTEMPLATE: Target Template"),"TARGET_CONTRACT,TARGET_SITE_DESCRIPTION,PERIOD_BEGIN_DATE,TARGET_TEMPLATE_ID,TARGET_TEMPLATE_DESCRIPTION",true,true,1);
		targetHeadlay.setEditable();
      
	}

	
	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();
		
		if (!"return".equals(dflag)) {    
			printHiddenField("FIRST_REQUEST","OK");         
			appendToHTML(headlay.show());   
			printNewLine();
			appendToHTML(targetHeadlay.show());
			printNewLine();
			printSubmitButton("BOK","MPCCOWCOPYTEMPLATEDLGOK:    OK   ","");
			printSubmitButton("BCANCEL","MPCCOWCOPYTEMPLATEDLGCANCEL:  Cancel ","");
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

		return mgr.translate("MPCCOWCOPYTEMPLATEDLGDESC: Copy Template ");
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWCOPYTEMPLATEDLGTITLE: Copy Template ");
	}

}
