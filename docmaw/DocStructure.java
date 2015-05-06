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
*  File         : DocStructure.java
*  Description  : Page to handle file operations on Document Structures
*  ------------------------------------------------------------------------------
*  09-09-2004    Dikalk    Created.
*  25-01-2005    Bakalk    Fixed the call 121031.
*  27-05-2005    Shthlk    Fixed the call 124228, Modified checkOutStructure()
*  23-06-2005    Bakalk    Fixed the call 124107.
*  xx-xx-xxxx    SukMlk    Added functions getFileTypeFromFileName
*  26-07-2005    SukMlk    Fixed call 125170, made changes to the way structure documents are checked in.
*  06-09-2005    SukMlk    Fixed call 125669
*  06-09-2005    SukMlk    Call 125670 additional fix.
*  07-10-2005    Amnalk    Fixed call 126675.
*  2005-10-20    SukMlk    Call 125670. Some stuff done in the earlier fix generates a foundation level bug.
*                          Did some stuff to sidestep this. Read the file attached to the call for more info.
*  2005-10-27    Dikalk    Changed logic behind send mail to also include all extended files, view copies and 
*                          redline files.
*  2005-11-10    AMNALK    Fixed Call 128624.
*  2005-12-20    SukMlk    Fixed call 126601.
*  2005-01-04    DiKalk    Added code to ensure that the previously select file type is used as default 
*                          in the file open dialog, during a checkin.
*  2006-01-30    SukMlk    Refixed the above, after a functionality overwrite... :)
*  2006-02-09    DiKalk    Implemented Checkin/Checkout reports for structure operations
*  2006-07-27    Nijalk    Bug 54793, Modified java script function executeServerCommand().
*  2006-08-04    Nijalk    Bug 55611, Modified java script function setDefaultCheckoutPath().
*  2007-08-08    JANSLK    Changed all non-translatable constants.
*  2007-08-09    ASSALK    Merged Bug Id 65968, Handle the apostrophe in the title of the structure documents.
*  2007-10-13    AMNILK    Eliminated SQL Injections Security Vulnerability.
*  2007-08-13    NaLrlk    XSS Correction.
*  2007-08-15    ASSALK    Merged Bug 58526, Modified drawCheckOutUI() and drawRadio(). Added drawLabel().
*  2007-08-15    ASSALK    Merged Bug Id 66906, Modified the select statement for getting file types in getFileTypes().
*  2007-08-27    DinHlk    Call 147549, Moved javascript functions validateSelect() and validateEdit() from preDefine() 
*                          to getContents() since the framework is creating a duplicate method for validate javascript functions 
*                          even when developer has defined a method inside DocStructure-en.js. 
*  ----------------------------------------------------------------------------------
* -----------------------APP7.5 SP1--------------------------------------------------
*   071213       ILSOLK     Bug Id 68773, Eliminated XSS.
*  2008-03-27    VIRALK    Bug Id 71463, Added new function adjustFileName() and Modified viewStructureWithExternalViewer()
*  2008-07-07    AMNALK    Bug Id 72460, Added new variable to the buffer header in viewStructureWithExternalViewer().
*  2008-07-22    VIRALK    Bug Id 74523, Modified adjustFileName(),sendStructureByMail() and getCheckOutFileName().
*  2008-08-14    SANSLK    Bug Id 73605, Modified sendStructureByMail() to add all details of the documents to the mail body.
*  2008-08-22    AMNALK    Bug Id 74989, Modified editStructure() and added new function getStringAttribute().
*  2008-09-09    AMNALK    Bug Id 73718, Modified the code to handle the view copy and print copy.
*  2009-02-26    SHTHLK    Bug id 80882, Modified printStructure to send the correct action to edmMacto.
*  2010-04-02    VIRALK    Bug Id 88317, Modified addSubLevelDocuments(). Restrict creation of new files for * users.
* -----------------------APP7.5 SP7---------------------------------------------------
*  2010-07-21    ARWILK    Bug Id 73606, Modified field BOOKING_LIST lov to filter from NUM_COUNT_ID1 and NUM_COUNT_ID2
*  2010-09-16    DULOLK    Bug Id 92125, Added js method filterFilesWithoutExtensions() to warn and remove files without extensions. 
* ------------------------------------------------------------------------------------
*/



package ifs.docmaw;


import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.docmaw.edm.*;
import java.util.StringTokenizer;


