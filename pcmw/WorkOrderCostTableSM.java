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
*  File        : WorkOrderCostTableSM.java 
*  Created     : ARWILK  010313  Created.
*  Modified    :
*  ARWILK  010821  Modified graphs to support webkit 3.5.0 beta 5.
*  CHCRLK  030123  Added parameter TOOL_OUTCOME.
*  CHAMLK  031018  Modified function okFindTree() to display cost type Tools/Facilities
*                  and make it available for the connected graphs.
*  ARWILK  031217  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  031230  Edge Developments - (Replaced links with multirow RMB's)
*  THWILK  040316  Call ID 111786 - Added selectActiveCommand() and deselectActiveCommand() since mgr.generateClientScript() has been called in the page
*                  instead of endPresentation().This functionality involves frames and endPresentation() cannot be called since there will
*                  be additional lines displayed by foundation. Therefore selectActiveCommand() and deselectActiveCommand() were overridden.
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  ILSOLK  070731  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.Vector; 
import java.awt.Color;
import java.awt.Font;

public class WorkOrderCostTableSM extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCostTableSM");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPForm frm;
	private ASPContext ctx;
	private ASPHTMLFormatter fmt;
	private ASPLog log;
	private ASPBlock headblk;
	private ASPRowSet headset;
	private ASPBlock itemblk0;
	private ASPRowSet itemset0;
	private ASPCommandBar itembar0;
	private ASPTable itemtbl0;
	private ASPBlockLayout itemlay0;
	private ASPPopup item0popup;
	private ASPBlock dummyblk;
	private ASPField f;
	private ASPBlock b;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private int sWorkOrderNoTree;
	private int windowHeight;
	private ASPTransactionBuffer trans;
	private String activetab;
	private int graphflagWo;
	private boolean GraphInNewWind;
	private boolean revGraphInNewWind;
	private boolean marginGraphInNewWind;
	private String cbhasconnections_var; 
	private String costGraph_File; 
	private String sExtOfGraph; 
	private String sTitleOfGraph;
	private int costGraph_Width;
	private int costGraph_Height;
	private int windowWidth; 
	private int valueOfWoNo; 

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderCostTableSM(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		windowWidth =  100;
		windowHeight =  100;

		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		frm = mgr.getASPForm();
		ctx = mgr.getASPContext();
		fmt = getASPHTMLFormatter();
		log = mgr.getASPLog();

		activetab = ctx.readValue("ACTIVETAB","0");
		valueOfWoNo = ctx.readNumber("CTXWONO",valueOfWoNo);
		sWorkOrderNoTree = ctx.readNumber("CTXWORTRR",sWorkOrderNoTree);

		graphflagWo = 0;

		GraphInNewWind = false;
		revGraphInNewWind = false;
		marginGraphInNewWind = false;

		if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECTED_WO_NO")))
		{
			sWorkOrderNoTree = toInt(mgr.readValue("CONNECTED_WO_NO"));
			okFindTree();      
		}
		else
		{
			costGraph_File = "";
			sExtOfGraph = "";
			costGraph_Width = 0;
			costGraph_Height = 0;
			sTitleOfGraph = "";
			windowWidth = 100;
			windowHeight = 100;
		}

		adjust();   

		ctx.writeNumber("CTXWORTRR",sWorkOrderNoTree);
		ctx.writeValue("ACTIVETAB",activetab);
		ctx.writeNumber("CTXWONO",valueOfWoNo);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  okFindTree()
	{
		ASPManager mgr = getASPManager();

		int woCost;    
		String sCheckValue;
		String woCostType;
		String costTyptemp;
		String woRevenue; 
		String structureRevenue; 
		String woMargin;
		String structureMargin;    
		double sPersonelOutCome;
		double sMaterialOutCome;
		double sExternalOutCome;
		double sExpenceOutCome;
		double sFixedPriceOutCome;
		double sToolOutCome;
		double sPersonelCost;
		double sMaterialCost;
		double sExternalCost;
		double sExpenceCost;
		double sFixedPriceCost;
		double sToolCost;
		double sPersonelInCome;
		double sMaterialInCome;
		double sExternalInCome;
		double sExpenceInCome;
		double sFixedPriceInCome;
		double sToolInCome;
		double sPersonelStrucRev;
		double sMaterialStrucRev;
		double sExternalStrucRev;
		double sExpenceStrucRev;
		double sFixedPriceStrucRev;
		double sToolStrucRev;
		double sPersonelBudgtCost;
		double sMaterialBudgtCost;
		double sExternalBudgtCost;
		double sExpenceBudgtCost;
		double sFixedPriceBudgtCost;
		double sToolBudgtCost;
		double sPersonelStrcBudgtCost;
		double sMaterialStrcBudgtCost;
		double sExternalStrcBudgtCost;
		double sExpenceStrcBudgtCost;
		double sFixedPriceStrcBudgtCost;
		double sToolStrcBudgtCost;
		double sPersonelBudgRev;
		double sMaterialBudgRev;
		double sExternalBudgRev;
		double sExpenceBudgRev;
		double sFixedPriceBudgRev;
		double sToolBudgRev;
		double sPersonelStrcBudgRev;
		double sMaterialStrcBudgRev;
		double sExternalStrcBudgRev;
		double sExpenceStrcBudgRev;
		double sFixedPriceStrcBudgRev;
		double sToolStrcBudgRev;
		double sPersonelPlndCost;
		double sMaterialPlndCost;
		double sExternalPlndCost;
		double sExpencePlndCost;
		double sFixedPricePlndCost;
		double sToolPlndCost;
		double sPersonelStrcPlndCost;
		double sMaterialStrcPlndCost;
		double sExternalStrcPlndCost;
		double sExpenceStrcPlndCost;
		double sFixedPriceStrcPlndCost;
		double sToolStrcPlndCost;
		double sPersonelPlndRev;
		double sMaterialPlndRev;
		double sExternalPlndRev;
		double sExpencePlndRev;
		double sFixedPricePlndRev;
		double sToolPlndRev;
		double sPersonelStrcPlndRev;
		double sMaterialStrcPlndRev;
		double sExternalStrcPlndRev;
		double sExpenceStrcPlndRev;
		double sFixedPriceStrcPlndRev;
		double sToolStrcPlndRev;
		double woMarginTemp; 
		double strMarginTemp;
		double sTotalOutCome;
		double sTotalCost;
		double sTotalInCome; 
		double sTotalStrucRev;
		double sTotalWoBudCst;
		double sTotalStcBudCst; 
		double sTotalWoBudRev; 
		double sTotalStcBudRev; 
		double sTotalPlndCst; 
		double sTotalStcPlndCst;
		double sTotlaPlndRev; 
		double sTotalStcPlndRev;
		double structureCost; 
		double woCostTemp; 
		double strCostTemp; 
		double woRevenueTemp; 
		double strRevenueTemp;

		ASPQuery q = trans.addEmptyQuery(headblk);
		q.includeMeta("ALL");

		mgr.submit(trans);
		trans.clear();   

		ASPCommand cmd = trans.addCustomCommand("ALLOUTCOME","Work_Order_Coding_API.Get_All_Outcome");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");          
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));

		cmd = trans.addCustomCommand("STUCCOST","Work_Order_Coding_Utility_API.Get_Structure_Cost");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));

		cmd = trans.addCustomCommand("ALLINCOME","Work_Order_Coding_API.Get_All_Income");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");          
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));

		cmd = trans.addCustomCommand("STUCINCOME","Work_Order_Coding_Utility_API.Get_Structure_Income");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("WOBUDCOST","WORK_ORDER_API.Get_All_Bud_Cost");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME",""); 
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("STCBUDCOST","WORK_ORDER_API.Get_Budget_Cost_Structure");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("WOBUDREV","WORK_ORDER_API.Get_All_Bud_Revenue");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME",""); 
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("STCBUDREV","WORK_ORDER_API.Get_Budget_Revenue_Structure");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("WOPLNDCOST","WORK_ORDER_PLANNING_API.Get_Planned_Cost");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("STCPLNDCOST","WORK_ORDER_PLANNING_UTIL_API.Get_Planned_Cost_Structure");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME",""); 
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("WOPLNDREV","WORK_ORDER_PLANNING_API.Get_Planned_Revenue");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomCommand("STCPLNDREV","WORK_ORDER_PLANNING_UTIL_API.Get_Planned_Revenue_Structure");
		cmd.addParameter("PERSONEL_OUTCOME","");          
		cmd.addParameter("MATERIAL_OUTCOME","");          
		cmd.addParameter("EXTERNAL_OUTCOME","");          
		cmd.addParameter("EXPENCE_OUTCOME","");
		cmd.addParameter("FIXED_PRICE_OUTCOME","");
		cmd.addParameter("TOOL_OUTCOME","");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));   

		cmd = trans.addCustomFunction("CHECKVAL","WORK_ORDER_CONNECTION_API.Has_Connection_Down","CHECK_VALUE");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));

		trans = mgr.perform(trans);

		sPersonelOutCome   = trans.getNumberValue("ALLOUTCOME/DATA/PERSONEL_OUTCOME");
		sMaterialOutCome   = trans.getNumberValue("ALLOUTCOME/DATA/MATERIAL_OUTCOME");
		sExternalOutCome   = trans.getNumberValue("ALLOUTCOME/DATA/EXTERNAL_OUTCOME");
		sExpenceOutCome    = trans.getNumberValue("ALLOUTCOME/DATA/EXPENCE_OUTCOME");
		sFixedPriceOutCome = trans.getNumberValue("ALLOUTCOME/DATA/FIXED_PRICE_OUTCOME");
		sToolOutCome       = trans.getNumberValue("ALLOUTCOME/DATA/TOOL_OUTCOME");

		sPersonelCost   = trans.getNumberValue("STUCCOST/DATA/PERSONEL_OUTCOME");
		sMaterialCost   = trans.getNumberValue("STUCCOST/DATA/MATERIAL_OUTCOME");
		sExternalCost   = trans.getNumberValue("STUCCOST/DATA/EXTERNAL_OUTCOME");
		sExpenceCost    = trans.getNumberValue("STUCCOST/DATA/EXPENCE_OUTCOME");
		sFixedPriceCost = trans.getNumberValue("STUCCOST/DATA/FIXED_PRICE_OUTCOME");
		sToolCost       = trans.getNumberValue("STUCCOST/DATA/TOOL_OUTCOME");

		sPersonelInCome   = trans.getNumberValue("ALLINCOME/DATA/PERSONEL_OUTCOME");
		sMaterialInCome   = trans.getNumberValue("ALLINCOME/DATA/MATERIAL_OUTCOME");
		sExternalInCome   = trans.getNumberValue("ALLINCOME/DATA/EXTERNAL_OUTCOME");
		sExpenceInCome    = trans.getNumberValue("ALLINCOME/DATA/EXPENCE_OUTCOME");
		sFixedPriceInCome = trans.getNumberValue("ALLINCOME/DATA/FIXED_PRICE_OUTCOME");
		sToolInCome       = trans.getNumberValue("ALLINCOME/DATA/TOOL_OUTCOME");

		sPersonelStrucRev   = trans.getNumberValue("STUCINCOME/DATA/PERSONEL_OUTCOME");
		sMaterialStrucRev   = trans.getNumberValue("STUCINCOME/DATA/MATERIAL_OUTCOME");
		sExternalStrucRev   = trans.getNumberValue("STUCINCOME/DATA/EXTERNAL_OUTCOME");
		sExpenceStrucRev    = trans.getNumberValue("STUCINCOME/DATA/EXPENCE_OUTCOME");
		sFixedPriceStrucRev = trans.getNumberValue("STUCINCOME/DATA/FIXED_PRICE_OUTCOME");
		sToolStrucRev       = trans.getNumberValue("STUCINCOME/DATA/TOOL_OUTCOME");

		sPersonelBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/MATERIAL_OUTCOME");
		sExternalBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/EXTERNAL_OUTCOME");
		sExpenceBudgtCost    = trans.getNumberValue("WOBUDCOST/DATA/EXPENCE_OUTCOME");
		sFixedPriceBudgtCost = trans.getNumberValue("WOBUDCOST/DATA/FIXED_PRICE_OUTCOME");
		sToolBudgtCost       = trans.getNumberValue("WOBUDCOST/DATA/TOOL_OUTCOME");

		sPersonelStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/MATERIAL_OUTCOME");
		sExternalStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/EXTERNAL_OUTCOME");
		sExpenceStrcBudgtCost    = trans.getNumberValue("STCBUDCOST/DATA/EXPENCE_OUTCOME");
		sFixedPriceStrcBudgtCost = trans.getNumberValue("STCBUDCOST/DATA/FIXED_PRICE_OUTCOME");
		sToolStrcBudgtCost       = trans.getNumberValue("STCBUDCOST/DATA/TOOL_OUTCOME");

		sPersonelBudgRev    = trans.getNumberValue("WOBUDREV/DATA/PERSONEL_OUTCOME");
		sMaterialBudgRev    = trans.getNumberValue("WOBUDREV/DATA/MATERIAL_OUTCOME");
		sExternalBudgRev    = trans.getNumberValue("WOBUDREV/DATA/EXTERNAL_OUTCOME");
		sExpenceBudgRev     = trans.getNumberValue("WOBUDREV/DATA/EXPENCE_OUTCOME");
		sFixedPriceBudgRev  = trans.getNumberValue("WOBUDREV/DATA/FIXED_PRICE_OUTCOME");
		sToolBudgRev        = trans.getNumberValue("WOBUDREV/DATA/TOOL_OUTCOME");

		sPersonelStrcBudgRev    = trans.getNumberValue("STCBUDREV/DATA/PERSONEL_OUTCOME");
		sMaterialStrcBudgRev    = trans.getNumberValue("STCBUDREV/DATA/MATERIAL_OUTCOME");
		sExternalStrcBudgRev    = trans.getNumberValue("STCBUDREV/DATA/EXTERNAL_OUTCOME");
		sExpenceStrcBudgRev     = trans.getNumberValue("STCBUDREV/DATA/EXPENCE_OUTCOME");
		sFixedPriceStrcBudgRev  = trans.getNumberValue("STCBUDREV/DATA/FIXED_PRICE_OUTCOME");
		sToolStrcBudgRev        = trans.getNumberValue("STCBUDREV/DATA/TOOL_OUTCOME");

		sPersonelPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/MATERIAL_OUTCOME");
		sExternalPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/EXTERNAL_OUTCOME");
		sExpencePlndCost     = trans.getNumberValue("WOPLNDCOST/DATA/EXPENCE_OUTCOME");
		sFixedPricePlndCost  = trans.getNumberValue("WOPLNDCOST/DATA/FIXED_PRICE_OUTCOME");
		sToolPlndCost        = trans.getNumberValue("WOPLNDCOST/DATA/TOOL_OUTCOME");

		sPersonelStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/MATERIAL_OUTCOME");
		sExternalStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/EXTERNAL_OUTCOME");
		sExpenceStrcPlndCost     = trans.getNumberValue("STCPLNDCOST/DATA/EXPENCE_OUTCOME");
		sFixedPriceStrcPlndCost  = trans.getNumberValue("STCPLNDCOST/DATA/FIXED_PRICE_OUTCOME");
		sToolStrcPlndCost        = trans.getNumberValue("STCPLNDCOST/DATA/TOOL_OUTCOME");

		sPersonelPlndRev    = trans.getNumberValue("WOPLNDREV/DATA/PERSONEL_OUTCOME");
		sMaterialPlndRev    = trans.getNumberValue("WOPLNDREV/DATA/MATERIAL_OUTCOME");
		sExternalPlndRev    = trans.getNumberValue("WOPLNDREV/DATA/EXTERNAL_OUTCOME");
		sExpencePlndRev     = trans.getNumberValue("WOPLNDREV/DATA/EXPENCE_OUTCOME");
		sFixedPricePlndRev  = trans.getNumberValue("WOPLNDREV/DATA/FIXED_PRICE_OUTCOME");
		sToolPlndRev        = trans.getNumberValue("WOPLNDREV/DATA/TOOL_OUTCOME");

		sPersonelStrcPlndRev    = trans.getNumberValue("STCPLNDREV/DATA/PERSONEL_OUTCOME");
		sMaterialStrcPlndRev    = trans.getNumberValue("STCPLNDREV/DATA/MATERIAL_OUTCOME");
		sExternalStrcPlndRev    = trans.getNumberValue("STCPLNDREV/DATA/EXTERNAL_OUTCOME");
		sExpenceStrcPlndRev     = trans.getNumberValue("STCPLNDREV/DATA/EXPENCE_OUTCOME");
		sFixedPriceStrcPlndRev  = trans.getNumberValue("STCPLNDREV/DATA/FIXED_PRICE_OUTCOME");
		sToolStrcPlndRev        = trans.getNumberValue("STCPLNDREV/DATA/TOOL_OUTCOME");

		sCheckValue   = trans.getValue("CHECKVAL/DATA/CHECK_VALUE");

		trans.clear();

		itemset0.clear();

		ASPBuffer data = trans.getBuffer("ITEM0/DATA");
		itemset0.addRow(data);
		ASPBuffer r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMPERSON: Personnel"));
		r.setNumberValue("NACCUMULATEDCOST",sPersonelCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sPersonelOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sPersonelStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sPersonelInCome);   
		r.setNumberValue("NACCUMULATEDRESULT",sPersonelStrucRev - sPersonelCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sPersonelInCome - sPersonelOutCome);
		r.setNumberValue("WOBUDGETEDCOST",sPersonelBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sPersonelStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sPersonelBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sPersonelStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sPersonelPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sPersonelStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sPersonelPlndRev);
		r.setNumberValue("STCPLANNEDREV",sPersonelStrcPlndRev);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMMATERIAL: Material"));
		r.setNumberValue("NACCUMULATEDCOST",sMaterialCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sMaterialOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sMaterialStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sMaterialInCome);
		r.setNumberValue("NACCUMULATEDRESULT",sMaterialStrucRev - sMaterialCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sMaterialInCome - sMaterialOutCome);      
		r.setNumberValue("WOBUDGETEDCOST",sMaterialBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sMaterialStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sMaterialBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sMaterialStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sMaterialPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sMaterialStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sMaterialPlndRev);
		r.setNumberValue("STCPLANNEDREV",sMaterialStrcPlndRev);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMEXTERNAL: External"));
		r.setNumberValue("NACCUMULATEDCOST",sExternalCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sExternalOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sExternalStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sExternalInCome);
		r.setNumberValue("NACCUMULATEDRESULT",sExternalStrucRev - sExternalCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sExternalInCome - sExternalOutCome);      
		r.setNumberValue("WOBUDGETEDCOST",sExternalBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sExternalStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sExternalBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sExternalStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sExternalPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sExternalStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sExternalPlndRev);
		r.setNumberValue("STCPLANNEDREV",sExternalStrcPlndRev);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMEXPENCES: Expences"));
		r.setNumberValue("NACCUMULATEDCOST",sExpenceCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sExpenceOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sExpenceStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sExpenceInCome);   
		r.setNumberValue("NACCUMULATEDRESULT",sExpenceStrucRev - sExpenceCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sExpenceInCome - sExpenceOutCome);   
		r.setNumberValue("WOBUDGETEDCOST",sExpenceBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sExpenceStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sExpenceBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sExpenceStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sExpencePlndCost);
		r.setNumberValue("STCPLANNEDCOST",sExpenceStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sExpencePlndRev);
		r.setNumberValue("STCPLANNEDREV",sExpenceStrcPlndRev);
		itemset0.setRow(r);   

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMFIXEDPRICE: Fixed Price"));
		r.setNumberValue("NACCUMULATEDCOST",sFixedPriceCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sFixedPriceOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sFixedPriceStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sFixedPriceInCome); 
		r.setNumberValue("NACCUMULATEDRESULT",sFixedPriceStrucRev - sFixedPriceCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sFixedPriceInCome - sFixedPriceOutCome);   
		r.setNumberValue("WOBUDGETEDCOST",sFixedPriceBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sFixedPriceStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sFixedPriceBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sFixedPriceStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sFixedPricePlndCost);
		r.setNumberValue("STCPLANNEDCOST",sFixedPriceStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sFixedPricePlndRev);
		r.setNumberValue("STCPLANNEDREV",sFixedPriceStrcPlndRev);
		itemset0.setRow(r);  

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMTOOLS: Tools/Facilities"));
		r.setNumberValue("NACCUMULATEDCOST",sToolCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sToolOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sToolStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sToolInCome);
		r.setNumberValue("NACCUMULATEDRESULT",sToolStrucRev - sToolCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sToolInCome - sToolOutCome);      
		r.setNumberValue("WOBUDGETEDCOST",sToolBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sToolStrcBudgtCost);
		r.setNumberValue("WOBUDGETEDREV",sToolBudgRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sToolStrcBudgRev);
		r.setNumberValue("PLANNEDCOST",sToolPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sToolStrcPlndCost);
		r.setNumberValue("PLANNEDREV",sToolPlndRev);
		r.setNumberValue("STCPLANNEDREV",sToolStrcPlndRev);
		itemset0.setRow(r); 

		sTotalOutCome    = sPersonelOutCome+sMaterialOutCome+sExternalOutCome+sExpenceOutCome+sFixedPriceOutCome+sToolOutCome ;
		sTotalCost       = sPersonelCost+sMaterialCost+sExternalCost+sExpenceCost+sFixedPriceCost+sToolCost ;
		sTotalInCome     = sPersonelInCome+sMaterialInCome+sExternalInCome+sExpenceInCome+sFixedPriceInCome+sToolInCome;
		sTotalStrucRev   = sPersonelStrucRev+sMaterialStrucRev+sExternalStrucRev+sExpenceStrucRev+sFixedPriceStrucRev+sToolStrucRev;
		sTotalWoBudCst   = sPersonelBudgtCost+sMaterialBudgtCost+sExternalBudgtCost+sExpenceBudgtCost+sFixedPriceBudgtCost+sToolBudgtCost;
		sTotalStcBudCst  = sPersonelStrcBudgtCost+sMaterialStrcBudgtCost+sExternalStrcBudgtCost+sExpenceStrcBudgtCost+sFixedPriceStrcBudgtCost+sToolStrcBudgtCost;
		sTotalWoBudRev   = sPersonelBudgRev+sMaterialBudgRev+sExternalBudgRev+sExpenceBudgRev+sFixedPriceBudgRev+sToolBudgRev;
		sTotalStcBudRev  = sPersonelStrcBudgRev+sMaterialStrcBudgRev+sExternalStrcBudgRev+sExpenceStrcBudgRev+sFixedPriceStrcBudgRev+sToolStrcBudgRev;
		sTotalPlndCst    = sPersonelPlndCost+sMaterialPlndCost+sExternalPlndCost+sExpencePlndCost+sFixedPricePlndCost+sToolPlndCost;
		sTotalStcPlndCst = sPersonelStrcPlndCost+sMaterialStrcPlndCost+sExternalStrcPlndCost+sExpenceStrcPlndCost+sFixedPriceStrcPlndCost+sToolStrcPlndCost;
		sTotlaPlndRev    = sPersonelPlndRev+sMaterialPlndRev+sExternalPlndRev+sExpencePlndRev+sFixedPricePlndRev+sToolPlndRev;
		sTotalStcPlndRev = sPersonelStrcPlndRev+sMaterialStrcPlndRev+sExternalStrcPlndRev+sExpenceStrcPlndRev+sFixedPriceStrcPlndRev+sToolStrcPlndRev;

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLESMTOTAL: Total"));
		r.setNumberValue("NACCUMULATEDCOST",sTotalCost);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sTotalOutCome);
		r.setNumberValue("NACCUMULATEDREVENUE",sTotalStrucRev);
		r.setNumberValue("NACCUMULATEDREVENUESINGLE",sTotalInCome); 
		r.setNumberValue("NACCUMULATEDRESULT",sTotalStrucRev - sTotalCost);
		r.setNumberValue("NACCUMULATEDRESULTSINGLE",sTotalInCome - sTotalOutCome);   
		r.setNumberValue("WOBUDGETEDCOST",sTotalWoBudCst);
		r.setNumberValue("WOSTCBUDGETEDCOST",sTotalStcBudCst);
		r.setNumberValue("WOBUDGETEDREV",sTotalWoBudRev);
		r.setNumberValue("WOSTCBUDGETEDREV",sTotalStcBudRev);
		r.setNumberValue("PLANNEDCOST",sTotalPlndCst);
		r.setNumberValue("STCPLANNEDCOST",sTotalStcPlndCst);
		r.setNumberValue("PLANNEDREV",sTotlaPlndRev);
		r.setNumberValue("STCPLANNEDREV",sTotalStcPlndRev);
		itemset0.setRow(r); 

		itemset0.first(); 

		if ("TRUE".equals(sCheckValue))
			cbhasconnections_var = "checked";
		else
			cbhasconnections_var	= "unchecked";

		costGraph_File = "";
		sExtOfGraph = "";
		costGraph_Width = 0;
		costGraph_Height = 0;
		sTitleOfGraph = "";
		windowWidth = 100;
		windowHeight = 100; 
	}

