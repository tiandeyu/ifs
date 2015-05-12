/*
*                 IFS Research & Development
*
*  This program is protected by copyright law and by international
*  conventions. All licensing, renting, lending or copyin g (including
*  for private use), and all other use of the program, which is not
*  expressively permitted by IFS Research & Development (IFS), is a
*  violation of the rights of IFS. Such violations will be reported to the
*  appropriate authorities.
*
*  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
*  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
* ----------------------------------------------------------------------------
*  File         : FileUploadPage.java
*  Modified     : 
*     Mangala       2007-01-30  - Bug 63250, Added theming support in IFS clients.
*     Chandana D:   05-02-2003 - Created.
*     Chandana D:   08-05-2003 - Fixed a bug in getContents() method.
*     Chandana D:   12-05-2004 - Updated for the use of new style sheets.
*     Chandana D:   19-05-2004 - Changed mgr.isNetscape6() to mgr.isMozilla().
* ----------------------------------------------------------------------------
*  New Comments   :
* 2009/09/29 sumelk Bug 85807, Changed getContents() to add the file type to the query string.
* 2008/12/23 sumelk Bug 79480, Changed getContents().
* 2007/02/23 rahelk Bug id 58590, placed form tag after startPresentation
* 2006/08/08 buhilk Bug 59442, Corrected Translatins in Javascript
*
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class FileUploadPage extends ASPPageProvider
{
   //===============================================================
   // Construction
   //===============================================================
   public FileUploadPage(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      FileUploadPage page = (FileUploadPage)(super.clone(obj));
      return page;

   }

   public void run()
   {
       disableNavigate();
   }

   protected void preDefine()
   {
       disableHeader();
       disableOptions();
       disableFooter();
   }
   
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "EVCATORDERTITLE2: Upload File";
   }

   protected String getTitle()
   {
       return "EVCATORDERTITLE: Upload File";
   }

   protected AutoString getContents() throws FndException
   { 
       ASPManager mgr = getASPManager();

       boolean upload_ok = mgr.isFileUploadSuccessful();
       boolean file_size_error = mgr.isFileUploadError();

       String upload_page_url =  mgr.readValue("UPLOAD_PAGE_URL"); 
       String upload_id = mgr.readValue("UPLOAD_ID"); 
       String block_name = mgr.readValue("BLOCK_NAME"); 
       String blob_id = mgr.readValue("BLOB_ID"); 
       String upload_mode = mgr.readValue("UPLOAD_MODE"); 
       String upload_destination = mgr.readValue("UPLOAD_DESTINATION"); 
       String post_function = mgr.readValue("POST_FUNCTION");
       String upload_type = mgr.readValue("UPLOAD_TYPE");
       if (mgr.isEmpty(upload_type)) upload_type = "UNKNOWN";
       
       String query_string = "UPLOAD_PAGE_URL="+mgr.URLEncode(upload_page_url)+
                             "&UPLOAD_ID="+mgr.URLEncode(upload_id)+
                             "&BLOCK_NAME="+mgr.URLEncode(block_name)+
                             "&BLOB_ID="+mgr.URLEncode(blob_id)+
                             "&UPLOAD_MODE="+mgr.URLEncode(upload_mode)+
                             "&UPLOAD_DESTINATION="+mgr.URLEncode(upload_destination)+
                             "&POST_FUNCTION="+mgr.URLEncode(post_function)+
                             "&UPLOAD_TYPE="+mgr.URLEncode(upload_type)+
                             "&UPLOAD_FROM=__WEB_CLIENT";
                             
       AutoString out = getOutputStream();
       out.clear();
       
       if (file_size_error)
       {
           String filename = mgr.getFileUploadAttribute("filename");
           String size = mgr.getASPConfig().getParameter("ADMIN/FILE_UPLOAD/MAX_SIZE_MB","5");

           out.append("<html>\n");
           out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
           out.append("<head>");
           out.append("<title>"+mgr.translate("IFSFNDFILEUPLOADERRORMSG: File Upload Error")+"</title>\n");
           out.append(mgr.getStyleSheetTag());
           out.append(mgr.getScriptFileTag());
           out.append("</head>\n");
           out.append("<body bgcolor=\"#FFFFFF\">");
           out.append("<form>");
           out.append("<br><br>\n");
           out.append("<table width=100% cellspacing=0 cellpadding=0 class=\"pageCommandBar\" height=22>");
           out.append("<tr>");
           out.append("<td width=100% height=22>");
           out.append(mgr.translate("FILEUPLOADERRORTITLE: File Upload Error"));
           out.append("</td>");
           out.append("</tr>");
           out.append("</table>");
           out.append("<table width=100% cellspacing=0 cellpadding=0 class=\"pageForm\">");
           out.append("<tr>");
           out.append("<td height=120 ALIGN=\"LEFT\" class=\"normalTextValue\">&nbsp;");
           out.append("\""+filename+"\" ");
           out.append(mgr.translate("FILEUPLOADERRORMSSAGE1: exceeds maximum uploadable file size [&1 &2]", size+"","MB"));
           out.append("</td>");
           out.append("</tr>");
           out.append("<tr>");
           out.append("<td align=right>");
           out.append("<input type=button name=Done value="+mgr.translate("FILEUPLOADERRORBTNTXTOK: OK")+" onClick=\"javascript:window.close()\"");
           out.append("class=\"button\">");
           out.append("</td>");
           out.append("</tr>");
           out.append("</table>");
           out.append("</form>");
           out.append("</body>");
           out.append("</html>");
       }
       else if (upload_ok)
       {
           blob_id = mgr.getFileUploadAttribute("blob_id"); 
           String filename = mgr.getFileUploadAttribute("filename");
           String filepath = mgr.getFileUploadAttribute("filepath");
           String filesize = mgr.getFileUploadAttribute("filesize");

           out.append("<html>\n");
           out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
           out.append("<head>");
           out.append(mgr.getStyleSheetTag());
           out.append("<title>"+mgr.translate("IFSFNDFILEUPLOADSTATUS: File Upload Status")+"</title>\n");
           out.append(mgr.getScriptFileTag());
           out.append("</head>\n");
           out.append("<body bgcolor=\"#FFFFFF\">");
           out.append("<br><br>\n");
           out.append( "<table cellpadding=0 cellspacing=0 width=100% class=\"pageCommandBar\" height=22>"+
                        "<tr><td height=22 width=100%>&nbsp;"+
                        mgr.translate("FNDFILEUPLOADSTATTITLE: File Upload Status")+
                        "</td></tr></table>\n");
           out.append("<table width=100% cellspacing=0 cellpadding=0 class=\"pageForm\">");
           out.append("<tr>");
           out.append("<td width=100% height=120 ALIGN=\"LEFT\" class=\"normalTextValue\">&nbsp;");
           out.append("\""+filename+"\" ");
           out.append(mgr.translate("FILEUPLOADTOMESSAGE: uploaded to &1 successfully",("0".equals(upload_mode))?mgr.translate("FILEUPLOADTODATABASE: database"):mgr.translate("FILEUPLOADTODISK: disk")));
           out.append("</td>");
           out.append("</tr>");
           out.append("<tr>");
           out.append("<td align=right>");
           out.append("<input type=button name=Done value="+mgr.translate("FILEUPLOADBTNTXTDONE: Done")+" onClick=\"javascript:window.close()\"");
           out.append("class=\"button\">");
           out.append("</td>");
           out.append("</tr>");
           out.append("</table>");
           out.append("</body>");

           out.append("<script language=javascript>");
           if ("0".equals(upload_mode) && !mgr.isEmpty(blob_id))
               out.append("opener."+readValue("BLOCK_NAME")+"_saveUploadId('"+blob_id+"');");
           else if ("1".equals(upload_mode))
               out.append("opener."+readValue("BLOCK_NAME")+"_saveUploadId('"+filename+"');");
           out.append((!"NULL".equalsIgnoreCase(post_function)?
                        "opener."+post_function+"(\""+blob_id+"\",\""+filename+"\",\""+mgr.encodeStringForJavascript(filepath)+"\",\""+filesize+"\");":""));
           out.append("window.focus();");
           out.append("</script>");
           out.append("</html>");
       }
       else
       {    
           out.append("<html>\n");
           out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
           out.append("<head>");
           out.append(mgr.getStyleSheetTag());
           out.append(mgr.generateHeadTag("IFSFNDFILEUPLOADPAGE: File Upload"));
           out.append("<title>"+mgr.translate("IFSFNDFILEUPLOADPAGETITLE: File Upload")+"</title>\n");
           out.append("</head>\n");
           out.append("<body topmargin=0 leftmargin=0 marginheight=0 marginwidth=0 bgcolor=\"#FFFFFF\" OnLoad=\"javascript:onLoad()\" OnFocus=\"javascript:OnFocus()\" OnUnLoad=\"javascript:OnUnLoad()\" onKeyPress=keyPressed()>");
           out.append("<form enctype=\"multipart/form-data\" name=\"form\" method=\"POST\" action=\""+getFormTagAction()+"?"+query_string+"\">");
           out.append( mgr.startPresentation("IFSFNDFILEUPLOADPAGEDESCRIPTION: File Upload"));
           beginDataPresentation();
           out.append( "<table cellpadding=0 cellspacing=0 width=100% class=\"pageCommandBar\" height=22>");
           out.append( "<tr><td height=22 width=100%>");
           out.append( "&nbsp;&nbsp;");
           out.append( mgr.translate("IFSFNDFILEUPLOADPAGEISTRUCTION: File Upload Dialog"));
           out.append( "</td></tr></table>\n");
           out.append("<table cellpadding=0 cellspacing=0 border=0 class=pageFormWithBorder width=100%>\n");
           out.append("<tr><td>&nbsp;\n");
           out.append("</td></tr>\n");       
           out.append("<tr><td height=70>\n");
           out.append("<input size=50 TYPE =\"file\"  id=\"__upload_file\"name=\"__upload_file\"");
           out.append("class=\"editableTextField\">");
           out.append("</td></tr>\n");              
           out.append("<tr><td>&nbsp;</td></tr>\n");
           out.append("<tr><td align=right>\n");
           out.append("<input TYPE ='button'  name=\"manual\" value=\""+mgr.translate("FILEUPLOADESUBMITBOTTON: Upload")+"\" onClick=\"javascript:manualUpload()\"");
           out.append("class=\"button\">");
           out.append("</td></tr>\n");
           out.append("</table>\n");
           endDataPresentation(false);
           out.append(mgr.endPresentation());

           out.append("<script language=javascript>\n");
           out.append("function manualUpload(){\n");
           out.append("if(document.form.__upload_file.value=='')\n");
           out.append("alert('"+mgr.translateJavaScript("SELECTAFILETOUPLOAD: Please select a file to upload")+"');\n");
           out.append("else{\n");
           out.append("   window.status='"+mgr.translateJavaScript("FILEUPLOADWAITMESSAGE: Please wait for authorization")+"';\n");
           out.append("   r = __connect('"+mgr.getQueryStringValue("UPLOAD_PAGE_URL")+"?VALIDATE=FILE_UPLOAD&__FILE='+URLClientEncode(document.form.__upload_file.value)+'&__COMMAND="+block_name+".isAuthorizedToUpload()');\n");
           out.append("   if(r==' TRUE')\n");
           out.append("      document.forms[0].submit();\n");
           out.append("   else\n");
           out.append("      alert('"+mgr.translateJavaScript("NOTAUTHORIZEDTOUPLOAD: Not authorized upload this file!")+"');\n");
           out.append("}\n");
           out.append("}\n");
           out.append("</script>\n");
           out.append("</form>\n");
           out.append("</body>\n");
           out.append("</html>");
       }                    
       return out;
   }



}
