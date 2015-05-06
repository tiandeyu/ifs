package ifs.docmaw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPHTMLFormatter;
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

public class DocStructureTreeNav extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructureTreeNav");
   
   protected ASPBlock blk;
   protected ASPRowSet set;
   protected ASPHTMLFormatter fmt;
   
   protected String imgLoc;
   
   protected TreeList doc_tree;
   
   public DocStructureTreeNav(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else
         search();
   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry = trans.addEmptyQuery(blk);
      
      qry.addWhereCondition("LENGTH(FULL_PATH) = 5");
      
      qry.setOrderByClause("FULL_PATH");
      
      qry.includeMeta("ALL");
      mgr.querySubmit(trans, blk);
      if (set.countRows() == 0) 
      {
         set.clear();
      }
   }
   
   public void validate()
    {
        ASPManager mgr = getASPManager();
        String val = mgr.readValue("VALIDATE");
        if("EXPAND_TREE".equals(val))
        {
            TreeList tempNode = new TreeList(mgr, "DUMMY");
            
            String nodeId = mgr.readValue("NODE_ID");
            
            buildSubNode(tempNode, nodeId);
            String dynamicData = tempNode.getDynamicNodeString();
            
            mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
        }
        mgr.endResponse();
    }
   
   public void buildSubNode(TreeList parent_tree, String node_id)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(blk);
      
