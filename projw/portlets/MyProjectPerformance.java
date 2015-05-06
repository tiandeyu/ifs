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
 * File        : MyProjectPerformance.java
 * Description : Displays the number of returns to suppliers
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified      :   2000-01-11 Created by ToFj
 *    ToFj       :   2000-08-11 Added weighting mechanism.
 *    KeFe       :   2000-21-11 Call IDs - 53862,53863,53864.
 *    Jagath     :   2000-02-12 Call IDs - 55511,54914.
 *    Shamal     :   2000-05-12 Call ID  - 56541 - set font style to bold AND corrected the translation IDs.
 *    ToFj       :   2000-11-12 Call ID  - 56493 - Formatted child table CPI and SPI. Modified customize layout.
 *    Sandaruwan :   2001-03-16 Incorporated the changes made in the Lear Project.
 *    Shamal     :   2001-05-28 Call ID - 65628 - Set as Project Portal aware.
 *    Enocha     :   2001-05-30 Call Id - #65711 corrected.
 *    Gunasiri       2001-05-31 Project Portal Check is added."PROJECT_PORTAL" String i used as an identifiction.
 *    Shiraz     :   2001-06-11 Call Id - 65702 (Rounded-up the CPI and SPI average values to two decimals.)
 *    Muneera        2001-09-07 Removed the duplicated translation constants and stndardized the non standard constants.
 *    Shamal     :   2001-09-19 Call Id - 65968  Added lov's to Project Id and Sub Project Id fields in the printCustomBody() method and overwrite the closeLOVWindow() script
                                                 to reload the form when select a project id from lov. Also disable the Project selection option when at the Project portal page.
 *    Shiraz     :   2001-10-17 Call Id - 70333  Added the new check box for the critical path, and the new functionality
 *                                               has been added to that option too.
 *                              Call Id - 70940  The condition has been checked whether the SPI and CPI Total values
 *                                               are NaN or Not.
 *    CHAG       :   2002-02-11 Bug 27815, Errors occur when view descriptions contains " Project Portal " is corrected.
 *    KrSilk     :   2003-11-05 Call ID: 109508 - Modified printCustomBody to put URL encoded
 *                              escape characters for the dynamic LOV "TEMP_SUB_PROJECT" in an ordinary URL string.
 *    Gacolk     :   2004-02-26 UNICODE: Removed the use of SUBSTRB.
 *    ---------------------------2004-1 SP1 Merge Start------------------------
 *    RuRalk     :   2004-02-16 Bug 40534, Extracted project id from the profile variable saved under the portal.
 *    CHRALK     :   2004-03-08 Bug 40559, Project_Id & project portal check written to the page context and read from runCustom().
 *    Ishelk     :   2004-03-29 Merged.
 *    Haunlk     :   2005-03-30    Replaced deprecated method field_separator  with fieldSeparator & record_separator with recordSeparator .
 *    ---------------------------2004-1 SP1 Merge End--------------------------
 *
 *    -------------------------------------------------------------------------
 *    ---------------------------------- EDGE ---------------------------------
 *    -------------------------------------------------------------------------
 *
 *    Saallk     :   2005-12-08 Call 128802, Modified the layout of the portal. Removed all but "PLAIN" calculation method for calculating CPI/SPI.
 *    Saallk     :   2005-12-09 Call 128800, Modified method call readGlobalProfileValue accordingly to new Web Client Foundation.
 *    Saallk     :   2005-12-14 Call 128805, Modified query WHERE condition so that activities only related to parent sub project will appear in detail mode.
 *    Chselk    :   2006-11-13 B58216. Merged. SQL Injection.
 * ----------------------------------------------------------------------------
 *              Rucslk   :        2006-03-22 Platform Tidy up in Sparx - Update the method submitCustomization - use the new function readAbsoluteValue() 
 *    ASSALK    :   2007-08-07 Merged Bug 64068. removed additional mgr.translate() statements.
 *    DinHlk    :   2007-08-08 Web Security - XSS Corrections.
 *    CHRALK    :   2010-03-15 Bug 89400, read/write the db_rows & skip_rows values to context cache.
 */

package ifs.projw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;


/**
 */
