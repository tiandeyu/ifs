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
* File                   : CopyTitleWizard.java
* Description            : Copies titles.
* Called by              : DocTitleOvw.page
* Files Called           :
* ----------------------------------------------------------------------------
* Date        Sign              History
* ----------  ------   ----------------------------------------------
* 2002-11-12  Bakalk   Created.
* 2002-11-28  Bakalk   Extended DocSrv instead of ASPPageProvider in order to avoid calling EdmMacro for
                       copying files.
* 2002-12-23  Nisilk   Call Id:92245  - Added some code in methods prepareDialog and in AutoString getContents
                       to get the default Title reviion and to get the Document Title in the text fields of the Wizard
* 2003-04-09  Bakalk   Made some chages in run ,finsh and sendBack.
* 2003-06-17  NiSilk   Added a statement in method finish() to get the new_doc_no which the user has entered.
* 2003-07-08  InoSlk   Removed unnecessary DB calls in hasPermission and prepareDialog.
* 2003-08-27  InoSlk   Call ID 101731: Changes in doReset() and clone().
* 2003-08-27  InoSlk   Removed unneccessarry variables.
q 2003-09-16  Japalk   The label "Copy Latest Issues" was changed to "Copy Latest Sheet Revision(s)"
* 2003-09-17  Japalk   Call ID 102898 . Three fields were set as mandatory.
* 2003-10-07  NiSilk   Added some validations for Number Generator support
* 2003-10-13  InoSlk   Call ID 106322 - Modified methods run(),getContents(),validate(),prepareDialog() & finish().
* 2003-10-15  Inoslk   Call ID 106375 - Renamed the label "Copy Latest Sheet Revision(s)" to "Copy Document Data",
*                      added code to hide the copy options check boxes whem master unchecked in getContents().
* 2003-10-16  Inoslk   Call ID 106322 - Reflected LU name change from Doc_Def_Values_API to Doc_Number_Generator_Type_API.
* 2003-10-21  InoSlk   Call ID 103257 - Modified method finish().
* 2003-10-21  NiSilk   Call ID 106561 - Modified the field order of the wizard.
* 2003-10-27  InoSlk   Call ID 103257 - Modified method finish().
* 2003-12-03  BAKALK   Layout modifications done.
* 2004-03-23  DIKALK   SP1-Merge - Bug ID 40574. Set 'ID1' readOnly and removed the LOV reference.
* 2004-03-23  DIKALK   Merged Bud Id 44412
* 2004-03-23  DIKALK   Merged Bud Id 44620
* 2004-06-29  BAKALK   Merged LCS patch <44554>.
* 2004-10-18  DIKALK   Merged Bud Id 45870
* 2004-11-02  SUKMLK   Fixed null value being passed into ID2 prepareDialog()
* 2006-01-25  MDAHSE   Added throws FndException to preDefine().
* 2006-07-14  Bakalk   Fixed Bug 58214, fixing security issues regarding HTML/Script errors.
* 20070808    ILSOLK   XSS Correction.
* 2007-08-15  ASSALK   Merged Bug 58526, Modified getContents(). 
* 2008-03-13  VIRALK   Bug ID 71130, Text changes in UI. 
* 2008-09-29  SHTHLK   Bug Id 74966, Added javascript method to handle validation of Doc_Class, to disable lovs 
* -----------------------APP7.5 SP1---------------------------------------------------
* 2007-12-12  ILSOLK   Bug Id 68773, Eliminated XSS.
* -----------------------APP7.5 SP7---------------------------------------------------
* 2010-07-21  ARWILK   Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
* 2010-08-02  AMNALK   Bug Id 92140, Added code to support check box 'Copy Object Connections' and to function all other check boxes properly.
* ------------------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CopyTitleWizard extends DocSrv
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.CopyTitleWizard");

   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter   fmt;
   private ASPLog             log;
   private ASPContext         ctx;
   private ASPBlock       headblk;
   private ASPRowSet      headset;
   private ASPTable       headtbl;
   private ASPBlockLayout headlay;
   private ASPCommandBar  headbar;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPBuffer  rev_select_buff;
   private ASPBuffer             keys;
   private ASPBuffer             buff;
   private ASPCommand             cmd;
   private ASPTransactionBuffer trans;

   private boolean currentStep;
   private boolean start;
   private boolean structure_checkable;
   private boolean file_checkable;
   private boolean access_checkable;
   private boolean method_checkable;
   private boolean docsurvey_checkable;
   private boolean structure_val;
   private boolean file_val;
   private boolean access_val;
   private boolean revision_val;
   private boolean state_val;
   private boolean copyapp_val;
   private boolean rec_val;
   private boolean activeFrame1;
   private boolean multiTransfer;
   private boolean bTranferToEDM;
   private boolean bFirtPage;
   // Bug Id 92140, start
   private boolean copyobj_val; 
   private String hasStructure;
   private String hasFile;
   private String hasAppRoute;
   private String hasObjCon;
   // Bug Id 92140, end

   private String meth_val;
   private String s_val;
   private String url;
   private String doc_class;
   private String doc_no;
   private String dclass;
   private String dno;
   private String revisions;
   private String rev;
   private String docu_class;
   private String method_val;
   private String selectedRows;
   private String title;
   private String d_no;
   private String sFilePath;
   private String sFirstRev;
   private String numberGenerator;
   private String id1;
   private String id2;
   private String numberGeneratorDb;

   private int noTransferRows;
   private int currentRow;
   private int i;

   //===============================================================
   // Construction
   //===============================================================
   public CopyTitleWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      rev_select_buff      = null;
      keys                 = null;
      buff                 = null;
      trans                = null;
      cmd                  = null;

      meth_val             = null;
      s_val                = null;
      url                  = null;
      doc_class            = null;
      doc_no               = null;
      dclass               = null;
      dno                  = null;
      revisions            = null;
      rev                  = null;
      docu_class           = null;
      method_val           = null;
      selectedRows         = null;
      title                = null;
      d_no                 = null;
      sFilePath            = null;
      sFirstRev            = null;
      numberGenerator      = null;
      id1                  = null;
      id2                  = null;
      numberGeneratorDb    = null;

      multiTransfer       = false;
      start               = false;
      structure_checkable = false;
      file_checkable      = false;
      access_checkable    = false;
      method_checkable    = false;
      docsurvey_checkable = false;
      structure_val       = false;
      file_val            = false;
      access_val          = false;
      revision_val        = false;
      state_val           = false;
      copyapp_val         = false;
      rec_val             = false;
      activeFrame1        = false;
      currentStep         = false;
      bTranferToEDM       = false;
      bFirtPage           = false;
      copyobj_val         = false; // Bug Id 92140

      noTransferRows = 0;
      currentRow     = 0;
      i              = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      CopyTitleWizard page = (CopyTitleWizard)(super.clone(obj));

      // Initializing mutable attributes
      page.rev_select_buff      = null;
      page.keys                 = null;
      page.buff                 = null;
      page.cmd                  = null;
      page.trans                = null;

      page.meth_val             = null;
      page.s_val                = null;
      page.url                  = null;
      page.doc_class            = null;
      page.dclass               = null;
      page.doc_no               = null;
      page.dno                  = null;
      page.revisions            = null;
      page.rev                  = null;
      page.docu_class           = null;
      page.method_val           = null;
      page.selectedRows         = null;
      page.title                = null;
      page.d_no                 = null;
      page.sFilePath            = null;
      page.sFirstRev            = null;
      page.numberGenerator      = null;
      page.id1                  = null;
      page.id2                  = null;
      page.numberGeneratorDb    = null;

      page.currentStep         = false;
      page.start               = false;
      page.structure_checkable = false;
      page.file_checkable      = false;
      page.access_checkable    = false;
      page.method_checkable    = false;
      page.docsurvey_checkable = false;
      page.structure_val       = false;
      page.file_val            = false;
      page.access_val          = false;
      page.revision_val        = false;
      page.state_val           = false;
      page.copyapp_val         = false;
      page.rec_val             = false;
      page.activeFrame1        = false;
      page.multiTransfer       = false;
      page.bTranferToEDM       = false;
      page.bFirtPage           = false;
      page.copyobj_val         = false; // Bug Id 92140

      page.i              = 0;
      page.noTransferRows = 0;
      page.currentRow     = 0;

      // Cloning immutable attributes
      page.fmt     = page.getASPHTMLFormatter();
      page.log     = page.getASPLog();
      page.ctx     = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();

      return page;
   }

   public void run()
   {
      meth_val = "";
      s_val     = "";

      ASPManager mgr = getASPManager();


      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();

      log = mgr.getASPLog();
      ctx = mgr.getASPContext();

      url         = ctx.readValue("SEND__URL","");
      doc_class   = ctx.readValue("DOC_CLASS","");
      doc_no      = ctx.readValue("DOC_NO","");
      rev         = ctx.readValue("REV");
      dclass      = ctx.readValue("DOCCLASS");
      dno         = ctx.readValue("DOCNO");
      revisions   = ctx.readValue("REVISIONS","");

      currentStep = ctx.readFlag("STEP_NO",false);
      start       = ctx.readFlag("START",false);

      if (!mgr.isEmpty(mgr.getQueryStringValue("SENDBACK")))
      {
         sendBack(url,doc_class,doc_no);
      }


      // ctx vars used for checkboxes
      structure_checkable  = ctx.readFlag("STRUCT_CHECKABLE",true);
      file_checkable       = ctx.readFlag("FILE_CHECKABLE",true);
      access_checkable     = ctx.readFlag("ACCESS_CHECKABLE",true);
      method_checkable     = ctx.readFlag("METHOD_CHECKABLE",true);
      docsurvey_checkable  = ctx.readFlag("SURVEY_CHECKABLE",true);
      structure_val        = ctx.readFlag("STRUCTURE_VAL",true);
      file_val             = ctx.readFlag("FIL_VAL",true);
      access_val           = ctx.readFlag("ACC_VAL",true);
      revision_val         = ctx.readFlag("REV_VAL",false);
      state_val            = ctx.readFlag("STATE_VAL",false);
      copyapp_val          = ctx.readFlag("SURVEY_VAL",true);
      rec_val              = ctx.readFlag("REC_VAL",true);
      activeFrame1         = ctx.readFlag("ACTIVEFRAME1",false);
      docu_class           = ctx.readValue("DOCCLASS1",doc_class);
      //ctx vars use for radio button
      method_val           = ctx.readValue("METHOD_VAL","");

      rev_select_buff      = ctx.readBuffer("REV_SELECT_BUFF");

      selectedRows   = ctx.readValue("SELECTEDROWS","");
      buff           = ctx.readBuffer("BUFF");
      keys           = ctx.readBuffer("KEYS");
      noTransferRows = ctx.readNumber("NOTRANSFERROWS",0);
      currentRow     = ctx.readNumber("CURRENTROW",0);
      multiTransfer  = ctx.readFlag("MULTITRANSFER",false);
      numberGeneratorDb = ctx.readValue("NUM_GENERATOR_DB","");

      copyobj_val    = ctx.readFlag("OBJ_VAL", true); // Bug Id 92140

      mgr.setPageExpiring();


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.dataTransfered())
      {

         url       = mgr.readValue("SEND_URL");
         doc_class = mgr.readValue("DOC_CLASS");
         doc_no    = mgr.readValue("DOC_NO");
         revisions = mgr.readValue("REVISIONS");
         keys      = mgr.getTransferedData();

         bFirtPage = true;
         runQuery();
         //adjust();
      }
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))) && (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL"))))
      {
         prepareDialog();
         //sadjust();
      }
      else if (mgr.buttonPressed("CANCEL"))
      {
         Cancel();
      }
      else if ("FIN".equals(mgr.readValue("FIN")))
      {
         finish();
      }

      adjust();

      ctx.writeFlag("STEP_NO",currentStep);

      //==================general ctx variables====================================
      ctx.writeValue("DOCCLASS",dclass);
      ctx.writeValue("DOCNO",dno);
      ctx.writeValue("DOC_NO",doc_no);
      ctx.writeValue("DOC_CLASS",doc_class);
      ctx.writeValue("SEND__URL",url);
      ctx.writeValue("DOCCLASS1",docu_class);

      ctx.writeValue("DOCNO",dno);

      if (rev_select_buff != null)
         ctx.writeBuffer("REV_SELECT_BUFF",rev_select_buff);

      //============================== Write ctx vars used for checkboxes========
      ctx.writeFlag("STRUCT_CHECKABLE",structure_checkable);
      ctx.writeFlag("FILE_CHECKABLE",file_checkable);
      ctx.writeFlag("ACCESS_CHECKABLE",access_checkable);
      ctx.writeFlag("METHOD_CHECKABLE",method_checkable);
      ctx.writeFlag("SURVEY_CHECKABLE",docsurvey_checkable);
      ctx.writeFlag("STRUCTURE_VAL",structure_val);
      ctx.writeFlag("FIL_VAL",file_val);
      ctx.writeFlag("SURVEY_VAL",copyapp_val);
      ctx.writeFlag("ACC_VAL",access_val);
      ctx.writeFlag("REV_VAL",revision_val);
      ctx.writeFlag("STATE_VAL",state_val);
      ctx.writeFlag("ACTIVEFRAME1",activeFrame1);
      ctx.writeFlag("REC_VAL",rec_val);
      // ctx.writeValue("STRUCTUREVAL",has_structure);
      // ctx.writeValue("BOOKLIST",book_list);

      //============================== Write ctx vars used for radio buttons========

      ctx.writeValue("METHOD_VAL",method_val);

      //==================variables===============================

      ctx.writeValue("SELECTEDROWS",selectedRows);
      ctx.writeFlag("START",start);
      if (buff != null)
         ctx.writeBuffer("BUFF",buff);
      ctx.writeBuffer("KEYS",keys);
      ctx.writeNumber("NOTRANSFERROWS",noTransferRows);
      ctx.writeNumber("CURRENTROW",currentRow);
      ctx.writeFlag("MULTITRANSFER",multiTransfer);
      ctx.writeValue("NUM_GENERATOR_DB",numberGeneratorDb);

      ctx.writeFlag("OBJ_VAL", copyobj_val); // Bug Id 92140

      String wizHeader=makeWizardHeader();
      headblk.setTitle(mgr.translate("DOCMAWCOPYTITLEWIZARDWIZHEADER: Copy Document Title From: ") +wizHeader);



   }


   public void  runQuery()
   {
      prepareDialog();
   }

   //=============================================================================
   //   Validation
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");
      if ("NEW_DOC_CLASS".equals(val))
      {
         //NUMBER_GENERATOR
         trans.clear();

         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("NEW_DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

         cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
         cmd.addParameter("NEW_DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_COUNTER");

         trans = mgr.perform(trans);
         numberGeneratorDb = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
         id1 = trans.getValue("ID1COM/DATA/ID1");

         trans.clear();
         cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT");
         cmd.addParameter("NUMBER_GENERATOR",numberGeneratorDb);

         trans = mgr.perform(trans);
         numberGenerator = trans.getValue("GETCLIENTVAL/DATA/OUT");

         if ("ADVANCED".equals(numberGeneratorDb))
         {
            trans.clear();

            cmd = trans.addCustomFunction("ID2COM","Doc_NUmber_Counter_API.Get_Default_Id2","ID2");
            cmd.addParameter("ID1",id1);

            trans = mgr.perform(trans);
            id2 = trans.getValue("ID2COM/DATA/ID2");
            if (id2.equals("0"))
               id2 = "";
         }
         else
         {
            id1 = "";
            id2 = "";
         }

         trans.clear();

         String txt="";
         txt = numberGenerator +"^"+(mgr.isEmpty(id1) ? "" : id1)+"^"+(mgr.isEmpty(id2) ? "" : id2)+"^";
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
   }

   //=============================================================================

   public void  prepareDialog()
   {
      ASPManager mgr = getASPManager();

      //First Revision
      trans.clear();

      cmd = trans.addCustomFunction("FIRSTREV","Doc_Class_Default_API.Get_Default_Value_","TITLE_REV");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DUMMY1","DocTitle");
      cmd.addParameter("DUMMY2","DOC_REV");

      cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DUMMY1","DocTitle");
      cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

      cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DUMMY1","DocTitle");
      cmd.addParameter("DUMMY2","NUMBER_COUNTER");

      trans = mgr.perform(trans);
      numberGeneratorDb = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
      id1 = trans.getValue("ID1COM/DATA/ID1");
      sFirstRev = trans.getValue("FIRSTREV/DATA/TITLE_REV");

      trans.clear();

      cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT");
      cmd.addParameter("NUMBER_GENERATOR",numberGeneratorDb);

      cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
      cmd.addParameter("ID1",id1);

      trans = mgr.perform(trans);
      numberGenerator = trans.getValue("GETCLIENTVAL/DATA/OUT");
      id2 = trans.getValue("ID2COM/DATA/ID2");

      if (!mgr.isEmpty(id2) && id2.equals("0")) // Avoid a null error if ID2 is empty
         id2 = "";

      if (!"ADVANCED".equals(numberGeneratorDb))
      {
         id1 = "";
         id2 = "";
      }

      // Bug Id 92140, start
      trans.clear();
      cmd = trans.addCustomCommand("COPYVALS", "DOC_TITLE_API.Get_Info");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO", doc_no);
      cmd.addOutParameter("DUMMY1", "");
      cmd.addOutParameter("DUMMY2", "");
      cmd.addOutParameter("DUMMY3", "");
      cmd.addOutParameter("DUMMY4", "");

      trans = mgr.perform(trans);

      hasStructure = trans.getValue("COPYVALS/DATA/DUMMY1");
      hasFile      = trans.getValue("COPYVALS/DATA/DUMMY2");
      hasAppRoute  = trans.getValue("COPYVALS/DATA/DUMMY3");
      hasObjCon    = trans.getValue("COPYVALS/DATA/DUMMY4");


      // Bug Id 92140, end
   }


   public String  makeWizardHeader()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomFunction("GETDOCTITLE","DOC_TITLE_API.Get_Title","DUMMY1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);


      cmd = trans.addCustomFunction("GETREVISION","DOC_TITLE_API.Get_REVISION","TITLE_REV");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);

      trans = mgr.perform(trans);
      title = trans.getValue("GETDOCTITLE/DATA/DUMMY1");
      String revision = trans.getValue("GETREVISION/DATA/TITLE_REV");
      trans.clear();
      if (mgr.isEmpty(revision))
      {
         return title + "<font size=1> ("  + doc_class + " - " + doc_no +  ")</font>";
      }
      else
      {
         return title + "<font size=1> ("  + doc_class + " - " + doc_no +" - "+revision+  ")</font>";
      }







   }


   public String  getAppSettings()
   {
      ASPManager mgr = getASPManager();
      String sUser, sAppSet;

      sUser    = mgr.getUserId().toUpperCase();
      sUser    = replaceString(sUser,"\\","~");
      sAppSet  = "application=DOCMAW^instance=";
      sAppSet += mgr.getConfigParameter("APPLICATION/ID")+"^language=";
      sAppSet += mgr.getConfigParameter("APPLICATION/LANGUAGE")+"^username="+sUser+"^";
      return sAppSet;
   }


   public boolean  hasPermission()
   {
      ASPManager mgr = getASPManager();

      // ============= Checks security ==============
      trans.clear();

      trans.addSecurityQuery("EDM_FILE_API","Delete_File_Reference,Get_Edm_Information,Create_File_Reference,Set_File_State");
      trans.addSecurityQuery("DOC_ISSUE_API","Get_Document_Info");
      trans.addSecurityQuery("EDM_APPLICATION_API","Get_File_Type_List");
      trans.addSecurityQuery("EDM_FILE_TEMPLATE_API","Get_File_Template");

      trans = mgr.perform(trans);
      ASPBuffer sec = trans.getSecurityInfo();

      if ((!sec.itemExists("EDM_FILE_API.Delete_File_Reference")) || (!sec.itemExists("EDM_FILE_API.Get_Edm_Information"))|| (!sec.itemExists("EDM_FILE_API.Create_File_Reference"))|| (!sec.itemExists("EDM_FILE_API.Set_File_State")) || (!sec.itemExists("DOC_ISSUE_API.Get_Document_Info"))|| (!sec.itemExists("EDM_APPLICATION_API.Get_File_Type_List"))|| (!sec.itemExists("EDM_FILE_TEMPLATE_API.Get_File_Template")))
         return false;
      else
         return true;

   }

   //===========================================================================================================
   //                     PUSH BUTTON FUNCTIONS
   //===========================================================================================================

   public void  finish()
   {
      ASPManager mgr = getASPManager();

      int hasst, hasfl, hasapp, hasac, setrev, setstat, setrec, copymeth, hasobj; // Bug Id 92140, Added hasobj

      String struct = mgr.readValue("CBCOPYSTRUCTURE");
      String fil    = mgr.readValue("CBCOPYFILE");
      String app    = mgr.readValue("CBCOPYDOCUMENTSURVEY");
      String acc    = mgr.readValue("CBCOPYACCESS");
      String meth   = mgr.readValue("METHOD");
      String revi   = mgr.readValue("CBSETREV");
      String stat   = mgr.readValue("CBSETSTATE");
      String rec    = mgr.readValue("CBCOPYREC");
      String obj    = mgr.readValue("CBCOPYOBJECTS"); // Bug Id 92140

      if (( "STRUCT".equals(struct) ) &&  ( "ON".equals(meth) ))
      {
         hasst=1;
      }
      else
      {
         hasst=0;
      }

      if (( "COPY".equals(fil) ) &&  ( "ON".equals(meth) ))
      {
         hasfl=1;
      }
      else
      {
         hasfl=0;
      }

      if (( "APP".equals(app) ) &&  ( "ON".equals(meth) ))
      {
         hasapp=1;
      }
      else
      {
         hasapp=0;
      }

      if (( "ACC".equals(acc) ) &&  ( "ON".equals(meth) ))
      {
         hasac=1;
      }
      else
      {
         hasac=0;
      }
         setrev=1;

      if (( "PRE".equals(stat) ) &&  ( "ON".equals(meth) ))
      {
         setstat=1;
      }
      else
      {
         setstat=0;
      }

      if (( "ON".equals(rec) ) &&  ( "ON".equals(meth) ))
      {
         setrec=1;
      }
      else
      {
         setrec=0;
      }

      // Bug Id 92140, start
      if (("OBJ".equals(obj)) && ("ON".equals(meth))) 
      {
         hasobj = 1;
      }
      else
      {
         hasobj = 0;
      }
      // Bug Id 92140, end

      if ("ON".equals(meth))
      {
         copymeth=1;
      }
      else
      {
         copymeth=0;
      }

      d_no     = "";
      String docclass = mgr.readValue("NEW_DOC_CLASS");
      d_no     = mgr.readValue("NEW_DOC_NO");
      String sAttr="",generated_doc_no = "";

      trans.clear();
      cmd = trans.addCustomFunction("GENNUMBER","DOC_TITLE_API.Generate_Doc_Number","NEW_DOC_NO");
      cmd.addParameter("NEW_DOC_CLASS",mgr.readValue("NEW_DOC_CLASS"));
      cmd.addParameter("BOOKING_LIST",mgr.readValue("BOOKING_LIST"));
      cmd.addParameter("ID1",mgr.readValue("ID1"));
      cmd.addParameter("ID2",mgr.readValue("ID2"));
      cmd.addParameter("ATTR",sAttr);

      trans = mgr.perform(trans);
      generated_doc_no = trans.getValue("GENNUMBER/DATA/NEW_DOC_NO");

      trans.clear();
      cmd = trans.addCustomCommand("NEWCOPY","DOC_TITLE_API.Copy_Doc_Title");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("NEW_DOC_CLASS",docclass);
      //If it's empty get the default number.
      if ((!("ADVANCED".equals(numberGeneratorDb))) && (!mgr.isEmpty(d_no)))
          cmd.addParameter("NEW_DOC_NO",d_no);
      else
          cmd.addParameter("NEW_DOC_NO",generated_doc_no);
      cmd.addParameter("TITLE",mgr.readValue("TITLE"));
      cmd.addParameter("TITLE_REV",mgr.readValue("TITLE_REV"));   //title revision bakalk
      cmd.addParameter("HAS_STRUCTURE",hasst+"");
      cmd.addParameter("HAS_APP_ROUTE",hasapp+"");
      cmd.addParameter("HAS_FILE",hasfl+"");
      cmd.addParameter("COPY_ACCESS",hasac+"");
      cmd.addParameter("NEW_REVISION",1+"");
      cmd.addParameter("DUMMY1",copymeth+"");
      cmd.addParameter("DUMMY2",setrev+"");
      cmd.addParameter("DUMMY3",setstat+"");
      cmd.addParameter("DUMMY4",setrec+"");
      cmd.addParameter("COPY_OBJECTS",hasobj+""); // Bug Id 92140

      trans = mgr.perform(trans);

      d_no = trans.getValue("NEWCOPY/DATA/NEW_DOC_NO");

      if (( "COPY".equals(fil) ) &&  ( "ON".equals(meth) ))
      {
         if (!hasPermission())
         {
            mgr.showAlert(mgr.translate("DOCMAWCOPYTITLEWIZARDCANPERFORMCOPY: You do not have permission to copy this file"));
            return;
         }
         else
         {
            trans.clear();
            cmd   = trans.addQuery("DOCSHEET","EDM_FILE","DISTINCT DOC_SHEET,DOC_REV","DOC_NO=? AND DOC_CLASS=?","DOC_SHEET");
            cmd.addParameter("DOC_NO",d_no);
            cmd.addParameter("DOC_CLASS",docclass);
            trans = mgr.perform(trans);
            ASPBuffer returnBuffer = trans.getBuffer("DOCSHEET");
            trans.clear();

            ASPBuffer tempBuffer;

            for (int i=0; i<returnBuffer.countItems()-1; i++)
            {
               tempBuffer =returnBuffer.getBufferAt(i);

               trans.clear();
               cmd = trans.addCustomFunction("GETLATESTREV","Doc_Issue_API.Get_Latest_Chg_Rev","OUT");
               cmd.addParameter("DOC_CLASS",doc_class);
               cmd.addParameter("DOC_NO",doc_no);
               cmd.addParameter("DOC_SHEET",tempBuffer.getValueAt(0));
               trans = mgr.perform(trans);

               if (!("".equals(tempBuffer.getValueAt(0))))
               {
                  try
                  {
                     copyFileInRepository(doc_class, doc_no,tempBuffer.getValueAt(0), trans.getValue("GETLATESTREV/DATA/OUT"), docclass, d_no,tempBuffer.getValueAt(0), tempBuffer.getValueAt(1));                  }
                  catch (FndException e)
                  {

                  }
               }
            }
         }

      }
      sendBack(url,mgr.readValue("NEW_DOC_CLASS"),d_no);
   }



   public void sendBack(String url_,String doc_class_, String doc_no_)
   {
      ASPManager mgr = getASPManager();
      //ASPBuffer keys = mgr.newASPBuffer();
      String bufferName = "" + (keys.countItems()+1);
      ASPBuffer row  = keys.addBuffer(bufferName);
      row.addItem("DOC_CLASS",doc_class_);
      row.addItem("DOC_NO",doc_no_);
      mgr.transferDataTo(url_,keys);
   }



   public void  Cancel()
   {
      ASPManager mgr = getASPManager();


      if (multiTransfer)
      {
         String redirectFrom = ctx.findGlobal("__REDIRECT_FROM","");
         mgr.transferDataTo(redirectFrom,buff);
      }
      else
         sendBack(url,doc_class,doc_no);
   }



   public void  preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();


      disableConfiguration();

      disableHeader();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(17).
      setHidden();

      headblk.addField("DOC_NO").
      setSize(17).
      setHidden();

      headblk.addField("NEW_DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setFunction("''").
      setLabel(mgr.translate("DOCMAWCOPYTITLEWIZARDNEWDOCCLASS: New Document Class")).
      setDynamicLOV("DOC_CLASS",630,450).
      setLOVProperty("TITLE",mgr.translate("DOCMAWCOPYTITLEWIZARDDOCCLASS1: List of Document Class")).
      setCustomValidation("NEW_DOC_CLASS","NUMBER_GENERATOR,ID1,ID2");

      headblk.addField("NEW_DOC_NO").
      setSize(20).
      setMaxLength(120).
      setInsertable().
      setUpperCase().
      setLabel(mgr.translate("DOCMAWCOPYTITLEWIZARDNEWDOCNO: New Document Number"));


      headblk.addField("TITLE").
      setSize(20).
      setMaxLength(250).
      setLabel(mgr.translate("DOCMAWCOPYTITLEWIZARDNEWDOCTITLE: New Document Title"));


      headblk.addField("DOC_SHEET").
      setSize(7).
      setLabel("DOCMAWCOPYTITLEWIZARDSNEWPARTSHEET: Sheet No").
      setUpperCase().
      setHidden().
      setFunction("''");


      headblk.addField("TITLE_REV").
      setSize(20).
      setMaxLength(28).
      setLabel(mgr.translate("DOCMAWCOPYTITLEWIZARDNEWTITLEREVISION: New Title Revision"));



      headblk.addField("DOC_REV").
      setSize(7).
      setMandatory().
      setLabel("DOCMAWCOPYTITLEWIZARDSNEWPARTREV: Revision").
      setUpperCase().
      setHidden().
      setFunction("''");

      headblk.addField("NUMBER_GENERATOR").
      setSize(20).
      setMaxLength(30).
      setUpperCase().
      setReadOnly().
      setLabel("COPYTITLEWIZARDNUMGENERATOR: Number Generator").
      setFunction("''");
      //Bug Id 74966, Added LOV. Removed ReadOnly tag
      headblk.addField("ID1").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLabel("COPYTITLEWIZARDNUMBERCOUNTERID1: Number Counter ID1").
      setLOV("Id1Lov.page");
      

      headblk.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(30).
      setUpperCase().
      setLabel("COPYTITLEWIZARDBOOKINGLIST: Booking List").
      setFunction("''").
      setLOV("BookListLov.page", "ID1,ID2");//Bug Id 73606


      headblk.addField("ID2").
      setSize(20).
      setMaxLength(30).
      setUpperCase().
      //setReadOnly().
      setLabel("COPYTITLEWIZARDNUMBERCOUNTERID2: Number Counter ID2").
      setFunction("''").
      setLOV("Id2Lov.page","ID1");

      headblk.addField("ATTR").
      setFunction("''");

      //--------------configurable doc no ends

      headblk.addField( "HAS_STRUCTURE" ).
      setHidden().
      //setCheckBox().
      setFunction("''");

      headblk.addField( "HAS_APP_ROUTE" ).
      setHidden().
      setFunction("''");

      headblk.addField( "HAS_FILE" ).
      setHidden().
      setFunction("''");

      headblk.addField( "COPY_ACCESS" ).
      setHidden().
      setFunction("''");

      // Bug Id 92140, start
      headblk.addField( "COPY_OBJECTS" ).
      setHidden().
      setFunction("''");
      // Bug Id 92140, end

      headblk.addField( "DUMMY1" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DUMMY2" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DUMMY3" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DUMMY4" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DUMMY5" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DUMMY6" ).
      setHidden().
      setFunction("''");

      headblk.addField( "DOC_TYPE" ).
      setHidden().
      setFunction("''");

      headblk.addField( "KEY_GEN" ).
      setHidden().
      setFunction("''");

      headblk.addField( "EXIST" ).
      setHidden().
      setFunction("''");

      headblk.addField( "NEW_REVISION" ).
      setHidden().
      setFunction("''");

      headblk.addField( "OUT" ).
      setHidden().
      setFunction("''");

      headblk.addField("REV_NO").
      setSize(10).
      setHidden().
      setFunction("''");


      headblk.setView("DOC_TITLE");
      headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableMinimize();

      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      //headlay.setEditable();
      //headlay.setDialogColumns(2);
      
      super.preDefine();
   }

   public void  createAnewRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addEmptyCommand("HEAD","DOC_TITLE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }



   public void  adjust()
   {
      ASPManager mgr = getASPManager();
      //any adjustments?
      if (headset.countRows()==0)
      {
         createAnewRow();
      }

      if (bFirtPage)
      {
         eval(headblk.generateAssignments());
      }

   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWCOPYTITLEWIZARDTITLE: Copy Document Title Wizard"; //Bug id 71130

   }

   protected String getTitle()
   {
      return "DOCMAWCOPYTITLEWIZARDTITLE: Copy Document Title Wizard";  //Bug id 71130

   }

   protected AutoString getContents() throws FndException
   {

      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWCOPYTITLEWIZARDTITLE: Copy Document Title Wizard")); //Bug id 71130
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">  \n");
      out.append("<input type=\"hidden\" name=\"FIN\" value=\"\">\n");

      out.append(mgr.startPresentation("DOCMAWCOPYTITLEWIZARDTITLE: Copy Document Title Wizard"));//Bug id 71130




      String methodAndCopyLatestTable="<table border=\"0\" width=\"100%\"> \n";
      methodAndCopyLatestTable+="<tr><td align=\"left\" colspan=\"2\"><U>";
      methodAndCopyLatestTable+=fmt.drawReadLabel("DOCMAWCOPYTITLEWIZARDMETHOD: Method")+"<U></td></tr> \n";
      methodAndCopyLatestTable+="<tr><td align=\"left\">";
      methodAndCopyLatestTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPLAT: Copy Data from Sheets")+"</td>"; //Bug id 71130
      methodAndCopyLatestTable+="<td align=\"right\">";
      methodAndCopyLatestTable+=fmt.drawCheckbox("METHOD","ON",true,"OnClick=methodCheck()")+"</td></tr> \n";
      methodAndCopyLatestTable+="</table  > \n";

      String fromOldTable="<table border=\"0\" width=\"100%\"> \n";
      fromOldTable+="<tr><td align=\"left\" colspan=\"2\"><U>";
      fromOldTable+=fmt.drawReadLabel("DOCMAWCOPYTITLEWIZARDOLDVALUES: From Latest Revision")+"<U></td></tr> \n";  //Bug id 71130

      //1
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPSTR: Copy Structure")+"</td>";
      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYSTRUCTURE","STRUCT",structure_val,"OnClick=structureCheck()")+"</td></tr> \n";

      //2
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPFIL: Copy File")+"</td>";
      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYFILE","COPY",file_val,"OnClick=fileCheck()")+"</td></tr> \n";

      //3
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPAPPROUTE: Copy Approval Process")+"</td>";
      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYDOCUMENTSURVEY","APP",copyapp_val,"OnClick=appCheck()")+"</td></tr> \n";

      //4
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPACCESS: Copy Access")+"</td>";
      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYACCESS","ACC",access_val,"OnClick=accCheck()")+"</td></tr> \n";

      //5
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCOPYREV: Copy Field Values")+"</td>";   //Bug id 71130

      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYREC", "ON",rec_val,"")+"</td></tr> \n";

      // Bug Id 92140, start
      //6
      fromOldTable+="<tr><td align=\"left\">";
      fromOldTable+=fmt.drawWriteLabel("DOCMAWCOPYTITLEWIZARDCPOBJECTS: Copy Object Connections")+"</td>";
      fromOldTable+="<td align=\"right\">";
      fromOldTable+=fmt.drawCheckbox("CBCOPYOBJECTS","OBJ",copyobj_val,"OnClick=objCheck()")+"</td></tr> \n";
      // Bug Id 92140, end

      fromOldTable+="</table  > \n";


      String dialog = "<table class=\"BlockLayoutTable\" cellpadding=5 cellspacing=5 border=0 >\n";
      //row 1
      dialog += "   <tr>\n";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENEWDOCUMENTCLASS: New Document Class");
      dialog +="</td>\n";
      dialog += "   <td >";
      dialog +=fmt.drawTextField("NEW_DOC_CLASS","",mgr.getASPField("NEW_DOC_CLASS").getTag(),20,12,true);
      dialog +=" </td>";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENEWDOCUMENTNUMBER: New Document Number");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("NEW_DOC_NO","",mgr.getASPField("NEW_DOC_NO").getTag());
      dialog +=" </td>";
      dialog +=" </tr>";
      //row 2
      dialog += "   <tr>\n";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENEWDOCUMENTTITLE: New Document Title");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("TITLE","",mgr.getASPField("TITLE").getTag(),20,250,true);
      dialog +=" </td>";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENEWTITLEREVISION: New Title Revision");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("TITLE_REV","",mgr.getASPField("TITLE_REV").getTag(),20,28,false);
      dialog +=" </td>";
      dialog +=" </tr>";
      //row 3
      dialog += "   <tr>\n";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENUMBERGENERATOR: Number Generator");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawReadOnlyTextField("NUMBER_GENERATOR","",mgr.getASPField("NUMBER_GENERATOR").getTag());
      dialog +=" </td>";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENUMBERCOUNTERID1: Number Counter ID1");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("ID1","",mgr.getASPField("ID1").getTag());
      dialog +=" </td>";
      dialog +=" </tr>";
      //row 4
      dialog += "   <tr>\n";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLEBOOKINGLIST: Booking List");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("BOOKING_LIST","",mgr.getASPField("BOOKING_LIST").getTag());
      dialog +=" </td>";
      dialog +="    <td>";
      dialog +=fmt.drawWriteLabel("COPYDOCUMENTTITLENUMBERCOUNTERID2: Number Counter ID2");
      dialog +="</td>\n";
      dialog += "   <td nowrap >";
      dialog +=fmt.drawTextField("ID2","",mgr.getASPField("ID2").getTag());
      dialog +=" </td>";
      dialog +=" </tr>";

      //row 5: this row has only two columns
      dialog +="  <tr valign=\"top\">\n";
      dialog +="    <td colspan=\"2\">";
      dialog += methodAndCopyLatestTable;
      dialog +="</td>\n";
      dialog += "   <td nowrap colspan=\"2\" >";
      dialog += fromOldTable;
      dialog +=" </td>";
      dialog +=" </tr>";

      dialog +=" </table>";//end of table for the dialog


      String buttons =fmt.drawButton("FINISH",mgr.translate("DOCMAWCOPYTITLEWIZARDFINISHH:  Finish "),"OnClick=checkEmpty('fin')") ;
      buttons +="&nbsp;&nbsp;";// keep a space
      buttons +=fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWCOPYTITLEWIZARDCANCELL: Cancel"),"");

      out.append(headblk.generateHiddenFields());
      out.append("<table border=0 cellspacing=0 cellpadding=0 >\n");
      out.append("<tr><td colspan=\"4\">"+headbar.showBar()+"<td></tr>");
      out.append("<tr >");

      //out.append(" <td width=10></td>");//just to leave a space here : bakalk
      out.append("<td>&nbsp;&nbsp;</td>");
      out.append("<td  ><img src = \"images/CreateNewDocRevision.jpg\" width=\"100\" height=\"300\"></td>\n");//image column
      out.append("<td >"+dialog+"</td>");
      //out.append(" <td width=10></td>");//just to leave a space here : bakalk
      out.append("<td>&nbsp;&nbsp;</td>");
      out.append(" </tr><tr>\n");

      out.append("<td colspan=\"3\" align=\"right\">"+buttons+"</td>");
      //out.append(" <td width=10></td>");//just to leave a space here : bakalk
      out.append("<td>&nbsp;&nbsp;</td>");
      out.append(" </tr>\n");
      out.append("</table>\n");

      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("doc_class=\"\";\n");
      appendDirtyJavaScript("document.form.NEW_DOC_CLASS.value=\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(doc_class));
      appendDirtyJavaScript("\";\n");
      appendDirtyJavaScript("document.form.TITLE_REV.value=\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sFirstRev));
      appendDirtyJavaScript("\";\n");
      appendDirtyJavaScript("document.form.TITLE.value=\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(title));
      appendDirtyJavaScript("\";\n");
      appendDirtyJavaScript("document.form.METHOD.checked=true;\n");
      appendDirtyJavaScript("meth_val=\"1\";\n");

      if (this.bFirtPage)
      {
         appendDirtyJavaScript("document.form.NUMBER_GENERATOR.value=\"" + mgr.encodeStringForJavascript(numberGenerator) + "\"; \n");
         appendDirtyJavaScript("   document.form.ID1.value=\"" + (mgr.encodeStringForJavascript(id1) == null ? "" : mgr.encodeStringForJavascript(id1)) + "\"; \n"); //Bug Id 68773,Start 
         appendDirtyJavaScript("   document.form.ID2.value=\"" + (mgr.encodeStringForJavascript(id2) == null ? "" : mgr.encodeStringForJavascript(id2)) + "\"; \n"); //Bug Id 68773,End
	 //Bug Id 74966, Start
	 appendDirtyJavaScript("    if (document.form.NUMBER_GENERATOR.value ==\"ADVANCED\")\n");
	 appendDirtyJavaScript("    {\n");
	 appendDirtyJavaScript("      if (document.form.ID1.value ==\"\")\n");
	 appendDirtyJavaScript("         document.form.ID1.readOnly=0;\n");
	 appendDirtyJavaScript("      else  \n"); 
	 appendDirtyJavaScript("         document.form.ID1.readOnly=1;\n");
	 appendDirtyJavaScript("      document.form.ID2.readOnly=0;\n");
	 appendDirtyJavaScript("      document.form.BOOKING_LIST.readOnly=0;\n");
	 appendDirtyJavaScript("    }\n");
	 appendDirtyJavaScript("    else  \n");
	 appendDirtyJavaScript("    {\n");
	 appendDirtyJavaScript("       document.form.ID1.readOnly=1;\n");
	 appendDirtyJavaScript("       document.form.ID2.readOnly=1;\n");
	 appendDirtyJavaScript("       document.form.BOOKING_LIST.readOnly=1;\n");
	 appendDirtyJavaScript("       document.form.BOOKING_LIST.value=\"\";\n");
	 appendDirtyJavaScript("    }\n");
	 //Bug Id 74966, End
      }
      appendDirtyJavaScript("function methodCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("     if (document.form.METHOD.checked==false)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       meth_val=\"0\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYSTRUCTURE.style.visibility = \"hidden\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYFILE.style.visibility = \"hidden\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYDOCUMENTSURVEY.style.visibility = \"hidden\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYACCESS.style.visibility = \"hidden\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYREC.style.visibility = \"hidden\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYOBJECTS.style.visibility = \"hidden\";\n"); // Bug Id 92140
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       meth_val=\"1\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYSTRUCTURE.style.visibility = \"visible\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYFILE.style.visibility = \"visible\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYDOCUMENTSURVEY.style.visibility = \"visible\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYACCESS.style.visibility = \"visible\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYREC.style.visibility = \"visible\";\n");
      appendDirtyJavaScript("       document.form.CBCOPYOBJECTS.style.visibility = \"visible\";\n"); // Bug Id 92140
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function structureCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYSTRUCTURE.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("      //alert(");
      appendDirtyJavaScript(structure_checkable); // XSS_Safe ILSOLK 20070808 
      appendDirtyJavaScript(");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("  /* else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("    if (document.form.CBCOPYSTRUCTURE.checked==true)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      s_val=\"1\";\n");
      appendDirtyJavaScript("      alert(s_val);\n");
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    s_val=\"0\";\n");
      appendDirtyJavaScript("    alert(s_val);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("   }*/ \n");
      // Bug Id 92140, start
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if('");
      appendDirtyJavaScript(hasStructure);
      appendDirtyJavaScript("' == '0')\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.CBCOPYSTRUCTURE.checked=false;\n");
      appendDirtyJavaScript("         msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("         alert(msg);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      // Bug Id 92140, end
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function appCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYDOCUMENTSURVEY.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      // Bug Id 92140, start
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if('");
      appendDirtyJavaScript(hasAppRoute);
      appendDirtyJavaScript("' == '0')\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.CBCOPYDOCUMENTSURVEY.checked=false;\n");
      appendDirtyJavaScript("         msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("         alert(msg);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      // Bug Id 92140, end
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function accCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYACCESS.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function fileCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYFILE.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      // Bug Id 92140, start
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if('");
      appendDirtyJavaScript(hasFile);
      appendDirtyJavaScript("' == '0')\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.CBCOPYFILE.checked=false;\n");
      appendDirtyJavaScript("         msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("         alert(msg);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      // Bug Id 92140, end
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function revisionCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBSETREV.checked=false;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function preCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBSETSTATE.checked=false;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function checkEmpty(str)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if (document.form.NEW_DOC_CLASS.value==\"\")\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(mgr.translate("DOCMAWCOPYTITLEWIZARDRFDCLASS: Doc Class field requires a value"));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript(" return;\n }\n");
      appendDirtyJavaScript(" else if (document.form.TITLE.value==\"\")\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("       alert(\"");
      appendDirtyJavaScript(mgr.translate("DOCMAWCOPYTITLEWIZARDRTTITLE: Title field requires a value"));
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript(" return;\n}\n");
      appendDirtyJavaScript(" else if (str==\"fin\")\n");
      appendDirtyJavaScript("       document.form.FIN.value=\"FIN\";\n");
      appendDirtyJavaScript("    submit();\n");
      appendDirtyJavaScript("}\n");
      // Bug Id 92140, start
      appendDirtyJavaScript("function objCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (meth_val==\"0\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYOBJECTS.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if('");
      appendDirtyJavaScript(hasObjCon);
      appendDirtyJavaScript("' == '0')\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         document.form.CBCOPYOBJECTS.checked=false;\n");
      appendDirtyJavaScript("         msg ='");
      appendDirtyJavaScript(correctDoubleQuotation(mgr.encodeStringForJavascript(mgr.translate("DOCMAWCOPYDOCTITLENOOPTION: This option is not available!"))));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("         alert(msg);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      if (hasStructure.equals("0")) 
      {
         appendDirtyJavaScript("document.form.CBCOPYSTRUCTURE.checked=false;\n");
      }
      if (hasFile.equals("0")) 
      {
         appendDirtyJavaScript("document.form.CBCOPYFILE.checked=false;\n");
      }
      if (hasAppRoute.equals("0")) 
      {
         appendDirtyJavaScript("document.form.CBCOPYDOCUMENTSURVEY.checked=false;\n");
      }
      if (hasObjCon.equals("0")) 
      {
         appendDirtyJavaScript("document.form.CBCOPYOBJECTS.checked=false;\n");
      }
      // Bug Id 92140, end

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
         appendDirtyJavaScript("       window.location=\"../docmaw/CopyTitleWizard.page?DOC_CLASS="+mgr.URLEncode(mgr.readValue("NEW_DOC_CLASS"))+"&DOC_NO="+mgr.URLEncode(d_no)+"&SENDBACK=YES&SEND_URL="+mgr.URLEncode(url)+"\";");
         appendDirtyJavaScript("}\n");
      }
      //Bug Id 74966, Start
      appendDirtyJavaScript("function validateNewDocClass(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('HEAD',i)=='QueryMode__' ) \n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if(!checkNewDocClass(i) ) return;\n");
      appendDirtyJavaScript("    if( getValue_('NEW_DOC_CLASS',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    var r = __connect('" + mgr.getURL() + "?VALIDATE=NEW_DOC_CLASS'+'&NEW_DOC_CLASS=' + URLClientEncode(getValue_('NEW_DOC_CLASS',i)));\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'NEW_DOC_CLASS',i,'New Document Class') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('NUMBER_GENERATOR',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ID1',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ID2',i,2);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (document.form.NUMBER_GENERATOR.value ==\"ADVANCED\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      if (document.form.ID1.value ==\"\")\n");
      appendDirtyJavaScript("         document.form.ID1.readOnly=0;\n");
      appendDirtyJavaScript("      else  \n"); 
      appendDirtyJavaScript("         document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("      document.form.ID2.readOnly=0;\n");
      appendDirtyJavaScript("      document.form.BOOKING_LIST.readOnly=0;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else  \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.ID2.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.BOOKING_LIST.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.BOOKING_LIST.value=\"\";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");
      //disabled Id1 lov
      appendDirtyJavaScript("function preLovId1(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.ID1.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("      KEY_VALUE = (getValue_('ID1',i).indexOf('%') !=-1)? getValue_('ID1',i):'';\n");
      appendDirtyJavaScript("	   lovId1(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      
      //disabled Id2 lov
      appendDirtyJavaScript("function preLovId2(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.ID2.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("      KEY_VALUE = (getValue_('ID2',i).indexOf('%') !=-1)? getValue_('ID2',i):'';\n");
      appendDirtyJavaScript("	   lovId2(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      
      //disabled bookinglist lov
      appendDirtyJavaScript("function preLovBookingList(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.BOOKING_LIST.readOnly == 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(params)\n"); 
      appendDirtyJavaScript("         PARAM = params;\n");
      appendDirtyJavaScript("      else\n"); 
      appendDirtyJavaScript("         PARAM = '';\n");
      appendDirtyJavaScript("      var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("      MULTICH=''+enable_multichoice;\n");
      appendDirtyJavaScript("      MASK ='';\n");	 
      appendDirtyJavaScript("     KEY_VALUE = (getValue_('BOOKING_LIST',i).indexOf('%') !=-1)? getValue_('BOOKING_LIST',i):'';\n");
      appendDirtyJavaScript("	   lovBookingList(i,params);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n"); 
      appendDirtyJavaScript("      return;\n"); 
      appendDirtyJavaScript("}\n");
      //Bug Id 74966, End

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

   /*==========================================================================
    *  Bakalk: Methods on Handling Strings
    *========================================================================== */
   // 1
   private int stringIndex(String mainString,String subString)
   {
      int a=mainString.length();
      int index=-1;
      for (int i=0;i<a;i++)
         if (mainString.startsWith(subString,i))
         {
            index=i;
            break;
         }
      return index;
   }//end of stringIndex

   // 2

   private String replaceString(String mainString,String subString,String replaceString)
   {
      String retString = "";
      int posi;
      posi = stringIndex(mainString, subString);
      while (posi!=-1)
      {
         retString+=mainString.substring(0,posi)+replaceString;
         mainString=mainString.substring(posi+subString.length(),mainString.length());
         posi = stringIndex(mainString, subString);
      }

      return retString+mainString;

   }//repstring

   // 3
   private int howManyOccurance(String str,char c)
   {
      int strLength=str.length();
      int occurance=0;
      for (int index=0;index<strLength;index++)
         if (str.charAt(index)==c)
            occurance++;
      return occurance;

   }

   // 4
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

   // Bug Id 92140, start
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
   // Bug Id 92140, end	

}
