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
*  File         : DocBriefcase.java
*  Description  : Create and manage document briefcases
*
*
*
*  18-04-2003  Bakalk  Created.
*  22-04-2003  Dikalk  Fixed a few bugs in layout
*  22-04-2003  Dikalk  Added RMB Export Briefcase
*  23-04-2003  Bakalk  Fixed some more bugs in layout.
*  23-04-2003  NiSilk  Added okFind,okFindITEM in run method to populate the form after data transfer
*  24-04-2003  Dikalk  Added method transferToEdmBriefcase()
*  24-04-2003  NiSilk  Added method importBriefcase() and menu Import Briefcase
*  30-04-2003  Bakalk  Implemented exportBriefcase. (downloading files to client).
*  05-05-2003  Bakalk  Added commands Delete,Add documents, unlock Documents and document issue.
*  06-05-2003  Bakalk  Implemented Delete.
*  07-05-2003  NiSilk  Implemented methods goToDocIssue and resetBriefcase.
*  07-05-2003  InoSlk  Implemented Unlock Document, singlerow and multirow.
*  07-05-2003  Bakalk  Implemented Add Documents.
*  08-05-2003  InoSlk  Fixed the refresh problem in Unlock Document.
*  08-05-2003  NiSilk  Modified method importBriefcase to check for failed documents during export
*  13-05-2003  Dikalk  Removed method getBCStatus()
*  20-05-2003  NiSilk  modified method adjust().
*  20-05-2003  Dikalk  Reviewed the code. Changed variable names to IFS standards. Removed unused variables,
*                      reduandant/obsolete code. Removed unnecessary debug code. Removed unused fields in the headblk,
*                      and fixed quite a few bugs in the page.*
*  03-06-2003  NiSilk  Removed fields INCLUDE_VIEW_COPY and INCLUDE_RED_LINE from the header section.
*  11-06-2003  InoSlk  Added Action 'Export Rejected Documents'.
*  17-06-2003  InoSlk  Added methods createNewBc, modified transferToEdmBriefcase and exportRejectedDocs
*                      to implement Export Rejected Documents.
*  20-06-2003  NiSilk  call Id 97610 Fixed - Added methods saveReturn(), saveNewRecord(), saveNew()
*                      to override the saveNew and saveReturn functionality.
*
*  25-06-2003  Bakalk  Modified SaveNewRecord.
*  26-06-2003  Bakalk  Changed the flow of Exporting a bc. State changed after process is completed.
*                      Added a new method completeExportBriefcase().
*  26-06-2003  NiSilk  Fixed the refresh problem when adding a bc issue (File exist and title fields were not refreshed).
*  01-07-2003  NiSilk  Added method completeExportRejected, and modified methods run and okFind to refresh the page properly.
*  10-07-2003  InoSlk  Added an alert message when there are no document issues to be exported.
*                      Added code to select the chosen row in multi row layout.
*                      Added workaround for getting the incorrect item set in multi row layout.
*  11-07-2003  InoSlk  Added valid condition for action 'Reset Briefcase'.
*  15-07-2003  Bakalk  Added a new method unlockDocumentFromCommandBar.
*  16-07-2003  Bakalk  Removed the method unlockDocumentFromCommandBar.
*                      Made a confirm box pop up prior to unlocking a doc issue.
*  17-07-2003  NiSilk  Changes in method unlockDocument to change the edm state (when the bc is in state Under Import).
*  22-07-2003  InoSlk  Modified method 'resetBriefcase'.
*  24-07-2003  Dikalk  Modified the validate() method
*  24-07-2003  Bakalk  Modified the okFind() method. Made reverse the transfered buffer
*                      if it comming from doc issue afeter adding document to a bc.
*  24-07-2003  Dikalk  Modified completeExportBriefcase()
*  25-07-2003  Dikalk  Modified methods exportBriefcase() and completeExportBriefcase()
*  28-07-2003  NiSilk  Modified method adjust().
*  28-07-2003  Bakalk  Added a check box for "FILEEXIST".
*  31-07-2003  Dikalk  Implemented the Error logging functionality. Modified several methods.
*  31-07-2003  Dikalk  Found bugs when exporting, importing and reseting a briefcase in the
*                      multirow mode
*  01-08-2003  Bakalk  Made Delete button conditionall visible. In item block added a normal
*                      check box in new layout instead of image.
*  04-08-2003  InoSlk  Fixed MultiRow select error in method deleteBriefcase.
*  07-08-2003  Thwilk  Call ID 97618 - Added runQuery() method to query for a briefcaseNo when passed from CENTURA CLIENT.
*  08-08-2003  InoSlk  Call ID 100800 - Modified method unlockDocument to unlock Imported Documents.
*  12-08-2003  NiSilk  Fixed Call 100804 Modified method unlockDocument to change the edm state correctly after unlocking.
*  26-08-2003  Bakalk  Fixed the call: 101707 :
*  27-08-2003  InoSlk  Call ID 101731: Modified doReset() and clone().
*  27-08-2003  NiSilk  Fixed Call ID 101833. Added method unlockDocumentHyperLink and modified method unlockDocument and getContents.
*  03-08-2003  Dikalk  Call ID: 102318. Fixed bug in javascript methods selectAll and DeSelectAll
*  09-09-2003  InoSlk  Call ID 102857: Changed the code so that OCX is downloaded as needed.
*  09-09-2003  NiSilk  Call ID:102637.
*  19-09-2003  BaKalk  Call ID:103649. Made new button in item lay available only for "Created" bcs.
*                      Action button in item lay removed when multirow.
*  06-11-2003  InoSlk  Call ID 110303 - Replaced calls to selectRow() with storeSelections() where
*                      needed and relevant.
*  19-04-2004  InoSlk  Bug ID 43915 - Modified okFindITEM0().
*  03-06-2004  Shthlk  Bug Id 45106 - Changed unlockDocument method to remove the document from doc_briefcase_unpacked_tab.
*  03-06-2004  Shthlk  Bug Id 45197 - Modified exportRejectedDocs to transfer the current briefcase number to edmbriefcase.
*  07-06-2004  Shthlk  Bug Id 45106 - Modified unlockDocument to set the briefcase to import when all the documents are unlocked.
*  11-06-2004  Shthlk  Bug Id 45106 - Modified unlockDocument again to set the briefcase to import when all the documents are unlocked.
*  20-07-2004  InoSlk  Bug ID 45944 - Modified unlockDocument() and preDefine().
*  26-07-2004  Shthlk  Bug Id 45547 - Added action deleteLocalFiles. Check bug id for more information.
*  30-07-2004  Shthlk  Bug Id 45547 - Modified adjust() to enable deleteLocalFiles when briefcase is in state 'Exported'.
*  02-08-2004  Shthlk  Bug Id 45851 - Changed length of 'BRIEFCASE_NO' to enable export of rejected documents. Now the users
*  02-08-2004          can enter BRIEFCASE_NO with 10 characters and export it 999 times as the table value for BRIEFCASE_NO is 15.
*  04-08-2004  Shthlk  Bug Id 45606 - Modified unlockDocument to enable unlock of documents which are not in the
*  04-08-2004          doc_briefcase_unpacked_tab and to promote the briefcase to 'Import' if there are no pending documents.
*  07-09-2004  InoSlk  Bug ID 46718 - Modified in adjust().
*  10-11-2004  InoSlk  Bug ID 47171 - Added method okFindUnpacked(). Modified completeExportRejected(), transferToEdmBriefcase(),
*  10-11-2004          exportRejectedDocs() and preDefine().
*  10-11-2004  Raselk  Bug ID 47033 - Modified createNewBc to add a traslation constant to 'Rejected Documents'.
*  15-11-2004  InoSlk  Bug ID 47907 - Added class variables bGetConfirmation, iRowNo. Modified run(), adjust(), preDefine(),
*  15-11-2004          importBriefcase(),getContents(). Added methods gotoImportComparePage(),undoImport() & completeUndoExport().
*  15-11-2004  InoSlk  Bug ID 47907 - Modifed unlockDocument() and preDefine().
*  17-11-2004  InoSlk  Bug ID 48028 - Modified adjust(),newRow(),completeExportRejected(),transferToEdmBriefcase(),exportRejectedDocs() & preDefine().
*  19-11-2004  InoSlk  Bug ID 47986 - Modified getContents() so that the edmBriefcase page is not launched in the same window as edmMacro page
*  19-11-2004          when both are being launched simultaneuosly.
*  22-11-2004  InoSlk  Bug ID 47994 - Modified adjust ().
*  13-12-2004  Karalk  Bug  Id 48271- modified  transferToEdmBriefcase(), createNewBc(),  getcontents() resetBriefcase(). so that when create a new rejected briefcase REJECTED_BC column in view will be set to true.
*  14-12-2004  Karalk  Bug Id 48397 - Modified run() method.
*  17-12-2004  Shthlk  Bug Id 48046 - Modified unlockDocument to check whether the file is already checked out to briefcase even though briefcase state is Created.
*  22-12-2004  Shthlk  Bug Id 48298 - Modified deleteLocalFiles to get the briefcase imported path.
*  22-12-2004          Replace context veriable BRIEFCASENUMBER with IMPORTEDPATH.
*  01-02-2005  Karalk  Bug Id 49067  - modified Predefine()/headblk.addField("STATE") to add Lov to State field.
*  10-03-2005  Karalk  Bug Id 49701    for this bug  correction was removed after reopen the bug.  now there is no correction in this file for this bug
*  23-06-2005  KARALK  BUG ID 50158 is merged.
*  17-05-2006  KARALK  Bug Id 56784 - Modified resetBriefcase() and importBriefcase() to send the data useing a buffer. This will ensure encoding of double byte characters.
*  17-05-2006          Removed the transferToEdmBriefcase(String action) as it's not used anymore. Moved the fix for bug 48271 which was in transferToEdmBriefcase(String action) to resetBriefcase()
*  17-07-2006  BAKALK  Bug ID 58216, Fixed Sql Injection.
*  13-11-2006  NIJALK  Bug ID 61462, Modified method saveNewRecord().
*  29-05-2007  NIJALK  Bug ID 145180, Handled double quotaions in alert messages.
*  07-08-2007  ILSOLK  Eliminated SQLInjection.
*  09-08-2007  NaLrlk  XSS Correction.
*  18-09-2007  DinHlk  Patched in Bug Id 62423 - Added method checkObject(). Modified getContents() & run() to do the plant design object check before continue to compare.
*  29-10-2007  DULOLK  Bug Id 65509 - Modified checkObject() to accomodate all other objects besides plant design.
*  21-11-2007  SHTHLK  Bug Id 67519 - Modified transferToEdmBriefcase() and completeResetBriefcase() to get all the rows in itemset0.
*  03-12-2007  AMNALK  Bug Id 69579 - Added a warning message when reseting the briefcase.
*  03-03-2008  VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*  15-04-2008  SHTHLK  Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*  05-08-2008  NIJALK  Bug Id 75490, Modified okFind(), okFindITEM0(), saveNewRecord() and runQuery().
*  20-07-2010  AMNALK  Bug Id 89939, Modified okFind() to support for multiple document selections.
*  03-09-2010  AMNALK  Bug Id 92560, Modified createNewBc() and preDefine() to enable the briefcase description editable.
*  ------------------------------------------------------------------------------
*/



