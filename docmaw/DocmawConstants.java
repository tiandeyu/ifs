
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
*  File        : DocmawConstants.java
*
*  Date        Sign    Descripiton
*  ----        ----    -----------
*  2003-01-05  DiKalk  Created
*  2003-02-26  Dikalk  Added more constants (Edm Macro Processes).
*  2003-05-07  InoSlk  Added contants for Briefcase Handling.
*  2003-05-08  InoSlk  Added constants from Doc_Bc_Issue.
*  2003-05-08  InoSlk  Added constants for bc compare.
*  2003-07-29  InoSlk  Added contants for new edm states. (briefcase)
*  2004-11-08  DiKalk  Modified and added new OCX translation contants
*  2004-12-17  Bakalk  Merged the bug 47864.
*  2005-01-17  DiKalk  Added a new HashMap to hold instances of DocmawConstants for
*                      different languages.
*  2005-10-25  AMNALK  Fixed Call 128090.
*  2006-01-23  DIKALK  Added method getDBValue() to return the db value in the response
*                      buffer, or raise an exception of the response value is empty
*  2006-03-15  BAKALK  Call Id:135770, Added a new constant:ID_VC1022. 
*  2006-03-15  RUCSLK  Call Id:39859, Added a new constant:ID_ER2035. 
*  2006-06-04  BAKALK  Call Id:58326, Added a new constant:ID_ER2036. 
*  2006-06-23  KARALK  Bug Id, Added a new constant:ID_ER2037,ID_ER2038.
*  2006-07-20  NIJALK  Bug Id 54793 Added constants ID_ER2039 and ID_ER2040 for OCX CheckPathWriteProtected method.
*  2006-07-25  NIJALK  Bug Id 55611, Modified constant ID_DF5005. Added constant ID_DF5006,ID_ER2041.
*  2007-08-09  AMNILK  Eliminated SQL Injection Security Vulnerability.
*  2008-05-06  BAKALK  Bug Id 71838, Added new constants transmittal_direction_in & transmittal_direction_out.
*  2008-05-12  VIRALK  Bug Id 71463, Added new constants ID_ER2042 & ID_ER2043.
*  2008-07-07  AMNALK  Bug Id 72460, Added new constant ID_ER2044.
*  2008-07-08  SHTHLK  Bug Id 69562, Added new constant ID_ER2045
*  2008-10-17  SHTHLK  Bug Id 77857, Enabled proper translation of OCX messages.
*  2009-02-18  AMNALK  Bug Id 78749, Added new constants ID_ER2046 & ID_ER2047
*  2009-08-27  AMNALK  Bug Id 85187, Added new constant ID_DF5007.
*  2010-09-16  DULOLK  Bug Id 92125, Added new constant ID_ER2049.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import java.util.HashMap;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.AutoString;
import ifs.fnd.service.FndException;

/**
 * DocmawConstants contains references to various LU and other client constants.
 * The LU constants are accessed via the singleton method getConstantHolder().
 * Client constants take the form of normal static variables.
 *
 * A single instance of this class is created the first time this class is accessed,
 * through getConstantHolder(), and requires that a reference to an instance of
 * ASPManager is passed in order to initialse the LU constants by making db calls.
 * Subsequent calls to getConstantHolder() will not make any db calls.
 */
public class DocmawConstants
{

   private static HashMap const_map;

   //
   // DATABASE CONSTANTS
   //
   //inter-page parameter name constant
   public static final String INTER_PAGE_PARENT_PAGE = "INTER_PAGE_PARENT_PAGE";
   public static final String INTER_PAGE_PARENT_PAGE_EDITABLE = "INTER_PAGE_PARENT_PAGE_EDITABLE";
   public static final String PARENT_DOC_CLASS = "PARENT_DOC_CLASS_";
   public static final String PARENT_DOC_NO = "PARENT_DOC_NO_";
   public static final String PARENT_DOC_SHEET = "PARENT_DOC_SHEET_";
   public static final String PARENT_DOC_REV = "PARENT_DOC_REV_";
   

   // Names should follow the convention lu_name_constant
   public static final String PROJ_RECEIVE    = "010101"; 
   public static final String PROJ_SEND = "010102";
   public static final String PROJ_PROPHASE = "0102";
   public static final String PROJ_CONSTRUCT = "0103";
   public static final String PROJ_DESIGN = "0104";
   public static final String PROJ_EQUIPMENT = "0105";
   public static final String PROJ_TEST = "0106";
   public static final String PROJ_COMPLETION = "0107";
   public static final String PROJ_STANDARD = "0108";
   public static final String PROJ_TEMP = "9901";
   
   
   public static final String PROJ_RECEIVE_FAX       = "01"; 
   public static final String PROJ_RECEIVE_MEMO      = "02"; 
   public static final String PROJ_RECEIVE_MEETING   = "03"; 
   public static final String PROJ_SEND_FAX          = "01";
   public static final String PROJ_SEND_MEMO         = "02";
   public static final String PROJ_SEND_MEETING      = "03";
   
   public static final String PROJ_CONSTRUCT_PROCEDURE = "0203";
   
