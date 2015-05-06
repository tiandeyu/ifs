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
*  File        : DocTransmittalInfo.java
*  MODIFIED    :
*    BAKALK   2007-01-02   Created.
*    BAKALK   2007-02-19   Added a new commnad: Transmittal Wizard. Added populating record for transfered data. 
*                          Added refreshing recordset from outside.
*    BAKALK   2007-03-01   Added a new commnad: Transmittal Wizard. Added populating record for transfered data. 
*    BAKALK   2007-03-14   Rename AspField: DOC_SURVERY_STATUS to DOC_TRANS_SURVEY_STATUS.
*    BAKALK   2007-03-26   Removed DOC_TRANS_SURVEY_STATUS, new State engine replaced it.
*    BAKALK   2007-03-30   Added functionality of "Multiple comment file".
*    BAKALK   2007-04-05   "Add Acknowledgement" done
*    CHSELK   2007-04-05   Added 'DOC_TRANSMITTAL_LINE_STATUS' field.
*    BAKALK   2007-04-06   "View Comment files" done
*    BAKALK   2007-04-10   Document Transmittals: Send comment files done
*    BAKALK   2007-04-18   Handled Out Bound status.
*    BAKALK   2007-04-20   Handled In Bound status.
*    BAKALK   2007-04-23   Change the view of Report tab. Modified where conditions accordingly.  
*    BAKALK   2007-04-27   Most of Commands are validated.  
*    BAKALK   2007-05-03   Made a modification regarding above.  
*    BAKALK   2007-05-10   Call Id 143075, Removed asp field : COMMENT_FILE_ID and COMMENT_RECEIVED_ID. Added new asp field ITEM0_COMMENT_RECEIVED
*    BAKALK   2007-05-15   Call Id 140763, Removed upper case setting on aspfield NOTE .
*    BAKALK   2007-05-15   Call Id: 140755, Modified okFind() and run() so that Transmittal_id in query string can populate the rowset.
*    BAKALK   2007-05-15   Call Id: 140768, Removed Lov from fields for Customer, Supplier and Sub-Contractor.
*    BAKALK   2007-05-21   Call Id: 140769, Added new command Add document and implemented it.
*    BAKALK   2007-05-23   Call Id: 141463: Removed 'our' prefix from labels.
*    BAKALK   2007-06-22   Updating State when sending Transmittal was not correct. fixed it.
*    BAKALK   2007-06-27   Call Id: 146371: Added new command "Send Transmittal".
*    BAKALK   2007-06-29   Call Id: 146310: Modified ajust().
*    BAKALK   2007-07-05   Call Id: 146371: Modified okFindITEM2().
*    BAKALK   2007-07-23   Call Id: 146371: Modified preDefine(), change the command title Receive.
*    UPDELK   2007-07-24   Call Id: 146870: DOCMAW/DOCMAN - Transmittal and dynamic dependencies to PROJ.
*    BAKALK   2007-07-25   Call Id: 146949: Modified preDefine(), added LOV for State in head block.
*    NaLrlk   2007-08-10   XSS Correction.
*    DinHlk   2007-09-17   Call Id: 148288: Modified okFind() to add a default order by condition.   
*    BAKALK   2007-11-05   Bug Id: 68844: Made vlaue of UPDATE_STATE (hidden field) depen on updateSatate boolean.
*    BAKALK   2007-11-13   Bug Id: 68849, addNewLineInTransmittalCommentFile() is now invoked before refreshing records in run().
*    AMNALK   2008-01-16   Bug Id: 68528, Modified addAcknowledgement(). 
*    VIRALK   2008-03-03   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*    BAKALK   2008-03-10   Bug Id: 71840, Modified preDefine(), set some fields in Document block mandatory.
*    BAKALK   2008-03-19   Bug Id: 68530, Moved "Add Documents" command from Transmittal block to Document block.
*    SHTHLK   2008-04-16   Bug Id  70286, Replaced addCustomCommand() with addSecureCustomCommand()
*    BAKALK   2008-04-21   Bug Id: 71762, NEWROW is bisabled for doucments if Transmittal is not in Preliminary.
*                          sendCommentfiles now does not update the state.
*    AMNALK   2008-07-01   Bug Id: 75084, Corrected the wrong spelt label "Reciever" to "Receiver"
*    AMNALK   2008-08-06   Bug Id: 75081, Modified getDescription() by changing the display name.
*    AMNALK   2008-08-25   Bug Id: 75074, Modified preDefine(), performRefreshParent() & printContents() and 
*			   added new functions checkAllDocumentsReleased() & handleSingleQuote().
*    AMNALK   2008-09-09   Bug Id: 76248, Modified adjust(), preDefine() to make the fields readonly accordingly.
*    AMNALK   2008-09-24   Bug Id: 77029, Modified preDefine() to make ACTUAL_SENT_DATE and ACTUAL_RETURN_DATE readonly.
*    AMNALK   2008-10-28   Bug Id: 77777, Modified preDefine() to remove the Uppercase flag from field DOC_TRANSMITTAL_LINE_STATUS.
*    AMCHLK   2008-12-02   Bug Id: 78792, Added "Note" field to itemblk1 and modified some fields in itemblk2   
*    AMNALK   2008-02-16   Bug Id: 79174, Modified PreDefine(), printContents() and run(). Added new methods getSelectedTransmittalIds(), executeReport(),
*                          showReport(), getParamList(), setDefualtLanguageLayout() and receiveAcknowledgement() to support RMB Execute Report.
*    SHTHLK   2008-04-27   Bug Id 81804, Added the columns reason for Issue and Reason for Issue Description.
*    AMCHLK   2009-05-21   Bug Id: 81808, Filtered the reciver Ids according to the project  
*    AMNALK   2009-05-27   Bug Id: 81807, Modified transmittalWizard() to block the launch of the wizard for IN transmittals.
*    AMNALK   2009-06-02   Bug Id: 81806, Modified run() to load this page in SINGLE_LAYOUT.
*    DULOLK   2009-06-03   Bug Id: 81808, Filtered receiver either according to proj id or show all.
*    SHTHLK   2009-07-03   Bug Id: 84461, Set the length of TRANSMITTAL_ID to 120
*    SHTHLK   2009-08-04   Bug Id; 84284, Modified lovReceiverContactPerson to call validateReceiverContactPerson()
*    SHTHLK   2009-08-14   Bug Id; 85164, Added Lov for RECEIVER_ADDRESS.
*    VIRALK   2010-04-20   Bug Id; 88512, warning message if tryed to send/receive transmittal without documents.
*    AMCHLK   2010-04-30   Bug Id: 89680 Replaced view PERSON_INFO_LOV with PERSON_INFO_PUBLIC_LOV.
*    BAKALK   2010-06-15   Bug Id: 89221 Added validation on PROJECT_ID and added client method to override validation on the field. 
                           Implemented adding history line for customer project id being modified by validation of project Id.
