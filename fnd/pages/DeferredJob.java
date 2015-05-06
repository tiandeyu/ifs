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
*  File        : DeferredJob.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-01  - Created Using the ASP file DeferredJob.asp
*    Chaminda O.    2001-03-01  - Modifications after converting to Java 
*    Suneth M       2003-09-04  - Changed execute(),preDefine(). Added reExecute().
*    Mangala        2003-10-10  - Project Call 107338 - Detail Part not populate. 
*    Suneth M       2007-04-16  - Merged the corrections for Bug 64089, Changed preDefine().
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DeferredJob extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.DeferredJob");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private int currrow;
   private ASPCommand b;
   private String search_url;  
      
   
   //===============================================================
   // Construction 
   //===============================================================
   public DeferredJob(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      q   = null;
      currrow   = 0;
      b   = null;
      search_url   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DeferredJob page = (DeferredJob)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.q   = null;
      page.currrow   = 0;
      page.b   = null;
      page.search_url   = null;
      
      // Cloning immutable attributes
      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay = page.itemblk0.getASPBlockLayout();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
   
      search_url = null;
      
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         search();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      search_url = mgr.createSearchURL(headblk);
   
      trans.clear();
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
   
      if(mgr.dataTransfered())
        q.addOrCondition(mgr.getTransferedData());
      
      q.setOrderByClause("JOB_ID DESC");
      mgr.querySubmit(trans,headblk);
   
      if (  headset.countRows() == 0 ) 
      {
         mgr.showAlert("FNDPAGESDEFERREDJOBNODATA: No data found.");
         headset.clear();
      }
      eval(headset.syncItemSets());
   }


   public void  search()
   {
      ASPManager mgr = getASPManager();

      search_url = mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
   
      if(mgr.dataTransfered())
        q.addOrCondition(mgr.getTransferedData());
   
      q.setOrderByClause("JOB_ID DESC");
      mgr.submit(trans);
   
      if ( headset.countRows() == 0 ) 
      {
         mgr.setStatusLine("FNDPAGESDEFERREDJOBNODATAFOUND: No data found.");
      }

   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      itemset0.clear();
      q = trans.addEmptyQuery(itemblk0);
      q.addWhereCondition("JOB_ID = ?");
      q.addParameter("JOB_ID",headset.getValue("JOB_ID"));
   
      q.includeMeta("ALL");
      currrow = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk0);
   
      headset.goTo(currrow);

   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");
   
      headblk.addField("OBJID").
         setHidden();
   
      headblk.addField("OBJVERSION").
         setHidden();
   
      headblk.addField("STATE_DB").
         setHidden();

      headblk.addField("JOB_ID","Number","######").
         setSize(10).
         setLabel("FNDPAGESDEFERREDJOBJOB_ID: Job Id");
   
      headblk.addField("DESCRIPTION").
         setSize(30).
         setLabel("FNDPAGESDEFERREDJOBDESCRIPTION: Description");
   
      headblk.addField("USERNAME").
         setSize(20).
         setLabel("FNDPAGESDEFERREDJOBUSERNAME: Username");
   
      headblk.addField("STATE").
         setSize(20).
         setLabel("FNDPAGESDEFERREDJOBSTATE: State");

      headblk.addField("PROCEDURE_NAME").
         setSize(30).
         setLabel("FNDPAGESDEFERREDJOBPROCEDURE_NAME: Procedure Name");
      
      headblk.addField("POSTED","Datetime").
         setSize(20).
         setLabel("FNDPAGESDEFERREDJOBPOSTED: Posted");
   
      headblk.addField("EXECUTED","Datetime").
         setSize(20).
         setLabel("FNDPAGESDEFERREDJOBEXECUTED: Executed");
   
      headblk.addField("PROGRESS_INFO").
         setSize(20).
         setHeight(2).
         setLabel("FNDPAGESDEFERREDJOBPROGRESS_INFO: Progress Info");
   
      headblk.addField("ERROR_TEXT").
         setSize(60).
         setLabel("FNDPAGESDEFERREDJOBERROR_TEXT: Error Text");

      headblk.addField("ARGUMENTS_STRING").
         setSize(30).
         setLabel("FNDPAGESDEFERREDJOBARGUMENTS: Arguments");
      
      headblk.addField("QUEUE_ID", "Number","######").
         setSize(20).
         setHidden().
         setLabel("FNDPAGESDEFERREDJOBQUEUE_ID: Queue Id");
   
      headblk.addField("BATCH_QUEUE_DESCRIPTION").
         setSize(20).
         setLabel("FNDPAGESDEFERREDJOBBATCH_QUEUE_DESCRIPTION: Batch Queue Description").
         setFunction("BATCH_QUEUE_API.GET_DESCRIPTION (:QUEUE_ID)");
   
      headblk.addField("BATCH_QUEUE_EXECUTION_PLAN").
         setSize(20).
         setHidden().
         setLabel("FNDPAGESDEFERREDJOBBATCH_QUEUE_EXECUTION_PLAN: Batch Queue Execution Plan").
         setFunction("BATCH_QUEUE_API.GET_EXECUTION_PLAN (:QUEUE_ID)");

      headblk.setView("DEFERRED_JOB");
   
      headset = headblk.getASPRowSet();
   
      headbar = mgr.newASPCommandBar(headblk);
   
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
   
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("FNDPAGESDEFERREDJOBDEFERRED_JOB: Background Jobs");
   
      headbar.addCustomCommand("execute", mgr.translate("FNDPAGESDEFERREDJOBEXECUTE: Execute"));
      headbar.addCustomCommand("reExecute", mgr.translate("FNDPAGESDEFERREDJOBREEXECUTE: Re-execute"));
      headbar.addCommandValidConditions("execute","STATE_DB","Enable","Posted");
      headbar.addCommandValidConditions("reExecute","STATE_DB","Enable","Error");
   
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
   
      itemblk0 = mgr.newASPBlock("ITEM0");
      itemblk0.setMasterBlock(headblk);
   
      itemblk0.addField("ITEM0_OBJID").
         setHidden().
         setDbName("OBJID");
   
      itemblk0.addField("ITEM0_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");
   
      itemblk0.addField("ITEM0_JOB_ID","Number","######").
         setSize(20).
         setHidden().
         setLabel("FNDPAGESDEFERREDJOBITEM0_JOB_ID: Job Id").
         setDbName("JOB_ID");
   
      itemblk0.addField("TEXT").
         setSize(90).
         setLabel("FNDPAGESDEFERREDJOBTEXT: Status Information");

      itemblk0.setView("DEFERRED_JOB_STATUS");
      itemblk0.defineCommand("DEFERRED_JOB_STATUS_API","New__,Modify__,Remove__");
      itemset0 = itemblk0.getASPRowSet();
   
      itembar0 = mgr.newASPCommandBar(itemblk0);
   
      itemtbl0 = mgr.newASPTable(itemblk0);
   
      itembar0.disableCommand(itembar0.EDITROW);
      itembar0.disableCommand(itembar0.FIND);
      itembar0.disableCommand(itembar0.NEWROW);
   
      itemlay = itemblk0.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
   }

   public void  execute()
   {
      ASPManager mgr = getASPManager();
      
      if (headlay.isMultirowLayout()) 
          headset.goTo(headset.getRowSelected());

      String job_id = headset.getValue("JOB_ID");

      ASPCommand  cmdbuf = mgr.newASPCommand();
      cmdbuf = trans.addCustomCommand("EXEC", "DEFERRED_JOB_API.Execute_Job");      
      cmdbuf.addParameter("JOB_ID",job_id);
      mgr.perform(trans);
      headset.refreshRow();
   }

   public void  reExecute()
   {
       execute();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESDEFERREDJOBTITLE: Background Jobs";
   }

   protected String getTitle()
   {
      return "FNDPAGESDEFERREDJOBTITLE: Background Jobs";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if(headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }
      if(itemlay.isVisible())
      {
        appendToHTML(itemlay.show());
      }
   }

}
