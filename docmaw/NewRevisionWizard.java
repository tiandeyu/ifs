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
*  File        : NewRevisionWizard.java
*  Modified    :
*  18/03/2002  Shtolk  Bug Id# 27767  Modifed to copy the file when creating a new revision.
*  20/11/2002  Bakakl  Added doc_sheet to 2002 2 track  and new fields also added.
*  28/11/2002  Bakakl  Extended DocSrv instead of ASPPageProvider in order to avoid calling EdmMacro for
                       copying files.

*  2002-12-18  Nisilk  Fixed call id 92126
*  2003-01-29  Thwilk  Bug Id  29168 wrote a function that alerts can use to replace double quotes, with escaped quotes.
*  2003-03-12  Dikalk  Removed doReset() and clone()
*  2003-04-01  BaKalk  Added clone() and doReset() in order to support 3.5.1 web client.
*  2003-08-06  Shtolk  2003-2 SP4 Merge: Bug Id 29168.
*  2003-08-15  InoSlk  Call ID 100767: Modified methods runQuery(), cancel(), transferToCallingUrl()
*                      and printContents().
*  2003-08-15  Bakalk  Merged the fix of bug:38093
*  2003-08-15  InoSlk  Call ID 100767: Added method close(). Modified methods getContents(), cancel() and
*                      transferToCallingUrl().
*  2003-08-29  InoSlk  Removed unused variables.
*  2003-09-05  InoSlk  Uncommented the submit() call in Javascript function updateAllValues()
*                      so that relevant info is fetched for the selected revision.
*  2003-09-05  NiSilk  Fixed Call ID:102832. Removed javascript method toggleStyleDisplay that was overriden.
*  2003-09-23  Bakalk  Fixed Call ID:103691. Changed the layout of first step.
*  2003-10-01  NiSilk  Fixed call Id 104826. Modified methods getDocRevisionSettings,predefine.
*  2003-10-17  Bakalk  Fixec Call Id:108322. Modified finish to copy parent docs.
*  2003-10-23  Bakalk  Fixec Call Id:108322. Removed the fix hereof.
*  2004-01-05  Bakalk  Web Alingment (Field order) done.
*  2004-03-23  Dikalk  SP1 Merge. Bug Id 41894. Removed the setSize() for 'DOC_OBJECT',
*                      'DOC_OBJECT_KEY' and 'DOC_OBJECT_DESC' to display all characters.
*  2004-06-23  Dikalk  Merged Bug Id 44646.
*  2004-12-02  Bakalk  Now this wizard supports for X-Ref.
*  2004-12-02  Bakalk  Modified lay out in 3rd step. Modified client methods for selection and deselections of rows in 3rd step.
*  2004-12-06  Dikalk  Merged bug 46754
*  2004-12-06  Dikalk  Merged bug 47706
*  2005-01-05  Bakalk  Fixed the call 120922. Modified okFindItem3.
*  2005-01-05  Bakalk  Fixed the call 120923. Modified finish().
*  2005-01-06  Bakalk  Fixed the call 120949. In addition to that, Arranged all layouts in accordance with IFS standards.
*  2005-01-05  Bakalk  Fixed the call 120923. Modified okFindITEM3().
*  2005-10-31  Amnalk  Merged Bug Id 53112.
*  2005-12-12  Dikalk  Added code to set the focus on the NEW_REV field in Step-1
*  2007-04-05  Chselk  Merged bug 63859
*              2007-03-02  THWILK  Bug 63859, Modified method finish().
*  2007-08-13  NaLrlk  XSS Corrections.
*  2007-08-13  AsSalk  Merged Bug 65167, Modified printContents() to disable move of lock objects.
*  2007-08-15  ASSALK  Merged Bug 58526, Modified printContents().
*  2007-11-20  AMNALK  Bug Id 67230, Modified printContents() to limit max length of New Title Revision.
*  2007-11-21  AMNALK  Bug Id 67456, Modified getDocRevisionSettings().
*  2008-07-08  AMNALK  Bug Id 74405, Added maximum length for fields Revision and Revision Text
*  2008-07-15  AMNALK  Bug 69329, Added new functions GetNumberOfObjConnections() & storeRadioButtons(). Made changes to display new page
*		       the number of object connections > 100. 
*  2008-12-31  AMCHLK  Bug Id 78853, Replaced the function call 'Move_Document_Reference' from 'History_Log_For_Move_Obj' 
*  2009-01-15  AMCHLK  Bug Id 78853, increased the buffer size to dispaly up to 100 connected objects at new revision wizard Step 02 
*----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.Vector;
import java.lang.reflect.*;