*    BAKALK   2010-06-23   Bug Id: 89221, Client method validateCustomerProjectId() overriden.                           
*    AMNALK   2010-07-09   Bug Id: 91571, Set the field RECEIVER_CONTACT_PERSON to uppercase.
*    AMNALK   2010-07-11   Bug Id: 91710, Disabled the DELETE button from the headbar.
*    AMNALK   2010-09-20   Bug Id: 92775, Modified preDefine() and validate() to add new fields and validate some existing fields.
*    AMNALK   2010-09-27   Bug Id: 78806, Added new function checkDocsForNoFile() and modified showConfirmMessage()
* ----------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocTransmittalInfo extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocTransmittalInfo");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext       ctx;
   private ASPHTMLFormatter fmt;

   private ASPBlock       headblk;
   private ASPRowSet      headset;
   private ASPCommandBar  headbar;
   private ASPTable       headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock tempblk;

   private ASPBlock       itemblk0;
	private ASPRowSet      itemset0;
	private ASPCommandBar  itembar0;
	private ASPTable       itemtbl0;
	private ASPBlockLayout itemlay0;

	private ASPBlock       itemblk1;
	private ASPRowSet      itemset1;
	private ASPCommandBar  itembar1;
	private ASPTable       itemtbl1;
	private ASPBlockLayout itemlay1;

	private ASPBlock       itemblk2;
	private ASPRowSet      itemset2;
	private ASPCommandBar  itembar2;
	private ASPTable       itemtbl2;
	private ASPBlockLayout itemlay2;

	private ASPBlock  dummyblk; //Bug Id 75074

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPCommand           cmd;
   private ASPQuery             q;
   private ASPBuffer            data;
   private ASPBuffer            keys;
   private ASPBuffer            transferBuffer;

   private ASPTabContainer tabs;

   private String sHistoryMode;
   private String sFilePath;
   private String currCommentFileNo;
   private boolean bTranferToEDM;
   private boolean openWizard;
   private boolean refreshParentDone;
   private boolean bAddNewTransmittalCommentFile;
   private boolean afterAddingDocs;
   private boolean isProjInstalled = false;

   private String acknowledgementDocClass;
   private String acknowledgementDocNo;
   private String acknowledgementDocSheet;
   private String acknowledgementDocRev;

   private boolean updateState;
   private String  updateEvent;

   //Bug Id 75074, start 
   String error_message; 
   private boolean checkAllDocReleased;
   //Bug Id 75074, end

   //Bug Id 79174, start
   private ASPInfoServices inf;
   private String language;
   private String layout;
   private boolean bShowReport = false;
   //Bug Id 79174, end

   // Bug Id 81808, Start
   private String  root_path;
   // Bug Id 81808, End

    // Bug Id 88512, Start
   private String  process;
   String warn_message; 
   private boolean showConfirmConnectFiles;

    // Bug Id 88512, End
   
   // Bug Id 78806, start
   private String informationMessage;
   // Bug Id 78806, end

   //===============================================================
   // Construction
   //===============================================================
   public DocTransmittalInfo(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void  preDefine()
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
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("DOCTRANSMITTALINFOTRANSMITTALID: Transmittal Id");
      

      headblk.addField("TRANSMITTAL_DESCRIPTION").
      setSize(20).
      setMaxLength(800).
      setLabel("DOCTRANSMITTALINFOTRANSDESC: Transmittal Description");

      headblk.addField("TRANSMITTAL_INFO").
      setSize(20).
      setMaxLength(100).
      setLabel("DOCTRANSMITTALINFOTRANSMITTAL_INFO: Transmittal Info");

      headblk.addField("TRANSMITTAL_DIRECTION").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("TRANSMITTAL_DIRECTION_API").
      setLabel("DOCTRANSMITTALINFOTRANSDIR: Transmittal Direction");

      headblk.addField("STATE").
      setSize(20).
      setMaxLength(400).
      setSelectBox().
      enumerateValues("DOCUMENT_TRANSMITTAL_API.Enumerate_States__","DOCUMENT_TRANSMITTAL_API.Finite_State_Encode__").
      unsetSearchOnDbColumn().setReadOnly().
      setLabel("DOCTRANSMITTALINFOTRANSMITTAL_STATE: Transmittal State");


      
      headblk.addField("DB_STATE").
      setFunction("DOCUMENT_TRANSMITTAL_API.Finite_State_Encode__(:STATE)").
      setHidden();

      isProjInstalled = DocmawUtil.isProjInstalled(getASPManager());

      if (isProjInstalled)
      {

          headblk.addField("PROJECT_ID").
          setSize(20).
          setDynamicLOV("PROJECT").
          //Bug Id: 89221, start
          setCustomValidation("PROJECT_ID", "CUSTOMER_PROJECT_ID").
          //Bug Id: 89221, end
          //setFunction("Transmittal_Obj_Con_Api.Get_Project_Id(:TRANSMITTAL_ID)").
          setLabel("DOCTRANSMITTALINFOPROJECTID: Project Id");
    
    
          headblk.addField("SUB_PROJECT_ID").
          setSize(20).
          setDynamicLOV("SUB_PROJECT","PROJECT_ID").
          setLabel("DOCTRANSMITTALINFOSUBPROJECTID: Sub Project Id");
    
          
          headblk.addField("ACTIVITY_ID").
          setSize(20).
          setDynamicLOV("ACTIVITY_LOV1","SUB_PROJECT_ID").
          //setFunction("Transmittal_Obj_Con_Api.Get_Activity_No(:TRANSMITTAL_ID)").
          setLabel("DOCTRANSMITTALINFOACTIVITYID: Activity Id");

      }

      headblk.addField("CONTACT_PERSON").
      setSize(20).
      setDynamicLOV("PERSON_INFO_LOV").
      setLabel("DOCTRANSMITTALINFOCONTACTPERSON: Contact Person");


      headblk.addField("EXPECTED_SEND_DATE","Date").
      setSize(20).
      setMandatory(). //Bug Id 76248
      setLabel("DOCTRANSMITTALINFOEXPECTEDSENDDATE: Expected Send Date");


      headblk.addField("ACTUAL_SENT_DATE","Date").
      setSize(20).
      setReadOnly(). //Bug Id 77029
      setLabel("DOCTRANSMITTALINFOACTUALSENTDATE: actual Sent Date");

      
      headblk.addField("EXPECTED_RETURN_DATE","Date").
      setSize(20).
      setMandatory(). //Bug Id 76248
      setLabel("DOCTRANSMITTALINFOEXPECTEDACCCEPTDATE: Expected Accept Date");


      
      headblk.addField("ACTUAL_RETURN_DATE","Date").
      setSize(20).
      setReadOnly(). //Bug Id 77029
      setLabel("DOCTRANSMITTALINFOACTUALRETURNDATE: Actual Return Date");


      headblk.addField("RECEIVER_TYPE").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("RECEIVER_TYPE_API").
      setLabel("DOCTRANSMITTALINFORECEIVER: Receiver");

      
      headblk.addField("RECEIVER_ADDRESS").
      setSize(20).
      setDynamicLOV("PERSON_INFO_ADDRESS","RECEIVER_CONTACT_PERSON"). // Bug Id 81808
      setLabel("DOCTRANSMITTALINFORECEIVERADDRESS: Receiver Address");


      
      headblk.addField("RECEIVER_CONTACT_PERSON").
      setSize(20).
      setDynamicLOV("PERSON_INFO_PUBLIC_LOV"). // Bug Id 85164 // Bug Id 89680
      setUpperCase(). // Bug Id 91571
      setLabel("DOCTRANSMITTALINFORECEIVERCONTACTPERSON: Receiver Contact Person");


      headblk.addField("CUSTOMER_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOCUSTOMERPROJECTID: Customer Project Id");


      headblk.addField("CUSTOMER_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOCUSTOMERSUBPROJECTID: Customer Sub Project Id");

      
      headblk.addField("CUSTOMER_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOCUSTOMERACTIVITYID: Customer Activity Id");


      headblk.addField("SUPPLIER_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUPPLIERPROJECTID: Supplier Project Id");


      headblk.addField("SUPPLIER_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUPPLIERSUBPROJECTID: Supplier Sub Project Id");

      
      headblk.addField("SUPPLIER_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUPPLIERACTIVITYID: Supplier Activity Id");
      
      
      
      headblk.addField("SUB_CONTRACTOR_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUBCONTRACTORPROJECTID: Sub Contractor Project Id");


      headblk.addField("SUB_CONTRACTOR_SUB_PROJECT_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUBCONTRACTORSUBPROJECTID: Sub Contractor Sub Project Id");

      
      headblk.addField("SUB_CONTRACTOR_ACTIVITY_ID").
      setSize(20).
      setLabel("DOCTRANSMITTALINFOSUBCONTRACTORACTIVITYID: Sub Contractor Activity Id");


      headblk.addField("DISTRIBUTION_METHOD").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("DISTRIBUTION_METHOD_API").
      setLabel("DOCTRANSMITTALINFODISTRIBUTIONMETHOD: Distribution Method");


      headblk.addField("COMMENT_RECEIVED").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setLabel("DOCTRANSMITTALINFOCOMMENTRECEIVED: Comment Received");


      headblk.addField("CREATED_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFOCREATEDDATE: Created Date");

     

      headblk.addField("CREATED_BY").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFOCREATEDBY: Created By");

      headblk.addField("VALIDATE_RECEIVE_TRANS").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('receiveTransmittal',:TRANSMITTAL_ID,'')").
      setHidden();

      headblk.addField("VALIDATE_CLOSE_TRANS").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('closeTransmittal',:TRANSMITTAL_ID,'')").
      setHidden();

      headblk.addField("VALIDATE_SEND_COMMENT_HEAD").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('sendComment' ,:TRANSMITTAL_ID,'')").
      setHidden();

      headblk.addField("VALIDATE_SEND_TRANSMITTAL").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('send' ,:TRANSMITTAL_ID,'')").
      setHidden();
      
      //Bug Id 79174, start
      headblk.addField("VALIDATE_ADD_ACKNOWLEDGE").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Is_Ack_Added(:TRANSMITTAL_ID)").
      setHidden();
      //Bug Id 79174, end

      headblk.addField("MODIFIED_DATE","Date").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFOMODIFIED: Modified");

     

      headblk.addField("MODIFIED_BY").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFOMODIFIEDBY: Modified By");

      headblk.setView("DOCUMENT_TRANSMITTAL");
      headblk.defineCommand("DOCUMENT_TRANSMITTAL_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE); // Bug Id 91710
      headbar.addSecureCustomCommand("transmittalWizard",mgr.translate("DOCMAWDOCTRANSMITTALINFOWIZARD: Transmittal Wizard..."),"DOCUMENT_TRANSMITTAL_API.Handle_Transmittal_Wizard_Save"); //Bug Id  70286 //Bug Id 79174

      //Bug Id 79174, start
      headbar.addCustomCommand("executeReport",mgr.translate("DOCMAWDOCTRANSMITTALINFOREPORT: Execute Transmittal Report")); //Bug Id  70286
      headbar.addSecureCustomCommand("receiveAcknowledgement",mgr.translate("DOCMAWDOCTRANSMITTALINFORECEIVEACKNOWLEGEMENT: Receive Acknowledgement"),"DOC_TRANSMITTAL_ISSUE_API.Create_Transmittal_Doc"); 
      //Bug Id 79174, end

      headbar.addSecureCustomCommand("addAcknowledgement",mgr.translate("DOCMAWDOCTRANSMITTALINFOACKNOWLEGEMENT: Add Acknowledgement"),"DOC_TRANSMITTAL_ISSUE_API.Create_Transmittal_Doc"); //Bug Id  70286
      //handling status
      headbar.addSecureCustomCommand("closeTransmittal",mgr.translate("DOCMAWDOCTRANSMITTALINFOCLOSE: Close"),"document_transmittal_api.Set_Transmittal_State"); //Bug Id  70286
      headbar.addSecureCustomCommand("receiveTransmittal",mgr.translate("DOCMAWDOCTRANSMITTALINFORECEIVETRANS: Receive Transmittal"),"document_transmittal_api.Set_Transmittal_State"); //Bug Id  70286
      headbar.addSecureCustomCommand("sendAcknowledgement",mgr.translate("DOCMAWDOCTRANSMITTALINFOSENDACKNOWLEDGEMENT: Send Acknowledgement"),"document_transmittal_api.Set_Transmittal_State"); //Bug Id  70286
      headbar.addSecureCustomCommand("sendComment",mgr.translate("DOCMAWDOCTRANSMITTALINFOSENDCOMMENT: Send Comment"),"document_transmittal_api.Set_Transmittal_State");  //Bug Id  70286
 
      headbar.addSecureCustomCommand("sendTrnasmittal", mgr.translate("DOCMAWDOCTRANSMITTALINFOSENDTRANSMIT: Send Transmittal"),"document_transmittal_api.Set_Transmittal_State");  //Bug Id  70286
      

      headbar.addCommandValidConditions("transmittalWizard",  "DB_STATE", "Enable", "Preliminary");
      headbar.addCommandValidConditions("addAcknowledgement", "DB_STATE", "Enable", "Sent;Received");
      
      //headbar.addCommandValidConditions("receiveTransmittal", "DB_STATE", "Enable", "Preliminary");
      headbar.addCommandValidConditions("receiveTransmittal", "VALIDATE_RECEIVE_TRANS", "Enable", "TRUE");

      headbar.addCommandValidConditions("closeTransmittal", "VALIDATE_CLOSE_TRANS", "Enable", "TRUE");
      headbar.addCommandValidConditions("sendAcknowledgement", "DB_STATE", "Enable", "Received");
      headbar.addCommandValidConditions("sendComment", "VALIDATE_SEND_COMMENT_HEAD", "Enable", "TRUE");

      headbar.addCommandValidConditions("sendTrnasmittal", "VALIDATE_SEND_TRANSMITTAL", "Enable", "TRUE");

      //Bug Id 79174, start
      headbar.addCommandValidConditions("receiveAcknowledgement", "DB_STATE", "Enable", "Sent");
      headbar.addCommandValidConditions("addAcknowledgement", "VALIDATE_ADD_ACKNOWLEDGE", "Disable", "1");
      //Bug Id 79174, end

      //Preliminary^Received^Sent^Acknowledgement Received^Comment Received^Acknowledgement Sent^Comment Sent^Closed^';
      //tab commands
      headbar.addCustomCommand("activateDocuments", "Documents");
      headbar.addCustomCommand("activateReports", "Reports");
      headbar.addCustomCommand("activateHistory", "History");

      headbar.enableMultirowAction(); //Bug Id 75074  
      
      
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.enableRowSelect();
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.defineGroup("Main", "OBJID,OBJVERSION,TRANSMITTAL_ID,TRANSMITTAL_DESCRIPTION,TRANSMITTAL_INFO,STATE", false, true);
		headlay.defineGroup(mgr.translate("DOCMAWDOCTRANSMITTALINFOGENERALGROUP: General"), ",TRANSMITTAL_DIRECTION,CONTACT_PERSON,EXPECTED_SEND_DATE,ACTUAL_SENT_DATE,EXPECTED_RETURN_DATE,ACTUAL_RETURN_DATE,CREATED_DATE,CREATED_BY,MODIFIED_DATE,MODIFIED_BY,DISTRIBUTION_METHOD,COMMENT_RECEIVED", true, false);
		//
		headlay.defineGroup(mgr.translate("DOCMAWDOCTRANSMITTALINFORECIEVERGROUP: Receiver"), ",RECEIVER_TYPE,RECEIVER_ADDRESS,RECEIVER_CONTACT_PERSON", true, false); //Bug Id 75084: Corrected a spelling error
		
                if (isProjInstalled)
                    headlay.defineGroup(mgr.translate("DOCMAWDOCTRANSMITTALINFOPROJECTGROUP: Project"), ",PROJECT_ID,SUB_PROJECT_ID,ACTIVITY_ID,CUSTOMER_PROJECT_ID,CUSTOMER_SUB_PROJECT_ID,CUSTOMER_ACTIVITY_ID,SUPPLIER_PROJECT_ID,SUPPLIER_SUB_PROJECT_ID,SUPPLIER_ACTIVITY_ID,SUB_CONTRACTOR_PROJECT_ID,SUB_CONTRACTOR_SUB_PROJECT_ID,SUB_CONTRACTOR_ACTIVITY_ID", true, false);
                else
                    headlay.defineGroup(mgr.translate("DOCMAWDOCTRANSMITTALINFOPROJECTGROUP: Project"), ",CUSTOMER_PROJECT_ID,CUSTOMER_SUB_PROJECT_ID,CUSTOMER_ACTIVITY_ID,SUPPLIER_PROJECT_ID,SUPPLIER_SUB_PROJECT_ID,SUPPLIER_ACTIVITY_ID,SUB_CONTRACTOR_PROJECT_ID,SUB_CONTRACTOR_SUB_PROJECT_ID,SUB_CONTRACTOR_ACTIVITY_ID", true, false);
      //======================== Documents ============

      itemblk0 = mgr.newASPBlock("ITEM0");

		itemblk0.disableDocMan();

		itemblk0.addField("ITEM0_OBJID").
		setHidden().
		setDbName("OBJID");
      
      itemblk0.addField("ITEM0_OBJVERSION").
		setHidden().
		setDbName("OBJVERSION");

      itemblk0.addField("ITEM0_TRANSMITTAL_ID").
		setDbName("TRANSMITTAL_ID").
		setHidden();

      itemblk0.addField("TRANSMITTAL_LINE_NO","Number").
      setSize(20).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFOTRANSMITTALLINENO: Transmittal Line No");

      itemblk0.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setMandatory().                  //Bug Id: 71840
      setCustomValidation("DOC_CLASS","SDOCNAME").
      setLabel("DOCTRANSMITTALINFODOCCLASS: Doc Class");

      itemblk0.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCTRANSMITTALINFODOCCLASSDESC: Doc Class Desc");


      itemblk0.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setMandatory().                  //Bug Id: 71840
      setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO").
      setCustomValidation("DOC_CLASS,DOC_NO","DOC_TITLE"). // Bug Id 92775  
      setLabel("DOCTRANSMITTALINFODOCNO: Document Number");

		itemblk0.addField("DOC_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO").
      setMandatory().                  //Bug Id: 71840
		setLabel("DOCTRANSMITTALINFODOCSHEET: Document Sheet");

      itemblk0.addField("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setMandatory().                  //Bug Id: 71840
      setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","DOC_STATE"). // Bug Id 92775          
      setLabel("DOCTRANSMITTALINFODOCREV: Document Revision");
      
      // Bug Id 92775, start
      itemblk0.addField("DOC_TITLE").
      setSize(60).
      setMaxLength(250).
      setReadOnly().
      setLabel("DOCTRANSMITTALINFODOCTITLE: Title").
      setFunction("Doc_Title_Api.Get_Title(:DOC_CLASS,:DOC_NO)");      
      
      itemblk0.addField("DOC_STATE").
      setSize(20).
      setMaxLength(253).
      setReadOnly().        
      setLabel("DOCTRANSMITTALINFODOCSTATE: Document State").
      setFunction("Doc_Issue_Api.Get_State(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");
      // Bug Id 92775, end
      
       //Bug Id 81804, Start
       itemblk0.addField("REASON_FOR_ISSUE").
       setSize(20).
       setReadOnly().
       setLabel("DOCTRANSMITTALINFODOCREASONISSUE: Reason For Issue").
       setFunction("Doc_Issue_Api.Get_Reason_For_Issue(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)");

       itemblk0.addField("REASON_FOR_ISSUE_DESC").
       setSize(20).
       setReadOnly().
       setLabel("DOCTRANSMITTALINFODOCREASONISSUEDESC: Description").
       setFunction("document_reason_for_issue_api.Get_Description(Doc_Issue_Api.Get_Reason_For_Issue(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      //Bug Id 81804, End

      itemblk0.addField("FILE_NO","Number").
      setFunction("''").
      setHidden();
      

      itemblk0.addField("CHECKED_IN_SIGN").
      setFunction("Edm_File_Api.Get_Checked_In_Sign(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')").
      setHidden();


      itemblk0.addField("CUSTOMER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCTRANSMITTALINFOCUSTOMERDOCCLASS: Customer Doc Class");


      itemblk0.addField("CUSTOMER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOCUSTOMERDOCNO: Customer Document Number");

		itemblk0.addField("CUSTOMER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOCUSTOMERDOCSHEET: Customer Document Sheet");

		itemblk0.addField("CUSTOMER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOCUSTOMERDOCREV: Customer Document Revision");


      itemblk0.addField("SUPPLIER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCTRANSMITTALINFOSUPPLIERDOCCLASS: Supplier Doc Class");


      itemblk0.addField("SUPPLIER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUPPLIERDOCNO: Supplier Document Number");

		itemblk0.addField("SUPPLIER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUPPLIERDOCSHEET: Supplier Document Sheet");

		itemblk0.addField("SUPPLIER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUPPLIERDOCREV: Supplier Document Revision");



      itemblk0.addField("SUB_CONTRACTOR_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setLabel("DOCTRANSMITTALINFOSUBCONTRACTORDOCCLASS: Sub Contractor Doc Class");


      itemblk0.addField("SUB_CONTRACTOR_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUBCONTRACTORDOCNO: Sub Contractor Document Number");

		itemblk0.addField("SUB_CONTRACTOR_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUBCONTRACTORDOCSHEET: Sub Contractor Document Sheet");

		itemblk0.addField("SUB_CONTRACTOR_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOSUBCONTRACTORDOCREV: Sub Contractor Document Revision");

       
      itemblk0.addField("NOTE").
		setSize(20).
		setMaxLength(6).
		setLabel("DOCTRANSMITTALINFONOTE: Note");


      itemblk0.addField("COMMENT_RECEIVED_DATE","Date").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCTRANSMITTALINFOCOMMENTRECEIVEDDATE: Comment Received Date");

		

      itemblk0.addField("ITEM0_COMMENT_RECEIVED").
      setDbName("COMMENT_RECEIVED").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setLabel("DOCTRANSMITTALINFOCOMMENTRECEIVEDISSUE: Comment Received");

      

      

      itemblk0.addField("GETVIEWACCES").
		setHidden().
		setFunction("DOC_ISSUE_API.Get_View_Access_(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :DOC_REV)");
      if (isProjInstalled)
      {
          itemblk0.addField("DOC_TRANSMITTAL_LINE_STATUS").
              setSize(20).
              setMaxLength(6).
              setDynamicLOV("TRANSMITTAL_LINE_STATE","PROJECT_ID").
              setLabel("DOCUMENTTRANSMITTALISSUESTRANSMITTALLINESTATE: Doc Transmittal Line Status");
      } else {

          itemblk0.addField("DOC_TRANSMITTAL_LINE_STATUS").
                   setSize(20).
                   setMaxLength(6).
                   setDynamicLOV("TRANSMITTAL_LINE_STATE").
                   setLabel("DOCUMENTTRANSMITTALISSUESTRANSMITTALLINESTATE: Doc Transmittal Line Status");

      }



      itemblk0.addField("TRANSMITTAL_STATE").
      setFunction("DOCUMENT_TRANSMITTAL_API.Finite_State_Encode__(DOCUMENT_TRANSMITTAL_API.Get_State(:ITEM0_TRANSMITTAL_ID))").
      setHidden();

      itemblk0.addField("VALIDATE_ADD_COMMENT").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('addCommentfiles' ,:ITEM0_TRANSMITTAL_ID,:TRANSMITTAL_LINE_NO)").
      setHidden();

      itemblk0.addField("VALIDATE_VIEW_COMMENT").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('viewCommentfiles' ,:ITEM0_TRANSMITTAL_ID,:TRANSMITTAL_LINE_NO)").
      setHidden();

      itemblk0.addField("VALIDATE_SEND_COMMENT").
      setFunction("DOC_TRANSMITTAL_ISSUE_API.Validate_Command('sendCommentfiles' ,:ITEM0_TRANSMITTAL_ID,:TRANSMITTAL_LINE_NO)").
      setHidden();





      itemblk0.setView("DOC_TRANSMITTAL_ISSUE"); 
      itemblk0.defineCommand("DOC_TRANSMITTAL_ISSUE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableMultirowAction();
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      

      itembar0.addCustomCommand("docInfoFromIssues",mgr.translate("DOCTRANSMITTALINFODOCINFO: Document Info..."));
      itembar0.addSecureCustomCommand("viewOriginal",mgr.translate("DOCTRANSMITTALINFOVIEW: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      
     
      itembar0.addSecureCustomCommand("addCommentfiles",mgr.translate("DOCTRANSMITTALINFOADDCOMMENTFILES: Add"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      itembar0.addSecureCustomCommand("viewCommentfiles",mgr.translate("DOCTRANSMITTALINFOVIEWCOMMENTFILES: View"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      itembar0.addSecureCustomCommand("sendCommentfiles",mgr.translate("DOCTRANSMITTALINFOSENDCOMMENTFILES: Send by E-mail"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      //Bug Id: 68530, start
      itembar0.addSecureCustomCommand("addDocuments", mgr.translate("DOCMAWDOCTRANSMITTALINFOADDDOCS: Add Documents..."),"DOC_TRANSMITTAL_ISSUE_API.New__");    //Bug Id 70286
      //itembar0.addCommandValidConditions("addDocuments",       "TRANSMITTAL_STATE", "Enable", "Preliminary");
      itembar0.forceEnableMultiActionCommand("addDocuments");
      //Bug Id: 68530, end

      
      //itembar0.addCommandValidConditions("addCommentfiles", "TRANSMITTAL_STATE", "Enable", "Acknowledgement Received;Acknowledgement Sent");
      //itembar0.addCommandValidConditions("addCommentfiles", "TRANSMITTAL", "Enable", "Acknowledgement Received;Acknowledgement Sent");
      itembar0.addCommandValidConditions("addCommentfiles", "VALIDATE_ADD_COMMENT", "Enable", "TRUE");

      itembar0.addCustomCommandGroup("COMMENTFILES", mgr.translate("DOCMAWDOCTRANSMITTALINFOCOMMENTFILES: Comment Files"));
      itembar0.setCustomCommandGroup("addCommentfiles", "COMMENTFILES");
      itembar0.setCustomCommandGroup("viewCommentfiles", "COMMENTFILES");
      itembar0.setCustomCommandGroup("sendCommentfiles", "COMMENTFILES");

      itembar0.addCommandValidConditions("sendCommentfiles", "VALIDATE_SEND_COMMENT", "Enable", "TRUE");
      itembar0.addCommandValidConditions("viewCommentfiles", "VALIDATE_VIEW_COMMENT", "Enable", "TRUE");


      itembar0.removeFromMultirowAction("viewOriginal");

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.enableRowSelect();
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      //================= Reports 

      itemblk1 = mgr.newASPBlock("ITEM1");

		itemblk1.disableDocMan();

		itemblk1.addField("ITEM1_OBJID").
		setHidden().
		setDbName("OBJID");
      
      itemblk1.addField("ITEM1_OBJVERSION").
		setHidden().
		setDbName("OBJVERSION");

      // ITEM1_

      itemblk1.addField("ITEM1_TRANSMITTAL_ID").
		setDbName("TRANSMITTAL_ID").
		setHidden();

      itemblk1.addField("ITEM1_DOC_CLASS").
      setDbName("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setReadOnly(). //Bug Id 76248
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("ITEM1_DOC_CLASS","ITEM1_SDOCNAME").
      setLabel("DOCTRANSMITTALINFODOCCLASS: Doc Class");

      itemblk1.addField("ITEM1_SDOCNAME").
      setSize(21).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCTRANSMITTALINFODOCCLASSDESC2: Doc Class Desc");


      itemblk1.addField("ITEM1_DOC_NO").
      setDbName("DOC_NO").
		setSize(21).
		setMaxLength(121).
		setUpperCase().
		setReadOnly(). //Bug Id 76248
		setDynamicLOV("DOC_TITLE","ITEM1_DOC_CLASS DOC_CLASS").
      setSecureHyperlink("DocIssue.page", "ITEM1_DOC_CLASS DOC_CLASS,ITEM1_DOC_NO DOC_NO").
		setLabel("DOCTRANSMITTALINFODOCNO: Document Number");

		itemblk1.addField("ITEM1_DOC_SHEET").
      setDbName("DOC_SHEET").
		setSize(21).
		setMaxLength(11).
		setUpperCase().
		setReadOnly(). //Bug Id 76248
		setDynamicLOV("DOC_ISSUE_LOV1","ITEM1_DOC_CLASS DOC_CLASS,ITEM1_DOC_NO DOC_NO").
		setLabel("DOCTRANSMITTALINFODOCSHEET: Document Sheet");

		itemblk1.addField("ITEM1_DOC_REV").
      setDbName("DOC_REV").
		setSize(21).
		setMaxLength(6).
		setUpperCase().
		setReadOnly(). //Bug Id 76248
		setDynamicLOV("DOC_ISSUE","ITEM1_DOC_CLASS DOC_CLASS,ITEM1_DOC_NO DOC_NO,ITEM1_DOC_SHEET DOC_SHEET").
		setLabel("DOCTRANSMITTALINFODOCREV: Document Revision");

      itemblk1.addField("REPORT_CHECKED_IN_SIGN").
      setFunction("Edm_File_Api.Get_Checked_In_Sign(:ITEM1_DOC_CLASS,:ITEM1_DOC_NO,:ITEM1_DOC_SHEET,:ITEM1_DOC_REV,'ORIGINAL')").
      setHidden();
      
      //Bug Id 78792 Strat
      itemblk1.addField("NOTE_ITEMBLK1").
      setLabel("DOCTRANSMITTALINFOREPNOTE: Note").
      setDbName("NOTE");
      //Bug Id 78792 End
      
      
      itemblk1.setView("DOC_TRANSMITTAL_ISSUE"); 
      itemblk1.defineCommand("DOC_TRANSMITTAL_ISSUE_API","Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.enableMultirowAction();

      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
		itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");

      itembar1.addCustomCommand("docInfoFromReports",mgr.translate("DOCTRANSMITTALINFODOCINFO: Document Info..."));
      itembar1.addSecureCustomCommand("viewReport",mgr.translate("DOCTRANSMITTALINFOVIEWREP: View Report"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      itembar1.removeFromMultirowAction("viewReport");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.enableRowSelect();
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(2);
      itemlay1.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      // end of Reports

      //================== History
      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.disableDocMan();

      itemblk2.addField("ITEM2_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk2.addField("ITEM2_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk2.addField("ITEM2_TRANSMITTAL_ID").
		setDbName("TRANSMITTAL_ID").
		setHidden();

      itemblk2.addField("HISTORY_LOG_LINE_NO","Number").
      setSize(20).
      setReadOnly(). // Bug Id 78792
      setLabel("DOCUMENTTRANSMITTALHISTORYHISTORYLOGLINENO: History Log Line No");

      itemblk2.addField("DOC_TRANS_HISTORY_CAT").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setSelectBox().
      enumerateValues("DOC_TRANS_HISTORY_CAT_API").
      setLabel("DOCUMENTTRANSMITTALHISTORYINFOCATEGORY: Info Category");


      itemblk2.addField("STATUS").
      setSize(20).
      setReadOnly(). // Bug Id 78792
      setMaxLength(400).
      setLabel("DOCUMENTTRANSMITTALHISTORY: Status");

      itemblk2.addField("ITEM2_NOTE").
      setDbName("NOTE").
      setSize(20).
      setMaxLength(400).
      setLabel("DOCUMENTTRANSMITTALHISTORYSTATUSNOTE: Note");

      itemblk2.addField("ITEM2_CREATED_BY").
      setDbName("CREATED_BY").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setLabel("DOCUMENTTRANSMITTALHISTORYCREATEDBY: Created By");
      
      
      
      itemblk2.addField("ITEM2_CREATED_DATE","Date").
      setDbName("CREATED_DATE").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setLabel("DOCUMENTTRANSMITTALHISTORYCREATEDDATE: Created Date");

      
      itemblk2.setView("DOC_TRANSMITTAL_HISTORY");
      itemblk2.defineCommand("DOC_TRANSMITTAL_HISTORY_API","New__"); //Bug Id 76248
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
		
      
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DocTransmittalInfohistory: Document Transmittal History"));
      itemtbl2.enableRowSelect();
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);


      //end of history

      //Bug Id 75074, start
      //
      //  DUMMY BLOCK
      //

      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("DUMMY2");
      
      //Bug Id 75074, end

      //Bug Id 79174, start
      // report 
      ASPBlock reportblk = mgr.newASPBlock("REPORT");
 
      reportblk.addField("REPORT_ID");
      reportblk.addField("CLIENT_VALUES0");
      reportblk.addField("CLIENT_VALUES1");
      reportblk.addField("ENUMUSERWHERE");
      reportblk.addField("PROPERTIES_LIST_OUT");
      reportblk.addField("RESULT_KEY");

      inf = mgr.newASPInfoServices();
      inf.addFields(); 
      //Bug Id 79174, end


      //tab container
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCTRANSMITTALINFODOCUMENTS: Documents"), "javascript:commandSet('HEAD.activateDocuments','')");
      tabs.addTab(mgr.translate("DOCTRANSMITTALINFOREPORTS: Reports"), "javascript:commandSet('HEAD.activateReports','')");
      tabs.addTab(mgr.translate("DOCTRANSMITTALINFOHISTORY: History"), "javascript:commandSet('HEAD.activateHistory','')");

   }//end of predefine


   public void addDocuments()
   {

      ASPManager mgr = getASPManager();
      int currRow;

      if (headlay.isSingleLayout())
      {
         currRow = headset.getCurrentRowNo();
      }
      else
      {
         currRow = headset.getRowSelected();
      }

      headset.goTo(currRow);
      //Bug Id: 68530, start
      if (!"Preliminary".equals(headset.getValue("DB_STATE"))) {
          mgr.showAlert("DOCMAWDOCTRANSMITTALINFODOCCANADDPRELI: Documents can only be added to Transmittals in Preliminary state.");
      }
      else{
          String transmittalId     = headset.getValue("TRANSMITTAL_ID");
          String url = mgr.getURL();
          keys  = headset.getRows("TRANSMITTAL_ID");
          mgr.transferDataTo("DocIssue.page?CONNECT_TO_TRANS=YES&TRANSMITTAL_ID="+mgr.URLEncode(transmittalId)+"&CUR_ROW="+mgr.URLEncode(currRow +"")+"&SEND_URL="+mgr.URLEncode(url),keys);
      }
      //Bug Id: 68530, end

      

   }


   public void activateDocuments()
	{
		tabs.setActiveTab(1);
		okFindITEM0();
	}

   public void activateReports()
	{
		tabs.setActiveTab(2);
		okFindITEM1();
	}


   public void addCommentfiles()
   {
      this.updateState = true;
      this.updateEvent = "ReceiveComment";
      commentFileAction(true,"ADDCOMMENT");
   }



   public void viewCommentfiles()
   {
      commentFileAction(false,"VIEWCOMMENT");
   }


   public void sendCommentfiles()
   {
      //Bug Id: 71762
      // this.updateState = true;
      //this.updateEvent = "SendComment";
      commentFileAction(false,"SENDCOMMENT");
   }


   public void sendComment()// update state 
   {
      this.updateState = true;
      this.updateEvent = "SendComment";
      performRefreshParent();
   }



   public void commentFileAction(boolean createNewline, String fleAction)
   {
      ASPManager mgr = getASPManager();

      bAddNewTransmittalCommentFile = createNewline; // to create new line in Transmittal_Comment_File if not exist.

      if (!isValidColumnValue("GETVIEWACCES", "TRUE", true))
      {
         mgr.showAlert("DOCMAWDOCISSUENOCHECKINACCESSFORCOMMENT: You must have view access to be able to check the comment file in.");
         return;
      }

      
      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("FILE_ACTION", fleAction); //
		buff.addItem("DOC_TYPE", "REDLINE");
      //buff.addItem("FILE_NO", ""+getCommentFileNo());
		buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));

      currCommentFileNo = "" + getCommentFileNo();

      setFileNoInCurRow(new Integer(currCommentFileNo).intValue());
      
		transferToEdmMacro(buff,itemlay0,itemset0,"DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,FILE_NO");
   }


   public void addNewLineInTransmittalCommentFile()
   { 
      ASPManager mgr = getASPManager();
      if (itemlay0.isSingleLayout())
		{
			itemset0.selectRow();
		}
		else
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			
		}
      String attr = "TRANSMITTAL_ID" + String.valueOf((char)31) + headset.getValue("TRANSMITTAL_ID")      + String.valueOf((char)30)
                    + "DOC_CLASS"    + String.valueOf((char)31) + itemset0.getRow().getValue("DOC_CLASS") + String.valueOf((char)30)
                    + "DOC_NO"       + String.valueOf((char)31) + itemset0.getRow().getValue("DOC_NO")    + String.valueOf((char)30)
                    + "DOC_SHEET"    + String.valueOf((char)31) + itemset0.getRow().getValue("DOC_SHEET") + String.valueOf((char)30)
                    + "DOC_REV"      + String.valueOf((char)31) + itemset0.getRow().getValue("DOC_REV")   + String.valueOf((char)30)
                    + "DOC_TYPE"     + String.valueOf((char)31) + "REDLINE"                               + String.valueOf((char)30)
                    + "FILE_NO"      + String.valueOf((char)31) + currCommentFileNo                       + String.valueOf((char)30) ;

      trans.clear();

      cmd = trans.addCustomCommand("CRETENEW", "Transmittal_Comment_File_Api.Create_Transmittal_Comment ");
      cmd.addParameter("DOC_CLASS",attr);
      mgr.perform(trans);

   }


   public int getCommentFileNo()
   {
      ASPManager mgr = getASPManager();
      
      if (itemlay0.isSingleLayout())
		{
			itemset0.selectRow();
		}
		else
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			
		}
      String transmittalId =  headset.getValue("TRANSMITTAL_ID");

      String docClass = itemset0.getRow().getValue("DOC_CLASS");
      String docNo    = itemset0.getRow().getValue("DOC_NO");
      String docSheet = itemset0.getRow().getValue("DOC_SHEET");
      String docRev   = itemset0.getRow().getValue("DOC_REV");

      trans.clear();

      cmd = trans.addCustomFunction("GETNUMBER", "Transmittal_Comment_File_Api.Get_File_No", "FILE_NO");
      cmd.addParameter("DOC_CLASS",docClass);
      cmd.addParameter("DOC_NO",docNo);
      cmd.addParameter("DOC_SHEET",docSheet);
      cmd.addParameter("DOC_REV",docRev);
      cmd.addParameter("DOC_REV","REDLINE"); //for DOC_TYPE
      cmd.addParameter("TRANSMITTAL_ID",transmittalId );

      trans = mgr.perform(trans);
      double fileNo =  trans.getNumberValue("GETNUMBER/DATA/FILE_NO");

      if (itemlay0.isMultirowLayout())
		{
			itemset0.setFilterOff();
		}
      int intFileNo = new Double(fileNo).intValue();
      
      return intFileNo;
   }


   public void activateHistory()
	{
		tabs.setActiveTab(3);
		okFindITEM2();
	}

   public void transmittalWizard() throws FndException //Bug Id 81807, throws FndException
   {
      ASPManager mgr = getASPManager();

      //Bug Id 81807, start
      if (headlay.isMultirowLayout()) 
      {
         headset.storeSelections(); //this command does not support for multi row selections
         headset.goTo (headset.getRowSelected());
      }
      else
         headset.selectRow();

      if (headset.getRow().getValue("TRANSMITTAL_DIRECTION").equals(DocmawConstants.getConstantHolder(mgr).transmittal_direction_in)) 
      {
         throw new FndException(mgr.translate("DOCMAWDOCTRNINFOWIZARD: Wizard can not be launched for 'IN' transmittals."));
      }
      //Bug Id 81807, end

      sFilePath  = "DocTransmittalWizard.page?FROM_NAV=FALSE";
      sFilePath += "&TRANSMITTAL_ID="+mgr.URLEncode(getCurrTransmittaId());
      openWizard = true;
      updateState = true;
      updateEvent = "Send";
      checkAllDocReleased = false; //Bug Id 75074
   }

   private String getCurrTransmittaId()
   {
       

      if (headlay.isMultirowLayout()) {
         headset.storeSelections(); //this comman not supporting for multi row selections
         headset.goTo (headset.getRowSelected());
      }
      else
         headset.selectRow();
      return headset.getRow().getValue("TRANSMITTAL_ID");
   }

   public void addAcknowledgement()
   {
      ASPManager mgr = getASPManager();
      this.updateState = false;
      //create document and connect to the Transmittal
      createNewDocForAcknowledgement();

      //transfer to EdmMacro to checkin the Acknowledgement
      String action      = "CHECKIN";
      String doc_type    = "ORIGINAL";
      String same_action = "NO";

      ASPBuffer buff = mgr.newASPBuffer();
      buff.addItem("DOC_TYPE",doc_type);
      buff.addItem("FILE_ACTION",action);
      buff.addItem("SAME_ACTION_TO_ALL",same_action);
      //file action done

      ASPBuffer        data = mgr.newASPBuffer();;
      ASPBuffer current_doc = mgr.newASPBuffer();

      current_doc.addItem("DOC_CLASS"    , acknowledgementDocClass);
      current_doc.addItem("DOC_NO"       , acknowledgementDocNo);
      current_doc.addItem("DOC_SHEET"    , acknowledgementDocSheet);
      current_doc.addItem("DOC_REV"      , acknowledgementDocRev);
      current_doc.addItem("DOC_TYPE"     , "ORIGINAL");

      data.addBuffer("DATA", current_doc);

      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page",buff,data);
      bTranferToEDM = true;

      bAddNewTransmittalCommentFile = false; //Bug Id 68528

   }//end of addAcknowledgement

   //Bug Id 79174, start
   private String getSelectedTransmittalIds()
   {
      String sTransmittalIds = "";
      int no_rows_execute_on = 0;
      if (headlay.isMultirowLayout()) 
      {
         headset.selectRows();
         headset.storeSelections();
         headset.setFilterOn();
         no_rows_execute_on = headset.countRows();
      }
      else
      {
         no_rows_execute_on = 1;
      }
      for (int k = 0; k < no_rows_execute_on; k++)
      {
         sTransmittalIds += headset.getRow().getValue("TRANSMITTAL_ID");
         sTransmittalIds += ";";
         if (headlay.isMultirowLayout())
            headset.next();
      }
      if (headlay.isMultirowLayout())
            headset.setFilterOff();

      return sTransmittalIds;
   }

   public void executeReport()
   {
      ASPManager mgr = getASPManager();
      sFilePath = "DocTransmittalInfo.page?EXECUTE_REPORT=TRUE&TRANSMITTAL_ID=" + getSelectedTransmittalIds();
      
      bShowReport = true;
   }

   public void showReport()
   {
      ASPManager mgr = getASPManager();
      inf = mgr.newASPInfoServices();

      String TransmittalId = mgr.getQueryStringValue("TRANSMITTAL_ID");

      String reportId = "DOC_TRANSMITTAL_INFO_REP";

      String[] parameterColumns = {"TRANSMITTAL_ID","FROM_WIZARD"};

      String[] parameterValues  = {TransmittalId,"TRUE"};   

      String report_attr = inf.itemValueCreate("REPORT_ID", reportId)+
                           inf.itemValueCreate("LAYOUT_NAME", "")+
                           inf.itemValueCreate("LANG_CODE", "");

      this.setDefualtLanguageLayout(reportId);

      String parameter_attr = getParamList(parameterColumns,parameterValues);

      String result_key = inf.onlineReportOrder(report_attr, parameter_attr);

      String pdf_job_id = inf.createPdfReport( result_key, this.layout, this.language );

      String where_condition = "PRINT_JOB_ID=?";
      String where_parameters = "PRINT_JOB_ID^S^IN^"+pdf_job_id;

      String temp = "";
      int a = 0;
      do{
         temp = inf.getPDFContents(where_condition, where_parameters);
         a ++;
      }while(mgr.isEmpty(temp));

      debug("binary data \n" + temp);

      inf.sendPDFContents(where_condition,where_parameters);
   }


   public String getParamList(String[] column,String[] value)
   {
      String paramStr ="";
      for (int a = 0 ; a < column.length; a++ ) 
      {
         paramStr += column[a];
         paramStr += (char)31;
         paramStr += value[a];
         paramStr += (char)30;
      }


      return paramStr;
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

   public void receiveAcknowledgement()
   {
      this.updateState = true;
      this.updateEvent = "ReceiveAcknowledgement";
      performRefreshParent();
   }
   //Bug Id 79174, end


   public void closeTransmittal()
   {
      this.updateState = true;
      this.updateEvent = "Close";
      performRefreshParent();
   }



   public void receiveTransmittal()
   {
      //this.updateState = true;
      //this.updateEvent = "Receive";
      //performRefreshParent();
      //Bug id 88512 Start these values will be assigned in run() 
      ctx.writeValue("PROCESS", "Receive");
      showConfirmMessage();
     //Bug id 88512 End
       
   }



   public void sendAcknowledgement()
   {
      this.updateState = true;
      this.updateEvent = "SendAcknowledgement";
      performRefreshParent();
   }


   private void createNewDocForAcknowledgement()
   {
      
      ASPManager mgr = getASPManager();

      String  titleForNewDoc = mgr.translate("DOCMAWDOCTRANSMITTALWIZARDADDACKNOWLEDGEMENT: Transmittal Acknowledgement");
      String  attr           = "TRANSMITTAL_ID" + String.valueOf((char)31) + this.getCurrTransmittaId() + String.valueOf((char)30);
              attr          += "TRANSMITTAL_DOCUMENT_TYPE" + String.valueOf((char)31) + "ACKNOWLEDGEMENT" + String.valueOf((char)30);

      trans.clear();

      cmd =  trans.addCustomCommand("NEWDOC","DOC_TRANSMITTAL_ISSUE_API.Create_Transmittal_Doc");
      cmd.addParameter("DOC_CLASS");
      cmd.addParameter("DOC_NO");
      cmd.addParameter("DOC_SHEET");
      cmd.addParameter("DOC_REV");
      cmd.addParameter("CHECKED_IN_SIGN",titleForNewDoc); //title
      cmd.addParameter("CHECKED_IN_SIGN",attr);           //attr

      trans = mgr.perform(trans);
      
      acknowledgementDocClass = trans.getValue("NEWDOC/DATA/DOC_CLASS");
      acknowledgementDocNo    = trans.getValue("NEWDOC/DATA/DOC_NO");
      acknowledgementDocSheet = trans.getValue("NEWDOC/DATA/DOC_SHEET");
      acknowledgementDocRev   = trans.getValue("NEWDOC/DATA/DOC_REV");
   }

  


   public void okFindITEM0()
	{
		ASPManager mgr = getASPManager();
		int k = tabs.getActiveTab();

		if (k == 1)
		{
			if (headset.countRows() == 0)
				return;
			trans.clear();
			q = trans.addEmptyQuery(itemblk0);
			q.addWhereCondition("TRANSMITTAL_ID = ? AND TRANSMITTAL_DOCUMENT_TYPE != 'REPORT'");
			q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
			q.includeMeta("ALL");
			int headrowno = headset.getCurrentRowNo();
			mgr.submit(trans);
			headset.goTo(headrowno);
		}
	}


   public void countFindITEM0()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk0);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("TRANSMITTAL_ID = ?");
		q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
		mgr.submit(trans);
		itemlay0.setCountValue(toInt(itemset0.getValue("N")));
		itemset0.clear();

      //------------

      //------------
	}


   public void countFindITEM1()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk1);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("TRANSMITTAL_ID = ?");
		q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
		mgr.submit(trans);
		itemlay1.setCountValue(toInt(itemset0.getValue("N")));
		itemset1.clear();
	}


   public void countFindITEM2()
	{
		ASPManager mgr = getASPManager();

		q = trans.addQuery(itemblk2);
		q.setSelectList("to_char(count(*)) N");
		q.addWhereCondition("TRANSMITTAL_ID = ?");
		q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
		mgr.submit(trans);
		itemlay2.setCountValue(toInt(itemset0.getValue("N")));
		itemset2.clear();
	}



   public void okFindITEM1()
	{
		ASPManager mgr = getASPManager();
		int k = tabs.getActiveTab();

		if (k == 2)
		{
			if (headset.countRows() == 0)
				return;
			trans.clear();
			q = trans.addEmptyQuery(itemblk1);
			q.addWhereCondition("TRANSMITTAL_ID = ? AND TRANSMITTAL_DOCUMENT_TYPE = 'REPORT'");
			q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
			q.includeMeta("ALL");
			int headrowno = headset.getCurrentRowNo();
			mgr.submit(trans);
			headset.goTo(headrowno);
		}
	}


   public void okFindITEM2()
	{
		ASPManager mgr = getASPManager();
		int k = tabs.getActiveTab();

		if (k == 3)
		{
			if (headset.countRows() == 0)
			 return;
			trans.clear();
			q = trans.addEmptyQuery(itemblk2);
			q.addWhereCondition("TRANSMITTAL_ID = ?");
			q.addParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
			
         //----------

         if (mgr.dataTransfered())
         {
           //ASPBuffer buff = mgr.getTransferedData();
           q.addOrCondition(transferBuffer);
         }
         else
         { 
            String whereCondition;
            int nHistoryMode = new Integer(sHistoryMode).intValue();

            switch (nHistoryMode)
		      {
		        case 1:
			        whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'TRANSMITTAL_CREATED'";
                 break;
		        case 2 :
			        //whereCondition =  " DOC_TRANS_HISTORY_CAT_DB IN ('TRANSMITTAL_SENT','TRANSMITTAL_RECEIVED')";//
                 whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'STATE_CHANGED' AND DOCUMENT_TRANSMITTAL_API.Finite_State_Encode__(STATUS)='Preliminary'";//
                 
                 break;
		        case 3 :
                 whereCondition =  " DOC_TRANS_HISTORY_CAT_DB IN ('DOC_ADDED_TO_TRANS','DOC_REM_FROM_TRANS')";
                 break;
		        case 4 :
                 whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'TRANS_METADATA_MODI'";
                 break;
		        case 5 :
                 whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'MANUAL_NOTE'";
                 break;
		        case 6 :
                 whereCondition =  "";
                 break;
		        default :
                whereCondition =  "";
		     }
           if (!mgr.isEmpty(whereCondition)) {
              q.addWhereCondition(whereCondition);
           }

        }
        int headrowno = headset.getCurrentRowNo();
        mgr.querySubmit(trans,itemblk2);
        headset.goTo(headrowno);
        //----------
		}
	}


   public void newRowITEM0()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOC_TRANSMITTAL_ISSUE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   }//end of newRowITEM0



    public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_TRANSMITTAL_ISSUE_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
      cmd.setParameter("REPORT", "YES");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }//end of newRowITEM1



     public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","DOC_TRANSMITTAL_HISTORY_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("TRANSMITTAL_ID", headset.getValue("TRANSMITTAL_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      itemset2.addRow(data);
   }//end of newRowITEM0

   


    public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      //Bug Id 81808, Start
      if(val.equals("PROJECT_ID") && (isProjInstalled))
      {
         trans.clear();
         //Bug Id: 89221, start
         cmd = trans.addCustomFunction("PROJECT", "DOCUMENT_TRANSMITTAL_API.Get_Customer_Project_Id", "CUSTOMER_PROJECT_ID");
         cmd.addParameter("PROJECT_ID");
         trans = mgr.validate(trans);

         String cutomerProjectId = trans.getValue("PROJECT/DATA/CUSTOMER_PROJECT_ID");
         cutomerProjectId = mgr.isEmpty(cutomerProjectId)?"":cutomerProjectId;
         StringBuffer response   = new StringBuffer("");
         response.append(cutomerProjectId);
         //Bug Id: 89221, end
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      else if (val.equals("PROJ_ID") && (isProjInstalled)) {
         cmd = trans.addCustomFunction("CHECK_PROJ_RECEIVE", "PROJ_TRANSMITTAL_RECEIVER_API.Check_Recievers_For_Project", "DUMMY2");
          cmd.addParameter("PROJECT_ID");
          trans = mgr.validate(trans);
          String check_proj_receiver = trans.getValue("CHECK_PROJ_RECEIVE/DATA/DUMMY2");
          StringBuffer response = new StringBuffer(""); 
          response.append(check_proj_receiver);
          response.append("^");
          mgr.responseWrite(response.toString());
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
      //Bug Id 81808, End
      // Bug Id 92775, start
      else  if(val.equals("DOC_NO"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("DOCTITLE", "Doc_Title_Api.Get_Title", "DUMMY2");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DOC_NO");
         trans = mgr.validate(trans);

         String docTitle = trans.getValue("DOCTITLE/DATA/DUMMY2");
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
         cmd = trans.addCustomFunction("DOCSTATE", "Doc_Issue_Api.Get_State", "DUMMY2");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DOC_NO");
         cmd.addParameter("DOC_SHEET");
         cmd.addParameter("DOC_REV");
         trans = mgr.validate(trans);

         String docState = trans.getValue("DOCSTATE/DATA/DUMMY2");
         if (mgr.isEmpty(docState))
            docState = "";
         StringBuffer response = new StringBuffer("");
         response.append(docState);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      // Bug Id 92775, end
      mgr.endResponse();
    }


    



    public void run()
   {
      ASPManager mgr = getASPManager();
      ctx            = mgr.getASPContext();
      fmt            = mgr.newASPHTMLFormatter();
      trans          = mgr.newASPTransactionBuffer();
      sHistoryMode   = ctx.readValue("HISTORY_MODE","6");
      updateState    = ctx.readFlag("UPDATE_STATE",false);
      updateEvent    = ctx.readValue("UPDATE_EVENT","");
      checkAllDocReleased = ctx.readFlag("CHECK_ALL_DOC_RELEASED",true); //Bug Id 75074

      bAddNewTransmittalCommentFile = ctx.readFlag("NEW_TRANS_COMMENT_FILE", false);
      currCommentFileNo = ctx.readValue("FILE_NO","");

      process        = ctx.readValue("PROCESS","");   //Bug id 88512
     // DocmawDebug.getInstance().debug("DocTransmittalInfo:run --------- start");
      
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");  //Bug Id 81808
      

      if (mgr.commandBarActivated())
      //Bug Id: 89221, start
      {
          String command = mgr.readValue("__COMMAND");
          eval(mgr.commandBarFunction());
          if ("HEAD.SaveReturn".equals(command)) {
             
             if ("TRUE".equals(mgr.readValue("CUSTOMER_PROJECT_ID_EDITED"))) {
                 //add history line here
                 addHistoryLine4ModifyingCustomerProject();
             }
             performRefreshParent();
          }
      }
      //Bug Id: 89221, end
         
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED")))
			historyModeChanged();
      //Bug Id 79174, start
      else if ("TRUE".equals(mgr.getQueryStringValue("EXECUTE_REPORT"))) 
      {
         showReport();
      }
      //Bug Id 79174, end
      //Bug Id 81806, start
      else if ("TRUE".equals(mgr.getQueryStringValue("LOAD_PAGE"))) 
      {
	 okFind();
	 headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }
      //Bug Id 81806, end

      //Bug id 88512 start
      else if ("OK".equals(mgr.readValue("CONFIRMCONNECTION")))
      {
          if("Send".equals(process))
          {
              this.updateState = true;
              this.updateEvent = "Send";
              this.checkAllDocReleased = true; //Bug Id 75074

          }
          else if("Receive".equals(process))
          {

              this.updateState = true;
              this.updateEvent = "Receive";
              
          }
       
       
        performRefreshParent();     
        
      }
      //Bug id 88512 end


      else if (mgr.dataTransfered())
      {
         okFind();
      }
      else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
      {
         if (!mgr.isEmpty(mgr.readValue("UPDATE_STATE")) ) {
            updateState = "TRUE".equals(mgr.readValue("UPDATE_STATE"));

         }

	 //Bug Id 75074, start
	 if (!mgr.isEmpty(mgr.readValue("CHECK_ALL_DOC_RELEASED")) ) {
            checkAllDocReleased = "TRUE".equals(mgr.readValue("CHECK_ALL_DOC_RELEASED"));

         }
	 //Bug Id 75074, end

         if (bAddNewTransmittalCommentFile) {
            addNewLineInTransmittalCommentFile();
         }

         //Bug Id: 68849, start
         performRefreshParent();
         refreshParentDone = true;
         //Bug Id: 68849, end
        
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("TRANSMITTAL_ID")) )
      {
         afterAddingDocs = true;
         okFind();
      }
         
		
      adjust();
      tabs.saveActiveTab();
      ctx.writeValue("HISTORY_MODE",sHistoryMode);
      
      ctx.writeFlag("NEW_TRANS_COMMENT_FILE", bAddNewTransmittalCommentFile);
      ctx.writeValue("FILE_NO",currCommentFileNo);
      ctx.writeFlag("UPDATE_STATE",updateState);
      ctx.writeValue("UPDATE_EVENT",updateEvent);
      ctx.writeFlag("CHECK_ALL_DOC_RELEASED", checkAllDocReleased); //Bug Id 75074
    }


     public void  adjust()
   {
      ASPManager mgr = getASPManager();
      // do any adjustments
      if (headset.countRows() <=0)
      {
         headbar.removeCustomCommand("activateDocuments");
         headbar.removeCustomCommand("activateReports");
         headbar.removeCustomCommand("activateHistory");
         
       }
      //Bug Id: 71762 start
      if (headlay.isSingleLayout())
          if(!"Preliminary".equals(headset.getRow().getValue("DB_STATE")))
              itembar0.disableCommand(itembar0.NEWROW);
          else
              itembar0.enableCommand(itembar0.NEWROW);
      //Bug Id: 71762 end
      
      //Bug Id 76248, start
      if(headlay.isEditLayout() && !"Preliminary".equals(headset.getRow().getValue("DB_STATE")))
      {
          mgr.getASPField("TRANSMITTAL_ID").setReadOnly();
          mgr.getASPField("TRANSMITTAL_DIRECTION").setReadOnly();
          mgr.getASPField("EXPECTED_SEND_DATE").setReadOnly();
          mgr.getASPField("EXPECTED_RETURN_DATE").setReadOnly();
          mgr.getASPField("DISTRIBUTION_METHOD").setReadOnly();
          mgr.getASPField("RECEIVER_TYPE").setReadOnly();
          mgr.getASPField("RECEIVER_ADDRESS").setReadOnly();
          mgr.getASPField("RECEIVER_CONTACT_PERSON").setReadOnly();

          
          mgr.getASPField("CUSTOMER_PROJECT_ID").setReadOnly();
          mgr.getASPField("CUSTOMER_SUB_PROJECT_ID").setReadOnly();
          mgr.getASPField("CUSTOMER_ACTIVITY_ID").setReadOnly();
          mgr.getASPField("SUPPLIER_PROJECT_ID").setReadOnly();
          mgr.getASPField("SUPPLIER_SUB_PROJECT_ID").setReadOnly();
          mgr.getASPField("SUPPLIER_ACTIVITY_ID").setReadOnly();
          mgr.getASPField("SUB_CONTRACTOR_PROJECT_ID").setReadOnly();
          mgr.getASPField("SUB_CONTRACTOR_SUB_PROJECT_ID").setReadOnly();
          mgr.getASPField("SUB_CONTRACTOR_ACTIVITY_ID").setReadOnly();
          
      }

      if(headlay.isSingleLayout() && !"Preliminary".equals(headset.getRow().getValue("DB_STATE")))
      {
	  itembar0.disableCommand(itembar0.DELETE);
	  itembar0.disableCommand(itembar0.DUPLICATEROW);
	 
	  itembar1.disableCommand(itembar1.DELETE);
      }

      if (itemlay0.isEditLayout() && !"Preliminary".equals(headset.getRow().getValue("DB_STATE"))) 
      {
	  mgr.getASPField("DOC_CLASS").setReadOnly();
	  mgr.getASPField("DOC_NO").setReadOnly();
	  mgr.getASPField("DOC_SHEET").setReadOnly();
	  mgr.getASPField("DOC_REV").setReadOnly();
      }
      //Bug Id 76248, end


      if (isProjInstalled) {
      
          if (itemlay0.isEditLayout() && headset.getValue("PROJECT_ID") == null)
          {
             mgr.getASPField("DOC_TRANSMITTAL_LINE_STATUS").setReadOnly();
          }

          if (headlay.isEditLayout() && !"Preliminary".equals(headset.getValue("DB_STATE"))) {
             mgr.getASPField("PROJECT_ID").setReadOnly();
             mgr.getASPField("SUB_PROJECT_ID").setReadOnly();
             mgr.getASPField("ACTIVITY_ID").setReadOnly();
          }
      
      }
   }

  //command methods
  //Bug Id: 89221, start
  private void addHistoryLine4ModifyingCustomerProject()
  {
      ASPManager mgr = getASPManager();
      String oldCustomerPrjectId  = mgr.readValue("OLD_CUSTOMER_PROJECT_ID");
      String currentTransmittalId = getCurrTransmittaId();
      String msgText = mgr.translate("DOCMAWDOCTRANSMITTALINFOCUSPROJMODI: The Customer Project ID '&1' was overwritten automatically.", oldCustomerPrjectId);

      trans.clear();
      cmd = trans.addCustomCommand("ADD_HIS_LINE", "Doc_Transmittal_History_Api.Create_New_Line");
      cmd.addParameter("DOC_CLASS",currentTransmittalId);
      cmd.addParameter("SDOCNAME",msgText);  //THIS THIS
      cmd.addParameter("SDOCNAME","TRANS_METADATA_MODI");
      cmd.addParameter("SDOCNAME","");
      cmd.addParameter("SDOCNAME","");
      cmd.addParameter("SDOCNAME","");
      cmd.addParameter("SDOCNAME","");
      mgr.perform(trans);
  }
  //Bug Id: 89221, end

   public void  docInfoFromReports()
   {
      ASPManager mgr = getASPManager();

      itemset1.storeSelections();
      if (itemlay1.isSingleLayout())
         itemset1.selectRow();
      keys=itemset1.getSelectedRows("ITEM1_DOC_CLASS,ITEM1_DOC_NO,ITEM1_DOC_SHEET,ITEM1_DOC_REV");

      if (keys.countItems()>0)
      {
         mgr.transferDataTo("DocIssue.page",keys);
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCTRANSMITTALINFONORECSEL: No records selected!"));
      }

   }




   public void  docInfoFromIssues()
   {
      ASPManager mgr = getASPManager();

      itemset0.storeSelections();
      if (itemlay0.isSingleLayout())
         itemset0.selectRow();
      keys=itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      if (keys.countItems()>0)
      {
         mgr.transferDataTo("DocIssue.page",keys);
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCTRANSMITTALINFONORECSEL: No records selected!"));
      }

   }


   public void  viewOriginal()
	{
		ASPManager mgr = getASPManager();

		if (itemlay0.isMultirowLayout() && itemset0.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCTRANSMITTALINFONOROWS: No Rows Selected."));
			return;
		}

		if (isEmptyColumnValue("CHECKED_IN_SIGN",itemlay0,itemset0))
		{
			if (itemlay0.isMultirowLayout())
				mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILEMULTI: One or more documents you're trying to view has no file checked in yet."));
			else
				mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILE: The document you're trying to view has no file checked in yet."));
			return;
		}

		if (isValidColumnValue("GETVIEWACCES", "TRUE", true))
		{
			ASPBuffer buff = mgr.newASPBuffer();
			buff.addItem("FILE_ACTION", "VIEW");
			buff.addItem("DOC_TYPE", "ORIGINAL");
			buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
			buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
			transferToEdmMacro(buff,itemlay0,itemset0,"DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
		}
		else
		{
			if (itemlay0.isMultirowLayout())
				mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEWMULTI: You don't have view access to one or more of the selected documents.");
			else
				mgr.showAlert("DOCMAWDOCISSUENOVIEWACCESSVIEW: You must have view access to be able to view this document.");
		}
	}


   
   public void  viewReport()
	{
		ASPManager mgr = getASPManager();

		if (itemlay1.isMultirowLayout() && itemset1.selectRows()==0)
		{
			mgr.showAlert(mgr.translate("DOCMAWDOCTRANSMITTALINFONOROWS: No Rows Selected."));
			return;
		}

		if (isEmptyColumnValue("REPORT_CHECKED_IN_SIGN",itemlay1,itemset1))
		{
			if (itemlay1.isMultirowLayout())
				mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILEMULTI: One or more documents you're trying to view has no file checked in yet."));
			else
				mgr.showAlert(mgr.translate("DOCMAWDOCISSUENOVIEWEMPTYFILE: The document you're trying to view has no file checked in yet."));
			return;
		}

		ASPBuffer buff = mgr.newASPBuffer();
		buff.addItem("FILE_ACTION", "VIEW");
		buff.addItem("DOC_TYPE", "ORIGINAL");
		buff.addItem("SAME_ACTION_TO_ALL", mgr.readValue("SAME_ACTION_TO_ALL"));
		buff.addItem("LAUNCH_FILE", mgr.readValue("LAUNCH_FILE"));
		transferToEdmMacro(buff,itemlay1,itemset1,"ITEM1_DOC_CLASS,ITEM1_DOC_NO,ITEM1_DOC_SHEET,ITEM1_DOC_REV");
		
	}


   public boolean isEmptyColumnValue(String         column,
                                     ASPBlockLayout lay,
                                     ASPRowSet      set)
	{

		ASPManager mgr = getASPManager();
		boolean empty = false;
		int noOfRowsSelected = 1;

		if (lay.isSingleLayout())
		{
			set.selectRow();
		}
		else
		{
			set.selectRows();
			set.setFilterOn();
			noOfRowsSelected = set.countRows();
		}


		for (int k=0;k<noOfRowsSelected;k++)
		{
			if (mgr.isEmpty(set.getRow().getValue(column)))
			{
				empty = true;
				break;
			}
			if (lay.isMultirowLayout())
			{
				set.next();
			}
		}

		if (lay.isMultirowLayout())
		{
			set.setFilterOff();
		}
		return empty;
	}

public boolean  isProjInstalled()   {
      ASPManager mgr = getASPManager();
      String sSQLStmt;
      trans = mgr.newASPTransactionBuffer();     

      trans.clear(); 
      cmd = trans.addCustomFunction("PROJQUERY", "TRANSACTION_SYS.Logical_Unit_Is_Installed_Num", "DUMMY1"); 
      cmd.addParameter("LU_NAME","Project");
            
      debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + trans.getValue("PROJQUERY/DATA/DUMMY1"));

      
      trans = mgr.performConfig(trans);
      trans.clear();

      return true;
    }

   public boolean isValidColumnValue(String column, String validity_check, boolean match)
	{

		boolean invalid = true;
		int noOFSelectedRows = 1;

		if (itemlay0.isSingleLayout())
		{
			itemset0.selectRow();
		}
		else
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			noOFSelectedRows =  itemset0.countRows();
		}

		for (int k=0;k<noOFSelectedRows;k++)
		{
			if ((match && !validity_check.equals(itemset0.getRow().getValue(column))) ||
				 (!match && validity_check.equals(itemset0.getRow().getValue(column))))
			{
				invalid = false;
				break;
			}
			if (itemlay0.isMultirowLayout())
			{
				itemset0.next();
			}

		}

		if (itemlay0.isMultirowLayout())
		{
			itemset0.setFilterOff();
		}
		return invalid;
	}

   public void transferToEdmMacro(ASPBuffer      buff,
                                  ASPBlockLayout lay,
                                  ASPRowSet      set,
                                  String         selectedFields)
	{
		ASPManager mgr = getASPManager();

		if (lay.isSingleLayout())
		{
			set.unselectRows();
			set.selectRow();
		}
		else
			set.selectRows();

		ASPBuffer data = set.getSelectedRows(selectedFields);//
		sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page",buff,data);
      data.traceBuffer("aaaaa");
		bTranferToEDM = true;

	}


   public void setFileNoInCurRow(int fileNo)
   {
      if (itemlay0.isSingleLayout())
		{
			itemset0.selectRow();
		}
		else
		{
			itemset0.storeSelections();
			itemset0.setFilterOn();
			
		}

      itemset0.setValue("FILE_NO",""+fileNo);

      if (itemlay0.isMultirowLayout())
		{
			itemset0.setFilterOff();
		}
   }


   public void sendTrnasmittal()
   {
      //Bug id 88512 Start these values will be assigned in run() 
      //this.updateState = true;
      //this.updateEvent = "Send";
      //this.checkAllDocReleased = true; //Bug Id 75074
      //performRefreshParent();
     ctx.writeValue("PROCESS", "Send");
     showConfirmMessage();
     //Bug id 88512 End

   }


   //Bug id 88512 Start
   public void showConfirmMessage()
  {
      ASPManager mgr = getASPManager();

     if("FALSE".equals(checkDocExist(headset.getValue("TRANSMITTAL_ID"))))
     {

         warn_message = mgr.translate("DOCMAWDOCTRANSMITTALINFONODOCS: The Document Transmittal that you are trying to send/receive does not contain any documents. Do you still want to continue?.");
         showConfirmConnectFiles = true;

     } 
     // Bug Id 78806, start
     else if (!checkDocsForNoFile(getCurrTransmittaId()))
     {
         showConfirmConnectFiles = true;
     }
     // Bug Id 78806, end
     else
     {

         if("Send".equals(ctx.readValue("PROCESS", "")))
         {
             this.updateState = true;
             this.updateEvent = "Send";
             this.checkAllDocReleased = true; //Bug Id 75074
             performRefreshParent();

         }
         else if("Receive".equals(ctx.readValue("PROCESS", "")))
         {
             this.updateState = true;
             this.updateEvent = "Receive";
             performRefreshParent();

         }
     }
  }       

   public String checkDocExist(String transmittalId)
   {
       ASPManager mgr = getASPManager();
       trans.clear();
       String result;
       cmd = trans.addCustomFunction("CHECK_EXIST","DOC_TRANSMITTAL_ISSUE_API.Check_If_Docs_Exist","DUMMY2");
       cmd.addParameter("TRANSMITTAL_ID",transmittalId);
       trans = mgr.perform(trans);

       if ("TRUE".equals(trans.getValue("CHECK_EXIST/DATA/DUMMY2"))) 
	   return "TRUE";
       else
           return "FALSE";

   }

   //Bug id 88512 End

   // Bug Id 78806, start
   public boolean checkDocsForNoFile(String transmittalId)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addCustomFunction("CHECK_EXIST","DOC_TRANSMITTAL_ISSUE_API.Check_Docs_Has_No_Files","DUMMY2");
      cmd.addParameter("TRANSMITTAL_ID",transmittalId);
      trans = mgr.perform(trans);
      
      warn_message = trans.getValue("CHECK_EXIST/DATA/DUMMY2");
      
      if (mgr.isEmpty(warn_message))
         return true;
      else
         return false;
      
   }
   // Bug Id 78806, end


    public void  performRefreshParent()  
    {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         if (updateState) 
	 {
            //Bug Id 75074, start
	     if ("Send".equals(updateEvent))
	     {
		 if (checkAllDocReleased && !checkAllDocumentsReleased(headset.getValue("TRANSMITTAL_ID"))) 
		 {
		     updateState = false;
		     error_message = mgr.translate("DOCMAWDOCTRANSMITTALINFORELALLDOCS: Please 'Release' all the documents attached to transmittal before send.");
		     return;
		 }
		 else
		     updateState(headset.getValue("TRANSMITTAL_ID"),updateEvent);
	     }
	     else
		 updateState(headset.getValue("TRANSMITTAL_ID"),updateEvent);
             //Bug Id 75074, end
         }
         headset.refreshRow();
	 eval(headset.syncItemSets());
         refreshActiveTab();
      }  
      else
      {
	 if (headset.countSelectedRows() == 0)
	     headset.selectRows();

	 headset.storeSelections(); //Bug Id 75074
	 headset.setFilterOn();
         int k= 0;
	 headset.first();

	 //Bug Id 75074, start
	 boolean bAllDocsNotReleased = true;
	 do
	 {
	     if (updateState && "Send".equals(updateEvent) && checkAllDocReleased)
	     {
		 bAllDocsNotReleased = checkAllDocumentsReleased(headset.getValue("TRANSMITTAL_ID"));
		 if (! bAllDocsNotReleased)
		 {
		     error_message = mgr.translate("DOCMAWDOCTRANSMITTALINFORELALLDOCS: Please 'Release' all the documents attached to transmittal before send.");
		     updateState = false;
		     headset.setFilterOff();
		     return ;
		 }
	     }
	 }
	 while (headset.next());

	 headset.first();
	 //Bug Id 75074, end

	 do
	 {
            if (updateState) 
	    {
               updateState(headset.getValue("TRANSMITTAL_ID"),updateEvent);
            }
            headset.refreshRow(); // Note: this operation is expensive when doing for many rows
	 } 
	 while (headset.next());

	 headset.setFilterOff();
      }

      updateState = false;
      
   }

    //Bug Id 75074, start
   public boolean checkAllDocumentsReleased(String transmittalId)
   {
       ASPManager mgr = getASPManager();
       trans.clear();
       cmd = trans.addCustomFunction("ALL_RELEASED","DOC_TRANSMITTAL_ISSUE_API.All_Docs_Released","DUMMY2");
       cmd.addParameter("TRANSMITTAL_ID",transmittalId);
       trans = mgr.perform(trans);

       if ("TRUE".equals(trans.getValue("ALL_RELEASED/DATA/DUMMY2"))) 
	   return true;
       else
           return false;
   }
   //Bug Id 75074, end

    public void updateState(String transmittalId,String event){
       ASPManager mgr = getASPManager();
       trans.clear();
       cmd = trans.addCustomCommand("CHANGESTATE", "document_transmittal_api.Set_Transmittal_State");
       cmd.addParameter("SDOCNAME",transmittalId );
       cmd.addParameter("SDOCNAME",event );
       mgr.perform(trans);

    }



   private void refreshActiveTab()
	{
		if (tabs.getActiveTab()== 1)
		{
			okFindITEM0();
		}
		else if (tabs.getActiveTab()== 2)
		{
			okFindITEM1();
		}
		else if (tabs.getActiveTab()== 3)
		{
			okFindITEM2();
		}
	  
	}






    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
       if (headlay.isMultirowLayout())
          return "DOCTRANSMITTALINFOTITLEOVERVIEW: Overview - Document Transmittal Info"; //Bug Id 75081, Changed the display name
       else
          return "DOCTRANSMITTALINFOTITLEINFO: Document Transmittal Info";
    }

    protected String getTitle()
    {
       return getDescription();
    }





   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      //Bug Id: 88512, start
      appendToHTML("  <input type =\"hidden\" name=\"CONFIRMCONNECTION\" value=\"\">\n");
      //Bug Id: 88512, end
      appendToHTML("<input type=\"hidden\" name=\"REFRESH_PARENT\" value=\"FALSE\">\n");
      //Bug Id: 68844, start
      appendToHTML("<input type=\"hidden\" name=\"UPDATE_STATE\" value=\""+(updateState?"TRUE":"FALSE")+"\">\n");
      //Bug Id: 68844, end
      //Bug Id, 75074, start
      appendToHTML("<input type=\"hidden\" name=\"CHECK_ALL_DOC_RELEASED\" value=\""+(checkAllDocReleased?"TRUE":"FALSE")+"\">\n");
      //Bug Id, 75074, end
      appendToHTML("<input type=\"hidden\" name=\"PROJECT_RECEIVER_EXIST\" value=\"FALSE\">\n"); // Bug Id 81808
      //Bug Id: 89221, start
      // CUSTOMER_PROJECT_ID_EDITED: meaning that CUSTOMER_PROJECT_ID is changed as a result of modifying PROJECT_ID, but not manual modifications
      appendToHTML("<input type=\"hidden\" name=\"CUSTOMER_PROJECT_ID_EDITED\" value=\"FALSE\">\n");
      appendToHTML("<input type=\"hidden\" name=\"OLD_CUSTOMER_PROJECT_ID\" value=\"FALSE\">\n");
      //Bug Id: 89221, end

      appendToHTML(headlay.show());
      if ( headset.countRows() > 0 && (headlay.isSingleLayout()||headlay.isCustomLayout())) //headlay.isVisible() &&
      {
         appendToHTML(tabs.showTabsInit());
			if (tabs.getActiveTab()== 1)
			{
			   appendToHTML(itemlay0.show());
            

			}
			else if (tabs.getActiveTab()== 2)
			{
			   appendToHTML(itemlay1.show());
			}
			else if (tabs.getActiveTab()== 3)
			{
            appendToHTML("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");
            appendToHTML(itembar2.showBar());
            appendToHTML("<table cellpadding=\"10\" cellspacing=\"0\" border=\"0\" width=\"");
		      appendToHTML("\">\n");
		      appendToHTML("<tr><td>");// row 1
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODECREATED: Transmittal Created"), "HISTORY_MODE", "1", "1".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		      appendToHTML("</td><td>");
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODESENTREC: Transmittal Sent or Received"), "HISTORY_MODE" , "2" , "2".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
		      appendToHTML("</td><td>");
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEDOCUMENT: document Added,Removed or Modified"), "HISTORY_MODE", "3", "3".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		      appendToHTML("</td></tr><tr><td>");
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEMETAMODI: Meta Data Modified"), "HISTORY_MODE", "4", "4".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		      appendToHTML("</td><td>");
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEMANUAL: Manual Note"), "HISTORY_MODE", "5", "5".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
		      appendToHTML("</td><td>");
		      appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYALL: All"), "HISTORY_MODE", "6", "6".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		      appendToHTML("</td></tr>");
		      appendToHTML("</table>\n");

            appendToHTML(itemlay2.generateDataPresentation());

            appendDirtyJavaScript("function modeChanged()\n");
		      appendDirtyJavaScript("{\n");
		      appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
		      appendDirtyJavaScript("   submit();\n");
		      appendDirtyJavaScript("}\n");
			   
			}
         appendToHTML(tabs.showTabsFinish());

         
      }
      //bug id 88512 start
      if (showConfirmConnectFiles)

		{
         
			appendDirtyJavaScript(" if (confirm('");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(warn_message));
			appendDirtyJavaScript("')) {\n");
			appendDirtyJavaScript("   f.CONFIRMCONNECTION.value='OK';\n");
			appendDirtyJavaScript("   submit();\n");
			appendDirtyJavaScript(" }\n");
                        appendDirtyJavaScript(" else{\n");
                        appendDirtyJavaScript("   f.CONFIRMCONNECTION.value='CANCEL';\n");
			appendDirtyJavaScript("   submit();\n");
                        appendDirtyJavaScript(" }\n");

                  }

       //bug id 88512 end


      if (openWizard || (bTranferToEDM &&(tabs.getActiveTab()== 1 || tabs.getActiveTab()== 2)) )
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width="+(openWizard?1000:500)+",height=500,left=100,top=100\");\n");
      }

      //Bug Id 79174, start
      if (bShowReport)
      {
         appendDirtyJavaScript("   report_window = window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }
      //Bug Id 79174, end

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function update_State(value)\n");// '_' is to make this Js method look different.
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    document.form.UPDATE_STATE.value = value;\n");
      appendDirtyJavaScript("}\n");

      //Bug Id 75074, start

      appendDirtyJavaScript("function check_doc_released(value)\n");// '_' is to make this Js method look different.
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    document.form.CHECK_ALL_DOC_RELEASED.value = value;\n");
      appendDirtyJavaScript("}\n");

      if (!mgr.isEmpty(error_message))
      {
         error_message = error_message.replaceAll("\n","\\n");
         appendDirtyJavaScript("\n alert('"+handleSingleQuote(error_message)+"');\n");
      }
      //Bug Id 75074, end

      // Bug Id 81808, Start
      appendDirtyJavaScript("function lovReceiverContactPerson(i, params)\n"); 
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   var project_id_ = getValue_('PROJECT_ID',i);\n");
      appendDirtyJavaScript("   last_value =  getValue_('RECEIVER_CONTACT_PERSON',i);\n");
      appendDirtyJavaScript("   var r = __connect('" + mgr.getURL() + "?VALIDATE=PROJ_ID'+'&PROJECT_ID=' + URLClientEncode(project_id_));\n");
      appendDirtyJavaScript("	assignValue_('PROJECT_RECEIVER_EXIST',i,0);\n");
      appendDirtyJavaScript("   var project_receiver_exist_ = document.form.PROJECT_RECEIVER_EXIST.value;\n");
      appendDirtyJavaScript("   if (project_id_ == \"\" || project_id_ == null || project_receiver_exist_ != \"TRUE\") \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('RECEIVER_CONTACT_PERSON',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PERSON_INFO_PUBLIC_LOV&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALWIZARDRECEIVERID: Receiver Id"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n"); // Bug Id 89680
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("      + '&RECEIVER_CONTACT_PERSON=' + URLClientEncode(last_value)\n");
      appendDirtyJavaScript("      ,550,500,'validateReceiverContactPerson');\n"); //Bug Id 84284
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      openLOVWindow('RECEIVER_CONTACT_PERSON',i,\n");
      appendDirtyJavaScript("      '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PROJ_TRANSMITTAL_RECEIVER_LOV&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALWIZARDRECEIVERID: Receiver Id"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("      + '&RECEIVER_CONTACT_PERSON=' + URLClientEncode(last_value)\n");
      appendDirtyJavaScript("      + '&PROJECT_ID=' + URLClientEncode(getValue_('PROJECT_ID',i))\n");
      appendDirtyJavaScript("      ,550,500,'validateReceiverContactPerson');\n"); //Bug Id 84284
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 81808, End
      // Bug Id 85164, Start
      appendDirtyJavaScript("function lovReceiverAddress(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice;\n");
      appendDirtyJavaScript("   try {enable_multichoice =(true && HEAD_IN_FIND_MODE);}catch(er){enable_multichoice=false;}\n");
      appendDirtyJavaScript("   openLOVWindow('RECEIVER_ADDRESS',i,\n");
      appendDirtyJavaScript("   '" + root_path + "common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PERSON_INFO_ADDRESS&__FIELD="+mgr.URLEncode(mgr.translate("DOCTRANSMITTALINFORECEIVERADDRESS: Receiver Address"))+"&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('RECEIVER_ADDRESS',i))\n");
      appendDirtyJavaScript("     + '&PERSON_ID=' + URLClientEncode(getValue_('RECEIVER_CONTACT_PERSON',i))\n");
      appendDirtyJavaScript("   ,550,500,'validateReceiverAddress');\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 85164, End
      //Bug Id: 89221, start
      appendDirtyJavaScript("function validateProjectId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkProjectId(i) ) return;\n");
      appendDirtyJavaScript("   if( getValue_('PROJECT_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("   var headLayEdit = "+headlay.isEditLayout()+";\n");
      appendDirtyJavaScript("   if( getValue_('PROJECT_ID',i)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      //getField_('CUSTOMER_PROJECT_ID',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   var oldCutomerProjectId = getField_('CUSTOMER_PROJECT_ID',i).value;\n");
      appendDirtyJavaScript("   window.status='Please wait for validation';\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("   APP_ROOT+ 'docmaw/DocTransmittalInfo.page'+'?VALIDATE=PROJECT_ID'\n");
      appendDirtyJavaScript("   + '&PROJECT_ID=' + URLClientEncode(getValue_('PROJECT_ID',i))\n");
      appendDirtyJavaScript("   , 'GET');\n");
      appendDirtyJavaScript("   window.status='';\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'PROJECT_ID',i,'Project ID') )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      assignValue_('CUSTOMER_PROJECT_ID',i,0);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if(getField_('CUSTOMER_PROJECT_ID',i).value == '')\n");
      appendDirtyJavaScript("      getField_('CUSTOMER_PROJECT_ID',i).value = oldCutomerProjectId;\n");
      if (headset != null && headset.countRows()>0) {
         appendDirtyJavaScript("   else if(headLayEdit && "+!mgr.isEmpty(headset.getRow().getValue("CUSTOMER_PROJECT_ID"))+"){ \n");
         appendDirtyJavaScript("      if(getField_('CUSTOMER_PROJECT_ID',i).value != '"+headset.getRow().getValue("CUSTOMER_PROJECT_ID")+"' ){\n");
         appendDirtyJavaScript("         getField_('CUSTOMER_PROJECT_ID_EDITED',i).value = \"TRUE\";\n");  
         appendDirtyJavaScript("         getField_('OLD_CUSTOMER_PROJECT_ID',i).value = '"+headset.getRow().getValue("CUSTOMER_PROJECT_ID")+"';\n");
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("      else{\n");
         appendDirtyJavaScript("         getField_('CUSTOMER_PROJECT_ID_EDITED',i).value = \"FALSE\";\n");  
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("   }\n");
      }
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCustomerProjectId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("setDirty();\n");
      appendDirtyJavaScript("if( !checkCustomerProjectId(i) ) return;\n");
      appendDirtyJavaScript("getField_('CUSTOMER_PROJECT_ID_EDITED',i).value = \"FALSE\";\n");  
      appendDirtyJavaScript("}\n");

      //Bug Id: 89221, end
      
   }//end of printContents


   //=============================================================================
   //   CMDBAR FUNCTIONS
   //=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      boolean dataTransfered = false;
      
      String currentTrans = "";

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
      {
         ASPBuffer buff = mgr.getTransferedData();
         
         q.addOrCondition(buff);
         
         dataTransfered = true;
         if ("TRUE".equals(mgr.readValue("UPDATE_STATE") )){
            this.updateState = true;
         }

         if (!mgr.isEmpty(mgr.getQueryStringValue("AFTER_ADDING_DOCS")))
         {
            currentTrans = mgr.getQueryStringValue("CURR_TRANS_NO");
            afterAddingDocs = true;
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("TRANSMITTAL_ID")) ){
         q.addWhereCondition("TRANSMITTAL_ID = ?");
		   q.addParameter("TRANSMITTAL_ID", mgr.getQueryStringValue("TRANSMITTAL_ID"));
      }
      
      q.setOrderByClause("TRANSMITTAL_ID");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
         headset.clear();
      }

      headset.first();

      if (afterAddingDocs)
      {
         do
         {
            if (currentTrans.equals(headset.getRow().getValue("TRANSMITTAL_ID")))
            {
               break;
            }
         }while (headset.next());
      }

      if ((dataTransfered && headset.countRows()== 1)||afterAddingDocs) {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (this.updateState) {
          updateState(headset.getValue("TRANSMITTAL_ID"),"Send");
          headset.refreshRow();
          eval(headset.syncItemSets());
          this.updateState = false;
      }

      eval(headset.syncItemSets());
		okFindITEM0();


   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void newRow()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("HEAD","DOCUMENT_TRANSMITTAL_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


   public void  historyModeChanged()
	{
		ASPManager mgr = getASPManager();

		sHistoryMode = mgr.readValue("HISTORY_MODE");
		okFindITEM2();
	}


   //Bug Id 75074, start
   private String handleSingleQuote(String script)
   {
      String returnString = "";
      for (int k=0;k<script.length();k++) 
      {
         if ("'".equals(String.valueOf(script.charAt(k))))
            returnString += "\\'";
         else
            returnString +=  String.valueOf(script.charAt(k));

      }
      return returnString;
   }
   //Bug Id 75074, end

}// end of class