//=========GRAPH FOR WORK ORDER COST====================

	public void  createCostGraph()
	{
		ASPManager mgr = getASPManager();

		int woGraphPoints;
		String costTyptemp;
		String costGraph_Ext;
		double woCostTemp; 
		double strCostTemp;
		String sGraphTitle;
		String sLeftTitle;

		int noOfRows = itemset0.countRows(); 
		GraphInNewWind = false;
		int wono = sWorkOrderNoTree;

		String[] woCostType  = new String[noOfRows];   
		double[] woCost = new double[noOfRows];
		double[] structureCost = new double[noOfRows];

		itemset0.first();

		for (int i = 0 ; i < noOfRows ; i++)
		{
			woCost[i] = itemset0.getRow().getNumberValue("NACCUMULATEDCOSTSINGLE");
			woCostType[i]  = itemset0.getRow().getValue("SWOCOSTTYPE");
			structureCost[i] = itemset0.getRow().getNumberValue("NACCUMULATEDCOST");

			itemset0.forward(1);
		}

		woGraphPoints = 6;

		for (int i = 0 ; i < noOfRows ; i++)
		{
			if (woCost[i]>0 || structureCost[i]>0)
				graphflagWo = 1;
		}

		sGraphTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMWOCOSTTITLE: Work Order Cost");
		sLeftTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMAMOUNTTR: Amount");

		GraphInNewWind = true;

		drawGraphPic(wono,sGraphTitle,sLeftTitle,noOfRows,woCostType,woCost,structureCost);
	}

