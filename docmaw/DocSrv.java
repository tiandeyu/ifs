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
 *  File                        : DocSrv.java
 *  Description                 : Server class for handling checkin/out of document files
 *  Notes                       : This is just an abstract class that EdmMaco.java inherits from
 *  Other Programs Called       : XML parsers, FND classes for Base64 encoding etc.
 *  ----------------------------------------------------------------------------
 *  Modified    :
 *      THABLK   2001-04-02   Created
 *      MDAHSE   2001-05-17   Lots of changes since Thushan's first draft
 *      BAKALK   2001-05-28   Changed translation tag of the title.
 *      BAKALK   2001-06-06   "Undo check out" is not allowed  when a document file has never been checked in.
 *      BAKALK   2001-06-08   Added a new parameter to "createNewDocumentFile".
 *      THABLK   2001-06-20   Added Non IE support.
 *      THABLK   2001-06-29   Added Original file type check in checkInNewFile.
 *      THABLK   2001-07-03   Added Original file types in createXmlFile.
 *      THABLK   2001-07-06   Added deleteOldFilesNonIe and deleteOldFiles.
 *      THABLK   2001-07-10   Call ID - 66883 Added deleteRedlineFile.
 *      PRSALK   2001-08-07   Call ID - 62828 Added Copy Original Document functionality.
 *      BAKALK   2001-09-19   Fixed localization strings
 *      MDAHSE   2001-09-19   Fixed serious bug in decryptFtpPassword()
 *      MDAHSE   2001-10-05   Fixed serious bugs in deleteDocumentFile() and checkIn().
 *                            Wrong parameters was beeing sent to database methods. For example, IN_3
 *                            was used for both in-parameter 3 and 4 in some calls.
 *      MDAHSE   2001-10-05   Fixed bug that caused some error messages not be correctly displyed in javascript
 *                            In the futiure, DO NOT USE ' or " inside error messages. It messes up the javascript.
 *      DIKALK   2001-10-10   getAppOwner() had an invalid sql statement (typing error)
 *      SHTOLK   2002-03-26   Bug Id#27766, Modified checkIn() to put View Copy in to the achieve when View Copy is
 *                            already existing  with the same extension.
 *      SHTHLK   2002-11-01   Bug Id 30724, Modified createXmlFile method to handle the process 'CHECKOUTALREADY'.
 *      PRSALK   2002-11-05   Added docSheet.
 *      PRSALK   2002-11-28   Changed submit(trans) to perform(trans)
 *      BAKALK   2002-12-27   Merged 2002-2 SP3 bug fixes.
 *      NISILK   2003-01-13   Call Id 92204 Undo Create Document File.
 *      DIKALK   2003-01-30   Changed references to column DOCUMENT_TYPE_DB to DOCUMENT_TYPE in CreateXMLFile
 *      MDAHSE   2003-02-11   Added new methods getRandomFileName() and convertRandomBytesToFileName() to
 *                            be used instead of getUniqueRandomFileName()
 *      DIKALK   2003-02-20   Started impelementing the new Extended EDM Functionality
 *      DIKALK   2003-02-20   Modified several methods: CreateXMLFile, CheckInNewDocument, CheckInDocument and CheckOutDocument
 *      DIKALK   2003-02-20   Added overloaded versions of methods GetFileFromSecondaryFtp and PutFileToSecondaryFtp
 *      DIKALK   2003-02-20   Made changes to several methods to handle the extened edm functionality
 *      DIKALK   2003-02-25   Removed method CheckInNewDocument.
 *      DIKALK   2003-02-25   Modified methods CreateNewDocument, GetCopy and DeleteDocumentFile to support XEdm.
 *      DIKALK   2003-03-13   Modified method copyFileInRepository to support copying of XEdm files
 *      DIKALK   2003-03-17   Replaced usage of getUniqueRandomFileName() with getRandomFilename().
 *                            (also removed the now obsolete method, getUniqueRandomFileName())
 *      BAKALK   2003-03-28   Added new class variable RedlineNeeded. You can set it true if u want to
 *                            download Redline file along with Original and Extended files from Base ftp
 *                            to Secondary ftp.
 *      DIKALK   2003-01-04   Added the clone() and doReset() methods to support Web Client 3.5.1
 *      BAKALK   2003-04-08   Modified 2003 copyFileInRepository method. Now we make copies of REDLINE and VIEW too.
 *                            (if they exist.)
 *      DIKALK   2003-04-22   Fixed a bug in modified method getAssociatedDocumentTypes()
 *      INOSLK   2003-04-26   Added overloaded method createXmlFile and changes to support Create New Document.
 *      NISILK   2003-05-02   Modified method createNewImportFile to support XEDM file import
 *      DIKALK   2003-05-07   Changed duplicate translation constants.
 *      BAKALK   2003-05-19   Modified the method checkInExistingFromBC which was added in last version.
 *      BAKALK   2003-05-27   Modified the method checkInExistingFromBC.
 *      NISILK   2003-06-05   Removed checks made for INCLUDE_VIEW_COPY and INCLUDE_RED_LINE.
 *      NISILK   2003-06-18   Made a checks for view and reference exists, in method checkOutDocumentForBC
 *                            to add the VIEW and REDLINE doc types to doc_type_list.
 *      BAKALK   2003-07-14   Modified checkOutDocumentForBC. Stopped updating file state when document does not
 *                            have any file reference.
 *      DIKALK   2003-08-04   Fixed a bug while checking in a new document in CreateXMLFile()
 *      INOSLK   2003-08-07   SP4 Merge: Bug ID 30724.
 *      DIKALK   2003-08-13   Modidied methods checkAccess() and getRandomFilename()
 *      NISILK   2003-08-14   call id 100766 Added method getRevision() in order to fix Bug Id 37037*
 *      NISILK   2003-08-18   Added doc_sheet as a parameter to method getRevisions() and modified method.
 *      DIKALK   2003-09-24   Removed methods copyOriginalDocWithConnectFiles and copyOriginalDocNotWithConnectFiles
 *      INOSLK   2003-09-25   Call ID 103727 - Modified method createNewImportFile().
 *      BAKALK   2003-10-02   Merged Bug Id 39449, Added new method unicode2Byte() and modified createXmlFile()
 *      NISILK   2003-10-14   Fixed Call Id 107457. Modified method checkAccess.
 *      DIKALK   2004-03-23   SP1 Merge. Bug Id 41806. Modified method checkAccess() to return correct message when checking out.
 *      DIKALK   2004-06-17   Overloaded GetCopy() and removed member variable redlineNeeded
 *      DIKALK   2004-06-23   Merged Bug Id 44187
 *      BAKALK   2004-06-25   Merged LCS patch <45197>.
 *      DIKALK   2004-10-01   Merged Bug Id 45861.
 *      DIKALK   2004-11-08   Reviewed and refactored the code.
 *      BAKALK   2005-01-18   Fixed the call 121008.
 *      BAKALK   2005-01-26   Fixed the call 121486.
 *      SUKMLK   2005-02-10   Added 2 new funtions createNewDocumentNonIE(), createNewDocumentNonIETmplt()
 *                            for first time checkout funtionality in non IE browsers. Call 121728
 *      SUKMLK   2005-03-23   Merged Bug 49558
 *      KARALK   2005-04-27   CALL ID 123341 CORRECTED IN checkInDocument().
 *      SUKMLK   2005-05-18   Fixed call 124110.
 *      SUKMLK   2004-05-27   Fixed call 124282, added getGeneratedLocalFileName().
 *      SUKMLK   2005-07-13   Fixed call 125662. Made some major changes to checkInDocument, added some functions.
 *      SUKMLK   2005-07-14   Fixed call 125663. Now docmaw stores a modified file name in USER_FILE_NAME
 *                            if the extension and filetype dont match.
 *      SUKMLK   2005-07-28   Fixed call 125963. View files now check in with original for struc/non struc.
 *      SHTHLK   2006-08-08   Fixed call 126193, Modified createXmlFile and createNewImportFile for File Import to work.
 *      AMNALK   2005-10-05   Merged Bug Id 53293.
 *      MDAHSE   2005-10-20   Call 126700. Changed code for non-IE. Mostly changed where the temp file is placed.
 *      SUKMLK   2005-10-25   Fixed call 125802.
 *      DIKALK   2005-12-08   Added method checkTitleExists() to check if a given document title exists
 *      BAKALK   2005-12-15   Added database Repository.
 *      MDAHSE   2005-12-30   Changed code in checkInDocument() so that the file references are created before the files are checked in.
 *                            This required a lot of work and hopefully I did not mess something up... I tested checking in VIEW files
 *                            as ORIGINALs, I tested checking in a HTM file as a TXT file and the other way around.
 *      DIKALK   2006-01-12   Fixed a bug in deleteOldFilesNonIe() where the temp path was not being created if it didn't exist
 *      BAKALK   2006-02-08   Fixed the call: 133186. when we check out ORIGINAL from database rep, ORIGINAL sometimes cannot be found in
 *                            Edm_File_Storage_Tab when single file is stored against both ORIGINAL and VIEW.
 *      BAKALK   2006-02-11   Fixed the call: 133095. Modified the way we change the status of files, specially when view and Original have
 *                            one file in the Repository.
 *      BAKALK   2006-02-26   Fixed calls: 134853 and 135305.
 *      BAKALK   2006-03-02   135305 was not fixed by above. so did it again.
 *      BAKALK   2006-03-08   Fixed calls:134877.
 *      BAKALK   2006-03-08   Fixed calls:134877.
 *      THWILK   2006-03-20   Bug 56016, Modified createXmlFile().
 *      DULOLK   2006-05-24   Bug 57006, Checked for errors in path SYSCFG_SHARED_PATH_FNDWEB in getTmpPath(). 
 *      BAKALK   2006-05-29   Bug 58326, Removing 2nd Ftp.
 *      DIKALK   2006-05-31   Bug 57779, Removed use of appowner and replaced with Docman Administrator
 *      KARALK   2006-06-02   Bug 57041, corrected in createxml method.
 *      BAKALK   2006-06-07   Bug 58326, Some more fixes.
 *      BAKALK   2006-06-08   Bug 58326, fixed the problem in accepting docs with database type from bc.
 *      DULOLK   2006-06-15   Bug 58771, modified checkInFromBC().
 *      SHTHLK   2006-07-14   Bug Id 58724, Modified createNewDocument() and createNewDocumentNonIE() to get the template file by sending template document's keys.
 *      NIJALK   2006-07-18   Bug Id 56631, Modified checkOutDocument().
 *      BAKALK   2006-07-25   Bug ID 58216, Fixed Sql Injection.
 *      NIJALK   2006-07-28   Bug 59564, Modified checkOutDocumentNonIe(), createNewDocumentNonIE(), createNewDocumentNonIETmplt(), getCopyNonIe().
 *      NIJALK   2006-08-10   Bug 59564, Modified putFileIntoArchiveNonIe().
 *      NIJALK   2006-08-21   Bug 58792, Modified createXmlFile().
 *      NIJALK   2006-09-06   Bug 59843, Modified deleteDocumentFile(), checkInFromBC(), deleteFileInDatabaseRepository().
 *      NIJALK   2006-09-20   Bug 60698, Modified the code added by bug 59564.
 *      DIKALK   2006-10-16   Bug 59605, Modified method CheckInDocument()
 *      NIJALK   2006-10-16   Bug 60903, Modified getTmpPath().
 *      NIJALK   2006-11-09   Bug 58421, Modified createXmlFile().
 *	KARALK   2006-12-15   DMPR303. MODIFIED METHOD createNewDocument()
 *      BAKALK   2007-03-01   Document Transmittals: Added functionality of "Add comment files from Transmittals"
 *      BAKALK   2007-03-27   Document Transmittals: Added functionality of "Connect report to Transmittal"
 *      BAKALK   2007-03-30   Document Transmittals: Added functionality of "Multiple comment files"
 *      BAKALK   2007-04-05   Document Transmittals: Now pdf are checked in as comment files from Transmittal
 *      BAKALK   2007-04-06   Document Transmittals: View comment files done
 *      BAKALK   2007-04-10   Document Transmittals: Send comment files done
 *      BAKALK   2007-05-09   Call Id: 143076, Made IN_NUM aspfield numeric.
 *      ASSALK   2007-08-15   Merged Bug 67114, Modified deleteFileInDatabaseRepository() and added new function checkExistDocType().
 *      ASSALK   2007-08-16   Merged Bug 65769, Modified copyFileInRepository(),putFileToFtpRepository() and putFileToDatabaseRepository().
 *      BAKALK   2007-08-24   Merged Bug 64138, Modified createXmlFile().   
 *      ASSALK   2007-08-27   Corrections to patch merge 67114, modified checkExistDocType().
 *      BAKALK   2007-15-29   Bug Id: 68527, Modified checkInCommentFile().
 *      BAKALK   2007-11-13   Bug Id: 68849, Added file no in file action on Database repository and in generating local  file name.
 *      AMNALK   2007-11-15   Bug Id: 66826, Modified createNewImportFile() to send the entire file name instead of the extension
 *      AMNALK   2007-11-30   Bug Id: 65997, Added new methods getRepositoryFileNamesFromServer(), CheckValidViewCopy() and overloaded getAssociatedDocumentTypes(),
 *			      getCopy(). Changed call of getAssociatedDocumentTypes() to overloaded method.
 *      SHTHLK   2007-12-08   Bug Id 67966, Modified checkOutDocumentForBC() to get state change of document types
 *      SHTHLK   2007-12-10   Bug Id 67966, Modified checkOutDocumentForBC() to get only the Existing document types
 *      AMNALK   2008-01-01   Bug Id 67442, Modified copyFileInRepository(), putFileToDatabaseRepository() and putFileToFtpRepository().
 *      AMNALK   2008-01-07   Bug Id 68528, Modified checkOutDocument(), getCopy() and createXmlFile().
 *      AMNALK   2008-01-17   Bug Id 70484, Modified checkInCommentFile() to remove the full path from the original_file_name.
 *      DULOLK   2008-03-17   Bug Id 69087, Added methods getFilesFromRepository(), getSingleFileFromFtpRepository(), getSingleFileFromDatabaseRepository(), deleteFilesInRepository(), 
 *                                          deleteSingleFileInFtpRepository(), deleteSingleFileInDatabaseRepository(), putFilesToRepository(), putSingleFileToFtpRepository(), putSingleFileToDatabaseRepository().
 *                                          Modified methods checkInDocument(), checkInFromBC(), checkOutDocument(), checkOutDocumentForBC(), deleteDocumentFile(), deleteDocument(), getCopy(), copyFileInRepository()
 *                                          and checkInCommentFile() to call above new methods to deal with documents in different repositories.
 *      BAKALK   2008-04-21   Bug Id: 71762, Added file no in getSingleFileFromDatabaseRepository().
 *      AMNALK   2008-07-07   Bug Id 72460, Added new methods undoStateChange(), removeLastHistoryLine(), removeLastSimillarHistoryLines() & removeFileReference().
 *      VIRALK   2008-07-22   Bug Id 74523, Modified CreateXmlFile to shorten the file name and added adjustFileName().
 *      NIJALK   2008-08-04   Bug Id 71814, Modified createXmlFile(), createNewImportFile().
 *      AMNALK   2008-08-08   Bug Id 74950, Modified createNewDocument() to get the repository file names of the correct document.
 *	     AMNALK   2008-08-13   Bug Id 74956, Modified getCopy() to get the correct doc_type_list for REDLINE process.
 * 	  AMNALK	  2008-08-19   Bug Id 67447, Modified createNewDocument() using file template.
 * 	  VIRALK	  2008-09-01   Bug Id 70512, Modified checkAccess to exclude Redline.
 *      AMCHLK   2009-03-26   Bug Id 79368, Modified createNewImportFile() to rollback the operations if the repository transfer fails.
 *      DULOLK   2009-04-02   Bug Id 80834, Added method compareFileSize() and used it to compare files after uploading and downloading the files
 *      SHTHLK   2009-10-01   Bug Id 86204, Modified compareFileSize() to add the file_no parameter
 *      ARWILK   2010-09-22   Bug ID 89622, Modified createXmlFile and checkInCommentFile to support all registered file types as transmittal comments.
 *                            Added two overloaded method deleteFileInDatabaseRepository, deleteFileInRepository. Added new method modifiyFileReferenceFileType. 
 * --------------------------------------------------------------------------------------------------------------------
 */

package ifs.docmaw;

import ifs.docmaw.edm.*;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.*;
import ifs.fnd.xml.XMLUtil;

import java.io.*;
import java.util.*;
import java.security.*;

import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.w3c.dom.*;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.XMLSerializer;

public abstract class DocSrv extends ASPPageProvider
{
   //
   // Static constants
   //

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocSrv");

   protected String docBriefcase;
   protected boolean viewCopyNeeded;
   protected boolean fileNotExist;
   protected String checkin_file_exts;
   protected String delete_file_exts;


   protected String local_file_for_check_in;
   private   String[] selected_doc_types;

   //
   // Instances created on page creation (immutable attributes)
   //

   private ASPBlock headblk;


   //
   // Transient temporary variables (never cloned)
   //

   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private String old_view_file_ext = "";
   private String old_redline_file_ext = "";
   private String view_file_ext;
   private boolean remove_old_view_file = false;
   private boolean remove_old_redline_file = false;

   // Protected variables for sub classes to pass info.
   protected boolean use_original_file_name = false;
   protected String redline_file_ext;
   protected String sameActionToAll;
   protected String launchFile;
   // Added by Terry 20140730
   // Check DOC_FOLDER parameter
   protected String doc_folder;
   // Added end
   protected String copyToDirPath;
   protected String currentDocBriefcase = "";
   protected String imported_path;
   protected String OriginalRefExist = "";
   protected String macro_attributes = "";
   //Bug Id 57041 start
   protected boolean structure_doc = false;
   protected String checkInPath = "";
   //Bug Id 57041 End
   protected boolean  notCheckCurrentState     = false;
   protected boolean  notCheckOriginalRefExist = false;
   //for multiple comment file
   protected String file_no = "";
   protected boolean modifyDocClassProcessConfig;
   protected boolean addComment;
   protected boolean viewComment;
   protected boolean sendComment;
   protected final String TRANSMITTAL_COMMENT_TYPE = "ACROBAT";

   //Bug id 74523, Start
   private int MAX_FILE_NAME_LENGTH = 250;
   //Bug id 74523, End

   //
   // Construction
   //
   public DocSrv(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public ASPTransactionBuffer perform(ASPTransactionBuffer trans) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer response = null;

      try
      {
         response = mgr.performEx(trans);
      }
      catch(ASPLog.ExtendedAbortException e)
      {
         Buffer info = e.getExtendedInfo();
         String error_message = info.getItem("ERROR_MESSAGE").toString();
         error_message = error_message.substring(error_message.indexOf("=") + 1);
         throw new FndException(error_message);
      }

      return response;
   }


   public String generateError() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("RAISEERROR", "Edm_File_Util_API.Get_File_Name_", "OUT_1");
      cmd.addParameter("IN_STR1", "");
      trans = perform(trans);

