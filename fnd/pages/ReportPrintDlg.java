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
*  File        : ReportPrintDlg.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-02  - Created Using the ASP file ReportPrintDlg.asp
*    Chaminda O.    2001-03-03  - Modifications after converting to Java
*    Kingsly P.     2001-05-23  - Bug Correction.
*    Ramila H       2001-06-14  - Log id # 754 corrections.
*    Ramila H       2001-09-09  - Implemented code to send archived PDF by mail.
*    Suneth M       2001-12-10  - Changed startup() & getContents() to sort the printer name combo box.
*    Ramila H       2001-12-21  - Corrected PROD call id 42947.
*    Ramila H       2002-02-15  - Added translation key for preview button (bug id 27980).
*    Ramila H       2002-02-15  - Corrected bug id 27985, Title row not translated.
*    Ramila H       2002-02-18  - Converted code to 3.5.x track standerds.removed define method.
*    Suneth M       2003-01-02  - Added new localization tag.
*    Mangala P      2003-01-06  - Log #877:better support for viewing PDF based reports online.
*    Chandana D     2003-01-22  - Log id 942. Modified okFind() method.
*    Mangala P      2003-01-23  - Remove preview from Netscape 4x.Move scripts to the .js
*    Ramila H       2003-03-06  - Bug 34190. added nowrap to all <td>.
*    Suneth M       2003-03-07  - Changed preview() & getPreviewScript().
*    Suneth M       2003-07-29  - Log Id 1047, Changed startup() & getContents().
*    Ramila H       2003-09-05  - Added preview support according to bayonet project.
*    Ramila H       2003-09-09  - Corrected script to support preview in netscape 4.7
*    Ramila H       2003-10-10  - Added check in print to remove extra print_job when type is AGENT.
*    Chandana       2004-05-12  - Updated for the use of new style sheets.
*    Chandana D     2004-06-10 - Removed all absolute URLs.
* ----------------------------------------------------------------------------
* New Comments:
* 2010/08/17 sumelk Bug 92465, Changed refreshPrinters() script function in getPreviewScript().
* 2010/07/07 sumelk Bug 89480, Changed startup() to set the option values for printers select box correctly.
* 2010/03/22 aswilk Bug 89479, Modified print_report() to call rowset.changeRows() before accessing values in rowset.
* 2010/02/10 sumelk Bug 88916, Changed startup(), validate() & getContents() to fetch the printer list from the database by executing a query.
* 2009/07/03 sumelk Bug 83681, Changed startup() to fetch the email address correctly.
* 2008/12/23 dusdlk Bug 78562, Modified methods getPreviewScript(), drawTable(AutoString out), okFind(), preDefine(), preview() and print_report(). 
* 2008/01/29 vohelk Bug 69330, Modified methods preview(), print_report()
* 2007/03/06 buhilk Bug 63950, disabled application support
* 2007/01/30 mapelk Bug 63250, Added theming support in IFS clients.
*               2006/08/21 gegulk
* Bug id 60018 - changed the occurences of WIDTH: to width:, in-order to avoid 
* them appearing as translation constants
*               2006/08/08 buhilk
* Bug 59442, Corrected Translatins in Javascript
*               2006/07/12 gegulk
* Bug id 59203 - Modified startup(),getContents() & getPreviewScript()
*               2006/04/26 rahelk
* Bug id 57022 - Added send mail functionality to AGENT type reports
*               2006/01/20 rahelk
* call id 130914 fixed - Redesigned dialog to support multiple reports
*
*               2006/01/02 prralk
* Converts GET to POST to fix preview in PDF issue.
* Revision 1.2  2005/09/22 12:39:23  japase
* Merged in PKG 16 changes
*
* Revision 1.1  2005/09/15 12:38:01  japase
* *** empty log message ***
*
* Revision 1.3.6.1  2005/08/30 08:36:34  rahelk
* passed default user language or site language when language is empty
*
* Revision 1.3  2005/04/08 06:05:38  riralk
* Merged changes made to PKG12 back to HEAD
*
* Revision 1.2  2005/02/02 08:22:19  riralk
* Adapted global profile functionality to new profile changes
*
* Revision 1.1  2005/01/28 18:07:27  marese
* Initial checkin
*
* Revision 1.3  2005/01/18 10:50:36  sumelk
* Changed startup() to select the default language.
*
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;

import java.io.File;


