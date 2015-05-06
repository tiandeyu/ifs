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
 
package ifs.budgew;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ProjectBudgetLineTree extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.budgew.ProjectBudgetLineTree");


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
   private ASPContext ctx;
   //===============================================================
   // Construction 
   //===============================================================
   public ProjectBudgetLineTree(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();   
   
      imgLoc = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") + "common/images/";
      
      if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("BUDGET_NO"))&&!mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO")))
      {
         search();   
      }
          
   }


   
   public void buildSubNode(TreeList parent_tree, String parent_no,String proj_no, String budget_no)
   {
      ASPManager mgr = getASPManager();      
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPQuery qry = trans.addEmptyQuery(headblk);
      
      qry.addWhereCondition("PROJ_NO = '" + proj_no + "'");
      
      qry.addWhereCondition("BUDGET_NO = '"+ budget_no +"'");  
      
      qry.addWhereCondition("PARENT_NO = '"+ parent_no +"'");      
      qry.setOrderByClause("BUDGET_LINE_NO");
      
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
            String budget_line_no = headset.getValue("BUDGET_LINE_NO");
            String budget_name = mgr.isEmpty(headset.getValue("BUDGET_NAME")) ? "" : headset.getValue("BUDGET_NAME");  
                 
            String expand_data = "&BUDGET_LINE_NO=" + mgr.URLEncode(budget_line_no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) +"&BUDGET_NO="+ mgr.URLEncode(budget_no);
            
            String target = getTargetScript(budget_line_no,proj_no,budget_no);    
                
            TreeListNode item = parent_tree.addNode(budget_name);
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
      
      ctx = mgr.getASPContext();     
      String rootName = ctx.findGlobal("BUDGET_NAME");        
      String target = getTargetScript("",mgr.readValue("PROJ_NO"),mgr.readValue("BUDGET_NO"));
      initUrl = target;      
      tree = new TreeList(mgr);       
          
      tree.setLabel(rootName);
      tree.setImage(imgLoc.concat("Object_Project.gif"));    
      tree.setTarget(target);
      tree.setTreePosition(1, 1);  
      tree.setTreeAreaWidth(300);      
          
   }
   
   protected String getTargetScript(String budget_line_no,String proj_no,String budget_no)
   {
      ASPManager mgr = getASPManager();
      String target;
      String path = mgr.getASPConfig().getApplicationPath()+"/budgew/ProjectBudgetLine.page?";    
      String expandData = "BUDGET_LINE_NO="+ mgr.URLEncode(budget_line_no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&BUDGET_NO=" +mgr.URLEncode(budget_no);
      target = path + expandData + "' target='ChildMain";     
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
         String budget_line_no = headset.getValue("BUDGET_LINE_NO");
         String budget_name = mgr.isEmpty(headset.getValue("BUDGET_NAME")) ? "" : headset.getValue("BUDGET_NAME");  
         String proj_no = mgr.isEmpty(headset.getValue("PROJ_NO")) ? "" : headset.getValue("PROJ_NO");  
         String budget_no = mgr.isEmpty(headset.getValue("BUDGET_NO")) ? "" : headset.getValue("BUDGET_NO");  
            
         String expand_data = "&BUDGET_LINE_NO="+ mgr.URLEncode(budget_line_no) + "&PROJ_NO=" + mgr.URLEncode(proj_no) + "&BUDGET_NO=" +mgr.URLEncode(budget_no);
         
         String target = getTargetScript(budget_line_no,proj_no,budget_no);
           
           
         TreeListNode item = tree.addNode(budget_name);
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
      
      qry.addWhereCondition("PARENT_NO = '1' ");   
      qry.addWhereCondition("BUDGET_NO = '"+ mgr.getQueryStringValue("BUDGET_NO") +"'");
      qry.addWhereCondition("PROJ_NO = '"+ mgr.getQueryStringValue("PROJ_NO") +"'");
      
      qry.setOrderByClause("BUDGET_LINE_NO");
      qry.includeMeta("ALL");
      qry.setBufferSize(1000);
      
      mgr.querySubmit(trans, headblk);
      if (headset.countRows() == 0) 
         headset.clear();
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      if("EXPAND_TREE".equals(val))
      {
         TreeList budget_node = new TreeList(mgr, "DUMMY");
         String budget_line_no = mgr.readValue("BUDGET_LINE_NO");
         String proj_no =  mgr.readValue("PROJ_NO") ;
         String budget_no = mgr.readValue("BUDGET_NO");      
                   
         buildSubNode(budget_node, budget_line_no,proj_no,budget_no);    
         String dynamicData = budget_node.getDynamicNodeString();  
         
         mgr.responseWrite(String.valueOf(String.valueOf(dynamicData)).concat("^"));
      }
      mgr.endResponse();
   }
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
      
      headblk.addField("BUDGET_NO").
              setHidden();
      
      headblk.addField("RELOAD").
              setHidden().
              setFunction("''");
      
      headblk.addField("PROJ_NO").
              setHidden();
      
      headblk.addField("BUDGET_LINE_NO").
              setHidden();
      
      headblk.addField("BUDGET_NAME").
              setHidden();
      
      headblk.addField("PARENT_NO").
              setHidden();
      
      headblk.addField("TREE_LEVEL").
              setHidden();
      
      headblk.addField("SEQ_NO").
              setHidden();
                                    
      headblk.setView("PROJECT_BUDGET_LINE");
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
      if (isBuildTree)
      {
         out.append(tree.show());
         if (mgr.isEmpty(mgr.readValue("RELOAD")) || mgr.readValue("RELOAD").equals("TRUE"))
         {
            appendDirtyJavaScript("populateRightFrame('"+initUrl+"'); \n");
         }
         isBuildTree = false;
      }   

      if (mgr.isEmpty(mgr.getQueryStringValue("BUDGET_NO")))
      {
         appendDirtyJavaScript("populateRightFrame('empty.htm'); \n");
      }

      appendDirtyJavaScript("function populateRightFrame(url) \n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("   window.parent.frames.ChildMain.location.href=url; \n");
      appendDirtyJavaScript("} \n");   
      return out;    
   }    
     
}              


