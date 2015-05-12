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
 * File        : ASPInfoServices.java
 * Description : IFS/InfoServices related utilities
 * Notes       : 
 * ----------------------------------------------------------------------------
 * Modified    :
 *    Marek D     1998-Sep-23 - Created
 *    Magnus N    1998-Okt-02 - Added methods isReportAvailableAsPdf() & sendReportAsPdf()
 *    Jacek P     1999-Feb-10 - Utilities classes moved to util directory.
 *    Jacek P     1999-Feb-17 - Extends ASPPageElement instead of ASPObject.
 *    Jacek P     1999-Mar-05 - Reference to ASPManager replaced with function call.
 *    Jacek P     1999-Mar-15 - Added method construct().
 *    Marek D     1999-Apr-27 - Added verify() and scan()
 *    Chaminda O  1999-Jul-09 - Added itemValueCreate(), reportOrder(), agendaExecute()
 *    Chaminda O  1999-Jul-13 - Added Attribute ASPBlock b;
 *                   - Added methods addFields() and getFields()
 *    Chaminda O  1999-Jul-19 - Changed the existing methods (creating ASPfields) to work with
 *                              the ASP pages with preDefine() method.
 *    Mangala P   1999-Jul-20 - Added methods restart(), getResultKey()
 *    Chaminda O  1999-Jul-21 - Added method getFndUser()
 *    Mangala P   1999-Jul-28 - Added methods getLanguageValues(),getLayoutValues(),getTokenAt(),getOptions()
 *                              to populate llist boxes
 *    Mangala P   1999-Aug-03 - Added method getArchivateCheckBox()
 *    Chaminda O  1999-Aug-19 - Modified method getResultKey() to return multiple result keys.
 *    TOWR        1999-Sep-10 - Changed the archivateIsActive method
 *    Kingsly P   2001-May-23 - Change the PDF_ARCHIVE_PATH to get from ASPConfig.ifm
 *    Mangala P   2001-Jun-29 - deprecated getFields() and archiveIsActiveConfig() and remove 
 *                              obsolete codes from protected method archiveIsActive(boolean b).
 *    Mangala P   2001-Jun-29 - Improve doc comments.
 *    Ramila H    2001-Oct-05 - overloaded reportOrder() to accept parameter for sending PDF by mail.
 *    Suneth M    2001-Dec-11 - Added method getSortedBuffer().
 *    Rifki R     2002-Oct-09 - Set ContentLength property in response header in sendFile(). This is
 *                              required to display PDF responses correctly in the browser.  
 *    Rifki R     2002-Oct-11 - Overloaded sendFile() and sendPdfFile() with extra parameter "delete_after_send".  
 *    Mangala P   2003-Jan-06 - Added method getReportPDFFileName.
 *    Suneth M    2003-Mar-07 - Overloaded sendReportAsPdf(), isReportAvailableAsPdf & getReportPDFFileName. 
 *                              Added createPdfReport().
 *    Suneth M    2003-Jul-29 - Log Id 1047, Overloaded getOptions().
 *    Ramila H    2003-Aug-01 - Log id 1052,overloaded reportOrder(). 
 *    Ramila H    2003-Sep-03 - Added methods to support project Bayonet. 
 *    Ramila H    2003-Sep-30 - overloaded method createPdfReport to check whether PRINTSRV.
 *    Chandana D  2003-Oct-06 - Fixed a bug in getSortedBuffer().
 *    Ramila H    2003-Oct-10 - Checked and added ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING to pdf_archive path.
 *    Ramila H    2003-Oct-14 - Added PDF_PRINTER to fix 'inactive' bug. (undid 2003-Sep-30 stuff)
 *    Chandana D  2004-May-12 - Updated for the use of new style sheets.
 *    Ramila H    2004-05-18  - Added method for scheduled task
 *    Ramila H    2004-06-04  - Implemented PARAMETER Table functionality
 *    Ramila H    2004-06-29  - Implemented functionality for scheduled reports.
 *    Ramila H    2004-10-20  - Merged Bug 46730, implementation code.
 * ----------------------------------------------------------------------------
 * New Comments:
 *               2008/12/23 dusdlk
 * Bug 78562, Added new method createPdfReportLocale() and getPdfId( String result_key, String print_job_id ).
 *               2008/01/29 vohelk  
 * Bug 69330, Added new methods getExcelContent(), getLayoutType() , modified methods sendPDFContent() 
 *
 *               2006/06/30 buhilk 
 * Fixed Bug 58216, by removing SQL Injection threats
 *
 *               2006/01/20 rahelk
 * Added public method getTokenValue
 *
 * Revision 1.5  2005/11/11 10:02:58  rahelk
 * move method writeContentToBrowser to ASPManager
 *
 * Revision 1.4  2005/11/11 10:01:37  rahelk
 * fixed bug with writing output channel content to browser
 *
 * Revision 1.3  2005/11/10 04:34:57  rahelk
 * Online report execution for web implemented
 *
 * Revision 1.2  2005/11/02 10:27:08  rahelk
 * set cache-control/ Pragma for opening PDF content in IE
 *
 * Revision 1.1  2005/09/15 12:38:00  japase
 * *** empty log message ***
 *
 * Revision 1.6  2005/08/19 07:05:23  rahelk
 * Bug fixes and added get_defaults from expression
 *
 * Revision 1.5  2005/07/07 08:14:09  rahelk
 * changed schedule task API in-params datatype from string to date/datetime
 *
 * Revision 1.4  2005/05/26 13:45:36  kirolk
 * Merged PKG14 changes to HEAD.
 *
 * Revision 1.3  2005/05/18 04:24:08  rahelk
 * Added method to run schedules ASAP
 *
 * Revision 1.2.8.1  2005/05/18 06:01:08  mapelk
 * Added public method runTaskASAP()
 *
 * Revision 1.2  2005/02/08 08:12:18  rahelk
 * Merged Call id 121571. Reimplemented scheduled reports to be similar to general tasks. Deprecated old scheduleReport methods
 *
 *
 * ----------------------------------------------------------------------------
 */

package ifs.fnd.asp;

import ifs.fnd.buffer.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

import ifs.fnd.service.*;

//import javax.servlet.http.HttpServletResponse;


/**
 * This is a utility class for InfoServices. Here you can not directly call the 
 * constructor. 
 *
 * @see ifs.fnd.asp.ASPManager#newASPInfoServices
 * @see ifs.fnd.asp.ASPInfoServices#addFields
 */

