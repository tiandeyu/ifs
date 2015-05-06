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
 *  ----------------------------------------------------------------------------
 *  File                        : DocFileFetcher.java
 *  Description                 : Used by NonIEUploadDownloadDelete. Reads file data
 *                                from the temp file on disk and writes this into
 *                                the reponse object. This gets rid of both the problem
 *                                with the random file names reaching the user and also
 *                                the problem of deleting the file after it is downloaded.
 *  Notes                       :
 *  Other Programs Called       : Uses ASPManager, mostly.
 *  ----------------------------------------------------------------------------
 *  Modified    :
 *
 *  2005-10-17  MDAHSE  Created. Call ID 126700.
 *  2006-02-02  MDAHSE  Removed quotes around user_file_name. They will be added by setResponseContentFileName().
 *  2006-07-28  NIJALK  Bug 59564, Modified run(). Modified Usage.
 *  2006-09-20  NIJALK  Bug 60698, Modified code added by 59564 in run(). Modified usage.
 *  2006-09-21  NIJALK  Bug 58221, Added function assertLegalFileName(). Modified run().
 *  2010-01-20  DULOLK  Bug 88552, Added dummy value for Pragma in response header.
 *  -----------------------------------------------------------------------------------------------------
 */


/*

  Usage:
  
   This page is fairly easy to use. Just send in the name of a temp
   file that exist in the directory specified by
   DOCMAW/DOCUMENT_TEMP_PATH in parameter FILE_NAME, and, optionally,
   also a new file name, that the user will see, in parameter
   NEW_FILE_NAME. If NEW_FILE_NAME is not sent in, the code will
   default to use FILE_NAME.

   This approach gets rid of several problems; we do not need to point
   the user to the physical file on disk and we can present a better
   file name to the user than what the file really have.

  Example:

   http://mdahse:8080/b2ejap/secured/docmaw/DocFileFetcher.page?FILE_NAME=sjvcb8do2gz0apt26he.DOC&NEW_FILE_NAME=Copy%20of%20julesaga.doc

  Note:

   In order to work, this page must get hold of the response object,
   both for writing the data but also to set the response
   headers. Getting the response object is done by letting this page
   use our own DocmawManager, so make sure your web.xml is correctly
   configured.

  Enjoy!

*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import javax.servlet.http.*;

import java.io.*;

public class DocFileFetcher extends ASPPageProvider
{

   // Static constants

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocFileFetcher");

   // Instances created on page creation (immutable attributes)

   private ASPContext           ctx;
   private ASPHTMLFormatter     fmt;

   // Transient temporary variables (never cloned)

   private String real_file_name;
   private String user_file_name;

   private HttpServletResponse my_response;

   // Construction

   public DocFileFetcher(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      //Bug 60698, Start
      ctx            = mgr.getASPContext();
      //Bug 60698, End

      if (!mgr.isEmpty(mgr.getQueryStringValue("FILE_NAME")))
      {
         //Bug 58221, Start, Added check on assertLegalFileName 
         real_file_name = assertLegalFileName(mgr.getQueryStringValue("FILE_NAME"));
         user_file_name = assertLegalFileName(mgr.getQueryStringValue("NEW_FILE_NAME"));
         //Bug 58221, End

         debug("real_file_name: " + real_file_name);
         debug("user_file_name: " + user_file_name);

         if (mgr.isEmpty(user_file_name))
         {
            debug("user_file_name is empty, defaulting to real_file_name");
            user_file_name = real_file_name;
         }

         //Bug 60698, Start
         String temp_path = ctx.findGlobal("TMPPATHGLOBAL","");

         // Make sure we have the correct separator
         if (!mgr.isEmpty(temp_path))
             temp_path = temp_path.charAt(temp_path.length() - 1) == File.separatorChar ? temp_path : temp_path + File.separator;

         String full_file_name = temp_path + real_file_name;

         debug("full_file_name = " + full_file_name);
         //Bug 60698, End

         if (mgr instanceof DocmawManager)
         {

            try
            {
               //Bug 60698, Start, replaced real_file_name by full_file_name
               //Bug 59564, Start, replaced full_file_name by real_file_name
               // Open file stream
               FileInputStream in = new FileInputStream(full_file_name);
               //Bug 59564, End
               //Bug 60698, End

               // If the above worked, it is safe to continue

               my_response = ((DocmawManager)mgr).getPublicAspResponse();

               OutputStream out = my_response.getOutputStream();

               // Overwriting header that in some case makes Internet
               // Explorer bail out and not save the file.

               // Bug Id 88552, Start
               if (my_response.containsHeader("Cache-Control") || my_response.containsHeader("Pragma"))
               {
                  debug("Overwriting the Cache-Control and Pragma header...");
                  my_response.setHeader("Cache-Control", "dummy");
                  my_response.setHeader("Pragma", "dummy");
               }
               // Bug Id 88552, End

               debug("Setting Content-Type...");
               my_response.setContentType("application/octet-stream");

               mgr.setResponseContentFileName(user_file_name, true);

               int buf_size = 65536;
               byte[] buf = new byte[buf_size];
               int count = 0;

               // Read until we get no more data
               while ((count = in.read(buf, 0, buf_size)) != -1)
               {
                  debug("read " + Integer.toString(count) + " bytes");
                  out.write(buf, 0, count);
               }
               in.close();
               out.close();

               try
               {

                  // Now it should be safe to delete the file

                  // Note about deleting: if the user never press the
                  // link that links to this page, the file will not
                  // deleted by this file, but it will be deleted by
                  // the deleteOldFiles() methods in DocSrv. It is
                  // nicer to at least *try* and clean up here though.

                  debug("Deleting temp file...");

                  //Bug 60698, Start, replaced real_file_name by full_file_name
                  //Bug 59564, Start, replaced full_file_name by real_file_name
                  File file = new File(full_file_name);
                  //Bug 59564, End
                  //Bug 60698, End
                  file.delete();

                  debug("Delete OK.");
               }

               // Probably quite unlikely but...
               
               catch (SecurityException se)
               {
                  if (DEBUG) se.printStackTrace();
                  throw new FndException(mgr.translate("DOCMAWDOCFILEFETCHERDELETEFILEFAILED: An error occured while deleting file") + ":\r\n\r\n" + se.toString());
               }

            }
            catch (IOException ioe)
            {
               if (DEBUG) ioe.printStackTrace();
               throw new FndException(mgr.translate("DOCMAWDOCFILEFETCHERREADFILEFAILED: An error occured while reading file") + ":\r\n\r\n" + ioe.toString());
            }

         }
         else
         {
            debug("mgr is not an instance of DocmawManager");
            mgr.responseWrite("<html><body>Error: Probably there is a wrong manager mask in web.xml.</body></html>");
            mgr.endResponse();
         }
      }
      else
      {
         debug("FILE parameter not sent in or empty.");
         mgr.responseWrite("<html><body>Please specify the FILE parameter!</body></html>");
         mgr.endResponse();
      }

   }

   public void debug(String str)
   {
      // System.out.println(str);
      if (DEBUG) super.debug(str);
   }

   //Bug 58221, Start
   private String assertLegalFileName(String fileName) throws FndException
   {
       // Make sure file name is valid
       if (! fileName.matches("^[^\\\\/:*?<>|]+$") || (-1 != fileName.indexOf(0)))
      {
         throw new FndException("DANGEROUSFILENAME: Security exception, execution aborted due to illegal filename." + " [" + fileName + "]");
      }
      
      return fileName;
   }
   //Bug 58221, End
}
