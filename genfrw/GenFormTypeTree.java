package ifs.genfrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class GenFormTypeTree extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.genfrw.GenFormTypeTree");
   
   private ASPBlock blk;
   private ASPRowSet set;
   
   private String imgLoc;
   
   private TreeList type_tree;
   
   public GenFormTypeTree(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
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
      
      qry.setOrderByClause("TYPE_NO");
      
      qry.includeMeta("ALL");
      qry.setBufferSize(100);
      
      mgr.querySubmit(trans, blk);
      if (set.countRows() == 0) 
         set.clear();
   }
   
   public void createTreeRoot() 
   {
      ASPManager mgr = getASPManager();
      
      String rootName = mgr.translate(getDescription());
      String target = getTargetScript("");
      
      type_tree = new TreeList(mgr);
      
      type_tree.setLabel(rootName);
      type_tree.setImage(imgLoc.concat("Object_Root.gif"));
      type_tree.setTarget(target);
      type_tree.setTreePosition(1, 1);
      type_tree.setTreeAreaWidth(300);
   }
   
   protected String getTargetScript(String type_no)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(type_no))
         target = "GenFormBlank.page" + "' target='mainFrame";
      else
         target = "GenFormSmall.page?" + "TYPE_NO=" + mgr.URLEncode(type_no) + "' target='mainFrame";
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
         String type_no = set.getValue("TYPE_NO");
         String type_name = mgr.isEmpty(set.getValue("TYPE_NAME")) ? "" : set.getValue("TYPE_NAME");
         
         String target = getTargetScript(type_no);
         
         TreeListItem item = type_tree.addItem(type_no + ' ' + type_name);
         item.setTarget(target);
         item.setImage(imgLoc.concat("navigator_url.gif"));
         
         set.next();
      }
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("TYPE_NO").
      setHidden();
      
      blk.addField("TYPE_NAME").
      setHidden();
      
      blk.setView("GEN_FORM_TYPE");
      
      set = blk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return "GENFRWGENFORMTYPETREE: General Form Type Tree";
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
      
      if (type_tree != null)
         out.append(type_tree.show());
      
      return out;
   }
}
