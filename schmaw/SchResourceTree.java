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
 
package ifs.schmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SchResourceTree extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchResourceTree");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock nodeblk;
   private ASPRowSet nodeset;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPField f;
   private ASPPopup node_popup;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private TreeList tree;
    
   private String imgLoc;
   private boolean isBuildTree = false;
   private String initUrl;

   //===============================================================
   // Construction 
   //===============================================================
   public SchResourceTree(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();   
   
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("RESOURCE_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO"))))
      {
         search();   
      }
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      if ("EXPAND_TREE".equals(val) )
      {
         TreeList temp_node = new TreeList(mgr,"DUMMY");
         TreeListNode node;
         String nodeLabel;
         String expandData;
         String dynamicData;
         String target;
         String tmpTarget;
         String parentSubProjectNo = mgr.readValue("RESOURCE_LINE_NO");
         String planNo          = mgr.readValue("RESOURCE_NO");
         String projecttype          = mgr.readValue("PROJ_NO");
         String subProjectNo       = "";
         String subProjDesc        = "";
         String subProjecttype        = "";
         String activityNo         = "";
         String activitySeq        = "";
         String activityDesc       = "";
         int n;
         nodeset.clear();
         
         searchNode(planNo, projecttype,parentSubProjectNo);         

         n = nodeset.countRows();
         for (int i=0; i<n; i++)
         {
            subProjectNo     = nodeset.getValue("RESOURCE_LINE_NO");
            subProjDesc      = nodeset.getValue("RESOURCE_LINE_NAME");
            subProjecttype     = nodeset.getValue("PROJ_NO");

            nodeLabel        = subProjDesc;
            tmpTarget        = "RESOURCE_NO="+mgr.URLEncode(planNo)+
                               "&PROJ_NO="+mgr.URLEncode(subProjecttype)+
                               "&RESOURCE_LINE_NO="+mgr.URLEncode(subProjectNo);
            target           = "SchResourceLine.page?"+tmpTarget;
            expandData       = "&"+tmpTarget;
            node             = temp_node.addNode(nodeLabel,target+"' target=\'ChildMain", imgLoc+"Object_Sub_Project.gif",expandData);
            
            String rmbString = ";setRmbValues('" +mgr.URLEncode(planNo)+"','"+mgr.URLEncode(subProjectNo)+ "');";
            node.addDefinedPopup(node_popup.generateCall() + rmbString);

            nodeset.next();
         }
         dynamicData = temp_node.getDynamicNodeString();
         mgr.responseWrite(dynamicData+"^");
      }
       mgr.endResponse();      
   }

   public void search()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry;            
      
      qry = trans.addEmptyQuery(headblk);    
      qry.addWhereCondition("RESOURCE_NO= ? AND PROJ_NO = ?");
      qry.addParameter("RESOURCE_NO", mgr.readValue("RESOURCE_NO"));
      qry.addParameter("PROJ_NO", mgr.readValue("PROJ_NO"));
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

   public void searchNode(String standardNo,  String projecttype)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      nodeset.clear();
      q = trans.addEmptyQuery(nodeblk);      
      q.addWhereCondition("RESOURCE_NO = ? AND PROJ_NO = ? AND PARENT_ID IS NULL");    
      q.addParameter("RESOURCE_NO", standardNo);
      q.addParameter("PROJ_NO", projecttype);

      q.includeMeta("ALL");
      q.setBufferSize(nodeset.countDbRows());
      mgr.submit(trans);
   }

   public void searchNode(String projectNo,String projecttype, String parentSubNo)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      nodeset.clear();
      q = trans.addEmptyQuery(nodeblk);

      q.addWhereCondition("RESOURCE_NO = ? AND PROJ_NO = ?  AND PARENT_ID = ? ");            
      q.addParameter("RESOURCE_NO", projectNo);
      q.addParameter("PROJ_NO", projecttype);
      q.addParameter("PARENT_ID", parentSubNo);
      q.includeMeta("ALL");
      q.setBufferSize(nodeset.countDbRows());

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
      int n;
      String subProjectNo = "";
      String subProjecttype = "";
      String subProjectName = ""; 

      isBuildTree         = true;

      String ResourceNo    = mgr.readValue("RESOURCE_NO");
      String ProjNo  = mgr.readValue("PROJ_NO");
      target              = "SchResourceLine.page?RESOURCE_NO="+mgr.URLEncode(ResourceNo)+"&PROJ_NO="+ProjNo;
      initUrl             = target;
      tree                = new TreeList(mgr, headset.getValue("RESOURCE_NAME"),target+"' target=\'ChildMain", imgLoc+"Object_Project.gif");
      tree.setTreePosition(1, 1);
      tree.setTreeAreaWidth(300);
      searchNode(ResourceNo,ProjNo);

      n = nodeset.countRows();
      
      for (int i=0; i<n; i++)      
      {
         subProjectNo     = nodeset.getValue("RESOURCE_LINE_NO");
         subProjecttype     = nodeset.getValue("PROJ_NO");
         subProjectName   = nodeset.getValue("RESOURCE_LINE_NAME");
         nodeLabel        = subProjectName;
         tmpTarget        = "RESOURCE_NO="+mgr.URLEncode(ResourceNo)+
                           "&PROJ_NO="+mgr.URLEncode(subProjecttype)+
                            "&RESOURCE_LINE_NO="+mgr.URLEncode(subProjectNo);
         target           = "SchResourceLine.page?"+tmpTarget;
         expandData       = "&"+tmpTarget;
         node             = tree.addNode(nodeLabel,target+"' target=\'ChildMain", imgLoc+"Object_Sub_Project.gif",expandData);

         String rmbString = ";setRmbValues('" +mgr.URLEncode(ResourceNo)+"','"+mgr.URLEncode(subProjectNo)+"','"+mgr.URLEncode(subProjectNo)+ "')";
     //    String rmbString = "";
         node.addDefinedPopup(node_popup.generateCall() + rmbString);
         
         
         
         nodeset.next();
      }
   }



   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      headblk.addField("PROJ_NO");
      headblk.addField("RESOURCE_NO");
      headblk.addField("RESOURCE_NAME");
