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
 *  File        : ScheduledTaskWizard.java
 *  Modified    :
 *    Ramila H    2004-05-18  - Created.
 *    Ramila H    2004-06-04  - Implemented PARAMETER Table functionality
 *    Ramila H    2004-06-09  - Imp customizable ParameterSheets
 *    Ramila H    2004-06-29  - Imp scheduled reports
 *    Ramila H    2004-07-12  - Imp. code for tree sort order.
 *                              added functionality for LANG_CODE.
 *    Ramila H    2004-07-16  - Imp future-proof solution for custom reports.
 *    Ramila H    2004-07-22  - Reimplemented Cust. parameter sheets to comply with custom reports.
 *    Ramila H    2004-08-23  - Added execution option 'Custom'.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2010/02/10 sumelk Bug 88916, Changed initReportSchedule() & getContents() to fetch the printer list from the database by executing a query.
 * 2008/08/12 sumelk Bug 76108, Added appendDateField() and changed getReportProperties(),drawTable() to show
 *                   the date/time picker and format mask for date fields.
 * 2007/11/01 sumelk Bug 68222, Modified getExecutionPlan() to adjust the interval hour as a two digit number. 
 * 2007/07/20 rahelk Bug id 66785, Validated hours (0-99) and mins (0-59) and weekly day(s) as in centura.
 * 2007/06/01 rahelk Bug id 65745, Changed language selectbox populating query.
 * 2007/05/23 rahelk call id 145164. Corrected error when modifying task advance property.
 * 2007/03/10 rahelk call id 143798, passed to_step var to getAttributeString javascript function.
 * 2007/02/23 rahelk Bug id 58590, placed form tag after startPresentation
 * 2007/02/16 rahelk Bug id 63594: Passed hardcoded number mask for NUMBER columns to remove client formatting
 * 2007/02/15 sadhlk
 * Bug 63229, Changed predifne() to suppport what's this functionality in child frames.
 *
 * 23/08/2006 rahelk
 * Bug 58944 - Changed EXECUTION_PLAN to EXEC_PLAN in getScheduleAttr method
 *
 * 2006/08/10 buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 * Revision x.x  2006/03/09 rahelk
 * Added possibility to pass parameter values from pages
 *
 * Revision 1.7  2005/12/06 rahelk
 * Fixed call id 129581. Added DATA_TYPE to okFind query
 *
 * Revision 1.6  2005/11/10 04:34:58  rahelk
 * Online report execution for web implemented
 *
 * Revision 1.5  2005/11/02 10:27:55  rahelk
 * fixed date mask bug
 *
 * Revision 1.4  2005/10/11 12:23:26  rahelk
 * Validated scheduled task params DATA_TYPEs
 *
 * Revision 1.3  2005/10/10 09:39:00  rahelk
 * Added checking parameter values using validation_method
 *
 * Revision 1.2  2005/09/22 12:39:23  japase
 * Merged in PKG 16 changes
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.9.2.1  2005/08/26 09:57:51  rahelk
 * Call ID 123807 fixed - Hadn't considered parameter_attr passed from custom report param page
 *
 * Revision 1.9  2005/08/19 07:05:23  rahelk
 * Bug fixes and added get_defaults from expression
 *
 * Revision 1.8  2005/08/17 10:53:28  rahelk
 * Removed submit() from previous button and task parameter sheet
 *
 * Revision 1.7  2005/08/08 09:44:06  rahelk
 * Declarative JAAS security restructure
 *
 * Revision 1.6  2005/07/07 08:14:09  rahelk
 * changed schedule task API in-params datatype from string to date/datetime
 *
 * Revision 1.5  2005/05/26 13:45:36  kirolk
 * Merged PKG14 changes to HEAD.
 *
 * Revision 1.4.2.1  2005/05/16 11:19:22  mapelk
 * Bug fix: 124179 and 124181
 *
 * Revision 1.4  2005/04/08 06:05:38  riralk
 * Merged changes made to PKG12 back to HEAD
 *
 * Revision 1.3  2005/02/08 08:12:55  rahelk
 * Merged Call id 121571. Reimplemented scheduled reports to be similar to general tasks.
 *
 * Revision 1.2  2005/02/02 08:22:19  riralk
 * Adapted global profile functionality to new profile changes
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import ifs.fnd.*;
import java.util.*;
import java.text.*;
import java.io.*;


public class ScheduledTaskWizard extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ScheduledTaskWizard");
   public static String DIALOG_HEIGHT = "270";
   public static String DIALOG_WIDTH = "545";
   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPInfoServices inf;
   private ASPContext ctx;
   private ASPBlock paramblk;
   private ASPRowSet paramset;
   private ASPTable paramtbl;
   private ASPCommandBar parambar;
   private ASPBlockLayout paramlay;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPBlockLayout lay;

   private ASPField schd_time_fld,schd_date_fld;
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;

   private int step_no;

   //execute report step variables
   private String execution;

   private String on_day;
   private String intv_hours;
   private String intv_mins;
   private String custom_exp;

   private String monday;
   private String tuesday;
   private String wednesday;
   private String thursday;
   private String friday;
   private String saturday;
   private String sunday;

   private boolean retrieved_params;
   private String default_script;

   private boolean scheduled_report;
   private boolean task_scheduled;
   private boolean modify_task;
   private String tree_type;
   private String sort_by;
   private String schedule_id;

   private ASPBuffer report_buffer;
   private String report_id;
   private String selected_report;
   private boolean skip_step0;
   private boolean skip_step1;
   private String user_where;
   private boolean new_report;
   private String report_id_list;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPTable itemtbl;
   private ASPCommandBar itembar;
   private ASPBlockLayout itemlay;
   private ASPBlock utilblk;
   private ASPField value_field;
   private Vector field_list;
   private String lov_script;

   private String event_executor;
   private String print_server;
   private String message_type;
   private String send_email_to;
   private String archivate_box;
   private String archivate;
   private String send_pdf;
   private String send_pdf_to;
   private String smtp_mail_address; // default email address

   private String printer;
   private String language;
   private String layout;
   private ASPBuffer printers;
   private String layouts;
   private String languages;
   private String layout_title;

   private boolean from_schedule_task;
   private String redirect_url;
   private String client_params;

   private String custom_attr;
   private String parameter_attr;
   private String custom_url;
   private String iframe_width;
   private String iframe_height;
   private boolean scheduled_by_fndweb = true;
   private String itemset_parameter_attr;

   //===============================================================
   // Construction
   //===============================================================
   public ScheduledTaskWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();

      //fmt = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();

      readFromContext();

      if (rowset.countRows() >0)
         rowset.changeRow();

      if("PREVIOUS".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         step_no--;
      }
      else if("NEXT".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         step_no++;
      }
      else if (!mgr.isEmpty(mgr.readValue("VALIDATE")))
      {
         validate();
      }
      else if ( mgr.buttonPressed("FINISH") )
      {
         if (!scheduled_report)
         {
            if (!mgr.isEmpty(custom_url))
               prepareParamRowset();

            if (!modify_task)
               newBatchSchedule();
            else
               modifyBatchSchedule();
         }
         else
         {
            if (!modify_task)
               newScheduleReport();
            else
               modifyScheduleReport();
         }
         task_scheduled = true;
      }
      else if("TASK_COMPLETED".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         //for scheduling reports through custom report methods. ex: Report generator report
         task_scheduled = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SCHEDULE_ID")))
      {
         okFind();
      }
      else
      {
         rowset.addRow(mgr.newASPBuffer());
         String method_name = mgr.getQueryStringValue("METHOD_NAME");

         if (!"Y".equals(mgr.getQueryStringValue("FROM_SCHED_TASK")))
         {
            // wizard called directly from navigator or a page. i.e NOT from ScheduleTask
            trans.addQuery("SINGLETASK","BATCH_SCHEDULE_METHOD","SCHEDULE_METHOD_ID, DESCRIPTION, METHOD_NAME, ARGUMENT_TYPE_DB, VALIDATION_METHOD","METHOD_NAME=?", null).
                    addParameter("METHOD_NAME", "S", "IN", method_name);
            trans = mgr.perform(trans);

            rowset.setValue("SCHEDULE_NAME", trans.getValue("SINGLETASK/DATA/DESCRIPTION"));
            rowset.setValue("SCHEDULE_METHOD_ID", trans.getValue("SINGLETASK/DATA/SCHEDULE_METHOD_ID"));
            rowset.setValue("ACTIVE_DB","TRUE");
            rowset.setValue("ARGUMENT_TYPE_DB", trans.getValue("SINGLETASK/DATA/ARGUMENT_TYPE_DB"));
            rowset.setValue("METHOD_NAME", method_name);
            rowset.setValue("VALIDATION_METHOD", trans.getValue("SINGLETASK/DATA/VALIDATION_METHOD"));

            if (mgr.isEmpty(mgr.getQueryStringValue("REDIRECT_URL")))
               redirect_url = mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR");
         }
         else
         {
            rowset.setValue("SCHEDULE_NAME", mgr.getQueryStringValue("SCHEDULE_NAME"));
            rowset.setValue("SCHEDULE_METHOD_ID", mgr.getQueryStringValue("SCHEDULE_METHOD_ID"));
            rowset.setValue("ACTIVE_DB","TRUE");
            rowset.setValue("ARGUMENT_TYPE_DB", mgr.getQueryStringValue("ARGUMENT_TYPE_DB"));
            rowset.setValue("METHOD_NAME", method_name);
            rowset.setValue("VALIDATION_METHOD", mgr.getQueryStringValue("VALIDATION_METHOD"));
         }

         String profile_language = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if (mgr.isEmpty(profile_language))
            rowset.setValue("LANG_CODE",mgr.getLanguageCode());
         else
            rowset.setValue("LANG_CODE",profile_language);

         custom_url = mgr.getConfigParameter(method_name+"/URL","");
      }


      if(!mgr.isEmpty(mgr.getQueryStringValue("REPORT_ID")))
      {
         report_id = mgr.getQueryStringValue("REPORT_ID");
         scheduled_report = true;
         custom_url = getCustomReportURL();
         user_where = "REPORT_ID='" + report_id + "'";

         getAvailableReports();
         rowset.clear();
         populateUserReport();
         new_report = true;
         skip_step0 = true;
         step_no = 1;

         if (mgr.isEmpty(custom_url))
            getReportProperties();
      }
      else if(!mgr.isEmpty(mgr.getQueryStringValue("REPORT_MODULE")))
      {
         scheduled_report = true;
         user_where = "MODULE='" + mgr.getQueryStringValue("REPORT_MODULE") + "'";
         getAvailableReports();
         rowset.clear();
         populateUserReport();
         new_report = true;
         step_no = 0;
      }
      else if (step_no == 0) //select report step
      {
         getAvailableReports();
         readStep1Values();
      }
      else if(step_no == 1) //report parameter step
      {
         readStep0Values();

         custom_url = getCustomReportURL();
         readStep2Values();

         if (mgr.isEmpty(custom_url))
            getReportProperties();
      }
      else if (step_no == 2) //execution step
      {
         readStep1Values();
         readStep3Values();
      }
      else if(step_no == 3)
      {
         readStep2Values();
         readStep4Values();
      }
      else if(step_no == 4)
      {
         if (!scheduled_report)
            initTaskParameters();
         else
            initReportSchedule();
         readStep3Values();
      }
      else if(step_no == 5)
      {
         readStep4Values();
      }

      writeToContext();
   }