   public static final String PROJ_RECEIVE_FAX_TITLE_PAGE        = "DocTitleReceiveFax.page"; 
   public static final String PROJ_RECEIVE_MEMO_TITLE_PAGE       = "DocTitleReceiveMemo.page"; 
   public static final String PROJ_RECEIVE_MEETING_TITLE_PAGE    = "DocTitleReceiveMeeting.page"; 
   public static final String PROJ_SEND_FAX_TITLE_PAGE           = "DocTitleSendFax.page";
   public static final String PROJ_SEND_MEMO_TITLE_PAGE          = "DocTitleSendMemo.page";
   public static final String PROJ_SEND_MEETING_TITLE_PAGE       = "DocTitleSendMeeting.page";
   public static final String PROJ_PROPHASE_TITLE_PAGE           = "DocTitleProphase.page";
   public static final String PROJ_CONSTRUCT_TITLE_PAGE          = "DocTitleConstruct.page";
   public static final String PROJ_DESIGN_TITLE_PAGE             = "DocTitleDesign.page";
   public static final String PROJ_EQUIPMENT_TITLE_PAGE          = "DocTitleEquipment.page";
   public static final String PROJ_TEST_TITLE_PAGE               = "DocTitleTest.page";
   public static final String PROJ_COMPLETION_TITLE_PAGE         = "DocTitleCompletion.page";
   public static final String PROJ_STANDARD_TITLE_PAGE           = "DocTitleStandard.page";
   public static final String PROJ_TEMP_TITLE_PAGE               = "DocTitleTemp.page";
   
   public static final String PROJ_RECEIVE_FAX_ISSUE_PAGE        = "DocIssueReceiveFax.page"; 
   public static final String PROJ_RECEIVE_MEMO_ISSUE_PAGE       = "DocIssueReceiveMemo.page"; 
   public static final String PROJ_RECEIVE_MEETING_ISSUE_PAGE    = "DocIssueReceiveMeeting.page"; 
   public static final String PROJ_SEND_FAX_ISSUE_PAGE           = "DocIssueSendFax.page";
   public static final String PROJ_SEND_MEMO_ISSUE_PAGE          = "DocIssueSendMemo.page";
   public static final String PROJ_SEND_MEETING_ISSUE_PAGE       = "DocIssueSendMeeting.page";
   public static final String PROJ_PROPHASE_ISSUE_PAGE           = "DocIssueProphase.page";
   public static final String PROJ_CONSTRUCT_ISSUE_PAGE          = "DocIssueConstruct.page";
   public static final String PROJ_DESIGN_ISSUE_PAGE             = "DocIssueDesign.page";
   public static final String PROJ_EQUIPMENT_ISSUE_PAGE          = "DocIssueEquipment.page";
   public static final String PROJ_TEST_ISSUE_PAGE               = "DocIssueTest.page";
   public static final String PROJ_COMPLETION_ISSUE_PAGE         = "DocIssueCompletion.page";
   public static final String PROJ_STANDARD_ISSUE_PAGE           = "DocIssueStandard.page";
   public static final String PROJ_TEMP_ISSUE_PAGE               = "DocIssueTemp.page";
   
   public static final String EXCH_RECEIVE = "010101X";
   public static final String EXCH_SEND = "010102X";
   public static final String EXCH_PROPHASE = "0102X";
   public static final String EXCH_CONSTRUCT = "0103X";
   public static final String EXCH_DESIGN = "0104X";
   public static final String EXCH_EQUIPMENT = "0105X";
   public static final String EXCH_TEST = "0106X";
   public static final String EXCH_COMPLETION = "0107X";
   public static final String EXCH_STANDARD = "0108X";
   public static final String EXCH_TEMP = PROJ_TEMP;
   
   public static final String EXCH_RECEIVE_FAX       = "01"; 
   public static final String EXCH_RECEIVE_MEMO      = "02"; 
   public static final String EXCH_RECEIVE_MEETING   = "03"; 
   public static final String EXCH_SEND_FAX          = "01";
   public static final String EXCH_SEND_MEMO         = "02";
   public static final String EXCH_SEND_MEETING      = "03";
   
   public static final String EXCH_RECEIVE_FAX_TITLE_PAGE       = "DocTitleReceiveFaxExch.page";       
   public static final String EXCH_RECEIVE_MEMO_TITLE_PAGE      = "DocTitleReceiveMemoExch.page";       
   public static final String EXCH_RECEIVE_MEETING_TITLE_PAGE   = "DocTitleReceiveMeetingExch.page";       
   public static final String EXCH_SEND_FAX_TITLE_PAGE          = "DocTitleSendFaxExch.page";          
   public static final String EXCH_SEND_MEMO_TITLE_PAGE         = "DocTitleSendMemoExch.page";          
   public static final String EXCH_SEND_MEETING_TITLE_PAGE      = "DocTitleSendMeetingExch.page";          
   public static final String EXCH_PROPHASE_TITLE_PAGE          = "DocTitleProphaseExch.page";      
   public static final String EXCH_CONSTRUCT_TITLE_PAGE         = "DocTitleConstructExch.page";     
   public static final String EXCH_DESIGN_TITLE_PAGE            = "DocTitleDesignExch.page";        
   public static final String EXCH_EQUIPMENT_TITLE_PAGE         = "DocTitleEquipmentExch.page";     
   public static final String EXCH_TEST_TITLE_PAGE              = "DocTitleTestExch.page";          
   public static final String EXCH_COMPLETION_TITLE_PAGE        = "DocTitleCompletionExch.page";    
   public static final String EXCH_STANDARD_TITLE_PAGE          = "DocTitleStandardExch.page";  
   public static final String EXCH_TEMP_TITLE_PAGE              = "DocTitleTempExch.page";  
   