public class ASPInfoServices extends ASPPageElement
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.asp.ASPInfoServices");
   static int     CHAR_US = IfsNames.fieldSeparator; //31
   static int     CHAR_RS = IfsNames.recordSeparator; //30;
   AutoString tmpStr = new AutoString();
   ASPBlock b;


   //==========================================================================
   //  Construction
   //==========================================================================

   ASPInfoServices( ASPPage page )
   {
      super(page);
   }

   ASPInfoServices construct()
   {
      return this;
   }

   /**
    * Overrids the clone function in the super class.
    *
    * @see ifs.fnd.asp.ASPPageElement#clone
    *
    */
   public ASPPoolElement clone( Object page ) throws FndException
   {
      ASPInfoServices i = new ASPInfoServices((ASPPage)page);
      i.setCloned();
      return i;
   }

   protected void verify( ASPPage page ) throws FndException
   {
      this.verifyPage(page);
   }

   protected void scan( ASPPage page, int level ) throws FndException
   {
      scanAction(page,level);
   }

   /**
    * Sends the given PDF file to the client browser by changing the MIME type 
    * into "application/pdf". 
    * @param filename physical path and the file name of the existing PDF file.
    * @param delete_after_send specifies whether to delete file after sending
    * 
    * @see ifs.fnd.asp.ASPInfoServices#sendFile  
    * @see ifs.fnd.asp.ASPManager#getApplicationPath 
    */

   public void sendPdfFile( String filename, boolean delete_after_send )
   {
      sendFile(filename,"application/pdf",delete_after_send);
   } 
   
   public void sendPdfFile( String filename )
   {
      sendFile(filename,"application/pdf",false);
   }   

   
   /**
    * Sends a file to the client browser.
    * @param filename physical path and the file name of the file.
    * @param content_type MIME type of the file
    */   
   public void sendFile( String filename, String content_type )
   {
      sendFile(filename, content_type, false);
   } 
   
   /**
    * Sends the given PDF file to the client browser by changing the MIME type 
    * into a given MIME type.
    * @param filename physical path and the file name of the existing PDF file.
    * @param content_type sending file's MIME type.
    * @param delete_after_send specifies whether to delete file after sending
    * 
    * @see ifs.fnd.asp.ASPInfoServices#sendPdfFile  
    * @see ifs.fnd.asp.ASPManager#getApplicationPath 
    */

   public void sendFile( String filename, String content_type, boolean delete_after_send )
   {
      try
      {
         if ( getASPManager().getAspResponseBuffered() ) getASPManager().clearResponse();
         
         byte[] data = readFile(filename);                  
         if (delete_after_send)
           getASPManager().removeTempFile(filename);
         
         writeContentToBrowser(data, content_type);

      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   private void writeContentToBrowser(byte[] data, String content_type)
   {
      try
      {
         getASPManager().writeContentToBrowser(data, content_type);
         /*
         mgr.setAspResponsContentType(content_type); 
         
         HttpServletResponse response = mgr.getAspResponse();
         
         if (response.containsHeader("Cache-Control") || response.containsHeader("Pragma"))
         {
            response.setHeader("Cache-Control","dummy");
            response.setHeader("Pragma", "dummy");
         }
         
         response.setContentLength(data.length);
         mgr.writeResponse(data);
         mgr.endResponse();
          */
      }
      catch ( Throwable any )
      {
         error(any);
      }
      
   }

   private byte[] readFile( String filename ) throws IOException
   {
      File file = new File(filename);
      int size = (int)file.length();
      byte[] bb = new byte[size];
      FileInputStream in = new FileInputStream(filename);
      in.read(bb);
      in.close();
      return bb;
   }


   private String toBase64Text( byte[] data ) throws IOException
   {
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      (new Base64()).encode(in,out);
      return out.toString();
   }

   /**
    * Return true if the PDF file is already available for the given result_key. 
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport             
    * @see ifs.fnd.asp.ASPInfoServices#addFields
    */

   public boolean isReportAvailableAsPdf( String result_key )
   {
      return isReportAvailableAsPdf(result_key ,"");
   }

   /**
    * Return true if the PDF file is already available for the given result_key and language. 
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    * @param language the language of the report.                    
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport         
    * @see ifs.fnd.asp.ASPInfoServices#addFields
    */

   public boolean isReportAvailableAsPdf( String result_key, String language )
   {
      ASPTransactionBuffer trans=getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand( "MASTER", "Archive_API.Get_Pdf_Info");

      cmd.addParameter("attr_in_", "");
      cmd.addParameter("result_key_in_", result_key);
      cmd.addParameter("lang_code_in_", language);

      trans = getASPManager().perform(trans);
      String value= trans.getValue("MASTER/DATA/attr_in_");
      //String path = GetValue("PDF_ARCHIVE_PATH", value );

      String path = getASPManager().getASPConfig().getParameter("APPLICATION/PDF_ARCHIVE_PATH");
      path += path.endsWith(ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING)?"":ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;
      String file = GetValue("PDF_FILE_NAME", value );
      
      if ( !Str.isEmpty(file) ){
         File hFile = new File(path + file);
         if ( hFile.canRead() )
            return true;
      }
      return false;
   }

   /**
    * Return the expected file name of the PDF file for the given result_key. 
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport                 
    * @see ifs.fnd.asp.ASPInfoServices#addFields
    * @see ifs.fnd.asp.ASPInfoServices@isReportAvailableAsPdf
    */

   public String getReportPDFFileName( String result_key )
   {
      return getReportPDFFileName( result_key,"" );
   }
   
   /**
    * Return the expected file name of the PDF file for the given result_key. 
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    * @param language the language of the report                        
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport                 
    * @see ifs.fnd.asp.ASPInfoServices#addFields
    * @see ifs.fnd.asp.ASPInfoServices@isReportAvailableAsPdf
    */

   public String getReportPDFFileName( String result_key, String language )
   {
      ASPTransactionBuffer trans=getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand( "MASTER", "Archive_API.Get_Pdf_Info");

      cmd.addParameter("attr_in_", "");
      cmd.addParameter("result_key_in_", result_key);
      cmd.addParameter("lang_code_in_", language);

      trans = getASPManager().perform(trans);
      String value= trans.getValue("MASTER/DATA/attr_in_");

      String path = getASPManager().getASPConfig().getParameter("APPLICATION/PDF_ARCHIVE_PATH"); 
      path += path.endsWith(ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING)?"":ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;

      String file = GetValue("PDF_FILE_NAME", value );
      if (DEBUG) 
         debug("Collected file name is: " + file + path);
      

      return path + file;
   }

   private String GetValue( String attr_name, String attr )
   {
      if ( Str.isEmpty(attr) ) return "";
      StringTokenizer st = new StringTokenizer(attr,""+IfsNames.recordSeparator);
      while ( st.hasMoreTokens() )
      {
         String field = st.nextToken();
         int pos = field.indexOf(IfsNames.fieldSeparator);
         String name  = field.substring(0,pos);
         pos++;
         String value = pos==field.length() ? null : field.substring(pos);
         if ( name.equals(attr_name) ){
            return value;
         };
      };
      return "";
   }

   /**
    * Send the PDF report for the given result_key, to the client browser by changing 
    * the MIME type into "application/pdf", if this file is already available. 
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport             
    * @see ifs.fnd.asp.ASPInfoServices#isReportAvailableAsPdf 
    * @see ifs.fnd.asp.ASPInfoServices#addFields   
    */

   public void sendReportAsPdf( String result_key )
   {
      sendReportAsPdf(result_key,"");
   }

   /**
    * Send the PDF report for the given result_key, to the client browser by changing 
    * the MIME type into "application/pdf", if this file is already available. 
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY by which the PDF file is created.
    * @param language the language of the report.                
    *
    * @see ifs.fnd.asp.ASPInfoServices#convertReportToPdf 
    * @see ifs.fnd.asp.ASPInfoServices#createPdfReport     
    * @see ifs.fnd.asp.ASPInfoServices#isReportAvailableAsPdf 
    * @see ifs.fnd.asp.ASPInfoServices#addFields   
    */

   public void sendReportAsPdf( String result_key, String language )
   {
      ASPTransactionBuffer trans=getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand( "MASTER", "Archive_API.Get_Pdf_Info");

      cmd.addParameter("attr_", "");
      cmd.addParameter("result_key_", result_key);
      cmd.addParameter("lang_code_", language);

      trans = getASPManager().perform(trans);
      String value = trans.getValue("MASTER/DATA/attr_");
      //String path = GetValue("PDF_ARCHIVE_PATH", value );
      String path = getASPManager().getASPConfig().getParameter("APPLICATION/PDF_ARCHIVE_PATH");
      path += path.endsWith(ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING)?"":ifs.fnd.os.OSInfo.OS_SEPARATOR_STRING;
      String file = GetValue("PDF_FILE_NAME", value );

      if ( !Str.isEmpty(file) ){
         File hFile = new File(path + file);
         if ( hFile.canRead() )
            sendFile( path + file, "application/pdf" );
      }
   }

   /**
    * Covert the report for the given RESULT_KEY into a PDF report and this report can be 
    * accessible here after.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    * <PRE>    
    * Example:
    * <P>
    *    //note that info is an instance of the ASPInfoServices class
    *    String key= "1000011"; //should give the correct RESULT_KEY
    *    if( info.isReportAvailableAsPdf( key ) )
    *       info.sendReportAsPdf( key );
    *    else
    *    {
    *       info.convertReportToPdf( key );
    *       getASPManager().showAlert("FNDPAGESREPORTARCHIVEPDFCONVERT: A job was created that will convert the report to PDF. Redo the action in about a minute.");
    *    }
    * </P>
    * </PRE>
    *
    * @param result_key corresponding RESULT_KEY from which the PDF file should be created.
    *
    * @see ifs.fnd.asp.ASPInfoServices#sendReportAsPdf 
    * @see ifs.fnd.asp.ASPInfoServices#isReportAvailableAsPdf
    * @see ifs.fnd.asp.ASPInfoServices#getResultKey
    * @see ifs.fnd.asp.ASPInfoServices#addFields     
    * 
    */

   public void convertReportToPdf( String result_key )
   {
      ASPTransactionBuffer trans=getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand( "MASTER", "Print_Job_API.Archivate");

      cmd.addParameter("job_id_", "");
      cmd.addParameter("pdf_result_key_", result_key);

      trans = getASPManager().perform(trans);
   }

   /**
    * Create a PDF report for the given RESULT_KEY and this report can be accessible here after.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY from which the PDF file should be created.
    * @param layout the layout of the report.
    * @param language the language of the report.            
    *
    * @see ifs.fnd.asp.ASPInfoServices#sendReportAsPdf 
    * @see ifs.fnd.asp.ASPInfoServices#isReportAvailableAsPdf
    * @see ifs.fnd.asp.ASPInfoServices#getResultKey
    * @see ifs.fnd.asp.ASPInfoServices#addFields     
    * 
    */

   public String createPdfReport( String result_key, String layout, String language, boolean is_printsrv )
   {
      return createPdfReport( result_key, layout, language);
   }
   
   public String createPdfReport( String result_key, String layout, String language )
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = null;

      String pdf_printer_id = "";
      //if printer_id is null then state = 'inactive'
      trans.addCustomFunction("PDFPRINT", "Print_Server_SYS.Get_Pdf_Printer", "CLIENT_VALUES1"); 
      
      cmd = trans.addCustomCommand("REPORT_IDENTITY", "Archive_API.Get_Report_Identity");
      cmd.addParameter("f1"); // report_id
      cmd.addParameter("f2",result_key);
      
      cmd = trans.addCustomFunction("LAYOUT_TYPE", "Report_Layout_Definition_API.Get_Layout_Type","f0");
      cmd.addReference("f1", "REPORT_IDENTITY/DATA");
      cmd.addParameter("f2", layout);

      cmd = trans.addCustomFunction("TYPE_OWNER", "Report_Layout_Type_Config_API.Get_Layout_Type_Owner","f3");
      cmd.addReference("f0", "LAYOUT_TYPE/DATA");
      
      trans = mgr.perform(trans);

      String layout_type_owner = trans.getValue("TYPE_OWNER/DATA/f3");
      pdf_printer_id = "PRINTER_ID" + (char)31 + ("AGENT".equals(layout_type_owner)?"":trans.getValue("PDFPRINT/DATA/CLIENT_VALUES1")) + (char)30; 
      trans.clear();

      cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
      cmd.addParameter("f0",null);
      cmd.addParameter("f1",pdf_printer_id);
      trans = mgr.perform(trans);

      String pdf_job_id = trans.getValue("PJOBNEW/DATA/f0");

      String idd_pdf = "PRINT_JOB_ID" + (char)31 
                       + pdf_job_id + (char)30 
                       + "RESULT_KEY" + (char)31 
                       + result_key + (char)30 
                       + "LAYOUT_NAME" + (char)31 
                       + layout + (char)30
                       +  "LANG_CODE" + (char)31
                       + language + (char)30
                       +  "OPTIONS" + (char)31
                       + "" + (char)30;     
      trans.clear();

      cmd = trans.addCustomCommand("NEWINS", "Print_Job_Contents_API.New_Instance");
      cmd.addParameter("f2", idd_pdf);

      cmd = trans.addCustomCommand("RPRINT", "Print_Job_API.Print");
      cmd.addParameter("f3", pdf_job_id);

      trans = mgr.perform(trans);
      
      return pdf_job_id;
   }

   /**
    * Create a PDF report for the given RESULT_KEY and this report can be accessible here after.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    *
    * @param result_key corresponding RESULT_KEY from which the PDF file should be created.
    * @param layout the layout of the report.
    * @param language the language of the report.   
    * @param locale corresponds to the current locale setting.          
    *
    * @see ifs.fnd.asp.ASPInfoServices#sendReportAsPdf 
    * @see ifs.fnd.asp.ASPInfoServices#isReportAvailableAsPdf
    * @see ifs.fnd.asp.ASPInfoServices#getResultKey
    * @see ifs.fnd.asp.ASPInfoServices#addFields     
    * 
    */   
   public String createPdfReportLocale( String result_key, String layout, String language, String Locale )
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = null;
      
      String locale_lang = Locale.substring(0,Locale.indexOf("-"));
      String locale_country = Locale.substring(Locale.indexOf("-")+1,Locale.length());

      String pdf_printer_id = "";
      //if printer_id is null then state = 'inactive'
      trans.addCustomFunction("PDFPRINT", "Print_Server_SYS.Get_Pdf_Printer", "CLIENT_VALUES1"); 
      
      cmd = trans.addCustomCommand("REPORT_IDENTITY", "Archive_API.Get_Report_Identity");
      cmd.addParameter("f1"); // report_id
      cmd.addParameter("f2",result_key);
      
      cmd = trans.addCustomFunction("LAYOUT_TYPE", "Report_Layout_Definition_API.Get_Layout_Type","f0");
      cmd.addReference("f1", "REPORT_IDENTITY/DATA");
      cmd.addParameter("f2", layout);

      cmd = trans.addCustomFunction("TYPE_OWNER", "Report_Layout_Type_Config_API.Get_Layout_Type_Owner","f3");
      cmd.addReference("f0", "LAYOUT_TYPE/DATA");
      
      trans = mgr.perform(trans);

      String layout_type_owner = trans.getValue("TYPE_OWNER/DATA/f3");
      pdf_printer_id = "PRINTER_ID" + (char)31 + ("AGENT".equals(layout_type_owner)?"":trans.getValue("PDFPRINT/DATA/CLIENT_VALUES1")) + (char)30; 
      trans.clear();

      cmd = trans.addCustomCommand("PJOBNEW", "Print_Job_API.New");
      cmd.addParameter("f0",null);
      cmd.addParameter("f1",pdf_printer_id);
      trans = mgr.perform(trans);

      String pdf_job_id = trans.getValue("PJOBNEW/DATA/f0");

      String idd_pdf = "PRINT_JOB_ID" + (char)31 
                       + pdf_job_id + (char)30 
                       + "RESULT_KEY" + (char)31 
                       + result_key + (char)30 
                       + "LAYOUT_NAME" + (char)31 
                       + layout + (char)30
                       +  "LANG_CODE" + (char)31
                       + language + (char)30
                       +  "OPTIONS" + (char)31
                       + "" + (char)30
                       + "LOCALE_LANGUAGE" + (char)31
                       + locale_lang + (char)30
                       + "LOCALE_COUNTRY" + (char)31
                       + locale_country + (char)30;     
      trans.clear();

      cmd = trans.addCustomCommand("NEWINS", "Print_Job_Contents_API.New_Instance");
      cmd.addParameter("f2", idd_pdf);

      cmd = trans.addCustomCommand("RPRINT", "Print_Job_API.Print");
      cmd.addParameter("f3", pdf_job_id);

      trans = mgr.perform(trans);
      
      return pdf_job_id;
   }   

   /**
    * Return true if the config parameter "APPLICATION/PDF_ARCHIVE_PATH" contains PDF archive 
    * path.
    */

   public boolean archivateIsActive()
   {
      return archivateIsActive(false);
   }

   /**
    * @deprecated This method is deprecated and use archivateIsActive() method instead of this.
    * 
    * @see ifs.fnd.asp.ASPInfoServices#archivateIsActive 
    */
   public boolean archivateIsActiveConfig()
   {
      return archivateIsActive(true);
   }


   /**
    * Return true if the config parameter "APPLICATION/PDF_ARCHIVE_PATH" contains PDF archive 
    * path.
    *
    * @param isConfig This parameter dose not have a meaning due to recent changes. It should 
    * be just a boolean.
    */

   protected boolean archivateIsActive(boolean isConfig)
   {
      String path = getASPManager().getASPConfig().getParameter("APPLICATION/PDF_ARCHIVE_PATH");
      if ( path != "" )
         return true;
      else
         return false;
   }

   /**
    * Returns attribute item with the given name and the value. Item string is generated by concatanating
    * the name,the field_separator,the value and the record_separator. It is not recomended to use this 
    * method unless you have any more to do with the ASPInfoServices class.
    * @param item the name of the item
    * @param value the value of the item
    *
    * @see ifs.fnd.service.IfsNames#fieldSeparator 
    * @see ifs.fnd.service.IfsNames#recordSeparator 
    */

   public String itemValueCreate(String item, String value)
   {
      tmpStr.clear();
      tmpStr.append(item);
      tmpStr.append((char)CHAR_US);
      tmpStr.append(value);
      tmpStr.append((char)CHAR_RS );
      return(tmpStr.toString());
   }


   public String getAttrItemValue(String attr_name, String attr)
   {
      return GetValue(attr_name, attr);
   }
   
   /**
    * Orders new report for a given report id.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.       
    *
    * @param sReportId the ordering report id.
    * @param sLayoutName the layout name
    * @param isParameters parameters as an attribute string
    * @param sBatchType this is called execution plan of the report. It can have following values.
    * <pre>
    * "ASAP"  Report will execute as soon as posible.
    * "ON date AT time"  Report will execute on the given date at given time.
    * "WEKLY ON mon;tue;wed;thu;fri;sat;sun AT time"  Report executes on given days at given 
    * time every week. 
    * </pre>    
    * @param sJobName the job name. 
    * @param sMessage the message.
    * @param sPrinter the printer id.
    * @param sArchivate whether archivate "TRUE" or "FALSE".
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */
   public String reportOrder( String sReportId,
                              String sLayoutName,
                              String isParameters,
                              String sBatchType,
                              String sJobName,
                              String sMessage,
                              String sPrinter,
                              String sArchivate)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      String fnduser = getFndUser()+(char)CHAR_US;

      ASPCommand cmd = trans.addCustomCommand("REPORDER", "Agenda_API.New_Job");
      cmd.addParameter("f0");
      cmd.addParameter("f1",itemValueCreate("REPORT_ID", sReportId)+itemValueCreate("LAYOUT_NAME", sLayoutName));
      cmd.addParameter("f2",isParameters);
      cmd.addParameter("f3",itemValueCreate("EXEC_PLAN", sBatchType)+itemValueCreate("JOB_NAME", sJobName)+itemValueCreate("MESSAGE_TYPE", sMessage)+itemValueCreate("PRINTER_ID", sPrinter)+itemValueCreate("ARCHIVATE", sArchivate));
      cmd.addParameter("f4",fnduser);
      trans=getASPManager().perform(trans);
      tmpStr.clear();
      tmpStr.append(trans.getValue("REPORDER/DATA/f0"));  

      return(tmpStr.toString());   
   }

   
   /**
    * Orders new report for a given report id.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.       
    *
    * @param sReportId the ordering report id.
    * @param sLayoutName the layout name
    * @param isParameters parameters as an attribute string
    * @param sBatchType this is called execution plan of the report. It can have following values.
    * <pre>
    * "ASAP"  Report will execute as soon as posible.
    * "ON date AT time"  Report will execute on the given date at given time.
    * "WEKLY ON mon;tue;wed;thu;fri;sat;sun AT time"  Report executes on given days at given 
    * time every week. 
    * </pre>    
    * @param sJobName the job name. 
    * @param sMessage the message.
    * @param sPrinter the printer id.
    * @param sArchivate whether archivate "TRUE" or "FALSE".
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */
   public String reportOrder( String sReportId,
                              String sLayoutName,
                              String isParameters,
                              String sBatchType,
                              String sJobName,
                              String sMessage,
                              String sPrinter,
                              String sArchivate,
                              String sSendPdf,
                              String sSendPdfTo)
   {
      return reportOrder(sReportId,sLayoutName,isParameters,sBatchType,sJobName,
                         sMessage,sPrinter,sArchivate,sSendPdf,sSendPdfTo,null);
   }
   

   /**
    * Orders new report for a given report id.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.       
    *
    * @param sReportId the ordering report id.
    * @param sLayoutName the layout name
    * @param isParameters parameters as an attribute string
    * @param sBatchType this is called execution plan of the report. It can have following values.
    * <pre>
    * "ASAP"  Report will execute as soon as posible.
    * "ON date AT time"  Report will execute on the given date at given time.
    * "WEKLY ON mon;tue;wed;thu;fri;sat;sun AT time"  Report executes on given days at given 
    * time every week. 
    * </pre>    
    * @param sJobName the job name. 
    * @param sMessage the message "NONE", "PRINTER", or "EMAIL".
    * @param sPrinter the printer id.
    * @param sArchivate whether archivate "TRUE" or "FALSE".
    * @param sSendPdf send a pdf "TRUE" or "FALSE".
    * @param sSendPdfTo email address where to send the pdf.
    * @param sLanguage report language.
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */
   public String reportOrder( String sReportId,
                              String sLayoutName,
                              String isParameters,
                              String sBatchType,
                              String sJobName,
                              String sMessage,
                              String sPrinter,
                              String sArchivate,
                              String sSendPdf,
                              String sSendPdfTo,
                              String sLanguage)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      String fnduser = getFndUser()+(char)CHAR_US;

      ASPCommand cmd = trans.addCustomCommand("REPORDER", "Agenda_API.New_Job");
      cmd.addParameter("f0");
      cmd.addParameter("f1",itemValueCreate("REPORT_ID", sReportId)+
                            itemValueCreate("LAYOUT_NAME", sLayoutName)+
                            itemValueCreate("LANG_CODE", sLanguage));
      cmd.addParameter("f2",isParameters);
      cmd.addParameter("f3",itemValueCreate("EXEC_PLAN", sBatchType)+
                            itemValueCreate("JOB_NAME", sJobName)+
                            itemValueCreate("MESSAGE_TYPE", sMessage)+
                            itemValueCreate("PRINTER_ID", sPrinter)+
                            itemValueCreate("ARCHIVATE", sArchivate)+
                            itemValueCreate("SEND_PDF", sSendPdf)+
                            ("TRUE".equals(sSendPdf)?itemValueCreate("SEND_PDF_TO", sSendPdfTo):"") );
      
      cmd.addParameter("f4",fnduser);
      trans=getASPManager().perform(trans);
      //tmpStr.clear();
      //tmpStr.append(trans.getValue("REPORDER/DATA/f0"));  

      return(trans.getValue("REPORDER/DATA/f0"));   
   }
   
   public String onlineReportOrder(String report_attr, String parameter_attr)
   {
      String result_key = null;
      
      String fnduser = getFndUser()+(char)CHAR_US;
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("REPORDER", "Archive_API.New_Client_Report");
      cmd.addParameter("f0"); //result_key
      cmd.addParameter("f1",report_attr);
      cmd.addParameter("f2",parameter_attr);
      cmd.addParameter("f3",fnduser);
      cmd.addParameter("f4",null);
      
      trans=getASPManager().perform(trans);
      result_key = trans.getValue("REPORDER/DATA/f0");  
      
      return result_key;
   }
   

   /*
    *@deprecated use newScheduledTask
    */   
   public String newScheduleReport( String sReportId,String sLayoutName,
                                    String isParameters,
                                    String schedule_name,String schedule_method_id,String active_db,
                                    String exec_plan,String start_date,String stop_date,
                                    String next_exec_date,String params,
                                    String sMessage,String sPrinter,
                                    String sArchivate,String sSendPdf,String sSendPdfTo,
                                    String sLanguage)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      String fnduser = getFndUser()+(char)CHAR_US;

      ASPCommand cmd = trans.addCustomCommand("REPORDER", "Scheduled_Report_API.New");
      cmd.addParameter("f0");
      cmd.addParameter("f1",itemValueCreate("REPORT_ID", sReportId)+
                            itemValueCreate("LAYOUT_NAME", sLayoutName)+
                            itemValueCreate("LANG_CODE", sLanguage));
      cmd.addParameter("f2",isParameters);
      //schedule attr
      cmd.addParameter("f3",itemValueCreate("SCHEDULE_NAME",schedule_name) +
                            itemValueCreate("SCHEDULE_METHOD_ID",schedule_method_id)+
                            itemValueCreate("ACTIVE_DB",active_db)+
                            itemValueCreate("EXECUTION_PLAN",exec_plan)+
                            itemValueCreate("START_DATE",start_date)+
                            itemValueCreate("STOP_DATE",stop_date)+
                            itemValueCreate("NEXT_EXECUTION_DATE",next_exec_date)+
                            itemValueCreate("PARAMETERS",params));
      //message attr
      cmd.addParameter("f4",itemValueCreate("MESSAGE_TYPE", sMessage)+
                            itemValueCreate("PRINTER_ID", sPrinter));
      //archive attr
      cmd.addParameter("f5",itemValueCreate("ARCHIVATE", sArchivate)+
                            itemValueCreate("SEND_PDF", sSendPdf)+
                            ("TRUE".equals(sSendPdf)?itemValueCreate("SEND_PDF_TO", sSendPdfTo):"") );
      //distribution list                            
      cmd.addParameter("f6",fnduser);
      trans=getASPManager().perform(trans);
      tmpStr.clear();
      tmpStr.append(trans.getValue("REPORDER/DATA/f0"));  

      return(tmpStr.toString());   
   }
   

   public String newScheduledTask(String method_name,
                                  String schedule_name,
                                  String next_exec_date,
                                  String start_date,
                                  String stop_date,
                                  String active_db,
                                  String exec_plan,
                                  String lang_code,
                                  String installation_id,
                                  ASPRowSet paramset)
   {
      return newScheduledTask(method_name,schedule_name,next_exec_date,
                              start_date,stop_date,active_db,exec_plan,
                              lang_code,installation_id,"",paramset);
   }
   
   public String newScheduledTask(String method_name,
                                  String schedule_name,
                                  String next_exec_date,
                                  String start_date,
                                  String stop_date,
                                  String active_db,
                                  String exec_plan,
                                  String lang_code,
                                  String installation_id,
                                  String external_id,
                                  ASPRowSet paramset)
   {
      String schedule_id = "";
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("NEW_SCHED","Batch_SYS.New_Batch_Schedule");
      cmd.addParameter("n1");
      cmd.addParameter("d1",next_exec_date);
      cmd.addParameter("d2",start_date);
      cmd.addParameter("d2",stop_date);
      cmd.addParameter("f4",schedule_name);
      cmd.addParameter("f5",method_name); 
      cmd.addParameter("f6",active_db);
      cmd.addParameter("f7",exec_plan);
      cmd.addParameter("f8",lang_code);
      cmd.addParameter("f9",installation_id);
      cmd.addParameter("f10",external_id);
      
      trans = getASPManager().perform(trans);
      schedule_id = trans.getValue("NEW_SCHED/DATA/n1");
      //could not use addRerence b'cas the New_Batch_Schedule_Param method as been overloaded 
      //therefore the proper datatype is required.
      trans.clear();
      
      saveParameters(trans, paramset, schedule_id, ifs.fnd.pages.ScheduledTask.SCHED_REP_METHOD_NAME.equals(method_name));
      trans = getASPManager().perform(trans);
      
      return schedule_id;
   }

   
   public void modifyScheduledTask(String schedule_id,
                                   String schedule_name,
                                   String next_exec_date,
                                   String start_date,
                                   String stop_date,
                                   String active_db,
                                   String exec_plan,
                                   String lang_code,
                                   boolean is_schd_rep,
                                   ASPRowSet paramset)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      
      ASPCommand remcmd = trans.addCustomCommand("REM_PARAMS","Batch_Schedule_API.Remove_Parameters__");
      remcmd.addInParameter("n1", schedule_id);
      
      ASPCommand cmd = trans.addCustomCommand("MODIFY_SCHED","Batch_SYS.Modify_Batch_Schedule");
      cmd.addParameter("d1",next_exec_date);
      cmd.addParameter("d2",start_date);
      cmd.addParameter("d2",stop_date);
      cmd.addParameter("f3",schedule_id);
      cmd.addParameter("f4",schedule_name);
      cmd.addParameter("f5",active_db);
      cmd.addParameter("f6",exec_plan);
      cmd.addParameter("f7",lang_code);
      
      saveParameters(trans, paramset, schedule_id, is_schd_rep);
      
      getASPManager().perform(trans);
      
   }
   
   private void saveParameters(ASPTransactionBuffer trans, ASPRowSet paramset, String schedule_id, boolean is_scheduled_report)
   {
      int rows = paramset.countRows();
      
      if (rows <1) return;
      
      for (int i=0; i<rows; i++)
      {
         ASPCommand cmd = trans.addCustomCommand("PARAM"+i,"Batch_SYS.New_Batch_Schedule_Param");
         cmd.addParameter("n1"); // for seq_no
         //if (schedule_id == null)
         //   cmd.addInReference("n2","NEW_SCHED/DATA","f0");  //returned schedule_id for new task
         //else
         cmd.addInParameter("n2",schedule_id); 
         
         String param_name = paramset.getValueAt(i,"PARAM_NAME");
         String param_value = paramset.getValueAt(i,"PARAM_VALUE");
         
         if (is_scheduled_report && "REPORT_ATTR".equals(param_name))
            param_value += itemValueCreate("SCHEDULE_ID",schedule_id);
            
         cmd.addInParameter("f3",param_name);
         cmd.addInParameter("f4",param_value);
      }
      
   }
   
   /*
    *@deprecated use newScheduledTask
    */   
   public String newScheduledReport(String report_attr_,
                                    String parameter_attr_,
                                    String schedule_attr,
                                    String message_attr_,
                                    String archiving_attr_)
                                    
   {
      String schedule_id = "";
      String fnduser = getFndUser()+(char)CHAR_US;
      
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("NEW_SCHED_REP","Scheduled_Report_API.New");
      cmd.addParameter("f0");
      cmd.addParameter("f1",report_attr_); 
      cmd.addParameter("f2",parameter_attr_);
      cmd.addParameter("f3",schedule_attr);
      cmd.addParameter("f4",message_attr_);
      cmd.addParameter("f5",archiving_attr_); 
      cmd.addParameter("f6",fnduser);
      
      trans = getASPManager().perform(trans);
      schedule_id = trans.getValue("NEW_SCHED_REP/DATA/f0");
      
      return schedule_id;
      
   }

   /*
    *@deprecated use modifyScheduledTask
    */   
   public void modifyScheduledReport(String schedule_id,
                                       String report_attr_,
                                       String parameter_attr_,
                                       String schedule_attr,
                                       String message_attr_,
                                       String archiving_attr_)
                                    
   {
      String fnduser = getFndUser()+(char)CHAR_US;
      
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("MODIFY_SCHED_REP","Scheduled_Report_API.Modify");
      cmd.addParameter("f0",schedule_id);
      cmd.addParameter("f1",report_attr_); 
      cmd.addParameter("f2",parameter_attr_);
      cmd.addParameter("f3",schedule_attr);
      cmd.addParameter("f4",message_attr_);
      cmd.addParameter("f5",archiving_attr_); 
      cmd.addParameter("f6",fnduser);
      
      trans = getASPManager().perform(trans);
      
   }
   
   /**
    * Run a task scheduled as ASAP
    * @param schedule_id scheduled id of the task you wish to run in ASAP mode.
    */
   public void runTaskASAP(String schedule_id)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomCommand("RUN_SCHED","Batch_Schedule_API.Run_Batch_Schedule__");
      cmd.addParameter("SCHEDULE_ID",schedule_id);
      cmd.addParameter("ONLINE","FALSE");
      
      mgr.perform(trans);
   }

   
   /**
    * Runs the given job now.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.     
    * @param jobid job id to be executed. 
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */
   public void agendaExecute(String jobId)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("AGENDAEXECUTE", "Agenda_API.Run_Job_Now");
      cmd.addParameter("f0",jobId);
      trans = getASPManager().perform(trans);
   }

   /**
    * Restart the given job.
    * ifs.fnd.asp.ASPInfoServices.addFields() method should be called in your preDefine() 
    * before calling this method.   
    * @param job_id job id to be restarted.
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */

   public void restart(String job_id)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("PRINT", "Print_Job_API.Print");
      cmd.addParameter("f0",job_id);
      getASPManager().submit(trans);
   }


   /**
    * Returns the result key of the given job. ifs.fnd.asp.ASPInfoServices.addFields() method should 
    * be called in your preDefine() before calling this method.   
    * @param job_id the job id.
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */

   public String getResultKey(String job_id)
   { 
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("INSTANT_ATTR", "Print_Job_Contents_API.Get_Instance_Attr");
      cmd.addParameter("f0");
      cmd.addParameter("f1", job_id);
      trans = getASPManager().submit(trans);

      String str = new String (trans.getValue("INSTANT_ATTR/DATA/f0"));

      tmpStr.clear();

      int i = str.indexOf("INSTANCE_SEQ");
      while ( i>=0 )
      {
         str = str.substring(i);
         tmpStr.append(GetValue("RESULT_KEY",str)+(char)CHAR_US+GetValue("INSTANCE_SEQ",str)+(char)CHAR_RS);
         i = str.indexOf("INSTANCE_SEQ",12);
      }
      return(tmpStr.toString());
   }


   /**
    * Used to add ASPFields to the block. Should be called from the preDefine method of 
    * the pages which use the methods of this class.
    */

   public void addFields()
   {
      b = getASPManager().newASPBlock("blk");
      b.addField("f0");
      b.addField("f1");
      b.addField("f2");
      b.addField("f3");
      b.addField("f4");
      b.addField("f5");
      b.addField("f6");
      b.addField("f7");
      b.addField("f8");
      b.addField("f9");
      b.addField("f10");

      b.addField("d1","Datetime");
      b.addField("d2","Date");
      
      b.addField("n1","Number");
      b.addField("n2","Number");

      b.addField( "result_key_","Number" ,"00000");
      b.addField( "lang_code_" ); 
      b.addField( "attr_" );

      b.addField( "result_key_in_","Number" ,"00000");
      b.addField( "lang_code_in_" );
      b.addField( "attr_in_" ); 

      b.addField( "job_id_","Number" ,"00000");
      b.addField( "pdf_result_key_","Number" ,"00000");

      b.addField( "result_key_in2_","Number" ,"00000");
      b.addField( "lang_code_in2_" ); 
      b.addField( "attr_in2_" );    
   }

   /**
    * @deprecated There is no meaning of calling this method.
    */
   public void getFields()
   {
      b = getASPPage().getASPBlock("blk");
   }


   /**
    * Returns the currently logged in Foundation user. ifs.fnd.asp.ASPInfoServices.addFields() 
    * method should be called in your preDefine() before calling this method. Never use this 
    * method unless you have any more to do with the ASPInfoServices class. You can get the 
    * FND_USER by calling "FND_SESSION_API.Get_Fnd_User" as a custom function.
    *
    * @see ifs.fnd.asp.ASPInfoServices#addFields         
    */
   public String getFndUser()
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      trans.addCustomFunction("FNDUSER", "FND_SESSION_API.Get_Fnd_User","f0");
      trans=getASPManager().perform(trans);
      return(trans.getValue("FNDUSER/DATA/f0"));
   }


   /**
    * To collect language codes, language description into seperate strings 
    * and return the concatanated string of both.
    * @param attr An Attribute string which has language code and language description
    * as attributes.
    */
   public String getLanguageValues(String attr)
   {
      if (getASPManager().isEmpty(attr)) return "";
      
      StringTokenizer st = new StringTokenizer(attr,""+IfsNames.recordSeparator);
      String langCode = new String();
      String langDesc = new String();
      while ( st.hasMoreTokens() )
      {
         String field = st.nextToken();
         int pos = field.indexOf(IfsNames.fieldSeparator);
         langCode = langCode + field.substring(0,pos) + IfsNames.fieldSeparator; 
         pos++; // jump over the field seperator index
         langDesc = langDesc + field.substring(pos) + IfsNames.fieldSeparator;
      }
      return(langCode + IfsNames.recordSeparator + langDesc);
   }

   /**
    * To collect layout codes, layout description and order by into seperate strings
    * and return the concatanated string of them.
    * @param attr attribute string which contains layout codes, layout description 
    * and order by for all layouts seperated with IfsNames.recordSeparator and 
    * IfsNames.fieldSeparator.
    * @see ifs.fnd.service.IfsNames#recordSeparator 
    * @see ifs.fnd.service.IfsNames#fieldSeparator 
    */
   public String getLayoutValues(String attr)
   {
      if (getASPManager().isEmpty(attr)) return "";
      
      StringTokenizer st = new StringTokenizer(attr,""+IfsNames.recordSeparator);
      String layoutNames = new String();
      String layoutDescs = new String();
      String layoutOrderBy = new String();
      while ( st.hasMoreTokens() )
      {
         String field = st.nextToken();
         int pos = field.indexOf(IfsNames.fieldSeparator);
         layoutNames = layoutNames + field.substring(0,pos) + IfsNames.fieldSeparator;
         pos++;
         field = field.substring(pos);
         pos = field.indexOf(IfsNames.fieldSeparator);
         layoutDescs = layoutDescs + field.substring(0,pos) + IfsNames.fieldSeparator;
         pos++;
         layoutOrderBy = layoutOrderBy + field.substring(pos)+ IfsNames.fieldSeparator;
      }
      return(layoutNames + IfsNames.recordSeparator + layoutDescs + IfsNames.recordSeparator + layoutOrderBy);
   }

      /**
    * Return the token value from a given attribute string.
    * @param attr IFS standard attribute string.
    * @param token_name name of token to b retrieved
    * @see getTokenAt
    */
   public String getTokenValue(String attr, String token_name)
   {
      return GetValue( token_name, attr );   
   }
   
   /**
    * Return the token value at position i from a given attribute string.
    * @param attr IFS standard attribute string.
    * @param i position
    * @see getTokenValue
    */
   public String getTokenAt(String attr, int i)
   {
      StringTokenizer st = new StringTokenizer(attr,""+IfsNames.recordSeparator);
      String field = new String();

      int j = 0;
      while ( st.hasMoreTokens() && j <= i )
      {
         j++;
         field = st.nextToken();
         if ( j == i )
            return field;
      }
      return "";
   }

   /**
    * Return option tag values for a mandatory select box using the given name, value strings.
    * @param name attribute string with names seperated by IfsNames.fieldSeparator
    * @param value attribute string with value seperated by IfsNames.fieldSeparator
    * @see ifs.fnd.service.IfsNames#fieldSeparator 
    */
   public String getOptions(String name, String value)
   { 
      return getOptions(name,value,"");
   }

   /**
    * Return option tag values with a selected value for a mandatory select box using the 
    * given name, value strings.
    * @param name attribute string with names seperated by IfsNames.fieldSeparator
    * @param value attribute string with value seperated by IfsNames.fieldSeparator
    * @param key selected value
    * @see ifs.fnd.service.IfsNames#fieldSeparator 
    */
   public String getOptions(String name, String value, String key)
   {
      return getOptions(name, value, key, true);
   }
   
   public String getOptions(String name, String value, String key, boolean mandatory)
   { 
      AutoString tmpStr    = new AutoString();

      if (!mandatory)
         tmpStr.append("<option value=''></option>");

      int i = name.indexOf(IfsNames.fieldSeparator);
      int j = value.indexOf(IfsNames.fieldSeparator);
      
      while ( i > 0 )
      {
         if ((value.substring(0,j)).equals(key)) 
            tmpStr.append("<option selected value=\""+ value.substring(0,j) +"\">" + name.substring(0,i) + "</option>");
         else
            tmpStr.append("<option value=\""+ value.substring(0,j) +"\">" + name.substring(0,i) + "</option>");
         name = name.substring(++i);
         value = value.substring(++j);    
         i = name.indexOf(IfsNames.fieldSeparator);
         j = value.indexOf(IfsNames.fieldSeparator);    
      }
      return tmpStr.toString();
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
      if ( "ALWAYS ON".equals(strArchivate) ) // to show read only check box which is checked
         return("&nbsp;<IMG SRC=\"../images/check_box_marked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      else if ( "OFF".equals(strArchivate) )// to show read only check box which is not checked
         return("&nbsp;<IMG SRC=\"../images/check_box_unmarked.gif\"  BORDER=0 HEIGHT=13 WIDTH=13>");
      else if ( "DEFAULT ON".equals(strArchivate) )// to show check box which is default checked
         return("<input type=\"checkbox\" class='checkbox' name=\"ARCHIVATE\" value=\"ON\" checked tabindex=\"1\">");
      else // to show check box which is default unchecked
         return("<input type=\"checkbox\" class='checkbox' name=\"ARCHIVATE\" value=\"ON\" tabindex=\"1\">");

   }

   /**
    * Return the sorted ASPBuffer for the given attribute string . 
    * @param attr attribute string with values seperated by IfsNames.fieldSeparator
    */
   public ASPBuffer getSortedBuffer(String attr)
   {
       ASPBuffer sub = null;
       ASPBuffer sortedBuffer = getASPManager().newASPBuffer();
       if(attr != null)
       {
           StringTokenizer st = new StringTokenizer(attr,""+IfsNames.fieldSeparator);
           String token = null;          

           while (st.hasMoreTokens())
           {
              sub = getASPManager().newASPBuffer();
              sub = sortedBuffer.addBuffer("DATA");
              token = st.nextToken();
              sub.addItem("VALUE",token); 
              sub.addItem("NAME",token); 
           }
           sortedBuffer.sort("VALUE",true);
       }
       else
       {
           sub = getASPManager().newASPBuffer();
           sub = sortedBuffer.addBuffer("DATA");
           sub.addItem("DUMMY",""); 
       }          
       return sortedBuffer;
   }
   
   /**
    * Returns the owner of the layout type. 
    */
   public String getLayoutTypeOwner(String report_id, String layout_name)
   {
      String layout_type_owner ="";
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = null;
      
      //Bug 46730, start
      if (mgr.isEmpty(layout_name))
      {
         cmd = trans.addCustomFunction("DEFAULT_LAYOUT", "Report_Layout_Definition_API.Get_Default_Layout","f2");
         cmd.addParameter("f1", report_id);
      }
      
      cmd = trans.addCustomFunction("LAYOUT_TYPE", "Report_Layout_Definition_API.Get_Layout_Type","f0");
      cmd.addParameter("f1", report_id);
      if (mgr.isEmpty(layout_name))
         cmd.addReference("f2", "DEFAULT_LAYOUT/DATA");
      else
         cmd.addParameter("f2", layout_name);
      //Bug 46730, end

      cmd = trans.addCustomFunction("TYPE_OWNER", "Report_Layout_Type_Config_API.Get_Layout_Type_Owner","f3");
      cmd.addReference("f0", "LAYOUT_TYPE/DATA");
      
      trans = mgr.perform(trans);
      
      if (! mgr.isEmpty(trans.getValue("TYPE_OWNER/DATA/f3"))) 
      {
         layout_type_owner = trans.getValue("TYPE_OWNER/DATA/f3");
      }
      
      return layout_type_owner;
   }

   /**
    * Returns the type of the layout_name. 
    */
   public String getLayoutType(String report_id, String layout_name)
   {
      String layout_type ="";
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = null;
      
      if (getASPManager().isEmpty(layout_name))
      {
         cmd = trans.addCustomFunction("DEFAULT_LAYOUT", "Report_Layout_Definition_API.Get_Default_Layout","f2");
         cmd.addParameter("f1", report_id);
      }
      
      cmd = trans.addCustomFunction("LAYOUT_TYPE", "Report_Layout_Definition_API.Get_Layout_Type","f0");
      cmd.addParameter("f1", report_id);
      if (getASPManager().isEmpty(layout_name))
         cmd.addReference("f2", "DEFAULT_LAYOUT/DATA");
      else
         cmd.addParameter("f2", layout_name);

      cmd = trans.addCustomFunction("LAYOUT_TYPE_DB", "Report_Layout_Type_API.Encode","f1");
      cmd.addReference("f0", "LAYOUT_TYPE/DATA");
      
      trans = getASPManager().perform(trans);
      
      layout_type = trans.getValue("LAYOUT_TYPE_DB/DATA/f1");
      return layout_type;
   }
   
   
   //Bug 46730, start
   /**
    * This is an obsolete method. 
    */
   public void sendPDFContents( String result_key, String layout_name, String lang_code )
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = null;
      cmd = trans.addCustomCommand("REPORT_IDENTITY", "Archive_API.Get_Report_Identity");
      cmd.addParameter("f1"); // report_id
      cmd.addParameter("f2",result_key);
      
      cmd = trans.addCustomFunction("LAYOUT_TYPE", "Report_Layout_Definition_API.Get_Layout_Type","f0");
      cmd.addReference("f1", "REPORT_IDENTITY/DATA");
      cmd.addParameter("f2", layout_name);

      cmd = trans.addCustomFunction("LAYOUT_TYPE_DB", "Report_Layout_Type_API.Encode","f1");
      cmd.addReference("f0", "LAYOUT_TYPE/DATA");
      
      trans = mgr.perform(trans);
      String layout_type = trans.getValue("LAYOUT_TYPE_DB/DATA/f1");
      String report_id   = trans.getValue("REPORT_IDENTITY/DATA/f1");
      
      try
      {
         
         if ("EXCEL".equals(layout_type)) 
         {
            byte[] data = Util.fromBase64Text(getExcelContent(result_key,report_id ));
            String file_name = report_id + ".xls";
            getASPManager().writeContentToBrowser(data, "application/vnd.ms-excel", file_name);
         }
         else
         {
            byte[] data = Util.fromBase64Text(getPDFContents(result_key,layout_name,lang_code ));
            
            writeContentToBrowser(data, "application/pdf");
         }
         /*
         getASPManager().setAspResponsContentType("application/pdf");                  
         getASPManager().getAspResponse().setContentLength(data.length);
         getASPManager().writeResponse(data);
         getASPManager().endResponse();
          */
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   
   /**
    * Returns true excel file contents. 
    */
   public String getExcelContent(String result_key, String report_id)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      String where_condition = "";
      where_condition = "result_key=? and UPPER(REPORT_ID)=UPPER(?)";
      ASPQuery q = trans.addQuery("EXCEL_DATA","EXCEL_REPORT_ARCHIVE","REPORT_FILE", where_condition, "");
      q.addParameter("RESULT_KEY", "N", "IN", result_key);
      q.addParameter("REPORT_ID", "S", "IN", report_id);

      trans = getASPManager().perform(trans);

      return trans.getValue("EXCEL_DATA/DATA/REPORT_FILE");
   }

   
   /**
    * Returns true if pdf file contents is available. 
    */
   public boolean isPDFContentAvailable( String result_key, String layout_name, String lang_code )
   {
      String data = getPDFContents(result_key,layout_name,lang_code );
      return (data != null);
   }
   
   public String getPdfId( String result_key, String print_job_id )
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
        
      ASPQuery q = trans.addQuery("PDF_ID","select ID from PDF_ARCHIVE where RESULT_KEY = ? and PRINT_JOB_ID = ?");
      q.addParameter("RESULT_KEY", result_key);
      q.addParameter("PRINT_JOB_ID", print_job_id); 
         
      trans = getASPManager().perform(trans);
      
      return trans.getValue("PDF_ID/DATA/ID");
   }
   
   
   /**
    * Sends pdf file contents to the client browser. SQL injection fixed
    */   
   public void sendPDFContents( String where_condition, String where_parameters )
   {
      try
      {
         byte[] data = Util.fromBase64Text(getPDFContents(where_condition, where_parameters));
         
         writeContentToBrowser(data, "application/pdf");

      }
      catch ( Throwable any )
      {
         error(any);
      }
   }

   /**
    * Returns the pdf file contents. SQl injection fixed
    */   
   public String getPDFContents( String where_condition, String where_parameters )
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();

      ASPQuery q = trans.addQuery("PDF_DATA","PDF_ARCHIVE","PDF",where_condition, "");
      StringTokenizer st = new StringTokenizer(where_parameters, "^");
      while(st.hasMoreTokens()){
         q.addParameter(st.nextToken(), st.nextToken(), st.nextToken(),st.nextToken());
      }
      trans = getASPManager().perform(trans);

      return trans.getValue("PDF_DATA/DATA/PDF");
   }

   /**
    * Sends pdf file contents to the client browser.
    */   
   public void sendPDFContents( String where_condition )
   {
      try
      {
         byte[] data = Util.fromBase64Text(getPDFContents(where_condition));
         
         writeContentToBrowser(data, "application/pdf");
         /*
         getASPManager().setAspResponsContentType("application/pdf");                  
         getASPManager().getAspResponse().setContentLength(data.length);
         getASPManager().writeResponse(data);
         getASPManager().endResponse();
          */
      }
      catch ( Throwable any )
      {
         error(any);
      }
   }
   
   /**
    * Returns the pdf file contents. 
    */   
   public String getPDFContents( String where_condition )
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      //String where_condition = "ID='"+id+"'";
      ASPQuery q = trans.addQuery("PDF_DATA","PDF_ARCHIVE","PDF",where_condition, "");

      trans = getASPManager().perform(trans);

      return trans.getValue("PDF_DATA/DATA/PDF");
   }
   //Bug 46730, end
   
   
   /**
    * This is an obsolete method. 
    */
   public String getPDFContents( String result_key, String layout_name, String lang_code )
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      String where_condition = "";
      where_condition = "result_key=? and UPPER(layout_name)=UPPER(?) and UPPER(lang_code)=UPPER(?)"+
                        " and OBJVERSION in (select max(OBJVERSION) from PDF_ARCHIVE)";
      ASPQuery q = trans.addQuery("PDF_DATA","PDF_ARCHIVE","PDF",where_condition, "");
      q.addParameter("RESULT_KEY", "S", "IN", result_key);
      q.addParameter("LAYOUT_NAME", "S", "IN", layout_name);
      q.addParameter("LANG_CODE", "S", "IN", lang_code);

      trans = getASPManager().perform(trans);

      return trans.getValue("PDF_DATA/DATA/PDF");
   }
   
   /**
    * Returns the title of the layout. 
    */   
   public String getLayoutTitle(String report_id, String layout_name)
   {
      ASPTransactionBuffer trans = getASPManager().newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand("LAYOUT_TITLE", "report_layout_definition_api.enumerate_layout");
      cmd.addParameter("f0");
      cmd.addParameter("f1", report_id);
      
      trans = getASPManager().perform(trans);

      try
      {
         String layout_name_ = new String();

         StringTokenizer st = new StringTokenizer(trans.getValue("LAYOUT_TITLE/DATA/f0"),""+IfsNames.recordSeparator);

         while( st.hasMoreTokens() )
         {
            String field = st.nextToken();
            int pos = field.indexOf(IfsNames.fieldSeparator);
            layout_name_ = field.substring(0,pos);
            if (layout_name_.equalsIgnoreCase(layout_name))
            {
               field = field.substring(pos+1);
               String layout_title = field.substring(0,field.indexOf(IfsNames.fieldSeparator));
               return layout_title;
            }
         }
      }
      catch (Exception e)
      {
      }

      return "";
   }
}