//=========GRAPH FOR WORK ORDER REVENUE====================

	public void  createRevenueGraph()
	{
		ASPManager mgr = getASPManager();

		int woGraphPoints;
		String costTyptemp;
		String costGraph_Ext;
		double woRevenueTemp; 
		double strRevenueTemp;
		String sGraphTitle;
		String sLeftTitle;   

		int noOfRows = itemset0.countRows(); 
		revGraphInNewWind = false;
		int wono = sWorkOrderNoTree;

		String[] woCostType  = new String[noOfRows];   
		double[] woRevenue = new double[noOfRows];
		double[] structureRevenue = new double[noOfRows];

		itemset0.first();

		for (int i = 0; i < noOfRows; i++)
		{
			woRevenue[i] = itemset0.getRow().getNumberValue("NACCUMULATEDREVENUESINGLE");
			woCostType[i]  = itemset0.getRow().getValue("SWOCOSTTYPE");
			structureRevenue[i] = itemset0.getRow().getNumberValue("NACCUMULATEDREVENUE");

			itemset0.forward(1);
		}

		woGraphPoints = 6;

		for (int i = 0; i < noOfRows; i++)
		{
			if (woRevenue[i]>0 || structureRevenue[i]>0)
				graphflagWo = 1;
		}

		sGraphTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMWOREVTITLE: Work Order Revenue");
		sLeftTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMAMOUNTTR: Amount");

		revGraphInNewWind = true;

		drawGraphPic(wono,sGraphTitle,sLeftTitle,noOfRows,woCostType,woRevenue,structureRevenue);   
	}

