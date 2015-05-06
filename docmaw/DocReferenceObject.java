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
*  File        : DocReferenceObject.java
*  Modified    :
*    nisilk - 2003-01-06  - Added Doc Sheet
*    inoslk - 2003-03-28  - Modified methods doReset() and clone().
*    nisilk - 2003-10-22  - Fixed Call 107798, Added doc sheet and Added overriden function closeLOVWindow as a workaround for the focus problrm.
*    nisilk - 2003-10-29  - Changed the localized tag of DOC_SHEET.
*    nisilk - 2003-11-06  - Added config doc no functionality.
*    bakalk - 2004-02-17  - Replaced document.ClientUtil.connect with __connect.
*    dikalk - 2004-03-25  - SP1 merge. merged the following bugs: 42811, 40602,
*    bakalk - 2005-01-27  - Fixed the call 121422.
*    Amnalk - 2005-10-31  - Merged Bug Id 53112.
*    BAKALK - 2006-07-19  - Bug ID 58216, Fixed Sql Injection.
*    NaLrlk - 2007-08-09  - XSS Correction.
*    AmNilk - 2007-08-10  - Eliminated SQLInjections security vulnerability.
*    SHTHLK - 2008-03-17  - Bug ID 72013, Modified okFind()
* -----------------------APP7.5 SP7---------------------------------------------------
*    ARWILK - 2010-07-21  - Bug ID 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
* ------------------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import ifs.docmaw.edm.*;

