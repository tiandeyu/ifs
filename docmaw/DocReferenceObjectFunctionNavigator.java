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
*  File        : DocReferenceObjectFunctionNavigator.java
*  Modified    :
*    2001-03-19  ShDilk  ASP2JAVA Tool - Created Using the ASP file DocReferenceObjectFunctionNavigator.asp
*    2003-01-06  Dikalk  Removed obsolete methods doReset() and clone();
*    2003-01-06  Dikalk  Added doc_sheet to preDefine() and all relevant methods
*    2003-01-06  Dikalk  Removed unused instance variables
*    2003-04-01  InoSlk  Added the doReset and clone methods.(Support for Web Client 3.5.1.)
*    2003-08-28  InoSlk  Call ID 101731: Modified doReset() and clone().
*    2003-10-22  NiSilk  Call ID 107798: Added Doc Sheet.
*    2003-11-06  NiSilk  Added config doc no functionality.
*    2004-02-16  Bakalk  Replaced document.ClientUtil.connect with __connect.
*    2004-03-25  Dikalk  SP1 Merge. Merged the following bugs: 42811 and 40602
*    2005-10-31  Amnalk  Merged Bug Id 53112.
*    2006-07-19  BAKALK  Bug ID 58216, Fixed Sql Injection.
*    2007-08-09  NaLrlk  XSS Correction.
* -----------------------APP7.5 SP7---------------------------------------------------
*    2010-07-21  ARWILK  Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
* ------------------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import ifs.docmaw.edm.*;

