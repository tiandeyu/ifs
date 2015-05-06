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
 * File         : ProjwMyProjectDetails.java
 * Description  : Project Details Portlet
 * Notes        :
 * ----------------------------------------------------------------------------
 * Author       : Kelum Ferdinandez
 * Date created : 01-11-2000
 * Files Called : 
 * ----------------------------------------------------------------------------
 * Modified By	:
 *    Date			Sign		History 
 *    2000-11-28   Jagath     	Fixed Call Id 55512.
 *    2000-12-04   Jagath     	Fixed Call Id 56335.
 *    2000-12-05   Shamal      Call ID - 56543 # Set the font Style to BOLD using setFontStyle method.
 *    2000-12-14   Shamal      Call ID - 57340 # corected the title.
 *    2001-01-12   KeFe        CID 58700 - Change the code belongs to Project Portal concept in order to compatible with 300b3.
 *    2001-04-29   Gunasiri    Modified the code to adhere to Add/Remove Portal Page Functionality.
 *    Gunasiri 2001-05-31 - CID 65786 -Removed Conditional Link from ProjectId.
 *    Gunasiri 2001-05-31 - Project Portal Check is added.
 *    Gunasiri 2001-07-04 - CID 23042 - Changed Heading.
 *    Gunasiri 2001-07-20 - CID 65635 - Remove project selection combobox conditionally in customisation page.
 *    Muneera  2001-09-07 - Removed the duplicated translation constants and stndardized the non standard constants.
 *    Shamal   2001-09-20 - CID - 65968 Added a Lov to Project Id field in printCustomBody() method.
 *    CHAG     2002-02-11    Bug 27815, Errors occur when view descriptions contains " Project Portal " is corrected.
 *    020821 ThAblk Bug 28977, This was corrected in PROJW 2.0.0. but in a different way. Now the project id get converted
 *    020821        into upper case on the client itself.
 *    020826 Bakalk Bug Id: 28976, Removed local variable "project_id" in run().
 *    031024 KrSilk Call 109070; Replaced the profile contant "TITLE" with "MY_TITLE" to make 
 *                  the Portlet Label value retained.
 *    040216 RuRalk Bug 40534, Extracted project id from the profile variable saved under the portal. 
 *    040308 CHRALK Bug 40559, Project_Id & project portal check written to the page context and read from runCustom().
 *
 * ----------------------------------------------------------------------------
 * ------------------------------------ EDGE ----------------------------------
 * ----------------------------------------------------------------------------
 *
 *    081205 SAALLK Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation.
 *    061114 KARALK BUG 58216. Merged, SQL Injection.
 * ----------------------------------------------------------------------------
 *		070321 Rucslk	Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 
 *    070807 ASSALK Merged Bug 64068. removed additional mgr.translate() statements.
 *
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.io.*;

