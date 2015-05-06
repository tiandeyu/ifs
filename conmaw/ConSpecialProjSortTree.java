package ifs.conmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConSpecialProjSortTree extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.ConSpecialProjSortTree");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private TreeList sort_tree;
    
   private String imgLoc;
   private boolean isBuildTree = false;
   private String initUrl;
   
   
   //===============================================================
   // Construction 
   //===============================================================
   public ConSpecialProjSortTree(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();   
   
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
//      if (!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
//      {
//         search();   
//      }
//      else
         search();
          
   }
   
   private void search()
   {
      ASPManager mgr = getASPManager();
      try 
      {
         // Fetch tree data from database
         okFind();
         if(headset.countRows() == 0)
         {
            createTreeRoot();
            headset.clear();
         }
         else
            createTree();
      }
      catch (Exception e) 
      {
         createTreeRoot();
         headset.clear();
      }
   }
   
   public void createTreeRoot() 
   {
ASPManager mgr = getASPManager();
      
      String rootName = mgr.translate(getDescription());
      String target = getTargetScript("","");
      
      sort_tree = new TreeList(mgr);
      
      sort_tree.setLabel(rootName);
      sort_tree.setImage(imgLoc.concat("Object_Root.gif"));
      sort_tree.setTarget(target);
      sort_tree.setTreePosition(1, 1);
      sort_tree.setTreeAreaWidth(300);
   }
   
   protected String getTargetScript(String proj_no,String id)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(id))
         target = "ConSpecialProjSortBlank.page" + "' target='ChildMain";
      else
         target = "ConSpecialProjSortLine.page?" + "&PROJ_NO="+mgr.URLEncode(proj_no) + "&ID=" + mgr.URLEncode(id) + "' target='ChildMain";
      return target;
   }
   
   public void createTree() 
   {
      ASPManager mgr = getASPManager();
      
      createTreeRoot();
      
      int size = headset.countRows();
      headset.first();
      for (int i = 0; i < size; i++) 
      {
         String proj_no = headset.getValue("PROJ_NO");
         String id = headset.getValue("ID");
         String sort_no = headset.getValue("SORT_NO");
         String sort_name = headset.getValue("SORT_NAME");
         
         String expand_data = "&ID=" + mgr.URLEncode(id);
         String target = getTargetScript(proj_no,id);
         
         TreeListNode item = sort_tree.addNode(sort_name);
         item.setTarget(target);
         item.setExpandData(expand_data);
         
         item.setImage(imgLoc.concat("navigator_image.gif"));
         
         headset.next();
      }
   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry = trans.addEmptyQuery(headblk);
      qry.addWhereCondition("PROJ_NO = '"+ mgr.getQueryStringValue("PROJ_NO") +"'");
      
//      qry.addWhereCondition("PARENT_ID IS NULL");
      
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
      
      mgr.querySubmit(trans, headblk);
      if (headset.countRows() == 0) 
         headset.clear();
   }
   
   
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      headblk = mgr.newASPBlock("MAIN");
      
      headblk.addField("ID").
      setHidden();
      
      headblk.addField("PROJ_NO").
      setHidden();
      
      headblk.addField("SORT_NO").
      setHidden();
      
      headblk.addField("SORT_NAME").
      setHidden();
      
      
      headblk.setView("CON_SPECIAL_PROJ_SORT");
      
      headset = headblk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return "CONMAWCONSPECIALPROJSORTTREE: Con Special Proj Sort Tree";
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
//      if (isBuildTree)
//      {
//         out.append(sort_tree.show());
//         if (mgr.isEmpty(mgr.readValue("RELOAD")) || mgr.readValue("RELOAD").equals("TRUE"))
//         {
//            appendDirtyJavaScript("populateRightFrame('"+initUrl+"'); \n");
//         }
//         isBuildTree = false;
//      }   
//
//      if (mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
//      {
//         appendDirtyJavaScript("populateRightFrame('empty.htm'); \n");
//      }
//
//      appendDirtyJavaScript("function populateRightFrame(url) \n");
//      appendDirtyJavaScript("{ \n");
//      appendDirtyJavaScript("   window.parent.frames.ChildMain.location.href=url; \n");
//      appendDirtyJavaScript("} \n");  
      
      if (sort_tree != null)
         out.append(sort_tree.show());
      return out; 
   }
}
