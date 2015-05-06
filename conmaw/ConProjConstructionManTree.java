package ifs.conmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ConProjConstructionManTree extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConProjConstructionManTree");
   
   private ASPBlock blk;
   private ASPRowSet set;
   private ASPContext ctx;
   
   private String imgLoc;
   
   private TreeList proj_con_tree;
   
   
   public ConProjConstructionManTree(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext(); 
      String PROJ_NO =  mgr.getQueryStringValue("PROJ_NO");
      String UNIT =  mgr.getQueryStringValue("UNIT");
      String CONTRACT_NO = mgr.getQueryStringValue("CONTRACT_NO");
      String CONSTRUCT_ORG = mgr.getQueryStringValue("CONSTRUCT_ORG");
      if(!(mgr.isEmpty(PROJ_NO)||mgr.isEmpty(UNIT)||mgr.isEmpty(CONTRACT_NO)||mgr.isEmpty(CONSTRUCT_ORG))){
      ctx.setGlobal("PROJ_NO", PROJ_NO);
      ctx.setGlobal("UNIT", UNIT);
      ctx.setGlobal("CONTRACT_NO", CONTRACT_NO);
      ctx.setGlobal("CONSTRUCT_ORG", CONSTRUCT_ORG);
      }
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
      {
         search();   
      }
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
      
//      qry.addWhereCondition("PROJ_NO = '"+ mgr.getQueryStringValue("PROJ_NO") +"'");
//      qry.addWhereCondition("PARENT_ID IS NULL");
      qry.addWhereCondition("PROJ_NO = ? AND UNIT = ? AND CONTRACT_NO = ? AND CONSTRUCT_ORG = ? ");
      qry.addParameter("PROJ_NO",ctx.findGlobal("PROJ_NO")); 
      qry.addParameter("UNIT",ctx.findGlobal("UNIT")); 
      qry.addParameter("CONTRACT_NO",ctx.findGlobal("CONTRACT_NO")); 
      qry.addParameter("CONSTRUCT_ORG",ctx.findGlobal("CONSTRUCT_ORG")); 
      qry.addWhereCondition("PARENT_ID IS NULL");
      qry.setOrderByClause("NODE_NO");
      
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
      
      mgr.querySubmit(trans, blk);
      if (set.countRows() == 0) 
         set.clear();
   }
   
   public void createTreeRoot() 
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans=mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String UNIT=ctx.findGlobal("UNIT");
      cmd = trans.addCustomFunction("PROJ_NO","GENERAL_MACH_GROUP_API.Get_Mach_Grp_Desc","PROJ_NO");
      cmd.addParameter("UNIT",UNIT);

      trans = mgr.validate(trans);
      String rootName= trans.getValue("PROJ_NO/DATA/PROJ_NO");
      String target = getTargetScript(ctx.findGlobal("PROJ_NO"),"","");
      
      proj_con_tree = new TreeList(mgr);
      
      proj_con_tree.setLabel(rootName);
      proj_con_tree.setImage(imgLoc.concat("Object_Root.gif"));
      proj_con_tree.setTarget("");
      proj_con_tree.setTreePosition(1, 1);
      proj_con_tree.setTreeAreaWidth(300);
   }
   
   protected String getTargetScript(String proj_no,String node_no,String class_no)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(node_no))
