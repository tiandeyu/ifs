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
*  File         : QuickReport.java 
*  Modified     :
*    Suneth M   2001-Aug-21 - Created Using the ASP file QuickReport.asp
*    Rifki R    2002-Apr-08 - Fixed translation problems caused by trailing spaces.
*    Chanaka A  2003-Mar-06 - Modified page so that the QuickReportQuery.page will be
*                             opened in a new browser window       
*    Ramila H   2004-04-15  - Bug id 44094, removed "Number" mask from QUICK_REPORT_ID
*    Prabha R   2006-02-21  - Modified query to extract 'SQL' type only
* ----------------------------------------------------------------------------
* 2010-09-15  VOHELK - Bug 93000, Introduced new view, inorder to filter out new QR types 
* Revision 1.1  2006/09/18 04:13:00  chaalk
*
* Bug 58703, Reinstate Crystal web integration
* ----------------------------------------------------------------------------
*/

package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class QuickReport extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.QuickReport");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPQuery q;

   //===============================================================
   // Construction 
   //===============================================================
   public QuickReport(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      cmd   = null;
      data   = null;
      q   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      QuickReport page = (QuickReport)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.cmd   = null;
      page.data   = null;
      page.q   = null;

      // Cloning immutable attributes
      page.frm = page.getASPForm();
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.cmdbar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      frm   = mgr.getASPForm();
      ctx   = mgr.getASPContext();
      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();

      // This if clause determines what the ASP page does with data it is fed
      // when called. It figures out how and why it was called and does
      // suitable things for each case.

      if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( mgr.dataTransfered() )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("QUICK_REPORT_ID")) )
         okFind();
   }

//=============================================================================
// Command Bar Search Group functions
//=============================================================================

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      lay.setCountValue(toInt(rowset.getRow().getValue("N")));
      rowset.clear();
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(blk);
      //q.addWhereCondition("QR_TYPE_DB = 'SQL'"); 
      q.setOrderByClause("QUICK_REPORT_ID");
      q.includeMeta("ALL");
   
      if( mgr.dataTransfered() )
         q.addOrCondition( mgr.getTransferedData() );
   
      mgr.querySubmit(trans,blk);
   
      if (  rowset.countRows() == 0 ) 
      {
         mgr.showAlert("FNDPAGESQUICKREPORTNODATA: No data found.");
         rowset.clear();
      }
   }

//=============================================================================
// Definition
//=============================================================================

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");
   
      f = blk.addField( "OBJID" );
      f.setHidden();
   
      f = blk.addField( "OBJVERSION" );
      f.setHidden();
   
      //Bug id 44094, start
      f = blk.addField("QUICK_REPORT_ID");
      //Bug id 44094, end
      f.setHidden();
   
      f = blk.addField("DESCRIPTION");
      f.setMandatory();
      f.setLabel("FNDPAGESQUICKREPORTDESCRIPTION: Description");
      f.setSize(30);
      f.setHyperlink("QuickReportQuery.page", "QUICK_REPORT_ID", "NEWWIN");
   
      f = blk.addField("CATEGORY_DESCRIPTION");
      f.setDynamicLOV("REPORT_CATEGORY_LOV");
      f.setLabel("FNDPAGESQUICKREPORTCATEGORY_DESCRIPTION: Category");
      f.setSize(30);
   
      f = blk.addField("COMMENTS");
      f.setLabel("FNDPAGESQUICKREPORTCOMMENTS: Comments");
      f.setSize(30);
      f.setHeight(4);
   
      blk.setView("QUICK_REPORT_NON_BI");
      blk.defineCommand("QUICK_REPORT_API","New__,Modify__,Remove__");
   
      rowset = blk.getASPRowSet();
   
      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableCommand(cmdbar.DUPLICATEROW);
      cmdbar.disableCommand(cmdbar.NEWROW);
      cmdbar.disableCommand(cmdbar.DELETE);
      cmdbar.disableCommand(cmdbar.EDITROW);

      tbl = mgr.newASPTable(blk);
      tbl.setTitle("FNDPAGESQUICKREPORTQUICKREPORTS: Quick Report");
   
      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);
   }

//===============================================================
//  HTML
//===============================================================
   
   protected String getDescription()
   {
      return "FNDPAGESQUICKREPORTTITLE: Quick Report Overview";
   }

   protected String getTitle()
   {
      return "FNDPAGESQUICKREPORTTEXTQROVERVIEWTITLE: Quick Report Overview";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(lay.show());
   }

}