//    qry.addWhereCondition("ITEM_NO LIKE '" + item_no + ".%'");
      qry.addWhereCondition("SUBSTR(NODE_ID, 1, INSTR(NODE_ID, '.', -1, 1) - 1) = '" + node_id + "'");
      
      qry.setOrderByClause("FULL_PATH");
      qry.includeMeta("ALL");
      qry.setBufferSize(500);
      mgr.querySubmit(trans, blk);
      
      if (set.countRows() == 0) 
      {
         set.clear();
      }
      else
      {
         int countRows = set.countRows();
         String target = "";
         set.first();
           for(int i = 0; i < countRows; i++)
           {
            String nodeId = set.getValue("NODE_ID");
            String nodeCode = mgr.isEmpty(set.getValue("NODE_CODE")) ? "" : set.getValue("NODE_CODE");
            String nodeDescription = mgr.isEmpty(set.getValue("NODE_DESCRIPTION")) ? "" : set.getValue("NODE_DESCRIPTION");
            String nodeAddress = set.getValue("NODE_ADDRESS");
            String nodeType = set.getValue("HAVE_CHILDREN");
            String expand_data = "&NODE_ID=" + mgr.URLEncode(nodeId);
            target = getTargetScript(nodeAddress);
            if ("TRUE".equals(nodeType))
            {
               // NODE
               TreeListNode node = parent_tree.addNode(nodeCode + ' ' + nodeDescription);
//             item.setLinkId("ITEM_" + item_no_);
//             item.setLinkAddition("onclick=selectLink('" + "ITEM_" + item_no_ + "')");
               node.setTarget(target);
               node.setExpandData(expand_data);
               node.setImage(imgLoc.concat("schedule_folder.gif"));
            }
            else
            {
               TreeListItem node = parent_tree.addItem(nodeCode + ' ' + nodeDescription);
               node.setTarget(target);
               node.setImage(imgLoc.concat("lov.gif"));
            }
            set.next();
           }
      }
   }
   
   protected String getTargetScript(String item_address)
   {
      ASPManager mgr = getASPManager();
      String target;
      if (mgr.isEmpty(item_address))
         target = "../docmaw/DocIssueStructureQuery.page" + "' target='mainFrame";
      else
         target = "../docmaw/DocIssueStructureQuery.page?" + "NODE_ADDRESS=" + mgr.URLEncode(item_address) + "' target='mainFrame";
      return target;
   }
   
   public void createTreeRoot() 
   {
      ASPManager mgr = getASPManager();
      doc_tree = new TreeList(mgr);
      String rootName = mgr.translate("DOCMAWDOCISSUECONDITREEROOT: Doc Issue Tree");
      String target = getTargetScript("");
      doc_tree.setLabel(rootName);
      doc_tree.setImage(imgLoc.concat("Object_Root.gif"));
      doc_tree.setTarget(target);
      doc_tree.setTreePosition(1, 1);
      doc_tree.setTreeAreaWidth(300);
   }
   
   public void search()
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
   
   public void createTree() 
   {
      ASPManager mgr = getASPManager();
      
      createTreeRoot();
      
      int size = set.countRows();
      set.first();
      for (int i = 0; i < size; i++) 
      {
         String node_id = set.getValue("NODE_ID");
         String node_code = mgr.isEmpty(set.getValue("NODE_CODE")) ? "" : set.getValue("NODE_CODE");
         String node_description = mgr.isEmpty(set.getValue("NODE_DESCRIPTION")) ? "" : set.getValue("NODE_DESCRIPTION");
         String node_address = set.getValue("NODE_ADDRESS");
         String node_type = set.getValue("HAVE_CHILDREN");
         
         String expand_data = "&NODE_ID=" + mgr.URLEncode(node_id);
         String target = getTargetScript(node_address);
         
         if ("TRUE".equals(node_type))
         {
            // NODE
            TreeListNode item = doc_tree.addNode(node_code + ' ' + node_description);
//          item.setLinkId("ROOT_ITEM_" + item_no_);
//          item.setLinkAddition("onclick=selectLink('" + "ROOT_ITEM_" + item_no_ + "')");
                item.setTarget(target);
                item.setExpandData(expand_data);
                item.setImage(imgLoc.concat("schedule_folder.gif"));
         }
         else
         {
            // ITEM
            TreeListItem item = doc_tree.addItem(node_code + ' ' + node_description);
//          item.setLinkId("ROOT_ITEM_" + item_no_);
//          item.setLinkAddition("onclick=selectLink('" + "ROOT_ITEM_" + item_no_ + "')");
            item.setTarget(target);
                item.setImage(imgLoc.concat("lov.gif"));
         }
         
            set.next();
      }
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      fmt = mgr.newASPHTMLFormatter();
      
      blk = mgr.newASPBlock("MAIN");
      
      blk.addField("NODE_ID").
         setHidden();

      blk.addField("NODE_CODE").
         setHidden();
      
      blk.addField("NODE_DESCRIPTION").
         setHidden();
      
      blk.addField("NODE_ADDRESS").
         setHidden();
      
      blk.addField("FULL_PATH").
         setHidden();
      
      blk.addField("HAVE_CHILDREN").
         setFunction("DOC_STRUCTURE_TREE_API.Have_Children(:NODE_ID)").
         setHidden();
      
      blk.addField("IN_STR1").
      setFunction("''").
      setHidden();
      
      blk.setView("DOC_STRUCTURE_TREE");
      
      set = blk.getASPRowSet();
   }
   
   protected String getDescription()
   {
      return getTitle();
   }
   protected String getTitle()
   {
      return "DOCSTRUCTURETREENAVTITLE: Doc Structure Query Tree";
   }
   
   protected AutoString getContents() throws FndException 
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();
      
      if (doc_tree != null)
      {
         out.append(doc_tree.generateTreeHeader());
         
         out.append("<script language=\"JavaScript\">\n");
         
         out.append("var HIGHLIGHT = 0;\n");
         out.append("var HIGHLIGHT_COLOR = 'white';\n");
         out.append("var HIGHLIGHT_BG    = 'blue';\n");
         out.append("//Other variables\n");
         out.append("var lastClicked = null;\n");
         out.append("var lastClickedColor;\n");
         out.append("var lastClickedBgColor;\n");
         
         out.append("function getElById(idVal)\n");
         out.append("{\n");
         out.append("   if (document.getElementById != null)\n");
         out.append("      return document.getElementById(idVal);\n");
         out.append("   if (document.all != null)\n");
         out.append("      return document.all[idVal];\n");
         out.append("   return null;\n");
         out.append("}\n");
         
         out.append("function selectLink(nodeObj)\n");
         out.append("{\n");
         out.append("   if (nodeObj==null)\n");
         out.append("   {\n");
         out.append("      return;\n");
         out.append("   }\n");
         out.append("\n");
         out.append("   var clickedDOMObj = getElById(nodeObj);\n");
         out.append("   if (clickedDOMObj != null)\n");
         out.append("   {\n");
         out.append("      if (lastClicked != null)\n");
         out.append("      {\n");
         out.append("         var prevClickedDOMObj = getElById(lastClicked);\n");
         out.append("         prevClickedDOMObj.style.color = lastClickedColor;\n");
         out.append("         prevClickedDOMObj.style.backgroundColor = lastClickedBgColor;\n");
         out.append("      }\n");
         out.append("\n");
         out.append("      lastClickedColor = clickedDOMObj.style.color;\n");
         out.append("      lastClickedBgColor = clickedDOMObj.style.backgroundColor;\n");
         out.append("      clickedDOMObj.style.color = HIGHLIGHT_COLOR;\n");
         out.append("      clickedDOMObj.style.backgroundColor = HIGHLIGHT_BG;\n");
         out.append("   }\n");
         out.append("   lastClicked = nodeObj;\n");
         out.append("}\n");
         out.append("</script>\n");
         
         out.append(doc_tree.generateTreeBody());
         out.append(doc_tree.generateTreeScripts());
         out.append(doc_tree.generateHiddenFields());
         out.append(doc_tree.generateTreeFooter());
         
      }
      
      return out;
   }
}