//=========GRAPH FOR WORK ORDER MARGIN====================

	public void  createMarginGraph()
	{
		ASPManager mgr = getASPManager();

		int woGraphPoints;
		String sGraphTitle;
		String sLeftTitle;   

		int noOfRows = itemset0.countRows(); 
		marginGraphInNewWind = false;
		int wono = sWorkOrderNoTree;

		String[] woCostType  = new String[noOfRows];   
		double[] woMargin = new double[noOfRows];
		double[] structureMargin = new double[noOfRows];

		itemset0.first();

		for (int i = 0; i < noOfRows; i++)
		{
			woMargin[i] = itemset0.getRow().getNumberValue("NACCUMULATEDRESULTSINGLE");
			woCostType[i]  = itemset0.getRow().getValue("SWOCOSTTYPE");
			structureMargin[i] = itemset0.getRow().getNumberValue("NACCUMULATEDRESULT");

			itemset0.forward(1);
		}

		woGraphPoints = 6;

		for (int i = 0 ; i < noOfRows; i++)
		{
			if (woMargin[i]>0 || structureMargin[i]>0)
				graphflagWo = 1;
		}

		sGraphTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMWOMARTITLE: Work Order Profit Margin");
		sLeftTitle = mgr.translate("PCMWWORKORDERCOSTTABLESMAMOUNTTR: Amount");

		marginGraphInNewWind = true;

		drawGraphPic(wono,sGraphTitle,sLeftTitle,noOfRows,woCostType,woMargin,structureMargin);

	}    


	public void drawGraphPic(int wono,String sGraphTitle,String sLeftTitle,int noOfRows,String[] woCostType,double[] woCost,double[] structureCost)
	{
		int woGraphPoints = 6;
		String costTyptemp;
		String costGraph_Ext;
		double woMarginTemp; 
		double strMarginTemp;

		ASPManager mgr = getASPManager();      

		ASPGraph costGraph = mgr.newASPGraph(ASPGraph.COLUMNCHART);

		costGraph.setGraphTitle(sGraphTitle+" "+wono);
		costGraph.setGraphTitleFont(new Font(costGraph.getGraphTitleFont().getFontName(),costGraph.getGraphTitleFont().BOLD,costGraph.getGraphTitleFont().getSize()+15));

		costGraph.setLeftTitle(sLeftTitle);
		costGraph.setLeftTitleFont(new Font(costGraph.getLeftTitleFont().getFontName(),costGraph.getLeftTitleFont().getStyle(),costGraph.getLeftTitleFont().getSize()+2));

		costGraph.setBottomTitle(mgr.translate("PCMWWORKORDERCOSTTABLESMCOSTTYPE: Cost Type"));
		costGraph.setBottomTitleFont(new Font(costGraph.getBottomTitleFont().getFontName(),costGraph.getBottomTitleFont().getStyle(),costGraph.getBottomTitleFont().getSize()+2));

		Vector colorVector = new Vector(2);
		colorVector.add(Color.green);
		colorVector.add(Color.blue);              
		costGraph.setColors(colorVector);

		costGraph.setAntiAliasing(true);

		costGraph.setWidth(700);
		costGraph.setHeight(350);

		costGraph.setColumnOutline(true);
		costGraph.setColumnOutlineColor(Color.black);

		costGraph.setGraphOutline(true);
		costGraph.setGridOption(ASPGraph.HORIZONTAL_GRID);

		costGraph.setLegendPosition(1);

		costGraph.setShowValues(true);     

		costGraph.setNumSets(2);
		costGraph.setNumPoints(noOfRows);
		costGraph.setLabelFont(new Font(costGraph.getLabelFont().getFontName(),costGraph.getLabelFont().getStyle(),costGraph.getLabelFont().getSize()+1));

		costGraph.setLegend(1,Integer.toString(wono)); 
		costGraph.setLegend(2,mgr.translate("PCMWWORKORDERCOSTTABLESMSTRUCT: Structure"));
		costGraph.setLegendFont(new Font(costGraph.getLegendFont().getFontName(),costGraph.getLegendFont().getStyle(),costGraph.getLegendFont().getSize()+4));


		for (int i=1; i < 3; i++)
		{
			costGraph.setThisSet(i) ;

			int pnum = 1;
			for (int count4 = 0; count4 < noOfRows; count4++)
			{
				costTyptemp   = woCostType[count4];
				{   
					woMarginTemp    = woCost[count4];
					strMarginTemp = structureCost[count4];
					if (i == 1)
						costGraph.setData(pnum,woMarginTemp);
					else
						costGraph.setData(pnum,strMarginTemp);              

					costGraph.setLabel(pnum,costTyptemp);                                       

					pnum++;   
				}
			}
		}

		costGraph_File = costGraph.drawGraph();
		costGraph_Ext = costGraph.getOutput();
		costGraph_Width = costGraph.getWidth();
		costGraph_Height = costGraph.getHeight();

		sTitleOfGraph = sGraphTitle;

		windowWidth = costGraph_Width+260;
		windowHeight = costGraph_Height+260;

		sExtOfGraph = costGraph_Ext.substring(1,costGraph_Ext.length());  

	}

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		dummyblk = mgr.newASPBlock("DUMMY");

		f = dummyblk.addField("PERSONEL_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");

		f = dummyblk.addField("MATERIAL_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");

		f = dummyblk.addField("EXTERNAL_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");

		f = dummyblk.addField("EXPENCE_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");

		f = dummyblk.addField("FIXED_PRICE_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");   

		f = dummyblk.addField("TOOL_OUTCOME","Money");
		f.setHidden();
		f.setFunction("''");                

		f = dummyblk.addField("CURRENT_YEAR");
		f.setHidden();
		f.setFunction("TO_CHAR(sysdate,'yyyy')");

		f = dummyblk.addField("CHECK_VALUE");
		f.setHidden();
		f.setFunction("''");

		dummyblk.setView("DUAL"); 

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("WO_NO","Number");
		f.setHidden();
		f.setFunction("0");

		headblk.setView("DUAL");

		headset = headblk.getASPRowSet();

		////////////////////////////////////////////////////////////////////////
		///////////////////////ITEMBLK//////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////

		itemblk0 = mgr.newASPBlock("ITEM0");

		f = itemblk0.addField("ITEM0_OBJID");
		f.setHidden();
		f.setDbName("OBJID");
		f.setFunction("''");

		f = itemblk0.addField("ITEM0_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");
		f.setFunction("''");

		f = itemblk0.addField("SWOCOSTTYPE");
		f.setSize(18);
		f.setMandatory();
		f.setLabel("PCMWWORKORDERCOSTTABLESMSWOCOSTTYPE: Cost Type");
		f.setReadOnly();
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDCOSTSINGLE", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDCOSTSINGLE: Work Order Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDCOST: Structure Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDREVENUESINGLE", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDREVENUESINGLE: Work Order Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDREVENUE", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDREVENUE: Structure Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDRESULTSINGLE", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDRESULTSINGLE: Work Order Profit Margin");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDRESULT", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMNACCUMULATEDRESULT: Structure Profit Margin");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("WOBUDGETEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMWORKORDBUDGCOST: Budgeted Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("WOSTCBUDGETEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMWORKORDSTRUBUDGCOST: Structure Budgeted Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("WOBUDGETEDREV", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMWORKORDBUDGREV: Budgeted Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("WOSTCBUDGETEDREV", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMWORKORDSTRBUDGREV: Structure Budgeted Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("PLANNEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMPLANEDCOST: Planned Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("STCPLANNEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMSTCPLANEDCOST: Structure Planned Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("PLANNEDREV", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMPLANEDREV: Planned Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("STCPLANNEDREV", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESMSTCPLANEDCOSTREV: Structure Planned Revenue");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk0.addField("GRAPHTYPEVAL");
		f.setFunction("''");
		f.setHidden();

		itemblk0.setView("");
		itemblk0.defineCommand("","New__,Modify__,Remove__");
		itemblk0.setMasterBlock(headblk);
		itemblk0.setTitle(mgr.translate("PCMWWORKORDERCOSTTABLESMCOSTTABTRA: Cost/Revenue"));

		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);

		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");

		itembar0.disableCommand(itembar0.NEWROW);

		itembar0.addCustomCommand("createCostGraph",mgr.translate("PCMWWORKORDERCOSTTABLESMHANCOSTGRP: Cost Graph..."));
		itembar0.addCustomCommand("createRevenueGraph",mgr.translate("PCMWWORKORDERCOSTTABLESMHANREVGRP: Revenue Graph..."));
		itembar0.addCustomCommand("createMarginGraph",mgr.translate("PCMWWORKORDERCOSTTABLESMHANMARGRP: Profit Margin..."));      

		// 031230  ARWILK  Begin  (Links with multirow RMB's)
		itembar0.enableMultirowAction();
		// 031230  ARWILK  End  (Links with multirow RMB's)

		itembar0.disableCommand(itembar0.EDITROW);
		itembar0.disableCommand(itembar0.DELETE);
		itembar0.disableCommand(itembar0.DUPLICATEROW);

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.enableRowCounter();
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();    
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
		itemlay0.setFieldSpan("SWOCOSTTYPE",1,2);


		b = mgr.newASPBlock("WO_STATUS_ID");

		b.addField("CLIENT_VALUES0");
	}

	public void  adjust()
	{
		ASPManager mgr = getASPManager(); 
	}
