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
*  File        : DocumentReport.java
*  2005-01-21   bakalk   Created.
*  2006-02-09   dikalk   Made quite a few changes to this page.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentReport extends ASPPageProvider
{

   //
   // Static constants
   //
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentReport");


   //
   // Page elements for logs..
   //
   private ASPBlock logblk;
   private ASPRowSet logset;
   private ASPCommandBar logbar;
   private ASPBlockLayout loglay;
   private ASPTable logtbl;
   private String title;
   
   

   public DocumentReport(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run()
   {
       ASPManager mgr = getASPManager();
       ASPContext ctx = mgr.getASPContext();

       title = ctx.readValue("REPORT_TITLE", mgr.translate("DOCMAWDOCUMENTREPORTDEFAULTREPORTTITLE: Report"));


       // Get transferred data and populate block..
       if (mgr.dataTransfered())
          getTransferredData();
       
       
       ctx.writeValue("REPORT_TITLE", title);
   }


   private void getTransferredData()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buf = mgr.getTransferedData();

      buf.traceBuffer("debug: ######## LOG BUG REPORT DATA ############");


      // initialise the report title
      title = buf.getBufferAt(0).getValue("REPORT_TITLE");


      // Load transfered data to logset..
      for (int x = 1; x < buf.countItems(); x++)
      {
         logset.addRow(buf.getBufferAt(x));
      }
   }


   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      disableOptions();
      disableHomeIcon();
      disableNavigate();
      disableHelp();
      disableHeader();
      disableFooter();

      logblk = mgr.newASPBlock("LOG");

      logblk.addField("DOC_CLASS").
             setLabel("DOCMAWDOCUMENTREPORTDOCCLASS: Doc Class");

      logblk.addField("DOC_NO").
             setLabel("DOCMAWDOCUMENTREPORTDOCNO: Doc No");

      logblk.addField("DOC_SHEET").
             setLabel("DOCMAWDOCUMENTREPORTDOCSHEET: Doc Sheet");

      logblk.addField("DOC_REV").
             setLabel("DOCMAWDOCUMENTREPORTDOCREV: Doc Rev");

      logblk.addField("TITLE").
             setLabel("DOCMAWDOCUMENTREPORTTITLE: Title");

      logblk.addField("STATUS").
             setLabel("DOCMAWDOCUMENTREPORTSTATUS: Status");

      logset = logblk.getASPRowSet();

      logtbl = mgr.newASPTable(logblk);
      logtbl.enableTitleNoWrap();
      logtbl.unsetSortable();
      logtbl.unsetStripedBackground();

      logbar = mgr.newASPCommandBar(logblk);

      loglay = logblk.getASPBlockLayout();
      loglay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
   }


   private String getReportTitle()
   {
      return title ;
   }


   protected String getTitle()
   {
      return "DOCMAWDOCUMENTREPORTREPORT: Report";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();

      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(getTitle()));
      out.append("</head>\n");
      out.append("<body " + mgr.generateBodyTag());
      out.append("><form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");

      beginDataPresentation();

      out.append("<br>");
      out.append("&nbsp;&nbsp;");
      out.append("<font class=\"pageTitle\">");
      out.append(getReportTitle());
      out.append("</font>");
      out.append("<br><br>");

      out.append(loglay.generateDataPresentation());

      // draw dotted line after table
      endDataPresentation(false);

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      return out;
   }
}
