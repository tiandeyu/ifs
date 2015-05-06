package ifs.schmaw;

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
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

public class SchEpsTree extends ASPPageProvider
{
  public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchEpsTree");
  private ASPBlock blk;
  private ASPRowSet set;
  private ASPBlock blk2;
  private ASPRowSet set2;
  private ASPContext ctx;
  private String imgLoc;
  private TreeList org_tree;

  public SchEpsTree(ASPManager mgr, String page_path)
  {
    super(mgr, page_path);
  }

  public void run() throws FndException
  {
    ASPManager mgr = getASPManager();
    ctx = mgr.getASPContext();

    imgLoc = (mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/");

    if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
      validate();
    else
      search();
  }

  private void search()
  {
    ASPManager mgr = getASPManager();
    try
    {
      okFind2();
      if (set.countRows() == 0)
      {
        createTreeRoot();
        set.clear();
      }
      else {
        createTree();
      }
    }
    catch (Exception e) {
      createTreeRoot();
      set.clear();
    }
  }

//  public void okFind()
//  {
//    ASPManager mgr = getASPManager();
//
//    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//    ASPQuery qry = trans.addEmptyQuery(blk2);
//
////    qry.addWhereCondition("PARENT_ID IS NULL");
//
////    qry.setOrderByClause("ID");
//
//    qry.includeMeta("ALL");
//    qry.setBufferSize(1000);
//
//    mgr.querySubmit(trans, blk2);
//    if (set2.countRows() == 0)
//      set2.clear();
//  }
  
  public void okFind2()
  {
     ASPManager mgr = getASPManager();

     ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

     ASPQuery qry = trans.addEmptyQuery(blk);

     if(mgr.isEmpty(mgr.readValue("ID")))
        qry.addWhereCondition("PROJ_NO = '" +  mgr.readValue("PROJ_NO") + "'"+"AND PARENT_ID IS NULL");
     else {
        qry.addWhereCondition("PROJ_NO= ? AND PARENT_ID = ? ");
        qry.addParameter("PROJ_NO",  mgr.readValue("PROJ_NO"));
        qry.addParameter("PARENT_ID", mgr.readValue("ID"));
//        qry.addParameter("REV", rev);
     }

     qry.setOrderByClause("ID,REV");
     qry.includeMeta("ALL");
     qry.setBufferSize(1000);
     mgr.querySubmit(trans, blk);

  }

  public void createTreeRoot()
  {
    ASPManager mgr = getASPManager();

    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

    ASPQuery qry = trans.addEmptyQuery(blk2);
    qry.addWhereCondition("PROJ_NO = '"+mgr.readValue("PROJ_NO")+"'");    
    qry.includeMeta("ALL");
    qry.setBufferSize(1000);
    mgr.querySubmit(trans, blk2);
    int size = set2.countRows();
    set2.first();
    org_tree = new TreeList(mgr);
    String rootName = mgr.translate(set2.getValue("PROJ_DESC"));
    String target = getTargetScript(mgr.readValue("PROJ_NO"),"","");
    org_tree.setLabel(rootName);
    org_tree.setImage(imgLoc.concat("Object_Root.gif"));
    org_tree.setTarget(target);
    org_tree.setTreePosition(1, 1);
    org_tree.setTreeAreaWidth(300);
    
//    String projNo;
//    String projDesc;
//    String projNote;
//          
//    
//    for (int i = 0; i < size; i++){
//       projNo=set2.getValue("PROJ_NO");
//       projDesc=set2.getValue("PROJ_DESC");
//       projNote=set2.getValue("NOTE");
//
//
//       String expand_data = "&PROJ_NO=" + mgr.URLEncode(projNo);
//       target = getTargetScript(projNo,"","");
//
//       TreeListNode item = org_tree.addNode(projDesc);
//       item.setTarget(target);
//       item.setExpandData(expand_data);
//       
//       item.setImage(imgLoc.concat("Object_Position.gif"));
//       set2.next();
//    }

    
    

    

    
  }

  protected String getTargetScript(String org_no,String subProjecttype,String rev)
  {
    ASPManager mgr = getASPManager();
    String target;
    if (mgr.isEmpty(org_no))
      target = "SchEps.page' target='ChildMain";
    else
      target = "SchEps.page?PROJ_NO=" + mgr.URLEncode(org_no)+"&ID="+mgr.URLEncode(subProjecttype)+"&REV="+mgr.URLEncode(rev) + "' target='ChildMain";
    return target;
  }

  public void createTree()
  {
    ASPManager mgr = getASPManager();

    createTreeRoot();
    okFind2();

    int size = set.countRows();
    set.first();
    for (int i = 0; i < size; i++)
    {
       String org_no = set.getValue("ID");
       String org_desc = mgr.isEmpty(set.getValue("EPS_NAME")) ? "" : set.getValue("EPS_NAME");
       String org_type = set.getValue("REV");

       String expand_data =  "&PROJ_NO=" + mgr.URLEncode(set.getValue("PROJ_NO"))+"&ID=" + mgr.URLEncode(org_no)+"&REV=" + mgr.URLEncode(org_type);
      String target = getTargetScript(set.getValue("PROJ_NO"),org_no,org_type);

      TreeListNode item = org_tree.addNode(org_desc);
      item.setTarget(target);
      item.setExpandData(expand_data);

//      if ("ORG".equals(org_type))
//      {
//        item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
//      }
//      else
//      {
        item.setImage(imgLoc.concat("Object_Position.gif"));
//      }
      set.next();
    }
  }

  public void validate()
  {
    ASPManager mgr = getASPManager();
    String val = mgr.readValue("VALIDATE");
    if ("EXPAND_TREE".equals(val))
    {
      TreeList temp_node = new TreeList(mgr, "DUMMY");
      String projNo = mgr.readValue("PROJ_NO");
      String id = mgr.readValue("ID");
      String rev = mgr.readValue("REV");

//      if(mgr.isEmpty(id))
      buildSubNode(temp_node, projNo,id,rev);
//      else
//         buildSubNode(temp_node, projNo,id);
      String dynamicData = temp_node.getDynamicNodeString();

      mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
    }
    mgr.endResponse();
  }

  public void buildSubNode(TreeList parent_tree, String parent_org_no,String id,String rev)
  {
    ASPManager mgr = getASPManager();
    okFind2();

    if (set.countRows() == 0)
    {
      set.clear();
    }
    else
    {
      int countRows = set.countRows();
      set.first();
      for (int i = 0; i < countRows; i++)
      {
        String org_no = set.getValue("ID");
        String org_desc = mgr.isEmpty(set.getValue("EPS_NAME")) ? "" : set.getValue("EPS_NAME");
        String org_type = set.getValue("REV");

        String expand_data =  "&PROJ_NO=" + mgr.URLEncode(parent_org_no)+"&ID=" + mgr.URLEncode(org_no)+"&REV=" + mgr.URLEncode(rev);
        String target = getTargetScript(parent_org_no,org_no,org_type);

        TreeListNode item = parent_tree.addNode(org_desc);
        item.setTarget(target);
        item.setExpandData(expand_data);

//        if ("ORG".equals(org_type))
//        {
//          item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
//        }
//        else
//        {
          item.setImage(imgLoc.concat("Object_Position.gif"));
//        }
        set.next();
      }
    }
  }

  public void preDefine()
  {
    ASPManager mgr = getASPManager();

    blk = mgr.newASPBlock("MAIN");

    blk.addField("PROJ_NO")
      .setHidden();

    blk.addField("ID")
      .setHidden();

    blk.addField("REV")
      .setHidden();

    blk.addField("EPS_ID")
      .setHidden();

    blk.addField("EPS_NAME")
      .setHidden();

    blk.addField("PARENT_ID")
      .setHidden();

    blk.setView("SCH_EPS");

    set = blk.getASPRowSet();

    blk2 = mgr.newASPBlock("NODE");

    blk2.addField("ITEM1_PROJ_NO").setDbName("PROJ_NO")
      .setHidden();

    blk2.addField("PROJ_DESC")
      .setHidden();

    blk2.addField("NOTE")
      .setHidden();

    blk2.setView("GENERAL_PROJECT");

    set2 = blk2.getASPRowSet();
  }

  protected String getDescription()
  {
    return "SCHEPSTREE: Sch Eps Tree";
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

    if (org_tree != null) {
      out.append(org_tree.show());
    }
    return out;
  }
}