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
 *  File        : ReportOrder.java
 *  Modified    :
 *    ASP2JAVA Tool  2001-Mar-02 - Created Using the ASP file ReportOrder.asp
 *    Mangala        2001-Mar-02 - Convert to WebKit 3.5
 *    Jacek P        2001-Mar-29 - Changed .asp to .page
 *    Mangala        2001-Apr-03 - Add functionality to read Report_id from query string
 *                                 and populate accordingly.
 *    Ramila H       2001-Aug-24 - Added functionality for LOVs.
 *    Ramila H       2001-Oct-11 - Removed select report alert.
 *    Mangala P      2002-Jan-04 - Added support for Report generator report.
 *    Mangala        2002-Nov-06 - Added support for custom reports.
 *    Ramila H       2003-Mar-06 - Bug id 36225 corrected. added combo boxes,
 *                                 removed unnecessary LOV images, set default values.    
 *    Ramila H       2003-Mar-06 - Fixed javascript error when the first row doesnt have a VIEW.
 *                                 Changed the LOV if..else if ..else javascript to swtich case.  
 *    Ramila H       2003-Mar-14 - Did code review and changed all trans = mgr.submit(trans) to perform.
 *    Ramila H       2003-Apr-18 - Added ReportOrder.page as the url for setLOV. (PO scanning problem).
 *    Mangala        2003-May-24 - Bug fix in execCustomFinReports,execReportGenReports
 *    Chandana D     2003-May-28 - Data presentaton modified for the new L&F.
 *    Ramila H       2003-Jun-04 - Changed selectbox to IIDLOV.
 *    Ramila H       2003-Aug-01 - Log id 1052, changed to wizard format. 
 *    Ramila H       2003-Aug-12 - Added check for EVENT_EXECUTOR type.
 *    Chandana D     2003-Aug-14 - Made changes to support NE4.x(win/linux)
 *    Ramila H       2003-Sep-03 - Added preview support according to project Bayonet. 
 *    Ramila H       2003-Sep-09 - changed module name in execCustomFinReport method. 
 *    Ramila H       2003-Sep-30 - Called createPdfReport according to layout_owner.
 *    Ramila H       2003-Oct-10 - disabled print/view depending on PRINT_SERVER ON/OFF.
 *    Ramila H       2003-Oct-14 - Call id 107478, Set language to application language if global is also empty. 
 *                                 Checked existance of pdf before creating pdf ('view ordered report').
 *    Ramila H       2004-04-15  - Bug id 43991, renamed job_id to schedule_id in preview()
 *    Chandana D     2004-May-12 - Updated for the use of new style sheets.
 *    Chandana d     2004-May-19 - Changed mgr.isNetscape6() to mgr.isMozilla().
 *    Chandana D     2004-Jun-10 - Removed all absolute URLs.
 *    Ramila H       2004-10-20  - Merged Bug 46730, implementation code.
 * ----------------------------------------------------------------------------
 * New Comments:
 * 2009/08/27 sumelk Bug 85428, Added appendInputNumberField(). Changed drawTable() & getParamList() to handle
 *                   NUMBER type parameters properly.
 * 2008/08/08 sumelk Bug 71108, Changed readFromContext(),writeToContext(),getParamList() & getReportProperties()
 *                   to handle date type CSV's properly.
 * 2008/06/24 sumelk Bug 74930, Added appendDateField() and changed getReportProperties(),drawTable() to show
 *                   the date/time picker and format mask for date fields.
 * 2007/08/01 sumelk Merged the corrections for Bug 66964, Corrected localization errors. 
 * 2007/02/23 rahelk Bug id 58590, placed form tag after startPresentation
 * 2007/02/16 rahelk Bug id 63594, Passed hardcoded number mask for NUMBER columns to remove client formatting
 * 2007/02/15 sadhlk Bug 63229, Modified drawTable(), predefine() to enable "what's this" functionality.
 * 2007/02/09 buhilk Bug 63433, Modified getParamList() to support new mscro csv's.
 * 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
 * 2007/01/12 10:07:24  buhilk
 * Bug 61612, Modified getReportProperties(), getParamList(), readFromContext() and writeToContext() methods to handle
 *            error for report parameters based on the fields query flags.
 *
 * 2006/08/08 07:57:42  buhilk
 * Bug 59442, Corrected Translatins in Javascript
 *
 * 2006/07/03 17:07:25  buhilk
 * Bug 58216, Fixed SQL Injection threats
 *
 *           2006/03/19 rahelk
 * call id 135280 - Added func to check the checkbox scheduled for report generator report
 *  
 *               2006/02/21 rahelk
 * call id 136188 - fixed bug saved url and params in context and read from the proper HTML hidden field
 *
 *               2006/02/21 rahelk
 * call id 133022 - redesigned online report executing functionality
 *
 *               2005/01/02 prralk
 * Converts GET to POST to fix preview in PDF issue.
 *
 * Revision 1.2  2005/11/10 04:34:58  rahelk
 * Online report execution for web implemented
 *
 * Revision 1.1  2005/09/15 12:38:01  japase
 * *** empty log message ***
 *
 * Revision 1.3  2005/04/08 06:05:37  riralk
 * Merged changes made to PKG12 back to HEAD
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
import ifs.fnd.*;
import ifs.fnd.util.Str;
import java.util.*;
import java.text.*;
import java.io.*;


