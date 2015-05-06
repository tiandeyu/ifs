package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.asp.TreeList;
import ifs.fnd.asp.TreeListItem;
import ifs.fnd.asp.TreeListNode;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;

import com.horizon.workflow.flowengine.pub.XMLFlow;

public class HzNodeSyncTree extends ASPPageProvider {
	   public static boolean DEBUG = Util.isDebugEnabled("ifs.hzwflw.HzNodeSyncTree");

	   public HzNodeSyncTree(ASPManager mgr, String pagePath) {
	      super(mgr, pagePath);
	   }
	   
	   private ASPBlock blk;
	   private ASPRowSet set;
	   private ASPContext ctx;
	   
	   private String imgLoc;
	   
	   private TreeList process_tree;
	   
	   public void run() throws FndException
	   {
	      ASPManager mgr = getASPManager();
	      ctx = mgr.getASPContext();
	      
	      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
	      
	      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
	         validate();
	      else
	         search();
	   }
	   
	   private void search()
	   {
	      ASPManager mgr = getASPManager();
	      try 
	      {
	         // Fetch tree data from database
	         okFind();
	         if(set.countRows() == 0)
	         {
	            createTreeRoot();
	            set.clear();
	         }
	         else
	            createTree();
	      }
	      catch (Exception e) 
	      {
	         createTreeRoot();
	         set.clear();
	      }
	   }
	   
	   public void okFind()
	   {
	      ASPManager mgr = getASPManager();
	      
	      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	      ASPQuery qry = trans.addEmptyQuery(blk);
	      qry.includeMeta("ALL");
	      qry.setBufferSize(1000);
	      
	      mgr.querySubmit(trans, blk);
	      if (set.countRows() == 0) 
	         set.clear();
	   }
	   
	   public void createTreeRoot() 
	   {
	      ASPManager mgr = getASPManager();
	      
	      String rootName = mgr.translate(getDescription());
	      String target = getTargetScript("", "", "","");
	      
	      process_tree = new TreeList(mgr);
	      
	      process_tree.setLabel(rootName);
	      process_tree.setImage(imgLoc.concat("Object_Root.gif"));
	      process_tree.setTarget(target);
	      process_tree.setTreePosition(1, 1);
	      process_tree.setTreeAreaWidth(300);
	   }
	   
	   protected String getTargetScript(String processId, String processKey, String pagePath, String nodeId)
	   {
	      ASPManager mgr = getASPManager();
	      String target;
	      if (mgr.isEmpty(processKey)){
	    	  target = "HzNodeSyncBlank.page" + "' target='mainFrame";
	      }
	      else{
	    	  target = "HzNodeSyncSet.page?" + "P_PROCESS_KEY=" +mgr.URLEncode(processKey) + 
	    	  ( Str.isEmpty(nodeId) ? "" : "&P_NODE_ID=" + mgr.URLEncode(nodeId)) + (Str.isEmpty(processId) ? "" : "&P_PROCESS_ID=" + mgr.URLEncode(processId) )
	    	  + (Str.isEmpty(pagePath) ? "" : "&P_PAGE_PATH=" + mgr.URLEncode(pagePath) )
	    	  + "' target='mainFrame";
	      }
	      return target;
	   }
	   
	   public void createTree() 
	   {
	      ASPManager mgr = getASPManager();
	      
	      createTreeRoot();
	      
	      int size = set.countRows();
	      set.first();
	      for (int i = 0; i < size; i++) 
	      {
	    	  String process_id = set.getValue("PROCESS_ID");
	         String process_key = set.getValue("PROCESS_KEY");
	         String process_name = mgr.isEmpty(set.getValue("PROCESS_NAME")) ? "" : set.getValue("PROCESS_NAME");
	         
	         String expand_data = "&GET_CONN_PAGE=TRUE&PROCESS_ID=" + mgr.URLEncode(process_id) + "&PROCESS_KEY=" + mgr.URLEncode(process_key);
	         String target = getTargetScript(process_id, process_key, null, null);
	         
	         TreeListNode item = process_tree.addNode( process_name+"("+process_key+")");
	         item.setTarget(target);
	         item.setExpandData(expand_data);
	         
	         item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
	         
	         set.next();
	      }
	   }
	   
