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
*  File        : ProjectInfoGantt.java 
*  History     :
*  ARWILK  2002-03-25  Created.
*  ARWILK  2007-06-30  Added buildAppletDataXML to transfer data to applet.
*  LAFOLK  2007-07-23  Added buildTranslations for translation support.
*  ARWILK  2007-06-30  Added logic support applet user profile saving and reading.
*  LAFOLK  2007-08-22  Added functionality for Property Dialog Translations.
*  ARWILK  2007-08-23  Added support for annotations.
*  ARWILK  2007-09-05  Renamed profile variable GANTT_USER_PROPERTIES to PROJW_PROJGANTT_USER_PROPERTIES. Added new translations to buildTranslations. Removed use of JDOM.jar.
*  ARWILK  2007-09-07  Changed predefine to support connected object population.
* -----------------------APP7.5 SP1-------------------------------------------
*  ILSOLK  2007-12-14  Bug Id 68773, Eliminated XSS.
*  ARWILK  2007-12-31  Bug 69760(Re-write for Oracle App SRV), Modified predefine, saveEntityChanges and buildAppletDataXML.
* ----------------------------------------------------------------------------
*/ 

package ifs.projw;  

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;     

//(-) Bug 69760, Start
//import de.netronic.bean.diagramcontrolpanel.NeDiagramControlPanel;  
//(-) Bug 69760, End
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ifs.projw.lib.*;

