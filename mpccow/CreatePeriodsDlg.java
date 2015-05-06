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
* File        : CreatePeriodsDlg.java
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

public class CreatePeriodsDlg extends ASPPageProvider {

	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CreatePeriodsDlg");

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
	public CreatePeriodsDlg(ASPManager mgr, String page_path) {
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

		cmd = trans.addCustomFunction("PLANPERIODMAXPERIOD","Period_Template_Detail_API.Get_Max_Period_No","STARTING_PERIOD");
		cmd.addParameter("CONTRACT",sContract);
		cmd.addParameter("TEMPLATE_ID",sTempId);

		trans = mgr.perform(trans);
		String sMaxPeriod = trans.getValue("PLANPERIODMAXPERIOD/DATA/STARTING_PERIOD");
		sMaxPeriod = Integer.toString(Integer.parseInt(sMaxPeriod)+1);      

		headset.addRow(trans.getBuffer("HEAD/DATA"));
		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("STARTING_PERIOD",sMaxPeriod);
		headset.setValue("NUMBER_OF_PERIODS","0");
		headset.setValue("PERIOD_LENGTH","1");        
	}

	public void  bOk()
	{
		ASPManager mgr = getASPManager();

		String sContract = mgr.readValue("CONTRACT");
		String sCompSiteDesc = mgr.readValue("COMPANY_SITE_DESCRIPTION");
		String sTempId = mgr.readValue("TEMPLATE_ID");
		String sTempDesc  = mgr.readValue("TEMPLATE_DESCRIPTION");
		String sStartingPeriod = mgr.readValue("STARTING_PERIOD");  
		sStartingPeriod =Integer.toString(Integer.parseInt(sStartingPeriod)-1);  
		String sNoOfPeriods = mgr.readValue("NUMBER_OF_PERIODS");      
		String sPeriodLength = mgr.readValue("PERIOD_LENGTH");     
		String sPeriodUnit =  mgr.readValue("OPTION");

		headset.setValue("CONTRACT", sContract);
		headset.setValue("COMPANY_SITE_DESCRIPTION", sCompSiteDesc);
		headset.setValue("TEMPLATE_ID", sTempId);
		headset.setValue("TEMPLATE_DESCRIPTION", sTempDesc);
		headset.setValue("STARTING_PERIOD", sStartingPeriod);
		headset.setValue("NUMBER_OF_PERIODS", sNoOfPeriods);
		headset.setValue("PERIOD_LENGTH", sPeriodLength);   

		if ( "".equals(sContract) ) {
			dflag = "false";
		} else {
			if ( "0".equals(sPeriodLength) ) {
				mgr.showAlert(mgr.translate("MPCCOWCREATEPERIODPERIODDLGLENGALERT: Decimals or zero is not allowed in Period Lengths."));
				dflag = "false";
			} else {
				trans.clear();                           

				cmd = trans.addCustomFunction("PERIODUNITDBVAL","Plan_Period_Unit_API.Decode","PERIODUNITDB");
				cmd.addParameter("PERIODUNIT",sPeriodUnit);
				cmd = trans.addCustomCommand("CREATEPERIODS", "Period_Template_Manager_API.Create_Periods");
				cmd.addParameter("CONTRACT", sContract);           
				cmd.addParameter("TEMPLATE_ID", sTempId); 
				cmd.addReference("PERIODUNITDB","PERIODUNITDBVAL/DATA");                               
				cmd.addParameter("STARTING_PERIOD", sStartingPeriod);
				cmd.addParameter("NUMBER_OF_PERIODS", sNoOfPeriods);
				cmd.addParameter("PERIOD_LENGTH",sPeriodLength);
				trans = mgr.perform(trans);

				dflag = "return";
			}
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
			setLabel("MPCCOWCREATEPERIODDLGCONTRACT: Template Site").   
			setReadOnly().
			setSize(5);

		headblk.addField("COMPANY_SITE_DESCRIPTION").    
			setFunction("COMPANY_SITE_API.GET_DESCRIPTION ( :CONTRACT)"). 
			setLabel("MPCCOWCREATEPERIODDLGSITEDESCRIPTION: Site Description"). 
			setReadOnly().  
			setSize(30);

		headblk.addField("TEMPLATE_ID","Number"). 
			setMandatory().                    
			setLabel("MPCCOWCREATEPERIODDLGTEMPLATEID: Template ID").  
			setReadOnly().  
			setSize(30);

		headblk.addField("TEMPLATE_DESCRIPTION").  
			setMandatory().                        
			setLabel("MPCCOWCREATEPERIODDLGTEMPLATEDESCRIPTION: Template Description"). 
			setReadOnly().  
			setSize(0);

		headblk.addField("STARTING_PERIOD","Number"). 
			setFunction("TO_NUMBER(NULL)").            
			setLabel("MPCCOWCREATEPERIODDLGSTARTPERIOD: Starting Period"). 
			setReadOnly().  
			setSize(30);

		headblk.addField("NUMBER_OF_PERIODS","Number").   
			setFunction("TO_NUMBER(NULL)").            
			setLabel("MPCCOWCREATEPERIODDLGNOOFPERIOD: Number of Periods").  
			setInsertable().            
			setSize(30);

		headblk.addField("PERIOD_LENGTH","Number").  
			setFunction("TO_NUMBER(NULL)").          
			setLabel("MPCCOWCREATEPERIODDLGPERIODLEN: Period Length"). 
			setInsertable(). 
			setSize(30);

		headblk.addField("PERIODUNIT"). 
			setFunction("TO_NUMBER(NULL)").
			setHidden();   

		headblk.addField("PERIODUNITDB").  
			setFunction("TO_NUMBER(NULL)").  
			setHidden();                    

		headblk.setView("PERIOD_TEMPLATE");
		headset = headblk.getASPRowSet();

		headbar = mgr.newASPCommandBar(headblk);

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle("MPCCOWCREATEPERIODDLGTBLHEAD: Create Periods for Site");
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
			appendToHTML("<!-- table cell for radio buttons    -->\n");
			appendToHTML("<tr><td>\n");
			appendToHTML("<table border=0 width=100%  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
			appendToHTML("	<tr><td>\n");
			appendToHTML("	<table width=100% cellspacing=0 cellpadding=0 class=group>\n");
			appendToHTML("		<tr>\n");
			appendToHTML("			<td width=10 height=\"17\" noWrap vAlign=\"bottom\"><img src=\"../common/images/block_topleft.gif\"></td><td align=left width=25 height=\"17\" noWrap vAlign=\"center\"><td align=left height=\"17\" noWrap vAlign=\"center\"><font class=groupBoxTitleText>Plan Period Unit</font></td><td width=10>&nbsp;</td>		<td width=100% align=\"right\" height=\"17\" noWrap vAlign=\"bottom\" background=\"../common/images/block_topmiddle.gif\"><img src=\"../common/images/table_empty_image.gif\" width=\"0\" height=\"0\"></td><td align=\"right\" vAlign=\"bottom\" width=1><img valign=bottom src=\"../common/images/block_topright.gif\">\n");
			appendToHTML("			</td>\n");
			appendToHTML("		</tr>\n");
			appendToHTML("</table>\n");
			
			appendToHTML("<table width=100% border=0  class=\"SubBlockLayoutTable\" cellspacing=0 cellpadding=4>\n");
			appendToHTML("<tr>\n");
			appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
			printRadioButton("MPCCOWCREATEPERIODDLGRADIO1: Day","OPTION","DAY",true," OnClick='validateOption(0)'");
			appendToHTML("</td><td width=10>&nbsp</td>\n");
			appendToHTML("	<tr>\n");
			appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
			printRadioButton("MPCCOWCREATEPERIODDLGRADIO2: Week","OPTION","WEEK",false," OnClick='validateOption(1)'");
			appendToHTML("</td><td width=10>&nbsp</td>\n");
			appendToHTML("	<tr>\n");
			appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
			printRadioButton("MPCCOWCREATEPERIODDLGRADIO3: Month","OPTION","MONTH",false," OnClick='validateOption(2)'");
			appendToHTML("</td><td width=10>&nbsp</td>\n");
			appendToHTML("	<tr>\n");
			appendToHTML("		<td  nowrap  valign=top><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"><br><b>");
			printRadioButton("MPCCOWCREATEPERIODDLGRADIO4: Quarter", "OPTION","QUARTER",false,"OnClick='validateOption(3)'");
			appendToHTML("</td><td width=10>&nbsp</td>\n");         
			appendToHTML("</table>	\n");
			
			appendToHTML("<table  cellpadding=0 cellspacing=0 width=99%>");
			appendToHTML("<tr><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td><td height=9 background=\"../common/images/block_bottommiddle.gif\" width = 100%><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"1\"></td><td height=9 width=1><img src=\"../common/images/block_bottomleft.gif\"></td></tr></table><p>\n");
			appendToHTML("							<tr>\n");
			appendToHTML("								<td nowrap align=\"left\" valign=\"top\">\n");
			appendToHTML("</table></tr></td>");   

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

		appendDirtyJavaScript("function validateOption(i)\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("switch(i)");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("case 0:\n");
		appendDirtyJavaScript("getField_('OPTION',i).value = \"1\";\n");
		appendDirtyJavaScript("break;\n");
		appendDirtyJavaScript("case 1:\n");
		appendDirtyJavaScript("getField_('OPTION',i).value = \"2\";\n");
		appendDirtyJavaScript("break;\n");
		appendDirtyJavaScript("case 2:\n");
		appendDirtyJavaScript("getField_('OPTION',i).value = \"3\";\n");
		appendDirtyJavaScript("break;\n");
		appendDirtyJavaScript("case 3:\n");
		appendDirtyJavaScript("getField_('OPTION',i).value = \"4\";\n");
		appendDirtyJavaScript("break;\n");
		appendDirtyJavaScript("}\n"); 
		appendDirtyJavaScript("}\n"); 

	}   

	protected String getDescription()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWCREATEPERIODDLGDESC: Create Periods for Site ");
	}

	protected String getTitle()
	{
		ASPManager mgr = getASPManager();

		return mgr.translate("MPCCOWCREATEPERIODDLGTITLE: Create Periods for Site ");
	}                       

}
