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
 * File        : MyProjectStatus.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    : 27.03.2001 - NUGOLK - Adapted from the Lear Project.
 *               28.05.2001 - NUGOLK - Call ID 65629 - Portlet is now Portal aware.
 *               07.09.2001 - Muneera- Removed the duplicated translation constants and stndardized the non standard constants.
 *               24.10.2001 - Shamal - Call ID 70333  Added lov's to Project Id and Sub Project Id fields in the printCustomBody() method and overwrite the closeLOVWindow() script
                                       to reload the form when select a project id from lov. Also disable the Project selection option when at the Project portal page.
                                       corrected the CPI,SPI calculation by sending the correct sub project id's to the server method.
                                       Added a new check box to select projects that have a critical Path and modified
                                       where statements according to the conditions.
 *               18-10-2003 - Nimhlk - Call ID: 108393 - Modified printContents().
 *               05-11-2003 - Krsilk - Call ID: 109508 - Modified printCustomBody to put URL encoded
 *                                     escape characters for the dynamic LOV "TEMP_SUB_PROJECT" in an ordinary URL string.
 *               16-02-2004 - RuRalk - Bug 40534, Extracted project id from the profile variable saved under the portal.
 *               08-03-2004 - CHRALK - Bug 40559, Project_Id & project portal check written to the page context and read from runCustom().
 *               2005-03-30 - HAUNLK - Replaced deprecated method field_separator with fieldSeparator & record_separator with recordSeparator .
 *               2005-12-08 - SAALLK - Call 128802, Removed all calculation methods for CPI/SPI. Now it is calculated with PLAIN calculation method.
 *               2005-12-09 - SAALLK - Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation.
 *               2006-11-13 - KARALK - B58216. Merged. SQL Injection.
 *					  2007-03-22 - Rucslk - Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 
 *               2007-08-07 - ASSALK - Merged Bug 64068. removed additional mgr.translate() statements.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.projw.portlets;

// IFS libs
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

// Standard Java libs
import java.util.*;
import java.io.*;