//=============================================================================
//  Button functions
//=============================================================================

   private void readFromContext()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();

      scheduled_report = ctx.readFlag("SCHEDULED_TYPE", ScheduledTask.SCHED_REP_METHOD_NAME.equals(mgr.readValue("METHOD_NAME")));

      modify_task = ctx.readFlag("MODIFY_TASK", false);
      from_schedule_task = ctx.readFlag("FROM_SCHEDULE_TASK", ("Y".equals(mgr.getQueryStringValue("FROM_SCHED_TASK")) || !mgr.isEmpty(mgr.getQueryStringValue("TREE_TYPE"))) );
      tree_type = ctx.readValue("TREE_TYPE",mgr.getQueryStringValue("TREE_TYPE"));
      sort_by = ctx.readValue("SORT_BY",mgr.getQueryStringValue("SORT_BY"));
      user_where = ctx.readValue("USER_WHERE");

      redirect_url = ctx.readValue("REDIRECT_URL",mgr.getQueryStringValue("REDIRECT_URL"));
      client_params = ctx.readValue("CLIENT_PARAMS",mgr.getQueryStringValue("CLIENT_PARAMS"));

      schedule_id = ctx.readValue("SCHEDULE_ID","");

      step_no = ctx.readNumber("STEP_NO",!scheduled_report?2:0);
      skip_step0 = ctx.readFlag("SKIP_STEP0", !scheduled_report);
      skip_step1 = ctx.readFlag("SKIP_STEP1", !scheduled_report);

      //execution step
      execution = ctx.readValue("EXECUTION","SCHEDULED");
      monday = ctx.readValue("MONDAY","");
      tuesday = ctx.readValue("TUESDAY","");
      wednesday = ctx.readValue("WEDNESDAY","");
      thursday = ctx.readValue("THURSDAY","");
      friday = ctx.readValue("FRIDAY","");
      saturday = ctx.readValue("SATURDAY","");
      sunday = ctx.readValue("SUNDAY","");

      on_day = ctx.readValue("ON_DAY","1");
      intv_hours = ctx.readValue("INTV_HOURS","0");
      intv_mins = ctx.readValue("INTV_MINS","0");
      custom_exp = ctx.readValue("CUSTOM_EXP", "");

      retrieved_params = ctx.readFlag("RET_PARAMS",false);
      default_script = ctx.readValue("DEF_PARAMS","");

      //report stuff
      report_id = ctx.readValue("REPORT_ID","");
      selected_report = ctx.readValue("SELECTED_REPORT","");

      //when finished
      archivate = ctx.readValue("ARCHIVATE","");
      send_pdf = ctx.readValue("SEND_PDF");
      send_pdf_to = ctx.readValue("SEND_PDF_TO");
      message_type = ctx.readValue("MESSAGE_TYPE","NONE");
      send_email_to = ctx.readValue("SEND_EMAIL_TO","");
      event_executor = ctx.readValue("EVENT_EXECUTOR","NONE");
      print_server = ctx.readValue("PRINT_SERVER","OFF");
      printer = ctx.readValue("PRINTER","");
      layout = ctx.readValue("LAYOUT","");
      language = ctx.readValue("LANGUAGE","");

      custom_url = ctx.readValue("CUSTOM_URL","");
      custom_attr = ctx.readValue("CUSTOM_ATTR","");
      parameter_attr = ctx.readValue("PARAM_ATTR","");
      scheduled_by_fndweb = ctx.readFlag("USE_FNDWEB",true);
   }

   private void getAvailableReports()
   {
      ASPManager mgr = getASPManager();

      if(new_report)
         itemset.clear();

      trans.clear();
      cmd = trans.addCustomCommand("REPORT_TITLE","Report_Definition_API.Enumerate_Available_Reports");
      cmd.addParameter("CLIENT_VALUES0"); //report titles
      cmd.addParameter("CLIENT_VALUES1"); //report ids
      if (mgr.isEmpty(user_where))
         cmd.addParameter("ENUMUSERWHERE");
      else
         cmd.addParameter("ENUMUSERWHERE",user_where);

      trans = mgr.perform(trans);

      report_buffer = trans.getBuffer("REPORT_TITLE");
      report_id_list = trans.getValue("REPORT_TITLE/DATA/CLIENT_VALUES1");
   }

   private void populateUserReport()
   {
      ASPManager mgr = getASPManager();

      if (mgr.isEmpty(report_id_list))
      {
         mgr.showAlert("FNDSCHEDWIZARDNOREP: Requested Report not found");
         report_buffer = null;
      }
      else
      {
         String report_title =trans.getValue("REPORT_TITLE/DATA/CLIENT_VALUES0");
         report_title = (report_title.length()>1)?report_title.substring(0,report_title.length()-1):"";

         trans.clear();
         trans.addQuery("MAIN","BATCH_SCHEDULE_METHOD","'' SCHEDULE_NAME,SCHEDULE_METHOD_ID,'TRUE' ACTIVE_DB,ARGUMENT_TYPE_DB,METHOD_NAME,'' LANG_CODE", "upper(METHOD_NAME)=upper(?)","").
                 addParameter("METHOD_NAME", "S", "IN", ScheduledTask.SCHED_REP_METHOD_NAME);
         mgr.submit(trans);

         if (!mgr.isEmpty(report_id))
            rowset.setValue("SCHEDULE_NAME", report_title);
         else
            report_title = "";

         String profile_language = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if (mgr.isEmpty(profile_language))
            rowset.setValue("LANG_CODE",mgr.getLanguageCode());
         else
            rowset.setValue("LANG_CODE",profile_language);
      }

   }


   private void getReportProperties()
   {
      ASPManager mgr = getASPManager();
      String properties_list;
      String properties_item;
      String properties_ref;
      String properties_col;
      String properties_qvalue = "";
      String properties_enumerate;
      String properties_flags = "";
      String properties_datatype = "";

      if(new_report)
         itemset.clear();

      trans.clear();

      cmd = trans.addCustomCommand("PROPERTIES","REPORT_DEFINITION_API.Get_Query_Properties_");
      cmd.addParameter("PROPERTIES_LIST_OUT");
      cmd.addParameter("REPORT_ID",report_id);

      trans = mgr.perform(trans);
      properties_list = trans.getValue("PROPERTIES/DATA/PROPERTIES_LIST_OUT");
      trans.clear();

      lov_script ="";
      field_list = new Vector();
      String[] field_details;
      String view;
      String properties;
      HashMap propertiesmap = new HashMap();

      if (properties_list != null)
      {
         StringTokenizer st = new StringTokenizer(properties_list,""+IfsNames.groupSeparator);

         while (st.hasMoreTokens())
         {
            field_details = new String[8];
            properties_list = st.nextToken();
            propertiesmap.clear();
            
            // Fill HashMap with properties
            while (properties_list.length() > 0)
            {
               String name_value = properties_list.substring(0,properties_list.indexOf("^"));
               String name = name_value.substring(0,name_value.indexOf("="));
               String value = name_value.substring(name_value.indexOf("=")+1);

               propertiesmap.put(name, value);

               properties_list = properties_list.substring(properties_list.indexOf("^")+1, properties_list.length() );
            }

            properties_col = (String)propertiesmap.get("COLUMN_NAME"); 
            properties_item = (String)propertiesmap.get("QUERY");
            properties_flags = (String)propertiesmap.get("QFLAGS");
            properties_datatype = (String)propertiesmap.get("DATATYPE");

            field_details[0] = properties_col;   // field name
            field_details[1] = properties_item;  // field label also used as title for LOV
            field_details[6] = properties_flags;   // field flags
            field_details[7] = properties_datatype;   // field datatype

            properties_qvalue = (String)propertiesmap.get("QVALUE"); 

            if (!mgr.isEmpty(properties_qvalue))
            {
               field_details[4] = properties_qvalue; //qvalue
            }

            properties_ref = (String)propertiesmap.get("REF");

            if (!mgr.isEmpty(properties_ref))
            {
               if ( properties_ref.indexOf("(") == -1 )
               {
                  view = properties_ref;
               }
               else
               {
                  view = properties_ref.substring(0,properties_ref.indexOf("("));
                  properties = properties_ref.substring(properties_ref.indexOf("(")+1,properties_ref.indexOf(")"));
                  properties = properties.toUpperCase();

                  field_details[3] = properties; //view arguments
               }

               field_details[2] = view; //view name
            }

            properties_enumerate = (String)propertiesmap.get("ENUMERATE");            
            
            if (!mgr.isEmpty(properties_enumerate))
            {
               if ( properties_enumerate.indexOf("(") > -1 )
                  properties_enumerate = properties_enumerate.substring(0,properties_enumerate.indexOf("("));

               field_details[5] = mgr.replace(properties_enumerate,"..","."); //enumerate method
            }

            if(new_report)
            {
                itemset.addRow(mgr.newASPBuffer());
                itemset.setValue("ITEM_NAME", properties_item);
                itemset.setValue("VALUE", properties_qvalue);
                itemset.setValue("COLUMN_NAME", properties_col);
            }

            field_list.addElement(field_details);

            properties_qvalue = "";
         }
         itemset.first();

         lov_script = "function lovValue(i){ \n";
         lov_script +=" switch (i){ \n";

         for ( int i=0; i< field_list.size(); i++ )
         {
            field_details = (String[]) field_list.elementAt(i);
            if ( field_details[2] != null || field_details[5] != null)
               generateLOVScript( field_details[2],field_details[1],field_details[3],field_details[5],i+1, field_details[7]);
         }

         lov_script += "\n }\n";
         lov_script += "\n}\n";
      }
      else
      {
         //skip pages when report has no properties
         if("NEXT".equals(mgr.readValue("__PRESSED_BUTTON")) || skip_step0)
         {
            step_no++;
         }
         else
         {
            getAvailableReports();
            step_no--;
         }

         if (skip_step0) skip_step1 = true;

      }

   }


   void generateLOVScript(String view, String title, String properties, String enum_method, int row_no, String datatype)
   {
      lov_script += "\ncase "+row_no+" :";
      lov_script += "\n      "+getLOVScript(view,title,properties,enum_method,row_no, datatype );
      lov_script += "\n      break;";
   }


   private String getLOVScript(String view, String title, String properties, String enum_method, int row_no, String datatype)
   {
      String[] arguments;
      String field_script = "openLOVWindow('VALUE',i, \n";

      ASPManager mgr = getASPManager();

      if (title.lastIndexOf(":")>0)
         title = title.substring(0,title.lastIndexOf(":"));

      if (!mgr.isEmpty(view))
      {
         field_script += "      '"+mgr.getApplicationPath()+"/common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW="+view
                         +"&__FIELD=" + mgr.URLEncode(title )
                         +"&__INIT=1'";
      }
      else
      {
         field_script += "      '"+mgr.getApplicationPath()+"/common/scripts/DynamicIIDLov.page?IID_PACKAGE="+enum_method
                         +"&LABEL=" + mgr.URLEncode(title )
                         +"&__TYPE=SINGLE'";
      }

      if ("NUMBER".equalsIgnoreCase(datatype))
         field_script += "+'&__MASK='+URLClientEncode('#############')";

      try{
         arguments = split(properties,",");
         int field_no = 0;
         if ( arguments.length > 0 )
         {
            for ( int i=0; i<arguments.length; i++ )
            {
               for ( int j=0; j< field_list.size(); j++ )
                  if ( arguments[i].equalsIgnoreCase( ((String[])field_list.elementAt(j))[0]  ) )
                  {
                     field_no = j+1;
                     break;
                  }

               field_script += "\n      +'&"+arguments[i]+"=' + URLClientEncode(getValue_('VALUE',"+field_no+"))";
            }
         }
      }
      catch ( NullPointerException e )
      {
      }

      field_script += "\n      ,550,500,'validateValue'); ";

      return field_script;
   }

   private ASPBuffer getLangBuffer()
   {
      ASPManager mgr  = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      trans.addQuery("MAIN","language_code","LANG_CODE, DESCRIPTION","installed_db = 'TRUE'", "");
      trans = mgr.perform(trans);

      return trans.getBuffer("MAIN");
   }


   private String getCustomReportURL()
   {
      ASPManager mgr = getASPManager();
      scheduled_by_fndweb = true;

      // Report Generator Reports.
      boolean rep_gen_rep = false;
      String enable_rep_gen = "";
      if ("FINREP_REP".equals(report_id))
      {
         enable_rep_gen = mgr.getASPConfig().getParameter("FINANC/REPORT_GENERATOR_REPORTS/ENABLED","N");
         if ("Y".equals(enable_rep_gen))
         {
            iframe_width = mgr.getASPConfig().getParameter("FINANC/REPORT_GENERATOR_REPORTS/IFRAME_WIDTH","545");
            iframe_height = mgr.getASPConfig().getParameter("FINANC/REPORT_GENERATOR_REPORTS/IFRAME_HEIGHT","280");
            scheduled_by_fndweb = "Y".equals(mgr.getConfigParameter("FINANC/REPORT_GENERATOR_REPORTS/SCHEDULED_BY_FNDWEB","Y"));

            return mgr.getASPConfig().getParameter("FINANC/REPORT_GENERATOR_REPORTS/URL","");
         }
         else
            return "";
      }

      int rep_no=1;
      String cust_rep_id = mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");

      while(!"__BREAK".equals(cust_rep_id))
      {
         if (report_id.equals(cust_rep_id))
         {
            iframe_width = mgr.getASPConfig().getParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_WIDTH","545");
            iframe_height = mgr.getASPConfig().getParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_HEIGHT","280");
            scheduled_by_fndweb = "Y".equals(mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/SCHEDULED_BY_FNDWEB","Y"));

            return mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/URL","");
         }

         rep_no++;
         cust_rep_id = mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");
      }

      rep_no =1;
      cust_rep_id = mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");

      while(!"__BREAK".equals(cust_rep_id))
      {
         if (report_id.equals(cust_rep_id))
         {
            iframe_width = mgr.getASPConfig().getParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_WIDTH","545");
            iframe_height = mgr.getASPConfig().getParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_HEIGHT","280");
            scheduled_by_fndweb = "Y".equals(mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/SCHEDULED_BY_FNDWEB","Y"));

            return mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/URL","");
         }

         rep_no++;
         cust_rep_id = mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");
      }

      return "";
   }


   private void readStep0Values()
   {
      ASPManager mgr  = getASPManager();

      if (mgr.isEmpty(mgr.readValue("REPORT_ID"))) return;

      //if(report_id == null || !report_id.equals(mgr.isEmpty(mgr.readValue("REPORT_ID"))?"":mgr.readValue("REPORT_ID")))
      if(report_id == null || !report_id.equals(mgr.readValue("REPORT_ID")))
      {
         new_report = true;
         report_id = mgr.readValue("REPORT_ID");
      }
      else
         new_report = false;

      if (new_report)
      {
         selected_report = mgr.readValue("SELECTED_REPORT");
         rowset.setValue("SCHEDULE_NAME",mgr.readValue("REPORT_TITLE"));
      }
   }

   private void readStep1Values()
   {
      ASPManager mgr  = getASPManager();

      if (itemset.countRows() > 0)
         itemset.changeRows();

      if (mgr.isEmpty(mgr.readValue("REPORT_PARAM_ATTR"))) return;

      parameter_attr = mgr.readValue("REPORT_PARAM_ATTR");
      custom_attr = mgr.readValue("CUSTOM_ATTR");
   }


   private void readStep2Values()
   {
      ASPManager mgr  = getASPManager();

      if (mgr.isEmpty(mgr.readValue("EXECUTION"))) return;

      execution = mgr.readValue("EXECUTION");

   }

   private void readStep3Values()
   {
      ASPManager mgr  = getASPManager();

      if (!"3".equals(mgr.readValue("WIZARD_STEP_NO"))) return;

      if ("WEEKLY".equals(execution))
      {
         monday = mgr.readValue("cbMon");
         tuesday = mgr.readValue("cbTue");
         wednesday = mgr.readValue("cbWed");
         thursday = mgr.readValue("cbThu");
         friday = mgr.readValue("cbFri");
         saturday = mgr.readValue("cbSat");
         sunday = mgr.readValue("cbSun");
      }
      else if ("MONTHLY".equals(execution))
      {
         on_day = mgr.readValue("ON_DAY");
      }
      else if ("INTERVAL".equals(execution))
      {
         intv_hours = mgr.readValue("INTV_HOURS");
         intv_mins = mgr.readValue("INTV_MINS");
      }
      else if ("CUSTOM".equals(execution))
      {
         custom_exp = mgr.readValue("CUSTOM_EXP");
      }

   }

   private void readStep4Values() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      if (!"4".equals(mgr.readValue("WIZARD_STEP_NO"))) return;
      
      if (!scheduled_report)
      {
         if (mgr.isEmpty(custom_url))
            paramset.changeRows();
         else
         {
            if (mgr.isEmpty(mgr.readValue("PARAM_ATTR"))) return;

            parameter_attr = mgr.readValue("PARAM_ATTR");
            custom_attr = mgr.readValue("CUSTOM_ATTR");
         }
         
         //validate parameter values
         if (!mgr.isEmpty(rowset.getValue("VALIDATION_METHOD")))
         {
            Message message = new Message();

            if (mgr.isEmpty(custom_url))
            {
               int size = paramset.countRows();

               for (int i=0; i<size; i++)
                  message.addAttribute(paramset.getValueAt(i, "PARAM_NAME"),paramset.getValueAt(i, "PARAM_VALUE"));

            }
            else
            {
               String[] rows = split(parameter_attr, ""+IfsNames.recordSeparator);
               String[] item = null;

               int size = rows.length-1;

               for (int i=0; i<size; i++)
               {
                  item = split(rows[i], ""+IfsNames.fieldSeparator);
                  message.addAttribute(item[0], item[1]);
               }
            }

            try
            {
               cmd = trans.addCustomCommand("VAL_METH",rowset.getValue("VALIDATION_METHOD"));
               cmd.addInParameter("CLIENT_VALUES0", message.toString());
               
               mgr.performEx(trans);

            }
            catch(ASPLog.ExtendedAbortException e)
            {
               Buffer info = e.getExtendedInfo();
               String error_message = info.getString("ERROR_MESSAGE");
               mgr.showAlert(error_message);
               step_no = 4;
            }
         }
      }
      else
      {
         if (mgr.isEmpty(mgr.readValue("MESSAGE_TYPE"))) return;

         message_type = mgr.readValue("MESSAGE_TYPE");
         send_email_to = mgr.readValue("SEND_EMAIL_TO");

         printer = mgr.readValue("PRINTER");
         layout = mgr.readValue("LAYOUT");
         language = mgr.readValue("LANGUAGE");

         archivate = mgr.readValue("ARCHIVATE");
         send_pdf = mgr.readValue("SEND_PDF");
         if (mgr.isEmpty(send_pdf)) send_pdf = "FALSE";
         send_pdf_to = mgr.readValue("SEND_PDF_TO");
      }

   }

   private void initTaskParameters()
   {
      ASPManager mgr = getASPManager();

      if ("NONE".equals(rowset.getValue("ARGUMENT_TYPE_DB")))
      {
         retrieved_params = true;

         if("NEXT".equals(mgr.readValue("__PRESSED_BUTTON")))
            step_no++;
         else
            step_no--;
      }


      if (!retrieved_params && mgr.isEmpty(custom_url))
      {
         trans.clear();

         trans.addQuery("DEFAULT_PARAMS","select NAME PARAM_NAME, BATCH_SCHEDULE_METHOD_PAR_API.Get_Default_Value__( SCHEDULE_METHOD_ID, SEQ_NO ) PARAM_VALUE, MANDATORY_DB PARAM_MANDATORY, DATA_TYPE FROM BATCH_SCHEDULE_METHOD_PAR where SCHEDULE_METHOD_ID =? ORDER BY SEQ_NO").
                 addParameter("SCHEDULE_METHOD_ID", "N", "IN", rowset.getValue("SCHEDULE_METHOD_ID"));
         trans = mgr.perform(trans);

         retrieved_params = true;
         default_script = "";
         int j = 1; //0th one is the hidden field (old query level)
         HashMap client_param_map = new HashMap();

         if (!mgr.isEmpty(client_params))
         {
            StringTokenizer st = new StringTokenizer(client_params,ScheduledTask.CLIENT_PARAM_SEPARATOR );
            
            while (st.hasMoreTokens())
            {
               String name_value = st.nextToken();
               int index = name_value.indexOf('=');
               
               if (index != -1)
               {
                  String client_param_name  = name_value.substring(0,index);
                  String client_param_value = name_value.substring(index+1);
                  
                  client_param_map.put(client_param_name, client_param_value);
               }
            }
         }
         
         try
         {
            ASPBuffer buf = trans.getBuffer("DEFAULT_PARAMS");
            int buf_size = buf.countItems()-1;

            for (int i=0; i<buf_size; i++)
            {
               ASPBuffer def_buf = buf.getBufferAt(i);
               
               if (!modify_task)
               {
                  String client_param_value = (String)client_param_map.get(def_buf.getValue("PARAM_NAME"));

                  if ( !mgr.isEmpty(client_param_value))
                     def_buf.setValue("PARAM_VALUE", client_param_value);
                  
                  paramset.addRow(def_buf);
               }

               String def_value = def_buf.getValue("PARAM_VALUE");
               if (mgr.isEmpty(def_value)) def_value = "";

               default_script +="f.PARAM_VALUE["+(j++)+"].value='"+def_value+"';\n";
            }
            paramset.first();

         }
         catch (Exception e)
         {
            retrieved_params = false;
            if("NEXT".equals(mgr.readValue("__PRESSED_BUTTON")))
               step_no++;
            else
               step_no--;
         }
      }

   }

   private void initReportSchedule()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomFunction("EVENT_EXECUTOR", "Fnd_Setting_API.Get_Value","CLIENT_VALUES0");
      cmd.addParameter("CLIENT_VALUES1","EVENT_EXECUTOR");

      cmd = trans.addCustomFunction("PRINT_SERVER", "Fnd_Setting_API.Get_Value","CLIENT_VALUES0");
      cmd.addParameter("CLIENT_VALUES1","PRINT_SERVER");

      cmd = trans.addCustomCommand("ARCHIVATE", "Print_Server_SYS.Get_Archivate_Property__");
      cmd.addParameter("ARCHIVATE_");

      cmd = trans.addCustomFunction("SMPT_MAIL","Fnd_User_Property_API.Get_Value","SMTP_MAIL_ADDRESS");
      cmd.addInParameter("FND_USER",mgr.getUserId() );
      cmd.addInParameter("MAIL_PROPERTIES","SMTP_MAIL_ADDRESS");

      cmd = trans.addCustomCommand("GCLAS", "Report_Definition_API.Get_Class_Info");
      cmd.addParameter("REPORT_ATTR_");
      cmd.addParameter("COLUMN_PROPERTIES_");
      cmd.addParameter("TEXT_PROPERTIES_");
      cmd.addParameter("LAYOUT_PROPERTIES_");
      cmd.addParameter("LANG_PROPERTIES_");
      cmd.addParameter("REPORT_ID", report_id);

      trans.addQuery("SET_PRINTER", "SELECT printer_id,Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id FROM LOGICAL_PRINTER").setBufferSize(2000);

      trans = mgr.perform(trans);

      String archivate_value = trans.getValue("ARCHIVATE/DATA/ARCHIVATE_");
      archivate_box = getArchivateCheckBox(archivate_value);

      smtp_mail_address ="";
      smtp_mail_address = trans.getValue("SMPT_MAIL/DATA/SMTP_MAIL_ADDRESS");

      event_executor = trans.getValue("EVENT_EXECUTOR/DATA/CLIENT_VALUES0");
      print_server = trans.getValue("PRINT_SERVER/DATA/CLIENT_VALUES0");

      printers = trans.getBuffer("SET_PRINTER");
      languages = inf.getLanguageValues(trans.getValue("GCLAS/DATA/LANG_PROPERTIES_"));
      layouts = inf.getLayoutValues(trans.getValue("GCLAS/DATA/LAYOUT_PROPERTIES_"));


      layouts = inf.getOptions(inf.getTokenAt(layouts, 2), inf.getTokenAt(layouts, 1),layout, false);

      if (mgr.isEmpty(language))
      {
         language = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if (mgr.isEmpty(language))
            language = mgr.getLanguageCode();
      }
      languages = inf.getOptions(inf.getTokenAt(languages, 2), inf.getTokenAt(languages, 1),language, false);

   }

   // "ALWAYS ON"  - to show read only check box which is checked.
   // "OFF"        - to show read only check box which is not checked.
   // "DEFAULT ON" - to show check box which is default checked.
   // "DEFAULT OFF"- to show check box which is default unchecked.
   private String getArchivateCheckBox(String strArchivate)
   {
      ASPManager mgr = getASPManager();
      ASPHTMLFormatter fmt = mgr.newASPHTMLFormatter();

      if ( "ALWAYS ON".equals(strArchivate) ) // to show read only check box which is checked
      {
         archivate = "TRUE";
         return("&nbsp;<IMG SRC=\"../images/check_box_marked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      }
      else if ( "OFF".equals(strArchivate) )// to show read only check box which is not checked
      {
         archivate = "FALSE";
         return("&nbsp;<IMG SRC=\"../images/check_box_unmarked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      }
      else if ( "DEFAULT ON".equals(strArchivate) )// to show check box which is default checked
      {
         archivate = "TRUE";
         return fmt.drawCheckbox("ARCHCHKBOX","ON",true,"onClick='javascript:validateArchive()'");
      }
      else
      { // to show check box which is default unchecked
         archivate = "FALSE";
         return fmt.drawCheckbox("ARCHCHKBOX","ON",false,"onClick='javascript:validateArchive()'");
      }
   }



   private String getExecutionPlan()
   {
      ASPManager mgr = getASPManager();

      String exec_plan = "";
      String schd_time = rowset.getClientValue("SCHD_TIME");

      if ( "SCHEDULED".equals(execution) )
      {
         String schd_date = mgr.formatDate("SCHD_DATE", mgr.parseDate("SCHD_DATE", rowset.getValue("START_DATE")));
         exec_plan="ON "+schd_date+" AT "+schd_time;
      }
      else if ( "DAILY".equals(execution))
         exec_plan="DAILY AT "+schd_time;
      else if ( "WEEKLY".equals(execution) )
         exec_plan="WEEKLY ON "+ getRepeatedDays() +" AT "+schd_time;
      else if ( "MONTHLY".equals(execution) )
         exec_plan="MONTHLY ON DAY "+ on_day +" AT "+schd_time;
      else if ( "INTERVAL".equals(execution) )
         exec_plan="EVERY "+ (intv_hours.length()==1 ? "0"+intv_hours : intv_hours) +":"+intv_mins;
      else if ( "CUSTOM".equals(execution) )
         exec_plan=custom_exp;

      return exec_plan;
   }

   private void prepareParamRowset()
   {
      ASPManager mgr = getASPManager();

      if (paramset.countRows() > 0)
         paramset.clear();

      String[] rows = split(parameter_attr, ""+IfsNames.recordSeparator);
      String[] item = null;

      try
      {
         int size = rows.length-1;

         for (int i=0; i<size; i++)
         {
            item = split(rows[i], ""+IfsNames.fieldSeparator);
            paramset.addRow(mgr.newASPBuffer());
            paramset.setValue("PARAM_NAME", item[0]);
            paramset.setValue("PARAM_VALUE", item[1]);
         }
      }
      catch (Exception any)
      {
      }
   }

   private void newBatchSchedule()
   {
      ASPManager mgr = getASPManager();

      ASPInfoServices inf = mgr.newASPInfoServices();
      String schedule_id = inf.newScheduledTask(rowset.getValue("METHOD_NAME"),
                                                rowset.getValue("SCHEDULE_NAME"),
                                                rowset.getClientValue("NEXT_EXECUTION_DATE"),
                                                rowset.getClientValue("START_DATE"),
                                                rowset.getClientValue("STOP_DATE"),
                                                !mgr.isEmpty(mgr.readValue("ACTIVE"))?"TRUE":"FALSE",
                                                getExecutionPlan(),
                                                rowset.getValue("LANG_CODE"),
                                                "",
                                                paramset);

   }

   private void modifyBatchSchedule()
   {
      ASPManager mgr = getASPManager();

      ASPInfoServices inf = mgr.newASPInfoServices();

      inf.modifyScheduledTask(schedule_id,
                              rowset.getValue("SCHEDULE_NAME"),
                              rowset.getClientValue("NEXT_EXECUTION_DATE"),
                              rowset.getClientValue("START_DATE"),
                              rowset.getClientValue("STOP_DATE"),
                              !mgr.isEmpty(mgr.readValue("ACTIVE"))?"TRUE":"FALSE",
                              getExecutionPlan(),
                              rowset.getValue("LANG_CODE"),false,paramset);
   }


   private void newScheduleReport()
   {
      ASPManager mgr = getASPManager();

      ASPInfoServices inf = mgr.newASPInfoServices();

      if (paramset.countRows() > 0)
         paramset.clear();

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "REPORT_ATTR");
      paramset.setValue("PARAM_VALUE", getReportAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "PARAMETER_ATTR");
      paramset.setValue("PARAM_VALUE", getReportParameterAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "MESSAGE_ATTR");
      paramset.setValue("PARAM_VALUE", getMessageAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "ARCHIVING_ATTR");
      paramset.setValue("PARAM_VALUE", getArchiveAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "DISTRIBUTION_LIST");
      paramset.setValue("PARAM_VALUE", mgr.getFndUser()+ IfsNames.fieldSeparator );


      String schedule_id = inf.newScheduledTask(rowset.getValue("METHOD_NAME"),
                                                rowset.getValue("SCHEDULE_NAME"),
                                                rowset.getClientValue("NEXT_EXECUTION_DATE"),
                                                rowset.getClientValue("START_DATE"),
                                                rowset.getClientValue("STOP_DATE"),
                                                !mgr.isEmpty(mgr.readValue("ACTIVE"))?"TRUE":"FALSE",
                                                getExecutionPlan(),
                                                rowset.getValue("LANG_CODE"),
                                                "",
                                                report_id,
                                                paramset);
   }

   private String getReportAttr()
   {
      String report_attr = report_attr = inf.itemValueCreate("REPORT_ID", report_id);

      if ("PRINTER".equals(message_type))
         report_attr += inf.itemValueCreate("LAYOUT_NAME", layout)+
                        inf.itemValueCreate("LANG_CODE", language);

      return report_attr;
   }

   private String getScheduleAttr()
   {
      ASPManager mgr = getASPManager();
      String schedule_attr = "";

      schedule_attr = inf.itemValueCreate("SCHEDULE_NAME", rowset.getValue("SCHEDULE_NAME"))+
                      inf.itemValueCreate("SCHEDULE_METHOD_ID", rowset.getValue("SCHEDULE_METHOD_ID"))+
                      inf.itemValueCreate("ACTIVE_DB", !mgr.isEmpty(mgr.readValue("ACTIVE"))?"TRUE":"FALSE")+
                      inf.itemValueCreate("EXEC_PLAN", getExecutionPlan())+
                      inf.itemValueCreate("START_DATE", rowset.getValue("START_DATE"))+
                      inf.itemValueCreate("STOP_DATE", rowset.getValue("STOP_DATE"))+
                      inf.itemValueCreate("NEXT_EXECUTION_DATE", rowset.getValue("NEXT_EXECUTION_DATE"))+
                      inf.itemValueCreate("LANG_CODE", rowset.getValue("LANG_CODE"));

      return schedule_attr;
   }

   private String getMessageAttr()
   {
      String message_attr = inf.itemValueCreate("MESSAGE_TYPE", message_type);

      if ("PRINTER".equals(message_type))
         message_attr += inf.itemValueCreate("PRINTER_ID", printer);
      else if ("EMAIL".equals(message_type))
         message_attr += inf.itemValueCreate("SEND_EMAIL_TO", send_email_to);

      return message_attr;
   }

   private String getArchiveAttr()
   {
      String archiving_attr = "";
      archiving_attr = inf.itemValueCreate("PDF_ARCHIVING", archivate)+
                       inf.itemValueCreate("SEND_PDF", send_pdf)+
                       inf.itemValueCreate("SEND_PDF_TO", send_pdf_to);

      return archiving_attr;
   }

   private void modifyScheduleReport()
   {
      ASPManager mgr = getASPManager();

      ASPInfoServices inf = mgr.newASPInfoServices();
      if (paramset.countRows() > 0)
         paramset.clear();

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "REPORT_ATTR");
      paramset.setValue("PARAM_VALUE", getReportAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "PARAMETER_ATTR");
      paramset.setValue("PARAM_VALUE", getReportParameterAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "MESSAGE_ATTR");
      paramset.setValue("PARAM_VALUE", getMessageAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "ARCHIVING_ATTR");
      paramset.setValue("PARAM_VALUE", getArchiveAttr());

      paramset.addRow(mgr.newASPBuffer());
      paramset.setValue("PARAM_NAME", "DISTRIBUTION_LIST");
      paramset.setValue("PARAM_VALUE", mgr.getFndUser()+ IfsNames.fieldSeparator );

      inf.modifyScheduledTask(schedule_id,
                              rowset.getValue("SCHEDULE_NAME"),
                              rowset.getClientValue("NEXT_EXECUTION_DATE"),
                              rowset.getClientValue("START_DATE"),
                              rowset.getClientValue("STOP_DATE"),
                              !mgr.isEmpty(mgr.readValue("ACTIVE"))?"TRUE":"FALSE",
                              getExecutionPlan(),
                              rowset.getValue("LANG_CODE"),true,paramset);
   }

   private String getReportParameterAttr()
   {
      ASPManager mgr = getASPManager();
      int num;
      String paramStr = "";

      if (!mgr.isEmpty(custom_url))
      {
         return parameter_attr;
      }

      if ((num = itemset.countRows()) > 0)
      {
         itemset.first();
         for (int i = 0; i < num; i++)
         {
            paramStr += itemset.getValue("COLUMN_NAME");
            paramStr += (char)31;
            paramStr += (itemset.getValue("VALUE")==null? "":itemset.getValue("VALUE"));
            paramStr += (char)30;
            itemset.next();
         }
         itemset.clear();
      }
      else
         paramStr = itemset_parameter_attr; //used in modify

      return paramStr;
   }


   private void writeToContext()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();

      ctx.writeFlag("SCHEDULED_TYPE", scheduled_report);
      ctx.writeFlag("MODIFY_TASK", modify_task);
      ctx.writeFlag("FROM_SCHEDULE_TASK", from_schedule_task);
      ctx.writeValue("TREE_TYPE",tree_type);
      ctx.writeValue("SORT_BY",sort_by);
      ctx.writeValue("USER_WHERE",user_where);
      ctx.writeValue("REDIRECT_URL",redirect_url);
      ctx.writeValue("CLIENT_PARAMS",client_params);

      ctx.writeValue("SCHEDULE_ID", schedule_id);

      ctx.writeNumber("STEP_NO",step_no);
      ctx.writeFlag("SKIP_STEP0", skip_step0);
      ctx.writeFlag("SKIP_STEP1", skip_step1);

      //execution step
      ctx.writeValue("EXECUTION",execution);
      ctx.writeValue("MONDAY",monday);
      ctx.writeValue("TUESDAY",tuesday);
      ctx.writeValue("WEDNESDAY",wednesday);
      ctx.writeValue("THURSDAY",thursday);
      ctx.writeValue("FRIDAY",friday);
      ctx.writeValue("SATURDAY",saturday);
      ctx.writeValue("SUNDAY",sunday);

      ctx.writeValue("ON_DAY",on_day);
      ctx.writeValue("INTV_HOURS",intv_hours);
      ctx.writeValue("INTV_MINS",intv_mins);
      ctx.writeValue("CUSTOM_EXP", custom_exp);

      ctx.writeFlag("RET_PARAMS",retrieved_params);
      ctx.writeValue("DEF_PARAMS",default_script);

      //Report stuff
      ctx.writeValue("REPORT_ID",report_id);
      ctx.writeValue("SELECTED_REPORT",selected_report);

      //when finished
      ctx.writeValue("ARCHIVATE",archivate);
      ctx.writeValue("SEND_PDF",send_pdf);
      ctx.writeValue("SEND_PDF_TO",send_pdf_to);
      ctx.writeValue("MESSAGE_TYPE",message_type);
      ctx.writeValue("SEND_EMAIL_TO",send_email_to);

      ctx.writeValue("EVENT_EXECUTOR", event_executor);
      ctx.writeValue("PRINT_SERVER",print_server);
      ctx.writeValue("PRINTER",printer);
      ctx.writeValue("LAYOUT",layout);
      ctx.writeValue("LANGUAGE",language);

      ctx.writeValue("CUSTOM_URL",custom_url);
      ctx.writeFlag("USE_FNDWEB",scheduled_by_fndweb);
      ctx.writeValue("CUSTOM_ATTR",custom_attr);
      ctx.writeValue("PARAM_ATTR",parameter_attr);
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   private void  store()
   {
      //itemset.changeRows();
      //itemset.trace();
   }

   private void okFind()
   {
      ASPManager mgr = getASPManager();

      modify_task = true;
      skip_step1 = true; //avoid having "previous in step 2 for editing
      step_no = 2; //edit and default
      if ("SCHEDULE".equals(mgr.getQueryStringValue("STEP")))
         step_no = 3;
      else if ("OPTION".equals(mgr.getQueryStringValue("STEP")))
         step_no = 4;

      schedule_id = mgr.getQueryStringValue("SCHEDULE_ID");
      String method_name = mgr.getQueryStringValue("METHOD_NAME");
      String argument_type = mgr.getQueryStringValue("ARGUMENT_TYPE_DB");
      String validation_method = mgr.getQueryStringValue("VALIDATION_METHOD");

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      trans.addQuery("PARAM","select NAME PARAM_NAME, VALUE PARAM_VALUE, MANDATORY_DB PARAM_MANDATORY, BATCH_SCHEDULE_METHOD_PAR_API.Get_Data_Type( BATCH_SCHEDULE_API.Get_Schedule_Method_Id(SCHEDULE_ID), SEQ_NO ) DATA_TYPE, '' DATE_FIELD FROM BATCH_SCHEDULE_PAR where SCHEDULE_ID =? ORDER BY SEQ_NO").
              addParameter("SCHEDULE_ID", "N", "IN", schedule_id);
      
      q = trans.addQuery("MAIN","BATCH_SCHEDULE","SCHEDULE_NAME,SCHEDULE_METHOD_ID,ACTIVE_DB,EXECUTION_PLAN,START_DATE,STOP_DATE,NEXT_EXECUTION_DATE, ? ARGUMENT_TYPE_DB, ? METHOD_NAME, ? VALIDATION_METHOD, '' SCHD_TIME,LANG_CODE","SCHEDULE_ID=?","");
      q.addParameter("ARGUMENT_TYPE_DB", "S", "IN", argument_type);
      q.addParameter("METHOD_NAME", "S", "IN", method_name);
      q.addParameter("VALIDATION_METHOD", "S", "IN", validation_method);
      q.addParameter("SCHEDULE_ID", "N", "IN", schedule_id);
      
      mgr.submit(trans);

      String exec_plan = rowset.getValue("EXECUTION_PLAN");
      String schd_date = "";
      String schd_time = "";

      if (exec_plan.startsWith("ON"))
      {
         //"ON "+schd_date+" AT "+schd_time;
         execution = "SCHEDULED";
         int i = exec_plan.indexOf("ON ")+3;
         int j = exec_plan.indexOf(" AT ");
         schd_date = exec_plan.substring(i,j);
         schd_time = exec_plan.substring(j+4);
      }
      else if (exec_plan.startsWith("DAILY AT"))
      {
         //"DAILY AT "+schd_time;
         execution = "DAILY";
         int i = exec_plan.indexOf("AT ")+3;
         schd_time = exec_plan.substring(i);
      }
      else if (exec_plan.startsWith("WEEKLY ON"))
      {
         //"WEEKLY ON "+ getRepeatedDays() +" AT "+schd_time;
         execution = "WEEKLY";
         int j = exec_plan.indexOf("AT ");

         if (exec_plan.indexOf("mon") > 0)
            monday = "ON";
         if (exec_plan.indexOf("tue") > 0)
            tuesday = "ON";
         if (exec_plan.indexOf("wed") > 0)
            wednesday = "ON";
         if (exec_plan.indexOf("thu") > 0)
            thursday = "ON";
         if (exec_plan.indexOf("fri") > 0)
            friday = "ON";
         if (exec_plan.indexOf("sat") > 0)
            saturday = "ON";
         if (exec_plan.indexOf("sun") > 0)
            sunday = "ON";

         schd_time = exec_plan.substring(j+3);
      }
      else if (exec_plan.startsWith("MONTHLY ON DAY"))
      {
         //"MONTHLY ON DAY "+ on_day +" AT "+schd_time;
         execution = "MONTHLY";
         int i = exec_plan.indexOf("DAY ")+4;
         int j = exec_plan.indexOf(" AT ");
         on_day = exec_plan.substring(i,j);
         schd_time = exec_plan.substring(j+4);
      }
      else if (exec_plan.startsWith("EVERY"))
      {
         //"EVERY "+ intv_hours +":"+intv_mins;
         execution = "INTERVAL";
         int i = exec_plan.indexOf("EVERY ")+6;
         int j = exec_plan.indexOf(":");
         intv_hours = exec_plan.substring(i,j);
         intv_mins = exec_plan.substring(j+1);
      }
      else
         custom_exp = exec_plan;

      ASPBuffer buf = mgr.newASPBuffer();
      buf.addFieldDateItem("SCHD_TIME", mgr.parseDate("SCHD_TIME", schd_time));

      rowset.setValue("SCHD_TIME", buf.getValue("SCHD_TIME"));

      if (scheduled_report)
      {
         int rows = paramset.countRows();

         for (int i=0;i < rows; i++)
         {
            String param_name = paramset.getValueAt(i,"PARAM_NAME");
            String attr = paramset.getValueAt(i,"PARAM_VALUE");

            if ("REPORT_ATTR".equals(param_name))
            {
               report_id = inf.getAttrItemValue("REPORT_ID", attr);
               layout = inf.getAttrItemValue("LAYOUT_NAME", attr);
               language = inf.getAttrItemValue("LANG_CODE", attr);
            }
            else if ("MESSAGE_ATTR".equals(param_name))
            {
               message_type = inf.getAttrItemValue("MESSAGE_TYPE", attr);
               send_email_to = inf.getAttrItemValue("SEND_EMAIL_TO", attr);
               printer = inf.getAttrItemValue("PRINTER_ID", attr);
            }
            else if ("ARCHIVING_ATTR".equals(param_name))
            {
               archivate = inf.getAttrItemValue("PDF_ARCHIVING", attr);
               send_pdf = inf.getAttrItemValue("SEND_PDF", attr);
               send_pdf_to = inf.getAttrItemValue("SEND_PDF_TO", attr);
            }
            else if ("PARAMETER_ATTR".equals(param_name))
               itemset_parameter_attr = attr;
         }
      }
      custom_url = mgr.getConfigParameter(method_name+"/URL","");
   }

   private String getNextExecutionDate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomFunction("NEXT_EXE", "Batch_SYS.Update_Exec_Time__", "NEXT_EXECUTION_DATE");
      cmd.addParameter("EXECUTION",getExecutionPlan());

      trans = mgr.perform(trans);

      return trans.getValue("NEXT_EXE/DATA/NEXT_EXECUTION_DATE");
   }

   private String  getRepeatedDays()
   {
      ASPManager mgr = getASPManager();

      String value="";
      String delim="";

      if ( "ON".equals(monday) )
      {
         value=value+delim+"mon";
         delim=";";
      }

      if ( "ON".equals(tuesday) )
      {
         value=value+delim+"tue";
         delim=";";
      }

      if ( "ON".equals(wednesday) )
      {
         value=value+delim+"wed";
         delim=";";
      }

      if ( "ON".equals(thursday) )
      {
         value=value+delim+"thu";
         delim=";";
      }

      if ( "ON".equals(friday) )
      {
         value=value+delim+"fri";
         delim=";";
      }

      if ( "ON".equals(saturday) )
      {
         value=value+delim+"sat";
         delim=";";
      }

      if ( "ON".equals(sunday) )
         value=value+delim+"sun";

      return value;
   }



   public void  preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");
      blk.addField("SCHEDULE_NAME");
      blk.addField("SCHEDULE_METHOD_ID").setHidden();
      blk.addField("ACTIVE_DB").setHidden();
      blk.addField("EXECUTION_PLAN").setHidden();
      schd_date_fld = blk.addField("START_DATE","Date");
      blk.addField("STOP_DATE","Date");
      blk.addField("NEXT_EXECUTION_DATE","Datetime");
      blk.addField("ARGUMENT_TYPE_DB").setHidden();
      blk.addField("METHOD_NAME").setHidden();
      blk.addField("VALIDATION_METHOD").setHidden();
      
      schd_time_fld = blk.addField("SCHD_TIME","Time","HH:mm");

      blk.addField("EXECUTION");
      blk.addField("SCHD_DATE","Date","yyyy-MM-dd");
      blk.addField("LANG_CODE").setHidden();

      rowset = blk.getASPRowSet();
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);


      //disableHomeIcon();
      disableNavigate();
      disableOptions();

      paramblk = mgr.newASPBlock("PARAM");
      paramblk.addField("PARAM_NAME");
      paramblk.addField("PARAM_VALUE").setMandatory();
      paramblk.addField("PARAM_MANDATORY");
      paramblk.addField("DATA_TYPE");
      paramblk.addField("DATE_FIELD","Datetime","yyyy-MM-dd HH:mm:ss");

      paramset = paramblk.getASPRowSet();
      paramtbl = mgr.newASPTable(paramblk);
      paramtbl.setEditable();

      parambar = mgr.newASPCommandBar(paramblk);
      paramlay = paramblk.getASPBlockLayout();
      paramlay.setDefaultLayoutMode(paramlay.MULTIROW_LAYOUT);


      itemblk = mgr.newASPBlock("ITEM");

      itemblk.addField("REPORT_ID")
      .setHidden();

      itemblk.addField("COLUMN_NAME")
      .setHidden();

      itemblk.addField("ITEM_NAME")
      .setSize(30)
      .setLabel("FNDSCHEDWIZARDITEMNAME: Item");

      value_field = itemblk.addField("VALUE")
      .setSize(40)
      .setLOV("ScheduledTaskWizard.page")
      .setLabel("FNDSCHEDWIZARDVALUE: Value");


      itemset = itemblk.getASPRowSet();
      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setEditable();

      itembar = mgr.newASPCommandBar(itemblk);
      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      inf = mgr.newASPInfoServices();
      inf.addFields();

      utilblk = mgr.newASPBlock("UTIL");

      //utilblk.addField("REPORT_ID");
      //utilblk.addField("REPORT_TITLE");
      utilblk.addField("CLIENT_VALUES0");
      utilblk.addField("CLIENT_VALUES1");
      utilblk.addField("ENUMUSERWHERE");
      utilblk.addField("PROPERTIES");
      utilblk.addField("PROPERTIES_LIST_OUT");
      utilblk.addField("PROPERTIES_LIST");
      utilblk.addField("ARCHIVATE_");
      utilblk.addField("SMTP_MAIL_ADDRESS");
      utilblk.addField("MAIL_PROPERTIES");
      utilblk.addField("FND_USER");

      utilblk.addField("REPORT_ATTR_");
      utilblk.addField("COLUMN_PROPERTIES_");
      utilblk.addField("TEXT_PROPERTIES_");
      utilblk.addField("LAYOUT_PROPERTIES_");
      utilblk.addField("LANG_PROPERTIES_");
      utilblk.addField("RESULT_KEY");

      utilblk.addField("CUSTOM_EXP");

      appendJavaScript("function checkMandatoryValue(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("\tfld = getField_('PARAM_VALUE',i);\n");
      appendJavaScript("\tlbl = getField_('PARAM_NAME',i).value;\n");
      appendJavaScript("\treturn checkMandatory_(fld,lbl,'');\n");
      appendJavaScript("}\n");
      
      appendJavaScript("function checkNumberValue(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("\tfld = getField_('PARAM_VALUE',i);\n");
      appendJavaScript("\tlbl = getField_('PARAM_NAME',i).value;\n");
      appendJavaScript("\treturn checkNumberValue_(fld,lbl,'');\n");
      appendJavaScript("}\n");
      
      setChildFrameNames("cust_rep_frame",true);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDSCHEDWIZARDTITLE: Scheduled Task Wizard";
   }

   protected String getTitle()
   {
      return "FNDSCHEDWIZARDTITLE: Scheduled Task Wizard";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      if (task_scheduled)
      {
         if (from_schedule_task)
         {
            if (modify_task)
               appendDirtyJavaScript("\twindow.opener.location.href='"+getASPConfig().getScriptsLocation() + "ScheduledTaskHeader.page?SCHEDULE_ID="+schedule_id+"';");
            else if (!"REPORTS_ONLY".equals(tree_type))
               appendDirtyJavaScript("\twindow.opener.location.href='"+getASPConfig().getScriptsLocation() + "ScheduledTaskTreeList.page?__SORT_BY="+sort_by+"';");
            else
               appendDirtyJavaScript("\twindow.opener.location.href='"+getASPConfig().getScriptsLocation() + "ScheduledTaskTreeList.page?SCHEDULE_TYPE=REPORTS';");
         }
         else if (!mgr.isEmpty(redirect_url))
         {
            mgr.redirectTo(redirect_url);
         }

         appendDirtyJavaScript("\tif (window.opener) window.close();\n");
         return out;
      }
      
      if (mgr.isEmpty(mgr.getQueryStringValue("TREE_TYPE")))
      {
         enableNavigate();
         enableOptions();
      }

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDSCHEDWIZARDTITLE: Scheduled Task Wizard"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("FNDSCHEDWIZARDTITLE: Scheduled Task Wizard"));

      out.append("  <input type=\"hidden\" name=\"__PRESSED_BUTTON\">\n");
      eval(blk.generateAssignments());

      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate("FNDSCHEDWIZARDTITLE: Scheduled Task Wizard"));
      out.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" height=\"100%\"");
      out.append(" class=pageFormWithBorder>\n");
      out.append(" <tr>");
      out.append("<td valign=top rowspan=2><img src=\""+getASPConfig().getImagesLocation()+"report_wizard.gif\"></td>");
      out.append("<td height=100%>&nbsp;&nbsp;</td>");
      out.append("<td width=100%>\n");
      //out.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"10\" width=\"100%\" height=\"100%\">");
      //out.append("<tr><td>");

      if(step_no == 0) //select report
      {
         mgr.getASPField("SCHEDULE_NAME").setHidden();
         mgr.getASPField("STOP_DATE").setHidden();
         mgr.getASPField("NEXT_EXECUTION_DATE").setHidden();
         schd_date_fld.setHidden();
         schd_time_fld.setHidden();

         printHiddenField("REPORT_ID", report_id);
         printHiddenField("SELECTED_REPORT", selected_report);

         out.append("<script language=javaScript>REPORT_NOT_SELECTED_MSG ='",mgr.translateJavaScript("FNDSCHEDWIZARDTASKSELREPMSG: Please Select a Report"),"'; </script>");
         out.append("  <input type=\"hidden\" name=\"__REP_ID_LIST\" value=\"");
         out.append(report_id_list);
         out.append("\">");

         out.append("<table border=0 cellpadding=3 Class=\"BlockLayoutTable\" width=100% height="+DIALOG_HEIGHT+">\n");
         out.append(" <tr>\n");
         out.append("  <td>\n");
         printText("FNDSCHEDWIZARDTASKSELCTREP: Select a report");
         printNewLine();
         out.append("</td>\n");
         out.append("</tr>\n");

         out.append("<tr valign=top>\n");
         out.append("<td height=100%>\n");
         printReadLabel("FNDSCHEDWIZARDTASKREPTITLE: Report Title:");
         out.append("</td>\n");
         out.append("<td>");

         printSelectBox("REPORT_TITLE",report_buffer,selected_report);
         out.append("*</td>\n");
         out.append(" </tr>\n");
         out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:fromStep0to1(this.form);\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");
      }
      else if(step_no == 1) //report parameter step
      {
         mgr.getASPField("SCHEDULE_NAME").setHidden();
         mgr.getASPField("STOP_DATE").setHidden();
         mgr.getASPField("NEXT_EXECUTION_DATE").setHidden();
         schd_date_fld.setHidden();
         schd_time_fld.setHidden();

         out.append("<table border=0 cellpadding=3 Class=\"BlockLayoutTable\" width=100% height="+DIALOG_HEIGHT+">\n");
         out.append("<tr><td id=ramila width=\"100%\">");

         if (mgr.isEmpty(custom_url))
            drawTable(out);
         else
         {
            out.append("\n<iframe height="+iframe_height+" width="+iframe_width+" name=\"cust_rep_frame\" id=\"cust_rep_frame\" src = \""+mgr.getApplicationPath()+"/"+custom_url+"\"");
            out.append(" scrolling=\"no\"");
            out.append(" frameborder=\"0\" class=borders >\n");
            out.append(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
            out.append("  </iframe>\n");

            printHiddenField("REPORT_PARAM_ATTR",parameter_attr);
            printHiddenField("CUSTOM_ATTR",custom_attr);

            appendDirtyJavaScript("function toStep(to_step){\n");
            appendDirtyJavaScript("\t cust_rep_frame = window.frames['cust_rep_frame'];\n");
            appendDirtyJavaScript("\t var attr = cust_rep_frame.getAttributeString(to_step);\n");
            appendDirtyJavaScript("\t if (attr != 'INVALID'){\n");
            appendDirtyJavaScript("\t\t document.form.REPORT_PARAM_ATTR.value=attr;\n");
            appendDirtyJavaScript("\t\t document.form.CUSTOM_ATTR.value=cust_rep_frame.getCustomAttr();\n");
            appendDirtyJavaScript("\t\t document.form.__PRESSED_BUTTON.value=to_step;\n\t\tsubmit();\n");
            appendDirtyJavaScript("\t}\n");
            appendDirtyJavaScript("}\n");

            //javascript methods used by custom reports to repopulate
            appendDirtyJavaScript("function getAttributeString(){\n");
            appendDirtyJavaScript("\treturn f.REPORT_PARAM_ATTR.value;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function getCustomAttr(){\n");
            appendDirtyJavaScript("\treturn f.CUSTOM_ATTR.value; \n");
            appendDirtyJavaScript("}\n");

         }

         out.append("</td>\n");
         out.append("</tr> \n");
         out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         if (!skip_step0)
         {
            if (mgr.isEmpty(custom_url))
               printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
            else
               printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"toStep('PREVIOUS');\" ");
         }
         out.append("&nbsp;&nbsp;");

         if (mgr.isEmpty(custom_url))
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='NEXT';submit();\" ");
         else
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:toStep('NEXT');\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

         if (!mgr.isEmpty(lov_script))
            appendDirtyJavaScript("\n"+lov_script+"\n");

      }
      else if(step_no == 2) //execution details step
      {
         mgr.getASPField("STOP_DATE").setHidden();
         mgr.getASPField("NEXT_EXECUTION_DATE").setHidden();
         schd_date_fld.setHidden();
         schd_time_fld.setHidden();


          out.append("<table border=0 cellpadding=3 Class=\"BlockLayoutTable\" width=100% height="+DIALOG_HEIGHT+">\n");

          out.append("<tr>\n");
          out.append("<td>\n");
          printReadLabel("FNDSCHEDWIZARDTASKNAME: Task Name:");
          out.append("</td>\n");
          out.append("<td>");

          //schedule_name = (mgr.isEmpty(schedule_name)?mgr.readValue("SCHEDULE_NAME"):schedule_name);
          printField("SCHEDULE_NAME",rowset.getClientValue("SCHEDULE_NAME"), "", 50);
          out.append("</td>\n");
          out.append(" </tr>\n");


          out.append(" <tr>\n");
          out.append("  <td><p>\n");
          printText("FNDSCHEDWIZARDTASKPERTASK: Perform Task:");
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKDAILY: Daily","EXECUTION","DAILY","DAILY".equals(execution)?true:false, "",0);
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKWEEKLY: Weekly","EXECUTION","WEEKLY","WEEKLY".equals(execution)?true:false, "",1);
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKMONTHLY: Monthly","EXECUTION","MONTHLY","MONTHLY".equals(execution)?true:false, "",2);
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKSCHEDULED: Scheduled","EXECUTION","SCHEDULED","SCHEDULED".equals(execution)?true:false, "",3);
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKINTERVAL: Interval","EXECUTION","INTERVAL","INTERVAL".equals(execution)?true:false, "",4);
          printNewLine();
          printRadioButton("FNDSCHEDWIZARDTASKCUSTOM: Custom","EXECUTION","CUSTOM","CUSTOM".equals(execution)?true:false, "",5);

          out.append("</p></td></tr>\n");

          out.append("</td>\n");
          out.append("</tr> \n");
          out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         if (!skip_step1 && scheduled_report)
            printButton("PREVIOUS","FNDSCHEDWIZARDTASKPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
         out.append("&nbsp;&nbsp;");
         printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='NEXT';submit();\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

      }
      else if(step_no == 3) //execution date/time
      {
         mgr.getASPField("SCHEDULE_NAME").setHidden();
         mgr.getASPField("STOP_DATE").setHidden();
         mgr.getASPField("NEXT_EXECUTION_DATE").setHidden();

         String _date = "";
         String _time = "";

         ASPBuffer buf = mgr.newASPBuffer();
         buf.setFieldDateItem("START_DATE", new Date());
         buf.setFieldDateItem("SCHD_TIME", new Date());

         _date = buf.getFieldValue("START_DATE");
         _time = buf.getFieldValue("SCHD_TIME");

         String schd_time = rowset.getClientValue("SCHD_TIME");
         if (mgr.isEmpty(schd_time))
            schd_time = _time;

         out.append("<table border=0 class=\"BlockLayoutTable\" width=100% height=\""+DIALOG_HEIGHT+"\">\n");
         printHiddenField("WIZARD_STEP_NO", "3");

         if ("DAILY".equals(execution))
         {
            schd_date_fld.setHidden();

            out.append("<tr valign=top>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELSTARTTIME: Select the time you want to start this task");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr height=100%>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDTIME: Start time:");
            out.append("&nbsp;");
            printField("SCHD_TIME",schd_time,schd_time_fld.getTag(),6,5);
            out.append("</td>");
            out.append("</tr>\n");

         }
         else if ("WEEKLY".equals(execution))
         {
            schd_date_fld.setHidden();

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELSTARTTIMEANDDAY: Select the time and day you want to start this task");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDPSCHEDWIZARDTIME: Start time:");
            out.append("&nbsp;");
            printField("SCHD_TIME",schd_time,schd_time_fld.getTag(),6,5);
            out.append("</td>");
            out.append("</tr>\n");

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELDAYOFWEEK: Select the day(s) of the week below:");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            out.append("<table border=0 width=100% >\n");

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDMONDAY: Monday","cbMon","ON","ON".equals(monday),"");
            out.append("</td>");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDWEDNESDAY: Wednesday","cbWed","ON","ON".equals(wednesday),"");
            out.append("</td>");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDFRIDAY: Friday","cbFri","ON","ON".equals(friday),"");
            out.append("</td>");
            out.append("</tr>\n");

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDTUESDAY: Tuesday","cbTue","ON","ON".equals(tuesday),"");
            out.append("</td>");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDTHURSDAY: Thursday","cbThu","ON","ON".equals(thursday),"");
            out.append("</td>");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDSATURDAY: Saturday","cbSat","ON","ON".equals(saturday),"");
            out.append("</td>");
            out.append("</tr>\n");

            out.append("<tr>\n");
            out.append("<td align='left'>");
            out.append("</td>");
            out.append("<td align='left'>");
            out.append("</td>");
            out.append("<td align='left'>");
            printCheckBox("FNDSCHEDWIZARDSUNDAY: Sunday","cbSun","ON","ON".equals(sunday),"");
            out.append("</td>");
            out.append("</tr>\n");

            out.append("</table>\n");
            out.append("</td>");
            out.append("</tr>\n");
         }
         else if ("MONTHLY".equals(execution))
         {
            schd_date_fld.setHidden();

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELSTARTTIMEANDDAY: Select the time and day you want to start this task");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDTIME: Start time:");
            out.append("&nbsp;");
            printField("SCHD_TIME",schd_time,schd_time_fld.getTag(),6,5);
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDDAY: Day:");
            out.append("&nbsp;");
            printField("ON_DAY",mgr.isEmpty(on_day)?"1":on_day,"align=right",3,2);
            out.append("</td>");
            out.append("</tr>\n");
         }
         else if ("SCHEDULED".equals(execution))
         {
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELSTARTTIMEANDDAY: Select the time and day you want to start this task");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDTIME: Start time:");
            out.append("&nbsp;");
            printField("SCHD_TIME",schd_time,schd_time_fld.getTag(),6,5);
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDDAYS: Start Date:");
            out.append("&nbsp;");

            String schd_date = rowset.getClientValue("START_DATE");
            if (mgr.isEmpty(schd_date))
               schd_date =_date;

            printField("START_DATE",schd_date,schd_date_fld.getTag(),12,10);
            out.append("</td>");
            out.append("</tr>\n");
         }
         else if ("INTERVAL".equals(execution))
         {
            schd_date_fld.setHidden();
            schd_time_fld.setHidden();

            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDSELSTARTINTERVAL: Select the interval you want this task to be started");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDPERFORMTASK: Perform this task:");
            out.append("</td>");
            out.append("</tr>\n");


            out.append("<tr>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDEVERY: every: ");
            out.append("&nbsp;");
            printField("INTV_HOURS",mgr.isEmpty(intv_hours)?"0":intv_hours,"align=right",3,2);
            out.append("&nbsp;");
            printReadLabel("FNDSCHEDWIZARDHOURSAND: hours and");
            out.append("&nbsp;");
            printField("INTV_MINS",mgr.isEmpty(intv_mins)?"0":intv_mins,"align=right",3,2);
            out.append("&nbsp;");
            printReadLabel("FNDSCHEDWIZARDMINS: minutes");
            out.append("</td>");
            out.append("</tr>\n");
         }
         else if ("CUSTOM".equals(execution))
         {
            schd_date_fld.setHidden();
            schd_time_fld.setHidden();

            out.append("<tr valign=top>\n");
            out.append("<td align='left'>");
            printText("FNDSCHEDWIZARDENTCUSTEXP: Enter the custom expression to be used as the schedule for this task.");
            out.append("</td>");
            out.append("</tr>\n");
            out.append("<tr height=100%>\n");
            out.append("<td align='left'>");
            printReadLabel("FNDSCHEDWIZARDCUSTEXP: Expression:");
            out.append("&nbsp;");
            printField("CUSTOM_EXP",custom_exp,"",30,30);
            out.append("</td>");
            out.append("</tr>\n");
         }

         out.append("</td>\n");
         out.append("</tr> \n");
         out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
         out.append("&nbsp;&nbsp;");
         if ("CUSTOM".equals(execution))
         {
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:validateNextStep();\" ");

            appendDirtyJavaScript("\nfunction validateNextStep(){\n");
            appendDirtyJavaScript(" r = __connect('"+mgr.getURL()+"?VALIDATE=CUSTOM_EXP'\n");
            appendDirtyJavaScript("     +'&CUSTOM_EXP='+ URLClientEncode(getValue_('CUSTOM_EXP',-1)));\n");
            appendDirtyJavaScript(" if (r.indexOf(\"valid_expression^\") > -1)\n");
            appendDirtyJavaScript(" {  document.form.__PRESSED_BUTTON.value='NEXT';submit();}\n");
            appendDirtyJavaScript(" else\n");
            appendDirtyJavaScript("   alert(r.substring(1));\n");
            appendDirtyJavaScript("}\n");
         }
         else if ("WEEKLY".equals(execution))
         {
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:validateNextStep();\" ");

            appendDirtyJavaScript("\nfunction validateNextStep(){\n");
            appendDirtyJavaScript("  if (f.cbMon.checked || f.cbTue.checked ||f.cbWed.checked ||f.cbThu.checked ||f.cbFri.checked ||f.cbSat.checked ||f.cbSun.checked)\n");
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("    document.form.__PRESSED_BUTTON.value='NEXT';submit();\n");            
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript(" else\n");
            appendDirtyJavaScript("    alert('"+mgr.translate("FNDSCHEDWIZARDSELECTWEEKLYDAY: Select the day(s) of the week to schedule.")+"');\n");
            appendDirtyJavaScript("}\n");
         }
         else if ("INTERVAL".equals(execution))
         {
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:validateNextStep();\" ");

            appendDirtyJavaScript("\nfunction validateNextStep(){\n");
            appendDirtyJavaScript("  var invalid = false;\n"); 
            //Invalid interval if both hours and mins == 0
            appendDirtyJavaScript("  if (f.INTV_HOURS.value == '0' && f.INTV_MINS.value=='0')\n");
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("    alert('"+mgr.translate("FNDSCHEDWIZARDINVALIDINTVAL: Invalid interval value.")+"');\n");
            appendDirtyJavaScript("    return;\n");
            appendDirtyJavaScript("  }\n");
            
            appendDirtyJavaScript("  var re = /^\\d\\d$/;\n"); 
            appendDirtyJavaScript("  var re1 = /^\\d$/;\n"); 
            appendDirtyJavaScript("  var _val = f.INTV_HOURS.value;\n");
            //validate hours
            appendDirtyJavaScript("  if(_val.length == 1 ){\n"); 
            appendDirtyJavaScript("    if(!re1.test(_val))\n"); 
            appendDirtyJavaScript("       invalid = true;\n"); 
            appendDirtyJavaScript("  }else{\n"); 
            appendDirtyJavaScript("    if(!re.test(_val))\n"); 
            appendDirtyJavaScript("       invalid = true;\n"); 
            appendDirtyJavaScript("  }\n"); 

            appendDirtyJavaScript("  if(invalid)\n"); 
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("    alert('"+mgr.translate("FNDSCHEDWIZARDINVALIDHOURINTVAL: The hour interval must be between 0 and 99.")+"');\n");
            appendDirtyJavaScript("    return;\n");
            appendDirtyJavaScript("  }\n");

            //validate mins
            appendDirtyJavaScript("  re = /^[0-5]\\d$/;\n"); 
            appendDirtyJavaScript("  _val = f.INTV_MINS.value;\n");
            appendDirtyJavaScript("  if(_val.length == 1 ){\n"); 
            appendDirtyJavaScript("      if(!re1.test(_val))\n"); 
            appendDirtyJavaScript("        invalid = true;\n"); 
            appendDirtyJavaScript("  }else{\n"); 
            appendDirtyJavaScript("      if(!re.test(_val))\n"); 
            appendDirtyJavaScript("         invalid = true;\n"); 
            appendDirtyJavaScript("  }\n"); 
            
            appendDirtyJavaScript("  if(invalid)\n"); 
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("       alert('"+mgr.translate("FNDSCHEDWIZARDINVALIDMININTVAL: The minute interval must be between 0 and 59.")+"');\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript("  else\n");
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("        document.form.__PRESSED_BUTTON.value='NEXT';\n");
            appendDirtyJavaScript("        submit();\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript("}\n");
         }
         else
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='NEXT';submit();\" ");
            
         
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

      }
      else if(step_no == 4) // parameter step
      {
         printHiddenField("WIZARD_STEP_NO", "4");
         
         mgr.getASPField("SCHEDULE_NAME").setHidden();
         mgr.getASPField("STOP_DATE").setHidden();
         mgr.getASPField("NEXT_EXECUTION_DATE").setHidden();
         schd_date_fld.setHidden();
         schd_time_fld.setHidden();

         if (!scheduled_report)
         {
            if (mgr.isEmpty(custom_url))
               drawParameterSheet(out);
            else
            {
               iframe_height = mgr.getConfigParameter(rowset.getValue("METHOD_NAME")+"/IFRAME_HEIGHT",""+(Integer.parseInt(DIALOG_HEIGHT) - 5));
               iframe_width = mgr.getConfigParameter(rowset.getValue("METHOD_NAME")+"/IFRAME_WIDTH",DIALOG_WIDTH);
               out.append("\n<iframe height="+iframe_height+" width="+iframe_width+" name=\"cust_rep_frame\" id=\"cust_rep_frame\" src = \""+mgr.getApplicationPath()+"/"+custom_url+"?SCHEDULE_METHOD_ID="+rowset.getValue("SCHEDULE_METHOD_ID")+"&SCHEDULE_ID="+schedule_id+"&CLIENT_PARAMS="+(mgr.isEmpty(client_params)?"":client_params)+"\"");
               out.append(" scrolling=\"yes\"");
               out.append(" frameborder=\"0\" class=borders >\n");
               out.append(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
               out.append("  </iframe>\n");

               printHiddenField("PARAM_ATTR",parameter_attr);
               printHiddenField("CUSTOM_ATTR",custom_attr);

               appendDirtyJavaScript("function toStep(to_step){\n");
               appendDirtyJavaScript("\t cust_rep_frame = window.frames['cust_rep_frame'];\n");
               appendDirtyJavaScript("\t var attr = cust_rep_frame.getAttributeString(to_step);\n");
               appendDirtyJavaScript("\t if (attr != 'INVALID'){\n");
               appendDirtyJavaScript("\t\t document.form.PARAM_ATTR.value=attr;\n");
               appendDirtyJavaScript("\t\t document.form.CUSTOM_ATTR.value=cust_rep_frame.getCustomAttr();\n");
               appendDirtyJavaScript("\t\t document.form.__PRESSED_BUTTON.value=to_step;\n\t\tsubmit();\n");
               appendDirtyJavaScript("\t}\n");
               appendDirtyJavaScript("}\n");

               //javascript methods used by custom reports to repopulate
               appendDirtyJavaScript("function getAttributeString(){\n");
               appendDirtyJavaScript("\treturn f.PARAM_ATTR.value;\n");
               appendDirtyJavaScript("}\n");

               appendDirtyJavaScript("function getCustomAttr(){\n");
               appendDirtyJavaScript("\treturn f.CUSTOM_ATTR.value; \n");
               appendDirtyJavaScript("}\n");
            }
         }
         else
         {
            out.append("<table border=0 class=\"BlockLayoutTable\" width=100% height=\""+DIALOG_HEIGHT+"\">\n");
            out.append("<tr><td colspan=2>");
            printText("FNDSCHEDWIZARDWHENREPFIN: When finished");
            out.append("</td></tr>");
            out.append("<tr valign=top><td colspan=2 nowrap>");
            printRadioButton("FNDSCHEDWIZARDDONOTHING: Do nothing","MESSAGE_TYPE","NONE","NONE".equals(message_type)?true:false,"onclick=\"javascript:validateMessageType(this.value);\"",0);
            out.append("</td><tr><td nowrap>");
            printRadioButton("FNDSCHEDWIZARDSENDMAIL: Send E-Mail to","MESSAGE_TYPE","EMAIL","EMAIL".equals(message_type)?true:false,"onclick=\"javascript:validateMessageType(this.value);\"",1);
            out.append("</td><td width=100%>");
            send_email_to = (mgr.isEmpty(send_email_to)?smtp_mail_address:send_email_to);
            printField("SEND_EMAIL_TO",send_email_to,"EMAIL".equals(message_type)?"":"DISABLED",50);
            out.append("</td><tr><td colspan=2 nowrap>");
            printRadioButton("FNDSCHEDWIZARDPRINT: Print","MESSAGE_TYPE","PRINTER","PRINTER".equals(message_type)?true:false,"onclick=\"javascript:validateMessageType(this.value);\"",2);
            out.append("</td></tr>");

            out.append("<tr><td nowrap align=right>");
            printTextLabel("FNDSCHEDWIZARDPRINTER: Printer:");
            out.append("</td><td width=100%>");
            printer = mgr.isEmpty(printer)?readGlobalProfileValue("Defaults/Printer"+ProfileUtils.ENTRY_SEP+"Default",false):printer;
            printMandatorySelectBox("PRINTER",printers,printer,"PRINTER".equals(message_type)?"":"DISABLED");
            out.append("</td></tr>");
            out.append("<tr><td nowrap align=right>");
            printTextLabel("FNDSCHEDWIZARDLAYOUT: Layout:");
            out.append("</td><td width=100%>");
            printHiddenField("LAYOUT_TITLE","");
            out.append(mgr.newASPHTMLFormatter().drawPreparedSelect("LAYOUT",layouts,"onChange=f.LAYOUT_TITLE.value=this.options[this.selectedIndex].text "+("PRINTER".equals(message_type)?"":"DISABLED"),false));
            appendDirtyJavaScript("f.LAYOUT_TITLE.value=f.LAYOUT.options[f.LAYOUT.selectedIndex].text;");
            out.append("</td></tr>");
            out.append("<tr><td nowrap align=right>");
            printTextLabel("FNDSCHEDWIZARDLANGUAGE: Language:");
            out.append("</td><td width=100%>");
            out.append(mgr.newASPHTMLFormatter().drawPreparedSelect("LANGUAGE",languages,"PRINTER".equals(message_type)?"":"DISABLED",false));
            out.append("</td></tr>");


            out.append("<tr><td colspan=2>");
            printHiddenField("ARCHIVATE",archivate);
            out.append(archivate_box,"&nbsp;");
            printTextLabel("FNDSCHEDWIZARDARCHAND: Archive the report and");
            out.append("</td></tr>");

            out.append("<tr><td nowrap align=right>&nbsp;&nbsp;&nbsp;&nbsp;");
            if (!"NONE".equals(event_executor))
               printCheckBox("FNDSCHEDWIZARDSENDPDFTO: E-mail the PDF file to:","SEND_PDF","TRUE","TRUE".equals(send_pdf)?true:false,"onclick=\"javascript:assignEMail()\" "+("FALSE".equals(archivate)||mgr.isEmpty(archivate)?"DISABLED":""));
            else
               printHiddenField("EMAIL", "");
            out.append("</td>");
            send_pdf_to = (mgr.isEmpty(send_pdf_to)?smtp_mail_address:send_pdf_to);
            out.append("<td width=100%>");
            if (!"NONE".equals(event_executor))
               printField("SEND_PDF_TO",send_pdf_to,("FALSE".equals(archivate)||mgr.isEmpty(archivate)?"DISABLED":""),50);
            else
               printHiddenField("SEND_PDF_TO", "");
            out.append("</td></tr>");

            out.append("</table>\n");
         }
         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         if (scheduled_report || mgr.isEmpty(custom_url))
            printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
         else
            printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"javascript:toStep('PREVIOUS');\" ");
         out.append("&nbsp;&nbsp;");
         if (scheduled_report || mgr.isEmpty(custom_url))
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='NEXT';submit();\" ");
         else
            printButton("NEXT","FNDSCHEDWIZARDTASKNEXTBTN: Next","onClick=\"javascript:toStep('NEXT');\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

         appendDefaultScript();

      }
      else if(step_no == 5) // schedule final step
      {
         mgr.getASPField("SCHEDULE_NAME").setHidden();
         schd_time_fld.setHidden();

         out.append("<table border=0 class=\"BlockLayoutTable\" width=100% height=\""+DIALOG_HEIGHT+"\">\n");
         out.append("<tr>\n");
         out.append("<td align='left'><b>");
         printBoldText("FNDSCHEDWIZARDSUCC: You have successfully scheduled the task:");
         out.append("</b></td>");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td align='left'>");
         printText(rowset.getClientValue("SCHEDULE_NAME"));
         out.append("</td>");
         out.append("<br></tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printText("FNDSCHEDWIZARDJOBEXE: The job is scheduled to execute");
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printTextLabel("FNDSCHEDWIZARDSTARTDATE: start date:");

            String start_date_time = "";
            
            if (!modify_task)
               start_date_time = getNextExecutionDate();
            else
               start_date_time = rowset.getValue("START_DATE");
            
            //String start_date = mgr.formatDate("START_DATE", mgr.parseDate("START_DATE", start_date_time));

            ASPBuffer buf = mgr.newASPBuffer();
            buf.setValue("SCHD_TIME", start_date_time);
            buf.setValue("START_DATE", start_date_time);
            String start_time = buf.getFieldValue("SCHD_TIME");
            String start_date = buf.getFieldValue("START_DATE");

         printReadOnlyTextField("START_DATE", start_date,"");
         printText("FNDSCHEDWIZARDAT: at ");
         printText(start_time);
         //printText(rowset.getClientValue("SCHD_TIME"));
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printTextLabel("FNDSCHEDWIZARDSTOPTDATE: stop date:");
         printReadOnlyTextField("STOP_DATE", rowset.getClientValue("STOP_DATE"),"");
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printTextLabel("FNDSCHEDWIZARDNEXTEXE: next execution:");
         //next_exec_date = (mgr.isEmpty(next_exec_date)?getNextExecutionDate():next_exec_date);
         printReadOnlyTextField("NEXT_EXECUTION_DATE",rowset.getClientValue("NEXT_EXECUTION_DATE"),"");
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='right'>");
         printButton("ADVANCED", "FNDSCHEDWIZARDADVANCE: Advanced",
                     "onclick=\"javascript:showNewBrowser('" + getASPConfig().getScriptsLocation() +
                     "AdvancedScheduledOptions.page?START_DATE='+f.START_DATE.value+"+
                     "'&STOP_DATE='+f.STOP_DATE.value+"+
                     "'&CAL_NEXT_EXE_DATE="+getNextExecutionDate()+
                     "&NEXT_EXECUTION_DATE='+f.NEXT_EXECUTION_DATE.value)\" "+(!"SCHEDULED".equals(execution)?"":"disabled"));
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printTextLabel("FNDSCHEDWIZARDSELLANG: Selected Language:");
         out.append("&nbsp;");
         mgr.getASPField("LANG_CODE").unsetHidden();
         printSelectBox("LANG_CODE",getLangBuffer(),rowset.getValue("LANG_CODE"));
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printCheckBox("FNDSCHEDWIZARDACTIVATE: Mark this task active when I press finish","ACTIVE","TRUE","TRUE".equals(rowset.getValue("ACTIVE_DB")),"");
         //printText("FNDSCHEDWIZARDACTIVATE: Mark this task active when I press finish");
         out.append("</td>");
         out.append("</tr>\n");

         out.append("<tr>\n");
         out.append("<td align='left'>");
         printText("FNDSCHEDWIZARDFINIDESC: Click Finish to add this task to IFS Application schedule.");
         out.append("</td>");
         out.append("</tr>\n");
         out.append("</table>\n");


         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         printButton("PREVIOUS","FNDSCHEDWIZARDPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
         out.append("&nbsp;&nbsp;");

         if (mgr.isEmpty(custom_url) || scheduled_by_fndweb)
            printSubmitButton("FINISH","FNDSCHEDWIZARDFINISHBTN: Finish","");
         else
         {
            printButton("FINISH","FNDSCHEDWIZARDFINISHBTN: Finish","onClick=\"javascript:finish();\" ");

            out.append("\n<iframe height=1 width=1 name=\"order_rep_frame\" id=\"order_rep_frame\" src = \""+mgr.getApplicationPath()+"/"+custom_url+"\"");
            out.append(" scrolling=\"no\"");
            out.append(" frameborder=\"0\">\n");
            out.append(mgr.translate("FNDSCHEDULETASKNOIFRAME: This page uses iframes, but your browser doesn't support them."));
            out.append("  </iframe>\n");

            String report_attr = getReportAttr();
            String schedule_attr = getScheduleAttr();
            String message_attr = getMessageAttr();
            String archiving_attr = getArchiveAttr();

            //to avoid free floating javascript methods causing script errors
            appendDirtyJavaScript("function getAttributeString(){\n");
            appendDirtyJavaScript("\treturn '';\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function getCustomAttr(){\n");
            appendDirtyJavaScript("\treturn ''; \n");
            appendDirtyJavaScript("}\n");


            appendDirtyJavaScript("\nfunction finish(){\n");
            appendDirtyJavaScript("\t order_rep_frame = window.frames['order_rep_frame'];\n");
            appendDirtyJavaScript("\t order_rep_frame.finish('"+report_attr+"','"+parameter_attr+"','"+schedule_attr+"','"+message_attr+"','"+archiving_attr+"','"+custom_attr+"');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("\nfunction submitFinish(){\n");
            appendDirtyJavaScript("\tdocument.form.__PRESSED_BUTTON.value='TASK_COMPLETED';\n\tsubmit();\n");
            appendDirtyJavaScript("}\n");

         }

         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

      }

      endDataPresentation(false);

      out.append(blk.generateHiddenFields());

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");

      if (scheduled_report)
      {
         String script_loc = getASPConfig().getScriptsLocation();
         String src_file = script_loc + "ScheduleReportUtil.js";

         out.append("<script language=javascript src="+src_file+"></script>\n");
      }

      appendDirtyJavaScript("function cancel()\n");
      appendDirtyJavaScript("{\n");
      if (!Str.isEmpty(redirect_url))
         appendDirtyJavaScript("\tlocation.replace('"+redirect_url+"');\n");
      else
         appendDirtyJavaScript("\twindow.close();");
      appendDirtyJavaScript("}\n");


      return out;
   }


   private void appendInputField( AutoString out,
                                  ASPField column,
                                  int row_nr,
                                  String value,
                                  boolean has_lov ) throws FndException
   {
      ASPManager mgr = getASPManager();

      AutoString html = out;
      html.append("<input type=text name=",column.getName()," size=");
      html.appendInt(column.getSize());
      int max_len = column.getMaxLength();
      if(max_len>0)
      {
         html.append(" maxlength=");
         html.appendInt(max_len);
      }
      if(mgr.isExplorer())
      {
         String alignment = column.getAlignment();
         if(ASPField.ALIGN_RIGHT.equals(alignment))
            html.append(" style=\"text-align: right\"");
         else if(ASPField.ALIGN_CENTER.equals(alignment))
            html.append(" style=\"text-align: center\"");
      }

      if(mgr.isExplorer())
      {
         out.append(" CLASS=\"");
         out.append("grdCnt");
         out.append("\"");
      }

      html.append(" value=\"");
      if( !mgr.isEmpty(value) )
         html.append(getASPManager().HTMLEncode(value));
      html.append("\" ");

      if( has_lov )
         column.appendLOVTag(html,row_nr,false);
      html.append(">");
      printHiddenField("__"+column.getBlock().getName()+"_ROWSTATUS","New__");
   }

   private void appendDateField( AutoString out,
                                 ASPField column,
                                 int row_nr,
                                 String value,
                                 String mask ) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      AutoString html = out;
      html.append("<input type=text name=",column.getName()," size=");
      html.appendInt(column.getSize());
      int max_len = column.getMaxLength();
      if(max_len>0)
      {
         html.append(" maxlength=");
         html.appendInt(max_len);
      }
      if(mgr.isExplorer())
      {
         String alignment = column.getAlignment();
         if(ASPField.ALIGN_RIGHT.equals(alignment))
            html.append(" style=\"text-align: right\"");
         else if(ASPField.ALIGN_CENTER.equals(alignment))
            html.append(" style=\"text-align: center\"");
      }
      out.append(" class='editableTextField'");
      html.append(" value=\"");
      if( !mgr.isEmpty(value) )
         html.append(getASPManager().HTMLEncode(value));
      html.append("\" ");
      html.append(">");

      if (mask.indexOf("d") > 0) //For date and datetime fields
      {    
         ASPConfig cfg = mgr.getASPPage().getASPConfig();
         String _label = column.getName();
         String cal_image_border = cfg.getParameter("CALENDAR/IMAGE/BORDER","0");
         String cal_image_width  = cfg.getParameter("CALENDAR/IMAGE/WIDTH","13");
         String cal_image_height = cfg.getParameter("CALENDAR/IMAGE/HEIGHT","15");
         String path = mgr.getApplicationPath() + "/";
      
         html.append(" <a href=\"","javascript:showCalendar('");
         html.append(column.getName());
         html.append("','','',");
         html.append("''"); 
         html.append(",'",mask,"',");
         html.appendInt(row_nr);
         html.append(",'",_label,"'");
         html.append(");\">");
      
         html.append("<img src=\"",path,"common/images/calendar.gif\"");
         html.append(" width=");
         html.append(cal_image_width);
         html.append(" height=");
         html.append(cal_image_height);
         html.append(" border=");
         html.append(cal_image_border);
         html.append(" ");
         html.append(" alt=\"");
         html.append( mgr.translate("FNDCFFCALALT: Calendar"),"\"");
         html.append(" title=\"");
         html.append( mgr.translate("FNDCFFCALALT: Calendar"),"\"");
         html.append("></a>");
      }
      html.append("<font class=\"maskText\"> &nbsp;(");
      html.append(mask,") </font>");
     
      printHiddenField("__ITEM_ROWSTATUS","New__");
   }

   private void appendHiddenFields()
   {
      printHiddenField("__PARAM_ROWSTATUS","QueryMode__");
      printHiddenField("PARAM_NAME","");
      printHiddenField("PARAM_VALUE","");
      printHiddenField("PARAM_MANDATORY","");
      printHiddenField("DATE_FIELD","");

      int rows = paramset.countRows();
      //add hidden field for each row for ASPRowSet.store to work
      for (int i=0;i < rows; i++)
         printHiddenField("__PARAM_ROWSTATUS","New__");

      String editables = ",__PARAM_ROWSTATUS,PARAM_VALUE,";
      getASPManager().getASPPage().getASPContext().writeValue("__PARAM_COLUMNS",editables);
   }

   private void appendDefaultScript() throws FndException
   {
      appendDirtyJavaScript("function setDefaultValues()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\t"+default_script);
      appendDirtyJavaScript("}\n");
   }


   public void drawTable(AutoString out) throws FndException
   {
      int i,j, rows, cols;
      String str= "";
      String[] field_details;
      boolean is_date_field = false;
      String mask = "";

      // Create the table heading
      rows = itemset.countRows();
      out.append("\n<div class=borders style=\"overflow: auto; height:260\">\n");
      out.append("<table>");

         out.append("<tbody>\n<tr>");
         printHiddenField("__ITEM_ROWSTATUS","QueryMode__");
         printHiddenField("COLUMN_NAME","");
         printHiddenField("ITEM_NAME","");
         printHiddenField("VALUE","");
         // Create the table contents
         for (i=0;i < rows; i++)
         {
            out.append("<td>");
            printText(""+ (i+1));
            out.append("</td>");

            out.append("<td>");
            printReadLabel(itemset.getValueAt(i,"ITEM_NAME"));
            out.append("</td>");

            out.append("<td>");

            field_details = (String[]) field_list.elementAt(i);

            is_date_field = false;
            if ( field_details[7]!=null && field_details[7].indexOf("DATE") > -1)
            {
               is_date_field = true;    
               if (("DATE".equals(field_details[7])) || ("DATE/DATE".equals(field_details[7])))
                  mask = "yyyy-MM-dd";
               else if ("DATE/DATETIME".equals(field_details[7]))
                  mask = "yyyy-MM-dd hhhh:mm:ss";
               else
                  mask = "hhhh:mm:ss";
            }
            
            if (is_date_field)
               appendDateField(out,value_field,i+1,itemset.getValueAt(i,"VALUE"),mask); 
            else if ( field_details[2] != null || field_details[5] != null)
               appendInputField(out,value_field,i+1,itemset.getValueAt(i,"VALUE"),true);
            else
               appendInputField(out,value_field,i+1,itemset.getValueAt(i,"VALUE"),false);

            out.append("</td>");

            out.append("</tr><tr>");
         }
         out.append("</tr>\n</tbody>\n");
      out.append("</table>\n");
      out.append("</div>\n");

      String editables = ",__ITEM_ROWSTATUS,VALUE,";
      getASPManager().getASPPage().getASPContext().writeValue("__ITEM_COLUMNS",editables);
      //getASPManager().getASPPage().getASPContext().writeValue("__REPORT_ID",getASPManager().readValue("__REP_ID"));
   }

   private void drawParameterSheet(AutoString html) throws FndException
   {
      appendHiddenFields(); //add 0th level hidden field
      html.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
      html.append("<tr><td width=100%>");
      printText("FNDPARASHTENTERVALS: Enter value for defined parameters");
      html.append("</td></tr>\n");
      html.append("<tr><td width=100%>");
      drawParamOptions(html);
      html.append("</td><td valign=bottom>&nbsp;");
      printButton("DEFAULT","FNDSCHEDWIZARDRADDEFBUTT: Default","onclick=\"javascript:setDefaultValues()\"");
      html.append("&nbsp;</td></tr>\n");
      html.append("</table>\n");
      
      appendDirtyJavaScript("function validateDateField(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\tfld = getField_('PARAM_VALUE',i);\n");
      appendDirtyJavaScript("\tfld.value = getField_('DATE_FIELD',i).value;\n");
      appendDirtyJavaScript("}\n");
      
   }

   private void drawParamOptions(AutoString out) throws FndException
   {
      int i,j, rows, cols;
      String str= "";
      String[] field_details;
      
      ASPManager mgr = getASPManager();

      // Create the table heading
      rows = paramset.countRows();
      try
      {
         out.append("\n<div class=borders style=\"overflow: auto; height:"+(Integer.parseInt(DIALOG_HEIGHT) - 10)+"\">\n");
      }
      catch (NumberFormatException e)
      {
         out.append("\n<div class=borders style=\"overflow: auto; height:260\">\n");
      }
      out.append("<table>");

         out.append("<tbody>\n<tr>");
         
         // Create the table contents
         for (i=0;i < rows; i++)
         {
            //beginTableCell();
            out.append("<td>");
            printText(""+ (i+1));
            out.append("</td>");

            out.append("<td>");
            printReadLabel(paramset.getValueAt(i,"PARAM_NAME"));
            printHiddenField("PARAM_NAME",paramset.getValueAt(i,"PARAM_NAME"));
            out.append("</td>");

            out.append("<td>");
            String tag = "";
            if ("TRUE".equals(paramset.getValueAt(i,"PARAM_MANDATORY")))
               tag = "onChange='javascript:checkMandatoryValue("+(i+1)+");";
            
            if ("NUMBER".equals(paramset.getValueAt(i,"DATA_TYPE")))
            {
               if (Str.isEmpty(tag))
                  tag = "onChange='javascript:";

               tag += "checkNumberValue("+(i+1)+")";
            }
               
            if (!Str.isEmpty(tag)) tag +="'";
            
            printField("PARAM_VALUE",(paramset.getValueAt(i,"PARAM_VALUE")==null)?"":paramset.getValueAt(i,"PARAM_VALUE"),tag);
            if ("TRUE".equals(paramset.getValueAt(i,"PARAM_MANDATORY")))
               out.append(" *");
            
            if ("DATE".equals(paramset.getValueAt(i,"DATA_TYPE")))
            {
               out.append("<x");  // for the extra '>' from appendCalendarTag
               mgr.getASPField("DATE_FIELD").appendCalendarTag(out,i+1);
               out.append(">");              
               printReadLabel("&nbsp;&nbsp;("+mgr.getASPField("DATE_FIELD").getTranslatedMask()+")");
            }
            printHiddenField("DATE_FIELD","");
               
            out.append("</td>");

            out.append("</tr><tr>");
         }
         out.append("</tr>\n</tbody>\n");
      out.append("</table>\n");
      out.append("</div>\n");
   }

   private void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");

      if ("CUSTOM_EXP".equals(val))
      {
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd = trans.addCustomCommand("CUST_EXP","Batch_SYS.Check_Batch_Sched_Cust_Expr__");
         cmd.addParameter("CUSTOM_EXP",mgr.readValue("CUSTOM_EXP"));

         trans = mgr.perform(trans);

         mgr.responseWrite("valid_expression^");
      }

      mgr.endResponse();
   }
}
