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
 *  ----------------------------------------------------------------------------
 *  File                        : EdmBriefcaes.java
 *  Description                 : Generates HTML GUI for exporting/importing briefcases
 *  Notes                       :
 *  Other Programs Called       :
 *  ----------------------------------------------------------------------------
 *  History:
 *
 *  23-04-2003  Dikalk  Created first version of this page
 *  13-05-2003  NiSilk  Added method viewExportWithExternalViewer().
 *                      Modified methods generatePageHeader() performAction(),and getContents().
 *  20-05-2003  Bakalk  Accept existing docs.
 *  22-05-2003  Dikalk  Changed the Export flow a little
 *  26-05-2003  Dikalk  Implemented functionality for viewing exported documents
 *                      Added methods viewExported() and getFileName()
 *  27-05-2003  NiSilk  Modified method viewExportedWithExternalViewer() and removed
 *                      condition VIEWEXPORTEDWITHEXT from method run().
 *                      Added function viewExportedWithExt().
 *  29-05-2003  Dikalk  Modifed javascript methods importBriefcase() and createMetaFiles()
 *  05-06-2003  NiSilk  Removed checks made for INCLUDE_VIEW_COPY and INCLUDE_RED_LINE.
 *  12-06-2003  InoSlk  Added method exportRejectedDocuments.
 *  13-06-2003  Bakalk  Error message is prompted using alert method of javascript.
 *  26-06-2003  Bakalk  Modified client method:exportDocuments(), added a msg there.
 *  01-07-2003  NiSilk  Modified javascript method exportRejectedDocuments to refresh the page correctly.
 *  14-07-2003  Bakalk  Modified ExportBriefcase. Xml is not created when file reference is not there and
 *                      ExecuteAction of Ocx not called in same case.
 *  22-07-2003  InoSlk  Modified methods run, performAction and getContents.
 *                      Added method resetBriefcase.
 *  29-07-2003  Dikalk  Alert messages are now shown for any exceptions thrown.
 *  31-07-2003  Dikalk  Added code for logging errors during export
 *  07-08-2003  Bakalk  Modified client method importBriefcase: parent not refreshed if there is error.
 *  29-08-2003  InoSlk  Call ID 101731: Modified doReset() and clone().
 *  01-09-2003  Dikalk  Modified javascript method importDocuments()
 *  16-09-2003  Dikalk  Moved translation constants for ocx to DocmawConstants.java
 *  22-12-2003  INOSLK  bug ID 41187: Modified javascript method createMetaFiles() to halt the export process, if any client
 *  22-12-2003          errors were encountered when creating briefcase meta files.
 *  10-02-2004  Thwilk  Modified exportBriefcase() to accept the template key as in keyref format.
 *  17-03-2004  INOSLK  Bug ID 43497: Modified JavaScript methods viewExported() and viewExportedWithExt() in getContents().
 *  01-04-2004  INOSLK  Bug ID 43593: Modified exportBriefcase().
 *  04-06-2004  Shthlk   Bug ID 45197: Added new varible currentDocBriefcase and modified exportBriefcase.
 *  06-09-2004  INOSLK  Bug ID 46719: Modified getContents().
 *  16-09-2004  INOSLK  Bug ID 46925: Modified accept().
 *  22-09-2004  KARALK  Bug Id 45979, Modified methods viewExportedWithExternalViewer(), getContents()
 *  14-10-2004  INOSLK  Bug ID 47320, Modified run() and accept().
 *  20-10-2004  INOSLK  Bug ID 47367, Added new variable create_rejected_meta_files. Modified exportBriefcase() and getContents().
 *  28-10-2004  INOSLK  Bug ID 47320, Modified getContents().
 *  10-11-2004  INOSLK	Bug ID 47171, Modified in exportBriefcase() and getContents(). Transferring document files from
 *  10-11-2004          the original to the rejected bc happens at the time of creating the meta file now.
 *  15-11-2004  SHTHLK  Bug ID 47864, Modified resetBriefcase() to refreash the parent window only if the ocx deleted the folder properly.
 *  15-11-2004          Moved translateOCXStrings to client_main() for proper translation of constants
 *  22-12-2004  SHTHLK  Bug ID 48298, Added method getImportedPath to get the imported path of the briefcase.
 *  22-12-2004          Modified accept, exportBriefcase, viewExported and viewExportedWithExternalViewer. Check bug id for more info.
 *  29-07-2005  KARALK  merge bug 50336 from support.
 *  21-03-2006  RUCSLK  Merged Bug Id 54962.
 *  17-05-2006  KARALK  Bug Id 56784, Modified resetBriefcase() and importBriefcase() to retrive the "BRIEF_NO" from the buffer.
 *  31-05-2006  BAKALK  Removing 2nd ftp, modified variable names accordingly and some values of them too.
 *  28-03-2007  JANSLK  Merged Bug Id 61755, Modified createMetaFiles() java script method.
 *  29-03-2007  JANSLK  Merged Bug Id 62900, Modified createMetaFiles() java script method.
 *  10-08-2007  NaLrlk  XSS Correction.
 *  18-09-2007  DinHlk  Patched in Bug Id 62423, Added method acceptObjectsToDocument(). Modified accept() to do plant design object checks and insert them when accepting a document.
 *  29-10-2007  DULOLK  Bug Id 65509, Modified method acceptObjectsToDocument() to accomodate all other objects besides plant design.
 *  07-07-2008  AMNALK  Bug Id 72460, Modified run() & getContents(). Added new method rollbackStatus().
 *  07-29-2008  VIRALK  Bug Id 74523, Modified exportBriefcase() and getContents().
 
 *----------------------------------------------------------------------------
 */


package ifs.docmaw;

import ifs.docmaw.edm.*;
import ifs.fnd.asp.*;
import ifs.fnd.service.*;

import java.io.*;

import ifs.fnd.buffer.*;
import ifs.fnd.*;
import ifs.fnd.util.Str;

import java.io.IOException;
import java.util.*;

