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
*  File        : FileImport.java
*  Modified    :
*    BAKALK   2001-04-25   Made Lov for Doc Class (docblk)visible.
*    THABLK   2001-05-24   Made Import Files action copliant with java converted server functionality.
*    THABLK   2001-05-25   Made yesterday's modifications work with multi rows.
*    BAKALK   2001-06-19   Made print and view possible for multirow.
*    BAKALK   2001-07-09   Added a lov for 'user' and made it read-only in New Layout(Call id:66810).
*                          'Edit' command made invisible  for those already 'imported'.
*    SHTHLK   2002-05-02   Bug Id 28933, Added textual tip about how to select several files
*    NISILK   2003-02-05   Added Doc Sheet. Removed variable intObj
*    DIKALK   2003-04-02   Removed method getMultipleFileInfo()
*    DIKALK   2003-04-02   Removed method getOCXString()
*    NISILK   2003-05-02   Added new method transferToFileImport and Modified method Import file to support XEDM
*    SHTHLK   2003-05-05   Bug Id 36829, Modified method storeMultipleFiles() to give an error message if the file
*             2003-05-05   types are not registered. Also fix new file select layout to exit when Cancel is pressed.
*    SHTHLK   2003-05-12   Bug Id 36829, Modified method selectFileToField() to give an error when selected File Type isn't registered.
*    NISILK   2003-05-28   Implemented multi row file import,multi row view document and multi row Delete Records
*                          Modified method viewFileLink(). and Removed method tranferToEdmMacro();
*    NISILK   2003-05-29   Modified method printFileLink() to support multi row print file functionality.
*                          Removed method selectUnselectAll() and added javascript methods
*                          selectAll() and deSelectAll() for the multi row selections
*                          Modified method getContents(), to allow the page to not to submit again when doing
*                          operations selectAll() and deSelectAll().
*    NISILK   2003-06-09   Modified method storeMultipleFiles().
*    NISILK   2003-06-10   Modified method storeMultipleFiles() to get rid of Unnecessary database calls with seperate submits.
*    NISILK   2003-06-16   Added code to handle the CCA error which was generated in Web Client 3.5.1.
*                          modified javascript methods selectAll and deSelectAll.
*    SHTOLK   2003-08-07   2003-2 SP4 Merge: Bug Id 36829.
*    INOSLK   2003-08-29   Call ID 101731: Modified doReset() and clone().
*    INOSLK   2003-09-25   Call ID 103727 - Added fields NUMBER_GENERATOR, BOOKING_LIST, ID1 & ID2.
*                          Modified methods validate(),transferToFileImport() and refreshCurrentRow().
*    BAKALK   2003-09-16   Merged Bug Id: 39449, Modified the way DirFileSelectOCX is handled. Added a new method buttonPress4();
*                          Added a new method browse() to stop the browse dialog lossing focus.
*    NISILK   2003-10-03   Added method getNumGeneratorSettings and modified method selectFileToField.
*    INOSLK   2003-10-13   Call ID 106322 - Modified methods preDefine(),validate(),getNumGeneratorSettings().
*    INOSLK   2003-10-16   Call ID 106322 - Reflected LU name change from Doc_Def_Values_API to Doc_Number_Generator_Type_API.
*                          Modified method getNumGeneratorSettings().
*    NISILK   2003-10-18   Call ID 107908 - Removed Actions button in headbar for multirow, MOdified method adjust.
*    INOSLK   2003-10-20   Call ID 103727 - Added code to notify the user and reload the form if an
*                          exception occurs when adding multiple files.
*    BAKALK   2003-11-06   Call Id : 110303 , fixed check box problem in 3.5.1 and some changes in client side.
*    BAKALK   2003-11-07   Now we are handling Oracle exection when connecting Objects. Added new methods: newObjConn(String) and formatErrorMsg(String)
*    MDAHSE   2003-11-09   Call 110529, new queryparam DEF_DOC_CLASS added to make it possible to import a file using only a GET-request.
*    BAKALK   2003-12-12   Web Alignment done.
*    BAKALK   2003-12-18   Set current row no after adding objects from single layout.
*    BAKALK   2004-01-30   Removed a varible: sApplicationSettings,since it has not been used anywhere.
*    DIKALK   2004-03-23   SP1 Merge. Merged the following bugs: 40584, 40589 and 42811
*    BAKALK   2004-04-01   Remove the usage of DirFilSelect ocx. Insted IFSCliMgrOCX used.
*    BAKALK   2004-04-06   Changed layout for file selections. Now u can select files from many directories at once.
*    BAKALK   2004-04-22   Files types are fetched for only "Original" doc types. Removed some alert( for debug) from client codes.
*                          changed the title of documents when multiple files selected.
*                          fixed GUI in dialog layout.
*    DIKALK   2004-06-23   Merged Bug Id 44622
*    DIKALK   2004-07-29   Merged Bud Id 45267
*    DIKALK   2004-08-09   Reviewed and refactored the code in this page... mercilessly!
*    DIKALK   2004-07-29   Merged Bud 44459
*    SUKMLK   2004-09-30   Added Structure checkbox.
*    DIKALK   2004-07-29   Merged Bud 46305
*    DIKALK   2004-02-11   Modified
*    DIKALK   2004-08-11   Modified client code to handle exceptions raised by the OCX.
*    DIKALK   2004-02-11   Modified transferToEdmMacro() to also pass the FILE_NAME to EdmMacro
*    BAKALK   2005-01-18   Fixed the call 121008.
*    SHTHLK   2005-08-08   Call Id 125866, Added quick-link to DOC_NO.
*    BAKALK   2005-09-23   Modified to support for DnD.
*    MDAHSE   2005-10-13   Changed to use new drop control.
*    AMNALK   2005-10-31   Merged Bug Id 53112.
*    DIKALK   2005-12-09   Added checks to ensure files with Unicode characters are excluded when DnDing files
*    DIKALK   2005-12-14   Restricted the New mode to Internet Explorer only
*    RUCSLK   2006-01-05   Fixed Call 130709.
*    KARALK   2006-02-02   Fixed call 132058
*    KARALK   2006-02-06   Fixed call 132060.
*    ThWilk   2006-02-20   Fixed call 134116,Modified createNewFileImports.
*    BAKALK   2006-07-26   Bug ID 58216, Fixed Sql Injection.
*    BAKALK   2007-05-04   Merged Bug Id 58113.
*    ASSALK   2007-08-08   Merged Bug 63998, Modified checkImportFileType()
*    ASSALK   2007-08-15   Merged Bug 66906, Modified the select statement by adding order by clause in getFileTypes().
*    AMNALK   2007-11-19   Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
*    VIRALK   2008-03-03   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*    SHTHLK   2008-04-10   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*    SHTHLK   2008-04-23   Bug Id 72405, Got the default value for structure for the selected class
*    SHTHLK   2008-06-25   Bug Id 72502, Added an error message to inform the user about missing default ID2
*    VIRALK   2008-07-22   Bug Id 74523, Modified addNewFileImports()
*    SHTHLK   2008-09-10   Bug Id 74966, Modified addNewFileImports() and editRow() to disable fields based on the number counter
*    SHTHLK   2008-09-22   Bug Id 74966, Removed the fix previously done and added javascript method to handle validation of Doc_Class, to disable lovs 
*    SHTHLK   2008-10-13   Bug Id 77725, Modified validate() and addNewFileImports() to get default values for Doc_Sheet and Doc_Rev
*    SHTHLK   2008-10-27   Bug Id 77798, Checked the Object connection check box based on the number of connections
*    SHTHLK   2008-10-30   Bug Id 77798, Used method Check_Objects_Connected() to check the exists of objects connected
*    AMNALK   2009-01-23   Bug Id 79909, Modified createObjectConnections() to handle the connection of multiple objects.
*    AMNALK   2009-02-26   Bug Id 80912, Changed mgr.translate() to mgr.translateJavaScript() in client functions.
*    AMCHLK   2009-03-26   Bug Id 79368, Disabled the RMB 'transferToDocumentInfo' if the document isn't imported.
*    VIRALK   2010-04-05   Bug Id 88317, Restricted * users from creating new documents.
* -----------------------APP7.5 SP7---------------------------------------------------
*    ARWILK   2010-07-21   Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
*    DULOLK   2010-09-16   Bug Id 92125, Modified js method addGivenFiles() to disallow files without extensions.
* ------------------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;
import java.util.StringTokenizer;

public class FileImport extends ASPPageProvider
{
   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.FileImport");

   //
   // Instances created on page creation (immutable attributes)
   //
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock docblk;
   private ASPRowSet docset;
   private ASPBlockLayout doclay;
   private ASPCommandBar docbar;
   private ASPBlock objconblk;
   private ASPBlock dummyblk;

   //
   // Transient temporary variables (never cloned)
   //
   private ASPTransactionBuffer trans;
   private ASPBuffer data;
   private ASPCommand cmd;
   private ASPBuffer keys;
   private ASPBuffer filesBuffer;

   private boolean tranfer_to_edm;
   private boolean show_file_dialog;
   private boolean show_objects;
   private boolean show_delete_cbox;
   private boolean bDeleteOrig;
   private boolean confirm_delete;
   private boolean bImportRightNow;
   private boolean bShowDocClasses;
   private boolean bQuitMultiSel;


   private String layout_mode;
   private String url;
   private String error_message;
   private String sQuitMessage;
   private String fileNames;
   private String sPath;
   private String default_docu_class;


   private int nFileToImportNow;
   // bug id 74523
   private int MAX_FILE_NAME_LENGTH = 240;
   //Bug Id 74966, Start
   private String sNumberGeneratorTrans;
   private String sNumberGenerator; 
   //Bug Id 74966, End

   private String val; //bug is 88317
   //
   // Construction
   //
   public FileImport(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();

      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      layout_mode = ctx.readValue("MODE", "0");
      bDeleteOrig = ctx.readFlag("DELETE_LOCAL_FILES",false);
      String default_doc_class = "";
      
      //Bug id 88317 Start

      if( !mgr.isEmpty(mgr.getQueryStringValue("execute")) )
         execute(); 
      //Bug id 88317 End

      if (!mgr.isEmpty(mgr.readValue("DELETE_ORIGINAL_FILE")))
      {
         bDeleteOrig = "YES".equals(mgr.readValue("DELETE_ORIGINAL_FILE"))? true:false;
      }

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ("TRUE".equals(mgr.readValue("OBJECT_INSERTED")))
         createObjectConnections();
      else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
         refreshCurrentRow();
      else if ("TRUE".equals(mgr.readValue("CONFIRM_DELETE")))
         deleteRecords();
      else if ("YES".equals(mgr.getQueryStringValue("FROM_OTHER")))
      {
         sPath = mgr.getQueryStringValue("PATH");
         if (mgr.isEmpty(sPath)) 
         { 
            //when files not to be found from directory
            filesBuffer   = mgr.newASPBuffer();
            filesBuffer   = mgr.getTransferedData();
            fileNames     = filesBuffer.getValue("FILE_NAMES");

            ctx.writeValue("OBJ_LU_NAME",filesBuffer.getValue("LU_NAME"));
            ctx.writeValue("OBJ_KEY_REF",filesBuffer.getValue("KEY_REF"));
            
            default_doc_class = getDefaultDocClass(filesBuffer.getValue("LU_NAME"));
            if ("TRUE".equals(mgr.getQueryStringValue("CREATE_CONNECT_DEF")) && !mgr.isEmpty(filesBuffer.getValue("LU_NAME")) &&!mgr.isEmpty(default_doc_class) && !mgr.isEmpty(fileNames))
               importSelectedFiles(default_doc_class, fileNames);
         }
         else
         {
            sPath = sPath.replaceAll("\\\\", "\\\\\\\\");
         }

         showFileSelectionDialog();
      }

      adjust();

      // Added by Terry 20140508
      // Show default doc class of lu
      String lu_name = ctx.readValue("OBJ_LU_NAME");
      if (!mgr.isEmpty(lu_name))
      {
         if (!mgr.isEmpty(default_doc_class))
            default_docu_class = default_doc_class;
         else
            default_docu_class = getDefaultDocClass(lu_name);
      }
      // Added end
      
      if (mgr.isEmpty(default_docu_class))
      {
         trans.clear();
         cmd = trans.addCustomFunction("DEFAULTDOCCLASS", "Docman_Default_API.Get_Default_Value_", "STR_OUT");
         cmd.addParameter("STR_IN","DocFileImport");
         cmd.addParameter("STR_IN","DOC_CLASS");
         trans = mgr.validate(trans);
         
         default_docu_class = trans.getValue("DEFAULTDOCCLASS/DATA/STR_OUT");
         default_docu_class=default_docu_class==null?"":default_docu_class;
      }
      
      ctx.writeValue("MODE", layout_mode);
      ctx.writeFlag("DELETE_LOCAL_FILES", bDeleteOrig);
      ctx.writeValue("DEF_DOC_CLASS", mgr.readValue("DEF_DOC_CLASS", ctx.readValue("DEF_DOC_CLASS", default_docu_class)));
   }

