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
*  File        : WorkOrderNavigator.java 
*  Created     : 010308  Created Using the ASP file WorkOrderNavigator.asp
*  Modified    :  
*  SHFELK  010308  Corrected some conversion errors.
*  CHCRLK  010613  Modified overwritten validations.
*  INROLK  011002  WorkOrderNavigator is refreshed after creating a WO. call id 69794.
*  JEWILK  011011  Modified method 'selecAction()' and javascripts to transfer the correct wo_no when selecting the rmb actions.     
*  BUNILK  040116  Used TreeList class to generate navigator instead previous list.js.
* -------------------------------- Edge - SP1 Merge -------------------------
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  ThWilk  040324  Merge with SP1.
*  ARWILK  041201  Replaced deprecated method addMenuItem with addDefinedPopup.
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified methods addTransNodes(), searchItemNodes()
*  AMDILK  060904  Merged with the Bug Id 58216   
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderNavigator extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderNavigator");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPBlock headblk;
	private ASPRowSet headset;

	private ASPBlock itemblk;
	private ASPRowSet itemset;

	private ASPPopup node_popup;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private ASPQuery q;
	private int size;
	private String root_nodes;
	private String item_nodes;
	private int i;
	private String item_data;
	private ASPCommand cmd;
	private String topWoNo;
	private String sState;
	private String val;
	private String hypWoNo; 
	private TreeList rootOfNavigator;
	private TreeListNode root_node;
	private TreeListItem item_node;
	private String stateVal;
	private String errDescVal;
	private String contractVal;

	//===============================================================
	// Construction 
	//===============================================================
	public WorkOrderNavigator(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
		trans = mgr.newASPTransactionBuffer();

		if ( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
			createTree();
	}

//-----------------------------------------------------------------------------
//------------------------ SEARCH FUNCTIONS  --------------------------
//----------------------------------------------------------------------------- 

	public void createTree()
	{
		ASPManager mgr = getASPManager();

		rootOfNavigator = new TreeList(mgr);
		rootOfNavigator.setLabel(mgr.translate("PCMWWORKORDERNAVIGATORWORKORD: Work Orders")); 
		rootOfNavigator.setImage("../"+ mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + mgr.getConfigParameter("NAVIGATOR/" + mgr.getConfigParameter("NAVIGATOR/STYLE") + "/IMAGE/ROOT"));

		if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			hypWoNo = mgr.readValue("WO_NO","");
			addTransNodes();
		}
		else
			addInitialNodes();

		rootOfNavigator.setBaseTarget("main");
		rootOfNavigator.setTreePosition(0, 10);
		rootOfNavigator.setTreeAreaWidth(500);

		rootOfNavigator.setFolderOpenedImage("../" + mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + "/navigator_folder_opened.gif");
		rootOfNavigator.setFolderClosedImage("../" + mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + "/navigator_folder.gif");
	}

	public void validate() 
	{
		ASPManager mgr = getASPManager(); 

		String val = mgr.readValue("VALIDATE"); 

		if ("EXPAND_TREE".equals(val) )
			searchItemNodes();

		mgr.endResponse(); 
	}


	public void addInitialNodes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addQuery(headblk);
		q.addWhereCondition("WORK_ORDER_CONNECTION_API.Has_Connection_Up(WO_NO) = 'FALSE'");
		q.addWhereCondition("WORK_ORDER_CONNECTION_API.Has_Connection_Down(WO_NO) = 'TRUE'");
		q.addWhereCondition("CONTRACT IN ( SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
		q.includeMeta("ALL") ;
		q.setOrderByClause("WO_NO");  

		mgr.submit(trans);

		size = headset.countRows();
		headset.first();

		String sTargetForLink = "";
		String sNodeDescription = "";

		if (size > 0)
		{
			for (int i = 0; i < size; i++)
			{
				trans.clear();

				cmd = trans.addCustomFunction("STATEVAL","Work_Order_API.Get_Wo_Status_Id","STATE");
				cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

				cmd = trans.addCustomFunction("ERRDESC","Separate_Work_Order_API.Get_Err_Descr","ERR_DESC");
				cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

				cmd = trans.addCustomFunction("GETCONT","Separate_Work_Order_Api.Get_Contract","CONTRACT");
				cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

				trans = mgr.perform(trans);

				errDescVal = trans.getValue("ERRDESC/DATA/ERR_DESC");
				contractVal = trans.getValue("GETCONT/DATA/CONTRACT");
				stateVal = trans.getValue("STATEVAL/DATA/STATE");

				sTargetForLink = "ActiveSeparate2WOStructure.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + "&STATE=" + mgr.URLEncode(headset.getValue("STATE"));
				sNodeDescription = mgr.translate("WORKORDERNAVIGATORNODEDESCRIPTION: &1 - &2", (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")), (mgr.isEmpty(headset.getValue("STATE"))?"":headset.getValue("STATE")));
				root_node = rootOfNavigator.addNode(sNodeDescription,
													sTargetForLink,
													"",
													"&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + "");

				String rmbString = ";setRmbValues('"+ headset.getValue("WO_NO") + "','" + stateVal + "','" + errDescVal + "','" + contractVal + "');";
				root_node.addDefinedPopup(node_popup.generateCall() + rmbString);

				headset.next();
			}
		}
	}

	public void searchItemNodes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		q = trans.addEmptyQuery(itemblk);
		q.addWhereCondition("WO_NO = ?");
		q.addParameter("WO_NO", mgr.readValue("WO_NO",""));
		q.setOrderByClause("WO_NO");  
		q.includeMeta("ALL");

		mgr.submit(trans);

		size = itemset.countRows();
		itemset.first();

		String sTargetForLink = "";
		String sNodeDescription = "";
		String item_data = "";
		String numberOfChilds = "0";

		if (size > 0)
		{
			TreeList dummynode = new TreeList(mgr,"DUMMY");

			for (int i = 0; i < size; i++)
			{

				trans.clear();

				cmd = trans.addCustomFunction("STATEVAL","Work_Order_API.Get_Wo_Status_Id","STATE");
				cmd.addParameter("WO_NO",itemset.getValue("CONNECTED_WO_NO"));

				cmd = trans.addCustomFunction("ERRDESC","Separate_Work_Order_API.Get_Err_Descr","ERR_DESC");
				cmd.addParameter("WO_NO",itemset.getValue("CONNECTED_WO_NO"));

				cmd = trans.addCustomFunction("GETCONT","Separate_Work_Order_Api.Get_Contract","CONTRACT");
				cmd.addParameter("WO_NO",itemset.getValue("CONNECTED_WO_NO"));

				trans = mgr.perform(trans);

				errDescVal = trans.getValue("ERRDESC/DATA/ERR_DESC");
				contractVal = trans.getValue("GETCONT/DATA/CONTRACT");
				stateVal = trans.getValue("STATEVAL/DATA/STATE");

				sTargetForLink = "ActiveSeparate2WOStructure.page?WO_NO=" + mgr.URLEncode(itemset.getValue("CONNECTED_WO_NO")) + "&STATE=" + mgr.URLEncode(itemset.getValue("ITEM_STATE"));

				sNodeDescription = mgr.translate("WORKORDERNAVIGATORNODEDESCRIPTION: &1 - &2", (mgr.isEmpty(itemset.getValue("CONNECTED_WO_NO"))?"":itemset.getValue("CONNECTED_WO_NO")), (mgr.isEmpty(itemset.getValue("ITEM_STATE"))?"":itemset.getValue("ITEM_STATE")));

				root_node = dummynode.addNode(sNodeDescription,sTargetForLink,"","&WO_NO=" + mgr.URLEncode(itemset.getValue("CONNECTED_WO_NO")) + "");

				String rmbString = ";setRmbValues('"+ itemset.getValue("CONNECTED_WO_NO") + "','" + stateVal + "','" + errDescVal + "','" + contractVal + "');";
				root_node.addDefinedPopup(node_popup.generateCall() + rmbString);

				itemset.next();

			}
			item_data = dummynode.getDynamicNodeString();
		}
		mgr.responseWrite(item_data+"^"); 
	}

	public void addTransNodes()
	{
		ASPManager mgr = getASPManager();

		cmd = trans.addCustomFunction("CONWO","Work_Order_Connection_API.Get_Top_Parent","CONNECTED_WO_NO");
		cmd.addParameter("CONNECTED_WO_NO",hypWoNo);

		trans = mgr.perform(trans);

		topWoNo = trans.getValue("CONWO/DATA/CONNECTED_WO_NO");

		trans.clear();
		q = trans.addQuery("STATEQRY","ACTIVE_SEPARATE","STATE","WO_NO = ?","");
                q.addParameter("CONNECTED_WO_NO", topWoNo);

		cmd = trans.addCustomFunction("ERRDESC","Separate_Work_Order_API.Get_Err_Descr","ERR_DESC");
		cmd.addParameter("WO_NO",topWoNo);

		cmd = trans.addCustomFunction("GETCONT","Separate_Work_Order_Api.Get_Contract","CONTRACT");
		cmd.addParameter("WO_NO",topWoNo);

		trans = mgr.perform(trans);

		sState = trans.getValue("STATEQRY/DATA/STATE");
		errDescVal = trans.getValue("ERRDESC/DATA/ERR_DESC");
		contractVal = trans.getValue("GETCONT/DATA/CONTRACT");
		stateVal = trans.getValue("STATEVAL/DATA/STATE");

		String sTargetForLink = "";
		String sNodeDescription = "";

		sTargetForLink = "ActiveSeparate2WOStructure.page?WO_NO=" + mgr.URLEncode(topWoNo) + "&STATE=" + mgr.URLEncode(sState);

		sNodeDescription = mgr.translate("WORKORDERNAVIGATORNODEDESCRIPTION: &1 - &2", (mgr.isEmpty(topWoNo)?"":topWoNo), (mgr.isEmpty(sState)?"":sState));

		root_node = rootOfNavigator.addNode(sNodeDescription,
											sTargetForLink,
											"",
											"&WO_NO=" + mgr.URLEncode(topWoNo) + "");

		String rmbString = ";setRmbValues('"+ topWoNo + "','" + sState + "','" + errDescVal + "','" + contractVal + "');";
		root_node.addDefinedPopup(node_popup.generateCall() + rmbString);
	}
//-----------------------------------------------------------------------------
//------------------------ CUSTOM FUNCTIONS  --------------------------
//----------------------------------------------------------------------------- 

	public void  preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		headblk.addField("WO_NO");

		headblk.addField("STATE");

		headblk.addField("CONTRACT");

		headblk.addField("ERR_DESC").setHidden().setFunction("''");


		headblk.setView("ACTIVE_SEPARATE");
		headset = headblk.getASPRowSet();

		itemblk = mgr.newASPBlock("ITEM");

		itemblk.addField("CONNECTED_WO_NO");

		itemblk.addField("ITEM_STATE").setFunction("SEPARATE_WORK_ORDER_API.GET_STATE(:CONNECTED_WO_NO)");

		itemblk.setView("WORK_ORDER_CONNECTION");
		itemset = itemblk.getASPRowSet();



		node_popup = newASPPopup("workorderrmbs");
		node_popup.addItem("PCMWWORKORDERNAVIGATORPREPARE: Prepare...","jPrepare()");
		node_popup.addItem("PCMWWORKORDERNAVIGATORREPORTIN: Report In...","jReportIn()");
		node_popup.addItem("PCMWWORKORDERNAVIGATORCREATEWO: Create WO...","jCreateWo()");

		addToJSFile();
	}

	private void addToJSFile()
	{
		try
		{
			appendJavaScript("var jWoNo;\n");
			appendJavaScript("var jState;\n");
			appendJavaScript("var jErrDesc;\n");
			appendJavaScript("var jCont;\n");

			appendJavaScript("function setRmbValues(jWoNo_, jState_, jErrDesc_, jCont_)\n");
			appendJavaScript("{\n");
			appendJavaScript("   jWoNo     = jWoNo_; \n");
			appendJavaScript("   jState    = jState_; \n");
			appendJavaScript("   jErrDesc  = jErrDesc_; \n");
			appendJavaScript("   jCont     = jCont_; \n");
			appendJavaScript("}\n");
		}
		catch (Exception e)
		{
		}
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "THENAV: The Navigator";
	}

	protected String getTitle()
	{
		return "THENAV: The Navigator";
	}

	protected AutoString getContents() throws FndException
	{ 
		ASPManager mgr = getASPManager();   

		AutoString out = getOutputStream();
		out.clear();
		out.append(rootOfNavigator.show()); 

		appendDirtyJavaScript("function jPrepare()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   if(jState == 'FINISHED')\n");
		appendDirtyJavaScript("   window.open(\"HistoricalSeparateRMB.page?WO_NO=\" + URLClientEncode(jWoNo), \"_blank2\", \"scrollbars=yes,status=yes,resizable=yes,alwaysRaised=yes,width=770,height=525\");\n");
		appendDirtyJavaScript("   else\n");
		appendDirtyJavaScript("      window.open(\"ActiveSeparate2.page?WO_NO=\"+URLClientEncode(jWoNo), \"_blank\", \"scrollbars=yes,status=yes,resizable=yes,alwaysRaised=yes,width=770,height=525\");\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function jReportIn()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.open(\"ActiveSeperateReportInWorkOrder.page?WO_NO=\"+URLClientEncode(jWoNo), \"blank\", \"scrollbars=yes,status=yes,resizable=yes,alwaysRaised=yes,width=770,height=525\");\n");
		appendDirtyJavaScript("}\n");  

		appendDirtyJavaScript("function jCreateWo()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("   window.open(\"CreateWoDlg.page?WO_NO=\"+URLClientEncode(jWoNo) + \"&ERR_DESC=\"+URLClientEncode(jErrDesc), + \"&CONTRACT=\"+URLClientEncode(jCont), \"blank\", \"scrollbars=yes,status=yes,resizable=yes,alwaysRaised=yes,width=770,height=525\");\n");
		appendDirtyJavaScript("}\n");

		return out;
	}
}
