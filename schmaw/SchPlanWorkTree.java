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
*  File        : ChildNavigator.java 
*  Modified    :
*   2001-02-28   Shamal   Converted to Java.
*   2001-04-09   Shamal    Modify Deprecated Methods. getQueryString()
                           Code Review.
*   2001-05-23  Gunasiri - Avoid Exponential format conversion in sql query.Used getNumberValue() method for Number fields
*   and parameter values are given in client format.  
*   2002-11-01  Larelk   - Added order by clause in search_node     
*   2002-12-11  Gacolk   - Merged bug fixes in 2002-3 SP3
*   2004-02-11  Ishelk   - Bug 42686, The code to add the bgcolor parameter in applet tag was removed since
*                          the dependancies to the Applet was removed by the webclient. generateClientScript1()
*                          function was completely removed since it is no longer needed.
*   2004-03-11  Ishelk   - Bug 40602, The tree was completely redesigned according to the new tree classes of the webclient.
*   2004-10-06  Thsalk   - Added RMB for Add Valid Report Codes Wizard.
*   2004-10-22  Thsalk   - Fixed call 118721, passed projectId,subProjectId and activityNo to getAddReportCodesWiz() as strings. 
*   2005-11-16  ThGulk   - Replaced deprecated method addMenuItem with addDefinedPopup
*   2006-02-17   RURALK  - Changed getValue("ACTIVITY_SEQ") to getClientValue("ACTIVITY_SEQ") when adding to the URL and changed the Nuber format mask of the ACTIVITY_SEQ.
*   2006-05-23  VIATLK   - Changed the size of buffer.
*   2006-11-13  KARALK   - Bug Id 58216 Merged.
* ----------------------------------------------------------------------------
*/
 
