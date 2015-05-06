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
*  File        : DocTransmittalWizard..java
*  Modified    :
*    Bakalk  2007-01-24  Created.
*    Bakalk  2007-03-27  Added functionality "Connect Report to Transmittal".
*    BAKALK  2007-04-05  Modified the server method Create_Transmittal_Report_Doc to Create_Transmittal_Doc 
*    BAKALK  2007-04-18  Handled Out Bound status.
*    BAKALK  2007-05-23  Call Id: 141463: Removed 'our' prefix from labels./ Aspfields COMMENT_FILE_ID and COMMENT_RECEIVED_ID were replaced byCOMMENT_RECEIVED.
*    BAKALK  2007-06-11  Call Id: 145980: Made Contact Person in Uppercase.
*    BAKALK  2007-06-13  Call Id: 145984: Modified saveTransmittal(), added a new method generateTransmittalId().
*    BAKALK  2007-06-22  Call Id: 145982, Used record set for storing date values with correct format,Updating State when sending Transmittal was not correct. fixed it too.
*    BAKALK  2007-06-25  Call Id: 145981, Added * for all mandatory fields.
*    BAKALK  2007-06-28  Call Id: 146510, Modified values of fromNavigator variable.
*    UPDELK  2007-07-24  Call Id: 146870: DOCMAW/DOCMAN - Transmittal and dynamic dependencies to PROJ
*    BAKALK  2007-07-25  Call Id: 146748, Modified updateDateValues().
*    NaLrlk  2007-08-10  XSS Correction.
*    BAKALK  2007-08-21  Call Id: 143180: Added zip file option in last step and implemented it.
*    BAKALK  2007-09-17  Call Id: 148751: Added 'Add Documents' command in 3rd step and implemented it.
*    BAKALK  2007-09-17  Bug Id: 68407: Modified printContents() and run().
*    BAKALK  2007-12-07  Bug Id: 68404: Modified getMessageBodyForMail().

* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*   071213      ILSOLK     Bug Id 68773, Eliminated XSS.
*   071215      BAKALK     Bug Id 68455, Modified addDocsToBuffer().
*   071220      BAKALK     Bug Id 68412, Made many modifications to handle excetions thrown from database.
*   080303      VIRALK     Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*   080311      BAKALK     Bug Id 71844, Checked if trasmittal id already exists in reading step1, if it does we go back to step1.
*                          when using transmittal counter returns a non empty value, user entered value for transmittal id is now discarded.
*   080331      BAKALK     Bug Id 71759, Checked if all doucuments are relased before sending the Transmittal.
*   080401      BAKALK     Bug Id 71844, Many validation done to check transmitta is created correctly.
*   080403      BAKALK     Bug Id 71844, Some more validations done.
*   080404      BAKALK     Bug Id 71759, Added new method to handle single quote in error message, Stopped creating a doc for report
*                          if file operations are not being done due to any problem.
*   080404      BAKALK     Bug Id:69301, Added some validation on buttons: on 1st step, Next and Finish button check
*                          if mandatory fields are not empty. On 3rd step Next button checks if some documents have been
*                          connected to the transmittal.
*   080508      BAKALK     Bug Id: 71838, Disabled 'Send E-Mail and Transmittal Report' option for In bound Transmittal.
*   080715      AMNALK     Bug Id: 72460, Added new javascript function update_Wizard_State() and modified run(). 
*   080825      AMNALK     Bug Id: 75074, Modified executeFileOperations() and printContents()
*   080909      AMNALK     Bug Id: 76248, Modified preDefine(), writeValidateMethod4Button() & printContents() to make the fields readonly accordingly.
*   080924      AMNALK     Bug Id: 77029, Modified printContents() by removing ACTUAL_SENT_DATE & ACTUAL_RETURN_DATE from wizard.
*   081010      AMNALK     Bug Id: 77060, Modified preDefine() to remove the readonly flag from the transmittal id and saveTransmittal().
*   081031      AMNALK     Bug Id: 75085, Modified getMessageBodyForMail() & addDocsToBuffer() and added new functions findDocumentsInStructure() & isStructure().
*   090216      AMNALK     Bug Id: 79174, Modified writeValidateMethod4Button(), printContents() and setTitle().
*   090512      AMCHLK     Bug Id: 81808, Moved the fields 'Project ID', 'Sub project ID' and 'Activity ID' to Step 01. 
*   090602      AMNALK     Bug Id: 81807, Modified lots of places to improve the functionality of the last step of the wizard.
*   090602      AMNALK     Bug Id: 81806, Modified gotoTransmittalInfo(), okFind(), printContents(), run() and connectDocumentsToTrans().
*   090603      DULOLK     Bug Id: 81808, Made Transmittal Info multiline. Filtered Receiver Person correctly.
*   090608      DULOLK     Bug Id: 81808, Modified finish() to disable LOVs for proj fields if error is raised.
*   090609      DULOLK     Bug Id: 81808, Modified finish() to stop after rasing errors in 4th step.
*   090703      SHTHLK     Bug Id: 84461, Set the length of TRANSMITTAL_ID to 120 
*   090814      SHTHLK     Bug Id; 85164, Added Lov for RECEIVER_ADDRESS.
*   100402      VIRALK     Bug Id; 88317, Modified createNewDocForReport(). Restrict creation of new docs for * users.
*   100427      RUMELK     Bug Id: 89383, Modified addSendFolderByMailToBuffer().
*   100430      AMCHLK     Bug Id: 89680 Replaced view PERSON_INFO_LOV with PERSON_INFO_PUBLIC_LOV.
*   100615      BAKALK     Bug Id: 89221  Added a validation on PROJECT_ID, added line to add History log when
*                          customer project id is automatically modified.
*   100622      BAKALK     Bug Id: 89221, Now we are checking if Project_id is edited in step1, for existing transmittal Project Id from
*                          Transmittal Info is saved in contex buffer
*   100709      AMNALK     Bug Id: 91571, Set the field RECEIVER_CONTACT_PERSON to uppercase.
*   100723      AMNALK     Bug Id: 91417, Added function removeIllegalCharacters() and modified all the places to remove illegal characters from file paths.
*   100920      AMNALK     Bug Id: 92775, Modified preDefine() and validate() to add new fields and validate some existing fields.
*   100927      AMNALK     Bug Id: 78806, Added code to check the documents without files in transmittals.
*   101020      AMNALK     Bug Id: 93694, Added new function updateState() and modified gotoTransmittalInfo().
*   101025      AMNALK     Bug Id: 93698, Modified finish(), run() readStep1() and saveTransmittal() to avoid errors when an error raise during the new transmittal creation.
* -----------------------------------------------------------------------
*                        
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;
import ifs.docmaw.edm.*;
//Bug Id 75085, start
import java.lang.Character.*;
//Bug Id 75085, end



public class  DocTransmittalWizard extends ASPPageProvider
{

   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw. DocTransmittalWizard");
   
   //
   // Instances created on page creation (immutable attributes)
   //
   private ASPHTMLFormatter fmt;
   private ASPForm          frm;
   private ASPContext       ctx;
   private ASPCommand       cmd;

   private ASPBlock       headblk;
   private ASPRowSet      headset;
   private ASPCommandBar  headbar;
   private ASPTable       headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock       issueblk;
   private ASPRowSet      issueset;
   private ASPCommandBar  issuebar;
   private ASPTable       issuetbl;
   private ASPBlockLayout issuelay;

   private ASPTransactionBuffer trans;
   private boolean unreleased_sub_documents;
   private String message;
   private String client_action;
   private String server_command;
   private String client_command;

   private ASPBuffer            data;
   private ASPBuffer            fileOperationBuffer;

   // field values
   private String transmittalId;
   private String transmittalDesc;
   private String transmittalInfo;
   private String transmittalDirection;
   private String contactPerson;
   private String receiverType;
   private String receiverContactPerson;
   private String distributionMethod;
   private String receiverAddress;
   private String expectedSendDate;
   private String actualSentSate;
   private String expectedReturnDate;
   private String actualReturnDate;

   private String expectedSendDate_server;
   private String actualSentSate_server;
   private String expectedReturnDate_server;
   private String actualReturnDate_server;

   private String projectId;
   private String subProjectId;
   private String activityId;
   private String customerProjectId;
   
   private String customerSsubProjectId;
   private String customerActivityId;
   private String supplierProjectId;
   private String supplierSubProjectId;
   private String supplierActivityId;
   //Bug Id 81808, Start
   private String subContractProjectId;
   private String subContractSubProjectId;
   private String subContractActivityId;
   //Bug Id 81808, End
   private String checkoutPath;
   private String transmittalCheckoutPath;
   private String errorMessage;
   private String sPublishMode;

   private boolean bZipFiles;
   private boolean bPublishTrans;
   private boolean isProjInstalled;

   private String fromNavigator ;
   private String informations[][];
   private boolean refreshParentAndClose;
   private boolean fileOperationStarted;
   private boolean executingFileOperation;
   private boolean updateState;
   private int beforeNewLay;
   private int currLay;
   private String transfer_url;

   // to connect the report to Transmittal
   private String reportDocClass;
   private String reportDocNo;
   private String reportDocSheet;
   private String reportDocRev;
   private String reportDoc;
   private String reportFileNameName;
   private String reportDocState;
   private String reportDocTitle;

   //add documents
   private boolean bAddExistDoc;
   private String  root_path;

   // Bug Id: 68412, start
   String error_message;
   String windowLocation;
   // Bug Id: 68412, end

   //Bug Id: 71844,start
   private boolean bGoBack;
   private boolean bTransmittalIdNotCreated;
   //Bug Id: 71844, end

   //Bug Id:69301, start
   private String nextButtonEvent   = "validateNext()";
   private String finishButtonEvent = "validateFinish()";
   //Bug Id:69301, end

   //Bug Id 81807, start
   private boolean bDownloadOriginalFiles;
   private boolean bDownloadViewFiles;
   private boolean bOpenDownloadFolder;
   private boolean bStep4Visited;
   //Bug Id 81807, end

   //Bug Id 81806, start
   private ASPBuffer buff;
   private String sDocAttr;
   private String sFromOtherWindows = "FALSE";
   private boolean bLoadOnParentWindow;
   //Bug Id 81806, end
   
   //Bug Id: 89221, start
   private String oldCustomerProjectId;
   private boolean bCustomerProjectIdUpdated;
   //Bug Id: 89221, end
   
   // Bug Id 78806, start
   private String informationMessage;
   private boolean showConfirmation;
   private boolean confimationDone = false;
   // Bug Id 78806, end
   
   private boolean bSaveNewTransDone = false; // Bug Id 93698

   private void addReportToBuffer() // should be called after this.addDocsToBuffer(): bakalk
   {
      ASPManager mgr = getASPManager();
      ASPBuffer current_doc = mgr.newASPBuffer();

      current_doc.addItem("DOC_CLASS"    , reportDocClass);
      current_doc.addItem("DOC_NO"       , reportDocNo);
      current_doc.addItem("DOC_SHEET"    , reportDocSheet);
      current_doc.addItem("DOC_REV"      , reportDocRev);
      current_doc.addItem("DOC_TYPE"     , "ORIGINAL");
      //current_doc.addItem("FILE_ACTION"  , "SENDMAIL");//  we send the mail after all files downloaded.:bakalk
      current_doc.addItem("FILE_ACTION"  , "TRANSMITTALDOWNLOAD"); //Bug Id 81807
      current_doc.addItem("FILE_NAME"    , transmittalCheckoutPath + "\\" + reportFileNameName );
      current_doc.addItem("LAUNCH_FILE"  , "NO");
      current_doc.addItem("EXECUTE_MACRO", "NO");

      fileOperationBuffer.addBuffer("DATA", current_doc);
   }