public class ProjectInfoGantt extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.projw.ProjectInfoGantt");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPTable headtbl;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;

   private ASPBlock subprojblk;
   private ASPRowSet subprojset;

   private ASPBlock activityblk;
   private ASPRowSet activityset;

   private ASPBlock linkblk;
   private ASPRowSet linkset;

   private ASPBlock dummyblk;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPHTMLFormatter fmt;

   private String project_id;
   private ProjGanttXMLManager xmlman;

   private final String PROJW_PROJGANTT_USER_PROPERTIES = "Applets/projw.projectganttapplet/USER_SETTINGS";

   //===============================================================
   // Construction 
   //===============================================================
   public ProjectInfoGantt(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();  

      trans = mgr.newASPTransactionBuffer(); 
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.commandLinkActivated())
         eval(mgr.commandLinkFunction());
      else if ("TRUE".equals(mgr.getQueryStringValue("FETCH_LICENSE_KEY")))
      {
         try
         {
            xmlman = new ProjGanttXMLManager(mgr);
            xmlman.sendLicenseKey();
         }
         catch (ParserConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
         catch (TransformerConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
      }
      else if ("TRUE".equals(mgr.getQueryStringValue("FETCH_VAL_FOR_APPLET")))
      {
         try
         {
            xmlman = new ProjGanttXMLManager(mgr);

            project_id = mgr.getQueryStringValue("PROJECT_ID");
            buildAppletDataXML(xmlman);

            if ("TRUE".equals(mgr.getQueryStringValue("FETCH_TRANSLATIONS")))
            {
               buildTranslations(xmlman);
            }

            if ("TRUE".equals(mgr.getQueryStringValue("FETCH_USER_PROFILE")))
            {
               xmlman.addUserProfileToOutput(readGlobalProfileValue(PROJW_PROJGANTT_USER_PROPERTIES));
            }

            xmlman.writeOutputToResponse();
         }
         catch (ParserConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
         catch (TransformerConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
      }
      else if (!mgr.isEmpty(mgr.readValue("JGANTT_XML_TO_SAVE")))
      {
         try
         {
            xmlman = new ProjGanttXMLManager(mgr);
            project_id = mgr.readValue("PROJECT_ID");
            //(-/+) Bug 69760, Start
            //saveEntityChanges(xmlman);
            saveEntityChanges();
            //(-/+) Bug 69760, End
            xmlman.writeOutputToResponse();
         }
         catch (ParserConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
         catch (TransformerConfigurationException ex)
         {
            //(-/+) Bug 69760, Start
            //mgr.responseWrite(ex.getMessage());
            mgr.responseWrite("ERROR: " + ex.getMessage());
            //(-/+) Bug 69760, End
            mgr.endResponse();
         }
      }
      else if (!mgr.isEmpty(mgr.readValue("JGANTT_PROFILE_TO_SAVE")))
      {
         saveProfileChanges();
         mgr.endResponse();
      }

      adjust();
   }

   //=============================================================================
   //  Command Bar Search Group functions
   //=============================================================================

   public void countFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");

      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear(); 
   }


   public void okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.submit(trans);
      eval(headset.syncItemSets());

      if (headset.countRows() == 0)
      {
         headset.clear();
         mgr.showAlert("PROJWPROJGANTTNODATA: No Data Found.");  
      }
      else if (headset.countRows() == 1)
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }
   }

   private void okFindSUBPROJBLK() throws Exception
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addEmptyQuery(subprojblk);
      q.includeMeta("ALL");
      q.addWhereCondition("PROJECT_ID = ?");
      q.addParameter("PROJECT_ID", headset.getRow().getValue("PROJECT_ID"));

      mgr.submitEx(trans);
   }

   public void okFindACTIVITYBLK() throws Exception
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addEmptyQuery(activityblk);
      q.includeMeta("ALL");
      //q.addWhereCondition("PROJECT_ID = ? AND ACTIVITY_NO IS NOT NULL");
      q.addWhereCondition("PROJECT_ID = ? AND TOTAL_KEY_PATH IS NOT NULL AND ACTIVITY_SEQ IS NOT NULL");
      q.addParameter("PROJECT_ID",headset.getRow().getValue("PROJECT_ID")); 

      mgr.submitEx(trans);
   }

   public void okFindLINKBLK() throws Exception
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addEmptyQuery(linkblk);
      q.includeMeta("ALL");
      q.addWhereCondition("PROJECT_ID = ?");
      q.addParameter("PROJECT_ID",headset.getRow().getValue("PROJECT_ID"));

      mgr.submitEx(trans);    
   }

   private void buildAppletDataXML(ProjGanttXMLManager xmlman)
   {
      ASPManager mgr = getASPManager();

      String jGanttDataXML = "";
      boolean error_occured = false;
      String error_message = "";

      try
      {
         trans.clear();
         ASPQuery q = trans.addQuery(headblk);
         q.includeMeta("ALL");
         q.addWhereCondition("PROJECT_ID = ?");
         q.addParameter("PROJECT_ID", project_id);
         mgr.submitEx(trans);

         if (headset.countRows() == 1)
         {
            okFindSUBPROJBLK();
            okFindACTIVITYBLK();
            okFindLINKBLK();

            //Entity Set : SUB_PROJECT
            xmlman.createSubProjectEntitySet(); 

            //Entity Set : ACTIVITY
            xmlman.createActivityEntitySet();

            //Entity Set : LINKS
            xmlman.createLinkEntitySet();    

            if (subprojset.countRows() > 0)
               xmlman.readSubProjectRowSet(subprojset);

            if (activityset.countRows() > 0)
               xmlman.readActivityRowSet(activityset);

            if (linkset.countRows() > 0)
               xmlman.readLinkRowSet(linkset);

            //(-) Bug 69760, Start
            //jGanttDataXML = xmlman.getGanttData();   
            //xmlman.addDataToOutput(jGanttDataXML);
            //(-) Bug 69760, End
         }
      }
      catch (Exception ex)
      {
         xmlman.addErrorToOutput(ex);
      }
   }

   //(-/+) Bug 69760, Start
   //public void saveEntityChanges(ProjGanttXMLManager xmlman)   
   public void saveEntityChanges()   
   //(-/+) Bug 69760, End
   {
      ASPManager mgr = getASPManager(); 

      try
      {
         String ganttChangesAsXML = mgr.readValue("JGANTT_XML_TO_SAVE").trim();

         xmlman.setGanttData(ganttChangesAsXML);
         //(-/+) Bug 69760, Start
         //xmlman.setRemovedRecordSet(mgr.readValue("REMOVED_RECORDSET").trim());
         xmlman.commitData();  
         xmlman = new ProjGanttXMLManager(mgr);
         //(-/+) Bug 69760, End

         buildAppletDataXML(xmlman);
      }
      catch (Exception ex)
      {
         xmlman.addErrorToOutput(ex);
      }
   }

   //public void saveProfileChanges(ProjGanttXMLManager xmlman)   
   public void saveProfileChanges()   
   {
      ASPManager mgr = getASPManager();

      String ganttProfileXML = mgr.readValue("JGANTT_PROFILE_TO_SAVE").trim();
      writeGlobalProfileValue(PROJW_PROJGANTT_USER_PROPERTIES, ganttProfileXML);
   }

   private void buildTranslations(ProjGanttXMLManager xmlman)
   {
      ASPManager mgr = getASPManager();

      // JGantt Button Translations
      xmlman.addButtonTranslationToOutput("NE_PAGE_FORMAT_BUTTON", "Layout Sheet(s)", mgr.translate("PROJWPROJGANTTLAYSHEET: Layout Sheet(s)"));
      xmlman.addButtonTranslationToOutput("NE_PREVIEW_BUTTON", "Print Preview", mgr.translate("PROJWPROJGANTTPRINTPREVIEW: Print Preview"));
      xmlman.addButtonTranslationToOutput("NE_PRINT_BUTTON", "Print Sheets(s)", mgr.translate("PROJWPROJGANTTPRINTSHEET: Print Sheets(s)"));
      xmlman.addButtonTranslationToOutput("NE_ZOOM_IN_BUTTON", "Zoom In", mgr.translate("PROJWPROJGANTTZOOMIN: Zoom In"));
      xmlman.addButtonTranslationToOutput("NE_ZOOM_OUT_BUTTON", "Zoom Out", mgr.translate("PROJWPROJGANTTZOOMOUT: Zoom Out"));
      xmlman.addButtonTranslationToOutput("NE_1TO1_BUTTON", "Word Size", mgr.translate("PROJWPROJGANTTWRDSIZE: Word Size"));
      xmlman.addButtonTranslationToOutput("NE_PANES_BUTTON", "Panes View", mgr.translate("PROJWPROJGANTTPANESVIEW: Panes View"));
      xmlman.addButtonTranslationToOutput("NE_DIAGRAM_BUTTON", "Diagram View", mgr.translate("PROJWPROJGANTTDIAGRAMVIEW: Diagram View"));
      xmlman.addButtonTranslationToOutput("NE_TITLE_BUTTON", "Title", mgr.translate("PROJWPROJGANTTNEBUTTONTITLE: Title"));
      xmlman.addButtonTranslationToOutput("NE_UPPER_TIMESCALE_BUTTON", "Upper Timescale", mgr.translate("PROJWPROJGANTTUPPTIMESCALE: Upper Timescale"));
      xmlman.addButtonTranslationToOutput("NE_LOWER_TIMESCALE_BUTTON", "Lower Timescale", mgr.translate("PROJWPROJGANTTLOWERTIEMSCALE: Lower Timescale"));
      xmlman.addButtonTranslationToOutput("NE_LEFT_ROW_HEADER_BUTTON", "Left Rowheader", mgr.translate("PROJWPROJGANTTLEFTROWHEAD: Left Rowheader"));
      xmlman.addButtonTranslationToOutput("NE_LEFT_TABLE_BUTTON", "Left Table", mgr.translate("PROJWPROJGANTTLEFTTABLE: Left Table"));
      // Custom Button Translations
      xmlman.addButtonTranslationToOutput("NE_USER_BUTTON0", "Save Change(s)", mgr.translate("PROJWPROJGANTTSAVECHANGES: Save Change(s)"));
      xmlman.addButtonTranslationToOutput("NE_USER_BUTTON1", "Cancel Change(s)", mgr.translate("PROJWPROJGANTTCANCELCHANGES: Cancel Change(s)"));

      // JGantt Menu Translations
      xmlman.addPopupMenuTranslationToOutput("setSelectMode", "Select mode", mgr.translate("PROJWPROJGANTTSELECTMODE: Select mode"));
      xmlman.addPopupMenuTranslationToOutput("setCreateMode", "Create mode", mgr.translate("PROJWPROJGANTTCREATEMODE: Create mode"));
      xmlman.addPopupMenuTranslationToOutput("collapseGroup", "Collapse Group", mgr.translate("PROJWPROJGANTTCOLLPGROUP: Collapse Group"));
      xmlman.addPopupMenuTranslationToOutput("expandGroup", "Expand Group", mgr.translate("PROJWPROJGANTTEXPANDGROUP: Expand Group"));
      xmlman.addPopupMenuTranslationToOutput("deleteNodesAndLinks", "Save Details...", mgr.translate("PROJWPROJGANTTDELETENODE: Delete"));

      // Custom Menu Translations
      xmlman.addPopupMenuTranslationToOutput("refreshAction", "Refresh", mgr.translate("PROJWPROJGANTTREFRESH: Refresh"));
      xmlman.addPopupMenuTranslationToOutput("saveAction", "Save Details", mgr.translate("PROJWPROJGANTTSAVEDET: Save Details"));
      xmlman.addPopupMenuTranslationToOutput("activityInfo", "Activity Info...", mgr.translate("PROJWPROJGANTTACTIVITYINFO: Activity Info..."));
      xmlman.addPopupMenuTranslationToOutput("activityException", "Activity Exceptions...", mgr.translate("PROJWPROJGANTTACTIVITYEXCEPTION: Activity Exceptions..."));
      xmlman.addPopupMenuTranslationToOutput("projectInfo", "Project Info...", mgr.translate("PROJWPROJGANTTPROJECTINFO: Project Info..."));
      xmlman.addPopupMenuTranslationToOutput("scheduleProject", "Schedule Project", mgr.translate("PROJWPROJGANTTSCHEDULEPROJECT: Schedule Project"));
      xmlman.addPopupMenuTranslationToOutput("refreshClientAndSchProj", "Refresh Client and Schedule Project", mgr.translate("PROJWPROJGANTTREFRESHNSCHED: Refresh Client and Schedule Project"));
      xmlman.addPopupMenuTranslationToOutput("dependancyDetails", "Dependency Details...", mgr.translate("PROJWPROJGANTTDEPENDENCYDETAILS: Dependency Details..."));   
      xmlman.addPopupMenuTranslationToOutput("connectedObject", "Object Details...", mgr.translate("PROJWPROJGANTTCONNOBJDETAILS: Object Details..."));   
      xmlman.addPopupMenuTranslationToOutput("ganttProperties", "Gantt Properties...", mgr.translate("PROJWPROJGANTTGANTTPROP: Gantt Properties..."));   

      // Property Dialog Translations
      xmlman.addPropertyTranslationToOutput("tpProperties", "Gantt Properties", mgr.translate("PROJWPROJGANTTPROPDLG: Gantt Properties"));
      xmlman.addPropertyTranslationToOutput("tbgeneral", "General", mgr.translate("PROJWPROJGANTTGENERAL: General"));
      xmlman.addPropertyTranslationToOutput("tbschedule", "Schedule", mgr.translate("PROJWPROJGANTTSCHEDULE: Schedule"));
      xmlman.addPropertyTranslationToOutput("gbIntervalOfTime", "Interval of time", mgr.translate("PROJWPROJGANTTINTERVALOFTIME: Interval of time"));
      xmlman.addPropertyTranslationToOutput("gbStartDate", "Start Date", mgr.translate("PROJWPROJGANTTSTARTDATE: Start Date"));
      xmlman.addPropertyTranslationToOutput("gbEndDate", "End Date", mgr.translate("PROJWPROJGANTTENDDATE: End Date"));
      xmlman.addPropertyTranslationToOutput("lblStartDate", "Start Date", mgr.translate("PROJWPROJGANTTLABELSTARTDATE: Start Date"));
      xmlman.addPropertyTranslationToOutput("lblEndDate", "End Date", mgr.translate("PROJWPROJGANTTLABELENDDATE: End Date"));
      xmlman.addPropertyTranslationToOutput("lblDuration", "Duration", mgr.translate("PROJWPROJGANTTLABELDURATION: Duration"));   
      xmlman.addPropertyTranslationToOutput("gbTimeScale", "Timescale", mgr.translate("PROJWPROJGANTTTIMESCALE: Timescale"));      
      xmlman.addPropertyTranslationToOutput("chkStartDate", "Start Date Today", mgr.translate("PROJWPROJGANTTSTARTDATETODAY: Start Date Today"));
      xmlman.addPropertyTranslationToOutput("rbHours", "Hours", mgr.translate("PROJWPROJGANTTHOURS: Hours"));
      xmlman.addPropertyTranslationToOutput("rbDays", "Days", mgr.translate("PROJWPROJGANTTDAYS: Days"));
      xmlman.addPropertyTranslationToOutput("rbWeeks", "Weeks", mgr.translate("PROJWPROJGANTTWEEKS: Weeks"));
      xmlman.addPropertyTranslationToOutput("rbMonths", "Months", mgr.translate("PROJWPROJGANTTMONTHS: Months"));
      xmlman.addPropertyTranslationToOutput("chkShowProject", "Show Whole Project", mgr.translate("PROJWPROJGANTTSHOWWHOLEPROJECT: Show Whole Project"));
      xmlman.addPropertyTranslationToOutput("rbTSCMinutes", "Minutes", mgr.translate("PROJWPROJGANTTTSCMINUTES: Minutes"));
      xmlman.addPropertyTranslationToOutput("rbTSCHours", "Hours", mgr.translate("PROJWPROJGANTTTSCHOURS: Hours"));   
      xmlman.addPropertyTranslationToOutput("rbTSCDays", "Days", mgr.translate("PROJWPROJGANTTTSCDAYS: Days"));      
      xmlman.addPropertyTranslationToOutput("rbTSCWeeks", "Weeks", mgr.translate("PROJWPROJGANTTTSCWEEKS: Weeks"));
      xmlman.addPropertyTranslationToOutput("rbTSCMonths", "Months", mgr.translate("PROJWPROJGANTTTSCMONTHS: Months"));
      xmlman.addPropertyTranslationToOutput("rbTSCQuarters", "Quarters", mgr.translate("PROJWPROJGANTTTSCQUARTERS: Quarters"));
      xmlman.addPropertyTranslationToOutput("rbTSCYears", "Years", mgr.translate("PROJWPROJGANTTTSCYEARS: Years"));
      xmlman.addPropertyTranslationToOutput("chkShowDeps", "Show Dependencies", mgr.translate("PROJWPROJGANTTSHOWDEPS: Show Dependencies"));
      xmlman.addPropertyTranslationToOutput("chkShowCriticalAct", "Show Critical Activities", mgr.translate("PROJWPROJGANTTSHOWCRITICALACT: Show Critical Activities"));
      xmlman.addPropertyTranslationToOutput("chkShowDesc", "Show Descriptions", mgr.translate("PROJWPROJGANTTSHOWDESC: Show Descriptions"));     
      xmlman.addPropertyTranslationToOutput("chkSaveToolbarSetting", "Save Toolbar Settings", mgr.translate("PROJWSAVETOOLSETTING: Save Toolbar Settings"));     
      xmlman.addPropertyTranslationToOutput("rbSchdStart", "Schedule By Start Date", mgr.translate("PROJWPROJGANTTSCHDSTART: Schedule By Start Date"));   
      xmlman.addPropertyTranslationToOutput("rbSchdEnd", "Schedule By End Date", mgr.translate("PROJWPROJGANTTSCHDEND: Schedule By End Date"));   
      xmlman.addPropertyTranslationToOutput("rbSchdBoth", "Schedule By Both [Start and End Dates]", mgr.translate("PROJWPROJGANTTSCHDBOTH: Schedule By Both [Start and End Dates]"));   
      xmlman.addPropertyTranslationToOutput("bOk", "OK", mgr.translate("PROJWPROJGANTTBUTTONOK: OK"));   
      xmlman.addPropertyTranslationToOutput("bCancel", "Cancel", mgr.translate("PROJWPROJGANTTBUTTONCANCEL: Cancel"));   
      xmlman.addPropertyTranslationToOutput("bApply", "Apply", mgr.translate("PROJWPROJGANTTBUTTONAPPLY: Apply"));    
      xmlman.addPropertyTranslationToOutput("bRestoreDefault", "Restore Defaults", mgr.translate("PROJWPROJGANTTBUTTONRESTDEF: Restore Defaults"));    
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      // <editor-fold defaultstate="collapsed" desc=" Head Block "> 
      headblk = mgr.newASPBlock("HEADBLK");

      headblk.addField("OBJID")
      .setHidden();

      headblk.addField("OBJVERSION")
      .setHidden();

      headblk.addField("PROJECT_ID")
      .setLabel("PROJWPROJGANTT_PROJ_ID: Project ID");

      headblk.addField("PROJECT_DESC")
      .setFunction("PROJECT_API.Get_Description(PROJECT_ID)")
      .setLabel("PROJWPROJGANTT_PROJ_DESC: Project Description");

      headblk.addField("STATE")
      .setLabel("PROJWPROJGANTT_PROJ_STATUS: Status");

      headblk.addField("COMPANY")
      .setLabel("PROJWPROJGANTT_PROJ_COMP: Company");

      headblk.addField("CURR_CODE")
      .setFunction("CURRENCY_CODE_API.Get_Currency_Code(COMPANY)")
      .setLabel("PROJWPROJGANTT_PROJ_CURRCODE: Currency Code");

      headblk.addField("EARNED_VALUE_METHOD")
      .setLabel("PROJWPROJGANTT_PROJ_EVM: Earned Value Method");

      headblk.addField("BASELINE_REVISION_NUMBER", "Number")
      .setLabel("PROJWPROJGANTT_PROJ_BASEREV: Baseline Revision");

      headblk.setView("PROJECT");
      headblk.defineCommand("PROJECT_API","");

      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout(); 
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      // <editor-fold defaultstate="collapsed" desc=" Sub Project Block "> 
      subprojblk = mgr.newASPBlock("SUBPROJBLK");

      subprojblk.addField("SUBPROJBLK_PROJECT_ID")
      .setDbName("PROJECT_ID")
      .setHidden();

      subprojblk.addField("SUBPROJBLK_SUB_PROJECT_ID")
      .setDbName("SUB_PROJECT_ID")
      .setHidden();

      subprojblk.addField("SUBPROJBLK_SUB_PROJECT_DESC")
      .setFunction("SUB_PROJECT_API.Get_Description(PROJECT_ID,SUB_PROJECT_ID)")
      .setHidden();

      subprojblk.addField("SUBPROJECT_ANNOTATION")
      .setFunction("SUB_PROJECT_ID || ' - ' || SUB_PROJECT_API.Get_Description(PROJECT_ID,SUB_PROJECT_ID)")
      .setHidden(); 

      subprojblk.addField("SUB_PROJ_EARLY_START", "Datetime")
      .setDbName("EARLY_START")
      .setHidden();

      subprojblk.addField("SUB_PROJ_EARLY_FINISH", "Datetime")
      .setDbName("EARLY_FINISH")
      .setHidden();

      subprojblk.setView("ACT_BASED_SUB_PROJ");
      subprojblk.defineCommand("ACTIVITY_API","");

      subprojset = subprojblk.getASPRowSet();

      // <editor-fold defaultstate="collapsed" desc=" Activity Block "> 

      activityblk = mgr.newASPBlock("ACTIVITYBLK");

      activityblk.addField("ACTIVITYBLK_OBJID")
      .setDbName("OBJID")
      .setHidden();

      activityblk.addField("ACTIVITYBLK_OBJVERSION")
      .setDbName("OBJVERSION")     
      .setHidden();

      activityblk.addField("ACTIVITY_SEQ", "Number")
      .setHidden();

      activityblk.addField("PROGRAM_ID")
      .setFunction("Project_API.Get_Program_Id(PROJECT_ID)")
      .setHidden();

      activityblk.addField("PROGRAM_DESCRIPTION")
      .setFunction("Project_Program_API.Get_Description(Project_API.Get_Company(PROJECT_ID), Project_API.Get_Program_Id(PROJECT_ID))")
      .setHidden();

      activityblk.addField("ACTIVITYBLK_PROJECT_ID") 
      .setDbName("PROJECT_ID")
      .setHidden();

      activityblk.addField("ACTIVITYBLK_SUB_PROJECT_ID")
      .setDbName("SUB_PROJECT_ID")
      .setHidden();

      activityblk.addField("ACTIVITYBLK_SUB_PROJECT_DESC")
      .setFunction("SUB_PROJECT_API.Get_Description(project_id,SUB_PROJECT_ID)")
      .setHidden();

      activityblk.addField("ACTIVITY_NO")
      .setHidden();

      //(-) Bug 69760, Start
      //activityblk.addField("ACTIVITY_NO2")
      //.setHidden(); 
      //(-) Bug 69760, End

      activityblk.addField("DESCRIPTION")
      .setHidden(); 

      //(-) Bug 69760, Start
      //activityblk.addField("ACTIVITY_ANNOTATION") 
      //.setFunction("ACTIVITY_NO || ' - ' || DESCRIPTION")
      //.setHidden(); 
      //(-) Bug 69760, End

      activityblk.addField("EARLY_START", "Datetime")
      .setHidden();

      activityblk.addField("EARLY_FINISH", "Datetime")
      .setHidden();

      activityblk.addField("LATE_START", "Datetime")
      .setHidden();

      activityblk.addField("LATE_FINISH", "Datetime") 
      .setHidden();

      activityblk.addField("MIN_EARLY_START_SUB_PROJ", "Datetime")
      .setFunction("Activity_API.Get_Min_Early_Start_Sub_Proj(PROJECT_ID, SUB_PROJECT_ID)")
      .setHidden(); 

      activityblk.addField("MIN_EARLY_FINISH_SUB_PROJ", "Datetime")
      .setFunction("Activity_API.Get_Max_Early_Finish_Sub_Proj(PROJECT_ID, SUB_PROJECT_ID)")
      .setHidden(); 

      activityblk.addField("PROGRESS_HOURS_WEIGHTED", "Number")
      .setHidden();

      activityblk.addField("TOTAL_DURATION_DAYS", "Number")
      .setHidden();

      activityblk.addField("ACTUAL_START", "Datetime")
      .setHidden();

      activityblk.addField("ACTUAL_FINISH", "Datetime")
      .setHidden();

      activityblk.addField("OBJECT_IDENTIFICATION")
      .setHidden();

      activityblk.addField("NEED_DATE", "Datetime")
      .setHidden();

      activityblk.addField("PROJ_LU_NAME_DB")
      .setHidden();

      activityblk.addField("KEYREF1")
      .setHidden();

      activityblk.addField("KEYREF2")
      .setHidden();

      activityblk.addField("KEYREF3")
      .setHidden();

      activityblk.addField("EXCEPTIONS_EXIST", "Number")
      .setFunction("Project_Connection_Util_API.Exceptions_Exist(ACTIVITY_SEQ)")
      .setHidden();

      activityblk.addField("HAS_CONNECTED_WO", "Number")
      .setFunction("Project_Connection_Util_API.Has_Connected_Wo(ACTIVITY_SEQ)")
      .setHidden();

      activityblk.addField("HAS_CONNECTED_SO", "Number")
      .setFunction("Project_Connection_Util_API.Has_Connected_So(ACTIVITY_SEQ)")
      .setHidden();

      activityblk.addField("HAS_CONNECTED_PO", "Number")
      .setFunction("Project_Connection_Util_API.Has_Connected_Po(ACTIVITY_SEQ)")
      .setHidden();

      activityblk.addField("HAS_CONNECTED_DOP", "Number")
      .setFunction("Project_Connection_Util_API.Has_Connected_Dop(ACTIVITY_SEQ)")
      .setHidden();   

      activityblk.addField("TOTAL_WORK_DAYS", "Number")
      .setHidden();

      activityblk.addField("FREE_FLOAT", "Number")
      .setHidden();

      activityblk.addField("TOTAL_FLOAT", "Number")
      .setHidden();

      activityblk.addField("ACT_MILESTONE")
      .setHidden();

      activityblk.addField("WO_START_DATE", "Datetime")
      .setHidden();

      activityblk.addField("WO_COMPLETION_DATE", "Datetime")
      .setHidden();

      activityblk.setView("ACTIVITY_GANTT");
      activityblk.defineCommand("ACTIVITY_API","New__,Modify__,Remove__");

      activityset = activityblk.getASPRowSet();

      // <editor-fold defaultstate="collapsed" desc=" Link Block "> 
      linkblk = mgr.newASPBlock("LINKBLK");

      linkblk.addField("LINKBLK_OBJID")
      .setDbName("OBJID")
      .setHidden();

      linkblk.addField("LINKBLK_OBJVERSION")
      .setDbName("OBJVERSION")
      .setHidden();

      linkblk.addField("LINKBLK_PROJECT_ID")
      .setDbName("PROJECT_ID")
      .setHidden();

      linkblk.addField("LINKBLK_SUB_PROJECT_ID")
      .setDbName("SUB_PROJECT_ID")
      .setHidden();

      linkblk.addField("LINKBLK_ACTIVITY_NO")
      .setDbName("ACTIVITY_NO")
      .setHidden();

      linkblk.addField("LINKBLK_ACTIVITY_SEQ", "Number") 
      .setDbName("ACTIVITY_SEQ")
      .setHidden();

      linkblk.addField("ACTIVITY_RELATION_SEQ", "Number") 
      .setHidden();

      linkblk.addField("RELATIONSHIP_DIRECTION_DB")
      .setHidden();

      linkblk.addField("DEPENDENCY_TYPE_DB")
      .setHidden();

      linkblk.addField("LAG")
      .setHidden();

      linkblk.addField("LAG_TYPE")
      .setHidden();

      linkblk.setView("P3_ACTIVITY_DEPENDENCY_PROJ");
      linkblk.defineCommand("P3_ACTIVITY_DEPENDENCY_API","New__,Modify__,Remove__");

      linkset = linkblk.getASPRowSet();

      dummyblk = mgr.newASPBlock("BDUMMYBLK");

      dummyblk.addField("INFO")
      .setHidden();

      dummyblk.addField("ATTR")
      .setHidden();

      dummyblk.addField("ACTION")
      .setHidden();
   }

   public void adjust()
   {
   }

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "PROJWPROJGANTTTITLE: Project Gantt";
   }

   protected String getTitle()
   {
      return "PROJWPROJGANTTTITLE: Project Gantt"; 
   }

   protected void printContents() throws FndException
   { 
      ASPManager mgr = getASPManager();

      appendToHTML(headlay.show());

      appendToHTML(mgr.newASPHTMLFormatter().drawHidden("JGANTT_XML_TO_SAVE", ""));

      appendToHTML("<script language=javascript src=\"/b2e/secured/projw/scripts/ganttClientScript.js\"></script>");

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         appendToHTML("<table cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
         appendToHTML("   <tr>\n");
         appendToHTML("      <td>&nbsp;&nbsp;</td>\n");
         appendToHTML("      <td width=\"100%\">\n");  
         appendToHTML("<img src=\"/b2e/secured/projw/images/ProjExceptions.png\">");
         appendToHTML(" = ");
         appendToHTML(fmt.drawReadValue("PROJWPROJGANTTEXCEP: Exception"));
         appendToHTML(", ");
         appendToHTML("<img src=\"/b2e/secured/projw/images/ProjDOPConnObj.png\">");
         appendToHTML(" = ");
         appendToHTML(fmt.drawReadValue("PROJWPROJGANTTDOP: DOP Order"));
         appendToHTML(", ");
         appendToHTML("<img src=\"/b2e/secured/projw/images/ProjPOLConnObj.png\">");
         appendToHTML(" = ");
         appendToHTML(fmt.drawReadValue("PROJWPROJGANTTPO: Purchase Order"));
         appendToHTML(", ");
         appendToHTML("<img src=\"/b2e/secured/projw/images/ProjSOConnObj.png\">");
         appendToHTML(" = ");
         appendToHTML(fmt.drawReadValue("PROJWPROJGANTTSO: Shop Order"));
         appendToHTML(", ");
         appendToHTML("<img src=\"/b2e/secured/projw/images/ProjWOConnObj.png\">");
         appendToHTML(" = ");
         appendToHTML(fmt.drawReadValue("PROJWPROJGANTTWO: Work Order"));
         appendToHTML("      </td>\n");
         appendToHTML("      <td>&nbsp;&nbsp;</td>\n");
         appendToHTML("   </tr>\n");
         appendToHTML("</table>\n");    

         appendToHTML("<table cellpadding=\"0\" border=\"0\" width=\"100%\" height=\"75%\">\n");
         appendToHTML("   <tr>\n");
         appendToHTML("      <td>&nbsp;&nbsp;</td>\n");
         appendToHTML("      <td width=\"100%\">\n"); 
         appendToHTML("         <APPLET  CODE = \"ProjectGanttApplet.class\" ARCHIVE = \"/b2e/secured/projw/lib/projwApplets.jar, /b2e/secured/projw/lib/JGantt.jar, /b2e/secured/projw/lib/swing-layout-1.0.jar\" WIDTH = \"100%\" HEIGHT = \"100%\" NAME = \"ProjectGanttApplet\" MAYSCRIPT>\n");
         appendToHTML("            <PARAM NAME = \"BASE_PAGE\" VALUE = \"");
         appendToHTML(mgr.getApplicationAbsolutePath() + "/projw/ProjectInfoGantt.page");
         appendToHTML("\">\n"); 
         appendToHTML("            <PARAM NAME = \"APPL_ABSOLUTE_PATH\" VALUE = \"");
         appendToHTML(mgr.getApplicationAbsolutePath());
         appendToHTML("\">\n");
         appendToHTML("            <PARAM NAME = \"PAGE_ID\" VALUE = \"");
         appendToHTML(mgr.getPageId());
         appendToHTML("\">\n"); 
         appendToHTML("            <PARAM NAME = \"PROJECT_ID\" VALUE = \"");
         appendToHTML(mgr.HTMLEncode(headset.getRow().getValue("PROJECT_ID")));
         appendToHTML("\">\n"); 
         appendToHTML("         </APPLET>\n");
         appendToHTML("      </td>\n");
         appendToHTML("      <td>&nbsp;&nbsp;</td>\n");
         appendToHTML("   </tr>\n");
         appendToHTML("</table>\n"); 

         appendDirtyJavaScript("function refreshTheApplet()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   f.submit();\n");
         appendDirtyJavaScript("}\n");
      }
   }
}