package ifs.schmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class SchPlanWorkTree extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchPlanWbsTree");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;

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
   public SchPlanWorkTree(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();   
   
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("ID"))&&!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
      {
         search();   
      }
      else
         okFind();
   }


   
   public void buildSubNode(TreeList parent_tree, String parent_id,String proj_no, String id ,String rev)
   {
      ASPManager mgr = getASPManager();      
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(headblk);
      
      qry.addWhereCondition("PROJ_NO = '" + proj_no + "'");
      
      qry.addWhereCondition("ID = '"+ id +"'");  
      
//      qry.addWhereCondition("REV = '" + rev + "'");    
      
      qry.addWhereCondition("PARENT_ID = '"+ parent_id +"'");   
      
      qry.setOrderByClause("WBS_NO");  
      
      isBuildTree         = true;
      
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
      mgr.querySubmit(trans, headblk);
      
      if (headset.countRows() == 0) 
      {
         headset.clear();
      }
      else
      {
         int countRows = headset.countRows();
         headset.first();
         for(int i = 0; i < countRows; i++)
         {
            String no = headset.getValue("WBS_NO");
            String wbs_name = mgr.isEmpty(headset.getValue("WBS_NAME")) ? "" : headset.getValue("WBS_NAME");  
            String rev1 = mgr.isEmpty(headset.getValue("REV")) ? "" : headset.getValue("REV");
            String isleaf = mgr.isEmpty(headset.getValue("IS_LEAF")) ? "" : headset.getValue("IS_LEAF");
                     
            String expand_data = "&WBS_NO=" + mgr.URLEncode(no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) +"&ID="+ mgr.URLEncode(id);
            
            String target = getTargetScript(no,proj_no,id,rev1,isleaf);    
                
            TreeListNode item = parent_tree.addNode(wbs_name);
            item.setImage(imgLoc.concat("Object_Sub_Project.gif"));
            item.setTarget(target);    
            item.setExpandData(expand_data);    
            headset.next();
         }
      }
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
      String target = getTargetScript(mgr.readValue("WBS_NO"),mgr.readValue("PROJ_NO"),mgr.readValue("ID"),mgr.readValue("REV"),mgr.readValue("IS_LEAF"));
      initUrl = target;        
      tree = new TreeList(mgr);      
      tree.setLabel(mgr.readValue("WBS_NAME"));  
      tree.setImage(imgLoc.concat("Object_Project.gif"));    
      tree.setTarget(target);  
      tree.setTreePosition(1, 1);  
      tree.setTreeAreaWidth(300);      
          
   }
   
   protected String getTargetScript(String no,String proj_no,String id,String rev,String isleaf)
   {
      ASPManager mgr = getASPManager();
      String target;
      String path = mgr.getASPConfig().getApplicationPath()+"/schmaw/SchPlanWork.page?"; 
      if("1".equals(isleaf)){
         String expandData = "WBS_NO="+ headset.getValue("PARENT_ID") + "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&ID=" +mgr.URLEncode(id)+"&REV= " +mgr.URLEncode(rev)+"&IS_LEAF= " +mgr.URLEncode(isleaf) +"&WORK_NO= " +mgr.URLEncode(no);
         target = path + expandData + "' target='ChildMain";  
      }
      else
      {
         String expandData = "WBS_NO="+ mgr.URLEncode(no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&ID=" +mgr.URLEncode(id)+"&REV= " +mgr.URLEncode(rev)+"&IS_LEAF= " +mgr.URLEncode(isleaf) ;
         target = path + expandData + "' target='ChildMain"; 
      }
         
      return target;            
   }            
   
   public void createTree() 
   {
      ASPManager mgr = getASPManager();
              
      int size = headset.countRows();
      
      createTreeRoot();    
      
      headset.first();
      isBuildTree         = true;
      for (int i = 0; i < size; i++) 
      {
         String no = headset.getValue("WBS_NO");
         String name = mgr.isEmpty(headset.getValue("WBS_NAME")) ? "" : headset.getValue("WBS_NAME");     
         String proj_no = mgr.isEmpty(headset.getValue("PROJ_NO")) ? "" : headset.getValue("PROJ_NO");  
         String id = mgr.isEmpty(headset.getValue("ID")) ? "" : headset.getValue("ID");    
         String rev =mgr.isEmpty(headset.getValue("REV")) ? "" : headset.getValue("REV");      
         String isleaf =mgr.isEmpty(headset.getValue("IS_LEAF")) ? "" : headset.getValue("IS_LEAF"); 
         String expand_data = "&WBS_NO="+ mgr.URLEncode(no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&ID=" +mgr.URLEncode(id);
          
         String target = getTargetScript(no,proj_no,id,rev,isleaf);
             
             
         TreeListNode item = tree.addNode(name);
         item.setTarget(target);
         item.setExpandData(expand_data);
         
         item.setImage(imgLoc.concat("Object_Sub_Project.gif"));    
         headset.next();  
      }        
   }
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry = trans.addEmptyQuery(headblk);
      
      qry.addWhereCondition("PARENT_ID = '"+ mgr.getQueryStringValue("WBS_NO") +"'");  
      qry.addWhereCondition("ID = '"+ mgr.getQueryStringValue("ID") +"'");
      qry.addWhereCondition("PROJ_NO = '"+ mgr.getQueryStringValue("PROJ_NO") +"'");
      qry.addWhereCondition("REV = '"+ mgr.getQueryStringValue("REV") +"'");  
      qry.setOrderByClause("WBS_NO");
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
        
      mgr.querySubmit(trans, headblk);
      if (headset.countRows() == 0) 
         headset.clear();
   }
//   public void okFind2()
//   {
//      ASPManager mgr = getASPManager();
//      
//      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
//      ASPQuery qry = trans.addEmptyQuery(headblk);
//      
////      qry.addWhereCondition("PARENT_ID IS NULL");  
//      qry.addWhereCondition("ID = '"+ mgr.getQueryStringValue("ID") +"'");
//      qry.addWhereCondition("REV = '"+ mgr.getQueryStringValue("REV") +"'");
//      qry.addWhereCondition("WBS_NO = '"+ mgr.getQueryStringValue("WBS_NO") +"'");
//      qry.addWhereCondition("PROJ_NO = '"+ mgr.getQueryStringValue("PROJ_NO") +"'");
//      
//      qry.setOrderByClause("WBS_NO");
//      qry.includeMeta("ALL");
//      qry.setBufferSize(1000);
//      
//      mgr.querySubmit(trans, headblk);
//      if (headset.countRows() == 0) 
//         headset.clear();
//   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      if("EXPAND_TREE".equals(val))
      {
         TreeList node = new TreeList(mgr, "DUMMY");
         String proj_no =  mgr.readValue("PROJ_NO") ;  
         String id = mgr.readValue("ID");      
         String rev = mgr.readValue("REV");        
         String no = mgr.readValue("WBS_NO");     
         buildSubNode(node, no,proj_no,id,rev);        
         String dynamicData = node.getDynamicNodeString();  
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      
      headblk.addField("PROJ_NO").
              setHidden();
      
      headblk.addField("RELOAD").
              setHidden().
              setFunction("''");
      
      headblk.addField("ID").
              setHidden();

      headblk.addField("REV").
              setHidden(); 
      
      headblk.addField("WBS_NO").
              setHidden();
      
      
      headblk.addField("WBS_NAME").
              setHidden();
      
      headblk.addField("PARENT_ID").
              setHidden(); 
      
      headblk.addField("IS_LEAF").
              setHidden(); 
      
      headblk.setView("SCH_PLAN_WORK_WBS");
      headset = headblk.getASPRowSet();    
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
      out.clear();
      
      if (tree != null)
         out.append(tree.show());
      
//      if (isBuildTree)
//      {
//         out.append(tree.show());
//         if (mgr.isEmpty(mgr.readValue("RELOAD")) || mgr.readValue("RELOAD").equals("TRUE"))
//         {
//            appendDirtyJavaScript("populateRightFrame('"+initUrl+"'); \n");
//         }
//         isBuildTree = false;
//      }  
//      
//
//      if (mgr.isEmpty(mgr.getQueryStringValue("WBS_NO")))
//      {
//         appendDirtyJavaScript("populateRightFrame('empty.htm'); \n");
//      }
//
//      appendDirtyJavaScript("function populateRightFrame(url) \n");
//      appendDirtyJavaScript("{ \n");
//      appendDirtyJavaScript("   window.parent.frames.ChildMain.location.href=url; \n");
//      appendDirtyJavaScript("} \n");   
      return out;    
   }    
     
}              