public class ReportPrintDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.ReportPrintDlg");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPForm frm;
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPBlockLayout lay;
   private ASPCommandBar bar;
   private ASPTable tbl;
   private ASPBlock h;
   private ASPInfoServices inf;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String result_key;
   private ASPCommand cmd;
   private String printer;
   private String title;
   private String title_bar;
   private String multiple_reports;
   private String archivate_value;
   private String archivate_box;
   private String print_job_id;
   private String seq_keys;
   private ASPQuery q;
   private String seq_key;
   private String printers;
   private String new_window;
   private boolean close_window;

   private String visibility;
   private String archivate;
   private String send_pdf;
   private String send_pdf_to;
   private String smtp_mail_address;
   private String report_id;
   private String setExcelReport;

   private String all_printers;
   private String logical_printers;
   private String default_printer;
   private String[] all_printers_arr;
   private String[] logical_printers_arr;

   //===============================================================
   // Construction
   //===============================================================
   public ReportPrintDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   private String getPreviewScript()
   {
      AutoString script = new AutoString();
      ASPManager mgr = getASPManager();
      String path = mgr.getPath();
      String time_out = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_TIME_OUT","25");

      if (path.indexOf("?")>0)
         path += path.endsWith("?")?"":"&";
      else
         path += "?";


      script.append("function createReport(result_key, i)\n{\n");
      script.append("   window.status='", mgr.translateJavaScript("FNDREPPRNDLGCREPPREMSG: Previewing report. Please wait.."),"';\n");
      script.append("  c =  __connect(\n      '",
                    path,"VALIDATE=Y&PREVIEW=CREATE&LANGUAGE='+SelectURLClientEncode('LANG_CODE',i)+'&LAYOUT='+SelectURLClientEncode('LAYOUT_NAME',i)+'&RESULT_KEY=' + URLClientEncode(result_key)+'&LOCALE='+SelectURLClientEncode('LOCALE_INFO',i));\n");
      script.append("  setTimeout('previewReport('+ result_key +',0,'+i+','+__getValidateValue(0)+')',2000);\n}\n");

      script.append("function previewReport(result_key,timeout,i,print_job_id)\n{\n");
      script.append("   window.status = window.status + '.';\n");
      script.append("   if (timeout*2>",time_out,")\n   {\n");
      String time_out_msg = mgr.translateJavaScript("FNDREPPRNDLGTIMEOUT: Preview operation timed out. Please try again later.");
      script.append("      alert('", time_out_msg,"');\n");
      script.append("      window.status='", time_out_msg,"';\n");

      script.append("      return;\n   }\n");
      script.append("   timeout++;\n");

/*script.append("\nalert('create report:'+result_key+' i:'+i);");      
script.append("\nalert('lang:'+SelectURLClientEncode('LANG_CODE',i));");
script.append("\nalert('LAYOUT_NAME:'+SelectURLClientEncode('LAYOUT_NAME',i));");
script.append("\nalert('REPORT_ID:'+getValue_('REPORT_ID',i));");      
  */    
      
      script.append("   __connect(\n   '",
              path,"VALIDATE=Y&PREVIEW=READY&LANGUAGE='+SelectURLClientEncode('LANG_CODE',i)+'&LAYOUT='+SelectURLClientEncode('LAYOUT_NAME',i)+'&RESULT_KEY=' + URLClientEncode(result_key)+'&REPORT_ID='+URLClientEncode(getValue_('REPORT_ID',i))+'&PRINT_JOB_ID=' + URLClientEncode(print_job_id) );\n");
      script.append("   var id = __getValidateValue(0);\n");
      script.append("   if (id !='')\n");
      //script.append("      showNewBrowser('",path,"VALIDATE=Y&PREVIEW=SHOW&LANGUAGE='+SelectURLClientEncode('LANGUAGES',-1)+'&LAYOUT='+SelectURLClientEncode('LAY_OUT',-1)+'&RESULT_KEY=' + URLClientEncode(result_key)+'&REPORT_ID='+URLClientEncode(getValue_('REPORT_ID',-1)));\n");
      script.append("      showNewBrowser('",path,"OPENWIN=Y&PREVIEW=SHOW&LANGUAGE='+SelectURLClientEncode('LANG_CODE',i)+'&LAYOUT='+SelectURLClientEncode('LAYOUT_NAME',i)+'&RESULT_KEY=' + URLClientEncode(result_key)+'&REPORT_ID='+URLClientEncode(getValue_('REPORT_ID',i))+'&ID='+URLClientEncode(id)+'&EXCEL_REPORT="+setExcelReport+"');\n");
      
      script.append("   else\n      setTimeout('previewReport('+result_key+','+timeout+','+i+','+print_job_id+')',2000);\n");
      script.append("}\n\n");


      script.append("function validateAll()\n");
      script.append("{\n");
      script.append("   document.form.FROM_PAGE.value = \"\";\n");
      script.append("   document.form.TO_PAGE.value = \"\";   \n");
      script.append("}\n");
      script.append("function validateToPage()\n");
      script.append("{\n");
      script.append("   if (document.form.PRINT_OP[1].checked)\n");
      script.append("   {\n");
      script.append("      strto = document.form.TO_PAGE.value;\n");
      script.append("      strfrom = document.form.FROM_PAGE.value;\n");
      script.append("      to = document.form.TO_PAGE.value * 1;\n");
      script.append("      from = document.form.FROM_PAGE.value * 1;\n");
      script.append("      if (strto.length > 0 && strfrom.length > 0)\n");
      script.append("      {\n");
      script.append("         if ((to < from) || (to < 1) || (from < 1))\n");
      script.append("         {\n");
      script.append("            alert(document.form.INVALID_PAGE_NUMBERING.value);\n");
      script.append("            document.form.TO_PAGE.focus();\n");
      script.append("         } \n");
      script.append("       }\n");
      script.append("       else\n");
      script.append("       {\n");
      script.append("          alert(document.form.INVALID_PAGE_NUMBERING.value);\n");
      script.append("          document.form.TO_PAGE.focus();\n");
      script.append("       }\n");
      script.append("   }\n");
      script.append("}\n");
      script.append("function blurIfMultiple(obj)\n");
      script.append("{\n");
      script.append(" if (document.form.MULTIPLE_REPORTS.value==\"TRUE\")\n");
      script.append("    obj.blur();\n");
      script.append("}\n");
      script.append("function sameIfMultiple(obj)\n");
      script.append("{\n");
      script.append(" if (document.form.MULTIPLE_REPORTS.value==\"TRUE\")\n");
      script.append("    obj.selectedIndex = 0;\n");
      script.append("} \n");
      script.append("function selectAllPages()\n");
      script.append("{\n");
      script.append(" if (document.form.MULTIPLE_REPORTS.value==\"TRUE\")\n");
      script.append("    document.form.PRINT_OP[0].click();\n");
      script.append("}\n");


      script.append("function validateEMail(this_obj)\n");
      script.append("{\n");
      script.append("   if (!document.form.ARC_OPTIONS[1].checked)\n");
      script.append("      this_obj.blur();\n");
      script.append("}\n");

      script.append("function validateOption0(obj)\n");
      script.append("{\n");
      script.append("   if (!document.form.ARCHCHKBOX.checked)\n");
      script.append("       document.form.ARC_OPTIONS[0].checked = false;\n");
      script.append("   obj.blur(); \n");
      script.append("}\n");

      script.append("function validateOption1(obj)\n");
      script.append("{\n");
      script.append("   if (!document.form.ARCHCHKBOX.checked) {\n");
      script.append("       document.form.ARC_OPTIONS[1].checked = false;\n");
      script.append("       obj.blur(); \n");
      script.append("   } else \n");
      script.append("       assignEMail(); \n");
      script.append("}\n");


      script.append("function assignEMail()\n");
      script.append("{\n");
      script.append("   document.form.EMAILDISPLAY.value = document.form.EMAIL.value;\n");
      script.append("}\n");

      String printer_changed = mgr.translateJavaScript("FNDWEBREPORTPRINTDLGPRINTERCHANGED: Selected printer changed according to the language");
   /*   if (logical_printers==null)
      {
         script.append("document.form.PRINTERS.checked = true;\n");
         script.append("document.form.PRINTERS.disabled = true;\n");
      }*/
      script.append("function refreshPrinters(lang_code,report_id,all_printers)\n");
      script.append("{\n");      
      script.append("   __connect(APP_ROOT+ 'common/scripts/ReportPrintDlg.page'+'?VALIDATE=REFPRN&LANG_CODE='+lang_code+'&REPORT_ID='+report_id+'&ALL_PRINTERS='+all_printers);\n");
     // script.append("   document.form.PRINTERS.checked = false;\n");
     // script.append("   document.form.PRINTERS.disabled = false;\n");
      script.append("      var new_default_printer;\n");
      script.append("      var new_logical_printer_arr;\n");		
      
      script.append("   if ((last_response_script[0] !='null') && \n");
      script.append("       (last_response_script[1] !='null')) \n");    
      script.append("   {\n");
      script.append("      new_default_printer = last_response_script[0];\n");
      script.append("      new_logical_printer_arr = last_response_script[1].split(FIELD_SEPARATOR);\n");		
      script.append("      var selected_printer = document.form.SELECT_PRINTERS.value;\n");
      script.append("      document.form.SELECT_PRINTERS.options.length=0;\n");
      script.append("      if(selected_printer == default_printer)\n");
      script.append("      {\n");
      script.append("         for(i=0;i<new_logical_printer_arr.length;i++)\n");
      script.append("         {\n");
      script.append("            if(new_logical_printer_arr[i]!='')\n");
      script.append("            {\n");
      script.append("               if(new_logical_printer_arr[i]==new_default_printer)\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i],true,true);\n");
      script.append("               else\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i]);\n");
      script.append("            }\n");
      script.append("         }\n");
      script.append("      }\n");
      script.append("      else if (exists(new_logical_printer_arr,selected_printer))\n");
      script.append("      {\n");
      script.append("         for(i=0;i<new_logical_printer_arr.length;i++)\n");
      script.append("         {\n");
      script.append("            if(new_logical_printer_arr[i]!='')\n");
      script.append("            {\n");
      script.append("               if(new_logical_printer_arr[i]==selected_printer)\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i],true,true);\n");
      script.append("               else\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i]);\n");
      script.append("            }\n");
      script.append("         }\n");
      script.append("      }\n");
      script.append("      else\n");
      script.append("      {\n");  
      script.append("         for(i=0;i<new_logical_printer_arr.length;i++)\n");
      script.append("         {\n");
      script.append("            if(new_logical_printer_arr[i]!='')\n");
      script.append("            {\n");
      script.append("               if(new_logical_printer_arr[i]==new_default_printer)\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i],true,true);\n");
      script.append("               else\n");
      script.append("                  document.form.SELECT_PRINTERS.options[i] = new Option(new_logical_printer_arr[i],new_logical_printer_arr[i]);\n");
      script.append("            }\n");
      script.append("         }\n");
      script.append("         alert('"+printer_changed+"');\n");
      script.append("      }\n");
      script.append("   }\n");
      script.append("   else if ((last_response_script[0] !=null))\n");	     
      script.append("   {\n");
      script.append("      new_default_printer = last_response_script[0];\n");		
      script.append("      var selected_printer = document.form.SELECT_PRINTERS.value;\n");
      script.append("      document.form.SELECT_PRINTERS.options.length=0;\n");
      script.append("      if(selected_printer == default_printer)\n");
      script.append("      {            \n");
      script.append("         document.form.SELECT_PRINTERS.options[0] = new Option(new_default_printer,new_default_printer);\n");	
      script.append("      }\n");
      script.append("      else\n");
      script.append("      {\n");
      script.append("         document.form.SELECT_PRINTERS.options[0] = new Option(new_default_printer,new_default_printer);\n");	
      script.append("         alert('"+printer_changed+"');\n");
      script.append("      }\n");
      script.append("      logical_printers_arr = null;\n");
      script.append("      default_printer = new_default_printer;\n");
      script.append("   }\n");
      
      script.append("   if ((new_logical_printer_arr !=null) &&\n");	     
      script.append("       (new_default_printer !=null)) \n");	     
      script.append("   {\n");      
      script.append("      logical_printers_arr = new Array();\n");
      script.append("      for(i=0;i<new_logical_printer_arr.length;i++)\n");
      script.append("      {\n");
      script.append("         if(new_logical_printer_arr[i]!='')\n");
      script.append("         {\n");
      script.append("            logical_printers_arr[i] = new_logical_printer_arr[i];\n");
      script.append("         }\n");
      script.append("      }\n");      
      script.append("      default_printer = new_default_printer;\n");	
      script.append("   }\n");
      script.append("	else if (new_default_printer !=null)\n");
      script.append("   {\n");
      script.append("      logical_printers_arr = null;\n");
      script.append("      default_printer = new_default_printer;\n");
      script.append("   }\n");
      script.append("	else \n");
      script.append("   {\n");
      script.append("      logical_printers_arr = null;\n");
      script.append("      default_printer = null;\n");
      script.append("      document.form.PRINTERS.checked = true;\n");
      script.append("      refreshPrinters(lang_code,report_id,document.form.PRINTERS.checked);\n");
      script.append("   }\n");
      script.append("	if ((last_response_script[2] !=null))\n");
      script.append("   {\n");
      script.append("      document.form.PRINTERS.checked = all_printers;\n");
      script.append("   }\n");
      script.append("}\n");
     
      return script.toString();
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      mgr.setPageNonExpiring();
      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();

      result_key = mgr.readValue("RESULT_KEY", "");
      new_window = ctx.readValue("__NEWWINDOW",mgr.getQueryStringValue("__NEWWIN"));
      close_window = false;

      if ( mgr.buttonPressed("OK") )
         print_report();
      else if ( mgr.buttonPressed("CANCEL") )
         cancel();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("OPENWIN")) )
         preview();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else
         okFind();
      startup();
      
      ctx.writeValue("__NEWWINDOW",new_window);
   }

