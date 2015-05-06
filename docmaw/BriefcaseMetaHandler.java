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
 *  File        : BriefcaseMetaHandler.java
 *  Description :
 *  Modified    :
 *  2003-05-06  DIKALK  Added functionality for creating an XML document containing
 *                      all necessary meta data for a briefcase
 *  2003-06-20  INOSLK  Added Object Connection details to unpackMetaData, createMetaFile and preDefine.
 *  2003-06-27  Bakalk  Modified unpackMetaData. used new rev instead doc_rev when changed status is new rev.
 *  2003-06-30  NISILK  MOdified methods unpackMetaData, createMetaFile, preDefine after adding the new column lu_name in excel sheet.
 *  2003-07-23  NISILK  MOdified createMetaFile() method: Extended types are not checked when file name is empty.
 *  2003-08-28  Bakalk  Fixed 101755: Export date is not taken from the rowset but from the system.
 *  2003-09-15  Dikalk  Call 101593. Modified method createMetaFile()
 *  2003-10-20  Dikalk  Call 101593. Modified method formatErrorMessage()
 *  2003-10-27  Bakalk  Call 109239. Modified method run(), deleted all records against current bc
 *                      if we get any exception while saving   records in unpackMetaData().
 *  2004-02-11  InoSlk  Bug ID 42606 - Modified method responseWriteXML() to write debug info only if DEBUG is enabled
 *  2004-02-11          and to write the file to docmaw temporary path. Added method getDocmawTempPath(). Imported java.io.
 *  2004-03-30  Shthlk  Bug Id 43593, Added new method getKeyValues() and modified createMetaFile().
 *  2004-04-01  InoSlk  Bug ID 43593, Modified preDefine() and getKeyValues() so that Client_SYS.Get_Key_Reference is used
 *  2004-04-01          to build the document key in key reference format.
 *  2004-04-06  InoSlk  Bug ID 43625, Added support for DOC_TITLE.
 *  2004-04-09  InoSlk  Bug ID 45219, Modified unpackMetaData().
 *  2004-09-08  InoSlk  Bug ID 46717, Modified createMetaFile().
 *  2004-09-15  InoSLk  Bug ID 46925, Modified unpackMetaData() to connect up new sheets to the original documents.
 *  2004-10-11  InoSlk  Bug ID 47320, Modified unpackMetaData() and formatErrorMessage().
 *  2004-10-22  InoSlk  Bug ID 47367, Added okFindBcUnpackedIssues(). Modified preDefine(),doReset(),clone(),run(),createMetaFile(),populateMetaData().
 *  2004-11-02  InoSlk  Bug ID 47637, Added columns FORMAT_SIZE,SCALE,REASON_FOR_ISSUE in createMetaFile(),unpackMetaData(),preDefine().
 *  2004-11-10  InoSlk  Bug ID 47171, Added DocmawConstants variables for bc_change_status. Modified createMetaFile(),unpackMetaData(),preDefine().
 *  2004-12-15  Shthlk  Bug ID 48421, Modified getExtendedFileNames to stop exporting the view file if view file is set as the ORIGINAL type.
 *  2004-12-22  Shthlk  Bug ID 48298, Modified unpackMetaData to set the briefcase imported path.
 *  2005-02-18  CHRALK  Bug Id 49558, Changed method getDocmawTempPath() to get absolute path.
 *  2005-06-23  KARALK  BUG ID 50158,  merged.
 *  2006-03-09  BAKALK  Call Id 134882, set a date format for "DT_DOC_REV".
 *  2006-07-17  BAKALK  Bug ID 58216, Fixed Sql Injection.
 *  2007-03-28  JANSLK  Merged Bug ID 62341, Modified createMetaFile() for the "OLD_REVISION_DATE","OLD_REVISION_TEXT","OLD_REVISION_SIGN" to get the values.
 *  2007-08-09  BAKALK  Call Id 145226, set date format for REVISION_DATE,DATE1,DATE2,DATE3 AND DATE4.
 *  2007-11-29  SHTHLK  Bug Id 67966, Modified getExtendedFileNames() to get the files from Document Class Process Action
 *  2008-05-28  VIRALK  Bug Id 71463, Modified getExtendedFileNames() to get the extended files directly from the server method
 
 * ----------------------------------------------------------------------------
 */

package ifs.docmaw;


import ifs.fnd.service.*;
import ifs.fnd.asp.*;
import ifs.fnd.xml.XMLUtil;
import ifs.fnd.util.Str;
import java.io.IOException;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xerces.dom.*;
import java.text.*;
import java.io.*;


/**
 *
 *  Creating new meta files
 *  ========================
 *
 *  Request from client
 *  -------------------
 *
 *  <BC_META_DATA>
 *     <ACTION NAME="CREATE_META_FILE" BRIEFCASE_NO="XXXXXXX"></ACTION>
 *  </BC_META_DATA>
 *
 *
 *  Response
 *  --------
 *
 *  <BC_META_DATA>
 *    <BRIEFCASE BRIEFCASE_NO=XXXXX
 *               RECEIVING_COMPANY=XXXXX
 *               RECEIVING_PERSON=XXXXX
 *               EXPORT_DATE=XXXXX
 *               LATEST_RETURN_DATE=XXXXX
 *    </BRIEFCASE>
 *    <DOCUMENTS>
 *       <DOCUMENT0 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT0>
 *       <DOCUMENT1 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT1>
 *       <DOCUMENT2 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT2>
 *       ...
 *       ...
 *    </DOCUMENTS>
 *  </BC_META_DATA>
 *
 *
 *  Unpacking meta files
 *  ====================
 *
 *  Request from client
 *  -------------------
 *
 *  <BC_META_DATA>
 *     <ACTION NAME="UNPACK_META_FILE" BRIEFCASE_NO="XXXXX"></ACTION>
 *     <DOCUMENTS>
 *       <DOCUMENT0 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT0>
 *       <DOCUMENT1 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT1>
 *       <DOCUMENT2 DOC_CLASS="XXXXX" DOC_NO="XXXXXX" ... ><DOCUMENT2>
 *       ...
 *       ...
 *     </DOCUMENTS>
 *  </BC_META_DATA>
 *
 *
 *  Response (only if an error occured)
 *  -----------------------------------
 *
 *  <BC_META_DATA>
 *    <ERROR DESC="XXXXX"></ERROR>
 *  </BC_META_DATA>
 *
 *
 */


public class BriefcaseMetaHandler extends ASPPageProvider
{

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.BriefcaseMetaHandler");


   // Instances created on page creation (immutable attributes)
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPBlock dummyblk;
   // Bug ID 47367, inoslk, start
   private ASPBlock itemblk3;
   private ASPRowSet itemset3;
   // Bug ID 47367, inoslk, end

   // Transient temporary variables (never cloned)
   private Document xml_req;
   private Document xml_res;
   private String brief_no;
   private String action;
   private String old_brief_no; // Bug ID 47367, inoslk
   private String imported_path; //Bug Id 48298
   private ASPTransactionBuffer trans;

   // Bug ID 47171, inoslk, start
   private String db_change_state_changed;
   private String db_change_state_unchanged;
   private String db_change_state_newsheet;
   private String db_change_state_new;
   private String db_change_state_newrev;
   // Bug ID 47171, inoslk, end

   // Bug ID 50158, Start
   private int brief_issue_count;
   // Bug ID 50158, End

