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
 * File        : DocmawNewDocuments.java
 * Description :
 * Notes       :
 * ----------------------------------------------------------------------------
 * Modified    :
 *    000331  RoHi	                 Created.
 *    000622  Sandaruwan Wijenayake  Corrected minimized title to display no fo db rows.
 *
 *			  Chanaka				 Webkit 300A Beta1 change of printTableCell used for 
 *									 other operation than in pure text.
 *    010618 Bakalk   Modified configerations.
 *    010620 Bakalk   Simple adjusment in run();
 *    010627 Bakalk   Changed labels.
 *    030107 Prsalk   Added Doc_Sheet (Sheet Number)
 *    030120 Prsalk   Merged with sp3
 *    030408 Thwilk   Bug Id 36334, All links which associated with ".asp" were changed into ".page" instead.
 *    030902 InoSlk   Added the assignment DEBUG = Util.isDebugEnabled("ifs.portal.DocmawNewDocuments").
 *    2005-11-09 : AMNALK  Fixed Call 128624.
 *    060303 SHTHLK   Bug Id 55934, Modified the preDefine to get the fields from the view instead of getFunctions()
 *    060316 THWILK   Merged Bug 55934.
 *    060726 BAKALK   Bug ID 58216, Fixed Sql Injection.
 *    060912 CHODLK   Bug ID 59663, Modified method printCustomTable().
 *    070405 Chselk   Merged Bug 63562.
 *           070221 THWILK   Bug ID 63562, Modified method run().
 *   070808  JANSLK   Changed all non-translatable constants.
 *    070808 ASSALK   Merged Bug ID 65014, Modified method init() and run().
 *    080417 SHTHLK   Bug Id 70286, Enabled the gifs only if the users have rights for the methods
 *----------------------------------------------------------------------------
 * Select fields to show in the table: 
 */

package ifs.docmaw.portlets;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import ifs.fnd.util.*;

import java.util.*;
import java.io.*;

/**
 */
public class DocmawNewDocuments extends ASPPortletProvider
{
   //==========================================================================
   //  Static constants
   //==========================================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.portal.DocmawNewDocuments");
   private final static String transl_prefix = "DOCMAWNEWDOCUMENTS";
   private final static int size = 15;

   //==========================================================================
   //  instances created on page creation (immutable attributes)
   //==========================================================================
   private ASPContext ctx;
   private ASPBlock   blk;
   private ASPRowSet  rowset;
   private ASPTable   tbl;

   //==========================================================================
   //  Mutable attributes
   //==========================================================================
   private String dt_rel;
   private String noOf;

   //==========================================================================
   //  Transient temporary variables (never cloned)
   //==========================================================================
   private transient int      skip_rows;
   private transient int      db_rows;

   private String  sHideDate;
   private String  sHideFolder;
   private String  sHideTitle;
   private String  sHideReleaseDate;
   private String  sHideCreationDate;
   private String  sHideFileState;
   private String  sHideLastModified;
   private String  sHideDocumentCreator;
   private String  sHideDocumentStatus;
   private boolean bcanViewDocument =false; //Bug Id 70286

   //==========================================================================
   //  Construction
   //==========================================================================

   public DocmawNewDocuments( ASPPortal portal, String clspath )
   {
      super(portal, clspath);
      if (DEBUG) debug(this+": DocmawNewDocuments.<init> :"+portal+","+clspath);
   }