//         target = "ConProjConstructionMan.page?PROJ_NO="+"42098412003" + "&NODE_NO=" + "6" + "&NODE_NAME=" + "6ป๚ื้"+ "&CLASS_NO=" + mgr.URLEncode(class_no) + "' target='ChildMain";
         target = "ConProjConstructionMan.page?PROJ_NO="+mgr.URLEncode(proj_no)+ "' target='ChildMain";
      else
         target = "ConProjConstructionMan.page?PROJ_NO="+mgr.URLEncode(proj_no) + "&PLAN_NO=" + mgr.URLEncode(node_no) + "&SUB_PROJ_NO=" + mgr.URLEncode(class_no) + "' target='ChildMain";
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
         String proj_no = set.getValue("PROJ_NO");
         String node_no = set.getValue("NODE_NO");
         String node_name = mgr.isEmpty(set.getValue("NODE_NAME")) ? "" : set.getValue("NODE_NAME");
         String class_no = mgr.isEmpty(set.getValue("CLASS_NO")) ? "" : set.getValue("CLASS_NO");
         String standard_pre = set.getValue("PLAN_NO");
         String expand_data = "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&NODE_NO=" + mgr.URLEncode(node_no) + "&PLAN_NO=" + mgr.URLEncode(standard_pre);
         String target = getTargetScript(proj_no,standard_pre,node_no);
         
         TreeListNode item = proj_con_tree.addNode(node_no+node_name);
         item.setTarget(target);
         item.setExpandData(expand_data);
         
         item.setImage(imgLoc.concat("Object_Position.gif"));
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
         String proj_no = mgr.readValue("PROJ_NO");
         String node_no = mgr.readValue("NODE_NO");
         String standard_pre = mgr.readValue("PLAN_NO");
         buildSubNode(temp_node, proj_no ,node_no,standard_pre);
         String dynamicData = temp_node.getDynamicNodeString();
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   public void buildSubNode(TreeList parent_tree,String proj_no, String parent_id, String standard_pre)
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(blk);
//      qry.addWhereCondition("PROJ_NO = ? AND UNIT = ? AND CONTRACT_NO = ? AND CONSTRUCT_ORG = ? ");
//      qry.addParameter("PROJ_NO",ctx.findGlobal("ID")); 
//      qry.addParameter("UNIT",ctx.findGlobal("UNIT")); 
//      qry.addParameter("CONTRACT_NO",ctx.findGlobal("CONTRACT_NO")); 
//      qry.addParameter("CONSTRUCT_ORG",ctx.findGlobal("CONSTRUCT_ORG")); 
      
      qry.addWhereCondition("PARENT_ID = '" + parent_id + "'");
      qry.addWhereCondition("PROJ_NO = '" + proj_no + "'");
      qry.addWhereCondition("PLAN_NO = '" + standard_pre + "'");
      qry.setOrderByClause("NODE_NO");
      
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
            String node_no = set.getValue("NODE_NO");
            String node_name = mgr.isEmpty(set.getValue("NODE_NAME")) ? "" : set.getValue("NODE_NAME");
            String class_no = mgr.isEmpty(set.getValue("CLASS_NO")) ? "" : set.getValue("CLASS_NO");
            
//            String expand_data = "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&NODE_NO=" + mgr.URLEncode(node_no) ;
            String expand_data = "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&NODE_NO=" + mgr.URLEncode(node_no) + "&PLAN_NO=" + mgr.URLEncode(standard_pre);
            String target = getTargetScript(proj_no,standard_pre,node_no);
            
            TreeListNode item = parent_tree.addNode(node_no+node_name);
            item.setTarget(target);
            item.setExpandData(expand_data);
            
            item.setImage(imgLoc.concat("Object_Position.gif"));
            set.next();
         }
      }
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      blk = mgr.newASPBlock("MAIN");
      blk.addField("PROJ_NO").
      setHidden();
      blk.addField("NODE_NO").
      setHidden();
      blk.addField("NODE_NAME").
      setHidden();
      blk.addField("PARENT_ID").
      setHidden();
      blk.addField("CONSTRUCT_ORG").
      setHidden();
      blk.addField("CONTRACT_NO").
      setHidden();
      blk.addField("UNIT").
      setHidden();
      blk.addField("PLAN_NO").
      setHidden();
//      blk.addField("IS_LEAF").
//      setHidden();
//      blk.addField("NODE_LEVEL").
//      setHidden();
      blk.addField("STANDARD_PRE").
      setHidden();
      blk.addField("CLASS_NO").
      setHidden();
      
      blk.setView("CON_QUA_TREE3");
      
      set = blk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return "CONMAWCONPROJCONSTRUCTIONMANTREE: Proj Construction Man Tree";
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
      
      if (proj_con_tree != null)
         out.append(proj_con_tree.show());
      
      return out;
   }
}
