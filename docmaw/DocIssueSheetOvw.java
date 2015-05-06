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
*  File        : DocIssueSheetOvw.java
*  Modified    :
*
*
*  2004-03-24   DIKALK   SP1 Merge. Bug ID 40572 - Added functionality to handle Doc Sheet in the page.
*                        Bug ID 42046 - Changed No to Doc No, Sheet to Doc Sheet and Rev to Doc Rev in the
*                        page headding.
*  2006-07-18  BAKALK    Bug ID 58216, Fixed Sql Injection.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocIssueSheetOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocIssueSheetOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String sDocClass;
   private String sDocNo;
   private String sDocSheet;
   private String sDocRev;
   private String sTitle;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;

   //===============================================================
   // Construction
   //===============================================================
   public DocIssueSheetOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      sDocClass   = null;
      sDocNo   = null;
      sDocSheet = null;
      sDocRev   = null;
      sTitle   = null;
      q   = null;
      cmd   = null;
      data   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocIssueSheetOvw page = (DocIssueSheetOvw)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.sDocClass   = null;
      page.sDocNo   = null;
      page.sDocSheet = null;
      page.sDocRev   = null;
      page.sTitle   = null;
      page.q   = null;
      page.cmd   = null;
      page.data   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();


      sDocClass = ctx.readValue("DOCCLASS", "");
      sDocNo = ctx.readValue("DOCNO", "");
      sDocSheet = ctx.readValue("DOCSHEET","");
      sDocRev = ctx.readValue("DOCREV", "");

      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if((!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))))
         transfer();

      ctx.writeValue("DOCCLASS", sDocClass);
      ctx.writeValue("DOCNO", sDocNo);
      ctx.writeValue("DOCSHEET",sDocSheet);
      ctx.writeValue("DOCREV", sDocRev);

      sTitle  = mgr.translate("DOCMAWDOCISSUESHEETOVWTITLE: Document Sheets")+" - "+mgr.translate("DOCMAWDOCISSUESHEETOVWTDOCCLASS: Doc Class")+": "+sDocClass+"  ";
      sTitle += mgr.translate("DOCMAWDOCISSUESHEETOVWDOCSHEET: Doc Sheet") + ": " + sDocSheet + " ";
      sTitle += mgr.translate("DOCMAWDOCISSUESHEETOVWTDOCNO: No")+": "+sDocNo+"  ";
      sTitle += mgr.translate("DOCMAWDOCISSUESHEETOVWTDOCREV: Rev")+": "+sDocRev+"  ";

   }

//=============================================================================
//   VALIDATE FUNCTION
//=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//=============================================================================
//   CMDBAR FUNCTIONS
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ? and DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", sDocClass);
      q.addParameter("DOC_NO",    sDocNo);
      q.addParameter("DOC_SHEET", sDocSheet);
      q.addParameter("DOC_REV",   sDocRev);
      //bug 58216 end
      q.setOrderByClause("LINE_NO, REV_DATE, SHEET_NO");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUESHEETOVWNODATA: No data found."));
         headbar.disableMultirowAction();
         headset.clear();
      }
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ? and DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?");
      q.addParameter("DOC_CLASS", sDocClass);
      q.addParameter("DOC_NO",    sDocNo);
      q.addParameter("DOC_SHEET", sDocSheet);
      q.addParameter("DOC_REV",   sDocRev);
      //bug 58216 end
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","DOC_ISSUE_SHEET_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("DOC_CLASS", sDocClass);
      cmd.setParameter("DOC_NO", sDocNo);
      cmd.setParameter("DOC_SHEET",sDocSheet);
      cmd.setParameter("DOC_REV", sDocRev);
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void transfer()
   {
      ASPManager mgr = getASPManager();

      sDocClass = mgr.readValue("DOC_CLASS");
      sDocNo = mgr.readValue("DOC_NO");
      sDocSheet = mgr.readValue("DOC_SHEET");
      sDocRev = mgr.readValue("DOC_REV");

      trans.clear();
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if ( mgr.dataTransfered() )
         q.addOrCondition( mgr.getTransferedData() );
      else 
      {
	  //Bug ID 40572, inoslk, Added Doc Sheet in the WHERE condition
	  //Bug ID 45944, inoslk, start
	  q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ? AND DOC_REV = ?") ;
	  q.addParameter("DOC_CLASS",sDocClass);
	  q.addParameter("DOC_NO",sDocNo);
	  q.addParameter("DOC_SHEET",sDocSheet);
	  q.addParameter("DOC_REV",sDocRev);
	  //Bug ID 45944, inoslk, end
      }

      q.setOrderByClause("LINE_NO, REV_DATE,SHEET_NO");

      mgr.submit(trans);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCISSUESHEETOVWNODATA: No data found."));
         headset.clear();
      }
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
              setHidden();

      headblk.addField("OBJVERSION").
              setMandatory().
              setHidden();

      headblk.addField("DOC_CLASS").
              setMandatory().
              setHidden();

      headblk.addField("DOC_NO").
              setMandatory().
              setHidden();

      headblk.addField("DOC_SHEET").
              setMandatory().
              setHidden();

      headblk.addField("DOC_REV").
              setMandatory().
              setHidden();

      headblk.addField("LINE_NO", "Number").
              setSize(20).
              setMandatory().
              setReadOnly().
              setInsertable().
              setLabel("DOCMAWDOCISSUESHEETOVWLINENO: Line No.");

      headblk.addField("SHEET_NO").
              setSize(20).
              setMaxLength(10).
              setLabel("DOCMAWDOCISSUESHEETOVWSHEETNO: Sheet No.");

      headblk.addField("SHEET_REV").
              setSize(20).
              setMandatory().
              setMaxLength(6).
              setLabel("DOCMAWDOCISSUESHEETOVWSHEETREV: Revision");

      headblk.addField("REV_DATE", "Date").
              setSize(20).
              setMandatory().
              setLabel("DOCMAWDOCISSUESHEETOVWREVDATE: Revision Date");

      headblk.addField("SHEET_REV_TEXT").
              setSize(20).
              setMaxLength(255).
              setLabel("DOCMAWDOCISSUESHEETOVWREVNOTE: Revision Note");

      headblk.setView("DOC_ISSUE_SHEET");
      headblk.defineCommand("DOC_ISSUE_SHEET_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      headtbl = mgr.newASPTable(headblk);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return sTitle;
   }

   protected String getTitle()
   {
      return "DOCMAWDOCISSUESHEETOVWTITLE: Document Sheets";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCISSUESHEETOVWTITLE: Document Sheets"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(sTitle));
      out.append(headlay.show());
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
