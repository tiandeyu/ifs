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
*  File        : DocumentDistributionHistory.java
*  Modified    :
*
*    2001-05-29   Diaklk   Call Id 64971 : Added method Copy File To..., View Document with Ext. App
*    2002-12-30   Dikalk   2002-2 SP3 Merge: "Larelk 2002-09-18 - Bug 31903 Increased the field length in sender_person,receiver-person"
*    2003-01-01   nisilk   Added Doc Sheet
*    2003-07-11   DhPelk   Removed call to Finite_State_Machine___ in defineCommand
*    2003-08-01   NiSilk   Fixed call 95769, modified methods adjust() and run().
*    16/09/2003   Thwilk   Call ID 103379 : Modified field lengths in predefine().
*    2003-08-16   Bakalk   Call Id 108451 : Modified resendDistribution().
*    2003-12-17   Bakalk   Web Alingment done.
*    2004-02-24   DIKALK   Reviewed, refactored and cleaned this page. (more things can still
*                          be done though)
*    2004-02-24   DIKALK   Call 112746. Ensuring rows have been selected before performing
*                          multirow operations
*    2005-03-29   SUKMLK   Merged Bug 49701
*    2006-02-02   KARALK   call 132059 new methods  editHistroy(), initializeSession(), getAppOwner(), getFndUser(), getPersonId().
*    2006-05-25   DIKALK   Bug 57779, Replaced use of appowner with Docman Administrator
*    2007-08-10   NaLrlk   XSS Correction.
*    2007-08-15   ASSALK   Merged Bug 65659, Added search_url.
*    2007-08-20   ASSALK   Merged Bug 64508, Added dynamicLOV to DOC_CLASS, RECEIVER_PERSON and SENDER_PERSON. Added Titles to LOVs of DOC_CLASS, RECEIVER_PERSON and SENDER_PERSON. 
*    2008-04-15   SHTHLK   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;


public class DocumentDistributionHistory extends ASPPageProvider
{

   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentDistributionHistory");


   //
   // Instances created on page creation (immutable attributes)
   //
   private ASPContext ctx;
   private ASPLog log;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;
   private ASPField f;
   private ASPTable headtbl;
   private ASPBlock  dummyblk;


   //
   // Transient temporary variables (never cloned)
   //
   private ASPTransactionBuffer trans;
   private boolean bTranferToEDM;
   private String sDistributed;
   private String sApproved;
   private String sRead;
   private String sHardcopyDist;
   private String sFilePath;
   private ASPCommand cmd;

   private String search_url;

   //
   // Construction
   //
   public DocumentDistributionHistory(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      bTranferToEDM   = false;
      sDistributed   = null;
      sApproved   = null;
      sRead   = null;
      search_url = null;
      sHardcopyDist   = null;
      sFilePath   = null;

      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentDistributionHistory page = (DocumentDistributionHistory)(super.clone(obj));

      //Initializing mutable attributes
      page.trans   = null;
      page.bTranferToEDM   = false;
      page.sDistributed   = null;
      page.sApproved   = null;
      page.sRead   = null;
      page.sHardcopyDist   = null;

      //Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.log = page.getASPLog();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headlay = page.headblk.getASPBlockLayout();
      page.f = page.getASPField(f.getName());
      page.headtbl = page.getASPTable(headtbl.getName());
      page.sFilePath   = null;
      page.search_url = null;

      return page;
   }


   public void adjust()
   {
      if (headset.countRows() ==0)
      {
         debug("calling adjust");
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      bTranferToEDM = false;
      log = mgr.getASPLog();
      sDistributed = ctx.readValue("DISTRIBUTED", "");
      sApproved = ctx.readValue("APPROVED", "");
      sRead = ctx.readValue("READ", "");
      sHardcopyDist = ctx.readValue("HARDCOPYDIST", "");

      initializeSession();

      if ((mgr.isEmpty(sDistributed))||(mgr.isEmpty(sApproved))||(mgr.isEmpty(sRead))||(mgr.isEmpty(sHardcopyDist)))
         getStateVals();
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else
         headlay.setLayoutMode(headlay.FIND_LAYOUT);

      adjust();
      headset.unselectRows();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      search_url = mgr.createSearchURL(headblk);

      trans.clear();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNODATAFOUND: No data found"));
         headset.clear();
      }
   }

   public void editHistroy()
   { 
       ASPManager mgr = getASPManager();

       String fnd_user = getFndUser();
       // Bug 57779, Start
       boolean docman_admin = isUserDocmanAdministrator();
       // Bug 57779, End

       int currentRow= headset.getRowSelected(); 
       headset.goTo(currentRow);      
       
       
       // Bug 57779, Start
       if((headset.getRow().getValue("RECEIVER_PERSON").equals(fnd_user)) || docman_admin)
       // Bug 57779, End
       {
           if (headset.getRow().getValue("STATE").equals("Approved"))
           {               
               mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYAPPROVED: You cannot edit Approved Document Distribution History"));
               return;
           }
           else
           {
               headlay.setLayoutMode(headlay.EDIT_LAYOUT);

           }
       }
       else
       {
           mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOEDITACCESS: You do not have access to edit this document distribution history"));
           return;
       }
 
   }

