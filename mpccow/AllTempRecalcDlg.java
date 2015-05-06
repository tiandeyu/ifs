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
* File        : AllTempRecalcDlg.java
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

public class AllTempRecalcDlg extends ASPPageProvider {

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
	public AllTempRecalcDlg(ASPManager mgr, String page_path) {
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

		cmd = trans.addCustomFunction("ALLTEMPRECALC","SITE_API.Get_Site_Date","BEGIN_DATE");
		cmd.addParameter("CONTRACT",sContract);
		trans = mgr.perform(trans);
		String sBeginDate = trans.getValue("ALLTEMPRECALC/DATA/BEGIN_DATE");

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		headset.setValue("CONTRACT", sContract);
		headset.setValue("BEGIN_DATE",sBeginDate);
	}

	public void  bOk()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		String sContract = mgr.readValue("CONTRACT");
		String sBeginDate = mgr.readValue("BEGIN_DATE");  

		headset.setValue("CONTRACT", sContract);
		headset.setValue("BEGIN_DATE", sBeginDate);

		if ( "".equals(sContract) ) {
			dflag = "false";
		} 
		else {
			cmd = trans.addCustomCommand("DELETEPERIODS", "PERIOD_TEMPLATE_MANAGER_API.Recalculate_Site_Templates");
			cmd.addParameter("CONTRACT", sContract);           
			cmd.addParameter("BEGIN_DATE", sBeginDate);
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
			setHidden().  
			setSize(5);      

		headblk.addField("BEGIN_DATE","Date").              
			setFunction("TO_NUMBER(NULL)").
			setLabel("MPCCOWALLTEMPRECALCDLGPERIOD1BEGINDATE: Period 1 Begin Date").
			setInsertable().
			setSize(30);

		headblk.setView("PERIOD_TEMPLATE");  

		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);
		
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWALLTEMPRECALCDLGTBLHEAD: Recalculate period details for all Site");      
		headtbl.setWrap();

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
		headlay.setEditable();
      headlay.defineGroup(mgr.translate("MPCCOWALLTEMPRECALCDLGRECALCPARAM: Recalculation Parameters"),"BEGIN_DATE",true,true);
   }
	
	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();		

		if (!"return".equals(dflag)) {   
			printHiddenField("FIRST_REQUEST","OK");         
			appendToHTML(headlay.show());
			printNewLine();
			printSubmitButton("BOK","MPCCOWALLTEMPRECALCDLGOK:    OK   ","");
			printSubmitButton("BCANCEL","MPCCOWALLTEMPRECALCDLGCANCEL:  Cancel ","");
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

		return mgr.translate("MPCCOWALLTEMPRECALCDLGDESC: Recalculate period details for all Site ");
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWALLTEMPRECALCDLGTITLE: Recalculate period details for all Site ");
	}

}
