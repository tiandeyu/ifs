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
*  File             : DocumentText.page
*  Description      : 
* ----------------------------------------------------------------------------
*  Modified     :
*       
*     2003-05-19 - Mushlk  - Created.
*     2003-05-22 - Mushlk  - Modified.
*     2003-09-16 - RuRalk  - Call ID - 100808 Set the Output type field read only in the edit mode.
*     2006-09-12 - AmPalk  - Bug 59368, Modified the translated constant of title description according to the standard by applying the patch.   
*     2006-09-22 - AmPalk  - Bug 58216 Correcting SQL errors by applying the patch.
* ----------------------------------------------------------------------------                     
*/


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentText extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.DocumentText");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPBlock document_blk;
    private ASPBlockLayout document_lay;
    private ASPRowSet document_set;
    private ASPCommandBar document_bar;
    private ASPTable document_tbl;

    private ASPBlock document_text_blk;
    private ASPBlockLayout document_text_lay;
    private ASPRowSet document_text_set;
    private ASPCommandBar document_text_bar;
    private ASPTable document_text_tbl;

    private ASPContext ctx;

    //===============================================================
    // Transient temporary variables 
    //===============================================================
    private String sNoteId = "";
    private String sTitle = "";

    //===============================================================
    // Construction 
    //===============================================================
    public DocumentText(ASPManager mgr, String page_path)
    { 
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();
        ctx = mgr.getASPContext();
        sNoteId  = ctx.readValue("NOTE_ID"); 
        sTitle  = ctx.readValue("TITLE"); 
        
        if ( mgr.commandBarActivated() )
           eval(mgr.commandBarFunction());
        else if ( !mgr.isEmpty(mgr.getQueryStringValue("NOTE_ID")) )
        {
           sNoteId = mgr.getQueryStringValue("NOTE_ID");
           sTitle = mgr.getQueryStringValue("TITLE");
           okFindITEM();
        }
        else if (mgr.dataTransfered()) 
           okFindITEM();
        
        adjust();
        ctx.writeValue("NOTE_ID", sNoteId);
        ctx.writeValue("TITLE", sTitle);
    }

    public void okFindITEM()
    {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      trans.clear();

      q = trans.addEmptyQuery(document_text_blk);
      q.addWhereCondition("NOTE_ID = ? AND OUTPUT_TYPE != 'PURCHASE'");
      q.addParameter("NOTE_ID", sNoteId);
      q.includeMeta("ALL");
      
      mgr.submit(trans);
    }

    public void newRowITEM()
    {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      
      ASPCommand cmd = trans.addEmptyCommand("ITEM","DOCUMENT_TEXT_API.NEW__",document_text_blk);
      cmd.setOption("ACTION","PREPARE");
      
      trans = mgr.perform(trans);
      
      ASPBuffer data = trans.getBuffer("ITEM/DATA");
      data.setFieldItem("ITEM_NOTE_ID",sNoteId);
      
      document_text_set.addRow(data);
    }

//-----------------------------------------------------------------------------
//-----------------------  PreDefine  -----------------------------------------
//-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      disableNavigate();
      disableHomeIcon();

      document_blk = mgr.newASPBlock("HEAD");

      document_blk.addField("NOTE_ID").
      setHidden();
      
      document_blk.setView("DUAL");
      document_set = document_blk.getASPRowSet();

      document_tbl = mgr.newASPTable(document_blk);
      document_tbl.setTitle(mgr.translate("DOCUMENTTITLE1: Dummy Header"));
      document_tbl.enableTitleNoWrap();

      document_bar = mgr.newASPCommandBar(document_blk);
      document_lay = document_blk.getASPBlockLayout();
      document_lay.setDefaultLayoutMode(document_lay.SINGLE_LAYOUT);
      
      //-Detail
      document_text_blk = mgr.newASPBlock("ITEM");

      document_text_blk.addField("ITEM_OBJID").
      setDbName("OBJID").
      setHidden();

      document_text_blk.addField("ITEM_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      document_text_blk.addField("ITEM_NOTE_ID").
      setDbName("NOTE_ID").
      setHidden();
            
      document_text_blk.addField("OUTPUT_TYPE").
      setLabel("DOCUMENTTEXTOUTPUTTYPE: Output Type").
      setReadOnly().
      setInsertable().
      setDynamicLOV("OUTPUT_TYPE_LOV");
      

      document_text_blk.addField("NOTE_TEXT").
      setLabel("DOCUMENTTEXTNOTE: Note").
      setLOV("PhraseTextLov.page").
      setSize(50);

      document_text_blk.setView("DOCUMENT_TEXT");
      document_text_blk.setMasterBlock(document_blk);
      document_text_blk.defineCommand("DOCUMENT_TEXT_API","New__,Modify__,Remove__,");

      document_text_set = document_text_blk.getASPRowSet();
      
      document_text_tbl = mgr.newASPTable(document_text_blk);
      document_text_tbl.setTitle(mgr.translate("DOCUMENTTEXTTITLE1: Document Texts"));
      document_text_tbl.enableTitleNoWrap();

      document_text_bar = mgr.newASPCommandBar(document_text_blk);
      document_text_bar.defineCommand(document_text_bar.OKFIND, "okFindITEM");
      document_text_bar.defineCommand(document_text_bar.NEWROW,"newRowITEM");

      document_text_lay = document_text_blk.getASPBlockLayout();
      document_text_lay.setDefaultLayoutMode(document_text_lay.MULTIROW_LAYOUT);
      
   }
    
    public void adjust()
    {
    }

    protected String getDescription()
    {
       return "DOCUMENTTEXTTITLEDESC: Document Texts" ;
    }

    protected String getTitle()
    {
       ASPManager mgr = getASPManager();   
       return mgr.translate("DOCUMENTTEXTTITLE: Document Texts for ")+sTitle;
    }


//===============================================================
//  HTML
//===============================================================

    protected void printContents() throws FndException
    {
       appendToHTML(document_text_lay.show());
    }
}
