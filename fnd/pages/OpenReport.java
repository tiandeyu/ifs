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
* File                          : OpenReport.java
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    :
* 2009/05/18      buhilk      Created.
* 2009/06/09      buhilk      BugID 83650. Modified with error handling.
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.service.Util;
import ifs.fnd.service.FndException;

public class OpenReport extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.OpenReport");
   
   private String lang_code;
   private String layout_name;
   private String result_key;
   private String report_id;
   private String locale;
   private String time_out;
   private String delay;
   
   public  OpenReport (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();
      
      if(!mgr.isRWCHost()) 
         error(new FndException("FNDOPENREPORT: This page is only accessible through Enterprise Explorer."));
      
      lang_code   = mgr.readValue("LANG_CODE",mgr.getDefaultLanguage());
      layout_name = mgr.readValue("LAYOUT_NAME");
      result_key  = mgr.readValue("RESULT_KEY");
      report_id   = mgr.readValue("REPORT_ID");
      locale      = mgr.readValue("LOCALE");
      
      time_out    = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_TIME_OUT","25");
      delay       = mgr.getConfigParameter("APPLICATION/PDF_PREVIEW_DELAY","5");
   }

   protected String getDescription()
   {
      return "FNDOPENREPORTTITLE: Open Report";
   }

   protected String getTitle()
   {
      return getDescription();
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager(); 
      appendToHTML("<table border=\"0\" width=\"100%\" height=\"100%\"><tr><td><table border=\"0\" align=\"center\"><tr><td id=\"msgCell\" class=\"normalTextValue\">&nbsp;</td></tr></table></td></tr></table>");
      appendDirtyJavaScript("\nfunction openReport(){\n");
      appendDirtyJavaScript("   document.getElementById('msgCell').innerHTML='"+mgr.translateJavaScript("FNDOPENREPORTWAIT: Previewing report. Please wait.&1","<br>.")+"';\n");
      appendDirtyJavaScript("   __connect(APP_ROOT+'common/scripts/ReportPrintDlg.page?VALIDATE=Y&PREVIEW=CREATE&LANGUAGE='+URLClientEncode('"+lang_code+"')+'&LAYOUT='+URLClientEncode('"+layout_name+"')+'&RESULT_KEY=' + URLClientEncode('"+result_key+"') + '&LOCALE=' + URLClientEncode('"+locale+"'));\n");
      appendDirtyJavaScript("   setTimeout('previewReport("+result_key +",0,\\\''+__getValidateValue(0)+'\\\')',"+delay+"*1000);\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("\nfunction previewReport(result_key,timeout,print_job_id){\n");
      appendDirtyJavaScript("   document.getElementById('msgCell').innerHTML = document.getElementById('msgCell').innerHTML + '.';\n");
      appendDirtyJavaScript("   if (timeout*2>"+time_out+"){\n");
      appendDirtyJavaScript("      document.getElementById('msgCell').innerHTML='"+mgr.translateJavaScript("FNDOPENREPORTERROR01: Preview operation timed out. Please try again later.")+"';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   timeout++;\n");
      appendDirtyJavaScript("   __connect(APP_ROOT+'common/scripts/ReportPrintDlg.page?VALIDATE=Y&PREVIEW=READY&LANGUAGE='+URLClientEncode('"+lang_code+"')+'&LAYOUT='+URLClientEncode('"+layout_name+"')+'&RESULT_KEY=' + URLClientEncode('"+result_key+"') + '&REPORT_ID='+URLClientEncode('"+report_id+"')+'&PRINT_JOB_ID=' + URLClientEncode(print_job_id));\n");
      appendDirtyJavaScript("   var id = __getValidateValue(0);\n");
      appendDirtyJavaScript("   if (id !='' && id !='N')\n");
      appendDirtyJavaScript("      self.location = APP_ROOT+'common/scripts/ReportPrintDlg.page?OPENWIN=Y&PREVIEW=SHOW&LANGUAGE='+URLClientEncode('"+lang_code+"')+'&LAYOUT='+URLClientEncode('"+layout_name+"')+'&RESULT_KEY=' + URLClientEncode('"+result_key+"') + '&REPORT_ID='+URLClientEncode('"+report_id+"') + '&ID=' + URLClientEncode(id) + '&EXCEL_REPORT=FALSE';\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      setTimeout('previewReport('+result_key+','+timeout+',\\\''+print_job_id+'\\\')',2000);\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("openReport();\n");
   }
}