public class ReportOrder extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ReportOrder");
   public static String DIALOG_HEIGHT = "270";
   public static String DIALOG_WIDTH = "545";
   
   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPBlock utilblk;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPTable itemtbl;
   private ASPCommandBar itembar;
   private ASPBlockLayout itemlay;
   private ASPInfoServices inf;
   private ASPContext ctx;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;
   
   private ASPBuffer report_buffer;

   private int step_no;
   private String rep_id;
   //private boolean skip_step0;
      
   private String report_id_list;
   private String user_where;   
   private String selected_index;
   private String report_title;
   private boolean new_report;
   private String lov_script;
   private Vector field_list;
   private ASPField value_field;
   
   private String result_key;
   
   private String custom_attr;
   private String parameter_attr;
   private String custom_url;
   private String iframe_width;
   private String iframe_height;
   
   private String forced_schedule_report;
   private ASPBuffer field_flags;
   private ASPBuffer field_types;
   
   //===============================================================
   // Construction
   //===============================================================
   public ReportOrder(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      
      readFromContext();

      if("PREVIOUS".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         step_no--; 
      }
      else if("NEXT".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         step_no++;
      }
      else if("FINISH".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         readStep1Values();
         orderReport();
         step_no = 2;
      }
      else if("VIEW".equals(mgr.readValue("__PRESSED_BUTTON")))
      {
         ASPBuffer buf = mgr.newASPBuffer();
         result_key = mgr.readValue("RESULT_KEY");
         buf.addBuffer("DATA").addItem("RESULT_KEY",result_key);
         
         callPrintDlg(buf,true);
      }
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("OPENWIN")) )
         preview();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         preview();
      
      if(! mgr.isEmpty(mgr.readValue("REPORT_ID")))
      {
         rep_id = mgr.readValue("REPORT_ID");
         //if ("FINREP_REP".equals(rep_id))
         //    execReportGenReport();
         //execCustomFinReport();
         //user_where = "REPORT_ID='" + rep_id + "'";
         getAvailableReports();
         //populateUserWhereListboxes();
         new_report = true;
         //skip_step0 = true;
         //step_no = 1;
         getReportProperties();
      }
      else if(step_no == 0) //select report step
      {
         getAvailableReports();
         readStep1Values();
      }
      else if(step_no == 1) //report parameter step
      {
         readStep0Values();

         custom_url = getCustomReportURL();

         if (mgr.isEmpty(custom_url))
            getReportProperties();
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
      
      step_no = ctx.readNumber("STEP_NO",0);
      //skip_step0 = ctx.readFlag("SKIP_STEP0", false);
      
      rep_id = ctx.readValue("REPORT_ID","");
      report_title = ctx.readValue("REPORT_TITLE","");

      custom_url = ctx.readValue("CUSTOM_URL","");
      custom_attr = ctx.readValue("CUSTOM_ATTR","");
      parameter_attr = ctx.readValue("REPORT_PARAM_ATTR","");
      field_flags = ctx.readBuffer("FIELD_FLAGS");
      field_types = ctx.readBuffer("FIELD_TYPES");
   }
   
   private void readStep0Values()
   {
      ASPManager mgr  = getASPManager(); 
      
      if(rep_id == null || !rep_id.equals(mgr.isEmpty(mgr.readValue("__REP_ID"))?"":mgr.readValue("__REP_ID")))
      {
         new_report = true;
         rep_id = mgr.readValue("__REP_ID");
      }
      else
         new_report = false;
      
      selected_index = mgr.readValue("__SELECTED_INDEX");
      report_title = mgr.readValue("__REPORT_TITLE");
   }
   
   private void  readStep1Values()
   {
      ASPManager mgr  = getASPManager();
      
      if (mgr.isEmpty(custom_url))
         itemset.changeRows();
      else
      {
         if (mgr.isEmpty(mgr.readValue("REPORT_PARAM_ATTR"))) return;

         parameter_attr = mgr.readValue("REPORT_PARAM_ATTR");
         custom_attr = mgr.readValue("CUSTOM_ATTR");
      }
   }
   
   private void writeToContext()
   {
      ASPManager mgr = getASPManager();
      ctx = mgr.getASPContext();
      
      ctx.writeNumber("STEP_NO",step_no);
      //ctx.writeFlag("SKIP_STEP0", skip_step0);

      ctx.writeValue("REPORT_ID",rep_id);
      ctx.writeValue("REPORT_TITLE",report_title);
      
      ctx.writeValue("CUSTOM_URL",custom_url);
      ctx.writeValue("CUSTOM_ATTR",custom_attr);
      ctx.writeValue("REPORT_PARAM_ATTR",parameter_attr);
      if(field_flags!=null)
         ctx.writeBuffer("FIELD_FLAGS", field_flags);
      if(field_types!=null)
         ctx.writeBuffer("FIELD_TYPES", field_types);
   }   

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   private void  getAvailableReports()
   {
      ASPManager mgr = getASPManager();

      if(new_report)
         itemset.clear();
      
      trans.clear();
      cmd = trans.addCustomCommand("REPORT_TITLES","Report_Definition_API.Enumerate_Available_Reports");
      cmd.addParameter("CLIENT_VALUES0"); //report titles
      cmd.addParameter("CLIENT_VALUES1"); //report ids
      cmd.addParameter("ENUMUSERWHERE");

      if (!mgr.isEmpty(rep_id) && mgr.isEmpty(report_title) )
      {
         cmd = trans.addCustomFunction("REPORT_TITLE","Report_Definition_API.Get_Translated_Report_Title","CLIENT_VALUES0");
         cmd.addParameter("CLIENT_VALUES1",rep_id); //report id
      }
      
      cmd = trans.addCustomFunction("FORCED_TITLE","Report_Definition_API.Get_Translated_Report_Title","CLIENT_VALUES0");
      cmd.addParameter("CLIENT_VALUES1","FINREP_REP"); //report generator report report id

      trans = mgr.perform(trans);

      report_buffer  = trans.getBuffer("REPORT_TITLES");
      report_id_list = trans.getValue("REPORT_TITLES/DATA/CLIENT_VALUES1");
      
      forced_schedule_report = trans.getValue("FORCED_TITLE/DATA/CLIENT_VALUES0"); 
      
      if (mgr.isEmpty(report_title))
         report_title = trans.getValue("REPORT_TITLE/DATA/CLIENT_VALUES0"); 
   }


   private void  rePopulateListboxes()
   {
      ASPManager mgr = getASPManager();
      report_title = mgr.readValue("REPORT_TITLE");      
   }

   private void  populateUserWhereListboxes()
   {
      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(report_id_list))
      {
         mgr.showAlert("FNDPAGESREPORTORDERNOREP: Requested Report not found");
         report_buffer = null;
      }
      else
      {
         report_title =trans.getValue("REPORT_TITLE/DATA/CLIENT_VALUES0");
         report_title = (report_title.length()>1)?report_title.substring(0,report_title.length()-1):""; 
      }
   }

   
   private void  orderReport()
   {
      ASPManager mgr = getASPManager();

      /*
      if (mgr.isEmpty(language))
      {         
         String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if ( mgr.isEmpty(def_lang) )
            language = mgr.getLanguageCode();
         else            
            language = def_lang;
      }
      */
      
      String report_attr = inf.itemValueCreate("REPORT_ID", rep_id)+
                           inf.itemValueCreate("LAYOUT_NAME", "")+
                           inf.itemValueCreate("LANG_CODE", "");

      
      String parameter_attr = getParamList();
      
      result_key = inf.onlineReportOrder(report_attr, parameter_attr);
   }


   private String getParamList()
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
            String column = itemset.getValue("COLUMN_NAME");
            String value = (itemset.getValue("VALUE")==null? "":itemset.getValue("VALUE"));
            String field_type = (field_types!=null)?field_types.getValue(column):"";

            if(mgr.isCSVName(value))
            {
               value = mgr.getCSVValue(value);

               if (("DATE".equals(field_type)) || ("DATE/DATE".equals(field_type)))
                  value = value.substring(0,10); 
            }

            if (("NUMBER".equals(field_type)) || ("NUMBER/NUMBER".equals(field_type)))
            {
               value = mgr.parseNumber("NUMBER_FIELD",value) +"";
               if ("NaN".equals(value))
                  value = 0+"";
            }

            String flags = (field_flags!=null)?field_flags.getValue(column):"";
            
            if(!Str.isEmpty(value) && !Str.isEmpty(flags) && value.indexOf(";")>0 && flags.indexOf("S")==2)
               mgr.showError(mgr.translate("FNDPAGESREPORTORDERVALERR: You may only specify one value for &1", itemset.getValueAt(i,"ITEM_NAME")));
            
            paramStr += column;
            paramStr += (char)31;
            paramStr += value;
            paramStr += (char)30;
            itemset.next();
         }
         itemset.clear();
      }
      return paramStr;
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   private void  getReportProperties()
   {
      ASPManager mgr = getASPManager();
      String properties_list;
      String properties_item;
      String properties_ref;
      String properties_col;
      String properties_qvalue = "";
      String properties_enumerate;
      String properties_flags = "";

      if(new_report)
         itemset.clear();
      
      trans.clear();

      cmd = trans.addCustomCommand("PROPERTIES","REPORT_DEFINITION_API.Get_Query_Properties_");
      cmd.addParameter("PROPERTIES_LIST_OUT");
      cmd.addParameter("REPORT_ID",rep_id);

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
            field_details = new String[7];
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
            properties_qvalue = (String)propertiesmap.get("QVALUE");

            field_details[0] = properties_col;   // field name
            field_details[1] = properties_item;  // field label also used as title for LOV
            field_details[4] = properties_qvalue; //default value
            field_details[6] = (String)propertiesmap.get("DATATYPE");
            
            if (!mgr.isEmpty(field_details[6]))
            {
               if(field_types==null)
                  field_types = mgr.newASPBuffer();
               field_types.addItem(properties_col, field_details[6]);
            }

            properties_ref=(String)propertiesmap.get("REF");
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
            
            properties_flags = (String)propertiesmap.get("QFLAGS"); 
               
            if (!mgr.isEmpty(properties_flags))
            {
               if(field_flags==null)
                  field_flags = mgr.newASPBuffer();
               field_flags.addItem(properties_col, properties_flags);
            }
            
            properties_enumerate=(String)propertiesmap.get("ENUMERATE");
            if (!mgr.isEmpty(properties_enumerate))   
            {
               if ( properties_enumerate.indexOf("(") > -1 )
                  properties_enumerate = properties_enumerate.substring(0,properties_enumerate.indexOf("("));
                  
               field_details[5] = mgr.replace(properties_enumerate,"..","."); //enumerate method
            }
            
            if(new_report)
            {
                itemset.addRow(mgr.newASPBuffer());
                itemset.setValue("REP_ID", rep_id);
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
               generateLOVScript( field_details[2],field_details[1],field_details[3],field_details[5],i+1,field_details[6]);
         }

         lov_script += "\n }\n";
         lov_script += "\n}\n";
      }
   }

   void generateLOVScript(String view, String title, String properties, String enum_method, int row_no, String type)
   {
      lov_script += "\ncase "+row_no+" :";
      lov_script += "\n      "+getLOVScript(view,title,properties,enum_method,row_no, type );
      lov_script += "\n      break;";
   }


   private String getLOVScript(String view, String title, String properties, String enum_method, int row_no, String type)
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

      if ("NUMBER".equalsIgnoreCase(type))
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
   
   
   private String getCustomReportURL()
   {
      ASPManager mgr = getASPManager();

      // Report Generator Reports.
      boolean rep_gen_rep = false;
      String enable_rep_gen = "";
      if ("FINREP_REP".equals(rep_id))
         mgr.redirectTo("ScheduledTaskWizard.page?METHOD_NAME="+ScheduledTask.SCHED_REP_METHOD_NAME+"&REPORT_ID=FINREP_REP");

      int rep_no=1;
      String cust_rep_id = mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");

      while(!"__BREAK".equals(cust_rep_id))
      {
         if (rep_id.equals(cust_rep_id))
         {
            iframe_width = mgr.getASPConfig().getParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_WIDTH","545");
            iframe_height = mgr.getASPConfig().getParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_HEIGHT","280");

            return mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/URL","");
         }

         rep_no++;
         cust_rep_id = mgr.getConfigParameter("PAYLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");
      }

      rep_no =1;
      cust_rep_id = mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");

      while(!"__BREAK".equals(cust_rep_id))
      {
         if (rep_id.equals(cust_rep_id))
         {
            iframe_width = mgr.getASPConfig().getParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_WIDTH","545");
            iframe_height = mgr.getASPConfig().getParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/IFRAME_HEIGHT","280");

            return mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/URL","");
         }

         rep_no++;
         cust_rep_id = mgr.getConfigParameter("GENLEW/CUSTOM_REPORT/REPORT"+rep_no+"/REPORT_ID","__BREAK");
      }

      return "";
   }

   
   
   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      utilblk = mgr.newASPBlock("HEAD");

      utilblk.addField("REPORT_ID");
      utilblk.addField("CLIENT_VALUES0");
      utilblk.addField("CLIENT_VALUES1");
      utilblk.addField("ENUMUSERWHERE");
      utilblk.addField("PROPERTIES_LIST_OUT");
      utilblk.addField("RESULT_KEY");
      utilblk.addField("NUMBER_FIELD","Number");
      
      itemblk = mgr.newASPBlock("ITEM");

      itemblk.addField("REP_ID")
      .setHidden();

      itemblk.addField("COLUMN_NAME")
      .setHidden();

      itemblk.addField("ITEM_NAME")
      .setSize(30)
      .setReadOnly()
      .unsetQueryable()
      .setLabel("FNDPAGESREPORTORDERITEM_NAME: Item");

      value_field = itemblk.addField("VALUE")
      .unsetQueryable()
      .setSize(40)
      .setLOV("ReportOrder.page")
      .setLabel("FNDPAGESREPORTORDERVALUE: Value");


      itemset = itemblk.getASPRowSet();
      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setEditable();

      itembar = mgr.newASPCommandBar(itemblk);
      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      inf = mgr.newASPInfoServices();
      inf.addFields(); 
      
      enableConvertGettoPost();
      
      setChildFrameNames("cust_rep_frame",true);
      
