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
 *  File                        : EdmMacro.java
 *  Description                 : Generates HTML GUI for checkin/out of document files
 *  Notes                       :
 *  Other Programs Called       : Inherits from DocSrv where most of the database and file handling is done
 *  ----------------------------------------------------------------------------
 *  Modified    :
 *      THABLK   2001-04-23   Created
 *      MDAHSE   2001-05-04   Removed all occurance of sApplicationSettings
 *      MDAHSE   2001-05-17   Clean up of code
 *      THABLK   2001-05-24   Now multiple selected rows can call EdmMacro.
 *      DIKALK   2001-06-06   Support for applying same or different macros for the multiple files selected.
 *      BAKALK   2001-06-08   Adjusted createNewDocument being called.
 *      MDAHSE   2001-06-26   Some changes here and there. Search for my signature.
 *      THABLK   2001-06-29   Fixed wrong translation strings.
 *      THABLK   2001-07-02   Made the Create New work when there are no macro.
 *      THABLK   2001-07-10   Call ID - 66883 Made Redline file delete work properly.
 *      DIKALK   2001-07-10   Call ID - 66880 macro support for multiple checkin acion.
 *      PRSALK   2001-08-07   Call ID - 62828 Added Copy Original Document functionality.
 *      DIKALK   2001-10-11   Call ID - 70399 Perform action when no macros, without prompting the Ok button
 *      DIKALK   2001-10-16   Call ID - 70399 Recorrected a flaw in the fix on 2001-10-11
 *      SHTOLK   2001-03-18   Bug  ID - 27767 Modified perform to copy the file when creating a new revision.
 *      SHTHLK   2002-07-01   Bug  ID - 30018 Modified okFind() to stop the deadlock.
 *      BAKALK   2002-10-14   Bug  Id - 30724 Made modification to run macro when trying "Edit Document" on a document which is already checked out.
 *      PRSALK   2002-11-06   Added docSheet.
 *      SHTHLK   2002-11-07   Bug  Id - 30313 Called SetIsSendMail method in IFSCliMgrOCX when the
 *                            file action is "SENDMAIL" to prevent the Redline file launching.
 *      BAKALK   2002-11-15   Added new process "COPYTITLE".
 *      SHTHLK   2002-12-09   Bug  Id - 33385 Added new translation string (id_vc1021) to the IFSCliMgrOCX.
 *      BAKALK   2002-12-27   Merged 2002-2 SP3 bug fixes.
 *      DIKALK   2003-02-02   Restructured some of the code to support multirow file actions and the new
 *                            extented edm functionality
 *      DIKALK   2003-02-24   Changed the ocx translation constants into static variables. These variables
 *                            are now initialized in the constructor.
 *      DIKALK   2003-02-25   Removed initialization of ocx translation constants in the constructor since
 *                            the Web Client enters a state of PANIC when accessing mgr in the constructor.
 *      DIKALK   2003-02-25   Translation constants are now initialized in preDefine()
 *      SHTHLK   2003-03-11   Bug  Id - 36123 Added new translation strings (id_vc1019),(id_vc1020) to the IFSCliMgrOCX.
 *      BAKALK   2003-03-28   Implemented Copy File to.. and Send Mail.. for XEDM.
 *      BAKALK   2003-03-28   View with External Application.. for XEDM.
 *      NISILK   2003-04-01   Added methods doReset and clone
 *      DIKALK   2003-04-02   Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
 *      DIKALK   2003-04-03   Removed all unused objects in preDefine();
 *      MDAHSE   2003-04-15   Added getStackTrace() to make it easier to print full error messages.
 *      DIKALK   2003-04-23   Noticed that the clone method was resetting the wrong attributes. Fixed it!
 *      INOSLK   2003-04-26   Added method createNewDoc and relevant canges to support 'Create New Document'.
 *      INOSLK   2003-04-28   Changes in methods 'getActionMacroProcess', 'getContents' and 'createNewDoc'.
 *      NISILK   2003-05-02   Changed methods importFile, generatePageHeader, translateFileAction to support XEDM file import
 *      THWILK   2003-05-09   Bug  Id - 37037 Added a new feature to the link so that a certain document can be requested by specifying the status as RELEASED=TRUE or FALSE.
 *      NISILK   2003-05-28   Modified viewDocument() to support multi row view document
 *      NISILK   2003-05-29   Modified method printDocument() to support multi row print document
 *      NISILK   2003-06-13   Modified method prepareClientAction to avoid the unnecessary submit to
 *                            EdmMacro.page when doing viewDocument. Modified methods viewDocument and printDocument.
 *      BAKALK   2003-07-10   Modified createNewDoc.
 *      NISILK   2003-07-18   Added method checkString to check whether a String contains characters (") and (').
 *                            Modified method run to get the proper string for the err_msg using checkString method.
 *      NISILK   2003-07-31   Implemented multo row copy file to
 *      INOSLK   2003-08-07   SP4 Merge: Bug IDs 30313,33385,36123,30724.
 *      INOSLK   2003-08-08   Increased the no of array elements for translatedStrings.
 *      DIKALK   2003-08-12   Removed SP4 merge of bug 30313, since this bug did not exist in the project code
 *      NISILK   2003-08-14   call id 100766 Modified method prepareFileAction to merge Bug Id 37037
 *      NISILK   2003-08-18   Added doc_sheet as a parameter to method getRevision in prepareFileAction().
 *      SHTHLK   2003-08-17   Call Id 92264, Modified method performFileAction & added new method setNextDocument.
 *      DIKALK   2003-08-25   Fixed call 100960: Deleting documents and document files
 *      DIKALK   2003-08-25   Documented the code a bit more.
 *      NISILK   2003-09-02   Fixed call 95532. Modified method prepareFileAction. Modified method getContents().*
 *      DIKALK   2003-09-16   Moved translation constants for ocx to DocmawConstants.java
 *      INOSLK   2003-09-25   Call ID 103727 - Modified method importFile.
 *      DIKALK   2003-09-30   Added client action "DELETE_CHECKED_IN_FILES" to delete files only after checkin
 *                            has sucessfully completed.
 *      DIKALK   2003-10-07   Call 105917. An error message will be shown if the ocx could not be downloaded properly
 *      DIKALK   2003-10-07   Fixed a few bugs in the layout... and then changed it entirely to look more consistent with
 *                            the web client.
 *      DIKALK   2003-10-11   Call 106698, Changed the code a bit so that multi-row file operations continue
 *                            even if there was a server error
 *      DIKALK   2003-10-17   Added checks to ensure that that the local checkout path exists
 *      DIKALK   2003-10-18   Added try-catch around javascript method opener.refreshParent() in case a
 *                            parent window does not exist or does not support the refreshParent() method
 *      SHTOLK   2003-10-21   Call Id 108858, Modified getCopyToDir() & getActionMacroProcess()
 *                            to remove macro for the process 'GETCOPYTODIR'.
 *      DIKALK   2003-10-21   Overrode ASPPage.useLovContextSlot() to solve logout problem
 *      DIKALK   2004-03-25   SP1 merge. Bug Id 43366. No need to perform security checks during file import.
 *      DIKALK   2004-06-17   Changed implementation of Send Mail
 *      DIKALK   2004-08-03   Added new file action: LAUNCH_LOCAL_FILE
 *      DIKALK   2004-10-01   Merged Bug Id 45861.
 *      DIKALK   2004-10-15   Merged Bug Id 45980.
 *      DIKALK   2004-10-18   Merged Bug Id 45979.
 *      DIKALK   2004-11-08   Modified client code to handle exceptions raised by the OCX
 *      DIKALK   2004-11-10   Fixed call 119493. Added getServerUrl() as a temporary solution.
 *      BAKALK   2004-11-18   Used manager's getApplicationAbsolutePath() to get full Url to be submitted to OCX.
 *      BAKALK   2005-01-18   Fixed the call 121008.
 *      BAKALK   2005-01-25   Fixed the call 121031.
 *      SUKMLK   2005-02-01   Fixed call 120947.
 *      SUKMLK   2005-02-01   Fixed call 120961.
 *	     KARALK   2005-04-28   Fixed call 122411.
 *      SUKMLK   2005-05-27   Fixed call 124282, added a "CHECKFILEEXISTS" condition for the client actions.
 *      SUKMLK   2005-05-30   For call 124282, changed .replace to .replaceAll since .replace for char sequences
 *                            is new. (starts with java 1.5).
 *      SHTHLK   31-05-2005   Fixed Call 124228, Modified printDocument()
 *      SUKMLK   2005-06-23   Made changes to call 124282 to which enabled view, copy file, and print to work.
 *      SUKMLK   2005-07-13   Fixed call 125662. Did lots of stuff. Changed checkInDocument, added some methods
 *      SUKMLK   2005-07-25   Fixed call 125170. Made some changes to support structure.
 *      DIKALK   2005-07-27   Added support for configuring files names for normal documents.
 *      KARALK   2005-07-28   merged the bug 49654.
 *      SHTHLK   2005-08-01   Merged bug Id 51471. Added script functions removePageIDCookies() and trim_spaces(),
 *      SHTHLK                to search and remove cookies prefixed with fndwebPageID_.
 *      KARALK   2005-08-01   merged bug id 52230.
 *      AMNALK   2005-10-19   Fixed Call 127404. 
 *      DIKALK   2005-12-08   Added checks to make sure that a duplicate document number was not passed in during fileimport
 *      DIKALK   2005-12-12   Ensured checkAccess is performed on Structure documents, before trasfering to DocStructure.page
 *      SUKMLK   2005-12-20   Fixed call 126601.
 *      SUKMLK   2006-01-17   Fixed call 130815.
 *      SUKMLK   2006-01-19   Fixed call 131124.
 *      DIKALK   2006-02-09   Implemented Checkin/Checkout reports for structure operations
 *      BAKALK   2006-02-11   Fixed the call: 133095.
 *      NIJALK   2006-05-29   Bug 56572, Modified prepareFileAction().
 *      BAKALK   2006-05-30   Bug 58326, Removing 2nd Ftp.
 *      DIKALK   2006-05-31   Bug 57779, Replaced use of appowner with Docman Administrator
 *      KARALK   2006-06-02   Bug 57041, corrected in initEdmMacro().  
 *      SHAFLK   2006-07-13   Bug 58793, Modified formatErrorMessage().
 *      NIJALK   2006-07-20   Bug 54793, Checked whether local checkout path was read/write protected before create,view,checkout or print allowed.
 *      NIJALK   2006-07-25   Bug 55611, Modified getContents().
 *      NIJALK   2006-08-08   Bug 55611, Modified getContents().
 *      CHOLDK   2006-08-08   Bug 59432, Modified getContents().   
 *      NIJALK   2006-10-04   Bug 60851, Modified getContents().
 *      BAKALK   2007-02-19   Document Transmittals: default checkout path is overridden for CHECKOUT_TO_GIVEN_PATH. window.close() put in a try clause.
 *      BAKALK   2007-03-01   Document Transmittals: Added functionality of "Add comment files from Transmittals".
 *      BAKALK   2007-03-27   Document Transmittals: Added functionality of "Add Report to Transmittals".
 *      BAKALK   2007-03-30   Document Transmittals: Added functionality of "Multiple comment files".
 *      BAKALK   2007-04-05   Document Transmittals: Now pdf are checked in as comment files from Transmittal
 *      BAKALK   2007-04-06   Document Transmittals: View comment files done
 *      JANSLK   2007-04-09   Merged Bug 62489, Modified getOppSuccessMessage().
 *      BAKALK   2007-04-10   Document Transmittals: Send comment files done
 *      BAKALK   2007-05-14   Call Id: 140609.
 *      BAKALK   2007-06-29   Call Id: 144727,modified getActionMacroProcess().
 *      BAKALK   2007-07-13   Call Id: 144728,modified initEdmMacro().
 *      BAKALK   2007-07-18   Call Id: 146508,modified formatErrorMessage(),initEdmMacro(),transferToNonIEPage() and run().
 *      JANSLK   2007-07-30   Call Id: 143969,modified showApplicationChooser so that Application combo box sorts files types by description.
 *      JANSLK   2007-08-08   Changed debug("DEBUG:.... to debug("debug:... to correct localization errors.
 *      NaLrlk   2007-08-13   XSS Corrrection.
 *      BAKALK   2007-08-21   Call Id: 143180: Implemented zipping and sending files in a folder with different options than what we  have today.
 *      ASSALK   2007-08-22   Merged Bug 66906, Modified the select statements by adding order by clause in showApplicationChooser().
 *      BAKALK   2007-08-24   Merged Bug 64138, Modified getCopyToDir().
 *      DINHLK   2007-09-18   Patched in Bug 65833, Modified getActionMacroProcess().
 *      BAKALK   2007-09-19   Call Id: 148635, Modified getContents(): removed mgr.encodeStringForJavascript() from string passed to oCliMgr.Zip().
 *      BAKALK   2007-15-29   Bug Id: 68527, Modified getContents().
 *      BAKALK   2007-11-13   Bug Id: 68849, Modified getContents() for VIEWCOMMENT.
 *      AMNALK   2007-11-30   Bug Id: 65997, Added the code to delete the local file at all the places where copying the file from repository to server location.
 *      BAKALK   2007-12-15   Bug Id: 68455, Modified prepareFileAction().
 *      AMNALK   2008-01-07   Bug Id: 68528, Modified getActionMacroProcess().
 *      VIRALK   2008-03-27   Bug 71463, Modified sendMailDocument() zipAndEmail() & getContents().
 *      AMNALK   2008-07-01   Bug 72654, Modified prepareFileAction().   	 
 *      AMNALK   2008-07-07   Bug 72460, Modified run() & getContents(). Added new methods undoStateChange(), removeLastHistoryLine(), removeFileReference(), prepareClientActionWithError(),
 *			      getOppFailMessage() & logOperationModifyStatus(). 
 *      VIRALK   2008-07-22   Bug 74523, Call AdjustFileName() in sendMailDocument(),zipAndEmail() and retrieved path from OCX.
 *      AMNALK   2008-08-22   Bug 74989, Modified getOppSuccessMessage() and logError() to add log for CREATENEW structure documents.
 *	     AMNALK   2008-08-22   Bug 73728, Modified logError() to add log for GET_DOCUMENTS_FOR_MAIL.
 *      AMNALK   2008-08-26   Bug 73736, Modified run(), getContents(), prepareFileAction() and prepareClientAction(). Added updateLastOpLog().
 *      AMNALK   2008-09-09   Bug 73718, Modified initEdmMacro() and isStructureProcess() to handle the view copy & print copy of structure documents.
 *      DULOLK   2008-10-15   Bug 71831, Modified sendMailDocument(), getCopyToDir() and isFileNameConfigurable() to allow selection of file name type.
 *      SHTHLK   2008-11-03   Bug 78201, Modified transferToNonIEPage() to get the Document Revision if it doesn't exists in the querystring.
 *      DULOLK   2008-11-27   Bug 78799, Modified prepareFileAction() to add relevant error message when deleting redline files.
 *      SHTHLK   2008-02-26   Bug 80882, Modified getOppSuccessMessage and getOppFailMessage to update the report with the correct messages. 
 *      SHTHLK                Modified prepareFileAction() to handle the situation of printing document structures
 *      AMNALK   2009-05-27   Bug 81807, Added new function downloadTransmittalFiles() and modified run(), performFileAction(), getActionMacroProcess(), prepareFileAction() and getContents().
 *      RUMELK   2010-04-27   Bug Id: 89383, Modified sendFilesInTheFolderByMail() and getContents().
 *      ARWILK   2010-09-22   Bug ID 89622, Modified prepareFileAction, checkInDocument, getContents and showFileChooser to support any file type as transmittal comments.
 *-----------------------------------------------------------------------------------------------------
 */

package ifs.docmaw;
 
import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

/**
 * EdmMacro is a centralized page for handling file operations in Docmaw. EdmMacro
 * extends DocSrv - the Document Server class - which serves/provides methods for
 * carrying out the actual file transfers and EDM state changes. The role of EdmMacro
 * is to only act as a "mediator" or "broker" between its super class and the ocx,
 * by communicating the necessary information between them.
 *
 * To get up to speed with this class just study the following 3 methods:
 *
 *    - run()
 *
 *    - prepareFileAction()
 *
 *    - performFileAction()
 *
 *    (see method documentation for more information)
 *
 *
 * The general flow of a file operation (using checkout as an example) is as follows:
 *
 * The run() method starts by creating an instance of DocumentTransferHandler, which
 * automatically intercepts the current page request. All parameters passed to this
 * page can now be obtained from this instance.
 *
 * run() now invokes prepareFileAction() to ensure that all necessary information
 * (including security checks) are obtained, so that the action can be carried out
 * successfully. Only when prepareFileAction() returns "successfully" is performFileAction()
 * called.
 *
 * preformFileAction() dispatches the file action request to the appropriate action
 * handler depending on the type of file action. For instance, if the action was
 * "CHECKOUT", checkOutDocument() is invoked.
 *
 * checkOutDocument() calls the super class method checkOutDocument(), which copies
 * the document files from the primary repository to a secondary FTP server and also
 * updates the EDM states. checkOutDocument() then creates a XML configuration file
 * which contains information for the ocx, about the file action. This configuration file
 * is stored temporarily in the web server until it is downloaded by the ocx.
 *
 * Once the ocx downloads the configuration file, using the information contained in
 * it, it will download the document files from the secondary FTP, update the registry,
 * execute macros (if any) etc. and finally complete the file action.
 *
 *
 */
public class EdmMacro extends DocSrv
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.EdmMacro");
   public static final String DEFAULT_DOC_TYPE = "ORIGINAL";
   public String dlgName = getClass().getName().toUpperCase();

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext           ctx;
   private ASPHTMLFormatter     fmt;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private DocumentTransferHandler     doc_hdlr;
   private String                      xmlFileLoc;
   private String                      client_action;
   private String                      err_msg;
   private String                      confirm_msg;
   // Added by Terry 20120918
   private String                      information_msg;
   private String                      alert_msg;
   // Added end
   private String                      mail_msg_subject;
   private String                      mail_msg_body;
   private String                      mail_folder;
   private String                      mail_client_message;
   private String                      mail_zip_file_name;
   private String[]                    mail_file_attachments;
   private String[]                    mail_file_extensions;
   private String			doc_keys;
   // Bug Id 89383, start
   private String                      mail_to;
   // Bug Id 89383, end


   
   private String[]                    local_file_names;
   private String                      local_file_name;
   private String                      local_path_name;
   private String                      file_list;
   private String                      file_type_list;
   private String                      zip_file_name;
   private String                      name_files_using;
   private String                      providerPrefix;
   private String                      url;
   //Bug 73736, Start
   private String                      sLaunchFile;
   //Bug 73736, End
   private ASPBuffer                   data_buf;
   private ASPBuffer                   error_log;
   private boolean                     restart;
   private boolean                     resubmit;
   private boolean                     critical_error;
   private boolean                     bCloseWindow;
   private boolean                     file_writable_error;
   private boolean                     bDeleteTransmittalFoler; //Bug Id 72460
   private boolean                     bPrintView; //Bug id 80882

   private ASPBlock                    fileblk;
   private ASPCommandBar               filebar;
   private ASPRowSet                   fileset;
   private ASPBlockLayout              filelay;

   // Added by Terry 20120918
   // edm files
   protected ASPBlock edmblk;
   protected ASPRowSet edmset;
   protected ASPCommandBar edmbar;
   protected ASPTable edmtbl;
   protected ASPBlockLayout edmlay;
   // Added end
   
   private ASPInfoServices inf;
   private ASPTransactionBuffer trans;
   private String language;
   private String layout;

    //Bug Id 71463, start
   private int MAX_FILE_NAME_LENGTH = 250;
   //Bug Id 71463, end

   //Bug Id 74523, start
   private String fresh = "true";
   private String path = "";
   //Bug Id 74523, End

   //Bug Id 81807, start
   private boolean bNoViewRefExist;
   //Bug Id 81807, end
   
   // Added by Terry 20121024
   // Save edm file count
   private String edm_file_count;
   // Added end

   //===============================================================
   // Construction
   //===============================================================
   public EdmMacro(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   /**
    * The "main" method of this class, invoked by the framwork. run() creates an
    * instance of DocumentTransferHandler which intercepts the current page request.
    * All parameters passed to this page can now be retrieved using this instance.
    * The rest of the code is self-explanatory.
    *
    */
   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      mgr.setPageExpiring();
      ctx = mgr.getASPContext();

      // Bug 73736, Start
      sLaunchFile = "TRUE";
      // Bug 73736, End

      // Create the document transfer handler..
      doc_hdlr = new DocumentTransferHandler(mgr);

      // Initialise error log..
      error_log = ctx.readBuffer("ERROR_LOG");

      // Bug Id 72460, start
      // Rollback the status of checked out documents if the operation fails.
      if ("NORMAL".equals(mgr.readValue("ROLLBACK_STATUS")) || "STRUCTURE".equals(mgr.readValue("ROLLBACK_STATUS")))
      {
         // Delete transmittal download folders

         if ("TRUE".equals(doc_hdlr.getActionProperty("CHECKOUT_TO_GIVEN_PATH")))
         {
            doc_hdlr.firstDocument();

            // Download & publish case
            if ("YES".equals(doc_hdlr.getDocumentProperty("TRANSMITTAL_DOWNLOAD")))
            {
               bDeleteTransmittalFoler = true;
            }

            // Send report case
            if ("CONNECTREPORT".equals(doc_hdlr.getDocumentProperty("FILE_ACTION")))
            {
               bDeleteTransmittalFoler = true;
               deleteTransmittalDoc();
               rollbackTransmittalStatus("Preliminary");
            }
         }

         // Checkout of normal documents
         if ("CHECKOUT".equals(doc_hdlr.getActionProperty("FILE_ACTION")))
         {
            if (!doc_hdlr.isLastDocument())
               doc_hdlr.previousDocument();

            if ("REDLINE".equals(doc_hdlr.getActionProperty("DOC_TYPE")))
               undoStateChange("REDLINE");
            else
               undoStateChange("ORIGINAL");

            removeLastHistoryLine("CHECKOUT");

            if (!doc_hdlr.isLastDocument())
               setNextDocument();
         }
         // First time Redline file Edit
         else if ("CREATENEW".equals(doc_hdlr.getActionProperty("FILE_ACTION"))
               && "REDLINE".equals(doc_hdlr.getActionProperty("DOC_TYPE")))
         {
            if (!doc_hdlr.isLastDocument())
               doc_hdlr.previousDocument();
            removeFileReference("REDLINE", "1");
            if (!doc_hdlr.isLastDocument())
               setNextDocument();
         }
         // Structure documents
         else if ("DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE")))
         {
            doc_hdlr.previousDocument();
            prepareClientActionWithError();

            if ("CHECKOUT".equals(doc_hdlr.getDocumentProperty("FILE_ACTION")))
            {
               undoStateChange("ORIGINAL");
               removeLastHistoryLine("CHECKOUT");
               setNextDocument();
               boolean prepared = prepareFileAction();
               if (prepared)
               {
                  performFileAction();
               }
            }
            else if ("GET_DOCUMENTS_FOR_EMAIL".equals(doc_hdlr.getDocumentProperty("FILE_ACTION")))
            {
               doc_hdlr.lastDocument();
               boolean prepared = prepareFileAction();
               if (prepared)
               {
                  performFileAction();
               }
            }
            else
            {
               setNextDocument();
               if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(doc_hdlr.getDocumentProperty("FILE_ACTION")))
                  setNextDocument();
               boolean prepared = prepareFileAction();
               if (prepared)
               {
                  performFileAction();
               }
            }
            doc_hdlr.saveRequest();
         }

         // Close the window if a normal document is processed
         if (!("DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE"))))
            client_action = "CLOSE";
      }
      else
      {
         // bug id 74523, Start
         fresh = ctx.readValue("FRESH", "true");
         // bug id 74523, End

         bNoViewRefExist = ctx.readFlag("NO_VIEW_REF_EXIST", false); // Bug Id 81807
         
         // Added by Terry 20121024
         // Get edm file count, and save it in ctx
         edm_file_count = ctx.readValue("EDM_FILE_COUNT");
         if (mgr.isEmpty(edm_file_count))
            edm_file_count = String.valueOf(getEdmFileCount(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), doc_hdlr.getDocType()));            
         // Added end
         try
         {
            // Initialise page..
            initEdmMacro();

            // Bug 73736, Start
            if ("FALSE".equals(mgr.readValue("VALIDATE_OK")) && 
                error_log.countItems() > 0 &&
                "DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE")))
            {
               updateLastOpLog();
               sLaunchFile = "FALSE";
            }
            // Bug 73736, End

            if (DEBUG)
               debug("debug: run() " + fresh);
            if (DEBUG)
               debug("debug: run() " + fresh);

            // bug id 74523, Start
            if ("true".equals(fresh))
            {
               getPathFromOCX();
            }
            // bug id 74523, End
            else
            {
               // bug id 74523, Start
               path = getPageProperty("LOCAL_CHECKOUT_PATH");

               if (DEBUG)
                  debug("debug: run() path" + path);
               // bug id 74523, End

               if (clientError())
               {
                  // log client error and move to the next document..
                  logClientError();
                  clearCurrentOperationState();
                  setNextDocument();
               }

               boolean prepared = prepareFileAction();
               if (prepared)
               {
                  performFileAction();
               }
            }
         }
         catch (FndException fnd)
         {
            client_action = "ERROR";
            err_msg = formatErrorMessage(fnd.getMessage());
         }
         catch (RuntimeException re)
         {
            client_action = "ERROR";
            err_msg = formatErrorMessage(re.getMessage());
            critical_error = true;
         }
         finally
         {
            prepareClientAction();
            doc_hdlr.saveRequest();
         }

         // Added by Terry 20121024
         // Get edm file count, and save it in ctx
         ctx.writeValue("EDM_FILE_COUNT", edm_file_count);
         // Added end
         
         // bug id 74523, Start
         ctx.writeValue("FRESH", "false");
         // bug id 74523, End

         ctx.writeFlag("NO_VIEW_REF_EXIST", bNoViewRefExist); // Bug Id 81807

      }
      // Bug Id 72460, end

      if (error_log != null)
      {
         ctx.writeBuffer("ERROR_LOG", error_log);
      }
   }
   
   //Bug Id 74523, Start
   private void getPathFromOCX()
   {
      client_action = "GET_PATH_FIRST";
      fresh = "false";
      ctx.writeValue("FRESH", "false");
      if (DEBUG)
         debug("debug: getPathFromOCX() " + fresh);
      if (DEBUG)
         debug("debug: getPathFromOCX() " + fresh);
      resubmit = true;
   }
    //Bug Id 74523,End

   // Added by Terry 20120918
   // okFind edm_file set
   public void okFindEdm()
   {
      ASPManager mgr = getASPManager();
      
      String file_action = getFileAction();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String doc_type = doc_hdlr.getDocType();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      mgr.createSearchURL(edmblk);
      
      ASPQuery q = trans.addQuery(edmblk);
      q.includeMeta("ALL");
      
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      
      if (!mgr.isEmpty(doc_class))
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
      
      if (!mgr.isEmpty(doc_no))
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
      
      if (!mgr.isEmpty(doc_sheet))
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
      
      if (!mgr.isEmpty(doc_rev))
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      
      if (!mgr.isEmpty(doc_type))
         q.addWhereCondition("DOC_TYPE = '" + doc_type + "'");
      
      if ("CHECKOUT".equals(file_action))
      {
         // if CHECKOUT operation, add edm_file state where condition.
         q.addWhereCondition("(OBJSTATE = 'Checked In' OR OBJSTATE = 'Checked Out' AND CHECKED_OUT_SIGN = Fnd_Session_API.Get_Fnd_User)");
      }
      else if ("DELETESEL".equals(file_action))
      {
         // if DELETE selected file operation, add edm_file state where condition.
         q.addWhereCondition("OBJSTATE = 'Checked In'");
      }
      else if ("CHECKINSEL".equals(file_action) || "UNDOCHECKOUT".equals(file_action))
      {
         // if CHECKINSEL, UNDOCHECKOUT operation, add edm_file state where condition.
         q.addWhereCondition("OBJSTATE = 'Checked Out' AND CHECKED_OUT_SIGN = Fnd_Session_API.Get_Fnd_User");
      }
      
      q.setBufferSize(15);
      mgr.querySubmit(trans, edmblk);
      if (edmset.countRows() == 0)
      {
         edmset.clear();
         mgr.showAlert(mgr.translate("EDMMACROEDMFILENODATA: No data found."));
      }
      clearSelectionToCtx();
   }
   
   public void countFindEdm()
   {
      ASPManager mgr = getASPManager();
      
      String file_action = getFileAction();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String doc_type = doc_hdlr.getDocType();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(edmblk);
      q.setSelectList("to_char(count(*)) N");
      
      if (!mgr.isEmpty(doc_class))
         q.addWhereCondition("DOC_CLASS = '" + doc_class + "'");
      
      if (!mgr.isEmpty(doc_no))
         q.addWhereCondition("DOC_NO = '" + doc_no + "'");
      
      if (!mgr.isEmpty(doc_sheet))
         q.addWhereCondition("DOC_SHEET = '" + doc_sheet + "'");
      
      if (!mgr.isEmpty(doc_rev))
         q.addWhereCondition("DOC_REV = '" + doc_rev + "'");
      
      if (!mgr.isEmpty(doc_type))
         q.addWhereCondition("DOC_TYPE = '" + doc_type + "'");
      
      if ("CHECKOUT".equals(file_action))
      {
         // if CHECKOUT operation, add edm_file state where condition.
         q.addWhereCondition("(OBJSTATE = 'Checked In' OR OBJSTATE = 'Checked Out' AND CHECKED_OUT_SIGN = Fnd_Session_API.Get_Fnd_User)");
      }
      else if ("DELETESEL".equals(file_action))
      {
         // if DELETE selected file operation, add edm_file state where condition.
         q.addWhereCondition("OBJSTATE = 'Checked In'");
      }
      else if ("CHECKINSEL".equals(file_action) || "UNDOCHECKOUT".equals(file_action))
      {
         // if CHECKINSEL, UNDOCHECKOUT operation, add edm_file state where condition.
         q.addWhereCondition("OBJSTATE = 'Checked Out' AND CHECKED_OUT_SIGN = Fnd_Session_API.Get_Fnd_User");
      }
      
      mgr.submit(trans);
      edmlay.setCountValue(toInt(edmset.getValue("N")));
      edmset.clear();
      clearSelectionToCtx();
   }
   
   private String getEdmFileKeyRef(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no)
   {
      return doc_class + IfsNames.textSeparator +
             doc_no + IfsNames.textSeparator +
             doc_sheet + IfsNames.textSeparator +
             doc_rev + IfsNames.textSeparator +
             doc_type + IfsNames.textSeparator +
             file_no + IfsNames.textSeparator;
   }
   
   private void storeSelectionToCtx()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      try
      {
         edmset.store();
      }
      catch(Exception e)
      {
         
      }
      
      int contRows = edmset.countRows();
      if (contRows > 0)
      {
         ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
         if (edm_files == null)
         {
            edm_files = mgr.newASPBuffer();
         }
         
         edmset.first();
         for(int i = 0; i < contRows; i++)
         {
            String doc_class = edmset.getRow().getValue("DOC_CLASS");
            String doc_no = edmset.getRow().getValue("DOC_NO");
            String doc_sheet = edmset.getRow().getValue("DOC_SHEET");
            String doc_rev = edmset.getRow().getValue("DOC_REV");
            String doc_type = edmset.getRow().getValue("DOC_TYPE");
            String file_no = edmset.getRow().getValue("FILE_NO");
            
            String edm_file_key_ref = getEdmFileKeyRef(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
            
            if (edmset.isRowSelected())
            {
               if (!edm_files.itemExists(edm_file_key_ref))
               {
                  ASPBuffer sel_edm_file = mgr.newASPBuffer();
                  sel_edm_file.addItem("DOC_CLASS", doc_class);
                  sel_edm_file.addItem("DOC_NO", doc_no);
                  sel_edm_file.addItem("DOC_SHEET", doc_sheet);
                  sel_edm_file.addItem("DOC_REV", doc_rev);
                  sel_edm_file.addItem("DOC_TYPE", doc_type);
                  sel_edm_file.addItem("FILE_NO", file_no);
                  edm_files.addBuffer(edm_file_key_ref, sel_edm_file);
               }
            }
            else
            {
               if (edm_files.itemExists(edm_file_key_ref))
               {
                  edm_files.removeItem(edm_file_key_ref);
               }
            }
            edmset.next();
         }
         ctx.setGlobalObject(dlgName, edm_files);
      }
   }
   
   private void clearSelectionToCtx()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      
      if (edm_files != null)
      {
         edm_files.clear();
      }
      
      ctx.setGlobalObject(dlgName, null);
   }
   
   private void adjustEdm()
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int setCount = edmset.countRows();
      
      if (edm_files != null && edm_files.countItems() != 0 && setCount > 0)
      {
         edmset.first();
         for(int i = 0; i < setCount; i++)
         {
            String edm_file_key_ref = getEdmFileKeyRef (
                  edmset.getValue("DOC_CLASS"),
                  edmset.getValue("DOC_NO"),
                  edmset.getValue("DOC_SHEET"),
                  edmset.getValue("DOC_REV"),
                  edmset.getValue("DOC_TYPE"),
                  edmset.getValue("FILE_NO")); 
               
            if (edm_files.itemExists(edm_file_key_ref))
            {
               edmset.selectRow();
            }
            edmset.next();
         }
      }
   }
   // Added end
   
   
   // Added by Terry 20121018
   // Get edm files from db to session
   private void getEdmFilesToCtx(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPContext ctx = mgr.getASPContext();
      
      ASPCommand cmd = trans.addCustomFunction("GETFILENOS", "Edm_File_API.Get_File_Nos", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = mgr.perform(trans);
      String file_nos = trans.getValue("GETFILENOS/DATA/OUT_1");
      if (mgr.isEmpty(file_nos))
         return;
      
      // 1. Clear files from ctx
      clearSelectionToCtx();
      
      // 2. Create files buffer
      ASPBuffer edm_files = mgr.newASPBuffer();
      
      // 3. Select and store to ctx
      StringTokenizer file_no_st = new StringTokenizer(file_nos, "^");
      int no_files = file_no_st.countTokens();
      
      for (int i = 0; i < no_files; i++)
      {
         String file_no = file_no_st.nextToken();
         String edm_file_key_ref = getEdmFileKeyRef(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
         ASPBuffer edm_file = mgr.newASPBuffer();
         edm_file.addItem("DOC_CLASS", doc_class);
         edm_file.addItem("DOC_NO", doc_no);
         edm_file.addItem("DOC_SHEET", doc_sheet);
         edm_file.addItem("DOC_REV", doc_rev);
         edm_file.addItem("DOC_TYPE", doc_type);
         edm_file.addItem("FILE_NO", file_no);
         edm_files.addBuffer(edm_file_key_ref, edm_file);
      }
      
      ctx.setGlobalObject(dlgName, edm_files);
   }
   // Added end
   
   // Added by Terry 20131002
   // Get selected edm file form db to session
   private void getEdmFilesToCtx(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPContext ctx = mgr.getASPContext();
      
      // 1. Clear files from ctx
      clearSelectionToCtx();
      
      // 2. Create files buffer
      ASPBuffer edm_files = mgr.newASPBuffer();
      
      // 3. Select and store to ctx
      
      String edm_file_key_ref = getEdmFileKeyRef(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
      ASPBuffer edm_file = mgr.newASPBuffer();
      edm_file.addItem("DOC_CLASS", doc_class);
      edm_file.addItem("DOC_NO", doc_no);
      edm_file.addItem("DOC_SHEET", doc_sheet);
      edm_file.addItem("DOC_REV", doc_rev);
      edm_file.addItem("DOC_TYPE", doc_type);
      edm_file.addItem("FILE_NO", file_no);
      edm_files.addBuffer(edm_file_key_ref, edm_file);
      
      ctx.setGlobalObject(dlgName, edm_files);
   }
   // Added end
   
   // Added by Terry 20121024
   // get edm file count from db
   private int getEdmFileCount(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addCustomFunction("GETEDMFILECOUNT", "Edm_File_API.Get_Edm_File_Count", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = mgr.perform(trans);
      String edm_file_count = trans.getValue("GETEDMFILECOUNT/DATA/OUT_1");
      return Integer.parseInt(edm_file_count);
   }
   // Added end
   
   
   // Added by Terry 20130307
   // Transfer to doc control page
   private void transferToDocControl() throws FndException
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buf = mgr.newASPBuffer();
      buf.addItem("DOC_CLASS",  doc_hdlr.getDocClass());  
      buf.addItem("DOC_NO",     doc_hdlr.getDocNo());
      buf.addItem("DOC_SHEET",  doc_hdlr.getDocSheet());
      buf.addItem("DOC_REV",    doc_hdlr.getDocRev());
      buf.addItem("RELEASED",   doc_hdlr.getDocumentProperty("RELEASED"));
      buf.addItem("LATEST_REV", doc_hdlr.getDocumentProperty("LATEST_REV"));
      buf.addItem("FILE_TYPE",  doc_hdlr.getFileType());
      buf.addItem("DOC_TYPE",   doc_hdlr.getDocType());
      buf.addItem("PROCESS_DB", getFileAction());

      checkAccess(doc_hdlr.getDocClass(),doc_hdlr.getDocNo(),doc_hdlr.getDocSheet(),doc_hdlr.getDocRev(),doc_hdlr.getDocType(),getActionMacroProcess(getFileAction()));
      
      mgr.transferDataTo("DocEdmControlVue.page", buf);
   }
   // Added end
   
   /**
    * Overriding ASPPage.useLovContextSlot in order to force the use of
    * the Lov slot in the session table. This solves the logout problem
    */
   protected boolean useLovContextSlot()
   {
      return true;
   }

   
   private String formatErrorMessage(String message)
   {  
      ASPManager mgr = getASPManager();
      //Bug 58793, start
      if (!mgr.isEmpty(message)) {
         message = message.replaceAll("\n", "\\\\n"); 
         message = message.replaceAll("\r", "\\\\r"); 
         message = message.replaceAll("\"", "\\\\\"");
      }
      
      //Bug 58793, end
      return message;
   }



   public void debug(String str)
   {
     if (DEBUG) 
         super.debug(str);
   }



   /**
    * This method identifies the current file action by querying the instance
    * of DocumentTransferHandler, and then dispatches the request to the
    * appropriate file action handler.
    *
    */
   public void performFileAction() throws FndException
   {
      if (DEBUG) debug("debug: performFileAction() {");

      

      ASPManager mgr = getASPManager();
      String file_action = getFileAction();


      sameActionToAll = doc_hdlr.getActionProperty("SAME_ACTION_TO_ALL");
      launchFile = getLaunchFile();
      // Added by Terry 20140730
      // Check DOC_FOLDER parameter
      doc_folder = getDocFolder();
      // Added end
      
      // Dispatch operation to the appropriate
      // file action handler

      if ("CHECKOUT".equals(file_action))
         checkOutDocument();
      else if ("CHECKIN".equals(file_action))
         checkInDocuments();
      else if ("CHECKINSEL".equals(file_action))
         checkInSelectDocument();
      // Added by Terry 20120918
      // Delete selected edm files
      else if ("DELETESEL".equals(file_action))
         deleteSelectDocument();
      // Added end
      // Added by Terry 20121018
      // Download files
      else if ("DOWNLOAD".equals(file_action))
         downloadDocument();
      // Added end
      else if ("UNDOCHECKOUT".equals(file_action))
         undoCheckOutDocument();
      else if ("GET_DOCUMENTS_FOR_EMAIL".equals(file_action))
         getDocumentsForEMail("SENDMAIL");
      else if ("SENDMAIL".equals(file_action))
         sendMailDocument();
      else if ("SENDFOLDER_BYMAIL".equals(file_action))
         sendFilesInTheFolderByMail();
      else if ("ZIP_FOLDER".equals(file_action))
         zipFilesInTheFolder();
      else if ("SENDCOMMENT".equals(file_action))
         sendCommentFile();
      else if ("ZIP_AND_EMAIL".equals(file_action))
         zipAndEmail();
      else if ("VIEW".equals(file_action))
         viewDocument();
      else if ("VIEWCOMMENT".equals(file_action))
         viewComment();
      else if ("VIEWWITHEXTVIEWER".equals(file_action))
         viewWithExternalViewer();
      else if ("GETCOPYTODIR".equals(file_action))
         getCopyToDir();
      else if ("PRINT".equals(file_action))
         printDocument();
      else if ("DELETE".equals(file_action))
         deleteDocument();
      else if ("DELETEALL".equals(file_action))
         deleteAll();
      else if ("CREATENEW".equals(file_action))
         createNewDocument();
      else if ("ADDCOMMENT".equals(file_action))
         addCommentFile();
      else if ("CONNECTREPORT".equals(file_action))
         connectReport();
      else if ("FILEIMPORT".equals(file_action))
         importFile();
      else if ("CREATENEWDOC".equals(file_action))
         createNewDoc();
      else if ("LAUNCH_LOCAL_FILE".equals(file_action))
         launchLocalFile();
      else if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(file_action))
         launchLocalFileWithExternalViewer();
      else if ("OPEN_FOLDER".equals(file_action))
         openLocalFolder();
      else if ("DELETE_LOCAL_FILE".equals(file_action))
         deleteLocalFile();
      else if ("SELECT_AND_EXECUTE_MACRO".equals(file_action))
         selectAndExecuteMacro();
      else if ("SHOW_ERROR_LOG".equals(file_action))
         generateErrorLog();
      //Bug Id 81807, start
      else if ("TRANSMITTALDOWNLOAD".equals(file_action)) 
         downloadTransmittalFiles();
      //Bug Id 81807, end

      try
      {
         if (!Str.isEmpty(xmlFileLoc))
         {
            ctx.setGlobal("DOCMAW_FILE_INFO_XML", Util.toBase64Text(xmlFileLoc.getBytes()));
         }
      }
      catch (IOException io)
      {
         throw new FndException(mgr.translate("EDMMACROSETGBLCTXFAILED: Set Global context failed.\n")+io.getMessage());
        
      }

      if (DEBUG) debug("debug: } performFileAction()");
   }

   private void createNewDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      err_msg = mgr.translate("DOCMAWEDMMACROCREATENEW: Create new Document failed.\\n");
      
      String absolute_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String file_name = DocmawUtil.getFileName(absolute_file_name);
      
      if (!mgr.isEmpty(file_name))
      {
         // If the file extension of the specified file name matches
         // the file extension of the selected file type the same file
         // name can be used. Otherwise, the file has to be renamed to
         // use the valid file extension..
         
         String file_ext = getFileExtension(doc_hdlr.getFileType());
         if (!file_ext.equalsIgnoreCase(DocmawUtil.getFileExtention(file_name)))
         {
            file_name = DocmawUtil.getBaseFileName(file_name) + "." + file_ext;
            absolute_file_name = DocmawUtil.getPath(absolute_file_name) + "\\" + file_name;
         }
      }
      
      // Added by Terry 20121023
      // Mark create doc use template
      boolean create_use_template = false;
      // Added end
      
      //bug 58326
      String file_id = null;
      String check_out_path = getLocalCheckOutPath();
      String edm_info = getEdmInformation(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(),
            doc_hdlr.getDocRev(), doc_hdlr.getDocType());
      
      
      if ("ORIGINAL".equals(doc_hdlr.getDocType()) && "TRUE".equals(getStringAttribute(edm_info, "FILE_TEMPLATE_EXIST")))
      {
         // Create new document using template..
         //bug 58326
         // Modified by Terry 20121023
         // Original:
         // file_id = createNewDocument(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(),
         //       doc_hdlr.getDocRev(), doc_hdlr.getDocType(), check_out_path,
         //       file_name);
         createNewDocumentUseTempl(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(),
               doc_hdlr.getDocType(), check_out_path, file_name);
         create_use_template = true;
         // Modified end
      }
      else
      {
         // Create new empty document of the selected file type
         //bug 58326
         file_id = createNewDocument(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(),
               doc_hdlr.getDocRev(), doc_hdlr.getDocType(), doc_hdlr.getFileType(),
               check_out_path, file_name);
      }
      
      // Modified by Terry 20121023
      // if create empty document, download and open it 
      if (!create_use_template)
      {
         // If an absolute file name was not specified, but a checkout path was given..
         if (mgr.isEmpty(absolute_file_name) && !mgr.isEmpty(doc_hdlr.getDocumentProperty("FILE_PATH")))
         {
            absolute_file_name = check_out_path + getLocalFileName(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(),
                  doc_hdlr.getDocRev(), doc_hdlr.getDocType());
         }
         
         String macro = getSelectedMacro();
         String file_action = getFileAction();
         
         xmlFileLoc = createXmlFile(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), doc_hdlr.getDocType(),
               null, doc_hdlr.getFileType(), absolute_file_name, getMacroAction(macro), getMacroMainFunction(macro), file_action,
               getActionMacroProcess(file_action), file_id, path);
         
         client_action = "PERFORM";
      }
      // Modified end
      
   }


   private void addCommentFile() throws FndException
   {
      notCheckCurrentState = true;
      modifyDocClassProcessConfig = true;
      addComment = true;
      //for multiple comment files
      file_no = doc_hdlr.getDocumentProperty("FILE_NO");
      
      checkInDocument();
   }


   private void viewComment() throws FndException
   {
      debug("viewComment  {" );

      viewComment = true;
      modifyDocClassProcessConfig = true;

      String file_action = this.getFileAction();



      file_no = doc_hdlr.getDocumentProperty("FILE_NO");
      this.viewDocument();


   }


   private void sendCommentFile() throws FndException
   {
       debug("SendComment  " );
      sendComment = true;
      modifyDocClassProcessConfig = true;
      file_no = doc_hdlr.getDocumentProperty("FILE_NO");
      this.sendMailDocument();
   }



   private void checkOutDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String full_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String check_out_path = getLocalCheckOutPath();
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = "";
      String main_function = "";
      if (!mgr.isEmpty(macro))
      {
         action = getMacroAction(macro);
         main_function = getMacroMainFunction(macro);
      }
      
      String check_out_file_name = DocmawUtil.getFileName(full_file_name);
      String doc_title = null;

      // Set additional macro attributes
      macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

      // Added by Terry 20120919
      // Set file_no from user selected.
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      if (edm_files != null && edm_files.countItems() == 1)
      {
         this.file_no = edm_files.getBufferAt(0).getValue("FILE_NO");
         clearSelectionToCtx();
      }
      // Added end
      
      String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

      // Check if files checked out need to be named
      // using their original file name..
      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         check_out_file_name = DocmawUtil.getAttributeValue(edm_info, "USER_FILE_NAME");

         if ("REDLINE".equals(doc_type))
         {
            String base_file_name = DocmawUtil.getBaseFileName(check_out_file_name);
            String redline_ext = DocmawUtil.getAttributeValue(edm_info, "REDLINE_FILE_EXTENTION");
            check_out_file_name = base_file_name + "." + redline_ext;
         }
         use_original_file_name = true;
      }

      if ("ORIGINAL".equals(doc_type) && "TRUE".equals(getStringAttribute(edm_info, "CHECKED_OUT_TO_ME")))
      {
         //bug 58326, starts....
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, full_file_name, action, main_function, "ALREADYCHECKEDOUT", process, file_id, path);
      }
      else
      {
         file_id = checkOutDocument(doc_class, doc_no, doc_sheet, doc_rev, doc_type, check_out_path, check_out_file_name);
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, full_file_name, action, main_function, file_action, process, file_id, path);
      }
      //bug 58326, ends....
      client_action = "PERFORM";
   }


   private void checkInDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String process = getActionMacroProcess(file_action);
      // bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String original_file_name = getPageProperty("LOCAL_FILE_NAME");
      String doc_title = null;

      // File type. For normal docs first time checkin (fci) we get
      // the file type from the context cache.
      String file_type = getPageProperty("CHECKED_IN_FILE_TYPE");

      // For structure docs fci the filetype is extracted from
      // the document handler.
      if (mgr.isEmpty(file_type))
         file_type = doc_hdlr.getFileType();

      // file_type is still empty then its probably not a fci scenario so let it
      // be.
      if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
         // bug 58326
         file_id = getRandomFilename();

         // Set additional macro attributes
         macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

         if (DEBUG)
            debug(":debug edmmacrocheckin path" + path);

         // bug 58326, starts....
         xmlFileLoc = createXmlFile(
               doc_class,
               doc_no,
               doc_sheet,
               doc_rev,
               doc_type,
               doc_title,
               file_type,
               file_name,
               action,
               main_function,
               file_action,
               process,
               file_id,
               path);

         ctx.writeValue("UNIQUEFILENAME", file_id);
         // bug 58326, ends....
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
         // bug 58326,
         file_id = ctx.readValue("UNIQUEFILENAME", "");

         String files_checked_in = getPageProperty("FILES_CHECKED_IN");
         String view_files_checked_in = getPageProperty("VIEW_FILES_CHECKED_IN");
         String edmInfo = edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

         // delete the local file if this document has been checked in before..
         OriginalRefExist = getStringAttribute(edmInfo, "ORIGINAL_REF_EXIST");
         doc_hdlr.setActionProperty("DELETE_LOCAL_FILE", OriginalRefExist);

         // bug 58326,
         // check-in the documents..
         if (addComment)
         {
            // Bug ID 89622, Start
            local_file_names = checkInCommentFile(doc_class, doc_no, doc_sheet, doc_rev, file_type, file_id, original_file_name);
            // Bug ID 89622, Finish
         }
         else
            local_file_names = checkInDocument(
                  doc_class,
                  doc_no,
                  doc_sheet,
                  doc_rev,
                  file_type,
                  doc_type,
                  file_id,
                  files_checked_in,
                  view_files_checked_in,
                  original_file_name);

         client_action = "DELETE_CHECKED_IN_FILES";
      }
   }
   
   // Added by Terry 20120917
   // Get file type by file name.
   private String getFileTypeByName(String file_name)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      String file_ext = file_name.substring(file_name.lastIndexOf(".") + 1);

      //Get the file type from the extension
      ASPCommand cmd = trans.addCustomFunction("GETFTYPE", "EDM_APPLICATION_API.Get_File_Type", "FILE_TYPE");
      cmd.addParameter("IN_STR1", file_ext.toUpperCase());
      trans = mgr.perform(trans);

      return trans.getValue("GETFTYPE/DATA/FILE_TYPE");
   }
   
   private String getFileType(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type, String file_no)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFTYPE", "Edm_File_API.Get_File_Type", "FILE_TYPE");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_STR1", file_no);
      trans = mgr.perform(trans);

      return trans.getValue("GETFTYPE/DATA/FILE_TYPE");
   }
   
   // Added end
   
   // Added by Terry 20120917
   // Check in multi-documents
   private void checkInDocuments() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String process = getActionMacroProcess(file_action);
      // bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String original_file_name = getPageProperty("LOCAL_FILE_NAME");
      String doc_title = null;
      
      ASPBuffer check_in_buffer = ctx.readBuffer("CHECK_IN_BUFFER");
      int index = 0;
      if (check_in_buffer == null && !mgr.isEmpty(file_name))
      {
         check_in_buffer = mgr.newASPBuffer();
         StringTokenizer file_names = new StringTokenizer(file_name, "|");
         if (file_names.countTokens() > 1)
         {
            while(file_names.hasMoreTokens())
            {
               String file_name_ = file_names.nextToken();
               check_in_buffer.addItem(String.valueOf(index), file_name_);
               index = index + 1;
            }
         }
         else
         {
            check_in_buffer.addItem(String.valueOf(index), file_name);
         }
         ctx.writeBuffer("CHECK_IN_BUFFER", check_in_buffer);
         ctx.writeNumber("CHECK_IN_BUFFER_INDEX", 0);
      }
      
      int check_in_buffer_index = ctx.readNumber("CHECK_IN_BUFFER_INDEX", 0);

      // File type. For normal docs first time checkin (fci) we get
      // the file type from the context cache.
      String file_type; // = getPageProperty("CHECKED_IN_FILE_TYPE");

      // For structure docs fci the filetype is extracted from
      // the document handler.
      // if (mgr.isEmpty(file_type))
      //    file_type = doc_hdlr.getFileType();

      // Fetch file_name from check in buffer.
      file_name = check_in_buffer.getValue(String.valueOf(check_in_buffer_index));
      file_type = getFileTypeByName(file_name);
      
      if (check_in_buffer != null && check_in_buffer_index < check_in_buffer.countItems())
      {
         // file_type is still empty then its probably not a fci scenario so let it be.
         if (!ctx.readFlag("XML_FILE_CREATED", false))
         {
            // bug 58326
            file_id = getRandomFilename();

            // Set additional macro attributes
            macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

            if (DEBUG)
               debug(":debug edmmacrocheckin path" + path);
            
            // bug 58326, starts....
            xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type,
               doc_title, file_type, file_name, action, main_function, file_action, process, file_id, path);

            ctx.writeValue("UNIQUEFILENAME", file_id);
            // bug 58326, ends....
            ctx.writeFlag("XML_FILE_CREATED", true);
            restartFileAction(true);
            client_action = "PERFORM";
         }
         else
         {
            ctx.writeFlag("XML_FILE_CREATED", false);
            // bug 58326,
            file_id = ctx.readValue("UNIQUEFILENAME", "");
   
            String files_checked_in = getPageProperty("FILES_CHECKED_IN");
            String view_files_checked_in = getPageProperty("VIEW_FILES_CHECKED_IN");
            String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
   
            // delete the local file if this document has been checked in before..
            OriginalRefExist = getStringAttribute(edmInfo, "ORIGINAL_REF_EXIST");
            doc_hdlr.setActionProperty("DELETE_LOCAL_FILE", "FALSE");
   
            // bug 58326,
            // check-in the documents..
            if (addComment)
            {
               // Bug ID 89622, Start
               local_file_names = checkInCommentFile(doc_class, doc_no, doc_sheet, doc_rev, file_type, file_id, original_file_name);
               // Bug ID 89622, Finish
            }
            else
               local_file_names = checkInDocuments(doc_class, doc_no, doc_sheet, doc_rev, file_type,
                     doc_type, file_id, files_checked_in, view_files_checked_in, original_file_name);
   
            client_action = "CHECKE_IN_FILES";
            
            // Store buffer and index, and restart action.
            check_in_buffer_index = check_in_buffer_index + 1;
            if (check_in_buffer_index < check_in_buffer.countItems())
            {
               restartFileAction(true);
            }
         }
         ctx.writeBuffer("CHECK_IN_BUFFER", check_in_buffer);
         ctx.writeNumber("CHECK_IN_BUFFER_INDEX", check_in_buffer_index);
      }
   }
   
   private void checkInSelectDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      if ("CHECKINSEL".equals(file_action))
         file_action = "CHECKIN";
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String process = getActionMacroProcess(file_action);
      // bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String original_file_name = getPageProperty("LOCAL_FILE_NAME");
      String doc_title = null;
      
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int check_in_sel_buffer_index = ctx.readNumber("CHECK_IN_SEL_BUFFER_INDEX", 0);

      // File type. For normal docs first time checkin (fci) we get
      // the file type from the context cache.
      String file_type; // = getPageProperty("CHECKED_IN_FILE_TYPE");

      // For structure docs fci the filetype is extracted from
      // the document handler.
      // if (mgr.isEmpty(file_type))
      //    file_type = doc_hdlr.getFileType();
      
      if (edm_files != null && check_in_sel_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(check_in_sel_buffer_index).getValue("FILE_NO");
         
         file_type = getFileType(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
         
         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);
         // file_type is still empty then its probably not a fci scenario so let it be.
         if (!ctx.readFlag("XML_FILE_CREATED", false))
         {
            // bug 58326
            file_id = getRandomFilename();
   
            // Set additional macro attributes
            macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");
   
            if (DEBUG)
               debug(":debug edmmacrocheckin path" + path);
   
            // bug 58326, starts....
            xmlFileLoc = createXmlFile(
                  doc_class,
                  doc_no,
                  doc_sheet,
                  doc_rev,
                  doc_type,
                  doc_title,
                  file_type,
                  file_name,
                  action,
                  main_function,
                  file_action,
                  process,
                  file_id,
                  path);
   
            ctx.writeValue("UNIQUEFILENAME", file_id);
            // bug 58326, ends....
            ctx.writeFlag("XML_FILE_CREATED", true);
            restartFileAction(true);
            client_action = "PERFORM";
         }
         else
         {
            ctx.writeFlag("XML_FILE_CREATED", false);
            // bug 58326,
            file_id = ctx.readValue("UNIQUEFILENAME", "");
   
            String files_checked_in = getPageProperty("FILES_CHECKED_IN");
            String view_files_checked_in = getPageProperty("VIEW_FILES_CHECKED_IN");
            String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
   
            // delete the local file if this document has been checked in before..
            OriginalRefExist = getStringAttribute(edmInfo, "ORIGINAL_REF_EXIST");
            doc_hdlr.setActionProperty("DELETE_LOCAL_FILE", OriginalRefExist);
   
            // bug 58326,
            // check-in the documents..
            if (addComment)
            {
               // Bug ID 89622, Start
               local_file_names = checkInCommentFile(doc_class, doc_no, doc_sheet, doc_rev, file_type, file_id, original_file_name);
               // Bug ID 89622, Finish
            }
            else
               local_file_names = checkInDocument(
                     doc_class,
                     doc_no,
                     doc_sheet,
                     doc_rev,
                     file_type,
                     doc_type,
                     file_id,
                     files_checked_in,
                     view_files_checked_in,
                     original_file_name);

            client_action = "DELETE_CHECKED_IN_FILES";
            
            // Store buffer and index, and restart action.
            check_in_sel_buffer_index = check_in_sel_buffer_index + 1;
            if (check_in_sel_buffer_index < edm_files.countItems())
            {
               restartFileAction(true);
            }
         }
         ctx.writeNumber("CHECK_IN_SEL_BUFFER_INDEX", check_in_sel_buffer_index);
      }
      else
      {
         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
         clearSelectionToCtx();
      }
   }
   // Added end

   // Added by Terry 20121018
   // Download Document
   private void downloadDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String process = getActionMacroProcess(file_action);
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String transfered_file_no = doc_hdlr.getDocumentProperty("FILE_NO");
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;

      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      if (edm_files == null || edm_files != null && edm_files.countItems() == 0)
      {
         if (Str.isEmpty(transfered_file_no))
            getEdmFilesToCtx(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         else
            getEdmFilesToCtx(doc_class, doc_no, doc_sheet, doc_rev, doc_type, transfered_file_no);
         edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      }
      
      int download_buffer_index = ctx.readNumber("DOWNLOAD_BUFFER_INDEX", 0);
      
      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }

      // Set additional macro attributes
      macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

      err_msg = mgr.translate("DOCMAWEDMMACRODOWNLOAD: Download Document failed.\\n");
      
      // Add multi-download operation.
      if (edm_files != null && download_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(download_buffer_index).getValue("FILE_NO");
//         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);
         
         //bug 58326, starts....
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, process);//Bug Id 65997
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, process, file_id,path);
         
         //bug 58326, ends....
         client_action = "PERFORM";
         
         // Store buffer and index, and restart action.
         download_buffer_index = download_buffer_index + 1;
         if (download_buffer_index <= edm_files.countItems())
         {
            restartFileAction(true);
         }
         
         ctx.writeNumber("DOWNLOAD_BUFFER_INDEX", download_buffer_index);
      }
      else
      {
//         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
         clearSelectionToCtx();
         client_action = "DOWNLOAD_COMP";
      }
   }
   // Added end

   private void viewDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String process = getActionMacroProcess(file_action);
       //bug 58326,
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;

      // Added by Terry 20120919
      // Set file_no from user selected.
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      if (edm_files != null && edm_files.countItems() == 1)
      {
         this.file_no = edm_files.getBufferAt(0).getValue("FILE_NO");
         clearSelectionToCtx();
      }
      // Added end

      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }

      // Set additional macro attributes
      macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

      err_msg = mgr.translate("DOCMAWEDMMACROVIEWFAILED: View failed.\\n");
       //bug 58326, starts....
      file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, process);//Bug Id 65997

      xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, process, file_id,path);

      //bug 58326, ends....
      client_action = "PERFORM";
   }


   private void viewWithExternalViewer() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String macro = "";
      String action = "";
      String main_function = "";
      String doc_title = null;

      err_msg = mgr.translate("VIEWWITHEXTVIEWER: View with External Viewer failed.\\n");
       //bug 58326, starts....
      file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, process);//Bug Id 65997
      xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, null, action, main_function, file_action, "VIEW", file_id,path);
      //bug 58326, ends....
      client_action = "PERFORM";
   }


   private void undoCheckOutDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String delete_file = getDeleteFileOption();
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;
      
      // Added by Terry 20120925
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int undocheckout_buffer_index = ctx.readNumber("UNDOCHECKOUT_BUFFER_INDEX", 0);
      // Added end

      err_msg = mgr.translate("EDMMACROUNDOCHECKOUTFAILED: Undo Check Out failed.\\n");

      // Modified by Terry 20120925
      // Add multi-undocheckout operation.
      if (edm_files != null && undocheckout_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(undocheckout_buffer_index).getValue("FILE_NO");
         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);
         if (!ctx.readFlag("XML_FILE_CREATED", false))
         {
            // Set additional macro attributes
            macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");
   
             //bug 58326
            xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, delete_file, action, main_function, file_action, process, file_id,path);
            ctx.writeFlag("XML_FILE_CREATED", true);
            restartFileAction(true);
            client_action = "PERFORM";
         }
         else
         {
            ctx.writeFlag("XML_FILE_CREATED", false);
            undoCheckOut(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
            
            client_action = "UNDO_CHECK_OUT_PROCESSING";
            
            // Store buffer and index, and restart action.
            undocheckout_buffer_index = undocheckout_buffer_index + 1;
            if (undocheckout_buffer_index < edm_files.countItems())
            {
               restartFileAction(true);
            }
         }
         ctx.writeNumber("UNDOCHECKOUT_BUFFER_INDEX", undocheckout_buffer_index);
      }
      else
      {
         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
         clearSelectionToCtx();
      }
      // Modified end
   }



   private void deleteDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;

      err_msg = mgr.translate("EDMMACRODELETEFILE: Delete Document failed.\\n");

      if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
          //bug 58326
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, null, action, main_function, file_action, process, file_id,path);
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);

         // With DELETE we mean delete the document file and reference
         // on the server. Maybe we could implement a POWER delete also,
         // let's decide that later.
         deleteDocumentFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
      }
   }



   private void deleteAll() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;

      err_msg = mgr.translate("EDMDELETEFILEANDRECORD: Delete Document file and record failed.\\n");

      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }

      if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
          //bug 58326
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, null, action, main_function, file_action, process, file_id,path);
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
         deleteDocument(doc_class, doc_no, doc_sheet, doc_rev);
      }
   }
   
   // Added by Terry 20120918
   // Delete selected edm file
   private void deleteSelectDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String action = "";
      String main_function = "";
      String doc_title = null;
      String file_nos = "";

      err_msg = mgr.translate("EDMMACRODELETESELECTFILE: Delete Select edm file failed.\\n");

      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      
      if (edm_files != null && edm_files.countItems() > 0)
      {
         int count = edm_files.countItems();
         for (int i = 0; i < count; i++)
         {
            file_nos = file_nos + edm_files.getBufferAt(i).getValue("FILE_NO") + "^";
         }
         clearSelectionToCtx();
         deleteSelectDocumentFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_nos);
      }
      
      /*if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
          //bug 58326
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, null, action, main_function, file_action, process, file_id, path);
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
         
         
      }*/
   }
   // Added end


   private void printDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String process = getActionMacroProcess(file_action);
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;

      // Added by Terry 20120926
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int print_buffer_index = ctx.readNumber("PRINT_BUFFER_INDEX", 0);
      // Added end
      
      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }

      // Set additional macro attributes
      macro_attributes = doc_hdlr.getDocumentProperty("MACRO_ATTRIBUTES");

      err_msg = mgr.translate("DOCMAWEDMMACROPRINT: Print Document failed.\\n");
      
      // Modified by Terry 20120926
      // Add multi-print operation.
      if (edm_files != null && print_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(print_buffer_index).getValue("FILE_NO");
         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);
         
         //bug 58326, starts....
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, process);//Bug Id 65997
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, process, file_id,path);
         //bug 58326, ends....
         client_action = "PERFORM";
         
         // Store buffer and index, and restart action.
         print_buffer_index = print_buffer_index + 1;
         if (print_buffer_index < edm_files.countItems())
         {
            restartFileAction(true);
         }
         
         ctx.writeNumber("PRINT_BUFFER_INDEX", print_buffer_index);
      }
      else
      {
         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
         clearSelectionToCtx();
      }
   }

   
   private void getDocumentsForEMail(String file_action) throws FndException
   {
      ASPManager mgr = getASPManager();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String doc_type = doc_hdlr.getDocType();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
       //bug 58326
      String file_id = null;
      String macro = "";
      String action ="";
      String main_function = "";
      String doc_title = null;
      
      // Added by Terry 20120926
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int getdocforemail_buffer_index = ctx.readNumber("GETDOCFOREMAIL_BUFFER_INDEX", 0);
      // Added end
      
      if (DEBUG) debug("debug: getDocumentsToEMail..");
      if (DEBUG) debug("debug: file_name = " + file_name);
      
      // Modified by Terry 20120926
      // Add multi-getDocumentsForEMail operation.
      if (edm_files != null && getdocforemail_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(getdocforemail_buffer_index).getValue("FILE_NO");
         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);

         String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         boolean redline_exists = "TRUE".equals(getStringAttribute(edmInfo, "REDLINE_EXIST"));
         boolean view_exists = "TRUE".equals(getStringAttribute(edmInfo, "VIEW_REF_EXIST"));
         //bug 58326, starts....
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, "VIEW", redline_exists, view_exists);//Bug Id 65997
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, "VIEW", file_id,path);
         //bug 58326, ends....
         client_action = "PERFORM";
         
         // Store buffer and index, and restart action.
         getdocforemail_buffer_index = getdocforemail_buffer_index + 1;
         if (getdocforemail_buffer_index < edm_files.countItems())
         {
            restartFileAction(true);
         }
         
         ctx.writeNumber("GETDOCFOREMAIL_BUFFER_INDEX", getdocforemail_buffer_index);
      }
      else
      {
         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
      }
   }

   
   private void sendMailDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();

      if (DEBUG) debug("debug: sendMailDocument..");

      err_msg = mgr.translate("DOCMAWEDMMACROSENDMAILFAILED: Send Mail failed.\\n");

      //Bug Id 71831, Start
      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }
      //Bug Id 71831, End

      if (!ctx.readFlag("FILES_COPIED", false))
      {
         if (DEBUG) debug("debug: sendMailDocument..");
         getDocumentsForEMail(file_action);
         
         ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
         
         if (edm_files != null && edm_files.countItems() > 0 && ctx.readNumber("GETDOCFOREMAIL_BUFFER_INDEX", 0) >= edm_files.countItems())
         {
            ctx.writeFlag("FILES_COPIED", true);
            restartFileAction(true);
         }
         //Bug Id 71831, Start
         if (!mgr.isEmpty(getPageProperty("NAME_FILES_USING")))
         {
            setPageProperty("NAME_FILES_USING", getPageProperty("NAME_FILES_USING"));
         }
         //Bug Id 71831, End
      }
      else
      {
         String doc_title = getDocumentTitle(doc_class, doc_no);
         mail_msg_subject = mgr.translate("DOCMAWEDMMACROMAILSUBJECT: Regarding") + ": " + doc_title;
         mail_msg_body = mgr.translate("DOCMAWEDMMACROMAILTITLE: Title") + ": " + doc_title + "\\n";
         mail_msg_body += mgr.translate("DOCMAWEDMMACROMAILDOCCLASS: Document Class") + ": " + doc_class + "\\n";
         mail_msg_body += mgr.translate("DOCMAWEDMMACROMAILDOCNO: Document Number") + ": " + doc_no + "\\n";
         mail_msg_body += mgr.translate("DOCMAWEDMMACROMAILDOCSHEET: Document Sheet") + ": " + doc_sheet + "\\n";
         mail_msg_body += mgr.translate("DOCMAWEDMMACROMAILDOCREVISION: Document Revision") + ": " + doc_rev + "\\n";
         mail_msg_body += mgr.translate("DOCMAWEDMMACROMAILSTATUS: Status") + ": " + getDocumentState(doc_class, doc_no, doc_sheet, doc_rev);

         String file_exts = getPageProperty("FILE_EXTS_DOWNLOADED");
         String docType = "";
         if (sendComment)
         {
            docType = "REDLINE";
         }
         else
         {
            docType = "ORIGINAL";
         }

         // Modified by Terry 20120927
         // Loop set file list
         ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
         if (edm_files != null && edm_files.countItems() > 0)
         {
            ArrayList<String> mail_file_list = new ArrayList<String>();
            
            for (int i = 0; i < edm_files.countItems(); i++)
            {
               this.file_no = edm_files.getBufferAt(i).getValue("FILE_NO");
               
               String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, docType);
               String local_checkout_path = getLocalCheckOutPath();
               //Bug Id 71831, Start
               String user_file_name = DocmawUtil.getAttributeValue(getEdmInformation(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), doc_hdlr.getDocType()), "USER_FILE_NAME");
               String base_file_name;
               
               if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")) && !mgr.isEmpty(user_file_name))
               {
                  base_file_name = user_file_name; // DocmawUtil.getBaseFileName(user_file_name);           
               }
               else
               {        
                  base_file_name = getStringAttribute(edm_rep_info, "COPY_OF_FILE_NAME"); // DocmawUtil.getBaseFileName(getStringAttribute(edm_rep_info, "COPY_OF_FILE_NAME"));  //bug id 74523 get String attribute
               }        
               // Bug Id 71831, End
               
               // StringTokenizer st = new StringTokenizer(file_exts, "^");
               
               
               //Bug Id 71463, start
               if (file_no == null)
               {
                  file_no = "1";
               }
               
               // doc_keys = "(" + doc_class + " - " + doc_no + " - " + doc_sheet + " - " + doc_rev + " - " + file_no + ")";
               // String sFileExt;
               // Bug Id 71463, end
               // mail_file_attachments = new String[st.countTokens()];
               
               // Bug Id 71463, start
               // mail_file_extensions = new String[st.countTokens()];
               
               //Bug Id 71463, end 
               //Bug Id 71831, Start
               String mail_file_name = base_file_name;
               // for (int x = 0; st.hasMoreTokens(); x++)
               // {
               //    sFileExt = st.nextToken();
               //    mail_file_name = base_file_name+"."+sFileExt;
                  
                  if (MAX_FILE_NAME_LENGTH >= (mail_file_name.length() + path.length())) 
                  {
                     // mail_file_attachments[x] = mail_file_name;
                     mail_file_list.add(mail_file_name);
                  }
                  else
                  {
                     //Bug id 74523,Start
                     try
                     {
                        // mail_file_attachments[x] = adjustFileName(doc_class, doc_no, doc_sheet, doc_rev, file_no, path, mail_file_name);
                        mail_file_list.add(adjustFileName(doc_class, doc_no, doc_sheet, doc_rev, file_no, path, mail_file_name));
                     }
                     catch(Exception any)
                     {
                     }
                     //Bug id 74523,End                
                  }
                  // mail_file_extensions[x] = sFileExt;
               // }
               //Bug Id 71831, End
               //mail_file_attachments = removeRepeations(mail_file_attachments);
               
               //Bug Id 71463, start
               //mail_file_extensions = removeRepeations(mail_file_extensions);
               //Bug Id 71463, end
            }
            mail_file_attachments = new String[mail_file_list.size()];
            mail_file_list.toArray(mail_file_attachments);
            mail_file_list.clear();
            path = path + "temp\\";
            clearSelectionToCtx();
         }
         // Modified end
         client_action = "SEND_MAIL";
      }
   }

   private void sendFilesInTheFolderByMail()
   {
      mail_msg_subject    = doc_hdlr.getDocumentProperty("MAIL_MSG_SUBJECT");
      mail_msg_body       = doc_hdlr.getDocumentProperty("MAIL_MSG_BODY");
      mail_folder         = doc_hdlr.getDocumentProperty("MAIL_FOLDER");
      mail_client_message = doc_hdlr.getDocumentProperty("MAIL_CLIENT_MESSAGE");
      mail_zip_file_name  = doc_hdlr.getDocumentProperty("MAIL_ZIP_FILE_NAME");
      // Bug Id 89383, start
      mail_to             = doc_hdlr.getDocumentProperty("MAIL_TO");
      // Bug Id 89383, end

      client_action       = "SEND_FILES_IN_FOLDER_BY_MAIL";
   }


   private void zipAndEmail()
   {  //Bug Id 71463, start
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();

       if (file_no == null) {
	     file_no = "1";
	 }

      String doc_key = "(" + doc_class + " - " + doc_no + " - " + doc_sheet + " - " + doc_rev +" - " + file_no + ")";
      //Bug Id 71463, end

      zip_file_name = doc_hdlr.getDocumentProperty("ZIP_FILE_NAME");

      //Bug Id 71463, start
      if (MAX_FILE_NAME_LENGTH < zip_file_name.length()) 
      {
	 zip_file_name = zip_file_name.substring(0, MAX_FILE_NAME_LENGTH - 4) + ".zip";
      }
      //Bug Id 71463, end
      
      zip_file_name = zip_file_name.replaceAll("\\\\", "\\\\\\\\");

      local_path_name = doc_hdlr.getDocumentProperty("ROOT_DIRECTORY");
      local_path_name = local_path_name.replaceAll("\\\\", "\\\\\\\\");

      file_list = doc_hdlr.getDocumentProperty("FILE_LIST");
      //Bug Id 71463, start
      StringTokenizer file_name = new StringTokenizer(file_list, ""+DocmawUtil.FIELD_SEPARATOR);
      String sfile_name = "";
      file_list = "";
      for (int x = 0; file_name.hasMoreTokens(); x++) 
      {
	 sfile_name = file_name.nextToken();

	 if (MAX_FILE_NAME_LENGTH >= sfile_name.length()) 
	 {
	     sfile_name = sfile_name + DocmawUtil.FIELD_SEPARATOR;
	 }
         else
	 {
	     String file_ext = sfile_name.substring(sfile_name.lastIndexOf(".") + 1,sfile_name.length());
	     String empty_path = ""; //zip files contains the path in filename
	     //bug id 74523, Start
	     try{
	     
	     sfile_name = adjustFileName(doc_class,doc_no,doc_sheet,doc_rev,file_no,empty_path,sfile_name)+DocmawUtil.FIELD_SEPARATOR;
	     }
	     catch(Exception any){
	     }
	     
	    
	     //bug id 74523, End 
	 }

	 file_list = file_list + sfile_name;
      }
      //Bug Id 71463, end
      file_list = file_list.replaceAll("\\\\", "\\\\\\\\");

      mail_msg_subject = doc_hdlr.getDocumentProperty("MAIL_SUBJECT");
      mail_msg_body = doc_hdlr.getDocumentProperty("MAIL_BODY");

      client_action = "ZIP_AND_EMAIL";
   }


   private void getCopyToDir() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String doc_type = "ORIGINAL";

       //bug 58326
      String file_id  = null;
      copyToDirPath = doc_hdlr.getActionProperty("SELECTED_DIRECTORY");
      sameActionToAll = doc_hdlr.getActionProperty("SAME_ACTION_TO_ALL");

      //we do not need macros for Copy file to..
      String macro = "";
      String action = "";
      String main_function = "";
      String doc_title = null;

      // Added by Terry 20120926
      ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
      int getcopytodir_buffer_index = ctx.readNumber("GETCOPYTODIR_BUFFER_INDEX", 0);
      // Added end
      
      //Bug Id 71831, Start
      if ("ORIGINAL_FILE_NAME".equals(getPageProperty("NAME_FILES_USING")))
      {
         use_original_file_name = true;
      }
      //Bug Id 71831, End

      err_msg = mgr.translate("GETCOPYTODIR: Copying documents to directory failed.\\n");
      
      // Modified by Terry 20120926
      // Add multi-getcopytodir operation.
      if (edm_files != null && getcopytodir_buffer_index < edm_files.countItems())
      {
         this.file_no = edm_files.getBufferAt(getcopytodir_buffer_index).getValue("FILE_NO");
         ctx.writeFlag("DOC_OPERATION_PROCESSING", true);
         
         String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         boolean redline_exists = "TRUE".equals(getStringAttribute(edmInfo, "REDLINE_EXIST"));
         boolean view_exists = "TRUE".equals(getStringAttribute(edmInfo, "VIEW_REF_EXIST"));
         
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, redline_exists, view_exists);
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, "VIEW", file_id,path);
         client_action = "PERFORM";
         
         // Store buffer and index, and restart action.
         getcopytodir_buffer_index = getcopytodir_buffer_index + 1;
         if (getcopytodir_buffer_index < edm_files.countItems())
         {
            restartFileAction(true);
         }
         
         ctx.writeNumber("GETCOPYTODIR_BUFFER_INDEX", getcopytodir_buffer_index);
      }
      else
      {
         ctx.writeFlag("DOC_OPERATION_PROCESSING", false);
         clearSelectionToCtx();
      }
   }


   public void connectReport() throws FndException //();
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String process = getActionMacroProcess(file_action);
      String local_file_name = doc_hdlr.getDocumentProperty("LOCAL_FILE_NAME");

      String TransmittalId = doc_hdlr.getDocumentProperty("TRANSMITTAL_ID");
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;
      String file_type = doc_hdlr.getFileType();

      //String original_file_name = getPageProperty("LOCAL_FILE_NAME");
      String original_file_name    = "Report.pdf";
      String files_checked_in      = original_file_name + "|";
      String view_files_checked_in = original_file_name;

      err_msg = mgr.translate("DOCMAWEDMMACROCREATEREPORTFAILED: Create Report document failed.\\n");

      file_id = getRandomFilename();
      String tempPath = getTmpPath();
      String fullFileName = tempPath +(tempPath.endsWith("\\")?"":"\\") + file_id + ".pdf";//extension is hardcoded here.

      
      try{
        orderReport(fullFileName,TransmittalId);
        }catch( java.io.IOException e){}
      //now report is in our temp path 



      //String files_checked_in = getPageProperty("FILES_CHECKED_IN");
     
          //bug 58326
      local_file_names = checkInDocument(doc_class,
                                            doc_no,
                                            doc_sheet,
                                            doc_rev,
                                            file_type,
                                            doc_type,
                                            file_id,
                                            files_checked_in,
                                            view_files_checked_in,
                                            original_file_name);
         client_action = null;

      
   }




   public void createNewDoc() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String process = getActionMacroProcess(file_action);
      String local_file_name = doc_hdlr.getDocumentProperty("LOCAL_FILE_NAME");
       //bug 58326
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;
      String file_type = doc_hdlr.getFileType();
      String original_file_name = getPageProperty("LOCAL_FILE_NAME");

      err_msg = mgr.translate("DOCMAWEDMMACRONEWDOCCHECKINFAILED: Check In new document failed.\\n");

      // Added by Terry 20121026
      // Create file with file no 1.
      this.file_no = "1";
      // Added end
      
      if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
          //bug 58326, starts....
         file_id = getRandomFilename();
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, local_file_name, action, main_function, file_action, "CHECKIN", file_id,path);
         ctx.writeValue("UNIQUEFILENAME", file_id);
         //bug 58326, ends....
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
         //bug 58326
         file_id = ctx.readValue("UNIQUEFILENAME","");
         String files_checked_in = getPageProperty("FILES_CHECKED_IN");
         String view_files_checked_in = getPageProperty("VIEW_FILES_CHECKED_IN");
          //bug 58326
         local_file_names = checkInDocument(doc_class,
                                            doc_no,
                                            doc_sheet,
                                            doc_rev,
                                            file_type,
                                            doc_type,
                                            file_id,
                                            files_checked_in,
                                            view_files_checked_in,
                                            original_file_name);
         client_action = "DELETE_CHECKED_IN_FILES";
      }
   }


   private void importFile() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getDocumentProperty("FILE_TYPE");
      String process = getActionMacroProcess(file_action);
       //bug 58326
      String file_id = null;
      String local_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String doc_title =  doc_hdlr.getDocumentProperty("DOC_TITLE");
      String num_generator = doc_hdlr.getDocumentProperty("NUMBER_GENERATOR");
      String booking_list = doc_hdlr.getDocumentProperty("BOOKING_LIST");
      String id1 = doc_hdlr.getDocumentProperty("ID1");
      String id2 = doc_hdlr.getDocumentProperty("ID2");

      String check_out_path = getLocalCheckOutPath();
      err_msg = mgr.translate("IMPORTFILEFAILED: Import file failed.\\n");

      if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
          //bug 58326, starts....
         file_id = getRandomFilename();
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, local_file_name, null, null, file_action, process, file_id,path);
         ctx.writeValue("UNIQUEFILENAME", file_id);
         //bug 58326, ends....
         ctx.writeFlag("XML_FILE_CREATED", true);
         restartFileAction(true);
         client_action = "PERFORM";
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
          //bug 58326
         file_id = ctx.readValue("UNIQUEFILENAME","");
         String files_checked_in = getPageProperty("FILES_CHECKED_IN");
         String view_files_checked_in = getPageProperty("VIEW_FILES_CHECKED_IN");
          //bug 58326
         local_file_names = createNewImportFile(file_id, check_out_path, doc_class, (mgr.isEmpty(doc_no) || "null".equals(doc_no.toLowerCase()))?"":String.valueOf(doc_no), doc_sheet, doc_rev, file_type, doc_title, files_checked_in, num_generator, booking_list, id1, id2, view_files_checked_in);
         client_action = "TRUE".equals(doc_hdlr.getActionProperty("DELETE_IMPORTED_FILES"))?"DELETE_IMPORTED_FILES":"";
      }
   }


   //Bug Id 81807, start
   private void downloadTransmittalFiles() throws FndException
   {
      ASPManager mgr = getASPManager();
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_no = doc_hdlr.getDocNo();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_rev = doc_hdlr.getDocRev();
      String file_type = doc_hdlr.getFileType();
      String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      String process = getActionMacroProcess(file_action);
      String file_id = null;
      String macro = getSelectedMacro();
      String action = getMacroAction(macro);
      String main_function = getMacroMainFunction(macro);
      String doc_title = null;
      
      if ("TRANSMITTALDOWNLOAD".equals(file_action))
      {
         file_action = "VIEW";
         doc_hdlr.setDocumentProperty("FILE_ACTION","VIEW");
      }
      
      if ("ALL".equals(doc_type)) 
      {
         doc_type = "ORIGINAL";
         String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         boolean redline_exists = false;
         boolean view_exists = "TRUE".equals(getStringAttribute(edmInfo, "VIEW_REF_EXIST"));
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, redline_exists, view_exists);
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, "GETCOPYTODIR", process, file_id,path);
      }
      else
      {
         if ("VIEW".equals(doc_type)) 
         {
            String file_ext = getFileExtension(file_type);
            file_name = file_name.substring(0, file_name.lastIndexOf(".") + 1) + file_ext;
            String edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
            boolean view_exists = "TRUE".equals(getStringAttribute(edmInfo, "VIEW_REF_EXIST"));
            
            if (!view_exists)
            {
               doc_type = "ORIGINAL";
               bNoViewRefExist = true;
               ctx.writeFlag("NO_VIEW_REF_EXIST", true);
            }
         }
         file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type, process);
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, doc_title, file_type, file_name, action, main_function, file_action, process, file_id,path);
      }

      client_action = "PERFORM";
   }

   //Bug Id 81807, end

   private void launchLocalFile()
   {
      local_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      local_file_name = local_file_name.replaceAll("\\\\", "\\\\\\\\");
      client_action = "LAUNCH_LOCAL_FILE";
   }


   private void launchLocalFileWithExternalViewer()
   {
      local_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      local_file_name = local_file_name.replaceAll("\\\\", "\\\\\\\\");
      client_action = "LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER";
   }


   private void openLocalFolder()
   {
      local_path_name = doc_hdlr.getDocumentProperty("PATH");
      local_path_name = local_path_name.replaceAll("\\\\", "\\\\\\\\");
      client_action = "OPEN_LOCAL_FOLDER";
   }


   private void deleteLocalFile()
   {
      local_file_name = doc_hdlr.getDocumentProperty("FILE_NAME");
      local_file_name = local_file_name.replaceAll("\\\\", "\\\\\\\\");
      client_action = "DELETE_LOCAL_FILE";
   }


   private void selectAndExecuteMacro()
   {
      ASPManager mgr = getASPManager();
      String macro = getSelectedMacro();
      String buf_no = doc_hdlr.getDocumentProperty("DOCUMENT_BUF_NO");

      int count = 0;
      if (!mgr.isEmpty(buf_no))
      {
         // Get document buffer no to set macro on..
         count = Integer.parseInt(buf_no);
      }

      if (!mgr.isEmpty(getMacroAction(macro)))
      {
         // Inject selected macro into the last document
         doc_hdlr.setDocumentPropertyAt(count, "MACRO", macro);
      }
   }


   private void initEdmMacro() throws FndException
   {
      ASPManager mgr = getASPManager();

      String use_non_ie_solution = mgr.getConfigParameter("DOCMAW/USE_NON_IE_SOLUTION", "FALSE");

      // Initialize rowset..
      fileset.clear();
      fileset.addRowNoStatus(null);

      // Added by Terry 20130307
      // Check doc control, and transfer to doc control page.
      if ("VIEW".equals(getFileAction()))
      {
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd = trans.addCustomFunction("GETEDMCTL", "Doc_Class_API.Get_Doc_Control", "OUT_1");
         cmd.addParameter("IN_STR1", doc_hdlr.getDocClass());
         trans = mgr.perform(trans);
         String edm_ctl = trans.getValue("GETEDMCTL/DATA/OUT_1");
         if ("TRUE".equals(edm_ctl))
         {
            transferToDocControl();
            return;
         }
      }
      // Added end
      
      if (!mgr.isExplorer() || "TRUE".equals(use_non_ie_solution))
      {
         transferToNonIEPage();
         return;
      }

      // Initialize log buffer..
      if (error_log == null)
         error_log = mgr.newASPBuffer();

      /*
       * moved up ^^^ // Initialize rowset.. fileset.clear();
       * fileset.addRow(null);
       */

      // If only one document was passed, and if it is a Structure
      // Document transfer it to DocStructure.
      if (doc_hdlr.countDocuments() == 1 && !"DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE")))
      {
         // Bug Id 73718, changed the if condition
         if (!"REDLINE".equals(doc_hdlr.getDocType())
               && isStructureProcess(getFileAction())
               && isStructureDocument(doc_hdlr.getDocClass(), doc_hdlr.getDocNo()))
         {
            // Perform checkAccess on the current structure document, to ensure
            // that the
            // user is not trying to do something illegal
            checkAccess(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), doc_hdlr.getDocType(), getActionMacroProcess(getFileAction()));

            // Transfer to the DocStructure page..
            doc_hdlr.transferDataTo("DocStructure.page");
         }
      }

      setPageProperty("MACRO");
      setPageProperty("MACRO_SELECTED");
      setPageProperty("LOCAL_CHECKOUT_PATH");
      // Added by Terry 20121018
      // Save local download path
      setPageProperty("LOCAL_DOWNLOAD_PATH");
      // Added end 
      setPageProperty("LOCAL_FILE_NAME");
      setPageProperty("FILES_CHECKED_IN");
      setPageProperty("FILE_EXTS_DOWNLOADED");
      setPageProperty("APP_FILE_TYPE");
      setPageProperty("SELECTED_DIRECTORY");
      setPageProperty("CONFIRM");
      setPageProperty("EXTERNAL_VIEWER");
      setPageProperty("SELECTED_FILE_NAME");

      if (!"DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE")))
      {
         String file_action = getFileAction();
         if ("CHECKIN".equals(file_action) || "CHECKINSEL".equals(file_action))
            checkInPath = DocmawUtil.getPath(getPageProperty("SELECTED_FILE_NAME"));
      }

      if (!mgr.isEmpty(getPageProperty("APP_FILE_TYPE")))
      {
         doc_hdlr.setFileType(getPageProperty("APP_FILE_TYPE"));
      }

      if (!mgr.isEmpty(getPageProperty("SELECTED_DIRECTORY")))
      {
         doc_hdlr.setActionProperty("SELECTED_DIRECTORY", getPageProperty("SELECTED_DIRECTORY"));
      }

      if (!mgr.isEmpty(mgr.readValue("VIEW_FILES_CHECKED_IN")))
      {
         setPageProperty("VIEW_FILES_CHECKED_IN", mgr.readValue("VIEW_FILES_CHECKED_IN"));
      }
   }



   private void logOperation(String status)
   {
      ASPManager mgr = getASPManager();

      if (DEBUG) debug("debug: logging error = " + status);

      if (error_log.countItems() == 0)
      {
         error_log = mgr.newASPBuffer();
      }

      ASPBuffer buf = mgr.newASPBuffer();
      buf.addItem("DOC_CLASS", doc_hdlr.getDocClass());
      buf.addItem("DOC_NO", doc_hdlr.getDocNo());
      buf.addItem("DOC_SHEET", doc_hdlr.getDocSheet());
      buf.addItem("DOC_REV", doc_hdlr.getDocRev());
      buf.addItem("TITLE", doc_hdlr.getDocumentProperty("TITLE"));
      buf.addItem("STATUS", status);
      error_log.addBuffer("DATA", buf);
      error_log.traceBuffer("debug: Error Log...");
   }

   //Bug 73736, Start
   private void updateLastOpLog()
   {
       ASPManager mgr = getASPManager();
       ASPBuffer buf = mgr.newASPBuffer();
       int nLast = error_log.countItems()-1;

       buf = error_log.getBufferAt(nLast);
       buf.setValue("STATUS",getWritableMessage());
       error_log.removeItemAt(nLast);
       error_log.addBuffer("DATA", buf);

       error_log.traceBuffer("DEBUG: Updated Error Log...");
   }
   //Bug 73736, End

   private boolean isStructureProcess(String file_action)
   {
      if ("CREATENEW".equals(file_action) || "CHECKIN".equals(file_action) || "CHECKOUT".equals(file_action) || "DOWNLOAD".equals(file_action) ||
          "UNDOCHECKOUT".equals(file_action) || "VIEW".equals(file_action) || "PRINT".equals(file_action) ||
          "SENDMAIL".equals(file_action) || "GETCOPYTODIR".equals(file_action) || "VIEWWITHEXTVIEWER".equals(file_action)|| 
          "VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) //Bug Id 73718
         return true;
      else
         return false;
   }


   private void transferToNonIEPage() throws FndException
   {
      ASPManager mgr = getASPManager();
      //Bug Id 78201, Start
      if (Str.isEmpty(doc_hdlr.getDocRev()))
       {
          String releasedRev = doc_hdlr.getDocumentProperty("RELEASED");
	  String latestRev = doc_hdlr.getDocumentProperty("LATEST_REV");
	  if ((releasedRev != null) || (latestRev != null))
          {
             if(releasedRev !=null)
                releasedRev = releasedRev.toUpperCase();
             if (latestRev !=null)
                latestRev = latestRev.toUpperCase();

             doc_hdlr.setDocumentProperty ("DOC_REV", getRevision(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), latestRev, releasedRev));
          }
       }
      //Bug Id 78201, End

      ASPBuffer buf = mgr.newASPBuffer();
      buf.addItem("DOC_CLASS",  doc_hdlr.getDocClass());  
      buf.addItem("DOC_NO",     doc_hdlr.getDocNo());
      buf.addItem("DOC_SHEET",  doc_hdlr.getDocSheet());
      buf.addItem("DOC_REV",    doc_hdlr.getDocRev());
      buf.addItem("RELEASED",   doc_hdlr.getDocumentProperty("RELEASED"));
      buf.addItem("LATEST_REV", doc_hdlr.getDocumentProperty("LATEST_REV"));
      buf.addItem("FILE_TYPE",  doc_hdlr.getFileType());
      buf.addItem("DOC_TYPE",   doc_hdlr.getDocType());
      buf.addItem("PROCESS_DB", getFileAction());

      checkAccess(doc_hdlr.getDocClass(),doc_hdlr.getDocNo(),doc_hdlr.getDocSheet(),doc_hdlr.getDocRev(),doc_hdlr.getDocType(),getActionMacroProcess(getFileAction()));

      mgr.transferDataTo("NonIEUploadDownloadDelete.page", buf);
   }


   /**
    * Returns the named property/parameter from
    * the current doc_hdlr or if not found, from
    * the context cache
    */
   private String getPageProperty(String property)
   {
      ASPManager mgr = getASPManager();
      String value = mgr.readValue(property);
      if (mgr.isEmpty(value))
         value = ctx.readValue(property, null);
      return value;
   }


   /**
    * Saves the named property/parameter found in
    * the current doc_hdlr or, if it does not exist,
    * in the context cache, to the context cache
    */
   private void setPageProperty(String property)
   {
      String value = getPageProperty(property);
      ctx.writeValue(property, value);
   }


   /**
    * Saves the named property/parameter with the
    * given value in the context cache
    */
   private void setPageProperty(String property, String value)
   {
      ctx.writeValue(property, value);
   }


   /**
    * This method returns the local checkout path which is determined
    * in one of the following ways, in the given order:
    *
    *    1) From the "FILE_PATH" attribute, if it is specified
    *    2) From the "FILE_NAME" attribute which contains the
    *       absolute file name
    *    3) Defaults to the document folder configured in the
    *       registry
    */
   private String getLocalCheckOutPath()
   {
      ASPManager mgr = getASPManager();

      // Get check out path from the path specified..
      String local_path = doc_hdlr.getDocumentProperty("FILE_PATH");

      if (!mgr.isEmpty(local_path))
         return local_path;

      // Get check out path from the absolute file name..
      String local_file = doc_hdlr.getDocumentProperty("FILE_NAME");

      if (!mgr.isEmpty(local_file))
         return DocmawUtil.getPath(local_file);

      // Default to the document folder configured in the registry..
      return getPageProperty("LOCAL_CHECKOUT_PATH");
   }

   // Added by Terry 20121018
   // Get local download path
   private String getLocalDownloadPath()
   {
      ASPManager mgr = getASPManager();

      // Get download path from the path specified..
      String local_path = doc_hdlr.getDocumentProperty("FILE_PATH");

      if (!mgr.isEmpty(local_path))
         return local_path;

      // Get download path from the absolute file name..
      String local_file = doc_hdlr.getDocumentProperty("FILE_NAME");

      if (!mgr.isEmpty(local_file))
         return DocmawUtil.getPath(local_file);

      // Default to the document folder configured in the registry..
      return getPageProperty("LOCAL_DOWNLOAD_PATH");
   }
   // Added end

   /**
    * Returns the local file name
    **/
   private String getLocalFileName() throws FndException
   {
      ASPManager mgr = getASPManager();
      String local_file;

      // Try to get the filename from the page properties
      local_file = getPageProperty("LOCAL_FILE_NAME");

      if (!mgr.isEmpty(local_file))
         return local_file;

      // Try to get the local file name from the doc handler.
      local_file = doc_hdlr.getDocumentProperty("FILE_NAME");

      if (!mgr.isEmpty(local_file))
         return local_file;

      if (!mgr.isEmpty(local_file))
         return DocmawUtil.getFileName(local_file);
      else
         return getLocalFileName(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), "ORIGINAL");
   }


   private String getLaunchFile()
   {
      ASPManager mgr = getASPManager();

      String launch_file = doc_hdlr.getDocumentProperty("LAUNCH_FILE");

      if (mgr.isEmpty(launch_file))
      {
         launch_file = doc_hdlr.getActionProperty("LAUNCH_FILE");
      }

      return launch_file;
   }

   // Added by Terry 20140730
   // Check DOC_FOLDER parameter
   private String getDocFolder()
   {
      ASPManager mgr = getASPManager();

      String doc_folder = doc_hdlr.getDocumentProperty("DOC_FOLDER");

      if (mgr.isEmpty(doc_folder))
      {
         doc_folder = doc_hdlr.getActionProperty("DOC_FOLDER");
      }
      return doc_folder;
   }
   // Added end

   private String getFileAction()
   {
      ASPManager mgr = getASPManager();

      String file_action = doc_hdlr.getDocumentProperty("FILE_ACTION");

      if (mgr.isEmpty(file_action))
      {
         file_action = doc_hdlr.getActionProperty("FILE_ACTION");
      }

      return file_action;
   }


   private boolean logError()
   {
      String log_error = doc_hdlr.getActionProperty("LOG_ERROR");
      String file_action = getFileAction();

      if ("TRUE".equals(log_error))
      {
         if ("CHECKIN".equals(file_action) || "CHECKOUT".equals(file_action) || "VIEW".equals(file_action) || "DOWNLOAD".equals(file_action) ||
             "PRINT".equals(file_action) || "GETCOPYTODIR".equals(file_action) || "UNDOCHECKOUT".equals(file_action)|| "SENDMAIL".equals(file_action)||
             "CREATENEW".equals(file_action) || "GET_DOCUMENTS_FOR_EMAIL".equals(file_action)) //Bug Id 72460: added SENDMAIL //Bug Id 74989: added CREATENEW //Bug Id 73728: added GET_DOCUMENTS_FOR_EMAIL
            return true;
      }

      return false;
   }


   private boolean clientError()
   {
      ASPManager mgr = getASPManager();
      return "ERROR".equals(mgr.readValue("CLIENT_STATUS"));
   }


   private void logClientError()
   {
      ASPManager mgr = getASPManager();

      if (logError())
         logOperation(mgr.readValue("CLIENT_ERROR"));      
   }


   /*
   private void logServerError(String sever_error)
   {
      ASPManager mgr = getASPManager();

      if (logError())
         logOperation(sever_error);
   }
   */

   private String getOppSuccessMessage()
   {
      ASPManager mgr = getASPManager();

      String file_action = getFileAction();
      String message = "";
      
      if ("CHECKIN".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULCHECKIN: Checked in sucessfully");
      if ("CHECKOUT".equals(file_action) ||"CREATENEW".equals(file_action)) //Bug Id 72460 //Bug Id 74989: added CREATENEW
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULCHECKEDOUT: Checked out sucessfully");
      //Bug Id 80882, Start
      if ("VIEW".equals(file_action))
      {  
         if (bPrintView) {
            message = mgr.translate("DOCMAWEDMMACROSUCESSFULPRINTVIEW: Checked out for printing successfully.");
         }
         else
         {
            message = mgr.translate("DOCMAWEDMMACROSUCESSFULVIEW: Checked out Sucessfully for View.");
         }
      }
      if ("PRINT".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULPRINT: Printed Successfully.");
      if ("GETCOPYTODIR".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULCOPY: Copied Successfully.");
      if ("GET_DOCUMENTS_FOR_EMAIL".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULMAIL: Checked out Successfully for Send Mail.");
      //Bug Id 80882, End
      
      if ("UNDOCHECKOUT".equals(file_action))
      //Bug ID 62489, Start
      {
         String del_file_opt = getDeleteFileOption();
         if ("NO".equals(del_file_opt))
            message = mgr.translate("DOCMAWEDMMACROSUCESSFULUNDOCHECKOUT1: Unreserved");
         else
            message = mgr.translate("DOCMAWEDMMACROSUCESSFULUNDOCHECKOUT2: Unreserved and deleted");
      }
      //Bug ID 62489, End

      // Added by Terry 20121018
      if ("DOWNLOAD".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROSUCESSFULDOWNLOAD: Download sucessfully");
      // Added end
      return message;
   }


   private String getWritableMessage()
   {
      ASPManager mgr = getASPManager();

      // For now, its the same message for all operations
      return mgr.translate("DOCMAWEDMMACROPROCESSFILEWRITEABLE: Not checked out, prevented file overwrite");
   }



   private void clearCurrentOperationState()
   {
      ctx.writeFlag("XML_FILE_CREATED", false);
   }


   private void generateErrorLog()
   {
      ASPManager mgr = getASPManager();

      if (DEBUG) debug("debug: Showing error log..");

      ASPBuffer header = error_log.addBufferAt("HEADER", 0);
      header.addItem("REPORT_TITLE", doc_hdlr.getDocumentProperty("REPORT_TITLE"));

      String serialized_data = mgr.pack(error_log);
      url = "DocumentReport.page?__TRANSFER=" + serialized_data;
      client_action = "SHOW_ERROR_LOG";
   }


   private String getPageTitle()
   {
      ASPManager mgr = getASPManager();

      String title = doc_hdlr.getDocumentProperty("EDM_TITLE");

      if (mgr.isEmpty(title))
      {
         title = doc_hdlr.getActionProperty("EDM_TITLE");
      }

      return title;
   }


   private String getDeleteFileOption()
   {
      ASPManager mgr = getASPManager();

      String delete_file = doc_hdlr.getDocumentProperty("DELETE_FILE");

      if (mgr.isEmpty(delete_file))
      {
         delete_file = doc_hdlr.getActionProperty("DELETE_FILE");
      }

      return delete_file;
   }


   private boolean isMacroDisabled()
   {
      ASPManager mgr = getASPManager();

      String execute_macro = doc_hdlr.getDocumentProperty("EXECUTE_MACRO");

      if (mgr.isEmpty(execute_macro))
      {
         execute_macro = doc_hdlr.getActionProperty("EXECUTE_MACRO");
      }

      return "NO".equals(execute_macro);
   }



   /**
    * Checks to see if the current file action allows configurable
    * checkout file names
    */
   private boolean isFileNameConfigurable() throws FndException
   {
      ASPManager mgr = getASPManager();

      String action = getFileAction();
      String doc_class = doc_hdlr.getDocClass();
      String user_file_name = DocmawUtil.getAttributeValue(getEdmInformation(doc_hdlr.getDocClass(), doc_hdlr.getDocNo(), doc_hdlr.getDocSheet(), doc_hdlr.getDocRev(), doc_hdlr.getDocType()), "USER_FILE_NAME");
      boolean file_name_config_disabled = "NO".equals(doc_hdlr.getActionProperty("FILE_NAME_CONFIGURABLE"));

      if (DEBUG) debug("debug: user_file_name = " + user_file_name);

      //Bug Id: 85455, start
      if ("YES".equals(doc_hdlr.getDocumentProperty("TRANSMITTAL_DOWNLOAD"))) {
         setPageProperty("NAME_FILES_USING", "DOCUMENT_TITLE_AND_NAME");
         return true;
      }
      //Bug Id: 85455, end

      // Modified by Terry 20121019
      // Add GETCOPYTODIR and DOWNLOAD operation file name using
      if (!file_name_config_disabled && !mgr.isEmpty(user_file_name) && ("CHECKOUT".equals(action) || "DOWNLOAD".equals(action) || "VIEW".equals(action) || "PRINT".equals(action) || "SENDMAIL".equals(action) || "GETCOPYTODIR".equals(action) || "DOWNLOAD".equals(action)))   //Bug Id 71831
      {

         //Bug Id 71831, Start
         if ("SENDMAIL".equals(action) || "GETCOPYTODIR".equals(action) || "DOWNLOAD".equals(action))
         {
            name_files_using = getNameFileUsing(doc_class, "VIEW");
         }
         else
            name_files_using = getNameFileUsing(doc_class, action);
         //Bug Id 71831, End

         if ("Force Original".equals(name_files_using))
         {
            setPageProperty("NAME_FILES_USING", "ORIGINAL_FILE_NAME");
         }

         if (DEBUG) debug("debug: name_files_using = " + name_files_using);
         if ("Default Title".equals(name_files_using) || "Default Original".equals(name_files_using))
            return true;
      }
      // Modified end
      return false;
   }


   /**
    * Returns true if the action has been confirmed
    */
   private boolean actionConfirmed()
   {
      return getPageProperty("CONFIRM") != null;
   }


   private boolean confirmAction()
   {
      ASPManager mgr = getASPManager();

      String confirm = doc_hdlr.getDocumentProperty("CONFIRM");

      if (mgr.isEmpty(confirm))
      {
         confirm = doc_hdlr.getActionProperty("CONFIRM");
      }

      if (mgr.isEmpty(confirm) || "YES".equals(confirm))
         return true;
      else
         return false;
   }

   /**
    *  Returns true if a macro has been selected
    */
   private boolean macroSelected()
   {
      return getPageProperty("MACRO") != null;
   }


   private boolean fileNameSelected()
   {
      debug("debug: NAME_FILES_USING = " + getPageProperty("NAME_FILES_USING"));
      return getPageProperty("NAME_FILES_USING") != null;
   }


   /**
    * Returns the selected macro
    */
   private String getSelectedMacro()
   {
      ASPManager mgr = getASPManager();

      // Check if a macro has been explicitly set
      // on the current document..

      String macro = doc_hdlr.getDocumentProperty("MACRO");

      if (!mgr.isEmpty(macro))
         return macro;
      else
         return getPageProperty("MACRO");
   }


   /**
    * Allows to set the macro selection. Useful
    * when <b>we</b> need to select the macro to
    * execute and not the user
    */
   private void setMacroSelection(String macro)
   {
      setPageProperty("MACRO", macro);
   }


   private void restartFileAction(boolean restart)
   {
      this.restart = restart;
   }


   private boolean restartFileAction()
   {
      return restart;
   }


   private void prepareClientAction()
   {
      preparePageHeader();

      resubmit = false;

      // Added by Terry 20120917
      // Can restart EdmMacro page in check in opertion.
      if ("CHECKE_IN_FILES".equals(client_action) && restartFileAction())
         resubmit = true;
      else if ("DELETE_CHECKED_IN_FILES".equals(client_action) && restartFileAction())
         resubmit = true;
      // Added end
      // Added by Terry 20120925
      // Can restart EdmMacro page in undu checkout operation.
      else if ("UNDO_CHECK_OUT_PROCESSING".equals(client_action) && restartFileAction())
         resubmit = true;
      // Added end
      // Added by Terry 20121019
      else if ("DOWNLOAD_COMP".equals(client_action))
      {
        if (!doc_hdlr.isLastDocument())
        {
           if (logError())
           {
              logOperation(getOppSuccessMessage());
           }
           setNextDocument();
           resubmit = true;
        }
        else
        {
           client_action = "DOWN_CLOSE_WINDOW";
        }
      }
      // Added end
      else if ("PERFORM".equals(client_action))
      {
         if (restartFileAction())
            resubmit = true;
         else if (doc_hdlr.isLastDocument())
            resubmit = false;
         else if (!doc_hdlr.isLastDocument())
         {
            if (logError())
            {
               logOperation(getOppSuccessMessage());
            }
            setNextDocument();
            resubmit = true;
         }
      }
      //Bug id 74523 added "GET_PATH_FIRST".equals(client_action)

      else if ("GET_LOCAL_CHECK_OUT_PATH".equals(client_action) ||
               // Added by Terry 20121018
               // Fetch local download path operation
               "GET_LOCAL_DOWNLOAD_PATH".equals(client_action)  ||
               // Added end
               "SELECT_DIRECTORY".equals(client_action)         ||
               "SHOW_CONFIRMATION".equals(client_action)        ||
               // Added by Terry 20120918
               "SHOW_INFORMATION".equals(client_action)         ||
               "SHOW_ALERTBOX".equals(client_action)            ||
               // Added end
               "CHECK_EXTERNAL_VIEWER".equals(client_action)    ||
               "SHOW_FILE_CHOOSER".equals(client_action)    ||
               "GET_PATH_FIRST".equals(client_action))
      {
         resubmit = true;
      }
      else if (Str.isEmpty(client_action))
      {
         if (!doc_hdlr.isLastDocument())
         {
            if (logError())
            {
               if (file_writable_error)
                  logOperation(getWritableMessage());
               else
                  logOperation(getOppSuccessMessage());
            }

            setNextDocument();
            resubmit = true;
         }
         else
         {
            client_action = "CLOSE_WINDOW";
         }
      }
      else if ("ERROR".equals(client_action)              ||
	       "DELETE_CHECKED_IN_FILES".equals(client_action) ||
          "DELETE_LOCAL_FILE".equals(client_action)       ||
          "OPEN_LOCAL_FOLDER".equals(client_action)       ||
          "ZIP_AND_EMAIL".equals(client_action)           ||
	       "DELETE_IMPORTED_FILES".equals(client_action)   ||
          "LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(client_action) ||
          "LAUNCH_LOCAL_FILE".equals(client_action))
      {
         if (!doc_hdlr.isLastDocument())
         {
            if (logError())
            {
               if ("ERROR".equals(client_action))
                   logOperation(err_msg);
               else              
                  logOperation(getOppSuccessMessage());
            }

            setNextDocument();
            resubmit = true;
         }
      }
   }


   private void cancel() throws FndException
   {
      // It never comes here. When the cancel
      // is pressed, the window is closed.
   }


   /**
    * Initialise variables/properties before starting
    * to process the next document.
    */
   private void setNextDocument()
   {
      ASPManager mgr = getASPManager();

      // If SAME_ACTION_TO_ALL is NO then clear the selected macro.
      // This will make the page to show the macro selection dialog
      // for the next document
      if ("NO".equals(doc_hdlr.getActionProperty("SAME_ACTION_TO_ALL")))
         setMacroSelection(null);

      // Need to get confirmation for the next document as well..
      if ("CHECKOUT".equals(getFileAction())
          || "DELETE".equals(getFileAction())
          || "DELETEALL".equals(getFileAction())
          || "DELETESEL".equals(getFileAction()) // Added by Terry 20120918
          || "DOWNLOAD".equals(getFileAction())  // Added by Terry 20121018
          || "VIEW".equals(getFileAction())
          || "PRINT".equals(getFileAction())
          || "GETCOPYTODIR".equals(getFileAction()))
      {
         setPageProperty("CONFIRM", null);
      }

      // Proceed to the next document..
      doc_hdlr.nextDocument();
  }


   //
   // Methods for handling macros
   //


   private String getMacroAction(String selected_macro)
   {
      return getStringAttribute(selected_macro, "ACTION");
   }


   private String getMacroMainFunction(String selected_macro)
   {
      return getStringAttribute(selected_macro, "MAIN_FUNCTION");
   }


   /**
    * Checks whether the named file action has
    * a coressponding MacroProcess
    */
   public boolean hasActionMacroProcess(String file_action)
   {
      if (!Str.isEmpty(getActionMacroProcess(file_action)))
      {
         return true;
      }
      else
      {
         return false;
      }
   }


   /**
    * Returns the MacroProcess that corresponds to the named
    * file action or null if the file action has no corresponding
    * MacroProcess
    */
   public String getActionMacroProcess(String file_action)
   {
      String macro_processes[] = EdmMacroProcess.getEdmMacroProcesses();

      if ("DELETEALL".equals(file_action))
      {
         return "DELETEALL";
      }
      else if ("CHECKOUTALREADY".equals(file_action))
      {
         return "CHECKOUT";
      }
      //else if ("FILEIMPORT".equals(file_action) || "CHECKINNEW".equals(file_action)|| "ADDCOMMENT".equals(file_action) || "CONNECTREPORT".equals(file_action))
      else if ("CHECKINNEW".equals(file_action) || "CHECKINSEL".equals(file_action))
      {
         return "CHECKIN";
      }
      else if ("PRINT".equals(file_action))
      {
         return "PRINT";
      }
       else if ("GETCOPYTODIR".equals(file_action))
      {
         return "";
      }
      //else if ("SENDMAIL".equals(file_action) || "VIEWWITHEXTVIEWER".equals(file_action) ||  "VIEWCOMMENT".equals(file_action) 
      //      || "SENDCOMMENT".equals(file_action)) // Bug 57779
      else if ("VIEWWITHEXTVIEWER".equals(file_action) || "VIEWCOMMENT".equals(file_action) || "TRANSMITTALDOWNLOAD".equals(file_action)) //Bug Id 65833 //Bug Id 68528 //Bug Id 81807
      {
         return "VIEW";
      }
      else if ("SELECT_AND_EXECUTE_MACRO".equals(file_action))
      {
         return doc_hdlr.getDocumentProperty("PROCESS");
      }
      // Added by Terry 20121018
      else if ("DOWNLOAD".equals(file_action))
      {
         return "DOWNLOAD";
      }
      // Added end
      else
      {
         for (int x = 0; x < macro_processes.length; x++)
         {
            if (macro_processes[x].equals(file_action))
            {
               return file_action;
            }
         }
         return null;
      }
   }


   private void showApplicationChooser(String doc_type)
   {
      ASPManager mgr = getASPManager();
      String query = null;

      if ("ORIGINAL".equals(doc_type) || "VIEW".equals(doc_type))
         query = "SELECT FILE_TYPE, DESCRIPTION FROM EDM_APPLICATION WHERE DOCUMENT_TYPE IN ('ORIGINAL','VIEW') ORDER BY upper(description)";
      else if ("REDLINE".equals(doc_type))
         query = "SELECT FILE_TYPE, DESCRIPTION FROM EDM_APPLICATION WHERE DOCUMENT_TYPE IN ('REDLINE') ORDER BY upper(description)";

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addQuery("GETAPPLICS", query);
      trans = mgr.perform(trans);
      data_buf = trans.getBuffer("GETAPPLICS");
      client_action = "SHOW_APPLICATION_CHOOSER";
   }

   // Added by Terry 20120918
   // Show edm file chooser
   private void showEdmFileChooser()
   {
      client_action = "SHOW_EDM_FILE_CHOOSER";
   }
   // Added end


   private void showMacroChooser(ASPBuffer buf)
   {
      data_buf = buf;
      client_action = "SHOW_MACRO_CHOOSER";
   }


   private void showDirectoryChooser()
   {
      client_action = "SELECT_DIRECTORY";
   }


   private void showFileChooser(String doc_type)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      ASPCommand cmd;

      // Get file types and extensions...
      if ("ORIGINAL".equals(doc_type))
      {
         cmd = trans.addCustomCommand("FILE_TYPES_EXT", "Edm_Application_API.Get_Original_File_Type_List");
         cmd.addParameter("OUT_1");
      }
      // Bug 89622, Start (If Transmittal comment fetch all registered types)
      else if ("REDLINE".equals(doc_type))
      {
         cmd = trans.addCustomCommand("FILE_TYPES_EXT", "Edm_Application_API.Get_File_Type_List");
         cmd.addParameter("OUT_1");         
      }      
      // Bug 89622, Finish
      else
      {
         cmd = trans.addCustomCommand("FILE_TYPES_EXT", "Edm_Application_API.Get_File_Type_List");
         cmd.addParameter("OUT_1");
         cmd.addParameter("IN_STR1", doc_type);
      }
      
      trans = mgr.perform(trans);
      file_type_list = trans.getValue("FILE_TYPES_EXT/DATA/OUT_1");


      client_action = "SHOW_FILE_CHOOSER";
   }


   private void showConfirmationBox(String confirm_msg)
   {
      this.confirm_msg = confirm_msg;
      client_action = "SHOW_CONFIRMATION";
   }
   
   // Added by Terry 20120918
   private void showInformationBox(String infor_msg)
   {
      this.information_msg = infor_msg;
      client_action = "SHOW_INFORMATION";
   }
   
   private void showAlertBox(String alert_msg)
   {
      this.alert_msg = alert_msg;
      client_action = "SHOW_ALERTBOX";
   }
   // Added end


   private void checkExternalViewerApplication()
   {
      client_action = "CHECK_EXTERNAL_VIEWER";
   }


   private void abortCurrentDocument()
   {
      client_action = null;
   }


   private void fetchLocalCheckOutPath()
   {
      client_action = "GET_LOCAL_CHECK_OUT_PATH";
   }

   // Added by Terry 20121018
   // fetch local download path operation
   private void fetchLocalDownloadPath()
   {
      client_action = "GET_LOCAL_DOWNLOAD_PATH";
   }
   // Added end

   private void showFileNameSelection()
   {
      client_action = "GET_NAME_FILES_USING";
   }


   /**
    * prepareFileAction() prepares a file action by getting all necessary
    * information so that the action can be executed sucessfully. Different
    * actions need different kinds of information before it can be executed.
    * Some of this information is obtained from the database server while
    * others need to be obtained from the user/client.
    *
    * Examples:
    * 1. When creating a new document (action CREATENEW), the action
    * needs to know what type of application, or more specifically, the file
    * type the new document should be.
    *
    * 2. Some actions like CHECKOUT and CREATENEW need to know the local
    * checkout path before executing, so that these paths can be stored on
    * the database server.
    *
    * 3. Actions like DELETE and DELETEALL needs to obtain a confirmation
    * from the user before executing. Depending on the users response to the
    * confirmation, the appropriate action will be taken.
    *
    * 4. Many actions need to know what macro (when available) to execute.
    *
    * This method is called each time this page is executed. At each run, the
    * method checks to see if all necessary information is available. If all
    * information is available the method returns 'true' indicating that the
    * action has been sucessfully prepared. Otherwise it returns 'false'
    *
    * NOTE: This method replaces a former method in this page called okFind()
    */
   private boolean prepareFileAction() throws FndException
   {
      debug("prepareFileAction() {"); 

      ASPManager mgr = getASPManager();
      ASPContext ctx = mgr.getASPContext();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      //Bug Id 80882, Start
      if (("DOC_STRUCTURE".equals(doc_hdlr.getActionProperty("SOURCE"))) && ("PRINT_VIEW".equals(doc_hdlr.getDocumentProperty("FILE_ACTION"))))
      {
         doc_hdlr.setDocumentProperty("FILE_ACTION", "VIEW");
         bPrintView = true;
      }
      //Bug Id 80882, End
      String file_action = getFileAction();
      String doc_type = doc_hdlr.getDocType();
      String doc_class = doc_hdlr.getDocClass();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_no = doc_hdlr.getDocNo();
      String doc_rev = doc_hdlr.getDocRev();
      String macro = null;
      String edmInfo = null;
      //Bug Id 56572 Start.
      String doc_keys = null;
      String doc_title =  null;
      //Bug Id 56572 End.

      file_no = doc_hdlr.getDocumentProperty("FILE_NO");
      
      // Added by Terry 20120917
      // When check in document, file no set to -1 to invoke checkAccess in DocSrv.
      /*if (mgr.isEmpty(file_no) && ("CHECKIN".equals(file_action)))
      {
         file_no = "-1";
      }*/
      
      //Bug Id: 68455 start
      String accessRightsCheck = doc_hdlr.getDocumentProperty("ACCESS_RIGHTS_CHECK");
      //Bug Id: 68455 end

      //
      // If Doc_Type was not passed set it to the default value..
      //

      if (mgr.isEmpty(doc_hdlr.getDocType()))
      {
         doc_type = DEFAULT_DOC_TYPE;
         doc_hdlr.setActionProperty("DOC_TYPE", doc_type);
      }

      //
      // Check if we need to get the Latest Revision
      // or the Latest Released Revision..
      //

      if (Str.isEmpty(doc_rev))
      {
         String released = doc_hdlr.getDocumentProperty("RELEASED");
         String latest_rev = doc_hdlr.getDocumentProperty("LATEST_REV");

         if ((released != null) || (latest_rev != null))
         {
            // convert the boolean values if the user has entered them in lower case.
            if (released != null)
               released = released.toUpperCase();
            if (latest_rev != null)
               latest_rev = latest_rev.toUpperCase();

            doc_rev = getRevision(doc_class, doc_no, doc_sheet, latest_rev, released);
            doc_hdlr.setDocumentProperty("DOC_REV", doc_rev);
         }
      }


      //
      // Fetch the file type if it was not passed..
      //
      if (mgr.isEmpty(doc_hdlr.getFileType())
            && !"CREATENEW".equals(file_action)
            && !"FILEIMPORT".equals(file_action)
            && !"CREATENEWDOC".equals(file_action)
            && !"LAUNCH_LOCAL_FILE".equals(file_action)
            && !"OPEN_LOCAL_FOLDER".equals(file_action)
            && !"LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(file_action)
            && !"SELECT_FILE_TYPE".equals(file_action)
            && !"SHOW_ERROR_LOG".equals(file_action))
      {
         String file_type = getEdmFileType(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         doc_hdlr.setFileType(file_type);
      }

      if ("FILEIMPORT".equals(file_action))
      {
         if (!mgr.isEmpty(doc_no))
         {
            if (checkExistTitle(doc_no, doc_class))
               throw new FndException(mgr.translate("DOCMAWEDMMACRODUPLICATEDOCNO: A title with the document number &1 alread exists", doc_no));
         }
      }


      //
      // Check if the action on the current
      // document revision is legal..
      //
      //Bug Id: 68455 start
      if ("FILEIMPORT".equals(file_action) || "LAUNCH_LOCAL_FILE".equals(file_action) || "OPEN_LOCAL_FOLDER".equals(file_action) ||
          "LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(file_action) || "SHOW_ERROR_LOG".equals(file_action)
          || "SENDFOLDER_BYMAIL".equals(file_action)|| "ZIP_FOLDER".equals(file_action) || "NO".equals(accessRightsCheck)|| "OPEN_FOLDER".equals(file_action)) //Bug Id 72654 added condtion for OPEN_FOLDER file action
         //Bug Id: 68455 end
      {
         // Avoid performing a security check for these processes
         // because there's no document revision to perform a check
         // on, yet.
      }
      else
      {
         if ("ADDCOMMENT".equals(file_action))
            notCheckOriginalRefExist = true;
         // Bug Id 81807, start
         else if ("TRANSMITTALDOWNLOAD".equals(file_action))
         {
            if ("ALL".equals(doc_type))
               doc_type = "ORIGINAL";
            else if ("VIEW".equals(doc_type))
            {
               edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
               boolean view_exists = "TRUE".equals(getStringAttribute(edmInfo, "VIEW_REF_EXIST"));
               if (!view_exists)
                  doc_type = "ORIGINAL";
            }
         }
         // Bug Id 81807, end

         checkAccess(doc_class, doc_no, doc_sheet, doc_rev, doc_type, getActionMacroProcess(file_action));
      }


      //
      // Application selection
      //

      if ("CREATENEW".equals(file_action))
      {
         if (mgr.isEmpty(doc_hdlr.getFileType()))
         {
            debug("Master file type not selected...");

            edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
            if ("ORIGINAL".equals(doc_type) && "TRUE".equals(getStringAttribute(edmInfo, "FILE_TEMPLATE_EXIST")))
            {
               debug("Template exists...");
               String file_type = getStringAttribute(edmInfo, "TEMPLATE_FILE_TYPE");
               doc_hdlr.setFileType(file_type);
            }
            else if (!mgr.isEmpty(DocmawUtil.getFileName(doc_hdlr.getDocumentProperty("FILE_NAME"))))
            {
               // If a file name was specified, determine the
               // file type using the file's extension

               String file_type = getFileType(DocmawUtil.getFileExtention(doc_hdlr.getDocumentProperty("FILE_NAME")));

               if (!mgr.isEmpty(file_type))
                  doc_hdlr.setFileType(file_type);
            }

            // If the file type still couldn't be determined..
            if (mgr.isEmpty(doc_hdlr.getFileType()))
            {
               debug("Showing application chooser.....................");
               showApplicationChooser(doc_type);	
               return false;
            }
         }
      }

      // Bug 89622, Start - ADDCOMMENT will be handled the same way as a normal check-in.
      if ("CHECKIN".equals(file_action) || "ADDCOMMENT".equals(file_action))
      // Bug 89622, Finish
      {
         // Modified by Terry 20121224
         // Disable original ref exist check in CHECKIN operation
         // Original:
         // edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);

         // if (!("TRUE".equals(getStringAttribute(edmInfo, "ORIGINAL_REF_EXIST"))))
         // {
            // If a local file name was not passed..
            if (mgr.isEmpty(doc_hdlr.getDocumentProperty("FILE_NAME")))
            {
               debug("No original file reference....");
               if(!mgr.isEmpty(getPageProperty("SELECTED_FILE_NAME")))
               {
                  //Get the selected file name and it's extension
                  String selected_file_name = getPageProperty("SELECTED_FILE_NAME");
                  String file_ext = selected_file_name.substring(selected_file_name.lastIndexOf(".") + 1);

                  trans.clear();
                  //Get the file type from the extension
                  trans = mgr.newASPTransactionBuffer();
                  ASPCommand cmd = trans.addCustomFunction("GETFTYPE","EDM_APPLICATION_API.Get_File_Type","FILE_TYPE");
                  cmd.addParameter("IN_STR1", file_ext.toUpperCase());
                  trans = mgr.perform(trans);

                  String file_type = trans.getValue("GETFTYPE/DATA/FILE_TYPE");
                  doc_hdlr.setDocumentProperty("FILE_NAME", selected_file_name);
                  doc_hdlr.setFileType(file_type);
               }
               else
               {
                  debug("Showing file chooser...");
                  // Pop up the file selecion dialog. 
                  // File has to be selected to get the macro for the filetype
                  //showFileChooser(doc_type);
                  showFileChooser(doc_type);
                  return false;
               }
            // }
         }
      }

      // Bug 89622, ADDCOMMENT will be handled the same way as a normal check-in. Removed code ADDCOMMENT related logic.      
      if ("SELECT_FILE_TYPE".equals(file_action))
      {
         ASPBuffer buf;
         buf = doc_hdlr.getCurrentDocument();
         String fileType = buf.getValue("FILE_TYPE");
         if (mgr.isEmpty(fileType))
         {
            showApplicationChooser("ORIGINAL");
         }
         else
         {
            // Application chooser has been shown and a file type selected.
            int docBufNumber = Integer.parseInt(buf.getValue("DOCUMENT_BUF_NO"));
            doc_hdlr.setDocumentPropertyAt(docBufNumber, "FILE_TYPE", fileType);
            doc_hdlr.nextDocument();
         }
      }


      //
      // Confirmations
      //
      boolean confirm = confirmAction();
      if (confirm && ("DELETE".equals(file_action) || "DELETEALL".equals(file_action)))
      {
         //Bug Id 56572 Start, modify the confirmation message.
         doc_keys = "(" + doc_class + " - " + doc_no + " - " + doc_sheet + " - " + doc_rev+ " ) " ;
         edmInfo = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         doc_title = getStringAttribute(edmInfo, "DOC_TITLE");
         
         // Added by Terry 20140729
         // Replace quotes of doc_title
         if (!mgr.isEmpty(doc_title))
            doc_title = doc_title.replace("\"", "\\\"").replace("\'", "\\\'");
         // Added end
         
         if (!actionConfirmed())
         {
            if ("DELETE".equals(file_action))
            {
               //Bug Id 78799, Start
               if ("REDLINE".equals(doc_type))
                  showConfirmationBox(mgr.translate("DOCMAWEDMMACRODELREDCONFIRM: Do you want to remove the redline file?"));
               else
                  showConfirmationBox(mgr.translate("DOCMAWEDMMACRODELCONFIRM: WARNING - You are about to delete the document files for the document &1 '&2' . This action cannot be reversed. Are you sure you want to continue?" , doc_keys, doc_title));
               //Bug Id 78799, End
            }
            else if ("DELETEALL".equals(file_action))
               showConfirmationBox(mgr.translate("DOCMAWEDMMACRODELALLCONFIRM: WARNING! Use this method with caution. This action will delete the document &1 '&2' , the physical file, all connections  and all its references! All information will be lost! ", doc_keys, doc_title));
            // Added by Terry 20120918
            // Delete selected edm files
            // else if ("DELETESEL".equals(file_action))
            //    showConfirmationBox(mgr.translate("DOCMAWEDMMACRODELCONFIRM: WARNING - You are about to delete the document files for the document &1 '&2' . This action cannot be reversed. Are you sure you want to continue?" , doc_keys, doc_title));
            // Added end
            return false;
         }
         else if ("CANCEL".equals(getPageProperty("CONFIRM")))
         {
            // The action has been confirmed, but the user chose 'Cancel'.
            // Abort executing the current document and go to the next.
            abortCurrentDocument();
            return false;
         }
         //Bug Id 56572 end.
      }
      else if (confirm && "UNDOCHECKOUT".equals(file_action))
      {
         if (!actionConfirmed())
         {
            showConfirmationBox(mgr.translate("DOCMAWEDMMACROUNDOCHECKOUTCONFIRM: The document record will be unreserved and the local file removed. Continue?"));
            return false;
         }
         else if ("CANCEL".equals(getPageProperty("CONFIRM")))
         {
            // The action has been confirmed, but the user chose 'Cancel'.
            // Abort executing the current document and go to the next.
            abortCurrentDocument();
            return false;
         }
      }


      //
      // Local check out path
      //

      if ("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action) || "SENDMAIL".equals(file_action) || "VIEWCOMMENT".equals(file_action)
          || "VIEW".equals(file_action) || "PRINT".equals(file_action) || "GETCOPYTODIR".equals(file_action)|| "VIEWCOMMENT".equals(file_action))
      {
         if (mgr.isEmpty(getLocalCheckOutPath()))
         {
            debug("Getting local checkout path...");
            fetchLocalCheckOutPath();
            return false;
         }
      }

      // Added by Terry 20121018
      // 
      // Local download path
      // 
      
      if ("DOWNLOAD".equals(file_action))
      {
         String first_doc_download = ctx.findGlobal("FIRST_DOC_DOWNLOAD", "TRUE");
         if (mgr.isEmpty(getLocalDownloadPath()) || "TRUE".equals(first_doc_download))
         {
            debug("Getting local download path...");
            fetchLocalDownloadPath();
            ctx.setGlobal("FIRST_DOC_DOWNLOAD", "FALSE");
            return false;
         }
      }
      // Added end

      //
      //  f the user has decided to cancel the file operation
      //

      if ("CHECKOUT".equals(file_action) || "VIEW".equals(file_action) || "PRINT".equals(file_action) || "GETCOPYTODIR".equals(file_action)|| "VIEWCOMMENT".equals(file_action))
      {
         debug("debug: CONFIRM = " + getPageProperty("CONFIRM"));

         if ("CANCEL".equals(getPageProperty("CONFIRM")))
         {
            // If the user has decided to cancel the file operation because there is already
            // a file at the target location about the checkout operation.

            //if (doc_hdlr.isLastDocument()) 
            //   abortCurrentDocument();

            file_writable_error = true;
            return false;
         }
      }


      //
      // Directory selection for downloading files to a
      // different directory
      //

      if ("GETCOPYTODIR".equals(file_action))
      {
         trans = mgr.newASPTransactionBuffer();

         // Modified by Terry 20120926
         // Original:
         // ASPCommand cmd = trans.addCustomCommand("CHECKFILEEXIST", "Edm_File_Api.Check_Exist");
         // cmd.addParameter("OUT_1");
         // Use new function to check exist.
         ASPCommand cmd = trans.addCustomFunction("CHECKFILEEXIST", "Edm_File_API.Find_Edm_File", "OUT_1");
         // Modified end
         cmd.addParameter("IN_1", doc_class);
         cmd.addParameter("IN_1", doc_no);
         cmd.addParameter("IN_1", doc_sheet);
         cmd.addParameter("IN_1", doc_rev);
         cmd.addParameter("IN_1", doc_type);

         trans = mgr.perform(trans);

         String exist;
         exist = trans.getValue("CHECKFILEEXIST/DATA/OUT_1");

         if("FALSE".equals(exist))
         {
            return false;
         }

         if ((mgr.isEmpty(doc_hdlr.getActionProperty("SELECTED_DIRECTORY"))) && ("YES".equals(doc_hdlr.getActionProperty("SAME_ACTION_TO_ALL"))))
         {
            showDirectoryChooser();
            return false;
         }
      }


      //
      // Check if an external viewer application has been set
      //

      if ("VIEWWITHEXTVIEWER".equals(file_action))
      {
         if (mgr.isEmpty(getPageProperty("EXTERNAL_VIEWER")))
         {
            checkExternalViewerApplication();
            return false;
         }
      }


      //
      // Macro and file name selection
      //

      // Check if macros have been explicitly disabled for the current action
      if (isMacroDisabled())
      {
         macro = EdmMacroProcess.getEmptyMacro();
         setMacroSelection(macro);
      }
      else if (!macroSelected())
      {
         debug("Macro not selected");

         // Select the macros for this file action. Not all file actions can
         // have macros. Only those that have a corresponding MacroProcess.

         if (!hasActionMacroProcess(file_action))
         {
            debug("Action does not have a MacroProcess...");
            // The current action does not have a
            // MacroProcess.. so no macros to execute!
            macro = EdmMacroProcess.getEmptyMacro();
            setMacroSelection(macro);
         }
         else
         {
            String process = getActionMacroProcess(file_action);
            EdmMacroProcess macro_proc = new EdmMacroProcess(mgr, doc_class, process, doc_hdlr.getFileType());


            ASPBuffer macro_buf = macro_proc.getMacros();

            //
            // If there are macros available, select the
            // macro to execute depending on the macro option
            //

            if (macro_buf != null)
            {
               if (macro_proc.selectAndRunMacro())
               {
                  debug("select and run macro...");
                  showMacroChooser(macro_buf);
                  return false;
               }
               else if (macro_proc.runNoMacro())
               {
                  debug("no macros...");
                  macro = EdmMacroProcess.getEmptyMacro();
               }
               else if (macro_proc.runIfOnlyOneMacro())
               {
                  debug("only one macro available... running...");
                  macro = macro_proc.getFirstMacro();
                  setPageProperty("MACRO_SELECTED", macro_proc.getFirstMacroDescription());
               }
            }
            else
            {
               macro = EdmMacroProcess.getEmptyMacro();
            }
            setMacroSelection(macro);
         }
      }


      //
      // File name selection
      //

      if (isFileNameConfigurable() && !fileNameSelected())
      {
         debug("file name configurable...");
         showFileNameSelection();
         return false;
      }
      
      // Added by Terry 20120918
      // if Delete selected edm files, show edm file chooser.
      // show CHECKOUT document chooser
      boolean only_one_file = false;
      if ("DELETESEL".equals(file_action)    || 
          "CHECKOUT".equals(file_action)     || 
          "CHECKINSEL".equals(file_action)   || 
          "VIEW".equals(file_action)         || 
          "UNDOCHECKOUT".equals(file_action) ||
          "PRINT".equals(file_action)        ||
          "GETCOPYTODIR".equals(file_action) ||
          "SENDMAIL".equals(file_action))
      {
         if ("1".equals(edm_file_count))
         {
            getEdmFilesToCtx(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         }
         // Added by Terry 20140211
         // Operate transfered file
         else if (!Str.isEmpty(file_no))
         {
            getEdmFilesToCtx(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
         }
         // Added end
         else
         {
            String show_alert_msg = "";
            if ("DELETESEL".equals(file_action)    || 
                "CHECKINSEL".equals(file_action)   || 
                "UNDOCHECKOUT".equals(file_action) || 
                "PRINT".equals(file_action)        ||
                "GETCOPYTODIR".equals(file_action) ||
                "SENDMAIL".equals(file_action))
            {
               only_one_file = false;
               show_alert_msg = "DOCMAWEDMMACRONOEDMFILESEL: Please select any edm files.";
            }
            else if ("CHECKOUT".equals(file_action) || "VIEW".equals(file_action))
            {
               only_one_file = true;
               show_alert_msg = "DOCMAWEDMMACROEDMFILESELONE: Sorry, you must select one file and you can only select one file.";
            }
            
            if (only_one_file)
               edmtbl.enableOneRowSelect();
            
            if (mgr.commandBarActivated())
            {
               String command = mgr.readValue("__COMMAND");
               if ("EDMFILE.Forward".equals(command)  ||
                   "EDMFILE.Last".equals(command)     ||
                   "EDMFILE.Backward".equals(command) ||
                   "EDMFILE.First".equals(command))
               {
                  if (only_one_file)
                     clearSelectionToCtx();
                  else
                     storeSelectionToCtx();
               }
               eval(mgr.commandBarFunction());
            }
            else if (mgr.buttonPressed("OK"))
            {
               storeSelectionToCtx();
               ASPBuffer edm_files = (ASPBuffer)ctx.findGlobalObject(dlgName);
               if (!only_one_file && edm_files != null && edm_files.countItems() > 0)
                  return true;
               else if (only_one_file && edm_files != null && edm_files.countItems() == 1)
                  return true;
               else
               {
                  showAlertBox(mgr.translate(show_alert_msg));
                  return false;
               }
            }
            else if (ctx.readFlag("DOC_OPERATION_PROCESSING", false))
            {
               return true;
            }
            else
               okFindEdm();
            adjustEdm();
            showEdmFileChooser();
            return false;
         }
      }
      // Added end

      return true;
   }

   public void zipFilesInTheFolder()// I tried to make this as much generic as possible, no sure this can belong to EdmMacro.:bakalk
   {
      //client
      mail_folder         = doc_hdlr.getDocumentProperty("MAIL_FOLDER");
      mail_client_message = doc_hdlr.getDocumentProperty("MAIL_CLIENT_MESSAGE");
      mail_zip_file_name  = doc_hdlr.getDocumentProperty("MAIL_ZIP_FILE_NAME");
      

      client_action       = "ZIP_FILES_IN_FOLDER";
   }
 
   //Bug Id 72460, start
   private void undoStateChange(String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String doc_class = doc_hdlr.getDocClass();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_no = doc_hdlr.getDocNo();
      String doc_rev = doc_hdlr.getDocRev();
      

      undoStateChange(doc_class, doc_no, doc_sheet, doc_rev, doc_type, "CheckIn");

   }

   private void removeLastHistoryLine(String status) throws FndException
   {
       ASPManager mgr = getASPManager ();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       String doc_class = doc_hdlr.getDocClass();
       String doc_sheet = doc_hdlr.getDocSheet();
       String doc_no = doc_hdlr.getDocNo();
       String doc_rev = doc_hdlr.getDocRev();

       removeLastHistoryLine(doc_class, doc_no, doc_sheet, doc_rev, status);
   }

   private void removeFileReference(String doc_type, String file_no) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String doc_class = doc_hdlr.getDocClass();
      String doc_sheet = doc_hdlr.getDocSheet();
      String doc_no = doc_hdlr.getDocNo();
      String doc_rev = doc_hdlr.getDocRev();

      removeFileReference(doc_class, doc_no, doc_sheet, doc_rev, doc_type, file_no);
   }

   private void prepareClientActionWithError()
   {
      preparePageHeader();
      
      resubmit = false;
      
      debug("DEBUG: prepareClientActionWithError() ");
      
      if (!doc_hdlr.isLastDocument())
      {
         if (logError())
         {
            logOperationModifyStatus(getOppFailMessage());
            
            if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(doc_hdlr.getActionProperty("FILE_ACTION")) )
            {
               if (doc_hdlr.countDocuments() > doc_hdlr.getCurrentDocumentCount()+ 3)
                  resubmit = true;
            }
            else
            {
               if (doc_hdlr.countDocuments() > doc_hdlr.getCurrentDocumentCount()+ 2)
                  resubmit = true;
            }
         }
      }
   }

   private String getOppFailMessage()
   {
      ASPManager mgr = getASPManager();

      String file_action = getFileAction();
      String message = "";
      
      if ("CHECKIN".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDCHECKIN: Did not check in sucessfully");
      //Bug Id 80882, Start
      if ("CHECKOUT".equals(file_action) ||"CREATENEW".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDCHECKEDOUT: Did not check out sucessfully");
      if ("VIEW".equals(file_action))
      {  
         if (bPrintView) {
            message = mgr.translate("DOCMAWEDMMACROFAILEDPRINTVIEW: Did not Check out Sucessfully for Print.");
         }
         else
         {
            message = mgr.translate("DOCMAWEDMMACROFAILEDVIEW: Did not Check out Sucessfully for View.");
         }
      }
      if ("PRINT".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDPRINT: Did not Print Sucessfully.");
      if ("GETCOPYTODIR".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDCOPY: Did not Copy Sucessfully.");
      if ("GET_DOCUMENTS_FOR_EMAIL".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDMAIL: Did not Check out Successfully for Send Mail.");
      //Bug Id 80882, End
      
      // Added by Terry 20121018
      if ("DOWNLOAD".equals(file_action))
         message = mgr.translate("DOCMAWEDMMACROFAILEDDOWNLOAD: Did not Download sucessfully.");
      // Added end
      
      return message;
   }

   private void logOperationModifyStatus(String Status)
   {
      ASPManager mgr = getASPManager();
      
      error_log.traceBuffer("DEBUG: Tracing Error Log...");
      if (error_log.countItems() > 0)
      {
         debug("DEBUG: logOperationModifyStatus().. doc_hdlr.getActionProperty(FILE_ACTION) " + doc_hdlr.getActionProperty("FILE_ACTION"));
         debug("DEBUG: logOperationModifyStatus().. doc_hdlr.getDocumentProperty(FILE_ACTION) " + doc_hdlr.getDocumentProperty("FILE_ACTION"));
         ASPBuffer buf = mgr.newASPBuffer();
         
         if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(doc_hdlr.getActionProperty("FILE_ACTION")) || "GETCOPYTODIR".equals(doc_hdlr.getDocumentProperty("FILE_ACTION")) || "OPEN_FOLDER".equals(doc_hdlr.getActionProperty("POST_CHECKOUT_ACTION"))
               || "DO_NOTHING".equals(doc_hdlr.getActionProperty("POST_CHECKOUT_ACTION"))) 
         {
            error_log.getBufferAt(doc_hdlr.getCurrentDocumentCount()).traceBuffer("DEBUG: Error Log");
            error_log.getBufferAt(doc_hdlr.getCurrentDocumentCount()).setValue("STATUS", Status);
         }
         else
         {
            error_log.getBufferAt(doc_hdlr.getCurrentDocumentCount()-1).traceBuffer("DEBUG: Error Log");
            error_log.getBufferAt(doc_hdlr.getCurrentDocumentCount()-1).setValue("STATUS", Status);
         }
         
      }
      error_log.traceBuffer("DEBUG: Delete Error Log...");
   }

   private void deleteTransmittalDoc() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String doc_class = doc_hdlr.getDocumentProperty("DOC_CLASS");
      String doc_sheet = doc_hdlr.getDocumentProperty("DOC_SHEET");
      String doc_no = doc_hdlr.getDocumentProperty("DOC_NO");
      String doc_rev = doc_hdlr.getDocumentProperty("DOC_REV");
      String transmittal_id = doc_hdlr.getDocumentProperty("TRANSMITTAL_ID");

      deleteTransmittalDoc(doc_class, doc_no, doc_sheet, doc_rev, transmittal_id);
   }

   private void rollbackTransmittalStatus(String to_state) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      String transmittal_id = doc_hdlr.getDocumentProperty("TRANSMITTAL_ID");

      rollbackTransmittalStatus(transmittal_id, to_state);
   }
   //Bug Id 72460, end

   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      fmt = mgr.newASPHTMLFormatter();

      disableOptions();
      disableHomeIcon();
      disableNavigate();


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
              setHidden().
              setLabel("DOCMAWEDMMACROAPPLICATION: Application");

      fileblk.addField("MACRO").
              setSelectBox().
              setValidateFunction("setMacroSelection").
              setHidden().
              setLabel("DOCMAWEDMMACROMACRO: Macro");

      fileblk.addField("MACRO_SELECTED").
              setSize(60).
              setReadOnly().
              setHidden().
              setLabel("DOCMAWEDMMACROMACRO: Macro");

//      fileblk.defineCommand("EDM_FILE_TEMP_API", "New__");
      fileset = fileblk.getASPRowSet();
      filebar = mgr.newASPCommandBar(fileblk);

      filelay = fileblk.getASPBlockLayout();
      filelay.setDataSpan("MACRO", 6);
      filelay.setDataSpan("APP_FILE_TYPE", 6);
      filelay.setDataSpan("MACRO_SELECTED", 6);
      filelay.setDefaultLayoutMode(filelay.CUSTOM_LAYOUT);        

      // report 
      ASPBlock reportblk = mgr.newASPBlock("REPORT");
 
      reportblk.addField("REPORT_ID");
      reportblk.addField("CLIENT_VALUES0");
      reportblk.addField("CLIENT_VALUES1");
      reportblk.addField("ENUMUSERWHERE");
      reportblk.addField("PROPERTIES_LIST_OUT");
      reportblk.addField("RESULT_KEY");
      
      //
      // Added by Terry 20120918
      // Edm file block
      //
      
      edmblk = mgr.newASPBlock("EDMFILE");

      edmblk.addField("EDM_DOC_CLASS").
      setDbName("DOC_CLASS").
      setLabel("DOCSELECTEDMFILESDLGDOCCLASS: Doc Class").
      setSize(10).
      setHidden();

      edmblk.addField("EDM_DOC_NO").
      setDbName("DOC_NO").
      setLabel("DOCSELECTEDMFILESDLGDOCNO: Doc No").
      setSize(20).
      setHidden();
      
      edmblk.addField("EDM_DOC_SHEET").
      setDbName("DOC_SHEET").
      setLabel("DOCSELECTEDMFILESDLGDOCSHEET: Doc Sheet").
      setSize(10).
      setHidden();

      edmblk.addField("EDM_DOC_REV").
      setDbName("DOC_REV").
      setLabel("DOCSELECTEDMFILESDLGDOCREV: Doc Rev").
      setSize(10).
      setHidden();

      edmblk.addField("EDM_DOC_TYPE").
      setDbName("DOC_TYPE").
      setLabel("DOCSELECTEDMFILESDLGDOCTYPE: Doc Type").
      setSize(10).
      setHidden();

      edmblk.addField("FILE_NO").
      setLabel("DOCSELECTEDMFILESDLGFILENO: File No").
      setHidden().
      setSize(10);
      
      // Modified by Terry 20140211
      // Add view link
      edmblk.addField("USER_FILE_NAME").
      setLabel("DOCSELECTEDMFILESDLGUSERFILENAME: User File Name").
      setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "EDM_DOC_CLASS DOC_CLASS,EDM_DOC_NO DOC_NO,EDM_DOC_SHEET DOC_SHEET,EDM_DOC_REV DOC_REV,FILE_NO", "NEWWIN").
      setSize(30);
      // Modified end
      
      edmblk.addField("STATE").
      setLabel("DOCSELECTEDMFILESDLGSTATE: State").
      setSize(10);
      
      edmblk.setView("EDM_FILE");
      edmset = edmblk.getASPRowSet();
      edmtbl = mgr.newASPTable(edmblk);
      edmtbl.setTitle("EDMMACROEDMFILETBL: Select Edm Files");
      edmtbl.disableQuickEdit();
      edmtbl.enableRowSelect();
      edmtbl.setWrap();

      edmbar = mgr.newASPCommandBar(edmblk);
      edmbar.defineCommand(edmbar.OKFIND, "okFindEdm");
      edmbar.defineCommand(edmbar.COUNTFIND, "countFindEdm");
      edmbar.disableMultirowAction();
      edmlay = edmblk.getASPBlockLayout();
      edmlay.setDefaultLayoutMode(edmlay.MULTIROW_LAYOUT);
      
      //
      // Added end
      //
      
      inf = mgr.newASPInfoServices();
      inf.addFields(); 
      
      super.preDefine();
   }



   private String getFileActionTitle()
   {
      ASPManager mgr = getASPManager();

      String title;

      // Check if any special titles have been passed
      // for the current action..
      title = getPageTitle();

      if (mgr.isEmpty(title))
      {
         // No special titles, use one of the standard titles..
         String file_action = getFileAction();
         if ("CHECKOUT".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCHECKOUTDOC: Check Out Document");
         else if ("CHECKIN".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCHECKINDOC: Check In Document");
         else if ("VIEW".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROVIEWDOC: View Document");
         else if ("SENDMAIL".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROSENDMAIL: Send Mail Document");
         else if ("PRINT".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROPRINTDOC: Print Document");
         else if ("UNDOCHECKOUT".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROUNDOCHKOUTDOC: Undo Checkout Document");
         else if ("DELETE".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACRODELETEDOC: Delete Document Files");
         else if ("DELETEALL".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACRODELETEALL: Delete Document Issue and Files");
         // Added by Terry 20120918
         // Delete selected edm files
         else if ("DELETESEL".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACRODELETESEL: Delete Selected Document Files");
         else if ("CHECKINSEL".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCHECKINSEL: Check In Selected Document Files");
         // Added end
         // Added by Terry 20121018
         // Download files
         else if ("DOWNLOAD".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACRODOWNLOADDOC: Download Documents");
         // Added end
         else if ("CHECKINNEW".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCHECKINNEWDOC: Check In New Document");
         else if ("CREATENEW".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCREATENEWDOC: Create New Document");
         else if ("VIEWWITHEXTVIEWER".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROVIEWDOCWITHEXTVIEW: View Document with External Viewer");
         else if ("GETCOPYTODIR".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROGETCOPYTODIR: Copy File To...");
         else if ("FILEIMPORT".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROCOFILEIMPORT: File Import");
         else if ("DELETE_LOCAL_FILE".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACRODELETELOCALFILE: Delete Local File");
         else if ("LAUNCH_LOCAL_FILE".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROLAUNCHLOCALFILE: Launch File");
         else if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROLAUNCHLOCALFILEWITHEXTERNALVIEWER: Launch File With External Viewer");
         else if ("OPEN_LOCAL_FOLDER".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROOPENLOCALFOLDER: Open Folder");
         else if ("SHOW_ERROR_LOG".equals(file_action))
            title = mgr.translate("DOCMAWEDMMACROSHOWERRORLOG: Generating Report..");         
         else
            title = mgr.translate("DOCMAWEDMMACRONOPROCESS: Document");
      }

      return title;
   }


   /**
    * Returns a string representing the current document
    * count out of the total number of documents
    */
   private String drawProgressIndicator()
   {
      ASPManager mgr = getASPManager();

      StringBuffer progress = new StringBuffer();
      progress.append("&nbsp;&nbsp;&nbsp;&nbsp");
      progress.append("[ ");
      progress.append(doc_hdlr.getCurrentDocumentCount() + 1);
      progress.append(" ");
      progress.append(mgr.translate("DOCMAWEDMMACROOF: of"));
      progress.append(" ");
      progress.append(doc_hdlr.countDocuments());
      progress.append(" ]");
      return progress.toString();
   }


   public String drawRadio(String label, String name, String value, boolean checked, String tag)
   {
      return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + (checked ? " CHECKED " : "") + " " + (tag == null ? "" : tag) + ">&nbsp;<font class=normalTextValue>" + label + "</font>";
   }


   /**
    * Sets the page heading depending on
    * the type of action
    */
   private void preparePageHeader()
   {
      ASPManager mgr = getASPManager();

      fileset.setValue("DOC_CLASS", doc_hdlr.getDocClass());
      fileset.setValue("DOC_NO", doc_hdlr.getDocNo());
      fileset.setValue("DOC_SHEET", doc_hdlr.getDocSheet());
      fileset.setValue("DOC_REV", doc_hdlr.getDocRev());
      fileset.setValue("DOC_TYPE", doc_hdlr.getDocType());
      fileset.setValue("FILE_TYPE", doc_hdlr.getFileType());
      fileset.setValue("MACRO_SELECTED", getPageProperty("MACRO_SELECTED"));

      // Title for command bar..
      if ("SHOW_MACRO_CHOOSER".equals(client_action))
         fileblk.setTitle(mgr.translate("DOCMAWEDMMACROCHOOSEAMACRO: Choose a macro"));
      else if ("SHOW_APPLICATION_CHOOSER".equals(client_action))
         fileblk.setTitle(mgr.translate("DOCMAWEDMMACROCHOOSEANAPPLICATION: Choose an application"));
      else
         fileblk.setTitle(mgr.translate("DOCMAWEDMMACROOPPERATIONINPROGRESS: Operation in progress..."));
      
      // Added by Terry 20120918
      // Title for edm file chooser
      if ("SHOW_EDM_FILE_CHOOSER".equals(client_action))
         edmblk.setTitle(mgr.translate("DOCMAWEDMMACROCHOOSEEDMFILES: Choose edm files"));
      // Added end
      
   }


   protected String getDescription()
   {
      ASPManager mgr = getASPManager();
      return mgr.translate("DOCMAWEDMMACROTITLE: Edm Macro");
   }


   protected AutoString getContents() throws FndException
   {
      debug(this+": getContents() {");

      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();

      if (!critical_error)
      {
         out.append("<html>\n");
         out.append("<head>");
         out.append(mgr.generateHeadTag(getDescription()));
         out.append("<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n");
         out.append("</head>\n");
         out.append("<body");
         out.append(">\n");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(">\n");
         out.append(mgr.startPresentation(getFileActionTitle()));
         out.append("  <input type=\"hidden\" name=\"LOCAL_CHECKOUT_PATH\" value=\"\">\n");
         // Added by Terry 20121018
         // Add local download path hidden field.
         out.append("  <input type=\"hidden\" name=\"LOCAL_DOWNLOAD_PATH\" value=\"\">\n");
         // Added end
         out.append("  <input type=\"hidden\" name=\"LOCAL_FILE_NAME\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"CHECKED_IN_FILE_TYPE\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"FILES_CHECKED_IN\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"VIEW_FILES_CHECKED_IN\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"FILE_EXTS_DOWNLOADED\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"APPLIC_SELECTED\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"SELECTED_DIRECTORY\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"CONFIRM\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"EXTERNAL_VIEWER\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"CLIENT_STATUS\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"CLIENT_ERROR\" value=\"\">\n");
         out.append("  <input type=\"hidden\" name=\"SELECTED_FILE_NAME\" value=\"\">\n");
	      out.append("  <input type=\"hidden\" name=\"ROLLBACK_STATUS\" value=\"\">\n"); //Bug Id 72460
	      //Bug Id 73736, Start
	      out.append("  <input type=\"hidden\" name=\"VALIDATE_OK\" value=\"\">\n"); 
         //Bug Id 73736, End

         //
         // Generate layout
         //
	      boolean show_button = true;
	      
	      filelay.showBottomLine(false);
	      filelay.setEditable();
	      out.append(filebar.showBar());
	      
	      if ("SHOW_EDM_FILE_CHOOSER".equals(client_action))
	      {
	         if (!edmlay.isSingleLayout() && !edmlay.isMultirowLayout() || edmset.countRows() == 0)
	            show_button = false;
	      }
	      

         beginDataPresentation();

         out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
         out.append("<tr><td>");

         if ("SHOW_MACRO_CHOOSER".equals(client_action))
         {
            mgr.getASPField("MACRO").unsetHidden();
            out.append(filelay.generateDataPresentation());
            out.append("<script language=\"javascript\">\n");
            out.append("document.form.MACRO.options[0] = new Option(\"" + getFileActionTitle() + " " + mgr.translate("DOCMAWEDMMACRONOMACRO: without macro") + "\", \"" + EdmMacroProcess.getEmptyMacro() + "\");\n");
            for (int x = 0; x < data_buf.countItems(); x++)
            {
               if ("DATA".equals(data_buf.getNameAt(x)))
                  out.append("document.form.MACRO.options[" + (x + 1) + "] = new Option(\"" + data_buf.getBufferAt(x).getValue("DESCRIPTION") + "\", \"" + data_buf.getBufferAt(x).getValue("VALUE") + "\");\n");
            }
            out.append("function setMacroSelection(val)\n");
            out.append("{\n");
            out.append("   if (document.form.MACRO.options.selectedIndex != 0)");
            out.append("      document.form.MACRO_SELECTED.value = document.form.MACRO.options[document.form.MACRO.options.selectedIndex].text;\n");
            out.append("}\n");
            out.append("</script>\n");
         }
         // Added by Terry 20120918
         // Show edm file chooser
         else if ("SHOW_EDM_FILE_CHOOSER".equals(client_action))
         {
            out.append(filelay.generateDataPresentation());
            out.append(edmlay.show());
            out.append("<script language=\"javascript\">\n");
//            if ("CHECKOUT".equals(getFileAction()) || "VIEW".equals(getFileAction()))
//            {
//               out.append("function rowClicked(row_no,table_id,elm,box,i){\n");
//               out.append("   deselectAllRows('__SELECTED1');\n");
//               out.append("   cca = false;\n");
//               out.append("   if (!cca) {\n");
//               out.append("      if (box != null) {\n");
//               out.append("         if (box.length > 1)\n");
//               out.append("            row = box[i];\n");
//               out.append("         else\n");
//               out.append("            row = box;\n");
//               out.append("         row.checked = true;\n");
//               out.append("         CCA(row, row_no);\n");
//               out.append("      }\n");
//               out.append("   }\n");
//               out.append("   cca = false;\n");
//               out.append("}\n");
//            }
            out.append("</script>\n");
         }
         // Added end
         else if ("SHOW_APPLICATION_CHOOSER".equals(client_action))
         {
            mgr.getASPField("APP_FILE_TYPE").unsetHidden();
            out.append(filelay.generateDataPresentation());
            out.append("<script language=\"javascript\">\n");
            for (int x = 0; x < data_buf.countItems(); x++)
            {
               if ("DATA".equals(data_buf.getNameAt(x)))
                  out.append("document.form.APP_FILE_TYPE.options[" + x + "] = new Option(\"" + data_buf.getBufferAt(x).getValue("DESCRIPTION") + "\", \"" + data_buf.getBufferAt(x).getValue("FILE_TYPE") + "\");\n");
            }
            out.append("</script>\n");
         }
         else if ("SEND_FILES_IN_FOLDER_BY_MAIL".equals(client_action)|| "ZIP_FILES_IN_FOLDER".equals(client_action))
         {
            out.append(mail_client_message); // mail_client_message must come with format and all neccessary html tags.
         }
         else
         {
            if (!mgr.isEmpty(fileset.getValue("MACRO_SELECTED")))
                mgr.getASPField("MACRO_SELECTED").unsetHidden();

            out.append(filelay.generateDataPresentation());//
         }


         if ("SHOW_MACRO_CHOOSER".equals(client_action) || "GET_NAME_FILES_USING".equals(client_action))
         {
            if (isFileNameConfigurable())
            {
               out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\">");
               out.append("<tr>");
               out.append("<td>&nbsp;</td>");
               out.append("<td width=100%>");
               out.append("<table>");
               out.append("<tr><td>&nbsp;</td></tr>");
               out.append("<tr><td>");
               out.append(fmt.drawReadLabel(mgr.translate("DOCMAWEDMMACRONAMEFILESUSING: Name Files Using")));
               out.append("</td></tr>");
               out.append("<tr><td>");
               out.append(drawRadio(mgr.translate("DOCMAWEDMMACRODOCUMENTTITLEANDNAME: Document Title and Name"), "NAME_FILES_USING", "DOCUMENT_TITLE_AND_NAME", "Default Title".equals(name_files_using), "onClick=\"\""));
               out.append("</td></tr>");
               out.append("<tr><td>");
               out.append(drawRadio(mgr.translate("DOCMAWEDMMACROORIGINALFILENAME: Original File Name"), "NAME_FILES_USING", "ORIGINAL_FILE_NAME", "Default Original".equals(name_files_using), "onClick=\"\""));
               out.append("</td></tr></table>");
               out.append("</td>");
               out.append("<td>&nbsp;</td>");
               out.append("</tr></table>");
            }
         }

         out.append("</td></tr>");
         out.append("</table>");

         // draw dotted line after table
         endDataPresentation(true);

         if (show_button && ("SHOW_MACRO_CHOOSER".equals(client_action) || "SHOW_APPLICATION_CHOOSER".equals(client_action) || "GET_NAME_FILES_USING".equals(client_action) || "SHOW_EDM_FILE_CHOOSER".equals(client_action)))
         {
            out.append("<table cellpadding=10 cellspacing=0 border=0 width=100%>\n");
            out.append("   <tr>\n");
            out.append("      <td align='right'>\n");
            out.append(fmt.drawSubmit("OK",mgr.translate("DOCMAWEDMMACROOK:    OK   "),""));
            out.append("      </td>\n");
            out.append("      <td align='left'>\n");
            out.append(fmt.drawButton("CANCEL",mgr.translate("DOCMAWEDMMACROCANCEL: Cancel"),"OnClick=javascript:window.close();"));
            out.append("      </td>\n");
            out.append("   </tr>\n");
            out.append("</table>\n");
         }

         out.append(mgr.generateClientScript());
         out.append("</form>\n");
         out.append(DocmawUtil.getClientMgrObjectStr());
         out.append("</body>\n");
         out.append("</html>\n");
         
         // Bug ID 89622, Start (Moved this logic inside the IF condition, else will give javascript error when critical_error = false)
         // Set window size..
//         appendDirtyJavaScript("if (document.body.offsetWidth > 650 || document.body.offsetHeight > 550)\n");
//         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   try\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      window.resizeTo(650, 550);\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   catch(err){}\n");
//         appendDirtyJavaScript("}\n");
         // Bug ID 89622, Finish
      }

      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      debug("client_action = " + client_action);
      debug("resubmit = " + resubmit);
      appendDirtyJavaScript("resubmit = " + resubmit + ";\n");
      appendDirtyJavaScript("log_error = " + logError() + ";\n");

      // Initialize the ocx..
      appendDirtyJavaScript("initClientManager();\n");
      appendDirtyJavaScript("function initClientManager()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      translateOCXStrings();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (err.number == -2146827850)\n");
      appendDirtyJavaScript("      {\n");
      // appendDirtyJavaScript("         alert(\"" + mgr.translate("The OCX used on this page could not be downloaded. The cause may be too high security settings for this site. Information on setting this up can be found in the ReleaseNotes for Docmaw. Contact your system administrator.\\n\\nThis page will now close.") + "\");\n");
      // appendDirtyJavaScript("         window.close();\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      //Remove cookies prefixed with fndwebPageID_
      appendDirtyJavaScript(" var cookie_string = document.cookie;\n");
      appendDirtyJavaScript(" removePageIDCookies(cookie_string);\n");

      appendDirtyJavaScript("function removePageIDCookies(cookie_string)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_array = cookie_string.split(';');\n");
      appendDirtyJavaScript("    var cookie_count = cookie_array.length;\n");
      appendDirtyJavaScript("    var cookies_to_remove = new Array();\n");
      appendDirtyJavaScript("    var cookie_remove_count = 0;\n");
      appendDirtyJavaScript("    for (i = 0; i < cookie_count; i++)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       var cookie = cookie_array[i];\n");
      appendDirtyJavaScript("       var re = new RegExp('[\t]*fndwebPageID_[0-9x]+[ \t]*=\\*');\n");
      appendDirtyJavaScript("       if (re.test(cookie))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          var cookie_pieces = cookie.split('=');\n");
      appendDirtyJavaScript("          cookie_pieces[0] = cookie_pieces[0].substring(14);\n");
      appendDirtyJavaScript("          cookies_to_remove[cookie_remove_count]= trim_spaces(cookie_pieces[0]);\n");
      appendDirtyJavaScript("          cookie_remove_count++;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if (typeof(APP_PATH) != 'undefined')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       for(i = 0; i < cookie_remove_count;i++)\n");
      appendDirtyJavaScript("       {\n");
      //appendDirtyJavaScript("          alert('cookies_to_remove[' + cookie_remove_count + '] = ' + cookies_to_remove[0] + '; COOKIE_PATH = ' + COOKIE_PATH);\n");
      appendDirtyJavaScript("          removeCookie(cookies_to_remove[i],COOKIE_PATH);\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function trim_spaces(trim_this)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   return trim_this.replace(/^\\s*/,'').replace(/\\s*$/,'');\n");
      appendDirtyJavaScript("}\n");


      if ("PERFORM".equals(client_action))
      {
         appendDirtyJavaScript("try\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   oCliMgr.Init(\"" + mgr.getApplicationAbsolutePath() +"\");\n");
         appendDirtyJavaScript("   document.form.LOCAL_FILE_NAME.value = oCliMgr.ExecuteAction();\n");
         //Bug Id 73736, Start
         appendDirtyJavaScript("   document.form.VALIDATE_OK.value = oCliMgr.FileValidateOK();\n");
         //Bug Id 73736, End
         appendDirtyJavaScript("   document.form.CHECKED_IN_FILE_TYPE.value = oCliMgr.getFileTypeSelected() ; \n");
         appendDirtyJavaScript("   document.form.FILES_CHECKED_IN.value = oCliMgr.GetFilesCheckedIn();\n");
         appendDirtyJavaScript("   document.form.VIEW_FILES_CHECKED_IN.value = oCliMgr.GetViewFilesCheckedIn();\n");
         appendDirtyJavaScript("   document.form.FILE_EXTS_DOWNLOADED.value = oCliMgr.GetdownLoadedExt();\n");
         appendDirtyJavaScript("   document.form.CLIENT_STATUS.value = \"OK\";\n");
         //Bug Id 72460, start
         appendDirtyJavaScript("   resubmit="+resubmit+";\n");
         //Bug Id 72460, end
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("catch(err)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if (!log_error)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("       resubmit = true;\n");//Bug Id 72460
         appendDirtyJavaScript("       if (err.description.length > 0)\n"); //Bug Id 72460
         appendDirtyJavaScript("       alert(err.description);\n");
         //Bug Id 72460, start
         appendDirtyJavaScript("       document.form.ROLLBACK_STATUS.value = \"NORMAL\";\n");
         appendDirtyJavaScript("       document.form.submit();\n");
         //Bug Id 72460, end
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("       resubmit = true;\n");//Bug Id 72460
         appendDirtyJavaScript("       document.form.CLIENT_STATUS.value = \"ERROR\";\n");
         appendDirtyJavaScript("       document.form.CLIENT_ERROR.value = err.description;\n");
         appendDirtyJavaScript("       document.form.ROLLBACK_STATUS.value = \"STRUCTURE\";\n");//Bug Id 72460
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("if(!resubmit) Close();\n");//Bug Id 72460
      }

      // Added by Terry 20120925
      // Invoke UNDO_CHECK_OUT_PROCESSING client action
      if ("UNDO_CHECK_OUT_PROCESSING".equals(client_action))
      {
         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }
      // Added end

      if("SHOW_FILE_CHOOSER".equals(client_action))
      {
         appendDirtyJavaScript("try\n");
         appendDirtyJavaScript("{\n");
         //Bug 60851, Changed the method call oCliMgr.ShowFileBrowser to oCliMgr.OpenFileDialog.
         // Modified by Terry 20120917
         // Add optional parameter, multiselect
         appendDirtyJavaScript("    document.form.SELECTED_FILE_NAME.value = oCliMgr.OpenFileDialog(\"" + mgr.encodeStringForJavascript(file_type_list) + "\" , \"\", \"True\");\n");
         // Modified end
         //Bug 60851, End
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("catch(err)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    alert(err.description);\n");
         appendDirtyJavaScript("    window.close();\n");
         appendDirtyJavaScript("}\n");
      }


      if ("DELETE_CHECKED_IN_FILES".equals(client_action))
      {
         appendDirtyJavaScript("var delete_file_setting = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"DeleteLocalFiles\");\n");
         appendDirtyJavaScript("var delete_file = \"" + doc_hdlr.getActionProperty("DELETE_LOCAL_FILE") + "\";\n");

         appendDirtyJavaScript("if (delete_file_setting == \"ON\" || delete_file == \"TRUE\")\n");
         appendDirtyJavaScript("{\n");

         for (int x = 0; x < local_file_names.length; x++)
         {
            appendDirtyJavaScript("oCliMgr.DeleteFile(\"" + local_file_names[x].replaceAll("\\\\", "\\\\\\\\") + "\");\n");
         }

         appendDirtyJavaScript("}\n");
         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }


      if ("DELETE_IMPORTED_FILES".equals(client_action))
      {
         for (int x = 0; x < local_file_names.length; x++)
         {
            appendDirtyJavaScript("oCliMgr.DeleteFile(\"" + local_file_names[x].replaceAll("\\\\", "\\\\\\\\") + "\");\n");
         }

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }


      if ("LAUNCH_LOCAL_FILE".equals(client_action))
      {
         appendDirtyJavaScript("oCliMgr.LaunchFile(\"" + mgr.encodeStringForJavascript(local_file_name) + "\");\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }

      //Bug 73736, Start, Added check on sLaunchFile
      if ("LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER".equals(client_action)&& !sLaunchFile.equals("FALSE"))
      {
         if (DEBUG) debug("debug: launchfile " + local_file_name);
            appendDirtyJavaScript("oCliMgr.LaunchFileWithExternalViewer(\"" + mgr.encodeStringForJavascript(local_file_name)+ "\");\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }
      //Bug 73736, End

      if ("OPEN_LOCAL_FOLDER".equals(client_action))
      {
         //Bug Id 81807, start
         if (ctx.readFlag("NO_VIEW_REF_EXIST", false))
         {
            ctx.writeFlag("NO_VIEW_REF_EXIST",false);
            appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWEDMMACRONOVIEWFILE: View copy did not exist for all files, original was taken instead.") + "\");\n");
         }
         //Bug Id 81807, end

         appendDirtyJavaScript("oCliMgr.OpenDocumentFolder(\"" + mgr.encodeStringForJavascript(local_path_name) + "\");\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }


      if ("ZIP_AND_EMAIL".equals(client_action))
      {
         appendDirtyJavaScript("oCliMgr.Zip(\"" + zip_file_name + "\", \"" + local_path_name + "\", \"" + file_list + "\");\n");
         appendDirtyJavaScript("oCliMgr.SendMail(\"\", \"\", \""+ mgr.encodeStringForJavascript(mail_msg_subject) + "\", \"" + mail_msg_body + "\", \"" + mgr.encodeStringForJavascript(zip_file_name) + "\");\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }


      if ("DELETE_LOCAL_FILE".equals(client_action))
      {
         appendDirtyJavaScript("oCliMgr.DeleteFile(\"" + mgr.encodeStringForJavascript(local_file_name) + "\");\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }

      //Bug id 74523 added || "GET_PATH_FIRST".equals(client_action)
      if ("GET_LOCAL_CHECK_OUT_PATH".equals(client_action)|| "GET_PATH_FIRST".equals(client_action))
      {
         appendDirtyJavaScript("var local_path = oCliMgr.GetDocumentFolder();\n");
         //Bug 55611, Start
         appendDirtyJavaScript("var invalid = false;\n");
         //Bug 55611, End
         appendDirtyJavaScript("if (!oCliMgr.FolderExists(local_path))\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    local_path = oCliMgr.BrowseForLocalPath();\n");
         appendDirtyJavaScript("    if (!oCliMgr.FolderExists(local_path))\n");
         appendDirtyJavaScript("    {\n");
         //Bug 55611, Start
         appendDirtyJavaScript("       alert(\"" + mgr.translate("DOCMAWEDMMACROOPRABORTED: Operation Aborted.") + "\");\n");
         appendDirtyJavaScript("       invalid = true;\n");
         //Bug 55611, End
         appendDirtyJavaScript("       window.close();\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("       oCliMgr.SetDocumentFolder(local_path);\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("document.form.LOCAL_CHECKOUT_PATH.value = local_path;\n");
         //Bug Id 55611, Start, Added check on invalid
         //Bug Id 54793, Start
         appendDirtyJavaScript("if (!invalid){\n");
         appendDirtyJavaScript("var PathWriteProtected = oCliMgr.CheckPathWriteProtected(local_path);\n");
         appendDirtyJavaScript("if (PathWriteProtected)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   eval(\"opener.refreshParent()\");\n");
         appendDirtyJavaScript("   window.close();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("}\n");
         //Bug Id 54793, End
         //Bug Id 55611, End
      }
      
      // Added by Terry 20121018
      // Get local download path
      if ("GET_LOCAL_DOWNLOAD_PATH".equals(client_action))
      {
         appendDirtyJavaScript("   var invalid = false;\n");
         appendDirtyJavaScript("   local_path = oCliMgr.BrowseForLocalDownloadPath();\n");
         appendDirtyJavaScript("   if (!oCliMgr.FolderExists(local_path))\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      alert(\"" + mgr.translate("DOCMAWEDMMACROOPRABORTED: Operation Aborted.") + "\");\n");
         appendDirtyJavaScript("      invalid = true;\n");
         appendDirtyJavaScript("      window.close();\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      oCliMgr.SetLocalDownloadPath(local_path);\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   document.form.LOCAL_DOWNLOAD_PATH.value = local_path;\n");
         appendDirtyJavaScript("   if (!invalid)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      var PathWriteProtected = oCliMgr.CheckPathWriteProtected(local_path);\n");
         appendDirtyJavaScript("      if (PathWriteProtected)\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         eval(\"opener.refreshParent()\");\n");
         appendDirtyJavaScript("         window.close();\n");
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("   }\n");
      }
      // Added end


      if ("SEND_MAIL".equals(client_action))
      {
	  
         appendDirtyJavaScript("var document_folder = oCliMgr.GetDocumentFolder();\n");
         appendDirtyJavaScript("if (document_folder.charAt(document_folder.length - 1) != '\\\\')\n");
         appendDirtyJavaScript("    document_folder = document_folder + '\\\\';\n");
         appendDirtyJavaScript("document_folder = document_folder + 'Temp' + '\\\\';\n");
      
         
         appendDirtyJavaScript("var file_list = \"\";\n");
         appendDirtyJavaScript("var base_file_name = \"\";\n");
         appendDirtyJavaScript("base_file_name_length = 0;\n");
         for (int x = 0; x < mail_file_attachments.length; x++)
         {
            //Bug Id 74523, Start
            appendDirtyJavaScript("file_list += document_folder + \"" + mail_file_attachments[x]+ "\" + \"|\";\n");
	  
            //Bug Id 74523, Start
            //Bug Id 71463, start
            /*appendDirtyJavaScript(" if ("+MAX_FILE_NAME_LENGTH+" > (document_folder.length + " + (mail_file_attachments[x]).length()+")) \n");                           
                                                                                                                                                                          
             appendDirtyJavaScript("     {file_list += document_folder + \"" + mail_file_attachments[x] +"\" + \"|\"};\n");                                                
             appendDirtyJavaScript("else{\n");                                                                                                                             
             appendDirtyJavaScript("     var base_file_ext = \""+ mail_file_extensions[x]+"\";\n");                                                                        
             appendDirtyJavaScript("     base_file_name = \""+ mail_file_attachments[x]+" \";\n");                                                                         
             appendDirtyJavaScript("     base_file_name_length = "+MAX_FILE_NAME_LENGTH+"-(document_folder.length + 2+ base_file_ext.length+" + doc_keys.length()+");\n"); 
             appendDirtyJavaScript("     base_file_name = base_file_name.substring(0,base_file_name_length);\n");                                                          
             appendDirtyJavaScript("     base_file_name = base_file_name+\" \"+\""+ doc_keys+"\"+ \".\"+ base_file_ext;\n ");                                              
             appendDirtyJavaScript("     file_list += document_folder+base_file_name+\"|\";}\n ");                                                                         
             */                                                                                                                                                             

            //Bug Id 71463, start
         }
         appendDirtyJavaScript("try\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    oCliMgr.SendMail(\"\", \"\", \"" + mgr.encodeStringForJavascript(mail_msg_subject) + "\", \"" + mail_msg_body + "\", file_list);\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("catch(err){}\n");
         appendDirtyJavaScript("Close();\n");
      }

      if ("SEND_FILES_IN_FOLDER_BY_MAIL".equals(client_action))
      {
         //Bug Id 81807, start
         if (ctx.readFlag("NO_VIEW_REF_EXIST", false))
         {
            ctx.writeFlag("NO_VIEW_REF_EXIST",false);
            appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWEDMMACRONOVIEWFILE: View copy did not exist for all files, original was taken instead.") + "\");\n");
         }
         //Bug Id 81807, end

         appendDirtyJavaScript("try\n");
         appendDirtyJavaScript("{\n");
         // if zip file name is empty then we do not make a zip file: bakalk
         // Bug Id 89383, start
         //appendDirtyJavaScript("    oCliMgr.SendFilesinFolderByMail(\""+mgr.encodeStringForJavascript(mail_folder)+"\", \"" + mgr.encodeStringForJavascript(mail_msg_subject) + "\", \"" + mail_msg_body + "\",\""+mgr.encodeStringForJavascript(mail_zip_file_name)+"\");\n");
         appendDirtyJavaScript("    oCliMgr.SendFilesinFolderByMail(\"" + mgr.encodeStringForJavascript(mail_to) + "\", \"\" ,\""+mgr.encodeStringForJavascript(mail_folder)+"\", \"" + mgr.encodeStringForJavascript(mail_msg_subject) + "\", \"" + mail_msg_body + "\",\""+mgr.encodeStringForJavascript(mail_zip_file_name)+"\");\n");
         // Bug Id 89383, end
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("catch(err){alert(err.message);}\n");
         appendDirtyJavaScript("Close();\n");
      }

      if ("ZIP_FILES_IN_FOLDER".equals(client_action))
      {
         //Bug Id 81807, start
         if (ctx.readFlag("NO_VIEW_REF_EXIST", false))
         {
            ctx.writeFlag("NO_VIEW_REF_EXIST",false);
            appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWEDMMACRONOVIEWFILE: View copy did not exist for all files, original was taken instead.") + "\");\n");
         }
         //Bug Id 81807, end

         appendDirtyJavaScript("try\n");
         appendDirtyJavaScript("{\n");
         // if filenames is empty then we take all files in the directory for ziping: bakalk
         appendDirtyJavaScript("    oCliMgr.Zip(\""+mail_zip_file_name+"\", \"" + mail_folder + "\", \"\",false,true);\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("catch(err){alert(err.message);}\n");
         appendDirtyJavaScript("Close();\n");
      }

      


      if ("SELECT_DIRECTORY".equals(client_action))
      {
         appendDirtyJavaScript("var local_path = oCliMgr.BrowseForLocalPath();\n");
         appendDirtyJavaScript("if (local_path == \"\")\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   window.close();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("else\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.SELECTED_DIRECTORY.value = local_path;\n");
         appendDirtyJavaScript("}\n");
      }


      if ("SHOW_CONFIRMATION".equals(client_action))
      {
         appendDirtyJavaScript("if (window.confirm(\"" + confirm_msg + "\"))\n");
         appendDirtyJavaScript("   document.form.CONFIRM.value = \"OK\";\n");
         appendDirtyJavaScript("else\n");
         appendDirtyJavaScript("   document.form.CONFIRM.value = \"CANCEL\";\n");
      }
      
      if ("SHOW_INFORMATION".equals(client_action))
      {
         appendDirtyJavaScript("window.alert(\"" + this.information_msg + "\");\n");
      }
      
      if ("SHOW_ALERTBOX".equals(client_action))
      {
         appendDirtyJavaScript("window.alert(\"" + this.alert_msg + "\");\n");
      }


      if ("CHECK_EXTERNAL_VIEWER".equals(client_action))
      {
         appendDirtyJavaScript("var viewer = oCliMgr.GetExternalViewerApplication();\n");
         //Bug 55611, Start
         appendDirtyJavaScript("var local_path = oCliMgr.GetDocumentFolder();\n");
         appendDirtyJavaScript("var invalid = false;\n");
         appendDirtyJavaScript("if (!oCliMgr.FolderExists(local_path))\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    local_path = oCliMgr.BrowseForLocalPath();\n");
         appendDirtyJavaScript("    if (!oCliMgr.FolderExists(local_path))\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("       alert(\"" + mgr.translate("DOCMAWEDMMACROOPRABORTED: Operation Aborted.") + "\");\n");
         appendDirtyJavaScript("       invalid = true;\n");
         appendDirtyJavaScript("       window.close();\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    else\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("       oCliMgr.SetDocumentFolder(local_path);\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("document.form.LOCAL_CHECKOUT_PATH.value = local_path;\n");
         appendDirtyJavaScript("if (!invalid){\n");
         //Bug Id 55611, End
         //Bug Id 54793, Start
         appendDirtyJavaScript("var PathWriteProtected = oCliMgr.CheckPathWriteProtected(local_path);\n");
         appendDirtyJavaScript("if (PathWriteProtected)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   eval(\"opener.refreshParent()\");\n");
         appendDirtyJavaScript("   window.close();\n");
         appendDirtyJavaScript("}\n");
         //Bug Id 54793, End
         appendDirtyJavaScript("else if (viewer == \"\" || viewer == null)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    alert(\"" + mgr.translate("DOCMAWEDMMACROEXTVIEWERAPPNOTEXIST: An External Viewer Application has not been configured. Please set the path in User Settings.") + "\");\n");
         appendDirtyJavaScript("    window.close();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("else\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.EXTERNAL_VIEWER.value = \"AVAILABLE\";\n");
         appendDirtyJavaScript("}\n");
         //Bug Id 55611, Start
         appendDirtyJavaScript("}\n");
         //Bug Id 55611, End
      }


      if ("SHOW_ERROR_LOG".equals(client_action))
      {
         appendDirtyJavaScript("var v_pos = (screen.availHeight - 175) - 50;");
         appendDirtyJavaScript("var h_pos = 150;");
         appendDirtyJavaScript("window.open(\"");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("\",\"reportWindow\",\"status=no,resizable,scrollbars,width=500,height=175,left=\" + h_pos + \",top=\" + v_pos);\n");

         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }


      if ("ERROR".equals(client_action))
      {
         if (!logError())
         {
            appendDirtyJavaScript("window.alert(\""+ mgr.encodeStringForJavascript(err_msg) +"\");\n");
         }
         if (!resubmit)
            appendDirtyJavaScript("window.close();\n");
      }

      //Bug Id 72460, start
      if ("CLOSE".equals(client_action))
      {
         if (bDeleteTransmittalFoler) 
         {
            String transmittal_checkout_folder = null;
            if ("CONNECTREPORT".equals(doc_hdlr.getDocumentProperty("FILE_ACTION"))) 
            {
               doc_hdlr.lastDocument();
               transmittal_checkout_folder = doc_hdlr.getDocumentProperty("MAIL_FOLDER");
            }
            else
            {
               doc_hdlr.lastDocument();
               transmittal_checkout_folder = doc_hdlr.getDocumentProperty("PATH");
               if (transmittal_checkout_folder == null) 
               {
                  transmittal_checkout_folder = doc_hdlr.getDocumentProperty("MAIL_FOLDER");
               }
            }
            transmittal_checkout_folder = transmittal_checkout_folder.replace("\\\\","\\");
            transmittal_checkout_folder = transmittal_checkout_folder.replace("\\","\\\\");
            appendDirtyJavaScript("oCliMgr.DeleteFolder(\"" + transmittal_checkout_folder + "\");\n");
            appendDirtyJavaScript("if (window.opener.CURRENT_URL == \"/b2e/secured/docmaw/DocTransmittalInfo.page?\")\n");
            appendDirtyJavaScript("   eval(\"opener.update_State('FALSE')\");\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("   eval(\"opener.update_Wizard_State('FALSE')\");\n");
         }
         appendDirtyJavaScript("Close();\n");
      }
      //Bug Id 72460, end


      if ("CLOSE_WINDOW".equals(client_action))
      {
         //Bug Id 81807, start
         if (ctx.readFlag("NO_VIEW_REF_EXIST", false))
         {
            ctx.writeFlag("NO_VIEW_REF_EXIST",false);
            appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWEDMMACRONOVIEWFILE: View copy did not exist for all files, original was taken instead.") + "\");\n");
         }
         //Bug Id 81807, end
         appendDirtyJavaScript("Close();\n");
      }
      
      // Added by Terry 20121019
      // Download completed, close EdmMacro window.
      if ("DOWN_CLOSE_WINDOW".equals(client_action))
      {
         appendDirtyJavaScript("window.alert(\""+ mgr.encodeStringForJavascript(mgr.translate("EDMMACRODOWNLOADSUCC: Documents download to &1 successfully.", getLocalDownloadPath())) +"\");\n");
         appendDirtyJavaScript("Close();\n");
      }
      // Added end
      
      // Added by Terry 20130318
      // Check in files client action
      if ("CHECKE_IN_FILES".equals(client_action))
      {
         if (!resubmit)
            appendDirtyJavaScript("Close();\n");
      }
      // Added end


      if (resubmit)
      {
         appendDirtyJavaScript("if (resubmit)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if (document.form.ROLLBACK_STATUS.value != \"NORMAL\")\n"); //Bug Id 72460
         appendDirtyJavaScript("      document.form.submit();\n");
         appendDirtyJavaScript("}\n");
      }

      appendDirtyJavaScript("function Close()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   resubmit = false;\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      //appendDirtyJavaScript("      document.write('This window is closing.......');\n");

      if (!mgr.isEmpty(doc_hdlr.getActionProperty("PROVIDER_PREFIX"))) { //when this page is called from a portlet :bakalk
         appendDirtyJavaScript("      eval(\"opener."+doc_hdlr.getActionProperty("PROVIDER_PREFIX")+"refreshParent()\");\n");
      }
      else
         appendDirtyJavaScript("      eval(\"opener.refreshParent()\");\n");
      //bug 68527 start
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err){}\n");

      appendDirtyJavaScript("try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      window.close();\n"); 
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err){}\n");
      //bug 68527 end

      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function translateOCXStrings()\n");
      appendDirtyJavaScript("{\n");
      String [][] climgr_translations = DocmawConstants.getCliMgrTranslations(mgr);
      for (int i = 0; i < climgr_translations.length; i++)
      {
         appendDirtyJavaScript("   oCliMgr.AddLocalizedString(\"" + climgr_translations[i][0] + "\", \"");
         appendDirtyJavaScript(climgr_translations[i][1]);
         appendDirtyJavaScript("\");\n");
      }
      appendDirtyJavaScript("}\n");

      return out;
   }

   //report ------------------------

   private void  orderReport(String fullFileName,String TransmittalId) throws java.io.IOException
  {
     ASPManager mgr = getASPManager();
     inf = mgr.newASPInfoServices();

     debug("***** orderReport >>>> TransmittalId="+TransmittalId);

     //String reportId = "PLANT_SWITCHGEAR_REQ_LIST_REP";
     String reportId = "DOC_TRANSMITTAL_INFO_REP";

     String[] parameterColumns = {"TRANSMITTAL_ID","FROM_WIZARD"};

     String[] parameterValues  = {TransmittalId,"TRUE"};          

     String report_attr = inf.itemValueCreate("REPORT_ID", reportId)+
                          inf.itemValueCreate("LAYOUT_NAME", "")+
                          inf.itemValueCreate("LANG_CODE", "");

     this.setDefualtLanguageLayout(reportId);


     String parameter_attr = getParamList(parameterColumns,parameterValues);

     String result_key = inf.onlineReportOrder(report_attr, parameter_attr);

     String pdf_job_id = this.writeReportToArchive(inf,result_key);



     byte[] data = this.readReport(inf,pdf_job_id);
     //byte[] data = this.readReport(inf,result_key);

     this.writeToFile(data,fullFileName);

  } // end of orderReport



  public byte[] readReport(ASPInfoServices inf,String printJobId) throws java.io.IOException
  {
      ASPManager mgr = getASPManager();
      String where_condition = "PRINT_JOB_ID=?";
      String where_parameters = "PRINT_JOB_ID^S^IN^"+printJobId;

      String temp = "";
      int a = 0;
      do{
         temp = inf.getPDFContents(where_condition, where_parameters);
      }while(mgr.isEmpty(temp));

      debug("binary data \n" + temp);

      return Util.fromBase64Text(temp);
  }// end of readReport 



   public String writeReportToArchive(ASPInfoServices inf,String result_key)
  {
     return inf.createPdfReport( result_key, this.layout, this.language );
  }// end of writeReportToArChive


   public String getParamList(String[] column,String[] value)
   {
      String paramStr ="";
      for (int a=0;a<column.length;a++) {
         paramStr += column[a];
         paramStr += (char)31;
         paramStr += value[a];
         paramStr += (char)30;
      }


      return paramStr;
   }

   private void writeToFile(byte[]  data,
                    String fileName) throws IOException
   {

      FileOutputStream foStream;

      foStream = new FileOutputStream(fileName,true);
      foStream.write(data);
      foStream.close();
   }

   private void setDefualtLanguageLayout(String report_id)
   {
      ASPManager mgr = getASPManager();

     String def_lang = readGlobalProfileValue("Defaults/Language"+ProfileUtils.ENTRY_SEP+"Default",false);

     language = mgr.isEmpty(def_lang)? mgr.getLanguageCode():def_lang;
     trans = mgr.newASPTransactionBuffer();

     ASPCommand cmd = trans.addCustomFunction("DEFAULT_LAYOUT", "Report_Layout_Definition_API.Get_Default_Layout","f2");
     cmd.addParameter("f1", report_id);

     trans = mgr.perform(trans);

     layout = trans.getValue("DEFAULT_LAYOUT/DATA/f2");
   }

   //report-------------------------
}
