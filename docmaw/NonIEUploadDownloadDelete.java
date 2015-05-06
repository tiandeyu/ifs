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
 *  File        : NonIEUploadDownloadDelete.java
 *  Description                 : Generates HTML GUI for checkin/out of document files
 *  Notes                       :
 *  Other Programs Called       : Inherits from DocSrv where most of the database and file handling is done
 *  ---------------------------------------------------------------------------
 *  Modified    :
 *    2001-06-18  MDAHSE  First version
 *    2001-06-20  THABLK  Build on first version
 *    2001-06-29  THABLK  Added refreshParent function call.
 *    2001-06-29  THABLK  Added Undo Checkout functionality.
 *    2003-01-07  DIKALK  Added doc_sheet to all relevant methods
 *    2003-03-24  DIKALK  Replaced method getUniqueRandomFileName with getRandomFilename
 *    2003-04-23  DIKALK  Removed import statement javax.servlet.http.HttpUtils
 *    2003-09-02  INOSLK  Call ID 101731: Modified doReset() and clone().
 *    2003-10-16  DIKALK  Tested page with doc_sheet and xedm functionality
 *    2004-11-08  DIKALK  Refactored some of the code.
 *    2005-02-02  SUKMLK  Fixed call 121703
 *    2005-02-10  SUKMLK  Added funtionality for first-time-checkout of documents.
 *                        For documents classes with/without templates. Call 121728.
 *    2005-02-28  SUKMLK  Made changes to the view document dialog. Fixed the problem with
 *                        document title not appearing in suggested filename in edit document.
 *                        Call 121703.
 *    2005-03-23  DIKALK  wrapped all calls to opener.refreshParent() in a try-catch clause
 *    2005-04-26  DIKALK  Modified title when selecting documents..
 *    2005-10-20  MDAHSE  Modified to support the new DocFileFetcher class. Call 126700.
 *                        Also corrected alll translation constants to follow the rules.
 *    2005-10-25  SUKMLK  Fixed call 125802.
 *    2005-11-02  SUKMLK  Made some visible invisible fields invisible. :) Minor fix related to 125802.
 *    2006-01-20  RUCSLK  Call 126699. Formated the page completely to have same look and feel for Non IE
 *    2006-01-25  MDAHSE  Added throws FndException to preDefine().
 *    2006-07-28  NIJALK  Bug 59564, Modified generateOpenDocAndClose().
 *    2006-09-20  NIJALK  Bug 60698, Modified the code added by bug 59564. Modified generateOpenDocAndClose().
 *    2009-07-21  VIRALK  Bug 84598, Specified the charset (UTF-8) when extracting data from the request body.
 *    2009-07-21  SHTHLK  Bug 84911, Removed the Singoutlink,Application search and settings link from the page. 
 *    2010-07-09  DULOLK  Bug 90463, Added new method generateDocFileFetcherURL() and added direct link instead of js.
 *                                   Modified generateClickOnceMessage() to handle click once and closing page.
 *                                   Removed timeout as adding it to this window itself caused the save dialog also to be closed in firefox.
 *    2010-07-16  DULOLK  Bug 90463, Replace whole download message with another info message once link is clicked.
 *    2010-07-16  DULOLK  Bug 90463, Modified generateClickOnceMessage(). Showed the message only when viewing a document.
 * ----------------------------------------------------------------------------

 * Documentation

 ** General

 This file is for uploading and downloading files to and from the IFS Document Management system
 It is a complement to the EdmMacro/IfsCliMgrOCX combination which only support Internet Explorer 5
 and Windows clients. This file is for Netscape and other browsers on Windows and other operating systems.

 The functionality is limited compared to the other combination mentioned. As we cannot run any ActiveX
 objects the use has to manually choose which file he want to check in to the system and also to where he
 wants to save the file to disk when downloading. It is also impossible to run macros.

 Also, not all types of actions are supported. We will begin by supporting CHECKINNEW, CHECKIN, CHECKOUT,
 VIEW and DELETE but will maybe extend it to include View copies, redline file, multiple actions etc later on.

 The techonlogy used limits size of files you can upload. I have successfully uploaded files up to
 30MB in size, but larger files may not work. The maximum file size you can upload depends very much
 on the amount of memory available on the web-server.

 Download of files should impose no limits on file size as this is done thorugh a normal link in the browser.

 ** Upload

 To upload a file we use HTTP POST and set the content-type to "multipart/formdata". This is a standard
 to upload files to a webserver.

 The POST:ed HTTP request is then parsed (it's a big chunk of bytes really) and the actual file in the request
 is extracted and saved to disk.

 From there we use about the same functionality as in EdmMacro, i.e we use methods from the DocSrv class
 that this file inherits from.

 ** Download

 Download is pretty easy, we get the file from the file repository, save it on a temporary area on the webserver
 and display a link to this file to the user OR redirects him to the URL of the file directly.

 ** Conclusion

   Drawbacks with this solution:

     - The one major drawback is that it has not all the functionality compared to the
       solution using an ActiveX component and IE5.

   Benefits:

     - It is in many situations faster
     - It works on more browsers than the other one. Tested are Netscape 4.7 and Internet Explorer 5.
     - It requires no extra files on the client apart from a standard-compliant web-browser
     - It probably works on many operating systems

 That's about it!

 /Mathias Dahl

 */

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.*;
import ifs.fnd.xml.*;

import java.util.*;
import java.util.Hashtable;
import java.io.*;

import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.w3c.dom.*;
import org.apache.xml.serialize.*;
import javax.xml.parsers.*;

public class NonIEUploadDownloadDelete extends DocSrv
{
    //
    // Static constants
    //
    public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.NonIEUploadDownloadDelete");

    //
    // Instances created on page creation (immutable attributes)
    //
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock                    fileblk;
    private ASPCommandBar               filebar;
    private ASPRowSet                   fileset;
    private ASPBlockLayout              filelay;

    private ASPHTMLFormatter     fmt;
    //Bug 60698, Start
    private ASPContext ctx;
    //Bug 60698, End
    //
    // Transient temporary variables (never cloned)
    //
    String  docClass;
    String  docNo;
    String  docSheet;
    String  docRev;
    String  docType;
    String  docFileName;
    String  fileType;    
    String  processDb;
    String  docTitle;
    String  errMessage;
    String  wholeDocName;
    //Bug 90463, Start
    String  nonIelinkId = "NonIELinkID";
    String  nonIeClickToDownloadId = "nonIeClickToDownloadId";
    //Bug 90463, End

    //
    // Construction
    //
    public NonIEUploadDownloadDelete(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }


    public void run() throws FndException
    {
        ASPManager   mgr;

        docClass = "";
        docNo    = "";
        docSheet = "";
        docRev   = "";
        docType  = "";
        fileType = "";
        processDb= "";
        docTitle = "";

        mgr       = getASPManager();
        //Bug 60698, Start
        ctx       = mgr.getASPContext();
        //Bug 60698, End
        AutoString out = getOutputStream();

        if(DEBUG) debug(this + ": Type of request = " + mgr.getRequestMethod());
        try
        {
          if ("POST".equals(mgr.getRequestMethod()))
         {
            performPost();
         }
         else if ("GET".equals(mgr.getRequestMethod()))
         {
            if (mgr.dataTransfered())
            {
               getTransferedData();
            }

            getOutputStream().clear();

            performGet();
          }
        }
        catch(FndException fnd)
        {
           // clientAction = "ERROR";
           errMessage += fnd.getMessage();
           errMessage = Str.replace(errMessage, "\\n", "<p>");
           fnd.printStackTrace();
           // mgr.showAlert(errMessage);
           //mgr.responseWrite("<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n");
           out.append(startDocument());                 
           out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETESHOWERROR: Error"));
           out.append("  <br>\n");
           out.append(fmt.drawReadLabel(errMessage));
           out.append(addCloseButton());
           out.append(endDocument());
           out.append(generateParentRefresh());

        }
    }


    private void getTransferedData()throws FndException
    {
       ASPManager mgr = getASPManager();
       String releasedRev;
       String latestRev;

       ASPBuffer buf = mgr.getTransferedData();
       buf.traceBuffer("Hello! what's this??");

       docClass         = buf.getValue("DOC_CLASS");
       docNo            = buf.getValue("DOC_NO");
       docSheet         = buf.getValue("DOC_SHEET");
       docRev           = buf.getValue("DOC_REV");
       releasedRev      = buf.getValue("RELEASED");
       latestRev        = buf.getValue("LATEST_REV");
       docFileName      = buf.getValue("FILEFILENAME");
       docType          = buf.getValue("DOC_TYPE");       
       fileType         = buf.getValue("FILE_TYPE");
       processDb        = buf.getValue("PROCESS_DB");       
       //docTitle  = mgr.readValue("TITLE");
             
       if (mgr.isEmpty(docType))
       {
          docType = EdmMacro.DEFAULT_DOC_TYPE;
       }

       if (mgr.isEmpty(fileType))
       {
          fileType = getEdmFileType(docClass, docNo, docSheet, docRev, docType);
       }

       if (Str.isEmpty(docRev))
       {
          if ((releasedRev != null) || (latestRev != null))
          {
             if(releasedRev !=null)
                releasedRev = releasedRev.toUpperCase();
             if (latestRev !=null)
                latestRev = latestRev.toUpperCase();

             docRev = getRevision(docClass, docNo, docSheet, latestRev, releasedRev);
          }
       }


       // Only IE currently supports file operations involving multiple files
       // Check if the document consists of multiple files
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd = trans.addCustomFunction("ISDOCXEDM", "Doc_Issue_Api.is_Document_Xedm", "OUT_1");
       cmd.addParameter("IN_STR1", docClass);
       cmd.addParameter("IN_STR1", docNo);
       cmd.addParameter("IN_STR1", docSheet);
       cmd.addParameter("IN_STR1", docRev);
       trans = mgr.perform(trans);

       if ("ORIGINAL".equals(docType) && "TRUE".equals(trans.getValue("ISDOCXEDM/DATA/OUT_1")))
       {
          throw new FndException(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEXEDMNOTSUPPORTED: This document consists of multiple files. An operation involving multiple files is currently supported by Internet Explorer only."));
       }
    }

    private void performPost() throws FndException
    {
       String  contentType;
       String  edmInfo="", fileName="", fullLocalFileName, fileExtension="";
       String  boundary;
       String  docData = "";
       byte [] requestBytes;

       ASPManager  mgr= getASPManager();
       AutoString out = getOutputStream();

       contentType = mgr.getRequestContentType();

       if (contentType.toLowerCase().startsWith("multipart/form-data"))
       {
          try
          {
             if(DEBUG)
             {
                debug(this + "");
                debug(this + ": #################################");
                debug(this + ": YESSS! It's a MULTIPART FORMDATA!");
                debug(this + ": #################################");
                debug(this + "");
             }

             // Fetch the whole request bytes
             requestBytes = getRequestBytes(mgr);

             // Get the boundary
             boundary    = contentType.substring(contentType.indexOf("boundary=") + 9);
             boundary    = "--" + boundary;

             // Get attributes
             docData = extractMultiPartRequestFields(requestBytes, boundary);

             docClass  = getStringAttribute(docData, "DOCCLASS");
             docNo     = getStringAttribute(docData, "DOCNO");
             docSheet  = getStringAttribute(docData, "DOCSHEET");
             docRev    = getStringAttribute(docData, "DOCREV");
             docType   = getStringAttribute(docData, "DOCTYPE");
             fileType  = getStringAttribute(docData, "FILETYPE");
             processDb = getStringAttribute(docData, "PROCESSDB");
             docTitle  = getStringAttribute(docData, "TITLE");
             docFileName  = getStringAttribute(docData, "FILEFILENAME");
                          

             if ("CHECKIN".equals(processDb) && "ORIGINAL".equals(docType))
             {
                fileName = getRandomFilename();
                fullLocalFileName = getTmpPath() + fileName;

                // Parse request and write file to disk
                writeFileFromHttpPostRequest(requestBytes, boundary, fullLocalFileName);

                edmInfo = getEdmInformation(docClass, docNo, docSheet, docRev, docType);

                if (!"TRUE".equals(getStringAttribute(edmInfo,"ORIGINAL_REF_EXIST")))
                {
                   if(DEBUG) debug(this + ": NOT ORIGINAL_REF_EXIST = TRUE    {");
                   fileExtension  = getStringAttribute(docData, "EXTENSION");
                   if(DEBUG) debug(this + ": fileExtension ="+fileExtension);
                   checkInNewFileNonIe(docClass, docNo, docSheet, docRev, docType, fileExtension, fullLocalFileName, docFileName);
                   if(DEBUG) debug(this + ": NOT ORIGINAL_REF_EXIST = TRUE    }");
                }
                else
                {
                   if(DEBUG) debug(this + ": ORIGINAL_REF_EXIST = TRUE    {");
                   checkInNonIe(docClass, docNo, docSheet, docRev, docType, fullLocalFileName);
                   if(DEBUG) debug(this + ": ORIGINAL_REF_EXIST = TRUE    }");
                }

                // Simple (for now) result to the client
                getOutputStream().clear();
                // mgr.responseWrite("<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n");
                out.append(startDocument());
                out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKIN: Check In Document"));

                filelay.showBottomLine(false);
                filelay.setEditable();
                fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEDOCCHECKEDIN: Document Checked In"));
                out.append(filebar.showBar());
                fileset.clear();
                fileset.addRow(null);
                preparePageHeader();
                
                beginDataPresentation();
                out.append(drawDataPresentation());
                endDataPresentation(true);              
                out.append(addCloseButton());
                out.append(endDocument());
                out.append(generateParentRefresh());

            }
            else if ("EXECUTE_CREATENEW".equals(processDb) && "ORIGINAL".equals(docType))
            {
               executeCreateNew();
            }
            else
            {
                mgr.responseWrite(processDb);
                out.append(startDocument());
                out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETEOPERATIONNOTAVAILABLE: Operation Not Available"));
                out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEWRONGBROWSER: This operation is currently not available for &1 browsers",mgr.getBrowserName())));
                out.append(addCloseButton());
                out.append(endDocument());
            }
          }
          catch (IOException e)
          {
             throw new FndException(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEUNEXPECTEDIOEXC: Unexpected IOException :" + e.toString()));
          }
       }
       else
       {
          throw new FndException(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECONTENTNOTSUPPORTED: Not a supported content type in HTTP POST."));
       }
    }

    private void executeCreateNew() throws FndException
    {
       // This method handles a first time checkout scenario where the document does not have a file ref.
       ASPManager mgr = getASPManager();

       AutoString out = getOutputStream();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

       trans.clear();

       ASPCommand cmd = trans.addCustomFunction("GETTITLE", "DOC_TITLE_API.Get_Title", "OUT_1");
       cmd.addParameter("IN_STR1", docClass);
       cmd.addParameter("IN_STR1", docNo);

       mgr.perform(trans);

       docTitle = "";
       docTitle = trans.getValue("GETTITLE/DATA/OUT_1");

       String edmInfoCheckout = getEdmInformation(docClass, docNo, docSheet, docRev, docType);
       String tempFileName;

       if ("TRUE".equals(getStringAttribute(edmInfoCheckout, "FILE_TEMPLATE_EXIST")))
       {
          tempFileName = createNewDocumentNonIETmplt(docClass,
                                                     docNo,
                                                     docSheet,
                                                     docRev,
                                                     docType);
       }
       else
       {
          tempFileName = createNewDocumentNonIE(docClass,
                                                docNo,
                                                docSheet,
                                                docRev,
                                                docType,
                                                fileType);
       }

       String fileExt = tempFileName.substring(tempFileName.lastIndexOf('.') + 1);

       out.append(startDocument());
       
       out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUT: Check Out Document"));

       filelay.showBottomLine(false);
       filelay.setEditable();
       fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTTOYOU: The document revision is now reserved under your user name"));
       out.append(filebar.showBar());
       fileset.clear();
       fileset.addRow(null);
       preparePageHeader();
       beginDataPresentation();
       out.append(drawDataPresentation());

       out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
       out.append("<tr><td>&nbsp;");
       // Bug 90463, Start
       out.append("<span id=\"" + nonIeClickToDownloadId + "\">");
       out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTLINK: Click to download file")));
       out.append("</span>");
       // Bug 90463, End
       out.append("</td><td>&nbsp;&nbsp;");
       

       debug("tempFileName = " + tempFileName);
       debug("wholeDocName = " + wholeDocName);

       if ("TRUE".equals(getStringAttribute(edmInfoCheckout, "FILE_TEMPLATE_EXIST")))
       {
          // Bug 90463, Start
          out.append("<font class=normalTextLabel>&nbsp; <a id=\"" + nonIelinkId + "\" href=\"" + generateDocFileFetcherURL(tempFileName, wholeDocName + fileExt) + "\">" +
                            wholeDocName + fileExt + "</a></font>\n");
          out.append(generateClickOnceMessage(false));
          // Bug 90463, End
       }
       else
       {

          fileExt = getFileExtension(fileType);
          wholeDocName = generateLocalFileName(docClass, docNo, docSheet, docRev, docType);

          debug("wholeDocName, second try = " + wholeDocName);
          debug("fileType = " + fileType);

          // Bug 90463, Start
          out.append("<font class=normalTextLabel>&nbsp;<a id=\"" + nonIelinkId + "\" href=\"" + generateDocFileFetcherURL(tempFileName + "." + fileExt, wholeDocName) + "\">" +
                            wholeDocName + "</a><font>\n");
          out.append(generateClickOnceMessage(false));
          // Bug 90463, End
       }

       out.append("</td></tr>");
       out.append("<tr><td>&nbsp;</td></tr>");
       out.append("</table>");

       endDataPresentation(true);
       out.append(addCloseButton());
       out.append(endDocument());
       out.append(generateParentRefresh());
    }


    private void performGet() throws FndException
    {
       String file_name = "";
       String edmInfo = "";
       String edmRepInfo;

       ASPManager  mgr= getASPManager();
       AutoString out = getOutputStream();

       mgr.setAspResponsContentType("text/html");

       edmRepInfo = getEdmRepositoryInformation(docClass, docNo, docSheet, docRev, docType);

       // MDAHSE, 2001-06-26
       // The same problem here as we had a long time ago in EdmMacro. The attribute
       // below is empty, so we have to make an extra database call.
       // String wholeDocName = getStringAttribute(edmRepInfo,"LOCAL_FILE_NAME");

       // generateLocalFileName() exist in DocSrv
       wholeDocName = generateLocalFileName(docClass, docNo, docSheet, docRev, docType);

       // MDAHSE, 2001-06-26
       // This seems to mess up resizing and such things because the form
       // is doing all queries again and that is not good. For example, two
       // checkout or checkin in a row does not work so well.

       // mgr.responseWrite("<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n");

       if ("CHECKIN".equals(processDb) && "ORIGINAL".equals(docType))
       {
          edmInfo = getEdmInformation(docClass, docNo, docSheet, docRev, docType);

          out.append(startDocument());

          if (!"TRUE".equals(getStringAttribute(edmInfo,"ORIGINAL_REF_EXIST")))
          {
             errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINNEWORIGFAILED: Checkin New Original failed.\\n");
             out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINNEW: Check In New Document"));
             filelay.showBottomLine(false);
             filelay.setEditable();
             fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINTITLE: Please select a document.."));
             out.append("&nbsp;&nbsp;&nbsp;");

          }
          else
          {
             errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINORIGFAILED: Checkin Original failed.\\n");
             out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKIN: Check In Document"));
             filelay.showBottomLine(false);
             filelay.setEditable();
             fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINTITLE: Please select a document.."));
             out.append("&nbsp;&nbsp;&nbsp;");             
          }

          out.append("<form Enctype=\"multipart/form-data\" name=\"form\" method=\"POST\" ");
          out.append(" action=\""+ mgr.getURL() +"\">");

          out.append(filebar.showBar());
          fileset.clear();
          fileset.addRow(null);
          preparePageHeader();
          beginDataPresentation();
          out.append(drawDataPresentation());         
          endDataPresentation(true);
          out.append("<br>");          
        
          out.append("<input type=\"hidden\" name=\"DOCCLASS\" value=\"" + docClass + "\">");
          out.append("<input type=\"hidden\" name=\"DOCNO\" value=\"" + docNo + "\">");
          out.append("<input type=\"hidden\" name=\"DOCSHEET\" value=\"" + docSheet + "\">");
          out.append("<input type=\"hidden\" name=\"DOCREV\" value=\"" + docRev + "\">");
          out.append("<input type=\"hidden\" name=\"DOCTYPE\" value=\"" + docType + "\">");
          out.append("<input type=\"hidden\" name=\"FILETYPE\" value=\"" + fileType + "\">");
          out.append("<input type=\"hidden\" name=\"PROCESSDB\" value=\"" + processDb + "\">");
          out.append("<input type=\"hidden\" name=\"TITLE\" value=\"" + docTitle + "\">");


         out.append("<table cellpadding=10 cellspacing=0 border=0 width=100%>\n");
         out.append("   <tr>\n");
         out.append("     <td>\n");
         out.append("      <input class=\"button\" type=\"file\"  name=\"FILEFILENAME\" value=\"\">");
         out.append("       &nbsp;");

          if (!"TRUE".equals(getStringAttribute(edmInfo,"ORIGINAL_REF_EXIST")))
          {
             
             out.append(fmt.drawButton("SUBMITBUTTON",mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINNEWDOC: Ok"),"OnClick=submitNew();"));
             out.append("<input type=\"hidden\" name=\"EXTENSION\"   value=\"\">");
             out.append("      </td>\n");
             out.append("   </tr>\n");
             out.append("</table>\n");   

             out.append(" <script language=javascript>   \n");
             out.append("  function submitNew() \n");
             out.append("  {  \n");
             out.append("     fileName = document.form.FILEFILENAME.value; \n");
             out.append("     document.form.EXTENSION.value = fileName.substring(fileName.lastIndexOf(\".\")+1,fileName.length);  \n");
             out.append("     document.form.submit(); \n");
             out.append("  }  \n");
             out.append(" </script> \n");
          }
          else
          {
             
              out.append(fmt.drawSubmit("SUBMIT",mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKINNEWDOC: Ok"),""));
              out.append("      </td>\n");
              out.append("   </tr>\n");
              out.append("</table>\n");   

          }
          out.append("</form> \n");
          out.append(endDocument());
          out.append(generateParentRefresh());

       }
       else if ("CHECKOUT".equals(processDb) && "ORIGINAL".equals(docType))
       {

          // This method handles checkouts for a file that has already been check in (ie not first time checkout of a title with no file ref attached)

          errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTORIGFAILED: Checkout Original failed.\\n");

          file_name = checkOutDocumentNonIe(docClass, docNo, docSheet, docRev, "ORIGINAL", "");

          out.append(startDocument());

          out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUT: Check Out Document"));

          filelay.showBottomLine(false);
          filelay.setEditable();
          fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTTOYOU: The document revision is now reserved under your user name"));
          out.append(filebar.showBar());
          fileset.clear();
          fileset.addRow(null);
          preparePageHeader();
          beginDataPresentation();
          out.append(drawDataPresentation());

          // Bug 90463, Start
          out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
          out.append("<tr><td>&nbsp;");
          out.append("<span id=\"" + nonIeClickToDownloadId + "\">");
          out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTLINK: Click to download file")));
          out.append("</span>");
          out.append(" &nbsp;&nbsp;");
          out.append("<font class=normalTextLabel>&nbsp;<a id=\"" + nonIelinkId + "\" href=\"" + generateDocFileFetcherURL(file_name, wholeDocName) + "\">" +
                  wholeDocName + "</a></font>\n");
          out.append("</td></tr>");
          out.append("<tr><td>&nbsp;</td></tr>");
          out.append("</table>");
          out.append(generateClickOnceMessage(false));
          // Bug 90463, End

          endDataPresentation(true);
          out.append(addCloseButton());
          out.append(endDocument());
     
          // Refresh parent window as file status is now changed

          out.append(generateParentRefresh());

       }
       else if ("VIEW".equals(processDb) && "ORIGINAL".equals(docType))
       {

          errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEVIEWORIGFAILED: View Original failed.\\n");

          wholeDocName = getStringAttribute(edmRepInfo,"COPY_OF_FILE_NAME");

          file_name = getCopyNonIe(docClass, docNo, docSheet, docRev,"ORIGINAL");

          out.append(startDocument());

          out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETEVIEWDOC: View Document"));

          filelay.showBottomLine(false);
          filelay.setEditable();
          fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEVIEWDOC: View Document"));
          out.append(filebar.showBar());
          fileset.clear();
          fileset.addRow(null);
          preparePageHeader();
          beginDataPresentation();

          out.append(drawDataPresentation());


          // Bug 90463, Start
          out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
          out.append("<tr><td>&nbsp;");
          out.append("<span id=\"" + nonIeClickToDownloadId + "\">");
          out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECHECKOUTLINK: Click to download file")));
          out.append("</span>");
          out.append("&nbsp;&nbsp;");
          out.append("<font class=normalTextLabel>&nbsp;<a id=\"" + nonIelinkId + "\" href=\"" + generateDocFileFetcherURL(file_name, wholeDocName) + "\">" +
                  wholeDocName + "</a></font>\n");
          out.append("</td></tr>");
          out.append("<tr><td>&nbsp;</td></tr>");
          out.append("</table>");
          out.append(generateClickOnceMessage(true));
          // Bug 90463, End

          endDataPresentation(true);        
          out.append(addCloseButton());        
          out.append(endDocument());
     
          
       }
       else if ("DELETE".equals(processDb) && "ORIGINAL".equals(docType))
       {
          errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEDELETEERR: Delete Document failed.\\n");

          // With DELETE we mean delete the document file and reference
          // on the server. Maybe we could implement a POWER delete also,
          // let's decide that later.
          deleteDocumentFile(docClass, docNo, docSheet, docRev, docType);

          out.append(startDocument());

          out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETEDELDOCFILE: Delete Document File"));

          filelay.showBottomLine(false);
          filelay.setEditable();
          fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEDELSUCCESS: The document file was successfully deleted"));
          out.append(filebar.showBar());

          fileset.clear();
          fileset.addRow(null);          
          preparePageHeader();
          beginDataPresentation();
          out.append(drawDataPresentation());
          
          endDataPresentation(true);      
          out.append(addCloseButton());
          out.append(endDocument());   
          out.append(generateParentRefresh());

       }
       else if ("UNDOCHECKOUT".equals(processDb) && "ORIGINAL".equals(docType))
       {
          errMessage = mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEUNDOCHECKOUTERR: Undo checkout Document failed.\\n");
          undoCheckOut(docClass, docNo, docSheet, docRev, docType);
          
          out.append(startDocument());

          out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETEUNDOCHECKOUT: Undo Checkout"));

          filelay.showBottomLine(false);
          filelay.setEditable();
          fileblk.setTitle(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEUNDOCHECKOUTSUCCESS: Undo Checkout was successfully completed"));
          out.append(filebar.showBar());
          fileset.clear();
          fileset.addRow(null);       
          preparePageHeader();        
          beginDataPresentation();
          
          out.append(drawDataPresentation());
          
          endDataPresentation(true);     
          out.append(addCloseButton());
          out.append(endDocument());
          out.append(generateParentRefresh());

       }
       else if ("CREATENEW".equals(processDb) && "ORIGINAL".equals(docType))
       {

          ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
          ASPBuffer data_buf;
          fmt = mgr.newASPHTMLFormatter();

          String query  = "SELECT FILE_TYPE, DESCRIPTION FROM EDM_APPLICATION WHERE DOCUMENT_TYPE IN ('ORIGINAL','VIEW')";
          trans.clear();
          ASPCommand cmd = trans.addQuery("GETAPPLICS", query);
          trans = mgr.perform(trans);
          data_buf = trans.getBuffer("GETAPPLICS");

          String edmInfoCheckout = getEdmInformation(docClass, docNo, docSheet, docRev, docType);

          if ("TRUE".equals(getStringAttribute(edmInfoCheckout, "FILE_TEMPLATE_EXIST")))
          {
             executeCreateNew();
          }
          else // No document template exists.
          {
             
             out.append(startDocument());

             //Invisible form to hold all the data between calls        
             out.append("<form Enctype=\"multipart/form-data\" name=\"form\" method=\"POST\" ");

             out.append(" action=\""+ mgr.getURL() + "\" " + "onsubmit=\"window.open('','" + mgr.getURL() + "',resizable=1,scrollbars=1,width=400,height=300') \">\n");

             out.append("<p><input type=\"hidden\" name=\"DOCCLASS\" value=\"" + docClass + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"DOCNO\" value=\"" + docNo + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"DOCSHEET\" value=\"" + docSheet + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"DOCREV\" value=\"" + docRev + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"DOCTYPE\" value=\"" + docType + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"FILETYPE\" value=\"" + fileType + "\"></p>");
             out.append("<p><input type=\"hidden\" name=\"PROCESSDB\" value=\"" + "EXECUTE_CREATENEW" + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"TITLE\" value=\"" + docTitle + "\"></p>\n");
             out.append("<p><input type=\"hidden\" name=\"EXTENSION\"   value=\"\"></p>\n");

             out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETESELECTFILETYPEHEADER: Create New Document"));

             filelay.showBottomLine(false);
             filelay.setEditable();
             fileblk.setTitle(mgr.translate("DOCMAWEDMMACROCHOOSEANAPPLICATION: Choose an application"));
             out.append(filebar.showBar());

             fileset.clear();
             fileset.addRow(null);

             preparePageHeader();

             beginDataPresentation();

             out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
             out.append("<tr><td>");

             mgr.getASPField("APP_FILE_TYPE").unsetHidden();
             out.append(filelay.generateDataPresentation());
             out.append("<script language=\"javascript\">\n");
             for (int x = 0; x < data_buf.countItems(); x++)
             {
                 if ("DATA".equals(data_buf.getNameAt(x)))
                     out.append("document.form.APP_FILE_TYPE.options[" + x + "] = new Option(\"" + data_buf.getBufferAt(x).getValue("DESCRIPTION") + "\", \"" + data_buf.getBufferAt(x).getValue("FILE_TYPE") + "\");\n");
             }
             out.append("</script>\n");         

             out.append("</td></tr>");
             out.append("</table>");
             out.append("</form> \n");

            // draw dotted line after table
             endDataPresentation(true);

             out.append("<table cellpadding=10 cellspacing=0 border=0 width=100%>\n");
             out.append("   <tr>\n");
             out.append("      <td align='right'>\n");
             out.append(fmt.drawButton("SUBMITBUTTON",mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEEDITNEWDOC: Ok"),"OnClick=editNew();"));
             out.append("      </td>\n");
             out.append("      <td align='left'>\n");
             out.append(fmt.drawButton("CLOSE",mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECLOSE: Close"),"OnClick=javascript:window.close();"));           
             out.append("      </td>\n");
             out.append("   </tr>\n");
             out.append("</table>\n");         
             
             //Invisible form to hold all the data between calls
             out.append(endDocument());
             //mgr.responseWrite(generateParentRefresh());

             //JavaScript
             out.append(" <script language=javascript>   \n");
             out.append("  function editNew() \n");
             out.append("  {  \n");
             out.append("     document.form.FILETYPE.value = document.form.APP_FILE_TYPE.value; \n");
             out.append("     document.form.submit(); \n");
             out.append("  }  \n");
             out.append(" </script> \n");
             out.append(generateParentRefresh());
             //JavaScript
             
             //OUTPUT End
          }
       }
       else
       {
          
           out.append(startDocument());
           out.append(mgr.startPresentation("DOCMAWNONIEUPLOADDOWNLOADDELETEOPERATIONNOTAVAILABLE: Operation Not Available"));
           out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETEWRONGBROWSER: This operation is currently not available for &1 browsers",mgr.getBrowserName())));
           out.append(addCloseButton());
           out.append(endDocument());
       }
    }

    // Bug 90463, Start
    private String generateDocFileFetcherURL(String real_file_name, String user_file_name) throws FndException
    {
       ASPManager  mgr= getASPManager();
       //Bug 60698, Start, Removed the code added by 59564
       //Bug 59564, Start, URLEncoded the real_file_name
       String URL = "DocFileFetcher.page?FILE_NAME=" + real_file_name + "&NEW_FILE_NAME=" + user_file_name;
       //Bug 59564, End
       //Bug 60698, End
       //Bug 58221, Start
       String tempPath = getTmpPath();
       ctx.setGlobal("TMPPATHGLOBAL", tempPath);
       //Bug 58221, End
       return URL;
    }
    //Bug 60698, Start, set the FndException
    private String generateClickOnceMessage(boolean setMessage) throws FndException
    //Bug 60698, End
    {
       ASPManager  mgr= getASPManager();
       StringBuffer str = new StringBuffer();
      
       str.append("    <script>\n");
       str.append("      function assignOnClickHandler()\n");
       str.append("      {\n");
       str.append("         var nonIeLink = document.getElementById(\"" + nonIelinkId + "\");\n");
       str.append("         var nonIeClickToDownloadId = document.getElementById(\"" + nonIeClickToDownloadId + "\");\n");
       str.append("         nonIeLink.onclick = function()\n");
       str.append("                             {\n");
       if (setMessage)
         str.append("                                nonIeClickToDownloadId.innerHTML=\"<font class=normalTextLabel>" + mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECLICKONCE: If you want to view the document again, close this page and retry the operation.") + "</font>\";\n");              
       else
         str.append("                                nonIeClickToDownloadId.innerHTML=\"\";\n");               
       str.append("                                nonIeLink.innerHTML=\"\";\n");
       str.append("                                nonIeLink.style.visibility=\"hidden\";\n");
       str.append("                             };\n");
       str.append("      }\n");
       str.append("      assignOnClickHandler();\n");
       str.append("    </script>\n");

       return str.toString();
   }
   // Bug 90463, End

   private String generateParentRefresh()
   {
      StringBuffer str = new StringBuffer();

      str.append("<script language=javascript>\n");
      str.append("try\n");
      str.append("{\n");
      str.append("  opener.refreshParent();\n");
      str.append("}\n");
      str.append("catch(e)\n");
      str.append("{\n");
      str.append("  // Nothing we can do\n");
      str.append("}       \n");
      str.append("</script>\n");

      return str.toString();
   }


    //=============================================================================
    //   CMDBAR CUSTOM FUNCTIONS
    //=============================================================================

    public void preDefine() throws FndException
    {

        ASPManager mgr = getASPManager();
        fmt = mgr.newASPHTMLFormatter();

        disableOptions();
        disableHomeIcon();
        disableNavigate();
	//Bug 84911, Start
	disableApplicationSearch(); 
	disableSettingsLink();
	disableSignoutLink();
	//Bug 84911, End

        fileblk = mgr.newASPBlock("FILE");

        fileblk.addField("DOC_CLASS").
            setReadOnly().
            setLabel("DOCMAWEDMMACRODOCCLASS: Doc Class");

        fileblk.addField("DOC_NO").
            setReadOnly().
            setLabel("DOCMAWEDMMACRODOCNO: Doc No");

        fileblk.addField("DOC_SHEET").
            setReadOnly().
            setLabel("DOCMAWEDMMACRODOCSHEET: Doc Sheet");

        fileblk.addField("DOC_REV").
            setReadOnly().
            setLabel("DOCMAWEDMMACRODOCREV: Doc Rev");

        fileblk.addField("FILE_TYPE").
            setReadOnly().
            setLabel("DOCMAWEDMMACROFILETYPE: File Type");

        fileblk.addField("DOC_TYPE").
            setReadOnly().
            setLabel("DOCMAWEDMMACRODOCTYPE: Doc Type");

        fileblk.addField("APP_FILE_TYPE").
            setSelectBox().
            setValidateFunction("javascript:").
            setHidden().
            setLabel("DOCMAWEDMMACROAPPLICATION: Application");
     
        fileset = fileblk.getASPRowSet();
       
        filebar = mgr.newASPCommandBar(fileblk);

        filelay = fileblk.getASPBlockLayout();
        filelay.setDataSpan("APP_FILE_TYPE", 6);
            
        filelay.setDefaultLayoutMode(filelay.CUSTOM_LAYOUT);
        super.preDefine();
    }

    //===============================================================
    //  HTML
    //===============================================================

    protected String getDescription()
    {
       return "DOCMAWNONIETITLE: NonIEUploadDownloadDelete";
    }


    protected String getTitle()
    {
       return "DOCMAWNONIETITLE: NonIEUploadDownloadDelete";
    }


    protected AutoString getContents() throws FndException
    {
       return getOutputStream();
    }


    //===============================================================
    //  PRIVATE FUNCTIONS
    //===============================================================

    //
    // Extract all fields and their valeus from a POSTed HTTP request
    //

    public String extractMultiPartRequestFields(byte[] requestBytes, String boundary) throws UnsupportedEncodingException
    {
       if (DEBUG) debug(this+": extractMultiPartRequestFields() {");

       StringBuffer fieldData = new StringBuffer();
       String name = "";
       String value = "";
       int boundaryStart=0, nameStart = 0, nameEnd = 0, valueStart = 0, valueEnd = 0;

       for (int i = 0; i < requestBytes.length; i++)
       {
          // Found out where the boundary is and from there a possible field and field value
          boundaryStart = byteArrayIndexOf(requestBytes, boundary, i);

          // There is always a boundary at the end of the request, but with an extra "--" string
          // If we found that we can break
          if (boundaryStart > -1 && "--".equals(new String(byteArraySubByteArray(requestBytes, boundaryStart + boundary.length(), 2))))
             break;

          // Ok, where does the name of the field start?
          nameStart = byteArrayIndexOf(requestBytes, "; name=\"", boundaryStart);

          // Good, we find a field name defintion
          if (nameStart > -1)
          {
             nameStart = nameStart + 8;
             nameEnd = byteArrayIndexOf(requestBytes, "\"", nameStart);
             //bug id 84598 specify charsetname(UTF-8)
             name = new String(byteArraySubByteArray(requestBytes, nameStart, nameEnd - nameStart),0,nameEnd - nameStart,"UTF-8");

             // This is the special case with the file name
             if ((char) requestBytes[nameEnd+1] == ';')
             {
                valueStart = byteArrayIndexOf(requestBytes, "; filename=\"", nameStart) + 12;
                valueEnd = byteArrayIndexOf(requestBytes, "\"", valueStart);
                //bug id 84598 specify charsetname(UTF-8)
                value = new String(byteArraySubByteArray(requestBytes, valueStart, valueEnd - valueStart),0,valueEnd - valueStart,"UTF-8");
             }
             // A normal field
             else if ((char) requestBytes[nameEnd+1] == '\r')
             {
                valueStart = byteArrayIndexOf(requestBytes, "\r\n\r\n", nameStart) + 4;
                valueEnd = byteArrayIndexOf(requestBytes, "\r\n", valueStart);
                //bug id 84598 specify charsetname(UTF-8)
                value = new String(byteArraySubByteArray(requestBytes, valueStart, valueEnd - valueStart),0,valueEnd - valueStart,"UTF-8");               
             }
             // Other case, should not happen
             else
             {
                value = "";
             }
             // Next time, search from where the last value ended
             i =  valueEnd;

             // Append to string
             fieldData.append(name);
             fieldData.append("=");
             fieldData.append(value);
             fieldData.append("^");

             if (DEBUG) debug(this+":   name = " + name + ", value = " + value);
          }
       }

       if (DEBUG) debug(this+": extractMultiPartRequestFields() }");

       return fieldData.toString();
    }

    //
    // Return index of a byte array in another
    //

    private int byteArrayIndexOf(byte [] sourceArray,
             byte [] searchArray,
             int startPos)
    {
       return internalbyteArrayIndexOf(sourceArray, searchArray, startPos);
    }


    private int byteArrayIndexOf(byte [] sourceArray,
             String searchString,
             int startPos)
    {
       return internalbyteArrayIndexOf(sourceArray, searchString.getBytes(), startPos);
    }

    //
    // Internal method, called from the two other wrapper methods
    //

    private int internalbyteArrayIndexOf(byte [] sourceArray,
                byte [] searchArray,
                int startPos)
    {
       int j = 0;

       int sourceLen = sourceArray.length;
       int searchLen = searchArray.length;

       // If the search data is larger then the source
       if (searchLen > sourceLen)
       {
          return -1;
       }

       // Loop through source array
       for (int i = startPos ; i < sourceLen ; i++)
       {
          // Does the current search cahar in the search array
          //
          if (sourceArray[i] == searchArray[j])
          {
            // If we are on the same pos in the search array
            // as the array is long, we have found what we
            // were searching for
             if (j == (searchLen - 1))
             {
                int posFound = i - (searchLen - 1);
                return (posFound);
             }
             // Else, increase search string position
             j = j + 1;
          }
          else
          {
             // Didn't find it this time. Set search string pos to zero
             j = 0;
          }
       }
       // Well, nothing was found.
       return -1;
    }

    //
    // Substract a byte array from another byte array. A little like the String.substring() method
    //

    private byte[] byteArraySubByteArray(byte[] sourceArray,
                int start,
                int len)
    {
       byte[] tmp = new byte[len];
       for (int i = 0; i < len; i++)
       {
          tmp[i] = sourceArray[start + i];
       }
       return tmp;
    }

    //
    // writeFile()
    //
    // Parses stream from request object and writes file to disk
    // Does only handle ONE file now but could be extended to look
    // for more than one file in the request
    //

    private void writeFileFromHttpPostRequest(byte[]  requestBytes,
                     String boundary,
                     String fileName) throws IOException
    {

       if(DEBUG) debug(this+": writeFile() {");

       int startPos = 0, endPos = 0;

       FileOutputStream foStream;

       if(DEBUG) debug("");

       // Find file part of the HTTP POST
       startPos = byteArrayIndexOf(requestBytes, "; filename=", 0);
       startPos = byteArrayIndexOf(requestBytes,    "\r\n\r\n", startPos) + 4;
       endPos   = byteArrayIndexOf(requestBytes,      boundary, startPos) - 2;

       if(DEBUG)
       {
          debug(this + ":   startPos = "   + startPos);
          debug(this + ":   endPos = "     + endPos);
       }

       // Write the file part
       foStream = new FileOutputStream(fileName);
       foStream.write(requestBytes, startPos, endPos - startPos);
       foStream.close();

       if(DEBUG) debug(this+": writeFile() }");
    }


    private byte[] getRequestBytes (ASPManager  mgr) throws IOException
    {
       if(DEBUG) debug(this+": getRequestBytes() {");

       int offset = 0, size;

       int currPercent = 0, lastPercent = 0;

       int contentLen   = 0;

       byte[] requestBytes;

       InputStream inputStream;

       contentLen  = mgr.getRequestLength();

       // Reserve a byte array to place all the file-bytes in
       requestBytes = new byte [contentLen];

       if(DEBUG) debug("contentLen = " + contentLen);

       // Get InpuStream to read bytes from
       inputStream = mgr.getRequestBodyAsInputStream() ;

       try
       {

          // Loop for getting the whole POSTed request into our byte array
          while (offset < contentLen)
          {
             size = inputStream.read (requestBytes, offset, contentLen - offset);
             if (size == -1)
                throw new IOException ("Unexpected error while reading POST request.");
             offset = offset + size;

             if(DEBUG)
             {
                currPercent = (100 * offset) / contentLen;
                if (currPercent != lastPercent &&  ((currPercent % 10) == 0))
                   debug(this+":   Read " + currPercent + " %");
                lastPercent = currPercent;
             }
          }
       }
       catch (IOException io)
       {
          inputStream.close();
          throw io;
       }

       if(DEBUG) debug("");

       if(DEBUG) debug(this+": getRequestBytes() }");

       return requestBytes;
    }

   public void debug(String str)
   {
      // System.out.println(str);
      if (DEBUG) super.debug(str);
   }


   private void preparePageHeader()
   {
       ASPManager mgr = getASPManager();
           
       fileset.setValue("DOC_CLASS", docClass);
       fileset.setValue("DOC_NO", docNo);
       fileset.setValue("DOC_SHEET", docSheet);
       fileset.setValue("DOC_REV", docRev);
       fileset.setValue("DOC_TYPE", docType);
       fileset.setValue("FILE_TYPE", fileType);
   
   }

   private String  addCloseButton(){

       StringBuffer str = new StringBuffer();
       ASPManager mgr = getASPManager();
       str.append("<table cellpadding=10 cellspacing=0 border=0 width=100%>\n");
       str.append("   <tr>\n");
       str.append("      <td align='center'>\n");
       str.append(fmt.drawButton("CLOSE",mgr.translate("DOCMAWNONIEUPLOADDOWNLOADDELETECLOSE: Close"),"OnClick=javascript:window.close();"));
       str.append("      </td>\n");
       str.append("   </tr>\n");
       str.append("</table>\n");  

       return str.toString();

   }


   private String startDocument() 
   {
       StringBuffer str = new StringBuffer();
       ASPManager mgr = getASPManager();
       str.append("<html>");
       str.append(mgr.generateHeadTag("DOCMAWSELECTFILETYPEHEADING: Edm Macro "));
       str.append("  <body>\n");
       return str.toString();
   }


   private String endDocument()
   {
       StringBuffer str = new StringBuffer();
       ASPManager mgr = getASPManager();
       str.append("  </body>\n");
       str.append("</html>\n");
       return str.toString();

   }

   private String drawDataPresentation()
   {      
       StringBuffer str = new StringBuffer();
       ASPManager mgr = getASPManager();
       str.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
       str.append("<tr><td>");
       str.append(filelay.generateDataPresentation());
       str.append("</td></tr>");
       str.append("</table>");
       return str.toString();

   }

}
