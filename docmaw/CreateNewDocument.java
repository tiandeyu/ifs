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
*  File        : CreateNewDocument.java
*  Converted   : Bakalk 2001-03-13  - Created Using the ASP file CreateNewDocument.asp
*  Modified    : Bakalk 2001-04-17  - Made "Doc Class Desc" read_only, avoid "Doc Class" and
*                                     "File Type" taking 'null'.
* Prsalk  2003-02-03 - Added Sheet Number (doc_sheet)
* inoslk  2003-04-11 - Changes to implement Create New Document.
* inoslk  2003-04-28 - Changes to implement Create New Document XEDM.
* bakalk  2003-07-10 - Fixed 1. file not uploading when title edited.
*                            2. having system error when file not selected properly.
*                            3. file type not read only.
*                            4. Not moving to DocIssue page after creating documents.
*                      Added a new button "Finish and Create new Document" and change the button "Create new Document"
*                      into "Finish". Now you can create as many document as wanted using these button.
*                      After creation of Documents will Docissue show all the document created here.
* bakalk  2003-07-21 - Added configurable document no.
* bakalk  2003-07-25 - Added doc no field so that user can enter his own number
*                      when the Number generating type is of Standard.
*                      Canged the lay out in second step in accordance with IFS GUI standards.
* bakalk  2003-08-14 - Changed the labels on the buttons in the second step.
* inoslk  2003-08-27 - Call ID 101731: Modified doReset() and clone(). Removed unused variables.
* nisilk  2003-10-06   Added some validations for Number Generator support
* bakalk  2003-10-18   Call Id: 106771, Made some fields look readOnly.(they were already readOnly).
* bakalk  2003-10-22   Call Id: 108701, Now u cannot proceed to 2nd step if u ve selected some file type which is not in edm basic.
*                      Added a new step which shows the current status : whether u r doing file operation,or moving to first step again
*                      or u r moving to Document info. Fixed some layout problem in all steps.
* bakalk  2003-10-24   Call Id: 108701, Added a titlte for second step too. Shrinked the image since its height is too much.
* DIKALK  2004-06-23   Merged Bug Id 44951.
* SUKMLK  2004-09-16   Added a structure checkbox to enable structure attribute when creating a document.
* DIKALK  2004-06-23   Merged Bug Id 44951.
* KARALK  2005-03-30   Call Id 122533. The File selector dialog should list only those applications configured in Edm basics
* SUKMLK  2005-07-14   Fixed call 125663. Now docmaw stores a modified file name in USER_FILE_NAME
*                       if the extension and filetype dont match.
* RUCSLK  2006-01-05   Fixed Call 130709. 
* RUCSLK  2006-01-20   Merged Bug Id 55471.
* CHODLK  2006-03-19   Merged Patch No : 56382 - Create new document wizard missing sign for mandatory field.
* AMNALK  2006-03-27   Fixed Call Id 137895.
* RUCSLK  2006-03-28   Fixed Call Id 137993. New JavaScript Function, setFocus to set the focus to DOC_CLASS field.
* NIJALK  2006-10-13   Bug 61028, Increased the maxLength of field FIRST_SHEET_NO.
* JANSLK  2007-08-08   Changed all non-translatable constants.
* ASSALK  2007-08-08   Merged Bug Id 64906, Modified getFileTypes() to retrieve all the file types
* NaLrlk  2007-08-09   XSS Correction.
* ASSALK  2007-08-15   Merged Bug Id 66906, Added the ORDER BY clause to the select statement at getFileTypes().
* DinHlk  2007-08-23   Call 147581, Corrected a Localization Error.
* AMNALK  2009-08-17   Bug Id 85187, Modify mynewRow() to give a descriptive error message when application isn't registered
* -----------------------APP7.5 SP7---------------------------------------------------
* ARWILK  2010-07-21   Bug Id 73606, Modified field BOOKING_LIST lov to filter from ID1 and ID2
* DULOLK  2010-09-16   Bug Id 92125, Modified js method buttonpressedok() to disallow files without extensions.
* ------------------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;

