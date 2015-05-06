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
*  File        : PmObjStructNavigator.java 
*  Created     : NIJALK  041214 (AMEC112 - Job Program)
*  Modified    :
*  050117   NIJALK   Changed reference of images to pcmw from equipw.
*  051110   NIJALK   Modified run().
*  060815   AMNILK   Bug 58216, Eliminated SQL Injection security vulnerability.
*  060904   AMNILK   Merged Bug Id: 58216. 
*  070713   AMNILK   Eliminated SQL injections.
*  080110   CHANLK   Bug 68772, Eliminated SQL injections.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class PmObjStructNavigator extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================

	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmObjStructNavigator");

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
	private String actTab;
	private ASPTransactionBuffer trans;
	private String callingUrl;
	private String whereStr1;

	private TreeListNode root_node;
	private TreeListItem item_node;
	private TreeList rootOfNavigator;

        String show = "";

	//===============================================================
	// Construction 
	//===============================================================

	public PmObjStructNavigator(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();
                actTab = "";

		trans = mgr.newASPTransactionBuffer();
		ctx = mgr.getASPContext();
		callingUrl = ctx.findGlobal("CALLING_FORM","");
                show = ctx.readValue("SHOW",show);

		if (callingUrl.indexOf("PMOBJSTRUCTMAIN") > 0)
                        urlLink = "PmObjStructPmAction.page";

		if ( mgr.commandBarActivated() )
                    eval(mgr.commandBarFunction());
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
                    validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")))
                {
                    show = "1";
                    createTree();
                }
                else if (mgr.dataTransfered())
                {
                    show = "1";
                    createTree();
                }

                ctx.writeValue("SHOW",show);
	}

	public void createTree()
	{
		ASPManager mgr = getASPManager();

		rootOfNavigator = new TreeList(mgr);
		rootOfNavigator.setLabel(" ");    
		rootOfNavigator.setImage("../"+ mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + mgr.getConfigParameter("NAVIGATOR/" + mgr.getConfigParameter("NAVIGATOR/STYLE") + "/IMAGE/ROOT"));

		if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT"))) {
			actTab = mgr.readValue("ACTTAB");

			if ("1".equals(actTab))
				urlLink = "PmObjStructPmAction.page";
			else if ("2".equals(actTab))
				urlLink = "PmObjStructMaintPlan.page";

			// Bug 68772, Start
			// whereStr = "SUP_MCH_CODE='" + mgr.readValue("MCH_CODE") + "' AND
			// SUP_CONTRACT='" + mgr.readValue("CONTRACT") + "'";
			// ctx.setGlobal("HERESTR", whereStr);
			ctx.setGlobal("HERESTRP1", mgr.readValue("MCH_CODE"));
			ctx.setGlobal("HERESTRP2", mgr.readValue("CONTRACT"));
			// Bug 68772, End
			ctx.setGlobal("ACTTAB", actTab);

			addInitialNodes();
		}

		rootOfNavigator.setBaseTarget("__pmPart");
		rootOfNavigator.setTreePosition(10,2);
		rootOfNavigator.setTreeAreaWidth(500);

		rootOfNavigator.setFolderOpenedImage("../" + mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + "/navigator_folder_opened.gif");
		rootOfNavigator.setFolderClosedImage("../" + mgr.getConfigParameter("APPLICATION/LOCATION/IMAGES_REL") + "/navigator_folder.gif");
	}

	public void validate() 
	{
		ASPManager mgr = getASPManager(); 

		String val = mgr.readValue("VALIDATE"); 

		if ("EXPAND_TREE".equals(val))
			searchItemNodes();

		mgr.endResponse(); 
	}


	public void addInitialNodes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPQuery q = trans.addEmptyQuery(yesnoblk);
		q.includeMeta("ALL");

                //Bug 58216 Start
		q = trans.addQuery("HASSTRUCT","SELECT HAS_STRUCTURE FROM MAINTENANCE_OBJECT WHERE MCH_CODE = ? AND CONTRACT = ?");
		q.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));
		q.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
		//Bug 58216 End

		trans = mgr.submit(trans);  

		String sTargetForLink = "";
		String sNodeDescription = "";  

		sTargetForLink = urlLink+"?MCH_CODE=" + mgr.URLEncode(mgr.readValue("MCH_CODE"))+"&CONTRACT="+mgr.URLEncode(mgr.readValue("CONTRACT"));
		sNodeDescription = mgr.translate("PCMWPMOBJSTRUCTNAVIGATORNODESC: &1", (mgr.isEmpty(mgr.readValue("MCH_CODE"))?"":mgr.readValue("MCH_CODE"))+" "+
                                                                                        (mgr.isEmpty(mgr.readValue("MCH_NAME"))?"":mgr.readValue("MCH_NAME")));
		if ("Yes".equals(trans.getValue("HASSTRUCT/DATA/HAS_STRUCTURE"))) 
		{
			root_node = rootOfNavigator.addNode(sNodeDescription, 
															sTargetForLink, "../pcmw/images/navigator_folder.gif",
															"&MCH_CODE=" + mgr.URLEncode(mgr.readValue("MCH_CODE")) + "&CONTRACT=" + mgr.URLEncode(mgr.readValue("CONTRACT")) + "");

		}
		else 
		{
			item_node = rootOfNavigator.addItem(sNodeDescription, 
															sTargetForLink,
															"../pcmw/images/webwindow.gif");
		}
	}

	public void searchItemNodes()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		navset.clear();
		int size;