   public static final String EXCH_RECEIVE_FAX_TITLE_TAB_PAGE       = "DocTitleReceiveFaxExchTab.page";       
   public static final String EXCH_RECEIVE_MEMO_TITLE_TAB_PAGE      = "DocTitleReceiveMemoExchTab.page";       
   public static final String EXCH_RECEIVE_MEETING_TITLE_TAB_PAGE   = "DocTitleReceiveMeetingExchTab.page";       
   public static final String EXCH_SEND_FAX_TITLE_TAB_PAGE          = "DocTitleSendFaxExchTab.page";          
   public static final String EXCH_SEND_MEMO_TITLE_TAB_PAGE         = "DocTitleSendMemoExchTab.page";          
   public static final String EXCH_SEND_MEETING_TITLE_TAB_PAGE      = "DocTitleSendMeetingExchTab.page";          
   public static final String EXCH_PROPHASE_TITLE_TAB_PAGE          = "DocTitleProphaseExchTab.page";      
   public static final String EXCH_CONSTRUCT_TITLE_TAB_PAGE         = "DocTitleConstructExchTab.page";     
   public static final String EXCH_DESIGN_TITLE_TAB_PAGE            = "DocTitleDesignExchTab.page";        
   public static final String EXCH_EQUIPMENT_TITLE_TAB_PAGE         = "DocTitleEquipmentExchTab.page";     
   public static final String EXCH_TEST_TITLE_TAB_PAGE              = "DocTitleTestExchTab.page";          
   public static final String EXCH_COMPLETION_TITLE_TAB_PAGE        = "DocTitleCompletionExchTab.page";    
   public static final String EXCH_STANDARD_TITLE_TAB_PAGE          = "DocTitleStandardExchTab.page";        
   public static final String EXCH_TEMP_TITLE_TAB_PAGE              = "DocTitleTempExchTab.page";        
                                                                                         
   public static final String EXCH_RECEIVE_FAX_ISSUE_PAGE       = "DocIssueReceiveFaxExch.page";       
   public static final String EXCH_RECEIVE_MEMO_ISSUE_PAGE      = "DocIssueReceiveMemoExch.page";       
   public static final String EXCH_RECEIVE_MEETING_ISSUE_PAGE   = "DocIssueReceiveMeetingExch.page";       
   public static final String EXCH_SEND_FAX_ISSUE_PAGE          = "DocIssueSendFaxExch.page";          
   public static final String EXCH_SEND_MEMO_ISSUE_PAGE         = "DocIssueSendMemoExch.page";          
   public static final String EXCH_SEND_MEETING_ISSUE_PAGE      = "DocIssueSendMeetingExch.page";          
   public static final String EXCH_PROPHASE_ISSUE_PAGE          = "DocIssueProphaseExch.page";      
   public static final String EXCH_CONSTRUCT_ISSUE_PAGE         = "DocIssueConstructExch.page";      
   public static final String EXCH_DESIGN_ISSUE_PAGE            = "DocIssueDesignExch.page";        
   public static final String EXCH_EQUIPMENT_ISSUE_PAGE         = "DocIssueEquipmentExch.page";     
   public static final String EXCH_TEST_ISSUE_PAGE              = "DocIssueTestExch.page";          
   public static final String EXCH_COMPLETION_ISSUE_PAGE        = "DocIssueCompletionExch.page";    
   public static final String EXCH_STANDARD_ISSUE_PAGE          = "DocIssueStandardExch.page";     
   public static final String EXCH_TEMP_ISSUE_PAGE              = "DocIssueTempExch.page";     
   
   

   
   
   // Doc Issue Object States
   public final String doc_issue_preliminary;
   public final String doc_issue_approval_in_progress;
   public final String doc_issue_approved;
   public final String doc_issue_released;
   public final String doc_issue_obsolete;

   // Doc User Access IIDs
   public final String doc_user_access_all;
   public final String doc_user_access_group;
   public final String doc_user_access_user;

   // Edm File Object States
   public final String edm_file_check_in;
   public final String edm_file_check_out;
   public final String edm_file_in_briefcase;
   public final String edm_file_checked_out_to_bc;

   // Edm Macro Process IIDs
   public final String edm_macro_process_checkin;
   public final String edm_macro_process_checkout;
   public final String edm_macro_process_delete;
   public final String edm_macro_process_print;
   public final String edm_macro_process_view;
   public final String edm_macro_process_createnew;
   public final String edm_macro_process_undocheckout;
   public final String edm_macro_process_other;

   // Doc BC States
   public final String doc_briefcase_created;
   public final String doc_briefcase_exported;
   public final String doc_briefcase_under_import;
   public final String doc_briefcase_imported;

