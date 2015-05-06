package ifs.matmaw;

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

public class MatCodeTree extends ASPPageProvider
{
  public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchEpsTree");
  private ASPBlock blk;
  private ASPRowSet set;
  private ASPBlock blk2;
  private ASPRowSet set2;
  private ASPContext ctx;
  private String imgLoc;
  private TreeList org_tree;

  public MatCodeTree(ASPManager mgr, String page_path)
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
      okFind();
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

  public void okFind()
  {
    ASPManager mgr = getASPManager();

    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
    ASPQuery qry = trans.addEmptyQuery(blk2);

//    qry.addWhereCondition("PARENT_ID IS NULL");

//    qry.setOrderByClause("ID");

    qry.includeMeta("ALL");
    qry.setBufferSize(1000);

    mgr.querySubmit(trans, blk2);
    if (set2.countRows() == 0)
      set2.clear();
  }
  
  public void okFind2()
  {
     ASPManager mgr = getASPManager();

     ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

     ASPQuery qry = trans.addEmptyQuery(blk);

     if(mgr.isEmpty(mgr.readValue("MAT_NO")))
        qry.addWhereCondition("PROJ_NO = '" +  mgr.readValue("PROJ_NO") + "' AND  MAT_TYPE_ID = '" +  mgr.readValue("MAT_TYPE_ID") + "'"+" AND PARENT_NO IS NULL");
     else {
        qry.addWhereCondition("PROJ_NO= ? AND MAT_TYPE_ID = ? AND PARENT_NO = ? ");
        qry.addParameter("PROJ_NO",  mgr.readValue("PROJ_NO"));
        qry.addParameter("MAT_TYPE_ID", mgr.readValue("MAT_TYPE_ID"));
        qry.addParameter("PARENT_NO", mgr.readValue("MAT_NO"));
     }

//     qry.setOrderByClause("ID,REV");
     qry.includeMeta("ALL");
     qry.setBufferSize(1000);
     mgr.querySubmit(trans, blk);

  }

  public void createTreeRoot()
  {
    ASPManager mgr = getASPManager();

    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

    ASPQuery qry = trans.addEmptyQuery(blk2);
//    qry.addWhereCondition("PROJ_NO = '"+mgr.readValue("PROJ_NO")+"'");    
    qry.includeMeta("ALL");
    qry.setBufferSize(1000);
    mgr.querySubmit(trans, blk2);
    int size = set2.countRows();
    set2.first();
    org_tree = new TreeList(mgr);
//    String rootName = mgr.translate(set2.getValue("PROJ_DESC"));
    String target = getTargetScript(mgr.readValue("PROJ_NO"),"","");
    org_tree.setLabel(mgr.readValue("PROJ_DESC")+"Îï×Ê±àÂë");
    org_tree.setImage(imgLoc.concat("Object_Root.gif"));
    org_tree.setTarget(target);
    org_tree.setTreePosition(1, 1);
    org_tree.setTreeAreaWidth(300);
    
    String matTypeId;
    String matTypeName;
//    String projNote;
          
    
    for (int i = 0; i < size; i++){
     matTypeId  =set2.getValue("MAT_TYPE_ID");
     matTypeName=set2.getValue("MAT_TYPE_NAME");
//       projNote=set2.getValue("NOTE");


       String expand_data = "&PROJ_NO=" + mgr.readValue("PROJ_NO")+"&MAT_TYPE_ID=" + mgr.URLEncode(matTypeId);
       target = getTargetScript(mgr.readValue("PROJ_NO"),matTypeId,"");

       TreeListNode item = org_tree.addNode(matTypeName);
       item.setTarget(target);
       item.setExpandData(expand_data);
       
       item.setImage(imgLoc.concat("Object_Position.gif"));
       set2.next();
    }

    
    

    

    
  }