//		Bug 68772, Start
//		whereStr1 = ctx.findGlobal("HERESTR", "");
//		ctx.setGlobal("HERESTR", "none");
		String mchCode = ctx.getGlobal("HERESTRP1");
		ctx.setGlobal("HERESTRP1", "none");
//		Bug 68772, End

		actTab = ctx.findGlobal("ACTTAB", "");

		ASPQuery q = trans.addEmptyQuery(navblk);

		if ("none".equals(mchCode)) {
			// Bug 58216 Start
			q.addWhereCondition("SUP_MCH_CODE = ?");
			q.addWhereCondition("SUP_CONTRACT = ?");
			q.addParameter("MCH_CODE", mgr.readValue("MCH_CODE"));
			q.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
			// Bug 58216 End
		}
		else {
//			Bug 68772, Start
			q.addWhereCondition("SUP_MCH_CODE = ? AND SUP_CONTRACT = ? ");
			q.addParameter("MCH_CODE", mchCode);
			q.addParameter("CONTRACT", ctx.getGlobal("HERESTRP2"));
//			Bug 68772, Start
			// SQLInjection_Safe AMNILK 20070713
//			q.addWhereCondition(whereStr1);
		}

		q.includeMeta("ALL");

		mgr.submit(trans);
		size = navset.countRows();
		navset.first();

		String sTargetForLink = "";
		String sNodeDescription = "";
		String item_data = "";

		if ("1".equals(actTab))
			urlLink = "PmObjStructPmAction.page";
		else if ("2".equals(actTab))
			urlLink = "PmObjStructMaintPlan.page";

		if (size > 0)
		{
			TreeList dummynode = new TreeList(mgr,"DUMMY");

			for (int i = 0; i < size; i++)
			{
				sTargetForLink = urlLink+"?MCH_CODE=" + mgr.URLEncode(navset.getValue("MCH_CODE"))+
                                                           "&CONTRACT=" + mgr.URLEncode(navset.getValue("CONTRACT")) ;
				sNodeDescription = mgr.translate("PCMWPMOBJSTRUCTNAVIGATORNODESC: &1", (mgr.isEmpty(navset.getValue("MCH_CODE"))?"":navset.getValue("MCH_CODE"))+" "+
                                                                                                   (mgr.isEmpty(navset.getValue("MCH_NAME"))?"":navset.getValue("MCH_NAME")));

				if ("Yes".equals(navset.getValue("HAS_STRUCTURE")))
				{
					root_node = dummynode.addNode(sNodeDescription,
												  sTargetForLink,
												  "../pcmw/images/navigator_folder.gif", 
                                                                                                   "&MCH_CODE=" + mgr.URLEncode(navset.getValue("MCH_CODE"))+"&CONTRACT="+mgr.URLEncode(navset.getValue("CONTRACT"))+"");
				}
				else
				{
					item_node = dummynode.addItem(sNodeDescription,
												  sTargetForLink,
												  "../pcmw/images/webwindow.gif");

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

		f = navblk.addField("MCH_CODE");

		f = navblk.addField("CONTRACT");

		f = navblk.addField("MCH_NAME");

                f = navblk.addField("HAS_STRUCTURE");

		f = navblk.addField("SUP_CONTRACT");

		f = navblk.addField("SUP_MCH_CODE");

		navblk.setView("MAINTENANCE_OBJECT");
		navset = navblk.getASPRowSet();
	}

//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
            ASPManager mgr = getASPManager();
            return (mgr.translate("PCMWTHENAVTITILE: The Navigator"));
	}

	protected String getTitle()
	{
            ASPManager mgr = getASPManager();
            return (mgr.translate("PCMWTHENAVTITILE: The Navigator"));
	}
	
        protected AutoString getContents() throws FndException
	{ 
		ASPManager mgr = getASPManager();

		AutoString out = getOutputStream();
		out.clear();
                if ("1".equals(show))
                {
                    out.append(rootOfNavigator.show());
                }
		return out;
	}
}
