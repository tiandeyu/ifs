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
*  File        : WorkOrderCostNavigator.java 
*  Created     : ARWILK  010307
*  Modified    :
*  CHAMLK  031020  Modified functions search1() and search2().
*  BUNILK  040113  Used TreeList class to generate navigator instead previous listForWo.js and listForWoSM.js.
* --------------------------------- Edge - SP1 Merge -------------------------
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  THWILK  040324  Merge with SP1.
*  AMDILK  060814  Bug 58216, Eliminated SQL errors in web applications. Modified method searchItemNodes()
*  AMDILK  060904  Merged with the Bug Id 58216
*  AMNILK  070713  Eliminated SQL Injections.
*  SHAFLK  071029  Bug 68026, Modified searchItemNodes(), addInitialNodes() and preDefine().
*  CHANLK  080110  Bug 68772, Eliminated SQL injections.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderCostNavigator extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderCostNavigator");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================

	private ASPContext ctx;
	private ASPBlock yesnoblk;
	private ASPRowSet yesset;
	private ASPBlock navblk;
	private ASPRowSet navset;
	private ASPField f;

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================

	private String whereStr;
	private String urlLink;
	private String formNo;
	private int sDataFlag;
	private ASPTransactionBuffer trans;
	private String callingUrl;
	private String whereStr1;
	private String root_nodes;
	private String item_nodes;
	private String sConnHas;

	private TreeListNode root_node;
	private TreeListItem item_node;
	private TreeList rootOfNavigator;

	//===============================================================
	// Construction 
	//===============================================================

	public WorkOrderCostNavigator(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		sDataFlag = ctx.readNumber("DTFLAG",0);
		callingUrl = ctx.getGlobal("CALLING_FORM"); 

		if (callingUrl.indexOf("WorkOrderCostAnalysisSM") > 0)
			urlLink = "WorkOrderCostTableSM.page";
		else if (callingUrl.indexOf("FrameSetWorkOrderCost") > 0)
			urlLink = "WorkOrderCostTable.page";

		if ( mgr.commandBarActivated() )
			eval(mgr.commandBarFunction());
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else
			createTree();
	}

	public void createTree()
	{
		ASPManager mgr = getASPManager();

		rootOfNavigator = new TreeList(mgr);
		rootOfNavigator.setLabel(mgr.translate("PCMWWORKORDERCOSTNAVIGATORPCMWWOSTR: Work Order Structure")); 
		rootOfNavigator.setImage("../"+ mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + mgr.getConfigParameter("NAVIGATOR/" + mgr.getConfigParameter("NAVIGATOR/STYLE") + "/IMAGE/ROOT"));

		if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECTED_WO_NO")))
		{
			formNo = mgr.readValue("FORM");

			if ("1".equals(formNo))
				urlLink = "WorkOrderCostTableSM.page";
			else
				urlLink	= "WorkOrderCostTable.page"; 

			// Bug 68772, Start
//			whereStr = "WO_NO="+mgr.readValue("CONNECTED_WO_NO");
//			ctx.setGlobal("HERESTR",whereStr);
			ctx.setGlobal("HERESTR",mgr.readValue("CONNECTED_WO_NO"));
			// Bug 68772, End

			addInitialNodes();
		}

		rootOfNavigator.setBaseTarget("__costPart");
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

		ASPQuery q = trans.addEmptyQuery(yesnoblk);
		q.includeMeta("ALL");


		ASPCommand cmd = trans.addCustomFunction("CBHASCO","WORK_ORDER_CONNECTION_API.HAS_CONNECTION_DOWN","CBHASCONNECTIONS");
		cmd.addParameter("CONNECTED_WO_NO",mgr.readValue("CONNECTED_WO_NO"));

                //Bug 68026, start
                cmd = trans.addCustomFunction("GETDIR","Work_Order_API.Get_Err_Descr","ERR_DESC");
                cmd.addParameter("CONNECTED_WO_NO",mgr.readValue("CONNECTED_WO_NO"));

                trans = mgr.submit(trans);  

                sConnHas = trans.getValue("CBHASCO/DATA/CBHASCONNECTIONS");         
                String sError = trans.getValue("GETDIR/DATA/ERR_DESC");
                //Bug 68026, end

		String sTargetForLink = "";
		String sNodeDescription = "";

		sTargetForLink = urlLink+"?CONNECTED_WO_NO=" + mgr.URLEncode(mgr.readValue("CONNECTED_WO_NO"));
		//Bug 68026, start
                sNodeDescription = mgr.translate("PCMWWORKORDERANANAVIGATORNODESC: &1 - &2", (mgr.isEmpty(mgr.readValue("CONNECTED_WO_NO"))?"":mgr.readValue("CONNECTED_WO_NO")),(mgr.isEmpty(sError)?"":sError));
		//Bug 68026, end

		if ("true".equals(sConnHas.toString().toLowerCase()))
		{
                        //Bug 68026, start
			root_node = rootOfNavigator.addNode(sNodeDescription,
															sTargetForLink,
															"../equipw/images/navigator_folder.gif",  
															"&CONNECTED_WO_NO=" + mgr.URLEncode(mgr.readValue("CONNECTED_WO_NO")) + "&ERR_DESC=" + mgr.URLEncode(sError) + "");
                        //Bug 68026, end

		}
		else
		{
			item_node = rootOfNavigator.addItem(sNodeDescription,
															sTargetForLink,
															"../equipw/images/webwindow.gif");
		}

	}

	public void searchItemNodes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		navset.clear();
		int size;

		String woNo = ctx.getGlobal("HERESTR");
		ctx.setGlobal("HERESTR","none");

		ASPQuery q = trans.addEmptyQuery(navblk);

		if ("none".equals(woNo))
		{       
		        q.addWhereCondition("WO_NO = ?");
		        q.addParameter("WO_NO", mgr.readValue("CONNECTED_WO_NO"));
		}
		else
		{
			// Bug 68772, Start
			q.addWhereCondition("WO_NO = ?");
			q.addParameter("WO_NO", woNo);
			// Bug 68772, End
			// SQLInjection_Safe AMNILK 20070713
			// q.addWhereCondition(whereStr1);
		}

		q.includeMeta("ALL");

		mgr.submit(trans);
		size = navset.countRows();
		navset.first();

		String sTargetForLink = "";
		String sNodeDescription = "";
		String item_data = "";

		if (size > 0)
		{
			TreeList dummynode = new TreeList(mgr,"DUMMY");

			for (int i = 0; i < size; i++)
			{
				sTargetForLink = urlLink+"?CONNECTED_WO_NO=" + mgr.URLEncode(navset.getValue("CONNECTED_WO_NO"));
                                //Bug 68026, start
                                sNodeDescription = mgr.translate("PCMWWORKORDERANANAVIGATORNODESC: &1 - &2", (mgr.isEmpty(navset.getValue("CONNECTED_WO_NO"))?"":navset.getValue("CONNECTED_WO_NO")),(mgr.isEmpty(navset.getValue("ERR_DESC"))?"":navset.getValue("ERR_DESC")));
                                //Bug 68026, end

				if ("true".equals(navset.getValue("CBHASCONNECTIONS").toString().toLowerCase()))
				{
                                        //Bug 68026, start
					root_node = dummynode.addNode(sNodeDescription,
															sTargetForLink,
															"../equipw/images/navigator_folder.gif", 
															"&CONNECTED_WO_NO=" + mgr.URLEncode(navset.getValue("CONNECTED_WO_NO")) + "&ERR_DESC=" + mgr.URLEncode(navset.getValue("ERR_DESC")) + "");                                             
                                        //Bug 68026, end
				}
				else
				{
					item_node = dummynode.addItem(sNodeDescription,
															sTargetForLink,
															"../equipw/images/webwindow.gif");

				}
				navset.next();
			}
			item_data = dummynode.getDynamicNodeString();
		}

		mgr.responseWrite(item_data + "^"); 
	}

	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		yesnoblk = mgr.newASPBlock("YESNO");

		f = yesnoblk.addField("NO");
		f.setFunction("Translate_Boolean_API.Get_Client_Value(0)");
		f.setHidden();

		f = yesnoblk.addField("YES");
		f.setFunction("Translate_Boolean_API.Get_Client_Value(1)");
		f.setHidden(); 

		yesnoblk.setView("Dual");
		yesset = yesnoblk.getASPRowSet();

		navblk = mgr.newASPBlock("NAVI");

		f = navblk.addField("WO_NO");

		f = navblk.addField("CONNECTED_WO_NO");

		f = navblk.addField("CBHASCONNECTIONS");     
		f.setFunction("substr(WORK_ORDER_CONNECTION_API.HAS_CONNECTION_DOWN(CONNECTED_WO_NO),1,5)");   
                
                //Bug 68026, start
                f = navblk.addField("ERR_DESC");     
                f.setFunction("substr(Work_Order_API.Get_Err_Descr(CONNECTED_WO_NO),1,60)"); 
                //Bug 68026, end

		navblk.setView("WORK_ORDER_CONNECTION");
		navset = navblk.getASPRowSet();
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
		return out;
	}
}