   public BriefcaseMetaHandler(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      xml_req  = null;
      xml_res  = null;
      brief_no = null;
      action   = null;
      trans    = null;
      // Bug ID 47367, inoslk, start
      old_brief_no = null;
      // Bug ID 47367, inoslk, end
      imported_path = null; //Bug Id 48298

      // Bug ID 47171, inoslk, start
      db_change_state_changed = null;
      db_change_state_new = null;
      db_change_state_newrev = null;
      db_change_state_newsheet = null;
      db_change_state_unchanged = null;
      // Bug ID 47171, inoslk, end
      // Bug ID 50158, Start
      brief_issue_count = 0;
      // Bug ID 50158, End
      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      BriefcaseMetaHandler page = (BriefcaseMetaHandler)(super.clone(obj));

      // Resetting immutable attributes
      page.xml_req  = null;
      page.xml_res  = null;
      page.brief_no = null;
      page.action   = null;
      page.trans    = null;
      // Bug ID 47367, inoslk, start
      old_brief_no = null;
      // Bug ID 47367, inoslk, end
      page.imported_path = null; //Bug Id 48298

      // Bug ID 47171, inoslk, start
      db_change_state_changed = null;
      db_change_state_new = null;
      db_change_state_newrev = null;
      db_change_state_newsheet = null;
      db_change_state_unchanged = null;
      // Bug ID 47171, inoslk, end

      // Cloning immutable attributes
      page.ctx      = page.getASPContext();
      page.headblk  = page.getASPBlock(headblk.getName());
      page.headset  = page.headblk.getASPRowSet();
      page.itemblk1 = page.getASPBlock(itemblk1.getName());
      page.itemset1 = page.itemblk1.getASPRowSet();
      page.itemblk2 = page.getASPBlock(itemblk2.getName());
      page.itemset2 = page.itemblk2.getASPRowSet();
      page.dummyblk = page.getASPBlock(dummyblk.getName());
      // Bug ID 47367, inoslk, start
      page.itemblk3 = page.getASPBlock(itemblk3.getName());
      page.itemset3 = page.itemblk3.getASPRowSet();
      // Bug ID 47367, inoslk, end

      // Bug ID 50158, Start
      page.brief_issue_count = 0;
      // Bug ID 50158, End
      return page;
   }


   public void run() throws FndException
   {
      debug("run() {");

      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();

      // Bug ID 47171, inoslk, start
      DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);
      db_change_state_changed = dm_const.doc_bc_change_changed;
      db_change_state_new = dm_const.doc_bc_change_new;
      db_change_state_newrev = dm_const.doc_bc_change_newrev;
      db_change_state_newsheet = dm_const.doc_bc_change_newsheet;
      db_change_state_unchanged = dm_const.doc_bc_change_unchanged;
      // Bug ID 47171, inoslk, end

      try
      {
         if (!mgr.isEmpty(mgr.getQueryStringValue("ACTION")) && !mgr.isEmpty(mgr.getQueryStringValue("BRIEFCASE_NO")))
         {
            action = mgr.getQueryStringValue("ACTION");
            brief_no = mgr.getQueryStringValue("BRIEFCASE_NO");
         }
         else
         {
            xml_req = loadXMLFromRequest();
            action = getActionProperty(xml_req, "NAME");
            brief_no = getActionProperty(xml_req, "BRIEFCASE_NO");
            // Bug ID 47367, inoslk, start
            old_brief_no = getActionProperty(xml_req,"OLD_BRIEFCASE_NO");
            // Bug ID 47367, inoslk, end
            imported_path = getActionProperty(xml_req,"IMPORTED_PATH"); //Bug Id 48298
         }

         // Dispatch the request to an appropriate
         // action handler, depending on the action
         // name

         // Bug ID 47367, inoslk, Modified the IF condition
         if ("CREATE_META_FILE".equals(action) || ("CREATE_REJECTED_META_FILE".equals(action)))
         {
            xml_res = createMetaFile();
         }
         else if ("UNPACK_META_FILE".equals(action))
         {
            unpackMetaData(xml_req);
            xml_res = ackUnpackMetaData();
         }
      }
      catch(Exception any)
      {
         any.printStackTrace();
         xml_res = formatErrorMessage(any);
         if ("UNPACK_META_FILE".equals(action))
         {
            deleteCurrentBc(brief_no);
         }
      }
      finally
      {
         responseWriteXML(xml_res);
      }