   // Document states in briefcase
   public final String doc_briefcase_issue_pending;
   public final String doc_briefcase_issue_accepted;
   public final String doc_briefcase_issue_rejected;
   public final String doc_briefcase_issue_failed;

   // Document change status in compare
   public final String doc_bc_change_unchanged;
   public final String doc_bc_change_changed;
   public final String doc_bc_change_new;
   public final String doc_bc_change_newsheet;
   public final String doc_bc_change_newrev;

   //Bug Id: 71838, start
   // Document Transmittal
   public final String transmittal_direction_in;
   public final String transmittal_direction_out;
   //Bug Id: 71838, end


   //
   // CLIENT CONSTANTS
   //

   // Translation constants for use in Client Manager
   private static String[][] climgr_translations;
   //language code used by the webserver
   private static String language_code; //Bug Id 77857
   /**
    *  Private constructor to prevent direct instantiation of class
    */
   private DocmawConstants() throws FndException
   {
      doc_issue_preliminary = null;
      doc_issue_approval_in_progress = null;
      doc_issue_approved = null;
      doc_issue_released = null;
      doc_issue_obsolete = null;
      doc_user_access_all = null;
      doc_user_access_group = null;
      doc_user_access_user = null;
      edm_file_check_in = null;
      edm_file_check_out = null;
      edm_file_in_briefcase = null;
      edm_file_checked_out_to_bc = null;
      edm_macro_process_checkin = null;
      edm_macro_process_checkout = null;
      edm_macro_process_delete = null;
      edm_macro_process_print = null;
      edm_macro_process_view = null;
      edm_macro_process_createnew = null;
      edm_macro_process_undocheckout = null;
      edm_macro_process_other = null;
      doc_briefcase_created = null;
      doc_briefcase_exported = null;
      doc_briefcase_under_import = null;
      doc_briefcase_imported = null;
      doc_briefcase_issue_pending = null;
      doc_briefcase_issue_accepted = null;
      doc_briefcase_issue_rejected = null;
      doc_briefcase_issue_failed = null;
      doc_bc_change_unchanged = null;
      doc_bc_change_changed = null;
      doc_bc_change_new = null;
      doc_bc_change_newsheet = null;
      doc_bc_change_newrev = null;
      //Bug Id: 71838, start
      transmittal_direction_in  = null;
      transmittal_direction_out = null;
      //Bug Id: 71838, end
   }