   private void initializeSession() throws FndException
   {
      ASPManager mgr = getASPManager();
      

      String initialised = ctx.readValue("INITIALISED", null);
      String fnd_user  = ctx.readValue("FNDUSER", null);
      // Bug 57779, Start
      String person_id = ctx.readValue("PERSONID",null);
      boolean docman_admin = ctx.readFlag("DOCMAN_ADMIN", false);      
      // Bug 57779, End

      if (mgr.isEmpty(initialised))
      {
         trans.clear();
         cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_Api.Get_Fnd_User", "DUMMY1");
         // Bug 57779, Start
         cmd = trans.addCustomFunction("DOCMAN_ADMIN", "Docman_Security_Util_API.Check_Docman_Administrator", "DUMMY1");
         // Bug 57779, End
         cmd = trans.addCustomFunction("PERSONID","Person_Info_API.Get_Id_For_User","DUMMY1");
         cmd.addParameter("USER_ID", fnd_user);

         
         trans = mgr.perform(trans);

         fnd_user  = trans.getValue("FNDUSER/DATA/DUMMY1");
         // Bug 57779, Start
         docman_admin = "TRUE".equals(trans.getValue("DOCMAN_ADMIN/DATA/DUMMY1"));
         // Bug 57779, End
         person_id = trans.getValue("PERSONID/DATA/DUMMY1");

         
      }

      ctx.writeValue("FNDUSER", fnd_user);
      ctx.writeValue("PERSONID", person_id);
      // Bug 57779, Start
      ctx.writeFlag("DOCMAN_ADMIN", docman_admin);      
      // Bug 57779, End
   }

   private String  getFndUser()
   {
      return ctx.readValue("FNDUSER", null);
   }


   // Bug 57779, Start
   private boolean isUserDocmanAdministrator()
   {
      return ctx.readFlag("DOCMAN_ADMIN", false);
   }
   // Bug 57779, End