   private void addSendFolderByMailToBuffer()
   {
      ASPManager mgr = getASPManager();

      String edmTitle = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSENDTRANS: Send Transmittal");
      String mailSubuject = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDREGDOCTRANS: Regarding Document Transmittal: ")+ transmittalId;

      String mailClientMessage = "<table><tr><td>";
      mailClientMessage+= fmt.drawReadLabel(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDTRANSID: Transmittal Id:"));
      mailClientMessage+= "</td><td>"+fmt.drawWriteLabel(transmittalId);
      mailClientMessage+= "</td><td>"+fmt.drawReadLabel(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDTRANSDES: Transmittal Desc."));
      mailClientMessage+= "</td><td>"+fmt.drawWriteLabel(transmittalDesc)+"</td>";
      mailClientMessage+="</tr></table>";
      
      ASPBuffer current_doc = mgr.newASPBuffer();
      current_doc.addItem("FILE_ACTION"        ,"SENDFOLDER_BYMAIL");
      current_doc.addItem("MAIL_FOLDER"        ,transmittalCheckoutPath);// make sure 'transmittalCheckoutPath' has proper value by now: bakalk
      current_doc.addItem("MAIL_MSG_SUBJECT"   ,mailSubuject);
      current_doc.addItem("MAIL_MSG_BODY"      ,getMessageBodyForMail());
      current_doc.addItem("MAIL_CLIENT_MESSAGE",mailClientMessage);
      current_doc.addItem("EDM_TITLE"          ,edmTitle);
      current_doc.addItem("MAIL_ZIP_FILE_NAME" ,bZipFiles?(transmittalCheckoutPath+"\\"+removeIllegalCharacters(transmittalId)+".zip"):"");//just pass "", if files are not zipped.:bakalk // Bug Id 91417
      
      // Bug Id 89383, start
      trans.clear();
      cmd = trans.addCustomFunction("MAIL_TO_BUF", "Doc_Transmittal_Issue_API.Get_Default_E_Mail", "ATTR");
      cmd.addParameter("RECEIVER_CONTACT_PERSON", receiverContactPerson);
      cmd.addParameter("RECEIVER_ADDRESS", receiverAddress);
      
      trans = mgr.perform(trans);
      current_doc.addItem("MAIL_TO", trans.getValue("MAIL_TO_BUF/DATA/ATTR"));
      // Bug Id 89383, end
         
      

      fileOperationBuffer.addBuffer("DATA", current_doc);
   }


   
   private void add_zipFolder_ToBuffer()
   {
      ASPManager mgr = getASPManager();

      String edmTitle = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDDOWNLTRANS: Download Transmittal");

      String mailClientMessage = "<table>";
      mailClientMessage+=   "<tr><td span=\"2\">";
      mailClientMessage+= fmt.drawWriteLabel(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDZIPFILES: Zipping Files in Transmittal"));
      mailClientMessage+=   "</td></tr>";
      mailClientMessage+=   "<tr><td>";
      mailClientMessage+= fmt.drawReadLabel(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDTRANSID: Transmittal Id:"));
      mailClientMessage+= "</td><td>"+fmt.drawWriteLabel(transmittalId);
      mailClientMessage+= "</td><td>"+fmt.drawReadLabel(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDTRANSDES: Transmittal Desc."));
      mailClientMessage+= "</td><td>"+fmt.drawWriteLabel(transmittalDesc)+"</td>";
      mailClientMessage+="</tr></table>";
      
      ASPBuffer current_doc = mgr.newASPBuffer();
      current_doc.addItem("FILE_ACTION"        ,"ZIP_FOLDER");//
      current_doc.addItem("MAIL_FOLDER"        ,transmittalCheckoutPath.replaceAll("\\\\","\\\\\\\\"));// make sure 'transmittalCheckoutPath' has proper value by now: bakalk //Bug Id 81807
      current_doc.addItem("MAIL_CLIENT_MESSAGE",mailClientMessage);
      current_doc.addItem("EDM_TITLE"          ,edmTitle);
      current_doc.addItem("MAIL_ZIP_FILE_NAME" ,transmittalCheckoutPath.replaceAll("\\\\","\\\\\\\\")+"\\\\"+removeIllegalCharacters(transmittalId)+".zip"); //Bug Id 81807 // Bug Id 91417
      

      fileOperationBuffer.addBuffer("DATA", current_doc);
   }


   private String getMessageBodyForMail()
   {
      ASPManager mgr = getASPManager();
      String mail_msg_body = "";
      // Bug Id: 68404, start
      trans.clear();
      cmd = trans.addCustomFunction("EMAILLOG","DOC_TRANSMITTAL_ISSUE_API.Get_Email_Log_For_Transmittal","ATTR");
      cmd.addParameter("TRANSMITTAL_ID",transmittalId);

      trans = mgr.perform(trans);
      mail_msg_body = trans.getValue("EMAILLOG/DATA/ATTR");
      //Bug Id 75085, start
      String record_sep = new String(Character.toChars(30));
      StringTokenizer logDetails = new StringTokenizer(mail_msg_body, record_sep);
      //Bug Id 75085, end

      mail_msg_body = "";
      int k = 1;
      while (logDetails.hasMoreTokens())
      {
	 //Bug Id 75085, start
	 String single_block = logDetails.nextToken();
	 String field_sep = new String(Character.toChars(31));
	 StringTokenizer message_line = new StringTokenizer(single_block, field_sep);
	 
	 if (message_line.countTokens() > 1) 
	 {
	    while (message_line.hasMoreTokens())
	    {

	       mail_msg_body += message_line.nextToken() + "\\n";

	    }
            mail_msg_body += "\\n";
         }
	 else
	 {
	    mail_msg_body += single_block + "\\n";
	    mail_msg_body += "\\n";
	 }
	 //Bug Id 75085, end
      }
      // Bug Id: 68404, end
      return mail_msg_body;
   }



   public void adjust()
   {
      ASPManager mgr = getASPManager();
      eval(headblk.generateAssignments());

      if (!"TRUE".equals(fromNavigator)) {
         disableOptions();
         disableHomeIcon();
         disableNavigate();
      }
      else
         issuebar.disableCommand(issuebar.FIND);

   }
   


   public void cancel()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }


    public void cancelNewIssue()
    {
       if ("TRUE".equals(fromNavigator))
       {
          deleteIssue();
       }
       else
       {
           issueset.clearRow();
       }
        issuelay.setLayoutMode(this.beforeNewLay);
    }


    private void createNewDocForReport()
   {
      
      ASPManager mgr = getASPManager();

      String  titleForNewDoc = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDNEWTITLE: Transmittal Report");
      String  attr           = "TRANSMITTAL_ID" + String.valueOf((char)31) + transmittalId + String.valueOf((char)30);
              attr          += "TRANSMITTAL_DOCUMENT_TYPE" + String.valueOf((char)31) + "REPORT" + String.valueOf((char)30);

      trans.clear();

      cmd =  trans.addCustomCommand("NEWDOC","DOC_TRANSMITTAL_ISSUE_API.Create_Transmittal_Doc");
      cmd.addParameter("DOC_CLASS");
      cmd.addParameter("DOC_NO");
      cmd.addParameter("DOC_SHEET");
      cmd.addParameter("DOC_REV");
      cmd.addParameter("ATTR",titleForNewDoc); //title
      cmd.addParameter("ATTR",attr);           //attr

      try{
         trans = mgr.performEx(trans);

      }
 	catch(ASPLog.ExtendedAbortException e)
            {
               Buffer info = e.getExtendedInfo();
               try{
                  errorMessage = info.getItem("ERROR_MESSAGE").toString();
                  errorMessage = errorMessage.substring(errorMessage.indexOf("=") + 1);
               }catch(ifs.fnd.buffer.ItemNotFoundException inf)
               {
                  errorMessage = "";
               }
                return;
                
            }



	catch(Exception e)
      {
         errorMessage = mgr.translate("DOCMAWDOCTRANSMITTALWIARDREPORTNOTCREATED: Document for Transmittal Report not created.\\n TRANSMITTAL_REPORT_CLASS in Default Values migh not have a valid value");
         return;
      }

      
      
      reportDocClass = trans.getValue("NEWDOC/DATA/DOC_CLASS");
      reportDocNo    = trans.getValue("NEWDOC/DATA/DOC_NO");
      reportDocSheet = trans.getValue("NEWDOC/DATA/DOC_SHEET");
      reportDocRev   = trans.getValue("NEWDOC/DATA/DOC_REV");
   }


    


    public void deleteIssue()
   { 
       if("TRUE".equals(fromNavigator)) {
          deleteIssueFromRecordSet();
       }
       else
       {
          deleteIssueFromDatabase();
       }
    }


    public void deleteIssueFromDatabase()
	{
		ASPManager mgr = getASPManager();

		issueset.store();
		if (issuelay.isMultirowLayout())
		{
			issueset.setSelectedRowsRemoved();
			issueset.unselectRows();
		}
		else
			issueset.setRemoved();

		mgr.submit(trans);
		//Refresh the headset only if there are items.
		
	}


    public void deleteIssueFromRecordSet()
   {
     //remove the row from the buffer
     ASPBuffer buf = getRowsAsBuffer(issueset);
     if (issuelay.isMultirowLayout())
	  {
		   issueset.storeSelections();
         issueset.first();
         int deletedRows = 0;
         for (int k=0;k<issueset.countRows();k++) {
            if (issueset.isRowSelected()) { //isRowSelected
               removeBufferAt(buf, k-deletedRows);
               deletedRows++;
            }
            issueset.next();
         }
	  }
	  else
     {
        issueset.selectRow();
        int current_row = issueset.getCurrentRowNo();
        removeBufferAt(buf, current_row);
     }
     setBufferAsRows(issueset, buf);
   }


    public  DocTransmittalWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   private void drawButtonPanel(boolean enable_back, String back_event, boolean enable_next, String next_event, boolean endable_finish, String finish_event)
   {
      ASPManager mgr = getASPManager();

      // Draw buttons..
      appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
      appendToHTML("<tr>\n");
      appendToHTML("<td align=\"right\">\n");
      appendToHTML(fmt.drawSubmit("BACK", mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSUBMITBACK: < Back"), enableButton(enable_back) + " " + onClickScript(back_event)));
      appendToHTML("&nbsp;");
      //Bug Id:69301, start
      appendToHTML(fmt.drawButton("NEXT", mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSUBMITNEXT: Next >"), enableButton(enable_next)  + " " + onClickScript(next_event)));
      appendToHTML("&nbsp;");
      appendToHTML(fmt.drawButton("FINISH", mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSUBMITFINISH: Finish"), enableButton(endable_finish)  + " " + onClickScript(finish_event)));
      //Bug Id:69301, end
      appendToHTML("&nbsp;");
      if ("TRUE".equals(fromNavigator)) {
         appendToHTML(fmt.drawSubmit("CANCEL", mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSUBMITCANCEL: Cancel"), enableButton(true)  + " " + onClickScript(null)));
      }
      else
         appendToHTML(fmt.drawButton("CANCEL", mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSUBMITCANCEL: Cancel"), "OnClick=javascript:window.close();"));

      appendToHTML("&nbsp;&nbsp;</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</table>\n");
   }


   private String enableButton(boolean enable)
   {
      if (!enable)
         return "disabled";
      else
         return "";
   }


   public void executeFileOperations() 
   {
      ASPManager mgr = getASPManager();
      fileOperationBuffer = mgr.newASPBuffer();
      checkoutPath = mgr.readValue("CHECKOUT_PATH_HIDDEN");
      
      
      //Bug Id:71759, start
      errorMessage = "";
      //check if all docs are released.
      if ("1".equals(sPublishMode)) {
         trans.clear();
         cmd = trans.addCustomFunction("ALL_RELEASED","DOC_TRANSMITTAL_ISSUE_API.All_Docs_Released","ATTR");
         cmd.addParameter("TRANSMITTAL_ID",transmittalId);
         trans = mgr.perform(trans);
         if ("FALSE".equals(trans.getValue("ALL_RELEASED/DATA/ATTR"))) {
            //Bug Id 75074, start
            updateState = false;
            errorMessage = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDRELALLDOCS: Please 'Release' all the documents attached to transmittal before send.");
            if ("TRUE".equals(fromNavigator)) {
               windowLocation = this.getTransmittalInfoLocation();
               return;
            } else {
               refreshParentAndClose = true;
               return;
            }
            //Bug Id 75074, end
         }
      }
      
      //Bug Id:71759, end
      
      
      
     /*  sPublishMode    operation
         ============   ==========
         "1"            Send Mail
         "2"            Download Files
      */
      
      
      if ("2".equals(sPublishMode)) {
         //file operations
         addDocsToBuffer(checkoutPath);
         if (bZipFiles) {
            add_zipFolder_ToBuffer();
         }
         
         
      }
      
      if ("1".equals(sPublishMode)) {
         createNewDocForReport();
         getReportData();
         if (!mgr.isEmpty(errorMessage)) {
            return;
         }
         adddReportConnectionToTheBuffer();
         addDocsToBuffer(checkoutPath);
         addReportToBuffer();
         //bZipFiles)
         addSendFolderByMailToBuffer();
         
      }
      
      ASPBuffer     buf = mgr.newASPBuffer();
      
      //Bug Id 81807, start
      if (bDownloadOriginalFiles && bDownloadViewFiles) {
         buf.addItem("DOC_TYPE", "ALL");
         buf.addItem("CHECKOUT_TO_GIVEN_PATH", "TRUE");
      } else if (bDownloadOriginalFiles) {
         buf.addItem("DOC_TYPE", "ORIGINAL");
         buf.addItem("CHECKOUT_TO_GIVEN_PATH", "TRUE");
      } else if (bDownloadViewFiles) {
         buf.addItem("DOC_TYPE", "VIEW");
         buf.addItem("CHECKOUT_TO_GIVEN_PATH", "TRUE");
      }
      //Bug Id 81807, end
      
      if (this.fileOperationBuffer == null) {
         return;
      } else
         transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, fileOperationBuffer);
      
      //Bug Id:71759, start
      if (mgr.isEmpty(errorMessage)) {
         executingFileOperation =  true;
      }
      //Bug Id:71759, end
   }



   public void finish()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      int count = 0;
      String current_step = ctx.readValue("CURRENT_STEP");

      boolean bFromNavigator = "TRUE".equals(fromNavigator);

      if ("STEP_1".equals(current_step))
      {
          readStep1();
          //Bug Id: 71844,start
          if (this.bGoBack) {
              try{
                  setNextStep("STEP_1");
                  }catch(FndException e){}
                  finally{
                      return;
                  }
              
          }
          //Bug Id: 71844,end
      }
         
      else if ("STEP_2".equals(current_step))
         readStep2();

      if (bFromNavigator)
      {
         if (!confimationDone)
         {
            // Bug Id 93698, start
            if(!bSaveNewTransDone)
            {
               // save new record
               saveNewTransmittal();
            }
            else
            {
               saveEdit();
            }
            // Bug Id 93698, end
         
            // Bug Id 81808, Start
            if (((!mgr.isEmpty(this.error_message))||(!mgr.isEmpty(this.errorMessage)))&& "STEP_2".equals(current_step) && isProjInstalled)
            {
               mgr.getASPField("PROJECT_ID").deactivateLOV();
               mgr.getASPField("SUB_PROJECT_ID").deactivateLOV();
               mgr.getASPField("ACTIVITY_ID").deactivateLOV();
            }
            if (bTransmittalIdNotCreated) {
               return;
            }
            // Bug Id 81808, End
            saveIssues();
         }
                  
      }
      else
      {
         saveEdit();
      }

      // Bug Id 81808, Start
      if (mgr.isEmpty(this.error_message) && mgr.isEmpty(this.errorMessage))
      {
         // Bug Id: 68412, start
         try
         {   
             //Bug Id: 71844,start
             /*
               we submit trans
                 when !bFromNavigator OR (bFromNavigator AND !this.bTransmittalIdNotCreated)
               take A = !bFromNavigator and B = !this.bTransmittalIdNotCreated 
                 A U (A' ^ B)
               = (A U A') ^ (A U B) 
               = A U B
               ======== : baka
            */
            //Bug Id 81808, Start
             if (mgr.isEmpty(error_message) && (!bFromNavigator || (bFromNavigator && !this.bTransmittalIdNotCreated))){
                trans.clear();
                mgr.submitEx(trans);
             }
             //Bug Id 81808, End
             //Bug Id: 71844,end
                 
         }
         catch(ASPLog.ExtendedAbortException e)
         {
            Buffer info = e.getExtendedInfo();
            try{
               error_message = info.getItem("ERROR_MESSAGE").toString();
               error_message = error_message.substring(error_message.indexOf("=") + 1);
            }catch(ifs.fnd.buffer.ItemNotFoundException inf)
            {
               error_message = "";
            }
               
         }
         // Bug Id: 68412, end
   
         if (mgr.isEmpty(this.error_message)) // Bug Id 93698
         {
            if ("STEP_4".equals(current_step))
            {
               this.readStep4();
            
               // Bug Id 78806, start
               if (checkDocsForNoFile(transmittalId))
               {            
                  // Bug Id: 68412, start
                  // Bug Id: 71844
                  if (mgr.isEmpty(this.error_message) && issueset.countRows()>0 && !mgr.isEmpty(transmittalId)) {
                     // Bug Id: 68412, end
                     //Bug Id 81807, start
                     try
                     {
                        if (!"3".equals(sPublishMode) && transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_out)) 
                        {
                           fileOperationStarted = true;
                        }
                     }
                     catch(FndException e)
                     {
                     }
                     //Bug Id 81807, end
                  }
               }
               else
               {
                  return;
               }
               // Bug Id 78806, end
            
            }
   
            //Bug Id 81807, start
            try
            {
               if (bStep4Visited && !"3".equals(sPublishMode) && transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_out))
               {
                  fileOperationStarted = true;
               }
            }
            catch(FndException e)
            {
            }
            //Bug Id 81807, end
   
            if (!fileOperationStarted)
            {
               if (bFromNavigator)
               {
                  updateState = false;
                  //Bug Id: 68412, start
                  if (!bTransmittalIdNotCreated) { //Bug Id: 71844
               
                     if (mgr.isEmpty(this.error_message)) {
                        gotoTransmittalInfo();
                     }
                  } //Bug Id: 71844,start
                  
                  //Bug Id: 68412, start
   
               }
               else
               {
                  refreshParentAndClose = true;
               }
            }
         }
      }
      // Bug Id 81808, End
   }
   


   public String getDataByName(String name)
   {
      String value = "";
      for (int k=0;k<this.informations.length;k++) {
         if (name.equals(this.informations[k][0])) {
            value = this.informations[k][1];
            break;
         }
      }
      return (value==null)?"":value;
   }


   protected String getDescription()
   {
      return "DOCMAWDOCTRANSMITTALWIZARDTITLE: Document Transmittal Wizard";
   }

   ASPBuffer getIidValuesBuffer(String[] clientValues)
   {
         return this.getIidValuesBuffer(clientValues,clientValues);
   }


   ASPBuffer getIidValuesBuffer(String[] dbValues, String[] clientValues)
      {
         try
         {
            ASPManager mgr = getASPManager();

            ASPBuffer aspbuf = mgr.newASPBuffer();
            
            
            for( int i=0; i< dbValues.length; i++ )
            {
               ASPBuffer row = aspbuf.addBuffer("DATA");
               row.addItem("VALUE",dbValues[i]);
               row.addItem("NAME", clientValues[i]);
               
            }
            return aspbuf;
         }
         catch( Throwable any )
         {
            error(any);
            return null;
         }
     }


   //to support deleteIssue
    private ASPBuffer getRowsAsBuffer(ASPRowSet rowset)
   {
      return rowset.getRows();
   }


    protected String getTitle()
   {
      return "DOCMAWDOCTRANSMITTALWIZARDTITLE: Document Transmittal Wizard";
   }


   private String getTransmittalInformations()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("TRANSINFO","DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Info","ATTR");
      cmd.addParameter("TRANSMITTAL_ID",transmittalId);

      trans = mgr.perform(trans);
      return trans.getValue("TRANSINFO/DATA/ATTR");

   }

   private void getReportData()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("REP_FILENAME","Edm_File_Util_API.Generate_Docman_File_Name_","FILE_NAME");
      cmd.addParameter("DOC_CLASS"    , reportDocClass);
      cmd.addParameter("DOC_NO"       , reportDocNo);
      cmd.addParameter("DOC_SHEET"    , reportDocSheet);
      cmd.addParameter("DOC_REV"      , reportDocRev);
      cmd.addParameter("DOC_REV"      , "ORIGINAL");//DOC_TYPE

      cmd = trans.addCustomFunction("REP_DOCSTATE","Doc_Issue_Api.Get_State","DOC_STATE");
      cmd.addParameter("DOC_CLASS"    , reportDocClass);
      cmd.addParameter("DOC_NO"       , reportDocNo);
      cmd.addParameter("DOC_SHEET"    , reportDocSheet);
      cmd.addParameter("DOC_REV"      , reportDocRev);

      cmd = trans.addCustomFunction("REP_DOCTITLE","Doc_Title_Api.Get_Title","DOC_TITLE");
      cmd.addParameter("DOC_CLASS"    , reportDocClass);
      cmd.addParameter("DOC_NO"       , reportDocNo);


      trans = mgr.perform(trans);
      reportFileNameName = trans.getValue("REP_FILENAME/DATA/FILE_NAME");
      reportDocState     = trans.getValue("REP_DOCSTATE/DATA/DOC_STATE");
      reportDocTitle     = trans.getValue("REP_DOCTITLE/DATA/DOC_TITLE");

   }


   private void gotoTransmittalInfo()
  {
     ASPManager mgr = getASPManager();

     ASPBuffer aspbuf = mgr.newASPBuffer();
     ASPBuffer    row = aspbuf.addBuffer("DATA");
     row.addItem("TRANSMITTAL_ID",transmittalId);

     //Bug Id 81806, start
     if ("TRUE".equals(sFromOtherWindows)) 
     {
        // Bug Id 93694, start
        if (updateState) 
        {
           updateState(transmittalId, "Send"); 
        }
        // Bug Id 93694, end

	transfer_url = "DocTransmittalInfo.page?LOAD_PAGE=TRUE&TRANSMITTAL_ID=" + transmittalId;
        bLoadOnParentWindow = true;
     }
     else
        mgr.transferDataTo("DocTransmittalInfo.page?UPDATE_STATE="+(updateState?"TRUE":"FALSE"),aspbuf);
     //Bug Id 81806, end
  }

  // Bug Id: 68412, start
  private String getTransmittalInfoLocation()
  {
     ASPManager mgr = getASPManager();

     ASPBuffer aspbuf = mgr.newASPBuffer();
     ASPBuffer    row = aspbuf.addBuffer("DATA");
     row.addItem("TRANSMITTAL_ID",transmittalId);

     return mgr.getConfigParameter("APPLICATION/LOCATION/ROOT")+ "docmaw/DocTransmittalInfo.page?UPDATE_STATE="+(updateState?"TRUE":"FALSE")+"&"+mgr.getTransferedQueryString(aspbuf);
  }
  // Bug Id: 68412, end

  public void initWizard()
  {
      ASPManager mgr = getASPManager();

      if (!mgr.isEmpty(mgr.readValue("FROM_NAV")) && "FALSE".equals(mgr.readValue("FROM_NAV"))) {
         setTransmittalInformations();
         okFindIssue();
         fromNavigator = "FALSE";
      }
   }


  public void newRowIssue()
   {
      if ( currLay != issuelay.NEW_LAYOUT)
      {
         beforeNewLay = currLay;
      }

      
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ISSUE","DOC_TRANSMITTAL_ISSUE_API.New__",issueblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANSMITTAL_ID", transmittalId);
      
      trans = mgr.perform(trans);
      data = trans.getBuffer("ISSUE/DATA");
      
      if ("TRUE".equals(fromNavigator)) {
         
         data.setFieldItem("TRANS_EXISTANCE_NO_CHECK","TRUE");

      }
      issueset.addRow(data);
      //eval(issueblk.generateAssignments());
      if ("TRUE".equals(fromNavigator)) //otherwise framework will handle the layout-mode
      {
         issuelay.setLayoutMode(issuelay.NEW_LAYOUT);
      }
   }

  public void newRowIssue(String doc_class,
                          String doc_no,
                          String doc_sheet,
                          String doc_rev,
                          ASPBuffer buff)
   {
      buff.setFieldItem("DOC_CLASS",doc_class);
      buff.setFieldItem("DOC_NO",doc_no);
      buff.setFieldItem("DOC_SHEET",doc_sheet);
      buff.setFieldItem("DOC_REV",doc_rev);


      issueset.addRow(buff);
      
   }


  public void nextStep() throws FndException
   {
      String current_step = ctx.readValue("CURRENT_STEP");

      if ("STEP_1".equals(current_step))
      {
         readStep1();
         //Bug Id: 71844, start
         if (this.bGoBack) {
            setNextStep("STEP_1");
         }
         else{
            setNextStep("STEP_2");
         }
         //Bug Id: 71844, end
          
      }
      else if ("STEP_2".equals(current_step))
      {
        readStep2();
        setNextStep("STEP_3");
      }
      else if ("STEP_3".equals(current_step))
      {
         setNextStep("STEP_4");
      }
      else if ("STEP_4".equals(current_step))
      {
         //readStep4();

      }
      
   }


   public void okFind()
   {
      //Bug Id 81806, start
      ASPManager mgr = getASPManager();

      ASPBuffer transferred_data = mgr.getTransferedData();

      buff = transferred_data.getBufferAt(1);

      String docNo;
      String docClass;
      String docSheet;
      String docRev;

      ASPBuffer buff1;
      sDocAttr = "";

      for (int i=0; i < buff.countItems(); i++)
      {
	 buff1 = buff.getBufferAt(i);
         docClass = buff1.getValueAt(0);
         docNo = buff1.getValueAt(1);
         docSheet = buff1.getValueAt(2);
         docRev = buff1.getValueAt(3);

	 //Attribute string is formed here to pass to connectDocumentsToTrans(). So kept some fields empty.
	 sDocAttr  = sDocAttr + docClass + "^ ^" + docNo + "^ ^" + docSheet + "^" + docRev + "^ ^;";
      }
      fromNavigator = "TRUE";
      sFromOtherWindows = "TRUE";
      this.connectDocumentsToTrans();
      //Bug Id 81806, end
   }



  public void  okFindIssue()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addQuery(issueblk);
      q.addWhereCondition("TRANSMITTAL_ID = ?");
      q.addParameter("TRANSMITTAL_ID",transmittalId);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,issueblk);
   }


  private String onClickScript(String event)
   {
      if (event == null)
         return "";
      else
         return "onClick=javascript:" + event;
   }


  public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();
     
      //Bug Id 84461, Set the length to 120
      headblk.addField("TRANSMITTAL_ID").
      setSize(20).
      setMaxLength(120).
      //setReadOnly(). //Bug Id 77060
      setInsertable().
      setUpperCase().
      setLabel("DOCTRANSMITTALWIZARDTRANSMITTALID: Transmittal Id");
      

      headblk.addField("TRANSMITTAL_DESCRIPTION").
      setSize(20).
      setMaxLength(800).
      setMandatory().
      setLabel("DOCTRANSMITTALWIZARDTRANSDESC: Transmittal Description");

      // Bug Id 81808, Start
      headblk.addField("TRANSMITTAL_INFO").
      setSize(20).
      setMaxLength(100).
      setLabel("DOCTRANSMITTALWIZARDTRANSMITTAL_INFO: Transmittal Info");
      // Bug Id 81808, End

      headblk.addField("TRANSMITTAL_DIRECTION").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("TRANSMITTAL_DIRECTION_API").
      setLabel("DOCTRANSMITTALWIZARDTRANSDIR: Transmittal Direction");


      isProjInstalled = DocmawUtil.isProjInstalled(getASPManager());

      if (isProjInstalled)
      {

          headblk.addField("PROJECT_ID").
          setSize(20).
          setDynamicLOV("PROJECT").
          setLabel("DOCTRANSMITTALWIZARDPROJECTID: Project Id");
    
    
          headblk.addField("SUB_PROJECT_ID").
          setSize(20).
          setDynamicLOV("SUB_PROJECT","PROJECT_ID").
          setLabel("DOCTRANSMITTALWIZARDSUBPROJECTID: Sub Project Id");
    
          
          headblk.addField("ACTIVITY_ID").
          setSize(20).
          setDynamicLOV("ACTIVITY_LOV1","SUB_PROJECT_ID").
          setLabel("DOCTRANSMITTALWIZARDACTIVITYID: Activity Id");
    
      }

      headblk.addField("CONTACT_PERSON").
      setSize(20).
      setDynamicLOV("PERSON_INFO_LOV").
      setUpperCase().
      setLabel("DOCTRANSMITTALWIZARDCONTACTPERSON: Contact Person");


      headblk.addField("EXPECTED_SEND_DATE","Date").
      setSize(20).
      setMandatory(). //Bug Id 76248
      setLabel("DOCTRANSMITTALWIZARDEXPECTEDSENDDATE: Expected Send Date");


      headblk.addField("ACTUAL_SENT_DATE","Date").
      setSize(20).
      setReadOnly(). //Bug Id 77029
      setLabel("DOCTRANSMITTALWIZARDACTUALSENTDATE: actual Sent Date");

      
      headblk.addField("EXPECTED_RETURN_DATE","Date").
      setSize(20).
      setMandatory(). //Bug Id 76248
      setLabel("DOCTRANSMITTALWIZARDEXPECTEDRETURNDATE: Expected Return Date");


      
      headblk.addField("ACTUAL_RETURN_DATE","Date").
      setSize(20).
      setReadOnly(). //Bug Id 77029
      setLabel("DOCTRANSMITTALWIZARDACTUALRETURNDATE: Actual Return Date");


      headblk.addField("RECEIVER_TYPE").
      setSize(20).
      setMandatory().
      setSelectBox().

      enumerateValues("RECEIVER_TYPE_API").
      setLabel("DOCTRANSMITTALWIZARDRECEIVER: Receiver");

      
      headblk.addField("RECEIVER_ADDRESS").
      setSize(20).
      setDynamicLOV("PERSON_INFO_ADDRESS","RECEIVER_CONTACT_PERSON"). // Bug Id 81808
      setLabel("DOCTRANSMITTALWIZARDRECEIVERADDRESS: Receiver Address");

      
      headblk.addField("RECEIVER_CONTACT_PERSON").
      setSize(20).
      setDynamicLOV("PERSON_INFO_PUBLIC_LOV"). // Bug Id 89680
      setUpperCase(). // Bug Id 91571
      setLabel("DOCTRANSMITTALWIZARDRECEIVERCONTACTPERSON: Receiver Contact Person");


      headblk.addField("CUSTOMER_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDCUSTOMERPROJECTID: Customer Project Id");


      headblk.addField("CUSTOMER_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDCUSTOMERSUBPROJECTID: Customer Sub Project Id");

      
      headblk.addField("CUSTOMER_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDCUSTOMERACTIVITYID: Customer Activity Id");


      headblk.addField("SUPPLIER_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUPPLIERPROJECTID: Supplier Project Id");


      headblk.addField("SUPPLIER_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUPPLIERSUBPROJECTID: Supplier Sub Project Id");

      
      headblk.addField("SUPPLIER_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUPPLIERACTIVITYID: Supplier Activity Id");
      
      
      
      headblk.addField("SUB_CONTRACTOR_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORPROJECTID: Sub Contractor Project Id");


      headblk.addField("SUB_CONTRACTOR_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORSUBPROJECTID: Sub Contractor Sub Project Id");

      
      headblk.addField("SUB_CONTRACTOR_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORACTIVITYID: Sub Contractor Activity Id");


      headblk.addField("DISTRIBUTION_METHOD"). 
      setSize(20).
      setMandatory().
      setSelectBox().
      setMandatory().
      enumerateValues("DISTRIBUTION_METHOD_API").
      setLabel("DOCTRANSMITTALWIZARDDISTRIBUTIONMETHOD: Distribution Method");


      headblk.addField("CREATED_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALWIZARDCREATEDDATE: Created Date");

     

      headblk.addField("CREATED_BY").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALWIZARDCREATEDBY: Created By");


      headblk.addField("MODIFIED_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALWIZARDMODIFIED: Modified");

     

      headblk.addField("MODIFIED_BY").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALWIZARDMODIFIEDBY: Modified By");


      headblk.addField("ATTR").
      setFunction("''").
      setHidden();

      headblk.setView("DOCUMENT_TRANSMITTAL");
      headblk.defineCommand("DOCUMENT_TRANSMITTAL_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      
      //tab commands
      headbar.addCustomCommand("activateDocuments", "Documents");
      headbar.addCustomCommand("activateReports", "Reports");
      headbar.addCustomCommand("activateHistory", "History");

      headtbl = mgr.newASPTable(headblk);
      headtbl.enableRowSelect();
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);

      ///// *************** DOC ISSUES *******************
      issueblk = mgr.newASPBlock("ISSUE");

      issueblk.disableDocMan();

      issueblk.addField("ISSUE_OBJID").
      setDbName("OBJID").
      setHidden();

      issueblk.addField("ISSUE_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      issueblk.addField("ISSUE_TRANSMITTAL_ID").
      setDbName("TRANSMITTAL_ID").
      setHidden();

      issueblk.addField("TRANSMITTAL_LINE_NO","Number").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCTRANSMITTALWIZARDTRANSMITTALLINENO: Transmittal Line No");
 
      // Bug Id 92775, start
      issueblk.addField("DOC_TRANSMITTAL_LINE_STATUS").
      setSize(20).
      setMaxLength(6).
      setDynamicLOV("TRANSMITTAL_LINE_STATE").
      setLabel("DOCUMENTTRANSMITTALWIZARDTRANSMITTALLINESTATE: Doc Transmittal Line Status");
      // Bug Id 92775, end
      
      issueblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      //Bug Id: 68412
      setMandatory().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DOC_CLASS","SDOCNAME").
      setLabel("DOCTRANSMITTALWIZARDDOCCLASS: Doc Class");

      issueblk.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCTRANSMITTALWIZARDSDOCNAME: Doc Class Desc");

      issueblk.addField("DOC_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
      //Bug Id: 68412
      setMandatory().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO","DOC_TITLE"). // Bug Id 92775          
      setLabel("DOCTRANSMITTALWIZARDDOCNO: Document Number");

		issueblk.addField("DOC_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
      //Bug Id: 68412
      setMandatory().
		setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO").
		setLabel("DOCTRANSMITTALWIZARDDOCSHEET: Document Sheet");

		issueblk.addField("DOC_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
      //Bug Id: 68412
      setMandatory().
      setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","DOC_STATE,REASON_FOR_ISSUE,REASON_FOR_ISSUE_DESC"). // Bug Id 92775        
      setLabel("DOCTRANSMITTALWIZARDDOCREV: Document Revision");

      // Bug Id 92775, start
      issueblk.addField("DOC_TITLE").
      setSize(60).
      setMaxLength(250).
      setReadOnly().        
      setLabel("DOCTRANSMITTALWIZARDDOCTITLE: Title").        
      setFunction("Doc_Title_Api.Get_Title(:DOC_CLASS,:DOC_NO)");
      

      issueblk.addField("DOC_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().        
      setLabel("DOCTRANSMITTALWIZARDDOCSTATE: Document State").        
      setFunction("Doc_Issue_Api.Get_State(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");
      
      issueblk.addField("REASON_FOR_ISSUE").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFODOCREASONISSUE: Reason For Issue").
      setFunction("Doc_Issue_Api.Get_Reason_For_Issue(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

      issueblk.addField("REASON_FOR_ISSUE_DESC").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFODOCREASONISSUEDESC: Description").
      setFunction("document_reason_for_issue_api.Get_Description(Doc_Issue_Api.Get_Reason_For_Issue(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      // Bug Id 92775, end

      issueblk.addField("CUSTOMER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCTRANSMITTALWIZARDCUSTOMERDOCCLASS: Customer Doc Class");

      issueblk.addField("CUSTOMER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCTRANSMITTALWIZARDCUSTOMERDOCNO: Customer Document Number");

		issueblk.addField("CUSTOMER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO").
		setLabel("DOCTRANSMITTALWIZARDCUSTOMERDOCSHEET: Customer Document Sheet");


      issueblk.addField("FILE_NAME").
      setHidden().
      setFunction("Edm_File_Util_API.Generate_Docman_File_Name_(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");


		issueblk.addField("CUSTOMER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO,CUSTOMER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCTRANSMITTALWIZARDCUSTOMERDOCREV: Customer Document Revision");

      issueblk.addField("SUPPLIER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCTRANSMITTALWIZARDSUPPLIERDOCCLASS: Supplier Doc Class");

      issueblk.addField("SUPPLIER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCTRANSMITTALWIZARDSUPPLIERDOCNO: Supplier Document Number");

		issueblk.addField("SUPPLIER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO").
		setLabel("DOCTRANSMITTALWIZARDSUPPLIERDOCSHEET: Supplier Document Sheet");

		issueblk.addField("SUPPLIER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO,SUPPLIER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCTRANSMITTALWIZARDSUPPLIERDOCREV: Supplier Document Revision");

      issueblk.addField("SUB_CONTRACTOR_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORDOCCLASS: Sub Contractor Doc Class");

      issueblk.addField("SUB_CONTRACTOR_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORDOCNO: Sub Contractor Document Number");

		issueblk.addField("SUB_CONTRACTOR_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO").
		setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORDOCSHEET: Sub Contractor Document Sheet");

		issueblk.addField("SUB_CONTRACTOR_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO,SUB_CONTRACTOR_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCTRANSMITTALWIZARDSUBCONTRACTORDOCREV: Sub Contractor Document Revision");
       
      issueblk.addField("NOTE").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALWIZARDNOTE: Note");

      issueblk.addField("COMMENT_RECEIVED_DATE","Date").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALWIZARDCOMMENTRECEIVEDDATE: Comment Received Date");

      issueblk.addField("COMMENT_RECEIVED").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setLabel("DOCTRANSMITTALWIZARDCOMMENTRECEIVEDID: Comment Received Id");

      /*issueblk.addField("DOC_TRANS_SURVEY_STATUS").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setLabel("DOCTRANSMITTALWIZARDDOCSURVERYSTATUS: Document Transmittal Survey Status");*/

      issueblk.addField("TRANS_EXISTANCE_NO_CHECK").
      setFunction("''").
      setHidden();

     /* issueblk.addField("ADD_COMMAND_VALID").
      setFunction("''").
      setHidden();*/


      issueblk.setView("DOC_TRANSMITTAL_ISSUE"); 
      issueblk.defineCommand("DOC_TRANSMITTAL_ISSUE_API","New__,Modify__,Remove__");
      //issueblk.setMasterBlock(headblk);

      issueset = issueblk.getASPRowSet();

      issuebar = mgr.newASPCommandBar(issueblk);

      issuebar.defineCommand(issuebar.NEWROW,    "newRowIssue");

      issuebar.defineCommand(issuebar.SAVENEW,   "saveNewIssue");
      issuebar.defineCommand(issuebar.SAVERETURN,"saveReturnIssue");
      issuebar.defineCommand(issuebar.DELETE,    "deleteIssue");
      issuebar.defineCommand(issuebar.CANCELNEW, "cancelNewIssue");

      issuebar.addCustomCommand("addDocuments",mgr.translate("DOCMAWDOCTRANSMITTALWIZARDADDDOCS: Add Documents.."));
      issuebar.enableMultirowAction();
      //issuebar.addCommandValidConditions("addDocuments", "ADD_COMMAND_VALID", "Enable", "abcdefgh~aaa"); //we never make this method availble at line(record) levels.
      issuebar.forceEnableMultiActionCommand("addDocuments");


      

      /** remove next line after fixing bug in webclient regarding findAidBtn printed only by show() in ASPBlockLayout 
       *  but not by generateDialog(). In another words web client creates a javascript error in FIND layout  :BAKALK 
      */
      
      
      issuetbl = mgr.newASPTable(issueblk);
      issuetbl.setTitle(mgr.translate("DocumentTransmittalIssuesTITLE: Overview - Document Classes for Transmittals"));
      issuetbl.enableRowSelect();
      issuelay = issueblk.getASPBlockLayout();
      issuelay.setDialogColumns(2);
      issuelay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }


   public void addDocuments()
   {
      ASPManager mgr = getASPManager();
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      bAddExistDoc = true;
      

   }

   //Bug Id:69301, start
   private String writeValidateMethod4Button(String button, String current_step)
   {
       ASPManager mgr = getASPManager();
       String validateString = ""; 
       validateString +=  "function validate"+button+"() {\n";
       if ("STEP_1".equals(current_step)){
          validateString +=  "   if (document.form.TRANSMITTAL_DESCRIPTION.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDDESCMANDATORY: Required value for field [TRANSMITTAL DESCRIPTION]")+"');\n";
          validateString +=  "   else if (document.form.TRANSMITTAL_DIRECTION.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDDIRECMANDATORY: Required value for field [TRANSMITTAL DIRECTION]")+"');\n";
          validateString +=  "   else if (document.form.RECEIVER_TYPE.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDRECEIVTYPEMANDATORY: Required value for field [RECEIVER TYPE]")+"');\n";
          validateString +=  "   else if (document.form.DISTRIBUTION_METHOD.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDDISTMETHODMANDATORY: Required value for field [DISTRIBUTION METHOD]")+"');\n";
	  //Bug Id 76248, start
	  validateString +=  "   else if (document.form.EXPECTED_SEND_DATE.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSENDDATEMANDATORY: Required value for field [EXPECTED SEND/RECEIVE DATE]")+"');\n";
	  validateString +=  "   else if (document.form.EXPECTED_RETURN_DATE.value == '')\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARRETURNDATEMANDATORY: Required value for field [EXPECTED LATEST COMMENT DATE]")+"');\n"; //Bug Id 79174
          //Bug Id 76248, end
          validateString +=  "   else \n";
       }
       else if ("STEP_3".equals(current_step) && "Next".equals(button)) {
          validateString +=  "   if ("+!(issueset.countRows()>0)+")\n";
          validateString +=  "      alert('"+mgr.translate("DOCMAWDOCTRANSMITTALWIZARDNODOCCONN: No Documents have been connected to Transmittal")+"');\n";
          validateString +=  "   else \n";
       }
       validateString +=  "    {\n";
       validateString +=  "       document.form."+button.toUpperCase()+"_OK.value = 'TRUE';\n";
       validateString +=  "       submit(form);\n";
       validateString +=  "    }\n";
       validateString +=  "}\n";
       return   validateString;
   }
   //Bug Id:69301, end



  public void previousStep() throws FndException
  {
      String current_step = ctx.readValue("CURRENT_STEP");

      

      if ("STEP_1".equals(current_step))
      {
         // No previous step..
      }
      else if ("STEP_2".equals(current_step))
      {
         // Save selections made by the user..
         readStep2();
         setPreviousStep("STEP_1");
      }
      else if ("STEP_3".equals(current_step))
      {
         setPreviousStep("STEP_2");
               
      }
      else if ("STEP_4".equals(current_step))
      {
         readStep4();
         setPreviousStep("STEP_3");         
      }
      
  }



  protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      String current_step = ctx.readValue("CURRENT_STEP","STEP_1");

      setTitle(current_step);
      if ("STEP_3".equals(current_step))
         appendToHTML(issuebar.showBar());
      else
         appendToHTML(headbar.showBar());

      //Bug Id:69301, start
      appendToHTML("<input type=\"hidden\" name=\"NEXT_OK\" value=\"FALSE\">");
      appendToHTML("<input type=\"hidden\" name=\"FINISH_OK\" value=\"FALSE\">");
      //Bug Id:69301, end
      //Bug Id 72460, start
      appendToHTML("<input type=\"hidden\" name=\"UPDATE_WIZARD_STATE\" value=\"TRUE\">\n");
      //Bug Id 72460, end

      //Bug Id 81808, Start
      appendToHTML("<input type=\"hidden\" name=\"TRANSMITTAL_SELECTED\" value=\"FALSE\">\n");
      appendToHTML("<input type=\"hidden\" name=\"PROJECT_RECEIVER_EXIST\" value=\"FALSE\">\n");
      //Bug Id 81808, End
   
      if ("STEP_1".equals(current_step))
      {
         
         beginDataPresentation();
         if(!"TRUE".equals(fromNavigator))// then no textfiel shown, insted label shown
         {
            appendToHTML("<input type=\"hidden\" name=\"TRANSMITTAL_ID\" value=\""+ mgr.HTMLEncode(transmittalId) +"\">");
	    appendToHTML("<input type=\"hidden\" name=\"TRANSMITTAL_DIRECTION\" value=\""+ mgr.HTMLEncode(transmittalDirection) +"\">"); //Bug Id 81807
         }
         
         headset.addRow(mgr.newASPBuffer());//just to take the tag
         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         //Bug Id 81808, Start
         if (isProjInstalled)
            appendToHTML("<tr><td height=\"260\" rowspan=\"4\" width=\"135\"><img height=\"100%\" src=\"images/ReleaseDocument.jpg\"></td>\n");
         else
            appendToHTML("<tr><td height=\"260\" rowspan=\"3\" width=\"135\"><img height=\"100%\" src=\"images/ReleaseDocument.jpg\"></td>\n");
         //Bug Id 81808, End

         appendToHTML("<td valign=\"top\" height=\"100\" >\n");
         //generateDataPresentation
     // group box Transmittal
         
         appendToHTML("&nbsp;<fieldset  style=\" height: 85px\">\n"); //width: 170px;
         appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDTRANSMITTALGROUP: Transmittal"))+"</legend>\n");

         //Bug Id 81808  Start
         appendToHTML("    <table border=\"0\" width=\"850\">\n");
         appendToHTML("     <tr   >\n");
         appendToHTML("      <td>");
         appendToHTML("       <table border=\"0\" width=\"415\">\n");
         appendToHTML("        <tr   >\n");
         appendToHTML("         <td width=\"215\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDTRANSMITTALID: Transmittal Id"));
         appendToHTML("         </td>\n");
         appendToHTML("         <td width=\"200\">");
         if("TRUE".equals(fromNavigator)) {
            appendToHTML(fmt.drawTextField("TRANSMITTAL_ID",transmittalId,mgr.getASPField("TRANSMITTAL_ID").getTag()));//+"&nbsp;*");
         }
         else
         {
            appendToHTML(fmt.drawReadLabel(mgr.HTMLEncode(transmittalId))); //Bug Id 91417
         }            
         appendToHTML("         </td>\n");
         appendToHTML("        </tr> \n");

         appendToHTML("        <tr   >\n");         
         appendToHTML("         <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDTRANSDIR: Transmittal Direction"));
         appendToHTML("         </td>\n");
         appendToHTML("         <td width=\"200\">");
         //Bug Id 81807, start
         if("TRUE".equals(fromNavigator)) 
	 {
            appendToHTML(fmt.drawSelect("TRANSMITTAL_DIRECTION",getIidValuesBuffer(mgr.getASPField("TRANSMITTAL_DIRECTION").getIidClientValues()),transmittalDirection,mgr.getASPField("TRANSMITTAL_DIRECTION").getTag())+"&nbsp;*");
         }
         else
         {
            appendToHTML(fmt.drawReadLabel(mgr.encodeStringForJavascript(transmittalDirection)));
         }
         //Bug Id 81807, end
         appendToHTML("         </td>\n");
         appendToHTML("        </tr> \n"); 

         appendToHTML("        <tr   >\n");         
         appendToHTML("         <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDCONTACTPERSON: Contact Person"));
         appendToHTML("         </td>\n");
         appendToHTML("         <td width=\"200\">");
         appendToHTML(fmt.drawTextField("CONTACT_PERSON",contactPerson,mgr.getASPField("CONTACT_PERSON").getTag()));
         appendToHTML("         </td>\n");
         appendToHTML("        </tr> \n");
         appendToHTML("       </table>\n");
         appendToHTML("      </td>\n");
         appendToHTML("      <td>");
         appendToHTML("       <table border=\"0\" width=\"435\">\n");
         appendToHTML("        <tr>\n");
         appendToHTML("         <td width=\"200\">");
         appendToHTML("          <table border=\"0\" width=\"200\">\n");
         appendToHTML("           <tr>\n");
         appendToHTML("            <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDTRANSDESC: Transmittal Description"));
         appendToHTML("            </td>\n");
         appendToHTML("           </tr> \n");
         appendToHTML("          </table>\n");
         appendToHTML("         </td>\n");
         appendToHTML("         <td width=\"200\">");
         appendToHTML(fmt.drawTextField("TRANSMITTAL_DESCRIPTION",mgr.HTMLEncode(transmittalDesc),mgr.getASPField("TRANSMITTAL_DESCRIPTION").getTag())+"&nbsp;*"); // Bug Id 91417
         appendToHTML("         </td>\n");
         appendToHTML("        </tr> \n");
         appendToHTML("        <tr>\n");
         appendToHTML("         <td width=\"200\">");
         appendToHTML("          <table border=\"0\">\n");
         appendToHTML("           <tr   >\n");
         appendToHTML("            <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDTRANSMITTAL_INFO: Transmittal Info"));
         appendToHTML("            </td>\n");
         appendToHTML("           </tr> \n"); 
         appendToHTML("           <tr   >\n");
         appendToHTML("            <td width=\"200\">");
         appendToHTML("             &nbsp");
         appendToHTML("            </td>\n");
         appendToHTML("           </tr> \n");
         appendToHTML("          </table>\n");
         appendToHTML("         </td>\n");
         appendToHTML("         <td width=\"200\">");
         appendToHTML(fmt.drawTextArea("TRANSMITTAL_INFO",transmittalInfo,mgr.getASPField("TRANSMITTAL_INFO").getTag()+" onkeypress=\"return checkMaxLength(this);\" onkeyup=\"return checkMaxLength(this);\"",3,19));
         appendToHTML("         </td>\n");
         appendToHTML("        </tr> \n"); 
         appendToHTML("       </table>\n");
         appendToHTML("      </td>\n");
         appendToHTML("     </tr> \n");
         appendToHTML("    </table>\n");

         appendToHTML("</fieldset>\n");

         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td valign=\"middle\" height=\"25\">\n");
         