public class DocReferenceObjectFunctionNavigator extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocReferenceObjectFunctionNavigator");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;
   private ASPBlock        dlgblk;
   private ASPRowSet       dlgset;
   private ASPCommandBar   dlgbar;
   private ASPBlockLayout  dlglay;
   private ASPBlock        tempblk;
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer keys;
   private ASPBuffer dataBuffer;

   private boolean createnew;
   private boolean bInsertExistDoc;
   private boolean bDocInfo;
   private boolean bRefreshTree;
   private boolean bTranferToEDM;
   private boolean duplicated;

   private String root_path;
   private String root_lu_name;
   private String root_key_ref;
   private String insertdoc;
   private String sNewWindowURL;
   private String comnd;
   private String dlg_doc_class;
   private String strBodyTag;
   private String sFilePath;

   private int inserted_row;

   //===============================================================
   // Construction
   //===============================================================
   public DocReferenceObjectFunctionNavigator(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans = null;
      cmd = null;
      keys = null;
      dataBuffer = null;

      createnew = false;
      bInsertExistDoc = false;
      bDocInfo = false;
      bRefreshTree = false;
      bTranferToEDM = false;
      duplicated = false;

      root_path = null;
      root_lu_name = null;
      root_key_ref = null;
      insertdoc = null;
      sNewWindowURL = null;
      comnd = null;
      dlg_doc_class = null;
      strBodyTag = null;
      sFilePath = null;

      inserted_row   = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocReferenceObjectFunctionNavigator page = (DocReferenceObjectFunctionNavigator)(super.clone(obj));

      // Initializing mutable attributes
      page.trans = null;
      page.cmd   = null;
      page.keys   = null;
      page.dataBuffer = null;

      page.createnew   = false;
      page.bInsertExistDoc   = false;
      page.bDocInfo   = false;
      page.bRefreshTree   = false;
      page.bTranferToEDM   = false;
      page.duplicated   = false;

      page.root_path = null;
      page.root_lu_name   = null;
      page.root_key_ref   = null;
      page.insertdoc   = null;
      page.sNewWindowURL   = null;
      page.comnd   = null;
      page.dlg_doc_class   = null;
      page.strBodyTag   = null;
      page.sFilePath = null;

      page.inserted_row   = 0;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.dlgblk = page.getASPBlock(dlgblk.getName());
      page.dlgset = page.dlgblk.getASPRowSet();
      page.dlgbar = page.dlgblk.getASPCommandBar();
      page.dlglay = page.dlgblk.getASPBlockLayout();
      page.tempblk = page.getASPBlock(tempblk.getName());
      page.tabs = page.getASPTabContainer();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      root_lu_name = ctx.readValue("ROOTLUNAME","");
      root_key_ref = ctx.readValue("ROOTKEYREF","");

      createnew = false;
      bInsertExistDoc = false;
      inserted_row = 0;
      insertdoc = "N";
      bDocInfo = false;
      bRefreshTree = false;
      sNewWindowURL = "";
      bTranferToEDM = false;
      duplicated = false;

      if (mgr.commandBarActivated())
      {
         eval(mgr.commandBarFunction());
         comnd = mgr.readValue("__COMMAND");

         if (( "HEAD.SaveReturn".equals(comnd) )|| ( "HEAD.SaveNew".equals(comnd) )|| ( "HEAD.Delete".equals(comnd) ))
            bRefreshTree = true;

         else if ("HEAD.DuplicateRow".equals(comnd))
            duplicated = true;
      }
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")))
      {
         root_lu_name = mgr.readValue("LU_NAME");
         root_key_ref = mgr.readValue("KEY_REF");
         searchDocuments();
      }
      else if (mgr.buttonPressed("OK"))
         dlgOk();

      else if ("TRUE".equals(mgr.readValue("SELECT_INSERT_FILE")))
         insertExistingDocOk();
      else if (mgr.buttonPressed("CANCEL"))
         dlgCancel();

      adjust();

      ctx.writeValue("ROOTLUNAME",root_lu_name);
      ctx.writeValue("ROOTKEYREF",root_key_ref);
      tabs.saveActiveTab();
   }

   //-----------------------------------------------------------------------------
   //-----------------------------  VALIDATE FUNCTION  ---------------------------
   //-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String txt;
      String doc_rev="",doc_sheet="",numberGenerator="",id1="",id2="",numGenTrx="";
      String val = mgr.readValue("VALIDATE");

      if ("DOC_REV".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETSTATUS", "DOC_ISSUE_API.Get_State","TEMP1");
         cmd.addParameter("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
         trans = mgr.validate(trans);
         trans.clear();
         mgr.responseWrite(trans.getValue("GETSTATUS/DATA/TEMP1")+"^");
         mgr.endResponse();
      }
      else if ("INSERT_DATA".equals(val))
      {
         mgr.responseWrite(mgr.readValue("INSERT_DATA"));
         mgr.endResponse();
      }
      else if ("DLG_DOC_CLASS".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomFunction("GETDEFREV","Doc_Class_Default_API.Get_Default_Value_","DLG_DOC_REV");
         cmd.addParameter("DLG_DOC_CLASS");
         cmd.addParameter("TEMP1","DocTitle");
         cmd.addParameter("TEMP2","DOC_REV");
         cmd = trans.addCustomFunction("GETDEFSHEET","Doc_Class_Default_API.Get_Default_Value_","DLG_DOC_SHEET");
         cmd.addParameter("DLG_DOC_CLASS");
         cmd.addParameter("TEMP1","DocTitle");
         cmd.addParameter("TEMP2","DOC_SHEET");

         //NUMBER_GENERATOR
         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("DLG_DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

         cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
         cmd.addParameter("DLG_DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_COUNTER");

         trans = mgr.validate(trans);
         doc_rev = trans.getValue("GETDEFREV/DATA/DLG_DOC_REV");
         doc_sheet = trans.getValue("GETDEFSHEET/DATA/DLG_DOC_SHEET");
         numberGenerator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
         id1 = trans.getValue("ID1COM/DATA/ID1");
         trans.clear();

         trans = mgr.validate(trans);
         trans.clear();

         cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT_1");
         cmd.addParameter("NUMBER_GENERATOR",numberGenerator);
         trans = mgr.validate(trans);
         numGenTrx = trans.getValue("GETCLIENTVAL/DATA/OUT_1");

         trans.clear();
         if ("ADVANCED".equals(numberGenerator))
         {
            cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
            cmd.addParameter("DUMMY1",id1);
         }


         if ("ADVANCED".equals(numberGenerator))
         {
            trans = mgr.validate(trans);
            id2 = trans.getValue("ID2COM/DATA/ID2");
            trans.clear();
         }
         else
         {
            id1 = "";
            id2 = "";
         }
         if (("0".equals(id2)) || (id2 == null))
            id2 = "";



            StringBuffer response = new StringBuffer(mgr.isEmpty(doc_rev) ? "A1" : doc_rev);
            response.append("^");
            response.append(mgr.isEmpty(doc_sheet) ? "1" : doc_sheet);
            response.append("^");
            response.append(mgr.isEmpty(numberGenerator) ? "" : numberGenerator);
            response.append("^");
            response.append(mgr.isEmpty(numGenTrx) ? "" : numGenTrx);
            response.append("^");
            response.append(mgr.isEmpty(id1) ? "" : id1);
            response.append("^");
            response.append(mgr.isEmpty(id2) ? "" : id2);
            response.append("^");
            mgr.responseWrite(response.toString());
            mgr.endResponse();
      }

      else if ("DOC_CLASS".equals(val))
      {
         cmd = trans.addCustomFunction("DOCNAME","DOC_CLASS_API.Get_Name","SDOCCLASSNAME");
         cmd.addParameter("DOC_CLASS");

         cmd = trans.addCustomFunction("LASTDOCREV","Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);

         String docname = trans.getValue("DOCNAME/DATA/SDOCCLASSNAME");
         String last_doc_rev = trans.getValue("LASTDOCREV/DATA/KEEP_LAST_DOC_REV");

         trans.clear();
         cmd = trans.addCustomFunction("LASTDOCREVDECODE","ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");

         if (mgr.isEmpty(last_doc_rev))
            cmd.addParameter("TEMP1","F");
         else
            cmd.addParameter("TEMP1",last_doc_rev);

         trans = mgr.validate(trans);
         String last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
         trans.clear();
         txt = (mgr.isEmpty(docname) ? "" : docname) + "^"+last_doc_rev_decode+ "^";
         mgr.responseWrite(txt);
         mgr.endResponse();
      }
      else
      {
         mgr.showError("VALIDATE not implemented");
         mgr.endResponse();
      }
   }

   //-----------------------------------------------------------------------------
   //----------------------------  CMDBAR FUNCTIONS  -----------------------------
   //-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("HEAD"," DOC_REFERENCE_OBJECT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("LU_NAME",root_lu_name);
      cmd.setParameter("KEY_REF",root_key_ref);
      trans = mgr.perform(trans);
      dataBuffer = trans.getBuffer("HEAD/DATA");
      headset.addRow(dataBuffer);

      if (duplicated)
      {
         dataBuffer = headset.getRow();
         trans.clear();
         cmd = trans.addCustomFunction("GETDOCNAME","DOC_CLASS_API.Get_Name","SDOCCLASSNAME");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd = trans.addCustomFunction("GETDOCTITLE","DOC_TITLE_API.Get_Title","SDOCTITLE");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         trans = mgr.perform(trans);
         dataBuffer.setFieldItem("SDOCCLASSNAME",trans.getValue("GETDOCNAME/DATA/SDOCCLASSNAME"));
         dataBuffer.setFieldItem("SDOCTITLE",trans.getValue("GETDOCTITLE/DATA/SDOCTITLE"));
         headset.setRow(dataBuffer);
      }
      trans.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addQuery(headblk);

      if (mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );
      else{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
         q.addParameter("LU_NAME",root_lu_name);
         q.addParameter("KEY_REF",root_key_ref);
         //bug 58216 end
      }
         

      q.includeMeta("ALL");
      mgr.submit(trans);
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME",root_lu_name);
      q.addParameter("KEY_REF",root_key_ref);
      //bug 58216 end
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  searchDocuments()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addEmptyQuery(headblk);
      q.includeMeta("ALL");
      //bug 58216 starts
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME",root_lu_name);
      q.addParameter("KEY_REF",root_key_ref);
      //bug 58216 end

      mgr.submit(trans);
   }

   //-----------------------------------------------------------------------------
   //------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
   //-----------------------------------------------------------------------------

   public void  dlgOk()
   {
      ASPManager mgr = getASPManager();
      String doc_no = "";

      createnew = true;
      dlgset.changeRow();
      eval(dlgblk.generateAssignments());

      if (mgr.isEmpty(mgr.getASPField("DLG_DOC_CLASS").getValue()))
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVALREQCLASS: Required value for field (Doc. Class)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_NO").getValue()))
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVALREQDOCNO: Required value for field (Doc. No.)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_SHEET").getValue()))
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVALREQDOCSHEET: Required value for field (Doc. Sheet.)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_REV").getValue()))
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVALREQDOCREV: Required value for field (Doc. Rev.)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_TITLE").getValue()))
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVALREQTITLE: Required value for field (Title)."));
      else
      {
         trans.clear();

         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
         {
            String sAttr="";
            cmd = trans.addCustomFunction("GENNUMBER","DOC_TITLE_API.Generate_Doc_Number","DLG_DOC_NO");
            cmd.addParameter("DOC_CLASS",mgr.readValue("DLG_DOC_CLASS"));
            cmd.addParameter("BOOKING_LIST",mgr.readValue("BOOKING_LIST"));
            cmd.addParameter("ID1",mgr.readValue("ID1"));
            cmd.addParameter("ID2",mgr.readValue("ID2"));
            cmd.addParameter("ATTR",sAttr);
            trans = mgr.perform(trans);
            doc_no = trans.getValue("GENNUMBER/DATA/DLG_DOC_NO");

         }

         trans.clear();
         cmd = trans.addCustomCommand("CRENEWDOCPROC","DOC_TITLE_API.Create_New_Document_");
         cmd.addParameter("DLG_DOC_CLASS", mgr.getASPField("DLG_DOC_CLASS").getValue());
         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
         {
            cmd.addParameter("DLG_DOC_NO",doc_no);
         }
         else
         {
            cmd.addParameter("DLG_DOC_NO",dlgset.getRow().getValue("DLG_DOC_NO"));
         }
         cmd.addParameter("DLG_DOC_SHEET", mgr.getASPField("DLG_DOC_SHEET").getValue());
         cmd.addParameter("DLG_DOC_REV", mgr.getASPField("DLG_DOC_REV").getValue());
         cmd.addParameter("DLG_DOC_TITLE", mgr.getASPField("DLG_DOC_TITLE").getValue());

         cmd = trans.addCustomCommand("CRENEWREFPROC","DOC_REFERENCE_OBJECT_API.Create_New_Reference__");
         cmd.addParameter("LU_NAME",root_lu_name);
         cmd.addParameter("KEY_REF",root_key_ref);
         cmd.addReference("DLG_DOC_CLASS","CRENEWDOCPROC/DATA");
         cmd.addReference("DLG_DOC_NO","CRENEWDOCPROC/DATA");
         cmd.addReference("DLG_DOC_SHEET","CRENEWDOCPROC/DATA");
         cmd.addReference("DLG_DOC_REV","CRENEWDOCPROC/DATA");

         trans = mgr.perform(trans);

         bRefreshTree = true;
         createnew = false;
         dlgset.clear();
         trans.clear();
         createnew = false;
         okFind();
      }
   }


   public void  dlgCancel()
   {
      dlgset.clear();
   }


   public void  tranferToEdmMacro( String doc_type,String action) //TODO: Parameter types are not recognized and set them to String. Declare type[s] for (doc_type,action)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
         headset.selectRow();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,FILE_TYPE");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr,"EdmMacro.page",action,doc_type,data);
      bTranferToEDM = true;
      headset.unselectRows();
      headset.setFilterOff();
   }


   public void  viewCopy()
   {
      tranferToEdmMacro("VIEW","VIEW");
   }


   public void  viewOriginal()
   {
      tranferToEdmMacro("ORIGINAL","VIEW");
   }


   public void  createNewDoc()
   {
      ASPManager mgr = getASPManager();
      String doc_rev="",doc_sheet="",numberGenerator="",id1="",id2="",numGenTrx="";

      if (headset.countRows()>0)
      {
         int currrow = headset.getCurrentRowNo();
         if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
         dlg_doc_class = headset.getValue("DOC_CLASS");
         headset.goTo(currrow);
      }
      else
         dlg_doc_class = "";

      dlgset.clear();
      trans.clear();

      if (!mgr.isEmpty(dlg_doc_class))
      {
         cmd = trans.addCustomFunction("GETDEFREV","DOC_CLASS_DEFAULT_API.Get_Default_Value_","DLG_DOC_REV");
         cmd.addParameter("DOC_CLASS",dlg_doc_class);
         cmd.addParameter("TEMP1","DocTitle");
         cmd.addParameter("TEMP2","DOC_REV");

         cmd = trans.addCustomFunction("GETDEFSHEET","DOC_CLASS_DEFAULT_API.Get_Default_Value_","DLG_DOC_SHEET");
         cmd.addParameter("DOC_CLASS",dlg_doc_class);
         cmd.addParameter("TEMP1","DocTitle");
         cmd.addParameter("TEMP2","DOC_SHEET");

         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

         cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
         cmd.addParameter("DLG_DOC_CLASS",dlg_doc_class);
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_COUNTER");
         trans = mgr.perform(trans);

         numberGenerator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");

         id1 = trans.getValue("ID1COM/DATA/ID1");
         doc_sheet = trans.getValue("GETDEFSHEET/DATA/DLG_DOC_SHEET");
         doc_rev = trans.getValue("GETDEFREV/DATA/DLG_DOC_REV");
      }

      trans.clear();

         cmd = trans.addCustomFunction("GETCLIENTVAL","Doc_Number_Generator_Type_API.Decode","OUT_1");
         cmd.addParameter("NUMBER_GENERATOR",numberGenerator);
         trans = mgr.perform(trans);
         numGenTrx = trans.getValue("GETCLIENTVAL/DATA/OUT_1");
         trans.clear();

      if ("ADVANCED".equals(numberGenerator))
         { trans.clear();
           cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
           cmd.addParameter("DUMMY1",id1);
           trans = mgr.perform(trans);
           id2 = trans.getValue("ID2COM/DATA/ID2");
           trans.clear();
         }
      else
         {
            id1 = "";
            id2 = "";
         }

         if ("0".equals(id2))
         id2 = "";

         dataBuffer = mgr.newASPBuffer();
         dataBuffer.setFieldItem("DLG_DOC_CLASS",dlg_doc_class);
         dataBuffer.setFieldItem("DLG_DOC_NO","*");

         if (!mgr.isEmpty(dlg_doc_class))
         {
            dataBuffer.setFieldItem("DLG_DOC_REV",doc_rev);
            dataBuffer.setFieldItem("DLG_DOC_SHEET",doc_sheet);
            dataBuffer.setFieldItem("NUMBER_GENERATOR",numberGenerator);
            dataBuffer.setFieldItem("NUM_GEN_TRANSLATED",numGenTrx);
            dataBuffer.setFieldItem("ID1",id1);
            dataBuffer.setFieldItem("DLG_DOC_TITLE","");
            dataBuffer.setFieldItem("ID2",id2);
         }

      int n = dlgset.addRow(dataBuffer);
      dlgset.goTo(n);

      trans.clear();

      createnew = true;
   }


   public void  insertExistingDoc()
   {
      bInsertExistDoc = true;
   }


   public void  insertExistingDocOk()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomFunction("LASTDOCREV","Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
      cmd.addParameter("DOC_CLASS",mgr.readValue("RET_DOC_CLASS"));
      trans = mgr.perform(trans);

      String last_doc_rev = trans.getValue("LASTDOCREV/DATA/KEEP_LAST_DOC_REV");

      trans.clear();
      cmd = trans.addCustomFunction("LASTDOCREVDECODE","ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");

      if (mgr.isEmpty(last_doc_rev))
         cmd.addParameter("TEMP1", "F");
      else
         cmd.addParameter("TEMP1", last_doc_rev);

      trans = mgr.perform(trans);
      String last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
      trans.clear();

      newRow();
      headlay.setLayoutMode(headlay.NEW_LAYOUT);
      dataBuffer = headset.getRow();
      dataBuffer.setFieldItem("KEEP_LAST_DOC_REV", last_doc_rev_decode);
      dataBuffer.setFieldItem("DOC_CLASS", mgr.readValue("RET_DOC_CLASS"));
      dataBuffer.setFieldItem("SDOCCLASSNAME", mgr.readValue("RET_DOC_CLASS_NAME"));
      dataBuffer.setFieldItem("DOC_NO", mgr.readValue("RET_DOC_NO"));
      dataBuffer.setFieldItem("SDOCTITLE", mgr.readValue("RET_DOC_TITLE"));
      dataBuffer.setFieldItem("DOC_SHEET", mgr.readValue("RET_DOC_SHEET"));
      dataBuffer.setFieldItem("DOC_REV", mgr.readValue("RET_DOC_REV"));
      dataBuffer.setFieldItem("STATUS", mgr.readValue("RET_DOC_STATUS"));

      headset.setRow(dataBuffer);
   }


   public void  documentInfo()
   {
      ASPManager mgr = getASPManager();

      bDocInfo = true;
      headset.storeSelections();
      if (headlay.isSingleLayout())
         headset.selectRow();
      keys = headset.getSelectedRows("DOC_NO,DOC_CLASS,DOC_SHEET,DOC_REV");
      sNewWindowURL = "\"DocIssue.page?__TRANSFER="+mgr.URLEncode(keys.format())+"\"";
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DOC_CLASS","SDOCCLASSNAME,KEEP_LAST_DOC_REV").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDOCCLASS: Doc Class");

      headblk.addField("SDOCCLASSNAME").
      setSize(15).
      setReadOnly().
      setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORSDOCCLASSNAME: Name");

      headblk.addField("DOC_NO").
      setSize(10).
      setMandatory().
      setUpperCase().
      setReadOnly().
      setInsertable().
      setDynamicLOV("DOC_TITLE","DOC_CLASS").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDOCNO: Doc No");

      headblk.addField("SDOCTITLE").
      setSize(15).
      setReadOnly().
      setFunction("DOC_TITLE_API.Get_Title(:DOC_CLASS,:DOC_NO)").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORSDOCTITLE: Title");
      mgr.getASPField("DOC_NO").setValidation("SDOCTITLE");

      headblk.addField("DOC_SHEET").
      setSize(10).
      setMandatory().
      setUpperCase().
      setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO").
      setLabel("DOCSHEET: Doc Sheet");

      headblk.addField("DOC_REV").
      setSize(10).
      setMandatory().
      setUpperCase().
      setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
      setCustomValidation("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV","STATUS").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDOCREV: Doc Rev");

      headblk.addField("CATEGORY").
      setSize(10).
      setUpperCase().
      setDynamicLOV("DOC_REFERENCE_CATEGORY").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORCATEGORY: Category");

      headblk.addField("COPY_FLAG").
      setSize(10).
      setMandatory().
      setSelectBox().
      enumerateValues("DOC_REFERENCE_COPY_STATUS_API").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORCOPYFLAG: Copy Flag");

      headblk.addField("KEEP_LAST_DOC_REV").
      setSize(19).
      setMandatory().
      setSelectBox().
      enumerateValues("ALWAYS_LAST_DOC_REV_API").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORKEEPLASTDOCREV: Keep Last Doc Rev");

      headblk.addField("SURVEY_LOCKED_FLAG").
      setSize(19).
      setMandatory().
      setReadOnly().
      setInsertable().
      setSelectBox().
      enumerateValues("LOCK_DOCUMENT_SURVEY_API").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORSURVEYLOCKEDFLAG: Doc Connection Status");

      headblk.addField("LU_NAME").
      setSize(20).
      setMandatory().
      setHidden().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORLUNAME: Lu Name");

      headblk.addField("KEY_REF").
      setSize(20).
      setMandatory().
      setHidden().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORKEYREF: Key Ref.");

      headblk.addField("STATUS").
      setSize(20).
      setReadOnly().
      setFunction("substr(DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV),0,20)").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORSTATUS: Status");

      headblk.addField("EDMINFO").
      setFunction("''").
      setHidden();

      headblk.addField("DOC_TYPE").
      setFunction("''").
      setHidden();

      headblk.addField("EDMREPINFO").
      setFunction("''").
      setHidden();

      headblk.addField("FILE_TYPE").
      setHidden().
      setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

      headblk.setView("DOC_REFERENCE_OBJECT");
      headblk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.enableCommandGroup(headbar.CMD_GROUP_CUSTOM);
      headbar.addCustomCommand("createNewDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORCREATEDOC: Create New Document..."));
      headbar.addCustomCommand("insertExistingDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORINSERTDOC: Insert Existing Document..."));
      headbar.addCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVIEVOR: View Document"));
      headbar.addCustomCommand("viewCopy",mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORVIEWCO: View Copy"));
      headbar.addCustomCommand("documentInfo",mgr.translate("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORSHOWDOCINFO: Document Info..."));
      headbar.addCustomCommand("activateDocuments","Documents");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDOREFUNA: Documents");

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);


      //
      // Temp block
      //

      tempblk = mgr.newASPBlock("TEMP");
      tempblk.addField("TEMP1");
      tempblk.addField("TEMP2");


      //
      // Dlg
      //

      dlgblk = mgr.newASPBlock("DLG");

      dlgblk.addField("DLG_DOC_CLASS").
      setSize(30).
      setMandatory().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DLG_DOC_CLASS","DLG_DOC_REV,DLG_DOC_SHEET,NUMBER_GENERATOR,NUM_GEN_TRANSLATED,ID1,ID2").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDLGDOCCLASS: Document Class");

      dlgblk.addField("DLG_DOC_NO").
      setSize(30).
      setMandatory().
      setUpperCase().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDLGDOCNO: No.");

      dlgblk.addField("DLG_DOC_TITLE").
      setSize(30).
      setMandatory().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDLGDOCREV: Title");

      dlgblk.addField("DLG_DOC_SHEET").
      setSize(30).
      setMandatory().
      setUpperCase().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDLGDOCSHEET: Doc Sheet");

      dlgblk.addField("DLG_DOC_REV").
      setSize(30).
      setMandatory().
      setUpperCase().
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDLGDOCTITLE: Revision");

      dlgblk.addField("NUMBER_GENERATOR").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setHidden().
      setFunction("''");

      dlgblk.addField("NUM_GEN_TRANSLATED").
      setReadOnly().
      setUpperCase().
      setFunction("''").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORNUMGENERATOR: Number Generator");

      dlgblk.addField("ID1").
      setSize(20).
      setReadOnly().
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id1Lov.page").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORNUMCOUNTID1: Number Counter ID1");

      dlgblk.addField("ID2").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id2Lov.page","ID1").
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORNUMCOUNTID2: Number Counter ID2");

      dlgblk.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
	   setUpperCase().
      setLOV("BookListLov.page", "ID1,ID2").//Bug Id 73606
      setLabel("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORNUMCOUNTID2BOOKINGLIST: Booking List");

      dlgblk.addField("DUMMY1").
      setFunction("''").
      setHidden();

      dlgblk.addField("DUMMY2").
      setFunction("''").
      setHidden();

      dlgblk.addField("ATTR").
      setFunction("''").
      setHidden();

      dlgblk.addField("OUT_1").
      setFunction("''").
      setHidden();


      dlgblk.setTitle("Create New Document");

      dlgset = dlgblk.getASPRowSet();
      dlgbar = mgr.newASPCommandBar(dlgblk);
      dlgbar.enableCommand(dlgbar.OKFIND);
      dlgbar.defineCommand(dlgbar.OKFIND,"dlgOk");
      dlgbar.enableCommand(dlgbar.CANCELFIND);
      dlgbar.defineCommand(dlgbar.CANCELFIND,"dlgCancel");

      dlglay = dlgblk.getASPBlockLayout();
      dlglay.setDialogColumns(2);
      dlglay.setDefaultLayoutMode(dlglay.CUSTOM_LAYOUT);
      dlglay.setEditable();


      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORDOCSTAB: Documents","javascript:commandSet('HEAD.activateDocuments','')");
      tabs.setLeftTabSpace(3);
      tabs.setContainerSpace(6);
      tabs.setTabWidth(125);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      headbar.removeCustomCommand("activateDocuments");

      strBodyTag = mgr.generateBodyTag();
      if (bInsertExistDoc)
      {
         strBodyTag=mgr.replace(strBodyTag,"javascript:onLoad","javascript:onLoad_");

      }
   }


   public void  activateDocuments()
   {
      tabs.setActiveTab(1);
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return null;
   }

   protected String getTitle()
   {
      return null;
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCREFERENCEOBJECTFUNCTIONNAVIGATORTITLE: Documents General"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(strBodyTag);
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("  <input type=\"hidden\" name=\"CHECK_INSERT\" value=\"");
      out.append(insertdoc);
      out.append("\">\n");
      out.append("  <input type=\"hidden\" name=\"INSERT_DATA\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_CLASS\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_CLASS_NAME\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_NO\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_SHEET\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_TITLE\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_REV\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"RET_DOC_STATUS\" value=\"\">  \n");
      out.append("  <input type=\"hidden\" name=\"SELECT_INSERT_FILE\" value=\"FALSE\">\n");
      if (createnew)
      {
         out.append(dlglay.show());
      }
      else
      {
         out.append(tabs.showTabsInit());
         out.append(headlay.show());
         out.append(tabs.showTabsFinish());
      }

      //----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  ----------------------------
      //----------------------------------------------------------------------------


      appendDirtyJavaScript("function openDocumentInsert()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    openLOVWindow('INSERT_DATA',-1,'");
      appendDirtyJavaScript(root_path);
      appendDirtyJavaScript("docmaw/DocIssueLov2.page',500,500,'validateInsertData');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovInsertData(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if (getValue_('CHECK_INSERT',-1)==\"Y\")\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    openLOVWindow('INSERT_DATA',i,\n");
      appendDirtyJavaScript("        'DocIssueLov2.page'\n");
      appendDirtyJavaScript("        ,600,445,'validateInsertData');\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateInsertData(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = ");
      appendDirtyJavaScript(inserted_row);
      appendDirtyJavaScript(";     \n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=INSERT_DATA'\n");
      appendDirtyJavaScript("        + '&INSERT_DATA=' + getValue_('INSERT_DATA',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'DOC_REV',i,'Doc.Rev.') )\n");
      appendDirtyJavaScript("    {        \n");
      appendDirtyJavaScript("        fld = getField_('CHECK_INSERT',-1);\n");
      appendDirtyJavaScript("        fld.value = fld.defaultValue = \"N\";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    ret_val = document.form.INSERT_DATA.value;\n");
      appendDirtyJavaScript("    ret_string_Array=new Array();\n");
      appendDirtyJavaScript("    ret_string_Array=ret_val.split(\"^\");\n");
      appendDirtyJavaScript("    document.form.RET_DOC_CLASS.value = ret_string_Array[0];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_CLASS_NAME.value = ret_string_Array[1];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_NO.value = ret_string_Array[2];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_TITLE.value = ret_string_Array[3];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_SHEET.value = ret_string_Array[4];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_REV.value = ret_string_Array[5];\n");
      appendDirtyJavaScript("    document.form.RET_DOC_STATUS.value = ret_string_Array[6];   \n");
      appendDirtyJavaScript("    document.form.SELECT_INSERT_FILE.value = \"TRUE\"; \n");
      appendDirtyJavaScript("    submit(); \n");
      appendDirtyJavaScript("}\n");


      if (bDocInfo)
      {
         // Open Document Info Window
         appendDirtyJavaScript("window.open(");
         appendDirtyJavaScript(sNewWindowURL);
         appendDirtyJavaScript(",\"anotherWindow\");\n");
      }


      appendDirtyJavaScript("function refreshTree()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   parent_url = this.parent.location.href;\n");
      appendDirtyJavaScript("   if (parent_url.indexOf(\"FunctionStructureNavigator.page\")!=-1) // if not unlocked\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      this.parent.frames[\"childnavwinfrm\"].refresh('FALSE');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      if (bRefreshTree)
      {
         appendDirtyJavaScript("refreshTree();\n");
         bRefreshTree = false;
      }


      appendDirtyJavaScript("function onLoad_()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   message = readCookie(f.__PAGE_ID.value);      \n");
      appendDirtyJavaScript("   onLoad();\n");
      appendDirtyJavaScript("// Open Insert Document LOV and insert data of a existing document\n");
      appendDirtyJavaScript("   if( message.length <= 1 )\n");
      appendDirtyJavaScript("        openDocumentInsert();\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("        fld = getField_('CHECK_INSERT',-1);\n");
      appendDirtyJavaScript("        fld.value = fld.defaultValue = \"N\";\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}  \n");

      if (bTranferToEDM)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
