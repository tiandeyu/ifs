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
*  File        : DocumentTextDlg.java 
*  Modified    :
*    ARWILK  2001-03-05  - Created.
*    JEWILK  2003-07-17  - Scream Merge.
*    NaLrlk  2006-08-15  - Bug 58216 Correcting SQL errors.
*    AMDILK  2007-05-23  - Call Id 144685: Modified okFind()
*    NiDalk  2007-07-10  - Performed XSS corrections.
*    NaLrlk  2007-09-12  - Removed unwanted variables, added function closeDlg(), Modified function saveReturn().
* ----------------------------------------------------------------------------
*/


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentTextDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.DocumentTextDlg");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext       ctx;
   private ASPBlock         headblk;
   private ASPRowSet        headset;
   private ASPCommandBar    headbar;
   private ASPTable         headtbl;
   private ASPBlockLayout   headlay;
   private ASPField         f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private int     nNoteId;
   private String  frmName;

   //===============================================================
   // Construction 
   //===============================================================
   public DocumentTextDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();      
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      nNoteId  = 0;

      nNoteId           = ctx.readNumber("CTXNOTE",nNoteId);
      frmName           = ctx.readValue("FRMNAME",frmName);

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if ( mgr.buttonPressed("CLOSEDOCTXT") )
         closeDlg();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTE_ID")))
      {
         nNoteId  = toInt(mgr.readValue("NOTE_ID"));
         frmName  = mgr.readValue("FRM_NAME");
         okFind();
      }

      ctx.writeNumber("CTXNOTE",nNoteId);
      ctx.writeValue("FRMNAME",frmName);
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  -------------------------- -
//-----------------------------------------------------------------------------

   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPCommand cmd = trans.addEmptyCommand("HEAD","DOCUMENT_TEXT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      data.setValue("NOTE_ID", Integer.toString(nNoteId));
      headset.addRow(data);
   }

   public void  saveReturn()
   {
      ASPManager mgr = getASPManager();
      int currrow;
      headset.changeRow();
      currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(currrow);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  clear()
   {
      headset.clear();
      headtbl.clearQueryRow();
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = trans.addQuery(headblk);
      q.addWhereCondition("NOTE_ID = ? AND OUTPUT_TYPE != 'PURCHASE'");
      q.addParameter("NOTE_ID", String.valueOf(nNoteId));
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void  closeDlg() throws FndException
   {
      ASPManager mgr = getASPManager();

      //orderw, partcw module pages
      if ("SalesChargeType".equals(frmName) || "SalesChargeGroup".equals(frmName) || "PartCatalog".equals(frmName)) 
      {
         appendDirtyJavaScript("window.opener.refreshLangData();\n");
         appendDirtyJavaScript("self.close();\n");
      }
      else if ("PurchaseReqLineAllOvw".equals(frmName))
      {
         appendDirtyJavaScript("window.opener.refreshPRL();\n");
         appendDirtyJavaScript("self.close();\n");
      }
      else
      {
         appendDirtyJavaScript("self.close();\n");
      }
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(headblk);
      q.addWhereCondition("NOTE_ID = ? AND OUTPUT_TYPE != 'PURCHASE'");
      q.addParameter("NOTE_ID", String.valueOf(nNoteId));
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("MPCCOWDOCUMENTTEXTDLGNODATA: No data found."));
         headset.clear();
      }
      if (headset.countRows() == 1)
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("NOTE_ID", "Number");
      f.setSize(10);
      f.setHidden();

      f = headblk.addField("OUTPUT_TYPE");
      f.setSize(15);
      f.setLabel("MPCCOWDOCUMENTTEXTDLGOUTPUTTYPE: Output Type");
      f.setUpperCase();
      f.setDynamicLOV("OUTPUT_TYPE_LOV");
      f.setMandatory();

      f = headblk.addField("NOTE_TEXT");
      f.setSize(30);
      f.setLabel("MPCCOWDOCUMENTTEXTDLGNOTETEXT: Note");
      f.setLOV("NoteLov.page",600,450);
      f.setMandatory();

      headblk.setView("DOCUMENT_TEXT");
      headblk.defineCommand("DOCUMENT_TEXT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DUPLICATE);

      headtbl = mgr.newASPTable(headblk);
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields()");
      headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields()");

      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(2);   
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "MPCCOWDOCUMENTTEXTDLGTITLE: Document Text";
   }

   protected String getTitle()
   {
      return "MPCCOWDOCUMENTTEXTDLGTITLE: Document Text";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
         appendToHTML(headlay.show());
      //Close Button
      printSpaces(3);
      printSubmitButton("CLOSEDOCTXT", mgr.translate("MPCCOWDOCUMENTTEXTDLGCLOSEBUT:  Close  "), "");
   }

}