public class MyProjectPerformance extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================
   public static boolean DEBUG = true;
   private final static int size = 10;


   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================
   private ASPBlock        blk;
   private ASPBlock        blk2;
   private ASPContext      ctx;
   private ASPRowSet       rowset;
   private ASPTable        tbl;
   private ASPCommandBar   bar;
   //==========================================================================
   //  Mutable attributes
   //==========================================================================

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================
   private transient double  green_limit;
   private transient double  red_limit;
   private transient String  portlet_title;
   private transient String  rbsel;
   private transient String  showOnly;
   private transient String  project_id;
   private transient String  sub_project_id;
   private transient boolean detail_responsible;
   private transient boolean detail_type;
   private transient boolean detail_start;
   private transient boolean detail_finish;
   private transient boolean detail_bcwp;
   private transient boolean detail_acwp;
   private transient boolean detail_bcws;
   private transient boolean detail_cv;
   private transient boolean detail_sv;
   private transient boolean detail_cpi;
   private transient boolean detail_cpi_ind;
   private transient boolean detail_spi;
   private transient boolean detail_spi_ind;
   private transient String  weighted_type;
   private transient int skip_rows;
   private transient int db_rows;
   private transient double return_pct;
   private transient int max_row_;
   private transient boolean autosearch;
   private transient boolean display_search_;
   private transient boolean  find;
   private transient String view_name;
   private transient String view_desc;
   private transient boolean projectportal;

   private transient boolean critical_path;
   private transient String spi_val;


   public MyProjectPerformance( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {
      super.doReset();
   }


   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      MyProjectPerformance page = (MyProjectPerformance)(super.clone(mgr));

      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.bar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.ctx = page.getASPContext();
      page.blk2 = page.getASPBlock(blk2.getName());

      return page;
   }

   //==========================================================================
   //  Other methods
   //==========================================================================

   public static String getDescription()
   {
      return "PROJWPORTLETSMYPROJECTPERFORMANCETITLE: My Project Performance";
   }

   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPField f;
      ctx = getASPContext();

      blk = newASPBlock("MAIN");

      addField(blk, "PROJECT_ID");

      addField(blk, "SUB_PROJECT_ID");

      f = addField(blk, "PROJECT_NAME","String");
      f.setFunction("Project_API.get_name(:PROJECT_ID)");

      f = addField(blk, "ACTIVITY_NAME");
      f.setFunction("DECODE(sign(length(Activity_API.get_description(:ACTIVITY_SEQ))-35),1,Activity_API.get_description(:ACTIVITY_SEQ)||'...',Activity_API.get_description(:ACTIVITY_SEQ))");

      addField(blk, "ACTIVITY_SEQ", "Number");

      f = addField(blk, "ACTIVITY_EARLY_START", "Date");
      f.setFunction("Activity_API.Get_Early_Start(:ACTIVITY_SEQ)");

      f = addField(blk, "ACTIVITY_EARLY_FINISH", "Date");
      f.setFunction("Activity_API.Get_Early_Finish(:ACTIVITY_SEQ)");

      addField(blk, "BCWP", "Number", "#########0.##");

      addField(blk, "ACWP", "Number", "#########0.##");

      addField(blk, "BCWS", "Number", "#########0.##");

      addField(blk, "CV", "Number", "#########0.##");

      addField(blk, "SV", "Number", "#########0.##");

      addField(blk, "CPI,", "Number", "#########0.##");

      addField(blk, "SPI", "Number", "#########0.##");

      f = addField(blk, "ACTIVITY_RESPONSIBLE", "String");
      f.setFunction("Activity_API.Get_Activity_Responsible(:ACTIVITY_SEQ)");

      f = addField(blk, "SUB_PROJECT_NAME", "String");
      f.setFunction("SUB_PROJECT_API.Get_Description(:PROJECT_ID,:SUB_PROJECT_ID)");

      blk.setView("ACTIVITY_CALCULATION");
      rowset = blk.getASPRowSet();
      tbl = newASPTable(blk);
      tbl.setStripedBackground();
      bar= mgr.newASPCommandBar(blk);

      blk2 = newASPBlock("CPI");
      addField(blk2, "PCT_RETURN", "Number");
      addField(blk2, "CPI_PROJECT_ID");
      addField(blk2, "CPI_SUB_PROJECT_ID");
      addField(blk2, "ATTR");
      addField(blk2, "RED_LIMIT", "Number");
      addField(blk2, "GREEN_LIMIT", "Number");

      appendJavaScript("function customizeProjPerf(obj,id)"+
                       "{"+
                       "   customizeBox(id);"+
                       "}\n"+
                       "function prevProjPerf(obj,id)"+
                       "{"+
                       "   getPortletField(id,'CMD').value = 'PREV';"+
                       "}\n"+
                       "function nextProjPerf(obj,id)"+
                       "{"+
                       "   getPortletField(id,'CMD').value = 'NEXT';"+
                       "}\n"+
                       "function findProjPerf(obj,id)"+
                       "{"+
                       "   getPortletField(id,'CMD').value = 'FIND';"+
                       "}\n"+
                       "function selSubProjProjPerf(obj,id)"+
                       "{"+
                       "   getPortletField(id,'CMD').value = 'CNGSUBPROJ';"+
                       "   submitPortlet(id);"+
                       "}\n"+
                       "function customizeProjPerf(obj,id)"+
                       "{"+
                       "   customizeBox(id);"+
                       "}\n");
      init();
   }


   protected void init() throws FndException
   {
      super.init();

      green_limit = getASPField("GREEN_LIMIT").parseNumber(readProfileValue("GREEN", getASPField("GREEN_LIMIT").formatNumber(1.0)));
      red_limit = getASPField("RED_LIMIT").parseNumber(readProfileValue("RED", getASPField("RED_LIMIT").formatNumber(0.8)));
      portlet_title = readProfileValue("PORTLET_TITLE",translate(getDescription()));
      rbsel = readProfileValue("RBSEL","SPI");
      showOnly = readProfileValue("SHOW_ONLY","1");
      project_id = readProfileValue("PROJECT_ID");
      sub_project_id = readProfileValue("SUB_PROJECT_ID");

      critical_path = readProfileFlag("CRITICAL_PATH",false);

      detail_type = readProfileFlag("DETAIL_TYPE",false);
      detail_responsible = readProfileFlag("DETAIL_RESPONSIBLE",false);
      detail_start = readProfileFlag("DETAIL_START",false);
      detail_finish = readProfileFlag("DETAIL_FINISH",false);
      detail_bcws = readProfileFlag("DETAIL_BCWS",false);
      detail_bcwp = readProfileFlag("DETAIL_BCWP",false);
      detail_acwp = readProfileFlag("DETAIL_ACWP",false);
      detail_cv = readProfileFlag("DETAIL_CV",false);
      detail_sv = readProfileFlag("DETAIL_SV",false);
      detail_cpi = readProfileFlag("DETAIL_CPI",true);
      detail_cpi_ind = readProfileFlag("DETAIL_CPI_IND",true);
      detail_spi = readProfileFlag("DETAIL_SPI",true);
      detail_spi_ind = readProfileFlag("DETAIL_SPI_IND",true);
      display_search_ = readProfileFlag ("PRF_DISPSRCH" , false );
      autosearch = readProfileFlag("PRF_AUTOSEARCH", true);
      find = autosearch;
      max_row_ = Str.toInt(readProfileValue ( "PRF_MAXROW", size+"" ));
      //Bug Id 89400, Start
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );
      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      //Bug Id 89400, End
      if ( Str.isEmpty(project_id) )
         project_id = readValue("TEMP_PROJECT");

      String cmd = readValue("CMD");

      if ( "CNGSUBPROJ".equals(cmd) )
      {
         skip_rows = 0;
         find = true;
         sub_project_id = readValue("TEMP_SUB_PROJECT");
      }
      else if ( "FIND".equals(cmd) )
      {
         skip_rows = 0;
         find = true;
      }
      else if ( "PREV".equals(cmd) && skip_rows>=max_row_ )
      {
         skip_rows -= max_row_;
         find = true;
      }
      else if ( "NEXT".equals(cmd) && skip_rows<=db_rows-max_row_ )
      {
         skip_rows += max_row_;
         find = true;
      }
      //Bug Id 89400, Start
      ctx.writeValue("SKIP_ROWS",skip_rows+"");
      //Bug Id 89400, End
   }

   protected void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      String view_name = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Current"+ProfileUtils.ENTRY_SEP+"Page Node", false);
      project_id = getASPManager().getPortalPage().readGlobalProfileValue("PortalViews/Available/"+view_name+ProfileUtils.ENTRY_SEP+"Page Node"+"/PROJID", false);

      if (!(mgr.isEmpty(project_id)))
      {
         projectportal = true;
         ctx.writeFlag("PROJECTPORTAL", projectportal);
         ctx.writeValue("PROJECT_ID", project_id);
      }
      else
      {
         project_id = readProfileValue("PROJECT_ID");
         projectportal = false;
         ctx.writeFlag("PROJECTPORTAL", projectportal);
      }


      if (mgr.isEmpty(project_id))
         sub_project_id = readProfileValue("SUB_PROJECT_ID");
      ASPCommand cmd = getASPManager().newASPCommand();

      // After call 128802 Weighted Type will always be "PLAIN"
      weighted_type = "PLAIN";
      if (find)
      {
         if (critical_path)
         {
            String critical = "1";
            if (Str.like(rbsel,"SPI"))
            {
               cmd.defineCustomFunction(this, "Sub_Project_API.get_spi_performance", "PCT_RETURN");
               cmd.addParameter(this, "PROJECT_ID", project_id);
               cmd.addParameter(this, "SUB_PROJECT_ID", sub_project_id);
               cmd.addParameter(this, "ATTR", "WEIGHTED_TYPE"+'\u001f'+weighted_type+'\u001e'+"CRITICAL_PATH"+'\u001f'+critical+'\u001e');
               trans.addCommand("RETURN", cmd);
            }
            else if (Str.like(rbsel,"CPI"))
            {
               cmd.defineCustomFunction(this, "Sub_Project_API.get_cpi_performance", "PCT_RETURN");
               cmd.addParameter(this, "PROJECT_ID", project_id);
               cmd.addParameter(this, "SUB_PROJECT_ID", sub_project_id);
               cmd.addParameter(this, "ATTR", "WEIGHTED_TYPE"+'\u001f'+weighted_type+'\u001e'+"CRITICAL_PATH"+'\u001f'+critical+'\u001e');
               trans.addCommand("RETURN", cmd);
            }
         }
         else
         {

            if (Str.like(rbsel,"SPI"))
            {
               cmd.defineCustomFunction(this, "Sub_Project_API.get_spi_performance", "PCT_RETURN");
               cmd.addParameter(this, "PROJECT_ID", project_id);
               cmd.addParameter(this, "SUB_PROJECT_ID", sub_project_id);
               cmd.addParameter(this, "ATTR", "WEIGHTED_TYPE" + IfsNames.fieldSeparator + weighted_type+ IfsNames.recordSeparator);
               trans.addCommand("RETURN", cmd);
            }
            else if (Str.like(rbsel,"CPI"))
            {
               cmd.defineCustomFunction(this, "Sub_Project_API.get_cpi_performance", "PCT_RETURN");
               cmd.addParameter(this, "PROJECT_ID", project_id);
               cmd.addParameter(this, "SUB_PROJECT_ID", sub_project_id);
               cmd.addParameter(this, "ATTR", "WEIGHTED_TYPE"+ IfsNames.fieldSeparator +weighted_type+ IfsNames.recordSeparator);
               trans.addCommand("RETURN", cmd);
            }

         }

         trans = submit(trans);

         return_pct = trans.getBuffer("RETURN/DATA").getNumberValue("PCT_RETURN");

         Double temp = (new Double(return_pct));
         if (temp.isNaN())
         {
            spi_val = "";
         }
         else
         {
            spi_val = String.valueOf(temp);
         }
      }
   }

   //==========================================================================
   //
   //==========================================================================

   public String getTitle( int mode )
   {
      return getCustomizedTitle();
   }

   public void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      printHiddenField("CMD","");

      if (Str.isEmpty(project_id))
      {
         printNewLine();
         printScriptLink("PROJWPORTLETSMYPROJECTPERFORMANCECUSTOMIZEMSG: Customize this portlet ", "customizeProjPerf");
         printNewLine();
      }
      else
      {
         if (display_search_ )
         {
            printNewLine();
            setFontStyle(BOLD);
            printText("PROJWPORTLETSMYPROJECTPERFORMANCESRCHCRI: Search Criteria:");
            endFont();
            printText("PROJWPORTLETSMYPROJECTPERFORMANCEDISPPROJECT: -Project: ");
            printText(project_id);
            printNewLine();
            printText("PROJWPORTLETSMYPROJECTPERFORMANCEDISPSUBPROJ: -Sub Project: ");
            printText(sub_project_id);
            printNewLine();
         }

         if (!autosearch)
         {
            printNewLine();
            printSubmitLink("PROJWPORTLETSMYPROJECTPERFORMANCEREFRESHMSG: Refresh this portlet","findProjPerf");
            printNewLine();
         }
         if (find)
         {
            String avg_mes="";

            if (Str.like(rbsel,"CPI"))
               avg_mes = translate("PROJWPORTLETSMYPROJECTPERFORMANCETOTCPI: Total CPI:");
            else if (Str.like(rbsel,"SPI"))
               avg_mes = translate("PROJWPORTLETSMYPROJECTPERFORMANCETOTSPI: Total SPI:");

            if (!detail_type)  //Details off
            {
               try
               {
                  printNewLine();
                  if (return_pct >= getGreenLimit())
                     printImage("projw/images/_curveGreen.gif");
                  else if (return_pct >= getRedLimit() && return_pct <= getGreenLimit())
                     printImage("projw/images/_curveYellow.gif");
                  else if (return_pct <= getRedLimit())
                     printImage("projw/images/_curveRed.gif");
                  printNewLine();
                  printNewLine();
                  printText("PROJWPORTLETSMYPROJECTPERFORMANCEPROJ1: Project:");
                  printSpaces(2);
                  printLink(project_id,"projw/ChildTree.page?PROJECT_ID="+project_id);
                  printNewLine();

                  printText(avg_mes);
                  printSpaces(2);
                  printText(spi_val+"");
                  printNewLine();
               }
               catch (NumberFormatException e)
               {
                  throw new FndException("PROJWPORTLETSMYPROJECTPERFORMANCEPERFPCTLIMIT: The percentage deviations must be integers. &1", "<BR>");
               }
            }
            else    //Details on
            {
               String cpi_spi_where = "";
               
               String cpi_spi_where_param = "";
               if (showOnly.equalsIgnoreCase("2"))
               {
                  if (Str.like(rbsel,"CPI"))
                  {
                      cpi_spi_where = "((CPI <= ?)";
                      cpi_spi_where_param = "" + getGreenLimit();
                  }
                  else
                  {
                      cpi_spi_where = "((SPI <= ?)";
                      cpi_spi_where_param = "" + getGreenLimit();
                  }

                  cpi_spi_where = cpi_spi_where + "or (spi is null))";
               }
               else if (showOnly.equalsIgnoreCase("3"))
               {
                  if (Str.like(rbsel,"CPI")) {
                      cpi_spi_where = "((CPI <= ?)";
                      cpi_spi_where_param = "" + getRedLimit();
                  }
                  else {
                      cpi_spi_where = "((SPI <= ?)";
                      cpi_spi_where_param = "" + getRedLimit();
                  }

                  cpi_spi_where = cpi_spi_where + "or (spi is null))";
               }
               
               trans.clear();
               ASPQuery qry  = trans.addQuery("SUBPROJLIST", "SUB_PROJECT",  "SUB_PROJECT_ID,SUB_PROJECT_ID||'  '||description","PROJECT_ID= ?", "SUB_PROJECT_ID");
               qry.addParameter(this,"PROJECT_ID",project_id);

               qry = trans.addQuery(blk);

               if ( Str.isEmpty(sub_project_id) )
               {
                  if (critical_path)
                  {
                     qry.addWhereCondition("PROJECT_ID = ? AND early_start < SYSDATE AND EARLY_START = LATE_START");
                     qry.addParameter(this,"PROJECT_ID",project_id);
                  }
                  else
                  {
                     qry.addWhereCondition("PROJECT_ID = ? AND early_start < SYSDATE");
                     qry.addParameter(this,"PROJECT_ID",project_id);
                  }
               }

               else
               {
                  // Modified WHERE condition (Now the Sub Project Id is checked after removing activity no from total_key_path column by using substr)
                  if (critical_path)
                  {
                     qry.addWhereCondition("PROJECT_ID = ? AND substr(total_key_path,0,length(total_key_path)-(length(activity_no)+1)) LIKE ? AND early_start < SYSDATE AND EARLY_START = LATE_START");
                     qry.addParameter(this,"PROJECT_ID",project_id);
                     qry.addParameter(this,"SUB_PROJECT_ID","%^"+sub_project_id+"^%");

                  }
                  else
                  {
                     qry.addWhereCondition("PROJECT_ID = ? AND substr(total_key_path,0,length(total_key_path)-(length(activity_no)+1)) LIKE ? AND early_start < SYSDATE");
                     qry.addParameter(this,"PROJECT_ID",project_id);
                     qry.addParameter(this,"SUB_PROJECT_ID","%^"+sub_project_id+"^%");

                  }
               }

               if (!("".equals(cpi_spi_where)))
               {   
                   qry.addWhereCondition(cpi_spi_where);
                   qry.addParameter(this,"PROJECT_ID",cpi_spi_where_param);
                   
               }
               qry.setBufferSize(max_row_);
               qry.skipRows(skip_rows);
               qry.includeMeta("ALL");
               trans = submit(trans);
               db_rows = blk.getASPRowSet().countDbRows();
               //Bug Id 89400, Start
               getASPContext().writeValue("DB_ROWS", db_rows+"" );
               //Bug Id 89400, End
               String slider;
               if (return_pct >= getGreenLimit())
                  slider = "projw/images/_strGreen.gif";
               else if (return_pct >= getRedLimit() && return_pct <= getGreenLimit())
                  slider = "projw/images/_strYellow.gif";
               else
                  slider = "projw/images/_strRed.gif";

               beginTable();
               beginTableCell(true);

               printText("PROJWPORTLETSMYPROJECTPERFORMANCEPROJ2: Project:");
               printSpaces(2);
               // XSS_Safe DINHLK 20070808
               appendToHTML("<a href=projw/ChildTree.page?PROJECT_ID="+mgr.HTMLEncode(project_id)+">"+mgr.HTMLEncode(project_id)+"</a>");
               printNewLine();
               // XSS_Safe DINHLK 20070808
               appendToHTML(mgr.HTMLEncode(avg_mes)+" "+mgr.HTMLEncode(spi_val));
               printSpaces(13);
               printImage(slider);
               endTable();

               printNewLine();
               printText("PROJWPORTLETSMYPROJECTPERFORMANCESUBPROJ2: Parent Sub Project:");
               printSpaces(2);
               printSelectBox("TEMP_SUB_PROJECT", trans.getBuffer("SUBPROJLIST"), sub_project_id, "selSubProjProjPerf");
               printNewLine();
               printNewLine();

               beginTable();
               beginTableTitleRow();
               printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCESUBPROJ: Sub Project","CENTER");
               printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEACTIVITY: Activity","CENTER");
               if (detail_start)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCESTART: Start","CENTER");
               if (detail_finish)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEFINISH: Finish","CENTER");
               if (detail_bcwp)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEBCWP: BCWP","CENTER");
               if (Str.like(rbsel,"CPI") && detail_acwp)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEACWP: ACWP","CENTER");
               if (Str.like(rbsel,"SPI") && detail_bcws)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEBCWS: BCWS","CENTER");
               if (Str.like(rbsel,"CPI") && detail_cv)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCECV: CV","CENTER");
               if (Str.like(rbsel,"SPI") && detail_sv)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCESV: SV","CENTER");
               if (Str.like(rbsel,"CPI") && detail_cpi)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCECPI: CPI","CENTER");
               if (Str.like(rbsel,"SPI") && detail_spi)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCESPI: SPI","CENTER");
               if ((Str.like(rbsel,"SPI") && detail_spi_ind) || (Str.like(rbsel,"CPI") && detail_cpi_ind))
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEINDICATOR: Indicator","CENTER");
               if (detail_responsible)
                  printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCERESPONSIBLE: Responsible","CENTER");
               endTableTitleRow();
               beginTableBody();

               rowset.first();
               int no_of_rows = rowset.countRows();
               for (int i=0; i<no_of_rows; i++)
               {
                  double ind_val;

                  double dot_type;
                  String dot;
                  if (Str.like(rbsel,"CPI"))
                  {
                     if (mgr.isEmpty(rowset.getValue("CPI")))
                        dot_type = 0;
                     else
                        dot_type = rowset.getNumberValue("CPI");
                  }
                  else
                  {
                     if (mgr.isEmpty(rowset.getValue("SPI")))
                        dot_type = 0;
                     else
                        dot_type = rowset.getNumberValue("SPI");
                  }
                  if ( dot_type >= getGreenLimit())
                  {
                     if (DEBUG) debug("GREEN SHOW_ONLY = " + showOnly);
                     if (showOnly.equalsIgnoreCase("2") || showOnly.equalsIgnoreCase("3"))
                     {
                        db_rows = db_rows - 1;
                        rowset.next();
                        continue;
                     }
                     if (DEBUG) debug("CONTINUE");
                     dot = "projw/images/_greendot.gif";
                  }
                  else if (dot_type >= getRedLimit() && dot_type <= getGreenLimit())
                  {
                     if (DEBUG) debug("YELLOW SHOW_ONLY = " + showOnly);
                     if (showOnly.equalsIgnoreCase("3"))
                     {
                        db_rows = db_rows - 1;
                        rowset.next();
                        continue;
                     }
                     if (DEBUG) debug("CONTINUE");
                     dot = "projw/images/_yellowdot.gif";
                  }
                  else
                  {
                     if (DEBUG) debug("RED SHOW_ONLY = " + showOnly);
                     dot = "projw/images/_reddot.gif";
                  }

                  ASPBuffer this_row = rowset.getRow();
                  printTableCell(this_row.getFieldValue(this, "SUB_PROJECT_ID"),"LEFT");
                  beginTableCell("LEFT");
                  printLink(this_row.getFieldValue(this, "ACTIVITY_NAME"),"projw/Activity.page?ACTIVITY_SEQ="+this_row.getValue("ACTIVITY_SEQ"));    //Activity
                  endTableCell();
                  if (detail_start)
                     printTableCell(this_row.getFieldValue(this, "ACTIVITY_EARLY_START"),"LEFT");   //start
                  if (detail_finish)
                     printTableCell(this_row.getFieldValue(this, "ACTIVITY_EARLY_FINISH"),"LEFT");   //finish
                  if (detail_bcwp)
                     printTableCell(this_row.getFieldValue(this, "BCWP"),"RIGHT");  //bcwp
                  if (Str.like(rbsel,"SPI") && detail_bcws)
                     printTableCell(this_row.getFieldValue(this, "BCWS"),"RIGHT");  //bcws
                  if (Str.like(rbsel,"CPI") && detail_acwp)
                     printTableCell(this_row.getFieldValue(this, "ACWP"),"RIGHT");  //acwp
                  if ((Str.like(rbsel,"CPI") && detail_cv))
                     printTableCell(this_row.getFieldValue(this, "CV"),"RIGHT");  //cv
                  if ((Str.like(rbsel,"CPI") && detail_cpi))
                     printTableCell(this_row.getFieldValue(this, "CPI"),"RIGHT"); //cpi
                  if ((Str.like(rbsel,"SPI") && detail_sv))
                     printTableCell(this_row.getFieldValue(this, "SV"),"RIGHT"); //sv
                  if ((Str.like(rbsel,"SPI") && detail_spi))
                     printTableCell(this_row.getFieldValue(this, "SPI"),"RIGHT"); //spi
                  if ((Str.like(rbsel,"SPI") && detail_spi_ind) || (Str.like(rbsel,"CPI") && detail_cpi_ind))
                  {
                     beginTableCell("CENTER");
                     printImage(dot);                                     // Indicator
                     endTableCell();
                  }
                  if (detail_responsible)
                     printTableCell(this_row.getFieldValue(this, "ACTIVITY_RESPONSIBLE"),"LEFT");
                  nextTableRow();
                  rowset.next();
               }
               rowset.first();

               endTableBody();
               endTable();

               if (max_row_ < db_rows )
               {
                  printNewLine();
                  printSpaces(5);
                  if (skip_rows>0)
                     printSubmitLink("PROJWPORTLETSMYPROJECTPERFORMANCEMYTPRV: Previous","prevProjPerf");
                  else
                     printText("PROJWPORTLETSMYPROJECTPERFORMANCEMYTPRV: Previous");

                  printSpaces(5);
                  String rows = translate("PROJWPORTLETSMYPROJECTPERFORMANCEMYROWS: (Rows &1 to &2 of &3)",
                                          (skip_rows+1)+"",
                                          (skip_rows+max_row_<db_rows?skip_rows+max_row_:db_rows)+"",
                                          db_rows+"");
                  printText( rows );
                  printSpaces(5);

                  if (skip_rows<db_rows-max_row_)
                     printSubmitLink("PROJWPORTLETSMYPROJECTPERFORMANCEMYTNXT: Next","nextProjPerf");
                  else
                     printText("PROJWPORTLETSMYPROJECTPERFORMANCEMYTNXT: Next");

                  printNewLine();
                  printNewLine();
               }
            }
         }
      }
   }

   public boolean canCustomize()
   {
      return true;
   }

   public void runCustom()
   {
      projectportal = ctx.readFlag("PROJECTPORTAL", false);
      if (projectportal)
      {
         project_id = ctx.readValue("PROJECT_ID");
      }
   }

   public void printCustomBody()    throws FndException
   {
      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPQuery qry;

      if (!Str.isEmpty(readValue("TEMP_PROJECT")))
      {
         project_id = readValue("TEMP_PROJECT");
      }

      printNewLine();
      setFontStyle(BOLD);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCESEARCRI: Search Criteria");
      endFont();

      printText("PROJWPORTLETSMYPROJECTPERFORMANCEINFOMSG: Use these fields to define your search. Mandatory fields are marked with an asterisk.");
      printNewLine();
      printNewLine();

      appendToHTML("<table border=0 cellpadding=2 cellspacing=0 width=100%>");
      appendToHTML("<tr>");


      // Table Row 1 Cell 1
      appendToHTML("<td align=LEFT width=50%>");

      if (Str.isEmpty(project_id))
         printText("PROJWPORTLETSMYPROJECTPERFORMANCENOPROJ: No Project selected.");
      else
      {
         printText("PROJWPORTLETSMYPROJECTPERFORMANCECURPROJ: Current Project:");
         printSpaces(1);
         printText(project_id+"  ");
      }

      if (!projectportal)
      {
         printNewLine();
         printNewLine();
         printText("PROJWPORTLETSMYPROJECTPERFORMANCECHOOSEPROJ: Choose your project:");
         printNewLine();
         printField("TEMP_PROJECT",project_id,15,"customizeProjPerf");
         printDynamicLOV("TEMP_PROJECT", "PROJECT","PROJWPORTLETSMYPROJECTPERFORMANCEPROJLOV: List of Projects");
         printNewLine();
      }

      if (!Str.isEmpty(project_id))
      {
         trans.clear();
         if (!(sub_project_id == null))
         {
            printText("PROJWPORTLETSMYPROJECTPERFORMANCECURSUBPROJ: Current Sub Project: ");
            printText(sub_project_id+"  ");
         }
         printNewLine();
         printNewLine();
         printText("PROJWPORTLETSMYPROJECTPERFORMANCECHSUBPROJ: Choose your Sub Project: ");
         printNewLine();
         printField("TEMP_SUB_PROJECT", sub_project_id, 10,50);
         printDynamicLOV("TEMP_SUB_PROJECT","SUB_PROJECT","PROJWPORTLETSMYPROJECTPERFORMANCESUBPROJLOV: List of Sub Projects","PROJECT_ID=\\'"+mgr.URLEncode(project_id)+"\\'");
         printNewLine();
         printNewLine();
      }

      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANCUSTDISPHEADER: Configure the SPI/CPI indicator:");
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERRED: Red ");
      printText("PROJWPORTLETSMYPROJECTPERFORMANCELESSTHAN: less than ");
      printField("RED_LIMIT", getASPField("RED_LIMIT").formatNumber(red_limit), 10);
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERYELLOW: Yellow ");
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERBETWEEN: Between");
      printText(" "+red_limit + " ");
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERAND: and ");
      printText(" "+green_limit);
      printNewLine();
      printNewLine();
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERGREEN: Green ");
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANAGERMORETHAN: More than ");
      printField("GREEN_LIMIT", getASPField("GREEN_LIMIT").formatNumber(green_limit), 10);
      printNewLine();
      printNewLine();

      endTableCell();

      beginTableCell(50);
      printRadioButton("RBSEL","CPI","PROJWPORTLETSMYPROJECTPERFORMANCESELCPISTYLE:  Display Cost Performance indicator", (Str.like(rbsel,"CPI")?true:false),"switchCPI");
      printNewLine();
      printRadioButton("RBSEL","SPI","PROJWPORTLETSMYPROJECTPERFORMANCESELSPISTYLE:  Display Schedule Performance indicator", (Str.like(rbsel,"SPI")?true:false),"switchSPI");
      printNewLine();
      printNewLine();

      printCheckBox("CRITVIEW", critical_path);
      printText("PROJWPERFORMDISPLAYCPAO: Display Critical Path Activities Only");
      printNewLine();

      printCheckBox("DETVIEW", detail_type,"switchSelection");
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMDISPLAYDET: Display Detail Information");
      printNewLine();
      printNewLine();

      String tag_ref = "div";
      String style_ref1 = "style=\"display:";
      String layer_ref = "";
      String style_ref = ".style.display";
      String disp_str = "block";
      String no_disp_str = "none";
      if (!mgr.isExplorer())
         no_disp_str = "block";

      appendToHTML("<"+tag_ref+" id=\"A"+addProviderPrefix()+"DISP1\" "+style_ref1+(detail_type?"block":no_disp_str)+"\">");
      printCheckBox("DETVIEWSTART", detail_start);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEDELSTART: Start");
      printNewLine();
      printCheckBox("DETVIEWFINISH", detail_finish);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFOMDELFIN: Finish");
      printNewLine();
      printCheckBox("DETVIEWBCWP", detail_bcwp);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMDET: Budgeted Cost of Work Performed (BCWP)");
      printNewLine();
      appendToHTML("</"+tag_ref+">");

      appendToHTML("<"+tag_ref+" id=\"A"+addProviderPrefix()+"DISPCPI\" "+style_ref1+((Str.like(rbsel,"CPI")&&detail_type)?"block":no_disp_str)+"\">\n");
      printCheckBox("DETVIEWACWP", detail_acwp);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMOORKPER: Actual Cost of Work Performed (ACWP)");
      printNewLine();
      printCheckBox("DETVIEWCV", detail_cv);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMVARIAN: Cost Variance (CV)");
      printNewLine();
      printCheckBox("DETVIEWCPI", detail_cpi);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMCPI: CPI");
      printNewLine();
      printCheckBox("DETVIEWCPIIND", detail_cpi_ind);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMCPIIND: CPI Indicator");
      printNewLine();
      appendToHTML("</"+tag_ref+">");

      appendToHTML("<"+tag_ref+" id=\"A"+addProviderPrefix()+"DISPSPI\" "+style_ref1+((Str.like(rbsel,"SPI")&&detail_type)?"block":no_disp_str)+"\">\n");
      printCheckBox("DETVIEWBCWS", detail_bcws);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMBUGCOST: Budget Cost of Work Scheduled (BCWS)");
      printNewLine();
      printCheckBox("DETVIEWSV", detail_sv);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMSV: Schedule Variance (SV)");
      printNewLine();
      printCheckBox("DETVIEWSPI", detail_spi);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMSPI: SPI");
      printNewLine();
      printCheckBox("DETVIEWSPIIND", detail_spi_ind);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEPERFORMSPIIND: SPI Indicator");
      printNewLine();
      appendToHTML("</"+tag_ref+">");
      appendToHTML("<"+tag_ref+" id=\"A"+addProviderPrefix()+"DISPRES\" "+style_ref1+(detail_type?"block":no_disp_str)+"\">\n");
      printCheckBox("DETVIEWRESPONSIBLE", detail_responsible);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEMANRESPONS: Responsible");
      appendToHTML("</"+tag_ref+">");

      printNewLine();
      if (Str.isEmpty(showOnly))
         showOnly = "1";
      appendToHTML("<"+tag_ref+" id=\"A"+addProviderPrefix()+"DISP2\" "+style_ref1+(detail_type?"block":no_disp_str)+"\">\n");
      printText("Show Activities: ");
      printNewLine();
      printRadioButton("TEMP_SHOW_ONLY","1","PROJWPORTLETSMYPROJECTPERFORMANCESHWALL: Show All", (Str.like(showOnly,"1")?true:false));
      printNewLine();
      printRadioButton("TEMP_SHOW_ONLY","2","PROJWPORTLETSMYPROJECTPERFORMANCESHWREDYEL: Red and Yellow", (Str.like(showOnly,"2")?true:false));
      printNewLine();
      printRadioButton("TEMP_SHOW_ONLY","3","PROJWPORTLETSMYPROJECTPERFORMANCESHWREDONLY: Only Red", (Str.like(showOnly,"3")?true:false));
      appendToHTML("</"+tag_ref+">");

      endTableCell();
      appendToHTML("</tr></table>");


      printNewLine();
      printNewLine();
      setFontStyle(BOLD);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCELAYOUT: Layout");
      endFont();

      printText("PROJWPORTLETSMYPROJECTPERFORMANCELAYINFMSG: Use these fields to determine how the information is displayed in your portlet.");
      printNewLine();
      printNewLine();

      beginTransparentTable();
      beginTableBody();
      printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEPORTNM: Portlet Name: ");
      beginTableCell();
      printField("TITLE", portlet_title, 40);
      endTableCell();
      nextTableRow();
      printTableCell("PROJWPORTLETSMYPROJECTPERFORMANCEMAXROW: Max rows to display: ");
      beginTableCell();
      printField("MAX_ROW", Str.intToString(max_row_,0),5);
      endTableCell();
      endTableBody();
      endTable();

      printNewLine();
      printNewLine();
      printCheckBox("DISPLAYSEARCH", display_search_ );
      printSpaces(3);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCESPSRCH: Display search criteria");
      printNewLine();
      printCheckBox("AUTOSEARCH", autosearch);
      printSpaces(3);
      printText("PROJWPORTLETSMYPROJECTPERFORMANCEAUTOS: Search automatically");

      appendDirtyJavaScript(
                           "function switchSelection(val,obj)\n"+
                           "{\n");
      if (mgr.isExplorer())
      {
         appendDirtyJavaScript(
                              "  if (document.form."+addProviderPrefix()+"DETVIEW.checked)"+
                              "  { \n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISP1"+style_ref+" = '"+disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISP2"+style_ref+" = '"+disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISPRES"+style_ref+" = '"+disp_str+"';\n"+
                              "    if (document.form."+addProviderPrefix()+"RBSEL[0].checked)"+
                              "    { \n"+
                              "       "+layer_ref+"A"+addProviderPrefix()+"DISPCPI"+style_ref+" = '"+disp_str+"';\n"+
                              "       "+layer_ref+"A"+addProviderPrefix()+"DISPSPI"+style_ref+" = '"+no_disp_str+"';"+
                              "    }\n"+
                              "    else\n"+
                              "    {\n"+
                              "       "+layer_ref+"A"+addProviderPrefix()+"DISPSPI"+style_ref+" = '"+disp_str+"';\n"+
                              "       "+layer_ref+"A"+addProviderPrefix()+"DISPCPI"+style_ref+" = '"+no_disp_str+"';"+
                              "    }\n"+
                              "  }\n "+
                              "  else\n"+
                              "  {\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISP1"+style_ref+" = '"+no_disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISP2"+style_ref+" = '"+no_disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISPCPI"+style_ref+"  = '"+no_disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISPRES"+style_ref+" = '"+no_disp_str+"';\n"+
                              "    "+layer_ref+"A"+addProviderPrefix()+"DISPSPI"+style_ref+" = '"+no_disp_str+"';"+
                              "  }\n");
      }
      appendDirtyJavaScript(
                           "}\n");

      appendDirtyJavaScript(
                           "function switchCPI(val,obj)\n"+
                           "{\n");
      if (mgr.isExplorer())
      {
         appendDirtyJavaScript(
                              " if (document.form."+addProviderPrefix()+"DETVIEW.checked)"+
                              "   { \n"+
                              "      "+layer_ref+"A"+addProviderPrefix()+"DISPCPI"+style_ref+" = '"+disp_str+"';\n"+
                              "      "+layer_ref+"A"+addProviderPrefix()+"DISPSPI"+style_ref+" = '"+no_disp_str+"';\n"+
                              "  }\n ");
      }
      appendDirtyJavaScript(
                           "}\n");

      appendDirtyJavaScript(
                           "function switchSPI(val,obj)\n"+
                           "{\n"+
                           " if (document.form."+addProviderPrefix()+"DETVIEW.checked)"+
                           "   { \n"+
                           "      "+layer_ref+"A"+addProviderPrefix()+"DISPSPI"+style_ref+" = '"+disp_str+"';\n"+
                           "      "+layer_ref+"A"+addProviderPrefix()+"DISPCPI"+style_ref+" = '"+no_disp_str+"';\n"+
                           "  }\n ");
      appendDirtyJavaScript(
                           "}\n");

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
      rbsel       = readValue("RBSEL");
      writeProfileValue("RBSEL"    , rbsel);

      detail_responsible = "TRUE".equals(readValue("DETVIEWRESPONSIBLE"));
      writeProfileFlag("DETAIL_RESPONSIBLE"    , detail_responsible);

      detail_start    = "TRUE".equals(readValue("DETVIEWSTART"));
      writeProfileFlag("DETAIL_START"    , detail_start);

      detail_finish    = "TRUE".equals(readValue("DETVIEWFINISH"));
      writeProfileFlag("DETAIL_FINISH"    , detail_finish);

      detail_bcwp    = "TRUE".equals(readValue("DETVIEWBCWP"));
      writeProfileFlag("DETAIL_BCWP"    , detail_bcwp);

      detail_bcws    = "TRUE".equals(readValue("DETVIEWBCWS"));
      writeProfileFlag("DETAIL_BCWS"    , detail_bcws);

      detail_acwp    = "TRUE".equals(readValue("DETVIEWACWP"));
      writeProfileFlag("DETAIL_ACWP"    , detail_acwp);

      autosearch      = "TRUE".equals(readValue("AUTOSEARCH"));
      writeProfileFlag("PRF_AUTOSEARCH"    , autosearch);

      display_search_ = "TRUE".equals(readValue("DISPLAYSEARCH"));
      writeProfileFlag("PRF_DISPSRCH"      , display_search_);

      max_row_ = Str.toInt(readValue("MAX_ROW"));
      writeProfileValue("PRF_MAXROW" , Str.intToString(max_row_,0));

      showOnly = readValue("TEMP_SHOW_ONLY");
      writeProfileValue("SHOW_ONLY", showOnly);
      if (DEBUG) debug("SHOW_ONLY = " + showOnly);

      if (Str.like(rbsel,"CPI"))
      {
         detail_cv    = "TRUE".equals(readValue("DETVIEWCV"));
         writeProfileFlag("DETAIL_CV"    , detail_cv);

         detail_cpi    = "TRUE".equals(readValue("DETVIEWCPI"));
         writeProfileFlag("DETAIL_CPI"    , detail_cpi);

         detail_cpi_ind    = "TRUE".equals(readValue("DETVIEWCPIIND"));
         writeProfileFlag("DETAIL_CPI_IND"    , detail_cpi_ind);
      }
      if (Str.like(rbsel,"SPI"))
      {
         detail_spi    = "TRUE".equals(readValue("DETVIEWSPI"));
         writeProfileFlag("DETAIL_SPI"    , detail_spi);

         detail_spi_ind    = "TRUE".equals(readValue("DETVIEWSPIIND"));
         writeProfileFlag("DETAIL_SPI_IND"    , detail_spi_ind);

         detail_sv    = "TRUE".equals(readValue("DETVIEWSV"));
         writeProfileFlag("DETAIL_SV"    , detail_sv);
      }

      critical_path  = "TRUE".equals(readValue("CRITVIEW"));

      detail_type    = "TRUE".equals(readValue("DETVIEW"));

      writeProfileFlag("CRITICAL_PATH"    , critical_path);

      writeProfileFlag("DETAIL_TYPE"    , detail_type);

      green_limit =  getASPField("GREEN_LIMIT").parseNumber(readValue ("GREEN_LIMIT"));
      if ( !Str.isEmpty(readValue ("GREEN_LIMIT")) )
      {
         writeProfileValue("GREEN", getASPField("GREEN_LIMIT").formatNumber(green_limit));
      }

      red_limit = getASPField("RED_LIMIT").parseNumber(readValue("RED_LIMIT"));
      if ( !Str.isEmpty(readValue("RED_LIMIT")) )
      {
         writeProfileValue("RED", getASPField("RED_LIMIT").formatNumber(red_limit));
      }

      if ( !Str.isEmpty(readValue("TEMP_PROJECT")) )
         writeProfileValue("PROJECT_ID", readAbsoluteValue("TEMP_PROJECT"));

      writeProfileValue("SUB_PROJECT_ID",readAbsoluteValue("TEMP_SUB_PROJECT"));

      portlet_title = readValue("TITLE");
      writeProfileValue("PORTLET_TITLE", readAbsoluteValue("TITLE"));
   }

   protected double getGreenLimit()
   {
      return green_limit;
   }

   protected double getRedLimit()
   {
      return red_limit;
   }

   protected String getMyTitle()
   {
      return portlet_title;
   }

   private String getCustomizedTitle()
   {
      return(Str.isEmpty(getMyTitle())?translate(getDescription()):getMyTitle());
   }

}