   public ASPPage construct() throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.construct()");
      return super.construct();
   }

   //==========================================================================
   //  Methods for implementation of pool
   //==========================================================================

   protected void doFreeze() throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.doFreeze()");
      super.doFreeze();
   }


   protected void doReset() throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.doReset()");

      super.doReset();
   }


   protected void doActivate() throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.doActivate()");
      super.doActivate();
   }


   public ASPPoolElement clone( Object mgr ) throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.clone("+mgr+")");

      DocmawNewDocuments page = (DocmawNewDocuments)(super.clone(mgr));

      dt_rel      = "";
      noOf        = "";

      page.ctx    = page.getASPContext();
      page.blk    = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.tbl    = page.getASPTable(tbl.getName());

      page.sHideDate = "F";
      page.sHideFolder = "F";
      page.sHideTitle = "F";
      page.sHideReleaseDate = "F";
      page.sHideCreationDate = "F";
      page.sHideFileState = "F";
      page.sHideLastModified = "F";
      page.sHideDocumentCreator = "F";
      page.sHideDocumentStatus = "F";

      return page;
   }

   //==========================================================================
   //  
   //==========================================================================

   protected void preDefine()
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.preDefine()");

      ctx = getASPContext();

      blk = newASPBlock("DOCAPP");

      addField(blk, "DOC_CLASS").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCCLASS: Document Class").setHidden();
      addField(blk, "DOC_NO").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOC_NO: Doc No").setHidden();
      addField(blk, "DOC_SHEET").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOC_SHEET: Doc Sheet").setHidden();
      addField(blk, "DOC_REV").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOC_REV: Doc Rev").setHidden();
      addField(blk, "DT_RELEASED", "Date").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDT_RELEASED: Date");
      addField(blk, "SUBJECT_NAME").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSSUBJECT_NAME: Folder");

      addField(blk, "SUBJECT_REV").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSSUBJECT_REV: Revision").setHidden();

      addField(blk, "DOC_ID").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCID: Doc Key").
      setHyperlink("docmaw/DocIssue.page","DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

      addField(blk, "DOC_TITLE").setLabel("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSTITLE: Title");//bakatoday2

      addField(blk, "FILE_TYPE").
      setHidden();

      addField(blk, "USER_CREATED");   
      
      addField(blk, "DOCUMENT_STATUS");
      
      addField(blk, "FILE_STATE");  

      //bug 58216 starts
      addField(blk, "DT_RELEASED_NUM","Number").setFunction("''").setHidden();
       //bug 58216 end
      
      blk.setView("DOC_PUBLISH_RELEASED");

      tbl = newASPTable( blk );
      tbl.disableQueryRow();
      tbl.disableQuickEdit();
      tbl.disableRowCounter();
      tbl.disableRowSelect();
      tbl.disableRowStatus();
      tbl.unsetSortable();
      tbl.unsetEditable();

      rowset = blk.getASPRowSet();
      getASPManager().newASPCommandBar(blk);

      init();
   }

   protected void init()// throws FndException
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.init()");

      dt_rel    = readProfileValue("DT_REL", "");
      noOf      = readProfileValue("NO_OF", "");
      String cmd = readValue("CMD");

      skip_rows  = Integer.parseInt( ctx.readValue("SKIP_ROWS","0") );
      db_rows    = Integer.parseInt( ctx.readValue("DB_ROWS","0") );

      if ("PREV".equals(cmd) && skip_rows>=size)
      {
         skip_rows -= size;
      }
      else if ("NEXT".equals(cmd) && skip_rows<=db_rows-size)
      {
         skip_rows += size;
      }

      ctx.writeValue("SKIP_ROWS",skip_rows+"");
   }

   protected void run()
   {
      if (DEBUG) debug(this+": DocmawNewDocuments.run()");

      sHideDate    = readProfileValue("HNDDATE", "F");
      sHideFolder  = readProfileValue("HNDFOLD", "F");
      sHideTitle   = readProfileValue("HNDTITL", "F");
      //sHideReleaseDate = readProfileValue("HNDRELEASEDATE", "F" );
      //sHideCreationDate = readProfileValue("HNDCREATIONDATE","F"  );
      sHideFileState = readProfileValue("HNDFILESTATE","F"  );
      //sHideLastModified = readProfileValue("HNDLASTMODIFIED", "F" );
      sHideDocumentCreator = readProfileValue("HNDDOCUMENTCREATOR", "F" );
      sHideDocumentStatus = readProfileValue("HNDDOCUMENTSTATUS","F"  );




      if ("T".equalsIgnoreCase(sHideDate))
         getASPField("DT_RELEASED").setHidden();
      else
         getASPField("DT_RELEASED").unsetHidden();    

      if ("T".equalsIgnoreCase(sHideFolder))
         getASPField("SUBJECT_NAME").setHidden();
      else
         getASPField("SUBJECT_NAME").unsetHidden();   

      if ("T".equalsIgnoreCase(sHideTitle))
         getASPField("DOC_TITLE").setHidden();
      else
         getASPField("DOC_TITLE").unsetHidden();  


      if ("T".equalsIgnoreCase(sHideDocumentCreator))
         getASPField("USER_CREATED").setHidden();
      else
         getASPField("USER_CREATED").unsetHidden();  

      if ("T".equalsIgnoreCase(sHideDocumentStatus))
         getASPField("DOCUMENT_STATUS").setHidden();
      else
         getASPField("DOCUMENT_STATUS").unsetHidden(); 

      if ("T".equalsIgnoreCase(sHideFileState))
         getASPField("FILE_STATE").setHidden();
      else
         getASPField("FILE_STATE").unsetHidden();          


      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery             qry   = trans.addQuery(blk);

      qry.setOrderByClause("DT_RELEASED DESC");
      qry.setBufferSize(size);
      qry.skipRows(skip_rows);
      qry.includeMeta("ALL");

      int noOfDays,inTo=0;

      if ("Days".equals(dt_rel))
         noOfDays=1;
      else if ("Weeks".equals(dt_rel))
         noOfDays=7;
      else if ("Months".equals(dt_rel))
         noOfDays=30;
      else if ("Years".equals(dt_rel))
         noOfDays=365;
      else
         noOfDays=-1;  

      try
      {
         inTo=  Integer.parseInt(noOf,10); 
      }
      catch (NumberFormatException e)
      {
      }


      if (noOfDays>0)
      {
          //bug 58216 starts
         qry.addWhereCondition(" (sysdate - DT_RELEASED) <= ?");
         qry.addParameter(this,"DT_RELEASED_NUM",""+noOfDays*inTo); //Bug ID 63562
         //bug 58216 end
      }





      /*if ("Last4Weeks".equals(dt_rel) )
      {
          qry.addWhereCondition(" (sysdate - DT_RELEASED) <= 28");
      }
      else if ("Last2Weeks".equals(dt_rel) )
      {
          qry.addWhereCondition(" (sysdate - DT_RELEASED) <= 14");
      }
      else if ("LastWeek".equals(dt_rel) )
      {
          qry.addWhereCondition(" (sysdate - DT_RELEASED) <= 7");
      }*/

      submit(trans);
      db_rows = blk.getASPRowSet().countDbRows();
      getASPContext().writeValue("DB_ROWS", db_rows+"" );
      trans.clear();
   }


   //==========================================================================
   //  
   //==========================================================================

   public String getTitle( int mode )
   {
      ASPManager mgr = getASPManager();
      if (DEBUG) debug(this+": DocmawNewDocuments.getTitle("+mode+")");

      if (mode==ASPPortal.MINIMIZED)
         return translate(getDescription() + mgr.translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSMINTXT:  - You have &1 new documents."), Integer.toString(db_rows));
      else
         return translate(getDescription());
   }

   public void printContents() throws FndException
   {
      if (DEBUG)
      {
         debug(this+": DocmawNewDocuments.getContents()");
      }

      // hidden field for next and previous links
      printHiddenField("CMD","");

      //printTable(tbl);
      printCustomTable();

      if (size < db_rows)
      {
         printNewLine();

         if (skip_rows>0)
            printSubmitLink("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCMAWDEMOCUSTPRV: Previous","prevCust");
         else
            printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCMAWDEMOCUSTPRV: Previous");

         printSpaces(5);
         String rows = translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDEMOCUSTROWS: Rows &1 to &2 of &3",              //bakatoday
                                 (skip_rows+1)+"",
                                 (skip_rows+size<db_rows?skip_rows+size:db_rows)+"",
                                 db_rows+"");
         printText( rows );
         printSpaces(5);

         if (skip_rows<db_rows-size)
            printSubmitLink("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSNEXT: Next" , "nextCust");
         else
            printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSNEXT: Next");

         printNewLine();
         printNewLine();

         appendDirtyJavaScript(
                              "function prevCust(obj,id)"+
                              "{"+
                              "   getPortletField(id,'CMD').value = 'PREV';"+
                              "}\n"+
                              "function nextCust(obj,id)"+
                              "{"+
                              "   getPortletField(id,'CMD').value = 'NEXT';"+
                              "}\n");

      }
      else
         printNewLine();

      appendDirtyJavaScript(
                           "function "+addProviderPrefix()+"openInNewWindow( file_path )\n"+
                           "{ \n"+
                           "   window.open(file_path,'anotherWindow','status,resizable,scrollbars,width=500,height=500,left=100,top=100');\n"+
                           "} \n");
   }

   public void printCustomBody()  throws FndException 
   {
      ASPManager           mgr   = getASPManager();

      ASPBuffer  buf = mgr.newASPTransactionBuffer();
      ASPBuffer  b   = mgr.newASPTransactionBuffer();

      b = buf.addBuffer("Data");
      b.setValue("Label", "Days");   
      b.setValue("Value", translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDAYS: Days"));  

      b = buf.addBuffer("Data");
      b.setValue("Label", "Weeks");   
      b.setValue("Value", translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSWEEKS: Weeks"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Months");   
      b.setValue("Value", translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSMONTHS: Months"));   

      b = buf.addBuffer("Data");
      b.setValue("Label", "Years");   
      b.setValue("Value", translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSYEARS: Years"));   

      printNewLine();
      printSpaces(5);
      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSSELDOCSREL: Select documents released: ");
      printNewLine();
      printNewLine();
      printSpaces(5);
      printSelectBox("DT_REL", buf, dt_rel);
      printSpaces(5);
      //appendToHTML("<input type=\"text\" name=\"nos\" size=\"8\" onChange=\"giveMessage();\">");
      printField("NO_OF",noOf,5);
      printNewLine();
      printNewLine();

      // Customize table fileds.
      appendToHTML("<table border=0 cellpadding=0 cellspacing=0>");
      appendToHTML("<tbody><tr><td style='FONT: 8pt Verdana'><br>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSSELFIELDS: Select fields to show in the table:");
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'><br>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFDATE: Date Released");
      appendToHTML("</td><td style='FONT: 8pt Verdana'><br>");
      printCheckBox("CHECK_DATE","T".equalsIgnoreCase(sHideDate)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFFOLD: Folder");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_FOLDER","T".equalsIgnoreCase(sHideFolder)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFTITLE: Title");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_TITLE","T".equalsIgnoreCase(sHideTitle)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCUMENTSTATUS: Document Status");
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DOCUMENTSTATUS","T".equalsIgnoreCase(sHideDocumentStatus)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCUMENTCREATOR: Document Creator");// // //
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_DOCUMENTCREATOR","T".equalsIgnoreCase(sHideDocumentCreator)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");

      /*printText("docmawnewdocumentslastmodified: Last Modified");// // //
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_LASTMODIFIED","T".equalsIgnoreCase(sHideLastModified)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");*/

      printText("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFFILESTATE: File State");// // //
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_FILESTATE","T".equalsIgnoreCase(sHideFileState)?false:true);
      appendToHTML("</td></tr></tbody></table>");

      /* printText("docmawnewdocumentscreationdate: Creation Date");// // //
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_CREATIONDATE","T".equalsIgnoreCase(sHideCreationDate)?false:true);
      appendToHTML("</td></tr><tr><td style='FONT: 8pt Verdana'>");*/

      /* printText("docmawnewdocumentsreleasedate: Release Date");// // //
      appendToHTML("</td><td style='FONT: 8pt Verdana'>");
      printCheckBox("CHECK_RELEASEDATE","T".equalsIgnoreCase(sHideReleaseDate)?false:true);
      appendToHTML("</td></tr></tbody></table>");*/





   }

   public void submitCustomization()
   {
      if (DEBUG)
      {
         debug(this+": DocmawNewDocuments.submitCustomization()");
      }

      dt_rel = readValue("DT_REL");// removed default value Bakalk
      noOf   =readValue("NO_OF",noOf);
      sHideDate    = "TRUE".equalsIgnoreCase(readValue("CHECK_DATE"))?"F":"T";
      sHideFolder  = "TRUE".equalsIgnoreCase(readValue("CHECK_FOLDER"))?"F":"T";
      sHideTitle   = "TRUE".equalsIgnoreCase(readValue("CHECK_TITLE"))?"F":"T";

      //sHideReleaseDate   = "TRUE".equalsIgnoreCase(readValue("CHECK_RELEASEDATE"))?"F":"T";
      //sHideCreationDate   = "TRUE".equalsIgnoreCase(readValue("CHECK_CREATIONDATE"))?"F":"T";
      sHideFileState   = "TRUE".equalsIgnoreCase(readValue("CHECK_FILESTATE"))?"F":"T";
      //sHideLastModified   = "TRUE".equalsIgnoreCase(readValue("CHECK_LASTMODIFIED"))?"F":"T";
      sHideDocumentCreator   = "TRUE".equalsIgnoreCase(readValue("CHECK_DOCUMENTCREATOR"))?"F":"T";
      sHideDocumentStatus   = "TRUE".equalsIgnoreCase(readValue("CHECK_DOCUMENTSTATUS"))?"F":"T";

      //           
      writeProfileValue("DT_REL", dt_rel );
      writeProfileValue("NO_OF", noOf );
      writeProfileValue("HNDDATE", sHideDate );
      writeProfileValue("HNDFOLD", sHideFolder );
      writeProfileValue("HNDTITL", sHideTitle );

      writeProfileValue("HNDRELEASEDATE", sHideReleaseDate );
      writeProfileValue("HNDCREATIONDATE", sHideCreationDate );
      writeProfileValue("HNDFILESTATE", sHideFileState );
      writeProfileValue("HNDLASTMODIFIED", sHideLastModified );
      writeProfileValue("HNDDOCUMENTCREATOR", sHideDocumentCreator );
      writeProfileValue("HNDDOCUMENTSTATUS", sHideDocumentStatus );
   }

   public static String getDescription()
   {
      return "DOCMAWPORTLETSDOCMAWNEWDOCUMENTSNEWDOC: New Documents";
   }

   public boolean canCustomize()
   {
      return true;
   }

   public void printCustomTable() throws FndException 
   {
      ASPManager mgr = getASPManager();
      bcanViewDocument =canViewDocument(); //Bug Id 70286

      beginTable();
      beginTableTitleRow();
      if (!getASPField("DT_RELEASED").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDATERELEASED: Date Released"));
      if (!getASPField("SUBJECT_NAME").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFOLD: Folder"));
      printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSDOCKEY: Document"));
      if (!getASPField("DOC_TITLE").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSTITLE: Title"));

      if (!getASPField("USER_CREATED").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSCREATOR: Creator"));
      if (!getASPField("DOCUMENT_STATUS").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSSTATUS: Status"));
      if (!getASPField("FILE_STATE").isHidden())
         printTableCell(translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSFILESTATE: File State"));
      printTableCell("");
      endTableTitleRow();
      beginTableBody();

      rowset.first();
      for (int i=0; i<rowset.countRows(); i++)
      {
         if (!getASPField("DT_RELEASED").isHidden())
         {
            /* Modified by Chanaka 
               Webkit 300A Beta1 change of printTableCell */

            /* printTableCell(rowset.getRow().getFieldValue(this, "DT_RELEASED")); */

            beginTableCell();
            printText(rowset.getRow().getFieldValue(this, "DT_RELEASED"));
            endTableCell();

         }
         if (!getASPField("SUBJECT_NAME").isHidden())
         {
            /* Modified by Chanaka 
               Webkit 300A Beta1 change of printTableCell */

            beginTableCell();
            printText(rowset.getValue("SUBJECT_NAME"));
            endTableCell();

            /* printTableCell(rowset.getValue("SUBJECT_NAME")); */

         }

         /* Modified by Chanaka 
              Webkit 300A Beta1 change of printTableCell */

         /*printTableCell("<a href=\"docmaw/DocIssue.asp"+
                    "?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"))+
                    "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"))+
                    "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"))+
                    "\">"+rowset.getValue("DOC_ID")+"</a>");
          */

         String url =   "docmaw/DocIssue.page"+
                        "?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"))+
                        "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"))+
                        "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"))+
                        "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));

         beginTableCell();
         printLink(rowset.getValue("DOC_ID"),url);
         endTableCell();

         if (!getASPField("DOC_TITLE").isHidden())
         {
            /* Modified by Chanaka 
              Webkit 300A Beta1 change of printTableCell */

            beginTableCell();
            printText(rowset.getValue("DOC_TITLE"));
            endTableCell();

            /* printTableCell(rowset.getValue("DOC_TITLE")); */
         }


         if (!getASPField("USER_CREATED").isHidden())
         {


            beginTableCell();
            printText(rowset.getValue("USER_CREATED"));
            endTableCell();


         }

         if (!getASPField("DOCUMENT_STATUS").isHidden())
         {

            beginTableCell();
            printText(rowset.getValue("DOCUMENT_STATUS"));
            endTableCell();


         }

         if (!getASPField("FILE_STATE").isHidden())
         {


            beginTableCell();
            printText(rowset.getValue("FILE_STATE"));
            endTableCell();


         }


         if (bcanViewDocument) //Bug Id 70286
	 {
	     String sFilePath  = "docmaw/EdmMacro.page?DOC_CLASS="+mgr.URLEncode(rowset.getValue("DOC_CLASS"));
	     sFilePath += "&DOC_NO="+mgr.URLEncode(rowset.getValue("DOC_NO"));
	     sFilePath += "&DOC_SHEET="+mgr.URLEncode(rowset.getValue("DOC_SHEET"));
	     sFilePath += "&DOC_REV="+mgr.URLEncode(rowset.getValue("DOC_REV"));
	     sFilePath += "&DOC_TYPE=ORIGINAL";
	     sFilePath += "&FILE_TYPE="+mgr.URLEncode(rowset.getValue("FILE_TYPE"));
	     sFilePath += "&PROCESS_DB=VIEW";
    
	     /* Modified by Chanaka 
		  Webkit 300A Beta1 change of printTableCell */
	     // Bug 59663, Start, used ASPPortletProvoder method to print the image link
	     String imghref = "javascript:showNewBrowser('"+sFilePath+"');";
	     String imgpath = "docmaw/images/open.gif";
	     
	     beginTableCell();
			    printImageLink(imgpath, imghref, "", translate("DOCMAWPORTLETSDOCMAWNEWDOCUMENTSALTVIEW: View"));
	     endTableCell();
			    //Bug 59663, End
	 }

         /* printTableCell("<a href=\"javascript:"+addProviderPrefix()+"openInNewWindow('"+sFilePath+"');\"><img border=0 src='docmaw/images/open.gif' alt='"+translate("docmawnewdocumentsaltview: View")+"'></a>"); */

         nextTableRow();
         rowset.next();
      }
      rowset.first();

      endTableBody();
      endTable();
   }
   //Bug Id 70286, Start
   public boolean  canViewDocument()  
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf;
      boolean bcanView =false;
	    
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
	    
      trans.clear();
      trans.addSecurityQuery("DOCUMENT_ISSUE_ACCESS_API","Get_Document_Access");
      trans = mgr.perform(trans);
      buf = trans.getSecurityInfo();    
      
      if( buf.itemExists("DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"))
	bcanView = true;
      
      return bcanView;
   }
   //Bug Id 70286, End
}