         // group box Project Info
         
         appendToHTML("&nbsp;<fieldset  style=\" height: 45px\">\n");
         appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDPROJECTGROUP: Project Info "))+"</legend>\n");

         appendToHTML("    <table border=\"0\" width=\"850\">\n");
         appendToHTML("    <tr>\n");
         appendToHTML("    <td>");
         appendToHTML("    <table border=\"0\" width=\"850\">\n");

	 appendToHTML("     <tr   >\n");

         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDPROJECTID: Project Id"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("PROJECT_ID",projectId,mgr.getASPField("PROJECT_ID").getTag()));
         appendToHTML("</td>\n");
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUBPROJECTID: Sub Project Id"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("SUB_PROJECT_ID",subProjectId,mgr.getASPField("SUB_PROJECT_ID").getTag()));
         appendToHTML("</td>\n");

         appendToHTML("   </tr> \n"); 

         appendToHTML("   <tr   >\n"); // row 2
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDACTIVITYID: Activity Id"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("ACTIVITY_ID",activityId,mgr.getASPField("ACTIVITY_ID").getTag()));
         appendToHTML("</td>\n");
         
         appendToHTML("</td>\n");

         appendToHTML("   </tr> \n"); 

         appendToHTML("    </table>\n");
         appendToHTML("    </td>");
         appendToHTML("    </tr>\n");         
         appendToHTML("    </table>\n");

