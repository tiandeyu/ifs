package ifs.hzwflw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.asp.TreeList;
import ifs.fnd.asp.TreeListNode;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

public class HzDelegateSetTree extends ASPPageProvider {
   public static boolean DEBUG = Util.isDebugEnabled("ifs.genbaw.GeneralOrganizationTree");

   public HzDelegateSetTree(ASPManager mgr, String pagePath) {
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
      String target = getTargetScript("","");
      
      process_tree = new TreeList(mgr);
      
      process_tree.setLabel(rootName);
      process_tree.setImage(imgLoc.concat("Object_Root.gif"));
      process_tree.setTarget(target);
      process_tree.setTreePosition(1, 1);
      process_tree.setTreeAreaWidth(300);
   }
   
   protected String getTargetScript2(String processKey)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(processKey))
         target = "HzDelegateSetBlank.page" + "' target='mainFrame";
      else
         target = "HzDelegateSet.page?" + "PROCESS_KEY=" + mgr.URLEncode(processKey) + "' target='mainFrame";
      return target;
   }
   
   protected String getTargetScript(String processKey,String processName)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(processKey))
         target = "HzDelegateSetBlank.page" + "' target='mainFrame";
      else
         target = "HzDelegateSet.page?" + "PROCESS_NAME=" +mgr.URLEncode(processName) +"&PROCESS_KEY=" + mgr.URLEncode(processKey) + "' target='mainFrame";
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
         String process_key = set.getValue("PROCESS_KEY");
         String process_name = mgr.isEmpty(set.getValue("PROCESS_NAME")) ? "" : set.getValue("PROCESS_NAME");
         
         String expand_data = "&PROCESS_KEY=" + mgr.URLEncode(process_key);
         String target = getTargetScript(process_key,process_name);
         
//         TreeListNode item = process_tree.addNode( process_name+"("+process_key+")");
         TreeListNode item = process_tree.addNode( process_name );
         item.setTarget(target);
         item.setExpandData(expand_data);
         
         item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
         
         set.next();
      }
   }
   
   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      if("EXPAND_TREE".equals(val))
      {
         TreeList temp_node = new TreeList(mgr, "DUMMY");
         String process_tree = mgr.readValue("PROCESS_KEY");
         
         buildSubNode(temp_node, process_tree);
         String dynamicData = temp_node.getDynamicNodeString();
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   public void buildSubNode(TreeList parent_tree, String parent_org_no)
   {
    
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("process_key").
      setHidden();
      
      blk.addField("process_name").
      setHidden();
      
      blk.setView("HZ_WF_PROCESS_DEF_SRC");
      
      set = blk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return "HZDELEGATESETTREEPROCESSTREE: Process Tree";
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