   public String getDefaultDocClass(String lu_name)
   {
      ASPManager mgr = getASPManager();
      String default_docu_class = "";
      if (mgr.isEmpty(lu_name))
         return default_docu_class;
      
      trans.clear();
      cmd = trans.addCustomFunction("GETLUDOCCLASS", "DOC_CLASS_LU_API.Get_Default_Class", "STR_OUT");
      cmd.addParameter("STR_IN", lu_name);
      trans = mgr.perform(trans);
      default_docu_class = trans.getValue("GETLUDOCCLASS/DATA/STR_OUT");
      default_docu_class = default_docu_class == null ? "" : default_docu_class;
      return default_docu_class;
   }

   public void validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");

      if ("DOC_CLASS".equals(val))
      {
         trans.clear();

         // Get default document revision
         cmd = trans.addCustomFunction("DOCREV","Doc_Class_Default_API.Get_Default_Value_","DOC_REV");
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));//Bug Id 74966
         cmd.addParameter("STR_IN","DocTitle");
         cmd.addParameter("STR_IN","DOC_REV");

         // Get number generator setting, Advanced or Standard
         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));//Bug Id 74966
         cmd.addParameter("STR_IN","DocTitle");
         cmd.addParameter("STR_IN","NUMBER_GENERATOR");

         // Default value for ID1
         cmd = trans.addCustomFunction("GETDEFAULTID1","Doc_Class_Default_API.Get_Default_Value_","ID1");
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));//Bug Id 74966
         cmd.addParameter("STR_IN","DocTitle");
         cmd.addParameter("STR_IN","NUMBER_COUNTER");

         // Get translated number generator
         cmd = trans.addCustomFunction("GETCLIENTVAL", "Doc_Number_Generator_Type_API.Decode", "NUM_GEN_TRANSLATED");
         cmd.addReference("NUMBER_GENERATOR", "NUMBERGENERATOR/DATA");

	 //Bug Id 72405, Start
	 // Default value for Strcture
         cmd = trans.addCustomFunction("GETDEFAULTST","Doc_Class_Default_API.Get_Default_Value_","STRUCTURE");
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));//Bug Id 74966
         cmd.addParameter("STR_IN","DocTitle");
         cmd.addParameter("STR_IN","STRUCTURE");
	 //Bug Id 72405, End
	 
	 //Bug Id 77725, Start
         // Get the default sheet for the class
         cmd = trans.addCustomFunction("DOCSHEET", "Doc_Class_Default_API.Get_Default_Value_", "DOC_SHEET");
         cmd.addParameter("DOC_CLASS", mgr.readValue("DOC_CLASS"));
         cmd.addParameter("STR_IN", "DocTitle");
         cmd.addParameter("STR_IN", "DOC_SHEET");
	 //Bug Id 77725, End
         trans = mgr.validate(trans);

         String doc_rev = trans.getValue("DOCREV/DATA/DOC_REV");
         String number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
         String id1 = trans.getValue("GETDEFAULTID1/DATA/ID1");
         String num_gen_translated = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
	 String strcture = trans.getValue("GETCLIENTVAL/DATA/STRUCTURE"); //Bug Id 72405
         String doc_sheet = trans.getValue("DOCSHEET/DATA/DOC_SHEET");//Bug Id 77725

         String id2;
         if ("ADVANCED".equals(number_generator))
         {
            trans.clear();
            cmd = trans.addCustomFunction("GETDEFAULTID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
            cmd.addParameter("STR_IN", id1);
            trans = mgr.perform(trans);
            id2 = trans.getValue("GETDEFAULTID2/DATA/ID2");

            if (mgr.isEmpty (id2) || id2.equals("0"))//Bug Id 72502
               id2 = "";
         }
         else
         {
            id1 = " ";
            id2 = " ";
         }

         StringBuffer response = new StringBuffer(mgr.isEmpty(doc_rev) ? "A1" : doc_rev) ;
         response.append("^");
         response.append(mgr.isEmpty(number_generator) ? "": number_generator);
         response.append("^");
         response.append(mgr.isEmpty(num_gen_translated) ? "": num_gen_translated);
         response.append("^");
         response.append(mgr.isEmpty(id1) ? "": id1);
         response.append("^");
         response.append(mgr.isEmpty(id2) ? "": id2);
         response.append("^");
	 //Bug Id 72405, Start
	 response.append(mgr.isEmpty(strcture) ? "0": strcture);
         response.append("^");
	 //Bug Id 72405, End
         //Bug Id 77725, Start
	 response.append(mgr.isEmpty(doc_sheet) ? "1": doc_sheet);
         response.append("^");
	 //Bug Id 77725, End
         mgr.responseWrite(response.toString());
      }
      else if ("VALIDATE_DOC_CLASS".equals(val))
      {
         cmd = trans.addCustomFunction("CHECKEXIST", "Doc_Class_API.Check_Exist", "OUT_STR1");
         cmd.addParameter("DOC_CLASS", mgr.readValue("DOC_CLASS"));
         trans = mgr.validate(trans);

         mgr.responseWrite(trans.getValue("CHECKEXIST/DATA/OUT_STR1"));
      }
      //Bug Id 72502, Start
      else if ("VALIDATE_ID2".equals(val))
      {
         trans.clear();

	 // Get number generator setting, Advanced or Standard
         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
         cmd.addParameter("STR_IN","DocTitle");
         cmd.addParameter("STR_IN","NUMBER_GENERATOR");

	 // Default value for ID1
	 cmd = trans.addCustomFunction("GETDEFAULTID1","Doc_Class_Default_API.Get_Default_Value_","ID1");
	 cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
	 cmd.addParameter("STR_IN","DocTitle");
	 cmd.addParameter("STR_IN","NUMBER_COUNTER");

	 trans = mgr.validate(trans);
         String number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
	 String id1 = trans.getValue("GETDEFAULTID1/DATA/ID1");
	 String id2="";

         if ("ADVANCED".equals(number_generator))
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETDEFAULTID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
            cmd.addParameter("STR_IN", id1);
            trans = mgr.perform(trans);
            id2 = trans.getValue("GETDEFAULTID2/DATA/ID2");

            if (mgr.isEmpty (id2) || id2.equals("0"))
               id2 = "ADVANCEDERROR^"+id1;
	 }
	 mgr.responseWrite(id2);
      }
      //Bug Id 72502, End
      mgr.endResponse();
   }

   //Bug id 88317 Start
   public String checkCreateNewAllowed()
   {


    /*ASPManager mgr = getASPManager();

    trans.clear();

    cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
    cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
    cmd.addReference("DUMMY2", "FNDUSER/DATA");

    trans = mgr.perform(trans);
    String person_id = trans.getValue("STARUSER/DATA/DUMMY1");*/
    String output="false";

    // Comment by Terry 20130306
    // if ("*".equals(person_id)){   
    //    output="true" ;
    // }
    // Comment end

    return output ;

   }
   
   public void  execute()
   {
      ASPManager mgr = getASPManager();      
     
      val = mgr.readValue("execute");


      if( "checkCreateNewAllowed".equals(val) )
      {
          mgr.responseWrite( checkCreateNewAllowed() );

       }
   }
   //Bug id 88317 End


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      String fndUser = getASPInfoServices().getFndUser();
      ASPQuery query = trans.addQuery(headblk);
      //bug 58216 starts
      query.addWhereCondition("FND_USER = ?" );
      query.addParameter("FND_USER",fndUser);
      //bug 58216 end
      query.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
   }


   public void okFind()
   {
      ASPManager mgr = getASPManager();

      mgr.createSearchURL(headblk);

      String fndUser = getASPInfoServices().getFndUser();
      ASPQuery query = trans.addQuery(headblk);
       //bug 58216 starts
      query.addWhereCondition("FND_USER = ?" );
      query.addParameter("FND_USER",fndUser);
      //bug 58216 end
      query.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert("DOCMAWFILEIMPORTNODATA: No data found.");
         headset.clear();
      }
   }


   public void editRow()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout()) {
         headset.goTo(headset.getRowSelected());
      }

      if ("1".equals(headset.getRow().getValue("IMPORTED"))) {
         mgr.showAlert("DOCMAWFILEIMPORTEDITIMPORTED: Imported files cannot be edited. You may however edit the attributes of the corressponding title.");
         return;
      }

      //Bug Id 74966, Start
      trans.clear();
      cmd = trans.addCustomFunction("GETNUMGEN", "Doc_Class_Default_API.Get_Default_Value_", "NUMBER_GENERATOR");
      cmd.addParameter("DOC_CLASS", headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("STR_IN", "DocTitle");
      cmd.addParameter("STR_IN", "NUMBER_GENERATOR");
      trans = mgr.perform(trans);

      sNumberGenerator = trans.getValue("GETNUMGEN/DATA/NUMBER_GENERATOR");
      trans.clear();
      //Bug Id 74966, End

      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
   }


   public void  saveNew()
   {
      ASPManager mgr = getASPManager();
      headset.changeRow();
      mgr.submit(trans);
      trans.clear();
      showFileSelectionDialog();
   }


   public void duplicateRow()
   {
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      headset.addRow(headset.getRow());
      headset.setValue("DOC_NO", "");
      headset.setValue("IMPORTED", "0");
      headlay.setLayoutMode(headlay.NEW_LAYOUT);
   }


   public String getFileTypes()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type");
      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("GETORIGINALFILETYPES");

      String file_types = "";
      int rows = Integer.parseInt(buf.getBuffer("INFO").getValue("ROWS"));
      //int rows = buf.countItems() - 1;

      if (rows > buf.countItems() - 1){  // only if no of file types are more than row-no-limitation in web client
        trans.clear();
        query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type");
        query.setBufferSize(rows);
        trans = mgr.perform(trans);
        buf = trans.getBuffer("GETORIGINALFILETYPES");
      }

      for (int i = 0; i < buf.countItems() - 1; i++)
      {
         file_types += buf.getBufferAt(i).getValueAt(0) + "^" + buf.getBufferAt(i).getValueAt(1) + "^";
      }

      return file_types;
   }


   public void showFileSelectionDialog()
   {
      ASPManager mgr = getASPManager();

//      if (!mgr.isExplorer())
//      {
//         mgr.showAlert("DOCMAWFILEIMPORTNONIENOTSUPPORTEDFORNEW: Creating new file imports is a feature limited to Internet Explorer only");
//         headlay.setLayoutMode(headlay.getHistoryMode());
//         return;
//      }    

      // Save current layout
      layout_mode = String.valueOf(headlay.getHistoryMode());

      // Show file selection dialog
      show_file_dialog = true;
      show_delete_cbox = false;
      ctx.writeFlag("CUSTOM_NEW_LAYOUT", true);
   }


   public void cancelNew()
   {
      int mode = Integer.parseInt(layout_mode);

      // Clear any unsaved rows that were added to the buffer..
      if (ctx.readFlag("CUSTOM_NEW_LAYOUT", false) != true && (headset != null && headset.countRows() > 0))
      {
      	 if (headset.countRows() == 1)
            headset.clear();
          else
            headset.clearRow();
      }

      headlay.setLayoutMode(mode);
   }


   public void createNewFileImports()
   {
      ASPManager mgr = getASPManager();

      // Get list of files selected by user
      String file_list = mgr.readValue("FILE_LIST");

      // Add all selected files as new file imports to the rowset
      addNewFileImports();

      if (file_list.indexOf("|") > 0 || bImportRightNow || !mgr.isEmpty(ctx.readValue("OBJ_LU_NAME","")))
      {
         // Save the new rows in the rowset to the database
         trans.clear();
         mgr.submit(trans);
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }
      else
      {
         
         // Since only one row was added, let the layout remain in the
         // New Layout, to allow the user to make any specific changes
         // before saving
          if (layout_mode.equals("0")) {
              headlay.setLayoutMode(headlay.NEW_LAYOUT);
          }

      }

      if( !mgr.isEmpty(ctx.readValue("OBJ_LU_NAME","")))
      {
         headset.first();
         do
         {
            headset.refreshRow();
            //headset.selectRow();
         } while (headset.next());
      }
   }


   public void addNewFileImports()
   {
      ASPManager mgr = getASPManager();

      // Get default_doc_class
      String default_doc_class = mgr.readValue("DEF_DOC_CLASS");
      StringTokenizer st = new StringTokenizer(mgr.readValue("FILE_LIST"), "|");

      if (st.countTokens() > 1 && mgr.isEmpty(default_doc_class))
      {
         mgr.showError(mgr.translate("DOCMAWFILEIMPORTDEFDOCCLASSNOTSEL: A Default Document Class was not selected"));
         return;
      }
      else
      {
         nFileToImportNow = st.countTokens();
         while (st.hasMoreTokens())
         {
            String file_name = st.nextToken();
            String file_extension = DocmawUtil.getFileExtention(file_name);
            String base_file_name = DocmawUtil.getBaseFileName(file_name);

            trans.clear();
            ASPCommand cmd = trans.addCustomFunction("GET_FILE_TYPE", "Edm_Application_API.Get_File_Type", "STR_OUT");
            cmd.addParameter("STR_IN", file_extension.toUpperCase());
            trans = mgr.perform(trans);

            String file_type = trans.getValue("GET_FILE_TYPE/DATA/STR_OUT");

            if (mgr.isEmpty(file_type))
            {
               mgr.showError(mgr.translate("DOCMAWFILEIMPORTNOFILETYPE: The file type &1 that you are trying to import is not registered in the system. Contact your system administrator to register this file type.", file_type));
               return;
            }

            // Create a new row for each the selected files
            trans.clear();
            cmd = trans.addEmptyCommand("HEAD", "DOC_FILE_IMPORT_API.New__", headblk);
            cmd.setOption("ACTION", "PREPARE");

            // Get the default sheet for the class
            cmd = trans.addCustomFunction("DOCSHEET", "Doc_Class_Default_API.Get_Default_Value_", "DOC_SHEET");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "DOC_SHEET");

            // Get the default revision for the class
            cmd = trans.addCustomFunction("DOCREV", "Doc_Class_Default_API.Get_Default_Value_", "DOC_REV");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "DOC_REV");

            // Number generator settings
            cmd = trans.addCustomFunction("GETNUMGEN", "Doc_Class_Default_API.Get_Default_Value_", "NUMBER_GENERATOR");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "NUMBER_GENERATOR");

            // Get File Import Title Pattern settings
            cmd = trans.addCustomFunction("FILEIMPORTTITLEPATTERN", "Doc_Class_Default_API.Get_Default_Value_", "FILE_IMPORT_TITLE_PATTERN");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocFileImport");
            cmd.addParameter("STR_IN", "FILE_IMPORT_TITLE_PATTERN");
    	    //Bug Id 72405, Start
	    // Default value for Strcture
	    cmd = trans.addCustomFunction("GETDEFAULTST","Doc_Class_Default_API.Get_Default_Value_","STRUCTURE");
	    cmd.addParameter("DOC_CLASS",default_doc_class);
	    cmd.addParameter("STR_IN","DocTitle");
	    cmd.addParameter("STR_IN","STRUCTURE");
	    //Bug Id 72405, End
            //Bug Id 74966, Start   
	    // Get translated number generator
	    cmd = trans.addCustomFunction("GETCLIENTVAL", "Doc_Number_Generator_Type_API.Decode", "NUM_GEN_TRANSLATED");
	    cmd.addReference("NUMBER_GENERATOR", "GETNUMGEN/DATA");
	    //Bug Id 74966, End
            trans = mgr.perform(trans);

            ASPBuffer data = trans.getBuffer("HEAD/DATA");
	    String default_doc_sheet = mgr.isEmpty(trans.getValue("DOCSHEET/DATA/DOC_SHEET")) ? "1": trans.getValue("DOCSHEET/DATA/DOC_SHEET"); //Bug Id 77725
	    String default_doc_rev = mgr.isEmpty(trans.getValue("DOCREV/DATA/DOC_REV")) ? "A1" : trans.getValue("DOCREV/DATA/DOC_REV"); //Bug Id 77725
            String number_generator = trans.getValue("GETNUMGEN/DATA/NUMBER_GENERATOR");
            String default_file_pattern = trans.getValue("FILEIMPORTTITLEPATTERN/DATA/FILE_IMPORT_TITLE_PATTERN");
	    String default_structure = trans.getValue("GETDEFAULTST/DATA/STRUCTURE");//Bug Id 72405
            //Bug Id 74966, Start
	    sNumberGenerator = number_generator;
	    sNumberGeneratorTrans = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
	    if (mgr.isEmpty(sNumberGeneratorTrans)) 
		sNumberGeneratorTrans = "";
	    //Bug Id 74966, End
	     //Bug Id 74523 Start
	    if (MAX_FILE_NAME_LENGTH < default_file_pattern.replaceAll("%f", base_file_name).length()) 
	   {
	       base_file_name =  base_file_name.substring(0, MAX_FILE_NAME_LENGTH-default_file_pattern.length());
	   }
	    //Bug Id 74523 End
            headset.addRow(data);
            headset.setValue("DOC_CLASS", default_doc_class);
            headset.setValue("FILE_NAME", file_name);
            headset.setValue("DOC_TITLE", base_file_name);
            headset.setValue("DOC_SHEET", default_doc_sheet);
            headset.setValue("DOC_REV", default_doc_rev);
            headset.setValue("FILE_TYPE", file_type);
	    headset.setValue("STRUCTURE", default_structure); //Bug Id 72405
            headset.setValue("IMPORTED", "0");
            headset.setValue("OBJ_KEY_REF", ctx.readValue("OBJ_KEY_REF",""));
            headset.setValue("OBJ_LU_NAME", ctx.readValue("OBJ_LU_NAME",""));


            String id1 = null;
            String id2 = null;

            if (number_generator.equals("ADVANCED"))
            {
               trans.clear();

               // Number counter settings
               cmd = trans.addCustomFunction("GETID1", "Doc_Class_Default_API.Get_Default_Value_", "ID1");
               cmd.addParameter("DOC_CLASS", default_doc_class);
               cmd.addParameter("STR_IN", "DocTitle");
               cmd.addParameter("STR_IN", "NUMBER_COUNTER");

               cmd = trans.addCustomFunction("GETID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
               cmd.addReference("ID1", "GETID1/DATA");
               trans = mgr.perform(trans);

               id1 = trans.getValue("GETID1/DATA/ID1");
               id2 = trans.getValue("GETID2/DATA/ID2");
               if (mgr.isEmpty (id2) || id2.equals("0"))//Bug Id 72502
                  id2 = "";
            }
            else
            {
               id1 = "";
               id2 = "";
            }

            headset.setValue("ID1", id1);
            headset.setValue("ID2", id2);
         }
      }
   }


   public static String formatErrorMsg(String error_message)
   {
      if (error_message.equals(error_message.substring(0,error_message.indexOf("\n"))))
      {
         return error_message.substring(0,error_message.indexOf("\r"));
      }
      else
      {
         return error_message.substring(0,error_message.indexOf("\n"));
      }
   }

   public void importSelectedFiles(String default_docu_class, String file_list)
   {
      ASPManager mgr = getASPManager();
      bImportRightNow = true;
      createNewFileImports(default_docu_class, file_list);
      
      //nFileToImportNow
      headset.goTo(headset.countRows()- nFileToImportNow);
      for(int k = 0; k< nFileToImportNow;k++){
         headset.selectRow();
         headset.next();
      }
      
      importFile();
      
      ctx.writeValue("OBJ_LU_NAME",ctx.readValue("OBJ_LU_NAME",""));
      ctx.writeValue("OBJ_KEY_REF",ctx.readValue("OBJ_KEY_REF",""));
   }

   public void createNewFileImports(String default_docu_class, String file_list)
   {
      ASPManager mgr = getASPManager();

      // Add all selected files as new file imports to the rowset
      addNewFileImports(default_docu_class, file_list);

      if (file_list.indexOf("|") > 0 || bImportRightNow || !mgr.isEmpty(ctx.readValue("OBJ_LU_NAME","")))
      {
         // Save the new rows in the rowset to the database
         trans.clear();
         mgr.submit(trans);
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }
      else
      {
         // Since only one row was added, let the layout remain in the
         // New Layout, to allow the user to make any specific changes
         // before saving
          if (layout_mode.equals("0")) {
              headlay.setLayoutMode(headlay.NEW_LAYOUT);
          }
      }

      if( !mgr.isEmpty(ctx.readValue("OBJ_LU_NAME","")))
      {
         headset.first();
         do
         {
            headset.refreshRow();
            //headset.selectRow();
         } while (headset.next());
      }
   }
   
   public void addNewFileImports(String default_doc_class, String file_list)
   {
      ASPManager mgr = getASPManager();
      
      StringTokenizer st = new StringTokenizer(file_list, "|");
      
      if (st.countTokens() > 1 && mgr.isEmpty(default_doc_class))
      {
         mgr.showError(mgr.translate("DOCMAWFILEIMPORTDEFDOCCLASSNOTSEL: A Default Document Class was not selected"));
         return;
      }
      else
      {
         nFileToImportNow = st.countTokens();
         while (st.hasMoreTokens())
         {
            String file_name = st.nextToken();
            String file_extension = DocmawUtil.getFileExtention(file_name);
            String base_file_name = DocmawUtil.getBaseFileName(file_name);
            
            trans.clear();
            ASPCommand cmd = trans.addCustomFunction("GET_FILE_TYPE", "Edm_Application_API.Get_File_Type", "STR_OUT");
            cmd.addParameter("STR_IN", file_extension.toUpperCase());
            trans = mgr.perform(trans);
            
            String file_type = trans.getValue("GET_FILE_TYPE/DATA/STR_OUT");
            
            if (mgr.isEmpty(file_type))
            {
               mgr.showError(mgr.translate("DOCMAWFILEIMPORTNOFILETYPE: The file type &1 that you are trying to import is not registered in the system. Contact your system administrator to register this file type.", file_type));
               return;
            }
            
            // Create a new row for each the selected files
            trans.clear();
            cmd = trans.addEmptyCommand("HEAD", "DOC_FILE_IMPORT_API.New__", headblk);
            cmd.setOption("ACTION", "PREPARE");
            
            // Get the default sheet for the class
            cmd = trans.addCustomFunction("DOCSHEET", "Doc_Class_Default_API.Get_Default_Value_", "DOC_SHEET");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "DOC_SHEET");
            
            // Get the default revision for the class
            cmd = trans.addCustomFunction("DOCREV", "Doc_Class_Default_API.Get_Default_Value_", "DOC_REV");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "DOC_REV");
            
            // Number generator settings
            cmd = trans.addCustomFunction("GETNUMGEN", "Doc_Class_Default_API.Get_Default_Value_", "NUMBER_GENERATOR");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocTitle");
            cmd.addParameter("STR_IN", "NUMBER_GENERATOR");
            
            // Get File Import Title Pattern settings
            cmd = trans.addCustomFunction("FILEIMPORTTITLEPATTERN", "Doc_Class_Default_API.Get_Default_Value_", "FILE_IMPORT_TITLE_PATTERN");
            cmd.addParameter("DOC_CLASS", default_doc_class);
            cmd.addParameter("STR_IN", "DocFileImport");
            cmd.addParameter("STR_IN", "FILE_IMPORT_TITLE_PATTERN");
            //Bug Id 72405, Start
            // Default value for Strcture
            cmd = trans.addCustomFunction("GETDEFAULTST","Doc_Class_Default_API.Get_Default_Value_","STRUCTURE");
            cmd.addParameter("DOC_CLASS",default_doc_class);
            cmd.addParameter("STR_IN","DocTitle");
            cmd.addParameter("STR_IN","STRUCTURE");
            //Bug Id 72405, End
            //Bug Id 74966, Start   
            // Get translated number generator
            cmd = trans.addCustomFunction("GETCLIENTVAL", "Doc_Number_Generator_Type_API.Decode", "NUM_GEN_TRANSLATED");
            cmd.addReference("NUMBER_GENERATOR", "GETNUMGEN/DATA");
            //Bug Id 74966, End
            trans = mgr.perform(trans);
            
            ASPBuffer data = trans.getBuffer("HEAD/DATA");
            String default_doc_sheet = mgr.isEmpty(trans.getValue("DOCSHEET/DATA/DOC_SHEET")) ? "1": trans.getValue("DOCSHEET/DATA/DOC_SHEET"); //Bug Id 77725
            String default_doc_rev = mgr.isEmpty(trans.getValue("DOCREV/DATA/DOC_REV")) ? "A1" : trans.getValue("DOCREV/DATA/DOC_REV"); //Bug Id 77725
            String number_generator = trans.getValue("GETNUMGEN/DATA/NUMBER_GENERATOR");
            String default_file_pattern = trans.getValue("FILEIMPORTTITLEPATTERN/DATA/FILE_IMPORT_TITLE_PATTERN");
            String default_structure = trans.getValue("GETDEFAULTST/DATA/STRUCTURE");//Bug Id 72405
            //Bug Id 74966, Start
            sNumberGenerator = number_generator;
            sNumberGeneratorTrans = trans.getValue("GETCLIENTVAL/DATA/NUM_GEN_TRANSLATED");
            if (mgr.isEmpty(sNumberGeneratorTrans)) 
               sNumberGeneratorTrans = "";
            //Bug Id 74966, End
            //Bug Id 74523 Start
            if (MAX_FILE_NAME_LENGTH < default_file_pattern.replaceAll("%f", base_file_name).length()) 
            {
               base_file_name =  base_file_name.substring(0, MAX_FILE_NAME_LENGTH-default_file_pattern.length());
            }
            //Bug Id 74523 End
            headset.addRow(data);
            headset.setValue("DOC_CLASS", default_doc_class);
            headset.setValue("FILE_NAME", file_name);
            headset.setValue("DOC_TITLE", base_file_name);
            headset.setValue("DOC_SHEET", default_doc_sheet);
            headset.setValue("DOC_REV", default_doc_rev);
            headset.setValue("FILE_TYPE", file_type);
            headset.setValue("STRUCTURE", default_structure); //Bug Id 72405
            headset.setValue("IMPORTED", "0");
            headset.setValue("OBJ_KEY_REF", ctx.readValue("OBJ_KEY_REF",""));
            headset.setValue("OBJ_LU_NAME", ctx.readValue("OBJ_LU_NAME",""));
            
            String id1 = null;
            String id2 = null;
            
            if (number_generator.equals("ADVANCED"))
            {
               trans.clear();
               
               // Number counter settings
               cmd = trans.addCustomFunction("GETID1", "Doc_Class_Default_API.Get_Default_Value_", "ID1");
               cmd.addParameter("DOC_CLASS", default_doc_class);
               cmd.addParameter("STR_IN", "DocTitle");
               cmd.addParameter("STR_IN", "NUMBER_COUNTER");
               
               cmd = trans.addCustomFunction("GETID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
               cmd.addReference("ID1", "GETID1/DATA");
               trans = mgr.perform(trans);
               
               id1 = trans.getValue("GETID1/DATA/ID1");
               id2 = trans.getValue("GETID2/DATA/ID2");
               if (mgr.isEmpty (id2) || id2.equals("0"))//Bug Id 72502
                  id2 = "";
            }
            else
            {
               id1 = "";
               id2 = "";
            }
            
            headset.setValue("ID1", id1);
            headset.setValue("ID2", id2);
         }
      }
   }

   public void importSelectedFiles()
   {
        ASPManager mgr = getASPManager();
        //mgr.showError(mgr.translate("DOCMAWFILEIMPORTDEFDOCCLASSNOTSEL2: u  did not import files"));
        bImportRightNow = true;
        createNewFileImports();

        //nFileToImportNow
        headset.goTo(headset.countRows()- nFileToImportNow);
        for(int k = 0; k< nFileToImportNow;k++){
           headset.selectRow();
           headset.next();
        }

        importFile();

        ctx.writeValue("OBJ_LU_NAME",ctx.readValue("OBJ_LU_NAME",""));
        ctx.writeValue("OBJ_KEY_REF",ctx.readValue("OBJ_KEY_REF",""));


   }

   public void objectConnections()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();

      headset.setFilterOn();


      // Check if all rows have been imported
      int no_selected_rows = headset.countRows();
      for (int x = 1; x <= no_selected_rows; x++)
      {
         if ("0".equals(headset.getRow().getValue("IMPORTED")))
         {
            mgr.showAlert("DOCMAWFILEIMPORTNOTALLIMPORTED: Object connections can only be made on imported rows.");
            headset.setFilterOff();
            return;
         }

         headset.next();
      }

      headset.setFilterOff();

      // Show Object Connections page to allow the user to
      // to choose objects from
      show_objects = true;
   }



   public void  createObjectConnections()
   {
     ASPManager mgr = getASPManager();

     int current_row_no = headset.getCurrentRowNo();
     if (headlay.isMultirowLayout())
        headset.storeSelections();
     else
     {
        headset.unselectRows();
        headset.selectRow();
     }

     //Bug Id 79909, start
     StringTokenizer stKeyRef = null;
     String sKeyRefStr = mgr.readValue("TEMP_KEY_REF");  
     //Bug Id 79909, end

     //
     // Connect the object to each of the selected rows
     //

     // Prepare an attribute string that can be reused for all new connections
     trans.clear();
     String prepared_attr = "";
     ASPCommand cmd = trans.addCustomCommand("OBJCON", "DOC_REFERENCE_OBJECT_API.New__");
     cmd.addParameter("STR_OUT");
     cmd.addParameter("STR_OUT");
     cmd.addParameter("STR_OUT");
     cmd.addParameter("STR_INOUT", prepared_attr);
     cmd.addParameter("STR_IN", "PREPARE");
     trans = mgr.perform(trans);
     prepared_attr = trans.getBuffer("OBJCON/DATA").getValueAt(3);

     ASPBuffer selected_rows = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

     int no_rows = selected_rows.countItems();
     for (int x = 0; x < no_rows; x++)
     {
        ASPBuffer row = selected_rows.getBufferAt(x);

        //Bug Id 79909, start
        stKeyRef = new StringTokenizer(sKeyRefStr, "||");
        while (stKeyRef.hasMoreTokens())
        {
           // Get class specific values for Keep_Last_Doc_Rev
           trans.clear();
           //bug 58216 starts
           cmd = trans.addQuery("LASTDOCREV", "SELECT DECODE(Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_(?), NULL, 'F', Doc_Reference_Object_API.Get_Keep_Last_Dov_Rev_(?)) STR_OUT FROM DUAL");
           cmd.addParameter("DOC_CLASS",row.getValue("DOC_CLASS"));
           cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
           //bug 58216 end
           cmd = trans.addCustomFunction("LASTDOCREVDECODE", "ALWAYS_LAST_DOC_REV_API.Decode", "KEEP_LAST_DOC_REV_DECODE");
           cmd.addReference("STR_OUT", "LASTDOCREV/DATA");
           trans = mgr.perform(trans);

           // extract the key values from the key_ref..
	   
           StringTokenizer st = new StringTokenizer(stKeyRef.nextToken(), "^"); //Bug Id 79909
	   
           String key_values = "";
           while (st.hasMoreTokens())
           {
              String item = st.nextToken();
              key_values += (item.substring(item.indexOf("=") + 1)) + "^";
           }


           StringBuffer attr = new StringBuffer(prepared_attr);
           DocmawUtil.addToAttribute(attr, "LU_NAME", mgr.readValue("TEMP_LU_NAME"));
           DocmawUtil.addToAttribute(attr, "KEY_VALUE", key_values);
           DocmawUtil.addToAttribute(attr, "DOC_CLASS", row.getValueAt(0));
           DocmawUtil.addToAttribute(attr, "DOC_NO", row.getValueAt(1));
           DocmawUtil.addToAttribute(attr, "DOC_SHEET", row.getValueAt(2));
           DocmawUtil.addToAttribute(attr, "DOC_REV", row.getValueAt(3));
           DocmawUtil.addToAttribute(attr, "KEEP_LAST_DOC_REV", trans.getValue("LASTDOCREVDECODE/DATA/KEEP_LAST_DOC_REV_DECODE"));

           trans.clear();
           cmd = trans.addCustomCommand("OBJECTCONN", "DOC_REFERENCE_OBJECT_API.New__");
           cmd.addParameter("STR_IN");
           cmd.addParameter("STR_IN");
           cmd.addParameter("STR_IN");
           cmd.addParameter("STR_IN", attr.toString());
           cmd.addParameter("STR_IN", "DO");

           try
           {
              mgr.submitEx(trans);
           }
           catch(ASPLog.ExtendedAbortException exp)
           {
              Buffer tempBuff = exp.getExtendedInfo();
              try
              {
                 String document_key = row.getValueAt(0) + " - " + row.getValueAt(1) + " - " + row.getValueAt(2) + " - " + row.getValueAt(3);
                 mgr.showAlert(mgr.translate("DOCMAWFILEIMPORTERRORINROW: Error connecting object to") + " " + document_key  + ": ");
                 mgr.showAlert(formatErrorMsg(tempBuff.getItem("ORACLE_ERROR_MESSAGE").getString()));
                 return;
              }
              catch(ItemNotFoundException expItem){}
           }
        }
        //Bug Id 79909, end
     }

     if (headlay.isSingleLayout()) {
        headset.goTo(current_row_no);
     }
     refreshCurrentRow(); //Bug Id 77798
  }



   public void  transferToDocumentInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();

      headset.setFilterOn();

      for (int x=0; x< headset.countRows(); x++)
      {
         if ("0".equals(headset.getRow().getValue("IMPORTED")))
         {
            mgr.showAlert("DOCMAWFILEIMPORTTRANSFERTODOCINFO: Files that have not been imported cannot be viewed Document Info");
            headset.setFilterOff();
            return;
         }
         headset.next();
      }

      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      mgr.transferDataTo("DocIssue.page", keys);
   }



   public void refreshCurrentRow()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.setFilterOn();
      }
      else
      {
         headset.refreshRow();  //  change the file import status
         return;
      }

      headset.first();

      do
      {
         headset.refreshRow();
      }
      while (headset.next());

      headset.setFilterOff();
      headset.unselectRows();

      if (!mgr.isEmpty(ctx.readValue("OBJ_LU_NAME","")) && !mgr.isEmpty(ctx.readValue("OBJ_KEY_REF",""))) {
         mgr.redirectTo("DocReference.page?LU_NAME=" +mgr.URLEncode(ctx.readValue("OBJ_LU_NAME",""))+ "&KEY_REF=" +mgr.URLEncode(ctx.readValue("OBJ_KEY_REF","")));
      }
   }



   public void transferToEdmMacro(String action)
   {
      String docAttr;
      ASPManager mgr = getASPManager();

      if (!bImportRightNow)
      {
             if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
         }
         else
         {
            headset.selectRow();
         }
      }

      tranfer_to_edm = true;
      ASPBuffer act_buf = mgr.newASPBuffer();
      act_buf.addItem("FILE_ACTION", action);
      act_buf.addItem("EXECUTE_MACRO","FALSE");

      if (action.equals("FILEIMPORT"))
      {
         act_buf.addItem("DELETE_IMPORTED_FILES", bDeleteOrig ? "TRUE" : "FALSE");
      }

      ASPBuffer doc_buf = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,FILE_NAME,DOC_TITLE,FILE_TYPE,NUMBER_GENERATOR,BOOKING_LIST,ID1,ID2");
      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", act_buf, doc_buf);
   }


   public void viewLocalFile()
   {
      transferToEdmMacro("ORIGINAL", "LAUNCH_LOCAL_FILE");
   }


   public void viewOriginal()
   {
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	  return;
      }
      //Bug Id 67336, end
      transferToEdmMacro("ORIGINAL","VIEW");
   }


   public void printDocument()
   {
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	  return;
      }
      //Bug Id 67336, end
      transferToEdmMacro("ORIGINAL","PRINT");
   }


   public void viewOriginalWithExternalViewer()
   {
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	  return;
      }
      //Bug Id 67336, end
      transferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
   }

   //Bug Id 67336, start
   private boolean CheckFileOperationEnable()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
	  headset.storeSelections();
	  headset.setFilterOn();
	  String temp = " ";
	  String structure;
	  if (headset.countSelectedRows() > 1)
	  {
	      for (int k = 0;k < headset.countSelectedRows();k++)
	      {
		  structure = headset.getValue("STRUCTURE");
		  if (structure == null)
		  {
		       structure = "0";
		  }
		  if (" ".equals(temp)) 
		  {
		      temp = structure;
		  }
		  if (!temp.equals(structure)) 
		  {
		      mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		      headset.setFilterOff();
		      return true;
		  }
		  if ("1".equals(temp) && "1".equals(structure)) 
		  {
		      mgr.showAlert(mgr.translate("DOCMAWAPPROVALCURRENTSTEPDOCAPPROVEOVWNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		      headset.setFilterOff();
		      return true;
		  }
		  temp = structure;
		  headset.next();
	      }
	  }
	headset.setFilterOff();
      }
      return false;
   }
   //Bug Id 67336, end

   public void importFile()
   {
      ASPManager mgr = getASPManager();

      if(!bImportRightNow){
         if (headlay.isMultirowLayout())
            headset.storeSelections();
         else
            headset.selectRow();
      }

      headset.setFilterOn();

      if (headset.countSelectedRows() == 0)
      {
         mgr.showAlert("DOCMAWFILEIMPORTNOROWSSELECTED: No rows were selected for importing");
         headset.setFilterOff();
         return;
      }

      headset.first();
      for (int x = 0; x < headset.countRows(); x++)
      {
         if ("1".equals(headset.getRow().getValue("IMPORTED")))
         {
            mgr.showAlert("DOCMAWFILEIMPORTROWSALREADYIMPORTED: Some of the files you are trying to import have already been imported.");
            headset.setFilterOff();
            return;
         }
         //Bug Id 72502, Start
	 if ("ADVANCED".equals(headset.getRow().getValue("NUMBER_GENERATOR")) && mgr.isEmpty(headset.getRow().getValue("ID2")))
         {
            mgr.showError(mgr.translate("DOCMAWFILEIMPORTNOID2: Cannot find a suitable Number Counter for ID1 = &1. Specify ID2 or make sure that a default Number Counter exists.", headset.getRow().getValue("ID1")));
            headset.setFilterOff();
            return;
         }
	 //Bug Id 72502, End
         headset.next();
      }

      headset.setFilterOff();
      transferToEdmMacro("FILEIMPORT");
   }


   public void transferToEdmMacro(String doc_type, String action)
   {
      ASPManager mgr = getASPManager();
      if (!bImportRightNow)
      {
         if (headlay.isSingleLayout())
         {
            headset.unselectRows();
            headset.selectRow();
         }
         else
        {
           headset.storeSelections();
        }
      }

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV,FILE_TYPE,FILE_NAME");
      url = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
      tranfer_to_edm = true;
   }


   public void  comfirmDeletion()
   {
      confirm_delete = true;
      headset.storeSelections();
   }


   public void deleteRecords()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.selectRows();
      else
         headset.selectRow();

      headset.setSelectedRowsRemoved();
      mgr.submit(trans);
  }


  public void modifyDocumentClass()
  {
      ASPManager mgr = getASPManager();

      // 2006-03-03, DIKALK: This is a temporary work around for what certainly
      // looks like a web client bug, since there is hardly any time now to be
      // investigating this further.
      headset.refreshAllRows();


      int current_row = headset.getCurrentRowNo(); 
      if (headlay.isMultirowLayout())
      {
         headset.selectRows();
      }
      else
      {
         headset.selectRow();
      }

      int numRows = headset.countSelectedRows();

      if (numRows == 0)
      {
         bQuitMultiSel = true;
         sQuitMessage = mgr.translate("DOCMAWFILEIMPORTAPPLYNOROWS: No rows were selected to modify the Document Class");
         headset.setFilterOff();
         return;
      }


      // Check if the user is trying to modify the class
      // for rows that have already been imported..

      headset.setFilterOn();
      headset.first();

      do
      {
         if ("1".equals(headset.getRow().getValue("IMPORTED")))
         {
            mgr.showAlert("DOCMAWFILEIMPORTEDITIMMODIFY: You cannot modify the class on a document that has already been imported.");
            headset.setFilterOff();
            return;
         }
      }
      while (headset.next());



      if(mgr.isEmpty(mgr.readValue("SELECTED_DOC_CLASS")))
      {
         bShowDocClasses = true;
      }
      else
      {
         String applyClass = "";
         String numgen = "",id1 = "",id2 = "", rev="",sheet="",strcture="";//Bug Id 72405

         applyClass = mgr.readValue("SELECTED_DOC_CLASS");

         if (!("".equals(applyClass)))
         {
            // get the default revision for the class
            trans.clear();
            cmd = trans.addCustomFunction("DOCREV", "Doc_Class_Default_API.Get_Default_Value_", "DOC_REV");
            cmd.addParameter("DOC_CLASS", applyClass);
            cmd.addParameter("DUMMY1", "DocTitle");
            cmd.addParameter("DUMMY2", "DOC_REV");

            // get the default sheet for the class
            cmd = trans.addCustomFunction("DOCSHEET", "Doc_Class_Default_API.Get_Default_Value_", "DOC_SHEET");
            cmd.addParameter("DOC_CLASS", applyClass);
            cmd.addParameter("DUMMY1", "DocTitle");
            cmd.addParameter("DUMMY2", "DOC_SHEET");

            // number generator settings
            cmd = trans.addCustomFunction("GETNUMGEN", "Doc_Class_Default_API.Get_Default_Value_", "NUMBER_GENERATOR");
            cmd.addParameter("DOC_CLASS", applyClass);
            cmd.addParameter("DUMMY1", "DocTitle");
            cmd.addParameter("DUMMY2", "NUMBER_GENERATOR");

            cmd = trans.addCustomFunction("GETID1", "Doc_Class_Default_API.Get_Default_Value_", "ID1");
            cmd.addParameter("DOC_CLASS", applyClass);
            cmd.addParameter("DUMMY1", "DocTitle");
            cmd.addParameter("DUMMY2", "NUMBER_COUNTER");
	    //Bug Id 72405, Start
	    // Default value for Strcture
	    cmd = trans.addCustomFunction("GETDEFAULTST","Doc_Class_Default_API.Get_Default_Value_","STRUCTURE");
	    cmd.addParameter("DOC_CLASS",applyClass);
	    cmd.addParameter("DUMMY1","DocTitle");
	    cmd.addParameter("DUMMY2","STRUCTURE");
	    //Bug Id 72405, End

            trans = mgr.perform(trans);

            rev = trans.getValue("DOCREV/DATA/DOC_REV");
            sheet = trans.getValue("DOCSHEET/DATA/DOC_SHEET");
            numgen = trans.getValue("GETNUMGEN/DATA/NUMBER_GENERATOR");
            id1 = trans.getValue("GETID1/DATA/ID1");
	    strcture = trans.getValue("GETDEFAULTST/DATA/STRUCTURE");//Bug Id 72405

            trans.clear();
            if (numgen.equals("ADVANCED"))
            {
               cmd = trans.addCustomFunction("GETID2", "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
               cmd.addParameter("ID1",id1);

               trans = mgr.perform(trans);
               id2 = trans.getValue("GETID2/DATA/ID2");
               if (mgr.isEmpty (id2) || id2.equals("0"))//Bug Id 72502
                  id2 = "";
            }
            else
            {
               id1 = "";
               id2 = "";
            }
         }


         trans.clear();
         headset.first();
         do
         {
            headset.setValue("DOC_CLASS", applyClass);
            headset.setValue("DOC_SHEET", sheet);
            headset.setValue("DOC_REV", rev);
            headset.setValue("ID1", id1);
            headset.setValue("ID2", id2); //Bug Id 72502, Previously this was set as ID1 by mistake
	    headset.setValue("STRUCTURE", strcture);//Bug Id 72405
         }
         while (headset.next());

        mgr.submit(trans);
        headset.setFilterOff();

        if (headlay.isSingleLayout())
        {
           headset.goTo(current_row);
        }
      }
  }



   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      getASPInfoServices().addFields();

      headblk = mgr.newASPBlock("HEAD");
      headbar=mgr.newASPCommandBar(headblk);

      headblk.addField("OBJID").
              setHidden();

      headblk.addField("KEEP_LAST_DOC_REV_DECODE").
              setHidden().
              setFunction("''");

      headblk.addField("DELETE_REC_VALIDATE").
              setHidden().
              setFunction("'0'");

      headblk.addField("IMPORT_RIGHT_NOW").
              setHidden().
              setFunction("'0'");

      headblk.addField("OBJVERSION").
              setHidden();

      headblk.addField("FILE_NAME").
              setSize(30).
              setMaxLength(250).
              setLabel("DOCMAWFILEIMPORTFILENAME: File Name");

      headblk.addField("FILE_TYPE").
              setSize(20).
              setMaxLength(30).
              setMandatory().
              setDynamicLOV("EDM_APPLICATION").
              setLabel("DOCMAWFILEIMPORTFILETYPE: File Type");

      headblk.addField("DOC_CLASS").
              setSize(20).
              setMaxLength(12).
              setUpperCase().
              setMandatory().
              setDynamicLOV("DOC_CLASS").
              setLOVProperty("TITLE",mgr.translate("DOCMAWFILEIMPORTDOCUCLS: Document Class")).
              setCustomValidation("DOC_CLASS","DOC_REV,NUMBER_GENERATOR,NUM_GEN_TRANSLATED,ID1,ID2").
              setLabel("DOCMAWFILEIMPORTDOCCLASS: Doc Class");

      headblk.addField("DOC_NO").
              setSize(20).
              setMaxLength(120).
              setUpperCase().
	           setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO").
              setLabel("DOCMAWFILEIMPORTDOCNO: Doc No");

      headblk.addField("DOC_SHEET").
              setSize(20).
              setMaxLength(10).
              setMandatory().
              setUpperCase().
              setLabel("DOCMAWFILEIMPORTDOCSHEET: Doc Sheet");

      headblk.addField("DOC_REV").
              setSize(20).
              setMaxLength(6).
              setMandatory().
              setUpperCase().
              setLabel("DOCMAWFILEIMPORTDOCREV: Doc Rev");

      headblk.addField("DOC_TITLE").
              setSize(30).
              setMandatory().
              setMaxLength(250).
              setLabel("DOCMAWFILEIMPORTDOCTITLE: Title");

      headblk.addField("STRUCTURE","Number").
              setCheckBox("0,1").
              setLabel("FILEIMPORTSTRUCTURE: Structure");
              //Note .setFunction() is not required here. If used will cause the value of structure not to be propagated to the DB level.

      headblk.addField("IMPORTED","Number").
              setReadOnly().
              setCheckBox("0,1").
              setFunction("nvl(IMPORTED,0)").
              setLabel("DOCMAWFILEIMPORTIMPORTED: Imported");

      headblk.addField("OBJCONNECETD","Number").
              setLabel("DOCMAWFILEIMPORTOBJCONNECTED: Object Connected").
              setReadOnly().
              setCheckBox("0,1").
              setFunction("DOC_REFERENCE_OBJECT_API.Check_Objects_Connected(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :DOC_REV)"); //Bug Id 77798	

      headblk.addField("FND_USER").
              setSize(20).
              setReadOnly().
              setLabel("DOCMAWFILEIMPORTUSER: User");

      headblk.addField("FILE_TYPE_LIST").
              setHidden().
              setFunction("''");

      headblk.addField("NUMBER_GENERATOR").
              setHidden().
              setFunction("DOC_CLASS_DEFAULT_API.Get_Default_Value_(:DOC_CLASS,'DocTitle','NUMBER_GENERATOR')");
	      

      headblk.addField("NUM_GEN_TRANSLATED").
              setSize(20).
              setMaxLength(30).
              setReadOnly().
              setFunction("''").
              setLabel("FILEIMPORTNUMGENERATOR: Number Generator");

      headblk.addField("BOOKING_LIST").
              setSize(20).
              setMaxLength(20).
              setUpperCase().
              setLOV("BookListLov.page", "ID1,ID2").//Bug Id 73606
              setLabel("FILEIMPORTBOOKINGLIST: Booking List");

      headblk.addField("ID1").
              setSize(20).
              setMaxLength(10).
              setUpperCase().
              setLOV("Id1Lov.page").
              setLabel("FILEIMPORTNUMBERCOUNTERID1: Number Counter ID1");

      headblk.addField("ID2").
              setSize(20).
              setMaxLength(10).
              setUpperCase().
              setLOV("Id2Lov.page","ID1").
              setLabel("FILEIMPORTNUMBERCOUNTERID2: Number Counter ID2");

      headblk.addField("OBJ_LU_NAME").
              setHidden().
              setSize(30);

      headblk.addField("OBJ_KEY_REF").
              setHidden().
              setSize(500);

      headblk.addField("OUT_STR1").
              setHidden().
              setFunction("''");

      headblk.addField("FILE_IMPORT_TITLE_PATTERN").
              setHidden().
              setFunction("DOC_CLASS_DEFAULT_API.Get_Default_Value_(:DOC_CLASS,'DocFileImport','FILE_IMPORT_TITLE_PATTERN')");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWFILEIMPORTFILEIMPORT: File Import"));
      headtbl.unsetEditable();
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headblk.setView("DOC_FILE_IMPORT");
      headblk.defineCommand("DOC_FILE_IMPORT_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();
      headbar.addCustomCommand("viewLocalFile", mgr.translate("DOCMAWFILEIMPORTVIEWLOCAL: View Local File..."));
      headbar.addSecureCustomCommand("showFileSelectionDialog", mgr.translate("DOCMAWFILEIMPORTOPENFILE: Open File(s)"),"DOC_FILE_IMPORT_API.New__"); //Bug Id 70286
      headbar.addSecureCustomCommand("importFile", mgr.translate("DOCMAWFILEIMPORTIMPORTFILE: Import File(s)"),"DOC_FILE_IMPORT_API.New__");  //Bug Id 70286
      headbar.addCustomCommand("transferToDocumentInfo", mgr.translate("DOCMAWFILEIMPORTDOCINFO: Document Info..."));
      headbar.addSecureCustomCommand("viewOriginal", mgr.translate("DOCMAWFILEIMPORTVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");
      headbar.addSecureCustomCommand("viewOriginalWithExternalViewer", mgr.translate("DOCMAWDOCREFERENCEVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("printDocument", mgr.translate("DOCMAWFILEIMPORTPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");  //Bug Id 70286
      headbar.addSecureCustomCommand("objectConnections", mgr.translate("DOCMAWFILEIMPORTOBJCONN: Object Connection"),"DOC_REFERENCE_OBJECT_API.New__");  //Bug Id 70286
      headbar.addSecureCustomCommand("comfirmDeletion", mgr.translate("DOCMAWFILEIMPORTDELETERECS: Delete Records"),"DOC_FILE_IMPORT_API.Remove__");  //Bug Id 70286
      headbar.addSecureCustomCommand("modifyDocumentClass", mgr.translate("DOCMAWFILEIMPORTMODIFYDOCCLASS: Modify Document class..."),"DOC_FILE_IMPORT_API.Modify__");  //Bug Id 70286

      headbar.enableMultirowAction();

      headbar.removeFromMultirowAction("showFileSelectionDialog");
      headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");
      headbar.removeFromMultirowAction("viewLocalFile");

      headbar.defineCommand(headbar.CANCELNEW, "cancelNew");
      headbar.defineCommand(headbar.DUPLICATEROW, "duplicateRow");
      headbar.defineCommand(headbar.NEWROW, "showFileSelectionDialog");
      headbar.defineCommand(headbar.SAVENEW, "saveNew");
      headbar.defineCommand(headbar.EDITROW, "editRow");


      //
      // Block for adding new files
      //

      docblk = mgr.newASPBlock("DOC");
      docbar = mgr.newASPCommandBar(docblk);
      docset = docblk.getASPRowSet();

      docblk.addField("DEF_DOC_CLASS").
             setSize(20).
             setMaxLength(12).
             setUpperCase().
             setMandatory().
             setDynamicLOV("DOC_CLASS").
             setFunction("''").
             setLOVProperty("TITLE",mgr.translate("DOCMAWFILEIMPORTDOCUCLS: Document Class"));

      docbar.disableCommand(docbar.SAVENEW);
      docbar.disableCommand(docbar.SAVERETURN);
      docbar.disableCommand(docbar.CANCELNEW);
      docbar.defineCommand(docbar.SAVERETURN, "createNewFileImports");
      docbar.addCustomCommand("importSelectedFiles", "");
      docbar.defineCommand(docbar.CANCELNEW, "cancelNew");

      doclay = docblk.getASPBlockLayout();
      doclay.setDefaultLayoutMode(doclay.CUSTOM_LAYOUT);


      //
      // Dummy block
      //

      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("STR_IN");
      dummyblk.addField("STR_OUT");
      dummyblk.addField("STR_INOUT");
      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
   }


   public void  adjust() throws FndException
   {
      ASPManager mgr = getASPManager();

      eval(docblk.generateAssignments());

      if (headlay.isMultirowLayout() || headlay.isSingleLayout())
      {
         headbar.addCommandValidConditions("objectConnections", "IMPORTED", "Disable", "0");
         headbar.addCommandValidConditions("EditRow", "IMPORTED", "Disable", "1");
         headbar.addCommandValidConditions("viewOriginal", "IMPORTED", "Disable", "0");
         headbar.addCommandValidConditions("printDocument", "IMPORTED", "Disable", "0");
         headbar.addCommandValidConditions("viewLocalFile", "IMPORTED", "Disable", "1");
         headbar.addCommandValidConditions("importFile", "IMPORTED", "Disable", "1");
         headbar.addCommandValidConditions("transferToDocumentInfo", "IMPORTED", "Disable", "0");
         headbar.addCommandValidConditions("comfirmDeletion", "DELETE_REC_VALIDATE", "Enable", "1");
      }

      //Bug Id 79368 Start 
      if (headlay.isSingleLayout() && (!("1".equals(headset.getRow().getValue("IMPORTED"))))) 
         headbar.removeCustomCommand("transferToDocumentInfo");
      //Bug Id 79368 End   

      if (headlay.isFindLayout())
         mgr.getASPField("FND_USER").setDynamicLOV("PERSON_INFO_LOV");
      if (!headlay.isNewLayout())
      {
         mgr.getASPField("NUM_GEN_TRANSLATED").setHidden();
      }
      if (headlay.isNewLayout() || headlay.isEditLayout())
      {
         mgr.getASPField("FND_USER").setReadOnly();
	 //Bug Id 74966, Start
	 if (!(show_file_dialog))
            appendDirtyJavaScript("enableFields();\n"); 
	 //Bug Id 74966, End
      }
      else
      {
	  mgr.getASPField("FND_USER").unsetReadOnly();
	  mgr.getASPField("BOOKING_LIST").setHidden(); //Bug Id 74966
      }


      if (headset.countRows()< 1)
      {
         headbar.disableMultirowAction();
      }
      else
      {
         headbar.enableMultirowAction();
      }
      if (this.headset.countRows()>0)
      {
         if (headlay.getLayoutMode()== headlay.NEW_LAYOUT || headlay.getLayoutMode()== headlay.FIND_LAYOUT)
         {
            this.show_delete_cbox = false;
         }
         else
            this.show_delete_cbox = true;
      }
   }


   protected String getDescription()
   {
      return "DOCMAWFILEIMPORTFILEIMPORT: File Import";
   }


   protected String getTitle()
   {
      return "DOCMAWFILEIMPORTFILEIMPORT: File Import";
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      String root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      eval(docblk.generateAssignments());

      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWFILEIMPORTFILEIMPORT: File Import"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"FILE_LIST\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"OBJECT_INSERTED\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_LU_NAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"TEMP_KEY_REF\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRM_DELETE\" value=\"FALSE\">\n");
      out.append("<input type=\"hidden\" name=\"RETURN_OBJ\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"DELETE_ORIGINAL_FILE\" value=\"\">\n");

      // Added by Terry 20140508
      String transfered_lu_name = mgr.getASPContext().readValue("OBJ_LU_NAME");
      transfered_lu_name = mgr.isEmpty(transfered_lu_name) ? "" : transfered_lu_name;
      out.append("<input type=\"hidden\" name=\"TRANSFERED_LU_NAME\" value=\""+ transfered_lu_name +"\">\n");
      // Added end
      
      if (headlay.isMultirowLayout() || headlay.isSingleLayout())
      {
         out.append("<input type=\"hidden\" name=\"SELECTED_DOC_CLASS\" value=\"\">\n");
      }


      out.append(mgr.startPresentation("DOCMAWFILEIMPORTFILEIMPORT: File Import"));

      if (show_delete_cbox)
      {
         out.append("<table border=\"0\"  cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         out.append("<tr onClick=\"javascript:userHitRow()\"><td width=\"8\"></td>");
         out.append("<td >"+fmt.drawCheckbox("DELETE_ORIGINALS","test",bDeleteOrig,"onClick=\"userHitCheckBox()\"")+fmt.drawReadLabel(mgr.translate("DOCMAWFILEIMPORTDELORIFILES: Delete Original Files"))+"<td></tr>");
      }

      if (show_file_dialog)
      {
         doclay.setLayoutMode(doclay.NEW_LAYOUT);

         docbar.disableMinimize();
         out.append("<table border=\"0\"  cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         out.append("    <tr><td colspan=\"3\">"+docbar.showBar()+"<td></tr>");
         out.append("    <tr><td width=\"8\"></td>");
         out.append("       <td>");

         // dialog starts
         out.append("       <table border=0 class=\"BlockLayoutTable\" border=\"0\" width=\"100%\">\n");
         out.append("           <tr>\n");
         out.append("               <td width=\"100%\">\n");
         out.append("                  <table  border=\"0\">\n");
         out.append("                     <tr>\n");
         out.append("                         <td></td>\n");
         out.append("                         <td align=\"right\">"+fmt.drawReadLabel(mgr.translate("DOCMAWFILEIMPORTDEFDOC: Default Document Class:"))+"</td>\n");
         docset.addRow(mgr.newASPBuffer());
         out.append("                         <td  colspan=\"2\" align=\"left\">&nbsp;"+fmt.drawTextField("DEF_DOC_CLASS", ctx.readValue("DEF_DOC_CLASS", default_docu_class) ,mgr.getASPField("DEF_DOC_CLASS").getTag())+"</td>\n");
         docset.clear();
         out.append("                     </tr>\n");
         out.append("                     <tr>\n");
         out.append("                         <td></td>\n");
         out.append("                         <td align=\"right\">"+fmt.drawReadLabel(mgr.translate("DOCMAWFILEIMPORTFILESEL: Files Selected")) +"</td>\n");
         out.append("                         <td  rowspan=\"5\" align=\"left\" valign=\"middle\">&nbsp;"+fmt.drawPreparedSelect("SELECTED_FILES","","size=15 multiple style='width:600px'",false)+"\n");
         out.append("                         </td>\n");
         out.append("                     </tr>\n");

         // embed DnD area..
         out.append("                     <tr>\n");
         out.append("                         <td></td>\n");
         out.append("                         <td>\n");


         if (mgr.isExplorer())         
         {
            out.append(DocmawUtil.getClientMgrObjectStr());

            String drop_here = mgr.translate("DOCMAWFILEIMPORTDROPOBJECTDROPHERE: To add files for import, drop them here");
            String unicode_msg = mgr.translate("DOCMAWFILEIMPORTUNICODECHARS: The Drag and Drop Area does not support adding files with Unicode characters and any such file will be excluded. Do you want to continue?");

            out.append("<script type=\"text/JavaScript\" for=\"DropArea\" event=\"DocmanDragDrop()\">\n");
            out.append("function document.form.DropArea::OLEDragDrop(data, effect, button, shift, x, y){\n");
            out.append("    var filesDropped = \"\";\n");
            out.append("    var path = \"\";\n");
            out.append("    var fullFileName;\n");
            out.append("    var foundUnicodeFiles = false;\n");
            //Bug id 88317 Start
            out.append("	var ret =__connectURL('");  
            out.append(mgr.getURL());
            out.append("?execute=checkCreateNewAllowed');\n");

            out.append("	  if (ret==\"true\")\n");        
            out.append("   {\n");
            out.append("      alert(\"");
            out.append(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
            out.append("\")\n");
             out.append("          return;\n");
            out.append("   }\n");
            
            //Bug id 88317 End
            out.append("    if(data.GetFormat(15)){\n");
            out.append("        var e = new Enumerator(data.Files);\n");
            out.append("        while(!e.atEnd())\n");
            out.append("        {\n");
            out.append("            fullFileName =  \"\" + e.item(); \n");
            out.append("            if (fullFileName.indexOf(\"?\") != -1)\n");
            out.append("                foundUnicodeFiles = true;\n");
            out.append("            else\n");
            out.append("                filesDropped += fullFileName + \"|\"; \n");
            out.append("            e.moveNext();\n");
            out.append("        }\n");
            out.append("        filesDropped = filesDropped.substr(0,filesDropped.length-1);\n"); //remove last '|'
            out.append("        document.form.DropArea.Backcolor = oldBackColor;\n");
            out.append("        document.form.DropArea.Status = \"" + drop_here + "\";\n");
            out.append("        if (foundUnicodeFiles)\n");
            out.append("        {\n");
            out.append("            if (!confirm(\"" + unicode_msg + "\"))\n");
            out.append("                return;\n");
            out.append("        }\n");
            out.append("        if (filesDropped != \"\")\n");
            out.append("        {\n");
            out.append("            addGivenFiles(filesDropped);\n");
            out.append("        }\n");
            out.append("    }\n");

            

            out.append("}\n");

            out.append(DocmawUtil.writeOleDragOverFunction(drop_here,
                                                           mgr.translate("DOCMAWFILEIMPORTDROPOBJECTACCEPTED: Files to drop"),
                                                           mgr.translate("DOCMAWFILEIMPORTONLYFILESDROP: Only files are accepted")));                                                            
            
            out.append("</script>  \n");
         
            out.append("<table width=150 height=150>\n");
            out.append("   <tr>\n");
            out.append("      <td>\n");
            out.append(DocmawUtil.drawDnDArea("", ""));
            out.append("      </td>\n");
            out.append("   </tr>\n");
            out.append("</table>\n");
         }

         out.append("                        </td>\n");
         out.append("                        <td>\n");
         out.append("                          <table height=200>\n");
         out.append("                              <tr>\n");
         out.append("                                 <td>"+fmt.drawButton("DLG2",mgr.translate("DOCMAWFILEIMPORTSELSAVEREVIEW: Save & Review"),"onClick='saveAndReview()'")+"</td>\n");
         out.append("                              </tr><tr>\n");
         out.append("                                 <td >"+fmt.drawButton("DLG1",mgr.translate("DOCMAWFILEIMPORTSELIMPORTNOW: Import Now  "),"onClick='importNow()'")+"</td>\n");
         out.append("                              </tr><tr>\n");
         out.append("                                 <td>"+fmt.drawButton("DLG2",mgr.translate("DOCMAWFILEIMPORTSELADDFILES: Add Files..."),"onClick='addFiles()'")+"</td>\n");
         out.append("                              </tr><tr>\n");
         out.append("                                 <td >"+fmt.drawButton("DLG1",mgr.translate("DOCMAWFILEIMPORTSELREMOVE: Remove Files  "),"onClick='removeFromSelectBox()'")+"</td>\n");
         out.append("                              </tr><tr>\n");
         out.append("                                 <td>"+fmt.drawButton("DLG1",mgr.translate("DOCMAWFILEIMPORTSELCANCEL: Cancel  "),"onClick='leaveSelectFileDlg()'")+"</td>\n");
         out.append("                              </tr>\n");
         out.append("                           </table>\n");
         out.append("                        </td>\n");

         out.append("                     </tr>\n");
         out.append("                  </table>\n");
         out.append("               </td>\n");
         out.append("           </tr>\n");
         out.append("       </table>\n");
         out.append("     </td>\n");
         out.append("   </tr>\n");
         out.append("</table>\n");
         
      }

      if (headlay.isVisible() && !show_file_dialog)
      {
         if (headlay.isMultirowLayout() && headset.countRows()>0)
         {
            out.append(headbar.showBar());
            out.append(headlay.generateDataPresentation());
         }
         else
            out.append(headlay.show());
      }

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("<script LANGUAGE=\"JavaScript\">\n");


      //
      //  CLIENT FUNCTIONS
      //

      if (!mgr.isEmpty(error_message))
         out.append("alert(\""+error_message+"\");\n");

      

      if (show_file_dialog)
      {
         String file_types_list = getFileTypes();
         out.append("file_types = '" + file_types_list + "';\n");
         out.append("var line = \"--------------------------------------------------------------------\";\n");
         out.append("var no_selected_files = 0;\n");
         out.append("var single_file =  \" " + mgr.translateJavaScript("DOCMAWFILEIMPORTSINGLEFILE: File") + ")\";\n"); //Bug Id 80912
         out.append("var multiple_files =  \" " + mgr.translateJavaScript("DOCMAWFILEIMPORTMULTIPLEFILES: Files") + ")\";\n"); //Bug Id 80912
         out.append("document.forms[0].SELECTED_FILES.options[0] = new Option(line + \"(0 \" + multiple_files + line, 0);\n");

         out.append("function setFileCount(n)\n");
         out.append("{ \n");
         out.append("   var file_label = multiple_files;\n");
         out.append("   if (n == 1)\n");
         out.append("      file_label = single_file;\n");
         out.append("   document.forms[0].SELECTED_FILES.options[0].text = line + \"(\" + n + file_label + line;\n");
         out.append("} \n");

         if ("YES".equals(mgr.getQueryStringValue("FROM_OTHER")))
         {
            if (!mgr.isEmpty(fileNames))
               out.append("addGivenFiles(\"" + fileNames + "\");\n");
            else if (!mgr.isEmpty(sPath))
               out.append("addFilesInDirectory(\"" + sPath + "\");\n");

            if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
               out.append(" document.forms[0].DEF_DOC_CLASS.value = \"" + mgr.getQueryStringValue("DOC_CLASS") + "\"\n");
            
            // Added by Terry 20140508
            // Change lov of field DEF_DOC_CLASS
            if (!mgr.isEmpty(mgr.getASPContext().readValue("OBJ_LU_NAME")))
            {
               appendDirtyJavaScript("function lovDefDocClass(i,params)\n");
               appendDirtyJavaScript("{\n");
               appendDirtyJavaScript("   if(params) param = params;\n");
               appendDirtyJavaScript("   else param = '';\n");
               appendDirtyJavaScript("   var enable_multichoice =(true && DOC_IN_FIND_MODE);\n");
               appendDirtyJavaScript("   var key_value = (getValue_('DEF_DOC_CLASS',i).indexOf('%') !=-1)? getValue_('DEF_DOC_CLASS',i):'';\n");
               appendDirtyJavaScript("   openLOVWindow('DEF_DOC_CLASS',i,\n");
               appendDirtyJavaScript("      APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_CLASS_LU_LOV&__FIELD=DEF_DOC_CLASS&__INIT=1&__LOV=Y' + '&__TITLE=" + mgr.URLEncode(mgr.translate("DOCMAWFILEIMPORTDEFDOCCLASS: Default Document Class")) + "&MULTICHOICE='+enable_multichoice+''\n");
               appendDirtyJavaScript("      + '&__KEY_VALUE=' + URLClientEncode(getValue_('DEF_DOC_CLASS',i))\n");
               appendDirtyJavaScript("      + '&DEF_DOC_CLASS=' + URLClientEncode(key_value)\n");
               appendDirtyJavaScript("      + '&LU_NAME=' + ((getValue_('TRANSFERED_LU_NAME',i) == null)? '':URLClientEncode(getValue_('TRANSFERED_LU_NAME',i)))\n");
               appendDirtyJavaScript("      ,550,500,'validateDefDocClass');\n");
               appendDirtyJavaScript("}\n");
            }
            // Added end
         }

         out.append("function checkImportFileType(file_name)\n");
         out.append("{\n");
         out.append("   var file_types = '^' + '" + file_types_list + "' + '^';\n");
         out.append("   var file_ext = \"^\" + file_name.substring(file_name.lastIndexOf('.') + 1).toUpperCase() + \"^\";\n");
         out.append("   return (file_types.indexOf(file_ext) != -1);\n");
         out.append("}\n");


         out.append("function addFilesInDirectory(sDirectory)\n");
         out.append("{\n");
         out.append("   var fso;\n");
         out.append("   var fileSet= \"\";\n");
         out.append("   fso = new ActiveXObject(\"Scripting.FileSystemObject\"); \n");
         out.append("   currentFoloder = fso.getFolder(sDirectory);\n");
         out.append("   e  = new Enumerator(currentFoloder.Files);\n");
         out.append("   if (sDirectory.charAt(sDirectory.length-1)!= \"\\\\\"){\n");
         out.append("      sDirectory += \"\\\\\";\n");
         out.append("   }\n");
         out.append("   while(!e.atEnd()){\n");
         out.append("      fileSet +=  sDirectory + e.item().name + \"|\";\n");
         out.append("      e.moveNext();\n");
         out.append("   }\n");
         out.append("   addGivenFiles(fileSet);\n");
         out.append("}\n");


         out.append("function addFiles()\n");
         out.append("{ \n");

         //Bug id 88317 Start
         out.append("	var ret =__connectURL('");  
         out.append(mgr.getURL());
         out.append("?execute=checkCreateNewAllowed');\n");

         out.append("	  if (ret==\"true\")\n");        
         out.append("   {\n");
         out.append("      alert(\"");
         out.append(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
         out.append("\")\n");
         out.append("   }\n");
         out.append("    else \n");
         out.append("    { \n");
         //Bug id 88317 End


         out.append("   var files = document.form.oCliMgr.ShowFileBrowser(\"\", file_types, true);\n");
         out.append("   addGivenFiles(files);\n");

         out.append("    } \n"); //Bug id 88317

         out.append("}\n");

      }

      // Bug Id 92125, Start
      out.append("function addGivenFiles(files)\n");
      out.append("{ \n");
      out.append("   arrFiles = files.split(\"|\");\n");
      out.append("   bShowFileExtError = \"FALSE\";\n");
      out.append("   for (i=0; i<arrFiles.length ; i++)\n");
      out.append("   {\n");
      out.append("      if(arrFiles[i] != \"\")\n");
      out.append("      {\n");
      out.append("         if(arrFiles[i].lastIndexOf(\".\") > -1)\n");      
      out.append("            document.forms[0].SELECTED_FILES.options[document.forms[0].SELECTED_FILES.length] = new Option(arrFiles[i],document.forms[0].SELECTED_FILES.length);\n");
      out.append("         else\n");
      out.append("            bShowFileExtError = \"TRUE\";\n");
      out.append("      }\n");
      out.append("   }\n");
      out.append("	if (bShowFileExtError==\"TRUE\")\n");        
      out.append("   {\n");
      out.append("      alert(\"");
      out.append(mgr.translate("DOCMAWFILEIMPORTNOEXTALERT: File(s) without extension(s) are not allowed."));
      out.append("\");\n");
      out.append("   }\n");
      out.append("   setFileCount(document.forms[0].SELECTED_FILES.length-1);\n");
      out.append("   updateFileNameField();\n");
      out.append("} \n");
      // Bug Id 92125, End


      out.append("function leaveSelectFileDlg()\n");
      out.append("{\n");
      out.append("   commandSet('DOC.CancelNew','');\n");
      out.append("}\n");


      out.append("function saveAndReview()\n");
      out.append("{\n");
      out.append("   if(document.forms[0].SELECTED_FILES.length - 1 == 0)\n");
      out.append("   {\n");
      out.append("      alert(\""+mgr.translateJavaScript("DOCMAWFILEIMPORTNOFILESSELECTED: No files selected")+"\");\n"); //Bug Id 80912
      out.append("      return;\n");
      out.append("   }\n");
      out.append("   else\n");
      out.append("   {\n");
      out.append("       if (!checkFilesToImport())\n");
      out.append("          return;\n");
      out.append("   }\n");
      out.append("   if (checkDefaultDocumentClass())\n");
      out.append("      commandSet('DOC.SaveReturn','');\n");
      out.append("}\n");


      out.append("function importNow()\n");
      out.append("{\n");
      out.append("   if(document.forms[0].SELECTED_FILES.length - 1 == 0)\n");
      out.append("   {\n");
      out.append("      alert(\""+mgr.translateJavaScript("DOCMAWFILEIMPORTNOFILESSELECTED: No files selected")+"\");\n"); //Bug Id 80912
      out.append("      return;\n");
      out.append("   }\n");
      out.append("   else\n");
      out.append("   {\n");
      out.append("       if (!checkFilesToImport())\n");
      out.append("          return;\n");
      out.append("   }\n");

      //Bug id 88317 Start
      out.append("	var ret =__connectURL('");  
      out.append(mgr.getURL());
      out.append("?execute=checkCreateNewAllowed');\n");
      out.append("	  if (ret==\"true\")\n");        
      out.append("   {\n");
      out.append("      alert(\"");
      out.append(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
      out.append("\")\n");
      out.append("          return;\n");
      out.append("   }\n");
      //Bug id 88317 End
      //Bug Id 72502, Start
      out.append("       if (!validateAdvancedId2())\n");
      out.append("          return;\n");
      //Bug Id 72502, End
      out.append("   if (checkDefaultDocumentClass())\n");
      out.append("   {\n");
      out.append("      f.__DOC_Perform.value = 'importSelectedFiles';\n");
      out.append("      commandSet('DOC.Perform','');\n");
      out.append("   }\n");
      out.append("}\n");


      out.append("function checkDefaultDocumentClass()\n");
      out.append("{\n");
      out.append("   if(document.forms[0].DEF_DOC_CLASS.value == \"\")\n");
      out.append("   {\n");
      out.append("      alert(\""+ mgr.translateJavaScript("DOCMAWFILEIMPORTDEFDOCCLASSNOTSEL: A Default Document Class was not selected")+"\");\n"); //Bug Id 80912
      out.append("      return false;\n");
      out.append("   }\n");
      out.append("   else\n");
      out.append("   {\n");
      out.append("       var class_exists = __connect('" + mgr.getURL() + "?VALIDATE=VALIDATE_DOC_CLASS' + '&DOC_CLASS=' + document.forms[0].DEF_DOC_CLASS.value);\n");
      out.append("       if(class_exists.indexOf('FALSE') >= 0)\n");
      out.append("       {\n");
      out.append("           alert('" + mgr.translateJavaScript("DOCMAWFILEIMPORTDEFAULTDOCUMENTCLASSNOTEXIST: The Default Document Class specified does not exist.") + "');\n"); //Bug Id 80912
      out.append("           return false;\n");
      out.append("       }\n");
      out.append("       else\n");
      out.append("       {\n");
      out.append("           return true;\n");
      out.append("       }\n");
      out.append("   }\n");
      out.append("}\n");

      //Bug Id 72502, Start
      out.append("function validateAdvancedId2()\n");
      out.append("{\n");
      out.append("       var id_exists = __connect('" + mgr.getURL() + "?VALIDATE=VALIDATE_ID2' + '&DOC_CLASS=' + document.forms[0].DEF_DOC_CLASS.value);\n");
      out.append("       if(id_exists.indexOf('ADVANCEDERROR') >= 0)\n");
      out.append("       {\n");
      out.append("    	    var msg1 = '" + mgr.translateJavaScript("DOCMAWFILEIMPORTNOID2VALIDATE1: Cannot find a suitable Number Counter for ID1 =") + "';\n"); //Bug Id 80912
      out.append("    	    var msg2 = '" + mgr.translateJavaScript("DOCMAWFILEIMPORTNOID2VALIDATE2: Specify ID2 or make sure that a default Number Counter exists.") + "';\n"); //Bug Id 80912
      out.append("          alert(msg1 + ' ' + id_exists.substring(id_exists.indexOf('^') + 1) + '. ' + msg2);\n");   
      out.append("          return false;\n");
      out.append("       }\n");
      out.append("       else\n");
      out.append("       {\n");
      out.append("           return true;\n");
      out.append("       }\n");
      out.append("}\n");
      //Bug Id 72502, End

      out.append("function checkFilesToImport()\n");
      out.append("{\n");
      out.append("    var msg1 = '" + mgr.translateJavaScript("DOCMAWFILEIMPORTFILETYPENOTVALID1: The file") + "';\n"); //Bug Id 80912
      out.append("    var msg2 = '" + mgr.translateJavaScript("DOCMAWFILEIMPORTFILETYPENOTVALID2: has an invalid file type.") + "';\n"); //Bug Id 80912
      out.append("    var msg3 = '" + mgr.translateJavaScript("DOCMAWFILEIMPORTFILETYPENOTVALID3: is not a registered file extension") + "';\n"); //Bug Id 80912
      out.append("    var count = document.forms[0].SELECTED_FILES.length;\n");
      out.append("    for(var x = 1; x < count; x++)\n");
      out.append("    {\n");
      out.append("        var file_name = document.forms[0].SELECTED_FILES.options[x].text;\n");   
      out.append("        if (!checkImportFileType(file_name))\n");
      out.append("        {\n");
      out.append("            alert(msg1 + ' ' + file_name + ' ' + msg2 + ' \\\'' + file_name.substring(file_name.indexOf('.') + 1) + '\\\' ' + msg3);\n");   
      out.append("            return false;\n");
      out.append("        }\n");
      out.append("    }\n");
      out.append("    return true;\n");
      out.append("}\n");


      out.append("function removeFromSelectBox()\n");
      out.append("{\n");
      out.append("   for(ix=document.forms[0].SELECTED_FILES.length-1; ix>=0;ix--)\n");
      out.append("   {\n");
      out.append("      if(document.forms[0].SELECTED_FILES.options[ix].selected==true && document.forms[0].SELECTED_FILES.options[ix].value != '0')\n");
      out.append("      {\n");
      out.append("         document.forms[0].SELECTED_FILES.options[ix] = null;\n");
      out.append("      }\n");
      out.append("   }\n");
      out.append("   setFileCount(document.forms[0].SELECTED_FILES.length-1);\n");
      out.append("   updateFileNameField();\n");
      out.append("}\n");


      out.append("function updateFileNameField()\n");
      out.append("{\n");
      out.append("   var fileSeperator;\n");
      out.append("   document.form.FILE_LIST.value =\"\";\n");
      out.append("   for(ix=0;ix<document.forms[0].SELECTED_FILES.length; ix++)\n");
      out.append("   {\n");
      out.append("      fileSeperator=(ix==document.forms[0].SELECTED_FILES.length-1)?\"\":\"|\";\n");
      out.append("      if(document.forms[0].SELECTED_FILES.options[ix].value != '0') \n");
      out.append("      {\n");
      out.append("         document.form.FILE_LIST.value +=document.forms[0].SELECTED_FILES.options[ix].text+fileSeperator;\n");
      out.append("      }\n");
      out.append("   }\n");
      out.append("}\n");


      if (tranfer_to_edm)
      {
         out.append("   window.open(\"");
         out.append(url);
         out.append("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }


      out.append("function validateFileType(i)\n");
      out.append("{\n");
      out.append("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;  \n");
      out.append("   if( !checkFileType(i) ) return;\n");
      out.append("}\n");


      out.append("function validateDefDocClass(i)\n");
      out.append("{  \n");
      out.append("   setDirty();\n");
      out.append("   if( !checkDefDocClass(i) ) return;\n");
      out.append("}\n");

      if (show_objects)
      {
         out.append("openObjectConnections();\n");
      }

      out.append("function openObjectConnections()\n");
      out.append("{    \n");
      out.append("var key_value = (getValue_('RETURN_OBJ',-1).indexOf('%') !=-1)? getValue_('RETURN_OBJ',-1):'';\n");
      out.append("    openLOVWindow('RETURN_OBJ',-1,\n");
      out.append("        '");
      out.append(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
      out.append("docmaw/ObjectConnection.page?SERVICE_LIST=DocReferenceObject'\n");
      out.append("        ,500,500,'insertRecord');\n");
      out.append("}\n");


      out.append("function insertRecord()\n");
      out.append("{\n");
      out.append("   ret_val = document.form.RETURN_OBJ.value;\n");
      out.append("   seperate_pt = ret_val.indexOf('~');\n");
      out.append("   lu_name = ret_val.substr(0,seperate_pt);\n");
      out.append("   key_ref = ret_val.substr(seperate_pt+1,ret_val.length-seperate_pt-1);\n");
      out.append("   document.form.TEMP_LU_NAME.value = lu_name;\n");
      out.append("   document.form.TEMP_KEY_REF.value = key_ref;\n");
      out.append("   document.form.OBJECT_INSERTED.value = \"TRUE\";\n");
      out.append("   submit();\n");
      out.append("}\n");


      out.append("function refreshParent()\n");
      out.append("{\n");
      out.append("   document.form.REFRESH_ROW.value=\"TRUE\"\n");
      out.append("   submit() \n");
      out.append("}\n");

      if (confirm_delete)
      {
         out.append("   getDeleteConfirmation()\n");
         out.append("   function getDeleteConfirmation()\n");
         out.append("   {  \n");
         out.append("      if (confirm(\"");
         out.append(mgr.translateJavaScript("DOCMAWFILEIMPORTCONFIRMDEL: Delete Selected Rows?")); //Bug Id 80912
         out.append("\"))\n");
         out.append("      {\n");
         out.append("         document.form.CONFIRM_DELETE.value=\"TRUE\"\n");
         out.append("         submit();\n");
         out.append("      }     \n");
         out.append("   }\n");
      }

      if (show_delete_cbox)
      {
         out.append("var bHitCheckBox=false;\n");

         out.append("function userHitCheckBox(){\n");
         out.append("   bHitCheckBox = true;\n");
         out.append("   setDeleteOrig();\n");
         out.append("}\n");

         out.append("function setDeleteOrig(){\n");
         out.append("   if (document.form.DELETE_ORIGINALS.checked){\n");
         out.append("      document.form.DELETE_ORIGINAL_FILE.value=\"YES\"\n");
         out.append("   }else{\n");
         out.append("      document.form.DELETE_ORIGINAL_FILE.value=\"NO\"\n");
         out.append("   }\n");
         out.append("}\n");

         out.append("function userHitRow(){\n");
         out.append("   if(!bHitCheckBox){\n");
         out.append("      document.form.DELETE_ORIGINALS.checked= !document.form.DELETE_ORIGINALS.checked; \n");
         out.append("      setDeleteOrig();\n");
         out.append("   }else\n");
         out.append("      bHitCheckBox = false;\n");
         out.append("}\n");
      }


      if (bShowDocClasses)
      {
          out.append("lovDocClass(-1);\n");
          out.append("function lovDocClass(i)\n");
          out.append("{\n");
          out.append("        openLOVWindow('SELECTED_DOC_CLASS',i,\n");
          out.append("        '");
          out.append(root_path);
          out.append("common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_CLASS&__FIELD="+mgr.URLEncode (mgr.translateJavaScript("DOCMAWFILEIMPORTDOCCLASS2: Documnet Class"))+"&__INIT=1&__AUTO%5FSEARCH=N'\n"); //Bug Id 80912
          out.append("        ,500,500,'clientModifyDocumentClass');\n");
          out.append("}\n");

          out.append("function clientModifyDocumentClass() {\n");
          out.append("   f.__HEAD_Perform.value = 'modifyDocumentClass';\n");
          out.append("   commandSet('HEAD.Perform','');\n");
          out.append("}\n");
      }


      if (bQuitMultiSel)
      {
         out.append("   alert(\"");
         out.append(sQuitMessage);
         out.append("\");\n");
      }
      //Bug Id 74966, Start
      appendDirtyJavaScript("function enableFields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.NUM_GEN_TRANSLATED.value =\""+sNumberGeneratorTrans+"\";\n");
      appendDirtyJavaScript("   if (\""+sNumberGenerator+"\"==\"ADVANCED\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (document.form.ID1.value ==\"\")\n");
      appendDirtyJavaScript("         document.form.ID1.readOnly=0;\n");
      appendDirtyJavaScript("       else  \n"); 
      appendDirtyJavaScript("          document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("        document.form.ID2.readOnly=0;\n");
      appendDirtyJavaScript("        document.form.BOOKING_LIST.readOnly=0;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else  \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       document.form.ID1.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.ID2.readOnly=1;\n");
      appendDirtyJavaScript("       document.form.BOOKING_LIST.readOnly=1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateDocClass(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM',i)=='QueryMode__' ) \n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if(!checkDocClass(i) ) return;\n");
      appendDirtyJavaScript("    var r = __connect('" + mgr.getURL() + "?VALIDATE=DOC_CLASS'+'&DOC_CLASS=' + URLClientEncode(getValue_('DOC_CLASS',i)));\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'DOC_REV',i,'doc_rev') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("      assignValue_('DOC_REV',i,0);\n");
      appendDirtyJavaScript("      assignValue_('NUMBER_GENERATOR',i,1);\n");
      appendDirtyJavaScript("      assignValue_('NUM_GEN_TRANSLATED',i,2);\n");
      appendDirtyJavaScript("      assignValue_('ID1',i,3);\n");
      appendDirtyJavaScript("      assignValue_('ID2',i,4);\n");
      appendDirtyJavaScript("      assignValue_('STRUCTURE',i,5);\n");
      appendDirtyJavaScript("      assignValue_('DOC_SHEET',i,6);\n");//Bug Id 77725
      appendDirtyJavaScript("    }\n");
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
      out.append("</script>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