//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "COSTTABLESM : Cost Table SM";
	}

	protected String getTitle()
	{
		return "COSTTABLESM : Cost Table SM";
	}

	protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();    

		out.append("<html>\n");
		out.append("<head>");
		out.append(mgr.generateHeadTag("PCMWWORKORDERCOSTTABLESMTITLE: Cost Analysis"));
		out.append("</head>\n");
		out.append("<body ");
		out.append(mgr.generateBodyTag());
		out.append(" >\n");
		out.append("<form ");
		out.append(mgr.generateFormTag());
		out.append(" >\n");

		out.append(itemlay0.show());

		out.append(mgr.generateClientScript());
		out.append("</form>\n");
		out.append("</body>\n");
		out.append("</html>\n");

		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(GraphInNewWind);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   graphWindow = window.open(\"PaintGraphPic.page?COGRAPHPATH=\"+URLClientEncode('");
		appendDirtyJavaScript(costGraph_File);
		appendDirtyJavaScript("')+\"&COGRAPHEXT=\"+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sExtOfGraph));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("')+\"&COGRAPHWIDTH=\"+");
		appendDirtyJavaScript(costGraph_Width);					//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&COGRAPHHEIGHT=\"+");
		appendDirtyJavaScript(costGraph_Height);				//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&TITLEOFGRAPH=\"+URLClientEncode('");
		appendDirtyJavaScript(sTitleOfGraph);
		appendDirtyJavaScript("'),\"HANGRAPHWIND\",\"alwaysRaised,resizable,scrollbars=yes,width=");
		appendDirtyJavaScript(windowWidth);
		appendDirtyJavaScript(",Height=");
		appendDirtyJavaScript(windowHeight);
		appendDirtyJavaScript("\");           \n");
		appendDirtyJavaScript("   graphWindow.moveTo(150,100);\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(revGraphInNewWind);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   graphWindow = window.open(\"PaintGraphPic.page?COGRAPHPATH=\"+URLClientEncode('");
		appendDirtyJavaScript(costGraph_File);
		appendDirtyJavaScript("')+\"&COGRAPHEXT=\"+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sExtOfGraph));			//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("')+\"&COGRAPHWIDTH=\"+");
		appendDirtyJavaScript(costGraph_Width);							//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&COGRAPHHEIGHT=\"+");
		appendDirtyJavaScript(costGraph_Height);						//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&TITLEOFGRAPH=\"+URLClientEncode('");
		appendDirtyJavaScript(sTitleOfGraph);
		appendDirtyJavaScript("'),\"HANGRAPHWIND\",\"alwaysRaised,resizable,scrollbars=yes,width=");
		appendDirtyJavaScript(windowWidth);
		appendDirtyJavaScript(",Height=");
		appendDirtyJavaScript(windowHeight);
		appendDirtyJavaScript("\");           \n");
		appendDirtyJavaScript("   graphWindow.moveTo(150,100);\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("if (");
		appendDirtyJavaScript(marginGraphInNewWind);
		appendDirtyJavaScript(")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   graphWindow = window.open(\"PaintGraphPic.page?COGRAPHPATH=\"+URLClientEncode('");
		appendDirtyJavaScript(costGraph_File);
		appendDirtyJavaScript("')+\"&COGRAPHEXT=\"+URLClientEncode('");
		appendDirtyJavaScript(mgr.encodeStringForJavascript(sExtOfGraph));	//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("')+\"&COGRAPHWIDTH=\"+");
		appendDirtyJavaScript(costGraph_Width);					//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&COGRAPHHEIGHT=\"+");
		appendDirtyJavaScript(costGraph_Height);				//XSS_Safe AMNILK 20070726
		appendDirtyJavaScript("+\"&TITLEOFGRAPH=\"+URLClientEncode('");
		appendDirtyJavaScript(sTitleOfGraph);
		appendDirtyJavaScript("'),\"HANGRAPHWIND\",\"alwaysRaised,resizable,scrollbars=yes,width=");
		appendDirtyJavaScript(windowWidth);
		appendDirtyJavaScript(",Height=");
		appendDirtyJavaScript(windowHeight);
		appendDirtyJavaScript("\");           \n");
		appendDirtyJavaScript("   graphWindow.moveTo(150,100);\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("function selectActiveCommand(e){\n");
		appendDirtyJavaScript("}\n");
		appendDirtyJavaScript("function deselectActiveCommand(e){}\n");

		return out;    
	}
}