public class MyProjectStatus extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.projw.portlets.MyProjectStatus");
   private final static int size = 10;

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

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private transient String project_id;
   private transient String parent_sub_project_id;
   private transient String portlet_title;
   private transient int skip_rows;
   private transient int db_rows;
   private transient int max_rows;
   private transient double green_limit;
   private transient double red_limit;
   private transient String weighted_type;
   private transient boolean first_time_flag = true;
   private transient boolean project_portal=false;
   private transient boolean criticalPath;

   //==========================================================================
   //  Construction
   //==========================================================================

   public MyProjectStatus( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if(DEBUG) debug(this+": MyProjectStatus.<init> :"+portal+","+clspath);
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {
      super.doReset();

      if(DEBUG) debug("=============== DO RESET ========================");
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if(DEBUG) debug(this+": MyProjectStatus.clone("+mgr+")");

      MyProjectStatus page = (MyProjectStatus)(super.clone(mgr));

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      skip_rows = 0;

      return page;
   }

   //==========================================================================
   //
   //==========================================================================

   protected void preDefine() throws FndException
   {
      if(DEBUG) debug(this+": MyProjectStatus.preDefine()");
      ctx = getASPContext();
      blk = newASPBlock("MAIN");

      addField(blk, "PROJECT_ID").
         setHyperlink("projw/ChildTree.page","PROJECT_ID").
         setHidden();

      addField(blk, "PARENT_SUB_PROJECT_ID").
         setHyperlink("projw/SubProject.page","PROJECT_ID,PARENT_SUB_PROJECT_ID").
         setHidden();;

      addField(blk, "SUB_PROJECT_ID").
         setHyperlink("projw/SubProject.page","PROJECT_ID,SUB_PROJECT_ID");

      addField(blk, "DESCRIPTION").
         setHyperlink("projw/SubProject.page","PROJECT_ID,SUB_PROJECT_ID");

      addField(blk, "MANAGER");

      addField(blk, "SPI", "Number").
         setFunction("''");

      addField(blk, "CPI", "Number").
         setFunction("''");

      addField(blk, "DUMMY_NUMBER","Number", "#########0.############").
         setFunction("''").
         setHidden();

      blk.setView("SUB_PROJECT");

      // Table config
      tbl = newASPTable( blk );
      tbl.disableQueryRow();
      tbl.disableQuickEdit();
      tbl.disableRowCounter();
      tbl.disableRowSelect();
      tbl.disableRowStatus();
      tbl.unsetSortable();

      rowset = blk.getASPRowSet();

      getASPManager().newASPCommandBar(blk);

      appendJavaScript("function customizeProjectStatus(obj,id)");
      appendJavaScript("{");
      appendJavaScript("   customizeBox(id);");
      appendJavaScript("}\n");

      init();
   }


   protected void init() throws FndException
   {
      if(DEBUG) debug(this+": MyProjectStatus.init()");

      super.init();

      green_limit = getASPField("DUMMY_NUMBER").parseNumber(readProfileValue("GREEN", getASPField("DUMMY_NUMBER").formatNumber(1.0)));
      red_limit = getASPField("DUMMY_NUMBER").parseNumber(readProfileValue("RED", getASPField("DUMMY_NUMBER").formatNumber(0.8)));
      max_rows = Str.toInt(readProfileValue ( "PRF_MAXROWS", size+"" ));
      portlet_title = readProfileValue("PORTLET_TITLE",translate(getDescription()));
      project_id = readProfileValue("PROJECT_ID");

      if( Str.isEmpty(project_id) )
         project_id = readValue("TEMP_PROJECT");

      parent_sub_project_id = readProfileValue("SUB_PROJECT_ID");
      criticalPath = readProfileFlag("CRITICAL_PATH",false);

      // Check for user action
      String cmd = readValue("CMD");
      if( "PREV".equals(cmd) && skip_rows>=max_rows )
         skip_rows -= max_rows;
      else if( "NEXT".equals(cmd) && skip_rows<=db_rows-max_rows )
         skip_rows += max_rows;
      else if ( "SUBP".equals(cmd))
      {
         first_time_flag = false;
         skip_rows = 0;
      }

      else
         first_time_flag = true;
   }

   protected void run()
   {
      String where_stmt;

      if(DEBUG) debug(this+": MyProjectStatus.run()");

      // Check whether this is a project portal. If so display only data relevant to that project id.
      ASPManager mgr   = getASPManager();
      project_portal=false;
      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);

      project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);
      if (!(mgr.isEmpty(project_id)))
      {
         project_portal= true;
         ctx.writeFlag("PROJECT_PORTAL", project_portal);
         ctx.writeValue("PROJECT_ID", project_id);
      }
      else
      {
         project_id = readProfileValue("PROJECT_ID");
         ctx.writeFlag("PROJECT_PORTAL", project_portal);
      }

      parent_sub_project_id = readProfileValue("SUB_PROJECT_ID");
      
      // After call 128802 Weighted Type will always be "PLAIN"
      weighted_type = "PLAIN";

      // Build block query
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      ASPQuery             qry   = trans.addQuery(blk);
      qry.setOrderByClause("PROJECT_ID,SUB_PROJECT_ID");
      qry.setBufferSize(max_rows);
      qry.skipRows(skip_rows);
      qry.includeMeta("ALL");

      if ( first_time_flag )
            parent_sub_project_id = readProfileValue("SUB_PROJECT_ID");
      else
      {
         parent_sub_project_id = readValue("TEMP_SUB_PROJECT");
      }

      if (project_id != null)
      {
         where_stmt = "PROJECT_ID = ? ";
         qry.addParameter(this,"PROJECT_ID",project_id);

         if (Str.isEmpty(parent_sub_project_id))
            where_stmt = where_stmt + " AND PARENT_SUB_PROJECT_ID IS NULL ";
         else
	 {
	     where_stmt = where_stmt + " AND PARENT_SUB_PROJECT_ID = ? ";
             qry.addParameter(this,"SUB_PROJECT_ID",parent_sub_project_id);
	 }
         if(criticalPath)
            where_stmt += " AND EXISTS(SELECT 1 FROM ACTIVITY WHERE SUB_PROJECT.PROJECT_ID = ACTIVITY.PROJECT_ID AND SUB_PROJECT.SUB_PROJECT_ID = ACTIVITY.SUB_PROJECT_ID AND EARLY_START= LATE_START) ";
         qry.addWhereCondition( where_stmt );
         submit(trans);
      }



      db_rows = blk.getASPRowSet().countDbRows();

      trans.clear();

      // Populate SPI and CPI columns
      String critical;
      if(criticalPath)
         critical ="1";
      else
         critical ="0";

      if(rowset.countRows() > 0)
      {
         rowset.first();
         for(int count=0;count<rowset.countRows();count++)
         {
            ASPCommand cmd1 = getASPManager().newASPCommand();
            cmd1.defineCustomFunction(this, "Sub_Project_API.get_spi_performance", "DUMMY_NUMBER");
            cmd1.addParameter(this, "PROJECT_ID", rowset.getValue("PROJECT_ID"));
            cmd1.addParameter(this, "SUB_PROJECT_ID", rowset.getValue("SUB_PROJECT_ID"));
            cmd1.addParameter(this, "PROJECT_ID", "WEIGHTED_TYPE" + IfsNames.fieldSeparator + weighted_type+ IfsNames.recordSeparator+"CRITICAL_PATH"+IfsNames.recordSeparator+critical+IfsNames.recordSeparator);
            trans.addCommand("SPIRETVAL"+count, cmd1);

            ASPCommand cmd2 = getASPManager().newASPCommand();
            cmd2.defineCustomFunction(this, "Sub_Project_API.get_cpi_performance", "DUMMY_NUMBER");
            cmd2.addParameter(this, "PROJECT_ID", rowset.getValue("PROJECT_ID"));
            cmd2.addParameter(this, "SUB_PROJECT_ID", rowset.getValue("SUB_PROJECT_ID"));
            cmd2.addParameter(this, "PROJECT_ID", "WEIGHTED_TYPE" + IfsNames.fieldSeparator + weighted_type+ IfsNames.recordSeparator+"CRITICAL_PATH"+IfsNames.recordSeparator+critical+IfsNames.recordSeparator);
            trans.addCommand("CPIRETVAL"+count, cmd2);

            rowset.next();
         }

         trans = perform(trans);
         rowset.first();
         for(int count=0;count<rowset.countRows();count++)
         {
            ASPBuffer temp_row = rowset.getRow();
            temp_row.setValue("SPI",trans.getBuffer("SPIRETVAL"+ count + "/DATA").getValue("DUMMY_NUMBER"));
            temp_row.setValue("CPI",trans.getBuffer("CPIRETVAL"+ count + "/DATA").getValue("DUMMY_NUMBER"));
            rowset.setRow(temp_row);

            rowset.next();


         }
         rowset.first();
      }
   }


   //==========================================================================
   //
   //==========================================================================

   public String getTitle( int mode )
   {
      if(DEBUG) debug(this+": MyProjectStatus.getTitle("+mode+")");
      return (Str.isEmpty(getMyTitle())?translate(getDescription()):getMyTitle());
   }


   public void printContents() throws FndException
   {
      if(DEBUG) debug(this+": MyProjectStatus.getContents()");

      // hidden field for next and previous links
      printHiddenField("CMD","");

      if (Str.isEmpty(project_id))
      {
         printNewLine();
         printScriptLink("PROJWPORTLETSMYPROJECTSTATUSCUSTOMIZEMSG: Customize this portlet ", "customizeProjectStatus");
         printNewLine();
      }
      else
      {
         printCustomTable(); // Print the table

         if(max_rows < db_rows )  // If necessary show next-previous links
            printNextPreviousLinks();
         else
            printNewLine();
      }
      appendDirtyJavaScript(
          "function submitProjStatPortlet(obj,id)"+
          "{"+
          "   getPortletField(id,'CMD').value = 'SUBP';"+
          "   submitPortlet(id);"+
          "}\n");
   }

   public void printNextPreviousLinks() throws FndException
   {
      printNewLine();
      printSpaces(5);
      if(skip_rows>0)
         printSubmitLink("PROJWPORTLETSMYPROJECTSTATUSPREVIOUSLINK: Previous","prevCust");
      else
         printText("PROJWPORTLETSMYPROJECTSTATUSPREVIOUSLINK: Previous");

      printSpaces(5);
      String rows = translate("PROJWPORTLETSMYPROJECTSTATUSDISPLAYEDROWS: (Rows &1 to &2 of &3)",
                              (skip_rows+1)+"",
                              (skip_rows+max_rows<db_rows?skip_rows+max_rows:db_rows)+"",
                              db_rows+"");
      printText( rows );
      printSpaces(5);

      if(skip_rows<db_rows-max_rows)
         printSubmitLink("PROJWPORTLETSMYPROJECTSTATUSNEXTLINK: Next","nextCust");
      else
         printText("PROJWPORTLETSMYPROJECTSTATUSNEXTLINK: Next");

      printNewLine();
      printNewLine();

      appendDirtyJavaScript(
         "function prevCust(obj,id)"+
         "{"+
         "   getPortletField(id,'CMD').value = 'PREV';"+
         "}\n"+
         "function nextCust(obj,id)"+
         "{"+
         "   getPortletField(id,'CMD').value = 'NEXT';"+
         "}\n");
   }

   public void printCustomBody() throws FndException
   {
      printNewLine();
      setFontStyle(BOLD);
      printText("PROJWPORTLETSMYPROJECTSTATUSSEARCHCRITERIA: Search Criteria");
      endFont();

      printText("PROJWPORTLETSMYPROJECTSTATUSINFOMSG: Use these fields to define your search. Mandatory fields are marked with an asterisk.");
      printNewLine();
      printNewLine();


      // Populate list box values
      ASPManager mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry;

      if (!Str.isEmpty(readValue("TEMP_PROJECT")))
      {
         project_id = readValue("TEMP_PROJECT");
      }

      appendToHTML("<table border=0 cellpadding=2 cellspacing=0 width=100%>");
      appendToHTML("<tr>");


      // Table Row 1 Cell 1
      appendToHTML("<td align=LEFT width=50%>");

      if (Str.isEmpty(project_id))
         printText("PROJWPORTLETSMYPROJECTSTATUSNOPROJECT: No Project selected.");
      else
      {
         printText("PROJWPORTLETSMYPROJECTSTATUSCURPROJECT: Current Project: ");
         printSpaces(1);
         printText(project_id+"  ");
      }

      printNewLine();
      if(!project_portal)
      {

         printNewLine();
         printText("PROJWPORTLETSMYPROJECTSTATUSCHOOSEPROJECT: Choose your project:");
         printNewLine();
         printField("TEMP_PROJECT",project_id,15,"customizeProjectStatus");
         printDynamicLOV("TEMP_PROJECT", "PROJECT","PROJWPORTLETSMYPROJECTSTATUSPROJLOV: List of Projects");

      }

      if(DEBUG) debug("5");
      if (!Str.isEmpty(project_id))
      {
         trans.clear();
         printNewLine();
         printNewLine();

         if (parent_sub_project_id != null)
         {
            printText("PROJWPORTLETSMYPROJECTSTATUSCURSUBPROJECT: Current Sub Project: ");
            printText(parent_sub_project_id +" ");
            printNewLine();
            printNewLine();
         }
         printText("PROJWPORTLETSMYPROJECTSTATUSCHOOSESUBPROJECT: Choose your Sub Project: ");
         printNewLine();
         printField("TEMP_SUB_PROJECT", parent_sub_project_id, 10,50);
         printDynamicLOV("TEMP_SUB_PROJECT","SUB_PROJECT","PROJWPORTLETSMYPROJECTSTATUSSUBPROJLOV: List of Sub Projects","PROJECT_ID=\\'"+mgr.URLEncode(project_id)+"\\'");

      }

      // Print red limit chooser
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTSTATUSCHOOSEREDLIMIT: Red less than ");
      printField("RED_LIMIT", getASPField("DUMMY_NUMBER").formatNumber(red_limit), 10);

      // Print yellow limit
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTSTATUSCHOOSEYELLOWLIMIT: Yellow between");
      printText(" "+ getASPField("DUMMY_NUMBER").formatNumber(red_limit) + " ");
      printText("PROJWPORTLETSMYPROJECTSTATUSAND: and ");
      printText(" "+ getASPField("DUMMY_NUMBER").formatNumber(green_limit));

      // Print green limit chooser
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTSTATUSCHOOSEGREENLIMIT: Green more than ");
      printField("GREEN_LIMIT", getASPField("DUMMY_NUMBER").formatNumber(green_limit), 10);
      printNewLine();

      // Table Row 1 Cell 2
      endTableCell();
      beginTableCell("LEFT");
      printNewLine();
      printCheckBox("CRITVIEW",criticalPath);
      printText("PROJWPORTLETSMYPROJECTSTATUSDISPLAYCPAO:  Display projects including Critical Path Only");
      printNewLine();


      endTableCell();

      nextTableRow();
      nextTableRow();
      beginTableCell();
      printNewLine();
      setFontStyle(BOLD);
      printText("PROJWPORTLETSMYPROJECTSTATUSLAYOUT: Layout");
      endFont();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTSTATUSLAYINFMSG: Use these fields to determine how the information is displayed in your portlet.");
      printNewLine();
      printNewLine();

      beginTransparentTable();
      beginTableBody();
      printTableCell("PROJWPORTLETSMYPROJECTSTATUSPORTLETNAME: Portlet Name: ");
      beginTableCell();
      printField("TITLE", portlet_title, 40);
      endTableCell();
      nextTableRow();
      printTableCell("PROJWPORTLETSMYPROJECTSTATUSMAXROWS: Max rows to display: ");
      beginTableCell();
      printField("MAX_ROWS", Str.intToString(max_rows,0),5);
      endTableCell();
      endTableBody();
      endTable();

      appendToHTML("</td></tr></tbody></table>");
      appendDirtyJavaScript(
                           " function closeLOVWindow(value)       \n"+
                           "{                                           \n"+

                               " fld = getField_(lov_field_name,lov_row_nr);   \n"+
                               " fld.value = value;                            \n"+
                               " lov_window.close();                           \n"+
                               " lov_window = null;                            \n"+
                               " eval(post_lov_function+'(lov_row_nr);');      \n"+
                               " if((fld.name).indexOf('TEMP_PROJECT')!=-1)    \n"+
                                   "customizeBox(f.__PORTLET_ID.value);        \n"+
                            "} \n");

    }


   public void submitCustomization() throws FndException
   {
      // Save green limit
      green_limit = getASPField("DUMMY_NUMBER").parseNumber(readValue ("GREEN_LIMIT"));
      if( !Str.isEmpty(readValue ("GREEN_LIMIT")) )
         writeProfileValue("GREEN", getASPField("DUMMY_NUMBER").formatNumber(green_limit));

      // Save red limit
      red_limit = getASPField("DUMMY_NUMBER").parseNumber(readValue("RED_LIMIT"));
      if( !Str.isEmpty(readValue("RED_LIMIT")) )
        writeProfileValue("RED", getASPField("DUMMY_NUMBER").formatNumber(red_limit));

      // Save project id
      if( !Str.isEmpty(readValue("TEMP_PROJECT")) )
            writeProfileValue("PROJECT_ID", readAbsoluteValue("TEMP_PROJECT"));

      // Save sub project id
      writeProfileValue("SUB_PROJECT_ID",readAbsoluteValue("TEMP_SUB_PROJECT"));

      // Save portlet title
      portlet_title = readValue("TITLE");
      writeProfileValue("PORTLET_TITLE", readAbsoluteValue("TITLE"));

      // Save max rows to display
      max_rows = Str.toInt(readValue("MAX_ROWS"));
      writeProfileValue("PRF_MAXROWS" , Str.intToString(max_rows,0));


      //Shamal - Save the critical path
      criticalPath  = "TRUE".equals(readValue("CRITVIEW"));
      writeProfileFlag("CRITICAL_PATH",criticalPath);

   }

   public static String getDescription()
   {
      return "PROJWPORTLETSMYPROJECTSTATUSMYPROJST: My  Project Status";
   }

   protected String getMyTitle()
   {
      return portlet_title;
   }

   public boolean canCustomize()
   {
      return true;
   }

   public void runCustom()
   {
      project_portal = ctx.readFlag("PROJECT_PORTAL", project_portal);
      if (project_portal)
         project_id = ctx.readValue("PROJECT_ID");
   }

   public void printCustomTable() throws FndException
   {
      String spi_image_url="";
      String cpi_image_url="";
      String url;
      double spi_dot_type, cpi_dot_type;

      ASPManager mgr = getASPManager();

      beginTable();
      beginTableTitleRow();
      printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSPROJECT: Project"));
      beginTableCell();

      url = "projw/ChildTree.page?PROJECT_ID="+mgr.URLEncode(project_id);
      printLink(project_id,url);
      endTableCell();

      printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSPARSUBPROJ: Parent Sub Project"));
      beginTableCell();

      if(DEBUG) debug("before");
      if(DEBUG) debug(parent_sub_project_id);

      // Populate sub project list select box
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery qry;
      qry = trans.addQuery("SUBPROJLIST", "SUB_PROJECT",  "SUB_PROJECT_ID,description", "PROJECT_ID= ?", "SUB_PROJECT_ID");
      qry.addParameter(this,"PROJECT_ID",project_id);

      trans = mgr.perform(trans);
      printSelectBox("TEMP_SUB_PROJECT", trans.getBuffer("SUBPROJLIST"),parent_sub_project_id, "submitProjStatPortlet");
      first_time_flag = false;
      if(DEBUG) debug("After");
      if(DEBUG) debug(parent_sub_project_id);
      endTableCell();
      endTableTitleRow();
      endTable();
      printNewLine();

      beginTable();
      beginTableTitleRow();
      printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSSUBPROJECT: Sub Project"));
      printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSSUBPROJDESC: Description"));
      if (!getASPField("SPI").isHidden())
         printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSSPIPERF: SPI Perf."));
      if (!getASPField("CPI").isHidden())
         printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSCPIPERF: CPI Perf."));
      if (!getASPField("MANAGER").isHidden())
         printTableCell(translate("PROJWPORTLETSMYPROJECTSTATUSMANAGER: Manager"));
      printTableCell("");
      endTableTitleRow();

      beginTableBody();

      rowset.first();
      if(DEBUG) debug("rowset.countRows : " + rowset.countRows());
      for (int i=0; i<rowset.countRows(); i++)
      {

         url = "projw/ChildTree.page"+"?PROJECT_ID="+mgr.URLEncode(rowset.getValue("PROJECT_ID"))+
                                      "&SUB_PROJECT_ID="+mgr.URLEncode(rowset.getValue("SUB_PROJECT_ID"));

         beginTableCell();
         printLink( rowset.getRow().getFieldValue(this,"SUB_PROJECT_ID"),url);
         endTableCell();
         beginTableCell();
         printLink( rowset.getRow().getFieldValue(this,"DESCRIPTION"),url);
         endTableCell();

         // Print SPI indicators
         if(mgr.isEmpty(rowset.getRow().getFieldValue(this,"SPI")))
            spi_dot_type = 0.0;
         else
            spi_dot_type = getASPField("DUMMY_NUMBER").parseNumber(rowset.getRow().getFieldValue(this,"SPI"));

         if ( spi_dot_type >= green_limit)
            spi_image_url = "projw/images/_greendot.gif";
         else if (spi_dot_type >= red_limit && spi_dot_type <= green_limit)
            spi_image_url = "projw/images/_yellowdot.gif";
         else if (spi_dot_type <= red_limit)
            spi_image_url = "projw/images/_reddot.gif";

         beginTableCell("CENTER");
         printImage(spi_image_url);// SPI Indicator
         endTableCell();

         // Print CPI indicators
         if(mgr.isEmpty(rowset.getRow().getFieldValue(this,"CPI")))
            cpi_dot_type = 0.0;
         else
            cpi_dot_type = getASPField("DUMMY_NUMBER").parseNumber(rowset.getRow().getFieldValue(this,"CPI"));

         if ( cpi_dot_type >= green_limit)
            cpi_image_url = "projw/images/_greendot.gif";
         else if (cpi_dot_type >= red_limit && cpi_dot_type <= green_limit)
            cpi_image_url = "projw/images/_yellowdot.gif";
         else if (cpi_dot_type <= red_limit)
            cpi_image_url = "projw/images/_reddot.gif";

         beginTableCell("CENTER");
         printImage(cpi_image_url); // CPI Indicator
         endTableCell();
         if (!getASPField("MANAGER").isHidden())
            printTableCell(rowset.getValue("MANAGER"));

         nextTableRow();
         rowset.next();
      }
      rowset.first();

      endTableBody();
      endTable();
   }

}