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
 *  File        : DocumentTransferHandler.java
 *  Description :
 *
 *  History
 *
 *  Date        Sign    Descripiton
 *  ----        ----    -----------
 *
 *  2003-01-29  MDAHSE  Added debug method and constant.
 *  2003-02-06  MDAHSE  Thought that we might spell correctly for once... :)
 *                      Renamed doc_buf to main_buf (could'nt find a better name)
 *                      and introduced two new buffers, doc_buf and act_buf to speed
 *                      things up (no need to call getBuffer() each time you want to
 *                      get a value now).
 *  2003-02-10  DIKALK  The call to countDocuments() in reformatTransferredBuffer()
 *                      was removed since it threw a NullPointerException.
 *  2003-02-10  MDAHSE  Addded new private method getValue() to replace the need to
 *                      use reformatTransferredBuffer(). Check the method for details.
 *  2003-04-03  DIKALK  Addded support for "old-style" urls where data is transfered
 *                      using normal query string paramters.
 *  2003-04-24  DIKALK  Overloaded getDataTransferUrl() to allow the ACTION buffer to
 *                      contain any number of items. Renamed a few variables, methods.
 *                      Made getActionProperty() and getDocumentProperty()  public
 * 2003-04-26  INOSLK   Added method getLocalFileName.
 * 2003-04-26  BAKALK   Added a method getDocTitle.
 * 2003-05-14  BAKALK   Modified getDocRev().
 * 2003-05-28  NISILK   Modified getFileType().
 * 2003-06-09  NISILK   Modified getFileType().
 * 2003-08-14  NISILK   call id 100766 Added method setDocumentProperty and Modified
                        methods dataTransferred and queryParamTransfer in order to merge
                        Bug Id 37037
 * 2003-09-01  NISILK   call id 95532. Removed the mandatory check for FILE_TYPE in
                        method queryParamTransfer().
 * 2003-09-19  THWILK   Call ID 103615.Checked the DEBUG flag before tracing the buffer.
 * 2003-09-25  INOSLK   Call ID 103727 - Added methods getNumGenerator,getBookingList,
                        getId1,getId2.
 * 2003-09-30  DIAKLK   Removed methods getNumGenerator(), getBookingList(), getId1(),
                        getId2(), getDocTitle() and getLocalFileName()
 * 2003-09-30  DIAKLK   Documented the class.
 * 2004-11-09  DIKALK   Made getCurrentDocumentCount() public
 * 2005-02-15  DIKALK   Removed method getFileAction()
 * 2005-07-18  DIKALK   Added another overloaded version of method getDataTransferUrl()
 * 2008-07-07  AMNALK   Bug Id 72460, Added new methods previousDocument() & lastDocument()
 *
 */



package ifs.docmaw.edm;

import ifs.fnd.asp.*;
import ifs.fnd.service.Util;
import ifs.fnd.service.FndException;
import ifs.fnd.util.Str;



/**
 * DocumentTransferHandler is a useful class for handling a set of documents records
 * that needs to be transferred from one page to another. This class wraps an instance
 * of ASPBuffer, exposing a simpler interface for retrieving data on the transferred
 * documents. This class supports 2 kinds of (HTTP GET) request urls:
 *
 *       - A url created using the DocumentTransferHandler.getDataTransferUrl()
 *
 *       - A url containing the mandatory query parameters (DOC_CLASS, DOC_NO
 *         DOC_SHEET and PROCESS_DB)
 *
 *
 * Both requests results in the creation of an instance of ASPBuffer and the following
 * structure is assumed:
 *
 *
 * Header -->  0:$ACTION=
 *                !                        _
 *                0:$FILE_ACTION=VIEW       |
 *                1:$DOC_TYPE=ORIGINAL      | Optional attributes
 *                2:$...                   _|
 *   Body -->  1:$DOCUMENTS=
 *                !
 *                0:$DATA=
 *                   !                     _
 *                   0:$DOC_CLASS=DRAW      |
 *                   1:$DOC_NO=1000436      |
 *                   2:$DOC_SHEET=1         | Optional attributes
 *                   3:$DOC_REV=A1          |
 *                   4:$FILE_TYPE=NOTEPAD   |
 *                   5:$...                _|
 *                1:$DATA=
 *                   !
 *                   0:$DOC_CLASS=DRAW
 *                   1:$DOC_NO=1000440
 *                   2:$DOC_SHEET=1
 *                   3:$DOC_REV=A1
 *                   4:$FILE_TYPE*
 *
 *
 * The purpose of the buffer-header is to hold information that is common to all
 * documents, like type of file action, the local checkout path etc. The buffer-body
 * on the other hand, is used to hold document specific information.
 *
 * Note that you can add your own set of attributes to the header or body and retrieve
 * them *without* having to modify this class.
 *
 * Any attributed of the header can be retrieved using getActionProperty(). Similarly
 * any attribute of the body can be retrieved using getDocumentProperty(). This class
 * also provides a set of convenient methods for commonly accessed attributes. For
 * example, methods like getDocClass(), getDocNo(), getDocType() etc.
 *
 */

public class DocumentTransferHandler
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.edm.DocumentTransferHandler");

   private final static String TRANSFER_PARAM_NAME = "__TRANSFER";


   private ASPManager mgr;
   private ASPBuffer  main_buf, doc_buf, act_buf;
   private ASPContext ctx;
   private ASPLog     log;
   private int        current_doc;
   private boolean    loaded_from_context;

   public DocumentTransferHandler(ASPManager mgr) throws FndException
   {
      this.mgr = mgr;
      ctx      = mgr.getASPContext();
      log      = mgr.getASPLog();


      if ((main_buf = ctx.readBuffer("TRANSFERRED_DOCUMENTS")) != null)
      {
         debug("Data fetched from context buffer...");
         loaded_from_context = true;
      }
      else if (dataTransferred())
      {
         debug("Standard data transfered...");
         main_buf = getTransferedData();
         firstDocument();
      }
      else
      {
         throw new FndException("Can't create DocumentTransferHandler. Malformed request.");
      }

      // Set specialized buffers for the document
      // records and fileaction
      doc_buf  = main_buf.getBuffer("DOCUMENTS");
      act_buf  = main_buf.getBuffer("ACTION");


      if (loaded_from_context)
         goToDocument(Integer.parseInt(getActionProperty("CURRENT_DOCUMENT")));


      // Trace tranfer contents..
      if (DEBUG)
      {
         trace();
      }
   }


   /**
    * Returns a url to the specified page.
    *
    * @param mgr
    * @param transfer_page
    * @param buf: The buffer returned in a call to ASPRowSet.getSelectedRows()
    *
    * @return Returns a url to the specified page.
    */
   public static String getDataTransferUrl(ASPManager mgr, String transfer_page, ASPBuffer buf)
   {
      String serialized_data = mgr.pack(buf);
      String url = transfer_page + "?" + DocumentTransferHandler.TRANSFER_PARAM_NAME + "=" + serialized_data;
      return url;
   }


   /**
    * Returns a url to the specified page.
    *
    * @param mgr
    * @param transfer_page
    * @param file_action
    *                 Name of File Action
    * @param doc_type Document Type
    * @param data     The buffer returned in a call to ASPRowSet.getSelectedRows()
    *
    * @return Returns a url to the specified page.
    */
   public static String getDataTransferUrl(ASPManager mgr, String transfer_page, String file_action, String doc_type, ASPBuffer doc_buf)
   {
      ASPBuffer buf    = mgr.newASPBuffer();
      ASPBuffer header = buf.addBuffer("ACTION");

      header.addItem("FILE_ACTION", file_action);
      header.addItem("DOC_TYPE", doc_type);
      buf.addBuffer("DOCUMENTS", doc_buf);

      String serialized_data = mgr.pack(buf);
      String url = transfer_page + "?" + DocumentTransferHandler.TRANSFER_PARAM_NAME + "=" + serialized_data;
      return url;
   }



   /**
    * Returns a url to the specified page.
    *
    * @param mgr
    * @param transfer_page
    * @param action_data
    *                 Buffer containing info. about the action
    * @param doc_data The buffer returned in a call to ASPRowSet.getSelectedRows()
    *
    * @return Returns a url to the specified page.
    */
   public static String getDataTransferUrl(ASPManager mgr, String transfer_page, ASPBuffer action_buf, ASPBuffer doc_buf)
   {
      ASPBuffer buf    = mgr.newASPBuffer();

      buf.addBuffer("ACTION", action_buf);
      buf.addBuffer("DOCUMENTS", doc_buf);

      if (DEBUG) {
          buf.traceBuffer("BUFFER STRUCTURE");
      }

      String serialized_data = mgr.pack(buf);
      String url = transfer_page + "?" + DocumentTransferHandler.TRANSFER_PARAM_NAME + "=" + serialized_data;
      return url;
   }

   /**
    * The buffer returned by ASPRowSet.getSelectedRows() contains item names only
    * for the first DATA buffer. This is to cut down on the size of the buffer as
    * it is generally used to transfer data to other pages. reformatTransferredBuffer()
    * recreates the DATA buffers in the desired format
    */
   private void reformatTransferredBuffer()
   {
      ASPBuffer data = main_buf.getBuffer("DOCUMENTS");

      if (data.countItems() == 1)
      {
         return;
      }

      ASPBuffer documents = mgr.newASPBuffer();
      ASPBuffer row0 = data.getBufferAt(0);
      ASPBuffer doc, row;
      int count = 0;

      do{
         row = data.getBufferAt(count);
         doc = mgr.newASPBuffer();

         for (int x = 0; x < row.countItems(); x++)
         {
            doc.addItem(row0.getNameAt(x), row.getValueAt(x));
         }

         documents.addBuffer("DATA", doc);

      }while(++count < data.countItems());

      main_buf.removeItem("DOCUMENTS");
      main_buf.addBuffer("DOCUMENTS", documents);
   }


   /**
    * Returns true if the current HTTP request is a data transfer
    */
   private boolean dataTransferred()
   {
      return (bufferedTransfer() || queryParamTransfer());
   }


   private boolean bufferedTransfer()
   {
      return mgr.getQueryStringValue(TRANSFER_PARAM_NAME) != null;
   }


   private boolean queryParamTransfer()
   {
      // Modified by Terry 20130828
      // Document Transfer can receive more parameters.
      String doc_class = mgr.getQueryStringValue("DOC_CLASS");
      if (Str.isEmpty(doc_class))
         doc_class = mgr.getQueryStringValue("SUB_DOC_CLASS");
      if (Str.isEmpty(doc_class))
         doc_class = mgr.getQueryStringValue("ITEM4_DOC_CLASS");
      
      String doc_no = mgr.getQueryStringValue("DOC_NO");
      if (Str.isEmpty(doc_no))
         doc_no = mgr.getQueryStringValue("SUB_DOC_NO");
      if (Str.isEmpty(doc_no))
         doc_no = mgr.getQueryStringValue("ITEM4_DOC_NO");
      
      String doc_sheet = mgr.getQueryStringValue("DOC_SHEET");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = mgr.getQueryStringValue("FIRST_SHEET_NO");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = mgr.getQueryStringValue("SUB_DOC_SHEET");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = mgr.getQueryStringValue("ITEM4_DOC_SHEET");
      
      String doc_rev = mgr.getQueryStringValue("DOC_REV");
      if (Str.isEmpty(doc_rev))
         doc_rev = mgr.getQueryStringValue("DOC_REVISION");
      if (Str.isEmpty(doc_rev))
         doc_rev = mgr.getQueryStringValue("FIRST_REVISION");
      if (Str.isEmpty(doc_rev))
         doc_rev = mgr.getQueryStringValue("SUB_DOC_REV");
      if (Str.isEmpty(doc_rev))
         doc_rev = mgr.getQueryStringValue("ITEM4_DOC_REV");
      
      if (!Str.isEmpty(doc_class) && !Str.isEmpty(doc_no) && !Str.isEmpty(doc_sheet) && !Str.isEmpty(doc_rev))
         return true;
      else
         return false;
      // Modified end
   }


   public void trace()
   {
      main_buf.traceBuffer("TRANSFERRED_DATA");
   }


   public void setDocumentProperty(String property, String value)
   {
      ASPBuffer buf = getCurrentDocument();

      if (buf.itemExists(property))
         buf.setValue(property, value);
      else
         buf.addItem(property, value);
   }


   public void setDocumentPropertyAt(int doc_pos, String property, String value)
   {
      ASPBuffer buf = doc_buf.getBufferAt(doc_pos);

      if (buf.itemExists(property))
         buf.setValue(property, value);
      else
         buf.addItem(property, value);
   }


   private ASPBuffer getTransferedData()
   {
      if (bufferedTransfer())
      {
         debug("buffered data transfer..");
         return (ASPBuffer)mgr.unpack(mgr.getQueryStringValue(TRANSFER_PARAM_NAME));
      }
      else
      {
         debug("query param transfer..");
         ASPBuffer buf    = mgr.newASPBuffer();
         ASPBuffer header = buf.addBuffer("ACTION");

         // buffer header
         header.addItem("FILE_ACTION", mgr.getQueryStringValue("PROCESS_DB"));
         header.addItem("DOC_TYPE", mgr.getQueryStringValue("DOC_TYPE"));
         // Added by Terry 20131002
         // Check launch file parameter
         header.addItem("LAUNCH_FILE", mgr.getQueryStringValue("LAUNCH_FILE"));
         // Check SAME_ACTION_TO_ALL parameter
         header.addItem("SAME_ACTION_TO_ALL", mgr.getQueryStringValue("SAME_ACTION_TO_ALL"));
         // Added end
         
         // Added by Terry 20140730
         // Check DOC_FOLDER parameter
         header.addItem("DOC_FOLDER", mgr.getQueryStringValue("DOC_FOLDER"));
         // Added end
         
         // body (documents)
         ASPBuffer doc_buf = buf.addBuffer("DOCUMENTS");
         ASPBuffer data    = doc_buf.addBuffer("DATA");

         // Modified by Terry 20130828
         // Document transfer can receive more parameters.
         String doc_class = "";
         String doc_no = "";
         String doc_sheet = "";
         String doc_rev = "";
         if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
            doc_class = mgr.getQueryStringValue("DOC_CLASS");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_CLASS")))
            doc_class = mgr.getQueryStringValue("SUB_DOC_CLASS");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("ITEM4_DOC_CLASS")))
            doc_class = mgr.getQueryStringValue("ITEM4_DOC_CLASS");
         
         if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
            doc_no = mgr.getQueryStringValue("DOC_NO");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_NO")))
            doc_no = mgr.getQueryStringValue("SUB_DOC_NO");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("ITEM4_DOC_NO")))
            doc_no = mgr.getQueryStringValue("ITEM4_DOC_NO");
         
         if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET")))
            doc_sheet = mgr.getQueryStringValue("DOC_SHEET");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_SHEET")))
            doc_sheet = mgr.getQueryStringValue("SUB_DOC_SHEET");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("ITEM4_DOC_SHEET")))
            doc_sheet = mgr.getQueryStringValue("ITEM4_DOC_SHEET");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("FIRST_SHEET_NO")))
            doc_sheet = mgr.getQueryStringValue("FIRST_SHEET_NO");
         
         if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV")))
            doc_rev = mgr.getQueryStringValue("DOC_REV");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SUB_DOC_REV")))
            doc_rev = mgr.getQueryStringValue("SUB_DOC_REV");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("ITEM4_DOC_REV")))
            doc_rev = mgr.getQueryStringValue("ITEM4_DOC_REV");
         else if (!mgr.isEmpty(mgr.getQueryStringValue("FIRST_REVISION")))
            doc_rev = mgr.getQueryStringValue("FIRST_REVISION");
         // Modified end
         
         data.addItem("DOC_CLASS",     doc_class);
         data.addItem("DOC_NO",        doc_no);
         data.addItem("DOC_SHEET",     doc_sheet);
         data.addItem("DOC_REV",       doc_rev);
         // Added by Terry 20131002
         // Check file no parameter
         if (!mgr.isEmpty(mgr.getQueryStringValue("FILE_NO")))
            data.addItem("FILE_NO", mgr.getQueryStringValue("FILE_NO"));
         // Added end
         data.addItem("RELEASED",      mgr.getQueryStringValue("RELEASED"));
         data.addItem("LATEST_REV",    mgr.getQueryStringValue("LATEST_REV"));
         data.addItem("FILE_TYPE",     mgr.getQueryStringValue("FILE_TYPE"));
         return buf;
      }
   }


   public void transferDataTo(String page)
   {
      mgr.transferDataTo(page, main_buf);
   }


   /**
    * Modifies the named property in the ACTION buffer or, if it does not exist,
    * appends a new property-value pair
    */
   public void setActionProperty(String property, String value)
   {
      if (act_buf.itemExists(property))
         act_buf.setValue(property, value);
      else
         act_buf.addItem(property, value);
   }


   /**
    * Returns the named item in the ACTION buffer
    */
   public String getActionProperty(String property)
   {
      return act_buf.getValue(property);
   }


   /**
    * Returns the named item in the DOCUMENTS buffer
    */
   public String getDocumentProperty(String property)
   {
      return getValue(property);
   }


   /**
    * Returns the current/active document as a buffer
    */
   public ASPBuffer getCurrentDocument()
   {
      return doc_buf.getBufferAt(current_doc);
   }


   // MDAHSE, 2003-01-29
   /**
    * Returns current document position
    */
   public int getCurrentDocumentCount()
   {
      return current_doc;
   }


   /**
    * Sets the current/active documents
    */
   private void goToDocument(int doc_no)
   {
      current_doc = doc_no;
   }


   /**
    * Saves data in the current request to the context buffer for future use
    */
   public void saveRequest()
   {
      setActionProperty("CURRENT_DOCUMENT", Integer.toString(current_doc));
      ctx.writeBuffer("TRANSFERRED_DOCUMENTS", main_buf);
   }


   /**
    * Returns a count of the documents transferred
    */
   public int countDocuments()
   {
      return doc_buf.countItems();
   }


   /**
    * Advances the active document in the documents buffer by one.
    */
   public void nextDocument()
   {
      current_doc++;
   }


   /**
    * Sets the first document in the documents buffer as the active document
    */
   public void firstDocument()
   {
      goToDocument(0);
   }


   /**
    * Returns true if the current/active document is the first document in
    * the request
    */
   public boolean isFirstDocument()
   {
      return getCurrentDocumentCount() == 0;
   }


   /**
    * Returns true if the current/active document is the last document in
    * the request
    */
   public boolean isLastDocument()
   {
      return getCurrentDocumentCount() == (countDocuments() - 1);
   }


   /**
    * Returns the document class of the current document
    */
   public String getDocClass()
   {
      // MDAHSE, 2003-02-10
      //      return getCurrentDocument().getValue("DOC_CLASS");
      // Modified by Terry 20131014
      // Add select name
      // Original:
      // return getValue("DOC_CLASS");
      String doc_class = getValue("DOC_CLASS");
      if (Str.isEmpty(doc_class))
         doc_class = getValue("SUB_DOC_CLASS");
      if (Str.isEmpty(doc_class))
         doc_class = getValue("ITEM4_DOC_CLASS");
      return doc_class;
      // Modified end
   }


   /**
    * Returns the document number of the current document
    */
   public String getDocNo()
   {
      // MDAHSE, 2003-02-10
      //      return getCurrentDocument().getValue("DOC_NO");
      // Modified by Terry 20131014
      // Add select name
      // Original:
      // return getValue("DOC_NO");
      String doc_no = getValue("DOC_NO");
      if (Str.isEmpty(doc_no))
         doc_no = getValue("SUB_DOC_NO");
      if (Str.isEmpty(doc_no))
         doc_no = getValue("ITEM4_DOC_NO");
      return doc_no;
      // Modified end
   }


   /**
    * Returns the document sheet of the current document
    */
   public String getDocSheet()
   {
      // MDAHSE, 2003-02-10
      //      return getCurrentDocument().getValue("DOC_SHEET");
      // Modified by Terry 20131014
      // Add select name
      // Original:
      // return getValue("DOC_SHEET");
      String doc_sheet = getValue("DOC_SHEET");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = getValue("FIRST_SHEET_NO");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = getValue("SUB_DOC_SHEET");
      if (Str.isEmpty(doc_sheet))
         doc_sheet = getValue("ITEM4_DOC_SHEET");
      return doc_sheet;
      // Modified end
   }


   /**
    * Returns the document revision of the current document
    */
   public String getDocRev()
   {
      // MDAHSE, 2003-02-10
      // return getCurrentDocument().getValue("DOC_REV");
      // Modified by Terry 20131014
      // Add select name
      // Original:
      // return mgr.isEmpty(getValue("DOC_REV"))? getValue("DOC_REVISION"): getValue("DOC_REV");
      String doc_rev = getValue("DOC_REV");
      if (Str.isEmpty(doc_rev))
         doc_rev = getValue("DOC_REVISION");
      if (Str.isEmpty(doc_rev))
         doc_rev = getValue("FIRST_REVISION");
      if (Str.isEmpty(doc_rev))
         doc_rev = getValue("SUB_DOC_REV");
      if (Str.isEmpty(doc_rev))
         doc_rev = getValue("ITEM4_DOC_REV");
      return doc_rev;
      // Modified end
   }


   /**
    * Returns the file type of the current document
    */
   public String getFileType()
   {
      return getValue("FILE_TYPE");
   }



   /**
    * Returns the document type of the current file action
    */
   public String getDocType()
   {
      return getActionProperty("DOC_TYPE");
   }


   /**
    *  Sets the file_type of the current document in documents buffer
    */
   public void setFileType(String file_type)
   {
      getCurrentDocument().setValue("FILE_TYPE", file_type);
   }


   /**
    *  Prints out debug messages
    */
   private void debug(String debugMessage)
   {
      if (DEBUG)
      {
         log.debug(getClass().getName() + ": " + debugMessage);
      }
   }


   /**
    * Reads parameters from current request (if any) and adds/updates them in
    * the ACTION buffer
    */
   private void setFileActionPropertyFromRequest(String property, String parameter)
   {
      String value = mgr.readValue(parameter);

      if (!mgr.isEmpty(value))
      {
         setActionProperty(property, value);
      }
   }


   private String getValue(String val_name)
   {

      ASPBuffer data_buf = getCurrentDocument();
      int item_position;

      // Determine the position of the name item..
      if (mgr.isEmpty(data_buf.getNameAt(0)))
         item_position = doc_buf.getBufferAt(0).getItemPosition(val_name);
      else
         item_position = data_buf.getItemPosition(val_name);


      if ((item_position < 0) || (item_position > data_buf.countItems() - 1))
      {
         // Item not found, return null
         return null;
      }


      // Fetch value using item position
      return getCurrentDocument().getValueAt(item_position);
   }

   //Bug Id 72460, start
   /**
   * Demoted the active document in the documents buffer by one.
   */
   public void previousDocument()
   {
      if (!isFirstDocument()) 
      {
	  current_doc--;
      }
       
   }

   public void lastDocument()
   {
      goToDocument(countDocuments()-1);
   }
   //Bug Id 72460, end

}