public class CreateNewDocument extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.CreateNewDocument");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter   fmt;
   private ASPContext         ctx;
   private ASPLog             log;
   private ASPBlock           headblk;
   private ASPRowSet          headset;
   private ASPCommandBar      headbar;
   private ASPTable           headtbl;
   private ASPBlockLayout     headlay;
   private ASPBlock           dummyblk;


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPBuffer docissue_buff;
   private ASPBuffer temp;
   private ASPQuery q;

   private boolean openBrowsWindow;
   private boolean saveOperation;
   private boolean savedItem;
   private boolean bPerform;
   private boolean bTransferToEDM;

   private String root_path;
   private String sClientFunc;
   private String sApplicationSettings;
   private String doc_class;
   private String title;
   private String local_path;
   private String file_type;
   private String ext;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String doc_type;
   private String txt;
   private String sFilePath;
   private String path;
   private String re;
   private String local_file_name;
   private int structure;

   private int create_NewFN;
   private boolean bFromPortlet = false;

   private String val; //bug id 88317

   //===============================================================
   // Construction
   //===============================================================
   public CreateNewDocument(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      mgr.setPageNonExpiring();

      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog();
      local_file_name = ctx.readValue("LOCAL_FILENAME");

      //variable initialization
      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      openBrowsWindow = true;
      saveOperation = false;
      savedItem = false;
      bTransferToEDM = false;
      bPerform = false;
      sClientFunc = "";
      sApplicationSettings = ctx.readValue("APPSET");
      docissue_buff = ctx.readBuffer("DOCISSUES");

      doc_class = "";
      title     = "";
      local_path ="";
      file_type  = "";

      //Bug id 88317 Start

      if( !mgr.isEmpty(mgr.getQueryStringValue("execute")) )
         execute(); 
      //Bug id 88317 End



      if (mgr.dataTransfered())
      {         
         doc_class=mgr.getTransferedData().getBufferAt(0).getValueAt(0);
         file_type=mgr.getTransferedData().getBufferAt(0).getValueAt(1);
         local_path=mgr.getTransferedData().getBufferAt(0).getValueAt(2);
         title=mgr.getTransferedData().getBufferAt(0).getValueAt(3);
         doc_sheet=mgr.getTransferedData().getBufferAt(0).getValueAt(4);
         doc_rev=mgr.getTransferedData().getBufferAt(0).getValueAt(5);
         saveItem( doc_class, file_type, local_path,title, doc_sheet, doc_rev);         
      }
      else if (!mgr.isEmpty(mgr.readValue("REFERRER")) && !mgr.isEmpty(mgr.readValue("FILE_NAME")))
      {
         bFromPortlet = true;
         importFile();
      }
      else if (!mgr.isEmpty(mgr.readValue("FROM_EDMMACRO")))
      {
         redirectTo();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("FINISH_NEW")||mgr.buttonPressed("FINISH"))
      {
         saveOperation = true;
         openBrowsWindow = false;
         if (mgr.buttonPressed("FINISH_NEW"))
         {
            ctx.writeFlag("GOTO_ISSUE",false);
         }
         else
         {
            ctx.writeFlag("GOTO_ISSUE",true);
         }

         doc_class = mgr.readValue("DOC_CLASS");
         title     = mgr.readValue("TITLE");
         file_type = mgr.readValue("FILE_TYPE");

         if (!mgr.isEmpty(doc_class) && !mgr.isEmpty(title) && !mgr.isEmpty(file_type))
            okSaveItem();
         else if (mgr.isEmpty(doc_class))
            mgr.showError(mgr.translate("DOCMAWCREATENEWDOCUMENTDOCCLSEMY: Document Class must have a value"));
         else if (mgr.isEmpty(title))
            mgr.showError(mgr.translate("DOCMAWCREATENEWDOCUMENTTITLEEMY: Title must have a value"));
         else if (mgr.isEmpty(file_type))
            mgr.showError(mgr.translate("DOCMAWCREATENEWDOCUMENTFILEEMY: File Type must have a value"));
      }
      else if (mgr.buttonPressed("CANCELOK"))
         backToForm();
      else if ("OK".equals(mgr.readValue("CONFIRMSELECT")))
      {
         local_path = mgr.readValue("FILEPATH");
         local_file_name = mgr.readValue("BROWSFILE");

         path = new String(local_path);
         re ="/~/gi";
         //re= "~";
         path = replaceString(path, re, "\\");// path.replace(re,"\\")
         openBrowsWindow = false;
         mynewRow(path, mgr.readValue("FILENAME"), mgr.readValue("FILENAME"), mgr.readValue("FILETYPE"));
      }
      else if (mgr.buttonPressed("CANCEL"))
         cancel();

      ctx.writeValue("APPSET", sApplicationSettings);

      adjust();

      if (this.docissue_buff != null)
      {
         ctx.writeBuffer("DOCISSUES", docissue_buff);
      }

      ctx.writeValue("LOCAL_FILENAME", local_file_name);
      ctx.writeValue("REFERRER", ctx.readValue("REFERRER", mgr.readValue("REFERRER")));
   }


   public void redirectTo()
   {
      ASPManager mgr = getASPManager();
      if (ctx.readFlag("GOTO_ISSUE",false))
      {
         mgr.transferDataTo("DocIssue.page",ctx.readBuffer("DOCISSUES"));
      }
      else
      {
         this.backToForm();
      }
   }


   public void okSaveItem()
   {
      ASPManager mgr = getASPManager();

      doc_class  = mgr.readValue("DOC_CLASS");
      title      = mgr.readValue("TITLE");
      file_type  = mgr.readValue("FILE_TYPE");
      local_path = mgr.readValue("LOCAL_PATH");
      doc_sheet  = mgr.readValue("FIRST_SHEET_NO");
      doc_rev    = mgr.readValue("FIRST_REVISION");

      String isChecked = mgr.readValue("STRUCTURE");

      if ("ISCHECKED".equals(isChecked))
      {
         structure = 1;
      }
      else
      {
         structure = 0;
      }

      savedItem = true;
      saveOperation = false;
      saveItem(doc_class, file_type, local_path, title, doc_sheet, doc_rev);
   }


   private void importFile()
   {
      ASPManager mgr = getASPManager();

      String path = DocmawUtil.getPath(mgr.readValue("FILE_NAME"));
      String base_file_name = DocmawUtil.getBaseFileName(mgr.readValue("FILE_NAME"));
      String title = DocmawUtil.getBaseFileName(mgr.readValue("FILE_NAME"));
      String file_type = getFileType(DocmawUtil.getFileExtention(mgr.readValue("FILE_NAME")));
      local_file_name = mgr.readValue("FILE_NAME");

      mynewRow(path, base_file_name, title, file_type);
   }


   private String getFileType(String file_ext)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomFunction("GETFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_1");
      cmd.addParameter("DUMMY1", file_ext);
      trans = mgr.perform(trans);
      return trans.getValue("GETFILETYPE/DATA/OUT_1");      
   }


   //This function will prepare a new record to be saved
   public void mynewRow(String path, String base_file_name, String title, String file_type)
   {

      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD", "DOC_TITLE_API.New__", headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);

      if (mgr.isEmpty(base_file_name))
      {
         openBrowsWindow = true;
         trans.clear();
         mgr.showError(mgr.translate("DOCMAWCREATENEWDOCUMENTFILENOTEXIST: Please Select a File Name and Press OK"));
      }
      else
      {
         saveOperation   = true;
         openBrowsWindow = false;
         headset.setValue("TITLE", title);
         headset.setValue("LOCAL_PATH",path);
         headset.setValue("FILE_TYPE", file_type);


         trans.clear();
         if (mgr.isEmpty(file_type)) {
            //Bug Id 85187, Start
	    openBrowsWindow = true;
            saveOperation   = false;
	    savedItem       = false;
            mgr.showError(mgr.translate("DOCMAWCREATENEWDOCUMENTINVALIDFILETYPE: The application type is not registered in Document Management/EDM Basic. Please contact the System Administrator."));
	    //Bug Id 85187, End
         }
      }
   }

   //This function will save the record of the file selected in DOC_TITLE and EDM_FILE
   public void saveItem(String doc_class_,String file_type,String local_path,String title, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();

      if ("".equals(mgr.readValue("DOC_CLASS")))
      {
         temp = headset.getRow();
         savedItem = false;
         openBrowsWindow = false;
         saveOperation = true;
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWDOCUMENTDOCCLASSMAN: Doc Class Should have a value"));

         temp.setValue("DOC_CLASS",doc_class_);
         temp.setValue("FILE_TYPE",file_type);
         temp.setValue("LOCAL_PATH",local_path);
         headset.setRow(temp);
      }
      else if ("".equals(mgr.readValue("FILE_TYPE")))
      {
         temp = headset.getRow();
         savedItem = false;
         openBrowsWindow = false;
         saveOperation = true;
         mgr.showAlert(mgr.translate("DOCMAWCREATENEWDOCUMENTFILETYPECHK: File Type Should have a value"));

         temp.setValue("DOC_CLASS",doc_class_);
         temp.setValue("FILE_TYPE",file_type);
         temp.setValue("LOCAL_PATH",local_path);
         headset.setRow(temp);
      }
      else
      {
         doc_no    = "";
         doc_type   ="ORIGINAL";
         create_NewFN = 1;

         String view_file_req;
         String view_file_req_decode;
         String sAttr="";

         trans.clear();
         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
         {
            cmd = trans.addCustomFunction("GENNUMBER","DOC_TITLE_API.Generate_Doc_Number","DOC_NO");
            cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));
            cmd.addParameter("BOOKING_LIST",mgr.readValue("BOOKING_LIST"));
            cmd.addParameter("ID1",mgr.readValue("ID1"));
            cmd.addParameter("ID2",mgr.readValue("ID2"));
            cmd.addParameter("ATTR",sAttr);
         }

         cmd = trans.addCustomFunction("VIEWFILEREQ","Doc_Class_Default_API.Get_Default_Value_","VIEW_FILE_REQ");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","VIEW_FILE_REQ");

         cmd = trans.addCustomCommand("DOCSAVE", "DOC_TITLE_API.Create_New_Document_");
         cmd.addParameter("DOC_CLASS",doc_class_);
         if ("ADVANCED".equals(mgr.readValue("NUMBER_GENERATOR")))
            cmd.addReference("DOC_NO","GENNUMBER/DATA");
         else
            cmd.addParameter("DOC_NO",mgr.readValue("DOC_NO"));

         cmd.addParameter("DOC_SHEET",doc_sheet);
         cmd.addParameter("DOC_REV",doc_rev);
         cmd.addParameter("TITLE",title);

         //cmd.addParameter("STRUCTURE",Integer.toString(structure));

         cmd = trans.addCustomFunction("GETFILEEXT","EDM_APPLICATION_API.Get_File_Extention","OUT_1");
         cmd.addParameter("FILE_TYPE",file_type);

         trans=mgr.perform(trans);

         view_file_req = trans.getValue("VIEWFILEREQ/DATA/VIEW_FILE_REQ");
         doc_no = trans.getValue("DOCSAVE/DATA/DOC_NO");
         doc_sheet = trans.getValue("DOCSAVE/DATA/DOC_SHEET");
         doc_rev= trans.getValue("DOCSAVE/DATA/DOC_REV");
         String file_ext = trans.getValue("GETFILEEXT/DATA/OUT_1");

         //mark the document as a structure
         if (structure == 1)
         {
             trans.clear();
             cmd = trans.addCustomCommand("SET_STRUCTURE","DOC_TITLE_API.Set_Structure_");
             cmd.addParameter("DOC_CLASS",doc_class_);
             cmd.addParameter("DOC_NO", doc_no);
             cmd.addParameter("STRUCTURE","1");
             trans = mgr.perform(trans);
         }

         if (docissue_buff==null)
         {
            docissue_buff = mgr.newASPBuffer();
         }

         int no_of_items = docissue_buff.countItems();
         String buffer_name = (no_of_items+1)+"";
         ASPBuffer new_row  = docissue_buff.addBufferAt(buffer_name,no_of_items);
         new_row.addItem("DOC_CLASS",doc_class_);
         new_row.addItem("DOC_NO",doc_no);
         new_row.addItem("DOC_SHEET",doc_sheet);
         new_row.addItem("DOC_REV",doc_rev);

         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

         local_path = replaceString(local_path,"~","\\");

         if (mgr.isExplorer())
         {
            ASPBuffer buf = mgr.newASPBuffer();
            ASPBuffer row = buf.addBuffer("1");
            row.addItem("DOC_CLASS",doc_class);
            row.addItem("DOC_NO",doc_no);
            row.addItem("DOC_SHEET",doc_sheet);
            row.addItem("DOC_REV",doc_rev);
            row.addItem("FILE_TYPE",file_type);
            row.addItem("LOCAL_FILE_NAME",local_file_name);

            sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", "CREATENEWDOC", doc_type, buf);
            bTransferToEDM = true;
         }
         else
         {
            bPerform = true;
            data = mgr.newASPBuffer();
            String sMsg = mgr.translate("DOCMAWCREATENEWDOCUMENTNOTIEMSG: This browser do not support ActiveX components");
            sClientFunc = "browserIsNotIE('"+sMsg+"')";
         }
      }
   }

   //This function will go back to the initial form

   public void backToForm()
   {
      ASPManager mgr = getASPManager();

      openBrowsWindow = true;
      saveOperation   = false;
      savedItem       = false;

 
      if (!mgr.isEmpty(ctx.readValue("REFERRER")))
      {         
         mgr.redirectTo(ctx.readValue("REFERRER"));
      }
      else
      {
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }
   }


   public void  cancel()
   {
      ASPManager mgr = getASPManager();

      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }
   
   //Bug id 88317 Start
   public String checkCreateNewAllowed()
   {


    ASPManager mgr = getASPManager();

    trans.clear();

    cmd = trans.addCustomFunction("FNDUSER", "Fnd_Session_API.Get_Fnd_User", "DUMMY2");
    cmd = trans.addCustomFunction("STARUSER", "person_info_api.Get_Id_For_User", "DUMMY1");
    cmd.addReference("DUMMY2", "FNDUSER/DATA");

    trans = mgr.perform(trans);
    String person_id = trans.getValue("STARUSER/DATA/DUMMY1");
    String output="false";

    if ("*".equals(person_id)){
       
       output="true" ;
    }

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



   public String getFileTypes()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addQuery("GETORIGINALFILETYPECOUNT", "SELECT count(*) COUNT FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW')");
      trans = mgr.perform(trans);
      int file_count = Integer.parseInt(trans.getValue("GETORIGINALFILETYPECOUNT/DATA/COUNT"));

      trans.clear();
      ASPQuery query = trans.addQuery("GETORIGINALFILETYPES", "SELECT file_type, file_extention FROM Edm_Application WHERE document_type IN ('ORIGINAL','VIEW') ORDER BY file_type");
      query.setBufferSize(file_count);
      trans = mgr.perform(trans);

      ASPBuffer buf = trans.getBuffer("GETORIGINALFILETYPES");

      String file_types = "";
      for (int i = 0; i < file_count; i++)
      {
         file_types += buf.getBufferAt(i).getValueAt(0) + "^" + buf.getBufferAt(i).getValueAt(1) + "^";
      }

      return file_types;
   }


   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(17).
      setMaxLength(12).
      setMandatory().
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTDOCCLASS: Doc Class").
      setDynamicLOV("DOC_CLASS",630,450).
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTDOCCLASS1: List of Document Class")).
      setCustomValidation("DOC_CLASS","SDOCNAME,FIRST_SHEET_NO,FIRST_REVISION,NUMBER_GENERATOR,ID1,ID2");


      headblk.addField("SDOCNAME").
      setLabel("DOCMAWCREATENEWDOCUMENTSDOCNAME: Doc Class Desc").
      setSize(20).
      setMaxLength(28).
      setReadOnly();

      headblk.addField("TITLE").
      setSize(20).
      setMandatory().
      setMaxLength(250).
      setCustomValidation("TITLE","CRE_NEW").
      setLabel("DOCMAWCREATENEWDOCUMENTDOCTITLE: Title");

      headblk.addField("FILE_TYPE").
      //setSize(20).
      //setMaxLength(250).
      setFunction("''").
      setReadOnly().
      setLabel("DOCMAWCREATENEWDOCUMENTFILETYPE: File Type");

      headblk.addField("DOC_NO").
      setSize(20).
      setMaxLength(120).
      setUpperCase().   // Bug Id 55471
      setLabel("DOCMAWCREATENEWDOCUMENTDOCNO: Doc No");

      headblk.addField("STRUCTURE").
      setCheckBox("0,1").
      setFunction("nvl(STRUCTURE,0)").
      setLabel("DOCTITLESTRUCTURE: Structure");

      headblk.addField("FIRST_SHEET_NO").
      setSize(20).
      //Bug 61028, Start, Increased the length
      setMaxLength(10).
      //Bug 61028, End
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTFIRSTSHEET: First Sheet No");

      headblk.addField("FIRST_REVISION").
      setSize(20).
      setMaxLength(6).
      setReadOnly().
      setInsertable().
      setMandatory().
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTFIRSTREV: First Document Revision");

      headblk.addField("DOC_SHEET").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setInsertable().
      setHidden().
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTDOCSHEET: Doc Sheet");

      headblk.addField("REV_NO_MAX").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTREVNOMAX: Max.Rev.No");

      headblk.addField("OBJ_CONN_REQ").
      setSize(20).
      setMandatory().
      setMaxLength(2000).
      setSelectBox().
      setHidden().
      enumerateValues("Doc_Reference_Object_Req_API").
      setLabel("DOCMAWCREATENEWDOCUMENTOBJCONNREQ: Object Connection");

      headblk.addField("VIEW_FILE_REQ").
      setSize(20).
      setMandatory().
      setMaxLength(200).
      setSelectBox().
      setHidden().
      enumerateValues("Doc_View_Copy_Req_API").
      setLabel("DOCMAWCREATENEWDOCUMENTVIEWFILEREQ: View Copy");

      headblk.addField("MAKE_WASTE_REQ").
      setSize(20).
      setMaxLength(200).
      setMandatory().
      setSelectBox().
      setHidden().
      enumerateValues("Doc_Make_Waste_Req_API").
      setLabel("DOCMAWCREATENEWDOCUMENTMAKEWASTEREQ: Destroy");

      headblk.addField("SAFETY_COPY_REQ").
      setSize(20).
      setMaxLength(200).
      setMandatory().
      setSelectBox().
      setHidden().
      enumerateValues("Doc_Safety_Copy_Req_API").
      setLabel("DOCMAWCREATENEWDOCUMENTSAFETYCOPYREQ: Safety Copy");

      headblk.addField("ALTERNATE_DOCUMENT_NUMBER").
      setSize(20).
      setHidden().
      setMaxLength(120).
      setLabel("DOCMAWCREATENEWDOCUMENTALTEDOCNU: Alternate Doc No");

      headblk.addField("CONFIDENTIAL").
      setSize(20).
      setHidden().
      setMaxLength(15).
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTCONFIDENTIAL: Confidential");

      headblk.addField("ISO_CLASSIFICATION").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTISOCLASSIFICATION: ISO Classification");

      headblk.addField("INFO").
      setSize(20).
      setMaxLength(2000).
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTINFO: Note");

      headblk.addField("ORIG_DOC_CLASS").
      setSize(17).
      setMaxLength(12).
      setUpperCase().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTORIGDOCCLASS: Based on Doc Class").
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTBASECLASS1: List of Based on Document Class"));

      headblk.addField("ORIG_DOC_NO").
      setSize(17).
      setMaxLength(120).
      setUpperCase().
      setHidden().
      setDynamicLOV("DOC_TITLE","ORIG_DOC_CLASS DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTBASENO1: List of Based on Document No")).
      setLabel("DOCMAWCREATENEWDOCUMENTORIGDOCNO: Based on Doc No");

      headblk.addField("ORIG_DOC_REV").
      setSize(17).
      setMaxLength(6).
      setUpperCase().
      setHidden().
      setDynamicLOV("DOC_ISSUE","ORIG_DOC_CLASS DOC_CLASS, ORIG_DOC_NO DOC_NO").
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTBASEREV: List of Based on Document Revision")).
      setLabel("DOCMAWCREATENEWDOCUMENTORIGDOCREV: Based on Doc Revision");

      headblk.addField("REPL_BY_DOC_CLASS").
      setSize(17).
      setMaxLength(12).
      setHidden().
      setUpperCase().
      setLabel("DOCMAWCREATENEWDOCUMENTREPLBYDOCCLASS: Replaced by Doc Class").
      setDynamicLOV("DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTREPCLASS: List of Replaced by Document Class"));

      headblk.addField("REPL_BY_DOC_NO").
      setSize(17).
      setMaxLength(120).
      setUpperCase().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTREPLBYDOCNO: Replaced by Doc No").
      setDynamicLOV("DOC_TITLE","REPL_BY_DOC_CLASS DOC_CLASS").
      setLOVProperty("TITLE",mgr.translate("DOCMAWCREATENEWDOCUMENTREPNO: List of Replaced by Document No"));

      headblk.addField("DT_CRE","Date").
      setSize(20).
      setReadOnly().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTDTCRE: Date Created");

      headblk.addField("USER_CREATED").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setUpperCase().
      setHidden().
      setLabel("DOCMAWCREATENEWDOCUMENTUSERCREATED: Created By");

      headblk.addField( "CRE_NEW","Number").
      setFunction("''").
      setHidden();

      headblk.addField("DOC_REV").
      setFunction("''").
      setHidden();

      headblk.addField("LOCAL_PATH").
      setFunction("''").
      setHidden();

      headblk.addField("DOC_TYPE").
      setFunction("''").
      setHidden();

      headblk.addField("LOC_PATH_OUT").
      setFunction("''").
      setHidden();

      headblk.addField("NUMBER_GENERATOR").
      setReadOnly().
      setUpperCase().
      setFunction("''");

      headblk.addField("ID1").
      setReadOnly().
      setFunction("''");

      headblk.addField("BOOKING_LIST").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setLOV("BookListLov.page", "ID1,ID2"); //Bug Id 73606

      headblk.addField("ID2").
      setSize(20).
      setMaxLength(30).
      setFunction("''").
      setUpperCase().
      setLOV("Id2Lov.page","ID1");

      headblk.addField("DUMMY1").setFunction("''").setHidden();
      headblk.addField("DUMMY2").setFunction("''").setHidden();
      headblk.addField("ATTR").setFunction("''").setHidden();
      headblk.addField("LOCAL_FILE_NAME").setFunction("''").setHidden();
      headblk.addField("OUT_1").setFunction("''").setHidden();
      headblk.addField("STR_IN").setFunction("''").setHidden();
  

      headblk.setView("DOC_TITLE");
      headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.SAVENEW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.SAVERETURN);
      headbar.disableCommand(headbar.CANCELNEW);
      headbar.disableMinimize();


      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWCREATENEWDOCUMENTOVEDOCTI: Create New Document"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);

        
   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();
      txt = null;

      String val = mgr.readValue("VALIDATE");

      if ("DOC_CLASS".equals(val))
      {
         doc_class=mgr.readValue("DOC_CLASS");
         cmd = trans.addCustomFunction( "DESC", "DOC_CLASS_API.GET_NAME", "SDOCNAME" );
         cmd.addParameter("DOC_CLASS",doc_class);

         trans = mgr.validate(trans);
         String descript = trans.getValue("DESC/DATA/SDOCNAME");
         //NUMBER_GENERATOR
         trans.clear();
         cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");

         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

         // First sheet no
         cmd = trans.addCustomFunction("FIRSTSHEET","Doc_Class_Default_API.Get_Default_Value_","FIRST_SHEET_NO");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","DOC_SHEET");

         // First revision
         cmd = trans.addCustomFunction("FIRSTREV","Doc_Class_Default_API.Get_Default_Value_","FIRST_REVISION");
         cmd.addParameter("DOC_CLASS");
         cmd.addParameter("DUMMY1","DocTitle");
         cmd.addParameter("DUMMY2","DOC_REV");

         trans = mgr.validate(trans);

         String numberGenerator  = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
         String first_sheet      = trans.getValue("FIRSTSHEET/DATA/FIRST_SHEET_NO");
         String first_rev        = trans.getValue("FIRSTREV/DATA/FIRST_REVISION");

         //ID1
         String id1 = "";
         String id2 = "";

         if ("ADVANCED".equals(numberGenerator))
         {
            trans.clear();
            cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
            cmd.addParameter("DOC_CLASS");
            cmd.addParameter("DUMMY1","DocTitle");
            cmd.addParameter("DUMMY2","NUMBER_COUNTER");

            trans = mgr.validate(trans);
            id1 = trans.getValue("ID1COM/DATA/ID1");

            trans.clear();
            cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
            cmd.addParameter("ID1",id1);
            trans = mgr.validate(trans);
            id2 = trans.getValue("ID2COM/DATA/ID2");
             if (("0".equals(id2)) || (id2 == null))
               id2 = "";
         }

         trans.clear();

         txt = (mgr.isEmpty(descript) ? "" : descript) + "^" +(mgr.isEmpty(first_sheet) ? "" : first_sheet) + "^"+(mgr.isEmpty(first_rev) ? "" : first_rev) + "^" +numberGenerator +"^"+id1+"^"+id2+"^";

         mgr.showAlert("txt"+txt);
         mgr.responseWrite(txt);
      }

      mgr.endResponse();

   }


   public void adjust()
   {
      ASPManager mgr = getASPManager();
      if (saveOperation)
         eval(headblk.generateAssignments());
   }


   protected String getDescription()
   {
      return "DOCMAWCREATENEWDOCUMENTTITLE: Create New Document";
   }


   protected String getTitle()
   {
      return "DOCMAWCREATENEWDOCUMENTTITLE: Create New Document";
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWCREATENEWDOCUMENTTITLE: Create New Document"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">  \n");
      out.append("<input type=\"hidden\" name=\"BROWSFILE\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMSELECT\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"FILENAME\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"FILETYPE\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"EXTENSION\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"FILEPATH\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"FROM_EDMMACRO\" value=\"\">\n");

      out.append(mgr.startPresentation("DOCMAWCREATENEWDOCUMENTTITLE: Create New Document"));

      if (openBrowsWindow)
      {
         headblk.setTitle(mgr.translate("DOCMAWCREATENEWDOCUMENTSELFILELBL: Select a file : "));
         out.append("<table cellpadding=0 cellspacing=0 border=0 width=\"100%\">\n");
         out.append(" <tr><td colspan=\"3\">"+headbar.showBar()+"<td></tr>");
         out.append(" <tr ><td width=10></td>");
         out.append("   <td> \n");//now inesert a BlockLayoutTable
         out.append("<table class=\"BlockLayoutTable\" width=\"100%\" border=0 cellpadding=5 cellspacing=5>");
         out.append("<tr><td align=\"right\"><input TYPE=\"text\" size=40 NAME=\"myUploadObject\" value=\"\"></td> ");
         out.append("<td >"+fmt.drawButton("DLG2",mgr.translate("DOCMAWCREATENEWDOCUMENTBROWSER: Browse..."),"onClick='browseFile()'")+"</td>\n");
         out.append("<td><input TYPE=\"hidden\" NAME=\"myCheckinType\" value=\"\"></td></tr> ");

         out.append("<tr hight=5><td ></td></tr>\n");
         out.append("<tr hight=5><td ></td></tr>\n");
         out.append("<tr hight=5><td ></td></tr>\n");
         out.append("</table> \n");// end of BlockLayoutTable
         out.append("</td> \n");
         out.append("   <td width=10></td></tr >");
         out.append("<tr><td colspan=\"2\" align=\"right\">");
         out.append(fmt.drawButton("SUBMIT",mgr.translate("DOCMAWCREATENEWDOCUMENTSELOK:   Next    "),"onClick='buttonpressedok()'"));
         out.append("&nbsp; ");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWCREATENEWDOCUMENTCANCELBUTTON: Cancel "),null));
         out.append("</td>\n");
         out.append("<td width=10></td>");
         out.append("   </tr> \n");
         out.append("  </table> \n");
      }
      else if (saveOperation)
      {
         headblk.setTitle(mgr.translate("DOCMAWCREATENEWDOCUMENTCREATENEWDOCWIZ: Create New Document Wizard"));
         out.append(headblk.generateHiddenFields());
         String my_doc_class=mgr.getASPField("DOC_CLASS").getValue();

         trans.clear();
         cmd = trans.addCustomFunction("DEFAULTDOCCLASS", "Docman_Default_API.Get_Default_Value_", "OUT_1");
         cmd.addParameter("STR_IN","Create New Document Wizard");
         cmd.addParameter("STR_IN","DOC_CLASS");//DOC_CLASS here is a parameter and not the ASP Field

         trans = mgr.validate(trans);
         my_doc_class = trans.getValue("DEFAULTDOCCLASS/DATA/OUT_1");

         trans.clear();

         String descript = "";
         String numberGenerator  = "";
         String first_sheet      = "";
         String first_rev        = "";
         String id1 = "";
         String id2 = "";
			
         if(my_doc_class==null || my_doc_class.equals("null" ))
         {
             my_doc_class="";				 

         }
         else
         {
             cmd = trans.addCustomFunction( "DESC", "DOC_CLASS_API.GET_NAME", "SDOCNAME" );
             cmd.addParameter("DOC_CLASS",my_doc_class);

             //NUMBER_GENERATOR
         
             cmd = trans.addCustomFunction("NUMBERGENERATOR","Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
             cmd.addParameter("DOC_CLASS",my_doc_class);
             cmd.addParameter("DUMMY1","DocTitle");
             cmd.addParameter("DUMMY2","NUMBER_GENERATOR");

             // First sheet no
             cmd = trans.addCustomFunction("FIRSTSHEET","Doc_Class_Default_API.Get_Default_Value_","FIRST_SHEET_NO");
             cmd.addParameter("DOC_CLASS",my_doc_class);
             cmd.addParameter("DUMMY1","DocTitle");
             cmd.addParameter("DUMMY2","DOC_SHEET");

             // First revision
             cmd = trans.addCustomFunction("FIRSTREV","Doc_Class_Default_API.Get_Default_Value_","FIRST_REVISION");
             cmd.addParameter("DOC_CLASS",my_doc_class);
             cmd.addParameter("DUMMY1","DocTitle");
             cmd.addParameter("DUMMY2","DOC_REV");

             trans = mgr.validate(trans);

             descript = trans.getValue("DESC/DATA/SDOCNAME");
             numberGenerator  = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
             first_sheet      = trans.getValue("FIRSTSHEET/DATA/FIRST_SHEET_NO");
             first_rev        = trans.getValue("FIRSTREV/DATA/FIRST_REVISION");

             trans.clear();
 
             descript=descript==null?"":descript;
             numberGenerator=numberGenerator==null?"":numberGenerator;
             first_sheet=first_sheet==null?"":first_sheet;
             first_rev=first_rev==null?"":first_rev;


             if ("ADVANCED".equals(numberGenerator))
             {
                 trans.clear();
                 cmd = trans.addCustomFunction("ID1COM","Doc_Class_Default_API.Get_Default_Value_","ID1");
                 cmd.addParameter("DOC_CLASS",my_doc_class);
                 cmd.addParameter("DUMMY1","DocTitle");
                 cmd.addParameter("DUMMY2","NUMBER_COUNTER");

                 cmd = trans.addCustomFunction("ID2COM","Doc_Number_Counter_API.Get_Default_Id2","ID2");
                 cmd.addReference("ID1","ID1COM/DATA" );
          
                 trans = mgr.validate(trans);

                 id1 = trans.getValue("ID1COM/DATA/ID1");
                 id2 = trans.getValue("ID2COM/DATA/ID2");
                 if (("0".equals(id2)) || (id2 == null))
                     id2 = "";
             }

             trans.clear();
         }
         String my_file_type=mgr.getASPField("FILE_TYPE").getValue();
         my_file_type=my_file_type==null?"":my_file_type;

         out.append("<table border=0  cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
         out.append("<tr><td colspan=\"3\">"+headbar.showBar()+"<td></tr>");
         out.append("<tr ><td width=10></td>");
         out.append("<td >");
         out.append("<table class=\"BlockLayoutTable\" width=\"100%\" border=0 cellpadding=0 cellspacing=0><tr >");//image and dialog inclueded in new table
         out.append("<td  ><img src = \"../docmaw/images/CreateNewDocRevision.jpg\" width=\"100\" height=\"300\"></td>\n");
         out.append("<td >");
         //+dialog+
         out.append("  <table class=\"BlockLayoutTable\" cellpadding=10 cellspacing=10 border=0 >\n");
         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTDOCCLASS: Doc Class"));
         out.append("</td>\n");
         out.append("      <td nowrap >");
         out.append(fmt.drawTextField("DOC_CLASS",my_doc_class,mgr.getASPField("DOC_CLASS").getTag(),20,12,true));
         out.append(" </td>");

         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTDOCCLASSDES: Doc Class Desc"));
         out.append("</td>\n");
         out.append("      <td nowrap >");
         out.append(fmt.drawReadOnlyTextField("SDOCNAME",descript,mgr.getASPField("SDOCNAME").getTag()));
         out.append(" </td>");

         out.append(" </tr>");

         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTDOCTITLE: Title"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("TITLE",mgr.getASPField("TITLE").getValue(),mgr.getASPField("TITLE").getTag()));
         out.append("</td>\n");

         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTDOCNO: Doc No"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("DOC_NO","",mgr.getASPField("DOC_NO").getTag()));
         out.append("</td>\n");
         out.append("   </tr> \n");

         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTSTRUCTURE: Structure"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawCheckbox("STRUCTURE","ISCHECKED",false,mgr.getASPField("STRUCTURE").getTag()));
         out.append("</td>\n");
         out.append("   </tr> \n");

         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTFIRSTSHEET: First Sheet No"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("FIRST_SHEET_NO",first_sheet,mgr.getASPField("FIRST_SHEET_NO").getTag()));
         out.append("</td>\n");

         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTFIRSTREV: First Document Revision"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("FIRST_REVISION",first_rev,mgr.getASPField("FIRST_REVISION").getTag()));
         out.append("</td>\n");
         out.append("   </tr> \n");

         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWCREATENEWDOCUMENTFILETYPE: File Type"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawReadOnlyTextField("FILE_TYPE",my_file_type,mgr.getASPField("FILE_TYPE").getTag()));
         out.append("</td>\n");



         //-----------CONFIG NO STARTS

         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWDOCTITLEOVWNUMBERGENERATOR: Number Generator"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawReadOnlyTextField("NUMBER_GENERATOR",numberGenerator,mgr.getASPField("NUMBER_GENERATOR").getTag()));
         out.append("</td>\n");
         out.append("   </tr>\n");

         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID1: Number Counter ID1"));
         out.append("</td>\n");
         //out.append("   </tr> \n");
         out.append("      <td>");
         out.append(fmt.drawReadOnlyTextField("ID1",id1,mgr.getASPField("ID1").getTag()));
         out.append("</td>\n");

         //out.append("   <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWDOCTITLEOVWBOOKINGLIST: Booking List"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("BOOKING_LIST","",mgr.getASPField("BOOKING_LIST").getTag()));
         out.append("</td>\n");
         out.append("   </tr>\n");

         out.append("   <tr   >\n");
         out.append("      <td>");
         out.append(fmt.drawWriteLabel("DOCMAWDOCTITLEOVWNUMBERCOUNTERID2: Number Counter ID2"));
         out.append("</td>\n");
         out.append("      <td>");
         out.append(fmt.drawTextField("ID2",id2,mgr.getASPField("ID2").getTag()));
         out.append("</td>\n");
         out.append("   </tr> \n");
         //-----------CONFIG NO ENDS
         out.append("  </table> \n");

         //end of dialog
         out.append("</td></tr></table>\n");// ends the table which contains the image and dialog
         out.append("</td><td width=10></td></tr>\n");
         out.append("<tr><td colspan=\"2\" align=\"right\">");
         //+buttons+
         if (bFromPortlet)
         {
            out.append(fmt.drawSubmit("FINISH_NEW",mgr.translate("DOCMAWCREATENEWDOCUMENTSAVEANDRETURN: Save and Return"),null));
         }
         else
            out.append(fmt.drawSubmit("FINISH_NEW",mgr.translate("DOCMAWCREATENEWDOCUMENTSAVEANDNEW: Save and New"),null));
         out.append("&nbsp;&nbsp;");
         out.append(fmt.drawSubmit("FINISH",mgr.translate("DOCMAWCREATENEWDOCUMENTSAVE: Save"),null));
         out.append("&nbsp;&nbsp;");
         out.append(fmt.drawSubmit("CANCELOK",mgr.translate("DOCMAWCREATENEWDOCUMENTCANCELBUTTONOK: Cancel"),null));
         //end of buttons
         out.append("</td><td width=10></td></tr>\n");
         out.append("</table>\n");
         

      }
      else if (bTransferToEDM)
      {
         // let us say what is happening now while edm macro does its job.!! : bakalk
        out.append("<table class=\"BlockLayoutTable\" width=200><tr><td width=10></td>");//first column is just to leave a space
        out.append("<td>"+fmt.drawReadOnlyTextField("_STATUS",mgr.translate("DOCMAWCREATENEWDOCUMENTFILEOPERATION: File Operations in progress..."),"",200)+"</td>");
        out.append("</tr>\n");
        out.append("<tr><td colspan=2>&nbsp;&nbsp;</td></tr>\n");
        out.append("<tr><td colspan=2>&nbsp;&nbsp;</td></tr>\n");
        out.append("<tr><td colspan=2>&nbsp;&nbsp;</td></tr>\n");
        out.append("<tr><td colspan=2>&nbsp;&nbsp;</td></tr>\n");
        out.append("</table>\n");
      }

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append(DocmawUtil.getClientMgrObjectStr());
      out.append("</body>\n");
      out.append("</html>");

		if(saveOperation)
		{
			appendDirtyJavaScript("setFocus();\n");
			appendDirtyJavaScript("function setFocus()\n");
			appendDirtyJavaScript("{\n");
			appendDirtyJavaScript(" 	document.form.DOC_CLASS.focus();\n");
			appendDirtyJavaScript("return ;\n");
			appendDirtyJavaScript("}\n");

		}

      appendDirtyJavaScript("function buttonpressedok()\n");
      appendDirtyJavaScript("{  \n");
      appendDirtyJavaScript("    document.form.BROWSFILE.value=document.form.myUploadObject.value;\n");
      appendDirtyJavaScript("    document.form.FILETYPE.value=document.form.myCheckinType.value;\n");
      appendDirtyJavaScript("    filename1=(document.form.myUploadObject.value).substring((((document.form.myUploadObject.value).lastIndexOf(\"\\\\\"))+1),((document.form.myUploadObject.value).lastIndexOf(\".\")));\n");
      appendDirtyJavaScript("    filepath =(document.form.myUploadObject.value).substring(0,((document.form.myUploadObject.value).lastIndexOf(\"\\\\\")));\n");
      appendDirtyJavaScript("    re = /\\\\/gi; \n");
      appendDirtyJavaScript("    filepath = filepath.replace(re,\"~\");\n");
      appendDirtyJavaScript("    //alert(filepath);\n");
      appendDirtyJavaScript("    filename2=filename1;\n");
      appendDirtyJavaScript("    document.form.FILENAME.value=filename2;\n");
      appendDirtyJavaScript("    document.form.FILEPATH.value=filepath;\n");
      // Bug Id 92125, Start
      appendDirtyJavaScript("    extIndex = (document.form.myUploadObject.value).lastIndexOf(\".\");\n");
      appendDirtyJavaScript("    if(extIndex > -1) \n");      
      appendDirtyJavaScript("       extension = (document.form.myUploadObject.value).substring(extIndex+1);\n");
      appendDirtyJavaScript("    else \n");
      appendDirtyJavaScript("       extension = \"\";\n");
      appendDirtyJavaScript("    document.form.EXTENSION.value=extension.toUpperCase();    \n");
      appendDirtyJavaScript("    document.form.CONFIRMSELECT.value =\"OK\";\n");
      appendDirtyJavaScript("    if(filename2.length === 0 || filepath.length === 0) \n");
      appendDirtyJavaScript("    { \n");
      appendDirtyJavaScript("       alert(\""+ mgr.translate("CREATENEWDOCUMENTPROPERFILENOTSELECT: Proper File has not been selected.")+"\"); \n");
      appendDirtyJavaScript("    } \n");
      appendDirtyJavaScript("    else if(extension.length === 0) \n");
      appendDirtyJavaScript("    { \n");
      appendDirtyJavaScript("       alert(\""+ mgr.translate("DOCMAWCREATENEWDOCUMENTNOEXTALERT: File(s) without extension(s) are not allowed.")+"\"); \n");
      appendDirtyJavaScript("    } \n");
      appendDirtyJavaScript("    else \n");
      appendDirtyJavaScript("    { \n");      
      appendDirtyJavaScript("       submit();\n");
      appendDirtyJavaScript("    } \n");
      appendDirtyJavaScript("}\n");
      // Bug Id 92125, End

      if (bPerform)
      {
         appendDirtyJavaScript("      eval(\"");
         appendDirtyJavaScript(sClientFunc);
         appendDirtyJavaScript("\");\n");
      }
      appendDirtyJavaScript("function browserIsNotIE( notIEMessage )\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    alert(notIEMessage);\n");
      appendDirtyJavaScript("    window.close(); \n");
      appendDirtyJavaScript("}\n");

      if (bTransferToEDM)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=200,top=100\");\n");
      }

      String gotoIssue = mgr.translate("DOCMAWCREATENEWDOCUMENTMOVETOISSUE: Moving to Document Info...");
      String gotoFirstStep = mgr.translate("DOCMAWCREATENEWDOCUMENTMOVEBACKTOISSUE: Moving back to First Step...");
      String current_state = ctx.readFlag("GOTO_ISSUE",false)? gotoIssue : gotoFirstStep;

      appendDirtyJavaScript("function refreshParent() \n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript(" document.forms[0].FROM_EDMMACRO.value=\"YES\"; \n");
      appendDirtyJavaScript(" document.forms[0]._STATUS.value=\""+current_state+"\"; \n");
      appendDirtyJavaScript("   submit(); \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("function browseFile()\n");
      appendDirtyJavaScript("{ \n");
      //Bug id 88317 Start
      appendDirtyJavaScript("	var ret =__connectURL('");  
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?execute=checkCreateNewAllowed');\n");

      appendDirtyJavaScript("	  if (ret==\"true\")\n");        
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert(\"");
      appendDirtyJavaScript(mgr.translate("DOCMAWCREATENEWNOTALLOWED: User with * Person ID is not allowed to create new documents. Please contact your Administrator."));
      appendDirtyJavaScript("\")\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("    else \n");
      appendDirtyJavaScript("    { \n");
      //Bug id 88317 End
      appendDirtyJavaScript("   file_types = '" + mgr.encodeStringForJavascript(getFileTypes()) + "';");
      appendDirtyJavaScript("   oCliMgr.Init(\"" + mgr.getApplicationAbsolutePath() +"\");\n");
      appendDirtyJavaScript("   var files = oCliMgr.ShowFileBrowser(\"\", file_types, false);\n");
      appendDirtyJavaScript("   var filetype = oCliMgr.getFileTypeSelected();\n");
      appendDirtyJavaScript("   document.form.myUploadObject.value = files;\n");
      appendDirtyJavaScript("   document.form.myCheckinType.value = filetype;\n");

      appendDirtyJavaScript("    } \n");    //bug id 88317

      appendDirtyJavaScript("}\n");
      return out;
   }


   //==========================================================================
   //  Methods on Handling Strings
   //==========================================================================
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
   public int howManyOccurance(String str,char c)
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

}//end of class
