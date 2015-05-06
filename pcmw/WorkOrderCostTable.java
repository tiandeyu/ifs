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
*  File        : WorkOrderCostTable.java 
*  Created     : ARWILK  010307  Created.
*  Modified    :
*  ARWILK  010821  Modified graphs to support webkit 3.5.0 beta 5.
*  CHCRLK  030123  Added parameter TOOL_OUTCOME.
*  CHAMLK  031020  Modified function okFindTree() to display cost type Tools/Facilities
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

public class WorkOrderCostTable extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCostTable");

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
	private String GraphInNewWind;
	private String costGraph_File;
	private String sExtOfGraph;
	private String sTitleOfGraph; 
	private int costGraph_Width;
	private int costGraph_Height;
	private int windowWidth;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderCostTable(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		frm = mgr.getASPForm();
		ctx = mgr.getASPContext();
		fmt = getASPHTMLFormatter();
		log = mgr.getASPLog();

		activetab = ctx.readValue("ACTIVETAB","0");
		sWorkOrderNoTree = ctx.readNumber("CTXWORTRR",sWorkOrderNoTree);

		graphflagWo = 0;   

		GraphInNewWind = "false";

		if (mgr.commandBarActivated())
		{
			eval(mgr.commandBarFunction());    
		}
		else if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECTED_WO_NO")))
		{
			sWorkOrderNoTree = toInt(mgr.readValue("CONNECTED_WO_NO"));
			okFindTree();          
		}
		else
		{
			costGraph_File = "";
			sExtOfGraph = "";
			sTitleOfGraph = "";
			costGraph_Width = 0;
			costGraph_Height = 0;
			windowWidth = 100;
			windowHeight = 100;      
		}

		adjust();

		ctx.writeNumber("CTXWORTRR",sWorkOrderNoTree);
		ctx.writeValue("ACTIVETAB",activetab);
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void  okFindTree()
	{
		ASPManager mgr = getASPManager();

		double sPersonelOutCome0;
		double sMaterialOutCome0;
		double sExternalOutCome0;
		double sToolOutCome0;
		double sPersonelCost0;
		double sMaterialCost0;
		double sExternalCost0;
		double sToolCost0;
		double sPersonelBudgtCost;
		double sMaterialBudgtCost;
		double sExternalBudgtCost;
		double sToolBudgtCost;
		double sPersonelStrcBudgtCost;
		double sMaterialStrcBudgtCost;
		double sExternalStrcBudgtCost;
		double sToolStrcBudgtCost;
		double sPersonelPlndCost;
		double sMaterialPlndCost;
		double sExternalPlndCost;
		double sToolPlndCost;
		double sPersonelStrcPlndCost;
		double sMaterialStrcPlndCost;
		double sExternalStrcPlndCost;
		double sToolStrcPlndCost;
		String sCheckValue;

		double sTotalOutCome0;  
		double sTotalCost0;
		double sTotalWoBudCst;
		double sTotalStcBudCst;
		double sTotalPlndCst;
		double sTotalStcPlndCst;

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

		cmd = trans.addCustomCommand("GETSTRCCOST","Work_Order_Coding_Utility_API.Get_Structure_Cost");
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

		cmd = trans.addCustomFunction("CHECKVAL","WORK_ORDER_CONNECTION_API.Has_Connection_Down","CHECK_VALUE");
		cmd.addParameter("WO_NO",Integer.toString(sWorkOrderNoTree));

		trans = mgr.perform(trans);

		sPersonelOutCome0   = trans.getNumberValue("ALLOUTCOME/DATA/PERSONEL_OUTCOME");
		sMaterialOutCome0   = trans.getNumberValue("ALLOUTCOME/DATA/MATERIAL_OUTCOME");
		sExternalOutCome0   = trans.getNumberValue("ALLOUTCOME/DATA/EXTERNAL_OUTCOME");
		sToolOutCome0       = trans.getNumberValue("ALLOUTCOME/DATA/TOOL_OUTCOME");

		sPersonelCost0   = trans.getNumberValue("GETSTRCCOST/DATA/PERSONEL_OUTCOME");
		sMaterialCost0   = trans.getNumberValue("GETSTRCCOST/DATA/MATERIAL_OUTCOME");
		sExternalCost0   = trans.getNumberValue("GETSTRCCOST/DATA/EXTERNAL_OUTCOME");
		sToolCost0       = trans.getNumberValue("GETSTRCCOST/DATA/TOOL_OUTCOME");

		sPersonelBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/MATERIAL_OUTCOME");
		sExternalBudgtCost   = trans.getNumberValue("WOBUDCOST/DATA/EXTERNAL_OUTCOME");
		sToolBudgtCost       = trans.getNumberValue("WOBUDCOST/DATA/TOOL_OUTCOME");

		sPersonelStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/MATERIAL_OUTCOME");
		sExternalStrcBudgtCost   = trans.getNumberValue("STCBUDCOST/DATA/EXTERNAL_OUTCOME");
		sToolStrcBudgtCost       = trans.getNumberValue("STCBUDCOST/DATA/TOOL_OUTCOME");

		sPersonelPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/MATERIAL_OUTCOME");
		sExternalPlndCost    = trans.getNumberValue("WOPLNDCOST/DATA/EXTERNAL_OUTCOME");
		sToolPlndCost        = trans.getNumberValue("WOPLNDCOST/DATA/TOOL_OUTCOME");

		sPersonelStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/PERSONEL_OUTCOME");
		sMaterialStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/MATERIAL_OUTCOME");
		sExternalStrcPlndCost    = trans.getNumberValue("STCPLNDCOST/DATA/EXTERNAL_OUTCOME");
		sToolStrcPlndCost        = trans.getNumberValue("STCPLNDCOST/DATA/TOOL_OUTCOME");

		sCheckValue   = trans.getValue("CHECKVAL/DATA/CHECK_VALUE");

		trans.clear();

		itemset0.clear();

		ASPBuffer data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		ASPBuffer r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLEPERSON: Personnel"));
		r.setNumberValue("NACCUMULATEDCOST",sPersonelCost0);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sPersonelOutCome0);
		r.setNumberValue("WOBUDGETEDCOST",sPersonelBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sPersonelStrcBudgtCost);
		r.setNumberValue("PLANNEDCOST",sPersonelPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sPersonelStrcPlndCost);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLEMATERIAL: Material"));
		r.setNumberValue("NACCUMULATEDCOST",sMaterialCost0);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sMaterialOutCome0);
		r.setNumberValue("WOBUDGETEDCOST",sMaterialBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sMaterialStrcBudgtCost);
		r.setNumberValue("PLANNEDCOST",sMaterialPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sMaterialStrcPlndCost);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLEEXTERNAL: External"));
		r.setNumberValue("NACCUMULATEDCOST",sExternalCost0);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sExternalOutCome0);
		r.setNumberValue("WOBUDGETEDCOST",sExternalBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sExternalStrcBudgtCost);
		r.setNumberValue("PLANNEDCOST",sExternalPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sExternalStrcPlndCost);
		itemset0.setRow(r);

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLETOOLS: Tools/Facilities"));
		r.setNumberValue("NACCUMULATEDCOST",sToolCost0);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sToolOutCome0);
		r.setNumberValue("WOBUDGETEDCOST",sToolBudgtCost);
		r.setNumberValue("WOSTCBUDGETEDCOST",sToolStrcBudgtCost);
		r.setNumberValue("PLANNEDCOST",sToolPlndCost);
		r.setNumberValue("STCPLANNEDCOST",sToolStrcPlndCost);
		itemset0.setRow(r);

		sTotalOutCome0   = sPersonelOutCome0+sMaterialOutCome0+sExternalOutCome0+sToolOutCome0;
		sTotalCost0      = sPersonelCost0+sMaterialCost0+sExternalCost0+sToolCost0;
		sTotalWoBudCst   = sPersonelBudgtCost+sMaterialBudgtCost+sExternalBudgtCost+sToolBudgtCost;
		sTotalStcBudCst  = sPersonelStrcBudgtCost+sMaterialStrcBudgtCost+sExternalStrcBudgtCost+sToolStrcBudgtCost;
		sTotalPlndCst    = sPersonelPlndCost+sMaterialPlndCost+sExternalPlndCost+sToolPlndCost;
		sTotalStcPlndCst = sPersonelStrcPlndCost+sMaterialStrcPlndCost+sExternalStrcPlndCost+sToolStrcPlndCost;

		data = trans.getBuffer("ITEM0/DATA");  
		itemset0.addRow(data);
		r = itemset0.getRow();
		r.setValue("SWOCOSTTYPE",mgr.translate("PCMWWORKORDERCOSTTABLETOTAL: Total"));
		r.setNumberValue("NACCUMULATEDCOST",sTotalCost0);
		r.setNumberValue("NACCUMULATEDCOSTSINGLE",sTotalOutCome0);
		r.setNumberValue("WOBUDGETEDCOST",sTotalWoBudCst);
		r.setNumberValue("WOSTCBUDGETEDCOST",sTotalStcBudCst);
		r.setNumberValue("PLANNEDCOST",sTotalPlndCst);
		r.setNumberValue("STCPLANNEDCOST",sTotalStcPlndCst);
		itemset0.setRow(r); 

		itemset0.first(); 

		costGraph_File = "";
		sExtOfGraph = "";
		sTitleOfGraph = "";
		costGraph_Width = 0;
		costGraph_Height = 0;
		windowWidth = 100;
		windowHeight = 100; 

	}

