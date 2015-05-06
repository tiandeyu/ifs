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
*  File        : DocLog.java 
*  AMNALK  2005-12-20  - Created: AMPR125 Events in Docman.
*  AMNALK  2006-01-30  - AMPR125 - 7 Events in Docman: Change printContent to getContents().
*  WYRALK  2007-02-21  - Merged 62137
*  BAKALK  2007-05-23  - Call Id 144581, Modified all translation tags of titles.
*  
* ----------------------------------------------------------------------------
*/

package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocLog extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocLog");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;

   private ASPTabContainer tabs;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPQuery q;
   private ASPCommand cmd;
   private ASPBuffer data;

   private String showGen;
   private String root_path;

   //===============================================================
   // Construction 
   //===============================================================
   public DocLog(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      trans     = null;
      val       = null;
      q         = null;
      cmd       = null;
      data      = null;
      showGen   = null;
      root_path = null;

      super.doReset();
   }
   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocLog page = (DocLog)(super.clone(obj));

      // Initializing mutable attributes
      page.trans     = null;
      page.val       = null;
      page.q         = null;
      page.cmd       = null;
      page.data      = null;
      page.showGen   = null;
      page.root_path = null;

      // Cloning immutable attributes
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();

      page.headblk  = page.getASPBlock(headblk.getName());
      page.headset  = page.headblk.getASPRowSet();
      //page.item1tbl = page.getASPTable(item1tbl.getName());
      page.headbar  = page.headblk.getASPCommandBar();
      page.headlay  = page.headblk.getASPBlockLayout();

      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemlay0 = page.itemblk0.getASPBlockLayout();

      page.tabs      = page.getASPTabContainer();
      page.f         = page.getASPField(f.getName());

      return page;
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();

      ctx   = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt   = mgr.newASPHTMLFormatter();

      root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      showGen = ctx.readValue("SHOWGEN", "0");

      if (mgr.commandBarActivated())
      {
         eval(mgr.commandBarFunction());      
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      
      tabs.saveActiveTab();
      
      ctx.writeValue("SHOWGEN", showGen);
      

   }


   //=============================================================================
   //  Validation
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

   }

   //=============================================================================
   // ITEM0
   //=============================================================================

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk0);

      if (itemset0.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICVARSET2NODATAFOUND: No data found"));
         itemset0.clear();
      }
      
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk0);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }

  

   //=============================================================================
   // Tab activate functions
   //=============================================================================

   public void  activateScale()
   {

      tabs.setActiveTab(1);
   }



   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");
      headblk.disableDocMan();

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.NEWROW);

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


      // tab commands
         headbar.addCustomCommand("activateLog", mgr.translate("DOCMAWDOCUMENTBASICLOG: Logs"));

         //---------------------------------------------------------------------
         //-------------- ITEM BLOCK - 0 ---------------------------------------
         //---------------------------------------------------------------------

         itemblk0 = mgr.newASPBlock("ITEM0");
         itemblk0.disableDocMan();

         itemblk0.addField("OBJID").
                  setHidden();

         itemblk0.addField("OBJVERSION").
                  setHidden();

         itemblk0.addField("DOC_CLASS").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setDynamicLOV("DOC_CLASS").
                  setLabel("DOCMAWDOCLOGDOCCLASS: Document Class");

         itemblk0.addField("DOC_NO").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO").
                  setDynamicLOV("DOC_TITLE","DOC_CLASS,DOC_NO").
                  setLabel("DOCMAWDOCLOGDOCNO: Document No");

         itemblk0.addField("DOC_SHEET").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setLabel("DOCMAWDOCLOGDOCSHEET: Document Sheet");

         itemblk0.addField("DOC_REV").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setLabel("DOCMAWDOCLOGDOCREV: Document Revision");
         
         itemblk0.addField("OPERATION").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setLabel("DOCMAWDOCLOGOPERATION: Operation");
                  
         itemblk0.addField("FND_USER").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setDynamicLOV("FND_USER").
                  setLabel("DOCMAWDOCLOGFNDUSER: Fnd User");

         itemblk0.addField("TIMESTAMP").
                  setSize(20).
                  setMaxLength(30).
                  setMandatory().
                  setLabel("DOCMAWDOCLOGTIMESTAMP: Timestamp");

         itemblk0.setView("EDM_FILE_OP_ANNOUNCE");
         
         itemset0 = itemblk0.getASPRowSet();

         itembar0 = mgr.newASPCommandBar(itemblk0);

         itembar0.enableCommand(itembar0.FIND);
         itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
         itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
         
         itemtbl0 = mgr.newASPTable(itemblk0);
         itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTBASICDOCLOG: Logs"));

         itemlay0 = itemblk0.getASPBlockLayout();
         itemlay0.setDialogColumns(1);
         itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

      //---------------------------------------------------------------------
      //-------------- DEFINITIONS OF TABS ----------------------------------
      //---------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICLOG: Logs"),"javascript:commandSet('HEAD.activateLog','')");
      
      tabs.setContainerWidth(700);   
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);

   }

   private void refreshHeadset()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      ASPQuery q = trans.addEmptyQuery(headblk);
      q.addOrCondition(headset.getRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"));
      q.setOrderByClause("DOC_CLASS, DOC_NO, SHEET_ORDER, REV_NO DESC");
      q.includeMeta("ALL");
      int row_no = headset.getCurrentRowNo();
      mgr.querySubmit(trans, headblk);
      headset.goTo(row_no);
      eval(headset.syncItemSets());
      
   }


   public String  tabsFinish()
   {
      ASPManager mgr = getASPManager();

      return tabs.showTabsFinish();
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTBASICDOCLOGTITLE: Basic Data - Logs";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTBASICDOCLOGTITLE: Basic Data - Logs";
   }

   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      AutoString presentation = new AutoString();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWMACROBASICDOCLOGTITLE: Basic Data - File Operation Log"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      
      out.append(mgr.startPresentation("DOCMAWMACROBASICDOCLOGTITLE2: File Operation Log"));
      
      out.append(tabs.showTabsInit());

      if  ( tabs.getActiveTab()==1 )
      {  
         if (itemlay0.isVisible())
         {
            out.append(itemlay0.show());
         }

      } 
     
      out.append(tabs.showTabsFinish()); 

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}