package ifs.docmaw;

import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class DocBriefcase extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocBriefcase");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext         ctx;
   private ASPBlock           headblk;
   private ASPRowSet          headset;
   private ASPCommandBar      headbar;
   private ASPTable           headtbl;
   private ASPBlockLayout     headlay;

   private ASPBlock           itemblk0;
   private ASPRowSet          itemset0;
   private ASPCommandBar      itembar0;
   private ASPTable           itemtbl0;
   private ASPBlockLayout     itemlay0;

   private ASPBlock           paramblk;
   private ASPField           f;
   private ASPHTMLFormatter   fmt;

   // Bug ID 47171, inoslk, start
   private ASPBlock	      unpackedblk;
   private ASPRowSet          unpackedset;
   // Bug ID 47171, inoslk, end

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery             q;
   private ASPCommand           cmd;
   private ASPBuffer            data;
   private ASPBuffer            keys;

   private boolean              changed_mode;
   private boolean              request_url = false;
   private boolean              bUnlock_current_row;
   private boolean              bUnlock_multi_row;
   private boolean              bDelete_current_row;

   private int                  prev_layout_mode;

   private String               search_url;
   private String               url;
   private String strIFSCliMgrOCX;

   private String sBcExported;
   private String sBcImported;
   private String sBcCreated;
   private String sBcUnderImport;
   private String sDocBcIssueAccepted;
   private String sDocBcIssueRejected;
   private String sRejBc;
   private boolean IsRejectedBc1;//Bug id 48271, karalk
   //Bug Id 45547 - Start
   private String  sImportedPath; //Bug Id 48298
   private boolean bDelete_Local_Files;
   private boolean bDelete_Briefcase_Files;
   //Bug Id 45547 - End
   // Bug ID 47907, inoslk, start
   private boolean bGetConfirmation = false;
   private int iRowNo = 0;
   // Bug ID 47907, inoslk, end
   private String sEdmFileCheckedOutToBc; //Bug Id 48046
   private String sCheckObjectMessage;
   private boolean bCheckObjectConfirm;
   private boolean bObjectChecked;
   private boolean bConfirmReset = false;//Bug Id 69579

   public DocBriefcase(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   protected void doReset() throws FndException
   {
      //we have to initialize all mutable instance variables
      trans            = null;
      q                = null;
      cmd              = null;
      data             = null;
      keys             = null;

      changed_mode        = false;
      request_url         = false;
      bUnlock_current_row = false;
      bUnlock_multi_row = false;
      bDelete_current_row = false;

      prev_layout_mode = 0;

      search_url      = null;
      url             = null;
      strIFSCliMgrOCX = null;

      sBcCreated          = null;
      sBcExported         = null;
      sBcImported         = null;
      sBcUnderImport      = null;
      sDocBcIssueAccepted = null;
      sDocBcIssueRejected = null;
      sRejBc              = null;
      IsRejectedBc1       = false;//Bug id 48271, karalk
      //Bug Id 45547 - Start
      sImportedPath = null;  //Bug Id 48298
      bDelete_Local_Files = false;
      bDelete_Briefcase_Files = false;
      //Bug Id 45547 - End

      // Bug ID 47907, inoslk, start
      bGetConfirmation = false;
      iRowNo = 0;
      // Bug ID 47907, inoslk, end
      sEdmFileCheckedOutToBc = null; //Bug Id 48046
      sCheckObjectMessage = null;
      bCheckObjectConfirm = false;
      bObjectChecked = false;
      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocBriefcase page = (DocBriefcase)(super.clone(obj));

      // Initializing mutable attributes
      page.trans            = null;
      page.q                = null;
      page.cmd              = null;
      page.data             = null;
      page.keys             = null;

      page.changed_mode        = false;
      page.request_url         = false;
      page.bUnlock_current_row = false;
      page.bUnlock_multi_row   = false;
      page.bDelete_current_row = false;

      page.prev_layout_mode = 0;

      page.search_url      = null;
      page.url             = null;
      page.sRejBc          = null;
      page.strIFSCliMgrOCX = null;

      page.sBcCreated          = null;
      page.sBcExported         = null;
      page.sBcImported         = null;
      page.sBcUnderImport      = null;
      page.sDocBcIssueAccepted = null;
      page.sDocBcIssueRejected = null;
      page.IsRejectedBc1       =false;//Bug id 48271, karalk
      //Bug Id 45547 - Start
      page.sImportedPath = null; //Bug Id 48298
      page.bDelete_Local_Files = false;
      page.bDelete_Briefcase_Files = false;
      //Bug Id 45547 - End

      // Bug ID 47907, inoslk, start
      page.bGetConfirmation = false;
      page.iRowNo = 0;
      // Bug ID 47907, inoslk, end
      page.sEdmFileCheckedOutToBc = null;  //Bug Id 48046
      page.sCheckObjectMessage = null;
      page.bCheckObjectConfirm = false;
      page.bObjectChecked = false;
      page.bConfirmReset = false;//Bug Id 69579
      
      // Cloning immutable attributes
      page.ctx     = page.getASPContext();
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

      page.paramblk = page.getASPBlock(paramblk.getName());
      page.f        = page.getASPField(f.getName());
      page.fmt      = page.getASPHTMLFormatter();

      // Bug ID 47171, inoslk, start
      page.unpackedblk = page.getASPBlock(unpackedblk.getName());
      page.unpackedset = page.unpackedblk.getASPRowSet();
      // Bug ID 47171, inoslk, end

      return page;
   }


   public void run() throws FndException
   {
      ASPManager mgr   = getASPManager();
      trans            = mgr.newASPTransactionBuffer();
      ctx              = mgr.getASPContext();
      fmt              = mgr.newASPHTMLFormatter();

      prev_layout_mode = ctx.readNumber("PREVLAYOUT",0);
      changed_mode     = ctx.readFlag("CHANGE",false);
      sImportedPath    = ctx.readValue("IMPORTEDPATH","");  //Bug Id 48298
      // Bug ID 47907, inoslk, start
      iRowNo	= ctx.readNumber("ROW_NO",0);
      // Bug ID 47907, inoslk, end

      request_url      = false;
      strIFSCliMgrOCX = "";

      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

      sDocBcIssueAccepted = dm_const.doc_briefcase_issue_accepted;
      sDocBcIssueRejected = dm_const.doc_briefcase_issue_rejected;
      sBcCreated = dm_const.doc_briefcase_created;
      sBcExported = dm_const.doc_briefcase_exported;
      sBcImported = dm_const.doc_briefcase_imported;
      sBcUnderImport = dm_const.doc_briefcase_under_import;
      sEdmFileCheckedOutToBc = dm_const.edm_file_checked_out_to_bc; //Bug Id 48046

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction()); // SQLInjection_Safe ILSOLK 20070807
      else if (mgr.commandLinkActivated())
         eval(mgr.commandLinkFunction()); // SQLInjection_Safe ILSOLK 20070807
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.dataTransfered ())
      {
         okFind();
         okFindITEM0();

      }
      //Bug Id 75490, Start
      else if (! (mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")) && mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")) && 
		  mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET")) && mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))))
      {
         runQuery();
      }
      //Bug Id 75490, End
      else if ("IMPORT_COMPLETE".equals(mgr.readValue("ACTION")))
         checkObject();
      else if ("IMPORT_CHECK_COMPLETE".equals(mgr.readValue("ACTION")))
         transferToDocBriefcaseCompare();
      else if ("EXPORT_COMPLETE".equals(mgr.readValue("ACTION")))
         completeExportBriefcase();
      else if ("EXPORT_REJ_COMPLETE".equals(mgr.readValue("ACTION")))
         completeExportRejected();
      else if ("RESET_COMPLETE".equals(mgr.readValue("ACTION")))
         completeResetBriefcase();
      else if (!mgr.isEmpty(mgr.readValue("MULTIROWACTION")))
         eval(mgr.readValue("MULTIROWACTION")); // SQLInjection_Safe ILSOLK 20070807
      else if (!mgr.isEmpty(mgr.readValue("UNLOCK_CURRENTROW")))
      {
         debug("unlock_currentrow is not empty");
         if ("YES".equals(mgr.readValue("UNLOCK_CURRENTROW")))
         {
            debug("YES is selected");
            unlockDocument();
         }
      }
      else if (!mgr.isEmpty(mgr.readValue("DELETE_CURRENTROW")))
      {
         if ("YES".equals(mgr.readValue("DELETE_CURRENTROW")))
         {
            deleteBriefcase();
         }
         else
         {
            itemset0.unselectRows();
         }
      }
      //Bug Id 45547, Start
      else if (!mgr.isEmpty(mgr.readValue("DELETE_LOCALFILE")))
      {
         if ("YES".equals(mgr.readValue("DELETE_LOCALFILE")))
         {
             bDelete_Briefcase_Files = true;
             deleteBriefcase();
         }
         else
         {
            headset.unselectRows();
         }
      }
      //Bug Id 45547, End
      // Bug ID 47907, inoslk, start
      else if (!mgr.isEmpty(mgr.readValue("UNDO_IMPORT")))
      {
	 if ("YES".equals(mgr.readValue("UNDO_IMPORT")))
	    completeUndoExport();
	 else
	 {
	    mgr.showAlert(mgr.translate("DOCMAWDOCBCOPERATIONCANCELLED: Operation Cancelled."));
	    headset.unselectRows();
	 }	
      }
      //Bug Id 69579, Start
      else if (!mgr.isEmpty(mgr.readValue("RESET_BRIEFCASE")))
      {
         if ("YES".equals(mgr.readValue("RESET_BRIEFCASE")))
	 {
             bConfirmReset = true;
	     resetBriefcase();
	 }
         else
            headset.unselectRows();
      }
      //Bug Id 69579, End
      // Bug id 48397,  karalk Start.
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
	  okFind();
       // Bug id 48397,  karalk End.

      // Bug ID 47907, inoslk, end

      adjust();
      ctx.writeNumber("PREVLAYOUT", prev_layout_mode);
      ctx.writeFlag("CHANGE", changed_mode);
      ctx.writeValue("IMPORTEDPATH", sImportedPath); //Bug Id 48298
      // Bug ID 47907, inoslk, start
      ctx.writeNumber("ROW_NO",iRowNo);
      // Bug ID 47907, inoslk, end
   }


   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.BACK);

         headbar.disableCommand("addDocuments");
         headbar.disableCommand("exportBriefcase");
         headbar.disableCommand("importBriefcase");
         headbar.disableCommand("resetBriefcase");
         headbar.disableCustomCommand("deleteBriefcase_client");
         headbar.disableCustomCommand("deleteLocalFiles"); //Bug Id 45547
      }

      //Bug ID 48028, inoslk, Removed the check for disabling 'exportRejectedDocs'

      if (itemset0.countRows()==0 && (itemlay0.getLayoutMode()== itemlay0.SINGLE_LAYOUT))
      {
         itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
         changed_mode = true;
      }
      else if ((itemlay0.getLayoutMode()== itemlay0.MULTIROW_LAYOUT) && changed_mode)
      {
         itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
         changed_mode = false;
      }

      if (headlay.getLayoutMode()== headlay.NEW_LAYOUT)
      {
         mgr.getASPField("BRIEFCASE_NO").deactivateLOV();
      }
      else
      {
         mgr.getASPField("BRIEFCASE_NO").activateLOV();
      }

      if (headlay.getLayoutMode()== headlay.SINGLE_LAYOUT && headset.countRows()>0)
      {
         //headset.refreshRow();

         headbar.disableCustomCommand("deleteBriefcase_client");
         if (!"TRUE".equals(headset.getRow().getValue("CAN_DELETE_THIS")))
         {
            headbar.disableCommand(headbar.DELETE);
         }
      }
      else if (headlay.getLayoutMode()== headlay.MULTIROW_LAYOUT)
      {
         headbar.disableCommand(headbar.DELETE);
         headbar.addCommandValidConditions("deleteBriefcase_client","CAN_DELETE_THIS","Enable","TRUE");
      }

      if (itemset0.countRows()< 1)
      {
         itembar0.disableMultirowAction();
      }
      else
      {
         itembar0.enableMultirowAction();
      }

      String state_created = DocmawConstants.getConstantHolder(mgr).doc_briefcase_created;
      String state_exported = DocmawConstants.getConstantHolder(mgr).doc_briefcase_exported;
      String state_under_import = DocmawConstants.getConstantHolder(mgr).doc_briefcase_under_import;

      //BUG Id 49701 Start column OBJSTATE is used insterd of STATE.
      //BUG Id 49701  after reopen the bug this correction was removed.  now there is no correction in this file for ths bug.
      headbar.addCommandValidConditions("addDocuments",    "STATE", "Enable", state_created);
      headbar.addCommandValidConditions("exportBriefcase", "STATE", "Enable", state_created);
      headbar.addCommandValidConditions("importBriefcase", "STATE", "Enable", state_exported); // Bug ID 47907, inoslk
      headbar.addCommandValidConditions("exportRejectedDocs", "CAN_RE_EXPORT", "Enable", "TRUE"); // Bug ID 48028, inoslk
      headbar.addCommandValidConditions("resetBriefcase", "STATE", "Enable", sBcExported);
      //BUG Id 49701 End.
      // Bug ID 47907, inoslk, start
      headbar.addCommandValidConditions("undoImport", "STATE", "Enable", state_under_import);
      headbar.addCommandValidConditions("gotoImportComparePage", "CAN_GO_TO_COMPARE_PAGE", "Enable", "TRUE"); // Bug ID 48028, inoslk
      // Bug ID 47907, inoslk, end
      if (headset.countRows()>0)
      {
         if (!state_created.equals(headset.getRow().getValue("STATE")))
         {
            itembar0.disableCommand(this.itembar0.NEWROW);
         }
         //Bug Id 45547, Start
         if (!"TRUE".equals(headset.getRow().getValue("CAN_DELETE_THIS")))
            headbar.disableCustomCommand("deleteLocalFiles");
         else
         {
            if (sBcCreated.equals(headset.getValue("STATE")))
               headbar.disableCustomCommand("deleteLocalFiles");
         }
         //Bug Id 45547, End

         //Bug ID 47994, inoslk, moved the following block inside the check (headset.countRows()>0)

         // Bug ID 46718, inoslk, start
         if (itemset0.countRows() > 0)
         {
            if (headset.getValue("STATE").equals(sBcCreated))
               itembar0.enableCommand(itembar0.DUPLICATEROW);
            else
               itembar0.disableCommand(itembar0.DUPLICATEROW);
         }
         // Bug ID 46718, inoslk, end
      }
   }


   public void newRow()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("HEAD","DOC_BRIEFCASE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("CAN_DELETE_THIS","TRUE");
      //Bug ID 48028, inoslk, start
      data.setFieldItem("CAN_GO_TO_COMPARE_PAGE","FALSE");
      data.setFieldItem("CAN_RE_EXPORT","FALSE");
      //Bug ID 48028, inoslk, end
      headset.addRow(data);
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOC_BRIEFCASE_ISSUE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("BRIEFCASE_NO", headset.getValue("BRIEFCASE_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   }


   public void completeExportRejected()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer doc_buf = mgr.newASPBuffer();

      //Bug ID 48028, inoslk, start
      headset.goTo(iRowNo);
      //Bug ID 48028, inoslk, end
      // Bug ID 47171, inoslk, start
      headset.refreshRow();
      // Bug ID 47171, inoslk, end
      String  sCurrentBc = headset.getValue("BRIEFCASE_NO");
      sRejBc = getRejectedBcName(sCurrentBc);

      //Bug ID 48028, inoslk, start
      trans.clear();

      cmd = trans.addCustomCommand("REMOVEUNPACKEDDATA","Doc_Briefcase_Unpacked_API.Remove_Unpacked_Bc_Data");
      cmd.addParameter("BRIEFCASE_NO",sCurrentBc);

      mgr.perform(trans);
      //Bug ID 48028, inoslk, end

      okFind();
      okFindITEM0();
   }

   private void runQuery()
   {
      ASPManager mgr = getASPManager();
      
      q = trans.addQuery(headblk);
      //Bug 75490, Start
      q.addWhereCondition("BRIEFCASE_NO IN (SELECT BRIEFCASE_NO FROM DOC_BRIEFCASE_ISSUE WHERE DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?)");
      q.addParameter("DOC_CLASS", mgr.getQueryStringValue("DOC_CLASS"));
      q.addParameter("DOC_NO", mgr.getQueryStringValue("DOC_NO"));
      q.addParameter("DOC_SHEET", mgr.getQueryStringValue("DOC_SHEET"));
      q.addParameter("DOC_REV", mgr.getQueryStringValue("DOC_REV"));
      //Bug 75490, End      
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
         headset.clear();
      }
      //Bug 75490, Start
      else if (headset.countRows() > 1)
      {
          headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }
      //Bug 75490, End      
      else
      {
         okFindITEM0();
      }
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      search_url = mgr.createSearchURL(headblk);
      int currRow = 0;
      boolean after_adding_docs = false;
      String currentBc="";

      trans.clear();
      q = trans.addQuery(headblk);

      if (!mgr.isEmpty(sRejBc))
      {
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",sRejBc);
         //bug 58216 starts
      }

      //Bug 75490, Start
      if (mgr.dataTransfered())
      {
         ASPBuffer buff = mgr.getTransferedData();

         currentBc = mgr.getQueryStringValue("CURR_BC_NO");
         if (buff.itemExists("BRIEFCASE_NO") || buff.itemExists("DATA/BRIEFCASE_NO"))
         {
             q.addOrCondition(buff);
         }
         else
         {
             // Bug Id 89939, start
             int itemCount = buff.countItems();
             int count = 1;
             String whereCondition = "";
             if (itemCount > 1)
             {
                whereCondition = "BRIEFCASE_NO IN (SELECT BRIEFCASE_NO FROM DOC_BRIEFCASE_ISSUE WHERE DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?)";
                
                while(itemCount > count)
                {
                   whereCondition = whereCondition + " OR BRIEFCASE_NO IN (SELECT BRIEFCASE_NO FROM DOC_BRIEFCASE_ISSUE WHERE DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?)";
                   count = count + 1;                   
                }
                count = 1;
                q.addWhereCondition(whereCondition);
                q.addParameter("DOC_CLASS", buff.getBufferAt(0).getValue("DOC_CLASS"));
                q.addParameter("DOC_NO", buff.getBufferAt(0).getValue("DOC_NO"));
                q.addParameter("DOC_SHEET", buff.getBufferAt(0).getValue("DOC_SHEET"));
                q.addParameter("DOC_REV", buff.getBufferAt(0).getValue("DOC_REV"));
                
                while(itemCount  > count)
                {
                   q.addParameter("DOC_CLASS", buff.getBufferAt(count).getValueAt(0));
                   q.addParameter("DOC_NO", buff.getBufferAt(count).getValueAt(1));
                   q.addParameter("DOC_SHEET", buff.getBufferAt(count).getValueAt(2));
                   q.addParameter("DOC_REV", buff.getBufferAt(count).getValueAt(3));
                   
                   count = count + 1;
                }
             }
             else
             {
                q.addWhereCondition("BRIEFCASE_NO IN (SELECT BRIEFCASE_NO FROM DOC_BRIEFCASE_ISSUE WHERE DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?)");
                q.addParameter("DOC_CLASS", buff.getBufferAt(0).getValue("DOC_CLASS"));
                q.addParameter("DOC_NO", buff.getBufferAt(0).getValue("DOC_NO"));
                q.addParameter("DOC_SHEET", buff.getBufferAt(0).getValue("DOC_SHEET"));
                q.addParameter("DOC_REV", buff.getBufferAt(0).getValue("DOC_REV"));
             }
             
             // Bug Id 89939, end
         }         
         buff.traceBuffer("---------------Trace Buffer-------------------");
      }

      q.setOrderByClause("BRIEFCASE_NO");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      
      eval(headset.syncItemSets());

      if (!mgr.isEmpty(mgr.getQueryStringValue("AFTER_ADDING_DOCS")))
         after_adding_docs = true;

      if (headset.countRows() > 0)
      {
          if (after_adding_docs)
          {
              headset.first();
             do
             {
                if (currentBc.equals(headset.getRow().getValue("BRIEFCASE_NO")))
                {
                   break;
                }
             }while (headset.next());
          }
          else if (headset.countRows() > 1)
          {
              headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
          }

          okFindITEM0();
      }
      else
      {
          mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
          headset.clear();
      }
      //Bug 75490, End
   }


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      //Bug 75490, Start
      q = trans.addEmptyQuery(itemblk0);
      //Bug 75490, End
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
       //bug 58216 starts
      q.includeMeta("ALL");
      int headrowno = headset.getCurrentRowNo();
      // Bug ID 43915, inoslk, Changed the submit to QuerySubmit so that the browse buttons do not disappear
      mgr.querySubmit(trans,itemblk0);
      headset.goTo(headrowno);
   }


   // Bug ID 47171, inoslk, start
   private void okFindUnpacked()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(unpackedblk);
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
       //bug 58216 starts
      q.includeMeta("ALL");
      mgr.querySubmit(trans,unpackedblk);
   }
   // Bug ID 47171, inoslk, end


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("BRIEFCASE_NO = ?");
      q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
       //bug 58216 starts
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }


   public void  saveReturn()
   {
      saveNewRecord("__SAVERETURN");
   }


   public void  saveNew()
   {
      saveNewRecord("saveNew");
      newRowITEM0();
   }


   public void  saveNewRecord(String action)
   {
      int currrow = headset.getCurrentRowNo();;

      ASPManager mgr = getASPManager();
      itemset0.changeRow();

      if ("New__".equals(itemset0.getRowStatus()))
      {
         trans.clear();

         //Bug 61462, Start
         cmd = trans.addCustomCommand("FILEREFEXIST","DOC_BRIEFCASE_ISSUE_API.File_Ref_Exist");
         cmd.addParameter("DOC_CLASS",itemset0.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",itemset0.getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",itemset0.getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",itemset0.getValue("DOC_REV"));
         cmd.addParameter("DUMMY1", "ORIGINAL");
         //Bug 61462, End

         mgr.submit(trans);
         headset.goTo(currrow);
         headset.refreshRow();
         trans.clear();
      }
      else
      {
         currrow = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(currrow);
      }
   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("DOC_NO".equals(val))
      {
         trans.clear();
         
         //bug 58216 starts
         ASPQuery q = trans.addQuery("DOCCLASS", "SELECT DOC_CLASS FROM DOC_TITLE WHERE DOC_NO=?");
         q.addParameter("DOC_NO",mgr.readValue("DOC_NO"));
         //bug 58216 ends

         cmd = trans.addCustomFunction("TITLE","Doc_Title_API.Get_Title","DOC_TITLE");
         cmd.addReference("DOC_CLASS", "DOCCLASS/DATA");
         cmd.addParameter("DOC_NO", mgr.readValue("DOC_NO"));


         cmd = trans.addCustomFunction("FILESTATE","EDM_FILE_API.GET_DOCUMENT_STATE","FILEEXIST");
         cmd.addReference("DOC_CLASS", "DOCCLASS/DATA");
         cmd.addParameter("DOC_NO", mgr.readValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", mgr.readValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", mgr.readValue("DOC_REV"));
         cmd.addParameter("DOC_NO", "ORIGINAL");

         trans = mgr.validate(trans);

         String doc_class = trans.getValue("DOCCLASS/DATA/DOC_CLASS");
         String title = trans.getValue("TITLE/DATA/DOC_TITLE");
         String file_state = trans.getValue("FILESTATE/DATA/FILEEXIST");

         String results = doc_class + "^" + (title == null ? "" : title) + "^" +(file_state == null? "0": "1") + "^";
         mgr.responseWrite(results);
      }
      else if ("DOC_REV".equals(val)|| "DOC_CLASS".equals(val)||"DOC_SHEET".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("FILESTATE","EDM_FILE_API.GET_DOCUMENT_STATE","FILEEXIST");
         cmd.addParameter("DOC_CLASS", mgr.readValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO", mgr.readValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET", mgr.readValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV", mgr.readValue("DOC_REV"));
         cmd.addParameter("DOC_NO", "ORIGINAL");
         trans = mgr.validate(trans);

         String file_state = trans.getValue("FILESTATE/DATA/FILEEXIST");
         String results = (mgr.isEmpty(file_state))? "0": "1" + "^";
         mgr.responseWrite(results);
      }

      mgr.endResponse();
   }


   public void deleteBriefcase()
   {
      ASPManager mgr = getASPManager();
      if (bDelete_Briefcase_Files)
         strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();

      if (headlay.isMultirowLayout())
         headset.setSelectedRowsRemoved();
      else
         headset.setRemoved();

      mgr.submit(trans);
   }

   //Bug Id 45547, Start  - Added deleteLocalFiles().
   public void deleteLocalFiles()
   {
       ASPManager mgr   = getASPManager(); //Bug Id 48298
       if (headlay.isMultirowLayout())
       {
              headset.storeSelections();
              headset.goTo (headset.getRowSelected());
       }
       else
          headset.selectRow();

       //Bug Id 48298, Start - Get the briefcase imported path
       trans.clear();
       ASPCommand cmd = trans.addCustomFunction("GETIMPORTEDPATH","DOC_BRIEFCASE_API.Get_Imported_Path","OUT_1");
       cmd.addParameter("IN_1",headset.getRow().getValue("BRIEFCASE_NO"));
       trans = mgr.perform(trans);
       sImportedPath = trans.getValue("GETIMPORTEDPATH/DATA/OUT_1");

       sImportedPath = sImportedPath.replaceAll("\\\\","\\\\\\\\");
       ctx.writeValue ("IMPORTEDPATH", sImportedPath);
       //Bug Id 48298, End

       bDelete_Local_Files = true;
   }
   //Bug Id 45547, End

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
      String bcNo     = headset.getValue("BRIEFCASE_NO");
      String url = mgr.getURL();
      keys  = headset.getRows("BRIEFCASE_NO");
      mgr.transferDataTo("DocIssue.page?ADDTOBC=YES&BC_NO="+mgr.URLEncode(bcNo)+"&CUR_ROW="+mgr.URLEncode(currRow +"")+"&SEND_URL="+mgr.URLEncode(url),keys);

   }


   public void resetBriefcase()
   {
      //Bug Id 56784, Start
      ASPManager mgr = getASPManager();
      ASPBuffer act_buf = mgr.newASPBuffer();
      //Bug Id 56784, End

      if (bConfirmReset) //Bug Id 69579
      {
      strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();
      //Bug Id 56784, Start
	  //Bug Id 69579, Start
	  headset.goTo(iRowNo); 
	  bConfirmReset = false;
	  //Bug Id 69579, End
      //Bug id 48271, karalk,  Strat
      trans.clear();
      cmd = trans.addCustomFunction("ISREJECTEDBC","Doc_Briefcase_API.Is_Rejected_Briefcase","OUT_1");
      cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));

      trans = mgr.perform(trans);
      String IsRejectedBc = trans.getValue("ISREJECTEDBC/DATA/OUT_1");

      if ("true".equals(IsRejectedBc)) {
         IsRejectedBc1=true;
      }
      //Bug id 48271, End.

      act_buf.addItem("BRIEF_NO", headset.getValue("BRIEFCASE_NO"));
      act_buf.addItem("ACTION", "RESET_BRIEFCASE");
      transferToEdmBriefcase(act_buf);
      headset.setFilterOff();
      //Bug Id 56784, End
   }
      //Bug Id 69579, Start
      else
      {
	   if (headlay.isMultirowLayout())
	  {
	     headset.storeSelections();
	     headset.setFilterOn();
	     iRowNo = headset.getRowSelected();
	     headset.setFilterOff();
	  }
	  else
	  {
	     headset.unselectRows();
	     headset.selectRow();
	     iRowNo = headset.getCurrentRowNo();
	  }
	  bConfirmReset = true;
	  
       }
      //Bug Id 69579, End
   }


   private void completeResetBriefcase()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.markSelectedRows("Reset_Briefcase__");
         mgr.submit(trans);
      }
      else
      {
         int current_row = headset.getCurrentRowNo();
         String brief_no = headset.getValue("BRIEFCASE_NO");
         headset.markRow("Reset_Briefcase__");
         mgr.submit(trans);

         //
         // A separate transaction is needed to repopulate the brifcase issues.
         // This ensures that "Reset_Briefcase__" is executed first before
         // repopulating
         //

         trans.clear();
         ASPQuery q = trans.addEmptyQuery(itemblk0); //Bug Id 67519
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",brief_no);
         //bug 58216 ends
         q.includeMeta("ALL");
         mgr.submit(trans);
         headset.goTo(current_row);
      }
   }


   public void unlockDocument()
   {
      ASPManager mgr = getASPManager();
      //Bug Id 48046, Start
      String sFile_State = null;
      ASPTransactionBuffer FileStateTans = mgr.newASPTransactionBuffer();
      //Bug Id 48046, End

      if (itemlay0.isMultirowLayout() && itemset0.countSelectedRows() == 0)
      {
         mgr.showAlert("DOCMAWDOCBCNOROWSSELECTED: No documents selected to Unlock.");
         return;
      }

      trans.clear();
      int count = 0;
      itemset0.setFilterOn();
      itemset0.first();

      do
      {
         // Accepted documents are already unlocked

         if (sDocBcIssueAccepted.equals(itemset0.getRow().getValue("STATE")))
         {
            mgr.showAlert("DOCMAWDOCBCNOUNLOCKACCEPTED: You cannot unlock an Accepted document.");
            itemset0.setFilterOff();
            return;
         }
         //Bug Id 48046, Start
         if (sBcCreated.equals(headset.getValue("STATE")))
         {
            if (itemset0.getRow().getValue("FILEEXIST").equals("1"))
            {
               FileStateTans.clear();
               cmd = FileStateTans.addCustomFunction("ISSUEFILESTATE","Edm_File_API.Get_Document_State","FILE_STATE");
               cmd.addParameter("DOC_CLASS", itemset0.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", itemset0.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", itemset0.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", itemset0.getRow().getValue("DOC_REV"));
               cmd.addParameter("DUMMY1", "ORIGINAL");

               FileStateTans = mgr.perform(FileStateTans);
               sFile_State = FileStateTans.getValue("ISSUEFILESTATE/DATA/FILE_STATE");
            }
         }
         //Bug Id 48046, End
         //Bug Id 48046, Changed the if condition. If the file is already checked out to the briefcase then state machine doesn't support 'unlock'
         if (sBcCreated.equals(headset.getValue("STATE")) && (!sEdmFileCheckedOutToBc.equals(sFile_State)))
         {
            if (itemset0.getRow().getValue("FILEEXIST").equals("1"))
            {
               cmd = trans.addCustomCommand("SETFILESTATEALL" + count++,"Edm_File_API.Set_File_State_All");
               cmd.addParameter("DOC_CLASS", itemset0.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", itemset0.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", itemset0.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", itemset0.getRow().getValue("DOC_REV"));
               cmd.addParameter("IN_STR", "Unlock");
               cmd.addParameter("IN_STR", "");
            }
         }
         //Bug Id 48046, Changed the else if condition. If the file is already checked out to the briefcase then 'UnlockExported' should be used.
         else if (sBcExported.equals(headset.getValue("STATE")) || sBcImported.equals(headset.getValue("STATE")) || (sBcCreated.equals(headset.getValue("STATE")) && (sEdmFileCheckedOutToBc.equals(sFile_State))))
         {
            if (itemset0.getRow().getValue("FILEEXIST").equals("1"))
            {
               cmd = trans.addCustomCommand("SETFILESTATEALL"+ count++,"Edm_File_API.Set_File_State_All");
               cmd.addParameter("DOC_CLASS", itemset0.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", itemset0.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", itemset0.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", itemset0.getRow().getValue("DOC_REV"));
               cmd.addParameter("IN_STR", "UnlockExported");
               cmd.addParameter("IN_STR", "");
            }
         }
         else if (sBcUnderImport.equals(headset.getValue("STATE")))
         {
            if (itemset0.getRow().getValue("FILEEXIST").equals("1"))
            {
               cmd = trans.addCustomCommand("SETFILESTATEALL"+ count++,"Edm_File_API.Set_File_State_All");
               cmd.addParameter("DOC_CLASS", itemset0.getRow().getValue("DOC_CLASS"));
               cmd.addParameter("DOC_NO", itemset0.getRow().getValue("DOC_NO"));
               cmd.addParameter("DOC_SHEET", itemset0.getRow().getValue("DOC_SHEET"));
               cmd.addParameter("DOC_REV", itemset0.getRow().getValue("DOC_REV"));
               cmd.addParameter("IN_STR", "UnlockExported");
               cmd.addParameter("IN_STR", "");
            }

            //Bug Id 45106, Start
	    //Bug ID 45944,inoslk,start
            q = trans.addQuery("BCUNPACKED", "DOC_BRIEFCASE_UNPACKED", "OBJID,OBJVERSION","BRIEFCASE_NO=? AND DOC_CLASS=? AND DOC_NO=? AND DOC_SHEET=? AND DOC_REVISION=?","");
	    q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
	    q.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
	    q.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
	    q.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
	    q.addParameter("DOC_REVISION",itemset0.getRow().getValue("DOC_REV"));
	    //Bug ID 45944,inoslk,end
            trans = mgr.perform(trans);

            String sUnpackedObjId      = trans.getValue("BCUNPACKED/DATA/OBJID");
            String sUnpackedObjVersion = trans.getValue("BCUNPACKED/DATA/OBJVERSION");
            trans.clear();
            //Bug Id 45606, Added if condition to see whether there are a row in DOC_BRIEFCASE_UNPACKED to remove
            if (!(mgr.isEmpty(sUnpackedObjId) && mgr.isEmpty(sUnpackedObjVersion)))
            {
               //remove the record from the temporary table
               cmd = trans.addCustomCommand("DOCBCUNPACKEDREMOVE","DOC_BRIEFCASE_UNPACKED_API.Remove__");
               cmd.addParameter("DUMMY1");
               cmd.addParameter("OBJID",sUnpackedObjId);
               cmd.addParameter("OBJVERSION",sUnpackedObjVersion);
               cmd.addParameter("IN_STR","DO");
               //Bug Id 45106, End
            }
         }
         itemset0.setRemoved();
      } while (itemset0.next());

      // Bug ID 47907, inoslk, start
      if (sBcUnderImport.equals(headset.getValue("STATE")))
      {
	 String data_locked = mgr.isEmpty(headset.getValue("IMPORTED_DATA_LOCKED_DB"))?"N":headset.getValue("IMPORTED_DATA_LOCKED_DB");
	 if (data_locked.equals("N"))
	 {
	    cmd = trans.addCustomCommand("SETBCLOCKED","DOC_BRIEFCASE_API.Set_Imported_Bc_Locked_");
	    cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
	 }
      }
      // Bug ID 47907, inoslk, end

      int row_no = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(row_no); //Bug Id 45606, go to the correct record
      itemset0.setFilterOff();
      itemset0.unselectRows();

      //Bug Id 45106 - Start
      if (sBcUnderImport.equals(headset.getValue("STATE")))
      {
          //Bug Id 45106 - Check whether there are any more lines in the DOC_BRIEFCASE_UNPACKED under same breifcase number before importing the briefcase.
         //Bug Id 45606, Start
         trans.clear();
         cmd = trans.addCustomFunction("BCUNPACKEXISTS","Doc_Briefcase_Unpacked_API.More_Docs_Pending","DUMMY1");
         cmd.addParameter("BRIEFCASE_NO",headset.getRow().getValue("BRIEFCASE_NO"));
         trans = mgr.perform(trans);
         //Bug Id 45606, End
         String sBreifcaseLine      = trans.getValue("BCUNPACKEXISTS/DATA/DUMMY1");
         trans.clear();

         if ("FALSE".equals(sBreifcaseLine)) //Bug Id 45606
         {
            trans.clear();
            cmd = trans.addCustomCommand("SETBRIEFCASESTATE","Doc_Briefcase_API.Set_Briefcase_State");
            cmd.addParameter("BRIEFCASE_NO",headset.getRow().getValue("BRIEFCASE_NO"));
            cmd.addParameter("IN_1","FinishImport");
            trans = mgr.perform(trans); //Bug Id 45606
         }
      }
      //Bug Id 45106 - End
      headset.refreshRow();
   }

   public void unlockDocumentHyperLink()
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
         itemset0.selectRow();

      bUnlock_multi_row = true;
   }


   public void unlockDocumentFromCommandBar()
   {
      if (itemlay0.isMultirowLayout())
         itemset0.storeSelections();
      else
      {
         itemset0.unselectRows();
         itemset0.selectRow();
      }

      bUnlock_current_row = true;
   }

   public void deleteBriefcase_client()
   {
      bDelete_current_row = true;
      headset.storeSelections();
   }


   public void goToDocIssue()
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isMultirowLayout())
      {
         itemset0.storeSelections();
         itemset0.setFilterOn();
      }
      else
      {
         itemset0.selectRow();
      }
      keys = itemset0.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      mgr.transferDataTo("DocIssue.page",keys);
   }


   public void transferToDocBriefcaseCompare()
   {
      ASPManager mgr = getASPManager();
      keys = headset.getSelectedRows("BRIEFCASE_NO");
      mgr.transferDataTo("DocBriefcaseCompare.page", keys);
   }

   //Bug Id 65509, Start
   public void checkObject()
   {
      ASPManager mgr = getASPManager();
      String bc_no = headset.getValue("BRIEFCASE_NO"); 
      String sMessage;
      trans.clear();
      
      cmd = trans.addCustomCommand("CHECKPLANTOBJECT","Doc_Briefcase_Unpacked_API.Check_Plant_Object");
      cmd.addParameter("OUT_1");
      cmd.addParameter("OUT_2");
      cmd.addParameter("IN_1",bc_no);
      cmd.addParameter("IN_1",null); 
      cmd.addParameter("IN_1","Import");
      
      cmd = trans.addCustomCommand("CHECKOBJECT","Doc_Briefcase_Unpacked_API.Check_Object");
      cmd.addParameter("OUT_3");         
      cmd.addParameter("IN_1",bc_no);
      cmd.addParameter("IN_1",null); 
      cmd.addParameter("IN_1","Import");
            
      trans = mgr.perform(trans);
      
      String sWarnNullMessage = trans.getValue("CHECKPLANTOBJECT/DATA/OUT_1");
      String sWarnMultipleMessage = trans.getValue("CHECKPLANTOBJECT/DATA/OUT_2");
      String sWarnObjNullMessage = trans.getValue("CHECKOBJECT/DATA/OUT_3");

      String sWarnNull = mgr.translate("DOCMAWDOCBCPLANTOBJNULL: There is no matching object for the following Object Id(s)") +": \\n";
      if (!mgr.isEmpty(sWarnNullMessage)) {
         sCheckObjectMessage = sWarnNull + sWarnNullMessage;
      }
      if (!mgr.isEmpty(sWarnObjNullMessage)) {
          if (mgr.isEmpty(sWarnNullMessage)) 
             sCheckObjectMessage = sWarnNull + sWarnObjNullMessage;
          else
             sCheckObjectMessage = sCheckObjectMessage + ", " + sWarnObjNullMessage;             
      }      
      if (!mgr.isEmpty(sWarnMultipleMessage)) {
          String sWarnMul = mgr.translate("DOCMAWDOCBCPLANTOBJMULTIPLE: There are too many matching objects for the following Object Id(s) (only unique objects can be handled)") +": \\n"+ sWarnMultipleMessage;
          if (mgr.isEmpty(sCheckObjectMessage)) 
             sCheckObjectMessage = sWarnMul;
          else
             sCheckObjectMessage = sCheckObjectMessage + "\\n\\n" + sWarnMul;              
      }

      if (!mgr.isEmpty(sCheckObjectMessage)) {
          bCheckObjectConfirm = true;
      }
      bObjectChecked = true;
   } 
   //Bug Id 65509, End

   public void completeExportBriefcase()
   {
      ASPManager mgr = getASPManager();
      int current_row = headset.getCurrentRowNo();

      if (headlay.isMultirowLayout())
         headset.markSelectedRows("Export_Briefcase__");
      else
      {
         headset.markRow("Export_Briefcase__");

         trans.clear();
         ASPQuery q = trans.addEmptyQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
         //bug 58216 ends
         q.includeMeta("ALL");
      }
      mgr.submit(trans);
      headset.goTo(current_row);
   }


   public void exportBriefcase()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer act_buf = mgr.newASPBuffer();
      int row_no = 0;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         row_no = headset.getRowSelected();
         headset.setFilterOff();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      // Populate briefcase issues
      if (headlay.isMultirowLayout())
      {
         trans.clear();
         q = trans.addEmptyQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
         //bug 58216 ends
         q.includeMeta("ALL");
         mgr.submit(trans);
         headset.goTo(row_no);
      }

      if (itemset0.countRows() == 0)
         mgr.showAlert("DOCMAWDOCBCNODOCSTOEXPORT: Briefcase must have at least one document issue to be exported!");
      else
      {
         strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();

         act_buf.addItem("BRIEF_NO", headset.getValue("BRIEFCASE_NO"));
         act_buf.addItem("ACTION", "EXPORT_BRIEFCASE");

         transferToEdmBriefcase(act_buf);
      }
   }


   public void importBriefcase() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPBuffer act_buf = mgr.newASPBuffer(); //Bug Id 56784

      strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();
      int row = 0;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         row = headset.getRowSelected();
         headset.setFilterOff();

         // populate the briefcase issues
         // depending on the selected briefcase
         trans.clear();
         q = trans.addEmptyQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",headset.getRow().getValue("BRIEFCASE_NO"));
         //bug 58216 ends
         q.includeMeta("ALL");
         mgr.submit(trans);
         headset.goTo(row);
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      String sDocBcIssueFailed = DocmawConstants.getConstantHolder(mgr).doc_briefcase_issue_failed;

      if (sBcExported.equals(headset.getRow().getValue("STATE")))
      {
         int row_count = itemset0.countRows();
         itemset0.first();

         for (int x = 0; x < row_count; x++)
         {
            if (sDocBcIssueFailed.equals(itemset0.getRow().getValue("STATE")))
            {
               mgr.showAlert("DOCMAWDOCBRIEFCASEIMPORTFAILED: One or more documents in this briefcase have action Failed. You must unlock them before importing.");
               return;
            }
            itemset0.next();
         }

         //Bug Id 56784, Start
	 act_buf.addItem("BRIEF_NO", headset.getValue("BRIEFCASE_NO"));
         act_buf.addItem("ACTION", "IMPORT_BRIEFCASE");

         transferToEdmBriefcase(act_buf);
	 //Bug Id 56784, End
      }
      // Bug ID 47907, inoslk, Removed code to transfer to the Compare page
   }

   // Bug ID 47907, inoslk, start
   public void gotoImportComparePage()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
	 headset.storeSelections();
      else
      {
	 headset.unselectRows();
	 headset.selectRow();
      }

      keys = headset.getSelectedRows("BRIEFCASE_NO");
      mgr.transferDataTo("DocBriefcaseCompare.page", keys);
   }

   public void undoImport()
   {
      ASPManager mgr = getASPManager();
      String sBcIsLocked = "";

      if (headlay.isMultirowLayout())
      {
	 headset.storeSelections();
	 headset.setFilterOn();
	 iRowNo = headset.getRowSelected();
	 headset.setFilterOff();
      }
      else
      {
	 headset.unselectRows();
	 headset.selectRow();
	 iRowNo = headset.getCurrentRowNo();
      }

      trans.clear();
      cmd = trans.addCustomFunction("GETBCLOCKEDSTATE","Doc_Briefcase_API.Is_Briefcase_Locked_","OUT_1");
      cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));

      trans = mgr.perform(trans);
      sBcIsLocked = trans.getValue("GETBCLOCKEDSTATE/DATA/OUT_1");

      if (sBcIsLocked.equals("TRUE"))
	 mgr.showAlert(mgr.translate("DOCMAWDOCBRIEFCASEBCLOCKED: This operation is not available once the document(s) under import are Accepted, Rejected or Unlocked."));
      else
         bGetConfirmation = true;
   }

   public void completeUndoExport()
   {
      ASPManager mgr = getASPManager();

      headset.goTo(iRowNo);

      trans.clear();

      cmd = trans.addCustomCommand("ROLLBACKIMPORT","Doc_Briefcase_API.Set_Briefcase_State");
      cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
      cmd.addParameter("IN_1","RollbackImport");

      mgr.perform(trans);

      headset.refreshRow();
   }
   // Bug ID 47907, inoslk, end


   private void transferToEdmBriefcase(ASPBuffer act_buf)
   {
      ASPManager mgr = getASPManager();
      ASPBuffer doc_buf;

      if (act_buf.getValue("ACTION").equals("EXPORTREJECTED"))
      {
         int count = 0;

         trans.clear();
         //Bug ID 48028, inoslk, start
         if (itemset0.countRows() > 0)
         {
            itemset0.first();

            do
            {
               if (itemset0.getRow().getValue("STATE").equals(sDocBcIssueRejected))
               {
                  itemset0.selectRow();
                  cmd = trans.addCustomCommand("MOVEREJECTED" + count++,"Doc_Briefcase_Issue_API.Move_Rejected_To_New_Bc");
                  cmd.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
                  cmd.addParameter("DOC_CLASS",itemset0.getRow().getValue("DOC_CLASS"));
                  cmd.addParameter("DOC_NO",itemset0.getRow().getValue("DOC_NO"));
                  cmd.addParameter("DOC_SHEET",itemset0.getRow().getValue("DOC_SHEET"));
                  cmd.addParameter("DOC_REV",itemset0.getRow().getValue("DOC_REV"));
                  cmd.addParameter("BRIEFCASE_NO",act_buf.getValue("BRIEF_NO"));
               }
            } while (itemset0.next());
         }
         //Bug ID 48028, inoslk, end

         cmd = trans.addCustomCommand("EXPORTBC", "Doc_Briefcase_API.Set_Briefcase_State");
         cmd.addParameter("BRIEFCASE_NO",act_buf.getValue("BRIEF_NO"));
         cmd.addParameter("STATE","ExportBriefcase");

         mgr.perform(trans);

	 // Bug ID 47171, inoslk, start
	 okFindUnpacked();
         doc_buf = unpackedset.getRows("LINE_NO,DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REVISION,DOC_TITLE");
	 // Bug ID 47171, inoslk, end
         itemset0.unselectRows();
      }
      else
      {
         // Bug ID 50158, Start 
         trans.clear();
         q = trans.addEmptyQuery(itemblk0); //Bug Id 67519
         q.setSelectList("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,Doc_Title_API.Get_Title(DOC_CLASS, DOC_NO) DOC_TITLE");
         //bug 58216 starts
         q.addWhereCondition("BRIEFCASE_NO = ?");
         q.addParameter("BRIEFCASE_NO",headset.getValue("BRIEFCASE_NO"));
         //bug 58216 ends
         q.includeMeta("ALL");
         q.setBufferSize(itemset0.countDbRows());
         trans = mgr.perform(trans);

         doc_buf = trans.getBuffer("ITEM0");

         // doc_buf = itemset0.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,DOC_TITLE");
         // Bug ID 50158, End
      }

      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmBriefcase.page", act_buf, doc_buf);
      request_url = true;
   }


  
   public void exportRejectedDocs()
   {
      ASPManager mgr = getASPManager();

      String sRejectedDocsExist = ""; // Bug ID 47171, inoslk
      String sCurrentBc = "";

      //Bug ID 48028, inoslk, start
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         sCurrentBc = headset.getValue("BRIEFCASE_NO");
         iRowNo = headset.getRowSelected();
         headset.setFilterOff();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
         sCurrentBc = headset.getValue("BRIEFCASE_NO");
         iRowNo = headset.getCurrentRowNo();
      }
      //Bug ID 48028, inoslk, end

      String sRejectedBc = getRejectedBcName(sCurrentBc);

      //Bug ID 47171, inoslk, start
      trans.clear();
      cmd = trans.addCustomFunction("REJECTEDDOCSEXIST","Doc_Briefcase_Unpacked_API.Rejected_Docs_Exist_","OUT_1");
      cmd.addParameter("BRIEFCASE_NO",sCurrentBc);

      trans = mgr.perform(trans);
      sRejectedDocsExist = trans.getValue("REJECTEDDOCSEXIST/DATA/OUT_1");
      //Bug ID 47171, inoslk, end

      if (sRejectedDocsExist.equals("FALSE")) // Bug ID 47171, inoslk
      {
         mgr.showAlert("DOCMAWDOCBCNOREJECTEDDOCS: No rejected documents in this briefcase to be exported");
         return;
      }

      strIFSCliMgrOCX = DocmawUtil.getClientMgrObjectStr();

      createNewBc(sRejectedBc);

      ASPBuffer act_buf = mgr.newASPBuffer();
      act_buf.addItem("BRIEF_NO", sRejectedBc);
      act_buf.addItem("CURRENT_BRIEF_NO", sCurrentBc);   //Bug Id 45197
      act_buf.addItem("ACTION", "EXPORTREJECTED");

      transferToEdmBriefcase(act_buf);
   }

   // Constructs the BC Name for the rejected documents
   private String getRejectedBcName(String sCurrentBcName)
   {
      if (sCurrentBcName.indexOf("_R") != -1)
      {
         int i = sCurrentBcName.indexOf("_R") + 2;
         String sub = sCurrentBcName.substring(0,i);

         int count = Integer.parseInt(sCurrentBcName.substring(i));
         count++;
         return sub + Integer.toString(count);
      }
      else
         return sCurrentBcName + "_R1";
   }

   private void createNewBc(String sBcName)
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomCommand("CREATENEWBC","Doc_Briefcase_API.Create_New_Briefcase");
      cmd.addParameter("IN_1",sBcName);
      cmd.addParameter("IN_1",headset.getValue("DESCRIPTION")); // Bug Id 92560
      cmd.addParameter("IN_1","true");//Bug id 48271, karalk

      mgr.perform(trans);
   }

   public void  preDefine()
   {

      ASPManager mgr = getASPManager();
      disableConfiguration();

      //
      // Document Briefcases
      //

      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();



      //Bug Id 45851, Reduce the MaxLength to 10.
      headblk.addField("BRIEFCASE_NO").
      setSize(20).
      setMaxLength(10).
      setUpperCase().
      setInsertable().
      setReadOnly().
      setMandatory().
      setDynamicLOV("DOC_BRIEFCASE").
      setLabel("DOCMAWDOCBRIEFCASEHEADBRIEFCASENO: Briefcase No");

      headblk.addField("DESCRIPTION").
      setSize(20).
      setMaxLength(50).
      setInsertable().
      setMandatory().
      setLabel("DOCMAWDOCBRIEFCASEHEADDESCRIPTION: Description");

      headblk.addField("CREATED_BY").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setUpperCase().
      setDynamicLOV("PERSON_INFO_LOV").
      setLabel("DOCMAWDOCBRIEFCASEHEADCREATEDBY: Created By");

      headblk.addField("RECEIVING_COMPANY").
      setSize(20).
      setMaxLength(30).
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADRECEIVINGCOMPANY: Receiving Company");

      headblk.addField("RECEIVING_PERSON").
      setSize(20).
      setMaxLength(30).
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADRECEIVINGPERSON: Receiving Person");

      headblk.addField("LATEST_RETURN_DATE", "Date").
      setSize(20).
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADLATESTRETURNDATE: Latest Return Date");

      headblk.addField("CREATED_DATE", "Date").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADCREATEDDATE: Created Date");

      //Bug Id 49067 added Lov for state field  Start.
      headblk.addField("STATE").
      setSize(20).
      setMaxLength(253).
      setSelectBox().
      enumerateValues("DOC_BRIEFCASE_API.Enumerate_States__","DOC_BRIEFCASE_API.Finite_State_Encode__" ).
      setReadOnly().
      setLabel("DOCMAWDOCBRIEFCASEHEADSTATE: Status");
      //Bug Id 49067 End.

      headblk.addField("EXPORT_DATE", "Date").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADEXPORTDATE: Export Date");

      headblk.addField("IMPORT_DATE", "Date").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCMAWDOCBRIEFCASEHEADIMPORTDATE: Import Date");

      headblk.addField("CAN_DELETE_THIS").
      setHidden().
      setFunction("DOC_BRIEFCASE_API.Can_Delete_The_Briefcase(BRIEFCASE_NO)");

      //Bug ID 47907, inoslk, start
      headblk.addField("IMPORTED_DATA_LOCKED_DB")
      .setHidden();
      //Bug ID 47907, inoslk, end

      //Bug ID 48028, inoslk, start
      headblk.addField("CAN_GO_TO_COMPARE_PAGE")
      .setHidden()
      .setFunction("Doc_Briefcase_API.Can_Go_To_Compare_Page_(BRIEFCASE_NO)");

      headblk.addField("CAN_RE_EXPORT")
      .setHidden()
      .setFunction("Doc_Briefcase_API.Can_Re_Export_Bc_(BRIEFCASE_NO)");
      //Bug ID 48028, inoslk, end

      headblk.setView("DOC_BRIEFCASE");
      headblk.defineCommand("DOC_BRIEFCASE_API", "New__, Modify__,Remove__,Export_Briefcase__,Reset_Briefcase__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.defineCommand(headbar.OKFIND,"okFind");
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      headbar.defineCommand(headbar.DELETE,"deleteBriefcase");

      headbar.addSecureCustomCommand("deleteBriefcase_client",mgr.translate("DOCMAWDOCBRIEFCASEDELETEBC: Delete"),"DOC_BRIEFCASE_API.Remove__"); //Bug Id 70286


      headbar.addSecureCustomCommand("addDocuments", mgr.translate("DOCMAWDOCBRIEFCASEADDDOCS: Add Documents..."),"DOC_BRIEFCASE_ISSUE_API.New__"); //Bug Id 70286
      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("exportBriefcase", mgr.translate("DOCMAWDOCBRIEFCASEEXPORTBC: Export Documents..."),"DOC_BRIEFCASE_API.Export_Briefcase__"); //Bug Id 70286
      headbar.addSecureCustomCommand("importBriefcase", mgr.translate("DOCMAWDOCBRIEFCASEIMPORTBC: Import Documents..."),"Doc_Briefcase_API.Set_Briefcase_State"); //Bug Id 70286
      headbar.addSecureCustomCommand("resetBriefcase", mgr.translate("DOCMAWDOCBRIEFCASERESETBC: Reset Briefcase"),"DOC_BRIEFCASE_API.Reset_Briefcase__"); //Bug Id 70286
      headbar.addSecureCustomCommand("exportRejectedDocs", mgr.translate("DOCMAWDOCBRIEFCASEEXPORTREJECTED: Export Rejected Documents"),"Doc_Briefcase_API.Create_New_Briefcase"); //Bug Id 70286
      headbar.addSecureCustomCommand("deleteLocalFiles",mgr.translate("DOCMAWDOCBRIEFCASEDELETEBCFILES: Delete Briefcase and Local Files"),"DOC_BRIEFCASE_API.Remove__"); //Bug Id 70286
      // Bug ID 47907, inoslk, start
      headbar.addSecureCustomCommand("undoImport", mgr.translate("DOCMAWDOCBRIEFCASEUNDOIMPORT: Undo Import"),"Doc_Briefcase_API.Set_Briefcase_State"); //Bug Id 70286
      headbar.addCustomCommand("gotoImportComparePage", mgr.translate("DOCMAWDOCBRIEFCASETRANSFERTOCOMPAREPAGE: Go to Import and Compare..."));
      // Bug ID 47907, inoslk, end

      headbar.disableMultirowAction();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCBRIEFCASEOVWDOCCRIEDCASE: Overview - Document Briefcases"));
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);



      //
      // Document Briefcase Issues
      //

      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.disableDocMan();

      itemblk0.addField("ITEM0_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk0.addField("ITEM0_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk0.addField("ITEM0_BRIEFCASE_NO").
      setDbName("BRIEFCASE_NO").
      setHidden();

      itemblk0.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setMandatory().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_REV,DOC_SHEET","FILEEXIST").
      setLabel("DOCMAWDOCBRIEFCASEITEM0DOCCLASS: Doc Class");

      itemblk0.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().
      setMandatory().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_REV,DOC_SHEET","DOC_CLASS,DOC_TITLE,FILEEXIST").
      //setCustomValidation("DOC_NO","DOC_CLASS,DOC_TITLE").
      setLabel("DOCMAWDOCBRIEFCASEITEM0DOCNO: Doc No");

      itemblk0.addField("DOC_SHEET").
      setSize(20).
      setMaxLength(10).
      setUpperCase().
      setMandatory().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_REV,DOC_SHEET","FILEEXIST").
      setLabel("DOCMAWDOCBRIEFCASEITEM0DOCSHEET: Doc Sheet");

      itemblk0.addField("DOC_REV").
      setSize(20).
      setMaxLength(6).
      setUpperCase().
      setMandatory().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_REV,DOC_SHEET","FILEEXIST").
      setLabel("DOCMAWDOCBRIEFCASEITEM0DOCREV: Doc Rev");

      itemblk0.addField("DOC_TITLE").
      setSize(20).
      setMaxLength(250).
      setReadOnly().
      setFunction("Doc_Title_API.Get_Title(DOC_CLASS, DOC_NO)").
      setLabel("DOCMAWDOCBRIEFCASEITEM0DOCTITLE: Title");

      itemblk0.addField("ITEM0_STATE").
      setDbName("STATE").
      setSize(20).
      setMaxLength(250).
      setReadOnly().
      setLabel("DOCMAWDOCBRIEFCASEITEM0ACTION: Action");

      itemblk0.addField("FILEEXIST").
      setCheckBox("0,1").
      setReadOnly().
      setInsertable().
      setValidateFunction("file_exist_onclick").
      setFunction("DECODE(EDM_FILE_API.GET_DOCUMENT_STATE(DOC_CLASS, DOC_NO, DOC_SHEET, DOC_REV, 'ORIGINAL'),NULL,'0','1')").
      setLabel("DOCMAWDOCBRIEFCASEITEM0FILEEXIST: File Exist");

      itemblk0.setView("DOC_BRIEFCASE_ISSUE");
      itemblk0.defineCommand("DOC_BRIEFCASE_ISSUE_API","New__,Modify__,Remove__,Reset__");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturn");
      itembar0.defineCommand(itembar0.SAVENEW,"saveNew");

      itembar0.disableCommand(itembar0.DELETE);
      itembar0.disableCommand(itembar0.EDITROW);

      // Custom commands
      itembar0.addSecureCustomCommand("unlockDocumentFromCommandBar", mgr.translate("DOCMAWDOCBRIEFCASEUNLOCKDOC: Unlock Document"),"Edm_File_API.Set_File_State_All"); //Bug Id 70286
      itembar0.addCustomCommand("goToDocIssue", mgr.translate("DOCMAWDOCBRIEFCASEDOCUMENTISSUE: Document Info..."));

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCBRIEFCASEITEM0TITLE: Briefcase Issues"));
      itemtbl0.enableRowSelect();

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);


      //
      // Format(Type) fields for db transactions
      //

      paramblk = mgr.newASPBlock("PARAMS");
      paramblk.addField("IN_STR");
      paramblk.addField("IN_DATE", "Date");
      paramblk.addField("IN_NUMBER", "Number");
      paramblk.addField("OUT_1");
      paramblk.addField("OUT_2");
      paramblk.addField("OUT_3");
      paramblk.addField("IN_1");
      paramblk.addField("DUMMY1");
      //Bug ID 45944, inoslk, start
      paramblk.addField("DOC_REVISION");
      //Bug ID 45944, inoslk, end
      paramblk.addField("FILE_STATE"); //Bug Id 48046

      // Bug ID 47171, inoslk, start
      unpackedblk = mgr.newASPBlock("UNPACKED");

      unpackedblk.addField("UNPACKED_BRIEFCASE_NO")
      .setDbName("BRIEFCASE_NO")
      .setHidden();

      unpackedblk.addField("LINE_NO")
      .setHidden();

      unpackedblk.addField("UNPACKED_DOC_CLASS")
      .setDbName("DOC_CLASS")
      .setHidden();

      unpackedblk.addField("UNPACKED_DOC_NO")
      .setDbName("DOC_NO")
      .setHidden();

      unpackedblk.addField("UNPACKED_DOC_SHEET")
      .setDbName("DOC_SHEET")
      .setHidden();

      unpackedblk.addField("UNPACKED_DOC_REV")
      .setDbName("DOC_REVISION")
      .setHidden();

      unpackedblk.addField("UNPACKED_DOC_TITLE")
      .setDbName("DOC_TITLE")
      .setHidden();

      unpackedblk.setView("DOC_BRIEFCASE_UNPACKED");
      unpackedset = unpackedblk.getASPRowSet();
      // Bug ID 47171, inoslk, end
   }


   protected String getDescription()
   {
      return "DOCMAWDOCBRIEFCASETITLE: Document Briefcase";
   }


   protected String getTitle()
   {
      return getDescription();
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCBRIEFCASETITLE: Document Briefcase"));
      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"BRIEF_ROOT_PATH\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTION\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"MULTIROWACTION\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"UNLOCK_CURRENTROW\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DELETE_CURRENTROW\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DELETE_LOCALFILE\" value=\"\">\n");  //Bug Id 45547
      // Bug Id 47907, inoslk, start
      out.append("<input type=\"hidden\" name=\"UNDO_IMPORT\" value=\"\">\n");
      // Bug Id 47907, inoslk, end
      out.append("<input type=\"hidden\" name=\"RESET_BRIEFCASE\" value=\"\">\n");//Bug Id 69579
      out.append(mgr.startPresentation("DOCMAWDOCBRIEFCASETITLE: Document Briefcase"));
      out.append(headlay.show());

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         if (itemlay0.isMultirowLayout())
         {
            out.append(itembar0.showBar());
            out.append(itemlay0.generateDataPresentation());
         }
         else
         {
            out.append(itemlay0.show());
         }
      }

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append(strIFSCliMgrOCX);
      out.append("</body>\n");
      out.append("</html>\n");

      if (request_url)
      {
          //Bug id 48271, karalk Start,
         if(IsRejectedBc1){

            appendDirtyJavaScript("    if (confirm('");
            appendDirtyJavaScript(mgr.translate("DOCMAWDOCBRIEFCASEREMOVE: This command will remove all local files in the exported rejected Briefcase.  Press Ok to continue"));
            appendDirtyJavaScript("'))\n");
            appendDirtyJavaScript("validateBriefcaseRootPath();\n");

            IsRejectedBc1=false;

         }else{

             appendDirtyJavaScript("validateBriefcaseRootPath();\n");
         }
	 //Bug id 48271, End.
      }

      appendDirtyJavaScript("function launchEdmBriefcase() {\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(url));
      //Bug ID 47986, inoslk, Changed the window name to 'edmBcWindow' so that it does not launch in the same window as the edm macro page
      appendDirtyJavaScript("\",\"edmBcWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateBriefcaseRootPath() {\n");
      appendDirtyJavaScript("  var root_path, path_valid;\n");
      appendDirtyJavaScript("  root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
      appendDirtyJavaScript("  if (root_path != \"\")\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      path_valid = oCliMgr.FolderExists(root_path);\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  if(root_path == \"\" || !path_valid)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.translate("DOCMAWEDMBRIEFCASEROOTPATHNOTEXIST: Briefcase Root Path is either invalid or does not exist. Press Ok to choose a path")));
      appendDirtyJavaScript(      "\");\n");
      appendDirtyJavaScript("     root_path = oCliMgr.BrowseForFolder(\"" + mgr.translate("DOCMAWDOCBRIEFCASECHOOSEBRIEFCASEROOTPATH: Select briefcase root path:") +"\");\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  if (root_path != \"\")\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\", root_path);\n");
      appendDirtyJavaScript("     launchEdmBriefcase();\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     alert(\"");
      appendDirtyJavaScript(        correctDoubleQuotation(mgr.translate("DOCMAWEDMBRIEFCASEINVALIDROOTPATH: A valid path was not chosen.")));
      appendDirtyJavaScript(      "\");\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function perform() {\n");
      appendDirtyJavaScript("  oCliMgr.RegSetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\", document.form.BRIEF_ROOT_PATH.value);\n");
      appendDirtyJavaScript("  launchEdmBriefcase();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refreshPage(message)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.ACTION.value = message;");
      appendDirtyJavaScript("   submit();");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function unlockDocumentMulti()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.MULTIROWACTION.value = 'unlockDocumentHyperLink();';");
      appendDirtyJavaScript("   submit();");
      appendDirtyJavaScript("}\n");

      if (bUnlock_current_row)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCBRIEFCASEUNLOCKDOCISSUE: Unlock this Document?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNLOCK_CURRENTROW.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNLOCK_CURRENTROW.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");

         bUnlock_current_row = false;
      }

      if (bUnlock_multi_row)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCBRIEFCASEUNLOCKDOCISSUEMULTI: Unlock Documents?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNLOCK_CURRENTROW.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNLOCK_CURRENTROW.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");

         bUnlock_multi_row = false;
      }

      if (bDelete_current_row)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCMAWDOCBRIEFCASECONFIRMDELETE: Delete this Row?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].DELETE_CURRENTROW.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].DELETE_CURRENTROW.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");

         bDelete_current_row = false;
      }

      //Bug Id 45547, Start
      if (bDelete_Local_Files)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCBRIEFCASEDELETEFILES: WARNING - You are about to delete the briefcase and its associated files. This action cannot be reversed. Are you sure you want to continue?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].DELETE_LOCALFILE.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].DELETE_LOCALFILE.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");

         bDelete_Local_Files = false;
      }

      if(bDelete_Briefcase_Files)
      {
          //Bug Id 48298, Added varible sImportedPath
          appendDirtyJavaScript(" var sImportedPath = \"" + mgr.encodeStringForJavascript(sImportedPath) + "\";\n");
          appendDirtyJavaScript(" oCliMgr.DeleteBriefcaseFolder(sImportedPath);\n");
      }
      //Bug Id 45547, End

      // Bug Id 47907, inoslk, start
      if (bGetConfirmation)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCMAWDOCBCUNDOWARNING: WARNING - You are about to undo importing the briefcase. Any changes done to imported data will be lost. Are you sure you want to continue?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNDO_IMPORT.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].UNDO_IMPORT.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");
      }
      // Bug Id 47907, inoslk, end

      //Bug Id 69579, Start
      if (bConfirmReset)
      {
         appendDirtyJavaScript("if(confirm(\""+mgr.translate("DOCMAWDOCBCRESETWARNING: WARNING - You are about to reset the briefcase. This will delete the briefcase folder in the briefcase root path. Are you sure you want to continue?")+"\"))\n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].RESET_BRIEFCASE.value= \"YES\"; \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("else \n");
         appendDirtyJavaScript("{ \n");
         appendDirtyJavaScript("   document.forms[0].RESET_BRIEFCASE.value= \"NO\";  \n");
         appendDirtyJavaScript("} \n");
         appendDirtyJavaScript("submit();\n");
      }
      //Bug Id 69579, End   

      if (bObjectChecked) {
          if (bCheckObjectConfirm) {
             appendDirtyJavaScript("alert(\"" + sCheckObjectMessage + "\");\n");
             bCheckObjectConfirm = false;
          }
          appendDirtyJavaScript("refreshPage(\"IMPORT_CHECK_COMPLETE\");\n");
          bObjectChecked = false;
      }
      

      appendDirtyJavaScript("function file_exist_onclick() \n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("document.forms[0].FILEEXIST.checked=!document.forms[0].FILEEXIST.checked;\n");
      appendDirtyJavaScript("} \n");
      return out;
   }

   String correctDoubleQuotation(String str)
   {
      if (str.indexOf("\"")==-1)
      {
         return str;
      }
      else
      {
         String[]  msgVectors = split(str,'"');
         String msg="";
         for (int i=0;i<msgVectors.length;i++)
         {
            if (i==msgVectors.length-1)
               msg+=msgVectors[i];
            else
               msg+=msgVectors[i]+"\\\"";
         }
         return msg;
      }
   }


    private String[] split(String str,char c)
   {
      int length_=howManyOccurance(str,c);
      int strLength=str.length();
      int occurance=0;
      int index=0;

      String[] tempString= new String[length_+1];
      while (strLength>0)
      {
         occurance=str.indexOf(c);
         if (occurance==-1)
         {
            tempString[index]=str;
            break;
         }
         else
         {
            tempString[index++]=str.substring(0,occurance);
            str=str.substring(occurance+1,strLength);
            strLength=str.length();
         }
      }
      return tempString;
   }


    public int howManyOccurance(String str,char c)
   {
      int strLength=str.length();
      int occurance=0;
      for (int index=0;index<strLength;index++)
         if (str.charAt(index)==c)
            occurance++;
      return occurance;
   }

}