//      headblk.addField("CONSTRUCT_ORG");
      headblk.addField("CREATE_PERSON");
      headblk.addField("CREATE_TIME");
      headblk.setView("SCH_RESOURCE");
   
      headset = headblk.getASPRowSet();
   
      nodeblk = mgr.newASPBlock("NODE");
      nodeblk.addField("NODE_PROJ_NO").setDbName("PROJ_NO");
      nodeblk.addField("NODE_RESOURCE_NO").setDbName("RESOURCE_NO");
      nodeblk.addField("RESOURCE_LINE_NO");
      nodeblk.addField("RESOURCE_LINE_NAME");
      nodeblk.addField("PARENT_ID");
      nodeblk.setView("SCH_RESOURCE_LINE");
   
      nodeset = nodeblk.getASPRowSet();

      node_popup = newASPPopup("childnavigatorrmbs");
      addToJSFile();      
   }


   private void addToJSFile()
   {
   try
    {
        appendJavaScript("var _projectNo;\n");
        appendJavaScript("var _subProjectNo;\n");
        appendJavaScript("function setRmbValues(projectNo_,subProjectNo_)\n");
        appendJavaScript("{\n");
        appendJavaScript("   _projectNo     = projectNo_; \n");
        appendJavaScript("   _subProjectNo  = subProjectNo_; \n");
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

      if (mgr.isEmpty(mgr.getQueryStringValue("RESOURCE_NO")))
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
   /*   appendDirtyJavaScript("function getAddReportCodesWiz()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("     var projectNo    = _projectNo; \n");
      appendDirtyJavaScript("     var subProjectNo = _subProjectNo; \n");
      appendDirtyJavaScript("     var activitySeq  = _activitySeq; \n");
      appendDirtyJavaScript("     var activityNo   = _activityNo; \n");
      appendDirtyJavaScript("   window.open('../prjrew/AddReportCodesWizard.page?PROJECT_ID='+projectNo+'&SUB_PROJECT_NO='+subProjectNo+'&ACTIVITY_SEQ='+activitySeq+'&ACTIVITY_NO='+activityNo,'AddReportCodesWizard','dependent=true,menubar=false,resizable=false,toolbar=false,scrollbars=yes,height=610,width=810');\n");           
      appendDirtyJavaScript("}\n");*/
      return out;
   }

               

}              