public class DocStructure extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocStructure");


   private ASPContext               ctx;
   private ASPTransactionBuffer     trans;
   private ASPHTMLFormatter         fmt;

   private ASPBlock                 structureblk;
   private ASPCommandBar            structurebar;
   private ASPRowSet                structureset;
   private ASPTable                 structuretbl;
   private ASPBlockLayout           structurelay;
   private ASPBlock                 dummyblk;


   private DocumentTransferHandler  doc_hdlr;
   private String                   page_title;
   private String                   doc_class;
   private String                   doc_no;
   private String                   doc_sheet;
   private String                   doc_rev;
   private String                   file_types;
   private String                   file_action;
   private String                   tc_connect_add_file;
   private boolean                  show_file_dialog;
   

   private int MAX_FILE_NAME_LENGTH = 250; //Bug Id 71463
   private boolean 			alert_once= false; //Bug Id 74523
   



   public DocStructure(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr   = getASPManager();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      boolean data_transfer = false;

      // Capture request..
      if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else
      {
         doc_hdlr = new DocumentTransferHandler(mgr);
         data_transfer = true;                  
         
         // Initialise page..
         initDocStructure();
      }

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());

      adjust();


      // Save the current request to the context if
      // it was a normal data transfer..
      if (data_transfer)
      {
         doc_hdlr.saveRequest();
      }

   }


   private void initDocStructure()
   {
      ASPManager mgr = getASPManager();

      // Initialise instance variables from transfered data..
      doc_class = doc_hdlr.getDocClass();
      doc_no = doc_hdlr.getDocNo();
      doc_sheet = doc_hdlr.getDocSheet();
      doc_rev = doc_hdlr.getDocRev();
      file_action = getFileAction();

      // initialise action-specific conditions..
      initAction();

      // Initialise page title..
      initialiseUI();


      // populate form..
      if (ctx.readFlag("POPULATE_FORM", true))
      {
         okFind();
         modifyStructureSet();
         ctx.writeFlag("POPULATE_FORM", false);
      }
      else
      {
         ctx.writeFlag("POPULATE_FORM", ctx.readFlag("POPULATE_FORM", true));
      }

      // Save any changes made to the table on the client,
      // on to the rowset..
      if ("TRUE".equals(mgr.readValue("SAVE_TABLE_DATA")))
         copyTableData();
   }


   private void initAction()
   {
      if ("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action))
         initEdit();
      else if ("CHECKIN".equals(file_action))
         initCheckIn();
      else if ("UNDOCHECKOUT".equals(file_action))
         initUndoCheckOut();
      else if ("VIEW".equals(file_action) || "PRINT".equals(file_action) ||
               "GETCOPYTODIR".equals(file_action) || "SENDMAIL".equals(file_action) ||
               "VIEWWITHEXTVIEWER".equals(file_action) || "VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) //Bug Id 73718
         initCheckOut();
   }


   private void addControlRow()
   {
      // add an empty row to the rowset. This first row will
      // used as the control row..
      if (ctx.readFlag("ADD_CONTROL_ROW", true))
      {
         addEmptyRow();
      }
      ctx.writeFlag("ADD_CONTROL_ROW", false);
   }



   /**
    * Initialise settings for all Check Out operations like
    * CHECKOUT, VIEW, PRINT, etc.
    *
    */
   private void initCheckOut()
   {
      ASPManager mgr = getASPManager();

      // save options set by the user to the context..

      String original_folder_structure;
      if (mgr.isEmpty(ctx.readValue("ORIGINAL_FOLDER_STRUCTURE")))
      {
         // When the page is generated for the first time..
         original_folder_structure = "TRUE";
      }
      else
      {
         original_folder_structure = mgr.isEmpty(mgr.readValue("ORIGINAL_FOLDER_STRUCTURE")) ? "FALSE" : mgr.readValue("ORIGINAL_FOLDER_STRUCTURE");
      }

      ctx.writeValue("ORIGINAL_FOLDER_STRUCTURE", original_folder_structure);
      ctx.writeValue("POST_CHECK_OUT_ACTION", mgr.readValue("POST_CHECK_OUT_ACTION", ctx.readValue("POST_CHECK_OUT_ACTION", "LAUNCH_OR_RUN_MACRO")));
      ctx.writeValue("FILE_NAME_TYPE", mgr.readValue("FILE_NAME_TYPE", ctx.readValue("FILE_NAME_TYPE", "USER_FILE_NAME")));
      ctx.writeValue("CHECKOUT_PATH", mgr.readValue("CHECKOUT_PATH", ctx.readValue("CHECKOUT_PATH", "")));

      // add control row..
      addControlRow();
   }



   /**
    * Initialise settings for CHECKOUT and CREATENEW
    *
    */
   private void initEdit()
   {
      initCheckOut();
   }


   private void initCheckIn()
   {
      ASPManager mgr = getASPManager();
      tc_connect_add_file = mgr.translate("DOCMAWDOCSTRUCTURECONNECT: [+]");
      ctx.writeValue("DEFAULT_FILE_DIALOG_EXT", ctx.readValue("DEFAULT_FILE_DIALOG_EXT", ""));
   }


   private void initUndoCheckOut()
   {
      // add control row..
      addControlRow();
   }

   
   /**
    * Modify the contents of the populated rowset 
    */
   private void modifyStructureSet()
   {
      ASPManager mgr = getASPManager();
      
      if ("CHECKIN".equals(file_action))
      {
         String file_name = doc_hdlr.getDocumentProperty("FILE_NAME");

         if (!mgr.isEmpty(file_name))
         {
            structureset.first(); 
            connectFile(file_name);
         }
      }       
   }

   
   private String getFileAction()
   {
      ASPManager mgr = getASPManager();

      String file_action = doc_hdlr.getDocumentProperty("FILE_ACTION");

      if (mgr.isEmpty(file_action))
      {
         file_action = doc_hdlr.getActionProperty("FILE_ACTION");
      }

      return file_action;
   }


   private String[] getDefaultValues(String doc_class)
   {
      ASPManager mgr = getASPManager();

      String[] values = new String[4];

      trans.clear();

      // default doc_sheet
      ASPCommand cmd = trans.addCustomFunction("DEFAULTDOCSHEET", "Doc_Class_Default_API.Get_Default_Value_", "DOC_SHEET");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("STR_IN", "DocTitle");
      cmd.addParameter("STR_IN", "DOC_SHEET");

      // default doc_rev
      cmd = trans.addCustomFunction("DEFAULTDOCREV", "Doc_Class_Default_API.Get_Default_Value_", "DOC_REV");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("STR_IN", "DocTitle");
      cmd.addParameter("STR_IN", "DOC_REV");

      // number generator setting..
      cmd = trans.addCustomFunction("NUMBERGENERATOR", "Doc_Class_Default_API.Get_Default_Value_", "STR_OUT");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("STR_IN", "DocTitle");
      cmd.addParameter("STR_IN", "NUMBER_GENERATOR");

      trans = mgr.perform(trans);

      values[0] = trans.getValue("DEFAULTDOCSHEET/DATA/DOC_SHEET");
      values[1] = trans.getValue("DEFAULTDOCREV/DATA/DOC_REV");
      String number_generator = trans.getValue("NUMBERGENERATOR/DATA/STR_OUT");

      values[2] = "";
      values[3] = "";

      // number generator advanced ?
      if ("ADVANCED".equals(number_generator))
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETID1", "Doc_Class_Default_API.Get_Default_Value_", "NUM_COUNT_ID1");
         cmd.addParameter("DOC_CLASS", doc_class);
         cmd.addParameter("STR_IN", "DocTitle");
         cmd.addParameter("STR_IN", "NUMBER_COUNTER");

         cmd = trans.addCustomFunction("GETID2", "Doc_Number_Counter_API.Get_Default_Id2", "NUM_COUNT_ID2");
         cmd.addReference("NUM_COUNT_ID1", "GETID1/DATA");

         trans = mgr.perform(trans);

         values[2] = trans.getValue("GETID1/DATA/NUM_COUNT_ID1");

         if (!"0".equals(trans.getValue("GETID2/DATA/NUM_COUNT_ID2")))
            values[3] = trans.getValue("GETID2/DATA/NUM_COUNT_ID2");
      }

      return values;
   }


   public void validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("DOC_CLASS".equals(val))
      {
         String[] values = getDefaultValues(mgr.getQueryStringValue("DOC_CLASS"));

         // build response string for validation and send it to the client..
         StringBuffer response = new StringBuffer();
         for (int x = 0; x < values.length; x++)
         {
            response.append(values[x]);
            response.append("^");
         }

         mgr.responseWrite(response.toString());
      }

      mgr.endResponse();
   }


   private void okFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q;

      // build query to fetch all document in structure..
      StringBuffer query = new StringBuffer();

      // SQLInjections_Safe AMNILK 20070810
      query.append("SELECT 1, ? , ? , ? , ? , '', Doc_Title_Api.Get_Title(?,?)");
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) 
	  query.append(     ", Edm_File_API.Get_Original_File_Name(?, ?, ?, ?, 'VIEW') ");
      else
	  query.append(     ", Edm_File_API.Get_Original_File_Name(?, ?, ?, ?, 'ORIGINAL') ");
      //Bug Id 73718, end

      if("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action))
         query.append(     ", Edm_File_Util_API.Generate_Docman_File_Name_(");
      else
         query.append(     ", Edm_File_API.Get_Copyof_File_Name(");

      query.append(     " ?, ?, ?, ?");

      if("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action))
         query.append(     ", 'ORIGINAL')");
      //Bug Id 73718, start
      else if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) 
	 query.append(     ", 'VIEW','')"); 
      //Bug Id 73718, end
      else
         query.append(     ", 'ORIGINAL','')"); 

      query.append(     ", Edm_File_API.Get_Document_State(");
      query.append(     " ?, ?, ?, ?");
      query.append(     ", 'ORIGINAL'), Edm_File_API.Get_Edm_Information(");
      query.append(     " ?, ?, ?, ?");
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) 
         query.append(     ", 'VIEW'), Doc_Class_Proc_Action_Line_API.Get_All_Doc_Types(");
      else
	 query.append(     ", 'ORIGINAL'), Doc_Class_Proc_Action_Line_API.Get_All_Doc_Types(");
      //Bug Id 73718, end
      query.append(     " ? ");
      query.append(     ", 'VIEW') ");      
      query.append("FROM DUAL ");
      query.append("UNION ALL ");
      query.append(     "(SELECT s.doc_level, s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, s.relative_path, t.title, e.user_file_name, ");
      
      if("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action))
         query.append(     "        Edm_File_Util_API.Generate_Docman_File_Name_(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'ORIGINAL'), ");
      else
	  //Bug Id 73718, start
	  if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) 
	     query.append(     "        Edm_File_API.Get_Copyof_File_Name(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'VIEW', ''), "); 
	  else
	     query.append(     "        Edm_File_API.Get_Copyof_File_Name(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'ORIGINAL', ''), ");
	  //Bug Id 73718, end
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) 
	  query.append(     "        Edm_File_API.Get_Document_State(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'VIEW'), ");
      else
	  query.append(     "        Edm_File_API.Get_Document_State(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'ORIGINAL'), ");
      query.append(     "        Edm_File_API.Get_Edm_Information(s.sub_doc_class, s.sub_doc_no, s.sub_doc_sheet, s.sub_doc_rev, 'ORIGINAL'), ");
      //Bug Id 73718, end
      
      query.append(     "        Doc_Class_Proc_Action_Line_API.Get_All_Doc_Types(s.sub_doc_class, 'VIEW') ");
      query.append(      "FROM ");
      query.append(     "(SELECT (LEVEL + 1) doc_level, sub_doc_class, sub_doc_no, sub_doc_sheet, sub_doc_rev, relative_path ");
      query.append(      "FROM doc_structure ");
      query.append(      "CONNECT BY doc_class = PRIOR sub_doc_class ");
      query.append(           "AND doc_no = PRIOR sub_doc_no ");
      query.append(           "AND doc_sheet = PRIOR sub_doc_sheet ");
      query.append(           "AND doc_rev = PRIOR sub_doc_rev ");
      query.append(      "START WITH doc_class = ?");
      query.append(         " AND doc_no = ?");
      query.append(         " AND doc_sheet = ?");
      query.append(         " AND doc_rev = ?");
      query.append(         ") s, doc_title t, edm_file e ");
      query.append(      "WHERE s.sub_doc_class = t.doc_class ");
      query.append(           "AND s.sub_doc_no = t.doc_no ");
      query.append(           "AND s.sub_doc_class = e.doc_class(+) ");
      query.append(           "AND s.sub_doc_no = e.doc_no(+) ");
      query.append(           "AND s.sub_doc_sheet = e.doc_sheet(+) ");
      query.append(           "AND s.sub_doc_rev = e.doc_rev(+) ");
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action))
	  query.append(           "AND e.doc_type(+) = 'VIEW') ");
      else
	  query.append(           "AND e.doc_type(+) = 'ORIGINAL') ");
      //Bug Id 73718, end	  
      q = trans.addQuery("STRUCTURE", query.toString());
      //trans.addQuery("STRUCTURE", query.toString()).setBufferSize(10000);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);

      q.addParameter("DOC_CLASS",doc_class);

      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_SHEET",doc_sheet);
      q.addParameter("DOC_REV",doc_rev);
      


      trans = mgr.perform(trans);

      // add items in the response buffer to the rowset..
      ASPBuffer buf = trans.getBuffer("STRUCTURE");
      for (int x = 0; x < buf.countItems() - 1; x++)
      {
         addDocument(buf.getBufferAt(x));
      }
   }


   /**
    * Adds a new document to structureset using values from the
    * given ASPBuffer. The new document is added to the end of the
    * rowset
    */
   private void addDocument(ASPBuffer document)
   {
      ASPManager mgr = getASPManager();


      // add an empty row to the rowset..
      addEmptyRow();

      // set corressponding values for the newly added row..
      structureset.setValue("LEVEL", document.getValueAt(0));
      structureset.setValue("DOC_CLASS", document.getValueAt(1));
      structureset.setValue("DOC_NO", document.getValueAt(2));
      structureset.setValue("DOC_SHEET", document.getValueAt(3));
      structureset.setValue("DOC_REV", document.getValueAt(4));

      // trim both sides of the relative path with extra '\'s
      String relative_path =  DocmawUtil.rightTrim(DocmawUtil.leftTrim(document.getValueAt(5), '\\'), '\\');

      // set relative path for the current document..
      structureset.setValue("RELATIVE_PATH", relative_path);
      if (Integer.parseInt((structureset.getValue("LEVEL"))) > 1)
      {
         String parent_path = getParentAttribute("RELATIVE_PATH", structureset.getCurrentRowNo());
         if (mgr.isEmpty(parent_path))
            structureset.setValue("RELATIVE_PATH", relative_path);
         else
            structureset.setValue("RELATIVE_PATH", parent_path + "\\" + relative_path);
      }


      structureset.setValue("TITLE", document.getValueAt(6));
      structureset.setValue("ORIGINAL_FILE_NAME", document.getValueAt(7));
      structureset.setValue("DOCMAN_FILE_NAME", document.getValueAt(8));
      structureset.setValue("FILE_STATE", document.getValueAt(9));

      // Edm Information
      String edm_info = document.getValueAt(10);
      structureset.setValue("EDM_INFO", edm_info);
      structureset.setValue("CHECK_IN_ACCESS", DocmawUtil.getAttributeValue(edm_info, "CHECK_IN_ACCESS"));
      structureset.setValue("EDIT_ACCESS", DocmawUtil.getAttributeValue(edm_info, "EDIT_ACCESS"));
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action))
      {
	  if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "VIEW_REF_EXIST")) && "FALSE".equals(DocmawUtil.getAttributeValue(edm_info, "VIEW_ACCESS"))) 
	      structureset.setValue("VIEW_ACCESS", DocmawUtil.getAttributeValue(edm_info, "VIEW_ACCESS"));
	  else
	      structureset.setValue("VIEW_ACCESS", DocmawUtil.getAttributeValue(edm_info, "VIEW_REF_EXIST"));
      }
      else
	  structureset.setValue("VIEW_ACCESS", DocmawUtil.getAttributeValue(edm_info, "VIEW_ACCESS"));
      //Bug Id 73718, end
      structureset.setValue("CHECKED_OUT_TO_ME", DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT_TO_ME"));
      structureset.setValue("ORIGINAL_REF_EXIST", DocmawUtil.getAttributeValue(edm_info, "ORIGINAL_REF_EXIST"));
      structureset.setValue("FILE_TYPE", DocmawUtil.getAttributeValue(edm_info, "ORIGINAL_FILE_TYPE"));
      
      structureset.setValue("CONNECTED_DOC_TYPES", document.getValueAt(11));
      
         
      //
      // Logic for enabling actions on the current row
      // depending on access rights to the document
      //

      if ("CHECKIN".equals(file_action))
      {
         structureset.setValue("CONNECT_ADD", tc_connect_add_file);


         // If a file is connected to the current document and the user
         // has CHECKIN access, then check the CHECKIN box..

         if ("TRUE".equals(structureset.getValue("CHECK_IN_ACCESS")) && !mgr.isEmpty(structureset.getValue("FILE_STATE")))
            structureset.setValue("CHECKIN", "1");
         else
            structureset.setValue("CHECKIN", "0");

      }

      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action))
      {

         // If the current document is the Top Level Document (TLD),
         // check the EDIT and VIEW boxes even if no files have been
         // checked in. If on the other hand, the current document is
         // not a TLD, uncheck EDIT, if no files have been checked in

         if ((structureset.getCurrentRowNo() == 1 && "TRUE".equals(structureset.getValue("EDIT_ACCESS"))) ||
             ("TRUE".equals(structureset.getValue("EDIT_ACCESS")) && !mgr.isEmpty(structureset.getValue("FILE_STATE"))))
            structureset.setValue("EDIT", "1");
         else
            structureset.setValue("EDIT", "0");
      }


      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action) || "VIEW".equals(file_action) || "PRINT".equals(file_action) ||
          "GETCOPYTODIR".equals(file_action) || "SENDMAIL".equals(file_action) || "VIEWWITHEXTVIEWER".equals(file_action) || 
	  "VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) //Bug Id 73718
      {
         // If the current document has VIEW access, or if the current
         // document is a TLD and has EDIT access (but even if no files
         // are checked in), check the VIEW box

         if ("TRUE".equals(structureset.getValue("VIEW_ACCESS")) || (structureset.getCurrentRowNo() == 1 && "TRUE".equals(structureset.getValue("EDIT_ACCESS"))))
            structureset.setValue("VIEW", "1");
         else
            structureset.setValue("VIEW", "0");
      }


      if ("UNDOCHECKOUT".equals(file_action))
      {
         if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "CHECKED_OUT")) && "TRUE".equals(structureset.getValue("CHECKED_OUT_TO_ME")))
            structureset.setValue("UNRESERVE", "1");
         else
            structureset.setValue("UNRESERVE", "0");

         structureset.setValue("DELETE", "1");
      }
   }

   //Bug Id 71463, start
   public String adjustFileName(String localpath, String filename)
   {

       String doc_class = structureset.getRow().getValue("DOC_CLASS");
       String doc_no = structureset.getRow().getValue("DOC_NO");
       String doc_sheet = structureset.getRow().getValue("DOC_SHEET");
       String doc_rev = structureset.getRow().getValue("DOC_REV");
        //Bug Id 74523, start
       ASPManager mgr = getASPManager();
       String file_no = structureset.getRow().getValue("FILE_NO");
      
       if (file_no == null) {
	     file_no = "1";
       }
       //Bug Id 74523, end
       String doc_key = "(" + doc_class + " - " + doc_no + " - " + doc_sheet + " - " + doc_rev + " - " + file_no +")";
       String file_ext = filename.substring(filename.lastIndexOf(".") + 1,filename.length());
       //Bug Id 74523, start
       if(mgr.isEmpty(file_ext)){
	   file_ext = " ";
       }
       //Bug Id 74523, end

       if (localpath.lastIndexOf("\\") < localpath.length()-1)
       {
           localpath = localpath + "\\";
           
       }
       if (MAX_FILE_NAME_LENGTH >= localpath.length() + 1 + filename.length()) 
       {
	   filename = filename;
       }
       else
       {
	   //Bug Id 74523, start
             trans.clear();                                                                                         
                                                                                                                    
             ASPCommand cmd = trans.addCustomFunction("SHORT_NAME", "Edm_File_API.Get_Short_File_Name","STR_OUT");  
             cmd.addParameter("STR_IN", doc_class);                                                                     
             cmd.addParameter("STR_IN", doc_no);                                                                        
             cmd.addParameter("STR_IN", doc_sheet);                                                                     
             cmd.addParameter("STR_IN", doc_rev);                                                                       
             cmd.addParameter("STR_IN", file_no);                                                                       
             cmd.addParameter("STR_IN", localpath);                                                                     
             cmd.addParameter("STR_IN", file_ext);                                                                                                                                      
             cmd.addParameter("STR_IN", filename);                                                                      
                                                                                                                    
             trans = mgr.perform(trans);                                                                            
                                                                                                                   
             filename = trans.getValue("SHORT_NAME/DATA/STR_OUT"); 
	     debug("DEBUG: adjustFileName()  alert_once " + alert_once);
	     if(!alert_once)
	     {

		 try{
		 
		 appendDirtyJavaScript("alert(\"" + mgr.translate("DOCMAWDOCSTRLENLMT: The length of the local file name exceeds the limit of 255 characters. File name will be shortened to:") + filename+ "\");\n");
		 }
		 catch(Exception any){
		 }

		 alert_once = true;  
		
	     }
	     
	   //Bug Id 74523, end
	   
       }

       debug("DEBUG: adjustFileName()  file_name " + filename);

       return filename;
   }
   //Bug Id 71463, end    


   private void addEmptyRow()
   {
      // add an empty row..
      structureset.addRow(null);
   }


   /**
    * Returns an ASPBuffer containing all rows in the rowset
    */
   private ASPBuffer getRowsAsBuffer(ASPRowSet rowset)
   {
      return rowset.getRows();
   }


   /**
    * Add sub-buffers in the ASPBuffer as rows to the rowset. Each sub-buffer
    * must match the row definition
    */
   private void setBufferAsRows(ASPRowSet rowset, ASPBuffer rows)
   {
      rowset.clear();
      for (int x = 0; x < rows.countItems(); x++)
         rowset.addRow(rows.getBufferAt(x));
   }


   /**
    * Inserts a new document to structureset at the specified row no
    *
    */
   private void insertDocumentBuffer(ASPBuffer rows, int row_no, String level, String doc_class, String doc_sheet,
                                     String doc_rev, String id1, String id2, String title, String file_name, String file_type)
   {
      ASPManager mgr = getASPManager();

      // insert new buffer at specified position..
      ASPBuffer buf = rows.addBufferAt("DATA", row_no);
      buf.addItem("CONNECT_ADD", tc_connect_add_file);
      buf.addItem("CHECKIN", "1");
      buf.addItem("LEVEL", level);
      buf.addItem("DOC_CLASS", doc_class);
      buf.addItem("DOC_NO", "");
      buf.addItem("DOC_SHEET", doc_sheet);
      buf.addItem("DOC_REV", doc_rev);
      buf.addItem("TITLE", title);
      buf.addItem("FILE_NAME", file_name);
      buf.addItem("NEW_DOCUMENT", "TRUE");
      buf.addItem("NUM_COUNT_ID1", id1);
      buf.addItem("NUM_COUNT_ID2", id2);
      buf.addItem("BOOKING_LIST", "");
      buf.addItem("FILE_TYPE", file_type);
   }


   private void removeBufferAt(ASPBuffer buf, int position)
   {
      buf.removeItemAt(position);
   }


   public String getFileTypes()
   {
      ASPManager mgr = getASPManager();

      // Get count of possible file types..
      trans.clear();
      ASPQuery query = trans.addQuery("GETFILETYPESCOUNT", "SELECT count(file_type) FILE_TYPE_COUNT FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW')");
      trans = mgr.perform(trans);
      int count = Integer.parseInt(trans.getValue("GETFILETYPESCOUNT/DATA/FILE_TYPE_COUNT"));

      // Fetch all available file types..
      trans.clear();
      query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type"); 
      query.setBufferSize(count);
      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("GETORIGINALFILETYPES");

      String file_types = "";
      int rows = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
      for (int i = 0; i < rows; i++)
      {
         file_types += buf.getBufferAt(i).getValueAt(0) + "^" + buf.getBufferAt(i).getValueAt(1) + "^";
      }

      return file_types;
   }


   /*
    *getFileTypeFromFileName
    *This method returns the file type by looking at the extension in the file name
    */

   public String getFileTypeFromFileName(String file_name)
   {
      ASPManager mgr = getASPManager();
      String ext = DocmawUtil.getFileExtention(file_name);
      String file_type, file_type_extension;

      // Try to avoid the extra db call by checking if we already have a list of file types.
      if (mgr.isEmpty(file_types))
         file_types = getFileTypes();

      StringTokenizer st = new StringTokenizer(file_types, "^");

      // Check through the file types to see if the extension is recognised.
      while (st.hasMoreTokens())
      {
         file_type = st.nextToken();
         file_type_extension = st.nextToken();

         if (ext.equalsIgnoreCase(file_type_extension))
            return file_type;
      }
      return null;
   }

   /**
    * Returns the row no of the parent document of the current
    * row
    */
   private int getParentRowNo(int current_row)
   {
      structureset.goTo(current_row);
      int level = Integer.parseInt(structureset.getValue("LEVEL"));

      while (structureset.previous())
      {
         if (Integer.parseInt(structureset.getValue("LEVEL")) < level)
         {
            // found the parent..
            return structureset.getCurrentRowNo();
         }
      }

      // return -1 if the current row does not have a parent..
      return -1;
   }


   /**
    * Returns the row no of the child document of the current row.
    *
    * NOTE: The current row may have many children. This method
    * only returns the first child out of the many children
    */
   private int getChildRowNo(int current_row)
   {
      structureset.goTo(current_row);
      int level = Integer.parseInt(structureset.getValue("LEVEL"));

      if (structureset.next())
      {
         int next_level = Integer.parseInt(structureset.getValue("LEVEL"));

         if (next_level > level)
         {
            // found the first child..
            return structureset.getCurrentRowNo();
         }
         else if (next_level == level)
         {
            // the current row has no children..
            return -1;
         }
      }

      // return -1 if the current row does not have
      // children..
      return -1;
   }


   private String getParentAttribute(String attribute_name, int current_row)
   {
      if (current_row == 0)
      {
         // this is the top-most document and has no parent
         return null;
      }

      int parent_row = getParentRowNo(current_row);
      structureset.goTo(parent_row);
      String value = structureset.getValue(attribute_name);
      structureset.goTo(current_row);
      return value;
   }


   private String[] getParentAttribute(String[] attribute_name, int current_row)
   {
      if (current_row == 0)
      {
         // this is the top-most document and has no parent
         return null;
      }

      int parent_row = getParentRowNo(current_row);
      String[] values = new String[attribute_name.length];
      structureset.goTo(parent_row);

      for (int x = 0; x < attribute_name.length; x++)
      {
         values[x] = structureset.getValue(attribute_name[x]);
      }

      structureset.goTo(current_row);
      return values;
   }


   private String getChildAttribute(String attribute_name, int current_row)
   {
      if (current_row == (structureset.countRows() - 1))
      {
         // this is the bottom-most document and has no children
         return null;
      }

      int child_row = getChildRowNo(current_row);
      if (child_row == -1)
      {
         // this row has no children..
         return null;
      }

      structureset.goTo(child_row);
      String value = structureset.getValue(attribute_name);
      structureset.goTo(current_row);
      return value;
   }
   
   
   public void addSubLevelDocuments() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;

     // Bug id 88317 start

      trans.clear();

      cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
      cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
      cmd.addReference("DUMMY2", "FNDUSER/DATA");
            
      trans = mgr.perform(trans);
      String person_id = trans.getValue("STARUSER/DATA/DUMMY1");


      if ("*".equals(person_id)){
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         return;
      }
      // Bug id 88317 end

      int current_row = ctx.readNumber("CURRENT_ROW", structureset.getCurrentRowNo());
      structureset.goTo(current_row);

      // check if the current row has a file already connected..
      String file_name = structureset.getValue("FILE_NAME");

      if (mgr.isEmpty(file_name))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCSTRUCTURENOFILECONNECTED: You must connect a file to the current document before adding sub documents"));
         return;
      }

      // if no files have been seleted, show file selector..
      String files_selected = mgr.readValue("FILES_SELECTED");
      if (mgr.isEmpty(files_selected))
      {
         // determine the default startup directory to
         // pick sub documents from..
         String default_path = "";
         String default_ext = "";

         if (file_name != null)
         {
            default_path = DocmawUtil.getPath(file_name);
            default_path = default_path.replaceAll("\\\\", "\\\\\\\\");
            default_ext  = ctx.readValue("DEFAULT_FILE_DIALOG_EXT");
         }

         appendDirtyJavaScript("file_types = '" + getFileTypes() + "';\n");
         appendDirtyJavaScript("selectFile('" + mgr.encodeStringForJavascript(default_path) + "', file_types, true, '" + mgr.translate("DOCMAWDOCSTRUCTUREOPENDLGTITLE: Choose one or more files to create new documents...") + "','" + mgr.encodeStringForJavascript(default_ext) + "', 'addSubLevelDocuments');\n");

         // save the current row no..
         ctx.writeNumber("CURRENT_ROW", current_row);

         return;
      }
      else if ("<NONE>".equals(files_selected))
      {
         // The user chose to cancel the dialog.
         // This condition is here to get rid of the current_row
         // context value, which would otherwise have an invalid 
         // value in the immediate call to this funtion again
         return;
      }


      //
      // Add selected files as sub-documents to the
      // selected row..
      //


      String path = DocmawUtil.getPath(files_selected.substring(0, (files_selected.indexOf("|") == -1 ? files_selected.length() : files_selected.indexOf("|")))).toLowerCase();

      if ("\\".equals(path.substring(path.length() - 1)))
      {
         path = path.substring(0, path.length() - 1);
      }


      // the file selected must either be in the same directory or a
      // sub-directory to the parent file..
      if (!path.startsWith(DocmawUtil.getPath(structureset.getValue("FILE_NAME")).toLowerCase()))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCSTRUCTUREINVALIDFILESELECTED: The file(s) selected must either be in the same directory as the parent or in any one of its sub directories."));
         return;
      }

      StringTokenizer st = new StringTokenizer(files_selected, "|");
      int row_no = current_row + 1;
      int level = Integer.parseInt(structureset.getValue("LEVEL"));
      ASPBuffer buf = getRowsAsBuffer(structureset);


      // fetch default values depending on the class..
      String[] default_values = getDefaultValues(structureset.getValue("DOC_CLASS"));

      String selected_file_name = "";
      while (st.hasMoreTokens())
      {
         selected_file_name = st.nextToken();
         String selected_file_type = getFileTypeFromFileName(selected_file_name);
         insertDocumentBuffer(buf, row_no++, Integer.toString(level + 1), structureset.getValue("DOC_CLASS"), default_values[0], default_values[1],
                              default_values[2], default_values[3], DocmawUtil.getBaseFileName(selected_file_name), selected_file_name, selected_file_type);
      }

      
      // set the modified buffer to the rowset..
      setBufferAsRows(structureset, buf);
      ctx.writeValue("DEFAULT_FILE_DIALOG_EXT", DocmawUtil.getFileExtention(selected_file_name));
   }


   
   public void connectFile() throws FndException
   {
      ASPManager mgr = getASPManager();

      int current_row = ctx.readNumber("CURRENT_ROW", structureset.getCurrentRowNo());

      // get the parent file name..
      String parent_file_name = getParentAttribute("FILE_NAME", current_row);

      // get the name of the selected file
      String selected_file = mgr.readValue("FILES_SELECTED");

      // if no files have been seleted, show file selector..
      if (mgr.isEmpty(selected_file))
      {
         // determine the default startup directory..

         String default_path = "";
         String default_ext = "";

         if (!mgr.isEmpty(parent_file_name))
         {
            default_path = DocmawUtil.getPath(parent_file_name);
            default_path = default_path.replaceAll("\\\\", "\\\\\\\\");
            default_ext = ctx.readValue("DEFAULT_FILE_DIALOG_EXT");
         }

         appendDirtyJavaScript("file_types = '" + getFileTypes() + "';\n");
         appendDirtyJavaScript("selectFile('" + mgr.encodeStringForJavascript(default_path) + "', file_types, false, '" + mgr.translate("DOCMAWDOCSTRUCTUREOPENDLGTITLE2: Choose file to Check In...") + "','" + mgr.encodeStringForJavascript(default_ext) + "', 'connectFile');\n");


         // save the current row no..
         ctx.writeNumber("CURRENT_ROW", current_row);

         return;
      }
      else if ("<NONE>".equals(selected_file))
      {
         // The user chose to cancel the dialog.
         // This condition is here to get rid of the current_row
         // context value, which would otherwise have an invalid 
         // value in the immediate call to this funtion again
         return;
      }


      // the file selected must either be in the same directory or a
      // sub-directory to the parent file..
      String path = DocmawUtil.getPath(selected_file).toLowerCase();

      if ("\\".equals(path.substring(path.length() - 1)))
      {
         path = path.substring(0, path.length() - 1);
      }

      if (!mgr.isEmpty(parent_file_name))
      {
         if (!path.startsWith(DocmawUtil.getPath(parent_file_name).toLowerCase()))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCSTRUCTUREFILECONFLICTPARENT: The file selected must either be in the same directory as the parent or in any one of its sub directories."));
            return;
         }
      }

      // if the current document has sub documents then the file selected
      // must be in a directory which also contains the sub documents..
      String child_file_name = getChildAttribute("FILE_NAME", current_row);

      if (!mgr.isEmpty(child_file_name))
      {
         if (!child_file_name.toLowerCase().startsWith(path))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCSTRUCTUREINVALIDFILECONFLICTCHILD: The file selected must be in a directory that also contains the sub documents."));
            return;
         }
      }
      
      // connect the selected file to the current row..
      structureset.goTo(current_row);
      connectFile(selected_file);
      ctx.writeValue("DEFAULT_FILE_DIALOG_EXT", DocmawUtil.getFileExtention(selected_file));
   }

   
   public void connectFile(String file_name)
   {
      // connect the selected file to the current row and
      // update the relevant fields..
      structureset.setValue("CHECKIN", "1");
      structureset.setValue("FILE_NAME", file_name);
      structureset.setValue("FILE_TYPE", getFileTypeFromFileName(file_name));

      if ("TRUE".equals(structureset.getValue("NEW_DOCUMENT")))
      {
         structureset.setValue("TITLE", DocmawUtil.getBaseFileName(file_name));
      }      
   }
   

   public void onClickAddDocumentsEventHandler() throws FndException
   {
      ASPManager mgr = getASPManager();

      int current_row = structureset.getCurrentRowNo();
      structureset.goTo(current_row);

      if (mgr.isEmpty(structureset.getValue("FILE_NAME")))
      {
         connectFile();
      }
      else
      {
         addSubLevelDocuments();
      }
   }


   public void deleteRow() throws FndException
   {
      ASPManager mgr = getASPManager();


      int current_row = ctx.readNumber("CURRENT_ROW", structureset.getCurrentRowNo());
      structureset.goTo(current_row);


      // only new documents can be deleted..
      if (!"TRUE".equals(structureset.getValue("NEW_DOCUMENT")))
      {
         mgr.showAlert("DOCMAWDOCSTRUCTUREDELETENEWONLY: Only newly added documents can be deleted");
         return;
      }

      // get level of current document..
      int level = Integer.parseInt(structureset.getValue("LEVEL"));

      // check if the current row has sub documents..
      ASPBuffer buf = getRowsAsBuffer(structureset);
      if ((current_row < structureset.countRows() - 1) && (Integer.parseInt(buf.getBufferAt(current_row + 1).getValue("LEVEL")) > level) && "FALSE".equals(mgr.readValue("DELETE_CONFIRMED")))
      {
         appendDirtyJavaScript("if (confirm('" + mgr.encodeStringForJavascript(mgr.translate("DOCMAWDOCSTRUCTURECONFIRMDELETE: This will delete the current document and all its sub documents. Continue?")) + "'))\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.DELETE_CONFIRMED.value = \"TRUE\";\n");
         appendDirtyJavaScript("   executeServerCommand('deleteRow');\n");
         appendDirtyJavaScript("}\n");

         // save the current row no..
         ctx.writeNumber("CURRENT_ROW", current_row);

         return;
      }

      // delete the current row..
      removeBufferAt(buf, current_row);

      // delete all sub-documents as well..
      while ((current_row < buf.countItems()) && (Integer.parseInt(buf.getBufferAt(current_row).getValue("LEVEL")) > level))
      {
         removeBufferAt(buf, current_row);
      }

      // set the modified buffer to the rowset..
      setBufferAsRows(structureset, buf);
   }



   public void copyTableData()
   {
      structureset.store();
   }


   /**
    * Returns the path relative to path1 (parent) from path2
    *
    */
   private String getRelativePath(String file_path1, String file_path2)
   {
      String path1 = DocmawUtil.getPath(file_path1).toLowerCase();
      String path2 = DocmawUtil.getPath(file_path2).toLowerCase();

      if ((path2.indexOf(path1) == 0) && (path2.length() > path1.length()))
         // extract path from the original string to preserve case..
         return DocmawUtil.getPath(file_path2).substring(path1.length() + 1);
      else if (path2.indexOf(path1) == 0)
         return "";
      else
         return null;
   }


   public void checkInStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      ASPCommand cmd;
      int count = 0;
      String[] attributes = new String[] {"DOC_CLASS", "DOC_NO", "DOC_SHEET", "DOC_REV", "FILE_NAME"};


      //
      // Create new revisions for all newly added documents..
      //

      structureset.first();
      trans.clear();

      do
      {
         if ("1".equals(structureset.getValue("CHECKIN")) && "TRUE".equals(structureset.getValue("NEW_DOCUMENT")))
         {
            // create a new title and revision..
            cmd = trans.addCustomCommand("CREATENEW" + count++, "Doc_Title_API.Create_New_Document_");
            cmd.addParameter("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            cmd.addParameter("DOC_NO", structureset.getValue("DOC_NO"));
            cmd.addParameter("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            cmd.addParameter("DOC_REV", structureset.getValue("DOC_REV"));
            cmd.addParameter("TITLE", structureset.getValue("TITLE"));
            cmd.addParameter("BOOKING_LIST", structureset.getValue("BOOKING_LIST"));
            cmd.addParameter("NUM_COUNT_ID1", structureset.getValue("NUM_COUNT_ID1"));
            cmd.addParameter("NUM_COUNT_ID2", structureset.getValue("NUM_COUNT_ID2"));
         }
      }
      while (structureset.next());

      ASPTransactionBuffer response = mgr.perform(trans);


      //
      // Assign returned values for DOC_NO, DOC_SHEET and DOC_REV from
      // the database call, to the corressponding rows.
      //

      structureset.first();
      count = 0;

      do
      {
         if ("1".equals(structureset.getValue("CHECKIN")) && "TRUE".equals(structureset.getValue("NEW_DOCUMENT")))
         {
            structureset.setValue("DOC_NO", response.getValue("CREATENEW" + count + "/DATA/DOC_NO"));
            structureset.setValue("DOC_SHEET", response.getValue("CREATENEW" + count + "/DATA/DOC_SHEET"));
            structureset.setValue("DOC_REV", response.getValue("CREATENEW" + count + "/DATA/DOC_REV"));
            count++;
         }
      }
      while (structureset.next());



      //
      // Connect each of the new documents to their corressponding
      // parents by creating new entries in DocStructure..
      //

      structureset.first();
      trans.clear();
      count = 0;

      do
      {
         if ("1".equals(structureset.getValue("CHECKIN")) && "TRUE".equals(structureset.getValue("NEW_DOCUMENT")))
         {
            // fetch keys of the parent document..
            String[] parent_attributes = getParentAttribute(attributes, structureset.getCurrentRowNo());

            // build attribute string to create new connection to parent..
            StringBuffer attr = new StringBuffer();
            DocmawUtil.addToAttribute(attr, "DOC_CLASS", parent_attributes[0]);
            DocmawUtil.addToAttribute(attr, "DOC_NO", parent_attributes[1]);
            DocmawUtil.addToAttribute(attr, "DOC_SHEET", parent_attributes[2]);
            DocmawUtil.addToAttribute(attr, "DOC_REV", parent_attributes[3]);
            DocmawUtil.addToAttribute(attr, "SUB_DOC_CLASS", structureset.getValue("DOC_CLASS"));
            DocmawUtil.addToAttribute(attr, "SUB_DOC_NO", structureset.getValue("DOC_NO"));
            DocmawUtil.addToAttribute(attr, "SUB_DOC_SHEET", structureset.getValue("DOC_SHEET"));
            DocmawUtil.addToAttribute(attr, "SUB_DOC_REV", structureset.getValue("DOC_REV"));
            DocmawUtil.addToAttribute(attr, "RELATIVE_PATH", getRelativePath(parent_attributes[4], structureset.getValue("FILE_NAME")));

            // create connection to the parent document revision..
            cmd = trans.addCustomCommand("CONNECTION" + count++, "Doc_Structure_API.New__");
            cmd.addParameter("STR_OUT");
            cmd.addParameter("STR_OUT");
            cmd.addParameter("STR_OUT");
            cmd.addParameter("STR_INOUT", attr.toString());
            cmd.addParameter("STR_IN", "DO");
         }
      }
      while (structureset.next());

      response = mgr.perform(trans);



      //
      // Transfer documents to be checked in by EdmMacro..
      //

      // Fetch client attributes for macro execution..
      String macro_attrs = getMacroAttributes();
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.first();
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("CHECKIN")))
         {
            ASPBuffer doc = mgr.newASPBuffer();
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_NAME", structureset.getValue("FILE_NAME"));
            doc.addItem("FILE_TYPE", structureset.getValue("FILE_TYPE"));
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("EXECUTE_MACRO", "NO");

            if (structureset.getCurrentRowNo() == 0)
            {
               // Add macro attributes to parent document..
               doc.addItem("MACRO_ATTRIBUTES", macro_attrs);
            }

            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.next());

      // Extra buffer to select and run macros..
      ASPBuffer buf = docs.addBufferAt("DATA", 0);
      structureset.first();
      buf.addItem("FILE_ACTION", "SELECT_AND_EXECUTE_MACRO");
      buf.addItem("PROCESS", "CHECKIN");
      buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
      buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
      buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
      buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
      buf.addItem("DOCUMENT_BUF_NO", "1");
      buf.addItem("EDM_TITLE", getTitle());


      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLECHECKIN: Check In Report"));      
      docs.addBuffer("DATA", buf);

      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "CHECKIN");
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("LOG_ERROR", "TRUE");


      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public String getCheckOutFileName()
   {
      ASPManager mgr = getASPManager();

      String file_name = null;

      if (!mgr.isEmpty(structureset.getValue("FILE_NAME")))
      {
         // If a file name is available..
         file_name =  structureset.getValue("FILE_NAME");
      }
      else if (!mgr.isEmpty(structureset.getValue("DOCMAN_FILE_NAME")))
      {
         // Otherwise use the DocMan file name..
         file_name =  structureset.getValue("DOCMAN_FILE_NAME");

      }
      //Bug Id 74523 Start
      file_name = structureset.getValue("FILE_PATH") + adjustFileName(structureset.getValue("FILE_PATH"),file_name);
      //Bug Id 74523 end
      debug("DEBUG: getCheckOutFileName()  file_name " + file_name);
      return file_name;
   }


   public void editStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Fetch client attributes for macro execution..
      String macro_attrs = getMacroAttributes();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));

            // Determine the type of action that must be peformed
            // on the current document..
            if ("1".equals(structureset.getValue("EDIT")))
            {
               if (mgr.isEmpty(structureset.getValue("FILE_STATE")))
                  doc.addItem("FILE_ACTION", "CREATENEW");
               else
                  doc.addItem("FILE_ACTION", "CHECKOUT");
            }
            else
            {
               doc.addItem("FILE_ACTION", "VIEW");
            }


            //
            // Setting the file name to be used when checking out..
            //

            String file_name = getCheckOutFileName();
            if (!mgr.isEmpty(file_name))
            {
               // If a file name is available..
               doc.addItem("FILE_NAME", file_name);
            }
            else
            {
               // If no file name is available for the current row
               // (Happens only for CREATENEW), specify the check out
               // directory only..

               doc.addItem("FILE_PATH", structureset.getValue("FILE_PATH"));
            }

            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");

            if (structureset.getCurrentRowNo() == 1)
            {
               // Add macro attributes to parent document..
               doc.addItem("MACRO_ATTRIBUTES", macro_attrs);
            }

            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);


      //
      // Settings for launching files, executing macros or
      // opening folders depending on selections made by user
      // and the current action being performed..
      //

      ASPBuffer buf;
      String post_check_out_action = ctx.readValue("POST_CHECK_OUT_ACTION");

      structureset.goTo(1);
      if ("LAUNCH_OR_RUN_MACRO".equals(post_check_out_action))
      {
         // Extra buffer to select and run macros..
         buf = docs.addBufferAt("DATA", 0);
         buf.addItem("FILE_ACTION", "SELECT_AND_EXECUTE_MACRO");
         buf.addItem("PROCESS", docs.getBufferAt(docs.countItems() - 1).getValue("FILE_ACTION"));
         buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
         buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
         buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
         buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
         buf.addItem("DOCUMENT_BUF_NO", Integer.toString(docs.countItems() - 1));
         buf.addItem("EDM_TITLE", getTitle());

         // In case the user does not select a macro, we should
         // get EdmMacro to launch the file..
         buf = docs.getBufferAt(docs.countItems() - 1);
         buf.setValue("LAUNCH_FILE", "YES");
      }
      else if ("OPEN_FOLDER".equals(post_check_out_action))
      {
         buf = mgr.newASPBuffer();
         buf.addItem("FILE_ACTION", "OPEN_FOLDER");
         buf.addItem("PATH", structureset.getValue("FILE_PATH"));
         docs.addBuffer("DATA", buf);
      }
      
       //Bug Id 74989, start
                                                                                                                
       ASPCommand cmd = trans.addCustomFunction("EDMINFO", "Edm_File_Api.get_edm_information", "STR_OUT");
       cmd.addParameter("STR_IN", structureset.getValue("DOC_CLASS"));
       cmd.addParameter("STR_IN", structureset.getValue("DOC_NO"));
       cmd.addParameter("STR_IN", structureset.getValue("DOC_SHEET"));
       cmd.addParameter("STR_IN", structureset.getValue("DOC_REV"));
       cmd.addParameter("STR_IN", "ORIGINAL");

       trans = mgr.perform(trans);

       String edm_info = trans.getValue("EDMINFO/DATA/STR_OUT");
       //Bug Id 74989, end
       
      // Check if the TLD does not have a file ref, if so add a buffer entry to open the application chooser.
       if (structureset.getValue("ORIGINAL_REF_EXIST").equalsIgnoreCase("FALSE") && "FALSE".equals(getStringAttribute(edm_info, "FILE_TEMPLATE_EXIST"))) //Bug Id 74989, check FILE_TEMPLATE_EXIST
      {         
         buf = docs.addBufferAt("DATA", 0);
         buf.addItem("FILE_ACTION", "SELECT_FILE_TYPE");
         buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
         buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
         buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
         buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
         buf.addItem("DOCUMENT_BUF_NO", Integer.toString(docs.countItems() - 1));
      }

      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLECHECKOUT: Check Out Report"));
      docs.addBuffer("DATA", buf);

      // configure the header..
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");
      //Bug Id 72460, start
      if ("OPEN_FOLDER".equals(post_check_out_action))
         buf.addItem("POST_CHECKOUT_ACTION", "OPEN_FOLDER");
      else if ("DO_NOTHING".equals(post_check_out_action))
         buf.addItem("POST_CHECKOUT_ACTION", "DO_NOTHING");
      //Bug Id 72460, end

      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }

   //Bug Id 74989, start
   protected String getStringAttribute(String attr_string, String attr_name)
   {
      StringTokenizer st = new StringTokenizer(attr_string, "^");

      attr_name += "=";
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(attr_name))
         {
            return str.substring(attr_name.length());
         }
      }
      return "";
   }
   //Bug Id 74989, end

   public void undoCheckOutStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Fetch client attributes for macro execution..
      String macro_attrs = getMacroAttributes();

      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("UNRESERVE")) || "1".equals(structureset.getValue("DELETE")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // Determine the type of action that must be
            // peformed on this document..
            if ("1".equals(structureset.getValue("UNRESERVE")))
            {
               // note: UNDOCHECKOUT also deletes the local file by default
               doc.addItem("FILE_ACTION", "UNDOCHECKOUT");

               if ("0".equals(structureset.getValue("DELETE")))
               {
                  // avoid deleting local file..
                  doc.addItem("DELETE_FILE", "NO");
               }
            }
            else if ("1".equals(structureset.getValue("DELETE")))
            {
               // only delete the local file..
               doc.addItem("FILE_ACTION", "DELETE_LOCAL_FILE");
            }

            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_NAME", structureset.getValue("FILE_NAME"));
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("EXECUTE_MACRO", "NO");

            if (structureset.getCurrentRowNo() == 1)
            {
               // Add macro attributes to parent document..
               doc.addItem("MACRO_ATTRIBUTES", macro_attrs);
            }

            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.next());

      // Extra buffer to select and run macros..
      ASPBuffer buf = docs.addBufferAt("DATA", 0);
      buf.addItem("FILE_ACTION", "SELECT_AND_EXECUTE_MACRO");
      buf.addItem("PROCESS", "UNDOCHECKOUT");
      buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
      buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
      buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
      buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
      buf.addItem("DOCUMENT_BUF_NO", "1");
      buf.addItem("EDM_TITLE", getTitle());


      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREUNDOCHECKOUTREPORT: Undo Checkout Report"));
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("CONFIRM", "NO");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("LOG_ERROR", "TRUE");


      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public void viewStructure() throws FndException
   {
       debug("DEBUG: viewStructure() ");
      ASPManager mgr = getASPManager();

      // Fetch client attributes for macro execution..
      String macro_attrs = getMacroAttributes();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_ACTION", "VIEW");
            doc.addItem("FILE_NAME", getCheckOutFileName());
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");

            if (structureset.getCurrentRowNo() == 1)
            {
               // Add macro attributes to parent document..
               doc.addItem("MACRO_ATTRIBUTES", macro_attrs);
            }

            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);



      //
      // Settings for launching files, executing macros or
      // opening folders depending on selections made by user
      // and the current action being performed..
      //

      String post_check_out_action = ctx.readValue("POST_CHECK_OUT_ACTION");
      ASPBuffer buf;

      structureset.goTo(1);
      if ("LAUNCH_OR_RUN_MACRO".equals(post_check_out_action))
      {
         // Extra buffer to select and run macros..
         buf = docs.addBufferAt("DATA", 0);
         buf.addItem("FILE_ACTION", "SELECT_AND_EXECUTE_MACRO");
         buf.addItem("PROCESS", docs.getBufferAt(docs.countItems() - 1).getValue("FILE_ACTION"));
         buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
         buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
         buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
         buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
         buf.addItem("DOCUMENT_BUF_NO", Integer.toString(docs.countItems() - 1));
         buf.addItem("EDM_TITLE", getTitle());

         // In case the user does not select a macro, we should
         // get EdmMacro to launch the file..
         buf = docs.getBufferAt(docs.countItems() - 1);
         buf.setValue("LAUNCH_FILE", "YES");
      }
      else if ("OPEN_FOLDER".equals(post_check_out_action))
      {
         buf = mgr.newASPBuffer();
         buf.addItem("FILE_ACTION", "OPEN_FOLDER");
         buf.addItem("PATH", structureset.getValue("FILE_PATH"));
         docs.addBuffer("DATA", buf);
      }

      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLEVIEW: View Report"));      
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      //Bug Id 73718, start
      if ("VIEWCOPY".equals(file_action) )
         buf.addItem("DOC_TYPE", "VIEW"); 
      else
	 buf.addItem("DOC_TYPE", "ORIGINAL");
      //Bug Id 73718, end
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");
      //Bug Id 72460, start
      if ("OPEN_FOLDER".equals(post_check_out_action))
         buf.addItem("POST_CHECKOUT_ACTION", "OPEN_FOLDER");
      else if ("DO_NOTHING".equals(post_check_out_action))
         buf.addItem("POST_CHECKOUT_ACTION", "DO_NOTHING");
      //Bug Id 72460, end

      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public void printStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Fetch client attributes for macro execution..
      String macro_attrs = getMacroAttributes();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));

            // Only parent document has to be printed. So set
            // FILE_ACTION of other documents to VIEW..

            if (structureset.getCurrentRowNo() == 1)
               doc.addItem("FILE_ACTION", "PRINT");
            else
               doc.addItem("FILE_ACTION", "PRINT_VIEW"); //Bug id 80882


            doc.addItem("FILE_NAME", getCheckOutFileName());
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");

            if (structureset.getCurrentRowNo() == 1)
            {
               // Add macro attributes to parent document..
               doc.addItem("MACRO_ATTRIBUTES", macro_attrs);
            }
            
            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);


      // Extra buffer to select and run macros..
      structureset.goTo(1);
      ASPBuffer buf = docs.addBufferAt("DATA", 0);
      buf.addItem("FILE_ACTION", "SELECT_AND_EXECUTE_MACRO");
      buf.addItem("PROCESS", docs.getBufferAt(docs.countItems() - 1).getValue("FILE_ACTION"));
      buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
      buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
      buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
      buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
      buf.addItem("DOCUMENT_BUF_NO", Integer.toString(docs.countItems() - 1));
      buf.addItem("EDM_TITLE", getTitle());

      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLEPRINT: Print Report"));
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      //Bug Id 73718, start
      if ("PRINTCOPY".equals(file_action))
	  buf.addItem("DOC_TYPE", "VIEW");
      else
	  buf.addItem("DOC_TYPE", "ORIGINAL");
      //Bug Id 73718, end
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");


      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public void copyStructure() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_ACTION", "GETCOPYTODIR");
            doc.addItem("FILE_NAME", getCheckOutFileName());
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");
            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);

      // Buffer to show error log..
      ASPBuffer buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLEGETCOPYTODIR: Copy Report"));
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");


      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public void sendStructureByMail() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      ASPBuffer buf = mgr.newASPBuffer();
      String file_list = "";
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_ACTION", "GET_DOCUMENTS_FOR_EMAIL");

            String file_name = getCheckOutFileName();
            doc.addItem("FILE_NAME", file_name);
            doc.addItem("TITLE", structureset.getValue("TITLE"));

            file_list += file_name + DocmawUtil.FIELD_SEPARATOR;

            //
            // Look for additional files apart from 
            // the original
            //

            String file_path = DocmawUtil.getPath(file_name);
            String base_file_name = DocmawUtil.getBaseFileName(file_name);
            String edm_info = structureset.getValue("EDM_INFO");

            if (!mgr.isEmpty(structureset.getValue("CONNECTED_DOC_TYPES")))
            {
               StringTokenizer st = new StringTokenizer(structureset.getValue("CONNECTED_DOC_TYPES"), "^");
               while (st.hasMoreTokens())
               {
                  String doc_type = st.nextToken();
                  if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, doc_type + "_REF_EXIST")))
                  {
                     file_list += file_path + "\\" + base_file_name + "." +  DocmawUtil.getAttributeValue(edm_info, doc_type + "_FILE_EXTENTION") + DocmawUtil.FIELD_SEPARATOR;
                  }
               }
            }

            // Check for View Copy and Redline files
            if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "REDLINE_EXIST")))
               file_list += file_path + "\\" + base_file_name + "." +  DocmawUtil.getAttributeValue(edm_info, "REDLINE_FILE_EXTENTION") + DocmawUtil.FIELD_SEPARATOR;
            if ("TRUE".equals(DocmawUtil.getAttributeValue(edm_info, "VIEW_REF_EXIST")))
               file_list += file_path + "\\" + base_file_name + "." +  DocmawUtil.getAttributeValue(edm_info, "VIEW_FILE_EXTENTION") + DocmawUtil.FIELD_SEPARATOR;
            
            
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");
            doc.addItem("EDM_TITLE", getTitle());
            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);

      // Extra buffer to zip and e-mail files..

      structureset.goTo(1);
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
      buf.addItem("DOC_NO", structureset.getValue("DOC_NO"));
      buf.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
      buf.addItem("DOC_REV", structureset.getValue("DOC_REV"));
      buf.addItem("FILE_ACTION", "ZIP_AND_EMAIL");
      //Bug Id 74523, Start
      String title = adjustFileName(mgr.readValue("LOCAL_CHECK_OUT_PATH") + "Temp\\", mgr.translate("DOCMAWDOCSTRUCTUREZIPFILENAME: Copy of Structure for") + " " + structureset.getValue("TITLE") + ".zip");
      //Bug Id 74523, End
      buf.addItem("ZIP_FILE_NAME", mgr.readValue("LOCAL_CHECK_OUT_PATH") + "Temp\\" + title); //bug id 74523
      //Bug Id 74523 Start
      //buf.addItem("ROOT_DIRECTORY", DocmawUtil.getPath(getCheckOutFileName()));
      buf.addItem("ROOT_DIRECTORY", structureset.getValue("FILE_PATH"));
      //Bug Id 74523 End
      buf.addItem("FILE_LIST", file_list);
      buf.addItem("EDM_TITLE", getTitle());


      
      // Mail subject..
      buf.addItem("MAIL_SUBJECT", mgr.translate("DOCMAWDOCSTRUCTUREMAILSUBJECT: Regarding") + ": " + structureset.getValue("TITLE"));

      //Bug Id 73605, start
      //Setup the mail body
      String mail_msg_body = mgr.translate("DOCMAWDOCSTRUCTUREMAILBODYTEXT: The following documents are attached to this e-mail:") + " \\n \\n"; 
      String mail_msg = ""; 
      ASPCommand cmd ; 
      String doc_state = "";
      do 
      {
	  if ("1".equals(structureset.getValue("VIEW")))
	  {

             String doc_title = structureset.getValue("TITLE");
             

             trans.clear();
             cmd = trans.addCustomFunction("GET_STATE", "Doc_Issue_API.Get_State", "STR_OUT");
             cmd.addParameter("STR_IN", structureset.getValue("DOC_CLASS"));
             cmd.addParameter("STR_IN", structureset.getValue("DOC_NO"));
             cmd.addParameter("STR_IN", structureset.getValue("DOC_SHEET"));
             cmd.addParameter("STR_IN", structureset.getValue("DOC_REV"));
             trans = mgr.perform(trans);
             doc_state = trans.getValue("GET_STATE/DATA/STR_OUT");

	     int level = Integer.parseInt(structureset.getValue("LEVEL"));
	     String sSpaces = "";
	     for (int i = 0; i < (level-1)*3; i++) 
	     {
		 sSpaces = sSpaces + " ";
	     }
	     mail_msg = doc_title + " " + "(" + structureset.getValue("DOC_CLASS") + " - " + structureset.getValue("DOC_NO") + " - " + structureset.getValue("DOC_SHEET") + " - " + structureset.getValue("DOC_REV") + ")" + " - " + doc_state + "\\n"; 
             mail_msg_body = mail_msg_body + sSpaces + mail_msg ; 
	  }

      } 
      while (structureset.next() && structureset.getCurrentRowNo() != structureset.countRows() );

      buf.addItem("MAIL_BODY", mail_msg_body);

      docs.addBuffer("DATA", buf);
      //Bug Id 73605, end

      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");      
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTURESENDBYEMAILREPORT: Send by E-mail Report"));
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");

      
      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }



   public void viewStructureWithExternalViewer() throws FndException
   {
      ASPManager mgr = getASPManager();

      // Build the document buffer..
      ASPBuffer docs = mgr.newASPBuffer();
      structureset.goTo(structureset.countRows() - 1);
      do
      {
         // Perform action only on the selected documents..
         if ("1".equals(structureset.getValue("VIEW")))
         {
            ASPBuffer doc = mgr.newASPBuffer();

            // document keys..
            doc.addItem("DOC_CLASS", structureset.getValue("DOC_CLASS"));
            doc.addItem("DOC_NO", structureset.getValue("DOC_NO"));
            doc.addItem("DOC_SHEET", structureset.getValue("DOC_SHEET"));
            doc.addItem("DOC_REV", structureset.getValue("DOC_REV"));
            doc.addItem("FILE_ACTION", "VIEW");
            doc.addItem("FILE_NAME", getCheckOutFileName());
            doc.addItem("TITLE", structureset.getValue("TITLE"));
            doc.addItem("LAUNCH_FILE", "NO");
            doc.addItem("EXECUTE_MACRO", "NO");
            docs.addBuffer("DATA", doc);
         }
      }
      while (structureset.previous() && structureset.getCurrentRowNo() != 0);

      // Launch parent file with external viewer..
      structureset.goTo(1);
      ASPBuffer buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER");
      //buf.addItem("FILE_NAME", getCheckOutFileName());

      //Bug Id 71463, start
      String file_name = null;

      if (!mgr.isEmpty(structureset.getValue("FILE_NAME")))
      {
         // If a file name is available..
         file_name = structureset.getValue("FILE_NAME");
      }
      else if (!mgr.isEmpty(structureset.getValue("DOCMAN_FILE_NAME")))
      {
         // Otherwise use the DocMan file name..
         file_name =  structureset.getValue("DOCMAN_FILE_NAME");
      }
      file_name = structureset.getValue("FILE_PATH") + adjustFileName(structureset.getValue("FILE_PATH"),file_name);
      debug("DEBUG:extviewer  file_name " + file_name);
      buf.addItem("FILE_NAME", file_name);
      //Bug Id 71463, end

      docs.addBuffer("DATA", buf);


      // Buffer to show error log..
      buf = mgr.newASPBuffer();
      buf.addItem("FILE_ACTION", "SHOW_ERROR_LOG");
      buf.addItem("REPORT_TITLE", mgr.translate("DOCMAWDOCSTRUCTUREREPORTTITLEVIEWWITHEXTVIEWER: View Report"));
      docs.addBuffer("DATA", buf);


      // configure the header..
      buf = mgr.newASPBuffer();
      buf.addItem("DOC_TYPE", "ORIGINAL");
      buf.addItem("SOURCE", "DOC_STRUCTURE");
      buf.addItem("SAME_ACTION_TO_ALL", "NO");
      buf.addItem("FILE_NAME_CONFIGURABLE", "NO");
      buf.addItem("LOG_ERROR", "TRUE");
      buf.addItem("FILE_ACTION", "LAUNCH_LOCAL_FILE_WITH_EXT_VIEWER"); //Bug Id 72460


      // transfer to EdmMacro for execution..
      String transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", buf, docs);
      appendDirtyJavaScript("document.location = \"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
      appendDirtyJavaScript("\";\n");
   }


   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      disableOptions();
      disableHomeIcon();
      disableNavigate();

      //
      // Block for documents in structure
      //
      // NOTE: Columns specific to a process have been prefixed with
      // the corressponding process id for clarity.
      //

      structureblk = mgr.newASPBlock("STRUCTURE");

      structureblk.addField("CONNECT_ADD").
                   setHidden().
                   setSize(3).
                   setReadOnly().
                   setBold().
                   setHilite().
                   //setHyperlink("javascript:executeServerCommand('connecFile')", "DOC_CLASS").
                   setAlignment(ASPField.ALIGN_CENTER).
                   setLabel("");

      structureblk.addField("CHECKIN").
                   setHidden().
                   setCheckBox("0,1").
                   setValidateFunction("validateCheckIn").
                   setLabel("DOCMAWDOCSTRUCTURECHECKINSELECTBOX: Check In");

      structureblk.addField("VIEW").
                   setHidden().
                   setCheckBox("0,1").
                   setValidateFunction("validateSelect").
                   setLabel("DOCMAWDOCSTRUCTUREEDITSELECTBOX: Select");

      structureblk.addField("EDIT").
                   setHidden().
                   setCheckBox("0,1").
                   setValidateFunction("validateEdit").
                   setLabel("DOCMAWDOCSTRUCTUREEDITEDITBOX: Edit");

      structureblk.addField("UNRESERVE").
                   setHidden().
                   setCheckBox("0,1").
                   setValidateFunction("validateUnreserve").
                   setLabel("DOCMAWDOCSTRUCTUREEDITUNRESERVEBOX: Unreserve");

      structureblk.addField("DELETE").
                   setHidden().
                   setCheckBox("0,1").
                   setValidateFunction("validateDelete").
                   setLabel("DOCMAWDOCSTRUCTUREEDITDELETEBOX: Delete");

      structureblk.addField("LEVEL").
                   setSize(5).
                   setReadOnly().
                   setLabel("DOCMAWDOCSTRUCTURELEVEL: Level");

      structureblk.addField("DOC_CLASS").
                   setSize(12).
                   setMaxLength(12).
                   setUpperCase().
                   setMandatory().
                   setDynamicLOV("DOC_CLASS").
                   setCustomValidation("DOC_CLASS","DOC_SHEET,DOC_REV,NUM_COUNT_ID1,NUM_COUNT_ID2").
                   setLabel("DOCMAWDOCSTRUCTUREDOCCLASS: Doc Class");

      structureblk.addField("DOC_NO").
                   setSize(10).
                   setMaxLength(120).
                   setUpperCase().
                   setLabel("DOCMAWDOCSTRUCTUREDOCNO: Doc No");

      structureblk.addField("DOC_SHEET").
                   setSize(10).
                   setMaxLength(10).
                   setUpperCase().
                   setLabel("DOCMAWDOCSTRUCTUREDOCSHEET: Doc Sheet");

      structureblk.addField("DOC_REV").
                   setSize(6).
                   setMaxLength(6).
                   setUpperCase().
                   setLabel("DOCMAWDOCSTRUCTUREDOCREV: Doc Rev");

      structureblk.addField("TITLE").
                   setLabel("DOCMAWDOCSTRUCTUREDOCTITLE: Title");

      structureblk.addField("FILE_PATH").
                   setSize(40).
                   setReadOnly().
                   setLabel("DOCMAWDOCSTRUCTUREPATH: Path");

      structureblk.addField("FILE_NAME").
                   setSize(40).
                   setLabel("DOCMAWDOCSTRUCTUREFILENAME: File Name");

      structureblk.addField("FILE_TYPE").                  
                   setSize(12).
                   setUpperCase().                   
                   setValidateFunction("validateAllFileTypes").
                   setDynamicLOV("EDM_APPLICATION").
                   setLOVProperty("WHERE", "DOCUMENT_TYPE IN ('ORIGINAL','VIEW')").
                   setLabel("DOCMAWDOCSTRUCTUREFILETYPE: File Type");

      structureblk.addField("FILE_STATE").
                   setReadOnly().
                   setLabel("DOCMAWDOCSTRUCTUREFILESTATE: File State");

      // ## TODO, make other hidden files to "default not visible"
      structureblk.addField("CHECKED_OUT_TO_ME").
                   setHidden();

      structureblk.addField("NEW_DOCUMENT").
                   setHidden();

      structureblk.addField("NUM_COUNT_ID1").
                   setSize(10).
                   setReadOnly().
                   setLabel("DOCMAWDOCSTRUCTURENUMCOUNTID1: Num Count ID1");

      structureblk.addField("NUM_COUNT_ID2").
                   setSize(10).
                   setMaxLength(10).
                   setUpperCase().
                   setLOV("Id2Lov.page","NUM_COUNT_ID1 ID1").//Bug Id 73606
                   setLabel("DOCMAWDOCSTRUCTURENUMCOUNTID2: Num Count ID2");

      structureblk.addField("BOOKING_LIST").
                   setSize(20).
                   setMaxLength(20).
                   setLOV("BookListLov.page", "NUM_COUNT_ID1 ID1,NUM_COUNT_ID2 ID2").//Bug Id 73606
                   setLabel("DOCMAWDOCSTRUCTUREBOOKINGLIST: Booking List");

      structureblk.addField("EDM_INFO").
                   setHidden();
      
      structureblk.addField("RELATIVE_PATH").
                   setHidden();

      structureblk.addField("ORIGINAL_FILE_NAME").
                   setHidden();

      structureblk.addField("DOCMAN_FILE_NAME").
                   setHidden();

      structureblk.addField("CHECK_IN_ACCESS").
                   setHidden();

      structureblk.addField("EDIT_ACCESS").
                   setHidden();

      structureblk.addField("VIEW_ACCESS").
                   setHidden();

      structureblk.addField("ORIGINAL_REF_EXIST").
                   setHidden();
      
      structureblk.addField("CONNECTED_DOC_TYPES").
                   setHidden();

      structureblk.addField("DUUMY_FOLDER_STRUCTURE").
                   setLabel("DOCMAWDOCSTRUCTURELABELUSEORGFLDSTR: Use Original Folder Structure").
                   setHidden();

      structureset = structureblk.getASPRowSet();
      structuretbl = mgr.newASPTable(structureblk);
      structuretbl.setTitle(mgr.translate("DOCMAWDOCSTRUCTURETABLETITLE: Document Structure"));
      structuretbl.enableTitleNoWrap();
      structuretbl.setEditable();
      structuretbl.unsetSortable();
      structuretbl.disableRowCounter();

      structurelay = structureblk.getASPBlockLayout();
      structurelay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      structurebar = mgr.newASPCommandBar(structureblk);
      structurebar.disableCommand(ASPCommandBar.FIND);

      structurebar.addCustomCommand("addSubLevelDocuments", mgr.translate("DOCMAWDOCSTRUCTUREADDSUBLEVELDOCUMENTS: Add Subdocuments..."));
      structurebar.addCustomCommand("connectFile", mgr.translate("DOCMAWDOCSTRUCTURECONNECTFILE: Connect File..."));
      structurebar.addCustomCommand("deleteRow", mgr.translate("DOCMAWDOCSTRUCTUREDELETEROW: Delete Subdocument"));
      structurebar.addCustomCommand("resetCheckboxes", mgr.translate("DOCMAWDOCSTRUCTURERESETCHECKBOXES: Reset Checkboxes"));
      structurebar.defineCommand("resetCheckboxes", null, "resetCheckboxes");

      structurebar.disableMultirowAction();


      // valid conditions for enabling/disabling commands..
      structurebar.addCommandValidConditions("addSubLevelDocuments", "FILE_NAME", "Disable", "");
      structurebar.addCommandValidConditions("connectFile", "FILE_STATE", "Enable", "");
      structurebar.addCommandValidConditions("deleteRow", "NEW_DOCUMENT", "Enable", "TRUE");
      structurebar.addCommandValidConditions("resetCheckboxes", "LEVEL", "Enable", "");


      // dummy commands so that we could "bind" html-button actions to java (server) functions..
      structurebar.addCustomCommand("editStructure", "Edit Structure");
      structurebar.disableCommand("editStructure");
      structurebar.addCustomCommand("checkInStructure", "Check In Structure");
      structurebar.disableCommand("checkInStructure");
      structurebar.addCustomCommand("undoCheckOutStructure", "Undo Checkout Structure");
      structurebar.disableCommand("undoCheckOutStructure");
      structurebar.addCustomCommand("viewStructure", "View Structure");
      structurebar.disableCommand("viewStructure");
      structurebar.addCustomCommand("printStructure", "Print Structure");
      structurebar.disableCommand("printStructure");
      structurebar.addCustomCommand("copyStructure", "Copy Structure");
      structurebar.disableCommand("copyStructure");
      structurebar.addCustomCommand("sendStructureByMail", "Send Structure by E-Mail");
      structurebar.disableCommand("sendStructureByMail");
      structurebar.addCustomCommand("viewStructureWithExternalViewer", "View with External Viewer");
      structurebar.disableCommand("viewStructureWithExternalViewer");



      //
      // Dummy block
      //

      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("STR_IN");
      dummyblk.addField("STR_OUT");
      dummyblk.addField("STR_INOUT");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
      


      //
      // Static Javascript
      //
      // Set window size..
      appendJavaScript("if (document.body.offsetWidth < 800 || document.body.offsetHeight < 550)\n");
      appendJavaScript("{\n");
      appendJavaScript("   try\n");
      appendJavaScript("   {\n");
      appendJavaScript("      window.resizeTo(800, 550);\n");
      appendJavaScript("   }\n");
      appendJavaScript("   catch(err){}\n");
      appendJavaScript("}\n");

      appendJavaScript("function selectFile(default_path, file_types, multiple, title, default_ext, command)\n");
      appendJavaScript("{\n");            
      appendJavaScript("   var files = document.form.oCliMgr.ShowFileBrowser(default_path, file_types, multiple, title, default_ext, '');\n");
      appendJavaScript("   files = filterFilesWithoutExtensions(files);\n"); // Bug Id 92125
      appendJavaScript("   if (files != null && files != \"\")\n");
      appendJavaScript("      document.form.FILES_SELECTED.value = files;\n");
      appendJavaScript("   else\n");
      appendJavaScript("      document.form.FILES_SELECTED.value = \"<NONE>\";\n");
      appendJavaScript("   executeServerCommand(command);\n");
      appendJavaScript("}\n");
      
      // Bug Id 92125, Start
      appendJavaScript("function filterFilesWithoutExtensions(files)\n");
      appendJavaScript("{\n");
      appendJavaScript("   arrFiles = files.split(\"|\");\n");
      appendJavaScript("   files = \"\";\n");
      appendJavaScript("   bShowFileExtError = \"FALSE\";\n");
      appendJavaScript("   for (i=0; i<arrFiles.length ; i++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if(arrFiles[i] != \"\")\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if(arrFiles[i].lastIndexOf(\".\") > -1)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            if(files != \"\")\n");
      appendJavaScript("               files += \"|\";\n");
      appendJavaScript("            files = arrFiles[i];\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else\n");
      appendJavaScript("            bShowFileExtError = \"TRUE\";\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("	if (bShowFileExtError==\"TRUE\")\n");        
      appendJavaScript("   {\n");
      appendJavaScript("      alert(\"");
      appendJavaScript(mgr.translate("DOCMAWDOCSTRUCTNOEXTALERT: File(s) without extension(s) are not allowed."));
      appendJavaScript("\");\n");
      appendJavaScript("   }\n");
      appendJavaScript("   return files;\n");
      appendJavaScript("}\n");
      // Bug Id 92125, End

      appendJavaScript("function deleteConfirmation()\n");
      appendJavaScript("{\n");
      appendJavaScript("   return confirm(\"");
      appendJavaScript(mgr.translate("DOCMAWDOCSTRUCTUREDELETECONFIRMATION: This will delete the current document and all its sub documents as well. Continue?"));
      appendJavaScript("   \")");
      appendJavaScript("}\n");


      appendJavaScript("function executeServerCommand(command)\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (document.form.CHECKOUT_PATH != null)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (!validateCheckOutPath(document.form.CHECKOUT_PATH.value))\n");
      appendJavaScript("         return;\n");
      //Bug Id 54793, Start
      appendJavaScript("      else\n");
      appendJavaScript("        {\n");
      appendJavaScript("            var PathWriteProtected = document.form.oCliMgr.CheckPathWriteProtected(document.form.CHECKOUT_PATH.value);\n");
      appendJavaScript("            if (PathWriteProtected)\n");
      appendJavaScript("            {\n");
      appendJavaScript("                eval(\"opener.refreshParent()\");\n");
      appendJavaScript("                window.close();\n");
      appendJavaScript("            }\n");
      appendJavaScript("        }\n");
      //Bug Id 54793, End
      appendJavaScript("   }\n");
      appendJavaScript("   if (command == 'checkInStructure')\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (!checkFileExtensionsOk())\n");
      appendJavaScript("         return;\n");
      appendJavaScript("   }\n");
      appendJavaScript("   f.__STRUCTURE_Perform.value = command;\n");
      appendJavaScript("   commandSet('STRUCTURE.Perform','');\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateCheckOutPath(path)\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (!document.form.oCliMgr.DriveExists(path))\n");
      appendJavaScript("   {\n");
      appendJavaScript("      alert(\"" + mgr.translate("DOCMAWDOCSTRUCTUREINVALIDDRIVE: Invalid drive name") + "\");\n");
      appendJavaScript("      return false;\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (!document.form.oCliMgr.FolderExists(path))\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (confirm(\"" + mgr.translate("DOCMAWDOCSTRUCTUREINVALIDCHECKOUTPATH: The Checkout Folder does not exist. Do you want to create it?") + "\"))\n");
      appendJavaScript("         {\n");
      appendJavaScript("            try\n");
      appendJavaScript("            {\n");
      appendJavaScript("               document.form.oCliMgr.CreatePath(path);\n");
      appendJavaScript("            }\n");
      appendJavaScript("            catch(e)\n");
      appendJavaScript("            {\n");
      appendJavaScript("               alert(e.description);\n");
      appendJavaScript("               return false;\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else\n");
      appendJavaScript("            return false;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   return true;\n");
      appendJavaScript("}\n");


      appendJavaScript("function showFolderBrowser()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var path;\n");
      appendJavaScript("   path = document.form.oCliMgr.BrowseForFolder(\"" + mgr.translate("DOCMAWDLGUSERSETTINGSCHOOSELOCALCHECKOUTPATH: Select Local Checkout Path:") +"\");\n");
      appendJavaScript("   if (path != \"\")\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (path.charAt(path.length - 1) != '\\\\')\n");
      appendJavaScript("         path = path + '\\\\';\n");
      appendJavaScript("      document.form.CHECKOUT_PATH.value = path;\n");
      appendJavaScript("      setCheckOutPath();\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function getDocumentProperty(doc_class, doc_no, doc_sheet, doc_rev, property_name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var key_name = \"Software\\\\IFS\\\\Document Management\\\\Documents\\\\\" + doc_class + '-' + doc_no + '-' + doc_sheet + '-' + doc_rev;\n");
      appendJavaScript("   var property_value = document.form.oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", key_name, property_name);\n");
      appendJavaScript("   return property_value;\n");
      appendJavaScript("}\n");


      appendJavaScript("function enableExecuteButton(enable)\n");
      appendJavaScript("{\n");
      appendJavaScript("   document.form.EXECUTE.disabled = !enable;\n");
      appendJavaScript("}\n");


      appendJavaScript("function isEmpty(str)\n");
      appendJavaScript("{\n");
      appendJavaScript("   return (str == '' || str == null);\n");
      appendJavaScript("}\n");

    
      appendJavaScript("function setFieldReadOnly(field)\n");
      appendJavaScript("{\n");
      appendJavaScript("   field.readOnly = true;\n");
      appendJavaScript("   field.className = \"readOnlyTextField\";\n");
      appendJavaScript("}\n");


      appendJavaScript("function setFieldEditable(field)\n");
      appendJavaScript("{\n");
      appendJavaScript("   field.readOnly = false;\n");
      appendJavaScript("   field.className = \"editableTextField\";\n");
      appendJavaScript("}\n");


      appendJavaScript("function setFieldEvent(field_name, event_name, funct_name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var field = getElements(field_name);\n");
      appendJavaScript("   for (var x = 0; x < field.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      eval(\"field[x].\" + event_name + \"=\" + funct_name);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setFieldEventProperty(field_name, property_name, property_value)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var field = getElements(field_name);\n");
      appendJavaScript("   for (var x = 0; x < field.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      eval(\"field[x].\" + property_name + \"=field[x].\" + property_value);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setFieldProperty(field_name, property_name, property_value)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var field = getElements(field_name);\n");
      appendJavaScript("   for (var x = 0; x < field.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      eval(\"field[x].\" + property_name + \"='\" + property_value + \"'\");\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setCheckOutPath()\n");
      appendJavaScript("{\n");
      appendJavaScript("   if (document.form.ORIGINAL_FOLDER_STRUCTURE.checked == true)\n");
      appendJavaScript("      setPath(true);\n");
      appendJavaScript("   else\n");
      appendJavaScript("      setPath(false);\n");
      appendJavaScript("}\n");


      appendJavaScript("function getElements(name)\n");
      appendJavaScript("{\n");
      appendJavaScript("   return eval('document.form.' + name);\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateCheckIn(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var checkinbox = getElements('_CHECKIN');\n");
      appendJavaScript("   CCA(checkinbox[i - 1], i);\n");
      appendJavaScript("}\n");


//      appendJavaScript("function validateEdit(i)\n");
//      appendJavaScript("{\n");
//      appendJavaScript("   var editbox = getElements('_EDIT');\n");
//      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
//      appendJavaScript("   var edit    = getElements('EDIT');\n");
//      appendJavaScript("   var view    = getElements('VIEW');\n");
//      appendJavaScript("   var checked = editbox[i - 1].checked;\n");
//      appendJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
//      appendJavaScript("   if (i == 1)\n");
//      appendJavaScript("   {\n");
//      appendJavaScript("      for (var x = 0; x < editbox.length; x++)\n");
//      appendJavaScript("      {\n");
//      appendJavaScript("         if (!editbox[x].disabled)\n");
//      appendJavaScript("         {\n");
//      appendJavaScript("            editbox[x].checked = checked;\n");
//      appendJavaScript("            edit[x + 1].value = value;\n");
//      appendJavaScript("            if (checked || viewbox[x].disabled)\n");
//      appendJavaScript("            {\n");
//      appendJavaScript("               viewbox[x].checked = checked;\n");
//      appendJavaScript("               view[x + 1].value = value;\n");
//      appendJavaScript("            }\n");
//      appendJavaScript("            if (x > 0)\n");
//      appendJavaScript("               CCA(viewbox[x], x + 1);\n");
//      appendJavaScript("         }\n");
//      appendJavaScript("      }\n");
//      appendJavaScript("   }\n");
//      appendJavaScript("   else\n");
//      appendJavaScript("   {\n");
//      appendJavaScript("      edit[i].value = value;\n");
//      appendJavaScript("      if (checked || viewbox[i - 1].disabled)\n");
//      appendJavaScript("      {\n");
//      appendJavaScript("         view[i].value = value;\n");
//      appendJavaScript("         viewbox[i - 1].checked = checked;\n");
//      appendJavaScript("      }\n");
//      appendJavaScript("      CCA(viewbox[i - 1], i);\n");
//      appendJavaScript("   }\n");
//      appendJavaScript("}\n");


//      appendJavaScript("function validateSelect(i)\n");
//      appendJavaScript("{\n");
//      appendJavaScript("   alert('in validate select');\n");
//      appendJavaScript("   var file_action = document.form.FILE_ACTION.value;\n");
//      appendJavaScript("   if (file_action == \"CHECKOUT\" || file_action == \"CREATENEW\")\n");
//      appendJavaScript("      validateSelectOnEdit(i);\n");
//      appendJavaScript("   else if (file_action == \"VIEW\" || file_action == \"PRINT\" || file_action == \"GETCOPYTODIR\" || file_action == \"SENDMAIL\")\n");
//      appendJavaScript("      validateSelectOnView(i);\n");
//      appendJavaScript("}\n");


      appendJavaScript("function validateSelectOnEdit(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var editbox = getElements('_EDIT');\n");
      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendJavaScript("   var edit    = getElements('EDIT');\n");
      appendJavaScript("   var view    = getElements('VIEW');\n");
      appendJavaScript("   var checked = viewbox[i - 1].checked;\n");
      appendJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
      appendJavaScript("   if (i == 1)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < viewbox.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (viewbox[x].value != \"2\")\n");
      appendJavaScript("         {\n");
      appendJavaScript("            if (!checked || (checked && !viewbox[x].disabled))\n");
      appendJavaScript("            {\n");
      appendJavaScript("               viewbox[x].checked = checked;\n");
      appendJavaScript("               view[x + 1].value = value;\n");
      appendJavaScript("               if (x > 0)\n");
      appendJavaScript("                  CCA(viewbox[x], x + 1);\n");
      appendJavaScript("            }\n");
      appendJavaScript("            if (!checked)\n");
      appendJavaScript("            {\n");
      appendJavaScript("               editbox[x].checked = checked;\n");
      appendJavaScript("               edit[x + 1].value = value;\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      view[i].value = value;\n");
      appendJavaScript("      if (!checked)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         editbox[i - 1].checked = checked;\n");
      appendJavaScript("         edit[i].value = value;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      CCA(viewbox[i - 1], i);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateSelectOnView(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendJavaScript("   var view    = getElements('VIEW');\n");
      appendJavaScript("   var checked = viewbox[i - 1].checked;\n");
      appendJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
      appendJavaScript("   if (i == 1)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < viewbox.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (viewbox[x].value != \"2\")\n");
      appendJavaScript("         {\n");
      appendJavaScript("            if (!checked || (checked && !viewbox[x].disabled))\n");
      appendJavaScript("            {\n");
      appendJavaScript("               viewbox[x].checked = checked;\n");
      appendJavaScript("               view[x + 1].value = value;\n");
      appendJavaScript("               if (x > 0)\n");
      appendJavaScript("                  CCA(viewbox[x], x + 1);\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      view[i].value = value;\n");
      appendJavaScript("      CCA(viewbox[i - 1], i);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateUnreserve(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var unreservebox = getElements('_UNRESERVE');\n");
      appendJavaScript("   var deletebox = getElements('_DELETE');\n");
      appendJavaScript("   var unreserve = getElements('UNRESERVE');\n");
      appendJavaScript("   var delete_field = getElements('DELETE');\n");
      appendJavaScript("   var checked = unreservebox[i - 1].checked;\n");
      appendJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
      appendJavaScript("   if (i == 1)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < unreservebox.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (!unreservebox[x].disabled )\n");
      appendJavaScript("         {\n");
      appendJavaScript("            unreservebox[x].checked = checked;\n");
      appendJavaScript("            unreserve[x + 1].value = value;\n");
      appendJavaScript("            if (x > 0)\n");
      appendJavaScript("            {\n");
      appendJavaScript("               CCA(unreservebox[x], x + 1);\n");
      appendJavaScript("               if (!checked && !deletebox[x].disabled)\n");
      appendJavaScript("               {\n");
      appendJavaScript("                  deletebox[x].checked = checked;\n");
      appendJavaScript("                  delete_field[x + 1].value = value;\n");
      appendJavaScript("               }\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      unreserve[i].value = value;\n");
      appendJavaScript("      if (!checked)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         deletebox[i - 1].checked = checked;\n");
      appendJavaScript("         delete_field[i].value = value;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      CCA(unreservebox[i - 1], i);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateDelete(i)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var unreservebox = getElements('_UNRESERVE');\n");
      appendJavaScript("   var deletebox = getElements('_DELETE');\n");
      appendJavaScript("   var unreserve = getElements('UNRESERVE');\n");
      appendJavaScript("   var delete_field = getElements('DELETE');\n");
      appendJavaScript("   var checked = deletebox[i - 1].checked;\n");
      appendJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
      appendJavaScript("   if (i == 1)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < deletebox.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (!deletebox[x].disabled)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            deletebox[x].checked = checked;\n");
      appendJavaScript("            delete_field[x + 1].value = value;\n");
      appendJavaScript("            if (checked && !unreservebox[x].disabled)\n");
      appendJavaScript("            {\n");
      appendJavaScript("               unreservebox[x].checked = checked;\n");
      appendJavaScript("               unreserve[x + 1].value = value;\n");
      appendJavaScript("            }\n");
      appendJavaScript("            if (checked && unreservebox[x].disabled)\n");
      appendJavaScript("               CCA(deletebox[x], x + 1);\n");
      appendJavaScript("            else if (x > 0)\n");
      appendJavaScript("               CCA(unreservebox[x], x + 1);\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      delete_field[i].value = value;\n");
      appendJavaScript("      if (checked && !unreservebox[i - 1].disabled && !unreservebox[i - 1].checked)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         unreservebox[i - 1].checked = true;\n");
      appendJavaScript("         unreserve[i].value = value;\n");
      appendJavaScript("         CCA(unreservebox[i - 1], i);\n");
      appendJavaScript("      }\n");
      appendJavaScript("      if (checked)\n");
      appendJavaScript("         CCA(deletebox[i - 1], i);\n");
      appendJavaScript("      else\n");
      appendJavaScript("         CCA(unreservebox[i - 1], i);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function resetCheckboxes()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var editbox = getElements('_EDIT');\n");
      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendJavaScript("   for (var x = 0; x < viewbox.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (x == 0)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         viewbox[x].checked = true;\n");
      appendJavaScript("         editbox[x].checked = true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (arrViewable[x - 1] == 'TRUE')\n");
      appendJavaScript("            viewbox[x].checked = true;\n");
      appendJavaScript("         if (arrEditable[x - 1] == 'TRUE')\n");
      appendJavaScript("            editbox[x].checked = true;\n");
      appendJavaScript("         if (viewbox[x].checked)\n");
      appendJavaScript("            CCA(viewbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   return false;\n");
      appendJavaScript("}\n");


      appendJavaScript("function initialiseEditDialog()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var editbox = getElements('_EDIT');\n");
      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendJavaScript("   for (var x = 0; x < viewbox.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (x == 0)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         viewbox[x].checked = true;\n");
      appendJavaScript("         editbox[x].checked = true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else if (x == 1)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         viewbox[x].disabled = true;\n");
      appendJavaScript("         editbox[x].disabled = true;\n");
      appendJavaScript("         CCA(viewbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (arrViewable[x - 1] == 'FALSE')\n");
      appendJavaScript("            viewbox[x].disabled = true;\n");
      appendJavaScript("         if (arrEditable[x - 1] == 'FALSE')\n");
      appendJavaScript("            editbox[x].disabled = true;\n");
      appendJavaScript("         if (viewbox[x].checked)\n");
      appendJavaScript("            CCA(viewbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function initialiseCheckInDialog()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var checkinbox = getElements('_CHECKIN');\n");
      appendJavaScript("   if (checkinbox.length > 0)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var x = 0; x < checkinbox.length; x++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (x == 0 || arrNewDocument[x] == 'TRUE')\n");
      appendJavaScript("         {\n");
      appendJavaScript("            checkinbox[x].disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         if (arrCheckInable[x] == 'FALSE')\n");
      appendJavaScript("            checkinbox[x].disabled = true;\n");
      appendJavaScript("         CCA(checkinbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("   else\n");
      appendJavaScript("   {\n");
      appendJavaScript("      checkinbox.disabled = true;\n");
      appendJavaScript("      CCA(checkinbox, 1);\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");

      
      appendJavaScript("function initialiseUndoCheckOutDialog()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var doc_class = getElements('DOC_CLASS');\n");
      appendJavaScript("   var doc_no = getElements('DOC_NO');\n");
      appendJavaScript("   var doc_sheet = getElements('DOC_SHEET');\n");
      appendJavaScript("   var doc_rev = getElements('DOC_REV');\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   var unreservebox = getElements('_UNRESERVE');\n");
      appendJavaScript("   var deletebox = getElements('_DELETE');\n");
      appendJavaScript("   var delete_field = getElements('DELETE');\n");
      appendJavaScript("   var root = getDocumentProperty(doc_class[2].value, doc_no[2].value, doc_sheet[2].value, doc_rev[2].value, \"LOCAL_PATH\");\n");
      appendJavaScript("   var local_file_name;\n");
      appendJavaScript("   var local_path;\n");
      appendJavaScript("   var file;\n");
      appendJavaScript("   var found_file;\n");
      appendJavaScript("   for (var x = 0; x < unreservebox.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (x == 0)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         unreservebox[x].checked = true;\n");
      appendJavaScript("         deletebox[x].checked = true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (x == 1)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            unreservebox[x].disabled = true;\n");
      appendJavaScript("            deletebox[x].disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         found_file = false;\n");
      appendJavaScript("         if (arrCheckedOutToMe[x - 1] == \"TRUE\")\n");
      appendJavaScript("         {\n");
      appendJavaScript("            CCA(unreservebox[x], x + 1);\n");
      appendJavaScript("            local_file_name = getDocumentProperty(doc_class[x + 1].value, doc_no[x + 1].value, doc_sheet[x + 1].value, doc_rev[x + 1].value, \"LOCAL_FILE_NAME\");\n");
      appendJavaScript("            local_path = getDocumentProperty(doc_class[x + 1].value, doc_no[x + 1].value, doc_sheet[x + 1].value, doc_rev[x + 1].value, \"LOCAL_PATH\");\n");
      appendJavaScript("            if (local_path != \"\" && local_path.charAt(local_path.length - 1) != '\\\\')\n");
      appendJavaScript("               local_path = local_path + '\\\\';\n");
      appendJavaScript("            file = local_path + local_file_name;\n");
      appendJavaScript("            if (file != \"\" && document.form.oCliMgr.FileExists(file))\n");
      appendJavaScript("            {\n");
      appendJavaScript("               file_name[x + 1].value = file;\n");
      appendJavaScript("               found_file = true;\n");
      appendJavaScript("            }\n");
      appendJavaScript("            else \n");
      appendJavaScript("            {\n");
      appendJavaScript("               file_name[x + 1].value = \"" + mgr.translate("DOCMAWDOCSTRUCTUREFILENOTFOUND: <File not found>") + "\";\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else\n");
      appendJavaScript("         {\n");
      appendJavaScript("            unreservebox[x].disabled = true;\n");
      appendJavaScript("            if (root != \"\" && root.charAt(root.length - 1) != '\\\\')\n");
      appendJavaScript("               root = root + '\\\\';\n");
      appendJavaScript("            if (document.form.oCliMgr.FileExists(root + arrDocmanFileName[x - 1]))\n");
      appendJavaScript("            {\n");
      appendJavaScript("               file_name[x + 1].value = root + arrDocmanFileName[x - 1];\n");
      appendJavaScript("               found_file = true;\n");
      appendJavaScript("            }\n");
      appendJavaScript("            else if (document.form.oCliMgr.FileExists(root + arrOriginalFileName[x - 1]))\n");
      appendJavaScript("            {\n");
      appendJavaScript("               file_name[x + 1].value = root + arrOriginalFileName[x - 1];\n");
      appendJavaScript("               found_file = true;\n");
      appendJavaScript("            }\n");
      appendJavaScript("            else\n");
      appendJavaScript("            {\n");
      appendJavaScript("               if (arrRelativePath[x - 1] != \"\")\n");
      appendJavaScript("               {\n");
      appendJavaScript("                  local_path = root + \"\\\\\" + arrRelativePath[x - 1];\n");
      appendJavaScript("                  if (local_path.charAt(local_path.length - 1) != '\\\\')\n");
      appendJavaScript("                     local_path = local_path + '\\\\';\n");
      appendJavaScript("                  if (document.form.oCliMgr.FileExists(local_path + arrDocmanFileName[x - 1]))\n");
      appendJavaScript("                  {\n");
      appendJavaScript("                     file_name[x + 1].value = local_path + arrDocmanFileName[x - 1];\n");
      appendJavaScript("                     found_file = true;\n");
      appendJavaScript("                  }\n");
      appendJavaScript("                  else if (document.form.oCliMgr.FileExists(local_path + arrOriginalFileName[x - 1]))\n");
      appendJavaScript("                  {\n");
      appendJavaScript("                     file_name[x + 1].value = local_path + arrOriginalFileName[x - 1];\n");
      appendJavaScript("                     found_file = true;\n");
      appendJavaScript("                  }\n");
      appendJavaScript("               }\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("         if (!found_file)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            delete_field[x + 1].value = \"0\";\n");
      appendJavaScript("            deletebox[x].checked = false;\n");
      appendJavaScript("            deletebox[x].disabled = true;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else \n");
      appendJavaScript("            CCA(deletebox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function initialiseViewDialog()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendJavaScript("   for (var x = 0; x < viewbox.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (x == 0)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         viewbox[x].checked = true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else if (x == 1)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         viewbox[x].disabled = true;\n");
      appendJavaScript("         CCA(viewbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (arrViewable[x - 1] == 'FALSE')\n");
      appendJavaScript("            viewbox[x].disabled = true;\n");
      appendJavaScript("         if (viewbox[x].checked)\n");
      appendJavaScript("            CCA(viewbox[x], x + 1);\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function getOptionValue(group)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < group.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (group[x].checked)\n");
      appendJavaScript("          return group[x].value;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setOptionValue(group, value)\n");
      appendJavaScript("{\n");
      appendJavaScript("   for (var x = 0; x < group.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("       if (group[x].value == value)\n");
      appendJavaScript("          group[x].checked = true;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setDefaultCheckoutPath()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var checkout_path = document.form.oCliMgr.GetDocumentFolder();\n");
      //Bug 55611, Start, Set the local checkout path if it is empty
      appendJavaScript("if (!document.form.oCliMgr.FolderExists(checkout_path))\n");
      appendJavaScript("{\n");
      appendJavaScript("    checkout_path = document.form.oCliMgr.BrowseForLocalPath();\n");
      appendJavaScript("    if (!document.form.oCliMgr.FolderExists(checkout_path))\n");
      appendJavaScript("    {\n");
      appendJavaScript("       alert(\"" + mgr.translate("DOCMAWDOCSTRUCTOPRABORTED: Operation Aborted.") + "\");\n");
      appendJavaScript("       window.close();\n");
      appendJavaScript("    }\n");
      appendJavaScript("    else\n");
      appendJavaScript("       document.form.oCliMgr.SetDocumentFolder(checkout_path);\n");
      appendJavaScript("}\n");
      //Bug 55611, End
      appendJavaScript("   if (checkout_path.charAt(checkout_path.length - 1) != '\\\\')\n");
      appendJavaScript("      checkout_path = checkout_path + '\\\\';\n");
      appendJavaScript("   var file_action = document.form.FILE_ACTION.value;\n");
      appendJavaScript("   if (file_action == \"VIEW\" || file_action == \"PRINT\" || file_action == \"GETCOPYTODIR\" || file_action == \"SENDMAIL\" || file_action == \"VIEWWITHEXTVIEWER\" || file_action == \"VIEWCOPY\" || file_action == \"PRINTCOPY\")\n"); //Bug Id 73718
      appendJavaScript("      checkout_path += \"Temp\\\\\";\n");
      appendJavaScript("   document.form.CHECKOUT_PATH.value = checkout_path;\n");
      appendJavaScript("}\n");


      appendJavaScript("function setPath(useRelativePath)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var path = document.form.CHECKOUT_PATH.value;\n");
      appendJavaScript("   if (path == '')\n");
      appendJavaScript("      return;\n");
      appendJavaScript("   if (path.charAt(path.length - 1) != '\\\\')\n");
      appendJavaScript("      path = path + '\\\\';\n");
      appendJavaScript("   var file_path = getElements('FILE_PATH');\n");
      appendJavaScript("   var i = 0;\n");
      appendJavaScript("   var temp_path;\n");
      appendJavaScript("   for (var x = 2; x < file_path.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      temp_path = path;\n");
      appendJavaScript("      if (useRelativePath)\n");
      appendJavaScript("         temp_path = temp_path + arrRelativePath[x-2];\n");
      appendJavaScript("      if (temp_path.charAt(temp_path.length - 1) != '\\\\')\n");
      appendJavaScript("         temp_path = temp_path + '\\\\';\n");
      appendJavaScript("      file_path[x].value = temp_path;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setFileName()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var i = 0;\n");
      appendJavaScript("   var file_name_type = document.form.FILE_NAME_TYPE;\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   for (var x = 0; x < file_name.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (i >= 2)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (file_name_type[0].checked == true)\n");
      appendJavaScript("            file_name[x].value = arrDocmanFileName[i-2];\n");
      appendJavaScript("         else \n");
      appendJavaScript("            file_name[x].value = arrOriginalFileName[i-2];\n");
      appendJavaScript("      }\n");
      appendJavaScript("      i++;\n");
      appendJavaScript("   }\n");
      appendJavaScript("   if (file_name_type[0].checked == true && isEmpty(arrFileState[0]))\n");
      appendJavaScript("      setFieldReadOnly(file_name[2]);\n");
      appendJavaScript("   else if (isEmpty(arrFileState[0]))\n");
      appendJavaScript("      setFieldEditable(file_name[2]);\n");
      appendJavaScript("}\n");


      appendJavaScript("function setColumnReadOnly(column_name, selective, arrReadOnly)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var i = 0;\n");
      appendJavaScript("   var column = getElements(column_name);\n");
      appendJavaScript("   for (var x = 0; x < column.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (i >= 1)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if ((selective && arrReadOnly[i-1] == 'TRUE') || !selective)\n");
      appendJavaScript("         {\n");
      appendJavaScript("             setFieldReadOnly(column[x]);\n");
      appendJavaScript("             disableLOV(column[x].parentElement);\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("      i++;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setColumnEditable(column_name, selective, arrReadOnly)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var i = 0;\n");
      appendJavaScript("   var column = getElements(column_name);\n");
      appendJavaScript("   for (var x = 0; x < column.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (i >= 1)\n");
      appendJavaScript("      {\n");
      appendJavaScript("          if ((selective && arrReadOnly[i-1] != 'TRUE') || !selective)\n");
      appendJavaScript("          {\n");
      appendJavaScript("              setFieldEditable(column[x]);\n");
      appendJavaScript("          }\n");
      appendJavaScript("      }\n");
      appendJavaScript("      i++;\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function cancelLink()\n");
      appendJavaScript("{\n");
      appendJavaScript("   return false;\n");
      appendJavaScript("}\n");


      // ## TODO: modify to use getElements()
      appendJavaScript("function disableLOV(pElement)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var cElements = pElement.childNodes;\n");
      appendJavaScript("   for (var x = 0; x < cElements.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (cElements(x).tagName == \"A\" || cElements(x).tagName == \"a\")\n");
      appendJavaScript("      {\n");
      appendJavaScript("         cElements(x).disabled = true;\n");
      appendJavaScript("         cElements(x).onclick = cancelLink;\n");
      appendJavaScript("         var c2Elements = cElements(x).childNodes;\n");
      appendJavaScript("         for (var y = 0; y < c2Elements.length; y++)\n");
      appendJavaScript("         {\n");
      appendJavaScript("            if (c2Elements(y).tagName == \"IMG\" || c2Elements(y).tagName == \"img\")\n");
      appendJavaScript("            {\n");
      appendJavaScript("               c2Elements(y).onclick = cancelLink;\n");
      appendJavaScript("               c2Elements(y).disabled = true;\n");
      appendJavaScript("               return;\n");
      appendJavaScript("            }\n");
      appendJavaScript("         }\n");
      appendJavaScript("         return;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      // ## TODO: modify to use getElements()
      appendJavaScript("function setFolderStructure()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var cElements = pElement.childNodes;\n");
      appendJavaScript("   for (var x = 0; x < cElements.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (cElements(x).tagName == \"A\" || cElements(x).tagName == \"a\")\n");
      appendJavaScript("      {\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setCheckedOutFileName(arrCheckedOutToMe)\n");
      appendJavaScript("{\n");
      appendJavaScript("   var doc_class = getElements('DOC_CLASS');\n");
      appendJavaScript("   var doc_no = getElements('DOC_NO');\n");
      appendJavaScript("   var doc_sheet = getElements('DOC_SHEET');\n");
      appendJavaScript("   var doc_rev = getElements('DOC_REV');\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   var local_file_name;\n");
      appendJavaScript("   var local_path;\n");
      appendJavaScript("   var file;\n");
      appendJavaScript("   for (var x = 1; x < file_name.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (arrCheckedOutToMe[x-1] == \"TRUE\")\n");
      appendJavaScript("      {\n");
      appendJavaScript("         local_file_name = getDocumentProperty(doc_class[x].value, doc_no[x].value, doc_sheet[x].value, doc_rev[x].value, \"LOCAL_FILE_NAME\");\n");
      appendJavaScript("         local_path = getDocumentProperty(doc_class[x].value, doc_no[x].value, doc_sheet[x].value, doc_rev[x].value, \"LOCAL_PATH\");\n");
      appendJavaScript("         if (local_path != \"\" && local_path.charAt(local_path.length - 1) != '\\\\')\n");
      appendJavaScript("            local_path = local_path + '\\\\';\n");
      appendJavaScript("         file = local_path + local_file_name;\n");
      appendJavaScript("         if (file != \"\" && document.form.oCliMgr.FileExists(file))\n");
      appendJavaScript("         {\n");
      appendJavaScript("            file_name[x].value = file;\n");
      appendJavaScript("         }\n");
      appendJavaScript("         else \n");
      appendJavaScript("         {\n");
      appendJavaScript("            file_name[x].value = \"" + mgr.translate("DOCMAWDOCSTRUCTUREFILENOTFOUND: <File not found>") + "\";\n");
      appendJavaScript("         }\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function setLocalReadOnlyFileNames()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   var root = file_name[2].value.substring(0, file_name[2].value.lastIndexOf(\"\\\\\"));\n");
      appendJavaScript("   var local_path;\n");
      appendJavaScript("   for (var x = 2; x < file_name.length - 1; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (isEmpty(file_name[x + 1].value))\n");
      appendJavaScript("      {\n");
      appendJavaScript("         local_path = root + \"\\\\\" + arrRelativePath[x];\n");
      appendJavaScript("         if (local_path.charAt(local_path.length - 1) != \"\\\\\")\n");
      appendJavaScript("            local_path = local_path + \"\\\\\";\n");
      appendJavaScript("         if (document.form.oCliMgr.FileExists(local_path + arrDocmanFileName[x]))\n");
      appendJavaScript("            file_name[x + 1].value = local_path + arrDocmanFileName[x];\n");
      appendJavaScript("         else if (document.form.oCliMgr.FileExists(local_path + arrOriginalFileName[x]))\n");
      appendJavaScript("            file_name[x + 1].value = local_path + arrOriginalFileName[x];\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");


      appendJavaScript("function validateCheckInButton()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   if (isEmpty(file_name[1].value))\n");
      appendJavaScript("      enableExecuteButton(false);\n");
      appendJavaScript("}\n");


      appendJavaScript("function onClickConnectAddFileEventHandler()\n");
      appendJavaScript("{\n");
      appendJavaScript("   var connect_add = getElements('CONNECT_ADD');\n");
      appendJavaScript("   var file_name = getElements('FILE_NAME');\n");
      appendJavaScript("   var field = event.srcElement;\n");
      appendJavaScript("   for (var x = 0; x < connect_add.length; x++)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      if (field == connect_add[x])\n");
      appendJavaScript("      {\n");
      appendJavaScript("         deselectAllRows('__SELECTED1');\n");
      appendJavaScript("         for(var __x=0;__x<document.form.__SELECTED1.length;__x++)\n");
      appendJavaScript("            document.form.__SELECTED1[__x].value=\"\";\n");
      appendJavaScript("         if (document.form.__SELECTED1.type)\n");
      appendJavaScript("            document.form.__SELECTED1.value = x - 1;\n");
      appendJavaScript("         else\n");
      appendJavaScript("            document.form.__SELECTED1[x - 1].value = x - 1;\n");
      appendJavaScript("         if (file_name[x].value == \"\")\n");
      appendJavaScript("            executeServerCommand(\"connectFile\");\n");
      appendJavaScript("         else\n");
      appendJavaScript("            executeServerCommand(\"addSubLevelDocuments\");\n");
      appendJavaScript("         return;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");



      /*
       * checkFileExtensions
       * Function to validate the file extension.
       * Validate meaning check if the extension of the file is the extension of
       * the file type selected when doing a checkin.
       */
      appendJavaScript("function checkFileExtensionsOk()\n");
      appendJavaScript("{\n");
      //appendJavaScript("debugger;\n");
      appendJavaScript("   var fileNames = getElements('FILE_NAME');\n");
      appendJavaScript("   var selectdedFileTypes = getElements('FILE_TYPE');\n");
      appendJavaScript("   var checkInSelected = getElements('CHECKIN');\n");
      appendJavaScript("   var allFieldsOk = true;\n");
      appendJavaScript("   if (fileNames.length > 1)\n");
      appendJavaScript("   {\n");
      appendJavaScript("      for (var i=1; i < fileNames.length; i++)\n");
      appendJavaScript("      {\n");
      appendJavaScript("         if (arrFileRefExists[i - 1] != 'TRUE')\n");
      appendJavaScript("         {\n");
      appendJavaScript("            var currentFieldOk = false;\n");
      appendJavaScript("            for (var p=1; p < fileExtensions.length; p++)\n");
      appendJavaScript("            {\n");
      appendJavaScript("               if (fileNames[i].value.lastIndexOf('.') != -1 && fileNames[i].value.substring(fileNames[i].value.lastIndexOf('.') + 1, fileNames[i].value.length).toUpperCase() == fileExtensions[p].toUpperCase())\n"); // check if the file extension is of a know type.
      appendJavaScript("               {\n");
      appendJavaScript("                  if (selectdedFileTypes[i].value == fileTypes[p])\n"); // check if the file type selected matches the type of the file extension.
      appendJavaScript("                     currentFieldOk = true;\n");
      appendJavaScript("               }\n");
      appendJavaScript("            }\n");
      appendJavaScript("            if (currentFieldOk == false)\n");
      appendJavaScript("               allFieldsOk = false;\n");
      appendJavaScript("         }");
      appendJavaScript("      }\n");      
      appendJavaScript("      if (allFieldsOk == false)");
      appendJavaScript("      {");
      appendJavaScript("         var result = confirm('" + mgr.translate("DOCSTRUCTURECONFIRMFILEEXT: Some file types selected do not match the file extensions, this will result in the file extension being renamed. Continue?") + "');\n");
      appendJavaScript("         if (result)\n");
      appendJavaScript("            return true;\n");
      appendJavaScript("         else\n");
      appendJavaScript("            return false;\n");
      appendJavaScript("      }\n");
      appendJavaScript("      else\n");
      appendJavaScript("      {\n");
      appendJavaScript("      return true;\n");
      appendJavaScript("      }\n");
      appendJavaScript("   }\n");
      appendJavaScript("}\n");

      int n = 10;
      int [] arrname = new int [n];

   }


   private String getStringValue(String value)
   {
      ASPManager mgr = getASPManager();

      if (mgr.isEmpty(value))
         return "";
      else
         return value;
   }


   private void adjustCheckOut()
   {
      ASPManager mgr = getASPManager();

      // modify field attributes according to process..
      mgr.getASPField("VIEW").unsetHidden();
      mgr.getASPField("DOC_CLASS").setReadOnly();
      mgr.getASPField("DOC_NO").setReadOnly();
      mgr.getASPField("DOC_SHEET").setReadOnly();
      mgr.getASPField("DOC_REV").setReadOnly();
      mgr.getASPField("TITLE").setReadOnly();
      mgr.getASPField("FILE_NAME").setReadOnly();
      mgr.getASPField("FILE_STATE").setHidden();
      mgr.getASPField("NUM_COUNT_ID1").setHidden();
      mgr.getASPField("NUM_COUNT_ID2").setHidden();
      mgr.getASPField("BOOKING_LIST").setHidden();      
      mgr.getASPField("FILE_TYPE").setHidden();


      // disable commands that are not applicable to this process..
      structurebar.disableCommand("addSubLevelDocuments");
      structurebar.disableCommand("connectFile");
      structurebar.disableCommand("deleteRow");
   }


   private String getMacroAttributes()
   {
      StringBuffer attrs = new StringBuffer();
      firstRow();

      // Get the root path where files will be checked out to
      // or checked in from..
      if ("CHECKIN".equals(file_action))
         DocmawUtil.addToAttribute(attrs, "CHECK_IN_PATH", DocmawUtil.getPath(structureset.getValue("FILE_NAME")));
      else if ("UNDOCHECKOUT".equals(file_action))
         DocmawUtil.addToAttribute(attrs, "CHECK_OUT_PATH", DocmawUtil.getPath(structureset.getValue("FILE_NAME")));
      else         
         DocmawUtil.addToAttribute(attrs, "CHECK_OUT_PATH", DocmawUtil.getPath(getCheckOutFileName()));


      // Get list of file names that will be checked out.. 
      int count = 1;
      do
      {
         // Perform action only on the selected documents..
         if (currentRowSelected())
         {
            String file_name;
            if ("CHECKIN".equals(file_action) || "UNDOCHECKOUT".equals(file_action))
               file_name = structureset.getValue("FILE_NAME");
            else         
               file_name = getCheckOutFileName();

            DocmawUtil.addToAttribute(attrs, "LOCAL_FILE_" + count++, file_name);
         }
      }
      while (structureset.next());

      DocmawUtil.addToAttribute(attrs, "LOCAL_FILE_COUNT", new Integer(count - 1).toString());

      return attrs.toString();
   }


   private void firstRow()
   {
      if ("CHECKIN".equals(file_action))
      {
         structureset.goTo(0);
      }      
      else
      {
         // To skip the control row..
         structureset.goTo(1);
      }
   }


   private boolean currentRowSelected()
   {
      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action) || 
          "VIEW".equals(file_action) || "PRINT".equals(file_action) || "VIEWCOPY".equals(file_action) || "PRINTCOPY".equals(file_action)) //Bug Id 73718
      {
         return "1".equals(structureset.getValue("VIEW"));
      }
      else if ("CHECKIN".equals(file_action))
      {
         return "1".equals(structureset.getValue("CHECKIN"));
      }
      else if ("UNDOCHECKOUT".equals(file_action))
      {
         return "1".equals(structureset.getValue("UNRESERVE"));         
      }

      return false;
   }


   private void generateCheckOutScript() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendDirtyJavaScript("var arrViewable = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrRelativePath = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrDocmanFileName = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrOriginalFileName = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrFileState = new Array(" + (structureset.countRows() - 1) + ");\n");

      structureset.goTo(1);
      for (int x = 0; x < structureset.countRows() - 1; x++)
      {
         appendDirtyJavaScript("arrViewable[" + x + "] = '" + mgr.encodeStringForJavascript(structureset.getValue("VIEW_ACCESS")) + "';\n"); //Bug Id 68773
         appendDirtyJavaScript("arrRelativePath[" + x + "] = '" + getStringValue((structureset.getValue("RELATIVE_PATH"))).replaceAll("\\\\", "\\\\\\\\") + "';\n");
         appendDirtyJavaScript("arrDocmanFileName[" + x + "] = '" + getStringValue(structureset.getValue("DOCMAN_FILE_NAME")).replaceAll("\'","\\\\'") + "';\n");
         appendDirtyJavaScript("arrOriginalFileName[" + x + "] = '" + getStringValue(structureset.getValue("ORIGINAL_FILE_NAME")) + "';\n");
         appendDirtyJavaScript("arrFileState[" + x + "] = '" + getStringValue(structureset.getValue("FILE_STATE")) + "';\n");
         structureset.next();
      }

      // If the checkout path is empty, then set a
      // default path and initialise all rows..
      if (mgr.isEmpty(mgr.readValue("CHECKOUT_PATH")))
      {
         appendDirtyJavaScript("setDefaultCheckoutPath();\n");
         appendDirtyJavaScript("setCheckOutPath();\n");
      }


      // Initialise file names with either the original file
      // name or the docman file name..
      appendDirtyJavaScript("setFileName();\n");
   }


   private void adjustEdit()
   {
      ASPManager mgr = getASPManager();

      adjustCheckOut();

      // modify field attributes according to process..
      mgr.getASPField("EDIT").unsetHidden();
   }


   private void generateEditScript() throws FndException
   {
      ASPManager mgr = getASPManager();

      generateCheckOutScript();

      appendDirtyJavaScript("var arrEditable = new Array(" + (structureset.countRows() - 1) + ");\n");

      structureset.goTo(1);
      for (int x = 0; x < structureset.countRows() - 1; x++)
      {
         appendDirtyJavaScript("arrEditable[" + x + "] = '" + (("TRUE".equals(structureset.getValue("EDIT_ACCESS")) && !mgr.isEmpty(structureset.getValue("FILE_STATE"))) ? "TRUE" : "FALSE") + "';\n");
         structureset.next();
      }

      appendDirtyJavaScript("initialiseEditDialog();\n");
   }


   private void adjustCheckIn()
   {
      ASPManager mgr = getASPManager();

      // modify field attributes according to process..
      mgr.getASPField("CONNECT_ADD").unsetHidden();
      mgr.getASPField("CHECKIN").unsetHidden();      
      mgr.getASPField("FILE_PATH").setHidden();
      mgr.getASPField("FILE_NAME").setReadOnly();

      // disable commands that are not applicable to this process..
      structurebar.disableCommand("resetCheckboxes");

      // disable actions in single row mode..
      if (structurelay.isSingleLayout())
      {
         structurebar.disableCommand("addSubLevelDocuments");
         structurebar.disableCommand("connectFile");
         structurebar.disableCommand("deleteRow");
      }
   }


   private void generateCheckInScript() throws FndException
   {
      ASPManager mgr = getASPManager();
      boolean allRowsHaveFileRefs = true;

      appendDirtyJavaScript("var arrReadOnly = new Array(" + structureset.countRows() + ");\n");
      appendDirtyJavaScript("var arrCheckedOutToMe = new Array(" + structureset.countRows() + ");\n");
      appendDirtyJavaScript("var arrCheckInable = new Array(" + structureset.countRows() + ");\n");
      appendDirtyJavaScript("var arrNewDocument = new Array(" + structureset.countRows() + ");\n");
      appendDirtyJavaScript("var arrFileRefExists = new Array(" + structureset.countRows() + ");\n");

      structureset.first();
      for (int x = 0; x < structureset.countRows(); x++)
      {
         appendDirtyJavaScript("arrReadOnly[" + x + "] = '" + ("TRUE".equals(structureset.getRow().getValue("NEW_DOCUMENT")) ? "FALSE" : "TRUE") + "';\n");
         appendDirtyJavaScript("arrCheckedOutToMe[" + x + "] = '" + mgr.encodeStringForJavascript(structureset.getRow().getValue("CHECKED_OUT_TO_ME")) + "';\n");//Bug Id 68773
         appendDirtyJavaScript("arrCheckInable[" + x + "] = '" + (("TRUE".equals(structureset.getRow().getValue("CHECK_IN_ACCESS")) && (!mgr.isEmpty(structureset.getRow().getValue("FILE_STATE")) || !mgr.isEmpty(structureset.getRow().getValue("FILE_NAME"))) ||
                                                                   // CHECK_IN_ACCESS is empty for new documents..
                                                                   mgr.isEmpty(structureset.getRow().getValue("CHECK_IN_ACCESS"))) ? "TRUE" : "FALSE") + "';\n");
         appendDirtyJavaScript("arrNewDocument[" + x + "] = '" + mgr.encodeStringForJavascript(structureset.getRow().getValue("NEW_DOCUMENT")) + "';\n");//Bug Id 68773
         appendDirtyJavaScript("arrFileRefExists[" + x + "] = '" + mgr.encodeStringForJavascript(structureset.getRow().getValue("ORIGINAL_REF_EXIST")) + "';\n");// Bug Id 68773

         //if (structureset.getRow().getValue("ORIGINAL_REF_EXIST").equalsIgnoreCase("FALSE"))

         structureset.next();
      }

      appendDirtyJavaScript("setColumnReadOnly('DOC_CLASS', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('DOC_NO', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('DOC_SHEET', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('DOC_REV', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('TITLE', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('NUM_COUNT_ID2', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('BOOKING_LIST', true, arrReadOnly);\n");
      appendDirtyJavaScript("setColumnReadOnly('FILE_TYPE', true, arrFileRefExists);\n");
      appendDirtyJavaScript("setCheckedOutFileName(arrCheckedOutToMe);\n");
      appendDirtyJavaScript("validateCheckInButton();\n");


      structureset.first();
      if ("TRUE".equals(structureset.getValue("CHECK_IN_ACCESS")))
      {
         // set events and properties for the "[Connect/Add]" column..
         appendDirtyJavaScript("setFieldEvent(\"CONNECT_ADD\", \"onclick\", \"onClickConnectAddFileEventHandler\");\n");
         appendDirtyJavaScript("setFieldProperty(\"CONNECT_ADD\", \"style.color\", \"blue\");\n");      
         appendDirtyJavaScript("setFieldEventProperty(\"CONNECT_ADD\", \"onmouseover\", \"style.cursor='hand'\");\n");
      }
      else
      {
         appendDirtyJavaScript("setFieldProperty(\"CONNECT_ADD\", \"style.color\", \"gray\");\n");      
      }
      // initialise the Check In box in the checkin dialog..
      appendDirtyJavaScript("initialiseCheckInDialog()\n");
      // 
      // Create and initialize the file types array
      //
      //Function to initialize the file types.
      String file_types = getFileTypes();
      StringTokenizer st = new StringTokenizer(file_types, "^");

      // Function to initialise the fileTypes array with value.
      appendDirtyJavaScript("function initFileTypes()\n");
      appendDirtyJavaScript("{\n");
      int array_index = 0;
      while (st.hasMoreTokens())
      {
         String token = st.nextToken();
         appendDirtyJavaScript("   fileTypes[" + array_index++ + "] = '" + token + "';\n");
         st.nextToken(); // skip the extension.
      }
      appendDirtyJavaScript("}\n");

      // Array to hold the file types.
      appendDirtyJavaScript("var fileTypes = new Array(" + array_index + ");\n");

      StringTokenizer stext = new StringTokenizer(file_types, "^");
      appendDirtyJavaScript("function initFileExtensions()\n");
      appendDirtyJavaScript("{\n");
      array_index = 0;
      while (stext.hasMoreTokens())
      {
         stext.nextToken(); // skip the filetype.
         String token = stext.nextToken();
         appendDirtyJavaScript("   fileExtensions[" + array_index++ + "] = '" + token + "';\n");
      }
      appendDirtyJavaScript("}\n");

      // Array to hold the file extensions.
      appendDirtyJavaScript("var fileExtensions = new Array(" + array_index + ");\n");

      // Early calls to init (just in case...:))
      appendDirtyJavaScript("initFileTypes();\n");
      appendDirtyJavaScript("initFileExtensions();\n");

      // initialise the checkin button by validating all the file types (this will disable the checkin button).
      appendDirtyJavaScript("validateAllFileTypes(1)\n");
   }


   private void adjustUndoCheckOut()
   {
      ASPManager mgr = getASPManager();

      // modify field attributes according to process..
      mgr.getASPField("UNRESERVE").unsetHidden();
      mgr.getASPField("DELETE").unsetHidden();
      mgr.getASPField("DOC_CLASS").setReadOnly();
      mgr.getASPField("DOC_NO").setReadOnly();
      mgr.getASPField("DOC_SHEET").setReadOnly();
      mgr.getASPField("DOC_REV").setReadOnly();
      mgr.getASPField("TITLE").setReadOnly();
      mgr.getASPField("FILE_NAME").setReadOnly();
      mgr.getASPField("FILE_PATH").setHidden();
      mgr.getASPField("NUM_COUNT_ID1").setHidden();
      mgr.getASPField("NUM_COUNT_ID2").setHidden();
      mgr.getASPField("BOOKING_LIST").setHidden();
      mgr.getASPField("FILE_TYPE").setHidden();


      // disable commands that are not applicable to this process..
      structurebar.disableCommand("addSubLevelDocuments");
      structurebar.disableCommand("connectFile");
      structurebar.disableCommand("deleteRow");
      structurebar.disableCommand("resetCheckboxes");


      // change the command bar title..
      structureblk.setTitle(mgr.translate("DOCMAWDOCSTRUCTURELABLEUNRESERVEANDDELETE: The following documents will be unreserved and their files deleted"));

   }


   private void adjustView()
   {
      ASPManager mgr = getASPManager();

      adjustCheckOut();
   }


   private void generateViewScript() throws FndException
   {
      ASPManager mgr = getASPManager();

      generateCheckOutScript();
      appendDirtyJavaScript("initialiseViewDialog();\n");
   }


   private void adjustPrint()
   {
      // Same as View..
      adjustView();
   }


   private void generatePrintScript() throws FndException
   {
      // Same as View..
      generateViewScript();
   }


   private void adjustCopy()
   {
      // Same as View..
      adjustView();
   }


   private void adjustSendMail()
   {
      // Same as View..
      adjustView();
   }

   private void adjustViewWithExternalViewer()
   {
      // Same as View..
      adjustView();
   }


   private void generateCopyScript() throws FndException
   {
      // Same as View..
      generateViewScript();
   }


   private void generateSendMailScript() throws FndException
   {
      // Same as View..
      generateViewScript();
      appendDirtyJavaScript("document.form.LOCAL_CHECK_OUT_PATH.value = document.form.oCliMgr.GetDocumentFolder();\n");
   }


   private void generateViewWithExternalViewerScript() throws FndException
   {
      // Same as View..
      generateViewScript();
   }


   private void generateUndoCheckOutScript() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendDirtyJavaScript("var arrCheckedOutToMe = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrDocmanFileName = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrOriginalFileName = new Array(" + (structureset.countRows() - 1) + ");\n");
      appendDirtyJavaScript("var arrRelativePath = new Array(" + (structureset.countRows() - 1) + ");\n");


      structureset.goTo(1);
      for (int x = 0; x < structureset.countRows() - 1; x++)
      {
         appendDirtyJavaScript("arrCheckedOutToMe[" + x + "] = '" + mgr.encodeStringForJavascript(structureset.getRow().getValue("CHECKED_OUT_TO_ME")) + "';\n");//Bug Id 68773
         appendDirtyJavaScript("arrDocmanFileName[" + x + "] = '" + getStringValue(structureset.getValue("DOCMAN_FILE_NAME")) + "';\n");
         appendDirtyJavaScript("arrOriginalFileName[" + x + "] = '" + getStringValue(structureset.getValue("ORIGINAL_FILE_NAME")) + "';\n");
         appendDirtyJavaScript("arrRelativePath[" + x + "] = '" + getStringValue((structureset.getValue("RELATIVE_PATH"))).replaceAll("\\\\", "\\\\\\\\") + "';\n");

         structureset.next();
      }

      appendDirtyJavaScript("initialiseUndoCheckOutDialog();\n");
      appendDirtyJavaScript("setLocalReadOnlyFileNames();\n");
   }


   public void adjust() throws FndException
   {
      ASPManager mgr = getASPManager();

      // adjust depending on the process..
      if ("CHECKOUT".equals(file_action) || "CREATENEW".equals(file_action))
      {
         adjustEdit();
         generateEditScript();
      }
      else if ("CHECKIN".equals(file_action))
      {
         adjustCheckIn();
         generateCheckInScript();
      }
      else if ("UNDOCHECKOUT".equals(file_action))
      {
         adjustUndoCheckOut();
         generateUndoCheckOutScript();
      }
      else if ("VIEW".equals(file_action))
      {
         adjustView();
         generateViewScript();
      }
      //Bug Id 73718, start
      else if ("VIEWCOPY".equals(file_action))
      {
         adjustView();
         generateViewScript();
      }
      else if ("PRINTCOPY".equals(file_action))
      {
         adjustPrint();
         generatePrintScript();
      }
      //Bug Id 73718, end
      else if ("PRINT".equals(file_action))
      {
         adjustPrint();
         generatePrintScript();
      }
      else if ("GETCOPYTODIR".equals(file_action))
      {
         adjustCopy();
         generateCopyScript();
      }
      else if ("SENDMAIL".equals(file_action))
      {
         adjustSendMail();
         generateSendMailScript();
      }
      else if ("VIEWWITHEXTVIEWER".equals(file_action))
      {
         adjustViewWithExternalViewer();
         generateViewWithExternalViewerScript();
      }
   }


   public String drawRadio(String label, String name, String value, boolean checked, String tag)
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      if (!mgr.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
      
      return "<input class=radioButton type=radio name=\"" + name + "\" value=\"" + value + "\"" + (checked ? " CHECKED " : "") + " " + (tag == null ? "" : tag) + ">&nbsp;<span OnClick=\"showHelpTag('"+usage_id+"')\"><font class=normalTextValue>" + mgr.translate(label) + "</font></span>";
   }


   private String drawLabel( String label)
   {
      ASPManager mgr = getASPManager();
      String usage_id ="";
      int i = 0;
      
      if (!mgr.isEmpty(label))
         i = label.indexOf(":");
      if (i>0)
      { 
         String tr_constant = label.substring(0,i);         
         usage_id = mgr.getASPConfig().getUsageVersionID(mgr.getLanguageCode().toUpperCase(),tr_constant);
      }
         
      return "<span OnClick=\"showHelpTag('"+usage_id+"')\"><font class=\"normalTextValue\">&nbsp;" + mgr.translate(label) + "</font></span>";
   }


   private void drawCheckOutUI(AutoString out)
   {
      ASPManager mgr = getASPManager();


      // Outer table
      out.append("<table border=0 width=75% class=\"BlockLayoutTable\">");
      out.append("<tr><td valign=top>");

      // Options for naming files..
      String file_name_type = ctx.readValue("FILE_NAME_TYPE");
      out.append("<table border=0 width=100%>");
      out.append("<tr><td>");
      out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCSTRUCTURELABLENAMEFILESUSING: Name Files Using")));
      out.append("</td></tr>");
      out.append("<tr><td>");
      out.append(drawRadio("DOCMAWDOCSTRUCTURELABLEDOCTITLEANDNAME: Document Title and Name", "FILE_NAME_TYPE", "DOCMAN_FILE_NAME", "DOCMAN_FILE_NAME".equals(file_name_type), "onClick=\"javascript:setFileName();\""));
      out.append("</td></tr>");
      out.append("<tr><td>");
      out.append(drawRadio("DOCMAWDOCSTRUCTURELABLEORIGFILENAME: Original File Name", "FILE_NAME_TYPE", "USER_FILE_NAME", "USER_FILE_NAME".equals(file_name_type), "onClick=\"javascript:setFileName();\""));
      out.append("</td></tr>");
      out.append("</table>");


      // Miscellaneous options..
      out.append("</td><td valign=top>");
      String original_folder_structure = ctx.readValue("ORIGINAL_FOLDER_STRUCTURE");
      out.append("<table valign=top border=0 width=100%>");
      out.append("<tr><td>");
      out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCSTRUCTURELABLEMISCOPTIONS: Misc. Options")));
      out.append("</td></tr>");
      out.append("<tr><td>");
      out.append(fmt.drawCheckbox("ORIGINAL_FOLDER_STRUCTURE", "TRUE", "TRUE".equals(original_folder_structure), "onClick=\"javascript:setCheckOutPath();\""));
      out.append(drawLabel("DOCMAWDOCSTRUCTURELABELUSEORGFLDSTR: Use Original Folder Structure"));
      out.append("</td></tr>");
      out.append("<tr><td>");
      out.append("</td></tr>");
      out.append("</table>");


      if (!"PRINT".equals(file_action) && !"GETCOPYTODIR".equals(file_action) && !"SENDMAIL".equals(file_action) && !"VIEWWITHEXTVIEWER".equals(file_action))
      {
         // Options for what actions to take after checking out..
         out.append("</td><td valign=top >");
         String post_check_out_action = ctx.readValue("POST_CHECK_OUT_ACTION");
         out.append("<table border=0 width=100%>");
         out.append("<tr><td>");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCSTRUCTURELABLEAFTERCHECKOUT: After Check Out")));
         out.append("</td></tr>");
         out.append("<tr><td>");
         out.append(drawRadio("DOCMAWDOCSTRUCTURELABLEOPENDOCUMENT: Open Document / Run Macro", "POST_CHECK_OUT_ACTION", "LAUNCH_OR_RUN_MACRO", "LAUNCH_OR_RUN_MACRO".equals(post_check_out_action), "onClick=\"\""));
         out.append("</td></tr>");
         out.append("<tr><td>");
         out.append(drawRadio("DOCMAWDOCSTRUCTURELABLEOPENFOLDER: Open Folder", "POST_CHECK_OUT_ACTION", "OPEN_FOLDER", "OPEN_FOLDER".equals(post_check_out_action), "onClick=\"\""));
         out.append("</td></tr>");
         out.append("<tr><td>");
         out.append(drawRadio("DOCMAWDOCSTRUCTURELABLEDONOTHING: Do Nothing", "POST_CHECK_OUT_ACTION", "DO_NOTHING", "DO_NOTHING".equals(post_check_out_action), "onClick=\"\""));
         out.append("</td></tr>");
         out.append("</table>");
      }


      out.append("</td></tr>");
      out.append("<tr><td height=10>");
      out.append("</td></tr>");
      out.append("<tr><td width=30% colspan=3>");

      // Target path for checking out files to..
      String checkout_path = ctx.readValue("CHECKOUT_PATH");
      out.append("<table align=left valign=top border=0 width=100%>");
      out.append("<tr><td>");
      out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCSTRUCTURELABLECHECKOUTFOLDER: Checkout Folder")));
      out.append("</td></tr>");
      out.append("<tr><td>");
      out.append(fmt.drawTextField("CHECKOUT_PATH", checkout_path, "onChange=\"javascript:setCheckOutPath();\"", 40, 1000));
      out.append(fmt.drawButton("BROWSE_FOLDER", mgr.translate("DOCMAWDOCSTRUCTUREBROWSE: Browse..."),"OnClick=\"javascript:showFolderBrowser();\""));
      out.append("</td></tr>");
      out.append("</table>");

      out.append("</td></tr>");
      out.append("</table>");
   }


   private void drawEditUI(AutoString out)
   {
      drawCheckOutUI(out);
   }


   private void drawCheckInUI(AutoString out)
   {
      ASPManager mgr = getASPManager();

      out.append("<input type=\"hidden\" name=\"FILES_SELECTED\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PATH\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DELETE_CONFIRMED\" value=\"FALSE\">\n");
   }


   private void drawUndoCheckOutUI(AutoString out)
   {
   }


   private void drawViewUI(AutoString out)
   {
      drawCheckOutUI(out);
   }


   private void drawPrintUI(AutoString out)
   {
      // Same as View..
      drawViewUI(out);
   }


   private void drawCopyUI(AutoString out)
   {
      // Same as View..
      drawViewUI(out);
   }


   private void drawSendMailUI(AutoString out)
   {
      // Same as View..
      drawViewUI(out);
   }


   private void drawViewWithExternalViewerUI(AutoString out)
   {
      // Same as View..
      drawViewUI(out);
   }


   private void drawControlButtons(AutoString out)
   {
      ASPManager mgr = getASPManager();
      String execute_lable = null;
      String execute_action = null;

      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEEDIT: Edit");
         execute_action = "editStructure";
      }
      else if ("CHECKIN".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTECHECKIN: Check In");
         execute_action = "checkInStructure";
      }
      else if ("UNDOCHECKOUT".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEUNDOCHECKOUT: Undo Check Out");
         execute_action = "undoCheckOutStructure";
      }
      else if ("VIEW".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEVIEW: View");
         execute_action = "viewStructure";
      }
      //Bug Id 73718, start
      else if ("VIEWCOPY".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEVIEWCOPY: View Copy");
         execute_action = "viewStructure";
      }
      else if ("PRINTCOPY".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEPRINTCOPY: Print View Copy");
         execute_action = "printStructure";
      }
      //Bug Id 73718, end

      else if ("PRINT".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEPRINT: Print");
         execute_action = "printStructure";
      }
      else if ("GETCOPYTODIR".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTECOPY: Copy");
         execute_action = "copyStructure";
      }
      else if ("SENDMAIL".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTESENDMAIL: Send by E-Mail");
         execute_action = "sendStructureByMail";
      }
      else if ("VIEWWITHEXTVIEWER".equals(file_action))
      {
         execute_lable = mgr.translate("DOCMAWDOCSTRUCTUREEXECUTEVIEWWITHEXTVIEWER: View");
         execute_action = "viewStructureWithExternalViewer";
      }


      out.append("<table border=0 cellspacing=20 align=right>");
      out.append("<tr><td>");
      out.append(fmt.drawButton("EXECUTE", execute_lable, "onClick=\"executeServerCommand('" + execute_action + "')\""));
      out.append("</td><td>");
      out.append(fmt.drawButton("CANCEL", mgr.translate("DOCMAWDOCSTRUCTURECANCEL: Cancel"), "onClick='window.close()'"));
      out.append("</td></tr>");
      out.append("</table>");
   }


   private void drawHeadLayout(AutoString out)
   {
      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action))
      {
         drawEditUI(out);
      }
      else if ("CHECKIN".equals(file_action))
      {
         drawCheckInUI(out);
      }
      else if ("UNDOCHECKOUT".equals(file_action))
      {
         drawUndoCheckOutUI(out);
      }
      else if ("VIEW".equals(file_action))
      {
         drawViewUI(out);
      }

      //Bug Id 73718, start
      else if ("VIEWCOPY".equals(file_action))
      {
         drawViewUI(out);
      }
      else if ("PRINTCOPY".equals(file_action))
      {
         drawPrintUI(out);
      }
      //Bug Id 73718, end

      else if ("PRINT".equals(file_action))
      {
         drawPrintUI(out);
      }
      else if ("GETCOPYTODIR".equals(file_action))
      {
         drawCopyUI(out);
      }
      else if ("SENDMAIL".equals(file_action))
      {
         drawSendMailUI(out);
      }
      else if ("VIEWWITHEXTVIEWER".equals(file_action))
      {
         drawViewWithExternalViewerUI(out);
      }
   }


   private void initialiseUI()
   {
      ASPManager mgr = getASPManager();

      if ("CREATENEW".equals(file_action) || "CHECKOUT".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREEDITSTRUCTURE: Edit Document Structure");
      }
      else if ("CHECKIN".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTURECHECKINSTRUCTURE: Check In Document Structure");
      }
      else if ("UNDOCHECKOUT".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREUNDOCHECKOUTSTRUCTURE: Undo Check Out Document Structure");
      }
      else if ("VIEW".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREVIEWSTRUCTURE: View Document Structure");
      }

      //Bug Id 73718, start
      else if ("VIEWCOPY".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREVIEWCOPYSTRUCTURE: View Copy Document Structure");
      }

      else if ("PRINTCOPY".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREPRINTCOPYSTRUCTURE: Print View Copy Document Structure");
      }
      //Bug Id 73718, end

      else if ("PRINT".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREPRINTSTRUCTURE: Print Document Structure");
      }
      else if ("GETCOPYTODIR".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTURECOPYSTRUCTURE: Copy Document Structure");
      }
      else if ("SENDMAIL".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTURESENDSTRUCTUREBYMAIL: Send Structure by E-Mail");
      }
      else if ("VIEWWITHEXTVIEWER".equals(file_action))
      {
         page_title = mgr.translate("DOCMAWDOCSTRUCTUREVIEWSTRUCTUREWITHEXTERNALVIEWER: View Structure with External Viewer");
      }
   }


   protected String getTitle()
   {
      return page_title;
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      // generate UI..
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getTitle()));
      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(page_title));

      out.append(structurebar.showBar());

      beginDataPresentation();

      // draw the header and the table..
      out.append("<table border=0 cellspacing=0 cellpadding=0 width=100% class=\"BlockLayoutTable\"><tr><td>");
      out.append("<tr><td>");
      drawHeadLayout(out);
      out.append("</td></tr>");

      // draw table of documents in structure
      out.append("<tr><td>");
      out.append(structurelay.generateDataPresentation());
      out.append("</td></tr>");
      out.append("</table>");


      // draw dotted line after table
      endDataPresentation(true);

      drawControlButtons(out);
      out.append("<p>&nbsp;</p>");

      // hidden fields
      out.append("<input type=\"hidden\" name=\"SAVE_TABLE_DATA\" value=\"TRUE\">\n");
      out.append("<input type=\"hidden\" name=\"FILE_ACTION\" value=\"" + file_action + "\">\n");
      out.append("<input type=\"hidden\" name=\"LOCAL_CHECK_OUT_PATH\" value=\"\">\n");
      out.append(mgr.endPresentation());
      out.append(DocmawUtil.getClientMgrObjectStr());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      
      // Function to validate the file types [Moved here from getContents due to a frmawork problem call ID 125670] 
      // The validateFileTpes function checks if all the file types entered are valid.
      // If not it disables the checkin button.
      appendDirtyJavaScript("function validateAllFileTypes(i) \n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("initFileTypes();\n");
      appendDirtyJavaScript("var fields = getElements('FILE_TYPE');\n");
      appendDirtyJavaScript("var fld; \n"); // store a field temporarily
      appendDirtyJavaScript("var checkInSelected = getElements('CHECKIN');\n");
      appendDirtyJavaScript("var allFieldsOk = true;\n");
      appendDirtyJavaScript("var currentFieldOk = false;\n");
      appendDirtyJavaScript("   if (fields.length != 0)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      for (var i=1; i < fields.length; i++)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         fld = getField_('FILE_TYPE',i);\n"); // Set the field to upper case. Though this should be handled
      appendDirtyJavaScript("         fld.value = fld.value.toUpperCase();\n"); // by the framework, a limitation comes into play when you use set validate function.
      appendDirtyJavaScript("            currentFieldOk = false;\n");
      appendDirtyJavaScript("            if (i == 1 && checkInSelected[i].value == '')\n"); // If no files are connected to the first row
      appendDirtyJavaScript("            {\n");
      appendDirtyJavaScript("                allFieldsOk = false;\n");
      appendDirtyJavaScript("            }\n");
      appendDirtyJavaScript("            else if (checkInSelected[i].value == '1')\n"); // If this row is to be checked in
      appendDirtyJavaScript("            {\n");
      appendDirtyJavaScript("               for (var p=0; p < fileTypes.length; p++)\n");
      appendDirtyJavaScript("                  if (fields[i].value == fileTypes[p])\n");
      appendDirtyJavaScript("                     currentFieldOk = true;\n");
      appendDirtyJavaScript("               if(arrFileRefExists[i - 1] == 'TRUE')\n"); // this means that its not a first time checkin
      appendDirtyJavaScript("                  currentFieldOk = true;\n");
      appendDirtyJavaScript("\n");
      appendDirtyJavaScript("            if (currentFieldOk == false)\n");
      appendDirtyJavaScript("               allFieldsOk = false;\n");
      appendDirtyJavaScript("            }\n");      
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("   }\n");           
      // DIKALK - This block seems unnecessary??      
      // SUKMLK - Looks like he's right :( (this time). But lets let this code be for a while,
      //          something "strange" happened when I removed it.
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");                              
      appendDirtyJavaScript("      var allFieldsOk = false;\n");
      appendDirtyJavaScript("      for (var i=0; i < fileTypes.length; i++)\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            if (fields.value == fileTypes[i].value && arrFileRefExists[0] != 'TRUE' && checkInSelected.value == '1')\n");
      appendDirtyJavaScript("               allFieldsOk = true;\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("if (allFieldsOk)\n");
      appendDirtyJavaScript("   enableExecuteButton(true);\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("   enableExecuteButton(false);\n");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("function validateSelect(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var file_action = document.form.FILE_ACTION.value;\n");
      appendDirtyJavaScript("   if (file_action == \"CHECKOUT\" || file_action == \"CREATENEW\")\n");
      appendDirtyJavaScript("      validateSelectOnEdit(i);\n");
      appendDirtyJavaScript("   else if (file_action == \"VIEW\" || file_action == \"PRINT\" || file_action == \"GETCOPYTODIR\" || file_action == \"SENDMAIL\" || file_action == \"VIEWCOPY\" || file_action == \"PRINTCOPY\")\n"); //Bug Id 73718
      appendDirtyJavaScript("      validateSelectOnView(i);\n");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("function validateEdit(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var editbox = getElements('_EDIT');\n");
      appendDirtyJavaScript("   var viewbox = getElements('_VIEW');\n");
      appendDirtyJavaScript("   var edit    = getElements('EDIT');\n");
      appendDirtyJavaScript("   var view    = getElements('VIEW');\n");
      appendDirtyJavaScript("   var checked = editbox[i - 1].checked;\n");
      appendDirtyJavaScript("   var value   = checked ? \"1\" : \"0\";\n");
      appendDirtyJavaScript("   if (i == 1)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      for (var x = 0; x < editbox.length; x++)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         if (!editbox[x].disabled)\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            editbox[x].checked = checked;\n");
      appendDirtyJavaScript("            edit[x + 1].value = value;\n");
      appendDirtyJavaScript("            if (checked || viewbox[x].disabled)\n");
      appendDirtyJavaScript("            {\n");
      appendDirtyJavaScript("               viewbox[x].checked = checked;\n");
      appendDirtyJavaScript("               view[x + 1].value = value;\n");
      appendDirtyJavaScript("            }\n");
      appendDirtyJavaScript("            if (x > 0)\n");
      appendDirtyJavaScript("               CCA(viewbox[x], x + 1);\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      edit[i].value = value;\n");
      appendDirtyJavaScript("      if (checked || viewbox[i - 1].disabled)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         view[i].value = value;\n");
      appendDirtyJavaScript("         viewbox[i - 1].checked = checked;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      CCA(viewbox[i - 1], i);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      
      return out;
   }

}