public class EdmBriefcase extends DocSrv
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.EdmBriefcase");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================

   private ASPContext           ctx;
   private ASPHTMLFormatter     fmt;
   private ASPBlock             blk;
   private ASPBlock             paramblk;
   private ASPRowSet            rowset;
   private ASPCommandBar        cmdbar;
   private ASPTable             tbl;
   private ASPBlockLayout       lay;


   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   private ASPTransactionBuffer trans;
   private DocumentTransferHandler doc_hdlr;

   private String action;
   private String client_action;
   private String final_client_action;
   private String err_msg_log;
   private String line_number;
   private String doc_class;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String doc_title;
   private String xmlFileLoc;
   private String parents_method;
   private String file_name;
   private String new_file_name;
   private String error_msg;
   private String document_key;

   private boolean caught_error;
   private boolean move_to_next;
   private boolean create_rejected_meta_files = false; //Bug ID 47367, inoslk
   private String brief_imported_path; // Bug Id 48298
   //Bug Id 62423,Start
   private String sCheckObjectMessage;
   private String sWarnMessage;
   private boolean bCheckObjectConfirm;
   //Bug Id 62423,End
   //===============================================================
   // Construction
   //===============================================================
   public EdmBriefcase(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans         = null;
      doc_hdlr      = null;

      action              = null;
      docBriefcase        = null;
      client_action       = null;
      final_client_action = null;
      err_msg_log         = null;
      line_number         = null;
      doc_class           = null;
      doc_no              = null;
      doc_sheet           = null;
      doc_rev             = null;
      doc_title           = null;
      xmlFileLoc          = null;
      parents_method      = null;
      file_name           = null;
      new_file_name       = null;
      error_msg           = null;
      document_key        = null;

      caught_error        = false;
      move_to_next        = false;
      currentDocBriefcase = null; //Bug Id 45197
      create_rejected_meta_files = false; //Bug ID 47367, inoslk
      brief_imported_path = null; // Bug Id 48298
      //Bug Id 62423,Start
      sCheckObjectMessage = null;
      sWarnMessage = null;
      bCheckObjectConfirm = false;
      //Bug Id 62423,End
      //Resetting mutable attributes
      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      EdmBriefcase page = (EdmBriefcase)(super.clone(obj));

      //Initialising mutable attributes
      page.trans               = null;
      page.doc_hdlr            = null;

      page.action              = null;
      page.docBriefcase        = null;
      page.client_action       = null;
      page.final_client_action = null;
      page.line_number         = null;
      page.doc_class           = null;
      page.doc_no              = null;
      page.doc_sheet           = null;
      page.doc_rev             = null;
      page.doc_title           = null;
      page.xmlFileLoc          = null;
      page.parents_method      = null;
      page.file_name           = null;
      page.new_file_name       = null;
      page.err_msg_log         = null;
      page.document_key        = null;

      page.caught_error = false;
      page.move_to_next = false;
      page.currentDocBriefcase = null;  //Bug Id 45197
      page.create_rejected_meta_files = false; //Bug ID 47367, inoslk
      page.brief_imported_path = null; // Bug Id 48298
      //Bug Id 62423,Start
      page.sCheckObjectMessage = null;
      page.sWarnMessage = null;
      page.bCheckObjectConfirm = false;
      //Bug Id 62423,End

      //Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
      page.blk = page.getASPBlock(blk.getName());
      page.paramblk = page.getASPBlock(paramblk.getName());
      page.rowset   = page.blk.getASPRowSet();
      page.cmdbar   = page.blk.getASPCommandBar();
      page.tbl      = page.blk.getASPTable();
      page.lay      = page.blk.getASPBlockLayout();

      return page;
   }


   public void run()
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();

      //Bug Id 72460, start
      //Rollback the status of checked out documents if the operation fails.
      if ( "OK".equals(mgr.readValue("ROLLBACK_STATUS")) )
      {
	  try 
	     {
		doc_hdlr = new DocumentTransferHandler(mgr);
		rollbackStatus();
	     }
	     catch(Exception e){
	     }
	     
      }
      else
      {
      //Bug Id 72460, end	  
      mgr.setPageExpiring();

      try
      {
         if ("IMPORT_BRIEFCASE".equals(mgr.getQueryStringValue("ACTION")))
            action = "IMPORT_BRIEFCASE";
         else if ("RESET_BRIEFCASE".equals(mgr.getQueryStringValue("ACTION")))
            action = "RESET_BRIEFCASE";
         else
         {
            doc_hdlr = new DocumentTransferHandler(mgr);
            action = doc_hdlr.getActionProperty("ACTION");
         }
      }
      catch (FndException fnd)
      {
         client_action = "ERROR";
         error_msg = fnd.getMessage();
         fnd.printStackTrace();
      }

      initialisePage();


      try
      {
         performAction();
         if (doc_hdlr != null)
         {
            if (!doc_hdlr.isLastDocument())
            {
               debug("This is not the last doc");
               final_client_action ="document.forms[0].submit();";
               if (move_to_next) // only if our job for current record is done
               {
                  doc_hdlr.nextDocument();
               }
            }
            else
            {
               if (move_to_next)  //means our job is done!!!
               {
                  if (mgr.isEmpty(parents_method))
                  {
                     final_client_action ="\n window.close(); \n";
                  }
                  else
                  {
                     final_client_action ="\n opener."+parents_method+"\n window.close();\n";
                  }

               }
               else
               {
                  final_client_action ="document.forms[0].submit();";
                  debug("***********   final_client_action="+final_client_action);
               }
            }
            debug("Saving reguest...");
            doc_hdlr.saveRequest();
         }
      }
      catch (FndException fnd)
      {
         client_action = "ERROR";
         error_msg = fnd.getMessage();
         fnd.printStackTrace();
      }
      // Bug ID 47320, inoslk, start
      catch (Exception any)
      {
	 any.printStackTrace();
	 error_msg = any.getMessage();
	 client_action = "ERROR";
	 final_client_action ="\n opener."+parents_method+"\n window.close();\n";

	 int is_ora_index = error_msg.indexOf("ORA");
	 if (is_ora_index != -1)
	    error_msg = error_msg.substring(0,is_ora_index-1);	
      }
      // Bug ID 47320, inoslk, end
       }//Bug Id 72460
   }


   private static String getStackTrace( Throwable t )
   {
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter( sw, true );

      t.printStackTrace( pw );

      String s = sw.toString();

      pw.close();

      return s;
   }


   private void initialisePage()
   {
   }


   public void debug(String str)
   {
      if (DEBUG) super.debug(str);
   }


   private void performAction() throws FndException
   {
      debug("performAction() {");

      ASPManager mgr = getASPManager();

      debug("Action = " + action);

      if ("EXPORT_BRIEFCASE".equals(action))
         exportBriefcase();
      else if ("ACCEPT".equals(action))
         accept();
      else if ("IMPORT_BRIEFCASE".equals(action))
         importBriefcase();
      else if ("VIEW_EXPORTED".equals(action))
         viewExported();
      else if ("VIEWEXPORTEDWITHEXT".equals(action))
         viewExportedWithExternalViewer();
      else if ("EXPORTREJECTED".equals(action))
         exportBriefcase();
      else if ("RESET_BRIEFCASE".equals(action))
         resetBriefcase();

      try
      {
         if (!Str.isEmpty(xmlFileLoc))
         {
            ctx.setGlobal("DOCMAW_FILE_INFO_XML", Util.toBase64Text(xmlFileLoc.getBytes()));
         }
      }
      catch (IOException io)
      {
         throw new FndException(mgr.translate("EDMBRIEFCASESETGBLCTXFAILED: Set Global context failed.\n")+io.getMessage());
      }

      debug("} performAction()");
   }


   private void viewExportedWithExternalViewer() throws FndException
   {
      doc_class      = doc_hdlr.getDocClass();
      doc_no         = doc_hdlr.getDocNo();
      doc_sheet      = doc_hdlr.getDocSheet();
      doc_rev        = doc_hdlr.getDocumentProperty("DOC_REVISION");
      action         = doc_hdlr.getActionProperty("ACTION");

      //Bug id 45979 Start.
      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(mgr.readValue("EXTERNAL_VIEWER")))
      {
         client_action = "CHECK_EXTERNAL_VIEWER";
         return;
      }
      //Bug id 45979 End.

      docBriefcase   = doc_hdlr.getActionProperty("BRIEF_NO");
      //bug 58326
      String file_id = null;
      String doc_title = null;
      String line_no = doc_hdlr.getDocumentProperty("LINE_NO");
      file_name = getFileName(docBriefcase, line_no, "ORIGINAL");
      //Bug Id 48298, Start
      brief_imported_path = getImportedPath(docBriefcase);
      brief_imported_path = brief_imported_path.replaceAll("\\\\","\\\\\\\\");
      //Bug Id 48298, End
      client_action = "VIEWEXPORTEDWITHEXT";
      move_to_next = true;
   }

   private void exportBriefcase()throws FndException
   {
      ASPManager mgr = getASPManager();
      //bug id 74523, Start
       if (mgr.isEmpty(mgr.readValue("LOCAL_CHECKOUT_PATH")))
      {  
	  if(ctx.readFlag("META_FILES_CREATED", false))
	 {
	     ctx.writeFlag("META_FILES_CREATED", true);
	 }
	 client_action = "GET_PATH_FIRST";
         return;
	 
      }
        //bug id 74523, End
      docBriefcase = doc_hdlr.getActionProperty("BRIEF_NO");
      currentDocBriefcase = doc_hdlr.getActionProperty("CURRENT_BRIEF_NO"); //Bug Id 45197
      // Check to see if the Meta files have been
      // created. If not, create them first!

      if (!ctx.readFlag("META_FILES_CREATED", false))
      {
	 // Bug ID 47367, inoslk, start
	 if (action.equals("EXPORTREJECTED"))
	    create_rejected_meta_files = true;
	 // Bug ID 47367, inoslk, end
         imported_path = getImportedPath(currentDocBriefcase); //Bug Id 48298
         trans.clear();
         ASPCommand cmd = trans.addCustomFunction("BCTMPL","DOCMAN_DEFAULT_API.Get_Default_Value_","OUT_STR");
         cmd.addParameter("IN_STR" ,"DocBriefcase");
         cmd.addParameter("IN_STR" ,"BRIEFCASE_TEMPLATE");
         trans = mgr.perform(trans);

         String template_key = trans.getValue("BCTMPL/DATA/OUT_STR");

         if (mgr.isEmpty(template_key))
         {
            error_msg = mgr.translate("DOCMAWEDMBRIEFCASEBCTEMPLATENOTCONFIG: A Briefcase Template has not been configured in Basic Data\\\\Various settings\\\\Def. Values.\\nContact your administrator.");
            final_client_action ="\n window.close(); \n";
            doc_hdlr = null;
            return;
         }


      //Bug Id 42507,Start
        StringTokenizer main_token = new StringTokenizer(template_key,"^");
        int count_tokens = main_token.countTokens();
        if (count_tokens>1 && count_tokens==4){
           int index = 0;
	   StringTokenizer sub_token;
           String []item_name=new String[count_tokens];
           String []item_value=new String[count_tokens];

           while (main_token.hasMoreElements())
           {
              sub_token = new StringTokenizer(main_token.nextToken(),"=");
              if(sub_token.hasMoreTokens()) {
                 item_name[index] = sub_token.nextToken();
              }
              if(sub_token.hasMoreTokens()) {
                 item_value[index] = sub_token.nextToken();
              }
              index=index+1;
           }
           if (item_name[0].equals("DOC_CLASS")) {
	      doc_class=item_value[0];
	   }
	   if (item_name[1].equals("DOC_NO")) {
              doc_no=item_value[1];
	   }
           if (item_name[2].equals("DOC_SHEET")) {
	      doc_sheet=item_value[2];
	   }
           if (item_name[3].equals("DOC_REV")) {
	      doc_rev=item_value[3];
	   }
        }
	else
	{
	   error_msg = mgr.translate("DOCMAWEDMBRIEFCASENONOREFBCTEMPLATEFORMAT: The format of the default value for Briefcase Template is incorrect.\\nContact your administrator.");
           final_client_action ="\n window.close(); \n";
	   doc_hdlr = null;
           return;
	}

       //Bug Id 42507, End
         String doc_type = "ORIGINAL";
         String edm_info = getEdmInformation(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL");
         String ref_exist = getStringAttribute(edm_info, "ORIGINAL_REF_EXIST");

         if (ref_exist.equals("FALSE"))
         {
            error_msg = mgr.translate("DOCMAWEDMBRIEFCASENONOREFBCTEMPLATE: A Briefcase Template file has not been checked in to the repository.\\nContact your administrator.");
            final_client_action ="\n window.close(); \n";
            doc_hdlr = null;
            return;
         }

         String edm_rep_info = getEdmRepositoryInformation(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL");
         file_name = getStringAttribute(edm_rep_info, "COPY_OF_FILE_NAME");
         new_file_name = docBriefcase + "." + DocmawUtil.getFileExtention(file_name);
	 //bug id 74523, Start
	 String path = mgr.readValue("LOCAL_CHECKOUT_PATH")+"\\"+ docBriefcase+"\\";
	 //bug id 74523, End
         launchFile = "NO";
         //bug 58326, starts..
         String file_id = getCopy(doc_class, doc_no, doc_sheet, doc_rev, doc_type);
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, doc_type, null, "", null, "", "", "VIEW", "VIEW", file_id, path);
         //bug 58326 ends..
         ctx.writeFlag("META_FILES_CREATED", true);
         client_action = "CREATE_META_FILES";
         return;
      }

      // Need to save the fact that the meta files
      // have been created in an ealier request
      ctx.writeFlag("META_FILES_CREATED", true);

      doc_class      = doc_hdlr.getDocClass();
      doc_no         = doc_hdlr.getDocNo();
      doc_sheet      = doc_hdlr.getDocSheet();
      doc_rev        = doc_hdlr.getDocRev();
      action         = doc_hdlr.getActionProperty("ACTION");

      if (action.equals("EXPORT_BRIEFCASE"))
      {
         //bug 58326
         String file_id = null;

         try
         {
            //bug 58326
            file_id = checkOutDocumentForBC(doc_class,doc_no,doc_sheet,doc_rev,"");
         }
         catch(FndException fnd)
         {
            caught_error = true;
            // Bug ID 43593, inoslk, Changed the document key to be in Key reference format
            document_key = "DOC_CLASS=" +doc_class + "^DOC_NO=" + doc_no + "^DOC_REV=" + doc_rev + "^DOC_SHEET=" + doc_sheet + "^";
            err_msg_log = fnd.getMessage();

            // Set the briefcase issue to state Failed
            trans.clear();
            ASPCommand cmd = trans.addCustomCommand("SETFAILED",  "Doc_Briefcase_Issue_Api.Set_Bc_Issue_State");
            cmd.addParameter("IN_STR", docBriefcase);
            cmd.addParameter("IN_STR", doc_class);
            cmd.addParameter("IN_STR", doc_no);
            cmd.addParameter("IN_STR", doc_sheet);
            cmd.addParameter("IN_STR", doc_rev);
            cmd.addParameter("IN_STR", "Fail");
            mgr.perform(trans);
         }

         if (!fileNotExist)
         {
	  //bug id 74523, Start
	 String path = mgr.readValue("LOCAL_CHECKOUT_PATH")+"\\"+ docBriefcase+"\\";
	 //bug id 74523, End
            //bug 58326
            xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL", null, "", null, "", "", action, "CHECKOUT", file_id, path);
         }
         client_action = "EXPORT_BRIEFCASE";
      }
      else if (action.equals("EXPORTREJECTED"))
      {
	 // Bug ID 47171, inoslk, Removed code for creating the XML file - all doc files are moved at meta file creation
         client_action = "EXPORTREJECTED";
	 // Bug ID 47171, inoslk, start
	 final_client_action ="\n window.close(); \n";
	 doc_hdlr = null;
	 // Bug ID 47171, inoslk, end
      }

      move_to_next = true;
   }


   public void accept()throws FndException
   {
      ASPManager mgr = getASPManager();

      DocmawConstants doc_constant = DocmawConstants.getConstantHolder(mgr);
      //bug 58326
      String file_id = null;
      String db_unchanged = doc_constant.doc_bc_change_unchanged;
      String db_changed   =  doc_constant.doc_bc_change_changed;
      String db_new       =  doc_constant.doc_bc_change_new;
      String db_new_sheet =  doc_constant.doc_bc_change_newsheet;

      String curr_change_state = doc_hdlr.getDocumentProperty("DOC_BC_CHANGE_STATUS");
      String curr_booking_list = doc_hdlr.getDocumentProperty("BOOKING_LIST");

      doc_class      = doc_hdlr.getDocClass();
      doc_no         = doc_hdlr.getDocNo();
      doc_sheet      = doc_hdlr.getDocSheet();
      doc_rev        = doc_hdlr.getDocRev();
      line_number    = doc_hdlr.getDocumentProperty("LINE_NO");
      action         = doc_hdlr.getActionProperty("ACTION");
      docBriefcase   = doc_hdlr.getActionProperty("BRIEF_NO");
      parents_method = doc_hdlr.getActionProperty("PARENT_REFRESH_METHOD");

      sWarnMessage = ctx.readValue("WARNMESSAGE",""); //Bug Id 62423
      // Bug ID 50336, Start
      /* 
      // Bug ID 46925, inoslk, start
      if (curr_change_state.equals(db_new_sheet))
      {
	 trans.clear();

	 ASPCommand cmd = trans.addCustomFunction("VALIDATESHEET","DOC_BRIEFCASE_UNPACKED_API.Validate_New_Sheet_","OUT_1");
	 cmd.addParameter("OUT_2",""); // Bug ID 47320, inoslk, get doc_class from connected doc
	 cmd.addParameter("IN_1",docBriefcase);
	 cmd.addParameter("IN_1",line_number);

	 trans = mgr.perform(trans);
	 String ret = trans.getValue("VALIDATESHEET/DATA/OUT_1");
	 // Bug ID 47320, inoslk, start
	 doc_class = trans.getValue("VALIDATESHEET/DATA/OUT_2");
	 // Bug ID 47320, inoslk, end

	 if (!("0".equals(ret)))
	 {
	    switch (ret.charAt(0))
	    {
	    case '1' :
	       error_msg = mgr.translate("DOCMAWEDMBCLONESHEET: Cannot find a document to connect this sheet to.");
	       break;
	    case '2' :
	       error_msg = mgr.translate("DOCMAWEDMBCNOSHEETNO: You must specify a sheet number for the new sheet.");
	       break;
	    case '3' :
	       error_msg = mgr.translate("DOCMAWEDMBCNEWDOCNOTACCEPTED: This document sheet belongs to a new document. You must accept the document first, in order to connect the sheet.");
	       break;
	    case '4' :
	       error_msg = mgr.translate("DOCMAWEDMBCSHEETEXISTS: Specified sheet already exists for this document.");
	       break;
	    }

	    move_to_next = true;
            return;
	 }
      }
      // Bug ID 46925, inoslk, end
      */
      // Bug ID 50336, End

      if (curr_change_state.equals(db_unchanged))
      {
         //just unlock the doccument and update status
         trans.clear();
         ASPCommand cmd = trans.addCustomCommand("ACCEPTDOCUMENT" ,  "DOC_BRIEFCASE_UNPACKED_API.Accept_");
         cmd.addParameter("OUT_4", ""); //checking list
         cmd.addParameter("OUT_5", ""); //delete list
         cmd.addParameter("OUT_1");
         cmd.addParameter("OUT_2");
         cmd.addParameter("OUT_3");
         cmd.addParameter("IN_1", docBriefcase);
         cmd.addParameter("IN_1", line_number);
         trans = mgr.perform(trans);

         acceptObjectsToDocument(docBriefcase, line_number); //Bug Id 62423

         move_to_next = true;
      }
      else if (!ctx.readFlag("XML_FILE_CREATED", false))
      {
         //bug 58326
         file_id = getRandomFilename();

         trans.clear();

         ASPCommand cmd = trans.addCustomFunction("VALIDATEFILENAMES",  "DOC_BRIEFCASE_UNPACKED_API.Validate_File_Names_", "OUT_1");
         cmd.addParameter("IN_1", docBriefcase);
         cmd.addParameter("IN_1", line_number);

         trans = mgr.perform(trans);
         error_msg =  trans.getValue("VALIDATEFILENAMES/DATA/OUT_1");

         if (!mgr.isEmpty(error_msg))
         {
            move_to_next = true;
            return;
         }

         checkin_file_exts = getFileExtensions();
         local_file_for_check_in = getFileNameWithNoExt();
         imported_path = getImportedPath(docBriefcase); //Bug Id 48298
         debug("Creating xml file..");
         //bug 58326, starts...
         xmlFileLoc = createXmlFile(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL","", "", local_file_for_check_in, "", "", action, "CHECKIN", file_id, imported_path);
         ctx.writeValue("FILEID", file_id);
         //bug 58326, ends...
         ctx.writeFlag("XML_FILE_CREATED", true);
         client_action = "ACCEPT_DOCUMENT";
         move_to_next = false;
      }
      else
      {
         ctx.writeFlag("XML_FILE_CREATED", false);
         //bug 58326
         file_id = ctx.readValue("FILEID","");
         String file_exts_checked_in = getFileExtensions();
         //bug 58326
         checkInFromBC(docBriefcase, line_number, doc_class,"ORIGINAL", file_id);
         acceptObjectsToDocument(docBriefcase, line_number); //Bug Id 62423
         //update states here ......
         move_to_next = true;
      }
      ctx.writeValue("WARNMESSAGE",sWarnMessage); //Bug Id 62423
   }

   //Bug Id 65509,Start
   //Bug Id 62423, Start
   private void acceptObjectsToDocument(String brief_no, String line_number)
   { 
      ASPCommand cmd;
      ASPManager mgr = getASPManager();
      
      trans.clear();

      cmd = trans.addCustomCommand("CHECKPLANTOBJECT","Doc_Briefcase_Unpacked_API.Check_Plant_Object");
      cmd.addParameter("OUT_1");
      cmd.addParameter("OUT_2");
      cmd.addParameter("IN_1",brief_no);
      cmd.addParameter("IN_1",line_number); 
      cmd.addParameter("IN_1","Accept");
      
      trans = mgr.perform(trans);

      String sDocNoNull = trans.getValue("CHECKPLANTOBJECT/DATA/OUT_1");
      String sDocNoMultiple = trans.getValue("CHECKPLANTOBJECT/DATA/OUT_2");

      trans.clear();           
      cmd = trans.addCustomCommand("CHECKOBJECT","Doc_Briefcase_Unpacked_API.Check_Object");
      cmd.addParameter("OUT_3");         
      cmd.addParameter("IN_1",brief_no);
      cmd.addParameter("IN_1",line_number); 
      cmd.addParameter("IN_1","Accept");
            
      trans = mgr.perform(trans);
            
      String sObjectDocNoNull = trans.getValue("CHECKOBJECT/DATA/OUT_3");

      if (!mgr.isEmpty(sDocNoNull)) {
         if (mgr.isEmpty(sWarnMessage)) 
             sWarnMessage = sDocNoNull;
         else
             sWarnMessage = sWarnMessage + ", " + sDocNoNull;
      }
      else if (!mgr.isEmpty(sObjectDocNoNull)) {
         if (mgr.isEmpty(sWarnMessage)) 
             sWarnMessage = sObjectDocNoNull;
         else
             sWarnMessage = sWarnMessage + ", " + sObjectDocNoNull;
      }
      else if (!mgr.isEmpty(sDocNoMultiple)) {
         if (mgr.isEmpty(sWarnMessage)) 
             sWarnMessage = sDocNoMultiple;
         else
             sWarnMessage = sWarnMessage + ", " + sDocNoMultiple;
      }
      if (doc_hdlr.isLastDocument()) {
         if (!mgr.isEmpty(sWarnMessage)) {
             sCheckObjectMessage = mgr.translate("DOCMAWEDMBCACCEPTOBJWARN: These documents have either invalid or multiple objects. Please check the document history.") +"\\n"+ sWarnMessage;
             bCheckObjectConfirm = true;
         }
      }
   }
   //Bug Id 62423, End
   //Bug Id 65509,End

   private void importBriefcase()
   {
      docBriefcase   = doc_hdlr.getActionProperty("BRIEF_NO"); //Bug Id 56784
      client_action  = "IMPORT_BRIEFCASE";
   }


   private void viewExported()
   {
      docBriefcase = doc_hdlr.getActionProperty("BRIEF_NO");
      String line_no = doc_hdlr.getDocumentProperty("LINE_NO");
      doc_class = doc_hdlr.getDocumentProperty("DOC_CLASS");
      file_name = getFileName(docBriefcase, line_no, "ORIGINAL");
      //Bug Id 48298, Start
      brief_imported_path = getImportedPath(docBriefcase);
      brief_imported_path = brief_imported_path.replaceAll("\\\\","\\\\\\\\");
      //Bug Id 48298, End
      client_action = "VIEW_EXPORTED";
      move_to_next = true;
   }


   private void resetBriefcase()
   {
      docBriefcase = doc_hdlr.getActionProperty("BRIEF_NO"); //Bug Id 56784
      client_action = "RESET_BRIEFCASE";
   }


   private String getFileName(String brief_no, String line_no, String doc_type)
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPCommand cmd = trans.addCustomFunction("GETFILENAME", "Doc_Briefcase_Unpacked_Api.Get_File_Name_", "OUT_STR");
      cmd.addParameter("IN_STR", brief_no);
      cmd.addParameter("IN_STR", line_no);
      cmd.addParameter("IN_STR", doc_type);
      trans = mgr.perform(trans);
      return trans.getValue("GETFILENAME/DATA/OUT_STR");
   }

   // Bug Id 48298, Start
   private String getImportedPath(String docBriefcaseNo)
   {
       ASPManager mgr = getASPManager();
       trans.clear();
       ASPCommand cmd = trans.addCustomFunction("EDMTIMPORTEDPATH","DOC_BRIEFCASE_API.Get_Imported_Path","OUT_1");
       cmd.addParameter("IN_1",docBriefcaseNo);

       trans = mgr.perform(trans);
       String Path = trans.getValue("EDMTIMPORTEDPATH/DATA/OUT_1");
       return Path;
   }

   //Bug Id 72460, start
   private void rollbackStatus() throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      String doc_class;
      String doc_no;
      String doc_sheet;
      String doc_rev;
      doc_hdlr.firstDocument();
      for (int i = 0; i < doc_hdlr.countDocuments()-1; i++)
      {
	  doc_class = doc_hdlr.getDocClass();
	  doc_sheet = doc_hdlr.getDocSheet();
	  doc_no = doc_hdlr.getDocNo();
	  doc_rev = doc_hdlr.getDocRev();
	  undoStateChange(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL", "CheckIn");
	  removeLastSimillarHistoryLines(doc_class, doc_no, doc_sheet, doc_rev, "EXPORTED");
	  doc_hdlr.nextDocument();
      }

      client_action = "CLOSE";

   }
   //Bug Id 72460, end

   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");

      blk.addField("RESULT").
          setHidden().
          setFunction("''");

      blk.addField("ERROR_MSG").
          setHidden().
          setFunction("''");

      blk.addField("DOC_CLASS").
          setHidden().
          setFunction("''");

      blk.addField("DOC_NO").
          setHidden().
          setFunction("''");

      blk.addField("DOC_SHEET").
          setHidden().
          setFunction("''");

      blk.addField("DOC_REV").
          setHidden().
          setFunction("''");

      blk.addField("DOC_TYPE").
          setHidden().
          setFunction("''");

      blk.addField("LOCAL_PATH").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P1").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P2").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P3").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P4").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P5").
          setHidden().
          setFunction("''");

      blk.addField("OUT_P6").
          setHidden().
          setFunction("''");

      blk.addField("DUMMY_1").
          setHidden().
          setFunction("''");

      blk.setView("DOC_ISSUE");

      blk.defineCommand("DOC_ISSUE_API","New__,Modify__,Remove__,PROMOTE_TO_APP_IN_PROGRESS__,PROMOTE_TO_APPROVED__,PROMOTE_TO_RELEASED__,PROMOTE_TO_OBSOLETE__,DEMOTE_TO_PRELIMINARY__");

      rowset = blk.getASPRowSet();
      cmdbar = mgr.newASPCommandBar(blk);
      tbl = mgr.newASPTable(blk);
      lay = blk.getASPBlockLayout();
      fmt = mgr.newASPHTMLFormatter();

      paramblk = mgr.newASPBlock("PARAMS");
      paramblk.addField("IN_STR");
      paramblk.addField("OUT_STR");
      paramblk.addField("IN_DATE", "Date");
      paramblk.addField("IN_NUMBER", "Number");

      super.preDefine();
   }


   private String getFileExtensions()
   {
      String file_names = doc_hdlr.getDocumentProperty("FILE_NAME");
      String temp_string;
      String return_str = "";

      StringTokenizer temp_tokenizer = new StringTokenizer(file_names,"|");

      while (temp_tokenizer.hasMoreElements())
      {
         temp_string =temp_tokenizer.nextToken();
         return_str += temp_string.substring(temp_string.lastIndexOf('.')+1)+ "^";
      }
      return return_str;
   }


   private String getFileNameWithNoExt()
   {
      String file_names = doc_hdlr.getDocumentProperty("FILE_NAME");
      String temp_string;
      String return_str = "";

      StringTokenizer temp_tokenizer = new StringTokenizer(file_names,"|");

      while (temp_tokenizer.hasMoreElements())
      {
         temp_string =temp_tokenizer.nextToken();
         return_str = temp_string.substring(0,temp_string.lastIndexOf('.'));
         break;

      }
      return return_str;
   }


   //===============================================================
   //  HTML
   //===============================================================


   /**
    * Sets the page heading depending on
    * the type of action
    */
   private String generatePageHeader(String action)
   {
      ASPManager mgr = getASPManager();
      String heading = null;
      String brief_no = docBriefcase;

      if ("EXPORT_BRIEFCASE".equals(action))
      {
         heading = "<font face=\"Verdana\" size=\"2\">"+mgr.translate("DOCMAWEDMBRIEFCASEHEADINGEXPORTBC: Exporting Briefcase " + brief_no)+"</b></font></br>";
         heading += "</br><font face=\"Verdana\" size=\"1\">"+ doc_class +" - " + doc_no +" - " + doc_sheet +" - " + doc_rev+"</font>";
      }
      if ("ACCEPT".equals(action))
      {
         heading = "<font face=\"Verdana\" size=\"2\">"+mgr.translate("DOCMAWEDMBRIEFCASEHEADINGACCEPT: Accepting Document")+"</b></font></br>";
         heading += "</br><font face=\"Verdana\" size=\"1\">"+doc_class +" - " + doc_no +" - " + doc_sheet +" - " + doc_rev+"</font>";
      }

      else if ("IMPORT_BRIEFCASE".equals(action))
         heading = mgr.translate("DOCMAWEDMBRIEFCASEHEADINGIMPORTBC: Importing Briefcase " + brief_no);
      else if ("VIEW_EXPORTED".equals(action))
         heading = mgr.translate("DOCMAWEDMBRIEFCASEHEADINGVIEWEXPORTED: View Exported Document");
      else if ("VIEWEXPORTEDWITHEXT".equals(action))
      {
         heading = mgr.translate("DOCMAWEDMBRIEFCASEHEADINGVIEWEXPORT: View Exported Document with External Viewer");
         heading += "</br><font face=\"Verdana\" size=\"2\">"+brief_no+"</font>";
         heading += "</br><font face=\"Verdana\" size=\"1\">"+doc_class +" - " + doc_no +" - " + doc_sheet +" - " + doc_rev+"</font>";
      }

      return heading;
   }

   protected AutoString getContents() throws FndException
   {
      debug(this+": getContents() {");

      AutoString out = getOutputStream();
      ASPManager mgr = getASPManager();

      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("DOCMAWEDMBRIEFCASETITLE: Edm Briefcase"));
      out.append("<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n");
      out.append("</head>\n");
      out.append("<body>\n");
      out.append("<font face='Arial'>\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<h4>");
      out.append(generatePageHeader(action));
      out.append("</h4>\n");
      out.append("  <hr>\n");
      out.append("  <input type=\"hidden\" name=\"LOCAL_CHECKOUT_PATH\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"PREPERFORMCHECK\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"LOCAL_FILE_NAME\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"CHECKED_IN_EXTS\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"VIEWFILEEXT\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"REDLINEFILEEXT\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"FILE_EXTS_CHECKED_IN\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"APPLIC_SELECTED\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"GOTLOCALCHECKOUTPATH\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"GOTCOPYTODIRPATH\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"EXTERNAL_VIEWER\" value=\"\">\n");//Bug Id 45979
      out.append("  <input type=\"hidden\" name=\"ROLLBACK_STATUS\" value=\"\">\n");//Bug Id 72460
      out.append("<p>\n");

      out.append(mgr.generateClientScript());
      out.append("</form>\n");
      out.append("</font>");
      out.append(DocmawUtil.getClientMgrObjectStr());
      out.append("</body>\n");
      out.append("</html>\n");


      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------


      //
      // main().. all execution on the client side starts here
      //
      appendDirtyJavaScript("var perform_client_action = true;\n");
      appendDirtyJavaScript("var cookie_string = document.cookie;\n");
      appendDirtyJavaScript("removePageIDCookies(cookie_string);\n");
      appendDirtyJavaScript("client_main();\n");


      appendDirtyJavaScript("function client_main()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   translateOCXStrings();\n"); // Bug Id 47864
      appendDirtyJavaScript("   var action = \"" + client_action + "\";\n");
      appendDirtyJavaScript("   if (action == \"CREATE_META_FILES\")\n");
      appendDirtyJavaScript("      createMetaFiles();\n");
      appendDirtyJavaScript("   else if (action == \"EXPORT_BRIEFCASE\")\n");
      appendDirtyJavaScript("      exportDocuments();\n");
      appendDirtyJavaScript("   else if (action == \"IMPORT_BRIEFCASE\")\n");
      appendDirtyJavaScript("      importBriefcase();\n");
      appendDirtyJavaScript("   else if (action == \"ACCEPT_DOCUMENT\")\n");
      appendDirtyJavaScript("      acceptDocument();\n");
      appendDirtyJavaScript("   else if (action == \"VIEW_EXPORTED\")\n");
      appendDirtyJavaScript("      viewExported();\n");
      appendDirtyJavaScript("   else if (action == \"VIEWEXPORTEDWITHEXT\") {\n");
      appendDirtyJavaScript("      viewExportedWithExt(); } \n");
      appendDirtyJavaScript("   else if (action == \"EXPORTREJECTED\") {\n");
      appendDirtyJavaScript("      exportRejectedDocuments(); } \n");
      appendDirtyJavaScript("   else if (action == \"RESET_BRIEFCASE\")\n");
      appendDirtyJavaScript("      resetBriefcase();\n");
      appendDirtyJavaScript("}\n");


      //Remove cookies prefixed with fndwebPageID_
      appendDirtyJavaScript("function removePageIDCookies(cookie_string)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    var cookie_array = cookie_string.split(';');\n");
      appendDirtyJavaScript("    var cookie_count = cookie_array.length;\n");
      appendDirtyJavaScript("    var cookies_to_remove = new Array();\n");
      appendDirtyJavaScript("    var cookie_remove_count = 0;\n");
      appendDirtyJavaScript("    for (i = 0; i < cookie_count; i++)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       var cookie = cookie_array[i];\n");
      appendDirtyJavaScript("       var re = new RegExp('[\t]*fndwebPageID_[0-9x]+[ \t]*=\\*');\n");
      appendDirtyJavaScript("       if (re.test(cookie))\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          var cookie_pieces = cookie.split('=');\n");
      appendDirtyJavaScript("          cookie_pieces[0] = cookie_pieces[0].substring(14);\n");
      appendDirtyJavaScript("          cookies_to_remove[cookie_remove_count]= trim_spaces(cookie_pieces[0]);\n");
      appendDirtyJavaScript("          cookie_remove_count++;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if (typeof(APP_PATH) != 'undefined')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       for(i = 0; i < cookie_remove_count;i++)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          removeCookie(cookies_to_remove[i],COOKIE_PATH);\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function trim_spaces(trim_this)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   return trim_this.replace(/^\\s*/,'').replace(/\\s*$/,'');\n");
      appendDirtyJavaScript("}\n");


      // Bug ID 41187, inoslk, If any errors occurred during the export process,
      // the user will be notified & the window will be closed.
      appendDirtyJavaScript("function createMetaFiles()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   var root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       oCliMgr.Init(\""+ mgr.getApplicationAbsolutePath() +"\");\n");
      appendDirtyJavaScript("       if (!checkFolderExists(root_path))\n");
      appendDirtyJavaScript("          return;\n");
      // Bug ID 62900, Start
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("       perform_client_action = false;\n"); 
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n"); 	
      // Bug ID 62900, End             
      appendDirtyJavaScript("       oCliMgr.CreateFolder(root_path, briefcase_no);\n");
      // Bug ID 62900, Start
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      var total_path = root_path + \"\\\\\" + briefcase_no;\n");
      appendDirtyJavaScript("      var err_msg = \"" +  mgr.translateJavaScript("DOCMAWEDMBRIEFCASEROOTFOLDERNOTEXIST: The briefcase folder :P1 already exists on disk.") + "\";\n");
      appendDirtyJavaScript("      err_msg = err_msg.replace(':P1', total_path);\n");
      appendDirtyJavaScript("      alert(err_msg);\n");
      appendDirtyJavaScript("      perform_client_action = false;\n"); 
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      // Bug ID 62900, End             
      appendDirtyJavaScript("      oCliMgr.SetCheckoutPath(root_path + \"\\\\\" + briefcase_no);\n");
       //Bug Id 72460, start
      appendDirtyJavaScript("       document.form.LOCAL_FILE_NAME.value = oCliMgr.ExecuteAction();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if (err.description.length > 0)\n");
      appendDirtyJavaScript("      alert(err.description);\n");
      appendDirtyJavaScript("      perform_client_action = false;\n"); 
      appendDirtyJavaScript("       try\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          oCliMgr.DeleteBriefcaseFolder(root_path + \"\\\\\" + briefcase_no + \"\\\\\");\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       catch(err)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          alert(err.description);\n");
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("   }\n"); 
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("   }\n");
      //Bug Id 72460, end
      // Bug ID 62900, Start
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n"); 	
      // Bug ID 62900, End     
      appendDirtyJavaScript("      oCliMgr.RenameFile(root_path + \"\\\\\" + briefcase_no + \"\\\\\" + \"" + mgr.encodeStringForJavascript(file_name) + "\", \"" + mgr.encodeStringForJavascript(new_file_name) + "\");\n");
       // Bug ID 62900, Start
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      var total_path = root_path + \"\\\\\" + briefcase_no+ \"\\\\\" + \"" + mgr.encodeStringForJavascript(new_file_name) +"\";\n");
      appendDirtyJavaScript("      var err_msg = \"" +  mgr.translateJavaScript("DOCMAWEDMBRIEFCASEROOTFILENOTEXIST: The briefcase file :P1 already exists on disk.") + "\";\n");
      appendDirtyJavaScript("      err_msg = err_msg.replace(':P1', total_path);\n");
      appendDirtyJavaScript("      alert(err_msg);\n");
      appendDirtyJavaScript("      perform_client_action = false;\n"); 
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n"); 
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      // Bug ID 62900, End     
      if (create_rejected_meta_files)
         appendDirtyJavaScript("       oCliMgr.CreateBriefcaseMetaFile(briefcase_no, root_path + \"\\\\\" + briefcase_no + \"\\\\\" + \"" + mgr.encodeStringForJavascript(new_file_name) + "\", \"CREATE_REJECTED\");\n");
      else
         appendDirtyJavaScript("       oCliMgr.CreateBriefcaseMetaFile(briefcase_no, root_path + \"\\\\\" + briefcase_no + \"\\\\\" + \"" + mgr.encodeStringForJavascript(new_file_name) + "\", \"CREATE_BRIEFCASE\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("      perform_client_action = false;\n"); // Bug Id 61755
      //Bug Id 72460, start
      appendDirtyJavaScript("       try\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          oCliMgr.DeleteBriefcaseFolder(root_path + \"\\\\\" + briefcase_no + \"\\\\\");\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       catch(err)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          alert(err.description);\n");
      appendDirtyJavaScript("          window.close();\n");
      appendDirtyJavaScript("       }\n");
      //Bug Id 72460, end
      appendDirtyJavaScript("      window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkFolderExists(path)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if (!oCliMgr.FolderExists(path))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        alert(\"" +  mgr.translate("DOCMAWEDMBRIEFCASEROOTPATHNOTEXIST: The briefcase root path does not exist. Select a proper root path and retry the operation.") + "\");\n");
      appendDirtyJavaScript("        return false;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    return true;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function exportDocuments()\n");
      appendDirtyJavaScript("{\n");
      //Bug Id 72460, start
      appendDirtyJavaScript("   var briefcase_no = \"" + docBriefcase + "\";\n");
      appendDirtyJavaScript("   var root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
      //Bug Id 72460, end
      if (caught_error)
      {
         appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
         appendDirtyJavaScript("   var root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
         appendDirtyJavaScript("   var log_file = root_path + \"\\\\\" + briefcase_no + \"\\\\\" + briefcase_no + \".log\";\n");
         appendDirtyJavaScript("   logError(log_file, \"" + document_key + "\", \"" + err_msg_log + "\");\n");
      }
      else if (!fileNotExist)
      {
         appendDirtyJavaScript("   try\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("       oCliMgr.Init(\""+ mgr.getApplicationAbsolutePath() +"\");\n");
         appendDirtyJavaScript("       document.form.LOCAL_FILE_NAME.value = oCliMgr.ExecuteAction();\n"); //Bug Id 72460
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   catch(err)\n");
         appendDirtyJavaScript("   {\n");
         //Bug Id 72460, start
	 appendDirtyJavaScript("       if(err.description.length > 0)\n");
         appendDirtyJavaScript("          alert(err.description);\n");
	 appendDirtyJavaScript("       document.form.ROLLBACK_STATUS.value = \"OK\";\n");
	 appendDirtyJavaScript("       document.form.submit();\n");
	 appendDirtyJavaScript("       perform_client_action = false;\n"); 
	 appendDirtyJavaScript("       try\n");
	 appendDirtyJavaScript("       {\n");
	 appendDirtyJavaScript("          oCliMgr.DeleteBriefcaseFolder(root_path + \"\\\\\" + briefcase_no + \"\\\\\");\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("   catch(err)\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("       alert(err.description);\n");
         appendDirtyJavaScript("       window.close();\n");
         appendDirtyJavaScript("   }\n");
	 //Bug Id 72460, end
         appendDirtyJavaScript("   }\n");
      }
      if (!"document.forms[0].submit();".equals(final_client_action))
         appendDirtyJavaScript("   refreshParentWindow(\"EXPORT_COMPLETE\");\n");

      appendDirtyJavaScript("}\n");

      fileNotExist = false; // supposing we r not using this any longer.

      //Bug id 45979 Start.
      if ("CHECK_EXTERNAL_VIEWER".equals(client_action))
      {

         appendDirtyJavaScript("var viewer = oCliMgr.GetExternalViewerApplication();\n");
         appendDirtyJavaScript("if (viewer == \"\" || viewer == null)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("    alert(\"" + mgr.translate("DOCMAWEDMBRIEFCASEEXTVIEWERAPPNOTEXIST: External Viewer application has not been configured. Please set the path in User Settings.") + "\");\n");
         appendDirtyJavaScript("    window.close();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("else\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.EXTERNAL_VIEWER.value = \"AVAILABLE\";\n");
         appendDirtyJavaScript("   submit()\n");
         appendDirtyJavaScript("}\n");
      }
      //Bug id 45979 End.


      //bug id 74523 Start
      if("GET_PATH_FIRST".equals(client_action))
      {
	  appendDirtyJavaScript("   var root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
	  appendDirtyJavaScript("document.form.LOCAL_CHECKOUT_PATH.value = root_path;\n");

      }
       //bug id 74523 End

      //Bug Id 72460, start
      if ("CLOSE".equals(client_action))
      {
	  appendDirtyJavaScript("   window.close();\n");
	  debug("client_action = CLOSE");
      }
      //Bug Id 72460, end

      appendDirtyJavaScript("function importBriefcase()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   var root_path = oCliMgr.RegGetValue(\"HKEY_CURRENT_USER\", \"Software\\\\IFS\\\\Document Management\\\\Settings\", \"BriefcaseRootPath\");\n");
      appendDirtyJavaScript("   oCliMgr.Init(\""+ mgr.getApplicationAbsolutePath() +"\");\n");
      appendDirtyJavaScript("   try{");
      appendDirtyJavaScript("      var template_file = oCliMgr.OpenFileDialog(\"EXCEL^XLS\", root_path + \"\\\\\" + briefcase_no);\n");
      //Bug ID 46719, inoslk, start
      appendDirtyJavaScript("      pos = template_file.indexOf(briefcase_no + '.XLS');\n");
      appendDirtyJavaScript("      if ( pos == -1 )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("          alert(\"" + mgr.translate("DOCMAWEDMBCNOVALIDTEMPLATE: Cannot continue importing the briefcase, because the template file you have chosen is not valid for this briefcase.") + "\");\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         if(\"<ERROR>\" != oCliMgr.UnpackBriefcaseMetaFile(briefcase_no, template_file))\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            refreshParentWindow(\"IMPORT_COMPLETE\");\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("      }\n");
      //Bug ID 46719, inoslk, end
      appendDirtyJavaScript("   }catch(err){}");
      appendDirtyJavaScript("   closeWindow();\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function acceptDocument()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       oCliMgr.Init(\""+ mgr.getApplicationAbsolutePath() +"\");\n");
      appendDirtyJavaScript("       executeAction();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function viewExported()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var doc_class = \"" + doc_class + "\";\n");
      appendDirtyJavaScript("   var file_name = \"" + mgr.encodeStringForJavascript(file_name) + "\";\n");
      appendDirtyJavaScript("   var root_path = \"" + mgr.encodeStringForJavascript(brief_imported_path) + "\";\n"); //Bug Id 48298, Get the imported path
      // Bug ID 43497, inoslk, Removed DOC_CLASS from the file path passed into oCliMgr.LaunchFile
      appendDirtyJavaScript("   oCliMgr.LaunchFile(root_path + \"\\\\\" + file_name);\n"); //Bug Id 48298
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function viewExportedWithExt()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   var doc_class = \"" + doc_class + "\";\n");
      appendDirtyJavaScript("   var file_name = \"" + mgr.encodeStringForJavascript(file_name) + "\";\n");
      appendDirtyJavaScript("   var root_path = \"" + mgr.encodeStringForJavascript(brief_imported_path) + "\";\n"); //Bug Id 48298, Get the imported path
      // Bug ID 43497, inoslk, Removed DOC_CLASS from the file path passed into oCliMgr.LaunchFileWithExternalViewer
      appendDirtyJavaScript("   oCliMgr.LaunchFileWithExternalViewer(root_path + \"\\\\\" + file_name);\n"); //Bug Id 48298
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function executeAction()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       document.form.LOCAL_FILE_NAME.value = oCliMgr.ExecuteAction();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("       perform_client_action = false;\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function exportRejectedDocuments()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       refreshParentWindow(\"EXPORT_REJ_COMPLETE\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function resetBriefcase()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var briefcase_no = \"" + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   try\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       oCliMgr.Init(\""+ mgr.getApplicationAbsolutePath() +"\");\n");
      appendDirtyJavaScript("       var bReturn = oCliMgr.ResetBriefcase(briefcase_no);\n");
      appendDirtyJavaScript("       if (bReturn == \"TRUE\")\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          refreshParentWindow(\"RESET_COMPLETE\");\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   catch(err)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       alert(err.description);\n");
      appendDirtyJavaScript("       window.close();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function refreshParentWindow(message)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   eval(\"opener.refreshPage(message)\");\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function closeWindow()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   window.close();\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function translateOCXStrings()\n");
      appendDirtyJavaScript("{\n");
      String [][] climgr_translations = DocmawConstants.getCliMgrTranslations(mgr);
      for (int i = 0; i < climgr_translations.length; i++)
      {
         appendDirtyJavaScript("   oCliMgr.AddLocalizedString(\"" + climgr_translations[i][0] + "\", \"");
         appendDirtyJavaScript(climgr_translations[i][1]);
         appendDirtyJavaScript("\");\n");
      }
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function logError(log_file, document_key, err_msg_log)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var d = new Date();\n");
      appendDirtyJavaScript("   var LOG_HEADER = \""   + mgr.translate("DOCMAWEDMBRIEFCASEBCLOGHEADER: B R I E F C A S E   E X P O R T   L O G") + "\";\n");
      appendDirtyJavaScript("   var LOG_DESC = \""     + mgr.translate("DOCMAWEDMBRIEFCASEBCLOGDESC: The following problems were encountered while exporting briefcase") + " " + mgr.encodeStringForJavascript(docBriefcase) + "\";\n");
      appendDirtyJavaScript("   var LOG_DATETIME = \"" + mgr.translate("DOCMAWEDMBRIEFCASEBCLOGDDATETIME: Date/Time") + "\";\n");
      appendDirtyJavaScript("   var LOG_DOCUMENT = \"" + mgr.translate("DOCMAWEDMBRIEFCASEBCLOGDOCUMENT: Document") + "\";\n");
      appendDirtyJavaScript("   var LOG_ERR_MSG = \""  + mgr.translate("DOCMAWEDMBRIEFCASEBCLOGERRMSG: Error Message") + "\";\n");
      appendDirtyJavaScript("   var heading = positionString(0, LOG_DATETIME, \"\");\n");
      appendDirtyJavaScript("   heading = positionString(28, LOG_DOCUMENT, heading);\n");
      appendDirtyJavaScript("   heading = positionString(56, LOG_ERR_MSG, heading);\n");
      appendDirtyJavaScript("   if (!oCliMgr.FileExists(log_file)){\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"-------------------------------------------------------------------------------\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"--\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"--\" + centerText(80, LOG_HEADER));\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"--\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"--\" + centerText(80, LOG_DESC));\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"--\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"-------------------------------------------------------------------------------\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"\");\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, heading);\n");
      appendDirtyJavaScript("      oCliMgr.appendToFile(log_file, \"------------------------    ------------------------    ------------------------\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   var log = \"[\" + d.getDate() + '/' + d.getMonth() + '/' + d.getYear() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds() + \"]\";\n");
      appendDirtyJavaScript("   log = positionString(0, log, \"\");\n");
      appendDirtyJavaScript("   log = positionString(28, document_key, log);\n");
      appendDirtyJavaScript("   log = positionString(56, err_msg_log, log);\n");
      appendDirtyJavaScript("   oCliMgr.appendToFile(log_file, log);\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function centerText(cols, str)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var no_spaces = Math.floor((cols - str.length)/2);\n");
      appendDirtyJavaScript("   for(x = 0; x < no_spaces; x++)\n");
      appendDirtyJavaScript("      str = \" \" + str;\n");
      appendDirtyJavaScript("   return str;\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function positionString(xcord, source_str, dest_str)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var no_spaces = xcord - dest_str.length;\n");
      appendDirtyJavaScript("   for(x = 0; x < no_spaces; x++)\n");
      appendDirtyJavaScript("      dest_str += \" \";\n");
      appendDirtyJavaScript("   dest_str += source_str;\n");
      appendDirtyJavaScript("   return dest_str;\n");
      appendDirtyJavaScript("}\n");

      //Bug Id 62423, Start
      if (bCheckObjectConfirm) {
          appendDirtyJavaScript("alert('"+sCheckObjectMessage+"'); \n");
          bCheckObjectConfirm = false;
      }
      //Bug Id 62423, End


      if (!mgr.isEmpty(error_msg)) //never save this error msg in the context!!
      {
	 //Bug ID 47320, inoslk, changed string quotations for JS error text.
         appendDirtyJavaScript("alert('"+ mgr.encodeStringForJavascript(error_msg) +"'); \n");
      }

      //WARNING: do whatever u wanna do in client prior to next line.
      //it will either close the window or submit the form again.
      appendDirtyJavaScript("if (perform_client_action == true)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(final_client_action);
      appendDirtyJavaScript("}\n");

      debug(this+": getContents() }");

      return out;
   }
}