public class ProjwMyProjectDetails extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.projw.portlets.ProjwMyProjectDetails");

   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;     

   //==========================================================================
   //  Mutable attributes
   //==========================================================================

   private transient String title;

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient ASPTransactionBuffer trans;
   private String where_stmt;
   private String myURL;
   private String url;
   private String view_name;
   private String view_desc;
   private boolean projectportal;


   //==========================================================================
   //  Profile variables (temporary)
   //==========================================================================

   private transient String project_id;
   private transient String project_name;
   private transient String cus_title;

   //==========================================================================
   //  Construction
   //==========================================================================

   public ProjwMyProjectDetails( ASPPortal portal, String clspath )
   {
      super(portal, clspath);      
   }   

   //==========================================================================
   //  Portlet description
   //==========================================================================

   public static String getDescription()
   {
      return("PROJWPORTLETSPROJWMYPROJECTDETAILSESC: My Project Details");           
   }  

   public String getTitle( int mode )
   {
      if (mode==MINIMIZED || mode==MAXIMIZED)
         return title;
      else
         return(translate("PROJWPORTLETSPROJWMYPROJECTDETAILSCUST1: My Project Details"));      
   }

   public String getBoxStyle( int mode )
   {
      return("");
   }

   //==========================================================================
   //  Configuration and initialisation
   //==========================================================================

   protected void preDefine()
   {
      ctx = getASPContext();

      blk = newASPBlock("MAIN");

      addField(blk, "PROJECT_ID");
      addField(blk, "NAME");
      addField(blk, "STATE");  
      addField(blk, "COMPANY").setHidden();     
      addField(blk, "COMPANY_NAME").setFunction("COMPANY_FINANCE_API.Get_Description(:COMPANY)");     
      addField(blk, "PROGRAM_ID").setHidden();     
      addField(blk, "PROJ_PROG_DESC").setFunction("PROJECT_PROGRAM_API.Get_Description(:COMPANY,:PROGRAM_ID)");   
      addField(blk, "MANAGER").setHidden();     
      addField(blk, "MANAGER_NAME").setFunction("PERSON_INFO_API.Get_Name(:MANAGER)");    
      addField(blk, "PLAN_START", "Date");          
      addField(blk, "PLAN_FINISH", "Date");           
      addField(blk, "ACTUAL_START", "Date");          
      addField(blk, "ACTUAL_FINISH", "Date");                  
      addField(blk, "DESCRIPTION");

      blk.setView("PROJECT");

      tbl = newASPTable(blk);
      rowset = blk.getASPRowSet();
      getASPManager().newASPCommandBar(blk);   

      init();
   }

   protected void init()
   {
      project_id = readProfileValue("PROJECT_ID", "");   
      cus_title = readProfileValue("MY_TITLE", translate("PROJWPORTLETSPROJWMYPROJECTDETAILSESC: My Project Details")); 


   }

   //==========================================================================
   //  Normal mode
   //==========================================================================

   protected void run()
   {
      ASPManager mgr  = getASPManager();
      trans         = mgr.newASPTransactionBuffer();
      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);


      String portal_project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);
      if (mgr.isEmpty(portal_project_id))
      {
         projectportal = false;
         ctx.writeFlag("PROJECTPORTAL", projectportal);
      }
      else
      {
         project_id = portal_project_id;      
         projectportal = true;
         ctx.writeFlag("PROJECTPORTAL", projectportal);
         ctx.writeValue("PROJECT_ID", project_id);         
      }      

      if (!(Str.isEmpty(project_id))&&(Str.isEmpty(cus_title)))
         title = translate("PROJWPORTLETSPROJWMYPROJECTDETAILSETLT31: My Project Details for "+project_id);
      else if (Str.isEmpty(cus_title))
         title = translate("PROJWPORTLETSPROJWMYPROJECTDETAILSETLT21: My Project Details");
      else
         title = cus_title;

      if ((!Str.isEmpty(project_id))&&(Str.isEmpty(project_name)))
      {
         ASPCommand cmd = mgr.newASPCommand();
         cmd.defineCustomFunction(this, "Project_API.Get_Name", "NAME");
         cmd.setParameter(this,"PROJECT_ID",project_id);
         trans.addCommand("GETPROJNAME", cmd);
         trans = mgr.perform(trans);
         project_name = trans.getValue("GETPROJNAME/DATA/NAME");
         trans.clear();
      }


      ASPQuery qry = trans.addQuery(blk);
      qry.includeMeta("ALL");          
      qry.addWhereCondition("PROJECT_ID = ? ");
      qry.addParameter(this,"PROJECT_ID",project_id);
      qry.setBufferSize(10);
      submit(trans);
      trans.clear();


   }

   public void printContents() throws FndException
   {
      if (Str.isEmpty(project_id))
      {
         printNewLine();
         appendToHTML("<a href=\"javascript:customizeBox('"+getId()+"')\">");
         printText("PROJWPORTLETSPROJWMYPROJECTDETAILSTLSEL: Customize this portlet");
         appendToHTML("</a>");             
         printNewLine();
      } else
      {
         printCustomTable(); 
      }     
   }    

   //==========================================================================
   //  Customize mode
   //==========================================================================   

   public boolean canCustomize()
   {
      return(true);
   }

   public void runCustom()
   {
      ASPManager mgr = getASPManager();
      trans  = mgr.newASPTransactionBuffer();
      projectportal = ctx.readFlag("PROJECTPORTAL", false);
      if (projectportal)
         project_id = ctx.readValue("PROJECT_ID");      
   }

   public void printCustomBody()
   {
      ASPManager mgr = getASPManager();

      printNewLine();
      printSpaces(5);  
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSRJSEL: Portlet Label: ");
      printField("MY_TITLE",cus_title,25);
      printSpaces(5);
      printNewLine();
      if (Str.isEmpty(project_id))
      {
         printNewLine();
         printNewLine();
         printSpaces(5);
         printText("PROJWPORTLETSPROJWMYPROJECTDETAILSPRSEL: No project selected.");

      } else
      {
         printNewLine();
         printNewLine();
         printSpaces(5);
         printText("PROJWPORTLETSPROJWMYPROJECTDETAILSROJ: Project: "); 
         printText(project_id+"  ");
         printText( project_name );       
      }
      printSpaces(5);
      if (!projectportal)
      {
         printNewLine();
         printNewLine();
         printSpaces(5); 
         printText("PROJWPORTLETSPROJWMYPROJECTDETAILSANPRJ: Select another project: ");
         printSpaces(5);
         printField("TEMP_PROJECT",project_id,15,"portletToUpper");
         printDynamicLOV("TEMP_PROJECT","PROJECT","PROJWPORTLETSPROJWMYPROJECTDETAILSPROJLOV: List of Projects");

         try
         {
            appendDirtyJavaScript("function portletToUpper(name, i) { name.value = name.value.toUpperCase(); }");
         } catch (FndException e)
         {
            Util.debug( Str.getStackTrace(e) );
         }
      }      
      printNewLine();
      printNewLine();     
   }

   public void submitCustomization()
   {
      project_id  = readValue("TEMP_PROJECT");
      cus_title = readValue("MY_TITLE");

      writeProfileValue("PROJECT_ID",readAbsoluteValue("TEMP_PROJECT"));   
      writeProfileValue("MY_TITLE",readAbsoluteValue("MY_TITLE")); 
   }

   //==========================================================================
   //  Other methods
   //==========================================================================

   public void printCustomTable() throws FndException
   {     
      ASPManager mgr = getASPManager(); 

      url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT") +"projw/ChildTree.page"+"?PROJECT_ID="+mgr.URLEncode(project_id);        


      appendToHTML("<table border=0 cellpadding=0 cellspacing=0 width='100%'>");
      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");

      appendToHTML("<table border=0 cellpadding=1 cellspacing=0>");

      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSPROID: Project ID:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printLink(rowset.getValue("PROJECT_ID"),url);
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSTLNAME: Project Name:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getValue("NAME"));
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSSTATE: Status:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getValue("STATE"));
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSCNAME: Company:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getValue("COMPANY_NAME"));
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSLPROG: Program:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getValue("PROJ_PROG_DESC"));
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSMNAME: Manager:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getValue("MANAGER_NAME"));
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSPSTA: Planned Start:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getRow().getFieldValue(this,"PLAN_START"));
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSLPFIN: Planned Finish:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getRow().getFieldValue(this,"PLAN_FINISH"));
      appendToHTML("</td></tr>");

      appendToHTML("<tr><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSTLASTA: Actual Start:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getRow().getFieldValue(this,"ACTUAL_START"));
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      setFontStyle(BOLD);
      printText("PROJWPORTLETSPROJWMYPROJECTDETAILSAFIN: Actual Finish:");
      endFont();
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printText(rowset.getRow().getFieldValue(this,"ACTUAL_FINISH"));
      appendToHTML("</td></tr>");   
      appendToHTML("</tbody></table>");
      appendToHTML("</td></tr></tbody></table>");
   }
}