   private DocmawConstants(ASPManager mgr) throws FndException
   {
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      StringBuffer query = new StringBuffer("SELECT ");

      query.append("Doc_Issue_Api.Finite_State_Decode__('Preliminary') PRELIMINARY,");
      query.append("Doc_Issue_Api.Finite_State_Decode__('Approval In Progress') APPROVAL_IN_PROG,");
      query.append("Doc_Issue_Api.Finite_State_Decode__('Approved') APPROVED,");
      query.append("Doc_Issue_Api.Finite_State_Decode__('Released') RELEASED,");
      query.append("Doc_Issue_Api.Finite_State_Decode__('Obsolete') OBSOLETE,");
      query.append("Doc_User_Access_API.Decode('A') ALL_ACCESS,");
      query.append("Doc_User_Access_API.Decode('G') GROUP_ACCESS,");
      query.append("Doc_User_Access_API.Decode('U') USER_ACCESS,");
      query.append("Edm_File_Api.Finite_State_Decode__('Checked In') CHECKIN,");
      query.append("Edm_File_Api.Finite_State_Decode__('Checked Out') CHECKOUT,");
      query.append("Edm_File_Api.Finite_State_Decode__('In Briefcase') INBC,");
      query.append("Edm_File_Api.Finite_State_Decode__('Checked Out to Briefcase') CHECKOUTTOBC,");
      query.append("Edm_Macro_Process_Api.Decode('CHECKIN') PROCESS_CHECKIN,");
      query.append("Edm_Macro_Process_Api.Decode('CHECKOUT') PROCESS_CHECKOUT,");
      query.append("Edm_Macro_Process_Api.Decode('DELETE') PROCESS_DELETE,");
      query.append("Edm_Macro_Process_Api.Decode('PRINT') PROCESS_PRINT,");
      query.append("Edm_Macro_Process_Api.Decode('VIEW') PROCESS_VIEW,");
      query.append("Edm_Macro_Process_Api.Decode('CREATENEW') PROCESS_CREATENEW,");
      query.append("Edm_Macro_Process_Api.Decode('OTHER') PROCESS_OTHER,");
      query.append("Edm_Macro_Process_Api.Decode('UNDOCHECKOUT') PROCESS_UNDOCHECKOUT,");
      query.append("Doc_Briefcase_Api.Finite_State_Decode__('Created') BC_CREATED,");
      query.append("Doc_Briefcase_Api.Finite_State_Decode__('Exported') BC_EXPORTED,");
      query.append("Doc_Briefcase_Api.Finite_State_Decode__('Under Import') BC_UNDER_IMPORT,");
      query.append("Doc_Briefcase_Api.Finite_State_Decode__('Imported') BC_IMPORTED,");
      query.append("Doc_Briefcase_Issue_Api.Finite_State_Decode__('Pending') BC_ISSUE_PENDING,");
      query.append("Doc_Briefcase_Issue_Api.Finite_State_Decode__('Accepted') BC_ISSUE_ACCEPTED,");
      query.append("Doc_Briefcase_Issue_Api.Finite_State_Decode__('Rejected') BC_ISSUE_REJECTED,");
      query.append("Doc_Briefcase_Issue_Api.Finite_State_Decode__('Failed') BC_ISSUE_FAILED,");
      query.append("Doc_Bc_Change_Status_Api.Decode('Unchanged') BC_CHANGE_UNCHANGED,");
      query.append("Doc_Bc_Change_Status_Api.Decode('Changed') BC_CHANGE_CHANGED,");
      query.append("Doc_Bc_Change_Status_Api.Decode('New') BC_CHANGE_NEW,");
      query.append("Doc_Bc_Change_Status_Api.Decode('New Sheet') BC_CHANGE_NEWSHEET,");
      //Bug Id: 71838, start
      query.append("Doc_Bc_Change_Status_Api.Decode('New Rev') BC_CHANGE_NEWREV,");
      query.append("Transmittal_Direction_API.Decode('IN') TRANSMITTAL_DIRECTION_IN,");
      query.append("Transmittal_Direction_API.Decode('OUT') TRANSMITTAL_DIRECTION_OUT");
      //Bug Id: 71838, end

      query.append(" FROM DUAL");

      trans.addQuery("IIDSTATEQUERY", query.toString());	//SQLInjections_Safe AMNILK 20070810
      trans = mgr.perform(trans);

      doc_issue_preliminary          = getDBValue(trans, "IIDSTATEQUERY/DATA/PRELIMINARY");
      doc_issue_approval_in_progress = getDBValue(trans, "IIDSTATEQUERY/DATA/APPROVAL_IN_PROG");
      doc_issue_approved             = getDBValue(trans, "IIDSTATEQUERY/DATA/APPROVED");
      doc_issue_released             = getDBValue(trans, "IIDSTATEQUERY/DATA/RELEASED");
      doc_issue_obsolete             = getDBValue(trans, "IIDSTATEQUERY/DATA/OBSOLETE");

      doc_user_access_all            = getDBValue(trans, "IIDSTATEQUERY/DATA/ALL_ACCESS");
      doc_user_access_group          = getDBValue(trans, "IIDSTATEQUERY/DATA/GROUP_ACCESS");
      doc_user_access_user           = getDBValue(trans, "IIDSTATEQUERY/DATA/USER_ACCESS");

      edm_file_check_in              = getDBValue(trans, "IIDSTATEQUERY/DATA/CHECKIN");
      edm_file_check_out             = getDBValue(trans, "IIDSTATEQUERY/DATA/CHECKOUT");
      edm_file_in_briefcase          = getDBValue(trans, "IIDSTATEQUERY/DATA/INBC");
      edm_file_checked_out_to_bc     = getDBValue(trans, "IIDSTATEQUERY/DATA/CHECKOUTTOBC");

      edm_macro_process_checkin      = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_CHECKIN");
      edm_macro_process_checkout     = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_CHECKOUT");
      edm_macro_process_delete       = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_DELETE");
      edm_macro_process_print        = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_PRINT");
      edm_macro_process_view         = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_VIEW");
      edm_macro_process_createnew    = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_CREATENEW");
      edm_macro_process_other        = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_OTHER");
      edm_macro_process_undocheckout = getDBValue(trans, "IIDSTATEQUERY/DATA/PROCESS_UNDOCHECKOUT");

      doc_briefcase_created          = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CREATED");
      doc_briefcase_exported         = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_EXPORTED");
      doc_briefcase_under_import     = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_UNDER_IMPORT");
      doc_briefcase_imported         = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_IMPORTED");

      doc_briefcase_issue_pending    = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_ISSUE_PENDING");
      doc_briefcase_issue_accepted   = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_ISSUE_ACCEPTED");
      doc_briefcase_issue_rejected   = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_ISSUE_REJECTED");
      doc_briefcase_issue_failed     = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_ISSUE_FAILED");

      doc_bc_change_unchanged        = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CHANGE_UNCHANGED");
      doc_bc_change_changed          = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CHANGE_CHANGED");
      doc_bc_change_new              = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CHANGE_NEW");
      doc_bc_change_newsheet         = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CHANGE_NEWSHEET");
      doc_bc_change_newrev           = getDBValue(trans, "IIDSTATEQUERY/DATA/BC_CHANGE_NEWREV");
      //Bug Id: 71838, start
      transmittal_direction_in       = getDBValue(trans, "IIDSTATEQUERY/DATA/TRANSMITTAL_DIRECTION_IN");
      transmittal_direction_out      = getDBValue(trans, "IIDSTATEQUERY/DATA/TRANSMITTAL_DIRECTION_OUT");
      //Bug Id: 71838, end
   }