//      String script_loc = getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS","http://&(APPLICATION/DOMAIN)/&(APPLICATION/PATH)/common/scripts/");
//      includeJavaScriptFile(script_loc.substring(getASPConfig().getParameter("APPLICATION/LOCATION/ROOT").length()-1) + "OrderReportUtil.js");
   }

 
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESREPORTORDERTITLE: Order Report Wizard";
   }

   protected String getTitle()
   {
      return "FNDPAGESREPORTORDERTITLE: Order Report Wizard";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("FNDPAGESREPORTORDERTITLE: Order Report Wizard"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("FNDPAGESREPORTORDERTITLE: Order Report Wizard"));
      
      out.append("  <input type=\"hidden\" name=\"__REP_ID\" value=\""+rep_id+"\">\n");
      out.append("  <input type=\"hidden\" name=\"__REPORT_TITLE\" value=\""+report_title+"\">");
      out.append("  <input type=\"hidden\" name=\"__SELECTED_INDEX\" value=\""+selected_index+"\">");
      out.append("  <input type=\"hidden\" name=\"__PRESSED_BUTTON\">\n");
            
      String title = "";
      
      switch(step_no)
      {
          case 0:
             title =  mgr.translate("FNDPAGESREPORTORDERSTEP1: Step 1: Select report");
             break;
          case 1:
             title =  mgr.translate("FNDPAGESREPORTORDERSTEP2: Step 2: Properties for report - &1",report_title);
             break;
          case 2:
             title =  mgr.translate("FNDPAGESREPORTORDERSTEP5: Step 3: Report &1 Executed",report_title);
             break;
      }
      
      beginDataPresentation();
      drawSimpleCommandBar(title);
      
      out.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" height=\"100%\"");
      out.append(" class=pageFormWithBorder>\n");
      out.append(" <tr>");
      out.append("<td valign=top rowspan=2><img src=\""+getASPConfig().getImagesLocation()+"report_wizard.gif\"></td>");
      out.append("<td height=100%>&nbsp;&nbsp;</td>");
      out.append("<td width=100%>\n");
      
      if(step_no == 0) //select report combobox
      {
         out.append("<script language=javaScript>\nREPORT_NOT_SELECTED_MSG ='",mgr.translateJavaScript("FNDRPTORDWIZNOREPSELMSG: Please Select a Report"),"';\n");
         out.append("FORCED_SCHEDULED_REP ='"+forced_schedule_report+"';\n");
         out.append("</script>");
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

         printSelectBox("REPORT_TITLE",report_buffer,report_title,"onChange=\"javascript:validateReportid()\"");
         out.append("*</td>\n");
         out.append(" </tr>\n");

         out.append("<tr> \n");
         out.append("<td>\n");
         printCheckBox("FNDRPTODRWIZSCHED: Schedule","SCHEDULED","TRUE", false, "");
         out.append("</td>\n");
         out.append("</tr> \n");
         
         out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         printButton("NEXT","FNDRPTODRWIZNEXTBTN: Next","onClick=\"javascript:fromStep0to1(this.form)\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDSCHEDWIZARDTASKCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");
          
         appendDirtyJavaScript("SCHEDULED_WIZARD_URL ='ScheduledTaskWizard.page?METHOD_NAME="+ScheduledTask.SCHED_REP_METHOD_NAME+"&REPORT_ID=';\n");
      }   
      else if(step_no == 1) //parameter step
      {
         out.append("<table border=0 cellpadding=3 Class=\"BlockLayoutTable\" width=100% height="+DIALOG_HEIGHT+">\n");
         out.append("<tr><td id=ramila width=\"100%\" valign=top>");

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
            appendDirtyJavaScript("\t var attr = cust_rep_frame.getAttributeString();\n");
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

         if (mgr.isEmpty(custom_url))
            printButton("PREVIOUS","FNDRPTODRWIZPREVIOUSBTN: Previous","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='PREVIOUS';submit();\" ");
         else
            printButton("PREVIOUS","FNDRPTODRWIZPREVIOUSBTN: Previous","onClick=\"toStep('PREVIOUS');\" ");

         out.append("&nbsp;&nbsp;");

         if (mgr.isEmpty(custom_url))
            printButton("FINISH","FNDRPTODRWIZFINISHBTN: Execute Report","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='FINISH';submit();\" ");
         else
            printButton("FINISH","FNDRPTODRWIZFINISHBTN: Execute Report","onClick=\"javascript:toStep('FINISH');\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step1_cancel","FNDRPTODRWIZCANCELBTN: Cancel","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");

         if (!mgr.isEmpty(lov_script))
            appendDirtyJavaScript("\n"+lov_script+"\n");

      }
      else if(step_no == 2)
      {
         printHiddenField("RESULT_KEY",result_key);
         
         out.append("<table border=0 cellpadding=3 Class=\"BlockLayoutTable\" width=100% height="+DIALOG_HEIGHT+">\n");
         out.append(" <tr>\n");
         out.append("  <td valign=top>\n");
         if (!mgr.isEmpty(result_key))
            printWriteLabel(mgr.translate("FNDREPORTORDERFINISMSG1: Report '&1' has been executed successfully.",report_title));
         else
            printWriteLabel(mgr.translate("FNDREPORTORDERFINISERRMSG: Report '&1' has NOT been executed successfully.",report_title));
         
         out.append("</td>\n");
         out.append("</tr>\n");

         out.append("</table>\n");

         out.append("</td>\n");
         out.append("</tr> \n");

         out.append("<tr><td valign=top align=right colspan=2 width=100%>\n");
         printButton("VIEW","FNDRPTODRSTEP3EVIEW: View/Print Report","onClick=\"javascript:document.form.__PRESSED_BUTTON.value='VIEW';submit();\" "+(mgr.isEmpty(result_key)?"DISABLED":""));
         out.append("&nbsp;&nbsp;");
         printButton("START","FNDRPTODRSTEP3ORDERANOTHER: Order a New Report","onClick=\"javascript:window.location.href='"+mgr.getURL()+"'\" ");
         out.append("&nbsp;&nbsp;");
         printButton("step5_exit","FNDRPTODRSTEP3EXIT: Exit","onClick=\"javascript:cancel()\" ");
         out.append("&nbsp;");
         out.append("</td></tr> \n");

         out.append("</table>\n");
         
         
         
         /*
         printButton("VIEW","FNDRPTODRSTEP3EVIEW: View/Print Report","onclick='javascript:viewReport()'");
         out.append("</td><td>");
         out.append("&nbsp;&nbsp;");
         out.append("</td><td>");
         
         printButton("START","FNDRPTODRSTEP3ORDERANOTHER: Order a New Report","onClick=\"javascript:window.location.href='"+mgr.getURL()+"'\" ");
         out.append("</td><td>");
         out.append("&nbsp;&nbsp;");
         out.append("</td><td>");
         printButton("step5_exit","FNDRPTODRSTEP3EXIT: Exit","onClick=\"javascript:cancel()\" ");
         out.append("</td></tr></table>");
         out.append("   </td>\n");
         out.append(" </tr> \n");
         out.append("</table>\n"); 
          
         
         String timeout = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_TIME_OUT","20");
         
         appendDirtyJavaScript("\nfunction viewReport()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript(" window.status='", mgr.translate("FNDRPTODRGETREPDETIALS: Retrieving report details. Please wait.."),"';\n");
         appendDirtyJavaScript(" var result_key = document.form.RESULT_KEY.value;\n");
         appendDirtyJavaScript(" getReportDetails(result_key,0);\n");         
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("\nfunction getReportDetails(result_key,timeout){\n");
         appendDirtyJavaScript(" window.status = window.status + '.';\n");
         appendDirtyJavaScript(" r = __connect('"+mgr.getURL()+"?'+ \n");
         appendDirtyJavaScript("    'VALIDATE=Y&PREVIEW=RESULT_KEY&RESULT_KEY='+result_key+ \n");
         appendDirtyJavaScript("    '&REPORT_ID="+mgr.URLEncode(rep_id)+"'+\n");         
         appendDirtyJavaScript("    '&LAYOUT='+URLClientEncode(f.LAYOUT.value)+ \n");
         appendDirtyJavaScript("    '&LANGUAGE='+URLClientEncode(f.LANGUAGE.value)); \n");
         appendDirtyJavaScript(" previewReport(result_key,__getValidateValue(0),__getValidateValue(1),__getValidateValue(2),__getValidateValue(3),0); \n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("\nfunction previewReport(result_key,layout,lang,owner_type,print_job_id,timeout){\n");
         appendDirtyJavaScript(" window.status = window.status + '.';\n");
         appendDirtyJavaScript(" if (timeout*2>"+timeout+")\n");
         appendDirtyJavaScript(" { \n");
         String time_out_msg = mgr.translate("FNDRPTODRTIMEOUT: Operation timed out. Report could not be previewed at this time.");
         appendDirtyJavaScript("      alert('", time_out_msg,"');\n");
         appendDirtyJavaScript("      window.status='", time_out_msg,"';\n");
         appendDirtyJavaScript("      return;\n   }\n");
         appendDirtyJavaScript(" timeout++; \n");
         appendDirtyJavaScript("  __connect('"+mgr.getURL());
         appendDirtyJavaScript("?VALIDATE=Y&PREVIEW=READY&RESULT_KEY='+URLClientEncode(result_key)+'&OWNER_TYPE='+URLClientEncode(owner_type)+");
         appendDirtyJavaScript("'&PRINT_JOB_ID='+URLClientEncode(print_job_id)+");
         appendDirtyJavaScript("'&REPORT_ID="+mgr.URLEncode(rep_id)+"'+\n");      // used if layout is empty   
         appendDirtyJavaScript("'&LAYOUT='+URLClientEncode(layout)+");
         appendDirtyJavaScript("'&LANGUAGE='+URLClientEncode(lang));\n");
         appendDirtyJavaScript(" if (\"Y\" == __getValidateValue(0))\n");
         //appendDirtyJavaScript("    window.open('"+mgr.getURL()+"?VALIDATE=Y&PREVIEW=SHOW&RESULT_KEY='+URLClientEncode(result_key)+");
         appendDirtyJavaScript("    window.open('"+mgr.getURL()+"?OPENWIN=Y&PREVIEW=SHOW&RESULT_KEY='+URLClientEncode(result_key)+");
         appendDirtyJavaScript("'&OWNER_TYPE='+URLClientEncode(owner_type)+");
         appendDirtyJavaScript("'&PRINT_JOB_ID='+URLClientEncode(print_job_id)+");
         appendDirtyJavaScript("'&REPORT_ID="+mgr.URLEncode(rep_id)+"'+\n");    //used if layout is empty     
         appendDirtyJavaScript("'&LAYOUT='+URLClientEncode(layout)+");
         appendDirtyJavaScript("'&LANGUAGE='+URLClientEncode(lang),'_blank','status,resizable,menubar,scrollbars,width=790,height=500');\n");
         appendDirtyJavaScript(" else\n");
         appendDirtyJavaScript("    setTimeout('previewReport('+result_key+',\"'+layout+'\",\"'+lang+'\",\"'+owner_type+'\",\"'+print_job_id+'\",'+timeout+')',2000);\n");
         appendDirtyJavaScript("}\n");
          */

      }
      endDataPresentation(false);
     
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      
      if (!mgr.isEmpty(lov_script))
         appendDirtyJavaScript("\n"+lov_script+"\n");
      
      String script_loc = getASPConfig().getParameter("APPLICATION/LOCATION/SCRIPTS","/&(APPLICATION/PATH)/common/scripts/");
      String src_file = script_loc + "OrderReportUtil.js";

      out.append("<script language=javascript src="+src_file+"></script>\n");
      
      appendDirtyJavaScript("function cancel()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("\twindow.location.href='"+mgr.getConfigParameter("APPLICATION/LOCATION/NAVIGATOR")+"';\n");
      appendDirtyJavaScript("}\n");
      
      
      return out;
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
      
      if (rows < 1)
      {
         printText("FNDRPTNOPARAM: No parameters required for this report.");
         return;
      }
      
      out.append("\n<div class=borders style=\"overflow: auto; height:260\">\n");
      out.append("<table>");

         out.append("<tbody>\n<tr>");
         printHiddenField("__ITEM_ROWSTATUS","QueryMode__");
         printHiddenField("REP_ID","");
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
            if ( field_details[6]!=null && field_details[6].indexOf("DATE") > -1)
            {
               is_date_field = true;    
               if (("DATE".equals(field_details[6])) || ("DATE/DATE".equals(field_details[6])))
                  mask = "yyyy-MM-dd";
               else if ("DATE/DATETIME".equals(field_details[6]))
                  mask = "yyyy-MM-dd hhhh:mm:ss";
               else
                  mask = "hhhh:mm:ss";
            }
            
            if (is_date_field)
               appendDateField(out,value_field,i+1,itemset.getValueAt(i,"VALUE"),mask); 
            else if (field_details[6].indexOf("NUMBER") > -1)
              appendInputNumberField(out,value_field,i+1,itemset.getValueAt(i,"VALUE"),itemset.getValueAt(i,"ITEM_NAME"));
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
      getASPManager().getASPPage().getASPContext().writeValue("__REPORT_ID",getASPManager().readValue("__REP_ID"));
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
      out.append(" class='editableTextField'");
      html.append(" value=\"");
      if( !mgr.isEmpty(value) )
         html.append(getASPManager().HTMLEncode(value));
      html.append("\" ");

      if( has_lov )
         column.appendLOVTag(html,row_nr,false);
      html.append(">");
      printHiddenField("__ITEM_ROWSTATUS","New__");
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

   private void appendInputNumberField( AutoString out,
                                  ASPField column,
                                  int row_nr,
                                  String value,
                                  String label) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      AutoString html = out;
      
      label = label.substring(0,label.length()-1); // To remove the colon
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

      html.append(" OnChange=\"checkNumberValue_(this,'",label,"','')\">");
      printHiddenField("__ITEM_ROWSTATUS","New__");
   }

   public void  preview()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("PREVIEW");
      String report_id = mgr.readValue("REPORT_ID");
      String result_key = mgr.readValue("RESULT_KEY");
      String lang   = mgr.readValue("LANGUAGE");
      String layout = mgr.readValue("LAYOUT");
      String layout_type_owner = mgr.readValue("OWNER_TYPE");

      //Bug 46730, start 
      if (mgr.isEmpty(lang))
      {         
         String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if ( mgr.isEmpty(def_lang) )
            lang = mgr.getLanguageCode();
         else            
            lang = def_lang;
      }

      if (mgr.isEmpty(layout))
      {
         ASPCommand cmd = trans.addCustomFunction("DEFAULT_LAYOUT", "Report_Layout_Definition_API.Get_Default_Layout","f2");
         cmd.addParameter("f1", report_id);
         
         trans = mgr.perform(trans);
         
         layout = trans.getValue("DEFAULT_LAYOUT/DATA/f2");
      }
      //Bug 46730, end
      
      if ("RESULT_KEY".equals(val) )
      {
         if (!mgr.isEmpty(result_key))
         {
            String print_job_id = "";
            layout_type_owner = inf.getLayoutTypeOwner(report_id,layout);
            
            if ("PRINTSRV".equals(layout_type_owner) && !inf.isReportAvailableAsPdf(result_key,lang.toLowerCase()))
               inf.createPdfReport(result_key,layout,lang.toLowerCase());
            else if ("AGENT".equals(layout_type_owner) && !inf.isPDFContentAvailable(result_key,layout,lang.toLowerCase()))
               print_job_id = inf.createPdfReport(result_key,layout,lang.toLowerCase());
                        
            mgr.responseWrite(layout+"^"+lang+"^"+layout_type_owner+"^"+print_job_id+"^");
         }
         else
            mgr.responseWrite(layout+"^"+lang+"^"+"^^");
      }
      else if ("READY".equals(val))
      {
         if ("PRINTSRV".equals(layout_type_owner))
         {
            File report = new File(inf.getReportPDFFileName(result_key,lang));
            mgr.responseWrite(report.canRead()?"Y":"N");
         }
         else if ("AGENT".equals(layout_type_owner))
         {
            //Bug 46730, start
            if (!mgr.isEmpty(mgr.readValue("PRINT_JOB_ID")))
               mgr.responseWrite((!mgr.isEmpty(inf.getPDFContents("PRINT_JOB_ID=?","PRINT_JOB_ID^S^IN^"+mgr.readValue("PRINT_JOB_ID"))))?"Y":"N");
            else
               mgr.responseWrite(inf.isPDFContentAvailable(result_key,layout,lang.toLowerCase())?"Y":"N");
            //Bug 46730, end
         }
      }
      else if ("SHOW".equals(val))
      {
         if ("PRINTSRV".equals(layout_type_owner))
            inf.sendReportAsPdf( result_key,lang );
         else if ("AGENT".equals(layout_type_owner))
         {
            //Bug 46730, start
            if (!mgr.isEmpty(mgr.readValue("PRINT_JOB_ID")))
               inf.sendPDFContents("PRINT_JOB_ID=?","PRINT_JOB_ID^S^IN^"+mgr.readValue("PRINT_JOB_ID"));
            else
               inf.sendPDFContents(result_key,layout,lang.toLowerCase());
            //Bug 46730, end
            
         }
      }
      mgr.endResponse();
   }
   
   
}