      return "Error Generated";
   }

   public void debug(String str)
   {
      if (DEBUG) 
      super.debug(str);
   }


   public void checkInNonIe(String doc_class,
                            String doc_no,
                            String doc_sheet,
                            String doc_rev,
                            String doc_type,
                            String tmp_file_location) throws FndException
   {
      if (DEBUG) Util.debug(this+": checkInNonIe() {");

      ASPManager mgr = getASPManager();

      String edm_info;
      String edm_rep_info = "";


      if ("ORIGINAL".equals(doc_type) || "REDLINE".equals(doc_type))
      {
         edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVONLYSUPPORIG: The checkIn method does not support doc types other than ORIGINAL or REDLINE in this version."));
      }

      if (Str.isEmpty(tmp_file_location))
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVTMPLOCEMPTY: temporary file location value is empty"));
      }


      // If we are checkin in a new view file and the extention is
      // not the same as before we have to do something about it...

      // get old view file and redline extention (if any)..
      old_view_file_ext = getStringAttribute(edm_info, "VIEW_FILE_EXTENTION").toUpperCase();
      old_redline_file_ext = getStringAttribute(edm_rep_info, "FILE_EXTENTION").toUpperCase();
      trans = mgr.newASPTransactionBuffer();


      // If there was one, is it different from the new one..
      if ("TRUE".equals(getStringAttribute(edm_info, "VIEW_REF_EXIST")) && !Str.isEmpty(view_file_ext) &&
          !Str.isEmpty(old_view_file_ext) && !view_file_ext.toUpperCase().equals(old_view_file_ext))
      {
         cmd = trans.addCustomCommand("DELFILEREFVIEW", "Edm_File_API.Delete_File_Reference");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "VIEW");

         remove_old_view_file = true;
      }

      

      

      // Now, if we had a old view copy we have removed it
      // so it should be safe to check in a new (if any)
      if (!Str.isEmpty(view_file_ext))
      {
         cmd = trans.addCustomFunction("GETVIEWFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_2");
         cmd.addParameter("IN_STR1", view_file_ext);

         cmd = trans.addCustomCommand("CRVIEWFILEREF", "Edm_File_API.Create_File_Reference");
         cmd.addParameter("IN_STR1", null);
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "VIEW");
         cmd.addReference("OUT_2",   "GETVIEWFILETYPE/DATA");
         cmd.addParameter("IN_STR1", null);
         cmd.addParameter("IN_NUM", "1");
      }

      // This is when we have had a redline file before and have changed
      // to one with a new file extention.

      if (!Str.isEmpty(redline_file_ext) && !Str.isEmpty(old_redline_file_ext) && !redline_file_ext.toUpperCase().equals(old_redline_file_ext))
      {
         // Remove the old Redline file ref...
         cmd = trans.addCustomCommand("DELFILEREFREDLINE", "Edm_File_API.Delete_File_Reference");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "REDLINE");

         remove_old_redline_file = true;

         // We can safely add a file reference now because the old one (if any)
         // has been deleted.

         // ...get the file type of the Redline file...
         cmd = trans.addCustomFunction("GETREDLINEFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_2");
         cmd.addParameter("IN_STR1", redline_file_ext);

         // ...and create a new one file reference.
         cmd = trans.addCustomCommand("CRREDLINEFILEREF", "Edm_File_API.Create_File_Reference");
         cmd.addParameter("IN_STR1", null);
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "REDLINE");
         cmd.addReference("OUT_2",   "GETREDLINEFILETYPE/DATA");
         cmd.addParameter("IN_STR1", null);
         cmd.addParameter("IN_NUM", "1");

         cmd = trans.addCustomCommand("SETSTATE1", "Edm_File_API.Set_File_State");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", doc_type);
         cmd.addParameter("IN_STR1", "StartCheckOut");
         cmd.addParameter("IN_STR1", null);
      }
      else
      {
         cmd = trans.addCustomCommand("SETSTATE1", "Edm_File_API.Set_File_State");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", doc_type);
         cmd.addParameter("IN_STR1", "StartCheckIn");
         cmd.addParameter("IN_STR1", null);
      }

      cmd = trans.addCustomCommand("SETSTATE2", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "FinishCheckIn");
      cmd.addParameter("IN_STR1", null);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      // We have already checked that tmpFileLocation is not empty so...
      putFileIntoArchiveNonIe(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev,doc_type), tmp_file_location);

   }

   //bug 58326, rename parameter secondary_ftp_file_id to param unique_file_id

   /**
    * checkInNewDocument checks in all new files to the repository.
    * It creates EDM references for each of the files and sets
    * the EDM status to 'Checked In' where appropriate.
    * (Exceptional cases include "VIEW" doc types where the edm
    * status is not manipulated).
    *
    * @param doc_class
    * @param doc_no
    * @param doc_sheet
    * @param doc_rev
    * @param doc_type
    * @param check_out_path
    * @param unique_file_id
    * @param local_file_name
    *                  Name of the master file selected by the user
    * @param extra_doc_type_exts
    *                  List of file extentions of additional doc types
    *                  to be checked in (including view copy extentions)
    *
    * @exception FndException
    */
   public String[] checkInDocument(String doc_class,
                                   String doc_no,
                                   String doc_sheet,
                                   String doc_rev,
                                   String file_type,
                                   String doc_type,
                                   String unique_file_id,
                                   String files_checked_in,
                                   String view_files_checked_in,
                                   String original_file_name) throws FndException
   {

      if (DEBUG) Util.debug(this+": checkInDocument() {");

      ASPManager mgr = getASPManager();

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      String original_file_ext = "";
      String doc_type_checked_in = "";
      boolean view_file_ref_found = false;
      boolean original_file_ref_found = false;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      StringTokenizer st = new StringTokenizer(files_checked_in, "|");
      int no_of_files = st.countTokens();
      String[] local_file_names = new String[no_of_files];
      String view_file_ext = !mgr.isEmpty(view_files_checked_in) ? DocmawUtil.getFileExtention(view_files_checked_in).toUpperCase() : "";

      String ori_file_name = "";

      String file_name = "";

      boolean bViewTypeOriRefExist = false;

      if (DEBUG) Util.debug(this + ": original_file_name = " + original_file_name);

      // Get a list of the remote file names...
      for (int x = 0; x < no_of_files; x++)
      {
         local_file_names[x] = st.nextToken();

         

         /*
          *This is a hack to make is possible in docmaw to checkin a file which
          *has an extension different to the file type. For example a test.txt
          *as a WORD document (.doc), which should get renamed to a test.doc at
          *checkin.
          */
         // See if this is the ORIGINAL file
         if (original_file_name.equalsIgnoreCase(local_file_names[x]))
         {
            /*
              Check if the file type has been passed. In the case of a normal checkin it should
              be passed, but in the case of a edit and checkin (or a checkout and checkin for that matter)
              the file type is not passed.
             */
            if (!mgr.isEmpty(file_type))
            {
               // Now we know this one is the original file ref, and the file type is passed.
               // Check to see if extension and file type passed matches
               if (!DocmawUtil.getFileExtention(local_file_names[x]).equalsIgnoreCase(getFileExtension(file_type)))
               {
                  /*
                    Now we know that the extension for the fileile_type and the extension of the original file selected
                    by the user does not match. This means that the user has selected a different type of filetype
                    in the open file dialog. (Like test.txt as filename and WORD as file_type. So we rename the
                    local file name to test.doc.
                   */
                  local_file_names[x] = DocmawUtil.getBaseFileName(local_file_names[x]) + "." + getFileExtension(file_type);
                  
               }
            }
            else
            {
               /*
                *Now no file type has been passed, so we dont rename the file.
                *This probably means that this is not a first time checkin, so
                *both the filetype and the filename is already in EDM_FILE so
                *we dont do anything... except of course extracting the file type
                *from the extension to avoid a nasty exception later on.
                */
               file_type = getFileType(DocmawUtil.getFileExtention(local_file_names[x]).toUpperCase());
               

            }
         }

         String file_ext = DocmawUtil.getFileExtention(local_file_names[x]);


         if ((file_ext.toUpperCase()).equals(view_file_ext))
         {
            original_file_ext = file_ext;
            view_file_ref_found = true;
         }


         cmd = trans.addCustomFunction("GETREMOTEFILENAME_" + x,  "Edm_File_Api.Generate_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", file_ext);
         if (!mgr.isEmpty(this.file_no)) {
            cmd.addParameter("IN_NUM", file_no);
         }

         cmd =  trans.addCustomFunction("GETDOCTYPE_" + x, "Edm_Application_Api.Get_Doc_Type_From_Ext", "OUT_1");
         cmd.addParameter("IN_1", file_ext.toUpperCase());

      }

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info2", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }

      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      String[] repository_file_names = new String[no_of_files];

      selected_doc_types = new String[no_of_files];

      for (int x = 0; x < no_of_files; x++)
      {
         repository_file_names[x] = trans.getValue("GETREMOTEFILENAME_" + x + "/DATA/OUT_1");
         if (DEBUG) Util.debug("repository_file_names[" + x + "] = " + repository_file_names[x]);
         this.selected_doc_types[x] = trans.getValue("GETDOCTYPE_" + x + "/DATA/OUT_1");
         
      }

      //
      // 2006-01-04, MDAHSE:
      //
      // Create file references and set the file status to Operaiton
      // In Progress:
      //
      // 1. Because this is the way it should be, so that we can have
      // the file status Operation In Progress while the file transfer
      // is ongoing.
      //
      // 2. Because if we try to save the file into the database,
      // EdmFileStorage will complain that the EdmFile does not
      // exist. This was the prime reason and why I noticed all this,
      // btw.
      //
      // After creating the new file ref, we need to "start a check
      // out", which in practice just means setting file state to
      // Operation In Progress.
      //
      // Also, I am sorry for placing yet another loop here, but I
      // could not see any other way to merge the code below into the
      // other loop above. Not in the short time we had, working
      // overtime in the project and all. If someone else want to do
      // it, please don't hestiate, if you see a good way to do it.
      //

      if (DEBUG) Util.debug(this + ": Creating file references...");

      trans.clear();

      for (int x = 0; x < no_of_files; x++)
      {
         file_name = DocmawUtil.getFileName(local_file_names[x]);


         if (DEBUG) Util.debug(this + ": File number: " + x + ", File name: " + file_name);
         

         // Correct the file type of the original file and do it
         // always, regardless if it already is OK or not.

         if (file_name.equalsIgnoreCase(DocmawUtil.getFileName((original_file_name))) )
          {
            file_name = getTypeCorrectedFileName(file_name, file_type);
            ori_file_name = file_name;

            if (DEBUG) Util.debug(this + ": 001, ORIGINAL: doc_type = " + this.selected_doc_types[x]);  
              
            cmd = trans.addCustomCommand("CHECKINNEW_" + x,  "Edm_File_Api.Check_In_File_");
            cmd.addParameter("OUT_1");
            cmd.addParameter("OUT_2");
            cmd.addParameter("OUT_3");  // Bug 59605, indicates if a new reference was created
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", file_name);
            cmd.addParameter("IN_STR1", file_type);
            if (notCheckCurrentState)
            {   //for 1.Addcomment files
               cmd.addParameter("IN_STR1", "FALSE");//
            }
            else
               cmd.addParameter("IN_STR1", "TRUE");
            if (!mgr.isEmpty(this.file_no)) {
               cmd.addParameter("IN_NUM", file_no);
            }

            // Set file status to Operation In Progress but only on
            // the ORIGINAL
            // we have not locked/unlocked view even in windows client.

           if ("ORIGINAL".equals(selected_doc_types[x]) ||!"VIEW".equals(selected_doc_types[x]))
           {
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", this.selected_doc_types[x]);
               if (!mgr.isEmpty(this.file_no)) {
                  cmd.addParameter("IN_NUM", file_no);
               }
               cmd.addParameter("IN_STR1", "StartCheckOut");
               cmd.addParameter("IN_STR1", null);
           }
           else if ("VIEW".equals(selected_doc_types[x]) && "TRUE".equals(OriginalRefExist))
           {
               bViewTypeOriRefExist = true;
               //just to handle the case when view type is selected for Original
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", "ORIGINAL");
               if (!mgr.isEmpty(this.file_no)) {
                  cmd.addParameter("IN_NUM", file_no);
               }
               cmd.addParameter("IN_STR1", "StartCheckIn");
               cmd.addParameter("IN_STR1", null);

               selected_doc_types[x] = "ORIGINAL";

           }
         }
         else
         {
            if (DEBUG) Util.debug(this + ": 002, OTHER: doc_type = " + this.selected_doc_types[x]);


            cmd = trans.addCustomFunction("GETFILETYPE_" + x, "Edm_Application_API.Get_File_Type", "OUT_1");
            cmd.addParameter("IN_STR1", DocmawUtil.getFileExtention(file_name));

            cmd = trans.addCustomCommand("CHECKINNEW_" + x,  "Edm_File_Api.Check_In_File_");
            cmd.addParameter("OUT_1");
            cmd.addParameter("OUT_2");
            cmd.addParameter("OUT_3");  // Bug 59605, indicates if a new reference was created
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", file_name);
            cmd.addReference("OUT_1", "GETFILETYPE_" + x + "/DATA");
            cmd.addParameter("IN_STR1", "TRUE");
            if (!mgr.isEmpty(this.file_no)) {
               cmd.addParameter("IN_NUM", file_no);
            }

            if (!"VIEW".equals(selected_doc_types[x]) )
            {   
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", selected_doc_types[x]);
               if (!mgr.isEmpty(this.file_no)) {
                  cmd.addParameter("IN_NUM", file_no);
               }
               cmd.addParameter("IN_STR1", "StartCheckOut");
               cmd.addParameter("IN_STR1", null);
            }

         }
         cmd = trans.addCustomFunction("REMOTEFILENAME_" + x,  "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addReference("OUT_1", "CHECKINNEW_" + x + "/DATA");
         if (!mgr.isEmpty(this.file_no)) {
            cmd.addParameter("IN_NUM", file_no);
         }
      }

      trans = perform(trans);

      // Put all files to the repository...
      //bug 58326, starts....
      String[] local_file_names2 = generateLocalFileNames(unique_file_id, repository_file_names);


      //
      // Bug 59605, Start
      // Check in the files to the repository. If an exception occurs,
      // role-back the state changes from Operation In Progress
      //
      try
      {
         putFilesToRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev), local_file_names2, repository_file_names);  //Bug Id 69087
      }
      catch (FndException fnd)
      {
         ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();

         for (int x = 0; x < no_of_files; x++)
         {
            boolean new_file = "TRUE".equals(trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_3"));

            if (new_file)
            {
               cmd = trans2.addCustomCommand("DELETEFILEREF_" + x, "Edm_File_API.Remove_Invalid_Reference_");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_1"));                  
            }
            else
            {
               cmd = trans2.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_1"));
               cmd.addParameter("IN_STR1", "FinishCheckOut");
               cmd.addParameter("IN_STR1", null);
            }
         }

         perform(trans2);

         // re-raise exception..
         throw fnd;
      }
      // Bug 59605, End

      //bug 58326, ends....

      if (DEBUG) Util.debug(this + ": Files checked in to repository sucessfully..");
      if (DEBUG) Util.debug(this + ": Changing edm state..");

      String[] old_file_names = new String[no_of_files];
      int count = 0;

      for (int x = 0; x < no_of_files; x++)
      {
         String old_file_ext = trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_2");

         // delete files that dont have a reference anymore
         if (!Str.isEmpty(old_file_ext))
         {
            String repository_file_name = trans.getValue("REMOTEFILENAME_" + x + "/DATA/OUT_1");
            old_file_names[count++] = repository_file_name.substring(0, repository_file_name.lastIndexOf(".") + 1) + old_file_ext;
            if (DEBUG) Util.debug("old_file_names[" + (count - 1) + "] = " + old_file_names[(count - 1)]);
         }

         doc_type_checked_in = this.selected_doc_types[x];

         if (DEBUG) Util.debug(this + ": doc_type_checked_in = " + doc_type_checked_in);

         if (!mgr.isEmpty(doc_type_checked_in) && doc_type_checked_in.equals("ORIGINAL"))
            original_file_ref_found = true;
      }

      //
      // Change the Edm State to checked in on all file references
      //

      trans.clear();
      // removed >>>>> Edm_File_API.Set_File_State_All being problematic

      for (int x = 0; x < no_of_files; x++)
      {
         cmd = trans.addCustomCommand("SETSTATECHECKIN"+x, "Edm_File_API.Set_File_State");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", selected_doc_types[x]);
         if (!mgr.isEmpty(this.file_no)) {
            cmd.addParameter("IN_NUM", file_no);
         }
         cmd.addParameter("IN_STR1", "FinishCheckIn");
         cmd.addParameter("IN_STR1", null);
      }



      // Here is where we fool the system into thinking we have two
      // files connected, when we actually only have one. Used when
      // checking in a file that normally would have been a VIEW file,
      // as ORIGINAL. This might seem ugly but actually works quite
      // well.


      if ((!original_file_ref_found) && (view_file_ref_found) && !bViewTypeOriRefExist)
      {
         cmd = trans.addCustomCommand("CREATEORIGINALREF","Edm_File_API.Check_In_View_Type_Orig_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", ori_file_name);
      }

      trans = perform(trans);


      if (count > 0)
      {
         String[] temp = old_file_names;
         old_file_names = new String[count];
         System.arraycopy(temp, 0, old_file_names, 0, count);
         deleteFilesInRepository(edm_rep_info, old_file_names); //Bug Id 69087
      }

      if (DEBUG) 
         Util.debug(this+": checkInDocument() }");

      return local_file_names;
   }

   
   // Added by Terry 20120917
   // Check in multi-documents
   public String[] checkInDocuments(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_type,
         String doc_type,
         String unique_file_id,
         String files_checked_in,
         String view_files_checked_in,
         String original_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this + ": checkInDocument() {");

      ASPManager mgr = getASPManager();

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      String original_file_ext = "";
      String doc_type_checked_in = "";
      boolean view_file_ref_found = false;
      boolean original_file_ref_found = false;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      StringTokenizer st = new StringTokenizer(files_checked_in, "|");
      int no_of_files = st.countTokens();
      String[] local_file_names = new String[no_of_files];
      String view_file_ext = !mgr.isEmpty(view_files_checked_in) ? DocmawUtil.getFileExtention(view_files_checked_in).toUpperCase() : "";

      String ori_file_name = "";

      String file_name = "";

      boolean bViewTypeOriRefExist = false;

      if (DEBUG) Util.debug(this + ": original_file_name = " + original_file_name);
      
      trans.clear();
      cmd = trans.addCustomFunction("GETNEXTFILENO",  "Edm_File_API.Get_Next_File_No", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      
      trans = perform(trans);

      String next_file_no = trans.getValue("GETNEXTFILENO/DATA/OUT_1");
      
      if (!mgr.isEmpty(next_file_no))
         file_no = next_file_no;
      
      trans.clear();

      // Get a list of the remote file names...
      for (int x = 0; x < no_of_files; x++)
      {
         local_file_names[x] = st.nextToken();

         /*
          * This is a hack to make is possible in docmaw to checkin a file which
          * has an extension different to the file type. For example a test.txt
          * as a WORD document (.doc), which should get renamed to a test.doc at
          * checkin.
          */
         // See if this is the ORIGINAL file
         if (original_file_name.equalsIgnoreCase(local_file_names[x]))
         {
            /*
             * Check if the file type has been passed. In the case of a normal
             * checkin it should be passed, but in the case of a edit and
             * checkin (or a checkout and checkin for that matter) the file type
             * is not passed.
             */
            if (!mgr.isEmpty(file_type))
            {
               // Now we know this one is the original file ref, and the file
               // type is passed.
               // Check to see if extension and file type passed matches
               if (!DocmawUtil.getFileExtention(local_file_names[x]).equalsIgnoreCase(getFileExtension(file_type)))
               {
                  /*
                   * Now we know that the extension for the fileile_type and the
                   * extension of the original file selected by the user does
                   * not match. This means that the user has selected a
                   * different type of filetype in the open file dialog. (Like
                   * test.txt as filename and WORD as file_type. So we rename
                   * the local file name to test.doc.
                   */
                  local_file_names[x] = DocmawUtil.getBaseFileName(local_file_names[x]) + "." + getFileExtension(file_type);
               }
            }
            else
            {
               /*
                * Now no file type has been passed, so we dont rename the file.
                * This probably means that this is not a first time checkin, so
                * both the filetype and the filename is already in EDM_FILE sowe
                * dont do anything... except of course extracting the file type
                * from the extension to avoid a nasty exception later on.
                */
               file_type = getFileType(DocmawUtil.getFileExtention(local_file_names[x]).toUpperCase());
            }
         }

         String file_ext = DocmawUtil.getFileExtention(local_file_names[x]);

         if ((file_ext.toUpperCase()).equals(view_file_ext))
         {
            original_file_ext = file_ext;
            view_file_ref_found = true;
         }

         cmd = trans.addCustomFunction("GETREMOTEFILENAME_" + x, "Edm_File_Api.Generate_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", file_ext);
         cmd.addParameter("IN_NUM",  file_no);

         cmd = trans.addCustomFunction("GETDOCTYPE_" + x, "Edm_Application_Api.Get_Doc_Type_From_Ext", "OUT_1");
         cmd.addParameter("IN_1", file_ext.toUpperCase());
      }

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info2", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      String[] repository_file_names = new String[no_of_files];

      selected_doc_types = new String[no_of_files];

      for (int x = 0; x < no_of_files; x++)
      {
         repository_file_names[x] = trans.getValue("GETREMOTEFILENAME_" + x + "/DATA/OUT_1");
         if (DEBUG)
            Util.debug("repository_file_names[" + x + "] = " + repository_file_names[x]);
         this.selected_doc_types[x] = trans.getValue("GETDOCTYPE_" + x + "/DATA/OUT_1");
      }

      //
      // 2006-01-04, MDAHSE:
      //
      // Create file references and set the file status to Operaiton
      // In Progress:
      //
      // 1. Because this is the way it should be, so that we can have
      // the file status Operation In Progress while the file transfer
      // is ongoing.
      //
      // 2. Because if we try to save the file into the database,
      // EdmFileStorage will complain that the EdmFile does not
      // exist. This was the prime reason and why I noticed all this,
      // btw.
      //
      // After creating the new file ref, we need to "start a check
      // out", which in practice just means setting file state to
      // Operation In Progress.
      //
      // Also, I am sorry for placing yet another loop here, but I
      // could not see any other way to merge the code below into the
      // other loop above. Not in the short time we had, working
      // overtime in the project and all. If someone else want to do
      // it, please don't hestiate, if you see a good way to do it.
      //

      if (DEBUG)
         Util.debug(this + ": Creating file references...");

      trans.clear();

      for (int x = 0; x < no_of_files; x++)
      {
         file_name = DocmawUtil.getFileName(local_file_names[x]);

         if (DEBUG)
            Util.debug(this + ": File number: " + x + ", File name: " + file_name);

         // Correct the file type of the original file and do it
         // always, regardless if it already is OK or not.

         if (file_name.equalsIgnoreCase(DocmawUtil.getFileName((original_file_name))))
         {
            file_name = getTypeCorrectedFileName(file_name, file_type);
            ori_file_name = file_name;

            if (DEBUG)
               Util.debug(this + ": 001, ORIGINAL: doc_type = " + this.selected_doc_types[x]);

            cmd = trans.addCustomCommand("CHECKINNEW_" + x, "Edm_File_API.Check_In_File_");
            cmd.addParameter("OUT_1");
            cmd.addParameter("OUT_2");
            cmd.addParameter("OUT_3"); // Bug 59605, indicates if a new reference was created
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", file_name);
            cmd.addParameter("IN_STR1", file_type);
            cmd.addParameter("IN_STR1", "FALSE");
            cmd.addParameter("IN_NUM",  file_no);

            // Set file status to Operation In Progress but only on
            // the ORIGINAL
            // we have not locked/unlocked view even in windows client.

            if ("ORIGINAL".equals(selected_doc_types[x]) || !"VIEW".equals(selected_doc_types[x]))
            {
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", this.selected_doc_types[x]);
               cmd.addParameter("IN_NUM",  file_no);
               cmd.addParameter("IN_STR1", "StartCheckOut");
               cmd.addParameter("IN_STR1", null);
            }
            else if ("VIEW".equals(selected_doc_types[x]) && "TRUE".equals(OriginalRefExist))
            {
               bViewTypeOriRefExist = true;
               // just to handle the case when view type is selected for
               // Original
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", "ORIGINAL");
               cmd.addParameter("IN_NUM",  file_no);
               cmd.addParameter("IN_STR1", "StartCheckIn");
               cmd.addParameter("IN_STR1", null);
               selected_doc_types[x] = "ORIGINAL";
            }
         }
         else
         {
            if (DEBUG)
               Util.debug(this + ": 002, OTHER: doc_type = " + this.selected_doc_types[x]);

            cmd = trans.addCustomFunction("GETFILETYPE_" + x, "Edm_Application_API.Get_File_Type", "OUT_1");
            cmd.addParameter("IN_STR1", DocmawUtil.getFileExtention(file_name));

            cmd = trans.addCustomCommand("CHECKINNEW_" + x, "Edm_File_Api.Check_In_File_");
            cmd.addParameter("OUT_1");
            cmd.addParameter("OUT_2");
            cmd.addParameter("OUT_3"); // Bug 59605, indicates if a new reference was created
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", file_name);
            cmd.addReference("OUT_1", "GETFILETYPE_" + x + "/DATA");
            cmd.addParameter("IN_STR1", "TRUE");
            cmd.addParameter("IN_NUM",  file_no);

            if (!"VIEW".equals(selected_doc_types[x]))
            {
               cmd = trans.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", selected_doc_types[x]);
               cmd.addParameter("IN_NUM",  file_no);
               cmd.addParameter("IN_STR1", "StartCheckOut");
               cmd.addParameter("IN_STR1", null);
            }
         }
         cmd = trans.addCustomFunction("REMOTEFILENAME_" + x, "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addReference("OUT_1", "CHECKINNEW_" + x + "/DATA");
         cmd.addParameter("IN_NUM",  file_no);
      }

      trans = perform(trans);

      // Put all files to the repository...
      // bug 58326, starts....
      String[] local_file_names2 = generateLocalFileNames(unique_file_id, repository_file_names);

      //
      // Bug 59605, Start
      // Check in the files to the repository. If an exception occurs,
      // role-back the state changes from Operation In Progress
      //
      try
      {
         putFilesToRepository(addKeys(edm_rep_info, doc_class, doc_no, doc_sheet, doc_rev), local_file_names2, repository_file_names); // Bug Id 69087
      }
      catch (FndException fnd)
      {
         ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();

         for (int x = 0; x < no_of_files; x++)
         {
            boolean new_file = "TRUE".equals(trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_3"));

            if (new_file)
            {
               cmd = trans2.addCustomCommand("DELETEFILEREF_" + x, "Edm_File_API.Remove_Invalid_Reference_");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_1"));
               cmd.addParameter("IN_NUM",  file_no);
            }
            else
            {
               cmd = trans2.addCustomCommand("SETSTATECHECKOUT_" + x, "Edm_File_API.Set_File_State");
               cmd.addParameter("IN_STR1", doc_class);
               cmd.addParameter("IN_STR1", doc_no);
               cmd.addParameter("IN_STR1", doc_sheet);
               cmd.addParameter("IN_STR1", doc_rev);
               cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_1"));
               cmd.addParameter("IN_NUM",  file_no);
               cmd.addParameter("IN_STR1", "FinishCheckOut");
               cmd.addParameter("IN_STR1", null);
            }
         }

         perform(trans2);

         // re-raise exception..
         throw fnd;
      }
      // Bug 59605, End

      // bug 58326, ends....

      if (DEBUG)
         Util.debug(this + ": Files checked in to repository sucessfully..");
      if (DEBUG)
         Util.debug(this + ": Changing edm state..");

      String[] old_file_names = new String[no_of_files];
      int count = 0;

      for (int x = 0; x < no_of_files; x++)
      {
         String old_file_ext = trans.getValue("CHECKINNEW_" + x + "/DATA/OUT_2");

         // delete files that dont have a reference anymore
         if (!Str.isEmpty(old_file_ext))
         {
            String repository_file_name = trans.getValue("REMOTEFILENAME_" + x + "/DATA/OUT_1");
            old_file_names[count++] = repository_file_name.substring(0, repository_file_name.lastIndexOf(".") + 1) + old_file_ext;
            if (DEBUG)
               Util.debug("old_file_names[" + (count - 1) + "] = " + old_file_names[(count - 1)]);
         }

         doc_type_checked_in = this.selected_doc_types[x];

         if (DEBUG)
            Util.debug(this + ": doc_type_checked_in = " + doc_type_checked_in);

         if (!mgr.isEmpty(doc_type_checked_in) && doc_type_checked_in.equals("ORIGINAL"))
            original_file_ref_found = true;
      }

      //
      // Change the Edm State to checked in on all file references
      //

      trans.clear();
      // removed >>>>> Edm_File_API.Set_File_State_All being problematic

      for (int x = 0; x < no_of_files; x++)
      {
         cmd = trans.addCustomCommand("SETSTATECHECKIN" + x, "Edm_File_API.Set_File_State");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", selected_doc_types[x]);
         cmd.addParameter("IN_NUM",  file_no);
         cmd.addParameter("IN_STR1", "FinishCheckIn");
         cmd.addParameter("IN_STR1", null);
      }

      // Here is where we fool the system into thinking we have two
      // files connected, when we actually only have one. Used when
      // checking in a file that normally would have been a VIEW file,
      // as ORIGINAL. This might seem ugly but actually works quite
      // well.

      if ((!original_file_ref_found) && (view_file_ref_found) && !bViewTypeOriRefExist)
      {
         cmd = trans.addCustomCommand("CREATEORIGINALREF", "Edm_File_API.Check_In_View_Type_Orig_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", ori_file_name);
      }

      trans = perform(trans);

      if (count > 0)
      {
         String[] temp = old_file_names;
         old_file_names = new String[count];
         System.arraycopy(temp, 0, old_file_names, 0, count);
         deleteFilesInRepository(edm_rep_info, old_file_names); // Bug Id 69087
      }

      if (DEBUG)
         Util.debug(this + ": checkInDocument() }");

      return local_file_names;
   }
   // Added end


    //bug 58326,  Modified parameter secondary_ftp_file_id to unique_file_id
   public void checkInFromBC(String briefcase_no,
                             String line_number,
                             String doc_class,
                             String doc_type,
                             String unique_file_id) throws FndException
   {
      if (DEBUG) Util.debug(this+": checkInExistingFromBC() {");

      ASPManager mgr = getASPManager();
      String what_to_update="";

      trans = mgr.newASPTransactionBuffer();

      // Do neccessary validation and make file reference if needed, update states
      cmd = trans.addCustomCommand("ACCEPTDOCUMENT", "Doc_Briefcase_Unpacked_API.Accept_");
      cmd.addParameter("OUT_1", "");
      cmd.addParameter("OUT_2", "");
      cmd.addParameter("OUT_3");
      cmd.addParameter("OUT_4");
      cmd.addParameter("OUT_5");
      cmd.addParameter("IN_STR1", docBriefcase);
      cmd.addParameter("IN_STR1", line_number);
      trans = perform(trans);

      checkin_file_exts = trans.getValue("ACCEPTDOCUMENT/DATA/OUT_1");
      delete_file_exts = trans.getValue("ACCEPTDOCUMENT/DATA/OUT_2");
      String doc_no = trans.getValue("ACCEPTDOCUMENT/DATA/OUT_3");
      String doc_sheet = trans.getValue("ACCEPTDOCUMENT/DATA/OUT_4");
      String doc_rev = trans.getValue("ACCEPTDOCUMENT/DATA/OUT_5");

      trans.clear();

      //get file name of original in base ftp
      cmd = trans.addCustomFunction("REMOTEFILENAME", "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      cmd = trans.addCustomFunction("EDMREPINFO", "EDM_FILE_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      cmd = trans.addCustomFunction("EDMINFO", "EDM_FILE_API.get_edm_information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String edm_rep_info       = trans.getValue("EDMREPINFO/DATA/OUT_1");
      String original_file_name = trans.getValue("REMOTEFILENAME/DATA/OUT_1");
      String edm_info           = trans.getValue("EDMINFO/DATA/OUT_1");

      String ori_file_name_withNoExt = original_file_name.substring(0,original_file_name.lastIndexOf('.'));

      //remove old files at first!!
      if (!mgr.isEmpty(delete_file_exts))
      {
         StringTokenizer st2    = new StringTokenizer(delete_file_exts, "^");
         int no_of_delete_files = st2.countTokens();
         String[] old_file_names        = new String[no_of_delete_files];
         //Bug 59843, Start
         selected_doc_types = new String[no_of_delete_files];
         //Bug 59843, End

         for (int x=0;x< no_of_delete_files;x++)
         {
            old_file_names[x] = ori_file_name_withNoExt + st2.nextToken();
         }

         deleteFilesInRepository(edm_rep_info, old_file_names); //Bug Id 69087
      }

      StringTokenizer st = new StringTokenizer(checkin_file_exts, "^");
      int no_of_files    = st.countTokens();

      String[] repository_file_names = new String[no_of_files];
      String[] file_ext              = new String[no_of_files];

      //Bug Id 58771, Start
      trans.clear();

      for (int x = 0; x < no_of_files; x++)
      {
         file_ext[x] = st.nextToken();

         //Bug Id 69087, Start
         cmd = trans.addCustomFunction("GETREMOTEFILENAME_" + x,  "Edm_File_Api.Generate_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", file_ext[x]);
         //Bug Id 69087, End

         // GET THE DOC TYPES HERE
         cmd =  trans.addCustomFunction("GETDOCTYPE_" + x, "Edm_Application_Api.Get_Doc_Type_From_Ext", "OUT_1");
         cmd.addParameter("IN_1", file_ext[x].toUpperCase());
      }

      trans = perform(trans);

      selected_doc_types = new String[no_of_files];
       
      for (int x = 0; x < no_of_files; x++)
      {
         repository_file_names[x] = trans.getValue("GETREMOTEFILENAME_" + x + "/DATA/OUT_1"); //Bug Id 69087
         selected_doc_types[x] = trans.getValue("GETDOCTYPE_" + x + "/DATA/OUT_1");
      }
      //Bug Id 58771, End
      //bug 58326, starts....
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);
      putFilesToRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev), local_file_names, repository_file_names);   //Bug Id 69087
      
      //bug 58326, ends....
      

      if (DEBUG) Util.debug(this+": checkInExistingFromBC() }");
   }



   public void checkInNewFileNonIe(String doc_class,
                                   String doc_no,
                                   String doc_sheet,
                                   String doc_rev,
                                   String doc_type,
                                   String file_extension,
                                   String file_name,
                                   String originalFileName) throws FndException
   {
      if (DEBUG) Util.debug(this+": checkInNewFileNonIe() {");

      ASPManager mgr = getASPManager();

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      file_extension = file_extension.toUpperCase();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_2");
      cmd.addParameter("IN_STR1", file_extension);
      trans = perform(trans);

      String file_type = trans.getValue("GETFILETYPE/DATA/OUT_2");

      if (mgr.isEmpty(file_type))
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFILETYPENOTALLOWED: This file type is not registered as an EDM Application."));
      }

      trans.clear();
      cmd = trans.addCustomCommand("CRFILEREF", "Edm_File_API.Create_File_Reference");
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_NUM", "1");
      cmd.addParameter("IN_STR1", originalFileName);

      
      if (!Str.isEmpty(view_file_ext))
      {
         cmd = trans.addCustomFunction("GETVIEWFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_3");
         cmd.addParameter("IN_STR1", view_file_ext);

         cmd = trans.addCustomCommand("CRVIEWFILEREF", "Edm_File_API.Create_File_Reference");
         cmd.addParameter("IN_STR1", "");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "VIEW");
         cmd.addReference("OUT_3","GETVIEWFILETYPE/DATA");
         cmd.addParameter("IN_STR1", "");
         cmd.addParameter("IN_NUM", "1");
      }

      cmd = trans.addCustomCommand("SETSTATE1", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "StartCheckOut");
      cmd.addParameter("IN_STR1", null);

      cmd = trans.addCustomCommand("SETSTATE2", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "FinishCheckIn");
      cmd.addParameter("IN_STR1", null);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      putFileIntoArchiveNonIe(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev,doc_type), file_name);

      if (DEBUG) Util.debug(this+": checkInNewFileNonIe() }");
   }


   // //bug 58326, starts....

  //bug 58326, modified parameter secondary_ftp_file_id into unique_file_id
   public String[] createNewImportFile(String unique_file_id,
                                   String local_checkout_path,
                                   String doc_class,
                                   String doc_no,
                                   String doc_sheet,
                                   String doc_rev,
                                   String file_type,
                                   String title,
                                   String files_checked_in,
                                   String num_generator,
                                   String booking_list,
                                   String id1,
                                   String id2,
                                   String view_files_checked_in) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewImportFile() {");

      // Bug Id 79368, Start  
      String obj_id2 = null;
      String obj_version2 = null;
      String attr = "";
      boolean doc_no_exists = true;
      // Bug Id 79368, End

      ASPManager mgr = getASPManager();

      if (doc_class.length()==0)
         throw new FndException(mgr.translate("DOCMAWDOCSRVDOCCLSMIS2: Document Class is missing in argument"));
      if (title.length()==0)
         throw new FndException(mgr.translate("DOCMAWDOCSRVDOCTTLMIS2: Document Title is missing in argument"));
      if (doc_sheet.length()==0)
         throw new FndException(mgr.translate("DOCMAWDOCSRVDOCREVMIS3: Document Sheet is missing in argument"));
      if (doc_rev.length()==0)
         throw new FndException(mgr.translate("DOCMAWDOCSRVDOCREVMIS: Document Revision is missing in argument"));
      if (file_type.length()==0)
         throw new FndException(mgr.translate("DOCMAWDOCSRVFILTYPMIS: File Type is missing in argument"));

      trans = mgr.newASPTransactionBuffer();

      if (mgr.isEmpty(doc_no)) //Bug Id 79368, Generate doc_no only if it doesn't exists
      {
          // If Number Generator is configured, get Doc No
          if ("ADVANCED".equals(num_generator))
          {
              trans.clear();
              cmd = trans.addCustomFunction("GETDOCNO", "Doc_Title_API.Generate_Doc_Number", "OUT_1");
              cmd.addParameter("IN_STR1", doc_class);
              cmd.addParameter("IN_STR1", booking_list);
              cmd.addParameter("IN_STR1", id1);
              cmd.addParameter("IN_STR1", id2);
              cmd.addParameter("IN_STR1", "");
              trans = perform(trans);

              doc_no = trans.getValue("GETDOCNO/DATA/OUT_1");
          }
          // Bug Id 79368, Start
          doc_no_exists= false;
      }
      // Bug Id 79368, End


      trans.clear();
      cmd = trans.addCustomCommand("INSERTFILE", "Doc_File_Import_API.Insert_File");
      cmd.addParameter("INOUT_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", title);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);

      cmd = trans.addCustomFunction("GETFNDUSER", "Fnd_Session_Api.Get_Fnd_User", "OUT_1");
      trans = perform(trans);

      doc_no = trans.getValue("INSERTFILE/DATA/INOUT_STR1");
      String fnd_user = trans.getValue("GETFNDUSER/DATA/OUT_1");

      //Bug Id 79368, Start, update File_import_tab with the document no if it doesn't already exists
      if (!(doc_no_exists))
      { 
          trans.clear();
          //bug 58216 starts
          ASPQuery query = trans.addQuery("GETKEYS", "SELECT objid,objversion FROM Doc_File_Import WHERE doc_title = ? AND fnd_user = ?");
          query.addParameter("IN_STR1",title);
          query.addParameter("IN_STR1",fnd_user);
          //bug 58216 end
      
          trans = perform(trans);

          //bug 58216 starts
          String obj_id = trans.getValue("GETKEYS/DATA/OBJID");
          String obj_version = trans.getValue("GETKEYS/DATA/OBJVERSION");
          //bug 58216 ends.


          attr  = "DOC_NO" + DocmawUtil.FIELD_SEPARATOR + doc_no + DocmawUtil.RECORD_SEPARATOR;
             
          trans.clear();
          cmd = trans.addCustomCommand("MODIFYDO", "Doc_File_Import_API.Modify__");
          cmd.addParameter("IN_STR1", "");
          cmd.addParameter("IN_STR1", obj_id);
          cmd.addParameter("IN_STR1", obj_version);
          cmd.addParameter("IN_STR1", attr);
          cmd.addParameter("IN_STR1", "DO");
          trans = mgr.perform(trans);
      }
      trans.clear();
      //Bug Id 79368, End

      String file_name = ""; //Bug Id 66826
      //Bug 71814, Start
      String sViewFileName = "";
      //Bug 71814, End
      boolean view_file_ref_found = false;
      boolean original_file_ref_found = false;

      

      StringTokenizer st = new StringTokenizer(files_checked_in, "|");
      int no_of_files = st.countTokens();
      String[] localFileNames = new String[no_of_files];
      String view_file_ext = !mgr.isEmpty(view_files_checked_in) ? DocmawUtil.getFileExtention(view_files_checked_in).toUpperCase() : "";

      for (int x = 0; x < no_of_files; x++)
      {
         localFileNames[x] = st.nextToken();
         file_name = DocmawUtil.getFileName(localFileNames[x]); //Bug Id 66826
         String file_ext = DocmawUtil.getFileExtention(file_name).toUpperCase();


         if (file_ext.equals(view_file_ext))
         {
            view_file_ref_found = true;
            //Bug 71814, Start
            sViewFileName = file_name;
            //Bug 71814, End
         }

         /*IMPORT FILE*/
         cmd = trans.addCustomFunction("GETFILETYPE_" + x, "Edm_Application_API.Get_File_Type", "OUT_1");
         cmd.addParameter("IN_STR1", DocmawUtil.getFileExtention(file_name));

         cmd = trans.addCustomCommand("CHECKINNEW" + x,  "Edm_File_Api.Check_In_File_");
         cmd.addParameter("OUT_1");
         cmd.addParameter("OUT_2");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", file_name);
         cmd.addReference("OUT_1", "GETFILETYPE_" + x + "/DATA"); 
         
         cmd = trans.addCustomFunction("REMOTEFILENAME" + x,  "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addReference("OUT_1", "CHECKINNEW" + x + "/DATA");
      }

      trans = perform(trans);

      String[] repository_file_names = new String[no_of_files];
      selected_doc_types = new String[no_of_files];

      for (int x = 0; x < no_of_files; x++)
      {
         repository_file_names[x] = trans.getValue("REMOTEFILENAME" + x + "/DATA/OUT_1");
         if (DEBUG) Util.debug("repository_file_names[" + x + "] = " + repository_file_names[x]);
      }

      String doc_type_checked_in = "";

      for (int i=0; i<no_of_files; i++)
      {
         doc_type_checked_in = trans.getValue("CHECKINNEW" + i + "/DATA/OUT_1");
         selected_doc_types[i] = doc_type_checked_in;
         if (doc_type_checked_in.equals("ORIGINAL"))
         {
            original_file_ref_found = true;
         }
      }

      if ((!original_file_ref_found) && (view_file_ref_found))
      {
         trans.clear();
         cmd = trans.addCustomCommand("CREATEORIGINALREF","Edm_File_API.Check_In_View_Type_Orig_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", sViewFileName); //Bug Id 71814
         perform(trans);
      }

      trans.clear();
      cmd = trans.addCustomFunction("GETEDMREPINFO", "EDM_FILE_API.Get_Edm_Repository_Info","OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", "ORIGINAL");
      trans = perform(trans);

      String edm_rep_info = trans.getValue("GETEDMREPINFO/DATA/OUT_1");
      //bug 58326, starts....
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);

      try
      {
          putFileToRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev), local_file_names, repository_file_names);
          //bug 58326, ends....

          //Bug Id 79368, Start - Update the file import to imported state once the file is in the repository.
          trans.clear();
          ASPQuery query = trans.addQuery("GETKEYS", "SELECT OBJID,OBJVERSION FROM DOC_FILE_IMPORT WHERE DOC_TITLE ='"+title+"' AND FND_USER ='"+fnd_user+"'");
          trans = mgr.perform(trans);
	      
          obj_id2 = trans.getValue("GETKEYS/DATA/OBJID");
          obj_version2 = trans.getValue("GETKEYS/DATA/OBJVERSION");
          attr = "IMPORTED" + (char)31 + "1" + (char)30;

          trans.clear();
          cmd = trans.addCustomCommand("MODIFYDO", "Doc_File_Import_API.Modify__");
          cmd.addParameter("IN_1", "");
          cmd.addParameter("IN_1", obj_id2);
          cmd.addParameter("IN_1", obj_version2);
          cmd.addParameter("IN_1", attr);
          cmd.addParameter("IN_1", "DO");
          trans = mgr.perform(trans);
          //Bug Id 79368, End
      }
      catch (Throwable e)
      {
          //put file to repository failed
          ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();
         
          //Delete all file references.  
          for (int x = 0; x < no_of_files; x++)
          {
              cmd = trans2.addCustomCommand("DELETEFILEREF_" + x, "Edm_File_API.Remove_Invalid_Reference_");
              cmd.addParameter("IN_1", doc_class);
              cmd.addParameter("IN_1", doc_no);
              cmd.addParameter("IN_1", doc_sheet);
              cmd.addParameter("IN_1", doc_rev);
              cmd.addParameter("IN_1", selected_doc_types[x]);
          }

          if ((!original_file_ref_found) && (view_file_ref_found))
          {
              //Remove original file reference when importing a view file as an original file
              cmd = trans2.addCustomCommand("DELETEFILEREF_" + no_of_files, "Edm_File_API.Remove_Invalid_Reference_");
              cmd.addParameter("IN_1", doc_class);
              cmd.addParameter("IN_1", doc_no);
              cmd.addParameter("IN_1", doc_sheet);
              cmd.addParameter("IN_1", doc_rev);
              cmd.addParameter("IN_1", "ORIGINAL");    
          }

          //get the objid and objversion to remove the document (title + issue)
          ASPQuery query = trans2.addQuery("GETISSUKEYS", "SELECT objid,objversion FROM DOC_ISSUE WHERE doc_class ='"+doc_class+"' AND doc_no ='"+doc_no+"' AND doc_sheet ='"+doc_sheet+"' AND doc_rev ='"+doc_rev+"'");
          trans2 = mgr.perform(trans2);

          obj_id2 = trans2.getValue("GETISSUKEYS/DATA/OBJID");
          obj_version2 = trans2.getValue("GETISSUKEYS/DATA/OBJVERSION");

          trans2.clear();
         //Need to set the document to Obsolete before deleting it.
          cmd = trans2.addCustomCommand("ISSUEREOBS", "Doc_Issue_Api.Promote_To_Obsolete__");
          cmd.addParameter("IN_1", "");
          cmd.addParameter("IN_1", obj_id2);
        	 cmd.addParameter("OUT_1", obj_version2);
        	 cmd.addParameter("IN_1", "");
        	 cmd.addParameter("IN_1", "DO");
         
        	 //Deleting the document (title + issue)
        	 cmd = trans2.addCustomCommand("ISSUEREMOVE", "Doc_Issue_Api.Remove__");
        	 cmd.addParameter("IN_1", "");
        	 cmd.addParameter("IN_1", obj_id2);
        	 cmd.addReference("OUT_1","ISSUEREOBS/DATA");
        	 cmd.addParameter("IN_1", "DO");
        	 trans2 = mgr.perform(trans2);
        	 // re-raise exception. Raise an alert instead of throwing the error as the file import page has to refresh at the end
        	 appendDirtyJavaScript("alert(\"" + mgr.encodeStringForJavascript(e.getMessage()) +"\");\n");
      }
      //Bug Id 79368, End

      
      
      return localFileNames;
   }



   public String checkOutDocument(String doc_class,
                                  String doc_no,
                                  String doc_sheet,
                                  String doc_rev,
                                  String master_doc_type,
                                  String check_out_path,
                                  String file_name) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_NUM",  this.file_no);

      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      String doc_type_list = getAssociatedDocumentTypes(doc_class, master_doc_type, "CHECKOUT");//Bug Id 65997

      //Bug Id 68528, start
      String[] repository_file_names;
      if ("REDLINE".equals(master_doc_type) )
      {
         repository_file_names = getRepositoryFileNames (doc_class,doc_no,doc_sheet,doc_rev,doc_type_list);
      }
      else
         repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list, this.file_no + "^");//Bug Id 65997
      //Bug Id 68528, end

     //bug 58326, starts....
      String unique_file_id = getRandomFilename();
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);

      getFilesFromRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev),local_file_names, repository_file_names); //Bug Id 69087

      //Bug 56631, Start
      trans.clear();
      cmd = trans.addCustomCommand("CHECKOUTEDMFILES", "Edm_File_Api.Check_Out_Master_File_");
      cmd.addParameter("IN_STR1", doc_class); 
      cmd.addParameter("IN_STR1", doc_no); 
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_NUM",  this.file_no);
      cmd.addParameter("IN_STR1", check_out_path); 
      cmd.addParameter("IN_STR1", file_name);
 
      trans = mgr.perform(trans);
      //Bug 56631, End

      return unique_file_id;
    //bug 58326, ends....
   }


   public String checkOutDocumentForBC(String doc_class,
                                       String doc_no,
                                       String doc_sheet,
                                       String doc_rev,
                                       String check_out_path) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", "ORIGINAL");

      cmd = trans.addCustomFunction("EDMINFORMATIONS", "Edm_File_Api.get_edm_information", "OUT_2");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", "ORIGINAL");
      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      String edm_info = trans.getValue("EDMINFORMATIONS/DATA/OUT_2");


      if (!getStringAttribute(edm_info, "ORIGINAL_REF_EXIST").equals("TRUE"))
      {
         fileNotExist = true;
      }
      //bug 58326 
      String unique_file_id = getRandomFilename();
      if (!fileNotExist)
      {
         //Bug Id 67966, Start 
	 String doc_type_list = null;
         trans = mgr.newASPTransactionBuffer();
         cmd = trans.addCustomFunction("PROCDOCTYPESLIST", "Edm_File_Api.Get_Proc_Doc_Types_", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", "CHECKOUT");
         trans = perform(trans);

         doc_type_list = trans.getValue("PROCDOCTYPESLIST/DATA/OUT_1");
         doc_type_list = "ORIGINAL^" + (mgr.isEmpty(doc_type_list) ? "" : doc_type_list);
	 //Bug Id 67966, End

         if (getStringAttribute(edm_info, "VIEW_REF_EXIST").equals("TRUE"))
         {
            doc_type_list ="VIEW^" + doc_type_list;
         }

         if (getStringAttribute(edm_info, "REDLINE_EXIST").equals("TRUE"))
         {
            doc_type_list ="REDLINE^" + doc_type_list;
         }

         trans.clear();
	 //Bug Id 67966, Start
	 StringTokenizer st = new StringTokenizer(doc_type_list, "^");
	 int no_doc_types = st.countTokens();
	 String current_doc_type;
	 
	 for (int x = 0; x < no_doc_types; x++)
	 {
	     current_doc_type = st.nextToken();
	     cmd = trans.addCustomCommand("CHECKOUTEDMFILES_" + x, "Edm_File_API.Set_File_State");
	     cmd.addParameter("IN_STR1", doc_class);
	     cmd.addParameter("IN_STR1", doc_no);
	     cmd.addParameter("IN_STR1", doc_sheet);
	     cmd.addParameter("IN_STR1", doc_rev);
	     cmd.addParameter("IN_STR1", current_doc_type);
	     cmd.addParameter("IN_STR1", "Export");
	     cmd.addParameter("IN_STR1", null);
	 }
	 //Bug Id 67966, End

         trans = perform(trans);

         String[] repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);//Bug Id 65997
      //bug 58326, starts....


         String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);

         getFilesFromRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev),local_file_names, repository_file_names); //Bug Id 69087
      }

      return unique_file_id;
    //bug 58326, ends....
   }


   /*
      checkOutDocumentNonIe
      Checks out a document for a non-ie browser.
   */

   public String checkOutDocumentNonIe(String doc_class,
                                       String doc_no,
                                       String doc_sheet,
                                       String doc_rev,
                                       String doc_type,
                                       String check_out_path) throws FndException
   {
      ASPManager mgr = getASPManager();

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomCommand("SETSTATE1", "EDM_FILE_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "StartCheckOut");
      cmd.addParameter("IN_STR1", check_out_path);

      cmd = trans.addCustomCommand("SETSTATE2", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "FinishCheckOut");
      cmd.addParameter("IN_STR1", check_out_path);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      //Bug 60698, Start,Removed the code added by bug 59564
      //Bug 59564, Start, Added full path to the file name
      return getFileFromArchiveNonIe( addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev,doc_type));
      //Bug 59564, End
      //Bug 60698, End
   }


   public String createNewDocument(String doc_class,
                                   String doc_no,
                                   String doc_sheet,
                                   String doc_rev,
                                   String master_doc_type,
                                   String check_out_path,
                                   String check_out_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewDocument() (using template) {");

      ASPManager mgr = getASPManager();
      String tmpl_doc_class = null;//dmpr303
      String tmpl_doc_no = null;
      String tmpl_doc_sheet = null;
      String tmpl_doc_rev = null;

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomCommand("CREATENEWUSINGTMPL", "Edm_File_Api.Create_File_Refs_Using_Tmpl_");
      cmd.addParameter("OUT_4", tmpl_doc_class);//dmpr303
      cmd.addParameter("OUT_1", tmpl_doc_no);
      cmd.addParameter("OUT_2", tmpl_doc_sheet);
      cmd.addParameter("OUT_3", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", check_out_file_name);

      cmd = trans.addCustomCommand("CHECKOUTEDMFILES", "Edm_File_Api.Check_Out_Master_File_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", check_out_path);
      cmd.addParameter("IN_STR1", check_out_file_name);

      trans = perform(trans);

      tmpl_doc_class = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_4");
      tmpl_doc_no = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_1");
      tmpl_doc_sheet = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_2");
      tmpl_doc_rev = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_3");

      //Bug Id 67447, start
      trans.clear();

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", tmpl_doc_class);
      cmd.addParameter("IN_STR1", tmpl_doc_no);
      cmd.addParameter("IN_STR1", tmpl_doc_sheet);
      cmd.addParameter("IN_STR1", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);

      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      //Bug Id 67447, end

      String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);
      String[] repository_file_names = getRepositoryFileNamesFromServer(tmpl_doc_class, tmpl_doc_no, tmpl_doc_sheet, tmpl_doc_rev, doc_type_list);//Bug Id 65997 //Bug Id 74950
      //bug 58326, starts....
      String unique_file_id = getRandomFilename();
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);

      getFileFromRepository(addKeys(edm_rep_info,tmpl_doc_class,tmpl_doc_no,tmpl_doc_sheet,tmpl_doc_rev), local_file_names,repository_file_names);//Bug Id 58724

      if (DEBUG) Util.debug(this+": createNewDocument() (using template) }");

      return unique_file_id;
      //bug 58326, ends....
   }
   
   // Added by Terry 20121023
   public void createNewDocumentUseTempl(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String master_doc_type,
         String check_out_path,
         String check_out_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewDocument() (using template) {");
      
      ASPManager mgr = getASPManager();
      String tmpl_doc_class = null;
      String tmpl_doc_no = null;
      String tmpl_doc_sheet = null;
      String tmpl_doc_rev = null;
      
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomCommand("CREATENEWUSINGTMPL", "Edm_File_Api.Create_File_Refs_Using_Tmpl_");
      cmd.addParameter("OUT_4", tmpl_doc_class);//dmpr303
      cmd.addParameter("OUT_1", tmpl_doc_no);
      cmd.addParameter("OUT_2", tmpl_doc_sheet);
      cmd.addParameter("OUT_3", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", check_out_file_name);
      
      trans = perform(trans);
      
      tmpl_doc_class = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_4");
      tmpl_doc_no = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_1");
      tmpl_doc_sheet = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_2");
      tmpl_doc_rev = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_3");
      
      // Modified by Terry 20121023
      // Original:
      /*
      trans.clear();
      
      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", tmpl_doc_class);
      cmd.addParameter("IN_STR1", tmpl_doc_no);
      cmd.addParameter("IN_STR1", tmpl_doc_sheet);
      cmd.addParameter("IN_STR1", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      
      trans = perform(trans);
      
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");
      
      String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);
      String[] repository_file_names = getRepositoryFileNamesFromServer(tmpl_doc_class, tmpl_doc_no, tmpl_doc_sheet, tmpl_doc_rev, doc_type_list);//Bug Id 65997 //Bug Id 74950
      
      int count = repository_file_names.length;
      String[] local_file_names = new String[count];
      String[] unique_file_ids = new String[count];
      
      for (int i = 0; i < count; i++)
      {
         unique_file_ids[i] = getRandomFilename();
         local_file_names[i] = generateLocalFileName(unique_file_ids[i], repository_file_names[i]);
      }
      
      getFileFromRepository(addKeys(edm_rep_info,tmpl_doc_class,tmpl_doc_no,tmpl_doc_sheet,tmpl_doc_rev), local_file_names, repository_file_names);//Bug Id 58724
      
      if (DEBUG) Util.debug(this+": createNewDocument() (using template) }");
      
      return unique_file_ids;
      */
      copyFileInRepository(tmpl_doc_class, tmpl_doc_no, tmpl_doc_sheet, tmpl_doc_rev,
            doc_class, doc_no, doc_sheet, doc_rev);
   }
   // Added end


   public String createNewDocument(String doc_class,
                                   String doc_no,
                                   String doc_sheet,
                                   String doc_rev,
                                   String master_doc_type,
                                   String file_type,
                                   String check_out_path,
                                   String check_out_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewDocument() {");

      ASPManager mgr = getASPManager();

      String file_name = getRandomFilename();
      String local_file_name = getTmpPath() + file_name;
      //bug 58326
      String unique_file_id = file_name;

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILEEXT", "Edm_Application_Api.Get_File_Extention", "OUT_1");
      cmd.addParameter("IN_STR1", file_type);
      trans = perform(trans);

      String file_ext = trans.getValue("GETFILEEXT/DATA/OUT_1");

      //bug 58326
      createNullFile(local_file_name + "." + file_ext, mgr);
      //bug 58326, removed putFileToSecondaryFtp

      trans.clear();
      
      // Added by Terry 20121024
      // if check_out_file_name is empty, then use doc_title instead.
      String original_file_name = check_out_file_name;
      if (mgr.isEmpty(check_out_file_name))
      {
         cmd = trans.addCustomFunction("GETDOCTITLE", "Doc_Title_API.Get_Title", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         trans = perform(trans);
         original_file_name = trans.getValue("GETDOCTITLE/DATA/OUT_1") + "." + file_ext;
         
         trans.clear();
      }
      // Added end
      
      cmd = trans.addCustomCommand("FILTEMPLINFO", "Edm_File_Api.Create_File_Reference");
      cmd.addParameter("IN_STR1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_NUM", "1");
      // Modified by Terry 20121024
      // Original:
      // cmd.addParameter("IN_STR1", check_out_file_name);
      cmd.addParameter("IN_STR1", original_file_name);
      // Modified end

      cmd = trans.addCustomCommand("SETSTATE1", "Edm_File_Api.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", "CheckOut");
      cmd.addParameter("IN_STR1", check_out_path);
      // Modified by Terry 20121024
      // Original:
      // cmd.addParameter("IN_STR1", check_out_file_name);
      cmd.addParameter("IN_STR1", original_file_name);
      // Modified end

      perform(trans);

      if (DEBUG) Util.debug(this+": createNewDocument() }");
       //bug 58326
      return unique_file_id;
   }

   /*
      createNewDocumentNonIE
      This method handles creating a new document for non IE docs
      that dont have a template.
   */
   public String createNewDocumentNonIE(String doc_class,
                                        String doc_no,
                                        String doc_sheet,
                                        String doc_rev,
                                        String master_doc_type,
                                        String file_type) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewDocumentNonIE() {");

      ASPManager mgr = getASPManager();

      String file_name = getRandomFilename();
      String local_file_name = getTmpPath() + file_name;

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILEEXT", "Edm_Application_Api.Get_File_Extention", "OUT_1");
      cmd.addParameter("IN_STR1", file_type);
      trans = perform(trans);

      String file_ext = trans.getValue("GETFILEEXT/DATA/OUT_1");

      createNullFile(local_file_name + "." + file_ext, mgr);

      trans.clear();

      cmd = trans.addCustomCommand("FILTEMPLINFO", "Edm_File_Api.Create_File_Reference");
      cmd.addParameter("IN_STR1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_NUM", "1");

      cmd = trans.addCustomCommand("SETSTATE1", "Edm_File_Api.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", "CheckOut");
      cmd.addParameter("IN_STR1", ""); // we dont know what local path the user will choose.

      perform(trans);

      if (DEBUG) Util.debug(this+": createNewDocumentNonIE() }");

      //Bug 60698, Start, Removed the code added by 59564
      //Bug 59564, Start
      return file_name;
      //Bug 59564, End
      //Bug 60698, End
   }

   /*
      createNewDocumentNonIETmplt
      This funtion handles creating a new document for non IE docs
      that have a template.
   */

   public String createNewDocumentNonIETmplt(String doc_class,
                                             String doc_no,
                                             String doc_sheet,
                                             String doc_rev,
                                             String master_doc_type) throws FndException
   {
      if (DEBUG) Util.debug(this+": createNewDocumentNonIETmpl() (using template) {");

      ASPManager mgr = getASPManager();
      String tmpl_doc_no = null;
      String tmpl_doc_sheet = null;
      String tmpl_doc_rev = null;

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomCommand("CREATENEWUSINGTMPL", "Edm_File_Api.Create_File_Refs_Using_Tmpl_");
      cmd.addParameter("OUT_1", tmpl_doc_no);
      cmd.addParameter("OUT_2", tmpl_doc_sheet);
      cmd.addParameter("OUT_3", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);

      trans = perform(trans);

      tmpl_doc_no = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_1");
      tmpl_doc_sheet = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_2");
      tmpl_doc_rev = trans.getValue("CREATENEWUSINGTMPL/DATA/OUT_3");

      trans.clear();

      cmd = trans.addCustomCommand("CHECKOUTEDMFILES", "Edm_File_Api.Check_Out_Master_File_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);
      cmd.addParameter("IN_STR1", ""); // No checkout path from non IE docs.

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", tmpl_doc_no);
      cmd.addParameter("IN_STR1", tmpl_doc_sheet);
      cmd.addParameter("IN_STR1", tmpl_doc_rev);
      cmd.addParameter("IN_STR1", master_doc_type);

      trans = perform(trans);

      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      trans.clear();

      if (DEBUG) Util.debug(this+": createNewDocumentNonIETmpl() (using template) }");

      //Bug 60698, Start, Removed the code added by 59564
      //Bug 59564, Start, Added full path to the file name
      return getFileFromArchiveNonIe( addKeys(edm_rep_info,doc_class,tmpl_doc_no,tmpl_doc_sheet,tmpl_doc_rev,master_doc_type));//Bug Id 58724
      //Bug 59564, End
      //Bug 60698, End
   }


   public void copyFileInRepository(String src_doc_class,
                                    String src_doc_no,
                                    String src_doc_sheet,
                                    String src_doc_rev,
                                    String dst_doc_class,
                                    String dst_doc_no,
                                    String dst_doc_sheet,
                                    String dst_doc_rev) throws FndException
   {
      String src_doc_type = "ORIGINAL";
      String dst_doc_type = "ORIGINAL";

      //Bug Id 69087, Start      
      String src_edm_rep_info;
      String src_rep_type;
      String src_rep_address;
      String src_rep_user;
      String src_rep_password;
      String src_rep_port;      
      //Bug Id 69087, End

      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ASPTransactionBuffer trans4 = mgr.newASPTransactionBuffer();   //Bug Id 69087

      trans.clear();

      cmd = trans.addCustomFunction("SRCEDMINFO", "EDM_FILE_API.get_edm_information", "OUT_1");
      cmd.addParameter("IN_STR1", src_doc_class);
      cmd.addParameter("IN_STR1", src_doc_no);
      cmd.addParameter("IN_STR1", src_doc_sheet);
      cmd.addParameter("IN_STR1", src_doc_rev);
      cmd.addParameter("IN_STR1", src_doc_type);

      cmd = trans.addCustomFunction("DSTEDMINFO", "EDM_FILE_API.get_edm_information", "OUT_1");
      cmd.addParameter("IN_STR1", dst_doc_class);
      cmd.addParameter("IN_STR1", dst_doc_no);
      cmd.addParameter("IN_STR1", dst_doc_sheet);
      cmd.addParameter("IN_STR1", dst_doc_rev);
      cmd.addParameter("IN_STR1", dst_doc_type);

      cmd = trans.addCustomFunction("DSTEDMREPINFO", "EDM_FILE_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", dst_doc_class);
      cmd.addParameter("IN_STR1", dst_doc_no);
      cmd.addParameter("IN_STR1", dst_doc_sheet);
      cmd.addParameter("IN_STR1", dst_doc_rev);
      cmd.addParameter("IN_STR1", dst_doc_type);

      trans = perform(trans);

      String src_edm_info = trans.getValue("SRCEDMINFO/DATA/OUT_1");

      String dst_edm__info = trans.getValue("DSTEDMINFO/DATA/OUT_1");
      String dst_edm_rep_info = trans.getValue("DSTEDMREPINFO/DATA/OUT_1");

      //
      // Get the source files from the repository
      // and put them back with the destination
      // file names
      //

      String dst_rep_type = getStringAttribute(dst_edm_rep_info, "LOCATION_TYPE");
      String dst_rep_address = getStringAttribute(dst_edm_rep_info, "LOCATION_ADDRESS");
      String dst_rep_user = getStringAttribute(dst_edm_rep_info, "LOCATION_USER");
      String dst_rep_password = decryptFtpPassword(getStringAttribute(dst_edm_rep_info, "LOCATION_PASSWORD"));
      String dst_rep_port = getStringAttribute(dst_edm_rep_info, "LOCATION_PORT");

      String doc_type_list = getAssociatedDocumentTypes(src_doc_class, src_doc_no, src_doc_sheet, src_doc_rev, src_doc_type);
      if ("TRUE".equals(getStringAttribute(dst_edm__info, "REDLINE_EXIST")))
      {
         doc_type_list += "REDLINE^";
      }
      if ("TRUE".equals(getStringAttribute(dst_edm__info, "VIEW_REF_EXIST")))
      {
         doc_type_list += "VIEW^";
      }
      
      // Modified by Terry 20120927
      // Original: String[] source_rep_file_names = getRepositoryFileNames(src_doc_class, src_doc_no, src_doc_sheet, src_doc_rev, doc_type_list);
      // Original: String[] dest_rep_file_names = getRepositoryFileNames(dst_doc_class, dst_doc_no, dst_doc_sheet, dst_doc_rev, doc_type_list);

      String[] source_rep_file_names = getRepositoryFileNamesFromServer(src_doc_class, src_doc_no, src_doc_sheet, src_doc_rev, doc_type_list);
      String[] dest_rep_file_names = getRepositoryFileNamesFromServer(dst_doc_class, dst_doc_no, dst_doc_sheet, dst_doc_rev, doc_type_list);
      
      // Modified end
      String source_files[];

      //Bug Id 69087, Start - Files maybe in different repositories
      int no_files = source_rep_file_names.length;
      
      // Modified by Terry 20120927
      // Original: String local_file_names[] = this.generateLocalFileNames(getRandomFilename(),source_rep_file_names);
      String local_file_names[] = new String[no_files];
      for (int i = 0; i < no_files; i++)
      {
         local_file_names[i] = generateLocalFileName(getRandomFilename(), source_rep_file_names[i]);
      }
      // Modified end
      
      String filtered_local_file_names = "";
      boolean db_to_db = false;
      boolean add_local_file = true;
      char rec_sep = 30;

      trans4.clear();

      //Bug Id 69087 - Added this to accomodate file_no used for transmitals in app75. 
      //               Only for a single comment file at a time now. May Require changes in the furture
      // Modified by Terry 20120927
      // Original:
      // String InFileNo;
      // if (!mgr.isEmpty(this.file_no))
      //    InFileNo = file_no;
      // else
      //    InFileNo = null;
      
      // for (int i = 0;i<no_files;i++)
      // {
      //    cmd = trans4.addCustomFunction("EDMREPINFO_" + i, "Edm_File_Api.Get_Edm_Repository_Info4", "OUT_1");
      cmd = trans4.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info4", "OUT_1");
      cmd.addParameter("IN_STR1", src_doc_class);
      cmd.addParameter("IN_STR1", src_doc_no);
      cmd.addParameter("IN_STR1", src_doc_sheet);
      cmd.addParameter("IN_STR1", src_doc_rev);
      // cmd.addParameter("IN_STR1", this.selected_doc_types[i]);
      cmd.addParameter("IN_STR1", this.selected_doc_types[0]);
         // Modified by Terry 20120927
         // Original:
         // cmd.addParameter("IN_NUM", InFileNo);
         // Modified end
      // }
      // Modified end

      trans4 = perform(trans4);

      // Modified by Terry 20120927
      src_edm_rep_info = trans4.getValue("EDMREPINFO/DATA/OUT_1");
      
      for (int i = 0; i < no_files; i++)
      {
         // Original: src_edm_rep_info = trans4.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
         src_rep_type = getStringAttribute(src_edm_rep_info, "LOCATION_TYPE");
         src_rep_address = getStringAttribute(src_edm_rep_info, "LOCATION_ADDRESS");
         src_rep_user = getStringAttribute(src_edm_rep_info, "LOCATION_USER");
         src_rep_password = decryptFtpPassword(getStringAttribute(src_edm_rep_info, "LOCATION_PASSWORD"));
         src_rep_port = getStringAttribute(src_edm_rep_info, "LOCATION_PORT");

         if ("2".equals(src_rep_type))
            getSingleFileFromFtpRepository(src_rep_address, src_rep_port, src_rep_user, src_rep_password, source_rep_file_names[i],local_file_names[i]);
         else if ("3".equals(src_rep_type)) {
            if ("3".equals(dst_rep_type)) {
               db_to_db = true;
               add_local_file = false;
            }
            else
               getSingleFileFromDatabaseRepository(src_doc_class, src_doc_no, src_doc_sheet, src_doc_rev,local_file_names[i], selected_doc_types[i]);
         }
         else
         {
            // Docmaw currently does not support shared file locations.
            throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILE1: Can't get file from Shared Repository. Shared Repositories are currently not supported by Docmaw."));
         }

         if (add_local_file) 
            filtered_local_file_names = filtered_local_file_names + rec_sep + local_file_names[i];            
         else
            add_local_file = true;
      }

      //Bug Id 69087 - Copy all database files in source to destination regardless of type.
      if (db_to_db) {
	     trans.clear();
	     cmd = trans.addCustomCommand("COPYFILES", "EDM_FILE_STORAGE_API.Copy_Files_");
	     cmd.addParameter("IN_1", src_doc_class);
	     cmd.addParameter("IN_1", src_doc_no);
	     cmd.addParameter("IN_1", src_doc_sheet);
	     cmd.addParameter("IN_1", src_doc_rev);
	     cmd.addParameter("IN_1", dst_doc_class);
	     cmd.addParameter("IN_1", dst_doc_no);
	     cmd.addParameter("IN_1", dst_doc_sheet);
	     cmd.addParameter("IN_1", dst_doc_rev);

	     trans = mgr.perform(trans);
      }

      //Bug Id 69087 - Get the filtered files which exclude those copied from db to db above.
      StringTokenizer st = new StringTokenizer(filtered_local_file_names, "" + rec_sep);
      int no_local_files = st.countTokens();
      String[] new_local_file_names = new String[no_local_files];

      for (int j = 0;j < no_local_files; j++) {
         new_local_file_names[j] = st.nextToken();
      }

      //Bug Id 69087 - Since all files go into a single repository no need to use the new methods
      if (no_local_files > 0) {
         if ("2".equals(dst_rep_type))
         {  
            putFileToFtpRepository(dst_rep_address, dst_rep_port, dst_rep_user, dst_rep_password, new_local_file_names, dest_rep_file_names);
         }
         else if ("3".equals(dst_rep_type))
         {
            putFileToDatabaseRepository(dst_doc_class, dst_doc_no,dst_doc_sheet,dst_doc_rev,new_local_file_names);
         }
      else
      {
         // Docmaw currently does not support shared file locations.
            throw new FndException(mgr.translate("DOCMAWDOCSRVPUTSHAREFILEPUT: Can't put file to Shared Repository. Shared Repositories are currently not supported by Docmaw."));
         }
      }
      //Bug Id 69087, End
   }

   public void deleteDocumentFile(String doc_class,
                                  String doc_no,
                                  String doc_sheet,
                                  String doc_rev,
                                  String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String edm_info = trans.getValue("EDMINFO/DATA/OUT_1");
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      if ("ORIGINAL".equals(doc_type))
      {
         if (getStringAttribute(edm_info, "VIEW_REF_EXIST").equals("TRUE"))
         {
             //Bug 59843, Start
             String sOriginalType = getEdmFileType(doc_class,doc_no,doc_sheet,doc_rev,"ORIGINAL");
             String sViewType = getEdmFileType(doc_class,doc_no,doc_sheet,doc_rev,"VIEW");

             if (sOriginalType.equals(sViewType))
                 doc_type_list = Str.replace(doc_type_list,"ORIGINAL","VIEW");
             else
                 doc_type_list = "VIEW^" + doc_type_list;
             //Bug 59843, End
         }

         if (getStringAttribute(edm_info, "REDLINE_EXIST").equals("TRUE"))
         {
            doc_type_list = "REDLINE^" + doc_type_list;
         }
      }

      String[] repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);//Bug Id 65997

      deleteFilesInRepository(addKeys(edm_rep_info,doc_class, doc_no, doc_sheet, doc_rev), repository_file_names); //Bug Id 69087

      // delete all file references

      trans.clear();
      cmd = trans.addCustomCommand("DELFILEREFS", "Edm_File_Api.Delete_File_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);

      if (DEBUG) Util.debug(this+": deleteDocumentFile() }");
   }

   public void deleteDocument(String doc_class,
                              String doc_no,
                              String doc_sheet,
                              String doc_rev) throws FndException
   {
      if (DEBUG) Util.debug(this+": deleteDocument() {");

      ASPManager mgr = getASPManager();

      String doc_type = "ORIGINAL";

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String edm_info = trans.getValue("EDMINFO/DATA/OUT_1");
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      trans.clear();
      if (getStringAttribute(edm_info, "ORIGINAL_REF_EXIST").equals("TRUE"))
      {
         String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

         if (getStringAttribute(edm_info, "VIEW_REF_EXIST").equals("TRUE"))
         {
            doc_type_list = "VIEW^" + doc_type_list;
         }

         if (getStringAttribute(edm_info, "REDLINE_EXIST").equals("TRUE"))
         {
            doc_type_list = "REDLINE^" + doc_type_list;
         }

         String[] repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);//Bug Id 65997

         deleteFilesInRepository(addKeys(edm_rep_info,doc_class, doc_no, doc_sheet, doc_rev), repository_file_names); //Bug Id 69087
         

         trans.clear();
         cmd = trans.addCustomCommand("DELFILEREFS", "Edm_File_Api.Delete_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", doc_type);
      }

      cmd = trans.addCustomCommand("DELDOCREF", "Doc_Issue_Api.Delete_Document_Issue_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);

      trans = perform(trans);

      if (DEBUG) Util.debug(this+": deleteDocument() }");
   }
   
   // Added by Terry 20120918
   // Delete selected file
   public void deleteSelectDocumentFile(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_nos) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.Get_Edm_Information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String edm_info = trans.getValue("EDMINFO/DATA/OUT_1");
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      if ("ORIGINAL".equals(doc_type))
      {
         if (getStringAttribute(edm_info, "VIEW_REF_EXIST").equals("TRUE"))
         {
            // Bug 59843, Start
            String sOriginalType = getEdmFileType(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL");
            String sViewType = getEdmFileType(doc_class, doc_no, doc_sheet, doc_rev, "VIEW");

            if (sOriginalType.equals(sViewType))
               doc_type_list = Str.replace(doc_type_list, "ORIGINAL", "VIEW");
            else
               doc_type_list = "VIEW^" + doc_type_list;
            // Bug 59843, End
         }

         if (getStringAttribute(edm_info, "REDLINE_EXIST").equals("TRUE"))
         {
            doc_type_list = "REDLINE^" + doc_type_list;
         }
      }

      String[] repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list, file_nos);// Bug Id 65997

      deleteFilesInRepository(addKeys(edm_rep_info, doc_class, doc_no, doc_sheet, doc_rev), repository_file_names); // Bug Id 69087

      // delete all file references

      trans.clear();
      
      StringTokenizer st = new StringTokenizer(file_nos, "^");
      int no_of_files = st.countTokens();
      for (int i = 0; i < no_of_files; i++)
      {
         cmd = trans.addCustomCommand("DELFILEREFS" + i, "Edm_File_Api.Delete_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", doc_type);
         cmd.addParameter("IN_STR1", st.nextToken());
      }
      
      trans = perform(trans);

      if (DEBUG)
         Util.debug(this + ": deleteDocumentFile() }");
   }
   // Added end


   public void undoCheckOut(String doc_class,
                            String doc_no,
                            String doc_sheet,
                            String doc_rev,
                            String master_doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);

      trans = mgr.newASPTransactionBuffer();

      if ("TRUE".equals(getStringAttribute(edm_info, "EXIST_IN_REPOSITORY")))
      {
         cmd = trans.addCustomCommand("UNDOCHECKOUT", "Edm_File_Api.Undo_Check_Out_Master_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", master_doc_type);
         // Added by Terry 20120925
         // Add file no parameter
         if (!mgr.isEmpty(this.file_no))
            cmd.addParameter("IN_NUM", this.file_no);
         // Added end
      }
      else
      {
         //
         // If the user invoked 'Undo Check Out' soon after
         // creating a new document and before checking them
         // in at least once, we need to delete the edm file
         // refs
         //

         cmd = trans.addCustomCommand("DELFILEREFS", "Edm_File_Api.Delete_File_");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", master_doc_type);
         // Added by Terry 20120925
         // Add file no parameter
         if (!mgr.isEmpty(this.file_no))
            cmd.addParameter("IN_NUM", this.file_no);
         // Added end
      }

      trans = perform(trans);
   }



   public String getCopy(String doc_class,
                         String doc_no,
                         String doc_sheet,
                         String doc_rev,
                         String master_doc_type) throws FndException
   {
      return getCopy(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type, false, false);
   }


   public String getCopy(String doc_class,
                         String doc_no,
                         String doc_sheet,
                         String doc_rev,
                         String master_doc_type,
                         boolean redline,
                         boolean view_copy) throws FndException
   {
      return getCopy(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type, redline, view_copy,false);
   }



   public String getCopy(String doc_class,
                         String doc_no,
                         String doc_sheet,
                         String doc_rev,
                         String master_doc_type,
                         boolean redline,
                         boolean view_copy,
                         boolean removeRepeatitions) throws FndException
   {
      ASPManager mgr = getASPManager();
      Util.debug(this+": ::::::::::::::::::  getCopy() {");
      Util.debug(this+": ::::::::::::::::::  master_doc_type = "+master_doc_type);
      Util.debug(this+": ::::::::::::::::::  file_no = " + file_no);



      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);
      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);
      String doc_type_list = getAssociatedDocumentTypes(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);

      if ("ORIGINAL".equals(master_doc_type))
      {
         if (redline)
            doc_type_list ="REDLINE^" + doc_type_list;
         if (view_copy)
            doc_type_list ="VIEW^" + doc_type_list;
      }

      String[] repository_file_names = getRepositoryFileNames(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);


      
      if (removeRepeatitions) {
         repository_file_names = removeRepeations(repository_file_names);
      }

      //bug 58326, starts....
      String unique_file_id = getRandomFilename();
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);

      getFilesFromRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev),local_file_names, repository_file_names); //Bug Id 69087
      Util.debug(this+": ::::::::::::::::::  getCopy() }");

      return unique_file_id;
      //bug 58326, ends....
   }

   //Bug Id 65997, Start

   public String getCopy(String doc_class,
                         String doc_no,
                         String doc_sheet,
                         String doc_rev,
                         String master_doc_type,
			 String process) throws FndException 
   {
      return getCopy(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type, process, false, false);
   }

   public String getCopy(String doc_class,
			 String doc_no,
			 String doc_sheet,
			 String doc_rev,
			 String master_doc_type,
			 String process,
			 boolean redline,
			 boolean view_copy) throws FndException 
   {
      ASPManager mgr = getASPManager();
      

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);
      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, master_doc_type);

      String doc_type_list;

      if ("REDLINE".equals(process)) 
         doc_type_list = getAssociatedDocumentTypes(doc_class,doc_no,doc_sheet,doc_rev,master_doc_type); //Bug Id 74956
      else
         doc_type_list = getAssociatedDocumentTypes(doc_class, master_doc_type, process);

      if ("ORIGINAL".equals(master_doc_type))
      {
         if (redline)
            doc_type_list ="REDLINE^" + doc_type_list;
         if (view_copy)
            doc_type_list ="VIEW^" + doc_type_list;
      }

      //Bug Id 68528, start
      String[] repository_file_names;
      if (("VIEW".equals(process)&& "REDLINE".equals(master_doc_type)) || ("EDIT".equals(process)&& "REDLINE".equals(master_doc_type))) 
      {
         repository_file_names = getRepositoryFileNames (doc_class,doc_no,doc_sheet,doc_rev,doc_type_list);
      }
      else
      {
         // Modified by Terry 20120920
         // Original: repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);
         if (!mgr.isEmpty(this.file_no))
            repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list, this.file_no + "^");
         else
            repository_file_names = getRepositoryFileNamesFromServer(doc_class, doc_no, doc_sheet, doc_rev, doc_type_list);
         // Modified end
      }
      
      //Bug Id 68528, end

      String unique_file_id = getRandomFilename();
      String[] local_file_names = generateLocalFileNames(unique_file_id, repository_file_names);
      getFilesFromRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev),local_file_names, repository_file_names); //Bug Id 69087
      return unique_file_id;
   }

   //Bug Id 65997, End


   //To get the doc_rev for publish document in WEB site
   public String getRevision(String doc_class,
                             String doc_no,
                             String doc_sheet,
                             String latestRev,
                             String released) throws FndException
    {
      ASPManager mgr = getASPManager();
      String doc_rev;
      trans = mgr.newASPTransactionBuffer();

      if (DEBUG) Util.debug(this+": checkInNonIe() {");

      if ("TRUE".equals(latestRev))
      {
         cmd = trans.addCustomFunction("LAST_REV", "Doc_Issue_Api.Latest_Revision", "OUT_1" );
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         trans = perform(trans);
         doc_rev = trans.getValue("LAST_REV/DATA/OUT_1");
         trans.clear();
      }
      else if ("TRUE".equals(released))
      {
         cmd = trans.addCustomFunction("REV", "Doc_Issue_Api.Get_Released_Revision", "OUT_1" );
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         trans = perform(trans);
         doc_rev = trans.getValue("REV/DATA/OUT_1");
         if (Str.isEmpty(doc_rev))
            throw new FndException(mgr.translate("DOCMAWDOCSRVINVALIDNORELEASEREV: This document does not have a released revision."));
      }
      else
         throw new FndException(mgr.translate("DOCMAWDOCSRVINVALIDRELEASELATESTREV: Invalid value for argument LATEST_REV or RELEASED"));

      return doc_rev;

    }


   public String getCopyNonIe(String doc_class,
                              String doc_no,
                              String doc_sheet,
                              String doc_rev,
                              String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      if ("FALSE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREF2: There is no file-reference to the document with Doc No. &1 and Doc Type &2.", doc_no, doc_type));
      }
      if ("FALSE".equals(getStringAttribute(edm_info, "VIEW_ACCESS")))
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVNOVIEWACCESS: You don't have sufficient rigths to view the document with Doc No. &1 and Doc Type &2.", doc_no, doc_type));
      }

      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      //Bug 60698, Start, Removed the code added by 59564
      //Bug 59564, Start, Added full path to the file name
      return getFileFromArchiveNonIe( addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev,doc_type));
      //Bug 59564, End
      //Bug 60698, End
   }



   //bug 58326, starts....
   public String createXmlFile(String doc_class,
                               String doc_no,
                               String doc_sheet,
                               String doc_rev,
                               String doc_type,
                               String doc_title,
                               String file_type,
                               String local_file_name,
                               String action,
                               String main_function,
                               String file_action,
                               String process,
                               String file_id,
			       String path) throws FndException
   {

      return createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, local_file_name,
                           null, action, main_function, file_action, process, file_id, path);

   }
   //bug 58326, ends....

   //bug 58326, modified String full_temp_ftp_file_id into file_id
   public String createXmlFile(String doc_class,
                               String doc_no,
                               String doc_sheet,
                               String doc_rev,
                               String doc_type,
                               String doc_title,
                               String file_type,
                               String local_file_name,
                               String delete_file,
                               String action,
                               String main_function,
                               String file_action,
                               String process,
                               String file_id,
			       String path) throws FndException
   {        
      ASPManager mgr = getASPManager();
      
      trans = mgr.newASPTransactionBuffer();
      
      if (DEBUG) Util.debug(this+ "debug get path $$$$$$$$$$$$$$$$$$" + path);
      
      //Bug 58792, Start
      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      //Bug 58792, End
      
      //
      // Get a list of file types and their extensions..
      // (Used when displaying the File Chooser Dialog.
      // The only process that needs this is "CHECKIN")
      //
      
      String file_filter = "";
      if ("CHECKIN".equals(file_action) || "CREATENEWDOC".equals(file_action) || "FILEIMPORT".equals(file_action))
      {
         // Get count of possible file types..
         trans.clear();
         ASPQuery query = trans.addQuery("GETFILETYPESCOUNT", "SELECT count(file_type) FILE_TYPE_COUNT FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW')");
         trans = mgr.perform(trans);
         int count = Integer.parseInt(trans.getValue("GETFILETYPESCOUNT/DATA/FILE_TYPE_COUNT"));
         
         // Fetch all available file types..
         trans.clear();
         query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW')");
         query.setBufferSize(count);
         trans = perform(trans);
         
         ASPBuffer buf = trans.getBuffer("GETORIGINALFILETYPES");
         int rows = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
         for (int i = 0; i < rows; i++)
         {
            file_filter += buf.getBufferAt(i).getValueAt(0) + "^" + buf.getBufferAt(i).getValueAt(1) + "^";
         }
      }
      
      // Added by Terry 20120920
      // Get user checkout edm_file count, and send it to OCX
      String user_checkout_count = "";
      if ("CHECKIN".equals(file_action))
      {
         trans.clear();
         ASPCommand cmd = trans.addCustomFunction("GETCHECKOUTCOUNT", "Edm_File_API.Get_User_Checkout_Count", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         trans = perform(trans);
         user_checkout_count = trans.getValue("GETCHECKOUTCOUNT/DATA/OUT_1");
      }
      // Added end
      
      // Added by Terry 20140730
      // Get doc_folder and transfer to OCX
      String doc_folder_ = mgr.isEmpty(doc_folder) ? getDocCode(doc_class, doc_no, doc_sheet, doc_rev) : doc_folder;
      // Added end
      
      //
      // Fetch the macro code if a macro was selected
      // by the user..
      //
      
      String macro_code = "";
      String macro_lng = "";
      //Bug 58421, Start
      String macro_timeout = "";
      //Bug 58421, End
      if (!Str.isEmpty(action))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETMACROCODE", "Edm_Macro_API.Create_Code", "OUT_1");
         cmd.addParameter("IN_STR1", action);
         cmd.addParameter("IN_STR1", file_type);
         
         if ("CHECKOUTALREADY".equals(process))
            cmd.addParameter("IN_STR1", "CHECKOUT");
         else
            cmd.addParameter("IN_STR1", process);
         
         cmd = trans.addCustomFunction("GETMACROLNG", "Edm_Macro_API.Get_Macro_Language", "OUT_2");
         cmd.addParameter("IN_STR1", action);
         cmd.addParameter("IN_STR1", file_type);
         cmd.addParameter("IN_STR1", process);
         
         // Bug 58421 ,Start
         cmd = trans.addCustomFunction("MACROTIMEOUT","Edm_Macro_API.Get_Timeout","OUT_2");
         cmd.addParameter("IN_STR1", action);
         cmd.addParameter("IN_STR1", file_type);
         cmd.addParameter("IN_STR1", process);
         // Bug 58421, End
         
         trans = perform(trans);
         macro_code = trans.getValue("GETMACROCODE/DATA/OUT_1");
         macro_lng = trans.getValue("GETMACROLNG/DATA/OUT_2");
         //Bug 58421, Start
         macro_timeout = trans.getValue("MACROTIMEOUT/DATA/OUT_2");
         //Bug 58421, End
         
         if (Str.isEmpty(macro_code))
            macro_code = "";
      }      
      
      //bug 58326, starts....
      String full_secondary_ftp_original_file_name = "";
      String unique_file_name = "";
      if ("REDLINE".equals(doc_type))
      {
         
         if (!"CHECKIN".equals(file_action) && !"DELETE".equals(file_action) && !"ADDCOMMENT".equals(file_action) && !"VIEWCOMMENT".equals(file_action) && !"SENDCOMMENT".equals(file_action))
         {
            unique_file_name = getCopy(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL", "REDLINE");//Bug Id 65997
         }         
      }
      //bug 58326, ends....      
      
      //
      // Fetch attributes of the current document. These
      // attributes will be stored in the clients registry.
      //      
      
      String document_data = getDocumentData(doc_class, doc_no, doc_sheet, doc_rev);      
      
      structure_doc = isStructureDocument(doc_class, doc_no); //Bug id 57041
      
      //
      // Now build an xml document containing all necessary data
      // for the ocx to work on..
      //      
      try 
      {
         Document doc = new DocumentImpl();
         CDATASectionImpl myCDATANode;
         
         // Create Root Element
         Element root = doc.createElement("File");
         Element item, child;
         
         root.setAttribute("doc_briefcase",          docBriefcase);
         root.setAttribute("current_doc_briefcase",  currentDocBriefcase);
         root.setAttribute("viewcopy_need",          viewCopyNeeded + "");
         root.setAttribute("file_exts_tocheckin",    checkin_file_exts );
         root.setAttribute("doc_class",              toBase64Text(mgr.isEmpty(doc_class) ? "" : doc_class));
         root.setAttribute("doc_no",                 toBase64Text(mgr.isEmpty(doc_no) ? "" : doc_no));
         root.setAttribute("doc_sheet",              toBase64Text(mgr.isEmpty(doc_sheet) ? "" : doc_sheet));
         root.setAttribute("doc_rev",                toBase64Text(mgr.isEmpty(doc_rev) ? "" : doc_rev));
         /*if (viewComment) {
            root.setAttribute("doc_type",            toBase64Text("ORIGINAL"));
         }else*/
         root.setAttribute("doc_type",               toBase64Text(mgr.isEmpty(doc_type) ? "" : doc_type));
         //not sure if we need this information for ActiveX.  let us c :)
         //if (!mgr.isEmpty(this.file_no)) {
         //root.setAttribute("file_no",               toBase64Text(this.file_no));
         //}
         root.setAttribute("file_no",                toBase64Text(mgr.isEmpty(this.file_no) ? "" : this.file_no));         
         
         root.setAttribute("local_file_name",        toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
         root.setAttribute("delete_file",            toBase64Text(mgr.isEmpty(delete_file) ? "" : delete_file));
         root.setAttribute("action",                 action);
         root.setAttribute("main_function",          main_function);
         root.setAttribute("file_type",              file_type);
         
         String processForActiveX = "";
         if ("ADDCOMMENT".equals(file_action))
         {
            processForActiveX = "CHECKIN";
         }
         else if ("SENDCOMMENT".equals(file_action))
         {
            processForActiveX = "SENDMAIL ";
         }
         else
            processForActiveX = file_action;
         
         root.setAttribute("process",                processForActiveX);
         root.setAttribute("script_language_type", macro_lng);
         //Bug 58421, Start
         root.setAttribute("timeout",                mgr.isEmpty(macro_timeout)?"0":macro_timeout);
         //Bug 58421, End
         root.setAttribute("same_action_to_all",     mgr.isEmpty(sameActionToAll)?"":sameActionToAll);
         root.setAttribute("launch_file",            mgr.isEmpty(launchFile) ? "" :launchFile);
         root.setAttribute("copy_to_dir_path",       toBase64Text(mgr.isEmpty(copyToDirPath) ? "" : copyToDirPath));
         root.setAttribute("imported_path",          toBase64Text(mgr.isEmpty(imported_path)?"":imported_path));
         root.setAttribute("use_original_file_name", toBase64Text((use_original_file_name) ? "TRUE":"FALSE"));
         root.setAttribute("macro_attributes",       toBase64Text(mgr.isEmpty(macro_attributes) ? "" : macro_attributes));
         //Bug Id 57041 start...
         root.setAttribute("structure_doc",          toBase64Text((structure_doc) ? "TRUE":"FALSE"));
         root.setAttribute("check_in_path",          toBase64Text(mgr.isEmpty(checkInPath) ? "" : checkInPath));
         //Bug Id 57041 End..
         
         // Added by Terry 20120920
         // Send user checkout edm_file count to OCX
         root.setAttribute("user_checkout_count",    toBase64Text(mgr.isEmpty(user_checkout_count) ? "" : user_checkout_count));
         root.setAttribute("doc_folder",             toBase64Text(mgr.isEmpty(doc_folder_) ? "" : doc_folder_));
         
         // Added end
         
         // Child element 0
         item = doc.createElement("TempRepositoryInformation");
         //bug 58326, starts....
         item.setAttribute("temp_location_file_id", file_id);
         item.setAttribute("temp_location_original_filename", unique_file_name);
         //bug 58326, ends....
         root.appendChild(item);
         
         // Child element 1
         item = doc.createElement("DocumentData");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(unicode2Byte(document_data)));
         item.appendChild(myCDATANode);
         root.appendChild(item);
         
         // Child element 2
         item = doc.createElement("MacroCode");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(unicode2Byte(macro_code)));
         item.appendChild(myCDATANode);
         root.appendChild(item);
         
         //Bug id 74523 Start
         String inName = "";
         String nameType = "";
         boolean edminfo = false;
         String substr1 = "";
         String substr2 = "";
         String namenew  = "";
         String row_name = "";
         String transmit = "\\Document Transmittal\\";//**
         boolean transmittal = false;
         boolean changed = false;         
         
         //Transmittals file name inclueds the path as well
         if((!mgr.isEmpty(local_file_name)) && local_file_name.indexOf(transmit) >-1)
         {
            transmittal = true;
            path = local_file_name.substring(0,local_file_name.lastIndexOf("\\")+1);
            
            row_name = local_file_name.substring(local_file_name.lastIndexOf("\\")+1);
            
            local_file_name = path + adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path, row_name );
            root.setAttribute("local_file_name",toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
            
            if (DEBUG) Util.debug("debug:local_file_name "+ local_file_name);
         }
         else
         {            
            if(!file_action.equals("CREATENEWDOC"))
            {
               if(!file_action.equals("ACCEPT"))
               {
                  if(!processForActiveX.equals("FILEIMPORT"))
                  {
                     if(!file_action.equals("ADDCOMMENT"))
                     {
                        if(!structure_doc || doc_type.equals("REDLINE") || doc_type.equals("VIEW"))
                        {
                           if(processForActiveX.equals("VIEW") || processForActiveX.equals("PRINT") || processForActiveX.equals("VIEWCOMMENT"))
                           {
                              path+="temp\\";
                              
                              if(mgr.isEmpty(local_file_name))
                              {
                                 if(doc_type.equals("REDLINE"))
                                 {
                                    if(use_original_file_name)
                                    {
                                       inName = getStringAttribute(edm_info,"USER_FILE_NAME");
                                       changed = true;
                                       edminfo = true;
                                       nameType = "USER_FILE_NAME=";
                                    }
                                    else
                                    {
                                       inName = getStringAttribute(edm_rep_info,"COPY_OF_FILE_NAME");
                                       changed=true;
                                       nameType = "COPY_OF_FILE_NAME=";
                                    }
                                 }
                                 else
                                 {
                                    if(use_original_file_name)
                                    {
                                       inName = getStringAttribute(edm_rep_info,"USER_FILE_NAME");
                                       changed=true;
                                       nameType = "USER_FILE_NAME=";
                                    }
                                    else
                                    {
                                       inName = getStringAttribute(edm_rep_info,"COPY_OF_FILE_NAME");
                                       changed=true;
                                       nameType = "COPY_OF_FILE_NAME=";
                                    }                                    
                                 }
                              }
                              else
                              {
                                 local_file_name = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path,local_file_name);
                                 root.setAttribute("local_file_name",toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
                              }                              
                           }
                           else if(processForActiveX.equals("SENDMAIL")|| processForActiveX.equals("VIEWWITHEXTVIEWER")||processForActiveX.equals("GETCOPYTODIR"))
                           {
                              path+="temp\\";
                              if(mgr.isEmpty(local_file_name))
                              {
                                 inName = getStringAttribute(edm_rep_info,"COPY_OF_FILE_NAME");
                                 changed=true;
                                 nameType = "COPY_OF_FILE_NAME=";
                              }
                              else
                              {
                                 local_file_name = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path,local_file_name);
                                 root.setAttribute("local_file_name",toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
                              }
                              
                           }
                           else if(processForActiveX.equals("CHECKOUT") || processForActiveX.equals("ALREADYCHECKEDOUT") || processForActiveX.equals("CREATENEW")|| processForActiveX.equals("EXPORT_BRIEFCASE") || processForActiveX.equals("DELETE")|| processForActiveX.equals("UNDOCHECKOUT"))
                           {
                              if(doc_type.equals("REDLINE"))
                              {
                                 if(use_original_file_name)
                                 {
                                    inName = getStringAttribute(edm_rep_info,"LOCAL_FILE_NAME");
                                    changed=true;
                                    nameType = "LOCAL_FILE_NAME=";
                                 }
                                 else
                                 {
                                    inName = getStringAttribute(edm_rep_info,"REDLINE_FILE_NAME");
                                    nameType = "REDLINE_FILE_NAME=";
                                    namenew = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path, inName );
                                    
                                    substr1 = edm_rep_info.substring(0,edm_rep_info.indexOf(nameType));
                                    substr2 = edm_rep_info.substring((edm_rep_info.indexOf(nameType+inName)+inName.length()+nameType.length()));
                                    edm_rep_info = substr1 + nameType + namenew + substr2;
                                    
                                    inName = getStringAttribute(edm_rep_info,"LOCAL_FILE_NAME");
                                    changed=true;
                                    nameType = "LOCAL_FILE_NAME=";
                                 }
                              }
                              else
                              {
                                 if(use_original_file_name)
                                 {
                                    inName = getStringAttribute(edm_rep_info,"USER_FILE_NAME");
                                    changed=true;
                                    nameType = "USER_FILE_NAME=";
                                 }
                                 else
                                 {
                                    inName = getStringAttribute(edm_rep_info,"LOCAL_FILE_NAME");
                                    changed=true;
                                    nameType = "LOCAL_FILE_NAME=";
                                 }
                              }                              
                           }                           
                           else if(processForActiveX.equals("CHECKIN"))
                           {
                              String file_ext = getStringAttribute(edm_info, "ORIGINAL_REF_EXIST");
                              if(doc_type.equals("REDLINE"))
                              {                                 
                                 inName = getStringAttribute(edm_rep_info,"LOCAL_FILE_NAME");
                                 changed=true;
                                 nameType = "LOCAL_FILE_NAME=";
                              }                              
                              else if(file_ext.equals("TRUE"))
                              {                                 
                                 if(!mgr.isEmpty(local_file_name))
                                 {
                                    local_file_name = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path,local_file_name);
                                    root.setAttribute("local_file_name",toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
                                 }
                                 else
                                 {
                                    inName = getStringAttribute(edm_rep_info,"LOCAL_FILE_NAME");
                                    changed=true;
                                    nameType = "LOCAL_FILE_NAME=";
                                 }
                              }
                           }
                           else //used for transmittal comment send mail
                           {
                              path+="temp\\";
                              if(mgr.isEmpty(local_file_name))
                              {
                                 inName = getStringAttribute(edm_rep_info,"COPY_OF_FILE_NAME");
                                 changed=true;
                                 nameType = "COPY_OF_FILE_NAME=";
                              }
                              else
                              {
                                 local_file_name = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path,local_file_name);
                                 root.setAttribute("local_file_name",toBase64Text(mgr.isEmpty(local_file_name) ? "" : local_file_name));
                              }
                           }                          
                           
                           if(changed)
                           {
                              if(mgr.isEmpty(file_no))
                              {
                                 file_no = "1";
                              }
                              namenew = adjustFileName(doc_class,doc_no, doc_sheet,doc_rev,file_no,path, inName );
                              
                              if(edminfo)
                              {
                                 substr1 = edm_info.substring(0,edm_info.indexOf(nameType));
                                 substr2 = edm_info.substring((edm_info.indexOf(nameType+inName)+inName.length()+nameType.length()));
                                 edm_info = substr1 + nameType + namenew + substr2;
                              }
                              else
                              {
                                 substr1 = edm_rep_info.substring(0,edm_rep_info.indexOf(nameType));
                                 substr2 = edm_rep_info.substring((edm_rep_info.indexOf(nameType+inName)+inName.length()+nameType.length()));
                                 edm_rep_info = substr1 + nameType + namenew + substr2;
                              }                              
                           }                           
                        }//if it's not structure
                     }// not a file import
                  }//not addcomment
               }//not ACCEPT
            }//create new document            
         }//transmittal
         //Bug id 74523 End
         
         // Child element 3
         item = doc.createElement("EdmInformation");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(unicode2Byte(edm_info)));
         item.appendChild(myCDATANode);
         root.appendChild(item);
         
         // Child element 4
         item = doc.createElement("EdmRepositoryInformation");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(unicode2Byte(edm_rep_info)));
         item.appendChild(myCDATANode);
         root.appendChild(item);         
         
         // Child element 5
         item = doc.createElement("OriginalFileExtensions");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(unicode2Byte(file_filter)));
         item.appendChild(myCDATANode);
         root.appendChild(item);         
         
         // Child element 6
         //Bug Id 65997, Start
         String configProcess;
         if ("GETCOPYTODIR".equals(file_action))
         {
            if (checkValidViewCopy(doc_class, doc_no, doc_sheet, doc_rev) )
               configProcess = "COPYNOTVIEW";
            else
               configProcess = "COPY";            
         }
         else if ("SENDMAIL".equals(file_action))
         {
            if (checkValidViewCopy(doc_class, doc_no, doc_sheet, doc_rev) )
               configProcess = "SENDORIGINAL";
            else
               configProcess = process;
         }
         //Bug 71814, Start
         else if ("FILEIMPORT".equals(file_action))
         {
            configProcess = "CHECKIN";
         }
         //Bug 71814, End
         // Bug ID 89622, Start
         else if ("ADDCOMMENT".equals(file_action))
         {
            configProcess = "ADDTRANSMITTALCOMMENT";
         }
         // Bug ID 89622, Finish
         else if ("REDLINE".equals(doc_type) && !"CHECKIN".equals(file_action) && !"VIEWCOMMENT".equals(file_action) && !"SENDCOMMENT".equals(file_action))  //Bug Id 68528,71762,89622
         {
            configProcess = "REDLINEACTION";
         }
         //Bug Id 65997, End
         else
            configProcess = process;
         
         String doc_class_process_config = getDocClassProcessConfiguration(doc_class, configProcess);         
         if (modifyDocClassProcessConfig)
         {         
            if ("SENDCOMMENT".equals(file_action))//Bug Id 68528, 89622
            { 
               // replace file type for redline.
               doc_class_process_config = replaceRedlineExt(doc_class_process_config,"PDF");
            }            
         }
         
         item = doc.createElement("DocClassProcessActionConfig");
         myCDATANode = (CDATASectionImpl)doc.createCDATASection(Util.toBase64Text(doc_class_process_config.getBytes()));
         item.appendChild(myCDATANode);
         root.appendChild(item);
         
         root.appendChild(item);
         
         // Add Root to Document
         doc.appendChild(root);
         
         OutputFormat format = new OutputFormat(doc);
         StringWriter stringOut = new StringWriter();
         XMLSerializer serial = new XMLSerializer(stringOut, format);
         
         serial.asDOMSerializer();
         serial.serialize(doc.getDocumentElement());
         
         String xmlHtml;
         xmlHtml = stringOut.toString();         
         
         String xmlFileName = getTmpPath() + getRandomFilename();
         XMLUtil.saveToFile(doc, xmlFileName);
         
         if (DEBUG) Util.debug(this+": createXmlFile() }");
            return xmlFileName;
         
      }
      catch (Throwable tr)
      {
         if (DEBUG)
            tr.printStackTrace();
         
         throw new FndException(mgr.translate("DOCMAWDOCSRVCREATEXML: An error occured while creating the XML document:") + tr.toString());
      }
   }

   private String getDocClassProcessConfiguration(String doc_class, String process) throws FndException
   {
      if (DEBUG) Util.debug(this+ ": getDocumentTypeConfiguration {");

      ASPManager mgr = getASPManager();
      ASPCommand cmd;

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETDOCCLASSPROCESSCONFIG", "Doc_Class_Proc_Action_Line_Api.Get_Process_Config_Info_", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", process);
      trans = perform(trans);

      return trans.getValue("GETDOCCLASSPROCESSCONFIG/DATA/OUT_1");
   }

   /**
    * This method returns the doc_type of a passed file extension
    * <p>
    * This method makes 1 database call per method call.
    * @param   file_ext  A file extension.
    * @return  a string containing the doc type
    * @see
    */

   private String getDocTypeFromFileExt(String file_ext) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      trans.clear();

      cmd = trans.addCustomFunction("GETDOCTYPEFROMEXTENSION", "EDM_APPLICATION_API.Get_Doc_Type_From_Ext", "OUT_1");
      cmd.addInParameter("IN_STR1", file_ext);

      trans = perform(trans);

      return trans.getValue("GETDOCTYPEFROMEXTENSION/DATA/OUT_1");
   }

   /**
    * This method returns the doc_type of a passed file name
    * <p>
    * This method makes 1 database call per method call and
    * uses the method getDocTypeFromFileExt
    * @param   file_ext  A file name
    * @return  a string containing the doc type
    * @see  getDocTypeFromFileExt
    */

   private String getDocTypeFromFileName(String file_name) throws FndException
   {
      return getDocTypeFromFileExt(DocmawUtil.getFileExtention(file_name));
   }


   //bug 58326, starts....
   private String[] generateLocalFileNames(String unique_file_id, String[] repository_file_names)
   {
      String[] local_file_names = new String[repository_file_names.length];

      for (int x = 0; x < repository_file_names.length; x++)
      {
         local_file_names[x] = unique_file_id + repository_file_names[x].substring(repository_file_names[x].lastIndexOf("."));
      }
      return local_file_names;
   }
   //bug 58326, ends....
   
   
   // Added by Terry 20120927
   // Refer unique file id and remote file name to generate local file name in web server temp dir.
   private String generateLocalFileName(String unique_file_id, String repository_file_name)
   {
      return unique_file_id + repository_file_name.substring(repository_file_name.lastIndexOf("."));
   }
   // Added end


   private String[] getRepositoryFileNames(String doc_class,
                                           String doc_no,
                                           String doc_sheet,
                                           String doc_rev,
                                           String doc_type_list) throws FndException
   {
      ASPManager mgr = getASPManager();
      StringTokenizer st = new StringTokenizer(doc_type_list, "^");
      String[] repository_file_names = new String[st.countTokens()];
      int no_doc_types = st.countTokens();
      String current_doc_type;

      trans.clear();
      this.selected_doc_types = new String[no_doc_types];

      for (int x = 0; x < no_doc_types; x++)
      {
         current_doc_type = st.nextToken();
         cmd = trans.addCustomFunction("REMOTEFILENAME" + x,  "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_1", current_doc_type);
         if (!mgr.isEmpty(this.file_no)) {
            cmd.addParameter("IN_NUM", file_no);
         }

         this.selected_doc_types[x] = current_doc_type;
      }

      trans = perform(trans);

      for (int x = 0; x < no_doc_types; x++)
      {
         repository_file_names[x] = trans.getValue("REMOTEFILENAME" + x + "/DATA/OUT_1");
         if (DEBUG) Util.debug("repository_file_names[" + x + "] = " + repository_file_names[x]);
      }

      return repository_file_names;
   }
   
   //Bug Id 65997, Start
   private String[] getRepositoryFileNamesFromServer(String doc_class,
                                           String doc_no,
                                           String doc_sheet,
                                           String doc_rev,
                                           String doc_type_list) throws FndException
   {
      ASPManager mgr = getASPManager();
      String current_doc_type;
      String repository_file_name;
      String current_file;

      trans.clear();

      // Modified by Terry 20120917
      // Get all remote file name
      // Original: cmd = trans.addCustomFunction("REMOTEFILENAME", "Edm_File_Api.Get_Remote_File_Names", "OUT_1");
      cmd = trans.addCustomFunction("REMOTEFILENAME", "Edm_File_Api.Get_All_Remote_File_Names", "OUT_1");
      // Modified end
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type_list);

      trans = perform(trans);

      repository_file_name = trans.getValue("REMOTEFILENAME/DATA/OUT_1");
      StringTokenizer st1 = new StringTokenizer(repository_file_name, "^");
      StringTokenizer st2 = new StringTokenizer(doc_type_list, "^");
      int no_files = st1.countTokens();
      String[] repository_file_names = new String[no_files];

      int no_doc_types = st2.countTokens();
      this.selected_doc_types = new String[no_doc_types];

      for (int x = 0; x < no_doc_types; x++)
      {
         current_doc_type = st2.nextToken();
         this.selected_doc_types[x] = current_doc_type;
      }

      for (int x = 0; x < no_files; x++)
      {
         current_file = st1.nextToken();
         repository_file_names[x] = current_file;
      }
      return repository_file_names;
   }
   //Bug Id 65997, End

   // Added by Terry 20120918
   private String[] getRepositoryFileNamesFromServer(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type_list,
         String file_no_list) throws FndException
   {
      ASPManager mgr = getASPManager();
      String current_doc_type;
      String repository_file_name;
      String current_file;

      trans.clear();

      // Modified by Terry 20120917
      // Get all remote file name
      // Original: cmd = trans.addCustomFunction("REMOTEFILENAME",
      // "Edm_File_Api.Get_Remote_File_Names", "OUT_1");
      cmd = trans.addCustomFunction("REMOTEFILENAME", "Edm_File_Api.Get_All_Remote_File_Names", "OUT_1");
      // Modified end
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type_list);
      cmd.addParameter("IN_STR1", file_no_list);

      trans = perform(trans);

      repository_file_name = trans.getValue("REMOTEFILENAME/DATA/OUT_1");
      StringTokenizer st1 = new StringTokenizer(repository_file_name, "^");
      StringTokenizer st2 = new StringTokenizer(doc_type_list, "^");
      int no_files = st1.countTokens();
      String[] repository_file_names = new String[no_files];

      int no_doc_types = st2.countTokens();
      this.selected_doc_types = new String[no_doc_types];

      for (int x = 0; x < no_doc_types; x++)
      {
         current_doc_type = st2.nextToken();
         this.selected_doc_types[x] = current_doc_type;
      }

      for (int x = 0; x < no_files; x++)
      {
         current_file = st1.nextToken();
         repository_file_names[x] = current_file;
      }
      return repository_file_names;
   }
   // Added end
   
   private String getAssociatedDocumentTypes(String doc_class,
                                             String doc_no,
                                             String doc_sheet,
                                             String doc_rev,
                                             String master_doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      String doc_type_list = null;

      if ("REDLINE".equals(master_doc_type) || "VIEW".equals(master_doc_type))
      {
         doc_type_list = master_doc_type;
      }
      else if ("ORIGINAL".equals(master_doc_type))
      {
         trans = mgr.newASPTransactionBuffer();
         cmd = trans.addCustomFunction("DOCTYPESLIST", "Edm_File_Api.Get_Connected_Doc_Types_", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_STR1", master_doc_type);
         trans = perform(trans);

         doc_type_list = trans.getValue("DOCTYPESLIST/DATA/OUT_1");
         doc_type_list = "ORIGINAL^" + (mgr.isEmpty(doc_type_list) ? "" : doc_type_list);
      }

      return doc_type_list;
   }

   //Bug Id 65997, Start
   private String getAssociatedDocumentTypes(String doc_class,
					     String master_doc_type,
                                             String process) throws FndException   
   {
      ASPManager mgr = getASPManager();
      String doc_type_list = null;

      if ("REDLINE".equals(master_doc_type) || "VIEW".equals(master_doc_type))
      {
         doc_type_list = master_doc_type + "^";
      }
      else
      {
         trans = mgr.newASPTransactionBuffer();
	 cmd = trans.addCustomFunction("DOCTYPESLIST", "Doc_Class_Proc_Action_Line_Api.Get_All_Doc_Types", "OUT_1");
	 cmd.addParameter("IN_STR1", doc_class);
	 cmd.addParameter("IN_STR1", process);
	 trans = perform(trans);

	 doc_type_list = trans.getValue("DOCTYPESLIST/DATA/OUT_1");
	 doc_type_list = "ORIGINAL^" + (mgr.isEmpty(doc_type_list) ? "" : doc_type_list);
      }

      return doc_type_list;
   }

   //Bug Id 65997, End

    //bug 58326, modified secondary_ftp_file_names into local_file_names
   protected void getFileFromRepository(String rep_info,
                                        String local_file_names[],
                                        String repository_file_names[]) throws FndException
   {
      String rep_type = getStringAttribute(rep_info, "LOCATION_TYPE");
      String rep_address = getStringAttribute(rep_info, "LOCATION_ADDRESS");
      String rep_user = getStringAttribute(rep_info, "LOCATION_USER");
      String rep_password = decryptFtpPassword(getStringAttribute(rep_info, "LOCATION_PASSWORD"));
      String rep_port = getStringAttribute(rep_info, "LOCATION_PORT");
      String[] local_files = null;

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");

      //bug 58326, starts....
      if ("1".equals(rep_type))
      {
         local_files = getFileFromSharedRepository(rep_address, rep_user, rep_password, repository_file_names,local_file_names);
      }
      else if ("2".equals(rep_type))
      {
         //local_files = getFileFromFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names,local_file_names);
         getFileFromFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names,local_file_names);
      }
      else if ("3".equals(rep_type))
      {
         //local_files = getFileFromDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,local_file_names);
         getFileFromDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,local_file_names);
      }
     //bug 58326, ends....
   }


  //bug 58326, modified return type and added a new parameter
   protected void getFileFromFtpRepository(String ftp_address,
                                               String ftp_port,
                                               String ftp_user,
                                               String ftp_password,
                                               String ftp_file_names[],
                                               String local_file_names[]) throws FndException
   {
      if (DEBUG) 
         Util.debug(this+": getFileFromFtpRepository() }");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();

      String file_name;
      String temp_path = getTmpPath();
      //bug 58326
      //String[] local_file_names;

      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         //bug 58326
         //local_file_names = new String[ftp_file_names.length];

         for (int x = 0; x < ftp_file_names.length; x++)
         { 
            //bug 58326, starts....
            //file_name = getRandomFilename();
            local_file_names[x] = temp_path + local_file_names[x];
            //bug 58326, ends....
            //Util.debug(this+": getFileFromFtpRepository local_file_names["+x+"]=" +local_file_names[x]);
            //Util.debug(this+": getFileFromFtpRepository ftp_file_names["+x+"]  =" +ftp_file_names[x]);
            if (!ftp_server.get(ftp_file_names[x], local_file_names[x]))
            {
               ftp_server.logoff();
               throw new FndException(mgr.translate("DOCMAWDOCSRVFTPFAILED: Failed to get file &1 from repository located in &2", ftp_file_names[x], ftp_address));
            }
         }
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED1: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
      
      //bug 58326.
      //return local_file_names;
   }


   //bug 58326. chnaged the return type
   protected void getFileFromDatabaseRepository( String doc_class,
                                                 String doc_no,
                                                 String doc_sheet,
                                                 String doc_rev,
                                                 String local_file_names[]) throws FndException
   {
      if (DEBUG) Util.debug(this+": getFileFromDatabaseRepository() {");

      ASPManager mgr = getASPManager();

      String file_name;
      String temp_path = getTmpPath();
      //bug 58326
      //String[] local_file_names;

      //local_file_names = new String[selected_doc_types.length];
      //Bug Id 65997, Start
      int i = 0;
      for (int x = 0; x < selected_doc_types.length; x++) 
      {
	  trans.clear();
	  cmd = trans.addCustomFunction("EDMRECEXIT", "edm_File_storage_api.File_Exist", "OUT_1");
	  cmd.addParameter("IN_STR1", doc_class);
	  cmd.addParameter("IN_STR1", doc_no);
	  cmd.addParameter("IN_STR1", doc_sheet);
	  cmd.addParameter("IN_STR1", doc_rev);
	  cmd.addParameter("IN_STR1", selected_doc_types[x]);
	  trans = mgr.perform(trans);

	  if ("TRUE".equals(trans.getValue("EDMRECEXIT/DATA/OUT_1")))
	  {
	      selected_doc_types[i] = selected_doc_types[x];
	      i++;
	  }
      }

      //Bug Id 65997, End

      for (int x = 0; x < local_file_names.length; x++)
      {
         //bug 58326
         //file_name = getRandomFilename();
         //local_file_names[x] = addTempPath(file_name,temp_path);
         //local_file_names[x] = file_name;

         //check if origingal is not there in the edm_file_storage_tab, then selected_doc_types[x] = "VIEW";
         if ("ORIGINAL".equals(selected_doc_types[x])) {
            
            trans.clear();
            cmd = trans.addCustomFunction("EDMRECEXIT", "edm_File_storage_api.File_Exist", "OUT_1");
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", "ORIGINAL");
            trans = mgr.perform(trans);// ONLY ONE DB TRANSACTION HAPPENS HERE!!!! 
            
            if (!"TRUE".equals(trans.getValue("EDMRECEXIT/DATA/OUT_1"))) {
               selected_doc_types[x] = "VIEW";
            }
         }
         try
         {
            //bug 58326
            //Bug Id: 68849,start
            double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
            if (dFileNo > 0) {
               DatabaseRepository.getInstance(getASPConfig(),mgr).downloadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x], dFileNo,local_file_names[x]);
            }
            else // Bug Id: 68849,end
               DatabaseRepository.getInstance(getASPConfig(),mgr).downloadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x], local_file_names[x]);
         }
         catch(Throwable any)
         {
            if (DEBUG) any.printStackTrace();
            throw new FndException(mgr.translate("DOCMAWDOCSRVGETFILEFROMDBREP: An error occured while trying to get the file from the database repository. Error: ") + any.getMessage());
         }

         //Bug Id 80834, Start
         if (! compareFileSize(this.addTempPath(local_file_names[x],temp_path),doc_class,doc_no,doc_sheet,doc_rev,selected_doc_types[x])) 
         {
	    throw new FndException(mgr.translate("DOCMAWDOCSRVFILECOMPARE: Error occurred while transferring the file. Retry the operation again."));
         }
	 //Bug Id 80834, End
      }        

      //return local_file_names;

   }



   protected String[] getFileFromSharedRepository(String rep_address,
                                                  String rep_user,
                                                  String rep_password,
                                                  String repository_file_names[],
                                                  String local_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();

      // Docmaw currently does not support shared file locations.
      throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILE2: Can't get file from Shared Repository. Shared Repositories are currently not supported by Docmaw."));
   }


   /**
    * Returns the full url of the file. (For Non IE use.)
    */
   protected String getFileFromArchiveNonIe(String edm_rep_info) throws FndException
   {
      ASPManager mgr = getASPManager();

      // delete old files..
      deleteOldFilesNonIe();

      String file_name;
      //bug 58326
      file_name = getRandomFilename() + "." +getStringAttribute(edm_rep_info, "FILE_EXTENTION").toLowerCase();


      if ("2".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE")))
      {
         //bug 58326, moved file_nave up
         String full_local_file_name = getTmpPath() + file_name;
         String remote_file = getStringAttribute(edm_rep_info, "REMOTE_FILE_NAME");
         DocmawFtp ftp = new DocmawFtp();
         String ftp_user = getStringAttribute(edm_rep_info, "LOCATION_USER");
         String ftp_address = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
         String ftp_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
         String ftp_port = getStringAttribute(edm_rep_info, "LOCATION_PORT");
         int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

         if (ftp.login(ftp_address, ftp_user, ftp_password, port_no))
         {
            if (!ftp.get(remote_file, full_local_file_name))
            {
               ftp.logoff();
               throw new FndException(mgr.translate("DOCMAWDOCSRVFTPGETFAILED: FTP get failed on file &1.", remote_file));
            }
            ftp.logoff();
         }
         else
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED: Login to FTP server &1, port: &2 with login name &3 failed.", ftp_address, ftp_port, ftp_user));
         }
      }
      else if ("3".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE")))
      {
         String doc_class = getStringAttribute(edm_rep_info, "DOC_CLASS");
         String doc_no    = getStringAttribute(edm_rep_info, "DOC_NO");
         String doc_sheet = getStringAttribute(edm_rep_info, "DOC_SHEET");
         String doc_rev   = getStringAttribute(edm_rep_info, "DOC_REV");

         selected_doc_types = new String[1];
         selected_doc_types[0] = getStringAttribute(edm_rep_info, "DOC_TYPE");
         //bug 58326, starts....
         String[] local_file_names = new String[1];
         local_file_names[0] = file_name;
         getFileFromDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,local_file_names);
         //bug 58326, ends..
      }
      else
      {
         // This is a share file location.
         throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILENOTINFTP1: The document is not in a FTP server and the web client does not support that."));
      }

      //      String url = mgr.getURL();
      //      return url.substring(0, url.lastIndexOf("/") + 1) + mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_NON_IE_DIRECTORY") +  "/" + file_name;

      return file_name;
   }

   //Bug Id 69087, Start
   /**
   *Use this method to get the files from respective repository for
   *each file. Use only if files are in different repositories.
   */
   protected void getFilesFromRepository(String rep_info,
                                        String local_file_names[],
                                        String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer edm_rep_info_trans = mgr.newASPTransactionBuffer();

      String rep_type;
      String rep_address;
      String rep_user;
      String rep_password;
      String rep_port;
      String[] local_files = null;

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");

      int no_files = repository_file_names.length;
      String edm_rep_info;      
      
      // Bug Id 69087 - Added this to accomodate file_no used for transmitals in app75. 
      //               Only for a single comment file at a time now. May Require changes in the future
      String InFileNo;
      if (!mgr.isEmpty(this.file_no))
         InFileNo = file_no;
      else
         InFileNo = null;

      edm_rep_info_trans.clear();
      for (int i = 0;i<no_files;i++) {
         cmd = edm_rep_info_trans.addCustomFunction("EDMREPINFO_"+i, "Edm_File_Api.Get_Edm_Repository_Info4", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);         
         cmd.addParameter("IN_STR1", this.selected_doc_types[i]);         
         cmd.addParameter("IN_NUM", InFileNo);
      }      

      edm_rep_info_trans = perform(edm_rep_info_trans);

      for (int i = 0;i<no_files;i++) {         
         edm_rep_info = edm_rep_info_trans.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
         rep_type = getStringAttribute(edm_rep_info, "LOCATION_TYPE");
         if ("1".equals(rep_type))
         {
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");
            local_files  = getFileFromSharedRepository(rep_address, rep_user, rep_password, repository_file_names,local_file_names);
         }
         else if ("2".equals(rep_type))
         {
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");

            getSingleFileFromFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names[i],local_file_names[i]);
         }
         else if ("3".equals(rep_type))
         {
            getSingleFileFromDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,local_file_names[i],selected_doc_types[i]);
         }
      }    
   }

   protected void getSingleFileFromFtpRepository(String ftp_address,
                                               String ftp_port,
                                               String ftp_user,
                                               String ftp_password,
                                               String ftp_file_name,
                                               String local_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": getSingleFileFromFtpRepository() }");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();

      String file_name;
      String temp_path = getTmpPath();

      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {         
         local_file_name = temp_path + local_file_name;  
         if (!ftp_server.get(ftp_file_name, local_file_name))
         {
            ftp_server.logoff();
            throw new FndException(mgr.translate("DOCMAWDOCSRVFTPFAILED: Failed to get file &1 from repository located in &2", ftp_file_name, ftp_address));
         }         
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED1: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
   }


   protected void getSingleFileFromDatabaseRepository( String doc_class,
                                                 String doc_no,
                                                 String doc_sheet,
                                                 String doc_rev,
                                                 String local_file_name,
                                                 String file_type) throws FndException
   {
      if (DEBUG) Util.debug(this+": getFileFromDatabaseRepository() {");
      ASPManager mgr = getASPManager();

      String file_name;
      String temp_path = getTmpPath();

      boolean file_exists = false;
      
      trans.clear();
      cmd = trans.addCustomFunction("EDMRECEXIT", "edm_File_storage_api.File_Exist", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", file_type);
      //Bug Id: 71762, start
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      //Bug Id: 71762, end
      trans = mgr.perform(trans);

      if ("TRUE".equals(trans.getValue("EDMRECEXIT/DATA/OUT_1")))
      {
         file_exists = true;
      }
      else
      {
         //check if origingal is not there in the edm_file_storage_tab, then file_type = "VIEW";
         if ("ORIGINAL".equals(file_type)) {               
            file_type = "VIEW";
            file_exists = true;
         }
      }
      if (file_exists) {
         try
         {
            double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
            if (dFileNo > 0) {
               DatabaseRepository.getInstance(getASPConfig(),mgr).downloadFile(doc_class, doc_no,doc_sheet,doc_rev,file_type, dFileNo,local_file_name);
            }
            else
               DatabaseRepository.getInstance(getASPConfig(),mgr).downloadFile(doc_class, doc_no,doc_sheet,doc_rev,file_type, local_file_name);
         }
         catch(Throwable any)
         {
            if (DEBUG) any.printStackTrace();
            throw new FndException(mgr.translate("DOCMAWDOCSRVGETFILEFROMDBREP: An error occured while trying to get the file from the database repository. Error: ") + any.getMessage());
         }

         //Bug Id 80834. Start
         if (! compareFileSize(this.addTempPath(local_file_name,temp_path),doc_class,doc_no,doc_sheet,doc_rev,file_type)) 
         {
	    throw new FndException(mgr.translate("DOCMAWDOCSRVFILECOMPARE: Error occurred while transferring the file. Retry the operation again."));
         }
	 //Bug Id 80834. End
      } 
   }
   //Bug Id 69087, End

   protected void deleteFileInRepository(String rep_info,
                                         String repository_file_names[]) throws FndException

   {
      String rep_type = getStringAttribute(rep_info, "LOCATION_TYPE");
      String rep_address = getStringAttribute(rep_info, "LOCATION_ADDRESS");
      String rep_user = getStringAttribute(rep_info, "LOCATION_USER");
      String rep_password = decryptFtpPassword(getStringAttribute(rep_info, "LOCATION_PASSWORD"));
      String rep_port = getStringAttribute(rep_info, "LOCATION_PORT");

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");

      if ("1".equals(rep_type))
      {
         deleteFileInSharedRepository(rep_address, rep_user, rep_password, repository_file_names);
      }
      if ("2".equals(rep_type))
      {
         deleteFileInFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names);
      }
      if ("3".equals(rep_type))
      {
         deleteFileInDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev);
      }
   }
   
   // Bug ID 89622, Start
   protected void deleteFileInRepository(String doc_class,
                                         String doc_no,
                                         String doc_sheet,
                                         String doc_rev,
                                         String doc_type) throws FndException
   {
      String rep_info = getEdmRepositoryInformation(doc_class,doc_no,doc_sheet,doc_rev,doc_type);
      String rep_type = getStringAttribute(rep_info, "LOCATION_TYPE");
      String rep_address = getStringAttribute(rep_info, "LOCATION_ADDRESS");
      String rep_user = getStringAttribute(rep_info, "LOCATION_USER");
      String rep_password = decryptFtpPassword(getStringAttribute(rep_info, "LOCATION_PASSWORD"));
      String rep_port = getStringAttribute(rep_info, "LOCATION_PORT");   
      
      String[] repository_file_names = new String[1]; 
      repository_file_names[0] = getFullRemoteFileName(doc_class,doc_no,doc_sheet,doc_rev,doc_type); 
      
      if ("1".equals(rep_type))
      {
         deleteFileInSharedRepository(rep_address, rep_user, rep_password, repository_file_names); 
      }
      if ("2".equals(rep_type))
      {
         deleteFileInFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names);
      }
      if ("3".equals(rep_type))
      {
         deleteFileInDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,doc_type);
      }
   }
   // Bug ID 89622, Finish
   
   protected void deleteFileInSharedRepository(String rep_address,
                                               String rep_user,
                                               String rep_password,
                                               String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();

      // Docmaw currently does not support shared file locations.
      throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILEDELETE: Can't delete file in Shared Repository. Shared Repositories are currently not supported by Docmaw."));
   }



   protected void deleteFileInFtpRepository(String ftp_address,
                                            String ftp_port,
                                            String ftp_user,
                                            String ftp_password,
                                            String ftp_file_names[]) throws FndException
   {
      if (DEBUG) Util.debug("deleteFileInFtpRepository (");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();

      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         for (int x = 0; x < ftp_file_names.length; x++)
         {
            ftp_server.deleteFile(ftp_file_names[x]);
         }
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED2: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }

      if (DEBUG) Util.debug("deleteFileInFtpRepository }");
   }


   protected void deleteFileInDatabaseRepository(String doc_class,
                                                 String doc_no,
                                                 String doc_sheet,
                                                 String doc_rev) throws FndException
   {
      if (DEBUG) Util.debug("deleteFileInDatabaseRepository {");

      ASPManager mgr = getASPManager();
      for (int x = 0; x < selected_doc_types.length; x++)
      {
         // Bug ID 89622, Start (Moved code to the new overloaded method)
         deleteFileInDatabaseRepository(doc_class, doc_no, doc_sheet, doc_rev, selected_doc_types[x]);
         // Bug ID 89622, Finish
      }
      if (DEBUG) Util.debug("deleteFileInDatabaseRepository }");
   }
   
   // Bug ID 89622, Start
   protected void deleteFileInDatabaseRepository(String doc_class,
                                                 String doc_no,
                                                 String doc_sheet,
                                                 String doc_rev,
                                                 String doc_type) throws FndException
   {
      if (DEBUG) Util.debug("deleteFileInDatabaseRepository2 {");

      ASPManager mgr = getASPManager();
      try
      {
         //Bug 59843, Start, Added check on selected_doc_types
         if (!mgr.isEmpty(doc_type))
         {
            if (checkExistDocType(doc_class,doc_no,doc_sheet,doc_rev,doc_type))
            {
               //Bug Id: 68849,start
               double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
               if (dFileNo > 0)                   
                  DatabaseRepository.getInstance(getASPConfig(),mgr).deleteRemoteFile(doc_class,doc_no,doc_sheet,doc_rev,doc_type,dFileNo);
               else
                  DatabaseRepository.getInstance(getASPConfig(),mgr).deleteRemoteFile(doc_class,doc_no,doc_sheet,doc_rev,doc_type);
            }           
         } 
      }
      catch(Throwable any)
      {
         if (DEBUG) any.printStackTrace();
            throw new FndException(mgr.translate("DOCMAWDOCSRVDELFILEINDBREP: An error occured while trying to delete the file from the database repository. Error: ") + any.getMessage());
      }
      
      if (DEBUG) Util.debug("deleteFileInDatabaseRepository2 }");
   }   
   // Bug ID 89622, Finish

   //Bug Id 69087, Start
   /**
   *Use this method to deletet the files in respective repository
   *for each file. Use only if files are in different repositories.
   */
   protected void deleteFilesInRepository(String rep_info,
                                          String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer edm_rep_info_trans = mgr.newASPTransactionBuffer();

      String rep_type;
      String rep_address;
      String rep_user;
      String rep_password;
      String rep_port;

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");

      int no_files = repository_file_names.length;
      String edm_rep_info;

      // Bug Id 69087 - Added this to accomodate file_no used for transmitals in app75. 
      //               Only for a single comment file at a time now. May Require changes in the future
      String InFileNo;
      if (!mgr.isEmpty(this.file_no))
         InFileNo = file_no;
      else
         InFileNo = null;

      edm_rep_info_trans.clear();
      for (int i = 0;i<no_files;i++) {
         cmd = edm_rep_info_trans.addCustomFunction("EDMREPINFO_"+i, "Edm_File_Api.Get_Edm_Repository_Info3", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_NUM", InFileNo);
         cmd.addParameter("IN_STR1", repository_file_names[i]);         
      }

      edm_rep_info_trans = perform(edm_rep_info_trans);

      for (int i = 0;i<no_files;i++) { 
         edm_rep_info = edm_rep_info_trans.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
         rep_type = getStringAttribute(edm_rep_info, "LOCATION_TYPE");
         if ("1".equals(rep_type))
         {
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");

            deleteFileInSharedRepository(rep_address, rep_user, rep_password, repository_file_names);
         }
         if ("2".equals(rep_type))
         {
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");

            deleteSingleFileInFtpRepository(rep_address, rep_port, rep_user, rep_password, repository_file_names[i]);
         }
         if ("3".equals(rep_type))
         {
            deleteSingleFileInDatabaseRepository(doc_class,doc_no,doc_sheet,doc_rev,selected_doc_types[i]);
         }
      }
   }
   

   protected void deleteSingleFileInFtpRepository(String ftp_address,
                                            String ftp_port,
                                            String ftp_user,
                                            String ftp_password,
                                            String ftp_file_name) throws FndException
   {
      if (DEBUG) Util.debug("deleteSingleFileInFtpRepository {");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();

      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         ftp_server.deleteFile(ftp_file_name);
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED2: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }

      if (DEBUG) Util.debug("deleteSingleFileInFtpRepository }");
   }

   protected void deleteSingleFileInDatabaseRepository(String doc_class,
                                                 String doc_no,
                                                 String doc_sheet,
                                                 String doc_rev,
                                                 String file_type) throws FndException
   {
      if (DEBUG) Util.debug("deleteSingleFileInDatabaseRepository {");

      ASPManager mgr = getASPManager();
      try
      {
         if (!mgr.isEmpty(file_type)){
            if (checkExistDocType(doc_class,doc_no,doc_sheet,doc_rev,file_type))
            {
               double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
               if (dFileNo > 0)
               {
                  DatabaseRepository.getInstance(getASPConfig(),mgr).deleteRemoteFile(doc_class,doc_no,doc_sheet,doc_rev,file_type,dFileNo);
               }
               else
               {
                  DatabaseRepository.getInstance(getASPConfig(),mgr).deleteRemoteFile(doc_class,doc_no,doc_sheet,doc_rev,file_type);
               }
            }
         }
      }
      catch(Throwable any)
      {
         if (DEBUG) any.printStackTrace();
         throw new FndException(mgr.translate("DOCMAWDOCSRVDELFILEINDBREP: An error occured while trying to delete the file from the database repository. Error: ") + any.getMessage());
      }
      if (DEBUG) Util.debug("deleteSingleFileInDatabaseRepository }");
   }
   //Bug Id 69087, End

  //bug 58326, modified parameter name
   protected void putFileToRepository(String rep_info,
                                      String local_file_names[],
                                      String repository_file_names[]) throws FndException
   {
      
      String rep_type = getStringAttribute(rep_info, "LOCATION_TYPE");
      String rep_address = getStringAttribute(rep_info, "LOCATION_ADDRESS");
      String rep_user = getStringAttribute(rep_info, "LOCATION_USER");
      String rep_password = decryptFtpPassword(getStringAttribute(rep_info, "LOCATION_PASSWORD"));
      String rep_port = getStringAttribute(rep_info, "LOCATION_PORT");
      String[] local_files;
     //bug 58326, starts....
      if (!"3".equals(rep_type)) { // for database we do not pass local path:bakalk
         local_files = addLocalPath(local_file_names);
      }else
         local_files = local_file_names;
      //bug 58326, ends..      

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");

      if ("1".equals(rep_type))
      {
         putFileToSharedRepository(rep_address, rep_user, rep_password, local_files, repository_file_names);
      }
      else if ("2".equals(rep_type))
      {
         if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____FTP_REP___XXXXXXXXXXXXXXXXXX");
         putFileToFtpRepository(rep_address, rep_port, rep_user, rep_password, local_files, repository_file_names);
      }
      else if ("3".equals(rep_type))
      {
         if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____DB_REP___XXXXXXXXXXXXXXXXXX");
         putFileToDatabaseRepository(doc_class, doc_no,doc_sheet,doc_rev,local_files);
      }
   }


   protected void putFileToSharedRepository(String rep_address,
                                            String rep_user,
                                            String rep_password,
                                            String local_files[],
                                            String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();

      // Docmaw currently does not support shared file locations.
      throw new FndException(mgr.translate("DOCMAWDOCSRVPUTSHAREFILEPUT: Can't put file to Shared Repository. Shared Repositories are currently not supported by Docmaw."));
   }


   /**
    *
    */
   protected void putFileToFtpRepository(String ftp_address,
                                         String ftp_port,
                                         String ftp_user,
                                         String ftp_password,
                                         String local_files[],
                                         String ftp_file_names[]) throws FndException
   {
      if (DEBUG) Util.debug(this+": putFileToFtpRepository() }");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();
      
      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
      
      String tp = getTmpPath();
      boolean bDelete = true; //Bug Id 67442
      
      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         for (int x = 0; x < local_files.length; x++)
         {
            /*ftp_server.putBinaryFile(local_files[x], ftp_file_names[x]);
            deleteFile(local_files[x]);*/
            
            bDelete = true; //Bug Id 67442
            
            if (local_files[x].startsWith(getTmpPath()))
            {
               ftp_server.putBinaryFile(local_files[x], ftp_file_names[x]);
               //Bug Id 67442, start
               for (int y = x+1; y < local_files.length; y++)
               {
                  if (local_files[x].equals(local_files[y]))
                  {
                     bDelete = false;
                     break;
                  }
                  
               }
               
               if (bDelete)
                  deleteFile(local_files[x]);
               
               //Bug Id 67442, end
            }
            else
            {
               ftp_server.putBinaryFile(tp+local_files[x], ftp_file_names[x]);
               //Bug Id 67442, start
               for (int y = x+1; y < local_files.length; y++)
               {
                  if (local_files[x].equals(local_files[y]))
                  {
                     bDelete = false;
                     break;
                  }
                  
               }
               
               if (bDelete)
                  deleteFile(tp+local_files[x]);
               
               //Bug Id 67442, end
            }
         }
         
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED3: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
      
      if (DEBUG) Util.debug(this+": putFileToFtpRepository() }");
   }


   protected void putFileToDatabaseRepository(String doc_class,
                                              String doc_no,
                                              String doc_sheet,
                                              String doc_rev,
                                              String local_files[]) throws FndException
   {
      if (DEBUG) Util.debug(this+": putFileToDatabaseRepository() {");
      if (DEBUG) Util.debug("doc_class="+doc_class);
      if (DEBUG) Util.debug("doc_no="+doc_no);
      if (DEBUG) Util.debug("doc_sheet="+doc_sheet);
      if (DEBUG) Util.debug("doc_rev="+doc_rev);

      // just to delete local files we need temppath
      String tempPath = getTmpPath();
      
      //Bug Id 67442, start
      //Check the solitary view file checkin : if so skip copying the original file to the DB.
      boolean bDelete = true;
      boolean bValidView = false;
      ASPManager mgr = getASPManager();
      
      trans.clear();
      cmd = trans.addCustomFunction("EDMVALIDVIEWCOPY", "edm_File_api.Check_Valid_View_Copy", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      trans = mgr.perform(trans);
      
      if ("TRUE".equals(trans.getValue("EDMVALIDVIEWCOPY/DATA/OUT_1")))
      {
         bValidView = true;
      }
      //Bug Id 67442, end
      
      for (int x = 0; x < local_files.length; x++)
      {
         if (DEBUG) Util.debug(this + ": selected_doc_types["+x+"]="+selected_doc_types[x]);
         if (DEBUG) Util.debug(this + ": local_files["+x+"]="+local_files[x]);
         
         bDelete = true; //Bug Id 67442
         
         String localfiles = local_files[x];
         int tempPathLength = tempPath.length();
         
         if (localfiles.startsWith(tempPath))
            localfiles = localfiles.substring(tempPathLength);
         
         try
         {
            //DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x],local_files[x]);
            //Bug Id: 68849,start
            double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
            if (dFileNo > 0) {
               //Bug Id 67442, start
               if (bValidView) 
               {
                  if (!"ORIGINAL".equals(selected_doc_types[x]) )
                  {
                     DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x],dFileNo,localfiles);
                  }
               }
               else
                  DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x],dFileNo,localfiles);
               
               //Bug Id 67442, end
            }
            else //Bug Id: 68849,end
            {
               //Bug Id 67442, start
               if (bValidView) 
               {
                  if (!"ORIGINAL".equals(selected_doc_types[x]) )
                  {
                     DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x],localfiles);
                  }
               }
               else
                  DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,selected_doc_types[x],localfiles);
               //Bug Id 67442, end
            }
         }
         catch(Throwable any)
         {
            if (DEBUG) Util.debug(Str.getStackTrace(any));
            throw new FndException(mgr.translate("DOCMAWDOCSRVPUTFILETODBREP: An error occured while trying to put the file to the database repository. Error: ") + any.getMessage());
         }
         
         //Bug Id 80834, Start