   /**
    * Returns the db value for the given parameter from the response
    * buffer. Raises an exception if the value in the response buffer
    * is empty
    * 
    */
   private String getDBValue(ASPTransactionBuffer trans, String name) throws FndException
   {
      String value = trans.getValue(name);

      if (value == null || "".equals(value))
         throw new FndException("Error creating DocmawConstants: The parameter &1 in the response buffer is empty. The probable cause may be due to insufficient grants to methods 'Decode' or 'Finite_State_Decode__' in the corresponding API", name);

      return value;
   }

   public static String getCorrespondingDocIssuePage(String docClass,String subDocClass){
      if(DocmawConstants.PROJ_RECEIVE.equals(docClass)){
         if(DocmawConstants.PROJ_RECEIVE_FAX.equals(subDocClass)){
            return PROJ_RECEIVE_FAX_ISSUE_PAGE;
         } else if(DocmawConstants.PROJ_RECEIVE_MEMO.equals(subDocClass)){
            return PROJ_RECEIVE_MEMO_ISSUE_PAGE;
         } else if(DocmawConstants.PROJ_RECEIVE_MEETING.equals(subDocClass)){
            return PROJ_RECEIVE_MEETING_ISSUE_PAGE;
         } else{
            return null;
         }
      } else if(DocmawConstants.PROJ_SEND.equals(docClass)){
         if(DocmawConstants.PROJ_SEND_FAX.equals(subDocClass)){
            return PROJ_SEND_FAX_ISSUE_PAGE;
         } else if(DocmawConstants.PROJ_SEND_MEMO.equals(subDocClass)){
            return PROJ_SEND_MEMO_ISSUE_PAGE;
         } else if(DocmawConstants.PROJ_SEND_MEETING.equals(subDocClass)){
            return PROJ_SEND_MEETING_ISSUE_PAGE;
         } else{
            return null;
         }
      } else if(DocmawConstants.PROJ_PROPHASE.equals(docClass)){
         return PROJ_PROPHASE_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_CONSTRUCT.equals(docClass)){
         return PROJ_CONSTRUCT_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_DESIGN.equals(docClass)){
         return PROJ_DESIGN_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_EQUIPMENT.equals(docClass)){
         return PROJ_EQUIPMENT_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_TEST.equals(docClass)){
         return PROJ_TEST_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_COMPLETION.equals(docClass)){
         return PROJ_COMPLETION_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_STANDARD.equals(docClass)){
         return PROJ_STANDARD_ISSUE_PAGE;
      } else if(DocmawConstants.PROJ_TEMP.equals(docClass)){
         return PROJ_TEMP_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_RECEIVE.equals(docClass)){
         if(DocmawConstants.EXCH_RECEIVE_FAX.equals(subDocClass)){
            return EXCH_RECEIVE_FAX_ISSUE_PAGE;
         } else if(DocmawConstants.EXCH_RECEIVE_MEMO.equals(subDocClass)){
            return EXCH_RECEIVE_MEMO_ISSUE_PAGE;
         } else if(DocmawConstants.EXCH_RECEIVE_MEETING.equals(subDocClass)){
            return EXCH_RECEIVE_MEETING_ISSUE_PAGE;
         } else{
            return null;
         }
      } else if(DocmawConstants.EXCH_SEND.equals(docClass)){
         if(DocmawConstants.EXCH_SEND_FAX.equals(subDocClass)){
            return EXCH_SEND_FAX_ISSUE_PAGE;
         } else if(DocmawConstants.EXCH_SEND_MEMO.equals(subDocClass)){
            return EXCH_SEND_MEMO_ISSUE_PAGE;
         } else if(DocmawConstants.EXCH_SEND_MEETING.equals(subDocClass)){
            return EXCH_SEND_MEETING_ISSUE_PAGE;
         } else{
            return null;
         }
      } else if(DocmawConstants.EXCH_PROPHASE.equals(docClass)){
         return EXCH_PROPHASE_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_CONSTRUCT.equals(docClass)){
         return EXCH_CONSTRUCT_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_DESIGN.equals(docClass)){
         return EXCH_DESIGN_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_EQUIPMENT.equals(docClass)){
         return EXCH_EQUIPMENT_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_TEST.equals(docClass)){
         return EXCH_TEST_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_COMPLETION.equals(docClass)){
         return EXCH_COMPLETION_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_STANDARD.equals(docClass)){
         return EXCH_STANDARD_ISSUE_PAGE;
      } else if(DocmawConstants.EXCH_TEMP.equals(docClass)){
         return EXCH_TEMP_ISSUE_PAGE;
      } else{
         return null;
      }
   }
   
   public static boolean isLibaryDocClass(String docClass){
      boolean result = true;
      if(DocmawConstants.PROJ_RECEIVE.equals(docClass)){
      } else if(DocmawConstants.PROJ_SEND.equals(docClass)){
      } else if(DocmawConstants.PROJ_PROPHASE.equals(docClass)){
      } else if(DocmawConstants.PROJ_CONSTRUCT.equals(docClass)){
      } else if(DocmawConstants.PROJ_DESIGN.equals(docClass)){
      } else if(DocmawConstants.PROJ_EQUIPMENT.equals(docClass)){
      } else if(DocmawConstants.PROJ_TEST.equals(docClass)){
      } else if(DocmawConstants.PROJ_COMPLETION.equals(docClass)){
      } else if(DocmawConstants.PROJ_STANDARD.equals(docClass)){
      } else {
         result = false;
      }
      return result;
   }
   
