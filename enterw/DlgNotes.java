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
*  File         : DlgNotes.java 
*  Modified     : 
*  2005-04-19   NaLslk   FIPR360, Created.
*  2005-05-03   NaLslk   FIPR360, Modified DlgNotes page to act as a popup window.
*  2005-05-25   NaLslk   FIPR360, Added support for invoice notes.
*  2005-06-13   Iswalk   FIHT330, Applied new notes concept to budget process,node, template and transaction
*  2005-09-07   Jakalk   Code Cleanup, Removed doReset and clone methods.
*  2006-01-04   Iswalk   FIHT330, Extended notes for paynment ledger items.
*  2006-01-09   Iswalk   FIHT330, Extended notes for paynment transactions.
*  2006-12-27   Maselk   Merged LCS Bug 58216, Fixed SQL Injection threats.
*  2006-01-09   Maaylk   FIPL638A, Extended notes for FA Object.
*  2009-02-17   Jakalk   Bug 78024, Disabled 'Documents' option from menu.
*  2009-04-03   Mohrlk   Bug 81117, Modified editRow() to fix single row layout edit problem.
*                        Also Modified the saveReturnNotes() to select the modified note after save and return.
* ----------------------------------------------------------------------------
*/

package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DlgNotes extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.DlgNotes");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm          frm;
   private ASPHTMLFormatter fmt;
   private ASPContext       ctx;
   private ASPBlock         noteblk;
   private ASPRowSet        noteset;
   private ASPCommandBar    notebar;
   private ASPTable         notetbl;
   private ASPBlockLayout   notelay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private String     key_attr;
   private String     note_id;
   private ASPBuffer  data;
   private ASPBuffer  trans_data;
   private ASPQuery   q;   
   private String     title;
   private String     pkg_name;
   private String     notes_exist;
   private String     company;
      
   //===============================================================
   // Construction 
   //===============================================================
   public DlgNotes(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
   
      frm   = mgr.getASPForm();
      fmt   = mgr.newASPHTMLFormatter();
      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      key_attr     = ctx.readValue("KEYATTR", "");
      note_id      = ctx.readValue("NOTEID", "");
      notes_exist  = ctx.readValue("NOTEEXIST", "FALSE");
      pkg_name     = ctx.readValue("PKGNAME", "");
      company      = ctx.readValue("COMPANY", "");
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ( mgr.commandLinkActivated() )
         eval(mgr.commandLinkFunction());
      else if (!mgr.isEmpty(mgr.readValue("COMPANY")))
         okFindOnTransfer();
      
      adjust();

      ctx.writeValue("KEYATTR", key_attr);
      ctx.writeValue("NOTEID", note_id);
      ctx.writeValue("NOTEEXIST", notes_exist);
      ctx.writeValue("PKGNAME", pkg_name);
      ctx.writeValue("COMPANY", company);
   }
      
   