         appendToHTML("</fieldset>\n");

         appendToHTML("</td></tr>\n");

         appendToHTML("<tr><td valign=\"middle\" height=\"25\">\n");  
    
         // group box Reciever

         appendToHTML("&nbsp;<fieldset  style=\"height: 45px\">\n"); //width: 170px; 
         appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDRECIEVERGROUP: Reciever"))+"</legend>\n");

         appendToHTML("    <table border=\"0\" width=\"850\">\n");
         appendToHTML("    <tr>\n");
         appendToHTML("    <td>");
         appendToHTML("    <table border=\"0\" width=\"850\">\n");

         appendToHTML("   <tr   >\n"); // row 1
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDRECEIVERTYPE: Receiver Type"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawSelect("RECEIVER_TYPE",getIidValuesBuffer(mgr.getASPField("RECEIVER_TYPE").getIidClientValues()),receiverType,mgr.getASPField("RECEIVER_TYPE").getTag())+"&nbsp;*");//mgr.getASPField("RECEIVER_TYPE").getIidDbValues(),
         appendToHTML("</td>\n");
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDRECEIVERID: Receiver Id"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("RECEIVER_CONTACT_PERSON",receiverContactPerson,mgr.getASPField("RECEIVER_CONTACT_PERSON").getTag()));
         appendToHTML("</td>\n");

         appendToHTML("   </tr> \n"); //----

         appendToHTML("   <tr   >\n"); // row 2
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDDISTRIBUTIONMETHOD: Distribution Method"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawSelect("DISTRIBUTION_METHOD",getIidValuesBuffer(mgr.getASPField("DISTRIBUTION_METHOD").getIidClientValues()),distributionMethod,mgr.getASPField("DISTRIBUTION_METHOD").getTag())+"&nbsp;*");//mgr.getASPField("DISTRIBUTION_METHOD").getIidDbValues(),
         appendToHTML("</td>\n");
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDRECEIVERADDRESSID: Address Id"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("RECEIVER_ADDRESS",receiverAddress,mgr.getASPField("RECEIVER_ADDRESS").getTag()));
         appendToHTML("</td>\n");

         appendToHTML("   </tr> \n"); //----

         appendToHTML("    </table>\n");
         appendToHTML("    </td>");
         appendToHTML("    </tr>\n");
         appendToHTML("    </table>\n");
         appendToHTML("</fieldset>\n");

         appendToHTML("</td></tr>\n");


          appendToHTML("<tr><td valign=\"bottom\" height=\"25\">\n");

          // date >>>> group box group box group box group box group box group box group box group box >>>>>>>

         appendToHTML("&nbsp;<fieldset  style=\"height: 45px\">\n"); //width: 170px; 
         appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDDATEGROUP: Date"))+"</legend>\n");
         appendToHTML("    <table border=\"0\" width=\"850\">\n");
         appendToHTML("    <tr>\n");
         appendToHTML("    <td>");
         appendToHTML("    <table border=\"0\" width=\"850\">\n");

	 appendToHTML("   <tr   >\n"); // row 1
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDEXPECTEDSENDDATE: Expected Send Date"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("EXPECTED_SEND_DATE",expectedSendDate,mgr.getASPField("EXPECTED_SEND_DATE").getTag())+"&nbsp;*"); //Bug Id 76248
         appendToHTML("</td>\n");
         
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDEXPECTEDRETURNDATE: Expected Return Date"));
         appendToHTML("</td>\n");
         appendToHTML("      <td width=\"200\">");
         appendToHTML(fmt.drawTextField("EXPECTED_RETURN_DATE",expectedReturnDate,mgr.getASPField("EXPECTED_RETURN_DATE").getTag())+"&nbsp;*"); //Bug Id 76248
         appendToHTML("</td>\n");
         
         appendToHTML("   </tr> \n"); //----

         appendToHTML("    </table>\n");
         appendToHTML("    </td>");
         appendToHTML("    </tr>\n");
         appendToHTML("    </table>\n");//
         appendToHTML("</fieldset>\n");
          

         appendToHTML("</td></tr>\n");

         appendToHTML("</table>\n");
         //Bug Id 81808 End


         endDataPresentation(false);
         // Draw control buttons..

         //Bug Id:69301, start
         drawButtonPanel(false, null, true, nextButtonEvent, true, finishButtonEvent);
         //Bug Id:69301, end
         headset.clear();

