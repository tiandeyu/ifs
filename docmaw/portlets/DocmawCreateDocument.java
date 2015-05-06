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
 * File        : DocmawCreateDocument.java
 * Description : Portlet to easily create a new document.
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    MDAHSE     2007-02-14  Created.
 *    NaLrlk     2007-08-13  XSS Correction.
 *    SHTHLK     2009-07-09  Bug Id 78770, Added Number counter fields to the portal
 *    SHTHLK     2009-07-13  Bug Id 78770, Cleaned up the code.
 *    SHTHLK     2009-07-14  Bug Id 84283, Enabled handling of Document Class with Stucture flag set.
 *    AMNALK     2010-07-07  Bug Id 91402, Modified run() and printContents() to clear the context values.
 * ----------------------------------------------------------------------------
 *
 */

package ifs.docmaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;
import java.util.*;
import java.io.*;
import java.lang.*;

import ifs.docmaw.edm.DocumentTransferHandler;

/**
 */

public class DocmawCreateDocument extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.portlets.DocmawCreateDocument");

   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================

   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;
   private ASPBuffer  data;


   //==========================================================================
   //  Mutable attributes
   //==========================================================================


   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================

   private String             transfer_url;
   private String             old_doc_class;
   private String             old_title;

   boolean      display_format         = false;
   boolean      display_language       = false;
   boolean      display_numbergen      = false; //Bug Id 78770
   //==========================================================================
   //  Construction
   //==========================================================================

   public DocmawCreateDocument( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
   }

   public ASPPage construct() throws FndException
   {
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doReset() throws FndException
   {
      super.doReset();
   }

   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      DocmawCreateDocument page = (DocmawCreateDocument)(super.clone(mgr));

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      return page;
   }

   protected void preDefine()
   {
      ctx = getASPContext();

      blk = newASPBlock("MAIN");

      addField(blk, "DOC_CLASS"       ).setFunction("''").setHidden();
      addField(blk, "BOOKING_LIST"    ).setFunction("''").setHidden();
      addField(blk, "ID1"             ).setFunction("''").setHidden();
      addField(blk, "ID2"             ).setFunction("''").setHidden();
      addField(blk, "DOC_NO"          ).setFunction("''").setHidden();
      addField(blk, "DOC_SHEET"       ).setFunction("''").setHidden();
      addField(blk, "FIRST_SHEET_NO"  ).setFunction("''").setHidden();
      addField(blk, "FIRST_REVISION"  ).setFunction("''").setHidden();
      addField(blk, "NUMBER_GENERATOR").setFunction("''").setHidden();
      addField(blk, "NUMBER_COUNTER"  ).setFunction("''").setHidden();
      addField(blk, "DOC_REV"         ).setFunction("''").setHidden();
      addField(blk, "TITLE"           ).setFunction("''").setHidden();
      addField(blk, "DUMMY1"          ).setFunction("''").setHidden();
      addField(blk, "DUMMY2"          ).setFunction("''").setHidden();
      addField(blk, "OBJID"           ).setFunction("''").setHidden();
      addField(blk, "OBJVERSION"      ).setFunction("''").setHidden();
      addField(blk, "INFO"            ).setFunction("''").setHidden();
      addField(blk, "ACTION"          ).setFunction("''").setHidden();
      addField(blk, "ATTR"            ).setFunction("''").setHidden();
      addField(blk, "FORMAT_SIZE"     ).setFunction("''").setHidden();
      addField(blk, "LANGUAGE_CODE"   ).setFunction("''").setHidden();
      //Bug Id 84283, Start
      addField(blk, "DOC_STRUCTURE"   ).setFunction("''").setHidden();
      addField(blk, "DUMMY3"          ).setFunction("''").setHidden();
      addField(blk, "DUMMY4"          ).setFunction("''").setHidden();
      addField(blk, "DUMMY5"          ).setFunction("''").setHidden();
      //Bug Id 84283, End
      blk.setView("DOC_TITLE");

      tbl = newASPTable( blk );
      tbl.disableQueryRow();
      tbl.disableQuickEdit();
      tbl.disableRowCounter();
      tbl.disableRowSelect();
      tbl.disableRowStatus();
      tbl.unsetSortable();

      rowset = blk.getASPRowSet();
      getASPManager().newASPCommandBar(blk);

      init();
   }

   protected void init()
   {
      blk    = getASPBlock("MAIN");
      rowset = blk.getASPRowSet();
      tbl    = getASPTable(blk.getName());
   }

   protected void runCustom()
   {
   }

   protected void run()
   {
      String command = readValue("CMD");

      if ("CREATE".equals(command) || "CREATEEDIT".equals(command))
      {
         ASPManager           mgr    = getASPManager();
         ASPTransactionBuffer trans  = mgr.newASPTransactionBuffer();
         ASPCommand           cmd    = mgr.newASPCommand();

         String title = readValue("TITLE");
         String doc_class = readValue("DOC_CLASS");
         String doc_no = "*";
         String format_size = readValue("FORMAT_SIZE");
         String language_code = readValue("LANGUAGE_CODE");
         //Bug Id 78770, Start
	 String number_generator_trans = readValue("NUMBER_GENERATOR_TRANS");
	 String number_generator = readValue("NUMBER_GENERATOR");
	 String booking_list     = readValue("BOOKING_LIST");
	 String id1 = readValue("ID1");
	 String id2 = readValue("ID2");
	 boolean bError=false;
	 //Bug Id 78770, End
         String first_sheet;
         String first_rev;
	 String doc_structure; //Bug Id 84283
 

         // About error handling:

         // We will try to handle errors in a nicer way than normally
         // and present them in the status area in the lower part of
         // the portlet, to avoid annoying pop ups and similar. If an
         // error is encountered, we write the error to the context
         // variable ERROR. When the portlet loads again and
         // printContents() is called again, we will read this
         // variable there, display an error and do nothing else. We
         // also report back what field that had a wrong value and the
         // value that the user tried with. This way we can fill the
         // field with that value again.

         if (mgr.isEmpty(doc_class))
         {
            printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCLASSEMPTY: Please enter a value for Document Class."),
                       "DOC_CLASS");
         }
         else if (mgr.isEmpty(title))
         {
            printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTTITLEEMPTY: Please enter a value for Title."),
                       "TITLE");
         }
         else
         {

            doc_class = doc_class.toUpperCase();

            cmd.defineCustomFunction(this, "Doc_Class_API.Check_Exist","DUMMY1");
            cmd.addParameter(this, "DOC_CLASS", doc_class);

            trans.clear();

            trans.addCommand("CLASSEXISTCHECK", cmd);

            if (!mgr.isEmpty(format_size))
            {
               cmd = trans.addCustomCommand("FORMATEXISTCHECK", "Doc_Class_Format_API.Exist");
               cmd.addParameter(this, "FORMAT_SIZE", format_size);
               cmd.addParameter(this, "DOC_CLASS", doc_class);
            }

            if (!mgr.isEmpty(language_code))
            {
               cmd = trans.addCustomCommand("LANGEXISTCHECK", "Application_Language_API.Exist");
               cmd.addParameter(this, "LANGUAGE_CODE", language_code);
            }

            trans = mgr.perform(trans);

            String class_exist  = trans.getValue("CLASSEXISTCHECK/DATA/DUMMY1");

            if ("FALSE".equals(class_exist))
            {
               printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCLASSNOTEXIST: Document Class &1 does not exist.", doc_class),
                          "DOC_CLASS");
            }
            else
            {

               // 2007-02-23 MDAHSE:

               // Saving class and title to the profile does not work
               // because the profile is not written if writeProfileValue()
               // is called outside submitCustomization(). However, I got a
               // feeling that Mangala did not like this restriction
               // himself, so I'll let the code be here and who knows when
               // it might start to work? :) If it starts to work, please
               // remove this comment...

               writeProfileValue("OLD_DOC_CLASS", doc_class);
               writeProfileValue("OLD_TITLE",     title);


               trans.clear();

               cmd = mgr.newASPCommand();

               cmd.defineCustomFunction(this, "Doc_Class_Default_API.Get_Default_Value_", "FIRST_SHEET_NO");
               cmd.addParameter(this, "DOC_CLASS", doc_class);
               cmd.addParameter(this, "DUMMY1",    "DocTitle");
               cmd.addParameter(this, "DUMMY2",    "DOC_SHEET");

               trans.addCommand("FIRSTSHEET", cmd);

               cmd = mgr.newASPCommand();

               cmd.defineCustomFunction(this, "Doc_Class_Default_API.Get_Default_Value_", "FIRST_REVISION");
               cmd.addParameter(this, "DOC_CLASS", doc_class);
               cmd.addParameter(this, "DUMMY1",    "DocTitle");
               cmd.addParameter(this, "DUMMY2",    "DOC_REV");

               trans.addCommand("FIRSTREV", cmd);

               //Bug Id 84283, Start
	       cmd = mgr.newASPCommand();
	       cmd.defineCustomFunction(this,"Doc_Class_Default_API.Get_Default_Value_","DOC_STRUCTURE");
	       cmd.addParameter(this,"DOC_CLASS",doc_class);
	       cmd.addParameter(this,"DUMMY1","DocTitle");
	       cmd.addParameter(this,"DUMMY2","STRUCTURE");
	       trans.addCommand("DOCSTRUCTURE", cmd);
	       //Bug Id 84283, End
               trans = mgr.perform(trans);

               first_sheet      = trans.getValue("FIRSTSHEET/DATA/FIRST_SHEET_NO");
               first_rev        = trans.getValue("FIRSTREV/DATA/FIRST_REVISION");
               doc_structure    = trans.getValue("DOCSTRUCTURE/DATA/DOC_STRUCTURE");//Bug Id 84283
	       //Bug Id 78770, Start
               if (mgr.isEmpty(booking_list) && "ADVANCED".equals(number_generator))
	       {
	          if (mgr.isEmpty(id1)) 
		  {
		     printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENID1EMPTY: No Booking List is selected and no default Number Counter is configured for document class &1.",doc_class),
		     "ID1");
		     bError = true;
		  }
		  else if (mgr.isEmpty(id2)) 
	          {
                    
		     trans.clear();
		     cmd = mgr.newASPCommand();   
		     cmd.defineCustomFunction(this, "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
		     cmd.addParameter(this,"DUMMY1", id1);
			  
		     trans.addCommand("NUMCOUNT2", cmd);
		     trans = mgr.perform(trans);
	
		     id2  = trans.getValue("NUMCOUNT2/DATA/ID2");
		     if ((mgr.isEmpty (id2)) || ("0".equals(id2))) 
		     {
                        printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENID1EMPTY: Cannot find a suitable Number Counter for ID1 = &1. Specify ID2 or make sure that a default Number Counter exists.",id1),
		        "");
		        bError = true;
		     }
		  }
	       }
	       if (mgr.isEmpty(number_generator))
	       {
		    trans.clear();
		    cmd = mgr.newASPCommand();
		    cmd.defineCustomFunction(this, "Doc_Class_Default_API.Get_Default_Value_","NUMBER_GENERATOR");
		    cmd.addParameter(this, "DOC_CLASS", doc_class);
		    cmd.addParameter(this, "DUMMY1",    "DocTitle");
		    cmd.addParameter(this, "DUMMY2",    "NUMBER_GENERATOR");
	
		    trans.addCommand("NUMBERGENERATOR", cmd);
		    cmd = mgr.newASPCommand();
		    cmd.defineCustomFunction(this, "Doc_Class_Default_API.Get_Default_Value_", "NUMBER_COUNTER");
		    cmd.addParameter(this, "DOC_CLASS", doc_class);
		    cmd.addParameter(this, "DUMMY1",    "DocTitle");
		    cmd.addParameter(this, "DUMMY2",    "NUMBER_COUNTER");
		      
		    trans.addCommand("NUMCOUNT", cmd);
		    
		    trans = mgr.perform(trans);
		    number_generator = trans.getValue("NUMBERGENERATOR/DATA/NUMBER_GENERATOR");
		    id1  = trans.getValue("NUMCOUNT/DATA/NUMBER_COUNTER");
		    trans.clear();
		    if ("ADVANCED".equals(number_generator))
		    {
			cmd = mgr.newASPCommand();   
			cmd.defineCustomFunction(this, "Doc_Number_Counter_API.Get_Default_Id2", "ID2");
			cmd.addParameter(this,"DUMMY1", id1);
			  
			trans.addCommand("NUMCOUNT2", cmd);
			trans = mgr.perform(trans);
	
			id2  = trans.getValue("NUMCOUNT2/DATA/ID2");
			if ((mgr.isEmpty (id2)) || ("0".equals(id2))) {
                            printError(mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENID1EMPTY: Cannot find a suitable Number Counter for ID1 = &1. Specify ID2 or make sure that a default Number Counter exists.",id1),
			    "");
			    bError = true;
			}
		    }
	        }
	       //Bug Id 78770, End
	       //Bug Id 78770, End
               if (!bError && ("ADVANCED".equals(number_generator)))//Bug Id 78770
               {
                  trans.clear();

                  cmd = mgr.newASPCommand();

                  cmd.defineCustomFunction(this, "DOC_TITLE_API.Generate_Doc_Number","DOC_NO");

                  cmd.addParameter(this, "DOC_CLASS",    doc_class);
		  //Bug Id 78770,start
                  cmd.addParameter(this, "BOOKING_LIST", booking_list);
                  cmd.addParameter(this, "ID1",          id1);
                  cmd.addParameter(this, "ID2",          id2);
                  //Bug Id 78770,End
                  cmd.addParameter(this, "ATTR",         "");

                  trans.addCommand("GENNUMBER", cmd);

                  trans = mgr.perform(trans);

                  doc_no  = trans.getValue("GENNUMBER/DATA/DOC_NO");

               }
               if (!bError) //Bug Id 78770
	       {
		   trans.clear();
                   //Bug Id 84283, Sent the doc structure
		   cmd = trans.addCustomCommand("CREATETITLE","Doc_Title_API.Create_New_Document_");
    
		   cmd.setParameter(this,"DOC_CLASS", doc_class);
		   cmd.setParameter(this,"DOC_NO",    doc_no);
		   cmd.setParameter(this,"DOC_SHEET", first_sheet);
		   cmd.setParameter(this,"DOC_REV",   first_rev);
		   cmd.setParameter(this,"TITLE",     title);
    		   cmd.setParameter(this,"DUMMY1",      "");
		   cmd.setParameter(this,"DUMMY2",      "");
		   cmd.setParameter(this,"DUMMY3",      "");
		   cmd.setParameter(this,"DUMMY4",      "");
		   cmd.setParameter(this,"DUMMY5",      "");
		   cmd.setParameter(this,"DOC_STRUCTURE",  doc_structure);
		   trans = mgr.perform(trans);
    
		   // Save values so that we can get them in printContents() and
		   // create URLs and transfer and stuff...
    
		   if ("*".equals(doc_no))
		   {
		      doc_no = trans.getValue("CREATETITLE/DATA/DOC_NO");
		      ctx.writeValue("DOC_NO", doc_no);
		   }
		   else
		   {
		      ctx.writeValue("DOC_NO", doc_no);
		   }
    
		   String attr = "";
    
		   if (!mgr.isEmpty(language_code))
		      attr = attr + "LANGUAGE_CODE" + (char)31 + language_code + (char)(30);
    
		   if (!mgr.isEmpty(format_size))
		      attr = attr + "FORMAT_SIZE" + (char)31 + format_size + (char)(30);
    
		   if (!"".equals(attr))
		   {
		      trans.clear();
    
		      ASPQuery query = trans.addQuery("GET_ID_VER", "SELECT OBJID, OBJVERSION FROM DOC_ISSUE WHERE DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
    
		      query.addParameter(this, "DOC_CLASS", doc_class);
		      query.addParameter(this, "DOC_NO",    doc_no);
		      query.addParameter(this, "DOC_SHEET", first_sheet);
		      query.addParameter(this, "DOC_REV",   first_rev);
    
		      trans = mgr.perform(trans);
    
		      String objid      = trans.getValue("GET_ID_VER/DATA/OBJID");
		      String objversion = trans.getValue("GET_ID_VER/DATA/OBJVERSION");
    
		      trans.clear();
    
		      cmd = trans.addCustomCommand("LANG_FORMAT", "Doc_Issue_API.Modify__");
    
		      cmd.addParameter(this, "INFO","");
		      cmd.addParameter(this, "OBJID", objid);
		      cmd.addParameter(this, "OBJVERSION", objversion);
		      cmd.addParameter(this, "ATTR", attr);
		      cmd.addParameter(this, "ACTION","DO");
    
		      trans = mgr.perform(trans);
    
		   }
	       }
               ctx.writeValue("DOC_SHEET", first_sheet);
               ctx.writeValue("DOC_REV",   first_rev);
               ctx.writeValue("COMMAND",   command);
            }

         }

         // We need these values in all cases, both when we get an
         // error and when it works fine.

         ctx.writeValue("DOC_CLASS", doc_class);
         ctx.writeValue("OLD_TITLE", title);
         ctx.writeValue("FORMAT_SIZE", format_size);
         ctx.writeValue("LANGUAGE_CODE", language_code);
	 //Bug Id 78770, Start
	 ctx.writeValue("ID1", id1);
	 ctx.writeValue("ID12", id2);
	 ctx.writeValue("BOOKING_LIST", booking_list);
	 ctx.writeValue("NUMBER_GENERATOR", number_generator);
	 ctx.writeValue("NUMBER_GENERATOR_TRANS", number_generator_trans);
	 //Bug Id 78770, End
      }
      // Bug Id 91402, start
      else
      {
         ctx.writeValue("COMMAND", "");
         ctx.writeValue("DOC_NO", "");
         ctx.writeValue("OLD_TITLE", "");
         ctx.writeValue("DOC_CLASS", "");
         ctx.writeValue("FORMAT_SIZE", "");
         ctx.writeValue("LANGUAGE_CODE", "");
         ctx.writeValue("ID1", "");
         ctx.writeValue("ID12", "");
         ctx.writeValue("BOOKING_LIST", "");
         ctx.writeValue("NUMBER_GENERATOR", "");
         ctx.writeValue("NUMBER_GENERATOR_TRANS", "");
      }
      // Bug Id 91402, end

   }

   private void printError (String error_message, String field_name)
   {
      ctx.writeValue("ERROR", error_message);
      ctx.writeValue("FIELD_NAME", field_name);
   }

   //==========================================================================
   //
   //==========================================================================

   public String getTitle( int mode )
   {
      return(translate(getDescription()));
   }


   public void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      String doc_class = ctx.readValue("DOC_CLASS", "");
      String doc_no    = ctx.readValue("DOC_NO",    "<EMPTY>");
      String doc_sheet = ctx.readValue("DOC_SHEET", "");
      String doc_rev   = ctx.readValue("DOC_REV",   "");

      String format_size     = ctx.readValue("FORMAT_SIZE", "");
      String language_code   = ctx.readValue("LANGUAGE_CODE",   "");

      String command   = ctx.readValue("COMMAND",   "<EMPTY>");

      String error      = ctx.readValue("ERROR",      "<EMPTY>");
      String field_name = ctx.readValue("FIELD_NAME", "<EMPTY>");
      String old_title  = ctx.readValue("OLD_TITLE",  "");
      //Bug Id 78770, Start
      //Bug Id 91402, start - Read the values from context
      String number_generator_trans = ctx.readValue("NUMBER_GENERATOR_TRANS","");
      String number_generator = ctx.readValue("NUMBER_GENERATOR","");
      String booking_list     = ctx.readValue("BOOKING_LIST","");
      String id1 = ctx.readValue("ID1","");
      String id2 = ctx.readValue("ID2","");
      //Bug Id 91402, end
      //Bug Id 78770, End
      // Provide a nice default value for the title

      if (mgr.isEmpty(old_title))
      {
         old_title = mgr.translate("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTTITLEFIELDDEFTEXT: My document");
      }

      // Empty values so that we won't get them again by mistake.

      ctx.writeValue("ERROR",      "");
      ctx.writeValue("FIELD_NAME", "");
      ctx.writeValue("OLD_TITLE",  "");

      String url;

      boolean transfer_to_edm = false;

      ASPBuffer action    = mgr.newASPBuffer();
      ASPBuffer documents = mgr.newASPBuffer();
      ASPBuffer doc       = documents.addBuffer("DATA");

      display_format       = readProfileFlag("DISPLAY_FORMAT", false);
      display_language     = readProfileFlag("DISPLAY_LANGUAGE", false);
      display_numbergen     = readProfileFlag("DISPLAY_NUMBERGEN", false);//Bug Id 78770
      printHiddenField("CMD","");

      beginTransparentTable();

      nextTableRow();

        beginTableCell();
          printText("DOCMAWCREATEDOCUMENTDOCCLASSHEADING: Document Class");
        endTableCell();

        beginTableCell();
          printText("DOCMAWCREATEDOCUMENTTITLEHEADING: Title");
        endTableCell();

      nextTableRow();

        beginTableCell();
          printField("DOC_CLASS", doc_class, 10, 12);
          printSpaces(1);
          printDynamicLOV("DOC_CLASS", "DOC_CLASS");
        endTableCell();

        beginTableCell();
          printField("TITLE", old_title, 24, 250);
        endTableCell();

      if (display_language || display_format)
      {
         nextTableRow();
      }

      if (display_format)
      {
        beginTableCell();
          printText("DOCMAWCREATEDOCUMENTFORMATHEADING: Format");
        endTableCell();
      }

      if (display_language)
      {
        beginTableCell();
          printText("DOCMAWCREATEDOCUMENTLANGUAGEHEADING: Language Code");
        endTableCell();
      }

      if (display_language || display_format)
      {
         nextTableRow();
      }

      if (display_format)
      {
        beginTableCell();
          printField("FORMAT_SIZE", format_size, 10, 10);
          printSpaces(1);
          // Yes, this is a really ugly hack, but what can you do if
          // you want a value from another field to be used in the
          // WHERE condition for the LOV... Feel free to fix in a
          // cleaner way, if possible.
          printDynamicLOV("FORMAT_SIZE", "DOC_CLASS_FORMAT_LOV", mgr.translate("DOCMAWCREATEDOCUMENTFORMATLOVTITLE: Select Format"),
                           "DOC_CLASS = \\'' + getPortletField('" + getId() + "','DOC_CLASS').value + '\\'");
        endTableCell();
      }

      if (display_language)
      {
        beginTableCell();
          printField("LANGUAGE_CODE", language_code, 10, 2); //Bug Id 78770, Increased the field length
          printSpaces(1);
          printDynamicLOV("LANGUAGE_CODE", "APPLICATION_LANGUAGE");
        endTableCell();
      }
      //Bug Id 78770, Start
      if (display_numbergen)
      {
	 
         nextTableRow();

         beginTableCell();
         printText("DOCMAWCREATEDOCUMENTNUMBERGEN: Number Generator");
         endTableCell();

	 beginTableCell();
         printText("DOCMAWCREATEDOCUMENTBOOKINGLIST: Booking List");
         endTableCell();

	 nextTableRow();

         beginTableCell();
         printField("NUMBER_GENERATOR_TRANS", number_generator_trans,10,20);
         printSpaces(1);
         endTableCell();

	 beginTableCell();
         printField("BOOKING_LIST", booking_list,10,20);
	 appendToHTML("<span id='_" + getId() + "_BKLREADONLY' style='display:inline'>\n");
	 printLOV("BOOKING_LIST", "docmaw/BookListLov.page?ID1=\'+ getPortletField('" + getId() + "','ID1').value+ \'&ID2=\'+ getPortletField('" + getId() + "','ID2').value+ \'",mgr.translate("DOCMAWCREATEDOCUMENTBOOKLISTTLOVTITLE: Select Booking List"));
         appendToHTML("</span>\n");
	 printSpaces(1);
         endTableCell();


	 nextTableRow();

         beginTableCell();
         printText("DOCMAWCREATEDOCUMENTID1: Number Counter ID1");
         endTableCell();

         beginTableCell();
         printText("DOCMAWCREATEDOCUMENTID2: Number Counter ID2");
         endTableCell();

         beginTableCell();
         printHiddenField("NUMBER_GENERATOR", number_generator);
         endTableCell();
	 nextTableRow();

         beginTableCell();
         printField("ID1", id1,10,10);
         appendToHTML("<span id='_" + getId() + "_ID1READONLY' style='display:inline'>\n");
	 printLOV("ID1", "docmaw/Id1Lov.page");
	 appendToHTML("</span>\n");
         printSpaces(1);
         endTableCell();

         beginTableCell();
         printField("ID2", id2,10,10);
	 appendToHTML("<span id='_" + getId() + "_ID2READONLY' style='display:inline'>\n");
	 printLOV("ID2", "docmaw/Id2Lov.page",mgr.translate("DOCMAWCREATEDOCUMENTID2LOVTITLE: Select Number Counter ID2"),
                           "ID1 = \\'' + getPortletField('" + getId() + "','ID1').value + '\\'");
	 appendToHTML("</span>\n");
         printSpaces(1);
         endTableCell();
      }
      //Bug Id 78770, End
      nextTableRow();
        beginTableCell();
          printSubmitLink("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSUBMITLINKCREATE: Create", "performCreate");
        endTableCell();

        beginTableCell();
          printSubmitLink("DOCMAWPORTLETSDOCMAWSEARCHDOCUMENTSUBMITLINKCRTAEDIT: Create and Edit", "performCreateEdit");
        endTableCell();

      // If we get an error, display it in the lower part of the
      // portlet.

      if (ctxValueNotEmpty(error))
      {

         nextTableRow();
           beginTableCell(2);

           String errorText = error;

             appendToHTML("<hr>\n");

             appendToHTML("<div style='background-color:#FF6666'>\n");

             printText(errorText);

             appendToHTML("</div>");

           endTableCell();
      }
      else if (ctxValueNotEmpty(doc_no))
      {

         url = "docmaw/DocIssue.page" +
            "?SEARCH=Y&DOC_CLASS=" + mgr.URLEncode(doc_class)+
            "&DOC_NO="             + mgr.URLEncode(doc_no)+
            "&DOC_SHEET="          + mgr.URLEncode(doc_sheet)+
            "&DOC_REV="            + mgr.URLEncode(doc_rev);

         nextTableRow();
           beginTableCell(2);

             String statusText = mgr.translate("DOCMAWCREATEDOCUMENTCLICKLINKTEXT1: Document &1 created.", doc_no);

             appendToHTML("<hr>\n");

             // The ID is used to get at the DIV and focus it later
             // on.

             appendToHTML("<div  id='_" + getId() + "_crtdoc_status' style='background-color:#FFCC66'>\n");

             printText(statusText + " ");

             if ("CREATEEDIT".equals(command))
                printLink("DOCMAWCREATEDOCUMENTCLICKLINKTEXT2: Click here to check in or to continue working with it.", url);
             else
                printLink("DOCMAWCREATEDOCUMENTCLICKLINKTEXT3: Click here to continue working with it.", url);

             // The code below relies on the fact that the link is the
             // third child of our div. Just in case this by any
             // chance might change due to the framework changing,
             // I've put it inside a try-catch.

             appendDirtyJavaScript("\ntry {\n");
             appendDirtyJavaScript("  document.getElementsByName('_" + getId() + "_crtdoc_status')[0].childNodes[2].focus();\n");
             appendDirtyJavaScript("} catch (e) {\n");
             appendDirtyJavaScript("}\n");

             appendToHTML("</div>");

           endTableCell();

         if ("CREATEEDIT".equals(command))
         {
            doc.addItem("DOC_CLASS", doc_class);
            doc.addItem("DOC_NO",    doc_no);
            doc.addItem("DOC_SHEET", doc_sheet);
            doc.addItem("DOC_REV",   doc_rev);

            action.addItem("DOC_TYPE",           "ORIGINAL");
            action.addItem("FILE_ACTION",        "CREATENEW");
            action.addItem("SAME_ACTION_TO_ALL", "NO");

            transfer_url = DocumentTransferHandler.getDataTransferUrl(mgr, "docmaw/EdmMacro.page", action, documents);

            transfer_to_edm = true;
         }

      }

      endTable();

      // Focus a field?
      if (ctxValueNotEmpty(error) && ctxValueNotEmpty(field_name))
      {
         String foo = "document.form._" + getId() + "_" + field_name + ".focus();\n";
         appendDirtyJavaScript(foo);
      }
      else if (transfer_to_edm)
      {
         // Actual redirect to the check out page (EdmMacro)
         appendDirtyJavaScript("window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(transfer_url));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }

      appendDirtyJavaScript(
                            "function performCreate(obj,id)"+
                            "{\n"+
                            " getPortletField(id,'CMD').value = 'CREATE';\n"+
                            "}\n"+
                            "function performCreateEdit(obj,id)"+
                            "{\n"+
                            " getPortletField(id,'CMD').value = 'CREATEEDIT';\n"+
                            "}\n");

      appendDirtyJavaScript(
                            "function refreshParent()"+
                            "{\n"+
                            "}\n");

      // The following two snippets sets doc class to uppercase when
      // the user leaves the field.
      //Bug Id 78770, Start
      if (display_numbergen) 
      {
         appendDirtyJavaScript("function _" + getId() + "_validate()"+
                            "{\n"+
                            "  var elt = getPortletField('" + getId() + "', 'DOC_CLASS');\n"+
                            "  elt.value = elt.value.toUpperCase();\n"+
			    "  var appRoot= \"http://"+mgr.getApplicationDomain()+mgr.getApplicationPath()+"\";\n"+
			    "  r = __connect(appRoot+\"/docmaw/DocmawCreateDocumentPortletUtil.page?VALIDATE=DOCCLASS&DOC_CLASS=\"+URLClientEncode(elt.value));\n"+
			    "  var num_gen =__getValidateValue(0); \n"+
			    "  var num_gen_trans =__getValidateValue(1); \n"+
			    "  var id1     =__getValidateValue(2); \n"+
			    "  var id2     =__getValidateValue(3); \n"+
			    "  if ((num_gen !=\"\") || (num_gen !=\"undefined\"))\n"+
			    "  {\n"+
			    "     var elt_num = getPortletField('" + getId() + "', 'NUMBER_GENERATOR');\n"+
			    "     elt_num.value = num_gen;\n"+
			    "     var elt_num = getPortletField('" + getId() + "', 'NUMBER_GENERATOR_TRANS');\n"+
			    "     elt_num.value = num_gen_trans;\n"+
			    "     elt_num.readOnly = 1;\n"+
			    "  }\n"+
                            "  if (num_gen ==\"ADVANCED\")\n"+
			    "  {\n"+
                            "     if (id1 !=\"\")\n"+
			    "     {\n"+
			    "        var elt_id1 = getPortletField('" + getId() + "', 'ID1');\n"+
			    "        elt_id1.value = id1;\n"+
			    "        elt_id1.readOnly = 1;\n"+
			    "        document.getElementById('_"+getId() + "_ID1READONLY').style.display ='none';\n"+
			    "     }\n"+
			    "     else\n"+
			    "     {\n"+
			    "        var elt_id1 = getPortletField('" + getId() + "', 'ID1');\n"+
			    "        elt_id1.value = id1;\n"+
			    "        elt_id1.readOnly = 0;\n"+
			    "        document.getElementById('_"+getId() + "_ID1READONLY').style.display ='inline';\n"+
			    "     }\n"+
			    "     var elt_id2     = getPortletField('" + getId() + "', 'ID2');\n"+
			    "     elt_id2.value = id2;\n"+
			    "     elt_id2.readOnly = 0;\n"+
			    "     document.getElementById('_"+getId() + "_ID2READONLY').style.display ='inline';\n"+
			    "     var elt_bk     = getPortletField('" + getId() + "', 'BOOKING_LIST');\n"+
			    "     elt_bk.readOnly = 0;\n"+
			    "     document.getElementById('_"+getId() + "_BKLREADONLY').style.display ='inline';\n"+
			    "     }\n"+
			    "  else\n"+
			    "  {\n"+
			    "     var elt_bk     = getPortletField('" + getId() + "', 'BOOKING_LIST');\n"+
			    "     elt_bk.readOnly = 1;\n"+
			    "     elt_bk.value = \"\";\n"+
			    "     document.getElementById('_"+getId() + "_BKLREADONLY').style.display ='none';\n"+
			    "     var elt_id1     = getPortletField('" + getId() + "', 'ID1');\n"+
			    "     elt_id1.readOnly = 1;\n"+
			    "     elt_id1.value = \"\";\n"+
			    "     document.getElementById('_"+getId() + "_ID1READONLY').style.display ='none';\n"+
			    "     var elt_id2     = getPortletField('" + getId() + "', 'ID2');\n"+
			    "     elt_id2.readOnly = 1;\n"+
			    "     elt_id2.value = \"\";\n"+
			    "     document.getElementById('_"+getId() + "_ID2READONLY').style.display ='none';\n"+
			    "  }\n"+
                            "}\n");
	 appendDirtyJavaScript("function _" + getId() + "_getId2()"+
                            "{\n"+
			       "  var elt = getPortletField('" + getId() + "', 'ID1');\n"+
			       "  var appRoot= \"http://"+mgr.getApplicationDomain()+mgr.getApplicationPath()+"\";\n"+
			       "  r = __connect(appRoot+\"/docmaw/DocmawCreateDocumentPortletUtil.page?VALIDATE=ID1&ID1=\"+URLClientEncode(elt.value));\n"+
                               "  var id2 =__getValidateValue(0); \n"+
			       "  var elt_id2     = getPortletField('" + getId() + "', 'ID2');\n"+
			       "  elt_id2.value = id2;\n"+
                            "}\n");
         appendDirtyJavaScript("getPortletField('" + getId() + "', 'DOC_CLASS').onblur=_" + getId() + "_validate;\n");
	 appendDirtyJavaScript("getPortletField('" + getId() + "', 'ID1').onblur=_" + getId() + "_getId2;\n");
         appendDirtyJavaScript("var elt_num = getPortletField('" + getId() + "', 'NUMBER_GENERATOR_TRANS');\n"+
			    "elt_num.readOnly = 1;\n");
                            
         if ("ADVANCED".equals(number_generator))
         {
            appendDirtyJavaScript("var elt_id1 = getPortletField('" + getId() + "', 'ID1');\n"+
			       "if (elt_id1.value !=\"\")\n"+
			       "{\n"+
                               "   elt_id1.readOnly = 1;\n" +
			       "   document.getElementById('_"+getId() + "_ID1READONLY').style.display ='none';\n"+
			       "}\n");
	
         }
      }
      else
      {
	  appendDirtyJavaScript("function _" + getId() + "_classToUpper()"+
				"{\n"+
				"  var elt = getPortletField('" + getId() + "', 'DOC_CLASS');\n"+
				"  elt.value = elt.value.toUpperCase();\n"+
				"}\n");

	  appendDirtyJavaScript("getPortletField('" + getId() + "', 'DOC_CLASS').onblur=_" + getId() + "_classToUpper");
      }
      //Bug Id 78770, End
   }

   private boolean ctxValueNotEmpty(String var)
   {
      return (!"<EMPTY>".equals(var) && !"".equals(var));
   }

   public static String getDescription()
   {
      return "DOCMAWPORTLETSDOCMAWCREATEDOCUMENTDESC: Create Document";
   }

   public boolean canCustomize()
   {
      return true;
   }

   public void printCustomBody()
   {

      ASPManager           mgr   = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      display_format    = readProfileFlag("DISPLAY_FORMAT", false);
      display_language  = readProfileFlag("DISPLAY_LANGUAGE", false);
      display_numbergen = readProfileFlag("DISPLAY_NUMBERGEN", false);//Bug Id 78770
      printNewLine();
      printText("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCUSTOMHEADER: Enable extra fields:");

      printNewLine();
      printCheckBox("DISPLAY_FORMAT", display_format);
      printText("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCUSTOMFORMAT: Format");
      printNewLine();

      printCheckBox("DISPLAY_LANGUAGE", display_language);
      printText("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCUSTOMLANG: Language Code");
      printNewLine();
      //Bug Id 78770, Start
      printCheckBox("DISPLAY_NUMBERGEN", display_numbergen);
      printText("DOCMAWPORTLETSDOCMAWCREATEDOCUMENTCUSTOMNUMGEN: Number Generator");
      printNewLine();
      //Bug Id 78770, End

   }

   public void submitCustomization()
   {
      display_format   = "TRUE".equals(readValue("DISPLAY_FORMAT"));
      writeProfileFlag("DISPLAY_FORMAT", display_format);

      display_language   = "TRUE".equals(readValue("DISPLAY_LANGUAGE"));
      writeProfileFlag("DISPLAY_LANGUAGE", display_language);
      
      //Bug Id 78770, Start
      display_numbergen   = "TRUE".equals(readValue("DISPLAY_NUMBERGEN"));
      writeProfileFlag("DISPLAY_NUMBERGEN", display_numbergen);
      //Bug Id 78770, End
   }

   protected String getZoomInURL()
   {
      return getASPConfig().getApplicationPath() + "/docmaw/DocTitleOvw.page";
   }

}