   public static String getLibarayDocClasses()
   {
      AutoString res = new AutoString();
      res.append("(");
      res.append("'" + PROJ_RECEIVE + "',");
      res.append("'" + PROJ_SEND + "',");
      res.append("'" + PROJ_PROPHASE + "',");
      res.append("'" + PROJ_CONSTRUCT + "',");
      res.append("'" + PROJ_DESIGN + "',");
      res.append("'" + PROJ_EQUIPMENT + "',");
      res.append("'" + PROJ_TEST + "',");
      res.append("'" + PROJ_COMPLETION + "',");
      res.append("'" + PROJ_STANDARD + "'");
      res.append(")");
      return res.toString();
   }
   
   /**
    *  Returns a reference to an instance of DocmawConstants.
    *  Does not keep a reference to the ASPManager passed.
    */
   public static DocmawConstants getConstantHolder(ASPManager mgr) throws FndException
   {
      synchronized (DocmawConstants.class)
      {
         if (const_map == null)
         {
            const_map = new HashMap();
         }

         DocmawConstants dm_const;
         if ((dm_const = (DocmawConstants)const_map.get(mgr.getLanguageCode())) != null)
         {
            return dm_const;
         }
         else
         {
            dm_const = new DocmawConstants(mgr);
            const_map.put(mgr.getLanguageCode(), dm_const);
            return dm_const;
         }
      }
   }