//=========GRAPH FOR WORK ORDER COST====================

	public void  createCostGraph()
	{
		ASPManager mgr = getASPManager();

		int noOfRows;
		int wono;
		int woGraphPoints;
		int pnum;
		String costGraph_Ext;
		String costTyptemp;
		double woCostTemp;
		double strCostTemp;

		noOfRows = itemset0.countRows(); 
		GraphInNewWind = "false";
		wono = sWorkOrderNoTree;

		double[] woCost = new double[noOfRows];
		double[] structureCost = new double[noOfRows];
		String[] woCostType  = new String[noOfRows];

		itemset0.first();

		for (int i = 0 ;i < noOfRows ; i++)
		{
			woCost[i] = toInt(itemset0.getRow().getNumberValue("NACCUMULATEDCOSTSINGLE"));
			woCostType[i]  = itemset0.getRow().getValue("SWOCOSTTYPE");
			structureCost[i] = toInt(itemset0.getRow().getNumberValue("NACCUMULATEDCOST"));        

			if (i != noOfRows-1)
				itemset0.next();
		}

		woGraphPoints = 4;

		for (int i = 0 ; i < noOfRows ; i++)
		{
			if (woCost[i]>0 || structureCost[i]>0)
				graphflagWo = 1;
		}   

		ASPGraph costGraph = mgr.newASPGraph(ASPGraph.COLUMNCHART);

		costGraph.setGraphTitle(mgr.translate("PCMWWORKORDERCOSTTABLEWOCOSTTITLE: Work Order Cost " )+wono);
		costGraph.setGraphTitleFont(new Font(costGraph.getGraphTitleFont().getFontName(),costGraph.getGraphTitleFont().BOLD,costGraph.getGraphTitleFont().getSize()+15));

		costGraph.setLeftTitle(mgr.translate("PCMWWORKORDERCOSTTABLEAMOUNT: Amount"));
		costGraph.setLeftTitleFont(new Font(costGraph.getLeftTitleFont().getFontName(),costGraph.getLeftTitleFont().getStyle(),costGraph.getLeftTitleFont().getSize()+2));

		costGraph.setBottomTitle(mgr.translate("PCMWWORKORDERCOSTTABLECOSTTYPE: Cost Type"));
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
		costGraph.setLegend(2,mgr.translate("PCMWWORKORDERCOSTTABLESTRUCT: Structure"));
		costGraph.setLegendFont(new Font(costGraph.getLegendFont().getFontName(),costGraph.getLegendFont().getStyle(),costGraph.getLegendFont().getSize()+4));

		for (int i=1;i<3;i++)
		{
			costGraph.setThisSet(i) ;

			pnum = 1;

			for (int count4 = 0;count4 < noOfRows;count4++)
			{
				costTyptemp   = woCostType[count4];          

				woCostTemp    = woCost[count4];
				strCostTemp = structureCost[count4];

				if (i == 1)
					costGraph.setData(pnum,woCostTemp);
				else
					costGraph.setData(pnum,strCostTemp);             

				costGraph.setLabel(pnum,costTyptemp);
				pnum++;        
			}
		}

		costGraph_File = costGraph.drawGraph();
		costGraph_Ext = costGraph.getOutput();
		costGraph_Width = costGraph.getWidth();
		costGraph_Height = costGraph.getHeight();

		sTitleOfGraph = "Work Order Cost";
		windowWidth = costGraph_Width+260;
		windowHeight = costGraph_Height+260;
		sExtOfGraph = costGraph_Ext.substring(1,costGraph_Ext.length());

		GraphInNewWind = "true"; 
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
		f.setLabel("PCMWWORKORDERCOSTTABLESWOCOSTTYPE: Cost Type");
		f.setReadOnly();
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDCOSTSINGLE", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLENACCUMULATEDCOSTSINGLE: Work Order Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("NACCUMULATEDCOST", "Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLENACCUMULATEDCOST: Structure Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("WOBUDGETEDCOST","Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLEWORKORDBUDGCOST: Budgeted Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("WOSTCBUDGETEDCOST","Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLEWORKORDSTRUBUDGCOST: Structure Budgeted Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("PLANNEDCOST","Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLEPLANEDCOST: Planned Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("STCPLANNEDCOST","Money");
		f.setSize(23);
		f.setLabel("PCMWWORKORDERCOSTTABLESTCPLANEDCOST: Structure Planned Cost");
		f.setReadOnly();
		f.setAlignment("RIGHT");
		f.setFunction("''");

		f = itemblk0.addField("GRAPHTYPEVAL");
		f.setFunction("''");
		f.setHidden();

		itemblk0.setView("");
		itemblk0.defineCommand("","New__,Modify__,Remove__");
		itemblk0.setMasterBlock(headblk);
		itemblk0.setTitle(mgr.translate("PCMWWORKORDERCOSTTABLECOSTTABTRA: Cost"));

		itemset0 = itemblk0.getASPRowSet();

		itembar0 = mgr.newASPCommandBar(itemblk0);

		itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");

		itembar0.disableCommand(itembar0.NEWROW);

		itembar0.addCustomCommand("createCostGraph",mgr.translate("PCMWWORKORDERCOSTTABLEHANCOSTGRP: Cost Graph..."));

		// 031230  ARWILK  Begin  (Links with multirow RMB's)
		itembar0.enableMultirowAction();
		// 031230  ARWILK  End  (Links with multirow RMB's)

		itemtbl0 = mgr.newASPTable(itemblk0);
		itemtbl0.setTitle(mgr.translate("PCMWWORKORDERCOSTTABLEITM0: Cost"));
		itemtbl0.setWrap();

		itemlay0 = itemblk0.getASPBlockLayout();    
		itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT); 
		itembar0.disableCommand(itembar0.EDITROW);
		itembar0.disableCommand(itembar0.DELETE);
		itembar0.disableCommand(itembar0.DUPLICATEROW);

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
		return "COSTTABLE : Cost Table";
	}

	protected String getTitle()
	{
		return "COSTTABLE : Cost Table";
	}

	protected AutoString getContents() throws FndException
	{ 
		AutoString out = getOutputStream();
		out.clear();
		ASPManager mgr = getASPManager();     

		out.append("<html>\n");  
		out.append("<head>");
		out.append(mgr.generateHeadTag("PCMWWORKORDERCOSTTABLETITLE: Cost Analysis"));
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

		appendDirtyJavaScript("if ('");
		appendDirtyJavaScript(GraphInNewWind);
		appendDirtyJavaScript("' == 'true')\n");
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