public class DocReferenceObject extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocReferenceObject");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;
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
   private ASPField        f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;
   private ASPBuffer keys;
   private ASPBuffer dataBuffer;

   private boolean bDocInfo;
   private boolean bRefreshTree;
   private boolean duplicated;
   private boolean createnew;
   private boolean bTranferToEDM;
   private boolean multirow;
   private boolean bInsertExistDoc;

   private String root_path;
   private String root_lu_name;
   private String root_key_ref;
   private String activetab;
   private String insertdoc;
   private String sNewWindowURL;
   private String comnd;
   private String doc_rev;
   private String searchURL;
   private String doc_class;
   private String doc_sheet;
   private String doc_no;
   private String strBodyTag;
   private String txt;
   private String sFilePath;

   private int inserted_row;
   private int currrow;
   private int n;

   //===============================================================
   // Construction
   //===============================================================
   public DocReferenceObject(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      cmd   = null;
      q   = null;
      keys   = null;
      dataBuffer = null;

      bDocInfo   = false;
      bRefreshTree   = false;
      duplicated   = false;
      createnew   = false;
      multirow   = false;
      bInsertExistDoc   = false;
      bTranferToEDM   = false;

      root_path   = null;
      root_lu_name   = null;
      root_key_ref   = null;
      activetab   = null;
      insertdoc   = null;
      sNewWindowURL   = null;
      comnd   = null;
      doc_rev   = null;
      searchURL   = null;
      doc_class   = null;
      doc_no   = null;
      doc_sheet=null;
      strBodyTag   = null;
      txt   = null;
      sFilePath   = null;

      inserted_row   = 0;
      currrow   = 0;
      n   = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocReferenceObject page = (DocReferenceObject)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.cmd   = null;
      page.q   = null;
      page.keys   = null;
      page.dataBuffer = null;

      page.multirow   = false;
      page.bInsertExistDoc   = false;
      page.bDocInfo   = false;
      page.bRefreshTree   = false;
      page.duplicated   = false;
      page.createnew   = false;
      page.bTranferToEDM   = false;

      page.root_path   = null;
      page.root_lu_name   = null;
      page.root_key_ref   = null;
      page.activetab   = null;
      page.insertdoc   = null;
      page.sNewWindowURL   = null;
      page.comnd   = null;
      page.doc_class   = null;
      page.doc_no   = null;
      page.doc_sheet = null;
      page.strBodyTag   = null;
      page.doc_rev   = null;
      page.searchURL   = null;
      page.txt   = null;
      page.sFilePath   = null;

      page.inserted_row   = 0;
      page.currrow   = 0;
      page.n   = 0;

      // Cloning immutable attributes
      page.fmt = page.getASPHTMLFormatter();
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
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      multirow = ctx.readFlag("MULTIROW",true);
      root_lu_name = ctx.readValue("ROOTLUNAME","");
      root_key_ref = ctx.readValue("ROOTKEYREF","");

      bInsertExistDoc = false;
      inserted_row = 0;
      insertdoc = "N";
      bDocInfo = false;
      bRefreshTree = false;
      sNewWindowURL = "";
      duplicated = false;
      createnew = false;
      bTranferToEDM = false;


      if(mgr.commandBarActivated())
      {
         comnd = mgr.readValue("__COMMAND");

         if  ( "HEAD.DuplicateRow".equals(comnd) )
            duplicated = true;

         if  (( "HEAD.SaveReturn".equals(comnd) )|| ( "HEAD.SaveNew".equals(comnd) )|| ( "HEAD.Delete".equals(comnd) ))
            bRefreshTree = true;

         eval(mgr.commandBarFunction());
      }
      else if( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());		//EVALInjections_Safe AMNILK 20070810
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")))
         okFind();
      else if(mgr.dataTransfered())
         okFind();

      else if ( "TRUE".equals(mgr.readValue("SELECT_INSERT_FILE")) )
         insertExistingDocOk();
      else if( mgr.buttonPressed("OK") )
         dlgOk();
      else if( mgr.buttonPressed("CANCEL") )
         dlgCancel();

      adjust();

      ctx.writeFlag("MULTIROW",multirow);
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

      String val = mgr.readValue("VALIDATE");
      String last_doc_rev="";
      String doc_sheet="";
      String numberGenerator="";
      String numGenTrx = "";
      String id1 = "";
      String id2 = "";

      if  ( "DOC_REV".equals(val) )
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETSTATUS", "DOC_ISSUE_API.Get_State","TEMP1");

         cmd.addParameter("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

         trans = mgr.validate(trans);
         String sDocStatus = trans.getValue("GETSTATUS/DATA/TEMP1");
         trans.clear();

         mgr.responseWrite(sDocStatus+"^");
         mgr.endResponse();
      }

      else if  ( "INSERT_DATA".equals(val) )
      {
         String data = mgr.readValue("INSERT_DATA");
         mgr.responseWrite(data);
         mgr.endResponse();
      }

      else if ( "DOC_CLASS".equals(val) )
      {
         if (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")))
            root_lu_name=mgr.readValue("LU_NAME");

         cmd = trans.addCustomFunction("NAME","DOC_CLASS_API.Get_Name","SDOCCLASSNAME");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);
         String doc_name = trans.getValue("NAME/DATA/SDOCCLASSNAME");
         trans.clear();

         cmd = trans.addCustomFunction("LASTDOCREV","Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_","KEEP_LAST_DOC_REV");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);
         last_doc_rev = trans.getValue("LASTDOCREV/DATA/KEEP_LAST_DOC_REV");
         trans.clear();

         cmd = trans.addCustomFunction("LASTDOCREVDECODE","ALWAYS_LAST_DOC_REV_API.Decode","KEEP_LAST_DOC_REV");
         if (mgr.isEmpty(last_doc_rev))
         	  cmd.addParameter("DUMMY","F");
         else
             cmd.addParameter("DUMMY",last_doc_rev);
         trans = mgr.validate(trans);
         String last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
         trans.clear();

         txt = (mgr.isEmpty(doc_name) ? "" : doc_name ) + "^"+ last_doc_rev_decode + "^" + numberGenerator + "^";
         mgr.responseWrite(txt);
         mgr.endResponse();
      }
	
      else if ( "DLG_DOC_CLASS".equals(val) )
      {
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
      else if ("DOC_NO".equals(val))
      {
	 trans.clear();
	 cmd = trans.addCustomFunction("GETTITLE1","DOC_TITLE_API.Get_Title","OUT_1");
	 cmd.addParameter("DOC_CLASS");
	 cmd.addParameter("DOC_NO");
	 trans = mgr.validate(trans);

         String title = trans.getValue("GETTITLE1/DATA/OUT_1");

         txt = (mgr.isEmpty(title) ? "" : title + "^") ;
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
      cmd = trans.addEmptyCommand("HEAD","DOC_REFERENCE_OBJECT_API.New__",headblk);
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
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));

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

      searchURL = mgr.createSearchURL(headblk);

      if (!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME")))
      {
         root_lu_name = mgr.readValue("LU_NAME");
         root_key_ref = mgr.readValue("KEY_REF");
      }

      trans.clear();
      q = trans.addQuery(headblk);
      if(mgr.dataTransfered())
        q.addOrCondition( mgr.getTransferedData() );
      else{
         //bug 58216 starts
         q.addWhereCondition("LU_NAME= ? AND KEY_REF= ?");
         q.addParameter("LU_NAME",root_lu_name);
         q.addParameter("KEY_REF",root_key_ref);
         //bug 58216 ends 
      }
        
      q.includeMeta("ALL");

      mgr.querySubmit (trans,headblk);//Bug Id 72013
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("LU_NAME= ? AND KEY_REF= ?");
      q.addParameter("LU_NAME",root_lu_name);
      q.addParameter("KEY_REF",root_key_ref);
      //bug 58216 ends 
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BUTTON FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  dlgOk()
   {
      ASPManager mgr = getASPManager();
      String doc_no = "";

      createnew = true;
      dlgset.changeRow();
      eval(dlgblk.generateAssignments());

      if (mgr.isEmpty(mgr.getASPField("DLG_DOC_CLASS").getValue()))
         mgr.showError(mgr.translate("DOCMAWDOCREFERENCEOBJECTVALREQCLASS: Required value for field (Doc. Class)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_NO").getValue()))
         mgr.showError(mgr.translate("DOCMAWDOCREFERENCEOBJECTVALREQDOCNO: Required value for field (Doc. No.)."));

      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_SHEET").getValue()))
         mgr.showError(mgr.translate("DOCMAWDOCREFERENCEOBJECTVALREQDOCSHEET: Required value for field (Doc. Sheet.)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_REV").getValue()))
         mgr.showError(mgr.translate("DOCMAWDOCREFERENCEOBJECTVALREQDOCREV: Required value for field (Doc. Rev.)."));
      else if (mgr.isEmpty(mgr.getASPField("DLG_DOC_TITLE").getValue()))
         mgr.showError(mgr.translate("DOCMAWDOCREFERENCEOBJECTVALREQTITLE: Required value for field (Title)."));
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
         cmd.addParameter("DLG_DOC_CLASS",mgr.getASPField("DLG_DOC_CLASS").getValue());
         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
         {
            cmd.addParameter("DLG_DOC_NO",doc_no);
         }
         else
         {
            cmd.addParameter("DLG_DOC_NO",dlgset.getRow().getValue("DLG_DOC_NO"));
         }
         cmd.addParameter("DLG_DOC_SHEET",mgr.getASPField("DLG_DOC_SHEET").getValue());
         cmd.addParameter("DLG_DOC_REV",mgr.getASPField("DLG_DOC_REV").getValue());
         cmd.addParameter("DLG_DOC_TITLE",mgr.getASPField("DLG_DOC_TITLE").getValue());

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

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  createNewDoc()
   {
      ASPManager mgr = getASPManager();
      String dlg_doc_class,numberGenerator="",id1="",id2="",numGenTrx="",doc_sheet="",doc_rev="";

      if (headset.countRows()>0)
      {
         currrow = headset.getCurrentRowNo();
         if(headlay.isMultirowLayout())
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

      n = dlgset.addRow(dataBuffer);
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
         cmd.addParameter("TEMP1","F");
      else
         cmd.addParameter("TEMP1",last_doc_rev);
      trans = mgr.perform(trans);
      String last_doc_rev_decode = trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV");
      trans.clear();

      newRow();
      headlay.setLayoutMode(headlay.NEW_LAYOUT);
      dataBuffer = headset.getRow();
      dataBuffer.setFieldItem("KEEP_LAST_DOC_REV",last_doc_rev_decode);
      dataBuffer.setFieldItem("DOC_CLASS",mgr.readValue("RET_DOC_CLASS"));
      dataBuffer.setFieldItem("SDOCCLASSNAME",mgr.readValue("RET_DOC_CLASS_NAME"));
      dataBuffer.setFieldItem("DOC_NO",mgr.readValue("RET_DOC_NO"));

      dataBuffer.setFieldItem("DOC_SHEET",mgr.readValue("RET_DOC_SHEET"));
      dataBuffer.setFieldItem("SDOCTITLE",mgr.readValue("RET_DOC_TITLE"));
      dataBuffer.setFieldItem("DOC_REV",mgr.readValue("RET_DOC_REV"));
      dataBuffer.setFieldItem("STATUS",mgr.readValue("RET_DOC_STATUS"));

      headset.setRow(dataBuffer);
   }


   public void  documentInfo()
   {
      ASPManager mgr = getASPManager();

      bDocInfo = true;
      headset.storeSelections();
      if(headlay.isSingleLayout())
         headset.selectRow();

      keys = headset.getSelectedRows("DOC_NO,DOC_CLASS,DOC_SHEET,DOC_REV");
      sNewWindowURL = "\"DocIssue.page?__TRANSFER="+mgr.URLEncode(keys.format())+"\"";
   }


   public void  tranferToEdmMacro( String doc_type,String action)
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


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("DOC_CLASS");
      f.setSize(17);
      f.setMaxLength(12);
      f.setDynamicLOV("DOC_CLASS");
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDOCCLASS: Doc Class");
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();
      f.setCustomValidation("DOC_CLASS,LU_NAME","SDOCCLASSNAME,KEEP_LAST_DOC_REV");

      f = headblk.addField("SDOCCLASSNAME");
      f.setSize(20);
      f.setMaxLength(2000);
      f.setLabel("DOCMAWDOCREFERENCEOBJECTSDOCCLASSNAME: Name");
      f.setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)");
      
      f.setReadOnly();

      f = headblk.addField("DOC_NO");
      f.setSize(17);
      f.setMaxLength(120);
      f.setDynamicLOV("DOC_TITLE","DOC_CLASS");
      f.setLOVProperty("AUTO_SEARCH","Y");
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDOCNO: Doc No");
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();

      f = headblk.addField("DOC_SHEET");
      f.setSize(20);
      f.setMaxLength(10);
      f.setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO");
      f.setLOVProperty("AUTO_SEARCH","Y");
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDOCSHEET: Doc Sheet");
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();


      f = headblk.addField("SDOCTITLE");
      f.setSize(20);
      f.setMaxLength(250);
      f.setLabel("DOCMAWDOCREFERENCEOBJECTSDOCTITLE: Title");
      f.setFunction("DOC_TITLE_API.Get_Title(:DOC_CLASS,:DOC_NO)");
      mgr.getASPField("DOC_NO").setCustomValidation("DOC_CLASS,DOC_NO,","SDOCTITLE");
      f.setReadOnly();
      

      f = headblk.addField("DOC_REV");
      f.setMaxLength(6);
      f.setSize(17);
      f.setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO");
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDOCREV: Doc Rev");
      f.setUpperCase();
      f.setCustomValidation("DOC_CLASS,DOC_NO,DOC_REV","STATUS");

      f = headblk.addField("CATEGORY");
      f.setSize(17);
      f.setMaxLength(5);
      f.setDynamicLOV("DOC_REFERENCE_CATEGORY");
      f.setLabel("DOCMAWDOCREFERENCEOBJECTCATEGORY: Category");
      f.setUpperCase();

      f = headblk.addField("COPY_FLAG");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTCOPYFLAG: Copy Flag");
      f.setSelectBox();
      f.enumerateValues("DOC_REFERENCE_COPY_STATUS_API");

      f = headblk.addField("KEEP_LAST_DOC_REV");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTKEEPLASTDOCREV: Keep Last Doc Rev");
      f.setSelectBox();
      f.enumerateValues("ALWAYS_LAST_DOC_REV_API");

      f = headblk.addField("SURVEY_LOCKED_FLAG");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTSURVEYLOCKEDFLAG: Doc Connection Status");
      f.setReadOnly();
      f.setInsertable();
      f.setSelectBox();
      f.enumerateValues("LOCK_DOCUMENT_SURVEY_API");

      f = headblk.addField("LU_NAME");
      f.setSize(20);
      f.setMaxLength(30);
      f.setMandatory();
      f.setHidden();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTLUNAME: Lu Name");

      f = headblk.addField("KEY_REF");
      f.setSize(20);
      f.setMaxLength(600);
      f.setMandatory();
      f.setHidden();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTKEYREF: Key Ref");

      f = headblk.addField("STATUS");
      f.setSize(20);
      f.setMaxLength(253);
      f.setLabel("DOCMAWDOCREFERENCEOBJECTSTATUS: Status");

      f.setFunction("substr(DOC_ISSUE_API.Get_State(DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV),0,20)");
      f.setReadOnly();

      f = headblk.addField("FILE_TYPE");
      f.setHidden();
      f.setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

      headblk.setView("DOC_REFERENCE_OBJECT");
      headblk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("createNewDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTCREATEDOC: Create New Document..."));
      headbar.addCustomCommand("insertExistingDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTINSERTDOC: Insert Existing Document..."));
      //----- File Operations -------------------
      headbar.addCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCREFERENCEOBJECTVIEVOR: View Document"));
      headbar.addCustomCommand("viewCopy",mgr.translate("DOCMAWDOCREFERENCEOBJECTVIEWCO: View Copy"));
      //-----------------------------------------
      headbar.addCustomCommand("documentInfo",mgr.translate("DOCMAWDOCREFERENCEOBJECTSHOWDOCINFO: Document Info..."));
      headbar.addCustomCommand("activateGeneral","General");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCMAWDOCREFERENCEOBJECTDOREOB: Document General");

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      //---------------------------------------------------------------------------------------------------
      tempblk = mgr.newASPBlock("TEMP");

      f = tempblk.addField("TEMP1");
      f = tempblk.addField("TEMP2");
      f = tempblk.addField("DUMMY");

      //---------------------------------------------------------------------------------------------------

      dlgblk = mgr.newASPBlock("DLG");

      f = dlgblk .addField("DLG_DOC_CLASS");
      f.setSize(17);
      f.setDynamicLOV("DOC_CLASS");
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDLGDOCCLASS: Doc Class");
      f.setCustomValidation("DLG_DOC_CLASS","DLG_DOC_REV,DLG_DOC_SHEET,NUMBER_GENERATOR,NUM_GEN_TRANSLATED,ID1,ID2");

      f = dlgblk .addField("DLG_DOC_NO");
      f.setSize(20);
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDLGDOCNO: No");

      f = dlgblk .addField("DLG_DOC_SHEET");
      f.setSize(20);
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDLGDOCSHEET: Sheet");


      f = dlgblk .addField("DLG_DOC_REV");
      f.setSize(20);
      f.setMandatory();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDLGDOCTITLE: Revision");

      f = dlgblk .addField("DLG_DOC_TITLE");
      f.setSize(20);
      f.setMandatory();
      f.setLabel("DOCMAWDOCREFERENCEOBJECTDLGDOCREV: Title");

      f = dlgblk .addField("NUMBER_GENERATOR");
      f.setSize(20);
      f.setReadOnly();
      f.setUpperCase();
      f.setHidden();
      f.setFunction("''");

      f = dlgblk.addField("NUM_GEN_TRANSLATED");
      f.setReadOnly();
      f.setUpperCase();
      f.setFunction("''");
      f.setLabel("DOCREFERENCENUMBERGENERATOR: Number Generator");

      f = dlgblk.addField("ID1");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(30);
      f.setFunction("''");
      f.setUpperCase();
      f.setLOV("Id1Lov.page");
      f.setLabel("DOCMAWDOCREFERENCEOBJECTID1: Number Counter ID1");

      f = dlgblk.addField("ID2");
      f.setSize(20);
      f.setMaxLength(30);
      f.setFunction("''");
      f.setUpperCase();
      f.setLOV("Id2Lov.page","ID1");
      f.setLabel("DPCMAWDOCREFERENCEOBJECTID2: Number Counter ID2");

      f = dlgblk.addField("BOOKING_LIST");
      f.setSize(20);
      f.setMaxLength(30);
      f.setFunction("''");
	   f.setUpperCase();
      f.setLOV("BookListLov.page", "ID1,ID2");//Bug Id 73606
      f.setLabel("DOCMAWDOCREFERENCEOBJECTBOOKINGLIST: Booking List");

      f = dlgblk.addField("DUMMY1");
      f.setFunction("''");
      f.setHidden();

      f = dlgblk.addField("DUMMY2");
      f.setFunction("''");
      f.setHidden();

      f = dlgblk.addField("ATTR");
      f.setFunction("''");
      f.setHidden();

      f = dlgblk.addField("OUT_1");
      f.setFunction("''");
      f.setHidden();


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

      //---------------------------------------------------------------------------------------------------
      // Tabs
      //---------------------------------------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab("DOCMAWDOCREFERENCEOBJECTGENERAL: General","javascript:commandSet('HEAD.activateGeneral','')");
      tabs.setLeftTabSpace(3);
      tabs.setContainerSpace(6);
      tabs.setTabWidth(125);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      headbar.removeCustomCommand("activateGeneral");

      strBodyTag = mgr.generateBodyTag();
      if (bInsertExistDoc)
      {
         //re = /javascript:onLoad/gi;
         //strBodyTag = strBodyTag.replace(re, "javascript:onLoad_");
         strBodyTag=mgr.replace(strBodyTag,"javascript:onLoad","javascript:onLoad_");
      }
   }


   public void  tabsInit()
   {
      ASPManager mgr = getASPManager();

      eval("tabs.setActiveTab("+activetab+"+1)");
      mgr.responseWrite(tabs.showTabsInit());
   }


   public void  tabsFinish()
   {
      ASPManager mgr = getASPManager();

       mgr.responseWrite(tabs.showTabsFinish());
   }


   public void  activateGeneral()
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
      out.append(mgr.generateHeadTag("DOCMAWDOCREFERENCEOBJECTTITLE: Document General"));
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
      if (createnew) {
         out.append(dlglay.show());
      }
	  else {
        out.append(tabs.showTabsInit());
        out.append(headlay.show());

		if  ( headset.countRows() == 0 ) {
		  out.append("       <input type=hidden name=DOC_REV value=\"\">   \n");
	      out.append("         <ul>\n");
		  out.append("            <li>");
		  out.append(fmt.showCommandLink("createNewDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTCREATEDOC: Create New Document...")));
		  out.append("            </li>\n");
		  out.append("            <li>");
		  out.append(fmt.showCommandLink("insertExistingDoc",mgr.translate("DOCMAWDOCREFERENCEOBJECTINSERTDOC: Insert Existing Document...")));
		  out.append("            </li>\n");
		  out.append("         </ul>\n");
       }
       out.append(tabs.showTabsFinish());
     }

     //-----------------------------------------------------------------------------
     //----------------------------  CLIENT FUNCTIONS  -----------------------------
     //-----------------------------------------------------------------------------

     if (bTranferToEDM) {
       appendDirtyJavaScript("// Open EdmMacro.page file\n");
       appendDirtyJavaScript("   window.open(\"");
       appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
       appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
     }

     appendDirtyJavaScript("function openDocumentInsert()\n");
     appendDirtyJavaScript("{\n");
     appendDirtyJavaScript("    openLOVWindow('INSERT_DATA',-1,'");
     appendDirtyJavaScript(root_path);
     appendDirtyJavaScript("docmaw/DocIssueLov2.page',500,500,'validateInsertData');\n");
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


     if (bDocInfo) {
      appendDirtyJavaScript("// Open Document Info Window.\n");
      appendDirtyJavaScript("window.open(");
      appendDirtyJavaScript(sNewWindowURL);
      appendDirtyJavaScript(",\"anotherWindow\");\n");
     }

     appendDirtyJavaScript("function refreshTree()\n");
     appendDirtyJavaScript("{\n");
     appendDirtyJavaScript("   parent_url = this.parent.location.href;\n");
     appendDirtyJavaScript("   if (parent_url.indexOf(\"ProductDataNavigator.page\")!=-1)\n");
     appendDirtyJavaScript("   {\n");
     appendDirtyJavaScript("      this.parent.frames[\"childnavwinfrm\"].refresh('FALSE');\n");
     appendDirtyJavaScript("   }\n");
     appendDirtyJavaScript("}\n");

     if (bRefreshTree) {
      appendDirtyJavaScript("refreshTree();\n");
      bRefreshTree = false;
     }

     appendDirtyJavaScript("function onLoad_()\n");
     appendDirtyJavaScript("{\n");
     appendDirtyJavaScript("   message = readCookie(f.__PAGE_ID.value);      \n");
     appendDirtyJavaScript("   onLoad();\n");
     appendDirtyJavaScript("// Open Insert Document LOV and insert data of a existing document\n");
     appendDirtyJavaScript("   if( message.length <= 1 )\n");
     appendDirtyJavaScript("      openDocumentInsert();\n");
     appendDirtyJavaScript("   else\n");
     appendDirtyJavaScript("   {\n");
     appendDirtyJavaScript("      fld = getField_('CHECK_INSERT',-1);\n");
     appendDirtyJavaScript("      fld.value = fld.defaultValue = \"N\";\n");
     appendDirtyJavaScript("   }\n");
     appendDirtyJavaScript("}  \n");

     out.append(mgr.endPresentation());
     out.append("</form>\n");
     out.append("</body>\n");
     out.append("</html>");
     return out;
   }

}
