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
*  File        : DocBriefcaseCompare.java
*  NiSilk        2003-04-25  Created
*  NiSilk        2003-04-28  Modified the detail section
*  NiSilk        2003-04-30  Modified the detail section
*  NiSilk        2003-05-06  Modified getDescription()
*  Bakalk        2003-05-09  Implemented Accept,Reject (for compare only).
*  NiSilk        2003-05-09  Modified the detail section - Edit section (method getContents)
*                            Made doc_class,doc_no_doc_sheet_doc_rev fields read only
*                            Modified method adjust()
*  InoSlk        2003-05-09  Implemented Reject Document (multirow and single row).
*  Inoslk        2003-05-13  Added action 'Compare'.
*  NiSilk        2003-05-13  Added methods viewExportWithExtViewer() and transferToEdmBriefcase()
*                            to implement View Unpacked Document with External Viewer.
*  Bakalk        2003-05-14  Modified transferToEDMBriefcase().
*  NiSilk        2003-05-19  Modified method adjust()
*                            Renamed command View Existing with External Viewer to view Exported.
*  Bakalk        2003-05-19  Refreshed the rowset after EdmBriefcase being called.
*  Dikalk        2003-05-22  Reviewed the code in this page. Fixed a few bugs in the layout,
*                            changed translation constants to have "DOCMAWDOCBRIEFCASECOMPARE" prefix,
*                            removed unncessary fields in preDefine
*  NiSilk        2003-05-23  Implemented Unlock Document (Single/Multi row), added method unlockDocuments()
*  NiSilk        2003-05-23  Implemented view existing document with external viewer
*                            Added method viewExistingWithExtViewer()
*  NiSilk        2003-05-27  Modified method adjust().
*  Bakalk        2003-05-27  Added new method remove_new_document.
*  NiSilk        2003-05-27  Modified method adjust() to disable the action button in mulrowlayout mode
*  InoSlk        2003-05-28  Added method openInEditLayOut, edited methods run and compareDocuments to support
*                            functionality in DocCompareWindow.
*                            Added valid conditions for Action 'Compare'.
*  NiSilk        2003-05-28  Added method showNewRevision() and added radio button Show New Revision
*                            Modified method showModeChanged()
*  NiSilk        03-06-2003  Removed fields INCLUDE_VIEW_COPY and INCLUDE_RED_LINE from the header section.
*  NiSilk        04-06-2003  Modified method transferToEDMBriefcase() after removing fields INCLUDE_VIEW_COPY
*                            and INCLUDE_RED_LINE.
*  InoSlk        05-06-2003  Corrections in rejectDocuments() and trasnferToBriefcase.
*  NiSilk        06-12-2003  Modified methods showNewRevision,showChangedDocument,showUnChangedDocument
*                            okFindOnBCStatusOnNew after changing the db values of doc_bc_change_status.
*  Bakalk        06-26-2003  Added command: Accept.
*  Nisilk        06-30-2003  Added lu_name in preDefine
*  InoSlk        07-16-2003  Correction in 'transferToEDMBriefcase(String action)'
*                            to fix the row selection error in action 'Accept'.
*  Bakalk        07-18-2003  Fixed the error rising when accepting after save & return.
*  NiSilk        07-25-2003  Modified validations in methods rejectDocuments(), transferToBriefcase(), unlockDocuments().
*                            Fixed the error when trying to unlock/reject documents with change status New/New Rev/New Sheet before accepting.
*                            Fixed the system error occured when trying to accept documents,when there were no pending doc.
*                            Update validations after setting the DOC_ISSUE_STATE default 'Pending'.
* NiSilk         07-28-2003  Modified methods adjust() and RejectDocuments().
* Bakalk         07-29-2003  Modified Predefine(). Removed tranlation method from FileExist tag.
* InoSlk         08-14-2003  Call ID 100800: Modifed method unlockDocuments().
* InoSlk         08-27-2003  Call ID 101731: Modified doReset() and clone().
* Dikalk         03-08-2003  Call ID: 102318. Fixed bug in javascript methods selectAll and DeSelectAll
* Bakalk         08-09-2003  Call Id: 102758. Set Lu name and Object Id editable.
* Bakalk         09-09-2003  Call Id: 102919. Made links and combo boxes not wrapable.
* NiSilk         09-10-2003  Call Id: 102843. Added javascript method lovObjectId. Added Lov's for Object Id and Lu Name.
* NiSilk         09-11-2003  Added javascript method validateObjectId().
* Bakalk         09-11-2003  Call id: 102841. Modified openInEditLayOut.
* NiSilk         09-16-2003  Call ID: 103395. Added a where condition to the LuNameLov.page in preDefine.
* Bakalk         09-22-2003  Call Id: 103650. Now compare window loaded in a seperate window.
* Bakalk         19-21-2003  Call Id: 107632. Changed the definition of "FILEEXIST" in predefine.
* NiSilk         06-11-2003  Call Id: 110303. Changed selectRows() to storeSelections() to fix the row selection error in web kit 3.5.1.
* SHTHLK         29-03-2004  Bug Id 43620, Modified saveReturn method to refresh the the rows after edit.
* INOSLK         01-04-2004  Bug ID 43624, Modified transferToEdmMacro(doc_type,action,disable_macro) and adjust().
* INOSLK         07-04-2004  Bug ID 43625, Added DOC_TITLE in preDefine().
* INOSLK         08-04-2004  Bug ID 43625, Set DOC_TITLE size and maxlength in preDefine().
* INOSLK         09-04-2004  Bug ID 43698, Restricted editing Released documents. Added editUnpacked().
* INOSLK         09-04-2004  Modified preDefine(),clone(),doReset(),run(),adjust().
* INOSLK         19-04-2004  Bug ID 43915, Modified transferToBriefcase().
* INOSLK         07-06-2004  Bug ID 45036, Added getOldRevision(). Modified remove_new_document() and adjust().
* INOSLK         09-06-2004  Bug ID 45219, Modified methods preDefine() and acceptDocuments().
* INOSLK         10-06-2004  Bug ID 45219, Modified acceptDocuments().
* INOSLK         11-06-2004  Bug ID 45219, Modified acceptDocuments().
* INOSLK         14-06-2004  Bug ID 45219, Modified acceptDocuments() so that itemset filter is turned off on return.
* INOSLK	 20-07-2004  Bug ID 45944, Modified unlockDocuments() and preDefine().
* SHTHLK	 31-08-2004  Bug ID 46692, Added a new translation constant for Sign2 in preDefine().
* KARALK	 10-09-2004  Bug Id 46726  Modified Predefine(), Adjust(). in adjust method a new  addCommandValidConditions statement is added. a addCommandValidConditions is removed which was added for Bug Id 43698
* INOSLK	 10-09-2004  Bug ID 46865, Modified adjust() to disable edit for Approved/Released docs in single row layout.
* INOSLK         16-09-2004  Bug ID 46925, Modifed acceptDocuments() and preDefine().
* INOSLK         20-09-2004  Bug ID 47011, Modified preDefine().
* INOSLK         21-09-2004  Bug ID 47010, Added okFindOnBcIssueStatus() and showPending(). Modified showModeChanged(),
* INOSLK                                   getContents(), refreshRow(), unlockDocuments().
* INOSLK         12-10-2004  Bug ID 47334, Modified okFindITEM0(), okFindOnBCStatus(), okFindOnBCStatusOnNew(),
* INOSLK		     okFindOnBCStatusOnNewRev(), okFindOnBcIssueStatus().
* INOSLK         13-10-2004  Bug ID 47333, Modified preDefine(), remove_new_document() and getContents().
* INOSLK		     Removed method getOldRevision().
* INOSLK         14-10-2004  Bug ID 47320, Modified preDefine() and adjust() to make doc_class editable with an LOV for new documents.
* INOSLK         02-11-2004  Bug ID 47637, Added columns FORMAT_SIZE,SCALE and REASON_FOR_ISSUE in preDefine(),transferToEDMBriefcase().
* INOSLK	 		  10-11-2004  bug ID 47171, Added column REJECT_ID. Modified preDefine(), adjust(), rejectDocuments().
* INOSLK	        12-11-2004  Bug ID 47171, Modified acceptDocuments() and preDefine().
* INOSLK         15-11-2004  Bug ID 47907, Modified rejectDocuments(),unlockDocuments(),preDefine().
* SHTHLK         17-11-2004  Bug ID 48032, Modified saveReturn() not to check whether the doc sheet already exists when DOC_NO is empty.
* SHTHLK                     Also changed the error message.
* INOSLK         23-11-2004  Bug ID 48138, Modified adjust() and preDefine().
* GACOLK         28-04-2005  Bug ID 50336, Reversed changes done in Bug 46925. Made DOC_CLASS, DOC_NO fields editable when state is New Sheet.
* AMNALK         27-10-2005  Merged Bug Id 53332.
* ThWilk         21-02-2006  Fixed Call 134195, Modified method completeSettingBookingList.
* BAKALK         17-03-2006  Fixed call 13508, Modified object connection URL. 
* RUCSLK			  21-03-2006  Merged Bug Id 54962. 	
* BAKALK         18-07-2006  Bug ID 58216, Fixed Sql Injection.
* BAKALK         11-09-2006  Bug ID 58216, Modified [ DOC_BC_CHANGE_STATUS_DB IN ('3','4') --> DOC_BC_CHANGE_STATUS_DB IN ('New','New Sheet')]
* ILSOLK         07-08-2007  Eliminated SQLInjection. 
* BAKALK         08-08-2007  Call ID 145225, Modified preDefine().
* NaLrlk         09-08-2007  XSS Correction.
* ASSALK         15-08-2007  Merged Bug ID 58526, Modified getContents().
* DINHLK         13-12-2007  Bug Id 65462, Modified method getContents().
* DULOLK         07-02-2008  Bug Id 65462, Modified method getContents().
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.docmaw.edm.DocumentTransferHandler;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class DocBriefcaseCompare extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocBriefcase");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext       ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock         headblk;
   private ASPRowSet        headset;
   private ASPCommandBar    headbar;
   private ASPTable         headtbl;
   private ASPBlockLayout   headlay;
   private ASPBlock         itemblk0;
   private ASPRowSet        itemset0;
   private ASPCommandBar    itembar0;
   private ASPTable         itemtbl0;
   private ASPBlockLayout   itemlay0;
   private ASPField         f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer keys;
   private ASPQuery q;

   private int headrowno;
   private int itemRowNo;

   private boolean launch_edm_briefcase = false;
   private boolean launch_edm_macro = false;
   private boolean checkMode;
   private boolean call_bookinglist_lov = false ;
   private boolean luanch_compare_window = false ;

   private String sShowMode;
   public  String checkVal;
   private String url;

   private String sBcUnderImport;
   private String sBcIssuePending;
   private String sBcIssueAccepted;
   private String sBcIssueRejected;
   private String sBcExported;

   private String change_state_new_doc;
   private String change_state_new_sheet;
   private String change_state_changed;
   private String change_state_new_revision;
   private String change_state_unchanged;

   // Bug ID 43698, inoslk, start
   private String doc_state_released;
   // Bug ID 43698, inoslk, end

   //===============================================================
   // Construction
   //===============================================================
   public DocBriefcaseCompare(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Initializing mutable instance variables
      trans           = null;
      cmd             = null;
      keys            = null;
      q               = null;

      headrowno       = 0;
      itemRowNo       = 0;

      launch_edm_briefcase = false;
      launch_edm_macro     = false;
      checkMode            = false;
      call_bookinglist_lov = false;

      sShowMode       = null;
      checkVal        = null;
      url             = null;

      sBcUnderImport   = null;
      sBcIssuePending  = null;
      sBcIssueAccepted = null;
      sBcIssueRejected = null;
      sBcExported      = null;

      change_state_new_doc      = null;
      change_state_new_sheet    = null;
      change_state_changed      = null;
      change_state_new_revision = null;
      change_state_unchanged    = null;
      // Bug ID 43698, inoslk, start
      doc_state_released        = null;
      // Bug ID 43698, inoslk, end

      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocBriefcaseCompare page = (DocBriefcaseCompare)(super.clone(obj));

      // Initialize mutable attributes
      page.trans           = null;
      page.cmd             = null;
      page.keys            = null;
      page.q               = null;

      page.headrowno       = 0;
      page.itemRowNo       = 0;

      page.launch_edm_briefcase = false;
      page.launch_edm_macro     = false;
      page.checkMode            = false;
      page.call_bookinglist_lov = false;

      page.sShowMode       = null;
      page.checkVal        = null;
      page.url             = null;

      page.sBcUnderImport   = null;
      page.sBcIssuePending  = null;
      page.sBcIssueAccepted = null;
      page.sBcIssueRejected = null;
      page.sBcIssuePending  = null;

      page.change_state_new_doc      = null;
      page.change_state_new_sheet    = null;
      page.change_state_changed      = null;
      page.change_state_new_revision = null;
      page.change_state_unchanged    = null;
      // Bug ID 43698, inoslk, start
      page.doc_state_released        = null;
      // Bug ID 43698, inoslk, end

      // Clone immutable attributes
      page.ctx     = page.getASPContext();
      page.fmt     = page.getASPHTMLFormatter();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay0 = page.itemblk0.getASPBlockLayout();
      page.f        = page.getASPField(f.getName());

      return page;
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      checkVal = ctx.readValue("SELECTED_RADIO","4");
      itemRowNo = (int)ctx.readNumber("ITEMROWNO",0);
      checkMode = ctx.readFlag("CHANGED",false);

      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      sBcUnderImport            = dm_const.doc_briefcase_under_import;
      sBcIssuePending           = dm_const.doc_briefcase_issue_pending;
      sBcIssueAccepted          = dm_const.doc_briefcase_issue_accepted;
      sBcIssueRejected          = dm_const.doc_briefcase_issue_rejected;
      sBcExported               = dm_const.doc_briefcase_exported;
      change_state_new_doc      = dm_const.doc_bc_change_new;
      change_state_new_sheet    = dm_const.doc_bc_change_newsheet;
      change_state_changed      = dm_const.doc_bc_change_changed;
      change_state_unchanged    = dm_const.doc_bc_change_unchanged;
      change_state_new_revision = dm_const.doc_bc_change_newrev;
      // Bug ID 43698, inoslk, start
      doc_state_released        = dm_const.doc_issue_released;
      // Bug ID 43698, inoslk, end

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED"))) //On mode change
         showModeChanged();
      else if (!mgr.isEmpty(mgr.readValue("COMMAN_COMMAND"))) //comman commands
         eval(mgr.readValue("COMMAN_COMMAND")); // SQLInjection_Safe ILSOLK 20070807
      else if (!mgr.isEmpty(mgr.readValue("REFRESH_ROW"))) //just to refresh the rowset
         refreshRow();
      else if (!mgr.isEmpty(mgr.readValue("SET_BOOKINGLIST"))) //afer selecting booking list
         completeSettingBookingList();
      else if (!mgr.isEmpty(mgr.readValue("LINE_NO_TO_EDIT"))) //afer selecting booking list
         openInEditLayOut();


      if (mgr.dataTransfered())
      {
         ASPBuffer temp = mgr.getTransferedData();
         runQuery();
      }
      adjust();
      ctx.writeValue("SELECTED_RADIO", checkVal);
      ctx.writeFlag("CHANGED", checkMode);
   }


   //=============================================================================
   //  Command Bar functions for Head Part
   //=============================================================================

   public void runQuery()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(headblk);
      q.addOrCondition(mgr.getTransferedData());
      q.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      if (headset.countRows()>0)
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFindITEM0();
      }
   }


   public void refreshRow()
   {
      // Bug ID 47010, inoslk, start
      showModeChanged();
      // Bug ID 47010, inoslk, end
      transferToBriefcase();
      //headset.refreshRow(); Bug Id= 54962
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
         mgr.showAlert(mgr.translate("DOCMAWDOCBRIEFCASECOMPARENODATA: No data found."));

      trans.clear();
   }


   //=============================================================================
   //  Command Bar functions for Detail Part
   //=============================================================================
   public void setBookingList()
   {
      if (itemlay0.isMultirowLayout())
      {
         itemset0.storeSelections();
         itemset0.setFilterOn();
      }
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }
      String line_no = itemset0.getValue("LINE_NO");
      ctx.writeValue("SELECTED_LINENO",line_no);
      call_bookinglist_lov = true;
      //itemset0.unselectRow();
      //itemset0.setFilterOff();
   }
   public void completeSettingBookingList()
   {
      ASPManager mgr = getASPManager();
      String booking_list = mgr.readValue("SET_BOOKINGLIST");


      String briefcase_no = headset.getValue("BRIEFCASE_NO");
      String line_no      = ctx.readValue("SELECTED_LINENO","0");
      String attr = "BOOKING_LIST" + (char)31 + booking_list  + (char)30;

      trans.clear();
      cmd = trans.addCustomCommand("SETBOOKINGLIST","DOC_BRIEFCASE_UNPACKED_API.Modify");
      cmd.addParameter("IN_1",briefcase_no);
      cmd.addParameter("IN_1",line_no);
      cmd.addParameter("IN_1",attr);
      mgr.perform(trans);
      itemset0.refreshRow();
      itemset0.unselectRows();
       if (itemlay0.isMultirowLayout())
          itemset0.setFilterOff();

   }

   public void okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addEmptyQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      //bug 58216 ends
      // Bug ID 47334, inoslk, start
      q.setOrderByClause("TO_NUMBER(LINE_NO)");
      // Bug ID 47334, inoslk, end
      q.includeMeta("ALL");
      mgr.querySubmit(trans, itemblk0);
   }

   public void viewExistingDocument()
   {
      transferToEdmMacro("ORIGINAL", "VIEW", true);
   }


   public void viewUnpackedDocument()
   {
      transferToEdmBriefcase("ORIGINAL", "VIEW_EXPORTED");
   }


   public void transferToEdmBriefcase(String doc_type, String action)
   {
      ASPManager mgr = getASPManager();
      String brief_no = headset.getRow().getValue("BRIEFCASE_NO");

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      ASPBuffer doc_buf = itemset0.getSelectedRows("LINE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOC_BC_CHANGE_STATUS,BOOKING_LIST");

      ASPBuffer act_buf = mgr.newASPBuffer();
      act_buf.addItem("BRIEF_NO", brief_no);
      act_buf.addItem("ACTION", action);
      act_buf.addItem("DOCTYPE",doc_type);

      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmBriefcase.page", act_buf, doc_buf);
      launch_edm_briefcase = true;
      itemset0.unselectRows();
   }


   public void transferToEdmMacro(String doc_type, String action, boolean disable_macro)
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isSingleLayout())
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }
      else
         itemset0.storeSelections();

      ASPBuffer act_buf = mgr.newASPBuffer();
      act_buf.addItem("DOC_TYPE", doc_type);
      act_buf.addItem("FILE_ACTION", action);

      if (disable_macro)
      {
         act_buf.addItem("EXECUTE_MACRO", "FALSE");
      }

      ASPBuffer doc_buf = itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,EDM_FILE_TYPE");

      // Bug ID 43624, inoslk, start
      if (action.equals("VIEW") || action.equals("VIEWWITHEXTVIEWER"))
      {
         String sOldRev = "";

         // Fetch old revision from the document key
         if (change_state_new_revision.equals(itemset0.getValue("DOC_BC_CHANGE_STATUS")))
         {
            trans.clear();
            cmd = trans.addCustomFunction("GETKEYVAL","Client_SYS.Get_Key_Reference_Value","DUMMY1");
            cmd.addParameter("IN_1",itemset0.getValue("DOCUMENT_KEY"));
            cmd.addParameter("IN_2","DOC_REV");

            trans = mgr.perform(trans);
            sOldRev = trans.getValue("GETKEYVAL/DATA/DUMMY1");

            doc_buf.getBufferAt(0).setValue("DOC_REVISION",sOldRev);
         }
      }
      // Bug ID 43624, inoslk, end

      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", act_buf, doc_buf);
      launch_edm_macro = true;
   }


   public void goToBriefcase()
   {
      ASPManager mgr = getASPManager();
      headset.refreshRow();
      if (headlay.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         headset.selectRow();
      }

      keys=headset.getSelectedRows("BRIEFCASE_NO");
      mgr.transferDataTo("DocBriefcase.page",keys);
   }

   // Bug ID 47333, inoslk, removed getOldRevision() which was added as part of fix for 45036

   //-----------------------------------------------------------------------------
   //------------------------  BUTTON FUNCTIONS  ---------------------------------
   //-----------------------------------------------------------------------------

   public void  showModeChanged()
   {
      ASPManager mgr = getASPManager();

      sShowMode = mgr.readValue("CHECKMODE");

      if ("1".equals(mgr.readValue("CHECKMODE")))
      {
         showNewSheets();
         checkVal = "1";
      }

      if ("2".equals(mgr.readValue("CHECKMODE")))
      {
         showChangedDocument();
         checkVal = "2";
      }

      if ("3".equals(mgr.readValue("CHECKMODE")))
      {
         showUnChangedDocument();
         checkVal = "3";
      }

      if ("4".equals(mgr.readValue("CHECKMODE")))
      {
         showAll();
         checkVal = "4";
      }

      if ("5".equals(mgr.readValue("CHECKMODE")))
      {
         showNewRevision();
         checkVal = "5";
      }

      // Bug ID 47010, inoslk, start
      if ("6".equals(mgr.readValue("CHECKMODE")))
      {
	 showPending();
	 checkVal = "6";
      }
      // Bug ID 47010, inoslk, end

      if ("NULL".equals(mgr.readValue("CHECKMODE")))
      {
         showAll();
         checkVal = "4";
      }

   }


   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      String bc_issue_state_pending   = dm_const.doc_briefcase_issue_pending;
      String decode_string    = "DECODE( DOC_BRIEFCASE_ISSUE_API.Get_Brief_Doc_Issue_State(BRIEFCASE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REVISION),";
      decode_string += "'"+bc_issue_state_pending+"',DOC_BC_CHANGE_STATUS,'',DOC_BC_CHANGE_STATUS,'')";
      //Bug ID 47171, inoslk, start
      String decode_bc_issue_state = "DECODE(REJECT_ID,NULL,DOC_BRIEFCASE_ISSUE_API.Get_Brief_Doc_Issue_State(BRIEFCASE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REVISION),DOC_BRIEFCASE_ISSUE_API.Finite_State_Decode__('Rejected'))";
      //Bug ID 47171, inoslk, end

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("BRIEFCASE_NO").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREBRIEFNO: Briefcase No");

      headblk.addField("DESCRIPTION").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC: Description");

      headblk.addField("CREATED_BY").
      setLabel("DOCMAWDOCBRIEFCASECOMPARECREATEDBY: Created By");

      headblk.addField("RECEIVING_COMPANY").
      setLabel("DOCMAWDOCBRIEFCASECOMPARERECCOMPANY: Receiving Company");

      headblk.addField("RECEIVING_PERSON").
      setLabel("DOCMAWDOCBRIEFCASECOMPARERECPERS: Receiving Person");

      headblk.addField("LATEST_RETURN_DATE", "Date").
      setLabel("DOCMAWDOCBRIEFCASECOMPARERETURNLATESTDATE: Latest Return Date");

      headblk.addField("CREATED_DATE", "Date").
      setLabel("DOCMAWDOCBRIEFCASECOMPARECREATEDDATE: Created Date");

      headblk.addField("STATE").
      setLabel("DOCMAWDOCBRIEFCASECOMPARESTATUS: Status");

      headblk.addField("EXPORT_DATE", "Date").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREEXPORTDATE: Export Date");

      headblk.addField("IMPORT_DATE", "Date").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREIMPORTDATE: Import Date");

      //Bug ID 47907, inoslk, start
      headblk.addField("IMPORTED_DATA_LOCKED_DB")
      .setHidden();
      //Bug ID 47907, inoslk, end

      headblk.setView("DOC_BRIEFCASE");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.FORWARD);

      headbar.addCustomCommand("goToBriefcase",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREGOTOBRIEFCASE: Go To Briefcase"));

      headtbl = mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setFieldOrder("BRIEFCASE_NO");
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.setDialogColumns(2);

      // -- For db transactions

      headblk.addField("IN_1").
      setFunction("''").
      setHidden();

      headblk.addField("IN_2").
      setFunction("''").
      setHidden();

      headblk.addField("DUMMY1").
      setFunction("''").
      setHidden();

      headblk.addField("DUMMY2").
      setFunction("''").
      setHidden();

      //Bug ID 45944,inoslk,start
      headblk.addField("DOC_REVISION")
      .setHidden()
      .setFunction("''");
      //Bug ID 45944,inoslk,end


      //
      // Lines - From Doc Briefcase Unpacked - temporary table
      //

      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.addField("ITEMBLK0_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk0.addField("ITEMBLK0_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk0.addField("ITEMBLK0_BRIEFCASE_NO").
      setDbName("BRIEFCASE_NO").
      setHidden();

      itemblk0.addField("LINE_NO").
      setHidden();

      itemblk0.addField("DOCUMENT_KEY").
      setSize(25).
      setMaxLength(200).
      setReadOnly ().
      setDefaultNotVisible().
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCKEY: Document Key");

      // Bug ID 47320, inoslk, Added LOV and set to Uppercase
      itemblk0.addField("DOC_CLASS").
      setSize(20).
      setReadOnly ().
      setDynamicLOV("DOC_CLASS").
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCCLASS: Doc Class");

      // Bug ID 48138, inoslk, Set the format to Uppercase
      // Bug ID 50336, Start
      itemblk0.addField("DOC_NO").
      setSize(20).
      setReadOnly ().
      setUpperCase().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCNO: Doc No");
      // Bug ID 50336, End

      itemblk0.addField("DOC_SHEET").
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPARESHEETNO: Sheet No");

      itemblk0.addField("DOC_REV").
      setDbName("DOC_REVISION").
      setSize(20).
      setReadOnly ().
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCREV: Doc Rev");

      //Bug ID 43625, inoslk, start
      itemblk0.addField("DOC_TITLE")
      .setSize(20)
      .setMaxLength(250)
      .setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCTITLE: Title");
      //Bug ID 43625, inoslk, end

      itemblk0.addField("EDM_FILE_TYPE").
      setHidden().
      setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

      itemblk0.addField("NEXT_SHEET_NUMBER").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASECOMPARENEXTSHEET: Next Sheet No");

      // Bug ID 47011, inoslk, Removed setMandatory()
      itemblk0.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setDynamicLOV("DOC_NUM_BOOKING").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREBOOKLISTLOVTITLE: List of Book List")).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREBOOKINGLIST: Booking List");

      // Bug ID 47171, inoslk, Changed the function call
      itemblk0.addField("DOC_ISSUE_STATE").
      setSize(20).
      setReadOnly().
      setFunction(decode_bc_issue_state).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCISSUESTATE: Action");

      itemblk0.addField("DOC_BC_CHANGE_STATUS").
      setSize(20).
      setMandatory().
      setSelectBox().
      enumerateValues("DOC_BC_CHANGE_STATUS_API").
      setLabel("DOCMAWDOCBRIEFCASECOMPARECHANGESTATUS: Change Status");

      itemblk0.addField("DOC_BC_CHANGE_STATUS_DB").
      setHidden().
      setFunction("''");


      

      itemblk0.addField("FILEEXIST").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setFunction("DECODE(FILE_NAME,NULL,'0','1')").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREITEMFILEEXIST: File Exist");

      itemblk0.addField("DOC_STATE").
      setSize(20).
      setReadOnly().
      setFunction("DOC_ISSUE_API.Get_State(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :DOC_REV)").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDOCSTATE: Document Status");

      itemblk0.addField("REVISION_DATE", "Date").
      setDefaultNotVisible().
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREREVISIONDATE: Revision Date");

      itemblk0.addField("REVISION_TEXT").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREREVISIONTEXT: Revision Text");

      itemblk0.addField("REVISION_SIGN").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREREVISIONSIGN: Revision Sign");

      itemblk0.addField("LU_NAME").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLOV("LuNameLov.page").
      setLOVProperty("WHERE","service_list = '*' or '^' || service_list like '^%DocReferenceObject^%' or service_list is null").
      setLabel("DOCMAWDOCBRIEFCASECOMPARELUNAME: LU Name");

      itemblk0.addField("OBJECT_ID").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLOV("ObjectConnection.page").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREOBJECTID: Object ID");

      itemblk0.addField("UPDATE_REVISION").
      setDefaultNotVisible().
      setSize(20).
      setSelectBox().
      enumerateValues("ALWAYS_LAST_DOC_REV_API").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREREUPDATEREVISION: Update Revision");

      itemblk0.addField("ASSOCIATED_CATEGORY").
      setDefaultNotVisible().
      setSize(20).
      setUpperCase().
      setDynamicLOV("DOC_REFERENCE_CATEGORY").
      setLabel("DOCMAWDOCBRIEFCASECOMPAREASSOCIATEDCATEGORY: Associated Category");

      itemblk0.addField("DATE1", "Date").
      setDefaultNotVisible().
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDATE1: Date1");

      itemblk0.addField("SIGN1").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCBRIEFCASECOMPARESIGN1: Sign1");

      itemblk0.addField("DATE2", "Date").
      setDefaultNotVisible().
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDATE2: Date2");

      itemblk0.addField("SIGN2").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCBRIEFCASECOMPARESIGN2: Sign2"); //Bug Id 46692

      itemblk0.addField("DATE3", "Date").
      setDefaultNotVisible().
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDATE3: Date3");

      itemblk0.addField("SIGN3").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCBRIEFCASECOMPARESIGN3: Sign3");

      itemblk0.addField("DATE4", "Date").
      setDefaultNotVisible().
      setSize(20).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDATE4: Date4");

      itemblk0.addField("SIGN4").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(30).
      setLabel("DOCMAWDOCBRIEFCASECOMPARESIGN4: Sign4");

      itemblk0.addField("DESCRIPTION1").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC1: Description 1");

      itemblk0.addField("DESCRIPTION2").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC2: Description 2");

      itemblk0.addField("DESCRIPTION3").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC3: Description 3");

      itemblk0.addField("DESCRIPTION4").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC4: Description 4");

      itemblk0.addField("DESCRIPTION5").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC5: Description 5");

      itemblk0.addField("DESCRIPTION6").
      setDefaultNotVisible().
      setSize(20).
      setMaxLength(2000).
      setLabel("DOCMAWDOCBRIEFCASECOMPAREDESC6: Description 6");

      itemblk0.addField("FILE_NAME").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setLabel("DOCMAWDOCBRIEFCASECOMPAREFILENAME: File Name");

      itemblk0.addField("BCISSUE_CHANGE_STATE").
      setSize(20).
      setMaxLength(2000).
      setDefaultNotVisible().
      setFunction(decode_string).
      setHidden();

      itemblk0.addField("SHEET_EXIST").
      setSize(20).
      setMaxLength(10).
      setFunction("DOC_ISSUE_API.Check_Exist_Sheet_No(:DOC_CLASS,:DOC_NO,:DOC_SHEET)").
      setHidden();

      //Bug Id 46726 Karalk Start.
      itemblk0.addField("EDIT_POSSIBILITY").
      setSize(20).
      setMaxLength(2000).
      setFunction("DOC_BRIEFCASE_ISSUE_API.Check_Editable_(:BRIEFCASE_NO,:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV) ").
      setHidden();
      //Bug Id 46726 Karalk End.

      // Bug ID 47333, inoslk, start
      itemblk0.addField("OLD_DOC_REV").
      setFunction("Client_SYS.Get_Key_Reference_Value(:DOCUMENT_KEY,'DOC_REV')").
      setHidden();
      // Bug ID 47333, inoslk, end

      // Bug ID 46925, inoslk, Removed code added as part of fix for 45219

      //Bug ID 47637, inoslk, start
      itemblk0.addField("FORMAT_SIZE")
      .setSize(6)
      .setUpperCase()
      .setDefaultNotVisible()
      .setDynamicLOV("DOC_CLASS_FORMAT_LOV","DOC_CLASS")
      .setLabel("DOCMAWDOCBCCOMPAREFORMATSIZE: Format Size");

      itemblk0.addField("SCALE")
      .setSize(60)
      .setUpperCase()
      .setDefaultNotVisible()
      .setDynamicLOV("DOC_SCALE")
      .setLabel("DOCMAWDOCBCCOMPAREDOCSCALE: Scale");

      itemblk0.addField("REASON_FOR_ISSUE")
      .setSize(20)
      .setUpperCase()
      .setDefaultNotVisible()
      .setDynamicLOV("DOC_CLASS_REASON_FOR_ISSUE_LOV","DOC_CLASS")
      .setLabel("DOCMAWDOCBCCOMPAREREASONFORISSUE: Reason For Issue");
      //Bug ID 47637, inoslk, end

      //Bug ID 47171, inoslk, start
      itemblk0.addField("REJECT_ID","Number")
      .setHidden();
      //Bug ID 47171, inoslk, end

      // Bug ID 50336, Start
      itemblk0.addField("DESIRED_DOC_NO").setHidden();
      // Bug ID 50336, End

       //bug 58216 starts
      itemblk0.addField("BC_ISSUE_STATE").setFunction("''").setHidden();
      //bug 58216 end

      itemblk0.setView("DOC_BRIEFCASE_UNPACKED");
      itemblk0.defineCommand("DOC_BRIEFCASE_UNPACKED_API","Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      itemblk0.setTitle(mgr.translate("DOCMAWDOCBRIEFCASECOMPAREIMPORTINGDATA: Documents During Import"));

      itemset0 = itemblk0.getASPRowSet();
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturn");
      //Bug ID 43698, inoslk, start
      itembar0.addCustomCommand("editUnpacked","DOCMAWDOCBCCOMPAREEDIT: Edit");
      itembar0.addCustomCommandSeparator();
      //Bug ID 43698, inoslk, end
      itembar0.addCustomCommand("acceptDocuments","DOCMAWDOCBRIEFCASECOMPAREACCEPT: Accept");
      itembar0.addCustomCommand("rejectDocuments","DOCMAWDOCBRIEFCASECOMPAREREJECT: Reject");
      itembar0.addCustomCommand("remove_new_document","DOCMAWDOCBRIEFCASECOMPAREREMOVENEWDOC: Remove New Document");
      itembar0.addCustomCommand("compareDocument",mgr.translate("DOCMAWDOCBRIEFCASECOMPARECOMPARE: Compare"));
      itembar0.addCustomCommand("unlockDocuments",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREUNLOCKDOC: Unlock Document"));
      itembar0.addCustomCommandSeparator();
      itembar0.addCustomCommand("viewExistingDocument",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREVIEWEXISTINGDOCUMENT: View Existing"));
      itembar0.addCustomCommand("viewUnpackedDocument",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREVIEWUNPACKEDDOCUMENT: View Unpacked"));
      itembar0.addCustomCommandSeparator();
      itembar0.addCustomCommand("viewExistingWithExtViewer",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREVIEWEXISTWITHEXTVIEWER: View Existing with External Viewer"));
      itembar0.addCustomCommand("viewExportWithExtViewer",mgr.translate("DOCMAWDOCBRIEFCASECOMPAREVIEWUNPACKEDWITHEXTVIEWER: View Unpacked with External Viewer"));
      itembar0.addCustomCommand("setBookingList",mgr.translate("DOCMAWDOCBRIEFCASECOMPARESETBOOKINGLIST: Set Booking List"));
      itembar0.disableCommand(itembar0.DELETE);

      itembar0.enableMultirowAction();
      itembar0.removeFromMultirowAction("compareDocument");
      itembar0.removeFromMultirowAction("viewExistingDocument");
      itembar0.removeFromMultirowAction("viewUnpackedDocument");
      itembar0.removeFromMultirowAction("viewExistingWithExtViewer");
      itembar0.removeFromMultirowAction("viewExportWithExtViewer");
      itembar0.removeFromMultirowAction("setBookingList");

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCBRIEFCASECOMPAREIMPORTINGDATA: Documents During Import"));
      itemtbl0.enableRowSelect();

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows() == 0)
      {
         headbar.disableCustomCommand("goToBriefcase");
      }

      itembar0.disableCommand (itembar0.FIND);
      itembar0.disableCommand (itembar0.NEWROW);
      // Bug ID 43698, inoslk, start
      if (itemlay0.isSingleLayout())
      {
         if (doc_state_released.equals(itemset0.getValue("DOC_STATE")))
            itembar0.disableCommand(itembar0.EDITROW);
         else
            itembar0.enableCommand(itembar0.EDITROW);
      }
      else
         itembar0.disableCommand(itembar0.EDITROW);

      if (itemlay0.isMultirowLayout())
         itembar0.enableCustomCommand("editUnpacked");
      else
      {
         //Bug ID 46865, inoslk, start
         if (itemset0.getValue("EDIT_POSSIBILITY").equals("TRUE"))
            itembar0.enableCommand(itembar0.EDITROW);
         else
            itembar0.disableCommand(itembar0.EDITROW);
         //Bug ID 46865, inoslk, end

         itembar0.disableCustomCommand("editUnpacked");
      }
      // Bug ID 43698, inoslk, end

      if (itemlay0.getLayoutMode() == itemlay0.MULTIROW_LAYOUT)
         itembar0.enableMultirowAction();
      else
         itembar0.disableMultirowAction();


      if (itemset0.countRows()==0 && (itemlay0.getLayoutMode()== itemlay0.SINGLE_LAYOUT))
      {
         itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
         checkMode = true;
      }
      else if ((itemlay0.getLayoutMode()== itemlay0.MULTIROW_LAYOUT) && checkMode)
      {
         itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
         checkMode = false;
      }
      else if (itemlay0.getLayoutMode()==itemlay0.EDIT_LAYOUT)
      {
         if (change_state_changed.equals(itemset0.getValue("DOC_BC_CHANGE_STATUS")))
         {
            mgr.getASPField("FILE_NAME").setReadOnly();
            mgr.getASPField("DOC_SHEET").setReadOnly();
         }

         if (change_state_unchanged.equals(itemset0.getValue("DOC_BC_CHANGE_STATUS")))
         {
            mgr.getASPField("FILE_NAME").setReadOnly();
            mgr.getASPField("DOC_SHEET").setReadOnly();

         }

	 // Bug ID 47320, inoslk, start
	 // Bug ID 50336, Start
	 if ((change_state_new_doc.equals(itemset0.getValue("DOC_BC_CHANGE_STATUS"))) || (change_state_new_sheet.equals(itemset0.getValue("DOC_BC_CHANGE_STATUS"))))
         {
            mgr.getASPField("DOC_CLASS").unsetReadOnly();
            mgr.getASPField("DOC_NO").unsetReadOnly(); //Bug ID 48138, inoslk
         }
	 else
         {
            mgr.getASPField("DOC_CLASS").setReadOnly();
            mgr.getASPField("DOC_NO").setReadOnly(); //Bug ID 48138, inoslk
         }
     // Bug ID 50336, End
	 // Bug ID 47320, inoslk, end
      }

      if (itemlay0.isSingleLayout() || itemlay0.isEditLayout())
      {
         itemblk0.setTitle(mgr.translate("DOCBRIEFCASECOMPAREUNPACKEDDATA: Unpacked Document Data"));
      }

      if (this.itemset0.countRows()<1) //w.a.
      {
         itembar0.disableMultirowAction();
      }
      else
      {
         itembar0.enableMultirowAction();
      }

      //itembar0.addCommandValidConditions("unlockDocuments","DOC_ISSUE_STATE","Disable",sBcIssueRejected + ";" + sBcIssueAccepted);
      itembar0.addCommandValidConditions("unlockDocuments","BCISSUE_CHANGE_STATE","Disable",change_state_new_sheet + ";" + change_state_new_doc + ";" + change_state_new_revision + ";''");
      // Bug ID 47171, inoslk, enable reject for new docs
      itembar0.addCommandValidConditions("rejectDocuments","BCISSUE_CHANGE_STATE","Disable",change_state_unchanged);
      // Bug ID 45036, inoslk, enabled remove new document for new revisions
      itembar0.addCommandValidConditions("remove_new_document","BCISSUE_CHANGE_STATE","Disable",change_state_changed + ";" + change_state_unchanged + ";''");

      itembar0.addCommandValidConditions("acceptDocuments","BCISSUE_CHANGE_STATE","Disable",change_state_unchanged+ ";''");
      itembar0.addCommandValidConditions("setBookingList","BCISSUE_CHANGE_STATE","Enable", change_state_new_doc);
      itembar0.addCommandValidConditions("viewExportWithExtViewer","FILEEXIST","Disable","0");
      itembar0.addCommandValidConditions("compareDocument","BCISSUE_CHANGE_STATE","Enable",change_state_changed + ";" + change_state_new_revision);
      // Bug ID 43624, inoslk, start
      itembar0.addCommandValidConditions("viewExistingDocument","BCISSUE_CHANGE_STATE","Enable",change_state_changed + ";" + change_state_new_revision + ";" + change_state_unchanged);
      itembar0.addCommandValidConditions("viewExistingWithExtViewer","BCISSUE_CHANGE_STATE","Enable",change_state_changed + ";" + change_state_new_revision + ";" + change_state_unchanged);
      // Bug ID 43624, inoslk, end

      //Bug Id 46726 Karalk Start.
      itembar0.addCommandValidConditions("editUnpacked","EDIT_POSSIBILITY","Disable", "FALSE");
      //Bug Id 46726 Karalk End
   }


   public void remove_new_document()
   {
      ASPManager mgr = getASPManager();
      int currow_head = 0;
      // Bug ID 4733, inoslk, removed unused variable
      trans.clear();

      // Bug ID 47333, inoslk, start
      if (itemlay0.isMultirowLayout())
	 itemset0.storeSelections();
      else
      {
	 itemset0.unselectRows();
	 itemset0.selectRow();
      }

      itemset0.setFilterOn();
      itemset0.first();

      do
      {
	 if ((change_state_changed.equals(itemset0.getRow().getValue("BCISSUE_CHANGE_STATE"))) || (change_state_unchanged.equals(itemset0.getRow().getValue("BCISSUE_CHANGE_STATE"))))
	 {
	    mgr.showAlert(mgr.translate("DOCMAWDOCBCCOMPAREREMOVENEWONLY: You may remove only New Documents, New Sheets or New Revisions."));
	    itemset0.setFilterOff();
	    return;
	 }

	 if (!(itemset0.getRow().getValue("DOC_ISSUE_STATE").equals(sBcIssuePending)))
	 {
	    mgr.showAlert(mgr.translate("DOCMAWDOCBCCOMPAREREMOVEPENDINGONLY: You may remove documents only while they are in state Pending."));
	    itemset0.setFilterOff();
	    return;
	 }
      } while ( itemset0.next() );

      int cnt = 0;
      itemset0.first();
      trans.clear();

      do
      {
	 // Bug ID 45036, inoslk, start
	 if (change_state_new_revision.equals(itemset0.getValue("BCISSUE_CHANGE_STATE")))
	 {
	    cmd = trans.addCustomCommand("SETFILESTATEALL" + cnt,"Edm_File_API.Set_File_State_All");
	    cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
	    cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
	    cmd.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
	    cmd.addParameter("DOC_REV",itemset0.getRow().getValue("OLD_DOC_REV"));
	    cmd.addParameter("IN_1","UnlockExported");
	    cmd.addParameter("IN_2","");

	    cmd = trans.addCustomCommand("REMOLDREV" + cnt,"Doc_Briefcase_Issue_API.Remove_Doc");
	    cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
	    cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
	    cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
	    cmd.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
	    cmd.addParameter("DOC_REV",itemset0.getRow().getValue("OLD_DOC_REV"));

	    cnt++;
	 }
	 // Bug ID 45036, inoslk, end
      } while ( itemset0.next() );

      mgr.perform(trans);
      trans.clear();
      // Bug ID 47333, inoslk, end

      currow_head= headset.getCurrentRowNo();
      // Bug ID 47333, inoslk, start
      itemset0.setSelectedRowsRemoved();
      // Bug ID 47333, inoslk, end
      mgr.submit(trans);

      // Bug ID 47333, inoslk, start
      itemset0.setFilterOff();
      itemset0.unselectRows();
      // Bug ID 47333, inoslk, end
      headset.goTo(currow_head);
      this.refreshRow();

      // Bug ID 47333, inoslk, Removed code added as part of fix for 45036
   }


   public void  viewExistingWithExtViewer()
   {
      transferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER",true);
   }


   public void  saveReturn()
   {

      ASPManager mgr = getASPManager();
      int currow_head = headset.getCurrentRowNo();
      int currow_item = itemset0.getCurrentRowNo();

      String sSheet = "FALSE";
      String sDocClass;
      String sDocNo;
      String sDocSheet;
      int numRows=0;
      int x = 0;
      int count=0;
      int n = itemset0.countRows();
      String sSheetArr[][] = new String[n][3];

      itemset0.changeRow();
      //Count the number of rows with status new Sheet and action not accepted
      itemset0.first();
      do
      {
         if (change_state_new_sheet.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS")) && (sBcIssuePending.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE"))))
         {
            sSheetArr[x][0] = itemset0.getRow().getValue("DOC_CLASS");
            sSheetArr[x][1] = itemset0.getRow().getValue("DOC_NO");
            sSheetArr[x][2] = itemset0.getRow().getValue("DOC_SHEET");
            x = x + 1;
         }
      }
      while (itemset0.next());
      itemset0.goTo(currow_item);
      numRows = x;
      if (change_state_new_sheet.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS")))
      {
         sDocClass = itemset0.getRow().getValue("DOC_CLASS");
         sDocNo    = itemset0.getRow().getValue("DOC_NO");
         sDocSheet = mgr.readValue("DOC_SHEET");

         if (mgr.isEmpty(mgr.readValue("DOC_SHEET")))
            mgr.showError("DOCMAWDOCBRIEFCASECOMPAREREQSHEET: Sheet No requires a value for new Sheets");
         else
         {
            trans.clear();
            cmd = trans.addCustomFunction("EXTSHEET","Doc_Issue_API.Check_Exist_Sheet_No","DOC_SHEET");
            cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO",mgr.readValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET",mgr.readValue("DOC_SHEET"));
            trans = mgr.perform(trans);
            sSheet = trans.getValue("EXTSHEET/DATA/DOC_SHEET");
            trans.clear();
         }

         if ("TRUE".equals(sSheet))
            mgr.showError("DOCMAWDOCBRIEFCASECOMPAREDUP: The Sheet No is already entered for the same title"); //Bug Id 48032

         if (!(mgr.isEmpty(mgr.readValue("DOC_NO")))) //Bug Id 48032, Added If condition
         {
            for (int y=0; y<numRows; y++)
            {
               if ((sDocClass.equals(sSheetArr[y][0])) && (sDocNo.equals(sSheetArr[y][1])) && (sDocSheet.equals(sSheetArr[y][2])))
               {
                  count = count + 1;
               }
            }
         }

         if (count>1)
            mgr.showError("DOCMAWDOCBRIEFCASECOMPAREDUP: The Sheet No is already entered for the same title");
      }

      mgr.submit(trans);
      trans.clear();
      headset.goTo(currow_head);
      okFindITEM0();  //Bug Id 43620
   }

   public void acceptDocuments()
   {
      ASPManager mgr = getASPManager();
      String sDocSheet;
      String sDocClass;
      String sDocNo;

      // Bug ID 46925, inoslk, Removed variables added as part of fix for 45219

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      if (itemlay0.isMultirowLayout() && itemset0.countSelectedRows() == 0)
      {
         mgr.showAlert("DOCMAWDOCBCCOMPAREACCEPTNOROWSSELECTED: No documents selected to Accept.");
         return;
      }

      itemset0.setFilterOn();
      itemset0.first();
      int numRows = itemset0.countSelectedRows();

      String sSheetArr[][] = new String[numRows][3];

      for (int x=0;x<numRows;x++)
      {
         int count = 0;
	 // Bug ID 46925, inoslk, Removed modification added as part of fix for 45219

         if (change_state_unchanged.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS")) && (sBcIssuePending.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE")))
             || (sBcIssueAccepted.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE"))))
         {
            mgr.showError("DOCMAWDOCBRIEFCASECOMPAREUNCHANGEEXIST: Accepted documents or Unchanged documents cannot be accepted");
         }
	 // Bug ID 47171, inoslk, start
	 if (sBcIssueRejected.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE")))
	 {
	    mgr.showError(mgr.translate("DOCMAWDOCBCCOMPARENOREJECTACCEPTED: You cannot accept Rejected documents."));
	 }
	 // Bug ID 47171, inoslk, end
         sDocClass = itemset0.getRow().getValue("DOC_CLASS");
         sDocNo    = itemset0.getRow().getValue("DOC_NO");


         if (change_state_new_sheet.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS")) && (sBcIssuePending.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE"))))
         {
            sSheetArr[x][0] = itemset0.getRow().getValue("DOC_CLASS");
            sSheetArr[x][1] = itemset0.getRow().getValue("DOC_NO");
            sSheetArr[x][2] = itemset0.getRow().getValue("DOC_SHEET");
            // Bug ID 50336, Start
            if (mgr.isEmpty(itemset0.getRow().getValue("DOC_NO")))
            {
               mgr.showAlert("DOCMAWDOCBRIEFCASECOMPARENODOCNO: Doc No should be entered before you can accept or reject new sheets.");
               itemset0.setFilterOff();
               return;
            }
            //Bug ID 50336, End
            if (mgr.isEmpty(itemset0.getRow().getValue("DOC_SHEET")))
            {
               mgr.showAlert("DOCMAWDOCBRIEFCASECOMPARENOSHEET: Sheet No should be entered for New Sheets");
               itemset0.setFilterOff();
               return;
            }
            // Bug ID 50336, End

	    // Bug ID 46925, inoslk, Removed code added as part of fix for 45219
	
	    // Bug ID 46925, inoslk, reversed the IF condition and retained the code for ELSE
            if ("FALSE".equals(itemset0.getRow().getValue("SHEET_EXIST")))
            {
               String sdClass     = sSheetArr[x][0];
               String sdNo        = sSheetArr[x][1];
               String sdSheet     = sSheetArr[x][2];
               for (int y = 0; y < numRows; y++)
               {
		  // Bug ID 46925, inoslk, start
		  // Added check for empty document numbers
		  if (!mgr.isEmpty(sdNo))
		  {
		     if ((sdClass.equals(sSheetArr[y][0])) && (sdNo.equals(sSheetArr[y][1])) && (sdSheet.equals(sSheetArr[y][2])))
		     {
			count=count+1;
		     }
		  }
		  // Bug ID 46925, inoslk, end
               }
               if (count>1)
               {
                  mgr.showAlert("Duplicate Sheet Numbers Exist for the same Title");
                  itemset0.setFilterOff();
                  return;
               }
            }
         }
         itemset0.next();
      }
      transferToEDMBriefcase("ACCEPT");
   }


   public void rejectDocuments()
   {
      ASPManager mgr = getASPManager();

      // Bug ID 50336, Start
      ASPBuffer rows = itemset0.getRows();
      ASPBuffer sub;
      int row_count;
      boolean bSheetConnectedToNewDoc;
      // Bug ID 50336, End

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      itemset0.setFilterOn();

      if (headset.getValue("STATE").equals(sBcUnderImport))
      {
         itemset0.first();
         do
         {
            // Bug ID 50336, Start
            bSheetConnectedToNewDoc = false;
            // Bug ID 50336, End 

	    // Bug ID 47171, inoslk, Removed check for new document issues, now it's possible to reject a new doc/sheet/rev
            if (itemset0.getRow().getValue("DOC_ISSUE_STATE").equals(sBcIssueAccepted))
            {
               mgr.showAlert("DOCMAWDOCBCCOMPAREINVALIDSTATEACCEPT: Accepted documents can not be rejected.");
               itemset0.setFilterOff();
               return;
            }
            else if (itemset0.getRow().getValue("DOC_ISSUE_STATE").equals(sBcIssueRejected))
            {
               mgr.showAlert("DOCMAWDOCBCCOMPAREINVALIDSTATEREJECT: Rejected documents cannot be rejected");
               itemset0.setFilterOff();
               return;
            }

            else if (itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(change_state_unchanged))
            {
               mgr.showAlert("DOCMAWDOCBCCOMPAREINVALIDSTATUSUNCHANGED: Cannot reject unchanged documents");
               itemset0.setFilterOff();
               return;
            }
            // Bug ID 50336, Start
            else if ((change_state_new_sheet.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS"))) && (mgr.isEmpty(itemset0.getRow().getValue("DOC_NO")))) 
            {
               mgr.showAlert("DOCMAWDOCBRIEFCASECOMPARENODOCNO: Doc No should be entered before you can accept or reject new sheets.");
               itemset0.setFilterOff();
               return;
            }
            // Bug ID 50336, End


         } while (itemset0.next());
      }
      else
      {
         mgr.showAlert("DOCMAWDOCBCCOMPARENOVALIDBCSTATE: Briefcase must be in state 'Under Import' for this action to continue..");
         return;
      }

      trans.clear();
      int count = 0;
      itemset0.first();

      do
      {
	 // Bug ID 47171, inoslk, start
	 if ((itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(change_state_new_doc)) || (itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(change_state_new_sheet)) || (itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(change_state_new_revision)))
	 {
	    // Set a reject ID and for this doc
	    cmd = trans.addCustomCommand("GENERATEREJID" + count ++, "Doc_Briefcase_Unpacked_API.Set_Reject_Id_");
	    cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
	    cmd.addParameter("LINE_NO",itemset0.getRow().getValue("LINE_NO"));
	 }
	 else
	 {
	    cmd = trans.addCustomCommand("SETDOCBCISSUESTATE" + count++,"Doc_Briefcase_Issue_API.Set_Bc_Issue_State");
	    cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
	    cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
	    cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
	    cmd.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
	    cmd.addParameter("DOC_REV",itemset0.getRow().getValue("DOC_REVISION"));
	    cmd.addParameter("DOC_ISSUE_STATE","Reject");
	 }
	 // Bug ID 47171, inoslk, end
      } while (itemset0.next());

      // Bug ID 47907, inoslk, start
      String data_locked = mgr.isEmpty(headset.getValue("IMPORTED_DATA_LOCKED_DB"))?"N":headset.getValue("IMPORTED_DATA_LOCKED_DB");
      if (data_locked.equals("N"))
      {
	 cmd = trans.addCustomCommand("SETBCLOCKED","DOC_BRIEFCASE_API.Set_Imported_Bc_Locked_");
	 cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      }
      // Bug ID 47907, inoslk, end


      trans = mgr.perform(trans); // Bug ID 47171, inoslk

      itemset0.first();
      do
      {
         itemset0.refreshRow();
      } while (itemset0.next());

      itemset0.setFilterOff();
      itemset0.unselectRows();
      transferToBriefcase();
   }


   public void compareDocument()
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }
      //itemset0.storeSelections();
      itemset0.setFilterOn();

      //ASPBuffer buff = itemset0.getSelectedRows("BRIEFCASE_NO,LINE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOCUMENT_KEY");

      url = "BriefcaseCompareWindow.page?FROM_COMPARE=YES";
      url += "&BRIEFCASE_NO="+mgr.URLEncode(itemset0.getValue("BRIEFCASE_NO"));
      url += "&LINE_NO="+mgr.URLEncode(itemset0.getValue("LINE_NO"));
      url += "&DOC_CLASS="+mgr.URLEncode(itemset0.getValue("DOC_CLASS"));
      url += "&DOC_NO="+mgr.URLEncode(itemset0.getValue("DOC_NO"));
      url += "&DOC_SHEET="+mgr.URLEncode(itemset0.getValue("DOC_SHEET"));
      url += "&DOC_REV="+mgr.URLEncode(itemset0.getValue("DOC_REVISION"));
      url += "&DOCUMENT_KEY="+mgr.URLEncode(itemset0.getValue("DOCUMENT_KEY"));
      luanch_compare_window = true;
      this.itemset0.unselectRow();
      itemset0.setFilterOff();

   }


   public void transferToBriefcase()
   {
      ASPManager mgr = getASPManager();
      //Bug ID 43915, inoslk, start
      String sMoreDocsPending = "";

      trans.clear();
      cmd = trans.addCustomFunction("MOREDOCS","Doc_Briefcase_Unpacked_API.More_Docs_Pending","DUMMY1");
      cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));

      trans = mgr.perform(trans);
      sMoreDocsPending = trans.getValue("MOREDOCS/DATA/DUMMY1");
      //Bug ID 43915, inoslk, end

      // Didn't find any, so set BC to imported and go to BC Page
      //Bug ID 43915, inoslk, Changed the following IF condition
      // Bug Id 54962, Start 
      if ("FALSE".equals(sMoreDocsPending))
      {
         trans.clear();
         cmd = trans.addCustomCommand("SETBCIMPORTED","Doc_Briefcase_API.Set_Briefcase_State");
         cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
         cmd.addParameter("STATE","FinishImport");
         mgr.perform(trans);

         ASPBuffer temp = headset.getRows("BRIEFCASE_NO");
         mgr.transferDataTo("DocBriefcase.page",temp);
      }
      else 
      {
          headset.refreshRow();
      }
      // Bug Id 54962, End
   }


   public void unlockDocuments()
   {
      ASPManager mgr = getASPManager();
      int nVal = 0;
      int numRows;
      String sBcIssueObjId;
      String sBcIssueObjVersion;
      String sUnpackedObjId;
      String sUnpackedObjVersion;
      String sBriefcaseNo;
      String sDocClass;
      String sDocNo;
      String sDocSheet;
      String sDocRev;
      int nAccept = 0;
      int nReject = 0;
      int nFileNotExist = 0;



      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      if (itemlay0.isMultirowLayout() && itemset0.countSelectedRows() == 0)
         mgr.showAlert("DOCMAWDOCBCCOMPARENODOCSELECTED: No documents selected to Unlock.");


      itemset0.setFilterOn();
      itemset0.first();



      numRows = itemset0.countSelectedRows();
      itemset0.first();

      for (int x=0;x<numRows;x++)
      {
         if (sBcIssueAccepted.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE")))
         {
            nAccept = 1;
         }

         else if (sBcIssueRejected.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE")))
         {
            nReject = 1;
         }

         else if ((change_state_new_doc.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS"))) || (change_state_new_sheet.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS"))) || (change_state_new_revision.equals(itemset0.getRow().getValue("DOC_BC_CHANGE_STATUS"))))
         {
            nFileNotExist = 1;
         }

         itemset0.next();
      }

      if ((nReject == 1) && (nAccept == 1))
      {
         mgr.showAlert("DOCMAWDOCBCCOMPNOUNLOCKACCEPTREJECT: You cannot unlock Accepted or Rejected documents.");
         itemset0.setFilterOff();
         return;
      }
      else if (nAccept == 1)
      {
         mgr.showAlert("DOCMAWDOCBCCOMPNOUNLOCKACCEPTED: You cannot unlock Accepted documents");
         itemset0.setFilterOff();
         return;
      }
      else if (nReject == 1)
      {
         mgr.showAlert("DOCMAWDOCBCCOMPNOUNLOCKREJECTED: You cannot unlock Rejected documents");
         itemset0.setFilterOff();
         return;
      }

      else if (nFileNotExist == 1)
      {
         mgr.showAlert("DOCMAWDOCBCCOMPNOUNLOCKNOEXISTS: Documents that does not exist in the system cannot be unlocked");
         itemset0.setFilterOff();
         return;
      }

      trans.clear();
      int count = 0;
      itemset0.setFilterOn();
      itemset0.first();


      do
      {
         sBriefcaseNo = headset.getRow().getValue("BRIEFCASE_NO");
         sDocClass = itemset0.getRow().getValue("DOC_CLASS");
         sDocNo = itemset0.getRow().getValue("DOC_NO");
         sDocSheet = itemset0.getRow().getValue("DOC_SHEET");
         sDocRev = itemset0.getRow().getValue("DOC_REVISION");

	 //Bug ID 45944,inoslk,start
         q = trans.addQuery("BRIEFISSUE","DOC_BRIEFCASE_ISSUE","OBJID,OBJVERSION","BRIEFCASE_NO=? AND DOC_CLASS=? AND DOC_NO=? AND DOC_SHEET=? AND DOC_REV=?","");
	 q.addParameter("BRIEFCASE_NO",sBriefcaseNo);
	 q.addParameter("DOC_CLASS",sDocClass);
	 q.addParameter("DOC_NO",sDocNo);
	 q.addParameter("DOC_SHEET",sDocSheet);
	 q.addParameter("DOC_REV",sDocRev);

         q = trans.addQuery("BCUNPACKED","DOC_BRIEFCASE_UNPACKED","OBJID,OBJVERSION","BRIEFCASE_NO=? AND DOC_CLASS=? AND DOC_NO=? AND DOC_SHEET=? AND DOC_REVISION=?","");
	 q.addParameter("BRIEFCASE_NO",sBriefcaseNo);
	 q.addParameter("DOC_CLASS",sDocClass);
	 q.addParameter("DOC_NO",sDocNo);
	 q.addParameter("DOC_SHEET",sDocSheet);
	 q.addParameter("DOC_REVISION",sDocRev);
	 //Bug ID 45944,inoslk,end

         trans = mgr.perform(trans);

         sBcIssueObjId       = trans.getValue("BRIEFISSUE/DATA/OBJID");
         sBcIssueObjVersion  = trans.getValue("BRIEFISSUE/DATA/OBJVERSION");
         sUnpackedObjId      = trans.getValue("BCUNPACKED/DATA/OBJID");
         sUnpackedObjVersion = trans.getValue("BCUNPACKED/DATA/OBJVERSION");

         trans.clear();
         if (sBcUnderImport.equals(headset.getValue("STATE")))
         {
            if (itemset0.getRow().getValue("FILEEXIST").equals("1"))
            {
               //change the state of the doc issue in edm
               cmd = trans.addCustomCommand("SETFILESTATEALL","Edm_File_API.Set_File_State_All");
               cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV",itemset0.getRow().getValue("DOC_REVISION"));
               cmd.addParameter("IN_1","UnlockExported");
               cmd.addParameter("IN_2","");
            }

            //remove the record from BC Issue
            cmd = trans.addCustomCommand("DOCBCISSUEREMOVE","DOC_BRIEFCASE_ISSUE_API.Remove__");
            cmd.addParameter("DUMMY1");
            cmd.addParameter("OBJID",sBcIssueObjId);
            cmd.addParameter("OBJVERSION",sBcIssueObjVersion);
            cmd.addParameter("DUMMY2","DO");

            //remove the record from the temporary table
            cmd = trans.addCustomCommand("DOCBCUNPACKEDREMOVE","DOC_BRIEFCASE_UNPACKED_API.Remove__");
            cmd.addParameter("DUMMY1");
            cmd.addParameter("OBJID",sUnpackedObjId);
            cmd.addParameter("OBJVERSION",sUnpackedObjVersion);
            cmd.addParameter("DUMMY2","DO");
         }

      } while (itemset0.next());

      // Bug ID 47907, inoslk, start
      String data_locked = mgr.isEmpty(headset.getValue("IMPORTED_DATA_LOCKED_DB"))? "N" : headset.getValue("IMPORTED_DATA_LOCKED_DB");
      if (data_locked.equals("N"))
      {
	 cmd = trans.addCustomCommand("SETBCLOCKED","DOC_BRIEFCASE_API.Set_Imported_Bc_Locked_");
	 cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      }
      // Bug ID 47907, inoslk, end

      int row_no = headset.getCurrentRowNo();

      mgr.submit(trans);

      itemset0.setFilterOff();
      itemset0.unselectRows();
      headset.goTo(row_no);
      // Bug ID 47010, inoslk, start
      showModeChanged();
      // Bug ID 47010, inoslk, end
      numRows = itemset0.countRows();
      itemset0.first();

      for (int x=0;x<numRows;x++)
      {
         if ((sBcIssuePending.equals(itemset0.getRow().getValue("DOC_ISSUE_STATE"))) || (mgr.isEmpty(itemset0.getRow().getValue("DOC_ISSUE_STATE"))))
         {
            nVal = 1;
         }
         itemset0.next();
      }


      if (nVal == 0)
      {
         //If there are no more pending documents : set the briefcase state to imported
         //And load the Briefcase page.
         trans.clear();
         cmd = trans.addCustomCommand("SETBRIEFCASESTATE","Doc_Briefcase_API.Set_Briefcase_State");
         cmd.addParameter("BRIEFCASE_NO",headset.getRow().getValue("BRIEFCASE_NO"));
         cmd.addParameter("IN_1","FinishImport");
         mgr.submit(trans);

         if (headlay.isMultirowLayout())
         {
            itemset0.storeSelections();
            headset.setFilterOn();
         }
         else
         {
            itemset0.unselectRows();
            headset.selectRow();
         }

         keys = headset.getSelectedRows("BRIEFCASE_NO");
         mgr.transferDataTo("DocBriefcase.page",keys);
      }
   }


   public void viewExportWithExtViewer()
   {
      transferToEdmBriefcase("ORIGINAL","VIEWEXPORTEDWITHEXT");
   }


   private void transferToEDMBriefcase(String action)
   {
      ASPManager mgr = getASPManager();
      String brief_no = headset.getRow().getValue("BRIEFCASE_NO");

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      if (itemset0.countSelectedRows() == 0)
      {
         mgr.showAlert("DOCMAWDOCBCCOMPARENOROWSSELECTED: You have not selected documents to Accept");
         return;
      }

      ASPBuffer doc_buf = itemset0.getSelectedRows("LINE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOC_BC_CHANGE_STATUS,BOOKING_LIST,FILE_NAME,DOC_STATE,REVISION_TEXT,REVISION_SIGN,REVISION_DATE,DESCRIPTION1,DESCRIPTION2,DESCRIPTION3,DESCRIPTION4,DESCRIPTION5,DESCRIPTION6,FORMAT_SIZE,SCALE,REASON_FOR_ISSUE"); // Bug ID 47637, inoslk

      ASPBuffer act_buf = mgr.newASPBuffer();
      act_buf.addItem("BRIEF_NO", brief_no);
      act_buf.addItem("ACTION", action);
      act_buf.addItem("PARENT_REFRESH_METHOD","refreshParent();");
      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmBriefcase.page", act_buf, doc_buf);
      launch_edm_briefcase = true;

      itemset0.setFilterOff();
      itemset0.unselectRows();
   }


   public void  okFindOnBCStatus(String sStatus)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ? AND DOC_BC_CHANGE_STATUS_DB = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      q.addParameter("DOC_BC_CHANGE_STATUS_DB",sStatus);
      //bug 58216 end
      // Bug ID 47334, inoslk, start
      q.setOrderByClause("TO_NUMBER(LINE_NO)");
      // Bug ID 47334, inoslk, end
      int headrowno = headset.getCurrentRowNo();
      mgr.querySubmit (trans,itemblk0);
      headset.goTo(headrowno);
   }


   public void  okFindOnBCStatusOnNew()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ? AND DOC_BC_CHANGE_STATUS_DB IN ('New','New Sheet')" );
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      //bug 58216 end
      // Bug ID 47334, inoslk, start
      q.setOrderByClause("TO_NUMBER(LINE_NO)");
      // Bug ID 47334, inoslk, end
      int headrowno = headset.getCurrentRowNo();
      mgr.querySubmit (trans,itemblk0);
      headset.goTo(headrowno);
   }


   public void  okFindOnBCStatusOnNewRev()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk0);
       //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ? AND DOC_BC_CHANGE_STATUS_DB IN ('New','New Sheet')" );
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      //bug 58216 end
      // Bug ID 47334, inoslk, start
      q.setOrderByClause("TO_NUMBER(LINE_NO)");
      // Bug ID 47334, inoslk, end
      int headrowno = headset.getCurrentRowNo();
      mgr.querySubmit (trans,itemblk0);
      headset.goTo(headrowno);
   }

   // Bug ID 47010, inoslk, start
   public void okFindOnBcIssueStatus(String sBcIssueState)
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ? AND DOC_BRIEFCASE_ISSUE_API.Get_Brief_Doc_Issue_State(BRIEFCASE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REVISION) = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      q.addParameter("BC_ISSUE_STATE",sBcIssueState);
      //bug 58216 end
      // Bug ID 47334, inoslk, start
      q.setOrderByClause("TO_NUMBER(LINE_NO)");
      // Bug ID 47334, inoslk, end
      int headrowno = headset.getCurrentRowNo();
      mgr.querySubmit (trans,itemblk0);
      headset.goTo(headrowno);
   }
   // Bug ID 47010, inoslk, end

   public void showNewSheets()
   {
      okFindOnBCStatusOnNew();
   }

   public void showNewRevision()
   {
      okFindOnBCStatus("New Rev");
   }


   public void showChangedDocument()
   {
      okFindOnBCStatus("Changed");
   }


   public void showUnChangedDocument()
   {
      okFindOnBCStatus("Unchanged");
   }


   public void showAll()
   {
      okFindITEM0();
   }

   // Bug ID 47010, inoslk, start
   public void showPending()
   {
      okFindOnBcIssueStatus(sBcIssuePending);
   }
   // Bug ID 47010, inoslk, end

   // Opens a document transferred from Doc Dompare Window for editing
   public void openInEditLayOut()
   {
      ASPManager mgr = getASPManager();
      
      String line_no = mgr.readValue("LINE_NO_TO_EDIT");
      do
      {
         if (line_no.equals(itemset0.getRow().getValue("LINE_NO")))
         {
            itemset0.goTo(itemset0.getCurrentRowNo());
            break;
         }

      } while (itemset0.next());
      itemlay0.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
   }

   //Bug ID 43698, inoslk, start
   public void editUnpacked()
   {
      itemset0.goTo(itemset0.getRowSelected());
      itemlay0.setLayoutMode(ASPBlockLayout.EDIT_LAYOUT);
   }
   //Bug ID 43698, inoslk, end

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCBRIEFCASECOMPARETITLE: BriefCase Import and Compare";
   }


   protected String getTitle()
   {
      return "DOCMAWDOCBRIEFCASECOMPARETITLE: BriefCase Import and Compare";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      AutoString presentation = new AutoString();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCBRIEFCASECOMPARETITLE2: Briefcase Import and Compare"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"COMMAN_COMMAND\" value=\"\"> \n");
      out.append("<input type=\"hidden\" name=\"REFRESH_ROW\" value=\"\"> \n");
      out.append("<input type=\"hidden\" name=\"SET_BOOKINGLIST\" value=\"\"> \n");
      out.append("<input type=\"hidden\" name=\"LINE_NO_TO_EDIT\" value=\"\"> \n");
      out.append(mgr.startPresentation("DOCMAWDOCBRIEFCASECOMPARETITLE2: Briefcase Import and Compare"));

      out.append(headlay.show());

      if (headlay.isSingleLayout())
      {
         if (itemlay0.isMultirowLayout())
         {
            //  Hyperlinks
            out.append(itembar0.showBar());

            //Radio buttons
            out.append("<table border=0 cellspacing=0 cellpadding=2 cols=2 width=\"65%\">\n");
            out.append("<tr><td nowrap>&nbsp;&nbsp;</td><td nowrap>\n");
            out.append("<table><tr><td  nowrap>\n");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWALL: Show All", "CHECKMODE", "4",checkVal.equals("4") , "OnClick=\"modeChanged()\""));
            out.append("</td><td  nowrap>");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWCHANGED: Show Changed", "CHECKMODE", "2",checkVal.equals("2"), "OnClick=\"modeChanged()\""));
            out.append("</td><td  nowrap>\n");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWNEWSHEETS: Show New/New Sheets", "CHECKMODE", "1", checkVal.equals("1"), "OnClick=\"modeChanged()\""));
            out.append("</td><td  nowrap>");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWUNCHANGED: Show Unchanged", "CHECKMODE", "3", checkVal.equals("3"),"OnClick=\"modeChanged()\""));
            out.append("</td><td  nowrap>");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWNEWREV: Show New Revision", "CHECKMODE", "5", checkVal.equals("5"),"OnClick=\"modeChanged()\""));
	    // Bug ID 47010, inoslk, start
            out.append("</td><td  nowrap>");
            out.append(fmt.drawRadio("DOCMAWDOCBRIEFCASECOMPARESHOWPENDING: Show Pending", "CHECKMODE", "6", checkVal.equals("6"),"OnClick=\"modeChanged()\""));
	    // Bug ID 47010, inoslk, end
            out.append("</td></tr></table>\n");
            out.append("</td></tr></table>\n");
            out.append(itemlay0.generateDataPresentation());
         }
         else
         {
            out.append(itemlay0.show());
         }
      }
      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      if (launch_edm_briefcase)
         appendDirtyJavaScript("launchEdmBriefcase();\n");

      if (launch_edm_macro)
         appendDirtyJavaScript("launchEdmMacro();\n");

      if (luanch_compare_window)
      {
         appendDirtyJavaScript("launchCompareWindow();");
      }


      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{ window.status.value=\"refresh parent\";\n");
      appendDirtyJavaScript(" document.form.REFRESH_ROW.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function modeChanged()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("window.\n");
      appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function launchCompareWindow() {\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(url));
      appendDirtyJavaScript("\",\"anotherWindow\",\"status=no,resizable,scrollbars,width=747,height=500,left=100,top=100\");\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function launchEdmBriefcase() {\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(url));
      appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function launchEdmMacro() {\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(url));
      appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("      function lovObjectId(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if(params) param = params;\n");
      appendDirtyJavaScript("else param = '';\n");
      appendDirtyJavaScript("try{var enable_multichoice =(true && ITEM0_IN_FIND_MODE);}catch(er){\n");
      appendDirtyJavaScript("enable_multichoice=false;}\n");
      appendDirtyJavaScript("var f1 = getField_('LU_NAME',i);\n");
      appendDirtyJavaScript("   if (f1.value==\"\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      alert('LU Name field requires a value');\n");
      appendDirtyJavaScript("return;");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("var key_value = (getValue_('OBJECT_ID',i).indexOf('%') !=-1)? getValue_('OBJECT_ID',i):'';\n");
      appendDirtyJavaScript("openLOVWindow('OBJECT_ID',i,\n");
      appendDirtyJavaScript("'ObjectConnection.page?LU_FROM_COMPARE=YES&__DYNAMIC_DEF_KEY='+getValue_('LU_NAME',i)+ '^LU_FROM_COMPARE' +''\n"); // Bug ID 65462
      appendDirtyJavaScript(",550,500,'validateObjectId');\n");
      appendDirtyJavaScript("}\n");

      //Bug ID 65462, Start
      appendDirtyJavaScript("function validateObjectId()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var default_objid= getValue_('OBJECT_ID',i);\n");
      appendDirtyJavaScript("   var arr = default_objid.split(\"~\")\n");
      //Since split returns the whole string if seperator is not found
      appendDirtyJavaScript("   if(arr.length > 1) \n");
      appendDirtyJavaScript("      getField_('OBJECT_ID',i).value=arr[1];\n");
      appendDirtyJavaScript("}\n");
      //Bug ID 65462, End

      if (call_bookinglist_lov)
      {
         appendDirtyJavaScript("lovBookingList(-1);\n");
         appendDirtyJavaScript("function lovBookingList(i) \n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   var key_value = (getValue_('BOOKING_LIST',i).indexOf('%') !=-1)? getValue_('BOOKING_LIST',i):'';\n");
         appendDirtyJavaScript("   openLOVWindow('SET_BOOKINGLIST',i,\n");
         appendDirtyJavaScript("   'BookListLov.page'\n");
         appendDirtyJavaScript("   ,550,500,'setBookingList');\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function setBookingList()\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");
         call_bookinglist_lov = false;

      }
      appendDirtyJavaScript("function editComparingRow(line_no)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.forms[0].LINE_NO_TO_EDIT.value=line_no;\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");


      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