	   public void validate() throws FndException
	   {
	      ASPManager mgr = getASPManager();
	      String val = mgr.readValue("VALIDATE");
	      if("EXPAND_TREE".equals(val))
	      {
	         TreeList temp_node = new TreeList(mgr, "DUMMY");
	         String process_id = mgr.readValue("PROCESS_ID");
	         String process_key = mgr.readValue("PROCESS_KEY");
	         String getPage = mgr.readValue("GET_CONN_PAGE");
	         String dynamicData = "";
	         if("TRUE".equals(getPage)){
	             ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	             ASPQuery q = null;
	             ASPBuffer buffer = null;
	             int count = 0;
	             StringBuilder sqlPageInfo = new StringBuilder();
	             sqlPageInfo.append(" select T.page_path PAGE_PATH,T.remark REMARK from HZ_BIZ_WF_CONFIG t WHERE T.process_key=  ?");
	             q = trans.addQuery("SQLPAGEINFO", sqlPageInfo.toString());
	             q.addParameter("PROCESS_KEY",process_key);
	             trans.addRequestHeader(getASPManager().getASPConfig().getConfigUser());
	             trans = mgr.perform(trans);
	             buffer = trans.getBuffer("SQLPAGEINFO");
	             count = buffer.countItems();
	             String pagePath = null;
	             String pageRemark;
		         String expand_data = "&GET_CONN_PAGE=FALSE&PROCESS_ID=" + mgr.URLEncode(process_id) + "&PROCESS_KEY=" + mgr.URLEncode(process_key);
	             for (int i = 0; i < count; i++)
	             {
	                if("DATA".equals(buffer.getNameAt(i)))
	                {
	                	pagePath = buffer.getBufferAt(i).getValue("PAGE_PATH");
	                	pageRemark = buffer.getBufferAt(i).getValue("REMARK");
		        		String target = getTargetScript(process_id, process_key, pagePath, null);
		        		TreeListNode node = temp_node.addNode("" + pageRemark);
		        		node.setTarget(target);
		        		
		        		node.setExpandData(expand_data + "&P_PAGE_PATH=" + mgr.URLEncode(pagePath));
		        		node.setImage(imgLoc.concat("Object_OrgUnit.gif"));
	                }
	             }
	             dynamicData = temp_node.getDynamicNodeString();
	         }else{
	        	 String nodeIds = XMLFlow.getNodeList(process_id);
	        	 System.out.println("^^^^^^^^" + nodeIds);
	        	 String pagePath = mgr.readValue("P_PAGE_PATH");
	        	 
	        	 String[] nodeIdArray = nodeIds.split("\\~");
	        	 for (int i = 0; i < nodeIdArray.length/3; i++) {
	        		 String nodeCode = nodeIdArray[3*i];
	        		 String nodeDescription =  nodeIdArray[3*i + 2];
	        		 String target = getTargetScript(null, process_key, pagePath, nodeCode);
	        		 TreeListItem node = temp_node.addItem(nodeCode + ' ' + nodeDescription);
	        		 node.setTarget(target);
	        		 node.setImage(imgLoc.concat("lov.gif"));
	        	 }
	        	 dynamicData = temp_node.getDynamicNodeString();
	         }
	         
	         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
	      }
	      mgr.endResponse();
	   }
	   
	   public void preDefine()
	   {
	      ASPManager mgr = getASPManager();
	      
	      blk = mgr.newASPBlock("MAIN");
	      
	      blk.addField("PROCESS_ID").setHidden();
	      
	      blk.addField("PROCESS_KEY").setHidden();
	      
	      blk.addField("PROCESS_NAME").setHidden();
	      
	      blk.setView("HZ_WF_PROCESS_DEF_SRC");
	      
	      set = blk.getASPRowSet();
	   }
	   
	   protected String getDescription()
	   {
	      return "HZNODESYNCTREEPROCESSTREE: Process Tree";
	   }
	   
	   protected String getTitle()
	   {
	      return getDescription();
	   }
	   
	   protected AutoString getContents() throws FndException 
	   {
	      ASPManager mgr = getASPManager();
	      AutoString out = getOutputStream();
	      out.clear();
	      
	      if (process_tree != null)
	         out.append(process_tree.show());
	      
	      return out;
	   }
	}
