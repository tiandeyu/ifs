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
*  File        : ActiveSeparate2WorkCenterLoad.java 
*  Modified    :
*    ASP2JAVA Tool  2001-04-09  - Created Using the ASP file ActiveSeparate2WorkCenterLoad.asp
* ----------------------------------------------------------------------------
*              :  2002-07-26  SHAFLK   Bug Id 31771 Added security check for Work_Center_Int_API.
*                 031222      ARWILK   Edge Developments - (Removed clone and doReset Methods)
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveSeparate2WorkCenterLoad extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2WorkCenterLoad");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPCommandBar headbar;
	private ASPTable headtbl;
	private ASPBlockLayout headlay;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPBuffer row;
	private ASPCommand cmd;

	//===============================================================
	// Construction 
	//===============================================================
	public ActiveSeparate2WorkCenterLoad(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
			okFind();

		adjust();
	}

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void  okFind()
	{
		ASPManager mgr = getASPManager();

		ASPQuery q = trans.addQuery(headblk);
		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());
		q.includeMeta("ALL");
		mgr.submit(trans);

		if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2WORKCENTERLOADNODATA: No data found."));
			headset.clear();
		}
	}

	//Bug 31771, start
	public void  setWorkCenterValues()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Work_Center_Int_API");

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists("Work_Center_Int_API"))
		{
			cmd = trans.addCustomFunction("WORKCENTER","Work_Center_Int_API.Get_Mch_Work_Center","MCHWORKCENTER");
			cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

			cmd = trans.addCustomFunction("RESOURCENO","Work_Center_Int_API.Get_Mch_Resource_No","MCHRESOURCENO");
			cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

			cmd = trans.addCustomFunction("RESOUCELOADHOURS","Work_Center_Int_API.Get_Mch_Load_Hours","MCHRESOUCELOADHOURS");
			cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
			cmd.addParameter("PLAN_S_DATE",headset.getRow().getFieldValue("PLAN_S_DATE"));
			cmd.addParameter("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE"));
			trans = mgr.perform(trans);

			String workcent = trans.getValue("WORKCENTER/DATA/MCHWORKCENTER");
			String resource = trans.getValue("RESOURCENO/DATA/MCHRESOURCENO");
			String resourcehours = trans.getValue("RESOUCELOADHOURS/DATA/MCHRESOUCELOADHOURS");

			row = headset.getRow();
			row.setValue("MCHWORKCENTER",workcent);
			row.setValue("MCHRESOURCENO",resource);
			row.setValue("MCHRESOUCELOADHOURS",resourcehours);
			headset.setRow(row);
		}
	}
	//Bug 31771, end

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("OBJID").
		setFunction("''").
		setHidden();

		headblk.addField("OBJVERSION").
		setFunction("''").
		setHidden();

		headblk.addField("WO_NO","Number").
		setHidden();

		headblk.addField("CONTRACT").
		setHidden();

		headblk.addField("MCH_CODE").
		setHidden(); 

		headblk.addField("PLAN_S_DATE","Datetime").
		setHidden();

		headblk.addField("PLAN_F_DATE","Datetime").
		setHidden();

		headblk.addField("MCHWORKCENTER"). 
		setSize(11).
		setLabel("PCMWACTIVESEPARATE2WORKCENTERLOADMCHWORKCENTER: Work Center").
		setFunction("''").
		//setFunction("Work_Center_Int_API.Get_Mch_Work_Center(:CONTRACT,:MCH_CODE)").
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("MCHRESOURCENO").
		setSize(11).
		setLabel("PCMWACTIVESEPARATE2WORKCENTERLOADMCHRESOURCENO: Resource").
		setFunction("''").
		//setFunction("Work_Center_Int_API.Get_Mch_Resource_No(:CONTRACT,:MCH_CODE)").
		setReadOnly().
		setMaxLength(2000);

		headblk.addField("MCHRESOUCELOADHOURS","Number").
		setSize(11).
		setLabel("PCMWACTIVESEPARATE2WORKCENTERLOADMCHRESOULOADHRS: Load").
		setFunction("''").
		//setFunction("Work_Center_Int_API.Get_Mch_Load_Hours(:CONTRACT,:MCH_CODE,:PLAN_S_DATE,:PLAN_F_DATE)").
		setReadOnly();  

		headblk.setView("ACTIVE_SEPARATE"); 
		headset = headblk.getASPRowSet();
		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2WORKCENTERLOADHD: Work Center Load"));
		headtbl.setWrap();

		headbar = mgr.newASPCommandBar(headblk);
		headbar.disableCommand(headbar.FIND);
		headbar.disableCommand(headbar.BACK);
		headbar.disableCommand(headbar.DUPLICATEROW);
		headbar.disableCommand(headbar.FORWARD);
		headbar.disableCommand(headbar.BACKWARD);
		headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
		headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

		headlay = headblk.getASPBlockLayout();
		headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
		headlay.setDialogColumns(3);
	}


	public void  adjust()
	{
		//Bug 31771
		if ( headset.countRows()>0 )
		{
			setWorkCenterValues();
		}
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWACTIVESEPARATE2WORKCENTERLOADTITLE: Work Center Load";
	}

	protected String getTitle()
	{
		return "PCMWACTIVESEPARATE2WORKCENTERLOADTITLE: Work Center Load";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

		appendToHTML(headlay.show());

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
	}
}