public class NewRevisionWizard extends DocSrv
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.NewRevisionWizard");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext       ctx;
   private ASPBlock         item1blk;
   private ASPRowSet        item1set;
   private ASPTable         item1tbl;
   private ASPBlockLayout   item1lay;
   private ASPBlock         item2blk;
   private ASPRowSet        item2set;
   private ASPTable         item2tbl;
   private ASPBlockLayout   item2lay;
   //Bug Id 69329, start
   private ASPBlock         item4blk;
   private ASPRowSet        item4set;
   private ASPTable         item4tbl;
   private ASPBlockLayout   item4lay;
   private ASPCommandBar    item4bar;
   //Bug Id 69329, end
   private ASPBlock         item3blk;
   private ASPRowSet        item3set;
   private ASPTable         item3tbl;
   private ASPBlockLayout   item3lay;

   private ASPCommandBar    item1bar;
   private ASPCommandBar    item2bar;
   private ASPCommandBar    item3bar;

   

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPBuffer  buff;
   private ASPCommand cmd;
   private ASPQuery   q;
   private ASPLog     log;

   private String sFilePath;
   private String callingUrl;
   private String doc_class;
   private String doc_no;
   private String doc_sheet;
   private String selectedOldRevision;
   private String newTitleRev;
   private String sNewTitlerevNote;
   private String newRevision;
   private String journalNote;
   private String selectedObjects;
   private String wizHeader;
   private String sNewRevision;
   private String oldTitleRevision;
   private String object_lu_name;
   private String object_key_ref;
   private String sRadioButton; //Bug Id 69329


   private boolean bCopyStructure;
   private boolean bOriginalCopyStructure;
   private boolean bCopyAppRoute;
   private boolean bOriginalCopyAppRoute;
   private boolean bCopyFile;
   private boolean bOriginalCopyFile;
   private boolean bCopyAccess;
   private boolean bCopyRecord;
   private boolean bOriginalCopyAccess;
   private boolean bUpdateAllowDuringApp;
   private boolean bNewTitleRev;
   private boolean bActiveFrame1;
   private boolean bActiveFrame2;
   private boolean bActiveFrame4;//Bug Id 69329
   private boolean bActiveFrame3; // for parent docs
   private boolean bMultipleRevisions;
   private boolean bTranferToEDM;
   private boolean bCloseWindow = false;
   private boolean bCancel = false;
   private boolean bNext2Available = false;
   private boolean breobject=false;

   private int noTransferRows;
   private int currentRow;

   private Vector selectDefaultArr;

   
       
   
   //===============================================================
   // Construction
   //===============================================================
   public NewRevisionWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();

      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
      log   =mgr.getASPLog();

      doc_class              = ctx.readValue("DOCCLASS", "");
      doc_no                 = ctx.readValue("DOCNO", "");
      doc_sheet              = ctx.readValue("DOCSHEET", "");
      callingUrl             = ctx.readValue("SEND_URL", "");
      selectedOldRevision    = mgr.readValue("SELECTED_VALUE", "");
      newTitleRev            = ctx.readValue("NEW_TITLE_REV","");
      sNewTitlerevNote       = ctx.readValue("NEW_TITLE_REV_NOTE","");
      newRevision            = ctx.readValue("NEWREVISION", "");
      journalNote            = ctx.readValue("JOURNALNOTE", "");
      selectedObjects        = ctx.readValue("SELECTEDROWS","");
      oldTitleRevision       = ctx.readValue("TITLE_REV","");
      object_lu_name         = ctx.readValue("OBJECT_LU_NAME", "");
      object_key_ref         = ctx.readValue("OBJECT_KEY_REF", "");
      bCopyStructure         = ctx.readFlag("BCOPYSTRUCTURE",         false);
      bCopyAppRoute          = ctx.readFlag("BCOPYAPPROUTE",          false);
      bCopyFile              = ctx.readFlag("BCOPYFILE",              false);
      bCopyAccess            = ctx.readFlag("BCOPYACCESS",            false);
      bCopyRecord            = ctx.readFlag("BCOPYRECORD",            false);

      bOriginalCopyStructure = ctx.readFlag("BORIGINALCOPYSTRUCTURE", false);
      bOriginalCopyAppRoute  = ctx.readFlag("BORIGINALCOPYAPPROUTE",  false);
      bOriginalCopyFile      = ctx.readFlag("BORIGINALCOPYFILE",      false);
      bOriginalCopyAccess    = ctx.readFlag("BORIGINALCOPYACCESS",    false);
      bUpdateAllowDuringApp  = ctx.readFlag("BUPDATEALLOWDURINGAPP",  false);
      bMultipleRevisions     = ctx.readFlag("MULTIPLE_REVISIONS",     false);
      bActiveFrame1          = ctx.readFlag("ACTIVEFRAME1",           true);
      bActiveFrame2          = ctx.readFlag("ACTIVEFRAME2",           false);
      bActiveFrame3          = ctx.readFlag("ACTIVEFRAME3",           false);
      bActiveFrame4          = ctx.readFlag("ACTIVEFRAME4",           false);//Bug Id 69329
      bNewTitleRev           = ctx.readFlag("NEWTITLEREV_CHECK",      false);
      breobject              = ctx.readFlag("BREOBJECT",             false);
      bNext2Available        = ctx.readFlag("NEXT2AVAILABLE",         false);

      sRadioButton	     = ctx.readValue("RADIOBUTTON", "DEFAULT");//Bug Id 69329

      buff            = ctx.readBuffer("BUFF");
      noTransferRows  = Integer.parseInt(ctx.readValue("NOTRANSFERROWS","0"));
      currentRow      = Integer.parseInt(ctx.readValue("CURRENTROW","0"));

      bCloseWindow = false;
      bCancel = false;

      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
         doc_class = mgr.readValue("DOC_CLASS");
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
         doc_no = mgr.readValue("DOC_NO");
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET")))
         doc_sheet = mgr.readValue("DOC_SHEET");
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV")))
         selectedOldRevision = mgr.readValue("DOC_REV");
      if (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL")))
         callingUrl = mgr.readValue("SEND_URL");
      if (!mgr.isEmpty(mgr.readValue("NEW_REV")))
         newRevision = mgr.readValue("NEW_REV");
      if (!mgr.isEmpty(mgr.readValue("JOURNAL_NOTE")))
         journalNote = mgr.readValue("JOURNAL_NOTE");

      if (!mgr.isEmpty(mgr.readValue("NEW_TITLEREV")))
         newTitleRev = mgr.readValue("NEW_TITLEREV");

      if (!mgr.isEmpty(mgr.readValue("NEW_TITLEREVNOTE")))
         sNewTitlerevNote = mgr.readValue("NEW_TITLEREVNOTE");

      if(!mgr.isEmpty(mgr.getQueryStringValue("OBJECT_LU_NAME")))
         object_lu_name =mgr.readValue("OBJECT_LU_NAME");

      if(!mgr.isEmpty(mgr.getQueryStringValue("OBJECT_KEY_REF")))
         object_key_ref =mgr.readValue("OBJECT_KEY_REF");

      if (DEBUG)
      {
         debug(this+ ": doc_class = "          + doc_class);
         debug(this+ ": doc_no = "             + doc_no);
         debug(this+ ": doc_sheet = "          + doc_sheet);
         debug(this+ ": selectedOldRevision = "+ selectedOldRevision);
         debug(this+ ": callingUrl = "         + callingUrl);
         debug(this+ ": newRevision = "        + newRevision);
         debug(this+ ": journalNote = "        + journalNote);
      }

      mgr.setPageExpiring();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.dataTransfered())
         runQuery();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))) && (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL"))))
      {
         getExistingRevisions();
         getDocRevisionSettings();
      }
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      else if (mgr.buttonPressed("FINISH2")||mgr.buttonPressed("FINISH3")||mgr.buttonPressed("FINISH4")) //Bug Id 69329
         finish();
      else if (mgr.buttonPressed("NXT2"))
         next2();
      else if ("NEXT".equals(mgr.readValue("NEXT")))
         Next();
      else if ("FINISH1".equals(mgr.readValue("FINISH1")))
         finish();
      else if (mgr.buttonPressed("PREVIOUS"))
         Previous();
      else if (mgr.buttonPressed("PREVIOUS2"))
         previous2();
      else if ("TRUE".equals(mgr.readValue("TRANSFERTOCALLINGURL")))
         transferToCallingURL();
      else if (!("".equals(mgr.readValue("SELECTED_VALUE"))))
         getDocRevisionSettings();

      ctx.writeValue("DOCCLASS",doc_class);
      ctx.writeValue("DOCNO",doc_no);
      ctx.writeValue("DOCSHEET",doc_sheet);
      ctx.writeValue("SEND_URL",callingUrl);
      ctx.writeValue("NEW_TITLE_REV",newTitleRev);
      ctx.writeValue("NEW_TITLE_REV_NOTE",sNewTitlerevNote);
      ctx.writeValue("SELECTEDROWS",selectedObjects);
      ctx.writeValue("NOTRANSFERROWS",String.valueOf(noTransferRows));
      ctx.writeValue("CURRENTROW",String.valueOf(currentRow));
      ctx.writeValue("NEWREVISION",newRevision);
      ctx.writeValue("JOURNALNOTE",journalNote);
      ctx.writeValue("TITLE_REV",oldTitleRevision);
      ctx.writeValue("OBJECT_LU_NAME", object_lu_name);
      ctx.writeValue("OBJECT_KEY_REF", object_key_ref);
      ctx.writeValue("RADIOBUTTON", sRadioButton); //Bug Id 69329

      ctx.writeFlag("BCOPYSTRUCTURE",bCopyStructure);
      ctx.writeFlag("BCOPYAPPROUTE",bCopyAppRoute);
      ctx.writeFlag("BCOPYFILE",bCopyFile);
      ctx.writeFlag("NEWTITLEREV_CHECK",bNewTitleRev);
      ctx.writeFlag("BCOPYACCESS",bCopyAccess);
      ctx.writeFlag("BCOPYRECORD",bCopyRecord);
      ctx.writeFlag("BORIGINALCOPYSTRUCTURE",bOriginalCopyStructure);
      ctx.writeFlag("BORIGINALCOPYAPPROUTE",bOriginalCopyAppRoute);
      ctx.writeFlag("BORIGINALCOPYFILE",bOriginalCopyFile);
      ctx.writeFlag("BORIGINALCOPYACCESS",bOriginalCopyAccess);
      ctx.writeFlag("ACTIVEFRAME1",bActiveFrame1);
      ctx.writeFlag("ACTIVEFRAME2",bActiveFrame2);
      ctx.writeFlag("ACTIVEFRAME4",bActiveFrame4); //Bug Id 69329
      ctx.writeFlag("ACTIVEFRAME3",bActiveFrame3);
      ctx.writeFlag("BUPDATEALLOWDURINGAPP",bUpdateAllowDuringApp);
      ctx.writeFlag("MULTIPLE_REVISIONS",bMultipleRevisions);
      ctx.writeFlag("BREOBJECT", breobject);
      ctx.writeFlag("NEXT2AVAILABLE",bNext2Available);

      ctx.writeBuffer("REV_SELECT_BUFF",ctx.readBuffer("REV_SELECT_BUFF")); //need to keep track of this buffer all the time

      if (buff != null)
         ctx.writeBuffer("BUFF",buff);

      if (bActiveFrame1)
         wizHeader=makeWizardHeader(1);
      else
         wizHeader=makeWizardHeader(2);

      adjust();
   }


   public void runQuery() throws FndException
   {
      if (DEBUG) debug(this+": runQuery() {");

      ASPManager mgr = getASPManager();

      ASPBuffer transferred_data = mgr.getTransferedData();

      buff = transferred_data.getBufferAt(1);

      noTransferRows = buff.countItems();

      if (DEBUG) debug(this+": No. of items transfered = " + noTransferRows);

      bMultipleRevisions = true;
      currentRow = 0;

      ASPBuffer subBuff = buff.getBufferAt(currentRow);
      doc_class = subBuff.getValueAt(0);
      doc_no = subBuff.getValueAt(1);
      doc_sheet           = subBuff.getValueAt(2);
      selectedOldRevision = subBuff.getValueAt(3);
      noTransferRows -= 1;

      //Get the existing revisions and the settings for the current revision
      findNext2Available();
      getExistingRevisions();
      getDocRevisionSettings();


      if (DEBUG) debug(this+": runQuery() }");
   }

  /**
   * This method determines if we must show next button on 2nd window and
   * 3rd window itself.: baka
  */
   private void findNext2Available() throws FndException
   {
       if (DEBUG) debug(this+": findNext2Available() {");

       ASPManager mgr = getASPManager();

       trans.clear();

       okFindITEM3(trans); // we populate item3blk here and then can find if parent docs exist!!

       cmd = trans.addCustomFunction("STRUCTRUE_TYPE","DOC_TITLE_API.Get_Structure_", "STRUCTRUE_VALUE" );
       cmd.addParameter("DOC_CLASS",doc_class);
       cmd.addParameter("DOC_NO",doc_no);

       trans = mgr.submit(trans);

       String sStructure = trans.getValue("STRUCTRUE_TYPE/DATA/STRUCTRUE_VALUE");
                                           //STRUCTRUE_TYPE/DATA/STRUCTRUE_VALUE

       debug("nStructure="+sStructure);

       if ("1".equals(sStructure)  ){ //&& item3set != null && item3set.countRows()>0 : we can use this later:bakalk
	   bNext2Available =  true;
	   setAllSelected(item3set);
	   
       }


       if (DEBUG) debug(this+": findNext2Available() }");

   }


   public void  setAllSelected(ASPRowSet currRowSet){
       if (currRowSet != null && currRowSet.countRows()>0 ) {
	   currRowSet.first();
	   for (int k=0;k<currRowSet.countRows();k++) {
	       currRowSet.selectRow();
	       currRowSet.next();
	   }
       }
   }


   public void  getNextRowFromBuff()
   {
      if (DEBUG) debug(this+": getNextRowFromBuff() {");

      currentRow += 1;

      ASPBuffer subBuff = buff.getBufferAt(currentRow);
      doc_class = subBuff.getValueAt(0);
      doc_no = subBuff.getValueAt(1);
      doc_sheet           = subBuff.getValueAt(2);
      selectedOldRevision = subBuff.getValueAt(3);

      //initialise variables
      newRevision="";
      journalNote="";
      item1set.clear();
      item2set.clear();
      debug("***************** 3");

      getExistingRevisions();
      getDocRevisionSettings();

      noTransferRows-=1;
      bActiveFrame1 = true;
      bActiveFrame2 = false;
      bActiveFrame4 = false;//Bug Id 69329

      if (DEBUG) debug(this+": getNextRowFromBuff() }");
   }



   public void  validate()
   {
      if (DEBUG) debug(this+": validate() {");

      ASPManager mgr = getASPManager();

      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();

      if (DEBUG) debug(this+": validate() }");
   }


   public void  getExistingRevisions()
   {
      if (DEBUG) debug(this+": getExistingRevisions() {");

      ASPManager mgr = getASPManager();

      if (DEBUG) debug(this+": Retrieving existing document revisions..");

      trans.clear();
      //Bug ID 45944, inoslk, start
      q = trans.addQuery("EXISTING_REVS","DOC_ISSUE","DOC_REV,DOC_REV PR2","DOC_CLASS= ? AND DOC_NO= ? AND DOC_SHEET= ? ","DOC_REV");
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      mgr.submit(trans);

      ctx.writeBuffer("REV_SELECT_BUFF",ctx.getDbState().getBuffer("EXISTING_REVS"));

      okFindITEM2();



      if (DEBUG) debug(this+": getExistingRevisions() }");
   }


   public void  getDocRevisionSettings()
   {
      if (DEBUG) debug(this+": getDocRevisionSettings() {");

      ASPManager mgr = getASPManager();
      String sHasStructure, sHasAppRoute, sHasFileReference,sHasRecordData;
      String sUpdateAllowedDuringApp;

      trans.clear();
      cmd = trans.addCustomCommand("GETINFO","DOC_ISSUE_API.Get_Info");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV", selectedOldRevision);
      cmd.addParameter("HAS_STRUCTURE");
      cmd.addParameter("HAS_APP_ROUTE");
      cmd.addParameter("HAS_FILE");

      cmd = trans.addCustomFunction("RECORDDATA","DOC_ISSUE_API.Has_Record_Data", "HAS_REC_DATA" );
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV", selectedOldRevision);

      cmd = trans.addCustomFunction("GETREVISION","DOC_TITLE_API.Get_Revision","TITLE_REVISION");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);

      trans = mgr.perform(trans);

      sHasStructure = trans.getValue("GETINFO/DATA/HAS_STRUCTURE");
      sHasAppRoute = trans.getValue("GETINFO/DATA/HAS_APP_ROUTE");
      sHasFileReference = trans.getValue("GETINFO/DATA/HAS_FILE");
      sHasRecordData = trans.getValue("RECORDDATA/DATA/HAS_REC_DATA");
      oldTitleRevision=trans.getValue("GETREVISION/DATA/TITLE_REVISION");

      //If the title revision is null then no text should be displayed.
      //Bug Id 67456, start
      if (oldTitleRevision == null)
      {
	  oldTitleRevision = "";
      }
      //Bug Id 67456, end

      //These are the default settings for the new revision..
      bCopyStructure = (sHasStructure.equals("1")) ? true : false;
      bCopyAppRoute = (sHasAppRoute.equals("1"))  ? true : false;
      bCopyFile = (sHasFileReference.equals("1")) ? true : false;
      bCopyRecord = (sHasRecordData.equals("1")) ? true : false;
      bCopyAccess = true;

      // Need to keep track of the orginal document settings also..
      bOriginalCopyStructure = bCopyStructure;
      bOriginalCopyAppRoute = bCopyAppRoute;
      bOriginalCopyFile = bCopyFile;
      bOriginalCopyAccess = bCopyAccess;

      trans.clear();
      cmd = trans.addCustomFunction("GETAPPUPDATE","DOC_ISSUE_API.Get_Approval_Update","UPDATE_ALLOW_APP");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV", selectedOldRevision);
      trans = mgr.perform(trans);

      sUpdateAllowedDuringApp = trans.getValue("GETAPPUPDATE/DATA/UPDATE_ALLOW_APP");
      bUpdateAllowDuringApp = (sUpdateAllowedDuringApp.equals("TRUE")) ? true : false;

      //create a new row in item1set and set the corressponding values
      item1set.addRow(null);
      ASPBuffer tempBuff = item1set.getRow();
      tempBuff.setFieldItem("JOURNAL_NOTE",journalNote);
      tempBuff.setFieldItem("NEW_REV",newRevision);
      tempBuff.setFieldItem("HAS_STRUCTURE",bCopyStructure ? "1" : "0");
      tempBuff.setFieldItem("HAS_APP_ROUTE",bCopyAppRoute ? "1" : "0");
      tempBuff.setFieldItem("HAS_FILE",bCopyFile ? "1" : "0");
      tempBuff.setFieldItem("EXIST",bCopyAccess ? "1" : "0");
      tempBuff.setFieldItem("HAS_REC_DATA",bCopyRecord ? "1" : "0");
      tempBuff.setFieldItem("UPDATE_ALLOW_APP",bUpdateAllowDuringApp ? "1" : "0");
      item1set.setRow(tempBuff);

      if (DEBUG) debug(this+": getDocRevisionSettings() }");
   }




   public void  okFindITEM2()
   {
      if (DEBUG) debug(this+": okFindITEM2() {");

      ASPManager mgr = getASPManager();

      trans.clear();
      item2set.clear();
      q = trans.addEmptyQuery(item2blk);
      //Bug ID 45944, inoslk, start
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ?");
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.includeMeta("ALL");
      q.setBufferSize(100); // Bug Id 78853
      mgr.submit(trans);

      if (item2set.countRows()==0)
         item2set.clear();

      if (DEBUG) debug(this+": okFindITEM2() }");
   }


   public void okFindITEM3(ASPTransactionBuffer trans2) throws FndException
   {
      if (DEBUG) debug(this+": okFindITEM3() {");
      
      ASPManager mgr = getASPManager();
      DocmawConstants  constantsHolder = DocmawConstants.getConstantHolder(mgr);


      item3set.clear();
      q = trans2.addQuery(item3blk);

      q.addWhereCondition("SUB_DOC_CLASS = ? AND SUB_DOC_NO = ? AND SUB_DOC_SHEET = ? AND SUB_DOC_REV = ? AND NOT (DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV) = ? OR DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV) = ?)");
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV", selectedOldRevision);
      q.addParameter("STRUCTRUE_VALUE", constantsHolder.doc_issue_approved);
      q.addParameter("STRUCTRUE_VALUE", constantsHolder.doc_issue_released);
      
      q.includeMeta("ALL");


      if (DEBUG) debug(this+": okFindITEM3() }");
   }


   public String  makeWizardHeader(int page)
   {
      if (DEBUG) debug(this+": makeWizardHeader() {");

      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETDOCTITLE","DOC_TITLE_API.Get_Title","DUMMY1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      trans = mgr.perform(trans);
      String title = trans.getValue("GETDOCTITLE/DATA/DUMMY1");
      trans.clear();

      if (DEBUG) debug(this+": makeWizardHeader() }");

      String tempRevision;

      tempRevision=(page==1) ? selectedOldRevision : newRevision;

      return title + " ("  + doc_class + " - " + doc_no + " - " + doc_sheet + " - " + tempRevision + ")";
   }


   //===================================== BUTTON FUNCTIONS ============================================
   public void  cancel()
   {
      if (DEBUG) debug(this+ ": cancel() {");
      // Close the window and refresh parent.
      bCancel = true;
   }


   public void  close()
   {
      // Close the window and refresh parent.
      bCloseWindow = true;
   }

    public void  next2(){
      bActiveFrame1 = false;
      bActiveFrame2 = false;
      bActiveFrame3 = true;
      //okFindITEM2();

      //Bug Id 69329, start
      bActiveFrame4 = false;

      storeRadioButtons();
      //Bug Id 69329, end

      if (item2set != null && item2set.countRows()>0) {
	  item2set.storeSelections();
      }

    }

   public void  Next()
   {
      if (DEBUG) debug(this+ ": Next() {");

      ASPManager mgr = getASPManager();

      bActiveFrame1 = false;

      //Bug Id 69329, start
      if (GetNumberOfObjConnections() < 100)
      {
      bActiveFrame2 = true;
	  bActiveFrame4 = false;
      }
      else
      {
          bActiveFrame2 = false;
	  bActiveFrame4 = true;
      }
      //Bug Id 69329, end

      bActiveFrame3 = false;

      ASPBuffer row = item1set.getRow();
      row.setValue("JOURNAL_NOTE",journalNote);
      row.setValue("NEW_REV",newRevision);
      item1set.setRow(row);

      /* The user could be moving back(Previous) and forth(Next) between the
         first frame and the second. *If* the user had selected objects of his choice
         in the second frame, then those have to be automatically set on going on to the
         second frame.
      */

      if (!mgr.isEmpty(selectedObjects))
         selectConnectedObjectsInRowset(false);
      else
         selectConnectedObjectsInRowset(true);

      getDocSettingsForNewRevision();

      if (DEBUG) debug(this+ ": Next() }");
   }

   public void previous2(){
      bActiveFrame1 = false;

      //Bug Id 69329, start
      if (GetNumberOfObjConnections() < 100) 
      {
      bActiveFrame2 = true;
	  bActiveFrame4 = false;
      }
      else
      {
	  bActiveFrame2 = false;
	  bActiveFrame4 = true;
      }
      //Bug Id 69329, end

      bActiveFrame3 = false;
      item3set.storeSelections();
   }
   public void  Previous()
   {
      if (DEBUG) debug(this+ ": Previous() {");

      bActiveFrame1 = true;
      bActiveFrame2 = false;
      bActiveFrame3 = false;
      bActiveFrame4 = false; //Bug Id 69329

      ASPBuffer row = item1set.getRow();
      row.setValue("JOURNAL_NOTE",journalNote);
      row.setValue("NEW_REV",newRevision);
      row.setValue("STRUCT_CHECKABLE", String.valueOf(bCopyStructure));
      row.setValue("SURVEY_CHECKABLE", String.valueOf(bCopyAppRoute));
      row.setValue("FILE_CHECKABLE", String.valueOf(bCopyFile));
      row.setValue("ACCESS_CHECKABLE", String.valueOf(bCopyAccess));
      item1set.setRow(row);

      //Bug Id 69329, start
      if (GetNumberOfObjConnections() < 100)
      {
      storeObjectsSelectedByUser();
      }
      else
      {
	  storeRadioButtons();
      }
      //Bug Id 69329, end

      if (DEBUG) debug(this+ ": Previous() }");
   }




   public void  finish()
   {
      if (DEBUG) debug(this+": finish() {");

      ASPManager mgr = getASPManager();

      int nNoOfObjConnections = GetNumberOfObjConnections(); //Bug Id 69329

      // if the "Finish" button of frame1 was pressed..
      if ("FINISH1".equals(mgr.readValue("FINISH1")))
      {
         if (DEBUG) debug(this+": finish in Frame1");

         getDocSettingsForNewRevision(); // get revision settings set by user
         selectConnectedObjectsInRowset(true);
         bActiveFrame1 = true;
         bActiveFrame2 = false;
	 bActiveFrame4 = false; //Bug Id 69329
      }

      trans.clear();
      cmd = trans.addCustomCommand("NEWREV1","DOC_ISSUE_API.NEW_REVISION2__");
      cmd.addParameter("DOC_CLASS",    doc_class);
      cmd.addParameter("DOC_NO",       doc_no);
      cmd.addParameter("DOC_SHEET",    doc_sheet);
      cmd.addParameter("NEW_REV",      newRevision);
      cmd.addParameter("OLD_REV",      selectedOldRevision);
      cmd.addParameter("JOURNAL_NOTE", journalNote); // Bug ID 63859
      cmd.addParameter("DUMMY1",       "0");
      cmd.addParameter("DUMMY2",       String.valueOf((bUpdateAllowDuringApp) ? 1 : 0));
      cmd.addParameter("DUMMY2",       String.valueOf((bCopyRecord) ? 1 : 0));


      //Bug Id 69329, start
      if (nNoOfObjConnections <= 100) 
      {
	  trans = mgr.perform(trans);
	  trans.clear();
      sNewRevision = mgr.readValue("NEW_REV");
      }
      //Bug Id 69329, end

      if (mgr.buttonPressed("FINISH2"))
      {
         item2set.storeSelections();
         int x=item2set.countRows();
         if (x>0)
         {
            item2set.first();
            do
            {
               if (item2set.isRowSelected())
               {
                  if((item2set.getRow().getValue("LU_NAME").equals(object_lu_name))&&(item2set.getRow().getValue("KEY_REF").equals(object_key_ref)))
                        breobject=true;
               }
            } while (item2set.next());
         }
      }

      if (mgr.buttonPressed("FINISH3")) {
	  item3set.storeSelections();
	  item3set.setFilterOn();
	
      }else{
	  item3set.setFilterOff();
	  item3set.unselectRows();
      }

      //Bug Id 69329, start
      if (mgr.buttonPressed("FINISH4")) 
      {
	  storeRadioButtons();
      }

      if (nNoOfObjConnections > 100) 
      {
          if ("DEFAULT".equals(ctx.readValue("RADIOBUTTON")))
          {
              cmd = trans.addCustomCommand("MOVEDEFAULT","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    doc_class);
              cmd.addParameter("DOC_NO",       doc_no);
              cmd.addParameter("DOC_SHEET",    doc_sheet);
              cmd.addParameter("NEW_REV",      newRevision);

              trans = mgr.perform(trans);
              trans.clear();
          }
          else if ("MOVEALL".equals(ctx.readValue("RADIOBUTTON")) )
          {
              cmd = trans.addCustomCommand("MOVEALL","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    doc_class);
              cmd.addParameter("DOC_NO",       doc_no);
              cmd.addParameter("DOC_SHEET",    doc_sheet);
              cmd.addParameter("NEW_REV",      newRevision);
              cmd.addParameter("DUMMY1",       "FALSE");
              cmd.addParameter("DUMMY1",       "Y");

              trans = mgr.perform(trans);
              trans.clear();
          }
          else if ("NOMOVE".equals(ctx.readValue("RADIOBUTTON")) )
          {
	      trans = mgr.perform(trans);
              trans.clear();
          }
	  else
	  {
              cmd = trans.addCustomCommand("MOVEDEFAULT","DOC_REFERENCE_OBJECT_API.Move_All_Objs_To_Rev");
              cmd.addParameter("DOC_CLASS",    doc_class);
              cmd.addParameter("DOC_NO",       doc_no);
              cmd.addParameter("DOC_SHEET",    doc_sheet);
              cmd.addParameter("NEW_REV",      newRevision);

              trans = mgr.perform(trans);
              trans.clear();
	  }
	  sNewRevision = mgr.readValue("NEW_REV");
      }
      else
      {
      item2set.setFilterOn();
      int numRows=item2set.countSelectedRows();

      item2set.first();
      for (int x=0; x<numRows; x++)
      {
         cmd = trans.addCustomCommand("NEWREV3", "DOC_REFERENCE_OBJECT_API.History_Log_For_Move_Obj"); // Bug Id 78853
         cmd.addParameter("DOC_CLASS", doc_class);
         cmd.addParameter("DOC_NO",    doc_no);
         cmd.addParameter("DOC_SHEET", doc_sheet);
         cmd.addParameter("OLD_REV",   selectedOldRevision);
         cmd.addParameter("LU_NAME",   item2set.getRow().getValue("LU_NAME") );
         cmd.addParameter("KEY_REF",   item2set.getRow().getValue("KEY_REF"));
         cmd.addParameter("NEW_REV",   mgr.readValue("NEW_REV"));
         trans = mgr.perform(trans);
         trans.clear();
         item2set.next();
      }


      item2set.setFilterOff();
      }
      //Bug Id 69329, end

      cmd = trans.addCustomCommand("NEWREV2","DOC_ISSUE_API.Copy_Info");
      cmd.addParameter("DOC_CLASS",     doc_class);
      cmd.addParameter("DOC_NO",        doc_no);
      cmd.addParameter("DOC_SHEET",     doc_sheet);
      cmd.addParameter("NEW_REV",       mgr.readValue("NEW_REV"));
      cmd.addParameter("OLD_REV",       selectedOldRevision);
      cmd.addParameter("HAS_STRUCTURE", String.valueOf((bCopyStructure) ? 1 : 0));
      cmd.addParameter("HAS_APP_ROUTE", String.valueOf((bCopyAppRoute) ? 1 : 0));
      cmd.addParameter("HAS_FILE",      String.valueOf((bCopyFile) ? 1 : 0));
      cmd.addParameter("EXIST",         String.valueOf((bCopyAccess) ? 1 : 0));
      cmd.addParameter("NEW_REVISION",  "1");
      trans = mgr.perform(trans);

      if (bNewTitleRev)
      {
         trans.clear();
         cmd = trans.addCustomCommand("CHANGETITLEREV","DOC_TITLE_API.New_Title_Revision");
         cmd.addParameter("DOC_CLASS", doc_class);
         cmd.addParameter("DOC_NO",    doc_no);
         cmd.addParameter("DUMMY1",    newTitleRev);
         cmd.addParameter("DUMMY1",    sNewTitlerevNote);
         trans = mgr.perform(trans);
         trans.clear();

      }

      if (bCopyFile)
      {
         if (DEBUG) debug(this+ "Copy file..");


         try
         {
            copyFileInRepository(doc_class, doc_no, doc_sheet, selectedOldRevision, doc_class, doc_no, doc_sheet, mgr.readValue("NEW_REV"));
         }
         catch (FndException err)
         {
            mgr.showError(mgr.translate("DOCMAWNEWREVISIONWIZARDCREATENEWREVFAILED: Create New Revision failed.\\n"));
         }
         catch (Exception err)
         {
            mgr.showError(mgr.translate("DOCMAWNEWREVISIONWIZARDCREATENEWREVFAILED: Create New Revision failed.\\n"));
         }
         finally
         {
         }
      }
      if (bNext2Available) { // only if 3rd window available:
	   trans.clear();
	   item3set.first();
	  for (int k=0; k<item3set.countRows();k++) {
	
              cmd = trans.addCustomCommand("CHANGETITLEREV"+k,"DOC_STRUCTURE_API.Replace_Revision_");
	      cmd.addParameter("ITEM3_DOC_CLASS",   item3set.getRow().getValue("DOC_CLASS") );
	      cmd.addParameter("ITEM3_DOC_NO",   item3set.getRow().getValue("DOC_NO") );
	      cmd.addParameter("ITEM3_DOC_SHEET",   item3set.getRow().getValue("DOC_SHEET") );
	      cmd.addParameter("ITEM3_DOC_REV",   item3set.getRow().getValue("DOC_REV") );
	      cmd.addParameter("DOC_CLASS",   doc_class );
	      cmd.addParameter("DOC_NO",   doc_no );
	      cmd.addParameter("DOC_SHEET",   doc_sheet );
	      cmd.addParameter("DOC_REV",   selectedOldRevision );
	      cmd.addParameter("NEW_REV",   newRevision );
	      item3set.next();
	  }
	  trans = mgr.perform(trans);
      }

      transferToCallingURL();

      if (DEBUG) debug(this+": finish() }");
   }



   public void transferToCallingURL()
   {
      if (DEBUG) debug(this+ ": transferToCallingURL() {");

      ASPManager mgr = getASPManager();

      if (noTransferRows != 0)
      {
         getNextRowFromBuff();
         //Note! if buffer exists, modification of last row in the buffer is skipped by the logic 'if (noTransferRows!=0)'
         buff.getBufferAt(currentRow-1).setValueAt(2, newRevision);
      }
      else
      {
         if (bMultipleRevisions)
         {
            // to modify the last row in the buffer
            //buff.getBufferAt(currentRow).setValueAt(2,mgr.readValue("NEW_REV"));
            buff.getBufferAt(currentRow).setValueAt(2,newRevision);
            close();
         }
         else
            close();
      }

      if (DEBUG) debug(this+ ": transferToCallingURL() }");
   }



   public void getDocSettingsForNewRevision()
   {
      if (DEBUG) debug(this+ ": getDocSettingsForNewRevision() {");

      ASPManager mgr = getASPManager();

      // these values are stored and read from the context on each run
      bNewTitleRev        = ("ON".equals(mgr.readValue("CREATENEWTITLEREV_CHECK"))) ? true : false;
      //from selected rev
      bCopyStructure        = ("ON".equals(mgr.readValue("CBCOPYSTRUCTURE"))) ? true : false;
      bCopyAppRoute         = ("ON".equals(mgr.readValue("CBCOPYDOCUMENTSURVEY"))) ? true : false;
      bCopyFile             = ("ON".equals(mgr.readValue("CBCOPYFILE"))) ? true : false;
      bCopyAccess           = ("ON".equals(mgr.readValue("CBCOPYACCESS"))) ? true : false;
      bCopyRecord           = ("ON".equals(mgr.readValue("CBCOPYRECORD"))) ? true : false;
      bUpdateAllowDuringApp = ("ON".equals(mgr.readValue("CBUPDATEDURINGAPP"))) ? true : false;


      if (DEBUG) debug(this+ ": getDocSettingsForNewRevision() }");
   }



   public void  selectDefaultObjectsInToArray()
   {
      if (DEBUG) debug(this+ ": selectDefaultObjectsInToArray() {");

      String lock, updateRevision;
      int numRows=item2set.countRows();

      selectDefaultArr=new Vector(0); // array to hold Rows Nos that have to be selected by default

      item2set.first();
      for (int x=0; x<numRows; x++)
      {
         lock=item2set.getRow().getValue("SURVEY_LOCKED_FLAG_DB");
         updateRevision=item2set.getRow().getValue("KEEP_LAST_DOC_REV_DB");

         if (( "L".equals(updateRevision) ))
            selectDefaultArr.addElement("1");
         else
            selectDefaultArr.addElement("0");
         item2set.next();
      }

      if (DEBUG) debug(this+ ": selectDefaultObjectsInToArray() }");
   }



   public void  selectConnectedObjectsInRowset(boolean bDefault)
   {
      if (DEBUG) debug(this+ ": selectConnectedObjectsInRowset() {");


      ASPManager mgr = getASPManager();


      /*
         The user could be moving back(Previous) and forth(Next) between the
         first frame and the second. *If* the user had selected objects of his choice
         in the second frame, then those have to be automatically set on going on to the
         second frame.
      */

      if (!bDefault)
      {
         if (DEBUG) debug(this+ ": Select objects selected by user..");

         String selectedRowsArr[];
         selectedRowsArr=split(selectedObjects,"^");
         item2set.first();

         if (!(selectedRowsArr==null))
         {
            for (int x=0;x<selectedRowsArr.length;x++)
            {
               if (selectedRowsArr[x]=="1")
                  item2set.selectRow();

               item2set.next();
            }
         }
      }
      else
      {
         if (DEBUG) debug(this+ ": Select objects by default..");

         String lock, updateRevision;
         int numRows=item2set.countRows();

         item2set.first();
         for (int x=0; x<numRows; x++)
         {
            lock=item2set.getRow().getValue("SURVEY_LOCKED_FLAG_DB");
            updateRevision=item2set.getRow().getValue("KEEP_LAST_DOC_REV_DB");

            if (( "0".equals(lock) ) &&  ( "L".equals(updateRevision) ))
               item2set.selectRow();

            item2set.next();
         }
      }


      if (DEBUG) debug(this+ ": selectConnectedObjectsInRowset() }");
   }



   public void  storeObjectsSelectedByUser()
   {
      if (DEBUG) debug(this+ ": storeObjectsSelectedByUser() {");

      int numRows=item2set.countRows();
      selectedObjects="";

      item2set.first();
      item2set.storeSelections();
      for (int x=0;x<numRows;x++)
      {
         if (item2set.isRowSelected() == true)
            selectedObjects+="1";
         else
            selectedObjects+="0";
         if (x != numRows-1)
            selectedObjects+="^";
         item2set.next();
      }

      // "selectedObjects" will be written to the context

      if (DEBUG) debug(this+ ": storeObjectsSelectedByUser() }");
   }

   //Bug Id 69329, start
   public int GetNumberOfObjConnections()
   {
       ASPManager mgr = getASPManager();

       cmd = trans.addCustomFunction("NOOFOBJECTCON","DOC_REFERENCE_OBJECT_API.Get_Num_Of_All_Connections", "ITEM4_NO_OF_OBJ_CON");
       cmd.addParameter("DOC_CLASS",   doc_class );
       cmd.addParameter("DOC_NO",   doc_no );
       cmd.addParameter("DOC_SHEET",   doc_sheet );

       trans = mgr.perform(trans);

       String sno_of_obj_con = trans.getValue("NOOFOBJECTCON/DATA/ITEM4_NO_OF_OBJ_CON");
       
       trans.clear();

       return Integer.parseInt(sno_of_obj_con);

   }

   public void storeRadioButtons()
   {
       ASPManager mgr = getASPManager();
       String buttonDefault = mgr.readValue("DEFAULT");
       String buttonMoveAll = mgr.readValue("MOVEALL");
       String buttonNoMove  = mgr.readValue("NOMOVE");

       if ("DEFAULT".equals(buttonDefault)) 
       {
	   ctx.writeValue("RADIOBUTTON", buttonDefault);
	   sRadioButton = buttonDefault;
       }
       else if ("MOVEALL".equals(buttonMoveAll))
       {
	   ctx.writeValue("RADIOBUTTON", buttonMoveAll);
	   sRadioButton = buttonMoveAll;
       }
       else if ("NOMOVE".equals(buttonNoMove))
       {
	   ctx.writeValue("RADIOBUTTON", buttonNoMove);
	   sRadioButton = buttonNoMove;
       }

   }
   //Bug Id 69329, end

   //============================================================================
   //   CUSTOM FUNCTIONS
   //============================================================================

   public void  doselect()
   {
      if (DEBUG) debug(this+ ": doselect() {}");
   }


   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();


      disableConfiguration();
      disableHeader();

      // *********************************** BLOCK FOR DIALOG 1 ***********************************
      item1blk = mgr.newASPBlock("ITEM1");

      item1blk.addField("ITEM1_OBJID").   // sicne DocSrv contains OBJID aspfield.
      setDbName("OBJID").
      setHidden();

      item1blk.addField("ITEM1_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      item1blk.addField( "DOC_CLASS" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "DOC_NO" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "DOC_SHEET" ).
      setHidden().
      setFunction("''");

      item1blk.addField("DOC_REV").
      setSize(7).
      setMandatory().
      setLabel("DOCMAWNEWREVISIONWIZARDSNEWPARTREV: Revision").
      setUpperCase().
      setHidden().
      setFunction("''");

      item1blk.addField( "HAS_STRUCTURE" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "HAS_APP_ROUTE" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "HAS_FILE" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "HAS_REC_DATA" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "DUMMY1" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "DUMMY2" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "EXIST" ).
      setHidden().
      setFunction("''");

      item1blk.addField( "NEW_REVISION" ).
      setHidden().
      setFunction("''");

      item1blk.addField("PART_NO").
      setHidden().
      setFunction("''");

      item1blk.addField("NEW_REV").
      setSize(7).
      setMaxLength(6). //Bug Id 74405
      setMandatory().
      setLabel("DOCMAWNEWREVISIONWIZARDSNEWPARTREV: Revision").
      setFunction("''").
      setUpperCase();

      item1blk.addField("OLD_REV").
      setHidden().
      setFunction("''");

      item1blk.addField("DOCTYPE").
      setHidden().
      setFunction("''");

      item1blk.addField("JOURNAL_NOTE").
      setSize(48).
      setHeight(3).
      setMaxLength(1900). //Bug Id 74405
      setMandatory().
      setLabel("DOCMAWNEWREVISIONWIZARDMLSNEWPARTREVTEXT: Revision Text").
      setFunction("''");

      item1blk.addField("UPDATE_ALLOW_APP").
      setHidden().
      setFunction("''");

      item1blk.addField("TITLE_REVISION").setHidden().setFunction("''");

      item1blk.setView("DOC_ISSUE");
      item1blk.setTitle(mgr.translate("DOCMAWNEWREVISIONWIZARDITEMBLK1TITLE: Create New revision Wizard - Step 1"));

      item1bar = mgr.newASPCommandBar(item1blk);
      item1bar.disableCommand(item1bar.DELETE);
      item1bar.disableCommand(item1bar.EDITROW);
      item1bar.disableCommand(item1bar.DUPLICATEROW);
      item1bar.disableCommand(item1bar.FIND);
      item1bar.disableCommand(item1bar.SAVENEW);
      item1bar.disableCommand(item1bar.NEWROW);
      item1bar.disableCommand(item1bar.SAVERETURN);
      item1bar.disableCommand(item1bar.CANCELNEW);
      item1bar.disableMinimize();

      item1set = item1blk.getASPRowSet();

      item1tbl = mgr.newASPTable(item1blk);

      item1lay = item1blk.getASPBlockLayout();
      item1lay.setDefaultLayoutMode(item1lay.CUSTOM_LAYOUT);
      item1lay.setEditable();
      item1lay.setDataSpan("JOURNAL_NOTE",5);
      item1lay.setDialogColumns(1);


      // *********************************** DLG 1 ***********************************
      item2blk = mgr.newASPBlock("ITEM2");

      item2blk.addField("ITEM2_OBJID").
      setHidden().
      setDbName("OBJID");

      item2blk.addField("ITEM2_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      item2blk.addField( "ITEM2_DOC_CLASS").
      setHidden().
      setDbName("DOC_CLASS");

      item2blk.addField( "ITEM2_DOC_NO" ).
      setHidden().
      setDbName("DOC_NO");

      item2blk.addField( "ITEM2_DOC_SHEET" ).
      setHidden().
      setDbName("DOC_SHEET");


      item2blk.addField(" LU_NAME").
      setHidden();

      item2blk.addField("KEY_REF").
      setHidden();

      item2blk.addField("DOC_OBJECT").
      setLabel("DOCMAWNEWREVISIONWIZARDOBJECT: Object").
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(:LU_NAME)");

      item2blk.addField("DOC_OBJECT_KEY").
      setLabel("DOCMAWNEWREVISIONWIZARDOBJECTKEY: Object Key").
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Instance_Description(:LU_NAME,NULL,:KEY_REF)");

      item2blk.addField("DOC_OBJECT_DESC").
      setReadOnly().
      setLabel("DOCMAWNEWREVISIONWIZARDOBJDESC: Object Description");

      item2blk.addField("SURVEY_LOCKED_FLAG").
      setSize(200).
      setReadOnly().
      setLabel("DOCMAWNEWREVISIONWIZARDSURVEYLOCKEDFLAG: Doc Connection Status");

      item2blk.addField("SURVEY_LOCKED_FLAG_DB").
      setHidden();

      item2blk.addField("KEEP_LAST_DOC_REV").
      setSize(200).
      setReadOnly().
      setLabel("DOCMAWNEWREVISIONWIZARDUPDREV: Update Revision");

      item2blk.addField("KEEP_LAST_DOC_REV_DB").
      setHidden();

      item2blk.setView("DOC_REFERENCE_OBJECT");
      item2blk.setTitle(mgr.translate("DOCMAWNEWREVISIONWIZARDSTEP2: Create New Revision - Step 2"));
      item2blk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");
      item2blk.setMasterBlock(item1blk);
      //item2blk.setTitle("Create New revision");
      item2bar = mgr.newASPCommandBar(item2blk);
      item2bar.defineCommand(item2bar.OKFIND,   "okFindITEM2");

      item2bar.disableCommand(item2bar.NEWROW);
      item2bar.disableCommand(item2bar.DUPLICATEROW);
      item2bar.disableCommand(item2bar.DELETE);
      item2bar.disableCommand(item2bar.NEWROW);
      item2bar.disableCommand(item2bar.EDITROW);
      item2bar.disableMinimize();

      //item2bar.addCustomCommand("doSelect", mgr.translate("Select..."));

      item2set = item2blk.getASPRowSet();
      item2tbl = mgr.newASPTable(item2blk);
      item2tbl.disableQuickEdit();
      item2tbl.enableRowSelect();
      item2tbl.disableEditProperties();
      item2lay = item2blk.getASPBlockLayout();
      item2lay.setDefaultLayoutMode(item2lay.MULTIROW_LAYOUT);
      item2lay.unsetAutoLayoutSelect();

      //Bug Id 69329, start
      // ********************** This is the dialog 2 when the number of object connections is > 100 ***********************************
      item4blk = mgr.newASPBlock("ITEM4");

      item4blk.addField( "ITEM4_NO_OF_OBJ_CON" ).
      setHidden();

      item4blk.setTitle(mgr.translate("DOCMAWNEWREVISIONWIZARDSTEP2: Create New Revision - Step 2"));
      item4blk.setMasterBlock(item1blk);
      item4bar = mgr.newASPCommandBar(item4blk);

      item4bar.disableCommand(item4bar.NEWROW);
      item4bar.disableCommand(item4bar.DUPLICATEROW);
      item4bar.disableCommand(item4bar.DELETE);
      item4bar.disableCommand(item4bar.NEWROW);
      item4bar.disableCommand(item4bar.EDITROW);
      item4bar.disableMinimize();


      item4set = item4blk.getASPRowSet();
      item4tbl = mgr.newASPTable(item4blk);
      item4tbl.disableQuickEdit();
      item4tbl.enableRowSelect();
      item4tbl.disableEditProperties();
      item4lay = item4blk.getASPBlockLayout();
      item4lay.setDefaultLayoutMode(item4lay.MULTIROW_LAYOUT);
      item4lay.unsetAutoLayoutSelect();

      //Bug Id 69329, end

      //************************************ Dialog 3 (consists of parent docs, showed only if current doc is of structure type.)
      item3blk = mgr.newASPBlock("ITEM3");

      item3blk.addField("ITEM3_OBJID").
      setHidden().
      setDbName("OBJID");

      item3blk.addField("ITEM3_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      item3blk.addField( "ITEM3_DOC_CLASS").
      setLabel("DOCMAWNEWREVISIONWIZARDDOCCLASS: Doc Class").
      setDbName("DOC_CLASS");

      item3blk.addField( "ITEM3_DOC_NO" ).
      setLabel("DOCMAWNEWREVISIONWIZARDDOCNO: Doc No").
      setDbName("DOC_NO");

      item3blk.addField( "ITEM3_DOC_SHEET" ).
      setLabel("DOCMAWNEWREVISIONWIZARDDOCSHEET: Doc Sheet").
      setDbName("DOC_SHEET");

      item3blk.addField( "ITEM3_DOC_REV" ).
      setLabel("DOCMAWNEWREVISIONWIZARDDOCREV: Doc Rev").
      setDbName("DOC_REV");

      //hidden field
      item3blk.addField("STRUCTRUE_VALUE" ).
      setFunction("''").
      setHidden();


      item3blk.setView("DOC_STRUCTURE");
      
      item3blk.setTitle(mgr.translate("DOCMAWNEWREVISIONWIZARDSTEP3: Create New Revision - Step 3"));
      item3blk.defineCommand("DOC_STRUCTURE_API","New__,Modify__,Remove__");
      item3blk.setMasterBlock(item1blk);
      //item2blk.setTitle("Create New revision");
      //appendToHTML(fmt.drawReadLabel());
      item3bar = mgr.newASPCommandBar(item3blk);
      item3bar.defineCommand(item3bar.OKFIND,   "okFindITEM3");

      item3bar.disableCommand(item3bar.NEWROW);
      item3bar.disableCommand(item3bar.DUPLICATEROW);
      item3bar.disableCommand(item3bar.DELETE);
      item3bar.disableCommand(item3bar.NEWROW);
      item3bar.disableCommand(item3bar.EDITROW);
      item3bar.disableMinimize();


      item3set = item3blk.getASPRowSet();
      item3tbl = mgr.newASPTable(item3blk);
      item3tbl.disableQuickEdit();
      item3tbl.enableRowSelect();
      item3tbl.disableEditProperties();
      item3lay = item3blk.getASPBlockLayout();
      item3lay.setDefaultLayoutMode(item3lay.MULTIROW_LAYOUT);

      super.preDefine();
   }


   public void  adjust()
   {
      if (DEBUG) debug(this+ ": adjust() {");

      ASPManager mgr = getASPManager();

      if (bActiveFrame2)
      {
         mgr.getASPField("NEW_REV").setHidden();
         mgr.getASPField("JOURNAL_NOTE").setHidden();
      }
      //disableHelp();
      disableNavigate();

      try
      {
         //disabling some icons not supported by version 3.5.1
         eval2("disableHomeIcon",null); //null for parameter set
         eval2("disableOptions",null);
      }catch(NoSuchMethodException e){}

      if (DEBUG) debug(this+ ": adjust() }");
   }


   private void eval2(String method,Class[] parms) throws NoSuchMethodException
   {

       Method m = getClass().getMethod(method,parms);
       eval(method);

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

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWNEWREVISIONWIZARDTITLE: Create New Revision Wizard";
   }

   protected String getTitle()
   {
      return "DOCMAWNEWREVISIONWIZARDTITLE: Create New Revision Wizard";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean lock_objects = false;
         
      appendToHTML("<input type=\"hidden\" name=\"TRANSFERTOCALLINGURL\" value=\"\">");

      if (bActiveFrame1)
      {
         //******** bar and image
         appendToHTML("<table border=0  cellspacing=\"0\" cellpadding=\"0\" >\n");
         appendToHTML("<tr><td colspan=\"4\">"+item1bar.showBar()+"<td></tr>");

         appendToHTML("<tr bgcolor=white >\n");
         appendToHTML("<td width=\"10\"></td>");
         appendToHTML("<td  ><img src = \"../docmaw/images/CreateNewDocRevision.jpg\" ></td>\n");
         appendToHTML("<td >");
         //******* start of dialog
         appendToHTML(" <table border=0    bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 >\n");
         appendToHTML("   <tr>\n");//row 1
         //appendToHTML("     <td nowrap height=\"26\" align=\"left\" ><img src = \"images/CreateNewDocRevision.jpg\"></td>\n");//r1 c1
         appendToHTML("    <td><!--r1c2 baka --><table align=\"top\" border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"400\">\n");
         appendToHTML("       <tr>\n");
         appendToHTML(fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDWIZHEADER: Create New Revision From :<p>"));
         appendToHTML(fmt.drawReadLabel(wizHeader));
         appendToHTML("</b></font><p>        \n");
         appendToHTML(fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDWIZARDDESC: Use this Wizard to create a new revision of an existing document"));
         appendToHTML("          <p>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr>\n");
         appendToHTML("       <td>"+fmt.drawWriteLabel("CREATENEWTITLEREV: Change Revision on Title?")+"</td>\n");
         appendToHTML("       <td>"+fmt.drawWriteLabel("YESNO: Yes")+fmt.drawCheckbox("CREATENEWTITLEREV_CHECK","ON",bNewTitleRev,"onClick=setValue()")+"</td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <hr>\n");
         appendToHTML("       <tr>\n");
         appendToHTML("       <td>"+fmt.drawWriteLabel("EXISTTITLEREV: Existing Title Revision")+"</td>\n");
         appendToHTML("       <td>"+ fmt.drawReadOnlyTextField("REVISION",oldTitleRevision,"")+"</td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr> \n");
         appendToHTML("       <td>"+fmt.drawWriteLabel("NEWTITLEREV: New Title Revision")+"</td>\n");
         appendToHTML("       <td>"+fmt.drawTextField("NEW_TITLEREV",newTitleRev,"",0,6)+"</td> \n"); //Bug 67230, Added parameters size and length.
         appendToHTML("       </tr> \n");
         appendToHTML("       <tr>  \n");
         appendToHTML("       <td>"+fmt.drawWriteLabel("NEWTITLEREVNOTE: Title Revision Note") + "</td>\n");
         appendToHTML("       <td>"+fmt.drawTextField("NEW_TITLEREVNOTE",sNewTitlerevNote,"")+"</td>  \n");
         appendToHTML("       </tr>\n");
         appendToHTML("    <tr>\n");
         appendToHTML("       <td nowrap align=\"top\" ><U>");
         appendToHTML(fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDNEWVALUES: New"));
         appendToHTML("</U></td>\n");
         appendToHTML("    </tr>\n");
         appendToHTML("    </table>\n");
         appendToHTML("    <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"400\">\n");
         appendToHTML("       <tr>\n");
         appendToHTML("       <td>");
         appendToHTML(item1lay.generateDialog());
         appendToHTML("<td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr>\n");
         appendToHTML("          <td>");
         appendToHTML(fmt.drawCheckbox("CBUPDATEDURINGAPP","ON",bUpdateAllowDuringApp,""));
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDUPDURAPP: Update allowed during Approval"));
         appendToHTML("</td>\n");
         appendToHTML("          <td><br></td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("    </table>\n");
         appendToHTML("      <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"500\">\n");
         appendToHTML("      <tr>\n");
         appendToHTML("       <td><hr></td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("      <tr>\n");
         appendToHTML("         <td><U>");
         appendToHTML(fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDOLDVALUES: From Old"));
         appendToHTML("</U></td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("      <tr><td></td></tr>\n");
         appendToHTML("      </table>\n");
         appendToHTML("      <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"300\">\n");
         appendToHTML("      <tr>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDREVSON: Revision"));
         appendToHTML("</td>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawSelect("DOC_REV_OLD", ctx.readBuffer("REV_SELECT_BUFF"), selectedOldRevision,"OnChange=updateAllValues()"));
         appendToHTML("</td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("      <tr>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDCPSTR: Copy Structure"));
         appendToHTML("</td>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawCheckbox("CBCOPYSTRUCTURE","ON",bCopyStructure,"OnClick=structureCheck()"));
         appendToHTML("</td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("      <tr>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDCPAPPROUTE: Copy Approval Process"));
         appendToHTML("</td>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawCheckbox("CBCOPYDOCUMENTSURVEY","ON",bCopyAppRoute,"OnClick=surveyCheck()"));
         appendToHTML("</td>\n");
         appendToHTML("      </tr>\n");

         appendToHTML("      <tr>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDCPFIL: Copy File"));
         appendToHTML("</td>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawCheckbox("CBCOPYFILE","ON",bCopyFile,"OnClick=fileCheck()"));//OnClick=fileCheck()
         appendToHTML("</td>\n");
         appendToHTML("      </tr>\n");

         appendToHTML("      <tr>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawWriteLabel("DOCMAWNEWREVISIONWIZARDCPACCESS: Copy Access"));
         appendToHTML("</td>\n");
         appendToHTML("         <td>");
         appendToHTML(fmt.drawCheckbox("CBCOPYACCESS","ON",bCopyAccess,"OnClick=accCheck()"));
         appendToHTML("</td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("     <tr>\n");
         appendToHTML("      <td>"+fmt.drawWriteLabel("COPYREV: Copy Record Data")+"</td>\n");
         appendToHTML("      <td>"+fmt.drawCheckbox("CBCOPYRECORD", "ON",bCopyRecord,"")+"</td>\n");
         appendToHTML("     </tr> \n");
         appendToHTML("      </table>\n");
         appendToHTML("      <!--table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"400\">\n");
         appendToHTML("       <tr>\n");
        // appendToHTML(item2lay.generateDataPresentation());
         appendToHTML("       </tr>\n");
         appendToHTML("      </table-->\n");
         appendToHTML("      <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"500\">\n");
         appendToHTML("      <tr>\n");
         appendToHTML("        <td>\n");
         appendToHTML("          <br>\n");
         appendToHTML("          <td><input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"");
         appendToHTML(mgr.HTMLEncode(selectedOldRevision));
         appendToHTML("\"></td>\n");
         appendToHTML("        <td><input type=\"hidden\" name=\"NP_REV\" value=\"");
         appendToHTML(mgr.HTMLEncode(newRevision));
         appendToHTML("\"></td>\n");
         appendToHTML("        <td><input type=\"hidden\" name=\"JNOTE\" value=\"");
         appendToHTML(mgr.HTMLEncode(journalNote));
         appendToHTML("\"></td>\n");
         appendToHTML("        </td>\n");
         appendToHTML("      </tr>\n");
         appendToHTML("      </table>\n");
         appendToHTML("    </td>\n");//end of r1c2
         appendToHTML("   </tr>\n");
         appendToHTML("   <tr>\n");
         appendToHTML("</tr>\n");
         appendToHTML(" </table>\n");
         appendToHTML("</td>");//end of dialog
         appendToHTML("<td width=\"10\"></td>"); //space at the end of second row
         appendToHTML("</tr><tr>\n");//start of last row
         appendToHTML("<td width=\"10\"></td>");  //space at the begining of last row
         appendToHTML("<td colspan=\"2\" align=\"right\">");
         //+buttons+
         //appendToHTML(" <table>\n");
         //appendToHTML(" <tr>\n");
         //appendToHTML("       <td align=\"right\" colspan=\"2\" width=628>");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWREVISIONWIZARDCANCELL: Cancel"),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML(fmt.drawButton("NXT",mgr.translate("DOCMAWNEWREVISIONWIZARDNEXT:  Next> "),"OnClick=checkEmptyFields('nxt')"));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML(fmt.drawButton("FINISH0",mgr.translate("DOCMAWNEWREVISIONWIZARDFINISHH: Finish"),"OnClick=checkEmptyFields('fin')"));
         //appendToHTML("</td>\n");
         appendToHTML("                           <input type=\"hidden\" name=\"NEXT\" value=\"\">\n");
         appendToHTML("                           <input type=\"hidden\" name=\"FINISH1\" value=\"\">\n");
         //appendToHTML("      </tr>\n");
         appendToHTML(" </table>\n");
       //end of buttons
         appendToHTML("</td>\n");
         appendToHTML("<td width=\"10\"></td>");  //space at the end of last row
         appendToHTML("</tr>\n");
         appendToHTML("</table>\n");


         appendToHTML("   <script LANGUAGE=\"JavaScript\">\n");
         appendToHTML("      document.form.NEW_REV.focus();\n");
         appendToHTML("      document.form.NEW_TITLEREV.readOnly=true;\n");
         appendToHTML("      document.form.NEW_TITLEREVNOTE.readOnly=true;\n");
         appendToHTML("      setValue();\n\n");
         appendToHTML("      function setValue()\n");
         appendToHTML("      { \n");
         appendToHTML("         if (document.form.CREATENEWTITLEREV_CHECK.checked==true)\n");
         appendToHTML("         { \n");
         appendToHTML("            document.form.NEW_TITLEREV.readOnly=false;\n");
         appendToHTML("            document.form.NEW_TITLEREVNOTE.readOnly=false;\n");
         appendToHTML("            document.form.NEW_TITLEREVNOTE.style.backgroundColor=\"#ffffff\";\n");
         appendToHTML("            document.form.NEW_TITLEREV.style.backgroundColor=\"#ffffff\";\n");
         appendToHTML("         } \n");
         appendToHTML("         else \n");
         appendToHTML("         { \n");
         appendToHTML("            document.form.NEW_TITLEREVNOTE.readOnly = true;\n");
         appendToHTML("            document.form.NEW_TITLEREV.readOnly     = true;\n");
         appendToHTML("            document.form.NEW_TITLEREVNOTE.value    = \"\";\n");
         appendToHTML("            document.form.NEW_TITLEREV.value        = \"\";\n");
         appendToHTML("            document.form.NEW_TITLEREVNOTE.style.backgroundColor=\"#cccccc\";\n");
         appendToHTML("            document.form.NEW_TITLEREV.style.backgroundColor=\"#cccccc\";\n");
         appendToHTML("         } \n");
         appendToHTML("      }\n");
         appendToHTML("   </script>\n");
      }

      String leavinLine = " <tr ><td>\n";
      leavinLine += "&nbsp;&nbsp;<!-- leave a line here -->";// just for leaving a line here.
      leavinLine += " </td></tr>\n";

      if (bActiveFrame2)
      {
         String button4rowSelect2 = fmt.drawButton("SELALL",mgr.translate("DOCMAWNEWREVISIONWIZARDSELALL: Select All"),"OnClick='selectUnselectAll(true);'");
	 button4rowSelect2 += "&nbsp;&nbsp;\n";
	 button4rowSelect2 += fmt.drawButton("UNSELALL",mgr.translate("DOCMAWNEWREVISIONWIZARDUNSELALL: Clear Selection"),"OnClick='selectUnselectAll(false);'");
	 button4rowSelect2 += "&nbsp;&nbsp;\n";
         button4rowSelect2 += fmt.drawButton("DEFAULTSELALL",mgr.translate("DOCMAWNEWREVISIONWIZARDDEFAULTSELALL: Select Default"),"OnClick='selectDefault();'");
	 
	 String innerTable2 = "<table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"100%\">\n";
	 innerTable2 += " <tr align=\"left\"><td>\n";
	 innerTable2 += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDSELOBJS: Select object(s) to be connected to the new revision:");
         innerTable2 += " </td></tr>\n";
	 
	 innerTable2 += " <tr align=\"left\"><td>\n";
	 innerTable2 += "&nbsp;&nbsp;&nbsp;&nbsp;"+fmt.drawReadLabel(wizHeader);
         innerTable2 += " </td></tr>\n";
	 
	 innerTable2 += " <tr><td>\n";
         innerTable2 +=item2lay.generateDataPresentation();
         innerTable2 +=" </td></tr>\n";
         innerTable2 +=" <tr>\n";
	 innerTable2 +="       <td><input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"";
	 innerTable2 +=selectedOldRevision;
	 innerTable2 +="\"></td>\n";
	 innerTable2 +="       <td><input type=\"hidden\" name=\"NEW_REV\" value=\"";
	 innerTable2 +=newRevision;
	 innerTable2 +="\"></td>\n";
	 innerTable2 +="       <td><input type=\"hidden\" name=\"JNOTE\" value=\"";
	 innerTable2 +=journalNote;
	 innerTable2 +="\"></td>\n";

         if (item2set.countRows() == 1)
	 {
	     	if ("1".equals(item2set.getValue("SURVEY_LOCKED_FLAG_DB")) && ("R".equals(item2set.getValue("KEEP_LAST_DOC_REV_DB")) || "F".equals(item2set.getValue("KEEP_LAST_DOC_REV_DB"))))
	       {
		     appendDirtyJavaScript("document.form.__SELECTED2.disabled = true;\n");
		     appendDirtyJavaScript("document.form.__SELECTED2.checked = false;\n");
		     lock_objects = true;
               }

	 }
         else
	 {
	    for (int x = 0; x < item2set.countRows(); x++)
	    {
	       if ("1".equals(item2set.getValue("SURVEY_LOCKED_FLAG_DB")) && ("R".equals(item2set.getValue("KEEP_LAST_DOC_REV_DB")) || "F".equals(item2set.getValue("KEEP_LAST_DOC_REV_DB"))))
	       {
		     appendDirtyJavaScript("document.form.__SELECTED2["+x+"].disabled = true;\n");
		     appendDirtyJavaScript("document.form.__SELECTED2["+x+"].checked = false;\n");
		     lock_objects = true;
               }
	       item2set.next();
	    }
	 }

	 if (item2set != null && item2set.countRows()>0) {
	     innerTable2 +="   </tr>\n";
	     innerTable2 +="<tr><td>&nbsp;&nbsp;"+button4rowSelect2+"</td></tr>\n";
	 }

         if (item2set != null && lock_objects) {
	     innerTable2 +="   </tr><tr align=\"left\"><td><div id='objectselectdiv' style='background-color:#FFCC66'>\n";
	     innerTable2 +="&nbsp;&nbsp;" + fmt.drawReadValue("DOCMAWNEWREVISIONLOCKOBJS: Selection of lock documents are disabled since they cannot be moved to other revisions.");
	     innerTable2 +="   </div></td></tr><tr><td>&nbsp;&nbsp;</td>\n";
	 }
	 innerTable2 +="</table>\n";
	 
	 
	 String button4step2 = fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWREVISIONWIZARDCANCELL: Cancel"),"");
	 button4step2 += "&nbsp;&nbsp;\n";
         button4step2 += fmt.drawSubmit("PREVIOUS",mgr.translate("DOCMAWNEWREVISIONWIZARDPREVIOUS: <Previous"),"");
	 button4step2 += "&nbsp;&nbsp;\n";
	 if (bNext2Available) {
	    button4step2 += fmt.drawSubmit("NXT2",mgr.translate("DOCMAWNEWREVISIONWIZARDNEXT:  Next> "),"");
	    button4step2 += "&nbsp;&nbsp;\n";
	 }
         button4step2 += fmt.drawSubmit("FINISH2",mgr.translate("DOCMAWNEWREVISIONWIZARDFINISHH: Finish"),"");

	 appendToHTML("<table cellspacing=0 cellpadding=0 >");
	 appendToHTML("<tr><td colspan=\"3\">"+item2bar.showBar()+"</td></tr>");
	 appendToHTML("<tr><td>&nbsp;&nbsp;</td><td>"+ innerTable2 +"</td><td>&nbsp;&nbsp;</td></tr>");
	 appendToHTML("<tr><td >&nbsp;&nbsp;</td><td align=\"right\">"+ button4step2+"</td><td>&nbsp;&nbsp;</td></tr>");
	 appendToHTML("</table>");
	 appendToHTML("  <input type=\"hidden\" name=\"NEXT2\" value=\"\">\n");
	
      }

      //Bug Id 69329, start
      //*********** Displays as Step 2 in wizard if the number of object connection exeeds 100****************************
      if (bActiveFrame4) {
	
	   String sRadioButton = ctx.readValue("RADIOBUTTON");

	   String innerTable4 = "<table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"100%\">\n";

	   innerTable4 += " <tr align=\"center\"><td>\n";
	   innerTable4 += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDOBJCON: Object Connections");
           innerTable4 += " </td></tr>\n";
	   innerTable4 += leavinLine;

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDOBJCONNOTE1: There are more connected objects than can be displayed in the wizard.");
	   innerTable4 += " </td></tr>\n";

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDOBJCONNOTE2: Use the radio buttons below to select how to treat the connected objects:");
	   innerTable4 += " </td></tr>\n";

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += "&nbsp;&nbsp;&nbsp;&nbsp;"+fmt.drawRadio("DOCMAWNEWREVISIONWIZARDOBJDEFAULT: Use Default", "DEFAULT", "DEFAULT", "DEFAULT".equals(sRadioButton), "OnClick='setRadioButton(\"DEFAULT\");'");
           innerTable4 += " </td></tr>\n";

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += "&nbsp;&nbsp;&nbsp;&nbsp;"+fmt.drawRadio("DOCMAWNEWREVISIONWIZARDOBJMOVEALL: Move All Object Connections", "MOVEALL", "MOVEALL", "MOVEALL".equals(sRadioButton), "OnClick='setRadioButton(\"MOVEALL\");'");
           innerTable4 += " </td></tr>\n";

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += "&nbsp;&nbsp;&nbsp;&nbsp;"+fmt.drawRadio("DOCMAWNEWREVISIONWIZARDOBJNOMOVE: Don't Move Connections", "NOMOVE", "NOMOVE", "NOMOVE".equals(sRadioButton), "OnClick='setRadioButton(\"NOMOVE\");'");
           innerTable4 += " </td></tr>\n";

	   innerTable4 += " <tr align=\"left\"><td>\n";
	   innerTable4 += " </td></tr>\n";
           
           innerTable4 += " <tr><td>\n";
           innerTable4 +=item4lay.generateDataPresentation();
           innerTable4 +=" </td></tr>\n";

	   innerTable4 +=" <tr>\n";
	   innerTable4 +="       <td><input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"";
	   innerTable4 +=selectedOldRevision;
	   innerTable4 +="\"></td>\n";
	   innerTable4 +="       <td><input type=\"hidden\" name=\"NEW_REV\" value=\"";
	   innerTable4 +=newRevision;
	   innerTable4 +="\"></td>\n";
	   innerTable4 +="       <td><input type=\"hidden\" name=\"JNOTE\" value=\"";
	   innerTable4 +=journalNote;
	   innerTable4 +="\"></td>\n";

	   innerTable4 +="</table>\n";

	   //outer table starts
           String button4step4 = fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWREVISIONWIZARDCANCELL: Cancel"),"");
	   button4step4 += "&nbsp;&nbsp;\n";
           button4step4 += fmt.drawSubmit("PREVIOUS",mgr.translate("DOCMAWNEWREVISIONWIZARDPREVIOUS: <Previous"),"");
	   button4step4 += "&nbsp;&nbsp;\n";
	   if (bNext2Available) 
	   {
	      button4step4 += fmt.drawSubmit("NXT2",mgr.translate("DOCMAWNEWREVISIONWIZARDNEXT:  Next> "),"");
	      button4step4 += "&nbsp;&nbsp;\n";
	   }
           button4step4 += fmt.drawSubmit("FINISH4",mgr.translate("DOCMAWNEWREVISIONWIZARDFINISHH: Finish"),"");

	   appendToHTML("<table cellspacing=0 cellpadding=0 >");
	   appendToHTML("<tr><td colspan=\"3\">"+item4bar.showBar()+"</td></tr>");
	   appendToHTML("<tr><td>&nbsp;&nbsp;</td><td>"+ innerTable4 +"</td><td>&nbsp;&nbsp;</td></tr>");
	   appendToHTML("<tr><td >&nbsp;&nbsp;</td><td align=\"right\">"+ button4step4+"</td><td>&nbsp;&nbsp;</td></tr>");
	   appendToHTML("</table>");
      }
      //Bug Id 69329,end

      if (bActiveFrame3) {
	
	   String button4rowSelect3 = fmt.drawButton("SELALL",mgr.translate("DOCMAWNEWREVISIONWIZARDSELALL: Select All"),"OnClick='selectUnselectAll(true);'");
	   button4rowSelect3 += "&nbsp;&nbsp;\n";
	   button4rowSelect3 += fmt.drawButton("UNSELALL",mgr.translate("DOCMAWNEWREVISIONWIZARDUNSELALL: Clear Selection"),"OnClick='selectUnselectAll(false);'");

	   String innerTable = "<table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"100%\">\n";
	   innerTable += " <tr align=\"center\"><td>\n";
	   innerTable += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDREPLACEOLD: Replace old revisions in structure with this one");
           innerTable += " </td></tr>\n";
	   innerTable += leavinLine;

	   innerTable += " <tr align=\"left\"><td>\n";
	   innerTable += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDPARENTDOCS: Parent Documents (where this document is used)");
           innerTable += " </td></tr>\n";

           innerTable += " <tr><td>\n";
           innerTable +=item3lay.generateDataPresentation();
           innerTable +=" </td></tr>\n";
           innerTable +=" <tr>\n";
	   innerTable +="       <td><input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"";
	   innerTable +=selectedOldRevision;
	   innerTable +="\"></td>\n";
	   innerTable +="       <td><input type=\"hidden\" name=\"NEW_REV\" value=\"";
	   innerTable +=newRevision;
	   innerTable +="\"></td>\n";
	   innerTable +="       <td><input type=\"hidden\" name=\"JNOTE\" value=\"";
	   innerTable +=journalNote;
	   innerTable +="\"></td>\n";
	   if (item3set != null && item3set.countRows()>0) {
	       innerTable +="   </tr>\n";
	       innerTable +="<tr><td>&nbsp;&nbsp;"+button4rowSelect3+"</td></tr>\n";
	   }
	   
	   //info: warn
	   innerTable += " <tr align=\"left\"><td>\n";
	   innerTable += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDREPLACEWARN: If you check the checkbox this new revision you are creating will replace the old one in the structure");
           innerTable += " </td></tr>\n";
	   innerTable += leavinLine; // need another line left blank !!
	   //info: Note
	   innerTable += " <tr align=\"left\"><td>\n";
	   innerTable += "&nbsp;&nbsp;"+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDNOTE: Note");
           innerTable += " </td></tr>\n";
	   //info: about Approve and Release
	   innerTable += " <tr align=\"left\"><td>\n";
	   innerTable += "&nbsp;&nbsp; - "+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDCANNOTREPLACE: You cannot replace a revision on a parent that is Released or Approved");
           innerTable += " </td></tr>\n";
	   //info: being able to replce latter.
	   innerTable += " <tr align=\"left\"><td>\n";
	   innerTable += "&nbsp;&nbsp; - "+fmt.drawReadLabel("DOCMAWNEWREVISIONWIZARDCANREPLACELATER: You can always replace the revision later(manually) if you change your mind");
           innerTable += " </td></tr>\n";

	   innerTable +="</table>\n";
	  //outer table starts
           String button4step3 = fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWREVISIONWIZARDCANCELL: Cancel"),"");
	   button4step3 += "&nbsp;&nbsp;\n";
           button4step3 += fmt.drawSubmit("PREVIOUS2",mgr.translate("DOCMAWNEWREVISIONWIZARDPREVIOUS: <Previous"),"");
	   button4step3 += "&nbsp;&nbsp;\n";
           button4step3 += fmt.drawSubmit("FINISH3",mgr.translate("DOCMAWNEWREVISIONWIZARDFINISHH: Finish"),"");

	   appendToHTML("<table cellspacing=0 cellpadding=0 >");
	   appendToHTML("<tr><td colspan=\"3\">"+item3bar.showBar()+"</td></tr>");
	   appendToHTML("<tr><td>&nbsp;&nbsp;</td><td>"+ innerTable +"</td><td>&nbsp;&nbsp;</td></tr>");
	   appendToHTML("<tr><td >&nbsp;&nbsp;</td><td align=\"right\">"+ button4step3+"</td><td>&nbsp;&nbsp;</td></tr>");
	   appendToHTML("</table>");
	  //outer table ends
	 // appendToHTML("row no = "+ item3set.countRows());
	






      }
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");

      appendDirtyJavaScript("function structureCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(bOriginalCopyStructure);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYSTRUCTURE.checked=false;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDNOOPTION: This option is not available!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function recordCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(bCopyRecord);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYRECORD.checked=false;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDNOOPTION: This option is not available!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function accCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(bOriginalCopyAccess);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYACCESS.checked=false;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDNOOPTION: This option is not available!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function fileCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(bOriginalCopyFile);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYFILE.checked=false;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDNOOPTION: This option is not available!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function surveyCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ('");
      appendDirtyJavaScript(bOriginalCopyAppRoute);
      appendDirtyJavaScript("'=='false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYDOCUMENTSURVEY.checked=false;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDNOOPTION: This option is not available!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function updateAllValues()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.DOC_REV_OLD.value==\"\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.DOC_REV_OLD.value = document.form.SELECTED_VALUE.value;\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDSELECTREVISION: You must select a revision!"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.SELECTED_VALUE.value = document.form.DOC_REV_OLD.value;\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkEmptyFields(str)\n");
      appendDirtyJavaScript("{\n");

      appendDirtyJavaScript(" if (document.form.CREATENEWTITLEREV_CHECK.checked==true)\n"); //NEW_TITLEREV NEW_TITLEREVNOTE
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.NEW_TITLEREV.value==\"\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(        mgr.translate("DOCMAWNEWREVISIONWIZARDREVTITLEFIELDREQVAL: Title Revision field requires a value"));
      appendDirtyJavaScript("       \");\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if (document.form.NEW_TITLEREVNOTE.value==\"\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(        mgr.translate("DOCMAWNEWREVISIONWIZARDREVTITLENOTEFIELDREQVAL: Title Revision Note field requires a value"));
      appendDirtyJavaScript("       \");\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if (((document.form.NEW_TITLEREV.value)+\"\").length > 6) \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.NEW_TITLEREV.value =  ((document.form.NEW_TITLEREV.value)+\"\").substring(0,6); \n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");

      appendDirtyJavaScript(" if (document.form.NEW_REV.value==\"\")\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDRFRAV: Revision field requires a value"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if (document.form.JOURNAL_NOTE.value==\"\")\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWNEWREVISIONWIZARDRTFRAV: Revision Text field requires a value"))));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (str==\"nxt\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.NEXT.value=\"NEXT\";\n");
      appendDirtyJavaScript("       submit();\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if (str==\"fin\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.FINISH1.value=\"FINISH1\";\n");
      appendDirtyJavaScript("       submit();\n");
      //appendDirtyJavaScript("        launchEdmMacro();\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");

      if (bActiveFrame3 ){
	  appendDirtyJavaScript("function selectUnselectAll(condition)\n");
	  appendDirtyJavaScript("{ // selects or unselects rows depending on condition\n");
	  appendDirtyJavaScript(" quit=false\n");
	  appendDirtyJavaScript(" x=0;\n");
	  appendDirtyJavaScript(" len=document.forms[0].elements.length;\n");
	  appendDirtyJavaScript(" do\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    if ((document.forms[0].elements[x].name==\"__SELECTED3\") || (x==len))\n");
	  appendDirtyJavaScript("    {\n");
	  appendDirtyJavaScript("       quit=true\n");
	  appendDirtyJavaScript("       break;\n");
	  appendDirtyJavaScript("    }\n");
	  appendDirtyJavaScript("    x++;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript(" while(quit==false)\n");
	  appendDirtyJavaScript(" quit=false\n");
	  appendDirtyJavaScript(" y=x;\n");
	  appendDirtyJavaScript(" do\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    if (document.forms[0].elements[y].name!=\"__SELECTED3\")\n");
	  appendDirtyJavaScript("    {\n");
	  appendDirtyJavaScript("       quit=true\n");
	  appendDirtyJavaScript("       break;\n");
	  appendDirtyJavaScript("    }\n");
	  appendDirtyJavaScript("    y++;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript(" while(quit==false)\n");
	  appendDirtyJavaScript(" for (z=x;z<y;z++)\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    document.forms[0].elements[z].checked=condition;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript("}\n");
      }

      //Bug Id 69329, stat
      if (bActiveFrame4 )
      {
         appendDirtyJavaScript("function setRadioButton(condition)\n");
         appendDirtyJavaScript("{ // selects or unselects radio buttons depending on condition\n");
         appendDirtyJavaScript("   if(condition == \"DEFAULT\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.DEFAULT.checked=true;\n");
         appendDirtyJavaScript("      document.form.MOVEALL.checked=false;\n");
         appendDirtyJavaScript("      document.form.NOMOVE.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if(condition == \"MOVEALL\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.MOVEALL.checked=true;\n");
         appendDirtyJavaScript("      document.form.DEFAULT.checked=false;\n");
         appendDirtyJavaScript("      document.form.NOMOVE.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   else if(condition == \"NOMOVE\")\n");
         appendDirtyJavaScript("   {\n");
	 appendDirtyJavaScript("      document.form.NOMOVE.checked=true;\n");
         appendDirtyJavaScript("      document.form.DEFAULT.checked=false;\n");
         appendDirtyJavaScript("      document.form.MOVEALL.checked=false;\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript(" }\n");
      }
      //Bug Id 69329, end

      if (bActiveFrame2)
      {
          appendDirtyJavaScript("function selectUnselectAll(condition)\n");
	  appendDirtyJavaScript("{ // selects or unselects rows depending on condition\n");
	  appendDirtyJavaScript(" quit=false\n");
	  appendDirtyJavaScript(" x=0;\n");
	  appendDirtyJavaScript(" len=document.form.elements.length;\n");
	  appendDirtyJavaScript(" do\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    if ((document.form.elements[x].name==\"__SELECTED2\") || (x==len))\n");
	  appendDirtyJavaScript("    {\n");
	  appendDirtyJavaScript("       quit=true\n");
	  appendDirtyJavaScript("       break;\n");
	  appendDirtyJavaScript("    }\n");
	  appendDirtyJavaScript("    x++;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript(" while(quit==false)\n");
	  appendDirtyJavaScript(" quit=false\n");
	  appendDirtyJavaScript(" y=x;\n");
	  appendDirtyJavaScript(" do\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    if (document.form.elements[y].name!=\"__SELECTED2\")\n");
	  appendDirtyJavaScript("    {\n");
	  appendDirtyJavaScript("       quit=true\n");
	  appendDirtyJavaScript("       break;\n");
	  appendDirtyJavaScript("    }\n");
	  appendDirtyJavaScript("    y++;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript(" while(quit==false)\n");
	  appendDirtyJavaScript(" for (z=x;z<y;z++)\n");
	  appendDirtyJavaScript(" {\n");
	  appendDirtyJavaScript("    if (document.form.elements[z].disabled == false)\n");
	  appendDirtyJavaScript("       document.form.elements[z].checked=condition;\n");
	  appendDirtyJavaScript(" }\n");
	  appendDirtyJavaScript("}\n");


         if ("".equals(selectedObjects))
            appendDirtyJavaScript("selectDefault();");

         appendDirtyJavaScript("function selectDefault()\n");
         appendDirtyJavaScript("{ // reselects rows that are checked by default\n");
         appendDirtyJavaScript(" var selectDefaultArr=new Array();\n");

         selectDefaultObjectsInToArray(); //This is needed for "Select Default" button in frame 2

         int x;
         for (x=0;x<selectDefaultArr.size();x++)
            appendDirtyJavaScript("selectDefaultArr[" + x + "]=" + selectDefaultArr.elementAt(x) + "\n");

         appendDirtyJavaScript("   if (selectDefaultArr.length==0)\n");
         appendDirtyJavaScript("    return;\n");
         appendDirtyJavaScript("   quit=false\n");
         appendDirtyJavaScript(" x=0;\n");
         appendDirtyJavaScript(" do\n");
         appendDirtyJavaScript(" {\n");
         appendDirtyJavaScript("    if (document.form.elements[x].name==\"__SELECTED2\")\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("       quit=true\n");
         appendDirtyJavaScript("       break;\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    x++;\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript(" while(quit==false)\n");
         appendDirtyJavaScript(" quit=false\n");
         appendDirtyJavaScript(" y=x;\n");
         appendDirtyJavaScript(" do\n");
         appendDirtyJavaScript(" {\n");
         appendDirtyJavaScript("    if (document.form.elements[y].name!=\"__SELECTED2\")\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("       quit=true\n");
         appendDirtyJavaScript("       break;\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("    y++;\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript(" while(quit==false)\n");
         appendDirtyJavaScript(" q=0;\n");
         appendDirtyJavaScript(" for (z=x;z<y;z++)\n");
         appendDirtyJavaScript(" {\n");
         appendDirtyJavaScript("    if (selectDefaultArr[q]==1 && document.form.elements[z].disabled == false)\n");
         appendDirtyJavaScript("       document.form.elements[z].checked=true;\n");
         appendDirtyJavaScript("    else\n");
         appendDirtyJavaScript("       document.form.elements[z].checked=false;\n");
         appendDirtyJavaScript("    q++;\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript("}\n");
      }

      appendDirtyJavaScript("function selectAllRows(s)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  selectUnselectAll(true);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function invertselection(s)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" quit=false\n");
      appendDirtyJavaScript(" x=0;\n");
      appendDirtyJavaScript(" len=document.form.elements.length;\n");
      appendDirtyJavaScript(" do\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if ((document.form.elements[x].name==\"__SELECTED2\") || (x==len))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       quit=true\n");
      appendDirtyJavaScript("       break;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    x++;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" while(quit==false)\n");
      appendDirtyJavaScript(" quit=false\n");
      appendDirtyJavaScript(" y=x;\n");
      appendDirtyJavaScript(" do\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.elements[y].name!=\"__SELECTED2\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       quit=true\n");
      appendDirtyJavaScript("       break;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    y++;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" while(quit==false)\n");
      appendDirtyJavaScript(" if (x==y)\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.elements.checked == false)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if (document.form.elements.disabled == false)\n");
      appendDirtyJavaScript("          document.form.elements.checked=true;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       document.form.elements.checked=false;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    for (z=x;z<y;z++)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if (document.form.elements[z].checked == false)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          if (document.form.elements[z].disabled == false)\n");
      appendDirtyJavaScript("             document.form.elements[z].checked=true;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          document.form.elements[z].checked=false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");

      if (bTranferToEDM)
      {
         appendDirtyJavaScript("launchEdmMacro();\n");
         appendDirtyJavaScript("function launchEdmMacro()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(sFilePath);
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function refreshParent()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("       document.form.TRANSFERTOCALLINGURL.value=\"TRUE\";\n");
         appendDirtyJavaScript("       submit();\n");
         appendDirtyJavaScript("}\n");
      }

      if (bCloseWindow)
      {
         //Encode the values in case they contain special characters
         appendDirtyJavaScript("  var openerwin=eval(\"opener.getPageName()\");\n");
         appendDirtyJavaScript(" if (openerwin==\"docissue\")\n");
         appendDirtyJavaScript(" {\n");
         appendDirtyJavaScript("  eval(\"opener.addNewRow('"+mgr.URLEncode(doc_class)+"','"+mgr.URLEncode(doc_no)+"','"+mgr.URLEncode(doc_sheet)+"','"+mgr.URLEncode(sNewRevision)+"')\");\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript("else {\n");
         appendDirtyJavaScript("  if (openerwin==\"docissuegeneral\")\n");
         appendDirtyJavaScript("  {\n");
         if(breobject)
         {
            appendDirtyJavaScript("      eval(\"opener.refeshParent('"+mgr.URLEncode(doc_class)+"','"+mgr.URLEncode(doc_no)+"','"+mgr.URLEncode(doc_sheet)+"','"+mgr.URLEncode(sNewRevision)+"')\");\n");
            appendDirtyJavaScript("      eval(\"opener.refreshTree()\");\n");
         }
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("  window.close();\n");
      }

      if (bCancel)
         appendDirtyJavaScript("  window.close();\n");
   }
}
