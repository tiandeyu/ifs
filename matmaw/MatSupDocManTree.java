package ifs.matmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MatSupDocManTree extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.genbaw.MatSupDocManTree");
   
   private ASPBlock blk;
   private ASPRowSet set;
   private ASPContext ctx;
   
   private String imgLoc;
   
   private TreeList org_tree;
   
   
   public MatSupDocManTree(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
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
      
      qry.addWhereCondition("FATHER_NO IS NULL");
      
      qry.setOrderByClause("MAT_SUP_DOC_CAT_NO");
      
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
      String target = getTargetScript("");
      
      org_tree = new TreeList(mgr);
      
      org_tree.setLabel(rootName);
      org_tree.setImage(imgLoc.concat("Object_Root.gif"));
      org_tree.setTarget(target);
      org_tree.setTreePosition(1, 1);
      org_tree.setTreeAreaWidth(300);
   }
   
   protected String getTargetScript(String org_no)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(org_no))
         target = "MatSupDocMan.page" + "' target='mainFrame";
      else
         target = "MatSupDocMan.page?" + "MAT_SUP_DOC_CAT_NO=" + mgr.URLEncode(org_no) + "' target='mainFrame";
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
         String MAT_SUP_DOC_CAT_NO = set.getValue("MAT_SUP_DOC_CAT_NO");
         String MAT_SUP_DOC_CAT_NAME = mgr.isEmpty(set.getValue("MAT_SUP_DOC_CAT_NAME")) ? "" : set.getValue("MAT_SUP_DOC_CAT_NAME");
         
         String expand_data = "&MAT_SUP_DOC_CAT_NO=" + mgr.URLEncode(MAT_SUP_DOC_CAT_NO);
         String target = getTargetScript(MAT_SUP_DOC_CAT_NO);
         
         TreeListNode item = org_tree.addNode(MAT_SUP_DOC_CAT_NAME);
         item.setTarget(target);
         item.setExpandData(expand_data);
         
//         if ("ORG".equals(org_type))
//         {
            // Organization
            item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
//         }
//         else
//         {
//            // Department
//            item.setImage(imgLoc.concat("Object_Position.gif"));
//         }
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
         String MAT_SUP_DOC_CAT_NAME = mgr.readValue("MAT_SUP_DOC_CAT_NO");
         
         buildSubNode(temp_node, MAT_SUP_DOC_CAT_NAME);
         String dynamicData = temp_node.getDynamicNodeString();
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   public void buildSubNode(TreeList parent_tree, String parent_org_no)
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(blk);
      
      qry.addWhereCondition("FATHER_NO = '" + parent_org_no + "'");
      
      qry.setOrderByClause("MAT_SUP_DOC_CAT_NO");
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
      mgr.querySubmit(trans, blk);
      
      if (set.countRows() == 0) 
      {
         set.clear();
      }
      else
      {
         int countRows = set.countRows();
         set.first();
         for(int i = 0; i < countRows; i++)
         {
            String org_no = set.getValue("MAT_SUP_DOC_CAT_NO");
            String org_desc = mgr.isEmpty(set.getValue("MAT_SUP_DOC_CAT_NAME")) ? "" : set.getValue("MAT_SUP_DOC_CAT_NAME");
            
            String expand_data = "&MAT_SUP_DOC_CAT_NO=" + mgr.URLEncode(org_no);
            String target = getTargetScript(org_no);
            
            TreeListNode item = parent_tree.addNode(org_desc);
            item.setTarget(target);
            item.setExpandData(expand_data);
            item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
            set.next();
         }
      }
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("MAT_SUP_DOC_CAT_NO").
      setHidden();
      
      blk.addField("MAT_SUP_DOC_CAT_NAME").
      setHidden();
      
      blk.addField("FATHER_NO").
      setHidden();
      
      blk.addField("CAT_MSG").
      setHidden();
      
      blk.addField("CREATE_TIME","Date").
      setHidden();
      
      blk.setView("MAT_SUP_DOC_MAN");
      
      set = blk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return "MATSUPDOCMANTREE: MatSupDocMan Tree";
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
      
      if (org_tree != null)
         out.append(org_tree.show());
      
      return out;
   }
}
