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
*  File        : QuaStandLibTree.java 
*  Modified    :

* ----------------------------------------------------------------------------
*/
 
package ifs.quamaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class QuanlityCheckSumTree extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuanlityCheckSumTree");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock nodeblk;
   private ASPRowSet nodeset;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPField f;
   private ASPPopup node_popup;
   private ASPBlockLayout headlay;
   
   private ASPBlock nodeblk_one;
   private ASPRowSet nodeset_one;
   private ASPBlock nodeblk_two;
   private ASPRowSet nodeset_two;
   private ASPBlock nodeblk_three;
   private ASPRowSet nodeset_three;
   
   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private TreeList tree;
    
   private String imgLoc;
   private boolean isBuildTree = false;
   private String initUrl;
   private ASPContext ctx;

   //===============================================================
   // Construction 
   //===============================================================
   public QuanlityCheckSumTree(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();   
   
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE"))){
         validate();    
      }
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO"))))
      {
         search();   
      }
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");  
      ctx=mgr.getASPContext();
      if ("EXPAND_TREE".equals(val) )
      {
         TreeList temp_node = new TreeList(mgr,"DUMMY");
         TreeListNode node;
         String nodeLabel;
         String expandData;     
         String dynamicData;      
         String target = "";     
         String tmpTarget;
         String unit;
         String projectNo;
         String standardPre;
         
         unit                      = mgr.readValue("UNIT");  
         projectNo                 = ctx.findGlobal("PROJ_NO");
         standardPre               = mgr.readValue("STANDARD_PRE");  
         String subProjectNo       = "";             
         String subProjDesc        = "";
         String subProjecttype     = "";
         String substandardPre     = "";
         String parentSubProjectNo = mgr.readValue("SUB_PROJ_NO");

         int n;
         
            nodeset_three.clear();
            if("".equals(parentSubProjectNo)||parentSubProjectNo == null){
               searchNode(projectNo, unit);  
            }else{
               searchNode(projectNo, standardPre, parentSubProjectNo);  
            }
            n = nodeset_three.countRows();
            for (int i=0; i<n; i++)
            {
               subProjectNo     = nodeset_three.getValue("SUB_PROJ_NO");
               subProjDesc      = nodeset_three.getValue("SUB_PROJ_NAME");
               substandardPre      = nodeset_three.getValue("STANDARD_PRE");
               nodeLabel        = subProjDesc;
               tmpTarget        = "PROJ_NO="+mgr.URLEncode(projectNo)+
                                  "&SUB_PROJ_NO="+mgr.URLEncode(subProjectNo)+
                                  "&STANDARD_PRE="+mgr.URLEncode(substandardPre);
               target           = "QuanlityCheckSumLine.page?"+tmpTarget;  
               expandData       = "&"+tmpTarget;
               node             = temp_node.addNode(nodeLabel,target+"' target=\'ChildMain", imgLoc+"Object_Sub_Project.gif",expandData);
               
               String rmbString = ";setRmbValues('" +mgr.URLEncode(subProjecttype)+"','"+mgr.URLEncode(subProjectNo)+ "','')";
               node.addDefinedPopup(node_popup.generateCall() + rmbString);
   
               nodeset_three.next();
            }            
          dynamicData = temp_node.getDynamicNodeString();
          mgr.responseWrite(dynamicData+"^");
          mgr.endResponse();      
      }
   }

   public void search()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry;            
      ctx=mgr.getASPContext();
      qry = trans.addEmptyQuery(headblk);    
      qry.addWhereCondition("PROJ_NO = ?");
      qry.addParameter("PROJ_NO", mgr.readValue("PROJ_NO"));
      ctx.setGlobal("PROJ_NO", mgr.readValue("PROJ_NO"));  
      qry.includeMeta("ALL");
      qry.setBufferSize(1);

      mgr.submit(trans);
      if (headset.countRows()==0)
      {      
         headset.clear();
      } else
      {
         buildTree();
      }
   }

   public void searchNode(String projectNo)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      nodeset_one.clear();
      q = trans.addEmptyQuery(nodeblk_one);       
      q.addWhereCondition("PROJ_NO = ? ");
      q.addParameter("PROJ_NO", projectNo);

      q.includeMeta("ALL");
      q.setBufferSize(nodeset_one.countDbRows());
      mgr.submit(trans);