      debug("} run()");
   }


   public void debug(String msg)
   {
      if (DEBUG)
         super.debug(this + ":" + msg);
   }


   /*
    we happen to do this, since Oracle exeption can be thrown after some new records saved.
    so we have to remove them.
   */
   public void deleteCurrentBc(String bc_no)
   {
      ASPManager mgr =  getASPManager();
      ASPCommand cmd;
      debug(" %%%%%%%%%%%%%%%%% in delete Current bc");
      trans.clear();
      cmd = trans.addCustomCommand("DELETEBC","DOC_BRIEFCASE_UNPACKED_API.Remove_Unpacked_Bc_Data");
      cmd.addParameter("DUMMY1",bc_no);
      mgr.perform(trans);

   }


   private Document loadXMLFromRequest() throws Exception
   {
      ASPManager mgr = getASPManager();
      return XMLUtil.loadFromInputStream(mgr.getRequestBodyAsInputStream());
   }


   private void responseWriteXML(Document xml_doc)
   {
      debug("Executing responseWriteXML()...");

      ASPManager mgr = getASPManager();
      // Bug ID 42606, inoslk, Added variable file_name
      String str_xml_doc = null, file_name = null;

      try{
         // Bug ID 42606, inoslk, we write the file to disk only if DEBUG is enabled.
         if (DEBUG)
         {
            file_name = getDocmawTempPath();

            // Bug ID 42606, inoslk, if docmaw temp path exists, carry on
            if (!mgr.isEmpty(file_name))
               XMLUtil.saveToFile(xml_doc, file_name + "temp2.xml");
         }
         str_xml_doc = XMLUtil.saveToString(xml_doc);
         str_xml_doc = Str.replace(str_xml_doc, "\r", "");
         str_xml_doc = Str.replace(str_xml_doc, "\n", "");
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }

      mgr.responseClear();
      mgr.setAspResponsContentType("text/xml");
      mgr.responseWrite(str_xml_doc);
      mgr.endResponse();
   }


   private String base64Decode(String str)
   {
      String decoded_str = null;

      try
      {
         if (!Str.isEmpty(str))
            decoded_str = new String(Util.fromBase64Text(str));
         else
            decoded_str = "";
      }
      catch(IOException ioe)
      {
         sendErrorToClient("Error decoding base64 string", ioe);
      }

      return decoded_str;
   }


   private String base64Encode(String str)
   {
      String encoded_str = null;

      try
      {
         if (!Str.isEmpty(str))
            encoded_str = new String(Util.toBase64Text(str.getBytes()));
         else
            encoded_str = "";

         if (encoded_str.indexOf("\r\n") > 0)
         {
            encoded_str = Str.replace(encoded_str, "\r\n", "");
         }
      }
      catch(IOException ioe)
      {
         sendErrorToClient("Error encoding base64 string", ioe);
      }

      return encoded_str;
   }


   private String getActionProperty(Document xml_doc, String property)
   {
      Element root_node = xml_doc.getDocumentElement();
      Node action_node = root_node.getFirstChild();
      String property_value = action_node.getAttributes().getNamedItem(property).getNodeValue();
      return base64Decode(property_value);
   }


   private Document createMetaFile() throws Exception
   {
      debug("createMetaFile() {");

      ASPManager mgr = getASPManager();
      //Bug 50158, Start
      ASPQuery query;
      //Bug 50158, End
      String doc_class;
      String doc_no;
      String doc_rev;
      String doc_sheet;

      populateMetaData();

      // Create new XML Document
      Document xml_doc = new DocumentImpl();

      // Root Element
      Element root_node = xml_doc.createElement("BC_META_DATA");
      xml_doc.appendChild(root_node);

      String time_now;
      GregorianCalendar gc = new GregorianCalendar();
      int date  = gc.get(gc.DAY_OF_MONTH);
      int month = gc.get(gc.MONTH);
      int year  = gc.get(gc.YEAR);
      month++;
      String prefix = (month <= 9)?"0":"";
      //Bug ID 46717, inoslk, start
      String date_prefix = (date <= 9)?"0":"";
      time_now  = year + "-" +prefix+month+ "-" + date_prefix+date;
      //Bug ID 46717, inoslk, end

      // Tag briefcase details..
      Element bc_header_node = xml_doc.createElement("BRIEFCASE");
      bc_header_node.setAttribute("BRIEFCASE_NO",       base64Encode(headset.getRow().getValue("BRIEFCASE_NO")));
      bc_header_node.setAttribute("RECEIVING_COMPANY",  base64Encode(headset.getRow().getValue("RECEIVING_COMPANY")));
      bc_header_node.setAttribute("RECEIVING_PERSON",   base64Encode(headset.getRow().getValue("RECEIVING_PERSON")));
      bc_header_node.setAttribute("EXPORT_DATE",        base64Encode(time_now));
      bc_header_node.setAttribute("LATEST_RETURN_DATE", base64Encode(headset.getRow().getValue("LATEST_RETURN_DATE")));
      root_node.appendChild(bc_header_node);

      // Tag document details..
      Element bc_detail_node = xml_doc.createElement("DOCUMENTS");
      Element bc_row;

      // Bug ID 47367, inoslk, start
      if ("CREATE_REJECTED_META_FILE".equals(action))
      {
         itemset3.first();
         for(int x = 0; x < itemset3.countRows(); x++)
         {
            bc_row = xml_doc.createElement("DOCUMENT" + x);

            doc_class = itemset3.getRow().getFieldValue("UNPACKED_DOC_CLASS");
            doc_no    = itemset3.getRow().getFieldValue("UNPACKED_DOC_NO");
            doc_sheet = itemset3.getRow().getFieldValue("UNPACKED_DOC_SHEET");
            doc_rev   = itemset3.getRow().getFieldValue("DOC_REVISION");
            String file_names = itemset3.getRow().getFieldValue("UNPACKED_FILE_NAME");
            StringTokenizer st = null;

            // Bug ID 47171, inoslk, start
            if (itemset3.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(db_change_state_changed))
               bc_row.setAttribute("DOC_KEY",          base64Encode(getKeyValues(doc_class,doc_no,doc_sheet,doc_rev)));
            else if (itemset3.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(db_change_state_newrev))
               bc_row.setAttribute("DOC_KEY",          base64Encode(itemset3.getRow().getValue("DOCUMENT_KEY")));
            else
               bc_row.setAttribute("DOC_KEY",          "");
            // Bug ID 47171, inoslk, end
            bc_row.setAttribute("DOC_CLASS",           base64Encode(doc_class));
            bc_row.setAttribute("DOC_NO",              base64Encode(doc_no));
            bc_row.setAttribute("DOC_SHEET",           base64Encode(doc_sheet));
            //Bug ID 47171, inoslk, start
            if (itemset3.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(db_change_state_newrev))
               bc_row.setAttribute("DOC_REV",          base64Encode(getStringAttribute(itemset3.getRow().getFieldValue("DOCUMENT_KEY"),"DOC_REV")));
            else
               bc_row.setAttribute("DOC_REV",          base64Encode(doc_rev));
            //Bug ID 47171, inoslk, end
            bc_row.setAttribute("DOC_TITLE",           base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DOC_TITLE")));
            // Bug ID 47171, inoslk, start
            if (itemset3.getRow().getValue("DOC_BC_CHANGE_STATUS").equals(db_change_state_newrev))
               bc_row.setAttribute("NEW_REVISION",     base64Encode(doc_rev));
            else
               bc_row.setAttribute("NEW_REVISION",     "");
            // Bug ID 47171, inoslk, end
            bc_row.setAttribute("NEXT_DOC_SHEET",      base64Encode(itemset3.getRow().getValue("NEXT_SHEET_NUMBER")));
            if (!mgr.isEmpty(file_names))
            {
               st = new StringTokenizer(file_names,"|");
               bc_row.setAttribute("FILE_NAME",base64Encode(st.nextToken()));
            }
            else
               bc_row.setAttribute("FILE_NAME",                base64Encode(itemset3.getRow().getValue("UNPACKED_FILE_NAME")));
            bc_row.setAttribute("CHANGE_STATUS",       base64Encode(itemset3.getRow().getValue("DOC_BC_CHANGE_STATUS")));
            bc_row.setAttribute("DOC_STATE",           base64Encode(itemset3.getRow().getValue("DOCUMENT_STATE")));
            
            bc_row.setAttribute("DT_DOC_REV",          base64Encode(itemset3.getRow().getFieldValue("REVISION_DATE")));

            bc_row.setAttribute("DOC_REV_TEXT",        base64Encode(itemset3.getRow().getValue("REVISION_TEXT")));
            bc_row.setAttribute("USER_CREATED",        base64Encode(itemset3.getRow().getValue("REVISION_SIGN")));
            bc_row.setAttribute("LU_NAME",             base64Encode(itemset3.getRow().getFieldValue("UNPACKED_LU_NAME")));
            bc_row.setAttribute("OBJECT_ID",           base64Encode(itemset3.getRow().getFieldValue("UNPACKED_OBJECT_ID")));
            bc_row.setAttribute("UPDATE_REVISION",     base64Encode(itemset3.getRow().getFieldValue("UNPACKED_UPDATE_REVISION")));
            bc_row.setAttribute("ASSOCIATED_CATEGORY", base64Encode(itemset3.getRow().getFieldValue("UNPACKED_ASSOCIATED_CATEGORY")));
            bc_row.setAttribute("DATE1",               base64Encode(itemset3.getRow().getFieldValue("DATE1")));
            bc_row.setAttribute("SIGN1",               base64Encode(itemset3.getRow().getValue("SIGN1")));
            bc_row.setAttribute("DATE2",               base64Encode(itemset3.getRow().getFieldValue("DATE2")));
            bc_row.setAttribute("SIGN2",               base64Encode(itemset3.getRow().getValue("SIGN2")));
            bc_row.setAttribute("DATE3",               base64Encode(itemset3.getRow().getFieldValue("DATE3")));
            bc_row.setAttribute("SIGN3",               base64Encode(itemset3.getRow().getValue("SIGN3")));
            bc_row.setAttribute("DATE4",               base64Encode(itemset3.getRow().getFieldValue("DATE4")));
            bc_row.setAttribute("SIGN4",               base64Encode(itemset3.getRow().getValue("SIGN4")));
            bc_row.setAttribute("DESC1",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION1")));
            bc_row.setAttribute("DESC2",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION2")));
            bc_row.setAttribute("DESC3",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION3")));
            bc_row.setAttribute("DESC4",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION4")));
            bc_row.setAttribute("DESC5",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION5")));
            bc_row.setAttribute("DESC6",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_DESCRIPTION6")));
            // Bug ID 47637, inoslk, start
            bc_row.setAttribute("FORMAT_SIZE",         base64Encode(itemset3.getRow().getFieldValue("UNPACKED_FORMAT_SIZE")));
            bc_row.setAttribute("SCALE",               base64Encode(itemset3.getRow().getFieldValue("UNPACKED_SCALE")));
            bc_row.setAttribute("REASON_FOR_ISSUE",    base64Encode(itemset3.getRow().getFieldValue("UNPACKED_REASON_FOR_ISSUE")));
            // Bug ID 47637, inoslk, end

            //Bug 62341, Start
            bc_row.setAttribute("OLD_REVISION_DATE",   base64Encode(itemset3.getRow().getFieldValue("REVISION_DATE")));
	    bc_row.setAttribute("OLD_REVISION_TEXT",   base64Encode(itemset3.getRow().getValue("REVISION_TEXT")));
	    bc_row.setAttribute("OLD_REVISION_SIGN",   base64Encode(itemset3.getRow().getValue("REVISION_SIGN")));
	    //Bug 62341, End
            int count = 0;
            if (!mgr.isEmpty(file_names))
            {
               while (st.hasMoreTokens())
               {
                  bc_row.setAttribute("XFILE_NAME" + count++, base64Encode(st.nextToken()));
               }
            }

            bc_row.setAttribute("XFILES_COUNT", base64Encode(Integer.toString(count)));
            bc_detail_node.appendChild(bc_row);
            itemset3.next();
         }
      }
      else
      {
         //Bug 50158, Start
         okFindDocIssues();
         
         trans.clear();
         query = trans.addQuery(itemblk2);
         query.addOrCondition(itemset1.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
         query.includeMeta("ALL");
         query.setBufferSize(itemset2.countDbRows());

         mgr.submit(trans);
         //Bug 50158, End  
         itemset2.first();
         //Bug 50158, used countDbRows() instead of countRows()
         for(int x = 0; x < itemset2.countDbRows(); x++)  
         {
            bc_row = xml_doc.createElement("DOCUMENT" + x);

            doc_class = itemset2.getRow().getValue("DOC_CLASS");
            doc_no    = itemset2.getRow().getValue("DOC_NO");
            doc_sheet = itemset2.getRow().getValue("DOC_SHEET");
            doc_rev   = itemset2.getRow().getValue("DOC_REV");

            bc_row.setAttribute("DOC_KEY",             base64Encode(getKeyValues(doc_class,doc_no,doc_sheet,doc_rev))); //Bug Id 43593
            bc_row.setAttribute("DOC_CLASS",           base64Encode(doc_class));
            bc_row.setAttribute("DOC_NO",              base64Encode(doc_no));
            bc_row.setAttribute("DOC_SHEET",           base64Encode(doc_sheet));
            bc_row.setAttribute("DOC_REV",             base64Encode(doc_rev));
            // Bug ID 43625, inoslk, start
            bc_row.setAttribute("DOC_TITLE",           base64Encode(itemset2.getRow().getValue("DOC_TITLE")));
            // Bug ID 43625, inoslk, end
            bc_row.setAttribute("NEW_REVISION",        "");
            bc_row.setAttribute("NEXT_DOC_SHEET",      base64Encode(itemset2.getRow().getValue("NEXT_DOC_SHEET")));
            bc_row.setAttribute("FILE_NAME",           base64Encode(itemset2.getRow().getValue("FILE_NAME")));
            bc_row.setAttribute("CHANGE_STATUS",       base64Encode(itemset2.getRow().getValue("CHANGE_STATUS")));
            bc_row.setAttribute("DOC_STATE",           base64Encode(itemset2.getRow().getFieldValue("DOC_STATE")));
            bc_row.setAttribute("DT_DOC_REV",          base64Encode(itemset2.getRow().getFieldValue("DT_DOC_REV")));
            bc_row.setAttribute("DOC_REV_TEXT",        base64Encode(itemset2.getRow().getValue("DOC_REV_TEXT")));
            bc_row.setAttribute("USER_CREATED",        base64Encode(itemset2.getRow().getValue("USER_CREATED")));

            // Note: These columns are currently unused, but may be used in future
            // bc_row.setAttribute("OBJECT_REVISION",     "");
            // bc_row.setAttribute("PLANT_ID",            "");
            bc_row.setAttribute("LU_NAME",             base64Encode(itemset2.getRow().getValue("LU_NAME")));
            bc_row.setAttribute("OBJECT_ID",           base64Encode(itemset2.getRow().getValue("OBJECT_ID")));
            bc_row.setAttribute("UPDATE_REVISION",     base64Encode(itemset2.getRow().getValue("UPDATE_REVISION")));
            bc_row.setAttribute("ASSOCIATED_CATEGORY", base64Encode(itemset2.getRow().getValue("ASSOCIATED_CATEGORY")));
            bc_row.setAttribute("DATE1",               "");
            bc_row.setAttribute("SIGN1",               "");
            bc_row.setAttribute("DATE2",               "");
            bc_row.setAttribute("SIGN2",               "");
            bc_row.setAttribute("DATE3",               "");
            bc_row.setAttribute("SIGN3",               "");
            bc_row.setAttribute("DATE4",               "");
            bc_row.setAttribute("SIGN4",               "");
            bc_row.setAttribute("DESC1",               base64Encode(itemset2.getRow().getValue("DESCRIPTION1")));
            bc_row.setAttribute("DESC2",               base64Encode(itemset2.getRow().getValue("DESCRIPTION2")));
            bc_row.setAttribute("DESC3",               base64Encode(itemset2.getRow().getValue("DESCRIPTION3")));
            bc_row.setAttribute("DESC4",               base64Encode(itemset2.getRow().getValue("DESCRIPTION4")));
            bc_row.setAttribute("DESC5",               base64Encode(itemset2.getRow().getValue("DESCRIPTION5")));
            bc_row.setAttribute("DESC6",               base64Encode(itemset2.getRow().getValue("DESCRIPTION6")));
            // Bug ID 47637, inoslk, start
            bc_row.setAttribute("FORMAT_SIZE",         base64Encode(itemset2.getRow().getFieldValue("FORMAT_SIZE")));
            bc_row.setAttribute("SCALE",               base64Encode(itemset2.getRow().getFieldValue("SCALE")));
            bc_row.setAttribute("REASON_FOR_ISSUE",    base64Encode(itemset2.getRow().getFieldValue("REASON_FOR_ISSUE")));
            // Bug ID 47637, inoslk, end
            //Bug 62341, Start
	    bc_row.setAttribute("OLD_REVISION_DATE",    base64Encode(itemset2.getRow().getFieldValue("DT_DOC_REV")));
	    bc_row.setAttribute("OLD_REVISION_TEXT",    base64Encode(itemset2.getRow().getValue("DOC_REV_TEXT")));
	    bc_row.setAttribute("OLD_REVISION_SIGN",    base64Encode(itemset2.getRow().getValue("USER_CREATED")));
	    //Bug 62341, End

            int count = 0;
            if (!mgr.isEmpty(itemset2.getRow().getValue("FILE_NAME")))
            {
               String file_names = getExtendedFileNames(doc_class, doc_no, doc_sheet, doc_rev, itemset2.getRow().getValue("FILE_NAME"));
               StringTokenizer st = new StringTokenizer(file_names, "|");


               while (st.hasMoreTokens())
               {
                  bc_row.setAttribute("XFILE_NAME" + count++, base64Encode(st.nextToken()));
               }
            }

            bc_row.setAttribute("XFILES_COUNT", base64Encode(Integer.toString(count)));
            bc_detail_node.appendChild(bc_row);
            itemset2.next();
         }
      }
      // Bug ID 47367, inoslk, end

      root_node.appendChild(bc_detail_node);
      // XMLUtil.saveToFile(xml_doc, "d:\\bc_xml_response.xml");

      debug("} createMetaFile()");
      return xml_doc;
   }


   private void unpackMetaData(Document xml_doc) throws FndException
   {
      debug("Executing unpackMetaData()...");

      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      String doc_key;
      StringBuffer attr;
      boolean unpack_document;
      // Bug ID 47171, inoslk, Removed DocmawConstant object variable

      Element root_node = xml_doc.getDocumentElement();
      Node documents_node = root_node.getChildNodes().item(1);


      // Get a list of all documents in the briefcase at
      // the time of unpacking. We'll need this information
      // to synchronize with the meta file. i.e unpack only
      // those documents that have not been Unlocked

      okFindBriefcaseIssues();

      String file_names;
      int line_no = 0;
      int xfiles_count = 0;
      int no_docs = documents_node.getChildNodes().getLength();
      //Bug ID 50336, Start
       
      // Bug ID 46925, inoslk, start
      int prev_line_no = 0;
      String prev_change_status = "",prev_doc_no = "",connected_to_doc_at_line_no = "", connected_to_doc_no = "", connected_to_doc_class = "", prev_doc_class = ""; // Bug ID 47320, inoslk, Added new variables
      // Bug ID 46925, inoslk, end
      
      //Bug ID 50336, End 

      trans.clear();
      for (int x = 0; x < no_docs; x++)
      {
         Node doc_node = documents_node.getChildNodes().item(x);
         NamedNodeMap doc_attributes = doc_node.getAttributes();

         doc_key = base64Decode(doc_attributes.getNamedItem("DOC_KEY").getNodeValue());
         unpack_document = true;

         // If document existed in the briefcase during export
         if (!Str.isEmpty(doc_key))
         {
            // Check if it still exists in the briefcase
            if (!documentExists(doc_key))
            {
               unpack_document = false;
            }
         }

         if (unpack_document)
         {
            //Bug ID 50336, Start
            // Bug ID 46925, inoslk, start
            // If the document is a new sheet
            if (mgr.isEmpty(base64Decode(doc_attributes.getNamedItem("DOC_NO").getNodeValue()))) 
            {
               if (base64Decode(doc_attributes.getNamedItem("CHANGE_STATUS").getNodeValue()).equals(db_change_state_newsheet)) // Bug ID 47171, inoslk
               {
                  // If the previous was a new doc, new rev, changed or unchanged doc, must update the connected line_no and doc_no
                  // Else, if the the previous was a new sheet, the values will remain for this sheet too
                  if (!(prev_change_status.equals(db_change_state_newsheet))) // Bug ID 47171, inoslk
                  {
                     connected_to_doc_at_line_no = (prev_line_no == line_no ? "" : Integer.toString(prev_line_no));
                     connected_to_doc_no = prev_doc_no;
                     // Bug ID 47320, inoslk, start
                     connected_to_doc_class = prev_doc_class;
                     // Bug ID 47320, inoslk, end
                  }
               }
               else
               {
                  // Document is not a new sheet, so connected doc_no and line_no do not apply, clear the variables
                  connected_to_doc_at_line_no = "";
                  connected_to_doc_no = "";
                  // Bug ID 47320, inoslk, start
                  connected_to_doc_class = "";
                  // Bug ID 47320, inoslk, end
               }
            }
            else
            {
               // Document is not a new sheet, so connected doc_no and line_no do not apply, clear the variables
               connected_to_doc_at_line_no = "";
               connected_to_doc_no = "";
               connected_to_doc_class = "";
            }

            // Update previous document's values for comparison with the next document
            prev_line_no = line_no;
            prev_change_status = base64Decode(doc_attributes.getNamedItem("CHANGE_STATUS").getNodeValue());
            prev_doc_no = base64Decode(doc_attributes.getNamedItem("DOC_NO").getNodeValue());
            // Bug ID 46925, inoslk, end
         
            // Bug ID 47320, inoslk, start
            prev_doc_class = base64Decode(doc_attributes.getNamedItem("DOC_CLASS").getNodeValue());
            // Bug ID 47320, inoslk, end
            //Bug ID 50336, End 


            attr = new StringBuffer();
            addToAttr("BRIEFCASE_NO",          brief_no, attr);
            addToAttr("LINE_NO",               Integer.toString(line_no),  attr);
            //Bug ID 50336, Start
            if ((mgr.isEmpty(base64Decode(doc_attributes.getNamedItem("DOC_NO").getNodeValue()))) && ((db_change_state_newsheet.equals(base64Decode(doc_attributes.getNamedItem("CHANGE_STATUS").getNodeValue()))))) // Bug ID 47171, inoslk
            {
               // Bug ID 47320, inoslk, start
               addToAttr("DOC_CLASS",          connected_to_doc_class, attr);
               // Bug ID 47320, inoslk, end
               addToAttr("DOC_NO",             connected_to_doc_no, attr);
               
            }
            else
            { 
            //Bug ID 50336, End
               // Bug ID 47320, inoslk, start
               addToAttr("DOC_CLASS",             base64Decode(doc_attributes.getNamedItem("DOC_CLASS").getNodeValue()), attr);
               // Bug ID 47320, inoslk, end
               addToAttr("DOC_NO",             base64Decode(doc_attributes.getNamedItem("DOC_NO").getNodeValue()), attr);
            }
            // Bug ID 46925, inoslk, end
            addToAttr("DOC_SHEET",             base64Decode(doc_attributes.getNamedItem("DOC_SHEET").getNodeValue()), attr);

            if(db_change_state_newrev.equals(base64Decode(doc_attributes.getNamedItem("CHANGE_STATUS").getNodeValue()))) // Bug ID 47171, inoslk
            {
               addToAttr("DOC_REVISION",          base64Decode(doc_attributes.getNamedItem("NEW_REVISION").getNodeValue()), attr);
            }
            else
            {
               addToAttr("DOC_REVISION",          base64Decode(doc_attributes.getNamedItem("DOC_REV").getNodeValue()), attr);
            }

            // Bug ID 43625, inoslk, start
            addToAttr("DOC_TITLE",             base64Decode(doc_attributes.getNamedItem("DOC_TITLE").getNodeValue()),attr);
            // Bug ID 43625, inoslk, end
            addToAttr("NEXT_SHEET_NUMBER",     base64Decode(doc_attributes.getNamedItem("NEXT_DOC_SHEET").getNodeValue()), attr);
            addToAttr("DOCUMENT_KEY",          base64Decode(doc_attributes.getNamedItem("DOC_KEY").getNodeValue()), attr);
            addToAttr("REVISION_DATE",         checkDateFormat("REVISION_DATE",base64Decode(doc_attributes.getNamedItem("DT_DOC_REV").getNodeValue())), attr);
            addToAttr("REVISION_TEXT",         base64Decode(doc_attributes.getNamedItem("DOC_REV_TEXT").getNodeValue()), attr);
            addToAttr("REVISION_SIGN",         base64Decode(doc_attributes.getNamedItem("USER_CREATED").getNodeValue()), attr);

            // Note: These columns are currently unused, but may be used in future
            // addToAttr("OBJECT_REVISION",       base64Decode(doc_attributes.getNamedItem("OBJECT_REVISION").getNodeValue()), attr);
            // addToAttr("PLANT_ID",              base64Decode(doc_attributes.getNamedItem("PLANT_ID").getNodeValue()), attr);

            addToAttr("LU_NAME",               base64Decode(doc_attributes.getNamedItem("LU_NAME").getNodeValue()), attr);
            addToAttr("OBJECT_ID",             base64Decode(doc_attributes.getNamedItem("OBJECT_ID").getNodeValue()), attr);
            addToAttr("UPDATE_REVISION",       base64Decode(doc_attributes.getNamedItem("UPDATE_REVISION").getNodeValue()), attr);
            addToAttr("ASSOCIATED_CATEGORY",   base64Decode(doc_attributes.getNamedItem("ASSOCIATED_CATEGORY").getNodeValue()), attr);

            addToAttr("DATE1",                 checkDateFormat("DATE1",base64Decode(doc_attributes.getNamedItem("DATE1").getNodeValue())), attr);
            addToAttr("SIGN1",                 base64Decode(doc_attributes.getNamedItem("SIGN1").getNodeValue()), attr);
            addToAttr("DATE2",                 checkDateFormat("DATE2",base64Decode(doc_attributes.getNamedItem("DATE2").getNodeValue())), attr);
            addToAttr("SIGN2",                 base64Decode(doc_attributes.getNamedItem("SIGN2").getNodeValue()), attr);
            addToAttr("DATE3",                 checkDateFormat("DATE3",base64Decode(doc_attributes.getNamedItem("DATE3").getNodeValue())), attr);
            addToAttr("SIGN3",                 base64Decode(doc_attributes.getNamedItem("SIGN3").getNodeValue()), attr);
            addToAttr("DATE4",                 checkDateFormat("DATE4",base64Decode(doc_attributes.getNamedItem("DATE4").getNodeValue())), attr);
            addToAttr("SIGN4",                 base64Decode(doc_attributes.getNamedItem("SIGN4").getNodeValue()), attr);
            addToAttr("DESCRIPTION1",          base64Decode(doc_attributes.getNamedItem("DESC1").getNodeValue()), attr);
            addToAttr("DESCRIPTION2",          base64Decode(doc_attributes.getNamedItem("DESC2").getNodeValue()), attr);
            addToAttr("DESCRIPTION3",          base64Decode(doc_attributes.getNamedItem("DESC3").getNodeValue()), attr);
            addToAttr("DESCRIPTION4",          base64Decode(doc_attributes.getNamedItem("DESC4").getNodeValue()), attr);
            addToAttr("DESCRIPTION5",          base64Decode(doc_attributes.getNamedItem("DESC5").getNodeValue()), attr);
            addToAttr("DESCRIPTION6",          base64Decode(doc_attributes.getNamedItem("DESC6").getNodeValue()), attr);
            addToAttr("DOC_BC_CHANGE_STATUS",  base64Decode(doc_attributes.getNamedItem("CHANGE_STATUS").getNodeValue()), attr);
            // Bug ID 46925, inoslk, Removed the modification added for 45219 and added new attribute CONNECTED_TO_DOC_AT_LINE
            //Bug ID 50336, Start
            //addToAttr("CONNECTED_TO_DOC_AT_LINE", connected_to_doc_at_line_no, attr);
            addToAttr("DESIRED_DOC_NO",                base64Decode(doc_attributes.getNamedItem("DOC_NO").getNodeValue()), attr);
            //Bug ID 50336, End
            addToAttr("FORMAT_SIZE",           base64Decode(doc_attributes.getNamedItem("FORMAT_SIZE").getNodeValue()), attr);
            addToAttr("SCALE",                 base64Decode(doc_attributes.getNamedItem("SCALE").getNodeValue()), attr);
            addToAttr("REASON_FOR_ISSUE",      base64Decode(doc_attributes.getNamedItem("REASON_FOR_ISSUE").getNodeValue()), attr);
            // Bug ID 47637, inoslk, end

            // master file name
            file_names = base64Decode(doc_attributes.getNamedItem("FILE_NAME").getNodeValue());

            // Concatenate extended file names, if any
            xfiles_count = Integer.parseInt(base64Decode(doc_attributes.getNamedItem("XFILES_COUNT").getNodeValue()));

            for (int c = 1; c <= xfiles_count; c++)
            {
               file_names += "|" + base64Decode(doc_attributes.getNamedItem("XFILE_NAME" + c).getNodeValue());
            }

            addToAttr("FILE_NAME", file_names, attr);
            line_no++;

            cmd = trans.addCustomCommand("UNPACKED_DOC" + x, "DOC_BRIEFCASE_UNPACKED_API.New__");
            cmd.addParameter("DUMMY1");
            cmd.addParameter("DUMMY1");
            cmd.addParameter("DUMMY1");
            cmd.addParameter("DUMMY1", attr.toString());
            cmd.addParameter("DUMMY1", "DO");
         }
      }

      //trans.addQuery("BRIEF", "SELECT OBJID, OBJVERSION FROM DOC_BRIEFCASE WHERE BRIEFCASE_NO='" + brief_no + "'");
      //bug 58216 starts
      ASPQuery query = trans.addQuery("BRIEF", "SELECT OBJID, OBJVERSION FROM DOC_BRIEFCASE WHERE BRIEFCASE_NO = ?");
      query.addParameter("BRIEFCASE_NO",brief_no);
      //bug 58216 ends
      cmd = trans.addCustomCommand("BRIEFSTATE", "DOC_BRIEFCASE_API.Unpack_Briefcase__");
      cmd.addParameter("DUMMY1");
      cmd.addReference("OBJID", "BRIEF/DATA");
      cmd.addReference("OBJVERSION", "BRIEF/DATA");
      cmd.addParameter("DUMMY1");
      cmd.addParameter("DUMMY1", "DO");

      //Bug Id 48298, Start
      cmd = trans.addCustomCommand("BRIEFIMPORTPATH", "DOC_BRIEFCASE_API.Set_Imported_Path_");
      cmd.addParameter("BRIEFCASE_NO",brief_no);
      cmd.addParameter("IN_1",imported_path);
      //Bug Id 48298, End

      trans = mgr.perform(trans);
   }


   private String  checkDateFormat(String date, String value) throws FndException
   {
      debug(" &&&&&&&&&&&&&&&& checkDateFormat ************** value " + value);
      ASPManager mgr = getASPManager();
      String return_str = "";
      Date temp_date;

      if (mgr.isEmpty(value))
         return "";

      SimpleDateFormat date_format =  new SimpleDateFormat();
      date_format.applyPattern("yyyy-MM-dd");

      try
      {
         temp_date = date_format.parse(value);
      }
      catch(ParseException pe)
      {
         throw new FndException(mgr.translate("DOCMAWBRIEFCASEMETAHANDLERWRONGDATEFORMAT: Date format for &1 not correct",date));
      }

      //following codes can be removed but i left them if we allow user to use other formats too :bakalk
      GregorianCalendar gc =  new GregorianCalendar();
      gc.setTime(temp_date);
      int year_ = gc.get(Calendar.YEAR);
      int month_ = gc.get(Calendar.MONTH);
      int date_ = gc.get(Calendar.DAY_OF_MONTH);
      String strMonth = (month_+1)<=9? "0"+(month_+1):""+(month_+1);
      String strDate = date_<=9? "0"+date_ :""+date_;
      return_str = year_+"-"+strMonth+"-" + strDate;
       debug(" &&&&&&&&&&&&&&&& checkDateFormat ************** return_str " + return_str);
      return return_str;
   }


   private boolean documentExists(String doc_key)
   {
      itemset1.first();
      for (int x = 0; x < itemset1.countRows(); x++)
      {
         if (itemset1.getRow().getValue("ITEM1_DOC_KEY").equals(doc_key))
            return true;

         itemset1.next();
      }
      return false;
   }


   private String getExtendedFileNames(String doc_class, String doc_no, String doc_sheet, String doc_rev, String original_file_name)
   {
      debug("Executing getExtendedFileNames()...");

      ASPManager mgr = getASPManager();
      //String base_name = original_file_name.substring(0, original_file_name.lastIndexOf("."));

      trans.clear();
      //Bug Id 67966, Start
     /* ASPCommand cmd = trans.addCustomFunction("DOCTYPESLIST", "DOC_CLASS_PROC_ACTION_LINE_API.Get_All_Doc_Types", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("PROCESS",  "CHECKOUT");
      */
      //Bug Id 67966, End

      //Bug Id 71463 Start
      ASPCommand cmd = trans.addCustomFunction("DOCFILESLIST", "EDM_FILE_API.Get_Extended_File_Names", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO",    doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV",   doc_rev);
      cmd.addParameter("PROCESS",  "CHECKOUT");
      cmd.addParameter("TXTSEP",  "|");
      
      //Bug Id 71463 End

      /*cmd = trans.addCustomFunction("EDMREPINFO", "EDM_FILE_API.get_edm_information", "DUMMY1");
      cmd.addParameter("DOC_CLASS", doc_class);
      cmd.addParameter("DOC_NO",    doc_no);
      cmd.addParameter("DOC_SHEET", doc_sheet);
      cmd.addParameter("DOC_REV",   doc_rev);
      cmd.addParameter("DOC_TYPE",  "ORIGINAL");
      */
      trans = mgr.perform(trans);

      String doc_file_list = trans.getValue("DOCFILESLIST/DATA/DUMMY1");

      if (mgr.isEmpty(doc_file_list))
         doc_file_list = "";

      //StringTokenizer st = new StringTokenizer(doc_file_list, "|");

      String file_name = "";
      //while(st.hasMoreTokens())
      //{
          file_name = doc_file_list;
         
      //}


      debug("Executed getExtendedFileNames()");

      return file_name;
   }



   private String getStringAttribute(String attrString, String attrName)
   {
      StringTokenizer st = new StringTokenizer(attrString, "^");
      String str;

      attrName+="=";
      while (st.hasMoreTokens())
      {
         str = st.nextToken();
         if (str.startsWith(attrName))
         {
            return str.substring(attrName.length());
         }
      }
      return "";
   }


   private void addToAttr(String name, String value, StringBuffer attr)
   {
      attr.append(name);
      attr.append(DocmawUtil.FIELD_SEPARATOR);
      attr.append(value);
      attr.append(DocmawUtil.RECORD_SEPARATOR);
   }


   private void populateMetaData()
   {
      debug("Executing populateMetaData()...");

      okFindBriefcase();       // Retrieve the Briefcase
      // Bug ID 50158, Start
      countFindBriefcaseIssues();
      // Bug ID 50158, End
      okFindBriefcaseIssues(); // Get all briefcase issues
      okFindDocIssues();       // Based on the results of the above, get all corresponding doc issues
      // Bug ID 47367, inoslk, start
      okFindBcUnpackedIssues();
      // Bug ID 47367, inoslk, end
   }


   private void okFindBriefcase()
   {
      debug("Executing okFindBriefcase()...");

      ASPManager mgr = getASPManager();
      ASPQuery query;

      trans.clear();
      query = trans.addQuery(headblk);
      //bug 58216 starts
      query.addWhereCondition("BRIEFCASE_NO = ?");
      query.addParameter("BRIEFCASE_NO",brief_no);
      //bug 58216 ends
      query.includeMeta("ALL");
      mgr.querySubmit(trans, headblk);
   }

   // Bug ID 50158, Start
   public void  countFindBriefcaseIssues()
   {
      ASPManager mgr = getASPManager();
      ASPQuery query;

      query = trans.addQuery("BRISCNT","DOC_BRIEFCASE_ISSUE",itemblk1);
      query.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      query.addWhereCondition("BRIEFCASE_NO = ?");
      query.addParameter("BRIEFCASE_NO",brief_no);
      //bug 58216 ends
      trans = mgr.perform(trans);
      brief_issue_count = toInt(trans.getValue("BRISCNT/DATA/N"));
   }
   // Bug ID 50158, End


   private void okFindBriefcaseIssues()
   {
      debug("Executing okFindBriefcaseIssues()...");

      ASPManager mgr = getASPManager();
      ASPQuery query;

      trans.clear();
      query = trans.addQuery(itemblk1);
      //bug 58216 starts
      query.addWhereCondition("BRIEFCASE_NO = ?");
      query.addParameter("BRIEFCASE_NO",brief_no);
      //bug 58216 ends
      query.includeMeta("ALL");
      // Bug ID 50158, Start
      if (brief_issue_count != 0) 
      {
         query.setBufferSize(brief_issue_count);
      }
      // Bug ID 50158, End
      mgr.querySubmit(trans, itemblk1);
   }


   private void okFindDocIssues()
   {
      debug("Executing okFindDocIssues()...");

      ASPManager mgr = getASPManager();
      ASPQuery query;

      // need to check if itemset1 has any rows,
      // otherwise the following query will return all rows

      if(itemset1.countRows() != 0)
      {
         trans.clear();
         query = trans.addQuery(itemblk2);
         query.addOrCondition(itemset1.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
         query.includeMeta("ALL");
         mgr.querySubmit(trans, itemblk2);
      }
   }


   // Bug ID 47367, inoslk, start
   private void okFindBcUnpackedIssues()
   {
      debug("Executing okFindBcUnpackedIssues()...");

      ASPManager mgr = getASPManager();
      ASPQuery query;

      trans.clear();
      query = trans.addQuery(itemblk3);
      //bug 58216 starts
      query.addWhereCondition("BRIEFCASE_NO = ?");
      query.addParameter("BRIEFCASE_NO",old_brief_no);
      //bug 58216 ends
      query.includeMeta("ALL");
      mgr.querySubmit(trans, itemblk3);
   }
   // Bug ID 47367, inoslk, end


   public void  preDefine()
   {
      debug("Executing preDefine()...");

      ASPManager mgr = getASPManager();


      //
      // Briefcase header
      //
      headblk = mgr.newASPBlock("BRIEFCASE_HEADER");

      headblk.addField("OBJID");
      headblk.addField("OBJVERSION");
      headblk.addField("BRIEFCASE_NO");
      headblk.addField("RECEIVING_COMPANY");
      headblk.addField("RECEIVING_PERSON");
      headblk.addField("EXPORT_DATE", "Date");
      headblk.addField("LATEST_RETURN_DATE", "Date");
      headblk.setView("DOC_BRIEFCASE");
      headset = headblk.getASPRowSet();


      //
      // Container for Briefcase Issues
      //
      itemblk1 = mgr.newASPBlock("BRIEFCASE_ISSUES");
      itemblk1.addField("ITEM1_DOC_KEY").setFunction("Client_SYS.Get_Key_Reference('DocIssue','DOC_CLASS',:DOC_CLASS,'DOC_NO',:DOC_NO,'DOC_SHEET',:DOC_SHEET,'DOC_REV',:DOC_REV)");  //Bug Id 43593
      itemblk1.addField("ITEM1_DOC_CLASS").setDbName("DOC_CLASS");
      itemblk1.addField("ITEM1_DOC_NO").setDbName("DOC_NO");
      itemblk1.addField("ITEM1_DOC_SHEET").setDbName("DOC_SHEET");
      itemblk1.addField("ITEM1_DOC_REV").setDbName("DOC_REV");
      itemblk1.setView("DOC_BRIEFCASE_ISSUE");
      itemset1 = itemblk1.getASPRowSet();


      //
      // Container for all the document meta-data
      //

      itemblk2 = mgr.newASPBlock("ITEM2");
      itemblk2.addField("DOC_CLASS");
      itemblk2.addField("DOC_NO");
      itemblk2.addField("DOC_SHEET");
      itemblk2.addField("DOC_REV");
      // Bug ID 43625, inoslk, start
      itemblk2.addField("DOC_TITLE").setFunction("Doc_Title_API.Get_Title(:DOC_CLASS,:DOC_NO)");
      // Bug ID 43625, inoslk, end
      itemblk2.addField("DOC_TYPE");
      itemblk2.addField("NEXT_DOC_SHEET");
      itemblk2.addField("FILE_NAME").setFunction("EDM_FILE_API.Get_Local_File_Name__(:DOC_CLASS, :DOC_NO, :DOC_SHEET, :DOC_REV, :DOC_TYPE)");
      itemblk2.addField("CHANGE_STATUS").setFunction("DOC_BC_CHANGE_STATUS_API.Decode('Unchanged')");
      itemblk2.addField("DOC_STATE").setDbName("STATE");
      itemblk2.addField("DT_DOC_REV", "Date","yyyy-MM-dd");
      itemblk2.addField("DOC_REV_TEXT");
      itemblk2.addField("USER_CREATED");
      itemblk2.addField("LU_NAME").setFunction("Client_SYS.Get_Item_Value('LU_NAME', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      itemblk2.addField("OBJECT_ID").setFunction("Client_SYS.Get_Item_Value('OBJECT_ID', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      itemblk2.addField("UPDATE_REVISION").setFunction("Client_SYS.Get_Item_Value('KEEP_LAST_DOC_REV', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      itemblk2.addField("ASSOCIATED_CATEGORY").setFunction("Client_SYS.Get_Item_Value('ASSOCIATED_CATEGORY', Doc_Reference_Object_API.Get_Object_Info_For_Bc(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV))");
      itemblk2.addField("DESCRIPTION1");
      itemblk2.addField("DESCRIPTION2");
      itemblk2.addField("DESCRIPTION3");
      itemblk2.addField("DESCRIPTION4");
      itemblk2.addField("DESCRIPTION5");
      itemblk2.addField("DESCRIPTION6");
      // Bug ID 47637, inoslk, start
      itemblk2.addField("FORMAT_SIZE");
      itemblk2.addField("SCALE");
      itemblk2.addField("REASON_FOR_ISSUE");
      // Bug ID 47637, inoslk, end
      itemblk2.setView("DOC_ISSUE_REFERENCE");
      itemset2 = itemblk2.getASPRowSet();

      // Bug ID 47367, inoslk, start
      // Container for unpacked documents
      itemblk3 = mgr.newASPBlock("ITEM3");
      itemblk3.addField("UNPACKED_BRIEFCASE_NO").setDbName("BRIEFCASE_NO");
      itemblk3.addField("LINE_NO");
      itemblk3.addField("UNPACKED_DOC_CLASS").setDbName("DOC_CLASS");
      itemblk3.addField("UNPACKED_DOC_NO").setDbName("DOC_NO");
      itemblk3.addField("UNPACKED_DOC_SHEET").setDbName("DOC_SHEET");
      itemblk3.addField("DOC_REVISION");
      itemblk3.addField("UNPACKED_DOC_TITLE").setDbName("DOC_TITLE");
      itemblk3.addField("NEXT_SHEET_NUMBER");
      itemblk3.addField("UNPACKED_FILE_NAME").setDbName("FILE_NAME");
      itemblk3.addField("DOC_BC_CHANGE_STATUS");
      itemblk3.addField("DOCUMENT_STATE").setFunction("DOC_ISSUE_API.Get_State(:UNPACKED_DOC_CLASS, :UNPACKED_DOC_NO, :UNPACKED_DOC_SHEET, :DOC_REVISION)");
      itemblk3.addField("REVISION_DATE", "Date","yyyy-MM-dd");
      itemblk3.addField("REVISION_TEXT");
      itemblk3.addField("REVISION_SIGN");
      itemblk3.addField("UNPACKED_LU_NAME").setDbName("LU_NAME");
      itemblk3.addField("UNPACKED_OBJECT_ID").setDbName("OBJECT_ID");
      itemblk3.addField("UNPACKED_UPDATE_REVISION").setDbName("UPDATE_REVISION");
      itemblk3.addField("UNPACKED_ASSOCIATED_CATEGORY").setDbName("ASSOCIATED_CATEGORY");
      itemblk3.addField("DATE1","Date","yyyy-MM-dd");
      itemblk3.addField("SIGN1");
      itemblk3.addField("DATE2","Date","yyyy-MM-dd");
      itemblk3.addField("SIGN2");
      itemblk3.addField("DATE3","Date","yyyy-MM-dd");
      itemblk3.addField("SIGN3");
      itemblk3.addField("DATE4","Date","yyyy-MM-dd");
      itemblk3.addField("SIGN4");
      itemblk3.addField("UNPACKED_DESCRIPTION1").setDbName("DESCRIPTION1");
      itemblk3.addField("UNPACKED_DESCRIPTION2").setDbName("DESCRIPTION2");
      itemblk3.addField("UNPACKED_DESCRIPTION3").setDbName("DESCRIPTION3");
      itemblk3.addField("UNPACKED_DESCRIPTION4").setDbName("DESCRIPTION4");
      itemblk3.addField("UNPACKED_DESCRIPTION5").setDbName("DESCRIPTION5");
      itemblk3.addField("UNPACKED_DESCRIPTION6").setDbName("DESCRIPTION6");
      // Bug ID 47637, inoslk, start
      itemblk3.addField("UNPACKED_FORMAT_SIZE").setDbName("FORMAT_SIZE");
      itemblk3.addField("UNPACKED_SCALE").setDbName("SCALE");
      itemblk3.addField("UNPACKED_REASON_FOR_ISSUE").setDbName("REASON_FOR_ISSUE");
      // Bug ID 47637, inoslk, end
      // Bug ID 47171, inoslk, start
      itemblk3.addField("DOCUMENT_KEY");
      // Bug ID 47171, inoslk, end
      itemblk3.setView("DOC_BRIEFCASE_UNPACKED");
      itemset3 = itemblk3.getASPRowSet();
      // Bug ID 47367, inoslk, end

      //
      // Dummy fields
      //
      dummyblk = mgr.newASPBlock("DUMMY");
      dummyblk.addField("DUMMY1");
      //Bug ID 43593, inoslk, start
      dummyblk.addField("OUT_1");
      //Bug ID 43593, inoslk, end
      dummyblk.addField("IN_1"); //Bug Id 48298
      dummyblk.addField("PROCESS");//Bug Id 67966
      dummyblk.addField("TXTSEP");//Bug Id 71463

      debug("preDefine() executed!");
   }


   private void sendErrorToClient(String msg)
   {
   }


   private void sendErrorToClient(String msg, Exception e)
   {
      debug(msg + " : " + e.getMessage());
   }


   private Document formatErrorMessage(Exception e)
   {
      ASPManager mgr = getASPManager();
      Document xml_doc = new DocumentImpl();


      // Root element

      Element root_node = xml_doc.createElement("BC_META_DATA");
      xml_doc.appendChild(root_node);


      // Build error message

      StringBuffer message = new StringBuffer();

      if ("CREATE_META_FILE".equals(action))
      {
         message.append(mgr.translate("DOCMAWBRIEFCASEMETAHANDLERSEVERERRORCREATEMETAFILE: An error occured while exporting the briefcase:"));
      }
      else if ("UNPACK_META_FILE".equals(action))
      {
         message.append(mgr.translate("DOCMAWBRIEFCASEMETAHANDLERSEVERERRORUNPACKMETAFILE: An error occured while importing the briefcase:"));
      }

      message.append("\n\n");
      // Bug ID 47320, inoslk, start
      String msg = e.getMessage();
      int is_ora_index = msg.indexOf("ORA");

      if (is_ora_index != -1)
         msg = msg.substring(0,is_ora_index);

      message.append(msg);
      message.append("\n");
      message.append(mgr.translate("DOCMAWBCMETAHANDLEROPERATIONCANCELLED: Operation cancelled."));
      // Bug ID 47320, inoslk, end

      Element error_node = xml_doc.createElement("ERROR");
      error_node.setAttribute("DESC", base64Encode(message.toString()));
      root_node.appendChild(error_node);

      return xml_doc;
   }


   private Document ackUnpackMetaData()
   {
      Document xml_doc = new DocumentImpl();
      Element  root_node= xml_doc.createElement("BC_META_DATA");
      xml_doc.appendChild(root_node);

      Element no_exp =  xml_doc.createElement("SUCESS");
      root_node.appendChild(no_exp);
      return xml_doc;
   }

   // Bug ID 42606, inoslk, start
   private String getDocmawTempPath()
   {
      ASPManager mgr = getASPManager();
      String path = null;
      File tmp_dir;
      //Bug Id 49558, Start
      boolean is_absolute;
      is_absolute = new File(mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH")).isAbsolute();

      if(is_absolute)/* For work with the existing hard coded path in docmawconfig.xml */
      {
         path = mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH");
      }
      else
      {
         try
         {
            path = new File(mgr.getPhyPath(mgr.getConfigParameter("DOCMAW/DOCUMENT_TEMP_PATH"))).getCanonicalPath();
         }
         catch(IOException e)
         {
            error(e);
         }
      }
      //Bug Id 49558, End

      // Get docmaw temp path and check if the file sperator is valid
      path = path.charAt(path.length()-1) == File.separatorChar?path:path+File.separator;

      // Check if temporary path exists
      tmp_dir = new File(path);

      if (!tmp_dir.exists())
         return null;

      return path;
   }
   // Bug ID 42606, inoslk, end

   // Bug ID 43593, Added this method to get the key values to the excel sheet.
   private String getKeyValues(String doc_class, String doc_no, String doc_sheet, String doc_rev)
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      String sKey = "";

      trans.clear();

      cmd = trans.addCustomFunction("GETKEYREF","Client_SYS.Get_Key_Reference","OUT_1");
      cmd.addParameter("DUMMY1","DocIssue");
      cmd.addParameter("DOC_CLASS","DOC_CLASS");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO","DOC_NO");
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET","DOC_SHEET");
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV","DOC_REV");
      cmd.addParameter("DOC_REV",doc_rev);

      trans = mgr.perform(trans);
      sKey = trans.getValue("GETKEYREF/DATA/OUT_1");

      return sKey;
  }

}