//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void editRow()
   {
      noteset.storeSelections();
      // Bug 81117, Begin, Added condition
      if(notelay.isMultirowLayout())
          noteset.goTo(noteset.getRowSelected());
      else
          noteset.goTo(noteset.getCurrentRowNo());
      // Bug 81117, End
      notebar.disableCommand(notebar.FORWARD);
      notelay.setLayoutMode(notelay.EDIT_LAYOUT);
   }
   
   public void okFindNotes()
   {
      ASPManager mgr = getASPManager();
      
      q = trans.addEmptyQuery(noteblk);
      q.addWhereCondition("NOTE_ID  = ?");
      q.addParameter("NOTE_ID", note_id);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,noteblk);
      trans.clear();
      if (noteset.countRows() > 0)
         notes_exist = "TRUE";
      else
         notes_exist = "FALSE";        
   }
   
   public void okFindOnTransfer()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer  row = null; 
      String     seriesId;
      String     paymentId;
      String     identity;
      String     party_type_db;
      String     invoiceId;
      String     budgetProcessId;
      String     budgetStructureId;
      String     budgetNodeId;
      String     recordType;
      String     budgetTemplateId;
      String     transactionId;
      String     transId;
      String     itemCategory;
      String     postingCombinationId;
      String     currencyCode;
      String     periodNumber;
      String     ledgerItemSeriesId;
      String     ledgerItemId;
      String     ledgerItemVersion;
      String     party_type;
      String     objectId;

      pkg_name             = mgr.readValue("PKG_NAME");
      company              = mgr.readValue("COMPANY");
      seriesId             = mgr.readValue("SERIES_ID");
      paymentId            = mgr.readValue("PAYMENT_ID");
      identity             = mgr.readValue("IDENTITY");
      party_type_db        = mgr.readValue("PARTY_TYPE_DB");
      invoiceId            = mgr.readValue("INVOICE_ID");
      budgetProcessId      = mgr.readValue("BUDGET_PROCESS_ID");
      budgetStructureId    = mgr.readValue("BUDGET_STRUCTURE_ID");
      budgetNodeId         = mgr.readValue("BUDGET_NODE_ID");
      recordType           = mgr.readValue("RECORD_TYPE");
      budgetTemplateId     = mgr.readValue("BUDGET_TEMPLATE_ID");
      transactionId        = mgr.readValue("TRANSACTION_ID");
      transId              = mgr.readValue("TRANS_ID");
      itemCategory         = mgr.readValue("ITEM_CATEGORY");
      postingCombinationId = mgr.readValue("POSTING_COMBINATION_ID");
      currencyCode         = mgr.readValue("CURRENCY_CODE");
      periodNumber         = mgr.readValue("PERIOD_NUMBER");
      ledgerItemSeriesId   = mgr.readValue("LEDGER_ITEM_SERIES_ID");
      ledgerItemId         = mgr.readValue("LEDGER_ITEM_ID");
      ledgerItemVersion    = mgr.readValue("LEDGER_ITEM_VERSION");
      party_type           = mgr.readValue("PARTY_TYPE");
      objectId             = mgr.readValue("OBJECT_ID");
      
      key_attr   = "";
      
      if (!mgr.isEmpty(company))
      {
         key_attr = "COMPANY" + (char)31 + company + (char)30;
      }

      if (!mgr.isEmpty(invoiceId))
      {
         key_attr = key_attr + "INVOICE_ID" + (char)31 + invoiceId + (char)30;
      }
      else
      {
         if (!mgr.isEmpty(seriesId))
         {
            key_attr = key_attr + "SERIES_ID" + (char)31 + seriesId + (char)30;         
         }

         if (!mgr.isEmpty(paymentId))
         {
            key_attr = key_attr + "PAYMENT_ID" + (char)31 + paymentId + (char)30;   
         }

         if (!mgr.isEmpty(transId))
         {
            key_attr = key_attr + "TRANS_ID" + (char)31 + transId + (char)30;   
         }

         if (!mgr.isEmpty(identity))
         {
            key_attr = key_attr + "IDENTITY" + (char)31 + identity + (char)30;
         }

         if (!mgr.isEmpty(party_type_db))
         {
            key_attr = key_attr + "PARTY_TYPE_DB" + (char)31 + party_type_db + (char)30;
         }

         if (!mgr.isEmpty(party_type))
         {
            key_attr = key_attr + "PARTY_TYPE" + (char)31 + party_type + (char)30;
         }

         if (!mgr.isEmpty(ledgerItemSeriesId))
         {
            key_attr = key_attr + "LEDGER_ITEM_SERIES_ID" + (char)31 + ledgerItemSeriesId + (char)30;
         }

         if (!mgr.isEmpty(ledgerItemId))
         {
            key_attr = key_attr + "LEDGER_ITEM_ID" + (char)31 + ledgerItemId + (char)30;
         }

         if (!mgr.isEmpty(ledgerItemVersion))
         {
            key_attr = key_attr + "LEDGER_ITEM_VERSION" + (char)31 + ledgerItemVersion + (char)30;
         }
      }

      if (!mgr.isEmpty(budgetProcessId))
      {
         key_attr = key_attr + "BUDGET_PROCESS_ID" + (char)31 + budgetProcessId + (char)30;
      }

      if (!mgr.isEmpty(budgetStructureId))
      {
         key_attr = key_attr + "BUDGET_STRUCTURE_ID" + (char)31 + budgetStructureId + (char)30;
      }

      if (!mgr.isEmpty(budgetNodeId))
      {
         key_attr = key_attr + "BUDGET_NODE_ID" + (char)31 + budgetNodeId + (char)30;
      }

      if (!mgr.isEmpty(recordType))
      {
         key_attr = key_attr + "RECORD_TYPE" + (char)31 + recordType + (char)30;
      }

      if (!mgr.isEmpty(budgetTemplateId))
      {
         key_attr = key_attr + "BUDGET_TEMPLATE_ID" + (char)31 + budgetTemplateId + (char)30;
      }

      if (!mgr.isEmpty(transactionId))
      {
         key_attr = key_attr + "TRANSACTION_ID" + (char)31 + transactionId + (char)30;
      }

      if (!mgr.isEmpty(itemCategory))
      {
         key_attr = key_attr + "ITEM_CATEGORY" + (char)31 + itemCategory + (char)30;
      }

      if (!mgr.isEmpty(postingCombinationId))
      {
         key_attr = key_attr + "POSTING_COMBINATION_ID" + (char)31 + postingCombinationId + (char)30;
      }

      if (!mgr.isEmpty(currencyCode))
      {
         key_attr = key_attr + "CURRENCY_CODE" + (char)31 + currencyCode + (char)30;
      }

      if (!mgr.isEmpty(periodNumber))
      {
         key_attr = key_attr + "PERIOD_NUMBER" + (char)31 + periodNumber + (char)30;
      }

      if (!mgr.isEmpty(objectId))
      {
         key_attr = key_attr + "OBJECT_ID" + (char)31 + objectId + (char)30;
      }

      note_id  = getNotesId();
            
      trans.clear();
      q = trans.addEmptyQuery(noteblk);
      q.addWhereCondition("NOTE_ID = ?");
      q.addParameter("NOTE_ID", note_id);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,noteblk);
      trans.clear();

      if (noteset.countRows() > 0)
         notes_exist = "TRUE";
      else
         notes_exist = "FALSE";
   }
   
   public void newNotes()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer  data;
                    
      cmd = trans.addEmptyCommand("NOTE","Fin_Note_Text_API.New__",noteblk);
      cmd.setOption("ACTION","PREPARE");      
      if (mgr.isEmpty(note_id))
      {
         cmd = trans.addCustomFunction("NOTEID", "Fin_Note_API.Get_New_Note_Id", "NOTE_ID");
      }
      trans = mgr.perform(trans);            
      data = trans.getBuffer("NOTE/DATA");      
      if (mgr.isEmpty(note_id))
         note_id = trans.getValue("NOTEID/DATA/NOTE_ID");      
      data.setValue("NOTE_ID", note_id);            
      noteset.addRow(data);
      eval(noteblk.generateAssignments());      
      trans.clear();         
   }

   public void deleteNotes()
   {
      ASPManager mgr  = getASPManager();
      int selRowCount;
      int rowCount    = noteset.countRows();
      
      selectRows();
      selRowCount = noteset.countSelectedRows();
      noteset.setSelectedRowsRemoved();
      
      if (selRowCount == rowCount)
      {
         trans.generateBlockCommands(noteblk);
         deleteNoteReferences();
      }
      mgr.submit(trans);
      trans.clear();        
      okFindNotes();
   }

   public void selectRows()
    {
      if(notelay.isMultirowLayout())
         noteset.storeSelections();
      else
         noteset.selectRow();
      noteset.setFilterOn();
    }
   
   public void  deleteNoteReferences()
   {
      deleteFinReference();
      deleteModuleReference();
   }
   
   private void deleteModuleReference()
   {
      cmd = trans.addCustomCommand("REMPAYREF", pkg_name + ".Remove_Note");
      cmd.addParameter("NOTE_ID", note_id);
      cmd.addParameter("DUMMY", key_attr);
   }
   
   private void deleteFinReference()
   {
      cmd = trans.addCustomCommand("REMFINREF", "Fin_Note_API.Remove_Note");
      cmd.addParameter("NOTE_ID", note_id);
   }
   
   public void saveReturnNotes()
   {   
      // Bug 81117, Begin
      int current_row = noteset.getCurrentRowNo();
      // Bug 81117, End
      saveNotes();
      okFindNotes();
      // Bug 81117, Begin
      noteset.goTo(current_row);
      // Bug 81117, End
   }

   public void saveNewNotes()
   {
      saveNotes();
      notes_exist = "TRUE";      
      newNotes();  
   }
      
   private void saveNotes()
   {
      ASPManager mgr  = getASPManager();
      ASPBuffer  temp = null;
      
      noteset.changeRow();     
      
      //If notes are entered for the first time, create note references.
      if ("FALSE".equals(notes_exist))
         createNoteReferences();
      mgr.submit(trans);
      trans.clear();
   }
   
   private void createNoteReferences()
   {
      createFinReference();
      createModuleReference();
   }

   private void createFinReference()
   {
      cmd = trans.addCustomCommand("NEWFINNOTE","Fin_Note_API.Create_Note");
      cmd.addParameter("NOTE_ID", note_id);
   }
   
   private void createModuleReference()
   {
      cmd = trans.addCustomCommand("NEWPAYNOTE",pkg_name + ".Create_Note");
      cmd.addParameter("NOTE_ID", note_id);
      cmd.addParameter("DUMMY", key_attr);
   }   
   
   private String getNotesId()
   {
      ASPManager mgr = getASPManager();
      String sNoteId = null;
      
      trans.clear();
      cmd = trans.addCustomFunction("NOTEID", pkg_name + ".Get_Note_Id", "NOTE_ID");
      cmd.addParameter("DUMMY", key_attr);
      trans = mgr.perform(trans);         
      sNoteId = trans.getValue("NOTEID/DATA/NOTE_ID");
      sNoteId = mgr.isEmpty(sNoteId) ? "" : sNoteId;
      trans.clear();
      return sNoteId;
   }   
    

   public void preDefine()
   {
      ASPManager mgr = getASPManager();   
   
      noteblk = mgr.newASPBlock("NOTE");

      noteblk.addField("NOTE_OBJID").
            setDbName("OBJID").
            setHidden();

      noteblk.addField("NOTE_OBJVERSION").
            setDbName("OBJVERSION").
            setHidden();

      noteblk.addField("NOTE_ID", "Number").
            setHidden();
      
      noteblk.addField("ROW_NO", "Number").
            setHidden();

      noteblk.addField("TIMESTAMP", "Datetime").
            setLabel("ENTERWDLGNOTESTIMESTAMP: Time Stamp").
            setReadOnly().
            setMandatory().
            setSize(22);

      noteblk.addField("NOTE_TEXT").
            setDbName("TEXT").
            setLabel("ENTERWDLGNOTESNOTETEXT: Notes").
            setMandatory().
            setSize(80).
            setMaxLength(4000);

      noteblk.addField("USER_ID").
            setLabel("ENTERWDLGNOTESUSERID: User").
            setReadOnly().
            setSize(10);
      
      noteblk.addField("DUMMY").
            setFunction("''").
            setHidden();
      
      noteblk.addField("IS_FND_USER").
            setFunction("DECODE(USER_ID, Fnd_Session_API.Get_Fnd_User, 'TRUE', 'FALSE')").
            setHidden();

      noteblk.setView("FIN_NOTE_TEXT");
      noteblk.defineCommand("FIN_NOTE_TEXT_API","New__,Modify__,Remove__");
      // Bug 78024, Begin
      noteblk.disableDocMan();
      // Bug 78024, End      
      
      notebar = mgr.newASPCommandBar(noteblk);
      notebar.defineCommand(notebar.NEWROW,"newNotes");
      notebar.defineCommand(notebar.SAVENEW, "saveNewNotes");
      notebar.defineCommand(notebar.SAVERETURN, "saveReturnNotes");
      notebar.defineCommand(notebar.REMOVE,"deleteNotes");
      notebar.defineCommand(notebar.EDIT, "editRow");
      notebar.disableCommand(notebar.FIND);
      notebar.disableCommand(notebar.DELETE);
      notebar.disableCommand(notebar.DUPLICATE);
      notebar.disableCommand(notebar.DUPLICATEROW);
      notebar.disableCommand(notebar.EDITROW);
      notebar.addCustomCommand("deleteNotes", mgr.translate("FINDLGNOTESDELNOTES: Delete Notes"));
      notebar.addCustomCommand("editRow", mgr.translate("FINDLGNOTESEDITNOTES: Edit Note..."));
      notebar.addCommandValidConditions("deleteNotes", "IS_FND_USER", "Enable", "TRUE");
      notebar.addCommandValidConditions("editRow", "IS_FND_USER","Enable","TRUE");
      notebar.removeFromMultirowAction("editRow");
      notebar.enableMultirowAction();
      
      notelay = noteblk.getASPBlockLayout();
      notelay.setDefaultLayoutMode(notelay.MULTIROW_LAYOUT);
      
      noteset = noteblk.getASPRowSet();
      
      notetbl = mgr.newASPTable(noteblk);
      notetbl.enableRowSelect();
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();      
      String     tempTitle = mgr.translate("ENTERWDLGNOTESTITLE: Notes");
      
      if (pkg_name.toUpperCase().indexOf("PAYMENT") != -1 )
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEPAY: Payment Notes");
      }
      else if (pkg_name.toUpperCase().indexOf("INVOICE") != -1)
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEINV: Invoice Notes");
      }
      else if (pkg_name.toUpperCase().indexOf("BUDGET_PROCESS") != -1)
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEBUDPRO: Budget Process Notes");
      }
      else if (pkg_name.toUpperCase().indexOf("BUDGET_NODE") != -1)
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEBUDPRON: Budget Node Notes");
      }
      else if (pkg_name.toUpperCase().indexOf("BUDGET_TEMPLATE_ROW") != -1)
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEBUDPROTR: Budget Transaction Notes");
      }
      else if (pkg_name.toUpperCase().indexOf("BUDGET_TEMPLATE") != -1)
      {
         tempTitle = mgr.translate("ENTERWDLGNOTESTITLEBUDPROT: Budget Template Notes");
      }

      title = company + " - " + tempTitle;
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return title;
   }

   protected String getTitle()
   {
      return title;
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();      
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(title));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(title));
      out.append(notelay.show());
      
      if ( notelay.isMultirowLayout() )
      {
         out.append("  <table> \n");
         out.append("  <tr> \n");
         out.append("<font size=\"1\" face=Verdana>");
         out.append("<a href=\"javascript:closeNotes()\">" + mgr.translate("DLGNOTESLINKOK: Ok") + "</a>");
         out.append("</font>\n");
         out.append("</tr> \n");
         out.append("</table> \n");
      }
      appendDirtyJavaScript("function closeNotes() \n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("   this.close(); \n");
      appendDirtyJavaScript("   opener.focus(); \n");
      appendDirtyJavaScript("   this.close(); \n");
      //this addtional close() is necessary for proper functionality of modal dialog in non IE browsers
      appendDirtyJavaScript("   opener.setNoteExist(" + (noteset.countRows() > 0) + ");\n");
      appendDirtyJavaScript("}\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");      
      return out;
   }
}