//      mgr.perform(trans);
   }
   
 
   public void searchNode(String projectNo, String unit)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      nodeset_three.clear();
      q = trans.addEmptyQuery(nodeblk_three);
      q.addWhereCondition("PROJ_NO = ?  AND STANDARD_PRE = ? AND PARENT_PROJ_NO IS NULL");            
      q.addParameter("PROJ_NO", projectNo);
      q.addParameter("STANDARD_PRE",unit);
      q.includeMeta("ALL");
      q.setBufferSize(nodeset_three.countDbRows());
      mgr.submit(trans);
   }
   
   public void searchNode(String projectNo, String standardPre, String parentProjNo)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      nodeset_three.clear();
      q = trans.addEmptyQuery(nodeblk_three);
      q.addWhereCondition("PROJ_NO = ? AND PARENT_PROJ_NO = ? AND STANDARD_PRE = ?");            
      q.addParameter("PROJ_NO", projectNo);
      q.addParameter("PARENT_PROJ_NO", parentProjNo);
      q.addParameter("STANDARD_PRE", standardPre);
      q.includeMeta("ALL");
      q.setBufferSize(nodeset_three.countDbRows());
      mgr.submit(trans);
   }

   public void buildTree()
   {
      ASPManager mgr = getASPManager();

      TreeListNode node;
      String nodeLabel    = "";
      String expandData   = "";
      String target       = "";  
      String tmpTarget    = "";
      int n,m;
      String projNo = "";
      String unit="";

      isBuildTree         = true;

      String projectNo    = mgr.readValue("PROJ_NO");

      target              = "QuanlityCheckSumLine.page?PROJ_NO="+projectNo;
      initUrl             = target;
      tree                = new TreeList(mgr, headset.getValue("GENERAL_PROJECT_PROJ_DESC"),target+"' target=\'ChildMain", imgLoc+"Object_Project.gif");
      tree.setTreePosition(1, 1);
      tree.setTreeAreaWidth(300);
      searchNode(projectNo);
      //机组级别
      n = nodeset_one.countRows();     
      for (int i=0; i<n; i++)      
      {
         projNo  = projectNo;
         unit        = nodeset_one.getValue("UNIT");
         nodeLabel        = unit + "机组";
         tmpTarget        = "PROJ_NO="+mgr.URLEncode(projNo)+
                            "&UNIT="+mgr.URLEncode(unit);
//         target           = "QuanlityCheckSumLine.page?"+tmpTarget;
         expandData       = "&"+tmpTarget;
         node             = tree.addNode(nodeLabel,target+"' target=\'ChildMain", imgLoc+"Object_Sub_Project.gif",expandData);
     //    String rmbString = ";setRmbValues('" +mgr.URLEncode(projectNo)+"','"+mgr.URLEncode(subProjectNo)+ "','"+mgr.URLEncode(subPlanNo)+"')";
         node.addDefinedPopup(node_popup.generateCall() + "");    
         nodeset_one.next();
      }      
   }



   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      headblk.addField("PROJ_NO");
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
            setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)");
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.setView("GENERAL_PROJECT");
      headset = headblk.getASPRowSet();
   
      nodeblk_one = mgr.newASPBlock("NODE_ONE");
      nodeblk_one.addField("NODE_ONE_PROJ_NO").setDbName("PROJ_NO");
      nodeblk_one.addField("UNIT");
      nodeblk_one.setView("QUANLITY_PROJ_UNIT");
      nodeset_one = nodeblk_one.getASPRowSet();   
      
     /* nodeblk_two = mgr.newASPBlock("NODE_TWO");
      nodeblk_two.addField("NODE_TWO_PROJ_NO").setDbName("PROJ_NO");
      nodeblk_two.addField("NODE_TWO_UNIT").setDbName("UNIT");
      nodeblk_two.addField("STANDARD_PRE");
      nodeblk_two.setView("QUANLITY_PROJ_UNIT_SPEC");
      nodeset_two = nodeblk_two.getASPRowSet();     */    
      
      nodeblk_three = mgr.newASPBlock("NODE_THREE");
      nodeblk_three.addField("NODE_THREE_PROJ_NO").setDbName("PROJ_NO"); 
      nodeblk_three.addField("STANDARD_PRE");
      nodeblk_three.addField("SUB_PROJ_NO");
      nodeblk_three.addField("PARENT_PROJ_NO");
      nodeblk_three.addField("SUB_PROJ_NAME");
      nodeblk_three.setView("QUANLITY_PLAN_LINE_DIST");   
      nodeset_three = nodeblk_three.getASPRowSet();
      node_popup = newASPPopup("childnavigatorrmbs");
      
      addToJSFile();      
   }


   private void addToJSFile()
   {
   try
    {
        appendJavaScript("var _projectNo;\n");
        appendJavaScript("var _subProjectNo;\n");
        appendJavaScript("var _planNo;\n");
        appendJavaScript("function setRmbValues(projectNo_,subProjectNo_,planNo_)\n");
        appendJavaScript("{\n");
        appendJavaScript("   _projectNo     = projectNo_; \n");
        appendJavaScript("   _subProjectNo  = subProjectNo_; \n");
        appendJavaScript("   _planNo  = planNo_; \n");
        appendJavaScript("}\n");

        }
    catch (Exception e)
    {
    }
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return null;
   }

   protected String getTitle()
   {
      return null;
   }

   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      if (isBuildTree)
      {
         out.append(tree.show());
         if (mgr.isEmpty(mgr.readValue("RELOAD")) || mgr.readValue("RELOAD").equals("TRUE"))
         {
            appendDirtyJavaScript("populateRightFrame('"+initUrl+"'); \n");
         }
         isBuildTree = false;
      }   

      if (mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
      {
         appendDirtyJavaScript("populateRightFrame('empty.htm'); \n");
      }

      appendDirtyJavaScript("function populateRightFrame(url) \n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("   window.parent.frames.ChildMain.location.href=url; \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");            

      appendDirtyJavaScript("function refresh(popright)\n");
      appendDirtyJavaScript("{\n");  
      appendDirtyJavaScript("   this_url = this.location.href; \n");     
      appendDirtyJavaScript("   if (popright==\"TRUE\")\n");
      appendDirtyJavaScript("   {\n");  
      appendDirtyJavaScript("      this_url = this_url+'&RELOAD=TRUE' \n");   
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n"); 
      appendDirtyJavaScript("      this_url = this_url+'&RELOAD=FALSE' \n");   
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   window.open(this_url,\"contents\");\n");
      appendDirtyJavaScript("}\n");
      return out;
   }

               

}              