   private String getPersonId()
   {
      return ctx.readValue("PERSONID",null);
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,headblk);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }


   public void cancelFind()
   {
      debug("calling cancel find");
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }


   public void  getStateVals()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      String txt = "select DOC_DIST_LIST_HISTORY_API.Finite_State_Decode__('Distributed') DISTRIBUTED,";
      txt += "DOC_DIST_LIST_HISTORY_API.Finite_State_Decode__('Approved') APPROVED,";
      txt += "DOC_DIST_LIST_HISTORY_API.Finite_State_Decode__('Read') READ,";
      txt += "DOC_DIST_LIST_HISTORY_API.Finite_State_Decode__('Hardcopy Distribution') HARDCOPYDIST from DUAL";

      trans.addQuery("GETSTATEVAL", txt);
      trans = mgr.perform(trans);

      sDistributed  = trans.getValue("GETSTATEVAL/DATA/DISTRIBUTED");
      sApproved = trans.getValue("GETSTATEVAL/DATA/APPROVED");
      sRead  = trans.getValue("GETSTATEVAL/DATA/READ");
      sHardcopyDist  = trans.getValue("GETSTATEVAL/DATA/HARDCOPYDIST");

      trans.clear();
   }


   public void  distribute()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      changeState("Distribute__");
   }


   public void  changeState(String state)
   {
      ASPManager mgr = getASPManager();

      int currrow = 0;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
      }
      else
      {
         headset.selectRow();
         currrow = headset.getCurrentRowNo();
      }

      headset.setFilterOn();
      int index = 0;
      boolean stateNotNullAll = true;

      for (index=0;index<headset.countSelectedRows();index++)
      {
         if (mgr.isEmpty(headset.getRow().getValue("STATE")))
         {
            stateNotNullAll = false;
            break;
         }
         headset.next();
      }

      if (stateNotNullAll)
      {
         headset.markSelectedRows(state);

         mgr.submit(trans);
         if (headlay.isSingleLayout())
         {
            headset.goTo(currrow);
         }

         headset.unselectRows();
      }
      else
      {
         if (headset.countSelectedRows()==1)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYCANNOTAPPINP: The requested operation is not allowed for objects when state is null"));
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYCANNOTAPPINPWITHNO: The requested operation is not allowed, since state of record &1 in selected rows is null.",(index+"")));
         }
      }
      headset.setFilterOff();
   }


   public void  approve()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      this.changeState("Approve__");
   }


   public void  markAsRead()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      changeState("Mark_As_Read__");
   }


   public void  resendDistribution()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
      }
      else
      {
         headset.selectRow();
      }

      headset.setFilterOn();
      trans.clear();
      for (int k = 0;k < headset.countSelectedRows();k++)
      {
         ASPCommand cmd = trans.addCustomCommand ("RESEND_DIST"+k, "DOC_DIST_ENGINE_API.Resend_Distribution");
         cmd.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getValue("DOC_REV"));
         cmd.addParameter("RECEIVER_PERSON",headset.getValue("RECEIVER_PERSON"));
         headset.next();
      }
      trans = mgr.perform(trans);
      headset.setFilterOff();
      headset.unselectRows();
   }


   public void  viewOriginal()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      transferToEdmMacro("ORIGINAL","VIEW");
   }


   public void  printDocument()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      transferToEdmMacro("ORIGINAL","PRINT");
   }


   public void copyFileTo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      transferToEdmMacro("ORIGINAL","GETCOPYTODIR");
   }



   public void  viewOriginalWithExternalViewer()
   {
      transferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
   }


   public void  documentInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNOROWS: No Rows Selected."));
         return;
      }

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
      }
      else
      {
         headset.selectRow();
      }

      headset.setFilterOn();
      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      mgr.transferDataTo("DocIssue.page", keys);
   }


   public void transferToEdmMacro(String doc_type, String action)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.selectRows();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
      bTranferToEDM = true;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
              setHidden();

      headblk.addField("OBJVERSION").
              setHidden();

      headblk.addField("OBJSTATE").
              setHidden();

      headblk.addField("OBJEVENTS").
              setHidden();

      headblk.addField("DOC_CLASS").
              setSize(20).
              setMaxLength(12).
              setMandatory().
              setDynamicLOV("DOC_CLASS").
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCCLASS: Doc Class");

      headblk.addField("DOC_NO").
              setSize(20).
              setMaxLength(120).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCNO: Doc No");

      headblk.addField("DOC_SHEET").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setDynamicLOV("DOC_ISSUE_LOV1").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCSHEET: Doc Sheet");

      headblk.addField("DOC_REV").
              setSize(20).
              setMaxLength(6).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCREV: Doc Rev");

      headblk.addField("TITLE").
              setSize(20).
              setMaxLength(250).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYTITLEFIELD: Doc Title");

      headblk.addField("RECEIVER_PERSON").
              setSize(20).
              setMaxLength(30).
              setMandatory().
              setDynamicLOV("DOC_DIST_PERSON").
              setLOVProperty("TITLE","DOCMAWDOCUMENTDISTRIBUTIONHISTORYRECEIVERPERSONLOV: Receiver Person").
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYRECEIVERPERSON: Receiver Person");

      headblk.addField("RECEIVER_PERSON_NAME").
              setSize(20).
              setMaxLength(250).
              setMandatory().
              setDynamicLOV("DOC_DIST_PERSON").
              setLOVProperty("TITLE","DOCMAWDOCUMENTDISTRIBUTIONHISTORYRECEIVERPERSONLOV: Receiver Person").
              setReadOnly().
              setFunction("DOC_DIST_PERSON_API.Get_Name(:RECEIVER_PERSON)").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYRECEIVERPERSONNAME: Receiver Name");

      headblk.addField("DATE_LOGGED","Date").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDATELOGGED: Date Logged");

      headblk.addField("STATE").
              setSize(20).
              setMaxLength(253).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYSTATE: State");

      headblk.addField("STATE_DB").
              setHidden().
              setFunction("DOC_DIST_LIST_HISTORY_API.Finite_State_Encode__(:STATE)");

      headblk.addField("SENDER_PERSON").
              setSize(20).
              setMaxLength(30).
              setMandatory().
              setDynamicLOV("DOC_DIST_PERSON").
              setLOVProperty("TITLE","DOCMAWDOCUMENTDISTRIBUTIONHISTORYSENDERPERSONLOV: Sender Person").
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYSENDERPERSON: Sender Person");

      headblk.addField("SENDER_PERSON_NAME").
              setSize(20).
              setMaxLength(250).
              setMandatory().
              setReadOnly().
              setFunction("DOC_DIST_PERSON_API.Get_Name(:SENDER_PERSON)").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYSENDERPERSONNAME: Sender Name");

      headblk.addField("DATE_DISTRIBUTED","Date").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDATEDISTRIBUTED: Date Distributed");

      headblk.addField("FILE_TYPE").
              setSize(20).
              setMaxLength(18).
              setReadOnly().
              setFunction("EDM_FILE_API.Get_File_Type(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,'ORIGINAL')").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYFILETYPE: File Type");

      headblk.addField("DATE_RECEIVED","Date").
              setSize(20).
              setMaxLength(10).
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDATERECEIVED: Date Received");

      headblk.addField("RECEIVER_NOTE").
              setSize(20).
              setMaxLength(2000).
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYRECEIVERNOTE: Receiver Note");

      headblk.addField("DISTRIBUTION_NOTE").
              setSize(20).
              setMaxLength(2000).
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDISTRIBUTIONNOTE: Distribution Note");

      headblk.addField("DOC_DIST_TYPE").
              setSize(20).
              setMaxLength(200).
              setMandatory().
              setReadOnly().
              setSelectBox().
              enumerateValues("DOC_DIST_TYPE_API").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCDISTTYPE: Doc Dist Type");

      headblk.addField("DOC_DIST_METHOD").
              setSize(20).
              setMaxLength(200).
              setMandatory().
              setReadOnly().
              setSelectBox().
              enumerateValues("DOC_DIST_METHOD_API").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCDISTMETHOD: Doc Dist Method");

      headblk.addField("DOC_DIST_TRIGGER_COND").
              setSize(20).
              setMaxLength(200).
              setMandatory().
              setReadOnly().
              setSelectBox().
              enumerateValues("DOC_DIST_TRIGGER_COND_API").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCDISTTRIGGERCOND: Doc Dist Trigger Cond");

      headblk.addField("DOC_DIST_PRIORITY").
              setSize(20).
              setMaxLength(200).
              setMandatory().
              setReadOnly().
              setSelectBox().
              enumerateValues("DOC_DIST_PRIORITY_API").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCDISTPRIORITY: Doc Dist Priority");

      headblk.addField("NUMBER_OF_COPIES").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYNUMBEROFCOPIES: No of Copies");

      headblk.addField("BASED_ON_DISTRIBUTION_NO").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYBASEDONDISTRIBUTIONNO: Based on Distribution No");

      headblk.addField("BASED_ON_LINE_NO").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setReadOnly().
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYBASEDONLINENO: Based on Line No");

      headblk.addField("EDM_INFORMATION").
              setSize(20).
              setMaxLength(250).
              setMandatory().
              setReadOnly().
              setHidden().
              setFunction("EDM_FILE_API.Get_Edm_Information(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,'ORIGINAL')").
              setLabel("DOCMAWDOCUMENTDISTRIBUTIONHISTORYEDMINFORMATION: Edm Information");

      headblk.setView("DOC_DIST_LIST_HISTORY");
      headblk.defineCommand("DOC_DIST_LIST_HISTORY_API","New__,Modify__,Remove__,Distribute__,Approve__,Mark_As_Read__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.OKFIND,"okFind");
      headbar.defineCommand(headbar.CANCELFIND,"cancelFind");
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DELETE);

      headbar.addSecureCustomCommand("editHistroy" , headbar.EDIT,"DOC_DIST_LIST_HISTORY_API.Modify__"); //Bug Id 70286

      headbar.addSecureCustomCommand("distribute",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDISTRIBUTE: Distribute"),"DOC_DIST_LIST_HISTORY_API.Distribute__");//Bug Id 70286
      headbar.addSecureCustomCommand("approve",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYAPPROVE: Approve"),"DOC_DIST_LIST_HISTORY_API.Approve__");//Bug Id 70286
      headbar.addSecureCustomCommand("markAsRead",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYMARKASREAD: Mark as Read"),"DOC_DIST_LIST_HISTORY_API.Mark_As_Read__");//Bug Id 70286
      headbar.addSecureCustomCommand("resendDistribution",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYRESENDDISTRIBUTION: Resend Distribution"),"DOC_DIST_ENGINE_API.Resend_Distribution");//Bug Id 70286
      headbar.addCustomCommand("documentInfo",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCUMENTINFO: Document Info..."));

      headbar.addCustomCommandSeparator();
      headbar.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("copyFileTo",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      
      headbar.addCommandValidConditions("distribute","STATE_DB","Disable","Approved;Distributed;Read");
      headbar.addCommandValidConditions("approve","STATE_DB","Disable","Approved;Hardcopy Distribution");
      headbar.addCommandValidConditions("markAsRead","STATE_DB","Disable","Approved;Hardcopy Distribution;Read");

      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONHISTORYDOCDISTHIST: Document Distribution History"));
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      //
      //  DUMMY BLOCK
      //

      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("USER_ID");

   }


   protected String getDescription()
   {
      return "DOCMAWDOCUMENTDISTRIBUTIONHISTORYTITLE: Overview - Document Distribution History";
   }


   protected String getTitle()
   {
      return "DOCMAWDOCUMENTDISTRIBUTIONHISTORYTITLE: Overview - Document Distribution History";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());


      //
      // CLIENT FUNCTIONS
      //

      // Tranfer to EdmMacro.page file
      if (bTranferToEDM) {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }

      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("}\n");
   }

}