  protected String getTargetScript(String org_no,String subProjecttype,String rev)
  {
    ASPManager mgr = getASPManager();
    String target;
    if (mgr.isEmpty(org_no))
      target = "MatCode.page' target='ChildMain";
    else if(mgr.isEmpty(subProjecttype))
       target = "MatCode.page?PROJ_NO=" + mgr.URLEncode(org_no)+"' target='ChildMain";
    else if(mgr.isEmpty(rev))
       target = "MatCode.page?PROJ_NO=" + mgr.URLEncode(org_no)+"&MAT_TYPE_ID="+mgr.URLEncode(subProjecttype)+"' target='ChildMain";
    else
      target = "MatCode.page?PROJ_NO=" + mgr.URLEncode(org_no)+"&MAT_TYPE_ID="+mgr.URLEncode(subProjecttype)+"&MAT_NO="+mgr.URLEncode(rev) + "' target='ChildMain";
    return target;
  }

  public void createTree()
  {
    ASPManager mgr = getASPManager();

    createTreeRoot();
/*    okFind2();

    int size = set.countRows();
    set.first();
    for (int i = 0; i < size; i++)
    {
       String org_no = set.getValue("ID");
       String org_desc = mgr.isEmpty(set.getValue("EPS_NAME")) ? "" : set.getValue("EPS_NAME");
       String org_type = set.getValue("REV");

       String expand_data =  "&PROJ_NO=" + mgr.URLEncode(set.getValue("PROJ_NO"))+"&ID=" + mgr.URLEncode(org_no)+"&REV=" + mgr.URLEncode(org_type);
      String target = "";

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
    }*/
  }

  public void validate()
  {
    ASPManager mgr = getASPManager();
    String val = mgr.readValue("VALIDATE");
    if ("EXPAND_TREE".equals(val))
    {
      TreeList temp_node = new TreeList(mgr, "DUMMY");
      String projNo = mgr.readValue("PROJ_NO");
      String matTypeId = mgr.readValue("MAT_TYPE_ID");
      String matNo = mgr.readValue("MAT_NO");

//      if(mgr.isEmpty(id))
      buildSubNode(temp_node, projNo,matTypeId,matNo);
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
        String proj_no = set.getValue("PROJ_NO");
        String mat_type_id = set.getValue("MAT_TYPE_ID");
        String mat_no = set.getValue("MAT_NO");

        String expand_data =  "&PROJ_NO=" + mgr.URLEncode(proj_no)+"&MAT_TYPE_ID=" + mgr.URLEncode(mat_type_id)+"&MAT_NO=" + mgr.URLEncode(mat_no);
        String target = getTargetScript(proj_no,mat_type_id,mat_no);

        TreeListNode item = parent_tree.addNode(set.getValue("MAT_NAME"));
        item.setTarget(target);
        item.setExpandData(expand_data);

        if ("TRUE".equals(set.getValue("IS_LEAF")))
        {
          item.setImage(imgLoc.concat("Object_OrgUnit.gif"));
        }
        else
        {
          item.setImage(imgLoc.concat("Object_Position.gif"));
        }
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

    blk.addField("MAT_NO")
      .setHidden();

    blk.addField("MAT_NAME")
      .setHidden();
    
    blk.addField("ITEM0_MAT_TYPE_ID").setDbName("MAT_TYPE_ID")
    .setHidden();

    blk.addField("PARENT_NO")
      .setHidden();

    blk.addField("IS_LEAF")
    .setHidden();

    blk.setView("MAT_CODE");

    set = blk.getASPRowSet();

    blk2 = mgr.newASPBlock("NODE");

    blk2.addField("MAT_TYPE_NAME")
      .setHidden();

    blk2.addField("MAT_TYPE_ID")
      .setHidden();

    blk2.setView("MAT_TYPE");
    
    set2 = blk2.getASPRowSet();
    
  }

  protected String getDescription()
  {
    return "MATCODETREE: Mat Code Tree";
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