   /**
    * Returns an array containing translation constants for
    * use in the Client Manager ocx.
    */
   public static String[][] getCliMgrTranslations(ASPManager mgr)
   {
      if (climgr_translations == null || language_code != mgr.getLanguageCode()) //Bug Id 77857, Modified the if condition and added language_code
      {
	  language_code = mgr.getLanguageCode(); //Bug Id 77857
         climgr_translations = new String[][] {
            {"ID_DF5000", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5000: Select your document folder")},
            {"ID_DF5001", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5001: Browse...")},
            {"ID_DF5002", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5002: OK")},
            {"ID_DF5003", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5003: Set document folder...")},
            {"ID_DF5004", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5004: Choose a folder")},
            //Bug 55611, Start, Modified text for ID_DF5005 and added ID_DF5006 
            {"ID_DF5005", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5005: Select Checkout Path...")},
            {"ID_DF5006", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5006: Cancel")},
            //Bug 55611, End
            {"ID_ER1000", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1000: Error in ")},
            {"ID_ER1001", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1001: No folder chosen. Operation cancelled.")},
            {"ID_ER1002", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1002: The OCX is not initialized.")},
            {"ID_ER1003", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1003: Errors during communication with")},
            {"ID_ER1004", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1004: Error message from server ")},
            {"ID_ER1005", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1005: No valid file was choosen. Operation cancelled")},
            {"ID_ER1006", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1006: Can't find file. Operation cancelled.")},
            {"ID_ER1007", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1007: Can't find file. Operation cancelled")},
            {"ID_ER1008", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1008: Can't find file to check in. Operation cancelled.")},
            {"ID_ER1009", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1009: Can't find the view-copy of the original file. Operation cancelled.")},
            {"ID_ER1010", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1010: View copy required and none was found. Operation cancelled.")},
            {"ID_ER1011", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1011: The file :P1 you are trying to check in is empty.")},
            {"ID_ER1012", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1012: The view file you are trying to check in is empty")},
            {"ID_ER1013", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1013: Invalid Handle Key")},
            {"ID_ER1014", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1014: Error when loading response XML document. No child nodes exist.")},
            {"ID_ER1015", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1015: Error when loading response XML document. Parse error.")},
            {"ID_ER1016", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1016: No view file extensions configured. Contact your administrator.")},
            {"ID_ER1017", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1017: Operation canceled.")},
            {"ID_ER1018", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1018: view-copies were found. Please remove all but one of them and try again. Operation cancelled.")},
            {"ID_ER1019", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1019: The view file you are trying to check in is empty.")},
            {"ID_ER1020", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1020: Can't find file to import. Operation cancelled.")},
            {"ID_ER1022", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1022: Redline files were found. Please remove all but one of them and try again. Operation cancelled.")},
            {"ID_ER1023", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1023: Can't find Redline file. Operation cancelled.")},
            {"ID_ER1024", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1024: The Redline file you are trying to check in is empty.")},
            {"ID_ER1025", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1025: Can't find a Redline file to check in, operation cancelled.")},
            {"ID_ER1026", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1026: The file :P1 is required for operation to continue but was not found. Operation cancelled.")},
            {"ID_ER1028", mgr.translate("DOCMAWEDMMACROOCX_ID_ER1028: More than one file of document type :P1 (:P2) was found. The system couldn't decide which file to Check In. Please remove all but one of them and try again. Operation cancelled.")},
            {"ID_VC1000", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1000: Do you want to replace the existing view copy with file type")},
            {"ID_VC1001", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1001: with the new view copy of file type")},
            {"ID_VC1002", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1002: Check in view copy...")},
            {"ID_VC1003", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1003: No view-copy will be checked in. Continue anyway?")},
            {"ID_VC1004", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1004: view-copies were found. Only checking in original file.")},
            {"ID_VC1005", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1005: View File does not exist, do you still want to Check In the Document?")},
            {"ID_VC1006", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1006: No View File")},
            {"ID_VC1007", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1007: File transfer")},
            {"ID_VC1008", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1008: Uploading file. Please wait.")},
            {"ID_VC1009", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1009: Downloading file. Please wait.")},
            {"ID_VC1011", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1011: The local file was not found and could not be removed. The document is still going to be unreserved.")},
            {"ID_VC1013", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1013: Do you want to replace the existing Redline with file type")},
            {"ID_VC1014", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1014: with the new Redline file of file type")},
            {"ID_VC1015", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1015: Check in Redlin File...")},
            {"ID_VC1016", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1016: The selected file type is not registered as a valid EDM file type. Do you want to select another file?")},
            {"ID_VC1017", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1017: Invalid File Type. Operation cancelled.")}, // Usage removed from the ocx.
            {"ID_VC1018", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1018: No file extensions have been configured for document type")},
            {"ID_VC1019", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1019: Warning - The file :P1 is stored on read-only media and cannot be deleted.")},
            {"ID_VC1020", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1020: The file cannot be deleted")},
            {"ID_VC1021", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1021: Cannot check the file in because it is locked. Close the file and try again.")},
            {"ID_VC1022", mgr.translate("DOCMAWEDMMACROOCX_ID_VC1022: The View-copy file is older than original file. Continue anyway?")},
            {"ID_ER2020", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2020: MSXML object name not supported:")},
            {"ID_ER2021", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2021: Could not create any suitable MSXML object for ")},
            {"ID_ER2022", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2022: FTP Error :")},
            {"ID_ER2023", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2023: The documet file is open. Close the file and try checking in again.")},
            {"ID_ER2024", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2024: Checked Out document(s) are still Open! Close them before Checking In.")},
            {"ID_ER2025", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2025: This document briefcase folder is locked. To unlock the folder, close the Excel template, any briefcase document files, and then navigate out of the briefcase folder.")},
            {"ID_ER2026", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2026: Invalid path name")},
            {"ID_ER2027", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2027: The current file will be checked out to")},
            {"ID_ER2028", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2028: , but there is a file at that location already. Would you like to overwrite it?")},
            {"ID_ER2029", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2029: Confirm overwriting of file.")},
            {"ID_ER2030", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2030: The selected path is not valid. Operation Cancelled.")},
            {"ID_ER2031", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2031: The file :P1 already exists. Do you want to overwrite it?")},
            {"ID_ER2032", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2032: File Type - File Extension Mismatch.")},
            {"ID_ER2033", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2033: The extension of the file you have selected does not match the file type. This will cause the file extension to be renamed to match the file type. Continue?")},
            {"ID_ER2034", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2034: File Checkin Cancelled.")},
            {"ID_ER2035", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2035: Can't find file :P1 to check in, operation cancelled.")},
            {"ID_ER2036", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2036: Web page does not reponse.")},
            {"ID_ER2037", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2037: Unable to open :P1 No application is associated with this file type or unable to launch the associated application. File is available inside local check out folder.")},
            {"ID_ER2038", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2038: Unable to Print :P1 No application is associated with this file type or unable to launch the associated application. A copy of the file has been checked out to the local check out folder.")},
            //Bug ID 54793, Start
            {"ID_ER2039", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2039: The path does not exist.")}, 
            {"ID_ER2040", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2040: The user does not have full control over the local checkout folder. Please check security rights and retry the operation.")}, 
            //Bug ID 54793, End
            //Bug ID 55611, Start
            {"ID_ER2041", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2041: The document folder is either invalid or it does not exist. Make sure that the folder exists and retry the operation.")}, 
            //Bug ID 55611, End
	    //Bug Id 71463, start
	    {"ID_ER2042", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2042: The length of the local file name exceeds the limit of 255 characters. File name will be shortened to :P1.")}, 
	    {"ID_ER2043", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2043: File Name Length Limitation")}, 
	    //Bug Id 71463, end
	    //Bug Id 72460, start
	    {"ID_ER2044", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2044: Disk space is not sufficient for retrieval of the file. Operation aborted, incomplete file will be deleted.")},
	    //Bug Id 72460, end
	    {"ID_ER2045", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2045: File in application server folder is not same size as file in the disk. Operation aborted.")}, //Bug Id 69562
            //Bug Id 78749, start
	    {"ID_ER2046", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2046: Undo checkout is done, but the local file ")},
	    {"ID_ER2047", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2047:  could not be removed.")},
	    {"ID_ER2048", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2048: IFS Applications - Information Message")},
            //Bug Id 78749, end
	    //Bug Id 85187, start
	    {"ID_DF5007", mgr.translate("DOCMAWEDMMACROOCX_ID_DF5007: Valid IFS File Types")},
	    //Bug Id 85187, end
       {"ID_ER2049", mgr.translate("DOCMAWEDMMACROOCX_ID_ER2049: File(s) without extension(s) are not allowed. Operation cancelled.")} //Bug Id 92125
         };
      }
      return climgr_translations;
   }

}