//=============================================================================
//  Utility functions
//=============================================================================

   public void  startup()
   {
      ASPManager mgr = getASPManager();
      String layout  = null;
      //to initialize values...

      trans.clear();

      trans.addQuery("ALL_PRINTERS", "SELECT Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id,Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id FROM LOGICAL_PRINTER").setBufferSize(2000);
      if (mgr.isModuleInstalled("APPSRV")) 
      {
         cmd = trans.addCustomFunction("LOGICAL_PRINTERS","PRINTER_CONNECTION_API.Get_Logical_Printers__","CLIENT_VALUES1");
         cmd.addParameter("FND_USER",mgr.getUserId());
         cmd.addParameter("REPORT_ID", rowset.countRows()>0 ? rowset.getValue("REPORT_ID") : "");
         cmd.addParameter("LANG_CODE",mgr.getLanguageCode());
         
         cmd = trans.addCustomFunction("DEFAULT_PRINTER","PRINTER_CONNECTION_API.Get_Default_Logical_Printer","DEFAULTPRINTER");
         cmd.addParameter("FND_USER",mgr.getUserId());
         cmd.addParameter("REPORT_ID", rowset.countRows()>0 ? rowset.getValue("REPORT_ID") : "");
         cmd.addParameter("LANG_CODE",mgr.getLanguageCode());
      }   

      /*
      cmd = trans.addCustomCommand("GCLAS", "Report_Definition_API.Get_Class_Info");
      cmd.addParameter("REPORT_ATTR_");
      cmd.addParameter("COLUMN_PROPERTIES_");
      cmd.addParameter("TEXT_PROPERTIES_");
      cmd.addParameter("LAYOUT_PROPERTIES_");
      cmd.addParameter("LANG_PROPERTIES_");
      cmd.addParameter("REPORT_ID", rowset.countRows()>0 ? rowset.getValue("REPORT_ID") : "");
       */

      cmd = trans.addCustomCommand("ARCHIVATE", "Print_Server_SYS.Get_Archivate_Property__");
      cmd.addParameter("ARCHIVATE_");

      cmd = trans.addCustomFunction("SMPT_MAIL","Fnd_User_Property_API.Get_Value","SMTP_MAIL_ADDRESS");
      cmd.addInParameter("FND_USER",mgr.getFndUser()); 
      cmd.addInParameter("MAIL_PROPERTIES","SMTP_MAIL_ADDRESS");

      //trans = mgr.submit(trans);
      trans = mgr.perform(trans);

      logical_printers = trans.getValue("LOGICAL_PRINTERS/DATA/CLIENT_VALUES1");
      default_printer = trans.getValue("DEFAULT_PRINTER/DATA/DEFAULTPRINTER");

      if (logical_printers != null)
      {
         logical_printers_arr = Str.split(logical_printers,String.valueOf(IfsNames.fieldSeparator));
      }
      


      /*
      String languages = inf.getLanguageValues(trans.getValue("GCLAS/DATA/LANG_PROPERTIES_"));
      String layouts = inf.getLayoutValues(trans.getValue("GCLAS/DATA/LAYOUT_PROPERTIES_"));
       */


      int j  = rowset.countRows();
      title = "";
      report_id = "";
      title_bar = "";
      if ( j > 1 )
      {
         multiple_reports = "TRUE";
         rowset.first();
         title_bar = mgr.translate("FNDPAGESREPORTPRINTDLGMULTIPLEREPORT: Multiple Reports");
         for (int i = 0; i < j; i++ )
         {
            title = title + rowset.getValue("REPORT_TITLE") + "\n";
            rowset.next();
         }
         rowset.first();
      }
      else
      {
         multiple_reports = "FALSE";
         title =  rowset.getValue("REPORT_TITLE");
         report_id =  rowset.getValue("REPORT_ID");
         layout = mgr.readValue("LAYOUT");
         title_bar = title;
         
         String layout_type = inf.getLayoutType(report_id,layout);
         if ("EXCEL".equals(layout_type))
            setExcelReport = "TRUE";
         else
            setExcelReport = "FALSE";         
      }

      /*
      String lay_out = inf.getOptions(inf.getTokenAt(layouts, 2), inf.getTokenAt(layouts, 1));
      
      if (mgr.isEmpty(language))
      {
         String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if ( mgr.isEmpty(def_lang) )
            language = mgr.getLanguageCode();
         else
            language = def_lang;
      }
      languages = inf.getOptions(inf.getTokenAt(languages, 2), inf.getTokenAt(languages, 1),language,false);
       */
      // languages = inf.getOptions(inf.getTokenAt(language, 2), inf.getTokenAt(language, 1));
      // printers = inf.getOptions(inf.getTokenAt(printer, 1), inf.getTokenAt(printer, 1));

      archivate_value = trans.getValue("ARCHIVATE/DATA/ARCHIVATE_");
      //archivate_box = inf.getArchivateCheckBox(archivate_value);
      archivate_box = getArchivateCheckBox(archivate_value);

      if ( "ALWAYS ON".equals(archivate_value) )
         archivate_value = "TRUE";
      else if ( "OFF".equals(archivate_value) )
         archivate_value = "FALSE";
      else
         archivate_value = "CHECK";

      smtp_mail_address ="";
      smtp_mail_address = trans.getValue("SMPT_MAIL/DATA/SMTP_MAIL_ADDRESS");

      if ( mgr.isEmpty(smtp_mail_address) )
         smtp_mail_address ="";
      
      result_key = rowset.getValue("RESULT_KEY"); 
   }


   /**
    * Return HTML codes for check box.
    * @param strArchivate Archivate value. This value decides the apearence of the check box.
    * <pre>
    * "ALWAYS ON"  - to show read only check box which is checked.
    * "OFF"        - to show read only check box which is not checked.
    * "DEFAULT ON" - to show check box which is default checked.
    * any other    - to show check box which is default unchecked.
    * </pre>
    */
   public String getArchivateCheckBox(String strArchivate)
   {
      ASPManager mgr = getASPManager();

      //visibleshow = "visible";
      //visiblehide = "hidden";

      if ( "ALWAYS ON".equals(strArchivate) ) // to show read only check box which is checked
      {
         visibility = "visible";
         archivate = "TRUE";
         return("&nbsp;<IMG SRC=\"../images/check_box_marked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      }
      else if ( "OFF".equals(strArchivate) )// to show read only check box which is not checked
      {
         visibility = "hidden";
         archivate = "FALSE";
         return("&nbsp;<IMG SRC=\"../images/check_box_unmarked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      }
      else if ( "DEFAULT ON".equals(strArchivate) )// to show check box which is default checked
      {
         visibility = "visible";
         archivate = "TRUE";
         return fmt.drawCheckbox("ARCHCHKBOX","ON",true,"onClick='javascript:showOptions()'");
      }
      else
      { // to show check box which is default unchecked
         visibility = "hidden";
         archivate = "FALSE";
         return fmt.drawCheckbox("ARCHCHKBOX","ON",false,"onClick='javascript:showOptions()'");
      }
   }


   public void  reassignValues()
   {
      ASPManager mgr = getASPManager();

      print_job_id = mgr.readValue("PRINT_JOB_ID");
      seq_keys = mgr.readValue("SEQ_KEYS");
   }

//=============================================================================
//  Command Bar Search Group functions
//=============================================================================
 
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      int rows = 1;
    
      trans.clear();
      rowset.clear();
      
      if ( mgr.dataTransfered() ) //transfered data found
      { 
         ASPBuffer data_trans = mgr.getTransferedData() ;
         result_key = data_trans.getValue("DATA/RESULT_KEY");

         if ( data_trans.itemExists("PRINT_JOB") )
         { //"print job id is found in the transfered data"
            print_job_id = data_trans.getValue("PRINT_JOB");
            seq_keys = data_trans.getValue("SEQ_DATA");
            data_trans.removeItem("PRINT_JOB");   // We should remove "print job id" before adding to Query
            data_trans.removeItem("SEQ_DATA");
         }
         else
         { // "print job id not found"
            print_job_id = "";
            seq_keys = "";
         }
         
         rows = data_trans.countItems();
         for (int i=0; i<rows; i++)
         {
            ASPBuffer buf = data_trans.getBufferAt(i);
            String result_key = buf.getValueAt(0);
            
				ASPBuffer row_buf = mgr.newASPBuffer();      
            row_buf.addItem("RESULT_KEY", result_key);
            row_buf.addItem("COPIES", "1");
            rowset.addRow(row_buf);
            
            cmd = trans.addCustomCommand("INSTANCE_INFO_"+i, "archive_api.get_instance_info");
            cmd.addParameter("CLIENT_VALUES0"); //INSTANCE_ATTR_
            cmd.addParameter("CLIENT_VALUES1"); //PARAMETER_ATTR_
            cmd.addParameter("CLIENT_VALUES2"); //VARIABLE_ATTR_
            cmd.addParameter("RESULT_KEY",result_key); //RESULT_KEY
         }
         
      }
      else
      {
         print_job_id = mgr.readValue("PRINT_JOB_ID");
         seq_keys = "";
         result_key = mgr.readValue("RESULT_KEY");
         
         ASPBuffer row_buf = mgr.newASPBuffer();      
         row_buf.addItem("RESULT_KEY", result_key);
         row_buf.addItem("COPIES", "1");
         rowset.addRow(row_buf);
         
         cmd = trans.addCustomCommand("INSTANCE_INFO_0", "archive_api.get_instance_info");
         cmd.addParameter("CLIENT_VALUES0"); //INSTANCE_ATTR_
         cmd.addParameter("CLIENT_VALUES1"); //PARAMETER_ATTR_
         cmd.addParameter("CLIENT_VALUES2"); //VARIABLE_ATTR_
         cmd.addParameter("RESULT_KEY",result_key);
      }
      
      ASPBuffer buf = mgr.perform(trans);
      
      trans.clear();
      
      for (int i=0; i<rows; i++)
      {
         
         String instance_attr =  buf.getValue("INSTANCE_INFO_"+i+"/DATA/CLIENT_VALUES0");

         String rep_id = inf.getTokenValue(instance_attr,"REPORT_ID");
         rowset.setValueAt(i, "REPORT_ID", rep_id);
         rowset.setValueAt(i, "REPORT_TITLE", inf.getTokenValue(instance_attr,"REPORT_TITLE"));
         rowset.setValueAt(i, "LAYOUT_NAME", inf.getTokenValue(instance_attr,"LAYOUT_NAME"));
         
         String language = inf.getTokenValue(instance_attr,"LANG_CODE");
         if (mgr.isEmpty(language))
         {
            String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
            if ( mgr.isEmpty(def_lang) )
               language = mgr.getLanguageCode();
            else
               language = def_lang;
         }

         rowset.setValueAt(i, "LANG_CODE", language);
         rowset.setValueAt(i, "NOTES", inf.getTokenValue(instance_attr,"NOTES"));
         
         cmd = trans.addCustomCommand("GCLAS_"+i, "Report_Definition_API.Get_Class_Info");
         cmd.addParameter("REPORT_ATTR_");
         cmd.addParameter("COLUMN_PROPERTIES_");
         cmd.addParameter("TEXT_PROPERTIES_");
         cmd.addParameter("LAYOUT_PROPERTIES_");
         cmd.addParameter("LANG_PROPERTIES_");
         cmd.addParameter("REPORT_ID", rep_id);
         
      }
      
      trans = mgr.perform(trans);
      
      for (int i=0; i<rows; i++)
      {
         rowset.setValueAt(i,"LAYOUT_PROPERTIES_",inf.getLayoutValues(trans.getValue("GCLAS_"+i+"/DATA/LAYOUT_PROPERTIES_")));
         rowset.setValueAt(i,"LANG_PROPERTIES_",inf.getLanguageValues(trans.getValue("GCLAS_"+i+"/DATA/LANG_PROPERTIES_")));
      }
      
      trans.clear();
      
      // Locale Information
      for (int i=0; i<rows; i++)
      {
         String enum_language = mgr.getUserLocale();
        
         rowset.setValueAt(i, "LOCALE_INFO", enum_language);
         
         rowset.setValueAt(i, "LOCALE_LANGUAGE", enum_language.substring(0,enum_language.indexOf("-")));
         rowset.setValueAt(i, "LOCALE_COUNTRY", enum_language.substring(enum_language.indexOf("-")+1,enum_language.length()));         
         
         cmd = trans.addCustomCommand("GCLOC_"+i, "Language_Code_Api.Enum_Lang_Code_Rfc3066");
         cmd.addParameter("LOCALE_PROPERTIES_");      
      }      
          
      trans = mgr.perform(trans);                
      
      for (int i=0; i<rows; i++)
      {   
         rowset.setValueAt(i,"LOCALE_PROPERTIES_",trans.getValue("GCLOC_"+i+"/DATA/LOCALE_PROPERTIES_"));               
      }         
   }
   
   /*
   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data_trans = mgr.newASPBuffer();

      q = trans.addQuery(blk);
      //chob
      if ( mgr.dataTransfered() ){ //transfered data found
         data_trans = mgr.getTransferedData() ;

         result_key = data_trans.getValue("DATA/RESULT_KEY");
         //result_key = "13996";
         if ( data_trans.itemExists("PRINT_JOB") )
         { //"print job id is found in the transfered data"
            print_job_id = data_trans.getValue("PRINT_JOB");
            seq_keys = data_trans.getValue("SEQ_DATA");
            data_trans.removeItem("PRINT_JOB");   // We should remove "print job id" before adding to Query
            data_trans.removeItem("SEQ_DATA");
         }
         else
         { // "print job id not found"
            print_job_id = "";
            seq_keys = "";
         }
         q.addOrCondition( data_trans );
      }
      else
      {
         print_job_id = mgr.readValue("PRINT_JOB_ID");
         seq_keys = "";
         result_key = mgr.readValue("RESULT_KEY");

         //q.addWhereCondition("RESULT_KEY = " + "'" + result_key + "'");
         q.addWhereCondition("RESULT_KEY = ?");
         q.addParameter("RESULT_KEY",result_key);
      }
      q.includeMeta("ALL");
      mgr.submit(trans);
   }
    */

//=============================================================================
//  Button functions
//=============================================================================

   public void  print_report()
   {
      ASPManager mgr = getASPManager();
      String strOptions = "";
      String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
      int rows = rowset.countRows();
      String layout_owner = null;

      rowset.changeRows();
      
      for (int i=0; i<rows; i++)
      {
         String temp_layout_type = inf.getLayoutType(rowset.getValueAt(i,"REPORT_ID"),rowset.getValueAt(i,"LAYOUT_NAME"));
         if ("EXCEL".equals(temp_layout_type)) 
         {
            mgr.showAlert("FNDPAGESREPORTPRINTDLGPRNTEXCEL: Please use Preview button to preview report in Excel, then use Excel Print functionality");
            return;
         }

         String temp_layout_owner = inf.getLayoutTypeOwner(rowset.getValueAt(i,"REPORT_ID"),rowset.getValueAt(i,"LAYOUT_NAME"));
         if (layout_owner == null) layout_owner = temp_layout_owner;
         if (layout_owner != null && !layout_owner.equals(temp_layout_owner))
         {
            mgr.showAlert("FNDPAGESREPORTPRINTDLGMIXEDOWNERS: This print job can not be executed. The contained reports have different printing solutions (different layout type owners).");
            return;
         }
      }
      
      String job_id = mgr.readValue("PRINT_JOB_ID");
      archivate_value = mgr.readValue("ARCHIVATE");
      
      String jobattr = "PRINTER_ID" + (char)31 + mgr.readValue("SELECT_PRINTERS") + (char)30;

      //PAGES option is disabled for multiple reports
      if ( "PAGES".equals(mgr.readValue("PRINT_OP")) )
         strOptions = "PAGES(" + mgr.readValue("FROM_PAGE") + ", " + mgr.readValue("TO_PAGE") + ");";
      
      if ("AGENT".equals(layout_owner) && "TRUE".equals(archivate_value) && "MAIL_TO".equals(mgr.readValue("ARC_OPTIONS")) )
         //jobattr += getPDFMessage();
         jobattr += "SETTINGS" + (char)31 + getPDFMessage() + (char)30;

      trans.clear();
      
      if ( mgr.isEmpty(job_id) )
      {  // job id has not been sent by the calling ASP.
         cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
         cmd.addParameter("PRINT_JOB_ID");
         cmd.addParameter("CLIENT_VALUES0",jobattr);

         trans = mgr.perform(trans);

         job_id = trans.getValue("PJOBNEW/DATA/PRINT_JOB_ID");
         trans.clear();
         
         String idd = "";
         
         for (int i = 0; i < rows; i++ )
         {
            String lang = rowset.getValueAt(i,"LANG_CODE");

            if (mgr.isEmpty(lang))
            {
               if ( mgr.isEmpty(def_lang) )
                  lang = mgr.getLanguageCode();
               else
                  lang = def_lang;
            }
            
            String locale = rowset.getValueAt(i,"LOCALE_INFO");
            
            String locale_lang = locale.substring(0,locale.indexOf("-"));
            String locale_country = locale.substring(locale.indexOf("-")+1,locale.length());
            
            idd = "PRINT_JOB_ID" + (char)31 + job_id + (char)30
                  + "RESULT_KEY" + (char)31 + rowset.getValueAt(i,"RESULT_KEY") + (char)30
                  + "LAYOUT_NAME" + (char)31 + rowset.getValueAt(i,"LAYOUT_NAME") + (char)30
                  + "LANG_CODE" + (char)31 + lang + (char)30
                  + "OPTIONS" + (char)31 + strOptions + "COPIES(" + rowset.getValueAt(i,"COPIES") + ")" + (char)30
                  + "LOCALE_LANGUAGE" + (char)31 + locale_lang + (char)30
                  + "LOCALE_COUNTRY" + (char)31 + locale_country + (char)30;

            cmd = trans.addCustomCommand("NEWINS1_"+i, "Print_Job_Contents_API.New_Instance");
            cmd.addParameter("CLIENT_VALUES0", idd);
         }
         
         cmd = trans.addCustomCommand("RPRINT1", "Print_Job_API.Print");
         cmd.addParameter("CLIENT_VALUES1", job_id);

         trans=mgr.perform(trans);
      }
      else
      {  // job id has been sent by the calling ASP
         cmd = trans.addCustomCommand("PJOBMODIFY", "Print_Job_API.Modify_Job");
         cmd.addParameter("PRINT_JOB_ID", job_id);
         cmd.addParameter("CLIENT_VALUES0",jobattr);
         
         seq_key = mgr.readValue("SEQ_KEYS");
         String idd = "";
         String seq_no = "0";
         
         for (int  i = 0; i < rows; i++ )
         {
            result_key = rowset.getValueAt(i,"RESULT_KEY");
            
            if (!mgr.isEmpty(seq_key))
            {
               String temp_str = seq_key.substring(seq_key.indexOf(result_key));
               seq_no = temp_str.substring(temp_str.indexOf((char)31+"")+1, temp_str.indexOf(""+(char)30));
            }
            
            String lang = rowset.getValueAt(i,"LANG_CODE");

            if (mgr.isEmpty(lang))
            {
               if ( mgr.isEmpty(def_lang) )
                  lang = mgr.getLanguageCode();
               else
                  lang = def_lang;
            }
            
            String locale = rowset.getValueAt(i,"LOCALE_INFO");
            
            String locale_lang = locale.substring(0,locale.indexOf("-"));
            String locale_country = locale.substring(locale.indexOf("-")+1,locale.length());            
            
            idd = "PRINT_JOB_ID" + (char)31 + job_id + (char)30
                  + "RESULT_KEY" + (char)31 + result_key+ (char)30
                  + "LAYOUT_NAME" + (char)31 + rowset.getValueAt(i,"LAYOUT_NAME") + (char)30
                  + "LANG_CODE" + (char)31 + lang + (char)30
                  + "OPTIONS" + (char)31 + strOptions + "COPIES(" + rowset.getValueAt(i,"COPIES") + ")" + (char)30
                  + "INSTANCE_SEQ" + (char)31 + seq_no + (char)30
                  + "LOCALE_LANGUAGE" + (char)31 + locale_lang + (char)30
                  + "LOCALE_COUNTRY" + (char)31 + locale_country + (char)30;

            cmd = trans.addCustomCommand("MODIFYINS_"+i, "Print_Job_Contents_API.Modify_Instance");
            cmd.addParameter("CLIENT_VALUES1", idd);
         }
         
         cmd = trans.addCustomCommand("RPRINT1", "Print_Job_API.Print");
         cmd.addParameter("CLIENT_VALUES2", job_id);

         trans = mgr.perform(trans);
      }
      
      trans.clear();
      
      if ( "TRUE".equals(archivate_value) && !"AGENT".equals(layout_owner))
      {
         boolean initialized = false;
         String job_id_pdf = ""; 
         String idd_pdf = "";
         
         for (int i=0; i<rows; i++)
         {
            //String layout_owner = inf.getLayoutTypeOwner(rowset.getValueAt(i,"REPORT_ID"),rowset.getValueAt(i,"LAYOUT_NAME"));
            
            String locale = rowset.getValueAt(i,"LOCALE_INFO");            
            String locale_lang = locale.substring(0,locale.indexOf("-"));
            String locale_country = locale.substring(locale.indexOf("-")+1,locale.length());
            
            //if ( !"AGENT".equals(layout_owner))
            {
               if (!initialized)
               {
                  trans.clear();
                  trans.addCustomFunction("PDFPRINT", "Print_Server_SYS.Get_Pdf_Printer", "CLIENT_VALUES1");
                  trans = mgr.perform(trans);

                  String jobattr_pdf = "PRINTER_ID" + (char)31
                                + trans.getValue("PDFPRINT/DATA/CLIENT_VALUES1") + (char)30;

                  if ( "MAIL_TO".equals(mgr.readValue("ARC_OPTIONS")) )
                     //jobattr_pdf += "PDF_ARCHIVING" + (char)31 + "TRUE" + (char)30 + getPDFMessage();
                     jobattr_pdf += "SETTINGS" + (char)31 + getPDFMessage() + (char)30;

                  trans.clear();
                  
                  cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
                  cmd.addParameter("PRINT_JOB_ID",null);
                  cmd.addParameter("CLIENT_VALUES0",jobattr_pdf);

                  trans = mgr.perform(trans);
                  job_id_pdf = trans.getValue("PJOBNEW/DATA/PRINT_JOB_ID");
                  trans.clear();
                  
                  initialized = true;
               }
               
               idd_pdf = "PRINT_JOB_ID" + (char)31 + job_id_pdf + (char)30
                         + "RESULT_KEY" + (char)31 + rowset.getValueAt(i,"RESULT_KEY") + (char)30
                         + "LAYOUT_NAME" + (char)31 + rowset.getValueAt(i,"LAYOUT_NAME") + (char)30
                         + "LANG_CODE" + (char)31 + rowset.getValueAt(i,"LANG_CODE") + (char)30
                         + "OPTIONS" + (char)31 + strOptions + "COPIES(" + rowset.getValueAt(i,"COPIES") + ")" + (char)30
                         + "LOCALE_LANGUAGE" + (char)31 + locale_lang + (char)30
                         + "LOCALE_COUNTRY" + (char)31 + locale_country + (char)30;
               
               cmd = trans.addCustomCommand("NEWINS2_"+i, "Print_Job_Contents_API.New_Instance");
               cmd.addParameter("CLIENT_VALUES1", idd_pdf);
            }
         }
         
         if (!"".equals(idd_pdf)) // to avoid send a print without print_job_contents
         {
            cmd = trans.addCustomCommand("RPRINT2", "Print_Job_API.Print");
            cmd.addParameter("CLIENT_VALUES3", job_id_pdf);

            trans = mgr.perform(trans);
         }
         
      }
      
      if ( !"true".equals(new_window) )
         mgr.redirectFrom();
      else
         close_window = true;
     
      reassignValues();
   }
   
   public void  print_report_old()
   {
      ASPManager mgr = getASPManager();
      String idd = null;
      String idd_pdf = null;
      String strOptions = "";
      String job_id_pdf = null;

      String layout = mgr.readValue("LAY_OUT");
      String _report_id = mgr.readValue("REPORT_ID");

      String lang = mgr.readValue("LANGUAGES");

      if (mgr.isEmpty(lang))
      {
         String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if ( mgr.isEmpty(def_lang) )
            lang = mgr.getLanguageCode();
         else
            lang = def_lang;
      }

      result_key = "";

      String job_id = mgr.readValue("PRINT_JOB_ID");

      boolean initial_job_id_null = mgr.isEmpty(job_id);

 /*     if ( "CHECK".equals(mgr.readValue("ARCHIVATE_VALUE")) ) // Archivate check box is not read-only. we have to check the value
         if ( "ON".equals(mgr.readValue("ARCHIVATE")) )
            archivate_value = "TRUE";
         else
            archivate_value = "FALSE";
      else  //Archivate check box is read-only.
         archivate_value = mgr.readValue("ARCHIVATE_VALUE");
 */

      archivate_value = mgr.readValue("ARCHIVATE");

      if ( "PAGES".equals(mgr.readValue("PRINT_OP")) )
         strOptions = "PAGES(" + mgr.readValue("FROM_PAGE") + ", " + mgr.readValue("TO_PAGE") + ");";

      strOptions = strOptions + "COPIES(" + mgr.readValue("COPIES") + ")";

      String jobattr = "PRINTER_ID" + (char)31
                + mgr.readValue("SELECT_PRINTERS") + (char)30;

      trans.clear();


      if ( initial_job_id_null )
      {  // job id has not been sent by the calling ASP.
         cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
         cmd.addParameter("PRINT_JOB_ID");
         cmd.addParameter("CLIENT_VALUES0",jobattr);

         trans=mgr.perform(trans);

         job_id = trans.getValue("PJOBNEW/DATA/PRINT_JOB_ID");
         trans.clear();

         int j  = rowset.countRows();
         // Multi Reports
         if ( j > 1 ){ //Multiple Reports
            rowset.first();

            for ( int i = 0; i < j; i++ )
            {
               result_key = rowset.getValue("RESULT_KEY");
               idd = "PRINT_JOB_ID" + (char)31
                     + job_id + (char)30
                     + "RESULT_KEY" + (char)31
                     + result_key+ (char)30
                     + "LAYOUT_NAME" + (char)31
                     + "" + (char)30
                     + "LANG_CODE" + (char)31
                     + "" + (char)30
                     + "OPTIONS" + (char)31
                     + "COPIES(1)" + (char)30;

               cmd = trans.addCustomCommand("NEWINS1", "Print_Job_Contents_API.New_Instance");
               cmd.addParameter("CLIENT_VALUES0", idd);
               trans=mgr.perform(trans);
               rowset.next();
               trans.clear();
            }
         }
         else
         { //Single Reports
            result_key = rowset.getValue("RESULT_KEY");

            idd = "PRINT_JOB_ID" + (char)31
                  + job_id + (char)30
                  + "RESULT_KEY" + (char)31
                  + result_key + (char)30
                  + "LAYOUT_NAME" + (char)31
                  + mgr.readValue("LAY_OUT") + (char)30
                  +  "LANG_CODE" + (char)31
                  + lang + (char)30
                  +  "OPTIONS" + (char)31
                  + strOptions + (char)30;

            cmd = trans.addCustomCommand("NEWINS1", "Print_Job_Contents_API.New_Instance");
            cmd.addParameter("CLIENT_VALUES0", idd);
         }

         cmd = trans.addCustomCommand("RPRINT1", "Print_Job_API.Print");
         cmd.addParameter("CLIENT_VALUES1", job_id);

         trans=mgr.perform(trans);
      }
      else
      {  // job id has been sent by the calling ASP
         cmd = trans.addCustomCommand("PJOBMODIFY", "Print_Job_API.Modify_Job");
         cmd.addParameter("PRINT_JOB_ID", job_id);
         cmd.addParameter("CLIENT_VALUES0",jobattr);

         int j  = rowset.countRows();

         if ( j > 1 )  //Multiple Reports
         {

            String temp_str = null;
            rowset.first();
            seq_key = mgr.readValue("SEQ_KEYS");

            for (int i = 0; i < j; i++ ){
               result_key = rowset.getValue("RESULT_KEY");
               temp_str = seq_key.substring(seq_key.indexOf(result_key));
               String seq_no = temp_str.substring(temp_str.indexOf((char)31+"")+1, temp_str.indexOf(""+(char)30));
               idd = "PRINT_JOB_ID" + (char)31
                     + job_id + (char)30
                     + "RESULT_KEY" + (char)31
                     + result_key+ (char)30
                     + "LAYOUT_NAME" + (char)31
                     + "" + (char)30
                     + "LANG_CODE" + (char)31
                     + "" + (char)30
                     + "OPTIONS" + (char)31
                     + "COPIES(1)" + (char)30
                     + "INSTANCE_SEQ" + (char)31
                     + seq_no + (char)30;

               cmd = trans.addCustomCommand("MODIFYINS", "Print_Job_Contents_API.Modify_Instance");
               cmd.addParameter("CLIENT_VALUES1", idd);
               trans=mgr.perform(trans);
               rowset.next();
               trans.clear();
            }
         }
         else
         { //Single Reports
            result_key = rowset.getValue("RESULT_KEY");

            idd = "PRINT_JOB_ID" + (char)31
                  + job_id + (char)30
                  + "RESULT_KEY" + (char)31
                  + result_key + (char)30
                  + "LAYOUT_NAME" + (char)31
                  + mgr.readValue("LAY_OUT") + (char)30
                  +  "LANG_CODE" + (char)31
                  + lang + (char)30
                  +  "OPTIONS" + (char)31
                  + strOptions + (char)30
                  + "INSTANCE_SEQ" + (char)31
                  + "0" + (char)30;

            cmd = trans.addCustomCommand("MODIFYINS", "Print_Job_Contents_API.Modify_Instance");
            cmd.addParameter("CLIENT_VALUES1", idd);
         }
         cmd = trans.addCustomCommand("RPRINT1", "Print_Job_API.Print");
         cmd.addParameter("CLIENT_VALUES2", job_id);

         trans=mgr.perform(trans);
      }

      trans.clear();

      String layout_owner = inf.getLayoutTypeOwner(_report_id,layout);

      if ( "TRUE".equals(archivate_value) && !"AGENT".equals(layout_owner))
      {  //Achivate is TRUE.
         //Get PDF printer

         trans.clear();
         trans.addCustomFunction("PDFPRINT", "Print_Server_SYS.Get_Pdf_Printer", "CLIENT_VALUES1");
         trans=mgr.perform(trans);

         String jobattr_pdf = "PRINTER_ID" + (char)31
                       + trans.getValue("PDFPRINT/DATA/CLIENT_VALUES1") + (char)30;

         if ( "MAIL_TO".equals(mgr.readValue("ARC_OPTIONS")) )
            jobattr_pdf += "SETTINGS" + (char)31 + getPDFMessage() + (char)30;

         trans.clear();

         cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
         cmd.addParameter("PRINT_JOB_ID",null);
         cmd.addParameter("CLIENT_VALUES0",jobattr_pdf);

         trans=mgr.perform(trans);
         job_id_pdf = trans.getValue("PJOBNEW/DATA/PRINT_JOB_ID");


         int j  = rowset.countRows();
         // Multi Reports
         if ( j > 1 ){ //Multiple Reports
            rowset.first();
            for (int i = 0; i < j; i++ ){
               result_key = rowset.getValue("RESULT_KEY");
               idd_pdf = "PRINT_JOB_ID" + (char)31
                         + job_id_pdf + (char)30
                         + "RESULT_KEY" + (char)31
                         + result_key+ (char)30
                         + "LAYOUT_NAME" + (char)31
                         + "" + (char)30
                         + "LANG_CODE" + (char)31
                         + "" + (char)30
                         + "OPTIONS" + (char)31
                         + "COPIES(1)" + (char)30;

               trans.clear();
               cmd = trans.addCustomCommand("NEWINS2", "Print_Job_Contents_API.New_Instance");
               cmd.addParameter("CLIENT_VALUES1", idd_pdf);
               trans=mgr.perform(trans);
               rowset.next();
            }
            trans.clear();
         }
         else
         { //Single Reports
            result_key = rowset.getValue("RESULT_KEY");
            idd_pdf = "PRINT_JOB_ID" + (char)31
                      + job_id_pdf + (char)30
                      + "RESULT_KEY" + (char)31
                      + result_key + (char)30
                      + "LAYOUT_NAME" + (char)31
                      + mgr.readValue("LAY_OUT") + (char)30
                      +  "LANG_CODE" + (char)31
                      + lang + (char)30
                      +  "OPTIONS" + (char)31
                      + strOptions + (char)30;
            trans.clear();

            cmd = trans.addCustomCommand("NEWINS2", "Print_Job_Contents_API.New_Instance");
            cmd.addParameter("CLIENT_VALUES1", idd_pdf);
         }

         cmd = trans.addCustomCommand("RPRINT2", "Print_Job_API.Print");
         cmd.addParameter("CLIENT_VALUES3", job_id_pdf);

         trans=mgr.perform(trans);
      }


      if ( !"true".equals(new_window) )
         mgr.redirectFrom();
      else
         close_window = true;

      reassignValues();
   }

   public String getPDFMessage()
   {
      /*
      String attr_ = "SEND_PDF" +(char)31 +"TRUE" +(char)30+
                     "SEND_PDF_TO" +(char)31 + getASPManager().readValue("EMAIL") +(char)30;
       */
      
      String attr_ = "!FNDINF.SEND_PDF" + (char)10 +
                     "$SEND_PDF=TRUE" + (char)10 +
                     "$SEND_PDF_TO=" + getASPManager().readValue("EMAIL") + (char)10;

      return attr_;
   }

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      String lang_code = mgr.readValue("LANG_CODE");
      String report_id = mgr.readValue("REPORT_ID");
      String all_printers = mgr.readValue("ALL_PRINTERS");
      if ("REFPRN".equals(val))
      {  
         if (mgr.isModuleInstalled("APPSRV"))
         {
            if ("false".equalsIgnoreCase(all_printers) )
            {
               trans.clear();
               cmd = trans.addCustomFunction("LOGICAL_PRINTERS","PRINTER_CONNECTION_API.Get_Logical_Printers__","CLIENT_VALUES1");
               cmd.addParameter("FND_USER",mgr.getUserId());
               cmd.addParameter("REPORT_ID", report_id);
               cmd.addParameter("LANG_CODE",lang_code);

               cmd = trans.addCustomFunction("DEFAULT_PRINTER","PRINTER_CONNECTION_API.Get_Default_Logical_Printer","DEFAULTPRINTER");
               cmd.addParameter("FND_USER",mgr.getUserId());
               cmd.addParameter("REPORT_ID", report_id);
               cmd.addParameter("LANG_CODE",lang_code);

               trans = mgr.perform(trans);

               logical_printers = trans.getValue("LOGICAL_PRINTERS/DATA/CLIENT_VALUES1");
               default_printer = trans.getValue("DEFAULT_PRINTER/DATA/DEFAULTPRINTER");

               mgr.responseWrite(default_printer+"^"+logical_printers+"^");
               mgr.endResponse();
            }
            else
            {
               trans.clear();
               
               trans.addQuery("ALL_PRINTERS", "SELECT Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id FROM LOGICAL_PRINTER").setBufferSize(2000);

               cmd = trans.addCustomFunction("DEFAULT_PRINTER","PRINTER_CONNECTION_API.Get_Default_Logical_Printer","DEFAULTPRINTER");
               cmd.addParameter("FND_USER",mgr.getUserId());
               cmd.addParameter("REPORT_ID", report_id);
               cmd.addParameter("LANG_CODE",lang_code);
               trans = mgr.perform(trans);

               ASPBuffer printer_list = trans.getBuffer("ALL_PRINTERS");
               default_printer = trans.getValue("DEFAULT_PRINTER/DATA/DEFAULTPRINTER");

                if (printer_list != null)
                {           
                   int count = printer_list.countItems();
                   all_printers = printer_list.getBufferAt(0).getValueAt(0);
                   for (int i=1; i < count-1 ; i++ )
                   {
                      String printer = printer_list.getBufferAt(i).getValueAt(0);
                      all_printers = all_printers + String.valueOf(IfsNames.fieldSeparator ) + printer;
                   }
                }

               mgr.responseWrite(default_printer+"^"+all_printers+"^");            
               mgr.endResponse();

            }
         }
         else
         { 
            trans.addQuery("ALL_PRINTERS", "SELECT Logical_Printer_API.Get_Description(printer_id)||',SERVER,'|| printer_id FROM LOGICAL_PRINTER").setBufferSize(2000);
            trans = mgr.perform(trans);
            ASPBuffer printer_list = trans.getBuffer("ALL_PRINTERS");
            
            if (printer_list != null)    
            {           
               int count = printer_list.countItems();
               all_printers = printer_list.getBufferAt(0).getValueAt(0);
               for (int i=1; i < count-1 ; i++ )
               {
                  String printer = printer_list.getBufferAt(i).getValueAt(0);
                  all_printers = all_printers + String.valueOf(IfsNames.fieldSeparator ) + printer;
               }
                
               all_printers_arr = Str.split(all_printers,String.valueOf(IfsNames.fieldSeparator ));
            }
            
            mgr.responseWrite(all_printers_arr[0]+"^"+all_printers+"^"+"true"+"^");            
            mgr.endResponse();
            
         }

         
      }
      else
      {
         preview();
      }
   }
   public void  preview()
   {  
      ASPManager mgr = getASPManager();
      String val = mgr.getQueryStringValue("PREVIEW");
      String lang   = mgr.getQueryStringValue("LANGUAGE");
      String layout = mgr.getQueryStringValue("LAYOUT");
      String report_id = mgr.getQueryStringValue("REPORT_ID");
      String id = mgr.getQueryStringValue("ID");
      String locale = mgr.getQueryStringValue("LOCALE");

      if (mgr.isEmpty(lang))
      {
         String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);
         if ( mgr.isEmpty(def_lang) )
            lang = mgr.getLanguageCode();
         else
            lang = def_lang;
      }

      if ("CREATE".equals(val))
      {
         String print_job_id = inf.createPdfReportLocale( mgr.readValue("RESULT_KEY"),layout,lang,locale );
         
         /*trans.clear();
         q = trans.addQuery("PDF_ID","select ID from PDF_ARCHIVE where RESULT_KEY = ? and PRINT_JOB_ID = ?");
         q.addParameter("RESULT_KEY", mgr.readValue("RESULT_KEY"));
         q.addParameter("PRINT_JOB_ID", print_job_id); 
         
         trans = mgr.perform(trans);*/
         
         // inf.convertReportToPdf(mgr.readValue("RESULT_KEY"));
         mgr.responseWrite(print_job_id+"^");
      }
      else if ("READY".equals(val))
      {
         String layout_type_owner = inf.getLayoutTypeOwner(report_id,layout);
         if ("PRINTSRV".equals(layout_type_owner))
         {
            File report = new File(inf.getReportPDFFileName(mgr.readValue("RESULT_KEY"),lang));
            mgr.responseWrite(report.canRead()?"Y":"N");
         }
         else if ("AGENT".equals(layout_type_owner) || mgr.isEmpty(layout_type_owner))
         {
            String layout_type = inf.getLayoutType(report_id,layout);
            if ("EXCEL".equals(layout_type)) 
            {
               mgr.responseWrite("Y");
               setExcelReport = "TRUE";
               //setExcel
            }
            else
            {
               setExcelReport = "FALSE";
               //mgr.responseWrite(inf.isPDFContentAvailable(result_key,layout,lang)?"Y":"N");
               id = inf.getPdfId(result_key,mgr.readValue("PRINT_JOB_ID"));
               mgr.responseWrite(mgr.isEmpty(id)?"":id+"^");
            }
         }
      }
      else if ("SHOW".equals(val))
      {
         String layout_type_owner   = inf.getLayoutTypeOwner(report_id,layout);
         String layout_type         = inf.getLayoutType(report_id,layout);
         
         if ("AGENT".equals(layout_type_owner))
            inf.sendPDFContents("RESULT_KEY=? and ID=?","RESULT_KEY^S^IN^"+result_key+"^ID^S^IN^"+id);
         else if ("PRINTSRV".equals(layout_type_owner))
            inf.sendReportAsPdf( result_key,lang );
         else if ("EXCEL".equals(layout_type)) 
            inf.sendPDFContents(result_key, layout, lang);
      }
      mgr.endResponse();
   }

   public void  cancel()
   {
      ASPManager mgr = getASPManager();

      if ( !"true".equals(new_window) )
         mgr.redirectFrom();
      else
         close_window = true;

      reassignValues();
   }

//=============================================================================
//  Definition
//=============================================================================


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      // Head Block

      blk = mgr.newASPBlock("HEAD");
      blk.disableHistory();

      //blk.addField("OBJID").setHidden();

      //blk.addField("OBJVERSION").setHidden();

      blk.addField("RESULT_KEY");
      blk.addField("REPORT_ID");
      blk.addField("NOTES");
      blk.addField("REPORT_TITLE");
      blk.addField("LAYOUT_NAME");
      blk.addField("LANG_CODE");
      blk.addField("COPIES");
      blk.addField("LOCALE_INFO");
      blk.addField("LOCALE_LANGUAGE");
      blk.addField("LOCALE_COUNTRY");      
      
      blk.addField("LAYOUT_PROPERTIES_");
      blk.addField("LANG_PROPERTIES_");
      blk.addField("LOCALE_PROPERTIES_");
      
      //blk.setView("ARCHIVE");
      //blk.defineCommand("ARCHIVE_API","New__,Modify__,Remove__");

      rowset = blk.getASPRowSet();

      lay    = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);

      bar = mgr.newASPCommandBar(blk);
      tbl = mgr.newASPTable(blk);
      tbl.setEditable();
      tbl.disableEditProperties();
      tbl.unsetPopup();
      tbl.disableRowCounter();
      

      h = mgr.newASPBlock("PRNT");
      h.addField("REPORT_ATTR_");
      h.addField("COLUMN_PROPERTIES_");
      h.addField("TEXT_PROPERTIES_");
      //h.addField("LAYOUT_PROPERTIES_");
      //h.addField("LANG_PROPERTIES_");
      h.addField("ARCHIVATE_");
      h.addField("CLIENT_VALUES0");
      h.addField("CLIENT_VALUES1");
      h.addField("CLIENT_VALUES2");
      h.addField("CLIENT_VALUES3");
      h.addField("LANGUAGES");
      h.addField("LAYOUTS");
      h.addField("PRINT_JOB_ID");
      h.addField("SMTP_MAIL_ADDRESS");
      h.addField("MAIL_PROPERTIES");
      h.addField("FND_USER");
      h.addField("LAYOUT_OWNER");
      h.addField("ID");      
      
      h.addField("DEFAULTPRINTER");
      
      try
      {
         appendJavaScript(getPreviewScript());
      }
      catch(FndException e)
      {
         error(e);
      }

      inf = mgr.newASPInfoServices();
      inf.addFields();

      this.disableHomeIcon();
      this.disableOptions();
      this.disableNavigate();
      
      this.enableConvertGettoPost();
      this.disableSettingsLink();
      this.disableSignoutLink();
      this.disableApplicationSearch();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESREPORTPRINTDLGWINDOWTITLE: Report Print Dialog"+" - "+title_bar;
   }

   protected String getTitle()
   {
      return "FNDPAGESREPORTPRINTDLGWINDOWTITLE: Report Print Dialog"+" - "+title_bar;
   }
   
   
   private void drawTable(AutoString out) throws FndException
   {
      // Create the table heading
      int rows = rowset.countRows();
      out.append("\n<div class=borders style=\"overflow: auto; height:100\">\n");
      out.append("<table border=0 cellpadding=0 cellspacing=0>");

         out.append("<tr><td>&nbsp;&nbsp;</td><td>");
         printBoldText("FNDPAGESREPORTPRINTDLGREPTITLE: Title");
         out.append("</td><td>&nbsp;</td><td>");
         printBoldText("FNDPAGESREPORTPRINTDLGRPDLAY: Layout");
         out.append("</td><td>&nbsp;</td><td>");
         printBoldText("FNDPAGESREPORTPRINTDLGRPDLAN: Language");
         out.append("</td><td>&nbsp;</td><td>");
         printBoldText("FNDPAGESREPORTPRINTDLGRPDFORLOC: Number/Date format locale");
         out.append("</td><td>&nbsp;</td><td>");         
         printBoldText("FNDPAGESREPORTPRINTDLGRPDCOPS: Copies");
         out.append("</td></tr>");      
         
         out.append("<tbody>\n");
         printHiddenField("__HEAD_ROWSTATUS","QueryMode__");
         printHiddenField("RESULT_KEY","");
         printHiddenField("REPORT_ID","");
         printHiddenField("NOTES","");
         printHiddenField("REPORT_TITLE","");
         printHiddenField("LAYOUT_NAME","");
         printHiddenField("LANG_CODE","");
         printHiddenField("COPIES","");
         printHiddenField("LOCALE_INFO","");         
         // Create the table contents

         
         
         for (int i=0;i < rows; i++)
         {
            printHiddenField("RESULT_KEY",rowset.getValueAt(i,"RESULT_KEY"));
            printHiddenField("REPORT_ID",rowset.getValueAt(i,"REPORT_ID"));
            
            out.append("<tr><td>&nbsp;</td>");
            String notes = rowset.getValueAt(i,"NOTES");
            
            if (!getASPManager().isEmpty(notes))
               out.append("<td onMouseover=\"showtip(this,event,'"+notes+"')\" onMouseOut=\"hidetip()\">");
            else
               out.append("<td nowrap>");
            
            printText(rowset.getValueAt(i,"REPORT_TITLE"));
            out.append("</td><td>&nbsp;</td>");

            //layout
            out.append("<td>");
            //printField("LAYOUT_NAME",rowset.getValueAt(i,"LAYOUT_NAME"),"");
            String layouts = rowset.getValueAt(i, "LAYOUT_PROPERTIES_");
            try
            {
               layouts = inf.getOptions(inf.getTokenAt(layouts, 2), inf.getTokenAt(layouts, 1),rowset.getValueAt(i,"LAYOUT_NAME"),false);
            }
            catch (Exception any)
            {
               layouts = "";
            }
            out.append(fmt.drawPreparedSelect("LAYOUT_NAME",layouts,"tabindex=3",false));
            out.append("</td><td>&nbsp;</td>");

            //language
            out.append("<td>");
            //printField("LANG_CODE",rowset.getValueAt(i,"LANG_CODE"),"",3);
            String languages = rowset.getValueAt(i, "LANG_PROPERTIES_");
            try
            {
               languages = inf.getOptions(inf.getTokenAt(languages, 2), inf.getTokenAt(languages, 1),rowset.getValueAt(i,"LANG_CODE"),false);
            }
            catch (Exception any)
            {
               languages = "";
            }
            out.append(fmt.drawPreparedSelect("LANG_CODE",languages,"tabindex=4 onchange=javascript:refreshPrinters(this.value,'"+rowset.getValueAt(i,"REPORT_ID")+"',document.form.PRINTERS.checked)",false));
            out.append("</td><td>&nbsp;</td>");

            //Number/Date Format Locale
            out.append("<td>");
            String enum_lang_code = rowset.getValueAt(i, "LOCALE_PROPERTIES_");

            try
            {
               enum_lang_code = inf.getOptions(inf.getTokenAt(enum_lang_code, 1), inf.getTokenAt(enum_lang_code, 1),rowset.getValueAt(i, "LOCALE_INFO"),true);
            }
            catch (Exception any)
            {
               enum_lang_code = "";
            }
            
            out.append(fmt.drawPreparedSelect("LOCALE_INFO",enum_lang_code,"tabindex=5",false));                    
            out.append("</td><td>&nbsp;</td>");            
                      
            //copies
            out.append("<td>");
            printField("COPIES",rowset.getValueAt(i, "COPIES"),"style=\"text-align: right\" CLASS=\"editableTextField\" OnChange=\"validateCopies(1)\"",4);
            out.append("</td></tr>\n");
         }
         out.append("</tbody>\n");
      out.append("</table>\n");         
      out.append("</div>\n");

      String editables = ",__HEAD_ROWSTATUS,LAYOUT_NAME,LANG_CODE,COPIES,LOCALE_INFO,";
      getASPManager().getASPPage().getASPContext().writeValue("__HEAD_COLUMNS",editables);
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getDescription()));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");

      if ( close_window )
      {
         out.append("<script language=javascript> \n");
         out.append(" window.close(); \n");
         out.append("</script> \n");
      }

      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      //if ( !"true".equals(new_window) )
         out.append(mgr.startPresentation("FNDPAGESREPORTPRINTDLGWINDOWTITLE: Report Print Dialog"));
      /*
      else
      {
         out.append("<tabel> \n");
         out.append("<tr><td nowrap width=10>&nbsp;</td><td nowrap class=pageTitle><font class=pageTitle>",mgr.translate(getTitle()),"</font></td></tr>\n");
         out.append("<tr><td nowrap><hr></td></tr> \n");
         out.append("<tr><td nowrap width=10>&nbsp;</td></tr> \n");
         out.append("</tabel> \n");
      }
       */
      out.append("  <input type=\"hidden\" name=\"INVALID_PAGE_NUMBERING\"\n");
      out.append("  value=\"");
      out.append(mgr.translate("Invalid page numbering"));
      out.append("\"><input type=\"hidden\"\n");
      out.append("  name=\"PRINT_JOB_ID\" value=\"");
      out.append(print_job_id);
      out.append("\"><input type=\"hidden\" name=\"SEQ_KEYS\"\n");
      out.append("  value=\"");
      out.append(seq_keys);
      out.append("\"><input type=\"hidden\" name=\"ARCHIVATE_VALUE\"\n");
      out.append("  value=\"");
      out.append(archivate_value);
      out.append("\"><input type=\"hidden\" name=\"MULTIPLE_REPORTS\"\n");
      out.append("  value=\"");
      out.append(multiple_reports);
      out.append("\">\n");

      beginDataPresentation();
      drawSimpleCommandBar(mgr.translate("FNDPAGESREPORTPRINTDLGCMDBARTITLE: Report ")+" - "+title_bar);

      out.append("<table  border=\"0\" class=\"pageFormWithBorder\" width=100%>\n");
      out.append("<tr>\n");
      out.append(" <td nowrap>\n");
      out.append("  <table border=\"0\" width=100%>\n");
      out.append("    <tr>\n");
      out.append("     <td nowrap>");
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGRPDPRI: Printer:"));
      out.append("</td>\n");
      out.append("    </tr>\n");
      out.append("     <tr>\n");
      out.append("                  <td nowrap>&nbsp;&nbsp;&nbsp;&nbsp; ");
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGRPDNAM: Name:"));
      out.append("</td>\n");
      out.append("                  <td nowrap colspan=\"2\">&nbsp;");
      //printMandatorySelectBox("SELECT_PRINTERS",inf.getSortedBuffer(printer),readGlobalProfileValue("Defaults/Printer"+ProfileUtils.ENTRY_SEP+"Default",false));
 
      if (logical_printers==null)
      { 
         printMandatorySelectBox("SELECT_PRINTERS",trans.getBuffer("ALL_PRINTERS"),readGlobalProfileValue("Defaults/Printer"+ProfileUtils.ENTRY_SEP+"Default",false),"style=\"width:300px\"");
         out.append("</td>\n");
         out.append("                </tr>\n");
         out.append("            <tr>\n");
         out.append("                  <td nowrap>&nbsp;&nbsp;&nbsp;&nbsp; ");
         
         out.append(fmt.drawCheckbox("PRINTERS","ON",true,"onClick=\"javascript:refreshPrinters(document.form.LANG_CODE[1].value,document.form.REPORT_ID[1].value,this.checked)\""));             
      } 
      else
      {
         printMandatorySelectBox("SELECT_PRINTERS",inf.getSortedBuffer(logical_printers),default_printer,"style=\"width:300px\"");
         out.append("</td>\n");
         out.append("                </tr>\n");
         out.append("            <tr>\n");
         out.append("                  <td nowrap>&nbsp;&nbsp;&nbsp;&nbsp; ");
         out.append(fmt.drawCheckbox("PRINTERS","ON",false,"onClick=\"javascript:refreshPrinters(document.form.LANG_CODE[1].value,document.form.REPORT_ID[1].value,this.checked)\""));             
      }
      
      
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGRPDSHOWALL: Show all printers"));

      out.append("</td>\n");
      out.append("                </tr>\n");
      out.append("            <tr>\n");
      out.append("              <td nowrap colspan=3 ><hr>\n");
      out.append("              </td>\n");
      out.append("            </tr>\n");
      out.append("          </table>\n");
      out.append("          </td></tr>\n");
      
      out.append("          <tr><td>\n");
      drawTable(out);
      //out.append(tbl.populate());
      out.append("          </td></tr>\n");
      out.append("          <tr><td><table><tr><td>\n");
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGRPDPROP: Print Options:"));
      out.append("          </td></tr>\n");
      out.append("          <tr><td>\n");
      out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGRPDALL: All","PRINT_OP","ALL",true,"tabindex=6 OnClick=validateAll()"));
      out.append("          </td></tr>\n");
      out.append("          <tr><td>\n");
      
      out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGRPDPAG: Pages","PRINT_OP","PAGES",false,"tabindex=7 OnClick=selectAllPages() "+("FALSE".equals(multiple_reports)?"":"DISABLED")));
      out.append("          </td><td nowrap>");
      out.append(fmt.drawTextField("FROM_PAGE","", "tabindex=8 onFocus=blurIfMultiple(this);document.form.PRINT_OP[1].click() "+("FALSE".equals(multiple_reports)?"":"DISABLED"),7));
      out.append("</td>\n");
      out.append("                        <td nowrap align=\"center\">-</td>\n");
      out.append("                        <td nowrap>");
      out.append(fmt.drawTextField("TO_PAGE","", "tabindex=9 onFocus=blurIfMultiple(this);document.form.PRINT_OP[1].click() "+("FALSE".equals(multiple_reports)?"":"DISABLED"),7));
      out.append("</td></tr></table>\n");
      out.append("</td></tr>\n");
      out.append("<tr>\n");
      out.append("                  <td nowrap colspan=2 ><hr>\n");
      out.append("                  </td>\n");
      out.append("</tr>\n");

      out.append("<tr>\n");
      out.append(" <td nowrap colspan=3>\n");
      out.append("  <table>\n");
      out.append("   <tr>\n");
      out.append("    <td nowrap>\n");
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGRPDPRINTED: When Report is Printed:"));
      out.append("    </td>\n");
      out.append("   </tr>\n");
      out.append("   <tr>\n");
      out.append("    <td nowrap valign=top>");
      out.append(fmt.drawHidden("ARCHIVATE",archivate));
      out.append(archivate_box);
      out.append(fmt.drawReadLabel("FNDPAGESREPORTPRINTDLGROEARC: Archive and..."));
      out.append("     </td>\n");

      out.append("     <td nowrap> \n");

      if ( mgr.isExplorer() )
      {
         out.append("       <table id=arctable style=\"visibility:"+visibility+"\" >\n");
         out.append("        <tr> \n");
         out.append("         <td nowrap> \n");
         out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGDO: Do nothing","ARC_OPTIONS","DO_NOTHING",true,""));
         out.append("         </td> \n");
         out.append("        </tr> \n");
         out.append("        <tr> \n");
         out.append("         <td nowrap> \n");
         out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGMAILTO: Send E-Mail","ARC_OPTIONS","MAIL_TO",false,"onClick=\"javascript:document.form.EMAILDISPLAY.value = document.form.EMAIL.value\""));
         out.append("         </td> \n");
         out.append("         <td nowrap> \n");
         out.append(fmt.drawHidden("EMAIL",smtp_mail_address));
         out.append(fmt.drawHidden("DEFAULTEMAIL",smtp_mail_address));
         out.append(fmt.drawTextField("EMAILDISPLAY","","onFocus=validateEMail(this) onChange=\"javascript:{if (this.value == '')this.value = document.form.DEFAULTEMAIL.value; document.form.EMAIL.value=this.value;}\"",30));
         out.append("         </td> \n");
         out.append("        </tr> \n");
         out.append("       </table> \n");
      }
      else if ( !("OFF".equals(archivate_value)) )
      {
         out.append("       <table >\n");
         out.append("        <tr> \n");
         out.append("         <td nowrap> \n");
         if ( "hidden".equals(visibility) )
            out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGDO: Do nothing","ARC_OPTIONS","DO_NOTHING",false," onClick=\"javascript:validateOption0(this)\""));
         else if ( "ALWAYS ON".equals(archivate_value) )
            out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGDO: Do nothing","ARC_OPTIONS","DO_NOTHING",true,""));
         else
            out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGDO: Do nothing","ARC_OPTIONS","DO_NOTHING",true,"onClick=\"javascript:validateOption0(this)\""));

         out.append("         </td> \n");
         out.append("        </tr> \n");
         out.append("        <tr> \n");
         out.append("         <td nowrap> \n");
         if ( "ALWAYS ON".equals(archivate_value) )
            out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGMAILTO: Send E-Mail","ARC_OPTIONS","MAIL_TO",false,"onClick=\"javascript:assignEMail()\""));
         else
            out.append(fmt.drawRadio("FNDPAGESREPORTPRINTDLGMAILTO: Send E-Mail","ARC_OPTIONS","MAIL_TO",false,"onClick=\"javascript:validateOption1(this)\""));
         out.append("         </td> \n");
         out.append("         <td nowrap> \n");
         out.append(fmt.drawHidden("EMAIL",smtp_mail_address));
         out.append(fmt.drawTextField("EMAILDISPLAY","","onFocus=validateEMail(this) onChange=\"javascript:document.form.EMAIL.value=this.value\"",30));
         out.append("         </td> \n");
         out.append("        </tr> \n");
         out.append("       </table> \n");

      }
      out.append("     </td>\n");
      out.append("</tr>\n");
      out.append("</table>\n");
      out.append("<p>&nbsp;</p>\n");
      out.append("</td>\n");
      out.append("</tr>\n");


      out.append("            <tr><td>\n");
      out.append(fmt.drawSubmit("OK","FNDPAGESREPORTPRINTDLGRPDOK: OK","OnMouseDown=validateToPage()"));
      out.append("&nbsp;&nbsp;");
      out.append(fmt.drawSubmit("CANCEL","FNDPAGESREPORTPRINTDLGRPDCANC: Cancel",""));
      out.append("&nbsp;&nbsp;");

      //if ( ( inf.archivateIsActive() ) &&  ( "FALSE".equals(multiple_reports) ) && !mgr.isNetscape4x()){
      if ( ( inf.archivateIsActive() ) &&  ( "FALSE".equals(multiple_reports) )){
         out.append(fmt.drawButton("PREVIEW","FNDPAGESREPORTPRINTDLGRPDPREVIEW: Preview","OnClick=\"javascript:createReport('" + result_key + "',1)\""));
      }
      out.append("</td>\n");
      out.append("</tr></table> \n");

      endDataPresentation(false);
      printHiddenField("REPORT_ID", report_id);

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

      appendDirtyJavaScript("function showOptions()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if (document.form.ARCHCHKBOX.checked){\n");
      appendDirtyJavaScript("     document.form.ARCHIVATE.value = 'TRUE' \n");

      if ( mgr.isExplorer() )
      {
         appendDirtyJavaScript("    document.all['arctable'].style.visibility =  \"visible\";\n");
         appendDirtyJavaScript(" }else { \n");
         appendDirtyJavaScript("    document.form.ARCHIVATE.value = 'FALSE' \n");
         appendDirtyJavaScript("    document.all['arctable'].style.visibility =  \"hidden\"; \n");
         appendDirtyJavaScript("}\n");
      }
      else
      {
         appendDirtyJavaScript("    document.form.ARC_OPTIONS[0].checked = true;\n");
         appendDirtyJavaScript(" } else {\n");
         appendDirtyJavaScript("    document.form.ARCHIVATE.value = 'FALSE' \n");
         appendDirtyJavaScript("    document.form.ARC_OPTIONS[0].checked = false; \n");
         appendDirtyJavaScript("    document.form.ARC_OPTIONS[1].checked = false; \n");
         appendDirtyJavaScript("}\n");
      }
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("var default_printer ='"+default_printer+"';\n");

        if (logical_printers==null)
      {
         appendDirtyJavaScript("document.form.PRINTERS.checked = true;\n");
         appendDirtyJavaScript("document.form.PRINTERS.disabled = true;\n");
      }
              
      appendDirtyJavaScript(getPreviewScript());
      
      out.append(mgr.endPresentation());
/*
      if ( !"true".equals(new_window) )
         out.append(mgr.endPresentation());
      else
         out.append(mgr.generateClientScript());
 */
      out.append("<input TYPE=\"HIDDEN\" NAME=RESULT_KEY VALUE=\"");
      out.append(result_key);
      out.append("\">\n");
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