      // Bug Id 81808 Start
      appendDirtyJavaScript("function lovReceiverContactPerson(i, params)\n"); 
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var project_id_ = getValue_('PROJECT_ID',i);\n");
      appendDirtyJavaScript("   last_value =  getValue_('RECEIVER_CONTACT_PERSON',i);\n");
      appendDirtyJavaScript("   var r = __connect('" + mgr.getURL() + "?VALIDATE=PROJECT_ID'+'&PROJECT_ID=' + URLClientEncode(project_id_));\n");
      appendDirtyJavaScript("	assignValue_('PROJECT_RECEIVER_EXIST',i,0);\n");
      appendDirtyJavaScript("   var project_receiver_exist_ = document.form.PROJECT_RECEIVER_EXIST.value;\n");
      appendDirtyJavaScript("   if (project_id_ == \"\" || project_id_ == null || project_receiver_exist_ != \"TRUE\") \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('RECEIVER_CONTACT_PERSON',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PERSON_INFO_PUBLIC_LOV&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALWIZARDRECEIVERID: Receiver Id"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n"); // Bug Id 89680
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("      + '&RECEIVER_CONTACT_PERSON=' + URLClientEncode(last_value)\n");
      appendDirtyJavaScript("      ,550,500,'ReceiverContactPerson');\n"); 
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('RECEIVER_CONTACT_PERSON',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PROJ_TRANSMITTAL_RECEIVER_LOV&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALWIZARDRECEIVERID: Receiver Id"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("      + '&RECEIVER_CONTACT_PERSON=' + URLClientEncode(last_value)\n");
      appendDirtyJavaScript("      + '&PROJECT_ID=' + URLClientEncode(getValue_('PROJECT_ID',i))\n");
      appendDirtyJavaScript("      ,550,500,'ReceiverContactPerson');\n"); 
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");   

      appendDirtyJavaScript("function ReceiverContactPerson() {\n");
      appendDirtyJavaScript("   document.form.TRANSMITTAL_SELECTED.value = 'TRUE';");
      appendDirtyJavaScript("}\n");
      //Bug Id 81808 End

      // Bug Id 85164, Start
      appendDirtyJavaScript("function lovReceiverAddress(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   openLOVWindow('RECEIVER_ADDRESS',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PERSON_INFO_ADDRESS&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALWIZARDRECEIVERADDRESS: Receiver Address"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_ADDRESS',i))\n");
      appendDirtyJavaScript("     + '&PERSON_ID=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateReceiverAddress');\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 85164, End
    
      }
      else if ("STEP_2".equals(current_step))
      {
        beginDataPresentation();
        headset.addRow(mgr.newASPBuffer());//just to take the tag
        appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
        appendToHTML("<tr><td height=\"260\" rowspan=\"3\" width=\"135\"><img height=\"100%\" src=\"images/ReleaseDocument.jpg\"></td>\n");

        appendToHTML("<tr><td valign=\"top\" height=\"100\">\n");

   // Project >>>> group box group box group box group box group box group box group box group box >>>>>>>

        appendToHTML("&nbsp;<fieldset  style=\"height: 262px;\">\n"); //height: 95px width: 170px; 
        appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDDATEPROJECT: Project"))+"</legend>\n");
        appendToHTML("    <table border=\"0\" >\n");

        if (isProjInstalled)
        {

        appendToHTML("   <tr   >\n"); // row 1       // Bug Id 81808 - set the text fields read only


            appendToHTML("<td width=\"200\">");
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDPROJECTID: Project Id"));
            appendToHTML("</td>\n");
            appendToHTML("      <td width=\"200\">");
            appendToHTML(fmt.drawReadOnlyTextField("PROJECT_ID",projectId,mgr.getASPField("PROJECT_ID").getTag()));
            appendToHTML("</td>\n");
    
            appendToHTML("<td width=\"200\">");
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUBPROJECTID: Sub Project Id"));
            appendToHTML("</td>\n");
            appendToHTML("      <td width=\"200\">");
            appendToHTML(fmt.drawReadOnlyTextField("SUB_PROJECT_ID",subProjectId,mgr.getASPField("SUB_PROJECT_ID").getTag()));
            appendToHTML("</td>\n");
    
            appendToHTML("<td width=\"200\">");
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDACTIVITYID: Activity Id"));
            appendToHTML("</td>\n");
            appendToHTML("      <td width=\"200\">");
            appendToHTML(fmt.drawReadOnlyTextField("ACTIVITY_ID",activityId,mgr.getASPField("ACTIVITY_ID").getTag()));
            appendToHTML("</td>\n");
            appendToHTML("   <tr   ></tr><tr   ></tr><tr   ></tr><tr   ></tr>\n");
     

        appendToHTML("   </tr> \n");
        
        }
        //----

        appendToHTML("   <tr   >\n"); // row2
        

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDCUSTOMERPROJECTID: Customer Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("    <td width=\"200\">");
        appendToHTML(fmt.drawTextField("CUSTOMER_PROJECT_ID",customerProjectId,mgr.getASPField("CUSTOMER_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDCUSTOMERSUBPROJECTID: Customer Sub Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("    <td width=\"200\">");
        appendToHTML(fmt.drawTextField("CUSTOMER_SUB_PROJECT_ID",customerSsubProjectId,mgr.getASPField("CUSTOMER_SUB_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDCUSTOMERACTIVITYID: Customer Activity Id"));
        appendToHTML("</td>\n");
        appendToHTML("    <td width=\"200\">");
        appendToHTML(fmt.drawTextField("CUSTOMER_ACTIVITY_ID",customerActivityId,mgr.getASPField("CUSTOMER_ACTIVITY_ID").getTag()));
        appendToHTML("</td>\n");
        appendToHTML("   <tr   ></tr><tr   ></tr><tr   ></tr><tr   ></tr>\n");

        appendToHTML("   </tr> \n"); //----
      

        appendToHTML("   <tr   >\n"); // row 3
         
        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUPPLIERPROJECTID: Supplier Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUPPLIER_PROJECT_ID",supplierProjectId,mgr.getASPField("SUPPLIER_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUPPLIERSUBPROJECTID: Supplier Sub Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUPPLIER_SUB_PROJECT_ID",supplierSubProjectId,mgr.getASPField("SUPPLIER_SUB_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUPPLIERACTIVITYID: Supplier Activity Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUPPLIER_ACTIVITY_ID",supplierActivityId,mgr.getASPField("SUPPLIER_ACTIVITY_ID").getTag()));
        appendToHTML("</td>\n");
        appendToHTML("   <tr   ></tr><tr   ></tr><tr   ></tr><tr   ></tr>\n");

        appendToHTML("   </tr> \n"); //----

        // Bug Id 81808 Start
        appendToHTML("   <tr   >\n"); // row 3
         
        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUBCONTRACTPROJECTID: Subcontractor Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUBCONT_PROJECT_ID",subContractProjectId,mgr.getASPField("SUB_CONTRACTOR_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUBCONTRACTSUBPROJECTID: Subcontractor Sub Project Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUBCONT_SUB_PROJECT_ID",subContractSubProjectId,mgr.getASPField("SUB_CONTRACTOR_SUB_PROJECT_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("<td width=\"200\">");
        appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDSUBCONTRACTACTIVITYID: Subcontractor Activity Id"));
        appendToHTML("</td>\n");
        appendToHTML("      <td width=\"200\">");
        appendToHTML(fmt.drawTextField("SUBCONT_ACTIVITY_ID",subContractActivityId,mgr.getASPField("SUB_CONTRACTOR_ACTIVITY_ID").getTag()));
        appendToHTML("</td>\n");

        appendToHTML("   </tr> \n"); //----
        // Bug Id 81808 End

        appendToHTML("    </table>\n");//
        appendToHTML("</fieldset>\n");


        appendToHTML("</td></tr>\n");

        appendToHTML("</table>\n");



        endDataPresentation(false);
        // Draw control buttons..
        //Bug Id:69301, start
        drawButtonPanel(true, null, true, nextButtonEvent, true, finishButtonEvent);
        //Bug Id:69301, end
        headset.clear();

         //&&&&&&&&&&&&&&&&&&
        
      }
      else if ("STEP_3".equals(current_step))
      {
         
         appendToHTML("<input type=\"hidden\" name=\"DOCUMENT_SELECTED\" value=\"FALSE\">");
         appendToHTML("<input type=\"hidden\" name=\"DOCUMENT_REV_SELECTED\" value=\"\">");
         beginDataPresentation();
         issueblk.generateHiddenFields();
        //issueset.addRow(mgr.newASPBuffer());//just to take the tag
        appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
        appendToHTML("<tr><td height=\"260\" rowspan=\"3\" width=\"135\"><img height=\"100%\" src=\"images/ReleaseDocument.jpg\"></td>\n");

        appendToHTML("<tr><td valign=\"top\" height=\"100\">\n");

        appendToHTML(issuelay.generateDataPresentation());



        appendToHTML("</td></tr>\n");

        appendToHTML("</table>\n");



        endDataPresentation(false);
        // Draw control buttons..
        if(issuelay.isMultirowLayout() ||  issuelay.isSingleLayout()){
            //Bug Id:69301, start
            drawButtonPanel(true, null, true, nextButtonEvent, true, finishButtonEvent);
            //Bug Id:69301, end
        }
        
        //Bug Id 92775, start
        if(isProjInstalled)
        {
           appendDirtyJavaScript("function lovDocTransmittalLineStatus(i, params)\n"); 
           appendDirtyJavaScript("{\n");
           appendDirtyJavaScript("   if(params) param = params;\n");         
           appendDirtyJavaScript("   else param = '';\n");
           appendDirtyJavaScript("   var enable_multichoice;\n");
           appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
           appendDirtyJavaScript("   if('"+projectId+"'==\"\" || '"+projectId+"'==null)\n");
           appendDirtyJavaScript("      openLOVWindow('DOC_TRANSMITTAL_LINE_STATUS',i,\n");
           appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=TRANSMITTAL_LINE_STATE&__FIELD="+mgr.URLEncode(mgr.translate("DOCUMENTTRANSMITTALWIZARDTRANSMITTALLINESTATE: Doc Transmittal Line Status"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
           appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_TRANSMITTAL_LINE_STATUS',i))\n");
           appendDirtyJavaScript("      + '&PROJECT_ID=' + URLClientEncode('-')\n");
           appendDirtyJavaScript("      ,550,500,'validateDocTransmittalLineStatus');\n"); 
           appendDirtyJavaScript("   else \n");
           appendDirtyJavaScript("      openLOVWindow('DOC_TRANSMITTAL_LINE_STATUS',i,\n");
           appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=TRANSMITTAL_LINE_STATE&__FIELD="+mgr.URLEncode(mgr.translate("DOCUMENTTRANSMITTALWIZARDTRANSMITTALLINESTATE: Doc Transmittal Line Status"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
           appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('DOC_TRANSMITTAL_LINE_STATUS',i))\n");
           appendDirtyJavaScript("      + '&PROJECT_ID=' + URLClientEncode('"+projectId+"')\n");
           appendDirtyJavaScript("      ,550,500,'validateDocTransmittalLineStatus');\n"); 
           appendDirtyJavaScript("}\n");
        }
        // Bug Id 92775, end

      }
      else if ("STEP_4".equals(current_step))
      {
         beginDataPresentation();
         
         appendToHTML("<input type=\"hidden\" name=\"ZIPFILES_HIDDEN\" value=\""+(bZipFiles?"TRUE":"FALSE")+"\">");
         //Bug Id 81807, start
         appendToHTML("<input type=\"hidden\" name=\"DOWNLOAD_ORIGINAL_HIDDEN\" value=\""+(bDownloadOriginalFiles?"TRUE":"FALSE")+"\">");
         appendToHTML("<input type=\"hidden\" name=\"DOWNLOAD_VIEW_HIDDEN\" value=\""+(bDownloadViewFiles?"TRUE":"FALSE")+"\">");
         appendToHTML("<input type=\"hidden\" name=\"OPEN_DOWNLOAD_FOLDER_HIDDEN\" value=\""+(bOpenDownloadFolder?"TRUE":"FALSE")+"\">");
         //Bug Id 81807, end
         
         // Bug Id 78806, start
         appendToHTML("  <input type=\"hidden\" name=\"CONFIRMACTION\" value=\"\">\n");
         // Bug Id 78806, end

         appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         appendToHTML("<tr><td height=\"260\" rowspan=\"3\" width=\"135\"><img height=\"100%\" src=\"images/ReleaseDocument.jpg\"></td>\n");

         appendToHTML("<tr><td valign=\"top\" height=\"100\">\n");

  // Send Transmittal >>>> group box group box group box group box group box group box group box group box >>>>>>>

         //Bug Id 81807, start
         if (transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_in)) 
         {
            appendToHTML("<fieldset  style=\"width: 100%; height: 262px\">\n"); 
            appendToHTML("   <p>&nbsp;</p>\n");
            appendToHTML("   <table border=\"0\" width=\"100%\" >\n");
            appendToHTML("   <tr   >\n");
            appendToHTML("      <td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            appendToHTML("      <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDFINISHLABEL: Click Finish to create the transmittal."))+"</legend>\n");
            appendToHTML("      </td >");
            appendToHTML("   </tr>\n");
            appendToHTML("   </table>\n");//
            appendToHTML("</fieldset>\n");
         }
         else
         {
         //Bug Id 81807, end
            appendToHTML("<fieldset  style=\"width: 100%; height: 262px\">\n"); //height: 95px width: 170px; style="width: 886px; height: 262px
            appendToHTML(" <legend>" +fmt.drawReadLabel( mgr.translate("DOCTRANSMITTALWIZARDDATESENDTRANSMITTAL: Send/Download Transmittal"))+"</legend>\n");//Bug Id 79174
            appendToHTML("    <table border=\"0\" width=\"100%\" >\n");

            appendToHTML("   <tr   >\n"); // row 1

            appendToHTML("      <td >");//width=\"20\"
            //Bug Id: 71838, start
            if (transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_in)) {
               appendToHTML(fmt.drawRadio(mgr.translate("DOCTRANSMITTALWIZARDSENDEMAIL: Send E-Mail and Transmittal Report"), "PUBLISH_MODE", "1", false, "DISABLED"));
            }
            else
               appendToHTML(fmt.drawRadio(mgr.translate("DOCTRANSMITTALWIZARDSENDEMAIL: Send E-Mail and Transmittal Report"), "PUBLISH_MODE", "1", "1".equals(sPublishMode), "onClick=\"handleCheckBoxes(this)\"")); //Bug Id 81807
            //Bug Id: 71838, end
            appendToHTML("</td>\n");
            /*appendToHTML("      <td >");
            appendToHTML("</td>\n");*/
       
            appendToHTML("   </tr> \n"); //----

            appendToHTML("   <tr   >\n"); // row2

            appendToHTML("      <td >");//width=\"20\"
            //appendToHTML(fmt.drawCheckbox("PUBLISH_TRANS","ON",bPublishTrans,"onClick=\"setCheckBoxValues('BPUBLISHTRANS_HIDDEN',this)\""));
            //Bug Id: 71838, start
            if (transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_in)) {
               appendToHTML(fmt.drawRadio(mgr.translate("DOCTRANSMITTALWIZARDPUBLISHANDDOWNLOAD: Publish/Download Document Transmittal Files"), "PUBLISH_MODE", "2", true, ""));
            }
            else
               appendToHTML(fmt.drawRadio(mgr.translate("DOCTRANSMITTALWIZARDPUBLISHANDDOWNLOAD: Publish/Download Document Transmittal Files"), "PUBLISH_MODE", "2", "2".equals(sPublishMode), "onClick=\"handleCheckBoxes(this)\"")); //Bug Id 81807
            //Bug Id: 71838, end
         
            appendToHTML("</td>\n");
            /*appendToHTML("      <td >");
            appendToHTML(fmt.drawWriteLabel(""));
            appendToHTML("</td>\n");*/

            appendToHTML("   </tr> \n"); //----

            //Bug Id 81807, start
            appendToHTML("   <tr   >\n"); 
            appendToHTML("      <td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            appendToHTML(fmt.drawCheckbox("OPEN_DOWNLOAD_FOLDER","ON",bOpenDownloadFolder,"onClick=\"setCheckBoxValues('OPEN_DOWNLOAD_FOLDER_HIDDEN',this)\" DISABLED")); 
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDOPENFOLDER: Open folder after download"));
            appendToHTML("</td>\n");
            appendToHTML("   </tr> \n"); 

            appendToHTML("   <tr   >\n"); 
            appendToHTML("      <td >");
            appendToHTML(fmt.drawRadio(mgr.translate("DOCTRANSMITTALWIZARDFINISH: Finish"), "PUBLISH_MODE", "3", "3".equals(sPublishMode), "onClick=\"handleCheckBoxes(this)\""));
            appendToHTML("</td>\n");
            appendToHTML("   </tr> \n"); 

            appendToHTML("   <tr   >\n"); 
            appendToHTML("      <td >");
            appendToHTML("</td>\n");
            appendToHTML("   </tr> \n"); 
            //Bug Id 81807, end

            appendToHTML("   <tr   >\n"); // row3

            appendToHTML("      <td >");
            appendToHTML(fmt.drawCheckbox("ZIP_FILES","ON",bZipFiles,"onClick=\"setCheckBoxValues('ZIPFILES_HIDDEN',this)\""));
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDZIPFILES: Zip Files"));
            appendToHTML("</td>\n");
            /*appendToHTML("      <td >");
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDZIPFILES: Zip Files"));
            appendToHTML("</td>\n");*/

            appendToHTML("   </tr> \n"); //----
	 
            //Bug Id 81807, start
            appendToHTML("   <tr   >\n"); 
            appendToHTML("      <td >");
            appendToHTML(fmt.drawCheckbox("DOWNLOAD_ORIGINALS","ON",bDownloadOriginalFiles,"onClick=\"setCheckBoxValues('DOWNLOAD_ORIGINAL_HIDDEN',this)\""));
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDDOWNLOADORIGINALS: Send/Download Original Files"));
            appendToHTML("</td>\n");
            appendToHTML("   </tr> \n"); 
	    
            appendToHTML("   <tr   >\n"); 
            appendToHTML("      <td >");
            appendToHTML(fmt.drawCheckbox("DOWNLOAD_VIEWS","ON",bDownloadViewFiles,"onClick=\"setCheckBoxValues('DOWNLOAD_VIEW_HIDDEN',this)\""));
            appendToHTML(fmt.drawWriteLabel("DOCTRANSMITTALWIZARDDOWNLOADVIEWS: Send/Download View Files"));
            appendToHTML("</td>\n");
            appendToHTML("   </tr> \n");
            //Bug Id 81807, end

            appendToHTML("    </table>\n");//
            appendToHTML("</fieldset>\n"); 
            
            // Bug Id 78806, start
            if (showConfirmation)
            {         
               appendDirtyJavaScript(" if (confirm('");
               appendDirtyJavaScript(mgr.encodeStringForJavascript(informationMessage));
               appendDirtyJavaScript("')) {\n");
               appendDirtyJavaScript("   f.CONFIRMACTION.value='OK';\n");
               appendDirtyJavaScript("   submit();\n");
               appendDirtyJavaScript(" }\n");
	       if("TRUE".equals(fromNavigator))
	       {
                  appendDirtyJavaScript(" else{\n");
                  appendDirtyJavaScript("   f.CONFIRMACTION.value='CANCEL';\n");
                  appendDirtyJavaScript("   submit();\n");
		  appendDirtyJavaScript(" }\n");
	       }
	       else
               {
                  appendDirtyJavaScript(" else{\n");
                  appendDirtyJavaScript("   f.CONFIRMACTION.value='CANCEL';\n");
                  appendDirtyJavaScript("   window.close();\n");
		  appendDirtyJavaScript(" }\n");
               }
               
            }
            // Bug Id 78806, end
         }//Bug Id 81807
  // >>>> end of group box 


         appendToHTML("</td></tr>\n");
         appendToHTML("</table>\n");

         endDataPresentation(false);
         // Draw control buttons..
         //Bug Id:69301, start
         drawButtonPanel(true, null, false, nextButtonEvent, true, finishButtonEvent);
         //Bug Id:69301, end

         appendDirtyJavaScript("function setCheckBoxValues(textField,checkbox)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript(" if (checkbox.checked) \n");
         appendDirtyJavaScript("    eval('document.forms[0].'+textField+'.value =\\\'TRUE\\\';');\n");
         appendDirtyJavaScript(" else \n");
         appendDirtyJavaScript("    eval('document.forms[0].'+textField+'.value = \\\'FALSE\\\';');\n");
         //Bug Id 81807, start
         appendDirtyJavaScript(" if ((!document.forms[0].DOWNLOAD_ORIGINALS.checked) && (!document.forms[0].DOWNLOAD_VIEWS.checked)) \n");
         appendDirtyJavaScript("    document.forms[0].FINISH.disabled = true \n");
         appendDirtyJavaScript(" else \n");
         appendDirtyJavaScript("    document.forms[0].FINISH.disabled = false \n");
         //Bug Id 81807, end
         appendDirtyJavaScript("}\n");

         //Bug Id 81807, start
         appendDirtyJavaScript("function handleCheckBoxes(radiobutton)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if (radiobutton.value == \"1\") \n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = true;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_ORIGINALS.disabled = false;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_VIEWS.disabled = false;\n");
         appendDirtyJavaScript("      document.forms[0].ZIP_FILES.disabled = false;\n");
         appendDirtyJavaScript("      if ((!document.forms[0].DOWNLOAD_ORIGINALS.checked) && (!document.forms[0].DOWNLOAD_VIEWS.checked)) \n");
         appendDirtyJavaScript("         document.forms[0].FINISH.disabled = true \n");
         appendDirtyJavaScript("      else \n");
         appendDirtyJavaScript("         document.forms[0].FINISH.disabled = false \n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if (radiobutton.value == \"2\") \n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = false;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_ORIGINALS.disabled = false;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_VIEWS.disabled = false;\n");
         appendDirtyJavaScript("      document.forms[0].ZIP_FILES.disabled = false;\n");
         appendDirtyJavaScript("      if ((!document.forms[0].DOWNLOAD_ORIGINALS.checked) && (!document.forms[0].DOWNLOAD_VIEWS.checked)) \n");
         appendDirtyJavaScript("         document.forms[0].FINISH.disabled = true \n");
         appendDirtyJavaScript("      else \n");
         appendDirtyJavaScript("         document.forms[0].FINISH.disabled = false \n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if (radiobutton.value == \"3\") \n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = true;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_ORIGINALS.disabled = true;\n");
         appendDirtyJavaScript("      document.forms[0].DOWNLOAD_VIEWS.disabled = true;\n");
         appendDirtyJavaScript("      document.forms[0].ZIP_FILES.disabled = true;\n");
         appendDirtyJavaScript("      document.forms[0].FINISH.disabled = false \n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");

         if (transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_out))
         {
            appendDirtyJavaScript("var len = document.forms[0].PUBLISH_MODE.length;\n");
            appendDirtyJavaScript("for (i = 0; i < len; i++) \n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   if (document.forms[0].PUBLISH_MODE[i].checked) \n");
            appendDirtyJavaScript("   {\n");
            appendDirtyJavaScript("      if (document.forms[0].PUBLISH_MODE[i].value == \"1\") \n");
            appendDirtyJavaScript("      {\n");
            appendDirtyJavaScript("         document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = true;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_ORIGINALS.disabled = false;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_VIEWS.disabled = false;\n");
            appendDirtyJavaScript("         document.forms[0].ZIP_FILES.disabled = false;\n");
            appendDirtyJavaScript("         if ((!document.forms[0].DOWNLOAD_ORIGINALS.checked) && (!document.forms[0].DOWNLOAD_VIEWS.checked)) \n");
            appendDirtyJavaScript("            document.forms[0].FINISH.disabled = true \n");
            appendDirtyJavaScript("         else \n");
            appendDirtyJavaScript("            document.forms[0].FINISH.disabled = false \n");
            appendDirtyJavaScript("      }\n");
            appendDirtyJavaScript("      else if (document.forms[0].PUBLISH_MODE[i].value == \"2\") \n");
            appendDirtyJavaScript("      {\n");
            appendDirtyJavaScript("         document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = false;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_ORIGINALS.disabled = false;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_VIEWS.disabled = false;\n");
            appendDirtyJavaScript("         document.forms[0].ZIP_FILES.disabled = false;\n");
            appendDirtyJavaScript("         if ((!document.forms[0].DOWNLOAD_ORIGINALS.checked) && (!document.forms[0].DOWNLOAD_VIEWS.checked)) \n");
            appendDirtyJavaScript("            document.forms[0].FINISH.disabled = true \n");
            appendDirtyJavaScript("         else \n");
            appendDirtyJavaScript("            document.forms[0].FINISH.disabled = false \n");
            appendDirtyJavaScript("      }\n");
            appendDirtyJavaScript("      else if (document.forms[0].PUBLISH_MODE[i].value == \"3\") \n");
            appendDirtyJavaScript("      {\n");
            appendDirtyJavaScript("         document.forms[0].OPEN_DOWNLOAD_FOLDER.disabled = true;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_ORIGINALS.disabled = true;\n");
            appendDirtyJavaScript("         document.forms[0].DOWNLOAD_VIEWS.disabled = true;\n");
            appendDirtyJavaScript("         document.forms[0].ZIP_FILES.disabled = true;\n");
            appendDirtyJavaScript("         document.forms[0].FINISH.disabled = false \n");
            appendDirtyJavaScript("      }\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n");
         }
         //Bug Id 81807, end

      }
      //Bug Id: 68412, start
      if (!mgr.isEmpty(error_message)) {
         error_message = error_message.replaceAll("\n","\\n");
         //Bug Id: 71759, start
         appendDirtyJavaScript("\n alert('"+handleSingleQuote(error_message)+"');\n");
         //Bug Id: 71759, end
         
      }
      if (!mgr.isEmpty(windowLocation)) {
         appendDirtyJavaScript("document.location = \"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(windowLocation));
         appendDirtyJavaScript("\";\n");
      }
      //Bug Id: 68412, end

      
      if (fileOperationStarted) {
         appendToHTML(DocmawUtil.getClientMgrObjectStr());
         // write checkout path to hidden field CHECKOUT_PATH
         appendToHTML("<input type=\"hidden\" name=\"CHECKOUT_PATH_HIDDEN\" value=\""+mgr.HTMLEncode(checkoutPath)+"\">");//Bug Id 68773
         appendToHTML("<input type=\"hidden\" name=\"FILE_OPERATIONS_STARTED\" value=\"TRUE\">");
         appendDirtyJavaScript("\n setDefaultCheckoutPath();\n");
         appendDirtyJavaScript("   submit();\n");
      }

      appendDirtyJavaScript("function Close()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      eval(\"opener.update_State('FALSE')\");\n");
      appendDirtyJavaScript("      eval(\"opener.refreshParent()\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err){}\n");
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("}\n");

      if ("TRUE".equals(fromNavigator))
      {
         appendToHTML("<input type=\"hidden\" name=\"FILE_OPERATIONS_DONE\" value=\"\">");

         appendDirtyJavaScript("function refreshParent()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.FILE_OPERATIONS_DONE.value=\"TRUE\";\n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");
      }
      

      appendDirtyJavaScript("function setDefaultCheckoutPath()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var checkout_path = document.form.oCliMgr.GetDocumentFolder();\n");
      appendDirtyJavaScript("   if (!document.form.oCliMgr.FolderExists(checkout_path))\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("     checkout_path = document.form.oCliMgr.BrowseForLocalPath();\n");
      appendDirtyJavaScript("     if (!document.form.oCliMgr.FolderExists(checkout_path))\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("       alert(\"" + mgr.translate("DOCMAWDOCSTRUCTOPRABORTED: Operation Aborted.") + "\");\n");
      appendDirtyJavaScript("      // window.close();\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("     else\n");
      appendDirtyJavaScript("       document.form.oCliMgr.SetDocumentFolder(checkout_path);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   document.form.CHECKOUT_PATH_HIDDEN.value = checkout_path;\n");
      appendDirtyJavaScript("   if (checkout_path.charAt(checkout_path.length - 1) != '\\\\')\n");
      appendDirtyJavaScript("         checkout_path = checkout_path + '\\\\';\n");
      appendDirtyJavaScript("   fullPath = checkout_path + '"+(mgr.translate("temp") + "\\\\" + mgr.translate("Document Transmittal") + "\\\\" + mgr.encodeStringForJavascript(removeIllegalCharacters(this.transmittalId)) +"'")+";\n"); //Bug Id 91417
      appendDirtyJavaScript("   if (!document.form.oCliMgr.FolderExists(fullPath))\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.oCliMgr.CreatePath(fullPath); \n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      if (executingFileOperation) {
         
         if ("TRUE".equals(fromNavigator))
         {
	    //Bug Id 81806, start
	    
	    if ("TRUE".equals(sFromOtherWindows)) 
	    {
	       appendDirtyJavaScript("   window.open(\"");
               appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
               appendDirtyJavaScript("\",\"newWindowForFileOperation\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
	    }
	    else
	    {
	       appendDirtyJavaScript("   window.open(\"");
	       appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
	       appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
	    }
	    //Bug Id 81806, end
         }
         else
         {
            //bug 68407 start
            if("2".equals(this.sPublishMode))
               appendDirtyJavaScript("eval(\"opener.update_State('FALSE')\");\n");
            //bug 68407 end
	    //Bug Id 75074, start
	    else
	       appendDirtyJavaScript("eval(\"opener.check_doc_released('FALSE')\");\n");
	    //Bug Id 75074, end
            appendDirtyJavaScript("document.location = \"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
            appendDirtyJavaScript("\";\n");
         }
         
         
      }

      //Bug Id 72460, start
      appendDirtyJavaScript("function update_Wizard_State(value)\n");// '_' is to make this Js method look different.
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    document.form.UPDATE_WIZARD_STATE.value = value;\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 72460, end

      if (bAddExistDoc) {
         appendDirtyJavaScript("lovDocuments();\n");

         appendDirtyJavaScript("function lovDocuments(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("        openLOVWindow('DOCUMENT_REV_SELECTED',i,\n");
         appendDirtyJavaScript("        '");
         appendDirtyJavaScript(root_path);
         appendDirtyJavaScript("docmaw/DocIssueLov2.page?MULTICHOICE=true',500,500,'submitForConnectingDocs');\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function submitForConnectingDocs() {\n");
         appendDirtyJavaScript("   document.form.DOCUMENT_SELECTED.value = 'TRUE';");
         appendDirtyJavaScript("   submit();");
         appendDirtyJavaScript("}\n");


         
      }
      //Bug Id:69301, start
      appendDirtyJavaScript(writeValidateMethod4Button("Next",current_step));
      appendDirtyJavaScript(writeValidateMethod4Button("Finish",current_step));
      //Bug Id:69301, end

      
      if (!mgr.isEmpty(errorMessage)) {
         /*if (this.bGoBack) {
            appendDirtyJavaScript("history.back();\n");
         }*/
         //Bug Id: 71759, start
         appendDirtyJavaScript("alert('"+ this.handleSingleQuote(errorMessage) +"');\n");
         //Bug Id: 71759, end
         
         
      }

      //Bug Id 75074, start

      if (refreshParentAndClose) {
         appendDirtyJavaScript("\n Close();\n");
      }
      //Bug Id 75074, end

      //Bug Id 81806, start
      if (bLoadOnParentWindow) 
      {
	 appendDirtyJavaScript("eval(\"opener.load_Transmittal_Info('"+transfer_url+"')\");\n");
	 appendDirtyJavaScript("window.close();\n");
      }
      //Bug Id 81806, end
      //Bug Id 89221, start
      appendToHTML("<input type=\"hidden\" name=\"PROJECT_ID_EDITED\" value=\"FALSE\">\n");

      
      appendDirtyJavaScript("function validateProjectId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkProjectId(i) ) return;\n");
      appendDirtyJavaScript("   getField_('PROJECT_ID_EDITED',i).value = \"TRUE\";\n");  
      appendDirtyJavaScript("}\n");
      //Bug Id 89221, end

      ctx.writeValue("CURRENT_STEP",current_step);

   }

  //Bug Id: 89221, start
  private void updateCustomerProjectId(String projectId)
  {

      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("PROJECT", "DOCUMENT_TRANSMITTAL_API.Get_Customer_Project_Id", "CUSTOMER_PROJECT_ID");
      cmd.addParameter("PROJECT_ID",projectId);
      trans = mgr.perform(trans);

      String newCustomerProjectId = trans.getValue("PROJECT/DATA/CUSTOMER_PROJECT_ID");
      if (!mgr.isEmpty(newCustomerProjectId)) {
          customerProjectId = newCustomerProjectId;
          bCustomerProjectIdUpdated = !mgr.isEmpty(oldCustomerProjectId) && !oldCustomerProjectId.equals(newCustomerProjectId);
      }
      
  }
  //Bug Id: 89221, end


  private void readStep1()
  {
     ASPManager mgr = getASPManager();

     //Bug Id 81808 Start
     projectId             = mgr.readValue("PROJECT_ID","");
     subProjectId          = mgr.readValue("SUB_PROJECT_ID","");
     activityId            = mgr.readValue("ACTIVITY_ID","");

     //Bug Id 89221 Start
     if ("TRUE".equals(mgr.readValue("PROJECT_ID_EDITED")) && !mgr.isEmpty(projectId))
     {
         updateCustomerProjectId(projectId); 
     }
     //Bug Id 89221 end
     

     // check the existance of the project id 
     String projectId_temp             = mgr.readValue("PROJECT_ID","");
     
     if (!mgr.isEmpty(projectId) ) 
     {
         if ("FALSE".equals(ProjectIdDoesExist(projectId)))
         {
              this.errorMessage = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDPROJNOTEXIST: The project object does not exist."); 
              bGoBack =  true;
         }
     }
     //Bug Id 81808 End
   
     //Bug Id:71844: we have to check the existance of the Transmittal id if this is new
     if ("TRUE".equals(fromNavigator)) {//it is new
        if (!bSaveNewTransDone) // Bug Id 93698
           if ("TRUE".equals(doesItExistAlready(mgr.readValue("TRANSMITTAL_ID",""))))
           {
              this.errorMessage = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDOBJECTEXIST: The Document Transmittal object already exist."); 
              bGoBack =  true;
           } 
     }
    if (headset.countRows()== 0) {
      trans.clear();
      cmd = trans.addEmptyCommand("TRANS","DOCUMENT_TRANSMITTAL_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      
      trans = mgr.perform(trans);
      data = trans.getBuffer("TRANS/DATA");

       headset.addRow(data);

     }
     headset.store();

     transmittalId         = mgr.readValue("TRANSMITTAL_ID","");
     transmittalDesc       = mgr.readValue("TRANSMITTAL_DESCRIPTION","");
     transmittalInfo       = mgr.readValue("TRANSMITTAL_INFO","");
     transmittalDirection  = mgr.readValue("TRANSMITTAL_DIRECTION","");
     contactPerson         = mgr.readValue("CONTACT_PERSON","");
     receiverType          = mgr.readValue("RECEIVER_TYPE","");
     receiverContactPerson = mgr.readValue("RECEIVER_CONTACT_PERSON","");
     distributionMethod    = mgr.readValue("DISTRIBUTION_METHOD","");
     receiverAddress       = mgr.readValue("RECEIVER_ADDRESS","");
     expectedSendDate      = mgr.readValue("EXPECTED_SEND_DATE","");
     actualSentSate        = mgr.readValue("ACTUAL_SENT_DATE","");
     expectedReturnDate    = mgr.readValue("EXPECTED_RETURN_DATE","");
     actualReturnDate      = mgr.readValue("ACTUAL_RETURN_DATE","");

     expectedSendDate_server   = mgr.isEmpty(headset.getValue("EXPECTED_SEND_DATE"))  ?"":headset.getValue("EXPECTED_SEND_DATE");
     actualSentSate_server     = mgr.isEmpty(headset.getValue("ACTUAL_SENT_DATE"))    ?"":headset.getValue("ACTUAL_SENT_DATE");
     expectedReturnDate_server = mgr.isEmpty(headset.getValue("EXPECTED_RETURN_DATE"))?"":headset.getValue("EXPECTED_RETURN_DATE");
     actualReturnDate_server   = mgr.isEmpty(headset.getValue("ACTUAL_RETURN_DATE"))  ?"":headset.getValue("ACTUAL_RETURN_DATE");

     headset.clear();



  }

  //Bug Id: 71844, start
  private String doesItExistAlready(String transmittalId)
  {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("TRANSEXIST","DOCUMENT_TRANSMITTAL_API.check_already_exist","ATTR");
      cmd.addParameter("TRANSMITTAL_ID",transmittalId);
      trans = mgr.perform(trans);
      return trans.getValue("TRANSEXIST/DATA/ATTR");
  }
  //Bug Id: 71844, end

  //Bug Id 81808, Start
  private String ProjectIdDoesExist(String projectId)
  {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("PROJEXIST","PROJECT_API.Check_Project_Exist","ATTR");
      cmd.addParameter("PROJECT_ID",projectId);
      trans = mgr.perform(trans);
      return trans.getValue("PROJEXIST/DATA/ATTR");
  }
  //Bug Id 81808, End



  private void readStep2()
  {
     ASPManager mgr = getASPManager();
     customerProjectId     = mgr.readValue("CUSTOMER_PROJECT_ID","");
     customerSsubProjectId = mgr.readValue("CUSTOMER_SUB_PROJECT_ID","");
     customerActivityId    = mgr.readValue("CUSTOMER_ACTIVITY_ID","");
     supplierProjectId     = mgr.readValue("SUPPLIER_PROJECT_ID","");
     supplierSubProjectId  = mgr.readValue("SUPPLIER_SUB_PROJECT_ID","");
     supplierActivityId    = mgr.readValue("SUPPLIER_ACTIVITY_ID","");
     //Bug Id 81808, Start
     subContractProjectId     = mgr.readValue("SUBCONT_PROJECT_ID","");
     subContractSubProjectId  = mgr.readValue("SUBCONT_SUB_PROJECT_ID","");
     subContractActivityId    = mgr.readValue("SUBCONT_ACTIVITY_ID","");
     //Bug Id 81808, End

  }

  private void readStep4()
  {
     ASPManager mgr = getASPManager();

     bZipFiles    = "TRUE".equals(mgr.readValue("ZIPFILES_HIDDEN",""));
     sPublishMode = mgr.readValue("PUBLISH_MODE","1"); //Bug Id 81807

     //Bug Id 81807, start
     bDownloadOriginalFiles = "TRUE".equals(mgr.readValue("DOWNLOAD_ORIGINAL_HIDDEN",""));
     bDownloadViewFiles = "TRUE".equals(mgr.readValue("DOWNLOAD_VIEW_HIDDEN",""));
     bOpenDownloadFolder = "TRUE".equals(mgr.readValue("OPEN_DOWNLOAD_FOLDER_HIDDEN", ""));
     bStep4Visited = true;
     //Bug Id 81807, end
  }


  private void removeBufferAt(ASPBuffer buf, int position)
   {
      buf.removeItemAt(position);
   }


  public void run() throws FndException
   {
      String isProjectInstalled; //Bug Id 81808
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      ctx = mgr.getASPContext();

      currLay = issuelay.getLayoutMode();
      

      transmittalId         = ctx.readValue("CTX_TRANSMITTALID","");
      transmittalDesc       = ctx.readValue("CTX_TRANSMITTALDESC","");
      transmittalInfo       = ctx.readValue("CTX_TRANSMITTALINFO","");
      transmittalDirection  = ctx.readValue("CTX_TRANSMITTALDIRECTION","");
      contactPerson         = ctx.readValue("CTX_CONTACTPERSON","");
      receiverType          = ctx.readValue("CTX_RECEIVERTYPE","");
      receiverContactPerson = ctx.readValue("CTX_RECEIVERCONTACTPERSON","");
      distributionMethod    = ctx.readValue("CTX_DISTRIBUTIONMETHOD","");
      receiverAddress       = ctx.readValue("CTX_RECEIVERADDRESS","");
      expectedSendDate      = ctx.readValue("CTX_EXPECTEDSENDDATE","");
      actualSentSate        = ctx.readValue("CTX_ACTUALSENTSATE","");
      expectedReturnDate    = ctx.readValue("CTX_EXPECTEDRETURNDATE","");
      actualReturnDate      = ctx.readValue("CTX_ACTUALRETURNDATE","");
      
      expectedSendDate_server   = ctx.readValue("CTX_EXPECTEDSENDDATE_SERVER","");
      actualSentSate_server     = ctx.readValue("CTX_ACTUALSENTSATE_SERVER","");
      expectedReturnDate_server = ctx.readValue("CTX_EXPECTEDRETURNDATE_SERVER","");
      actualReturnDate_server   = ctx.readValue("CTX_ACTUALRETURNDATE_SERVER","");

      projectId             = ctx.readValue("CTX_PROJECTID","");
      subProjectId          = ctx.readValue("CTX_SUBPROJECTID","");
      activityId            = ctx.readValue("CTX_ACTIVITYID","");
      customerProjectId     = ctx.readValue("CTX_CUSTOMERPROJECTID","");
      //Bug Id 89221 Start
      oldCustomerProjectId  = ctx.readValue("CTX_OLD_CUSTOMER_PROJECT_ID","");
      //Bug Id 89221 end
      customerSsubProjectId = ctx.readValue("CTX_CUSTOMERSSUBPROJECTID","");
      customerActivityId    = ctx.readValue("CTX_CUSTOMERACTIVITYID","");
      supplierProjectId     = ctx.readValue("CTX_SUPPLIERPROJECTID","");
      supplierSubProjectId  = ctx.readValue("CTX_SUPPLIERSUBPROJECTID","");
      supplierActivityId    = ctx.readValue("CTX_SUPPLIERACTIVITYID","");
      //Bug Id 81808, Start
      subContractProjectId     = ctx.readValue("CTX_SUBCONTPROJECTID","");
      subContractSubProjectId  = ctx.readValue("CTX_SUBCONTSUBPROJECTID","");
      subContractActivityId    = ctx.readValue("CTX_SUBCONTACTIVITYID","");
      isProjectInstalled    = ctx.readValue("CTX_ISPROJINSTALLED","");
      //Bug Id 81808, End

      // for Step 4
      bZipFiles     = ctx.readFlag("CTX_BZIPFILES",false);
      sPublishMode  = ctx.readValue("CTX_PUBLISHMODE","1"); //Bug Id 81807

      fromNavigator         = ctx.readValue("CTX_FROMNVG","TRUE");
      beforeNewLay          = ctx.readNumber("CTX_BEFORENEWLAY",0);
   
      //Bug Id 81808, Start
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      if (isProjectInstalled.equals("TRUE"))
         isProjInstalled = true;
      else if (isProjectInstalled.equals("FALSE"))
         isProjInstalled = false;
      //Bug Id 81808, End

      //Bug Id 81807, start
      bDownloadOriginalFiles = ctx.readFlag("CTX_BDOWNLOADORIGINALS", true);
      bDownloadViewFiles     = ctx.readFlag("CTX_BDOWNLOADVIEWS", false);
      bOpenDownloadFolder    = ctx.readFlag("CTX_BOPENDOWNLOADFOLDER", false);
      bStep4Visited          = ctx.readFlag("CTX_BSTEP4VISITED", false);
      //Bug Id 81807, end

      //Bug Id 81806, start
      sFromOtherWindows     = ctx.readValue("CTX_FROMOTHERWINDOWS", "FALSE"); 
      bLoadOnParentWindow   = ctx.readFlag("CTX_BLOADONPARENTWINDOW", false);
      //Bug Id 81806, end
      
      bSaveNewTransDone = ctx.readFlag("CTX_SAVENEWTRANSDONE", false); // Bug Id 93698
   
      // Fetch data transfered to wizard..
      if (mgr.dataTransfered())
         okFind();

      // Initialise wizard..
      initWizard();

      // Handle client actions..
     
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.buttonPressed("BACK"))
         previousStep();
      else if ("TRUE".equals(mgr.readValue("NEXT_OK")))//Bug Id:69301
         nextStep();
      else if ("TRUE".equals(mgr.readValue("FINISH_OK")))//Bug Id:69301
         finish();
       else if (mgr.buttonPressed("CANCEL"))
         cancel();
       else if (!mgr.isEmpty(mgr.readValue("FILE_OPERATIONS_STARTED"))) {
          this.executeFileOperations();
       }
       else if (!mgr.isEmpty(mgr.readValue("FILE_OPERATIONS_DONE")) && "TRUE".equals(mgr.readValue("FILE_OPERATIONS_DONE"))) {
          //bug 68407 start
          updateState = !"2".equals(sPublishMode); //we do not update state unless tranmsittal is sent by email
          //bug 68407 end
	  //Bug Id 72460, start
	  if ("FALSE".equals(mgr.readValue("UPDATE_WIZARD_STATE"))) 
	  {
              updateState = false;
	  }
	  //Bug Id 72460, end
          this.gotoTransmittalInfo();
       }
       else if ("TRUE".equals(mgr.readValue("DOCUMENT_SELECTED")))
         this.connectDocumentsToTrans();
     
      // Bug Id 78806, start
      else if ("OK".equals(mgr.readValue("CONFIRMACTION")))
      {
         confimationDone = true;
         finish();
      }
      else if ("CANCEL".equals(mgr.readValue("CONFIRMACTION"))) 
      {
         gotoTransmittalInfo();
      }
      // Bug Id 78806, end
       

      adjust();

      //Bug Id 81808, Start
      if (isProjInstalled) 
         isProjectInstalled = "TRUE";
      else
         isProjectInstalled = "FALSE";
      //Bug Id 81808, End

      ctx.writeValue("CTX_TRANSMITTALID",transmittalId);
      ctx.writeValue("CTX_TRANSMITTALDESC",transmittalDesc);
      ctx.writeValue("CTX_TRANSMITTALINFO",transmittalInfo);
      ctx.writeValue("CTX_TRANSMITTALDIRECTION",transmittalDirection);
      ctx.writeValue("CTX_CONTACTPERSON",contactPerson);
      ctx.writeValue("CTX_RECEIVERTYPE",receiverType);
      ctx.writeValue("CTX_RECEIVERCONTACTPERSON",receiverContactPerson);
      ctx.writeValue("CTX_DISTRIBUTIONMETHOD",distributionMethod);
      ctx.writeValue("CTX_RECEIVERADDRESS",receiverAddress);
      ctx.writeValue("CTX_EXPECTEDSENDDATE",expectedSendDate);
      ctx.writeValue("CTX_ACTUALSENTSATE",actualSentSate);
      ctx.writeValue("CTX_EXPECTEDRETURNDATE",expectedReturnDate);
      ctx.writeValue("CTX_ACTUALRETURNDATE",actualReturnDate);

      ctx.writeValue("CTX_EXPECTEDSENDDATE_SERVER",  expectedSendDate_server);
      ctx.writeValue("CTX_ACTUALSENTSATE_SERVER",    actualSentSate_server);
      ctx.writeValue("CTX_EXPECTEDRETURNDATE_SERVER",expectedReturnDate_server);
      ctx.writeValue("CTX_ACTUALRETURNDATE_SERVER",  actualReturnDate_server);

      ctx.writeValue("CTX_PROJECTID",projectId);
      ctx.writeValue("CTX_SUBPROJECTID",subProjectId);
      ctx.writeValue("CTX_ACTIVITYID",activityId);
      ctx.writeValue("CTX_CUSTOMERPROJECTID",customerProjectId);
      //Bug Id 89221 Start
      ctx.writeValue("CTX_OLD_CUSTOMER_PROJECT_ID",oldCustomerProjectId);
      //Bug Id 89221 end
      ctx.writeValue("CTX_CUSTOMERSSUBPROJECTID",customerSsubProjectId);
      ctx.writeValue("CTX_CUSTOMERACTIVITYID",customerActivityId);
      ctx.writeValue("CTX_SUPPLIERPROJECTID",supplierProjectId);
      ctx.writeValue("CTX_SUPPLIERSUBPROJECTID",supplierSubProjectId);
      ctx.writeValue("CTX_SUPPLIERACTIVITYID",supplierActivityId);
      // Bug Id 81808, Start
      ctx.writeValue("CTX_SUBCONTPROJECTID",subContractProjectId);
      ctx.writeValue("CTX_SUBCONTSUBPROJECTID",subContractSubProjectId);
      ctx.writeValue("CTX_SUBCONTACTIVITYID",subContractActivityId);
      ctx.writeValue("CTX_ISPROJINSTALLED",isProjectInstalled);
      // Bug Id 81808, End


      //for step 4
      ctx.writeFlag("CTX_BZIPFILES",bZipFiles);
      ctx.writeValue("CTX_PUBLISHMODE",sPublishMode);

      ctx.writeValue("CTX_FROMNVG",fromNavigator);
      ctx.writeNumber("CTX_BEFORENEWLAY",beforeNewLay);

      //Bug Id 81807, start
      ctx.writeFlag("CTX_BDOWNLOADORIGINALS",bDownloadOriginalFiles);
      ctx.writeFlag("CTX_BDOWNLOADVIEWS", bDownloadViewFiles);
      ctx.writeFlag("CTX_BOPENDOWNLOADFOLDER", bOpenDownloadFolder);
      ctx.writeFlag("CTX_BSTEP4VISITED", bStep4Visited);
      //Bug Id 81807, end

      //Bug Id 81806, start
      ctx.writeValue("CTX_FROMOTHERWINDOWS",sFromOtherWindows); 
      ctx.writeFlag("CTX_BLOADONPARENTWINDOW", bLoadOnParentWindow);
      //Bug Id 81806, end
      
      ctx.writeFlag("CTX_SAVENEWTRANSDONE", bSaveNewTransDone); // Bug Id 93698
   }


  public void saveEdit()
  {
     saveTransmittal(false);
  }


  public void saveIssues()
  {
     ASPManager mgr = getASPManager();
     trans.clear();
     if (issueset.countRows()>0) {
         //refresh row set
         ASPBuffer buf = getRowsAsBuffer(issueset);
         setBufferAsRows(issueset, buf);
         // Bug Id: 68412, mgr.submit() removed
     }
  }


  public void saveNewIssue()
   {
      updateRecord();
      newRowIssue();
   }


  private void saveNewTransmittal()
  {
     saveTransmittal(true);
  }


  public void saveReturnIssue()
   {
      updateRecord();
     
   }



    private void saveTransmittal(boolean newTransmittal)
  {

      ASPManager mgr      = getASPManager();
      String tempTransId = "";
      String current_step = ctx.readValue("CURRENT_STEP");
      //Bug Id:71844, start
      if (newTransmittal) {
         if (!mgr.isEmpty(projectId) && mgr.isEmpty(transmittalId)) { //Bug Id 77060, Added new check for transmittalId
            tempTransId = this.generateTransmittalId(projectId);
         }
         
         if (!mgr.isEmpty(tempTransId)) {
            transmittalId = tempTransId.toUpperCase();
            //Bug Id:71759, start

            //Bug Id:71759, end

         }
         //Bug Id:71844, start
         if (mgr.isEmpty(transmittalId)) {
             errorMessage = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDIDMANDATORY: Transmittal Id or Project with Transmittal counter must be entered.");
             bTransmittalIdNotCreated = true;
             return;
         }
         else
             /*update issueset with new transmittal Id, since transmittal id can be 
             given/created even after documents are connected to the transmittal.*/
            updateIssueSet();
         //Bug Id:71844, end
      }
      //Bug Id:71844, end
      
      trans.clear();
     
      
      String attr = "TRANSMITTAL_ID"            + String.valueOf((char)31) + transmittalId        + String.valueOf((char)30)
                    + "TRANSMITTAL_DESCRIPTION" + String.valueOf((char)31) + transmittalDesc      + String.valueOf((char)30)
                    + "DISTRIBUTION_METHOD"     + String.valueOf((char)31) + distributionMethod   + String.valueOf((char)30)
                    + "RECEIVER_TYPE"           + String.valueOf((char)31) + receiverType         + String.valueOf((char)30)
                    + "TRANSMITTAL_DIRECTION"   + String.valueOf((char)31) + transmittalDirection + String.valueOf((char)30);

     attr  += "TRANSMITTAL_INFO"       + String.valueOf((char)31) + transmittalInfo       + String.valueOf((char)30)
           + "CONTACT_PERSON"          + String.valueOf((char)31) + contactPerson         + String.valueOf((char)30)
           + "RECEIVER_CONTACT_PERSON" + String.valueOf((char)31) + receiverContactPerson + String.valueOf((char)30)
           + "DISTRIBUTION_METHOD"     + String.valueOf((char)31) + distributionMethod    + String.valueOf((char)30)
           + "RECEIVER_ADDRESS"        + String.valueOf((char)31) + receiverAddress       + String.valueOf((char)30)
           //+ "EXPECTED_SEND_DATE"      + String.valueOf((char)31) + expectedSendDate      + String.valueOf((char)30)
           + "EXPECTED_SEND_DATE"      + String.valueOf((char)31) + expectedSendDate_server + String.valueOf((char)30)
           + "ACTUAL_SENT_DATE"        + String.valueOf((char)31) + actualSentSate_server + String.valueOf((char)30)
           + "EXPECTED_RETURN_DATE"    + String.valueOf((char)31) + expectedReturnDate_server + String.valueOf((char)30)
           + "ACTUAL_RETURN_DATE"      + String.valueOf((char)31) + actualReturnDate_server + String.valueOf((char)30);

     // Bug Id 81808, Start
      if (isProjInstalled)
      {
         attr  +=  "PROJECT_ID"            + String.valueOf((char)31) + projectId             + String.valueOf((char)30)
               + "SUB_PROJECT_ID"          + String.valueOf((char)31) + subProjectId          + String.valueOf((char)30)
               + "ACTIVITY_ID"             + String.valueOf((char)31) + activityId            + String.valueOf((char)30)
               + "CUSTOMER_PROJECT_ID"     + String.valueOf((char)31) + customerProjectId     + String.valueOf((char)30)
               + "CUSTOMER_SUB_PROJECT_ID" + String.valueOf((char)31) + customerSsubProjectId + String.valueOf((char)30)
               + "CUSTOMER_ACTIVITY_ID"    + String.valueOf((char)31) + customerActivityId    + String.valueOf((char)30)
               + "SUPPLIER_PROJECT_ID"     + String.valueOf((char)31) + supplierProjectId     + String.valueOf((char)30)
               + "SUPPLIER_SUB_PROJECT_ID" + String.valueOf((char)31) + supplierSubProjectId  + String.valueOf((char)30)
               + "SUPPLIER_ACTIVITY_ID"    + String.valueOf((char)31) + supplierActivityId    + String.valueOf((char)30)
               + "SUB_CONTRACTOR_PROJECT_ID"     + String.valueOf((char)31) + subContractProjectId     + String.valueOf((char)30)
               + "SUB_CONTRACTOR_SUB_PROJECT_ID" + String.valueOf((char)31) + subContractSubProjectId  + String.valueOf((char)30)
               + "SUB_CONTRACTOR_ACTIVITY_ID"    + String.valueOf((char)31) + subContractActivityId    + String.valueOf((char)30);
      } else
      {
         attr  +="CUSTOMER_PROJECT_ID"     + String.valueOf((char)31) + customerProjectId     + String.valueOf((char)30)
                 + "CUSTOMER_SUB_PROJECT_ID" + String.valueOf((char)31) + customerSsubProjectId + String.valueOf((char)30)
                 + "CUSTOMER_ACTIVITY_ID"    + String.valueOf((char)31) + customerActivityId    + String.valueOf((char)30)
                 + "SUPPLIER_PROJECT_ID"     + String.valueOf((char)31) + supplierProjectId     + String.valueOf((char)30)
                 + "SUPPLIER_SUB_PROJECT_ID" + String.valueOf((char)31) + supplierSubProjectId  + String.valueOf((char)30)
                 + "SUPPLIER_ACTIVITY_ID"    + String.valueOf((char)31) + supplierActivityId    + String.valueOf((char)30)
                 + "SUB_CONTRACTOR_PROJECT_ID"     + String.valueOf((char)31) + subContractProjectId     + String.valueOf((char)30)
                 + "SUB_CONTRACTOR_SUB_PROJECT_ID" + String.valueOf((char)31) + subContractSubProjectId  + String.valueOf((char)30)
                 + "SUB_CONTRACTOR_ACTIVITY_ID"    + String.valueOf((char)31) + subContractActivityId    + String.valueOf((char)30);
      }
     // Bug Id 81808, End
      

      cmd =  trans.addCustomCommand("HANDLE","DOCUMENT_TRANSMITTAL_API.Handle_Transmittal_Wizard_Save");
      cmd.addParameter("ATTR", attr);
      cmd.addParameter("ATTR", (newTransmittal?"TRUE":"FALSE"));
      //Bug Id 89221 Start
      if (!newTransmittal && bCustomerProjectIdUpdated ) {
          //Add a history line  here                            
          String msgText = mgr.translate("DOCMAWDOCTRANSMITTALWIZCUSPROJMODI: The Customer Project ID '&1' was overwritten automatically.",oldCustomerProjectId);
          cmd = trans.addCustomCommand("ADD_HIS_LINE", "Doc_Transmittal_History_Api.Create_New_Line");
          cmd.addParameter("ATTR",transmittalId);
          cmd.addParameter("ATTR",msgText);
          cmd.addParameter("ATTR","TRANS_METADATA_MODI");
          cmd.addParameter("ATTR","");
          cmd.addParameter("ATTR","");
          cmd.addParameter("ATTR","");
          cmd.addParameter("ATTR","");

      }
      //Bug Id 89221 end
      //Bug Id: 68412, start
      //trans = mgr.perform(trans);
      try
      {
         trans = mgr.performEx(trans);
         bSaveNewTransDone = true; // Bug Id 93698
      }
      catch(ASPLog.ExtendedAbortException e)
      {
         Buffer info = e.getExtendedInfo();
         try{
            error_message = info.getItem("ERROR_MESSAGE").toString();
            error_message = error_message.substring(error_message.indexOf("=") + 1);
         }catch(ifs.fnd.buffer.ItemNotFoundException inf)
         {
            error_message = "";
         }
         
      }
      //Bug Id: 68412, end 
  }


  //Bug Id:71759, start
  private void updateIssueSet()
  {
      issueset.first();

      for (int k=0;k<issueset.countRows();k++) {
          //issueset.setValue("ISSUE_TRANSMITTAL_ID",this.transmittalId);
          issueset.setValue("TRANSMITTAL_ID",this.transmittalId);
          issueset.next();
      }
  }
  //Bug Id:71759, end


  private String generateTransmittalId(String projectId)
  {
     ASPManager mgr = getASPManager();
     trans.clear();
     cmd = trans.addCustomFunction("GENTRANS","TRANSMITTAL_COUNTER_API.Generate_Transmittal_Id","ATTR");
     cmd.addParameter("PROJECT_ID",projectId);

     trans = mgr.perform(trans);
     return trans.getValue("GENTRANS/DATA/ATTR");
  }



  public void connectDocumentsToTrans() // connecting selected docs to the transmittal in 3rd step.
   {
      ASPManager mgr = getASPManager();
      String revisionDetails = mgr.readValue("DOCUMENT_REV_SELECTED");

      //Bug Id 81806, start
      if (mgr.isEmpty(revisionDetails) )
      {
	 revisionDetails = sDocAttr;
      }
      //Bug Id 81806, end

      StringTokenizer stRecords = new StringTokenizer(revisionDetails, ";"); 

      int noOfRecordsSelected = stRecords.countTokens();
      String recordHolder[][] = new String[noOfRecordsSelected][];
      int k = 0;
      int j = 0;
      int h = 0;

      while (stRecords.hasMoreTokens())// fill recordHolder[][]
      {
         recordHolder[k] = new String[4];
         StringTokenizer stKeys = new StringTokenizer(stRecords.nextToken(), "^"); 

         //class	class desc	No	title		sheet	Rev	state ||--\
         //                                                       RECORD DETILS COME IN THIS order from the LOV
         //0*      1          2*  3       4*     5*     6   ||--/

         j = 0;
         h = 0;
         String currentValue;
         while (stKeys.hasMoreTokens()) {
            currentValue = stKeys.nextToken();
            if (j==0||j==2||j==4||j==5) { // condition here depends on the return values from Lov.
               recordHolder[k][h++]= currentValue;
            }
            j++;
         }
         k++;
           
      }
      
      // to get new line
      trans.clear();
      cmd = trans.addEmptyCommand("ISSUE","DOC_TRANSMITTAL_ISSUE_API.New__",issueblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANSMITTAL_ID", transmittalId);
      
      trans = mgr.perform(trans);
      data = trans.getBuffer("ISSUE/DATA");
      //...........new line
      for (int n=0;n<noOfRecordsSelected;n++) {
         this.newRowIssue(recordHolder[n][0],
                          recordHolder[n][1],
                          recordHolder[n][2],
                          recordHolder[n][3],data);
      }
      
     // Bug Id: 68412, mgr.submit() removed
      
   }


  private void setBufferAsRows(ASPRowSet rowset, ASPBuffer rows)
   {
      rowset.clear();
      for (int x = 0; x < rows.countItems(); x++)
         rowset.addRow(rows.getBufferAt(x));
   }


  public void addDocsToBuffer(String checkoutPath)
  {
     ASPManager mgr = getASPManager();

     transmittalCheckoutPath = checkoutPath + (checkoutPath.endsWith("\\")?"":"\\") + mgr.translate("temp") + "\\" + mgr.translate("Document Transmittal") + "\\" + removeIllegalCharacters(this.transmittalId); //Bug Id 91417
     int noOfRows = issueset.countRows();

     if (noOfRows<1) {
        return;
     }
     issueset.first();
     for (int k=0;k<noOfRows;k++)
     {
        ASPBuffer current_doc = mgr.newASPBuffer();

        current_doc.addItem("DOC_CLASS"    , issueset.getValue("DOC_CLASS"));
        current_doc.addItem("DOC_NO"       , issueset.getValue("DOC_NO"));
        current_doc.addItem("DOC_SHEET"    , issueset.getValue("DOC_SHEET"));
        current_doc.addItem("DOC_REV"      , issueset.getValue("DOC_REV"));

        //Bug Id: 85455, start
        current_doc.addItem("TRANSMITTAL_DOWNLOAD", "YES");
        //Bug Id: 85455, end
           
        // Bug Id 78806, start
        trans.clear();
        cmd = trans.addCustomFunction("CHECK_EXIST","EDM_FILE_API.Check_Exist","ATTR");
        cmd.addParameter("DOC_CLASS", issueset.getValue("DOC_CLASS"));
        cmd.addParameter("DOC_NO",    issueset.getValue("DOC_NO"));
        cmd.addParameter("DOC_SHEET", issueset.getValue("DOC_SHEET"));
        cmd.addParameter("DOC_REV",   issueset.getValue("DOC_REV"));
        cmd.addParameter("DOC_REV",   "ORIGINAL");
        trans = mgr.perform(trans);
        
        String exist = trans.getValue("CHECK_EXIST/DATA/ATTR");
        
        if ("TRUE".equals(exist))
        {
           current_doc.addItem("FILE_ACTION"     , "TRANSMITTALDOWNLOAD"); //Bug Id 81807
        }
        else
        {
           current_doc.addItem("FILE_ACTION"     , "NONE"); 
        }
        // Bug Id 78806, end
        
        
        current_doc.addItem("FILE_NAME"       , transmittalCheckoutPath + "\\" + issueset.getValue("FILE_NAME") );
        current_doc.addItem("LAUNCH_FILE"     , "NO");
        current_doc.addItem("EXECUTE_MACRO"   , "NO");

        fileOperationBuffer.addBuffer("DATA", current_doc);
        
	//Bug Id 75085, start
	if (isStructure(issueset.getValue("DOC_CLASS"), issueset.getValue("DOC_NO")))
	{
	    ASPBuffer temp_buff = findDocumentsInStructure(issueset.getValue("DOC_CLASS"), issueset.getValue("DOC_NO"), issueset.getValue("DOC_SHEET"), issueset.getValue("DOC_REV"));
	    temp_buff.traceBuffer("STRUCTURE_DOCS");
	    if (temp_buff.countItems() > 1)
	    {
		for (int i = 1; i < temp_buff.countItems(); i++) 
		{
		    ASPBuffer structure_doc = mgr.newASPBuffer();
		    ASPBuffer temp_doc = mgr.newASPBuffer();
		    temp_doc = temp_buff.getBufferAt(i);
		    temp_doc.traceBuffer("TEMP_DOC");
          
                    // Bug Id 78806, start
                    trans.clear();
                    cmd = trans.addCustomFunction("CHECK_EXIST","EDM_FILE_API.Check_Exist","ATTR");
                    cmd.addParameter("DOC_CLASS", temp_doc.getValue("DOC_CLASS"));
                    cmd.addParameter("DOC_NO",    temp_doc.getValue("DOC_NO"));
                    cmd.addParameter("DOC_SHEET", temp_doc.getValue("DOC_SHEET"));
                    cmd.addParameter("DOC_REV",   temp_doc.getValue("DOC_REV"));
                    cmd.addParameter("DOC_REV",   "ORIGINAL");
                    trans = mgr.perform(trans);
        
                    String fileExist = trans.getValue("CHECK_EXIST/DATA/ATTR");
          
                    if ("TRUE".equals(fileExist))
                    {		
		       structure_doc.addItem("DOC_CLASS"    , temp_doc.getValue("DOC_CLASS"));
		       structure_doc.addItem("DOC_NO"       , temp_doc.getValue("DOC_NO"));
		       structure_doc.addItem("DOC_SHEET"    , temp_doc.getValue("DOC_SHEET"));
		       structure_doc.addItem("DOC_REV"      , temp_doc.getValue("DOC_REV"));

		       trans.clear();
		       cmd = trans.addCustomFunction("REP_FILENAME","Edm_File_Util_API.Generate_Docman_File_Name_","FILE_NAME");
		       cmd.addParameter("DOC_CLASS"    , temp_doc.getValue("DOC_CLASS"));
		       cmd.addParameter("DOC_NO"       , temp_doc.getValue("DOC_NO"));
		       cmd.addParameter("DOC_SHEET"    , temp_doc.getValue("DOC_SHEET"));
		       cmd.addParameter("DOC_REV"      , temp_doc.getValue("DOC_REV"));
		       cmd.addParameter("DOC_REV"      , "ORIGINAL");//DOC_TYPE

		       trans = mgr.perform(trans);

		       structure_doc.addItem("TRANSMITTAL_DOWNLOAD", "YES");
		       structure_doc.addItem("FILE_ACTION"     , "VIEW");
		       structure_doc.addItem("FILE_NAME"       , transmittalCheckoutPath + "\\" + trans.getValue("REP_FILENAME/DATA/FILE_NAME") );
		       structure_doc.addItem("LAUNCH_FILE"     , "NO");
		       structure_doc.addItem("EXECUTE_MACRO"   , "NO");

		       fileOperationBuffer.addBuffer("DATA", structure_doc);
                    }
                    // Bug Id 78806, end
		}
	    
	    }
	}
	
	//Bug Id 75085, end

        issueset.next();
     }
     //Bug Id: 68455 start
     if ("2".equals(sPublishMode)){
        ASPBuffer buf = mgr.newASPBuffer();
        //Bug Id 81807, start
        if (bOpenDownloadFolder) 
           buf.addItem("FILE_ACTION", "OPEN_FOLDER");
        else
           buf.addItem("FILE_ACTION", "DO_NOTHING");
        //Bug Id 81807, end
        buf.addItem("ACCESS_RIGHTS_CHECK", "NO");
        buf.addItem("PATH", transmittalCheckoutPath);
        fileOperationBuffer.addBuffer("DATA", buf);
     }
     //Bug Id: 68455 end
     

  }

  //Bug Id 75085, start
  public ASPBuffer findDocumentsInStructure(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();

      // build query to fetch all documents in structure..
      StringBuffer select_structure = new StringBuffer();

      select_structure.append("SELECT '");
      select_structure.append(     doc_class);
      select_structure.append(     "' DOC_CLASS, '");
      select_structure.append(     doc_no);
      select_structure.append(     "' DOC_NO, '");
      select_structure.append(     doc_sheet);
      select_structure.append(     "' DOC_SHEET, '");
      select_structure.append(     doc_rev);
      select_structure.append(     "' DOC_REV ");
      select_structure.append("FROM DUAL ");
      select_structure.append("UNION ALL ");
      select_structure.append(     "SELECT sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev ");
      select_structure.append(      "FROM doc_structure ");
      select_structure.append(      "CONNECT BY doc_class = PRIOR sub_doc_class ");
      select_structure.append(           "AND doc_no = PRIOR sub_doc_no ");
      select_structure.append(           "AND doc_sheet = PRIOR sub_doc_sheet ");
      select_structure.append(           "AND doc_rev = PRIOR sub_doc_rev ");
      select_structure.append(      "START WITH doc_class = ?");
      select_structure.append(         " AND doc_no = ?");
      select_structure.append(         " AND doc_sheet = ?");
      select_structure.append(         " AND doc_rev = ?");

      // retrieve structure..
      trans.clear();

      ASPQuery q = trans.addQuery("GET_STRUCTURE", select_structure.toString());
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      trans = mgr.perform(trans);

      ASPBuffer doc_buf = trans.getBuffer("GET_STRUCTURE");

      // remove the last INFO item from this buffer..
      doc_buf.removeItemAt(doc_buf.countItems() - 1);

      return doc_buf;
   }

  public boolean isStructure(String docClass,
                               String docNo)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("TITLESTRUCTURE", "Doc_Title_Api.Get_Structure_","ATTR");
      cmd.addParameter("DOC_CLASS", docClass);
      cmd.addParameter("DOC_NO", docNo);

      trans = mgr.perform(trans);
      double  structure_type  = trans.getNumberValue("TITLESTRUCTURE/DATA/ATTR");
      trans.clear();
      
      return ((int)structure_type==1);

   }
  //Bug Id 75085, end

  private void adddReportConnectionToTheBuffer()
  {
     ASPManager mgr = getASPManager();
     ASPBuffer current_doc = mgr.newASPBuffer();

     current_doc.addItem("DOC_CLASS"     , reportDocClass);
     current_doc.addItem("DOC_NO"        , reportDocNo);
     current_doc.addItem("DOC_SHEET"     , reportDocSheet);
     current_doc.addItem("DOC_REV"       , reportDocRev);
     current_doc.addItem("DOC_TYPE"      , "ORIGINAL");
     current_doc.addItem("FILE_ACTION"   , "CONNECTREPORT");
     current_doc.addItem("TRANSMITTAL_ID", transmittalId);

     fileOperationBuffer.addBuffer("DATA", current_doc);
  }


 private void setNextStep(String step) throws FndException
 {
     // 81808 Start
     ASPManager mgr = getASPManager();
     if ("STEP_2".equals(step)) 
     {
        if (isProjInstalled)
        {
           mgr.getASPField("PROJECT_ID").deactivateLOV();
           mgr.getASPField("SUB_PROJECT_ID").deactivateLOV();
           mgr.getASPField("ACTIVITY_ID").deactivateLOV();
        }
     }
     // 81808 End
     ctx.writeValue("CURRENT_STEP", step);
 }


 private void setPreviousStep(String step) throws FndException
 {
    // 81808 Start
    ASPManager mgr = getASPManager();
    if ("STEP_2".equals(step)) 
    {
       if (isProjInstalled)
       {
        mgr.getASPField("PROJECT_ID").deactivateLOV();
        mgr.getASPField("SUB_PROJECT_ID").deactivateLOV();
        mgr.getASPField("ACTIVITY_ID").deactivateLOV();
       }
    }
    // 81808 End
    ctx.writeValue("CURRENT_STEP", step);

       
 }



 protected void setTitle(String currentStep)
 {
      ASPManager mgr = getASPManager();
      if (currentStep.equals("STEP_1")) {
         headblk.setTitle(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDGENERALINFO: Document Transmittal Wizard - Step 1 of 4 - General Info"));
      }else if (currentStep.equals("STEP_2")) {
         headblk.setTitle(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDPROJECTINFO: Document Transmittal Wizard - Step 2 of 4 - Project Info"));
      }else if (currentStep.equals("STEP_3")) {
         issueblk.setTitle(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDDOCUMENTS: Document Transmittal Wizard - Step 3 of 4 - Documents"));
      }else if (currentStep.equals("STEP_4")) {
         //Bug Id 81807, start
         try
         {
            if (transmittalDirection.equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_out))
               headblk.setTitle(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDSENDTRANSMITTAL: Document Transmittal Wizard - Step 4 of 4 - Send/Download Transmittal"));//Bug Id 79174
            else
               headblk.setTitle(mgr.translate("DOCMAWDOCTRANSMITTALWIZARDFINISHTRANSMITTAL: Document Transmittal Wizard - Step 4 of 4 - Finish Transmittal"));
         }
         catch (FndException e)
         {
         }
         //Bug Id 81807, end
      }
         
 }



 private void setTransmittalInformations()
 {
     //get transmittals Informations
     //setvalues
     ASPManager mgr = getASPManager();
     this.transmittalId = mgr.readValue("TRANSMITTAL_ID","");
     // update informations array by infromation values
     updateInformationArray(getTransmittalInformations());
     //now update variables 
     transmittalDesc       = getDataByName("TRANSMITTAL_DESCRIPTION");
     transmittalInfo       = getDataByName("TRANSMITTAL_INFO");
     transmittalDirection  = getDataByName("TRANSMITTAL_DIRECTION");
     contactPerson         = getDataByName("CONTACT_PERSON");
     receiverType          = getDataByName("RECEIVER_TYPE");
     receiverContactPerson = getDataByName("RECEIVER_CONTACT_PERSON");
     distributionMethod    = getDataByName("DISTRIBUTION_METHOD");
     receiverAddress       = getDataByName("RECEIVER_ADDRESS");
     /*expectedSendDate      = getDataByName("EXPECTED_SEND_DATE");
     actualSentSate        = getDataByName("ACTUAL_SENT_DATE");
     expectedReturnDate    = getDataByName("EXPECTED_RETURN_DATE");
     actualReturnDate      = getDataByName("ACTUAL_RETURN_DATE");*/

     projectId             = getDataByName("PROJECT_ID");
     subProjectId          = getDataByName("SUB_PROJECT_ID");
     activityId            = getDataByName("ACTIVITY_ID");
     customerProjectId     = getDataByName("CUSTOMER_PROJECT_ID");
     //Bug Id 89221 Start
     oldCustomerProjectId  = customerProjectId;
     //Bug Id 89221 end
     customerSsubProjectId = getDataByName("CUSTOMER_SUB_PROJECT_ID");
     customerActivityId    = getDataByName("CUSTOMER_ACTIVITY_ID");
     supplierProjectId     = getDataByName("SUPPLIER_PROJECT_ID");
     supplierSubProjectId  = getDataByName("SUPPLIER_SUB_PROJECT_ID");
     supplierActivityId    = getDataByName("SUPPLIER_ACTIVITY_ID");
     // Bug Id 81808, Start
     subContractProjectId     = getDataByName("SUB_CONTRACTOR_PROJECT_ID");
     subContractSubProjectId  = getDataByName("SUB_CONTRACTOR_SUB_PROJECT_ID");
     subContractActivityId    = getDataByName("SUB_CONTRACTOR_ACTIVITY_ID");
     // Bug Id 81808, End

     updateDateValues();

 }




   public void updateDateValues()
   {
      ASPManager mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd=mgr.newASPCommand();
      trans.clear();
      String txt;
      txt = "select EXPECTED_SEND_DATE, ACTUAL_SENT_DATE, EXPECTED_RETURN_DATE, ACTUAL_RETURN_DATE from document_transmittal where TRANSMITTAL_ID=?";


      ASPQuery q = trans.addQuery("GETTRANSDATES",txt);
      q.addParameter("TRANSMITTAL_ID",this.transmittalId);
      trans = mgr.perform(trans);

      headset.addRow(trans.getBuffer("GETTRANSDATES/DATA"));
      trans.clear();

      expectedSendDate      = mgr.isEmpty(headset.getClientValue("EXPECTED_SEND_DATE"))?"":headset.getClientValue("EXPECTED_SEND_DATE");
      actualSentSate        = mgr.isEmpty(headset.getClientValue("ACTUAL_SENT_DATE"))?"":headset.getClientValue("ACTUAL_SENT_DATE");
      expectedReturnDate    = mgr.isEmpty(headset.getClientValue("EXPECTED_RETURN_DATE"))?"":headset.getClientValue("EXPECTED_RETURN_DATE");
      actualReturnDate      = mgr.isEmpty(headset.getClientValue("ACTUAL_RETURN_DATE"))?"":headset.getClientValue("ACTUAL_RETURN_DATE");
      headset.clear();
   }


   public void updateInformationArray(String infomationStr)
   {
      java.util.StringTokenizer  strTokenizer = new StringTokenizer(infomationStr, String.valueOf((char)30));
      java.util.StringTokenizer subStrTokenizer;
      int noOfData = strTokenizer.countTokens();
      informations = new String[noOfData][];
      int k = 0;
      while (strTokenizer.hasMoreElements()) {
         subStrTokenizer = new StringTokenizer( strTokenizer.nextToken(),String.valueOf((char)31));
         this.informations[k] = new String[2];
         if (subStrTokenizer.hasMoreElements()) {
            informations[k][0] = subStrTokenizer.nextToken();
         }

         if (subStrTokenizer.hasMoreElements()) {
            informations[k][1] = subStrTokenizer.nextToken();
         }
         k++;
      }
   }



   public void updateRecord()
   {
      ASPManager mgr = getASPManager();
      issueset.changeRow(); 

      //Bug Id: 68412, start
      if("TRUE".equals(fromNavigator)) {
         issuelay.setLayoutMode(this.beforeNewLay);
      }
      // Bug Id: 68412, end
   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
 
      if(val.equals("PROJECT_ID") && isProjInstalled)
      {
          trans.clear();
          // Bug Id 81808 Start
          cmd = trans.addCustomFunction("CHECK_PROJ_RECEIVE", "PROJ_TRANSMITTAL_RECEIVER_API.Check_Recievers_For_Project", "ATTR");
          cmd.addParameter("PROJECT_ID");
          trans = mgr.validate(trans);
          String check_proj_receiver = trans.getValue("CHECK_PROJ_RECEIVE/DATA/ATTR");
          StringBuffer response = new StringBuffer(""); 
          response.append(check_proj_receiver);
          response.append("^");
          mgr.responseWrite(response.toString());
          // Bug Id 81808 End
      }
      else  if(val.equals("DOC_CLASS"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("CLASSNAME", "DOC_CLASS_API.GET_NAME", "SDOCNAME");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);

         String personName         = trans.getValue("CLASSNAME/DATA/SDOCNAME");
         StringBuffer response = new StringBuffer("");
         response.append(personName);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      // Bug Id 92775, start
      else  if(val.equals("DOC_NO"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("DOCTITLE", "Doc_Title_Api.Get_Title", "ATTR");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DOC_NO");
         trans = mgr.validate(trans);

         String docTitle = trans.getValue("DOCTITLE/DATA/ATTR");
         if (mgr.isEmpty(docTitle))
            docTitle = "";
         StringBuffer response = new StringBuffer("");
         response.append(docTitle);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      else  if(val.equals("DOC_REV"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("DOCSTATE", "Doc_Issue_Api.Get_State", "ATTR");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DOC_NO");
         cmd.addParameter("DOC_SHEET");
         cmd.addParameter("DOC_REV");
         
         cmd = trans.addCustomFunction("DOCREASONFORISSUE", "Doc_Issue_Api.Get_Reason_For_Issue", "ATTR");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DOC_NO");
         cmd.addParameter("DOC_SHEET");
         cmd.addParameter("DOC_REV");
         
         trans = mgr.validate(trans);

         String docState = trans.getValue("DOCSTATE/DATA/ATTR");
         String reasonForIssue = trans.getValue("DOCREASONFORISSUE/DATA/ATTR");
         String reasonForIssueDesc = "";
         if (mgr.isEmpty(docState))
            docState = "";
         if (mgr.isEmpty(reasonForIssue))
            reasonForIssue = "";
         else
         {
            trans.clear();
            cmd = trans.addCustomFunction("DOCREASONFORISSUEDESC", "Document_Reason_For_Issue_Api.Get_Description", "ATTR");
            cmd.addParameter("REASON_FOR_ISSUE",reasonForIssue);
            trans = mgr.validate(trans);
            
            reasonForIssueDesc = trans.getValue("DOCREASONFORISSUEDESC/DATA/ATTR");
         }
         
         StringBuffer response = new StringBuffer("");
         response.append(docState);
         response.append("^");
         response.append(reasonForIssue);
         response.append("^");
         response.append(reasonForIssueDesc);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      // Bug Id 92775, end
      mgr.endResponse();
   }

  //Bug Id: 71759, start
  private String handleSingleQuote(String script){
      String returnString = "";
      for (int k=0;k<script.length();k++) {
          if ("'".equals(String.valueOf(script.charAt(k)))) {
              returnString += "\\'";
          }
          else{
              returnString +=  String.valueOf(script.charAt(k));

          }
      }
      return returnString;
  }
  //Bug Id: 71759, end
  
  // Bug Id 91417, start
  private String removeIllegalCharacters(String transmittalId)
  {
     String returnString = transmittalId;
     returnString = returnString.replaceAll("\\?", "");
     returnString = returnString.replaceAll("\"","");
     returnString = returnString.replaceAll("\\>", "");
     returnString = returnString.replaceAll("\\<", "");
     returnString = returnString.replaceAll("\\\\","");
     returnString = returnString.replaceAll("/", "");
     returnString = returnString.replaceAll("\\:", "");
     returnString = returnString.replaceAll("\\|", "");
     returnString = returnString.replaceAll("\\;", "");
     returnString = returnString.replaceAll("\\^", "");
     returnString = returnString.replaceAll("\\*", "");
     returnString = returnString.replaceAll("\\]", "");
     returnString = returnString.replaceAll("\\[", "");
     
     return returnString;
     
  }
  // Bug Id 91417, end

   // Bug Id 78806, start
   public boolean checkDocsForNoFile(String transmittalId)
   {
      ASPManager mgr = getASPManager();
      String transmittalDirectionIn = "";
      try
      {
         transmittalDirectionIn = DocmawConstants.getConstantHolder(mgr).transmittal_direction_in;
      }
      catch(FndException e)
      {
      }
              
              
      if (confimationDone || "3".equals(sPublishMode) || transmittalDirection.equals(transmittalDirectionIn))
      {
         return true;
      }
      else
      {         
         trans.clear();
         cmd = trans.addCustomFunction("CHECK_EXIST","DOC_TRANSMITTAL_ISSUE_API.Check_Docs_Has_No_Files","ATTR");
         cmd.addParameter("TRANSMITTAL_ID",transmittalId);
         trans = mgr.perform(trans);
      
         informationMessage = trans.getValue("CHECK_EXIST/DATA/ATTR");      
      
         if (mgr.isEmpty(informationMessage))
         {
            showConfirmation = false;
            return true;
         }
         else
         {
            //informationMessage = informationMessage.replace("\r","\\n");
            showConfirmation = true;
            return false;
         }
      }
   }
   // Bug Id 78806, end

   // Bug Id 93694, start
   public void updateState(String transmittalId,String event)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomCommand("CHANGESTATE", "document_transmittal_api.Set_Transmittal_State");
      cmd.addParameter("SDOCNAME",transmittalId );
      cmd.addParameter("SDOCNAME",event );
      mgr.perform(trans);
    }
   // Bug Id 93694, end
   

}