//         if (! compareFileSize(this.addTempPath(localfiles,tempPath),doc_class,doc_no,doc_sheet,doc_rev,selected_doc_types[x])) 
//         {
//            throw new FndException(mgr.translate("DOCMAWDOCSRVFILECOMPARE: Error occurred while transferring the file. Retry the operation again."));
//         }
         //Bug Id 80834, End
         
         //Bug Id 67442, start
         for (int y = x+1; y < local_files.length; y++)
         {
            if (local_files[x].equals(local_files[y]))
            {
               bDelete = false;
               break;
            }
         }
         
         if (bDelete)
            deleteFile(this.addTempPath(localfiles,tempPath));
         
         //Bug Id 67442, end
      }
      
      if (DEBUG) Util.debug(this+": putFileToDatabaseRepository() }");
   }

   
   // //bug 58326, Remvoed putFileIntoArchive() since it is never used anywhere.
     /**
    * Stores the file in ftp archive.
    * @param edmRepInfo Edm repository information of the file.
    * @param fullSecondaryFtpFileName Full secondary ftp file name.
    */
   protected void putFileIntoArchiveNonIe(String edm_rep_info,
                                          String full_local_file_name) throws FndException
   {
      ASPManager mgr = getASPManager();

      // delete old files..
      deleteOldFilesNonIe();

      String server_view_file_name = "";
      String server_file_name = getStringAttribute(edm_rep_info, "REMOTE_FILE_NAME").toUpperCase();

      if (!Str.isEmpty(view_file_ext))
         server_view_file_name = (server_file_name.substring(0, server_file_name.lastIndexOf(".")+1) + view_file_ext).toUpperCase();

      if (!Str.isEmpty(redline_file_ext))
         server_file_name = (server_file_name.substring(0, server_file_name.lastIndexOf(".")+1) + redline_file_ext).toUpperCase();

      if ("2".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE"))) //ftp
      {
         DocmawFtp ftp = new DocmawFtp();
         String ftp_user = getStringAttribute(edm_rep_info, "LOCATION_USER");
         String ftp_address = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
         String ftp_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
         String ftp_port = getStringAttribute(edm_rep_info, "LOCATION_PORT");
         int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

         // View file also?
         String full_local_view_file_name = "";
         if (!Str.isEmpty(view_file_ext))
            full_local_view_file_name = full_local_file_name + "_view_" + view_file_ext;

         if (ftp.login( ftp_address, ftp_user, ftp_password, port_no))
         {
            ftp.putBinaryFile(full_local_file_name, server_file_name);

            // View file also?
            if (!Str.isEmpty(view_file_ext))
               ftp.putBinaryFile(full_local_view_file_name, server_view_file_name);

            // Remove old view file if we have changed file type of it.
            if (remove_old_view_file)
            {
               String old_server_view_file_name = (server_view_file_name.substring(0, server_view_file_name.lastIndexOf(".")+1) + old_view_file_ext).toUpperCase();
               ftp.deleteFile(old_server_view_file_name);
            }

            // Remove old Redline file if we have changed file type of it.
            if (remove_old_redline_file)
            {
               String old_server_redline_file_name = (server_file_name.substring(0, server_file_name.lastIndexOf(".")+1) + old_redline_file_ext).toUpperCase();
               ftp.deleteFile(old_server_redline_file_name);
            }

            ftp.logoff();

            deleteFile(full_local_file_name);

            // View file also?
            if (!Str.isEmpty(view_file_ext))
               deleteFile(full_local_view_file_name);
         }
         else
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED5: Login to FTP server &1, port: &2 with login name &3 failed.", ftp_address, ftp_port, ftp_user));
         }
      }
      else if ("3".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE"))) //database
      {
         String[] local_files = new String[1]; // currently only one file is operated for none-ie
         //Bug 59564, Start, if location type is Database, send the file name only (for Non-IE)
         if (full_local_file_name.indexOf(File.separator)>0)
             local_files[0] = full_local_file_name.substring(full_local_file_name.lastIndexOf(File.separator)+1);
         else
             local_files[0] = full_local_file_name ;
         //Bug 59564, End

         String doc_class = getStringAttribute(edm_rep_info, "DOC_CLASS");
         String doc_no    = getStringAttribute(edm_rep_info, "DOC_NO");
         String doc_sheet = getStringAttribute(edm_rep_info, "DOC_SHEET");
         String doc_rev   = getStringAttribute(edm_rep_info, "DOC_REV");

         selected_doc_types = new String[local_files.length];
         selected_doc_types[0] = getStringAttribute(edm_rep_info, "DOC_TYPE");


         putFileToDatabaseRepository(doc_class, doc_no,doc_sheet,doc_rev,local_files);
      }
      else
      {
         // This is a share file location.
         throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILENOTINFTP3: The document is not in a FTP server and the web client does not support that."));
      }
   }

   //Bug Id 69087, Start   
   /**
   *Use this method to put the files into respective repository for
   *each file. Use only if files are in different repositories.
   */
   protected void putFilesToRepository(String rep_info,
                                      String local_file_names[],
                                      String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer edm_rep_info_trans = mgr.newASPTransactionBuffer();

      String rep_type;
      String rep_address;
      String rep_user;
      String rep_password;
      String rep_port;
      String local_file;
      String tmpPath = "";

      try{
         tmpPath = this.getTmpPath();
      }catch(ifs.fnd.service.FndException e){
      }

      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");      


      int no_files = local_file_names.length;
      String edm_rep_info;

      // Bug Id 69087 - Added this to accomodate file_no used for transmitals in app75. 
      //               Only for a single comment file at a time now. May Require changes in the future
      String InFileNo;
      if (!mgr.isEmpty(this.file_no))
         InFileNo = file_no;
      else
         InFileNo = null;

      edm_rep_info_trans.clear();
      for (int i = 0;i<no_files;i++) {
         cmd = edm_rep_info_trans.addCustomFunction("EDMREPINFO_"+i, "Edm_File_Api.Get_Edm_Repository_Info3", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_NUM", InFileNo);
         cmd.addParameter("IN_STR1", repository_file_names[i]);         
      }

      edm_rep_info_trans = perform(edm_rep_info_trans);

      for (int i = 0;i<no_files;i++) {         
         edm_rep_info = edm_rep_info_trans.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
         rep_type = getStringAttribute(edm_rep_info, "LOCATION_TYPE");
         if ("1".equals(rep_type))
         {
            local_file = tmpPath + local_file_names[i];

            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");

            putFileToSharedRepository(rep_address, rep_user, rep_password, local_file_names, repository_file_names);
         }
         else if ("2".equals(rep_type))
         {
            local_file = tmpPath + local_file_names[i];

            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");

            if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____FTP_REP___XXXXXXXXXXXXXXXXXX");
            putSingleFileToFtpRepository(rep_address, rep_port, rep_user, rep_password, local_file, repository_file_names[i]);
         }
         else if ("3".equals(rep_type))
         {
            if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____DB_REP___XXXXXXXXXXXXXXXXXX");
            putSingleFileToDatabaseRepository(doc_class, doc_no,doc_sheet,doc_rev,local_file_names[i],selected_doc_types[i]);
         }
      }
   }

   protected void putSingleFileToFtpRepository(String ftp_address,
                                               String ftp_port,
                                               String ftp_user,
                                               String ftp_password,
                                               String local_file,
                                               String ftp_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": putSingleFileToFtpRepository() }");

      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();

      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
      
      String tp = getTmpPath();
      
      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         if (local_file.startsWith(getTmpPath()))
         {
            ftp_server.putBinaryFile(local_file, ftp_file_name);
            deleteFile(local_file);
         }
         else
         {
            ftp_server.putBinaryFile(tp+local_file, ftp_file_name);
            deleteFile(tp+local_file);
         }
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED3: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
      
      if (DEBUG) Util.debug(this+": putSingleFileToFtpRepository() }");
   }


   protected void putSingleFileToDatabaseRepository(String doc_class,
                                                    String doc_no,
                                                    String doc_sheet,
                                                    String doc_rev,
                                                    String local_file,
                                                    String file_type) throws FndException
   {
      if (DEBUG) Util.debug(this+": putSingleFileToDatabaseRepository() {");

      // just to delete local files we need temppath
      String tempPath = getTmpPath();
      
      ASPManager mgr = getASPManager();
      
      String localfiles = local_file;
      int tempPathLength = tempPath.length();
      
      if (localfiles.startsWith(tempPath))
         localfiles = localfiles.substring(tempPathLength);
      
      try
      {
         double dFileNo = mgr.isEmpty(this.file_no)?0:(new Double(this.file_no).doubleValue());
         if (dFileNo > 0)
         {            
            DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,file_type,dFileNo,localfiles);
         }
         else
         {
            DatabaseRepository.getInstance(getASPConfig(),mgr).uploadFile(doc_class, doc_no,doc_sheet,doc_rev,file_type,localfiles);
         }
      }
      catch(Throwable any)
      {
         if (DEBUG) Util.debug(Str.getStackTrace(any));
         throw new FndException(mgr.translate("DOCMAWDOCSRVPUTFILETODBREP: An error occured while trying to put the file to the database repository. Error: ") + any.getMessage());
      }
      //Bug Id 80834, Start
//      if (! compareFileSize(this.addTempPath(localfiles,tempPath),doc_class,doc_no,doc_sheet,doc_rev,file_type)) 
//      {
//         throw new FndException(mgr.translate("DOCMAWDOCSRVFILECOMPARE: Error occurred while transferring the file. Retry the operation again."));
//      }              
      //Bug Id 80834, End
      deleteFile(this.addTempPath(localfiles,tempPath));  
      
      if (DEBUG) Util.debug(this+": putSingleFileToDatabaseRepository() }");
   }
   //Bug Id 69087, End

   //Bug Id 74523, start
   public String adjustFileName(String doc_class, String doc_no, String doc_sheet, String doc_rev, String file_no, String localpath, String filename) throws FndException
   {
      ASPManager mgr = getASPManager();

      if (file_no == null)
      {
         file_no = "1";
      }
      
      String file_ext = filename.substring(filename.lastIndexOf(".") + 1,filename.length());
      
      if (localpath.lastIndexOf("\\") < localpath.length()-1)
      {
         localpath = localpath + "\\";  
      }
      if (MAX_FILE_NAME_LENGTH >= localpath.length()  + filename.length()) 
      {
         filename = filename;
      }
      else
      {
         trans.clear();                                                                                         
         
         ASPCommand cmd = trans.addCustomFunction("SHORT_NAME", "Edm_File_API.Get_Short_File_Name", "STR_OUT");  
         cmd.addParameter("IN_STR1", doc_class);                                                                     
         cmd.addParameter("IN_STR1", doc_no);                                                                        
         cmd.addParameter("IN_STR1", doc_sheet);                                                                     
         cmd.addParameter("IN_STR1", doc_rev);                                                                       
         cmd.addParameter("IN_STR1", file_no);                                                                       
         cmd.addParameter("IN_STR1", localpath);                                                                     
         cmd.addParameter("IN_STR1", file_ext);                                                                                                                                         
         cmd.addParameter("IN_STR1", filename);                                                                      
         
         trans = mgr.perform(trans);                                                                            
         
         filename = trans.getValue("SHORT_NAME/DATA/STR_OUT"); 
         
         appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWDOCSTRLENLMT: The length of the local file name exceeds the limit of 255 characters. File name will be shortened to:") +filename+ "\");\n");
         
         debug("DEBUG: adjustFileName()  outName@@@@ " + filename);
      }
      
      return filename;
   }
   //Bug Id 74523, end   

   //bug 58326, starts....
	private String[] addLocalPath(String[] localFiles){
      String tmpPath = "";
      try{
         tmpPath = this.getTmpPath();
      }catch(ifs.fnd.service.FndException e){
      }
      
      String[] fullFileNames = new String[localFiles.length];
      for (int i=0;i<localFiles.length;i++) {
         fullFileNames[i] = tmpPath + localFiles[i];
      }
      return fullFileNames;
   }

   //bug 58326, ends..

   /**
    * Deletes the specified local file
    */
   private void deleteFile(String tmp_file_location) throws FndException
   {
      ASPManager mgr = getASPManager();
      tmp_file_location = handleBackSlash(tmp_file_location);
      File tmp_file = new File(tmp_file_location);
      if (!tmp_file.delete())
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFILENOTDEL: The file &1 could not be deleted.", tmp_file_location));
      }
   }


   /**
    * creates an empty file which we will send to the client
    */
   private void createNullFile(String full_file_name, ASPManager mgr) throws FndException
   {
      try
      {
         FileWriter file_writer;
         file_writer = new FileWriter(full_file_name);
         file_writer.close();
      }
      catch (IOException ioe)
      {
         if (DEBUG) ioe.printStackTrace();
         throw new FndException(mgr.translate("DOCMAWDOCSRVCCRTNULLFILEL: An error occured while creating the empty dummy file: ") + ioe.toString());
      }
   }

   // Not needed anymore, but let's keep it for a while, just in case...

   private void deleteOldFilesNonIe() throws FndException
   {
      ASPManager mgr = getASPManager();

      String tempPath = null;
      boolean is_absolute;

      is_absolute = new File(mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH")).isAbsolute();
      if(is_absolute)
      {
         /* For work with the existing hard coded path in docmawconfig.xml */
         tempPath = mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH");
      }
      else
      {
         try
         {
            tempPath = new File(mgr.getPhyPath(mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH"))).getCanonicalPath();
         }
         catch(IOException e)
         {
            error(e);
         }
      }


      //
      // Check whether the path exists and
      // create it if it does not
      //
      File tmp_dir = new File(tempPath);
      if (!tmp_dir.exists())
      {
         createServerPath(tempPath);
      }


      String temp_expire = "";
      int expire;

      try
      {
         temp_expire = mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_EXPIRE_HOURS");
         expire = Integer.valueOf(temp_expire).intValue();
      }
      catch (Exception e)
      {
         expire = 24;
      }

      // Delete files that have expired
      deleteOldFiles(tempPath, expire);
   }


   private void deleteOldFiles(String path, int hrs_ago) throws FndException
   {
      File dir   = new File(path);
      if (dir.isDirectory())
      {
         String[] files = dir.list();

         for (int i = 0; i < files.length;i++)
         {
            File file = new File(path +"\\"+ files[i]);

            if (file.isFile())
            {
               long file_date = file.lastModified();
               long cur_time  = System.currentTimeMillis();
               //System.
               long age_hrs   = (cur_time - file_date) / 3600000;

               if (age_hrs > hrs_ago)
                  file.delete();
            }
         }
      }
      else
         throw new FndException("DOCMAWDOCSRVTEMPDIRNOTFOUND: The temp directory &1 does not exist. Change the parameter [DOCMAW/DOCUMENT_TEMP_PATH] or create directory.", path);
   }


   private void createServerPath(String path) throws FndException
   {
      ASPManager mgr = getASPManager();

      File server_path = new File(path);

      if (!server_path.exists())
      {
         try
         {
            // Attempt to create the temp path..
            server_path.mkdirs();
         }
         catch(Exception e)
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVTMPPATHNOTEXIST: The directory specified by the parameter DOCMAW/DOCUMENT_TEMP_PATH could not be created on the server. The problem may be due to insufficient privileges. Contact your system adminstrator."));
         }
      }
   }


   protected String decryptFtpPassword(String crypt_pwd)
   {
      if (DEBUG) Util.debug(this+": decryptFtpPassword() {");

      StringBuffer uncrypt_pwd = new StringBuffer();

      for (int i = crypt_pwd.length()-1; i>=0; i--)
      {
         char ch = crypt_pwd.charAt(i);
         switch (ch)
         {
         case 1: ch = 94;
            break;
         case 2: ch = 61;
            break;
         }

         if ((crypt_pwd.length() - i + 1) % 2 == 0)
         {
            ch = (char) (ch + (crypt_pwd.length() - i));
         }
         else
         {
            ch = (char) (ch - (crypt_pwd.length() - i));
         }
         uncrypt_pwd.append(ch);
      }

      if (DEBUG) Util.debug(this+":   decrypted FTP password: " + uncrypt_pwd.toString());
      if (DEBUG) Util.debug(this+":   decryptFtpPassword() {");

      return uncrypt_pwd.toString();
   }


   public boolean isStructureDocument(String doc_class, String doc_no) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETSTRUCTURE", "Doc_Title_API.Get_Structure_", "OUT_1" );
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      trans = perform(trans);

      if ("1".equals(trans.getValue("GETSTRUCTURE/DATA/OUT_1")))
         return true;
      else
         return false;
   }


   public String getEdmFileType(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFILETYPE", "Edm_File_Api.Get_File_Type", "OUT_1" );
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = perform(trans);
      return trans.getValue("GETFILETYPE/DATA/OUT_1");
   }

   // Added by Terry 20120919
   public String getEdmFileType(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFILETYPE", "Edm_File_Api.Get_File_Type", "OUT_1" );
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM", file_no);
      trans = perform(trans);
      return trans.getValue("GETFILETYPE/DATA/OUT_1");
   }
   // Added end

   public String getEdmInformation(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_API.get_edm_information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = perform(trans);
      
      return trans.getValue("EDMINFO/DATA/OUT_1");
   }
   
   // Added by Terry 20120919
   public String getEdmInformation(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_API.Get_Edm_Information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      trans = perform(trans);
      
      return trans.getValue("EDMINFO/DATA/OUT_1");
   }
   // Added end



   public String getEdmRepositoryInformation(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = perform(trans);
      return trans.getValue("EDMREPINFO/DATA/OUT_1");
   }
   
   // Added by Terry 20120919
   public String getEdmRepositoryInformation(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_API.Get_Edm_Repository_Info", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      trans = perform(trans);
      return trans.getValue("EDMREPINFO/DATA/OUT_1");
   }
   // Added end


   public String getLocalFileName(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      // Use the DocMan file name..
      return getStringAttribute(edm_rep_info, "LOCAL_FILE_NAME");
   }
   
   // Added by Terry 20120919
   public String getLocalFileName(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no) throws FndException
   {
      String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);

      // Use the DocMan file name..
      return getStringAttribute(edm_rep_info, "LOCAL_FILE_NAME");
   }
   // Added end

   /*
    *This method return a type corrected filename. For example if
    *the user has selected a file test.txt but the file_type as test.doc
    *then the parameters would be test.txt and NOTPAD. This method checks
    *if the file extension matches the file type, and if it doesnt, it renames
    *the file to have the correct externsion, IE in this case test.doc, which
    *is returned
    */

   public String getTypeCorrectedFileName(String file_name, String file_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      if (!mgr.isEmpty(file_type))
      {
         String file_ext = getFileExtension(file_type);
         file_name = DocmawUtil.getBaseFileName(file_name) + "." + file_ext;
      }
      return file_name;
   }


   public String getGeneratedLocalFileName(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GET_GENERATED_FILENAME", "Edm_File_Util_API.Generate_Docman_File_Name_", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      //Bug Id: 68849,start
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      //Bug Id: 68849,end
      trans = perform(trans);
      return trans.getValue("GET_GENERATED_FILENAME/DATA/OUT_1");
   }
   
   // Added by Terry 20120919
   public String getGeneratedLocalFileName(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GET_GENERATED_FILENAME", "Edm_File_Util_API.Generate_Docman_File_Name_", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      trans = perform(trans);
      return trans.getValue("GET_GENERATED_FILENAME/DATA/OUT_1");
   }
   // Added end


   public String getDocumentData(String doc_class, String doc_no, String doc_sheet, String doc_rev) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("GETDOCDATA", "Doc_Issue_API.Get_Document_Data", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      trans = perform(trans);

      return trans.getValue("GETDOCDATA/DATA/OUT_1");
   }


   public String getMacroCode(String action, String file_type, String process) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETMACROCODE", "Edm_Macro_API.Create_Code", "OUT_1");
      cmd.addParameter("IN_STR1", action);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", process);
      trans = perform(trans);
      return trans.getValue("GETMACROCODE/DATA/OUT_1");
   }


   public String getNameFileUsing(String doc_class, String process) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GET_NAME_FILE_USING", "Doc_Class_Proc_Action_Head_API.Get_Name_Files_Using_Db", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", process);
      trans = perform(trans);
      return trans.getValue("GET_NAME_FILE_USING/DATA/OUT_1");
   }


   public String getDocumentTitle(String doc_class, String doc_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETTITLE", "Doc_Title_API.Get_Title", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      trans = perform(trans);
      return trans.getValue("GETTITLE/DATA/OUT_1");
   }


   protected String getStringAttribute(String attr_string, String attr_name)
   {
      StringTokenizer st = new StringTokenizer(attr_string, "^");

      attr_name += "=";
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(attr_name))
         {
            return str.substring(attr_name.length());
         }
      }
      return "";
   }


   private String toBase64Text(String in_string) throws IOException
   {
      String tmp = Util.toBase64Text(unicode2Byte(in_string));

      // MDAHSE, 2001-06-16
      // If we let the CrLf be included here we get problem
      // when reading the XML attribute back in the DocHTTPXMLReceive class.
      // Trust me...

      if (tmp.indexOf("\r\n")>0)
      {
         tmp = Str.replace(tmp, "\r\n", "");
      }
      return tmp;
   }


   /**
    * getRandomFileName (very probably) returns a unique filename
    */
   protected String getRandomFilename()
   {
      return DocmawUtil.getRandomFilename();
   }



   /**
    * This returns the temporary path ending with a system dependent path separator.
    */
   protected String getTmpPath() throws FndException
   {
      String      path = null;
      File        tmpDir;
      boolean     is_absolute;

      ASPManager mgr = getASPManager();
       
      //bug 58326
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("DEFAULTSYSCFGPATHFNDWEB", "Docman_Default_API.Get_Default_Value_", "OUT_1");
      cmd.addParameter("IN_STR1","DocIssue");
      cmd.addParameter("IN_STR1","SYSCFG_SHARED_PATH_FNDWEB");
      trans = mgr.perform(trans);

      String defaultPath = trans.getValue("DEFAULTSYSCFGPATHFNDWEB/DATA/OUT_1");

      
      //Bug Id 57006, Start 
      if (defaultPath == null)
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTDEFINED: The path for default value SYSCFG_SHARED_PATH_FNDWEB is not defined. You can define this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if (!(new File(defaultPath)).exists())
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTEXISTS: The path set for default value SYSCFG_SHARED_PATH_FNDWEB does not exist. You can change this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if ( ! defaultPath.endsWith( File.separator )  ) {
         defaultPath += File.separator;
      }
      //Bug Id 57006, End
      
      is_absolute = new File(defaultPath).isAbsolute();

      if(is_absolute)
      {
         /* For work with the existing hard coded path in docmawconfig.xml */
         path = defaultPath;
         if (DEBUG) Util.debug("DocSrv.getTmpPath - path 1 = " + path);
      }
      else
      {
         try
         {
            path = new File(mgr.getPhyPath(defaultPath)).getCanonicalPath();
            if (DEBUG) Util.debug("DocSrv.getTmpPath - path 2 = " + path);
         }
         catch(IOException e)
         {
            if (DEBUG) Util.debug("DocSrv.getTmpPath error: " + Str.getStackTrace(e));
            error(e);
         }
      }

      // Make sure it ends with a path separator

      path = path.charAt(path.length() - 1) == File.separatorChar ? path : path + File.separator;

      // Check that path exist
      File tmp_dir = new File(path);

      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 3 = " + path);

      if (!tmp_dir.exists())
      {
         createServerPath(path);
      }

      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 4 = " + path);

      return path;
   }

    //bug 58326, removed method for getting 2nd ftp path


   /**
    * This returns the login user
    */
   public String getLoginUser() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("NTSESSION", "Fnd_Session_Api.Get_Fnd_User", "OUT_1");
      trans = perform(trans);
      return trans.getValue("NTSESSION/DATA/OUT_1");
   }


   // Bug 57779, Start
   private boolean isUserDocmanAdministrator()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPCommand cmd = trans.addCustomFunction("DOCMAN_ADMIN", "Docman_Security_Util_API.Check_Docman_Administrator", "OUT_1");
      trans = mgr.perform(trans);
      return "TRUE".equals(trans.getValue("DOCMAN_ADMIN/DATA/OUT_1"));
   }
   // Bug 57779, End



   public String getDocumentState(String doc_class, String doc_no, String doc_sheet, String doc_rev) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETDOCSTATE", "Doc_Issue_API.Get_State", "OUT_1" );
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      trans = perform(trans);
      return trans.getValue("GETDOCSTATE/DATA/OUT_1");
   }

   /*
   * This method returns the file extension for given file type
   */
   public String getFileExtension(String file_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILEEXT", "Edm_Application_Api.Get_File_Extention", "OUT_1");
      cmd.addParameter("IN_STR1", file_type);
      trans = perform(trans);
      String retval = trans.getValue("GETFILEEXT/DATA/OUT_1");
      return retval;
   }


   public String getFileType(String file_ext) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_1");
      cmd.addParameter("IN_STR1", file_ext);
      trans = perform(trans);
      String file_type = trans.getValue("GETFILETYPE/DATA/OUT_1");
      return file_type;
   }


   public boolean checkExistTitle(String doc_no, String doc_class) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand("EXIST", "Doc_Title_API.Exist_Doc_Title");
      cmd.addParameter("OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      trans = perform(trans);

      return "TRUE".equals(trans.getValue("EXIST/DATA/OUT_1"));
   }


   //This method checks the existance of doc_types in edm_file_storage_tab
   public boolean checkExistDocType(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("EXISTEDMDOCFILE", "Edm_File_Storage_API.File_Exist", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);

      return "TRUE".equals(trans.getValue("EXISTEDMDOCFILE/DATA/OUT_1"));  //Bug Id 69087
   }

   //This method checks the original file name and view file name. If both are same returns TRUE.
   //Bug Id 65997, start
   public boolean checkValidViewCopy(String doc_class, String doc_no, String doc_sheet, String doc_rev) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("EXIST", "Edm_File_API.Check_Valid_View_Copy", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      trans = perform(trans);

      return "TRUE".equals(trans.getValue("EXIST/DATA/OUT_1"));
   }
   //Bug Id 65997, end

   /**
    * This method checks for certain things so that we don't do something "illegal"
    */
   protected void checkAccess(String doc_class,
                              String doc_no,
                              String doc_sheet,
                              String doc_rev,
                              String doc_type,
                              String action) throws FndException
   {
      if (DEBUG) Util.debug(this+ ": checkAccess() {");

      ASPManager mgr = getASPManager();

      if (mgr.isEmpty(doc_class) || mgr.isEmpty(doc_no) || mgr.isEmpty(doc_sheet) || mgr.isEmpty(doc_rev))
         throw new FndException(mgr.translate("DOCMAWDOCSRVNOEDMREFERENCE: This is not a valid document reference"));

      String edm_info  = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      String fnd_user  = getLoginUser();
      // Bug 57779, Start
      boolean docman_admin = isUserDocmanAdministrator();
      // Bug 57779, End
      
      // Added by Terry 20120924
      boolean original_ref_exist = findEdmFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      boolean checked_out = findCheckedOutFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, "");
      boolean checked_out_to_me = findCheckedOutFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, fnd_user);
      boolean checked_in = findCheckedInFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, "");
      // Added end
      
      if ("CHECKOUT".equals(action))
      {
         if (!"TRUE".equals(getStringAttribute(edm_info, "EDIT_ACCESS"))&& !(doc_type.equals("REDLINE"))) //Bug id 70512
         {
            String document_state = getDocumentState(doc_class, doc_no, doc_sheet, doc_rev);
            DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

            if ((document_state.equals (dm_const.doc_issue_approved)) || (document_state.equals (dm_const.doc_issue_obsolete)) || (document_state.equals (dm_const.doc_issue_released)))
               throw new FndException(mgr.translate("DOCMAWDOCSRVCANNOTEDITINAPPORREL: You cannot edit a document revision in state Approved, Released or Obsolete."));
            else
               throw new FndException(mgr.translate("DOCMAWDOCSRVNOEDITACCESSCHECKOUT: You don't have edit access to this document revision."));
         }

         // Modified by Terry 20120919
         // Original:
         // if (!"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFCHECKOUT: No files have been checked in for this document revision."));

         if (!original_ref_exist)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFCHECKOUT: No files have been checked in for this document revision."));
         
         // if ("ORIGINAL".equals(doc_type) && "TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")) && "FALSE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDINCHECKOUT: This document revision has been checked out by another user."));
         
         if ("ORIGINAL".equals(doc_type) && checked_out && !checked_out_to_me && !checked_in)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDINCHECKOUT: This document revision has been checked out by another user."));
         // Modified end
      }
      else if ("CHECKIN".equals(action) || "CREATENEW".equals(action))
      {
         // Modified by Terry 20120920
         // Original:
         // if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")) && "TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVALREADYCHECKIN: The files have already been checked in."));
         
         // if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")) && "TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVCHECKEDOUTANOTHERUSERCHECKIN: This document revision has been checked out by another user and can only be checked in by that user."));
         // Modified end
         
         if (!notCheckOriginalRefExist && "REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVCREATEREDLINENOORIGREF: An original file reference must exist in order to create a new comment file."));
         
         if (!"REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECK_IN_ACCESS")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOCHKINACCESSCHECKIN: You don't have sufficient access rights to check files in."));
      }
      // Added by Terry 20120924
      // Add CHECKINSEL access check
      else if ("CHECKINSEL".equals(action))
      {
         if ("ORIGINAL".equals(doc_type) && !checked_out && original_ref_exist)
            throw new FndException(mgr.translate("DOCMAWDOCSRVALREADYCHECKIN: The files have already been checked in."));
         
         if ("ORIGINAL".equals(doc_type) && checked_out && !checked_out_to_me && original_ref_exist)
            throw new FndException(mgr.translate("DOCMAWDOCSRVCHECKEDOUTANOTHERUSERCHECKIN: This document revision has been checked out by another user and can only be checked in by that user."));
         
         if (!"REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECK_IN_ACCESS")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOCHKINACCESSCHECKIN: You don't have sufficient access rights to check files in."));
      }
      // Added end
      else if ("UNDOCHECKOUT".equals(action))
      {
         // Modified by Terry 20120919
         // Original:
         // if (!"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDOUT: This document revision has not been checked out."));

         if (!checked_out)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDOUT: This document revision has not been checked out."));
         
         // if (!docman_admin) // Bug 57779
         // {
         //    if (!"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")))
         //       throw new FndException(mgr.translate("DOCMAWDOCSRVUNDOCHECKOUTBYANOTHERUSER: This document revision has been checked out by another user and can only be undone by that user."));
         // }
         
         if (!docman_admin)
         {
            if (!checked_out_to_me)
               throw new FndException(mgr.translate("DOCMAWDOCSRVUNDOCHECKOUTBYANOTHERUSER: This document revision has been checked out by another user and can only be undone by that user."));
         }
         // Modified end
         
      }
      else if ("VIEW".equals(action) || "PRINT".equals(action)) // Bug 57779
      {
         // Modified by Terry 20120919
         // Original:
         // if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
         //    throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGORIGINAL: No files have been checked in for this document revision."));
         
         if ("ORIGINAL".equals(doc_type) && !original_ref_exist)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGORIGINAL: No files have been checked in for this document revision."));
         // Modified end
         
         if ("VIEW".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "VIEW_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGVIEW: This document revision does not have a view file reference."));

         if ("REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "REDLINE_IN_REPOSITORY")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGREDLINE: This document revision does not have a comment file reference."));

         if (!"TRUE".equals(getStringAttribute(edm_info, "VIEW_ACCESS")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOVIEWACCESSVIEW: You don't have sufficient access rights to view this document revision."));
      }
      else if ("DELETE".equals(action))
      {
         if ("REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "REDLINE_IN_REPOSITORY")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFDELETEREDLINE: This document revision does not have a comment file reference."));

         if (!"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFDELETEORIGINAL: No files have been checked in for this document revision."));
      }
      // Added by Terry 20120924
      // Add DELETESEL access check
      else if ("DELETESEL".equals(action))
      {
         if (!original_ref_exist)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFDELETEORIGINAL: No files have been checked in for this document revision."));
         
         if (!checked_in)
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOCHECKINFILEREFDELETEORIGINAL: No files have Checked In status for this document revision."));
      }
      // Added end
      if (DEBUG) Util.debug(this+ ": checkAccess() }");
   }
   
   // Added by Terry 20120924
   // Find Edm File
   protected boolean findEdmFile(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("FINDEDMFILE", "Edm_File_API.Find_Edm_File", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);
      
      String edm_file = trans.getValue("FINDEDMFILE/DATA/OUT_1");
      
      if ("TRUE".equals(edm_file))
         return true;
      
      return false;
   }
   
   // Find Checked In Edm File
   protected boolean findCheckedInFile(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String checked_in_sign) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("FINDEDMFILECI", "Edm_File_API.Find_CheckIn_File", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(checked_in_sign))
         cmd.addParameter("IN_STR1", checked_in_sign);
      trans = perform(trans);
      
      String edm_file = trans.getValue("FINDEDMFILECI/DATA/OUT_1");
      
      if ("TRUE".equals(edm_file))
         return true;
      
      return false;
   }
   
   // Find Checked Out Edm File
   protected boolean findCheckedOutFile(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String checked_out_sign) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("FINDEDMFILECO", "Edm_File_API.Find_CheckOut_File", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(checked_out_sign))
         cmd.addParameter("IN_STR1", checked_out_sign);
      trans = perform(trans);
      
      String edm_file = trans.getValue("FINDEDMFILECO/DATA/OUT_1");
      
      if ("TRUE".equals(edm_file))
         return true;
      
      return false;
   }
   // Added end
   
   // Added by Terry 20120919
   protected void checkAccess(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no,
         String action) throws FndException
   {
      if (DEBUG)
         Util.debug(this + ": checkAccess() {");

      ASPManager mgr = getASPManager();

      if (mgr.isEmpty(doc_class) || mgr.isEmpty(doc_no) || mgr.isEmpty(doc_sheet) || mgr.isEmpty(doc_rev))
         throw new FndException(mgr.translate("DOCMAWDOCSRVNOEDMREFERENCE: This is not a valid document reference"));

      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
      String fnd_user = getLoginUser();
      // Bug 57779, Start
      boolean docman_admin = isUserDocmanAdministrator();
      // Bug 57779, End

      if ("CHECKOUT".equals(action))
      {
         if (!"TRUE".equals(getStringAttribute(edm_info, "EDIT_ACCESS")) && !(doc_type.equals("REDLINE"))) // Bug id 70512
         {
            String document_state = getDocumentState(doc_class, doc_no, doc_sheet, doc_rev);
            DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

            if ((document_state.equals(dm_const.doc_issue_approved)) || (document_state.equals(dm_const.doc_issue_obsolete)) || (document_state.equals(dm_const.doc_issue_released)))
               throw new FndException(mgr.translate("DOCMAWDOCSRVCANNOTEDITINAPPORREL: You cannot edit a document revision in state Approved, Released or Obsolete."));
            else
               throw new FndException(mgr.translate("DOCMAWDOCSRVNOEDITACCESSCHECKOUT: You don't have edit access to this document revision."));
         }

         if (!"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFCHECKOUT: No files have been checked in for this document revision."));

         if ("ORIGINAL".equals(doc_type) && "TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")) && "FALSE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDINCHECKOUT: This document revision has been checked out by another user."));
      }
      else if ("CHECKIN".equals(action) || "CREATENEW".equals(action))
      {
         if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")) && "TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVALREADYCHECKIN: The files have already been checked in."));

         if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")) && "TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVCHECKEDOUTANOTHERUSERCHECKIN: This document revision has been checked out by another user and can only be checked in by that user."));

         if (!notCheckOriginalRefExist && "REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVCREATEREDLINENOORIGREF: An original file reference must exist in order to create a new comment file."));

         if (!"REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "CHECK_IN_ACCESS")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOCHKINACCESSCHECKIN: You don't have sufficient access rights to check files in."));
      }
      else if ("UNDOCHECKOUT".equals(action))
      {
         if (!"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOTCHECKEDOUT: This document revision has not been checked out."));

         if (!docman_admin) // Bug 57779
         {
            if (!"TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")))
               throw new FndException(mgr.translate("DOCMAWDOCSRVUNDOCHECKOUTBYANOTHERUSER: This document revision has been checked out by another user and can only be undone by that user."));
         }
      }
      else if ("VIEW".equals(action) || "PRINT".equals(action)) // Bug 57779
      {
         if ("ORIGINAL".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGORIGINAL: No files have been checked in for this document revision."));

         if ("VIEW".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "VIEW_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGVIEW: This document revision does not have a view file reference."));

         if ("REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "REDLINE_IN_REPOSITORY")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFVIEWINGREDLINE: This document revision does not have a comment file reference."));

         if (!"TRUE".equals(getStringAttribute(edm_info, "VIEW_ACCESS")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOVIEWACCESSVIEW: You don't have sufficient access rights to view this document revision."));
      }
      else if ("DELETE".equals(action))
      {
         if ("REDLINE".equals(doc_type) && !"TRUE".equals(getStringAttribute(edm_info, "REDLINE_IN_REPOSITORY")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFDELETEREDLINE: This document revision does not have a comment file reference."));

         if (!"TRUE".equals(getStringAttribute(edm_info, "ORIGINAL_REF_EXIST")))
            throw new FndException(mgr.translate("DOCMAWDOCSRVNOFILEREFDELETEORIGINAL: No files have been checked in for this document revision."));
      }
      if (DEBUG)
         Util.debug(this + ": checkAccess() }");
   }
   // Added end


   protected String generateLocalFileName(String doc_class,
                                          String doc_no,
                                          String doc_sheet,
                                          String doc_rev,
                                          String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GENFILENAME", "Edm_File_Api.Generate_Local_File_Name__", "OUT_1");
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", "");
      trans = perform(trans);
      return trans.getValue("GENFILENAME/DATA/OUT_1");
   }
   
   // Added by Terry 20120919
   protected String generateLocalFileName(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GENFILENAME", "Edm_File_Api.Generate_Local_File_Name__", "OUT_1");
      cmd.addParameter("IN_STR1", "");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      cmd.addParameter("IN_STR1", "");
      trans = perform(trans);
      return trans.getValue("GENFILENAME/DATA/OUT_1");
   }
   // Added end


   protected byte[] unicode2Byte(String str)
   {
       byte[] byte_temp = new byte[str.length()*2];
       int j = 0;
       for (int i = 0; i < str.length(); i++)
       {
         int iChar = (int)str.charAt(i);
         if (iChar > 255)
         {
           byte_temp[j++] = (byte) ( (iChar & 0xFF00) >> 8);
           byte_temp[j++] = (byte) (iChar & 0xFF);
         }
         else
         {
           byte_temp[j++] = (byte)0;
           byte_temp[j++] = (byte)iChar;
         }
       }
       return byte_temp;
    }


   private String addTempPath(String fileName,String filePath){// this filePath parameter prevents many database transactions: bakalk
      String returnFileName = filePath;

      if ( ! returnFileName.endsWith( File.separator )  ) {
         returnFileName += File.separator;
      }
      return (returnFileName + fileName);
  }



   /**
    * Dont add a new field unless you *really* need it.
    * Reuse the fields that already exist here.
    */
   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("DOCSRV_HEAD");

      headblk.addField("IN_STR1");
      headblk.addField("STR_OUT");
      headblk.addField("INOUT_STR1");
      headblk.addField("IN_NUM","Number");
      headblk.addField("OUT_1");
      headblk.addField("OUT_2");
      headblk.addField("OUT_3");
      headblk.addField("OUT_4");
      headblk.addField("OUT_5");
      //for none ie

      headblk.addField("IN_1");
   }


   private String addKeys(String info,
                          String doc_class,
                          String doc_no,
                          String doc_sheet,
                          String doc_rev,
                          String doc_type)
   {
      StringBuffer strBuff = new StringBuffer(info);
      strBuff.append("DOC_CLASS="+doc_class+"^");
      strBuff.append("DOC_NO="+doc_no+"^");
      strBuff.append("DOC_SHEET="+doc_sheet+"^");
      strBuff.append("DOC_REV="+doc_rev+"^");
      if (!"".equals(doc_type))
         strBuff.append("DOC_TYPE="+doc_type+"^");
      return strBuff.toString();
   }



   private String addKeys(String info,
                          String doc_class,
                          String doc_no,
                          String doc_sheet,
                          String doc_rev)
   {

      return addKeys(info,doc_class,doc_no,doc_sheet,doc_rev,"");
   }

  //-------------------------------- Add comment file starts --------------------
   protected String[] checkInCommentFile(String doc_class,
                                         String doc_no,
                                         String doc_sheet,
                                         String doc_rev,
                                         String file_type,
                                         String unique_file_id,
                                         String original_file_name) throws FndException // Bug ID 89622
   {
      if (DEBUG) Util.debug(this+": checkInCommentFile() {");

      ASPManager mgr = getASPManager();
      // Bug ID 89622, Start
      String edm_rep_info;
      String attr;
      // Bug ID 89622, Finish

      boolean refExist = checkFileReferenceExist(doc_class, doc_no, doc_sheet, doc_rev, "REDLINE"); 
      
      // Bug ID 89622, Removed file reference creation logic
      
      //bug 68527, starts
      this.selected_doc_types = new String[1];
      selected_doc_types[0] = "REDLINE";
      //bug 68527, ends
      
      // Bug ID 89622, Start
      if (refExist) 
      {          
         // If new comment file added over existing, delete the existing from repository (ex: when file types are changed)
         deleteFileInRepository(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE");
      
         attr = "";
         
         // If new comment file added over existing reference, modify original file name and type
         modifiyFileReferenceFileType(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE",file_type);
      }
      else
      {
         // Create new file reference         
         createFileReference(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE",file_type,null,1,DocmawUtil.getFileName(original_file_name)); //Bug Id 70484, 89622                            
      }
      
      // Updating checked in sign and user file name
      attr = "";
      if (refExist) 
      {
         attr += "USER_FILE_NAME" + String.valueOf((char)31) + DocmawUtil.getFileName(original_file_name) + String.valueOf((char)30);                           
      }     
      // Bug ID 89622, Finish
      
      attr += "CHECKED_IN_SIGN" + String.valueOf((char)31) +  getLoginUser()+ String.valueOf((char)30);//mgr.getFndUser()
      attr += "CHECKED_IN_DATE" + String.valueOf((char)31) + getDbDate() + String.valueOf((char)30);
         
      this.updateFileReference(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE",attr);         
      
      // Bug ID 89622, Start
      // Re-fetch repository information (required for newly created file references)
      edm_rep_info = getEdmRepositoryInformation(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE");            
      
      String[] newRepositoryFileNames = new String[1];
      newRepositoryFileNames[0] = getFullRemoteFileName(doc_class,doc_no,doc_sheet,doc_rev,"REDLINE");
      String[] local_file_names2 = generateLocalFileNames(unique_file_id, newRepositoryFileNames);      

      // Put the new file into repository
      putFilesToRepository(addKeys(edm_rep_info,doc_class,doc_no,doc_sheet,doc_rev), local_file_names2, newRepositoryFileNames);  //Bug Id 69087
      // Bug ID 89622, Finish
      
      String[] localFileNames = new String[1];
      localFileNames[0] = original_file_name;
      //update original file name

      if (DEBUG) Util.debug(this+": checkInCommentFile() }");

      return localFileNames;

   }//end of checkInCommentFile

   protected String getDbDate()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPQuery query = trans.addQuery("GETDATE", "SELECT SYSDATE CURRDATE FROM DUAL");
      trans = mgr.perform(trans);
      
      return  trans.getValue("GETDATE/DATA/CURRDATE");
      
   }


   protected String getRemoteNameOnly(String doc_class,
                                      String doc_no,
                                      String doc_sheet,
                                      String doc_rev)
   {

      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("REMOTEFILENAME", "Edm_File_API.Generate_File_Name___", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = mgr.perform(trans);
      return trans.getValue("REMOTEFILENAME/DATA/OUT_1"); 
   }



   protected boolean checkFileReferenceExist(String doc_class,
                                             String doc_no,
                                             String doc_sheet,
                                             String doc_rev,
                                             String doc_type)
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("CHECKEXIST", "Edm_File_API.Check_Exist", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = mgr.perform(trans);
      String result = trans.getValue("CHECKEXIST/DATA/OUT_1");

      return "TRUE".equals(result)?true:false;  
   }// end of checkFileReferenceExist

   // Added by Terry 20120919
   protected boolean checkFileReferenceExist(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no)
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();

      cmd = trans.addCustomFunction("CHECKEXIST", "Edm_File_API.Check_Exist", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      trans = mgr.perform(trans);
      String result = trans.getValue("CHECKEXIST/DATA/OUT_1");

      return "TRUE".equals(result) ? true : false;
   }
   // Added end

   protected void deleteFileReference(String doc_class,
                                      String doc_no,
                                      String doc_sheet,
                                      String doc_rev,
                                      String doc_type) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("DELFILEREFS", "Edm_File_Api.Delete_File_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      trans = perform(trans);
   }
   
   // Added by Terry 20120919
   protected void deleteFileReference(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("DELFILEREFS", "Edm_File_Api.Delete_File_");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      trans = perform(trans);
   }
   // Added end

   
   protected void createFileReference(String doc_class,
                                      String doc_no,
                                      String doc_sheet,
                                      String doc_rev,
                                      String doc_type, ////file_no_              IN     NUMBER,
                                      String file_type,
                                      String local_path,
                                      int    create_new_file_name,
                                      String original_file_name ) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("CREATEREFERENCE", "Edm_File_Api.Create_File_Reference");
      cmd.addParameter("OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", local_path);
      cmd.addParameter("IN_NUM",  ""+create_new_file_name);
      cmd.addParameter("IN_STR1", original_file_name);
      trans = perform(trans);
   }
   
   // Added by Terry 20120919
   protected void createFileReference(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type, ////file_no_              IN     NUMBER,
         String file_no,
         String file_type,
         String local_path,
         int    create_new_file_name,
         String original_file_name ) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("CREATEREFERENCE", "Edm_File_Api.Create_File_Reference");
      cmd.addParameter("OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", local_path);
      cmd.addParameter("IN_NUM", "" + create_new_file_name);
      cmd.addParameter("IN_STR1", original_file_name);
      trans = perform(trans);
   }
   // Added end


   protected String getRemotePath(String doc_class) throws ifs.fnd.service.FndException
   {
      //get location name
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("LOCATIONNAME", "Edm_Location_API.Get_Current_Location", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      trans = perform(trans);
      String remoteLocationName = trans.getValue("LOCATIONNAME/DATA/OUT_1");

      trans.clear();
      cmd = trans.addCustomFunction("LOCATIONTYPE", "Edm_Location_API.Get_Location_Type", "OUT_1");
      cmd.addParameter("IN_STR1", remoteLocationName); //
      trans = perform(trans);
      String remoteLocationType = trans.getValue("LOCATIONTYPE/DATA/OUT_1");

      if ("3".equals(remoteLocationType))//database type
         return ""; // no need a path
      else{
         // path_ := Edm_Location_API.Get_Path(location_name_);
         trans.clear();
         cmd = trans.addCustomFunction("LOCATIONPATH", "Edm_Location_API.Get_Path", "OUT_1");
         cmd.addParameter("IN_STR1", remoteLocationName);
         trans = perform(trans);
         return trans.getValue("LOCATIONPATH/DATA/OUT_1");
      }
   }//end of getRemotePath


   protected String getFullRemoteFileName(String doc_class,
                                          String doc_no,
                                          String doc_sheet,
                                          String doc_rev,
                                          String doc_type) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("FULLREMOTEFILENAME" ,  "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }

      trans = perform(trans);

      return trans.getValue("FULLREMOTEFILENAME/DATA/OUT_1");

   }
   
   // Added by Terry 201201919
   protected String getFullRemoteFileName(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("FULLREMOTEFILENAME", "Edm_File_Api.Get_Remote_File_Name", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);

      trans = perform(trans);

      return trans.getValue("FULLREMOTEFILENAME/DATA/OUT_1");

   }
   // Added end

   protected void updateFileReference(String doc_class,
                                      String doc_no,
                                      String doc_sheet,
                                      String doc_rev,
                                      String doc_type,
                                      String attr) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("MODIFYY" ,  "Edm_File_Api.Modify");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      cmd.addParameter("IN_STR1", attr);

      trans = perform(trans);

   }
   
   // Added by Terry 201201919
   protected void updateFileReference(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no,
         String attr) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("MODIFYY", "Edm_File_Api.Modify");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM", file_no);
      cmd.addParameter("IN_STR1", attr);

      trans = perform(trans);
   }
   // Added end
   
   // Bug ID 89622, Start
   protected void modifiyFileReferenceFileType(String doc_class,
                                               String doc_no,
                                               String doc_sheet,
                                               String doc_rev,
                                               String doc_type,
                                               String file_type) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      
      cmd = trans.addCustomCommand("MODIFYFILETYPE", "Edm_File_API.Modify_File_Reference");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      
      cmd.addParameter("IN_STR1", file_type);

      trans = perform(trans);
   }   
   // Bug ID 89622, Finish

   // Added by Terry 20120919
   protected void modifiyFileReferenceFileType(String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type,
         String file_no,
         String file_type) throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      cmd = trans.addCustomCommand("MODIFYFILETYPE", "Edm_File_API.Modify_File_Reference");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM", file_no);

      cmd.addParameter("IN_STR1", file_type);

      trans = perform(trans);
   }
   // Added end
   
   // Added by Terry 20140730
   private String getDocIssueKeyRef(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      return doc_class + IfsNames.textSeparator +
             doc_no + IfsNames.textSeparator +
             doc_sheet + IfsNames.textSeparator +
             doc_rev + IfsNames.textSeparator;
   }
   
   // Get doc_code for doc issue
   private String getDocCode(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      String doc_code;
      String doc_code_key = "__" + getDocIssueKeyRef(doc_class, doc_no, doc_sheet, doc_rev) + "_DOC_CODE";
      
      doc_code = ctx.findGlobal(doc_code_key);
      if (mgr.isEmpty(doc_code))
      {
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         
         ASPCommand cmd = trans.addCustomFunction("GETDOCCODE", "Doc_Issue_API.Get_Doc_Code", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         trans = mgr.perform(trans);
         doc_code = trans.getValue("GETDOCCODE/DATA/OUT_1");
         doc_code = mgr.isEmpty(doc_code) ? "" : doc_code;
         ctx.setGlobal(doc_code_key, doc_code);
      }
      return doc_code;
   }
   // Added end
   
   //-------------------------------- Add comment file ends --------------------

   private String handleBackSlash(String input){
      String output = "";
      for (int k=0;k<input.length();k++)
         if("\\".equals(input.charAt(k)+""))
            output += "\\\\";
         else
            output += input.charAt(k);
      return output;
   }


    public static String[] removeRepeations(String[] arr)
   {
      int[] markRemove = new int[arr.length];

      for (int a=0;a<arr.length-1;a++) {
         if (markRemove[a] == 1) { //if marked before this
            continue;
         }
		   for (int b=a+1;b<arr.length;b++) {
            if (arr[a].equals(arr[b])) {
               markRemove[b] = 1;
            }
         }
      }

      int nRemoves = 0;

      for (int b=0;b<arr.length;b++)
         if (markRemove[b]==1)
            nRemoves++;

      String afterRemoving[] = new String[arr.length-nRemoves];
      int nRemoved = 0;

      for (int b=0;b<arr.length;b++)
      {
         if (markRemove[b]==0) {
            afterRemoving[b-nRemoved] = arr[b];
         }else
            nRemoved++;
      	
      }
      return afterRemoving;
   }



    protected void putFileIntoArchive(String edm_rep_info,
                                   String file_name) throws FndException
   {
      ASPManager mgr = getASPManager();

      String server_view_file_name = "";
      String server_file_name = getStringAttribute(edm_rep_info, "REMOTE_FILE_NAME").toUpperCase();// ftp file name??

      if (!Str.isEmpty(view_file_ext))
         server_view_file_name = (server_file_name.substring(0,server_file_name.lastIndexOf(".")+1) + view_file_ext).toUpperCase();

      if (!Str.isEmpty(redline_file_ext))
         server_file_name = (server_file_name.substring(0, server_file_name.lastIndexOf(".")+1) + redline_file_ext).toUpperCase();

      if ("2".equals(getStringAttribute(edm_rep_info, "LOCATION_TYPE")))
      {
         DocmawFtp ftp = new DocmawFtp();

         String ftp_user = getStringAttribute(edm_rep_info, "LOCATION_USER");
         String ftp_address = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
         String ftp_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
         String ftp_port = getStringAttribute(edm_rep_info, "LOCATION_PORT");
         String full_local_file_name = getTmpPath() + file_name;
         int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);

         // View file also?
         String full_local_view_file_name = "";
         if (!Str.isEmpty(view_file_ext))
            full_local_view_file_name = getTmpPath() + file_name + "_view_" + view_file_ext;

         if (ftp.login(ftp_address, ftp_user, ftp_password, port_no))
         {
            ftp.putBinaryFile(full_local_file_name, server_file_name);

            // If an error occured, what happens?
            // DocmawFtp does not always throw FndExceptions
            // I suppose we have to enhance DocmawFtp then. Later...

            // View file also?
            if (!Str.isEmpty(view_file_ext))
               ftp.putBinaryFile(full_local_view_file_name, server_view_file_name);

            // Remove old view file if we have changed file type of it.
            if (remove_old_view_file)
            {
               String old_server_view_file_name = (server_view_file_name.substring(0, server_view_file_name.lastIndexOf(".")+1) + old_view_file_ext).toUpperCase();
               ftp.deleteFile(old_server_view_file_name);
            }

            // Remove old Redline file if we have changed file type of it.
            if (remove_old_redline_file)
            {
               String old_server_redline_file_name = (server_file_name.substring(0,server_file_name.lastIndexOf(".")+1) + old_redline_file_ext).toUpperCase();
               ftp.deleteFile(old_server_redline_file_name);
            }

            ftp.logoff();

            deleteFile(full_local_file_name);

            // View file also?
            if (!Str.isEmpty(view_file_ext))
               deleteFile(full_local_view_file_name);
         }
         else
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED4: Login to FTP server &1, port: &2 with login name &3 failed.", ftp_address, ftp_port, ftp_user));
         }
      }
      else
      {
         // This is a share file location.
         throw new FndException(mgr.translate("DOCMAWDOCSRVSHAREFILENOTINFTP2: The document is not in a FTP server and the web client does not support that."));
      }
   }


   private String replaceRedlineExt(String docList, String newExt)
   {
      if (DEBUG) Util.debug("replaceRedlineExt {");
	   String firstPart  = docList.substring(0,docList.indexOf("REDLINE_EXTS="));
	   String secondPart = docList.substring(docList.indexOf("REDLINE_EXTS=")+"REDLINE_EXTS=".length(),docList.length());
	   secondPart = secondPart.substring(secondPart.indexOf("^"));
	   String middlePart = "REDLINE_EXTS=" + newExt;
      if (DEBUG) Util.debug("replaceRedlineExt }");
	   return firstPart + middlePart + secondPart;
		
	}

   //Bug Id 72460, start
   protected void undoStateChange(String doc_class,
				  String doc_no,
				  String doc_sheet,
				  String doc_rev,
				  String doc_type,
				  String status) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("ROLLBACKSTATES", "Edm_File_Api.Rollback_All_Files_Status");
      cmd.addParameter("IN_1", doc_class);
      cmd.addParameter("IN_1", doc_no);
      cmd.addParameter("IN_1", doc_sheet);
      cmd.addParameter("IN_1", doc_rev);
      cmd.addParameter("IN_1", doc_type);
      cmd.addParameter("IN_1", status);
      cmd.addParameter("IN_1", "");

      trans = mgr.perform(trans);

   }

   protected void removeLastHistoryLine(String doc_class,
				        String doc_no,
				        String doc_sheet,
				        String doc_rev,
				        String status) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("REMOVEHISTORY", "Document_Issue_History_Api.Remove_Last_Line_");
      cmd.addParameter("IN_1", doc_class);
      cmd.addParameter("IN_1", doc_no);
      cmd.addParameter("IN_1", doc_sheet);
      cmd.addParameter("IN_1", doc_rev);
      cmd.addParameter("IN_1", status);

      trans = mgr.perform(trans);
   }

   protected void removeLastSimillarHistoryLines(String doc_class,
						 String doc_no,
						 String doc_sheet,
						 String doc_rev,
						 String status) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("REMOVESIMILLARHISTORY", "Document_Issue_History_Api.Remove_Last_Simillar_Lines_");
      cmd.addParameter("IN_1", doc_class);
      cmd.addParameter("IN_1", doc_no);
      cmd.addParameter("IN_1", doc_sheet);
      cmd.addParameter("IN_1", doc_rev);
      cmd.addParameter("IN_1", status);

      trans = mgr.perform(trans);
   }

   protected void removeFileReference(String doc_class,
				      String doc_no,
				      String doc_sheet,
				      String doc_rev,
				      String doc_type,
				      String file_no ) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("REMOVEREDLINEREF", "Edm_File_Api.Delete_File_Ref_No_History");
      cmd.addParameter("IN_1", doc_class);
      cmd.addParameter("IN_1", doc_no);
      cmd.addParameter("IN_1", doc_sheet);
      cmd.addParameter("IN_1", doc_rev);
      cmd.addParameter("IN_1", doc_type);
      cmd.addParameter("IN_1", file_no);

      trans = mgr.perform(trans);
   }

   protected void deleteTransmittalDoc(String doc_class,
				     String doc_no,
				     String doc_sheet,
				     String doc_rev,
				     String transmittal_id) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("DELETETRANSMITTALDOC", "DOC_TRANSMITTAL_ISSUE_API.Delete_Transmittal_Doc");
      cmd.addParameter("IN_1", transmittal_id);
      cmd.addParameter("IN_1", doc_class);
      cmd.addParameter("IN_1", doc_no);
      cmd.addParameter("IN_1", doc_sheet);
      cmd.addParameter("IN_1", doc_rev);
      
      trans = mgr.perform(trans);
   }

   protected void rollbackTransmittalStatus(String transmittal_id,
					    String to_state) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd = trans.addCustomCommand("ROLLBACKTRANSMITTAL", "DOCUMENT_TRANSMITTAL_API.Rollback_Transmittal_State");
      cmd.addParameter("IN_1", transmittal_id);
      cmd.addParameter("IN_1", to_state);
      
      trans = mgr.perform(trans);
   }
   //Bug Id 72460, end

   //Bug Id 80834, Start - Compare the file sizes in the database and the client
   private boolean compareFileSize(String file_name,
	                         String doc_class,
                                 String doc_no,
                                 String doc_sheet,
                                 String doc_rev,
                                 String doc_type) throws FndException
   {
      if (DEBUG) Util.debug(this+": compareFileSize() {");
      ASPManager mgr = getASPManager();
      String databaseFileSize = "";
      
      //Get database file size
      trans.clear();
      cmd = trans.addCustomFunction("GETFILESIZE",  "EDM_FILE_STORAGE_API.Get_File_Size", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      //Bug Id 86204, Start
      if (!mgr.isEmpty(this.file_no)) {
         cmd.addParameter("IN_NUM", file_no);
      }
      //Bug Id 86204, End
      trans = perform(trans);

      databaseFileSize = trans.getValue("GETFILESIZE/DATA/OUT_1");
      if (DEBUG) Util.debug(this + ": 001, Database file size " + databaseFileSize);  
      
      File file = new File(file_name);
      if (DEBUG) Util.debug(this + ": 002, local file size " + file.length());
      if (file.exists() && file.isFile()) 
      {
	   return true;    
      }

      return false;
   }
   //Bug Id 80834, End

}
